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
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0105  */
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
            org.telegram.messenger.-$$Lambda$NotificationsController$InuBhwQAikzB2YQhvIMFASK0kGU r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$InuBhwQAikzB2YQhvIMFASK0kGU
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
            org.telegram.messenger.-$$Lambda$NotificationsController$6wLjKsushNgyWMV901nmzWOaP1g r1 = new org.telegram.messenger.-$$Lambda$NotificationsController$6wLjKsushNgyWMV901nmzWOaP1g
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
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0049, code lost:
        if ((r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L_0x004f;
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
            if (r1 == 0) goto L_0x00fc
            r11 = 0
        L_0x002f:
            int r12 = r21.size()
            if (r11 >= r12) goto L_0x00fc
            java.lang.Object r12 = r1.get(r11)
            org.telegram.tgnet.TLRPC$Message r12 = (org.telegram.tgnet.TLRPC$Message) r12
            if (r12 == 0) goto L_0x004f
            boolean r13 = r12.silent
            if (r13 == 0) goto L_0x004f
            org.telegram.tgnet.TLRPC$MessageAction r13 = r12.action
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r14 != 0) goto L_0x004b
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r13 == 0) goto L_0x004f
        L_0x004b:
            r15 = r5
            r12 = r11
            goto L_0x00f4
        L_0x004f:
            int r13 = r12.id
            long r13 = (long) r13
            org.telegram.tgnet.TLRPC$Peer r15 = r12.peer_id
            int r15 = r15.channel_id
            if (r15 == 0) goto L_0x005b
            long r8 = (long) r15
            long r8 = r8 << r7
            long r13 = r13 | r8
        L_0x005b:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r8 = r0.pushMessagesDict
            int r8 = r8.indexOfKey(r13)
            if (r8 < 0) goto L_0x0064
            goto L_0x004b
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
            org.telegram.messenger.-$$Lambda$NotificationsController$W3EyHOoU-PJy5kZh4eta-QmQIJE r2 = new org.telegram.messenger.-$$Lambda$NotificationsController$W3EyHOoU-PJy5kZh4eta-QmQIJE
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
            r11 = 2131625927(0x7f0e07c7, float:1.8879076E38)
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
            r1 = 2131624677(0x7f0e02e5, float:1.887654E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            java.lang.String r0 = r0.localName
            r2[r7] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00c5:
            r1 = 2131626224(0x7f0e08f0, float:1.8879678E38)
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
            r0 = 2131626201(0x7f0e08d9, float:1.8879632E38)
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
            r0 = 2131626182(0x7f0e08c6, float:1.8879593E38)
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
            r1 = 2131627917(0x7f0e0f8d, float:1.8883112E38)
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
            r2 = 2131626251(0x7f0e090b, float:1.8879733E38)
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
            r0 = 2131624573(0x7f0e027d, float:1.887633E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02b7:
            r0 = 2131624567(0x7f0e0277, float:1.8876317E38)
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
            r0 = 2131624627(0x7f0e02b3, float:1.887644E38)
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
            r0 = 2131626203(0x7f0e08db, float:1.8879636E38)
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
            r0 = 2131626188(0x7f0e08cc, float:1.8879605E38)
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
            r0 = 2131626187(0x7f0e08cb, float:1.8879603E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0359:
            r3 = 1
            r1 = 2131626186(0x7f0e08ca, float:1.8879601E38)
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
            r0 = 2131626186(0x7f0e08ca, float:1.8879601E38)
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
            r0 = 2131626190(0x7f0e08ce, float:1.887961E38)
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
            r0 = 2131626195(0x7f0e08d3, float:1.887962E38)
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
            r1 = 2131626194(0x7f0e08d2, float:1.8879617E38)
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
            r0 = 2131626194(0x7f0e08d2, float:1.8879617E38)
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
            r0 = 2131626204(0x7f0e08dc, float:1.8879638E38)
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
            r0 = 2131626183(0x7f0e08c7, float:1.8879595E38)
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
            r0 = 2131626197(0x7f0e08d5, float:1.8879623E38)
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
            r0 = 2131626198(0x7f0e08d6, float:1.8879625E38)
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
            r1 = 2131626196(0x7f0e08d4, float:1.8879621E38)
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
            r0 = 2131624123(0x7f0e00bb, float:1.8875417E38)
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
            r0 = 2131624123(0x7f0e00bb, float:1.8875417E38)
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
            r0 = 2131626151(0x7f0e08a7, float:1.887953E38)
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
            r0 = 2131626148(0x7f0e08a4, float:1.8879524E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05ca:
            boolean r4 = r0.isVideo()
            r10 = 2131626172(0x7f0e08bc, float:1.8879573E38)
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
            r0 = 2131626175(0x7f0e08bf, float:1.8879579E38)
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
            r0 = 2131626142(0x7f0e089e, float:1.8879512E38)
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
            r0 = 2131626178(0x7f0e08c2, float:1.8879585E38)
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
            r0 = 2131626163(0x7f0e08b3, float:1.8879554E38)
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
            r0 = 2131626127(0x7f0e088f, float:1.8879481E38)
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
            r0 = 2131626138(0x7f0e089a, float:1.8879504E38)
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
            r0 = 2131626124(0x7f0e088c, float:1.8879475E38)
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
            r1 = 2131626160(0x7f0e08b0, float:1.8879548E38)
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
            r1 = 2131626157(0x7f0e08ad, float:1.8879542E38)
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
            r0 = 2131626154(0x7f0e08aa, float:1.8879536E38)
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
            r0 = 2131626130(0x7f0e0892, float:1.8879488E38)
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
            r0 = 2131626151(0x7f0e08a7, float:1.887953E38)
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
            r0 = 2131626136(0x7f0e0898, float:1.88795E38)
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
            r1 = 2131626168(0x7f0e08b8, float:1.8879565E38)
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
            r0 = 2131626166(0x7f0e08b6, float:1.887956E38)
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
            r0 = 2131626152(0x7f0e08a8, float:1.8879532E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0882:
            boolean r10 = r0.isMusic()
            if (r10 == 0) goto L_0x0898
            r0 = 2131626149(0x7f0e08a5, float:1.8879526E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0898:
            boolean r4 = r0.isVideo()
            r10 = 2131626173(0x7f0e08bd, float:1.8879575E38)
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
            r0 = 2131626176(0x7f0e08c0, float:1.887958E38)
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
            r0 = 2131626143(0x7f0e089f, float:1.8879514E38)
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
            r0 = 2131626179(0x7f0e08c3, float:1.8879587E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0942:
            boolean r5 = r0.isRoundVideo()
            if (r5 == 0) goto L_0x0958
            r0 = 2131626164(0x7f0e08b4, float:1.8879556E38)
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
            r0 = 2131626128(0x7f0e0890, float:1.8879483E38)
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
            r0 = 2131626139(0x7f0e089b, float:1.8879506E38)
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
            r0 = 2131626125(0x7f0e088d, float:1.8879477E38)
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
            r1 = 2131626161(0x7f0e08b1, float:1.887955E38)
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
            r1 = 2131626158(0x7f0e08ae, float:1.8879544E38)
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
            r0 = 2131626155(0x7f0e08ab, float:1.8879538E38)
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
            r0 = 2131626131(0x7f0e0893, float:1.887949E38)
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
            r0 = 2131626152(0x7f0e08a8, float:1.8879532E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ac3:
            r2 = 1
            r0 = 2131626137(0x7f0e0899, float:1.8879502E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ad4:
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0aee
            r1 = 2131626169(0x7f0e08b9, float:1.8879567E38)
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
            r0 = 2131626167(0x7f0e08b7, float:1.8879563E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0aff:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0b11
            r0 = 2131626153(0x7f0e08a9, float:1.8879534E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b11:
            boolean r6 = r0.isMusic()
            if (r6 == 0) goto L_0x0b25
            r0 = 2131626150(0x7f0e08a6, float:1.8879528E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedMusicUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b25:
            boolean r4 = r0.isVideo()
            r6 = 2131626174(0x7f0e08be, float:1.8879577E38)
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
            r0 = 2131626177(0x7f0e08c1, float:1.8879583E38)
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
            r0 = 2131626144(0x7f0e08a0, float:1.8879516E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGifUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bb0:
            r4 = 1
            boolean r5 = r0.isVoice()
            if (r5 == 0) goto L_0x0bc5
            r0 = 2131626180(0x7f0e08c4, float:1.8879589E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bc5:
            boolean r5 = r0.isRoundVideo()
            if (r5 == 0) goto L_0x0bd9
            r0 = 2131626165(0x7f0e08b5, float:1.8879558E38)
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
            r0 = 2131626129(0x7f0e0891, float:1.8879485E38)
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
            r0 = 2131626140(0x7f0e089c, float:1.8879508E38)
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
            r0 = 2131626126(0x7f0e088e, float:1.887948E38)
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
            r1 = 2131626162(0x7f0e08b2, float:1.8879552E38)
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
            r1 = 2131626159(0x7f0e08af, float:1.8879546E38)
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
            r0 = 2131626156(0x7f0e08ac, float:1.887954E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0cda:
            r4 = 1
            boolean r1 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0ced
            r0 = 2131626135(0x7f0e0897, float:1.8879498E38)
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
            r0 = 2131626153(0x7f0e08a9, float:1.8879534E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d2e:
            r2 = 1
            r0 = 2131626141(0x7f0e089d, float:1.887951E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGeoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d3d:
            r2 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0d55
            r1 = 2131626170(0x7f0e08ba, float:1.8879569E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r11
            r3[r2] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x0d55:
            r0 = 2131626171(0x7f0e08bb, float:1.887957E38)
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
            r0 = 2131624727(0x7f0e0317, float:1.8876642E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d86:
            r1 = 1
            r0 = 2131624693(0x7f0e02f5, float:1.8876573E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d97:
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x0db1
            r0 = 2131626185(0x7f0e08c9, float:1.88796E38)
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
            r0 = 2131626184(0x7f0e08c8, float:1.8879597E38)
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
            r0 = 2131626181(0x7f0e08c5, float:1.887959E38)
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
            r0 = 2131625927(0x7f0e07c7, float:1.8879076E38)
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
            r0 = 2131624355(0x7f0e01a3, float:1.8875887E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e32:
            r0 = 2131624372(0x7f0e01b4, float:1.8875922E38)
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
            r0 = 2131624356(0x7f0e01a4, float:1.887589E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e76:
            r0 = 2131624378(0x7f0e01ba, float:1.8875934E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e80:
            boolean r2 = r19.isGame()
            if (r2 == 0) goto L_0x0e90
            r0 = 2131624358(0x7f0e01a6, float:1.8875893E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e90:
            boolean r2 = r19.isVoice()
            if (r2 == 0) goto L_0x0ea0
            r0 = 2131624352(0x7f0e01a0, float:1.8875881E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ea0:
            boolean r2 = r19.isRoundVideo()
            if (r2 == 0) goto L_0x0eb0
            r0 = 2131624374(0x7f0e01b6, float:1.8875926E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0eb0:
            boolean r2 = r19.isMusic()
            if (r2 == 0) goto L_0x0ec0
            r0 = 2131624371(0x7f0e01b3, float:1.887592E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ec0:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0ed2
            r0 = 2131624354(0x7f0e01a2, float:1.8875885E38)
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
            r0 = 2131626849(0x7f0e0b61, float:1.8880946E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ee8:
            r0 = 2131626742(0x7f0e0af6, float:1.8880729E38)
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
            r0 = 2131624364(0x7f0e01ac, float:1.8875906E38)
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
            r0 = 2131624359(0x7f0e01a7, float:1.8875895E38)
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
            r0 = 2131624357(0x7f0e01a5, float:1.8875891E38)
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
            r0 = 2131624375(0x7f0e01b7, float:1.8875928E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0f9d:
            r0 = 2131624375(0x7f0e01b7, float:1.8875928E38)
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
            r0 = 2131625927(0x7f0e07c7, float:1.8879076E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0fbe:
            r0 = 2131624368(0x7f0e01b0, float:1.8875914E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fc8:
            if (r21 == 0) goto L_0x0fcc
            r21[r7] = r7
        L_0x0fcc:
            r0 = 2131625927(0x7f0e07c7, float:1.8879076E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0fd4:
            r0 = 2131626201(0x7f0e08d9, float:1.8879632E38)
            java.lang.String r1 = "NotificationHiddenMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:72:0x013a A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x013b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r19, boolean r20, boolean[] r21, boolean[] r22) {
        /*
            r18 = this;
            r0 = r19
            int r1 = android.os.Build.VERSION.SDK_INT
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r2 != 0) goto L_0x16e8
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r2 == 0) goto L_0x0010
            goto L_0x16e8
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
            if (r22 == 0) goto L_0x0025
            r22[r7] = r6
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
            r11 = 2131626237(0x7f0e08fd, float:1.8879705E38)
            java.lang.String r12 = "NotificationMessageNoText"
            r13 = 2
            if (r10 == 0) goto L_0x00c4
            if (r5 != 0) goto L_0x006b
            if (r2 == 0) goto L_0x006b
            if (r9 == 0) goto L_0x005c
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 != 0) goto L_0x00bd
        L_0x005c:
            if (r22 == 0) goto L_0x0060
            r22[r7] = r7
        L_0x0060:
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r0 = r0.localName
            r1[r7] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r11, r1)
            return r0
        L_0x006b:
            if (r5 == 0) goto L_0x00bd
            if (r9 == 0) goto L_0x0087
            boolean r1 = r0.localChannel
            if (r1 != 0) goto L_0x007b
            java.lang.String r1 = "EnablePreviewGroup"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 == 0) goto L_0x0087
        L_0x007b:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00bd
            java.lang.String r1 = "EnablePreviewChannel"
            boolean r1 = r8.getBoolean(r1, r6)
            if (r1 != 0) goto L_0x00bd
        L_0x0087:
            if (r22 == 0) goto L_0x008b
            r22[r7] = r7
        L_0x008b:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00a9
            boolean r1 = r19.isSupergroup()
            if (r1 != 0) goto L_0x00a9
            r1 = 2131624677(0x7f0e02e5, float:1.887654E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            java.lang.String r0 = r0.localName
            r2[r7] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00a9:
            r1 = 2131626224(0x7f0e08f0, float:1.8879678E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            java.lang.String r3 = r0.localUserName
            r2[r7] = r3
            java.lang.String r0 = r0.localName
            r2[r6] = r0
            java.lang.String r0 = "NotificationMessageGroupNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00bd:
            r21[r7] = r6
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = (java.lang.String) r0
            return r0
        L_0x00c4:
            org.telegram.messenger.UserConfig r10 = r18.getUserConfig()
            int r10 = r10.getClientUserId()
            if (r2 != 0) goto L_0x00d6
            int r2 = r19.getFromChatId()
            if (r2 != 0) goto L_0x00dc
            int r2 = -r5
            goto L_0x00dc
        L_0x00d6:
            if (r2 != r10) goto L_0x00dc
            int r2 = r19.getFromChatId()
        L_0x00dc:
            r14 = 0
            int r16 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r16 != 0) goto L_0x00ea
            if (r5 == 0) goto L_0x00e7
            int r3 = -r5
            long r3 = (long) r3
            goto L_0x00ea
        L_0x00e7:
            if (r2 == 0) goto L_0x00ea
            long r3 = (long) r2
        L_0x00ea:
            r14 = 0
            if (r2 <= 0) goto L_0x0123
            org.telegram.tgnet.TLRPC$Message r15 = r0.messageOwner
            boolean r15 = r15.from_scheduled
            if (r15 == 0) goto L_0x010e
            r16 = r12
            long r11 = (long) r10
            int r17 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r17 != 0) goto L_0x0104
            r11 = 2131625945(0x7f0e07d9, float:1.8879112E38)
            java.lang.String r12 = "MessageScheduledReminderNotification"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            goto L_0x0138
        L_0x0104:
            r11 = 2131626245(0x7f0e0905, float:1.887972E38)
            java.lang.String r12 = "NotificationMessageScheduledName"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            goto L_0x0138
        L_0x010e:
            r16 = r12
            org.telegram.messenger.MessagesController r11 = r18.getMessagesController()
            java.lang.Integer r12 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r12)
            if (r11 == 0) goto L_0x0137
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r11)
            goto L_0x0138
        L_0x0123:
            r16 = r12
            org.telegram.messenger.MessagesController r11 = r18.getMessagesController()
            int r12 = -r2
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r11 = r11.getChat(r12)
            if (r11 == 0) goto L_0x0137
            java.lang.String r11 = r11.title
            goto L_0x0138
        L_0x0137:
            r11 = r14
        L_0x0138:
            if (r11 != 0) goto L_0x013b
            return r14
        L_0x013b:
            if (r5 == 0) goto L_0x014c
            org.telegram.messenger.MessagesController r12 = r18.getMessagesController()
            java.lang.Integer r15 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r12 = r12.getChat(r15)
            if (r12 != 0) goto L_0x014d
            return r14
        L_0x014c:
            r12 = r14
        L_0x014d:
            int r4 = (int) r3
            if (r4 != 0) goto L_0x015b
            r0 = 2131627849(0x7f0e0var_, float:1.8882974E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x16e7
        L_0x015b:
            java.lang.String r3 = "🎬 "
            java.lang.String r4 = "📎 "
            java.lang.String r15 = "📹 "
            java.lang.String r14 = "🖼 "
            java.lang.String r13 = "NotificationMessageText"
            r7 = 3
            if (r5 != 0) goto L_0x0553
            if (r2 == 0) goto L_0x0553
            if (r9 == 0) goto L_0x053f
            java.lang.String r2 = "EnablePreviewAll"
            boolean r2 = r8.getBoolean(r2, r6)
            if (r2 == 0) goto L_0x053f
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r5 == 0) goto L_0x024c
            org.telegram.tgnet.TLRPC$MessageAction r1 = r2.action
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r2 == 0) goto L_0x018c
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x16e7
        L_0x018c:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r2 != 0) goto L_0x023c
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r2 == 0) goto L_0x0196
            goto L_0x023c
        L_0x0196:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r2 == 0) goto L_0x01aa
            r0 = 2131626182(0x7f0e08c6, float:1.8879593E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x01aa:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r2 == 0) goto L_0x020d
            r1 = 2131627917(0x7f0e0f8d, float:1.8883112E38)
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
            r4 = 0
            r3[r4] = r2
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterDay
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            int r4 = r4.date
            long r4 = (long) r4
            long r4 = r4 * r8
            java.lang.String r2 = r2.format((long) r4)
            r3[r6] = r2
            java.lang.String r2 = "formatDateAtTime"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r2, r1, r3)
            r2 = 2131626251(0x7f0e090b, float:1.8879733E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r4 = r18.getUserConfig()
            org.telegram.tgnet.TLRPC$User r4 = r4.getCurrentUser()
            java.lang.String r4 = r4.first_name
            r5 = 0
            r3[r5] = r4
            r3[r6] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r4 = 2
            r3[r4] = r1
            java.lang.String r0 = r0.address
            r3[r7] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x16e7
        L_0x020d:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 != 0) goto L_0x0234
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r2 == 0) goto L_0x0216
            goto L_0x0234
        L_0x0216:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x16e5
            boolean r0 = r1.video
            if (r0 == 0) goto L_0x0229
            r0 = 2131624573(0x7f0e027d, float:1.887633E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x16e7
        L_0x0229:
            r0 = 2131624567(0x7f0e0277, float:1.8876317E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x16e7
        L_0x0234:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x16e7
        L_0x023c:
            r0 = 2131626181(0x7f0e08c5, float:1.887959E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x024c:
            boolean r2 = r19.isMediaEmpty()
            if (r2 == 0) goto L_0x0295
            if (r20 != 0) goto L_0x0285
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0275
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x0275:
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r6]
            r0[r2] = r11
            r5 = r16
            r1 = 2131626237(0x7f0e08fd, float:1.8879705E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r5, r1, r0)
            goto L_0x16e7
        L_0x0285:
            r5 = r16
            r1 = 2131626237(0x7f0e08fd, float:1.8879705E38)
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r6]
            r0[r2] = r11
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r5, r1, r0)
            goto L_0x16e7
        L_0x0295:
            r2 = r15
            r5 = r16
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r7.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x02fb
            if (r20 != 0) goto L_0x02d4
            r2 = 19
            if (r1 < r2) goto L_0x02d4
            java.lang.String r1 = r7.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02d4
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x02d4:
            r2 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x02ec
            r0 = 2131626242(0x7f0e0902, float:1.8879715E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessageSDPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x02ec:
            r0 = 2131626238(0x7f0e08fe, float:1.8879707E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessagePhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x02fb:
            boolean r7 = r19.isVideo()
            if (r7 == 0) goto L_0x035e
            if (r20 != 0) goto L_0x0337
            r3 = 19
            if (r1 < r3) goto L_0x0337
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0337
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r7 = 0
            r1[r7] = r11
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r2)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r7] = r6
            goto L_0x16e7
        L_0x0337:
            r7 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x034f
            r0 = 2131626243(0x7f0e0903, float:1.8879717E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r7] = r11
            java.lang.String r2 = "NotificationMessageSDVideo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x034f:
            r0 = 2131626249(0x7f0e0909, float:1.8879729E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r7] = r11
            java.lang.String r2 = "NotificationMessageVideo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x035e:
            r7 = 0
            boolean r2 = r19.isGame()
            if (r2 == 0) goto L_0x037f
            r1 = 2131626211(0x7f0e08e3, float:1.8879652E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r0 = r0.title
            r2[r6] = r0
            java.lang.String r0 = "NotificationMessageGame"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x16e7
        L_0x037f:
            boolean r2 = r19.isVoice()
            if (r2 == 0) goto L_0x0395
            r0 = 2131626206(0x7f0e08de, float:1.8879642E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessageAudio"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x0395:
            r2 = 0
            boolean r7 = r19.isRoundVideo()
            if (r7 == 0) goto L_0x03ab
            r0 = 2131626241(0x7f0e0901, float:1.8879713E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessageRound"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x03ab:
            boolean r7 = r19.isMusic()
            if (r7 == 0) goto L_0x03c0
            r0 = 2131626236(0x7f0e08fc, float:1.8879702E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessageMusic"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x03c0:
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x03e4
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7
            r0 = 2131626207(0x7f0e08df, float:1.8879644E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r11
            java.lang.String r2 = r7.first_name
            java.lang.String r3 = r7.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageContact2"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x03e4:
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x041a
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0404
            r1 = 2131626240(0x7f0e0900, float:1.887971E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            java.lang.String r0 = r0.question
            r2[r6] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0404:
            r2 = 2
            r3 = 0
            r1 = 2131626239(0x7f0e08ff, float:1.8879709E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r11
            java.lang.String r0 = r0.question
            r2[r6] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
        L_0x0417:
            r14 = r0
            goto L_0x16e7
        L_0x041a:
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r2 != 0) goto L_0x052f
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r2 == 0) goto L_0x0424
            goto L_0x052f
        L_0x0424:
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x0438
            r0 = 2131626234(0x7f0e08fa, float:1.8879698E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessageLiveLocation"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x0438:
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x0503
            boolean r2 = r19.isSticker()
            if (r2 != 0) goto L_0x04db
            boolean r2 = r19.isAnimatedSticker()
            if (r2 == 0) goto L_0x044a
            goto L_0x04db
        L_0x044a:
            boolean r2 = r19.isGif()
            if (r2 == 0) goto L_0x0496
            if (r20 != 0) goto L_0x0486
            r2 = 19
            if (r1 < r2) goto L_0x0486
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0486
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x0486:
            r2 = 0
            r0 = 2131626213(0x7f0e08e5, float:1.8879656E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessageGif"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x0496:
            r2 = 0
            if (r20 != 0) goto L_0x04cc
            r3 = 19
            if (r1 < r3) goto L_0x04cc
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04cc
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r11
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x04cc:
            r0 = 2131626208(0x7f0e08e0, float:1.8879646E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessageDocument"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x04db:
            r2 = 0
            java.lang.String r0 = r19.getStickerEmoji()
            if (r0 == 0) goto L_0x04f4
            r1 = 2131626247(0x7f0e0907, float:1.8879725E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r11
            r3[r6] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0417
        L_0x04f4:
            r0 = 2131626246(0x7f0e0906, float:1.8879723E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0503:
            r2 = 0
            if (r20 != 0) goto L_0x0522
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0522
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r11
            java.lang.CharSequence r0 = r0.messageText
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x0522:
            java.lang.Object[] r0 = new java.lang.Object[r6]
            r0[r2] = r11
            r1 = 2131626237(0x7f0e08fd, float:1.8879705E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r5, r1, r0)
            goto L_0x16e7
        L_0x052f:
            r2 = 0
            r0 = 2131626235(0x7f0e08fb, float:1.88797E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationMessageMap"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x053f:
            r5 = r16
            r2 = 0
            if (r22 == 0) goto L_0x0546
            r22[r2] = r2
        L_0x0546:
            java.lang.Object[] r0 = new java.lang.Object[r6]
            r0[r2] = r11
            r1 = 2131626237(0x7f0e08fd, float:1.8879705E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r5, r1, r0)
            goto L_0x16e7
        L_0x0553:
            if (r5 == 0) goto L_0x16e5
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r12)
            if (r5 == 0) goto L_0x0561
            boolean r5 = r12.megagroup
            if (r5 != 0) goto L_0x0561
            r5 = 1
            goto L_0x0562
        L_0x0561:
            r5 = 0
        L_0x0562:
            if (r9 == 0) goto L_0x16b5
            if (r5 != 0) goto L_0x056e
            java.lang.String r9 = "EnablePreviewGroup"
            boolean r9 = r8.getBoolean(r9, r6)
            if (r9 != 0) goto L_0x0578
        L_0x056e:
            if (r5 == 0) goto L_0x16b5
            java.lang.String r5 = "EnablePreviewChannel"
            boolean r5 = r8.getBoolean(r5, r6)
            if (r5 == 0) goto L_0x16b5
        L_0x0578:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r8 == 0) goto L_0x1114
            org.telegram.tgnet.TLRPC$MessageAction r8 = r5.action
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r9 == 0) goto L_0x0690
            int r1 = r8.user_id
            if (r1 != 0) goto L_0x05a1
            java.util.ArrayList<java.lang.Integer> r3 = r8.users
            int r3 = r3.size()
            if (r3 != r6) goto L_0x05a1
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            r3 = 0
            java.lang.Object r1 = r1.get(r3)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
        L_0x05a1:
            if (r1 == 0) goto L_0x0638
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x05c4
            boolean r0 = r12.megagroup
            if (r0 != 0) goto L_0x05c4
            r0 = 2131624627(0x7f0e02b3, float:1.887644E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x05c4:
            r3 = 2
            r4 = 0
            if (r1 != r10) goto L_0x05db
            r0 = 2131626203(0x7f0e08db, float:1.8879636E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x05db:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x05eb
            r1 = 0
            return r1
        L_0x05eb:
            int r1 = r0.id
            if (r2 != r1) goto L_0x061d
            boolean r0 = r12.megagroup
            if (r0 == 0) goto L_0x0608
            r0 = 2131626188(0x7f0e08cc, float:1.8879605E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0608:
            r1 = 2
            r2 = 0
            r0 = 2131626187(0x7f0e08cb, float:1.8879603E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x061d:
            r2 = 0
            r1 = 2131626186(0x7f0e08ca, float:1.8879601E38)
            java.lang.Object[] r3 = new java.lang.Object[r7]
            r3[r2] = r11
            java.lang.String r2 = r12.title
            r3[r6] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0417
        L_0x0638:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x063e:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0675
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0672
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x066f
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x066f:
            r1.append(r3)
        L_0x0672:
            int r2 = r2 + 1
            goto L_0x063e
        L_0x0675:
            r0 = 2131626186(0x7f0e08ca, float:1.8879601E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r3 = 0
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            java.lang.String r1 = r1.toString()
            r9 = 2
            r2[r9] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0417
        L_0x0690:
            r9 = 2
            boolean r13 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r13 == 0) goto L_0x06a9
            r0 = 2131626190(0x7f0e08ce, float:1.887961E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationGroupCreatedCall"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x06a9:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r9 == 0) goto L_0x0767
            int r1 = r8.user_id
            if (r1 != 0) goto L_0x06cb
            java.util.ArrayList<java.lang.Integer> r2 = r8.users
            int r2 = r2.size()
            if (r2 != r6) goto L_0x06cb
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            goto L_0x06cc
        L_0x06cb:
            r2 = 0
        L_0x06cc:
            if (r1 == 0) goto L_0x070f
            if (r1 != r10) goto L_0x06e4
            r0 = 2131626195(0x7f0e08d3, float:1.887962E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x06e4:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x06f4
            r1 = 0
            return r1
        L_0x06f4:
            r1 = 2131626194(0x7f0e08d2, float:1.8879617E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r3 = 0
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x070f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0715:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x074c
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0749
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0746
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0746:
            r1.append(r3)
        L_0x0749:
            int r2 = r2 + 1
            goto L_0x0715
        L_0x074c:
            r0 = 2131626194(0x7f0e08d2, float:1.8879617E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r3 = 0
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            java.lang.String r1 = r1.toString()
            r9 = 2
            r2[r9] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0417
        L_0x0767:
            r9 = 2
            boolean r13 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r13 == 0) goto L_0x0780
            r0 = 2131626204(0x7f0e08dc, float:1.8879638E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r13 = 0
            r1[r13] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x0780:
            r13 = 0
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r7 == 0) goto L_0x0798
            r0 = 2131626183(0x7f0e08c7, float:1.8879595E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r1[r13] = r11
            java.lang.String r2 = r8.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x0798:
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r7 != 0) goto L_0x10b1
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r7 == 0) goto L_0x07a2
            goto L_0x10b1
        L_0x07a2:
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r5 == 0) goto L_0x0808
            int r1 = r8.user_id
            if (r1 != r10) goto L_0x07bf
            r0 = 2131626197(0x7f0e08d5, float:1.8879623E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x07bf:
            r3 = 2
            r4 = 0
            if (r1 != r2) goto L_0x07d6
            r0 = 2131626198(0x7f0e08d6, float:1.8879625E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x07d6:
            org.telegram.messenger.MessagesController r1 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x07ec
            r2 = 0
            return r2
        L_0x07ec:
            r1 = 2131626196(0x7f0e08d4, float:1.8879621E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x16e7
        L_0x0808:
            r2 = 0
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r5 == 0) goto L_0x0815
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x16e7
        L_0x0815:
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r5 == 0) goto L_0x0821
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x16e7
        L_0x0821:
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r5 == 0) goto L_0x0837
            r0 = 2131624123(0x7f0e00bb, float:1.8875417E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r5 = 0
            r1[r5] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x0837:
            r5 = 0
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r7 == 0) goto L_0x084d
            r0 = 2131624123(0x7f0e00bb, float:1.8875417E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x084d:
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r5 == 0) goto L_0x0859
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x16e7
        L_0x0859:
            boolean r5 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r5 == 0) goto L_0x10a5
            r2 = 20
            if (r12 == 0) goto L_0x0b70
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r12)
            if (r5 == 0) goto L_0x086b
            boolean r5 = r12.megagroup
            if (r5 == 0) goto L_0x0b70
        L_0x086b:
            org.telegram.messenger.MessageObject r5 = r0.replyMessageObject
            if (r5 != 0) goto L_0x0884
            r0 = 2131626151(0x7f0e08a7, float:1.887953E38)
            r7 = 2
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r8 = 0
            r1[r8] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x0884:
            r7 = 2
            r8 = 0
            boolean r9 = r5.isMusic()
            if (r9 == 0) goto L_0x089f
            r0 = 2131626148(0x7f0e08a4, float:1.8879524E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r8] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x089f:
            boolean r7 = r5.isVideo()
            if (r7 == 0) goto L_0x08f3
            r7 = 19
            if (r1 < r7) goto L_0x08de
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08de
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131626172(0x7f0e08bc, float:1.8879573E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            r2[r6] = r0
            java.lang.String r0 = r12.title
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x08de:
            r3 = 0
            r4 = 2
            r0 = 2131626175(0x7f0e08bf, float:1.8879579E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x08f3:
            boolean r7 = r5.isGif()
            if (r7 == 0) goto L_0x0947
            r7 = 19
            if (r1 < r7) goto L_0x0932
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0932
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131626172(0x7f0e08bc, float:1.8879573E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            r2[r6] = r0
            java.lang.String r0 = r12.title
            r7 = 2
            r2[r7] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0932:
            r3 = 0
            r7 = 2
            r0 = 2131626142(0x7f0e089e, float:1.8879512E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0947:
            r3 = 0
            r7 = 2
            boolean r8 = r5.isVoice()
            if (r8 == 0) goto L_0x0962
            r0 = 2131626178(0x7f0e08c2, float:1.8879585E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0962:
            boolean r8 = r5.isRoundVideo()
            if (r8 == 0) goto L_0x097b
            r0 = 2131626163(0x7f0e08b3, float:1.8879554E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x097b:
            boolean r3 = r5.isSticker()
            if (r3 != 0) goto L_0x0b3e
            boolean r3 = r5.isAnimatedSticker()
            if (r3 == 0) goto L_0x0989
            goto L_0x0b3e
        L_0x0989:
            org.telegram.tgnet.TLRPC$Message r3 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r3.media
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x09dd
            r8 = 19
            if (r1 < r8) goto L_0x09c8
            java.lang.String r0 = r3.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09c8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r4)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131626172(0x7f0e08bc, float:1.8879573E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            r2[r6] = r0
            java.lang.String r0 = r12.title
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x09c8:
            r3 = 0
            r4 = 2
            r0 = 2131626127(0x7f0e088f, float:1.8879481E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x09dd:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x0b29
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x09e7
            goto L_0x0b29
        L_0x09e7:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0a00
            r0 = 2131626138(0x7f0e089a, float:1.8879504E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0a00:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r4 == 0) goto L_0x0a2a
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626124(0x7f0e088c, float:1.8879475E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0a2a:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0a6a
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0a50
            r1 = 2131626160(0x7f0e08b0, float:1.8879548E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            java.lang.String r0 = r0.question
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0a50:
            r2 = 3
            r3 = 0
            r4 = 2
            r1 = 2131626157(0x7f0e08ad, float:1.8879542E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0a6a:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0aba
            r0 = 19
            if (r1 < r0) goto L_0x0aa5
            java.lang.String r0 = r3.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0aa5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2131626172(0x7f0e08bc, float:1.8879573E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            r2[r6] = r0
            java.lang.String r0 = r12.title
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0aa5:
            r3 = 0
            r4 = 2
            r0 = 2131626154(0x7f0e08aa, float:1.8879536E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0aba:
            r3 = 0
            r4 = 2
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0ad3
            r0 = 2131626130(0x7f0e0892, float:1.8879488E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0ad3:
            java.lang.CharSequence r0 = r5.messageText
            if (r0 == 0) goto L_0x0b14
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0b14
            java.lang.CharSequence r0 = r5.messageText
            int r1 = r0.length()
            if (r1 <= r2) goto L_0x0afc
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0afd
        L_0x0afc:
            r3 = 0
        L_0x0afd:
            r1 = 2131626172(0x7f0e08bc, float:1.8879573E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r11
            r2[r6] = r0
            java.lang.String r0 = r12.title
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0b14:
            r3 = 0
            r4 = 2
            r0 = 2131626151(0x7f0e08a7, float:1.887953E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0b29:
            r3 = 0
            r4 = 2
            r0 = 2131626136(0x7f0e0898, float:1.88795E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0b3e:
            r3 = 0
            java.lang.String r0 = r5.getStickerEmoji()
            if (r0 == 0) goto L_0x0b5c
            r1 = 2131626168(0x7f0e08b8, float:1.8879565E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0b5c:
            r4 = 2
            r0 = 2131626166(0x7f0e08b6, float:1.887956E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0b70:
            if (r12 == 0) goto L_0x0e22
            org.telegram.messenger.MessageObject r5 = r0.replyMessageObject
            if (r5 != 0) goto L_0x0b88
            r0 = 2131626152(0x7f0e08a8, float:1.8879532E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r7 = 0
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x0b88:
            r7 = 0
            boolean r8 = r5.isMusic()
            if (r8 == 0) goto L_0x0ba0
            r0 = 2131626149(0x7f0e08a5, float:1.8879526E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0ba0:
            boolean r7 = r5.isVideo()
            r8 = 2131626173(0x7f0e08bd, float:1.8879575E38)
            java.lang.String r9 = "NotificationActionPinnedTextChannel"
            if (r7 == 0) goto L_0x0bee
            r7 = 19
            if (r1 < r7) goto L_0x0bdc
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0bdc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r12.title
            r3 = 0
            r1[r3] = r2
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x0bdc:
            r3 = 0
            r0 = 2131626176(0x7f0e08c0, float:1.887958E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0bee:
            boolean r7 = r5.isGif()
            if (r7 == 0) goto L_0x0CLASSNAME
            r7 = 19
            if (r1 < r7) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r12.title
            r3 = 0
            r1[r3] = r2
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x0CLASSNAME:
            r3 = 0
            r0 = 2131626143(0x7f0e089f, float:1.8879514E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0CLASSNAME:
            r3 = 0
            boolean r7 = r5.isVoice()
            if (r7 == 0) goto L_0x0c4f
            r0 = 2131626179(0x7f0e08c3, float:1.8879587E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0c4f:
            boolean r7 = r5.isRoundVideo()
            if (r7 == 0) goto L_0x0CLASSNAME
            r0 = 2131626164(0x7f0e08b4, float:1.8879556E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0CLASSNAME:
            boolean r3 = r5.isSticker()
            if (r3 != 0) goto L_0x0df6
            boolean r3 = r5.isAnimatedSticker()
            if (r3 == 0) goto L_0x0CLASSNAME
            goto L_0x0df6
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r3 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r3.media
            boolean r10 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r10 == 0) goto L_0x0cbd
            r10 = 19
            if (r1 < r10) goto L_0x0cab
            java.lang.String r0 = r3.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0cab
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r4)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r12.title
            r3 = 0
            r1[r3] = r2
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x0cab:
            r3 = 0
            r0 = 2131626128(0x7f0e0890, float:1.8879483E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0cbd:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x0de4
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x0cc7
            goto L_0x0de4
        L_0x0cc7:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0cdd
            r0 = 2131626139(0x7f0e089b, float:1.8879506E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0cdd:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r4 == 0) goto L_0x0d04
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626125(0x7f0e088d, float:1.8879477E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r12.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r2[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0d04:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0d3e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0d27
            r1 = 2131626161(0x7f0e08b1, float:1.887955E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r12.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0d27:
            r2 = 2
            r4 = 0
            r1 = 2131626158(0x7f0e08ae, float:1.8879544E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r12.title
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0d3e:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0d83
            r0 = 19
            if (r1 < r0) goto L_0x0d71
            java.lang.String r0 = r3.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0d71
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r12.title
            r3 = 0
            r1[r3] = r2
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x0d71:
            r3 = 0
            r0 = 2131626155(0x7f0e08ab, float:1.8879538E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0d83:
            r3 = 0
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0d99
            r0 = 2131626131(0x7f0e0893, float:1.887949E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0d99:
            java.lang.CharSequence r0 = r5.messageText
            if (r0 == 0) goto L_0x0dd2
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0dd2
            java.lang.CharSequence r0 = r5.messageText
            int r1 = r0.length()
            if (r1 <= r2) goto L_0x0dc2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r7 = 0
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0dc3
        L_0x0dc2:
            r7 = 0
        L_0x0dc3:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r12.title
            r1[r7] = r2
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x0dd2:
            r7 = 0
            r0 = 2131626152(0x7f0e08a8, float:1.8879532E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0de4:
            r7 = 0
            r0 = 2131626137(0x7f0e0899, float:1.8879502E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0df6:
            r7 = 0
            java.lang.String r0 = r5.getStickerEmoji()
            if (r0 == 0) goto L_0x0e11
            r1 = 2131626169(0x7f0e08b9, float:1.8879567E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r12.title
            r2[r7] = r3
            r2[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0e11:
            r0 = 2131626167(0x7f0e08b7, float:1.8879563E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0e22:
            r7 = 0
            org.telegram.messenger.MessageObject r5 = r0.replyMessageObject
            if (r5 != 0) goto L_0x0e36
            r0 = 2131626153(0x7f0e08a9, float:1.8879534E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x0e36:
            boolean r8 = r5.isMusic()
            if (r8 == 0) goto L_0x0e4b
            r0 = 2131626150(0x7f0e08a6, float:1.8879528E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedMusicUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0e4b:
            boolean r7 = r5.isVideo()
            r8 = 2131626174(0x7f0e08be, float:1.8879577E38)
            java.lang.String r9 = "NotificationActionPinnedTextUser"
            if (r7 == 0) goto L_0x0e95
            r7 = 19
            if (r1 < r7) goto L_0x0e85
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0e85
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x0e85:
            r2 = 0
            r0 = 2131626177(0x7f0e08c1, float:1.8879583E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0e95:
            boolean r7 = r5.isGif()
            if (r7 == 0) goto L_0x0eda
            r7 = 19
            if (r1 < r7) goto L_0x0eca
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0eca
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r3)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r11
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x0eca:
            r3 = 0
            r0 = 2131626144(0x7f0e08a0, float:1.8879516E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r11
            java.lang.String r2 = "NotificationActionPinnedGifUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0eda:
            r3 = 0
            boolean r7 = r5.isVoice()
            if (r7 == 0) goto L_0x0ef0
            r0 = 2131626180(0x7f0e08c4, float:1.8879589E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r11
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0ef0:
            boolean r7 = r5.isRoundVideo()
            if (r7 == 0) goto L_0x0var_
            r0 = 2131626165(0x7f0e08b5, float:1.8879558E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r11
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0var_:
            boolean r3 = r5.isSticker()
            if (r3 != 0) goto L_0x107d
            boolean r3 = r5.isAnimatedSticker()
            if (r3 == 0) goto L_0x0var_
            goto L_0x107d
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Message r3 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r3.media
            boolean r10 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r10 == 0) goto L_0x0var_
            r10 = 19
            if (r1 < r10) goto L_0x0var_
            java.lang.String r0 = r3.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0var_
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r4)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x0var_:
            r2 = 0
            r0 = 2131626129(0x7f0e0891, float:1.8879485E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0var_:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x106d
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x0var_
            goto L_0x106d
        L_0x0var_:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0var_
            r0 = 2131626140(0x7f0e089c, float:1.8879508E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r4 = 0
            r1[r4] = r11
            java.lang.String r2 = "NotificationActionPinnedGeoLiveUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x0var_:
            r4 = 0
            boolean r10 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r10 == 0) goto L_0x0f9b
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626126(0x7f0e088e, float:1.887948E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r11
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r2[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedContactUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0f9b:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0fd1
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0fbc
            r1 = 2131626162(0x7f0e08b2, float:1.8879552E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            java.lang.String r0 = r0.question
            r2[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0fbc:
            r2 = 2
            r3 = 0
            r1 = 2131626159(0x7f0e08af, float:1.8879546E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r11
            java.lang.String r0 = r0.question
            r2[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedPollUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x0fd1:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x1012
            r0 = 19
            if (r1 < r0) goto L_0x1002
            java.lang.String r0 = r3.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x1002
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r1 = r5.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r11
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x1002:
            r3 = 0
            r0 = 2131626156(0x7f0e08ac, float:1.887954E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r11
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x1012:
            r3 = 0
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x1026
            r0 = 2131626135(0x7f0e0897, float:1.8879498E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r11
            java.lang.String r2 = "NotificationActionPinnedGameUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x1026:
            java.lang.CharSequence r0 = r5.messageText
            if (r0 == 0) goto L_0x105d
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x105d
            java.lang.CharSequence r0 = r5.messageText
            int r1 = r0.length()
            if (r1 <= r2) goto L_0x104f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x1050
        L_0x104f:
            r3 = 0
        L_0x1050:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r11
            r1[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            goto L_0x0417
        L_0x105d:
            r3 = 0
            r0 = 2131626153(0x7f0e08a9, float:1.8879534E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x106d:
            r3 = 0
            r0 = 2131626141(0x7f0e089d, float:1.887951E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r11
            java.lang.String r2 = "NotificationActionPinnedGeoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x107d:
            r3 = 0
            java.lang.String r0 = r5.getStickerEmoji()
            if (r0 == 0) goto L_0x1096
            r1 = 2131626170(0x7f0e08ba, float:1.8879569E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r11
            r2[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x1096:
            r0 = 2131626171(0x7f0e08bb, float:1.887957E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r11
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x10a5:
            boolean r1 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r1 == 0) goto L_0x16e6
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x16e7
        L_0x10b1:
            org.telegram.tgnet.TLRPC$Peer r1 = r5.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x10e5
            boolean r1 = r12.megagroup
            if (r1 != 0) goto L_0x10e5
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x10d3
            r0 = 2131624727(0x7f0e0317, float:1.8876642E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x10d3:
            r3 = 0
            r0 = 2131624693(0x7f0e02f5, float:1.8876573E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r12.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x10e5:
            r3 = 0
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x1100
            r0 = 2131626185(0x7f0e08c9, float:1.88796E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x1100:
            r1 = 2
            r0 = 2131626184(0x7f0e08c8, float:1.8879597E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x1114:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r12)
            if (r2 == 0) goto L_0x13b0
            boolean r2 = r12.megagroup
            if (r2 != 0) goto L_0x13b0
            boolean r2 = r19.isMediaEmpty()
            if (r2 == 0) goto L_0x1157
            if (r20 != 0) goto L_0x1147
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1147
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x1147:
            r2 = 0
            r0 = 2131624677(0x7f0e02e5, float:1.887654E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x1157:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r2.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x11a3
            if (r20 != 0) goto L_0x1193
            r3 = 19
            if (r1 < r3) goto L_0x1193
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1193
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x1193:
            r2 = 0
            r0 = 2131624678(0x7f0e02e6, float:1.8876542E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x11a3:
            boolean r2 = r19.isVideo()
            if (r2 == 0) goto L_0x11ef
            if (r20 != 0) goto L_0x11df
            r2 = 19
            if (r1 < r2) goto L_0x11df
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x11df
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x11df:
            r2 = 0
            r0 = 2131624684(0x7f0e02ec, float:1.8876555E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageVideo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x11ef:
            r2 = 0
            boolean r5 = r19.isVoice()
            if (r5 == 0) goto L_0x1205
            r0 = 2131624669(0x7f0e02dd, float:1.8876524E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageAudio"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x1205:
            boolean r5 = r19.isRoundVideo()
            if (r5 == 0) goto L_0x121a
            r0 = 2131624681(0x7f0e02e9, float:1.8876549E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageRound"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x121a:
            boolean r5 = r19.isMusic()
            if (r5 == 0) goto L_0x122f
            r0 = 2131624676(0x7f0e02e4, float:1.8876538E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageMusic"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x122f:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x1253
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            r0 = 2131624670(0x7f0e02de, float:1.8876526E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r11
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r1[r6] = r2
            java.lang.String r2 = "ChannelMessageContact2"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x1253:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x1289
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x1274
            r1 = 2131624680(0x7f0e02e8, float:1.8876547E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            java.lang.String r0 = r0.question
            r2[r6] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x1274:
            r2 = 2
            r3 = 0
            r1 = 2131624679(0x7f0e02e7, float:1.8876545E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r11
            java.lang.String r0 = r0.question
            r2[r6] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x1289:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r2 != 0) goto L_0x13a0
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r2 == 0) goto L_0x1293
            goto L_0x13a0
        L_0x1293:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x12a7
            r0 = 2131624674(0x7f0e02e2, float:1.8876534E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageLiveLocation"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x12a7:
            boolean r2 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x1372
            boolean r2 = r19.isSticker()
            if (r2 != 0) goto L_0x134a
            boolean r2 = r19.isAnimatedSticker()
            if (r2 == 0) goto L_0x12b9
            goto L_0x134a
        L_0x12b9:
            boolean r2 = r19.isGif()
            if (r2 == 0) goto L_0x1305
            if (r20 != 0) goto L_0x12f5
            r2 = 19
            if (r1 < r2) goto L_0x12f5
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x12f5
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x12f5:
            r2 = 0
            r0 = 2131624673(0x7f0e02e1, float:1.8876532E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x1305:
            r2 = 0
            if (r20 != 0) goto L_0x133b
            r3 = 19
            if (r1 < r3) goto L_0x133b
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x133b
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r11
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x133b:
            r0 = 2131624671(0x7f0e02df, float:1.8876528E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x134a:
            r2 = 0
            java.lang.String r0 = r19.getStickerEmoji()
            if (r0 == 0) goto L_0x1363
            r1 = 2131624683(0x7f0e02eb, float:1.8876553E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r11
            r3[r6] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0417
        L_0x1363:
            r0 = 2131624682(0x7f0e02ea, float:1.887655E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x1372:
            r2 = 0
            if (r20 != 0) goto L_0x1391
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1391
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r11
            java.lang.CharSequence r0 = r0.messageText
            r1[r6] = r0
            r0 = 2131626248(0x7f0e0908, float:1.8879727E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r13, r0, r1)
            r21[r2] = r6
            goto L_0x16e7
        L_0x1391:
            r0 = 2131624677(0x7f0e02e5, float:1.887654E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x13a0:
            r2 = 0
            r0 = 2131624675(0x7f0e02e3, float:1.8876536E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageMap"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x13b0:
            boolean r2 = r19.isMediaEmpty()
            r5 = 2131626231(0x7f0e08f7, float:1.8879692E38)
            java.lang.String r7 = "NotificationMessageGroupText"
            if (r2 == 0) goto L_0x13f3
            if (r20 != 0) goto L_0x13de
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x13de
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2 = 2
            r1[r2] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r5, r1)
            goto L_0x16e7
        L_0x13de:
            r2 = 2
            r0 = 2131626224(0x7f0e08f0, float:1.8879678E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupNoText"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x13f3:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r2.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x1444
            if (r20 != 0) goto L_0x142f
            r3 = 19
            if (r1 < r3) goto L_0x142f
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x142f
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r5, r1)
            goto L_0x16e7
        L_0x142f:
            r2 = 2
            r0 = 2131626225(0x7f0e08f1, float:1.887968E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x1444:
            boolean r2 = r19.isVideo()
            if (r2 == 0) goto L_0x1495
            if (r20 != 0) goto L_0x1480
            r2 = 19
            if (r1 < r2) goto L_0x1480
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1480
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r5, r1)
            goto L_0x16e7
        L_0x1480:
            r2 = 2
            r0 = 2131626232(0x7f0e08f8, float:1.8879694E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r8 = 0
            r1[r8] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = " "
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x1495:
            r2 = 2
            r8 = 0
            boolean r9 = r19.isVoice()
            if (r9 == 0) goto L_0x14b0
            r0 = 2131626214(0x7f0e08e6, float:1.8879658E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r8] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupAudio"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x14b0:
            boolean r9 = r19.isRoundVideo()
            if (r9 == 0) goto L_0x14c9
            r0 = 2131626228(0x7f0e08f4, float:1.8879686E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r8] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupRound"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x14c9:
            boolean r9 = r19.isMusic()
            if (r9 == 0) goto L_0x14e2
            r0 = 2131626223(0x7f0e08ef, float:1.8879676E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r8] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupMusic"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x14e2:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x150c
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r2
            r0 = 2131626215(0x7f0e08e7, float:1.887966E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r11
            java.lang.String r3 = r12.title
            r1[r6] = r3
            java.lang.String r3 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r3, r2)
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupContact2"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x150c:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x154c
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x1532
            r1 = 2131626227(0x7f0e08f3, float:1.8879684E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            java.lang.String r0 = r0.question
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x1532:
            r2 = 3
            r3 = 0
            r4 = 2
            r1 = 2131626226(0x7f0e08f2, float:1.8879682E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r11
            java.lang.String r3 = r12.title
            r2[r6] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0417
        L_0x154c:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x156c
            r0 = 2131626217(0x7f0e08e9, float:1.8879664E38)
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r11
            java.lang.String r3 = r12.title
            r1[r6] = r3
            org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
            java.lang.String r2 = r2.title
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupGame"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x156c:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x16a1
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x1576
            goto L_0x16a1
        L_0x1576:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x158f
            r0 = 2131626221(0x7f0e08ed, float:1.8879672E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x158f:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x166f
            boolean r2 = r19.isSticker()
            if (r2 != 0) goto L_0x163d
            boolean r2 = r19.isAnimatedSticker()
            if (r2 == 0) goto L_0x15a1
            goto L_0x163d
        L_0x15a1:
            boolean r2 = r19.isGif()
            if (r2 == 0) goto L_0x15f2
            if (r20 != 0) goto L_0x15dd
            r2 = 19
            if (r1 < r2) goto L_0x15dd
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x15dd
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r5, r1)
            goto L_0x16e7
        L_0x15dd:
            r2 = 2
            r0 = 2131626219(0x7f0e08eb, float:1.8879668E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupGif"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x15f2:
            if (r20 != 0) goto L_0x1628
            r2 = 19
            if (r1 < r2) goto L_0x1628
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1628
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            r1[r2] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r5, r1)
            goto L_0x16e7
        L_0x1628:
            r2 = 2
            r0 = 2131626216(0x7f0e08e8, float:1.8879662E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupDocument"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x163d:
            r2 = 0
            java.lang.String r0 = r19.getStickerEmoji()
            if (r0 == 0) goto L_0x165b
            r1 = 2131626230(0x7f0e08f6, float:1.887969E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r11
            java.lang.String r2 = r12.title
            r3[r6] = r2
            r4 = 2
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0417
        L_0x165b:
            r4 = 2
            r0 = 2131626229(0x7f0e08f5, float:1.8879688E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0417
        L_0x166f:
            if (r20 != 0) goto L_0x168d
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x168d
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.CharSequence r0 = r0.messageText
            r3 = 2
            r1[r3] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r5, r1)
            goto L_0x16e7
        L_0x168d:
            r2 = 0
            r3 = 2
            r0 = 2131626224(0x7f0e08f0, float:1.8879678E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupNoText"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x16a1:
            r2 = 0
            r3 = 2
            r0 = 2131626222(0x7f0e08ee, float:1.8879674E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupMap"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x16b5:
            r2 = 0
            if (r22 == 0) goto L_0x16ba
            r22[r2] = r2
        L_0x16ba:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r12)
            if (r0 == 0) goto L_0x16d2
            boolean r0 = r12.megagroup
            if (r0 != 0) goto L_0x16d2
            r0 = 2131624677(0x7f0e02e5, float:1.887654E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r2] = r11
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x16d2:
            r0 = 2131626224(0x7f0e08f0, float:1.8879678E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r11
            java.lang.String r2 = r12.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationMessageGroupNoText"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x16e7
        L_0x16e5:
            r2 = 0
        L_0x16e6:
            r14 = r2
        L_0x16e7:
            return r14
        L_0x16e8:
            r0 = 2131627849(0x7f0e0var_, float:1.8882974E38)
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

    public void deleteNotificationChannel(long j, int i) {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationsQueue.postRunnable(new Runnable(i, j) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    NotificationsController.this.lambda$deleteNotificationChannel$31$NotificationsController(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$deleteNotificationChannel$31 */
    public /* synthetic */ void lambda$deleteNotificationChannel$31$NotificationsController(int i, long j) {
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            if (i == 0 || i == -1) {
                String str = "org.telegram.key" + j;
                String string = notificationsSettings.getString(str, (String) null);
                if (string != null) {
                    edit.remove(str).remove(str + "_s");
                    systemNotificationManager.deleteNotificationChannel(string);
                }
            }
            if (i == 1 || i == -1) {
                String str2 = "org.telegram.keyia" + j;
                String string2 = notificationsSettings.getString(str2, (String) null);
                if (string2 != null) {
                    edit.remove(str2).remove(str2 + "_s");
                    systemNotificationManager.deleteNotificationChannel(string2);
                }
            }
            edit.commit();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void deleteNotificationChannelGlobal(int i) {
        deleteNotificationChannelGlobal(i, -1);
    }

    public void deleteNotificationChannelGlobal(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationsQueue.postRunnable(new Runnable(i2, i) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$deleteNotificationChannelGlobal$32 */
    public /* synthetic */ void lambda$deleteNotificationChannelGlobal$32$NotificationsController(int i, int i2) {
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            if (i == 0 || i == -1) {
                String str = i2 == 2 ? "channels" : i2 == 0 ? "groups" : "private";
                String string = notificationsSettings.getString(str, (String) null);
                if (string != null) {
                    SharedPreferences.Editor remove = edit.remove(str);
                    remove.remove(str + "_s");
                    systemNotificationManager.deleteNotificationChannel(string);
                }
            }
            if (i == 1 || i == -1) {
                String str2 = i2 == 2 ? "channels_ia" : i2 == 0 ? "groups_ia" : "private_ia";
                String string2 = notificationsSettings.getString(str2, (String) null);
                if (string2 != null) {
                    SharedPreferences.Editor remove2 = edit.remove(str2);
                    remove2.remove(str2 + "_s");
                    systemNotificationManager.deleteNotificationChannel(string2);
                }
            }
            edit.commit();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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

    /* JADX WARNING: Removed duplicated region for block: B:210:0x0461 A[LOOP:1: B:208:0x045e->B:210:0x0461, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0476  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x04a3  */
    @android.annotation.TargetApi(26)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String validateChannelId(long r29, java.lang.String r31, long[] r32, int r33, android.net.Uri r34, int r35, boolean r36, boolean r37, boolean r38, int r39) {
        /*
            r28 = this;
            r0 = r28
            r1 = r29
            r3 = r35
            r4 = r39
            r28.ensureGroupsCreated()
            org.telegram.messenger.AccountInstance r5 = r28.getAccountInstance()
            android.content.SharedPreferences r5 = r5.getNotificationsSettings()
            java.lang.String r6 = "groups"
            java.lang.String r7 = "private"
            java.lang.String r8 = "channels"
            r9 = 2
            if (r38 == 0) goto L_0x0030
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "other"
            r10.append(r11)
            int r11 = r0.currentAccount
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            goto L_0x0069
        L_0x0030:
            if (r4 != r9) goto L_0x0044
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r8)
            int r11 = r0.currentAccount
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            goto L_0x0069
        L_0x0044:
            if (r4 != 0) goto L_0x0058
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r6)
            int r11 = r0.currentAccount
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            goto L_0x0069
        L_0x0058:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r7)
            int r11 = r0.currentAccount
            r10.append(r11)
            java.lang.String r10 = r10.toString()
        L_0x0069:
            r11 = 1
            r12 = 0
            if (r36 != 0) goto L_0x0072
            int r13 = (int) r1
            if (r13 != 0) goto L_0x0072
            r13 = 1
            goto L_0x0073
        L_0x0072:
            r13 = 0
        L_0x0073:
            if (r38 == 0) goto L_0x0081
            r6 = 2131626298(0x7f0e093a, float:1.8879828E38)
            java.lang.String r7 = "NotificationsSilent"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.String r7 = "silent"
            goto L_0x00d2
        L_0x0081:
            if (r36 == 0) goto L_0x00aa
            if (r37 == 0) goto L_0x008b
            r14 = 2131626276(0x7f0e0924, float:1.8879784E38)
            java.lang.String r15 = "NotificationsInAppDefault"
            goto L_0x0090
        L_0x008b:
            r14 = 2131626260(0x7f0e0914, float:1.8879751E38)
            java.lang.String r15 = "NotificationsDefault"
        L_0x0090:
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            if (r4 != r9) goto L_0x009d
            if (r37 == 0) goto L_0x009a
            java.lang.String r8 = "channels_ia"
        L_0x009a:
            r7 = r8
        L_0x009b:
            r6 = r14
            goto L_0x00d2
        L_0x009d:
            if (r4 != 0) goto L_0x00a5
            if (r37 == 0) goto L_0x00a3
            java.lang.String r6 = "groups_ia"
        L_0x00a3:
            r7 = r6
            goto L_0x009b
        L_0x00a5:
            if (r37 == 0) goto L_0x009b
            java.lang.String r7 = "private_ia"
            goto L_0x009b
        L_0x00aa:
            if (r37 == 0) goto L_0x00ba
            r6 = 2131626257(0x7f0e0911, float:1.8879745E38)
            java.lang.Object[] r7 = new java.lang.Object[r11]
            r7[r12] = r31
            java.lang.String r8 = "NotificationsChatInApp"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r8, r6, r7)
            goto L_0x00bc
        L_0x00ba:
            r6 = r31
        L_0x00bc:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            if (r37 == 0) goto L_0x00c6
            java.lang.String r8 = "org.telegram.keyia"
            goto L_0x00c8
        L_0x00c6:
            java.lang.String r8 = "org.telegram.key"
        L_0x00c8:
            r7.append(r8)
            r7.append(r1)
            java.lang.String r7 = r7.toString()
        L_0x00d2:
            r8 = 0
            java.lang.String r14 = r5.getString(r7, r8)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r15.append(r7)
            java.lang.String r11 = "_s"
            r15.append(r11)
            java.lang.String r15 = r15.toString()
            java.lang.String r15 = r5.getString(r15, r8)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r12 = "secret"
            if (r14 == 0) goto L_0x040b
            android.app.NotificationManager r9 = systemNotificationManager
            android.app.NotificationChannel r9 = r9.getNotificationChannel(r14)
            if (r9 == 0) goto L_0x03f3
            if (r38 != 0) goto L_0x040b
            r16 = r6
            int r6 = r9.getImportance()
            r17 = r14
            android.net.Uri r14 = r9.getSound()
            long[] r18 = r9.getVibrationPattern()
            r19 = r10
            boolean r10 = r9.shouldVibrate()
            if (r10 != 0) goto L_0x0124
            if (r18 != 0) goto L_0x0124
            r21 = r7
            r20 = r11
            r11 = 2
            long[] r7 = new long[r11]
            r7 = {0, 0} // fill-array
            goto L_0x012a
        L_0x0124:
            r21 = r7
            r20 = r11
            r7 = r18
        L_0x012a:
            int r9 = r9.getLightColor()
            r18 = r10
            if (r7 == 0) goto L_0x0140
            r11 = 0
        L_0x0133:
            int r10 = r7.length
            if (r11 >= r10) goto L_0x0140
            r1 = r7[r11]
            r8.append(r1)
            int r11 = r11 + 1
            r1 = r29
            goto L_0x0133
        L_0x0140:
            r8.append(r9)
            if (r14 == 0) goto L_0x014c
            java.lang.String r1 = r14.toString()
            r8.append(r1)
        L_0x014c:
            r8.append(r6)
            if (r36 != 0) goto L_0x0156
            if (r13 == 0) goto L_0x0156
            r8.append(r12)
        L_0x0156:
            java.lang.String r1 = r8.toString()
            java.lang.String r1 = org.telegram.messenger.Utilities.MD5(r1)
            r2 = 0
            r8.setLength(r2)
            boolean r2 = r1.equals(r15)
            if (r2 != 0) goto L_0x03d2
            java.lang.String r2 = "notify2_"
            if (r6 != 0) goto L_0x01a6
            android.content.SharedPreferences$Editor r6 = r5.edit()
            if (r36 == 0) goto L_0x0187
            if (r37 != 0) goto L_0x0181
            java.lang.String r2 = getGlobalNotificationsKey(r39)
            r10 = 2147483647(0x7fffffff, float:NaN)
            r6.putInt(r2, r10)
            r0.updateServerNotificationsSettings((int) r4)
        L_0x0181:
            r22 = r12
            r10 = 1
            r11 = r29
            goto L_0x01a2
        L_0x0187:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r2)
            r22 = r12
            r11 = r29
            r10.append(r11)
            java.lang.String r2 = r10.toString()
            r10 = 2
            r6.putInt(r2, r10)
            r10 = 1
            r0.updateServerNotificationsSettings(r11, r10)
        L_0x01a2:
            r23 = r1
            goto L_0x0236
        L_0x01a6:
            r22 = r12
            r10 = 1
            r11 = r29
            if (r6 == r3) goto L_0x0232
            if (r37 != 0) goto L_0x022d
            android.content.SharedPreferences$Editor r10 = r5.edit()
            r23 = r1
            r1 = 4
            if (r6 == r1) goto L_0x01c9
            r1 = 5
            if (r6 != r1) goto L_0x01bc
            goto L_0x01c9
        L_0x01bc:
            r1 = 1
            if (r6 != r1) goto L_0x01c2
            r1 = 2
            r6 = 4
            goto L_0x01cb
        L_0x01c2:
            r1 = 2
            if (r6 != r1) goto L_0x01c7
            r6 = 5
            goto L_0x01cb
        L_0x01c7:
            r6 = 0
            goto L_0x01cb
        L_0x01c9:
            r1 = 2
            r6 = 1
        L_0x01cb:
            if (r36 == 0) goto L_0x01f0
            java.lang.String r2 = getGlobalNotificationsKey(r39)
            r1 = 0
            android.content.SharedPreferences$Editor r2 = r10.putInt(r2, r1)
            r2.commit()
            r1 = 2
            if (r4 != r1) goto L_0x01e2
            java.lang.String r1 = "priority_channel"
            r10.putInt(r1, r6)
            goto L_0x022b
        L_0x01e2:
            if (r4 != 0) goto L_0x01ea
            java.lang.String r1 = "priority_group"
            r10.putInt(r1, r6)
            goto L_0x022b
        L_0x01ea:
            java.lang.String r1 = "priority_messages"
            r10.putInt(r1, r6)
            goto L_0x022b
        L_0x01f0:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            r2 = 0
            r10.putInt(r1, r2)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "notifyuntil_"
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            r10.remove(r1)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "priority_"
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            r10.putInt(r1, r6)
        L_0x022b:
            r6 = r10
            goto L_0x0230
        L_0x022d:
            r23 = r1
            r6 = 0
        L_0x0230:
            r10 = 1
            goto L_0x0236
        L_0x0232:
            r23 = r1
            r6 = 0
            r10 = 0
        L_0x0236:
            if (r14 != 0) goto L_0x023a
            if (r34 != 0) goto L_0x024c
        L_0x023a:
            if (r14 == 0) goto L_0x0328
            if (r34 == 0) goto L_0x024c
            java.lang.String r1 = r14.toString()
            java.lang.String r2 = r34.toString()
            boolean r1 = android.text.TextUtils.equals(r1, r2)
            if (r1 != 0) goto L_0x0328
        L_0x024c:
            if (r37 != 0) goto L_0x031c
            if (r6 != 0) goto L_0x0254
            android.content.SharedPreferences$Editor r6 = r5.edit()
        L_0x0254:
            java.lang.String r1 = "GroupSound"
            java.lang.String r2 = "GlobalSound"
            java.lang.String r10 = "ChannelSound"
            r24 = r7
            java.lang.String r7 = "sound_"
            r25 = r15
            java.lang.String r15 = "NoSound"
            if (r14 != 0) goto L_0x0290
            if (r36 == 0) goto L_0x0279
            r26 = r13
            r13 = 2
            if (r4 != r13) goto L_0x026f
            r6.putString(r10, r15)
            goto L_0x028d
        L_0x026f:
            if (r4 != 0) goto L_0x0275
            r6.putString(r1, r15)
            goto L_0x028d
        L_0x0275:
            r6.putString(r2, r15)
            goto L_0x028d
        L_0x0279:
            r26 = r13
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            r6.putString(r1, r15)
        L_0x028d:
            r27 = r14
            goto L_0x02ee
        L_0x0290:
            r26 = r13
            java.lang.String r15 = r14.toString()
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.media.Ringtone r13 = android.media.RingtoneManager.getRingtone(r13, r14)
            if (r13 == 0) goto L_0x02c0
            r34 = r15
            android.net.Uri r15 = android.provider.Settings.System.DEFAULT_RINGTONE_URI
            boolean r15 = r14.equals(r15)
            if (r15 == 0) goto L_0x02b4
            r15 = 2131625018(0x7f0e043a, float:1.8877232E38)
            r27 = r14
            java.lang.String r14 = "DefaultRingtone"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r14, r15)
            goto L_0x02bc
        L_0x02b4:
            r27 = r14
            android.content.Context r14 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r14 = r13.getTitle(r14)
        L_0x02bc:
            r13.stop()
            goto L_0x02c5
        L_0x02c0:
            r27 = r14
            r34 = r15
            r14 = 0
        L_0x02c5:
            if (r14 == 0) goto L_0x02ec
            if (r36 == 0) goto L_0x02da
            r13 = 2
            if (r4 != r13) goto L_0x02d0
            r6.putString(r10, r14)
            goto L_0x02ec
        L_0x02d0:
            if (r4 != 0) goto L_0x02d6
            r6.putString(r1, r14)
            goto L_0x02ec
        L_0x02d6:
            r6.putString(r2, r14)
            goto L_0x02ec
        L_0x02da:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            r6.putString(r1, r14)
        L_0x02ec:
            r15 = r34
        L_0x02ee:
            if (r36 == 0) goto L_0x0307
            r1 = 2
            if (r4 != r1) goto L_0x02f9
            java.lang.String r1 = "ChannelSoundPath"
            r6.putString(r1, r15)
            goto L_0x0324
        L_0x02f9:
            if (r4 != 0) goto L_0x0301
            java.lang.String r1 = "GroupSoundPath"
            r6.putString(r1, r15)
            goto L_0x0324
        L_0x0301:
            java.lang.String r1 = "GlobalSoundPath"
            r6.putString(r1, r15)
            goto L_0x0324
        L_0x0307:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "sound_path_"
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            r6.putString(r1, r15)
            goto L_0x0324
        L_0x031c:
            r24 = r7
            r26 = r13
            r27 = r14
            r25 = r15
        L_0x0324:
            r1 = r32
            r10 = 1
            goto L_0x0332
        L_0x0328:
            r24 = r7
            r26 = r13
            r25 = r15
            r1 = r32
            r27 = r34
        L_0x0332:
            boolean r2 = r0.isEmptyVibration(r1)
            r7 = 1
            r2 = r2 ^ r7
            r7 = r18
            if (r2 == r7) goto L_0x038d
            if (r37 != 0) goto L_0x0389
            if (r6 != 0) goto L_0x0344
            android.content.SharedPreferences$Editor r6 = r5.edit()
        L_0x0344:
            if (r36 == 0) goto L_0x036f
            r1 = 2
            if (r4 != r1) goto L_0x0355
            if (r7 == 0) goto L_0x034d
            r1 = 0
            goto L_0x034e
        L_0x034d:
            r1 = 2
        L_0x034e:
            java.lang.String r2 = "vibrate_channel"
            r6.putInt(r2, r1)
            goto L_0x0389
        L_0x0355:
            if (r4 != 0) goto L_0x0363
            if (r7 == 0) goto L_0x035b
            r1 = 0
            goto L_0x035c
        L_0x035b:
            r1 = 2
        L_0x035c:
            java.lang.String r2 = "vibrate_group"
            r6.putInt(r2, r1)
            goto L_0x0389
        L_0x0363:
            if (r7 == 0) goto L_0x0367
            r1 = 0
            goto L_0x0368
        L_0x0367:
            r1 = 2
        L_0x0368:
            java.lang.String r2 = "vibrate_messages"
            r6.putInt(r2, r1)
            goto L_0x0389
        L_0x036f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "vibrate_"
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            if (r7 == 0) goto L_0x0385
            r2 = 0
            goto L_0x0386
        L_0x0385:
            r2 = 2
        L_0x0386:
            r6.putInt(r1, r2)
        L_0x0389:
            r2 = r33
            r10 = 1
            goto L_0x0391
        L_0x038d:
            r2 = r33
            r24 = r1
        L_0x0391:
            if (r9 == r2) goto L_0x03ca
            if (r37 != 0) goto L_0x03c8
            if (r6 != 0) goto L_0x039b
            android.content.SharedPreferences$Editor r6 = r5.edit()
        L_0x039b:
            if (r36 == 0) goto L_0x03b4
            r1 = 2
            if (r4 != r1) goto L_0x03a6
            java.lang.String r1 = "ChannelLed"
            r6.putInt(r1, r9)
            goto L_0x03c8
        L_0x03a6:
            if (r4 != 0) goto L_0x03ae
            java.lang.String r1 = "GroupLed"
            r6.putInt(r1, r9)
            goto L_0x03c8
        L_0x03ae:
            java.lang.String r1 = "MessagesLed"
            r6.putInt(r1, r9)
            goto L_0x03c8
        L_0x03b4:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "color_"
            r1.append(r2)
            r1.append(r11)
            java.lang.String r1 = r1.toString()
            r6.putInt(r1, r9)
        L_0x03c8:
            r10 = 1
            goto L_0x03cb
        L_0x03ca:
            r9 = r2
        L_0x03cb:
            if (r6 == 0) goto L_0x03d0
            r6.commit()
        L_0x03d0:
            r2 = r10
            goto L_0x03e6
        L_0x03d2:
            r2 = r33
            r23 = r1
            r22 = r12
            r26 = r13
            r25 = r15
            r11 = r29
            r1 = r32
            r27 = r34
            r24 = r1
            r9 = r2
            r2 = 0
        L_0x03e6:
            r7 = r2
            r2 = r9
            r14 = r17
            r6 = r23
            r1 = r24
            r15 = r25
            r4 = r27
            goto L_0x0428
        L_0x03f3:
            r16 = r6
            r21 = r7
            r19 = r10
            r20 = r11
            r22 = r12
            r26 = r13
            r11 = r1
            r1 = r32
            r2 = r33
            r4 = r34
            r6 = 0
            r7 = 0
            r14 = 0
            r15 = 0
            goto L_0x0428
        L_0x040b:
            r16 = r6
            r21 = r7
            r19 = r10
            r20 = r11
            r22 = r12
            r26 = r13
            r17 = r14
            r25 = r15
            r11 = r1
            r1 = r32
            r2 = r33
            r4 = r34
            r14 = r17
            r15 = r25
            r6 = 0
            r7 = 0
        L_0x0428:
            if (r7 == 0) goto L_0x044f
            if (r6 == 0) goto L_0x044f
            android.content.SharedPreferences$Editor r7 = r5.edit()
            r9 = r21
            android.content.SharedPreferences$Editor r7 = r7.putString(r9, r14)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r9)
            r10 = r20
            r8.append(r10)
            java.lang.String r8 = r8.toString()
            android.content.SharedPreferences$Editor r7 = r7.putString(r8, r6)
            r7.commit()
            goto L_0x045a
        L_0x044f:
            r10 = r20
            r9 = r21
            if (r6 == 0) goto L_0x045d
            if (r37 == 0) goto L_0x045d
            if (r36 != 0) goto L_0x045a
            goto L_0x045d
        L_0x045a:
            r20 = r10
            goto L_0x04a1
        L_0x045d:
            r6 = 0
        L_0x045e:
            int r7 = r1.length
            if (r6 >= r7) goto L_0x046f
            r20 = r10
            r10 = r1[r6]
            r8.append(r10)
            int r6 = r6 + 1
            r11 = r29
            r10 = r20
            goto L_0x045e
        L_0x046f:
            r20 = r10
            r8.append(r2)
            if (r4 == 0) goto L_0x047d
            java.lang.String r6 = r4.toString()
            r8.append(r6)
        L_0x047d:
            r8.append(r3)
            if (r36 != 0) goto L_0x0489
            if (r26 == 0) goto L_0x0489
            r6 = r22
            r8.append(r6)
        L_0x0489:
            java.lang.String r6 = r8.toString()
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r6)
            if (r38 != 0) goto L_0x04a1
            if (r14 == 0) goto L_0x04a1
            boolean r7 = r15.equals(r6)
            if (r7 != 0) goto L_0x04a1
            android.app.NotificationManager r7 = systemNotificationManager
            r7.deleteNotificationChannel(r14)
            r14 = 0
        L_0x04a1:
            if (r14 != 0) goto L_0x056c
            java.lang.String r7 = "_"
            java.lang.String r8 = "channel_"
            if (r36 == 0) goto L_0x04ca
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            int r11 = r0.currentAccount
            r10.append(r11)
            r10.append(r8)
            r10.append(r9)
            r10.append(r7)
            java.security.SecureRandom r7 = org.telegram.messenger.Utilities.random
            long r7 = r7.nextLong()
            r10.append(r7)
            java.lang.String r7 = r10.toString()
            goto L_0x04ec
        L_0x04ca:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            int r11 = r0.currentAccount
            r10.append(r11)
            r10.append(r8)
            r11 = r29
            r10.append(r11)
            r10.append(r7)
            java.security.SecureRandom r7 = org.telegram.messenger.Utilities.random
            long r7 = r7.nextLong()
            r10.append(r7)
            java.lang.String r7 = r10.toString()
        L_0x04ec:
            r14 = r7
            android.app.NotificationChannel r7 = new android.app.NotificationChannel
            if (r26 == 0) goto L_0x04fb
            r8 = 2131627057(0x7f0e0CLASSNAME, float:1.8881368E38)
            java.lang.String r10 = "SecretChatName"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            goto L_0x04fd
        L_0x04fb:
            r8 = r16
        L_0x04fd:
            r7.<init>(r14, r8, r3)
            r10 = r19
            r7.setGroup(r10)
            if (r2 == 0) goto L_0x0510
            r3 = 1
            r7.enableLights(r3)
            r7.setLightColor(r2)
            r2 = 0
            goto L_0x0515
        L_0x0510:
            r2 = 0
            r3 = 1
            r7.enableLights(r2)
        L_0x0515:
            boolean r8 = r0.isEmptyVibration(r1)
            if (r8 != 0) goto L_0x0525
            r7.enableVibration(r3)
            int r2 = r1.length
            if (r2 <= 0) goto L_0x0528
            r7.setVibrationPattern(r1)
            goto L_0x0528
        L_0x0525:
            r7.enableVibration(r2)
        L_0x0528:
            android.media.AudioAttributes$Builder r1 = new android.media.AudioAttributes$Builder
            r1.<init>()
            r2 = 4
            r1.setContentType(r2)
            r2 = 5
            r1.setUsage(r2)
            if (r4 == 0) goto L_0x053f
            android.media.AudioAttributes r1 = r1.build()
            r7.setSound(r4, r1)
            goto L_0x0547
        L_0x053f:
            android.media.AudioAttributes r1 = r1.build()
            r2 = 0
            r7.setSound(r2, r1)
        L_0x0547:
            android.app.NotificationManager r1 = systemNotificationManager
            r1.createNotificationChannel(r7)
            android.content.SharedPreferences$Editor r1 = r5.edit()
            android.content.SharedPreferences$Editor r1 = r1.putString(r9, r14)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r9)
            r3 = r20
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.content.SharedPreferences$Editor r1 = r1.putString(r2, r6)
            r1.commit()
        L_0x056c:
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARNING: type inference failed for: r15v22 */
    /* JADX WARNING: type inference failed for: r15v23 */
    /* JADX WARNING: type inference failed for: r15v36 */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x07b4, code lost:
        if (r14 < 26) goto L_0x07b8;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:375:0x0860 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0201 A[SYNTHETIC, Splitter:B:103:0x0201] */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0273 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0323 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x03fc A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x03fe A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x0416 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0496 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x049e A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x04f6 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x0541 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0545 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x054f A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0552 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0557 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0576 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x05b0 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x05b5 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x05e6 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:283:0x0674 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:299:0x072e A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:314:0x0778  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0780  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x07bc A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x0872 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x087c A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x088a A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x08f7 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x09ac A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x09db A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x09f4 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x0a10 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x0a29 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c5 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ce A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d5 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00e4 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00e7 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00f3 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00fe A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0112 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0118 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x012e A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0145 A[SYNTHETIC, Splitter:B:80:0x0145] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0179 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0185 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x019d A[SYNTHETIC, Splitter:B:97:0x019d] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01b3 A[Catch:{ Exception -> 0x0a33 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r40) {
        /*
            r39 = this;
            r13 = r39
            java.lang.String r1 = "currentAccount"
            int r2 = android.os.Build.VERSION.SDK_INT
            org.telegram.messenger.UserConfig r0 = r39.getUserConfig()
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x0a38
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r13.pushMessages
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0a38
            boolean r0 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r0 != 0) goto L_0x0024
            int r0 = r13.currentAccount
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            if (r0 == r3) goto L_0x0024
            goto L_0x0a38
        L_0x0024:
            org.telegram.tgnet.ConnectionsManager r0 = r39.getConnectionsManager()     // Catch:{ Exception -> 0x0a33 }
            r0.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0a33 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r13.pushMessages     // Catch:{ Exception -> 0x0a33 }
            r3 = 0
            java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x0a33 }
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4     // Catch:{ Exception -> 0x0a33 }
            org.telegram.messenger.AccountInstance r0 = r39.getAccountInstance()     // Catch:{ Exception -> 0x0a33 }
            android.content.SharedPreferences r5 = r0.getNotificationsSettings()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r0 = "dismissDate"
            int r0 = r5.getInt(r0, r3)     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner     // Catch:{ Exception -> 0x0a33 }
            int r6 = r6.date     // Catch:{ Exception -> 0x0a33 }
            if (r6 > r0) goto L_0x004d
            r39.dismissNotification()     // Catch:{ Exception -> 0x0a33 }
            return
        L_0x004d:
            long r6 = r4.getDialogId()     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner     // Catch:{ Exception -> 0x0a33 }
            boolean r8 = r8.mentioned     // Catch:{ Exception -> 0x0a33 }
            if (r8 == 0) goto L_0x005d
            int r8 = r4.getFromChatId()     // Catch:{ Exception -> 0x0a33 }
            long r8 = (long) r8     // Catch:{ Exception -> 0x0a33 }
            goto L_0x005e
        L_0x005d:
            r8 = r6
        L_0x005e:
            r4.getId()     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$Message r10 = r4.messageOwner     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.peer_id     // Catch:{ Exception -> 0x0a33 }
            int r11 = r10.chat_id     // Catch:{ Exception -> 0x0a33 }
            if (r11 == 0) goto L_0x006a
            goto L_0x006c
        L_0x006a:
            int r11 = r10.channel_id     // Catch:{ Exception -> 0x0a33 }
        L_0x006c:
            int r10 = r10.user_id     // Catch:{ Exception -> 0x0a33 }
            boolean r12 = r4.isFromUser()     // Catch:{ Exception -> 0x0a33 }
            if (r12 == 0) goto L_0x0086
            if (r10 == 0) goto L_0x0080
            org.telegram.messenger.UserConfig r12 = r39.getUserConfig()     // Catch:{ Exception -> 0x0a33 }
            int r12 = r12.getClientUserId()     // Catch:{ Exception -> 0x0a33 }
            if (r10 != r12) goto L_0x0086
        L_0x0080:
            org.telegram.tgnet.TLRPC$Message r10 = r4.messageOwner     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.from_id     // Catch:{ Exception -> 0x0a33 }
            int r10 = r10.user_id     // Catch:{ Exception -> 0x0a33 }
        L_0x0086:
            org.telegram.messenger.MessagesController r12 = r39.getMessagesController()     // Catch:{ Exception -> 0x0a33 }
            java.lang.Integer r14 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$User r12 = r12.getUser(r14)     // Catch:{ Exception -> 0x0a33 }
            if (r11 == 0) goto L_0x00b7
            org.telegram.messenger.MessagesController r14 = r39.getMessagesController()     // Catch:{ Exception -> 0x0a33 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r11)     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$Chat r3 = r14.getChat(r3)     // Catch:{ Exception -> 0x0a33 }
            if (r3 != 0) goto L_0x00ab
            boolean r14 = r4.isFcmMessage()     // Catch:{ Exception -> 0x0a33 }
            if (r14 == 0) goto L_0x00ab
            boolean r14 = r4.localChannel     // Catch:{ Exception -> 0x0a33 }
            goto L_0x00b9
        L_0x00ab:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r3)     // Catch:{ Exception -> 0x0a33 }
            if (r14 == 0) goto L_0x00b8
            boolean r14 = r3.megagroup     // Catch:{ Exception -> 0x0a33 }
            if (r14 != 0) goto L_0x00b8
            r14 = 1
            goto L_0x00b9
        L_0x00b7:
            r3 = 0
        L_0x00b8:
            r14 = 0
        L_0x00b9:
            int r15 = r13.getNotifyOverride(r5, r8)     // Catch:{ Exception -> 0x0a33 }
            r17 = r1
            r1 = -1
            r18 = r10
            r10 = 2
            if (r15 != r1) goto L_0x00ce
            java.lang.Boolean r15 = java.lang.Boolean.valueOf(r14)     // Catch:{ Exception -> 0x0a33 }
            boolean r15 = r13.isGlobalNotificationsEnabled(r6, r15)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x00d3
        L_0x00ce:
            if (r15 == r10) goto L_0x00d2
            r15 = 1
            goto L_0x00d3
        L_0x00d2:
            r15 = 0
        L_0x00d3:
            if (r11 == 0) goto L_0x00d7
            if (r3 == 0) goto L_0x00d9
        L_0x00d7:
            if (r12 != 0) goto L_0x00e2
        L_0x00d9:
            boolean r19 = r4.isFcmMessage()     // Catch:{ Exception -> 0x0a33 }
            if (r19 == 0) goto L_0x00e2
            java.lang.String r1 = r4.localName     // Catch:{ Exception -> 0x0a33 }
            goto L_0x00eb
        L_0x00e2:
            if (r3 == 0) goto L_0x00e7
            java.lang.String r1 = r3.title     // Catch:{ Exception -> 0x0a33 }
            goto L_0x00eb
        L_0x00e7:
            java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r12)     // Catch:{ Exception -> 0x0a33 }
        L_0x00eb:
            r20 = r1
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a33 }
            if (r1 != 0) goto L_0x00fa
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a33 }
            if (r1 == 0) goto L_0x00f8
            goto L_0x00fa
        L_0x00f8:
            r1 = 0
            goto L_0x00fb
        L_0x00fa:
            r1 = 1
        L_0x00fb:
            int r10 = (int) r6     // Catch:{ Exception -> 0x0a33 }
            if (r10 == 0) goto L_0x0112
            r21 = r4
            android.util.LongSparseArray<java.lang.Integer> r4 = r13.pushDialogs     // Catch:{ Exception -> 0x0a33 }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0a33 }
            r22 = r12
            r12 = 1
            if (r4 > r12) goto L_0x0116
            if (r1 == 0) goto L_0x010e
            goto L_0x0116
        L_0x010e:
            r1 = r20
            r4 = 1
            goto L_0x0138
        L_0x0112:
            r21 = r4
            r22 = r12
        L_0x0116:
            if (r1 == 0) goto L_0x012e
            if (r11 == 0) goto L_0x0124
            java.lang.String r1 = "NotificationHiddenChatName"
            r4 = 2131626199(0x7f0e08d7, float:1.8879627E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0137
        L_0x0124:
            java.lang.String r1 = "NotificationHiddenName"
            r4 = 2131626202(0x7f0e08da, float:1.8879634E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0137
        L_0x012e:
            java.lang.String r1 = "AppName"
            r4 = 2131624263(0x7f0e0147, float:1.88757E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r4)     // Catch:{ Exception -> 0x0a33 }
        L_0x0137:
            r4 = 0
        L_0x0138:
            int r12 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0a33 }
            r23 = r10
            java.lang.String r10 = ""
            r24 = r14
            r14 = 1
            if (r12 <= r14) goto L_0x0179
            android.util.LongSparseArray<java.lang.Integer> r12 = r13.pushDialogs     // Catch:{ Exception -> 0x0a33 }
            int r12 = r12.size()     // Catch:{ Exception -> 0x0a33 }
            if (r12 != r14) goto L_0x015a
            org.telegram.messenger.UserConfig r12 = r39.getUserConfig()     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$User r12 = r12.getCurrentUser()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r12)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x017a
        L_0x015a:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r12.<init>()     // Catch:{ Exception -> 0x0a33 }
            org.telegram.messenger.UserConfig r14 = r39.getUserConfig()     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$User r14 = r14.getCurrentUser()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r14 = org.telegram.messenger.UserObject.getFirstName(r14)     // Catch:{ Exception -> 0x0a33 }
            r12.append(r14)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r14 = "・"
            r12.append(r14)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0a33 }
            goto L_0x017a
        L_0x0179:
            r12 = r10
        L_0x017a:
            android.util.LongSparseArray<java.lang.Integer> r14 = r13.pushDialogs     // Catch:{ Exception -> 0x0a33 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0a33 }
            r25 = r11
            r11 = 1
            if (r14 != r11) goto L_0x0190
            r11 = 23
            if (r2 >= r11) goto L_0x018a
            goto L_0x0190
        L_0x018a:
            r26 = r2
        L_0x018c:
            r27 = r5
        L_0x018e:
            r14 = r12
            goto L_0x01e9
        L_0x0190:
            android.util.LongSparseArray<java.lang.Integer> r11 = r13.pushDialogs     // Catch:{ Exception -> 0x0a33 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r14 = "NewMessages"
            r26 = r2
            r2 = 1
            if (r11 != r2) goto L_0x01b3
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r2.<init>()     // Catch:{ Exception -> 0x0a33 }
            r2.append(r12)     // Catch:{ Exception -> 0x0a33 }
            int r11 = r13.total_unread_count     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11)     // Catch:{ Exception -> 0x0a33 }
            r2.append(r11)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r12 = r2.toString()     // Catch:{ Exception -> 0x0a33 }
            goto L_0x018c
        L_0x01b3:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r2.<init>()     // Catch:{ Exception -> 0x0a33 }
            r2.append(r12)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r11 = "NotificationMessagesPeopleDisplayOrder"
            r27 = r5
            r12 = 2
            java.lang.Object[] r5 = new java.lang.Object[r12]     // Catch:{ Exception -> 0x0a33 }
            int r12 = r13.total_unread_count     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r14, r12)     // Catch:{ Exception -> 0x0a33 }
            r14 = 0
            r5[r14] = r12     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r12 = "FromChats"
            android.util.LongSparseArray<java.lang.Integer> r14 = r13.pushDialogs     // Catch:{ Exception -> 0x0a33 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r12 = org.telegram.messenger.LocaleController.formatPluralString(r12, r14)     // Catch:{ Exception -> 0x0a33 }
            r14 = 1
            r5[r14] = r12     // Catch:{ Exception -> 0x0a33 }
            r12 = 2131626250(0x7f0e090a, float:1.887973E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r11, r12, r5)     // Catch:{ Exception -> 0x0a33 }
            r2.append(r5)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r12 = r2.toString()     // Catch:{ Exception -> 0x0a33 }
            goto L_0x018e
        L_0x01e9:
            androidx.core.app.NotificationCompat$Builder r12 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            r12.<init>(r2)     // Catch:{ Exception -> 0x0a33 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r13.pushMessages     // Catch:{ Exception -> 0x0a33 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = ": "
            java.lang.String r11 = " "
            r28 = r6
            java.lang.String r6 = " @ "
            r7 = 1
            if (r2 != r7) goto L_0x0273
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r13.pushMessages     // Catch:{ Exception -> 0x0a33 }
            r2 = 0
            java.lang.Object r0 = r0.get(r2)     // Catch:{ Exception -> 0x0a33 }
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0     // Catch:{ Exception -> 0x0a33 }
            r30 = r8
            boolean[] r8 = new boolean[r7]     // Catch:{ Exception -> 0x0a33 }
            r7 = 0
            java.lang.String r9 = r13.getStringForMessage(r0, r2, r8, r7)     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x0a33 }
            boolean r0 = r0.silent     // Catch:{ Exception -> 0x0a33 }
            if (r9 != 0) goto L_0x021a
            return
        L_0x021a:
            if (r4 == 0) goto L_0x025f
            if (r3 == 0) goto L_0x0232
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r2.<init>()     // Catch:{ Exception -> 0x0a33 }
            r2.append(r6)     // Catch:{ Exception -> 0x0a33 }
            r2.append(r1)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r2 = r9.replace(r2, r10)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0260
        L_0x0232:
            r2 = 0
            boolean r4 = r8[r2]     // Catch:{ Exception -> 0x0a33 }
            if (r4 == 0) goto L_0x024b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r2.<init>()     // Catch:{ Exception -> 0x0a33 }
            r2.append(r1)     // Catch:{ Exception -> 0x0a33 }
            r2.append(r5)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r2 = r9.replace(r2, r10)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0260
        L_0x024b:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r2.<init>()     // Catch:{ Exception -> 0x0a33 }
            r2.append(r1)     // Catch:{ Exception -> 0x0a33 }
            r2.append(r11)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r2 = r9.replace(r2, r10)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0260
        L_0x025f:
            r2 = r9
        L_0x0260:
            r12.setContentText(r2)     // Catch:{ Exception -> 0x0a33 }
            androidx.core.app.NotificationCompat$BigTextStyle r4 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0a33 }
            r4.<init>()     // Catch:{ Exception -> 0x0a33 }
            r4.bigText(r2)     // Catch:{ Exception -> 0x0a33 }
            r12.setStyle(r4)     // Catch:{ Exception -> 0x0a33 }
            r33 = r15
            r15 = r0
            goto L_0x0321
        L_0x0273:
            r30 = r8
            r12.setContentText(r14)     // Catch:{ Exception -> 0x0a33 }
            androidx.core.app.NotificationCompat$InboxStyle r2 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0a33 }
            r2.<init>()     // Catch:{ Exception -> 0x0a33 }
            r2.setBigContentTitle(r1)     // Catch:{ Exception -> 0x0a33 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r13.pushMessages     // Catch:{ Exception -> 0x0a33 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0a33 }
            r8 = 10
            int r7 = java.lang.Math.min(r8, r7)     // Catch:{ Exception -> 0x0a33 }
            r8 = 1
            boolean[] r9 = new boolean[r8]     // Catch:{ Exception -> 0x0a33 }
            r33 = r15
            r8 = 0
            r15 = 2
            r32 = 0
        L_0x0295:
            if (r8 >= r7) goto L_0x0317
            r34 = r7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r13.pushMessages     // Catch:{ Exception -> 0x0a33 }
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0a33 }
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7     // Catch:{ Exception -> 0x0a33 }
            r37 = r8
            r35 = r12
            r36 = r14
            r12 = 0
            r14 = 0
            java.lang.String r8 = r13.getStringForMessage(r7, r14, r9, r12)     // Catch:{ Exception -> 0x0a33 }
            if (r8 == 0) goto L_0x030d
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner     // Catch:{ Exception -> 0x0a33 }
            int r12 = r7.date     // Catch:{ Exception -> 0x0a33 }
            if (r12 > r0) goto L_0x02b6
            goto L_0x030d
        L_0x02b6:
            r12 = 2
            if (r15 != r12) goto L_0x02bd
            boolean r15 = r7.silent     // Catch:{ Exception -> 0x0a33 }
            r32 = r8
        L_0x02bd:
            android.util.LongSparseArray<java.lang.Integer> r7 = r13.pushDialogs     // Catch:{ Exception -> 0x0a33 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0a33 }
            r12 = 1
            if (r7 != r12) goto L_0x030a
            if (r4 == 0) goto L_0x030a
            if (r3 == 0) goto L_0x02de
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r7.<init>()     // Catch:{ Exception -> 0x0a33 }
            r7.append(r6)     // Catch:{ Exception -> 0x0a33 }
            r7.append(r1)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r8 = r8.replace(r7, r10)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x030a
        L_0x02de:
            r7 = 0
            boolean r12 = r9[r7]     // Catch:{ Exception -> 0x0a33 }
            if (r12 == 0) goto L_0x02f7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r7.<init>()     // Catch:{ Exception -> 0x0a33 }
            r7.append(r1)     // Catch:{ Exception -> 0x0a33 }
            r7.append(r5)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r8 = r8.replace(r7, r10)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x030a
        L_0x02f7:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r7.<init>()     // Catch:{ Exception -> 0x0a33 }
            r7.append(r1)     // Catch:{ Exception -> 0x0a33 }
            r7.append(r11)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r8 = r8.replace(r7, r10)     // Catch:{ Exception -> 0x0a33 }
        L_0x030a:
            r2.addLine(r8)     // Catch:{ Exception -> 0x0a33 }
        L_0x030d:
            int r8 = r37 + 1
            r7 = r34
            r12 = r35
            r14 = r36
            goto L_0x0295
        L_0x0317:
            r35 = r12
            r2.setSummaryText(r14)     // Catch:{ Exception -> 0x0a33 }
            r12.setStyle(r2)     // Catch:{ Exception -> 0x0a33 }
            r9 = r32
        L_0x0321:
            if (r40 == 0) goto L_0x0335
            if (r33 == 0) goto L_0x0335
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0a33 }
            boolean r0 = r0.isRecordingAudio()     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x0335
            r2 = 1
            if (r15 != r2) goto L_0x0333
            goto L_0x0335
        L_0x0333:
            r0 = 0
            goto L_0x0336
        L_0x0335:
            r0 = 1
        L_0x0336:
            java.lang.String r2 = "custom_"
            if (r0 != 0) goto L_0x03e9
            int r6 = (r28 > r30 ? 1 : (r28 == r30 ? 0 : -1))
            if (r6 != 0) goto L_0x03e9
            if (r3 == 0) goto L_0x03e9
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r6.<init>()     // Catch:{ Exception -> 0x0a33 }
            r6.append(r2)     // Catch:{ Exception -> 0x0a33 }
            r7 = r28
            r6.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0a33 }
            r11 = r27
            r4 = 0
            boolean r5 = r11.getBoolean(r6, r4)     // Catch:{ Exception -> 0x0a33 }
            if (r5 == 0) goto L_0x0388
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r4.<init>()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = "smart_max_count_"
            r4.append(r5)     // Catch:{ Exception -> 0x0a33 }
            r4.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a33 }
            r5 = 2
            int r4 = r11.getInt(r4, r5)     // Catch:{ Exception -> 0x0a33 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r5.<init>()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r6 = "smart_delay_"
            r5.append(r6)     // Catch:{ Exception -> 0x0a33 }
            r5.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a33 }
            r6 = 180(0xb4, float:2.52E-43)
            int r5 = r11.getInt(r5, r6)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x038b
        L_0x0388:
            r5 = 180(0xb4, float:2.52E-43)
            r4 = 2
        L_0x038b:
            if (r4 == 0) goto L_0x03e3
            android.util.LongSparseArray<android.graphics.Point> r6 = r13.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a33 }
            java.lang.Object r6 = r6.get(r7)     // Catch:{ Exception -> 0x0a33 }
            android.graphics.Point r6 = (android.graphics.Point) r6     // Catch:{ Exception -> 0x0a33 }
            if (r6 != 0) goto L_0x03ac
            android.graphics.Point r4 = new android.graphics.Point     // Catch:{ Exception -> 0x0a33 }
            long r5 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a33 }
            r27 = 1000(0x3e8, double:4.94E-321)
            long r5 = r5 / r27
            int r6 = (int) r5     // Catch:{ Exception -> 0x0a33 }
            r5 = 1
            r4.<init>(r5, r6)     // Catch:{ Exception -> 0x0a33 }
            android.util.LongSparseArray<android.graphics.Point> r5 = r13.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a33 }
            r5.put(r7, r4)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x03e3
        L_0x03ac:
            r29 = r0
            int r0 = r6.y     // Catch:{ Exception -> 0x0a33 }
            int r0 = r0 + r5
            r30 = r9
            r5 = r10
            long r9 = (long) r0     // Catch:{ Exception -> 0x0a33 }
            long r31 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a33 }
            r27 = 1000(0x3e8, double:4.94E-321)
            long r31 = r31 / r27
            int r0 = (r9 > r31 ? 1 : (r9 == r31 ? 0 : -1))
            if (r0 >= 0) goto L_0x03cd
            long r9 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a33 }
            long r9 = r9 / r27
            int r0 = (int) r9     // Catch:{ Exception -> 0x0a33 }
            r4 = 1
            r6.set(r4, r0)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x03f2
        L_0x03cd:
            int r0 = r6.x     // Catch:{ Exception -> 0x0a33 }
            if (r0 >= r4) goto L_0x03e0
            r4 = 1
            int r0 = r0 + r4
            long r9 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a33 }
            r27 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 / r27
            int r4 = (int) r9     // Catch:{ Exception -> 0x0a33 }
            r6.set(r0, r4)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x03f2
        L_0x03e0:
            r29 = 1
            goto L_0x03f2
        L_0x03e3:
            r29 = r0
            r30 = r9
            r5 = r10
            goto L_0x03f2
        L_0x03e9:
            r30 = r9
            r5 = r10
            r11 = r27
            r7 = r28
            r29 = r0
        L_0x03f2:
            android.net.Uri r0 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r4 = r0.getPath()     // Catch:{ Exception -> 0x0a33 }
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x03fe
            r10 = 1
            goto L_0x03ff
        L_0x03fe:
            r10 = 0
        L_0x03ff:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r0.<init>()     // Catch:{ Exception -> 0x0a33 }
            r0.append(r2)     // Catch:{ Exception -> 0x0a33 }
            r0.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a33 }
            r2 = 0
            boolean r0 = r11.getBoolean(r0, r2)     // Catch:{ Exception -> 0x0a33 }
            r2 = 3
            if (r0 == 0) goto L_0x0496
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r0.<init>()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r6 = "vibrate_"
            r0.append(r6)     // Catch:{ Exception -> 0x0a33 }
            r0.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a33 }
            r6 = 0
            int r0 = r11.getInt(r0, r6)     // Catch:{ Exception -> 0x0a33 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r6.<init>()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r9 = "priority_"
            r6.append(r9)     // Catch:{ Exception -> 0x0a33 }
            r6.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0a33 }
            int r6 = r11.getInt(r6, r2)     // Catch:{ Exception -> 0x0a33 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r9.<init>()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r2 = "sound_path_"
            r9.append(r2)     // Catch:{ Exception -> 0x0a33 }
            r9.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r2 = r9.toString()     // Catch:{ Exception -> 0x0a33 }
            r9 = 0
            java.lang.String r2 = r11.getString(r2, r9)     // Catch:{ Exception -> 0x0a33 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r9.<init>()     // Catch:{ Exception -> 0x0a33 }
            r32 = r0
            java.lang.String r0 = "color_"
            r9.append(r0)     // Catch:{ Exception -> 0x0a33 }
            r9.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r0 = r9.toString()     // Catch:{ Exception -> 0x0a33 }
            boolean r0 = r11.contains(r0)     // Catch:{ Exception -> 0x0a33 }
            if (r0 == 0) goto L_0x0490
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r0.<init>()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r9 = "color_"
            r0.append(r9)     // Catch:{ Exception -> 0x0a33 }
            r0.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a33 }
            r9 = 0
            int r0 = r11.getInt(r0, r9)     // Catch:{ Exception -> 0x0a33 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x0a33 }
            r36 = r14
            r9 = r32
            goto L_0x049c
        L_0x0490:
            r36 = r14
            r9 = r32
            r0 = 0
            goto L_0x049c
        L_0x0496:
            r36 = r14
            r0 = 0
            r2 = 0
            r6 = 3
            r9 = 0
        L_0x049c:
            if (r25 == 0) goto L_0x04f6
            if (r24 == 0) goto L_0x04cb
            java.lang.String r14 = "ChannelSoundPath"
            java.lang.String r14 = r11.getString(r14, r4)     // Catch:{ Exception -> 0x0a33 }
            r32 = r14
            java.lang.String r14 = "vibrate_channel"
            r33 = r5
            r5 = 0
            int r14 = r11.getInt(r14, r5)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = "priority_channel"
            r34 = r14
            r14 = 1
            int r5 = r11.getInt(r5, r14)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r14 = "ChannelLed"
            r35 = r5
            r5 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r14 = r11.getInt(r14, r5)     // Catch:{ Exception -> 0x0a33 }
            r5 = r32
            r24 = 2
            goto L_0x0522
        L_0x04cb:
            r33 = r5
            java.lang.String r5 = "GroupSoundPath"
            java.lang.String r5 = r11.getString(r5, r4)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r14 = "vibrate_group"
            r32 = r5
            r5 = 0
            int r14 = r11.getInt(r14, r5)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = "priority_group"
            r34 = r14
            r14 = 1
            int r5 = r11.getInt(r5, r14)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r14 = "GroupLed"
            r35 = r5
            r5 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r14 = r11.getInt(r14, r5)     // Catch:{ Exception -> 0x0a33 }
            r5 = r32
            r24 = 0
            goto L_0x0522
        L_0x04f6:
            r33 = r5
            if (r18 == 0) goto L_0x052d
            java.lang.String r5 = "GlobalSoundPath"
            java.lang.String r5 = r11.getString(r5, r4)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r14 = "vibrate_messages"
            r32 = r5
            r5 = 0
            int r14 = r11.getInt(r14, r5)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = "priority_messages"
            r34 = r14
            r14 = 1
            int r5 = r11.getInt(r5, r14)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r14 = "MessagesLed"
            r35 = r5
            r5 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r14 = r11.getInt(r14, r5)     // Catch:{ Exception -> 0x0a33 }
            r5 = r32
            r24 = 1
        L_0x0522:
            r32 = r4
            r4 = r34
            r34 = r15
            r15 = r35
            r35 = r1
            goto L_0x053e
        L_0x052d:
            r5 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r35 = r1
            r32 = r4
            r34 = r15
            r4 = 0
            r5 = 0
            r14 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r15 = 0
            r24 = 1
        L_0x053e:
            r1 = 4
            if (r4 != r1) goto L_0x0545
            r4 = 0
            r37 = 1
            goto L_0x0547
        L_0x0545:
            r37 = 0
        L_0x0547:
            if (r2 == 0) goto L_0x0552
            boolean r38 = android.text.TextUtils.equals(r5, r2)     // Catch:{ Exception -> 0x0a33 }
            if (r38 != 0) goto L_0x0552
            r1 = 3
            r5 = 0
            goto L_0x0555
        L_0x0552:
            r2 = r5
            r1 = 3
            r5 = 1
        L_0x0555:
            if (r6 == r1) goto L_0x055b
            if (r15 == r6) goto L_0x055b
            r5 = 0
            goto L_0x055c
        L_0x055b:
            r6 = r15
        L_0x055c:
            if (r0 == 0) goto L_0x0569
            int r1 = r0.intValue()     // Catch:{ Exception -> 0x0a33 }
            if (r1 == r14) goto L_0x0569
            int r14 = r0.intValue()     // Catch:{ Exception -> 0x0a33 }
            r5 = 0
        L_0x0569:
            if (r9 == 0) goto L_0x0573
            r1 = 4
            if (r9 == r1) goto L_0x0573
            if (r9 == r4) goto L_0x0573
            r4 = r9
            r9 = 0
            goto L_0x0574
        L_0x0573:
            r9 = r5
        L_0x0574:
            if (r10 == 0) goto L_0x0598
            java.lang.String r0 = "EnableInAppSounds"
            r1 = 1
            boolean r0 = r11.getBoolean(r0, r1)     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x0580
            r2 = 0
        L_0x0580:
            java.lang.String r0 = "EnableInAppVibrate"
            boolean r0 = r11.getBoolean(r0, r1)     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x0589
            r4 = 2
        L_0x0589:
            java.lang.String r0 = "EnableInAppPriority"
            r1 = 0
            boolean r0 = r11.getBoolean(r0, r1)     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x0594
            r6 = 0
            goto L_0x0598
        L_0x0594:
            r1 = 2
            if (r6 != r1) goto L_0x0598
            r6 = 1
        L_0x0598:
            if (r37 == 0) goto L_0x05ae
            r1 = 2
            if (r4 == r1) goto L_0x05ae
            android.media.AudioManager r0 = audioManager     // Catch:{ Exception -> 0x05aa }
            int r0 = r0.getRingerMode()     // Catch:{ Exception -> 0x05aa }
            if (r0 == 0) goto L_0x05ae
            r1 = 1
            if (r0 == r1) goto L_0x05ae
            r4 = 2
            goto L_0x05ae
        L_0x05aa:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0a33 }
        L_0x05ae:
            if (r29 == 0) goto L_0x05b5
            r0 = 0
            r2 = 0
            r4 = 0
            r6 = 0
            goto L_0x05b7
        L_0x05b5:
            r0 = r6
            r6 = r14
        L_0x05b7:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r14 = org.telegram.ui.LaunchActivity.class
            r1.<init>(r5, r14)     // Catch:{ Exception -> 0x0a33 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r5.<init>()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r14 = "com.tmessages.openchat"
            r5.append(r14)     // Catch:{ Exception -> 0x0a33 }
            double r14 = java.lang.Math.random()     // Catch:{ Exception -> 0x0a33 }
            r5.append(r14)     // Catch:{ Exception -> 0x0a33 }
            r14 = 2147483647(0x7fffffff, float:NaN)
            r5.append(r14)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a33 }
            r1.setAction(r5)     // Catch:{ Exception -> 0x0a33 }
            r5 = 32768(0x8000, float:4.5918E-41)
            r1.setFlags(r5)     // Catch:{ Exception -> 0x0a33 }
            if (r23 == 0) goto L_0x0674
            android.util.LongSparseArray<java.lang.Integer> r5 = r13.pushDialogs     // Catch:{ Exception -> 0x0a33 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0a33 }
            r14 = 1
            if (r5 != r14) goto L_0x0603
            if (r25 == 0) goto L_0x05f9
            java.lang.String r5 = "chatId"
            r14 = r25
            r1.putExtra(r5, r14)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0603
        L_0x05f9:
            if (r18 == 0) goto L_0x0603
            java.lang.String r5 = "userId"
            r14 = r18
            r1.putExtra(r5, r14)     // Catch:{ Exception -> 0x0a33 }
        L_0x0603:
            boolean r5 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a33 }
            if (r5 != 0) goto L_0x066c
            boolean r5 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a33 }
            if (r5 == 0) goto L_0x060f
            goto L_0x066c
        L_0x060f:
            android.util.LongSparseArray<java.lang.Integer> r5 = r13.pushDialogs     // Catch:{ Exception -> 0x0a33 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0a33 }
            r14 = 1
            if (r5 != r14) goto L_0x0662
            r5 = 28
            r14 = r26
            if (r14 >= r5) goto L_0x065c
            if (r3 == 0) goto L_0x063e
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r3.photo     // Catch:{ Exception -> 0x0a33 }
            if (r5 == 0) goto L_0x065c
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ Exception -> 0x0a33 }
            if (r5 == 0) goto L_0x065c
            r18 = r9
            r15 = r10
            long r9 = r5.volume_id     // Catch:{ Exception -> 0x0a33 }
            r25 = 0
            int r23 = (r9 > r25 ? 1 : (r9 == r25 ? 0 : -1))
            if (r23 == 0) goto L_0x065f
            int r9 = r5.local_id     // Catch:{ Exception -> 0x0a33 }
            if (r9 == 0) goto L_0x065f
            r9 = r5
            r5 = r22
            r22 = r11
            goto L_0x0697
        L_0x063e:
            r18 = r9
            r15 = r10
            if (r22 == 0) goto L_0x065f
            r5 = r22
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r5.photo     // Catch:{ Exception -> 0x0a33 }
            if (r9 == 0) goto L_0x0669
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small     // Catch:{ Exception -> 0x0a33 }
            if (r9 == 0) goto L_0x0669
            r22 = r11
            long r10 = r9.volume_id     // Catch:{ Exception -> 0x0a33 }
            r25 = 0
            int r23 = (r10 > r25 ? 1 : (r10 == r25 ? 0 : -1))
            if (r23 == 0) goto L_0x0696
            int r10 = r9.local_id     // Catch:{ Exception -> 0x0a33 }
            if (r10 == 0) goto L_0x0696
            goto L_0x0697
        L_0x065c:
            r18 = r9
            r15 = r10
        L_0x065f:
            r5 = r22
            goto L_0x0669
        L_0x0662:
            r18 = r9
            r15 = r10
            r5 = r22
            r14 = r26
        L_0x0669:
            r22 = r11
            goto L_0x0696
        L_0x066c:
            r18 = r9
            r15 = r10
            r5 = r22
            r14 = r26
            goto L_0x0669
        L_0x0674:
            r18 = r9
            r15 = r10
            r5 = r22
            r14 = r26
            r22 = r11
            android.util.LongSparseArray<java.lang.Integer> r9 = r13.pushDialogs     // Catch:{ Exception -> 0x0a33 }
            int r9 = r9.size()     // Catch:{ Exception -> 0x0a33 }
            r10 = 1
            if (r9 != r10) goto L_0x0696
            long r9 = globalSecretChatId     // Catch:{ Exception -> 0x0a33 }
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 == 0) goto L_0x0696
            java.lang.String r9 = "encId"
            r10 = 32
            long r10 = r7 >> r10
            int r11 = (int) r10     // Catch:{ Exception -> 0x0a33 }
            r1.putExtra(r9, r11)     // Catch:{ Exception -> 0x0a33 }
        L_0x0696:
            r9 = 0
        L_0x0697:
            int r10 = r13.currentAccount     // Catch:{ Exception -> 0x0a33 }
            r11 = r17
            r1.putExtra(r11, r10)     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            r25 = r7
            r7 = 1073741824(0x40000000, float:2.0)
            r8 = 0
            android.app.PendingIntent r1 = android.app.PendingIntent.getActivity(r10, r8, r1, r7)     // Catch:{ Exception -> 0x0a33 }
            r7 = r35
            r12.setContentTitle(r7)     // Catch:{ Exception -> 0x0a33 }
            r7 = 2131165832(0x7var_, float:1.7945892E38)
            r12.setSmallIcon(r7)     // Catch:{ Exception -> 0x0a33 }
            r7 = 1
            r12.setAutoCancel(r7)     // Catch:{ Exception -> 0x0a33 }
            int r7 = r13.total_unread_count     // Catch:{ Exception -> 0x0a33 }
            r12.setNumber(r7)     // Catch:{ Exception -> 0x0a33 }
            r12.setContentIntent(r1)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r1 = r13.notificationGroup     // Catch:{ Exception -> 0x0a33 }
            r12.setGroup(r1)     // Catch:{ Exception -> 0x0a33 }
            r1 = 1
            r12.setGroupSummary(r1)     // Catch:{ Exception -> 0x0a33 }
            r12.setShowWhen(r1)     // Catch:{ Exception -> 0x0a33 }
            r1 = r21
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner     // Catch:{ Exception -> 0x0a33 }
            int r7 = r7.date     // Catch:{ Exception -> 0x0a33 }
            long r7 = (long) r7     // Catch:{ Exception -> 0x0a33 }
            r27 = 1000(0x3e8, double:4.94E-321)
            long r7 = r7 * r27
            r12.setWhen(r7)     // Catch:{ Exception -> 0x0a33 }
            r7 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r12.setColor(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r7 = "msg"
            r12.setCategory(r7)     // Catch:{ Exception -> 0x0a33 }
            if (r3 != 0) goto L_0x0709
            if (r5 == 0) goto L_0x0709
            java.lang.String r3 = r5.phone     // Catch:{ Exception -> 0x0a33 }
            if (r3 == 0) goto L_0x0709
            int r3 = r3.length()     // Catch:{ Exception -> 0x0a33 }
            if (r3 <= 0) goto L_0x0709
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r3.<init>()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r7 = "tel:+"
            r3.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = r5.phone     // Catch:{ Exception -> 0x0a33 }
            r3.append(r5)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0a33 }
            r12.addPerson(r3)     // Catch:{ Exception -> 0x0a33 }
        L_0x0709:
            android.content.Intent r3 = new android.content.Intent     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r7 = org.telegram.messenger.NotificationDismissReceiver.class
            r3.<init>(r5, r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = "messageDate"
            org.telegram.tgnet.TLRPC$Message r7 = r1.messageOwner     // Catch:{ Exception -> 0x0a33 }
            int r7 = r7.date     // Catch:{ Exception -> 0x0a33 }
            r3.putExtra(r5, r7)     // Catch:{ Exception -> 0x0a33 }
            int r5 = r13.currentAccount     // Catch:{ Exception -> 0x0a33 }
            r3.putExtra(r11, r5)     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            r7 = 134217728(0x8000000, float:3.85186E-34)
            r8 = 1
            android.app.PendingIntent r3 = android.app.PendingIntent.getBroadcast(r5, r8, r3, r7)     // Catch:{ Exception -> 0x0a33 }
            r12.setDeleteIntent(r3)     // Catch:{ Exception -> 0x0a33 }
            if (r9 == 0) goto L_0x0778
            org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r5 = "50_50"
            r8 = 0
            android.graphics.drawable.BitmapDrawable r3 = r3.getImageFromMemory(r9, r8, r5)     // Catch:{ Exception -> 0x0a33 }
            if (r3 == 0) goto L_0x0743
            android.graphics.Bitmap r3 = r3.getBitmap()     // Catch:{ Exception -> 0x0a33 }
            r12.setLargeIcon(r3)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0779
        L_0x0743:
            r3 = 1
            java.io.File r5 = org.telegram.messenger.FileLoader.getPathToAttach(r9, r3)     // Catch:{ all -> 0x0776 }
            boolean r3 = r5.exists()     // Catch:{ all -> 0x0776 }
            if (r3 == 0) goto L_0x0779
            r3 = 1126170624(0x43200000, float:160.0)
            r9 = 1112014848(0x42480000, float:50.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)     // Catch:{ all -> 0x0776 }
            float r9 = (float) r9     // Catch:{ all -> 0x0776 }
            float r3 = r3 / r9
            android.graphics.BitmapFactory$Options r9 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0776 }
            r9.<init>()     // Catch:{ all -> 0x0776 }
            r10 = 1065353216(0x3var_, float:1.0)
            int r10 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r10 >= 0) goto L_0x0765
            r3 = 1
            goto L_0x0766
        L_0x0765:
            int r3 = (int) r3     // Catch:{ all -> 0x0776 }
        L_0x0766:
            r9.inSampleSize = r3     // Catch:{ all -> 0x0776 }
            java.lang.String r3 = r5.getAbsolutePath()     // Catch:{ all -> 0x0776 }
            android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeFile(r3, r9)     // Catch:{ all -> 0x0776 }
            if (r3 == 0) goto L_0x0779
            r12.setLargeIcon(r3)     // Catch:{ all -> 0x0776 }
            goto L_0x0779
        L_0x0776:
            goto L_0x0779
        L_0x0778:
            r8 = 0
        L_0x0779:
            r3 = 5
            r5 = 26
            r9 = r34
            if (r40 == 0) goto L_0x07b0
            r10 = 1
            if (r9 != r10) goto L_0x0784
            goto L_0x07b0
        L_0x0784:
            if (r0 != 0) goto L_0x078e
            r8 = 0
            r12.setPriority(r8)     // Catch:{ Exception -> 0x0a33 }
            if (r14 < r5) goto L_0x07b8
            r8 = 3
            goto L_0x07ba
        L_0x078e:
            if (r0 == r10) goto L_0x07a8
            r8 = 2
            if (r0 != r8) goto L_0x0794
            goto L_0x07a8
        L_0x0794:
            r8 = 4
            if (r0 != r8) goto L_0x079f
            r0 = -2
            r12.setPriority(r0)     // Catch:{ Exception -> 0x0a33 }
            if (r14 < r5) goto L_0x07b8
            r8 = 1
            goto L_0x07b9
        L_0x079f:
            if (r0 != r3) goto L_0x07b8
            r8 = -1
            r12.setPriority(r8)     // Catch:{ Exception -> 0x0a33 }
            if (r14 < r5) goto L_0x07b8
            goto L_0x07b6
        L_0x07a8:
            r8 = 1
            r12.setPriority(r8)     // Catch:{ Exception -> 0x0a33 }
            if (r14 < r5) goto L_0x07b8
            r8 = 4
            goto L_0x07b9
        L_0x07b0:
            r8 = -1
            r12.setPriority(r8)     // Catch:{ Exception -> 0x0a33 }
            if (r14 < r5) goto L_0x07b8
        L_0x07b6:
            r8 = 2
            goto L_0x07b9
        L_0x07b8:
            r8 = 0
        L_0x07b9:
            r10 = 1
        L_0x07ba:
            if (r9 == r10) goto L_0x08cc
            if (r29 != 0) goto L_0x08cc
            if (r15 == 0) goto L_0x07ca
            java.lang.String r0 = "EnableInAppPreview"
            r9 = r22
            boolean r0 = r9.getBoolean(r0, r10)     // Catch:{ Exception -> 0x0a33 }
            if (r0 == 0) goto L_0x07ff
        L_0x07ca:
            int r0 = r30.length()     // Catch:{ Exception -> 0x0a33 }
            r9 = 100
            if (r0 <= r9) goto L_0x07f9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a33 }
            r0.<init>()     // Catch:{ Exception -> 0x0a33 }
            r9 = 100
            r10 = r30
            r7 = 0
            java.lang.String r9 = r10.substring(r7, r9)     // Catch:{ Exception -> 0x0a33 }
            r7 = 32
            r10 = 10
            java.lang.String r7 = r9.replace(r10, r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r7 = r7.trim()     // Catch:{ Exception -> 0x0a33 }
            r0.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r7 = "..."
            r0.append(r7)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r9 = r0.toString()     // Catch:{ Exception -> 0x0a33 }
            goto L_0x07fc
        L_0x07f9:
            r10 = r30
            r9 = r10
        L_0x07fc:
            r12.setTicker(r9)     // Catch:{ Exception -> 0x0a33 }
        L_0x07ff:
            if (r2 == 0) goto L_0x086f
            java.lang.String r0 = "NoSound"
            boolean r0 = r2.equals(r0)     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x086f
            if (r14 < r5) goto L_0x081b
            r7 = r32
            boolean r0 = r2.equals(r7)     // Catch:{ Exception -> 0x0a33 }
            if (r0 == 0) goto L_0x0816
            android.net.Uri r7 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0870
        L_0x0816:
            android.net.Uri r7 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0870
        L_0x081b:
            r7 = r32
            boolean r0 = r2.equals(r7)     // Catch:{ Exception -> 0x0a33 }
            if (r0 == 0) goto L_0x0829
            android.net.Uri r0 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a33 }
            r12.setSound(r0, r3)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x086f
        L_0x0829:
            r0 = 24
            if (r14 < r0) goto L_0x0868
            java.lang.String r0 = "file://"
            boolean r0 = r2.startsWith(r0)     // Catch:{ Exception -> 0x0a33 }
            if (r0 == 0) goto L_0x0868
            android.net.Uri r0 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0a33 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x0868
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0860 }
            java.lang.String r7 = "org.telegram.messenger.provider"
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x0860 }
            java.lang.String r10 = "file://"
            r5 = r33
            java.lang.String r5 = r2.replace(r10, r5)     // Catch:{ Exception -> 0x0860 }
            r9.<init>(r5)     // Catch:{ Exception -> 0x0860 }
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r0, r7, r9)     // Catch:{ Exception -> 0x0860 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0860 }
            java.lang.String r7 = "com.android.systemui"
            r9 = 1
            r5.grantUriPermission(r7, r0, r9)     // Catch:{ Exception -> 0x0860 }
            r12.setSound(r0, r3)     // Catch:{ Exception -> 0x0860 }
            goto L_0x086f
        L_0x0860:
            android.net.Uri r0 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0a33 }
            r12.setSound(r0, r3)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x086f
        L_0x0868:
            android.net.Uri r0 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0a33 }
            r12.setSound(r0, r3)     // Catch:{ Exception -> 0x0a33 }
        L_0x086f:
            r7 = 0
        L_0x0870:
            if (r6 == 0) goto L_0x0879
            r0 = 1000(0x3e8, float:1.401E-42)
            r2 = 1000(0x3e8, float:1.401E-42)
            r12.setLights(r6, r0, r2)     // Catch:{ Exception -> 0x0a33 }
        L_0x0879:
            r2 = 2
            if (r4 != r2) goto L_0x088a
            long[] r0 = new long[r2]     // Catch:{ Exception -> 0x0a33 }
            r2 = 0
            r4 = 0
            r0[r4] = r2     // Catch:{ Exception -> 0x0a33 }
            r4 = 1
            r0[r4] = r2     // Catch:{ Exception -> 0x0a33 }
            r12.setVibrate(r0)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x08c9
        L_0x088a:
            r2 = 1
            if (r4 != r2) goto L_0x08a5
            r3 = 4
            long[] r0 = new long[r3]     // Catch:{ Exception -> 0x0a33 }
            r3 = 0
            r5 = 0
            r0[r5] = r3     // Catch:{ Exception -> 0x0a33 }
            r9 = 100
            r0[r2] = r9     // Catch:{ Exception -> 0x0a33 }
            r2 = 2
            r0[r2] = r3     // Catch:{ Exception -> 0x0a33 }
            r2 = 100
            r4 = 3
            r0[r4] = r2     // Catch:{ Exception -> 0x0a33 }
            r12.setVibrate(r0)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x08c9
        L_0x08a5:
            if (r4 == 0) goto L_0x08c2
            r2 = 4
            if (r4 != r2) goto L_0x08ab
            goto L_0x08c2
        L_0x08ab:
            r2 = 3
            if (r4 != r2) goto L_0x08bf
            r2 = 2
            long[] r0 = new long[r2]     // Catch:{ Exception -> 0x0a33 }
            r2 = 0
            r4 = 0
            r0[r4] = r2     // Catch:{ Exception -> 0x0a33 }
            r2 = 1000(0x3e8, double:4.94E-321)
            r4 = 1
            r0[r4] = r2     // Catch:{ Exception -> 0x0a33 }
            r12.setVibrate(r0)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x08c9
        L_0x08bf:
            r4 = 1
            r5 = 0
            goto L_0x08dc
        L_0x08c2:
            r2 = 2
            r12.setDefaults(r2)     // Catch:{ Exception -> 0x0a33 }
            r2 = 0
            long[] r0 = new long[r2]     // Catch:{ Exception -> 0x0a33 }
        L_0x08c9:
            r5 = r0
            r4 = 1
            goto L_0x08dc
        L_0x08cc:
            r2 = 2
            long[] r0 = new long[r2]     // Catch:{ Exception -> 0x0a33 }
            r2 = 0
            r4 = 0
            r0[r4] = r2     // Catch:{ Exception -> 0x0a33 }
            r4 = 1
            r0[r4] = r2     // Catch:{ Exception -> 0x0a33 }
            r12.setVibrate(r0)     // Catch:{ Exception -> 0x0a33 }
            r5 = r0
            r7 = 0
        L_0x08dc:
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x09ac
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x09ac
            long r2 = r1.getDialogId()     // Catch:{ Exception -> 0x0a33 }
            r9 = 777000(0xbdb28, double:3.83889E-318)
            int r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x09ac
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup     // Catch:{ Exception -> 0x0a33 }
            if (r0 == 0) goto L_0x09ac
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows     // Catch:{ Exception -> 0x0a33 }
            int r2 = r0.size()     // Catch:{ Exception -> 0x0a33 }
            r3 = 0
            r9 = 0
        L_0x08ff:
            if (r3 >= r2) goto L_0x09a3
            java.lang.Object r10 = r0.get(r3)     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r10 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r10     // Catch:{ Exception -> 0x0a33 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r4 = r10.buttons     // Catch:{ Exception -> 0x0a33 }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0a33 }
            r16 = r0
            r0 = 0
        L_0x0910:
            if (r0 >= r4) goto L_0x0989
            r40 = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r2 = r10.buttons     // Catch:{ Exception -> 0x0a33 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ Exception -> 0x0a33 }
            org.telegram.tgnet.TLRPC$KeyboardButton r2 = (org.telegram.tgnet.TLRPC$KeyboardButton) r2     // Catch:{ Exception -> 0x0a33 }
            r21 = r4
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0a33 }
            if (r4 == 0) goto L_0x096b
            android.content.Intent r4 = new android.content.Intent     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            r22 = r10
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r10 = org.telegram.messenger.NotificationCallbackReceiver.class
            r4.<init>(r9, r10)     // Catch:{ Exception -> 0x0a33 }
            int r9 = r13.currentAccount     // Catch:{ Exception -> 0x0a33 }
            r4.putExtra(r11, r9)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r9 = "did"
            r23 = r7
            r10 = r8
            r7 = r25
            r4.putExtra(r9, r7)     // Catch:{ Exception -> 0x0a33 }
            byte[] r9 = r2.data     // Catch:{ Exception -> 0x0a33 }
            if (r9 == 0) goto L_0x0948
            r25 = r15
            java.lang.String r15 = "data"
            r4.putExtra(r15, r9)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x094a
        L_0x0948:
            r25 = r15
        L_0x094a:
            java.lang.String r9 = "mid"
            int r15 = r1.getId()     // Catch:{ Exception -> 0x0a33 }
            r4.putExtra(r9, r15)     // Catch:{ Exception -> 0x0a33 }
            java.lang.String r2 = r2.text     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            int r15 = r13.lastButtonId     // Catch:{ Exception -> 0x0a33 }
            r26 = r1
            int r1 = r15 + 1
            r13.lastButtonId = r1     // Catch:{ Exception -> 0x0a33 }
            r1 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r9, r15, r4, r1)     // Catch:{ Exception -> 0x0a33 }
            r1 = 0
            r12.addAction(r1, r2, r4)     // Catch:{ Exception -> 0x0a33 }
            r9 = 1
            goto L_0x0977
        L_0x096b:
            r23 = r7
            r22 = r10
            r10 = r8
            r7 = r25
            r26 = r1
            r25 = r15
            r1 = 0
        L_0x0977:
            int r0 = r0 + 1
            r2 = r40
            r4 = r21
            r15 = r25
            r1 = r26
            r25 = r7
            r8 = r10
            r10 = r22
            r7 = r23
            goto L_0x0910
        L_0x0989:
            r40 = r2
            r23 = r7
            r10 = r8
            r7 = r25
            r26 = r1
            r25 = r15
            r1 = 0
            int r3 = r3 + 1
            r0 = r16
            r1 = r26
            r4 = 1
            r25 = r7
            r8 = r10
            r7 = r23
            goto L_0x08ff
        L_0x09a3:
            r23 = r7
            r10 = r8
            r7 = r25
            r25 = r15
            r3 = r9
            goto L_0x09b5
        L_0x09ac:
            r23 = r7
            r10 = r8
            r7 = r25
            r1 = 0
            r25 = r15
            r3 = 0
        L_0x09b5:
            if (r3 != 0) goto L_0x0a0c
            r0 = 24
            if (r14 >= r0) goto L_0x0a0c
            java.lang.String r0 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0a33 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x0a33 }
            if (r0 != 0) goto L_0x0a0c
            boolean r0 = r39.hasMessagesToReply()     // Catch:{ Exception -> 0x0a33 }
            if (r0 == 0) goto L_0x0a0c
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r2 = org.telegram.messenger.PopupReplyReceiver.class
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x0a33 }
            int r1 = r13.currentAccount     // Catch:{ Exception -> 0x0a33 }
            r0.putExtra(r11, r1)     // Catch:{ Exception -> 0x0a33 }
            r1 = 19
            if (r14 > r1) goto L_0x09f4
            r1 = 2131165475(0x7var_, float:1.7945168E38)
            java.lang.String r2 = "Reply"
            r3 = 2131626913(0x7f0e0ba1, float:1.8881076E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r9 = 2
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r3, r9, r0, r4)     // Catch:{ Exception -> 0x0a33 }
            r12.addAction(r1, r2, r0)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0a0c
        L_0x09f4:
            r1 = 2131165474(0x7var_, float:1.7945166E38)
            java.lang.String r2 = "Reply"
            r3 = 2131626913(0x7f0e0ba1, float:1.8881076E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x0a33 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a33 }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r9 = 2
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r3, r9, r0, r4)     // Catch:{ Exception -> 0x0a33 }
            r12.addAction(r1, r2, r0)     // Catch:{ Exception -> 0x0a33 }
        L_0x0a0c:
            r0 = 26
            if (r14 < r0) goto L_0x0a29
            r1 = r39
            r2 = r7
            r4 = r20
            r7 = r23
            r8 = r10
            r9 = r18
            r10 = r25
            r11 = r29
            r14 = r12
            r12 = r24
            java.lang.String r0 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x0a33 }
            r14.setChannelId(r0)     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0a2a
        L_0x0a29:
            r14 = r12
        L_0x0a2a:
            r12 = r36
            r13.showExtraNotifications(r14, r12)     // Catch:{ Exception -> 0x0a33 }
            r39.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0a33 }
            goto L_0x0a37
        L_0x0a33:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0a37:
            return
        L_0x0a38:
            r39.dismissNotification()
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

    /*  JADX ERROR: JadxRuntimeException in pass: IfRegionVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0CLASSNAME: MOVE  (r4v14 org.json.JSONObject) = (r74v0 org.json.JSONObject)
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
    /* JADX WARNING: Removed duplicated region for block: B:133:0x02ff  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x031e  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x0333  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0385  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x0393 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x03a8  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0424  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x043b  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0454  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0467  */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x050c  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0514  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0521 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x053a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x055a  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x0562  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x0572  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x05a8  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x05e5  */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x06c9  */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x06ce  */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0740  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x082e  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x084f  */
    /* JADX WARNING: Removed duplicated region for block: B:395:0x087c  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x088e A[SYNTHETIC, Splitter:B:398:0x088e] */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0940  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0952  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0972  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x09cc  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x09ff  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0a20  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0a42  */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0af5  */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0b00  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0b07  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x0b17  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0b1d  */
    /* JADX WARNING: Removed duplicated region for block: B:449:0x0b21  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0b26  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0b36  */
    /* JADX WARNING: Removed duplicated region for block: B:476:0x0c1e  */
    /* JADX WARNING: Removed duplicated region for block: B:487:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:496:0x0cb4 A[Catch:{ JSONException -> 0x0d05 }] */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0cdb A[Catch:{ JSONException -> 0x0d05 }] */
    /* JADX WARNING: Removed duplicated region for block: B:501:0x0ce6  */
    /* JADX WARNING: Removed duplicated region for block: B:504:0x0ced A[Catch:{ JSONException -> 0x0d05 }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x01d3  */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r78, java.lang.String r79) {
        /*
            r77 = this;
            r8 = r77
            int r9 = android.os.Build.VERSION.SDK_INT
            android.app.Notification r10 = r78.build()
            r0 = 18
            if (r9 >= r0) goto L_0x001d
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r8.notificationId
            r0.notify(r1, r10)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x001c
            java.lang.String r0 = "show summary notification by SDK check"
            org.telegram.messenger.FileLog.d(r0)
        L_0x001c:
            return
        L_0x001d:
            org.telegram.messenger.AccountInstance r0 = r77.getAccountInstance()
            android.content.SharedPreferences r0 = r0.getNotificationsSettings()
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            android.util.LongSparseArray r12 = new android.util.LongSparseArray
            r12.<init>()
            r13 = 0
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
            int r5 = r0.getInt(r5, r13)
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r6 = r6.date
            if (r6 > r5) goto L_0x0061
            goto L_0x007b
        L_0x0061:
            java.lang.Object r5 = r12.get(r3)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x0078
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r12.put(r3, r5)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r11.add(r13, r3)
        L_0x0078:
            r5.add(r2)
        L_0x007b:
            int r1 = r1 + 1
            goto L_0x0031
        L_0x007e:
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.wearNotificationsIds
            android.util.LongSparseArray r14 = r0.clone()
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.wearNotificationsIds
            r0.clear()
            java.util.ArrayList r15 = new java.util.ArrayList
            r15.<init>()
            boolean r0 = org.telegram.messenger.WearDataLayerListenerService.isWatchConnected()
            if (r0 == 0) goto L_0x009b
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r6 = r0
            goto L_0x009c
        L_0x009b:
            r6 = 0
        L_0x009c:
            r5 = 27
            r4 = 1
            if (r9 <= r5) goto L_0x00ac
            if (r9 <= r5) goto L_0x00aa
            int r0 = r11.size()
            if (r0 <= r4) goto L_0x00aa
            goto L_0x00ac
        L_0x00aa:
            r3 = 0
            goto L_0x00ad
        L_0x00ac:
            r3 = 1
        L_0x00ad:
            r2 = 26
            if (r3 == 0) goto L_0x00b6
            if (r9 < r2) goto L_0x00b6
            checkOtherNotificationsChannel()
        L_0x00b6:
            org.telegram.messenger.UserConfig r0 = r77.getUserConfig()
            int r1 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x00cc
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x00c9
            goto L_0x00cc
        L_0x00c9:
            r16 = 0
            goto L_0x00ce
        L_0x00cc:
            r16 = 1
        L_0x00ce:
            int r0 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()
            r2 = 3
            if (r0 < r2) goto L_0x00d8
            r0 = 7
            r2 = 7
            goto L_0x00dc
        L_0x00d8:
            r0 = 10
            r2 = 10
        L_0x00dc:
            int r5 = r11.size()
            r7 = 0
        L_0x00e1:
            java.lang.String r4 = "id"
            if (r7 >= r5) goto L_0x0d1f
            int r0 = r15.size()
            if (r0 < r2) goto L_0x00ed
            goto L_0x0d1f
        L_0x00ed:
            java.lang.Object r0 = r11.get(r7)
            java.lang.Long r0 = (java.lang.Long) r0
            r22 = r14
            long r13 = r0.longValue()
            java.lang.Object r0 = r12.get(r13)
            r23 = r2
            r2 = r0
            java.util.ArrayList r2 = (java.util.ArrayList) r2
            r24 = r4
            r4 = 0
            java.lang.Object r0 = r2.get(r4)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r4 = r0.getId()
            r25 = r12
            int r12 = (int) r13
            r26 = r5
            r5 = 32
            r27 = r10
            r28 = r11
            long r10 = r13 >> r5
            int r11 = (int) r10
            r10 = r22
            java.lang.Object r0 = r10.get(r13)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0133
            if (r12 == 0) goto L_0x012e
            java.lang.Integer r0 = java.lang.Integer.valueOf(r12)
            goto L_0x0136
        L_0x012e:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r11)
            goto L_0x0136
        L_0x0133:
            r10.remove(r13)
        L_0x0136:
            r22 = r0
            if (r6 == 0) goto L_0x0140
            org.json.JSONObject r0 = new org.json.JSONObject
            r0.<init>()
            goto L_0x0141
        L_0x0140:
            r0 = 0
        L_0x0141:
            r5 = 0
            java.lang.Object r30 = r2.get(r5)
            r5 = r30
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            r30 = r0
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            r31 = r10
            int r10 = r0.date
            r32 = r6
            android.util.LongSparseArray r6 = new android.util.LongSparseArray
            r6.<init>()
            r33 = 0
            if (r12 == 0) goto L_0x0267
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r12 == r0) goto L_0x0164
            r0 = 1
            goto L_0x0165
        L_0x0164:
            r0 = 0
        L_0x0165:
            if (r12 <= 0) goto L_0x01e9
            r35 = r0
            org.telegram.messenger.MessagesController r0 = r77.getMessagesController()
            r36 = r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r7)
            if (r0 != 0) goto L_0x01a0
            boolean r7 = r5.isFcmMessage()
            if (r7 == 0) goto L_0x0186
            java.lang.String r5 = r5.localName
        L_0x0181:
            r38 = r5
            r37 = r6
            goto L_0x01bb
        L_0x0186:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0297
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found user to show dialog notification "
            r0.append(r2)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x0297
        L_0x01a0:
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            if (r7 == 0) goto L_0x0181
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x0181
            r38 = r5
            r37 = r6
            long r5 = r7.volume_id
            int r39 = (r5 > r33 ? 1 : (r5 == r33 ? 0 : -1))
            if (r39 == 0) goto L_0x01bb
            int r5 = r7.local_id
            if (r5 == 0) goto L_0x01bb
            goto L_0x01bc
        L_0x01bb:
            r7 = 0
        L_0x01bc:
            long r5 = (long) r12
            boolean r5 = org.telegram.messenger.UserObject.isReplyUser((long) r5)
            if (r5 == 0) goto L_0x01d3
            r5 = 2131626906(0x7f0e0b9a, float:1.8881061E38)
            java.lang.String r6 = "RepliesTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
        L_0x01cc:
            r6 = r0
            r40 = r5
            r0 = r7
            r7 = r30
            goto L_0x01e5
        L_0x01d3:
            if (r12 != r1) goto L_0x01df
            r5 = 2131625945(0x7f0e07d9, float:1.8879112E38)
            java.lang.String r6 = "MessageScheduledReminderNotification"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            goto L_0x01cc
        L_0x01df:
            r6 = r0
            r0 = r7
            r7 = r30
            r40 = r38
        L_0x01e5:
            r30 = 0
            goto L_0x02f3
        L_0x01e9:
            r35 = r0
            r37 = r6
            r36 = r7
            org.telegram.messenger.MessagesController r0 = r77.getMessagesController()
            int r6 = -r12
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r6)
            if (r0 != 0) goto L_0x0233
            boolean r6 = r5.isFcmMessage()
            if (r6 == 0) goto L_0x021a
            boolean r6 = r5.isSupergroup()
            java.lang.String r7 = r5.localName
            boolean r5 = r5.localChannel
            r39 = r5
            r38 = r6
        L_0x0210:
            r40 = r7
        L_0x0212:
            r7 = r30
            r6 = 0
            r30 = r0
            r0 = 0
            goto L_0x02f7
        L_0x021a:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0297
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found chat to show dialog notification "
            r0.append(r2)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x0297
        L_0x0233:
            boolean r5 = r0.megagroup
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r6 == 0) goto L_0x0241
            boolean r6 = r0.megagroup
            if (r6 != 0) goto L_0x0241
            r6 = 1
            goto L_0x0242
        L_0x0241:
            r6 = 0
        L_0x0242:
            java.lang.String r7 = r0.title
            r38 = r5
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r0.photo
            if (r5 == 0) goto L_0x0264
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small
            if (r5 == 0) goto L_0x0264
            r39 = r6
            r40 = r7
            long r6 = r5.volume_id
            int r41 = (r6 > r33 ? 1 : (r6 == r33 ? 0 : -1))
            if (r41 == 0) goto L_0x0212
            int r6 = r5.local_id
            if (r6 == 0) goto L_0x0212
            r7 = r30
            r6 = 0
            r30 = r0
            r0 = r5
            goto L_0x02f7
        L_0x0264:
            r39 = r6
            goto L_0x0210
        L_0x0267:
            r37 = r6
            r36 = r7
            long r5 = globalSecretChatId
            int r0 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x02e0
            org.telegram.messenger.MessagesController r0 = r77.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r11)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r5)
            if (r0 != 0) goto L_0x02b3
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0297
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found secret chat to show dialog notification "
            r0.append(r2)
            r0.append(r11)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x0297:
            r37 = r1
            r11 = r8
            r10 = r9
            r17 = r23
            r18 = r26
            r2 = r27
            r7 = r32
            r24 = r36
            r1 = 26
            r20 = 1
            r21 = 0
            r23 = 27
            r29 = 0
            r9 = r3
            r3 = r15
            goto L_0x0d07
        L_0x02b3:
            org.telegram.messenger.MessagesController r5 = r77.getMessagesController()
            int r6 = r0.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 != 0) goto L_0x02de
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0297
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "not found secret chat user to show dialog notification "
            r2.append(r4)
            int r0 = r0.user_id
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x0297
        L_0x02de:
            r0 = r5
            goto L_0x02e1
        L_0x02e0:
            r0 = 0
        L_0x02e1:
            r5 = 2131627057(0x7f0e0CLASSNAME, float:1.8881368E38)
            java.lang.String r6 = "SecretChatName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = r0
            r40 = r5
            r0 = 0
            r7 = 0
            r30 = 0
            r35 = 0
        L_0x02f3:
            r38 = 0
            r39 = 0
        L_0x02f7:
            java.lang.String r5 = "NotificationHiddenChatName"
            r42 = r15
            java.lang.String r15 = "NotificationHiddenName"
            if (r16 == 0) goto L_0x031e
            if (r12 >= 0) goto L_0x030b
            r44 = r6
            r6 = 2131626199(0x7f0e08d7, float:1.8879627E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r6)
            goto L_0x0314
        L_0x030b:
            r44 = r6
            r6 = 2131626202(0x7f0e08da, float:1.8879634E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r6)
        L_0x0314:
            r35 = r3
            r40 = r10
            r45 = r11
            r3 = 0
            r6 = 0
            r10 = r0
            goto L_0x032f
        L_0x031e:
            r44 = r6
            r6 = r0
            r45 = r11
            r75 = r35
            r35 = r3
            r3 = r75
            r76 = r40
            r40 = r10
            r10 = r76
        L_0x032f:
            r11 = 28
            if (r6 == 0) goto L_0x0385
            r46 = r15
            r15 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r15)
            if (r9 >= r11) goto L_0x037e
            org.telegram.messenger.ImageLoader r15 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r11 = "50_50"
            r47 = r5
            r5 = 0
            android.graphics.drawable.BitmapDrawable r11 = r15.getImageFromMemory(r6, r5, r11)
            if (r11 == 0) goto L_0x0353
            android.graphics.Bitmap r11 = r11.getBitmap()
        L_0x034f:
            r19 = r0
            r15 = r11
            goto L_0x038d
        L_0x0353:
            boolean r11 = r0.exists()     // Catch:{ all -> 0x0381 }
            if (r11 == 0) goto L_0x037c
            r11 = 1126170624(0x43200000, float:160.0)
            r15 = 1112014848(0x42480000, float:50.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)     // Catch:{ all -> 0x0381 }
            float r15 = (float) r15     // Catch:{ all -> 0x0381 }
            float r11 = r11 / r15
            android.graphics.BitmapFactory$Options r15 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0381 }
            r15.<init>()     // Catch:{ all -> 0x0381 }
            r19 = 1065353216(0x3var_, float:1.0)
            int r19 = (r11 > r19 ? 1 : (r11 == r19 ? 0 : -1))
            if (r19 >= 0) goto L_0x0370
            r11 = 1
            goto L_0x0371
        L_0x0370:
            int r11 = (int) r11     // Catch:{ all -> 0x0381 }
        L_0x0371:
            r15.inSampleSize = r11     // Catch:{ all -> 0x0381 }
            java.lang.String r11 = r0.getAbsolutePath()     // Catch:{ all -> 0x0381 }
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeFile(r11, r15)     // Catch:{ all -> 0x0381 }
            goto L_0x034f
        L_0x037c:
            r11 = r5
            goto L_0x034f
        L_0x037e:
            r47 = r5
            r5 = 0
        L_0x0381:
            r19 = r0
            r15 = r5
            goto L_0x038d
        L_0x0385:
            r47 = r5
            r46 = r15
            r5 = 0
            r15 = r5
            r19 = r15
        L_0x038d:
            java.lang.String r11 = "max_id"
            java.lang.String r5 = "currentAccount"
            if (r39 == 0) goto L_0x0395
            if (r38 == 0) goto L_0x0427
        L_0x0395:
            if (r3 == 0) goto L_0x0427
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0427
            if (r1 == r12) goto L_0x0427
            r49 = r6
            r48 = r7
            long r6 = (long) r12
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r0 != 0) goto L_0x0424
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r7 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r6, r7)
            java.lang.String r6 = "dialog_id"
            r0.putExtra(r6, r13)
            r0.putExtra(r11, r4)
            int r6 = r8.currentAccount
            r0.putExtra(r5, r6)
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r7 = r22.intValue()
            r50 = r3
            r3 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r6, r7, r0, r3)
            androidx.core.app.RemoteInput$Builder r3 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r6 = "extra_voice_reply"
            r3.<init>(r6)
            r6 = 2131626913(0x7f0e0ba1, float:1.8881076E38)
            java.lang.String r7 = "Reply"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r3.setLabel(r6)
            androidx.core.app.RemoteInput r3 = r3.build()
            if (r12 >= 0) goto L_0x03f7
            r7 = 1
            java.lang.Object[] r6 = new java.lang.Object[r7]
            r7 = 0
            r6[r7] = r10
            java.lang.String r7 = "ReplyToGroup"
            r51 = r15
            r15 = 2131626914(0x7f0e0ba2, float:1.8881078E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r7, r15, r6)
            goto L_0x0408
        L_0x03f7:
            r51 = r15
            r6 = 2131626915(0x7f0e0ba3, float:1.888108E38)
            r7 = 1
            java.lang.Object[] r15 = new java.lang.Object[r7]
            r7 = 0
            r15[r7] = r10
            java.lang.String r7 = "ReplyToUser"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r7, r6, r15)
        L_0x0408:
            androidx.core.app.NotificationCompat$Action$Builder r7 = new androidx.core.app.NotificationCompat$Action$Builder
            r15 = 2131165519(0x7var_f, float:1.7945257E38)
            r7.<init>(r15, r6, r0)
            r6 = 1
            r7.setAllowGeneratedReplies(r6)
            r7.setSemanticAction(r6)
            r7.addRemoteInput(r3)
            r3 = 0
            r7.setShowsUserInterface(r3)
            androidx.core.app.NotificationCompat$Action r0 = r7.build()
            r3 = r0
            goto L_0x0430
        L_0x0424:
            r50 = r3
            goto L_0x042d
        L_0x0427:
            r50 = r3
            r49 = r6
            r48 = r7
        L_0x042d:
            r51 = r15
            r3 = 0
        L_0x0430:
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            java.lang.Object r0 = r0.get(r13)
            java.lang.Integer r0 = (java.lang.Integer) r0
            r6 = 0
            if (r0 != 0) goto L_0x043f
            java.lang.Integer r0 = java.lang.Integer.valueOf(r6)
        L_0x043f:
            int r0 = r0.intValue()
            int r7 = r2.size()
            int r0 = java.lang.Math.max(r0, r7)
            r7 = 2
            r15 = 1
            if (r0 <= r15) goto L_0x0467
            r15 = 28
            if (r9 < r15) goto L_0x0454
            goto L_0x0467
        L_0x0454:
            java.lang.Object[] r15 = new java.lang.Object[r7]
            r15[r6] = r10
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r6 = 1
            r15[r6] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r15)
            r6 = r0
            goto L_0x0468
        L_0x0467:
            r6 = r10
        L_0x0468:
            long r7 = (long) r1
            r15 = r37
            java.lang.Object r0 = r15.get(r7)
            r37 = r0
            androidx.core.app.Person r37 = (androidx.core.app.Person) r37
            r52 = r7
            r7 = 28
            if (r9 < r7) goto L_0x0500
            if (r37 != 0) goto L_0x0500
            org.telegram.messenger.MessagesController r0 = r77.getMessagesController()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r7)
            if (r0 != 0) goto L_0x0491
            org.telegram.messenger.UserConfig r0 = r77.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x0491:
            if (r0 == 0) goto L_0x04f4
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo     // Catch:{ all -> 0x04e4 }
            if (r7 == 0) goto L_0x04f4
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small     // Catch:{ all -> 0x04e4 }
            if (r7 == 0) goto L_0x04f4
            r54 = r3
            r8 = r4
            long r3 = r7.volume_id     // Catch:{ all -> 0x04e0 }
            int r55 = (r3 > r33 ? 1 : (r3 == r33 ? 0 : -1))
            if (r55 == 0) goto L_0x04dd
            int r3 = r7.local_id     // Catch:{ all -> 0x04e0 }
            if (r3 == 0) goto L_0x04dd
            androidx.core.app.Person$Builder r3 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x04e0 }
            r3.<init>()     // Catch:{ all -> 0x04e0 }
            java.lang.String r4 = "FromYou"
            r7 = 2131625558(0x7f0e0656, float:1.8878327E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r7)     // Catch:{ all -> 0x04e0 }
            r3.setName(r4)     // Catch:{ all -> 0x04e0 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x04e0 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x04e0 }
            r4 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r4)     // Catch:{ all -> 0x04e0 }
            r7 = r77
            r75 = r52
            r52 = r5
            r4 = r75
            r7.loadRoundAvatar(r0, r3)     // Catch:{ all -> 0x04db }
            androidx.core.app.Person r3 = r3.build()     // Catch:{ all -> 0x04db }
            r15.put(r4, r3)     // Catch:{ all -> 0x04d7 }
            r37 = r3
            goto L_0x0506
        L_0x04d7:
            r0 = move-exception
            r37 = r3
            goto L_0x04f0
        L_0x04db:
            r0 = move-exception
            goto L_0x04f0
        L_0x04dd:
            r7 = r77
            goto L_0x04f9
        L_0x04e0:
            r0 = move-exception
            r7 = r77
            goto L_0x04ea
        L_0x04e4:
            r0 = move-exception
            r7 = r77
            r54 = r3
            r8 = r4
        L_0x04ea:
            r75 = r52
            r52 = r5
            r4 = r75
        L_0x04f0:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0506
        L_0x04f4:
            r7 = r77
            r54 = r3
            r8 = r4
        L_0x04f9:
            r75 = r52
            r52 = r5
            r4 = r75
            goto L_0x0506
        L_0x0500:
            r7 = r77
            r54 = r3
            r8 = r4
            goto L_0x04f9
        L_0x0506:
            r0 = r37
            java.lang.String r3 = ""
            if (r0 == 0) goto L_0x0514
            r37 = r1
            androidx.core.app.NotificationCompat$MessagingStyle r1 = new androidx.core.app.NotificationCompat$MessagingStyle
            r1.<init>((androidx.core.app.Person) r0)
            goto L_0x051b
        L_0x0514:
            r37 = r1
            androidx.core.app.NotificationCompat$MessagingStyle r1 = new androidx.core.app.NotificationCompat$MessagingStyle
            r1.<init>((java.lang.CharSequence) r3)
        L_0x051b:
            r53 = r8
            r8 = 28
            if (r9 < r8) goto L_0x0530
            if (r12 >= 0) goto L_0x0525
            if (r39 == 0) goto L_0x0530
        L_0x0525:
            r8 = r10
            r55 = r11
            long r10 = (long) r12
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r10)
            if (r0 == 0) goto L_0x0536
            goto L_0x0533
        L_0x0530:
            r8 = r10
            r55 = r11
        L_0x0533:
            r1.setConversationTitle(r6)
        L_0x0536:
            r6 = 28
            if (r9 < r6) goto L_0x0548
            if (r39 != 0) goto L_0x053e
            if (r12 < 0) goto L_0x0548
        L_0x053e:
            long r10 = (long) r12
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r10)
            if (r0 == 0) goto L_0x0546
            goto L_0x0548
        L_0x0546:
            r0 = 0
            goto L_0x0549
        L_0x0548:
            r0 = 1
        L_0x0549:
            r1.setGroupConversation(r0)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r10 = 1
            java.lang.String[] r11 = new java.lang.String[r10]
            r56 = r1
            boolean[] r1 = new boolean[r10]
            if (r48 == 0) goto L_0x0562
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r57 = r0
            goto L_0x0564
        L_0x0562:
            r57 = 0
        L_0x0564:
            int r0 = r2.size()
            int r0 = r0 - r10
            r10 = r0
            r58 = 0
            r59 = 0
        L_0x056e:
            r60 = 1000(0x3e8, double:4.94E-321)
            if (r10 < 0) goto L_0x08ff
            java.lang.Object r0 = r2.get(r10)
            r62 = r2
            r2 = r0
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            java.lang.String r0 = r7.getShortStringForMessage(r2, r11, r1)
            int r63 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r63 != 0) goto L_0x058a
            r21 = 0
            r11[r21] = r8
            r63 = r8
            goto L_0x05a4
        L_0x058a:
            r21 = 0
            r63 = r8
            if (r12 >= 0) goto L_0x05a4
            org.telegram.tgnet.TLRPC$Message r8 = r2.messageOwner
            boolean r8 = r8.from_scheduled
            if (r8 == 0) goto L_0x05a4
            r8 = 2131626245(0x7f0e0905, float:1.887972E38)
            r64 = r10
            java.lang.String r10 = "NotificationMessageScheduledName"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r11[r21] = r8
            goto L_0x05a6
        L_0x05a4:
            r64 = r10
        L_0x05a6:
            if (r0 != 0) goto L_0x05e5
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05d2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "message text is null for "
            r0.append(r8)
            int r8 = r2.getId()
            r0.append(r8)
            java.lang.String r8 = " did = "
            r0.append(r8)
            r8 = r3
            long r2 = r2.getDialogId()
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x05d3
        L_0x05d2:
            r8 = r3
        L_0x05d3:
            r65 = r4
            r41 = r6
            r68 = r11
            r69 = r15
            r43 = r46
            r67 = r47
            r4 = r56
            r10 = r57
            goto L_0x08e5
        L_0x05e5:
            r8 = r3
            int r3 = r6.length()
            if (r3 <= 0) goto L_0x05f1
            java.lang.String r3 = "\n\n"
            r6.append(r3)
        L_0x05f1:
            int r3 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x061b
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            boolean r3 = r3.from_scheduled
            if (r3 == 0) goto L_0x061b
            if (r12 <= 0) goto L_0x061b
            r3 = 2
            java.lang.Object[] r10 = new java.lang.Object[r3]
            r3 = 2131626245(0x7f0e0905, float:1.887972E38)
            r65 = r4
            java.lang.String r4 = "NotificationMessageScheduledName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r4 = 0
            r10[r4] = r3
            r3 = 1
            r10[r3] = r0
            java.lang.String r0 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r0, r10)
            r6.append(r0)
            goto L_0x0639
        L_0x061b:
            r65 = r4
            r4 = 0
            r3 = r11[r4]
            if (r3 == 0) goto L_0x0636
            r3 = 2
            java.lang.Object[] r5 = new java.lang.Object[r3]
            r10 = r11[r4]
            r5[r4] = r10
            r4 = 1
            r5[r4] = r0
            java.lang.String r4 = "%1$s: %2$s"
            java.lang.String r4 = java.lang.String.format(r4, r5)
            r6.append(r4)
            goto L_0x0639
        L_0x0636:
            r6.append(r0)
        L_0x0639:
            r4 = r0
            if (r12 <= 0) goto L_0x063f
            r5 = r4
            long r3 = (long) r12
            goto L_0x064d
        L_0x063f:
            r5 = r4
            if (r39 == 0) goto L_0x0645
            int r0 = -r12
        L_0x0643:
            long r3 = (long) r0
            goto L_0x064d
        L_0x0645:
            if (r12 >= 0) goto L_0x064c
            int r0 = r2.getSenderId()
            goto L_0x0643
        L_0x064c:
            r3 = r13
        L_0x064d:
            java.lang.Object r0 = r15.get(r3)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r10 = 0
            r67 = r11[r10]
            if (r67 != 0) goto L_0x06ab
            if (r16 == 0) goto L_0x06a0
            if (r12 >= 0) goto L_0x068b
            if (r39 == 0) goto L_0x0678
            r10 = 27
            r41 = r6
            if (r9 <= r10) goto L_0x0674
            r6 = r47
            r10 = 2131626199(0x7f0e08d7, float:1.8879627E38)
            java.lang.String r47 = org.telegram.messenger.LocaleController.getString(r6, r10)
            r67 = r6
            r6 = r46
            r10 = r47
            goto L_0x06ba
        L_0x0674:
            r10 = 2131626199(0x7f0e08d7, float:1.8879627E38)
            goto L_0x06a2
        L_0x0678:
            r41 = r6
            r6 = r47
            r10 = 2131626200(0x7f0e08d8, float:1.887963E38)
            r67 = r6
            java.lang.String r6 = "NotificationHiddenChatUserName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r10)
            r10 = r6
            r6 = r46
            goto L_0x06ba
        L_0x068b:
            r41 = r6
            r67 = r47
            r6 = 27
            if (r9 <= r6) goto L_0x069d
            r6 = r46
            r10 = 2131626202(0x7f0e08da, float:1.8879634E38)
            java.lang.String r43 = org.telegram.messenger.LocaleController.getString(r6, r10)
            goto L_0x06b8
        L_0x069d:
            r6 = r46
            goto L_0x06a6
        L_0x06a0:
            r41 = r6
        L_0x06a2:
            r6 = r46
            r67 = r47
        L_0x06a6:
            r10 = 2131626202(0x7f0e08da, float:1.8879634E38)
            r10 = r8
            goto L_0x06ba
        L_0x06ab:
            r41 = r6
            r6 = r46
            r67 = r47
            r10 = 2131626202(0x7f0e08da, float:1.8879634E38)
            r21 = 0
            r43 = r11[r21]
        L_0x06b8:
            r10 = r43
        L_0x06ba:
            r43 = r6
            if (r0 == 0) goto L_0x06ce
            java.lang.CharSequence r6 = r0.getName()
            boolean r6 = android.text.TextUtils.equals(r6, r10)
            if (r6 != 0) goto L_0x06c9
            goto L_0x06ce
        L_0x06c9:
            r3 = r0
            r68 = r11
            goto L_0x073e
        L_0x06ce:
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            r0.setName(r10)
            r6 = 0
            boolean r10 = r1[r6]
            if (r10 == 0) goto L_0x0734
            if (r12 == 0) goto L_0x0734
            r6 = 28
            if (r9 < r6) goto L_0x0734
            if (r12 > 0) goto L_0x072c
            if (r39 == 0) goto L_0x06e6
            goto L_0x072c
        L_0x06e6:
            if (r12 >= 0) goto L_0x0728
            int r6 = r2.getSenderId()
            org.telegram.messenger.MessagesController r10 = r77.getMessagesController()
            r68 = r11
            java.lang.Integer r11 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r11)
            if (r10 != 0) goto L_0x070e
            org.telegram.messenger.MessagesStorage r10 = r77.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r10 = r10.getUserSync(r6)
            if (r10 == 0) goto L_0x070e
            org.telegram.messenger.MessagesController r6 = r77.getMessagesController()
            r11 = 1
            r6.putUser(r10, r11)
        L_0x070e:
            if (r10 == 0) goto L_0x072a
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r10.photo
            if (r6 == 0) goto L_0x072a
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            if (r6 == 0) goto L_0x072a
            long r10 = r6.volume_id
            int r69 = (r10 > r33 ? 1 : (r10 == r33 ? 0 : -1))
            if (r69 == 0) goto L_0x072a
            int r10 = r6.local_id
            if (r10 == 0) goto L_0x072a
            r10 = 1
            java.io.File r6 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r10)
            goto L_0x0730
        L_0x0728:
            r68 = r11
        L_0x072a:
            r6 = 0
            goto L_0x0730
        L_0x072c:
            r68 = r11
            r6 = r19
        L_0x0730:
            r7.loadRoundAvatar(r6, r0)
            goto L_0x0736
        L_0x0734:
            r68 = r11
        L_0x0736:
            androidx.core.app.Person r0 = r0.build()
            r15.put(r3, r0)
            r3 = r0
        L_0x073e:
            if (r12 == 0) goto L_0x087c
            r4 = 0
            boolean r0 = r1[r4]
            if (r0 == 0) goto L_0x0827
            r4 = 28
            if (r9 < r4) goto L_0x0827
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r6 = "activity"
            java.lang.Object r0 = r0.getSystemService(r6)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x0827
            if (r16 != 0) goto L_0x0827
            boolean r0 = r2.isSecretMedia()
            if (r0 != 0) goto L_0x0827
            int r0 = r2.type
            r6 = 1
            if (r0 == r6) goto L_0x076c
            boolean r0 = r2.isSticker()
            if (r0 == 0) goto L_0x0827
        L_0x076c:
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r6 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner
            int r10 = r10.date
            long r10 = (long) r10
            long r10 = r10 * r60
            r6.<init>(r5, r10, r3)
            boolean r10 = r2.isSticker()
            if (r10 == 0) goto L_0x0787
            java.lang.String r10 = "image/webp"
            goto L_0x0789
        L_0x0787:
            java.lang.String r10 = "image/jpeg"
        L_0x0789:
            boolean r11 = r0.exists()
            if (r11 == 0) goto L_0x079f
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x079a }
            java.lang.String r4 = "org.telegram.messenger.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r11, r4, r0)     // Catch:{ Exception -> 0x079a }
            r69 = r15
            goto L_0x07f5
        L_0x079a:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07f2
        L_0x079f:
            org.telegram.messenger.FileLoader r4 = r77.getFileLoader()
            java.lang.String r11 = r0.getName()
            boolean r4 = r4.isLoadingFile(r11)
            if (r4 == 0) goto L_0x07f2
            android.net.Uri$Builder r4 = new android.net.Uri$Builder
            r4.<init>()
            java.lang.String r11 = "content"
            android.net.Uri$Builder r4 = r4.scheme(r11)
            java.lang.String r11 = "org.telegram.messenger.notification_image_provider"
            android.net.Uri$Builder r4 = r4.authority(r11)
            java.lang.String r11 = "msg_media_raw"
            android.net.Uri$Builder r4 = r4.appendPath(r11)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r69 = r15
            int r15 = r7.currentAccount
            r11.append(r15)
            r11.append(r8)
            java.lang.String r11 = r11.toString()
            android.net.Uri$Builder r4 = r4.appendPath(r11)
            java.lang.String r11 = r0.getName()
            android.net.Uri$Builder r4 = r4.appendPath(r11)
            java.lang.String r0 = r0.getAbsolutePath()
            java.lang.String r11 = "final_path"
            android.net.Uri$Builder r0 = r4.appendQueryParameter(r11, r0)
            android.net.Uri r0 = r0.build()
            goto L_0x07f5
        L_0x07f2:
            r69 = r15
            r0 = 0
        L_0x07f5:
            if (r0 == 0) goto L_0x0829
            r6.setData(r10, r0)
            r4 = r56
            r4.addMessage(r6)
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r10 = "com.android.systemui"
            r11 = 1
            r6.grantUriPermission(r10, r0, r11)
            org.telegram.messenger.-$$Lambda$NotificationsController$0YINMSsEaa1VtQ6qrU-ZxF9e9ro r6 = new org.telegram.messenger.-$$Lambda$NotificationsController$0YINMSsEaa1VtQ6qrU-ZxF9e9ro
            r6.<init>(r0)
            r10 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6, r10)
            java.lang.CharSequence r0 = r2.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0825
            java.lang.CharSequence r0 = r2.caption
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r6 = r6.date
            long r10 = (long) r6
            long r10 = r10 * r60
            r4.addMessage(r0, r10, r3)
        L_0x0825:
            r0 = 1
            goto L_0x082c
        L_0x0827:
            r69 = r15
        L_0x0829:
            r4 = r56
            r0 = 0
        L_0x082c:
            if (r0 != 0) goto L_0x0838
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            int r0 = r0.date
            long r10 = (long) r0
            long r10 = r10 * r60
            r4.addMessage(r5, r10, r3)
        L_0x0838:
            r3 = 0
            boolean r0 = r1[r3]
            if (r0 == 0) goto L_0x088a
            if (r16 != 0) goto L_0x088a
            boolean r0 = r2.isVoice()
            if (r0 == 0) goto L_0x088a
            java.util.List r0 = r4.getMessages()
            boolean r3 = r0.isEmpty()
            if (r3 != 0) goto L_0x088a
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3)
            r6 = 24
            if (r9 < r6) goto L_0x0864
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0862 }
            java.lang.String r10 = "org.telegram.messenger.provider"
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r6, r10, r3)     // Catch:{ Exception -> 0x0862 }
            goto L_0x0868
        L_0x0862:
            r3 = 0
            goto L_0x0868
        L_0x0864:
            android.net.Uri r3 = android.net.Uri.fromFile(r3)
        L_0x0868:
            if (r3 == 0) goto L_0x088a
            int r6 = r0.size()
            r10 = 1
            int r6 = r6 - r10
            java.lang.Object r0 = r0.get(r6)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r0
            java.lang.String r6 = "audio/ogg"
            r0.setData(r6, r3)
            goto L_0x088a
        L_0x087c:
            r69 = r15
            r4 = r56
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            int r0 = r0.date
            long r10 = (long) r0
            long r10 = r10 * r60
            r4.addMessage(r5, r10, r3)
        L_0x088a:
            r10 = r57
            if (r10 == 0) goto L_0x08ce
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x08cd }
            r0.<init>()     // Catch:{ JSONException -> 0x08cd }
            java.lang.String r3 = "text"
            r0.put(r3, r5)     // Catch:{ JSONException -> 0x08cd }
            java.lang.String r3 = "date"
            org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner     // Catch:{ JSONException -> 0x08cd }
            int r5 = r5.date     // Catch:{ JSONException -> 0x08cd }
            r0.put(r3, r5)     // Catch:{ JSONException -> 0x08cd }
            boolean r3 = r2.isFromUser()     // Catch:{ JSONException -> 0x08cd }
            if (r3 == 0) goto L_0x08c9
            if (r12 >= 0) goto L_0x08c9
            org.telegram.messenger.MessagesController r3 = r77.getMessagesController()     // Catch:{ JSONException -> 0x08cd }
            int r5 = r2.getSenderId()     // Catch:{ JSONException -> 0x08cd }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ JSONException -> 0x08cd }
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r5)     // Catch:{ JSONException -> 0x08cd }
            if (r3 == 0) goto L_0x08c9
            java.lang.String r5 = "fname"
            java.lang.String r6 = r3.first_name     // Catch:{ JSONException -> 0x08cd }
            r0.put(r5, r6)     // Catch:{ JSONException -> 0x08cd }
            java.lang.String r5 = "lname"
            java.lang.String r3 = r3.last_name     // Catch:{ JSONException -> 0x08cd }
            r0.put(r5, r3)     // Catch:{ JSONException -> 0x08cd }
        L_0x08c9:
            r10.put(r0)     // Catch:{ JSONException -> 0x08cd }
            goto L_0x08ce
        L_0x08cd:
        L_0x08ce:
            r5 = 777000(0xbdb28, double:3.83889E-318)
            int r0 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x08e5
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x08e5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r2 = r2.getId()
            r59 = r0
            r58 = r2
        L_0x08e5:
            int r0 = r64 + -1
            r56 = r4
            r3 = r8
            r57 = r10
            r6 = r41
            r46 = r43
            r2 = r62
            r8 = r63
            r4 = r65
            r47 = r67
            r11 = r68
            r15 = r69
            r10 = r0
            goto L_0x056e
        L_0x08ff:
            r62 = r2
            r41 = r6
            r63 = r8
            r69 = r15
            r4 = r56
            r10 = r57
            r8 = r3
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
            if (r12 == 0) goto L_0x0952
            if (r12 <= 0) goto L_0x0949
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r12)
            goto L_0x094f
        L_0x0949:
            int r1 = -r12
            java.lang.String r2 = "chatId"
            r0.putExtra(r2, r1)
        L_0x094f:
            r2 = r45
            goto L_0x0959
        L_0x0952:
            java.lang.String r1 = "encId"
            r2 = r45
            r0.putExtra(r1, r2)
        L_0x0959:
            int r1 = r7.currentAccount
            r3 = r52
            r0.putExtra(r3, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r5 = 1073741824(0x40000000, float:2.0)
            r6 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r1, r6, r0, r5)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r5 = r54
            if (r54 == 0) goto L_0x0975
            r1.addAction(r5)
        L_0x0975:
            android.content.Intent r6 = new android.content.Intent
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r15 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r6.<init>(r11, r15)
            r11 = 32
            r6.addFlags(r11)
            java.lang.String r11 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r6.setAction(r11)
            java.lang.String r11 = "dialog_id"
            r6.putExtra(r11, r13)
            r11 = r53
            r15 = r55
            r6.putExtra(r15, r11)
            r19 = r10
            int r10 = r7.currentAccount
            r6.putExtra(r3, r10)
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r15 = r22.intValue()
            r29 = r9
            r9 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r6 = android.app.PendingIntent.getBroadcast(r10, r15, r6, r9)
            androidx.core.app.NotificationCompat$Action$Builder r9 = new androidx.core.app.NotificationCompat$Action$Builder
            r10 = 2131165676(0x7var_ec, float:1.7945576E38)
            r15 = 2131625858(0x7f0e0782, float:1.8878936E38)
            r54 = r5
            java.lang.String r5 = "MarkAsRead"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r15)
            r9.<init>(r10, r5, r6)
            r5 = 2
            r9.setSemanticAction(r5)
            r5 = 0
            r9.setShowsUserInterface(r5)
            androidx.core.app.NotificationCompat$Action r5 = r9.build()
            java.lang.String r9 = "_"
            if (r12 == 0) goto L_0x09ff
            if (r12 <= 0) goto L_0x09e6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "tguser"
            r2.append(r6)
            r2.append(r12)
            r2.append(r9)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            goto L_0x0a1e
        L_0x09e6:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "tgchat"
            r2.append(r6)
            int r6 = -r12
            r2.append(r6)
            r2.append(r9)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            goto L_0x0a1e
        L_0x09ff:
            long r33 = globalSecretChatId
            int r6 = (r13 > r33 ? 1 : (r13 == r33 ? 0 : -1))
            if (r6 == 0) goto L_0x0a1d
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r10 = "tgenc"
            r6.append(r10)
            r6.append(r2)
            r6.append(r9)
            r6.append(r11)
            java.lang.String r2 = r6.toString()
            goto L_0x0a1e
        L_0x0a1d:
            r2 = 0
        L_0x0a1e:
            if (r2 == 0) goto L_0x0a42
            r1.setDismissalId(r2)
            androidx.core.app.NotificationCompat$WearableExtender r6 = new androidx.core.app.NotificationCompat$WearableExtender
            r6.<init>()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r15 = "summary_"
            r10.append(r15)
            r10.append(r2)
            java.lang.String r2 = r10.toString()
            r6.setDismissalId(r2)
            r10 = r78
            r10.extend(r6)
            goto L_0x0a44
        L_0x0a42:
            r10 = r78
        L_0x0a44:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "tgaccount"
            r2.append(r6)
            r6 = r37
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            r1.setBridgeTag(r2)
            r2 = r62
            r15 = 0
            java.lang.Object r33 = r2.get(r15)
            r15 = r33
            org.telegram.messenger.MessageObject r15 = (org.telegram.messenger.MessageObject) r15
            org.telegram.tgnet.TLRPC$Message r15 = r15.messageOwner
            int r15 = r15.date
            r53 = r11
            long r10 = (long) r15
            long r10 = r10 * r60
            androidx.core.app.NotificationCompat$Builder r15 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            r15.<init>(r6)
            r6 = r63
            r15.setContentTitle(r6)
            r33 = r9
            r9 = 2131165832(0x7var_, float:1.7945892E38)
            r15.setSmallIcon(r9)
            java.lang.String r9 = r41.toString()
            r15.setContentText(r9)
            r9 = 1
            r15.setAutoCancel(r9)
            int r2 = r2.size()
            r15.setNumber(r2)
            r2 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r15.setColor(r2)
            r2 = 0
            r15.setGroupSummary(r2)
            r15.setWhen(r10)
            r15.setShowWhen(r9)
            r15.setStyle(r4)
            r15.setContentIntent(r0)
            r15.extend(r1)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r8)
            r1 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r1 = r1 - r10
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r15.setSortKey(r0)
            java.lang.String r0 = "msg"
            r15.setCategory(r0)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r2 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r1, r2)
            java.lang.String r1 = "messageDate"
            r8 = r40
            r0.putExtra(r1, r8)
            java.lang.String r1 = "dialogId"
            r0.putExtra(r1, r13)
            int r1 = r7.currentAccount
            r0.putExtra(r3, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r2 = r22.intValue()
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r2, r0, r4)
            r15.setDeleteIntent(r0)
            if (r35 == 0) goto L_0x0afe
            java.lang.String r0 = r7.notificationGroup
            r15.setGroup(r0)
            r1 = 1
            r15.setGroupAlertBehavior(r1)
        L_0x0afe:
            if (r54 == 0) goto L_0x0b05
            r1 = r54
            r15.addAction(r1)
        L_0x0b05:
            if (r16 != 0) goto L_0x0b0a
            r15.addAction(r5)
        L_0x0b0a:
            int r0 = r28.size()
            r4 = 1
            if (r0 != r4) goto L_0x0b1d
            boolean r0 = android.text.TextUtils.isEmpty(r79)
            if (r0 != 0) goto L_0x0b1d
            r9 = r79
            r15.setSubText(r9)
            goto L_0x0b1f
        L_0x0b1d:
            r9 = r79
        L_0x0b1f:
            if (r12 != 0) goto L_0x0b24
            r15.setLocalOnly(r4)
        L_0x0b24:
            if (r51 == 0) goto L_0x0b2b
            r5 = r51
            r15.setLargeIcon(r5)
        L_0x0b2b:
            r1 = 0
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1)
            if (r0 != 0) goto L_0x0c1e
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0c1e
            r1 = r59
            if (r1 == 0) goto L_0x0bc7
            int r0 = r1.size()
            r2 = 0
        L_0x0b3f:
            if (r2 >= r0) goto L_0x0bc7
            java.lang.Object r5 = r1.get(r2)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r5 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r10 = r5.buttons
            int r10 = r10.size()
            r11 = 0
        L_0x0b4e:
            if (r11 >= r10) goto L_0x0bb7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r4 = r5.buttons
            java.lang.Object r4 = r4.get(r11)
            org.telegram.tgnet.TLRPC$KeyboardButton r4 = (org.telegram.tgnet.TLRPC$KeyboardButton) r4
            r34 = r0
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r0 == 0) goto L_0x0b9e
            android.content.Intent r0 = new android.content.Intent
            r40 = r1
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r41 = r5
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r5 = org.telegram.messenger.NotificationCallbackReceiver.class
            r0.<init>(r1, r5)
            int r1 = r7.currentAccount
            r0.putExtra(r3, r1)
            java.lang.String r1 = "did"
            r0.putExtra(r1, r13)
            byte[] r1 = r4.data
            if (r1 == 0) goto L_0x0b7e
            java.lang.String r5 = "data"
            r0.putExtra(r5, r1)
        L_0x0b7e:
            java.lang.String r1 = "mid"
            r5 = r58
            r0.putExtra(r1, r5)
            java.lang.String r1 = r4.text
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            r52 = r3
            int r3 = r7.lastButtonId
            r43 = r5
            int r5 = r3 + 1
            r7.lastButtonId = r5
            r5 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r4, r3, r0, r5)
            r4 = 0
            r15.addAction(r4, r1, r0)
            goto L_0x0ba9
        L_0x0b9e:
            r40 = r1
            r52 = r3
            r41 = r5
            r43 = r58
            r4 = 0
            r5 = 134217728(0x8000000, float:3.85186E-34)
        L_0x0ba9:
            int r11 = r11 + 1
            r0 = r34
            r1 = r40
            r5 = r41
            r58 = r43
            r3 = r52
            r4 = 1
            goto L_0x0b4e
        L_0x0bb7:
            r34 = r0
            r40 = r1
            r52 = r3
            r43 = r58
            r4 = 0
            r5 = 134217728(0x8000000, float:3.85186E-34)
            int r2 = r2 + 1
            r4 = 1
            goto L_0x0b3f
        L_0x0bc7:
            r4 = 0
            r0 = 29
            r10 = r29
            if (r10 < r0) goto L_0x0CLASSNAME
            if (r12 == 0) goto L_0x0CLASSNAME
            long r0 = (long) r12
            r2 = r69
            java.lang.Object r0 = r2.get(r0)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r11 = r37
            r1 = r77
            r17 = r23
            r5 = 26
            r2 = r15
            r9 = r35
            r11 = r50
            r3 = r12
            r40 = r8
            r70 = r24
            r8 = r53
            r20 = 1
            r21 = 0
            r4 = r6
            r18 = r26
            r8 = 26
            r23 = 27
            r24 = 0
            r5 = r44
            r73 = r6
            r72 = r32
            r8 = r44
            r71 = r49
            r6 = r30
            r29 = r24
            r24 = r36
            r74 = r48
            r11 = r7
            r7 = r0
            r1.createNotificationShortcut(r2, r3, r4, r5, r6, r7)
            goto L_0x0c3f
        L_0x0CLASSNAME:
            r73 = r6
            r11 = r7
            r40 = r8
            r17 = r23
            r70 = r24
            r18 = r26
            goto L_0x0c2b
        L_0x0c1e:
            r73 = r6
            r11 = r7
            r40 = r8
            r17 = r23
            r70 = r24
            r18 = r26
            r10 = r29
        L_0x0c2b:
            r72 = r32
            r9 = r35
            r24 = r36
            r8 = r44
            r74 = r48
            r71 = r49
            r20 = 1
            r21 = 0
            r23 = 27
            r29 = 0
        L_0x0c3f:
            if (r30 != 0) goto L_0x0CLASSNAME
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
            r15.addPerson(r0)
        L_0x0CLASSNAME:
            r1 = 26
            r2 = r27
            if (r10 < r1) goto L_0x0c6c
            r11.setNotificationChannel(r2, r15, r9)
        L_0x0c6c:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            int r3 = r22.intValue()
            android.app.Notification r4 = r15.build()
            r0.<init>(r3, r4)
            r3 = r42
            r3.add(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r11.wearNotificationsIds
            r4 = r22
            r0.put(r13, r4)
            if (r12 == 0) goto L_0x0d05
            r4 = r74
            if (r4 == 0) goto L_0x0d05
            java.lang.String r0 = "reply"
            r5 = r50
            r4.put(r0, r5)     // Catch:{ JSONException -> 0x0d05 }
            java.lang.String r0 = "name"
            r5 = r73
            r4.put(r0, r5)     // Catch:{ JSONException -> 0x0d05 }
            r5 = r53
            r6 = r55
            r4.put(r6, r5)     // Catch:{ JSONException -> 0x0d05 }
            java.lang.String r0 = "max_date"
            r5 = r40
            r4.put(r0, r5)     // Catch:{ JSONException -> 0x0d05 }
            int r0 = java.lang.Math.abs(r12)     // Catch:{ JSONException -> 0x0d05 }
            r5 = r70
            r4.put(r5, r0)     // Catch:{ JSONException -> 0x0d05 }
            r5 = r71
            if (r5 == 0) goto L_0x0cd9
            java.lang.String r0 = "photo"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0d05 }
            r6.<init>()     // Catch:{ JSONException -> 0x0d05 }
            int r7 = r5.dc_id     // Catch:{ JSONException -> 0x0d05 }
            r6.append(r7)     // Catch:{ JSONException -> 0x0d05 }
            r7 = r33
            r6.append(r7)     // Catch:{ JSONException -> 0x0d05 }
            long r13 = r5.volume_id     // Catch:{ JSONException -> 0x0d05 }
            r6.append(r13)     // Catch:{ JSONException -> 0x0d05 }
            r6.append(r7)     // Catch:{ JSONException -> 0x0d05 }
            long r7 = r5.secret     // Catch:{ JSONException -> 0x0d05 }
            r6.append(r7)     // Catch:{ JSONException -> 0x0d05 }
            java.lang.String r5 = r6.toString()     // Catch:{ JSONException -> 0x0d05 }
            r4.put(r0, r5)     // Catch:{ JSONException -> 0x0d05 }
        L_0x0cd9:
            if (r19 == 0) goto L_0x0ce2
            java.lang.String r0 = "msgs"
            r5 = r19
            r4.put(r0, r5)     // Catch:{ JSONException -> 0x0d05 }
        L_0x0ce2:
            java.lang.String r0 = "type"
            if (r12 <= 0) goto L_0x0ced
            java.lang.String r5 = "user"
            r4.put(r0, r5)     // Catch:{ JSONException -> 0x0d05 }
            goto L_0x0cff
        L_0x0ced:
            if (r12 >= 0) goto L_0x0cff
            if (r39 != 0) goto L_0x0cfa
            if (r38 == 0) goto L_0x0cf4
            goto L_0x0cfa
        L_0x0cf4:
            java.lang.String r5 = "group"
            r4.put(r0, r5)     // Catch:{ JSONException -> 0x0d05 }
            goto L_0x0cff
        L_0x0cfa:
            java.lang.String r5 = "channel"
            r4.put(r0, r5)     // Catch:{ JSONException -> 0x0d05 }
        L_0x0cff:
            r7 = r72
            r7.put(r4)     // Catch:{ JSONException -> 0x0d07 }
            goto L_0x0d07
        L_0x0d05:
            r7 = r72
        L_0x0d07:
            int r0 = r24 + 1
            r15 = r3
            r6 = r7
            r3 = r9
            r9 = r10
            r8 = r11
            r5 = r18
            r12 = r25
            r11 = r28
            r14 = r31
            r1 = r37
            r13 = 0
            r7 = r0
            r10 = r2
            r2 = r17
            goto L_0x00e1
        L_0x0d1f:
            r37 = r1
            r9 = r3
            r5 = r4
            r7 = r6
            r11 = r8
            r2 = r10
            r31 = r14
            r3 = r15
            r21 = 0
            if (r9 == 0) goto L_0x0d4f
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0d47
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r11.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0d47:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r11.notificationId
            r0.notify(r1, r2)
            goto L_0x0d5e
        L_0x0d4f:
            java.util.HashSet<java.lang.Long> r0 = r11.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0d5e
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r11.notificationId
            r0.cancel(r1)
        L_0x0d5e:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r3.size()
            r0.<init>(r1)
            int r1 = r3.size()
            r4 = 0
        L_0x0d6c:
            if (r4 >= r1) goto L_0x0d89
            java.lang.Object r2 = r3.get(r4)
            org.telegram.messenger.NotificationsController$1NotificationHolder r2 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r2
            r2.call()
            boolean r6 = r77.unsupportedNotificationShortcut()
            if (r6 != 0) goto L_0x0d86
            android.app.Notification r2 = r2.notification
            java.lang.String r2 = r2.getShortcutId()
            r0.add(r2)
        L_0x0d86:
            int r4 = r4 + 1
            goto L_0x0d6c
        L_0x0d89:
            boolean r1 = r77.unsupportedNotificationShortcut()
            if (r1 != 0) goto L_0x0d99
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d95 }
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r1, r0)     // Catch:{ Exception -> 0x0d95 }
            goto L_0x0d99
        L_0x0d95:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0d99:
            r13 = 0
        L_0x0d9a:
            int r0 = r31.size()
            if (r13 >= r0) goto L_0x0ddf
            r1 = r31
            long r2 = r1.keyAt(r13)
            java.util.HashSet<java.lang.Long> r0 = r11.openedInBubbleDialogs
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x0db3
            goto L_0x0dda
        L_0x0db3:
            java.lang.Object r0 = r1.valueAt(r13)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0dd1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "cancel notification id "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0dd1:
            androidx.core.app.NotificationManagerCompat r2 = notificationManager
            int r0 = r0.intValue()
            r2.cancel(r0)
        L_0x0dda:
            int r13 = r13 + 1
            r31 = r1
            goto L_0x0d9a
        L_0x0ddf:
            if (r7 == 0) goto L_0x0dff
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0dff }
            r0.<init>()     // Catch:{ Exception -> 0x0dff }
            r1 = r37
            r0.put(r5, r1)     // Catch:{ Exception -> 0x0dff }
            java.lang.String r1 = "n"
            r0.put(r1, r7)     // Catch:{ Exception -> 0x0dff }
            java.lang.String r1 = "/notify"
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0dff }
            byte[] r0 = r0.getBytes()     // Catch:{ Exception -> 0x0dff }
            java.lang.String r2 = "remote_notifications"
            org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r1, r0, r2)     // Catch:{ Exception -> 0x0dff }
        L_0x0dff:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, java.lang.String):void");
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
