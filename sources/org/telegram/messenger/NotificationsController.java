package org.telegram.messenger;

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
import android.provider.Settings;
import android.text.TextUtils;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.content.LocusIdCompat;
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
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
    public static long globalSecretChatId = DialogObject.makeEncryptedDialogId(1);
    /* access modifiers changed from: private */
    public static NotificationManagerCompat notificationManager;
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    private static NotificationManager systemNotificationManager;
    private AlarmManager alarmManager;
    private boolean channelGroupsCreated;
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
    private long openedDialogId = 0;
    private HashSet<Long> openedInBubbleDialogs = new HashSet<>();
    private int personalCount = 0;
    public ArrayList<MessageObject> popupMessages = new ArrayList<>();
    public ArrayList<MessageObject> popupReplyMessages = new ArrayList<>();
    private LongSparseArray<Integer> pushDialogs = new LongSparseArray<>();
    private LongSparseArray<Integer> pushDialogsOverrideMention = new LongSparseArray<>();
    private ArrayList<MessageObject> pushMessages = new ArrayList<>();
    private LongSparseArray<SparseArray<MessageObject>> pushMessagesDict = new LongSparseArray<>();
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

    static {
        notificationManager = null;
        systemNotificationManager = null;
        if (Build.VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
            notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
            checkOtherNotificationsChannel();
        }
    }

    public static NotificationsController getInstance(int num) {
        NotificationsController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (NotificationsController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    NotificationsController[] notificationsControllerArr = Instance;
                    NotificationsController notificationsController = new NotificationsController(num);
                    localInstance = notificationsController;
                    notificationsControllerArr[num] = notificationsController;
                }
            }
        }
        return localInstance;
    }

    public NotificationsController(int instance) {
        super(instance);
        StringBuilder sb = new StringBuilder();
        sb.append("messages");
        sb.append(this.currentAccount == 0 ? "" : Integer.valueOf(this.currentAccount));
        this.notificationGroup = sb.toString();
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        this.inChatSoundEnabled = preferences.getBoolean("EnableInChatSound", true);
        this.showBadgeNumber = preferences.getBoolean("badgeNumber", true);
        this.showBadgeMuted = preferences.getBoolean("badgeNumberMuted", false);
        this.showBadgeMessages = preferences.getBoolean("badgeNumberMessages", true);
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
        this.notificationDelayRunnable = new NotificationsController$$ExternalSyntheticLambda40(this);
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1102lambda$new$0$orgtelegrammessengerNotificationsController() {
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
        if (Build.VERSION.SDK_INT >= 26) {
            SharedPreferences preferences = null;
            if (OTHER_NOTIFICATIONS_CHANNEL == null) {
                preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                OTHER_NOTIFICATIONS_CHANNEL = preferences.getString("OtherKey", "Other3");
            }
            NotificationChannel notificationChannel = systemNotificationManager.getNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
            if (notificationChannel != null && notificationChannel.getImportance() == 0) {
                systemNotificationManager.deleteNotificationChannel(OTHER_NOTIFICATIONS_CHANNEL);
                OTHER_NOTIFICATIONS_CHANNEL = null;
                notificationChannel = null;
            }
            if (OTHER_NOTIFICATIONS_CHANNEL == null) {
                if (preferences == null) {
                    preferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                }
                OTHER_NOTIFICATIONS_CHANNEL = "Other" + Utilities.random.nextLong();
                preferences.edit().putString("OtherKey", OTHER_NOTIFICATIONS_CHANNEL).commit();
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
        this.channelGroupsCreated = false;
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda36(this));
    }

    /* renamed from: lambda$cleanup$1$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1095lambda$cleanup$1$orgtelegrammessengerNotificationsController() {
        this.openedDialogId = 0;
        this.total_unread_count = 0;
        this.personalCount = 0;
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
        SharedPreferences.Editor editor = getAccountInstance().getNotificationsSettings().edit();
        editor.clear();
        editor.commit();
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                systemNotificationManager.deleteNotificationChannelGroup("channels" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("groups" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("private" + this.currentAccount);
                systemNotificationManager.deleteNotificationChannelGroup("other" + this.currentAccount);
                String keyStart = this.currentAccount + "channel";
                List<NotificationChannel> list = systemNotificationManager.getNotificationChannels();
                int count = list.size();
                for (int a = 0; a < count; a++) {
                    String id = list.get(a).getId();
                    if (id.startsWith(keyStart)) {
                        systemNotificationManager.deleteNotificationChannel(id);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel cleanup " + id);
                        }
                    }
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            } catch (Throwable e3) {
                FileLog.e(e3);
            }
        }
    }

    public void setInChatSoundEnabled(boolean value) {
        this.inChatSoundEnabled = value;
    }

    /* renamed from: lambda$setOpenedDialogId$2$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1124x8a2b000c(long dialog_id) {
        this.openedDialogId = dialog_id;
    }

    public void setOpenedDialogId(long dialog_id) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda14(this, dialog_id));
    }

    public void setOpenedInBubble(long dialogId, boolean opened) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda30(this, opened, dialogId));
    }

    /* renamed from: lambda$setOpenedInBubble$3$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1125x8CLASSNAMEe1d(boolean opened, long dialogId) {
        if (opened) {
            this.openedInBubbleDialogs.add(Long.valueOf(dialogId));
        } else {
            this.openedInBubbleDialogs.remove(Long.valueOf(dialogId));
        }
    }

    public void setLastOnlineFromOtherDevice(int time) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda12(this, time));
    }

    /* renamed from: lambda$setLastOnlineFromOtherDevice$4$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1123xfb3acdad(int time) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set last online from other device = " + time);
        }
        this.lastOnlineFromOtherDevice = time;
    }

    public void removeNotificationsForDialog(long did) {
        processReadMessages((LongSparseIntArray) null, did, 0, Integer.MAX_VALUE, false);
        LongSparseIntArray dialogsToUpdate = new LongSparseIntArray();
        dialogsToUpdate.put(did, 0);
        processDialogsUpdateRead(dialogsToUpdate);
    }

    public boolean hasMessagesToReply() {
        for (int a = 0; a < this.pushMessages.size(); a++) {
            MessageObject messageObject = this.pushMessages.get(a);
            long dialog_id = messageObject.getDialogId();
            if ((!messageObject.messageOwner.mentioned || !(messageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialog_id) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda38(this));
    }

    /* renamed from: lambda$forceShowPopupForReply$6$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1100x16c2e2d7() {
        ArrayList<MessageObject> popupArray = new ArrayList<>();
        for (int a = 0; a < this.pushMessages.size(); a++) {
            MessageObject messageObject = this.pushMessages.get(a);
            long dialog_id = messageObject.getDialogId();
            if ((!messageObject.messageOwner.mentioned || !(messageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialog_id) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                popupArray.add(0, messageObject);
            }
        }
        if (popupArray.isEmpty() == 0 && !AndroidUtilities.needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda18(this, popupArray));
        }
    }

    /* renamed from: lambda$forceShowPopupForReply$5$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1099xdcvar_f8(ArrayList popupArray) {
        this.popupReplyMessages = popupArray;
        Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
        popupIntent.putExtra("force", true);
        popupIntent.putExtra("currentAccount", this.currentAccount);
        popupIntent.setFlags(NUM);
        ApplicationLoader.applicationContext.startActivity(popupIntent);
        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    public void removeDeletedMessagesFromNotifications(LongSparseArray<ArrayList<Integer>> deletedMessages) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda17(this, deletedMessages, new ArrayList<>(0)));
    }

    /* renamed from: lambda$removeDeletedMessagesFromNotifications$9$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1121x6483a23c(LongSparseArray deletedMessages, ArrayList popupArrayRemove) {
        long key;
        Integer newCount;
        LongSparseArray longSparseArray = deletedMessages;
        ArrayList arrayList = popupArrayRemove;
        int old_unread_count = this.total_unread_count;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        int a = 0;
        while (true) {
            int i = 0;
            if (a >= deletedMessages.size()) {
                break;
            }
            long key2 = longSparseArray.keyAt(a);
            SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(key2);
            if (sparseArray != null) {
                ArrayList<Integer> mids = (ArrayList) longSparseArray.get(key2);
                int b = 0;
                int N = mids.size();
                while (b < N) {
                    int mid = mids.get(b).intValue();
                    MessageObject messageObject = sparseArray.get(mid);
                    if (messageObject != null) {
                        key = key2;
                        long dialogId = messageObject.getDialogId();
                        Integer currentCount = this.pushDialogs.get(dialogId);
                        if (currentCount == null) {
                            currentCount = Integer.valueOf(i);
                        }
                        Integer newCount2 = Integer.valueOf(currentCount.intValue() - 1);
                        if (newCount2.intValue() <= 0) {
                            Integer newCount3 = Integer.valueOf(i);
                            this.smartNotificationsDialogs.remove(dialogId);
                            newCount = newCount3;
                        } else {
                            newCount = newCount2;
                        }
                        if (!newCount.equals(currentCount)) {
                            int intValue = this.total_unread_count - currentCount.intValue();
                            this.total_unread_count = intValue;
                            this.total_unread_count = intValue + newCount.intValue();
                            this.pushDialogs.put(dialogId, newCount);
                        }
                        if (newCount.intValue() == 0) {
                            this.pushDialogs.remove(dialogId);
                            this.pushDialogsOverrideMention.remove(dialogId);
                        }
                        sparseArray.remove(mid);
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList.add(messageObject);
                    } else {
                        key = key2;
                    }
                    b++;
                    LongSparseArray longSparseArray2 = deletedMessages;
                    key2 = key;
                    i = 0;
                }
                long key3 = key2;
                if (sparseArray.size() == 0) {
                    this.pushMessagesDict.remove(key3);
                }
            }
            a++;
            longSparseArray = deletedMessages;
        }
        if (!popupArrayRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda23(this, arrayList));
        }
        if (old_unread_count != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
            }
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda10(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* renamed from: lambda$removeDeletedMessagesFromNotifications$7$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1119xf0ee5e7e(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* renamed from: lambda$removeDeletedMessagesFromNotifications$8$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1120x2ab9005d(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void removeDeletedHisoryFromNotifications(LongSparseIntArray deletedMessages) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda28(this, deletedMessages, new ArrayList<>(0)));
    }

    /* renamed from: lambda$removeDeletedHisoryFromNotifications$12$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1118xCLASSNAMEf6e8(LongSparseIntArray deletedMessages, ArrayList popupArrayRemove) {
        long key;
        int i;
        LongSparseIntArray longSparseIntArray = deletedMessages;
        ArrayList arrayList = popupArrayRemove;
        int old_unread_count = this.total_unread_count;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        int a = 0;
        while (a < deletedMessages.size()) {
            long key2 = longSparseIntArray.keyAt(a);
            long dialogId = -key2;
            long id = (long) longSparseIntArray.get(key2);
            Integer currentCount = this.pushDialogs.get(dialogId);
            if (currentCount == null) {
                currentCount = 0;
            }
            Integer newCount = currentCount;
            int c = 0;
            while (c < this.pushMessages.size()) {
                MessageObject messageObject = this.pushMessages.get(c);
                if (messageObject.getDialogId() == dialogId) {
                    key = key2;
                    if (((long) messageObject.getId()) <= id) {
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(dialogId);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(dialogId);
                            }
                        }
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        c--;
                        if (isPersonalMessage(messageObject)) {
                            i = 1;
                            this.personalCount--;
                        } else {
                            i = 1;
                        }
                        arrayList.add(messageObject);
                        newCount = Integer.valueOf(newCount.intValue() - i);
                    } else {
                        i = 1;
                    }
                } else {
                    key = key2;
                    i = 1;
                }
                c += i;
                LongSparseIntArray longSparseIntArray2 = deletedMessages;
                key2 = key;
            }
            if (newCount.intValue() <= 0) {
                newCount = 0;
                this.smartNotificationsDialogs.remove(dialogId);
            }
            if (!newCount.equals(currentCount)) {
                int intValue = this.total_unread_count - currentCount.intValue();
                this.total_unread_count = intValue;
                this.total_unread_count = intValue + newCount.intValue();
                this.pushDialogs.put(dialogId, newCount);
            }
            if (newCount.intValue() == 0) {
                this.pushDialogs.remove(dialogId);
                this.pushDialogsOverrideMention.remove(dialogId);
            }
            a++;
            longSparseIntArray = deletedMessages;
        }
        if (popupArrayRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda21(this, arrayList));
        }
        if (old_unread_count != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
            }
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda9(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* renamed from: lambda$removeDeletedHisoryFromNotifications$10$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1116x4db2b32a(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* renamed from: lambda$removeDeletedHisoryFromNotifications$11$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1117x877d5509(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void processReadMessages(LongSparseIntArray inbox, long dialogId, int maxDate, int maxId, boolean isPopup) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda29(this, inbox, new ArrayList<>(0), dialogId, maxId, maxDate, isPopup));
    }

    /* renamed from: lambda$processReadMessages$14$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1115x6297dvar_(LongSparseIntArray inbox, ArrayList popupArrayRemove, long dialogId, int maxId, int maxDate, boolean isPopup) {
        long did;
        long did2;
        LongSparseIntArray longSparseIntArray = inbox;
        ArrayList arrayList = popupArrayRemove;
        int i = maxId;
        int i2 = maxDate;
        long j = 0;
        if (longSparseIntArray != null) {
            int b = 0;
            while (b < inbox.size()) {
                long key = longSparseIntArray.keyAt(b);
                int messageId = longSparseIntArray.get(key);
                int a = 0;
                while (a < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(a);
                    if (!messageObject.messageOwner.from_scheduled && messageObject.getDialogId() == key && messageObject.getId() <= messageId) {
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList.add(messageObject);
                        if (messageObject.messageOwner.peer_id.channel_id != j) {
                            did2 = -messageObject.messageOwner.peer_id.channel_id;
                        } else {
                            did2 = 0;
                        }
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(did2);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(did2);
                            }
                        }
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(a);
                        a--;
                    }
                    a++;
                    j = 0;
                }
                b++;
                j = 0;
            }
        }
        if (!(dialogId == 0 || (i == 0 && i2 == 0))) {
            int a2 = 0;
            while (a2 < this.pushMessages.size()) {
                MessageObject messageObject2 = this.pushMessages.get(a2);
                if (messageObject2.getDialogId() == dialogId) {
                    boolean remove = false;
                    if (i2 != 0) {
                        if (messageObject2.messageOwner.date <= i2) {
                            remove = true;
                        }
                    } else if (!isPopup) {
                        if (messageObject2.getId() <= i || i < 0) {
                            remove = true;
                        }
                    } else if (messageObject2.getId() == i || i < 0) {
                        remove = true;
                    }
                    if (remove) {
                        if (isPersonalMessage(messageObject2)) {
                            this.personalCount--;
                        }
                        if (messageObject2.messageOwner.peer_id.channel_id != 0) {
                            did = -messageObject2.messageOwner.peer_id.channel_id;
                        } else {
                            did = 0;
                        }
                        SparseArray<MessageObject> sparseArray2 = this.pushMessagesDict.get(did);
                        if (sparseArray2 != null) {
                            sparseArray2.remove(messageObject2.getId());
                            if (sparseArray2.size() == 0) {
                                this.pushMessagesDict.remove(did);
                            }
                        }
                        this.pushMessages.remove(a2);
                        this.delayedPushMessages.remove(messageObject2);
                        arrayList.add(messageObject2);
                        a2--;
                    }
                }
                a2++;
            }
        }
        if (popupArrayRemove.isEmpty() == 0) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda20(this, arrayList));
        }
    }

    /* renamed from: lambda$processReadMessages$13$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1114x28cd3d59(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    private int addToPopupMessages(ArrayList<MessageObject> popupArrayAdd, MessageObject messageObject, long dialogId, boolean isChannel, SharedPreferences preferences) {
        int popup = 0;
        if (!DialogObject.isEncryptedDialog(dialogId)) {
            if (preferences.getBoolean("custom_" + dialogId, false)) {
                popup = preferences.getInt("popup_" + dialogId, 0);
            }
            if (popup == 0) {
                if (isChannel) {
                    popup = preferences.getInt("popupChannel", 0);
                } else {
                    popup = preferences.getInt(DialogObject.isChatDialog(dialogId) ? "popupGroup" : "popupAll", 0);
                }
            } else if (popup == 1) {
                popup = 3;
            } else if (popup == 2) {
                popup = 0;
            }
        }
        if (!(popup == 0 || messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
            popup = 0;
        }
        if (popup != 0) {
            popupArrayAdd.add(0, messageObject);
        }
        return popup;
    }

    public void processEditedMessages(LongSparseArray<ArrayList<MessageObject>> editedMessages) {
        if (editedMessages.size() != 0) {
            new ArrayList(0);
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda16(this, editedMessages));
        }
    }

    /* renamed from: lambda$processEditedMessages$15$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1108xd706556a(LongSparseArray editedMessages) {
        long did;
        LongSparseArray longSparseArray = editedMessages;
        boolean updated = false;
        int a = 0;
        int N = editedMessages.size();
        while (a < N) {
            if (this.pushDialogs.indexOfKey(longSparseArray.keyAt(a)) >= 0) {
                ArrayList<MessageObject> messages = (ArrayList) longSparseArray.valueAt(a);
                int b = 0;
                int N2 = messages.size();
                while (b < N2) {
                    MessageObject messageObject = messages.get(b);
                    if (messageObject.messageOwner.peer_id.channel_id != 0) {
                        did = -messageObject.messageOwner.peer_id.channel_id;
                    } else {
                        did = 0;
                    }
                    SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(did);
                    if (sparseArray == null) {
                        break;
                    }
                    MessageObject oldMessage = sparseArray.get(messageObject.getId());
                    if (oldMessage != null) {
                        updated = true;
                        sparseArray.put(messageObject.getId(), messageObject);
                        int idx = this.pushMessages.indexOf(oldMessage);
                        if (idx >= 0) {
                            this.pushMessages.set(idx, messageObject);
                        }
                        int idx2 = this.delayedPushMessages.indexOf(oldMessage);
                        if (idx2 >= 0) {
                            this.delayedPushMessages.set(idx2, messageObject);
                        }
                    }
                    b++;
                    LongSparseArray longSparseArray2 = editedMessages;
                }
            }
            a++;
            longSparseArray = editedMessages;
        }
        if (updated) {
            showOrUpdateNotification(false);
        }
    }

    public void processNewMessages(ArrayList<MessageObject> messageObjects, boolean isLast, boolean isFcm, CountDownLatch countDownLatch) {
        if (!messageObjects.isEmpty()) {
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda26(this, messageObjects, new ArrayList<>(0), isFcm, isLast, countDownLatch));
        } else if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    /* renamed from: lambda$processNewMessages$18$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1113xffba819a(ArrayList messageObjects, ArrayList popupArrayAdd, boolean isFcm, boolean isLast, CountDownLatch countDownLatch) {
        Boolean isChannel;
        boolean canAddValue;
        Integer override;
        LongSparseArray<Boolean> settingsCache;
        boolean allowPinned;
        int a;
        boolean added;
        int popup;
        long randomId;
        boolean isChannel2;
        long did;
        long randomId2;
        boolean value;
        SparseArray<MessageObject> sparseArray;
        long dialogId;
        boolean edited;
        boolean edited2;
        long dialogId2;
        SparseArray<MessageObject> sparseArray2;
        LongSparseArray<Boolean> settingsCache2;
        boolean value2;
        SparseArray<MessageObject> sparseArray3;
        boolean added2;
        int popup2;
        MessageObject messageObject;
        ArrayList arrayList = messageObjects;
        LongSparseArray<Boolean> settingsCache3 = new LongSparseArray<>();
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        boolean allowPinned2 = preferences.getBoolean("PinnedMessages", true);
        boolean messageObject2 = false;
        boolean edited3 = false;
        int mid = 0;
        boolean hasScheduled = false;
        int a2 = 0;
        while (a2 < messageObjects.size()) {
            MessageObject messageObject3 = (MessageObject) arrayList.get(a2);
            if (messageObject3.messageOwner != null) {
                if (!messageObject3.isImportedForward() && !(messageObject3.messageOwner.action instanceof TLRPC.TL_messageActionSetMessagesTTL)) {
                    if (messageObject3.messageOwner.silent) {
                        if (!(messageObject3.messageOwner.action instanceof TLRPC.TL_messageActionContactSignUp)) {
                            if (messageObject3.messageOwner.action instanceof TLRPC.TL_messageActionUserJoined) {
                                a = a2;
                                settingsCache = settingsCache3;
                                allowPinned = allowPinned2;
                                added = messageObject2;
                                popup = mid;
                                mid = popup;
                                messageObject2 = added;
                                a2 = a + 1;
                                arrayList = messageObjects;
                                allowPinned2 = allowPinned;
                                settingsCache3 = settingsCache;
                            }
                        }
                    }
                }
                a = a2;
                settingsCache = settingsCache3;
                allowPinned = allowPinned2;
                added = messageObject2;
                popup = mid;
                mid = popup;
                messageObject2 = added;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache3 = settingsCache;
            }
            int mid2 = messageObject3.getId();
            if (messageObject3.isFcmMessage()) {
                a = a2;
                randomId = messageObject3.messageOwner.random_id;
            } else {
                a = a2;
                randomId = 0;
            }
            allowPinned = allowPinned2;
            long dialogId3 = messageObject3.getDialogId();
            if (messageObject3.isFcmMessage()) {
                isChannel2 = messageObject3.localChannel;
            } else if (DialogObject.isChatDialog(dialogId3)) {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-dialogId3));
                isChannel2 = ChatObject.isChannel(chat) && !chat.megagroup;
            } else {
                isChannel2 = false;
            }
            if (messageObject3.messageOwner.peer_id.channel_id != 0) {
                did = -messageObject3.messageOwner.peer_id.channel_id;
            } else {
                did = 0;
            }
            SparseArray<MessageObject> sparseArray4 = this.pushMessagesDict.get(did);
            MessageObject oldMessageObject = sparseArray4 != null ? sparseArray4.get(mid2) : null;
            if (oldMessageObject == null) {
                randomId2 = randomId;
                if (messageObject3.messageOwner.random_id != 0) {
                    settingsCache = settingsCache3;
                    oldMessageObject = this.fcmRandomMessagesDict.get(messageObject3.messageOwner.random_id);
                    if (oldMessageObject != null) {
                        this.fcmRandomMessagesDict.remove(messageObject3.messageOwner.random_id);
                    }
                } else {
                    settingsCache = settingsCache3;
                }
            } else {
                randomId2 = randomId;
                settingsCache = settingsCache3;
            }
            MessageObject oldMessageObject2 = oldMessageObject;
            if (oldMessageObject2 == null) {
                added = messageObject2;
                popup = mid;
                long randomId3 = randomId2;
                long did2 = did;
                MessageObject messageObject4 = messageObject3;
                int mid3 = mid2;
                if (!edited3) {
                    if (isFcm) {
                        getMessagesStorage().putPushMessage(messageObject4);
                    }
                    long originalDialogId = dialogId3;
                    if (dialogId3 != this.openedDialogId || !ApplicationLoader.isScreenOn) {
                        if (messageObject4.messageOwner.mentioned) {
                            if (allowPinned || !(messageObject4.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)) {
                                dialogId3 = messageObject4.getFromChatId();
                            }
                        }
                        if (isPersonalMessage(messageObject4)) {
                            this.personalCount++;
                        }
                        boolean isChatDialog = DialogObject.isChatDialog(dialogId3);
                        LongSparseArray<Boolean> settingsCache4 = settingsCache;
                        int index = settingsCache4.indexOfKey(dialogId3);
                        if (index >= 0) {
                            sparseArray = sparseArray4;
                            value = settingsCache4.valueAt(index).booleanValue();
                        } else {
                            int notifyOverride = getNotifyOverride(preferences, dialogId3);
                            if (notifyOverride == -1) {
                                value2 = isGlobalNotificationsEnabled(dialogId3, Boolean.valueOf(isChannel2));
                            } else {
                                value2 = notifyOverride != 2;
                            }
                            sparseArray = sparseArray4;
                            settingsCache4.put(dialogId3, Boolean.valueOf(value2));
                            value = value2;
                        }
                        if (value) {
                            if (!isFcm) {
                                settingsCache2 = settingsCache4;
                                int i = index;
                                long j = dialogId3;
                                dialogId = dialogId3;
                                dialogId2 = originalDialogId;
                                edited = edited3;
                                edited2 = false;
                                popup = addToPopupMessages(popupArrayAdd, messageObject4, j, isChannel2, preferences);
                            } else {
                                settingsCache2 = settingsCache4;
                                int i2 = index;
                                dialogId = dialogId3;
                                edited = edited3;
                                edited2 = false;
                                dialogId2 = originalDialogId;
                            }
                            if (!hasScheduled) {
                                hasScheduled = messageObject4.messageOwner.from_scheduled;
                            }
                            this.delayedPushMessages.add(messageObject4);
                            this.pushMessages.add(edited2, messageObject4);
                            if (mid3 != 0) {
                                if (sparseArray == null) {
                                    sparseArray2 = new SparseArray<>();
                                    this.pushMessagesDict.put(did2, sparseArray2);
                                } else {
                                    sparseArray2 = sparseArray;
                                }
                                sparseArray2.put(mid3, messageObject4);
                                SparseArray<MessageObject> sparseArray5 = sparseArray2;
                                long j2 = randomId3;
                            } else {
                                long randomId4 = randomId3;
                                if (randomId4 != 0) {
                                    this.fcmRandomMessagesDict.put(randomId4, messageObject4);
                                }
                            }
                            if (dialogId2 != dialogId) {
                                Integer current = this.pushDialogsOverrideMention.get(dialogId2);
                                this.pushDialogsOverrideMention.put(dialogId2, Integer.valueOf(current == null ? 1 : current.intValue() + 1));
                            }
                            edited3 = edited;
                            messageObject2 = true;
                            mid = popup;
                        } else {
                            settingsCache = settingsCache4;
                            int i3 = index;
                            long j3 = dialogId3;
                            boolean z = edited3;
                            long j4 = randomId3;
                            long dialogId4 = originalDialogId;
                            messageObject2 = true;
                            mid = popup;
                        }
                        a2 = a + 1;
                        arrayList = messageObjects;
                        allowPinned2 = allowPinned;
                        settingsCache3 = settingsCache;
                    } else if (!isFcm) {
                        playInChatSound();
                    }
                }
                mid = popup;
                messageObject2 = added;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache3 = settingsCache;
            } else if (oldMessageObject2.isFcmMessage()) {
                if (sparseArray4 == null) {
                    SparseArray<MessageObject> sparseArray6 = new SparseArray<>();
                    this.pushMessagesDict.put(did, sparseArray6);
                    sparseArray3 = sparseArray6;
                } else {
                    sparseArray3 = sparseArray4;
                }
                sparseArray3.put(mid2, messageObject3);
                int idxOld = this.pushMessages.indexOf(oldMessageObject2);
                if (idxOld >= 0) {
                    this.pushMessages.set(idxOld, messageObject3);
                    MessageObject messageObject5 = oldMessageObject2;
                    SparseArray<MessageObject> sparseArray7 = sparseArray3;
                    long j5 = did;
                    int i4 = mid;
                    int popup3 = mid2;
                    added2 = messageObject2;
                    long j6 = randomId2;
                    messageObject = messageObject3;
                    int i5 = idxOld;
                    popup2 = addToPopupMessages(popupArrayAdd, messageObject3, dialogId3, isChannel2, preferences);
                } else {
                    MessageObject messageObject6 = oldMessageObject2;
                    SparseArray<MessageObject> sparseArray8 = sparseArray3;
                    added2 = messageObject2;
                    popup2 = mid;
                    long j7 = randomId2;
                    long j8 = did;
                    messageObject = messageObject3;
                    int popup4 = mid2;
                }
                if (isFcm) {
                    boolean z2 = messageObject.localEdit;
                    edited3 = z2;
                    if (z2) {
                        getMessagesStorage().putPushMessage(messageObject);
                    }
                }
                mid = popup2;
                messageObject2 = added2;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache3 = settingsCache;
            } else {
                added = messageObject2;
                popup = mid;
                long j9 = randomId2;
                long j10 = did;
                MessageObject messageObject7 = messageObject3;
                int popup5 = mid2;
                mid = popup;
                messageObject2 = added;
                a2 = a + 1;
                arrayList = messageObjects;
                allowPinned2 = allowPinned;
                settingsCache3 = settingsCache;
            }
        }
        int i6 = a2;
        LongSparseArray<Boolean> longSparseArray = settingsCache3;
        boolean z3 = allowPinned2;
        boolean added3 = messageObject2;
        boolean edited4 = edited3;
        int popup6 = mid;
        if (added3) {
            this.notifyCheck = isLast;
        } else {
            boolean z4 = isLast;
        }
        if (popupArrayAdd.isEmpty() || AndroidUtilities.needShowPasscode() || SharedConfig.isWaitingForPasscodeEnter) {
            ArrayList arrayList2 = popupArrayAdd;
        } else {
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda24(this, popupArrayAdd, popup6));
        }
        if (!isFcm && !hasScheduled) {
            ArrayList arrayList3 = messageObjects;
        } else if (edited4) {
            this.delayedPushMessages.clear();
            showOrUpdateNotification(this.notifyCheck);
            ArrayList arrayList4 = messageObjects;
        } else if (added3) {
            MessageObject messageObject8 = (MessageObject) messageObjects.get(0);
            long dialog_id = messageObject8.getDialogId();
            if (messageObject8.isFcmMessage()) {
                isChannel = Boolean.valueOf(messageObject8.localChannel);
            } else {
                isChannel = null;
            }
            int old_unread_count = this.total_unread_count;
            int notifyOverride2 = getNotifyOverride(preferences, dialog_id);
            if (notifyOverride2 == -1) {
                canAddValue = isGlobalNotificationsEnabled(dialog_id, isChannel);
            } else {
                canAddValue = notifyOverride2 != 2;
            }
            Integer currentCount = this.pushDialogs.get(dialog_id);
            int newCount = currentCount != null ? currentCount.intValue() + 1 : 1;
            if (this.notifyCheck && !canAddValue && (override = this.pushDialogsOverrideMention.get(dialog_id)) != null && override.intValue() != 0) {
                canAddValue = true;
                newCount = override.intValue();
            }
            if (canAddValue) {
                if (currentCount != null) {
                    this.total_unread_count -= currentCount.intValue();
                }
                this.total_unread_count += newCount;
                this.pushDialogs.put(dialog_id, Integer.valueOf(newCount));
            }
            if (old_unread_count != this.total_unread_count) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
                AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda8(this, this.pushDialogs.size()));
            }
            this.notifyCheck = false;
            if (this.showBadgeNumber) {
                setBadge(getTotalAllUnreadCount());
            }
        } else {
            ArrayList arrayList5 = messageObjects;
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    /* renamed from: lambda$processNewMessages$16$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1111x8CLASSNAMEddc(ArrayList popupArrayAdd, int popupFinal) {
        this.popupMessages.addAll(0, popupArrayAdd);
        if (!ApplicationLoader.mainInterfacePaused && ApplicationLoader.isScreenOn) {
            return;
        }
        if (popupFinal == 3 || ((popupFinal == 1 && ApplicationLoader.isScreenOn) || (popupFinal == 2 && !ApplicationLoader.isScreenOn))) {
            Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
            popupIntent.setFlags(NUM);
            try {
                ApplicationLoader.applicationContext.startActivity(popupIntent);
            } catch (Throwable th) {
            }
        }
    }

    /* renamed from: lambda$processNewMessages$17$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1112xc5efdfbb(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(LongSparseIntArray dialogsToUpdate) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda27(this, dialogsToUpdate, new ArrayList<>()));
    }

    /* renamed from: lambda$processDialogsUpdateRead$21$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1107xc2d50a00(LongSparseIntArray dialogsToUpdate, ArrayList popupArrayToRemove) {
        boolean z;
        long dialogId;
        long did;
        Integer override;
        TLRPC.Chat chat;
        LongSparseIntArray longSparseIntArray = dialogsToUpdate;
        ArrayList arrayList = popupArrayToRemove;
        int old_unread_count = this.total_unread_count;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        int b = 0;
        while (true) {
            boolean canAddValue = false;
            z = true;
            if (b >= dialogsToUpdate.size()) {
                break;
            }
            long dialogId2 = longSparseIntArray.keyAt(b);
            Integer currentCount = this.pushDialogs.get(dialogId2);
            int newCount = longSparseIntArray.get(dialogId2);
            if (DialogObject.isChatDialog(dialogId2) && ((chat = getMessagesController().getChat(Long.valueOf(-dialogId2))) == null || chat.min || ChatObject.isNotInChat(chat))) {
                newCount = 0;
            }
            int notifyOverride = getNotifyOverride(preferences, dialogId2);
            if (notifyOverride == -1) {
                canAddValue = isGlobalNotificationsEnabled(dialogId2);
            } else if (notifyOverride != 2) {
                canAddValue = true;
            }
            if (this.notifyCheck && !canAddValue && (override = this.pushDialogsOverrideMention.get(dialogId2)) != null && override.intValue() != 0) {
                canAddValue = true;
                newCount = override.intValue();
            }
            if (newCount == 0) {
                this.smartNotificationsDialogs.remove(dialogId2);
            }
            if (newCount < 0) {
                if (currentCount == null) {
                    b++;
                } else {
                    newCount += currentCount.intValue();
                }
            }
            if ((canAddValue || newCount == 0) && currentCount != null) {
                this.total_unread_count -= currentCount.intValue();
            }
            if (newCount == 0) {
                this.pushDialogs.remove(dialogId2);
                this.pushDialogsOverrideMention.remove(dialogId2);
                int a = 0;
                while (a < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(a);
                    if (messageObject.messageOwner.from_scheduled || messageObject.getDialogId() != dialogId2) {
                        dialogId = dialogId2;
                    } else {
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount -= z ? 1 : 0;
                        }
                        this.pushMessages.remove(a);
                        a--;
                        this.delayedPushMessages.remove(messageObject);
                        dialogId = dialogId2;
                        if (messageObject.messageOwner.peer_id.channel_id != 0) {
                            did = -messageObject.messageOwner.peer_id.channel_id;
                        } else {
                            did = 0;
                        }
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(did);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(did);
                            }
                        }
                        arrayList.add(messageObject);
                    }
                    z = true;
                    a++;
                    dialogId2 = dialogId;
                }
            } else {
                long dialogId3 = dialogId2;
                if (canAddValue) {
                    this.total_unread_count += newCount;
                    this.pushDialogs.put(dialogId3, Integer.valueOf(newCount));
                }
            }
            b++;
        }
        if (popupArrayToRemove.isEmpty() == 0) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda19(this, arrayList));
        }
        if (old_unread_count != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                if (this.lastOnlineFromOtherDevice <= getConnectionsManager().getCurrentTime()) {
                    z = false;
                }
                scheduleNotificationDelay(z);
            }
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda6(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* renamed from: lambda$processDialogsUpdateRead$19$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1105x91a07ef7(ArrayList popupArrayToRemove) {
        int size = popupArrayToRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayToRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* renamed from: lambda$processDialogsUpdateRead$20$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1106x890a6821(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void processLoadedUnreadMessages(LongSparseArray<Integer> dialogs, ArrayList<TLRPC.Message> messages, ArrayList<MessageObject> push, ArrayList<TLRPC.User> users, ArrayList<TLRPC.Chat> chats, ArrayList<TLRPC.EncryptedChat> encryptedChats) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        getMessagesController().putEncryptedChats(encryptedChats, true);
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda25(this, messages, dialogs, push));
    }

    /* renamed from: lambda$processLoadedUnreadMessages$23$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1110xf8b52a58(ArrayList messages, LongSparseArray dialogs, ArrayList push) {
        SharedPreferences preferences;
        boolean value;
        long did;
        int notifyOverride;
        boolean value2;
        int a;
        long did2;
        long dialog_id;
        boolean value3;
        ArrayList arrayList = messages;
        LongSparseArray longSparseArray = dialogs;
        ArrayList arrayList2 = push;
        this.pushDialogs.clear();
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        boolean z = false;
        this.total_unread_count = 0;
        this.personalCount = 0;
        SharedPreferences preferences2 = getAccountInstance().getNotificationsSettings();
        LongSparseArray<Boolean> settingsCache = new LongSparseArray<>();
        long j = 0;
        int i = -1;
        int i2 = 1;
        if (arrayList != null) {
            int a2 = 0;
            while (a2 < messages.size()) {
                TLRPC.Message message = (TLRPC.Message) arrayList.get(a2);
                if (message == null) {
                    a = a2;
                } else {
                    if (message.fwd_from == null || !message.fwd_from.imported) {
                        if (!(message.action instanceof TLRPC.TL_messageActionSetMessagesTTL)) {
                            if (message.silent) {
                                if (!(message.action instanceof TLRPC.TL_messageActionContactSignUp)) {
                                    if (message.action instanceof TLRPC.TL_messageActionUserJoined) {
                                        a = a2;
                                    }
                                }
                            }
                            if (message.peer_id.channel_id != j) {
                                did2 = -message.peer_id.channel_id;
                            } else {
                                did2 = 0;
                            }
                            SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(did2);
                            if (sparseArray == null || sparseArray.indexOfKey(message.id) < 0) {
                                MessageObject messageObject = new MessageObject(this.currentAccount, message, z, z);
                                if (isPersonalMessage(messageObject)) {
                                    this.personalCount += i2;
                                }
                                long dialog_id2 = messageObject.getDialogId();
                                long original_dialog_id = dialog_id2;
                                if (messageObject.messageOwner.mentioned) {
                                    a = a2;
                                    dialog_id = messageObject.getFromChatId();
                                } else {
                                    a = a2;
                                    dialog_id = dialog_id2;
                                }
                                int index = settingsCache.indexOfKey(dialog_id);
                                if (index >= 0) {
                                    value3 = settingsCache.valueAt(index).booleanValue();
                                } else {
                                    int notifyOverride2 = getNotifyOverride(preferences2, dialog_id);
                                    if (notifyOverride2 == i) {
                                        value3 = isGlobalNotificationsEnabled(dialog_id);
                                    } else {
                                        value3 = notifyOverride2 != 2;
                                    }
                                    settingsCache.put(dialog_id, Boolean.valueOf(value3));
                                }
                                if (value3) {
                                    int index2 = index;
                                    boolean value4 = value3;
                                    if (dialog_id != this.openedDialogId || !ApplicationLoader.isScreenOn) {
                                        if (sparseArray == null) {
                                            sparseArray = new SparseArray<>();
                                            this.pushMessagesDict.put(did2, sparseArray);
                                        }
                                        sparseArray.put(message.id, messageObject);
                                        this.pushMessages.add(0, messageObject);
                                        long original_dialog_id2 = original_dialog_id;
                                        if (original_dialog_id2 != dialog_id) {
                                            int i3 = index2;
                                            Integer current = this.pushDialogsOverrideMention.get(original_dialog_id2);
                                            boolean z2 = value4;
                                            Integer num = current;
                                            this.pushDialogsOverrideMention.put(original_dialog_id2, Integer.valueOf(current == null ? 1 : current.intValue() + 1));
                                        } else {
                                            boolean z3 = value4;
                                        }
                                    }
                                } else {
                                    long j2 = original_dialog_id;
                                    int i4 = index;
                                    boolean z4 = value3;
                                    long j3 = j2;
                                }
                            } else {
                                a = a2;
                            }
                        } else {
                            a = a2;
                        }
                    }
                    a = a2;
                }
                a2 = a + 1;
                arrayList = messages;
                z = false;
                j = 0;
                i = -1;
                i2 = 1;
            }
            int i5 = a2;
        }
        for (int a3 = 0; a3 < dialogs.size(); a3++) {
            long dialog_id3 = longSparseArray.keyAt(a3);
            int index3 = settingsCache.indexOfKey(dialog_id3);
            if (index3 >= 0) {
                notifyOverride = settingsCache.valueAt(index3).booleanValue();
            } else {
                int notifyOverride3 = getNotifyOverride(preferences2, dialog_id3);
                if (notifyOverride3 == -1) {
                    value2 = isGlobalNotificationsEnabled(dialog_id3);
                } else {
                    value2 = notifyOverride3 != 2;
                }
                settingsCache.put(dialog_id3, Boolean.valueOf(value2));
                notifyOverride = value2;
            }
            if (notifyOverride != 0) {
                int count = ((Integer) longSparseArray.valueAt(a3)).intValue();
                this.pushDialogs.put(dialog_id3, Integer.valueOf(count));
                this.total_unread_count += count;
            }
        }
        if (arrayList2 != null) {
            int a4 = 0;
            while (a4 < push.size()) {
                MessageObject messageObject2 = (MessageObject) arrayList2.get(a4);
                int mid = messageObject2.getId();
                if (this.pushMessagesDict.indexOfKey((long) mid) >= 0) {
                    preferences = preferences2;
                } else {
                    if (isPersonalMessage(messageObject2)) {
                        this.personalCount++;
                    }
                    long dialogId = messageObject2.getDialogId();
                    long originalDialogId = dialogId;
                    long randomId = messageObject2.messageOwner.random_id;
                    if (messageObject2.messageOwner.mentioned) {
                        dialogId = messageObject2.getFromChatId();
                    }
                    int index4 = settingsCache.indexOfKey(dialogId);
                    if (index4 >= 0) {
                        value = settingsCache.valueAt(index4).booleanValue();
                    } else {
                        int notifyOverride4 = getNotifyOverride(preferences2, dialogId);
                        if (notifyOverride4 == -1) {
                            value = isGlobalNotificationsEnabled(dialogId);
                        } else {
                            value = notifyOverride4 != 2;
                        }
                        settingsCache.put(dialogId, Boolean.valueOf(value));
                    }
                    if (!value) {
                        preferences = preferences2;
                    } else if (dialogId != this.openedDialogId || !ApplicationLoader.isScreenOn) {
                        if (mid != 0) {
                            if (messageObject2.messageOwner.peer_id.channel_id != 0) {
                                did = -messageObject2.messageOwner.peer_id.channel_id;
                            } else {
                                did = 0;
                            }
                            SparseArray<MessageObject> sparseArray2 = this.pushMessagesDict.get(did);
                            if (sparseArray2 == null) {
                                sparseArray2 = new SparseArray<>();
                                preferences = preferences2;
                                this.pushMessagesDict.put(did, sparseArray2);
                            } else {
                                preferences = preferences2;
                            }
                            sparseArray2.put(mid, messageObject2);
                        } else {
                            preferences = preferences2;
                            if (randomId != 0) {
                                this.fcmRandomMessagesDict.put(randomId, messageObject2);
                            }
                        }
                        this.pushMessages.add(0, messageObject2);
                        if (originalDialogId != dialogId) {
                            Integer current2 = this.pushDialogsOverrideMention.get(originalDialogId);
                            this.pushDialogsOverrideMention.put(originalDialogId, Integer.valueOf(current2 == null ? 1 : current2.intValue() + 1));
                        }
                        Integer currentCount = this.pushDialogs.get(dialogId);
                        int newCount = currentCount != null ? currentCount.intValue() + 1 : 1;
                        if (currentCount != null) {
                            this.total_unread_count -= currentCount.intValue();
                        }
                        this.total_unread_count += newCount;
                        this.pushDialogs.put(dialogId, Integer.valueOf(newCount));
                    } else {
                        preferences = preferences2;
                    }
                }
                a4++;
                LongSparseArray longSparseArray2 = dialogs;
                arrayList2 = push;
                preferences2 = preferences;
            }
        }
        AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda7(this, this.pushDialogs.size()));
        showOrUpdateNotification(SystemClock.elapsedRealtime() / 1000 < 60);
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* renamed from: lambda$processLoadedUnreadMessages$22$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1109xbeea8879(int pushDialogsCount) {
        if (this.total_unread_count == 0) {
            this.popupMessages.clear();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    private int getTotalAllUnreadCount() {
        int count = 0;
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                NotificationsController controller = getInstance(a);
                if (controller.showBadgeNumber) {
                    if (controller.showBadgeMessages) {
                        if (controller.showBadgeMuted) {
                            try {
                                int N = MessagesController.getInstance(a).allDialogs.size();
                                for (int i = 0; i < N; i++) {
                                    TLRPC.Dialog dialog = MessagesController.getInstance(a).allDialogs.get(i);
                                    if (dialog == null || !DialogObject.isChatDialog(dialog.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-dialog.id)))) {
                                        if (!(dialog == null || dialog.unread_count == 0)) {
                                            count += dialog.unread_count;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } else {
                            count += controller.total_unread_count;
                        }
                    } else if (controller.showBadgeMuted) {
                        try {
                            int N2 = MessagesController.getInstance(a).allDialogs.size();
                            for (int i2 = 0; i2 < N2; i2++) {
                                TLRPC.Dialog dialog2 = MessagesController.getInstance(a).allDialogs.get(i2);
                                if (!DialogObject.isChatDialog(dialog2.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-dialog2.id)))) {
                                    if (dialog2.unread_count != 0) {
                                        count++;
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    } else {
                        count += controller.pushDialogs.size();
                    }
                }
            }
        }
        return count;
    }

    /* renamed from: lambda$updateBadge$24$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1127x8d3d4342() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda5(this));
    }

    private void setBadge(int count) {
        if (this.lastBadgeCount != count) {
            this.lastBadgeCount = count;
            NotificationBadge.applyCount(count);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00eb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r29, java.lang.String[] r30, boolean[] r31) {
        /*
            r28 = this;
            r0 = r29
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x12e6
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x12e6
        L_0x000e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r1 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            long r3 = r3.chat_id
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            if (r7 == 0) goto L_0x0025
            long r3 = r3.chat_id
            goto L_0x0027
        L_0x0025:
            long r3 = r3.channel_id
        L_0x0027:
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r7 = r7.user_id
            r9 = 1
            r10 = 0
            if (r31 == 0) goto L_0x0033
            r31[r10] = r9
        L_0x0033:
            org.telegram.messenger.AccountInstance r11 = r28.getAccountInstance()
            android.content.SharedPreferences r11 = r11.getNotificationsSettings()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "content_preview_"
            r12.append(r13)
            r12.append(r1)
            java.lang.String r12 = r12.toString()
            boolean r12 = r11.getBoolean(r12, r9)
            boolean r13 = r29.isFcmMessage()
            java.lang.String r15 = "Message"
            r14 = 27
            if (r13 == 0) goto L_0x0107
            int r13 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x0081
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x0081
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 <= r14) goto L_0x006a
            java.lang.String r5 = r0.localName
            r30[r10] = r5
        L_0x006a:
            if (r12 == 0) goto L_0x0075
            java.lang.String r5 = "EnablePreviewAll"
            r6 = 1
            boolean r5 = r11.getBoolean(r5, r6)
            if (r5 != 0) goto L_0x0102
        L_0x0075:
            if (r31 == 0) goto L_0x0079
            r31[r10] = r10
        L_0x0079:
            r5 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r15, r5)
            return r5
        L_0x0081:
            int r13 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x0102
            org.telegram.tgnet.TLRPC$Message r13 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r13 = r13.peer_id
            long r9 = r13.channel_id
            int r13 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x00a3
            boolean r9 = r29.isSupergroup()
            if (r9 == 0) goto L_0x0097
            r10 = 0
            goto L_0x00a4
        L_0x0097:
            int r9 = android.os.Build.VERSION.SDK_INT
            if (r9 <= r14) goto L_0x00a1
            java.lang.String r9 = r0.localName
            r10 = 0
            r30[r10] = r9
            goto L_0x00a8
        L_0x00a1:
            r10 = 0
            goto L_0x00a8
        L_0x00a3:
            r10 = 0
        L_0x00a4:
            java.lang.String r9 = r0.localUserName
            r30[r10] = r9
        L_0x00a8:
            if (r12 == 0) goto L_0x00c4
            boolean r9 = r0.localChannel
            if (r9 != 0) goto L_0x00b7
            java.lang.String r9 = "EnablePreviewGroup"
            r10 = 1
            boolean r9 = r11.getBoolean(r9, r10)
            if (r9 == 0) goto L_0x00c4
        L_0x00b7:
            boolean r9 = r0.localChannel
            if (r9 == 0) goto L_0x0102
            java.lang.String r9 = "EnablePreviewChannel"
            r10 = 1
            boolean r9 = r11.getBoolean(r9, r10)
            if (r9 != 0) goto L_0x0102
        L_0x00c4:
            if (r31 == 0) goto L_0x00c9
            r9 = 0
            r31[r9] = r9
        L_0x00c9:
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r9 = r9.peer_id
            long r9 = r9.channel_id
            int r13 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x00eb
            boolean r5 = r29.isSupergroup()
            if (r5 != 0) goto L_0x00eb
            r5 = 2131624793(0x7f0e0359, float:1.8876776E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r0.localName
            r10 = 0
            r6[r10] = r9
            java.lang.String r9 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r5, r6)
            return r5
        L_0x00eb:
            r5 = 2131626669(0x7f0e0aad, float:1.888058E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r0.localUserName
            r10 = 0
            r6[r10] = r9
            java.lang.String r9 = r0.localName
            r10 = 1
            r6[r10] = r9
            java.lang.String r9 = "NotificationMessageGroupNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r5, r6)
            return r5
        L_0x0102:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            java.lang.String r5 = r5.message
            return r5
        L_0x0107:
            org.telegram.messenger.UserConfig r9 = r28.getUserConfig()
            long r9 = r9.getClientUserId()
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x011d
            long r7 = r29.getFromChatId()
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x0125
            long r7 = -r3
            goto L_0x0125
        L_0x011d:
            int r13 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r13 != 0) goto L_0x0125
            long r7 = r29.getFromChatId()
        L_0x0125:
            int r13 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x0134
            int r13 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x012f
            long r1 = -r3
            goto L_0x0134
        L_0x012f:
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x0134
            r1 = r7
        L_0x0134:
            r13 = 0
            boolean r17 = org.telegram.messenger.UserObject.isReplyUser((long) r1)
            if (r17 == 0) goto L_0x0153
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            if (r14 == 0) goto L_0x0153
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id
            if (r14 == 0) goto L_0x0153
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id
            long r7 = org.telegram.messenger.MessageObject.getPeerId(r14)
        L_0x0153:
            int r18 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r18 <= 0) goto L_0x0184
            org.telegram.messenger.MessagesController r14 = r28.getMessagesController()
            java.lang.Long r5 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r5 = r14.getUser(r5)
            if (r5 == 0) goto L_0x0183
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r5)
            r13 = 0
            int r21 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r21 == 0) goto L_0x0173
            r13 = 0
            r30[r13] = r6
            goto L_0x0182
        L_0x0173:
            r13 = 0
            int r14 = android.os.Build.VERSION.SDK_INT
            r13 = 27
            if (r14 <= r13) goto L_0x017e
            r13 = 0
            r30[r13] = r6
            goto L_0x0182
        L_0x017e:
            r13 = 0
            r14 = 0
            r30[r13] = r14
        L_0x0182:
            r13 = r6
        L_0x0183:
            goto L_0x019b
        L_0x0184:
            org.telegram.messenger.MessagesController r5 = r28.getMessagesController()
            r6 = r13
            long r13 = -r7
            java.lang.Long r13 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r13)
            if (r5 == 0) goto L_0x019a
            java.lang.String r13 = r5.title
            r6 = 0
            r30[r6] = r13
            goto L_0x019b
        L_0x019a:
            r13 = r6
        L_0x019b:
            if (r13 == 0) goto L_0x01f9
            r5 = 0
            int r14 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r14 <= 0) goto L_0x01f9
            boolean r5 = org.telegram.messenger.UserObject.isReplyUser((long) r1)
            if (r5 == 0) goto L_0x01f9
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r5 = r5.fwd_from
            if (r5 == 0) goto L_0x01f9
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r5 = r5.fwd_from
            org.telegram.tgnet.TLRPC$Peer r5 = r5.saved_from_peer
            if (r5 == 0) goto L_0x01f9
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r5 = r5.fwd_from
            org.telegram.tgnet.TLRPC$Peer r5 = r5.saved_from_peer
            long r5 = org.telegram.messenger.MessageObject.getPeerId(r5)
            boolean r14 = org.telegram.messenger.DialogObject.isChatDialog(r5)
            if (r14 == 0) goto L_0x01f6
            org.telegram.messenger.MessagesController r14 = r28.getMessagesController()
            r21 = r9
            long r9 = -r5
            java.lang.Long r9 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r9 = r14.getChat(r9)
            if (r9 == 0) goto L_0x01fb
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r13)
            java.lang.String r14 = " @ "
            r10.append(r14)
            java.lang.String r14 = r9.title
            r10.append(r14)
            java.lang.String r13 = r10.toString()
            r10 = 0
            r14 = r30[r10]
            if (r14 == 0) goto L_0x01fb
            r30[r10] = r13
            goto L_0x01fb
        L_0x01f6:
            r21 = r9
            goto L_0x01fb
        L_0x01f9:
            r21 = r9
        L_0x01fb:
            if (r13 != 0) goto L_0x01ff
            r5 = 0
            return r5
        L_0x01ff:
            r5 = 0
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x022a
            org.telegram.messenger.MessagesController r6 = r28.getMessagesController()
            java.lang.Long r9 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r5 = r6.getChat(r9)
            if (r5 != 0) goto L_0x0216
            r6 = 0
            return r6
        L_0x0216:
            r6 = 0
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r9 == 0) goto L_0x022a
            boolean r9 = r5.megagroup
            if (r9 != 0) goto L_0x022a
            int r9 = android.os.Build.VERSION.SDK_INT
            r10 = 27
            if (r9 > r10) goto L_0x022a
            r9 = 0
            r30[r9] = r6
        L_0x022a:
            r6 = 0
            boolean r9 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
            if (r9 == 0) goto L_0x023f
            r9 = 0
            r10 = 0
            r30[r10] = r9
            r9 = 2131626646(0x7f0e0a96, float:1.8880534E38)
            java.lang.String r10 = "NotificationHiddenMessage"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            return r9
        L_0x023f:
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r9 == 0) goto L_0x024b
            boolean r9 = r5.megagroup
            if (r9 != 0) goto L_0x024b
            r9 = 1
            goto L_0x024c
        L_0x024b:
            r9 = 0
        L_0x024c:
            if (r12 == 0) goto L_0x12cf
            r19 = 0
            int r10 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r10 != 0) goto L_0x0261
            int r10 = (r7 > r19 ? 1 : (r7 == r19 ? 0 : -1))
            if (r10 == 0) goto L_0x0261
            java.lang.String r10 = "EnablePreviewAll"
            r14 = 1
            boolean r10 = r11.getBoolean(r10, r14)
            if (r10 != 0) goto L_0x027d
        L_0x0261:
            r19 = 0
            int r10 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r10 == 0) goto L_0x12cf
            if (r9 != 0) goto L_0x0272
            java.lang.String r10 = "EnablePreviewGroup"
            r14 = 1
            boolean r10 = r11.getBoolean(r10, r14)
            if (r10 != 0) goto L_0x027d
        L_0x0272:
            if (r9 == 0) goto L_0x12cf
            java.lang.String r10 = "EnablePreviewChannel"
            r14 = 1
            boolean r10 = r11.getBoolean(r10, r14)
            if (r10 == 0) goto L_0x12cf
        L_0x027d:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            java.lang.String r14 = "🎬 "
            r23 = r3
            java.lang.String r3 = "📎 "
            java.lang.String r4 = "📹 "
            r17 = r6
            java.lang.String r6 = "🖼 "
            r25 = r9
            r9 = 19
            if (r10 == 0) goto L_0x10c4
            r10 = 0
            r15 = 0
            r30[r15] = r10
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGeoProximityReached
            if (r10 == 0) goto L_0x02aa
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x02aa:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined
            if (r10 != 0) goto L_0x10b0
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r10 == 0) goto L_0x02c0
            r26 = r11
            r27 = r12
            goto L_0x10b4
        L_0x02c0:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto
            if (r10 == 0) goto L_0x02d8
            r3 = 2131626627(0x7f0e0a83, float:1.8880496E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationContactNewPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x02d8:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation
            r15 = 3
            if (r10 == 0) goto L_0x0344
            r3 = 2131628796(0x7f0e12fc, float:1.8884895E38)
            r4 = 2
            java.lang.Object[] r6 = new java.lang.Object[r4]
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r4 = r4.formatterYear
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            int r9 = r9.date
            long r9 = (long) r9
            r18 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 * r18
            java.lang.String r4 = r4.format((long) r9)
            r9 = 0
            r6[r9] = r4
            org.telegram.messenger.LocaleController r4 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r4 = r4.formatterDay
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            int r9 = r9.date
            long r9 = (long) r9
            long r9 = r9 * r18
            java.lang.String r4 = r4.format((long) r9)
            r9 = 1
            r6[r9] = r4
            java.lang.String r4 = "formatDateAtTime"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r3, r6)
            r4 = 2131626696(0x7f0e0ac8, float:1.8880635E38)
            r6 = 4
            java.lang.Object[] r6 = new java.lang.Object[r6]
            org.telegram.messenger.UserConfig r10 = r28.getUserConfig()
            org.telegram.tgnet.TLRPC$User r10 = r10.getCurrentUser()
            java.lang.String r10 = r10.first_name
            r14 = 0
            r6[r14] = r10
            r6[r9] = r3
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            java.lang.String r9 = r9.title
            r10 = 2
            r6[r10] = r9
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            java.lang.String r9 = r9.address
            r6[r15] = r9
            java.lang.String r9 = "NotificationUnrecognizedDevice"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0344:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore
            if (r10 != 0) goto L_0x10a5
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent
            if (r10 == 0) goto L_0x035a
            r26 = r11
            r27 = r12
            goto L_0x10a9
        L_0x035a:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r10 == 0) goto L_0x037e
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3.video
            if (r3 == 0) goto L_0x0374
            r3 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r4 = "CallMessageVideoIncomingMissed"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x0374:
            r3 = 2131624661(0x7f0e02d5, float:1.8876508E38)
            java.lang.String r4 = "CallMessageIncomingMissed"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x037e:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r10 == 0) goto L_0x04b1
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            long r3 = r3.user_id
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 != 0) goto L_0x03b0
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            int r6 = r6.size()
            r9 = 1
            if (r6 != r9) goto L_0x03b0
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            r9 = 0
            java.lang.Object r6 = r6.get(r9)
            java.lang.Long r6 = (java.lang.Long) r6
            long r3 = r6.longValue()
        L_0x03b0:
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x0455
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            r26 = r11
            r27 = r12
            long r11 = r6.channel_id
            int r6 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x03dd
            boolean r6 = r5.megagroup
            if (r6 != 0) goto L_0x03dd
            r6 = 2131624742(0x7f0e0326, float:1.8876672E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r10 = 0
            r9[r10] = r13
            java.lang.String r10 = r5.title
            r11 = 1
            r9[r11] = r10
            java.lang.String r10 = "ChannelAddedByNotification"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r10, r6, r9)
            return r6
        L_0x03dd:
            int r6 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r6 != 0) goto L_0x03f6
            r6 = 2131626648(0x7f0e0a98, float:1.8880538E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r10 = 0
            r9[r10] = r13
            java.lang.String r10 = r5.title
            r11 = 1
            r9[r11] = r10
            java.lang.String r10 = "NotificationInvitedToGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r10, r6, r9)
            return r6
        L_0x03f6:
            org.telegram.messenger.MessagesController r6 = r28.getMessagesController()
            java.lang.Long r9 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r9)
            if (r6 != 0) goto L_0x0406
            r9 = 0
            return r9
        L_0x0406:
            long r9 = r6.id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x043a
            boolean r9 = r5.megagroup
            if (r9 == 0) goto L_0x0425
            r9 = 2131626633(0x7f0e0a89, float:1.8880508E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r11 = 0
            r10[r11] = r13
            java.lang.String r11 = r5.title
            r12 = 1
            r10[r12] = r11
            java.lang.String r11 = "NotificationGroupAddSelfMega"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r11, r9, r10)
            return r9
        L_0x0425:
            r10 = 2
            r11 = 0
            r12 = 1
            r9 = 2131626632(0x7f0e0a88, float:1.8880506E38)
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r10[r11] = r13
            java.lang.String r11 = r5.title
            r10[r12] = r11
            java.lang.String r11 = "NotificationGroupAddSelf"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r11, r9, r10)
            return r9
        L_0x043a:
            r11 = 0
            r12 = 1
            r9 = 2131626631(0x7f0e0a87, float:1.8880504E38)
            java.lang.Object[] r10 = new java.lang.Object[r15]
            r10[r11] = r13
            java.lang.String r11 = r5.title
            r10[r12] = r11
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r6)
            r12 = 2
            r10[r12] = r11
            java.lang.String r11 = "NotificationGroupAddMember"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r11, r9, r10)
            return r9
        L_0x0455:
            r26 = r11
            r27 = r12
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r9 = 0
        L_0x045f:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            java.util.ArrayList<java.lang.Long> r10 = r10.users
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x0496
            org.telegram.messenger.MessagesController r10 = r28.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r11 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r11 = r11.action
            java.util.ArrayList<java.lang.Long> r11 = r11.users
            java.lang.Object r11 = r11.get(r9)
            java.lang.Long r11 = (java.lang.Long) r11
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r11)
            if (r10 == 0) goto L_0x0493
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r10)
            int r12 = r6.length()
            if (r12 == 0) goto L_0x0490
            java.lang.String r12 = ", "
            r6.append(r12)
        L_0x0490:
            r6.append(r11)
        L_0x0493:
            int r9 = r9 + 1
            goto L_0x045f
        L_0x0496:
            r9 = 2131626631(0x7f0e0a87, float:1.8880504E38)
            java.lang.Object[] r10 = new java.lang.Object[r15]
            r11 = 0
            r10[r11] = r13
            java.lang.String r11 = r5.title
            r12 = 1
            r10[r12] = r11
            java.lang.String r11 = r6.toString()
            r12 = 2
            r10[r12] = r11
            java.lang.String r11 = "NotificationGroupAddMember"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r11, r9, r10)
            return r9
        L_0x04b1:
            r26 = r11
            r27 = r12
            r12 = 2
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGroupCall
            if (r10 == 0) goto L_0x04d2
            r3 = 2131626635(0x7f0e0a8b, float:1.8880512E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationGroupCreatedCall"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x04d2:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGroupCallScheduled
            if (r10 == 0) goto L_0x04e1
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x04e1:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionInviteToGroupCall
            if (r10 == 0) goto L_0x05b5
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            long r3 = r3.user_id
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 != 0) goto L_0x0513
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            int r6 = r6.size()
            r9 = 1
            if (r6 != r9) goto L_0x0513
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            r9 = 0
            java.lang.Object r6 = r6.get(r9)
            java.lang.Long r6 = (java.lang.Long) r6
            long r3 = r6.longValue()
        L_0x0513:
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x055d
            int r6 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r6 != 0) goto L_0x0532
            r6 = 2131626640(0x7f0e0a90, float:1.8880522E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r10 = 0
            r9[r10] = r13
            java.lang.String r10 = r5.title
            r11 = 1
            r9[r11] = r10
            java.lang.String r10 = "NotificationGroupInvitedYouToCall"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r10, r6, r9)
            return r6
        L_0x0532:
            org.telegram.messenger.MessagesController r6 = r28.getMessagesController()
            java.lang.Long r9 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r9)
            if (r6 != 0) goto L_0x0542
            r9 = 0
            return r9
        L_0x0542:
            r9 = 2131626639(0x7f0e0a8f, float:1.888052E38)
            java.lang.Object[] r10 = new java.lang.Object[r15]
            r11 = 0
            r10[r11] = r13
            java.lang.String r11 = r5.title
            r12 = 1
            r10[r12] = r11
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r6)
            r12 = 2
            r10[r12] = r11
            java.lang.String r11 = "NotificationGroupInvitedToCall"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r11, r9, r10)
            return r9
        L_0x055d:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r9 = 0
        L_0x0563:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            java.util.ArrayList<java.lang.Long> r10 = r10.users
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x059a
            org.telegram.messenger.MessagesController r10 = r28.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r11 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r11 = r11.action
            java.util.ArrayList<java.lang.Long> r11 = r11.users
            java.lang.Object r11 = r11.get(r9)
            java.lang.Long r11 = (java.lang.Long) r11
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r11)
            if (r10 == 0) goto L_0x0597
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r10)
            int r12 = r6.length()
            if (r12 == 0) goto L_0x0594
            java.lang.String r12 = ", "
            r6.append(r12)
        L_0x0594:
            r6.append(r11)
        L_0x0597:
            int r9 = r9 + 1
            goto L_0x0563
        L_0x059a:
            r9 = 2131626639(0x7f0e0a8f, float:1.888052E38)
            java.lang.Object[] r10 = new java.lang.Object[r15]
            r11 = 0
            r10[r11] = r13
            java.lang.String r11 = r5.title
            r12 = 1
            r10[r12] = r11
            java.lang.String r11 = r6.toString()
            r12 = 2
            r10[r12] = r11
            java.lang.String r11 = "NotificationGroupInvitedToCall"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r11, r9, r10)
            return r9
        L_0x05b5:
            r12 = 2
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r10 == 0) goto L_0x05d2
            r3 = 2131626649(0x7f0e0a99, float:1.888054E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationInvitedToGroupByLink"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x05d2:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle
            if (r10 == 0) goto L_0x05f3
            r3 = 2131626628(0x7f0e0a84, float:1.8880498E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.lang.String r6 = r6.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationEditedGroupName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x05f3:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r10 != 0) goto L_0x103b
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto
            if (r10 == 0) goto L_0x0605
            goto L_0x103b
        L_0x0605:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r10 == 0) goto L_0x067c
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            long r3 = r3.user_id
            int r6 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r6 != 0) goto L_0x062c
            r3 = 2131626642(0x7f0e0a92, float:1.8880526E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationGroupKickYou"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x062c:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            long r3 = r3.user_id
            int r6 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r6 != 0) goto L_0x064b
            r3 = 2131626643(0x7f0e0a93, float:1.8880528E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationGroupLeftMember"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x064b:
            org.telegram.messenger.MessagesController r3 = r28.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r9 = r4.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 != 0) goto L_0x0661
            r4 = 0
            return r4
        L_0x0661:
            r4 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            java.lang.Object[] r6 = new java.lang.Object[r15]
            r9 = 0
            r6[r9] = r13
            java.lang.String r9 = r5.title
            r10 = 1
            r6[r10] = r9
            java.lang.String r9 = org.telegram.messenger.UserObject.getUserName(r3)
            r10 = 2
            r6[r10] = r9
            java.lang.String r9 = "NotificationGroupKickMember"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x067c:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate
            if (r10 == 0) goto L_0x068b
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x068b:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r10 == 0) goto L_0x069a
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x069a:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r10 == 0) goto L_0x06b4
            r3 = 2131624147(0x7f0e00d3, float:1.8875465E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "ActionMigrateFromGroupNotify"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x06b4:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r10 == 0) goto L_0x06d2
            r3 = 2131624147(0x7f0e00d3, float:1.8875465E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.lang.String r6 = r6.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "ActionMigrateFromGroupNotify"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x06d2:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken
            if (r10 == 0) goto L_0x06e1
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x06e1:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r10 == 0) goto L_0x0fc7
            java.lang.String r10 = "..."
            if (r5 == 0) goto L_0x0a1d
            boolean r12 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r12 == 0) goto L_0x06f7
            boolean r12 = r5.megagroup
            if (r12 == 0) goto L_0x0a1d
        L_0x06f7:
            org.telegram.messenger.MessageObject r12 = r0.replyMessageObject
            if (r12 != 0) goto L_0x0710
            r3 = 2131626596(0x7f0e0a64, float:1.8880433E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedNoText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0710:
            org.telegram.messenger.MessageObject r12 = r0.replyMessageObject
            boolean r16 = r12.isMusic()
            if (r16 == 0) goto L_0x072d
            r3 = 2131626593(0x7f0e0a61, float:1.8880427E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedMusic"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x072d:
            boolean r16 = r12.isVideo()
            java.lang.String r11 = "NotificationActionPinnedText"
            if (r16 == 0) goto L_0x0780
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x076b
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x076b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object[] r4 = new java.lang.Object[r15]
            r6 = 0
            r4[r6] = r13
            r6 = 1
            r4[r6] = r3
            java.lang.String r6 = r5.title
            r9 = 2
            r4[r9] = r6
            r6 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x076b:
            r9 = 2
            r3 = 2131626620(0x7f0e0a7c, float:1.8880481E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedVideo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0780:
            boolean r4 = r12.isGif()
            if (r4 == 0) goto L_0x07d1
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x07bc
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x07bc
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object[] r4 = new java.lang.Object[r15]
            r6 = 0
            r4[r6] = r13
            r6 = 1
            r4[r6] = r3
            java.lang.String r6 = r5.title
            r9 = 2
            r4[r9] = r6
            r6 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x07bc:
            r9 = 2
            r3 = 2131626587(0x7f0e0a5b, float:1.8880414E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGif"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x07d1:
            boolean r4 = r12.isVoice()
            if (r4 == 0) goto L_0x07ec
            r3 = 2131626623(0x7f0e0a7f, float:1.8880487E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedVoice"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x07ec:
            boolean r4 = r12.isRoundVideo()
            if (r4 == 0) goto L_0x0807
            r3 = 2131626608(0x7f0e0a70, float:1.8880457E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedRound"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0807:
            boolean r4 = r12.isSticker()
            if (r4 != 0) goto L_0x09eb
            boolean r4 = r12.isAnimatedSticker()
            if (r4 == 0) goto L_0x0815
            goto L_0x09eb
        L_0x0815:
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x0868
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r9) goto L_0x0853
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0853
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            java.lang.String r3 = r3.message
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            java.lang.Object[] r4 = new java.lang.Object[r15]
            r6 = 0
            r4[r6] = r13
            r6 = 1
            r4[r6] = r3
            java.lang.String r6 = r5.title
            r9 = 2
            r4[r9] = r6
            r6 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x0853:
            r9 = 2
            r3 = 2131626572(0x7f0e0a4c, float:1.8880384E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedFile"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0868:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r3 != 0) goto L_0x09d6
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r3 == 0) goto L_0x087d
            r6 = 0
            r9 = 2
            r10 = 1
            goto L_0x09d9
        L_0x087d:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x089a
            r3 = 2131626583(0x7f0e0a57, float:1.8880406E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGeoLive"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x089a:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r3 == 0) goto L_0x08c7
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r3
            r4 = 2131626569(0x7f0e0a49, float:1.8880378E38)
            java.lang.Object[] r6 = new java.lang.Object[r15]
            r9 = 0
            r6[r9] = r13
            java.lang.String r9 = r5.title
            r10 = 1
            r6[r10] = r9
            java.lang.String r9 = r3.first_name
            java.lang.String r10 = r3.last_name
            java.lang.String r9 = org.telegram.messenger.ContactsController.formatName(r9, r10)
            r10 = 2
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedContact2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x08c7:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r3 == 0) goto L_0x0911
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r4 = r3.poll
            boolean r4 = r4.quiz
            if (r4 == 0) goto L_0x08f6
            r4 = 2131626605(0x7f0e0a6d, float:1.888045E38)
            java.lang.Object[] r6 = new java.lang.Object[r15]
            r9 = 0
            r6[r9] = r13
            java.lang.String r9 = r5.title
            r10 = 1
            r6[r10] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r3.poll
            java.lang.String r9 = r9.question
            r10 = 2
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedQuiz2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x08f6:
            r4 = 2131626602(0x7f0e0a6a, float:1.8880445E38)
            java.lang.Object[] r6 = new java.lang.Object[r15]
            r9 = 0
            r6[r9] = r13
            java.lang.String r9 = r5.title
            r10 = 1
            r6[r10] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r3.poll
            java.lang.String r9 = r9.question
            r10 = 2
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedPoll2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0911:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0964
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x094f
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x094f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.Object[] r4 = new java.lang.Object[r15]
            r6 = 0
            r4[r6] = r13
            r6 = 1
            r4[r6] = r3
            java.lang.String r6 = r5.title
            r9 = 2
            r4[r9] = r6
            r6 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x094f:
            r9 = 2
            r3 = 2131626599(0x7f0e0a67, float:1.8880439E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0964:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x0981
            r3 = 2131626575(0x7f0e0a4f, float:1.888039E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGame"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0981:
            java.lang.CharSequence r3 = r12.messageText
            if (r3 == 0) goto L_0x09c1
            java.lang.CharSequence r3 = r12.messageText
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x09c1
            java.lang.CharSequence r3 = r12.messageText
            int r4 = r3.length()
            r6 = 20
            if (r4 <= r6) goto L_0x09ac
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r9 = 0
            java.lang.CharSequence r6 = r3.subSequence(r9, r6)
            r4.append(r6)
            r4.append(r10)
            java.lang.String r3 = r4.toString()
            goto L_0x09ad
        L_0x09ac:
            r9 = 0
        L_0x09ad:
            java.lang.Object[] r4 = new java.lang.Object[r15]
            r4[r9] = r13
            r6 = 1
            r4[r6] = r3
            java.lang.String r6 = r5.title
            r9 = 2
            r4[r9] = r6
            r6 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x09c1:
            r9 = 2
            r3 = 2131626596(0x7f0e0a64, float:1.8880433E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r10 = 1
            r4[r10] = r6
            java.lang.String r6 = "NotificationActionPinnedNoText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x09d6:
            r6 = 0
            r9 = 2
            r10 = 1
        L_0x09d9:
            r3 = 2131626581(0x7f0e0a55, float:1.8880402E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r4[r10] = r6
            java.lang.String r6 = "NotificationActionPinnedGeo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x09eb:
            java.lang.String r3 = r12.getStickerEmoji()
            if (r3 == 0) goto L_0x0a08
            r4 = 2131626613(0x7f0e0a75, float:1.8880467E38)
            java.lang.Object[] r6 = new java.lang.Object[r15]
            r9 = 0
            r6[r9] = r13
            java.lang.String r9 = r5.title
            r10 = 1
            r6[r10] = r9
            r11 = 2
            r6[r11] = r3
            java.lang.String r9 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0a08:
            r9 = 0
            r10 = 1
            r11 = 2
            r4 = 2131626611(0x7f0e0a73, float:1.8880463E38)
            java.lang.Object[] r6 = new java.lang.Object[r11]
            r6[r9] = r13
            java.lang.String r9 = r5.title
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedSticker"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0a1d:
            if (r5 == 0) goto L_0x0d09
            org.telegram.messenger.MessageObject r11 = r0.replyMessageObject
            if (r11 != 0) goto L_0x0a35
            r3 = 2131626597(0x7f0e0a65, float:1.8880435E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0a35:
            org.telegram.messenger.MessageObject r11 = r0.replyMessageObject
            boolean r12 = r11.isMusic()
            if (r12 == 0) goto L_0x0a4f
            r3 = 2131626594(0x7f0e0a62, float:1.8880429E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedMusicChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0a4f:
            boolean r12 = r11.isVideo()
            java.lang.String r15 = "NotificationActionPinnedTextChannel"
            if (r12 == 0) goto L_0x0a9d
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0a8b
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0a8b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            r6 = 1
            r4[r6] = r3
            r6 = 2131626618(0x7f0e0a7a, float:1.8880477E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0a8b:
            r6 = 1
            r3 = 2131626621(0x7f0e0a7d, float:1.8880483E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedVideoChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0a9d:
            boolean r4 = r11.isGif()
            if (r4 == 0) goto L_0x0ae9
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0ad7
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0ad7
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            r12 = 1
            r4[r12] = r3
            r6 = 2131626618(0x7f0e0a7a, float:1.8880477E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0ad7:
            r12 = 1
            r3 = 2131626588(0x7f0e0a5c, float:1.8880416E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            java.lang.String r6 = r5.title
            r14 = 0
            r4[r14] = r6
            java.lang.String r6 = "NotificationActionPinnedGifChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0ae9:
            r12 = 1
            r14 = 0
            boolean r4 = r11.isVoice()
            if (r4 == 0) goto L_0x0b01
            r3 = 2131626624(0x7f0e0a80, float:1.888049E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            java.lang.String r6 = r5.title
            r4[r14] = r6
            java.lang.String r6 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0b01:
            boolean r4 = r11.isRoundVideo()
            if (r4 == 0) goto L_0x0b17
            r3 = 2131626609(0x7f0e0a71, float:1.888046E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            java.lang.String r6 = r5.title
            r4[r14] = r6
            java.lang.String r6 = "NotificationActionPinnedRoundChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0b17:
            boolean r4 = r11.isSticker()
            if (r4 != 0) goto L_0x0cdc
            boolean r4 = r11.isAnimatedSticker()
            if (r4 == 0) goto L_0x0b25
            goto L_0x0cdc
        L_0x0b25:
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x0b73
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r9) goto L_0x0b61
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0b61
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            r6 = 1
            r4[r6] = r3
            r6 = 2131626618(0x7f0e0a7a, float:1.8880477E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0b61:
            r6 = 1
            r3 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedFileChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0b73:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r3 != 0) goto L_0x0cca
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r3 == 0) goto L_0x0b87
            r6 = 1
            r9 = 0
            goto L_0x0ccc
        L_0x0b87:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0ba1
            r3 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0ba1:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r3 == 0) goto L_0x0bcc
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r3
            r4 = 2131626570(0x7f0e0a4a, float:1.888038E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r5.title
            r10 = 0
            r6[r10] = r9
            java.lang.String r9 = r3.first_name
            java.lang.String r10 = r3.last_name
            java.lang.String r9 = org.telegram.messenger.ContactsController.formatName(r9, r10)
            r10 = 1
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedContactChannel2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0bcc:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r3 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r4 = r3.poll
            boolean r4 = r4.quiz
            if (r4 == 0) goto L_0x0bf9
            r4 = 2131626606(0x7f0e0a6e, float:1.8880453E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r5.title
            r10 = 0
            r6[r10] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r3.poll
            java.lang.String r9 = r9.question
            r12 = 1
            r6[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0bf9:
            r6 = 2
            r10 = 0
            r12 = 1
            r4 = 2131626603(0x7f0e0a6b, float:1.8880447E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r5.title
            r6[r10] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r3.poll
            java.lang.String r9 = r9.question
            r6[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedPollChannel2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0CLASSNAME
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0c4e
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0c4e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            r6 = 1
            r4[r6] = r3
            r6 = 2131626618(0x7f0e0a7a, float:1.8880477E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0c4e:
            r6 = 1
            r3 = 2131626600(0x7f0e0a68, float:1.888044E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x0c7a
            r3 = 2131626576(0x7f0e0a50, float:1.8880392E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGameChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0c7a:
            java.lang.CharSequence r3 = r11.messageText
            if (r3 == 0) goto L_0x0cb8
            java.lang.CharSequence r3 = r11.messageText
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0cb8
            java.lang.CharSequence r3 = r11.messageText
            int r4 = r3.length()
            r6 = 20
            if (r4 <= r6) goto L_0x0ca5
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r9 = 0
            java.lang.CharSequence r6 = r3.subSequence(r9, r6)
            r4.append(r6)
            r4.append(r10)
            java.lang.String r3 = r4.toString()
            goto L_0x0ca6
        L_0x0ca5:
            r9 = 0
        L_0x0ca6:
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r4[r9] = r6
            r6 = 1
            r4[r6] = r3
            r6 = 2131626618(0x7f0e0a7a, float:1.8880477E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0cb8:
            r6 = 1
            r3 = 2131626597(0x7f0e0a65, float:1.8880435E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0cca:
            r6 = 1
            r9 = 0
        L_0x0ccc:
            r3 = 2131626582(0x7f0e0a56, float:1.8880404E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGeoChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0cdc:
            java.lang.String r3 = r11.getStickerEmoji()
            if (r3 == 0) goto L_0x0cf7
            r4 = 2131626614(0x7f0e0a76, float:1.888047E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r5.title
            r12 = 0
            r6[r12] = r9
            r15 = 1
            r6[r15] = r3
            java.lang.String r9 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0cf7:
            r12 = 0
            r15 = 1
            r4 = 2131626612(0x7f0e0a74, float:1.8880465E38)
            java.lang.Object[] r6 = new java.lang.Object[r15]
            java.lang.String r9 = r5.title
            r6[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedStickerChannel"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0d09:
            r12 = 0
            r15 = 1
            org.telegram.messenger.MessageObject r11 = r0.replyMessageObject
            if (r11 != 0) goto L_0x0d1d
            r3 = 2131626598(0x7f0e0a66, float:1.8880437E38)
            java.lang.Object[] r4 = new java.lang.Object[r15]
            r4[r12] = r13
            java.lang.String r6 = "NotificationActionPinnedNoTextUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0d1d:
            org.telegram.messenger.MessageObject r11 = r0.replyMessageObject
            boolean r12 = r11.isMusic()
            if (r12 == 0) goto L_0x0d35
            r3 = 2131626595(0x7f0e0a63, float:1.888043E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedMusicUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0d35:
            boolean r12 = r11.isVideo()
            java.lang.String r15 = "NotificationActionPinnedTextUser"
            if (r12 == 0) goto L_0x0d7f
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0d6f
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0d6f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            r9 = 1
            r4[r9] = r3
            r6 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0d6f:
            r6 = 0
            r9 = 1
            r3 = 2131626622(0x7f0e0a7e, float:1.8880485E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedVideoUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0d7f:
            boolean r4 = r11.isGif()
            if (r4 == 0) goto L_0x0dc7
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0db7
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0db7
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r12 = 0
            r4[r12] = r13
            r14 = 1
            r4[r14] = r3
            r6 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0db7:
            r12 = 0
            r14 = 1
            r3 = 2131626589(0x7f0e0a5d, float:1.8880418E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            r4[r12] = r13
            java.lang.String r6 = "NotificationActionPinnedGifUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0dc7:
            r12 = 0
            r14 = 1
            boolean r4 = r11.isVoice()
            if (r4 == 0) goto L_0x0ddd
            r3 = 2131626625(0x7f0e0a81, float:1.8880491E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            r4[r12] = r13
            java.lang.String r6 = "NotificationActionPinnedVoiceUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0ddd:
            boolean r4 = r11.isRoundVideo()
            if (r4 == 0) goto L_0x0df1
            r3 = 2131626610(0x7f0e0a72, float:1.8880461E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            r4[r12] = r13
            java.lang.String r6 = "NotificationActionPinnedRoundUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0df1:
            boolean r4 = r11.isSticker()
            if (r4 != 0) goto L_0x0f9e
            boolean r4 = r11.isAnimatedSticker()
            if (r4 == 0) goto L_0x0dff
            goto L_0x0f9e
        L_0x0dff:
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x0e49
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r9) goto L_0x0e39
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0e39
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            r9 = 1
            r4[r9] = r3
            r6 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0e39:
            r6 = 0
            r9 = 1
            r3 = 2131626574(0x7f0e0a4e, float:1.8880388E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedFileUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0e49:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r3 != 0) goto L_0x0f8e
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r3 == 0) goto L_0x0e5d
            r6 = 1
            r9 = 0
            goto L_0x0var_
        L_0x0e5d:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0e75
            r3 = 2131626585(0x7f0e0a59, float:1.888041E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedGeoLiveUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0e75:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r3 == 0) goto L_0x0e9e
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r3
            r4 = 2131626571(0x7f0e0a4b, float:1.8880382E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r13
            java.lang.String r9 = r3.first_name
            java.lang.String r10 = r3.last_name
            java.lang.String r9 = org.telegram.messenger.ContactsController.formatName(r9, r10)
            r10 = 1
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedContactUser"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0e9e:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r3 == 0) goto L_0x0ee0
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r4 = r3.poll
            boolean r4 = r4.quiz
            if (r4 == 0) goto L_0x0ec9
            r4 = 2131626607(0x7f0e0a6f, float:1.8880455E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r13
            org.telegram.tgnet.TLRPC$Poll r9 = r3.poll
            java.lang.String r9 = r9.question
            r10 = 1
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedQuizUser"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0ec9:
            r6 = 2
            r9 = 0
            r10 = 1
            r4 = 2131626604(0x7f0e0a6c, float:1.8880449E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r9] = r13
            org.telegram.tgnet.TLRPC$Poll r9 = r3.poll
            java.lang.String r9 = r9.question
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedPollUser"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0ee0:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0f2a
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0f1a
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0f1a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            r9 = 1
            r4[r9] = r3
            r6 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0f1a:
            r6 = 0
            r9 = 1
            r3 = 2131626601(0x7f0e0a69, float:1.8880443E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedPhotoUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0f2a:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x0var_
            r3 = 2131626580(0x7f0e0a54, float:1.88804E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedGameUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0var_:
            java.lang.CharSequence r3 = r11.messageText
            if (r3 == 0) goto L_0x0f7e
            java.lang.CharSequence r3 = r11.messageText
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0f7e
            java.lang.CharSequence r3 = r11.messageText
            int r4 = r3.length()
            r6 = 20
            if (r4 <= r6) goto L_0x0f6d
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r9 = 0
            java.lang.CharSequence r6 = r3.subSequence(r9, r6)
            r4.append(r6)
            r4.append(r10)
            java.lang.String r3 = r4.toString()
            goto L_0x0f6e
        L_0x0f6d:
            r9 = 0
        L_0x0f6e:
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r9] = r13
            r6 = 1
            r4[r6] = r3
            r6 = 2131626619(0x7f0e0a7b, float:1.888048E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0f7e:
            r6 = 1
            r9 = 0
            r3 = 2131626598(0x7f0e0a66, float:1.8880437E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r4[r9] = r13
            java.lang.String r6 = "NotificationActionPinnedNoTextUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0f8e:
            r6 = 1
            r9 = 0
        L_0x0var_:
            r3 = 2131626586(0x7f0e0a5a, float:1.8880412E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r4[r9] = r13
            java.lang.String r6 = "NotificationActionPinnedGeoUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0f9e:
            java.lang.String r3 = r11.getStickerEmoji()
            if (r3 == 0) goto L_0x0fb7
            r4 = 2131626615(0x7f0e0a77, float:1.8880471E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r13
            r10 = 1
            r6[r10] = r3
            java.lang.String r9 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0fb7:
            r9 = 0
            r10 = 1
            r4 = 2131626616(0x7f0e0a78, float:1.8880473E38)
            java.lang.Object[] r6 = new java.lang.Object[r10]
            r6[r9] = r13
            java.lang.String r9 = "NotificationActionPinnedStickerUser"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0fc7:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme
            if (r3 == 0) goto L_0x102a
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r3 = (org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme) r3
            java.lang.String r3 = r3.emoticon
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 == 0) goto L_0x1002
            int r4 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r4 != 0) goto L_0x0fee
            r4 = 2131624895(0x7f0e03bf, float:1.8876983E38)
            r6 = 0
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = "ChatThemeDisabledYou"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            goto L_0x1000
        L_0x0fee:
            r6 = 0
            r4 = 2131624894(0x7f0e03be, float:1.887698E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r6] = r13
            r6 = 1
            r9[r6] = r3
            java.lang.String r6 = "ChatThemeDisabled"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r4, r9)
        L_0x1000:
            goto L_0x1029
        L_0x1002:
            int r4 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r4 != 0) goto L_0x1016
            r4 = 2131624892(0x7f0e03bc, float:1.8876977E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r3
            java.lang.String r9 = "ChangedChatThemeYou"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            goto L_0x1028
        L_0x1016:
            r6 = 1
            r9 = 0
            r4 = 2131624891(0x7f0e03bb, float:1.8876975E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r10[r9] = r13
            r10[r6] = r3
            java.lang.String r6 = "ChangedChatThemeTo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r4, r10)
        L_0x1028:
        L_0x1029:
            return r4
        L_0x102a:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByRequest
            if (r3 == 0) goto L_0x1039
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x1039:
            r3 = 0
            return r3
        L_0x103b:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            long r3 = r3.channel_id
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x1075
            boolean r3 = r5.megagroup
            if (r3 != 0) goto L_0x1075
            boolean r3 = r29.isVideoAvatar()
            if (r3 == 0) goto L_0x1063
            r3 = 2131624844(0x7f0e038c, float:1.887688E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "ChannelVideoEditNotification"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x1063:
            r4 = 1
            r9 = 0
            r3 = 2131624809(0x7f0e0369, float:1.8876808E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r4[r9] = r6
            java.lang.String r6 = "ChannelPhotoEditNotification"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x1075:
            boolean r3 = r29.isVideoAvatar()
            if (r3 == 0) goto L_0x1090
            r3 = 2131626630(0x7f0e0a86, float:1.8880502E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationEditedGroupVideo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x1090:
            r4 = 2
            r6 = 0
            r9 = 1
            r3 = 2131626629(0x7f0e0a85, float:1.88805E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r4[r9] = r6
            java.lang.String r6 = "NotificationEditedGroupPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x10a5:
            r26 = r11
            r27 = r12
        L_0x10a9:
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x10b0:
            r26 = r11
            r27 = r12
        L_0x10b4:
            r3 = 2131626626(0x7f0e0a82, float:1.8880494E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationContactJoined"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x10c4:
            r26 = r11
            r27 = r12
            boolean r10 = r29.isMediaEmpty()
            if (r10 == 0) goto L_0x10e5
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x10dd
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            return r3
        L_0x10dd:
            r3 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            return r3
        L_0x10e5:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r10 == 0) goto L_0x112b
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x110f
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x110f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            return r3
        L_0x110f:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            int r3 = r3.ttl_seconds
            if (r3 == 0) goto L_0x1121
            r3 = 2131624403(0x7f0e01d3, float:1.8875985E38)
            java.lang.String r4 = "AttachDestructingPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x1121:
            r3 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r4 = "AttachPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x112b:
            boolean r6 = r29.isVideo()
            if (r6 == 0) goto L_0x116f
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x1153
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x1153
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            return r3
        L_0x1153:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            int r3 = r3.ttl_seconds
            if (r3 == 0) goto L_0x1165
            r3 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r4 = "AttachDestructingVideo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x1165:
            r3 = 2131624427(0x7f0e01eb, float:1.8876033E38)
            java.lang.String r4 = "AttachVideo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x116f:
            boolean r4 = r29.isGame()
            if (r4 == 0) goto L_0x117f
            r3 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r4 = "AttachGame"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x117f:
            boolean r4 = r29.isVoice()
            if (r4 == 0) goto L_0x118f
            r3 = 2131624400(0x7f0e01d0, float:1.8875979E38)
            java.lang.String r4 = "AttachAudio"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x118f:
            boolean r4 = r29.isRoundVideo()
            if (r4 == 0) goto L_0x119f
            r3 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r4 = "AttachRound"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x119f:
            boolean r4 = r29.isMusic()
            if (r4 == 0) goto L_0x11af
            r3 = 2131624420(0x7f0e01e4, float:1.887602E38)
            java.lang.String r4 = "AttachMusic"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x11af:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r4 == 0) goto L_0x11c1
            r3 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r4 = "AttachContact"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x11c1:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r4 == 0) goto L_0x11e9
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            boolean r3 = r3.quiz
            if (r3 == 0) goto L_0x11df
            r3 = 2131627408(0x7f0e0d90, float:1.888208E38)
            java.lang.String r4 = "QuizPoll"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x11df:
            r3 = 2131627227(0x7f0e0cdb, float:1.8881712E38)
            java.lang.String r4 = "Poll"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x11e9:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r4 != 0) goto L_0x12c5
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r4 == 0) goto L_0x11fb
            goto L_0x12c5
        L_0x11fb:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x120d
            r3 = 2131624412(0x7f0e01dc, float:1.8876003E38)
            java.lang.String r4 = "AttachLiveLocation"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x120d:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x12ae
            boolean r4 = r29.isSticker()
            if (r4 != 0) goto L_0x1280
            boolean r4 = r29.isAnimatedSticker()
            if (r4 == 0) goto L_0x1222
            goto L_0x1280
        L_0x1222:
            boolean r4 = r29.isGif()
            if (r4 == 0) goto L_0x1254
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x124a
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x124a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            return r3
        L_0x124a:
            r3 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r4 = "AttachGif"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x1254:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r9) goto L_0x1276
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x1276
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            return r3
        L_0x1276:
            r3 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r4 = "AttachDocument"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x1280:
            java.lang.String r3 = r29.getStickerEmoji()
            if (r3 == 0) goto L_0x12a4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r6 = " "
            r4.append(r6)
            r6 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r9 = "AttachSticker"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.append(r6)
            java.lang.String r4 = r4.toString()
            return r4
        L_0x12a4:
            r4 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r6 = "AttachSticker"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            return r4
        L_0x12ae:
            java.lang.CharSequence r3 = r0.messageText
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x12bd
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x12bd:
            r3 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            return r3
        L_0x12c5:
            r3 = 2131624416(0x7f0e01e0, float:1.8876011E38)
            java.lang.String r4 = "AttachLocation"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x12cf:
            r23 = r3
            r17 = r6
            r25 = r9
            r26 = r11
            r27 = r12
            if (r31 == 0) goto L_0x12de
            r3 = 0
            r31[r3] = r3
        L_0x12de:
            r3 = 2131626319(0x7f0e094f, float:1.887987E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            return r3
        L_0x12e6:
            r1 = 2131626646(0x7f0e0a96, float:1.8880534E38)
            java.lang.String r2 = "NotificationHiddenMessage"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /*  JADX ERROR: JadxRuntimeException in pass: InitCodeVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Several immutable types in one variable: [boolean, int], vars: [r8v105 ?, r8v103 ?, r8v106 ?, r8v107 ?]
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVarType(InitCodeVariables.java:102)
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVar(InitCodeVariables.java:78)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:69)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVars(InitCodeVariables.java:51)
        	at jadx.core.dex.visitors.InitCodeVariables.visit(InitCodeVariables.java:32)
        */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r31, boolean r32, boolean[] r33, boolean[] r34) {
        /*
            r30 = this;
            r0 = r31
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x1924
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x1924
        L_0x000e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r1 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            long r3 = r3.chat_id
            r5 = 0
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            if (r7 == 0) goto L_0x0025
            long r3 = r3.chat_id
            goto L_0x0027
        L_0x0025:
            long r3 = r3.channel_id
        L_0x0027:
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r7 = r7.user_id
            r9 = 1
            r10 = 0
            if (r34 == 0) goto L_0x0033
            r34[r10] = r9
        L_0x0033:
            org.telegram.messenger.AccountInstance r11 = r30.getAccountInstance()
            android.content.SharedPreferences r11 = r11.getNotificationsSettings()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "content_preview_"
            r12.append(r13)
            r12.append(r1)
            java.lang.String r12 = r12.toString()
            boolean r12 = r11.getBoolean(r12, r9)
            boolean r13 = r31.isFcmMessage()
            java.lang.String r14 = "NotificationMessageGroupNoText"
            java.lang.String r15 = "NotificationMessageNoText"
            if (r13 == 0) goto L_0x00f0
            int r13 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x0087
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x0087
            if (r12 == 0) goto L_0x0072
            java.lang.String r5 = "EnablePreviewAll"
            boolean r5 = r11.getBoolean(r5, r9)
            if (r5 != 0) goto L_0x006d
            goto L_0x0072
        L_0x006d:
            r18 = r11
            r11 = 0
            goto L_0x00e9
        L_0x0072:
            if (r34 == 0) goto L_0x0078
            r5 = 0
            r34[r5] = r5
            goto L_0x0079
        L_0x0078:
            r5 = 0
        L_0x0079:
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r9 = r0.localName
            r6[r5] = r9
            r5 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r15, r5, r6)
            return r5
        L_0x0087:
            int r13 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x00e6
            if (r12 == 0) goto L_0x00aa
            boolean r13 = r0.localChannel
            if (r13 != 0) goto L_0x0099
            java.lang.String r13 = "EnablePreviewGroup"
            boolean r13 = r11.getBoolean(r13, r9)
            if (r13 == 0) goto L_0x00aa
        L_0x0099:
            boolean r13 = r0.localChannel
            if (r13 == 0) goto L_0x00a6
            java.lang.String r13 = "EnablePreviewChannel"
            boolean r13 = r11.getBoolean(r13, r9)
            if (r13 != 0) goto L_0x00a6
            goto L_0x00aa
        L_0x00a6:
            r18 = r11
            r11 = 0
            goto L_0x00e9
        L_0x00aa:
            if (r34 == 0) goto L_0x00af
            r13 = 0
            r34[r13] = r13
        L_0x00af:
            org.telegram.tgnet.TLRPC$Message r13 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r13 = r13.peer_id
            r18 = r11
            long r10 = r13.channel_id
            int r13 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x00d2
            boolean r5 = r31.isSupergroup()
            if (r5 != 0) goto L_0x00d2
            r5 = 2131624793(0x7f0e0359, float:1.8876776E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r9 = r0.localName
            r10 = 0
            r6[r10] = r9
            java.lang.String r9 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r5, r6)
            return r5
        L_0x00d2:
            r5 = 2131626669(0x7f0e0aad, float:1.888058E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r10 = r0.localUserName
            r11 = 0
            r6[r11] = r10
            java.lang.String r10 = r0.localName
            r6[r9] = r10
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r14, r5, r6)
            return r5
        L_0x00e6:
            r18 = r11
            r11 = 0
        L_0x00e9:
            r33[r11] = r9
            java.lang.CharSequence r5 = r0.messageText
            java.lang.String r5 = (java.lang.String) r5
            return r5
        L_0x00f0:
            r18 = r11
            org.telegram.messenger.UserConfig r10 = r30.getUserConfig()
            long r10 = r10.getClientUserId()
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x0108
            long r7 = r31.getFromChatId()
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x0110
            long r7 = -r3
            goto L_0x0110
        L_0x0108:
            int r13 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
            if (r13 != 0) goto L_0x0110
            long r7 = r31.getFromChatId()
        L_0x0110:
            int r13 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x011f
            int r13 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x011a
            long r1 = -r3
            goto L_0x011f
        L_0x011a:
            int r13 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r13 == 0) goto L_0x011f
            r1 = r7
        L_0x011f:
            r13 = 0
            int r19 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
            if (r19 <= 0) goto L_0x0158
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            boolean r9 = r9.from_scheduled
            if (r9 == 0) goto L_0x0144
            int r9 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r9 != 0) goto L_0x0139
            r9 = 2131626344(0x7f0e0968, float:1.8879922E38)
            java.lang.String r5 = "MessageScheduledReminderNotification"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r5, r9)
            r6 = r14
            goto L_0x016d
        L_0x0139:
            r5 = 2131626690(0x7f0e0ac2, float:1.8880623E38)
            java.lang.String r6 = "NotificationMessageScheduledName"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = r14
            goto L_0x016d
        L_0x0144:
            org.telegram.messenger.MessagesController r5 = r30.getMessagesController()
            java.lang.Long r6 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 == 0) goto L_0x0156
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r5)
        L_0x0156:
            r6 = r14
            goto L_0x016d
        L_0x0158:
            org.telegram.messenger.MessagesController r5 = r30.getMessagesController()
            r9 = r13
            r6 = r14
            long r13 = -r7
            java.lang.Long r13 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r13)
            if (r5 == 0) goto L_0x016c
            java.lang.String r13 = r5.title
            goto L_0x016d
        L_0x016c:
            r13 = r9
        L_0x016d:
            r5 = 0
            if (r13 != 0) goto L_0x0171
            return r5
        L_0x0171:
            r9 = 0
            r20 = 0
            int r14 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1))
            if (r14 == 0) goto L_0x0188
            org.telegram.messenger.MessagesController r14 = r30.getMessagesController()
            java.lang.Long r5 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r9 = r14.getChat(r5)
            if (r9 != 0) goto L_0x0188
            r5 = 0
            return r5
        L_0x0188:
            r5 = 0
            boolean r14 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
            if (r14 == 0) goto L_0x01a1
            r6 = 2131628719(0x7f0e12af, float:1.8884739E38)
            java.lang.String r14 = "YouHaveNewMessage"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r14, r6)
            r24 = r3
            r28 = r7
            r8 = r9
            r16 = r12
            goto L_0x1923
        L_0x01a1:
            java.lang.String r14 = "🎬 "
            r22 = r5
            java.lang.String r5 = "📎 "
            r23 = r6
            java.lang.String r6 = "📹 "
            r24 = r9
            java.lang.String r9 = "🖼 "
            r25 = r5
            java.lang.String r5 = "NotificationMessageText"
            r26 = r14
            r20 = 0
            int r27 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1))
            if (r27 != 0) goto L_0x07a0
            int r27 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
            if (r27 == 0) goto L_0x07a0
            if (r12 == 0) goto L_0x0781
            java.lang.String r14 = "EnablePreviewAll"
            r28 = r7
            r7 = r18
            r8 = 1
            boolean r14 = r7.getBoolean(r14, r8)
            if (r14 == 0) goto L_0x077f
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r8 == 0) goto L_0x0383
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGeoProximityReached
            if (r5 == 0) goto L_0x01f0
            java.lang.CharSequence r5 = r0.messageText
            java.lang.String r5 = r5.toString()
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x01f0:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined
            if (r5 != 0) goto L_0x036a
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r5 == 0) goto L_0x0202
            goto L_0x036a
        L_0x0202:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto
            if (r5 == 0) goto L_0x0223
            r5 = 2131626627(0x7f0e0a83, float:1.8880496E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.String r8 = "NotificationContactNewPhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0223:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation
            if (r5 == 0) goto L_0x0298
            r5 = 2131628796(0x7f0e12fc, float:1.8884895E38)
            r6 = 2
            java.lang.Object[] r8 = new java.lang.Object[r6]
            org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r6 = r6.formatterYear
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            int r9 = r9.date
            long r14 = (long) r9
            r20 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 * r20
            java.lang.String r6 = r6.format((long) r14)
            r9 = 0
            r8[r9] = r6
            org.telegram.messenger.LocaleController r6 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r6 = r6.formatterDay
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            int r9 = r9.date
            long r14 = (long) r9
            long r14 = r14 * r20
            java.lang.String r6 = r6.format((long) r14)
            r9 = 1
            r8[r9] = r6
            java.lang.String r6 = "formatDateAtTime"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r5, r8)
            r6 = 2131626696(0x7f0e0ac8, float:1.8880635E38)
            r8 = 4
            java.lang.Object[] r8 = new java.lang.Object[r8]
            org.telegram.messenger.UserConfig r14 = r30.getUserConfig()
            org.telegram.tgnet.TLRPC$User r14 = r14.getCurrentUser()
            java.lang.String r14 = r14.first_name
            r15 = 0
            r8[r15] = r14
            r8[r9] = r5
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            java.lang.String r9 = r9.title
            r14 = 2
            r8[r14] = r9
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            java.lang.String r9 = r9.address
            r14 = 3
            r8[r14] = r9
            java.lang.String r9 = "NotificationUnrecognizedDevice"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r6, r8)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0298:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore
            if (r5 != 0) goto L_0x035a
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent
            if (r5 == 0) goto L_0x02aa
            goto L_0x035a
        L_0x02aa:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r5 == 0) goto L_0x02e0
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5.video
            if (r5 == 0) goto L_0x02cd
            r5 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r6 = "CallMessageVideoIncomingMissed"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x02cd:
            r5 = 2131624661(0x7f0e02d5, float:1.8876508E38)
            java.lang.String r6 = "CallMessageIncomingMissed"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x02e0:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme
            if (r5 == 0) goto L_0x0350
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r5 = (org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme) r5
            java.lang.String r5 = r5.emoticon
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 == 0) goto L_0x031c
            int r6 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x0307
            r6 = 2131624895(0x7f0e03bf, float:1.8876983E38)
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.String r14 = "ChatThemeDisabledYou"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r14, r6, r9)
            goto L_0x0319
        L_0x0307:
            r8 = 0
            r6 = 2131624894(0x7f0e03be, float:1.887698E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r8] = r13
            r8 = 1
            r9[r8] = r5
            java.lang.String r8 = "ChatThemeDisabled"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r8, r6, r9)
        L_0x0319:
            r8 = 1
            r14 = 0
            goto L_0x0343
        L_0x031c:
            int r6 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x0330
            r6 = 2131624892(0x7f0e03bc, float:1.8876977E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r14 = 0
            r9[r14] = r5
            java.lang.String r15 = "ChangedChatThemeYou"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r15, r6, r9)
            goto L_0x0342
        L_0x0330:
            r8 = 1
            r14 = 0
            r6 = 2131624891(0x7f0e03bb, float:1.8876975E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r14] = r13
            r9[r8] = r5
            java.lang.String r15 = "ChangedChatThemeTo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r15, r6, r9)
        L_0x0342:
        L_0x0343:
            r33[r14] = r8
            r5 = r6
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0350:
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1921
        L_0x035a:
            java.lang.CharSequence r5 = r0.messageText
            java.lang.String r5 = r5.toString()
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x036a:
            r5 = 2131626626(0x7f0e0a82, float:1.8880494E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.String r8 = "NotificationContactJoined"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0383:
            boolean r8 = r31.isMediaEmpty()
            if (r8 == 0) goto L_0x03e3
            if (r32 != 0) goto L_0x03cc
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x03b5
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            java.lang.String r9 = r9.message
            r14 = 1
            r6[r14] = r9
            r9 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x03b5:
            r8 = 0
            r14 = 1
            java.lang.Object[] r5 = new java.lang.Object[r14]
            r5[r8] = r13
            r6 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r15, r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x03cc:
            r6 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            r8 = 0
            r14 = 1
            java.lang.Object[] r5 = new java.lang.Object[r14]
            r5[r8] = r13
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r15, r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x03e3:
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r8 == 0) goto L_0x0466
            if (r32 != 0) goto L_0x042c
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 19
            if (r6 < r8) goto L_0x042c
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x042c
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r9)
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            java.lang.String r9 = r9.message
            r14.append(r9)
            java.lang.String r9 = r14.toString()
            r14 = 1
            r6[r14] = r9
            r9 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x042c:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            int r5 = r5.ttl_seconds
            if (r5 == 0) goto L_0x044d
            r5 = 2131626687(0x7f0e0abf, float:1.8880617E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageSDPhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x044d:
            r6 = 1
            r8 = 0
            r5 = 2131626683(0x7f0e0abb, float:1.888061E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessagePhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0466:
            boolean r8 = r31.isVideo()
            if (r8 == 0) goto L_0x04e7
            if (r32 != 0) goto L_0x04ad
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 19
            if (r8 < r9) goto L_0x04ad
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            java.lang.String r8 = r8.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x04ad
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r9 = 0
            r8[r9] = r13
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r6)
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            r14.append(r6)
            java.lang.String r6 = r14.toString()
            r14 = 1
            r8[r14] = r6
            r6 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r8)
            r33[r9] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x04ad:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            int r5 = r5.ttl_seconds
            if (r5 == 0) goto L_0x04ce
            r5 = 2131626688(0x7f0e0ac0, float:1.888062E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageSDVideo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x04ce:
            r6 = 1
            r8 = 0
            r5 = 2131626694(0x7f0e0ac6, float:1.8880631E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageVideo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x04e7:
            r8 = 0
            boolean r6 = r31.isGame()
            if (r6 == 0) goto L_0x0511
            r5 = 2131626656(0x7f0e0aa0, float:1.8880554E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r8] = r13
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            org.telegram.tgnet.TLRPC$TL_game r8 = r8.game
            java.lang.String r8 = r8.title
            r9 = 1
            r6[r9] = r8
            java.lang.String r8 = "NotificationMessageGame"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0511:
            r9 = 1
            boolean r6 = r31.isVoice()
            if (r6 == 0) goto L_0x0530
            r5 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            r8 = 0
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageAudio"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0530:
            r8 = 0
            boolean r6 = r31.isRoundVideo()
            if (r6 == 0) goto L_0x054e
            r5 = 2131626686(0x7f0e0abe, float:1.8880615E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageRound"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x054e:
            boolean r6 = r31.isMusic()
            if (r6 == 0) goto L_0x056b
            r5 = 2131626681(0x7f0e0ab9, float:1.8880605E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageMusic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x056b:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r6 == 0) goto L_0x059d
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5
            r6 = 2131626652(0x7f0e0a9c, float:1.8880546E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r9 = 0
            r8[r9] = r13
            java.lang.String r9 = r5.first_name
            java.lang.String r14 = r5.last_name
            java.lang.String r9 = org.telegram.messenger.ContactsController.formatName(r9, r14)
            r14 = 1
            r8[r14] = r9
            java.lang.String r9 = "NotificationMessageContact2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r6, r8)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x059d:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r6 == 0) goto L_0x05ea
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r6 = r5.poll
            boolean r6 = r6.quiz
            if (r6 == 0) goto L_0x05c9
            r6 = 2131626685(0x7f0e0abd, float:1.8880613E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r9 = 0
            r8[r9] = r13
            org.telegram.tgnet.TLRPC$Poll r9 = r5.poll
            java.lang.String r9 = r9.question
            r14 = 1
            r8[r14] = r9
            java.lang.String r9 = "NotificationMessageQuiz2"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r8)
            r5 = r6
            goto L_0x05e0
        L_0x05c9:
            r8 = 2
            r9 = 0
            r14 = 1
            r6 = 2131626684(0x7f0e0abc, float:1.8880611E38)
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r8[r9] = r13
            org.telegram.tgnet.TLRPC$Poll r9 = r5.poll
            java.lang.String r9 = r9.question
            r8[r14] = r9
            java.lang.String r9 = "NotificationMessagePoll2"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r8)
            r5 = r6
        L_0x05e0:
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x05ea:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r6 != 0) goto L_0x0766
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r6 == 0) goto L_0x05fe
            r8 = 0
            r14 = 1
            goto L_0x0768
        L_0x05fe:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x061f
            r5 = 2131626679(0x7f0e0ab7, float:1.88806E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageLiveLocation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x061f:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r6 == 0) goto L_0x0727
            boolean r6 = r31.isSticker()
            if (r6 != 0) goto L_0x06f3
            boolean r6 = r31.isAnimatedSticker()
            if (r6 == 0) goto L_0x0635
            goto L_0x06f3
        L_0x0635:
            boolean r6 = r31.isGif()
            if (r6 == 0) goto L_0x0697
            if (r32 != 0) goto L_0x067e
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 19
            if (r6 < r8) goto L_0x067e
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x067e
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r14 = r26
            r9.append(r14)
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            java.lang.String r14 = r14.message
            r9.append(r14)
            java.lang.String r9 = r9.toString()
            r14 = 1
            r6[r14] = r9
            r9 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x067e:
            r8 = 0
            r14 = 1
            r5 = 2131626658(0x7f0e0aa2, float:1.8880558E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageGif"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0697:
            if (r32 != 0) goto L_0x06da
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 19
            if (r6 < r8) goto L_0x06da
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x06da
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r15 = r25
            r9.append(r15)
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            java.lang.String r14 = r14.message
            r9.append(r14)
            java.lang.String r9 = r9.toString()
            r14 = 1
            r6[r14] = r9
            r9 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x06da:
            r8 = 0
            r14 = 1
            r5 = 2131626653(0x7f0e0a9d, float:1.8880548E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageDocument"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x06f3:
            java.lang.String r5 = r31.getStickerEmoji()
            if (r5 == 0) goto L_0x070d
            r6 = 2131626692(0x7f0e0ac4, float:1.8880627E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r9 = 0
            r8[r9] = r13
            r14 = 1
            r8[r14] = r5
            java.lang.String r9 = "NotificationMessageStickerEmoji"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r8)
            r5 = r6
            goto L_0x071d
        L_0x070d:
            r9 = 0
            r14 = 1
            r6 = 2131626691(0x7f0e0ac3, float:1.8880625E38)
            java.lang.Object[] r8 = new java.lang.Object[r14]
            r8[r9] = r13
            java.lang.String r9 = "NotificationMessageSticker"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r8)
            r5 = r6
        L_0x071d:
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0727:
            if (r32 != 0) goto L_0x074f
            java.lang.CharSequence r6 = r0.messageText
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x074f
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.CharSequence r9 = r0.messageText
            r14 = 1
            r6[r14] = r9
            r9 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x074f:
            r8 = 0
            r14 = 1
            java.lang.Object[] r5 = new java.lang.Object[r14]
            r5[r8] = r13
            r6 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r15, r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x0766:
            r8 = 0
            r14 = 1
        L_0x0768:
            r5 = 2131626680(0x7f0e0ab8, float:1.8880603E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageMap"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x077f:
            r8 = 0
            goto L_0x0786
        L_0x0781:
            r28 = r7
            r7 = r18
            r8 = 0
        L_0x0786:
            if (r34 == 0) goto L_0x078a
            r34[r8] = r8
        L_0x078a:
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r8] = r13
            r6 = 2131626682(0x7f0e0aba, float:1.8880607E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r15, r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x1923
        L_0x07a0:
            r28 = r7
            r7 = r18
            r15 = r25
            r14 = r26
            r20 = 0
            int r8 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1))
            if (r8 == 0) goto L_0x1919
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r24)
            if (r8 == 0) goto L_0x07be
            r8 = r24
            r24 = r3
            boolean r3 = r8.megagroup
            if (r3 != 0) goto L_0x07c2
            r3 = 1
            goto L_0x07c3
        L_0x07be:
            r8 = r24
            r24 = r3
        L_0x07c2:
            r3 = 0
        L_0x07c3:
            if (r12 == 0) goto L_0x18e1
            if (r3 != 0) goto L_0x07d3
            java.lang.String r4 = "EnablePreviewGroup"
            r16 = r12
            r12 = 1
            boolean r4 = r7.getBoolean(r4, r12)
            if (r4 != 0) goto L_0x07e0
            goto L_0x07d5
        L_0x07d3:
            r16 = r12
        L_0x07d5:
            if (r3 == 0) goto L_0x18dc
            java.lang.String r4 = "EnablePreviewChannel"
            r12 = 1
            boolean r4 = r7.getBoolean(r4, r12)
            if (r4 == 0) goto L_0x18dc
        L_0x07e0:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r4 == 0) goto L_0x12a7
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r4 == 0) goto L_0x091e
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r4 = r4.user_id
            r14 = 0
            int r6 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r6 != 0) goto L_0x0818
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            int r6 = r6.size()
            r9 = 1
            if (r6 != r9) goto L_0x0818
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            r9 = 0
            java.lang.Object r6 = r6.get(r9)
            java.lang.Long r6 = (java.lang.Long) r6
            long r4 = r6.longValue()
        L_0x0818:
            r14 = 0
            int r6 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r6 == 0) goto L_0x08c1
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            r18 = r7
            long r6 = r6.channel_id
            int r9 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r9 == 0) goto L_0x0845
            boolean r6 = r8.megagroup
            if (r6 != 0) goto L_0x0845
            r6 = 2131624742(0x7f0e0326, float:1.8876672E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r9 = 0
            r7[r9] = r13
            java.lang.String r9 = r8.title
            r12 = 1
            r7[r12] = r9
            java.lang.String r9 = "ChannelAddedByNotification"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            r5 = r6
            goto L_0x091c
        L_0x0845:
            int r6 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x0860
            r6 = 2131626648(0x7f0e0a98, float:1.8880538E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r9 = 0
            r7[r9] = r13
            java.lang.String r9 = r8.title
            r12 = 1
            r7[r12] = r9
            java.lang.String r9 = "NotificationInvitedToGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            r5 = r6
            goto L_0x091c
        L_0x0860:
            org.telegram.messenger.MessagesController r6 = r30.getMessagesController()
            java.lang.Long r7 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 != 0) goto L_0x0870
            r7 = 0
            return r7
        L_0x0870:
            long r14 = r6.id
            int r7 = (r28 > r14 ? 1 : (r28 == r14 ? 0 : -1))
            if (r7 != 0) goto L_0x08a4
            boolean r7 = r8.megagroup
            if (r7 == 0) goto L_0x088f
            r7 = 2131626633(0x7f0e0a89, float:1.8880508E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r12 = 0
            r9[r12] = r13
            java.lang.String r12 = r8.title
            r14 = 1
            r9[r14] = r12
            java.lang.String r12 = "NotificationGroupAddSelfMega"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r12, r7, r9)
            goto L_0x08bf
        L_0x088f:
            r9 = 2
            r12 = 0
            r14 = 1
            r7 = 2131626632(0x7f0e0a88, float:1.8880506E38)
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r12] = r13
            java.lang.String r12 = r8.title
            r9[r14] = r12
            java.lang.String r12 = "NotificationGroupAddSelf"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r12, r7, r9)
            goto L_0x08bf
        L_0x08a4:
            r12 = 0
            r14 = 1
            r7 = 2131626631(0x7f0e0a87, float:1.8880504E38)
            r9 = 3
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r12] = r13
            java.lang.String r12 = r8.title
            r9[r14] = r12
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r6)
            r14 = 2
            r9[r14] = r12
            java.lang.String r12 = "NotificationGroupAddMember"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r12, r7, r9)
        L_0x08bf:
            r5 = r7
            goto L_0x091c
        L_0x08c1:
            r18 = r7
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r7 = 0
        L_0x08c9:
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            java.util.ArrayList<java.lang.Long> r9 = r9.users
            int r9 = r9.size()
            if (r7 >= r9) goto L_0x0900
            org.telegram.messenger.MessagesController r9 = r30.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r12 = r12.action
            java.util.ArrayList<java.lang.Long> r12 = r12.users
            java.lang.Object r12 = r12.get(r7)
            java.lang.Long r12 = (java.lang.Long) r12
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r12)
            if (r9 == 0) goto L_0x08fd
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r9)
            int r14 = r6.length()
            if (r14 == 0) goto L_0x08fa
            java.lang.String r14 = ", "
            r6.append(r14)
        L_0x08fa:
            r6.append(r12)
        L_0x08fd:
            int r7 = r7 + 1
            goto L_0x08c9
        L_0x0900:
            r7 = 2131626631(0x7f0e0a87, float:1.8880504E38)
            r9 = 3
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r12 = 0
            r9[r12] = r13
            java.lang.String r12 = r8.title
            r14 = 1
            r9[r14] = r12
            java.lang.String r12 = r6.toString()
            r14 = 2
            r9[r14] = r12
            java.lang.String r12 = "NotificationGroupAddMember"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r12, r7, r9)
            r5 = r7
        L_0x091c:
            goto L_0x1923
        L_0x091e:
            r18 = r7
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGroupCall
            if (r4 == 0) goto L_0x093e
            r4 = 2131626635(0x7f0e0a8b, float:1.8880512E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationGroupCreatedCall"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x093e:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGroupCallScheduled
            if (r4 == 0) goto L_0x094e
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x1923
        L_0x094e:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionInviteToGroupCall
            if (r4 == 0) goto L_0x0a29
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r4 = r4.user_id
            r6 = 0
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 != 0) goto L_0x0980
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            int r6 = r6.size()
            r7 = 1
            if (r6 != r7) goto L_0x0980
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            r7 = 0
            java.lang.Object r6 = r6.get(r7)
            java.lang.Long r6 = (java.lang.Long) r6
            long r4 = r6.longValue()
        L_0x0980:
            r6 = 0
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 == 0) goto L_0x09ce
            int r6 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x09a1
            r6 = 2131626640(0x7f0e0a90, float:1.8880522E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r9 = 0
            r7[r9] = r13
            java.lang.String r9 = r8.title
            r12 = 1
            r7[r12] = r9
            java.lang.String r9 = "NotificationGroupInvitedYouToCall"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            r5 = r6
            goto L_0x0a27
        L_0x09a1:
            org.telegram.messenger.MessagesController r6 = r30.getMessagesController()
            java.lang.Long r7 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 != 0) goto L_0x09b1
            r7 = 0
            return r7
        L_0x09b1:
            r7 = 2131626639(0x7f0e0a8f, float:1.888052E38)
            r9 = 3
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r12 = 0
            r9[r12] = r13
            java.lang.String r12 = r8.title
            r14 = 1
            r9[r14] = r12
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r6)
            r14 = 2
            r9[r14] = r12
            java.lang.String r12 = "NotificationGroupInvitedToCall"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r12, r7, r9)
            r5 = r6
            goto L_0x0a27
        L_0x09ce:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r7 = 0
        L_0x09d4:
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            java.util.ArrayList<java.lang.Long> r9 = r9.users
            int r9 = r9.size()
            if (r7 >= r9) goto L_0x0a0b
            org.telegram.messenger.MessagesController r9 = r30.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r12 = r12.action
            java.util.ArrayList<java.lang.Long> r12 = r12.users
            java.lang.Object r12 = r12.get(r7)
            java.lang.Long r12 = (java.lang.Long) r12
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r12)
            if (r9 == 0) goto L_0x0a08
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r9)
            int r14 = r6.length()
            if (r14 == 0) goto L_0x0a05
            java.lang.String r14 = ", "
            r6.append(r14)
        L_0x0a05:
            r6.append(r12)
        L_0x0a08:
            int r7 = r7 + 1
            goto L_0x09d4
        L_0x0a0b:
            r7 = 2131626639(0x7f0e0a8f, float:1.888052E38)
            r9 = 3
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r12 = 0
            r9[r12] = r13
            java.lang.String r12 = r8.title
            r14 = 1
            r9[r14] = r12
            java.lang.String r12 = r6.toString()
            r14 = 2
            r9[r14] = r12
            java.lang.String r12 = "NotificationGroupInvitedToCall"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r12, r7, r9)
            r5 = r7
        L_0x0a27:
            goto L_0x1923
        L_0x0a29:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r4 == 0) goto L_0x0a47
            r4 = 2131626649(0x7f0e0a99, float:1.888054E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationInvitedToGroupByLink"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x0a47:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle
            if (r4 == 0) goto L_0x0a69
            r4 = 2131626628(0x7f0e0a84, float:1.8880498E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.lang.String r6 = r6.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationEditedGroupName"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x0a69:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r4 != 0) goto L_0x1239
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto
            if (r4 == 0) goto L_0x0a7b
            goto L_0x1239
        L_0x0a7b:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r4 == 0) goto L_0x0af6
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r4 = r4.user_id
            int r6 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x0aa3
            r4 = 2131626642(0x7f0e0a92, float:1.8880526E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationGroupKickYou"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x0aa3:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r4 = r4.user_id
            int r6 = (r4 > r28 ? 1 : (r4 == r28 ? 0 : -1))
            if (r6 != 0) goto L_0x0ac3
            r4 = 2131626643(0x7f0e0a93, float:1.8880528E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationGroupLeftMember"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x0ac3:
            org.telegram.messenger.MessagesController r4 = r30.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            long r5 = r5.user_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 != 0) goto L_0x0ad9
            r5 = 0
            return r5
        L_0x0ad9:
            r5 = 2131626641(0x7f0e0a91, float:1.8880524E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = org.telegram.messenger.UserObject.getUserName(r4)
            r9 = 2
            r6[r9] = r7
            java.lang.String r7 = "NotificationGroupKickMember"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x1923
        L_0x0af6:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate
            if (r4 == 0) goto L_0x0b06
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x1923
        L_0x0b06:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x0b16
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x1923
        L_0x0b16:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r4 == 0) goto L_0x0b31
            r4 = 2131624147(0x7f0e00d3, float:1.8875465E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = r8.title
            r7 = 0
            r5[r7] = r6
            java.lang.String r6 = "ActionMigrateFromGroupNotify"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x0b31:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r4 == 0) goto L_0x0b50
            r4 = 2131624147(0x7f0e00d3, float:1.8875465E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.lang.String r6 = r6.title
            r7 = 0
            r5[r7] = r6
            java.lang.String r6 = "ActionMigrateFromGroupNotify"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x0b50:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken
            if (r4 == 0) goto L_0x0b60
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x1923
        L_0x0b60:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r4 == 0) goto L_0x11b5
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r4 == 0) goto L_0x0e73
            boolean r4 = r8.megagroup
            if (r4 == 0) goto L_0x0b74
            goto L_0x0e73
        L_0x0b74:
            org.telegram.messenger.MessageObject r4 = r0.replyMessageObject
            if (r4 != 0) goto L_0x0b8b
            r4 = 2131626597(0x7f0e0a65, float:1.8880435E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = r8.title
            r7 = 0
            r5[r7] = r6
            java.lang.String r6 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x0b8b:
            org.telegram.messenger.MessageObject r4 = r0.replyMessageObject
            boolean r5 = r4.isMusic()
            if (r5 == 0) goto L_0x0ba6
            r5 = 2131626594(0x7f0e0a62, float:1.8880429E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedMusicChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0ba6:
            boolean r5 = r4.isVideo()
            r7 = 2131626618(0x7f0e0a7a, float:1.8880477E38)
            java.lang.String r12 = "NotificationActionPinnedTextChannel"
            if (r5 == 0) goto L_0x0bf8
            int r5 = android.os.Build.VERSION.SDK_INT
            r9 = 19
            if (r5 < r9) goto L_0x0be5
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0be5
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r6)
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            java.lang.String r6 = r6.message
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r8.title
            r14 = 0
            r6[r14] = r9
            r9 = 1
            r6[r9] = r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x0e71
        L_0x0be5:
            r9 = 1
            r5 = 2131626621(0x7f0e0a7d, float:1.8880483E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedVideoChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0bf8:
            boolean r5 = r4.isGif()
            if (r5 == 0) goto L_0x0CLASSNAME
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r14)
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            java.lang.String r6 = r6.message
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r8.title
            r14 = 0
            r6[r14] = r9
            r14 = 1
            r6[r14] = r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x0e71
        L_0x0CLASSNAME:
            r14 = 1
            r5 = 2131626588(0x7f0e0a5c, float:1.8880416E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            java.lang.String r7 = r8.title
            r17 = 0
            r6[r17] = r7
            java.lang.String r7 = "NotificationActionPinnedGifChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0CLASSNAME:
            r14 = 1
            r17 = 0
            boolean r5 = r4.isVoice()
            if (r5 == 0) goto L_0x0CLASSNAME
            r5 = 2131626624(0x7f0e0a80, float:1.888049E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            java.lang.String r7 = r8.title
            r6[r17] = r7
            java.lang.String r7 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0CLASSNAME:
            boolean r5 = r4.isRoundVideo()
            if (r5 == 0) goto L_0x0CLASSNAME
            r5 = 2131626609(0x7f0e0a71, float:1.888046E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            java.lang.String r7 = r8.title
            r6[r17] = r7
            java.lang.String r7 = "NotificationActionPinnedRoundChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0CLASSNAME:
            boolean r5 = r4.isSticker()
            if (r5 != 0) goto L_0x0e44
            boolean r5 = r4.isAnimatedSticker()
            if (r5 == 0) goto L_0x0CLASSNAME
            goto L_0x0e44
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r5 == 0) goto L_0x0cd4
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0cc1
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0cc1
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r15)
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            java.lang.String r6 = r6.message
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r8.title
            r14 = 0
            r6[r14] = r9
            r9 = 1
            r6[r9] = r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x0e71
        L_0x0cc1:
            r9 = 1
            r5 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedFileChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0cd4:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r5 != 0) goto L_0x0e32
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r5 == 0) goto L_0x0ce8
            r9 = 1
            r12 = 0
            goto L_0x0e34
        L_0x0ce8:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0d03
            r5 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0d03:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r5 == 0) goto L_0x0d2f
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5
            r6 = 2131626570(0x7f0e0a4a, float:1.888038E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r9 = r8.title
            r12 = 0
            r7[r12] = r9
            java.lang.String r9 = r5.first_name
            java.lang.String r12 = r5.last_name
            java.lang.String r9 = org.telegram.messenger.ContactsController.formatName(r9, r12)
            r12 = 1
            r7[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedContactChannel2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            goto L_0x0e71
        L_0x0d2f:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r5 == 0) goto L_0x0d77
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r6 = r5.poll
            boolean r6 = r6.quiz
            if (r6 == 0) goto L_0x0d5c
            r6 = 2131626606(0x7f0e0a6e, float:1.8880453E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r9 = r8.title
            r12 = 0
            r7[r12] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r5.poll
            java.lang.String r9 = r9.question
            r14 = 1
            r7[r14] = r9
            java.lang.String r9 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            goto L_0x0d74
        L_0x0d5c:
            r7 = 2
            r12 = 0
            r14 = 1
            r6 = 2131626603(0x7f0e0a6b, float:1.8880447E38)
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r9 = r8.title
            r7[r12] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r5.poll
            java.lang.String r9 = r9.question
            r7[r14] = r9
            java.lang.String r9 = "NotificationActionPinnedPollChannel2"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
        L_0x0d74:
            r5 = r6
            goto L_0x0e71
        L_0x0d77:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0dc6
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0db3
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0db3
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r9)
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            java.lang.String r6 = r6.message
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r8.title
            r14 = 0
            r6[r14] = r9
            r9 = 1
            r6[r9] = r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x0e71
        L_0x0db3:
            r9 = 1
            r5 = 2131626600(0x7f0e0a68, float:1.888044E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0dc6:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r5 == 0) goto L_0x0de1
            r5 = 2131626576(0x7f0e0a50, float:1.8880392E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGameChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0de1:
            java.lang.CharSequence r5 = r4.messageText
            if (r5 == 0) goto L_0x0e20
            java.lang.CharSequence r5 = r4.messageText
            int r5 = r5.length()
            if (r5 <= 0) goto L_0x0e20
            java.lang.CharSequence r5 = r4.messageText
            int r6 = r5.length()
            r9 = 20
            if (r6 <= r9) goto L_0x0e10
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r9 = 20
            r14 = 0
            java.lang.CharSequence r9 = r5.subSequence(r14, r9)
            r6.append(r9)
            java.lang.String r9 = "..."
            r6.append(r9)
            java.lang.String r5 = r6.toString()
            goto L_0x0e11
        L_0x0e10:
            r14 = 0
        L_0x0e11:
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r8.title
            r6[r14] = r9
            r9 = 1
            r6[r9] = r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x0e71
        L_0x0e20:
            r9 = 1
            r5 = 2131626597(0x7f0e0a65, float:1.8880435E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r12 = 0
            r6[r12] = r7
            java.lang.String r7 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0e32:
            r9 = 1
            r12 = 0
        L_0x0e34:
            r5 = 2131626582(0x7f0e0a56, float:1.8880404E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r6[r12] = r7
            java.lang.String r7 = "NotificationActionPinnedGeoChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e71
        L_0x0e44:
            java.lang.String r5 = r4.getStickerEmoji()
            if (r5 == 0) goto L_0x0e5f
            r6 = 2131626614(0x7f0e0a76, float:1.888047E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r9 = r8.title
            r12 = 0
            r7[r12] = r9
            r9 = 1
            r7[r9] = r5
            java.lang.String r9 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            goto L_0x0e70
        L_0x0e5f:
            r9 = 1
            r12 = 0
            r6 = 2131626612(0x7f0e0a74, float:1.8880465E38)
            java.lang.Object[] r7 = new java.lang.Object[r9]
            java.lang.String r9 = r8.title
            r7[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedStickerChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
        L_0x0e70:
            r5 = r6
        L_0x0e71:
            goto L_0x1923
        L_0x0e73:
            org.telegram.messenger.MessageObject r4 = r0.replyMessageObject
            if (r4 != 0) goto L_0x0e8d
            r4 = 2131626596(0x7f0e0a64, float:1.8880433E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationActionPinnedNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x0e8d:
            org.telegram.messenger.MessageObject r4 = r0.replyMessageObject
            boolean r5 = r4.isMusic()
            if (r5 == 0) goto L_0x0eab
            r5 = 2131626593(0x7f0e0a61, float:1.8880427E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedMusic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x0eab:
            boolean r5 = r4.isVideo()
            r7 = 2131626617(0x7f0e0a79, float:1.8880475E38)
            java.lang.String r12 = "NotificationActionPinnedText"
            if (r5 == 0) goto L_0x0var_
            int r5 = android.os.Build.VERSION.SDK_INT
            r9 = 19
            if (r5 < r9) goto L_0x0eed
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0eed
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r6)
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            java.lang.String r6 = r6.message
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r13
            r9 = 1
            r6[r9] = r5
            java.lang.String r9 = r8.title
            r14 = 2
            r6[r14] = r9
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x11b3
        L_0x0eed:
            r14 = 2
            r5 = 2131626620(0x7f0e0a7c, float:1.8880481E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedVideo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x0var_:
            boolean r5 = r4.isGif()
            if (r5 == 0) goto L_0x0var_
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0var_
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r14)
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            java.lang.String r6 = r6.message
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r13
            r9 = 1
            r6[r9] = r5
            java.lang.String r9 = r8.title
            r14 = 2
            r6[r14] = r9
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x11b3
        L_0x0var_:
            r14 = 2
            r5 = 2131626587(0x7f0e0a5b, float:1.8880414E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGif"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x0var_:
            boolean r5 = r4.isVoice()
            if (r5 == 0) goto L_0x0var_
            r5 = 2131626623(0x7f0e0a7f, float:1.8880487E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedVoice"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x0var_:
            boolean r5 = r4.isRoundVideo()
            if (r5 == 0) goto L_0x0f8e
            r5 = 2131626608(0x7f0e0a70, float:1.8880457E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedRound"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x0f8e:
            boolean r5 = r4.isSticker()
            if (r5 != 0) goto L_0x1180
            boolean r5 = r4.isAnimatedSticker()
            if (r5 == 0) goto L_0x0f9c
            goto L_0x1180
        L_0x0f9c:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r5 == 0) goto L_0x0ff1
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0fdb
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0fdb
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r15)
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            java.lang.String r6 = r6.message
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r13
            r9 = 1
            r6[r9] = r5
            java.lang.String r9 = r8.title
            r14 = 2
            r6[r14] = r9
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x11b3
        L_0x0fdb:
            r14 = 2
            r5 = 2131626572(0x7f0e0a4c, float:1.8880384E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedFile"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x0ff1:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r5 != 0) goto L_0x116b
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r5 == 0) goto L_0x1006
            r7 = 0
            r9 = 1
            r14 = 2
            goto L_0x116e
        L_0x1006:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x1024
            r5 = 2131626583(0x7f0e0a57, float:1.8880406E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGeoLive"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x1024:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r5 == 0) goto L_0x1053
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5
            r6 = 2131626569(0x7f0e0a49, float:1.8880378E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r9 = 0
            r7[r9] = r13
            java.lang.String r9 = r8.title
            r12 = 1
            r7[r12] = r9
            java.lang.String r9 = r5.first_name
            java.lang.String r12 = r5.last_name
            java.lang.String r9 = org.telegram.messenger.ContactsController.formatName(r9, r12)
            r12 = 2
            r7[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedContact2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            goto L_0x11b3
        L_0x1053:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r5 == 0) goto L_0x10a1
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r6 = r5.poll
            boolean r6 = r6.quiz
            if (r6 == 0) goto L_0x1083
            r6 = 2131626605(0x7f0e0a6d, float:1.888045E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r9 = 0
            r7[r9] = r13
            java.lang.String r9 = r8.title
            r12 = 1
            r7[r12] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r5.poll
            java.lang.String r9 = r9.question
            r12 = 2
            r7[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedQuiz2"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            goto L_0x109e
        L_0x1083:
            r6 = 2131626602(0x7f0e0a6a, float:1.8880445E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r9 = 0
            r7[r9] = r13
            java.lang.String r9 = r8.title
            r12 = 1
            r7[r12] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r5.poll
            java.lang.String r9 = r9.question
            r12 = 2
            r7[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedPoll2"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
        L_0x109e:
            r5 = r6
            goto L_0x11b3
        L_0x10a1:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r5 == 0) goto L_0x10f6
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x10e0
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x10e0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r9)
            org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
            java.lang.String r6 = r6.message
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r13
            r9 = 1
            r6[r9] = r5
            java.lang.String r9 = r8.title
            r14 = 2
            r6[r14] = r9
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x11b3
        L_0x10e0:
            r14 = 2
            r5 = 2131626599(0x7f0e0a67, float:1.8880439E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedPhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x10f6:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r5 == 0) goto L_0x1114
            r5 = 2131626575(0x7f0e0a4f, float:1.888039E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGame"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x1114:
            java.lang.CharSequence r5 = r4.messageText
            if (r5 == 0) goto L_0x1156
            java.lang.CharSequence r5 = r4.messageText
            int r5 = r5.length()
            if (r5 <= 0) goto L_0x1156
            java.lang.CharSequence r5 = r4.messageText
            int r6 = r5.length()
            r9 = 20
            if (r6 <= r9) goto L_0x1143
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r9 = 20
            r14 = 0
            java.lang.CharSequence r9 = r5.subSequence(r14, r9)
            r6.append(r9)
            java.lang.String r9 = "..."
            r6.append(r9)
            java.lang.String r5 = r6.toString()
            goto L_0x1144
        L_0x1143:
            r14 = 0
        L_0x1144:
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r14] = r13
            r9 = 1
            r6[r9] = r5
            java.lang.String r9 = r8.title
            r14 = 2
            r6[r14] = r9
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x11b3
        L_0x1156:
            r14 = 2
            r5 = 2131626596(0x7f0e0a64, float:1.8880433E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x116b:
            r7 = 0
            r9 = 1
            r14 = 2
        L_0x116e:
            r5 = 2131626581(0x7f0e0a55, float:1.8880402E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGeo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11b3
        L_0x1180:
            java.lang.String r5 = r4.getStickerEmoji()
            if (r5 == 0) goto L_0x119e
            r6 = 2131626613(0x7f0e0a75, float:1.8880467E38)
            r7 = 3
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r9 = 0
            r7[r9] = r13
            java.lang.String r9 = r8.title
            r12 = 1
            r7[r12] = r9
            r14 = 2
            r7[r14] = r5
            java.lang.String r9 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            goto L_0x11b2
        L_0x119e:
            r9 = 0
            r12 = 1
            r14 = 2
            r6 = 2131626611(0x7f0e0a73, float:1.8880463E38)
            java.lang.Object[] r7 = new java.lang.Object[r14]
            r7[r9] = r13
            java.lang.String r9 = r8.title
            r7[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedSticker"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
        L_0x11b2:
            r5 = r6
        L_0x11b3:
            goto L_0x1923
        L_0x11b5:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore
            if (r4 == 0) goto L_0x11c5
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x1923
        L_0x11c5:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme
            if (r4 == 0) goto L_0x1229
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r4 = (org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme) r4
            java.lang.String r4 = r4.emoticon
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 == 0) goto L_0x1200
            int r5 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r5 != 0) goto L_0x11ec
            r5 = 2131624895(0x7f0e03bf, float:1.8876983E38)
            r6 = 0
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r7 = "ChatThemeDisabledYou"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11fe
        L_0x11ec:
            r6 = 0
            r5 = 2131624894(0x7f0e03be, float:1.887698E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r7[r6] = r13
            r6 = 1
            r7[r6] = r4
            java.lang.String r6 = "ChatThemeDisabled"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r5, r7)
        L_0x11fe:
            goto L_0x1227
        L_0x1200:
            int r5 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r5 != 0) goto L_0x1214
            r5 = 2131624892(0x7f0e03bc, float:1.8876977E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r4
            java.lang.String r7 = "ChangedChatThemeYou"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x1226
        L_0x1214:
            r6 = 1
            r7 = 0
            r5 = 2131624891(0x7f0e03bb, float:1.8876975E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r7] = r13
            r9[r6] = r4
            java.lang.String r6 = "ChangedChatThemeTo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r5, r9)
        L_0x1226:
        L_0x1227:
            goto L_0x1923
        L_0x1229:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByRequest
            if (r4 == 0) goto L_0x1921
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x1923
        L_0x1239:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            long r4 = r4.channel_id
            r6 = 0
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 == 0) goto L_0x1275
            boolean r4 = r8.megagroup
            if (r4 != 0) goto L_0x1275
            boolean r4 = r31.isVideoAvatar()
            if (r4 == 0) goto L_0x1262
            r4 = 2131624844(0x7f0e038c, float:1.887688E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = r8.title
            r7 = 0
            r5[r7] = r6
            java.lang.String r6 = "ChannelVideoEditNotification"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1262:
            r5 = 1
            r7 = 0
            r4 = 2131624809(0x7f0e0369, float:1.8876808E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = r8.title
            r5[r7] = r6
            java.lang.String r6 = "ChannelPhotoEditNotification"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1275:
            boolean r4 = r31.isVideoAvatar()
            if (r4 == 0) goto L_0x1291
            r4 = 2131626630(0x7f0e0a86, float:1.8880502E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationEditedGroupVideo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1291:
            r5 = 2
            r6 = 0
            r7 = 1
            r4 = 2131626629(0x7f0e0a85, float:1.88805E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r5[r7] = r6
            java.lang.String r6 = "NotificationEditedGroupPhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x12a7:
            r18 = r7
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r4 == 0) goto L_0x1587
            boolean r4 = r8.megagroup
            if (r4 != 0) goto L_0x1587
            boolean r4 = r31.isMediaEmpty()
            if (r4 == 0) goto L_0x12ee
            if (r32 != 0) goto L_0x12dd
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x12dd
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            java.lang.String r7 = r7.message
            r9 = 1
            r4[r9] = r7
            r7 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x1923
        L_0x12dd:
            r6 = 0
            r9 = 1
            r4 = 2131624793(0x7f0e0359, float:1.8876776E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x12ee:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r4 == 0) goto L_0x1340
            if (r32 != 0) goto L_0x132f
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x132f
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x132f
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r9)
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            java.lang.String r9 = r9.message
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r9 = 1
            r4[r9] = r7
            r7 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x1923
        L_0x132f:
            r6 = 0
            r9 = 1
            r4 = 2131624794(0x7f0e035a, float:1.8876778E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessagePhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1340:
            boolean r4 = r31.isVideo()
            if (r4 == 0) goto L_0x1390
            if (r32 != 0) goto L_0x137f
            int r4 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r4 < r7) goto L_0x137f
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x137f
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r7 = 0
            r4[r7] = r13
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r6)
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            r9.append(r6)
            java.lang.String r6 = r9.toString()
            r9 = 1
            r4[r9] = r6
            r6 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r4)
            r33[r7] = r9
            goto L_0x1923
        L_0x137f:
            r7 = 0
            r9 = 1
            r4 = 2131624800(0x7f0e0360, float:1.887679E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r7] = r13
            java.lang.String r6 = "ChannelMessageVideo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1390:
            r7 = 0
            r9 = 1
            boolean r4 = r31.isVoice()
            if (r4 == 0) goto L_0x13a7
            r4 = 2131624785(0x7f0e0351, float:1.887676E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r7] = r13
            java.lang.String r6 = "ChannelMessageAudio"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x13a7:
            boolean r4 = r31.isRoundVideo()
            if (r4 == 0) goto L_0x13bc
            r4 = 2131624797(0x7f0e035d, float:1.8876784E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r7] = r13
            java.lang.String r6 = "ChannelMessageRound"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x13bc:
            boolean r4 = r31.isMusic()
            if (r4 == 0) goto L_0x13d1
            r4 = 2131624792(0x7f0e0358, float:1.8876774E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r7] = r13
            java.lang.String r6 = "ChannelMessageMusic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x13d1:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r4 == 0) goto L_0x13fb
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r4
            r5 = 2131624786(0x7f0e0352, float:1.8876762E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r4.first_name
            java.lang.String r9 = r4.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r9)
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "ChannelMessageContact2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x1923
        L_0x13fb:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r4 == 0) goto L_0x143e
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r5 = r4.poll
            boolean r5 = r5.quiz
            if (r5 == 0) goto L_0x1426
            r5 = 2131624796(0x7f0e035c, float:1.8876782E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            org.telegram.tgnet.TLRPC$Poll r7 = r4.poll
            java.lang.String r7 = r7.question
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "ChannelMessageQuiz2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x143c
        L_0x1426:
            r6 = 2
            r7 = 0
            r9 = 1
            r5 = 2131624795(0x7f0e035b, float:1.887678E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r7] = r13
            org.telegram.tgnet.TLRPC$Poll r7 = r4.poll
            java.lang.String r7 = r7.question
            r6[r9] = r7
            java.lang.String r7 = "ChannelMessagePoll2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
        L_0x143c:
            goto L_0x1923
        L_0x143e:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r4 != 0) goto L_0x1576
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r4 == 0) goto L_0x1452
            r6 = 0
            r9 = 1
            goto L_0x1578
        L_0x1452:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x146b
            r4 = 2131624790(0x7f0e0356, float:1.887677E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageLiveLocation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x146b:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x1545
            boolean r4 = r31.isSticker()
            if (r4 != 0) goto L_0x151b
            boolean r4 = r31.isAnimatedSticker()
            if (r4 == 0) goto L_0x1481
            goto L_0x151b
        L_0x1481:
            boolean r4 = r31.isGif()
            if (r4 == 0) goto L_0x14d1
            if (r32 != 0) goto L_0x14c0
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x14c0
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x14c0
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r14)
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            java.lang.String r9 = r9.message
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r9 = 1
            r4[r9] = r7
            r7 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x1923
        L_0x14c0:
            r6 = 0
            r9 = 1
            r4 = 2131624789(0x7f0e0355, float:1.8876768E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageGIF"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x14d1:
            if (r32 != 0) goto L_0x150a
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x150a
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x150a
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r15)
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            java.lang.String r9 = r9.message
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            r9 = 1
            r4[r9] = r7
            r7 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x1923
        L_0x150a:
            r6 = 0
            r9 = 1
            r4 = 2131624787(0x7f0e0353, float:1.8876764E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageDocument"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x151b:
            java.lang.String r4 = r31.getStickerEmoji()
            if (r4 == 0) goto L_0x1534
            r5 = 2131624799(0x7f0e035f, float:1.8876788E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            r9 = 1
            r6[r9] = r4
            java.lang.String r7 = "ChannelMessageStickerEmoji"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x1543
        L_0x1534:
            r7 = 0
            r9 = 1
            r5 = 2131624798(0x7f0e035e, float:1.8876786E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            r6[r7] = r13
            java.lang.String r7 = "ChannelMessageSticker"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
        L_0x1543:
            goto L_0x1923
        L_0x1545:
            if (r32 != 0) goto L_0x1565
            java.lang.CharSequence r4 = r0.messageText
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x1565
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.CharSequence r7 = r0.messageText
            r9 = 1
            r4[r9] = r7
            r7 = 2131626693(0x7f0e0ac5, float:1.888063E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x1923
        L_0x1565:
            r6 = 0
            r9 = 1
            r4 = 2131624793(0x7f0e0359, float:1.8876776E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1576:
            r6 = 0
            r9 = 1
        L_0x1578:
            r4 = 2131624791(0x7f0e0357, float:1.8876772E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageMap"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1587:
            boolean r4 = r31.isMediaEmpty()
            r5 = 2131626676(0x7f0e0ab4, float:1.8880595E38)
            java.lang.String r7 = "NotificationMessageGroupText"
            if (r4 == 0) goto L_0x15cc
            if (r32 != 0) goto L_0x15b6
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x15b6
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r8.title
            r9 = 1
            r4[r9] = r6
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            r9 = 2
            r4[r9] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r4)
            goto L_0x1923
        L_0x15b6:
            r9 = 2
            r4 = 2131626669(0x7f0e0aad, float:1.888058E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            r12 = r23
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r4, r5)
            goto L_0x1923
        L_0x15cc:
            r12 = r23
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r4 == 0) goto L_0x1625
            if (r32 != 0) goto L_0x160f
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x160f
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x160f
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r8.title
            r12 = 1
            r4[r12] = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r9)
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            java.lang.String r9 = r9.message
            r6.append(r9)
            java.lang.String r6 = r6.toString()
            r9 = 2
            r4[r9] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r4)
            goto L_0x1923
        L_0x160f:
            r9 = 2
            r4 = 2131626670(0x7f0e0aae, float:1.8880583E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupPhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1625:
            boolean r4 = r31.isVideo()
            if (r4 == 0) goto L_0x167a
            if (r32 != 0) goto L_0x1664
            int r4 = android.os.Build.VERSION.SDK_INT
            r9 = 19
            if (r4 < r9) goto L_0x1664
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x1664
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r9 = 0
            r4[r9] = r13
            java.lang.String r9 = r8.title
            r12 = 1
            r4[r12] = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r6)
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            r9.append(r6)
            java.lang.String r6 = r9.toString()
            r9 = 2
            r4[r9] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r4)
            goto L_0x1923
        L_0x1664:
            r9 = 2
            r4 = 2131626677(0x7f0e0ab5, float:1.8880597E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = " "
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x167a:
            boolean r4 = r31.isVoice()
            if (r4 == 0) goto L_0x1696
            r4 = 2131626659(0x7f0e0aa3, float:1.888056E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupAudio"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1696:
            boolean r4 = r31.isRoundVideo()
            if (r4 == 0) goto L_0x16b2
            r4 = 2131626673(0x7f0e0ab1, float:1.8880589E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupRound"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x16b2:
            boolean r4 = r31.isMusic()
            if (r4 == 0) goto L_0x16ce
            r4 = 2131626668(0x7f0e0aac, float:1.8880579E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupMusic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x16ce:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r4 == 0) goto L_0x16fd
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r4
            r5 = 2131626660(0x7f0e0aa4, float:1.8880562E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = r4.first_name
            java.lang.String r9 = r4.last_name
            java.lang.String r7 = org.telegram.messenger.ContactsController.formatName(r7, r9)
            r9 = 2
            r6[r9] = r7
            java.lang.String r7 = "NotificationMessageGroupContact2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x1923
        L_0x16fd:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r4 == 0) goto L_0x174a
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r5 = r4.poll
            boolean r5 = r5.quiz
            if (r5 == 0) goto L_0x172d
            r5 = 2131626672(0x7f0e0ab0, float:1.8880587E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            org.telegram.tgnet.TLRPC$Poll r7 = r4.poll
            java.lang.String r7 = r7.question
            r9 = 2
            r6[r9] = r7
            java.lang.String r7 = "NotificationMessageGroupQuiz2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x1748
        L_0x172d:
            r5 = 2131626671(0x7f0e0aaf, float:1.8880585E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            org.telegram.tgnet.TLRPC$Poll r7 = r4.poll
            java.lang.String r7 = r7.question
            r9 = 2
            r6[r9] = r7
            java.lang.String r7 = "NotificationMessageGroupPoll2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
        L_0x1748:
            goto L_0x1923
        L_0x174a:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r4 == 0) goto L_0x1773
            r4 = 2131626662(0x7f0e0aa6, float:1.8880567E38)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            org.telegram.tgnet.TLRPC$TL_game r6 = r6.game
            java.lang.String r6 = r6.title
            r7 = 2
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupGame"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1773:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r4 != 0) goto L_0x18c7
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r4 == 0) goto L_0x1788
            r6 = 0
            r7 = 1
            r9 = 2
            goto L_0x18ca
        L_0x1788:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x17a6
            r4 = 2131626666(0x7f0e0aaa, float:1.8880575E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupLiveLocation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x17a6:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x1894
            boolean r4 = r31.isSticker()
            if (r4 != 0) goto L_0x1860
            boolean r4 = r31.isAnimatedSticker()
            if (r4 == 0) goto L_0x17bc
            goto L_0x1860
        L_0x17bc:
            boolean r4 = r31.isGif()
            if (r4 == 0) goto L_0x1811
            if (r32 != 0) goto L_0x17fb
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x17fb
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x17fb
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r8.title
            r9 = 1
            r4[r9] = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r14)
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            java.lang.String r9 = r9.message
            r6.append(r9)
            java.lang.String r6 = r6.toString()
            r9 = 2
            r4[r9] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r4)
            goto L_0x1923
        L_0x17fb:
            r9 = 2
            r4 = 2131626664(0x7f0e0aa8, float:1.888057E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupGif"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1811:
            if (r32 != 0) goto L_0x184a
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x184a
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x184a
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r8.title
            r9 = 1
            r4[r9] = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r15)
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            java.lang.String r9 = r9.message
            r6.append(r9)
            java.lang.String r6 = r6.toString()
            r9 = 2
            r4[r9] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r4)
            goto L_0x1923
        L_0x184a:
            r9 = 2
            r4 = 2131626661(0x7f0e0aa5, float:1.8880564E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupDocument"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1860:
            java.lang.String r4 = r31.getStickerEmoji()
            if (r4 == 0) goto L_0x187e
            r5 = 2131626675(0x7f0e0ab3, float:1.8880593E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            r12 = 2
            r6[r12] = r4
            java.lang.String r7 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x1892
        L_0x187e:
            r7 = 0
            r9 = 1
            r12 = 2
            r5 = 2131626674(0x7f0e0ab2, float:1.888059E38)
            java.lang.Object[] r6 = new java.lang.Object[r12]
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r6[r9] = r7
            java.lang.String r7 = "NotificationMessageGroupSticker"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
        L_0x1892:
            goto L_0x1923
        L_0x1894:
            if (r32 != 0) goto L_0x18b4
            java.lang.CharSequence r4 = r0.messageText
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x18b4
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r8.title
            r9 = 1
            r4[r9] = r6
            java.lang.CharSequence r6 = r0.messageText
            r9 = 2
            r4[r9] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r4)
            goto L_0x1923
        L_0x18b4:
            r9 = 2
            r4 = 2131626669(0x7f0e0aad, float:1.888058E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r4, r5)
            goto L_0x1923
        L_0x18c7:
            r6 = 0
            r7 = 1
            r9 = 2
        L_0x18ca:
            r4 = 2131626667(0x7f0e0aab, float:1.8880577E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupMap"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x18dc:
            r18 = r7
            r12 = r23
            goto L_0x18e7
        L_0x18e1:
            r18 = r7
            r16 = r12
            r12 = r23
        L_0x18e7:
            if (r34 == 0) goto L_0x18ec
            r4 = 0
            r34[r4] = r4
        L_0x18ec:
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r4 == 0) goto L_0x1906
            boolean r4 = r8.megagroup
            if (r4 != 0) goto L_0x1906
            r4 = 2131624793(0x7f0e0359, float:1.8876776E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x1923
        L_0x1906:
            r6 = 0
            r4 = 2131626669(0x7f0e0aad, float:1.888058E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r4, r5)
            goto L_0x1923
        L_0x1919:
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
        L_0x1921:
            r5 = r22
        L_0x1923:
            return r5
        L_0x1924:
            r1 = 2131628719(0x7f0e12af, float:1.8884739E38)
            java.lang.String r2 = "YouHaveNewMessage"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getStringForMessage(org.telegram.messenger.MessageObject, boolean, boolean[], boolean[]):java.lang.String");
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent pintent = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 0);
            int minutes = getAccountInstance().getNotificationsSettings().getInt("repeat_messages", 60);
            if (minutes <= 0 || this.personalCount <= 0) {
                this.alarmManager.cancel(pintent);
            } else {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) (minutes * 60 * 1000)), pintent);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        return messageObject.messageOwner.peer_id != null && messageObject.messageOwner.peer_id.chat_id == 0 && messageObject.messageOwner.peer_id.channel_id == 0 && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty));
    }

    private int getNotifyOverride(SharedPreferences preferences, long dialog_id) {
        int notifyOverride = preferences.getInt("notify2_" + dialog_id, -1);
        if (notifyOverride != 3) {
            return notifyOverride;
        }
        if (preferences.getInt("notifyuntil_" + dialog_id, 0) >= getConnectionsManager().getCurrentTime()) {
            return 2;
        }
        return notifyOverride;
    }

    /* renamed from: lambda$showNotifications$25$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1126x8a90ed32() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda4(this));
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda39(this));
    }

    /* renamed from: lambda$hideNotifications$26$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1101x832e582c() {
        notificationManager.cancel(this.notificationId);
        this.lastWearNotifiedMessageId.clear();
        for (int a = 0; a < this.wearNotificationsIds.size(); a++) {
            notificationManager.cancel(this.wearNotificationsIds.valueAt(a).intValue());
        }
        this.wearNotificationsIds.clear();
    }

    private void dismissNotification() {
        try {
            notificationManager.cancel(this.notificationId);
            this.pushMessages.clear();
            this.pushMessagesDict.clear();
            this.lastWearNotifiedMessageId.clear();
            for (int a = 0; a < this.wearNotificationsIds.size(); a++) {
                if (!this.openedInBubbleDialogs.contains(Long.valueOf(this.wearNotificationsIds.keyAt(a)))) {
                    notificationManager.cancel(this.wearNotificationsIds.valueAt(a).intValue());
                }
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread(NotificationsController$$ExternalSyntheticLambda31.INSTANCE);
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
                if (getNotifyOverride(getAccountInstance().getNotificationsSettings(), this.openedDialogId) != 2) {
                    notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda1(this));
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    /* renamed from: lambda$playInChatSound$29$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1103xa67ee1() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundPlay) > 500) {
            try {
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda22.INSTANCE);
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

    static /* synthetic */ void lambda$playInChatSound$28(SoundPool soundPool2, int sampleId, int status) {
        if (status == 0) {
            try {
                soundPool2.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private void scheduleNotificationDelay(boolean onlineReason) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay notification start, onlineReason = " + onlineReason);
            }
            this.notificationDelayWakelock.acquire(10000);
            notificationsQueue.cancelRunnable(this.notificationDelayRunnable);
            notificationsQueue.postRunnable(this.notificationDelayRunnable, (long) (onlineReason ? 3000 : 1000));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    /* access modifiers changed from: protected */
    public void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda3(this));
    }

    /* renamed from: lambda$repeatNotificationMaybe$30$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1122x309788cf() {
        int hour = Calendar.getInstance().get(11);
        if (hour < 11 || hour > 22) {
            scheduleNotificationRepeat();
            return;
        }
        notificationManager.cancel(this.notificationId);
        showOrUpdateNotification(true);
    }

    private boolean isEmptyVibration(long[] pattern) {
        if (pattern == null || pattern.length == 0) {
            return false;
        }
        for (long j : pattern) {
            if (j != 0) {
                return false;
            }
        }
        return true;
    }

    public void deleteNotificationChannel(long dialogId) {
        deleteNotificationChannel(dialogId, -1);
    }

    /* access modifiers changed from: private */
    /* renamed from: deleteNotificationChannelInternal */
    public void m1097xab324d39(long dialogId, int what) {
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
                SharedPreferences.Editor editor = preferences.edit();
                if (what == 0 || what == -1) {
                    String key = "org.telegram.key" + dialogId;
                    String channelId = preferences.getString(key, (String) null);
                    if (channelId != null) {
                        editor.remove(key).remove(key + "_s");
                        try {
                            systemNotificationManager.deleteNotificationChannel(channelId);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel internal " + channelId);
                        }
                    }
                }
                if (what == 1 || what == -1) {
                    String key2 = "org.telegram.keyia" + dialogId;
                    String channelId2 = preferences.getString(key2, (String) null);
                    if (channelId2 != null) {
                        editor.remove(key2).remove(key2 + "_s");
                        try {
                            systemNotificationManager.deleteNotificationChannel(channelId2);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel internal " + channelId2);
                        }
                    }
                }
                editor.commit();
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
            }
        }
    }

    public void deleteNotificationChannel(long dialogId, int what) {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda15(this, dialogId, what));
        }
    }

    public void deleteNotificationChannelGlobal(int type) {
        deleteNotificationChannelGlobal(type, -1);
    }

    /* renamed from: deleteNotificationChannelGlobalInternal */
    public void m1098xb6var_c1b(int type, int what) {
        String overwriteKey;
        String key;
        String key2;
        if (Build.VERSION.SDK_INT >= 26) {
            try {
                SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
                SharedPreferences.Editor editor = preferences.edit();
                if (what == 0 || what == -1) {
                    if (type == 2) {
                        key2 = "channels";
                    } else if (type == 0) {
                        key2 = "groups";
                    } else {
                        key2 = "private";
                    }
                    String channelId = preferences.getString(key2, (String) null);
                    if (channelId != null) {
                        SharedPreferences.Editor remove = editor.remove(key2);
                        remove.remove(key2 + "_s");
                        try {
                            systemNotificationManager.deleteNotificationChannel(channelId);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel global internal " + channelId);
                        }
                    }
                }
                if (what == 1 || what == -1) {
                    if (type == 2) {
                        key = "channels_ia";
                    } else if (type == 0) {
                        key = "groups_ia";
                    } else {
                        key = "private_ia";
                    }
                    String channelId2 = preferences.getString(key, (String) null);
                    if (channelId2 != null) {
                        SharedPreferences.Editor remove2 = editor.remove(key);
                        remove2.remove(key + "_s");
                        try {
                            systemNotificationManager.deleteNotificationChannel(channelId2);
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel global internal " + channelId2);
                        }
                    }
                }
                if (type == 2) {
                    overwriteKey = "overwrite_channel";
                } else if (type == 0) {
                    overwriteKey = "overwrite_group";
                } else {
                    overwriteKey = "overwrite_private";
                }
                editor.remove(overwriteKey);
                editor.commit();
            } catch (Exception e3) {
                FileLog.e((Throwable) e3);
            }
        }
    }

    public void deleteNotificationChannelGlobal(int type, int what) {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda13(this, type, what));
        }
    }

    public void deleteAllNotificationChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda37(this));
        }
    }

    /* renamed from: lambda$deleteAllNotificationChannels$33$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1096xdfb4577b() {
        try {
            SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
            Map<String, ?> values = preferences.getAll();
            SharedPreferences.Editor editor = preferences.edit();
            for (Map.Entry<String, ?> entry : values.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("org.telegram.key")) {
                    if (!key.endsWith("_s")) {
                        String id = (String) entry.getValue();
                        systemNotificationManager.deleteNotificationChannel(id);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete all channel " + id);
                        }
                    }
                    editor.remove(key);
                }
            }
            editor.commit();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private boolean unsupportedNotificationShortcut() {
        return Build.VERSION.SDK_INT < 29 || !SharedConfig.chatBubbles;
    }

    private String createNotificationShortcut(NotificationCompat.Builder builder, long did, String name, TLRPC.User user, TLRPC.Chat chat, Person person) {
        IconCompat icon;
        NotificationCompat.Builder builder2 = builder;
        long j = did;
        TLRPC.User user2 = user;
        TLRPC.Chat chat2 = chat;
        Person person2 = person;
        if (unsupportedNotificationShortcut()) {
            String str = name;
            return null;
        } else if (!ChatObject.isChannel(chat) || chat2.megagroup) {
            try {
                String id = "ndid_" + j;
                Intent shortcutIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
                shortcutIntent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
                if (j > 0) {
                    shortcutIntent.putExtra("userId", j);
                } else {
                    shortcutIntent.putExtra("chatId", -j);
                }
                try {
                    ShortcutInfoCompat.Builder shortcutBuilder = new ShortcutInfoCompat.Builder(ApplicationLoader.applicationContext, id).setShortLabel(chat2 != null ? name : UserObject.getFirstName(user)).setLongLabel(name).setIntent(new Intent("android.intent.action.VIEW")).setIntent(shortcutIntent).setLongLived(true).setLocusId(new LocusIdCompat(id));
                    Bitmap avatar = null;
                    if (person2 != null) {
                        shortcutBuilder.setPerson(person2);
                        shortcutBuilder.setIcon(person.getIcon());
                        if (person.getIcon() != null) {
                            avatar = person.getIcon().getBitmap();
                        }
                    }
                    ShortcutInfoCompat shortcut = shortcutBuilder.build();
                    ShortcutManagerCompat.pushDynamicShortcut(ApplicationLoader.applicationContext, shortcut);
                    builder2.setShortcutInfo(shortcut);
                    ShortcutInfoCompat.Builder builder3 = shortcutBuilder;
                    Intent intent = new Intent(ApplicationLoader.applicationContext, BubbleActivity.class);
                    StringBuilder sb = new StringBuilder();
                    sb.append("com.tmessages.openchat");
                    Intent intent2 = shortcutIntent;
                    sb.append(Math.random());
                    sb.append(Integer.MAX_VALUE);
                    intent.setAction(sb.toString());
                    if (DialogObject.isUserDialog(did)) {
                        intent.putExtra("userId", j);
                    } else {
                        intent.putExtra("chatId", -j);
                    }
                    intent.putExtra("currentAccount", this.currentAccount);
                    if (avatar != null) {
                        icon = IconCompat.createWithAdaptiveBitmap(avatar);
                    } else if (user2 != null) {
                        icon = IconCompat.createWithResource(ApplicationLoader.applicationContext, user2.bot ? NUM : NUM);
                    } else {
                        icon = IconCompat.createWithResource(ApplicationLoader.applicationContext, NUM);
                    }
                    NotificationCompat.BubbleMetadata.Builder bubbleBuilder = new NotificationCompat.BubbleMetadata.Builder(PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM), icon);
                    bubbleBuilder.setSuppressNotification(this.openedDialogId == j);
                    bubbleBuilder.setAutoExpandBubble(false);
                    bubbleBuilder.setDesiredHeight(AndroidUtilities.dp(640.0f));
                    builder2.setBubbleMetadata(bubbleBuilder.build());
                    return id;
                } catch (Exception e) {
                    e = e;
                    FileLog.e((Throwable) e);
                    return null;
                }
            } catch (Exception e2) {
                e = e2;
                String str2 = name;
                FileLog.e((Throwable) e);
                return null;
            }
        } else {
            String str3 = name;
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void ensureGroupsCreated() {
        String userName;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        if (this.groupsCreated == null) {
            this.groupsCreated = Boolean.valueOf(preferences.getBoolean("groupsCreated4", false));
        }
        if (!this.groupsCreated.booleanValue()) {
            try {
                String keyStart = this.currentAccount + "channel";
                List<NotificationChannel> list = systemNotificationManager.getNotificationChannels();
                int count = list.size();
                SharedPreferences.Editor editor = null;
                for (int a = 0; a < count; a++) {
                    NotificationChannel channel = list.get(a);
                    String id = channel.getId();
                    if (id.startsWith(keyStart)) {
                        int importance = channel.getImportance();
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
                                    long dialogId = Utilities.parseLong(id.substring(9, id.indexOf(95, 9))).longValue();
                                    if (dialogId != 0) {
                                        if (editor == null) {
                                            editor = getAccountInstance().getNotificationsSettings().edit();
                                        }
                                        editor.remove("priority_" + dialogId).remove("vibrate_" + dialogId).remove("sound_path_" + dialogId).remove("sound_" + dialogId);
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
            preferences.edit().putBoolean("groupsCreated4", true).commit();
            this.groupsCreated = true;
        }
        if (!this.channelGroupsCreated) {
            List<NotificationChannelGroup> list2 = systemNotificationManager.getNotificationChannelGroups();
            String channelsId = "channels" + this.currentAccount;
            String groupsId = "groups" + this.currentAccount;
            String privateId = "private" + this.currentAccount;
            String otherId = "other" + this.currentAccount;
            int N = list2.size();
            for (int a2 = 0; a2 < N; a2++) {
                String id2 = list2.get(a2).getId();
                if (channelsId != null && channelsId.equals(id2)) {
                    channelsId = null;
                } else if (groupsId != null && groupsId.equals(id2)) {
                    groupsId = null;
                } else if (privateId != null && privateId.equals(id2)) {
                    privateId = null;
                } else if (otherId != null && otherId.equals(id2)) {
                    otherId = null;
                }
                if (channelsId == null && groupsId == null && privateId == null && otherId == null) {
                    break;
                }
            }
            if (!(channelsId == null && groupsId == null && privateId == null && otherId == null)) {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
                if (user == null) {
                    getUserConfig().getCurrentUser();
                }
                if (user != null) {
                    userName = " (" + ContactsController.formatName(user.first_name, user.last_name) + ")";
                } else {
                    userName = "";
                }
                ArrayList<NotificationChannelGroup> channelGroups = new ArrayList<>();
                if (channelsId != null) {
                    channelGroups.add(new NotificationChannelGroup(channelsId, LocaleController.getString("NotificationsChannels", NUM) + userName));
                }
                if (groupsId != null) {
                    channelGroups.add(new NotificationChannelGroup(groupsId, LocaleController.getString("NotificationsGroups", NUM) + userName));
                }
                if (privateId != null) {
                    channelGroups.add(new NotificationChannelGroup(privateId, LocaleController.getString("NotificationsPrivateChats", NUM) + userName));
                }
                if (otherId != null) {
                    channelGroups.add(new NotificationChannelGroup(otherId, LocaleController.getString("NotificationsOther", NUM) + userName));
                }
                systemNotificationManager.createNotificationChannelGroups(channelGroups);
            }
            this.channelGroupsCreated = true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:167:0x03f7  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x044b  */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x0485  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0559 A[LOOP:1: B:226:0x0556->B:228:0x0559, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0570  */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x05c1  */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x06b1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String validateChannelId(long r35, java.lang.String r37, long[] r38, int r39, android.net.Uri r40, int r41, boolean r42, boolean r43, boolean r44, int r45) {
        /*
            r34 = this;
            r1 = r34
            r2 = r35
            r4 = r41
            r5 = r45
            r34.ensureGroupsCreated()
            org.telegram.messenger.AccountInstance r0 = r34.getAccountInstance()
            android.content.SharedPreferences r6 = r0.getNotificationsSettings()
            java.lang.String r0 = "groups"
            java.lang.String r7 = "private"
            java.lang.String r8 = "channels"
            r9 = 2
            if (r44 == 0) goto L_0x0033
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "other"
            r10.append(r11)
            int r11 = r1.currentAccount
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r11 = 0
            goto L_0x0075
        L_0x0033:
            if (r5 != r9) goto L_0x004a
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r8)
            int r11 = r1.currentAccount
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            java.lang.String r11 = "overwrite_channel"
            goto L_0x0075
        L_0x004a:
            if (r5 != 0) goto L_0x0061
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r0)
            int r11 = r1.currentAccount
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            java.lang.String r11 = "overwrite_group"
            goto L_0x0075
        L_0x0061:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r7)
            int r11 = r1.currentAccount
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            java.lang.String r11 = "overwrite_private"
        L_0x0075:
            r13 = 0
            if (r42 != 0) goto L_0x0080
            boolean r14 = org.telegram.messenger.DialogObject.isEncryptedDialog(r35)
            if (r14 == 0) goto L_0x0080
            r14 = 1
            goto L_0x0081
        L_0x0080:
            r14 = 0
        L_0x0081:
            if (r43 != 0) goto L_0x008d
            if (r11 == 0) goto L_0x008d
            boolean r15 = r6.getBoolean(r11, r13)
            if (r15 == 0) goto L_0x008d
            r15 = 1
            goto L_0x008e
        L_0x008d:
            r15 = 0
        L_0x008e:
            if (r44 == 0) goto L_0x009e
            r0 = 2131626743(0x7f0e0af7, float:1.888073E38)
            java.lang.String r7 = "NotificationsSilent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            java.lang.String r7 = "silent"
            r12 = r0
            goto L_0x00f8
        L_0x009e:
            if (r42 == 0) goto L_0x00cb
            if (r43 == 0) goto L_0x00ac
            r13 = 2131626721(0x7f0e0ae1, float:1.8880686E38)
            java.lang.String r12 = "NotificationsInAppDefault"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r12, r13)
            goto L_0x00b5
        L_0x00ac:
            r12 = 2131626705(0x7f0e0ad1, float:1.8880654E38)
            java.lang.String r13 = "NotificationsDefault"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
        L_0x00b5:
            if (r5 != r9) goto L_0x00bd
            if (r43 == 0) goto L_0x00bb
            java.lang.String r8 = "channels_ia"
        L_0x00bb:
            r7 = r8
            goto L_0x00f8
        L_0x00bd:
            if (r5 != 0) goto L_0x00c5
            if (r43 == 0) goto L_0x00c3
            java.lang.String r0 = "groups_ia"
        L_0x00c3:
            r7 = r0
            goto L_0x00f8
        L_0x00c5:
            if (r43 == 0) goto L_0x00ca
            java.lang.String r7 = "private_ia"
        L_0x00ca:
            goto L_0x00f8
        L_0x00cb:
            if (r43 == 0) goto L_0x00dd
            r0 = 2131626702(0x7f0e0ace, float:1.8880648E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            r7 = 0
            r8[r7] = r37
            java.lang.String r7 = "NotificationsChatInApp"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r0, r8)
            goto L_0x00df
        L_0x00dd:
            r0 = r37
        L_0x00df:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            if (r43 == 0) goto L_0x00ea
            java.lang.String r8 = "org.telegram.keyia"
            goto L_0x00ed
        L_0x00ea:
            java.lang.String r8 = "org.telegram.key"
        L_0x00ed:
            r7.append(r8)
            r7.append(r2)
            java.lang.String r7 = r7.toString()
            r12 = r0
        L_0x00f8:
            r8 = 0
            java.lang.String r0 = r6.getString(r7, r8)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r7)
            java.lang.String r9 = "_s"
            r13.append(r9)
            java.lang.String r13 = r13.toString()
            java.lang.String r13 = r6.getString(r13, r8)
            r17 = 0
            java.lang.StringBuilder r18 = new java.lang.StringBuilder
            r18.<init>()
            r37 = r18
            r18 = 0
            java.lang.String r8 = "secret"
            if (r0 == 0) goto L_0x04de
            r19 = r11
            android.app.NotificationManager r11 = systemNotificationManager
            android.app.NotificationChannel r11 = r11.getNotificationChannel(r0)
            boolean r20 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r21 = r12
            java.lang.String r12 = " = "
            if (r20 == 0) goto L_0x0151
            r20 = r10
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r22 = r9
            java.lang.String r9 = "current channel for "
            r10.append(r9)
            r10.append(r0)
            r10.append(r12)
            r10.append(r11)
            java.lang.String r9 = r10.toString()
            org.telegram.messenger.FileLog.d(r9)
            goto L_0x0155
        L_0x0151:
            r22 = r9
            r20 = r10
        L_0x0155:
            if (r11 == 0) goto L_0x04c0
            if (r44 != 0) goto L_0x04aa
            if (r15 != 0) goto L_0x04aa
            int r9 = r11.getImportance()
            android.net.Uri r10 = r11.getSound()
            long[] r23 = r11.getVibrationPattern()
            r24 = r15
            boolean r15 = r11.shouldVibrate()
            if (r15 != 0) goto L_0x0180
            if (r23 != 0) goto L_0x0180
            r25 = r7
            r26 = r15
            r7 = 2
            long[] r15 = new long[r7]
            r15 = {0, 0} // fill-array
            r23 = r15
            r7 = r23
            goto L_0x0186
        L_0x0180:
            r25 = r7
            r26 = r15
            r7 = r23
        L_0x0186:
            int r15 = r11.getLightColor()
            if (r7 == 0) goto L_0x01a6
            r23 = 0
            r27 = r11
            r11 = r23
        L_0x0192:
            int r4 = r7.length
            if (r11 >= r4) goto L_0x01a3
            r2 = r7[r11]
            r4 = r37
            r4.append(r2)
            int r11 = r11 + 1
            r2 = r35
            r4 = r41
            goto L_0x0192
        L_0x01a3:
            r4 = r37
            goto L_0x01aa
        L_0x01a6:
            r4 = r37
            r27 = r11
        L_0x01aa:
            r4.append(r15)
            if (r10 == 0) goto L_0x01b6
            java.lang.String r2 = r10.toString()
            r4.append(r2)
        L_0x01b6:
            r4.append(r9)
            if (r42 != 0) goto L_0x01c0
            if (r14 == 0) goto L_0x01c0
            r4.append(r8)
        L_0x01c0:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x01e6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "current channel settings for "
            r2.append(r3)
            r2.append(r0)
            r2.append(r12)
            r2.append(r4)
            java.lang.String r3 = " old = "
            r2.append(r3)
            r2.append(r13)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x01e6:
            java.lang.String r2 = r4.toString()
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r3 = 0
            r4.setLength(r3)
            boolean r3 = r2.equals(r13)
            if (r3 != 0) goto L_0x0489
            r3 = 0
            java.lang.String r11 = "notify2_"
            if (r9 != 0) goto L_0x0248
            android.content.SharedPreferences$Editor r3 = r6.edit()
            if (r42 == 0) goto L_0x0221
            if (r43 != 0) goto L_0x021a
            java.lang.String r11 = getGlobalNotificationsKey(r45)
            r12 = 2147483647(0x7fffffff, float:NaN)
            r3.putInt(r11, r12)
            r1.updateServerNotificationsSettings((int) r5)
            r37 = r13
            r23 = r14
            r13 = r35
            goto L_0x023e
        L_0x021a:
            r37 = r13
            r23 = r14
            r13 = r35
            goto L_0x023e
        L_0x0221:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r11)
            r37 = r13
            r23 = r14
            r13 = r35
            r12.append(r13)
            java.lang.String r11 = r12.toString()
            r12 = 2
            r3.putInt(r11, r12)
            r11 = 1
            r1.updateServerNotificationsSettings(r13, r11)
        L_0x023e:
            r17 = 1
            r12 = r41
            r18 = r2
            r28 = r9
            goto L_0x02e3
        L_0x0248:
            r37 = r13
            r23 = r14
            r13 = r35
            r12 = r41
            if (r9 == r12) goto L_0x02df
            if (r43 != 0) goto L_0x02d8
            android.content.SharedPreferences$Editor r3 = r6.edit()
            r18 = r2
            r2 = 4
            if (r9 == r2) goto L_0x026d
            r2 = 5
            if (r9 != r2) goto L_0x0261
            goto L_0x026d
        L_0x0261:
            r2 = 1
            if (r9 != r2) goto L_0x0266
            r2 = 4
            goto L_0x026e
        L_0x0266:
            r2 = 2
            if (r9 != r2) goto L_0x026b
            r2 = 5
            goto L_0x026e
        L_0x026b:
            r2 = 0
            goto L_0x026e
        L_0x026d:
            r2 = 1
        L_0x026e:
            if (r42 == 0) goto L_0x0298
            java.lang.String r11 = getGlobalNotificationsKey(r45)
            r28 = r9
            r9 = 0
            android.content.SharedPreferences$Editor r11 = r3.putInt(r11, r9)
            r11.commit()
            r9 = 2
            if (r5 != r9) goto L_0x0288
            java.lang.String r9 = "priority_channel"
            r3.putInt(r9, r2)
            goto L_0x02dc
        L_0x0288:
            if (r5 != 0) goto L_0x0291
            java.lang.String r9 = "priority_group"
            r3.putInt(r9, r2)
            goto L_0x02dc
        L_0x0291:
            java.lang.String r9 = "priority_messages"
            r3.putInt(r9, r2)
            goto L_0x02dc
        L_0x0298:
            r28 = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r11)
            r9.append(r13)
            java.lang.String r9 = r9.toString()
            r11 = 0
            r3.putInt(r9, r11)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "notifyuntil_"
            r9.append(r11)
            r9.append(r13)
            java.lang.String r9 = r9.toString()
            r3.remove(r9)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "priority_"
            r9.append(r11)
            r9.append(r13)
            java.lang.String r9 = r9.toString()
            r3.putInt(r9, r2)
            goto L_0x02dc
        L_0x02d8:
            r18 = r2
            r28 = r9
        L_0x02dc:
            r17 = 1
            goto L_0x02e3
        L_0x02df:
            r18 = r2
            r28 = r9
        L_0x02e3:
            if (r10 != 0) goto L_0x02e7
            if (r40 != 0) goto L_0x0302
        L_0x02e7:
            if (r10 == 0) goto L_0x03e3
            if (r40 == 0) goto L_0x0302
            java.lang.String r2 = r10.toString()
            java.lang.String r9 = r40.toString()
            boolean r2 = android.text.TextUtils.equals(r2, r9)
            if (r2 != 0) goto L_0x02fa
            goto L_0x0302
        L_0x02fa:
            r29 = r0
            r33 = r4
            r31 = r8
            goto L_0x03e9
        L_0x0302:
            if (r43 != 0) goto L_0x03d9
            if (r3 != 0) goto L_0x030a
            android.content.SharedPreferences$Editor r3 = r6.edit()
        L_0x030a:
            java.lang.String r2 = "GroupSound"
            java.lang.String r9 = "GlobalSound"
            java.lang.String r11 = "ChannelSound"
            r29 = r0
            java.lang.String r0 = "sound_"
            if (r10 != 0) goto L_0x0347
            java.lang.String r30 = "NoSound"
            r31 = r8
            java.lang.String r8 = "NoSound"
            if (r42 == 0) goto L_0x0330
            r0 = 2
            if (r5 != r0) goto L_0x0326
            r3.putString(r11, r8)
            goto L_0x0342
        L_0x0326:
            if (r5 != 0) goto L_0x032c
            r3.putString(r2, r8)
            goto L_0x0342
        L_0x032c:
            r3.putString(r9, r8)
            goto L_0x0342
        L_0x0330:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            r2.append(r13)
            java.lang.String r0 = r2.toString()
            r3.putString(r0, r8)
        L_0x0342:
            r33 = r4
            r0 = r30
            goto L_0x03aa
        L_0x0347:
            r31 = r8
            java.lang.String r30 = r10.toString()
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.media.Ringtone r8 = android.media.RingtoneManager.getRingtone(r8, r10)
            r32 = 0
            if (r8 == 0) goto L_0x037d
            android.net.Uri r12 = android.provider.Settings.System.DEFAULT_RINGTONE_URI
            boolean r12 = r10.equals(r12)
            if (r12 == 0) goto L_0x036d
            r12 = 2131625187(0x7f0e04e3, float:1.8877575E38)
            r33 = r4
            java.lang.String r4 = "DefaultRingtone"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r12)
            r32 = r4
            goto L_0x0377
        L_0x036d:
            r33 = r4
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r4 = r8.getTitle(r4)
            r32 = r4
        L_0x0377:
            r8.stop()
            r4 = r32
            goto L_0x0381
        L_0x037d:
            r33 = r4
            r4 = r32
        L_0x0381:
            if (r4 == 0) goto L_0x03a8
            if (r42 == 0) goto L_0x0396
            r0 = 2
            if (r5 != r0) goto L_0x038c
            r3.putString(r11, r4)
            goto L_0x03a8
        L_0x038c:
            if (r5 != 0) goto L_0x0392
            r3.putString(r2, r4)
            goto L_0x03a8
        L_0x0392:
            r3.putString(r9, r4)
            goto L_0x03a8
        L_0x0396:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            r2.append(r13)
            java.lang.String r0 = r2.toString()
            r3.putString(r0, r4)
        L_0x03a8:
            r0 = r30
        L_0x03aa:
            if (r42 == 0) goto L_0x03c3
            r2 = 2
            if (r5 != r2) goto L_0x03b5
            java.lang.String r2 = "ChannelSoundPath"
            r3.putString(r2, r0)
            goto L_0x03df
        L_0x03b5:
            if (r5 != 0) goto L_0x03bd
            java.lang.String r2 = "GroupSoundPath"
            r3.putString(r2, r0)
            goto L_0x03df
        L_0x03bd:
            java.lang.String r2 = "GlobalSoundPath"
            r3.putString(r2, r0)
            goto L_0x03df
        L_0x03c3:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "sound_path_"
            r2.append(r4)
            r2.append(r13)
            java.lang.String r2 = r2.toString()
            r3.putString(r2, r0)
            goto L_0x03df
        L_0x03d9:
            r29 = r0
            r33 = r4
            r31 = r8
        L_0x03df:
            r0 = r10
            r17 = 1
            goto L_0x03eb
        L_0x03e3:
            r29 = r0
            r33 = r4
            r31 = r8
        L_0x03e9:
            r0 = r40
        L_0x03eb:
            r2 = r38
            boolean r4 = r1.isEmptyVibration(r2)
            r8 = 1
            r4 = r4 ^ r8
            r8 = r26
            if (r4 == r8) goto L_0x0447
            if (r43 != 0) goto L_0x0444
            if (r3 != 0) goto L_0x03ff
            android.content.SharedPreferences$Editor r3 = r6.edit()
        L_0x03ff:
            if (r42 == 0) goto L_0x042a
            r9 = 2
            if (r5 != r9) goto L_0x0410
            if (r8 == 0) goto L_0x0408
            r9 = 0
            goto L_0x0409
        L_0x0408:
            r9 = 2
        L_0x0409:
            java.lang.String r11 = "vibrate_channel"
            r3.putInt(r11, r9)
            goto L_0x0444
        L_0x0410:
            if (r5 != 0) goto L_0x041e
            if (r8 == 0) goto L_0x0416
            r9 = 0
            goto L_0x0417
        L_0x0416:
            r9 = 2
        L_0x0417:
            java.lang.String r11 = "vibrate_group"
            r3.putInt(r11, r9)
            goto L_0x0444
        L_0x041e:
            if (r8 == 0) goto L_0x0422
            r9 = 0
            goto L_0x0423
        L_0x0422:
            r9 = 2
        L_0x0423:
            java.lang.String r11 = "vibrate_messages"
            r3.putInt(r11, r9)
            goto L_0x0444
        L_0x042a:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "vibrate_"
            r9.append(r11)
            r9.append(r13)
            java.lang.String r9 = r9.toString()
            if (r8 == 0) goto L_0x0440
            r11 = 0
            goto L_0x0441
        L_0x0440:
            r11 = 2
        L_0x0441:
            r3.putInt(r9, r11)
        L_0x0444:
            r2 = r7
            r17 = 1
        L_0x0447:
            r9 = r39
            if (r15 == r9) goto L_0x0483
            if (r43 != 0) goto L_0x0480
            if (r3 != 0) goto L_0x0453
            android.content.SharedPreferences$Editor r3 = r6.edit()
        L_0x0453:
            if (r42 == 0) goto L_0x046c
            r11 = 2
            if (r5 != r11) goto L_0x045e
            java.lang.String r11 = "ChannelLed"
            r3.putInt(r11, r15)
            goto L_0x0480
        L_0x045e:
            if (r5 != 0) goto L_0x0466
            java.lang.String r11 = "GroupLed"
            r3.putInt(r11, r15)
            goto L_0x0480
        L_0x0466:
            java.lang.String r11 = "MessagesLed"
            r3.putInt(r11, r15)
            goto L_0x0480
        L_0x046c:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "color_"
            r11.append(r12)
            r11.append(r13)
            java.lang.String r11 = r11.toString()
            r3.putInt(r11, r15)
        L_0x0480:
            r9 = r15
            r17 = 1
        L_0x0483:
            if (r3 == 0) goto L_0x04a1
            r3.commit()
            goto L_0x04a1
        L_0x0489:
            r29 = r0
            r18 = r2
            r33 = r4
            r31 = r8
            r28 = r9
            r37 = r13
            r23 = r14
            r8 = r26
            r13 = r35
            r2 = r38
            r9 = r39
            r0 = r40
        L_0x04a1:
            r7 = r37
            r3 = r0
            r0 = r18
            r4 = r29
            goto L_0x0501
        L_0x04aa:
            r33 = r37
            r9 = r39
            r29 = r0
            r25 = r7
            r31 = r8
            r27 = r11
            r37 = r13
            r23 = r14
            r24 = r15
            r13 = r2
            r2 = r38
            goto L_0x04f9
        L_0x04c0:
            r33 = r37
            r9 = r39
            r29 = r0
            r25 = r7
            r31 = r8
            r27 = r11
            r37 = r13
            r23 = r14
            r24 = r15
            r13 = r2
            r2 = r38
            r0 = 0
            r3 = 0
            r4 = r0
            r7 = r3
            r0 = r18
            r3 = r40
            goto L_0x0501
        L_0x04de:
            r33 = r37
            r29 = r0
            r25 = r7
            r31 = r8
            r22 = r9
            r20 = r10
            r19 = r11
            r21 = r12
            r37 = r13
            r23 = r14
            r24 = r15
            r9 = r39
            r13 = r2
            r2 = r38
        L_0x04f9:
            r7 = r37
            r3 = r40
            r0 = r18
            r4 = r29
        L_0x0501:
            if (r17 == 0) goto L_0x0540
            if (r0 == 0) goto L_0x0540
            android.content.SharedPreferences$Editor r8 = r6.edit()
            r10 = r25
            android.content.SharedPreferences$Editor r8 = r8.putString(r10, r4)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            r12 = r22
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            android.content.SharedPreferences$Editor r8 = r8.putString(r11, r0)
            r8.commit()
            boolean r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r8 == 0) goto L_0x054d
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r11 = "change edited channel "
            r8.append(r11)
            r8.append(r4)
            java.lang.String r8 = r8.toString()
            org.telegram.messenger.FileLog.d(r8)
            goto L_0x054d
        L_0x0540:
            r12 = r22
            r10 = r25
            if (r24 != 0) goto L_0x0555
            if (r0 == 0) goto L_0x0555
            if (r43 == 0) goto L_0x0555
            if (r42 != 0) goto L_0x054d
            goto L_0x0555
        L_0x054d:
            r8 = r41
            r22 = r12
            r15 = r33
            goto L_0x05bf
        L_0x0555:
            r8 = 0
        L_0x0556:
            int r11 = r2.length
            if (r8 >= r11) goto L_0x0567
            r22 = r12
            r11 = r2[r8]
            r15 = r33
            r15.append(r11)
            int r8 = r8 + 1
            r12 = r22
            goto L_0x0556
        L_0x0567:
            r22 = r12
            r15 = r33
            r15.append(r9)
            if (r3 == 0) goto L_0x0577
            java.lang.String r8 = r3.toString()
            r15.append(r8)
        L_0x0577:
            r8 = r41
            r15.append(r8)
            if (r42 != 0) goto L_0x0585
            if (r23 == 0) goto L_0x0585
            r11 = r31
            r15.append(r11)
        L_0x0585:
            java.lang.String r11 = r15.toString()
            java.lang.String r11 = org.telegram.messenger.Utilities.MD5(r11)
            if (r44 != 0) goto L_0x05be
            if (r4 == 0) goto L_0x05be
            if (r24 != 0) goto L_0x0599
            boolean r0 = r7.equals(r11)
            if (r0 != 0) goto L_0x05be
        L_0x0599:
            android.app.NotificationManager r0 = systemNotificationManager     // Catch:{ Exception -> 0x059f }
            r0.deleteNotificationChannel(r4)     // Catch:{ Exception -> 0x059f }
            goto L_0x05a3
        L_0x059f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05a3:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05bb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r12 = "delete channel by settings change "
            r0.append(r12)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x05bb:
            r4 = 0
            r0 = r11
            goto L_0x05bf
        L_0x05be:
            r0 = r11
        L_0x05bf:
            if (r4 != 0) goto L_0x06b1
            java.lang.String r11 = "_"
            java.lang.String r12 = "channel_"
            if (r42 == 0) goto L_0x05ea
            r37 = r4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = r1.currentAccount
            r4.append(r5)
            r4.append(r12)
            r4.append(r10)
            r4.append(r11)
            java.security.SecureRandom r5 = org.telegram.messenger.Utilities.random
            long r11 = r5.nextLong()
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            goto L_0x060c
        L_0x05ea:
            r37 = r4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = r1.currentAccount
            r4.append(r5)
            r4.append(r12)
            r4.append(r13)
            r4.append(r11)
            java.security.SecureRandom r5 = org.telegram.messenger.Utilities.random
            long r11 = r5.nextLong()
            r4.append(r11)
            java.lang.String r4 = r4.toString()
        L_0x060c:
            android.app.NotificationChannel r5 = new android.app.NotificationChannel
            if (r23 == 0) goto L_0x061a
            r11 = 2131627669(0x7f0e0e95, float:1.8882609E38)
            java.lang.String r12 = "SecretChatName"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            goto L_0x061c
        L_0x061a:
            r11 = r21
        L_0x061c:
            r5.<init>(r4, r11, r8)
            r11 = r20
            r5.setGroup(r11)
            if (r9 == 0) goto L_0x062e
            r12 = 1
            r5.enableLights(r12)
            r5.setLightColor(r9)
            goto L_0x0633
        L_0x062e:
            r12 = 1
            r12 = 0
            r5.enableLights(r12)
        L_0x0633:
            boolean r12 = r1.isEmptyVibration(r2)
            if (r12 != 0) goto L_0x0644
            r12 = 1
            r5.enableVibration(r12)
            int r12 = r2.length
            if (r12 <= 0) goto L_0x0648
            r5.setVibrationPattern(r2)
            goto L_0x0648
        L_0x0644:
            r12 = 0
            r5.enableVibration(r12)
        L_0x0648:
            android.media.AudioAttributes$Builder r12 = new android.media.AudioAttributes$Builder
            r12.<init>()
            r16 = r2
            r2 = 4
            r12.setContentType(r2)
            r2 = 5
            r12.setUsage(r2)
            if (r3 == 0) goto L_0x0663
            android.media.AudioAttributes r2 = r12.build()
            r5.setSound(r3, r2)
            r38 = r3
            goto L_0x066d
        L_0x0663:
            android.media.AudioAttributes r2 = r12.build()
            r38 = r3
            r3 = 0
            r5.setSound(r3, r2)
        L_0x066d:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0685
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "create new channel "
            r2.append(r3)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0685:
            long r2 = android.os.SystemClock.elapsedRealtime()
            r1.lastNotificationChannelCreateTime = r2
            android.app.NotificationManager r2 = systemNotificationManager
            r2.createNotificationChannel(r5)
            android.content.SharedPreferences$Editor r2 = r6.edit()
            android.content.SharedPreferences$Editor r2 = r2.putString(r10, r4)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            r1 = r22
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            android.content.SharedPreferences$Editor r1 = r2.putString(r1, r0)
            r1.commit()
            goto L_0x06b9
        L_0x06b1:
            r16 = r2
            r38 = r3
            r37 = r4
            r11 = r20
        L_0x06b9:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:106:0x0205 A[SYNTHETIC, Splitter:B:106:0x0205] */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x021f A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x027b A[SYNTHETIC, Splitter:B:112:0x027b] */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02f2 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04af A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x04b1 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04cc A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x0556 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0569 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x05da A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0626 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0633 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x063d A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0666 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x06a3 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x06ae A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06ee A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x07a2 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x086f A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x08d2  */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x08d9  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00ce A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:363:0x091e A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0928 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x0931 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0989 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x0a36 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0a40 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0a4c A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0a5e A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0ada A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0bcf A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00fb A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x0CLASSNAME A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:471:0x0CLASSNAME A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0119 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0124 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0146 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x014b A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0157 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0167 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0179 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x017d A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0195 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01af A[SYNTHETIC, Splitter:B:91:0x01af] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01e3 A[Catch:{ Exception -> 0x0c8e }] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01ef A[Catch:{ Exception -> 0x0c8e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r69) {
        /*
            r68 = this;
            r15 = r68
            java.lang.String r1 = "color_"
            java.lang.String r2 = "currentAccount"
            org.telegram.messenger.UserConfig r3 = r68.getUserConfig()
            boolean r3 = r3.isClientActivated()
            if (r3 == 0) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r15.pushMessages
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0CLASSNAME
            boolean r3 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r3 != 0) goto L_0x0024
            int r3 = r15.currentAccount
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            if (r3 == r4) goto L_0x0024
            goto L_0x0CLASSNAME
        L_0x0024:
            org.telegram.tgnet.ConnectionsManager r3 = r68.getConnectionsManager()     // Catch:{ Exception -> 0x0c8e }
            r3.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0c8e }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r15.pushMessages     // Catch:{ Exception -> 0x0c8e }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0c8e }
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3     // Catch:{ Exception -> 0x0c8e }
            r14 = r3
            org.telegram.messenger.AccountInstance r3 = r68.getAccountInstance()     // Catch:{ Exception -> 0x0c8e }
            android.content.SharedPreferences r3 = r3.getNotificationsSettings()     // Catch:{ Exception -> 0x0c8e }
            r13 = r3
            java.lang.String r3 = "dismissDate"
            int r3 = r13.getInt(r3, r4)     // Catch:{ Exception -> 0x0c8e }
            r12 = r3
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x0c8e }
            int r3 = r3.date     // Catch:{ Exception -> 0x0c8e }
            if (r3 > r12) goto L_0x004f
            r68.dismissNotification()     // Catch:{ Exception -> 0x0c8e }
            return
        L_0x004f:
            long r5 = r14.getDialogId()     // Catch:{ Exception -> 0x0c8e }
            r10 = r5
            r3 = 0
            org.telegram.tgnet.TLRPC$Message r7 = r14.messageOwner     // Catch:{ Exception -> 0x0c8e }
            boolean r7 = r7.mentioned     // Catch:{ Exception -> 0x0c8e }
            if (r7 == 0) goto L_0x0063
            long r7 = r14.getFromChatId()     // Catch:{ Exception -> 0x0c8e }
            r5 = r7
            r8 = r5
            goto L_0x0064
        L_0x0063:
            r8 = r5
        L_0x0064:
            int r5 = r14.getId()     // Catch:{ Exception -> 0x0c8e }
            r16 = r5
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id     // Catch:{ Exception -> 0x0c8e }
            long r5 = r5.chat_id     // Catch:{ Exception -> 0x0c8e }
            r17 = 0
            int r7 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r7 == 0) goto L_0x007d
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id     // Catch:{ Exception -> 0x0c8e }
            long r5 = r5.chat_id     // Catch:{ Exception -> 0x0c8e }
            goto L_0x0083
        L_0x007d:
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id     // Catch:{ Exception -> 0x0c8e }
            long r5 = r5.channel_id     // Catch:{ Exception -> 0x0c8e }
        L_0x0083:
            r6 = r5
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id     // Catch:{ Exception -> 0x0c8e }
            long r4 = r5.user_id     // Catch:{ Exception -> 0x0c8e }
            boolean r20 = r14.isFromUser()     // Catch:{ Exception -> 0x0c8e }
            if (r20 == 0) goto L_0x00b2
            int r20 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r20 == 0) goto L_0x00a6
            org.telegram.messenger.UserConfig r20 = r68.getUserConfig()     // Catch:{ Exception -> 0x0c8e }
            long r20 = r20.getClientUserId()     // Catch:{ Exception -> 0x0c8e }
            int r22 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1))
            if (r22 != 0) goto L_0x00a1
            goto L_0x00a6
        L_0x00a1:
            r20 = r3
            r21 = r4
            goto L_0x00b6
        L_0x00a6:
            r20 = r3
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id     // Catch:{ Exception -> 0x0c8e }
            r21 = r4
            long r3 = r3.user_id     // Catch:{ Exception -> 0x0c8e }
            r4 = r3
            goto L_0x00b8
        L_0x00b2:
            r20 = r3
            r21 = r4
        L_0x00b6:
            r4 = r21
        L_0x00b8:
            org.telegram.messenger.MessagesController r3 = r68.getMessagesController()     // Catch:{ Exception -> 0x0c8e }
            r21 = r2
            java.lang.Long r2 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)     // Catch:{ Exception -> 0x0c8e }
            r3 = r2
            r2 = 0
            r22 = r2
            int r23 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r23 == 0) goto L_0x00fb
            org.telegram.messenger.MessagesController r2 = r68.getMessagesController()     // Catch:{ Exception -> 0x0c8e }
            r24 = r4
            java.lang.Long r4 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r4)     // Catch:{ Exception -> 0x0c8e }
            if (r2 != 0) goto L_0x00ea
            boolean r4 = r14.isFcmMessage()     // Catch:{ Exception -> 0x0c8e }
            if (r4 == 0) goto L_0x00ea
            boolean r4 = r14.localChannel     // Catch:{ Exception -> 0x0c8e }
            r20 = r4
            r4 = r2
            goto L_0x00ff
        L_0x00ea:
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r2)     // Catch:{ Exception -> 0x0c8e }
            if (r4 == 0) goto L_0x00f6
            boolean r4 = r2.megagroup     // Catch:{ Exception -> 0x0c8e }
            if (r4 != 0) goto L_0x00f6
            r4 = 1
            goto L_0x00f7
        L_0x00f6:
            r4 = 0
        L_0x00f7:
            r20 = r4
            r4 = r2
            goto L_0x00ff
        L_0x00fb:
            r24 = r4
            r4 = r22
        L_0x00ff:
            r2 = 0
            r5 = 0
            r22 = 0
            r26 = 0
            r27 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r28 = 0
            int r29 = r15.getNotifyOverride(r13, r8)     // Catch:{ Exception -> 0x0c8e }
            r30 = r29
            r29 = r2
            r2 = -1
            r31 = r5
            r5 = r30
            if (r5 != r2) goto L_0x0124
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r20)     // Catch:{ Exception -> 0x0c8e }
            boolean r2 = r15.isGlobalNotificationsEnabled(r10, r2)     // Catch:{ Exception -> 0x0c8e }
            r33 = r2
            goto L_0x012c
        L_0x0124:
            r2 = 2
            if (r5 == r2) goto L_0x0129
            r2 = 1
            goto L_0x012a
        L_0x0129:
            r2 = 0
        L_0x012a:
            r33 = r2
        L_0x012c:
            r2 = 1
            int r34 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r34 == 0) goto L_0x0133
            if (r4 == 0) goto L_0x0135
        L_0x0133:
            if (r3 != 0) goto L_0x0142
        L_0x0135:
            boolean r34 = r14.isFcmMessage()     // Catch:{ Exception -> 0x0c8e }
            if (r34 == 0) goto L_0x0142
            r34 = r2
            java.lang.String r2 = r14.localName     // Catch:{ Exception -> 0x0c8e }
            r35 = r2
            goto L_0x0151
        L_0x0142:
            r34 = r2
            if (r4 == 0) goto L_0x014b
            java.lang.String r2 = r4.title     // Catch:{ Exception -> 0x0c8e }
            r35 = r2
            goto L_0x0151
        L_0x014b:
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r3)     // Catch:{ Exception -> 0x0c8e }
            r35 = r2
        L_0x0151:
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0c8e }
            if (r2 != 0) goto L_0x015e
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0c8e }
            if (r2 == 0) goto L_0x015c
            goto L_0x015e
        L_0x015c:
            r2 = 0
            goto L_0x015f
        L_0x015e:
            r2 = 1
        L_0x015f:
            r36 = r2
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r10)     // Catch:{ Exception -> 0x0c8e }
            if (r2 != 0) goto L_0x0179
            androidx.collection.LongSparseArray<java.lang.Integer> r2 = r15.pushDialogs     // Catch:{ Exception -> 0x0c8e }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0c8e }
            r37 = r5
            r5 = 1
            if (r2 > r5) goto L_0x017b
            if (r36 == 0) goto L_0x0175
            goto L_0x017b
        L_0x0175:
            r2 = r35
            r5 = r2
            goto L_0x01a2
        L_0x0179:
            r37 = r5
        L_0x017b:
            if (r36 == 0) goto L_0x0195
            int r2 = (r6 > r17 ? 1 : (r6 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x018b
            java.lang.String r2 = "NotificationHiddenChatName"
            r5 = 2131626644(0x7f0e0a94, float:1.888053E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x019e
        L_0x018b:
            java.lang.String r2 = "NotificationHiddenName"
            r5 = 2131626647(0x7f0e0a97, float:1.8880536E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x019e
        L_0x0195:
            java.lang.String r2 = "AppName"
            r5 = 2131624300(0x7f0e016c, float:1.8875776E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r5)     // Catch:{ Exception -> 0x0c8e }
        L_0x019e:
            r5 = 0
            r34 = r5
            r5 = r2
        L_0x01a2:
            int r2 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0c8e }
            r38 = r14
            java.lang.String r14 = ""
            r39 = r3
            r3 = 1
            if (r2 <= r3) goto L_0x01e3
            androidx.collection.LongSparseArray<java.lang.Integer> r2 = r15.pushDialogs     // Catch:{ Exception -> 0x0c8e }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0c8e }
            if (r2 != r3) goto L_0x01c4
            org.telegram.messenger.UserConfig r2 = r68.getUserConfig()     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x01e4
        L_0x01c4:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r2.<init>()     // Catch:{ Exception -> 0x0c8e }
            org.telegram.messenger.UserConfig r3 = r68.getUserConfig()     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$User r3 = r3.getCurrentUser()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r3)     // Catch:{ Exception -> 0x0c8e }
            r2.append(r3)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r3 = "・"
            r2.append(r3)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0c8e }
            goto L_0x01e4
        L_0x01e3:
            r2 = r14
        L_0x01e4:
            androidx.collection.LongSparseArray<java.lang.Integer> r3 = r15.pushDialogs     // Catch:{ Exception -> 0x0c8e }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0c8e }
            r40 = r6
            r6 = 1
            if (r3 != r6) goto L_0x01fa
            int r3 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            r6 = 23
            if (r3 >= r6) goto L_0x01f6
            goto L_0x01fa
        L_0x01f6:
            r44 = r1
            r7 = r2
            goto L_0x0258
        L_0x01fa:
            androidx.collection.LongSparseArray<java.lang.Integer> r3 = r15.pushDialogs     // Catch:{ Exception -> 0x0c8e }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r6 = "NewMessages"
            r7 = 1
            if (r3 != r7) goto L_0x021f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r3.<init>()     // Catch:{ Exception -> 0x0c8e }
            r3.append(r2)     // Catch:{ Exception -> 0x0c8e }
            int r7 = r15.total_unread_count     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r7)     // Catch:{ Exception -> 0x0c8e }
            r3.append(r6)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0c8e }
            r2 = r3
            r44 = r1
            r7 = r2
            goto L_0x0258
        L_0x021f:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r3.<init>()     // Catch:{ Exception -> 0x0c8e }
            r3.append(r2)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r7 = "NotificationMessagesPeopleDisplayOrder"
            r42 = r2
            r44 = r1
            r2 = 2
            java.lang.Object[] r1 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x0c8e }
            int r2 = r15.total_unread_count     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)     // Catch:{ Exception -> 0x0c8e }
            r6 = 0
            r1[r6] = r2     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r2 = "FromChats"
            androidx.collection.LongSparseArray<java.lang.Integer> r6 = r15.pushDialogs     // Catch:{ Exception -> 0x0c8e }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r6)     // Catch:{ Exception -> 0x0c8e }
            r6 = 1
            r1[r6] = r2     // Catch:{ Exception -> 0x0c8e }
            r2 = 2131626695(0x7f0e0ac7, float:1.8880633E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ Exception -> 0x0c8e }
            r3.append(r1)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x0c8e }
            r2 = r1
            r7 = r2
        L_0x0258:
            androidx.core.app.NotificationCompat$Builder r1 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0c8e }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0c8e }
            r6 = r1
            r1 = 2
            r2 = 0
            r42 = 0
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r15.pushMessages     // Catch:{ Exception -> 0x0c8e }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0c8e }
            r43 = r1
            java.lang.String r1 = ": "
            r45 = r2
            java.lang.String r2 = " "
            r46 = r13
            java.lang.String r13 = " @ "
            r47 = r8
            r8 = 1
            if (r3 != r8) goto L_0x02f2
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r15.pushMessages     // Catch:{ Exception -> 0x0c8e }
            r8 = 0
            java.lang.Object r3 = r3.get(r8)     // Catch:{ Exception -> 0x0c8e }
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3     // Catch:{ Exception -> 0x0c8e }
            r8 = 1
            boolean[] r9 = new boolean[r8]     // Catch:{ Exception -> 0x0c8e }
            r8 = r9
            r51 = r10
            r9 = 0
            r10 = 0
            java.lang.String r11 = r15.getStringForMessage(r3, r10, r8, r9)     // Catch:{ Exception -> 0x0c8e }
            r9 = r11
            r10 = r11
            org.telegram.tgnet.TLRPC$Message r11 = r3.messageOwner     // Catch:{ Exception -> 0x0c8e }
            boolean r11 = r11.silent     // Catch:{ Exception -> 0x0c8e }
            if (r10 != 0) goto L_0x0299
            return
        L_0x0299:
            if (r34 == 0) goto L_0x02e0
            if (r4 == 0) goto L_0x02b2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r1.<init>()     // Catch:{ Exception -> 0x0c8e }
            r1.append(r13)     // Catch:{ Exception -> 0x0c8e }
            r1.append(r5)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r10.replace(r1, r14)     // Catch:{ Exception -> 0x0c8e }
            r10 = r1
            goto L_0x02e0
        L_0x02b2:
            r13 = 0
            boolean r43 = r8[r13]     // Catch:{ Exception -> 0x0c8e }
            if (r43 == 0) goto L_0x02cc
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r2.<init>()     // Catch:{ Exception -> 0x0c8e }
            r2.append(r5)     // Catch:{ Exception -> 0x0c8e }
            r2.append(r1)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r10.replace(r1, r14)     // Catch:{ Exception -> 0x0c8e }
            r10 = r1
            goto L_0x02e0
        L_0x02cc:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r1.<init>()     // Catch:{ Exception -> 0x0c8e }
            r1.append(r5)     // Catch:{ Exception -> 0x0c8e }
            r1.append(r2)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r10.replace(r1, r14)     // Catch:{ Exception -> 0x0c8e }
            r10 = r1
        L_0x02e0:
            r6.setContentText(r10)     // Catch:{ Exception -> 0x0c8e }
            androidx.core.app.NotificationCompat$BigTextStyle r1 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0c8e }
            r1.<init>()     // Catch:{ Exception -> 0x0c8e }
            androidx.core.app.NotificationCompat$BigTextStyle r1 = r1.bigText(r10)     // Catch:{ Exception -> 0x0c8e }
            r6.setStyle(r1)     // Catch:{ Exception -> 0x0c8e }
            r13 = r11
            goto L_0x03b2
        L_0x02f2:
            r51 = r10
            r6.setContentText(r7)     // Catch:{ Exception -> 0x0c8e }
            androidx.core.app.NotificationCompat$InboxStyle r3 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0c8e }
            r3.<init>()     // Catch:{ Exception -> 0x0c8e }
            r3.setBigContentTitle(r5)     // Catch:{ Exception -> 0x0c8e }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r15.pushMessages     // Catch:{ Exception -> 0x0c8e }
            int r8 = r8.size()     // Catch:{ Exception -> 0x0c8e }
            r9 = 10
            int r8 = java.lang.Math.min(r9, r8)     // Catch:{ Exception -> 0x0c8e }
            r9 = 1
            boolean[] r10 = new boolean[r9]     // Catch:{ Exception -> 0x0c8e }
            r9 = r10
            r10 = 0
            r11 = r10
            r10 = r43
        L_0x0313:
            if (r11 >= r8) goto L_0x03a0
            r43 = r8
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r15.pushMessages     // Catch:{ Exception -> 0x0c8e }
            java.lang.Object r8 = r8.get(r11)     // Catch:{ Exception -> 0x0c8e }
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8     // Catch:{ Exception -> 0x0c8e }
            r53 = r6
            r54 = r7
            r6 = 0
            r7 = 0
            java.lang.String r55 = r15.getStringForMessage(r8, r7, r9, r6)     // Catch:{ Exception -> 0x0c8e }
            r6 = r55
            if (r6 == 0) goto L_0x0394
            org.telegram.tgnet.TLRPC$Message r7 = r8.messageOwner     // Catch:{ Exception -> 0x0c8e }
            int r7 = r7.date     // Catch:{ Exception -> 0x0c8e }
            if (r7 > r12) goto L_0x0334
            goto L_0x0396
        L_0x0334:
            r7 = 2
            if (r10 != r7) goto L_0x033e
            r45 = r6
            org.telegram.tgnet.TLRPC$Message r7 = r8.messageOwner     // Catch:{ Exception -> 0x0c8e }
            boolean r7 = r7.silent     // Catch:{ Exception -> 0x0c8e }
            r10 = r7
        L_0x033e:
            androidx.collection.LongSparseArray<java.lang.Integer> r7 = r15.pushDialogs     // Catch:{ Exception -> 0x0c8e }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0c8e }
            r55 = r8
            r8 = 1
            if (r7 != r8) goto L_0x0390
            if (r34 == 0) goto L_0x0390
            if (r4 == 0) goto L_0x0362
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r7.<init>()     // Catch:{ Exception -> 0x0c8e }
            r7.append(r13)     // Catch:{ Exception -> 0x0c8e }
            r7.append(r5)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r7 = r6.replace(r7, r14)     // Catch:{ Exception -> 0x0c8e }
            r6 = r7
            goto L_0x0390
        L_0x0362:
            r7 = 0
            boolean r8 = r9[r7]     // Catch:{ Exception -> 0x0c8e }
            if (r8 == 0) goto L_0x037c
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r7.<init>()     // Catch:{ Exception -> 0x0c8e }
            r7.append(r5)     // Catch:{ Exception -> 0x0c8e }
            r7.append(r1)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r7 = r6.replace(r7, r14)     // Catch:{ Exception -> 0x0c8e }
            r6 = r7
            goto L_0x0390
        L_0x037c:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r7.<init>()     // Catch:{ Exception -> 0x0c8e }
            r7.append(r5)     // Catch:{ Exception -> 0x0c8e }
            r7.append(r2)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r7 = r6.replace(r7, r14)     // Catch:{ Exception -> 0x0c8e }
            r6 = r7
        L_0x0390:
            r3.addLine(r6)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x0396
        L_0x0394:
            r55 = r8
        L_0x0396:
            int r11 = r11 + 1
            r8 = r43
            r6 = r53
            r7 = r54
            goto L_0x0313
        L_0x03a0:
            r53 = r6
            r54 = r7
            r43 = r8
            r3.setSummaryText(r7)     // Catch:{ Exception -> 0x0c8e }
            r6 = r53
            r6.setStyle(r3)     // Catch:{ Exception -> 0x0c8e }
            r13 = r10
            r9 = r45
        L_0x03b2:
            if (r69 == 0) goto L_0x03c3
            if (r33 == 0) goto L_0x03c3
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0c8e }
            boolean r1 = r1.isRecordingAudio()     // Catch:{ Exception -> 0x0c8e }
            if (r1 != 0) goto L_0x03c3
            r1 = 1
            if (r13 != r1) goto L_0x03c6
        L_0x03c3:
            r1 = 1
            r31 = r1
        L_0x03c6:
            java.lang.String r1 = "custom_"
            if (r31 != 0) goto L_0x0497
            int r8 = (r51 > r47 ? 1 : (r51 == r47 ? 0 : -1))
            if (r8 != 0) goto L_0x0497
            if (r4 == 0) goto L_0x0497
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r8.<init>()     // Catch:{ Exception -> 0x0c8e }
            r8.append(r1)     // Catch:{ Exception -> 0x0c8e }
            r10 = r51
            r8.append(r10)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0c8e }
            r2 = r46
            r3 = 0
            boolean r8 = r2.getBoolean(r8, r3)     // Catch:{ Exception -> 0x0c8e }
            if (r8 == 0) goto L_0x041f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r3.<init>()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r8 = "smart_max_count_"
            r3.append(r8)     // Catch:{ Exception -> 0x0c8e }
            r3.append(r10)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0c8e }
            r8 = 2
            int r3 = r2.getInt(r3, r8)     // Catch:{ Exception -> 0x0c8e }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r8.<init>()     // Catch:{ Exception -> 0x0c8e }
            r43 = r3
            java.lang.String r3 = "smart_delay_"
            r8.append(r3)     // Catch:{ Exception -> 0x0c8e }
            r8.append(r10)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r3 = r8.toString()     // Catch:{ Exception -> 0x0c8e }
            r8 = 180(0xb4, float:2.52E-43)
            int r3 = r2.getInt(r3, r8)     // Catch:{ Exception -> 0x0c8e }
            r8 = r3
            r3 = r43
            goto L_0x0422
        L_0x041f:
            r3 = 2
            r8 = 180(0xb4, float:2.52E-43)
        L_0x0422:
            if (r3 == 0) goto L_0x048c
            r43 = r12
            androidx.collection.LongSparseArray<android.graphics.Point> r12 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0c8e }
            java.lang.Object r12 = r12.get(r10)     // Catch:{ Exception -> 0x0c8e }
            android.graphics.Point r12 = (android.graphics.Point) r12     // Catch:{ Exception -> 0x0c8e }
            if (r12 != 0) goto L_0x044b
            r54 = r7
            android.graphics.Point r7 = new android.graphics.Point     // Catch:{ Exception -> 0x0c8e }
            long r45 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0c8e }
            r55 = r13
            r53 = r14
            r51 = 1000(0x3e8, double:4.94E-321)
            long r13 = r45 / r51
            int r14 = (int) r13     // Catch:{ Exception -> 0x0c8e }
            r13 = 1
            r7.<init>(r13, r14)     // Catch:{ Exception -> 0x0c8e }
            androidx.collection.LongSparseArray<android.graphics.Point> r12 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0c8e }
            r12.put(r10, r7)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x04a3
        L_0x044b:
            r54 = r7
            r55 = r13
            r53 = r14
            int r7 = r12.y     // Catch:{ Exception -> 0x0c8e }
            int r13 = r7 + r8
            long r13 = (long) r13     // Catch:{ Exception -> 0x0c8e }
            long r45 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0c8e }
            r51 = 1000(0x3e8, double:4.94E-321)
            long r45 = r45 / r51
            int r56 = (r13 > r45 ? 1 : (r13 == r45 ? 0 : -1))
            if (r56 >= 0) goto L_0x046e
            long r13 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0c8e }
            long r13 = r13 / r51
            int r14 = (int) r13     // Catch:{ Exception -> 0x0c8e }
            r13 = 1
            r12.set(r13, r14)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x04a3
        L_0x046e:
            int r13 = r12.x     // Catch:{ Exception -> 0x0c8e }
            if (r13 >= r3) goto L_0x0485
            int r14 = r13 + 1
            long r45 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0c8e }
            r57 = r7
            r56 = r8
            r51 = 1000(0x3e8, double:4.94E-321)
            long r7 = r45 / r51
            int r8 = (int) r7     // Catch:{ Exception -> 0x0c8e }
            r12.set(r14, r8)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x04a3
        L_0x0485:
            r57 = r7
            r56 = r8
            r31 = 1
            goto L_0x04a3
        L_0x048c:
            r54 = r7
            r56 = r8
            r43 = r12
            r55 = r13
            r53 = r14
            goto L_0x04a3
        L_0x0497:
            r54 = r7
            r43 = r12
            r55 = r13
            r53 = r14
            r2 = r46
            r10 = r51
        L_0x04a3:
            android.net.Uri r3 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r3 = r3.getPath()     // Catch:{ Exception -> 0x0c8e }
            r14 = r3
            r3 = 1
            boolean r7 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0c8e }
            if (r7 != 0) goto L_0x04b1
            r7 = 1
            goto L_0x04b2
        L_0x04b1:
            r7 = 0
        L_0x04b2:
            r45 = r7
            r7 = 1
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r8.<init>()     // Catch:{ Exception -> 0x0c8e }
            r8.append(r1)     // Catch:{ Exception -> 0x0c8e }
            r8.append(r10)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r8.toString()     // Catch:{ Exception -> 0x0c8e }
            r8 = 0
            boolean r1 = r2.getBoolean(r1, r8)     // Catch:{ Exception -> 0x0c8e }
            r8 = 3
            if (r1 == 0) goto L_0x0556
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r1.<init>()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r12 = "vibrate_"
            r1.append(r12)     // Catch:{ Exception -> 0x0c8e }
            r1.append(r10)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0c8e }
            r12 = 0
            int r1 = r2.getInt(r1, r12)     // Catch:{ Exception -> 0x0c8e }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r12.<init>()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r13 = "priority_"
            r12.append(r13)     // Catch:{ Exception -> 0x0c8e }
            r12.append(r10)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0c8e }
            int r12 = r2.getInt(r12, r8)     // Catch:{ Exception -> 0x0c8e }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r13.<init>()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r8 = "sound_path_"
            r13.append(r8)     // Catch:{ Exception -> 0x0c8e }
            r13.append(r10)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r8 = r13.toString()     // Catch:{ Exception -> 0x0c8e }
            r13 = 0
            java.lang.String r8 = r2.getString(r8, r13)     // Catch:{ Exception -> 0x0c8e }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r13.<init>()     // Catch:{ Exception -> 0x0c8e }
            r56 = r1
            r1 = r44
            r13.append(r1)     // Catch:{ Exception -> 0x0c8e }
            r13.append(r10)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0c8e }
            boolean r13 = r2.contains(r13)     // Catch:{ Exception -> 0x0c8e }
            if (r13 == 0) goto L_0x054b
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r13.<init>()     // Catch:{ Exception -> 0x0c8e }
            r13.append(r1)     // Catch:{ Exception -> 0x0c8e }
            r13.append(r10)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r13.toString()     // Catch:{ Exception -> 0x0c8e }
            r13 = 0
            int r1 = r2.getInt(r1, r13)     // Catch:{ Exception -> 0x0c8e }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ Exception -> 0x0c8e }
            r44 = r1
            r13 = r56
            r67 = r12
            r12 = r8
            r8 = r67
            goto L_0x0562
        L_0x054b:
            r1 = 0
            r44 = r1
            r13 = r56
            r67 = r12
            r12 = r8
            r8 = r67
            goto L_0x0562
        L_0x0556:
            r1 = 0
            r12 = 3
            r8 = 0
            r13 = 0
            r44 = r13
            r13 = r1
            r67 = r12
            r12 = r8
            r8 = r67
        L_0x0562:
            r1 = 0
            r56 = r1
            int r57 = (r40 > r17 ? 1 : (r40 == r17 ? 0 : -1))
            if (r57 == 0) goto L_0x05da
            if (r20 == 0) goto L_0x05a3
            java.lang.String r1 = "ChannelSoundPath"
            java.lang.String r1 = r2.getString(r1, r14)     // Catch:{ Exception -> 0x0c8e }
            r26 = r1
            java.lang.String r1 = "vibrate_channel"
            r58 = r3
            r3 = 0
            int r1 = r2.getInt(r1, r3)     // Catch:{ Exception -> 0x0c8e }
            r22 = r1
            java.lang.String r1 = "priority_channel"
            r3 = 1
            int r1 = r2.getInt(r1, r3)     // Catch:{ Exception -> 0x0c8e }
            r28 = r1
            java.lang.String r1 = "ChannelLed"
            r3 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r2.getInt(r1, r3)     // Catch:{ Exception -> 0x0c8e }
            r27 = r1
            r7 = 2
            r1 = r22
            r3 = r26
            r22 = r7
            r26 = r14
            r7 = r27
            r14 = r28
            goto L_0x0621
        L_0x05a3:
            r58 = r3
            java.lang.String r1 = "GroupSoundPath"
            java.lang.String r1 = r2.getString(r1, r14)     // Catch:{ Exception -> 0x0c8e }
            r26 = r1
            java.lang.String r1 = "vibrate_group"
            r3 = 0
            int r1 = r2.getInt(r1, r3)     // Catch:{ Exception -> 0x0c8e }
            r22 = r1
            java.lang.String r1 = "priority_group"
            r3 = 1
            int r1 = r2.getInt(r1, r3)     // Catch:{ Exception -> 0x0c8e }
            r28 = r1
            java.lang.String r1 = "GroupLed"
            r3 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r2.getInt(r1, r3)     // Catch:{ Exception -> 0x0c8e }
            r27 = r1
            r7 = 0
            r1 = r22
            r3 = r26
            r22 = r7
            r26 = r14
            r7 = r27
            r14 = r28
            goto L_0x0621
        L_0x05da:
            r58 = r3
            int r1 = (r24 > r17 ? 1 : (r24 == r17 ? 0 : -1))
            if (r1 == 0) goto L_0x0615
            java.lang.String r1 = "GlobalSoundPath"
            java.lang.String r1 = r2.getString(r1, r14)     // Catch:{ Exception -> 0x0c8e }
            r26 = r1
            java.lang.String r1 = "vibrate_messages"
            r3 = 0
            int r1 = r2.getInt(r1, r3)     // Catch:{ Exception -> 0x0c8e }
            r22 = r1
            java.lang.String r1 = "priority_messages"
            r3 = 1
            int r1 = r2.getInt(r1, r3)     // Catch:{ Exception -> 0x0c8e }
            r28 = r1
            java.lang.String r1 = "MessagesLed"
            r3 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r2.getInt(r1, r3)     // Catch:{ Exception -> 0x0c8e }
            r27 = r1
            r7 = 1
            r1 = r22
            r3 = r26
            r22 = r7
            r26 = r14
            r7 = r27
            r14 = r28
            goto L_0x0621
        L_0x0615:
            r1 = r22
            r3 = r26
            r22 = r7
            r26 = r14
            r7 = r27
            r14 = r28
        L_0x0621:
            r27 = r9
            r9 = 4
            if (r1 != r9) goto L_0x062b
            r28 = 1
            r1 = 0
            r56 = r28
        L_0x062b:
            if (r12 == 0) goto L_0x063d
            boolean r28 = android.text.TextUtils.equals(r3, r12)     // Catch:{ Exception -> 0x0c8e }
            if (r28 != 0) goto L_0x063d
            r3 = r12
            r28 = 0
            r67 = r28
            r28 = r3
            r3 = r67
            goto L_0x0641
        L_0x063d:
            r28 = r3
            r3 = r58
        L_0x0641:
            r9 = 3
            if (r8 == r9) goto L_0x0648
            if (r14 == r8) goto L_0x0648
            r14 = r8
            r3 = 0
        L_0x0648:
            if (r44 == 0) goto L_0x0656
            int r9 = r44.intValue()     // Catch:{ Exception -> 0x0c8e }
            if (r9 == r7) goto L_0x0656
            int r9 = r44.intValue()     // Catch:{ Exception -> 0x0c8e }
            r7 = r9
            r3 = 0
        L_0x0656:
            if (r13 == 0) goto L_0x0662
            r9 = 4
            if (r13 == r9) goto L_0x0662
            if (r13 == r1) goto L_0x0662
            r1 = r13
            r3 = 0
            r58 = r3
            goto L_0x0664
        L_0x0662:
            r58 = r3
        L_0x0664:
            if (r45 == 0) goto L_0x068a
            java.lang.String r3 = "EnableInAppSounds"
            r9 = 1
            boolean r3 = r2.getBoolean(r3, r9)     // Catch:{ Exception -> 0x0c8e }
            if (r3 != 0) goto L_0x0671
            r28 = 0
        L_0x0671:
            java.lang.String r3 = "EnableInAppVibrate"
            r9 = 1
            boolean r3 = r2.getBoolean(r3, r9)     // Catch:{ Exception -> 0x0c8e }
            if (r3 != 0) goto L_0x067b
            r1 = 2
        L_0x067b:
            java.lang.String r3 = "EnableInAppPriority"
            r9 = 0
            boolean r3 = r2.getBoolean(r3, r9)     // Catch:{ Exception -> 0x0c8e }
            if (r3 != 0) goto L_0x0686
            r14 = 0
            goto L_0x068a
        L_0x0686:
            r3 = 2
            if (r14 != r3) goto L_0x068a
            r14 = 1
        L_0x068a:
            if (r56 == 0) goto L_0x06a1
            r3 = 2
            if (r1 == r3) goto L_0x06a1
            android.media.AudioManager r3 = audioManager     // Catch:{ Exception -> 0x069c }
            int r3 = r3.getRingerMode()     // Catch:{ Exception -> 0x069c }
            if (r3 == 0) goto L_0x069b
            r9 = 1
            if (r3 == r9) goto L_0x069b
            r1 = 2
        L_0x069b:
            goto L_0x06a1
        L_0x069c:
            r0 = move-exception
            r3 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)     // Catch:{ Exception -> 0x0c8e }
        L_0x06a1:
            if (r31 == 0) goto L_0x06ae
            r1 = 0
            r14 = 0
            r7 = 0
            r28 = 0
            r9 = r7
            r7 = r14
            r3 = r28
            r14 = r1
            goto L_0x06b3
        L_0x06ae:
            r9 = r7
            r7 = r14
            r3 = r28
            r14 = r1
        L_0x06b3:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0c8e }
            r28 = r8
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            r59 = r12
            java.lang.Class<org.telegram.ui.LaunchActivity> r12 = org.telegram.ui.LaunchActivity.class
            r1.<init>(r8, r12)     // Catch:{ Exception -> 0x0c8e }
            r12 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r1.<init>()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r8 = "com.tmessages.openchat"
            r1.append(r8)     // Catch:{ Exception -> 0x0c8e }
            r60 = r13
            r61 = r14
            double r13 = java.lang.Math.random()     // Catch:{ Exception -> 0x0c8e }
            r1.append(r13)     // Catch:{ Exception -> 0x0c8e }
            r8 = 2147483647(0x7fffffff, float:NaN)
            r1.append(r8)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0c8e }
            r12.setAction(r1)     // Catch:{ Exception -> 0x0c8e }
            r1 = 67108864(0x4000000, float:1.5046328E-36)
            r12.setFlags(r1)     // Catch:{ Exception -> 0x0c8e }
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r10)     // Catch:{ Exception -> 0x0c8e }
            if (r1 != 0) goto L_0x07a2
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0c8e }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0c8e }
            r8 = 1
            if (r1 != r8) goto L_0x071d
            int r1 = (r40 > r17 ? 1 : (r40 == r17 ? 0 : -1))
            if (r1 == 0) goto L_0x0707
            java.lang.String r1 = "chatId"
            r13 = r40
            r12.putExtra(r1, r13)     // Catch:{ Exception -> 0x0c8e }
            r40 = r13
            r13 = r24
            goto L_0x071f
        L_0x0707:
            r13 = r40
            int r1 = (r24 > r17 ? 1 : (r24 == r17 ? 0 : -1))
            if (r1 == 0) goto L_0x0718
            java.lang.String r1 = "userId"
            r40 = r13
            r13 = r24
            r12.putExtra(r1, r13)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x071f
        L_0x0718:
            r40 = r13
            r13 = r24
            goto L_0x071f
        L_0x071d:
            r13 = r24
        L_0x071f:
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0c8e }
            if (r1 != 0) goto L_0x079b
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x072f
            r24 = r13
            r8 = r39
            goto L_0x079f
        L_0x072f:
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0c8e }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0c8e }
            r8 = 1
            if (r1 != r8) goto L_0x0796
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            r8 = 28
            if (r1 >= r8) goto L_0x0796
            if (r4 == 0) goto L_0x076b
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x0766
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x0766
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0c8e }
            r24 = r13
            long r13 = r1.volume_id     // Catch:{ Exception -> 0x0c8e }
            int r1 = (r13 > r17 ? 1 : (r13 == r17 ? 0 : -1))
            if (r1 == 0) goto L_0x0768
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0c8e }
            int r1 = r1.local_id     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x0768
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0c8e }
            r14 = r1
            r8 = r39
            goto L_0x07be
        L_0x0766:
            r24 = r13
        L_0x0768:
            r8 = r39
            goto L_0x07bc
        L_0x076b:
            r24 = r13
            if (r39 == 0) goto L_0x0793
            r8 = r39
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r8.photo     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x07bc
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r8.photo     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x07bc
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r8.photo     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0c8e }
            long r13 = r1.volume_id     // Catch:{ Exception -> 0x0c8e }
            int r1 = (r13 > r17 ? 1 : (r13 == r17 ? 0 : -1))
            if (r1 == 0) goto L_0x07bc
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r8.photo     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0c8e }
            int r1 = r1.local_id     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x07bc
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r8.photo     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0c8e }
            r14 = r1
            goto L_0x07be
        L_0x0793:
            r8 = r39
            goto L_0x07bc
        L_0x0796:
            r24 = r13
            r8 = r39
            goto L_0x07bc
        L_0x079b:
            r24 = r13
            r8 = r39
        L_0x079f:
            r1 = 0
            r14 = r1
            goto L_0x07be
        L_0x07a2:
            r8 = r39
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0c8e }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0c8e }
            r13 = 1
            if (r1 != r13) goto L_0x07bc
            long r13 = globalSecretChatId     // Catch:{ Exception -> 0x0c8e }
            int r1 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x07bc
            java.lang.String r1 = "encId"
            int r13 = org.telegram.messenger.DialogObject.getEncryptedChatId(r10)     // Catch:{ Exception -> 0x0c8e }
            r12.putExtra(r1, r13)     // Catch:{ Exception -> 0x0c8e }
        L_0x07bc:
            r14 = r29
        L_0x07be:
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0c8e }
            r13 = r21
            r12.putExtra(r13, r1)     // Catch:{ Exception -> 0x0c8e }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            r62 = r10
            r10 = 1073741824(0x40000000, float:2.0)
            r11 = 0
            android.app.PendingIntent r1 = android.app.PendingIntent.getActivity(r1, r11, r12, r10)     // Catch:{ Exception -> 0x0c8e }
            r11 = r1
            androidx.core.app.NotificationCompat$Builder r1 = r6.setContentTitle(r5)     // Catch:{ Exception -> 0x0c8e }
            r10 = 2131165916(0x7var_dc, float:1.7946063E38)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setSmallIcon((int) r10)     // Catch:{ Exception -> 0x0c8e }
            r10 = 1
            androidx.core.app.NotificationCompat$Builder r1 = r1.setAutoCancel(r10)     // Catch:{ Exception -> 0x0c8e }
            int r10 = r15.total_unread_count     // Catch:{ Exception -> 0x0c8e }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setNumber(r10)     // Catch:{ Exception -> 0x0c8e }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setContentIntent(r11)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r10 = r15.notificationGroup     // Catch:{ Exception -> 0x0c8e }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setGroup(r10)     // Catch:{ Exception -> 0x0c8e }
            r10 = 1
            androidx.core.app.NotificationCompat$Builder r1 = r1.setGroupSummary(r10)     // Catch:{ Exception -> 0x0c8e }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setShowWhen(r10)     // Catch:{ Exception -> 0x0c8e }
            r21 = r5
            r10 = r38
            org.telegram.tgnet.TLRPC$Message r5 = r10.messageOwner     // Catch:{ Exception -> 0x0c8e }
            int r5 = r5.date     // Catch:{ Exception -> 0x0c8e }
            r38 = r11
            r29 = r12
            long r11 = (long) r5     // Catch:{ Exception -> 0x0c8e }
            r51 = 1000(0x3e8, double:4.94E-321)
            long r11 = r11 * r51
            androidx.core.app.NotificationCompat$Builder r1 = r1.setWhen(r11)     // Catch:{ Exception -> 0x0c8e }
            r5 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r1.setColor(r5)     // Catch:{ Exception -> 0x0c8e }
            r1 = 0
            r5 = 0
            java.lang.String r11 = "msg"
            r6.setCategory(r11)     // Catch:{ Exception -> 0x0c8e }
            if (r4 != 0) goto L_0x0844
            if (r8 == 0) goto L_0x0844
            java.lang.String r11 = r8.phone     // Catch:{ Exception -> 0x0c8e }
            if (r11 == 0) goto L_0x0844
            java.lang.String r11 = r8.phone     // Catch:{ Exception -> 0x0c8e }
            int r11 = r11.length()     // Catch:{ Exception -> 0x0c8e }
            if (r11 <= 0) goto L_0x0844
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r11.<init>()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r12 = "tel:+"
            r11.append(r12)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r12 = r8.phone     // Catch:{ Exception -> 0x0c8e }
            r11.append(r12)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0c8e }
            r6.addPerson((java.lang.String) r11)     // Catch:{ Exception -> 0x0c8e }
        L_0x0844:
            android.content.Intent r11 = new android.content.Intent     // Catch:{ Exception -> 0x0c8e }
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            r39 = r1
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r1 = org.telegram.messenger.NotificationDismissReceiver.class
            r11.<init>(r12, r1)     // Catch:{ Exception -> 0x0c8e }
            r12 = r11
            java.lang.String r1 = "messageDate"
            org.telegram.tgnet.TLRPC$Message r11 = r10.messageOwner     // Catch:{ Exception -> 0x0c8e }
            int r11 = r11.date     // Catch:{ Exception -> 0x0c8e }
            r12.putExtra(r1, r11)     // Catch:{ Exception -> 0x0c8e }
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0c8e }
            r12.putExtra(r13, r1)     // Catch:{ Exception -> 0x0c8e }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            r11 = 134217728(0x8000000, float:3.85186E-34)
            r64 = r4
            r4 = 1
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r1, r4, r12, r11)     // Catch:{ Exception -> 0x0c8e }
            r6.setDeleteIntent(r1)     // Catch:{ Exception -> 0x0c8e }
            if (r14 == 0) goto L_0x08d2
            org.telegram.messenger.ImageLoader r1 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r4 = "50_50"
            r11 = 0
            android.graphics.drawable.BitmapDrawable r1 = r1.getImageFromMemory(r14, r11, r4)     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x0886
            android.graphics.Bitmap r4 = r1.getBitmap()     // Catch:{ Exception -> 0x0c8e }
            r6.setLargeIcon(r4)     // Catch:{ Exception -> 0x0c8e }
            r66 = r5
            goto L_0x08d4
        L_0x0886:
            r4 = 1
            java.io.File r11 = org.telegram.messenger.FileLoader.getPathToAttach(r14, r4)     // Catch:{ all -> 0x08cc }
            r4 = r11
            boolean r11 = r4.exists()     // Catch:{ all -> 0x08cc }
            if (r11 == 0) goto L_0x08c7
            r50 = 1112014848(0x42480000, float:50.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r50)     // Catch:{ all -> 0x08cc }
            float r11 = (float) r11     // Catch:{ all -> 0x08cc }
            r50 = 1126170624(0x43200000, float:160.0)
            float r11 = r50 / r11
            android.graphics.BitmapFactory$Options r50 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x08cc }
            r50.<init>()     // Catch:{ all -> 0x08cc }
            r65 = r50
            r50 = 1065353216(0x3var_, float:1.0)
            int r50 = (r11 > r50 ? 1 : (r11 == r50 ? 0 : -1))
            if (r50 >= 0) goto L_0x08ae
            r50 = r1
            r1 = 1
            goto L_0x08b1
        L_0x08ae:
            r50 = r1
            int r1 = (int) r11
        L_0x08b1:
            r66 = r5
            r5 = r65
            r5.inSampleSize = r1     // Catch:{ all -> 0x08c5 }
            java.lang.String r1 = r4.getAbsolutePath()     // Catch:{ all -> 0x08c5 }
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r1, r5)     // Catch:{ all -> 0x08c5 }
            if (r1 == 0) goto L_0x08cb
            r6.setLargeIcon(r1)     // Catch:{ all -> 0x08c5 }
            goto L_0x08cb
        L_0x08c5:
            r0 = move-exception
            goto L_0x08d4
        L_0x08c7:
            r50 = r1
            r66 = r5
        L_0x08cb:
            goto L_0x08d4
        L_0x08cc:
            r0 = move-exception
            r50 = r1
            r66 = r5
            goto L_0x08d4
        L_0x08d2:
            r66 = r5
        L_0x08d4:
            r1 = 0
            r5 = 26
            if (r69 == 0) goto L_0x091e
            r11 = r55
            r4 = 1
            if (r11 != r4) goto L_0x08df
            goto L_0x0920
        L_0x08df:
            if (r7 != 0) goto L_0x08ed
            r4 = 0
            r6.setPriority(r4)     // Catch:{ Exception -> 0x0c8e }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            if (r4 < r5) goto L_0x092c
            r1 = 3
            r30 = r1
            goto L_0x092e
        L_0x08ed:
            r4 = 1
            if (r7 == r4) goto L_0x0912
            r4 = 2
            if (r7 != r4) goto L_0x08f4
            goto L_0x0912
        L_0x08f4:
            r4 = 4
            if (r7 != r4) goto L_0x0903
            r4 = -2
            r6.setPriority(r4)     // Catch:{ Exception -> 0x0c8e }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            if (r4 < r5) goto L_0x092c
            r1 = 1
            r30 = r1
            goto L_0x092e
        L_0x0903:
            r4 = 5
            if (r7 != r4) goto L_0x092c
            r4 = -1
            r6.setPriority(r4)     // Catch:{ Exception -> 0x0c8e }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            if (r4 < r5) goto L_0x092c
            r1 = 2
            r30 = r1
            goto L_0x092e
        L_0x0912:
            r4 = 1
            r6.setPriority(r4)     // Catch:{ Exception -> 0x0c8e }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            if (r4 < r5) goto L_0x092c
            r1 = 4
            r30 = r1
            goto L_0x092e
        L_0x091e:
            r11 = r55
        L_0x0920:
            r4 = -1
            r6.setPriority(r4)     // Catch:{ Exception -> 0x0c8e }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            if (r4 < r5) goto L_0x092c
            r1 = 2
            r30 = r1
            goto L_0x092e
        L_0x092c:
            r30 = r1
        L_0x092e:
            r1 = 1
            if (r11 == r1) goto L_0x0aa9
            if (r31 != 0) goto L_0x0aa9
            if (r45 == 0) goto L_0x0943
            java.lang.String r4 = "EnableInAppPreview"
            boolean r4 = r2.getBoolean(r4, r1)     // Catch:{ Exception -> 0x0c8e }
            if (r4 == 0) goto L_0x093e
            goto L_0x0943
        L_0x093e:
            r1 = r27
            r27 = r2
            goto L_0x097f
        L_0x0943:
            int r1 = r27.length()     // Catch:{ Exception -> 0x0c8e }
            r4 = 100
            if (r1 <= r4) goto L_0x0976
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0c8e }
            r1.<init>()     // Catch:{ Exception -> 0x0c8e }
            r4 = 100
            r5 = r27
            r27 = r2
            r2 = 0
            java.lang.String r4 = r5.substring(r2, r4)     // Catch:{ Exception -> 0x0c8e }
            r2 = 32
            r65 = r5
            r5 = 10
            java.lang.String r2 = r4.replace(r5, r2)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r2 = r2.trim()     // Catch:{ Exception -> 0x0c8e }
            r1.append(r2)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r2 = "..."
            r1.append(r2)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0c8e }
            goto L_0x097c
        L_0x0976:
            r65 = r27
            r27 = r2
            r1 = r65
        L_0x097c:
            r6.setTicker(r1)     // Catch:{ Exception -> 0x0c8e }
        L_0x097f:
            if (r3 == 0) goto L_0x0a36
            java.lang.String r2 = "NoSound"
            boolean r2 = r3.equals(r2)     // Catch:{ Exception -> 0x0c8e }
            if (r2 != 0) goto L_0x0a36
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            r4 = 26
            if (r2 < r4) goto L_0x09af
            r4 = r26
            boolean r2 = r3.equals(r4)     // Catch:{ Exception -> 0x0c8e }
            if (r2 == 0) goto L_0x09a2
            android.net.Uri r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0c8e }
            r26 = r1
            r5 = r2
            r49 = r4
            r55 = r7
            goto L_0x0a3e
        L_0x09a2:
            android.net.Uri r2 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0c8e }
            r26 = r1
            r5 = r2
            r49 = r4
            r55 = r7
            goto L_0x0a3e
        L_0x09af:
            r4 = r26
            boolean r2 = r3.equals(r4)     // Catch:{ Exception -> 0x0c8e }
            if (r2 == 0) goto L_0x09c5
            android.net.Uri r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0c8e }
            r5 = 5
            r6.setSound(r2, r5)     // Catch:{ Exception -> 0x0c8e }
            r26 = r1
            r49 = r4
            r55 = r7
            goto L_0x0a3c
        L_0x09c5:
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            r5 = 24
            if (r2 < r5) goto L_0x0a27
            java.lang.String r2 = "file://"
            boolean r2 = r3.startsWith(r2)     // Catch:{ Exception -> 0x0c8e }
            if (r2 == 0) goto L_0x0a27
            android.net.Uri r2 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0c8e }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r2)     // Catch:{ Exception -> 0x0c8e }
            if (r2 != 0) goto L_0x0a27
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a15 }
            java.lang.String r5 = "org.telegram.messenger.beta.provider"
            r26 = r1
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x0a0e }
            r49 = r4
            java.lang.String r4 = "file://"
            r55 = r7
            r7 = r53
            java.lang.String r4 = r3.replace(r4, r7)     // Catch:{ Exception -> 0x0a06 }
            r1.<init>(r4)     // Catch:{ Exception -> 0x0a06 }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r2, r5, r1)     // Catch:{ Exception -> 0x0a06 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a06 }
            java.lang.String r4 = "com.android.systemui"
            r5 = 1
            r2.grantUriPermission(r4, r1, r5)     // Catch:{ Exception -> 0x0a06 }
            r2 = 5
            r6.setSound(r1, r2)     // Catch:{ Exception -> 0x0a06 }
            goto L_0x0a3c
        L_0x0a06:
            r0 = move-exception
            r1 = r0
            goto L_0x0a1d
        L_0x0a09:
            r0 = move-exception
            r55 = r7
            r1 = r0
            goto L_0x0a1d
        L_0x0a0e:
            r0 = move-exception
            r49 = r4
            r55 = r7
            r1 = r0
            goto L_0x0a1d
        L_0x0a15:
            r0 = move-exception
            r26 = r1
            r49 = r4
            r55 = r7
            r1 = r0
        L_0x0a1d:
            android.net.Uri r2 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0c8e }
            r4 = 5
            r6.setSound(r2, r4)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x0a3c
        L_0x0a27:
            r26 = r1
            r49 = r4
            r55 = r7
            android.net.Uri r1 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0c8e }
            r2 = 5
            r6.setSound(r1, r2)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x0a3c
        L_0x0a36:
            r55 = r7
            r49 = r26
            r26 = r1
        L_0x0a3c:
            r5 = r66
        L_0x0a3e:
            if (r9 == 0) goto L_0x0a47
            r1 = 1000(0x3e8, float:1.401E-42)
            r2 = 1000(0x3e8, float:1.401E-42)
            r6.setLights(r9, r1, r2)     // Catch:{ Exception -> 0x0c8e }
        L_0x0a47:
            r7 = r61
            r1 = 2
            if (r7 != r1) goto L_0x0a5e
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0c8e }
            r1 = 0
            r2[r1] = r17     // Catch:{ Exception -> 0x0c8e }
            r1 = 1
            r2[r1] = r17     // Catch:{ Exception -> 0x0c8e }
            r1 = r2
            r6.setVibrate(r2)     // Catch:{ Exception -> 0x0c8e }
            r39 = r1
            r66 = r5
            goto L_0x0ac4
        L_0x0a5e:
            r1 = 1
            if (r7 != r1) goto L_0x0a7c
            r2 = 4
            long[] r2 = new long[r2]     // Catch:{ Exception -> 0x0c8e }
            r4 = 0
            r2[r4] = r17     // Catch:{ Exception -> 0x0c8e }
            r50 = 100
            r2[r1] = r50     // Catch:{ Exception -> 0x0c8e }
            r1 = 2
            r2[r1] = r17     // Catch:{ Exception -> 0x0c8e }
            r17 = 100
            r1 = 3
            r2[r1] = r17     // Catch:{ Exception -> 0x0c8e }
            r1 = r2
            r6.setVibrate(r2)     // Catch:{ Exception -> 0x0c8e }
            r39 = r1
            r66 = r5
            goto L_0x0ac4
        L_0x0a7c:
            if (r7 == 0) goto L_0x0a9c
            r1 = 4
            if (r7 != r1) goto L_0x0a82
            goto L_0x0a9c
        L_0x0a82:
            r1 = 3
            if (r7 != r1) goto L_0x0a99
            r1 = 2
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0c8e }
            r1 = 0
            r2[r1] = r17     // Catch:{ Exception -> 0x0c8e }
            r1 = 1
            r17 = 1000(0x3e8, double:4.94E-321)
            r2[r1] = r17     // Catch:{ Exception -> 0x0c8e }
            r1 = r2
            r6.setVibrate(r2)     // Catch:{ Exception -> 0x0c8e }
            r39 = r1
            r66 = r5
            goto L_0x0ac4
        L_0x0a99:
            r66 = r5
            goto L_0x0ac4
        L_0x0a9c:
            r1 = 2
            r6.setDefaults(r1)     // Catch:{ Exception -> 0x0c8e }
            r1 = 0
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0c8e }
            r1 = r2
            r39 = r1
            r66 = r5
            goto L_0x0ac4
        L_0x0aa9:
            r55 = r7
            r49 = r26
            r65 = r27
            r7 = r61
            r27 = r2
            r1 = 2
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0c8e }
            r1 = 0
            r2[r1] = r17     // Catch:{ Exception -> 0x0c8e }
            r1 = 1
            r2[r1] = r17     // Catch:{ Exception -> 0x0c8e }
            r1 = r2
            r6.setVibrate(r2)     // Catch:{ Exception -> 0x0c8e }
            r39 = r1
            r26 = r65
        L_0x0ac4:
            r1 = 0
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0c8e }
            if (r2 != 0) goto L_0x0bcf
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0c8e }
            if (r2 != 0) goto L_0x0bcf
            long r4 = r10.getDialogId()     // Catch:{ Exception -> 0x0c8e }
            r17 = 777000(0xbdb28, double:3.83889E-318)
            int r2 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r2 != 0) goto L_0x0bcf
            org.telegram.tgnet.TLRPC$Message r2 = r10.messageOwner     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0c8e }
            if (r2 == 0) goto L_0x0bc2
            org.telegram.tgnet.TLRPC$Message r2 = r10.messageOwner     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0c8e }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows     // Catch:{ Exception -> 0x0c8e }
            r4 = 0
            int r5 = r2.size()     // Catch:{ Exception -> 0x0c8e }
        L_0x0aeb:
            if (r4 >= r5) goto L_0x0baf
            java.lang.Object r17 = r2.get(r4)     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r17 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r17     // Catch:{ Exception -> 0x0c8e }
            r18 = r17
            r17 = 0
            r23 = r1
            r1 = r18
            r18 = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r2 = r1.buttons     // Catch:{ Exception -> 0x0c8e }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0c8e }
            r67 = r17
            r17 = r3
            r3 = r67
        L_0x0b09:
            if (r3 >= r2) goto L_0x0b8c
            r46 = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r2 = r1.buttons     // Catch:{ Exception -> 0x0c8e }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x0c8e }
            org.telegram.tgnet.TLRPC$KeyboardButton r2 = (org.telegram.tgnet.TLRPC.KeyboardButton) r2     // Catch:{ Exception -> 0x0c8e }
            r50 = r1
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x0b69
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0c8e }
            r51 = r5
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            r61 = r7
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r7 = org.telegram.messenger.NotificationCallbackReceiver.class
            r1.<init>(r5, r7)     // Catch:{ Exception -> 0x0c8e }
            int r5 = r15.currentAccount     // Catch:{ Exception -> 0x0c8e }
            r1.putExtra(r13, r5)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r5 = "did"
            r53 = r11
            r52 = r12
            r11 = r62
            r1.putExtra(r5, r11)     // Catch:{ Exception -> 0x0c8e }
            byte[] r5 = r2.data     // Catch:{ Exception -> 0x0c8e }
            if (r5 == 0) goto L_0x0b43
            java.lang.String r5 = "data"
            byte[] r7 = r2.data     // Catch:{ Exception -> 0x0c8e }
            r1.putExtra(r5, r7)     // Catch:{ Exception -> 0x0c8e }
        L_0x0b43:
            java.lang.String r5 = "mid"
            int r7 = r10.getId()     // Catch:{ Exception -> 0x0c8e }
            r1.putExtra(r5, r7)     // Catch:{ Exception -> 0x0c8e }
            java.lang.String r5 = r2.text     // Catch:{ Exception -> 0x0c8e }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            r57 = r2
            int r2 = r15.lastButtonId     // Catch:{ Exception -> 0x0c8e }
            r62 = r8
            int r8 = r2 + 1
            r15.lastButtonId = r8     // Catch:{ Exception -> 0x0c8e }
            r8 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r7, r2, r1, r8)     // Catch:{ Exception -> 0x0c8e }
            r7 = 0
            r6.addAction(r7, r5, r2)     // Catch:{ Exception -> 0x0c8e }
            r2 = 1
            r23 = r2
            goto L_0x0b78
        L_0x0b69:
            r57 = r2
            r51 = r5
            r61 = r7
            r53 = r11
            r52 = r12
            r11 = r62
            r7 = 0
            r62 = r8
        L_0x0b78:
            int r3 = r3 + 1
            r2 = r46
            r1 = r50
            r5 = r51
            r7 = r61
            r8 = r62
            r62 = r11
            r12 = r52
            r11 = r53
            goto L_0x0b09
        L_0x0b8c:
            r50 = r1
            r46 = r2
            r51 = r5
            r61 = r7
            r53 = r11
            r52 = r12
            r11 = r62
            r7 = 0
            r62 = r8
            int r4 = r4 + 1
            r3 = r17
            r2 = r18
            r1 = r23
            r7 = r61
            r62 = r11
            r12 = r52
            r11 = r53
            goto L_0x0aeb
        L_0x0baf:
            r23 = r1
            r18 = r2
            r17 = r3
            r51 = r5
            r61 = r7
            r53 = r11
            r52 = r12
            r11 = r62
            r62 = r8
            goto L_0x0bdd
        L_0x0bc2:
            r17 = r3
            r61 = r7
            r53 = r11
            r52 = r12
            r11 = r62
            r62 = r8
            goto L_0x0bdb
        L_0x0bcf:
            r17 = r3
            r61 = r7
            r53 = r11
            r52 = r12
            r11 = r62
            r62 = r8
        L_0x0bdb:
            r23 = r1
        L_0x0bdd:
            if (r23 != 0) goto L_0x0CLASSNAME
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            r2 = 24
            if (r1 >= r2) goto L_0x0CLASSNAME
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0c8e }
            int r1 = r1.length()     // Catch:{ Exception -> 0x0c8e }
            if (r1 != 0) goto L_0x0CLASSNAME
            boolean r1 = r68.hasMessagesToReply()     // Catch:{ Exception -> 0x0c8e }
            if (r1 == 0) goto L_0x0CLASSNAME
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0c8e }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r3 = org.telegram.messenger.PopupReplyReceiver.class
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x0c8e }
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0c8e }
            r1.putExtra(r13, r2)     // Catch:{ Exception -> 0x0c8e }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0c8e }
            r3 = 19
            if (r2 > r3) goto L_0x0CLASSNAME
            r2 = 2131165494(0x7var_, float:1.7945207E38)
            java.lang.String r3 = "Reply"
            r4 = 2131627477(0x7f0e0dd5, float:1.888222E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0c8e }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r7 = 2
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r4, r7, r1, r5)     // Catch:{ Exception -> 0x0c8e }
            r6.addAction(r2, r3, r4)     // Catch:{ Exception -> 0x0c8e }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r2 = 2131165493(0x7var_, float:1.7945205E38)
            java.lang.String r3 = "Reply"
            r4 = 2131627477(0x7f0e0dd5, float:1.888222E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0c8e }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0c8e }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r7 = 2
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r4, r7, r1, r5)     // Catch:{ Exception -> 0x0c8e }
            r6.addAction(r2, r3, r4)     // Catch:{ Exception -> 0x0c8e }
        L_0x0CLASSNAME:
            r1 = r68
            r13 = r27
            r2 = r6
            r18 = r17
            r17 = r62
            r3 = r54
            r27 = r21
            r19 = r37
            r32 = r49
            r21 = r64
            r4 = r11
            r37 = r6
            r6 = r35
            r46 = r54
            r49 = r55
            r50 = r61
            r7 = r39
            r8 = r9
            r51 = r9
            r9 = r66
            r54 = r10
            r61 = r11
            r10 = r30
            r67 = r53
            r53 = r38
            r38 = r67
            r11 = r58
            r55 = r52
            r52 = r29
            r29 = r43
            r43 = r59
            r12 = r45
            r57 = r38
            r59 = r60
            r38 = r13
            r13 = r31
            r60 = r50
            r50 = r32
            r32 = r54
            r54 = r14
            r14 = r22
            r1.showExtraNotifications(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0c8e }
            r68.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0c8e }
            goto L_0x0CLASSNAME
        L_0x0c8e:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0CLASSNAME:
            return
        L_0x0CLASSNAME:
            r68.dismissNotification()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    private void setNotificationChannel(Notification mainNotification, NotificationCompat.Builder builder, boolean useSummaryNotification) {
        if (useSummaryNotification) {
            builder.setChannelId(OTHER_NOTIFICATIONS_CHANNEL);
        } else {
            builder.setChannelId(mainNotification.getChannelId());
        }
    }

    /* access modifiers changed from: private */
    public void resetNotificationSound(NotificationCompat.Builder notificationBuilder, long dialogId, String chatName, long[] vibrationPattern, int ledColor, Uri sound, int importance, boolean isDefault, boolean isInApp, boolean isSilent, int chatType) {
        long j = dialogId;
        int i = chatType;
        Uri defaultSound = Settings.System.DEFAULT_RINGTONE_URI;
        if (defaultSound == null || sound == null || TextUtils.equals(defaultSound.toString(), sound.toString())) {
            NotificationCompat.Builder builder = notificationBuilder;
            Uri uri = sound;
            return;
        }
        SharedPreferences.Editor editor = getAccountInstance().getNotificationsSettings().edit();
        String newSound = defaultSound.toString();
        String ringtoneName = LocaleController.getString("DefaultRingtone", NUM);
        if (isDefault) {
            if (i == 2) {
                editor.putString("ChannelSound", ringtoneName);
            } else if (i == 0) {
                editor.putString("GroupSound", ringtoneName);
            } else {
                editor.putString("GlobalSound", ringtoneName);
            }
            if (i == 2) {
                editor.putString("ChannelSoundPath", newSound);
            } else if (i == 0) {
                editor.putString("GroupSoundPath", newSound);
            } else {
                editor.putString("GlobalSoundPath", newSound);
            }
            getNotificationsController().m1098xb6var_c1b(i, -1);
        } else {
            editor.putString("sound_" + j, ringtoneName);
            editor.putString("sound_path_" + j, newSound);
            m1097xab324d39(j, -1);
        }
        editor.commit();
        String str = ringtoneName;
        String str2 = newSound;
        SharedPreferences.Editor editor2 = editor;
        notificationBuilder.setChannelId(validateChannelId(dialogId, chatName, vibrationPattern, ledColor, Settings.System.DEFAULT_RINGTONE_URI, importance, isDefault, isInApp, isSilent, chatType));
        notificationManager.notify(this.notificationId, notificationBuilder.build());
    }

    /* JADX WARNING: Removed duplicated region for block: B:191:0x05e5  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x05ed  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0606  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x061b  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x06bc A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x06ea A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x0723  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0977  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0ade  */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0b65  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0b6f  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0b9a  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0ba0  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0bfd  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0c8f  */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0d4f  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0d5a  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x0d5f  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0d6f  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0d75  */
    /* JADX WARNING: Removed duplicated region for block: B:455:0x0d7d  */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0d82  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0d85  */
    /* JADX WARNING: Removed duplicated region for block: B:459:0x0d8b  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d99  */
    /* JADX WARNING: Removed duplicated region for block: B:480:0x0e7b  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0e8c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:491:0x0ebc  */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0ec4  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0fef  */
    /* JADX WARNING: Removed duplicated region for block: B:525:0x1041  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r87, java.lang.String r88, long r89, java.lang.String r91, long[] r92, int r93, android.net.Uri r94, int r95, boolean r96, boolean r97, boolean r98, int r99) {
        /*
            r86 = this;
            r15 = r86
            r14 = r87
            int r0 = android.os.Build.VERSION.SDK_INT
            r13 = 26
            if (r0 < r13) goto L_0x0027
            r1 = r86
            r2 = r89
            r4 = r91
            r5 = r92
            r6 = r93
            r7 = r94
            r8 = r95
            r9 = r96
            r10 = r97
            r11 = r98
            r12 = r99
            java.lang.String r0 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r14.setChannelId(r0)
        L_0x0027:
            android.app.Notification r12 = r87.build()
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 18
            if (r0 >= r1) goto L_0x0043
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.notify(r1, r12)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0042
            java.lang.String r0 = "show summary notification by SDK check"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0042:
            return
        L_0x0043:
            org.telegram.messenger.AccountInstance r0 = r86.getAccountInstance()
            android.content.SharedPreferences r11 = r0.getNotificationsSettings()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r10 = r0
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r9 = r0
            r0 = 0
        L_0x0058:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r15.pushMessages
            int r1 = r1.size()
            r8 = 0
            if (r0 >= r1) goto L_0x00a7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r15.pushMessages
            java.lang.Object r1 = r1.get(r0)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            long r2 = r1.getDialogId()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "dismissDate"
            r4.append(r5)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            int r4 = r11.getInt(r4, r8)
            org.telegram.tgnet.TLRPC$Message r5 = r1.messageOwner
            int r5 = r5.date
            if (r5 > r4) goto L_0x0089
            goto L_0x00a4
        L_0x0089:
            java.lang.Object r5 = r9.get(r2)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x00a1
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r5 = r6
            r9.put(r2, r5)
            java.lang.Long r6 = java.lang.Long.valueOf(r2)
            r10.add(r6)
        L_0x00a1:
            r5.add(r1)
        L_0x00a4:
            int r0 = r0 + 1
            goto L_0x0058
        L_0x00a7:
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r7 = r0
            r0 = 0
        L_0x00ae:
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.wearNotificationsIds
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x00ca
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.wearNotificationsIds
            long r1 = r1.keyAt(r0)
            androidx.collection.LongSparseArray<java.lang.Integer> r3 = r15.wearNotificationsIds
            java.lang.Object r3 = r3.valueAt(r0)
            java.lang.Integer r3 = (java.lang.Integer) r3
            r7.put(r1, r3)
            int r0 = r0 + 1
            goto L_0x00ae
        L_0x00ca:
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r0.clear()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6 = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 27
            r5 = 1
            if (r0 <= r4) goto L_0x00e5
            int r0 = r10.size()
            if (r0 <= r5) goto L_0x00e3
            goto L_0x00e5
        L_0x00e3:
            r0 = 0
            goto L_0x00e6
        L_0x00e5:
            r0 = 1
        L_0x00e6:
            r2 = r0
            if (r2 == 0) goto L_0x00f0
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r13) goto L_0x00f0
            checkOtherNotificationsChannel()
        L_0x00f0:
            org.telegram.messenger.UserConfig r0 = r86.getUserConfig()
            long r13 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x0105
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x0103
            goto L_0x0105
        L_0x0103:
            r0 = 0
            goto L_0x0106
        L_0x0105:
            r0 = 1
        L_0x0106:
            r19 = r0
            r3 = 7
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r1 = r0
            r0 = 0
            int r4 = r10.size()
            r5 = r0
        L_0x0115:
            if (r5 >= r4) goto L_0x0var_
            int r0 = r6.size()
            if (r0 < r3) goto L_0x0132
            r81 = r1
            r43 = r2
            r23 = r3
            r41 = r7
            r27 = r9
            r26 = r10
            r22 = r11
            r84 = r12
            r35 = r13
            r14 = r6
            goto L_0x0var_
        L_0x0132:
            java.lang.Object r0 = r10.get(r5)
            java.lang.Long r0 = (java.lang.Long) r0
            r20 = r4
            r21 = r5
            long r4 = r0.longValue()
            java.lang.Object r0 = r9.get(r4)
            r22 = r11
            r11 = r0
            java.util.ArrayList r11 = (java.util.ArrayList) r11
            java.lang.Object r0 = r11.get(r8)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r8 = r0.getId()
            java.lang.Object r0 = r7.get(r4)
            java.lang.Integer r0 = (java.lang.Integer) r0
            r24 = r3
            r3 = 32
            if (r0 != 0) goto L_0x0170
            r25 = r0
            int r0 = (int) r4
            r27 = r9
            r26 = r10
            long r9 = r4 >> r3
            int r10 = (int) r9
            int r0 = r0 + r10
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r10 = r0
            goto L_0x017b
        L_0x0170:
            r25 = r0
            r27 = r9
            r26 = r10
            r7.remove(r4)
            r10 = r25
        L_0x017b:
            r9 = 0
            java.lang.Object r0 = r11.get(r9)
            r9 = r0
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            org.telegram.tgnet.TLRPC$Message r0 = r9.messageOwner
            int r3 = r0.date
            r0 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            boolean r34 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            r35 = 777000(0xbdb28, double:3.83889E-318)
            r37 = 0
            if (r34 != 0) goto L_0x035a
            int r34 = (r4 > r35 ? 1 : (r4 == r35 ? 0 : -1))
            if (r34 == 0) goto L_0x01a6
            r34 = 1
            goto L_0x01a8
        L_0x01a6:
            r34 = 0
        L_0x01a8:
            boolean r39 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r39 == 0) goto L_0x028d
            r39 = r0
            org.telegram.messenger.MessagesController r0 = r86.getMessagesController()
            r40 = r6
            java.lang.Long r6 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r6)
            if (r0 != 0) goto L_0x0215
            boolean r6 = r9.isFcmMessage()
            if (r6 == 0) goto L_0x01cc
            java.lang.String r6 = r9.localName
            r41 = r7
            goto L_0x0246
        L_0x01cc:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r6 == 0) goto L_0x01fd
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r41 = r7
            java.lang.String r7 = "not found user to show dialog notification "
            r6.append(r7)
            r6.append(r4)
            java.lang.String r6 = r6.toString()
            org.telegram.messenger.FileLog.w(r6)
            r81 = r1
            r43 = r2
            r84 = r12
            r35 = r13
            r23 = r24
            r14 = r40
            r42 = 27
            r45 = 1
            r60 = 0
            r70 = 26
            goto L_0x0var_
        L_0x01fd:
            r41 = r7
            r81 = r1
            r43 = r2
            r84 = r12
            r35 = r13
            r23 = r24
            r14 = r40
            r42 = 27
            r45 = 1
            r60 = 0
            r70 = 26
            goto L_0x0var_
        L_0x0215:
            r41 = r7
            java.lang.String r6 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            if (r7 == 0) goto L_0x0242
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x0242
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            r28 = r6
            long r6 = r7.volume_id
            int r42 = (r6 > r37 ? 1 : (r6 == r37 ? 0 : -1))
            if (r42 == 0) goto L_0x0244
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            int r6 = r6.local_id
            if (r6 == 0) goto L_0x0244
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            r31 = r6
            r6 = r28
            goto L_0x0246
        L_0x0242:
            r28 = r6
        L_0x0244:
            r6 = r28
        L_0x0246:
            boolean r7 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r7 == 0) goto L_0x0264
            r7 = 2131627470(0x7f0e0dce, float:1.8882205E38)
            r28 = r0
            java.lang.String r0 = "RepliesTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r7)
            r43 = r3
            r44 = r11
            r42 = r12
            r11 = r28
            r12 = r39
            r3 = r2
            goto L_0x043f
        L_0x0264:
            r28 = r0
            int r0 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x0280
            r0 = 2131626344(0x7f0e0968, float:1.8879922E38)
            java.lang.String r7 = "MessageScheduledReminderNotification"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r0)
            r43 = r3
            r44 = r11
            r42 = r12
            r11 = r28
            r12 = r39
            r3 = r2
            goto L_0x043f
        L_0x0280:
            r43 = r3
            r44 = r11
            r42 = r12
            r11 = r28
            r12 = r39
            r3 = r2
            goto L_0x043f
        L_0x028d:
            r39 = r0
            r40 = r6
            r41 = r7
            org.telegram.messenger.MessagesController r0 = r86.getMessagesController()
            long r6 = -r4
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r6)
            if (r0 != 0) goto L_0x0303
            boolean r6 = r9.isFcmMessage()
            if (r6 == 0) goto L_0x02be
            boolean r30 = r9.isSupergroup()
            java.lang.String r6 = r9.localName
            boolean r7 = r9.localChannel
            r43 = r3
            r29 = r7
            r44 = r11
            r42 = r12
            r11 = r28
            r12 = r0
            r3 = r2
            goto L_0x043f
        L_0x02be:
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r6 == 0) goto L_0x02ed
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "not found chat to show dialog notification "
            r6.append(r7)
            r6.append(r4)
            java.lang.String r6 = r6.toString()
            org.telegram.messenger.FileLog.w(r6)
            r81 = r1
            r43 = r2
            r84 = r12
            r35 = r13
            r23 = r24
            r14 = r40
            r42 = 27
            r45 = 1
            r60 = 0
            r70 = 26
            goto L_0x0var_
        L_0x02ed:
            r81 = r1
            r43 = r2
            r84 = r12
            r35 = r13
            r23 = r24
            r14 = r40
            r42 = 27
            r45 = 1
            r60 = 0
            r70 = 26
            goto L_0x0var_
        L_0x0303:
            boolean r6 = r0.megagroup
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r7 == 0) goto L_0x0311
            boolean r7 = r0.megagroup
            if (r7 != 0) goto L_0x0311
            r7 = 1
            goto L_0x0312
        L_0x0311:
            r7 = 0
        L_0x0312:
            r29 = r7
            java.lang.String r7 = r0.title
            r30 = r6
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            if (r6 == 0) goto L_0x034a
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            if (r6 == 0) goto L_0x034a
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            r39 = r7
            long r6 = r6.volume_id
            int r42 = (r6 > r37 ? 1 : (r6 == r37 ? 0 : -1))
            if (r42 == 0) goto L_0x034c
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            int r6 = r6.local_id
            if (r6 == 0) goto L_0x034c
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            r43 = r3
            r31 = r6
            r44 = r11
            r42 = r12
            r11 = r28
            r6 = r39
            r12 = r0
            r3 = r2
            goto L_0x043f
        L_0x034a:
            r39 = r7
        L_0x034c:
            r43 = r3
            r44 = r11
            r42 = r12
            r11 = r28
            r6 = r39
            r12 = r0
            r3 = r2
            goto L_0x043f
        L_0x035a:
            r39 = r0
            r40 = r6
            r41 = r7
            r34 = 0
            long r6 = globalSecretChatId
            int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x0429
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r4)
            org.telegram.messenger.MessagesController r6 = r86.getMessagesController()
            java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r6 = r6.getEncryptedChat(r7)
            if (r6 != 0) goto L_0x03c3
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r7 == 0) goto L_0x03ab
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r42 = r12
            java.lang.String r12 = "not found secret chat to show dialog notification "
            r7.append(r12)
            r7.append(r0)
            java.lang.String r7 = r7.toString()
            org.telegram.messenger.FileLog.w(r7)
            r81 = r1
            r43 = r2
            r35 = r13
            r23 = r24
            r14 = r40
            r84 = r42
            r42 = 27
            r45 = 1
            r60 = 0
            r70 = 26
            goto L_0x0var_
        L_0x03ab:
            r42 = r12
            r81 = r1
            r43 = r2
            r35 = r13
            r23 = r24
            r14 = r40
            r84 = r42
            r42 = 27
            r45 = 1
            r60 = 0
            r70 = 26
            goto L_0x0var_
        L_0x03c3:
            r42 = r12
            org.telegram.messenger.MessagesController r7 = r86.getMessagesController()
            r12 = r2
            r43 = r3
            long r2 = r6.user_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r28 = r7.getUser(r2)
            if (r28 != 0) goto L_0x0425
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x040c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "not found secret chat user to show dialog notification "
            r2.append(r3)
            r44 = r11
            r3 = r12
            long r11 = r6.user_id
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.w(r2)
            r81 = r1
            r43 = r3
            r35 = r13
            r23 = r24
            r14 = r40
            r84 = r42
            r42 = 27
            r45 = 1
            r60 = 0
            r70 = 26
            goto L_0x0var_
        L_0x040c:
            r44 = r11
            r3 = r12
            r81 = r1
            r43 = r3
            r35 = r13
            r23 = r24
            r14 = r40
            r84 = r42
            r42 = 27
            r45 = 1
            r60 = 0
            r70 = 26
            goto L_0x0var_
        L_0x0425:
            r44 = r11
            r3 = r12
            goto L_0x0430
        L_0x0429:
            r43 = r3
            r44 = r11
            r42 = r12
            r3 = r2
        L_0x0430:
            r0 = 2131627669(0x7f0e0e95, float:1.8882609E38)
            java.lang.String r2 = "SecretChatName"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r31 = 0
            r11 = r28
            r12 = r39
        L_0x043f:
            java.lang.String r7 = "NotificationHiddenChatName"
            java.lang.String r2 = "NotificationHiddenName"
            if (r19 == 0) goto L_0x0469
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r0 == 0) goto L_0x0455
            r0 = r6
            r6 = 2131626644(0x7f0e0a94, float:1.888053E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r6 = r0
            goto L_0x045e
        L_0x0455:
            r0 = r6
            r6 = 2131626647(0x7f0e0a97, float:1.8880536E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r6)
            r6 = r0
        L_0x045e:
            r31 = 0
            r34 = 0
            r85 = r31
            r31 = r11
            r11 = r85
            goto L_0x0470
        L_0x0469:
            r0 = r6
            r85 = r31
            r31 = r11
            r11 = r85
        L_0x0470:
            r45 = r3
            if (r11 == 0) goto L_0x04da
            r3 = 1
            java.io.File r33 = org.telegram.messenger.FileLoader.getPathToAttach(r11, r3)
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r0 >= r3) goto L_0x04d3
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            r3 = 0
            r47 = r2
            java.lang.String r2 = "50_50"
            android.graphics.drawable.BitmapDrawable r2 = r0.getImageFromMemory(r11, r3, r2)
            if (r2 == 0) goto L_0x0497
            android.graphics.Bitmap r32 = r2.getBitmap()
            r2 = r32
            r3 = r33
            goto L_0x04e0
        L_0x0497:
            boolean r0 = r33.exists()     // Catch:{ all -> 0x04cf }
            if (r0 == 0) goto L_0x04c8
            r0 = 1126170624(0x43200000, float:160.0)
            r3 = 1112014848(0x42480000, float:50.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ all -> 0x04cf }
            float r3 = (float) r3     // Catch:{ all -> 0x04cf }
            float r0 = r0 / r3
            android.graphics.BitmapFactory$Options r3 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x04cf }
            r3.<init>()     // Catch:{ all -> 0x04cf }
            r48 = 1065353216(0x3var_, float:1.0)
            int r48 = (r0 > r48 ? 1 : (r0 == r48 ? 0 : -1))
            if (r48 >= 0) goto L_0x04b6
            r48 = r2
            r2 = 1
            goto L_0x04b9
        L_0x04b6:
            r48 = r2
            int r2 = (int) r0
        L_0x04b9:
            r3.inSampleSize = r2     // Catch:{ all -> 0x04c6 }
            java.lang.String r2 = r33.getAbsolutePath()     // Catch:{ all -> 0x04c6 }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2, r3)     // Catch:{ all -> 0x04c6 }
            r32 = r2
            goto L_0x04ca
        L_0x04c6:
            r0 = move-exception
            goto L_0x04d5
        L_0x04c8:
            r48 = r2
        L_0x04ca:
            r2 = r32
            r3 = r33
            goto L_0x04e0
        L_0x04cf:
            r0 = move-exception
            r48 = r2
            goto L_0x04d5
        L_0x04d3:
            r47 = r2
        L_0x04d5:
            r2 = r32
            r3 = r33
            goto L_0x04e0
        L_0x04da:
            r47 = r2
            r2 = r32
            r3 = r33
        L_0x04e0:
            if (r12 == 0) goto L_0x0513
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r6)
            if (r3 == 0) goto L_0x0501
            boolean r32 = r3.exists()
            if (r32 == 0) goto L_0x0501
            r32 = r11
            int r11 = android.os.Build.VERSION.SDK_INT
            r33 = r2
            r2 = 28
            if (r11 < r2) goto L_0x0505
            r15.loadRoundAvatar(r3, r0)
            goto L_0x0505
        L_0x0501:
            r33 = r2
            r32 = r11
        L_0x0505:
            r11 = r3
            long r2 = r12.id
            long r2 = -r2
            r48 = r12
            androidx.core.app.Person r12 = r0.build()
            r1.put(r2, r12)
            goto L_0x051a
        L_0x0513:
            r33 = r2
            r32 = r11
            r48 = r12
            r11 = r3
        L_0x051a:
            r0 = 0
            java.lang.String r2 = "max_id"
            java.lang.String r3 = "dialog_id"
            java.lang.String r12 = "currentAccount"
            if (r29 == 0) goto L_0x0532
            if (r30 == 0) goto L_0x0526
            goto L_0x0532
        L_0x0526:
            r50 = r0
            r56 = r2
            r52 = r7
            r53 = r10
            r51 = r11
            goto L_0x05d9
        L_0x0532:
            if (r34 == 0) goto L_0x05cf
            boolean r50 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r50 != 0) goto L_0x05cf
            int r50 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r50 == 0) goto L_0x05cf
            boolean r50 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r50 != 0) goto L_0x05cf
            r50 = r0
            android.content.Intent r0 = new android.content.Intent
            r51 = r11
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            r52 = r7
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r7 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r11, r7)
            r0.putExtra(r3, r4)
            r0.putExtra(r2, r8)
            int r7 = r15.currentAccount
            r0.putExtra(r12, r7)
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r11 = r10.intValue()
            r53 = r10
            r10 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r7 = android.app.PendingIntent.getBroadcast(r7, r11, r0, r10)
            androidx.core.app.RemoteInput$Builder r10 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r11 = "extra_voice_reply"
            r10.<init>(r11)
            r11 = 2131627477(0x7f0e0dd5, float:1.888222E38)
            r54 = r0
            java.lang.String r0 = "Reply"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r11)
            androidx.core.app.RemoteInput$Builder r0 = r10.setLabel(r0)
            androidx.core.app.RemoteInput r0 = r0.build()
            boolean r10 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r10 == 0) goto L_0x059d
            r11 = 1
            java.lang.Object[] r10 = new java.lang.Object[r11]
            r23 = 0
            r10[r23] = r6
            java.lang.String r11 = "ReplyToGroup"
            r56 = r2
            r2 = 2131627478(0x7f0e0dd6, float:1.8882222E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r11, r2, r10)
            goto L_0x05af
        L_0x059d:
            r56 = r2
            r23 = 0
            r2 = 2131627479(0x7f0e0dd7, float:1.8882224E38)
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r11[r23] = r6
            java.lang.String r10 = "ReplyToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r10, r2, r11)
        L_0x05af:
            androidx.core.app.NotificationCompat$Action$Builder r10 = new androidx.core.app.NotificationCompat$Action$Builder
            r11 = 2131165539(0x7var_, float:1.7945298E38)
            r10.<init>((int) r11, (java.lang.CharSequence) r2, (android.app.PendingIntent) r7)
            r11 = 1
            androidx.core.app.NotificationCompat$Action$Builder r10 = r10.setAllowGeneratedReplies(r11)
            androidx.core.app.NotificationCompat$Action$Builder r10 = r10.setSemanticAction(r11)
            androidx.core.app.NotificationCompat$Action$Builder r10 = r10.addRemoteInput(r0)
            r11 = 0
            androidx.core.app.NotificationCompat$Action$Builder r10 = r10.setShowsUserInterface(r11)
            androidx.core.app.NotificationCompat$Action r10 = r10.build()
            r11 = r10
            goto L_0x05db
        L_0x05cf:
            r50 = r0
            r56 = r2
            r52 = r7
            r53 = r10
            r51 = r11
        L_0x05d9:
            r11 = r50
        L_0x05db:
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.pushDialogs
            java.lang.Object r0 = r0.get(r4)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x05ed
            r2 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r2)
            r50 = r0
            goto L_0x05ef
        L_0x05ed:
            r50 = r0
        L_0x05ef:
            int r0 = r50.intValue()
            int r2 = r44.size()
            int r10 = java.lang.Math.max(r0, r2)
            r2 = 2
            r7 = 1
            if (r10 <= r7) goto L_0x061b
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 28
            if (r0 < r7) goto L_0x0606
            goto L_0x061b
        L_0x0606:
            java.lang.Object[] r0 = new java.lang.Object[r2]
            r7 = 0
            r0[r7] = r6
            java.lang.Integer r7 = java.lang.Integer.valueOf(r10)
            r18 = 1
            r0[r18] = r7
            java.lang.String r7 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r7, r0)
            r7 = r0
            goto L_0x061d
        L_0x061b:
            r0 = r6
            r7 = r0
        L_0x061d:
            java.lang.Object r0 = r1.get(r13)
            r54 = r0
            androidx.core.app.Person r54 = (androidx.core.app.Person) r54
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 28
            if (r0 < r2) goto L_0x06a8
            if (r54 != 0) goto L_0x06a8
            org.telegram.messenger.MessagesController r0 = r86.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x0645
            org.telegram.messenger.UserConfig r2 = r86.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r2.getCurrentUser()
            r2 = r0
            goto L_0x0646
        L_0x0645:
            r2 = r0
        L_0x0646:
            if (r2 == 0) goto L_0x06a1
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r2.photo     // Catch:{ all -> 0x0696 }
            if (r0 == 0) goto L_0x06a1
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r2.photo     // Catch:{ all -> 0x0696 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x0696 }
            if (r0 == 0) goto L_0x06a1
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r2.photo     // Catch:{ all -> 0x0696 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x0696 }
            r58 = r10
            r57 = r11
            long r10 = r0.volume_id     // Catch:{ all -> 0x0694 }
            int r0 = (r10 > r37 ? 1 : (r10 == r37 ? 0 : -1))
            if (r0 == 0) goto L_0x06a5
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r2.photo     // Catch:{ all -> 0x0694 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x0694 }
            int r0 = r0.local_id     // Catch:{ all -> 0x0694 }
            if (r0 == 0) goto L_0x06a5
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x0694 }
            r0.<init>()     // Catch:{ all -> 0x0694 }
            java.lang.String r10 = "FromYou"
            r11 = 2131625794(0x7f0e0742, float:1.8878806E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)     // Catch:{ all -> 0x0694 }
            androidx.core.app.Person$Builder r0 = r0.setName(r10)     // Catch:{ all -> 0x0694 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r10 = r2.photo     // Catch:{ all -> 0x0694 }
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.photo_small     // Catch:{ all -> 0x0694 }
            r11 = 1
            java.io.File r10 = org.telegram.messenger.FileLoader.getPathToAttach(r10, r11)     // Catch:{ all -> 0x0694 }
            r15.loadRoundAvatar(r10, r0)     // Catch:{ all -> 0x0694 }
            androidx.core.app.Person r11 = r0.build()     // Catch:{ all -> 0x0694 }
            r1.put(r13, r11)     // Catch:{ all -> 0x0690 }
            r54 = r11
            goto L_0x06a5
        L_0x0690:
            r0 = move-exception
            r54 = r11
            goto L_0x069b
        L_0x0694:
            r0 = move-exception
            goto L_0x069b
        L_0x0696:
            r0 = move-exception
            r58 = r10
            r57 = r11
        L_0x069b:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r11 = r54
            goto L_0x06ae
        L_0x06a1:
            r58 = r10
            r57 = r11
        L_0x06a5:
            r11 = r54
            goto L_0x06ae
        L_0x06a8:
            r58 = r10
            r57 = r11
            r11 = r54
        L_0x06ae:
            org.telegram.tgnet.TLRPC$Message r0 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByRequest
            r2 = 1
            r0 = r0 ^ r2
            r54 = r0
            java.lang.String r2 = ""
            if (r11 == 0) goto L_0x06c5
            if (r54 == 0) goto L_0x06c5
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((androidx.core.app.Person) r11)
            r10 = r0
            goto L_0x06cb
        L_0x06c5:
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((java.lang.CharSequence) r2)
            r10 = r0
        L_0x06cb:
            int r0 = android.os.Build.VERSION.SDK_INT
            r59 = r9
            r9 = 28
            if (r0 < r9) goto L_0x06e1
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r0 == 0) goto L_0x06db
            if (r29 == 0) goto L_0x06e1
        L_0x06db:
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r0 == 0) goto L_0x06e4
        L_0x06e1:
            r10.setConversationTitle(r7)
        L_0x06e4:
            int r0 = android.os.Build.VERSION.SDK_INT
            r9 = 28
            if (r0 < r9) goto L_0x06fb
            if (r29 != 0) goto L_0x06f2
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r0 != 0) goto L_0x06fb
        L_0x06f2:
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r0 == 0) goto L_0x06f9
            goto L_0x06fb
        L_0x06f9:
            r0 = 0
            goto L_0x06fc
        L_0x06fb:
            r0 = 1
        L_0x06fc:
            r10.setGroupConversation(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r9 = r0
            r60 = r7
            r61 = r11
            r7 = 1
            java.lang.String[] r11 = new java.lang.String[r7]
            r62 = r8
            boolean[] r8 = new boolean[r7]
            r0 = 0
            r63 = 0
            int r18 = r44.size()
            int r64 = r18 + -1
            r65 = r63
            r7 = r64
            r63 = r0
        L_0x071f:
            r66 = 1000(0x3e8, double:4.94E-321)
            if (r7 < 0) goto L_0x0b1e
            r64 = r3
            r3 = r44
            java.lang.Object r0 = r3.get(r7)
            r3 = r0
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            java.lang.String r0 = r15.getShortStringForMessage(r3, r11, r8)
            r68 = r12
            java.lang.String r12 = "NotificationMessageScheduledName"
            int r70 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r70 != 0) goto L_0x0741
            r23 = 0
            r11[r23] = r6
            r70 = r6
            goto L_0x075d
        L_0x0741:
            r23 = 0
            boolean r70 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r70 == 0) goto L_0x075b
            r70 = r6
            org.telegram.tgnet.TLRPC$Message r6 = r3.messageOwner
            boolean r6 = r6.from_scheduled
            if (r6 == 0) goto L_0x075d
            r6 = 2131626690(0x7f0e0ac2, float:1.8880623E38)
            java.lang.String r71 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r11[r23] = r71
            goto L_0x075d
        L_0x075b:
            r70 = r6
        L_0x075d:
            if (r0 != 0) goto L_0x07ac
            boolean r6 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r6 == 0) goto L_0x079a
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r12 = "message text is null for "
            r6.append(r12)
            int r12 = r3.getId()
            r6.append(r12)
            java.lang.String r12 = " did = "
            r6.append(r12)
            r72 = r7
            r71 = r8
            long r7 = r3.getDialogId()
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            org.telegram.messenger.FileLog.w(r6)
            r76 = r2
            r39 = r11
            r73 = r13
            r77 = r47
            r75 = r52
            r52 = r1
            goto L_0x0b06
        L_0x079a:
            r72 = r7
            r71 = r8
            r76 = r2
            r39 = r11
            r73 = r13
            r77 = r47
            r75 = r52
            r52 = r1
            goto L_0x0b06
        L_0x07ac:
            r72 = r7
            r71 = r8
            int r6 = r9.length()
            if (r6 <= 0) goto L_0x07bb
            java.lang.String r6 = "\n\n"
            r9.append(r6)
        L_0x07bb:
            int r6 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r6 == 0) goto L_0x07e6
            org.telegram.tgnet.TLRPC$Message r6 = r3.messageOwner
            boolean r6 = r6.from_scheduled
            if (r6 == 0) goto L_0x07e6
            boolean r6 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r6 == 0) goto L_0x07e6
            r6 = 2
            java.lang.Object[] r7 = new java.lang.Object[r6]
            r6 = 2131626690(0x7f0e0ac2, float:1.8880623E38)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r12, r6)
            r8 = 0
            r7[r8] = r6
            r6 = 1
            r7[r6] = r0
            java.lang.String r6 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r6, r7)
            r9.append(r0)
            r6 = r0
            goto L_0x0803
        L_0x07e6:
            r6 = 0
            r7 = r11[r6]
            if (r7 == 0) goto L_0x07ff
            r7 = 2
            java.lang.Object[] r8 = new java.lang.Object[r7]
            r7 = r11[r6]
            r8[r6] = r7
            r6 = 1
            r8[r6] = r0
            java.lang.String r6 = "%1$s: %2$s"
            java.lang.String r6 = java.lang.String.format(r6, r8)
            r9.append(r6)
            goto L_0x0802
        L_0x07ff:
            r9.append(r0)
        L_0x0802:
            r6 = r0
        L_0x0803:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r0 == 0) goto L_0x080b
            r7 = r4
            goto L_0x081b
        L_0x080b:
            if (r29 == 0) goto L_0x080f
            long r7 = -r4
            goto L_0x081b
        L_0x080f:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r0 == 0) goto L_0x081a
            long r7 = r3.getSenderId()
            goto L_0x081b
        L_0x081a:
            r7 = r4
        L_0x081b:
            java.lang.Object r0 = r1.get(r7)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            java.lang.String r12 = ""
            r23 = 0
            r69 = r11[r23]
            if (r69 != 0) goto L_0x0897
            if (r19 == 0) goto L_0x0889
            boolean r69 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r69 == 0) goto L_0x086b
            if (r29 == 0) goto L_0x0856
            r69 = r12
            int r12 = android.os.Build.VERSION.SDK_INT
            r73 = r13
            r13 = 27
            if (r12 <= r13) goto L_0x084b
            r13 = r52
            r12 = 2131626644(0x7f0e0a94, float:1.888053E38)
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r12 = r14
            r14 = r47
            goto L_0x08a8
        L_0x084b:
            r13 = r52
            r12 = 2131626644(0x7f0e0a94, float:1.888053E38)
            r14 = r47
            r12 = 2131626647(0x7f0e0a97, float:1.8880536E38)
            goto L_0x0894
        L_0x0856:
            r69 = r12
            r73 = r13
            r13 = r52
            r12 = 2131626644(0x7f0e0a94, float:1.888053E38)
            r14 = 2131626645(0x7f0e0a95, float:1.8880532E38)
            java.lang.String r12 = "NotificationHiddenChatUserName"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r12, r14)
            r14 = r47
            goto L_0x08a8
        L_0x086b:
            r69 = r12
            r73 = r13
            r13 = r52
            int r12 = android.os.Build.VERSION.SDK_INT
            r14 = 27
            if (r12 <= r14) goto L_0x0883
            r14 = r47
            r12 = 2131626647(0x7f0e0a97, float:1.8880536E38)
            java.lang.String r39 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r12 = r39
            goto L_0x08a8
        L_0x0883:
            r14 = r47
            r12 = 2131626647(0x7f0e0a97, float:1.8880536E38)
            goto L_0x0894
        L_0x0889:
            r69 = r12
            r73 = r13
            r14 = r47
            r13 = r52
            r12 = 2131626647(0x7f0e0a97, float:1.8880536E38)
        L_0x0894:
            r12 = r69
            goto L_0x08a8
        L_0x0897:
            r69 = r12
            r73 = r13
            r14 = r47
            r13 = r52
            r12 = 2131626647(0x7f0e0a97, float:1.8880536E38)
            r23 = 0
            r39 = r11[r23]
            r12 = r39
        L_0x08a8:
            if (r0 == 0) goto L_0x08c0
            r39 = r11
            java.lang.CharSequence r11 = r0.getName()
            boolean r11 = android.text.TextUtils.equals(r11, r12)
            if (r11 != 0) goto L_0x08b7
            goto L_0x08c2
        L_0x08b7:
            r11 = r0
            r69 = r12
            r75 = r13
            r77 = r14
            goto L_0x0971
        L_0x08c0:
            r39 = r11
        L_0x08c2:
            androidx.core.app.Person$Builder r11 = new androidx.core.app.Person$Builder
            r11.<init>()
            androidx.core.app.Person$Builder r11 = r11.setName(r12)
            r23 = 0
            boolean r52 = r71[r23]
            if (r52 == 0) goto L_0x0961
            boolean r52 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r52 != 0) goto L_0x0961
            r52 = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            r69 = r12
            r12 = 28
            if (r0 < r12) goto L_0x095c
            r0 = 0
            boolean r12 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r12 != 0) goto L_0x0950
            if (r29 == 0) goto L_0x08f2
            r76 = r0
            r75 = r13
            r77 = r14
            goto L_0x0956
        L_0x08f2:
            r75 = r13
            long r12 = r3.getSenderId()
            r76 = r0
            org.telegram.messenger.MessagesController r0 = r86.getMessagesController()
            r77 = r14
            java.lang.Long r14 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r14)
            if (r0 != 0) goto L_0x0922
            org.telegram.messenger.MessagesStorage r14 = r86.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r0 = r14.getUserSync(r12)
            if (r0 == 0) goto L_0x091f
            org.telegram.messenger.MessagesController r14 = r86.getMessagesController()
            r78 = r12
            r12 = 1
            r14.putUser(r0, r12)
            goto L_0x0924
        L_0x091f:
            r78 = r12
            goto L_0x0924
        L_0x0922:
            r78 = r12
        L_0x0924:
            if (r0 == 0) goto L_0x094d
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            if (r12 == 0) goto L_0x094d
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
            if (r12 == 0) goto L_0x094d
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
            long r12 = r12.volume_id
            int r14 = (r12 > r37 ? 1 : (r12 == r37 ? 0 : -1))
            if (r14 == 0) goto L_0x094d
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
            int r12 = r12.local_id
            if (r12 == 0) goto L_0x094d
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
            r13 = 1
            java.io.File r12 = org.telegram.messenger.FileLoader.getPathToAttach(r12, r13)
            r0 = r12
            goto L_0x0958
        L_0x094d:
            r0 = r76
            goto L_0x0958
        L_0x0950:
            r76 = r0
            r75 = r13
            r77 = r14
        L_0x0956:
            r0 = r51
        L_0x0958:
            r15.loadRoundAvatar(r0, r11)
            goto L_0x0969
        L_0x095c:
            r75 = r13
            r77 = r14
            goto L_0x0969
        L_0x0961:
            r52 = r0
            r69 = r12
            r75 = r13
            r77 = r14
        L_0x0969:
            androidx.core.app.Person r0 = r11.build()
            r1.put(r7, r0)
            r11 = r0
        L_0x0971:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r0 != 0) goto L_0x0ade
            r12 = 0
            r13 = 0
            boolean r0 = r71[r13]
            if (r0 == 0) goto L_0x0a84
            int r0 = android.os.Build.VERSION.SDK_INT
            r13 = 28
            if (r0 < r13) goto L_0x0a84
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r14 = "activity"
            java.lang.Object r0 = r0.getSystemService(r14)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x0a84
            if (r19 != 0) goto L_0x0a7d
            boolean r0 = r3.isSecretMedia()
            if (r0 != 0) goto L_0x0a7d
            int r0 = r3.type
            r14 = 1
            if (r0 == r14) goto L_0x09af
            boolean r0 = r3.isSticker()
            if (r0 == 0) goto L_0x09a7
            goto L_0x09af
        L_0x09a7:
            r52 = r1
            r76 = r2
            r78 = r7
            goto L_0x0a8a
        L_0x09af:
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            java.io.File r14 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r13 = r3.messageOwner
            int r13 = r13.date
            r78 = r7
            long r7 = (long) r13
            long r7 = r7 * r66
            r0.<init>((java.lang.CharSequence) r6, (long) r7, (androidx.core.app.Person) r11)
            r7 = r0
            boolean r0 = r3.isSticker()
            if (r0 == 0) goto L_0x09cd
            java.lang.String r0 = "image/webp"
            goto L_0x09cf
        L_0x09cd:
            java.lang.String r0 = "image/jpeg"
        L_0x09cf:
            r8 = r0
            boolean r0 = r14.exists()
            if (r0 == 0) goto L_0x09e8
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x09e0 }
            java.lang.String r13 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r0, r13, r14)     // Catch:{ Exception -> 0x09e0 }
            goto L_0x09e5
        L_0x09e0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x09e5:
            r52 = r1
            goto L_0x0a40
        L_0x09e8:
            org.telegram.messenger.FileLoader r0 = r86.getFileLoader()
            java.lang.String r13 = r14.getName()
            boolean r0 = r0.isLoadingFile(r13)
            if (r0 == 0) goto L_0x0a3d
            android.net.Uri$Builder r0 = new android.net.Uri$Builder
            r0.<init>()
            java.lang.String r13 = "content"
            android.net.Uri$Builder r0 = r0.scheme(r13)
            java.lang.String r13 = "org.telegram.messenger.beta.notification_image_provider"
            android.net.Uri$Builder r0 = r0.authority(r13)
            java.lang.String r13 = "msg_media_raw"
            android.net.Uri$Builder r0 = r0.appendPath(r13)
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r52 = r1
            int r1 = r15.currentAccount
            r13.append(r1)
            r13.append(r2)
            java.lang.String r1 = r13.toString()
            android.net.Uri$Builder r0 = r0.appendPath(r1)
            java.lang.String r1 = r14.getName()
            android.net.Uri$Builder r0 = r0.appendPath(r1)
            java.lang.String r1 = r14.getAbsolutePath()
            java.lang.String r13 = "final_path"
            android.net.Uri$Builder r0 = r0.appendQueryParameter(r13, r1)
            android.net.Uri r0 = r0.build()
            goto L_0x0a40
        L_0x0a3d:
            r52 = r1
            r0 = 0
        L_0x0a40:
            if (r0 == 0) goto L_0x0a77
            r7.setData(r8, r0)
            r10.addMessage(r7)
            r1 = r0
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            r76 = r2
            java.lang.String r2 = "com.android.systemui"
            r80 = r7
            r7 = 1
            r13.grantUriPermission(r2, r0, r7)
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda35 r2 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda35
            r2.<init>(r1)
            r7 = r0
            r13 = r1
            r0 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r0)
            java.lang.CharSequence r0 = r3.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0a75
            java.lang.CharSequence r0 = r3.caption
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner
            int r1 = r1.date
            long r1 = (long) r1
            long r1 = r1 * r66
            r10.addMessage((java.lang.CharSequence) r0, (long) r1, (androidx.core.app.Person) r11)
        L_0x0a75:
            r12 = 1
            goto L_0x0a8a
        L_0x0a77:
            r76 = r2
            r80 = r7
            r7 = r0
            goto L_0x0a8a
        L_0x0a7d:
            r52 = r1
            r76 = r2
            r78 = r7
            goto L_0x0a8a
        L_0x0a84:
            r52 = r1
            r76 = r2
            r78 = r7
        L_0x0a8a:
            if (r12 != 0) goto L_0x0a96
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            int r0 = r0.date
            long r0 = (long) r0
            long r0 = r0 * r66
            r10.addMessage((java.lang.CharSequence) r6, (long) r0, (androidx.core.app.Person) r11)
        L_0x0a96:
            r1 = 0
            boolean r0 = r71[r1]
            if (r0 == 0) goto L_0x0add
            if (r19 != 0) goto L_0x0add
            boolean r0 = r3.isVoice()
            if (r0 == 0) goto L_0x0add
            java.util.List r1 = r10.getMessages()
            boolean r0 = r1.isEmpty()
            if (r0 != 0) goto L_0x0add
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 24
            if (r0 < r7) goto L_0x0ac6
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ac3 }
            java.lang.String r7 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r0, r7, r2)     // Catch:{ Exception -> 0x0ac3 }
            goto L_0x0ac5
        L_0x0ac3:
            r0 = move-exception
            r0 = 0
        L_0x0ac5:
            goto L_0x0aca
        L_0x0ac6:
            android.net.Uri r0 = android.net.Uri.fromFile(r2)
        L_0x0aca:
            if (r0 == 0) goto L_0x0add
            int r7 = r1.size()
            r8 = 1
            int r7 = r7 - r8
            java.lang.Object r7 = r1.get(r7)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r7 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r7
            java.lang.String r8 = "audio/ogg"
            r7.setData(r8, r0)
        L_0x0add:
            goto L_0x0aee
        L_0x0ade:
            r52 = r1
            r76 = r2
            r78 = r7
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            int r0 = r0.date
            long r0 = (long) r0
            long r0 = r0 * r66
            r10.addMessage((java.lang.CharSequence) r6, (long) r0, (androidx.core.app.Person) r11)
        L_0x0aee:
            int r0 = (r4 > r35 ? 1 : (r4 == r35 ? 0 : -1))
            if (r0 != 0) goto L_0x0b06
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x0b06
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r1 = r3.getId()
            r63 = r0
            r65 = r1
        L_0x0b06:
            int r7 = r72 + -1
            r11 = r39
            r1 = r52
            r3 = r64
            r12 = r68
            r6 = r70
            r8 = r71
            r13 = r73
            r52 = r75
            r2 = r76
            r47 = r77
            goto L_0x071f
        L_0x0b1e:
            r52 = r1
            r64 = r3
            r70 = r6
            r72 = r7
            r71 = r8
            r39 = r11
            r68 = r12
            r73 = r13
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
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r1 == 0) goto L_0x0b6f
            int r1 = org.telegram.messenger.DialogObject.getEncryptedChatId(r4)
            java.lang.String r2 = "encId"
            r0.putExtra(r2, r1)
            goto L_0x0b82
        L_0x0b6f:
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r1 == 0) goto L_0x0b7c
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r4)
            goto L_0x0b82
        L_0x0b7c:
            long r1 = -r4
            java.lang.String r3 = "chatId"
            r0.putExtra(r3, r1)
        L_0x0b82:
            int r1 = r15.currentAccount
            r2 = r68
            r0.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r3 = 1073741824(0x40000000, float:2.0)
            r6 = 0
            android.app.PendingIntent r14 = android.app.PendingIntent.getActivity(r1, r6, r0, r3)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r13 = r1
            if (r57 == 0) goto L_0x0ba0
            r11 = r57
            r13.addAction(r11)
            goto L_0x0ba2
        L_0x0ba0:
            r11 = r57
        L_0x0ba2:
            android.content.Intent r1 = new android.content.Intent
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r6 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r1.<init>(r3, r6)
            r12 = r1
            r1 = 32
            r12.addFlags(r1)
            java.lang.String r1 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r12.setAction(r1)
            r1 = r64
            r12.putExtra(r1, r4)
            r1 = r56
            r8 = r62
            r12.putExtra(r1, r8)
            int r1 = r15.currentAccount
            r12.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r3 = r53.intValue()
            r6 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r7 = android.app.PendingIntent.getBroadcast(r1, r3, r12, r6)
            androidx.core.app.NotificationCompat$Action$Builder r1 = new androidx.core.app.NotificationCompat$Action$Builder
            r3 = 2131165698(0x7var_, float:1.794562E38)
            r6 = 2131626239(0x7f0e08ff, float:1.8879709E38)
            r25 = r0
            java.lang.String r0 = "MarkAsRead"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r6)
            r1.<init>((int) r3, (java.lang.CharSequence) r0, (android.app.PendingIntent) r7)
            r3 = 2
            androidx.core.app.NotificationCompat$Action$Builder r0 = r1.setSemanticAction(r3)
            r1 = 0
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setShowsUserInterface(r1)
            androidx.core.app.NotificationCompat$Action r0 = r0.build()
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            java.lang.String r3 = "_"
            if (r1 != 0) goto L_0x0c3c
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r1 == 0) goto L_0x0c1f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "tguser"
            r1.append(r6)
            r1.append(r4)
            r1.append(r3)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            r28 = r7
            r7 = r1
            goto L_0x0CLASSNAME
        L_0x0c1f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "tgchat"
            r1.append(r6)
            r28 = r7
            long r6 = -r4
            r1.append(r6)
            r1.append(r3)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            r7 = r1
            goto L_0x0CLASSNAME
        L_0x0c3c:
            r28 = r7
            long r6 = globalSecretChatId
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "tgenc"
            r1.append(r6)
            int r6 = org.telegram.messenger.DialogObject.getEncryptedChatId(r4)
            r1.append(r6)
            r1.append(r3)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            r7 = r1
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r1 = 0
            r7 = r1
        L_0x0CLASSNAME:
            if (r7 == 0) goto L_0x0c8f
            r13.setDismissalId(r7)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "summary_"
            r3.append(r6)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r1.setDismissalId(r3)
            r6 = r87
            r35 = r7
            r62 = r8
            r7 = r73
            r6.extend(r1)
            goto L_0x0CLASSNAME
        L_0x0c8f:
            r6 = r87
            r35 = r7
            r62 = r8
            r7 = r73
        L_0x0CLASSNAME:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "tgaccount"
            r1.append(r3)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            r13.setBridgeTag(r1)
            r3 = r44
            r1 = 0
            java.lang.Object r36 = r3.get(r1)
            r1 = r36
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.date
            r73 = r7
            long r6 = (long) r1
            long r7 = r6 * r66
            androidx.core.app.NotificationCompat$Builder r1 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            r1.<init>(r6)
            r6 = r70
            androidx.core.app.NotificationCompat$Builder r1 = r1.setContentTitle(r6)
            r6 = 2131165916(0x7var_dc, float:1.7946063E38)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setSmallIcon((int) r6)
            java.lang.String r6 = r9.toString()
            androidx.core.app.NotificationCompat$Builder r1 = r1.setContentText(r6)
            r6 = 1
            androidx.core.app.NotificationCompat$Builder r1 = r1.setAutoCancel(r6)
            int r6 = r3.size()
            androidx.core.app.NotificationCompat$Builder r1 = r1.setNumber(r6)
            r6 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setColor(r6)
            r6 = 0
            androidx.core.app.NotificationCompat$Builder r1 = r1.setGroupSummary(r6)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setWhen(r7)
            r6 = 1
            androidx.core.app.NotificationCompat$Builder r1 = r1.setShowWhen(r6)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setStyle(r10)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setContentIntent(r14)
            androidx.core.app.NotificationCompat$Builder r1 = r1.extend(r13)
            r36 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r36 = r36 - r7
            java.lang.String r6 = java.lang.String.valueOf(r36)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setSortKey(r6)
            java.lang.String r6 = "msg"
            androidx.core.app.NotificationCompat$Builder r6 = r1.setCategory(r6)
            android.content.Intent r1 = new android.content.Intent
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            r36 = r7
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r7 = org.telegram.messenger.NotificationDismissReceiver.class
            r1.<init>(r3, r7)
            r8 = r1
            java.lang.String r1 = "messageDate"
            r3 = r43
            r8.putExtra(r1, r3)
            java.lang.String r1 = "dialogId"
            r8.putExtra(r1, r4)
            int r1 = r15.currentAccount
            r8.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r7 = r53.intValue()
            r3 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r1, r7, r8, r3)
            r6.setDeleteIntent(r1)
            if (r45 == 0) goto L_0x0d58
            java.lang.String r1 = r15.notificationGroup
            r6.setGroup(r1)
            r1 = 1
            r6.setGroupAlertBehavior(r1)
        L_0x0d58:
            if (r11 == 0) goto L_0x0d5d
            r6.addAction(r11)
        L_0x0d5d:
            if (r19 != 0) goto L_0x0d62
            r6.addAction(r0)
        L_0x0d62:
            int r1 = r26.size()
            r3 = 1
            if (r1 != r3) goto L_0x0d75
            boolean r1 = android.text.TextUtils.isEmpty(r88)
            if (r1 != 0) goto L_0x0d75
            r7 = r88
            r6.setSubText(r7)
            goto L_0x0d77
        L_0x0d75:
            r7 = r88
        L_0x0d77:
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r1 == 0) goto L_0x0d82
            r3 = 1
            r6.setLocalOnly(r3)
            goto L_0x0d83
        L_0x0d82:
            r3 = 1
        L_0x0d83:
            if (r33 == 0) goto L_0x0d8b
            r1 = r33
            r6.setLargeIcon(r1)
            goto L_0x0d8d
        L_0x0d8b:
            r1 = r33
        L_0x0d8d:
            r18 = 0
            boolean r33 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r18)
            if (r33 != 0) goto L_0x0e7b
            boolean r18 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r18 != 0) goto L_0x0e7b
            if (r63 == 0) goto L_0x0e6b
            r18 = 0
            int r3 = r63.size()
            r33 = r0
            r0 = r18
        L_0x0da5:
            if (r0 >= r3) goto L_0x0e5b
            r7 = r63
            java.lang.Object r18 = r7.get(r0)
            r38 = r1
            r1 = r18
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r1 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r1
            r18 = 0
            r46 = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r3 = r1.buttons
            int r3 = r3.size()
            r47 = r7
            r7 = r18
        L_0x0dc1:
            if (r7 >= r3) goto L_0x0e38
            r18 = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r3 = r1.buttons
            java.lang.Object r3 = r3.get(r7)
            org.telegram.tgnet.TLRPC$KeyboardButton r3 = (org.telegram.tgnet.TLRPC.KeyboardButton) r3
            r56 = r1
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback
            if (r1 == 0) goto L_0x0e18
            android.content.Intent r1 = new android.content.Intent
            r57 = r8
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            r63 = r9
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r9 = org.telegram.messenger.NotificationCallbackReceiver.class
            r1.<init>(r8, r9)
            int r8 = r15.currentAccount
            r1.putExtra(r2, r8)
            java.lang.String r8 = "did"
            r1.putExtra(r8, r4)
            byte[] r8 = r3.data
            if (r8 == 0) goto L_0x0df5
            byte[] r8 = r3.data
            java.lang.String r9 = "data"
            r1.putExtra(r9, r8)
        L_0x0df5:
            java.lang.String r8 = "mid"
            r9 = r65
            r1.putExtra(r8, r9)
            java.lang.String r8 = r3.text
            r68 = r2
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r64 = r3
            int r3 = r15.lastButtonId
            r65 = r4
            int r4 = r3 + 1
            r15.lastButtonId = r4
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r2, r3, r1, r4)
            r5 = 0
            r6.addAction(r5, r8, r2)
            goto L_0x0e27
        L_0x0e18:
            r68 = r2
            r64 = r3
            r57 = r8
            r63 = r9
            r9 = r65
            r65 = r4
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 0
        L_0x0e27:
            int r7 = r7 + 1
            r3 = r18
            r1 = r56
            r8 = r57
            r4 = r65
            r2 = r68
            r65 = r9
            r9 = r63
            goto L_0x0dc1
        L_0x0e38:
            r56 = r1
            r68 = r2
            r18 = r3
            r57 = r8
            r63 = r9
            r9 = r65
            r65 = r4
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 0
            int r0 = r0 + 1
            r7 = r88
            r1 = r38
            r3 = r46
            r4 = r65
            r65 = r9
            r9 = r63
            r63 = r47
            goto L_0x0da5
        L_0x0e5b:
            r38 = r1
            r46 = r3
            r57 = r8
            r47 = r63
            r63 = r9
            r9 = r65
            r65 = r4
            r5 = 0
            goto L_0x0e8a
        L_0x0e6b:
            r33 = r0
            r38 = r1
            r57 = r8
            r47 = r63
            r63 = r9
            r9 = r65
            r65 = r4
            r5 = 0
            goto L_0x0e8a
        L_0x0e7b:
            r33 = r0
            r38 = r1
            r57 = r8
            r47 = r63
            r63 = r9
            r9 = r65
            r65 = r4
            r5 = 0
        L_0x0e8a:
            if (r48 != 0) goto L_0x0eb4
            if (r31 == 0) goto L_0x0eb4
            r8 = r31
            java.lang.String r0 = r8.phone
            if (r0 == 0) goto L_0x0eb6
            java.lang.String r0 = r8.phone
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0eb6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "tel:+"
            r0.append(r1)
            java.lang.String r1 = r8.phone
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r6.addPerson((java.lang.String) r0)
            goto L_0x0eb6
        L_0x0eb4:
            r8 = r31
        L_0x0eb6:
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 26
            if (r0 < r7) goto L_0x0ec4
            r4 = r42
            r2 = r45
            r15.setNotificationChannel(r4, r6, r2)
            goto L_0x0ec8
        L_0x0ec4:
            r4 = r42
            r2 = r45
        L_0x0ec8:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            r16 = r38
            r3 = r52
            r1 = r0
            int r18 = r53.intValue()
            r81 = r3
            r23 = r24
            r31 = r43
            r24 = r44
            r38 = r51
            r42 = 1
            r3 = r18
            r43 = r2
            r44 = r16
            r2 = r86
            r16 = r4
            r17 = 0
            r42 = 27
            r45 = 1
            r4 = r65
            r46 = r6
            r82 = r40
            r40 = r70
            r6 = r40
            r51 = r36
            r49 = r47
            r18 = 26
            r37 = r28
            r47 = r35
            r28 = r60
            r35 = r73
            r7 = r8
            r17 = r8
            r55 = r62
            r56 = r71
            r60 = 0
            r8 = r48
            r62 = r63
            r63 = r9
            r9 = r46
            r83 = r53
            r53 = r58
            r58 = r10
            r10 = r91
            r64 = r11
            r67 = r17
            r85 = r61
            r61 = r39
            r39 = r85
            r11 = r92
            r68 = r12
            r84 = r16
            r12 = r93
            r69 = r13
            r70 = 26
            r13 = r94
            r71 = r14
            r14 = r95
            r15 = r96
            r16 = r97
            r17 = r98
            r18 = r99
            r1.<init>(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            r14 = r82
            r14.add(r0)
            r15 = r86
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r1 = r65
            r3 = r83
            r0.put(r1, r3)
        L_0x0var_:
            int r5 = r21 + 1
            r6 = r14
            r4 = r20
            r11 = r22
            r3 = r23
            r10 = r26
            r9 = r27
            r13 = r35
            r7 = r41
            r2 = r43
            r1 = r81
            r12 = r84
            r8 = 0
            goto L_0x0115
        L_0x0var_:
            r81 = r1
            r43 = r2
            r23 = r3
            r20 = r4
            r21 = r5
            r41 = r7
            r27 = r9
            r26 = r10
            r22 = r11
            r84 = r12
            r35 = r13
            r14 = r6
        L_0x0var_:
            if (r43 == 0) goto L_0x0fd7
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0fa5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r15.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0fa5:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager     // Catch:{ SecurityException -> 0x0fb3 }
            int r1 = r15.notificationId     // Catch:{ SecurityException -> 0x0fb3 }
            r13 = r84
            r0.notify(r1, r13)     // Catch:{ SecurityException -> 0x0fb1 }
            r16 = r13
            goto L_0x0fe8
        L_0x0fb1:
            r0 = move-exception
            goto L_0x0fb6
        L_0x0fb3:
            r0 = move-exception
            r13 = r84
        L_0x0fb6:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = r86
            r2 = r87
            r3 = r89
            r5 = r91
            r6 = r92
            r7 = r93
            r8 = r94
            r9 = r95
            r10 = r96
            r11 = r97
            r12 = r98
            r16 = r13
            r13 = r99
            r1.resetNotificationSound(r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x0fe8
        L_0x0fd7:
            r16 = r84
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0fe8
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.cancel(r1)
        L_0x0fe8:
            r0 = 0
        L_0x0fe9:
            int r1 = r41.size()
            if (r0 >= r1) goto L_0x102e
            r9 = r41
            long r1 = r9.keyAt(r0)
            java.util.HashSet<java.lang.Long> r3 = r15.openedInBubbleDialogs
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            boolean r3 = r3.contains(r4)
            if (r3 == 0) goto L_0x1002
            goto L_0x1029
        L_0x1002:
            java.lang.Object r3 = r9.valueAt(r0)
            java.lang.Integer r3 = (java.lang.Integer) r3
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r4 == 0) goto L_0x1020
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "cancel notification id "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r4 = r4.toString()
            org.telegram.messenger.FileLog.d(r4)
        L_0x1020:
            androidx.core.app.NotificationManagerCompat r4 = notificationManager
            int r5 = r3.intValue()
            r4.cancel(r5)
        L_0x1029:
            int r0 = r0 + 1
            r41 = r9
            goto L_0x0fe9
        L_0x102e:
            r9 = r41
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r14.size()
            r0.<init>(r1)
            r1 = 0
            int r10 = r14.size()
            r11 = r1
        L_0x103f:
            if (r11 >= r10) goto L_0x109b
            java.lang.Object r1 = r14.get(r11)
            r12 = r1
            org.telegram.messenger.NotificationsController$1NotificationHolder r12 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r12
            r0.clear()
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 29
            if (r1 < r2) goto L_0x107c
            long r1 = r12.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
            if (r1 != 0) goto L_0x107c
            androidx.core.app.NotificationCompat$Builder r2 = r12.notification
            long r3 = r12.dialogId
            java.lang.String r5 = r12.name
            org.telegram.tgnet.TLRPC$User r6 = r12.user
            org.telegram.tgnet.TLRPC$Chat r7 = r12.chat
            r41 = r9
            long r8 = r12.dialogId
            r13 = r81
            java.lang.Object r1 = r13.get(r8)
            r8 = r1
            androidx.core.app.Person r8 = (androidx.core.app.Person) r8
            r1 = r86
            java.lang.String r1 = r1.createNotificationShortcut(r2, r3, r5, r6, r7, r8)
            if (r1 == 0) goto L_0x1080
            r0.add(r1)
            goto L_0x1080
        L_0x107c:
            r41 = r9
            r13 = r81
        L_0x1080:
            r12.call()
            boolean r1 = r86.unsupportedNotificationShortcut()
            if (r1 != 0) goto L_0x1094
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x1094
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r1, r0)
        L_0x1094:
            int r11 = r11 + 1
            r81 = r13
            r9 = r41
            goto L_0x103f
        L_0x109b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, java.lang.String, long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):void");
    }

    private void loadRoundAvatar(File avatar, Person.Builder personBuilder) {
        if (avatar != null) {
            try {
                personBuilder.setIcon(IconCompat.createWithBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(avatar), NotificationsController$$ExternalSyntheticLambda0.INSTANCE)));
            } catch (Throwable th) {
            }
        }
    }

    static /* synthetic */ int lambda$loadRoundAvatar$35(Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        int width = canvas.getWidth();
        path.addRoundRect(0.0f, 0.0f, (float) width, (float) canvas.getHeight(), (float) (width / 2), (float) (width / 2), Path.Direction.CW);
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
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda2(this));
        }
    }

    /* renamed from: lambda$playOutChatSound$38$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1104x9var_e1fc() {
        try {
            if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundOutPlay) > 100) {
                this.lastSoundOutPlay = SystemClock.elapsedRealtime();
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda33.INSTANCE);
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

    static /* synthetic */ void lambda$playOutChatSound$37(SoundPool soundPool2, int sampleId, int status) {
        if (status == 0) {
            try {
                soundPool2.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void clearDialogNotificationsSettings(long did) {
        SharedPreferences.Editor editor = getAccountInstance().getNotificationsSettings().edit();
        SharedPreferences.Editor remove = editor.remove("notify2_" + did);
        remove.remove("custom_" + did);
        getMessagesStorage().setDialogFlags(did, 0);
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(did);
        if (dialog != null) {
            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
        }
        editor.commit();
        getNotificationsController().updateServerNotificationsSettings(did, true);
    }

    public void setDialogNotificationsSettings(long dialog_id, int setting) {
        long flags;
        SharedPreferences.Editor editor = getAccountInstance().getNotificationsSettings().edit();
        TLRPC.Dialog dialog = MessagesController.getInstance(UserConfig.selectedAccount).dialogs_dict.get(dialog_id);
        if (setting == 4) {
            if (isGlobalNotificationsEnabled(dialog_id)) {
                editor.remove("notify2_" + dialog_id);
            } else {
                editor.putInt("notify2_" + dialog_id, 0);
            }
            getMessagesStorage().setDialogFlags(dialog_id, 0);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
        } else {
            int untilTime = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
            if (setting == 0) {
                untilTime += 3600;
            } else if (setting == 1) {
                untilTime += 28800;
            } else if (setting == 2) {
                untilTime += 172800;
            } else if (setting == 3) {
                untilTime = Integer.MAX_VALUE;
            }
            if (setting == 3) {
                editor.putInt("notify2_" + dialog_id, 2);
                flags = 1;
            } else {
                editor.putInt("notify2_" + dialog_id, 3);
                editor.putInt("notifyuntil_" + dialog_id, untilTime);
                flags = (((long) untilTime) << 32) | 1;
            }
            getInstance(UserConfig.selectedAccount).removeNotificationsForDialog(dialog_id);
            MessagesStorage.getInstance(UserConfig.selectedAccount).setDialogFlags(dialog_id, flags);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                dialog.notify_settings.mute_until = untilTime;
            }
        }
        editor.commit();
        updateServerNotificationsSettings(dialog_id);
    }

    public void updateServerNotificationsSettings(long dialog_id) {
        updateServerNotificationsSettings(dialog_id, true);
    }

    public void updateServerNotificationsSettings(long dialogId, boolean post) {
        int i = 0;
        if (post) {
            getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
        if (!DialogObject.isEncryptedDialog(dialogId)) {
            SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
            TLRPC.TL_account_updateNotifySettings req = new TLRPC.TL_account_updateNotifySettings();
            req.settings = new TLRPC.TL_inputPeerNotifySettings();
            req.settings.flags |= 1;
            req.settings.show_previews = preferences.getBoolean("content_preview_" + dialogId, true);
            TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings = req.settings;
            tL_inputPeerNotifySettings.flags = tL_inputPeerNotifySettings.flags | 2;
            req.settings.silent = preferences.getBoolean("silent_" + dialogId, false);
            int mute_type = preferences.getInt("notify2_" + dialogId, -1);
            if (mute_type != -1) {
                req.settings.flags |= 4;
                if (mute_type == 3) {
                    req.settings.mute_until = preferences.getInt("notifyuntil_" + dialogId, 0);
                } else {
                    TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings2 = req.settings;
                    if (mute_type == 2) {
                        i = Integer.MAX_VALUE;
                    }
                    tL_inputPeerNotifySettings2.mute_until = i;
                }
            }
            req.peer = new TLRPC.TL_inputNotifyPeer();
            ((TLRPC.TL_inputNotifyPeer) req.peer).peer = getMessagesController().getInputPeer(dialogId);
            getConnectionsManager().sendRequest(req, NotificationsController$$ExternalSyntheticLambda32.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$39(TLObject response, TLRPC.TL_error error) {
    }

    public void updateServerNotificationsSettings(int type) {
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        TLRPC.TL_account_updateNotifySettings req = new TLRPC.TL_account_updateNotifySettings();
        req.settings = new TLRPC.TL_inputPeerNotifySettings();
        req.settings.flags = 5;
        if (type == 0) {
            req.peer = new TLRPC.TL_inputNotifyChats();
            req.settings.mute_until = preferences.getInt("EnableGroup2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewGroup", true);
        } else if (type == 1) {
            req.peer = new TLRPC.TL_inputNotifyUsers();
            req.settings.mute_until = preferences.getInt("EnableAll2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewAll", true);
        } else {
            req.peer = new TLRPC.TL_inputNotifyBroadcasts();
            req.settings.mute_until = preferences.getInt("EnableChannel2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewChannel", true);
        }
        getConnectionsManager().sendRequest(req, NotificationsController$$ExternalSyntheticLambda34.INSTANCE);
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$40(TLObject response, TLRPC.TL_error error) {
    }

    public boolean isGlobalNotificationsEnabled(long dialogId) {
        return isGlobalNotificationsEnabled(dialogId, (Boolean) null);
    }

    public boolean isGlobalNotificationsEnabled(long dialogId, Boolean forceChannel) {
        int type;
        if (!DialogObject.isChatDialog(dialogId)) {
            type = 1;
        } else if (forceChannel == null) {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-dialogId));
            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                type = 0;
            } else {
                type = 2;
            }
        } else if (forceChannel.booleanValue()) {
            type = 2;
        } else {
            type = 0;
        }
        return isGlobalNotificationsEnabled(type);
    }

    public boolean isGlobalNotificationsEnabled(int type) {
        return getAccountInstance().getNotificationsSettings().getInt(getGlobalNotificationsKey(type), 0) < getConnectionsManager().getCurrentTime();
    }

    public void setGlobalNotificationsEnabled(int type, int time) {
        getAccountInstance().getNotificationsSettings().edit().putInt(getGlobalNotificationsKey(type), time).commit();
        updateServerNotificationsSettings(type);
        getMessagesStorage().updateMutedDialogsFiltersCounters();
        deleteNotificationChannelGlobal(type);
    }

    public static String getGlobalNotificationsKey(int type) {
        if (type == 0) {
            return "EnableGroup2";
        }
        if (type == 1) {
            return "EnableAll2";
        }
        return "EnableChannel2";
    }
}
