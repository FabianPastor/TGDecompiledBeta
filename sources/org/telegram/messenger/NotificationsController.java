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
import android.util.SparseBooleanArray;
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
    public static final int SETTING_MUTE_CUSTOM = 5;
    public static final int SETTING_MUTE_FOREVER = 3;
    public static final int SETTING_MUTE_HOUR = 0;
    public static final int SETTING_MUTE_UNMUTE = 4;
    public static final int SETTING_SOUND_OFF = 1;
    public static final int SETTING_SOUND_ON = 0;
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
    char[] spoilerChars = {10252, 10338, 10385, 10280};
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
    public /* synthetic */ void m1044lambda$new$0$orgtelegrammessengerNotificationsController() {
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

    public void muteUntil(long did, int selectedTimeInSeconds) {
        long flags;
        if (did != 0) {
            SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            boolean defaultEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(did);
            if (selectedTimeInSeconds != Integer.MAX_VALUE) {
                editor.putInt("notify2_" + did, 3);
                editor.putInt("notifyuntil_" + did, getConnectionsManager().getCurrentTime() + selectedTimeInSeconds);
                flags = (((long) selectedTimeInSeconds) << 32) | 1;
            } else if (!defaultEnabled) {
                editor.remove("notify2_" + did);
                flags = 0;
            } else {
                editor.putInt("notify2_" + did, 2);
                flags = 1;
            }
            getInstance(this.currentAccount).removeNotificationsForDialog(did);
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(did, flags);
            editor.commit();
            TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(did);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
                if (selectedTimeInSeconds != Integer.MAX_VALUE || defaultEnabled) {
                    dialog.notify_settings.mute_until = selectedTimeInSeconds;
                }
            }
            getInstance(this.currentAccount).updateServerNotificationsSettings(did);
        }
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        this.channelGroupsCreated = false;
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda36(this));
    }

    /* renamed from: lambda$cleanup$1$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1037lambda$cleanup$1$orgtelegrammessengerNotificationsController() {
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
    public /* synthetic */ void m1066x8a2b000c(long dialog_id) {
        this.openedDialogId = dialog_id;
    }

    public void setOpenedDialogId(long dialog_id) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda14(this, dialog_id));
    }

    public void setOpenedInBubble(long dialogId, boolean opened) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda30(this, opened, dialogId));
    }

    /* renamed from: lambda$setOpenedInBubble$3$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1067x8CLASSNAMEe1d(boolean opened, long dialogId) {
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
    public /* synthetic */ void m1065xfb3acdad(int time) {
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
    public /* synthetic */ void m1042x16c2e2d7() {
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
    public /* synthetic */ void m1041xdcvar_f8(ArrayList popupArray) {
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
    public /* synthetic */ void m1063x6483a23c(LongSparseArray deletedMessages, ArrayList popupArrayRemove) {
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
    public /* synthetic */ void m1061xf0ee5e7e(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* renamed from: lambda$removeDeletedMessagesFromNotifications$8$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1062x2ab9005d(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void removeDeletedHisoryFromNotifications(LongSparseIntArray deletedMessages) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda28(this, deletedMessages, new ArrayList<>(0)));
    }

    /* renamed from: lambda$removeDeletedHisoryFromNotifications$12$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1060xCLASSNAMEf6e8(LongSparseIntArray deletedMessages, ArrayList popupArrayRemove) {
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
    public /* synthetic */ void m1058x4db2b32a(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* renamed from: lambda$removeDeletedHisoryFromNotifications$11$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1059x877d5509(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void processReadMessages(LongSparseIntArray inbox, long dialogId, int maxDate, int maxId, boolean isPopup) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda29(this, inbox, new ArrayList<>(0), dialogId, maxId, maxDate, isPopup));
    }

    /* renamed from: lambda$processReadMessages$14$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1057x6297dvar_(LongSparseIntArray inbox, ArrayList popupArrayRemove, long dialogId, int maxId, int maxDate, boolean isPopup) {
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
    public /* synthetic */ void m1056x28cd3d59(ArrayList popupArrayRemove) {
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
    public /* synthetic */ void m1050xd706556a(LongSparseArray editedMessages) {
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
                    if (oldMessage != null && oldMessage.isReactionPush) {
                        oldMessage = null;
                    }
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
    public /* synthetic */ void m1055xffba819a(ArrayList messageObjects, ArrayList popupArrayAdd, boolean isFcm, boolean isLast, CountDownLatch countDownLatch) {
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
        LongSparseArray<Boolean> settingsCache2;
        boolean edited2;
        long dialogId2;
        SparseArray<MessageObject> sparseArray2;
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
                        } else {
                            settingsCache2 = settingsCache4;
                            int i3 = index;
                            dialogId = dialogId3;
                            edited = edited3;
                            long j3 = randomId3;
                            long dialogId4 = originalDialogId;
                        }
                        if (messageObject4.isReactionPush) {
                            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
                            sparseBooleanArray.put(mid3, true);
                            getMessagesController().checkUnreadReactions(dialogId, sparseBooleanArray);
                        }
                        edited3 = edited;
                        messageObject2 = true;
                        mid = popup;
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
                    long j4 = did;
                    int i4 = mid;
                    int popup3 = mid2;
                    added2 = messageObject2;
                    long j5 = randomId2;
                    messageObject = messageObject3;
                    int i5 = idxOld;
                    popup2 = addToPopupMessages(popupArrayAdd, messageObject3, dialogId3, isChannel2, preferences);
                } else {
                    MessageObject messageObject6 = oldMessageObject2;
                    SparseArray<MessageObject> sparseArray8 = sparseArray3;
                    added2 = messageObject2;
                    popup2 = mid;
                    long j6 = randomId2;
                    long j7 = did;
                    messageObject = messageObject3;
                    int popup4 = mid2;
                }
                if (isFcm) {
                    boolean z = messageObject.localEdit;
                    edited3 = z;
                    if (z) {
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
                long j8 = randomId2;
                long j9 = did;
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
        boolean z2 = allowPinned2;
        boolean added3 = messageObject2;
        boolean edited4 = edited3;
        int popup6 = mid;
        if (added3) {
            this.notifyCheck = isLast;
        } else {
            boolean z3 = isLast;
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
    public /* synthetic */ void m1053x8CLASSNAMEddc(ArrayList popupArrayAdd, int popupFinal) {
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
    public /* synthetic */ void m1054xc5efdfbb(int pushDialogsCount) {
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
    public /* synthetic */ void m1049xc2d50a00(LongSparseIntArray dialogsToUpdate, ArrayList popupArrayToRemove) {
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
    public /* synthetic */ void m1047x91a07ef7(ArrayList popupArrayToRemove) {
        int size = popupArrayToRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayToRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* renamed from: lambda$processDialogsUpdateRead$20$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1048x890a6821(int pushDialogsCount) {
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
    public /* synthetic */ void m1052xf8b52a58(ArrayList messages, LongSparseArray dialogs, ArrayList push) {
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
    public /* synthetic */ void m1051xbeea8879(int pushDialogsCount) {
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
                                ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>(MessagesController.getInstance(a).allDialogs);
                                int N = dialogs.size();
                                for (int i = 0; i < N; i++) {
                                    TLRPC.Dialog dialog = dialogs.get(i);
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
    public /* synthetic */ void m1069x8d3d4342() {
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
            if (r1 != 0) goto L_0x12e0
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x12e0
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
            r5 = 2131626469(0x7f0e09e5, float:1.8880175E38)
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
            r5 = 2131624856(0x7f0e0398, float:1.8876904E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r0.localName
            r10 = 0
            r6[r10] = r9
            java.lang.String r9 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r5, r6)
            return r5
        L_0x00eb:
            r5 = 2131626831(0x7f0e0b4f, float:1.888091E38)
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
            java.lang.String r5 = r28.replaceSpoilers(r29)
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
            r9 = 2131626808(0x7f0e0b38, float:1.8880863E38)
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
            if (r12 == 0) goto L_0x12c9
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
            if (r10 == 0) goto L_0x12c9
            if (r9 != 0) goto L_0x0272
            java.lang.String r10 = "EnablePreviewGroup"
            r14 = 1
            boolean r10 = r11.getBoolean(r10, r14)
            if (r10 != 0) goto L_0x027d
        L_0x0272:
            if (r9 == 0) goto L_0x12c9
            java.lang.String r10 = "EnablePreviewChannel"
            r14 = 1
            boolean r10 = r11.getBoolean(r10, r14)
            if (r10 == 0) goto L_0x12c9
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
            if (r10 == 0) goto L_0x10c0
            r10 = 0
            r15 = 0
            r30[r15] = r10
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGeoProximityReached
            if (r10 == 0) goto L_0x02a6
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x02a6:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined
            if (r10 != 0) goto L_0x10ac
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r10 == 0) goto L_0x02bc
            r26 = r11
            r27 = r12
            goto L_0x10b0
        L_0x02bc:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto
            if (r10 == 0) goto L_0x02d4
            r3 = 2131626789(0x7f0e0b25, float:1.8880824E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationContactNewPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x02d4:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation
            r15 = 3
            if (r10 == 0) goto L_0x0340
            r3 = 2131629114(0x7f0e143a, float:1.888554E38)
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
            r4 = 2131626858(0x7f0e0b6a, float:1.8880964E38)
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
        L_0x0340:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore
            if (r10 != 0) goto L_0x10a1
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent
            if (r10 == 0) goto L_0x0356
            r26 = r11
            r27 = r12
            goto L_0x10a5
        L_0x0356:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r10 == 0) goto L_0x037a
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3.video
            if (r3 == 0) goto L_0x0370
            r3 = 2131624728(0x7f0e0318, float:1.8876644E38)
            java.lang.String r4 = "CallMessageVideoIncomingMissed"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x0370:
            r3 = 2131624722(0x7f0e0312, float:1.8876632E38)
            java.lang.String r4 = "CallMessageIncomingMissed"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x037a:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r10 == 0) goto L_0x04ad
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            long r3 = r3.user_id
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 != 0) goto L_0x03ac
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            int r6 = r6.size()
            r9 = 1
            if (r6 != r9) goto L_0x03ac
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            r9 = 0
            java.lang.Object r6 = r6.get(r9)
            java.lang.Long r6 = (java.lang.Long) r6
            long r3 = r6.longValue()
        L_0x03ac:
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x0451
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            r26 = r11
            r27 = r12
            long r11 = r6.channel_id
            int r6 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x03d9
            boolean r6 = r5.megagroup
            if (r6 != 0) goto L_0x03d9
            r6 = 2131624805(0x7f0e0365, float:1.88768E38)
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
        L_0x03d9:
            int r6 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r6 != 0) goto L_0x03f2
            r6 = 2131626810(0x7f0e0b3a, float:1.8880867E38)
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
        L_0x03f2:
            org.telegram.messenger.MessagesController r6 = r28.getMessagesController()
            java.lang.Long r9 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r9)
            if (r6 != 0) goto L_0x0402
            r9 = 0
            return r9
        L_0x0402:
            long r9 = r6.id
            int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r11 != 0) goto L_0x0436
            boolean r9 = r5.megagroup
            if (r9 == 0) goto L_0x0421
            r9 = 2131626795(0x7f0e0b2b, float:1.8880836E38)
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
        L_0x0421:
            r10 = 2
            r11 = 0
            r12 = 1
            r9 = 2131626794(0x7f0e0b2a, float:1.8880834E38)
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r10[r11] = r13
            java.lang.String r11 = r5.title
            r10[r12] = r11
            java.lang.String r11 = "NotificationGroupAddSelf"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r11, r9, r10)
            return r9
        L_0x0436:
            r11 = 0
            r12 = 1
            r9 = 2131626793(0x7f0e0b29, float:1.8880832E38)
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
        L_0x0451:
            r26 = r11
            r27 = r12
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r9 = 0
        L_0x045b:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            java.util.ArrayList<java.lang.Long> r10 = r10.users
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x0492
            org.telegram.messenger.MessagesController r10 = r28.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r11 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r11 = r11.action
            java.util.ArrayList<java.lang.Long> r11 = r11.users
            java.lang.Object r11 = r11.get(r9)
            java.lang.Long r11 = (java.lang.Long) r11
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r11)
            if (r10 == 0) goto L_0x048f
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r10)
            int r12 = r6.length()
            if (r12 == 0) goto L_0x048c
            java.lang.String r12 = ", "
            r6.append(r12)
        L_0x048c:
            r6.append(r11)
        L_0x048f:
            int r9 = r9 + 1
            goto L_0x045b
        L_0x0492:
            r9 = 2131626793(0x7f0e0b29, float:1.8880832E38)
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
        L_0x04ad:
            r26 = r11
            r27 = r12
            r12 = 2
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGroupCall
            if (r10 == 0) goto L_0x04ce
            r3 = 2131626797(0x7f0e0b2d, float:1.888084E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationGroupCreatedCall"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x04ce:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGroupCallScheduled
            if (r10 == 0) goto L_0x04dd
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x04dd:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionInviteToGroupCall
            if (r10 == 0) goto L_0x05b1
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            long r3 = r3.user_id
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 != 0) goto L_0x050f
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            int r6 = r6.size()
            r9 = 1
            if (r6 != r9) goto L_0x050f
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            r9 = 0
            java.lang.Object r6 = r6.get(r9)
            java.lang.Long r6 = (java.lang.Long) r6
            long r3 = r6.longValue()
        L_0x050f:
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x0559
            int r6 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r6 != 0) goto L_0x052e
            r6 = 2131626802(0x7f0e0b32, float:1.888085E38)
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
        L_0x052e:
            org.telegram.messenger.MessagesController r6 = r28.getMessagesController()
            java.lang.Long r9 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r9)
            if (r6 != 0) goto L_0x053e
            r9 = 0
            return r9
        L_0x053e:
            r9 = 2131626801(0x7f0e0b31, float:1.8880848E38)
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
        L_0x0559:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r9 = 0
        L_0x055f:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            java.util.ArrayList<java.lang.Long> r10 = r10.users
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x0596
            org.telegram.messenger.MessagesController r10 = r28.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r11 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r11 = r11.action
            java.util.ArrayList<java.lang.Long> r11 = r11.users
            java.lang.Object r11 = r11.get(r9)
            java.lang.Long r11 = (java.lang.Long) r11
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r11)
            if (r10 == 0) goto L_0x0593
            java.lang.String r11 = org.telegram.messenger.UserObject.getUserName(r10)
            int r12 = r6.length()
            if (r12 == 0) goto L_0x0590
            java.lang.String r12 = ", "
            r6.append(r12)
        L_0x0590:
            r6.append(r11)
        L_0x0593:
            int r9 = r9 + 1
            goto L_0x055f
        L_0x0596:
            r9 = 2131626801(0x7f0e0b31, float:1.8880848E38)
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
        L_0x05b1:
            r12 = 2
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r10 == 0) goto L_0x05ce
            r3 = 2131626811(0x7f0e0b3b, float:1.8880869E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationInvitedToGroupByLink"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x05ce:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle
            if (r10 == 0) goto L_0x05ef
            r3 = 2131626790(0x7f0e0b26, float:1.8880826E38)
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
        L_0x05ef:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r10 != 0) goto L_0x1037
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto
            if (r10 == 0) goto L_0x0601
            goto L_0x1037
        L_0x0601:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r10 == 0) goto L_0x0678
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            long r3 = r3.user_id
            int r6 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r6 != 0) goto L_0x0628
            r3 = 2131626804(0x7f0e0b34, float:1.8880855E38)
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
        L_0x0628:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            long r3 = r3.user_id
            int r6 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r6 != 0) goto L_0x0647
            r3 = 2131626805(0x7f0e0b35, float:1.8880857E38)
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
        L_0x0647:
            org.telegram.messenger.MessagesController r3 = r28.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r9 = r4.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 != 0) goto L_0x065d
            r4 = 0
            return r4
        L_0x065d:
            r4 = 2131626803(0x7f0e0b33, float:1.8880853E38)
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
        L_0x0678:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate
            if (r10 == 0) goto L_0x0687
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x0687:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r10 == 0) goto L_0x0696
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x0696:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r10 == 0) goto L_0x06b0
            r3 = 2131624149(0x7f0e00d5, float:1.887547E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "ActionMigrateFromGroupNotify"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x06b0:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r10 == 0) goto L_0x06ce
            r3 = 2131624149(0x7f0e00d5, float:1.887547E38)
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
        L_0x06ce:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken
            if (r10 == 0) goto L_0x06dd
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x06dd:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r10 = r10.action
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r10 == 0) goto L_0x0fc3
            java.lang.String r10 = "..."
            if (r5 == 0) goto L_0x0a19
            boolean r12 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r12 == 0) goto L_0x06f3
            boolean r12 = r5.megagroup
            if (r12 == 0) goto L_0x0a19
        L_0x06f3:
            org.telegram.messenger.MessageObject r12 = r0.replyMessageObject
            if (r12 != 0) goto L_0x070c
            r3 = 2131626758(0x7f0e0b06, float:1.8880761E38)
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
        L_0x070c:
            org.telegram.messenger.MessageObject r12 = r0.replyMessageObject
            boolean r16 = r12.isMusic()
            if (r16 == 0) goto L_0x0729
            r3 = 2131626755(0x7f0e0b03, float:1.8880755E38)
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
        L_0x0729:
            boolean r16 = r12.isVideo()
            java.lang.String r11 = "NotificationActionPinnedText"
            if (r16 == 0) goto L_0x077c
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0767
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0767
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
            r6 = 2131626779(0x7f0e0b1b, float:1.8880804E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x0767:
            r9 = 2
            r3 = 2131626782(0x7f0e0b1e, float:1.888081E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedVideo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x077c:
            boolean r4 = r12.isGif()
            if (r4 == 0) goto L_0x07cd
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x07b8
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x07b8
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
            r6 = 2131626779(0x7f0e0b1b, float:1.8880804E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x07b8:
            r9 = 2
            r3 = 2131626749(0x7f0e0afd, float:1.8880743E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGif"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x07cd:
            boolean r4 = r12.isVoice()
            if (r4 == 0) goto L_0x07e8
            r3 = 2131626785(0x7f0e0b21, float:1.8880816E38)
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
        L_0x07e8:
            boolean r4 = r12.isRoundVideo()
            if (r4 == 0) goto L_0x0803
            r3 = 2131626770(0x7f0e0b12, float:1.8880786E38)
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
        L_0x0803:
            boolean r4 = r12.isSticker()
            if (r4 != 0) goto L_0x09e7
            boolean r4 = r12.isAnimatedSticker()
            if (r4 == 0) goto L_0x0811
            goto L_0x09e7
        L_0x0811:
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x0864
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r9) goto L_0x084f
            org.telegram.tgnet.TLRPC$Message r4 = r12.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x084f
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
            r6 = 2131626779(0x7f0e0b1b, float:1.8880804E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x084f:
            r9 = 2
            r3 = 2131626734(0x7f0e0aee, float:1.8880713E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedFile"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0864:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r3 != 0) goto L_0x09d2
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r3 == 0) goto L_0x0879
            r6 = 0
            r9 = 2
            r10 = 1
            goto L_0x09d5
        L_0x0879:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0896
            r3 = 2131626745(0x7f0e0af9, float:1.8880735E38)
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
        L_0x0896:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r3 == 0) goto L_0x08c3
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r3
            r4 = 2131626731(0x7f0e0aeb, float:1.8880706E38)
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
        L_0x08c3:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r3 == 0) goto L_0x090d
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r4 = r3.poll
            boolean r4 = r4.quiz
            if (r4 == 0) goto L_0x08f2
            r4 = 2131626767(0x7f0e0b0f, float:1.888078E38)
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
        L_0x08f2:
            r4 = 2131626764(0x7f0e0b0c, float:1.8880773E38)
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
        L_0x090d:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0960
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x094b
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x094b
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
            r6 = 2131626779(0x7f0e0b1b, float:1.8880804E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x094b:
            r9 = 2
            r3 = 2131626761(0x7f0e0b09, float:1.8880767E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r9 = 1
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0960:
            org.telegram.tgnet.TLRPC$Message r3 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x097d
            r3 = 2131626737(0x7f0e0af1, float:1.8880719E38)
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
        L_0x097d:
            java.lang.CharSequence r3 = r12.messageText
            if (r3 == 0) goto L_0x09bd
            java.lang.CharSequence r3 = r12.messageText
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x09bd
            java.lang.CharSequence r3 = r12.messageText
            int r4 = r3.length()
            r6 = 20
            if (r4 <= r6) goto L_0x09a8
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r9 = 0
            java.lang.CharSequence r6 = r3.subSequence(r9, r6)
            r4.append(r6)
            r4.append(r10)
            java.lang.String r3 = r4.toString()
            goto L_0x09a9
        L_0x09a8:
            r9 = 0
        L_0x09a9:
            java.lang.Object[] r4 = new java.lang.Object[r15]
            r4[r9] = r13
            r6 = 1
            r4[r6] = r3
            java.lang.String r6 = r5.title
            r9 = 2
            r4[r9] = r6
            r6 = 2131626779(0x7f0e0b1b, float:1.8880804E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r6, r4)
            return r4
        L_0x09bd:
            r9 = 2
            r3 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r10 = 1
            r4[r10] = r6
            java.lang.String r6 = "NotificationActionPinnedNoText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x09d2:
            r6 = 0
            r9 = 2
            r10 = 1
        L_0x09d5:
            r3 = 2131626743(0x7f0e0af7, float:1.888073E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r4[r10] = r6
            java.lang.String r6 = "NotificationActionPinnedGeo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x09e7:
            java.lang.String r3 = r12.getStickerEmoji()
            if (r3 == 0) goto L_0x0a04
            r4 = 2131626775(0x7f0e0b17, float:1.8880796E38)
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
        L_0x0a04:
            r9 = 0
            r10 = 1
            r11 = 2
            r4 = 2131626773(0x7f0e0b15, float:1.8880792E38)
            java.lang.Object[] r6 = new java.lang.Object[r11]
            r6[r9] = r13
            java.lang.String r9 = r5.title
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedSticker"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0a19:
            if (r5 == 0) goto L_0x0d05
            org.telegram.messenger.MessageObject r11 = r0.replyMessageObject
            if (r11 != 0) goto L_0x0a31
            r3 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0a31:
            org.telegram.messenger.MessageObject r11 = r0.replyMessageObject
            boolean r12 = r11.isMusic()
            if (r12 == 0) goto L_0x0a4b
            r3 = 2131626756(0x7f0e0b04, float:1.8880757E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedMusicChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0a4b:
            boolean r12 = r11.isVideo()
            java.lang.String r15 = "NotificationActionPinnedTextChannel"
            if (r12 == 0) goto L_0x0a99
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0a87
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0a87
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
            r6 = 2131626780(0x7f0e0b1c, float:1.8880806E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0a87:
            r6 = 1
            r3 = 2131626783(0x7f0e0b1f, float:1.8880812E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedVideoChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0a99:
            boolean r4 = r11.isGif()
            if (r4 == 0) goto L_0x0ae5
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0ad3
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0ad3
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
            r6 = 2131626780(0x7f0e0b1c, float:1.8880806E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0ad3:
            r12 = 1
            r3 = 2131626750(0x7f0e0afe, float:1.8880745E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            java.lang.String r6 = r5.title
            r14 = 0
            r4[r14] = r6
            java.lang.String r6 = "NotificationActionPinnedGifChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0ae5:
            r12 = 1
            r14 = 0
            boolean r4 = r11.isVoice()
            if (r4 == 0) goto L_0x0afd
            r3 = 2131626786(0x7f0e0b22, float:1.8880818E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            java.lang.String r6 = r5.title
            r4[r14] = r6
            java.lang.String r6 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0afd:
            boolean r4 = r11.isRoundVideo()
            if (r4 == 0) goto L_0x0b13
            r3 = 2131626771(0x7f0e0b13, float:1.8880788E38)
            java.lang.Object[] r4 = new java.lang.Object[r12]
            java.lang.String r6 = r5.title
            r4[r14] = r6
            java.lang.String r6 = "NotificationActionPinnedRoundChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0b13:
            boolean r4 = r11.isSticker()
            if (r4 != 0) goto L_0x0cd8
            boolean r4 = r11.isAnimatedSticker()
            if (r4 == 0) goto L_0x0b21
            goto L_0x0cd8
        L_0x0b21:
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x0b6f
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r9) goto L_0x0b5d
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0b5d
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
            r6 = 2131626780(0x7f0e0b1c, float:1.8880806E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0b5d:
            r6 = 1
            r3 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedFileChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0b6f:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r3 != 0) goto L_0x0cc6
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r3 == 0) goto L_0x0b83
            r6 = 1
            r9 = 0
            goto L_0x0cc8
        L_0x0b83:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0b9d
            r3 = 2131626746(0x7f0e0afa, float:1.8880737E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0b9d:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r3 == 0) goto L_0x0bc8
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r3
            r4 = 2131626732(0x7f0e0aec, float:1.8880709E38)
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
        L_0x0bc8:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r3 == 0) goto L_0x0c0e
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r4 = r3.poll
            boolean r4 = r4.quiz
            if (r4 == 0) goto L_0x0bf5
            r4 = 2131626768(0x7f0e0b10, float:1.8880782E38)
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
        L_0x0bf5:
            r6 = 2
            r10 = 0
            r12 = 1
            r4 = 2131626765(0x7f0e0b0d, float:1.8880775E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r5.title
            r6[r10] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r3.poll
            java.lang.String r9 = r9.question
            r6[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedPollChannel2"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0c0e:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0c5c
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0c4a
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0c4a
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
            r6 = 2131626780(0x7f0e0b1c, float:1.8880806E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0c4a:
            r6 = 1
            r3 = 2131626762(0x7f0e0b0a, float:1.888077E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0c5c:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x0CLASSNAME
            r3 = 2131626738(0x7f0e0af2, float:1.888072E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGameChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0CLASSNAME:
            java.lang.CharSequence r3 = r11.messageText
            if (r3 == 0) goto L_0x0cb4
            java.lang.CharSequence r3 = r11.messageText
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0cb4
            java.lang.CharSequence r3 = r11.messageText
            int r4 = r3.length()
            r6 = 20
            if (r4 <= r6) goto L_0x0ca1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r9 = 0
            java.lang.CharSequence r6 = r3.subSequence(r9, r6)
            r4.append(r6)
            r4.append(r10)
            java.lang.String r3 = r4.toString()
            goto L_0x0ca2
        L_0x0ca1:
            r9 = 0
        L_0x0ca2:
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r4[r9] = r6
            r6 = 1
            r4[r6] = r3
            r6 = 2131626780(0x7f0e0b1c, float:1.8880806E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0cb4:
            r6 = 1
            r3 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0cc6:
            r6 = 1
            r9 = 0
        L_0x0cc8:
            r3 = 2131626744(0x7f0e0af8, float:1.8880733E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            java.lang.String r6 = r5.title
            r4[r9] = r6
            java.lang.String r6 = "NotificationActionPinnedGeoChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0cd8:
            java.lang.String r3 = r11.getStickerEmoji()
            if (r3 == 0) goto L_0x0cf3
            r4 = 2131626776(0x7f0e0b18, float:1.8880798E38)
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
        L_0x0cf3:
            r12 = 0
            r15 = 1
            r4 = 2131626774(0x7f0e0b16, float:1.8880794E38)
            java.lang.Object[] r6 = new java.lang.Object[r15]
            java.lang.String r9 = r5.title
            r6[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedStickerChannel"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0d05:
            r12 = 0
            r15 = 1
            org.telegram.messenger.MessageObject r11 = r0.replyMessageObject
            if (r11 != 0) goto L_0x0d19
            r3 = 2131626760(0x7f0e0b08, float:1.8880765E38)
            java.lang.Object[] r4 = new java.lang.Object[r15]
            r4[r12] = r13
            java.lang.String r6 = "NotificationActionPinnedNoTextUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0d19:
            org.telegram.messenger.MessageObject r11 = r0.replyMessageObject
            boolean r12 = r11.isMusic()
            if (r12 == 0) goto L_0x0d31
            r3 = 2131626757(0x7f0e0b05, float:1.888076E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedMusicUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0d31:
            boolean r12 = r11.isVideo()
            java.lang.String r15 = "NotificationActionPinnedTextUser"
            if (r12 == 0) goto L_0x0d7b
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0d6b
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0d6b
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
            r6 = 2131626781(0x7f0e0b1d, float:1.8880808E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0d6b:
            r6 = 0
            r9 = 1
            r3 = 2131626784(0x7f0e0b20, float:1.8880814E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedVideoUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0d7b:
            boolean r4 = r11.isGif()
            if (r4 == 0) goto L_0x0dc3
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0db3
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0db3
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
            r6 = 2131626781(0x7f0e0b1d, float:1.8880808E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0db3:
            r12 = 0
            r14 = 1
            r3 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            r4[r12] = r13
            java.lang.String r6 = "NotificationActionPinnedGifUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0dc3:
            r12 = 0
            r14 = 1
            boolean r4 = r11.isVoice()
            if (r4 == 0) goto L_0x0dd9
            r3 = 2131626787(0x7f0e0b23, float:1.888082E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            r4[r12] = r13
            java.lang.String r6 = "NotificationActionPinnedVoiceUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0dd9:
            boolean r4 = r11.isRoundVideo()
            if (r4 == 0) goto L_0x0ded
            r3 = 2131626772(0x7f0e0b14, float:1.888079E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            r4[r12] = r13
            java.lang.String r6 = "NotificationActionPinnedRoundUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0ded:
            boolean r4 = r11.isSticker()
            if (r4 != 0) goto L_0x0f9a
            boolean r4 = r11.isAnimatedSticker()
            if (r4 == 0) goto L_0x0dfb
            goto L_0x0f9a
        L_0x0dfb:
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x0e45
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r9) goto L_0x0e35
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0e35
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
            r6 = 2131626781(0x7f0e0b1d, float:1.8880808E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0e35:
            r6 = 0
            r9 = 1
            r3 = 2131626736(0x7f0e0af0, float:1.8880717E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedFileUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0e45:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r3 != 0) goto L_0x0f8a
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r3 == 0) goto L_0x0e59
            r6 = 1
            r9 = 0
            goto L_0x0f8c
        L_0x0e59:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0e71
            r3 = 2131626747(0x7f0e0afb, float:1.8880739E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedGeoLiveUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0e71:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r3 == 0) goto L_0x0e9a
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r3
            r4 = 2131626733(0x7f0e0aed, float:1.888071E38)
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
        L_0x0e9a:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r3 == 0) goto L_0x0edc
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r4 = r3.poll
            boolean r4 = r4.quiz
            if (r4 == 0) goto L_0x0ec5
            r4 = 2131626769(0x7f0e0b11, float:1.8880784E38)
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
        L_0x0ec5:
            r6 = 2
            r9 = 0
            r10 = 1
            r4 = 2131626766(0x7f0e0b0e, float:1.8880777E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r9] = r13
            org.telegram.tgnet.TLRPC$Poll r9 = r3.poll
            java.lang.String r9 = r9.question
            r6[r10] = r9
            java.lang.String r9 = "NotificationActionPinnedPollUser"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0edc:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0var_
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x0var_
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
            r6 = 2131626781(0x7f0e0b1d, float:1.8880808E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0var_:
            r6 = 0
            r9 = 1
            r3 = 2131626763(0x7f0e0b0b, float:1.8880771E38)
            java.lang.Object[] r4 = new java.lang.Object[r9]
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedPhotoUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r3 == 0) goto L_0x0f3e
            r3 = 2131626742(0x7f0e0af6, float:1.8880729E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationActionPinnedGameUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0f3e:
            java.lang.CharSequence r3 = r11.messageText
            if (r3 == 0) goto L_0x0f7a
            java.lang.CharSequence r3 = r11.messageText
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0f7a
            java.lang.CharSequence r3 = r11.messageText
            int r4 = r3.length()
            r6 = 20
            if (r4 <= r6) goto L_0x0var_
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r9 = 0
            java.lang.CharSequence r6 = r3.subSequence(r9, r6)
            r4.append(r6)
            r4.append(r10)
            java.lang.String r3 = r4.toString()
            goto L_0x0f6a
        L_0x0var_:
            r9 = 0
        L_0x0f6a:
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r9] = r13
            r6 = 1
            r4[r6] = r3
            r6 = 2131626781(0x7f0e0b1d, float:1.8880808E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r15, r6, r4)
            return r4
        L_0x0f7a:
            r6 = 1
            r9 = 0
            r3 = 2131626760(0x7f0e0b08, float:1.8880765E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r4[r9] = r13
            java.lang.String r6 = "NotificationActionPinnedNoTextUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0f8a:
            r6 = 1
            r9 = 0
        L_0x0f8c:
            r3 = 2131626748(0x7f0e0afc, float:1.888074E38)
            java.lang.Object[] r4 = new java.lang.Object[r6]
            r4[r9] = r13
            java.lang.String r6 = "NotificationActionPinnedGeoUser"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x0f9a:
            java.lang.String r3 = r11.getStickerEmoji()
            if (r3 == 0) goto L_0x0fb3
            r4 = 2131626777(0x7f0e0b19, float:1.88808E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r13
            r10 = 1
            r6[r10] = r3
            java.lang.String r9 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0fb3:
            r9 = 0
            r10 = 1
            r4 = 2131626778(0x7f0e0b1a, float:1.8880802E38)
            java.lang.Object[] r6 = new java.lang.Object[r10]
            r6[r9] = r13
            java.lang.String r9 = "NotificationActionPinnedStickerUser"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            return r4
        L_0x0fc3:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme
            if (r3 == 0) goto L_0x1026
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r3 = (org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme) r3
            java.lang.String r3 = r3.emoticon
            boolean r4 = android.text.TextUtils.isEmpty(r3)
            if (r4 == 0) goto L_0x0ffe
            int r4 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r4 != 0) goto L_0x0fea
            r4 = 2131624958(0x7f0e03fe, float:1.887711E38)
            r6 = 0
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = "ChatThemeDisabledYou"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            goto L_0x0ffc
        L_0x0fea:
            r6 = 0
            r4 = 2131624957(0x7f0e03fd, float:1.8877108E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r6] = r13
            r6 = 1
            r9[r6] = r3
            java.lang.String r6 = "ChatThemeDisabled"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r4, r9)
        L_0x0ffc:
            goto L_0x1025
        L_0x0ffe:
            int r4 = (r1 > r21 ? 1 : (r1 == r21 ? 0 : -1))
            if (r4 != 0) goto L_0x1012
            r4 = 2131624955(0x7f0e03fb, float:1.8877104E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r9 = 0
            r6[r9] = r3
            java.lang.String r9 = "ChangedChatThemeYou"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r6)
            goto L_0x1024
        L_0x1012:
            r6 = 1
            r9 = 0
            r4 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            r10 = 2
            java.lang.Object[] r10 = new java.lang.Object[r10]
            r10[r9] = r13
            r10[r6] = r3
            java.lang.String r6 = "ChangedChatThemeTo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r6, r4, r10)
        L_0x1024:
        L_0x1025:
            return r4
        L_0x1026:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByRequest
            if (r3 == 0) goto L_0x1035
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x1035:
            r3 = 0
            return r3
        L_0x1037:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            long r3 = r3.channel_id
            r9 = 0
            int r6 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r6 == 0) goto L_0x1071
            boolean r3 = r5.megagroup
            if (r3 != 0) goto L_0x1071
            boolean r3 = r29.isVideoAvatar()
            if (r3 == 0) goto L_0x105f
            r3 = 2131624907(0x7f0e03cb, float:1.8877007E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r9 = 0
            r4[r9] = r6
            java.lang.String r6 = "ChannelVideoEditNotification"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x105f:
            r4 = 1
            r9 = 0
            r3 = 2131624872(0x7f0e03a8, float:1.8876936E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r6 = r5.title
            r4[r9] = r6
            java.lang.String r6 = "ChannelPhotoEditNotification"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x1071:
            boolean r3 = r29.isVideoAvatar()
            if (r3 == 0) goto L_0x108c
            r3 = 2131626792(0x7f0e0b28, float:1.888083E38)
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
        L_0x108c:
            r4 = 2
            r6 = 0
            r9 = 1
            r3 = 2131626791(0x7f0e0b27, float:1.8880828E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r6] = r13
            java.lang.String r6 = r5.title
            r4[r9] = r6
            java.lang.String r6 = "NotificationEditedGroupPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x10a1:
            r26 = r11
            r27 = r12
        L_0x10a5:
            java.lang.CharSequence r3 = r0.messageText
            java.lang.String r3 = r3.toString()
            return r3
        L_0x10ac:
            r26 = r11
            r27 = r12
        L_0x10b0:
            r3 = 2131626788(0x7f0e0b24, float:1.8880822E38)
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.String r6 = "NotificationContactJoined"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r6, r3, r4)
            return r3
        L_0x10c0:
            r26 = r11
            r27 = r12
            boolean r10 = r29.isMediaEmpty()
            if (r10 == 0) goto L_0x10e1
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x10d9
            java.lang.String r3 = r28.replaceSpoilers(r29)
            return r3
        L_0x10d9:
            r3 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            return r3
        L_0x10e1:
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r10.media
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r10 == 0) goto L_0x1127
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x110b
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x110b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r6)
            java.lang.String r4 = r28.replaceSpoilers(r29)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            return r3
        L_0x110b:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            int r3 = r3.ttl_seconds
            if (r3 == 0) goto L_0x111d
            r3 = 2131624420(0x7f0e01e4, float:1.887602E38)
            java.lang.String r4 = "AttachDestructingPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x111d:
            r3 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.String r4 = "AttachPhoto"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x1127:
            boolean r6 = r29.isVideo()
            if (r6 == 0) goto L_0x116b
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x114f
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x114f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            java.lang.String r4 = r28.replaceSpoilers(r29)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            return r3
        L_0x114f:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            int r3 = r3.ttl_seconds
            if (r3 == 0) goto L_0x1161
            r3 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r4 = "AttachDestructingVideo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x1161:
            r3 = 2131624449(0x7f0e0201, float:1.8876078E38)
            java.lang.String r4 = "AttachVideo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x116b:
            boolean r4 = r29.isGame()
            if (r4 == 0) goto L_0x117b
            r3 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r4 = "AttachGame"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x117b:
            boolean r4 = r29.isVoice()
            if (r4 == 0) goto L_0x118b
            r3 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.String r4 = "AttachAudio"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x118b:
            boolean r4 = r29.isRoundVideo()
            if (r4 == 0) goto L_0x119b
            r3 = 2131624445(0x7f0e01fd, float:1.887607E38)
            java.lang.String r4 = "AttachRound"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x119b:
            boolean r4 = r29.isMusic()
            if (r4 == 0) goto L_0x11ab
            r3 = 2131624442(0x7f0e01fa, float:1.8876064E38)
            java.lang.String r4 = "AttachMusic"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x11ab:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r4 == 0) goto L_0x11bd
            r3 = 2131624419(0x7f0e01e3, float:1.8876017E38)
            java.lang.String r4 = "AttachContact"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x11bd:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r4 == 0) goto L_0x11e5
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r3 = r3.poll
            boolean r3 = r3.quiz
            if (r3 == 0) goto L_0x11db
            r3 = 2131627630(0x7f0e0e6e, float:1.888253E38)
            java.lang.String r4 = "QuizPoll"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x11db:
            r3 = 2131627414(0x7f0e0d96, float:1.8882092E38)
            java.lang.String r4 = "Poll"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x11e5:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r4 != 0) goto L_0x12bf
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r4 == 0) goto L_0x11f7
            goto L_0x12bf
        L_0x11f7:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x1209
            r3 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r4 = "AttachLiveLocation"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x1209:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x12aa
            boolean r4 = r29.isSticker()
            if (r4 != 0) goto L_0x127c
            boolean r4 = r29.isAnimatedSticker()
            if (r4 == 0) goto L_0x121e
            goto L_0x127c
        L_0x121e:
            boolean r4 = r29.isGif()
            if (r4 == 0) goto L_0x1250
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r9) goto L_0x1246
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x1246
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r14)
            java.lang.String r4 = r28.replaceSpoilers(r29)
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            return r3
        L_0x1246:
            r3 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r4 = "AttachGif"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x1250:
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r9) goto L_0x1272
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x1272
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r3 = r28.replaceSpoilers(r29)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            return r3
        L_0x1272:
            r3 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r4 = "AttachDocument"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x127c:
            java.lang.String r3 = r29.getStickerEmoji()
            if (r3 == 0) goto L_0x12a0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            java.lang.String r6 = " "
            r4.append(r6)
            r6 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r9 = "AttachSticker"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r4.append(r6)
            java.lang.String r4 = r4.toString()
            return r4
        L_0x12a0:
            r4 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r6 = "AttachSticker"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            return r4
        L_0x12aa:
            java.lang.CharSequence r3 = r0.messageText
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x12b7
            java.lang.String r3 = r28.replaceSpoilers(r29)
            return r3
        L_0x12b7:
            r3 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            return r3
        L_0x12bf:
            r3 = 2131624433(0x7f0e01f1, float:1.8876046E38)
            java.lang.String r4 = "AttachLocation"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            return r3
        L_0x12c9:
            r23 = r3
            r17 = r6
            r25 = r9
            r26 = r11
            r27 = r12
            if (r31 == 0) goto L_0x12d8
            r3 = 0
            r31[r3] = r3
        L_0x12d8:
            r3 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            return r3
        L_0x12e0:
            r1 = 2131626808(0x7f0e0b38, float:1.8880863E38)
            java.lang.String r2 = "NotificationHiddenMessage"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    private String replaceSpoilers(MessageObject messageObject) {
        String text = messageObject.messageOwner.message;
        if (text == null || messageObject == null || messageObject.messageOwner == null || messageObject.messageOwner.entities == null) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(text);
        for (int i = 0; i < messageObject.messageOwner.entities.size(); i++) {
            if (messageObject.messageOwner.entities.get(i) instanceof TLRPC.TL_messageEntitySpoiler) {
                TLRPC.TL_messageEntitySpoiler spoiler = (TLRPC.TL_messageEntitySpoiler) messageObject.messageOwner.entities.get(i);
                for (int j = 0; j < spoiler.length; j++) {
                    char[] cArr = this.spoilerChars;
                    stringBuilder.setCharAt(spoiler.offset + j, cArr[j % cArr.length]);
                }
            }
        }
        return stringBuilder.toString();
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
            if (r1 != 0) goto L_0x1920
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x1920
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
            r5 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
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
            r5 = 2131624856(0x7f0e0398, float:1.8876904E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r9 = r0.localName
            r10 = 0
            r6[r10] = r9
            java.lang.String r9 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r5, r6)
            return r5
        L_0x00d2:
            r5 = 2131626831(0x7f0e0b4f, float:1.888091E38)
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
            r9 = 2131626494(0x7f0e09fe, float:1.8880226E38)
            java.lang.String r5 = "MessageScheduledReminderNotification"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r5, r9)
            r6 = r14
            goto L_0x016d
        L_0x0139:
            r5 = 2131626852(0x7f0e0b64, float:1.8880952E38)
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
            r6 = 2131629035(0x7f0e13eb, float:1.888538E38)
            java.lang.String r14 = "YouHaveNewMessage"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r14, r6)
            r24 = r3
            r28 = r7
            r8 = r9
            r16 = r12
            goto L_0x191f
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
            if (r27 != 0) goto L_0x079c
            int r27 = (r7 > r20 ? 1 : (r7 == r20 ? 0 : -1))
            if (r27 == 0) goto L_0x079c
            if (r12 == 0) goto L_0x077d
            java.lang.String r14 = "EnablePreviewAll"
            r28 = r7
            r7 = r18
            r8 = 1
            boolean r14 = r7.getBoolean(r14, r8)
            if (r14 == 0) goto L_0x077b
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r8 == 0) goto L_0x037f
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGeoProximityReached
            if (r5 == 0) goto L_0x01ec
            java.lang.CharSequence r5 = r0.messageText
            java.lang.String r5 = r5.toString()
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x01ec:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserJoined
            if (r5 != 0) goto L_0x0366
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp
            if (r5 == 0) goto L_0x01fe
            goto L_0x0366
        L_0x01fe:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto
            if (r5 == 0) goto L_0x021f
            r5 = 2131626789(0x7f0e0b25, float:1.8880824E38)
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
            goto L_0x191f
        L_0x021f:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation
            if (r5 == 0) goto L_0x0294
            r5 = 2131629114(0x7f0e143a, float:1.888554E38)
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
            r6 = 2131626858(0x7f0e0b6a, float:1.8880964E38)
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
            goto L_0x191f
        L_0x0294:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore
            if (r5 != 0) goto L_0x0356
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent
            if (r5 == 0) goto L_0x02a6
            goto L_0x0356
        L_0x02a6:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall
            if (r5 == 0) goto L_0x02dc
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5.video
            if (r5 == 0) goto L_0x02c9
            r5 = 2131624728(0x7f0e0318, float:1.8876644E38)
            java.lang.String r6 = "CallMessageVideoIncomingMissed"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x02c9:
            r5 = 2131624722(0x7f0e0312, float:1.8876632E38)
            java.lang.String r6 = "CallMessageIncomingMissed"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x02dc:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme
            if (r5 == 0) goto L_0x034c
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r5 = (org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme) r5
            java.lang.String r5 = r5.emoticon
            boolean r6 = android.text.TextUtils.isEmpty(r5)
            if (r6 == 0) goto L_0x0318
            int r6 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x0303
            r6 = 2131624958(0x7f0e03fe, float:1.887711E38)
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]
            java.lang.String r14 = "ChatThemeDisabledYou"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r14, r6, r9)
            goto L_0x0315
        L_0x0303:
            r8 = 0
            r6 = 2131624957(0x7f0e03fd, float:1.8877108E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r8] = r13
            r8 = 1
            r9[r8] = r5
            java.lang.String r8 = "ChatThemeDisabled"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r8, r6, r9)
        L_0x0315:
            r8 = 1
            r14 = 0
            goto L_0x033f
        L_0x0318:
            int r6 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x032c
            r6 = 2131624955(0x7f0e03fb, float:1.8877104E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r14 = 0
            r9[r14] = r5
            java.lang.String r15 = "ChangedChatThemeYou"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r15, r6, r9)
            goto L_0x033e
        L_0x032c:
            r8 = 1
            r14 = 0
            r6 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r14] = r13
            r9[r8] = r5
            java.lang.String r15 = "ChangedChatThemeTo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r15, r6, r9)
        L_0x033e:
        L_0x033f:
            r33[r14] = r8
            r5 = r6
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x034c:
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191d
        L_0x0356:
            java.lang.CharSequence r5 = r0.messageText
            java.lang.String r5 = r5.toString()
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x0366:
            r5 = 2131626788(0x7f0e0b24, float:1.8880822E38)
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
            goto L_0x191f
        L_0x037f:
            boolean r8 = r31.isMediaEmpty()
            if (r8 == 0) goto L_0x03df
            if (r32 != 0) goto L_0x03c8
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x03b1
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            java.lang.String r9 = r9.message
            r14 = 1
            r6[r14] = r9
            r9 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x03b1:
            r8 = 0
            r14 = 1
            java.lang.Object[] r5 = new java.lang.Object[r14]
            r5[r8] = r13
            r6 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r15, r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x03c8:
            r6 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            r8 = 0
            r14 = 1
            java.lang.Object[] r5 = new java.lang.Object[r14]
            r5[r8] = r13
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r15, r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x03df:
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r8 == 0) goto L_0x0462
            if (r32 != 0) goto L_0x0428
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 19
            if (r6 < r8) goto L_0x0428
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x0428
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
            r9 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x0428:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            int r5 = r5.ttl_seconds
            if (r5 == 0) goto L_0x0449
            r5 = 2131626849(0x7f0e0b61, float:1.8880946E38)
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
            goto L_0x191f
        L_0x0449:
            r6 = 1
            r8 = 0
            r5 = 2131626845(0x7f0e0b5d, float:1.8880938E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessagePhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x0462:
            boolean r8 = r31.isVideo()
            if (r8 == 0) goto L_0x04e3
            if (r32 != 0) goto L_0x04a9
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 19
            if (r8 < r9) goto L_0x04a9
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            java.lang.String r8 = r8.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 != 0) goto L_0x04a9
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
            r6 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r8)
            r33[r9] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x04a9:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            int r5 = r5.ttl_seconds
            if (r5 == 0) goto L_0x04ca
            r5 = 2131626850(0x7f0e0b62, float:1.8880948E38)
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
            goto L_0x191f
        L_0x04ca:
            r6 = 1
            r8 = 0
            r5 = 2131626856(0x7f0e0b68, float:1.888096E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageVideo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x04e3:
            r8 = 0
            boolean r6 = r31.isGame()
            if (r6 == 0) goto L_0x050d
            r5 = 2131626818(0x7f0e0b42, float:1.8880883E38)
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
            goto L_0x191f
        L_0x050d:
            r9 = 1
            boolean r6 = r31.isVoice()
            if (r6 == 0) goto L_0x052c
            r5 = 2131626813(0x7f0e0b3d, float:1.8880873E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            r8 = 0
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageAudio"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x052c:
            r8 = 0
            boolean r6 = r31.isRoundVideo()
            if (r6 == 0) goto L_0x054a
            r5 = 2131626848(0x7f0e0b60, float:1.8880944E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageRound"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x054a:
            boolean r6 = r31.isMusic()
            if (r6 == 0) goto L_0x0567
            r5 = 2131626843(0x7f0e0b5b, float:1.8880934E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageMusic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x0567:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r6 == 0) goto L_0x0599
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5
            r6 = 2131626814(0x7f0e0b3e, float:1.8880875E38)
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
            goto L_0x191f
        L_0x0599:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r6 == 0) goto L_0x05e6
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r6 = r5.poll
            boolean r6 = r6.quiz
            if (r6 == 0) goto L_0x05c5
            r6 = 2131626847(0x7f0e0b5f, float:1.8880942E38)
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
            goto L_0x05dc
        L_0x05c5:
            r8 = 2
            r9 = 0
            r14 = 1
            r6 = 2131626846(0x7f0e0b5e, float:1.888094E38)
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r8[r9] = r13
            org.telegram.tgnet.TLRPC$Poll r9 = r5.poll
            java.lang.String r9 = r9.question
            r8[r14] = r9
            java.lang.String r9 = "NotificationMessagePoll2"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r8)
            r5 = r6
        L_0x05dc:
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x05e6:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r6 != 0) goto L_0x0762
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r6 == 0) goto L_0x05fa
            r8 = 0
            r14 = 1
            goto L_0x0764
        L_0x05fa:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x061b
            r5 = 2131626841(0x7f0e0b59, float:1.888093E38)
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
            goto L_0x191f
        L_0x061b:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r6 == 0) goto L_0x0723
            boolean r6 = r31.isSticker()
            if (r6 != 0) goto L_0x06ef
            boolean r6 = r31.isAnimatedSticker()
            if (r6 == 0) goto L_0x0631
            goto L_0x06ef
        L_0x0631:
            boolean r6 = r31.isGif()
            if (r6 == 0) goto L_0x0693
            if (r32 != 0) goto L_0x067a
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 19
            if (r6 < r8) goto L_0x067a
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x067a
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
            r9 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x067a:
            r8 = 0
            r14 = 1
            r5 = 2131626820(0x7f0e0b44, float:1.8880887E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageGif"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x0693:
            if (r32 != 0) goto L_0x06d6
            int r6 = android.os.Build.VERSION.SDK_INT
            r8 = 19
            if (r6 < r8) goto L_0x06d6
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.String r6 = r6.message
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x06d6
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
            r9 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x06d6:
            r8 = 0
            r14 = 1
            r5 = 2131626815(0x7f0e0b3f, float:1.8880877E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageDocument"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x06ef:
            java.lang.String r5 = r31.getStickerEmoji()
            if (r5 == 0) goto L_0x0709
            r6 = 2131626854(0x7f0e0b66, float:1.8880956E38)
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]
            r9 = 0
            r8[r9] = r13
            r14 = 1
            r8[r14] = r5
            java.lang.String r9 = "NotificationMessageStickerEmoji"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r8)
            r5 = r6
            goto L_0x0719
        L_0x0709:
            r9 = 0
            r14 = 1
            r6 = 2131626853(0x7f0e0b65, float:1.8880954E38)
            java.lang.Object[] r8 = new java.lang.Object[r14]
            r8[r9] = r13
            java.lang.String r9 = "NotificationMessageSticker"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r8)
            r5 = r6
        L_0x0719:
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x0723:
            if (r32 != 0) goto L_0x074b
            java.lang.CharSequence r6 = r0.messageText
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x074b
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r8 = 0
            r6[r8] = r13
            java.lang.CharSequence r9 = r0.messageText
            r14 = 1
            r6[r14] = r9
            r9 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r9, r6)
            r33[r8] = r14
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x074b:
            r8 = 0
            r14 = 1
            java.lang.Object[] r5 = new java.lang.Object[r14]
            r5[r8] = r13
            r6 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r15, r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x0762:
            r8 = 0
            r14 = 1
        L_0x0764:
            r5 = 2131626842(0x7f0e0b5a, float:1.8880932E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r6[r8] = r13
            java.lang.String r8 = "NotificationMessageMap"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r5, r6)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x077b:
            r8 = 0
            goto L_0x0782
        L_0x077d:
            r28 = r7
            r7 = r18
            r8 = 0
        L_0x0782:
            if (r34 == 0) goto L_0x0786
            r34[r8] = r8
        L_0x0786:
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r8] = r13
            r6 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r15, r6, r5)
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
            goto L_0x191f
        L_0x079c:
            r28 = r7
            r7 = r18
            r15 = r25
            r14 = r26
            r20 = 0
            int r8 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1))
            if (r8 == 0) goto L_0x1915
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r24)
            if (r8 == 0) goto L_0x07ba
            r8 = r24
            r24 = r3
            boolean r3 = r8.megagroup
            if (r3 != 0) goto L_0x07be
            r3 = 1
            goto L_0x07bf
        L_0x07ba:
            r8 = r24
            r24 = r3
        L_0x07be:
            r3 = 0
        L_0x07bf:
            if (r12 == 0) goto L_0x18dd
            if (r3 != 0) goto L_0x07cf
            java.lang.String r4 = "EnablePreviewGroup"
            r16 = r12
            r12 = 1
            boolean r4 = r7.getBoolean(r4, r12)
            if (r4 != 0) goto L_0x07dc
            goto L_0x07d1
        L_0x07cf:
            r16 = r12
        L_0x07d1:
            if (r3 == 0) goto L_0x18d8
            java.lang.String r4 = "EnablePreviewChannel"
            r12 = 1
            boolean r4 = r7.getBoolean(r4, r12)
            if (r4 == 0) goto L_0x18d8
        L_0x07dc:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageService
            if (r4 == 0) goto L_0x12a3
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser
            if (r4 == 0) goto L_0x091a
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r4 = r4.user_id
            r14 = 0
            int r6 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r6 != 0) goto L_0x0814
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            int r6 = r6.size()
            r9 = 1
            if (r6 != r9) goto L_0x0814
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            r9 = 0
            java.lang.Object r6 = r6.get(r9)
            java.lang.Long r6 = (java.lang.Long) r6
            long r4 = r6.longValue()
        L_0x0814:
            r14 = 0
            int r6 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r6 == 0) goto L_0x08bd
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            r18 = r7
            long r6 = r6.channel_id
            int r9 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
            if (r9 == 0) goto L_0x0841
            boolean r6 = r8.megagroup
            if (r6 != 0) goto L_0x0841
            r6 = 2131624805(0x7f0e0365, float:1.88768E38)
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
            goto L_0x0918
        L_0x0841:
            int r6 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x085c
            r6 = 2131626810(0x7f0e0b3a, float:1.8880867E38)
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
            goto L_0x0918
        L_0x085c:
            org.telegram.messenger.MessagesController r6 = r30.getMessagesController()
            java.lang.Long r7 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 != 0) goto L_0x086c
            r7 = 0
            return r7
        L_0x086c:
            long r14 = r6.id
            int r7 = (r28 > r14 ? 1 : (r28 == r14 ? 0 : -1))
            if (r7 != 0) goto L_0x08a0
            boolean r7 = r8.megagroup
            if (r7 == 0) goto L_0x088b
            r7 = 2131626795(0x7f0e0b2b, float:1.8880836E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r12 = 0
            r9[r12] = r13
            java.lang.String r12 = r8.title
            r14 = 1
            r9[r14] = r12
            java.lang.String r12 = "NotificationGroupAddSelfMega"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r12, r7, r9)
            goto L_0x08bb
        L_0x088b:
            r9 = 2
            r12 = 0
            r14 = 1
            r7 = 2131626794(0x7f0e0b2a, float:1.8880834E38)
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r12] = r13
            java.lang.String r12 = r8.title
            r9[r14] = r12
            java.lang.String r12 = "NotificationGroupAddSelf"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r12, r7, r9)
            goto L_0x08bb
        L_0x08a0:
            r12 = 0
            r14 = 1
            r7 = 2131626793(0x7f0e0b29, float:1.8880832E38)
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
        L_0x08bb:
            r5 = r7
            goto L_0x0918
        L_0x08bd:
            r18 = r7
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r7 = 0
        L_0x08c5:
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            java.util.ArrayList<java.lang.Long> r9 = r9.users
            int r9 = r9.size()
            if (r7 >= r9) goto L_0x08fc
            org.telegram.messenger.MessagesController r9 = r30.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r12 = r12.action
            java.util.ArrayList<java.lang.Long> r12 = r12.users
            java.lang.Object r12 = r12.get(r7)
            java.lang.Long r12 = (java.lang.Long) r12
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r12)
            if (r9 == 0) goto L_0x08f9
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r9)
            int r14 = r6.length()
            if (r14 == 0) goto L_0x08f6
            java.lang.String r14 = ", "
            r6.append(r14)
        L_0x08f6:
            r6.append(r12)
        L_0x08f9:
            int r7 = r7 + 1
            goto L_0x08c5
        L_0x08fc:
            r7 = 2131626793(0x7f0e0b29, float:1.8880832E38)
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
        L_0x0918:
            goto L_0x191f
        L_0x091a:
            r18 = r7
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGroupCall
            if (r4 == 0) goto L_0x093a
            r4 = 2131626797(0x7f0e0b2d, float:1.888084E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationGroupCreatedCall"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x093a:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGroupCallScheduled
            if (r4 == 0) goto L_0x094a
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x191f
        L_0x094a:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionInviteToGroupCall
            if (r4 == 0) goto L_0x0a25
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r4 = r4.user_id
            r6 = 0
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 != 0) goto L_0x097c
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            int r6 = r6.size()
            r7 = 1
            if (r6 != r7) goto L_0x097c
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            r7 = 0
            java.lang.Object r6 = r6.get(r7)
            java.lang.Long r6 = (java.lang.Long) r6
            long r4 = r6.longValue()
        L_0x097c:
            r6 = 0
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 == 0) goto L_0x09ca
            int r6 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x099d
            r6 = 2131626802(0x7f0e0b32, float:1.888085E38)
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
            goto L_0x0a23
        L_0x099d:
            org.telegram.messenger.MessagesController r6 = r30.getMessagesController()
            java.lang.Long r7 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 != 0) goto L_0x09ad
            r7 = 0
            return r7
        L_0x09ad:
            r7 = 2131626801(0x7f0e0b31, float:1.8880848E38)
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
            goto L_0x0a23
        L_0x09ca:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r7 = 0
        L_0x09d0:
            org.telegram.tgnet.TLRPC$Message r9 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r9 = r9.action
            java.util.ArrayList<java.lang.Long> r9 = r9.users
            int r9 = r9.size()
            if (r7 >= r9) goto L_0x0a07
            org.telegram.messenger.MessagesController r9 = r30.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r12 = r12.action
            java.util.ArrayList<java.lang.Long> r12 = r12.users
            java.lang.Object r12 = r12.get(r7)
            java.lang.Long r12 = (java.lang.Long) r12
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r12)
            if (r9 == 0) goto L_0x0a04
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r9)
            int r14 = r6.length()
            if (r14 == 0) goto L_0x0a01
            java.lang.String r14 = ", "
            r6.append(r14)
        L_0x0a01:
            r6.append(r12)
        L_0x0a04:
            int r7 = r7 + 1
            goto L_0x09d0
        L_0x0a07:
            r7 = 2131626801(0x7f0e0b31, float:1.8880848E38)
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
        L_0x0a23:
            goto L_0x191f
        L_0x0a25:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink
            if (r4 == 0) goto L_0x0a43
            r4 = 2131626811(0x7f0e0b3b, float:1.8880869E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationInvitedToGroupByLink"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x0a43:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle
            if (r4 == 0) goto L_0x0a65
            r4 = 2131626790(0x7f0e0b26, float:1.8880826E38)
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
            goto L_0x191f
        L_0x0a65:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto
            if (r4 != 0) goto L_0x1235
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto
            if (r4 == 0) goto L_0x0a77
            goto L_0x1235
        L_0x0a77:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser
            if (r4 == 0) goto L_0x0af2
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r4 = r4.user_id
            int r6 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x0a9f
            r4 = 2131626804(0x7f0e0b34, float:1.8880855E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationGroupKickYou"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x0a9f:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r4 = r4.user_id
            int r6 = (r4 > r28 ? 1 : (r4 == r28 ? 0 : -1))
            if (r6 != 0) goto L_0x0abf
            r4 = 2131626805(0x7f0e0b35, float:1.8880857E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationGroupLeftMember"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x0abf:
            org.telegram.messenger.MessagesController r4 = r30.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            long r5 = r5.user_id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 != 0) goto L_0x0ad5
            r5 = 0
            return r5
        L_0x0ad5:
            r5 = 2131626803(0x7f0e0b33, float:1.8880853E38)
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
            goto L_0x191f
        L_0x0af2:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatCreate
            if (r4 == 0) goto L_0x0b02
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x191f
        L_0x0b02:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x0b12
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x191f
        L_0x0b12:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo
            if (r4 == 0) goto L_0x0b2d
            r4 = 2131624149(0x7f0e00d5, float:1.887547E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = r8.title
            r7 = 0
            r5[r7] = r6
            java.lang.String r6 = "ActionMigrateFromGroupNotify"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x0b2d:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
            if (r4 == 0) goto L_0x0b4c
            r4 = 2131624149(0x7f0e00d5, float:1.887547E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.lang.String r6 = r6.title
            r7 = 0
            r5[r7] = r6
            java.lang.String r6 = "ActionMigrateFromGroupNotify"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x0b4c:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken
            if (r4 == 0) goto L_0x0b5c
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x191f
        L_0x0b5c:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionPinMessage
            if (r4 == 0) goto L_0x11b1
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r4 == 0) goto L_0x0e6f
            boolean r4 = r8.megagroup
            if (r4 == 0) goto L_0x0b70
            goto L_0x0e6f
        L_0x0b70:
            org.telegram.messenger.MessageObject r4 = r0.replyMessageObject
            if (r4 != 0) goto L_0x0b87
            r4 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = r8.title
            r7 = 0
            r5[r7] = r6
            java.lang.String r6 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x0b87:
            org.telegram.messenger.MessageObject r4 = r0.replyMessageObject
            boolean r5 = r4.isMusic()
            if (r5 == 0) goto L_0x0ba2
            r5 = 2131626756(0x7f0e0b04, float:1.8880757E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedMusicChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0ba2:
            boolean r5 = r4.isVideo()
            r7 = 2131626780(0x7f0e0b1c, float:1.8880806E38)
            java.lang.String r12 = "NotificationActionPinnedTextChannel"
            if (r5 == 0) goto L_0x0bf4
            int r5 = android.os.Build.VERSION.SDK_INT
            r9 = 19
            if (r5 < r9) goto L_0x0be1
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0be1
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
            goto L_0x0e6d
        L_0x0be1:
            r9 = 1
            r5 = 2131626783(0x7f0e0b1f, float:1.8880812E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedVideoChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0bf4:
            boolean r5 = r4.isGif()
            if (r5 == 0) goto L_0x0CLASSNAME
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0c2e
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0c2e
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
            goto L_0x0e6d
        L_0x0c2e:
            r14 = 1
            r5 = 2131626750(0x7f0e0afe, float:1.8880745E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            java.lang.String r7 = r8.title
            r17 = 0
            r6[r17] = r7
            java.lang.String r7 = "NotificationActionPinnedGifChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0CLASSNAME:
            r14 = 1
            r17 = 0
            boolean r5 = r4.isVoice()
            if (r5 == 0) goto L_0x0c5c
            r5 = 2131626786(0x7f0e0b22, float:1.8880818E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            java.lang.String r7 = r8.title
            r6[r17] = r7
            java.lang.String r7 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0c5c:
            boolean r5 = r4.isRoundVideo()
            if (r5 == 0) goto L_0x0CLASSNAME
            r5 = 2131626771(0x7f0e0b13, float:1.8880788E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            java.lang.String r7 = r8.title
            r6[r17] = r7
            java.lang.String r7 = "NotificationActionPinnedRoundChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0CLASSNAME:
            boolean r5 = r4.isSticker()
            if (r5 != 0) goto L_0x0e40
            boolean r5 = r4.isAnimatedSticker()
            if (r5 == 0) goto L_0x0CLASSNAME
            goto L_0x0e40
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r5 == 0) goto L_0x0cd0
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0cbd
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0cbd
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
            goto L_0x0e6d
        L_0x0cbd:
            r9 = 1
            r5 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedFileChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0cd0:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r5 != 0) goto L_0x0e2e
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r5 == 0) goto L_0x0ce4
            r9 = 1
            r12 = 0
            goto L_0x0e30
        L_0x0ce4:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0cff
            r5 = 2131626746(0x7f0e0afa, float:1.8880737E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0cff:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r5 == 0) goto L_0x0d2b
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5
            r6 = 2131626732(0x7f0e0aec, float:1.8880709E38)
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
            goto L_0x0e6d
        L_0x0d2b:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r5 == 0) goto L_0x0d73
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r6 = r5.poll
            boolean r6 = r6.quiz
            if (r6 == 0) goto L_0x0d58
            r6 = 2131626768(0x7f0e0b10, float:1.8880782E38)
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
            goto L_0x0d70
        L_0x0d58:
            r7 = 2
            r12 = 0
            r14 = 1
            r6 = 2131626765(0x7f0e0b0d, float:1.8880775E38)
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r9 = r8.title
            r7[r12] = r9
            org.telegram.tgnet.TLRPC$Poll r9 = r5.poll
            java.lang.String r9 = r9.question
            r7[r14] = r9
            java.lang.String r9 = "NotificationActionPinnedPollChannel2"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
        L_0x0d70:
            r5 = r6
            goto L_0x0e6d
        L_0x0d73:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0dc2
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0daf
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0daf
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
            goto L_0x0e6d
        L_0x0daf:
            r9 = 1
            r5 = 2131626762(0x7f0e0b0a, float:1.888077E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0dc2:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r5 == 0) goto L_0x0ddd
            r5 = 2131626738(0x7f0e0af2, float:1.888072E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r7 = r8.title
            r9 = 0
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGameChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0ddd:
            java.lang.CharSequence r5 = r4.messageText
            if (r5 == 0) goto L_0x0e1c
            java.lang.CharSequence r5 = r4.messageText
            int r5 = r5.length()
            if (r5 <= 0) goto L_0x0e1c
            java.lang.CharSequence r5 = r4.messageText
            int r6 = r5.length()
            r9 = 20
            if (r6 <= r9) goto L_0x0e0c
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r9 = 20
            r14 = 0
            java.lang.CharSequence r9 = r5.subSequence(r14, r9)
            r6.append(r9)
            java.lang.String r9 = "..."
            r6.append(r9)
            java.lang.String r5 = r6.toString()
            goto L_0x0e0d
        L_0x0e0c:
            r14 = 0
        L_0x0e0d:
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r9 = r8.title
            r6[r14] = r9
            r9 = 1
            r6[r9] = r5
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x0e6d
        L_0x0e1c:
            r9 = 1
            r5 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r12 = 0
            r6[r12] = r7
            java.lang.String r7 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0e2e:
            r9 = 1
            r12 = 0
        L_0x0e30:
            r5 = 2131626744(0x7f0e0af8, float:1.8880733E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            java.lang.String r7 = r8.title
            r6[r12] = r7
            java.lang.String r7 = "NotificationActionPinnedGeoChannel"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x0e6d
        L_0x0e40:
            java.lang.String r5 = r4.getStickerEmoji()
            if (r5 == 0) goto L_0x0e5b
            r6 = 2131626776(0x7f0e0b18, float:1.8880798E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.String r9 = r8.title
            r12 = 0
            r7[r12] = r9
            r9 = 1
            r7[r9] = r5
            java.lang.String r9 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
            goto L_0x0e6c
        L_0x0e5b:
            r9 = 1
            r12 = 0
            r6 = 2131626774(0x7f0e0b16, float:1.8880794E38)
            java.lang.Object[] r7 = new java.lang.Object[r9]
            java.lang.String r9 = r8.title
            r7[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedStickerChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
        L_0x0e6c:
            r5 = r6
        L_0x0e6d:
            goto L_0x191f
        L_0x0e6f:
            org.telegram.messenger.MessageObject r4 = r0.replyMessageObject
            if (r4 != 0) goto L_0x0e89
            r4 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationActionPinnedNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x0e89:
            org.telegram.messenger.MessageObject r4 = r0.replyMessageObject
            boolean r5 = r4.isMusic()
            if (r5 == 0) goto L_0x0ea7
            r5 = 2131626755(0x7f0e0b03, float:1.8880755E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedMusic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x0ea7:
            boolean r5 = r4.isVideo()
            r7 = 2131626779(0x7f0e0b1b, float:1.8880804E38)
            java.lang.String r12 = "NotificationActionPinnedText"
            if (r5 == 0) goto L_0x0eff
            int r5 = android.os.Build.VERSION.SDK_INT
            r9 = 19
            if (r5 < r9) goto L_0x0ee9
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0ee9
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
            goto L_0x11af
        L_0x0ee9:
            r14 = 2
            r5 = 2131626782(0x7f0e0b1e, float:1.888081E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedVideo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x0eff:
            boolean r5 = r4.isGif()
            if (r5 == 0) goto L_0x0var_
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0f3c
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0f3c
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
            goto L_0x11af
        L_0x0f3c:
            r14 = 2
            r5 = 2131626749(0x7f0e0afd, float:1.8880743E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGif"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x0var_:
            boolean r5 = r4.isVoice()
            if (r5 == 0) goto L_0x0f6e
            r5 = 2131626785(0x7f0e0b21, float:1.8880816E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedVoice"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x0f6e:
            boolean r5 = r4.isRoundVideo()
            if (r5 == 0) goto L_0x0f8a
            r5 = 2131626770(0x7f0e0b12, float:1.8880786E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedRound"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x0f8a:
            boolean r5 = r4.isSticker()
            if (r5 != 0) goto L_0x117c
            boolean r5 = r4.isAnimatedSticker()
            if (r5 == 0) goto L_0x0var_
            goto L_0x117c
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r5 == 0) goto L_0x0fed
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x0fd7
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x0fd7
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
            goto L_0x11af
        L_0x0fd7:
            r14 = 2
            r5 = 2131626734(0x7f0e0aee, float:1.8880713E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedFile"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x0fed:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r5 != 0) goto L_0x1167
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r5 == 0) goto L_0x1002
            r7 = 0
            r9 = 1
            r14 = 2
            goto L_0x116a
        L_0x1002:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x1020
            r5 = 2131626745(0x7f0e0af9, float:1.8880735E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGeoLive"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x1020:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r5 == 0) goto L_0x104f
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r5
            r6 = 2131626731(0x7f0e0aeb, float:1.8880706E38)
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
            goto L_0x11af
        L_0x104f:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r5 == 0) goto L_0x109d
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r6 = r5.poll
            boolean r6 = r6.quiz
            if (r6 == 0) goto L_0x107f
            r6 = 2131626767(0x7f0e0b0f, float:1.888078E38)
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
            goto L_0x109a
        L_0x107f:
            r6 = 2131626764(0x7f0e0b0c, float:1.8880773E38)
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
        L_0x109a:
            r5 = r6
            goto L_0x11af
        L_0x109d:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r5 == 0) goto L_0x10f2
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r5 < r6) goto L_0x10dc
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            java.lang.String r5 = r5.message
            boolean r5 = android.text.TextUtils.isEmpty(r5)
            if (r5 != 0) goto L_0x10dc
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
            goto L_0x11af
        L_0x10dc:
            r14 = 2
            r5 = 2131626761(0x7f0e0b09, float:1.8880767E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedPhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x10f2:
            org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r5 == 0) goto L_0x1110
            r5 = 2131626737(0x7f0e0af1, float:1.8880719E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGame"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x1110:
            java.lang.CharSequence r5 = r4.messageText
            if (r5 == 0) goto L_0x1152
            java.lang.CharSequence r5 = r4.messageText
            int r5 = r5.length()
            if (r5 <= 0) goto L_0x1152
            java.lang.CharSequence r5 = r4.messageText
            int r6 = r5.length()
            r9 = 20
            if (r6 <= r9) goto L_0x113f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r9 = 20
            r14 = 0
            java.lang.CharSequence r9 = r5.subSequence(r14, r9)
            r6.append(r9)
            java.lang.String r9 = "..."
            r6.append(r9)
            java.lang.String r5 = r6.toString()
            goto L_0x1140
        L_0x113f:
            r14 = 0
        L_0x1140:
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r14] = r13
            r9 = 1
            r6[r9] = r5
            java.lang.String r9 = r8.title
            r14 = 2
            r6[r14] = r9
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r7, r6)
            goto L_0x11af
        L_0x1152:
            r14 = 2
            r5 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r7 = 0
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r9 = 1
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x1167:
            r7 = 0
            r9 = 1
            r14 = 2
        L_0x116a:
            r5 = 2131626743(0x7f0e0af7, float:1.888073E38)
            java.lang.Object[] r6 = new java.lang.Object[r14]
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r6[r9] = r7
            java.lang.String r7 = "NotificationActionPinnedGeo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11af
        L_0x117c:
            java.lang.String r5 = r4.getStickerEmoji()
            if (r5 == 0) goto L_0x119a
            r6 = 2131626775(0x7f0e0b17, float:1.8880796E38)
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
            goto L_0x11ae
        L_0x119a:
            r9 = 0
            r12 = 1
            r14 = 2
            r6 = 2131626773(0x7f0e0b15, float:1.8880792E38)
            java.lang.Object[] r7 = new java.lang.Object[r14]
            r7[r9] = r13
            java.lang.String r9 = r8.title
            r7[r12] = r9
            java.lang.String r9 = "NotificationActionPinnedSticker"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r9, r6, r7)
        L_0x11ae:
            r5 = r6
        L_0x11af:
            goto L_0x191f
        L_0x11b1:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionGameScore
            if (r4 == 0) goto L_0x11c1
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x191f
        L_0x11c1:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme
            if (r4 == 0) goto L_0x1225
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r4 = (org.telegram.tgnet.TLRPC.TL_messageActionSetChatTheme) r4
            java.lang.String r4 = r4.emoticon
            boolean r5 = android.text.TextUtils.isEmpty(r4)
            if (r5 == 0) goto L_0x11fc
            int r5 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r5 != 0) goto L_0x11e8
            r5 = 2131624958(0x7f0e03fe, float:1.887711E38)
            r6 = 0
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r7 = "ChatThemeDisabledYou"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x11fa
        L_0x11e8:
            r6 = 0
            r5 = 2131624957(0x7f0e03fd, float:1.8877108E38)
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]
            r7[r6] = r13
            r6 = 1
            r7[r6] = r4
            java.lang.String r6 = "ChatThemeDisabled"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r5, r7)
        L_0x11fa:
            goto L_0x1223
        L_0x11fc:
            int r5 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r5 != 0) goto L_0x1210
            r5 = 2131624955(0x7f0e03fb, float:1.8877104E38)
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r4
            java.lang.String r7 = "ChangedChatThemeYou"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x1222
        L_0x1210:
            r6 = 1
            r7 = 0
            r5 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            r9[r7] = r13
            r9[r6] = r4
            java.lang.String r6 = "ChangedChatThemeTo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r5, r9)
        L_0x1222:
        L_0x1223:
            goto L_0x191f
        L_0x1225:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByRequest
            if (r4 == 0) goto L_0x191d
            java.lang.CharSequence r4 = r0.messageText
            java.lang.String r5 = r4.toString()
            goto L_0x191f
        L_0x1235:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            long r4 = r4.channel_id
            r6 = 0
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 == 0) goto L_0x1271
            boolean r4 = r8.megagroup
            if (r4 != 0) goto L_0x1271
            boolean r4 = r31.isVideoAvatar()
            if (r4 == 0) goto L_0x125e
            r4 = 2131624907(0x7f0e03cb, float:1.8877007E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = r8.title
            r7 = 0
            r5[r7] = r6
            java.lang.String r6 = "ChannelVideoEditNotification"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x125e:
            r5 = 1
            r7 = 0
            r4 = 2131624872(0x7f0e03a8, float:1.8876936E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = r8.title
            r5[r7] = r6
            java.lang.String r6 = "ChannelPhotoEditNotification"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x1271:
            boolean r4 = r31.isVideoAvatar()
            if (r4 == 0) goto L_0x128d
            r4 = 2131626792(0x7f0e0b28, float:1.888083E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationEditedGroupVideo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x128d:
            r5 = 2
            r6 = 0
            r7 = 1
            r4 = 2131626791(0x7f0e0b27, float:1.8880828E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r5[r7] = r6
            java.lang.String r6 = "NotificationEditedGroupPhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x12a3:
            r18 = r7
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r4 == 0) goto L_0x1583
            boolean r4 = r8.megagroup
            if (r4 != 0) goto L_0x1583
            boolean r4 = r31.isMediaEmpty()
            if (r4 == 0) goto L_0x12ea
            if (r32 != 0) goto L_0x12d9
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x12d9
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            java.lang.String r7 = r7.message
            r9 = 1
            r4[r9] = r7
            r7 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x191f
        L_0x12d9:
            r6 = 0
            r9 = 1
            r4 = 2131624856(0x7f0e0398, float:1.8876904E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x12ea:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r4 == 0) goto L_0x133c
            if (r32 != 0) goto L_0x132b
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x132b
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x132b
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
            r7 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x191f
        L_0x132b:
            r6 = 0
            r9 = 1
            r4 = 2131624857(0x7f0e0399, float:1.8876906E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessagePhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x133c:
            boolean r4 = r31.isVideo()
            if (r4 == 0) goto L_0x138c
            if (r32 != 0) goto L_0x137b
            int r4 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r4 < r7) goto L_0x137b
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x137b
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
            r6 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r6, r4)
            r33[r7] = r9
            goto L_0x191f
        L_0x137b:
            r7 = 0
            r9 = 1
            r4 = 2131624863(0x7f0e039f, float:1.8876918E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r7] = r13
            java.lang.String r6 = "ChannelMessageVideo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x138c:
            r7 = 0
            r9 = 1
            boolean r4 = r31.isVoice()
            if (r4 == 0) goto L_0x13a3
            r4 = 2131624848(0x7f0e0390, float:1.8876887E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r7] = r13
            java.lang.String r6 = "ChannelMessageAudio"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x13a3:
            boolean r4 = r31.isRoundVideo()
            if (r4 == 0) goto L_0x13b8
            r4 = 2131624860(0x7f0e039c, float:1.8876912E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r7] = r13
            java.lang.String r6 = "ChannelMessageRound"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x13b8:
            boolean r4 = r31.isMusic()
            if (r4 == 0) goto L_0x13cd
            r4 = 2131624855(0x7f0e0397, float:1.8876901E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r7] = r13
            java.lang.String r6 = "ChannelMessageMusic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x13cd:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r4 == 0) goto L_0x13f7
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r4
            r5 = 2131624849(0x7f0e0391, float:1.887689E38)
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
            goto L_0x191f
        L_0x13f7:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r4 == 0) goto L_0x143a
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r5 = r4.poll
            boolean r5 = r5.quiz
            if (r5 == 0) goto L_0x1422
            r5 = 2131624859(0x7f0e039b, float:1.887691E38)
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
            goto L_0x1438
        L_0x1422:
            r6 = 2
            r7 = 0
            r9 = 1
            r5 = 2131624858(0x7f0e039a, float:1.8876908E38)
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r7] = r13
            org.telegram.tgnet.TLRPC$Poll r7 = r4.poll
            java.lang.String r7 = r7.question
            r6[r9] = r7
            java.lang.String r7 = "ChannelMessagePoll2"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
        L_0x1438:
            goto L_0x191f
        L_0x143a:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r4 != 0) goto L_0x1572
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r4 == 0) goto L_0x144e
            r6 = 0
            r9 = 1
            goto L_0x1574
        L_0x144e:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x1467
            r4 = 2131624853(0x7f0e0395, float:1.8876897E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageLiveLocation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x1467:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x1541
            boolean r4 = r31.isSticker()
            if (r4 != 0) goto L_0x1517
            boolean r4 = r31.isAnimatedSticker()
            if (r4 == 0) goto L_0x147d
            goto L_0x1517
        L_0x147d:
            boolean r4 = r31.isGif()
            if (r4 == 0) goto L_0x14cd
            if (r32 != 0) goto L_0x14bc
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x14bc
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x14bc
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
            r7 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x191f
        L_0x14bc:
            r6 = 0
            r9 = 1
            r4 = 2131624852(0x7f0e0394, float:1.8876895E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageGIF"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x14cd:
            if (r32 != 0) goto L_0x1506
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x1506
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x1506
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
            r7 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x191f
        L_0x1506:
            r6 = 0
            r9 = 1
            r4 = 2131624850(0x7f0e0392, float:1.8876891E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageDocument"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x1517:
            java.lang.String r4 = r31.getStickerEmoji()
            if (r4 == 0) goto L_0x1530
            r5 = 2131624862(0x7f0e039e, float:1.8876916E38)
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r13
            r9 = 1
            r6[r9] = r4
            java.lang.String r7 = "ChannelMessageStickerEmoji"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
            goto L_0x153f
        L_0x1530:
            r7 = 0
            r9 = 1
            r5 = 2131624861(0x7f0e039d, float:1.8876914E38)
            java.lang.Object[] r6 = new java.lang.Object[r9]
            r6[r7] = r13
            java.lang.String r7 = "ChannelMessageSticker"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
        L_0x153f:
            goto L_0x191f
        L_0x1541:
            if (r32 != 0) goto L_0x1561
            java.lang.CharSequence r4 = r0.messageText
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x1561
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r6 = 0
            r4[r6] = r13
            java.lang.CharSequence r7 = r0.messageText
            r9 = 1
            r4[r9] = r7
            r7 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r5, r7, r4)
            r33[r6] = r9
            goto L_0x191f
        L_0x1561:
            r6 = 0
            r9 = 1
            r4 = 2131624856(0x7f0e0398, float:1.8876904E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x1572:
            r6 = 0
            r9 = 1
        L_0x1574:
            r4 = 2131624854(0x7f0e0396, float:1.88769E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageMap"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x1583:
            boolean r4 = r31.isMediaEmpty()
            r5 = 2131626838(0x7f0e0b56, float:1.8880923E38)
            java.lang.String r7 = "NotificationMessageGroupText"
            if (r4 == 0) goto L_0x15c8
            if (r32 != 0) goto L_0x15b2
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x15b2
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
            goto L_0x191f
        L_0x15b2:
            r9 = 2
            r4 = 2131626831(0x7f0e0b4f, float:1.888091E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            r12 = r23
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r4, r5)
            goto L_0x191f
        L_0x15c8:
            r12 = r23
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
            if (r4 == 0) goto L_0x1621
            if (r32 != 0) goto L_0x160b
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x160b
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x160b
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
            goto L_0x191f
        L_0x160b:
            r9 = 2
            r4 = 2131626832(0x7f0e0b50, float:1.8880911E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupPhoto"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x1621:
            boolean r4 = r31.isVideo()
            if (r4 == 0) goto L_0x1676
            if (r32 != 0) goto L_0x1660
            int r4 = android.os.Build.VERSION.SDK_INT
            r9 = 19
            if (r4 < r9) goto L_0x1660
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x1660
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
            goto L_0x191f
        L_0x1660:
            r9 = 2
            r4 = 2131626839(0x7f0e0b57, float:1.8880926E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = " "
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x1676:
            boolean r4 = r31.isVoice()
            if (r4 == 0) goto L_0x1692
            r4 = 2131626821(0x7f0e0b45, float:1.888089E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupAudio"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x1692:
            boolean r4 = r31.isRoundVideo()
            if (r4 == 0) goto L_0x16ae
            r4 = 2131626835(0x7f0e0b53, float:1.8880917E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupRound"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x16ae:
            boolean r4 = r31.isMusic()
            if (r4 == 0) goto L_0x16ca
            r4 = 2131626830(0x7f0e0b4e, float:1.8880907E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupMusic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x16ca:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact
            if (r4 == 0) goto L_0x16f9
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaContact) r4
            r5 = 2131626822(0x7f0e0b46, float:1.8880891E38)
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
            goto L_0x191f
        L_0x16f9:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
            if (r4 == 0) goto L_0x1746
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r5 = r4.poll
            boolean r5 = r5.quiz
            if (r5 == 0) goto L_0x1729
            r5 = 2131626834(0x7f0e0b52, float:1.8880915E38)
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
            goto L_0x1744
        L_0x1729:
            r5 = 2131626833(0x7f0e0b51, float:1.8880913E38)
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
        L_0x1744:
            goto L_0x191f
        L_0x1746:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
            if (r4 == 0) goto L_0x176f
            r4 = 2131626824(0x7f0e0b48, float:1.8880895E38)
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
            goto L_0x191f
        L_0x176f:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo
            if (r4 != 0) goto L_0x18c3
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaVenue
            if (r4 == 0) goto L_0x1784
            r6 = 0
            r7 = 1
            r9 = 2
            goto L_0x18c6
        L_0x1784:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x17a2
            r4 = 2131626828(0x7f0e0b4c, float:1.8880903E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupLiveLocation"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x17a2:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
            if (r4 == 0) goto L_0x1890
            boolean r4 = r31.isSticker()
            if (r4 != 0) goto L_0x185c
            boolean r4 = r31.isAnimatedSticker()
            if (r4 == 0) goto L_0x17b8
            goto L_0x185c
        L_0x17b8:
            boolean r4 = r31.isGif()
            if (r4 == 0) goto L_0x180d
            if (r32 != 0) goto L_0x17f7
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x17f7
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x17f7
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
            goto L_0x191f
        L_0x17f7:
            r9 = 2
            r4 = 2131626826(0x7f0e0b4a, float:1.88809E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupGif"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x180d:
            if (r32 != 0) goto L_0x1846
            int r4 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r4 < r6) goto L_0x1846
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x1846
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
            goto L_0x191f
        L_0x1846:
            r9 = 2
            r4 = 2131626823(0x7f0e0b47, float:1.8880893E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupDocument"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x185c:
            java.lang.String r4 = r31.getStickerEmoji()
            if (r4 == 0) goto L_0x187a
            r5 = 2131626837(0x7f0e0b55, float:1.8880921E38)
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
            goto L_0x188e
        L_0x187a:
            r7 = 0
            r9 = 1
            r12 = 2
            r5 = 2131626836(0x7f0e0b54, float:1.888092E38)
            java.lang.Object[] r6 = new java.lang.Object[r12]
            r6[r7] = r13
            java.lang.String r7 = r8.title
            r6[r9] = r7
            java.lang.String r7 = "NotificationMessageGroupSticker"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r7, r5, r6)
        L_0x188e:
            goto L_0x191f
        L_0x1890:
            if (r32 != 0) goto L_0x18b0
            java.lang.CharSequence r4 = r0.messageText
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x18b0
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
            goto L_0x191f
        L_0x18b0:
            r9 = 2
            r4 = 2131626831(0x7f0e0b4f, float:1.888091E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r4, r5)
            goto L_0x191f
        L_0x18c3:
            r6 = 0
            r7 = 1
            r9 = 2
        L_0x18c6:
            r4 = 2131626829(0x7f0e0b4d, float:1.8880905E38)
            java.lang.Object[] r5 = new java.lang.Object[r9]
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r5[r7] = r6
            java.lang.String r6 = "NotificationMessageGroupMap"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x18d8:
            r18 = r7
            r12 = r23
            goto L_0x18e3
        L_0x18dd:
            r18 = r7
            r16 = r12
            r12 = r23
        L_0x18e3:
            if (r34 == 0) goto L_0x18e8
            r4 = 0
            r34[r4] = r4
        L_0x18e8:
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r4 == 0) goto L_0x1902
            boolean r4 = r8.megagroup
            if (r4 != 0) goto L_0x1902
            r4 = 2131624856(0x7f0e0398, float:1.8876904E38)
            r5 = 1
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r6 = 0
            r5[r6] = r13
            java.lang.String r6 = "ChannelMessageNoText"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r6, r4, r5)
            goto L_0x191f
        L_0x1902:
            r6 = 0
            r4 = 2131626831(0x7f0e0b4f, float:1.888091E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r6] = r13
            java.lang.String r6 = r8.title
            r7 = 1
            r5[r7] = r6
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r4, r5)
            goto L_0x191f
        L_0x1915:
            r18 = r7
            r16 = r12
            r8 = r24
            r24 = r3
        L_0x191d:
            r5 = r22
        L_0x191f:
            return r5
        L_0x1920:
            r1 = 2131629035(0x7f0e13eb, float:1.888538E38)
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
    public /* synthetic */ void m1068x8a90ed32() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda4(this));
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda39(this));
    }

    /* renamed from: lambda$hideNotifications$26$org-telegram-messenger-NotificationsController  reason: not valid java name */
    public /* synthetic */ void m1043x832e582c() {
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
    public /* synthetic */ void m1045xa67ee1() {
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
    public /* synthetic */ void m1064x309788cf() {
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
    public void m1039xab324d39(long dialogId, int what) {
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
    public void m1040xb6var_c1b(int type, int what) {
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
    public /* synthetic */ void m1038xdfb4577b() {
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

    /* JADX WARNING: Removed duplicated region for block: B:191:0x0483 A[LOOP:1: B:189:0x047e->B:191:0x0483, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x049c  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x04e9  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x05d8  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0493 A[EDGE_INSN: B:245:0x0493->B:192:0x0493 ?: BREAK  , SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String validateChannelId(long r34, java.lang.String r36, long[] r37, int r38, android.net.Uri r39, int r40, boolean r41, boolean r42, boolean r43, int r44) {
        /*
            r33 = this;
            r1 = r33
            r2 = r34
            r4 = r39
            r5 = r40
            r6 = r44
            r33.ensureGroupsCreated()
            org.telegram.messenger.AccountInstance r0 = r33.getAccountInstance()
            android.content.SharedPreferences r7 = r0.getNotificationsSettings()
            java.lang.String r0 = "groups"
            java.lang.String r8 = "private"
            java.lang.String r9 = "channels"
            r10 = 2
            if (r43 == 0) goto L_0x0033
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "other"
            r11.append(r12)
            int r12 = r1.currentAccount
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            r12 = 0
            goto L_0x0072
        L_0x0033:
            if (r6 != r10) goto L_0x0049
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r9)
            int r12 = r1.currentAccount
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.lang.String r12 = "overwrite_channel"
            goto L_0x0072
        L_0x0049:
            if (r6 != 0) goto L_0x005f
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r0)
            int r12 = r1.currentAccount
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.lang.String r12 = "overwrite_group"
            goto L_0x0072
        L_0x005f:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r8)
            int r12 = r1.currentAccount
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.lang.String r12 = "overwrite_private"
        L_0x0072:
            r14 = 0
            if (r41 != 0) goto L_0x007d
            boolean r15 = org.telegram.messenger.DialogObject.isEncryptedDialog(r34)
            if (r15 == 0) goto L_0x007d
            r15 = 1
            goto L_0x007e
        L_0x007d:
            r15 = 0
        L_0x007e:
            if (r42 != 0) goto L_0x008b
            if (r12 == 0) goto L_0x008b
            boolean r16 = r7.getBoolean(r12, r14)
            if (r16 == 0) goto L_0x008b
            r16 = 1
            goto L_0x008d
        L_0x008b:
            r16 = 0
        L_0x008d:
            if (r4 != 0) goto L_0x0092
            java.lang.String r17 = "NoSound"
            goto L_0x0096
        L_0x0092:
            java.lang.String r17 = r39.toString()
        L_0x0096:
            java.lang.String r13 = org.telegram.messenger.Utilities.MD5(r17)
            r10 = 5
            if (r13 == 0) goto L_0x00a8
            int r14 = r13.length()
            if (r14 <= r10) goto L_0x00a8
            r14 = 0
            java.lang.String r13 = r13.substring(r14, r10)
        L_0x00a8:
            if (r43 == 0) goto L_0x00b7
            r0 = 2131626906(0x7f0e0b9a, float:1.8881061E38)
            java.lang.String r8 = "NotificationsSilent"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r8, r0)
            java.lang.String r8 = "silent"
            r10 = r0
            goto L_0x010f
        L_0x00b7:
            if (r41 == 0) goto L_0x00e4
            if (r42 == 0) goto L_0x00c5
            r14 = 2131626884(0x7f0e0b84, float:1.8881017E38)
            java.lang.String r10 = "NotificationsInAppDefault"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r14)
            goto L_0x00ce
        L_0x00c5:
            r10 = 2131626867(0x7f0e0b73, float:1.8880982E38)
            java.lang.String r14 = "NotificationsDefault"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
        L_0x00ce:
            r14 = 2
            if (r6 != r14) goto L_0x00d7
            if (r42 == 0) goto L_0x00d5
            java.lang.String r9 = "channels_ia"
        L_0x00d5:
            r8 = r9
            goto L_0x010f
        L_0x00d7:
            if (r6 != 0) goto L_0x00df
            if (r42 == 0) goto L_0x00dd
            java.lang.String r0 = "groups_ia"
        L_0x00dd:
            r8 = r0
            goto L_0x010f
        L_0x00df:
            if (r42 == 0) goto L_0x00e3
            java.lang.String r8 = "private_ia"
        L_0x00e3:
            goto L_0x010f
        L_0x00e4:
            if (r42 == 0) goto L_0x00f6
            r0 = 2131626864(0x7f0e0b70, float:1.8880976E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r8 = 0
            r9[r8] = r36
            java.lang.String r8 = "NotificationsChatInApp"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r0, r9)
            goto L_0x00f8
        L_0x00f6:
            r0 = r36
        L_0x00f8:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            if (r42 == 0) goto L_0x0102
            java.lang.String r9 = "org.telegram.keyia"
            goto L_0x0104
        L_0x0102:
            java.lang.String r9 = "org.telegram.key"
        L_0x0104:
            r8.append(r9)
            r8.append(r2)
            java.lang.String r8 = r8.toString()
            r10 = r0
        L_0x010f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r8)
            java.lang.String r9 = "_"
            r0.append(r9)
            r0.append(r13)
            java.lang.String r8 = r0.toString()
            r14 = 0
            java.lang.String r0 = r7.getString(r8, r14)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r8)
            r19 = r10
            java.lang.String r10 = "_s"
            r14.append(r10)
            java.lang.String r14 = r14.toString()
            r20 = r12
            r12 = 0
            java.lang.String r14 = r7.getString(r14, r12)
            r12 = 0
            java.lang.StringBuilder r21 = new java.lang.StringBuilder
            r21.<init>()
            r22 = r21
            r21 = 0
            r23 = r12
            java.lang.String r12 = "secret"
            if (r0 == 0) goto L_0x040a
            r24 = r13
            android.app.NotificationManager r13 = systemNotificationManager
            android.app.NotificationChannel r13 = r13.getNotificationChannel(r0)
            boolean r25 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r26 = r11
            java.lang.String r11 = " = "
            if (r25 == 0) goto L_0x017f
            r25 = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r4 = "current channel for "
            r9.append(r4)
            r9.append(r0)
            r9.append(r11)
            r9.append(r13)
            java.lang.String r4 = r9.toString()
            org.telegram.messenger.FileLog.d(r4)
            goto L_0x0181
        L_0x017f:
            r25 = r9
        L_0x0181:
            if (r13 == 0) goto L_0x03ef
            if (r43 != 0) goto L_0x03de
            if (r16 != 0) goto L_0x03de
            int r4 = r13.getImportance()
            android.net.Uri r9 = r13.getSound()
            long[] r27 = r13.getVibrationPattern()
            r28 = r10
            boolean r10 = r13.shouldVibrate()
            if (r10 != 0) goto L_0x01ac
            if (r27 != 0) goto L_0x01ac
            r29 = r8
            r30 = r10
            r8 = 2
            long[] r10 = new long[r8]
            r10 = {0, 0} // fill-array
            r27 = r10
            r8 = r27
            goto L_0x01b2
        L_0x01ac:
            r29 = r8
            r30 = r10
            r8 = r27
        L_0x01b2:
            int r10 = r13.getLightColor()
            if (r8 == 0) goto L_0x01d2
            r27 = 0
            r31 = r13
            r13 = r27
        L_0x01be:
            int r5 = r8.length
            if (r13 >= r5) goto L_0x01cf
            r2 = r8[r13]
            r5 = r22
            r5.append(r2)
            int r13 = r13 + 1
            r2 = r34
            r5 = r40
            goto L_0x01be
        L_0x01cf:
            r5 = r22
            goto L_0x01d6
        L_0x01d2:
            r31 = r13
            r5 = r22
        L_0x01d6:
            r5.append(r10)
            if (r9 == 0) goto L_0x01e2
            java.lang.String r2 = r9.toString()
            r5.append(r2)
        L_0x01e2:
            r5.append(r4)
            if (r41 != 0) goto L_0x01ec
            if (r15 == 0) goto L_0x01ec
            r5.append(r12)
        L_0x01ec:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0212
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "current channel settings for "
            r2.append(r3)
            r2.append(r0)
            r2.append(r11)
            r2.append(r5)
            java.lang.String r3 = " old = "
            r2.append(r3)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0212:
            java.lang.String r2 = r5.toString()
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r3 = 0
            r5.setLength(r3)
            boolean r3 = r2.equals(r14)
            if (r3 != 0) goto L_0x03c0
            r3 = 0
            java.lang.String r11 = "notify2_"
            if (r4 != 0) goto L_0x0274
            android.content.SharedPreferences$Editor r3 = r7.edit()
            if (r41 == 0) goto L_0x024c
            if (r42 != 0) goto L_0x0245
            java.lang.String r11 = getGlobalNotificationsKey(r44)
            r13 = 2147483647(0x7fffffff, float:NaN)
            r3.putInt(r11, r13)
            r1.updateServerNotificationsSettings((int) r6)
            r27 = r14
            r22 = r15
            r14 = r34
            goto L_0x0269
        L_0x0245:
            r27 = r14
            r22 = r15
            r14 = r34
            goto L_0x0269
        L_0x024c:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r11)
            r27 = r14
            r22 = r15
            r14 = r34
            r13.append(r14)
            java.lang.String r11 = r13.toString()
            r13 = 2
            r3.putInt(r11, r13)
            r11 = 1
            r1.updateServerNotificationsSettings(r14, r11)
        L_0x0269:
            r11 = 1
            r13 = r40
            r21 = r2
            r32 = r4
            r23 = r11
            goto L_0x030b
        L_0x0274:
            r27 = r14
            r22 = r15
            r14 = r34
            r13 = r40
            if (r4 == r13) goto L_0x0307
            if (r42 != 0) goto L_0x02ff
            android.content.SharedPreferences$Editor r3 = r7.edit()
            r21 = r2
            r2 = 4
            if (r4 == r2) goto L_0x0299
            r2 = 5
            if (r4 != r2) goto L_0x028d
            goto L_0x0299
        L_0x028d:
            r2 = 1
            if (r4 != r2) goto L_0x0292
            r2 = 4
            goto L_0x029a
        L_0x0292:
            r2 = 2
            if (r4 != r2) goto L_0x0297
            r2 = 5
            goto L_0x029a
        L_0x0297:
            r2 = 0
            goto L_0x029a
        L_0x0299:
            r2 = 1
        L_0x029a:
            if (r41 == 0) goto L_0x02c1
            java.lang.String r11 = getGlobalNotificationsKey(r44)
            r32 = r4
            r4 = 0
            android.content.SharedPreferences$Editor r11 = r3.putInt(r11, r4)
            r11.commit()
            r4 = 2
            if (r6 != r4) goto L_0x02b3
            java.lang.String r4 = "priority_channel"
            r3.putInt(r4, r2)
            goto L_0x0303
        L_0x02b3:
            if (r6 != 0) goto L_0x02bb
            java.lang.String r4 = "priority_group"
            r3.putInt(r4, r2)
            goto L_0x0303
        L_0x02bb:
            java.lang.String r4 = "priority_messages"
            r3.putInt(r4, r2)
            goto L_0x0303
        L_0x02c1:
            r32 = r4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r11)
            r4.append(r14)
            java.lang.String r4 = r4.toString()
            r11 = 0
            r3.putInt(r4, r11)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r11 = "notifyuntil_"
            r4.append(r11)
            r4.append(r14)
            java.lang.String r4 = r4.toString()
            r3.remove(r4)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r11 = "priority_"
            r4.append(r11)
            r4.append(r14)
            java.lang.String r4 = r4.toString()
            r3.putInt(r4, r2)
            goto L_0x0303
        L_0x02ff:
            r21 = r2
            r32 = r4
        L_0x0303:
            r2 = 1
            r23 = r2
            goto L_0x030b
        L_0x0307:
            r21 = r2
            r32 = r4
        L_0x030b:
            r2 = r37
            boolean r4 = r1.isEmptyVibration(r2)
            r11 = 1
            r4 = r4 ^ r11
            r11 = r30
            if (r4 == r11) goto L_0x036b
            if (r42 != 0) goto L_0x0365
            if (r3 != 0) goto L_0x031f
            android.content.SharedPreferences$Editor r3 = r7.edit()
        L_0x031f:
            if (r41 == 0) goto L_0x0349
            r30 = r0
            r0 = 2
            if (r6 != r0) goto L_0x0331
            if (r11 == 0) goto L_0x032a
            r0 = 0
            goto L_0x032b
        L_0x032a:
            r0 = 2
        L_0x032b:
            java.lang.String r2 = "vibrate_channel"
            r3.putInt(r2, r0)
            goto L_0x0367
        L_0x0331:
            if (r6 != 0) goto L_0x033e
            if (r11 == 0) goto L_0x0337
            r0 = 0
            goto L_0x0338
        L_0x0337:
            r0 = 2
        L_0x0338:
            java.lang.String r2 = "vibrate_group"
            r3.putInt(r2, r0)
            goto L_0x0367
        L_0x033e:
            if (r11 == 0) goto L_0x0342
            r0 = 0
            goto L_0x0343
        L_0x0342:
            r0 = 2
        L_0x0343:
            java.lang.String r2 = "vibrate_messages"
            r3.putInt(r2, r0)
            goto L_0x0367
        L_0x0349:
            r30 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "vibrate_"
            r0.append(r2)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            if (r11 == 0) goto L_0x0360
            r2 = 0
            goto L_0x0361
        L_0x0360:
            r2 = 2
        L_0x0361:
            r3.putInt(r0, r2)
            goto L_0x0367
        L_0x0365:
            r30 = r0
        L_0x0367:
            r0 = r8
            r23 = 1
            goto L_0x036f
        L_0x036b:
            r30 = r0
            r0 = r37
        L_0x036f:
            r2 = r38
            if (r10 == r2) goto L_0x03b3
            if (r42 != 0) goto L_0x03ad
            if (r3 != 0) goto L_0x037b
            android.content.SharedPreferences$Editor r3 = r7.edit()
        L_0x037b:
            if (r41 == 0) goto L_0x0396
            r37 = r0
            r0 = 2
            if (r6 != r0) goto L_0x0388
            java.lang.String r0 = "ChannelLed"
            r3.putInt(r0, r10)
            goto L_0x03af
        L_0x0388:
            if (r6 != 0) goto L_0x0390
            java.lang.String r0 = "GroupLed"
            r3.putInt(r0, r10)
            goto L_0x03af
        L_0x0390:
            java.lang.String r0 = "MessagesLed"
            r3.putInt(r0, r10)
            goto L_0x03af
        L_0x0396:
            r37 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "color_"
            r0.append(r2)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            r3.putInt(r0, r10)
            goto L_0x03af
        L_0x03ad:
            r37 = r0
        L_0x03af:
            r0 = r10
            r23 = 1
            goto L_0x03b7
        L_0x03b3:
            r37 = r0
            r0 = r38
        L_0x03b7:
            if (r3 == 0) goto L_0x03bc
            r3.commit()
        L_0x03bc:
            r2 = r0
            r0 = r37
            goto L_0x03d4
        L_0x03c0:
            r13 = r40
            r21 = r2
            r32 = r4
            r27 = r14
            r22 = r15
            r11 = r30
            r14 = r34
            r30 = r0
            r0 = r37
            r2 = r38
        L_0x03d4:
            r3 = r2
            r8 = r27
            r4 = r30
            r2 = r0
            r0 = r21
            goto L_0x0428
        L_0x03de:
            r30 = r0
            r29 = r8
            r28 = r10
            r31 = r13
            r27 = r14
            r13 = r5
            r5 = r22
            r22 = r15
            r14 = r2
            goto L_0x041e
        L_0x03ef:
            r30 = r0
            r29 = r8
            r28 = r10
            r31 = r13
            r27 = r14
            r13 = r5
            r5 = r22
            r22 = r15
            r14 = r2
            r0 = 0
            r2 = 0
            r3 = r38
            r4 = r0
            r8 = r2
            r0 = r21
            r2 = r37
            goto L_0x0428
        L_0x040a:
            r30 = r0
            r29 = r8
            r25 = r9
            r28 = r10
            r26 = r11
            r24 = r13
            r27 = r14
            r13 = r5
            r5 = r22
            r22 = r15
            r14 = r2
        L_0x041e:
            r2 = r37
            r3 = r38
            r0 = r21
            r8 = r27
            r4 = r30
        L_0x0428:
            if (r23 == 0) goto L_0x046a
            if (r0 == 0) goto L_0x046a
            android.content.SharedPreferences$Editor r9 = r7.edit()
            r10 = r29
            android.content.SharedPreferences$Editor r9 = r9.putString(r10, r4)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            r12 = r28
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            android.content.SharedPreferences$Editor r9 = r9.putString(r11, r0)
            r9.commit()
            boolean r9 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r9 == 0) goto L_0x0468
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "change edited channel "
            r9.append(r11)
            r9.append(r4)
            java.lang.String r9 = r9.toString()
            org.telegram.messenger.FileLog.d(r9)
            r9 = r12
            goto L_0x0477
        L_0x0468:
            r9 = r12
            goto L_0x0477
        L_0x046a:
            r9 = r28
            r10 = r29
            if (r16 != 0) goto L_0x047d
            if (r0 == 0) goto L_0x047d
            if (r42 == 0) goto L_0x047d
            if (r41 != 0) goto L_0x0477
            goto L_0x047d
        L_0x0477:
            r6 = r39
            r17 = r7
            goto L_0x04e7
        L_0x047d:
            r11 = 0
        L_0x047e:
            r37 = r0
            int r0 = r2.length
            if (r11 >= r0) goto L_0x0493
            r17 = r7
            r6 = r2[r11]
            r5.append(r6)
            int r11 = r11 + 1
            r0 = r37
            r6 = r44
            r7 = r17
            goto L_0x047e
        L_0x0493:
            r17 = r7
            r5.append(r3)
            r6 = r39
            if (r6 == 0) goto L_0x04a3
            java.lang.String r0 = r39.toString()
            r5.append(r0)
        L_0x04a3:
            r5.append(r13)
            if (r41 != 0) goto L_0x04ad
            if (r22 == 0) goto L_0x04ad
            r5.append(r12)
        L_0x04ad:
            java.lang.String r0 = r5.toString()
            java.lang.String r7 = org.telegram.messenger.Utilities.MD5(r0)
            if (r43 != 0) goto L_0x04e6
            if (r4 == 0) goto L_0x04e6
            if (r16 != 0) goto L_0x04c1
            boolean r0 = r8.equals(r7)
            if (r0 != 0) goto L_0x04e6
        L_0x04c1:
            android.app.NotificationManager r0 = systemNotificationManager     // Catch:{ Exception -> 0x04c7 }
            r0.deleteNotificationChannel(r4)     // Catch:{ Exception -> 0x04c7 }
            goto L_0x04cb
        L_0x04c7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04cb:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x04e3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r11 = "delete channel by settings change "
            r0.append(r11)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x04e3:
            r4 = 0
            r0 = r7
            goto L_0x04e7
        L_0x04e6:
            r0 = r7
        L_0x04e7:
            if (r4 != 0) goto L_0x05d8
            java.lang.String r7 = "channel_"
            if (r41 == 0) goto L_0x0514
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            int r12 = r1.currentAccount
            r11.append(r12)
            r11.append(r7)
            r11.append(r10)
            r12 = r25
            r11.append(r12)
            java.security.SecureRandom r7 = org.telegram.messenger.Utilities.random
            r37 = r4
            r21 = r5
            long r4 = r7.nextLong()
            r11.append(r4)
            java.lang.String r4 = r11.toString()
            goto L_0x053a
        L_0x0514:
            r37 = r4
            r21 = r5
            r12 = r25
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = r1.currentAccount
            r4.append(r5)
            r4.append(r7)
            r4.append(r14)
            r4.append(r12)
            java.security.SecureRandom r5 = org.telegram.messenger.Utilities.random
            long r11 = r5.nextLong()
            r4.append(r11)
            java.lang.String r4 = r4.toString()
        L_0x053a:
            android.app.NotificationChannel r5 = new android.app.NotificationChannel
            if (r22 == 0) goto L_0x0548
            r7 = 2131627935(0x7f0e0f9f, float:1.8883148E38)
            java.lang.String r11 = "SecretChatName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r11, r7)
            goto L_0x054a
        L_0x0548:
            r7 = r19
        L_0x054a:
            r5.<init>(r4, r7, r13)
            r11 = r26
            r5.setGroup(r11)
            if (r3 == 0) goto L_0x055c
            r7 = 1
            r5.enableLights(r7)
            r5.setLightColor(r3)
            goto L_0x0561
        L_0x055c:
            r7 = 1
            r12 = 0
            r5.enableLights(r12)
        L_0x0561:
            boolean r12 = r1.isEmptyVibration(r2)
            if (r12 != 0) goto L_0x0571
            r5.enableVibration(r7)
            int r7 = r2.length
            if (r7 <= 0) goto L_0x0575
            r5.setVibrationPattern(r2)
            goto L_0x0575
        L_0x0571:
            r7 = 0
            r5.enableVibration(r7)
        L_0x0575:
            android.media.AudioAttributes$Builder r7 = new android.media.AudioAttributes$Builder
            r7.<init>()
            r12 = 4
            r7.setContentType(r12)
            r12 = 5
            r7.setUsage(r12)
            if (r6 == 0) goto L_0x058c
            android.media.AudioAttributes r12 = r7.build()
            r5.setSound(r6, r12)
            goto L_0x0590
        L_0x058c:
            r12 = 0
            r5.setSound(r12, r12)
        L_0x0590:
            boolean r12 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r12 == 0) goto L_0x05ab
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r18 = r2
            java.lang.String r2 = "create new channel "
            r12.append(r2)
            r12.append(r4)
            java.lang.String r2 = r12.toString()
            org.telegram.messenger.FileLog.d(r2)
            goto L_0x05ad
        L_0x05ab:
            r18 = r2
        L_0x05ad:
            r12 = r3
            long r2 = android.os.SystemClock.elapsedRealtime()
            r1.lastNotificationChannelCreateTime = r2
            android.app.NotificationManager r2 = systemNotificationManager
            r2.createNotificationChannel(r5)
            android.content.SharedPreferences$Editor r2 = r17.edit()
            android.content.SharedPreferences$Editor r2 = r2.putString(r10, r4)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r10)
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            android.content.SharedPreferences$Editor r2 = r2.putString(r3, r0)
            r2.commit()
            goto L_0x05e1
        L_0x05d8:
            r18 = r2
            r12 = r3
            r37 = r4
            r21 = r5
            r11 = r26
        L_0x05e1:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:106:0x020a A[SYNTHETIC, Splitter:B:106:0x020a] */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0224 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0280 A[SYNTHETIC, Splitter:B:112:0x0280] */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02f7 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x04b1 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04d7 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x04d9 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x04f5 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x05a5 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x05ba A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0663 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x06c5 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x06d6 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x06e2 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x070b A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0748 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0756 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0797 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x085f A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:325:0x08e3 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0936 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00ce A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:356:0x0999  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x09a0  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x09e5 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x09ef A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:392:0x09f8 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0a47 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0aef A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0af3 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0afd A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0b04 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:445:0x0b1a A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0ba4 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00fb A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0c8e A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:493:0x0cc6 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:494:0x0cdf A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x011b A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0126 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x014a A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x014f A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x015b A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x016b A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x017d A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0181 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x019b A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01b5 A[SYNTHETIC, Splitter:B:91:0x01b5] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x01e8 A[Catch:{ Exception -> 0x0d4c }] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01f4 A[Catch:{ Exception -> 0x0d4c }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r71) {
        /*
            r70 = this;
            r15 = r70
            java.lang.String r1 = "color_"
            java.lang.String r2 = "currentAccount"
            org.telegram.messenger.UserConfig r3 = r70.getUserConfig()
            boolean r3 = r3.isClientActivated()
            if (r3 == 0) goto L_0x0d52
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r15.pushMessages
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x0d52
            boolean r3 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r3 != 0) goto L_0x0024
            int r3 = r15.currentAccount
            int r4 = org.telegram.messenger.UserConfig.selectedAccount
            if (r3 == r4) goto L_0x0024
            goto L_0x0d52
        L_0x0024:
            org.telegram.tgnet.ConnectionsManager r3 = r70.getConnectionsManager()     // Catch:{ Exception -> 0x0d4c }
            r3.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0d4c }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r15.pushMessages     // Catch:{ Exception -> 0x0d4c }
            r4 = 0
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0d4c }
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3     // Catch:{ Exception -> 0x0d4c }
            r14 = r3
            org.telegram.messenger.AccountInstance r3 = r70.getAccountInstance()     // Catch:{ Exception -> 0x0d4c }
            android.content.SharedPreferences r3 = r3.getNotificationsSettings()     // Catch:{ Exception -> 0x0d4c }
            r13 = r3
            java.lang.String r3 = "dismissDate"
            int r3 = r13.getInt(r3, r4)     // Catch:{ Exception -> 0x0d4c }
            r12 = r3
            org.telegram.tgnet.TLRPC$Message r3 = r14.messageOwner     // Catch:{ Exception -> 0x0d4c }
            int r3 = r3.date     // Catch:{ Exception -> 0x0d4c }
            if (r3 > r12) goto L_0x004f
            r70.dismissNotification()     // Catch:{ Exception -> 0x0d4c }
            return
        L_0x004f:
            long r5 = r14.getDialogId()     // Catch:{ Exception -> 0x0d4c }
            r10 = r5
            r3 = 0
            org.telegram.tgnet.TLRPC$Message r7 = r14.messageOwner     // Catch:{ Exception -> 0x0d4c }
            boolean r7 = r7.mentioned     // Catch:{ Exception -> 0x0d4c }
            if (r7 == 0) goto L_0x0063
            long r7 = r14.getFromChatId()     // Catch:{ Exception -> 0x0d4c }
            r5 = r7
            r8 = r5
            goto L_0x0064
        L_0x0063:
            r8 = r5
        L_0x0064:
            int r5 = r14.getId()     // Catch:{ Exception -> 0x0d4c }
            r16 = r5
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id     // Catch:{ Exception -> 0x0d4c }
            long r5 = r5.chat_id     // Catch:{ Exception -> 0x0d4c }
            r7 = r2
            r17 = r3
            r2 = 0
            int r18 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r18 == 0) goto L_0x0080
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id     // Catch:{ Exception -> 0x0d4c }
            long r5 = r5.chat_id     // Catch:{ Exception -> 0x0d4c }
            goto L_0x0086
        L_0x0080:
            org.telegram.tgnet.TLRPC$Message r5 = r14.messageOwner     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id     // Catch:{ Exception -> 0x0d4c }
            long r5 = r5.channel_id     // Catch:{ Exception -> 0x0d4c }
        L_0x0086:
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id     // Catch:{ Exception -> 0x0d4c }
            long r2 = r4.user_id     // Catch:{ Exception -> 0x0d4c }
            boolean r4 = r14.isFromUser()     // Catch:{ Exception -> 0x0d4c }
            if (r4 == 0) goto L_0x00b2
            r19 = 0
            int r4 = (r2 > r19 ? 1 : (r2 == r19 ? 0 : -1))
            if (r4 == 0) goto L_0x00a8
            org.telegram.messenger.UserConfig r4 = r70.getUserConfig()     // Catch:{ Exception -> 0x0d4c }
            long r21 = r4.getClientUserId()     // Catch:{ Exception -> 0x0d4c }
            int r4 = (r2 > r21 ? 1 : (r2 == r21 ? 0 : -1))
            if (r4 != 0) goto L_0x00a5
            goto L_0x00a8
        L_0x00a5:
            r21 = r2
            goto L_0x00b4
        L_0x00a8:
            org.telegram.tgnet.TLRPC$Message r4 = r14.messageOwner     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$Peer r4 = r4.from_id     // Catch:{ Exception -> 0x0d4c }
            r21 = r2
            long r2 = r4.user_id     // Catch:{ Exception -> 0x0d4c }
            r3 = r2
            goto L_0x00b6
        L_0x00b2:
            r21 = r2
        L_0x00b4:
            r3 = r21
        L_0x00b6:
            org.telegram.messenger.MessagesController r2 = r70.getMessagesController()     // Catch:{ Exception -> 0x0d4c }
            r21 = r7
            java.lang.Long r7 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r7)     // Catch:{ Exception -> 0x0d4c }
            r7 = r2
            r2 = 0
            r22 = r2
            r19 = 0
            int r23 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r23 == 0) goto L_0x00fb
            org.telegram.messenger.MessagesController r2 = r70.getMessagesController()     // Catch:{ Exception -> 0x0d4c }
            r24 = r3
            java.lang.Long r3 = java.lang.Long.valueOf(r5)     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r3)     // Catch:{ Exception -> 0x0d4c }
            if (r2 != 0) goto L_0x00ea
            boolean r3 = r14.isFcmMessage()     // Catch:{ Exception -> 0x0d4c }
            if (r3 == 0) goto L_0x00ea
            boolean r3 = r14.localChannel     // Catch:{ Exception -> 0x0d4c }
            r4 = r2
            r17 = r3
            goto L_0x00ff
        L_0x00ea:
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r2)     // Catch:{ Exception -> 0x0d4c }
            if (r3 == 0) goto L_0x00f6
            boolean r3 = r2.megagroup     // Catch:{ Exception -> 0x0d4c }
            if (r3 != 0) goto L_0x00f6
            r3 = 1
            goto L_0x00f7
        L_0x00f6:
            r3 = 0
        L_0x00f7:
            r4 = r2
            r17 = r3
            goto L_0x00ff
        L_0x00fb:
            r24 = r3
            r4 = r22
        L_0x00ff:
            r2 = 0
            r3 = 0
            r22 = 0
            r26 = 0
            r27 = 0
            r28 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r29 = 0
            int r30 = r15.getNotifyOverride(r13, r8)     // Catch:{ Exception -> 0x0d4c }
            r31 = r30
            r30 = r2
            r2 = -1
            r32 = r3
            r3 = r31
            if (r3 != r2) goto L_0x0126
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r17)     // Catch:{ Exception -> 0x0d4c }
            boolean r2 = r15.isGlobalNotificationsEnabled(r10, r2)     // Catch:{ Exception -> 0x0d4c }
            r34 = r2
            goto L_0x012e
        L_0x0126:
            r2 = 2
            if (r3 == r2) goto L_0x012b
            r2 = 1
            goto L_0x012c
        L_0x012b:
            r2 = 0
        L_0x012c:
            r34 = r2
        L_0x012e:
            r2 = 1
            r19 = 0
            int r35 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r35 == 0) goto L_0x0137
            if (r4 == 0) goto L_0x0139
        L_0x0137:
            if (r7 != 0) goto L_0x0146
        L_0x0139:
            boolean r35 = r14.isFcmMessage()     // Catch:{ Exception -> 0x0d4c }
            if (r35 == 0) goto L_0x0146
            r35 = r2
            java.lang.String r2 = r14.localName     // Catch:{ Exception -> 0x0d4c }
            r36 = r2
            goto L_0x0155
        L_0x0146:
            r35 = r2
            if (r4 == 0) goto L_0x014f
            java.lang.String r2 = r4.title     // Catch:{ Exception -> 0x0d4c }
            r36 = r2
            goto L_0x0155
        L_0x014f:
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r7)     // Catch:{ Exception -> 0x0d4c }
            r36 = r2
        L_0x0155:
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0d4c }
            if (r2 != 0) goto L_0x0162
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0d4c }
            if (r2 == 0) goto L_0x0160
            goto L_0x0162
        L_0x0160:
            r2 = 0
            goto L_0x0163
        L_0x0162:
            r2 = 1
        L_0x0163:
            r37 = r2
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r10)     // Catch:{ Exception -> 0x0d4c }
            if (r2 != 0) goto L_0x017d
            androidx.collection.LongSparseArray<java.lang.Integer> r2 = r15.pushDialogs     // Catch:{ Exception -> 0x0d4c }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0d4c }
            r38 = r3
            r3 = 1
            if (r2 > r3) goto L_0x017f
            if (r37 == 0) goto L_0x0179
            goto L_0x017f
        L_0x0179:
            r2 = r36
            r3 = r2
            goto L_0x01a8
        L_0x017d:
            r38 = r3
        L_0x017f:
            if (r37 == 0) goto L_0x019b
            r2 = 0
            int r39 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r39 == 0) goto L_0x0191
            java.lang.String r2 = "NotificationHiddenChatName"
            r3 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x01a4
        L_0x0191:
            java.lang.String r2 = "NotificationHiddenName"
            r3 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x01a4
        L_0x019b:
            java.lang.String r2 = "AppName"
            r3 = 2131624316(0x7f0e017c, float:1.8875808E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ Exception -> 0x0d4c }
        L_0x01a4:
            r3 = 0
            r35 = r3
            r3 = r2
        L_0x01a8:
            int r2 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0d4c }
            r39 = r14
            java.lang.String r14 = ""
            r40 = r7
            r7 = 1
            if (r2 <= r7) goto L_0x01e8
            androidx.collection.LongSparseArray<java.lang.Integer> r2 = r15.pushDialogs     // Catch:{ Exception -> 0x0d4c }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0d4c }
            if (r2 != r7) goto L_0x01ca
            org.telegram.messenger.UserConfig r2 = r70.getUserConfig()     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$User r2 = r2.getCurrentUser()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x01e9
        L_0x01ca:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r2.<init>()     // Catch:{ Exception -> 0x0d4c }
            org.telegram.messenger.UserConfig r7 = r70.getUserConfig()     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$User r7 = r7.getCurrentUser()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = org.telegram.messenger.UserObject.getFirstName(r7)     // Catch:{ Exception -> 0x0d4c }
            r2.append(r7)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = "・"
            r2.append(r7)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0d4c }
            goto L_0x01e9
        L_0x01e8:
            r2 = r14
        L_0x01e9:
            androidx.collection.LongSparseArray<java.lang.Integer> r7 = r15.pushDialogs     // Catch:{ Exception -> 0x0d4c }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0d4c }
            r41 = r5
            r5 = 1
            if (r7 != r5) goto L_0x01ff
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            r6 = 23
            if (r5 >= r6) goto L_0x01fb
            goto L_0x01ff
        L_0x01fb:
            r45 = r1
            r7 = r2
            goto L_0x025d
        L_0x01ff:
            androidx.collection.LongSparseArray<java.lang.Integer> r5 = r15.pushDialogs     // Catch:{ Exception -> 0x0d4c }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r6 = "NewMessages"
            r7 = 1
            if (r5 != r7) goto L_0x0224
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r5.<init>()     // Catch:{ Exception -> 0x0d4c }
            r5.append(r2)     // Catch:{ Exception -> 0x0d4c }
            int r7 = r15.total_unread_count     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r6, r7)     // Catch:{ Exception -> 0x0d4c }
            r5.append(r6)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0d4c }
            r2 = r5
            r45 = r1
            r7 = r2
            goto L_0x025d
        L_0x0224:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r5.<init>()     // Catch:{ Exception -> 0x0d4c }
            r5.append(r2)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = "NotificationMessagesPeopleDisplayOrder"
            r43 = r2
            r45 = r1
            r2 = 2
            java.lang.Object[] r1 = new java.lang.Object[r2]     // Catch:{ Exception -> 0x0d4c }
            int r2 = r15.total_unread_count     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)     // Catch:{ Exception -> 0x0d4c }
            r6 = 0
            r1[r6] = r2     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = "FromChats"
            androidx.collection.LongSparseArray<java.lang.Integer> r6 = r15.pushDialogs     // Catch:{ Exception -> 0x0d4c }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r6)     // Catch:{ Exception -> 0x0d4c }
            r6 = 1
            r1[r6] = r2     // Catch:{ Exception -> 0x0d4c }
            r2 = 2131626857(0x7f0e0b69, float:1.8880962E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r7, r2, r1)     // Catch:{ Exception -> 0x0d4c }
            r5.append(r1)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x0d4c }
            r2 = r1
            r7 = r2
        L_0x025d:
            androidx.core.app.NotificationCompat$Builder r1 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0d4c }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            r1.<init>(r2)     // Catch:{ Exception -> 0x0d4c }
            r6 = r1
            r1 = 2
            r2 = 0
            r43 = 0
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r15.pushMessages     // Catch:{ Exception -> 0x0d4c }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0d4c }
            r44 = r1
            java.lang.String r1 = ": "
            r46 = r2
            java.lang.String r2 = " "
            r47 = r13
            java.lang.String r13 = " @ "
            r48 = r8
            r8 = 1
            if (r5 != r8) goto L_0x02f7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r15.pushMessages     // Catch:{ Exception -> 0x0d4c }
            r8 = 0
            java.lang.Object r5 = r5.get(r8)     // Catch:{ Exception -> 0x0d4c }
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5     // Catch:{ Exception -> 0x0d4c }
            r8 = 1
            boolean[] r9 = new boolean[r8]     // Catch:{ Exception -> 0x0d4c }
            r8 = r9
            r52 = r10
            r9 = 0
            r10 = 0
            java.lang.String r11 = r15.getStringForMessage(r5, r10, r8, r9)     // Catch:{ Exception -> 0x0d4c }
            r9 = r11
            r10 = r11
            boolean r11 = r15.isSilentMessage(r5)     // Catch:{ Exception -> 0x0d4c }
            if (r10 != 0) goto L_0x029e
            return
        L_0x029e:
            if (r35 == 0) goto L_0x02e5
            if (r4 == 0) goto L_0x02b7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r1.<init>()     // Catch:{ Exception -> 0x0d4c }
            r1.append(r13)     // Catch:{ Exception -> 0x0d4c }
            r1.append(r3)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r10.replace(r1, r14)     // Catch:{ Exception -> 0x0d4c }
            r10 = r1
            goto L_0x02e5
        L_0x02b7:
            r13 = 0
            boolean r44 = r8[r13]     // Catch:{ Exception -> 0x0d4c }
            if (r44 == 0) goto L_0x02d1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r2.<init>()     // Catch:{ Exception -> 0x0d4c }
            r2.append(r3)     // Catch:{ Exception -> 0x0d4c }
            r2.append(r1)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r10.replace(r1, r14)     // Catch:{ Exception -> 0x0d4c }
            r10 = r1
            goto L_0x02e5
        L_0x02d1:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r1.<init>()     // Catch:{ Exception -> 0x0d4c }
            r1.append(r3)     // Catch:{ Exception -> 0x0d4c }
            r1.append(r2)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r10.replace(r1, r14)     // Catch:{ Exception -> 0x0d4c }
            r10 = r1
        L_0x02e5:
            r6.setContentText(r10)     // Catch:{ Exception -> 0x0d4c }
            androidx.core.app.NotificationCompat$BigTextStyle r1 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0d4c }
            r1.<init>()     // Catch:{ Exception -> 0x0d4c }
            androidx.core.app.NotificationCompat$BigTextStyle r1 = r1.bigText(r10)     // Catch:{ Exception -> 0x0d4c }
            r6.setStyle(r1)     // Catch:{ Exception -> 0x0d4c }
            r13 = r11
            goto L_0x03b7
        L_0x02f7:
            r52 = r10
            r6.setContentText(r7)     // Catch:{ Exception -> 0x0d4c }
            androidx.core.app.NotificationCompat$InboxStyle r5 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0d4c }
            r5.<init>()     // Catch:{ Exception -> 0x0d4c }
            r5.setBigContentTitle(r3)     // Catch:{ Exception -> 0x0d4c }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r15.pushMessages     // Catch:{ Exception -> 0x0d4c }
            int r8 = r8.size()     // Catch:{ Exception -> 0x0d4c }
            r9 = 10
            int r8 = java.lang.Math.min(r9, r8)     // Catch:{ Exception -> 0x0d4c }
            r9 = 1
            boolean[] r10 = new boolean[r9]     // Catch:{ Exception -> 0x0d4c }
            r9 = r10
            r10 = 0
            r11 = r10
            r10 = r44
        L_0x0318:
            if (r11 >= r8) goto L_0x03a5
            r44 = r8
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r15.pushMessages     // Catch:{ Exception -> 0x0d4c }
            java.lang.Object r8 = r8.get(r11)     // Catch:{ Exception -> 0x0d4c }
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8     // Catch:{ Exception -> 0x0d4c }
            r54 = r6
            r55 = r7
            r6 = 0
            r7 = 0
            java.lang.String r56 = r15.getStringForMessage(r8, r7, r9, r6)     // Catch:{ Exception -> 0x0d4c }
            r6 = r56
            if (r6 == 0) goto L_0x0399
            org.telegram.tgnet.TLRPC$Message r7 = r8.messageOwner     // Catch:{ Exception -> 0x0d4c }
            int r7 = r7.date     // Catch:{ Exception -> 0x0d4c }
            if (r7 > r12) goto L_0x0339
            goto L_0x039b
        L_0x0339:
            r7 = 2
            if (r10 != r7) goto L_0x0343
            r46 = r6
            boolean r7 = r15.isSilentMessage(r8)     // Catch:{ Exception -> 0x0d4c }
            r10 = r7
        L_0x0343:
            androidx.collection.LongSparseArray<java.lang.Integer> r7 = r15.pushDialogs     // Catch:{ Exception -> 0x0d4c }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0d4c }
            r56 = r8
            r8 = 1
            if (r7 != r8) goto L_0x0395
            if (r35 == 0) goto L_0x0395
            if (r4 == 0) goto L_0x0367
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r7.<init>()     // Catch:{ Exception -> 0x0d4c }
            r7.append(r13)     // Catch:{ Exception -> 0x0d4c }
            r7.append(r3)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = r6.replace(r7, r14)     // Catch:{ Exception -> 0x0d4c }
            r6 = r7
            goto L_0x0395
        L_0x0367:
            r7 = 0
            boolean r8 = r9[r7]     // Catch:{ Exception -> 0x0d4c }
            if (r8 == 0) goto L_0x0381
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r7.<init>()     // Catch:{ Exception -> 0x0d4c }
            r7.append(r3)     // Catch:{ Exception -> 0x0d4c }
            r7.append(r1)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = r6.replace(r7, r14)     // Catch:{ Exception -> 0x0d4c }
            r6 = r7
            goto L_0x0395
        L_0x0381:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r7.<init>()     // Catch:{ Exception -> 0x0d4c }
            r7.append(r3)     // Catch:{ Exception -> 0x0d4c }
            r7.append(r2)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = r6.replace(r7, r14)     // Catch:{ Exception -> 0x0d4c }
            r6 = r7
        L_0x0395:
            r5.addLine(r6)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x039b
        L_0x0399:
            r56 = r8
        L_0x039b:
            int r11 = r11 + 1
            r8 = r44
            r6 = r54
            r7 = r55
            goto L_0x0318
        L_0x03a5:
            r54 = r6
            r55 = r7
            r44 = r8
            r5.setSummaryText(r7)     // Catch:{ Exception -> 0x0d4c }
            r6 = r54
            r6.setStyle(r5)     // Catch:{ Exception -> 0x0d4c }
            r13 = r10
            r9 = r46
        L_0x03b7:
            if (r71 == 0) goto L_0x03c8
            if (r34 == 0) goto L_0x03c8
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0d4c }
            boolean r1 = r1.isRecordingAudio()     // Catch:{ Exception -> 0x0d4c }
            if (r1 != 0) goto L_0x03c8
            r1 = 1
            if (r13 != r1) goto L_0x03cb
        L_0x03c8:
            r1 = 1
            r32 = r1
        L_0x03cb:
            java.lang.String r1 = "custom_"
            if (r32 != 0) goto L_0x04a1
            int r2 = (r52 > r48 ? 1 : (r52 == r48 ? 0 : -1))
            if (r2 != 0) goto L_0x04a1
            if (r4 == 0) goto L_0x04a1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r2.<init>()     // Catch:{ Exception -> 0x0d4c }
            r2.append(r1)     // Catch:{ Exception -> 0x0d4c }
            r10 = r52
            r2.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0d4c }
            r8 = r47
            r5 = 0
            boolean r2 = r8.getBoolean(r2, r5)     // Catch:{ Exception -> 0x0d4c }
            if (r2 == 0) goto L_0x0422
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r2.<init>()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r5 = "smart_max_count_"
            r2.append(r5)     // Catch:{ Exception -> 0x0d4c }
            r2.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0d4c }
            r5 = 2
            int r2 = r8.getInt(r2, r5)     // Catch:{ Exception -> 0x0d4c }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r5.<init>()     // Catch:{ Exception -> 0x0d4c }
            r44 = r2
            java.lang.String r2 = "smart_delay_"
            r5.append(r2)     // Catch:{ Exception -> 0x0d4c }
            r5.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = r5.toString()     // Catch:{ Exception -> 0x0d4c }
            r5 = 180(0xb4, float:2.52E-43)
            int r2 = r8.getInt(r2, r5)     // Catch:{ Exception -> 0x0d4c }
            r5 = r2
            r2 = r44
            goto L_0x0425
        L_0x0422:
            r2 = 2
            r5 = 180(0xb4, float:2.52E-43)
        L_0x0425:
            if (r2 == 0) goto L_0x0494
            r44 = r12
            androidx.collection.LongSparseArray<android.graphics.Point> r12 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0d4c }
            java.lang.Object r12 = r12.get(r10)     // Catch:{ Exception -> 0x0d4c }
            android.graphics.Point r12 = (android.graphics.Point) r12     // Catch:{ Exception -> 0x0d4c }
            if (r12 != 0) goto L_0x0451
            r46 = r7
            android.graphics.Point r7 = new android.graphics.Point     // Catch:{ Exception -> 0x0d4c }
            long r52 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0d4c }
            r56 = r13
            r47 = r14
            r54 = 1000(0x3e8, double:4.94E-321)
            long r13 = r52 / r54
            int r14 = (int) r13     // Catch:{ Exception -> 0x0d4c }
            r13 = 1
            r7.<init>(r13, r14)     // Catch:{ Exception -> 0x0d4c }
            androidx.collection.LongSparseArray<android.graphics.Point> r12 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0d4c }
            r12.put(r10, r7)     // Catch:{ Exception -> 0x0d4c }
            r57 = r3
            goto L_0x04af
        L_0x0451:
            r46 = r7
            r56 = r13
            r47 = r14
            int r7 = r12.y     // Catch:{ Exception -> 0x0d4c }
            int r13 = r7 + r5
            long r13 = (long) r13     // Catch:{ Exception -> 0x0d4c }
            long r52 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0d4c }
            r54 = 1000(0x3e8, double:4.94E-321)
            long r52 = r52 / r54
            int r57 = (r13 > r52 ? 1 : (r13 == r52 ? 0 : -1))
            if (r57 >= 0) goto L_0x0476
            long r13 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0d4c }
            long r13 = r13 / r54
            int r14 = (int) r13     // Catch:{ Exception -> 0x0d4c }
            r13 = 1
            r12.set(r13, r14)     // Catch:{ Exception -> 0x0d4c }
            r57 = r3
            goto L_0x04af
        L_0x0476:
            int r13 = r12.x     // Catch:{ Exception -> 0x0d4c }
            if (r13 >= r2) goto L_0x048d
            int r14 = r13 + 1
            long r52 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0d4c }
            r58 = r2
            r57 = r3
            r54 = 1000(0x3e8, double:4.94E-321)
            long r2 = r52 / r54
            int r3 = (int) r2     // Catch:{ Exception -> 0x0d4c }
            r12.set(r14, r3)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x04af
        L_0x048d:
            r58 = r2
            r57 = r3
            r32 = 1
            goto L_0x04af
        L_0x0494:
            r58 = r2
            r57 = r3
            r46 = r7
            r44 = r12
            r56 = r13
            r47 = r14
            goto L_0x04af
        L_0x04a1:
            r57 = r3
            r46 = r7
            r44 = r12
            r56 = r13
            r8 = r47
            r10 = r52
            r47 = r14
        L_0x04af:
            if (r32 != 0) goto L_0x04cb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r2.<init>()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r3 = "sound_enabled_"
            r2.append(r3)     // Catch:{ Exception -> 0x0d4c }
            r2.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0d4c }
            r3 = 1
            boolean r2 = r8.getBoolean(r2, r3)     // Catch:{ Exception -> 0x0d4c }
            if (r2 != 0) goto L_0x04cb
            r32 = 1
        L_0x04cb:
            android.net.Uri r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = r2.getPath()     // Catch:{ Exception -> 0x0d4c }
            r14 = r2
            r2 = 1
            boolean r3 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0d4c }
            if (r3 != 0) goto L_0x04d9
            r3 = 1
            goto L_0x04da
        L_0x04d9:
            r3 = 0
        L_0x04da:
            r52 = r3
            r3 = 1
            r5 = 0
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r7.<init>()     // Catch:{ Exception -> 0x0d4c }
            r7.append(r1)     // Catch:{ Exception -> 0x0d4c }
            r7.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r7.toString()     // Catch:{ Exception -> 0x0d4c }
            r7 = 0
            boolean r1 = r8.getBoolean(r1, r7)     // Catch:{ Exception -> 0x0d4c }
            r7 = 3
            if (r1 == 0) goto L_0x05a5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r1.<init>()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r12 = "vibrate_"
            r1.append(r12)     // Catch:{ Exception -> 0x0d4c }
            r1.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d4c }
            r12 = 0
            int r1 = r8.getInt(r1, r12)     // Catch:{ Exception -> 0x0d4c }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r12.<init>()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r13 = "priority_"
            r12.append(r13)     // Catch:{ Exception -> 0x0d4c }
            r12.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0d4c }
            int r12 = r8.getInt(r12, r7)     // Catch:{ Exception -> 0x0d4c }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r13.<init>()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = "sound_document_id_"
            r13.append(r7)     // Catch:{ Exception -> 0x0d4c }
            r13.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = r13.toString()     // Catch:{ Exception -> 0x0d4c }
            r58 = r1
            r13 = r2
            r1 = 0
            long r19 = r8.getLong(r7, r1)     // Catch:{ Exception -> 0x0d4c }
            r59 = r19
            r7 = r12
            r61 = r13
            r12 = r59
            int r59 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r59 == 0) goto L_0x0552
            r1 = 1
            org.telegram.messenger.MediaDataController r2 = r70.getMediaDataController()     // Catch:{ Exception -> 0x0d4c }
            org.telegram.messenger.ringtone.RingtoneDataStore r2 = r2.ringtoneDataStore     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = r2.getSoundPath(r12)     // Catch:{ Exception -> 0x0d4c }
            r5 = r1
            goto L_0x0569
        L_0x0552:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r1.<init>()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r2 = "sound_path_"
            r1.append(r2)     // Catch:{ Exception -> 0x0d4c }
            r1.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d4c }
            r2 = 0
            java.lang.String r1 = r8.getString(r1, r2)     // Catch:{ Exception -> 0x0d4c }
            r2 = r1
        L_0x0569:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r1.<init>()     // Catch:{ Exception -> 0x0d4c }
            r59 = r2
            r2 = r45
            r1.append(r2)     // Catch:{ Exception -> 0x0d4c }
            r1.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d4c }
            boolean r1 = r8.contains(r1)     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x059b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r1.<init>()     // Catch:{ Exception -> 0x0d4c }
            r1.append(r2)     // Catch:{ Exception -> 0x0d4c }
            r1.append(r10)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d4c }
            r2 = 0
            int r1 = r8.getInt(r1, r2)     // Catch:{ Exception -> 0x0d4c }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x059c
        L_0x059b:
            r1 = 0
        L_0x059c:
            r45 = r1
            r13 = r58
            r12 = r59
            r58 = r5
            goto L_0x05b2
        L_0x05a5:
            r61 = r2
            r1 = 0
            r12 = 3
            r2 = 0
            r7 = 0
            r13 = r1
            r58 = r5
            r45 = r7
            r7 = r12
            r12 = r2
        L_0x05b2:
            r1 = 0
            r5 = r3
            r2 = 0
            int r19 = (r41 > r2 ? 1 : (r41 == r2 ? 0 : -1))
            if (r19 == 0) goto L_0x0663
            if (r17 == 0) goto L_0x0612
            java.lang.String r2 = "ChannelSoundDocId"
            r3 = r5
            r60 = r6
            r5 = 0
            long r19 = r8.getLong(r2, r5)     // Catch:{ Exception -> 0x0d4c }
            r62 = r19
            r64 = r1
            r1 = r62
            int r62 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r62 == 0) goto L_0x05e0
            r27 = 1
            org.telegram.messenger.MediaDataController r5 = r70.getMediaDataController()     // Catch:{ Exception -> 0x0d4c }
            org.telegram.messenger.ringtone.RingtoneDataStore r5 = r5.ringtoneDataStore     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r5 = r5.getSoundPath(r1)     // Catch:{ Exception -> 0x0d4c }
            r26 = r5
            goto L_0x05e8
        L_0x05e0:
            java.lang.String r5 = "ChannelSoundPath"
            java.lang.String r5 = r8.getString(r5, r14)     // Catch:{ Exception -> 0x0d4c }
            r26 = r5
        L_0x05e8:
            java.lang.String r5 = "vibrate_channel"
            r6 = 0
            int r5 = r8.getInt(r5, r6)     // Catch:{ Exception -> 0x0d4c }
            r22 = r5
            java.lang.String r5 = "priority_channel"
            r6 = 1
            int r5 = r8.getInt(r5, r6)     // Catch:{ Exception -> 0x0d4c }
            r29 = r5
            java.lang.String r5 = "ChannelLed"
            r6 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r5 = r8.getInt(r5, r6)     // Catch:{ Exception -> 0x0d4c }
            r28 = r5
            r3 = 2
            r1 = r22
            r2 = r26
            r5 = r29
            r22 = r3
            r3 = r28
            goto L_0x06c2
        L_0x0612:
            r64 = r1
            r3 = r5
            r60 = r6
            java.lang.String r1 = "GroupSoundDocId"
            r5 = 0
            long r1 = r8.getLong(r1, r5)     // Catch:{ Exception -> 0x0d4c }
            int r62 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r62 == 0) goto L_0x0632
            r27 = 1
            org.telegram.messenger.MediaDataController r5 = r70.getMediaDataController()     // Catch:{ Exception -> 0x0d4c }
            org.telegram.messenger.ringtone.RingtoneDataStore r5 = r5.ringtoneDataStore     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r5 = r5.getSoundPath(r1)     // Catch:{ Exception -> 0x0d4c }
            r26 = r5
            goto L_0x063a
        L_0x0632:
            java.lang.String r5 = "GroupSoundPath"
            java.lang.String r5 = r8.getString(r5, r14)     // Catch:{ Exception -> 0x0d4c }
            r26 = r5
        L_0x063a:
            java.lang.String r5 = "vibrate_group"
            r6 = 0
            int r5 = r8.getInt(r5, r6)     // Catch:{ Exception -> 0x0d4c }
            r22 = r5
            java.lang.String r5 = "priority_group"
            r6 = 1
            int r5 = r8.getInt(r5, r6)     // Catch:{ Exception -> 0x0d4c }
            r29 = r5
            java.lang.String r5 = "GroupLed"
            r6 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r5 = r8.getInt(r5, r6)     // Catch:{ Exception -> 0x0d4c }
            r28 = r5
            r3 = 0
            r1 = r22
            r2 = r26
            r5 = r29
            r22 = r3
            r3 = r28
            goto L_0x06c2
        L_0x0663:
            r64 = r1
            r3 = r5
            r60 = r6
            r1 = 0
            int r5 = (r24 > r1 ? 1 : (r24 == r1 ? 0 : -1))
            if (r5 == 0) goto L_0x06b8
            java.lang.String r5 = "GlobalSoundDocId"
            long r5 = r8.getLong(r5, r1)     // Catch:{ Exception -> 0x0d4c }
            int r62 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r62 == 0) goto L_0x0687
            r27 = 1
            org.telegram.messenger.MediaDataController r1 = r70.getMediaDataController()     // Catch:{ Exception -> 0x0d4c }
            org.telegram.messenger.ringtone.RingtoneDataStore r1 = r1.ringtoneDataStore     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r1.getSoundPath(r5)     // Catch:{ Exception -> 0x0d4c }
            r26 = r1
            goto L_0x068f
        L_0x0687:
            java.lang.String r1 = "GlobalSoundPath"
            java.lang.String r1 = r8.getString(r1, r14)     // Catch:{ Exception -> 0x0d4c }
            r26 = r1
        L_0x068f:
            java.lang.String r1 = "vibrate_messages"
            r2 = 0
            int r1 = r8.getInt(r1, r2)     // Catch:{ Exception -> 0x0d4c }
            r22 = r1
            java.lang.String r1 = "priority_messages"
            r2 = 1
            int r1 = r8.getInt(r1, r2)     // Catch:{ Exception -> 0x0d4c }
            r29 = r1
            java.lang.String r1 = "MessagesLed"
            r2 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r8.getInt(r1, r2)     // Catch:{ Exception -> 0x0d4c }
            r28 = r1
            r3 = 1
            r1 = r22
            r2 = r26
            r5 = r29
            r22 = r3
            r3 = r28
            goto L_0x06c2
        L_0x06b8:
            r1 = r22
            r2 = r26
            r5 = r29
            r22 = r3
            r3 = r28
        L_0x06c2:
            r6 = 4
            if (r1 != r6) goto L_0x06ca
            r26 = 1
            r1 = 0
            r64 = r26
        L_0x06ca:
            boolean r26 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x0d4c }
            if (r26 != 0) goto L_0x06e2
            boolean r26 = android.text.TextUtils.equals(r2, r12)     // Catch:{ Exception -> 0x0d4c }
            if (r26 != 0) goto L_0x06e2
            r27 = r58
            r2 = r12
            r26 = 0
            r69 = r26
            r26 = r2
            r2 = r69
            goto L_0x06e6
        L_0x06e2:
            r26 = r2
            r2 = r61
        L_0x06e6:
            r6 = 3
            if (r7 == r6) goto L_0x06ed
            if (r5 == r7) goto L_0x06ed
            r5 = r7
            r2 = 0
        L_0x06ed:
            if (r45 == 0) goto L_0x06fb
            int r6 = r45.intValue()     // Catch:{ Exception -> 0x0d4c }
            if (r6 == r3) goto L_0x06fb
            int r6 = r45.intValue()     // Catch:{ Exception -> 0x0d4c }
            r3 = r6
            r2 = 0
        L_0x06fb:
            if (r13 == 0) goto L_0x0707
            r6 = 4
            if (r13 == r6) goto L_0x0707
            if (r13 == r1) goto L_0x0707
            r1 = r13
            r2 = 0
            r29 = r2
            goto L_0x0709
        L_0x0707:
            r29 = r2
        L_0x0709:
            if (r52 == 0) goto L_0x072f
            java.lang.String r2 = "EnableInAppSounds"
            r6 = 1
            boolean r2 = r8.getBoolean(r2, r6)     // Catch:{ Exception -> 0x0d4c }
            if (r2 != 0) goto L_0x0716
            r26 = 0
        L_0x0716:
            java.lang.String r2 = "EnableInAppVibrate"
            r6 = 1
            boolean r2 = r8.getBoolean(r2, r6)     // Catch:{ Exception -> 0x0d4c }
            if (r2 != 0) goto L_0x0720
            r1 = 2
        L_0x0720:
            java.lang.String r2 = "EnableInAppPriority"
            r6 = 0
            boolean r2 = r8.getBoolean(r2, r6)     // Catch:{ Exception -> 0x0d4c }
            if (r2 != 0) goto L_0x072b
            r5 = 0
            goto L_0x072f
        L_0x072b:
            r2 = 2
            if (r5 != r2) goto L_0x072f
            r5 = 1
        L_0x072f:
            if (r64 == 0) goto L_0x0746
            r2 = 2
            if (r1 == r2) goto L_0x0746
            android.media.AudioManager r2 = audioManager     // Catch:{ Exception -> 0x0741 }
            int r2 = r2.getRingerMode()     // Catch:{ Exception -> 0x0741 }
            if (r2 == 0) goto L_0x0740
            r6 = 1
            if (r2 == r6) goto L_0x0740
            r1 = 2
        L_0x0740:
            goto L_0x0746
        L_0x0741:
            r0 = move-exception
            r2 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x0d4c }
        L_0x0746:
            if (r32 == 0) goto L_0x0756
            r1 = 0
            r5 = 0
            r3 = 0
            r26 = 0
            r6 = r1
            r2 = r26
            r69 = r5
            r5 = r3
            r3 = r69
            goto L_0x075e
        L_0x0756:
            r6 = r1
            r2 = r26
            r69 = r5
            r5 = r3
            r3 = r69
        L_0x075e:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0d4c }
            r26 = r7
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            r59 = r12
            java.lang.Class<org.telegram.ui.LaunchActivity> r12 = org.telegram.ui.LaunchActivity.class
            r1.<init>(r7, r12)     // Catch:{ Exception -> 0x0d4c }
            r12 = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r1.<init>()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = "com.tmessages.openchat"
            r1.append(r7)     // Catch:{ Exception -> 0x0d4c }
            r61 = r6
            double r6 = java.lang.Math.random()     // Catch:{ Exception -> 0x0d4c }
            r1.append(r6)     // Catch:{ Exception -> 0x0d4c }
            r6 = 2147483647(0x7fffffff, float:NaN)
            r1.append(r6)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d4c }
            r12.setAction(r1)     // Catch:{ Exception -> 0x0d4c }
            r1 = 67108864(0x4000000, float:1.5046328E-36)
            r12.setFlags(r1)     // Catch:{ Exception -> 0x0d4c }
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r10)     // Catch:{ Exception -> 0x0d4c }
            if (r1 != 0) goto L_0x085f
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0d4c }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0d4c }
            r6 = 1
            if (r1 != r6) goto L_0x07c9
            r6 = 0
            int r1 = (r41 > r6 ? 1 : (r41 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x07b2
            java.lang.String r1 = "chatId"
            r6 = r41
            r12.putExtra(r1, r6)     // Catch:{ Exception -> 0x0d4c }
            r41 = r6
            r6 = r24
            goto L_0x07cb
        L_0x07b2:
            r6 = r41
            r19 = 0
            int r1 = (r24 > r19 ? 1 : (r24 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x07c4
            java.lang.String r1 = "userId"
            r41 = r6
            r6 = r24
            r12.putExtra(r1, r6)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x07cb
        L_0x07c4:
            r41 = r6
            r6 = r24
            goto L_0x07cb
        L_0x07c9:
            r6 = r24
        L_0x07cb:
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0d4c }
            if (r1 != 0) goto L_0x0856
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x07dd
            r24 = r6
            r7 = r40
            r40 = r5
            goto L_0x085c
        L_0x07dd:
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0d4c }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0d4c }
            r24 = r6
            r6 = 1
            if (r1 != r6) goto L_0x0851
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            r6 = 28
            if (r1 >= r6) goto L_0x0851
            if (r4 == 0) goto L_0x081f
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x0819
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x0819
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0d4c }
            long r6 = r1.volume_id     // Catch:{ Exception -> 0x0d4c }
            r19 = 0
            int r1 = (r6 > r19 ? 1 : (r6 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x0819
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0d4c }
            int r1 = r1.local_id     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x0819
            org.telegram.tgnet.TLRPC$ChatPhoto r1 = r4.photo     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0d4c }
            r6 = r1
            r7 = r40
            r40 = r5
            goto L_0x087d
        L_0x0819:
            r7 = r40
            r40 = r5
            goto L_0x087b
        L_0x081f:
            if (r40 == 0) goto L_0x084c
            r7 = r40
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r7.photo     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x0849
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r7.photo     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x0849
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r7.photo     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0d4c }
            r40 = r5
            long r5 = r1.volume_id     // Catch:{ Exception -> 0x0d4c }
            r19 = 0
            int r1 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x087b
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r7.photo     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0d4c }
            int r1 = r1.local_id     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x087b
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r7.photo     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small     // Catch:{ Exception -> 0x0d4c }
            r6 = r1
            goto L_0x087d
        L_0x0849:
            r40 = r5
            goto L_0x087b
        L_0x084c:
            r7 = r40
            r40 = r5
            goto L_0x087b
        L_0x0851:
            r7 = r40
            r40 = r5
            goto L_0x087b
        L_0x0856:
            r24 = r6
            r7 = r40
            r40 = r5
        L_0x085c:
            r1 = 0
            r6 = r1
            goto L_0x087d
        L_0x085f:
            r7 = r40
            r40 = r5
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0d4c }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0d4c }
            r5 = 1
            if (r1 != r5) goto L_0x087b
            long r5 = globalSecretChatId     // Catch:{ Exception -> 0x0d4c }
            int r1 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r1 == 0) goto L_0x087b
            java.lang.String r1 = "encId"
            int r5 = org.telegram.messenger.DialogObject.getEncryptedChatId(r10)     // Catch:{ Exception -> 0x0d4c }
            r12.putExtra(r1, r5)     // Catch:{ Exception -> 0x0d4c }
        L_0x087b:
            r6 = r30
        L_0x087d:
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0d4c }
            r5 = r21
            r12.putExtra(r5, r1)     // Catch:{ Exception -> 0x0d4c }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            r21 = r13
            r13 = 1073741824(0x40000000, float:2.0)
            r62 = r10
            r10 = 0
            android.app.PendingIntent r1 = android.app.PendingIntent.getActivity(r1, r10, r12, r13)     // Catch:{ Exception -> 0x0d4c }
            r13 = r1
            r11 = r57
            r10 = r60
            androidx.core.app.NotificationCompat$Builder r1 = r10.setContentTitle(r11)     // Catch:{ Exception -> 0x0d4c }
            r57 = r11
            r11 = 2131165961(0x7var_, float:1.7946154E38)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setSmallIcon((int) r11)     // Catch:{ Exception -> 0x0d4c }
            r11 = 1
            androidx.core.app.NotificationCompat$Builder r1 = r1.setAutoCancel(r11)     // Catch:{ Exception -> 0x0d4c }
            int r11 = r15.total_unread_count     // Catch:{ Exception -> 0x0d4c }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setNumber(r11)     // Catch:{ Exception -> 0x0d4c }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setContentIntent(r13)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r11 = r15.notificationGroup     // Catch:{ Exception -> 0x0d4c }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setGroup(r11)     // Catch:{ Exception -> 0x0d4c }
            r11 = 1
            androidx.core.app.NotificationCompat$Builder r1 = r1.setGroupSummary(r11)     // Catch:{ Exception -> 0x0d4c }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setShowWhen(r11)     // Catch:{ Exception -> 0x0d4c }
            r30 = r12
            r11 = r39
            org.telegram.tgnet.TLRPC$Message r12 = r11.messageOwner     // Catch:{ Exception -> 0x0d4c }
            int r12 = r12.date     // Catch:{ Exception -> 0x0d4c }
            r39 = r13
            long r12 = (long) r12     // Catch:{ Exception -> 0x0d4c }
            r54 = 1000(0x3e8, double:4.94E-321)
            long r12 = r12 * r54
            androidx.core.app.NotificationCompat$Builder r1 = r1.setWhen(r12)     // Catch:{ Exception -> 0x0d4c }
            r12 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r1.setColor(r12)     // Catch:{ Exception -> 0x0d4c }
            r1 = 0
            r12 = 0
            java.lang.String r13 = "msg"
            r10.setCategory(r13)     // Catch:{ Exception -> 0x0d4c }
            if (r4 != 0) goto L_0x090a
            if (r7 == 0) goto L_0x090a
            java.lang.String r13 = r7.phone     // Catch:{ Exception -> 0x0d4c }
            if (r13 == 0) goto L_0x090a
            java.lang.String r13 = r7.phone     // Catch:{ Exception -> 0x0d4c }
            int r13 = r13.length()     // Catch:{ Exception -> 0x0d4c }
            if (r13 <= 0) goto L_0x090a
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r13.<init>()     // Catch:{ Exception -> 0x0d4c }
            r60 = r1
            java.lang.String r1 = "tel:+"
            r13.append(r1)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r7.phone     // Catch:{ Exception -> 0x0d4c }
            r13.append(r1)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r13.toString()     // Catch:{ Exception -> 0x0d4c }
            r10.addPerson((java.lang.String) r1)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x090c
        L_0x090a:
            r60 = r1
        L_0x090c:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0d4c }
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            r65 = r4
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r4 = org.telegram.messenger.NotificationDismissReceiver.class
            r1.<init>(r13, r4)     // Catch:{ Exception -> 0x0d4c }
            r13 = r1
            java.lang.String r1 = "messageDate"
            org.telegram.tgnet.TLRPC$Message r4 = r11.messageOwner     // Catch:{ Exception -> 0x0d4c }
            int r4 = r4.date     // Catch:{ Exception -> 0x0d4c }
            r13.putExtra(r1, r4)     // Catch:{ Exception -> 0x0d4c }
            int r1 = r15.currentAccount     // Catch:{ Exception -> 0x0d4c }
            r13.putExtra(r5, r1)     // Catch:{ Exception -> 0x0d4c }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r66 = r7
            r7 = 1
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r1, r7, r13, r4)     // Catch:{ Exception -> 0x0d4c }
            r10.setDeleteIntent(r1)     // Catch:{ Exception -> 0x0d4c }
            if (r6 == 0) goto L_0x0999
            org.telegram.messenger.ImageLoader r1 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r7 = "50_50"
            r4 = 0
            android.graphics.drawable.BitmapDrawable r1 = r1.getImageFromMemory(r6, r4, r7)     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x094d
            android.graphics.Bitmap r4 = r1.getBitmap()     // Catch:{ Exception -> 0x0d4c }
            r10.setLargeIcon(r4)     // Catch:{ Exception -> 0x0d4c }
            r68 = r6
            goto L_0x099b
        L_0x094d:
            r4 = 1
            java.io.File r7 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r4)     // Catch:{ all -> 0x0993 }
            r4 = r7
            boolean r7 = r4.exists()     // Catch:{ all -> 0x0993 }
            if (r7 == 0) goto L_0x098e
            r51 = 1112014848(0x42480000, float:50.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r51)     // Catch:{ all -> 0x0993 }
            float r7 = (float) r7     // Catch:{ all -> 0x0993 }
            r51 = 1126170624(0x43200000, float:160.0)
            float r7 = r51 / r7
            android.graphics.BitmapFactory$Options r51 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0993 }
            r51.<init>()     // Catch:{ all -> 0x0993 }
            r67 = r51
            r51 = 1065353216(0x3var_, float:1.0)
            int r51 = (r7 > r51 ? 1 : (r7 == r51 ? 0 : -1))
            if (r51 >= 0) goto L_0x0975
            r51 = r1
            r1 = 1
            goto L_0x0978
        L_0x0975:
            r51 = r1
            int r1 = (int) r7
        L_0x0978:
            r68 = r6
            r6 = r67
            r6.inSampleSize = r1     // Catch:{ all -> 0x098c }
            java.lang.String r1 = r4.getAbsolutePath()     // Catch:{ all -> 0x098c }
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r1, r6)     // Catch:{ all -> 0x098c }
            if (r1 == 0) goto L_0x0992
            r10.setLargeIcon(r1)     // Catch:{ all -> 0x098c }
            goto L_0x0992
        L_0x098c:
            r0 = move-exception
            goto L_0x099b
        L_0x098e:
            r51 = r1
            r68 = r6
        L_0x0992:
            goto L_0x099b
        L_0x0993:
            r0 = move-exception
            r51 = r1
            r68 = r6
            goto L_0x099b
        L_0x0999:
            r68 = r6
        L_0x099b:
            r1 = 0
            r6 = 26
            if (r71 == 0) goto L_0x09e5
            r7 = r56
            r4 = 1
            if (r7 != r4) goto L_0x09a6
            goto L_0x09e7
        L_0x09a6:
            if (r3 != 0) goto L_0x09b4
            r4 = 0
            r10.setPriority(r4)     // Catch:{ Exception -> 0x0d4c }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            if (r4 < r6) goto L_0x09f3
            r1 = 3
            r31 = r1
            goto L_0x09f5
        L_0x09b4:
            r4 = 1
            if (r3 == r4) goto L_0x09d9
            r4 = 2
            if (r3 != r4) goto L_0x09bb
            goto L_0x09d9
        L_0x09bb:
            r4 = 4
            if (r3 != r4) goto L_0x09ca
            r4 = -2
            r10.setPriority(r4)     // Catch:{ Exception -> 0x0d4c }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            if (r4 < r6) goto L_0x09f3
            r1 = 1
            r31 = r1
            goto L_0x09f5
        L_0x09ca:
            r4 = 5
            if (r3 != r4) goto L_0x09f3
            r4 = -1
            r10.setPriority(r4)     // Catch:{ Exception -> 0x0d4c }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            if (r4 < r6) goto L_0x09f3
            r1 = 2
            r31 = r1
            goto L_0x09f5
        L_0x09d9:
            r4 = 1
            r10.setPriority(r4)     // Catch:{ Exception -> 0x0d4c }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            if (r4 < r6) goto L_0x09f3
            r1 = 4
            r31 = r1
            goto L_0x09f5
        L_0x09e5:
            r7 = r56
        L_0x09e7:
            r4 = -1
            r10.setPriority(r4)     // Catch:{ Exception -> 0x0d4c }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            if (r4 < r6) goto L_0x09f3
            r1 = 2
            r31 = r1
            goto L_0x09f5
        L_0x09f3:
            r31 = r1
        L_0x09f5:
            r1 = 1
            if (r7 == r1) goto L_0x0b71
            if (r32 != 0) goto L_0x0b71
            if (r52 == 0) goto L_0x0a08
            java.lang.String r4 = "EnableInAppPreview"
            boolean r4 = r8.getBoolean(r4, r1)     // Catch:{ Exception -> 0x0d4c }
            if (r4 == 0) goto L_0x0a05
            goto L_0x0a08
        L_0x0a05:
            r67 = r3
            goto L_0x0a3d
        L_0x0a08:
            int r1 = r9.length()     // Catch:{ Exception -> 0x0d4c }
            r4 = 100
            if (r1 <= r4) goto L_0x0a38
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0d4c }
            r1.<init>()     // Catch:{ Exception -> 0x0d4c }
            r4 = 100
            r6 = 0
            java.lang.String r4 = r9.substring(r6, r4)     // Catch:{ Exception -> 0x0d4c }
            r6 = 32
            r67 = r3
            r3 = 10
            java.lang.String r3 = r4.replace(r3, r6)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r3 = r3.trim()     // Catch:{ Exception -> 0x0d4c }
            r1.append(r3)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r3 = "..."
            r1.append(r3)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0d4c }
            r9 = r1
            goto L_0x0a3a
        L_0x0a38:
            r67 = r3
        L_0x0a3a:
            r10.setTicker(r9)     // Catch:{ Exception -> 0x0d4c }
        L_0x0a3d:
            if (r2 == 0) goto L_0x0aef
            java.lang.String r1 = "NoSound"
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0d4c }
            if (r1 != 0) goto L_0x0aef
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            r3 = 26
            if (r1 < r3) goto L_0x0a88
            java.lang.String r1 = "Default"
            boolean r1 = r2.equals(r1)     // Catch:{ Exception -> 0x0d4c }
            if (r1 != 0) goto L_0x0a81
            boolean r1 = r2.equals(r14)     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x0a5c
            goto L_0x0a81
        L_0x0a5c:
            if (r27 == 0) goto L_0x0a78
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r3 = "org.telegram.messenger.beta.provider"
            java.io.File r4 = new java.io.File     // Catch:{ Exception -> 0x0d4c }
            r4.<init>(r2)     // Catch:{ Exception -> 0x0d4c }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r4)     // Catch:{ Exception -> 0x0d4c }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r4 = "com.android.systemui"
            r6 = 1
            r3.grantUriPermission(r4, r1, r6)     // Catch:{ Exception -> 0x0d4c }
            r12 = r1
            r56 = r7
            goto L_0x0af1
        L_0x0a78:
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0d4c }
            r12 = r1
            r56 = r7
            goto L_0x0af1
        L_0x0a81:
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0d4c }
            r12 = r1
            r56 = r7
            goto L_0x0af1
        L_0x0a88:
            boolean r1 = r2.equals(r14)     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x0a97
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0d4c }
            r3 = 5
            r10.setSound(r1, r3)     // Catch:{ Exception -> 0x0d4c }
            r56 = r7
            goto L_0x0af1
        L_0x0a97:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            r3 = 24
            if (r1 < r3) goto L_0x0ae4
            java.lang.String r1 = "file://"
            boolean r1 = r2.startsWith(r1)     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x0ae4
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0d4c }
            boolean r1 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r1)     // Catch:{ Exception -> 0x0d4c }
            if (r1 != 0) goto L_0x0ae4
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ad6 }
            java.lang.String r3 = "org.telegram.messenger.beta.provider"
            java.io.File r4 = new java.io.File     // Catch:{ Exception -> 0x0ad6 }
            java.lang.String r6 = "file://"
            r56 = r7
            r7 = r47
            java.lang.String r6 = r2.replace(r6, r7)     // Catch:{ Exception -> 0x0ad3 }
            r4.<init>(r6)     // Catch:{ Exception -> 0x0ad3 }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r1, r3, r4)     // Catch:{ Exception -> 0x0ad3 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ad3 }
            java.lang.String r4 = "com.android.systemui"
            r6 = 1
            r3.grantUriPermission(r4, r1, r6)     // Catch:{ Exception -> 0x0ad3 }
            r3 = 5
            r10.setSound(r1, r3)     // Catch:{ Exception -> 0x0ad3 }
            goto L_0x0af1
        L_0x0ad3:
            r0 = move-exception
            r1 = r0
            goto L_0x0ada
        L_0x0ad6:
            r0 = move-exception
            r56 = r7
            r1 = r0
        L_0x0ada:
            android.net.Uri r3 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0d4c }
            r4 = 5
            r10.setSound(r3, r4)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x0af1
        L_0x0ae4:
            r56 = r7
            android.net.Uri r1 = android.net.Uri.parse(r2)     // Catch:{ Exception -> 0x0d4c }
            r3 = 5
            r10.setSound(r1, r3)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x0af1
        L_0x0aef:
            r56 = r7
        L_0x0af1:
            if (r40 == 0) goto L_0x0afd
            r1 = 1000(0x3e8, float:1.401E-42)
            r3 = 1000(0x3e8, float:1.401E-42)
            r4 = r40
            r10.setLights(r4, r1, r3)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x0aff
        L_0x0afd:
            r4 = r40
        L_0x0aff:
            r6 = r61
            r1 = 2
            if (r6 != r1) goto L_0x0b1a
            long[] r3 = new long[r1]     // Catch:{ Exception -> 0x0d4c }
            r1 = 0
            r19 = 0
            r3[r1] = r19     // Catch:{ Exception -> 0x0d4c }
            r1 = 1
            r3[r1] = r19     // Catch:{ Exception -> 0x0d4c }
            r1 = r3
            r10.setVibrate(r3)     // Catch:{ Exception -> 0x0d4c }
            r60 = r1
            r19 = r9
            r20 = r12
            goto L_0x0b8e
        L_0x0b1a:
            r1 = 1
            if (r6 != r1) goto L_0x0b3c
            r3 = 4
            long[] r3 = new long[r3]     // Catch:{ Exception -> 0x0d4c }
            r7 = 0
            r19 = 0
            r3[r7] = r19     // Catch:{ Exception -> 0x0d4c }
            r50 = 100
            r3[r1] = r50     // Catch:{ Exception -> 0x0d4c }
            r1 = 2
            r3[r1] = r19     // Catch:{ Exception -> 0x0d4c }
            r19 = 100
            r1 = 3
            r3[r1] = r19     // Catch:{ Exception -> 0x0d4c }
            r1 = r3
            r10.setVibrate(r3)     // Catch:{ Exception -> 0x0d4c }
            r60 = r1
            r19 = r9
            r20 = r12
            goto L_0x0b8e
        L_0x0b3c:
            if (r6 == 0) goto L_0x0b62
            r1 = 4
            if (r6 != r1) goto L_0x0b42
            goto L_0x0b62
        L_0x0b42:
            r1 = 3
            if (r6 != r1) goto L_0x0b5d
            r1 = 2
            long[] r3 = new long[r1]     // Catch:{ Exception -> 0x0d4c }
            r1 = 0
            r18 = 0
            r3[r1] = r18     // Catch:{ Exception -> 0x0d4c }
            r1 = 1
            r19 = 1000(0x3e8, double:4.94E-321)
            r3[r1] = r19     // Catch:{ Exception -> 0x0d4c }
            r1 = r3
            r10.setVibrate(r3)     // Catch:{ Exception -> 0x0d4c }
            r60 = r1
            r19 = r9
            r20 = r12
            goto L_0x0b8e
        L_0x0b5d:
            r19 = r9
            r20 = r12
            goto L_0x0b8e
        L_0x0b62:
            r1 = 2
            r10.setDefaults(r1)     // Catch:{ Exception -> 0x0d4c }
            r1 = 0
            long[] r3 = new long[r1]     // Catch:{ Exception -> 0x0d4c }
            r1 = r3
            r60 = r1
            r19 = r9
            r20 = r12
            goto L_0x0b8e
        L_0x0b71:
            r67 = r3
            r56 = r7
            r4 = r40
            r6 = r61
            r1 = 2
            long[] r3 = new long[r1]     // Catch:{ Exception -> 0x0d4c }
            r1 = 0
            r19 = 0
            r3[r1] = r19     // Catch:{ Exception -> 0x0d4c }
            r1 = 1
            r3[r1] = r19     // Catch:{ Exception -> 0x0d4c }
            r1 = r3
            r10.setVibrate(r3)     // Catch:{ Exception -> 0x0d4c }
            r60 = r1
            r19 = r9
            r20 = r12
        L_0x0b8e:
            r1 = 0
            boolean r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0d4c }
            if (r3 != 0) goto L_0x0c8e
            boolean r3 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0d4c }
            if (r3 != 0) goto L_0x0c8e
            long r50 = r11.getDialogId()     // Catch:{ Exception -> 0x0d4c }
            r53 = 777000(0xbdb28, double:3.83889E-318)
            int r3 = (r50 > r53 ? 1 : (r50 == r53 ? 0 : -1))
            if (r3 != 0) goto L_0x0c8e
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$ReplyMarkup r3 = r3.reply_markup     // Catch:{ Exception -> 0x0d4c }
            if (r3 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r3 = r11.messageOwner     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$ReplyMarkup r3 = r3.reply_markup     // Catch:{ Exception -> 0x0d4c }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r3 = r3.rows     // Catch:{ Exception -> 0x0d4c }
            r7 = 0
            int r9 = r3.size()     // Catch:{ Exception -> 0x0d4c }
        L_0x0bb5:
            if (r7 >= r9) goto L_0x0CLASSNAME
            java.lang.Object r12 = r3.get(r7)     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r12 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r12     // Catch:{ Exception -> 0x0d4c }
            r23 = 0
            r28 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r1 = r12.buttons     // Catch:{ Exception -> 0x0d4c }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0d4c }
            r69 = r23
            r23 = r2
            r2 = r69
        L_0x0bcd:
            if (r2 >= r1) goto L_0x0CLASSNAME
            r40 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r1 = r12.buttons     // Catch:{ Exception -> 0x0d4c }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x0d4c }
            org.telegram.tgnet.TLRPC$KeyboardButton r1 = (org.telegram.tgnet.TLRPC.KeyboardButton) r1     // Catch:{ Exception -> 0x0d4c }
            r47 = r3
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0d4c }
            if (r3 == 0) goto L_0x0CLASSNAME
            android.content.Intent r3 = new android.content.Intent     // Catch:{ Exception -> 0x0d4c }
            r50 = r4
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            r61 = r6
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r6 = org.telegram.messenger.NotificationCallbackReceiver.class
            r3.<init>(r4, r6)     // Catch:{ Exception -> 0x0d4c }
            int r4 = r15.currentAccount     // Catch:{ Exception -> 0x0d4c }
            r3.putExtra(r5, r4)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r4 = "did"
            r6 = r12
            r51 = r13
            r12 = r62
            r3.putExtra(r4, r12)     // Catch:{ Exception -> 0x0d4c }
            byte[] r4 = r1.data     // Catch:{ Exception -> 0x0d4c }
            if (r4 == 0) goto L_0x0CLASSNAME
            java.lang.String r4 = "data"
            r53 = r6
            byte[] r6 = r1.data     // Catch:{ Exception -> 0x0d4c }
            r3.putExtra(r4, r6)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x0c0b
        L_0x0CLASSNAME:
            r53 = r6
        L_0x0c0b:
            java.lang.String r4 = "mid"
            int r6 = r11.getId()     // Catch:{ Exception -> 0x0d4c }
            r3.putExtra(r4, r6)     // Catch:{ Exception -> 0x0d4c }
            java.lang.String r4 = r1.text     // Catch:{ Exception -> 0x0d4c }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            r54 = r1
            int r1 = r15.lastButtonId     // Catch:{ Exception -> 0x0d4c }
            r55 = r8
            int r8 = r1 + 1
            r15.lastButtonId = r8     // Catch:{ Exception -> 0x0d4c }
            r8 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r6, r1, r3, r8)     // Catch:{ Exception -> 0x0d4c }
            r6 = 0
            r10.addAction(r6, r4, r1)     // Catch:{ Exception -> 0x0d4c }
            r1 = 1
            r28 = r1
            goto L_0x0c3f
        L_0x0CLASSNAME:
            r54 = r1
            r50 = r4
            r61 = r6
            r55 = r8
            r53 = r12
            r51 = r13
            r12 = r62
            r6 = 0
        L_0x0c3f:
            int r2 = r2 + 1
            r62 = r12
            r1 = r40
            r3 = r47
            r4 = r50
            r13 = r51
            r12 = r53
            r8 = r55
            r6 = r61
            goto L_0x0bcd
        L_0x0CLASSNAME:
            r40 = r1
            r47 = r3
            r50 = r4
            r61 = r6
            r55 = r8
            r53 = r12
            r51 = r13
            r12 = r62
            r6 = 0
            int r7 = r7 + 1
            r2 = r23
            r1 = r28
            r13 = r51
            r6 = r61
            goto L_0x0bb5
        L_0x0CLASSNAME:
            r28 = r1
            r23 = r2
            r47 = r3
            r50 = r4
            r61 = r6
            r55 = r8
            r51 = r13
            r12 = r62
            goto L_0x0c9c
        L_0x0CLASSNAME:
            r23 = r2
            r50 = r4
            r61 = r6
            r55 = r8
            r51 = r13
            r12 = r62
            goto L_0x0c9a
        L_0x0c8e:
            r23 = r2
            r50 = r4
            r61 = r6
            r55 = r8
            r51 = r13
            r12 = r62
        L_0x0c9a:
            r28 = r1
        L_0x0c9c:
            if (r28 != 0) goto L_0x0cf7
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            r2 = 24
            if (r1 >= r2) goto L_0x0cf7
            java.lang.String r1 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0d4c }
            int r1 = r1.length()     // Catch:{ Exception -> 0x0d4c }
            if (r1 != 0) goto L_0x0cf7
            boolean r1 = r70.hasMessagesToReply()     // Catch:{ Exception -> 0x0d4c }
            if (r1 == 0) goto L_0x0cf7
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0d4c }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r3 = org.telegram.messenger.PopupReplyReceiver.class
            r1.<init>(r2, r3)     // Catch:{ Exception -> 0x0d4c }
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0d4c }
            r1.putExtra(r5, r2)     // Catch:{ Exception -> 0x0d4c }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0d4c }
            r3 = 19
            if (r2 > r3) goto L_0x0cdf
            r2 = 2131165512(0x7var_, float:1.7945243E38)
            java.lang.String r3 = "Reply"
            r4 = 2131627732(0x7f0e0ed4, float:1.8882737E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0d4c }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0d4c }
            r10.addAction(r2, r3, r4)     // Catch:{ Exception -> 0x0d4c }
            goto L_0x0cf7
        L_0x0cdf:
            r2 = 2131165511(0x7var_, float:1.7945241E38)
            java.lang.String r3 = "Reply"
            r4 = 2131627732(0x7f0e0ed4, float:1.8882737E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0d4c }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0d4c }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r4, r6, r1, r5)     // Catch:{ Exception -> 0x0d4c }
            r10.addAction(r2, r3, r4)     // Catch:{ Exception -> 0x0d4c }
        L_0x0cf7:
            r1 = r70
            r18 = r23
            r2 = r10
            r23 = r24
            r25 = r38
            r33 = r57
            r38 = r67
            r3 = r46
            r40 = r41
            r47 = r50
            r42 = r65
            r4 = r12
            r50 = r10
            r54 = r61
            r53 = r68
            r6 = r36
            r57 = r26
            r26 = r66
            r7 = r60
            r61 = r48
            r48 = r55
            r8 = r47
            r9 = r20
            r65 = r12
            r10 = r31
            r49 = r11
            r11 = r29
            r69 = r44
            r44 = r30
            r30 = r69
            r12 = r52
            r55 = r51
            r51 = r39
            r39 = r56
            r69 = r48
            r48 = r21
            r21 = r69
            r13 = r32
            r56 = r14
            r14 = r22
            r1.showExtraNotifications(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0d4c }
            r70.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0d4c }
            goto L_0x0d51
        L_0x0d4c:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0d51:
            return
        L_0x0d52:
            r70.dismissNotification()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    private boolean isSilentMessage(MessageObject messageObject) {
        return messageObject.messageOwner.silent || messageObject.isReactionPush;
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
            getNotificationsController().m1040xb6var_c1b(i, -1);
        } else {
            editor.putString("sound_" + j, ringtoneName);
            editor.putString("sound_path_" + j, newSound);
            m1039xab324d39(j, -1);
        }
        editor.commit();
        String str = ringtoneName;
        String str2 = newSound;
        SharedPreferences.Editor editor2 = editor;
        notificationBuilder.setChannelId(validateChannelId(dialogId, chatName, vibrationPattern, ledColor, Settings.System.DEFAULT_RINGTONE_URI, importance, isDefault, isInApp, isSilent, chatType));
        notificationManager.notify(this.notificationId, notificationBuilder.build());
    }

    /* JADX WARNING: Removed duplicated region for block: B:196:0x05c9  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x05d1  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x05ea  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x05ff  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x06a2 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:252:0x06d4 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x070d  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0960  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0ace  */
    /* JADX WARNING: Removed duplicated region for block: B:423:0x0b57  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0b61  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0b8b  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0b91  */
    /* JADX WARNING: Removed duplicated region for block: B:434:0x0bed  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0c2a  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0c7b  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0d3a  */
    /* JADX WARNING: Removed duplicated region for block: B:449:0x0d45  */
    /* JADX WARNING: Removed duplicated region for block: B:450:0x0d4b  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0d4f  */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0d5f  */
    /* JADX WARNING: Removed duplicated region for block: B:458:0x0d65  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0d6d  */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x0d72  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0d75  */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0d7d  */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x0d8d  */
    /* JADX WARNING: Removed duplicated region for block: B:486:0x0e6c  */
    /* JADX WARNING: Removed duplicated region for block: B:488:0x0e7d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:497:0x0eac  */
    /* JADX WARNING: Removed duplicated region for block: B:498:0x0eb4  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0fe0  */
    /* JADX WARNING: Removed duplicated region for block: B:531:0x1032  */
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
            if (r0 >= r1) goto L_0x0042
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.notify(r1, r12)
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0041
            java.lang.String r0 = "show summary notification by SDK check"
            org.telegram.messenger.FileLog.d(r0)
        L_0x0041:
            return
        L_0x0042:
            org.telegram.messenger.AccountInstance r0 = r86.getAccountInstance()
            android.content.SharedPreferences r11 = r0.getNotificationsSettings()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r10 = r0
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r9 = r0
            r0 = 0
        L_0x0057:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r15.pushMessages
            int r1 = r1.size()
            r8 = 0
            if (r0 >= r1) goto L_0x00a6
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
            if (r5 > r4) goto L_0x0088
            goto L_0x00a3
        L_0x0088:
            java.lang.Object r5 = r9.get(r2)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x00a0
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r5 = r6
            r9.put(r2, r5)
            java.lang.Long r6 = java.lang.Long.valueOf(r2)
            r10.add(r6)
        L_0x00a0:
            r5.add(r1)
        L_0x00a3:
            int r0 = r0 + 1
            goto L_0x0057
        L_0x00a6:
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r7 = r0
            r0 = 0
        L_0x00ad:
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.wearNotificationsIds
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x00c9
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.wearNotificationsIds
            long r1 = r1.keyAt(r0)
            androidx.collection.LongSparseArray<java.lang.Integer> r3 = r15.wearNotificationsIds
            java.lang.Object r3 = r3.valueAt(r0)
            java.lang.Integer r3 = (java.lang.Integer) r3
            r7.put(r1, r3)
            int r0 = r0 + 1
            goto L_0x00ad
        L_0x00c9:
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r0.clear()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r6 = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 27
            r5 = 1
            if (r0 <= r4) goto L_0x00e4
            int r0 = r10.size()
            if (r0 <= r5) goto L_0x00e2
            goto L_0x00e4
        L_0x00e2:
            r0 = 0
            goto L_0x00e5
        L_0x00e4:
            r0 = 1
        L_0x00e5:
            r2 = r0
            if (r2 == 0) goto L_0x00ef
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r13) goto L_0x00ef
            checkOtherNotificationsChannel()
        L_0x00ef:
            org.telegram.messenger.UserConfig r0 = r86.getUserConfig()
            long r13 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x0104
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x0102
            goto L_0x0104
        L_0x0102:
            r0 = 0
            goto L_0x0105
        L_0x0104:
            r0 = 1
        L_0x0105:
            r19 = r0
            r3 = 7
            androidx.collection.LongSparseArray r0 = new androidx.collection.LongSparseArray
            r0.<init>()
            r1 = r0
            r0 = 0
            int r4 = r10.size()
            r5 = r0
        L_0x0114:
            if (r5 >= r4) goto L_0x0var_
            int r0 = r6.size()
            if (r0 < r3) goto L_0x0131
            r81 = r1
            r23 = r2
            r24 = r3
            r49 = r7
            r27 = r9
            r26 = r10
            r22 = r11
            r83 = r12
            r58 = r13
            r14 = r6
            goto L_0x0f7a
        L_0x0131:
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
            if (r0 != 0) goto L_0x016f
            r25 = r0
            int r0 = (int) r4
            r27 = r9
            r26 = r10
            long r9 = r4 >> r3
            int r10 = (int) r9
            int r0 = r0 + r10
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r10 = r0
            goto L_0x017a
        L_0x016f:
            r25 = r0
            r27 = r9
            r26 = r10
            r7.remove(r4)
            r10 = r25
        L_0x017a:
            r9 = 0
            java.lang.Object r0 = r11.get(r9)
            r9 = r0
            org.telegram.messenger.MessageObject r9 = (org.telegram.messenger.MessageObject) r9
            r0 = 0
            r25 = 0
            r3 = r0
            r0 = r25
        L_0x0188:
            r25 = r6
            int r6 = r11.size()
            if (r0 >= r6) goto L_0x01ab
            java.lang.Object r6 = r11.get(r0)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r6 = r6.date
            if (r3 >= r6) goto L_0x01a6
            java.lang.Object r6 = r11.get(r0)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner
            int r3 = r6.date
        L_0x01a6:
            int r0 = r0 + 1
            r6 = r25
            goto L_0x0188
        L_0x01ab:
            r0 = 0
            r6 = 0
            r29 = 0
            r30 = 0
            r31 = 0
            r32 = 0
            r33 = 0
            boolean r34 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            r35 = 777000(0xbdb28, double:3.83889E-318)
            r37 = 0
            if (r34 != 0) goto L_0x034c
            int r34 = (r4 > r35 ? 1 : (r4 == r35 ? 0 : -1))
            if (r34 == 0) goto L_0x01c9
            r34 = 1
            goto L_0x01cb
        L_0x01c9:
            r34 = 0
        L_0x01cb:
            boolean r39 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r39 == 0) goto L_0x0294
            r39 = r0
            org.telegram.messenger.MessagesController r0 = r86.getMessagesController()
            r40 = r6
            java.lang.Long r6 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r6 = r0.getUser(r6)
            if (r6 != 0) goto L_0x023b
            boolean r0 = r9.isFcmMessage()
            if (r0 == 0) goto L_0x01f3
            java.lang.String r0 = r9.localName
            r42 = r2
            r43 = r3
            r41 = r7
            goto L_0x026c
        L_0x01f3:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0223
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r41 = r7
            java.lang.String r7 = "not found user to show dialog notification "
            r0.append(r7)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            r81 = r1
            r23 = r2
            r83 = r12
            r58 = r13
            r14 = r25
            r49 = r41
            r44 = 27
            r45 = 1
            r51 = 26
            r57 = 0
            goto L_0x0var_
        L_0x0223:
            r41 = r7
            r81 = r1
            r23 = r2
            r83 = r12
            r58 = r13
            r14 = r25
            r49 = r41
            r44 = 27
            r45 = 1
            r51 = 26
            r57 = 0
            goto L_0x0var_
        L_0x023b:
            r41 = r7
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r6)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r6.photo
            if (r7 == 0) goto L_0x0268
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r6.photo
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            if (r7 == 0) goto L_0x0268
            org.telegram.tgnet.TLRPC$UserProfilePhoto r7 = r6.photo
            org.telegram.tgnet.TLRPC$FileLocation r7 = r7.photo_small
            r42 = r2
            r43 = r3
            long r2 = r7.volume_id
            int r7 = (r2 > r37 ? 1 : (r2 == r37 ? 0 : -1))
            if (r7 == 0) goto L_0x026c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r6.photo
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
            int r2 = r2.local_id
            if (r2 == 0) goto L_0x026c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r2 = r6.photo
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small
            r31 = r2
            goto L_0x026c
        L_0x0268:
            r42 = r2
            r43 = r3
        L_0x026c:
            boolean r2 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r2 == 0) goto L_0x027f
            r2 = 2131627725(0x7f0e0ecd, float:1.8882723E38)
            java.lang.String r3 = "RepliesTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r7 = r39
            goto L_0x0420
        L_0x027f:
            int r2 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r2 != 0) goto L_0x0290
            r2 = 2131626494(0x7f0e09fe, float:1.8880226E38)
            java.lang.String r3 = "MessageScheduledReminderNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r7 = r39
            goto L_0x0420
        L_0x0290:
            r7 = r39
            goto L_0x0420
        L_0x0294:
            r39 = r0
            r42 = r2
            r43 = r3
            r40 = r6
            r41 = r7
            org.telegram.messenger.MessagesController r0 = r86.getMessagesController()
            long r2 = -r4
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r2)
            if (r0 != 0) goto L_0x0307
            boolean r2 = r9.isFcmMessage()
            if (r2 == 0) goto L_0x02c3
            boolean r30 = r9.isSupergroup()
            java.lang.String r2 = r9.localName
            boolean r3 = r9.localChannel
            r7 = r0
            r0 = r2
            r29 = r3
            r6 = r40
            goto L_0x0420
        L_0x02c3:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x02f1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "not found chat to show dialog notification "
            r2.append(r3)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.w(r2)
            r81 = r1
            r83 = r12
            r58 = r13
            r14 = r25
            r49 = r41
            r23 = r42
            r44 = 27
            r45 = 1
            r51 = 26
            r57 = 0
            goto L_0x0var_
        L_0x02f1:
            r81 = r1
            r83 = r12
            r58 = r13
            r14 = r25
            r49 = r41
            r23 = r42
            r44 = 27
            r45 = 1
            r51 = 26
            r57 = 0
            goto L_0x0var_
        L_0x0307:
            boolean r2 = r0.megagroup
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r3 == 0) goto L_0x0315
            boolean r3 = r0.megagroup
            if (r3 != 0) goto L_0x0315
            r3 = 1
            goto L_0x0316
        L_0x0315:
            r3 = 0
        L_0x0316:
            r29 = r3
            java.lang.String r3 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            if (r6 == 0) goto L_0x0344
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            if (r6 == 0) goto L_0x0344
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            long r6 = r6.volume_id
            int r30 = (r6 > r37 ? 1 : (r6 == r37 ? 0 : -1))
            if (r30 == 0) goto L_0x0344
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            int r6 = r6.local_id
            if (r6 == 0) goto L_0x0344
            org.telegram.tgnet.TLRPC$ChatPhoto r6 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
            r7 = r0
            r30 = r2
            r0 = r3
            r31 = r6
            r6 = r40
            goto L_0x0420
        L_0x0344:
            r7 = r0
            r30 = r2
            r0 = r3
            r6 = r40
            goto L_0x0420
        L_0x034c:
            r39 = r0
            r42 = r2
            r43 = r3
            r40 = r6
            r41 = r7
            r34 = 0
            long r2 = globalSecretChatId
            int r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0411
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r4)
            org.telegram.messenger.MessagesController r2 = r86.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r2 = r2.getEncryptedChat(r3)
            if (r2 != 0) goto L_0x03b4
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x039e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "not found secret chat to show dialog notification "
            r3.append(r6)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.w(r3)
            r81 = r1
            r83 = r12
            r58 = r13
            r14 = r25
            r49 = r41
            r23 = r42
            r44 = 27
            r45 = 1
            r51 = 26
            r57 = 0
            goto L_0x0var_
        L_0x039e:
            r81 = r1
            r83 = r12
            r58 = r13
            r14 = r25
            r49 = r41
            r23 = r42
            r44 = 27
            r45 = 1
            r51 = 26
            r57 = 0
            goto L_0x0var_
        L_0x03b4:
            org.telegram.messenger.MessagesController r3 = r86.getMessagesController()
            long r6 = r2.user_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r6 = r3.getUser(r6)
            if (r6 != 0) goto L_0x040e
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x03f6
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r7 = "not found secret chat user to show dialog notification "
            r3.append(r7)
            r40 = r6
            long r6 = r2.user_id
            r3.append(r6)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.w(r3)
            r81 = r1
            r83 = r12
            r58 = r13
            r14 = r25
            r49 = r41
            r23 = r42
            r44 = 27
            r45 = 1
            r51 = 26
            r57 = 0
            goto L_0x0var_
        L_0x03f6:
            r40 = r6
            r81 = r1
            r83 = r12
            r58 = r13
            r14 = r25
            r49 = r41
            r23 = r42
            r44 = 27
            r45 = 1
            r51 = 26
            r57 = 0
            goto L_0x0var_
        L_0x040e:
            r40 = r6
            goto L_0x0413
        L_0x0411:
            r6 = r40
        L_0x0413:
            r0 = 2131627935(0x7f0e0f9f, float:1.8883148E38)
            java.lang.String r2 = "SecretChatName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r31 = 0
            r7 = r39
        L_0x0420:
            java.lang.String r3 = "NotificationHiddenChatName"
            java.lang.String r2 = "NotificationHiddenName"
            if (r19 == 0) goto L_0x044b
            boolean r44 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r44 == 0) goto L_0x0436
            r44 = r12
            r12 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r12)
            goto L_0x043f
        L_0x0436:
            r44 = r12
            r12 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r12)
        L_0x043f:
            r31 = 0
            r34 = 0
            r12 = r0
            r85 = r31
            r31 = r6
            r6 = r85
            goto L_0x0454
        L_0x044b:
            r44 = r12
            r12 = r0
            r85 = r31
            r31 = r6
            r6 = r85
        L_0x0454:
            r45 = r2
            if (r6 == 0) goto L_0x04be
            r2 = 1
            java.io.File r33 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r2)
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 28
            if (r0 >= r2) goto L_0x04b7
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()
            r2 = 0
            r47 = r3
            java.lang.String r3 = "50_50"
            android.graphics.drawable.BitmapDrawable r2 = r0.getImageFromMemory(r6, r2, r3)
            if (r2 == 0) goto L_0x047b
            android.graphics.Bitmap r32 = r2.getBitmap()
            r2 = r32
            r3 = r33
            goto L_0x04c4
        L_0x047b:
            boolean r0 = r33.exists()     // Catch:{ all -> 0x04b3 }
            if (r0 == 0) goto L_0x04ac
            r0 = 1126170624(0x43200000, float:160.0)
            r3 = 1112014848(0x42480000, float:50.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ all -> 0x04b3 }
            float r3 = (float) r3     // Catch:{ all -> 0x04b3 }
            float r0 = r0 / r3
            android.graphics.BitmapFactory$Options r3 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x04b3 }
            r3.<init>()     // Catch:{ all -> 0x04b3 }
            r48 = 1065353216(0x3var_, float:1.0)
            int r48 = (r0 > r48 ? 1 : (r0 == r48 ? 0 : -1))
            if (r48 >= 0) goto L_0x049a
            r48 = r2
            r2 = 1
            goto L_0x049d
        L_0x049a:
            r48 = r2
            int r2 = (int) r0
        L_0x049d:
            r3.inSampleSize = r2     // Catch:{ all -> 0x04aa }
            java.lang.String r2 = r33.getAbsolutePath()     // Catch:{ all -> 0x04aa }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2, r3)     // Catch:{ all -> 0x04aa }
            r32 = r2
            goto L_0x04ae
        L_0x04aa:
            r0 = move-exception
            goto L_0x04b9
        L_0x04ac:
            r48 = r2
        L_0x04ae:
            r2 = r32
            r3 = r33
            goto L_0x04c4
        L_0x04b3:
            r0 = move-exception
            r48 = r2
            goto L_0x04b9
        L_0x04b7:
            r47 = r3
        L_0x04b9:
            r2 = r32
            r3 = r33
            goto L_0x04c4
        L_0x04be:
            r47 = r3
            r2 = r32
            r3 = r33
        L_0x04c4:
            if (r7 == 0) goto L_0x04f7
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r12)
            if (r3 == 0) goto L_0x04e5
            boolean r32 = r3.exists()
            if (r32 == 0) goto L_0x04e5
            r32 = r6
            int r6 = android.os.Build.VERSION.SDK_INT
            r33 = r2
            r2 = 28
            if (r6 < r2) goto L_0x04e9
            r15.loadRoundAvatar(r3, r0)
            goto L_0x04e9
        L_0x04e5:
            r33 = r2
            r32 = r6
        L_0x04e9:
            r6 = r3
            long r2 = r7.id
            long r2 = -r2
            r48 = r7
            androidx.core.app.Person r7 = r0.build()
            r1.put(r2, r7)
            goto L_0x04fe
        L_0x04f7:
            r33 = r2
            r32 = r6
            r48 = r7
            r6 = r3
        L_0x04fe:
            r0 = 0
            java.lang.String r2 = "max_id"
            java.lang.String r3 = "dialog_id"
            java.lang.String r7 = "currentAccount"
            if (r29 == 0) goto L_0x0516
            if (r30 == 0) goto L_0x050a
            goto L_0x0516
        L_0x050a:
            r50 = r0
            r56 = r2
            r51 = r6
            r52 = r9
            r53 = r10
            goto L_0x05bd
        L_0x0516:
            if (r34 == 0) goto L_0x05b3
            boolean r50 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r50 != 0) goto L_0x05b3
            int r50 = (r13 > r4 ? 1 : (r13 == r4 ? 0 : -1))
            if (r50 == 0) goto L_0x05b3
            boolean r50 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r50 != 0) goto L_0x05b3
            r50 = r0
            android.content.Intent r0 = new android.content.Intent
            r51 = r6
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            r52 = r9
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r9 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r6, r9)
            r0.putExtra(r3, r4)
            r0.putExtra(r2, r8)
            int r6 = r15.currentAccount
            r0.putExtra(r7, r6)
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r9 = r10.intValue()
            r53 = r10
            r10 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r6 = android.app.PendingIntent.getBroadcast(r6, r9, r0, r10)
            androidx.core.app.RemoteInput$Builder r9 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r10 = "extra_voice_reply"
            r9.<init>(r10)
            r10 = 2131627732(0x7f0e0ed4, float:1.8882737E38)
            r54 = r0
            java.lang.String r0 = "Reply"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r10)
            androidx.core.app.RemoteInput$Builder r0 = r9.setLabel(r0)
            androidx.core.app.RemoteInput r0 = r0.build()
            boolean r9 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r9 == 0) goto L_0x0581
            r10 = 1
            java.lang.Object[] r9 = new java.lang.Object[r10]
            r23 = 0
            r9[r23] = r12
            java.lang.String r10 = "ReplyToGroup"
            r56 = r2
            r2 = 2131627733(0x7f0e0ed5, float:1.8882739E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r10, r2, r9)
            goto L_0x0593
        L_0x0581:
            r56 = r2
            r23 = 0
            r2 = 2131627734(0x7f0e0ed6, float:1.888274E38)
            r9 = 1
            java.lang.Object[] r10 = new java.lang.Object[r9]
            r10[r23] = r12
            java.lang.String r9 = "ReplyToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r9, r2, r10)
        L_0x0593:
            androidx.core.app.NotificationCompat$Action$Builder r9 = new androidx.core.app.NotificationCompat$Action$Builder
            r10 = 2131165557(0x7var_, float:1.7945334E38)
            r9.<init>((int) r10, (java.lang.CharSequence) r2, (android.app.PendingIntent) r6)
            r10 = 1
            androidx.core.app.NotificationCompat$Action$Builder r9 = r9.setAllowGeneratedReplies(r10)
            androidx.core.app.NotificationCompat$Action$Builder r9 = r9.setSemanticAction(r10)
            androidx.core.app.NotificationCompat$Action$Builder r9 = r9.addRemoteInput(r0)
            r10 = 0
            androidx.core.app.NotificationCompat$Action$Builder r9 = r9.setShowsUserInterface(r10)
            androidx.core.app.NotificationCompat$Action r9 = r9.build()
            r10 = r9
            goto L_0x05bf
        L_0x05b3:
            r50 = r0
            r56 = r2
            r51 = r6
            r52 = r9
            r53 = r10
        L_0x05bd:
            r10 = r50
        L_0x05bf:
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.pushDialogs
            java.lang.Object r0 = r0.get(r4)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x05d1
            r2 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r2)
            r50 = r0
            goto L_0x05d3
        L_0x05d1:
            r50 = r0
        L_0x05d3:
            int r0 = r50.intValue()
            int r2 = r11.size()
            int r9 = java.lang.Math.max(r0, r2)
            r2 = 2
            r6 = 1
            if (r9 <= r6) goto L_0x05ff
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 28
            if (r0 < r6) goto L_0x05ea
            goto L_0x05ff
        L_0x05ea:
            java.lang.Object[] r0 = new java.lang.Object[r2]
            r6 = 0
            r0[r6] = r12
            java.lang.Integer r6 = java.lang.Integer.valueOf(r9)
            r18 = 1
            r0[r18] = r6
            java.lang.String r6 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r6, r0)
            r6 = r0
            goto L_0x0601
        L_0x05ff:
            r0 = r12
            r6 = r0
        L_0x0601:
            java.lang.Object r0 = r1.get(r13)
            r54 = r0
            androidx.core.app.Person r54 = (androidx.core.app.Person) r54
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 28
            if (r0 < r2) goto L_0x068c
            if (r54 != 0) goto L_0x068c
            org.telegram.messenger.MessagesController r0 = r86.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x0629
            org.telegram.messenger.UserConfig r2 = r86.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r2.getCurrentUser()
            r2 = r0
            goto L_0x062a
        L_0x0629:
            r2 = r0
        L_0x062a:
            if (r2 == 0) goto L_0x0685
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r2.photo     // Catch:{ all -> 0x067a }
            if (r0 == 0) goto L_0x0685
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r2.photo     // Catch:{ all -> 0x067a }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x067a }
            if (r0 == 0) goto L_0x0685
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r2.photo     // Catch:{ all -> 0x067a }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x067a }
            r57 = r8
            r58 = r9
            long r8 = r0.volume_id     // Catch:{ all -> 0x0678 }
            int r0 = (r8 > r37 ? 1 : (r8 == r37 ? 0 : -1))
            if (r0 == 0) goto L_0x0689
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r2.photo     // Catch:{ all -> 0x0678 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x0678 }
            int r0 = r0.local_id     // Catch:{ all -> 0x0678 }
            if (r0 == 0) goto L_0x0689
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x0678 }
            r0.<init>()     // Catch:{ all -> 0x0678 }
            java.lang.String r8 = "FromYou"
            r9 = 2131625932(0x7f0e07cc, float:1.8879086E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)     // Catch:{ all -> 0x0678 }
            androidx.core.app.Person$Builder r0 = r0.setName(r8)     // Catch:{ all -> 0x0678 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r8 = r2.photo     // Catch:{ all -> 0x0678 }
            org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_small     // Catch:{ all -> 0x0678 }
            r9 = 1
            java.io.File r8 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r9)     // Catch:{ all -> 0x0678 }
            r15.loadRoundAvatar(r8, r0)     // Catch:{ all -> 0x0678 }
            androidx.core.app.Person r9 = r0.build()     // Catch:{ all -> 0x0678 }
            r1.put(r13, r9)     // Catch:{ all -> 0x0674 }
            r54 = r9
            goto L_0x0689
        L_0x0674:
            r0 = move-exception
            r54 = r9
            goto L_0x067f
        L_0x0678:
            r0 = move-exception
            goto L_0x067f
        L_0x067a:
            r0 = move-exception
            r57 = r8
            r58 = r9
        L_0x067f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r9 = r54
            goto L_0x0692
        L_0x0685:
            r57 = r8
            r58 = r9
        L_0x0689:
            r9 = r54
            goto L_0x0692
        L_0x068c:
            r57 = r8
            r58 = r9
            r9 = r54
        L_0x0692:
            r8 = r52
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByRequest
            r2 = 1
            r0 = r0 ^ r2
            r52 = r0
            java.lang.String r2 = ""
            if (r9 == 0) goto L_0x06ad
            if (r52 == 0) goto L_0x06ad
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((androidx.core.app.Person) r9)
            r54 = r9
            r9 = r0
            goto L_0x06b5
        L_0x06ad:
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((java.lang.CharSequence) r2)
            r54 = r9
            r9 = r0
        L_0x06b5:
            int r0 = android.os.Build.VERSION.SDK_INT
            r59 = r8
            r8 = 28
            if (r0 < r8) goto L_0x06cb
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r0 == 0) goto L_0x06c5
            if (r29 == 0) goto L_0x06cb
        L_0x06c5:
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r0 == 0) goto L_0x06ce
        L_0x06cb:
            r9.setConversationTitle(r6)
        L_0x06ce:
            int r0 = android.os.Build.VERSION.SDK_INT
            r8 = 28
            if (r0 < r8) goto L_0x06e5
            if (r29 != 0) goto L_0x06dc
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r0 != 0) goto L_0x06e5
        L_0x06dc:
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r4)
            if (r0 == 0) goto L_0x06e3
            goto L_0x06e5
        L_0x06e3:
            r0 = 0
            goto L_0x06e6
        L_0x06e5:
            r0 = 1
        L_0x06e6:
            r9.setGroupConversation(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r8 = r0
            r61 = r3
            r60 = r6
            r6 = 1
            java.lang.String[] r3 = new java.lang.String[r6]
            r62 = r10
            boolean[] r10 = new boolean[r6]
            r0 = 0
            r63 = 0
            int r18 = r11.size()
            int r64 = r18 + -1
            r65 = r63
            r6 = r64
            r63 = r0
        L_0x0709:
            r66 = 1000(0x3e8, double:4.94E-321)
            if (r6 < 0) goto L_0x0b11
            java.lang.Object r0 = r11.get(r6)
            r64 = r11
            r11 = r0
            org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
            java.lang.String r0 = r15.getShortStringForMessage(r11, r3, r10)
            r68 = r7
            java.lang.String r7 = "NotificationMessageScheduledName"
            int r70 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r70 != 0) goto L_0x0729
            r23 = 0
            r3[r23] = r12
            r70 = r12
            goto L_0x0745
        L_0x0729:
            r23 = 0
            boolean r70 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r70 == 0) goto L_0x0743
            r70 = r12
            org.telegram.tgnet.TLRPC$Message r12 = r11.messageOwner
            boolean r12 = r12.from_scheduled
            if (r12 == 0) goto L_0x0745
            r12 = 2131626852(0x7f0e0b64, float:1.8880952E38)
            java.lang.String r71 = org.telegram.messenger.LocaleController.getString(r7, r12)
            r3[r23] = r71
            goto L_0x0745
        L_0x0743:
            r70 = r12
        L_0x0745:
            if (r0 != 0) goto L_0x0793
            boolean r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r7 == 0) goto L_0x0781
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r12 = "message text is null for "
            r7.append(r12)
            int r12 = r11.getId()
            r7.append(r12)
            java.lang.String r12 = " did = "
            r7.append(r12)
            r12 = r9
            r71 = r10
            long r9 = r11.getDialogId()
            r7.append(r9)
            java.lang.String r7 = r7.toString()
            org.telegram.messenger.FileLog.w(r7)
            r72 = r2
            r73 = r13
            r77 = r45
            r75 = r47
            r47 = r1
            r45 = r3
            r13 = r12
            goto L_0x0af8
        L_0x0781:
            r12 = r9
            r71 = r10
            r72 = r2
            r73 = r13
            r77 = r45
            r75 = r47
            r47 = r1
            r45 = r3
            r13 = r12
            goto L_0x0af8
        L_0x0793:
            r12 = r9
            r71 = r10
            int r9 = r8.length()
            if (r9 <= 0) goto L_0x07a1
            java.lang.String r9 = "\n\n"
            r8.append(r9)
        L_0x07a1:
            int r9 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
            if (r9 == 0) goto L_0x07cc
            org.telegram.tgnet.TLRPC$Message r9 = r11.messageOwner
            boolean r9 = r9.from_scheduled
            if (r9 == 0) goto L_0x07cc
            boolean r9 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r9 == 0) goto L_0x07cc
            r9 = 2
            java.lang.Object[] r10 = new java.lang.Object[r9]
            r9 = 2131626852(0x7f0e0b64, float:1.8880952E38)
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r9)
            r9 = 0
            r10[r9] = r7
            r7 = 1
            r10[r7] = r0
            java.lang.String r7 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r7, r10)
            r8.append(r0)
            r7 = r0
            goto L_0x07e9
        L_0x07cc:
            r7 = 0
            r9 = r3[r7]
            if (r9 == 0) goto L_0x07e5
            r9 = 2
            java.lang.Object[] r10 = new java.lang.Object[r9]
            r9 = r3[r7]
            r10[r7] = r9
            r7 = 1
            r10[r7] = r0
            java.lang.String r7 = "%1$s: %2$s"
            java.lang.String r7 = java.lang.String.format(r7, r10)
            r8.append(r7)
            goto L_0x07e8
        L_0x07e5:
            r8.append(r0)
        L_0x07e8:
            r7 = r0
        L_0x07e9:
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r0 == 0) goto L_0x07f1
            r9 = r4
            goto L_0x0801
        L_0x07f1:
            if (r29 == 0) goto L_0x07f5
            long r9 = -r4
            goto L_0x0801
        L_0x07f5:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r0 == 0) goto L_0x0800
            long r9 = r11.getSenderId()
            goto L_0x0801
        L_0x0800:
            r9 = r4
        L_0x0801:
            java.lang.Object r0 = r1.get(r9)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            java.lang.String r69 = ""
            r23 = 0
            r72 = r3[r23]
            if (r72 != 0) goto L_0x0880
            if (r19 == 0) goto L_0x0872
            boolean r72 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            if (r72 == 0) goto L_0x0854
            if (r29 == 0) goto L_0x083d
            r72 = r12
            int r12 = android.os.Build.VERSION.SDK_INT
            r73 = r13
            r13 = 27
            if (r12 <= r13) goto L_0x0832
            r13 = r47
            r12 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            java.lang.String r69 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r14 = r45
            r12 = r69
            goto L_0x0891
        L_0x0832:
            r13 = r47
            r12 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            r14 = r45
            r12 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            goto L_0x087d
        L_0x083d:
            r72 = r12
            r73 = r13
            r13 = r47
            r12 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            r14 = 2131626807(0x7f0e0b37, float:1.888086E38)
            java.lang.String r12 = "NotificationHiddenChatUserName"
            java.lang.String r69 = org.telegram.messenger.LocaleController.getString(r12, r14)
            r14 = r45
            r12 = r69
            goto L_0x0891
        L_0x0854:
            r72 = r12
            r73 = r13
            r13 = r47
            int r12 = android.os.Build.VERSION.SDK_INT
            r14 = 27
            if (r12 <= r14) goto L_0x086c
            r14 = r45
            r12 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            java.lang.String r69 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r12 = r69
            goto L_0x0891
        L_0x086c:
            r14 = r45
            r12 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            goto L_0x087d
        L_0x0872:
            r72 = r12
            r73 = r13
            r14 = r45
            r13 = r47
            r12 = 2131626809(0x7f0e0b39, float:1.8880865E38)
        L_0x087d:
            r12 = r69
            goto L_0x0891
        L_0x0880:
            r72 = r12
            r73 = r13
            r14 = r45
            r13 = r47
            r12 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            r23 = 0
            r69 = r3[r23]
            r12 = r69
        L_0x0891:
            if (r0 == 0) goto L_0x08a9
            r45 = r3
            java.lang.CharSequence r3 = r0.getName()
            boolean r3 = android.text.TextUtils.equals(r3, r12)
            if (r3 != 0) goto L_0x08a0
            goto L_0x08ab
        L_0x08a0:
            r3 = r0
            r69 = r12
            r75 = r13
            r77 = r14
            goto L_0x095a
        L_0x08a9:
            r45 = r3
        L_0x08ab:
            androidx.core.app.Person$Builder r3 = new androidx.core.app.Person$Builder
            r3.<init>()
            androidx.core.app.Person$Builder r3 = r3.setName(r12)
            r23 = 0
            boolean r47 = r71[r23]
            if (r47 == 0) goto L_0x094a
            boolean r47 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r47 != 0) goto L_0x094a
            r47 = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            r69 = r12
            r12 = 28
            if (r0 < r12) goto L_0x0945
            r0 = 0
            boolean r12 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r12 != 0) goto L_0x0939
            if (r29 == 0) goto L_0x08db
            r76 = r0
            r75 = r13
            r77 = r14
            goto L_0x093f
        L_0x08db:
            r75 = r13
            long r12 = r11.getSenderId()
            r76 = r0
            org.telegram.messenger.MessagesController r0 = r86.getMessagesController()
            r77 = r14
            java.lang.Long r14 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r14)
            if (r0 != 0) goto L_0x090b
            org.telegram.messenger.MessagesStorage r14 = r86.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r0 = r14.getUserSync(r12)
            if (r0 == 0) goto L_0x0908
            org.telegram.messenger.MessagesController r14 = r86.getMessagesController()
            r78 = r12
            r12 = 1
            r14.putUser(r0, r12)
            goto L_0x090d
        L_0x0908:
            r78 = r12
            goto L_0x090d
        L_0x090b:
            r78 = r12
        L_0x090d:
            if (r0 == 0) goto L_0x0936
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            if (r12 == 0) goto L_0x0936
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
            if (r12 == 0) goto L_0x0936
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
            long r12 = r12.volume_id
            int r14 = (r12 > r37 ? 1 : (r12 == r37 ? 0 : -1))
            if (r14 == 0) goto L_0x0936
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
            int r12 = r12.local_id
            if (r12 == 0) goto L_0x0936
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r0.photo
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
            r13 = 1
            java.io.File r12 = org.telegram.messenger.FileLoader.getPathToAttach(r12, r13)
            r0 = r12
            goto L_0x0941
        L_0x0936:
            r0 = r76
            goto L_0x0941
        L_0x0939:
            r76 = r0
            r75 = r13
            r77 = r14
        L_0x093f:
            r0 = r51
        L_0x0941:
            r15.loadRoundAvatar(r0, r3)
            goto L_0x0952
        L_0x0945:
            r75 = r13
            r77 = r14
            goto L_0x0952
        L_0x094a:
            r47 = r0
            r69 = r12
            r75 = r13
            r77 = r14
        L_0x0952:
            androidx.core.app.Person r0 = r3.build()
            r1.put(r9, r0)
            r3 = r0
        L_0x095a:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r0 != 0) goto L_0x0ace
            r12 = 0
            r13 = 0
            boolean r0 = r71[r13]
            if (r0 == 0) goto L_0x0a73
            int r0 = android.os.Build.VERSION.SDK_INT
            r13 = 28
            if (r0 < r13) goto L_0x0a73
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r14 = "activity"
            java.lang.Object r0 = r0.getSystemService(r14)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x0a73
            if (r19 != 0) goto L_0x0a6a
            boolean r0 = r11.isSecretMedia()
            if (r0 != 0) goto L_0x0a6a
            int r0 = r11.type
            r14 = 1
            if (r0 == r14) goto L_0x099a
            boolean r0 = r11.isSticker()
            if (r0 == 0) goto L_0x0990
            goto L_0x099a
        L_0x0990:
            r47 = r1
            r78 = r9
            r13 = r72
            r72 = r2
            goto L_0x0a7b
        L_0x099a:
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner
            java.io.File r14 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r13 = r11.messageOwner
            int r13 = r13.date
            r78 = r9
            long r9 = (long) r13
            long r9 = r9 * r66
            r0.<init>((java.lang.CharSequence) r7, (long) r9, (androidx.core.app.Person) r3)
            r9 = r0
            boolean r0 = r11.isSticker()
            if (r0 == 0) goto L_0x09b8
            java.lang.String r0 = "image/webp"
            goto L_0x09ba
        L_0x09b8:
            java.lang.String r0 = "image/jpeg"
        L_0x09ba:
            r10 = r0
            boolean r0 = r14.exists()
            if (r0 == 0) goto L_0x09d2
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x09ca }
            java.lang.String r13 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r0, r13, r14)     // Catch:{ Exception -> 0x09ca }
            goto L_0x09cf
        L_0x09ca:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x09cf:
            r47 = r1
            goto L_0x0a28
        L_0x09d2:
            org.telegram.messenger.FileLoader r0 = r86.getFileLoader()
            java.lang.String r13 = r14.getName()
            boolean r0 = r0.isLoadingFile(r13)
            if (r0 == 0) goto L_0x0a25
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
            r47 = r1
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
            goto L_0x0a28
        L_0x0a25:
            r47 = r1
            r0 = 0
        L_0x0a28:
            if (r0 == 0) goto L_0x0a61
            r9.setData(r10, r0)
            r13 = r72
            r13.addMessage(r9)
            r1 = r0
            r72 = r2
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r76 = r9
            java.lang.String r9 = "com.android.systemui"
            r80 = r10
            r10 = 1
            r2.grantUriPermission(r9, r0, r10)
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda35 r2 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda35
            r2.<init>(r1)
            r9 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r9)
            java.lang.CharSequence r2 = r11.caption
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0a5f
            java.lang.CharSequence r2 = r11.caption
            org.telegram.tgnet.TLRPC$Message r9 = r11.messageOwner
            int r9 = r9.date
            long r9 = (long) r9
            long r9 = r9 * r66
            r13.addMessage((java.lang.CharSequence) r2, (long) r9, (androidx.core.app.Person) r3)
        L_0x0a5f:
            r12 = 1
            goto L_0x0a7b
        L_0x0a61:
            r76 = r9
            r80 = r10
            r13 = r72
            r72 = r2
            goto L_0x0a7b
        L_0x0a6a:
            r47 = r1
            r78 = r9
            r13 = r72
            r72 = r2
            goto L_0x0a7b
        L_0x0a73:
            r47 = r1
            r78 = r9
            r13 = r72
            r72 = r2
        L_0x0a7b:
            if (r12 != 0) goto L_0x0a87
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner
            int r0 = r0.date
            long r0 = (long) r0
            long r0 = r0 * r66
            r13.addMessage((java.lang.CharSequence) r7, (long) r0, (androidx.core.app.Person) r3)
        L_0x0a87:
            r1 = 0
            boolean r0 = r71[r1]
            if (r0 == 0) goto L_0x0acd
            if (r19 != 0) goto L_0x0acd
            boolean r0 = r11.isVoice()
            if (r0 == 0) goto L_0x0acd
            java.util.List r1 = r13.getMessages()
            boolean r0 = r1.isEmpty()
            if (r0 != 0) goto L_0x0acd
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r9 = 24
            if (r0 < r9) goto L_0x0ab6
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0ab3 }
            java.lang.String r9 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r0, r9, r2)     // Catch:{ Exception -> 0x0ab3 }
            goto L_0x0ab5
        L_0x0ab3:
            r0 = move-exception
            r0 = 0
        L_0x0ab5:
            goto L_0x0aba
        L_0x0ab6:
            android.net.Uri r0 = android.net.Uri.fromFile(r2)
        L_0x0aba:
            if (r0 == 0) goto L_0x0acd
            int r9 = r1.size()
            r10 = 1
            int r9 = r9 - r10
            java.lang.Object r9 = r1.get(r9)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r9 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r9
            java.lang.String r10 = "audio/ogg"
            r9.setData(r10, r0)
        L_0x0acd:
            goto L_0x0ae0
        L_0x0ace:
            r47 = r1
            r78 = r9
            r13 = r72
            r72 = r2
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner
            int r0 = r0.date
            long r0 = (long) r0
            long r0 = r0 * r66
            r13.addMessage((java.lang.CharSequence) r7, (long) r0, (androidx.core.app.Person) r3)
        L_0x0ae0:
            int r0 = (r4 > r35 ? 1 : (r4 == r35 ? 0 : -1))
            if (r0 != 0) goto L_0x0af8
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x0af8
            org.telegram.tgnet.TLRPC$Message r0 = r11.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r1 = r11.getId()
            r63 = r0
            r65 = r1
        L_0x0af8:
            int r6 = r6 + -1
            r9 = r13
            r3 = r45
            r1 = r47
            r11 = r64
            r7 = r68
            r12 = r70
            r10 = r71
            r2 = r72
            r13 = r73
            r47 = r75
            r45 = r77
            goto L_0x0709
        L_0x0b11:
            r47 = r1
            r45 = r3
            r68 = r7
            r71 = r10
            r64 = r11
            r70 = r12
            r73 = r13
            r13 = r9
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
            if (r1 == 0) goto L_0x0b61
            int r1 = org.telegram.messenger.DialogObject.getEncryptedChatId(r4)
            java.lang.String r2 = "encId"
            r0.putExtra(r2, r1)
            goto L_0x0b73
        L_0x0b61:
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r1 == 0) goto L_0x0b6d
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r4)
            goto L_0x0b73
        L_0x0b6d:
            long r1 = -r4
            java.lang.String r3 = "chatId"
            r0.putExtra(r3, r1)
        L_0x0b73:
            int r1 = r15.currentAccount
            r2 = r68
            r0.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r3 = 1073741824(0x40000000, float:2.0)
            r6 = 0
            android.app.PendingIntent r14 = android.app.PendingIntent.getActivity(r1, r6, r0, r3)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r12 = r1
            if (r62 == 0) goto L_0x0b91
            r10 = r62
            r12.addAction(r10)
            goto L_0x0b93
        L_0x0b91:
            r10 = r62
        L_0x0b93:
            android.content.Intent r1 = new android.content.Intent
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r6 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r1.<init>(r3, r6)
            r11 = r1
            r1 = 32
            r11.addFlags(r1)
            java.lang.String r1 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r11.setAction(r1)
            r1 = r61
            r11.putExtra(r1, r4)
            r1 = r56
            r9 = r57
            r11.putExtra(r1, r9)
            int r1 = r15.currentAccount
            r11.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r3 = r53.intValue()
            r6 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r7 = android.app.PendingIntent.getBroadcast(r1, r3, r11, r6)
            androidx.core.app.NotificationCompat$Action$Builder r1 = new androidx.core.app.NotificationCompat$Action$Builder
            r3 = 2131165716(0x7var_, float:1.7945657E38)
            r6 = 2131626389(0x7f0e0995, float:1.8880013E38)
            r28 = r0
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
            if (r1 != 0) goto L_0x0c2a
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r4)
            if (r1 == 0) goto L_0x0c0e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "tguser"
            r1.append(r6)
            r1.append(r4)
            r1.append(r3)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            r35 = r7
            r7 = r1
            goto L_0x0CLASSNAME
        L_0x0c0e:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "tgchat"
            r1.append(r6)
            r35 = r7
            long r6 = -r4
            r1.append(r6)
            r1.append(r3)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            r7 = r1
            goto L_0x0CLASSNAME
        L_0x0c2a:
            r35 = r7
            long r6 = globalSecretChatId
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x0c4f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "tgenc"
            r1.append(r6)
            int r6 = org.telegram.messenger.DialogObject.getEncryptedChatId(r4)
            r1.append(r6)
            r1.append(r3)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            r7 = r1
            goto L_0x0CLASSNAME
        L_0x0c4f:
            r1 = 0
            r7 = r1
        L_0x0CLASSNAME:
            if (r7 == 0) goto L_0x0c7b
            r12.setDismissalId(r7)
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
            r57 = r9
            r62 = r10
            r9 = r73
            r6.extend(r1)
            goto L_0x0CLASSNAME
        L_0x0c7b:
            r6 = r87
            r57 = r9
            r62 = r10
            r9 = r73
        L_0x0CLASSNAME:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "tgaccount"
            r1.append(r3)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            r12.setBridgeTag(r1)
            r3 = r64
            r1 = 0
            java.lang.Object r36 = r3.get(r1)
            r1 = r36
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            int r1 = r1.date
            r36 = r7
            long r6 = (long) r1
            long r6 = r6 * r66
            androidx.core.app.NotificationCompat$Builder r1 = new androidx.core.app.NotificationCompat$Builder
            r73 = r9
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext
            r1.<init>(r9)
            r10 = r70
            androidx.core.app.NotificationCompat$Builder r1 = r1.setContentTitle(r10)
            r9 = 2131165961(0x7var_, float:1.7946154E38)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setSmallIcon((int) r9)
            java.lang.String r9 = r8.toString()
            androidx.core.app.NotificationCompat$Builder r1 = r1.setContentText(r9)
            r9 = 1
            androidx.core.app.NotificationCompat$Builder r1 = r1.setAutoCancel(r9)
            int r9 = r3.size()
            androidx.core.app.NotificationCompat$Builder r1 = r1.setNumber(r9)
            r9 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setColor(r9)
            r9 = 0
            androidx.core.app.NotificationCompat$Builder r1 = r1.setGroupSummary(r9)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setWhen(r6)
            r9 = 1
            androidx.core.app.NotificationCompat$Builder r1 = r1.setShowWhen(r9)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setStyle(r13)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setContentIntent(r14)
            androidx.core.app.NotificationCompat$Builder r1 = r1.extend(r12)
            r37 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r37 = r37 - r6
            java.lang.String r9 = java.lang.String.valueOf(r37)
            androidx.core.app.NotificationCompat$Builder r1 = r1.setSortKey(r9)
            java.lang.String r9 = "msg"
            androidx.core.app.NotificationCompat$Builder r9 = r1.setCategory(r9)
            android.content.Intent r1 = new android.content.Intent
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            r37 = r6
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r6 = org.telegram.messenger.NotificationDismissReceiver.class
            r1.<init>(r3, r6)
            r7 = r1
            java.lang.String r1 = "messageDate"
            r3 = r43
            r7.putExtra(r1, r3)
            java.lang.String r1 = "dialogId"
            r7.putExtra(r1, r4)
            int r1 = r15.currentAccount
            r7.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r6 = r53.intValue()
            r3 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r1, r6, r7, r3)
            r9.setDeleteIntent(r1)
            if (r42 == 0) goto L_0x0d43
            java.lang.String r1 = r15.notificationGroup
            r9.setGroup(r1)
            r1 = 1
            r9.setGroupAlertBehavior(r1)
        L_0x0d43:
            if (r62 == 0) goto L_0x0d4b
            r6 = r62
            r9.addAction(r6)
            goto L_0x0d4d
        L_0x0d4b:
            r6 = r62
        L_0x0d4d:
            if (r19 != 0) goto L_0x0d52
            r9.addAction(r0)
        L_0x0d52:
            int r1 = r26.size()
            r3 = 1
            if (r1 != r3) goto L_0x0d65
            boolean r1 = android.text.TextUtils.isEmpty(r88)
            if (r1 != 0) goto L_0x0d65
            r3 = r88
            r9.setSubText(r3)
            goto L_0x0d67
        L_0x0d65:
            r3 = r88
        L_0x0d67:
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r4)
            if (r1 == 0) goto L_0x0d72
            r1 = 1
            r9.setLocalOnly(r1)
            goto L_0x0d73
        L_0x0d72:
            r1 = 1
        L_0x0d73:
            if (r33 == 0) goto L_0x0d7d
            r62 = r6
            r6 = r33
            r9.setLargeIcon(r6)
            goto L_0x0d81
        L_0x0d7d:
            r62 = r6
            r6 = r33
        L_0x0d81:
            r18 = 0
            boolean r33 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r18)
            if (r33 != 0) goto L_0x0e6c
            boolean r18 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r18 != 0) goto L_0x0e6c
            if (r63 == 0) goto L_0x0e5c
            r18 = 0
            int r1 = r63.size()
            r33 = r0
            r0 = r18
        L_0x0d99:
            if (r0 >= r1) goto L_0x0e4c
            r18 = r6
            r6 = r63
            java.lang.Object r39 = r6.get(r0)
            r40 = r1
            r1 = r39
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r1 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r1
            r39 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r3 = r1.buttons
            int r3 = r3.size()
            r46 = r6
            r6 = r39
        L_0x0db5:
            if (r6 >= r3) goto L_0x0e2b
            r39 = r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r3 = r1.buttons
            java.lang.Object r3 = r3.get(r6)
            org.telegram.tgnet.TLRPC$KeyboardButton r3 = (org.telegram.tgnet.TLRPC.KeyboardButton) r3
            r56 = r1
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback
            if (r1 == 0) goto L_0x0e0b
            android.content.Intent r1 = new android.content.Intent
            r61 = r7
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            r63 = r8
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r8 = org.telegram.messenger.NotificationCallbackReceiver.class
            r1.<init>(r7, r8)
            int r7 = r15.currentAccount
            r1.putExtra(r2, r7)
            java.lang.String r7 = "did"
            r1.putExtra(r7, r4)
            byte[] r7 = r3.data
            if (r7 == 0) goto L_0x0de9
            byte[] r7 = r3.data
            java.lang.String r8 = "data"
            r1.putExtra(r8, r7)
        L_0x0de9:
            java.lang.String r7 = "mid"
            r8 = r65
            r1.putExtra(r7, r8)
            java.lang.String r7 = r3.text
            r68 = r2
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            r65 = r3
            int r3 = r15.lastButtonId
            r66 = r4
            int r4 = r3 + 1
            r15.lastButtonId = r4
            r4 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r2, r3, r1, r4)
            r5 = 0
            r9.addAction(r5, r7, r2)
            goto L_0x0e1a
        L_0x0e0b:
            r68 = r2
            r66 = r4
            r61 = r7
            r63 = r8
            r8 = r65
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 0
            r65 = r3
        L_0x0e1a:
            int r6 = r6 + 1
            r65 = r8
            r3 = r39
            r1 = r56
            r7 = r61
            r8 = r63
            r4 = r66
            r2 = r68
            goto L_0x0db5
        L_0x0e2b:
            r56 = r1
            r68 = r2
            r39 = r3
            r66 = r4
            r61 = r7
            r63 = r8
            r8 = r65
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 0
            int r0 = r0 + 1
            r3 = r88
            r6 = r18
            r1 = r40
            r8 = r63
            r4 = r66
            r63 = r46
            goto L_0x0d99
        L_0x0e4c:
            r40 = r1
            r66 = r4
            r18 = r6
            r61 = r7
            r46 = r63
            r5 = 0
            r63 = r8
            r8 = r65
            goto L_0x0e7b
        L_0x0e5c:
            r33 = r0
            r66 = r4
            r18 = r6
            r61 = r7
            r46 = r63
            r5 = 0
            r63 = r8
            r8 = r65
            goto L_0x0e7b
        L_0x0e6c:
            r33 = r0
            r66 = r4
            r18 = r6
            r61 = r7
            r46 = r63
            r5 = 0
            r63 = r8
            r8 = r65
        L_0x0e7b:
            if (r48 != 0) goto L_0x0ea4
            if (r31 == 0) goto L_0x0ea4
            r6 = r31
            java.lang.String r0 = r6.phone
            if (r0 == 0) goto L_0x0ea6
            java.lang.String r0 = r6.phone
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0ea6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "tel:+"
            r0.append(r1)
            java.lang.String r1 = r6.phone
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r9.addPerson((java.lang.String) r0)
            goto L_0x0ea6
        L_0x0ea4:
            r6 = r31
        L_0x0ea6:
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 26
            if (r0 < r7) goto L_0x0eb4
            r2 = r42
            r4 = r44
            r15.setNotificationChannel(r4, r9, r2)
            goto L_0x0eb8
        L_0x0eb4:
            r2 = r42
            r4 = r44
        L_0x0eb8:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            r3 = r47
            r16 = 1
            r1 = r0
            int r23 = r53.intValue()
            r81 = r3
            r39 = r43
            r42 = r45
            r40 = r51
            r31 = r64
            r3 = r23
            r23 = r2
            r43 = r18
            r2 = r86
            r16 = r4
            r17 = 0
            r44 = 27
            r45 = 1
            r4 = r66
            r47 = r6
            r82 = r25
            r25 = r32
            r32 = r60
            r18 = r62
            r6 = r10
            r49 = r41
            r51 = 26
            r41 = r36
            r36 = r35
            r35 = r48
            r48 = r61
            r7 = r47
            r55 = r57
            r17 = r59
            r56 = r63
            r57 = 0
            r63 = r8
            r8 = r35
            r64 = r9
            r62 = r13
            r61 = r54
            r60 = r58
            r58 = r73
            r54 = r17
            r17 = r10
            r65 = r18
            r13 = r53
            r53 = r71
            r10 = r91
            r68 = r11
            r11 = r92
            r70 = r12
            r83 = r16
            r69 = r17
            r12 = r93
            r84 = r13
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
            r1 = r66
            r3 = r84
            r0.put(r1, r3)
        L_0x0var_:
            int r5 = r21 + 1
            r6 = r14
            r4 = r20
            r11 = r22
            r2 = r23
            r3 = r24
            r10 = r26
            r9 = r27
            r7 = r49
            r13 = r58
            r1 = r81
            r12 = r83
            r8 = 0
            goto L_0x0114
        L_0x0var_:
            r81 = r1
            r23 = r2
            r24 = r3
            r20 = r4
            r21 = r5
            r49 = r7
            r27 = r9
            r26 = r10
            r22 = r11
            r83 = r12
            r58 = r13
            r14 = r6
        L_0x0f7a:
            if (r23 == 0) goto L_0x0fc8
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0var_
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r15.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0var_:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager     // Catch:{ SecurityException -> 0x0fa4 }
            int r1 = r15.notificationId     // Catch:{ SecurityException -> 0x0fa4 }
            r13 = r83
            r0.notify(r1, r13)     // Catch:{ SecurityException -> 0x0fa2 }
            r16 = r13
            goto L_0x0fd9
        L_0x0fa2:
            r0 = move-exception
            goto L_0x0fa7
        L_0x0fa4:
            r0 = move-exception
            r13 = r83
        L_0x0fa7:
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
            goto L_0x0fd9
        L_0x0fc8:
            r16 = r83
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0fd9
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.cancel(r1)
        L_0x0fd9:
            r0 = 0
        L_0x0fda:
            int r1 = r49.size()
            if (r0 >= r1) goto L_0x101f
            r9 = r49
            long r1 = r9.keyAt(r0)
            java.util.HashSet<java.lang.Long> r3 = r15.openedInBubbleDialogs
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            boolean r3 = r3.contains(r4)
            if (r3 == 0) goto L_0x0ff3
            goto L_0x101a
        L_0x0ff3:
            java.lang.Object r3 = r9.valueAt(r0)
            java.lang.Integer r3 = (java.lang.Integer) r3
            boolean r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r4 == 0) goto L_0x1011
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "cancel notification id "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r4 = r4.toString()
            org.telegram.messenger.FileLog.d(r4)
        L_0x1011:
            androidx.core.app.NotificationManagerCompat r4 = notificationManager
            int r5 = r3.intValue()
            r4.cancel(r5)
        L_0x101a:
            int r0 = r0 + 1
            r49 = r9
            goto L_0x0fda
        L_0x101f:
            r9 = r49
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r14.size()
            r0.<init>(r1)
            r1 = 0
            int r10 = r14.size()
            r11 = r1
        L_0x1030:
            if (r11 >= r10) goto L_0x108c
            java.lang.Object r1 = r14.get(r11)
            r12 = r1
            org.telegram.messenger.NotificationsController$1NotificationHolder r12 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r12
            r0.clear()
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 29
            if (r1 < r2) goto L_0x106d
            long r1 = r12.dialogId
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
            if (r1 != 0) goto L_0x106d
            androidx.core.app.NotificationCompat$Builder r2 = r12.notification
            long r3 = r12.dialogId
            java.lang.String r5 = r12.name
            org.telegram.tgnet.TLRPC$User r6 = r12.user
            org.telegram.tgnet.TLRPC$Chat r7 = r12.chat
            r49 = r9
            long r8 = r12.dialogId
            r13 = r81
            java.lang.Object r1 = r13.get(r8)
            r8 = r1
            androidx.core.app.Person r8 = (androidx.core.app.Person) r8
            r1 = r86
            java.lang.String r1 = r1.createNotificationShortcut(r2, r3, r5, r6, r7, r8)
            if (r1 == 0) goto L_0x1071
            r0.add(r1)
            goto L_0x1071
        L_0x106d:
            r49 = r9
            r13 = r81
        L_0x1071:
            r12.call()
            boolean r1 = r86.unsupportedNotificationShortcut()
            if (r1 != 0) goto L_0x1085
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x1085
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r1, r0)
        L_0x1085:
            int r11 = r11 + 1
            r81 = r13
            r9 = r49
            goto L_0x1030
        L_0x108c:
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
    public /* synthetic */ void m1046x9var_e1fc() {
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
            long soundDocumentId = preferences.getLong("sound_document_id_" + dialogId, 0);
            String soundPath = preferences.getString("sound_path_" + dialogId, (String) null);
            TLRPC.TL_inputPeerNotifySettings tL_inputPeerNotifySettings3 = req.settings;
            tL_inputPeerNotifySettings3.flags = tL_inputPeerNotifySettings3.flags | 8;
            if (soundDocumentId != 0) {
                TLRPC.TL_notificationSoundRingtone ringtoneSound = new TLRPC.TL_notificationSoundRingtone();
                ringtoneSound.id = soundDocumentId;
                req.settings.sound = ringtoneSound;
            } else if (soundPath == null) {
                req.settings.sound = new TLRPC.TL_notificationSoundDefault();
            } else if (soundPath.equals("NoSound")) {
                req.settings.sound = new TLRPC.TL_notificationSoundNone();
            } else {
                TLRPC.TL_notificationSoundLocal localSound = new TLRPC.TL_notificationSoundLocal();
                localSound.title = preferences.getString("sound_" + dialogId, (String) null);
                localSound.data = soundPath;
                req.settings.sound = localSound;
            }
            req.peer = new TLRPC.TL_inputNotifyPeer();
            ((TLRPC.TL_inputNotifyPeer) req.peer).peer = getMessagesController().getInputPeer(dialogId);
            getConnectionsManager().sendRequest(req, NotificationsController$$ExternalSyntheticLambda32.INSTANCE);
        }
    }

    static /* synthetic */ void lambda$updateServerNotificationsSettings$39(TLObject response, TLRPC.TL_error error) {
    }

    public void updateServerNotificationsSettings(int type) {
        String soundPathPref;
        String soundDocumentIdPref;
        String soundNamePref;
        SharedPreferences preferences = getAccountInstance().getNotificationsSettings();
        TLRPC.TL_account_updateNotifySettings req = new TLRPC.TL_account_updateNotifySettings();
        req.settings = new TLRPC.TL_inputPeerNotifySettings();
        req.settings.flags = 5;
        if (type == 0) {
            req.peer = new TLRPC.TL_inputNotifyChats();
            req.settings.mute_until = preferences.getInt("EnableGroup2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewGroup", true);
            soundNamePref = "GroupSound";
            soundDocumentIdPref = "GroupSoundDocId";
            soundPathPref = "GroupSoundPath";
        } else if (type == 1) {
            req.peer = new TLRPC.TL_inputNotifyUsers();
            req.settings.mute_until = preferences.getInt("EnableAll2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewAll", true);
            soundNamePref = "GlobalSound";
            soundDocumentIdPref = "GlobalSoundDocId";
            soundPathPref = "GlobalSoundPath";
        } else {
            req.peer = new TLRPC.TL_inputNotifyBroadcasts();
            req.settings.mute_until = preferences.getInt("EnableChannel2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewChannel", true);
            soundNamePref = "ChannelSound";
            soundDocumentIdPref = "ChannelSoundDocId";
            soundPathPref = "ChannelSoundPath";
        }
        req.settings.flags |= 8;
        long soundDocumentId = preferences.getLong(soundDocumentIdPref, 0);
        String soundPath = preferences.getString(soundPathPref, "NoSound");
        if (soundDocumentId != 0) {
            TLRPC.TL_notificationSoundRingtone ringtoneSound = new TLRPC.TL_notificationSoundRingtone();
            ringtoneSound.id = soundDocumentId;
            req.settings.sound = ringtoneSound;
        } else if (soundPath == null) {
            req.settings.sound = new TLRPC.TL_notificationSoundDefault();
        } else if (soundPath.equals("NoSound")) {
            req.settings.sound = new TLRPC.TL_notificationSoundNone();
        } else {
            TLRPC.TL_notificationSoundLocal localSound = new TLRPC.TL_notificationSoundLocal();
            localSound.title = preferences.getString(soundNamePref, (String) null);
            localSound.data = soundPath;
            req.settings.sound = localSound;
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

    public void muteDialog(long dialog_id, boolean mute) {
        if (mute) {
            getInstance(this.currentAccount).muteUntil(dialog_id, Integer.MAX_VALUE);
            return;
        }
        boolean defaultEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(dialog_id);
        SharedPreferences.Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        if (defaultEnabled) {
            editor.remove("notify2_" + dialog_id);
        } else {
            editor.putInt("notify2_" + dialog_id, 0);
        }
        getMessagesStorage().setDialogFlags(dialog_id, 0);
        editor.apply();
        TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(dialog_id);
        if (dialog != null) {
            dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
        }
        updateServerNotificationsSettings(dialog_id);
    }
}
