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
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
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
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC$TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC$TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC$TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC$TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp;
import org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL;
import org.telegram.tgnet.TLRPC$TL_messageActionUserJoined;
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
    private int lastInternalNotificationId;
    public long lastNotificationChannelCreateTime;
    private int lastOnlineFromOtherDevice = 0;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private LongSparseArray<Integer> lastWearNotifiedMessageId = new LongSparseArray<>();
    private String launcherClassName;
    private Runnable notificationDelayRunnable;
    private PowerManager.WakeLock notificationDelayWakelock;
    private String notificationGroup;
    private int notificationId;
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

    public static String getGlobalNotificationsKey(int i) {
        return i == 0 ? "EnableGroup2" : i == 1 ? "EnableAll2" : "EnableChannel2";
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateServerNotificationsSettings$39(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateServerNotificationsSettings$40(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        this.lastInternalNotificationId = (i * 1000000) + 5000;
        this.notificationId = this.currentAccount + 1;
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
        this.notificationDelayRunnable = new NotificationsController$$ExternalSyntheticLambda8(this);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
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
        this.channelGroupsCreated = false;
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda11(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanup$1() {
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
    public /* synthetic */ void lambda$setOpenedDialogId$2(long j) {
        this.openedDialogId = j;
    }

    public void setOpenedDialogId(long j) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda22(this, j));
    }

    public void setOpenedInBubble(long j, boolean z) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda37(this, z, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setOpenedInBubble$3(boolean z, long j) {
        if (z) {
            this.openedInBubbleDialogs.add(Long.valueOf(j));
        } else {
            this.openedInBubbleDialogs.remove(Long.valueOf(j));
        }
    }

    public void setLastOnlineFromOtherDevice(int i) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda18(this, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setLastOnlineFromOtherDevice$4(int i) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set last online from other device = " + i);
        }
        this.lastOnlineFromOtherDevice = i;
    }

    public void removeNotificationsForDialog(long j) {
        processReadMessages((LongSparseIntArray) null, j, 0, Integer.MAX_VALUE, false);
        LongSparseIntArray longSparseIntArray = new LongSparseIntArray();
        longSparseIntArray.put(j, 0);
        processDialogsUpdateRead(longSparseIntArray);
    }

    public boolean hasMessagesToReply() {
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialogId) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$forceShowPopupForReply$6() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialogId) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                arrayList.add(0, messageObject);
            }
        }
        if (!arrayList.isEmpty() && !AndroidUtilities.needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda29(this, arrayList));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$forceShowPopupForReply$5(ArrayList arrayList) {
        this.popupReplyMessages = arrayList;
        Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
        intent.putExtra("force", true);
        intent.putExtra("currentAccount", this.currentAccount);
        intent.setFlags(NUM);
        ApplicationLoader.applicationContext.startActivity(intent);
        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    public void removeDeletedMessagesFromNotifications(LongSparseArray<ArrayList<Integer>> longSparseArray) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda25(this, longSparseArray, new ArrayList(0)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$9(LongSparseArray longSparseArray, ArrayList arrayList) {
        Integer num;
        ArrayList arrayList2;
        Integer num2;
        LongSparseArray longSparseArray2 = longSparseArray;
        ArrayList arrayList3 = arrayList;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        int i2 = 0;
        int i3 = 0;
        while (i3 < longSparseArray.size()) {
            long keyAt = longSparseArray2.keyAt(i3);
            SparseArray sparseArray = this.pushMessagesDict.get(keyAt);
            if (sparseArray == null) {
                num = i2;
            } else {
                ArrayList arrayList4 = (ArrayList) longSparseArray2.get(keyAt);
                int size = arrayList4.size();
                int i4 = 0;
                while (i4 < size) {
                    int intValue = ((Integer) arrayList4.get(i4)).intValue();
                    MessageObject messageObject = (MessageObject) sparseArray.get(intValue);
                    Integer num3 = i2;
                    if (messageObject != null) {
                        long dialogId = messageObject.getDialogId();
                        Integer num4 = this.pushDialogs.get(dialogId);
                        if (num4 == null) {
                            num4 = num3;
                        }
                        Integer valueOf = Integer.valueOf(num4.intValue() - 1);
                        if (valueOf.intValue() <= 0) {
                            this.smartNotificationsDialogs.remove(dialogId);
                            num2 = num3;
                        } else {
                            num2 = valueOf;
                        }
                        if (!num2.equals(num4)) {
                            arrayList2 = arrayList4;
                            int intValue2 = this.total_unread_count - num4.intValue();
                            this.total_unread_count = intValue2;
                            this.total_unread_count = intValue2 + num2.intValue();
                            this.pushDialogs.put(dialogId, num2);
                        } else {
                            arrayList2 = arrayList4;
                        }
                        if (num2.intValue() == 0) {
                            this.pushDialogs.remove(dialogId);
                            this.pushDialogsOverrideMention.remove(dialogId);
                        }
                        sparseArray.remove(intValue);
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList3.add(messageObject);
                    } else {
                        arrayList2 = arrayList4;
                    }
                    i4++;
                    LongSparseArray longSparseArray3 = longSparseArray;
                    i2 = num3;
                    arrayList4 = arrayList2;
                }
                num = i2;
                if (sparseArray.size() == 0) {
                    this.pushMessagesDict.remove(keyAt);
                }
            }
            i3++;
            longSparseArray2 = longSparseArray;
            i2 = num;
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda27(this, arrayList3));
        }
        if (i != this.total_unread_count) {
            if (!this.notifyCheck) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            } else {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > getConnectionsManager().getCurrentTime());
            }
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda15(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$7(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$8(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void removeDeletedHisoryFromNotifications(LongSparseIntArray longSparseIntArray) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda35(this, longSparseIntArray, new ArrayList(0)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$12(LongSparseIntArray longSparseIntArray, ArrayList arrayList) {
        boolean z;
        Integer num;
        LongSparseIntArray longSparseIntArray2 = longSparseIntArray;
        ArrayList arrayList2 = arrayList;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        int i2 = 0;
        int i3 = 0;
        while (true) {
            z = true;
            if (i3 >= longSparseIntArray.size()) {
                break;
            }
            long keyAt = longSparseIntArray2.keyAt(i3);
            long j = -keyAt;
            long j2 = (long) longSparseIntArray2.get(keyAt);
            Integer num2 = this.pushDialogs.get(j);
            if (num2 == null) {
                num2 = i2;
            }
            Integer num3 = num2;
            int i4 = 0;
            while (i4 < this.pushMessages.size()) {
                MessageObject messageObject = this.pushMessages.get(i4);
                if (messageObject.getDialogId() == j) {
                    num = i2;
                    if (((long) messageObject.getId()) <= j2) {
                        SparseArray sparseArray = this.pushMessagesDict.get(j);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(j);
                            }
                        }
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        i4--;
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList2.add(messageObject);
                        num3 = Integer.valueOf(num3.intValue() - 1);
                    }
                } else {
                    num = i2;
                }
                i4++;
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
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda28(this, arrayList2));
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
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda19(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$10(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$11(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processReadMessages(LongSparseIntArray longSparseIntArray, long j, int i, int i2, boolean z) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda36(this, longSparseIntArray, new ArrayList(0), j, i2, i, z));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00d7, code lost:
        r8 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processReadMessages$14(org.telegram.messenger.support.LongSparseIntArray r19, java.util.ArrayList r20, long r21, int r23, int r24, boolean r25) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r20
            r3 = r23
            r4 = r24
            r6 = 0
            r8 = 1
            if (r1 == 0) goto L_0x0098
            r9 = 0
        L_0x0010:
            int r10 = r19.size()
            if (r9 >= r10) goto L_0x0098
            long r10 = r1.keyAt(r9)
            int r12 = r1.get(r10)
            r13 = 0
        L_0x001f:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r14 = r0.pushMessages
            int r14 = r14.size()
            if (r13 >= r14) goto L_0x0091
            java.util.ArrayList<org.telegram.messenger.MessageObject> r14 = r0.pushMessages
            java.lang.Object r14 = r14.get(r13)
            org.telegram.messenger.MessageObject r14 = (org.telegram.messenger.MessageObject) r14
            org.telegram.tgnet.TLRPC$Message r15 = r14.messageOwner
            boolean r15 = r15.from_scheduled
            if (r15 != 0) goto L_0x0089
            long r15 = r14.getDialogId()
            int r17 = (r15 > r10 ? 1 : (r15 == r10 ? 0 : -1))
            if (r17 != 0) goto L_0x0089
            int r15 = r14.getId()
            if (r15 > r12) goto L_0x0089
            boolean r15 = r0.isPersonalMessage(r14)
            if (r15 == 0) goto L_0x004e
            int r15 = r0.personalCount
            int r15 = r15 - r8
            r0.personalCount = r15
        L_0x004e:
            r2.add(r14)
            org.telegram.tgnet.TLRPC$Message r15 = r14.messageOwner
            org.telegram.tgnet.TLRPC$Peer r15 = r15.peer_id
            r17 = r9
            long r8 = r15.channel_id
            int r15 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r15 == 0) goto L_0x005f
            long r8 = -r8
            goto L_0x0060
        L_0x005f:
            r8 = r6
        L_0x0060:
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r15 = r0.pushMessagesDict
            java.lang.Object r15 = r15.get(r8)
            android.util.SparseArray r15 = (android.util.SparseArray) r15
            if (r15 == 0) goto L_0x007c
            int r5 = r14.getId()
            r15.remove(r5)
            int r5 = r15.size()
            if (r5 != 0) goto L_0x007c
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r5 = r0.pushMessagesDict
            r5.remove(r8)
        L_0x007c:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.delayedPushMessages
            r5.remove(r14)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.pushMessages
            r5.remove(r13)
            int r13 = r13 + -1
            goto L_0x008b
        L_0x0089:
            r17 = r9
        L_0x008b:
            r5 = 1
            int r13 = r13 + r5
            r9 = r17
            r8 = 1
            goto L_0x001f
        L_0x0091:
            r17 = r9
            int r9 = r17 + 1
            r8 = 1
            goto L_0x0010
        L_0x0098:
            int r1 = (r21 > r6 ? 1 : (r21 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x0121
            if (r3 != 0) goto L_0x00a0
            if (r4 == 0) goto L_0x0121
        L_0x00a0:
            r1 = 0
        L_0x00a1:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.pushMessages
            int r5 = r5.size()
            if (r1 >= r5) goto L_0x0121
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r0.pushMessages
            java.lang.Object r5 = r5.get(r1)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            long r8 = r5.getDialogId()
            int r10 = (r8 > r21 ? 1 : (r8 == r21 ? 0 : -1))
            if (r10 != 0) goto L_0x011e
            if (r4 == 0) goto L_0x00c3
            org.telegram.tgnet.TLRPC$Message r8 = r5.messageOwner
            int r8 = r8.date
            if (r8 > r4) goto L_0x00d7
        L_0x00c1:
            r8 = 1
            goto L_0x00d8
        L_0x00c3:
            if (r25 != 0) goto L_0x00ce
            int r8 = r5.getId()
            if (r8 <= r3) goto L_0x00c1
            if (r3 >= 0) goto L_0x00d7
            goto L_0x00c1
        L_0x00ce:
            int r8 = r5.getId()
            if (r8 == r3) goto L_0x00c1
            if (r3 >= 0) goto L_0x00d7
            goto L_0x00c1
        L_0x00d7:
            r8 = 0
        L_0x00d8:
            if (r8 == 0) goto L_0x011e
            boolean r8 = r0.isPersonalMessage(r5)
            if (r8 == 0) goto L_0x00e6
            int r8 = r0.personalCount
            r9 = 1
            int r8 = r8 - r9
            r0.personalCount = r8
        L_0x00e6:
            org.telegram.tgnet.TLRPC$Message r8 = r5.messageOwner
            org.telegram.tgnet.TLRPC$Peer r8 = r8.peer_id
            long r8 = r8.channel_id
            int r10 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r10 == 0) goto L_0x00f2
            long r8 = -r8
            goto L_0x00f3
        L_0x00f2:
            r8 = r6
        L_0x00f3:
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r10 = r0.pushMessagesDict
            java.lang.Object r10 = r10.get(r8)
            android.util.SparseArray r10 = (android.util.SparseArray) r10
            if (r10 == 0) goto L_0x010f
            int r11 = r5.getId()
            r10.remove(r11)
            int r10 = r10.size()
            if (r10 != 0) goto L_0x010f
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r10 = r0.pushMessagesDict
            r10.remove(r8)
        L_0x010f:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r0.pushMessages
            r8.remove(r1)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r8 = r0.delayedPushMessages
            r8.remove(r5)
            r2.add(r5)
            int r1 = r1 + -1
        L_0x011e:
            r5 = 1
            int r1 = r1 + r5
            goto L_0x00a1
        L_0x0121:
            boolean r1 = r20.isEmpty()
            if (r1 != 0) goto L_0x012f
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda30 r1 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda30
            r1.<init>(r0, r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x012f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processReadMessages$14(org.telegram.messenger.support.LongSparseIntArray, java.util.ArrayList, long, int, int, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processReadMessages$13(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0056, code lost:
        if (r0 == 2) goto L_0x0058;
     */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0070  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int addToPopupMessages(java.util.ArrayList<org.telegram.messenger.MessageObject> r4, org.telegram.messenger.MessageObject r5, long r6, boolean r8, android.content.SharedPreferences r9) {
        /*
            r3 = this;
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            r1 = 0
            if (r0 != 0) goto L_0x0058
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "custom_"
            r0.append(r2)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            boolean r0 = r9.getBoolean(r0, r1)
            if (r0 == 0) goto L_0x0034
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "popup_"
            r0.append(r2)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            int r0 = r9.getInt(r0, r1)
            goto L_0x0035
        L_0x0034:
            r0 = 0
        L_0x0035:
            if (r0 != 0) goto L_0x0050
            if (r8 == 0) goto L_0x0040
            java.lang.String r6 = "popupChannel"
            int r0 = r9.getInt(r6, r1)
            goto L_0x0059
        L_0x0040:
            boolean r6 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r6 == 0) goto L_0x0049
            java.lang.String r6 = "popupGroup"
            goto L_0x004b
        L_0x0049:
            java.lang.String r6 = "popupAll"
        L_0x004b:
            int r0 = r9.getInt(r6, r1)
            goto L_0x0059
        L_0x0050:
            r6 = 1
            if (r0 != r6) goto L_0x0055
            r0 = 3
            goto L_0x0059
        L_0x0055:
            r6 = 2
            if (r0 != r6) goto L_0x0059
        L_0x0058:
            r0 = 0
        L_0x0059:
            if (r0 == 0) goto L_0x006e
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            long r6 = r6.channel_id
            r8 = 0
            int r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r2 == 0) goto L_0x006e
            boolean r6 = r5.isSupergroup()
            if (r6 != 0) goto L_0x006e
            r0 = 0
        L_0x006e:
            if (r0 == 0) goto L_0x0073
            r4.add(r1, r5)
        L_0x0073:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.addToPopupMessages(java.util.ArrayList, org.telegram.messenger.MessageObject, long, boolean, android.content.SharedPreferences):int");
    }

    public void processEditedMessages(LongSparseArray<ArrayList<MessageObject>> longSparseArray) {
        if (longSparseArray.size() != 0) {
            new ArrayList(0);
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda24(this, longSparseArray));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processEditedMessages$15(LongSparseArray longSparseArray) {
        int size = longSparseArray.size();
        boolean z = false;
        for (int i = 0; i < size; i++) {
            if (this.pushDialogs.indexOfKey(longSparseArray.keyAt(i)) >= 0) {
                ArrayList arrayList = (ArrayList) longSparseArray.valueAt(i);
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    MessageObject messageObject = (MessageObject) arrayList.get(i2);
                    long j = messageObject.messageOwner.peer_id.channel_id;
                    long j2 = 0;
                    if (j != 0) {
                        j2 = -j;
                    }
                    SparseArray sparseArray = this.pushMessagesDict.get(j2);
                    if (sparseArray == null) {
                        break;
                    }
                    MessageObject messageObject2 = (MessageObject) sparseArray.get(messageObject.getId());
                    if (messageObject2 != null) {
                        sparseArray.put(messageObject.getId(), messageObject);
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
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda33(this, arrayList, new ArrayList(0), z2, z, countDownLatch));
        } else if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0048, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L_0x0052;
     */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00f6  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x013d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processNewMessages$18(java.util.ArrayList r30, java.util.ArrayList r31, boolean r32, boolean r33, java.util.concurrent.CountDownLatch r34) {
        /*
            r29 = this;
            r7 = r29
            r8 = r30
            androidx.collection.LongSparseArray r9 = new androidx.collection.LongSparseArray
            r9.<init>()
            org.telegram.messenger.AccountInstance r0 = r29.getAccountInstance()
            android.content.SharedPreferences r10 = r0.getNotificationsSettings()
            java.lang.String r0 = "PinnedMessages"
            r11 = 1
            boolean r12 = r10.getBoolean(r0, r11)
            r0 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
        L_0x001f:
            int r1 = r30.size()
            if (r14 >= r1) goto L_0x0235
            java.lang.Object r1 = r8.get(r14)
            r6 = r1
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            if (r1 == 0) goto L_0x0052
            boolean r1 = r6.isImportedForward()
            if (r1 != 0) goto L_0x004a
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r1.action
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL
            if (r4 != 0) goto L_0x004a
            boolean r1 = r1.silent
            if (r1 == 0) goto L_0x0052
            boolean r1 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r1 != 0) goto L_0x004a
            boolean r1 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r1 == 0) goto L_0x0052
        L_0x004a:
            r26 = r0
            r23 = r12
            r21 = r14
            goto L_0x0168
        L_0x0052:
            int r5 = r6.getId()
            boolean r1 = r6.isFcmMessage()
            r19 = 0
            if (r1 == 0) goto L_0x0065
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            r21 = r14
            long r13 = r1.random_id
            goto L_0x0069
        L_0x0065:
            r21 = r14
            r13 = r19
        L_0x0069:
            long r3 = r6.getDialogId()
            boolean r22 = r6.isFcmMessage()
            if (r22 == 0) goto L_0x0079
            boolean r1 = r6.localChannel
            r11 = r1
            r23 = r12
            goto L_0x00a0
        L_0x0079:
            boolean r1 = org.telegram.messenger.DialogObject.isChatDialog(r3)
            if (r1 == 0) goto L_0x009d
            org.telegram.messenger.MessagesController r1 = r29.getMessagesController()
            r23 = r12
            long r11 = -r3
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r11)
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r11 == 0) goto L_0x009a
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x009a
            r1 = 1
            goto L_0x009b
        L_0x009a:
            r1 = 0
        L_0x009b:
            r11 = r1
            goto L_0x00a0
        L_0x009d:
            r23 = r12
            r11 = 0
        L_0x00a0:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            r24 = r3
            long r2 = r1.channel_id
            int r1 = (r2 > r19 ? 1 : (r2 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x00af
            long r1 = -r2
            r3 = r1
            goto L_0x00b1
        L_0x00af:
            r3 = r19
        L_0x00b1:
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r1 = r7.pushMessagesDict
            java.lang.Object r1 = r1.get(r3)
            r2 = r1
            android.util.SparseArray r2 = (android.util.SparseArray) r2
            if (r2 == 0) goto L_0x00c3
            java.lang.Object r1 = r2.get(r5)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            goto L_0x00c4
        L_0x00c3:
            r1 = 0
        L_0x00c4:
            if (r1 != 0) goto L_0x00ec
            org.telegram.tgnet.TLRPC$Message r12 = r6.messageOwner
            r26 = r0
            r18 = r1
            long r0 = r12.random_id
            int r12 = (r0 > r19 ? 1 : (r0 == r19 ? 0 : -1))
            if (r12 == 0) goto L_0x00f0
            androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r12 = r7.fcmRandomMessagesDict
            java.lang.Object r0 = r12.get(r0)
            r1 = r0
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 == 0) goto L_0x00e9
            androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r7.fcmRandomMessagesDict
            org.telegram.tgnet.TLRPC$Message r12 = r6.messageOwner
            r27 = r13
            long r12 = r12.random_id
            r0.remove(r12)
            goto L_0x00f4
        L_0x00e9:
            r27 = r13
            goto L_0x00f4
        L_0x00ec:
            r26 = r0
            r18 = r1
        L_0x00f0:
            r27 = r13
            r1 = r18
        L_0x00f4:
            if (r1 == 0) goto L_0x013d
            boolean r0 = r1.isFcmMessage()
            if (r0 == 0) goto L_0x0168
            if (r2 != 0) goto L_0x0108
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r0 = r7.pushMessagesDict
            r0.put(r3, r2)
        L_0x0108:
            r2.put(r5, r6)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.pushMessages
            int r0 = r0.indexOf(r1)
            if (r0 < 0) goto L_0x0127
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r7.pushMessages
            r1.set(r0, r6)
            r0 = r29
            r1 = r31
            r2 = r6
            r3 = r24
            r5 = r11
            r12 = r6
            r6 = r10
            int r0 = r0.addToPopupMessages(r1, r2, r3, r5, r6)
            goto L_0x012a
        L_0x0127:
            r12 = r6
            r0 = r26
        L_0x012a:
            if (r32 == 0) goto L_0x0139
            boolean r1 = r12.localEdit
            if (r1 == 0) goto L_0x0137
            org.telegram.messenger.MessagesStorage r2 = r29.getMessagesStorage()
            r2.putPushMessage(r12)
        L_0x0137:
            r16 = r1
        L_0x0139:
            r22 = r9
            goto L_0x022a
        L_0x013d:
            r12 = r6
            if (r16 == 0) goto L_0x0141
            goto L_0x0168
        L_0x0141:
            if (r32 == 0) goto L_0x014a
            org.telegram.messenger.MessagesStorage r0 = r29.getMessagesStorage()
            r0.putPushMessage(r12)
        L_0x014a:
            long r0 = r7.openedDialogId
            int r6 = (r24 > r0 ? 1 : (r24 == r0 ? 0 : -1))
            if (r6 != 0) goto L_0x015a
            boolean r0 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r0 == 0) goto L_0x015a
            if (r32 != 0) goto L_0x0168
            r29.playInChatSound()
            goto L_0x0168
        L_0x015a:
            org.telegram.tgnet.TLRPC$Message r0 = r12.messageOwner
            boolean r1 = r0.mentioned
            if (r1 == 0) goto L_0x0174
            if (r23 != 0) goto L_0x016e
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r0 == 0) goto L_0x016e
        L_0x0168:
            r22 = r9
            r0 = r26
            goto L_0x022a
        L_0x016e:
            long r0 = r12.getFromChatId()
            r13 = r0
            goto L_0x0176
        L_0x0174:
            r13 = r24
        L_0x0176:
            boolean r0 = r7.isPersonalMessage(r12)
            if (r0 == 0) goto L_0x0182
            int r0 = r7.personalCount
            r1 = 1
            int r0 = r0 + r1
            r7.personalCount = r0
        L_0x0182:
            org.telegram.messenger.DialogObject.isChatDialog(r13)
            int r0 = r9.indexOfKey(r13)
            if (r0 < 0) goto L_0x0196
            java.lang.Object r0 = r9.valueAt(r0)
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r0 = r0.booleanValue()
            goto L_0x01b3
        L_0x0196:
            int r0 = r7.getNotifyOverride(r10, r13)
            r1 = -1
            if (r0 != r1) goto L_0x01a6
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r11)
            boolean r0 = r7.isGlobalNotificationsEnabled(r13, r0)
            goto L_0x01ac
        L_0x01a6:
            r1 = 2
            if (r0 == r1) goto L_0x01ab
            r0 = 1
            goto L_0x01ac
        L_0x01ab:
            r0 = 0
        L_0x01ac:
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r0)
            r9.put(r13, r1)
        L_0x01b3:
            if (r0 == 0) goto L_0x0225
            if (r32 != 0) goto L_0x01ca
            r0 = r29
            r1 = r31
            r15 = r2
            r2 = r12
            r22 = r9
            r8 = r3
            r3 = r13
            r6 = r5
            r5 = r11
            r11 = r6
            r6 = r10
            int r0 = r0.addToPopupMessages(r1, r2, r3, r5, r6)
            goto L_0x01d1
        L_0x01ca:
            r15 = r2
            r11 = r5
            r22 = r9
            r8 = r3
            r0 = r26
        L_0x01d1:
            if (r17 != 0) goto L_0x01d9
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner
            boolean r1 = r1.from_scheduled
            r17 = r1
        L_0x01d9:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r7.delayedPushMessages
            r1.add(r12)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r7.pushMessages
            r2 = 0
            r1.add(r2, r12)
            if (r11 == 0) goto L_0x01f8
            if (r15 != 0) goto L_0x01f3
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            androidx.collection.LongSparseArray<android.util.SparseArray<org.telegram.messenger.MessageObject>> r1 = r7.pushMessagesDict
            r1.put(r8, r2)
            goto L_0x01f4
        L_0x01f3:
            r2 = r15
        L_0x01f4:
            r2.put(r11, r12)
            goto L_0x0203
        L_0x01f8:
            int r1 = (r27 > r19 ? 1 : (r27 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x0203
            androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r7.fcmRandomMessagesDict
            r2 = r27
            r1.put(r2, r12)
        L_0x0203:
            int r1 = (r24 > r13 ? 1 : (r24 == r13 ? 0 : -1))
            if (r1 == 0) goto L_0x0229
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r7.pushDialogsOverrideMention
            r2 = r24
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r7.pushDialogsOverrideMention
            if (r1 != 0) goto L_0x0217
            r1 = 1
            goto L_0x021d
        L_0x0217:
            int r1 = r1.intValue()
            r5 = 1
            int r1 = r1 + r5
        L_0x021d:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r4.put(r2, r1)
            goto L_0x0229
        L_0x0225:
            r22 = r9
            r0 = r26
        L_0x0229:
            r15 = 1
        L_0x022a:
            int r14 = r21 + 1
            r8 = r30
            r9 = r22
            r12 = r23
            r11 = 1
            goto L_0x001f
        L_0x0235:
            r26 = r0
            if (r15 == 0) goto L_0x023d
            r0 = r33
            r7.notifyCheck = r0
        L_0x023d:
            boolean r0 = r31.isEmpty()
            if (r0 != 0) goto L_0x0259
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x0259
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0259
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda31 r0 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda31
            r2 = r31
            r13 = r26
            r0.<init>(r7, r2, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x0259:
            if (r32 != 0) goto L_0x025d
            if (r17 == 0) goto L_0x0311
        L_0x025d:
            if (r16 == 0) goto L_0x026b
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.delayedPushMessages
            r0.clear()
            boolean r0 = r7.notifyCheck
            r7.showOrUpdateNotification(r0)
            goto L_0x0311
        L_0x026b:
            if (r15 == 0) goto L_0x0311
            r0 = r30
            r2 = 0
            java.lang.Object r0 = r0.get(r2)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r2 = r0.getDialogId()
            boolean r4 = r0.isFcmMessage()
            if (r4 == 0) goto L_0x0287
            boolean r0 = r0.localChannel
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r0)
            goto L_0x0288
        L_0x0287:
            r4 = 0
        L_0x0288:
            int r0 = r7.total_unread_count
            int r5 = r7.getNotifyOverride(r10, r2)
            r1 = -1
            if (r5 != r1) goto L_0x0296
            boolean r1 = r7.isGlobalNotificationsEnabled(r2, r4)
            goto L_0x029c
        L_0x0296:
            r1 = 2
            if (r5 == r1) goto L_0x029b
            r1 = 1
            goto L_0x029c
        L_0x029b:
            r1 = 0
        L_0x029c:
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r7.pushDialogs
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x02ad
            int r5 = r4.intValue()
            r6 = 1
            int r5 = r5 + r6
            goto L_0x02af
        L_0x02ad:
            r6 = 1
            r5 = 1
        L_0x02af:
            boolean r8 = r7.notifyCheck
            if (r8 == 0) goto L_0x02cb
            if (r1 != 0) goto L_0x02cb
            androidx.collection.LongSparseArray<java.lang.Integer> r8 = r7.pushDialogsOverrideMention
            java.lang.Object r8 = r8.get(r2)
            java.lang.Integer r8 = (java.lang.Integer) r8
            if (r8 == 0) goto L_0x02cb
            int r9 = r8.intValue()
            if (r9 == 0) goto L_0x02cb
            int r5 = r8.intValue()
            r11 = 1
            goto L_0x02cc
        L_0x02cb:
            r11 = r1
        L_0x02cc:
            if (r11 == 0) goto L_0x02e7
            if (r4 == 0) goto L_0x02d9
            int r1 = r7.total_unread_count
            int r4 = r4.intValue()
            int r1 = r1 - r4
            r7.total_unread_count = r1
        L_0x02d9:
            int r1 = r7.total_unread_count
            int r1 = r1 + r5
            r7.total_unread_count = r1
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r7.pushDialogs
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r1.put(r2, r4)
        L_0x02e7:
            int r1 = r7.total_unread_count
            if (r0 == r1) goto L_0x0303
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.delayedPushMessages
            r0.clear()
            boolean r0 = r7.notifyCheck
            r7.showOrUpdateNotification(r0)
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r7.pushDialogs
            int r0 = r0.size()
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda20 r1 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda20
            r1.<init>(r7, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x0303:
            r0 = 0
            r7.notifyCheck = r0
            boolean r0 = r7.showBadgeNumber
            if (r0 == 0) goto L_0x0311
            int r0 = r29.getTotalAllUnreadCount()
            r7.setBadge(r0)
        L_0x0311:
            if (r34 == 0) goto L_0x0316
            r34.countDown()
        L_0x0316:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processNewMessages$18(java.util.ArrayList, java.util.ArrayList, boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processNewMessages$16(ArrayList arrayList, int i) {
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
    public /* synthetic */ void lambda$processNewMessages$17(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(LongSparseIntArray longSparseIntArray) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda34(this, longSparseIntArray, new ArrayList()));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDialogsUpdateRead$21(LongSparseIntArray longSparseIntArray, ArrayList arrayList) {
        boolean z;
        Integer num;
        TLRPC$Chat chat;
        LongSparseIntArray longSparseIntArray2 = longSparseIntArray;
        ArrayList arrayList2 = arrayList;
        int i = this.total_unread_count;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        int i2 = 0;
        while (true) {
            z = true;
            if (i2 >= longSparseIntArray.size()) {
                break;
            }
            long keyAt = longSparseIntArray2.keyAt(i2);
            Integer num2 = this.pushDialogs.get(keyAt);
            int i3 = longSparseIntArray2.get(keyAt);
            if (DialogObject.isChatDialog(keyAt) && ((chat = getMessagesController().getChat(Long.valueOf(-keyAt))) == null || chat.min || ChatObject.isNotInChat(chat))) {
                i3 = 0;
            }
            int notifyOverride = getNotifyOverride(notificationsSettings, keyAt);
            boolean isGlobalNotificationsEnabled = notifyOverride == -1 ? isGlobalNotificationsEnabled(keyAt) : notifyOverride != 2;
            if (this.notifyCheck && !isGlobalNotificationsEnabled && (num = this.pushDialogsOverrideMention.get(keyAt)) != null && num.intValue() != 0) {
                i3 = num.intValue();
                isGlobalNotificationsEnabled = true;
            }
            if (i3 == 0) {
                this.smartNotificationsDialogs.remove(keyAt);
            }
            if (i3 < 0) {
                if (num2 == null) {
                    i2++;
                } else {
                    i3 += num2.intValue();
                }
            }
            if ((isGlobalNotificationsEnabled || i3 == 0) && num2 != null) {
                this.total_unread_count -= num2.intValue();
            }
            if (i3 == 0) {
                this.pushDialogs.remove(keyAt);
                this.pushDialogsOverrideMention.remove(keyAt);
                int i4 = 0;
                while (i4 < this.pushMessages.size()) {
                    MessageObject messageObject = this.pushMessages.get(i4);
                    if (!messageObject.messageOwner.from_scheduled && messageObject.getDialogId() == keyAt) {
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        this.pushMessages.remove(i4);
                        i4--;
                        this.delayedPushMessages.remove(messageObject);
                        long j = messageObject.messageOwner.peer_id.channel_id;
                        long j2 = 0;
                        if (j != 0) {
                            j2 = -j;
                        }
                        SparseArray sparseArray = this.pushMessagesDict.get(j2);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(j2);
                            }
                        }
                        arrayList2.add(messageObject);
                    }
                    i4++;
                }
            } else if (isGlobalNotificationsEnabled) {
                this.total_unread_count += i3;
                this.pushDialogs.put(keyAt, Integer.valueOf(i3));
            }
            i2++;
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda26(this, arrayList2));
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
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda17(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDialogsUpdateRead$19(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDialogsUpdateRead$20(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processLoadedUnreadMessages(LongSparseArray<Integer> longSparseArray, ArrayList<TLRPC$Message> arrayList, ArrayList<MessageObject> arrayList2, ArrayList<TLRPC$User> arrayList3, ArrayList<TLRPC$Chat> arrayList4, ArrayList<TLRPC$EncryptedChat> arrayList5) {
        getMessagesController().putUsers(arrayList3, true);
        getMessagesController().putChats(arrayList4, true);
        getMessagesController().putEncryptedChats(arrayList5, true);
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda32(this, arrayList, longSparseArray, arrayList2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedUnreadMessages$23(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2) {
        LongSparseArray longSparseArray2;
        SharedPreferences sharedPreferences;
        boolean z;
        boolean z2;
        int i;
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader;
        long j;
        SparseArray sparseArray;
        boolean z3;
        SparseArray sparseArray2;
        ArrayList arrayList3 = arrayList;
        LongSparseArray longSparseArray3 = longSparseArray;
        ArrayList arrayList4 = arrayList2;
        this.pushDialogs.clear();
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        boolean z4 = false;
        this.total_unread_count = 0;
        this.personalCount = 0;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        LongSparseArray longSparseArray4 = new LongSparseArray();
        long j2 = 0;
        int i2 = 1;
        if (arrayList3 != null) {
            int i3 = 0;
            while (i3 < arrayList.size()) {
                TLRPC$Message tLRPC$Message = (TLRPC$Message) arrayList3.get(i3);
                if (tLRPC$Message != null && ((tLRPC$MessageFwdHeader = tLRPC$Message.fwd_from) == null || !tLRPC$MessageFwdHeader.imported)) {
                    TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
                    if (!(tLRPC$MessageAction instanceof TLRPC$TL_messageActionSetMessagesTTL) && (!tLRPC$Message.silent || (!(tLRPC$MessageAction instanceof TLRPC$TL_messageActionContactSignUp) && !(tLRPC$MessageAction instanceof TLRPC$TL_messageActionUserJoined)))) {
                        long j3 = tLRPC$Message.peer_id.channel_id;
                        long j4 = j3 != j2 ? -j3 : j2;
                        SparseArray sparseArray3 = this.pushMessagesDict.get(j4);
                        if (sparseArray3 == null || sparseArray3.indexOfKey(tLRPC$Message.id) < 0) {
                            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message, z4, z4);
                            if (isPersonalMessage(messageObject)) {
                                this.personalCount += i2;
                            }
                            i = i3;
                            long dialogId = messageObject.getDialogId();
                            if (messageObject.messageOwner.mentioned) {
                                sparseArray = sparseArray3;
                                j = messageObject.getFromChatId();
                            } else {
                                sparseArray = sparseArray3;
                                j = dialogId;
                            }
                            int indexOfKey = longSparseArray4.indexOfKey(j);
                            if (indexOfKey >= 0) {
                                z3 = ((Boolean) longSparseArray4.valueAt(indexOfKey)).booleanValue();
                            } else {
                                int notifyOverride = getNotifyOverride(notificationsSettings, j);
                                z3 = notifyOverride == -1 ? isGlobalNotificationsEnabled(j) : notifyOverride != 2;
                                longSparseArray4.put(j, Boolean.valueOf(z3));
                            }
                            if (z3 && (j != this.openedDialogId || !ApplicationLoader.isScreenOn)) {
                                if (sparseArray == null) {
                                    sparseArray2 = new SparseArray();
                                    this.pushMessagesDict.put(j4, sparseArray2);
                                } else {
                                    sparseArray2 = sparseArray;
                                }
                                sparseArray2.put(tLRPC$Message.id, messageObject);
                                this.pushMessages.add(0, messageObject);
                                if (dialogId != j) {
                                    Integer num = this.pushDialogsOverrideMention.get(dialogId);
                                    this.pushDialogsOverrideMention.put(dialogId, Integer.valueOf(num == null ? 1 : num.intValue() + 1));
                                }
                            }
                            i3 = i + 1;
                            arrayList3 = arrayList;
                            ArrayList arrayList5 = arrayList2;
                            z4 = false;
                            j2 = 0;
                            i2 = 1;
                        }
                    }
                }
                i = i3;
                i3 = i + 1;
                arrayList3 = arrayList;
                ArrayList arrayList52 = arrayList2;
                z4 = false;
                j2 = 0;
                i2 = 1;
            }
        }
        for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
            long keyAt = longSparseArray3.keyAt(i4);
            int indexOfKey2 = longSparseArray4.indexOfKey(keyAt);
            if (indexOfKey2 >= 0) {
                z2 = ((Boolean) longSparseArray4.valueAt(indexOfKey2)).booleanValue();
            } else {
                int notifyOverride2 = getNotifyOverride(notificationsSettings, keyAt);
                z2 = notifyOverride2 == -1 ? isGlobalNotificationsEnabled(keyAt) : notifyOverride2 != 2;
                longSparseArray4.put(keyAt, Boolean.valueOf(z2));
            }
            if (z2) {
                int intValue = ((Integer) longSparseArray3.valueAt(i4)).intValue();
                this.pushDialogs.put(keyAt, Integer.valueOf(intValue));
                this.total_unread_count += intValue;
            }
        }
        ArrayList arrayList6 = arrayList2;
        if (arrayList6 != null) {
            int i5 = 0;
            while (i5 < arrayList2.size()) {
                MessageObject messageObject2 = (MessageObject) arrayList6.get(i5);
                int id = messageObject2.getId();
                if (this.pushMessagesDict.indexOfKey((long) id) >= 0) {
                    sharedPreferences = notificationsSettings;
                    longSparseArray2 = longSparseArray4;
                } else {
                    if (isPersonalMessage(messageObject2)) {
                        this.personalCount++;
                    }
                    long dialogId2 = messageObject2.getDialogId();
                    TLRPC$Message tLRPC$Message2 = messageObject2.messageOwner;
                    long j5 = tLRPC$Message2.random_id;
                    long fromChatId = tLRPC$Message2.mentioned ? messageObject2.getFromChatId() : dialogId2;
                    int indexOfKey3 = longSparseArray4.indexOfKey(fromChatId);
                    if (indexOfKey3 >= 0) {
                        z = ((Boolean) longSparseArray4.valueAt(indexOfKey3)).booleanValue();
                    } else {
                        int notifyOverride3 = getNotifyOverride(notificationsSettings, fromChatId);
                        z = notifyOverride3 == -1 ? isGlobalNotificationsEnabled(fromChatId) : notifyOverride3 != 2;
                        longSparseArray4.put(fromChatId, Boolean.valueOf(z));
                    }
                    sharedPreferences = notificationsSettings;
                    if (z) {
                        longSparseArray2 = longSparseArray4;
                        if (fromChatId != this.openedDialogId || !ApplicationLoader.isScreenOn) {
                            if (id != 0) {
                                long j6 = messageObject2.messageOwner.peer_id.channel_id;
                                long j7 = j6 != 0 ? -j6 : 0;
                                SparseArray sparseArray4 = this.pushMessagesDict.get(j7);
                                if (sparseArray4 == null) {
                                    sparseArray4 = new SparseArray();
                                    this.pushMessagesDict.put(j7, sparseArray4);
                                }
                                sparseArray4.put(id, messageObject2);
                            } else if (j5 != 0) {
                                this.fcmRandomMessagesDict.put(j5, messageObject2);
                            }
                            this.pushMessages.add(0, messageObject2);
                            if (dialogId2 != fromChatId) {
                                Integer num2 = this.pushDialogsOverrideMention.get(dialogId2);
                                this.pushDialogsOverrideMention.put(dialogId2, Integer.valueOf(num2 == null ? 1 : num2.intValue() + 1));
                            }
                            Integer num3 = this.pushDialogs.get(fromChatId);
                            int intValue2 = num3 != null ? num3.intValue() + 1 : 1;
                            if (num3 != null) {
                                this.total_unread_count -= num3.intValue();
                            }
                            this.total_unread_count += intValue2;
                            this.pushDialogs.put(fromChatId, Integer.valueOf(intValue2));
                            i5++;
                            notificationsSettings = sharedPreferences;
                            longSparseArray4 = longSparseArray2;
                        }
                    } else {
                        longSparseArray2 = longSparseArray4;
                    }
                }
                i5++;
                notificationsSettings = sharedPreferences;
                longSparseArray4 = longSparseArray2;
            }
        }
        AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda16(this, this.pushDialogs.size()));
        showOrUpdateNotification(SystemClock.elapsedRealtime() / 1000 < 60);
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedUnreadMessages$22(int i) {
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
                                    if (!DialogObject.isChatDialog(tLRPC$Dialog.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog.id)))) {
                                        int i5 = tLRPC$Dialog.unread_count;
                                        if (i5 != 0) {
                                            i2 += i5;
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
                            for (int i6 = 0; i6 < size2; i6++) {
                                TLRPC$Dialog tLRPC$Dialog2 = MessagesController.getInstance(i3).allDialogs.get(i6);
                                if (!DialogObject.isChatDialog(tLRPC$Dialog2.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog2.id)))) {
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
    public /* synthetic */ void lambda$updateBadge$24() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda13(this));
    }

    private void setBadge(int i) {
        if (this.lastBadgeCount != i) {
            this.lastBadgeCount = i;
            NotificationBadge.applyCount(i);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0224, code lost:
        if (r11.getBoolean("EnablePreviewAll", true) == false) goto L_0x0228;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0234, code lost:
        if (r11.getBoolean("EnablePreviewGroup", r7) != false) goto L_0x0240;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x023e, code lost:
        if (r11.getBoolean("EnablePreviewChannel", r7) != false) goto L_0x0240;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0240, code lost:
        r2 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x024c, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0e56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x024e, code lost:
        r23[0] = null;
        r3 = r2.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x0255, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) == false) goto L_0x025e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x025d, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x0260, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L_0x0e47;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x0264, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L_0x0268;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x026a, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L_0x027b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x027a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x027e, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L_0x02dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0280, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", NUM, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(((long) r0.messageOwner.date) * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(((long) r0.messageOwner.date) * 1000));
        r0 = r0.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02dc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", NUM, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02df, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L_0x0e40;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02e3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L_0x02e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02e9, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L_0x0303;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02ed, code lost:
        if (r3.video == false) goto L_0x02f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x02f8, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageVideoIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0302, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x0305, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x041b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x0307, code lost:
        r4 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x030d, code lost:
        if (r4 != 0) goto L_0x0328;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0316, code lost:
        if (r3.users.size() != 1) goto L_0x0328;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x0318, code lost:
        r4 = r0.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x032c, code lost:
        if (r4 == 0) goto L_0x03c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0336, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x0350;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x033a, code lost:
        if (r6.megagroup != false) goto L_0x0350;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x034f, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0352, code lost:
        if (r4 != r16) goto L_0x0368;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x0367, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0368, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0374, code lost:
        if (r0 != null) goto L_0x0378;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x0376, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x037c, code lost:
        if (r8 != r0.id) goto L_0x03aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0380, code lost:
        if (r6.megagroup == false) goto L_0x0396;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x0395, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x03a9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x03c3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03c4, code lost:
        r2 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03d4, code lost:
        if (r3 >= r0.messageOwner.action.users.size()) goto L_0x0401;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x03d6, code lost:
        r4 = getMessagesController().getUser(r0.messageOwner.action.users.get(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x03ea, code lost:
        if (r4 == null) goto L_0x03fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x03ec, code lost:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03f4, code lost:
        if (r2.length() == 0) goto L_0x03fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x03f6, code lost:
        r2.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x03fb, code lost:
        r2.append(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x03fe, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x041a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r1, r6.title, r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x041e, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L_0x0433;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0432, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x0435, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) == false) goto L_0x043e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x043d, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0440, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L_0x0502;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0442, code lost:
        r4 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0448, code lost:
        if (r4 != 0) goto L_0x0463;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x0451, code lost:
        if (r3.users.size() != 1) goto L_0x0463;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x0453, code lost:
        r4 = r0.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x0467, code lost:
        if (r4 == 0) goto L_0x04ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x046b, code lost:
        if (r4 != r16) goto L_0x0481;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0480, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x0481, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x048d, code lost:
        if (r0 != null) goto L_0x0491;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x048f, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04aa, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04ab, code lost:
        r2 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04bb, code lost:
        if (r3 >= r0.messageOwner.action.users.size()) goto L_0x04e8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x04bd, code lost:
        r4 = getMessagesController().getUser(r0.messageOwner.action.users.get(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04d1, code lost:
        if (r4 == null) goto L_0x04e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04d3, code lost:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04db, code lost:
        if (r2.length() == 0) goto L_0x04e2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04dd, code lost:
        r2.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x04e2, code lost:
        r2.append(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x04e5, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0501, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r1, r6.title, r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0505, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x051a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x0519, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x051d, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x0531;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0530, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r1, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0533, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0ddc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0537, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x053b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x053d, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x05a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x053f, code lost:
        r2 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0543, code lost:
        if (r2 != r16) goto L_0x0559;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0558, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x055d, code lost:
        if (r2 != r8) goto L_0x0571;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0570, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0571, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0583, code lost:
        if (r0 != null) goto L_0x0587;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x0585, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x05a0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x05a3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x05ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x05ab, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x05ae, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x05b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x05b6, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x05b9, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x05cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x05cb, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x05cf, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x05e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05e0, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x05e3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x05ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x05eb, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x05ee, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0dda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x05f4, code lost:
        if (r6 == null) goto L_0x08d2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x05fa, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r6) == false) goto L_0x0600;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x05fe, code lost:
        if (r6.megagroup == false) goto L_0x08d2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x0600, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x0602, code lost:
        if (r0 != null) goto L_0x0618;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x0617, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x061e, code lost:
        if (r0.isMusic() == false) goto L_0x0632;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x0631, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x063b, code lost:
        if (r0.isVideo() == false) goto L_0x0685;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x0641, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0671;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x064b, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0671;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x0670, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "📹 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x0684, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x0689, code lost:
        if (r0.isGif() == false) goto L_0x06d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x068f, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x06bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x0699, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x06bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x06be, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "🎬 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x06d2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x06d9, code lost:
        if (r0.isVoice() == false) goto L_0x06ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x06ec, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x06f1, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0705;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x0704, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x0709, code lost:
        if (r0.isSticker() != false) goto L_0x08a3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x070f, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0713;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0713, code lost:
        r4 = r0.messageOwner;
        r7 = r4.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x0719, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0761;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x071f, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x074d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x0727, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x074d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x074c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "📎 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x0760, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x0763, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x088f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x0767, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x076b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x076d, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0783;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x0782, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x0786, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x07a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x0788, code lost:
        r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x07a6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r1, r6.title, org.telegram.messenger.ContactsController.formatName(r7.first_name, r7.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x07a9, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x07e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x07ab, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x07b1, code lost:
        if (r0.quiz == false) goto L_0x07cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x07ca, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r1, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x07e2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r1, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x07e5, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x082d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x07eb, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0819;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x07f3, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0819;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0818, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "🖼 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x082c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0831, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0845;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0844, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0845, code lost:
        r4 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0847, code lost:
        if (r4 == null) goto L_0x087b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x084d, code lost:
        if (r4.length() <= 0) goto L_0x087b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a5, code lost:
        if (r11.getBoolean("EnablePreviewGroup", true) != false) goto L_0x00a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x084f, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0855, code lost:
        if (r0.length() <= 20) goto L_0x086a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0857, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x087a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, r0, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x088e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x08a2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x08a3, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x08a8, code lost:
        if (r0 == null) goto L_0x08bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x08be, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r1, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x08d1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x08d3, code lost:
        if (r6 == null) goto L_0x0b6e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x08d5, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x08d7, code lost:
        if (r0 != null) goto L_0x08e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x08e8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x08ed, code lost:
        if (r0.isMusic() == false) goto L_0x08ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x08fe, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x0908, code lost:
        if (r0.isVideo() == false) goto L_0x094d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:438:0x090e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x093c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x0918, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x093c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x093b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x094c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x0951, code lost:
        if (r0.isGif() == false) goto L_0x0996;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0957, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0985;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0961, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0985;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0984, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x0995, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x099b, code lost:
        if (r0.isVoice() == false) goto L_0x09ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x09ac, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00b3, code lost:
        if (r11.getBoolean("EnablePreviewChannel", r3) == false) goto L_0x00b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:460:0x09b1, code lost:
        if (r0.isRoundVideo() == false) goto L_0x09c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x09c2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x09c7, code lost:
        if (r0.isSticker() != false) goto L_0x0b43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x09cd, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x09d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x09d1, code lost:
        r1 = r0.messageOwner;
        r4 = r1.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x09d7, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0a1a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x09dd, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0a09;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x09e5, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L_0x0a09;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0a08, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0a19, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0a1c, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0b32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x0a20, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0a24;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x0a26, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0a39;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x0a38, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x0a3b, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0a5b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0a3d, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0a5a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r6.title, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0a5d, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0a93;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0a5f, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0a65, code lost:
        if (r0.quiz == false) goto L_0x0a7d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0a7c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x0a92, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0a95, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0ad8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x0a9b, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0ac7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0aa3, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L_0x0ac7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x0ac6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0ad7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0adb, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0aed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x0aec, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0aed, code lost:
        r1 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0aef, code lost:
        if (r1 == null) goto L_0x0b21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0af5, code lost:
        if (r1.length() <= 0) goto L_0x0b21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0af7, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0afd, code lost:
        if (r0.length() <= 20) goto L_0x0b12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x0aff, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0b20, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0b31, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0b42, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0b43, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0b47, code lost:
        if (r0 == null) goto L_0x0b5d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0b5c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0b6d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0b6e, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0b70, code lost:
        if (r0 != null) goto L_0x0b80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0b7f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0b84, code lost:
        if (r0.isMusic() == false) goto L_0x0b94;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0b93, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0b9d, code lost:
        if (r0.isVideo() == false) goto L_0x0bde;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0ba3, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0bcf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0bad, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0bcf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0bce, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r1, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0bdd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0be2, code lost:
        if (r0.isGif() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0be8, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0bf2, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r1, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0CLASSNAME, code lost:
        if (r0.isVoice() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0c3c, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0c4c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0c4b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0CLASSNAME, code lost:
        if (r0.isSticker() != false) goto L_0x0db4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0CLASSNAME, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0c5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0c5a, code lost:
        r4 = r0.messageOwner;
        r6 = r4.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0CLASSNAME, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0c9f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0CLASSNAME, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0c6e, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0c8f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r1, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0c9e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0ca1, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0da5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0ca5, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0ca9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0cab, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0cbc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0cbb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0cbe, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0cdc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0cc0, code lost:
        r6 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0cdb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r1, org.telegram.messenger.ContactsController.formatName(r6.first_name, r6.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0cde, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0d10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0ce0, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r6).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0ce6, code lost:
        if (r0.quiz == false) goto L_0x0cfc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0cfb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r1, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0d0f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r1, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0d12, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0d51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0d18, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0d42;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0d20, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0d42;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0d41, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r1, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0d50, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0d54, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0d64;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0d63, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0d64, code lost:
        r4 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0d66, code lost:
        if (r4 == null) goto L_0x0d96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0d6c, code lost:
        if (r4.length() <= 0) goto L_0x0d96;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0d6e, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x0d74, code lost:
        if (r0.length() <= 20) goto L_0x0d89;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0d76, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0d95, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0da4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0db3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0db4, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x0db9, code lost:
        if (r0 == null) goto L_0x0dcc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0dcb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0dd9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0dda, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0de4, code lost:
        if (r2.peer_id.channel_id == 0) goto L_0x0e12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0de8, code lost:
        if (r6.megagroup != false) goto L_0x0e12;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0dee, code lost:
        if (r22.isVideoAvatar() == false) goto L_0x0e01;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0e00, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0e11, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0e16, code lost:
        if (r22.isVideoAvatar() == false) goto L_0x0e2c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0e2b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0e3f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0e46, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0e55, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:655:0x0e5a, code lost:
        if (r22.isMediaEmpty() == false) goto L_0x0e74;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x0e64, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0e6b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:659:0x0e6a, code lost:
        return r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:661:0x0e73, code lost:
        return org.telegram.messenger.LocaleController.getString(r13, NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0e74, code lost:
        r1 = r13;
        r2 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x0e7b, code lost:
        if ((r2.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0ebb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x0e81, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0e9f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x0e89, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x0e9f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x0e9e, code lost:
        return "🖼 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x0ea5, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0eb1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x0eb0, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x0eba, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:677:0x0ebf, code lost:
        if (r22.isVideo() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:679:0x0ec5, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0ee5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0ecf, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0ee5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0ee4, code lost:
        return "📹 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x0eeb, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0ef7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x0ef6, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x0var_, code lost:
        if (r22.isGame() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:693:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:695:0x0var_, code lost:
        if (r22.isVoice() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:697:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x0var_, code lost:
        if (r22.isRoundVideo() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x0var_, code lost:
        if (r22.isMusic() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:705:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0var_, code lost:
        r2 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x0var_, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:711:0x0var_, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:713:0x0f5d, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2).poll.quiz == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:715:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:717:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:719:0x0var_, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x1043;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:0x0var_, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0f7d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:0x0f7f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0f8b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x0f8a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0f8d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x102c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:729:0x0var_, code lost:
        if (r22.isSticker() != false) goto L_0x0ffe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:731:0x0var_, code lost:
        if (r22.isAnimatedSticker() == false) goto L_0x0f9c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0fa0, code lost:
        if (r22.isGif() == false) goto L_0x0fd0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:735:0x0fa6, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0fc6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:737:0x0fb0, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0fc6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:739:0x0fc5, code lost:
        return "🎬 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:741:0x0fcf, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:743:0x0fd4, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0ff4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:745:0x0fde, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0ff4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:747:0x0ff3, code lost:
        return "📎 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:749:0x0ffd, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x0ffe, code lost:
        r0 = r22.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:751:0x1002, code lost:
        if (r0 == null) goto L_0x1022;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:753:0x1021, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:755:0x102b, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:757:0x1032, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x103b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:759:0x103a, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:761:0x1042, code lost:
        return org.telegram.messenger.LocaleController.getString(r1, NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:763:0x104c, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r22, java.lang.String[] r23, boolean[] r24) {
        /*
            r21 = this;
            r0 = r22
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x105a
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x105a
        L_0x000e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r2 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r4 = r1.chat_id
            r6 = 0
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x001d
            goto L_0x001f
        L_0x001d:
            long r4 = r1.channel_id
        L_0x001f:
            long r8 = r1.user_id
            r1 = 1
            r10 = 0
            if (r24 == 0) goto L_0x0027
            r24[r10] = r1
        L_0x0027:
            org.telegram.messenger.AccountInstance r11 = r21.getAccountInstance()
            android.content.SharedPreferences r11 = r11.getNotificationsSettings()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "content_preview_"
            r12.append(r13)
            r12.append(r2)
            java.lang.String r12 = r12.toString()
            boolean r12 = r11.getBoolean(r12, r1)
            boolean r13 = r22.isFcmMessage()
            java.lang.String r15 = "Message"
            r14 = 27
            r1 = 2
            if (r13 == 0) goto L_0x00f4
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x0076
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 == 0) goto L_0x0076
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 <= r14) goto L_0x005f
            java.lang.String r1 = r0.localName
            r23[r10] = r1
        L_0x005f:
            if (r12 == 0) goto L_0x006a
            java.lang.String r1 = "EnablePreviewAll"
            r2 = 1
            boolean r1 = r11.getBoolean(r1, r2)
            if (r1 != 0) goto L_0x00ef
        L_0x006a:
            if (r24 == 0) goto L_0x006e
            r24[r10] = r10
        L_0x006e:
            r0 = 2131626225(0x7f0e08f1, float:1.887968E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r15, r0)
            return r0
        L_0x0076:
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 == 0) goto L_0x00ef
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            long r2 = r2.channel_id
            int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r4 == 0) goto L_0x0094
            boolean r2 = r22.isSupergroup()
            if (r2 == 0) goto L_0x008b
            goto L_0x0094
        L_0x008b:
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 <= r14) goto L_0x0098
            java.lang.String r2 = r0.localName
            r23[r10] = r2
            goto L_0x0098
        L_0x0094:
            java.lang.String r2 = r0.localUserName
            r23[r10] = r2
        L_0x0098:
            if (r12 == 0) goto L_0x00b5
            boolean r2 = r0.localChannel
            if (r2 != 0) goto L_0x00a8
            java.lang.String r2 = "EnablePreviewGroup"
            r3 = 1
            boolean r2 = r11.getBoolean(r2, r3)
            if (r2 == 0) goto L_0x00b5
            goto L_0x00a9
        L_0x00a8:
            r3 = 1
        L_0x00a9:
            boolean r2 = r0.localChannel
            if (r2 == 0) goto L_0x00ef
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r2 = r11.getBoolean(r2, r3)
            if (r2 != 0) goto L_0x00ef
        L_0x00b5:
            if (r24 == 0) goto L_0x00b9
            r24[r10] = r10
        L_0x00b9:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            long r2 = r2.channel_id
            int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r4 == 0) goto L_0x00da
            boolean r2 = r22.isSupergroup()
            if (r2 != 0) goto L_0x00da
            r1 = 2131624764(0x7f0e033c, float:1.8876717E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r0 = r0.localName
            r2[r10] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00da:
            r2 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r3 = r0.localUserName
            r1[r10] = r3
            java.lang.String r0 = r0.localName
            r3 = 1
            r1[r3] = r0
            java.lang.String r0 = "NotificationMessageGroupNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r1)
            return r0
        L_0x00ef:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x00f4:
            org.telegram.messenger.UserConfig r13 = r21.getUserConfig()
            long r16 = r13.getClientUserId()
            int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r13 != 0) goto L_0x010a
            long r8 = r22.getFromChatId()
            int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r13 != 0) goto L_0x0112
            long r8 = -r4
            goto L_0x0112
        L_0x010a:
            int r13 = (r8 > r16 ? 1 : (r8 == r16 ? 0 : -1))
            if (r13 != 0) goto L_0x0112
            long r8 = r22.getFromChatId()
        L_0x0112:
            int r13 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r13 != 0) goto L_0x0121
            int r13 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r13 == 0) goto L_0x011c
            long r2 = -r4
            goto L_0x0121
        L_0x011c:
            int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r13 == 0) goto L_0x0121
            r2 = r8
        L_0x0121:
            boolean r13 = org.telegram.messenger.UserObject.isReplyUser((long) r2)
            if (r13 == 0) goto L_0x0135
            org.telegram.tgnet.TLRPC$Message r13 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r13 = r13.fwd_from
            if (r13 == 0) goto L_0x0135
            org.telegram.tgnet.TLRPC$Peer r13 = r13.from_id
            if (r13 == 0) goto L_0x0135
            long r8 = org.telegram.messenger.MessageObject.getPeerId(r13)
        L_0x0135:
            int r18 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r18 <= 0) goto L_0x0160
            org.telegram.messenger.MessagesController r1 = r21.getMessagesController()
            java.lang.Long r13 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r13)
            if (r1 == 0) goto L_0x015d
            java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r1)
            int r13 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r13 == 0) goto L_0x0152
            r23[r10] = r1
            goto L_0x015e
        L_0x0152:
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 <= r14) goto L_0x0159
            r23[r10] = r1
            goto L_0x015e
        L_0x0159:
            r13 = 0
            r23[r10] = r13
            goto L_0x015e
        L_0x015d:
            r1 = 0
        L_0x015e:
            r13 = r15
            goto L_0x0176
        L_0x0160:
            org.telegram.messenger.MessagesController r1 = r21.getMessagesController()
            r13 = r15
            long r14 = -r8
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r14)
            if (r1 == 0) goto L_0x0175
            java.lang.String r1 = r1.title
            r23[r10] = r1
            goto L_0x0176
        L_0x0175:
            r1 = 0
        L_0x0176:
            if (r1 == 0) goto L_0x01c1
            int r14 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r14 <= 0) goto L_0x01c1
            boolean r14 = org.telegram.messenger.UserObject.isReplyUser((long) r2)
            if (r14 == 0) goto L_0x01c1
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            if (r14 == 0) goto L_0x01c1
            org.telegram.tgnet.TLRPC$Peer r14 = r14.saved_from_peer
            if (r14 == 0) goto L_0x01c1
            long r14 = org.telegram.messenger.MessageObject.getPeerId(r14)
            boolean r20 = org.telegram.messenger.DialogObject.isChatDialog(r14)
            if (r20 == 0) goto L_0x01c1
            org.telegram.messenger.MessagesController r6 = r21.getMessagesController()
            long r14 = -r14
            java.lang.Long r7 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            if (r6 == 0) goto L_0x01c1
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r1)
            java.lang.String r1 = " @ "
            r7.append(r1)
            java.lang.String r1 = r6.title
            r7.append(r1)
            java.lang.String r1 = r7.toString()
            r6 = r23[r10]
            if (r6 == 0) goto L_0x01c1
            r23[r10] = r1
        L_0x01c1:
            if (r1 != 0) goto L_0x01c6
            r19 = 0
            return r19
        L_0x01c6:
            r6 = 0
            r19 = 0
            int r14 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r14 == 0) goto L_0x01f0
            org.telegram.messenger.MessagesController r6 = r21.getMessagesController()
            java.lang.Long r7 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            if (r6 != 0) goto L_0x01dd
            return r19
        L_0x01dd:
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r7 == 0) goto L_0x01f2
            boolean r7 = r6.megagroup
            if (r7 != 0) goto L_0x01f2
            int r7 = android.os.Build.VERSION.SDK_INT
            r14 = 27
            if (r7 > r14) goto L_0x01f2
            r23[r10] = r19
            goto L_0x01f2
        L_0x01f0:
            r6 = r19
        L_0x01f2:
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
            if (r2 == 0) goto L_0x0204
            r23[r10] = r19
            r0 = 2131626531(0x7f0e0a23, float:1.88803E38)
            java.lang.String r1 = "NotificationHiddenMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0204:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r2 == 0) goto L_0x0210
            boolean r2 = r6.megagroup
            if (r2 != 0) goto L_0x0210
            r2 = 1
            goto L_0x0211
        L_0x0210:
            r2 = 0
        L_0x0211:
            if (r12 == 0) goto L_0x104d
            r14 = 0
            int r3 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r3 != 0) goto L_0x0227
            int r3 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x0227
            java.lang.String r3 = "EnablePreviewAll"
            r7 = 1
            boolean r3 = r11.getBoolean(r3, r7)
            if (r3 != 0) goto L_0x0240
            goto L_0x0228
        L_0x0227:
            r7 = 1
        L_0x0228:
            int r3 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r3 == 0) goto L_0x104d
            if (r2 != 0) goto L_0x0236
            java.lang.String r3 = "EnablePreviewGroup"
            boolean r3 = r11.getBoolean(r3, r7)
            if (r3 != 0) goto L_0x0240
        L_0x0236:
            if (r2 == 0) goto L_0x104d
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r2 = r11.getBoolean(r2, r7)
            if (r2 == 0) goto L_0x104d
        L_0x0240:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r4 = "🎬 "
            java.lang.String r5 = "📎 "
            java.lang.String r7 = "📹 "
            java.lang.String r11 = "🖼 "
            if (r3 == 0) goto L_0x0e56
            r3 = 0
            r23[r10] = r3
            org.telegram.tgnet.TLRPC$MessageAction r3 = r2.action
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r13 == 0) goto L_0x025e
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x025e:
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r13 != 0) goto L_0x0e47
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r13 == 0) goto L_0x0268
            goto L_0x0e47
        L_0x0268:
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r13 == 0) goto L_0x027b
            r0 = 2131626512(0x7f0e0a10, float:1.8880262E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x027b:
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            r14 = 3
            if (r13 == 0) goto L_0x02dd
            r1 = 2131628642(0x7f0e1262, float:1.8884582E38)
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
            r3[r10] = r2
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
            r2 = 2131626581(0x7f0e0a55, float:1.8880402E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r5 = r21.getUserConfig()
            org.telegram.tgnet.TLRPC$User r5 = r5.getCurrentUser()
            java.lang.String r5 = r5.first_name
            r3[r10] = r5
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
        L_0x02dd:
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r13 != 0) goto L_0x0e40
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r13 == 0) goto L_0x02e7
            goto L_0x0e40
        L_0x02e7:
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r13 == 0) goto L_0x0303
            boolean r0 = r3.video
            if (r0 == 0) goto L_0x02f9
            r0 = 2131624638(0x7f0e02be, float:1.8876461E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02f9:
            r0 = 2131624632(0x7f0e02b8, float:1.887645E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0303:
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r13 == 0) goto L_0x041b
            long r4 = r3.user_id
            r11 = 0
            int r2 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x0328
            java.util.ArrayList<java.lang.Long> r2 = r3.users
            int r2 = r2.size()
            r3 = 1
            if (r2 != r3) goto L_0x0328
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Long> r2 = r2.users
            java.lang.Object r2 = r2.get(r10)
            java.lang.Long r2 = (java.lang.Long) r2
            long r4 = r2.longValue()
        L_0x0328:
            r2 = 0
            int r7 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x03c4
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r11 = r0.channel_id
            int r0 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
            if (r0 == 0) goto L_0x0350
            boolean r0 = r6.megagroup
            if (r0 != 0) goto L_0x0350
            r0 = 2131624713(0x7f0e0309, float:1.8876613E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0350:
            int r0 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r0 != 0) goto L_0x0368
            r0 = 2131626533(0x7f0e0a25, float:1.8880305E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0368:
            org.telegram.messenger.MessagesController r0 = r21.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x0378
            r2 = 0
            return r2
        L_0x0378:
            long r2 = r0.id
            int r4 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x03aa
            boolean r0 = r6.megagroup
            if (r0 == 0) goto L_0x0396
            r0 = 2131626518(0x7f0e0a16, float:1.8880274E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0396:
            r2 = 2
            r3 = 1
            r0 = 2131626517(0x7f0e0a15, float:1.8880272E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x03aa:
            r3 = 1
            r2 = 2131626516(0x7f0e0a14, float:1.888027E38)
            java.lang.Object[] r4 = new java.lang.Object[r14]
            r4[r10] = r1
            java.lang.String r1 = r6.title
            r4[r3] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r4[r1] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            return r0
        L_0x03c4:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
        L_0x03ca:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Long> r4 = r4.users
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0401
            org.telegram.messenger.MessagesController r4 = r21.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            java.util.ArrayList<java.lang.Long> r5 = r5.users
            java.lang.Object r5 = r5.get(r3)
            java.lang.Long r5 = (java.lang.Long) r5
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 == 0) goto L_0x03fe
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r4)
            int r5 = r2.length()
            if (r5 == 0) goto L_0x03fb
            java.lang.String r5 = ", "
            r2.append(r5)
        L_0x03fb:
            r2.append(r4)
        L_0x03fe:
            int r3 = r3 + 1
            goto L_0x03ca
        L_0x0401:
            r0 = 2131626516(0x7f0e0a14, float:1.888027E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            r3[r10] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r2.toString()
            r13 = 2
            r3[r13] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            return r0
        L_0x041b:
            r13 = 2
            boolean r15 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r15 == 0) goto L_0x0433
            r0 = 2131626520(0x7f0e0a18, float:1.8880279E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupCreatedCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0433:
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            if (r13 == 0) goto L_0x043e
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x043e:
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r13 == 0) goto L_0x0502
            long r4 = r3.user_id
            r7 = 0
            int r2 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r2 != 0) goto L_0x0463
            java.util.ArrayList<java.lang.Long> r2 = r3.users
            int r2 = r2.size()
            r3 = 1
            if (r2 != r3) goto L_0x0463
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Long> r2 = r2.users
            java.lang.Object r2 = r2.get(r10)
            java.lang.Long r2 = (java.lang.Long) r2
            long r4 = r2.longValue()
        L_0x0463:
            r2 = 0
            int r7 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x04ab
            int r0 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r0 != 0) goto L_0x0481
            r0 = 2131626525(0x7f0e0a1d, float:1.8880289E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0481:
            org.telegram.messenger.MessagesController r0 = r21.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x0491
            r2 = 0
            return r2
        L_0x0491:
            r2 = 2131626524(0x7f0e0a1c, float:1.8880287E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            r3[r10] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r3[r1] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x04ab:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
        L_0x04b1:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Long> r4 = r4.users
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x04e8
            org.telegram.messenger.MessagesController r4 = r21.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            java.util.ArrayList<java.lang.Long> r5 = r5.users
            java.lang.Object r5 = r5.get(r3)
            java.lang.Long r5 = (java.lang.Long) r5
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 == 0) goto L_0x04e5
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r4)
            int r5 = r2.length()
            if (r5 == 0) goto L_0x04e2
            java.lang.String r5 = ", "
            r2.append(r5)
        L_0x04e2:
            r2.append(r4)
        L_0x04e5:
            int r3 = r3 + 1
            goto L_0x04b1
        L_0x04e8:
            r0 = 2131626524(0x7f0e0a1c, float:1.8880287E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            r3[r10] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r2.toString()
            r13 = 2
            r3[r13] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            return r0
        L_0x0502:
            r13 = 2
            boolean r15 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r15 == 0) goto L_0x051a
            r0 = 2131626534(0x7f0e0a26, float:1.8880307E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r15 = 1
            r2[r15] = r1
            java.lang.String r1 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x051a:
            r15 = 1
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r12 == 0) goto L_0x0531
            r0 = 2131626513(0x7f0e0a11, float:1.8880264E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            r2[r10] = r1
            java.lang.String r1 = r3.title
            r2[r15] = r1
            java.lang.String r1 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0531:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r12 != 0) goto L_0x0ddc
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r12 == 0) goto L_0x053b
            goto L_0x0ddc
        L_0x053b:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r2 == 0) goto L_0x05a1
            long r2 = r3.user_id
            int r4 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x0559
            r0 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
            r4 = 2
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r5 = 1
            r2[r5] = r1
            java.lang.String r1 = "NotificationGroupKickYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0559:
            r4 = 2
            r5 = 1
            int r7 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r7 != 0) goto L_0x0571
            r0 = 2131626528(0x7f0e0a20, float:1.8880295E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r5] = r1
            java.lang.String r1 = "NotificationGroupLeftMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0571:
            org.telegram.messenger.MessagesController r2 = r21.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            long r3 = r0.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            if (r0 != 0) goto L_0x0587
            r2 = 0
            return r2
        L_0x0587:
            r2 = 2131626526(0x7f0e0a1e, float:1.888029E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            r3[r10] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r3[r1] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x05a1:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r2 == 0) goto L_0x05ac
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x05ac:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r2 == 0) goto L_0x05b7
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x05b7:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r2 == 0) goto L_0x05cc
            r0 = 2131624139(0x7f0e00cb, float:1.887545E38)
            r2 = 1
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05cc:
            r2 = 1
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r8 == 0) goto L_0x05e1
            r0 = 2131624139(0x7f0e00cb, float:1.887545E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r3.title
            r1[r10] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05e1:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r2 == 0) goto L_0x05ec
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x05ec:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r2 == 0) goto L_0x0dda
            java.lang.String r2 = "..."
            r3 = 20
            if (r6 == 0) goto L_0x08d2
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r8 == 0) goto L_0x0600
            boolean r8 = r6.megagroup
            if (r8 == 0) goto L_0x08d2
        L_0x0600:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0618
            r0 = 2131626481(0x7f0e09f1, float:1.88802E38)
            r8 = 2
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r9 = 1
            r2[r9] = r1
            java.lang.String r1 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0618:
            r8 = 2
            r9 = 1
            boolean r12 = r0.isMusic()
            if (r12 == 0) goto L_0x0632
            r0 = 2131626478(0x7f0e09ee, float:1.8880193E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r9] = r1
            java.lang.String r1 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0632:
            boolean r8 = r0.isVideo()
            r9 = 2131626502(0x7f0e0a06, float:1.8880242E38)
            java.lang.String r12 = "NotificationActionPinnedText"
            if (r8 == 0) goto L_0x0685
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0671
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0671
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r10] = r1
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = r6.title
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r9, r2)
            return r0
        L_0x0671:
            r3 = 1
            r4 = 2
            r0 = 2131626505(0x7f0e0a09, float:1.8880248E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0685:
            boolean r7 = r0.isGif()
            if (r7 == 0) goto L_0x06d3
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x06bf
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x06bf
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r10] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = r6.title
            r7 = 2
            r2[r7] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r9, r2)
            return r0
        L_0x06bf:
            r4 = 1
            r7 = 2
            r0 = 2131626472(0x7f0e09e8, float:1.8880181E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x06d3:
            r4 = 1
            r7 = 2
            boolean r8 = r0.isVoice()
            if (r8 == 0) goto L_0x06ed
            r0 = 2131626508(0x7f0e0a0c, float:1.8880254E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x06ed:
            boolean r8 = r0.isRoundVideo()
            if (r8 == 0) goto L_0x0705
            r0 = 2131626493(0x7f0e09fd, float:1.8880224E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0705:
            boolean r4 = r0.isSticker()
            if (r4 != 0) goto L_0x08a3
            boolean r4 = r0.isAnimatedSticker()
            if (r4 == 0) goto L_0x0713
            goto L_0x08a3
        L_0x0713:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r4.media
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0761
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x074d
            java.lang.String r2 = r4.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x074d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r10] = r1
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = r6.title
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r9, r2)
            return r0
        L_0x074d:
            r3 = 1
            r4 = 2
            r0 = 2131626457(0x7f0e09d9, float:1.888015E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0761:
            boolean r5 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x088f
            boolean r5 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x076b
            goto L_0x088f
        L_0x076b:
            boolean r5 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0783
            r0 = 2131626468(0x7f0e09e4, float:1.8880173E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r5 = 1
            r2[r5] = r1
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0783:
            r5 = 1
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x07a7
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7
            r0 = 2131626454(0x7f0e09d6, float:1.8880145E38)
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r5] = r1
            java.lang.String r1 = r7.first_name
            java.lang.String r3 = r7.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r3)
            r3 = 2
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x07a7:
            boolean r5 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x07e3
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x07cb
            r2 = 2131626490(0x7f0e09fa, float:1.8880218E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            r3[r10] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r5 = 2
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x07cb:
            r4 = 1
            r5 = 2
            r2 = 2131626487(0x7f0e09f7, float:1.8880212E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            r3[r10] = r1
            java.lang.String r1 = r6.title
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x07e3:
            boolean r5 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x082d
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0819
            java.lang.String r2 = r4.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0819
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r10] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = r6.title
            r5 = 2
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r9, r2)
            return r0
        L_0x0819:
            r4 = 1
            r5 = 2
            r0 = 2131626484(0x7f0e09f4, float:1.8880205E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x082d:
            r4 = 1
            r5 = 2
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r7 == 0) goto L_0x0845
            r0 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0845:
            java.lang.CharSequence r4 = r0.messageText
            if (r4 == 0) goto L_0x087b
            int r4 = r4.length()
            if (r4 <= 0) goto L_0x087b
            java.lang.CharSequence r0 = r0.messageText
            int r4 = r0.length()
            if (r4 <= r3) goto L_0x086a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r10, r3)
            r4.append(r0)
            r4.append(r2)
            java.lang.String r0 = r4.toString()
        L_0x086a:
            java.lang.Object[] r2 = new java.lang.Object[r14]
            r2[r10] = r1
            r8 = 1
            r2[r8] = r0
            java.lang.String r0 = r6.title
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r9, r2)
            return r0
        L_0x087b:
            r3 = 2
            r8 = 1
            r0 = 2131626481(0x7f0e09f1, float:1.88802E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x088f:
            r3 = 2
            r8 = 1
            r0 = 2131626466(0x7f0e09e2, float:1.888017E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x08a3:
            r8 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x08bf
            r2 = 2131626498(0x7f0e0a02, float:1.8880234E38)
            java.lang.Object[] r3 = new java.lang.Object[r14]
            r3[r10] = r1
            java.lang.String r1 = r6.title
            r3[r8] = r1
            r4 = 2
            r3[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x08bf:
            r4 = 2
            r0 = 2131626496(0x7f0e0a00, float:1.888023E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x08d2:
            r8 = 1
            if (r6 == 0) goto L_0x0b6e
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x08e9
            r0 = 2131626482(0x7f0e09f2, float:1.8880201E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08e9:
            boolean r1 = r0.isMusic()
            if (r1 == 0) goto L_0x08ff
            r0 = 2131626479(0x7f0e09ef, float:1.8880195E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08ff:
            boolean r1 = r0.isVideo()
            r8 = 2131626503(0x7f0e0a07, float:1.8880244E38)
            java.lang.String r9 = "NotificationActionPinnedTextChannel"
            if (r1 == 0) goto L_0x094d
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x093c
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x093c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            return r0
        L_0x093c:
            r2 = 1
            r0 = 2131626506(0x7f0e0a0a, float:1.888025E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x094d:
            boolean r1 = r0.isGif()
            if (r1 == 0) goto L_0x0996
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0985
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0985
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
            r1[r10] = r2
            r4 = 1
            r1[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            return r0
        L_0x0985:
            r4 = 1
            r0 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0996:
            r4 = 1
            boolean r1 = r0.isVoice()
            if (r1 == 0) goto L_0x09ad
            r0 = 2131626509(0x7f0e0a0d, float:1.8880256E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09ad:
            boolean r1 = r0.isRoundVideo()
            if (r1 == 0) goto L_0x09c3
            r0 = 2131626494(0x7f0e09fe, float:1.8880226E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09c3:
            boolean r1 = r0.isSticker()
            if (r1 != 0) goto L_0x0b43
            boolean r1 = r0.isAnimatedSticker()
            if (r1 == 0) goto L_0x09d1
            goto L_0x0b43
        L_0x09d1:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r7 == 0) goto L_0x0a1a
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0a09
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a09
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
            r1[r10] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            return r0
        L_0x0a09:
            r2 = 1
            r0 = 2131626458(0x7f0e09da, float:1.8880153E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a1a:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x0b32
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x0a24
            goto L_0x0b32
        L_0x0a24:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0a39
            r0 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a39:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r5 == 0) goto L_0x0a5b
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131626455(0x7f0e09d7, float:1.8880147E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = r4.first_name
            java.lang.String r3 = r4.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a5b:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0a93
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0a7d
            r1 = 2131626491(0x7f0e09fb, float:1.888022E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r2[r10] = r3
            java.lang.String r0 = r0.question
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0a7d:
            r2 = 2
            r3 = 1
            r1 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r6.title
            r2[r10] = r4
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0a93:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0ad8
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0ac7
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ac7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            r5 = 1
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            return r0
        L_0x0ac7:
            r5 = 1
            r0 = 2131626485(0x7f0e09f5, float:1.8880208E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ad8:
            r5 = 1
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0aed
            r0 = 2131626461(0x7f0e09dd, float:1.8880159E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0aed:
            java.lang.CharSequence r1 = r0.messageText
            if (r1 == 0) goto L_0x0b21
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0b21
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            if (r1 <= r3) goto L_0x0b12
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r10, r3)
            r1.append(r0)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
        L_0x0b12:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r1)
            return r0
        L_0x0b21:
            r2 = 1
            r0 = 2131626482(0x7f0e09f2, float:1.8880201E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b32:
            r2 = 1
            r0 = 2131626467(0x7f0e09e3, float:1.8880171E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b43:
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0b5d
            r1 = 2131626499(0x7f0e0a03, float:1.8880236E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r2[r10] = r3
            r8 = 1
            r2[r8] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0b5d:
            r8 = 1
            r0 = 2131626497(0x7f0e0a01, float:1.8880232E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b6e:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0b80
            r0 = 2131626483(0x7f0e09f3, float:1.8880203E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0b80:
            boolean r6 = r0.isMusic()
            if (r6 == 0) goto L_0x0b94
            r0 = 2131626480(0x7f0e09f0, float:1.8880197E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedMusicUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0b94:
            boolean r6 = r0.isVideo()
            r8 = 2131626504(0x7f0e0a08, float:1.8880246E38)
            java.lang.String r9 = "NotificationActionPinnedTextUser"
            if (r6 == 0) goto L_0x0bde
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0bcf
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0bcf
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r2)
            return r0
        L_0x0bcf:
            r3 = 1
            r0 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0bde:
            boolean r6 = r0.isGif()
            if (r6 == 0) goto L_0x0CLASSNAME
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r2)
            return r0
        L_0x0CLASSNAME:
            r4 = 1
            r0 = 2131626474(0x7f0e09ea, float:1.8880185E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0CLASSNAME:
            r4 = 1
            boolean r6 = r0.isVoice()
            if (r6 == 0) goto L_0x0CLASSNAME
            r0 = 2131626510(0x7f0e0a0e, float:1.8880258E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0CLASSNAME:
            boolean r6 = r0.isRoundVideo()
            if (r6 == 0) goto L_0x0c4c
            r0 = 2131626495(0x7f0e09ff, float:1.8880228E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0c4c:
            boolean r4 = r0.isSticker()
            if (r4 != 0) goto L_0x0db4
            boolean r4 = r0.isAnimatedSticker()
            if (r4 == 0) goto L_0x0c5a
            goto L_0x0db4
        L_0x0c5a:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r4.media
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r7 == 0) goto L_0x0c9f
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0CLASSNAME
            java.lang.String r2 = r4.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r2)
            return r0
        L_0x0CLASSNAME:
            r3 = 1
            r0 = 2131626459(0x7f0e09db, float:1.8880155E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0c9f:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x0da5
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x0ca9
            goto L_0x0da5
        L_0x0ca9:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0cbc
            r0 = 2131626470(0x7f0e09e6, float:1.8880177E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0cbc:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r5 == 0) goto L_0x0cdc
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r6 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r6
            r0 = 2131626456(0x7f0e09d8, float:1.8880149E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = r6.first_name
            java.lang.String r3 = r6.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r3)
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0cdc:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0d10
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r6 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r6
            org.telegram.tgnet.TLRPC$Poll r0 = r6.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x0cfc
            r2 = 2131626492(0x7f0e09fc, float:1.8880222E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r10] = r1
            java.lang.String r0 = r0.question
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x0cfc:
            r3 = 2
            r4 = 1
            r2 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r10] = r1
            java.lang.String r0 = r0.question
            r3[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPollUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x0d10:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0d51
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0d42
            java.lang.String r2 = r4.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0d42
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r2)
            return r0
        L_0x0d42:
            r4 = 1
            r0 = 2131626486(0x7f0e09f6, float:1.888021E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0d51:
            r4 = 1
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r5 == 0) goto L_0x0d64
            r0 = 2131626465(0x7f0e09e1, float:1.8880167E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0d64:
            java.lang.CharSequence r4 = r0.messageText
            if (r4 == 0) goto L_0x0d96
            int r4 = r4.length()
            if (r4 <= 0) goto L_0x0d96
            java.lang.CharSequence r0 = r0.messageText
            int r4 = r0.length()
            if (r4 <= r3) goto L_0x0d89
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r10, r3)
            r4.append(r0)
            r4.append(r2)
            java.lang.String r0 = r4.toString()
        L_0x0d89:
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r9, r8, r2)
            return r0
        L_0x0d96:
            r3 = 1
            r0 = 2131626483(0x7f0e09f3, float:1.8880203E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0da5:
            r3 = 1
            r0 = 2131626471(0x7f0e09e7, float:1.888018E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0db4:
            r3 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0dcc
            r2 = 2131626500(0x7f0e0a04, float:1.8880238E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r10] = r1
            r4[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            return r0
        L_0x0dcc:
            r0 = 2131626501(0x7f0e0a05, float:1.888024E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0dda:
            r0 = 0
            return r0
        L_0x0ddc:
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            long r2 = r2.channel_id
            r4 = 0
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x0e12
            boolean r2 = r6.megagroup
            if (r2 != 0) goto L_0x0e12
            boolean r0 = r22.isVideoAvatar()
            if (r0 == 0) goto L_0x0e01
            r0 = 2131624815(0x7f0e036f, float:1.887682E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0e01:
            r1 = 1
            r0 = 2131624780(0x7f0e034c, float:1.887675E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0e12:
            boolean r0 = r22.isVideoAvatar()
            if (r0 == 0) goto L_0x0e2c
            r0 = 2131626515(0x7f0e0a13, float:1.8880268E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationEditedGroupVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0e2c:
            r2 = 2
            r3 = 1
            r0 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r10] = r1
            java.lang.String r1 = r6.title
            r2[r3] = r1
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0e40:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0e47:
            r3 = 1
            r0 = 2131626511(0x7f0e0a0f, float:1.888026E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r10] = r1
            java.lang.String r1 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0e56:
            boolean r1 = r22.isMediaEmpty()
            if (r1 == 0) goto L_0x0e74
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e6b
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x0e6b:
            r1 = r13
            r0 = 2131626225(0x7f0e08f1, float:1.887968E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e74:
            r1 = r13
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0ebb
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0e9f
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e9f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0e9f:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0eb1
            r0 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0eb1:
            r0 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ebb:
            boolean r2 = r22.isVideo()
            if (r2 == 0) goto L_0x0var_
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0ee5
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ee5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0ee5:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0ef7
            r0 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ef7:
            r0 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r2 = r22.isGame()
            if (r2 == 0) goto L_0x0var_
            r0 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r2 = r22.isVoice()
            if (r2 == 0) goto L_0x0var_
            r0 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r2 = r22.isRoundVideo()
            if (r2 == 0) goto L_0x0var_
            r0 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r2 = r22.isMusic()
            if (r2 == 0) goto L_0x0var_
            r0 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0var_
            r0 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0var_
            r0 = 2131627292(0x7f0e0d1c, float:1.8881844E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            r0 = 2131627111(0x7f0e0CLASSNAME, float:1.8881477E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x1043
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x0f7d
            goto L_0x1043
        L_0x0f7d:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0f8b
            r0 = 2131624397(0x7f0e01cd, float:1.8875973E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f8b:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x102c
            boolean r1 = r22.isSticker()
            if (r1 != 0) goto L_0x0ffe
            boolean r1 = r22.isAnimatedSticker()
            if (r1 == 0) goto L_0x0f9c
            goto L_0x0ffe
        L_0x0f9c:
            boolean r1 = r22.isGif()
            if (r1 == 0) goto L_0x0fd0
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0fc6
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0fc6
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0fc6:
            r0 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fd0:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0ff4
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ff4
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0ff4:
            r0 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ffe:
            java.lang.String r0 = r22.getStickerEmoji()
            if (r0 == 0) goto L_0x1022
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " "
            r1.append(r0)
            r0 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x1022:
            r0 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x102c:
            java.lang.CharSequence r2 = r0.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x103b
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x103b:
            r0 = 2131626225(0x7f0e08f1, float:1.887968E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x1043:
            r0 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x104d:
            r1 = r13
            if (r24 == 0) goto L_0x1052
            r24[r10] = r10
        L_0x1052:
            r0 = 2131626225(0x7f0e08f1, float:1.887968E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x105a:
            r0 = 2131626531(0x7f0e0a23, float:1.88803E38)
            java.lang.String r1 = "NotificationHiddenMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:232:0x05a9, code lost:
        if (r11.getBoolean("EnablePreviewGroup", true) == false) goto L_0x05ad;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x05b5, code lost:
        if (r11.getBoolean("EnablePreviewChannel", r12) != false) goto L_0x05b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x05b7, code lost:
        r4 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x05bb, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0f8b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x05bd, code lost:
        r3 = r4.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x05c1, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x06e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x05c3, code lost:
        r4 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x05c9, code lost:
        if (r4 != 0) goto L_0x05e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x05d2, code lost:
        if (r3.users.size() != 1) goto L_0x05e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x05d4, code lost:
        r4 = r0.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x05e9, code lost:
        if (r4 == 0) goto L_0x068b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x05f3, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x060f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x05f7, code lost:
        if (r6.megagroup != false) goto L_0x060f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x05f9, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x0611, code lost:
        if (r4 != r17) goto L_0x0629;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x0613, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x0629, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x0635, code lost:
        if (r0 != null) goto L_0x0639;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x0637, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x063d, code lost:
        if (r8 != r0.id) goto L_0x066f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x0641, code lost:
        if (r6.megagroup == false) goto L_0x0659;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0643, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0659, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x066f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x068b, code lost:
        r3 = new java.lang.StringBuilder();
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x069b, code lost:
        if (r4 >= r0.messageOwner.action.users.size()) goto L_0x06c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x069d, code lost:
        r5 = getMessagesController().getUser(r0.messageOwner.action.users.get(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x06b1, code lost:
        if (r5 == null) goto L_0x06c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x06b3, code lost:
        r5 = org.telegram.messenger.UserObject.getUserName(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x06bb, code lost:
        if (r3.length() == 0) goto L_0x06c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x06bd, code lost:
        r3.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x06c2, code lost:
        r3.append(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:275:0x06c5, code lost:
        r4 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x06c8, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r1, r6.title, r3.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x06e7, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L_0x06fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0700, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) == false) goto L_0x070a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x070c, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L_0x07d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:285:0x070e, code lost:
        r4 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0714, code lost:
        if (r4 != 0) goto L_0x0730;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x071d, code lost:
        if (r3.users.size() != 1) goto L_0x0730;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x071f, code lost:
        r4 = r0.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x0734, code lost:
        if (r4 == 0) goto L_0x077c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x0738, code lost:
        if (r4 != r17) goto L_0x0750;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x073a, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x0750, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x075c, code lost:
        if (r0 != null) goto L_0x0760;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x075e, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0760, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x077c, code lost:
        r3 = new java.lang.StringBuilder();
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x078c, code lost:
        if (r4 >= r0.messageOwner.action.users.size()) goto L_0x07b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x078e, code lost:
        r5 = getMessagesController().getUser(r0.messageOwner.action.users.get(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x07a2, code lost:
        if (r5 == null) goto L_0x07b6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x07a4, code lost:
        r5 = org.telegram.messenger.UserObject.getUserName(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x07ac, code lost:
        if (r3.length() == 0) goto L_0x07b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x07ae, code lost:
        r3.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x07b3, code lost:
        r3.append(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x07b6, code lost:
        r4 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x07b9, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r1, r6.title, r3.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x07d8, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x07ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x07f3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x0808;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x080a, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x080e, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x0812;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x0814, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x087e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x0816, code lost:
        r3 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x081a, code lost:
        if (r3 != r17) goto L_0x0832;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x0837, code lost:
        if (r3 != r8) goto L_0x084c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x084c, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x085e, code lost:
        if (r0 != null) goto L_0x0862;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x0860, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x087e, code lost:
        r5 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x0881, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x088b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x088d, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x0897;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x0899, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x08ae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x08b2, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x08c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x08c7, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x08d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x08d3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0eb9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x08d9, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r6) == false) goto L_0x0bb2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x08dd, code lost:
        if (r6.megagroup == false) goto L_0x08e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x08e1, code lost:
        r1 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x08e3, code lost:
        if (r1 != null) goto L_0x08f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x08fe, code lost:
        if (r1.isMusic() == false) goto L_0x0911;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x0900, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x091a, code lost:
        if (r1.isVideo() == false) goto L_0x0963;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0920, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0950;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x092a, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x0950;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x092c, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📹 " + r1.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x0950, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x0967, code lost:
        if (r1.isGif() == false) goto L_0x09b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x096d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x099d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x0977, code lost:
        if (android.text.TextUtils.isEmpty(r1.messageOwner.message) != false) goto L_0x099d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x0979, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🎬 " + r1.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x099d, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x09b6, code lost:
        if (r1.isVoice() == false) goto L_0x09c9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x09b8, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x09cd, code lost:
        if (r1.isRoundVideo() == false) goto L_0x09e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x09cf, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x09e4, code lost:
        if (r1.isSticker() != false) goto L_0x0b84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x09ea, code lost:
        if (r1.isAnimatedSticker() == false) goto L_0x09ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x09ee, code lost:
        r2 = r1.messageOwner;
        r5 = r2.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x09f4, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0a3b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x09fa, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0a28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x0a02, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x0a28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0a04, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📎 " + r1.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x0a28, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x0a3d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0b71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:396:0x0a41, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0a45;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:398:0x0a47, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0a5c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0a49, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0a5e, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0a84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x0a60, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0.messageOwner.media;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r6.title, org.telegram.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:404:0x0a86, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0ac0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0a88, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0a8e, code lost:
        if (r0.quiz == false) goto L_0x0aa8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0a90, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0aa8, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0ac2, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0b09;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0ac8, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0af6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0ad0, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x0af6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0ad2, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🖼 " + r1.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x0af6, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:418:0x0b0d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0b20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x0b0f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x0b20, code lost:
        r0 = r1.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x0b22, code lost:
        if (r0 == null) goto L_0x0b5e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x0b28, code lost:
        if (r0.length() <= 0) goto L_0x0b5e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:424:0x0b2a, code lost:
        r0 = r1.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x0b32, code lost:
        if (r0.length() <= 20) goto L_0x0b4d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:426:0x0b34, code lost:
        r1 = new java.lang.StringBuilder();
        r5 = 0;
        r1.append(r0.subSequence(0, 20));
        r1.append("...");
        r0 = r1.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x0b4d, code lost:
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x0b4e, code lost:
        r1 = new java.lang.Object[2];
        r1[r5] = r6.title;
        r1[1] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x0b5e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x0b71, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0b84, code lost:
        r0 = r1.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:432:0x0b89, code lost:
        if (r0 == null) goto L_0x0ba0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0b8b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:434:0x0ba0, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0bb2, code lost:
        r3 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:436:0x0bb5, code lost:
        if (r3 != null) goto L_0x0bcc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0bd2, code lost:
        if (r3.isMusic() == false) goto L_0x0be7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:440:0x0bd4, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:442:0x0bf0, code lost:
        if (r3.isVideo() == false) goto L_0x0c3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:444:0x0bf6, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:446:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r3.messageOwner.message) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "📹 " + r3.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:448:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x0CLASSNAME, code lost:
        if (r3.isGif() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0CLASSNAME, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0c7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r3.messageOwner.message) != false) goto L_0x0c7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "🎬 " + r3.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x0c7a, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:458:0x0CLASSNAME, code lost:
        if (r3.isVoice() == false) goto L_0x0cac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x0cb0, code lost:
        if (r3.isRoundVideo() == false) goto L_0x0cc5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:462:0x0cb2, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:464:0x0cc9, code lost:
        if (r3.isSticker() != false) goto L_0x0e87;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:466:0x0ccf, code lost:
        if (r3.isAnimatedSticker() == false) goto L_0x0cd3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0cd3, code lost:
        r4 = r3.messageOwner;
        r7 = r4.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x0cd9, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0d25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0cdf, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0d0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:472:0x0ce7, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0d0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0ce9, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "📎 " + r3.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0d0f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:476:0x0d27, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0e71;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:478:0x0d2b, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0d2f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:480:0x0d31, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0d49;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0d33, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0d4b, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0d73;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:484:0x0d4d, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0.messageOwner.media;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r1, r6.title, org.telegram.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:486:0x0d75, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0db3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0d77, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0d7d, code lost:
        if (r0.quiz == false) goto L_0x0d99;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0d7f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r1, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0d99, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r1, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0db5, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0e01;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0dbb, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0deb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:496:0x0dc3, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0deb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0dc5, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "🖼 " + r3.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0deb, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x0e06, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0e1b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x0e08, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0e1b, code lost:
        r0 = r3.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0e1d, code lost:
        if (r0 == null) goto L_0x0e5b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0e23, code lost:
        if (r0.length() <= 0) goto L_0x0e5b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0e25, code lost:
        r0 = r3.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0e2d, code lost:
        if (r0.length() <= 20) goto L_0x0e48;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0e2f, code lost:
        r3 = new java.lang.StringBuilder();
        r7 = 0;
        r3.append(r0.subSequence(0, 20));
        r3.append("...");
        r0 = r3.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0e48, code lost:
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x0e49, code lost:
        r2 = new java.lang.Object[3];
        r2[r7] = r1;
        r2[1] = r0;
        r2[2] = r6.title;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0e5b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0e71, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0e87, code lost:
        r0 = r3.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0e8d, code lost:
        if (r0 == null) goto L_0x0ea5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0e8f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r1, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0ea5, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0ebb, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) == false) goto L_0x0ec5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0ec7, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) == false) goto L_0x158c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0ec9, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r3).emoticon;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0ed1, code lost:
        if (android.text.TextUtils.isEmpty(r0) == false) goto L_0x0ef9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0ed5, code lost:
        if (r23 != r17) goto L_0x0ee5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0ed7, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChatThemeDisabledYou", NUM, new java.lang.Object[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0ee5, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChatThemeDisabled", NUM, r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0efd, code lost:
        if (r23 != r17) goto L_0x0f0e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0eff, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChangedChatThemeYou", NUM, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0f0e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChangedChatThemeTo", NUM, r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0var_, code lost:
        if (r4.peer_id.channel_id == 0) goto L_0x0f5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0f2c, code lost:
        if (r6.megagroup != false) goto L_0x0f5a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0var_, code lost:
        if (r27.isVideoAvatar() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0f5f, code lost:
        if (r27.isVideoAvatar() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0f8f, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r6) == false) goto L_0x1243;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0var_, code lost:
        if (r6.megagroup != false) goto L_0x1243;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0var_, code lost:
        if (r27.isMediaEmpty() == false) goto L_0x0fd0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0f9b, code lost:
        if (r28 != false) goto L_0x0fbf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x0fa5, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0fbf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0fa7, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r1, r0.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0fd0, code lost:
        r2 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x0fd6, code lost:
        if ((r2.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x1020;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0fd8, code lost:
        if (r28 != false) goto L_0x100f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0fde, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x100f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0fe6, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x100f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x0fe8, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r1, "🖼 " + r0.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x1024, code lost:
        if (r27.isVideo() == false) goto L_0x1070;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x1026, code lost:
        if (r28 != false) goto L_0x105f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x102c, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x105f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x1036, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x105f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x1038, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r1, "📹 " + r0.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x1076, code lost:
        if (r27.isVoice() == false) goto L_0x1087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x108b, code lost:
        if (r27.isRoundVideo() == false) goto L_0x109c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x10a0, code lost:
        if (r27.isMusic() == false) goto L_0x10b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x10b1, code lost:
        r2 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x10b7, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x10d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x10b9, code lost:
        r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x10d8, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x110e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x10da, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x10e0, code lost:
        if (r0.quiz == false) goto L_0x10f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x10e2, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", NUM, r1, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x10f8, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", NUM, r1, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x1110, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x1232;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x1114, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x1118;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x111a, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x112d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x112f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x1202;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x1135, code lost:
        if (r27.isSticker() != false) goto L_0x11d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x113b, code lost:
        if (r27.isAnimatedSticker() == false) goto L_0x113f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x1143, code lost:
        if (r27.isGif() == false) goto L_0x118f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:606:0x1145, code lost:
        if (r28 != false) goto L_0x117e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:608:0x114b, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x117e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x1155, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x117e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x1157, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r1, "🎬 " + r0.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x118f, code lost:
        if (r28 != false) goto L_0x11c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x1195, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x11c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x119f, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x11c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x11a1, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r1, "📎 " + r0.messageOwner.message);
        r29[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x11d9, code lost:
        r0 = r27.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x11df, code lost:
        if (r0 == null) goto L_0x11f3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x11e1, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", NUM, r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x11f3, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x1203, code lost:
        if (r28 != false) goto L_0x1222;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x120b, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x1222;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x120d, code lost:
        r13 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", NUM, r1, r0.messageText);
        r29[0] = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x124c, code lost:
        if (r27.isMediaEmpty() == false) goto L_0x1287;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x124e, code lost:
        if (r28 != false) goto L_0x1271;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x1258, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x1271;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x1287, code lost:
        r3 = r22;
        r8 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x128f, code lost:
        if ((r8.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x12dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x1291, code lost:
        if (r28 != false) goto L_0x12c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x1297, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x12c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x129f, code lost:
        if (android.text.TextUtils.isEmpty(r8.message) != false) goto L_0x12c7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x12e1, code lost:
        if (r27.isVideo() == false) goto L_0x1331;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x12e3, code lost:
        if (r28 != false) goto L_0x131b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x12e9, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x131b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x12f3, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x131b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:657:0x1338, code lost:
        if (r27.isVoice() == false) goto L_0x134d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x1351, code lost:
        if (r27.isRoundVideo() == false) goto L_0x1366;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:663:0x136a, code lost:
        if (r27.isMusic() == false) goto L_0x137f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:665:0x137f, code lost:
        r8 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x1385, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x13a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:667:0x1387, code lost:
        r8 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:669:0x13ab, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x13e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x13ad, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r8).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:671:0x13b3, code lost:
        if (r0.quiz == false) goto L_0x13cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x13b5, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", NUM, r1, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:673:0x13cf, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", NUM, r1, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:675:0x13eb, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x1409;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x140b, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x1544;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x140f, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x1413;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x1415, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x142d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:685:0x142f, code lost:
        if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x1513;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:687:0x1435, code lost:
        if (r27.isSticker() != false) goto L_0x14e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x143b, code lost:
        if (r27.isAnimatedSticker() == false) goto L_0x143f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:691:0x1443, code lost:
        if (r27.isGif() == false) goto L_0x1493;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x1445, code lost:
        if (r28 != false) goto L_0x147d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x144b, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x147d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x1455, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x147d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:699:0x1493, code lost:
        if (r28 != false) goto L_0x14cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:701:0x1499, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x14cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:703:0x14a3, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x14cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x14e1, code lost:
        r0 = r27.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:707:0x14e7, code lost:
        if (r0 == null) goto L_0x14ff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x14e9, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, r1, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:709:0x14ff, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x1513, code lost:
        if (r28 != false) goto L_0x1531;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x151b, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x1531;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:?, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r1, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:773:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:775:?, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:?, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:777:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:779:?, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:781:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:?, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:783:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:785:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:786:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:787:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:788:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:789:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:790:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:791:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:792:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:793:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:794:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:795:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageMusic", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", NUM, r1, org.telegram.messenger.ContactsController.formatName(r2.first_name, r2.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:802:?, code lost:
        return r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:803:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:804:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:805:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r1, r6.title, r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:806:?, code lost:
        return org.telegram.messenger.LocaleController.formatString(r22, NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:807:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r1, r6.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:808:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:809:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r1, r6.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:810:?, code lost:
        return org.telegram.messenger.LocaleController.formatString(" ", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:811:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:812:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:813:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMusic", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:814:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", NUM, r1, r6.title, org.telegram.messenger.ContactsController.formatName(r8.first_name, r8.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:815:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", NUM, r1, r6.title, r8.game.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:816:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:817:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r1, r6.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:818:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:819:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r1, r6.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:820:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:821:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", NUM, r1, r6.title, r0.messageText);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:822:?, code lost:
        return org.telegram.messenger.LocaleController.formatString(r3, NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:823:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", NUM, r1, r6.title);
     */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x014b A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x014c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r27, boolean r28, boolean[] r29, boolean[] r30) {
        /*
            r26 = this;
            r0 = r27
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x158e
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x158e
        L_0x000e:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r2 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r4 = r1.chat_id
            r6 = 0
            int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x001d
            goto L_0x001f
        L_0x001d:
            long r4 = r1.channel_id
        L_0x001f:
            long r8 = r1.user_id
            r1 = 1
            r10 = 0
            if (r30 == 0) goto L_0x0027
            r30[r10] = r1
        L_0x0027:
            org.telegram.messenger.AccountInstance r11 = r26.getAccountInstance()
            android.content.SharedPreferences r11 = r11.getNotificationsSettings()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "content_preview_"
            r12.append(r13)
            r12.append(r2)
            java.lang.String r12 = r12.toString()
            boolean r12 = r11.getBoolean(r12, r1)
            boolean r13 = r27.isFcmMessage()
            java.lang.String r14 = "NotificationMessageGroupNoText"
            java.lang.String r15 = "NotificationMessageNoText"
            r10 = 2
            if (r13 == 0) goto L_0x00d3
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 != 0) goto L_0x0074
            int r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r2 == 0) goto L_0x0074
            if (r12 == 0) goto L_0x0061
            java.lang.String r2 = "EnablePreviewAll"
            boolean r2 = r11.getBoolean(r2, r1)
            if (r2 != 0) goto L_0x00cb
        L_0x0061:
            r2 = 0
            if (r30 == 0) goto L_0x0066
            r30[r2] = r2
        L_0x0066:
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r0 = r0.localName
            r1[r2] = r0
            r0 = 2131626567(0x7f0e0a47, float:1.8880374E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r15, r0, r1)
            return r0
        L_0x0074:
            int r2 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r2 == 0) goto L_0x00cb
            if (r12 == 0) goto L_0x0092
            boolean r2 = r0.localChannel
            if (r2 != 0) goto L_0x0086
            java.lang.String r2 = "EnablePreviewGroup"
            boolean r2 = r11.getBoolean(r2, r1)
            if (r2 == 0) goto L_0x0092
        L_0x0086:
            boolean r2 = r0.localChannel
            if (r2 == 0) goto L_0x00cb
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r2 = r11.getBoolean(r2, r1)
            if (r2 != 0) goto L_0x00cb
        L_0x0092:
            if (r30 == 0) goto L_0x0097
            r2 = 0
            r30[r2] = r2
        L_0x0097:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            long r2 = r2.channel_id
            int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r4 == 0) goto L_0x00b8
            boolean r2 = r27.isSupergroup()
            if (r2 != 0) goto L_0x00b8
            r2 = 2131624764(0x7f0e033c, float:1.8876717E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r0 = r0.localName
            r3 = 0
            r1[r3] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r1)
            return r0
        L_0x00b8:
            r3 = 0
            r2 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            java.lang.Object[] r4 = new java.lang.Object[r10]
            java.lang.String r5 = r0.localUserName
            r4[r3] = r5
            java.lang.String r0 = r0.localName
            r4[r1] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r14, r2, r4)
            return r0
        L_0x00cb:
            r3 = 0
            r29[r3] = r1
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = (java.lang.String) r0
            return r0
        L_0x00d3:
            org.telegram.messenger.UserConfig r13 = r26.getUserConfig()
            long r17 = r13.getClientUserId()
            int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r13 != 0) goto L_0x00e9
            long r8 = r27.getFromChatId()
            int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r13 != 0) goto L_0x00f1
            long r8 = -r4
            goto L_0x00f1
        L_0x00e9:
            int r13 = (r8 > r17 ? 1 : (r8 == r17 ? 0 : -1))
            if (r13 != 0) goto L_0x00f1
            long r8 = r27.getFromChatId()
        L_0x00f1:
            int r13 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r13 != 0) goto L_0x0100
            int r13 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r13 == 0) goto L_0x00fb
            long r2 = -r4
            goto L_0x0100
        L_0x00fb:
            int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r13 == 0) goto L_0x0100
            r2 = r8
        L_0x0100:
            r13 = 0
            int r19 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r19 <= 0) goto L_0x0136
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            boolean r10 = r10.from_scheduled
            if (r10 == 0) goto L_0x0123
            int r10 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r10 != 0) goto L_0x0119
            r10 = 2131626250(0x7f0e090a, float:1.887973E38)
            java.lang.String r1 = "MessageScheduledReminderNotification"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r10)
            goto L_0x0149
        L_0x0119:
            r1 = 2131626575(0x7f0e0a4f, float:1.888039E38)
            java.lang.String r10 = "NotificationMessageScheduledName"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r1)
            goto L_0x0149
        L_0x0123:
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()
            java.lang.Long r10 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r10)
            if (r1 == 0) goto L_0x0148
            java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r1)
            goto L_0x0149
        L_0x0136:
            org.telegram.messenger.MessagesController r1 = r26.getMessagesController()
            long r6 = -r8
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r6)
            if (r1 == 0) goto L_0x0148
            java.lang.String r1 = r1.title
            goto L_0x0149
        L_0x0148:
            r1 = r13
        L_0x0149:
            if (r1 != 0) goto L_0x014c
            return r13
        L_0x014c:
            r6 = 0
            int r10 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r10 == 0) goto L_0x0161
            org.telegram.messenger.MessagesController r6 = r26.getMessagesController()
            java.lang.Long r7 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            if (r6 != 0) goto L_0x0162
            return r13
        L_0x0161:
            r6 = r13
        L_0x0162:
            boolean r7 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
            if (r7 == 0) goto L_0x0173
            r0 = 2131628565(0x7f0e1215, float:1.8884426E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x158d
        L_0x0173:
            java.lang.String r7 = "🎬 "
            java.lang.String r10 = "📎 "
            java.lang.String r13 = "📹 "
            r22 = r14
            java.lang.String r14 = "🖼 "
            r23 = r2
            java.lang.String r3 = "NotificationMessageText"
            r2 = 3
            r20 = 0
            int r25 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1))
            if (r25 != 0) goto L_0x058b
            int r25 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1))
            if (r25 == 0) goto L_0x058b
            if (r12 == 0) goto L_0x0578
            java.lang.String r4 = "EnablePreviewAll"
            r5 = 1
            boolean r4 = r11.getBoolean(r4, r5)
            if (r4 == 0) goto L_0x0578
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r5 == 0) goto L_0x026e
            org.telegram.tgnet.TLRPC$MessageAction r3 = r4.action
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r4 == 0) goto L_0x01ab
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x158d
        L_0x01ab:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r4 != 0) goto L_0x025d
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r4 == 0) goto L_0x01b5
            goto L_0x025d
        L_0x01b5:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r4 == 0) goto L_0x01ca
            r0 = 2131626512(0x7f0e0a10, float:1.8880262E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = "NotificationContactNewPhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x01ca:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r1 == 0) goto L_0x022e
            r1 = 2131628642(0x7f0e1262, float:1.8884582E38)
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
            r5 = 0
            r4[r5] = r3
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
            r3 = 2131626581(0x7f0e0a55, float:1.8880402E38)
            r4 = 4
            java.lang.Object[] r4 = new java.lang.Object[r4]
            org.telegram.messenger.UserConfig r6 = r26.getUserConfig()
            org.telegram.tgnet.TLRPC$User r6 = r6.getCurrentUser()
            java.lang.String r6 = r6.first_name
            r7 = 0
            r4[r7] = r6
            r4[r5] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r5 = 2
            r4[r5] = r1
            java.lang.String r0 = r0.address
            r4[r2] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            goto L_0x158d
        L_0x022e:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r1 != 0) goto L_0x0255
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r1 == 0) goto L_0x0237
            goto L_0x0255
        L_0x0237:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x158b
            boolean r0 = r3.video
            if (r0 == 0) goto L_0x024a
            r0 = 2131624638(0x7f0e02be, float:1.8876461E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x158d
        L_0x024a:
            r0 = 2131624632(0x7f0e02b8, float:1.887645E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x158d
        L_0x0255:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x158d
        L_0x025d:
            r0 = 2131626511(0x7f0e0a0f, float:1.888026E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = "NotificationContactJoined"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x026e:
            boolean r2 = r27.isMediaEmpty()
            if (r2 == 0) goto L_0x02b6
            if (r28 != 0) goto L_0x02a7
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0298
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x0298:
            r4 = 0
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r4] = r1
            r2 = 2131626567(0x7f0e0a47, float:1.8880374E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r15, r2, r0)
            goto L_0x158d
        L_0x02a7:
            r2 = 2131626567(0x7f0e0a47, float:1.8880374E38)
            r4 = 0
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r4] = r1
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r15, r2, r0)
            goto L_0x158d
        L_0x02b6:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x031d
            if (r28 != 0) goto L_0x02f5
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r4 < r5) goto L_0x02f5
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x02f5
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x02f5:
            r4 = 0
            r5 = 1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x030e
            r0 = 2131626572(0x7f0e0a4c, float:1.8880384E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageSDPhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x030e:
            r0 = 2131626568(0x7f0e0a48, float:1.8880376E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessagePhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x031d:
            boolean r2 = r27.isVideo()
            if (r2 == 0) goto L_0x0384
            if (r28 != 0) goto L_0x035c
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r2 < r4) goto L_0x035c
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x035c
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x035c:
            r4 = 0
            r5 = 1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0375
            r0 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageSDVideo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0375:
            r0 = 2131626579(0x7f0e0a53, float:1.8880398E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageVideo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0384:
            r4 = 0
            boolean r2 = r27.isGame()
            if (r2 == 0) goto L_0x03a6
            r2 = 2131626541(0x7f0e0a2d, float:1.8880321E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r0 = r0.title
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessageGame"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x158d
        L_0x03a6:
            r4 = 1
            boolean r2 = r27.isVoice()
            if (r2 == 0) goto L_0x03bd
            r0 = 2131626536(0x7f0e0a28, float:1.888031E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r5 = 0
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageAudio"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x03bd:
            r5 = 0
            boolean r2 = r27.isRoundVideo()
            if (r2 == 0) goto L_0x03d3
            r0 = 2131626571(0x7f0e0a4b, float:1.8880382E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageRound"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x03d3:
            boolean r2 = r27.isMusic()
            if (r2 == 0) goto L_0x03e8
            r0 = 2131626566(0x7f0e0a46, float:1.8880372E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageMusic"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x03e8:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r4 == 0) goto L_0x040d
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r2
            r0 = 2131626537(0x7f0e0a29, float:1.8880313E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r5] = r1
            java.lang.String r1 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r2)
            r2 = 1
            r3[r2] = r1
            java.lang.String r1 = "NotificationMessageContact2"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x158d
        L_0x040d:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x0445
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x042e
            r2 = 2131626570(0x7f0e0a4a, float:1.888038E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0442
        L_0x042e:
            r3 = 2
            r4 = 0
            r5 = 1
            r2 = 2131626569(0x7f0e0a49, float:1.8880378E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
        L_0x0442:
            r13 = r0
            goto L_0x158d
        L_0x0445:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x0567
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x044f
            goto L_0x0567
        L_0x044f:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x0464
            r0 = 2131626564(0x7f0e0a44, float:1.8880368E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageLiveLocation"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0464:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x0539
            boolean r2 = r27.isSticker()
            if (r2 != 0) goto L_0x0510
            boolean r2 = r27.isAnimatedSticker()
            if (r2 == 0) goto L_0x0476
            goto L_0x0510
        L_0x0476:
            boolean r2 = r27.isGif()
            if (r2 == 0) goto L_0x04c6
            if (r28 != 0) goto L_0x04b5
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r2 < r4) goto L_0x04b5
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x04b5
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x04b5:
            r4 = 0
            r5 = 1
            r0 = 2131626543(0x7f0e0a2f, float:1.8880325E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageGif"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x04c6:
            if (r28 != 0) goto L_0x04ff
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r2 < r4) goto L_0x04ff
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x04ff
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x04ff:
            r4 = 0
            r5 = 1
            r0 = 2131626538(0x7f0e0a2a, float:1.8880315E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageDocument"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0510:
            r4 = 0
            r5 = 1
            java.lang.String r0 = r27.getStickerEmoji()
            if (r0 == 0) goto L_0x052a
            r2 = 2131626577(0x7f0e0a51, float:1.8880394E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0442
        L_0x052a:
            r0 = 2131626576(0x7f0e0a50, float:1.8880392E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0539:
            r4 = 0
            if (r28 != 0) goto L_0x0559
            java.lang.CharSequence r2 = r0.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0559
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r1
            java.lang.CharSequence r0 = r0.messageText
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x0559:
            r5 = 1
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r4] = r1
            r1 = 2131626567(0x7f0e0a47, float:1.8880374E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r15, r1, r0)
            goto L_0x158d
        L_0x0567:
            r4 = 0
            r5 = 1
            r0 = 2131626565(0x7f0e0a45, float:1.888037E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageMap"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0578:
            r4 = 0
            r5 = 1
            if (r30 == 0) goto L_0x057e
            r30[r4] = r4
        L_0x057e:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r4] = r1
            r1 = 2131626567(0x7f0e0a47, float:1.8880374E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r15, r1, r0)
            goto L_0x158d
        L_0x058b:
            r15 = 0
            int r25 = (r4 > r15 ? 1 : (r4 == r15 ? 0 : -1))
            if (r25 == 0) goto L_0x158b
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r4 == 0) goto L_0x059d
            boolean r4 = r6.megagroup
            if (r4 != 0) goto L_0x059d
            r4 = 1
            goto L_0x059e
        L_0x059d:
            r4 = 0
        L_0x059e:
            if (r12 == 0) goto L_0x1559
            if (r4 != 0) goto L_0x05ac
            java.lang.String r5 = "EnablePreviewGroup"
            r12 = 1
            boolean r5 = r11.getBoolean(r5, r12)
            if (r5 != 0) goto L_0x05b7
            goto L_0x05ad
        L_0x05ac:
            r12 = 1
        L_0x05ad:
            if (r4 == 0) goto L_0x1559
            java.lang.String r4 = "EnablePreviewChannel"
            boolean r4 = r11.getBoolean(r4, r12)
            if (r4 == 0) goto L_0x1559
        L_0x05b7:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r5 == 0) goto L_0x0f8b
            org.telegram.tgnet.TLRPC$MessageAction r3 = r4.action
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r5 == 0) goto L_0x06e4
            long r4 = r3.user_id
            r10 = 0
            int r7 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r7 != 0) goto L_0x05e5
            java.util.ArrayList<java.lang.Long> r3 = r3.users
            int r3 = r3.size()
            r7 = 1
            if (r3 != r7) goto L_0x05e5
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Long> r3 = r3.users
            r4 = 0
            java.lang.Object r3 = r3.get(r4)
            java.lang.Long r3 = (java.lang.Long) r3
            long r4 = r3.longValue()
        L_0x05e5:
            r10 = 0
            int r3 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r3 == 0) goto L_0x068b
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r12 = r0.channel_id
            int r0 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x060f
            boolean r0 = r6.megagroup
            if (r0 != 0) goto L_0x060f
            r0 = 2131624713(0x7f0e0309, float:1.8876613E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x060f:
            int r0 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r0 != 0) goto L_0x0629
            r0 = 2131626533(0x7f0e0a25, float:1.8880305E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0629:
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            java.lang.Long r3 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            if (r0 != 0) goto L_0x0639
            r3 = 0
            return r3
        L_0x0639:
            long r3 = r0.id
            int r5 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x066f
            boolean r0 = r6.megagroup
            if (r0 == 0) goto L_0x0659
            r0 = 2131626518(0x7f0e0a16, float:1.8880274E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0659:
            r2 = 2
            r3 = 0
            r4 = 1
            r0 = 2131626517(0x7f0e0a15, float:1.8880272E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x066f:
            r3 = 0
            r4 = 1
            r5 = 2131626516(0x7f0e0a14, float:1.888027E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r2[r1] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r5, r2)
            goto L_0x0442
        L_0x068b:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 0
        L_0x0691:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            java.util.ArrayList<java.lang.Long> r5 = r5.users
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x06c8
            org.telegram.messenger.MessagesController r5 = r26.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r7 = r7.action
            java.util.ArrayList<java.lang.Long> r7 = r7.users
            java.lang.Object r7 = r7.get(r4)
            java.lang.Long r7 = (java.lang.Long) r7
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r7)
            if (r5 == 0) goto L_0x06c5
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r5)
            int r7 = r3.length()
            if (r7 == 0) goto L_0x06c2
            java.lang.String r7 = ", "
            r3.append(r7)
        L_0x06c2:
            r3.append(r5)
        L_0x06c5:
            int r4 = r4 + 1
            goto L_0x0691
        L_0x06c8:
            r0 = 2131626516(0x7f0e0a14, float:1.888027E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r1 = r3.toString()
            r5 = 2
            r2[r5] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x06e4:
            r5 = 2
            boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r11 == 0) goto L_0x06fe
            r0 = 2131626520(0x7f0e0a18, float:1.8880279E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupCreatedCall"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x06fe:
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            if (r5 == 0) goto L_0x070a
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x158d
        L_0x070a:
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r5 == 0) goto L_0x07d5
            long r4 = r3.user_id
            r7 = 0
            int r9 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0730
            java.util.ArrayList<java.lang.Long> r3 = r3.users
            int r3 = r3.size()
            r7 = 1
            if (r3 != r7) goto L_0x0730
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Long> r3 = r3.users
            r4 = 0
            java.lang.Object r3 = r3.get(r4)
            java.lang.Long r3 = (java.lang.Long) r3
            long r4 = r3.longValue()
        L_0x0730:
            r7 = 0
            int r3 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r3 == 0) goto L_0x077c
            int r0 = (r4 > r17 ? 1 : (r4 == r17 ? 0 : -1))
            if (r0 != 0) goto L_0x0750
            r0 = 2131626525(0x7f0e0a1d, float:1.8880289E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0750:
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            java.lang.Long r3 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            if (r0 != 0) goto L_0x0760
            r3 = 0
            return r3
        L_0x0760:
            r3 = 2131626524(0x7f0e0a1c, float:1.8880287E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r2[r1] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r2)
            goto L_0x0442
        L_0x077c:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 0
        L_0x0782:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            java.util.ArrayList<java.lang.Long> r5 = r5.users
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x07b9
            org.telegram.messenger.MessagesController r5 = r26.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r7 = r7.action
            java.util.ArrayList<java.lang.Long> r7 = r7.users
            java.lang.Object r7 = r7.get(r4)
            java.lang.Long r7 = (java.lang.Long) r7
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r7)
            if (r5 == 0) goto L_0x07b6
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r5)
            int r7 = r3.length()
            if (r7 == 0) goto L_0x07b3
            java.lang.String r7 = ", "
            r3.append(r7)
        L_0x07b3:
            r3.append(r5)
        L_0x07b6:
            int r4 = r4 + 1
            goto L_0x0782
        L_0x07b9:
            r0 = 2131626524(0x7f0e0a1c, float:1.8880287E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r1 = r3.toString()
            r5 = 2
            r2[r5] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x07d5:
            r5 = 2
            boolean r11 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r11 == 0) goto L_0x07ef
            r0 = 2131626534(0x7f0e0a26, float:1.8880307E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r11 = 0
            r2[r11] = r1
            java.lang.String r1 = r6.title
            r12 = 1
            r2[r12] = r1
            java.lang.String r1 = "NotificationInvitedToGroupByLink"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x07ef:
            r11 = 0
            r12 = 1
            boolean r15 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r15 == 0) goto L_0x0808
            r0 = 2131626513(0x7f0e0a11, float:1.8880264E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r11] = r1
            java.lang.String r1 = r3.title
            r2[r12] = r1
            java.lang.String r1 = "NotificationEditedGroupName"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0808:
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r5 != 0) goto L_0x0var_
            boolean r5 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r5 == 0) goto L_0x0812
            goto L_0x0var_
        L_0x0812:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r4 == 0) goto L_0x087e
            long r3 = r3.user_id
            int r5 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r5 != 0) goto L_0x0832
            r0 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
            r5 = 2
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r7 = 0
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r10 = 1
            r2[r10] = r1
            java.lang.String r1 = "NotificationGroupKickYou"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0832:
            r5 = 2
            r7 = 0
            r10 = 1
            int r11 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r11 != 0) goto L_0x084c
            r0 = 2131626528(0x7f0e0a20, float:1.8880295E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r10] = r1
            java.lang.String r1 = "NotificationGroupLeftMember"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x084c:
            org.telegram.messenger.MessagesController r3 = r26.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            long r4 = r0.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r3.getUser(r0)
            if (r0 != 0) goto L_0x0862
            r5 = 0
            return r5
        L_0x0862:
            r3 = 2131626526(0x7f0e0a1e, float:1.888029E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r2[r1] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r0, r3, r2)
            goto L_0x158d
        L_0x087e:
            r5 = 0
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r4 == 0) goto L_0x088b
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x158d
        L_0x088b:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x0897
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x158d
        L_0x0897:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r4 == 0) goto L_0x08ae
            r0 = 2131624139(0x7f0e00cb, float:1.887545E38)
            r4 = 1
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r8 = 0
            r1[r8] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x158d
        L_0x08ae:
            r4 = 1
            r8 = 0
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r9 == 0) goto L_0x08c5
            r0 = 2131624139(0x7f0e00cb, float:1.887545E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r3.title
            r1[r8] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x158d
        L_0x08c5:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r4 == 0) goto L_0x08d1
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x158d
        L_0x08d1:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r4 == 0) goto L_0x0eb9
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r3 == 0) goto L_0x0bb2
            boolean r3 = r6.megagroup
            if (r3 == 0) goto L_0x08e1
            goto L_0x0bb2
        L_0x08e1:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x08f8
            r0 = 2131626482(0x7f0e09f2, float:1.8880201E38)
            r2 = 1
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x158d
        L_0x08f8:
            r2 = 1
            r3 = 0
            boolean r4 = r1.isMusic()
            if (r4 == 0) goto L_0x0911
            r0 = 2131626479(0x7f0e09ef, float:1.8880195E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0911:
            boolean r2 = r1.isVideo()
            r3 = 2131626503(0x7f0e0a07, float:1.8880244E38)
            java.lang.String r4 = "NotificationActionPinnedTextChannel"
            if (r2 == 0) goto L_0x0963
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0950
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0950
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r5 = 0
            r1[r5] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r3, r1)
            goto L_0x0442
        L_0x0950:
            r2 = 1
            r5 = 0
            r0 = 2131626506(0x7f0e0a0a, float:1.888025E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0963:
            boolean r2 = r1.isGif()
            if (r2 == 0) goto L_0x09b0
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x099d
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x099d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r5 = 0
            r1[r5] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r3, r1)
            goto L_0x0442
        L_0x099d:
            r2 = 1
            r5 = 0
            r0 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x09b0:
            r2 = 1
            r5 = 0
            boolean r7 = r1.isVoice()
            if (r7 == 0) goto L_0x09c9
            r0 = 2131626509(0x7f0e0a0d, float:1.8880256E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x09c9:
            boolean r7 = r1.isRoundVideo()
            if (r7 == 0) goto L_0x09e0
            r0 = 2131626494(0x7f0e09fe, float:1.8880226E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x09e0:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0b84
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x09ee
            goto L_0x0b84
        L_0x09ee:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r2.media
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r7 == 0) goto L_0x0a3b
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r0 < r5) goto L_0x0a28
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0a28
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r10)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r5 = 0
            r1[r5] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r3, r1)
            goto L_0x0442
        L_0x0a28:
            r2 = 1
            r5 = 0
            r0 = 2131626458(0x7f0e09da, float:1.8880153E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0a3b:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r7 != 0) goto L_0x0b71
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r7 == 0) goto L_0x0a45
            goto L_0x0b71
        L_0x0a45:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r7 == 0) goto L_0x0a5c
            r0 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0a5c:
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x0a84
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626455(0x7f0e09d7, float:1.8880147E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0442
        L_0x0a84:
            boolean r0 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0ac0
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0aa8
            r1 = 2131626491(0x7f0e09fb, float:1.888022E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0442
        L_0x0aa8:
            r2 = 2
            r3 = 1
            r4 = 0
            r1 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r5 = r6.title
            r2[r4] = r5
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0442
        L_0x0ac0:
            boolean r0 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0b09
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r0 < r5) goto L_0x0af6
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0af6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r7 = 0
            r1[r7] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r3, r1)
            goto L_0x0442
        L_0x0af6:
            r2 = 1
            r7 = 0
            r0 = 2131626485(0x7f0e09f5, float:1.8880208E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0b09:
            r2 = 1
            r7 = 0
            boolean r0 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0b20
            r0 = 2131626461(0x7f0e09dd, float:1.8880159E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0b20:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0b5e
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0b5e
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0b4d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r5 = 0
            java.lang.CharSequence r0 = r0.subSequence(r5, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0b4e
        L_0x0b4d:
            r5 = 0
        L_0x0b4e:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r3, r1)
            goto L_0x0442
        L_0x0b5e:
            r2 = 1
            r5 = 0
            r0 = 2131626482(0x7f0e09f2, float:1.8880201E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0b71:
            r2 = 1
            r5 = 0
            r0 = 2131626467(0x7f0e09e3, float:1.8880171E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0b84:
            r5 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0ba0
            r1 = 2131626499(0x7f0e0a03, float:1.8880236E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r2[r5] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0442
        L_0x0ba0:
            r3 = 1
            r0 = 2131626497(0x7f0e0a01, float:1.8880232E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0bb2:
            r5 = 0
            org.telegram.messenger.MessageObject r3 = r0.replyMessageObject
            if (r3 != 0) goto L_0x0bcc
            r0 = 2131626481(0x7f0e09f1, float:1.88802E38)
            r4 = 2
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r5] = r1
            java.lang.String r1 = r6.title
            r8 = 1
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedNoText"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0bcc:
            r4 = 2
            r8 = 1
            boolean r9 = r3.isMusic()
            if (r9 == 0) goto L_0x0be7
            r0 = 2131626478(0x7f0e09ee, float:1.8880193E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r5] = r1
            java.lang.String r1 = r6.title
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0be7:
            boolean r4 = r3.isVideo()
            r5 = 2131626502(0x7f0e0a06, float:1.8880242E38)
            java.lang.String r8 = "NotificationActionPinnedText"
            if (r4 == 0) goto L_0x0c3e
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = r6.title
            r7 = 2
            r2[r7] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            goto L_0x0442
        L_0x0CLASSNAME:
            r3 = 0
            r4 = 1
            r7 = 2
            r0 = 2131626505(0x7f0e0a09, float:1.8880248E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0c3e:
            boolean r4 = r3.isGif()
            if (r4 == 0) goto L_0x0CLASSNAME
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x0c7a
            org.telegram.tgnet.TLRPC$Message r0 = r3.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0c7a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            r7 = 1
            r2[r7] = r0
            java.lang.String r0 = r6.title
            r9 = 2
            r2[r9] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            goto L_0x0442
        L_0x0c7a:
            r4 = 0
            r7 = 1
            r9 = 2
            r0 = 2131626472(0x7f0e09e8, float:1.8880181E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0CLASSNAME:
            r4 = 0
            r7 = 1
            r9 = 2
            boolean r11 = r3.isVoice()
            if (r11 == 0) goto L_0x0cac
            r0 = 2131626508(0x7f0e0a0c, float:1.8880254E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0cac:
            boolean r11 = r3.isRoundVideo()
            if (r11 == 0) goto L_0x0cc5
            r0 = 2131626493(0x7f0e09fd, float:1.8880224E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0cc5:
            boolean r4 = r3.isSticker()
            if (r4 != 0) goto L_0x0e87
            boolean r4 = r3.isAnimatedSticker()
            if (r4 == 0) goto L_0x0cd3
            goto L_0x0e87
        L_0x0cd3:
            org.telegram.tgnet.TLRPC$Message r4 = r3.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r4.media
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x0d25
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r0 < r7) goto L_0x0d0f
            java.lang.String r0 = r4.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0d0f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r10)
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = r6.title
            r7 = 2
            r2[r7] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            goto L_0x0442
        L_0x0d0f:
            r3 = 0
            r4 = 1
            r7 = 2
            r0 = 2131626457(0x7f0e09d9, float:1.888015E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0d25:
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r9 != 0) goto L_0x0e71
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r9 == 0) goto L_0x0d2f
            goto L_0x0e71
        L_0x0d2f:
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r9 == 0) goto L_0x0d49
            r0 = 2131626468(0x7f0e09e4, float:1.8880173E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0d49:
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r9 == 0) goto L_0x0d73
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r3 = 2131626454(0x7f0e09d6, float:1.8880145E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r1 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r1, r0)
            r1 = 2
            r2[r1] = r0
            java.lang.String r0 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r2)
            goto L_0x0442
        L_0x0d73:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0db3
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r3 = r0.quiz
            if (r3 == 0) goto L_0x0d99
            r3 = 2131626490(0x7f0e09fa, float:1.8880218E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r5 = 1
            r2[r5] = r1
            java.lang.String r0 = r0.question
            r7 = 2
            r2[r7] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r2)
            goto L_0x0442
        L_0x0d99:
            r4 = 0
            r5 = 1
            r7 = 2
            r3 = 2131626487(0x7f0e09f7, float:1.8880212E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r5] = r1
            java.lang.String r0 = r0.question
            r2[r7] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r2)
            goto L_0x0442
        L_0x0db3:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0e01
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r0 < r7) goto L_0x0deb
            java.lang.String r0 = r4.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0deb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            java.lang.String r3 = r3.message
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            r9 = 1
            r2[r9] = r0
            java.lang.String r0 = r6.title
            r10 = 2
            r2[r10] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            goto L_0x0442
        L_0x0deb:
            r4 = 0
            r9 = 1
            r10 = 2
            r0 = 2131626484(0x7f0e09f4, float:1.8880205E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r9] = r1
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0e01:
            r4 = 0
            r9 = 1
            r10 = 2
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0e1b
            r0 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r9] = r1
            java.lang.String r1 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0e1b:
            java.lang.CharSequence r0 = r3.messageText
            if (r0 == 0) goto L_0x0e5b
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0e5b
            java.lang.CharSequence r0 = r3.messageText
            int r3 = r0.length()
            r4 = 20
            if (r3 <= r4) goto L_0x0e48
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 20
            r7 = 0
            java.lang.CharSequence r0 = r0.subSequence(r7, r4)
            r3.append(r0)
            java.lang.String r0 = "..."
            r3.append(r0)
            java.lang.String r0 = r3.toString()
            goto L_0x0e49
        L_0x0e48:
            r7 = 0
        L_0x0e49:
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = r6.title
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            goto L_0x0442
        L_0x0e5b:
            r3 = 2
            r4 = 1
            r7 = 0
            r0 = 2131626481(0x7f0e09f1, float:1.88802E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0e71:
            r3 = 2
            r4 = 1
            r7 = 0
            r0 = 2131626466(0x7f0e09e2, float:1.888017E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0e87:
            r4 = 1
            r7 = 0
            java.lang.String r0 = r3.getStickerEmoji()
            if (r0 == 0) goto L_0x0ea5
            r3 = 2131626498(0x7f0e0a02, float:1.8880234E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            r5 = 2
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r2)
            goto L_0x0442
        L_0x0ea5:
            r5 = 2
            r0 = 2131626496(0x7f0e0a00, float:1.888023E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x0eb9:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 == 0) goto L_0x0ec5
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x158d
        L_0x0ec5:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r0 == 0) goto L_0x158c
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r3 = (org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r3
            java.lang.String r0 = r3.emoticon
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L_0x0ef9
            int r2 = (r23 > r17 ? 1 : (r23 == r17 ? 0 : -1))
            if (r2 != 0) goto L_0x0ee5
            r0 = 2131624865(0x7f0e03a1, float:1.8876922E38)
            r2 = 0
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = "ChatThemeDisabledYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0442
        L_0x0ee5:
            r2 = 0
            r3 = 2131624864(0x7f0e03a0, float:1.887692E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r2] = r1
            r5 = 1
            r4[r5] = r0
            java.lang.String r0 = "ChatThemeDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            goto L_0x0442
        L_0x0ef9:
            r2 = 0
            r5 = 1
            int r3 = (r23 > r17 ? 1 : (r23 == r17 ? 0 : -1))
            if (r3 != 0) goto L_0x0f0e
            r1 = 2131624863(0x7f0e039f, float:1.8876918E38)
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r2] = r0
            java.lang.String r0 = "ChangedChatThemeYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0442
        L_0x0f0e:
            r3 = 2131624862(0x7f0e039e, float:1.8876916E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r2] = r1
            r4[r5] = r0
            java.lang.String r0 = "ChangedChatThemeTo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            goto L_0x0442
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Peer r2 = r4.peer_id
            long r2 = r2.channel_id
            r4 = 0
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x0f5a
            boolean r2 = r6.megagroup
            if (r2 != 0) goto L_0x0f5a
            boolean r0 = r27.isVideoAvatar()
            if (r0 == 0) goto L_0x0var_
            r0 = 2131624815(0x7f0e036f, float:1.887682E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x158d
        L_0x0var_:
            r1 = 1
            r3 = 0
            r0 = 2131624780(0x7f0e034c, float:1.887675E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x158d
        L_0x0f5a:
            r3 = 0
            boolean r0 = r27.isVideoAvatar()
            if (r0 == 0) goto L_0x0var_
            r0 = 2131626515(0x7f0e0a13, float:1.8880268E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r1 = "NotificationEditedGroupVideo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0var_:
            r2 = 2
            r4 = 1
            r0 = 2131626514(0x7f0e0a12, float:1.8880266E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0f8b:
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r4 == 0) goto L_0x1243
            boolean r4 = r6.megagroup
            if (r4 != 0) goto L_0x1243
            boolean r2 = r27.isMediaEmpty()
            if (r2 == 0) goto L_0x0fd0
            if (r28 != 0) goto L_0x0fbf
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0fbf
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x0fbf:
            r4 = 0
            r5 = 1
            r0 = 2131624764(0x7f0e033c, float:1.8876717E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageNoText"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x0fd0:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x1020
            if (r28 != 0) goto L_0x100f
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 19
            if (r4 < r5) goto L_0x100f
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x100f
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x100f:
            r4 = 0
            r5 = 1
            r0 = 2131624765(0x7f0e033d, float:1.8876719E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessagePhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1020:
            boolean r2 = r27.isVideo()
            if (r2 == 0) goto L_0x1070
            if (r28 != 0) goto L_0x105f
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r2 < r4) goto L_0x105f
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x105f
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x105f:
            r4 = 0
            r5 = 1
            r0 = 2131624771(0x7f0e0343, float:1.8876731E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageVideo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1070:
            r4 = 0
            r5 = 1
            boolean r2 = r27.isVoice()
            if (r2 == 0) goto L_0x1087
            r0 = 2131624756(0x7f0e0334, float:1.88767E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageAudio"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1087:
            boolean r2 = r27.isRoundVideo()
            if (r2 == 0) goto L_0x109c
            r0 = 2131624768(0x7f0e0340, float:1.8876725E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageRound"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x109c:
            boolean r2 = r27.isMusic()
            if (r2 == 0) goto L_0x10b1
            r0 = 2131624763(0x7f0e033b, float:1.8876715E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageMusic"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x10b1:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r5 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r5 == 0) goto L_0x10d6
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r2
            r0 = 2131624757(0x7f0e0335, float:1.8876703E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            java.lang.String r1 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r2)
            r2 = 1
            r3[r2] = r1
            java.lang.String r1 = "ChannelMessageContact2"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x158d
        L_0x10d6:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x110e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x10f8
            r2 = 2131624767(0x7f0e033f, float:1.8876723E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0442
        L_0x10f8:
            r3 = 2
            r4 = 0
            r5 = 1
            r2 = 2131624766(0x7f0e033e, float:1.887672E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0442
        L_0x110e:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x1232
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x1118
            goto L_0x1232
        L_0x1118:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x112d
            r0 = 2131624761(0x7f0e0339, float:1.887671E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageLiveLocation"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x112d:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x1202
            boolean r2 = r27.isSticker()
            if (r2 != 0) goto L_0x11d9
            boolean r2 = r27.isAnimatedSticker()
            if (r2 == 0) goto L_0x113f
            goto L_0x11d9
        L_0x113f:
            boolean r2 = r27.isGif()
            if (r2 == 0) goto L_0x118f
            if (r28 != 0) goto L_0x117e
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r2 < r4) goto L_0x117e
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x117e
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x117e:
            r4 = 0
            r5 = 1
            r0 = 2131624760(0x7f0e0338, float:1.8876709E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageGIF"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x118f:
            if (r28 != 0) goto L_0x11c8
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r2 < r4) goto L_0x11c8
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x11c8
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x11c8:
            r4 = 0
            r5 = 1
            r0 = 2131624758(0x7f0e0336, float:1.8876705E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageDocument"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x11d9:
            r4 = 0
            r5 = 1
            java.lang.String r0 = r27.getStickerEmoji()
            if (r0 == 0) goto L_0x11f3
            r2 = 2131624770(0x7f0e0342, float:1.887673E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0442
        L_0x11f3:
            r0 = 2131624769(0x7f0e0341, float:1.8876727E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x1202:
            r4 = 0
            if (r28 != 0) goto L_0x1222
            java.lang.CharSequence r2 = r0.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1222
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r1
            java.lang.CharSequence r0 = r0.messageText
            r5 = 1
            r2[r5] = r0
            r0 = 2131626578(0x7f0e0a52, float:1.8880396E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r29[r4] = r5
            goto L_0x158d
        L_0x1222:
            r5 = 1
            r0 = 2131624764(0x7f0e033c, float:1.8876717E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageNoText"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1232:
            r4 = 0
            r5 = 1
            r0 = 2131624762(0x7f0e033a, float:1.8876713E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "ChannelMessageMap"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1243:
            boolean r3 = r27.isMediaEmpty()
            r4 = 2131626561(0x7f0e0a41, float:1.8880362E38)
            java.lang.String r5 = "NotificationMessageGroupText"
            if (r3 == 0) goto L_0x1287
            if (r28 != 0) goto L_0x1271
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x1271
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r3 = 2
            r2[r3] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r4, r2)
            goto L_0x158d
        L_0x1271:
            r3 = 2
            r0 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            r3 = r22
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x158d
        L_0x1287:
            r3 = r22
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r8.media
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r9 == 0) goto L_0x12dd
            if (r28 != 0) goto L_0x12c7
            int r3 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r3 < r7) goto L_0x12c7
            java.lang.String r3 = r8.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x12c7
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r3 = 2
            r2[r3] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r4, r2)
            goto L_0x158d
        L_0x12c7:
            r3 = 2
            r0 = 2131626555(0x7f0e0a3b, float:1.888035E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x12dd:
            boolean r8 = r27.isVideo()
            if (r8 == 0) goto L_0x1331
            if (r28 != 0) goto L_0x131b
            int r3 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r3 < r7) goto L_0x131b
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x131b
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r8 = 2
            r2[r8] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r4, r2)
            goto L_0x158d
        L_0x131b:
            r8 = 2
            r0 = 2131626562(0x7f0e0a42, float:1.8880364E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r9 = 0
            r2[r9] = r1
            java.lang.String r1 = r6.title
            r11 = 1
            r2[r11] = r1
            java.lang.String r1 = " "
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1331:
            r8 = 2
            r9 = 0
            r11 = 1
            boolean r12 = r27.isVoice()
            if (r12 == 0) goto L_0x134d
            r0 = 2131626544(0x7f0e0a30, float:1.8880327E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r9] = r1
            java.lang.String r1 = r6.title
            r2[r11] = r1
            java.lang.String r1 = "NotificationMessageGroupAudio"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x134d:
            boolean r12 = r27.isRoundVideo()
            if (r12 == 0) goto L_0x1366
            r0 = 2131626558(0x7f0e0a3e, float:1.8880356E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r9] = r1
            java.lang.String r1 = r6.title
            r2[r11] = r1
            java.lang.String r1 = "NotificationMessageGroupRound"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1366:
            boolean r12 = r27.isMusic()
            if (r12 == 0) goto L_0x137f
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r9] = r1
            java.lang.String r1 = r6.title
            r2[r11] = r1
            java.lang.String r1 = "NotificationMessageGroupMusic"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x137f:
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r9 == 0) goto L_0x13a9
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r8 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r8
            r0 = 2131626545(0x7f0e0a31, float:1.888033E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = r8.first_name
            java.lang.String r3 = r8.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r3)
            r3 = 2
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupContact2"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x13a9:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r9 == 0) goto L_0x13e9
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r8 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r8
            org.telegram.tgnet.TLRPC$Poll r0 = r8.poll
            boolean r3 = r0.quiz
            if (r3 == 0) goto L_0x13cf
            r3 = 2131626557(0x7f0e0a3d, float:1.8880354E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r5 = 1
            r2[r5] = r1
            java.lang.String r0 = r0.question
            r7 = 2
            r2[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r2)
            goto L_0x0442
        L_0x13cf:
            r4 = 0
            r5 = 1
            r7 = 2
            r3 = 2131626556(0x7f0e0a3c, float:1.8880352E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r5] = r1
            java.lang.String r0 = r0.question
            r2[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r2)
            goto L_0x0442
        L_0x13e9:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r9 == 0) goto L_0x1409
            r0 = 2131626547(0x7f0e0a33, float:1.8880333E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            org.telegram.tgnet.TLRPC$TL_game r1 = r8.game
            java.lang.String r1 = r1.title
            r3 = 2
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupGame"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1409:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r9 != 0) goto L_0x1544
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r9 == 0) goto L_0x1413
            goto L_0x1544
        L_0x1413:
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r9 == 0) goto L_0x142d
            r0 = 2131626551(0x7f0e0a37, float:1.8880341E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x142d:
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x1513
            boolean r3 = r27.isSticker()
            if (r3 != 0) goto L_0x14e1
            boolean r3 = r27.isAnimatedSticker()
            if (r3 == 0) goto L_0x143f
            goto L_0x14e1
        L_0x143f:
            boolean r3 = r27.isGif()
            if (r3 == 0) goto L_0x1493
            if (r28 != 0) goto L_0x147d
            int r3 = android.os.Build.VERSION.SDK_INT
            r8 = 19
            if (r3 < r8) goto L_0x147d
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x147d
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r3 = 2
            r2[r3] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r4, r2)
            goto L_0x158d
        L_0x147d:
            r3 = 2
            r0 = 2131626549(0x7f0e0a35, float:1.8880337E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupGif"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1493:
            if (r28 != 0) goto L_0x14cb
            int r3 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r3 < r7) goto L_0x14cb
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x14cb
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r3 = 2
            r2[r3] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r4, r2)
            goto L_0x158d
        L_0x14cb:
            r3 = 2
            r0 = 2131626546(0x7f0e0a32, float:1.8880331E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageGroupDocument"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x14e1:
            r3 = 0
            r4 = 1
            java.lang.String r0 = r27.getStickerEmoji()
            if (r0 == 0) goto L_0x14ff
            r5 = 2131626560(0x7f0e0a40, float:1.888036E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            r7 = 2
            r2[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r5, r2)
            goto L_0x0442
        L_0x14ff:
            r7 = 2
            r0 = 2131626559(0x7f0e0a3f, float:1.8880358E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x0442
        L_0x1513:
            if (r28 != 0) goto L_0x1531
            java.lang.CharSequence r7 = r0.messageText
            boolean r7 = android.text.TextUtils.isEmpty(r7)
            if (r7 != 0) goto L_0x1531
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r7 = 0
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r8 = 1
            r2[r8] = r1
            java.lang.CharSequence r0 = r0.messageText
            r9 = 2
            r2[r9] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r4, r2)
            goto L_0x158d
        L_0x1531:
            r7 = 0
            r8 = 1
            r9 = 2
            r0 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r8] = r1
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            goto L_0x158d
        L_0x1544:
            r7 = 0
            r8 = 1
            r9 = 2
            r0 = 2131626552(0x7f0e0a38, float:1.8880343E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r8] = r1
            java.lang.String r1 = "NotificationMessageGroupMap"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1559:
            r3 = r22
            r7 = 0
            if (r30 == 0) goto L_0x1560
            r30[r7] = r7
        L_0x1560:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r0 == 0) goto L_0x1579
            boolean r0 = r6.megagroup
            if (r0 != 0) goto L_0x1579
            r0 = 2131624764(0x7f0e033c, float:1.8876717E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r1
            java.lang.String r1 = "ChannelMessageNoText"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x158d
        L_0x1579:
            r2 = 1
            r0 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r7] = r1
            java.lang.String r1 = r6.title
            r4[r2] = r1
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r3, r0, r4)
            goto L_0x158d
        L_0x158b:
            r5 = 0
        L_0x158c:
            r13 = r5
        L_0x158d:
            return r13
        L_0x158e:
            r0 = 2131628565(0x7f0e1215, float:1.8884426E38)
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
            if (i <= 0 || this.personalCount <= 0) {
                this.alarmManager.cancel(service);
            } else {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) (i * 60 * 1000)), service);
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0014, code lost:
        r7 = r7.action;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isPersonalMessage(org.telegram.messenger.MessageObject r7) {
        /*
            r6 = this;
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r7.peer_id
            if (r0 == 0) goto L_0x001e
            long r1 = r0.chat_id
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x001e
            long r0 = r0.channel_id
            int r2 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r2 != 0) goto L_0x001e
            org.telegram.tgnet.TLRPC$MessageAction r7 = r7.action
            if (r7 == 0) goto L_0x001c
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r7 == 0) goto L_0x001e
        L_0x001c:
            r7 = 1
            goto L_0x001f
        L_0x001e:
            r7 = 0
        L_0x001f:
            return r7
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
    public /* synthetic */ void lambda$showNotifications$25() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda14(this));
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda12(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hideNotifications$26() {
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
            AndroidUtilities.runOnUIThread(NotificationsController$$ExternalSyntheticLambda38.INSTANCE);
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
                    notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda5(this));
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$playInChatSound$29() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundPlay) > 500) {
            try {
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda3.INSTANCE);
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$playInChatSound$28(SoundPool soundPool2, int i, int i2) {
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
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda7(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$repeatNotificationMaybe$30() {
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
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda23(this, j, i));
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
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda21(this, i, i2));
        }
    }

    public void deleteAllNotificationChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda10(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteAllNotificationChannels$33() {
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

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00ef A[Catch:{ Exception -> 0x0150 }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00f3 A[Catch:{ Exception -> 0x0150 }] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0100 A[Catch:{ Exception -> 0x0150 }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0105 A[Catch:{ Exception -> 0x0150 }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0136 A[Catch:{ Exception -> 0x0150 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0138 A[Catch:{ Exception -> 0x0150 }] */
    @android.annotation.SuppressLint({"RestrictedApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String createNotificationShortcut(androidx.core.app.NotificationCompat.Builder r18, long r19, java.lang.String r21, org.telegram.tgnet.TLRPC$User r22, org.telegram.tgnet.TLRPC$Chat r23, androidx.core.app.Person r24) {
        /*
            r17 = this;
            r1 = r17
            r0 = r18
            r2 = r19
            r4 = r22
            r5 = r23
            r6 = r24
            java.lang.String r7 = "com.tmessages.openchat"
            boolean r8 = r17.unsupportedNotificationShortcut()
            if (r8 != 0) goto L_0x0156
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r23)
            if (r8 == 0) goto L_0x0020
            boolean r8 = r5.megagroup
            if (r8 != 0) goto L_0x0020
            goto L_0x0156
        L_0x0020:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0150 }
            r8.<init>()     // Catch:{ Exception -> 0x0150 }
            java.lang.String r10 = "ndid_"
            r8.append(r10)     // Catch:{ Exception -> 0x0150 }
            r8.append(r2)     // Catch:{ Exception -> 0x0150 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0150 }
            android.content.Intent r10 = new android.content.Intent     // Catch:{ Exception -> 0x0150 }
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0150 }
            java.lang.Class<org.telegram.messenger.OpenChatReceiver> r12 = org.telegram.messenger.OpenChatReceiver.class
            r10.<init>(r11, r12)     // Catch:{ Exception -> 0x0150 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0150 }
            r11.<init>()     // Catch:{ Exception -> 0x0150 }
            r11.append(r7)     // Catch:{ Exception -> 0x0150 }
            double r12 = java.lang.Math.random()     // Catch:{ Exception -> 0x0150 }
            r11.append(r12)     // Catch:{ Exception -> 0x0150 }
            r12 = 2147483647(0x7fffffff, float:NaN)
            r11.append(r12)     // Catch:{ Exception -> 0x0150 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0150 }
            r10.setAction(r11)     // Catch:{ Exception -> 0x0150 }
            r13 = 0
            java.lang.String r11 = "userId"
            java.lang.String r15 = "chatId"
            int r16 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r16 <= 0) goto L_0x0064
            r10.putExtra(r11, r2)     // Catch:{ Exception -> 0x0150 }
            goto L_0x0068
        L_0x0064:
            long r13 = -r2
            r10.putExtra(r15, r13)     // Catch:{ Exception -> 0x0150 }
        L_0x0068:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r13 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ Exception -> 0x0150 }
            android.content.Context r14 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0150 }
            r13.<init>((android.content.Context) r14, (java.lang.String) r8)     // Catch:{ Exception -> 0x0150 }
            if (r5 == 0) goto L_0x0074
            r5 = r21
            goto L_0x0078
        L_0x0074:
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r22)     // Catch:{ Exception -> 0x0150 }
        L_0x0078:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r13.setShortLabel(r5)     // Catch:{ Exception -> 0x0150 }
            r13 = r21
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setLongLabel(r13)     // Catch:{ Exception -> 0x0150 }
            android.content.Intent r13 = new android.content.Intent     // Catch:{ Exception -> 0x0150 }
            java.lang.String r14 = "android.intent.action.VIEW"
            r13.<init>(r14)     // Catch:{ Exception -> 0x0150 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setIntent(r13)     // Catch:{ Exception -> 0x0150 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setIntent(r10)     // Catch:{ Exception -> 0x0150 }
            r10 = 1
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setLongLived(r10)     // Catch:{ Exception -> 0x0150 }
            androidx.core.content.LocusIdCompat r13 = new androidx.core.content.LocusIdCompat     // Catch:{ Exception -> 0x0150 }
            r13.<init>(r8)     // Catch:{ Exception -> 0x0150 }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setLocusId(r13)     // Catch:{ Exception -> 0x0150 }
            if (r6 == 0) goto L_0x00ba
            r5.setPerson(r6)     // Catch:{ Exception -> 0x0150 }
            androidx.core.graphics.drawable.IconCompat r13 = r24.getIcon()     // Catch:{ Exception -> 0x0150 }
            r5.setIcon(r13)     // Catch:{ Exception -> 0x0150 }
            androidx.core.graphics.drawable.IconCompat r13 = r24.getIcon()     // Catch:{ Exception -> 0x0150 }
            if (r13 == 0) goto L_0x00ba
            androidx.core.graphics.drawable.IconCompat r6 = r24.getIcon()     // Catch:{ Exception -> 0x0150 }
            android.graphics.Bitmap r6 = r6.getBitmap()     // Catch:{ Exception -> 0x0150 }
            goto L_0x00bb
        L_0x00ba:
            r6 = 0
        L_0x00bb:
            androidx.core.content.pm.ShortcutInfoCompat r5 = r5.build()     // Catch:{ Exception -> 0x0150 }
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0150 }
            androidx.core.content.pm.ShortcutManagerCompat.pushDynamicShortcut(r13, r5)     // Catch:{ Exception -> 0x0150 }
            r0.setShortcutInfo(r5)     // Catch:{ Exception -> 0x0150 }
            android.content.Intent r5 = new android.content.Intent     // Catch:{ Exception -> 0x0150 }
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0150 }
            java.lang.Class<org.telegram.ui.BubbleActivity> r14 = org.telegram.ui.BubbleActivity.class
            r5.<init>(r13, r14)     // Catch:{ Exception -> 0x0150 }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0150 }
            r13.<init>()     // Catch:{ Exception -> 0x0150 }
            r13.append(r7)     // Catch:{ Exception -> 0x0150 }
            double r9 = java.lang.Math.random()     // Catch:{ Exception -> 0x0150 }
            r13.append(r9)     // Catch:{ Exception -> 0x0150 }
            r13.append(r12)     // Catch:{ Exception -> 0x0150 }
            java.lang.String r9 = r13.toString()     // Catch:{ Exception -> 0x0150 }
            r5.setAction(r9)     // Catch:{ Exception -> 0x0150 }
            boolean r9 = org.telegram.messenger.DialogObject.isUserDialog(r19)     // Catch:{ Exception -> 0x0150 }
            if (r9 == 0) goto L_0x00f3
            r5.putExtra(r11, r2)     // Catch:{ Exception -> 0x0150 }
            goto L_0x00f7
        L_0x00f3:
            long r9 = -r2
            r5.putExtra(r15, r9)     // Catch:{ Exception -> 0x0150 }
        L_0x00f7:
            java.lang.String r9 = "currentAccount"
            int r10 = r1.currentAccount     // Catch:{ Exception -> 0x0150 }
            r5.putExtra(r9, r10)     // Catch:{ Exception -> 0x0150 }
            if (r6 == 0) goto L_0x0105
            androidx.core.graphics.drawable.IconCompat r4 = androidx.core.graphics.drawable.IconCompat.createWithAdaptiveBitmap(r6)     // Catch:{ Exception -> 0x0150 }
            goto L_0x0122
        L_0x0105:
            if (r4 == 0) goto L_0x0119
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0150 }
            boolean r4 = r4.bot     // Catch:{ Exception -> 0x0150 }
            if (r4 == 0) goto L_0x0111
            r4 = 2131165288(0x7var_, float:1.7944789E38)
            goto L_0x0114
        L_0x0111:
            r4 = 2131165292(0x7var_c, float:1.7944797E38)
        L_0x0114:
            androidx.core.graphics.drawable.IconCompat r4 = androidx.core.graphics.drawable.IconCompat.createWithResource(r6, r4)     // Catch:{ Exception -> 0x0150 }
            goto L_0x0122
        L_0x0119:
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0150 }
            r6 = 2131165290(0x7var_a, float:1.7944793E38)
            androidx.core.graphics.drawable.IconCompat r4 = androidx.core.graphics.drawable.IconCompat.createWithResource(r4, r6)     // Catch:{ Exception -> 0x0150 }
        L_0x0122:
            androidx.core.app.NotificationCompat$BubbleMetadata$Builder r6 = new androidx.core.app.NotificationCompat$BubbleMetadata$Builder     // Catch:{ Exception -> 0x0150 }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0150 }
            r10 = 134217728(0x8000000, float:3.85186E-34)
            r11 = 0
            android.app.PendingIntent r5 = android.app.PendingIntent.getActivity(r9, r11, r5, r10)     // Catch:{ Exception -> 0x0150 }
            r6.<init>(r5, r4)     // Catch:{ Exception -> 0x0150 }
            long r4 = r1.openedDialogId     // Catch:{ Exception -> 0x0150 }
            int r9 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r9 != 0) goto L_0x0138
            r10 = 1
            goto L_0x0139
        L_0x0138:
            r10 = 0
        L_0x0139:
            r6.setSuppressNotification(r10)     // Catch:{ Exception -> 0x0150 }
            r6.setAutoExpandBubble(r11)     // Catch:{ Exception -> 0x0150 }
            r2 = 1142947840(0x44200000, float:640.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x0150 }
            r6.setDesiredHeight(r2)     // Catch:{ Exception -> 0x0150 }
            androidx.core.app.NotificationCompat$BubbleMetadata r2 = r6.build()     // Catch:{ Exception -> 0x0150 }
            r0.setBubbleMetadata(r2)     // Catch:{ Exception -> 0x0150 }
            return r8
        L_0x0150:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r2 = 0
            return r2
        L_0x0156:
            r2 = 0
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.createNotificationShortcut(androidx.core.app.NotificationCompat$Builder, long, java.lang.String, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, androidx.core.app.Person):java.lang.String");
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
            notificationsSettings.edit().putBoolean("groupsCreated4", true).commit();
            this.groupsCreated = Boolean.TRUE;
        }
        if (!this.channelGroupsCreated) {
            List<NotificationChannelGroup> notificationChannelGroups = systemNotificationManager.getNotificationChannelGroups();
            String str3 = "channels" + this.currentAccount;
            String str4 = "groups" + this.currentAccount;
            int size2 = notificationChannelGroups.size();
            String str5 = "other" + this.currentAccount;
            String str6 = "private" + this.currentAccount;
            for (int i2 = 0; i2 < size2; i2++) {
                String id2 = notificationChannelGroups.get(i2).getId();
                if (str3 != null && str3.equals(id2)) {
                    str3 = null;
                } else if (str4 != null && str4.equals(id2)) {
                    str4 = null;
                } else if (str6 != null && str6.equals(id2)) {
                    str6 = null;
                } else if (str5 != null && str5.equals(id2)) {
                    str5 = null;
                }
                if (str3 == null && str4 == null && str6 == null && str5 == null) {
                    break;
                }
            }
            if (!(str3 == null && str4 == null && str6 == null && str5 == null)) {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
                if (user == null) {
                    getUserConfig().getCurrentUser();
                }
                if (user != null) {
                    str = " (" + ContactsController.formatName(user.first_name, user.last_name) + ")";
                } else {
                    str = "";
                }
                ArrayList arrayList = new ArrayList();
                if (str3 != null) {
                    arrayList.add(new NotificationChannelGroup(str3, LocaleController.getString("NotificationsChannels", NUM) + str));
                }
                if (str4 != null) {
                    arrayList.add(new NotificationChannelGroup(str4, LocaleController.getString("NotificationsGroups", NUM) + str));
                }
                if (str6 != null) {
                    arrayList.add(new NotificationChannelGroup(str6, LocaleController.getString("NotificationsPrivateChats", NUM) + str));
                }
                if (str5 != null) {
                    arrayList.add(new NotificationChannelGroup(str5, LocaleController.getString("NotificationsOther", NUM) + str));
                }
                systemNotificationManager.createNotificationChannelGroups(arrayList);
            }
            this.channelGroupsCreated = true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:166:0x03a8  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x03f5  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x03fb  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0434  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x04f1 A[LOOP:1: B:225:0x04ee->B:227:0x04f1, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0504  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x0552  */
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
            if (r36 != 0) goto L_0x007c
            boolean r15 = org.telegram.messenger.DialogObject.isEncryptedDialog(r29)
            if (r15 == 0) goto L_0x007c
            r15 = 1
            goto L_0x007d
        L_0x007c:
            r15 = 0
        L_0x007d:
            if (r37 != 0) goto L_0x0089
            if (r12 == 0) goto L_0x0089
            boolean r12 = r5.getBoolean(r12, r14)
            if (r12 == 0) goto L_0x0089
            r12 = 1
            goto L_0x008a
        L_0x0089:
            r12 = 0
        L_0x008a:
            if (r38 == 0) goto L_0x0099
            r6 = 2131626628(0x7f0e0a84, float:1.8880498E38)
            java.lang.String r7 = "NotificationsSilent"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.String r7 = "silent"
        L_0x0097:
            r8 = 0
            goto L_0x00ec
        L_0x0099:
            if (r36 == 0) goto L_0x00c2
            if (r37 == 0) goto L_0x00a3
            r9 = 2131626606(0x7f0e0a6e, float:1.8880453E38)
            java.lang.String r14 = "NotificationsInAppDefault"
            goto L_0x00a8
        L_0x00a3:
            r9 = 2131626590(0x7f0e0a5e, float:1.888042E38)
            java.lang.String r14 = "NotificationsDefault"
        L_0x00a8:
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r14, r9)
            if (r0 != r10) goto L_0x00b5
            if (r37 == 0) goto L_0x00b2
            java.lang.String r8 = "channels_ia"
        L_0x00b2:
            r7 = r8
        L_0x00b3:
            r6 = r9
            goto L_0x0097
        L_0x00b5:
            if (r0 != 0) goto L_0x00bd
            if (r37 == 0) goto L_0x00bb
            java.lang.String r6 = "groups_ia"
        L_0x00bb:
            r7 = r6
            goto L_0x00b3
        L_0x00bd:
            if (r37 == 0) goto L_0x00b3
            java.lang.String r7 = "private_ia"
            goto L_0x00b3
        L_0x00c2:
            if (r37 == 0) goto L_0x00d3
            r6 = 2131626587(0x7f0e0a5b, float:1.8880414E38)
            java.lang.Object[] r7 = new java.lang.Object[r13]
            r8 = 0
            r7[r8] = r31
            java.lang.String r8 = "NotificationsChatInApp"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r8, r6, r7)
            goto L_0x00d5
        L_0x00d3:
            r6 = r31
        L_0x00d5:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            if (r37 == 0) goto L_0x00df
            java.lang.String r8 = "org.telegram.keyia"
            goto L_0x00e1
        L_0x00df:
            java.lang.String r8 = "org.telegram.key"
        L_0x00e1:
            r7.append(r8)
            r7.append(r2)
            java.lang.String r7 = r7.toString()
            goto L_0x0097
        L_0x00ec:
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
            if (r9 == 0) goto L_0x047f
            android.app.NotificationManager r6 = systemNotificationManager
            android.app.NotificationChannel r6 = r6.getNotificationChannel(r9)
            boolean r16 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r17 = r11
            java.lang.String r11 = " = "
            if (r16 == 0) goto L_0x013d
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
            goto L_0x0141
        L_0x013d:
            r18 = r7
            r16 = r13
        L_0x0141:
            if (r6 == 0) goto L_0x046c
            if (r38 != 0) goto L_0x045b
            if (r12 != 0) goto L_0x045b
            int r7 = r6.getImportance()
            android.net.Uri r13 = r6.getSound()
            long[] r19 = r6.getVibrationPattern()
            r20 = r12
            boolean r12 = r6.shouldVibrate()
            if (r12 != 0) goto L_0x0166
            if (r19 != 0) goto L_0x0166
            r21 = r12
            r12 = 2
            long[] r4 = new long[r12]
            r4 = {0, 0} // fill-array
            goto L_0x016a
        L_0x0166:
            r21 = r12
            r4 = r19
        L_0x016a:
            int r6 = r6.getLightColor()
            if (r4 == 0) goto L_0x017c
            r12 = 0
        L_0x0171:
            int r2 = r4.length
            if (r12 >= r2) goto L_0x017c
            r2 = r4[r12]
            r8.append(r2)
            int r12 = r12 + 1
            goto L_0x0171
        L_0x017c:
            r8.append(r6)
            if (r13 == 0) goto L_0x0188
            java.lang.String r2 = r13.toString()
            r8.append(r2)
        L_0x0188:
            r8.append(r7)
            if (r36 != 0) goto L_0x0192
            if (r15 == 0) goto L_0x0192
            r8.append(r10)
        L_0x0192:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x01b8
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
        L_0x01b8:
            java.lang.String r2 = r8.toString()
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r3 = 0
            r8.setLength(r3)
            boolean r3 = r2.equals(r14)
            if (r3 != 0) goto L_0x0438
            java.lang.String r3 = "notify2_"
            if (r7 != 0) goto L_0x020e
            android.content.SharedPreferences$Editor r7 = r5.edit()
            if (r36 == 0) goto L_0x01ea
            if (r37 != 0) goto L_0x01e3
            java.lang.String r3 = getGlobalNotificationsKey(r39)
            r11 = 2147483647(0x7fffffff, float:NaN)
            r7.putInt(r3, r11)
            r1.updateServerNotificationsSettings((int) r0)
        L_0x01e3:
            r12 = r9
            r19 = r10
            r11 = 1
            r9 = r29
            goto L_0x0206
        L_0x01ea:
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
        L_0x0206:
            r11 = r35
            r22 = r2
            r23 = r4
            goto L_0x029c
        L_0x020e:
            r11 = r35
            r12 = r9
            r19 = r10
            r9 = r29
            r22 = r2
            if (r7 == r11) goto L_0x029e
            if (r37 != 0) goto L_0x0298
            android.content.SharedPreferences$Editor r2 = r5.edit()
            r23 = r4
            r4 = 4
            if (r7 == r4) goto L_0x0235
            r4 = 5
            if (r7 != r4) goto L_0x0228
            goto L_0x0235
        L_0x0228:
            r4 = 1
            if (r7 != r4) goto L_0x022e
            r4 = 2
            r7 = 4
            goto L_0x0237
        L_0x022e:
            r4 = 2
            if (r7 != r4) goto L_0x0233
            r7 = 5
            goto L_0x0237
        L_0x0233:
            r7 = 0
            goto L_0x0237
        L_0x0235:
            r4 = 2
            r7 = 1
        L_0x0237:
            if (r36 == 0) goto L_0x025c
            java.lang.String r3 = getGlobalNotificationsKey(r39)
            r4 = 0
            android.content.SharedPreferences$Editor r3 = r2.putInt(r3, r4)
            r3.commit()
            r3 = 2
            if (r0 != r3) goto L_0x024e
            java.lang.String r3 = "priority_channel"
            r2.putInt(r3, r7)
            goto L_0x029b
        L_0x024e:
            if (r0 != 0) goto L_0x0256
            java.lang.String r3 = "priority_group"
            r2.putInt(r3, r7)
            goto L_0x029b
        L_0x0256:
            java.lang.String r3 = "priority_messages"
            r2.putInt(r3, r7)
            goto L_0x029b
        L_0x025c:
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
            goto L_0x029b
        L_0x0298:
            r23 = r4
            r2 = 0
        L_0x029b:
            r7 = r2
        L_0x029c:
            r2 = 1
            goto L_0x02a2
        L_0x029e:
            r23 = r4
            r2 = 0
            r7 = 0
        L_0x02a2:
            if (r13 != 0) goto L_0x02a6
            if (r34 != 0) goto L_0x02b8
        L_0x02a6:
            if (r13 == 0) goto L_0x0394
            if (r34 == 0) goto L_0x02b8
            java.lang.String r3 = r13.toString()
            java.lang.String r4 = r34.toString()
            boolean r3 = android.text.TextUtils.equals(r3, r4)
            if (r3 != 0) goto L_0x0394
        L_0x02b8:
            if (r37 != 0) goto L_0x0388
            if (r7 != 0) goto L_0x02c0
            android.content.SharedPreferences$Editor r7 = r5.edit()
        L_0x02c0:
            java.lang.String r2 = "GroupSound"
            java.lang.String r3 = "GlobalSound"
            java.lang.String r4 = "ChannelSound"
            r24 = r12
            java.lang.String r12 = "sound_"
            r25 = r14
            java.lang.String r14 = "NoSound"
            if (r13 != 0) goto L_0x02fc
            if (r36 == 0) goto L_0x02e5
            r26 = r15
            r15 = 2
            if (r0 != r15) goto L_0x02db
            r7.putString(r4, r14)
            goto L_0x02f9
        L_0x02db:
            if (r0 != 0) goto L_0x02e1
            r7.putString(r2, r14)
            goto L_0x02f9
        L_0x02e1:
            r7.putString(r3, r14)
            goto L_0x02f9
        L_0x02e5:
            r26 = r15
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r12)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r7.putString(r2, r14)
        L_0x02f9:
            r27 = r13
            goto L_0x035a
        L_0x02fc:
            r26 = r15
            java.lang.String r14 = r13.toString()
            android.content.Context r15 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.media.Ringtone r15 = android.media.RingtoneManager.getRingtone(r15, r13)
            if (r15 == 0) goto L_0x032c
            r34 = r14
            android.net.Uri r14 = android.provider.Settings.System.DEFAULT_RINGTONE_URI
            boolean r14 = r13.equals(r14)
            if (r14 == 0) goto L_0x0320
            r14 = 2131625148(0x7f0e04bc, float:1.8877496E38)
            r27 = r13
            java.lang.String r13 = "DefaultRingtone"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r14)
            goto L_0x0328
        L_0x0320:
            r27 = r13
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r13 = r15.getTitle(r13)
        L_0x0328:
            r15.stop()
            goto L_0x0331
        L_0x032c:
            r27 = r13
            r34 = r14
            r13 = 0
        L_0x0331:
            if (r13 == 0) goto L_0x0358
            if (r36 == 0) goto L_0x0346
            r14 = 2
            if (r0 != r14) goto L_0x033c
            r7.putString(r4, r13)
            goto L_0x0358
        L_0x033c:
            if (r0 != 0) goto L_0x0342
            r7.putString(r2, r13)
            goto L_0x0358
        L_0x0342:
            r7.putString(r3, r13)
            goto L_0x0358
        L_0x0346:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r12)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r7.putString(r2, r13)
        L_0x0358:
            r14 = r34
        L_0x035a:
            if (r36 == 0) goto L_0x0373
            r2 = 2
            if (r0 != r2) goto L_0x0365
            java.lang.String r2 = "ChannelSoundPath"
            r7.putString(r2, r14)
            goto L_0x0390
        L_0x0365:
            if (r0 != 0) goto L_0x036d
            java.lang.String r2 = "GroupSoundPath"
            r7.putString(r2, r14)
            goto L_0x0390
        L_0x036d:
            java.lang.String r2 = "GlobalSoundPath"
            r7.putString(r2, r14)
            goto L_0x0390
        L_0x0373:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "sound_path_"
            r2.append(r3)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            r7.putString(r2, r14)
            goto L_0x0390
        L_0x0388:
            r24 = r12
            r27 = r13
            r25 = r14
            r26 = r15
        L_0x0390:
            r3 = r32
            r2 = 1
            goto L_0x039e
        L_0x0394:
            r24 = r12
            r25 = r14
            r26 = r15
            r3 = r32
            r27 = r34
        L_0x039e:
            boolean r4 = r1.isEmptyVibration(r3)
            r12 = 1
            r4 = r4 ^ r12
            r12 = r21
            if (r4 == r12) goto L_0x03f5
            if (r37 != 0) goto L_0x03f1
            if (r7 != 0) goto L_0x03b0
            android.content.SharedPreferences$Editor r7 = r5.edit()
        L_0x03b0:
            if (r36 == 0) goto L_0x03d8
            r2 = 2
            if (r0 != r2) goto L_0x03c0
            if (r12 == 0) goto L_0x03b9
            r2 = 0
            goto L_0x03ba
        L_0x03b9:
            r2 = 2
        L_0x03ba:
            java.lang.String r3 = "vibrate_channel"
            r7.putInt(r3, r2)
            goto L_0x03f1
        L_0x03c0:
            if (r0 != 0) goto L_0x03cd
            if (r12 == 0) goto L_0x03c6
            r2 = 0
            goto L_0x03c7
        L_0x03c6:
            r2 = 2
        L_0x03c7:
            java.lang.String r3 = "vibrate_group"
            r7.putInt(r3, r2)
            goto L_0x03f1
        L_0x03cd:
            if (r12 == 0) goto L_0x03d1
            r2 = 0
            goto L_0x03d2
        L_0x03d1:
            r2 = 2
        L_0x03d2:
            java.lang.String r3 = "vibrate_messages"
            r7.putInt(r3, r2)
            goto L_0x03f1
        L_0x03d8:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "vibrate_"
            r2.append(r3)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            if (r12 == 0) goto L_0x03ed
            r3 = 0
            goto L_0x03ee
        L_0x03ed:
            r3 = 2
        L_0x03ee:
            r7.putInt(r2, r3)
        L_0x03f1:
            r4 = r33
            r2 = 1
            goto L_0x03f9
        L_0x03f5:
            r4 = r33
            r23 = r3
        L_0x03f9:
            if (r6 == r4) goto L_0x0432
            if (r37 != 0) goto L_0x0430
            if (r7 != 0) goto L_0x0403
            android.content.SharedPreferences$Editor r7 = r5.edit()
        L_0x0403:
            if (r36 == 0) goto L_0x041c
            r2 = 2
            if (r0 != r2) goto L_0x040e
            java.lang.String r0 = "ChannelLed"
            r7.putInt(r0, r6)
            goto L_0x0430
        L_0x040e:
            if (r0 != 0) goto L_0x0416
            java.lang.String r0 = "GroupLed"
            r7.putInt(r0, r6)
            goto L_0x0430
        L_0x0416:
            java.lang.String r0 = "MessagesLed"
            r7.putInt(r0, r6)
            goto L_0x0430
        L_0x041c:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "color_"
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            r7.putInt(r0, r6)
        L_0x0430:
            r4 = r6
            r2 = 1
        L_0x0432:
            if (r7 == 0) goto L_0x044f
            r7.commit()
            goto L_0x044f
        L_0x0438:
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
        L_0x044f:
            r0 = r2
            r6 = r22
            r3 = r23
            r7 = r24
            r12 = r25
            r2 = r27
            goto L_0x049d
        L_0x045b:
            r11 = r4
            r24 = r9
            r19 = r10
            r20 = r12
            r25 = r14
            r26 = r15
            r4 = r33
            r9 = r2
            r3 = r32
            goto L_0x0495
        L_0x046c:
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
            goto L_0x049d
        L_0x047f:
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
        L_0x0495:
            r2 = r34
            r7 = r24
            r12 = r25
            r0 = 0
            r6 = 0
        L_0x049d:
            if (r0 == 0) goto L_0x04dc
            if (r6 == 0) goto L_0x04dc
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
            if (r0 == 0) goto L_0x04e9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "change edited channel "
            r0.append(r8)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x04e9
        L_0x04dc:
            r14 = r16
            r13 = r18
            if (r20 != 0) goto L_0x04ed
            if (r6 == 0) goto L_0x04ed
            if (r37 == 0) goto L_0x04ed
            if (r36 != 0) goto L_0x04e9
            goto L_0x04ed
        L_0x04e9:
            r8 = r7
            r16 = r14
            goto L_0x0550
        L_0x04ed:
            r0 = 0
        L_0x04ee:
            int r6 = r3.length
            if (r0 >= r6) goto L_0x04fd
            r16 = r14
            r14 = r3[r0]
            r8.append(r14)
            int r0 = r0 + 1
            r14 = r16
            goto L_0x04ee
        L_0x04fd:
            r16 = r14
            r8.append(r4)
            if (r2 == 0) goto L_0x050b
            java.lang.String r0 = r2.toString()
            r8.append(r0)
        L_0x050b:
            r8.append(r11)
            if (r36 != 0) goto L_0x0517
            if (r26 == 0) goto L_0x0517
            r0 = r19
            r8.append(r0)
        L_0x0517:
            java.lang.String r0 = r8.toString()
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r0)
            if (r38 != 0) goto L_0x054f
            if (r7 == 0) goto L_0x054f
            if (r20 != 0) goto L_0x052b
            boolean r0 = r12.equals(r6)
            if (r0 != 0) goto L_0x054f
        L_0x052b:
            android.app.NotificationManager r0 = systemNotificationManager     // Catch:{ Exception -> 0x0531 }
            r0.deleteNotificationChannel(r7)     // Catch:{ Exception -> 0x0531 }
            goto L_0x0535
        L_0x0531:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0535:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x054d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "delete channel by settings change "
            r0.append(r8)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x054d:
            r8 = 0
            goto L_0x0550
        L_0x054f:
            r8 = r7
        L_0x0550:
            if (r8 != 0) goto L_0x0637
            java.lang.String r0 = "_"
            java.lang.String r7 = "channel_"
            if (r36 == 0) goto L_0x0579
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
            goto L_0x0599
        L_0x0579:
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
        L_0x0599:
            r8 = r0
            android.app.NotificationChannel r0 = new android.app.NotificationChannel
            if (r26 == 0) goto L_0x05a8
            r7 = 2131627537(0x7f0e0e11, float:1.8882341E38)
            java.lang.String r9 = "SecretChatName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            goto L_0x05aa
        L_0x05a8:
            r7 = r31
        L_0x05aa:
            r0.<init>(r8, r7, r11)
            r11 = r17
            r0.setGroup(r11)
            if (r4 == 0) goto L_0x05bd
            r7 = 1
            r0.enableLights(r7)
            r0.setLightColor(r4)
            r4 = 0
            goto L_0x05c2
        L_0x05bd:
            r4 = 0
            r7 = 1
            r0.enableLights(r4)
        L_0x05c2:
            boolean r9 = r1.isEmptyVibration(r3)
            if (r9 != 0) goto L_0x05d2
            r0.enableVibration(r7)
            int r4 = r3.length
            if (r4 <= 0) goto L_0x05d5
            r0.setVibrationPattern(r3)
            goto L_0x05d5
        L_0x05d2:
            r0.enableVibration(r4)
        L_0x05d5:
            android.media.AudioAttributes$Builder r3 = new android.media.AudioAttributes$Builder
            r3.<init>()
            r4 = 4
            r3.setContentType(r4)
            r4 = 5
            r3.setUsage(r4)
            if (r2 == 0) goto L_0x05ec
            android.media.AudioAttributes r3 = r3.build()
            r0.setSound(r2, r3)
            goto L_0x05f4
        L_0x05ec:
            android.media.AudioAttributes r2 = r3.build()
            r3 = 0
            r0.setSound(r3, r2)
        L_0x05f4:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x060c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "create new channel "
            r2.append(r3)
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x060c:
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
        L_0x0637:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARNING: type inference failed for: r6v46 */
    /* JADX WARNING: type inference failed for: r6v47 */
    /* JADX WARNING: type inference failed for: r6v54 */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x07e0, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x07e2;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:378:0x088d */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01ca A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0217 A[SYNTHETIC, Splitter:B:105:0x0217] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x028d A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0347 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x0429 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x042c A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0445 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04ba A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x04c4 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x051c A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x056b A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x056f A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0579 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x057c A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x0581 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x059e A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x05d9 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x060f A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x068e A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x074c A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x0796  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x07a2  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x07da A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x07e9 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x089f A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x08a9 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x08ba A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x092c A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x09d6 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x0a02 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0a1b A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0119 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012b A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x012f A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0149 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0160 A[SYNTHETIC, Splitter:B:84:0x0160] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0193 A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x019f A[Catch:{ Exception -> 0x0a4d }] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01b4 A[SYNTHETIC, Splitter:B:99:0x01b4] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r46) {
        /*
            r45 = this;
            r15 = r45
            java.lang.String r1 = "currentAccount"
            org.telegram.messenger.UserConfig r2 = r45.getUserConfig()
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x0a53
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r15.pushMessages
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0a53
            boolean r2 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r2 != 0) goto L_0x0022
            int r2 = r15.currentAccount
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            if (r2 == r3) goto L_0x0022
            goto L_0x0a53
        L_0x0022:
            org.telegram.tgnet.ConnectionsManager r2 = r45.getConnectionsManager()     // Catch:{ Exception -> 0x0a4d }
            r2.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0a4d }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r15.pushMessages     // Catch:{ Exception -> 0x0a4d }
            r3 = 0
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x0a4d }
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2     // Catch:{ Exception -> 0x0a4d }
            org.telegram.messenger.AccountInstance r4 = r45.getAccountInstance()     // Catch:{ Exception -> 0x0a4d }
            android.content.SharedPreferences r4 = r4.getNotificationsSettings()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r5 = "dismissDate"
            int r5 = r4.getInt(r5, r3)     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner     // Catch:{ Exception -> 0x0a4d }
            int r6 = r6.date     // Catch:{ Exception -> 0x0a4d }
            if (r6 > r5) goto L_0x004a
            r45.dismissNotification()     // Catch:{ Exception -> 0x0a4d }
            return
        L_0x004a:
            long r6 = r2.getDialogId()     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$Message r8 = r2.messageOwner     // Catch:{ Exception -> 0x0a4d }
            boolean r8 = r8.mentioned     // Catch:{ Exception -> 0x0a4d }
            if (r8 == 0) goto L_0x0059
            long r8 = r2.getFromChatId()     // Catch:{ Exception -> 0x0a4d }
            goto L_0x005a
        L_0x0059:
            r8 = r6
        L_0x005a:
            r2.getId()     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.peer_id     // Catch:{ Exception -> 0x0a4d }
            long r11 = r10.chat_id     // Catch:{ Exception -> 0x0a4d }
            r13 = 0
            int r16 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r16 == 0) goto L_0x006a
            goto L_0x006c
        L_0x006a:
            long r11 = r10.channel_id     // Catch:{ Exception -> 0x0a4d }
        L_0x006c:
            r17 = r4
            long r3 = r10.user_id     // Catch:{ Exception -> 0x0a4d }
            boolean r10 = r2.isFromUser()     // Catch:{ Exception -> 0x0a4d }
            if (r10 == 0) goto L_0x008c
            int r10 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r10 == 0) goto L_0x0086
            org.telegram.messenger.UserConfig r10 = r45.getUserConfig()     // Catch:{ Exception -> 0x0a4d }
            long r18 = r10.getClientUserId()     // Catch:{ Exception -> 0x0a4d }
            int r10 = (r3 > r18 ? 1 : (r3 == r18 ? 0 : -1))
            if (r10 != 0) goto L_0x008c
        L_0x0086:
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id     // Catch:{ Exception -> 0x0a4d }
            long r3 = r3.user_id     // Catch:{ Exception -> 0x0a4d }
        L_0x008c:
            org.telegram.messenger.MessagesController r10 = r45.getMessagesController()     // Catch:{ Exception -> 0x0a4d }
            java.lang.Long r13 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r13)     // Catch:{ Exception -> 0x0a4d }
            r18 = 0
            int r20 = (r11 > r18 ? 1 : (r11 == r18 ? 0 : -1))
            if (r20 == 0) goto L_0x00c5
            org.telegram.messenger.MessagesController r13 = r45.getMessagesController()     // Catch:{ Exception -> 0x0a4d }
            java.lang.Long r14 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$Chat r13 = r13.getChat(r14)     // Catch:{ Exception -> 0x0a4d }
            if (r13 != 0) goto L_0x00b5
            boolean r14 = r2.isFcmMessage()     // Catch:{ Exception -> 0x0a4d }
            if (r14 == 0) goto L_0x00b5
            boolean r14 = r2.localChannel     // Catch:{ Exception -> 0x0a4d }
            goto L_0x00c2
        L_0x00b5:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r13)     // Catch:{ Exception -> 0x0a4d }
            if (r14 == 0) goto L_0x00c1
            boolean r14 = r13.megagroup     // Catch:{ Exception -> 0x0a4d }
            if (r14 != 0) goto L_0x00c1
            r14 = 1
            goto L_0x00c2
        L_0x00c1:
            r14 = 0
        L_0x00c2:
            r21 = r3
            goto L_0x00c9
        L_0x00c5:
            r21 = r3
            r13 = 0
            r14 = 0
        L_0x00c9:
            r44 = r17
            r17 = r1
            r1 = r44
            int r3 = r15.getNotifyOverride(r1, r8)     // Catch:{ Exception -> 0x0a4d }
            r4 = -1
            r23 = r1
            r1 = 2
            if (r3 != r4) goto L_0x00e2
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r14)     // Catch:{ Exception -> 0x0a4d }
            boolean r3 = r15.isGlobalNotificationsEnabled(r6, r3)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x00e7
        L_0x00e2:
            if (r3 == r1) goto L_0x00e6
            r3 = 1
            goto L_0x00e7
        L_0x00e6:
            r3 = 0
        L_0x00e7:
            r18 = 0
            int r24 = (r11 > r18 ? 1 : (r11 == r18 ? 0 : -1))
            if (r24 == 0) goto L_0x00ef
            if (r13 == 0) goto L_0x00f1
        L_0x00ef:
            if (r10 != 0) goto L_0x00fa
        L_0x00f1:
            boolean r24 = r2.isFcmMessage()     // Catch:{ Exception -> 0x0a4d }
            if (r24 == 0) goto L_0x00fa
            java.lang.String r4 = r2.localName     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0103
        L_0x00fa:
            if (r13 == 0) goto L_0x00ff
            java.lang.String r4 = r13.title     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0103
        L_0x00ff:
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r10)     // Catch:{ Exception -> 0x0a4d }
        L_0x0103:
            r25 = r4
            boolean r4 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a4d }
            if (r4 != 0) goto L_0x0112
            boolean r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a4d }
            if (r4 == 0) goto L_0x0110
            goto L_0x0112
        L_0x0110:
            r4 = 0
            goto L_0x0113
        L_0x0112:
            r4 = 1
        L_0x0113:
            boolean r26 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)     // Catch:{ Exception -> 0x0a4d }
            if (r26 != 0) goto L_0x012b
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0a4d }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0a4d }
            r27 = r2
            r2 = 1
            if (r1 > r2) goto L_0x012d
            if (r4 == 0) goto L_0x0127
            goto L_0x012d
        L_0x0127:
            r1 = r25
            r2 = 1
            goto L_0x0153
        L_0x012b:
            r27 = r2
        L_0x012d:
            if (r4 == 0) goto L_0x0149
            r1 = 0
            int r4 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r4 == 0) goto L_0x013f
            java.lang.String r1 = "NotificationHiddenChatName"
            r2 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0152
        L_0x013f:
            java.lang.String r1 = "NotificationHiddenName"
            r2 = 2131626532(0x7f0e0a24, float:1.8880303E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0152
        L_0x0149:
            java.lang.String r1 = "AppName"
            r2 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0a4d }
        L_0x0152:
            r2 = 0
        L_0x0153:
            int r4 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0a4d }
            r28 = r10
            java.lang.String r10 = ""
            r29 = r14
            r14 = 1
            if (r4 <= r14) goto L_0x0193
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0a4d }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0a4d }
            if (r4 != r14) goto L_0x0175
            org.telegram.messenger.UserConfig r4 = r45.getUserConfig()     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$User r4 = r4.getCurrentUser()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0194
        L_0x0175:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r4.<init>()     // Catch:{ Exception -> 0x0a4d }
            org.telegram.messenger.UserConfig r14 = r45.getUserConfig()     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$User r14 = r14.getCurrentUser()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r14 = org.telegram.messenger.UserObject.getFirstName(r14)     // Catch:{ Exception -> 0x0a4d }
            r4.append(r14)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r14 = "・"
            r4.append(r14)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0194
        L_0x0193:
            r4 = r10
        L_0x0194:
            androidx.collection.LongSparseArray<java.lang.Integer> r14 = r15.pushDialogs     // Catch:{ Exception -> 0x0a4d }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0a4d }
            r30 = r11
            r11 = 1
            if (r14 != r11) goto L_0x01a9
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a4d }
            r12 = 23
            if (r11 >= r12) goto L_0x01a6
            goto L_0x01a9
        L_0x01a6:
            r32 = r6
            goto L_0x01ff
        L_0x01a9:
            androidx.collection.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0a4d }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r12 = "NewMessages"
            r14 = 1
            if (r11 != r14) goto L_0x01ca
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r11.<init>()     // Catch:{ Exception -> 0x0a4d }
            r11.append(r4)     // Catch:{ Exception -> 0x0a4d }
            int r4 = r15.total_unread_count     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r12, r4)     // Catch:{ Exception -> 0x0a4d }
            r11.append(r4)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r11.toString()     // Catch:{ Exception -> 0x0a4d }
            goto L_0x01a6
        L_0x01ca:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r11.<init>()     // Catch:{ Exception -> 0x0a4d }
            r11.append(r4)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = "NotificationMessagesPeopleDisplayOrder"
            r32 = r6
            r14 = 2
            java.lang.Object[] r6 = new java.lang.Object[r14]     // Catch:{ Exception -> 0x0a4d }
            int r7 = r15.total_unread_count     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r12, r7)     // Catch:{ Exception -> 0x0a4d }
            r12 = 0
            r6[r12] = r7     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r7 = "FromChats"
            androidx.collection.LongSparseArray<java.lang.Integer> r12 = r15.pushDialogs     // Catch:{ Exception -> 0x0a4d }
            int r12 = r12.size()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r12)     // Catch:{ Exception -> 0x0a4d }
            r12 = 1
            r6[r12] = r7     // Catch:{ Exception -> 0x0a4d }
            r7 = 2131626580(0x7f0e0a54, float:1.88804E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r7, r6)     // Catch:{ Exception -> 0x0a4d }
            r11.append(r4)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r11.toString()     // Catch:{ Exception -> 0x0a4d }
        L_0x01ff:
            androidx.core.app.NotificationCompat$Builder r6 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            r6.<init>(r7)     // Catch:{ Exception -> 0x0a4d }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0a4d }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r11 = ": "
            java.lang.String r12 = " "
            java.lang.String r14 = " @ "
            r34 = r8
            r9 = 1
            if (r7 != r9) goto L_0x028d
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r15.pushMessages     // Catch:{ Exception -> 0x0a4d }
            r7 = 0
            java.lang.Object r5 = r5.get(r7)     // Catch:{ Exception -> 0x0a4d }
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5     // Catch:{ Exception -> 0x0a4d }
            boolean[] r8 = new boolean[r9]     // Catch:{ Exception -> 0x0a4d }
            r36 = r3
            r9 = 0
            java.lang.String r3 = r15.getStringForMessage(r5, r7, r8, r9)     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner     // Catch:{ Exception -> 0x0a4d }
            boolean r5 = r5.silent     // Catch:{ Exception -> 0x0a4d }
            if (r3 != 0) goto L_0x0230
            return
        L_0x0230:
            if (r2 == 0) goto L_0x0275
            if (r13 == 0) goto L_0x0248
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r2.<init>()     // Catch:{ Exception -> 0x0a4d }
            r2.append(r14)     // Catch:{ Exception -> 0x0a4d }
            r2.append(r1)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r2 = r3.replace(r2, r10)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0276
        L_0x0248:
            r2 = 0
            boolean r7 = r8[r2]     // Catch:{ Exception -> 0x0a4d }
            if (r7 == 0) goto L_0x0261
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r2.<init>()     // Catch:{ Exception -> 0x0a4d }
            r2.append(r1)     // Catch:{ Exception -> 0x0a4d }
            r2.append(r11)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r2 = r3.replace(r2, r10)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0276
        L_0x0261:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r2.<init>()     // Catch:{ Exception -> 0x0a4d }
            r2.append(r1)     // Catch:{ Exception -> 0x0a4d }
            r2.append(r12)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r2 = r3.replace(r2, r10)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0276
        L_0x0275:
            r2 = r3
        L_0x0276:
            r6.setContentText(r2)     // Catch:{ Exception -> 0x0a4d }
            androidx.core.app.NotificationCompat$BigTextStyle r7 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0a4d }
            r7.<init>()     // Catch:{ Exception -> 0x0a4d }
            androidx.core.app.NotificationCompat$BigTextStyle r2 = r7.bigText(r2)     // Catch:{ Exception -> 0x0a4d }
            r6.setStyle(r2)     // Catch:{ Exception -> 0x0a4d }
            r2 = r6
            r44 = r4
            r4 = r3
            r3 = r44
            goto L_0x0345
        L_0x028d:
            r36 = r3
            r6.setContentText(r4)     // Catch:{ Exception -> 0x0a4d }
            androidx.core.app.NotificationCompat$InboxStyle r3 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0a4d }
            r3.<init>()     // Catch:{ Exception -> 0x0a4d }
            r3.setBigContentTitle(r1)     // Catch:{ Exception -> 0x0a4d }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0a4d }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0a4d }
            r8 = 10
            int r7 = java.lang.Math.min(r8, r7)     // Catch:{ Exception -> 0x0a4d }
            r8 = 1
            boolean[] r9 = new boolean[r8]     // Catch:{ Exception -> 0x0a4d }
            r38 = r6
            r6 = 2
            r8 = 0
            r37 = 0
        L_0x02af:
            if (r8 >= r7) goto L_0x0335
            r39 = r7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0a4d }
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0a4d }
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7     // Catch:{ Exception -> 0x0a4d }
            r42 = r3
            r40 = r4
            r41 = r8
            r4 = 0
            r8 = 0
            java.lang.String r3 = r15.getStringForMessage(r7, r8, r9, r4)     // Catch:{ Exception -> 0x0a4d }
            if (r3 == 0) goto L_0x032a
            org.telegram.tgnet.TLRPC$Message r4 = r7.messageOwner     // Catch:{ Exception -> 0x0a4d }
            int r7 = r4.date     // Catch:{ Exception -> 0x0a4d }
            if (r7 > r5) goto L_0x02d0
            goto L_0x032a
        L_0x02d0:
            r7 = 2
            if (r6 != r7) goto L_0x02d7
            boolean r6 = r4.silent     // Catch:{ Exception -> 0x0a4d }
            r37 = r3
        L_0x02d7:
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0a4d }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0a4d }
            r7 = 1
            if (r4 != r7) goto L_0x0324
            if (r2 == 0) goto L_0x0324
            if (r13 == 0) goto L_0x02f8
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r4.<init>()     // Catch:{ Exception -> 0x0a4d }
            r4.append(r14)     // Catch:{ Exception -> 0x0a4d }
            r4.append(r1)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r3 = r3.replace(r4, r10)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0324
        L_0x02f8:
            r4 = 0
            boolean r7 = r9[r4]     // Catch:{ Exception -> 0x0a4d }
            if (r7 == 0) goto L_0x0311
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r4.<init>()     // Catch:{ Exception -> 0x0a4d }
            r4.append(r1)     // Catch:{ Exception -> 0x0a4d }
            r4.append(r11)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r3 = r3.replace(r4, r10)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0324
        L_0x0311:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r4.<init>()     // Catch:{ Exception -> 0x0a4d }
            r4.append(r1)     // Catch:{ Exception -> 0x0a4d }
            r4.append(r12)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r3 = r3.replace(r4, r10)     // Catch:{ Exception -> 0x0a4d }
        L_0x0324:
            r4 = r42
            r4.addLine(r3)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x032c
        L_0x032a:
            r4 = r42
        L_0x032c:
            int r8 = r41 + 1
            r3 = r4
            r7 = r39
            r4 = r40
            goto L_0x02af
        L_0x0335:
            r44 = r4
            r4 = r3
            r3 = r44
            r4.setSummaryText(r3)     // Catch:{ Exception -> 0x0a4d }
            r2 = r38
            r2.setStyle(r4)     // Catch:{ Exception -> 0x0a4d }
            r5 = r6
            r4 = r37
        L_0x0345:
            if (r46 == 0) goto L_0x0359
            if (r36 == 0) goto L_0x0359
            org.telegram.messenger.MediaController r6 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0a4d }
            boolean r6 = r6.isRecordingAudio()     // Catch:{ Exception -> 0x0a4d }
            if (r6 != 0) goto L_0x0359
            r6 = 1
            if (r5 != r6) goto L_0x0357
            goto L_0x0359
        L_0x0357:
            r6 = 0
            goto L_0x035a
        L_0x0359:
            r6 = 1
        L_0x035a:
            java.lang.String r7 = "custom_"
            if (r6 != 0) goto L_0x0414
            int r11 = (r32 > r34 ? 1 : (r32 == r34 ? 0 : -1))
            if (r11 != 0) goto L_0x0414
            if (r13 == 0) goto L_0x0414
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r11.<init>()     // Catch:{ Exception -> 0x0a4d }
            r11.append(r7)     // Catch:{ Exception -> 0x0a4d }
            r8 = r32
            r11.append(r8)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0a4d }
            r12 = r23
            r14 = 0
            boolean r11 = r12.getBoolean(r11, r14)     // Catch:{ Exception -> 0x0a4d }
            if (r11 == 0) goto L_0x03ae
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r11.<init>()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r14 = "smart_max_count_"
            r11.append(r14)     // Catch:{ Exception -> 0x0a4d }
            r11.append(r8)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0a4d }
            r14 = 2
            int r11 = r12.getInt(r11, r14)     // Catch:{ Exception -> 0x0a4d }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r14.<init>()     // Catch:{ Exception -> 0x0a4d }
            r32 = r6
            java.lang.String r6 = "smart_delay_"
            r14.append(r6)     // Catch:{ Exception -> 0x0a4d }
            r14.append(r8)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r6 = r14.toString()     // Catch:{ Exception -> 0x0a4d }
            r14 = 180(0xb4, float:2.52E-43)
            int r14 = r12.getInt(r6, r14)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x03b3
        L_0x03ae:
            r32 = r6
            r14 = 180(0xb4, float:2.52E-43)
            r11 = 2
        L_0x03b3:
            if (r11 == 0) goto L_0x0410
            androidx.collection.LongSparseArray<android.graphics.Point> r6 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a4d }
            java.lang.Object r6 = r6.get(r8)     // Catch:{ Exception -> 0x0a4d }
            android.graphics.Point r6 = (android.graphics.Point) r6     // Catch:{ Exception -> 0x0a4d }
            if (r6 != 0) goto L_0x03d9
            android.graphics.Point r6 = new android.graphics.Point     // Catch:{ Exception -> 0x0a4d }
            long r36 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a4d }
            r23 = r10
            r33 = 1000(0x3e8, double:4.94E-321)
            long r10 = r36 / r33
            int r11 = (int) r10     // Catch:{ Exception -> 0x0a4d }
            r10 = 1
            r6.<init>(r10, r11)     // Catch:{ Exception -> 0x0a4d }
            androidx.collection.LongSparseArray<android.graphics.Point> r10 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a4d }
            r10.put(r8, r6)     // Catch:{ Exception -> 0x0a4d }
            r40 = r3
            r14 = r4
            goto L_0x041f
        L_0x03d9:
            r23 = r10
            int r10 = r6.y     // Catch:{ Exception -> 0x0a4d }
            int r10 = r10 + r14
            r40 = r3
            r14 = r4
            long r3 = (long) r10     // Catch:{ Exception -> 0x0a4d }
            long r36 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a4d }
            r33 = 1000(0x3e8, double:4.94E-321)
            long r36 = r36 / r33
            int r10 = (r3 > r36 ? 1 : (r3 == r36 ? 0 : -1))
            if (r10 >= 0) goto L_0x03fa
            long r3 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a4d }
            long r3 = r3 / r33
            int r4 = (int) r3     // Catch:{ Exception -> 0x0a4d }
            r3 = 1
            r6.set(r3, r4)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x041f
        L_0x03fa:
            int r3 = r6.x     // Catch:{ Exception -> 0x0a4d }
            if (r3 >= r11) goto L_0x040d
            r4 = 1
            int r3 = r3 + r4
            long r10 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a4d }
            r33 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 / r33
            int r4 = (int) r10     // Catch:{ Exception -> 0x0a4d }
            r6.set(r3, r4)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x041f
        L_0x040d:
            r32 = 1
            goto L_0x041f
        L_0x0410:
            r40 = r3
            r14 = r4
            goto L_0x041d
        L_0x0414:
            r40 = r3
            r14 = r4
            r12 = r23
            r8 = r32
            r32 = r6
        L_0x041d:
            r23 = r10
        L_0x041f:
            android.net.Uri r3 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r3 = r3.getPath()     // Catch:{ Exception -> 0x0a4d }
            boolean r4 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0a4d }
            if (r4 != 0) goto L_0x042c
            r33 = 1
            goto L_0x042e
        L_0x042c:
            r33 = 0
        L_0x042e:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r4.<init>()     // Catch:{ Exception -> 0x0a4d }
            r4.append(r7)     // Catch:{ Exception -> 0x0a4d }
            r4.append(r8)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a4d }
            r6 = 0
            boolean r4 = r12.getBoolean(r4, r6)     // Catch:{ Exception -> 0x0a4d }
            r6 = 3
            if (r4 == 0) goto L_0x04ba
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r4.<init>()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r7 = "vibrate_"
            r4.append(r7)     // Catch:{ Exception -> 0x0a4d }
            r4.append(r8)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a4d }
            r7 = 0
            int r4 = r12.getInt(r4, r7)     // Catch:{ Exception -> 0x0a4d }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r7.<init>()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r10 = "priority_"
            r7.append(r10)     // Catch:{ Exception -> 0x0a4d }
            r7.append(r8)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a4d }
            int r7 = r12.getInt(r7, r6)     // Catch:{ Exception -> 0x0a4d }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r10.<init>()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r11 = "sound_path_"
            r10.append(r11)     // Catch:{ Exception -> 0x0a4d }
            r10.append(r8)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0a4d }
            r11 = 0
            java.lang.String r10 = r12.getString(r10, r11)     // Catch:{ Exception -> 0x0a4d }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r11.<init>()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r6 = "color_"
            r11.append(r6)     // Catch:{ Exception -> 0x0a4d }
            r11.append(r8)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r6 = r11.toString()     // Catch:{ Exception -> 0x0a4d }
            boolean r6 = r12.contains(r6)     // Catch:{ Exception -> 0x0a4d }
            if (r6 == 0) goto L_0x04b8
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r6.<init>()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r11 = "color_"
            r6.append(r11)     // Catch:{ Exception -> 0x0a4d }
            r6.append(r8)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0a4d }
            r11 = 0
            int r6 = r12.getInt(r6, r11)     // Catch:{ Exception -> 0x0a4d }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x04be
        L_0x04b8:
            r6 = 0
            goto L_0x04be
        L_0x04ba:
            r4 = 0
            r6 = 0
            r7 = 3
            r10 = 0
        L_0x04be:
            r18 = 0
            int r37 = (r30 > r18 ? 1 : (r30 == r18 ? 0 : -1))
            if (r37 == 0) goto L_0x051c
            if (r29 == 0) goto L_0x04f1
            java.lang.String r11 = "ChannelSoundPath"
            java.lang.String r11 = r12.getString(r11, r3)     // Catch:{ Exception -> 0x0a4d }
            r37 = r11
            java.lang.String r11 = "vibrate_channel"
            r38 = r14
            r14 = 0
            int r11 = r12.getInt(r11, r14)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r14 = "priority_channel"
            r39 = r11
            r11 = 1
            int r14 = r12.getInt(r14, r11)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r11 = "ChannelLed"
            r41 = r14
            r14 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r11 = r12.getInt(r11, r14)     // Catch:{ Exception -> 0x0a4d }
            r14 = r11
            r11 = r37
            r29 = 2
            goto L_0x054c
        L_0x04f1:
            r38 = r14
            java.lang.String r11 = "GroupSoundPath"
            java.lang.String r11 = r12.getString(r11, r3)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r14 = "vibrate_group"
            r37 = r11
            r11 = 0
            int r14 = r12.getInt(r14, r11)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r11 = "priority_group"
            r39 = r14
            r14 = 1
            int r11 = r12.getInt(r11, r14)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r14 = "GroupLed"
            r41 = r11
            r11 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r11 = r12.getInt(r14, r11)     // Catch:{ Exception -> 0x0a4d }
            r14 = r11
            r11 = r37
            r29 = 0
            goto L_0x054c
        L_0x051c:
            r38 = r14
            r18 = 0
            int r11 = (r21 > r18 ? 1 : (r21 == r18 ? 0 : -1))
            if (r11 == 0) goto L_0x0557
            java.lang.String r11 = "GlobalSoundPath"
            java.lang.String r11 = r12.getString(r11, r3)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r14 = "vibrate_messages"
            r37 = r11
            r11 = 0
            int r14 = r12.getInt(r14, r11)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r11 = "priority_messages"
            r39 = r14
            r14 = 1
            int r11 = r12.getInt(r11, r14)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r14 = "MessagesLed"
            r41 = r11
            r11 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r11 = r12.getInt(r14, r11)     // Catch:{ Exception -> 0x0a4d }
            r14 = r11
            r11 = r37
            r29 = 1
        L_0x054c:
            r37 = r3
            r3 = r39
            r39 = r5
            r5 = r41
            r41 = r1
            goto L_0x0568
        L_0x0557:
            r11 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r41 = r1
            r37 = r3
            r39 = r5
            r3 = 0
            r5 = 0
            r11 = 0
            r14 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r29 = 1
        L_0x0568:
            r1 = 4
            if (r3 != r1) goto L_0x056f
            r3 = 0
            r42 = 1
            goto L_0x0571
        L_0x056f:
            r42 = 0
        L_0x0571:
            if (r10 == 0) goto L_0x057c
            boolean r43 = android.text.TextUtils.equals(r11, r10)     // Catch:{ Exception -> 0x0a4d }
            if (r43 != 0) goto L_0x057c
            r1 = 3
            r11 = 0
            goto L_0x057f
        L_0x057c:
            r10 = r11
            r1 = 3
            r11 = 1
        L_0x057f:
            if (r7 == r1) goto L_0x0585
            if (r5 == r7) goto L_0x0585
            r11 = 0
            goto L_0x0586
        L_0x0585:
            r7 = r5
        L_0x0586:
            if (r6 == 0) goto L_0x0593
            int r1 = r6.intValue()     // Catch:{ Exception -> 0x0a4d }
            if (r1 == r14) goto L_0x0593
            int r14 = r6.intValue()     // Catch:{ Exception -> 0x0a4d }
            r11 = 0
        L_0x0593:
            if (r4 == 0) goto L_0x059c
            r1 = 4
            if (r4 == r1) goto L_0x059c
            if (r4 == r3) goto L_0x059c
            r3 = r4
            r11 = 0
        L_0x059c:
            if (r33 == 0) goto L_0x05c0
            java.lang.String r1 = "EnableInAppSounds"
            r4 = 1
            boolean r1 = r12.getBoolean(r1, r4)     // Catch:{ Exception -> 0x0a4d }
            if (r1 != 0) goto L_0x05a8
            r10 = 0
        L_0x05a8:
            java.lang.String r1 = "EnableInAppVibrate"
            boolean r1 = r12.getBoolean(r1, r4)     // Catch:{ Exception -> 0x0a4d }
            if (r1 != 0) goto L_0x05b1
            r3 = 2
        L_0x05b1:
            java.lang.String r1 = "EnableInAppPriority"
            r4 = 0
            boolean r1 = r12.getBoolean(r1, r4)     // Catch:{ Exception -> 0x0a4d }
            if (r1 != 0) goto L_0x05bc
            r7 = 0
            goto L_0x05c0
        L_0x05bc:
            r1 = 2
            if (r7 != r1) goto L_0x05c0
            r7 = 1
        L_0x05c0:
            if (r42 == 0) goto L_0x05d7
            r1 = 2
            if (r3 == r1) goto L_0x05d7
            android.media.AudioManager r1 = audioManager     // Catch:{ Exception -> 0x05d2 }
            int r1 = r1.getRingerMode()     // Catch:{ Exception -> 0x05d2 }
            if (r1 == 0) goto L_0x05d7
            r4 = 1
            if (r1 == r4) goto L_0x05d7
            r3 = 2
            goto L_0x05d7
        L_0x05d2:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x0a4d }
        L_0x05d7:
            if (r32 == 0) goto L_0x05dd
            r3 = 0
            r7 = 0
            r10 = 0
            r14 = 0
        L_0x05dd:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            java.lang.Class<org.telegram.ui.LaunchActivity> r5 = org.telegram.ui.LaunchActivity.class
            r1.<init>(r4, r5)     // Catch:{ Exception -> 0x0a4d }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r4.<init>()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r5 = "com.tmessages.openchat"
            r4.append(r5)     // Catch:{ Exception -> 0x0a4d }
            double r5 = java.lang.Math.random()     // Catch:{ Exception -> 0x0a4d }
            r4.append(r5)     // Catch:{ Exception -> 0x0a4d }
            r5 = 2147483647(0x7fffffff, float:NaN)
            r4.append(r5)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a4d }
            r1.setAction(r4)     // Catch:{ Exception -> 0x0a4d }
            r4 = 67108864(0x4000000, float:1.5046328E-36)
            r1.setFlags(r4)     // Catch:{ Exception -> 0x0a4d }
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)     // Catch:{ Exception -> 0x0a4d }
            if (r4 != 0) goto L_0x068e
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0a4d }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0a4d }
            r5 = 1
            if (r4 != r5) goto L_0x0631
            r4 = 0
            int r6 = (r30 > r4 ? 1 : (r30 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x0626
            java.lang.String r6 = "chatId"
            r4 = r30
            r1.putExtra(r6, r4)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0631
        L_0x0626:
            int r6 = (r21 > r4 ? 1 : (r21 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x0631
            java.lang.String r4 = "userId"
            r5 = r21
            r1.putExtra(r4, r5)     // Catch:{ Exception -> 0x0a4d }
        L_0x0631:
            boolean r4 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a4d }
            if (r4 != 0) goto L_0x0688
            boolean r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a4d }
            if (r4 == 0) goto L_0x063c
            goto L_0x0688
        L_0x063c:
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0a4d }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0a4d }
            r5 = 1
            if (r4 != r5) goto L_0x0688
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a4d }
            r5 = 28
            if (r4 >= r5) goto L_0x0688
            if (r13 == 0) goto L_0x0668
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r13.photo     // Catch:{ Exception -> 0x0a4d }
            if (r4 == 0) goto L_0x0688
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.photo_small     // Catch:{ Exception -> 0x0a4d }
            if (r4 == 0) goto L_0x0688
            long r5 = r4.volume_id     // Catch:{ Exception -> 0x0a4d }
            r18 = 0
            int r21 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1))
            if (r21 == 0) goto L_0x0688
            int r5 = r4.local_id     // Catch:{ Exception -> 0x0a4d }
            if (r5 == 0) goto L_0x0688
            r5 = r4
            r6 = r10
            r21 = r11
            r4 = r28
            goto L_0x06ac
        L_0x0668:
            if (r28 == 0) goto L_0x0688
            r4 = r28
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r4.photo     // Catch:{ Exception -> 0x0a4d }
            if (r5 == 0) goto L_0x0684
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ Exception -> 0x0a4d }
            if (r5 == 0) goto L_0x0684
            r6 = r10
            r21 = r11
            long r10 = r5.volume_id     // Catch:{ Exception -> 0x0a4d }
            r18 = 0
            int r22 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1))
            if (r22 == 0) goto L_0x06ab
            int r10 = r5.local_id     // Catch:{ Exception -> 0x0a4d }
            if (r10 == 0) goto L_0x06ab
            goto L_0x06ac
        L_0x0684:
            r6 = r10
            r21 = r11
            goto L_0x06ab
        L_0x0688:
            r6 = r10
            r21 = r11
            r4 = r28
            goto L_0x06ab
        L_0x068e:
            r6 = r10
            r21 = r11
            r4 = r28
            androidx.collection.LongSparseArray<java.lang.Integer> r5 = r15.pushDialogs     // Catch:{ Exception -> 0x0a4d }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0a4d }
            r10 = 1
            if (r5 != r10) goto L_0x06ab
            long r10 = globalSecretChatId     // Catch:{ Exception -> 0x0a4d }
            int r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r5 == 0) goto L_0x06ab
            java.lang.String r5 = "encId"
            int r10 = org.telegram.messenger.DialogObject.getEncryptedChatId(r8)     // Catch:{ Exception -> 0x0a4d }
            r1.putExtra(r5, r10)     // Catch:{ Exception -> 0x0a4d }
        L_0x06ab:
            r5 = 0
        L_0x06ac:
            int r10 = r15.currentAccount     // Catch:{ Exception -> 0x0a4d }
            r11 = r17
            r1.putExtra(r11, r10)     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            r30 = r8
            r8 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            android.app.PendingIntent r1 = android.app.PendingIntent.getActivity(r10, r9, r1, r8)     // Catch:{ Exception -> 0x0a4d }
            r8 = r41
            androidx.core.app.NotificationCompat$Builder r8 = r2.setContentTitle(r8)     // Catch:{ Exception -> 0x0a4d }
            r9 = 2131165891(0x7var_c3, float:1.7946012E38)
            androidx.core.app.NotificationCompat$Builder r8 = r8.setSmallIcon(r9)     // Catch:{ Exception -> 0x0a4d }
            r9 = 1
            androidx.core.app.NotificationCompat$Builder r8 = r8.setAutoCancel(r9)     // Catch:{ Exception -> 0x0a4d }
            int r9 = r15.total_unread_count     // Catch:{ Exception -> 0x0a4d }
            androidx.core.app.NotificationCompat$Builder r8 = r8.setNumber(r9)     // Catch:{ Exception -> 0x0a4d }
            androidx.core.app.NotificationCompat$Builder r1 = r8.setContentIntent(r1)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r8 = r15.notificationGroup     // Catch:{ Exception -> 0x0a4d }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setGroup(r8)     // Catch:{ Exception -> 0x0a4d }
            r8 = 1
            androidx.core.app.NotificationCompat$Builder r1 = r1.setGroupSummary(r8)     // Catch:{ Exception -> 0x0a4d }
            androidx.core.app.NotificationCompat$Builder r1 = r1.setShowWhen(r8)     // Catch:{ Exception -> 0x0a4d }
            r8 = r27
            org.telegram.tgnet.TLRPC$Message r9 = r8.messageOwner     // Catch:{ Exception -> 0x0a4d }
            int r9 = r9.date     // Catch:{ Exception -> 0x0a4d }
            long r9 = (long) r9     // Catch:{ Exception -> 0x0a4d }
            r27 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 * r27
            androidx.core.app.NotificationCompat$Builder r1 = r1.setWhen(r9)     // Catch:{ Exception -> 0x0a4d }
            r9 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r1.setColor(r9)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r1 = "msg"
            r2.setCategory(r1)     // Catch:{ Exception -> 0x0a4d }
            if (r13 != 0) goto L_0x0727
            if (r4 == 0) goto L_0x0727
            java.lang.String r1 = r4.phone     // Catch:{ Exception -> 0x0a4d }
            if (r1 == 0) goto L_0x0727
            int r1 = r1.length()     // Catch:{ Exception -> 0x0a4d }
            if (r1 <= 0) goto L_0x0727
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r1.<init>()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r9 = "tel:+"
            r1.append(r9)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r4.phone     // Catch:{ Exception -> 0x0a4d }
            r1.append(r4)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0a4d }
            r2.addPerson(r1)     // Catch:{ Exception -> 0x0a4d }
        L_0x0727:
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r9 = org.telegram.messenger.NotificationDismissReceiver.class
            r1.<init>(r4, r9)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = "messageDate"
            org.telegram.tgnet.TLRPC$Message r9 = r8.messageOwner     // Catch:{ Exception -> 0x0a4d }
            int r9 = r9.date     // Catch:{ Exception -> 0x0a4d }
            r1.putExtra(r4, r9)     // Catch:{ Exception -> 0x0a4d }
            int r4 = r15.currentAccount     // Catch:{ Exception -> 0x0a4d }
            r1.putExtra(r11, r4)     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            r9 = 134217728(0x8000000, float:3.85186E-34)
            r10 = 1
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r4, r10, r1, r9)     // Catch:{ Exception -> 0x0a4d }
            r2.setDeleteIntent(r1)     // Catch:{ Exception -> 0x0a4d }
            if (r5 == 0) goto L_0x0796
            org.telegram.messenger.ImageLoader r1 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = "50_50"
            r10 = 0
            android.graphics.drawable.BitmapDrawable r1 = r1.getImageFromMemory(r5, r10, r4)     // Catch:{ Exception -> 0x0a4d }
            if (r1 == 0) goto L_0x0761
            android.graphics.Bitmap r1 = r1.getBitmap()     // Catch:{ Exception -> 0x0a4d }
            r2.setLargeIcon(r1)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0797
        L_0x0761:
            r1 = 1
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r5, r1)     // Catch:{ all -> 0x0794 }
            boolean r1 = r4.exists()     // Catch:{ all -> 0x0794 }
            if (r1 == 0) goto L_0x0797
            r1 = 1126170624(0x43200000, float:160.0)
            r5 = 1112014848(0x42480000, float:50.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ all -> 0x0794 }
            float r5 = (float) r5     // Catch:{ all -> 0x0794 }
            float r1 = r1 / r5
            android.graphics.BitmapFactory$Options r5 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0794 }
            r5.<init>()     // Catch:{ all -> 0x0794 }
            r13 = 1065353216(0x3var_, float:1.0)
            int r13 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r13 >= 0) goto L_0x0783
            r1 = 1
            goto L_0x0784
        L_0x0783:
            int r1 = (int) r1     // Catch:{ all -> 0x0794 }
        L_0x0784:
            r5.inSampleSize = r1     // Catch:{ all -> 0x0794 }
            java.lang.String r1 = r4.getAbsolutePath()     // Catch:{ all -> 0x0794 }
            android.graphics.Bitmap r1 = android.graphics.BitmapFactory.decodeFile(r1, r5)     // Catch:{ all -> 0x0794 }
            if (r1 == 0) goto L_0x0797
            r2.setLargeIcon(r1)     // Catch:{ all -> 0x0794 }
            goto L_0x0797
        L_0x0794:
            goto L_0x0797
        L_0x0796:
            r10 = 0
        L_0x0797:
            r1 = 5
            r4 = 26
            r5 = r39
            if (r46 == 0) goto L_0x07da
            r13 = 1
            if (r5 != r13) goto L_0x07a2
            goto L_0x07da
        L_0x07a2:
            if (r7 != 0) goto L_0x07af
            r13 = 0
            r2.setPriority(r13)     // Catch:{ Exception -> 0x0a4d }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a4d }
            if (r7 < r4) goto L_0x07e5
            r7 = 1
            r13 = 3
            goto L_0x07e7
        L_0x07af:
            r13 = 1
            if (r7 == r13) goto L_0x07cf
            r13 = 2
            if (r7 != r13) goto L_0x07b6
            goto L_0x07cf
        L_0x07b6:
            r13 = 4
            if (r7 != r13) goto L_0x07c4
            r7 = -2
            r2.setPriority(r7)     // Catch:{ Exception -> 0x0a4d }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a4d }
            if (r7 < r4) goto L_0x07e5
            r7 = 1
            r13 = 1
            goto L_0x07e7
        L_0x07c4:
            if (r7 != r1) goto L_0x07e5
            r7 = -1
            r2.setPriority(r7)     // Catch:{ Exception -> 0x0a4d }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a4d }
            if (r7 < r4) goto L_0x07e5
            goto L_0x07e2
        L_0x07cf:
            r7 = 1
            r2.setPriority(r7)     // Catch:{ Exception -> 0x0a4d }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a4d }
            if (r7 < r4) goto L_0x07e5
            r7 = 1
            r13 = 4
            goto L_0x07e7
        L_0x07da:
            r7 = -1
            r2.setPriority(r7)     // Catch:{ Exception -> 0x0a4d }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a4d }
            if (r7 < r4) goto L_0x07e5
        L_0x07e2:
            r7 = 1
            r13 = 2
            goto L_0x07e7
        L_0x07e5:
            r7 = 1
            r13 = 0
        L_0x07e7:
            if (r5 == r7) goto L_0x0900
            if (r32 != 0) goto L_0x0900
            if (r33 == 0) goto L_0x07f5
            java.lang.String r5 = "EnableInAppPreview"
            boolean r5 = r12.getBoolean(r5, r7)     // Catch:{ Exception -> 0x0a4d }
            if (r5 == 0) goto L_0x082a
        L_0x07f5:
            int r5 = r38.length()     // Catch:{ Exception -> 0x0a4d }
            r7 = 100
            if (r5 <= r7) goto L_0x0824
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a4d }
            r5.<init>()     // Catch:{ Exception -> 0x0a4d }
            r7 = 100
            r12 = r38
            r10 = 0
            java.lang.String r7 = r12.substring(r10, r7)     // Catch:{ Exception -> 0x0a4d }
            r10 = 32
            r12 = 10
            java.lang.String r7 = r7.replace(r12, r10)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r7 = r7.trim()     // Catch:{ Exception -> 0x0a4d }
            r5.append(r7)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r7 = "..."
            r5.append(r7)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0827
        L_0x0824:
            r12 = r38
            r5 = r12
        L_0x0827:
            r2.setTicker(r5)     // Catch:{ Exception -> 0x0a4d }
        L_0x082a:
            if (r6 == 0) goto L_0x089c
            java.lang.String r5 = "NoSound"
            boolean r5 = r6.equals(r5)     // Catch:{ Exception -> 0x0a4d }
            if (r5 != 0) goto L_0x089c
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a4d }
            if (r5 < r4) goto L_0x0848
            r4 = r37
            boolean r1 = r6.equals(r4)     // Catch:{ Exception -> 0x0a4d }
            if (r1 == 0) goto L_0x0843
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a4d }
            goto L_0x089d
        L_0x0843:
            android.net.Uri r1 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x089d
        L_0x0848:
            r4 = r37
            boolean r4 = r6.equals(r4)     // Catch:{ Exception -> 0x0a4d }
            if (r4 == 0) goto L_0x0856
            android.net.Uri r4 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a4d }
            r2.setSound(r4, r1)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x089c
        L_0x0856:
            r4 = 24
            if (r5 < r4) goto L_0x0895
            java.lang.String r4 = "file://"
            boolean r4 = r6.startsWith(r4)     // Catch:{ Exception -> 0x0a4d }
            if (r4 == 0) goto L_0x0895
            android.net.Uri r4 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a4d }
            boolean r4 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r4)     // Catch:{ Exception -> 0x0a4d }
            if (r4 != 0) goto L_0x0895
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x088d }
            java.lang.String r5 = "org.telegram.messenger.beta.provider"
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x088d }
            java.lang.String r10 = "file://"
            r12 = r23
            java.lang.String r10 = r6.replace(r10, r12)     // Catch:{ Exception -> 0x088d }
            r7.<init>(r10)     // Catch:{ Exception -> 0x088d }
            android.net.Uri r4 = androidx.core.content.FileProvider.getUriForFile(r4, r5, r7)     // Catch:{ Exception -> 0x088d }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x088d }
            java.lang.String r7 = "com.android.systemui"
            r10 = 1
            r5.grantUriPermission(r7, r4, r10)     // Catch:{ Exception -> 0x088d }
            r2.setSound(r4, r1)     // Catch:{ Exception -> 0x088d }
            goto L_0x089c
        L_0x088d:
            android.net.Uri r4 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a4d }
            r2.setSound(r4, r1)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x089c
        L_0x0895:
            android.net.Uri r4 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a4d }
            r2.setSound(r4, r1)     // Catch:{ Exception -> 0x0a4d }
        L_0x089c:
            r1 = 0
        L_0x089d:
            if (r14 == 0) goto L_0x08a6
            r4 = 1000(0x3e8, float:1.401E-42)
            r5 = 1000(0x3e8, float:1.401E-42)
            r2.setLights(r14, r4, r5)     // Catch:{ Exception -> 0x0a4d }
        L_0x08a6:
            r4 = 2
            if (r3 != r4) goto L_0x08ba
            long[] r3 = new long[r4]     // Catch:{ Exception -> 0x0a4d }
            r4 = 0
            r6 = 0
            r3[r6] = r4     // Catch:{ Exception -> 0x0a4d }
            r6 = 1
            r3[r6] = r4     // Catch:{ Exception -> 0x0a4d }
            r2.setVibrate(r3)     // Catch:{ Exception -> 0x0a4d }
        L_0x08b6:
            r20 = r1
            r7 = r3
            goto L_0x08fe
        L_0x08ba:
            r4 = 1
            if (r3 != r4) goto L_0x08d5
            r5 = 4
            long[] r3 = new long[r5]     // Catch:{ Exception -> 0x0a4d }
            r5 = 0
            r7 = 0
            r3[r7] = r5     // Catch:{ Exception -> 0x0a4d }
            r17 = 100
            r3[r4] = r17     // Catch:{ Exception -> 0x0a4d }
            r4 = 2
            r3[r4] = r5     // Catch:{ Exception -> 0x0a4d }
            r4 = 100
            r6 = 3
            r3[r6] = r4     // Catch:{ Exception -> 0x0a4d }
            r2.setVibrate(r3)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x08b6
        L_0x08d5:
            if (r3 == 0) goto L_0x08f4
            r4 = 4
            if (r3 != r4) goto L_0x08db
            goto L_0x08f4
        L_0x08db:
            r4 = 3
            if (r3 != r4) goto L_0x08ef
            r3 = 2
            long[] r4 = new long[r3]     // Catch:{ Exception -> 0x0a4d }
            r3 = 0
            r5 = 0
            r4[r3] = r5     // Catch:{ Exception -> 0x0a4d }
            r3 = 1
            r5 = 1000(0x3e8, double:4.94E-321)
            r4[r3] = r5     // Catch:{ Exception -> 0x0a4d }
            r2.setVibrate(r4)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x08fb
        L_0x08ef:
            r20 = r1
            r1 = 1
            r7 = 0
            goto L_0x0911
        L_0x08f4:
            r3 = 2
            r2.setDefaults(r3)     // Catch:{ Exception -> 0x0a4d }
            r3 = 0
            long[] r4 = new long[r3]     // Catch:{ Exception -> 0x0a4d }
        L_0x08fb:
            r20 = r1
            r7 = r4
        L_0x08fe:
            r1 = 1
            goto L_0x0911
        L_0x0900:
            r1 = 2
            long[] r3 = new long[r1]     // Catch:{ Exception -> 0x0a4d }
            r1 = 0
            r4 = 0
            r3[r1] = r4     // Catch:{ Exception -> 0x0a4d }
            r1 = 1
            r3[r1] = r4     // Catch:{ Exception -> 0x0a4d }
            r2.setVibrate(r3)     // Catch:{ Exception -> 0x0a4d }
            r7 = r3
            r20 = 0
        L_0x0911:
            boolean r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a4d }
            if (r3 != 0) goto L_0x09d6
            boolean r3 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a4d }
            if (r3 != 0) goto L_0x09d6
            long r3 = r8.getDialogId()     // Catch:{ Exception -> 0x0a4d }
            r5 = 777000(0xbdb28, double:3.83889E-318)
            int r10 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r10 != 0) goto L_0x09d6
            org.telegram.tgnet.TLRPC$Message r3 = r8.messageOwner     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$ReplyMarkup r3 = r3.reply_markup     // Catch:{ Exception -> 0x0a4d }
            if (r3 == 0) goto L_0x09d6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r3 = r3.rows     // Catch:{ Exception -> 0x0a4d }
            int r4 = r3.size()     // Catch:{ Exception -> 0x0a4d }
            r5 = 0
            r12 = 0
        L_0x0934:
            if (r12 >= r4) goto L_0x09cf
            java.lang.Object r6 = r3.get(r12)     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r6 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r6     // Catch:{ Exception -> 0x0a4d }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r10 = r6.buttons     // Catch:{ Exception -> 0x0a4d }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0a4d }
            r1 = 0
        L_0x0943:
            if (r1 >= r10) goto L_0x09b9
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r9 = r6.buttons     // Catch:{ Exception -> 0x0a4d }
            java.lang.Object r9 = r9.get(r1)     // Catch:{ Exception -> 0x0a4d }
            org.telegram.tgnet.TLRPC$KeyboardButton r9 = (org.telegram.tgnet.TLRPC$KeyboardButton) r9     // Catch:{ Exception -> 0x0a4d }
            r18 = r3
            boolean r3 = r9 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0a4d }
            if (r3 == 0) goto L_0x099b
            android.content.Intent r3 = new android.content.Intent     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            r46 = r4
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r4 = org.telegram.messenger.NotificationCallbackReceiver.class
            r3.<init>(r5, r4)     // Catch:{ Exception -> 0x0a4d }
            int r4 = r15.currentAccount     // Catch:{ Exception -> 0x0a4d }
            r3.putExtra(r11, r4)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = "did"
            r19 = r6
            r5 = r30
            r3.putExtra(r4, r5)     // Catch:{ Exception -> 0x0a4d }
            byte[] r4 = r9.data     // Catch:{ Exception -> 0x0a4d }
            if (r4 == 0) goto L_0x0978
            r22 = r10
            java.lang.String r10 = "data"
            r3.putExtra(r10, r4)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x097a
        L_0x0978:
            r22 = r10
        L_0x097a:
            java.lang.String r4 = "mid"
            int r10 = r8.getId()     // Catch:{ Exception -> 0x0a4d }
            r3.putExtra(r4, r10)     // Catch:{ Exception -> 0x0a4d }
            java.lang.String r4 = r9.text     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            int r10 = r15.lastButtonId     // Catch:{ Exception -> 0x0a4d }
            r27 = r8
            int r8 = r10 + 1
            r15.lastButtonId = r8     // Catch:{ Exception -> 0x0a4d }
            r8 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r3 = android.app.PendingIntent.getBroadcast(r9, r10, r3, r8)     // Catch:{ Exception -> 0x0a4d }
            r8 = 0
            r2.addAction(r8, r4, r3)     // Catch:{ Exception -> 0x0a4d }
            r3 = 1
            goto L_0x09a7
        L_0x099b:
            r46 = r4
            r3 = r5
            r19 = r6
            r27 = r8
            r22 = r10
            r5 = r30
            r8 = 0
        L_0x09a7:
            int r1 = r1 + 1
            r4 = r46
            r30 = r5
            r6 = r19
            r10 = r22
            r8 = r27
            r9 = 134217728(0x8000000, float:3.85186E-34)
            r5 = r3
            r3 = r18
            goto L_0x0943
        L_0x09b9:
            r18 = r3
            r46 = r4
            r3 = r5
            r27 = r8
            r5 = r30
            r8 = 0
            int r12 = r12 + 1
            r8 = r27
            r1 = 1
            r9 = 134217728(0x8000000, float:3.85186E-34)
            r5 = r3
            r3 = r18
            goto L_0x0934
        L_0x09cf:
            r16 = r5
            r5 = r30
            r3 = r16
            goto L_0x09da
        L_0x09d6:
            r5 = r30
            r8 = 0
            r3 = 0
        L_0x09da:
            if (r3 != 0) goto L_0x0a33
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a4d }
            r3 = 24
            if (r1 >= r3) goto L_0x0a33
            java.lang.String r3 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0a4d }
            int r3 = r3.length()     // Catch:{ Exception -> 0x0a4d }
            if (r3 != 0) goto L_0x0a33
            boolean r3 = r45.hasMessagesToReply()     // Catch:{ Exception -> 0x0a4d }
            if (r3 == 0) goto L_0x0a33
            android.content.Intent r3 = new android.content.Intent     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r8 = org.telegram.messenger.PopupReplyReceiver.class
            r3.<init>(r4, r8)     // Catch:{ Exception -> 0x0a4d }
            int r4 = r15.currentAccount     // Catch:{ Exception -> 0x0a4d }
            r3.putExtra(r11, r4)     // Catch:{ Exception -> 0x0a4d }
            r4 = 19
            if (r1 > r4) goto L_0x0a1b
            r1 = 2131165477(0x7var_, float:1.7945172E38)
            java.lang.String r4 = "Reply"
            r8 = 2131627361(0x7f0e0d61, float:1.8881984E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            r9 = 134217728(0x8000000, float:3.85186E-34)
            r10 = 2
            android.app.PendingIntent r3 = android.app.PendingIntent.getBroadcast(r8, r10, r3, r9)     // Catch:{ Exception -> 0x0a4d }
            r2.addAction(r1, r4, r3)     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0a33
        L_0x0a1b:
            r1 = 2131165476(0x7var_, float:1.794517E38)
            java.lang.String r4 = "Reply"
            r8 = 2131627361(0x7f0e0d61, float:1.8881984E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)     // Catch:{ Exception -> 0x0a4d }
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a4d }
            r9 = 134217728(0x8000000, float:3.85186E-34)
            r10 = 2
            android.app.PendingIntent r3 = android.app.PendingIntent.getBroadcast(r8, r10, r3, r9)     // Catch:{ Exception -> 0x0a4d }
            r2.addAction(r1, r4, r3)     // Catch:{ Exception -> 0x0a4d }
        L_0x0a33:
            r1 = r45
            r3 = r40
            r4 = r5
            r6 = r25
            r8 = r14
            r9 = r20
            r10 = r13
            r11 = r21
            r12 = r33
            r13 = r32
            r14 = r29
            r1.showExtraNotifications(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0a4d }
            r45.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0a4d }
            goto L_0x0a52
        L_0x0a4d:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0a52:
            return
        L_0x0a53:
            r45.dismissNotification()
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
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0aec: MOVE  (r1v38 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>) = 
          (r51v1 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>)
        
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
    /* JADX WARNING: Removed duplicated region for block: B:117:0x030c  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x032c  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x0335  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0387  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x038e  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x03ba  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x041e  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0474  */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0490  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04a4  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x04c2  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x04cc A[SYNTHETIC, Splitter:B:187:0x04cc] */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x051f  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0525  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0546 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x0575  */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x05ad  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x05e4  */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x0660  */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x06a3  */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x06bd  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x06c4  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x0747  */
    /* JADX WARNING: Removed duplicated region for block: B:358:0x082b  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x084c  */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x087b  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x08ec  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x08f6  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0921  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x097b  */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x09b2  */
    /* JADX WARNING: Removed duplicated region for block: B:403:0x09d7  */
    /* JADX WARNING: Removed duplicated region for block: B:404:0x09f9  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0aa9  */
    /* JADX WARNING: Removed duplicated region for block: B:409:0x0ab4  */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0ab9  */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x0ac9  */
    /* JADX WARNING: Removed duplicated region for block: B:417:0x0acf  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0ad7  */
    /* JADX WARNING: Removed duplicated region for block: B:422:0x0adc  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0af7  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0b7e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x0baf  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01cc  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01d6  */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r70, java.lang.String r71, long r72, java.lang.String r74, long[] r75, int r76, android.net.Uri r77, int r78, boolean r79, boolean r80, boolean r81, int r82) {
        /*
            r69 = this;
            r15 = r69
            r14 = r70
            int r0 = android.os.Build.VERSION.SDK_INT
            r13 = 26
            if (r0 < r13) goto L_0x0027
            r1 = r69
            r2 = r72
            r4 = r74
            r5 = r75
            r6 = r76
            r7 = r77
            r8 = r78
            r9 = r79
            r10 = r80
            r11 = r81
            r12 = r82
            java.lang.String r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r14.setChannelId(r1)
        L_0x0027:
            android.app.Notification r12 = r70.build()
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
            org.telegram.messenger.AccountInstance r0 = r69.getAccountInstance()
            android.content.SharedPreferences r0 = r0.getNotificationsSettings()
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            androidx.collection.LongSparseArray r10 = new androidx.collection.LongSparseArray
            r10.<init>()
            r9 = 0
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
            int r5 = r0.getInt(r5, r9)
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner
            int r6 = r6.date
            if (r6 > r5) goto L_0x0084
            goto L_0x009e
        L_0x0084:
            java.lang.Object r5 = r10.get(r3)
            java.util.ArrayList r5 = (java.util.ArrayList) r5
            if (r5 != 0) goto L_0x009b
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r10.put(r3, r5)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r11.add(r3)
        L_0x009b:
            r5.add(r2)
        L_0x009e:
            int r1 = r1 + 1
            goto L_0x0054
        L_0x00a1:
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            androidx.collection.LongSparseArray r8 = r0.clone()
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r0.clear()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 27
            r4 = 1
            if (r0 <= r6) goto L_0x00c1
            int r1 = r11.size()
            if (r1 <= r4) goto L_0x00bf
            goto L_0x00c1
        L_0x00bf:
            r5 = 0
            goto L_0x00c2
        L_0x00c1:
            r5 = 1
        L_0x00c2:
            if (r5 == 0) goto L_0x00c9
            if (r0 < r13) goto L_0x00c9
            checkOtherNotificationsChannel()
        L_0x00c9:
            org.telegram.messenger.UserConfig r0 = r69.getUserConfig()
            long r2 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x00df
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x00dc
            goto L_0x00df
        L_0x00dc:
            r19 = 0
            goto L_0x00e1
        L_0x00df:
            r19 = 1
        L_0x00e1:
            r1 = 7
            androidx.collection.LongSparseArray r13 = new androidx.collection.LongSparseArray
            r13.<init>()
            int r6 = r11.size()
            r4 = 0
        L_0x00ec:
            if (r4 >= r6) goto L_0x0CLASSNAME
            int r0 = r7.size()
            if (r0 < r1) goto L_0x00f6
            goto L_0x0CLASSNAME
        L_0x00f6:
            java.lang.Object r0 = r11.get(r4)
            java.lang.Long r0 = (java.lang.Long) r0
            r21 = r6
            r20 = r7
            long r6 = r0.longValue()
            java.lang.Object r0 = r10.get(r6)
            r1 = r0
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            java.lang.Object r0 = r1.get(r9)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            int r9 = r0.getId()
            java.lang.Object r0 = r8.get(r6)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x012a
            int r0 = r15.lastInternalNotificationId
            r24 = r4
            int r4 = r0 + 1
            r15.lastInternalNotificationId = r4
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            goto L_0x012f
        L_0x012a:
            r24 = r4
            r8.remove(r6)
        L_0x012f:
            r4 = r0
            r25 = r8
            r8 = 0
            java.lang.Object r0 = r1.get(r8)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            int r8 = r8.date
            boolean r26 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            r27 = 777000(0xbdb28, double:3.83889E-318)
            r29 = 0
            r31 = r10
            if (r26 != 0) goto L_0x0271
            int r26 = (r6 > r27 ? 1 : (r6 == r27 ? 0 : -1))
            if (r26 == 0) goto L_0x0151
            r26 = 1
            goto L_0x0153
        L_0x0151:
            r26 = 0
        L_0x0153:
            boolean r32 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r32 == 0) goto L_0x01ee
            org.telegram.messenger.MessagesController r10 = r69.getMessagesController()
            r33 = r12
            java.lang.Long r12 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r12)
            if (r10 != 0) goto L_0x01aa
            boolean r12 = r0.isFcmMessage()
            if (r12 == 0) goto L_0x0176
            java.lang.String r0 = r0.localName
        L_0x0171:
            r35 = r10
            r34 = r11
            goto L_0x01c5
        L_0x0176:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x018e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found user to show dialog notification "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x018e:
            r27 = r2
            r23 = r5
            r67 = r13
            r14 = r20
            r20 = r25
            r26 = r31
            r66 = r33
            r22 = 7
            r25 = 0
            r29 = 1
            r30 = 27
            r32 = 26
            r31 = r11
            goto L_0x0c0a
        L_0x01aa:
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r10)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r10.photo
            if (r12 == 0) goto L_0x0171
            org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
            if (r12 == 0) goto L_0x0171
            r35 = r10
            r34 = r11
            long r10 = r12.volume_id
            int r36 = (r10 > r29 ? 1 : (r10 == r29 ? 0 : -1))
            if (r36 == 0) goto L_0x01c5
            int r10 = r12.local_id
            if (r10 == 0) goto L_0x01c5
            goto L_0x01c6
        L_0x01c5:
            r12 = 0
        L_0x01c6:
            boolean r10 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r10 == 0) goto L_0x01d6
            r0 = 2131627354(0x7f0e0d5a, float:1.888197E38)
            java.lang.String r10 = "RepliesTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            goto L_0x01e3
        L_0x01d6:
            int r10 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r10 != 0) goto L_0x01e3
            r0 = 2131626250(0x7f0e090a, float:1.887973E38)
            java.lang.String r10 = "MessageScheduledReminderNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
        L_0x01e3:
            r36 = r12
            r10 = r35
            r11 = 0
            r35 = 0
            r37 = 0
            goto L_0x0304
        L_0x01ee:
            r34 = r11
            r33 = r12
            org.telegram.messenger.MessagesController r10 = r69.getMessagesController()
            long r11 = -r6
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r11)
            if (r10 != 0) goto L_0x0234
            boolean r11 = r0.isFcmMessage()
            if (r11 == 0) goto L_0x021a
            boolean r11 = r0.isSupergroup()
            java.lang.String r12 = r0.localName
            boolean r0 = r0.localChannel
            r37 = r0
            r35 = r11
            r0 = r12
            r36 = 0
            r11 = r10
            r10 = 0
            goto L_0x0304
        L_0x021a:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02a5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found chat to show dialog notification "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02a5
        L_0x0234:
            boolean r0 = r10.megagroup
            boolean r11 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r11 == 0) goto L_0x0242
            boolean r11 = r10.megagroup
            if (r11 != 0) goto L_0x0242
            r11 = 1
            goto L_0x0243
        L_0x0242:
            r11 = 0
        L_0x0243:
            java.lang.String r12 = r10.title
            r35 = r0
            org.telegram.tgnet.TLRPC$ChatPhoto r0 = r10.photo
            if (r0 == 0) goto L_0x0265
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small
            if (r0 == 0) goto L_0x0265
            r36 = r10
            r37 = r11
            long r10 = r0.volume_id
            int r38 = (r10 > r29 ? 1 : (r10 == r29 ? 0 : -1))
            if (r38 == 0) goto L_0x0269
            int r10 = r0.local_id
            if (r10 == 0) goto L_0x0269
            r11 = r36
            r10 = 0
            r36 = r0
            r0 = r12
            goto L_0x0304
        L_0x0265:
            r36 = r10
            r37 = r11
        L_0x0269:
            r0 = r12
            r11 = r36
            r10 = 0
            r36 = 0
            goto L_0x0304
        L_0x0271:
            r34 = r11
            r33 = r12
            long r10 = globalSecretChatId
            int r0 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x02ec
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            org.telegram.messenger.MessagesController r10 = r69.getMessagesController()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r10 = r10.getEncryptedChat(r11)
            if (r10 != 0) goto L_0x02c1
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x02a5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "not found secret chat to show dialog notification "
            r1.append(r4)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x02a5:
            r27 = r2
            r23 = r5
            r67 = r13
            r14 = r20
            r20 = r25
            r26 = r31
            r66 = r33
            r31 = r34
            r22 = 7
            r25 = 0
            r29 = 1
            r30 = 27
            r32 = 26
            goto L_0x0c0a
        L_0x02c1:
            org.telegram.messenger.MessagesController r0 = r69.getMessagesController()
            long r11 = r10.user_id
            java.lang.Long r11 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r11)
            if (r0 != 0) goto L_0x02ed
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02a5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found secret chat user to show dialog notification "
            r0.append(r1)
            long r6 = r10.user_id
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02a5
        L_0x02ec:
            r0 = 0
        L_0x02ed:
            r10 = 2131627537(0x7f0e0e11, float:1.8882341E38)
            java.lang.String r11 = "SecretChatName"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r11 = 0
            r26 = 0
            r35 = 0
            r36 = 0
            r37 = 0
            r68 = r10
            r10 = r0
            r0 = r68
        L_0x0304:
            java.lang.String r12 = "NotificationHiddenChatName"
            r39 = r10
            java.lang.String r10 = "NotificationHiddenName"
            if (r19 == 0) goto L_0x032c
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r0 == 0) goto L_0x031c
            r41 = r5
            r5 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r5)
            goto L_0x0325
        L_0x031c:
            r41 = r5
            r5 = 2131626532(0x7f0e0a24, float:1.8880303E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r5)
        L_0x0325:
            r5 = r0
            r36 = r8
            r0 = 0
            r26 = 0
            goto L_0x0333
        L_0x032c:
            r41 = r5
            r5 = r0
            r0 = r36
            r36 = r8
        L_0x0333:
            if (r0 == 0) goto L_0x0387
            r8 = 1
            java.io.File r42 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r8)
            int r8 = android.os.Build.VERSION.SDK_INT
            r14 = 28
            if (r8 >= r14) goto L_0x0380
            org.telegram.messenger.ImageLoader r8 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r14 = "50_50"
            r43 = r10
            r10 = 0
            android.graphics.drawable.BitmapDrawable r0 = r8.getImageFromMemory(r0, r10, r14)
            if (r0 == 0) goto L_0x0355
            android.graphics.Bitmap r0 = r0.getBitmap()
        L_0x0353:
            r8 = r0
            goto L_0x0384
        L_0x0355:
            boolean r0 = r42.exists()     // Catch:{ all -> 0x0383 }
            if (r0 == 0) goto L_0x037e
            r0 = 1126170624(0x43200000, float:160.0)
            r8 = 1112014848(0x42480000, float:50.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)     // Catch:{ all -> 0x0383 }
            float r8 = (float) r8     // Catch:{ all -> 0x0383 }
            float r0 = r0 / r8
            android.graphics.BitmapFactory$Options r8 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0383 }
            r8.<init>()     // Catch:{ all -> 0x0383 }
            r14 = 1065353216(0x3var_, float:1.0)
            int r14 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r14 >= 0) goto L_0x0372
            r0 = 1
            goto L_0x0373
        L_0x0372:
            int r0 = (int) r0     // Catch:{ all -> 0x0383 }
        L_0x0373:
            r8.inSampleSize = r0     // Catch:{ all -> 0x0383 }
            java.lang.String r0 = r42.getAbsolutePath()     // Catch:{ all -> 0x0383 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r8)     // Catch:{ all -> 0x0383 }
            goto L_0x0353
        L_0x037e:
            r0 = r10
            goto L_0x0353
        L_0x0380:
            r43 = r10
            r10 = 0
        L_0x0383:
            r8 = r10
        L_0x0384:
            r14 = r42
            goto L_0x038c
        L_0x0387:
            r43 = r10
            r10 = 0
            r8 = r10
            r14 = r8
        L_0x038c:
            if (r11 == 0) goto L_0x03ba
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r5)
            if (r14 == 0) goto L_0x03ab
            boolean r32 = r14.exists()
            if (r32 == 0) goto L_0x03ab
            int r10 = android.os.Build.VERSION.SDK_INT
            r42 = r8
            r8 = 28
            if (r10 < r8) goto L_0x03ad
            r15.loadRoundAvatar(r14, r0)
            goto L_0x03ad
        L_0x03ab:
            r42 = r8
        L_0x03ad:
            r8 = r4
            r10 = r5
            long r4 = r11.id
            long r4 = -r4
            androidx.core.app.Person r0 = r0.build()
            r13.put(r4, r0)
            goto L_0x03be
        L_0x03ba:
            r10 = r5
            r42 = r8
            r8 = r4
        L_0x03be:
            java.lang.String r4 = "max_id"
            java.lang.String r5 = "dialog_id"
            r44 = r14
            java.lang.String r14 = "currentAccount"
            if (r37 == 0) goto L_0x03ca
            if (r35 == 0) goto L_0x0461
        L_0x03ca:
            if (r26 == 0) goto L_0x0461
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0461
            int r0 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x0461
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r0 != 0) goto L_0x0461
            android.content.Intent r0 = new android.content.Intent
            r26 = r11
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            r35 = r12
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r12 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r11, r12)
            r0.putExtra(r5, r6)
            r0.putExtra(r4, r9)
            int r11 = r15.currentAccount
            r0.putExtra(r14, r11)
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r12 = r8.intValue()
            r45 = r8
            r8 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r11, r12, r0, r8)
            androidx.core.app.RemoteInput$Builder r8 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r11 = "extra_voice_reply"
            r8.<init>(r11)
            r11 = 2131627361(0x7f0e0d61, float:1.8881984E38)
            java.lang.String r12 = "Reply"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            androidx.core.app.RemoteInput$Builder r8 = r8.setLabel(r11)
            androidx.core.app.RemoteInput r8 = r8.build()
            boolean r11 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r11 == 0) goto L_0x0430
            r12 = 1
            java.lang.Object[] r11 = new java.lang.Object[r12]
            r12 = 0
            r11[r12] = r10
            java.lang.String r12 = "ReplyToGroup"
            r46 = r4
            r4 = 2131627362(0x7f0e0d62, float:1.8881986E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r12, r4, r11)
            goto L_0x0441
        L_0x0430:
            r46 = r4
            r4 = 2131627363(0x7f0e0d63, float:1.8881988E38)
            r11 = 1
            java.lang.Object[] r12 = new java.lang.Object[r11]
            r11 = 0
            r12[r11] = r10
            java.lang.String r11 = "ReplyToUser"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r11, r4, r12)
        L_0x0441:
            androidx.core.app.NotificationCompat$Action$Builder r11 = new androidx.core.app.NotificationCompat$Action$Builder
            r12 = 2131165521(0x7var_, float:1.7945261E38)
            r11.<init>(r12, r4, r0)
            r4 = 1
            androidx.core.app.NotificationCompat$Action$Builder r0 = r11.setAllowGeneratedReplies(r4)
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setSemanticAction(r4)
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.addRemoteInput(r8)
            r4 = 0
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setShowsUserInterface(r4)
            androidx.core.app.NotificationCompat$Action r0 = r0.build()
            r4 = r0
            goto L_0x046a
        L_0x0461:
            r46 = r4
            r45 = r8
            r26 = r11
            r35 = r12
            r4 = 0
        L_0x046a:
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.pushDialogs
            java.lang.Object r0 = r0.get(r6)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0479
            r8 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r8)
        L_0x0479:
            int r0 = r0.intValue()
            int r8 = r1.size()
            int r0 = java.lang.Math.max(r0, r8)
            r8 = 2
            r11 = 1
            if (r0 <= r11) goto L_0x04a4
            int r12 = android.os.Build.VERSION.SDK_INT
            r11 = 28
            if (r12 < r11) goto L_0x0490
            goto L_0x04a4
        L_0x0490:
            java.lang.Object[] r11 = new java.lang.Object[r8]
            r12 = 0
            r11[r12] = r10
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r12 = 1
            r11[r12] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r11)
            r11 = r0
            goto L_0x04a5
        L_0x04a4:
            r11 = r10
        L_0x04a5:
            java.lang.Object r0 = r13.get(r2)
            r12 = r0
            androidx.core.app.Person r12 = (androidx.core.app.Person) r12
            int r0 = android.os.Build.VERSION.SDK_INT
            r8 = 28
            if (r0 < r8) goto L_0x0517
            if (r12 != 0) goto L_0x0517
            org.telegram.messenger.MessagesController r0 = r69.getMessagesController()
            java.lang.Long r8 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r8)
            if (r0 != 0) goto L_0x04ca
            org.telegram.messenger.UserConfig r0 = r69.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x04ca:
            if (r0 == 0) goto L_0x0517
            org.telegram.tgnet.TLRPC$UserProfilePhoto r8 = r0.photo     // Catch:{ all -> 0x050e }
            if (r8 == 0) goto L_0x0517
            org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_small     // Catch:{ all -> 0x050e }
            if (r8 == 0) goto L_0x0517
            r48 = r4
            r47 = r5
            long r4 = r8.volume_id     // Catch:{ all -> 0x050c }
            int r49 = (r4 > r29 ? 1 : (r4 == r29 ? 0 : -1))
            if (r49 == 0) goto L_0x051b
            int r4 = r8.local_id     // Catch:{ all -> 0x050c }
            if (r4 == 0) goto L_0x051b
            androidx.core.app.Person$Builder r4 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x050c }
            r4.<init>()     // Catch:{ all -> 0x050c }
            java.lang.String r5 = "FromYou"
            r8 = 2131625730(0x7f0e0702, float:1.8878676E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r8)     // Catch:{ all -> 0x050c }
            androidx.core.app.Person$Builder r4 = r4.setName(r5)     // Catch:{ all -> 0x050c }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x050c }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x050c }
            r5 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r5)     // Catch:{ all -> 0x050c }
            r15.loadRoundAvatar(r0, r4)     // Catch:{ all -> 0x050c }
            androidx.core.app.Person r4 = r4.build()     // Catch:{ all -> 0x050c }
            r13.put(r2, r4)     // Catch:{ all -> 0x0509 }
            r12 = r4
            goto L_0x051b
        L_0x0509:
            r0 = move-exception
            r12 = r4
            goto L_0x0513
        L_0x050c:
            r0 = move-exception
            goto L_0x0513
        L_0x050e:
            r0 = move-exception
            r48 = r4
            r47 = r5
        L_0x0513:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x051b
        L_0x0517:
            r48 = r4
            r47 = r5
        L_0x051b:
            java.lang.String r4 = ""
            if (r12 == 0) goto L_0x0525
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((androidx.core.app.Person) r12)
            goto L_0x052a
        L_0x0525:
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((java.lang.CharSequence) r4)
        L_0x052a:
            r5 = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            r8 = 28
            if (r0 < r8) goto L_0x053f
            boolean r8 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r8 == 0) goto L_0x0539
            if (r37 == 0) goto L_0x053f
        L_0x0539:
            boolean r8 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r8 == 0) goto L_0x0542
        L_0x053f:
            r5.setConversationTitle(r11)
        L_0x0542:
            r8 = 28
            if (r0 < r8) goto L_0x0557
            if (r37 != 0) goto L_0x054e
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r0 != 0) goto L_0x0557
        L_0x054e:
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r0 == 0) goto L_0x0555
            goto L_0x0557
        L_0x0555:
            r0 = 0
            goto L_0x0558
        L_0x0557:
            r0 = 1
        L_0x0558:
            r5.setGroupConversation(r0)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r11 = 1
            java.lang.String[] r12 = new java.lang.String[r11]
            r49 = r9
            boolean[] r9 = new boolean[r11]
            int r0 = r1.size()
            int r0 = r0 - r11
            r11 = r0
            r50 = 0
            r51 = 0
        L_0x0571:
            r52 = 1000(0x3e8, double:4.94E-321)
            if (r11 < 0) goto L_0x08ab
            java.lang.Object r0 = r1.get(r11)
            r54 = r1
            r1 = r0
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            java.lang.String r0 = r15.getShortStringForMessage(r1, r12, r9)
            r55 = r14
            java.lang.String r14 = "NotificationMessageScheduledName"
            int r57 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r57 != 0) goto L_0x058f
            r23 = 0
            r12[r23] = r10
            goto L_0x05a9
        L_0x058f:
            r23 = 0
            boolean r57 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r57 == 0) goto L_0x05a9
            r57 = r10
            org.telegram.tgnet.TLRPC$Message r10 = r1.messageOwner
            boolean r10 = r10.from_scheduled
            if (r10 == 0) goto L_0x05ab
            r10 = 2131626575(0x7f0e0a4f, float:1.888039E38)
            java.lang.String r58 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r12[r23] = r58
            goto L_0x05ab
        L_0x05a9:
            r57 = r10
        L_0x05ab:
            if (r0 != 0) goto L_0x05e4
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05d8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "message text is null for "
            r0.append(r10)
            int r10 = r1.getId()
            r0.append(r10)
            java.lang.String r10 = " did = "
            r0.append(r10)
            r58 = r11
            long r10 = r1.getDialogId()
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x05da
        L_0x05d8:
            r58 = r11
        L_0x05da:
            r59 = r2
            r61 = r8
            r40 = r12
            r56 = r43
            goto L_0x0899
        L_0x05e4:
            r58 = r11
            int r10 = r8.length()
            if (r10 <= 0) goto L_0x05f1
            java.lang.String r10 = "\n\n"
            r8.append(r10)
        L_0x05f1:
            int r10 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r10 == 0) goto L_0x061b
            org.telegram.tgnet.TLRPC$Message r10 = r1.messageOwner
            boolean r10 = r10.from_scheduled
            if (r10 == 0) goto L_0x061b
            boolean r10 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r10 == 0) goto L_0x061b
            r10 = 2
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r10 = 2131626575(0x7f0e0a4f, float:1.888039E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r14 = 0
            r11[r14] = r10
            r10 = 1
            r11[r10] = r0
            java.lang.String r0 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r0, r11)
            r8.append(r0)
            goto L_0x0637
        L_0x061b:
            r14 = 0
            r10 = r12[r14]
            if (r10 == 0) goto L_0x0634
            r10 = 2
            java.lang.Object[] r11 = new java.lang.Object[r10]
            r10 = r12[r14]
            r11[r14] = r10
            r10 = 1
            r11[r10] = r0
            java.lang.String r10 = "%1$s: %2$s"
            java.lang.String r10 = java.lang.String.format(r10, r11)
            r8.append(r10)
            goto L_0x0637
        L_0x0634:
            r8.append(r0)
        L_0x0637:
            r10 = r0
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r0 == 0) goto L_0x0641
            r59 = r2
            goto L_0x0654
        L_0x0641:
            if (r37 == 0) goto L_0x0647
            r59 = r2
            long r2 = -r6
            goto L_0x0655
        L_0x0647:
            r59 = r2
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r0 == 0) goto L_0x0654
            long r2 = r1.getSenderId()
            goto L_0x0655
        L_0x0654:
            r2 = r6
        L_0x0655:
            java.lang.Object r0 = r13.get(r2)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r11 = 0
            r14 = r12[r11]
            if (r14 != 0) goto L_0x06a3
            if (r19 == 0) goto L_0x069c
            boolean r11 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r11 == 0) goto L_0x068c
            if (r37 == 0) goto L_0x067e
            int r11 = android.os.Build.VERSION.SDK_INT
            r14 = 27
            if (r11 <= r14) goto L_0x069c
            r14 = r35
            r11 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.String r35 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r11 = r35
            r35 = r14
            goto L_0x0689
        L_0x067e:
            r14 = r35
            r11 = 2131626530(0x7f0e0a22, float:1.8880299E38)
            java.lang.String r14 = "NotificationHiddenChatUserName"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
        L_0x0689:
            r14 = r43
            goto L_0x06ae
        L_0x068c:
            int r11 = android.os.Build.VERSION.SDK_INT
            r14 = 27
            if (r11 <= r14) goto L_0x069c
            r14 = r43
            r11 = 2131626532(0x7f0e0a24, float:1.8880303E38)
            java.lang.String r40 = org.telegram.messenger.LocaleController.getString(r14, r11)
            goto L_0x06ac
        L_0x069c:
            r14 = r43
            r11 = 2131626532(0x7f0e0a24, float:1.8880303E38)
            r11 = r4
            goto L_0x06ae
        L_0x06a3:
            r14 = r43
            r11 = 2131626532(0x7f0e0a24, float:1.8880303E38)
            r23 = 0
            r40 = r12[r23]
        L_0x06ac:
            r11 = r40
        L_0x06ae:
            r40 = r12
            if (r0 == 0) goto L_0x06c4
            java.lang.CharSequence r12 = r0.getName()
            boolean r12 = android.text.TextUtils.equals(r12, r11)
            if (r12 != 0) goto L_0x06bd
            goto L_0x06c4
        L_0x06bd:
            r2 = r0
            r61 = r8
            r56 = r14
            goto L_0x0741
        L_0x06c4:
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r11)
            r11 = 0
            boolean r12 = r9[r11]
            if (r12 == 0) goto L_0x0735
            boolean r11 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r11 != 0) goto L_0x0735
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 28
            if (r11 < r12) goto L_0x0735
            boolean r11 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r11 != 0) goto L_0x072b
            if (r37 == 0) goto L_0x06e7
            goto L_0x072b
        L_0x06e7:
            long r11 = r1.getSenderId()
            r56 = r14
            org.telegram.messenger.MessagesController r14 = r69.getMessagesController()
            r61 = r8
            java.lang.Long r8 = java.lang.Long.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r8 = r14.getUser(r8)
            if (r8 != 0) goto L_0x070f
            org.telegram.messenger.MessagesStorage r8 = r69.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r8 = r8.getUserSync(r11)
            if (r8 == 0) goto L_0x070f
            org.telegram.messenger.MessagesController r11 = r69.getMessagesController()
            r12 = 1
            r11.putUser(r8, r12)
        L_0x070f:
            if (r8 == 0) goto L_0x0729
            org.telegram.tgnet.TLRPC$UserProfilePhoto r8 = r8.photo
            if (r8 == 0) goto L_0x0729
            org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_small
            if (r8 == 0) goto L_0x0729
            long r11 = r8.volume_id
            int r14 = (r11 > r29 ? 1 : (r11 == r29 ? 0 : -1))
            if (r14 == 0) goto L_0x0729
            int r11 = r8.local_id
            if (r11 == 0) goto L_0x0729
            r11 = 1
            java.io.File r8 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r11)
            goto L_0x0731
        L_0x0729:
            r8 = 0
            goto L_0x0731
        L_0x072b:
            r61 = r8
            r56 = r14
            r8 = r44
        L_0x0731:
            r15.loadRoundAvatar(r8, r0)
            goto L_0x0739
        L_0x0735:
            r61 = r8
            r56 = r14
        L_0x0739:
            androidx.core.app.Person r0 = r0.build()
            r13.put(r2, r0)
            r2 = r0
        L_0x0741:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r0 != 0) goto L_0x087b
            r3 = 0
            boolean r0 = r9[r3]
            if (r0 == 0) goto L_0x0828
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r0 < r3) goto L_0x0828
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r8 = "activity"
            java.lang.Object r0 = r0.getSystemService(r8)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x0828
            if (r19 != 0) goto L_0x0828
            boolean r0 = r1.isSecretMedia()
            if (r0 != 0) goto L_0x0828
            int r0 = r1.type
            r8 = 1
            if (r0 == r8) goto L_0x0775
            boolean r0 = r1.isSticker()
            if (r0 == 0) goto L_0x0828
        L_0x0775:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r8 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r11 = r1.messageOwner
            int r11 = r11.date
            long r11 = (long) r11
            long r11 = r11 * r52
            r8.<init>(r10, r11, r2)
            boolean r11 = r1.isSticker()
            if (r11 == 0) goto L_0x0790
            java.lang.String r11 = "image/webp"
            goto L_0x0792
        L_0x0790:
            java.lang.String r11 = "image/jpeg"
        L_0x0792:
            boolean r12 = r0.exists()
            if (r12 == 0) goto L_0x07a6
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x07a1 }
            java.lang.String r14 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r12, r14, r0)     // Catch:{ Exception -> 0x07a1 }
            goto L_0x07f8
        L_0x07a1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07f7
        L_0x07a6:
            org.telegram.messenger.FileLoader r12 = r69.getFileLoader()
            java.lang.String r14 = r0.getName()
            boolean r12 = r12.isLoadingFile(r14)
            if (r12 == 0) goto L_0x07f7
            android.net.Uri$Builder r12 = new android.net.Uri$Builder
            r12.<init>()
            java.lang.String r14 = "content"
            android.net.Uri$Builder r12 = r12.scheme(r14)
            java.lang.String r14 = "org.telegram.messenger.beta.notification_image_provider"
            android.net.Uri$Builder r12 = r12.authority(r14)
            java.lang.String r14 = "msg_media_raw"
            android.net.Uri$Builder r12 = r12.appendPath(r14)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            int r3 = r15.currentAccount
            r14.append(r3)
            r14.append(r4)
            java.lang.String r3 = r14.toString()
            android.net.Uri$Builder r3 = r12.appendPath(r3)
            java.lang.String r12 = r0.getName()
            android.net.Uri$Builder r3 = r3.appendPath(r12)
            java.lang.String r0 = r0.getAbsolutePath()
            java.lang.String r12 = "final_path"
            android.net.Uri$Builder r0 = r3.appendQueryParameter(r12, r0)
            android.net.Uri r0 = r0.build()
            goto L_0x07f8
        L_0x07f7:
            r0 = 0
        L_0x07f8:
            if (r0 == 0) goto L_0x0828
            r8.setData(r11, r0)
            r5.addMessage(r8)
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r8 = "com.android.systemui"
            r11 = 1
            r3.grantUriPermission(r8, r0, r11)
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda4 r3 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda4
            r3.<init>(r0)
            r11 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r11)
            java.lang.CharSequence r0 = r1.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0826
            java.lang.CharSequence r0 = r1.caption
            org.telegram.tgnet.TLRPC$Message r3 = r1.messageOwner
            int r3 = r3.date
            long r11 = (long) r3
            long r11 = r11 * r52
            r5.addMessage(r0, r11, r2)
        L_0x0826:
            r0 = 1
            goto L_0x0829
        L_0x0828:
            r0 = 0
        L_0x0829:
            if (r0 != 0) goto L_0x0835
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.date
            long r11 = (long) r0
            long r11 = r11 * r52
            r5.addMessage(r10, r11, r2)
        L_0x0835:
            r2 = 0
            boolean r0 = r9[r2]
            if (r0 == 0) goto L_0x0885
            if (r19 != 0) goto L_0x0885
            boolean r0 = r1.isVoice()
            if (r0 == 0) goto L_0x0885
            java.util.List r0 = r5.getMessages()
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L_0x0885
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            java.io.File r2 = org.telegram.messenger.FileLoader.getPathToMessage(r2)
            int r3 = android.os.Build.VERSION.SDK_INT
            r8 = 24
            if (r3 < r8) goto L_0x0863
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0861 }
            java.lang.String r8 = "org.telegram.messenger.beta.provider"
            android.net.Uri r2 = androidx.core.content.FileProvider.getUriForFile(r3, r8, r2)     // Catch:{ Exception -> 0x0861 }
            goto L_0x0867
        L_0x0861:
            r2 = 0
            goto L_0x0867
        L_0x0863:
            android.net.Uri r2 = android.net.Uri.fromFile(r2)
        L_0x0867:
            if (r2 == 0) goto L_0x0885
            int r3 = r0.size()
            r8 = 1
            int r3 = r3 - r8
            java.lang.Object r0 = r0.get(r3)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r0
            java.lang.String r3 = "audio/ogg"
            r0.setData(r3, r2)
            goto L_0x0885
        L_0x087b:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.date
            long r11 = (long) r0
            long r11 = r11 * r52
            r5.addMessage(r10, r11, r2)
        L_0x0885:
            int r0 = (r6 > r27 ? 1 : (r6 == r27 ? 0 : -1))
            if (r0 != 0) goto L_0x0899
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x0899
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r1 = r1.getId()
            r51 = r0
            r50 = r1
        L_0x0899:
            int r11 = r58 + -1
            r12 = r40
            r1 = r54
            r14 = r55
            r43 = r56
            r10 = r57
            r2 = r59
            r8 = r61
            goto L_0x0571
        L_0x08ab:
            r54 = r1
            r59 = r2
            r61 = r8
            r57 = r10
            r55 = r14
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
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r1 == 0) goto L_0x08f6
            int r1 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            java.lang.String r2 = "encId"
            r0.putExtra(r2, r1)
            goto L_0x0908
        L_0x08f6:
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r1 == 0) goto L_0x0902
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r6)
            goto L_0x0908
        L_0x0902:
            long r1 = -r6
            java.lang.String r3 = "chatId"
            r0.putExtra(r3, r1)
        L_0x0908:
            int r1 = r15.currentAccount
            r2 = r55
            r0.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r3 = 1073741824(0x40000000, float:2.0)
            r4 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r1, r4, r0, r3)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r3 = r48
            if (r48 == 0) goto L_0x0924
            r1.addAction(r3)
        L_0x0924:
            android.content.Intent r4 = new android.content.Intent
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r9 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r4.<init>(r8, r9)
            r8 = 32
            r4.addFlags(r8)
            java.lang.String r8 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r4.setAction(r8)
            r8 = r47
            r4.putExtra(r8, r6)
            r9 = r46
            r8 = r49
            r4.putExtra(r9, r8)
            int r9 = r15.currentAccount
            r4.putExtra(r2, r9)
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r10 = r45.intValue()
            r11 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r9, r10, r4, r11)
            androidx.core.app.NotificationCompat$Action$Builder r9 = new androidx.core.app.NotificationCompat$Action$Builder
            r10 = 2131165678(0x7var_ee, float:1.794558E38)
            r11 = 2131626150(0x7f0e08a6, float:1.8879528E38)
            java.lang.String r12 = "MarkAsRead"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r9.<init>(r10, r11, r4)
            r4 = 2
            androidx.core.app.NotificationCompat$Action$Builder r4 = r9.setSemanticAction(r4)
            r9 = 0
            androidx.core.app.NotificationCompat$Action$Builder r4 = r4.setShowsUserInterface(r9)
            androidx.core.app.NotificationCompat$Action r4 = r4.build()
            boolean r9 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            java.lang.String r10 = "_"
            if (r9 != 0) goto L_0x09b2
            boolean r9 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r9 == 0) goto L_0x0999
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "tguser"
            r9.append(r11)
            r9.append(r6)
            r9.append(r10)
            r9.append(r8)
            java.lang.String r10 = r9.toString()
            goto L_0x09d5
        L_0x0999:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "tgchat"
            r9.append(r11)
            long r11 = -r6
            r9.append(r11)
            r9.append(r10)
            r9.append(r8)
            java.lang.String r10 = r9.toString()
            goto L_0x09d5
        L_0x09b2:
            long r11 = globalSecretChatId
            int r9 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r9 == 0) goto L_0x09d4
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "tgenc"
            r9.append(r11)
            int r11 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            r9.append(r11)
            r9.append(r10)
            r9.append(r8)
            java.lang.String r10 = r9.toString()
            goto L_0x09d5
        L_0x09d4:
            r10 = 0
        L_0x09d5:
            if (r10 == 0) goto L_0x09f9
            r1.setDismissalId(r10)
            androidx.core.app.NotificationCompat$WearableExtender r8 = new androidx.core.app.NotificationCompat$WearableExtender
            r8.<init>()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "summary_"
            r9.append(r11)
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.setDismissalId(r9)
            r14 = r70
            r14.extend(r8)
            goto L_0x09fb
        L_0x09f9:
            r14 = r70
        L_0x09fb:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "tgaccount"
            r8.append(r9)
            r9 = r59
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r1.setBridgeTag(r8)
            r8 = r54
            r11 = 0
            java.lang.Object r12 = r8.get(r11)
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            org.telegram.tgnet.TLRPC$Message r11 = r12.messageOwner
            int r11 = r11.date
            long r11 = (long) r11
            long r11 = r11 * r52
            androidx.core.app.NotificationCompat$Builder r9 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext
            r9.<init>(r10)
            r10 = r57
            androidx.core.app.NotificationCompat$Builder r9 = r9.setContentTitle(r10)
            r10 = 2131165891(0x7var_c3, float:1.7946012E38)
            androidx.core.app.NotificationCompat$Builder r9 = r9.setSmallIcon(r10)
            java.lang.String r10 = r61.toString()
            androidx.core.app.NotificationCompat$Builder r9 = r9.setContentText(r10)
            r10 = 1
            androidx.core.app.NotificationCompat$Builder r9 = r9.setAutoCancel(r10)
            int r8 = r8.size()
            androidx.core.app.NotificationCompat$Builder r8 = r9.setNumber(r8)
            r9 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            androidx.core.app.NotificationCompat$Builder r8 = r8.setColor(r9)
            r9 = 0
            androidx.core.app.NotificationCompat$Builder r8 = r8.setGroupSummary(r9)
            androidx.core.app.NotificationCompat$Builder r8 = r8.setWhen(r11)
            androidx.core.app.NotificationCompat$Builder r8 = r8.setShowWhen(r10)
            androidx.core.app.NotificationCompat$Builder r5 = r8.setStyle(r5)
            androidx.core.app.NotificationCompat$Builder r0 = r5.setContentIntent(r0)
            androidx.core.app.NotificationCompat$Builder r0 = r0.extend(r1)
            r8 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r8 = r8 - r11
            java.lang.String r1 = java.lang.String.valueOf(r8)
            androidx.core.app.NotificationCompat$Builder r0 = r0.setSortKey(r1)
            java.lang.String r1 = "msg"
            androidx.core.app.NotificationCompat$Builder r9 = r0.setCategory(r1)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r5 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r1, r5)
            java.lang.String r1 = "messageDate"
            r5 = r36
            r0.putExtra(r1, r5)
            java.lang.String r1 = "dialogId"
            r0.putExtra(r1, r6)
            int r1 = r15.currentAccount
            r0.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r5 = r45.intValue()
            r8 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r5, r0, r8)
            r9.setDeleteIntent(r0)
            if (r41 == 0) goto L_0x0ab2
            java.lang.String r0 = r15.notificationGroup
            r9.setGroup(r0)
            r1 = 1
            r9.setGroupAlertBehavior(r1)
        L_0x0ab2:
            if (r3 == 0) goto L_0x0ab7
            r9.addAction(r3)
        L_0x0ab7:
            if (r19 != 0) goto L_0x0abc
            r9.addAction(r4)
        L_0x0abc:
            int r0 = r34.size()
            r4 = 1
            if (r0 != r4) goto L_0x0acf
            boolean r0 = android.text.TextUtils.isEmpty(r71)
            if (r0 != 0) goto L_0x0acf
            r12 = r71
            r9.setSubText(r12)
            goto L_0x0ad1
        L_0x0acf:
            r12 = r71
        L_0x0ad1:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r0 == 0) goto L_0x0ada
            r9.setLocalOnly(r4)
        L_0x0ada:
            if (r42 == 0) goto L_0x0ae1
            r10 = r42
            r9.setLargeIcon(r10)
        L_0x0ae1:
            r1 = 0
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1)
            if (r0 != 0) goto L_0x0b7b
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0b7b
            r1 = r51
            if (r1 == 0) goto L_0x0b7b
            int r0 = r1.size()
            r8 = 0
        L_0x0af5:
            if (r8 >= r0) goto L_0x0b7b
            java.lang.Object r3 = r1.get(r8)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r3 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r5 = r3.buttons
            int r5 = r5.size()
            r10 = 0
        L_0x0b04:
            if (r10 >= r5) goto L_0x0b69
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r11 = r3.buttons
            java.lang.Object r11 = r11.get(r10)
            org.telegram.tgnet.TLRPC$KeyboardButton r11 = (org.telegram.tgnet.TLRPC$KeyboardButton) r11
            boolean r4 = r11 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r4 == 0) goto L_0x0b52
            android.content.Intent r4 = new android.content.Intent
            r27 = r0
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            r28 = r1
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r1 = org.telegram.messenger.NotificationCallbackReceiver.class
            r4.<init>(r0, r1)
            int r0 = r15.currentAccount
            r4.putExtra(r2, r0)
            java.lang.String r0 = "did"
            r4.putExtra(r0, r6)
            byte[] r0 = r11.data
            if (r0 == 0) goto L_0x0b32
            java.lang.String r1 = "data"
            r4.putExtra(r1, r0)
        L_0x0b32:
            java.lang.String r0 = "mid"
            r1 = r50
            r4.putExtra(r0, r1)
            java.lang.String r0 = r11.text
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            r29 = r1
            int r1 = r15.lastButtonId
            r55 = r2
            int r2 = r1 + 1
            r15.lastButtonId = r2
            r2 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r11, r1, r4, r2)
            r11 = 0
            r9.addAction(r11, r0, r1)
            goto L_0x0b5d
        L_0x0b52:
            r27 = r0
            r28 = r1
            r55 = r2
            r29 = r50
            r2 = 134217728(0x8000000, float:3.85186E-34)
            r11 = 0
        L_0x0b5d:
            int r10 = r10 + 1
            r0 = r27
            r1 = r28
            r50 = r29
            r2 = r55
            r4 = 1
            goto L_0x0b04
        L_0x0b69:
            r27 = r0
            r28 = r1
            r55 = r2
            r29 = r50
            r2 = 134217728(0x8000000, float:3.85186E-34)
            r11 = 0
            int r8 = r8 + 1
            r2 = r55
            r4 = 1
            goto L_0x0af5
        L_0x0b7b:
            r11 = 0
            if (r26 != 0) goto L_0x0ba3
            if (r39 == 0) goto L_0x0ba3
            r8 = r39
            java.lang.String r0 = r8.phone
            if (r0 == 0) goto L_0x0ba5
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0ba5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "tel:+"
            r0.append(r1)
            java.lang.String r1 = r8.phone
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r9.addPerson(r0)
            goto L_0x0ba5
        L_0x0ba3:
            r8 = r39
        L_0x0ba5:
            int r0 = android.os.Build.VERSION.SDK_INT
            r10 = 26
            r4 = r33
            r5 = r41
            if (r0 < r10) goto L_0x0bb2
            r15.setNotificationChannel(r4, r9, r5)
        L_0x0bb2:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            r22 = 7
            r1 = r0
            int r3 = r45.intValue()
            r27 = r59
            r2 = r69
            r16 = r4
            r23 = r5
            r62 = r45
            r18 = r57
            r29 = 1
            r4 = r6
            r63 = r6
            r30 = 27
            r6 = r18
            r65 = r20
            r7 = r8
            r20 = r25
            r8 = r26
            r25 = 0
            r26 = r31
            r17 = 26
            r10 = r74
            r31 = r34
            r11 = r75
            r66 = r16
            r12 = r76
            r67 = r13
            r32 = 26
            r13 = r77
            r14 = r78
            r15 = r79
            r16 = r80
            r17 = r81
            r18 = r82
            r1.<init>(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            r14 = r65
            r14.add(r0)
            r15 = r69
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r3 = r62
            r1 = r63
            r0.put(r1, r3)
        L_0x0c0a:
            int r4 = r24 + 1
            r7 = r14
            r8 = r20
            r6 = r21
            r5 = r23
            r10 = r26
            r2 = r27
            r11 = r31
            r12 = r66
            r13 = r67
            r1 = 7
            r9 = 0
            r14 = r70
            goto L_0x00ec
        L_0x0CLASSNAME:
            r23 = r5
            r14 = r7
            r20 = r8
            r66 = r12
            r67 = r13
            r25 = 0
            if (r23 == 0) goto L_0x0CLASSNAME
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0c4a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r15.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0c4a:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager     // Catch:{ SecurityException -> 0x0CLASSNAME }
            int r1 = r15.notificationId     // Catch:{ SecurityException -> 0x0CLASSNAME }
            r2 = r66
            r0.notify(r1, r2)     // Catch:{ SecurityException -> 0x0CLASSNAME }
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = r69
            r2 = r70
            r3 = r72
            r5 = r74
            r6 = r75
            r7 = r76
            r8 = r77
            r9 = r78
            r10 = r79
            r11 = r80
            r12 = r81
            r13 = r82
            r1.resetNotificationSound(r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0CLASSNAME
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.cancel(r1)
        L_0x0CLASSNAME:
            r9 = 0
        L_0x0CLASSNAME:
            int r0 = r20.size()
            if (r9 >= r0) goto L_0x0cc9
            r1 = r20
            long r2 = r1.keyAt(r9)
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x0c9d
            goto L_0x0cc4
        L_0x0c9d:
            java.lang.Object r0 = r1.valueAt(r9)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0cbb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "cancel notification id "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0cbb:
            androidx.core.app.NotificationManagerCompat r2 = notificationManager
            int r0 = r0.intValue()
            r2.cancel(r0)
        L_0x0cc4:
            int r9 = r9 + 1
            r20 = r1
            goto L_0x0CLASSNAME
        L_0x0cc9:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r14.size()
            r0.<init>(r1)
            int r1 = r14.size()
            r9 = 0
        L_0x0cd7:
            if (r9 >= r1) goto L_0x0d35
            java.lang.Object r2 = r14.get(r9)
            org.telegram.messenger.NotificationsController$1NotificationHolder r2 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r2
            r0.clear()
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 29
            if (r3 < r4) goto L_0x0d1a
            long r3 = r2.dialogId
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r3)
            if (r3 != 0) goto L_0x0d1a
            androidx.core.app.NotificationCompat$Builder r3 = r2.notification
            long r4 = r2.dialogId
            java.lang.String r6 = r2.name
            org.telegram.tgnet.TLRPC$User r7 = r2.user
            org.telegram.tgnet.TLRPC$Chat r8 = r2.chat
            r10 = r67
            java.lang.Object r11 = r10.get(r4)
            androidx.core.app.Person r11 = (androidx.core.app.Person) r11
            r70 = r69
            r71 = r3
            r72 = r4
            r74 = r6
            r75 = r7
            r76 = r8
            r77 = r11
            java.lang.String r3 = r70.createNotificationShortcut(r71, r72, r74, r75, r76, r77)
            if (r3 == 0) goto L_0x0d1c
            r0.add(r3)
            goto L_0x0d1c
        L_0x0d1a:
            r10 = r67
        L_0x0d1c:
            r2.call()
            boolean r2 = r69.unsupportedNotificationShortcut()
            if (r2 != 0) goto L_0x0d30
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L_0x0d30
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r2, r0)
        L_0x0d30:
            int r9 = r9 + 1
            r67 = r10
            goto L_0x0cd7
        L_0x0d35:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, java.lang.String, long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):void");
    }

    @TargetApi(28)
    private void loadRoundAvatar(File file, Person.Builder builder) {
        if (file != null) {
            try {
                builder.setIcon(IconCompat.createWithBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(file), NotificationsController$$ExternalSyntheticLambda0.INSTANCE)));
            } catch (Throwable unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$loadRoundAvatar$35(Canvas canvas) {
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
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda9(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$playOutChatSound$38() {
        try {
            if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundOutPlay) > 100) {
                this.lastSoundOutPlay = SystemClock.elapsedRealtime();
                if (this.soundPool == null) {
                    SoundPool soundPool2 = new SoundPool(3, 1, 0);
                    this.soundPool = soundPool2;
                    soundPool2.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda2.INSTANCE);
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$playOutChatSound$37(SoundPool soundPool2, int i, int i2) {
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
        if (!DialogObject.isEncryptedDialog(j)) {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            TLRPC$TL_account_updateNotifySettings tLRPC$TL_account_updateNotifySettings = new TLRPC$TL_account_updateNotifySettings();
            TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings = new TLRPC$TL_inputPeerNotifySettings();
            tLRPC$TL_account_updateNotifySettings.settings = tLRPC$TL_inputPeerNotifySettings;
            tLRPC$TL_inputPeerNotifySettings.flags |= 1;
            tLRPC$TL_inputPeerNotifySettings.show_previews = notificationsSettings.getBoolean("content_preview_" + j, true);
            TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings2 = tLRPC$TL_account_updateNotifySettings.settings;
            tLRPC$TL_inputPeerNotifySettings2.flags = tLRPC$TL_inputPeerNotifySettings2.flags | 2;
            tLRPC$TL_inputPeerNotifySettings2.silent = notificationsSettings.getBoolean("silent_" + j, false);
            int i2 = notificationsSettings.getInt("notify2_" + j, -1);
            if (i2 != -1) {
                TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings3 = tLRPC$TL_account_updateNotifySettings.settings;
                tLRPC$TL_inputPeerNotifySettings3.flags |= 4;
                if (i2 == 3) {
                    tLRPC$TL_inputPeerNotifySettings3.mute_until = notificationsSettings.getInt("notifyuntil_" + j, 0);
                } else {
                    if (i2 == 2) {
                        i = Integer.MAX_VALUE;
                    }
                    tLRPC$TL_inputPeerNotifySettings3.mute_until = i;
                }
            }
            TLRPC$TL_inputNotifyPeer tLRPC$TL_inputNotifyPeer = new TLRPC$TL_inputNotifyPeer();
            tLRPC$TL_account_updateNotifySettings.peer = tLRPC$TL_inputNotifyPeer;
            tLRPC$TL_inputNotifyPeer.peer = getMessagesController().getInputPeer(j);
            getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, NotificationsController$$ExternalSyntheticLambda40.INSTANCE);
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
        getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, NotificationsController$$ExternalSyntheticLambda39.INSTANCE);
    }

    public boolean isGlobalNotificationsEnabled(long j) {
        return isGlobalNotificationsEnabled(j, (Boolean) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000e, code lost:
        if (r6.booleanValue() != false) goto L_0x002c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0028, code lost:
        if (r4.megagroup == false) goto L_0x002c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isGlobalNotificationsEnabled(long r4, java.lang.Boolean r6) {
        /*
            r3 = this;
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            r1 = 2
            r2 = 0
            if (r0 == 0) goto L_0x002b
            if (r6 == 0) goto L_0x0013
            boolean r4 = r6.booleanValue()
            if (r4 == 0) goto L_0x0011
            goto L_0x002c
        L_0x0011:
            r1 = 0
            goto L_0x002c
        L_0x0013:
            org.telegram.messenger.MessagesController r6 = r3.getMessagesController()
            long r4 = -r4
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r6.getChat(r4)
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r5 == 0) goto L_0x0011
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L_0x0011
            goto L_0x002c
        L_0x002b:
            r1 = 1
        L_0x002c:
            boolean r4 = r3.isGlobalNotificationsEnabled((int) r1)
            return r4
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
