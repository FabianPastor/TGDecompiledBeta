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
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC$TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC$TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC$TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC$TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp;
import org.telegram.tgnet.TLRPC$TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL;
import org.telegram.tgnet.TLRPC$TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler;
import org.telegram.tgnet.TLRPC$TL_notificationSoundDefault;
import org.telegram.tgnet.TLRPC$TL_notificationSoundLocal;
import org.telegram.tgnet.TLRPC$TL_notificationSoundNone;
import org.telegram.tgnet.TLRPC$TL_notificationSoundRingtone;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.PopupNotificationActivity;
/* loaded from: classes.dex */
public class NotificationsController extends BaseController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = null;
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
    protected static AudioManager audioManager;
    private static final Object[] lockObjects;
    private static NotificationManagerCompat notificationManager;
    private static NotificationManager systemNotificationManager;
    private AlarmManager alarmManager;
    private boolean channelGroupsCreated;
    private ArrayList<MessageObject> delayedPushMessages;
    private LongSparseArray<MessageObject> fcmRandomMessagesDict;
    private Boolean groupsCreated;
    private boolean inChatSoundEnabled;
    private int lastBadgeCount;
    private int lastButtonId;
    public long lastNotificationChannelCreateTime;
    private int lastOnlineFromOtherDevice;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private LongSparseArray<Integer> lastWearNotifiedMessageId;
    private String launcherClassName;
    private Runnable notificationDelayRunnable;
    private PowerManager.WakeLock notificationDelayWakelock;
    private String notificationGroup;
    private int notificationId;
    private boolean notifyCheck;
    private long openedDialogId;
    private HashSet<Long> openedInBubbleDialogs;
    private int personalCount;
    public ArrayList<MessageObject> popupMessages;
    public ArrayList<MessageObject> popupReplyMessages;
    private LongSparseArray<Integer> pushDialogs;
    private LongSparseArray<Integer> pushDialogsOverrideMention;
    private ArrayList<MessageObject> pushMessages;
    private LongSparseArray<SparseArray<MessageObject>> pushMessagesDict;
    public boolean showBadgeMessages;
    public boolean showBadgeMuted;
    public boolean showBadgeNumber;
    private LongSparseArray<Point> smartNotificationsDialogs;
    private int soundIn;
    private boolean soundInLoaded;
    private int soundOut;
    private boolean soundOutLoaded;
    private SoundPool soundPool;
    private int soundRecord;
    private boolean soundRecordLoaded;
    char[] spoilerChars;
    private int total_unread_count;
    private LongSparseArray<Integer> wearNotificationsIds;
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    public static long globalSecretChatId = DialogObject.makeEncryptedDialogId(1);

    public static String getGlobalNotificationsKey(int i) {
        return i == 0 ? "EnableGroup2" : i == 1 ? "EnableAll2" : "EnableChannel2";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateServerNotificationsSettings$39(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* JADX INFO: Access modifiers changed from: private */
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
        audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService("audio");
        Instance = new NotificationsController[4];
        lockObjects = new Object[4];
        for (int i = 0; i < 4; i++) {
            lockObjects[i] = new Object();
        }
    }

    public static NotificationsController getInstance(int i) {
        NotificationsController notificationsController = Instance[i];
        if (notificationsController == null) {
            synchronized (lockObjects[i]) {
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
        this.pushMessages = new ArrayList<>();
        this.delayedPushMessages = new ArrayList<>();
        this.pushMessagesDict = new LongSparseArray<>();
        this.fcmRandomMessagesDict = new LongSparseArray<>();
        this.smartNotificationsDialogs = new LongSparseArray<>();
        this.pushDialogs = new LongSparseArray<>();
        this.wearNotificationsIds = new LongSparseArray<>();
        this.lastWearNotifiedMessageId = new LongSparseArray<>();
        this.pushDialogsOverrideMention = new LongSparseArray<>();
        this.popupMessages = new ArrayList<>();
        this.popupReplyMessages = new ArrayList<>();
        this.openedInBubbleDialogs = new HashSet<>();
        this.openedDialogId = 0L;
        this.lastButtonId = 5000;
        this.total_unread_count = 0;
        this.personalCount = 0;
        this.notifyCheck = false;
        this.lastOnlineFromOtherDevice = 0;
        this.lastBadgeCount = -1;
        this.spoilerChars = new char[]{10252, 10338, 10385, 10280};
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
            FileLog.e(e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm");
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "telegram:notification_delay_lock");
            this.notificationDelayWakelock = newWakeLock;
            newWakeLock.setReferenceCounted(false);
        } catch (Exception e3) {
            FileLog.e(e3);
        }
        this.notificationDelayRunnable = new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$new$0();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("delay reached");
        }
        if (!this.delayedPushMessages.isEmpty()) {
            showOrUpdateNotification(true);
            this.delayedPushMessages.clear();
        }
        try {
            if (!this.notificationDelayWakelock.isHeld()) {
                return;
            }
            this.notificationDelayWakelock.release();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void checkOtherNotificationsChannel() {
        SharedPreferences sharedPreferences;
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
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
        if (notificationChannel != null) {
            return;
        }
        NotificationChannel notificationChannel2 = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, "Internal notifications", 3);
        notificationChannel2.enableLights(false);
        notificationChannel2.enableVibration(false);
        notificationChannel2.setSound(null, null);
        try {
            systemNotificationManager.createNotificationChannel(notificationChannel2);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void muteUntil(long j, int i) {
        long j2 = 0;
        if (j != 0) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            boolean isGlobalNotificationsEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(j);
            if (i != Integer.MAX_VALUE) {
                edit.putInt("notify2_" + j, 3);
                edit.putInt("notifyuntil_" + j, getConnectionsManager().getCurrentTime() + i);
                j2 = (((long) i) << 32) | 1;
            } else if (!isGlobalNotificationsEnabled) {
                edit.remove("notify2_" + j);
            } else {
                edit.putInt("notify2_" + j, 2);
                j2 = 1L;
            }
            getInstance(this.currentAccount).removeNotificationsForDialog(j);
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j, j2);
            edit.commit();
            TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j);
            if (tLRPC$Dialog != null) {
                TLRPC$TL_peerNotifySettings tLRPC$TL_peerNotifySettings = new TLRPC$TL_peerNotifySettings();
                tLRPC$Dialog.notify_settings = tLRPC$TL_peerNotifySettings;
                if (i != Integer.MAX_VALUE || isGlobalNotificationsEnabled) {
                    tLRPC$TL_peerNotifySettings.mute_until = i;
                }
            }
            getInstance(this.currentAccount).updateServerNotificationsSettings(j);
        }
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        this.channelGroupsCreated = false;
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$cleanup$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanup$1() {
        this.openedDialogId = 0L;
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
            FileLog.e(e);
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
                        try {
                            systemNotificationManager.deleteNotificationChannel(id);
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete channel cleanup " + id);
                        }
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setOpenedDialogId$2(long j) {
        this.openedDialogId = j;
    }

    public void setOpenedDialogId(final long j) {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$setOpenedDialogId$2(j);
            }
        });
    }

    public void setOpenedInBubble(final long j, final boolean z) {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$setOpenedInBubble$3(z, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setOpenedInBubble$3(boolean z, long j) {
        if (z) {
            this.openedInBubbleDialogs.add(Long.valueOf(j));
        } else {
            this.openedInBubbleDialogs.remove(Long.valueOf(j));
        }
    }

    public void setLastOnlineFromOtherDevice(final int i) {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$setLastOnlineFromOtherDevice$4(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setLastOnlineFromOtherDevice$4(int i) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set last online from other device = " + i);
        }
        this.lastOnlineFromOtherDevice = i;
    }

    public void removeNotificationsForDialog(long j) {
        processReadMessages(null, j, 0, Integer.MAX_VALUE, false);
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

    /* JADX INFO: Access modifiers changed from: protected */
    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$forceShowPopupForReply$6();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$forceShowPopupForReply$6() {
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if ((!tLRPC$Message.mentioned || !(tLRPC$Message.action instanceof TLRPC$TL_messageActionPinMessage)) && !DialogObject.isEncryptedDialog(dialogId) && (messageObject.messageOwner.peer_id.channel_id == 0 || messageObject.isSupergroup())) {
                arrayList.add(0, messageObject);
            }
        }
        if (arrayList.isEmpty() || AndroidUtilities.needShowPasscode() || SharedConfig.isWaitingForPasscodeEnter) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$forceShowPopupForReply$5(arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$forceShowPopupForReply$5(ArrayList arrayList) {
        this.popupReplyMessages = arrayList;
        Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
        intent.putExtra("force", true);
        intent.putExtra("currentAccount", this.currentAccount);
        intent.setFlags(NUM);
        ApplicationLoader.applicationContext.startActivity(intent);
        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    public void removeDeletedMessagesFromNotifications(final LongSparseArray<ArrayList<Integer>> longSparseArray, final boolean z) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$9(longSparseArray, z, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$9(LongSparseArray longSparseArray, boolean z, final ArrayList arrayList) {
        long j;
        Integer num;
        LongSparseArray longSparseArray2 = longSparseArray;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        int i2 = 0;
        while (i2 < longSparseArray.size()) {
            long keyAt = longSparseArray2.keyAt(i2);
            SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(keyAt);
            if (sparseArray != null) {
                ArrayList arrayList2 = (ArrayList) longSparseArray2.get(keyAt);
                int size = arrayList2.size();
                int i3 = 0;
                while (i3 < size) {
                    int intValue = ((Integer) arrayList2.get(i3)).intValue();
                    MessageObject messageObject = sparseArray.get(intValue);
                    if (messageObject == null || (z && !messageObject.isReactionPush)) {
                        j = keyAt;
                    } else {
                        j = keyAt;
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
                        sparseArray.remove(intValue);
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList.add(messageObject);
                    }
                    i3++;
                    keyAt = j;
                }
                long j2 = keyAt;
                if (sparseArray.size() == 0) {
                    this.pushMessagesDict.remove(j2);
                }
            }
            i2++;
            longSparseArray2 = longSparseArray;
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$7(arrayList);
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
            final int size2 = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedMessagesFromNotifications$8(size2);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$7(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$8(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void removeDeletedHisoryFromNotifications(final LongSparseIntArray longSparseIntArray) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda35
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$12(longSparseIntArray, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$12(LongSparseIntArray longSparseIntArray, final ArrayList arrayList) {
        boolean z;
        Integer num;
        int i = this.total_unread_count;
        getAccountInstance().getNotificationsSettings();
        Integer num2 = 0;
        int i2 = 0;
        while (true) {
            z = true;
            if (i2 >= longSparseIntArray.size()) {
                break;
            }
            long keyAt = longSparseIntArray.keyAt(i2);
            long j = -keyAt;
            long j2 = longSparseIntArray.get(keyAt);
            Integer num3 = this.pushDialogs.get(j);
            if (num3 == null) {
                num3 = num2;
            }
            Integer num4 = num3;
            int i3 = 0;
            while (i3 < this.pushMessages.size()) {
                MessageObject messageObject = this.pushMessages.get(i3);
                if (messageObject.getDialogId() == j) {
                    num = num2;
                    if (messageObject.getId() <= j2) {
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(j);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(j);
                            }
                        }
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(messageObject);
                        i3--;
                        if (isPersonalMessage(messageObject)) {
                            this.personalCount--;
                        }
                        arrayList.add(messageObject);
                        num4 = Integer.valueOf(num4.intValue() - 1);
                    }
                } else {
                    num = num2;
                }
                i3++;
                num2 = num;
            }
            Integer num5 = num2;
            if (num4.intValue() <= 0) {
                this.smartNotificationsDialogs.remove(j);
                num4 = num5;
            }
            if (!num4.equals(num3)) {
                int intValue = this.total_unread_count - num3.intValue();
                this.total_unread_count = intValue;
                this.total_unread_count = intValue + num4.intValue();
                this.pushDialogs.put(j, num4);
            }
            if (num4.intValue() == 0) {
                this.pushDialogs.remove(j);
                this.pushDialogsOverrideMention.remove(j);
            }
            i2++;
            num2 = num5;
        }
        if (arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$10(arrayList);
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
            final int size = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$removeDeletedHisoryFromNotifications$11(size);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$10(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$11(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processReadMessages(final LongSparseIntArray longSparseIntArray, final long j, final int i, final int i2, final boolean z) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda36
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processReadMessages$14(longSparseIntArray, arrayList, j, i2, i, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x00d7, code lost:
        r8 = false;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processReadMessages$14(org.telegram.messenger.support.LongSparseIntArray r19, final java.util.ArrayList r20, long r21, int r23, int r24, boolean r25) {
        /*
            Method dump skipped, instructions count: 304
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processReadMessages$14(org.telegram.messenger.support.LongSparseIntArray, java.util.ArrayList, long, int, int, boolean):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processReadMessages$13(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0056, code lost:
        if (r0 == 2) goto L30;
     */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0070  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int addToPopupMessages(java.util.ArrayList<org.telegram.messenger.MessageObject> r4, org.telegram.messenger.MessageObject r5, long r6, boolean r8, android.content.SharedPreferences r9) {
        /*
            r3 = this;
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            r1 = 0
            if (r0 != 0) goto L58
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "custom_"
            r0.append(r2)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            boolean r0 = r9.getBoolean(r0, r1)
            if (r0 == 0) goto L34
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "popup_"
            r0.append(r2)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            int r0 = r9.getInt(r0, r1)
            goto L35
        L34:
            r0 = 0
        L35:
            if (r0 != 0) goto L50
            if (r8 == 0) goto L40
            java.lang.String r6 = "popupChannel"
            int r0 = r9.getInt(r6, r1)
            goto L59
        L40:
            boolean r6 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r6 == 0) goto L49
            java.lang.String r6 = "popupGroup"
            goto L4b
        L49:
            java.lang.String r6 = "popupAll"
        L4b:
            int r0 = r9.getInt(r6, r1)
            goto L59
        L50:
            r6 = 1
            if (r0 != r6) goto L55
            r0 = 3
            goto L59
        L55:
            r6 = 2
            if (r0 != r6) goto L59
        L58:
            r0 = 0
        L59:
            if (r0 == 0) goto L6e
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            long r6 = r6.channel_id
            r8 = 0
            int r2 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r2 == 0) goto L6e
            boolean r6 = r5.isSupergroup()
            if (r6 != 0) goto L6e
            r0 = 0
        L6e:
            if (r0 == 0) goto L73
            r4.add(r1, r5)
        L73:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.addToPopupMessages(java.util.ArrayList, org.telegram.messenger.MessageObject, long, boolean, android.content.SharedPreferences):int");
    }

    public void processEditedMessages(final LongSparseArray<ArrayList<MessageObject>> longSparseArray) {
        if (longSparseArray.size() == 0) {
            return;
        }
        new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processEditedMessages$15(longSparseArray);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
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
                    SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(j2);
                    if (sparseArray == null) {
                        break;
                    }
                    MessageObject messageObject2 = sparseArray.get(messageObject.getId());
                    if (messageObject2 != null && messageObject2.isReactionPush) {
                        messageObject2 = null;
                    }
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

    public void processNewMessages(final ArrayList<MessageObject> arrayList, final boolean z, final boolean z2, final CountDownLatch countDownLatch) {
        if (!arrayList.isEmpty()) {
            final ArrayList arrayList2 = new ArrayList(0);
            notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$processNewMessages$18(arrayList, arrayList2, z2, z, countDownLatch);
                }
            });
        } else if (countDownLatch == null) {
        } else {
            countDownLatch.countDown();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0048, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L20;
     */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00f6  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x013d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$processNewMessages$18(java.util.ArrayList r30, final java.util.ArrayList r31, boolean r32, boolean r33, java.util.concurrent.CountDownLatch r34) {
        /*
            Method dump skipped, instructions count: 812
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processNewMessages$18(java.util.ArrayList, java.util.ArrayList, boolean, boolean, java.util.concurrent.CountDownLatch):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processNewMessages$16(ArrayList arrayList, int i) {
        this.popupMessages.addAll(0, arrayList);
        if (ApplicationLoader.mainInterfacePaused || !ApplicationLoader.isScreenOn) {
            if (i != 3 && ((i != 1 || !ApplicationLoader.isScreenOn) && (i != 2 || ApplicationLoader.isScreenOn))) {
                return;
            }
            Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
            intent.setFlags(NUM);
            try {
                ApplicationLoader.applicationContext.startActivity(intent);
            } catch (Throwable unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processNewMessages$17(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(final LongSparseIntArray longSparseIntArray) {
        final ArrayList arrayList = new ArrayList();
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda34
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processDialogsUpdateRead$21(longSparseIntArray, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processDialogsUpdateRead$21(LongSparseIntArray longSparseIntArray, final ArrayList arrayList) {
        boolean z;
        boolean z2;
        Integer num;
        TLRPC$Chat chat;
        int i = this.total_unread_count;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        int i2 = 0;
        while (true) {
            z = true;
            if (i2 >= longSparseIntArray.size()) {
                break;
            }
            long keyAt = longSparseIntArray.keyAt(i2);
            Integer num2 = this.pushDialogs.get(keyAt);
            int i3 = longSparseIntArray.get(keyAt);
            if (DialogObject.isChatDialog(keyAt) && ((chat = getMessagesController().getChat(Long.valueOf(-keyAt))) == null || chat.min || ChatObject.isNotInChat(chat))) {
                i3 = 0;
            }
            int notifyOverride = getNotifyOverride(notificationsSettings, keyAt);
            if (notifyOverride == -1) {
                z2 = isGlobalNotificationsEnabled(keyAt);
            } else {
                z2 = notifyOverride != 2;
            }
            if (this.notifyCheck && !z2 && (num = this.pushDialogsOverrideMention.get(keyAt)) != null && num.intValue() != 0) {
                i3 = num.intValue();
                z2 = true;
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
            if ((z2 || i3 == 0) && num2 != null) {
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
                        SparseArray<MessageObject> sparseArray = this.pushMessagesDict.get(j2);
                        if (sparseArray != null) {
                            sparseArray.remove(messageObject.getId());
                            if (sparseArray.size() == 0) {
                                this.pushMessagesDict.remove(j2);
                            }
                        }
                        arrayList.add(messageObject);
                    }
                    i4++;
                }
            } else if (z2) {
                this.total_unread_count += i3;
                this.pushDialogs.put(keyAt, Integer.valueOf(i3));
            }
            i2++;
        }
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$processDialogsUpdateRead$19(arrayList);
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
            final int size = this.pushDialogs.size();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$processDialogsUpdateRead$20(size);
                }
            });
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processDialogsUpdateRead$19(ArrayList arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            this.popupMessages.remove(arrayList.get(i));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processDialogsUpdateRead$20(int i) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    public void processLoadedUnreadMessages(final LongSparseArray<Integer> longSparseArray, final ArrayList<TLRPC$Message> arrayList, final ArrayList<MessageObject> arrayList2, ArrayList<TLRPC$User> arrayList3, ArrayList<TLRPC$Chat> arrayList4, ArrayList<TLRPC$EncryptedChat> arrayList5) {
        getMessagesController().putUsers(arrayList3, true);
        getMessagesController().putChats(arrayList4, true);
        getMessagesController().putEncryptedChats(arrayList5, true);
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda32
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processLoadedUnreadMessages$23(arrayList, longSparseArray, arrayList2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedUnreadMessages$23(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2) {
        boolean z;
        SharedPreferences sharedPreferences;
        LongSparseArray longSparseArray2;
        boolean z2;
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader;
        int i;
        SparseArray<MessageObject> sparseArray;
        long j;
        boolean z3;
        boolean z4;
        SparseArray<MessageObject> sparseArray2;
        ArrayList arrayList3 = arrayList;
        this.pushDialogs.clear();
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        boolean z5 = false;
        this.total_unread_count = 0;
        this.personalCount = 0;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        LongSparseArray longSparseArray3 = new LongSparseArray();
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
                        SparseArray<MessageObject> sparseArray3 = this.pushMessagesDict.get(j4);
                        if (sparseArray3 == null || sparseArray3.indexOfKey(tLRPC$Message.id) < 0) {
                            MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$Message, z5, z5);
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
                            int indexOfKey = longSparseArray3.indexOfKey(j);
                            if (indexOfKey >= 0) {
                                z4 = ((Boolean) longSparseArray3.valueAt(indexOfKey)).booleanValue();
                            } else {
                                int notifyOverride = getNotifyOverride(notificationsSettings, j);
                                if (notifyOverride == -1) {
                                    z3 = isGlobalNotificationsEnabled(j);
                                } else {
                                    z3 = notifyOverride != 2;
                                }
                                z4 = z3;
                                longSparseArray3.put(j, Boolean.valueOf(z4));
                            }
                            if (z4 && (j != this.openedDialogId || !ApplicationLoader.isScreenOn)) {
                                if (sparseArray == null) {
                                    sparseArray2 = new SparseArray<>();
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
                            z5 = false;
                            j2 = 0;
                            i2 = 1;
                        }
                    }
                }
                i = i3;
                i3 = i + 1;
                arrayList3 = arrayList;
                z5 = false;
                j2 = 0;
                i2 = 1;
            }
        }
        for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
            long keyAt = longSparseArray.keyAt(i4);
            int indexOfKey2 = longSparseArray3.indexOfKey(keyAt);
            if (indexOfKey2 >= 0) {
                z2 = ((Boolean) longSparseArray3.valueAt(indexOfKey2)).booleanValue();
            } else {
                int notifyOverride2 = getNotifyOverride(notificationsSettings, keyAt);
                if (notifyOverride2 == -1) {
                    z2 = isGlobalNotificationsEnabled(keyAt);
                } else {
                    z2 = notifyOverride2 != 2;
                }
                longSparseArray3.put(keyAt, Boolean.valueOf(z2));
            }
            if (z2) {
                int intValue = ((Integer) longSparseArray.valueAt(i4)).intValue();
                this.pushDialogs.put(keyAt, Integer.valueOf(intValue));
                this.total_unread_count += intValue;
            }
        }
        if (arrayList2 != null) {
            int i5 = 0;
            while (i5 < arrayList2.size()) {
                MessageObject messageObject2 = (MessageObject) arrayList2.get(i5);
                int id = messageObject2.getId();
                if (this.pushMessagesDict.indexOfKey(id) >= 0) {
                    sharedPreferences = notificationsSettings;
                    longSparseArray2 = longSparseArray3;
                } else {
                    if (isPersonalMessage(messageObject2)) {
                        this.personalCount++;
                    }
                    long dialogId2 = messageObject2.getDialogId();
                    TLRPC$Message tLRPC$Message2 = messageObject2.messageOwner;
                    long j5 = tLRPC$Message2.random_id;
                    long fromChatId = tLRPC$Message2.mentioned ? messageObject2.getFromChatId() : dialogId2;
                    int indexOfKey3 = longSparseArray3.indexOfKey(fromChatId);
                    if (indexOfKey3 >= 0) {
                        z = ((Boolean) longSparseArray3.valueAt(indexOfKey3)).booleanValue();
                    } else {
                        int notifyOverride3 = getNotifyOverride(notificationsSettings, fromChatId);
                        if (notifyOverride3 == -1) {
                            z = isGlobalNotificationsEnabled(fromChatId);
                        } else {
                            z = notifyOverride3 != 2;
                        }
                        longSparseArray3.put(fromChatId, Boolean.valueOf(z));
                    }
                    sharedPreferences = notificationsSettings;
                    if (z) {
                        longSparseArray2 = longSparseArray3;
                        if (fromChatId != this.openedDialogId || !ApplicationLoader.isScreenOn) {
                            if (id != 0) {
                                long j6 = messageObject2.messageOwner.peer_id.channel_id;
                                long j7 = j6 != 0 ? -j6 : 0L;
                                SparseArray<MessageObject> sparseArray4 = this.pushMessagesDict.get(j7);
                                if (sparseArray4 == null) {
                                    sparseArray4 = new SparseArray<>();
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
                            longSparseArray3 = longSparseArray2;
                        }
                    } else {
                        longSparseArray2 = longSparseArray3;
                    }
                }
                i5++;
                notificationsSettings = sharedPreferences;
                longSparseArray3 = longSparseArray2;
            }
        }
        final int size = this.pushDialogs.size();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$processLoadedUnreadMessages$22(size);
            }
        });
        showOrUpdateNotification(SystemClock.elapsedRealtime() / 1000 < 60);
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processLoadedUnreadMessages$22(int i) {
        if (this.total_unread_count == 0) {
            this.popupMessages.clear();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        getNotificationCenter().postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(i));
    }

    private int getTotalAllUnreadCount() {
        int size;
        int i;
        int i2 = 0;
        for (int i3 = 0; i3 < 4; i3++) {
            if (UserConfig.getInstance(i3).isClientActivated()) {
                NotificationsController notificationsController = getInstance(i3);
                if (notificationsController.showBadgeNumber) {
                    if (notificationsController.showBadgeMessages) {
                        if (notificationsController.showBadgeMuted) {
                            try {
                                ArrayList arrayList = new ArrayList(MessagesController.getInstance(i3).allDialogs);
                                int size2 = arrayList.size();
                                for (int i4 = 0; i4 < size2; i4++) {
                                    TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) arrayList.get(i4);
                                    if ((tLRPC$Dialog == null || !DialogObject.isChatDialog(tLRPC$Dialog.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog.id)))) && tLRPC$Dialog != null && (i = tLRPC$Dialog.unread_count) != 0) {
                                        i2 += i;
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else {
                            size = notificationsController.total_unread_count;
                        }
                    } else if (notificationsController.showBadgeMuted) {
                        try {
                            int size3 = MessagesController.getInstance(i3).allDialogs.size();
                            for (int i5 = 0; i5 < size3; i5++) {
                                TLRPC$Dialog tLRPC$Dialog2 = MessagesController.getInstance(i3).allDialogs.get(i5);
                                if ((!DialogObject.isChatDialog(tLRPC$Dialog2.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog2.id)))) && tLRPC$Dialog2.unread_count != 0) {
                                    i2++;
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    } else {
                        size = notificationsController.pushDialogs.size();
                    }
                    i2 += size;
                }
            }
        }
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateBadge$24() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$updateBadge$24();
            }
        });
    }

    private void setBadge(int i) {
        if (this.lastBadgeCount == i) {
            return;
        }
        this.lastBadgeCount = i;
        NotificationBadge.applyCount(i);
    }

    /* JADX WARN: Code restructure failed: missing block: B:154:0x024f, code lost:
        if (r12.getBoolean("EnablePreviewAll", true) == false) goto L773;
     */
    /* JADX WARN: Code restructure failed: missing block: B:161:0x025d, code lost:
        if (r12.getBoolean("EnablePreviewGroup", r15) != false) goto L135;
     */
    /* JADX WARN: Code restructure failed: missing block: B:164:0x0267, code lost:
        if (r12.getBoolean(r24, r15) != false) goto L135;
     */
    /* JADX WARN: Code restructure failed: missing block: B:165:0x0269, code lost:
        r1 = r26.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:166:0x0275, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L663;
     */
    /* JADX WARN: Code restructure failed: missing block: B:167:0x0277, code lost:
        r27[0] = null;
        r2 = r1.action;
     */
    /* JADX WARN: Code restructure failed: missing block: B:168:0x027f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) == false) goto L141;
     */
    /* JADX WARN: Code restructure failed: missing block: B:170:0x0287, code lost:
        return r26.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:172:0x028a, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L661;
     */
    /* JADX WARN: Code restructure failed: missing block: B:174:0x028e, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L145;
     */
    /* JADX WARN: Code restructure failed: missing block: B:177:0x0294, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L149;
     */
    /* JADX WARN: Code restructure failed: missing block: B:179:0x02a4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", org.telegram.messenger.R.string.NotificationContactNewPhoto, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:181:0x02a8, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L153;
     */
    /* JADX WARN: Code restructure failed: missing block: B:182:0x02aa, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", org.telegram.messenger.R.string.formatDateAtTime, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(r26.messageOwner.date * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(r26.messageOwner.date * 1000));
        r2 = org.telegram.messenger.R.string.NotificationUnrecognizedDevice;
        r0 = r26.messageOwner.action;
     */
    /* JADX WARN: Code restructure failed: missing block: B:183:0x0306, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", r2, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARN: Code restructure failed: missing block: B:185:0x0309, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L659;
     */
    /* JADX WARN: Code restructure failed: missing block: B:187:0x030d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L157;
     */
    /* JADX WARN: Code restructure failed: missing block: B:190:0x0313, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L165;
     */
    /* JADX WARN: Code restructure failed: missing block: B:192:0x0317, code lost:
        if (r2.video == false) goto L163;
     */
    /* JADX WARN: Code restructure failed: missing block: B:194:0x0321, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageVideoIncomingMissed", org.telegram.messenger.R.string.CallMessageVideoIncomingMissed);
     */
    /* JADX WARN: Code restructure failed: missing block: B:196:0x032a, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", org.telegram.messenger.R.string.CallMessageIncomingMissed);
     */
    /* JADX WARN: Code restructure failed: missing block: B:198:0x032d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L212;
     */
    /* JADX WARN: Code restructure failed: missing block: B:199:0x032f, code lost:
        r3 = r2.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:200:0x0335, code lost:
        if (r3 != 0) goto L172;
     */
    /* JADX WARN: Code restructure failed: missing block: B:202:0x033e, code lost:
        if (r2.users.size() != 1) goto L172;
     */
    /* JADX WARN: Code restructure failed: missing block: B:203:0x0340, code lost:
        r3 = r26.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:205:0x0355, code lost:
        if (r3 == 0) goto L197;
     */
    /* JADX WARN: Code restructure failed: missing block: B:207:0x035f, code lost:
        if (r26.messageOwner.peer_id.channel_id == 0) goto L180;
     */
    /* JADX WARN: Code restructure failed: missing block: B:209:0x0363, code lost:
        if (r8.megagroup != false) goto L180;
     */
    /* JADX WARN: Code restructure failed: missing block: B:211:0x0378, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", org.telegram.messenger.R.string.ChannelAddedByNotification, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:213:0x037b, code lost:
        if (r3 != r19) goto L184;
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x0390, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", org.telegram.messenger.R.string.NotificationInvitedToGroup, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:216:0x0391, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:217:0x039d, code lost:
        if (r0 != null) goto L187;
     */
    /* JADX WARN: Code restructure failed: missing block: B:218:0x039f, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:221:0x03a5, code lost:
        if (r9 != r0.id) goto L195;
     */
    /* JADX WARN: Code restructure failed: missing block: B:223:0x03a9, code lost:
        if (r8.megagroup == false) goto L193;
     */
    /* JADX WARN: Code restructure failed: missing block: B:225:0x03be, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", org.telegram.messenger.R.string.NotificationGroupAddSelfMega, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:227:0x03d2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", org.telegram.messenger.R.string.NotificationGroupAddSelf, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:229:0x03ec, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.R.string.NotificationGroupAddMember, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:230:0x03ed, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:232:0x03fd, code lost:
        if (r2 >= r26.messageOwner.action.users.size()) goto L209;
     */
    /* JADX WARN: Code restructure failed: missing block: B:233:0x03ff, code lost:
        r3 = getMessagesController().getUser(r26.messageOwner.action.users.get(r2));
     */
    /* JADX WARN: Code restructure failed: missing block: B:234:0x0413, code lost:
        if (r3 == null) goto L208;
     */
    /* JADX WARN: Code restructure failed: missing block: B:235:0x0415, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:236:0x041d, code lost:
        if (r1.length() == 0) goto L205;
     */
    /* JADX WARN: Code restructure failed: missing block: B:237:0x041f, code lost:
        r1.append(", ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:238:0x0424, code lost:
        r1.append(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:239:0x0427, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:241:0x0443, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.R.string.NotificationGroupAddMember, r7, r8.title, r1.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:243:0x0447, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L216;
     */
    /* JADX WARN: Code restructure failed: missing block: B:245:0x045b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", org.telegram.messenger.R.string.NotificationGroupCreatedCall, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:247:0x045e, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) == false) goto L220;
     */
    /* JADX WARN: Code restructure failed: missing block: B:249:0x0466, code lost:
        return r26.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:251:0x0469, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L253;
     */
    /* JADX WARN: Code restructure failed: missing block: B:252:0x046b, code lost:
        r3 = r2.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:253:0x0471, code lost:
        if (r3 != 0) goto L227;
     */
    /* JADX WARN: Code restructure failed: missing block: B:255:0x047a, code lost:
        if (r2.users.size() != 1) goto L227;
     */
    /* JADX WARN: Code restructure failed: missing block: B:256:0x047c, code lost:
        r3 = r26.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:258:0x0491, code lost:
        if (r3 == 0) goto L238;
     */
    /* JADX WARN: Code restructure failed: missing block: B:260:0x0495, code lost:
        if (r3 != r19) goto L233;
     */
    /* JADX WARN: Code restructure failed: missing block: B:262:0x04aa, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:263:0x04ab, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:264:0x04b7, code lost:
        if (r0 != null) goto L236;
     */
    /* JADX WARN: Code restructure failed: missing block: B:265:0x04b9, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:268:0x04d4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.R.string.NotificationGroupInvitedToCall, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:269:0x04d5, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:271:0x04e5, code lost:
        if (r2 >= r26.messageOwner.action.users.size()) goto L250;
     */
    /* JADX WARN: Code restructure failed: missing block: B:272:0x04e7, code lost:
        r3 = getMessagesController().getUser(r26.messageOwner.action.users.get(r2));
     */
    /* JADX WARN: Code restructure failed: missing block: B:273:0x04fb, code lost:
        if (r3 == null) goto L249;
     */
    /* JADX WARN: Code restructure failed: missing block: B:274:0x04fd, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:275:0x0505, code lost:
        if (r1.length() == 0) goto L246;
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x0507, code lost:
        r1.append(", ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:277:0x050c, code lost:
        r1.append(r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:278:0x050f, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:280:0x052b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.R.string.NotificationGroupInvitedToCall, r7, r8.title, r1.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:282:0x052f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L257;
     */
    /* JADX WARN: Code restructure failed: missing block: B:284:0x0544, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", org.telegram.messenger.R.string.NotificationInvitedToGroupByLink, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:286:0x054a, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L261;
     */
    /* JADX WARN: Code restructure failed: missing block: B:288:0x055c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", org.telegram.messenger.R.string.NotificationEditedGroupName, r7, r2.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:290:0x055f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L643;
     */
    /* JADX WARN: Code restructure failed: missing block: B:292:0x0563, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L265;
     */
    /* JADX WARN: Code restructure failed: missing block: B:295:0x0569, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L280;
     */
    /* JADX WARN: Code restructure failed: missing block: B:296:0x056b, code lost:
        r1 = r2.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:297:0x056f, code lost:
        if (r1 != r19) goto L271;
     */
    /* JADX WARN: Code restructure failed: missing block: B:299:0x0584, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", org.telegram.messenger.R.string.NotificationGroupKickYou, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:301:0x058a, code lost:
        if (r1 != r9) goto L275;
     */
    /* JADX WARN: Code restructure failed: missing block: B:303:0x059c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", org.telegram.messenger.R.string.NotificationGroupLeftMember, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:304:0x059d, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r26.messageOwner.action.user_id));
     */
    /* JADX WARN: Code restructure failed: missing block: B:305:0x05af, code lost:
        if (r0 != null) goto L278;
     */
    /* JADX WARN: Code restructure failed: missing block: B:306:0x05b1, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:309:0x05cd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", org.telegram.messenger.R.string.NotificationGroupKickMember, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:311:0x05d0, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L284;
     */
    /* JADX WARN: Code restructure failed: missing block: B:313:0x05d8, code lost:
        return r26.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:315:0x05db, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L288;
     */
    /* JADX WARN: Code restructure failed: missing block: B:317:0x05e3, code lost:
        return r26.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:319:0x05e6, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L292;
     */
    /* JADX WARN: Code restructure failed: missing block: B:321:0x05f8, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.R.string.ActionMigrateFromGroupNotify, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:323:0x05fd, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L296;
     */
    /* JADX WARN: Code restructure failed: missing block: B:325:0x060d, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.R.string.ActionMigrateFromGroupNotify, r2.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:327:0x0610, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L300;
     */
    /* JADX WARN: Code restructure failed: missing block: B:329:0x0618, code lost:
        return r26.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:331:0x061b, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L622;
     */
    /* JADX WARN: Code restructure failed: missing block: B:333:0x0621, code lost:
        if (r8 == null) goto L412;
     */
    /* JADX WARN: Code restructure failed: missing block: B:335:0x0627, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r8) == false) goto L308;
     */
    /* JADX WARN: Code restructure failed: missing block: B:337:0x062b, code lost:
        if (r8.megagroup == false) goto L412;
     */
    /* JADX WARN: Code restructure failed: missing block: B:338:0x062d, code lost:
        r0 = r26.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:339:0x062f, code lost:
        if (r0 != null) goto L312;
     */
    /* JADX WARN: Code restructure failed: missing block: B:341:0x0644, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.R.string.NotificationActionPinnedNoText, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:343:0x064c, code lost:
        if (r0.isMusic() == false) goto L316;
     */
    /* JADX WARN: Code restructure failed: missing block: B:345:0x065e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", org.telegram.messenger.R.string.NotificationActionPinnedMusic, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:347:0x0665, code lost:
        if (r0.isVideo() == false) goto L326;
     */
    /* JADX WARN: Code restructure failed: missing block: B:349:0x066b, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L324;
     */
    /* JADX WARN: Code restructure failed: missing block: B:351:0x0675, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L324;
     */
    /* JADX WARN: Code restructure failed: missing block: B:353:0x069e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r7, "📹 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:355:0x06b2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", org.telegram.messenger.R.string.NotificationActionPinnedVideo, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:357:0x06b7, code lost:
        if (r0.isGif() == false) goto L336;
     */
    /* JADX WARN: Code restructure failed: missing block: B:359:0x06bd, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L334;
     */
    /* JADX WARN: Code restructure failed: missing block: B:361:0x06c7, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L334;
     */
    /* JADX WARN: Code restructure failed: missing block: B:363:0x06f0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r7, "🎬 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:365:0x0704, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", org.telegram.messenger.R.string.NotificationActionPinnedGif, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:367:0x070c, code lost:
        if (r0.isVoice() == false) goto L340;
     */
    /* JADX WARN: Code restructure failed: missing block: B:369:0x071e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", org.telegram.messenger.R.string.NotificationActionPinnedVoice, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:371:0x0723, code lost:
        if (r0.isRoundVideo() == false) goto L344;
     */
    /* JADX WARN: Code restructure failed: missing block: B:373:0x0735, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", org.telegram.messenger.R.string.NotificationActionPinnedRound, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:375:0x073a, code lost:
        if (r0.isSticker() != false) goto L406;
     */
    /* JADX WARN: Code restructure failed: missing block: B:377:0x0740, code lost:
        if (r0.isAnimatedSticker() == false) goto L348;
     */
    /* JADX WARN: Code restructure failed: missing block: B:379:0x0744, code lost:
        r3 = r0.messageOwner;
        r5 = r3.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:380:0x074a, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L358;
     */
    /* JADX WARN: Code restructure failed: missing block: B:382:0x0750, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L356;
     */
    /* JADX WARN: Code restructure failed: missing block: B:384:0x0758, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L356;
     */
    /* JADX WARN: Code restructure failed: missing block: B:386:0x0781, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r7, "📎 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:388:0x0795, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", org.telegram.messenger.R.string.NotificationActionPinnedFile, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:390:0x0798, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L404;
     */
    /* JADX WARN: Code restructure failed: missing block: B:392:0x079c, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L362;
     */
    /* JADX WARN: Code restructure failed: missing block: B:395:0x07a2, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L366;
     */
    /* JADX WARN: Code restructure failed: missing block: B:397:0x07b7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", org.telegram.messenger.R.string.NotificationActionPinnedGeoLive, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:399:0x07bc, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L370;
     */
    /* JADX WARN: Code restructure failed: missing block: B:400:0x07be, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:401:0x07dc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", org.telegram.messenger.R.string.NotificationActionPinnedContact2, r7, r8.title, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:403:0x07df, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L378;
     */
    /* JADX WARN: Code restructure failed: missing block: B:404:0x07e1, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:405:0x07e7, code lost:
        if (r0.quiz == false) goto L376;
     */
    /* JADX WARN: Code restructure failed: missing block: B:407:0x0801, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", org.telegram.messenger.R.string.NotificationActionPinnedQuiz2, r7, r8.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:409:0x081a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", org.telegram.messenger.R.string.NotificationActionPinnedPoll2, r7, r8.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:411:0x081d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L388;
     */
    /* JADX WARN: Code restructure failed: missing block: B:413:0x0823, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L386;
     */
    /* JADX WARN: Code restructure failed: missing block: B:415:0x082b, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L386;
     */
    /* JADX WARN: Code restructure failed: missing block: B:417:0x0854, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r7, "\u1f5bc " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:419:0x0868, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", org.telegram.messenger.R.string.NotificationActionPinnedPhoto, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:421:0x086e, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L392;
     */
    /* JADX WARN: Code restructure failed: missing block: B:423:0x0880, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", org.telegram.messenger.R.string.NotificationActionPinnedGame, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:424:0x0881, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:425:0x0883, code lost:
        if (r3 == null) goto L402;
     */
    /* JADX WARN: Code restructure failed: missing block: B:427:0x0889, code lost:
        if (r3.length() <= 0) goto L402;
     */
    /* JADX WARN: Code restructure failed: missing block: B:428:0x088b, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:429:0x0891, code lost:
        if (r0.length() <= 20) goto L401;
     */
    /* JADX WARN: Code restructure failed: missing block: B:430:0x0893, code lost:
        r3 = new java.lang.StringBuilder();
        r5 = 0;
        r3.append((java.lang.Object) r0.subSequence(0, 20));
        r3.append("...");
        r0 = r3.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:431:0x08a8, code lost:
        r5 = 0;
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:432:0x08a9, code lost:
        r1 = org.telegram.messenger.R.string.NotificationActionPinnedText;
        r2 = new java.lang.Object[3];
        r2[r5] = r7;
        r2[1] = r0;
        r2[2] = r8.title;
     */
    /* JADX WARN: Code restructure failed: missing block: B:433:0x08bc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", r1, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:435:0x08d0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.R.string.NotificationActionPinnedNoText, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:437:0x08e4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", org.telegram.messenger.R.string.NotificationActionPinnedGeo, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:438:0x08e5, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:439:0x08eb, code lost:
        if (r0 == null) goto L410;
     */
    /* JADX WARN: Code restructure failed: missing block: B:441:0x0901, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji, r7, r8.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:443:0x0913, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", org.telegram.messenger.R.string.NotificationActionPinnedSticker, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:445:0x0915, code lost:
        if (r8 == null) goto L518;
     */
    /* JADX WARN: Code restructure failed: missing block: B:446:0x0917, code lost:
        r0 = r26.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:447:0x0919, code lost:
        if (r0 != null) goto L418;
     */
    /* JADX WARN: Code restructure failed: missing block: B:449:0x092a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:451:0x0930, code lost:
        if (r0.isMusic() == false) goto L422;
     */
    /* JADX WARN: Code restructure failed: missing block: B:453:0x0940, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", org.telegram.messenger.R.string.NotificationActionPinnedMusicChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:455:0x0947, code lost:
        if (r0.isVideo() == false) goto L432;
     */
    /* JADX WARN: Code restructure failed: missing block: B:457:0x094d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L430;
     */
    /* JADX WARN: Code restructure failed: missing block: B:459:0x0957, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L430;
     */
    /* JADX WARN: Code restructure failed: missing block: B:461:0x097d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r8.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:463:0x098e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:465:0x0993, code lost:
        if (r0.isGif() == false) goto L442;
     */
    /* JADX WARN: Code restructure failed: missing block: B:467:0x0999, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L440;
     */
    /* JADX WARN: Code restructure failed: missing block: B:469:0x09a3, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L440;
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x00b3, code lost:
        if (r12.getBoolean("EnablePreviewGroup", true) != false) goto L45;
     */
    /* JADX WARN: Code restructure failed: missing block: B:471:0x09c9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r8.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:473:0x09da, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", org.telegram.messenger.R.string.NotificationActionPinnedGifChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:475:0x09e1, code lost:
        if (r0.isVoice() == false) goto L446;
     */
    /* JADX WARN: Code restructure failed: missing block: B:477:0x09f1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:479:0x09f6, code lost:
        if (r0.isRoundVideo() == false) goto L450;
     */
    /* JADX WARN: Code restructure failed: missing block: B:481:0x0a06, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:483:0x0a0b, code lost:
        if (r0.isSticker() != false) goto L512;
     */
    /* JADX WARN: Code restructure failed: missing block: B:485:0x0a11, code lost:
        if (r0.isAnimatedSticker() == false) goto L454;
     */
    /* JADX WARN: Code restructure failed: missing block: B:487:0x0a15, code lost:
        r3 = r0.messageOwner;
        r5 = r3.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:488:0x0a1b, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L464;
     */
    /* JADX WARN: Code restructure failed: missing block: B:490:0x0a21, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L462;
     */
    /* JADX WARN: Code restructure failed: missing block: B:492:0x0a29, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L462;
     */
    /* JADX WARN: Code restructure failed: missing block: B:494:0x0a4f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r8.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:496:0x0a60, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", org.telegram.messenger.R.string.NotificationActionPinnedFileChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:498:0x0a63, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L510;
     */
    /* JADX WARN: Code restructure failed: missing block: B:500:0x0a67, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L468;
     */
    /* JADX WARN: Code restructure failed: missing block: B:503:0x0a6d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L472;
     */
    /* JADX WARN: Code restructure failed: missing block: B:505:0x0a7f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:507:0x0a83, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L476;
     */
    /* JADX WARN: Code restructure failed: missing block: B:508:0x0a85, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:509:0x0aa1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2, r8.title, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:511:0x0aa4, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L484;
     */
    /* JADX WARN: Code restructure failed: missing block: B:512:0x0aa6, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:513:0x0aac, code lost:
        if (r0.quiz == false) goto L482;
     */
    /* JADX WARN: Code restructure failed: missing block: B:515:0x0ac3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2, r8.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:517:0x0ad9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2, r8.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:519:0x0adc, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L494;
     */
    /* JADX WARN: Code restructure failed: missing block: B:521:0x0ae2, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L492;
     */
    /* JADX WARN: Code restructure failed: missing block: B:523:0x0aea, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L492;
     */
    /* JADX WARN: Code restructure failed: missing block: B:525:0x0b10, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r8.title, "\u1f5bc " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:527:0x0b21, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:529:0x0b26, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L498;
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x00bf, code lost:
        if (r12.getBoolean("EnablePreviewChannel", r2) == false) goto L50;
     */
    /* JADX WARN: Code restructure failed: missing block: B:531:0x0b36, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", org.telegram.messenger.R.string.NotificationActionPinnedGameChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:532:0x0b37, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:533:0x0b39, code lost:
        if (r3 == null) goto L508;
     */
    /* JADX WARN: Code restructure failed: missing block: B:535:0x0b3f, code lost:
        if (r3.length() <= 0) goto L508;
     */
    /* JADX WARN: Code restructure failed: missing block: B:536:0x0b41, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:537:0x0b47, code lost:
        if (r0.length() <= 20) goto L507;
     */
    /* JADX WARN: Code restructure failed: missing block: B:538:0x0b49, code lost:
        r3 = new java.lang.StringBuilder();
        r9 = 0;
        r3.append((java.lang.Object) r0.subSequence(0, 20));
        r3.append("...");
        r0 = r3.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:539:0x0b5e, code lost:
        r9 = 0;
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:540:0x0b5f, code lost:
        r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel;
        r2 = new java.lang.Object[2];
        r2[r9] = r8.title;
        r2[1] = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:541:0x0b6f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", r1, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:543:0x0b80, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:545:0x0b91, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:546:0x0b92, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:547:0x0b97, code lost:
        if (r0 == null) goto L516;
     */
    /* JADX WARN: Code restructure failed: missing block: B:549:0x0bab, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel, r8.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:551:0x0bbb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:552:0x0bbc, code lost:
        r0 = r26.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:553:0x0bbf, code lost:
        if (r0 != null) goto L522;
     */
    /* JADX WARN: Code restructure failed: missing block: B:555:0x0bcd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:557:0x0bd2, code lost:
        if (r0.isMusic() == false) goto L526;
     */
    /* JADX WARN: Code restructure failed: missing block: B:559:0x0be0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicUser", org.telegram.messenger.R.string.NotificationActionPinnedMusicUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:561:0x0be7, code lost:
        if (r0.isVideo() == false) goto L536;
     */
    /* JADX WARN: Code restructure failed: missing block: B:563:0x0bed, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L534;
     */
    /* JADX WARN: Code restructure failed: missing block: B:565:0x0bf7, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L534;
     */
    /* JADX WARN: Code restructure failed: missing block: B:567:0x0c1b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r7, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:569:0x0c2a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", org.telegram.messenger.R.string.NotificationActionPinnedVideoUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:571:0x0c2f, code lost:
        if (r0.isGif() == false) goto L546;
     */
    /* JADX WARN: Code restructure failed: missing block: B:573:0x0CLASSNAME, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L544;
     */
    /* JADX WARN: Code restructure failed: missing block: B:575:0x0c3f, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L544;
     */
    /* JADX WARN: Code restructure failed: missing block: B:577:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r7, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:579:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", org.telegram.messenger.R.string.NotificationActionPinnedGifUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:581:0x0CLASSNAME, code lost:
        if (r0.isVoice() == false) goto L550;
     */
    /* JADX WARN: Code restructure failed: missing block: B:583:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", org.telegram.messenger.R.string.NotificationActionPinnedVoiceUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:585:0x0c8c, code lost:
        if (r0.isRoundVideo() == false) goto L554;
     */
    /* JADX WARN: Code restructure failed: missing block: B:587:0x0c9a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", org.telegram.messenger.R.string.NotificationActionPinnedRoundUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:589:0x0c9f, code lost:
        if (r0.isSticker() != false) goto L616;
     */
    /* JADX WARN: Code restructure failed: missing block: B:591:0x0ca5, code lost:
        if (r0.isAnimatedSticker() == false) goto L558;
     */
    /* JADX WARN: Code restructure failed: missing block: B:593:0x0ca9, code lost:
        r3 = r0.messageOwner;
        r5 = r3.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:594:0x0caf, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L568;
     */
    /* JADX WARN: Code restructure failed: missing block: B:596:0x0cb5, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L566;
     */
    /* JADX WARN: Code restructure failed: missing block: B:598:0x0cbd, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L566;
     */
    /* JADX WARN: Code restructure failed: missing block: B:600:0x0ce1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r7, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:602:0x0cf0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", org.telegram.messenger.R.string.NotificationActionPinnedFileUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:604:0x0cf3, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L614;
     */
    /* JADX WARN: Code restructure failed: missing block: B:606:0x0cf7, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L572;
     */
    /* JADX WARN: Code restructure failed: missing block: B:609:0x0cfd, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L576;
     */
    /* JADX WARN: Code restructure failed: missing block: B:611:0x0d0d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:613:0x0d11, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L580;
     */
    /* JADX WARN: Code restructure failed: missing block: B:614:0x0d13, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:615:0x0d2d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", org.telegram.messenger.R.string.NotificationActionPinnedContactUser, r7, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:617:0x0d30, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L588;
     */
    /* JADX WARN: Code restructure failed: missing block: B:618:0x0d32, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:619:0x0d38, code lost:
        if (r0.quiz == false) goto L586;
     */
    /* JADX WARN: Code restructure failed: missing block: B:621:0x0d4d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", org.telegram.messenger.R.string.NotificationActionPinnedQuizUser, r7, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:623:0x0d61, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", org.telegram.messenger.R.string.NotificationActionPinnedPollUser, r7, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:625:0x0d64, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L598;
     */
    /* JADX WARN: Code restructure failed: missing block: B:627:0x0d6a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L596;
     */
    /* JADX WARN: Code restructure failed: missing block: B:629:0x0d72, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L596;
     */
    /* JADX WARN: Code restructure failed: missing block: B:631:0x0d96, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r7, "\u1f5bc " + r0.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:633:0x0da5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", org.telegram.messenger.R.string.NotificationActionPinnedPhotoUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:635:0x0daa, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L602;
     */
    /* JADX WARN: Code restructure failed: missing block: B:637:0x0db8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", org.telegram.messenger.R.string.NotificationActionPinnedGameUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:638:0x0db9, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:639:0x0dbb, code lost:
        if (r3 == null) goto L612;
     */
    /* JADX WARN: Code restructure failed: missing block: B:641:0x0dc1, code lost:
        if (r3.length() <= 0) goto L612;
     */
    /* JADX WARN: Code restructure failed: missing block: B:642:0x0dc3, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:643:0x0dc9, code lost:
        if (r0.length() <= 20) goto L611;
     */
    /* JADX WARN: Code restructure failed: missing block: B:644:0x0dcb, code lost:
        r3 = new java.lang.StringBuilder();
        r5 = 0;
        r3.append((java.lang.Object) r0.subSequence(0, 20));
        r3.append("...");
        r0 = r3.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:645:0x0de0, code lost:
        r5 = 0;
        r0 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:646:0x0de1, code lost:
        r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser;
        r2 = new java.lang.Object[2];
        r2[r5] = r7;
        r2[1] = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:647:0x0def, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", r1, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:649:0x0dfe, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:651:0x0e0d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:652:0x0e0e, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:653:0x0e14, code lost:
        if (r0 == null) goto L620;
     */
    /* JADX WARN: Code restructure failed: missing block: B:655:0x0e25, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiUser, r7, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:657:0x0e32, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerUser, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:659:0x0e35, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) == false) goto L638;
     */
    /* JADX WARN: Code restructure failed: missing block: B:660:0x0e37, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r2).emoticon;
     */
    /* JADX WARN: Code restructure failed: missing block: B:661:0x0e3f, code lost:
        if (android.text.TextUtils.isEmpty(r0) == false) goto L632;
     */
    /* JADX WARN: Code restructure failed: missing block: B:663:0x0e43, code lost:
        if (r3 != r19) goto L630;
     */
    /* JADX WARN: Code restructure failed: missing block: B:667:0x0e67, code lost:
        if (r3 != r19) goto L636;
     */
    /* JADX WARN: Code restructure failed: missing block: B:670:0x0e85, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChangedChatThemeTo", org.telegram.messenger.R.string.ChatThemeChangedTo, r7, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:672:0x0e88, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest) == false) goto L642;
     */
    /* JADX WARN: Code restructure failed: missing block: B:674:0x0e90, code lost:
        return r26.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:675:0x0e91, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:678:0x0e9b, code lost:
        if (r1.peer_id.channel_id == 0) goto L653;
     */
    /* JADX WARN: Code restructure failed: missing block: B:680:0x0e9f, code lost:
        if (r8.megagroup != false) goto L653;
     */
    /* JADX WARN: Code restructure failed: missing block: B:682:0x0ea5, code lost:
        if (r26.isVideoAvatar() == false) goto L651;
     */
    /* JADX WARN: Code restructure failed: missing block: B:684:0x0eb7, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", org.telegram.messenger.R.string.ChannelVideoEditNotification, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:686:0x0ec8, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", org.telegram.messenger.R.string.ChannelPhotoEditNotification, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:688:0x0ece, code lost:
        if (r26.isVideoAvatar() == false) goto L657;
     */
    /* JADX WARN: Code restructure failed: missing block: B:690:0x0ee2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", org.telegram.messenger.R.string.NotificationEditedGroupVideo, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:692:0x0ef5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", org.telegram.messenger.R.string.NotificationEditedGroupPhoto, r7, r8.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:694:0x0efc, code lost:
        return r26.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:696:0x0f0b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", org.telegram.messenger.R.string.NotificationContactJoined, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:698:0x0var_, code lost:
        if (r26.isMediaEmpty() == false) goto L671;
     */
    /* JADX WARN: Code restructure failed: missing block: B:700:0x0f1a, code lost:
        if (android.text.TextUtils.isEmpty(r26.messageOwner.message) != false) goto L669;
     */
    /* JADX WARN: Code restructure failed: missing block: B:702:0x0var_, code lost:
        return replaceSpoilers(r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:704:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString(r23, org.telegram.messenger.R.string.Message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:705:0x0f2a, code lost:
        r1 = r23;
        r2 = r26.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:706:0x0var_, code lost:
        if ((r2.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L685;
     */
    /* JADX WARN: Code restructure failed: missing block: B:708:0x0var_, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L679;
     */
    /* JADX WARN: Code restructure failed: missing block: B:710:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L679;
     */
    /* JADX WARN: Code restructure failed: missing block: B:712:0x0var_, code lost:
        return "\u1f5bc " + replaceSpoilers(r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:714:0x0f5c, code lost:
        if (r26.messageOwner.media.ttl_seconds == 0) goto L683;
     */
    /* JADX WARN: Code restructure failed: missing block: B:716:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", org.telegram.messenger.R.string.AttachDestructingPhoto);
     */
    /* JADX WARN: Code restructure failed: missing block: B:718:0x0f6f, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARN: Code restructure failed: missing block: B:720:0x0var_, code lost:
        if (r26.isVideo() == false) goto L699;
     */
    /* JADX WARN: Code restructure failed: missing block: B:722:0x0f7a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L693;
     */
    /* JADX WARN: Code restructure failed: missing block: B:724:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r26.messageOwner.message) != false) goto L693;
     */
    /* JADX WARN: Code restructure failed: missing block: B:726:0x0var_, code lost:
        return "📹 " + replaceSpoilers(r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:728:0x0fa0, code lost:
        if (r26.messageOwner.media.ttl_seconds == 0) goto L697;
     */
    /* JADX WARN: Code restructure failed: missing block: B:730:0x0faa, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", org.telegram.messenger.R.string.AttachDestructingVideo);
     */
    /* JADX WARN: Code restructure failed: missing block: B:732:0x0fb3, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARN: Code restructure failed: missing block: B:734:0x0fb8, code lost:
        if (r26.isGame() == false) goto L703;
     */
    /* JADX WARN: Code restructure failed: missing block: B:736:0x0fc2, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARN: Code restructure failed: missing block: B:738:0x0fc7, code lost:
        if (r26.isVoice() == false) goto L707;
     */
    /* JADX WARN: Code restructure failed: missing block: B:740:0x0fd1, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARN: Code restructure failed: missing block: B:742:0x0fd6, code lost:
        if (r26.isRoundVideo() == false) goto L711;
     */
    /* JADX WARN: Code restructure failed: missing block: B:744:0x0fe0, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARN: Code restructure failed: missing block: B:746:0x0fe5, code lost:
        if (r26.isMusic() == false) goto L715;
     */
    /* JADX WARN: Code restructure failed: missing block: B:748:0x0fef, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", org.telegram.messenger.R.string.AttachMusic);
     */
    /* JADX WARN: Code restructure failed: missing block: B:749:0x0ff0, code lost:
        r2 = r26.messageOwner.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:750:0x0ff6, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L719;
     */
    /* JADX WARN: Code restructure failed: missing block: B:752:0x1000, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARN: Code restructure failed: missing block: B:754:0x1003, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L727;
     */
    /* JADX WARN: Code restructure failed: missing block: B:756:0x100b, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2).poll.quiz == false) goto L725;
     */
    /* JADX WARN: Code restructure failed: missing block: B:758:0x1015, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", org.telegram.messenger.R.string.QuizPoll);
     */
    /* JADX WARN: Code restructure failed: missing block: B:760:0x101e, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARN: Code restructure failed: missing block: B:762:0x1021, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L771;
     */
    /* JADX WARN: Code restructure failed: missing block: B:764:0x1025, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L731;
     */
    /* JADX WARN: Code restructure failed: missing block: B:767:0x102b, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L735;
     */
    /* JADX WARN: Code restructure failed: missing block: B:769:0x1035, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARN: Code restructure failed: missing block: B:771:0x1038, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L765;
     */
    /* JADX WARN: Code restructure failed: missing block: B:773:0x103e, code lost:
        if (r26.isSticker() != false) goto L759;
     */
    /* JADX WARN: Code restructure failed: missing block: B:775:0x1044, code lost:
        if (r26.isAnimatedSticker() == false) goto L741;
     */
    /* JADX WARN: Code restructure failed: missing block: B:778:0x104b, code lost:
        if (r26.isGif() == false) goto L751;
     */
    /* JADX WARN: Code restructure failed: missing block: B:780:0x1051, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L749;
     */
    /* JADX WARN: Code restructure failed: missing block: B:782:0x105b, code lost:
        if (android.text.TextUtils.isEmpty(r26.messageOwner.message) != false) goto L749;
     */
    /* JADX WARN: Code restructure failed: missing block: B:784:0x1070, code lost:
        return "🎬 " + replaceSpoilers(r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:786:0x1079, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARN: Code restructure failed: missing block: B:788:0x107e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L757;
     */
    /* JADX WARN: Code restructure failed: missing block: B:790:0x1088, code lost:
        if (android.text.TextUtils.isEmpty(r26.messageOwner.message) != false) goto L757;
     */
    /* JADX WARN: Code restructure failed: missing block: B:792:0x109d, code lost:
        return "📎 " + replaceSpoilers(r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:794:0x10a6, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARN: Code restructure failed: missing block: B:795:0x10a7, code lost:
        r0 = r26.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:796:0x10ab, code lost:
        if (r0 == null) goto L763;
     */
    /* JADX WARN: Code restructure failed: missing block: B:798:0x10c9, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARN: Code restructure failed: missing block: B:800:0x10d2, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARN: Code restructure failed: missing block: B:802:0x10d9, code lost:
        if (android.text.TextUtils.isEmpty(r26.messageText) != false) goto L769;
     */
    /* JADX WARN: Code restructure failed: missing block: B:804:0x10df, code lost:
        return replaceSpoilers(r26);
     */
    /* JADX WARN: Code restructure failed: missing block: B:806:0x10e6, code lost:
        return org.telegram.messenger.LocaleController.getString(r1, org.telegram.messenger.R.string.Message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:808:0x10ef, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARN: Code restructure failed: missing block: B:822:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChatThemeDisabledYou", org.telegram.messenger.R.string.ChatThemeDisabledYou, new java.lang.Object[0]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:823:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChatThemeDisabled", org.telegram.messenger.R.string.ChatThemeDisabled, r7, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:824:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChangedChatThemeYou", org.telegram.messenger.R.string.ChatThemeChangedYou, r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r26, java.lang.String[] r27, boolean[] r28) {
        /*
            Method dump skipped, instructions count: 4357
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getShortStringForMessage(org.telegram.messenger.MessageObject, java.lang.String[], boolean[]):java.lang.String");
    }

    private String replaceSpoilers(MessageObject messageObject) {
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        String str = tLRPC$Message.message;
        if (str == null || tLRPC$Message == null || tLRPC$Message.entities == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < messageObject.messageOwner.entities.size(); i++) {
            if (messageObject.messageOwner.entities.get(i) instanceof TLRPC$TL_messageEntitySpoiler) {
                TLRPC$TL_messageEntitySpoiler tLRPC$TL_messageEntitySpoiler = (TLRPC$TL_messageEntitySpoiler) messageObject.messageOwner.entities.get(i);
                for (int i2 = 0; i2 < tLRPC$TL_messageEntitySpoiler.length; i2++) {
                    char[] cArr = this.spoilerChars;
                    sb.setCharAt(tLRPC$TL_messageEntitySpoiler.offset + i2, cArr[i2 % cArr.length]);
                }
            }
        }
        return sb.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:262:0x0606, code lost:
        if (r12.getBoolean(r22, true) == false) goto L820;
     */
    /* JADX WARN: Code restructure failed: missing block: B:267:0x0612, code lost:
        if (r12.getBoolean("EnablePreviewChannel", r15) != false) goto L275;
     */
    /* JADX WARN: Code restructure failed: missing block: B:268:0x0614, code lost:
        r6 = r29.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:269:0x0618, code lost:
        if ((r6 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L611;
     */
    /* JADX WARN: Code restructure failed: missing block: B:270:0x061a, code lost:
        r7 = r6.action;
     */
    /* JADX WARN: Code restructure failed: missing block: B:271:0x061e, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L318;
     */
    /* JADX WARN: Code restructure failed: missing block: B:272:0x0620, code lost:
        r3 = r7.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:273:0x0626, code lost:
        if (r3 != 0) goto L284;
     */
    /* JADX WARN: Code restructure failed: missing block: B:275:0x062f, code lost:
        if (r7.users.size() != 1) goto L284;
     */
    /* JADX WARN: Code restructure failed: missing block: B:276:0x0631, code lost:
        r3 = r29.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:278:0x0646, code lost:
        if (r3 == 0) goto L304;
     */
    /* JADX WARN: Code restructure failed: missing block: B:280:0x0650, code lost:
        if (r29.messageOwner.peer_id.channel_id == 0) goto L291;
     */
    /* JADX WARN: Code restructure failed: missing block: B:282:0x0654, code lost:
        if (r5.megagroup != false) goto L291;
     */
    /* JADX WARN: Code restructure failed: missing block: B:283:0x0656, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", org.telegram.messenger.R.string.ChannelAddedByNotification, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:285:0x066d, code lost:
        if (r3 != r19) goto L294;
     */
    /* JADX WARN: Code restructure failed: missing block: B:286:0x066f, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", org.telegram.messenger.R.string.NotificationInvitedToGroup, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:287:0x0684, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:288:0x0690, code lost:
        if (r0 != null) goto L297;
     */
    /* JADX WARN: Code restructure failed: missing block: B:289:0x0692, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:292:0x0698, code lost:
        if (r9 != r0.id) goto L303;
     */
    /* JADX WARN: Code restructure failed: missing block: B:294:0x069c, code lost:
        if (r5.megagroup == false) goto L302;
     */
    /* JADX WARN: Code restructure failed: missing block: B:295:0x069e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", org.telegram.messenger.R.string.NotificationGroupAddSelfMega, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:296:0x06b3, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", org.telegram.messenger.R.string.NotificationGroupAddSelf, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:297:0x06c8, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.R.string.NotificationGroupAddMember, r2, r5.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:298:0x06e4, code lost:
        r1 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:300:0x06f4, code lost:
        if (r3 >= r29.messageOwner.action.users.size()) goto L316;
     */
    /* JADX WARN: Code restructure failed: missing block: B:301:0x06f6, code lost:
        r4 = getMessagesController().getUser(r29.messageOwner.action.users.get(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:302:0x070a, code lost:
        if (r4 == null) goto L315;
     */
    /* JADX WARN: Code restructure failed: missing block: B:303:0x070c, code lost:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:304:0x0714, code lost:
        if (r1.length() == 0) goto L312;
     */
    /* JADX WARN: Code restructure failed: missing block: B:305:0x0716, code lost:
        r1.append(", ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:306:0x071b, code lost:
        r1.append(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:307:0x071e, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:308:0x0721, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.R.string.NotificationGroupAddMember, r2, r5.title, r1.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:310:0x0740, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L322;
     */
    /* JADX WARN: Code restructure failed: missing block: B:313:0x0758, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) == false) goto L326;
     */
    /* JADX WARN: Code restructure failed: missing block: B:316:0x0764, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L356;
     */
    /* JADX WARN: Code restructure failed: missing block: B:317:0x0766, code lost:
        r3 = r7.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:318:0x076c, code lost:
        if (r3 != 0) goto L333;
     */
    /* JADX WARN: Code restructure failed: missing block: B:320:0x0775, code lost:
        if (r7.users.size() != 1) goto L333;
     */
    /* JADX WARN: Code restructure failed: missing block: B:321:0x0777, code lost:
        r3 = r29.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARN: Code restructure failed: missing block: B:323:0x078c, code lost:
        if (r3 == 0) goto L342;
     */
    /* JADX WARN: Code restructure failed: missing block: B:325:0x0790, code lost:
        if (r3 != r19) goto L338;
     */
    /* JADX WARN: Code restructure failed: missing block: B:326:0x0792, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:327:0x07a7, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:328:0x07b3, code lost:
        if (r0 != null) goto L341;
     */
    /* JADX WARN: Code restructure failed: missing block: B:329:0x07b5, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:331:0x07b7, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.R.string.NotificationGroupInvitedToCall, r2, r5.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:332:0x07d3, code lost:
        r1 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:334:0x07e3, code lost:
        if (r3 >= r29.messageOwner.action.users.size()) goto L354;
     */
    /* JADX WARN: Code restructure failed: missing block: B:335:0x07e5, code lost:
        r4 = getMessagesController().getUser(r29.messageOwner.action.users.get(r3));
     */
    /* JADX WARN: Code restructure failed: missing block: B:336:0x07f9, code lost:
        if (r4 == null) goto L353;
     */
    /* JADX WARN: Code restructure failed: missing block: B:337:0x07fb, code lost:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:338:0x0803, code lost:
        if (r1.length() == 0) goto L350;
     */
    /* JADX WARN: Code restructure failed: missing block: B:339:0x0805, code lost:
        r1.append(", ");
     */
    /* JADX WARN: Code restructure failed: missing block: B:340:0x080a, code lost:
        r1.append(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:341:0x080d, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:342:0x0810, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.R.string.NotificationGroupInvitedToCall, r2, r5.title, r1.toString());
     */
    /* JADX WARN: Code restructure failed: missing block: B:344:0x082f, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L360;
     */
    /* JADX WARN: Code restructure failed: missing block: B:347:0x0848, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L364;
     */
    /* JADX WARN: Code restructure failed: missing block: B:350:0x085f, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L595;
     */
    /* JADX WARN: Code restructure failed: missing block: B:352:0x0863, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L368;
     */
    /* JADX WARN: Code restructure failed: missing block: B:355:0x0869, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L383;
     */
    /* JADX WARN: Code restructure failed: missing block: B:356:0x086b, code lost:
        r3 = r7.user_id;
     */
    /* JADX WARN: Code restructure failed: missing block: B:357:0x086f, code lost:
        if (r3 != r19) goto L374;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x008f, code lost:
        if (r12.getBoolean("EnablePreviewGroup", true) != false) goto L37;
     */
    /* JADX WARN: Code restructure failed: missing block: B:360:0x088b, code lost:
        if (r3 != r9) goto L378;
     */
    /* JADX WARN: Code restructure failed: missing block: B:362:0x089f, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r29.messageOwner.action.user_id));
     */
    /* JADX WARN: Code restructure failed: missing block: B:363:0x08b1, code lost:
        if (r0 != null) goto L381;
     */
    /* JADX WARN: Code restructure failed: missing block: B:364:0x08b3, code lost:
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:367:0x08d1, code lost:
        r9 = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:368:0x08d4, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L387;
     */
    /* JADX WARN: Code restructure failed: missing block: B:371:0x08e0, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L391;
     */
    /* JADX WARN: Code restructure failed: missing block: B:374:0x08ec, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L395;
     */
    /* JADX WARN: Code restructure failed: missing block: B:377:0x0904, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L399;
     */
    /* JADX WARN: Code restructure failed: missing block: B:380:0x0918, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L403;
     */
    /* JADX WARN: Code restructure failed: missing block: B:383:0x0924, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L575;
     */
    /* JADX WARN: Code restructure failed: missing block: B:385:0x092c, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r5) == false) goto L492;
     */
    /* JADX WARN: Code restructure failed: missing block: B:387:0x0930, code lost:
        if (r5.megagroup == false) goto L409;
     */
    /* JADX WARN: Code restructure failed: missing block: B:389:0x0934, code lost:
        r2 = r29.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:390:0x0936, code lost:
        if (r2 != null) goto L413;
     */
    /* JADX WARN: Code restructure failed: missing block: B:393:0x0950, code lost:
        if (r2.isMusic() == false) goto L416;
     */
    /* JADX WARN: Code restructure failed: missing block: B:394:0x0952, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", org.telegram.messenger.R.string.NotificationActionPinnedMusicChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:396:0x0968, code lost:
        if (r2.isVideo() == false) goto L424;
     */
    /* JADX WARN: Code restructure failed: missing block: B:398:0x096e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L423;
     */
    /* JADX WARN: Code restructure failed: missing block: B:400:0x0978, code lost:
        if (android.text.TextUtils.isEmpty(r2.messageOwner.message) != false) goto L423;
     */
    /* JADX WARN: Code restructure failed: missing block: B:401:0x097a, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r5.title, "📹 " + r2.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:402:0x09a0, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:404:0x09b6, code lost:
        if (r2.isGif() == false) goto L432;
     */
    /* JADX WARN: Code restructure failed: missing block: B:406:0x09bc, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L431;
     */
    /* JADX WARN: Code restructure failed: missing block: B:408:0x09c6, code lost:
        if (android.text.TextUtils.isEmpty(r2.messageOwner.message) != false) goto L431;
     */
    /* JADX WARN: Code restructure failed: missing block: B:409:0x09c8, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r5.title, "🎬 " + r2.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:410:0x09ee, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", org.telegram.messenger.R.string.NotificationActionPinnedGifChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:412:0x0a06, code lost:
        if (r2.isVoice() == false) goto L435;
     */
    /* JADX WARN: Code restructure failed: missing block: B:413:0x0a08, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:415:0x0a1c, code lost:
        if (r2.isRoundVideo() == false) goto L438;
     */
    /* JADX WARN: Code restructure failed: missing block: B:416:0x0a1e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:418:0x0a32, code lost:
        if (r2.isSticker() != false) goto L488;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x009d, code lost:
        if (r12.getBoolean("EnablePreviewChannel", r3) == false) goto L42;
     */
    /* JADX WARN: Code restructure failed: missing block: B:420:0x0a38, code lost:
        if (r2.isAnimatedSticker() == false) goto L442;
     */
    /* JADX WARN: Code restructure failed: missing block: B:422:0x0a3c, code lost:
        r1 = r2.messageOwner;
        r3 = r1.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:423:0x0a42, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L450;
     */
    /* JADX WARN: Code restructure failed: missing block: B:425:0x0a48, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L449;
     */
    /* JADX WARN: Code restructure failed: missing block: B:427:0x0a50, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L449;
     */
    /* JADX WARN: Code restructure failed: missing block: B:428:0x0a52, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r5.title, "📎 " + r2.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:429:0x0a78, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", org.telegram.messenger.R.string.NotificationActionPinnedFileChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:431:0x0a8c, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L487;
     */
    /* JADX WARN: Code restructure failed: missing block: B:433:0x0a90, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L454;
     */
    /* JADX WARN: Code restructure failed: missing block: B:436:0x0a96, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L457;
     */
    /* JADX WARN: Code restructure failed: missing block: B:437:0x0a98, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:439:0x0aac, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L460;
     */
    /* JADX WARN: Code restructure failed: missing block: B:440:0x0aae, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r29.messageOwner.media;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2, r5.title, org.telegram.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:442:0x0ad3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L466;
     */
    /* JADX WARN: Code restructure failed: missing block: B:443:0x0ad5, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:444:0x0adb, code lost:
        if (r0.quiz == false) goto L465;
     */
    /* JADX WARN: Code restructure failed: missing block: B:445:0x0add, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2, r5.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:446:0x0af4, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2, r5.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:448:0x0b0d, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L474;
     */
    /* JADX WARN: Code restructure failed: missing block: B:450:0x0b13, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L473;
     */
    /* JADX WARN: Code restructure failed: missing block: B:452:0x0b1b, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L473;
     */
    /* JADX WARN: Code restructure failed: missing block: B:453:0x0b1d, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r5.title, "\u1f5bc " + r2.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:454:0x0b43, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:456:0x0b59, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L477;
     */
    /* JADX WARN: Code restructure failed: missing block: B:457:0x0b5b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", org.telegram.messenger.R.string.NotificationActionPinnedGameChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:458:0x0b6b, code lost:
        r0 = r2.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:459:0x0b6d, code lost:
        if (r0 == null) goto L486;
     */
    /* JADX WARN: Code restructure failed: missing block: B:461:0x0b73, code lost:
        if (r0.length() <= 0) goto L486;
     */
    /* JADX WARN: Code restructure failed: missing block: B:462:0x0b75, code lost:
        r0 = r2.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:463:0x0b7b, code lost:
        if (r0.length() <= 20) goto L485;
     */
    /* JADX WARN: Code restructure failed: missing block: B:464:0x0b7d, code lost:
        r1 = new java.lang.StringBuilder();
        r3 = 0;
        r1.append((java.lang.Object) r0.subSequence(0, 20));
        r1.append("...");
        r0 = r1.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:465:0x0b94, code lost:
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:466:0x0b95, code lost:
        r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel;
        r2 = new java.lang.Object[2];
        r2[r3] = r5.title;
        r2[1] = r0;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", r1, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:467:0x0ba7, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:468:0x0bb9, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:469:0x0bcb, code lost:
        r0 = r2.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:470:0x0bd0, code lost:
        if (r0 == null) goto L491;
     */
    /* JADX WARN: Code restructure failed: missing block: B:471:0x0bd2, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel, r5.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:472:0x0be6, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:473:0x0bf7, code lost:
        r6 = r29.replyMessageObject;
     */
    /* JADX WARN: Code restructure failed: missing block: B:474:0x0bfa, code lost:
        if (r6 != null) goto L496;
     */
    /* JADX WARN: Code restructure failed: missing block: B:477:0x0CLASSNAME, code lost:
        if (r6.isMusic() == false) goto L499;
     */
    /* JADX WARN: Code restructure failed: missing block: B:478:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", org.telegram.messenger.R.string.NotificationActionPinnedMusic, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:480:0x0CLASSNAME, code lost:
        if (r6.isVideo() == false) goto L507;
     */
    /* JADX WARN: Code restructure failed: missing block: B:482:0x0CLASSNAME, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L506;
     */
    /* JADX WARN: Code restructure failed: missing block: B:484:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r6.messageOwner.message) != false) goto L506;
     */
    /* JADX WARN: Code restructure failed: missing block: B:485:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r2, "📹 " + r6.messageOwner.message, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:486:0x0c6b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", org.telegram.messenger.R.string.NotificationActionPinnedVideo, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:488:0x0CLASSNAME, code lost:
        if (r6.isGif() == false) goto L515;
     */
    /* JADX WARN: Code restructure failed: missing block: B:490:0x0c8a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L514;
     */
    /* JADX WARN: Code restructure failed: missing block: B:492:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r6.messageOwner.message) != false) goto L514;
     */
    /* JADX WARN: Code restructure failed: missing block: B:493:0x0CLASSNAME, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r2, "🎬 " + r6.messageOwner.message, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:494:0x0cbf, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", org.telegram.messenger.R.string.NotificationActionPinnedGif, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:496:0x0cdb, code lost:
        if (r6.isVoice() == false) goto L518;
     */
    /* JADX WARN: Code restructure failed: missing block: B:497:0x0cdd, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", org.telegram.messenger.R.string.NotificationActionPinnedVoice, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:499:0x0cf3, code lost:
        if (r6.isRoundVideo() == false) goto L521;
     */
    /* JADX WARN: Code restructure failed: missing block: B:500:0x0cf5, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", org.telegram.messenger.R.string.NotificationActionPinnedRound, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:502:0x0d0b, code lost:
        if (r6.isSticker() != false) goto L571;
     */
    /* JADX WARN: Code restructure failed: missing block: B:504:0x0d11, code lost:
        if (r6.isAnimatedSticker() == false) goto L525;
     */
    /* JADX WARN: Code restructure failed: missing block: B:506:0x0d15, code lost:
        r1 = r6.messageOwner;
        r3 = r1.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:507:0x0d1b, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L533;
     */
    /* JADX WARN: Code restructure failed: missing block: B:509:0x0d21, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L532;
     */
    /* JADX WARN: Code restructure failed: missing block: B:511:0x0d29, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L532;
     */
    /* JADX WARN: Code restructure failed: missing block: B:512:0x0d2b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r2, "📎 " + r6.messageOwner.message, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:513:0x0d54, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", org.telegram.messenger.R.string.NotificationActionPinnedFile, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:515:0x0d6b, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L570;
     */
    /* JADX WARN: Code restructure failed: missing block: B:517:0x0d6f, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L537;
     */
    /* JADX WARN: Code restructure failed: missing block: B:520:0x0d75, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L540;
     */
    /* JADX WARN: Code restructure failed: missing block: B:521:0x0d77, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", org.telegram.messenger.R.string.NotificationActionPinnedGeoLive, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:523:0x0d8e, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L543;
     */
    /* JADX WARN: Code restructure failed: missing block: B:524:0x0d90, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r29.messageOwner.media;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", org.telegram.messenger.R.string.NotificationActionPinnedContact2, r2, r5.title, org.telegram.messenger.ContactsController.formatName(r0.first_name, r0.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:526:0x0db8, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L549;
     */
    /* JADX WARN: Code restructure failed: missing block: B:527:0x0dba, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:528:0x0dc0, code lost:
        if (r0.quiz == false) goto L548;
     */
    /* JADX WARN: Code restructure failed: missing block: B:529:0x0dc2, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", org.telegram.messenger.R.string.NotificationActionPinnedQuiz2, r2, r5.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:530:0x0ddc, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", org.telegram.messenger.R.string.NotificationActionPinnedPoll2, r2, r5.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:532:0x0df8, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L557;
     */
    /* JADX WARN: Code restructure failed: missing block: B:534:0x0dfe, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L556;
     */
    /* JADX WARN: Code restructure failed: missing block: B:536:0x0e06, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L556;
     */
    /* JADX WARN: Code restructure failed: missing block: B:537:0x0e08, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r2, "\u1f5bc " + r6.messageOwner.message, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:538:0x0e31, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", org.telegram.messenger.R.string.NotificationActionPinnedPhoto, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:540:0x0e4b, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L560;
     */
    /* JADX WARN: Code restructure failed: missing block: B:541:0x0e4d, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", org.telegram.messenger.R.string.NotificationActionPinnedGame, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:542:0x0e5f, code lost:
        r0 = r6.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:543:0x0e61, code lost:
        if (r0 == null) goto L569;
     */
    /* JADX WARN: Code restructure failed: missing block: B:545:0x0e67, code lost:
        if (r0.length() <= 0) goto L569;
     */
    /* JADX WARN: Code restructure failed: missing block: B:546:0x0e69, code lost:
        r0 = r6.messageText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:547:0x0e6f, code lost:
        if (r0.length() <= 20) goto L568;
     */
    /* JADX WARN: Code restructure failed: missing block: B:548:0x0e71, code lost:
        r1 = new java.lang.StringBuilder();
        r3 = 0;
        r1.append((java.lang.Object) r0.subSequence(0, 20));
        r1.append("...");
        r0 = r1.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:549:0x0e88, code lost:
        r3 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:550:0x0e89, code lost:
        r1 = org.telegram.messenger.R.string.NotificationActionPinnedText;
        r4 = new java.lang.Object[3];
        r4[r3] = r2;
        r4[1] = r0;
        r4[2] = r5.title;
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", r1, r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:551:0x0e9e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.R.string.NotificationActionPinnedNoText, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:552:0x0eb3, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", org.telegram.messenger.R.string.NotificationActionPinnedGeo, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:553:0x0ec8, code lost:
        r0 = r6.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:554:0x0ece, code lost:
        if (r0 == null) goto L574;
     */
    /* JADX WARN: Code restructure failed: missing block: B:555:0x0ed0, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji, r2, r5.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:556:0x0ee6, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", org.telegram.messenger.R.string.NotificationActionPinnedSticker, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:558:0x0efb, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) == false) goto L579;
     */
    /* JADX WARN: Code restructure failed: missing block: B:561:0x0var_, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) == false) goto L591;
     */
    /* JADX WARN: Code restructure failed: missing block: B:562:0x0var_, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r7).emoticon;
     */
    /* JADX WARN: Code restructure failed: missing block: B:563:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r0) == false) goto L587;
     */
    /* JADX WARN: Code restructure failed: missing block: B:565:0x0var_, code lost:
        if (r3 != r19) goto L586;
     */
    /* JADX WARN: Code restructure failed: missing block: B:566:0x0var_, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChatThemeDisabledYou", org.telegram.messenger.R.string.ChatThemeDisabledYou, new java.lang.Object[0]);
     */
    /* JADX WARN: Code restructure failed: missing block: B:567:0x0var_, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChatThemeDisabled", org.telegram.messenger.R.string.ChatThemeDisabled, r2, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:569:0x0f3b, code lost:
        if (r3 != r19) goto L590;
     */
    /* JADX WARN: Code restructure failed: missing block: B:570:0x0f3d, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChangedChatThemeYou", org.telegram.messenger.R.string.ChatThemeChangedYou, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:571:0x0f4b, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChangedChatThemeTo", org.telegram.messenger.R.string.ChatThemeChangedTo, r2, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:573:0x0f5e, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest) == false) goto L136;
     */
    /* JADX WARN: Code restructure failed: missing block: B:576:0x0var_, code lost:
        if (r6.peer_id.channel_id == 0) goto L605;
     */
    /* JADX WARN: Code restructure failed: missing block: B:578:0x0var_, code lost:
        if (r5.megagroup != false) goto L605;
     */
    /* JADX WARN: Code restructure failed: missing block: B:580:0x0f7a, code lost:
        if (r29.isVideoAvatar() == false) goto L603;
     */
    /* JADX WARN: Code restructure failed: missing block: B:584:0x0fa5, code lost:
        if (r29.isVideoAvatar() == false) goto L609;
     */
    /* JADX WARN: Code restructure failed: missing block: B:588:0x0fd3, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r5) == false) goto L716;
     */
    /* JADX WARN: Code restructure failed: missing block: B:590:0x0fd7, code lost:
        if (r5.megagroup != false) goto L716;
     */
    /* JADX WARN: Code restructure failed: missing block: B:592:0x0fdd, code lost:
        if (r29.isMediaEmpty() == false) goto L624;
     */
    /* JADX WARN: Code restructure failed: missing block: B:593:0x0fdf, code lost:
        if (r30 != false) goto L622;
     */
    /* JADX WARN: Code restructure failed: missing block: B:595:0x0fe9, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageOwner.message) != false) goto L622;
     */
    /* JADX WARN: Code restructure failed: missing block: B:596:0x0feb, code lost:
        r14 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.R.string.NotificationMessageText, r2, r29.messageOwner.message);
        r31[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:598:0x1012, code lost:
        r4 = r29.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:599:0x101a, code lost:
        if ((r4.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L635;
     */
    /* JADX WARN: Code restructure failed: missing block: B:600:0x101c, code lost:
        if (r30 != false) goto L633;
     */
    /* JADX WARN: Code restructure failed: missing block: B:602:0x1022, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L633;
     */
    /* JADX WARN: Code restructure failed: missing block: B:604:0x102a, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L633;
     */
    /* JADX WARN: Code restructure failed: missing block: B:605:0x102c, code lost:
        r1 = org.telegram.messenger.R.string.NotificationMessageText;
        r14 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", r1, r2, "\u1f5bc " + r29.messageOwner.message);
        r31[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:608:0x1066, code lost:
        if (r29.isVideo() == false) goto L646;
     */
    /* JADX WARN: Code restructure failed: missing block: B:609:0x1068, code lost:
        if (r30 != false) goto L644;
     */
    /* JADX WARN: Code restructure failed: missing block: B:611:0x106e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L644;
     */
    /* JADX WARN: Code restructure failed: missing block: B:613:0x1078, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageOwner.message) != false) goto L644;
     */
    /* JADX WARN: Code restructure failed: missing block: B:614:0x107a, code lost:
        r3 = org.telegram.messenger.R.string.NotificationMessageText;
        r14 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", r3, r2, "📹 " + r29.messageOwner.message);
        r31[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:617:0x10b6, code lost:
        if (r29.isVoice() == false) goto L650;
     */
    /* JADX WARN: Code restructure failed: missing block: B:620:0x10ca, code lost:
        if (r29.isRoundVideo() == false) goto L654;
     */
    /* JADX WARN: Code restructure failed: missing block: B:623:0x10de, code lost:
        if (r29.isMusic() == false) goto L658;
     */
    /* JADX WARN: Code restructure failed: missing block: B:625:0x10ee, code lost:
        r1 = r29.messageOwner.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:626:0x10f4, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L662;
     */
    /* JADX WARN: Code restructure failed: missing block: B:627:0x10f6, code lost:
        r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:629:0x1114, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L668;
     */
    /* JADX WARN: Code restructure failed: missing block: B:630:0x1116, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:631:0x111c, code lost:
        if (r0.quiz == false) goto L667;
     */
    /* JADX WARN: Code restructure failed: missing block: B:632:0x111e, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageQuiz2", org.telegram.messenger.R.string.ChannelMessageQuiz2, r2, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:633:0x1133, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessagePoll2", org.telegram.messenger.R.string.ChannelMessagePoll2, r2, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:635:0x114a, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L714;
     */
    /* JADX WARN: Code restructure failed: missing block: B:637:0x114e, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L672;
     */
    /* JADX WARN: Code restructure failed: missing block: B:640:0x1154, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L676;
     */
    /* JADX WARN: Code restructure failed: missing block: B:643:0x1168, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L706;
     */
    /* JADX WARN: Code restructure failed: missing block: B:645:0x116e, code lost:
        if (r29.isSticker() != false) goto L702;
     */
    /* JADX WARN: Code restructure failed: missing block: B:647:0x1174, code lost:
        if (r29.isAnimatedSticker() == false) goto L682;
     */
    /* JADX WARN: Code restructure failed: missing block: B:650:0x117c, code lost:
        if (r29.isGif() == false) goto L693;
     */
    /* JADX WARN: Code restructure failed: missing block: B:651:0x117e, code lost:
        if (r30 != false) goto L691;
     */
    /* JADX WARN: Code restructure failed: missing block: B:653:0x1184, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L691;
     */
    /* JADX WARN: Code restructure failed: missing block: B:655:0x118e, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageOwner.message) != false) goto L691;
     */
    /* JADX WARN: Code restructure failed: missing block: B:656:0x1190, code lost:
        r1 = org.telegram.messenger.R.string.NotificationMessageText;
        r14 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", r1, r2, "🎬 " + r29.messageOwner.message);
        r31[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:658:0x11c6, code lost:
        if (r30 != false) goto L700;
     */
    /* JADX WARN: Code restructure failed: missing block: B:660:0x11cc, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L700;
     */
    /* JADX WARN: Code restructure failed: missing block: B:662:0x11d6, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageOwner.message) != false) goto L700;
     */
    /* JADX WARN: Code restructure failed: missing block: B:663:0x11d8, code lost:
        r1 = org.telegram.messenger.R.string.NotificationMessageText;
        r14 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", r1, r2, "📎 " + r29.messageOwner.message);
        r31[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:665:0x120e, code lost:
        r0 = r29.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:666:0x1214, code lost:
        if (r0 == null) goto L705;
     */
    /* JADX WARN: Code restructure failed: missing block: B:667:0x1216, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageStickerEmoji", org.telegram.messenger.R.string.ChannelMessageStickerEmoji, r2, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:668:0x1227, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("ChannelMessageSticker", org.telegram.messenger.R.string.ChannelMessageSticker, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:670:0x1236, code lost:
        if (r30 != false) goto L712;
     */
    /* JADX WARN: Code restructure failed: missing block: B:672:0x123e, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageText) != false) goto L712;
     */
    /* JADX WARN: Code restructure failed: missing block: B:673:0x1240, code lost:
        r14 = org.telegram.messenger.LocaleController.formatString("NotificationMessageText", org.telegram.messenger.R.string.NotificationMessageText, r2, r29.messageText);
        r31[0] = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:677:0x1277, code lost:
        if (r29.isMediaEmpty() == false) goto L725;
     */
    /* JADX WARN: Code restructure failed: missing block: B:678:0x1279, code lost:
        if (r30 != false) goto L723;
     */
    /* JADX WARN: Code restructure failed: missing block: B:680:0x1283, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageOwner.message) != false) goto L723;
     */
    /* JADX WARN: Code restructure failed: missing block: B:683:0x12b4, code lost:
        r3 = r29.messageOwner;
     */
    /* JADX WARN: Code restructure failed: missing block: B:684:0x12bc, code lost:
        if ((r3.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L736;
     */
    /* JADX WARN: Code restructure failed: missing block: B:685:0x12be, code lost:
        if (r30 != false) goto L734;
     */
    /* JADX WARN: Code restructure failed: missing block: B:687:0x12c4, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L734;
     */
    /* JADX WARN: Code restructure failed: missing block: B:689:0x12cc, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L734;
     */
    /* JADX WARN: Code restructure failed: missing block: B:690:0x12ce, code lost:
        r1 = org.telegram.messenger.R.string.NotificationMessageGroupText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:693:0x1310, code lost:
        if (r29.isVideo() == false) goto L747;
     */
    /* JADX WARN: Code restructure failed: missing block: B:694:0x1312, code lost:
        if (r30 != false) goto L745;
     */
    /* JADX WARN: Code restructure failed: missing block: B:696:0x1318, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L745;
     */
    /* JADX WARN: Code restructure failed: missing block: B:698:0x1322, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageOwner.message) != false) goto L745;
     */
    /* JADX WARN: Code restructure failed: missing block: B:699:0x1324, code lost:
        r3 = org.telegram.messenger.R.string.NotificationMessageGroupText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:702:0x1369, code lost:
        if (r29.isVoice() == false) goto L751;
     */
    /* JADX WARN: Code restructure failed: missing block: B:705:0x1381, code lost:
        if (r29.isRoundVideo() == false) goto L755;
     */
    /* JADX WARN: Code restructure failed: missing block: B:708:0x1399, code lost:
        if (r29.isMusic() == false) goto L759;
     */
    /* JADX WARN: Code restructure failed: missing block: B:710:0x13ad, code lost:
        r1 = r29.messageOwner.media;
     */
    /* JADX WARN: Code restructure failed: missing block: B:711:0x13b3, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L763;
     */
    /* JADX WARN: Code restructure failed: missing block: B:712:0x13b5, code lost:
        r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:714:0x13d9, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L769;
     */
    /* JADX WARN: Code restructure failed: missing block: B:715:0x13db, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1).poll;
     */
    /* JADX WARN: Code restructure failed: missing block: B:716:0x13e1, code lost:
        if (r0.quiz == false) goto L768;
     */
    /* JADX WARN: Code restructure failed: missing block: B:717:0x13e3, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupQuiz2", org.telegram.messenger.R.string.NotificationMessageGroupQuiz2, r2, r5.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:718:0x13fd, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPoll2", org.telegram.messenger.R.string.NotificationMessageGroupPoll2, r2, r5.title, r0.question);
     */
    /* JADX WARN: Code restructure failed: missing block: B:720:0x1419, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L773;
     */
    /* JADX WARN: Code restructure failed: missing block: B:723:0x1439, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L818;
     */
    /* JADX WARN: Code restructure failed: missing block: B:725:0x143d, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L777;
     */
    /* JADX WARN: Code restructure failed: missing block: B:728:0x1443, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L781;
     */
    /* JADX WARN: Code restructure failed: missing block: B:731:0x145c, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L811;
     */
    /* JADX WARN: Code restructure failed: missing block: B:733:0x1462, code lost:
        if (r29.isSticker() != false) goto L807;
     */
    /* JADX WARN: Code restructure failed: missing block: B:735:0x1468, code lost:
        if (r29.isAnimatedSticker() == false) goto L787;
     */
    /* JADX WARN: Code restructure failed: missing block: B:738:0x1470, code lost:
        if (r29.isGif() == false) goto L798;
     */
    /* JADX WARN: Code restructure failed: missing block: B:739:0x1472, code lost:
        if (r30 != false) goto L796;
     */
    /* JADX WARN: Code restructure failed: missing block: B:741:0x1478, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L796;
     */
    /* JADX WARN: Code restructure failed: missing block: B:743:0x1482, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageOwner.message) != false) goto L796;
     */
    /* JADX WARN: Code restructure failed: missing block: B:744:0x1484, code lost:
        r1 = org.telegram.messenger.R.string.NotificationMessageGroupText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:746:0x14c2, code lost:
        if (r30 != false) goto L805;
     */
    /* JADX WARN: Code restructure failed: missing block: B:748:0x14c8, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L805;
     */
    /* JADX WARN: Code restructure failed: missing block: B:750:0x14d2, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageOwner.message) != false) goto L805;
     */
    /* JADX WARN: Code restructure failed: missing block: B:751:0x14d4, code lost:
        r1 = org.telegram.messenger.R.string.NotificationMessageGroupText;
     */
    /* JADX WARN: Code restructure failed: missing block: B:753:0x1512, code lost:
        r0 = r29.getStickerEmoji();
     */
    /* JADX WARN: Code restructure failed: missing block: B:754:0x1518, code lost:
        if (r0 == null) goto L810;
     */
    /* JADX WARN: Code restructure failed: missing block: B:755:0x151a, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupStickerEmoji", org.telegram.messenger.R.string.NotificationMessageGroupStickerEmoji, r2, r5.title, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:756:0x1530, code lost:
        r0 = org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupSticker", org.telegram.messenger.R.string.NotificationMessageGroupSticker, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:757:0x1543, code lost:
        if (r30 != false) goto L816;
     */
    /* JADX WARN: Code restructure failed: missing block: B:759:0x154b, code lost:
        if (android.text.TextUtils.isEmpty(r29.messageText) != false) goto L816;
     */
    /* JADX WARN: Code restructure failed: missing block: B:816:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", org.telegram.messenger.R.string.NotificationGroupCreatedCall, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:817:?, code lost:
        return r29.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:818:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", org.telegram.messenger.R.string.NotificationInvitedToGroupByLink, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:819:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", org.telegram.messenger.R.string.NotificationEditedGroupName, r2, r7.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:820:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", org.telegram.messenger.R.string.NotificationGroupKickYou, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:821:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", org.telegram.messenger.R.string.NotificationGroupLeftMember, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:822:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", org.telegram.messenger.R.string.NotificationGroupKickMember, r2, r5.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARN: Code restructure failed: missing block: B:823:?, code lost:
        return r29.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:824:?, code lost:
        return r29.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:825:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.R.string.ActionMigrateFromGroupNotify, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:826:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.R.string.ActionMigrateFromGroupNotify, r7.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:827:?, code lost:
        return r29.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:828:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:829:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.R.string.NotificationActionPinnedNoText, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:830:?, code lost:
        return r29.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:831:?, code lost:
        return r29.messageText.toString();
     */
    /* JADX WARN: Code restructure failed: missing block: B:832:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", org.telegram.messenger.R.string.ChannelVideoEditNotification, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:833:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", org.telegram.messenger.R.string.ChannelPhotoEditNotification, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:834:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", org.telegram.messenger.R.string.NotificationEditedGroupVideo, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:835:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", org.telegram.messenger.R.string.NotificationEditedGroupPhoto, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:836:?, code lost:
        return r14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:837:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", org.telegram.messenger.R.string.ChannelMessageNoText, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:838:?, code lost:
        return r14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:839:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessagePhoto", org.telegram.messenger.R.string.ChannelMessagePhoto, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:840:?, code lost:
        return r14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:841:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageVideo", org.telegram.messenger.R.string.ChannelMessageVideo, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:842:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageAudio", org.telegram.messenger.R.string.ChannelMessageAudio, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:843:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageRound", org.telegram.messenger.R.string.ChannelMessageRound, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:844:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageMusic", org.telegram.messenger.R.string.ChannelMessageMusic, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:845:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageContact2", org.telegram.messenger.R.string.ChannelMessageContact2, r2, org.telegram.messenger.ContactsController.formatName(r1.first_name, r1.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:846:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageLiveLocation", org.telegram.messenger.R.string.ChannelMessageLiveLocation, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:847:?, code lost:
        return r14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:848:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageGIF", org.telegram.messenger.R.string.ChannelMessageGIF, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:849:?, code lost:
        return r14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:850:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageDocument", org.telegram.messenger.R.string.ChannelMessageDocument, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:851:?, code lost:
        return r14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:852:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageNoText", org.telegram.messenger.R.string.ChannelMessageNoText, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:853:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelMessageMap", org.telegram.messenger.R.string.ChannelMessageMap, r2);
     */
    /* JADX WARN: Code restructure failed: missing block: B:854:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.R.string.NotificationMessageGroupText, r2, r5.title, r29.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:855:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", org.telegram.messenger.R.string.NotificationMessageGroupNoText, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:856:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", r1, r2, r5.title, "\u1f5bc " + r29.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:857:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupPhoto", org.telegram.messenger.R.string.NotificationMessageGroupPhoto, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:858:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", r3, r2, r5.title, "📹 " + r29.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:859:?, code lost:
        return org.telegram.messenger.LocaleController.formatString(" ", org.telegram.messenger.R.string.NotificationMessageGroupVideo, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:860:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupAudio", org.telegram.messenger.R.string.NotificationMessageGroupAudio, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:861:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupRound", org.telegram.messenger.R.string.NotificationMessageGroupRound, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:862:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMusic", org.telegram.messenger.R.string.NotificationMessageGroupMusic, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:863:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupContact2", org.telegram.messenger.R.string.NotificationMessageGroupContact2, r2, r5.title, org.telegram.messenger.ContactsController.formatName(r1.first_name, r1.last_name));
     */
    /* JADX WARN: Code restructure failed: missing block: B:864:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGame", org.telegram.messenger.R.string.NotificationMessageGroupGame, r2, r5.title, r1.game.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:865:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupLiveLocation", org.telegram.messenger.R.string.NotificationMessageGroupLiveLocation, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:866:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", r1, r2, r5.title, "🎬 " + r29.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:867:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupGif", org.telegram.messenger.R.string.NotificationMessageGroupGif, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:868:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", r1, r2, r5.title, "📎 " + r29.messageOwner.message);
     */
    /* JADX WARN: Code restructure failed: missing block: B:869:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupDocument", org.telegram.messenger.R.string.NotificationMessageGroupDocument, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:870:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupText", org.telegram.messenger.R.string.NotificationMessageGroupText, r2, r5.title, r29.messageText);
     */
    /* JADX WARN: Code restructure failed: missing block: B:871:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupNoText", org.telegram.messenger.R.string.NotificationMessageGroupNoText, r2, r5.title);
     */
    /* JADX WARN: Code restructure failed: missing block: B:872:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationMessageGroupMap", org.telegram.messenger.R.string.NotificationMessageGroupMap, r2, r5.title);
     */
    /* JADX WARN: Removed duplicated region for block: B:260:0x05fd  */
    /* JADX WARN: Removed duplicated region for block: B:765:0x1591  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r29, boolean r30, boolean[] r31, boolean[] r32) {
        /*
            Method dump skipped, instructions count: 5573
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.getStringForMessage(org.telegram.messenger.MessageObject, boolean, boolean[], boolean[]):java.lang.String");
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent service = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 0);
            int i = getAccountInstance().getNotificationsSettings().getInt("repeat_messages", 60);
            if (i > 0 && this.personalCount > 0) {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + (i * 60 * 1000), service);
            } else {
                this.alarmManager.cancel(service);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        TLRPC$MessageAction tLRPC$MessageAction;
        TLRPC$Message tLRPC$Message = messageObject.messageOwner;
        TLRPC$Peer tLRPC$Peer = tLRPC$Message.peer_id;
        return tLRPC$Peer != null && tLRPC$Peer.chat_id == 0 && tLRPC$Peer.channel_id == 0 && ((tLRPC$MessageAction = tLRPC$Message.action) == null || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionEmpty));
    }

    private int getNotifyOverride(SharedPreferences sharedPreferences, long j) {
        int i = sharedPreferences.getInt("notify2_" + j, -1);
        if (i == 3) {
            if (sharedPreferences.getInt("notifyuntil_" + j, 0) < getConnectionsManager().getCurrentTime()) {
                return i;
            }
            return 2;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showNotifications$25() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$showNotifications$25();
            }
        });
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$hideNotifications$26();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
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
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$dismissNotification$27() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    private void playInChatSound() {
        if (!this.inChatSoundEnabled || MediaController.getInstance().isRecordingAudio()) {
            return;
        }
        try {
            if (audioManager.getRingerMode() == 0) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            if (getNotifyOverride(getAccountInstance().getNotificationsSettings(), this.openedDialogId) == 2) {
                return;
            }
            notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsController.this.lambda$playInChatSound$29();
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$playInChatSound$29() {
        if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundPlay) <= 500) {
            return;
        }
        try {
            if (this.soundPool == null) {
                SoundPool soundPool = new SoundPool(3, 1, 0);
                this.soundPool = soundPool;
                soundPool.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda3.INSTANCE);
            }
            if (this.soundIn == 0 && !this.soundInLoaded) {
                this.soundInLoaded = true;
                this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_in, 1);
            }
            int i = this.soundIn;
            if (i == 0) {
                return;
            }
            try {
                this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$playInChatSound$28(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void scheduleNotificationDelay(boolean z) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay notification start, onlineReason = " + z);
            }
            this.notificationDelayWakelock.acquire(10000L);
            notificationsQueue.cancelRunnable(this.notificationDelayRunnable);
            notificationsQueue.postRunnable(this.notificationDelayRunnable, z ? 3000 : 1000);
        } catch (Exception e) {
            FileLog.e(e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$repeatNotificationMaybe$30();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$repeatNotificationMaybe$30() {
        int i = Calendar.getInstance().get(11);
        if (i >= 11 && i <= 22) {
            notificationManager.cancel(this.notificationId);
            showOrUpdateNotification(true);
            return;
        }
        scheduleNotificationRepeat();
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

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: deleteNotificationChannelInternal */
    public void lambda$deleteNotificationChannel$31(long j, int i) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            if (i == 0 || i == -1) {
                String str = "org.telegram.key" + j;
                String string = notificationsSettings.getString(str, null);
                if (string != null) {
                    edit.remove(str).remove(str + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel internal " + string);
                    }
                }
            }
            if (i == 1 || i == -1) {
                String str2 = "org.telegram.keyia" + j;
                String string2 = notificationsSettings.getString(str2, null);
                if (string2 != null) {
                    edit.remove(str2).remove(str2 + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string2);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel internal " + string2);
                    }
                }
            }
            edit.commit();
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public void deleteNotificationChannel(final long j, final int i) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$deleteNotificationChannel$31(j, i);
            }
        });
    }

    public void deleteNotificationChannelGlobal(int i) {
        deleteNotificationChannelGlobal(i, -1);
    }

    /* renamed from: deleteNotificationChannelGlobalInternal */
    public void lambda$deleteNotificationChannelGlobal$32(int i, int i2) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            if (i2 == 0 || i2 == -1) {
                String str = i == 2 ? "channels" : i == 0 ? "groups" : "private";
                String string = notificationsSettings.getString(str, null);
                if (string != null) {
                    SharedPreferences.Editor remove = edit.remove(str);
                    remove.remove(str + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel global internal " + string);
                    }
                }
            }
            if (i2 == 1 || i2 == -1) {
                String str2 = i == 2 ? "channels_ia" : i == 0 ? "groups_ia" : "private_ia";
                String string2 = notificationsSettings.getString(str2, null);
                if (string2 != null) {
                    SharedPreferences.Editor remove2 = edit.remove(str2);
                    remove2.remove(str2 + "_s");
                    try {
                        systemNotificationManager.deleteNotificationChannel(string2);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("delete channel global internal " + string2);
                    }
                }
            }
            edit.remove(i == 2 ? "overwrite_channel" : i == 0 ? "overwrite_group" : "overwrite_private");
            edit.commit();
        } catch (Exception e3) {
            FileLog.e(e3);
        }
    }

    public void deleteNotificationChannelGlobal(final int i, final int i2) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$deleteNotificationChannelGlobal$32(i, i2);
            }
        });
    }

    public void deleteAllNotificationChannels() {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$deleteAllNotificationChannels$33();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteAllNotificationChannels$33() {
        try {
            SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
            Map<String, ?> all = notificationsSettings.getAll();
            SharedPreferences.Editor edit = notificationsSettings.edit();
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("org.telegram.key")) {
                    if (!key.endsWith("_s")) {
                        String str = (String) entry.getValue();
                        systemNotificationManager.deleteNotificationChannel(str);
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("delete all channel " + str);
                        }
                    }
                    edit.remove(key);
                }
            }
            edit.commit();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean unsupportedNotificationShortcut() {
        return Build.VERSION.SDK_INT < 29 || !SharedConfig.chatBubbles;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x00ef A[Catch: Exception -> 0x014d, TryCatch #0 {Exception -> 0x014d, blocks: (B:9:0x0020, B:12:0x0060, B:14:0x0068, B:18:0x0078, B:20:0x00a1, B:22:0x00b1, B:24:0x00bb, B:26:0x00ef, B:28:0x00f7, B:30:0x0100, B:38:0x011f, B:42:0x0136, B:32:0x0107, B:34:0x010d, B:36:0x0112, B:35:0x0110, B:37:0x0117, B:27:0x00f3, B:17:0x0074, B:13:0x0064), top: B:49:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:27:0x00f3 A[Catch: Exception -> 0x014d, TryCatch #0 {Exception -> 0x014d, blocks: (B:9:0x0020, B:12:0x0060, B:14:0x0068, B:18:0x0078, B:20:0x00a1, B:22:0x00b1, B:24:0x00bb, B:26:0x00ef, B:28:0x00f7, B:30:0x0100, B:38:0x011f, B:42:0x0136, B:32:0x0107, B:34:0x010d, B:36:0x0112, B:35:0x0110, B:37:0x0117, B:27:0x00f3, B:17:0x0074, B:13:0x0064), top: B:49:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0100 A[Catch: Exception -> 0x014d, TryCatch #0 {Exception -> 0x014d, blocks: (B:9:0x0020, B:12:0x0060, B:14:0x0068, B:18:0x0078, B:20:0x00a1, B:22:0x00b1, B:24:0x00bb, B:26:0x00ef, B:28:0x00f7, B:30:0x0100, B:38:0x011f, B:42:0x0136, B:32:0x0107, B:34:0x010d, B:36:0x0112, B:35:0x0110, B:37:0x0117, B:27:0x00f3, B:17:0x0074, B:13:0x0064), top: B:49:0x0020 }] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0105  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0133  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0135  */
    @android.annotation.SuppressLint({"RestrictedApi"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String createNotificationShortcut(androidx.core.app.NotificationCompat.Builder r18, long r19, java.lang.String r21, org.telegram.tgnet.TLRPC$User r22, org.telegram.tgnet.TLRPC$Chat r23, androidx.core.app.Person r24) {
        /*
            Method dump skipped, instructions count: 341
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.createNotificationShortcut(androidx.core.app.NotificationCompat$Builder, long, java.lang.String, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, androidx.core.app.Person):java.lang.String");
    }

    @TargetApi(26)
    protected void ensureGroupsCreated() {
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        if (this.groupsCreated == null) {
            this.groupsCreated = Boolean.valueOf(notificationsSettings.getBoolean("groupsCreated4", false));
        }
        if (!this.groupsCreated.booleanValue()) {
            try {
                String str = this.currentAccount + "channel";
                List<NotificationChannel> notificationChannels = systemNotificationManager.getNotificationChannels();
                int size = notificationChannels.size();
                SharedPreferences.Editor editor = null;
                for (int i = 0; i < size; i++) {
                    NotificationChannel notificationChannel = notificationChannels.get(i);
                    String id = notificationChannel.getId();
                    if (id.startsWith(str)) {
                        int importance = notificationChannel.getImportance();
                        if (importance != 4 && importance != 5 && !id.contains("_ia_")) {
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
                        systemNotificationManager.deleteNotificationChannel(id);
                    }
                }
                if (editor != null) {
                    editor.commit();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            notificationsSettings.edit().putBoolean("groupsCreated4", true).commit();
            this.groupsCreated = Boolean.TRUE;
        }
        if (!this.channelGroupsCreated) {
            List<NotificationChannelGroup> notificationChannelGroups = systemNotificationManager.getNotificationChannelGroups();
            String str2 = "channels" + this.currentAccount;
            String str3 = "groups" + this.currentAccount;
            int size2 = notificationChannelGroups.size();
            String str4 = "other" + this.currentAccount;
            String str5 = "private" + this.currentAccount;
            for (int i2 = 0; i2 < size2; i2++) {
                String id2 = notificationChannelGroups.get(i2).getId();
                if (str2 != null && str2.equals(id2)) {
                    str2 = null;
                } else if (str3 != null && str3.equals(id2)) {
                    str3 = null;
                } else if (str5 != null && str5.equals(id2)) {
                    str5 = null;
                } else if (str4 != null && str4.equals(id2)) {
                    str4 = null;
                }
                if (str2 == null && str3 == null && str5 == null && str4 == null) {
                    break;
                }
            }
            if (str2 != null || str3 != null || str5 != null || str4 != null) {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
                if (user == null) {
                    getUserConfig().getCurrentUser();
                }
                String str6 = user != null ? " (" + ContactsController.formatName(user.first_name, user.last_name) + ")" : "";
                ArrayList arrayList = new ArrayList();
                if (str2 != null) {
                    arrayList.add(new NotificationChannelGroup(str2, LocaleController.getString("NotificationsChannels", R.string.NotificationsChannels) + str6));
                }
                if (str3 != null) {
                    arrayList.add(new NotificationChannelGroup(str3, LocaleController.getString("NotificationsGroups", R.string.NotificationsGroups) + str6));
                }
                if (str5 != null) {
                    arrayList.add(new NotificationChannelGroup(str5, LocaleController.getString("NotificationsPrivateChats", R.string.NotificationsPrivateChats) + str6));
                }
                if (str4 != null) {
                    arrayList.add(new NotificationChannelGroup(str4, LocaleController.getString("NotificationsOther", R.string.NotificationsOther) + str6));
                }
                systemNotificationManager.createNotificationChannelGroups(arrayList);
            }
            this.channelGroupsCreated = true;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:191:0x03fe A[LOOP:1: B:189:0x03fb->B:191:0x03fe, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:194:0x0413  */
    /* JADX WARN: Removed duplicated region for block: B:215:0x0461  */
    @android.annotation.TargetApi(26)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.lang.String validateChannelId(long r27, java.lang.String r29, long[] r30, int r31, android.net.Uri r32, int r33, boolean r34, boolean r35, boolean r36, int r37) {
        /*
            Method dump skipped, instructions count: 1358
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARN: Code restructure failed: missing block: B:369:0x087d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 26) goto L325;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x0133, code lost:
        if (r11 == 0) goto L455;
     */
    /* JADX WARN: Code restructure failed: missing block: B:79:0x0135, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("NotificationHiddenChatName", org.telegram.messenger.R.string.NotificationHiddenChatName);
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x013e, code lost:
        r1 = org.telegram.messenger.LocaleController.getString("NotificationHiddenName", org.telegram.messenger.R.string.NotificationHiddenName);
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:101:0x01b3 A[Catch: Exception -> 0x0b29, TRY_ENTER, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:102:0x01ce A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:106:0x021e A[Catch: Exception -> 0x0b29, TRY_ENTER, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:118:0x0294 A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0350 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:152:0x0367  */
    /* JADX WARN: Removed duplicated region for block: B:174:0x042a A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:179:0x044e  */
    /* JADX WARN: Removed duplicated region for block: B:180:0x0451  */
    /* JADX WARN: Removed duplicated region for block: B:183:0x046a A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:192:0x0510  */
    /* JADX WARN: Removed duplicated region for block: B:195:0x051e  */
    /* JADX WARN: Removed duplicated region for block: B:206:0x05a1  */
    /* JADX WARN: Removed duplicated region for block: B:217:0x05fe  */
    /* JADX WARN: Removed duplicated region for block: B:218:0x0602  */
    /* JADX WARN: Removed duplicated region for block: B:221:0x060a A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:227:0x0619  */
    /* JADX WARN: Removed duplicated region for block: B:230:0x061f  */
    /* JADX WARN: Removed duplicated region for block: B:233:0x0624 A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:237:0x0631  */
    /* JADX WARN: Removed duplicated region for block: B:243:0x063c A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:256:0x0660  */
    /* JADX WARN: Removed duplicated region for block: B:267:0x0677  */
    /* JADX WARN: Removed duplicated region for block: B:268:0x067c  */
    /* JADX WARN: Removed duplicated region for block: B:271:0x06b3 A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:309:0x0725 A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:317:0x079e A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:325:0x07e5 A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:340:0x0833  */
    /* JADX WARN: Removed duplicated region for block: B:343:0x083b  */
    /* JADX WARN: Removed duplicated region for block: B:411:0x0982 A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:412:0x098c  */
    /* JADX WARN: Removed duplicated region for block: B:415:0x0993 A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:416:0x09a1  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0119 A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x012b  */
    /* JADX WARN: Removed duplicated region for block: B:86:0x015d A[Catch: Exception -> 0x0b29, TRY_ENTER, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0190  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x019c A[Catch: Exception -> 0x0b29, TryCatch #3 {Exception -> 0x0b29, blocks: (B:11:0x0022, B:13:0x0046, B:15:0x004a, B:17:0x0054, B:19:0x005a, B:23:0x006c, B:27:0x007a, B:29:0x0086, B:30:0x008c, B:32:0x009e, B:34:0x00ac, B:36:0x00b2, B:45:0x00c9, B:47:0x00d9, B:55:0x00f1, B:57:0x00f7, B:61:0x0103, B:63:0x010b, B:68:0x0113, B:70:0x0119, B:83:0x0150, B:86:0x015d, B:88:0x0165, B:91:0x0191, B:93:0x019c, B:103:0x0208, B:106:0x021e, B:111:0x023b, B:117:0x027d, B:143:0x0352, B:155:0x036d, B:157:0x0387, B:160:0x03be, B:162:0x03c8, B:174:0x042a, B:177:0x0444, B:181:0x0453, B:183:0x046a, B:185:0x04b2, B:187:0x04d5, B:189:0x04ec, B:196:0x0520, B:198:0x0530, B:200:0x0543, B:219:0x0604, B:221:0x060a, B:233:0x0624, B:235:0x062a, B:243:0x063c, B:246:0x0646, B:249:0x064f, B:265:0x0672, B:269:0x067d, B:271:0x06b3, B:275:0x06c2, B:278:0x06ce, B:279:0x06d5, B:281:0x06db, B:284:0x06e0, B:286:0x06e9, B:289:0x06f1, B:291:0x06f5, B:293:0x06f9, B:295:0x0701, B:315:0x0740, B:318:0x07a0, B:320:0x07a4, B:322:0x07aa, B:323:0x07c0, B:325:0x07e5, B:327:0x07f2, B:348:0x0842, B:375:0x088a, B:383:0x08c7, B:385:0x08cf, B:387:0x08d3, B:389:0x08db, B:393:0x08e6, B:411:0x0982, B:415:0x0993, B:431:0x09f4, B:433:0x09fa, B:435:0x09fe, B:437:0x0a09, B:439:0x0a0f, B:441:0x0a19, B:443:0x0a28, B:445:0x0a38, B:447:0x0a57, B:448:0x0a5c, B:450:0x0a88, B:451:0x0a99, B:455:0x0abc, B:457:0x0ac2, B:459:0x0aca, B:461:0x0ad0, B:463:0x0ae2, B:464:0x0af9, B:465:0x0b0f, B:418:0x09a4, B:425:0x09c5, B:428:0x09d9, B:394:0x0910, B:395:0x0915, B:396:0x0918, B:398:0x0920, B:401:0x092a, B:403:0x0932, B:407:0x0970, B:408:0x0978, B:377:0x0892, B:379:0x089a, B:381:0x08c2, B:430:0x09e2, B:358:0x0856, B:362:0x0863, B:365:0x086c, B:368:0x0877, B:299:0x070b, B:301:0x0711, B:303:0x0715, B:305:0x071d, B:309:0x0725, B:311:0x0730, B:313:0x0736, B:199:0x053c, B:201:0x0560, B:203:0x0572, B:205:0x0585, B:204:0x057e, B:208:0x05ac, B:210:0x05b6, B:212:0x05c9, B:211:0x05c2, B:186:0x04be, B:163:0x03e2, B:165:0x03f7, B:166:0x0403, B:168:0x0407, B:112:0x024f, B:114:0x0254, B:115:0x0268, B:118:0x0294, B:120:0x02b8, B:122:0x02d0, B:127:0x02da, B:128:0x02e0, B:132:0x02ed, B:133:0x0301, B:135:0x0306, B:136:0x031a, B:137:0x032d, B:139:0x0335, B:140:0x033e, B:98:0x01a8, B:101:0x01b3, B:102:0x01ce, B:89:0x0172, B:79:0x0135, B:80:0x013e, B:81:0x0147, B:59:0x00fc, B:60:0x00ff, B:37:0x00b5, B:39:0x00bb, B:22:0x006a, B:405:0x093c, B:258:0x0663), top: B:478:0x0022, inners: #0, #1 }] */
    /* JADX WARN: Type inference failed for: r1v15 */
    /* JADX WARN: Type inference failed for: r6v6 */
    /* JADX WARN: Type inference failed for: r6v7 */
    /* JADX WARN: Type inference failed for: r6v8 */
    /* JADX WARN: Type inference failed for: r6v9 */
    /* JADX WARN: Type inference failed for: r6v97 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showOrUpdateNotification(boolean r48) {
        /*
            Method dump skipped, instructions count: 2867
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    private boolean isSilentMessage(MessageObject messageObject) {
        return messageObject.messageOwner.silent || messageObject.isReactionPush;
    }

    @SuppressLint({"NewApi"})
    private void setNotificationChannel(Notification notification, NotificationCompat.Builder builder, boolean z) {
        if (z) {
            builder.setChannelId(OTHER_NOTIFICATIONS_CHANNEL);
        } else {
            builder.setChannelId(notification.getChannelId());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetNotificationSound(NotificationCompat.Builder builder, long j, String str, long[] jArr, int i, Uri uri, int i2, boolean z, boolean z2, boolean z3, int i3) {
        Uri uri2 = Settings.System.DEFAULT_RINGTONE_URI;
        if (uri2 == null || uri == null || TextUtils.equals(uri2.toString(), uri.toString())) {
            return;
        }
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        String uri3 = uri2.toString();
        String string = LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone);
        if (z) {
            if (i3 == 2) {
                edit.putString("ChannelSound", string);
            } else if (i3 == 0) {
                edit.putString("GroupSound", string);
            } else {
                edit.putString("GlobalSound", string);
            }
            if (i3 == 2) {
                edit.putString("ChannelSoundPath", uri3);
            } else if (i3 == 0) {
                edit.putString("GroupSoundPath", uri3);
            } else {
                edit.putString("GlobalSoundPath", uri3);
            }
            getNotificationsController().lambda$deleteNotificationChannelGlobal$32(i3, -1);
        } else {
            edit.putString("sound_" + j, string);
            edit.putString("sound_path_" + j, uri3);
            lambda$deleteNotificationChannel$31(j, -1);
        }
        edit.commit();
        builder.setChannelId(validateChannelId(j, str, jArr, i, Settings.System.DEFAULT_RINGTONE_URI, i2, z, z2, z3, i3));
        notificationManager.notify(this.notificationId, builder.build());
    }

    /* JADX WARN: Removed duplicated region for block: B:139:0x0344  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x035e  */
    /* JADX WARN: Removed duplicated region for block: B:146:0x0369  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x03c1  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x03cc  */
    /* JADX WARN: Removed duplicated region for block: B:173:0x03f2  */
    /* JADX WARN: Removed duplicated region for block: B:176:0x03fd A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:186:0x0452  */
    /* JADX WARN: Removed duplicated region for block: B:187:0x0463  */
    /* JADX WARN: Removed duplicated region for block: B:192:0x04a5  */
    /* JADX WARN: Removed duplicated region for block: B:195:0x04ba  */
    /* JADX WARN: Removed duplicated region for block: B:202:0x04e3 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:205:0x04f3  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x055d A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:234:0x0571  */
    /* JADX WARN: Removed duplicated region for block: B:242:0x0586 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:253:0x05b5  */
    /* JADX WARN: Removed duplicated region for block: B:263:0x05ee  */
    /* JADX WARN: Removed duplicated region for block: B:268:0x0629  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x068b  */
    /* JADX WARN: Removed duplicated region for block: B:295:0x06a6  */
    /* JADX WARN: Removed duplicated region for block: B:308:0x06df  */
    /* JADX WARN: Removed duplicated region for block: B:311:0x06ea  */
    /* JADX WARN: Removed duplicated region for block: B:317:0x070c  */
    /* JADX WARN: Removed duplicated region for block: B:348:0x0792  */
    /* JADX WARN: Removed duplicated region for block: B:381:0x0866  */
    /* JADX WARN: Removed duplicated region for block: B:388:0x089d  */
    /* JADX WARN: Removed duplicated region for block: B:391:0x08ac A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:396:0x08be  */
    /* JADX WARN: Removed duplicated region for block: B:404:0x0902  */
    /* JADX WARN: Removed duplicated region for block: B:407:0x0914  */
    /* JADX WARN: Removed duplicated region for block: B:413:0x0977  */
    /* JADX WARN: Removed duplicated region for block: B:414:0x0981  */
    /* JADX WARN: Removed duplicated region for block: B:420:0x09ac  */
    /* JADX WARN: Removed duplicated region for block: B:423:0x0a04  */
    /* JADX WARN: Removed duplicated region for block: B:427:0x0a3b  */
    /* JADX WARN: Removed duplicated region for block: B:432:0x0a60  */
    /* JADX WARN: Removed duplicated region for block: B:433:0x0a82  */
    /* JADX WARN: Removed duplicated region for block: B:436:0x0b31  */
    /* JADX WARN: Removed duplicated region for block: B:438:0x0b3c  */
    /* JADX WARN: Removed duplicated region for block: B:440:0x0b41  */
    /* JADX WARN: Removed duplicated region for block: B:443:0x0b4b  */
    /* JADX WARN: Removed duplicated region for block: B:449:0x0b5f  */
    /* JADX WARN: Removed duplicated region for block: B:451:0x0b64  */
    /* JADX WARN: Removed duplicated region for block: B:454:0x0b70  */
    /* JADX WARN: Removed duplicated region for block: B:460:0x0b7f  */
    /* JADX WARN: Removed duplicated region for block: B:473:0x0CLASSNAME A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:482:0x0CLASSNAME  */
    /* JADX WARN: Removed duplicated region for block: B:528:0x04fd A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:556:0x0924 A[ADDED_TO_REGION, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0206  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x020f  */
    @android.annotation.SuppressLint({"InlinedApi"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r70, java.lang.String r71, long r72, java.lang.String r74, long[] r75, int r76, android.net.Uri r77, int r78, boolean r79, boolean r80, boolean r81, int r82) {
        /*
            Method dump skipped, instructions count: 3513
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(androidx.core.app.NotificationCompat$Builder, java.lang.String, long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.messenger.NotificationsController$1NotificationHolder  reason: invalid class name */
    /* loaded from: classes.dex */
    public class C1NotificationHolder {
        TLRPC$Chat chat;
        long dialogId;
        int id;
        String name;
        NotificationCompat.Builder notification;
        TLRPC$User user;
        final /* synthetic */ String val$chatName;
        final /* synthetic */ int val$chatType;
        final /* synthetic */ int val$importance;
        final /* synthetic */ boolean val$isDefault;
        final /* synthetic */ boolean val$isInApp;
        final /* synthetic */ boolean val$isSilent;
        final /* synthetic */ int val$ledColor;
        final /* synthetic */ Uri val$sound;
        final /* synthetic */ long[] val$vibrationPattern;

        C1NotificationHolder(int i, long j, String str, TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, NotificationCompat.Builder builder, String str2, long[] jArr, int i2, Uri uri, int i3, boolean z, boolean z2, boolean z3, int i4) {
            this.val$chatName = str2;
            this.val$vibrationPattern = jArr;
            this.val$ledColor = i2;
            this.val$sound = uri;
            this.val$importance = i3;
            this.val$isDefault = z;
            this.val$isInApp = z2;
            this.val$isSilent = z3;
            this.val$chatType = i4;
            this.id = i;
            this.name = str;
            this.user = tLRPC$User;
            this.chat = tLRPC$Chat;
            this.notification = builder;
            this.dialogId = j;
        }

        void call() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("show dialog notification with id " + this.id + " " + this.dialogId + " user=" + this.user + " chat=" + this.chat);
            }
            try {
                NotificationsController.notificationManager.notify(this.id, this.notification.build());
            } catch (SecurityException e) {
                FileLog.e(e);
                NotificationsController.this.resetNotificationSound(this.notification, this.dialogId, this.val$chatName, this.val$vibrationPattern, this.val$ledColor, this.val$sound, this.val$importance, this.val$isDefault, this.val$isInApp, this.val$isSilent, this.val$chatType);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$showExtraNotifications$34(Uri uri) {
        ApplicationLoader.applicationContext.revokeUriPermission(uri, 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadRoundAvatar$36(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        imageDecoder.setPostProcessor(NotificationsController$$ExternalSyntheticLambda1.INSTANCE);
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

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$loadRoundAvatar$35(Canvas canvas) {
        Path path = new Path();
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
        int width = canvas.getWidth();
        float f = width / 2;
        path.addRoundRect(0.0f, 0.0f, width, canvas.getHeight(), f, f, Path.Direction.CW);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        canvas.drawPath(path, paint);
        return -3;
    }

    public void playOutChatSound() {
        if (!this.inChatSoundEnabled || MediaController.getInstance().isRecordingAudio()) {
            return;
        }
        try {
            if (audioManager.getRingerMode() == 0) {
                return;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        notificationsQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsController.this.lambda$playOutChatSound$38();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$playOutChatSound$38() {
        try {
            if (Math.abs(SystemClock.elapsedRealtime() - this.lastSoundOutPlay) <= 100) {
                return;
            }
            this.lastSoundOutPlay = SystemClock.elapsedRealtime();
            if (this.soundPool == null) {
                SoundPool soundPool = new SoundPool(3, 1, 0);
                this.soundPool = soundPool;
                soundPool.setOnLoadCompleteListener(NotificationsController$$ExternalSyntheticLambda2.INSTANCE);
            }
            if (this.soundOut == 0 && !this.soundOutLoaded) {
                this.soundOutLoaded = true;
                this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_out, 1);
            }
            int i = this.soundOut;
            if (i == 0) {
                return;
            }
            try {
                this.soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$playOutChatSound$37(SoundPool soundPool, int i, int i2) {
        if (i2 == 0) {
            try {
                soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void clearDialogNotificationsSettings(long j) {
        SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
        SharedPreferences.Editor remove = edit.remove("notify2_" + j);
        remove.remove("custom_" + j);
        getMessagesStorage().setDialogFlags(j, 0L);
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
            getMessagesStorage().setDialogFlags(j, 0L);
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
        if (DialogObject.isEncryptedDialog(j)) {
            return;
        }
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
        long j2 = notificationsSettings.getLong("sound_document_id_" + j, 0L);
        String string = notificationsSettings.getString("sound_path_" + j, null);
        TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings4 = tLRPC$TL_account_updateNotifySettings.settings;
        tLRPC$TL_inputPeerNotifySettings4.flags = tLRPC$TL_inputPeerNotifySettings4.flags | 8;
        if (j2 != 0) {
            TLRPC$TL_notificationSoundRingtone tLRPC$TL_notificationSoundRingtone = new TLRPC$TL_notificationSoundRingtone();
            tLRPC$TL_notificationSoundRingtone.id = j2;
            tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundRingtone;
        } else if (string != null) {
            if (string.equals("NoSound")) {
                tLRPC$TL_account_updateNotifySettings.settings.sound = new TLRPC$TL_notificationSoundNone();
            } else {
                TLRPC$TL_notificationSoundLocal tLRPC$TL_notificationSoundLocal = new TLRPC$TL_notificationSoundLocal();
                tLRPC$TL_notificationSoundLocal.title = notificationsSettings.getString("sound_" + j, null);
                tLRPC$TL_notificationSoundLocal.data = string;
                tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundLocal;
            }
        } else {
            tLRPC$TL_inputPeerNotifySettings4.sound = new TLRPC$TL_notificationSoundDefault();
        }
        TLRPC$TL_inputNotifyPeer tLRPC$TL_inputNotifyPeer = new TLRPC$TL_inputNotifyPeer();
        tLRPC$TL_account_updateNotifySettings.peer = tLRPC$TL_inputNotifyPeer;
        tLRPC$TL_inputNotifyPeer.peer = getMessagesController().getInputPeer(j);
        getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, NotificationsController$$ExternalSyntheticLambda40.INSTANCE);
    }

    public void updateServerNotificationsSettings(int i) {
        String str;
        String str2;
        String str3;
        SharedPreferences notificationsSettings = getAccountInstance().getNotificationsSettings();
        TLRPC$TL_account_updateNotifySettings tLRPC$TL_account_updateNotifySettings = new TLRPC$TL_account_updateNotifySettings();
        TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings = new TLRPC$TL_inputPeerNotifySettings();
        tLRPC$TL_account_updateNotifySettings.settings = tLRPC$TL_inputPeerNotifySettings;
        tLRPC$TL_inputPeerNotifySettings.flags = 5;
        if (i == 0) {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyChats();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableGroup2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewGroup", true);
            str = "GroupSound";
            str2 = "GroupSoundDocId";
            str3 = "GroupSoundPath";
        } else if (i == 1) {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyUsers();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableAll2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewAll", true);
            str = "GlobalSound";
            str2 = "GlobalSoundDocId";
            str3 = "GlobalSoundPath";
        } else {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyBroadcasts();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableChannel2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewChannel", true);
            str = "ChannelSound";
            str2 = "ChannelSoundDocId";
            str3 = "ChannelSoundPath";
        }
        tLRPC$TL_account_updateNotifySettings.settings.flags |= 8;
        long j = notificationsSettings.getLong(str2, 0L);
        String string = notificationsSettings.getString(str3, "NoSound");
        if (j != 0) {
            TLRPC$TL_notificationSoundRingtone tLRPC$TL_notificationSoundRingtone = new TLRPC$TL_notificationSoundRingtone();
            tLRPC$TL_notificationSoundRingtone.id = j;
            tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundRingtone;
        } else if (string != null) {
            if (string.equals("NoSound")) {
                tLRPC$TL_account_updateNotifySettings.settings.sound = new TLRPC$TL_notificationSoundNone();
            } else {
                TLRPC$TL_notificationSoundLocal tLRPC$TL_notificationSoundLocal = new TLRPC$TL_notificationSoundLocal();
                tLRPC$TL_notificationSoundLocal.title = notificationsSettings.getString(str, null);
                tLRPC$TL_notificationSoundLocal.data = string;
                tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundLocal;
            }
        } else {
            tLRPC$TL_account_updateNotifySettings.settings.sound = new TLRPC$TL_notificationSoundDefault();
        }
        getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, NotificationsController$$ExternalSyntheticLambda39.INSTANCE);
    }

    public boolean isGlobalNotificationsEnabled(long j) {
        return isGlobalNotificationsEnabled(j, null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:12:0x0028, code lost:
        if (r4.megagroup == false) goto L7;
     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x000e, code lost:
        if (r6.booleanValue() != false) goto L7;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean isGlobalNotificationsEnabled(long r4, java.lang.Boolean r6) {
        /*
            r3 = this;
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r4)
            r1 = 2
            r2 = 0
            if (r0 == 0) goto L2b
            if (r6 == 0) goto L13
            boolean r4 = r6.booleanValue()
            if (r4 == 0) goto L11
            goto L2c
        L11:
            r1 = 0
            goto L2c
        L13:
            org.telegram.messenger.MessagesController r6 = r3.getMessagesController()
            long r4 = -r4
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r6.getChat(r4)
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r5 == 0) goto L11
            boolean r4 = r4.megagroup
            if (r4 != 0) goto L11
            goto L2c
        L2b:
            r1 = 1
        L2c:
            boolean r4 = r3.isGlobalNotificationsEnabled(r1)
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

    public void muteDialog(long j, boolean z) {
        if (z) {
            getInstance(this.currentAccount).muteUntil(j, Integer.MAX_VALUE);
            return;
        }
        boolean isGlobalNotificationsEnabled = getInstance(this.currentAccount).isGlobalNotificationsEnabled(j);
        SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        if (isGlobalNotificationsEnabled) {
            edit.remove("notify2_" + j);
        } else {
            edit.putInt("notify2_" + j, 0);
        }
        getMessagesStorage().setDialogFlags(j, 0L);
        edit.apply();
        TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(j);
        if (tLRPC$Dialog != null) {
            tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
        }
        updateServerNotificationsSettings(j);
    }
}
