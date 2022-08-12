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
import org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler;
import org.telegram.tgnet.TLRPC$TL_notificationSoundDefault;
import org.telegram.tgnet.TLRPC$TL_notificationSoundLocal;
import org.telegram.tgnet.TLRPC$TL_notificationSoundNone;
import org.telegram.tgnet.TLRPC$TL_notificationSoundRingtone;
import org.telegram.tgnet.TLRPC$TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.PopupNotificationActivity;

public class NotificationsController extends BaseController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = new NotificationsController[4];
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
    private static final Object[] lockObjects = new Object[4];
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
                j2 = 1;
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
            if (r14 >= r1) goto L_0x024a
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
            goto L_0x023f
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
            goto L_0x023f
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
            if (r1 == 0) goto L_0x022a
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
            goto L_0x022a
        L_0x0225:
            r11 = r5
            r22 = r9
            r0 = r26
        L_0x022a:
            boolean r1 = r12.isReactionPush
            if (r1 == 0) goto L_0x023e
            android.util.SparseBooleanArray r1 = new android.util.SparseBooleanArray
            r1.<init>()
            r2 = 1
            r1.put(r11, r2)
            org.telegram.messenger.MessagesController r2 = r29.getMessagesController()
            r2.checkUnreadReactions(r13, r1)
        L_0x023e:
            r15 = 1
        L_0x023f:
            int r14 = r21 + 1
            r8 = r30
            r9 = r22
            r12 = r23
            r11 = 1
            goto L_0x001f
        L_0x024a:
            r26 = r0
            if (r15 == 0) goto L_0x0252
            r0 = r33
            r7.notifyCheck = r0
        L_0x0252:
            boolean r0 = r31.isEmpty()
            if (r0 != 0) goto L_0x026e
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x026e
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x026e
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda31 r0 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda31
            r2 = r31
            r13 = r26
            r0.<init>(r7, r2, r13)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
        L_0x026e:
            if (r32 != 0) goto L_0x0272
            if (r17 == 0) goto L_0x0326
        L_0x0272:
            if (r16 == 0) goto L_0x0280
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.delayedPushMessages
            r0.clear()
            boolean r0 = r7.notifyCheck
            r7.showOrUpdateNotification(r0)
            goto L_0x0326
        L_0x0280:
            if (r15 == 0) goto L_0x0326
            r0 = r30
            r2 = 0
            java.lang.Object r0 = r0.get(r2)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r2 = r0.getDialogId()
            boolean r4 = r0.isFcmMessage()
            if (r4 == 0) goto L_0x029c
            boolean r0 = r0.localChannel
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r0)
            goto L_0x029d
        L_0x029c:
            r4 = 0
        L_0x029d:
            int r0 = r7.total_unread_count
            int r5 = r7.getNotifyOverride(r10, r2)
            r1 = -1
            if (r5 != r1) goto L_0x02ab
            boolean r1 = r7.isGlobalNotificationsEnabled(r2, r4)
            goto L_0x02b1
        L_0x02ab:
            r1 = 2
            if (r5 == r1) goto L_0x02b0
            r1 = 1
            goto L_0x02b1
        L_0x02b0:
            r1 = 0
        L_0x02b1:
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r7.pushDialogs
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x02c2
            int r5 = r4.intValue()
            r6 = 1
            int r5 = r5 + r6
            goto L_0x02c4
        L_0x02c2:
            r6 = 1
            r5 = 1
        L_0x02c4:
            boolean r8 = r7.notifyCheck
            if (r8 == 0) goto L_0x02e0
            if (r1 != 0) goto L_0x02e0
            androidx.collection.LongSparseArray<java.lang.Integer> r8 = r7.pushDialogsOverrideMention
            java.lang.Object r8 = r8.get(r2)
            java.lang.Integer r8 = (java.lang.Integer) r8
            if (r8 == 0) goto L_0x02e0
            int r9 = r8.intValue()
            if (r9 == 0) goto L_0x02e0
            int r5 = r8.intValue()
            r11 = 1
            goto L_0x02e1
        L_0x02e0:
            r11 = r1
        L_0x02e1:
            if (r11 == 0) goto L_0x02fc
            if (r4 == 0) goto L_0x02ee
            int r1 = r7.total_unread_count
            int r4 = r4.intValue()
            int r1 = r1 - r4
            r7.total_unread_count = r1
        L_0x02ee:
            int r1 = r7.total_unread_count
            int r1 = r1 + r5
            r7.total_unread_count = r1
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r7.pushDialogs
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r1.put(r2, r4)
        L_0x02fc:
            int r1 = r7.total_unread_count
            if (r0 == r1) goto L_0x0318
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r7.delayedPushMessages
            r0.clear()
            boolean r0 = r7.notifyCheck
            r7.showOrUpdateNotification(r0)
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r7.pushDialogs
            int r0 = r0.size()
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda20 r1 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda20
            r1.<init>(r7, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x0318:
            r0 = 0
            r7.notifyCheck = r0
            boolean r0 = r7.showBadgeNumber
            if (r0 == 0) goto L_0x0326
            int r0 = r29.getTotalAllUnreadCount()
            r7.setBadge(r0)
        L_0x0326:
            if (r34 == 0) goto L_0x032b
            r34.countDown()
        L_0x032b:
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
        int i2;
        int i3 = 0;
        for (int i4 = 0; i4 < 4; i4++) {
            if (UserConfig.getInstance(i4).isClientActivated()) {
                NotificationsController instance = getInstance(i4);
                if (instance.showBadgeNumber) {
                    if (instance.showBadgeMessages) {
                        if (instance.showBadgeMuted) {
                            try {
                                ArrayList arrayList = new ArrayList(MessagesController.getInstance(i4).allDialogs);
                                int size = arrayList.size();
                                for (int i5 = 0; i5 < size; i5++) {
                                    TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) arrayList.get(i5);
                                    if (tLRPC$Dialog == null || !DialogObject.isChatDialog(tLRPC$Dialog.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog.id)))) {
                                        if (!(tLRPC$Dialog == null || (i2 = tLRPC$Dialog.unread_count) == 0)) {
                                            i3 += i2;
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
                            int size2 = MessagesController.getInstance(i4).allDialogs.size();
                            for (int i6 = 0; i6 < size2; i6++) {
                                TLRPC$Dialog tLRPC$Dialog2 = MessagesController.getInstance(i4).allDialogs.get(i6);
                                if (!DialogObject.isChatDialog(tLRPC$Dialog2.id) || !ChatObject.isNotInChat(getMessagesController().getChat(Long.valueOf(-tLRPC$Dialog2.id)))) {
                                    if (tLRPC$Dialog2.unread_count != 0) {
                                        i3++;
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                        }
                    } else {
                        i = instance.pushDialogs.size();
                    }
                    i3 += i;
                }
            }
        }
        return i3;
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

    /* JADX WARNING: type inference failed for: r2v254 */
    /* JADX WARNING: type inference failed for: r2v255 */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x024f, code lost:
        if (r12.getBoolean("EnablePreviewAll", true) == false) goto L_0x0253;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x025d, code lost:
        if (r12.getBoolean("EnablePreviewGroup", r15) != false) goto L_0x0269;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0267, code lost:
        if (r12.getBoolean(r24, r15) != false) goto L_0x0269;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x0269, code lost:
        r1 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x0275, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0f0c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:0x0277, code lost:
        r27[0] = null;
        r2 = r1.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x027f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) == false) goto L_0x0288;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x0287, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x028a, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L_0x0efd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x028e, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L_0x0292;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x0294, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L_0x02a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x02a4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", org.telegram.messenger.R.string.NotificationContactNewPhoto, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02a8, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L_0x0307;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x02aa, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", org.telegram.messenger.R.string.formatDateAtTime, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(((long) r0.messageOwner.date) * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(((long) r0.messageOwner.date) * 1000));
        r2 = org.telegram.messenger.R.string.NotificationUnrecognizedDevice;
        r0 = r0.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x0306, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", r2, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x0309, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L_0x0ef6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x030d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L_0x0311;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x0313, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L_0x032b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x0317, code lost:
        if (r2.video == false) goto L_0x0322;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0321, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageVideoIncomingMissed", org.telegram.messenger.R.string.CallMessageVideoIncomingMissed);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x032a, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", org.telegram.messenger.R.string.CallMessageIncomingMissed);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x032d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x0444;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x032f, code lost:
        r3 = r2.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0335, code lost:
        if (r3 != 0) goto L_0x0351;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x033e, code lost:
        if (r2.users.size() != 1) goto L_0x0351;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0340, code lost:
        r3 = r0.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0355, code lost:
        if (r3 == 0) goto L_0x03ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x035f, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x0379;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0363, code lost:
        if (r8.megagroup != false) goto L_0x0379;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0378, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", org.telegram.messenger.R.string.ChannelAddedByNotification, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x037b, code lost:
        if (r3 != r19) goto L_0x0391;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0390, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", org.telegram.messenger.R.string.NotificationInvitedToGroup, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x0391, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x039d, code lost:
        if (r0 != null) goto L_0x03a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x039f, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x03a5, code lost:
        if (r9 != r0.id) goto L_0x03d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x03a9, code lost:
        if (r8.megagroup == false) goto L_0x03bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x03be, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", org.telegram.messenger.R.string.NotificationGroupAddSelfMega, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x03d2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", org.telegram.messenger.R.string.NotificationGroupAddSelf, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:218:0x03ec, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.R.string.NotificationGroupAddMember, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03ed, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x03fd, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x042a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x03ff, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x0413, code lost:
        if (r3 == null) goto L_0x0427;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x0415, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x041d, code lost:
        if (r1.length() == 0) goto L_0x0424;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x041f, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x0424, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x0427, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0443, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", org.telegram.messenger.R.string.NotificationGroupAddMember, r7, r8.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0447, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L_0x045c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x045b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", org.telegram.messenger.R.string.NotificationGroupCreatedCall, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x045e, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) == false) goto L_0x0467;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0466, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0469, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L_0x052c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x046b, code lost:
        r3 = r2.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x0471, code lost:
        if (r3 != 0) goto L_0x048d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x047a, code lost:
        if (r2.users.size() != 1) goto L_0x048d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x047c, code lost:
        r3 = r0.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x0491, code lost:
        if (r3 == 0) goto L_0x04d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x0495, code lost:
        if (r3 != r19) goto L_0x04ab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x04aa, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x04ab, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04b7, code lost:
        if (r0 != null) goto L_0x04bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04b9, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x04d4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.R.string.NotificationGroupInvitedToCall, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04d5, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04e5, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x0512;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04e7, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04fb, code lost:
        if (r3 == null) goto L_0x050f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04fd, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x0505, code lost:
        if (r1.length() == 0) goto L_0x050c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x0507, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x050c, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x050f, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x052b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", org.telegram.messenger.R.string.NotificationGroupInvitedToCall, r7, r8.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x052f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x0545;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0544, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", org.telegram.messenger.R.string.NotificationInvitedToGroupByLink, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x054a, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x055d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x055c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", org.telegram.messenger.R.string.NotificationEditedGroupName, r7, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x055f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0e93;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0563, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x0567;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0569, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x05ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:283:0x056b, code lost:
        r1 = r2.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x056f, code lost:
        if (r1 != r19) goto L_0x0585;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0584, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", org.telegram.messenger.R.string.NotificationGroupKickYou, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x058a, code lost:
        if (r1 != r9) goto L_0x059d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x059c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", org.telegram.messenger.R.string.NotificationGroupLeftMember, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x059d, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x05af, code lost:
        if (r0 != null) goto L_0x05b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x05b1, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x05cd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", org.telegram.messenger.R.string.NotificationGroupKickMember, r7, r8.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x05d0, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x05d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x05d8, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x05db, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x05e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x05e3, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x05e6, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x05f9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x05f8, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.R.string.ActionMigrateFromGroupNotify, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05fd, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x060e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x060d, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", org.telegram.messenger.R.string.ActionMigrateFromGroupNotify, r2.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x0610, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x0619;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x0618, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x061b, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0e33;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0621, code lost:
        if (r8 == null) goto L_0x0914;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x0627, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r8) == false) goto L_0x062d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x062b, code lost:
        if (r8.megagroup == false) goto L_0x0914;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x062d, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x062f, code lost:
        if (r0 != null) goto L_0x0645;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x0644, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.R.string.NotificationActionPinnedNoText, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x064c, code lost:
        if (r0.isMusic() == false) goto L_0x065f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x065e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", org.telegram.messenger.R.string.NotificationActionPinnedMusic, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x0665, code lost:
        if (r0.isVideo() == false) goto L_0x06b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x066b, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x069f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x0675, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x069f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x069e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r7, "📹 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x06b2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", org.telegram.messenger.R.string.NotificationActionPinnedVideo, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x06b7, code lost:
        if (r0.isGif() == false) goto L_0x0705;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x06bd, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x06f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x06c7, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x06f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x06f0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r7, "🎬 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x0704, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", org.telegram.messenger.R.string.NotificationActionPinnedGif, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x070c, code lost:
        if (r0.isVoice() == false) goto L_0x071f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x071e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", org.telegram.messenger.R.string.NotificationActionPinnedVoice, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x0723, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0736;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x0735, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", org.telegram.messenger.R.string.NotificationActionPinnedRound, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x073a, code lost:
        if (r0.isSticker() != false) goto L_0x08e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x0740, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0744;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x0744, code lost:
        r3 = r0.messageOwner;
        r5 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x074a, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0796;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x0750, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0782;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x0758, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0782;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x0781, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r7, "📎 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x0795, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", org.telegram.messenger.R.string.NotificationActionPinnedFile, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x0798, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x08d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x079c, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x07a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x07a2, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x07b8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x07b7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", org.telegram.messenger.R.string.NotificationActionPinnedGeoLive, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x07bc, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x07dd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x07be, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x07dc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", org.telegram.messenger.R.string.NotificationActionPinnedContact2, r7, r8.title, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x07df, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x081b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x07e1, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x07e7, code lost:
        if (r0.quiz == false) goto L_0x0802;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x0801, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", org.telegram.messenger.R.string.NotificationActionPinnedQuiz2, r7, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x081a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", org.telegram.messenger.R.string.NotificationActionPinnedPoll2, r7, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x081d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0869;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0823, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0855;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x082b, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0855;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x0854, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", org.telegram.messenger.R.string.NotificationActionPinnedText, r7, "🖼 " + r0.messageOwner.message, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0868, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", org.telegram.messenger.R.string.NotificationActionPinnedPhoto, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x086e, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0881;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0880, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", org.telegram.messenger.R.string.NotificationActionPinnedGame, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:408:0x0881, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x0883, code lost:
        if (r3 == null) goto L_0x08bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0889, code lost:
        if (r3.length() <= 0) goto L_0x08bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x088b, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0891, code lost:
        if (r0.length() <= 20) goto L_0x08a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x0893, code lost:
        r3 = new java.lang.StringBuilder();
        r5 = 0;
        r3.append(r0.subSequence(0, 20));
        r3.append("...");
        r0 = r3.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x08a8, code lost:
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:416:0x08a9, code lost:
        r1 = org.telegram.messenger.R.string.NotificationActionPinnedText;
        r2 = new java.lang.Object[3];
        r2[r5] = r7;
        r2[1] = r0;
        r2[2] = r8.title;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x08bc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x08d0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", org.telegram.messenger.R.string.NotificationActionPinnedNoText, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00b3, code lost:
        if (r12.getBoolean("EnablePreviewGroup", true) != false) goto L_0x00b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x08e4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", org.telegram.messenger.R.string.NotificationActionPinnedGeo, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:422:0x08e5, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x08eb, code lost:
        if (r0 == null) goto L_0x0902;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x0901, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji, r7, r8.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x0913, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", org.telegram.messenger.R.string.NotificationActionPinnedSticker, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x0915, code lost:
        if (r8 == null) goto L_0x0bbc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:430:0x0917, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0919, code lost:
        if (r0 != null) goto L_0x092b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x092a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0930, code lost:
        if (r0.isMusic() == false) goto L_0x0941;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0940, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", org.telegram.messenger.R.string.NotificationActionPinnedMusicChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0947, code lost:
        if (r0.isVideo() == false) goto L_0x098f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x094d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x097e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x0957, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x097e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x097d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r8.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x098e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0993, code lost:
        if (r0.isGif() == false) goto L_0x09db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0999, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x09ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x09a3, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x09ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x09c9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r8.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x09da, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", org.telegram.messenger.R.string.NotificationActionPinnedGifChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x09e1, code lost:
        if (r0.isVoice() == false) goto L_0x09f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x09f1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x09f6, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0a07;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0a06, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0a0b, code lost:
        if (r0.isSticker() != false) goto L_0x0b92;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0a11, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0a15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00bf, code lost:
        if (r12.getBoolean("EnablePreviewChannel", r2) == false) goto L_0x00c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x0a15, code lost:
        r3 = r0.messageOwner;
        r5 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0a1b, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0a61;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0a21, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0a50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0a29, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0a50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0a4f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r8.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0a60, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", org.telegram.messenger.R.string.NotificationActionPinnedFileChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0a63, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0b81;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0a67, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0a6b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0a6d, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0a80;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0a7f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0a83, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0aa2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:490:0x0a85, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0aa1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2, r8.title, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0aa4, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0ada;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0aa6, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0aac, code lost:
        if (r0.quiz == false) goto L_0x0ac4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0ac3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x0ad9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2, r8.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x0adc, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0b22;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0ae2, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0b11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0aea, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0b11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0b10, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedTextChannel, r8.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0b21, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0b26, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0b37;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0b36, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", org.telegram.messenger.R.string.NotificationActionPinnedGameChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0b37, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0b39, code lost:
        if (r3 == null) goto L_0x0b70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x0b3f, code lost:
        if (r3.length() <= 0) goto L_0x0b70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0b41, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0b47, code lost:
        if (r0.length() <= 20) goto L_0x0b5e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0b49, code lost:
        r3 = new java.lang.StringBuilder();
        r9 = 0;
        r3.append(r0.subSequence(0, 20));
        r3.append("...");
        r0 = r3.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0b5e, code lost:
        r9 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0b5f, code lost:
        r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel;
        r2 = new java.lang.Object[2];
        r2[r9] = r8.title;
        r2[1] = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0b6f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0b80, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0b91, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0b92, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0b97, code lost:
        if (r0 == null) goto L_0x0bac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0bab, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel, r8.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0bbb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0bbc, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0bbf, code lost:
        if (r0 != null) goto L_0x0bce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0bcd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0bd2, code lost:
        if (r0.isMusic() == false) goto L_0x0be1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0be0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicUser", org.telegram.messenger.R.string.NotificationActionPinnedMusicUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0be7, code lost:
        if (r0.isVideo() == false) goto L_0x0c2b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0bed, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0c1c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0bf7, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0c1c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0c1b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r7, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0c2a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", org.telegram.messenger.R.string.NotificationActionPinnedVideoUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0c2f, code lost:
        if (r0.isGif() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0CLASSNAME, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0c3f, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r7, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", org.telegram.messenger.R.string.NotificationActionPinnedGifUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0CLASSNAME, code lost:
        if (r0.isVoice() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", org.telegram.messenger.R.string.NotificationActionPinnedVoiceUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0c8c, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0c9b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0c9a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", org.telegram.messenger.R.string.NotificationActionPinnedRoundUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0c9f, code lost:
        if (r0.isSticker() != false) goto L_0x0e0e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0ca5, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0ca9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0ca9, code lost:
        r3 = r0.messageOwner;
        r5 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0caf, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0cf1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0cb5, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0ce2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0cbd, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0ce2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0ce1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r7, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0cf0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", org.telegram.messenger.R.string.NotificationActionPinnedFileUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0cf3, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0dff;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0cf7, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0cfb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0cfd, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0d0e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0d0d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0d11, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0d2e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0d13, code lost:
        r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0d2d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", org.telegram.messenger.R.string.NotificationActionPinnedContactUser, r7, org.telegram.messenger.ContactsController.formatName(r5.first_name, r5.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0d30, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0d62;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0d32, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0d38, code lost:
        if (r0.quiz == false) goto L_0x0d4e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0d4d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", org.telegram.messenger.R.string.NotificationActionPinnedQuizUser, r7, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0d61, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", org.telegram.messenger.R.string.NotificationActionPinnedPollUser, r7, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0d64, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0da6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0d6a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0d97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0d72, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0d97;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0d96, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", org.telegram.messenger.R.string.NotificationActionPinnedTextUser, r7, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0da5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", org.telegram.messenger.R.string.NotificationActionPinnedPhotoUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0daa, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0db9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0db8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", org.telegram.messenger.R.string.NotificationActionPinnedGameUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0db9, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x0dbb, code lost:
        if (r3 == null) goto L_0x0df0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0dc1, code lost:
        if (r3.length() <= 0) goto L_0x0df0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0dc3, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0dc9, code lost:
        if (r0.length() <= 20) goto L_0x0de0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0dcb, code lost:
        r3 = new java.lang.StringBuilder();
        r5 = 0;
        r3.append(r0.subSequence(0, 20));
        r3.append("...");
        r0 = r3.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x0de0, code lost:
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0de1, code lost:
        r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser;
        r2 = new java.lang.Object[2];
        r2[r5] = r7;
        r2[1] = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0def, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0dfe, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x0e0d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", org.telegram.messenger.R.string.NotificationActionPinnedGeoUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0e0e, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0e14, code lost:
        if (r0 == null) goto L_0x0e26;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0e25, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiUser, r7, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0e32, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", org.telegram.messenger.R.string.NotificationActionPinnedStickerUser, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0e35, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) == false) goto L_0x0e86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0e37, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r2).emoticon;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0e3f, code lost:
        if (android.text.TextUtils.isEmpty(r0) == false) goto L_0x0e63;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:643:0x0e43, code lost:
        if (r3 != r19) goto L_0x0e51;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:647:0x0e67, code lost:
        if (r3 != r19) goto L_0x0e76;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0e88, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest) == false) goto L_0x0e91;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:653:0x0e90, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0e91, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0e9b, code lost:
        if (r1.peer_id.channel_id == 0) goto L_0x0ec9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0e9f, code lost:
        if (r8.megagroup != false) goto L_0x0ec9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0ea5, code lost:
        if (r26.isVideoAvatar() == false) goto L_0x0eb8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0eb7, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", org.telegram.messenger.R.string.ChannelVideoEditNotification, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0ec8, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", org.telegram.messenger.R.string.ChannelPhotoEditNotification, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0ece, code lost:
        if (r26.isVideoAvatar() == false) goto L_0x0ee3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0ee2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", org.telegram.messenger.R.string.NotificationEditedGroupVideo, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0ef5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", org.telegram.messenger.R.string.NotificationEditedGroupPhoto, r7, r8.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0efc, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0f0b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", org.telegram.messenger.R.string.NotificationContactJoined, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0var_, code lost:
        if (r26.isMediaEmpty() == false) goto L_0x0f2a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0f1a, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0var_, code lost:
        return replaceSpoilers(r26);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString(r23, org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:683:0x0f2a, code lost:
        r1 = r23;
        r2 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0var_, code lost:
        if ((r2.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0var_, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0var_, code lost:
        return "🖼 " + replaceSpoilers(r26);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0f5c, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", org.telegram.messenger.R.string.AttachDestructingPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0f6f, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", org.telegram.messenger.R.string.AttachPhoto);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0var_, code lost:
        if (r26.isVideo() == false) goto L_0x0fb4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0f7a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0f9a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0f9a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0var_, code lost:
        return "📹 " + replaceSpoilers(r26);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0fa0, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0fab;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0faa, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", org.telegram.messenger.R.string.AttachDestructingVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0fb3, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", org.telegram.messenger.R.string.AttachVideo);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0fb8, code lost:
        if (r26.isGame() == false) goto L_0x0fc3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0fc2, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", org.telegram.messenger.R.string.AttachGame);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0fc7, code lost:
        if (r26.isVoice() == false) goto L_0x0fd2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0fd1, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", org.telegram.messenger.R.string.AttachAudio);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0fd6, code lost:
        if (r26.isRoundVideo() == false) goto L_0x0fe1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0fe0, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", org.telegram.messenger.R.string.AttachRound);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0fe5, code lost:
        if (r26.isMusic() == false) goto L_0x0ff0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0fef, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", org.telegram.messenger.R.string.AttachMusic);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:727:0x0ff0, code lost:
        r2 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0ff6, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x1001;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x1000, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", org.telegram.messenger.R.string.AttachContact);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x1003, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x101f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x100b, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2).poll.quiz == false) goto L_0x1016;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x1015, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", org.telegram.messenger.R.string.QuizPoll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x101e, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", org.telegram.messenger.R.string.Poll);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x1021, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x10e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x1025, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x1029;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x102b, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x1036;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x1035, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", org.telegram.messenger.R.string.AttachLiveLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x1038, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x10d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x103e, code lost:
        if (r26.isSticker() != false) goto L_0x10a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x1044, code lost:
        if (r26.isAnimatedSticker() == false) goto L_0x1047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x104b, code lost:
        if (r26.isGif() == false) goto L_0x107a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x1051, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x1071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x105b, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x1071;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x1070, code lost:
        return "🎬 " + replaceSpoilers(r26);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x1079, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", org.telegram.messenger.R.string.AttachGif);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x107e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x109e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x1088, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x109e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x109d, code lost:
        return "📎 " + replaceSpoilers(r26);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x10a6, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", org.telegram.messenger.R.string.AttachDocument);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:771:0x10a7, code lost:
        r0 = r26.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x10ab, code lost:
        if (r0 == null) goto L_0x10ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x10c9, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x10d2, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", org.telegram.messenger.R.string.AttachSticker);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x10d9, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x10e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x10df, code lost:
        return replaceSpoilers(r26);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x10e6, code lost:
        return org.telegram.messenger.LocaleController.getString(r1, org.telegram.messenger.R.string.Message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:784:0x10ef, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLocation", org.telegram.messenger.R.string.AttachLocation);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChangedChatThemeTo", org.telegram.messenger.R.string.ChatThemeChangedTo, r7, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChatThemeDisabledYou", org.telegram.messenger.R.string.ChatThemeDisabledYou, new java.lang.Object[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:800:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChatThemeDisabled", org.telegram.messenger.R.string.ChatThemeDisabled, r7, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:801:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChangedChatThemeYou", org.telegram.messenger.R.string.ChatThemeChangedYou, r0);
     */
    /* JADX WARNING: Incorrect type for immutable var: ssa=boolean, code=?, for r2v253, types: [boolean] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r26, java.lang.String[] r27, boolean[] r28) {
        /*
            r25 = this;
            r0 = r26
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            java.lang.String r2 = "NotificationHiddenMessage"
            if (r1 != 0) goto L_0x10fe
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x0010
            goto L_0x10fe
        L_0x0010:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r3 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r5 = r1.chat_id
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x001f
            goto L_0x0021
        L_0x001f:
            long r5 = r1.channel_id
        L_0x0021:
            long r9 = r1.user_id
            r1 = 1
            r11 = 0
            if (r28 == 0) goto L_0x0029
            r28[r11] = r1
        L_0x0029:
            org.telegram.messenger.AccountInstance r12 = r25.getAccountInstance()
            android.content.SharedPreferences r12 = r12.getNotificationsSettings()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "content_preview_"
            r13.append(r14)
            r13.append(r3)
            java.lang.String r13 = r13.toString()
            boolean r13 = r12.getBoolean(r13, r1)
            boolean r14 = r26.isFcmMessage()
            java.lang.String r15 = "EnablePreviewChannel"
            java.lang.String r1 = "EnablePreviewGroup"
            java.lang.String r11 = "EnablePreviewAll"
            java.lang.String r7 = "Message"
            r8 = 2
            if (r14 == 0) goto L_0x0103
            r17 = 0
            int r2 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r2 != 0) goto L_0x0080
            int r2 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
            if (r2 == 0) goto L_0x0080
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 27
            if (r1 <= r2) goto L_0x006b
            java.lang.String r1 = r0.localName
            r2 = 0
            r27[r2] = r1
            goto L_0x006c
        L_0x006b:
            r2 = 0
        L_0x006c:
            if (r13 == 0) goto L_0x0075
            r1 = 1
            boolean r1 = r12.getBoolean(r11, r1)
            if (r1 != 0) goto L_0x00fe
        L_0x0075:
            if (r28 == 0) goto L_0x0079
            r28[r2] = r2
        L_0x0079:
            int r0 = org.telegram.messenger.R.string.Message
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r7, r0)
            return r0
        L_0x0080:
            r2 = 0
            int r4 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x00fe
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            long r4 = r4.channel_id
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 == 0) goto L_0x00a3
            boolean r2 = r26.isSupergroup()
            if (r2 == 0) goto L_0x0097
            goto L_0x00a3
        L_0x0097:
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 27
            if (r2 <= r3) goto L_0x00a8
            java.lang.String r2 = r0.localName
            r3 = 0
            r27[r3] = r2
            goto L_0x00a8
        L_0x00a3:
            r3 = 0
            java.lang.String r2 = r0.localUserName
            r27[r3] = r2
        L_0x00a8:
            if (r13 == 0) goto L_0x00c1
            boolean r2 = r0.localChannel
            if (r2 != 0) goto L_0x00b6
            r2 = 1
            boolean r1 = r12.getBoolean(r1, r2)
            if (r1 == 0) goto L_0x00c1
            goto L_0x00b7
        L_0x00b6:
            r2 = 1
        L_0x00b7:
            boolean r1 = r0.localChannel
            if (r1 == 0) goto L_0x00fe
            boolean r1 = r12.getBoolean(r15, r2)
            if (r1 != 0) goto L_0x00fe
        L_0x00c1:
            if (r28 == 0) goto L_0x00c6
            r1 = 0
            r28[r1] = r1
        L_0x00c6:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r1 = r1.channel_id
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x00e9
            boolean r1 = r26.isSupergroup()
            if (r1 != 0) goto L_0x00e9
            int r1 = org.telegram.messenger.R.string.ChannelMessageNoText
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r0 = r0.localName
            r3 = 0
            r2[r3] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00e9:
            r3 = 0
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupNoText
            java.lang.Object[] r2 = new java.lang.Object[r8]
            java.lang.String r4 = r0.localUserName
            r2[r3] = r4
            java.lang.String r0 = r0.localName
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationMessageGroupNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00fe:
            java.lang.String r0 = r25.replaceSpoilers(r26)
            return r0
        L_0x0103:
            org.telegram.messenger.UserConfig r14 = r25.getUserConfig()
            long r19 = r14.getClientUserId()
            r17 = 0
            int r14 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
            if (r14 != 0) goto L_0x011b
            long r9 = r26.getFromChatId()
            int r14 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
            if (r14 != 0) goto L_0x0123
            long r9 = -r5
            goto L_0x0123
        L_0x011b:
            int r14 = (r9 > r19 ? 1 : (r9 == r19 ? 0 : -1))
            if (r14 != 0) goto L_0x0123
            long r9 = r26.getFromChatId()
        L_0x0123:
            int r14 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r14 != 0) goto L_0x0132
            int r14 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r14 == 0) goto L_0x012d
            long r3 = -r5
            goto L_0x0132
        L_0x012d:
            int r14 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
            if (r14 == 0) goto L_0x0132
            r3 = r9
        L_0x0132:
            boolean r14 = org.telegram.messenger.UserObject.isReplyUser((long) r3)
            if (r14 == 0) goto L_0x0146
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r14.fwd_from
            if (r14 == 0) goto L_0x0146
            org.telegram.tgnet.TLRPC$Peer r14 = r14.from_id
            if (r14 == 0) goto L_0x0146
            long r9 = org.telegram.messenger.MessageObject.getPeerId(r14)
        L_0x0146:
            r17 = 0
            int r21 = (r9 > r17 ? 1 : (r9 == r17 ? 0 : -1))
            if (r21 <= 0) goto L_0x0184
            org.telegram.messenger.MessagesController r8 = r25.getMessagesController()
            java.lang.Long r14 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r14)
            if (r8 == 0) goto L_0x017c
            java.lang.String r8 = org.telegram.messenger.UserObject.getUserName(r8)
            int r14 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
            if (r14 == 0) goto L_0x016a
            r14 = 0
            r27[r14] = r8
            r23 = r7
            r24 = r15
            goto L_0x0182
        L_0x016a:
            r23 = r7
            r14 = 0
            int r7 = android.os.Build.VERSION.SDK_INT
            r24 = r15
            r15 = 27
            if (r7 <= r15) goto L_0x0178
            r27[r14] = r8
            goto L_0x0182
        L_0x0178:
            r7 = 0
            r27[r14] = r7
            goto L_0x0182
        L_0x017c:
            r23 = r7
            r24 = r15
            r14 = 0
            r8 = 0
        L_0x0182:
            r7 = r8
            goto L_0x019f
        L_0x0184:
            r23 = r7
            r24 = r15
            r14 = 0
            org.telegram.messenger.MessagesController r7 = r25.getMessagesController()
            long r14 = -r9
            java.lang.Long r8 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r7 = r7.getChat(r8)
            if (r7 == 0) goto L_0x019e
            java.lang.String r7 = r7.title
            r8 = 0
            r27[r8] = r7
            goto L_0x019f
        L_0x019e:
            r7 = 0
        L_0x019f:
            if (r7 == 0) goto L_0x01ed
            r14 = 0
            int r8 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
            if (r8 <= 0) goto L_0x01ed
            boolean r8 = org.telegram.messenger.UserObject.isReplyUser((long) r3)
            if (r8 == 0) goto L_0x01ed
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r8 = r8.fwd_from
            if (r8 == 0) goto L_0x01ed
            org.telegram.tgnet.TLRPC$Peer r8 = r8.saved_from_peer
            if (r8 == 0) goto L_0x01ed
            long r14 = org.telegram.messenger.MessageObject.getPeerId(r8)
            boolean r8 = org.telegram.messenger.DialogObject.isChatDialog(r14)
            if (r8 == 0) goto L_0x01ed
            org.telegram.messenger.MessagesController r8 = r25.getMessagesController()
            long r14 = -r14
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r14)
            if (r8 == 0) goto L_0x01ed
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r7)
            java.lang.String r7 = " @ "
            r14.append(r7)
            java.lang.String r7 = r8.title
            r14.append(r7)
            java.lang.String r7 = r14.toString()
            r8 = 0
            r14 = r27[r8]
            if (r14 == 0) goto L_0x01ed
            r27[r8] = r7
        L_0x01ed:
            if (r7 != 0) goto L_0x01f2
            r22 = 0
            return r22
        L_0x01f2:
            r14 = 0
            r22 = 0
            int r8 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r8 == 0) goto L_0x021f
            org.telegram.messenger.MessagesController r8 = r25.getMessagesController()
            java.lang.Long r14 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r14)
            if (r8 != 0) goto L_0x0209
            return r22
        L_0x0209:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r14 == 0) goto L_0x021d
            boolean r14 = r8.megagroup
            if (r14 != 0) goto L_0x021d
            int r14 = android.os.Build.VERSION.SDK_INT
            r15 = 27
            if (r14 > r15) goto L_0x021d
            r14 = 0
            r27[r14] = r22
            goto L_0x0222
        L_0x021d:
            r14 = 0
            goto L_0x0222
        L_0x021f:
            r14 = 0
            r8 = r22
        L_0x0222:
            boolean r15 = org.telegram.messenger.DialogObject.isEncryptedDialog(r3)
            if (r15 == 0) goto L_0x0231
            r27[r14] = r22
            int r0 = org.telegram.messenger.R.string.NotificationHiddenMessage
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            return r0
        L_0x0231:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r2 == 0) goto L_0x023d
            boolean r2 = r8.megagroup
            if (r2 != 0) goto L_0x023d
            r2 = 1
            goto L_0x023e
        L_0x023d:
            r2 = 0
        L_0x023e:
            if (r13 == 0) goto L_0x10f0
            r13 = 0
            int r15 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r15 != 0) goto L_0x0252
            int r15 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r15 == 0) goto L_0x0252
            r15 = 1
            boolean r11 = r12.getBoolean(r11, r15)
            if (r11 != 0) goto L_0x0269
            goto L_0x0253
        L_0x0252:
            r15 = 1
        L_0x0253:
            int r11 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r11 == 0) goto L_0x10f0
            if (r2 != 0) goto L_0x025f
            boolean r1 = r12.getBoolean(r1, r15)
            if (r1 != 0) goto L_0x0269
        L_0x025f:
            if (r2 == 0) goto L_0x10f0
            r1 = r24
            boolean r1 = r12.getBoolean(r1, r15)
            if (r1 == 0) goto L_0x10f0
        L_0x0269:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r5 = "🎬 "
            java.lang.String r6 = "📎 "
            java.lang.String r11 = "📹 "
            java.lang.String r12 = "🖼 "
            if (r2 == 0) goto L_0x0f0c
            r2 = 0
            r14 = 0
            r27[r14] = r2
            org.telegram.tgnet.TLRPC$MessageAction r2 = r1.action
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r14 == 0) goto L_0x0288
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0288:
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r14 != 0) goto L_0x0efd
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r14 == 0) goto L_0x0292
            goto L_0x0efd
        L_0x0292:
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r14 == 0) goto L_0x02a5
            int r0 = org.telegram.messenger.R.string.NotificationContactNewPhoto
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x02a5:
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            r15 = 3
            if (r14 == 0) goto L_0x0307
            int r1 = org.telegram.messenger.R.string.formatDateAtTime
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
            int r2 = org.telegram.messenger.R.string.NotificationUnrecognizedDevice
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r5 = r25.getUserConfig()
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
            r3[r15] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x0307:
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r14 != 0) goto L_0x0ef6
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r14 == 0) goto L_0x0311
            goto L_0x0ef6
        L_0x0311:
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r14 == 0) goto L_0x032b
            boolean r0 = r2.video
            if (r0 == 0) goto L_0x0322
            int r0 = org.telegram.messenger.R.string.CallMessageVideoIncomingMissed
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0322:
            int r0 = org.telegram.messenger.R.string.CallMessageIncomingMissed
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x032b:
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r14 == 0) goto L_0x0444
            long r3 = r2.user_id
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 != 0) goto L_0x0351
            java.util.ArrayList<java.lang.Long> r1 = r2.users
            int r1 = r1.size()
            r2 = 1
            if (r1 != r2) goto L_0x0351
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Long> r1 = r1.users
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            java.lang.Long r1 = (java.lang.Long) r1
            long r3 = r1.longValue()
        L_0x0351:
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 == 0) goto L_0x03ed
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r5 = r0.channel_id
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x0379
            boolean r0 = r8.megagroup
            if (r0 != 0) goto L_0x0379
            int r0 = org.telegram.messenger.R.string.ChannelAddedByNotification
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
        L_0x0379:
            int r0 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r0 != 0) goto L_0x0391
            int r0 = org.telegram.messenger.R.string.NotificationInvitedToGroup
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
        L_0x0391:
            org.telegram.messenger.MessagesController r0 = r25.getMessagesController()
            java.lang.Long r1 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x03a1
            r1 = 0
            return r1
        L_0x03a1:
            long r1 = r0.id
            int r3 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x03d3
            boolean r0 = r8.megagroup
            if (r0 == 0) goto L_0x03bf
            int r0 = org.telegram.messenger.R.string.NotificationGroupAddSelfMega
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
        L_0x03bf:
            r1 = 2
            r2 = 0
            r3 = 1
            int r0 = org.telegram.messenger.R.string.NotificationGroupAddSelf
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03d3:
            r2 = 0
            r3 = 1
            int r1 = org.telegram.messenger.R.string.NotificationGroupAddMember
            java.lang.Object[] r4 = new java.lang.Object[r15]
            r4[r2] = r7
            java.lang.String r2 = r8.title
            r4[r3] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r4[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4)
            return r0
        L_0x03ed:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x03f3:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Long> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x042a
            org.telegram.messenger.MessagesController r3 = r25.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Long> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Long r4 = (java.lang.Long) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0427
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0424
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0424:
            r1.append(r3)
        L_0x0427:
            int r2 = r2 + 1
            goto L_0x03f3
        L_0x042a:
            int r0 = org.telegram.messenger.R.string.NotificationGroupAddMember
            java.lang.Object[] r2 = new java.lang.Object[r15]
            r3 = 0
            r2[r3] = r7
            java.lang.String r3 = r8.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r1 = r1.toString()
            r14 = 2
            r2[r14] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0444:
            r14 = 2
            boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r13 == 0) goto L_0x045c
            int r0 = org.telegram.messenger.R.string.NotificationGroupCreatedCall
            java.lang.Object[] r1 = new java.lang.Object[r14]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupCreatedCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x045c:
            boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            if (r13 == 0) goto L_0x0467
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0467:
            boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r13 == 0) goto L_0x052c
            long r3 = r2.user_id
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 != 0) goto L_0x048d
            java.util.ArrayList<java.lang.Long> r1 = r2.users
            int r1 = r1.size()
            r2 = 1
            if (r1 != r2) goto L_0x048d
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Long> r1 = r1.users
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            java.lang.Long r1 = (java.lang.Long) r1
            long r3 = r1.longValue()
        L_0x048d:
            r1 = 0
            int r5 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r5 == 0) goto L_0x04d5
            int r0 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r0 != 0) goto L_0x04ab
            int r0 = org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = r8.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04ab:
            org.telegram.messenger.MessagesController r0 = r25.getMessagesController()
            java.lang.Long r1 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x04bb
            r1 = 0
            return r1
        L_0x04bb:
            int r1 = org.telegram.messenger.R.string.NotificationGroupInvitedToCall
            java.lang.Object[] r2 = new java.lang.Object[r15]
            r3 = 0
            r2[r3] = r7
            java.lang.String r3 = r8.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x04d5:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x04db:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Long> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0512
            org.telegram.messenger.MessagesController r3 = r25.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Long> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Long r4 = (java.lang.Long) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x050f
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x050c
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x050c:
            r1.append(r3)
        L_0x050f:
            int r2 = r2 + 1
            goto L_0x04db
        L_0x0512:
            int r0 = org.telegram.messenger.R.string.NotificationGroupInvitedToCall
            java.lang.Object[] r2 = new java.lang.Object[r15]
            r3 = 0
            r2[r3] = r7
            java.lang.String r3 = r8.title
            r4 = 1
            r2[r4] = r3
            java.lang.String r1 = r1.toString()
            r13 = 2
            r2[r13] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x052c:
            r13 = 2
            boolean r14 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r14 == 0) goto L_0x0545
            int r0 = org.telegram.messenger.R.string.NotificationInvitedToGroupByLink
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r14 = 0
            r1[r14] = r7
            java.lang.String r2 = r8.title
            r16 = 1
            r1[r16] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0545:
            r14 = 0
            r16 = 1
            boolean r15 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r15 == 0) goto L_0x055d
            int r0 = org.telegram.messenger.R.string.NotificationEditedGroupName
            java.lang.Object[] r1 = new java.lang.Object[r13]
            r1[r14] = r7
            java.lang.String r2 = r2.title
            r1[r16] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x055d:
            boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r13 != 0) goto L_0x0e93
            boolean r13 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r13 == 0) goto L_0x0567
            goto L_0x0e93
        L_0x0567:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r1 == 0) goto L_0x05ce
            long r1 = r2.user_id
            int r3 = (r1 > r19 ? 1 : (r1 == r19 ? 0 : -1))
            if (r3 != 0) goto L_0x0585
            int r0 = org.telegram.messenger.R.string.NotificationGroupKickYou
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r7
            java.lang.String r2 = r8.title
            r5 = 1
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0585:
            r3 = 2
            r4 = 0
            r5 = 1
            int r6 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r6 != 0) goto L_0x059d
            int r0 = org.telegram.messenger.R.string.NotificationGroupLeftMember
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x059d:
            org.telegram.messenger.MessagesController r1 = r25.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            long r2 = r0.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x05b3
            r1 = 0
            return r1
        L_0x05b3:
            int r1 = org.telegram.messenger.R.string.NotificationGroupKickMember
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
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
        L_0x05ce:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r1 == 0) goto L_0x05d9
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x05d9:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r1 == 0) goto L_0x05e4
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x05e4:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r1 == 0) goto L_0x05f9
            int r0 = org.telegram.messenger.R.string.ActionMigrateFromGroupNotify
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r9 = 0
            r1[r9] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05f9:
            r1 = 1
            r9 = 0
            boolean r10 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r10 == 0) goto L_0x060e
            int r0 = org.telegram.messenger.R.string.ActionMigrateFromGroupNotify
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r2.title
            r1[r9] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x060e:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r1 == 0) goto L_0x0619
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0619:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r1 == 0) goto L_0x0e33
            java.lang.String r1 = "..."
            r2 = 20
            if (r8 == 0) goto L_0x0914
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r8)
            if (r3 == 0) goto L_0x062d
            boolean r3 = r8.megagroup
            if (r3 == 0) goto L_0x0914
        L_0x062d:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0645
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoText
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r7
            java.lang.String r2 = r8.title
            r9 = 1
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0645:
            r3 = 2
            r4 = 0
            r9 = 1
            boolean r10 = r0.isMusic()
            if (r10 == 0) goto L_0x065f
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedMusic
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r7
            java.lang.String r2 = r8.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x065f:
            boolean r3 = r0.isVideo()
            java.lang.String r4 = "NotificationActionPinnedText"
            if (r3 == 0) goto L_0x06b3
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x069f
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x069f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r7
            r5 = 1
            r2[r5] = r0
            java.lang.String r0 = r8.title
            r6 = 2
            r2[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x069f:
            r3 = 0
            r5 = 1
            r6 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVideo
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06b3:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x0705
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x06f1
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x06f1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r7
            r5 = 1
            r2[r5] = r0
            java.lang.String r0 = r8.title
            r9 = 2
            r2[r9] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x06f1:
            r3 = 0
            r5 = 1
            r9 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGif
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0705:
            r3 = 0
            r5 = 1
            r9 = 2
            boolean r10 = r0.isVoice()
            if (r10 == 0) goto L_0x071f
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVoice
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x071f:
            boolean r10 = r0.isRoundVideo()
            if (r10 == 0) goto L_0x0736
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedRound
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0736:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x08e5
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x0744
            goto L_0x08e5
        L_0x0744:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x0796
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0782
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0782
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r7
            r5 = 1
            r2[r5] = r0
            java.lang.String r0 = r8.title
            r6 = 2
            r2[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0782:
            r3 = 0
            r5 = 1
            r6 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedFile
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0796:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x08d1
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x07a0
            goto L_0x08d1
        L_0x07a0:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x07b8
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLive
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r6 = 0
            r1[r6] = r7
            java.lang.String r2 = r8.title
            r9 = 1
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07b8:
            r6 = 0
            r9 = 1
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r10 == 0) goto L_0x07dd
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedContact2
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r7
            java.lang.String r2 = r8.title
            r1[r9] = r2
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 2
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07dd:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x081b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0802
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedQuiz2
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
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
        L_0x0802:
            r2 = 3
            r3 = 0
            r4 = 1
            r5 = 2
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedPoll2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r7
            java.lang.String r3 = r8.title
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x081b:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x0869
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0855
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0855
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r12)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r7
            r6 = 1
            r2[r6] = r0
            java.lang.String r0 = r8.title
            r9 = 2
            r2[r9] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0855:
            r3 = 0
            r6 = 1
            r9 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedPhoto
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0869:
            r3 = 0
            r6 = 1
            r9 = 2
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r5 == 0) goto L_0x0881
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGame
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0881:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x08bd
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x08bd
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r2) goto L_0x08a8
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r5 = 0
            java.lang.CharSequence r0 = r0.subSequence(r5, r2)
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
            goto L_0x08a9
        L_0x08a8:
            r5 = 0
        L_0x08a9:
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r5] = r7
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = r8.title
            r6 = 2
            r2[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x08bd:
            r3 = 1
            r5 = 0
            r6 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoText
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r5] = r7
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08d1:
            r3 = 1
            r5 = 0
            r6 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeo
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r5] = r7
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08e5:
            r3 = 1
            r5 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0902
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r5] = r7
            java.lang.String r4 = r8.title
            r2[r3] = r4
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0902:
            r4 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedSticker
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r5] = r7
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0914:
            r3 = 1
            if (r8 == 0) goto L_0x0bbc
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x092b
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x092b:
            r4 = 0
            boolean r7 = r0.isMusic()
            if (r7 == 0) goto L_0x0941
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedMusicChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0941:
            boolean r3 = r0.isVideo()
            java.lang.String r4 = "NotificationActionPinnedTextChannel"
            if (r3 == 0) goto L_0x098f
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x097e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x097e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r8.title
            r5 = 0
            r2[r5] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x097e:
            r3 = 1
            r5 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x098f:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x09db
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x09ca
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x09ca
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r8.title
            r5 = 0
            r2[r5] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x09ca:
            r3 = 1
            r5 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGifChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09db:
            r3 = 1
            r5 = 0
            boolean r7 = r0.isVoice()
            if (r7 == 0) goto L_0x09f2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09f2:
            boolean r7 = r0.isRoundVideo()
            if (r7 == 0) goto L_0x0a07
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a07:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x0b92
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x0a15
            goto L_0x0b92
        L_0x0a15:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r7 == 0) goto L_0x0a61
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0a50
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a50
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r8.title
            r5 = 0
            r2[r5] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0a50:
            r3 = 1
            r5 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedFileChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a61:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x0b81
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x0a6b
            goto L_0x0b81
        L_0x0a6b:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x0a80
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r6 = 0
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a80:
            r6 = 0
            boolean r7 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x0aa2
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r6] = r2
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0aa2:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x0ada
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0ac4
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2
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
        L_0x0ac4:
            r2 = 2
            r3 = 1
            r4 = 0
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r5 = r8.title
            r2[r4] = r5
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0ada:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x0b22
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0b11
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b11
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r12)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r8.title
            r6 = 0
            r2[r6] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0b11:
            r3 = 1
            r6 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b22:
            r3 = 1
            r6 = 0
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r5 == 0) goto L_0x0b37
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGameChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b37:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x0b70
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0b70
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r2) goto L_0x0b5e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r9 = 0
            java.lang.CharSequence r0 = r0.subSequence(r9, r2)
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
            goto L_0x0b5f
        L_0x0b5e:
            r9 = 0
        L_0x0b5f:
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r8.title
            r2[r9] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0b70:
            r3 = 1
            r9 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b81:
            r3 = 1
            r9 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b92:
            r9 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0bac
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r8.title
            r2[r9] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0bac:
            r3 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r8.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bbc:
            r9 = 0
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0bce
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r9] = r7
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bce:
            boolean r4 = r0.isMusic()
            if (r4 == 0) goto L_0x0be1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedMusicUser
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r9] = r7
            java.lang.String r2 = "NotificationActionPinnedMusicUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0be1:
            boolean r3 = r0.isVideo()
            java.lang.String r4 = "NotificationActionPinnedTextUser"
            if (r3 == 0) goto L_0x0c2b
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0c1c
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0c1c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r7
            r5 = 1
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0c1c:
            r3 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVideoUser
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r7
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0c2b:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x0CLASSNAME
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r7
            r5 = 1
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0CLASSNAME:
            r3 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGifUser
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r7
            java.lang.String r2 = "NotificationActionPinnedGifUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0CLASSNAME:
            r3 = 0
            r5 = 1
            boolean r8 = r0.isVoice()
            if (r8 == 0) goto L_0x0CLASSNAME
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVoiceUser
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r7
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0CLASSNAME:
            boolean r8 = r0.isRoundVideo()
            if (r8 == 0) goto L_0x0c9b
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedRoundUser
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r7
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0c9b:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x0e0e
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x0ca9
            goto L_0x0e0e
        L_0x0ca9:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0cf1
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0ce2
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ce2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r7
            r5 = 1
            r2[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0ce2:
            r3 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedFileUser
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r7
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0cf1:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x0dff
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x0cfb
            goto L_0x0dff
        L_0x0cfb:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x0d0e
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveUser
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r6 = 0
            r1[r6] = r7
            java.lang.String r2 = "NotificationActionPinnedGeoLiveUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d0e:
            r6 = 0
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x0d2e
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r5
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedContactUser
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r7
            java.lang.String r2 = r5.first_name
            java.lang.String r3 = r5.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d2e:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x0d62
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
            org.telegram.tgnet.TLRPC$Poll r0 = r5.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0d4e
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedQuizUser
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r7
            java.lang.String r0 = r0.question
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0d4e:
            r2 = 2
            r3 = 0
            r4 = 1
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedPollUser
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r7
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPollUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0d62:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x0da6
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0d97
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0d97
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r12)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r7
            r6 = 1
            r2[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0d97:
            r3 = 0
            r6 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedPhotoUser
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r7
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0da6:
            r3 = 0
            r6 = 1
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r5 == 0) goto L_0x0db9
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGameUser
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r7
            java.lang.String r2 = "NotificationActionPinnedGameUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0db9:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x0df0
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0df0
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r2) goto L_0x0de0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r5 = 0
            java.lang.CharSequence r0 = r0.subSequence(r5, r2)
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
            goto L_0x0de1
        L_0x0de0:
            r5 = 0
        L_0x0de1:
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextUser
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r5] = r7
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r1, r2)
            return r0
        L_0x0df0:
            r3 = 1
            r5 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextUser
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r5] = r7
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0dff:
            r3 = 1
            r5 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeoUser
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r5] = r7
            java.lang.String r2 = "NotificationActionPinnedGeoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0e0e:
            r3 = 1
            r5 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0e26
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiUser
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r5] = r7
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0e26:
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedStickerUser
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r5] = r7
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0e33:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r1 == 0) goto L_0x0e86
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r2 = (org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r2
            java.lang.String r0 = r2.emoticon
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x0e63
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 != 0) goto L_0x0e51
            int r0 = org.telegram.messenger.R.string.ChatThemeDisabledYou
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = "ChatThemeDisabledYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0e85
        L_0x0e51:
            r1 = 0
            int r2 = org.telegram.messenger.R.string.ChatThemeDisabled
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r1] = r7
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "ChatThemeDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0e85
        L_0x0e63:
            r1 = 0
            r5 = 1
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 != 0) goto L_0x0e76
            int r2 = org.telegram.messenger.R.string.ChatThemeChangedYou
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r1] = r0
            java.lang.String r0 = "ChangedChatThemeYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0e85
        L_0x0e76:
            int r2 = org.telegram.messenger.R.string.ChatThemeChangedTo
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r1] = r7
            r3[r5] = r0
            java.lang.String r0 = "ChangedChatThemeTo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
        L_0x0e85:
            return r0
        L_0x0e86:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest
            if (r1 == 0) goto L_0x0e91
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0e91:
            r0 = 0
            return r0
        L_0x0e93:
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r1 = r1.channel_id
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0ec9
            boolean r1 = r8.megagroup
            if (r1 != 0) goto L_0x0ec9
            boolean r0 = r26.isVideoAvatar()
            if (r0 == 0) goto L_0x0eb8
            int r0 = org.telegram.messenger.R.string.ChannelVideoEditNotification
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0eb8:
            r1 = 1
            r3 = 0
            int r0 = org.telegram.messenger.R.string.ChannelPhotoEditNotification
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r8.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ec9:
            r3 = 0
            boolean r0 = r26.isVideoAvatar()
            if (r0 == 0) goto L_0x0ee3
            int r0 = org.telegram.messenger.R.string.NotificationEditedGroupVideo
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ee3:
            r1 = 2
            r4 = 1
            int r0 = org.telegram.messenger.R.string.NotificationEditedGroupPhoto
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r7
            java.lang.String r2 = r8.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ef6:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0efd:
            r4 = 1
            int r0 = org.telegram.messenger.R.string.NotificationContactJoined
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r2 = 0
            r1[r2] = r7
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0f0c:
            boolean r1 = r26.isMediaEmpty()
            if (r1 == 0) goto L_0x0f2a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r0 = r25.replaceSpoilers(r26)
            return r0
        L_0x0var_:
            int r0 = org.telegram.messenger.R.string.Message
            r1 = r23
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f2a:
            r1 = r23
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x0var_
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0var_
            java.lang.String r1 = r2.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r12)
            java.lang.String r0 = r25.replaceSpoilers(r26)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0var_
            int r0 = org.telegram.messenger.R.string.AttachDestructingPhoto
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            int r0 = org.telegram.messenger.R.string.AttachPhoto
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r2 = r26.isVideo()
            if (r2 == 0) goto L_0x0fb4
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0f9a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0f9a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r11)
            java.lang.String r0 = r25.replaceSpoilers(r26)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0f9a:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0fab
            int r0 = org.telegram.messenger.R.string.AttachDestructingVideo
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fab:
            int r0 = org.telegram.messenger.R.string.AttachVideo
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fb4:
            boolean r2 = r26.isGame()
            if (r2 == 0) goto L_0x0fc3
            int r0 = org.telegram.messenger.R.string.AttachGame
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fc3:
            boolean r2 = r26.isVoice()
            if (r2 == 0) goto L_0x0fd2
            int r0 = org.telegram.messenger.R.string.AttachAudio
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fd2:
            boolean r2 = r26.isRoundVideo()
            if (r2 == 0) goto L_0x0fe1
            int r0 = org.telegram.messenger.R.string.AttachRound
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fe1:
            boolean r2 = r26.isMusic()
            if (r2 == 0) goto L_0x0ff0
            int r0 = org.telegram.messenger.R.string.AttachMusic
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ff0:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x1001
            int r0 = org.telegram.messenger.R.string.AttachContact
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x1001:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x101f
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x1016
            int r0 = org.telegram.messenger.R.string.QuizPoll
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x1016:
            int r0 = org.telegram.messenger.R.string.Poll
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x101f:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x10e7
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x1029
            goto L_0x10e7
        L_0x1029:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x1036
            int r0 = org.telegram.messenger.R.string.AttachLiveLocation
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x1036:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x10d3
            boolean r1 = r26.isSticker()
            if (r1 != 0) goto L_0x10a7
            boolean r1 = r26.isAnimatedSticker()
            if (r1 == 0) goto L_0x1047
            goto L_0x10a7
        L_0x1047:
            boolean r1 = r26.isGif()
            if (r1 == 0) goto L_0x107a
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x1071
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1071
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r5)
            java.lang.String r0 = r25.replaceSpoilers(r26)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x1071:
            int r0 = org.telegram.messenger.R.string.AttachGif
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x107a:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x109e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x109e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r6)
            java.lang.String r0 = r25.replaceSpoilers(r26)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x109e:
            int r0 = org.telegram.messenger.R.string.AttachDocument
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x10a7:
            java.lang.String r0 = r26.getStickerEmoji()
            if (r0 == 0) goto L_0x10ca
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " "
            r1.append(r0)
            int r0 = org.telegram.messenger.R.string.AttachSticker
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x10ca:
            int r0 = org.telegram.messenger.R.string.AttachSticker
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x10d3:
            java.lang.CharSequence r2 = r0.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x10e0
            java.lang.String r0 = r25.replaceSpoilers(r26)
            return r0
        L_0x10e0:
            int r0 = org.telegram.messenger.R.string.Message
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x10e7:
            int r0 = org.telegram.messenger.R.string.AttachLocation
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x10f0:
            r1 = r23
            if (r28 == 0) goto L_0x10f7
            r0 = 0
            r28[r0] = r0
        L_0x10f7:
            int r0 = org.telegram.messenger.R.string.Message
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x10fe:
            int r0 = org.telegram.messenger.R.string.NotificationHiddenMessage
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            return r0
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

    /*  JADX ERROR: JadxRuntimeException in pass: InitCodeVariables
        jadx.core.utils.exceptions.JadxRuntimeException: Several immutable types in one variable: [int, boolean], vars: [r5v63 ?, r5v62 ?, r5v64 ?, r5v65 ?]
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVarType(InitCodeVariables.java:102)
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVar(InitCodeVariables.java:78)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:69)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVars(InitCodeVariables.java:51)
        	at jadx.core.dex.visitors.InitCodeVariables.visit(InitCodeVariables.java:32)
        */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r29, boolean r30, boolean[] r31, boolean[] r32) {
        /*
            r28 = this;
            r0 = r29
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            java.lang.String r2 = "YouHaveNewMessage"
            if (r1 != 0) goto L_0x15bd
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x0010
            goto L_0x15bd
        L_0x0010:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            long r3 = r1.dialog_id
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r5 = r1.chat_id
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x001f
            goto L_0x0021
        L_0x001f:
            long r5 = r1.channel_id
        L_0x0021:
            long r9 = r1.user_id
            r1 = 1
            r11 = 0
            if (r32 == 0) goto L_0x0029
            r32[r11] = r1
        L_0x0029:
            org.telegram.messenger.AccountInstance r12 = r28.getAccountInstance()
            android.content.SharedPreferences r12 = r12.getNotificationsSettings()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "content_preview_"
            r13.append(r14)
            r13.append(r3)
            java.lang.String r13 = r13.toString()
            boolean r13 = r12.getBoolean(r13, r1)
            boolean r14 = r29.isFcmMessage()
            java.lang.String r15 = "EnablePreviewGroup"
            java.lang.String r11 = "EnablePreviewAll"
            java.lang.String r1 = "ChannelMessageNoText"
            java.lang.String r7 = "NotificationMessageGroupNoText"
            java.lang.String r8 = "NotificationMessageNoText"
            r18 = r2
            r2 = 2
            if (r14 == 0) goto L_0x00e1
            r16 = 0
            int r3 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r3 != 0) goto L_0x007e
            int r3 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x007e
            r1 = 1
            if (r13 == 0) goto L_0x006c
            boolean r2 = r12.getBoolean(r11, r1)
            if (r2 != 0) goto L_0x00d8
        L_0x006c:
            r2 = 0
            if (r32 == 0) goto L_0x0071
            r32[r2] = r2
        L_0x0071:
            int r3 = org.telegram.messenger.R.string.NotificationMessageNoText
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r0 = r0.localName
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r3, r1)
            return r0
        L_0x007e:
            r3 = 0
            int r8 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r8 == 0) goto L_0x00d8
            if (r13 == 0) goto L_0x009f
            boolean r3 = r0.localChannel
            if (r3 != 0) goto L_0x0092
            r3 = 1
            boolean r4 = r12.getBoolean(r15, r3)
            if (r4 == 0) goto L_0x009f
            goto L_0x0093
        L_0x0092:
            r3 = 1
        L_0x0093:
            boolean r4 = r0.localChannel
            if (r4 == 0) goto L_0x00d9
            java.lang.String r4 = "EnablePreviewChannel"
            boolean r4 = r12.getBoolean(r4, r3)
            if (r4 != 0) goto L_0x00d8
        L_0x009f:
            if (r32 == 0) goto L_0x00a4
            r3 = 0
            r32[r3] = r3
        L_0x00a4:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            long r3 = r3.channel_id
            r5 = 0
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 == 0) goto L_0x00c5
            boolean r3 = r29.isSupergroup()
            if (r3 != 0) goto L_0x00c5
            int r2 = org.telegram.messenger.R.string.ChannelMessageNoText
            r3 = 1
            java.lang.Object[] r3 = new java.lang.Object[r3]
            java.lang.String r0 = r0.localName
            r4 = 0
            r3[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            return r0
        L_0x00c5:
            r4 = 0
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupNoText
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r0.localUserName
            r2[r4] = r3
            java.lang.String r0 = r0.localName
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r2)
            return r0
        L_0x00d8:
            r3 = 1
        L_0x00d9:
            r4 = 0
            r31[r4] = r3
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = (java.lang.String) r0
            return r0
        L_0x00e1:
            org.telegram.messenger.UserConfig r14 = r28.getUserConfig()
            long r19 = r14.getClientUserId()
            r16 = 0
            int r14 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x00f9
            long r9 = r29.getFromChatId()
            int r14 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x0101
            long r9 = -r5
            goto L_0x0101
        L_0x00f9:
            int r14 = (r9 > r19 ? 1 : (r9 == r19 ? 0 : -1))
            if (r14 != 0) goto L_0x0101
            long r9 = r29.getFromChatId()
        L_0x0101:
            int r14 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r14 != 0) goto L_0x0110
            int r14 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r14 == 0) goto L_0x010b
            long r3 = -r5
            goto L_0x0110
        L_0x010b:
            int r14 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r14 == 0) goto L_0x0110
            r3 = r9
        L_0x0110:
            int r21 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r21 <= 0) goto L_0x0147
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            boolean r2 = r2.from_scheduled
            if (r2 == 0) goto L_0x0130
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 != 0) goto L_0x0127
            int r2 = org.telegram.messenger.R.string.MessageScheduledReminderNotification
            java.lang.String r14 = "MessageScheduledReminderNotification"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r14, r2)
            goto L_0x0144
        L_0x0127:
            int r2 = org.telegram.messenger.R.string.NotificationMessageScheduledName
            java.lang.String r14 = "NotificationMessageScheduledName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r14, r2)
            goto L_0x0144
        L_0x0130:
            org.telegram.messenger.MessagesController r2 = r28.getMessagesController()
            java.lang.Long r14 = java.lang.Long.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r14)
            if (r2 == 0) goto L_0x0143
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r2)
            goto L_0x0144
        L_0x0143:
            r2 = 0
        L_0x0144:
            r22 = r15
            goto L_0x015c
        L_0x0147:
            org.telegram.messenger.MessagesController r2 = r28.getMessagesController()
            r22 = r15
            long r14 = -r9
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r14)
            if (r2 == 0) goto L_0x015b
            java.lang.String r2 = r2.title
            goto L_0x015c
        L_0x015b:
            r2 = 0
        L_0x015c:
            if (r2 != 0) goto L_0x0160
            r14 = 0
            return r14
        L_0x0160:
            r14 = 0
            r16 = 0
            int r15 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r15 == 0) goto L_0x0177
            org.telegram.messenger.MessagesController r15 = r28.getMessagesController()
            java.lang.Long r14 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r14 = r15.getChat(r14)
            if (r14 != 0) goto L_0x0178
            r15 = 0
            return r15
        L_0x0177:
            r14 = 0
        L_0x0178:
            boolean r15 = org.telegram.messenger.DialogObject.isEncryptedDialog(r3)
            if (r15 == 0) goto L_0x0188
            int r0 = org.telegram.messenger.R.string.YouHaveNewMessage
            r1 = r18
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x15bc
        L_0x0188:
            java.lang.String r15 = "🎬 "
            r18 = r7
            java.lang.String r7 = "📎 "
            r23 = r1
            java.lang.String r1 = "📹 "
            r24 = r14
            java.lang.String r14 = "🖼 "
            r25 = r7
            java.lang.String r7 = "NotificationMessageText"
            r26 = r15
            r15 = 3
            r16 = 0
            int r27 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r27 != 0) goto L_0x05e0
            int r27 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1))
            if (r27 == 0) goto L_0x05e0
            if (r13 == 0) goto L_0x05ce
            r5 = 1
            boolean r6 = r12.getBoolean(r11, r5)
            if (r6 == 0) goto L_0x05ce
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r6 == 0) goto L_0x02da
            org.telegram.tgnet.TLRPC$MessageAction r1 = r5.action
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r5 == 0) goto L_0x01c4
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x15bc
        L_0x01c4:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r5 != 0) goto L_0x02ca
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r5 == 0) goto L_0x01ce
            goto L_0x02ca
        L_0x01ce:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r5 == 0) goto L_0x01e2
            int r0 = org.telegram.messenger.R.string.NotificationContactNewPhoto
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x01e2:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r5 == 0) goto L_0x0244
            int r1 = org.telegram.messenger.R.string.formatDateAtTime
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
            int r2 = org.telegram.messenger.R.string.NotificationUnrecognizedDevice
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r5 = r28.getUserConfig()
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
            r3[r15] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x15bc
        L_0x0244:
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r5 != 0) goto L_0x02c2
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r5 == 0) goto L_0x024e
            goto L_0x02c2
        L_0x024e:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x026a
            boolean r0 = r1.video
            if (r0 == 0) goto L_0x0260
            int r0 = org.telegram.messenger.R.string.CallMessageVideoIncomingMissed
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x15bc
        L_0x0260:
            int r0 = org.telegram.messenger.R.string.CallMessageIncomingMissed
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x15bc
        L_0x026a:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r0 == 0) goto L_0x15ba
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r1 = (org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r1
            java.lang.String r0 = r1.emoticon
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x029b
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 != 0) goto L_0x0289
            int r0 = org.telegram.messenger.R.string.ChatThemeDisabledYou
            r1 = 0
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r3 = "ChatThemeDisabledYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r3, r0, r2)
            r5 = 1
            goto L_0x02bd
        L_0x0289:
            r1 = 0
            int r3 = org.telegram.messenger.R.string.ChatThemeDisabled
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r1] = r2
            r5 = 1
            r4[r5] = r0
            java.lang.String r0 = "ChatThemeDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            goto L_0x02bd
        L_0x029b:
            r1 = 0
            r5 = 1
            int r6 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r6 != 0) goto L_0x02ae
            int r2 = org.telegram.messenger.R.string.ChatThemeChangedYou
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r1] = r0
            java.lang.String r0 = "ChangedChatThemeYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x02bd
        L_0x02ae:
            int r3 = org.telegram.messenger.R.string.ChatThemeChangedTo
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r1] = r2
            r4[r5] = r0
            java.lang.String r0 = "ChangedChatThemeTo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
        L_0x02bd:
            r14 = r0
            r31[r1] = r5
            goto L_0x15bc
        L_0x02c2:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x15bc
        L_0x02ca:
            r1 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationContactJoined
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r1] = r2
            java.lang.String r1 = "NotificationContactJoined"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x15bc
        L_0x02da:
            boolean r3 = r29.isMediaEmpty()
            if (r3 == 0) goto L_0x031f
            if (r30 != 0) goto L_0x0311
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0303
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x0303:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationMessageNoText
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r8, r0, r1)
            goto L_0x15bc
        L_0x0311:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationMessageNoText
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r8, r0, r1)
            goto L_0x15bc
        L_0x031f:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r3.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0383
            if (r30 != 0) goto L_0x035d
            int r1 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r1 < r4) goto L_0x035d
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x035d
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x035d:
            r4 = 0
            r5 = 1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0375
            int r0 = org.telegram.messenger.R.string.NotificationMessageSDPhoto
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "NotificationMessageSDPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0375:
            int r0 = org.telegram.messenger.R.string.NotificationMessagePhoto
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "NotificationMessagePhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0383:
            boolean r3 = r29.isVideo()
            if (r3 == 0) goto L_0x03e7
            if (r30 != 0) goto L_0x03c1
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r3 < r4) goto L_0x03c1
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x03c1
            int r3 = org.telegram.messenger.R.string.NotificationMessageText
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1 = 1
            r4[r1] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r3, r4)
            r31[r5] = r1
            goto L_0x15bc
        L_0x03c1:
            r1 = 1
            r5 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x03d9
            int r0 = org.telegram.messenger.R.string.NotificationMessageSDVideo
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageSDVideo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x03d9:
            int r0 = org.telegram.messenger.R.string.NotificationMessageVideo
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageVideo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x03e7:
            r5 = 0
            boolean r1 = r29.isGame()
            if (r1 == 0) goto L_0x0408
            int r1 = org.telegram.messenger.R.string.NotificationMessageGame
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r5] = r2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r0 = r0.title
            r4 = 1
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessageGame"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x15bc
        L_0x0408:
            r4 = 1
            boolean r1 = r29.isVoice()
            if (r1 == 0) goto L_0x041e
            int r0 = org.telegram.messenger.R.string.NotificationMessageAudio
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageAudio"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x041e:
            r3 = 0
            boolean r1 = r29.isRoundVideo()
            if (r1 == 0) goto L_0x0433
            int r0 = org.telegram.messenger.R.string.NotificationMessageRound
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageRound"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0433:
            boolean r1 = r29.isMusic()
            if (r1 == 0) goto L_0x0447
            int r0 = org.telegram.messenger.R.string.NotificationMessageMusic
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageMusic"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0447:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r4 == 0) goto L_0x046b
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            int r0 = org.telegram.messenger.R.string.NotificationMessageContact2
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r2
            java.lang.String r2 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r2, r1)
            r2 = 1
            r4[r2] = r1
            java.lang.String r1 = "NotificationMessageContact2"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            goto L_0x15bc
        L_0x046b:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x04a1
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x048b
            int r1 = org.telegram.messenger.R.string.NotificationMessageQuiz2
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r0 = r0.question
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x048b:
            r3 = 2
            r4 = 0
            r5 = 1
            int r1 = org.telegram.messenger.R.string.NotificationMessagePoll2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r2
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
        L_0x049e:
            r14 = r0
            goto L_0x15bc
        L_0x04a1:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x05be
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x04ab
            goto L_0x05be
        L_0x04ab:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x04bf
            int r0 = org.telegram.messenger.R.string.NotificationMessageLiveLocation
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageLiveLocation"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x04bf:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0592
            boolean r1 = r29.isSticker()
            if (r1 != 0) goto L_0x056b
            boolean r1 = r29.isAnimatedSticker()
            if (r1 == 0) goto L_0x04d1
            goto L_0x056b
        L_0x04d1:
            boolean r1 = r29.isGif()
            if (r1 == 0) goto L_0x0521
            if (r30 != 0) goto L_0x0511
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0511
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0511
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r8 = r26
            r2.append(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x0511:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationMessageGif
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "NotificationMessageGif"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0521:
            if (r30 != 0) goto L_0x055b
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x055b
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x055b
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r11 = r25
            r2.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x055b:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationMessageDocument
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "NotificationMessageDocument"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x056b:
            r4 = 0
            r5 = 1
            java.lang.String r0 = r29.getStickerEmoji()
            if (r0 == 0) goto L_0x0584
            int r1 = org.telegram.messenger.R.string.NotificationMessageStickerEmoji
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r2
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x0584:
            int r0 = org.telegram.messenger.R.string.NotificationMessageSticker
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0592:
            r4 = 0
            if (r30 != 0) goto L_0x05b1
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x05b1
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r2
            java.lang.CharSequence r0 = r0.messageText
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x05b1:
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationMessageNoText
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r8, r0, r1)
            goto L_0x15bc
        L_0x05be:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.NotificationMessageMap
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "NotificationMessageMap"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x05ce:
            r4 = 0
            if (r32 == 0) goto L_0x05d3
            r32[r4] = r4
        L_0x05d3:
            int r0 = org.telegram.messenger.R.string.NotificationMessageNoText
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r4] = r2
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r8, r0, r1)
            goto L_0x15bc
        L_0x05e0:
            r11 = r25
            r8 = r26
            r16 = 0
            int r25 = (r5 > r16 ? 1 : (r5 == r16 ? 0 : -1))
            if (r25 == 0) goto L_0x15ba
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r24)
            if (r5 == 0) goto L_0x05f8
            r5 = r24
            boolean r6 = r5.megagroup
            if (r6 != 0) goto L_0x05fa
            r6 = 1
            goto L_0x05fb
        L_0x05f8:
            r5 = r24
        L_0x05fa:
            r6 = 0
        L_0x05fb:
            if (r13 == 0) goto L_0x158a
            if (r6 != 0) goto L_0x0609
            r13 = r22
            r15 = 1
            boolean r13 = r12.getBoolean(r13, r15)
            if (r13 != 0) goto L_0x0614
            goto L_0x060a
        L_0x0609:
            r15 = 1
        L_0x060a:
            if (r6 == 0) goto L_0x158a
            java.lang.String r6 = "EnablePreviewChannel"
            boolean r6 = r12.getBoolean(r6, r15)
            if (r6 == 0) goto L_0x158a
        L_0x0614:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            boolean r12 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r12 == 0) goto L_0x0fcf
            org.telegram.tgnet.TLRPC$MessageAction r7 = r6.action
            boolean r12 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r12 == 0) goto L_0x073d
            long r3 = r7.user_id
            r11 = 0
            int r1 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x0642
            java.util.ArrayList<java.lang.Long> r1 = r7.users
            int r1 = r1.size()
            r6 = 1
            if (r1 != r6) goto L_0x0642
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Long> r1 = r1.users
            r3 = 0
            java.lang.Object r1 = r1.get(r3)
            java.lang.Long r1 = (java.lang.Long) r1
            long r3 = r1.longValue()
        L_0x0642:
            r6 = 0
            int r1 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x06e4
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r0 = r0.channel_id
            int r8 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x066b
            boolean r0 = r5.megagroup
            if (r0 != 0) goto L_0x066b
            int r0 = org.telegram.messenger.R.string.ChannelAddedByNotification
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x066b:
            int r0 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r0 != 0) goto L_0x0684
            int r0 = org.telegram.messenger.R.string.NotificationInvitedToGroup
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0684:
            org.telegram.messenger.MessagesController r0 = r28.getMessagesController()
            java.lang.Long r1 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x0694
            r1 = 0
            return r1
        L_0x0694:
            long r3 = r0.id
            int r1 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r1 != 0) goto L_0x06c8
            boolean r0 = r5.megagroup
            if (r0 == 0) goto L_0x06b3
            int r0 = org.telegram.messenger.R.string.NotificationGroupAddSelfMega
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x06b3:
            r1 = 2
            r3 = 0
            r4 = 1
            int r0 = org.telegram.messenger.R.string.NotificationGroupAddSelf
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x06c8:
            r3 = 0
            r4 = 1
            int r1 = org.telegram.messenger.R.string.NotificationGroupAddMember
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r3] = r2
            java.lang.String r2 = r5.title
            r6[r4] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r6[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r6)
            goto L_0x049e
        L_0x06e4:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = 0
        L_0x06ea:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Long> r4 = r4.users
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0721
            org.telegram.messenger.MessagesController r4 = r28.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            java.lang.Object r6 = r6.get(r3)
            java.lang.Long r6 = (java.lang.Long) r6
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r6)
            if (r4 == 0) goto L_0x071e
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r4)
            int r6 = r1.length()
            if (r6 == 0) goto L_0x071b
            java.lang.String r6 = ", "
            r1.append(r6)
        L_0x071b:
            r1.append(r4)
        L_0x071e:
            int r3 = r3 + 1
            goto L_0x06ea
        L_0x0721:
            int r0 = org.telegram.messenger.R.string.NotificationGroupAddMember
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r1 = r1.toString()
            r12 = 2
            r3[r12] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x049e
        L_0x073d:
            r12 = 2
            boolean r13 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r13 == 0) goto L_0x0756
            int r0 = org.telegram.messenger.R.string.NotificationGroupCreatedCall
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupCreatedCall"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0756:
            boolean r12 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            if (r12 == 0) goto L_0x0762
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x15bc
        L_0x0762:
            boolean r12 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r12 == 0) goto L_0x082c
            long r3 = r7.user_id
            r8 = 0
            int r1 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r1 != 0) goto L_0x0788
            java.util.ArrayList<java.lang.Long> r1 = r7.users
            int r1 = r1.size()
            r6 = 1
            if (r1 != r6) goto L_0x0788
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Long> r1 = r1.users
            r3 = 0
            java.lang.Object r1 = r1.get(r3)
            java.lang.Long r1 = (java.lang.Long) r1
            long r3 = r1.longValue()
        L_0x0788:
            r6 = 0
            int r1 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x07d3
            int r0 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r0 != 0) goto L_0x07a7
            int r0 = org.telegram.messenger.R.string.NotificationGroupInvitedYouToCall
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x07a7:
            org.telegram.messenger.MessagesController r0 = r28.getMessagesController()
            java.lang.Long r1 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x07b7
            r1 = 0
            return r1
        L_0x07b7:
            int r1 = org.telegram.messenger.R.string.NotificationGroupInvitedToCall
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x07d3:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = 0
        L_0x07d9:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Long> r4 = r4.users
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x0810
            org.telegram.messenger.MessagesController r4 = r28.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            java.lang.Object r6 = r6.get(r3)
            java.lang.Long r6 = (java.lang.Long) r6
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r6)
            if (r4 == 0) goto L_0x080d
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r4)
            int r6 = r1.length()
            if (r6 == 0) goto L_0x080a
            java.lang.String r6 = ", "
            r1.append(r6)
        L_0x080a:
            r1.append(r4)
        L_0x080d:
            int r3 = r3 + 1
            goto L_0x07d9
        L_0x0810:
            int r0 = org.telegram.messenger.R.string.NotificationGroupInvitedToCall
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r1 = r1.toString()
            r12 = 2
            r3[r12] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x049e
        L_0x082c:
            r12 = 2
            boolean r13 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r13 == 0) goto L_0x0845
            int r0 = org.telegram.messenger.R.string.NotificationInvitedToGroupByLink
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r13 = 0
            r1[r13] = r2
            java.lang.String r2 = r5.title
            r15 = 1
            r1[r15] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0845:
            r13 = 0
            boolean r15 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r15 == 0) goto L_0x085d
            int r0 = org.telegram.messenger.R.string.NotificationEditedGroupName
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r13] = r2
            java.lang.String r2 = r7.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x085d:
            boolean r12 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r12 != 0) goto L_0x0var_
            boolean r12 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r12 == 0) goto L_0x0867
            goto L_0x0var_
        L_0x0867:
            boolean r6 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r6 == 0) goto L_0x08d1
            long r3 = r7.user_id
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 != 0) goto L_0x0886
            int r0 = org.telegram.messenger.R.string.NotificationGroupKickYou
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r6 = 0
            r1[r6] = r2
            java.lang.String r2 = r5.title
            r7 = 1
            r1[r7] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0886:
            r1 = 2
            r6 = 0
            r7 = 1
            int r8 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r8 != 0) goto L_0x089f
            int r0 = org.telegram.messenger.R.string.NotificationGroupLeftMember
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r6] = r2
            java.lang.String r2 = r5.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x089f:
            org.telegram.messenger.MessagesController r1 = r28.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            long r3 = r0.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x08b5
            r9 = 0
            return r9
        L_0x08b5:
            int r1 = org.telegram.messenger.R.string.NotificationGroupKickMember
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x15bc
        L_0x08d1:
            r9 = 0
            boolean r6 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r6 == 0) goto L_0x08de
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x15bc
        L_0x08de:
            boolean r6 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r6 == 0) goto L_0x08ea
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x15bc
        L_0x08ea:
            boolean r6 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r6 == 0) goto L_0x0900
            int r0 = org.telegram.messenger.R.string.ActionMigrateFromGroupNotify
            r6 = 1
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r5.title
            r10 = 0
            r1[r10] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0900:
            r6 = 1
            r10 = 0
            boolean r12 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r12 == 0) goto L_0x0916
            int r0 = org.telegram.messenger.R.string.ActionMigrateFromGroupNotify
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.String r2 = r7.title
            r1[r10] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0916:
            boolean r6 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r6 == 0) goto L_0x0922
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x15bc
        L_0x0922:
            boolean r6 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r6 == 0) goto L_0x0ef9
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r5)
            r4 = 20
            if (r3 == 0) goto L_0x0bf7
            boolean r3 = r5.megagroup
            if (r3 == 0) goto L_0x0934
            goto L_0x0bf7
        L_0x0934:
            org.telegram.messenger.MessageObject r2 = r0.replyMessageObject
            if (r2 != 0) goto L_0x094a
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel
            r3 = 1
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r5.title
            r6 = 0
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x094a:
            r3 = 1
            r6 = 0
            boolean r7 = r2.isMusic()
            if (r7 == 0) goto L_0x0962
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedMusicChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r5.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0962:
            boolean r3 = r2.isVideo()
            java.lang.String r6 = "NotificationActionPinnedTextChannel"
            if (r3 == 0) goto L_0x09b2
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x09a0
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09a0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r5.title
            r4 = 0
            r2[r4] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r1, r2)
            goto L_0x049e
        L_0x09a0:
            r3 = 1
            r4 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVideoChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r5.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x09b2:
            boolean r1 = r2.isGif()
            if (r1 == 0) goto L_0x0a00
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 19
            if (r0 < r1) goto L_0x09ee
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09ee
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r8)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r5.title
            r7 = 0
            r2[r7] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r1, r2)
            goto L_0x049e
        L_0x09ee:
            r3 = 1
            r7 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGifChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r5.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0a00:
            r3 = 1
            r7 = 0
            boolean r1 = r2.isVoice()
            if (r1 == 0) goto L_0x0a18
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVoiceChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r5.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0a18:
            boolean r1 = r2.isRoundVideo()
            if (r1 == 0) goto L_0x0a2e
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedRoundChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r5.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0a2e:
            boolean r1 = r2.isSticker()
            if (r1 != 0) goto L_0x0bcb
            boolean r1 = r2.isAnimatedSticker()
            if (r1 == 0) goto L_0x0a3c
            goto L_0x0bcb
        L_0x0a3c:
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r7 == 0) goto L_0x0a8a
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0a78
            java.lang.String r0 = r1.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0a78
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r11)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r5.title
            r4 = 0
            r2[r4] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r1, r2)
            goto L_0x049e
        L_0x0a78:
            r3 = 1
            r4 = 0
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedFileChannel
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r5.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0a8a:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r7 != 0) goto L_0x0bb9
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r7 == 0) goto L_0x0a94
            goto L_0x0bb9
        L_0x0a94:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r7 == 0) goto L_0x0aaa
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLiveChannel
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r5.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0aaa:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x0ad1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedContactChannel2
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r5.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x049e
        L_0x0ad1:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0b0b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r0 = r3.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0af4
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedQuizChannel2
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r5.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x049e
        L_0x0af4:
            r2 = 2
            r3 = 1
            r4 = 0
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedPollChannel2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r5 = r5.title
            r2[r4] = r5
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x049e
        L_0x0b0b:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0b55
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0b43
            java.lang.String r0 = r1.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0b43
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r1 = r2.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r5.title
            r7 = 0
            r2[r7] = r3
            r8 = 1
            r2[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r1, r2)
            goto L_0x049e
        L_0x0b43:
            r7 = 0
            r8 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedPhotoChannel
            java.lang.Object[] r1 = new java.lang.Object[r8]
            java.lang.String r2 = r5.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0b55:
            r7 = 0
            r8 = 1
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0b6b
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGameChannel
            java.lang.Object[] r1 = new java.lang.Object[r8]
            java.lang.String r2 = r5.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0b6b:
            java.lang.CharSequence r0 = r2.messageText
            if (r0 == 0) goto L_0x0ba7
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0ba7
            java.lang.CharSequence r0 = r2.messageText
            int r1 = r0.length()
            if (r1 <= r4) goto L_0x0b94
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r4)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0b95
        L_0x0b94:
            r3 = 0
        L_0x0b95:
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedTextChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r5.title
            r2[r3] = r4
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r1, r2)
            goto L_0x049e
        L_0x0ba7:
            r3 = 0
            r4 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoTextChannel
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r5.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0bb9:
            r3 = 0
            r4 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeoChannel
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r5.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0bcb:
            r3 = 0
            java.lang.String r0 = r2.getStickerEmoji()
            if (r0 == 0) goto L_0x0be6
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmojiChannel
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r5.title
            r2[r3] = r4
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x049e
        L_0x0be6:
            r4 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedStickerChannel
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r5.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0bf7:
            r3 = 0
            org.telegram.messenger.MessageObject r6 = r0.replyMessageObject
            if (r6 != 0) goto L_0x0CLASSNAME
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoText
            r7 = 2
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r9 = 1
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0CLASSNAME:
            r7 = 2
            r9 = 1
            boolean r10 = r6.isMusic()
            if (r10 == 0) goto L_0x0c2a
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedMusic
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0c2a:
            boolean r3 = r6.isVideo()
            java.lang.String r7 = "NotificationActionPinnedText"
            if (r3 == 0) goto L_0x0CLASSNAME
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0c6b
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0c6b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            r6 = 1
            r3[r6] = r0
            java.lang.String r0 = r5.title
            r8 = 2
            r3[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            goto L_0x049e
        L_0x0c6b:
            r4 = 0
            r6 = 1
            r8 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVideo
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r4] = r2
            java.lang.String r2 = r5.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0CLASSNAME:
            boolean r1 = r6.isGif()
            if (r1 == 0) goto L_0x0cd4
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 19
            if (r0 < r1) goto L_0x0cbf
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0cbf
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r8)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r8 = 0
            r3[r8] = r2
            r9 = 1
            r3[r9] = r0
            java.lang.String r0 = r5.title
            r10 = 2
            r3[r10] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            goto L_0x049e
        L_0x0cbf:
            r8 = 0
            r9 = 1
            r10 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGif
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r8] = r2
            java.lang.String r2 = r5.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0cd4:
            r8 = 0
            r9 = 1
            r10 = 2
            boolean r1 = r6.isVoice()
            if (r1 == 0) goto L_0x0cef
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedVoice
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r8] = r2
            java.lang.String r2 = r5.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0cef:
            boolean r1 = r6.isRoundVideo()
            if (r1 == 0) goto L_0x0d07
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedRound
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r8] = r2
            java.lang.String r2 = r5.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0d07:
            boolean r1 = r6.isSticker()
            if (r1 != 0) goto L_0x0ec8
            boolean r1 = r6.isAnimatedSticker()
            if (r1 == 0) goto L_0x0d15
            goto L_0x0ec8
        L_0x0d15:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r1.media
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0d69
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0d54
            java.lang.String r0 = r1.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0d54
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r11)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            r6 = 1
            r3[r6] = r0
            java.lang.String r0 = r5.title
            r8 = 2
            r3[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            goto L_0x049e
        L_0x0d54:
            r4 = 0
            r6 = 1
            r8 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedFile
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r4] = r2
            java.lang.String r2 = r5.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0d69:
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x0eb3
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x0d73
            goto L_0x0eb3
        L_0x0d73:
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x0d8c
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeoLive
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0d8c:
            boolean r8 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x0db6
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedContact2
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r2 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r2, r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x0db6:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0df6
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r3 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r3
            org.telegram.tgnet.TLRPC$Poll r0 = r3.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0ddc
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedQuiz2
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r6 = 1
            r3[r6] = r2
            java.lang.String r0 = r0.question
            r7 = 2
            r3[r7] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x0ddc:
            r3 = 3
            r4 = 0
            r6 = 1
            r7 = 2
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedPoll2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r3[r6] = r2
            java.lang.String r0 = r0.question
            r3[r7] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x0df6:
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0e46
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0e31
            java.lang.String r0 = r1.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0e31
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r8 = 0
            r3[r8] = r2
            r9 = 1
            r3[r9] = r0
            java.lang.String r0 = r5.title
            r10 = 2
            r3[r10] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            goto L_0x049e
        L_0x0e31:
            r8 = 0
            r9 = 1
            r10 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedPhoto
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r8] = r2
            java.lang.String r2 = r5.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0e46:
            r8 = 0
            r9 = 1
            r10 = 2
            boolean r0 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0e5f
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGame
            java.lang.Object[] r1 = new java.lang.Object[r10]
            r1[r8] = r2
            java.lang.String r2 = r5.title
            r1[r9] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0e5f:
            java.lang.CharSequence r0 = r6.messageText
            if (r0 == 0) goto L_0x0e9e
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0e9e
            java.lang.CharSequence r0 = r6.messageText
            int r1 = r0.length()
            if (r1 <= r4) goto L_0x0e88
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r4)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0e89
        L_0x0e88:
            r3 = 0
        L_0x0e89:
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedText
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r2
            r8 = 1
            r4[r8] = r0
            java.lang.String r0 = r5.title
            r6 = 2
            r4[r6] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r1, r4)
            goto L_0x049e
        L_0x0e9e:
            r3 = 0
            r6 = 2
            r8 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedNoText
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0eb3:
            r3 = 0
            r6 = 2
            r8 = 1
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedGeo
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0ec8:
            r3 = 0
            r8 = 1
            java.lang.String r0 = r6.getStickerEmoji()
            if (r0 == 0) goto L_0x0ee6
            int r1 = org.telegram.messenger.R.string.NotificationActionPinnedStickerEmoji
            r4 = 3
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r2
            java.lang.String r2 = r5.title
            r4[r8] = r2
            r6 = 2
            r4[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4)
            goto L_0x049e
        L_0x0ee6:
            r6 = 2
            int r0 = org.telegram.messenger.R.string.NotificationActionPinnedSticker
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0ef9:
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r1 == 0) goto L_0x0var_
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x15bc
        L_0x0var_:
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r1 == 0) goto L_0x0f5c
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r7 = (org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r7
            java.lang.String r0 = r7.emoticon
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x0var_
            int r1 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r1 != 0) goto L_0x0var_
            int r0 = org.telegram.messenger.R.string.ChatThemeDisabledYou
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = "ChatThemeDisabledYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x0var_:
            r1 = 0
            int r3 = org.telegram.messenger.R.string.ChatThemeDisabled
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r1] = r2
            r5 = 1
            r4[r5] = r0
            java.lang.String r0 = "ChatThemeDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            goto L_0x049e
        L_0x0var_:
            r1 = 0
            r5 = 1
            int r6 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r6 != 0) goto L_0x0f4b
            int r2 = org.telegram.messenger.R.string.ChatThemeChangedYou
            java.lang.Object[] r3 = new java.lang.Object[r5]
            r3[r1] = r0
            java.lang.String r0 = "ChangedChatThemeYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x049e
        L_0x0f4b:
            int r3 = org.telegram.messenger.R.string.ChatThemeChangedTo
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r1] = r2
            r4[r5] = r0
            java.lang.String r0 = "ChangedChatThemeTo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r3, r4)
            goto L_0x049e
        L_0x0f5c:
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest
            if (r1 == 0) goto L_0x15bb
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r14 = r0.toString()
            goto L_0x15bc
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Peer r1 = r6.peer_id
            long r3 = r1.channel_id
            r6 = 0
            int r1 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r1 == 0) goto L_0x0fa0
            boolean r1 = r5.megagroup
            if (r1 != 0) goto L_0x0fa0
            boolean r0 = r29.isVideoAvatar()
            if (r0 == 0) goto L_0x0f8e
            int r0 = org.telegram.messenger.R.string.ChannelVideoEditNotification
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r5.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0f8e:
            r1 = 1
            r3 = 0
            int r0 = org.telegram.messenger.R.string.ChannelPhotoEditNotification
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r5.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0fa0:
            r3 = 0
            boolean r0 = r29.isVideoAvatar()
            if (r0 == 0) goto L_0x0fbb
            int r0 = org.telegram.messenger.R.string.NotificationEditedGroupVideo
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0fbb:
            r1 = 2
            r4 = 1
            int r0 = org.telegram.messenger.R.string.NotificationEditedGroupPhoto
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x0fcf:
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r3 == 0) goto L_0x1271
            boolean r3 = r5.megagroup
            if (r3 != 0) goto L_0x1271
            boolean r3 = r29.isMediaEmpty()
            if (r3 == 0) goto L_0x1012
            if (r30 != 0) goto L_0x1002
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1002
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x1002:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.ChannelMessageNoText
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            r3 = r23
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r3, r0, r1)
            goto L_0x15bc
        L_0x1012:
            r3 = r23
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r4.media
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x1062
            if (r30 != 0) goto L_0x1052
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x1052
            java.lang.String r1 = r4.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1052
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x1052:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.ChannelMessagePhoto
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x1062:
            boolean r4 = r29.isVideo()
            if (r4 == 0) goto L_0x10b0
            if (r30 != 0) goto L_0x10a0
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r3 < r4) goto L_0x10a0
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x10a0
            int r3 = org.telegram.messenger.R.string.NotificationMessageText
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1 = 1
            r4[r1] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r3, r4)
            r31[r5] = r1
            goto L_0x15bc
        L_0x10a0:
            r1 = 1
            r5 = 0
            int r0 = org.telegram.messenger.R.string.ChannelMessageVideo
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r5] = r2
            java.lang.String r2 = "ChannelMessageVideo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x10b0:
            r1 = 1
            r5 = 0
            boolean r4 = r29.isVoice()
            if (r4 == 0) goto L_0x10c6
            int r0 = org.telegram.messenger.R.string.ChannelMessageAudio
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r5] = r2
            java.lang.String r2 = "ChannelMessageAudio"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x10c6:
            boolean r4 = r29.isRoundVideo()
            if (r4 == 0) goto L_0x10da
            int r0 = org.telegram.messenger.R.string.ChannelMessageRound
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r5] = r2
            java.lang.String r2 = "ChannelMessageRound"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x10da:
            boolean r4 = r29.isMusic()
            if (r4 == 0) goto L_0x10ee
            int r0 = org.telegram.messenger.R.string.ChannelMessageMusic
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r5] = r2
            java.lang.String r2 = "ChannelMessageMusic"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x10ee:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r4 == 0) goto L_0x1112
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            int r0 = org.telegram.messenger.R.string.ChannelMessageContact2
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r5] = r2
            java.lang.String r2 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r2, r1)
            r2 = 1
            r3[r2] = r1
            java.lang.String r1 = "ChannelMessageContact2"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x15bc
        L_0x1112:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x1148
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x1133
            int r1 = org.telegram.messenger.R.string.ChannelMessageQuiz2
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r0 = r0.question
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x1133:
            r3 = 2
            r4 = 0
            r5 = 1
            int r1 = org.telegram.messenger.R.string.ChannelMessagePoll2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r2
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x1148:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x1261
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x1152
            goto L_0x1261
        L_0x1152:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x1166
            int r0 = org.telegram.messenger.R.string.ChannelMessageLiveLocation
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelMessageLiveLocation"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x1166:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x1235
            boolean r1 = r29.isSticker()
            if (r1 != 0) goto L_0x120e
            boolean r1 = r29.isAnimatedSticker()
            if (r1 == 0) goto L_0x1178
            goto L_0x120e
        L_0x1178:
            boolean r1 = r29.isGif()
            if (r1 == 0) goto L_0x11c6
            if (r30 != 0) goto L_0x11b6
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x11b6
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x11b6
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x11b6:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.ChannelMessageGIF
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x11c6:
            if (r30 != 0) goto L_0x11fe
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x11fe
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x11fe
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x11fe:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.ChannelMessageDocument
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x120e:
            r4 = 0
            r5 = 1
            java.lang.String r0 = r29.getStickerEmoji()
            if (r0 == 0) goto L_0x1227
            int r1 = org.telegram.messenger.R.string.ChannelMessageStickerEmoji
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r2
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x1227:
            int r0 = org.telegram.messenger.R.string.ChannelMessageSticker
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x1235:
            r4 = 0
            if (r30 != 0) goto L_0x1254
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1254
            int r1 = org.telegram.messenger.R.string.NotificationMessageText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r2
            java.lang.CharSequence r0 = r0.messageText
            r5 = 1
            r3[r5] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r7, r1, r3)
            r31[r4] = r5
            goto L_0x15bc
        L_0x1254:
            r5 = 1
            int r0 = org.telegram.messenger.R.string.ChannelMessageNoText
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r3, r0, r1)
            goto L_0x15bc
        L_0x1261:
            r4 = 0
            r5 = 1
            int r0 = org.telegram.messenger.R.string.ChannelMessageMap
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r2
            java.lang.String r2 = "ChannelMessageMap"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x1271:
            boolean r3 = r29.isMediaEmpty()
            java.lang.String r4 = "NotificationMessageGroupText"
            if (r3 == 0) goto L_0x12b4
            if (r30 != 0) goto L_0x129f
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x129f
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupText
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r6 = 0
            r3[r6] = r2
            java.lang.String r2 = r5.title
            r5 = 1
            r3[r5] = r2
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r6 = 2
            r3[r6] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            goto L_0x15bc
        L_0x129f:
            r6 = 2
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupNoText
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r3 = 1
            r1[r3] = r2
            r6 = r18
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r6, r0, r1)
            goto L_0x15bc
        L_0x12b4:
            r6 = r18
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r3.media
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r7 == 0) goto L_0x130c
            if (r30 != 0) goto L_0x12f7
            int r1 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r1 < r6) goto L_0x12f7
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x12f7
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupText
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r6 = 0
            r3[r6] = r2
            java.lang.String r2 = r5.title
            r5 = 1
            r3[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r6 = 2
            r3[r6] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            goto L_0x15bc
        L_0x12f7:
            r6 = 2
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupPhoto
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x130c:
            boolean r3 = r29.isVideo()
            if (r3 == 0) goto L_0x1362
            if (r30 != 0) goto L_0x134d
            int r3 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r3 < r6) goto L_0x134d
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            java.lang.String r3 = r3.message
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x134d
            int r3 = org.telegram.messenger.R.string.NotificationMessageGroupText
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r7 = 0
            r6[r7] = r2
            java.lang.String r2 = r5.title
            r5 = 1
            r6[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r1)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r1 = 2
            r6[r1] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r4, r3, r6)
            goto L_0x15bc
        L_0x134d:
            r1 = 2
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupVideo
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r7 = 1
            r1[r7] = r2
            java.lang.String r2 = " "
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x1362:
            r1 = 2
            r3 = 0
            r7 = 1
            boolean r9 = r29.isVoice()
            if (r9 == 0) goto L_0x137d
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupAudio
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationMessageGroupAudio"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x137d:
            boolean r9 = r29.isRoundVideo()
            if (r9 == 0) goto L_0x1395
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupRound
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationMessageGroupRound"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x1395:
            boolean r9 = r29.isMusic()
            if (r9 == 0) goto L_0x13ad
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupMusic
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationMessageGroupMusic"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x13ad:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x13d7
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupContact2
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r3[r4] = r2
            java.lang.String r2 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r2, r1)
            r2 = 2
            r3[r2] = r1
            java.lang.String r1 = "NotificationMessageGroupContact2"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x15bc
        L_0x13d7:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x1417
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x13fd
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupQuiz2
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r6 = 1
            r3[r6] = r2
            java.lang.String r0 = r0.question
            r7 = 2
            r3[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x13fd:
            r3 = 3
            r4 = 0
            r6 = 1
            r7 = 2
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupPoll2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r3[r6] = r2
            java.lang.String r0 = r0.question
            r3[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x049e
        L_0x1417:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r3 == 0) goto L_0x1437
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupGame
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r3[r4] = r2
            org.telegram.tgnet.TLRPC$TL_game r1 = r1.game
            java.lang.String r1 = r1.title
            r2 = 2
            r3[r2] = r1
            java.lang.String r1 = "NotificationMessageGroupGame"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x15bc
        L_0x1437:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x1576
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x1441
            goto L_0x1576
        L_0x1441:
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x145a
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupLiveLocation
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x145a:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x1543
            boolean r1 = r29.isSticker()
            if (r1 != 0) goto L_0x1512
            boolean r1 = r29.isAnimatedSticker()
            if (r1 == 0) goto L_0x146c
            goto L_0x1512
        L_0x146c:
            boolean r1 = r29.isGif()
            if (r1 == 0) goto L_0x14c2
            if (r30 != 0) goto L_0x14ad
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x14ad
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x14ad
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupText
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r6 = 0
            r3[r6] = r2
            java.lang.String r2 = r5.title
            r5 = 1
            r3[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r6 = 2
            r3[r6] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            goto L_0x15bc
        L_0x14ad:
            r6 = 2
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupGif
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationMessageGroupGif"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x14c2:
            if (r30 != 0) goto L_0x14fd
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x14fd
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x14fd
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupText
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r6 = 0
            r3[r6] = r2
            java.lang.String r2 = r5.title
            r5 = 1
            r3[r5] = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r6 = 2
            r3[r6] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            goto L_0x15bc
        L_0x14fd:
            r6 = 2
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupDocument
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationMessageGroupDocument"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x1512:
            r3 = 0
            r4 = 1
            java.lang.String r0 = r29.getStickerEmoji()
            if (r0 == 0) goto L_0x1530
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupStickerEmoji
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r3] = r2
            java.lang.String r2 = r5.title
            r6[r4] = r2
            r7 = 2
            r6[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r6)
            goto L_0x049e
        L_0x1530:
            r7 = 2
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupSticker
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r3] = r2
            java.lang.String r2 = r5.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x049e
        L_0x1543:
            if (r30 != 0) goto L_0x1564
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1564
            int r1 = org.telegram.messenger.R.string.NotificationMessageGroupText
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r7 = 0
            r3[r7] = r2
            java.lang.String r2 = r5.title
            r8 = 1
            r3[r8] = r2
            java.lang.CharSequence r0 = r0.messageText
            r9 = 2
            r3[r9] = r0
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r4, r1, r3)
            goto L_0x15bc
        L_0x1564:
            r7 = 0
            r8 = 1
            r9 = 2
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupNoText
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r1[r7] = r2
            java.lang.String r2 = r5.title
            r1[r8] = r2
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r6, r0, r1)
            goto L_0x15bc
        L_0x1576:
            r7 = 0
            r8 = 1
            r9 = 2
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupMap
            java.lang.Object[] r1 = new java.lang.Object[r9]
            r1[r7] = r2
            java.lang.String r2 = r5.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationMessageGroupMap"
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x15bc
        L_0x158a:
            r6 = r18
            r3 = r23
            r7 = 0
            if (r32 == 0) goto L_0x1593
            r32[r7] = r7
        L_0x1593:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r0 == 0) goto L_0x15a9
            boolean r0 = r5.megagroup
            if (r0 != 0) goto L_0x15a9
            int r0 = org.telegram.messenger.R.string.ChannelMessageNoText
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r2
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r3, r0, r1)
            goto L_0x15bc
        L_0x15a9:
            r1 = 1
            int r0 = org.telegram.messenger.R.string.NotificationMessageGroupNoText
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r2
            java.lang.String r2 = r5.title
            r3[r1] = r2
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatString(r6, r0, r3)
            goto L_0x15bc
        L_0x15ba:
            r9 = 0
        L_0x15bb:
            r14 = r9
        L_0x15bc:
            return r14
        L_0x15bd:
            r1 = r2
            int r0 = org.telegram.messenger.R.string.YouHaveNewMessage
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
                    this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_in, 1);
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

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00ef A[Catch:{ Exception -> 0x014d }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00f3 A[Catch:{ Exception -> 0x014d }] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0100 A[Catch:{ Exception -> 0x014d }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0105 A[Catch:{ Exception -> 0x014d }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0133 A[Catch:{ Exception -> 0x014d }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0135 A[Catch:{ Exception -> 0x014d }] */
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
            if (r8 != 0) goto L_0x0153
            boolean r8 = org.telegram.messenger.ChatObject.isChannel(r23)
            if (r8 == 0) goto L_0x0020
            boolean r8 = r5.megagroup
            if (r8 != 0) goto L_0x0020
            goto L_0x0153
        L_0x0020:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x014d }
            r8.<init>()     // Catch:{ Exception -> 0x014d }
            java.lang.String r10 = "ndid_"
            r8.append(r10)     // Catch:{ Exception -> 0x014d }
            r8.append(r2)     // Catch:{ Exception -> 0x014d }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x014d }
            android.content.Intent r10 = new android.content.Intent     // Catch:{ Exception -> 0x014d }
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x014d }
            java.lang.Class<org.telegram.messenger.OpenChatReceiver> r12 = org.telegram.messenger.OpenChatReceiver.class
            r10.<init>(r11, r12)     // Catch:{ Exception -> 0x014d }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x014d }
            r11.<init>()     // Catch:{ Exception -> 0x014d }
            r11.append(r7)     // Catch:{ Exception -> 0x014d }
            double r12 = java.lang.Math.random()     // Catch:{ Exception -> 0x014d }
            r11.append(r12)     // Catch:{ Exception -> 0x014d }
            r12 = 2147483647(0x7fffffff, float:NaN)
            r11.append(r12)     // Catch:{ Exception -> 0x014d }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x014d }
            r10.setAction(r11)     // Catch:{ Exception -> 0x014d }
            r13 = 0
            java.lang.String r11 = "userId"
            java.lang.String r15 = "chatId"
            int r16 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r16 <= 0) goto L_0x0064
            r10.putExtra(r11, r2)     // Catch:{ Exception -> 0x014d }
            goto L_0x0068
        L_0x0064:
            long r13 = -r2
            r10.putExtra(r15, r13)     // Catch:{ Exception -> 0x014d }
        L_0x0068:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r13 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ Exception -> 0x014d }
            android.content.Context r14 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x014d }
            r13.<init>((android.content.Context) r14, (java.lang.String) r8)     // Catch:{ Exception -> 0x014d }
            if (r5 == 0) goto L_0x0074
            r5 = r21
            goto L_0x0078
        L_0x0074:
            java.lang.String r5 = org.telegram.messenger.UserObject.getFirstName(r22)     // Catch:{ Exception -> 0x014d }
        L_0x0078:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r13.setShortLabel(r5)     // Catch:{ Exception -> 0x014d }
            r13 = r21
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setLongLabel(r13)     // Catch:{ Exception -> 0x014d }
            android.content.Intent r13 = new android.content.Intent     // Catch:{ Exception -> 0x014d }
            java.lang.String r14 = "android.intent.action.VIEW"
            r13.<init>(r14)     // Catch:{ Exception -> 0x014d }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setIntent(r13)     // Catch:{ Exception -> 0x014d }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setIntent(r10)     // Catch:{ Exception -> 0x014d }
            r10 = 1
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setLongLived(r10)     // Catch:{ Exception -> 0x014d }
            androidx.core.content.LocusIdCompat r13 = new androidx.core.content.LocusIdCompat     // Catch:{ Exception -> 0x014d }
            r13.<init>(r8)     // Catch:{ Exception -> 0x014d }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r5 = r5.setLocusId(r13)     // Catch:{ Exception -> 0x014d }
            if (r6 == 0) goto L_0x00ba
            r5.setPerson(r6)     // Catch:{ Exception -> 0x014d }
            androidx.core.graphics.drawable.IconCompat r13 = r24.getIcon()     // Catch:{ Exception -> 0x014d }
            r5.setIcon(r13)     // Catch:{ Exception -> 0x014d }
            androidx.core.graphics.drawable.IconCompat r13 = r24.getIcon()     // Catch:{ Exception -> 0x014d }
            if (r13 == 0) goto L_0x00ba
            androidx.core.graphics.drawable.IconCompat r6 = r24.getIcon()     // Catch:{ Exception -> 0x014d }
            android.graphics.Bitmap r6 = r6.getBitmap()     // Catch:{ Exception -> 0x014d }
            goto L_0x00bb
        L_0x00ba:
            r6 = 0
        L_0x00bb:
            androidx.core.content.pm.ShortcutInfoCompat r5 = r5.build()     // Catch:{ Exception -> 0x014d }
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x014d }
            androidx.core.content.pm.ShortcutManagerCompat.pushDynamicShortcut(r13, r5)     // Catch:{ Exception -> 0x014d }
            r0.setShortcutInfo(r5)     // Catch:{ Exception -> 0x014d }
            android.content.Intent r5 = new android.content.Intent     // Catch:{ Exception -> 0x014d }
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x014d }
            java.lang.Class<org.telegram.ui.BubbleActivity> r14 = org.telegram.ui.BubbleActivity.class
            r5.<init>(r13, r14)     // Catch:{ Exception -> 0x014d }
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x014d }
            r13.<init>()     // Catch:{ Exception -> 0x014d }
            r13.append(r7)     // Catch:{ Exception -> 0x014d }
            double r9 = java.lang.Math.random()     // Catch:{ Exception -> 0x014d }
            r13.append(r9)     // Catch:{ Exception -> 0x014d }
            r13.append(r12)     // Catch:{ Exception -> 0x014d }
            java.lang.String r9 = r13.toString()     // Catch:{ Exception -> 0x014d }
            r5.setAction(r9)     // Catch:{ Exception -> 0x014d }
            boolean r9 = org.telegram.messenger.DialogObject.isUserDialog(r19)     // Catch:{ Exception -> 0x014d }
            if (r9 == 0) goto L_0x00f3
            r5.putExtra(r11, r2)     // Catch:{ Exception -> 0x014d }
            goto L_0x00f7
        L_0x00f3:
            long r9 = -r2
            r5.putExtra(r15, r9)     // Catch:{ Exception -> 0x014d }
        L_0x00f7:
            java.lang.String r9 = "currentAccount"
            int r10 = r1.currentAccount     // Catch:{ Exception -> 0x014d }
            r5.putExtra(r9, r10)     // Catch:{ Exception -> 0x014d }
            if (r6 == 0) goto L_0x0105
            androidx.core.graphics.drawable.IconCompat r4 = androidx.core.graphics.drawable.IconCompat.createWithAdaptiveBitmap(r6)     // Catch:{ Exception -> 0x014d }
            goto L_0x011f
        L_0x0105:
            if (r4 == 0) goto L_0x0117
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x014d }
            boolean r4 = r4.bot     // Catch:{ Exception -> 0x014d }
            if (r4 == 0) goto L_0x0110
            int r4 = org.telegram.messenger.R.drawable.book_bot     // Catch:{ Exception -> 0x014d }
            goto L_0x0112
        L_0x0110:
            int r4 = org.telegram.messenger.R.drawable.book_user     // Catch:{ Exception -> 0x014d }
        L_0x0112:
            androidx.core.graphics.drawable.IconCompat r4 = androidx.core.graphics.drawable.IconCompat.createWithResource(r6, r4)     // Catch:{ Exception -> 0x014d }
            goto L_0x011f
        L_0x0117:
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x014d }
            int r6 = org.telegram.messenger.R.drawable.book_group     // Catch:{ Exception -> 0x014d }
            androidx.core.graphics.drawable.IconCompat r4 = androidx.core.graphics.drawable.IconCompat.createWithResource(r4, r6)     // Catch:{ Exception -> 0x014d }
        L_0x011f:
            androidx.core.app.NotificationCompat$BubbleMetadata$Builder r6 = new androidx.core.app.NotificationCompat$BubbleMetadata$Builder     // Catch:{ Exception -> 0x014d }
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x014d }
            r10 = 134217728(0x8000000, float:3.85186E-34)
            r11 = 0
            android.app.PendingIntent r5 = android.app.PendingIntent.getActivity(r9, r11, r5, r10)     // Catch:{ Exception -> 0x014d }
            r6.<init>(r5, r4)     // Catch:{ Exception -> 0x014d }
            long r4 = r1.openedDialogId     // Catch:{ Exception -> 0x014d }
            int r9 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r9 != 0) goto L_0x0135
            r10 = 1
            goto L_0x0136
        L_0x0135:
            r10 = 0
        L_0x0136:
            r6.setSuppressNotification(r10)     // Catch:{ Exception -> 0x014d }
            r6.setAutoExpandBubble(r11)     // Catch:{ Exception -> 0x014d }
            r2 = 1142947840(0x44200000, float:640.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x014d }
            r6.setDesiredHeight(r2)     // Catch:{ Exception -> 0x014d }
            androidx.core.app.NotificationCompat$BubbleMetadata r2 = r6.build()     // Catch:{ Exception -> 0x014d }
            r0.setBubbleMetadata(r2)     // Catch:{ Exception -> 0x014d }
            return r8
        L_0x014d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r2 = 0
            return r2
        L_0x0153:
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
                    arrayList.add(new NotificationChannelGroup(str3, LocaleController.getString("NotificationsChannels", R.string.NotificationsChannels) + str));
                }
                if (str4 != null) {
                    arrayList.add(new NotificationChannelGroup(str4, LocaleController.getString("NotificationsGroups", R.string.NotificationsGroups) + str));
                }
                if (str6 != null) {
                    arrayList.add(new NotificationChannelGroup(str6, LocaleController.getString("NotificationsPrivateChats", R.string.NotificationsPrivateChats) + str));
                }
                if (str5 != null) {
                    arrayList.add(new NotificationChannelGroup(str5, LocaleController.getString("NotificationsOther", R.string.NotificationsOther) + str));
                }
                systemNotificationManager.createNotificationChannelGroups(arrayList);
            }
            this.channelGroupsCreated = true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:187:0x03fe A[LOOP:1: B:185:0x03fb->B:187:0x03fe, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0413  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x0461  */
    @android.annotation.TargetApi(26)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String validateChannelId(long r27, java.lang.String r29, long[] r30, int r31, android.net.Uri r32, int r33, boolean r34, boolean r35, boolean r36, int r37) {
        /*
            r26 = this;
            r1 = r26
            r2 = r27
            r4 = r32
            r5 = r33
            r0 = r37
            r26.ensureGroupsCreated()
            org.telegram.messenger.AccountInstance r6 = r26.getAccountInstance()
            android.content.SharedPreferences r6 = r6.getNotificationsSettings()
            java.lang.String r7 = "groups"
            java.lang.String r8 = "private"
            java.lang.String r9 = "channels"
            r11 = 2
            if (r36 == 0) goto L_0x0033
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "other"
            r12.append(r13)
            int r13 = r1.currentAccount
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            r13 = 0
            goto L_0x0072
        L_0x0033:
            if (r0 != r11) goto L_0x0049
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r9)
            int r13 = r1.currentAccount
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            java.lang.String r13 = "overwrite_channel"
            goto L_0x0072
        L_0x0049:
            if (r0 != 0) goto L_0x005f
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r7)
            int r13 = r1.currentAccount
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            java.lang.String r13 = "overwrite_group"
            goto L_0x0072
        L_0x005f:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r8)
            int r13 = r1.currentAccount
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            java.lang.String r13 = "overwrite_private"
        L_0x0072:
            r15 = 0
            if (r34 != 0) goto L_0x007e
            boolean r16 = org.telegram.messenger.DialogObject.isEncryptedDialog(r27)
            if (r16 == 0) goto L_0x007e
            r16 = 1
            goto L_0x0080
        L_0x007e:
            r16 = 0
        L_0x0080:
            if (r35 != 0) goto L_0x008c
            if (r13 == 0) goto L_0x008c
            boolean r13 = r6.getBoolean(r13, r15)
            if (r13 == 0) goto L_0x008c
            r13 = 1
            goto L_0x008d
        L_0x008c:
            r13 = 0
        L_0x008d:
            if (r4 != 0) goto L_0x0092
            java.lang.String r17 = "NoSound"
            goto L_0x0096
        L_0x0092:
            java.lang.String r17 = r32.toString()
        L_0x0096:
            java.lang.String r10 = org.telegram.messenger.Utilities.MD5(r17)
            r14 = 5
            if (r10 == 0) goto L_0x00a7
            int r11 = r10.length()
            if (r11 <= r14) goto L_0x00a7
            java.lang.String r10 = r10.substring(r15, r14)
        L_0x00a7:
            if (r36 == 0) goto L_0x00b4
            int r7 = org.telegram.messenger.R.string.NotificationsSilent
            java.lang.String r8 = "NotificationsSilent"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r8 = "silent"
            goto L_0x0104
        L_0x00b4:
            if (r34 == 0) goto L_0x00dc
            if (r35 == 0) goto L_0x00bd
            int r11 = org.telegram.messenger.R.string.NotificationsInAppDefault
            java.lang.String r14 = "NotificationsInAppDefault"
            goto L_0x00c1
        L_0x00bd:
            int r11 = org.telegram.messenger.R.string.NotificationsDefault
            java.lang.String r14 = "NotificationsDefault"
        L_0x00c1:
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r14 = 2
            if (r0 != r14) goto L_0x00cf
            if (r35 == 0) goto L_0x00cc
            java.lang.String r9 = "channels_ia"
        L_0x00cc:
            r8 = r9
        L_0x00cd:
            r7 = r11
            goto L_0x0104
        L_0x00cf:
            if (r0 != 0) goto L_0x00d7
            if (r35 == 0) goto L_0x00d5
            java.lang.String r7 = "groups_ia"
        L_0x00d5:
            r8 = r7
            goto L_0x00cd
        L_0x00d7:
            if (r35 == 0) goto L_0x00cd
            java.lang.String r8 = "private_ia"
            goto L_0x00cd
        L_0x00dc:
            if (r35 == 0) goto L_0x00ec
            int r7 = org.telegram.messenger.R.string.NotificationsChatInApp
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r9[r15] = r29
            java.lang.String r8 = "NotificationsChatInApp"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r7, r9)
            goto L_0x00ee
        L_0x00ec:
            r7 = r29
        L_0x00ee:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            if (r35 == 0) goto L_0x00f8
            java.lang.String r9 = "org.telegram.keyia"
            goto L_0x00fa
        L_0x00f8:
            java.lang.String r9 = "org.telegram.key"
        L_0x00fa:
            r8.append(r9)
            r8.append(r2)
            java.lang.String r8 = r8.toString()
        L_0x0104:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            java.lang.String r8 = "_"
            r9.append(r8)
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r10 = 0
            java.lang.String r11 = r6.getString(r9, r10)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r9)
            java.lang.String r15 = "_s"
            r14.append(r15)
            java.lang.String r14 = r14.toString()
            java.lang.String r14 = r6.getString(r14, r10)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r29 = r7
            java.lang.String r7 = "secret"
            r19 = r12
            if (r11 == 0) goto L_0x0396
            android.app.NotificationManager r12 = systemNotificationManager
            android.app.NotificationChannel r12 = r12.getNotificationChannel(r11)
            boolean r20 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r21 = r8
            java.lang.String r8 = " = "
            if (r20 == 0) goto L_0x016a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r20 = r15
            java.lang.String r15 = "current channel for "
            r4.append(r15)
            r4.append(r11)
            r4.append(r8)
            r4.append(r12)
            java.lang.String r4 = r4.toString()
            org.telegram.messenger.FileLog.d(r4)
            goto L_0x016c
        L_0x016a:
            r20 = r15
        L_0x016c:
            if (r12 == 0) goto L_0x0387
            if (r36 != 0) goto L_0x037f
            if (r13 != 0) goto L_0x037f
            int r4 = r12.getImportance()
            android.net.Uri r15 = r12.getSound()
            long[] r22 = r12.getVibrationPattern()
            r23 = r13
            boolean r13 = r12.shouldVibrate()
            if (r13 != 0) goto L_0x0193
            if (r22 != 0) goto L_0x0193
            r24 = r9
            r25 = r13
            r9 = 2
            long[] r13 = new long[r9]
            r13 = {0, 0} // fill-array
            goto L_0x0199
        L_0x0193:
            r24 = r9
            r25 = r13
            r13 = r22
        L_0x0199:
            int r9 = r12.getLightColor()
            if (r13 == 0) goto L_0x01ad
            r12 = 0
        L_0x01a0:
            int r5 = r13.length
            if (r12 >= r5) goto L_0x01ad
            r2 = r13[r12]
            r10.append(r2)
            int r12 = r12 + 1
            r2 = r27
            goto L_0x01a0
        L_0x01ad:
            r10.append(r9)
            if (r15 == 0) goto L_0x01b9
            java.lang.String r2 = r15.toString()
            r10.append(r2)
        L_0x01b9:
            r10.append(r4)
            if (r34 != 0) goto L_0x01c3
            if (r16 == 0) goto L_0x01c3
            r10.append(r7)
        L_0x01c3:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x01e9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "current channel settings for "
            r2.append(r3)
            r2.append(r11)
            r2.append(r8)
            r2.append(r10)
            java.lang.String r3 = " old = "
            r2.append(r3)
            r2.append(r14)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x01e9:
            java.lang.String r2 = r10.toString()
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r3 = 0
            r10.setLength(r3)
            boolean r3 = r2.equals(r14)
            if (r3 != 0) goto L_0x036e
            java.lang.String r3 = "notify2_"
            if (r4 != 0) goto L_0x023b
            android.content.SharedPreferences$Editor r4 = r6.edit()
            if (r34 == 0) goto L_0x0219
            if (r35 != 0) goto L_0x0214
            java.lang.String r3 = getGlobalNotificationsKey(r37)
            r5 = 2147483647(0x7fffffff, float:NaN)
            r4.putInt(r3, r5)
            r1.updateServerNotificationsSettings((int) r0)
        L_0x0214:
            r8 = r11
            r5 = 1
            r11 = r27
            goto L_0x0233
        L_0x0219:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            r8 = r11
            r11 = r27
            r5.append(r11)
            java.lang.String r3 = r5.toString()
            r5 = 2
            r4.putInt(r3, r5)
            r5 = 1
            r1.updateServerNotificationsSettings(r11, r5)
        L_0x0233:
            r15 = r33
            r22 = r2
            r2 = r30
            goto L_0x02d0
        L_0x023b:
            r15 = r33
            r8 = r11
            r5 = 1
            r11 = r27
            if (r4 == r15) goto L_0x02ca
            if (r35 != 0) goto L_0x02c2
            android.content.SharedPreferences$Editor r5 = r6.edit()
            r22 = r2
            r2 = 4
            if (r4 == r2) goto L_0x025f
            r2 = 5
            if (r4 != r2) goto L_0x0252
            goto L_0x025f
        L_0x0252:
            r2 = 1
            if (r4 != r2) goto L_0x0258
            r2 = 2
            r4 = 4
            goto L_0x0261
        L_0x0258:
            r2 = 2
            if (r4 != r2) goto L_0x025d
            r4 = 5
            goto L_0x0261
        L_0x025d:
            r4 = 0
            goto L_0x0261
        L_0x025f:
            r2 = 2
            r4 = 1
        L_0x0261:
            if (r34 == 0) goto L_0x0286
            java.lang.String r3 = getGlobalNotificationsKey(r37)
            r2 = 0
            android.content.SharedPreferences$Editor r3 = r5.putInt(r3, r2)
            r3.commit()
            r2 = 2
            if (r0 != r2) goto L_0x0278
            java.lang.String r2 = "priority_channel"
            r5.putInt(r2, r4)
            goto L_0x02c5
        L_0x0278:
            if (r0 != 0) goto L_0x0280
            java.lang.String r2 = "priority_group"
            r5.putInt(r2, r4)
            goto L_0x02c5
        L_0x0280:
            java.lang.String r2 = "priority_messages"
            r5.putInt(r2, r4)
            goto L_0x02c5
        L_0x0286:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            r3 = 0
            r5.putInt(r2, r3)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "notifyuntil_"
            r2.append(r3)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            r5.remove(r2)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "priority_"
            r2.append(r3)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            r5.putInt(r2, r4)
            goto L_0x02c5
        L_0x02c2:
            r22 = r2
            r5 = 0
        L_0x02c5:
            r2 = r30
            r4 = r5
            r5 = 1
            goto L_0x02d0
        L_0x02ca:
            r22 = r2
            r2 = r30
            r4 = 0
            r5 = 0
        L_0x02d0:
            boolean r3 = r1.isEmptyVibration(r2)
            r17 = 1
            r3 = r3 ^ 1
            r2 = r25
            if (r3 == r2) goto L_0x0329
            if (r35 != 0) goto L_0x0325
            if (r4 != 0) goto L_0x02e4
            android.content.SharedPreferences$Editor r4 = r6.edit()
        L_0x02e4:
            if (r34 == 0) goto L_0x030c
            r3 = 2
            if (r0 != r3) goto L_0x02f4
            if (r2 == 0) goto L_0x02ed
            r2 = 0
            goto L_0x02ee
        L_0x02ed:
            r2 = 2
        L_0x02ee:
            java.lang.String r3 = "vibrate_channel"
            r4.putInt(r3, r2)
            goto L_0x0325
        L_0x02f4:
            if (r0 != 0) goto L_0x0301
            if (r2 == 0) goto L_0x02fa
            r2 = 0
            goto L_0x02fb
        L_0x02fa:
            r2 = 2
        L_0x02fb:
            java.lang.String r3 = "vibrate_group"
            r4.putInt(r3, r2)
            goto L_0x0325
        L_0x0301:
            if (r2 == 0) goto L_0x0305
            r2 = 0
            goto L_0x0306
        L_0x0305:
            r2 = 2
        L_0x0306:
            java.lang.String r3 = "vibrate_messages"
            r4.putInt(r3, r2)
            goto L_0x0325
        L_0x030c:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "vibrate_"
            r3.append(r5)
            r3.append(r11)
            java.lang.String r3 = r3.toString()
            if (r2 == 0) goto L_0x0321
            r2 = 0
            goto L_0x0322
        L_0x0321:
            r2 = 2
        L_0x0322:
            r4.putInt(r3, r2)
        L_0x0325:
            r2 = r31
            r5 = 1
            goto L_0x032d
        L_0x0329:
            r13 = r30
            r2 = r31
        L_0x032d:
            if (r9 == r2) goto L_0x0366
            if (r35 != 0) goto L_0x0364
            if (r4 != 0) goto L_0x0337
            android.content.SharedPreferences$Editor r4 = r6.edit()
        L_0x0337:
            if (r34 == 0) goto L_0x0350
            r2 = 2
            if (r0 != r2) goto L_0x0342
            java.lang.String r0 = "ChannelLed"
            r4.putInt(r0, r9)
            goto L_0x0364
        L_0x0342:
            if (r0 != 0) goto L_0x034a
            java.lang.String r0 = "GroupLed"
            r4.putInt(r0, r9)
            goto L_0x0364
        L_0x034a:
            java.lang.String r0 = "MessagesLed"
            r4.putInt(r0, r9)
            goto L_0x0364
        L_0x0350:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "color_"
            r0.append(r2)
            r0.append(r11)
            java.lang.String r0 = r0.toString()
            r4.putInt(r0, r9)
        L_0x0364:
            r5 = 1
            goto L_0x0367
        L_0x0366:
            r9 = r2
        L_0x0367:
            if (r4 == 0) goto L_0x036c
            r4.commit()
        L_0x036c:
            r3 = r5
            goto L_0x037b
        L_0x036e:
            r15 = r33
            r22 = r2
            r8 = r11
            r11 = r27
            r2 = r31
            r13 = r30
            r9 = r2
            r3 = 0
        L_0x037b:
            r2 = r9
            r0 = r22
            goto L_0x03a7
        L_0x037f:
            r15 = r5
            r24 = r9
            r8 = r11
            r23 = r13
            r11 = r2
            goto L_0x03a1
        L_0x0387:
            r11 = r2
            r15 = r5
            r24 = r9
            r23 = r13
            r2 = r31
            r13 = r30
            r0 = 0
            r3 = 0
            r8 = 0
            r14 = 0
            goto L_0x03a7
        L_0x0396:
            r21 = r8
            r24 = r9
            r8 = r11
            r23 = r13
            r20 = r15
            r11 = r2
            r15 = r5
        L_0x03a1:
            r2 = r31
            r13 = r30
            r0 = 0
            r3 = 0
        L_0x03a7:
            if (r3 == 0) goto L_0x03e6
            if (r0 == 0) goto L_0x03e6
            android.content.SharedPreferences$Editor r3 = r6.edit()
            r4 = r24
            android.content.SharedPreferences$Editor r3 = r3.putString(r4, r8)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r4)
            r9 = r20
            r5.append(r9)
            java.lang.String r5 = r5.toString()
            android.content.SharedPreferences$Editor r3 = r3.putString(r5, r0)
            r3.commit()
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x03f3
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "change edited channel "
            r3.append(r5)
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
            goto L_0x03f3
        L_0x03e6:
            r9 = r20
            r4 = r24
            if (r23 != 0) goto L_0x03fa
            if (r0 == 0) goto L_0x03fa
            if (r35 == 0) goto L_0x03fa
            if (r34 != 0) goto L_0x03f3
            goto L_0x03fa
        L_0x03f3:
            r3 = r32
            r18 = r6
        L_0x03f7:
            r10 = r8
            goto L_0x045f
        L_0x03fa:
            r3 = 0
        L_0x03fb:
            int r0 = r13.length
            if (r3 >= r0) goto L_0x040a
            r18 = r6
            r5 = r13[r3]
            r10.append(r5)
            int r3 = r3 + 1
            r6 = r18
            goto L_0x03fb
        L_0x040a:
            r18 = r6
            r10.append(r2)
            r3 = r32
            if (r3 == 0) goto L_0x041a
            java.lang.String r0 = r32.toString()
            r10.append(r0)
        L_0x041a:
            r10.append(r15)
            if (r34 != 0) goto L_0x0424
            if (r16 == 0) goto L_0x0424
            r10.append(r7)
        L_0x0424:
            java.lang.String r0 = r10.toString()
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r0)
            if (r36 != 0) goto L_0x045d
            if (r8 == 0) goto L_0x045d
            if (r23 != 0) goto L_0x0438
            boolean r0 = r14.equals(r5)
            if (r0 != 0) goto L_0x045d
        L_0x0438:
            android.app.NotificationManager r0 = systemNotificationManager     // Catch:{ Exception -> 0x043e }
            r0.deleteNotificationChannel(r8)     // Catch:{ Exception -> 0x043e }
            goto L_0x0442
        L_0x043e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0442:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x045a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "delete channel by settings change "
            r0.append(r6)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x045a:
            r0 = r5
            r10 = 0
            goto L_0x045f
        L_0x045d:
            r0 = r5
            goto L_0x03f7
        L_0x045f:
            if (r10 != 0) goto L_0x0541
            java.lang.String r5 = "channel_"
            if (r34 == 0) goto L_0x0488
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            int r7 = r1.currentAccount
            r6.append(r7)
            r6.append(r5)
            r6.append(r4)
            r7 = r21
            r6.append(r7)
            java.security.SecureRandom r5 = org.telegram.messenger.Utilities.random
            long r7 = r5.nextLong()
            r6.append(r7)
            java.lang.String r5 = r6.toString()
            goto L_0x04aa
        L_0x0488:
            r7 = r21
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            int r8 = r1.currentAccount
            r6.append(r8)
            r6.append(r5)
            r6.append(r11)
            r6.append(r7)
            java.security.SecureRandom r5 = org.telegram.messenger.Utilities.random
            long r7 = r5.nextLong()
            r6.append(r7)
            java.lang.String r5 = r6.toString()
        L_0x04aa:
            r10 = r5
            android.app.NotificationChannel r5 = new android.app.NotificationChannel
            if (r16 == 0) goto L_0x04b8
            int r6 = org.telegram.messenger.R.string.SecretChatName
            java.lang.String r7 = "SecretChatName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x04ba
        L_0x04b8:
            r7 = r29
        L_0x04ba:
            r5.<init>(r10, r7, r15)
            r12 = r19
            r5.setGroup(r12)
            if (r2 == 0) goto L_0x04cd
            r6 = 1
            r5.enableLights(r6)
            r5.setLightColor(r2)
            r2 = 0
            goto L_0x04d2
        L_0x04cd:
            r2 = 0
            r6 = 1
            r5.enableLights(r2)
        L_0x04d2:
            boolean r7 = r1.isEmptyVibration(r13)
            if (r7 != 0) goto L_0x04e2
            r5.enableVibration(r6)
            int r2 = r13.length
            if (r2 <= 0) goto L_0x04e5
            r5.setVibrationPattern(r13)
            goto L_0x04e5
        L_0x04e2:
            r5.enableVibration(r2)
        L_0x04e5:
            android.media.AudioAttributes$Builder r2 = new android.media.AudioAttributes$Builder
            r2.<init>()
            r6 = 4
            r2.setContentType(r6)
            r6 = 5
            r2.setUsage(r6)
            if (r3 == 0) goto L_0x04fc
            android.media.AudioAttributes r2 = r2.build()
            r5.setSound(r3, r2)
            goto L_0x0500
        L_0x04fc:
            r2 = 0
            r5.setSound(r2, r2)
        L_0x0500:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0518
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "create new channel "
            r2.append(r3)
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0518:
            long r2 = android.os.SystemClock.elapsedRealtime()
            r1.lastNotificationChannelCreateTime = r2
            android.app.NotificationManager r2 = systemNotificationManager
            r2.createNotificationChannel(r5)
            android.content.SharedPreferences$Editor r2 = r18.edit()
            android.content.SharedPreferences$Editor r2 = r2.putString(r4, r10)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r4)
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            android.content.SharedPreferences$Editor r0 = r2.putString(r3, r0)
            r0.commit()
        L_0x0541:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: long[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v12, resolved type: long[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v14, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v15, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v16, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v17, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v23, resolved type: long[]} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: long[]} */
    /* JADX WARNING: type inference failed for: r14v13 */
    /* JADX WARNING: type inference failed for: r6v89 */
    /* JADX WARNING: type inference failed for: r6v90 */
    /* JADX WARNING: type inference failed for: r6v98 */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x087d, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x087f;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:409:0x0970 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x01b3 A[SYNTHETIC, Splitter:B:100:0x01b3] */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x01ce A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x021e A[SYNTHETIC, Splitter:B:106:0x021e] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0294 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0350 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x042a A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x044e A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0451 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x046a A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x0510 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x051e A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:207:0x05a1 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x05fe A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0602 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0610 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0613 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0619 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x061f A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x063c A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0677 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x067c A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x06b3 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x0725 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x07e5 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0833  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x083f  */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x0877 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x0886 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0982 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x098c A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0993 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x09a1 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0a0f A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:456:0x0ab5 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x0ae2 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:467:0x0af9 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0119 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012b A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x012f A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0147 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x015d A[SYNTHETIC, Splitter:B:84:0x015d] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0190 A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x019c A[Catch:{ Exception -> 0x0b29 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r48) {
        /*
            r47 = this;
            r15 = r47
            java.lang.String r1 = "currentAccount"
            org.telegram.messenger.UserConfig r2 = r47.getUserConfig()
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x0b2f
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r15.pushMessages
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0b2f
            boolean r2 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r2 != 0) goto L_0x0022
            int r2 = r15.currentAccount
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            if (r2 == r3) goto L_0x0022
            goto L_0x0b2f
        L_0x0022:
            org.telegram.tgnet.ConnectionsManager r2 = r47.getConnectionsManager()     // Catch:{ Exception -> 0x0b29 }
            r2.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0b29 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r15.pushMessages     // Catch:{ Exception -> 0x0b29 }
            r3 = 0
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x0b29 }
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2     // Catch:{ Exception -> 0x0b29 }
            org.telegram.messenger.AccountInstance r4 = r47.getAccountInstance()     // Catch:{ Exception -> 0x0b29 }
            android.content.SharedPreferences r4 = r4.getNotificationsSettings()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r5 = "dismissDate"
            int r5 = r4.getInt(r5, r3)     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner     // Catch:{ Exception -> 0x0b29 }
            int r6 = r6.date     // Catch:{ Exception -> 0x0b29 }
            if (r6 > r5) goto L_0x004a
            r47.dismissNotification()     // Catch:{ Exception -> 0x0b29 }
            return
        L_0x004a:
            long r6 = r2.getDialogId()     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$Message r8 = r2.messageOwner     // Catch:{ Exception -> 0x0b29 }
            boolean r8 = r8.mentioned     // Catch:{ Exception -> 0x0b29 }
            if (r8 == 0) goto L_0x0059
            long r8 = r2.getFromChatId()     // Catch:{ Exception -> 0x0b29 }
            goto L_0x005a
        L_0x0059:
            r8 = r6
        L_0x005a:
            r2.getId()     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.peer_id     // Catch:{ Exception -> 0x0b29 }
            long r11 = r10.chat_id     // Catch:{ Exception -> 0x0b29 }
            r13 = 0
            int r16 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r16 == 0) goto L_0x006a
            goto L_0x006c
        L_0x006a:
            long r11 = r10.channel_id     // Catch:{ Exception -> 0x0b29 }
        L_0x006c:
            r17 = r4
            long r3 = r10.user_id     // Catch:{ Exception -> 0x0b29 }
            boolean r10 = r2.isFromUser()     // Catch:{ Exception -> 0x0b29 }
            if (r10 == 0) goto L_0x008c
            int r10 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r10 == 0) goto L_0x0086
            org.telegram.messenger.UserConfig r10 = r47.getUserConfig()     // Catch:{ Exception -> 0x0b29 }
            long r18 = r10.getClientUserId()     // Catch:{ Exception -> 0x0b29 }
            int r10 = (r3 > r18 ? 1 : (r3 == r18 ? 0 : -1))
            if (r10 != 0) goto L_0x008c
        L_0x0086:
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id     // Catch:{ Exception -> 0x0b29 }
            long r3 = r3.user_id     // Catch:{ Exception -> 0x0b29 }
        L_0x008c:
            org.telegram.messenger.MessagesController r10 = r47.getMessagesController()     // Catch:{ Exception -> 0x0b29 }
            java.lang.Long r13 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r13)     // Catch:{ Exception -> 0x0b29 }
            r18 = 0
            int r20 = (r11 > r18 ? 1 : (r11 == r18 ? 0 : -1))
            if (r20 == 0) goto L_0x00c5
            org.telegram.messenger.MessagesController r13 = r47.getMessagesController()     // Catch:{ Exception -> 0x0b29 }
            java.lang.Long r14 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$Chat r13 = r13.getChat(r14)     // Catch:{ Exception -> 0x0b29 }
            if (r13 != 0) goto L_0x00b5
            boolean r14 = r2.isFcmMessage()     // Catch:{ Exception -> 0x0b29 }
            if (r14 == 0) goto L_0x00b5
            boolean r14 = r2.localChannel     // Catch:{ Exception -> 0x0b29 }
            goto L_0x00c2
        L_0x00b5:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r13)     // Catch:{ Exception -> 0x0b29 }
            if (r14 == 0) goto L_0x00c1
            boolean r14 = r13.megagroup     // Catch:{ Exception -> 0x0b29 }
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
            r46 = r17
            r17 = r1
            r1 = r46
            int r3 = r15.getNotifyOverride(r1, r8)     // Catch:{ Exception -> 0x0b29 }
            r4 = -1
            r23 = r1
            r1 = 2
            if (r3 != r4) goto L_0x00e2
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r14)     // Catch:{ Exception -> 0x0b29 }
            boolean r3 = r15.isGlobalNotificationsEnabled(r6, r3)     // Catch:{ Exception -> 0x0b29 }
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
            boolean r24 = r2.isFcmMessage()     // Catch:{ Exception -> 0x0b29 }
            if (r24 == 0) goto L_0x00fa
            java.lang.String r4 = r2.localName     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0103
        L_0x00fa:
            if (r13 == 0) goto L_0x00ff
            java.lang.String r4 = r13.title     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0103
        L_0x00ff:
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r10)     // Catch:{ Exception -> 0x0b29 }
        L_0x0103:
            r25 = r4
            boolean r4 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b29 }
            if (r4 != 0) goto L_0x0112
            boolean r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b29 }
            if (r4 == 0) goto L_0x0110
            goto L_0x0112
        L_0x0110:
            r4 = 0
            goto L_0x0113
        L_0x0112:
            r4 = 1
        L_0x0113:
            boolean r26 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)     // Catch:{ Exception -> 0x0b29 }
            if (r26 != 0) goto L_0x012b
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0b29 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0b29 }
            r27 = r2
            r2 = 1
            if (r1 > r2) goto L_0x012d
            if (r4 == 0) goto L_0x0127
            goto L_0x012d
        L_0x0127:
            r1 = r25
            r2 = 1
            goto L_0x0150
        L_0x012b:
            r27 = r2
        L_0x012d:
            if (r4 == 0) goto L_0x0147
            r1 = 0
            int r4 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
            if (r4 == 0) goto L_0x013e
            java.lang.String r1 = "NotificationHiddenChatName"
            int r2 = org.telegram.messenger.R.string.NotificationHiddenChatName     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x014f
        L_0x013e:
            java.lang.String r1 = "NotificationHiddenName"
            int r2 = org.telegram.messenger.R.string.NotificationHiddenName     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x014f
        L_0x0147:
            java.lang.String r1 = "AppName"
            int r2 = org.telegram.messenger.R.string.AppName     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0b29 }
        L_0x014f:
            r2 = 0
        L_0x0150:
            int r4 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0b29 }
            r28 = r10
            java.lang.String r10 = ""
            r29 = r14
            r14 = 1
            if (r4 <= r14) goto L_0x0190
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0b29 }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0b29 }
            if (r4 != r14) goto L_0x0172
            org.telegram.messenger.UserConfig r4 = r47.getUserConfig()     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$User r4 = r4.getCurrentUser()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0191
        L_0x0172:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r4.<init>()     // Catch:{ Exception -> 0x0b29 }
            org.telegram.messenger.UserConfig r14 = r47.getUserConfig()     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$User r14 = r14.getCurrentUser()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r14 = org.telegram.messenger.UserObject.getFirstName(r14)     // Catch:{ Exception -> 0x0b29 }
            r4.append(r14)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r14 = "・"
            r4.append(r14)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0191
        L_0x0190:
            r4 = r10
        L_0x0191:
            androidx.collection.LongSparseArray<java.lang.Integer> r14 = r15.pushDialogs     // Catch:{ Exception -> 0x0b29 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0b29 }
            r30 = r11
            r11 = 1
            if (r14 != r11) goto L_0x01a8
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b29 }
            r12 = 23
            if (r11 >= r12) goto L_0x01a3
            goto L_0x01a8
        L_0x01a3:
            r32 = r6
        L_0x01a5:
            r34 = r8
            goto L_0x0208
        L_0x01a8:
            androidx.collection.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0b29 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r12 = "NewMessages"
            r14 = 1
            if (r11 != r14) goto L_0x01ce
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r11.<init>()     // Catch:{ Exception -> 0x0b29 }
            r11.append(r4)     // Catch:{ Exception -> 0x0b29 }
            int r4 = r15.total_unread_count     // Catch:{ Exception -> 0x0b29 }
            r32 = r6
            r14 = 0
            java.lang.Object[] r6 = new java.lang.Object[r14]     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r12, r4, r6)     // Catch:{ Exception -> 0x0b29 }
            r11.append(r4)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = r11.toString()     // Catch:{ Exception -> 0x0b29 }
            goto L_0x01a5
        L_0x01ce:
            r32 = r6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r6.<init>()     // Catch:{ Exception -> 0x0b29 }
            r6.append(r4)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = "NotificationMessagesPeopleDisplayOrder"
            int r7 = org.telegram.messenger.R.string.NotificationMessagesPeopleDisplayOrder     // Catch:{ Exception -> 0x0b29 }
            r11 = 2
            java.lang.Object[] r14 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x0b29 }
            int r11 = r15.total_unread_count     // Catch:{ Exception -> 0x0b29 }
            r34 = r8
            r8 = 0
            java.lang.Object[] r9 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r12, r11, r9)     // Catch:{ Exception -> 0x0b29 }
            r14[r8] = r9     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r9 = "FromChats"
            androidx.collection.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0b29 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0b29 }
            java.lang.Object[] r12 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatPluralString(r9, r11, r12)     // Catch:{ Exception -> 0x0b29 }
            r9 = 1
            r14[r9] = r8     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r7, r14)     // Catch:{ Exception -> 0x0b29 }
            r6.append(r4)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = r6.toString()     // Catch:{ Exception -> 0x0b29 }
        L_0x0208:
            androidx.core.app.NotificationCompat$Builder r6 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            r6.<init>(r7)     // Catch:{ Exception -> 0x0b29 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0b29 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r8 = ": "
            java.lang.String r9 = " "
            java.lang.String r11 = " @ "
            r14 = 1
            if (r7 != r14) goto L_0x0294
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r15.pushMessages     // Catch:{ Exception -> 0x0b29 }
            r7 = 0
            java.lang.Object r5 = r5.get(r7)     // Catch:{ Exception -> 0x0b29 }
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5     // Catch:{ Exception -> 0x0b29 }
            boolean[] r12 = new boolean[r14]     // Catch:{ Exception -> 0x0b29 }
            r36 = r3
            r14 = 0
            java.lang.String r3 = r15.getStringForMessage(r5, r7, r12, r14)     // Catch:{ Exception -> 0x0b29 }
            boolean r5 = r15.isSilentMessage(r5)     // Catch:{ Exception -> 0x0b29 }
            if (r3 != 0) goto L_0x0237
            return
        L_0x0237:
            if (r2 == 0) goto L_0x027c
            if (r13 == 0) goto L_0x024f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r2.<init>()     // Catch:{ Exception -> 0x0b29 }
            r2.append(r11)     // Catch:{ Exception -> 0x0b29 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r2 = r3.replace(r2, r10)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x027d
        L_0x024f:
            r2 = 0
            boolean r7 = r12[r2]     // Catch:{ Exception -> 0x0b29 }
            if (r7 == 0) goto L_0x0268
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r2.<init>()     // Catch:{ Exception -> 0x0b29 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b29 }
            r2.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r2 = r3.replace(r2, r10)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x027d
        L_0x0268:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r2.<init>()     // Catch:{ Exception -> 0x0b29 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b29 }
            r2.append(r9)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r2 = r3.replace(r2, r10)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x027d
        L_0x027c:
            r2 = r3
        L_0x027d:
            r6.setContentText(r2)     // Catch:{ Exception -> 0x0b29 }
            androidx.core.app.NotificationCompat$BigTextStyle r7 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0b29 }
            r7.<init>()     // Catch:{ Exception -> 0x0b29 }
            androidx.core.app.NotificationCompat$BigTextStyle r2 = r7.bigText(r2)     // Catch:{ Exception -> 0x0b29 }
            r6.setStyle(r2)     // Catch:{ Exception -> 0x0b29 }
            r2 = r6
            r46 = r4
            r4 = r3
            r3 = r46
            goto L_0x034e
        L_0x0294:
            r36 = r3
            r6.setContentText(r4)     // Catch:{ Exception -> 0x0b29 }
            androidx.core.app.NotificationCompat$InboxStyle r3 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0b29 }
            r3.<init>()     // Catch:{ Exception -> 0x0b29 }
            r3.setBigContentTitle(r1)     // Catch:{ Exception -> 0x0b29 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0b29 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0b29 }
            r12 = 10
            int r7 = java.lang.Math.min(r12, r7)     // Catch:{ Exception -> 0x0b29 }
            r12 = 1
            boolean[] r14 = new boolean[r12]     // Catch:{ Exception -> 0x0b29 }
            r38 = r6
            r6 = 2
            r12 = 0
            r37 = 0
        L_0x02b6:
            if (r12 >= r7) goto L_0x033e
            r39 = r7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0b29 }
            java.lang.Object r7 = r7.get(r12)     // Catch:{ Exception -> 0x0b29 }
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7     // Catch:{ Exception -> 0x0b29 }
            r42 = r3
            r40 = r4
            r41 = r12
            r4 = 0
            r12 = 0
            java.lang.String r3 = r15.getStringForMessage(r7, r12, r14, r4)     // Catch:{ Exception -> 0x0b29 }
            if (r3 == 0) goto L_0x0333
            org.telegram.tgnet.TLRPC$Message r4 = r7.messageOwner     // Catch:{ Exception -> 0x0b29 }
            int r4 = r4.date     // Catch:{ Exception -> 0x0b29 }
            if (r4 > r5) goto L_0x02d7
            goto L_0x0333
        L_0x02d7:
            r4 = 2
            if (r6 != r4) goto L_0x02e0
            boolean r6 = r15.isSilentMessage(r7)     // Catch:{ Exception -> 0x0b29 }
            r37 = r3
        L_0x02e0:
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0b29 }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0b29 }
            r7 = 1
            if (r4 != r7) goto L_0x032d
            if (r2 == 0) goto L_0x032d
            if (r13 == 0) goto L_0x0301
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r4.<init>()     // Catch:{ Exception -> 0x0b29 }
            r4.append(r11)     // Catch:{ Exception -> 0x0b29 }
            r4.append(r1)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = r3.replace(r4, r10)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x032d
        L_0x0301:
            r4 = 0
            boolean r7 = r14[r4]     // Catch:{ Exception -> 0x0b29 }
            if (r7 == 0) goto L_0x031a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r4.<init>()     // Catch:{ Exception -> 0x0b29 }
            r4.append(r1)     // Catch:{ Exception -> 0x0b29 }
            r4.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = r3.replace(r4, r10)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x032d
        L_0x031a:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r4.<init>()     // Catch:{ Exception -> 0x0b29 }
            r4.append(r1)     // Catch:{ Exception -> 0x0b29 }
            r4.append(r9)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = r3.replace(r4, r10)     // Catch:{ Exception -> 0x0b29 }
        L_0x032d:
            r4 = r42
            r4.addLine(r3)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0335
        L_0x0333:
            r4 = r42
        L_0x0335:
            int r12 = r41 + 1
            r3 = r4
            r7 = r39
            r4 = r40
            goto L_0x02b6
        L_0x033e:
            r46 = r4
            r4 = r3
            r3 = r46
            r4.setSummaryText(r3)     // Catch:{ Exception -> 0x0b29 }
            r2 = r38
            r2.setStyle(r4)     // Catch:{ Exception -> 0x0b29 }
            r5 = r6
            r4 = r37
        L_0x034e:
            if (r48 == 0) goto L_0x0362
            if (r36 == 0) goto L_0x0362
            org.telegram.messenger.MediaController r6 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0b29 }
            boolean r6 = r6.isRecordingAudio()     // Catch:{ Exception -> 0x0b29 }
            if (r6 != 0) goto L_0x0362
            r6 = 1
            if (r5 != r6) goto L_0x0360
            goto L_0x0362
        L_0x0360:
            r6 = 0
            goto L_0x0363
        L_0x0362:
            r6 = 1
        L_0x0363:
            java.lang.String r7 = "custom_"
            if (r6 != 0) goto L_0x041d
            int r11 = (r32 > r34 ? 1 : (r32 == r34 ? 0 : -1))
            if (r11 != 0) goto L_0x041d
            if (r13 == 0) goto L_0x041d
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r11.<init>()     // Catch:{ Exception -> 0x0b29 }
            r11.append(r7)     // Catch:{ Exception -> 0x0b29 }
            r8 = r32
            r11.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0b29 }
            r12 = r23
            r14 = 0
            boolean r11 = r12.getBoolean(r11, r14)     // Catch:{ Exception -> 0x0b29 }
            if (r11 == 0) goto L_0x03b7
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r11.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r14 = "smart_max_count_"
            r11.append(r14)     // Catch:{ Exception -> 0x0b29 }
            r11.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0b29 }
            r14 = 2
            int r11 = r12.getInt(r11, r14)     // Catch:{ Exception -> 0x0b29 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r14.<init>()     // Catch:{ Exception -> 0x0b29 }
            r32 = r6
            java.lang.String r6 = "smart_delay_"
            r14.append(r6)     // Catch:{ Exception -> 0x0b29 }
            r14.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r6 = r14.toString()     // Catch:{ Exception -> 0x0b29 }
            r14 = 180(0xb4, float:2.52E-43)
            int r14 = r12.getInt(r6, r14)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x03bc
        L_0x03b7:
            r32 = r6
            r14 = 180(0xb4, float:2.52E-43)
            r11 = 2
        L_0x03bc:
            if (r11 == 0) goto L_0x0419
            androidx.collection.LongSparseArray<android.graphics.Point> r6 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0b29 }
            java.lang.Object r6 = r6.get(r8)     // Catch:{ Exception -> 0x0b29 }
            android.graphics.Point r6 = (android.graphics.Point) r6     // Catch:{ Exception -> 0x0b29 }
            if (r6 != 0) goto L_0x03e2
            android.graphics.Point r6 = new android.graphics.Point     // Catch:{ Exception -> 0x0b29 }
            long r36 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0b29 }
            r23 = r10
            r33 = 1000(0x3e8, double:4.94E-321)
            long r10 = r36 / r33
            int r11 = (int) r10     // Catch:{ Exception -> 0x0b29 }
            r10 = 1
            r6.<init>(r10, r11)     // Catch:{ Exception -> 0x0b29 }
            androidx.collection.LongSparseArray<android.graphics.Point> r10 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0b29 }
            r10.put(r8, r6)     // Catch:{ Exception -> 0x0b29 }
            r40 = r3
            r14 = r4
            goto L_0x0428
        L_0x03e2:
            r23 = r10
            int r10 = r6.y     // Catch:{ Exception -> 0x0b29 }
            int r10 = r10 + r14
            r40 = r3
            r14 = r4
            long r3 = (long) r10     // Catch:{ Exception -> 0x0b29 }
            long r36 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0b29 }
            r33 = 1000(0x3e8, double:4.94E-321)
            long r36 = r36 / r33
            int r10 = (r3 > r36 ? 1 : (r3 == r36 ? 0 : -1))
            if (r10 >= 0) goto L_0x0403
            long r3 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0b29 }
            long r3 = r3 / r33
            int r4 = (int) r3     // Catch:{ Exception -> 0x0b29 }
            r3 = 1
            r6.set(r3, r4)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0428
        L_0x0403:
            int r3 = r6.x     // Catch:{ Exception -> 0x0b29 }
            if (r3 >= r11) goto L_0x0416
            r4 = 1
            int r3 = r3 + r4
            long r10 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0b29 }
            r33 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 / r33
            int r4 = (int) r10     // Catch:{ Exception -> 0x0b29 }
            r6.set(r3, r4)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0428
        L_0x0416:
            r32 = 1
            goto L_0x0428
        L_0x0419:
            r40 = r3
            r14 = r4
            goto L_0x0426
        L_0x041d:
            r40 = r3
            r14 = r4
            r12 = r23
            r8 = r32
            r32 = r6
        L_0x0426:
            r23 = r10
        L_0x0428:
            if (r32 != 0) goto L_0x0444
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r3.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = "sound_enabled_"
            r3.append(r4)     // Catch:{ Exception -> 0x0b29 }
            r3.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0b29 }
            r4 = 1
            boolean r3 = r12.getBoolean(r3, r4)     // Catch:{ Exception -> 0x0b29 }
            if (r3 != 0) goto L_0x0444
            r32 = 1
        L_0x0444:
            android.net.Uri r3 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = r3.getPath()     // Catch:{ Exception -> 0x0b29 }
            boolean r4 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0b29 }
            if (r4 != 0) goto L_0x0451
            r33 = 1
            goto L_0x0453
        L_0x0451:
            r33 = 0
        L_0x0453:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r4.<init>()     // Catch:{ Exception -> 0x0b29 }
            r4.append(r7)     // Catch:{ Exception -> 0x0b29 }
            r4.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b29 }
            r6 = 0
            boolean r4 = r12.getBoolean(r4, r6)     // Catch:{ Exception -> 0x0b29 }
            r6 = 3
            if (r4 == 0) goto L_0x0510
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r4.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r7 = "vibrate_"
            r4.append(r7)     // Catch:{ Exception -> 0x0b29 }
            r4.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b29 }
            r7 = 0
            int r4 = r12.getInt(r4, r7)     // Catch:{ Exception -> 0x0b29 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r7.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r10 = "priority_"
            r7.append(r10)     // Catch:{ Exception -> 0x0b29 }
            r7.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0b29 }
            int r7 = r12.getInt(r7, r6)     // Catch:{ Exception -> 0x0b29 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r10.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r11 = "sound_document_id_"
            r10.append(r11)     // Catch:{ Exception -> 0x0b29 }
            r10.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0b29 }
            r36 = r7
            r6 = 0
            long r10 = r12.getLong(r10, r6)     // Catch:{ Exception -> 0x0b29 }
            int r38 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r38 == 0) goto L_0x04be
            org.telegram.messenger.MediaDataController r6 = r47.getMediaDataController()     // Catch:{ Exception -> 0x0b29 }
            org.telegram.messenger.ringtone.RingtoneDataStore r6 = r6.ringtoneDataStore     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r6 = r6.getSoundPath(r10)     // Catch:{ Exception -> 0x0b29 }
            r7 = 1
            goto L_0x04d5
        L_0x04be:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r6.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r7 = "sound_path_"
            r6.append(r7)     // Catch:{ Exception -> 0x0b29 }
            r6.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0b29 }
            r7 = 0
            java.lang.String r6 = r12.getString(r6, r7)     // Catch:{ Exception -> 0x0b29 }
            r7 = 0
        L_0x04d5:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r10.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r11 = "color_"
            r10.append(r11)     // Catch:{ Exception -> 0x0b29 }
            r10.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0b29 }
            boolean r10 = r12.contains(r10)     // Catch:{ Exception -> 0x0b29 }
            if (r10 == 0) goto L_0x0507
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r10.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r11 = "color_"
            r10.append(r11)     // Catch:{ Exception -> 0x0b29 }
            r10.append(r8)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0b29 }
            r11 = 0
            int r10 = r12.getInt(r10, r11)     // Catch:{ Exception -> 0x0b29 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0508
        L_0x0507:
            r10 = 0
        L_0x0508:
            r11 = r4
            r46 = r36
            r36 = r7
            r7 = r46
            goto L_0x0516
        L_0x0510:
            r6 = 0
            r7 = 3
            r10 = 0
            r11 = 0
            r36 = 0
        L_0x0516:
            r38 = r5
            r4 = 0
            int r18 = (r30 > r4 ? 1 : (r30 == r4 ? 0 : -1))
            if (r18 == 0) goto L_0x05a1
            if (r29 == 0) goto L_0x0560
            r29 = r11
            java.lang.String r11 = "ChannelSoundDocId"
            r41 = r1
            r42 = r2
            long r1 = r12.getLong(r11, r4)     // Catch:{ Exception -> 0x0b29 }
            int r11 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x053c
            org.telegram.messenger.MediaDataController r4 = r47.getMediaDataController()     // Catch:{ Exception -> 0x0b29 }
            org.telegram.messenger.ringtone.RingtoneDataStore r4 = r4.ringtoneDataStore     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r1 = r4.getSoundPath(r1)     // Catch:{ Exception -> 0x0b29 }
            r2 = 1
            goto L_0x0543
        L_0x053c:
            java.lang.String r1 = "ChannelSoundPath"
            java.lang.String r1 = r12.getString(r1, r3)     // Catch:{ Exception -> 0x0b29 }
            r2 = 0
        L_0x0543:
            java.lang.String r4 = "vibrate_channel"
            r5 = 0
            int r4 = r12.getInt(r4, r5)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r5 = "priority_channel"
            r11 = 1
            int r5 = r12.getInt(r5, r11)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r11 = "ChannelLed"
            r43 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r12.getInt(r11, r1)     // Catch:{ Exception -> 0x0b29 }
            r39 = 2
            goto L_0x05e4
        L_0x0560:
            r41 = r1
            r42 = r2
            r29 = r11
            java.lang.String r1 = "GroupSoundDocId"
            r4 = 0
            long r1 = r12.getLong(r1, r4)     // Catch:{ Exception -> 0x0b29 }
            int r11 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x057e
            org.telegram.messenger.MediaDataController r4 = r47.getMediaDataController()     // Catch:{ Exception -> 0x0b29 }
            org.telegram.messenger.ringtone.RingtoneDataStore r4 = r4.ringtoneDataStore     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r1 = r4.getSoundPath(r1)     // Catch:{ Exception -> 0x0b29 }
            r2 = 1
            goto L_0x0585
        L_0x057e:
            java.lang.String r1 = "GroupSoundPath"
            java.lang.String r1 = r12.getString(r1, r3)     // Catch:{ Exception -> 0x0b29 }
            r2 = 0
        L_0x0585:
            java.lang.String r4 = "vibrate_group"
            r5 = 0
            int r4 = r12.getInt(r4, r5)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r5 = "priority_group"
            r11 = 1
            int r5 = r12.getInt(r5, r11)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r11 = "GroupLed"
            r43 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r12.getInt(r11, r1)     // Catch:{ Exception -> 0x0b29 }
            r39 = 0
            goto L_0x05e4
        L_0x05a1:
            r41 = r1
            r42 = r2
            r1 = r4
            r29 = r11
            int r4 = (r21 > r1 ? 1 : (r21 == r1 ? 0 : -1))
            if (r4 == 0) goto L_0x05ee
            java.lang.String r4 = "GlobalSoundDocId"
            long r4 = r12.getLong(r4, r1)     // Catch:{ Exception -> 0x0b29 }
            int r11 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r11 == 0) goto L_0x05c2
            org.telegram.messenger.MediaDataController r1 = r47.getMediaDataController()     // Catch:{ Exception -> 0x0b29 }
            org.telegram.messenger.ringtone.RingtoneDataStore r1 = r1.ringtoneDataStore     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r1 = r1.getSoundPath(r4)     // Catch:{ Exception -> 0x0b29 }
            r2 = 1
            goto L_0x05c9
        L_0x05c2:
            java.lang.String r1 = "GlobalSoundPath"
            java.lang.String r1 = r12.getString(r1, r3)     // Catch:{ Exception -> 0x0b29 }
            r2 = 0
        L_0x05c9:
            java.lang.String r4 = "vibrate_messages"
            r5 = 0
            int r4 = r12.getInt(r4, r5)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r5 = "priority_messages"
            r11 = 1
            int r5 = r12.getInt(r5, r11)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r11 = "MessagesLed"
            r43 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r12.getInt(r11, r1)     // Catch:{ Exception -> 0x0b29 }
            r39 = 1
        L_0x05e4:
            r46 = r4
            r4 = r1
            r1 = r43
            r43 = r2
            r2 = r46
            goto L_0x05fb
        L_0x05ee:
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r1 = 0
            r2 = 0
            r4 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r5 = 0
            r39 = 1
            r43 = 0
        L_0x05fb:
            r11 = 4
            if (r2 != r11) goto L_0x0602
            r2 = 0
            r44 = 1
            goto L_0x0604
        L_0x0602:
            r44 = 0
        L_0x0604:
            boolean r45 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x0b29 }
            if (r45 != 0) goto L_0x0613
            boolean r45 = android.text.TextUtils.equals(r1, r6)     // Catch:{ Exception -> 0x0b29 }
            if (r45 != 0) goto L_0x0613
            r1 = r6
            r6 = 0
            goto L_0x0616
        L_0x0613:
            r36 = r43
            r6 = 1
        L_0x0616:
            r11 = 3
            if (r7 == r11) goto L_0x061f
            r11 = r29
            if (r5 == r7) goto L_0x0621
            r6 = 0
            goto L_0x0622
        L_0x061f:
            r11 = r29
        L_0x0621:
            r7 = r5
        L_0x0622:
            if (r10 == 0) goto L_0x062f
            int r5 = r10.intValue()     // Catch:{ Exception -> 0x0b29 }
            if (r5 == r4) goto L_0x062f
            int r4 = r10.intValue()     // Catch:{ Exception -> 0x0b29 }
            r6 = 0
        L_0x062f:
            if (r11 == 0) goto L_0x0639
            r5 = 4
            if (r11 == r5) goto L_0x0639
            if (r11 == r2) goto L_0x0639
            r2 = r11
            r11 = 0
            goto L_0x063a
        L_0x0639:
            r11 = r6
        L_0x063a:
            if (r33 == 0) goto L_0x065e
            java.lang.String r5 = "EnableInAppSounds"
            r6 = 1
            boolean r5 = r12.getBoolean(r5, r6)     // Catch:{ Exception -> 0x0b29 }
            if (r5 != 0) goto L_0x0646
            r1 = 0
        L_0x0646:
            java.lang.String r5 = "EnableInAppVibrate"
            boolean r5 = r12.getBoolean(r5, r6)     // Catch:{ Exception -> 0x0b29 }
            if (r5 != 0) goto L_0x064f
            r2 = 2
        L_0x064f:
            java.lang.String r5 = "EnableInAppPriority"
            r6 = 0
            boolean r5 = r12.getBoolean(r5, r6)     // Catch:{ Exception -> 0x0b29 }
            if (r5 != 0) goto L_0x065a
            r7 = 0
            goto L_0x065e
        L_0x065a:
            r5 = 2
            if (r7 != r5) goto L_0x065e
            r7 = 1
        L_0x065e:
            if (r44 == 0) goto L_0x0675
            r5 = 2
            if (r2 == r5) goto L_0x0675
            android.media.AudioManager r5 = audioManager     // Catch:{ Exception -> 0x0670 }
            int r5 = r5.getRingerMode()     // Catch:{ Exception -> 0x0670 }
            if (r5 == 0) goto L_0x0675
            r6 = 1
            if (r5 == r6) goto L_0x0675
            r2 = 2
            goto L_0x0675
        L_0x0670:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x0b29 }
        L_0x0675:
            if (r32 == 0) goto L_0x067c
            r1 = 0
            r2 = 0
            r7 = 0
            r10 = 0
            goto L_0x067d
        L_0x067c:
            r10 = r4
        L_0x067d:
            android.content.Intent r4 = new android.content.Intent     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r6 = org.telegram.ui.LaunchActivity.class
            r4.<init>(r5, r6)     // Catch:{ Exception -> 0x0b29 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r5.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r6 = "com.tmessages.openchat"
            r5.append(r6)     // Catch:{ Exception -> 0x0b29 }
            r44 = r10
            r43 = r11
            double r10 = java.lang.Math.random()     // Catch:{ Exception -> 0x0b29 }
            r5.append(r10)     // Catch:{ Exception -> 0x0b29 }
            r6 = 2147483647(0x7fffffff, float:NaN)
            r5.append(r6)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0b29 }
            r4.setAction(r5)     // Catch:{ Exception -> 0x0b29 }
            r5 = 67108864(0x4000000, float:1.5046328E-36)
            r4.setFlags(r5)     // Catch:{ Exception -> 0x0b29 }
            boolean r5 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)     // Catch:{ Exception -> 0x0b29 }
            if (r5 != 0) goto L_0x0725
            androidx.collection.LongSparseArray<java.lang.Integer> r5 = r15.pushDialogs     // Catch:{ Exception -> 0x0b29 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0b29 }
            r6 = 1
            if (r5 != r6) goto L_0x06d5
            r5 = 0
            int r10 = (r30 > r5 ? 1 : (r30 == r5 ? 0 : -1))
            if (r10 == 0) goto L_0x06ca
            java.lang.String r10 = "chatId"
            r5 = r30
            r4.putExtra(r10, r5)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x06d5
        L_0x06ca:
            int r10 = (r21 > r5 ? 1 : (r21 == r5 ? 0 : -1))
            if (r10 == 0) goto L_0x06d5
            java.lang.String r5 = "userId"
            r10 = r21
            r4.putExtra(r5, r10)     // Catch:{ Exception -> 0x0b29 }
        L_0x06d5:
            boolean r5 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b29 }
            if (r5 != 0) goto L_0x0722
            boolean r5 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b29 }
            if (r5 == 0) goto L_0x06e0
            goto L_0x0722
        L_0x06e0:
            androidx.collection.LongSparseArray<java.lang.Integer> r5 = r15.pushDialogs     // Catch:{ Exception -> 0x0b29 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0b29 }
            r6 = 1
            if (r5 != r6) goto L_0x0722
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b29 }
            r6 = 28
            if (r5 >= r6) goto L_0x0722
            if (r13 == 0) goto L_0x0709
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r13.photo     // Catch:{ Exception -> 0x0b29 }
            if (r5 == 0) goto L_0x0722
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ Exception -> 0x0b29 }
            if (r5 == 0) goto L_0x0722
            long r10 = r5.volume_id     // Catch:{ Exception -> 0x0b29 }
            r18 = 0
            int r6 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1))
            if (r6 == 0) goto L_0x0722
            int r6 = r5.local_id     // Catch:{ Exception -> 0x0b29 }
            if (r6 == 0) goto L_0x0722
            r6 = r5
            r5 = r28
            goto L_0x0740
        L_0x0709:
            if (r28 == 0) goto L_0x0722
            r5 = r28
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r5.photo     // Catch:{ Exception -> 0x0b29 }
            if (r6 == 0) goto L_0x073f
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small     // Catch:{ Exception -> 0x0b29 }
            if (r6 == 0) goto L_0x073f
            long r10 = r6.volume_id     // Catch:{ Exception -> 0x0b29 }
            r18 = 0
            int r21 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1))
            if (r21 == 0) goto L_0x073f
            int r10 = r6.local_id     // Catch:{ Exception -> 0x0b29 }
            if (r10 == 0) goto L_0x073f
            goto L_0x0740
        L_0x0722:
            r5 = r28
            goto L_0x073f
        L_0x0725:
            r5 = r28
            androidx.collection.LongSparseArray<java.lang.Integer> r6 = r15.pushDialogs     // Catch:{ Exception -> 0x0b29 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0b29 }
            r10 = 1
            if (r6 != r10) goto L_0x073f
            long r10 = globalSecretChatId     // Catch:{ Exception -> 0x0b29 }
            int r6 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r6 == 0) goto L_0x073f
            java.lang.String r6 = "encId"
            int r10 = org.telegram.messenger.DialogObject.getEncryptedChatId(r8)     // Catch:{ Exception -> 0x0b29 }
            r4.putExtra(r6, r10)     // Catch:{ Exception -> 0x0b29 }
        L_0x073f:
            r6 = 0
        L_0x0740:
            int r10 = r15.currentAccount     // Catch:{ Exception -> 0x0b29 }
            r11 = r17
            r4.putExtra(r11, r10)     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            r21 = r8
            r8 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            android.app.PendingIntent r4 = android.app.PendingIntent.getActivity(r10, r9, r4, r8)     // Catch:{ Exception -> 0x0b29 }
            r9 = r41
            r8 = r42
            androidx.core.app.NotificationCompat$Builder r9 = r8.setContentTitle(r9)     // Catch:{ Exception -> 0x0b29 }
            int r10 = org.telegram.messenger.R.drawable.notification     // Catch:{ Exception -> 0x0b29 }
            androidx.core.app.NotificationCompat$Builder r9 = r9.setSmallIcon(r10)     // Catch:{ Exception -> 0x0b29 }
            r10 = 1
            androidx.core.app.NotificationCompat$Builder r9 = r9.setAutoCancel(r10)     // Catch:{ Exception -> 0x0b29 }
            int r10 = r15.total_unread_count     // Catch:{ Exception -> 0x0b29 }
            androidx.core.app.NotificationCompat$Builder r9 = r9.setNumber(r10)     // Catch:{ Exception -> 0x0b29 }
            androidx.core.app.NotificationCompat$Builder r4 = r9.setContentIntent(r4)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r9 = r15.notificationGroup     // Catch:{ Exception -> 0x0b29 }
            androidx.core.app.NotificationCompat$Builder r4 = r4.setGroup(r9)     // Catch:{ Exception -> 0x0b29 }
            r9 = 1
            androidx.core.app.NotificationCompat$Builder r4 = r4.setGroupSummary(r9)     // Catch:{ Exception -> 0x0b29 }
            androidx.core.app.NotificationCompat$Builder r4 = r4.setShowWhen(r9)     // Catch:{ Exception -> 0x0b29 }
            r9 = r27
            org.telegram.tgnet.TLRPC$Message r10 = r9.messageOwner     // Catch:{ Exception -> 0x0b29 }
            int r10 = r10.date     // Catch:{ Exception -> 0x0b29 }
            r27 = r2
            r17 = r3
            long r2 = (long) r10     // Catch:{ Exception -> 0x0b29 }
            r30 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 * r30
            androidx.core.app.NotificationCompat$Builder r2 = r4.setWhen(r2)     // Catch:{ Exception -> 0x0b29 }
            r3 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r2.setColor(r3)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r2 = "msg"
            r8.setCategory(r2)     // Catch:{ Exception -> 0x0b29 }
            if (r13 != 0) goto L_0x07c0
            if (r5 == 0) goto L_0x07c0
            java.lang.String r2 = r5.phone     // Catch:{ Exception -> 0x0b29 }
            if (r2 == 0) goto L_0x07c0
            int r2 = r2.length()     // Catch:{ Exception -> 0x0b29 }
            if (r2 <= 0) goto L_0x07c0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r2.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = "tel:+"
            r2.append(r3)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = r5.phone     // Catch:{ Exception -> 0x0b29 }
            r2.append(r3)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b29 }
            r8.addPerson(r2)     // Catch:{ Exception -> 0x0b29 }
        L_0x07c0:
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r4 = org.telegram.messenger.NotificationDismissReceiver.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = "messageDate"
            org.telegram.tgnet.TLRPC$Message r4 = r9.messageOwner     // Catch:{ Exception -> 0x0b29 }
            int r4 = r4.date     // Catch:{ Exception -> 0x0b29 }
            r2.putExtra(r3, r4)     // Catch:{ Exception -> 0x0b29 }
            int r3 = r15.currentAccount     // Catch:{ Exception -> 0x0b29 }
            r2.putExtra(r11, r3)     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 1
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r3, r5, r2, r4)     // Catch:{ Exception -> 0x0b29 }
            r8.setDeleteIntent(r2)     // Catch:{ Exception -> 0x0b29 }
            if (r6 == 0) goto L_0x0833
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = "50_50"
            r5 = 0
            android.graphics.drawable.BitmapDrawable r2 = r2.getImageFromMemory(r6, r5, r3)     // Catch:{ Exception -> 0x0b29 }
            if (r2 == 0) goto L_0x07fa
            android.graphics.Bitmap r2 = r2.getBitmap()     // Catch:{ Exception -> 0x0b29 }
            r8.setLargeIcon(r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0834
        L_0x07fa:
            org.telegram.messenger.FileLoader r2 = r47.getFileLoader()     // Catch:{ all -> 0x0831 }
            r3 = 1
            java.io.File r2 = r2.getPathToAttach(r6, r3)     // Catch:{ all -> 0x0831 }
            boolean r3 = r2.exists()     // Catch:{ all -> 0x0831 }
            if (r3 == 0) goto L_0x0834
            r3 = 1126170624(0x43200000, float:160.0)
            r6 = 1112014848(0x42480000, float:50.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ all -> 0x0831 }
            float r6 = (float) r6     // Catch:{ all -> 0x0831 }
            float r3 = r3 / r6
            android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0831 }
            r6.<init>()     // Catch:{ all -> 0x0831 }
            r10 = 1065353216(0x3var_, float:1.0)
            int r10 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1))
            if (r10 >= 0) goto L_0x0820
            r3 = 1
            goto L_0x0821
        L_0x0820:
            int r3 = (int) r3     // Catch:{ all -> 0x0831 }
        L_0x0821:
            r6.inSampleSize = r3     // Catch:{ all -> 0x0831 }
            java.lang.String r2 = r2.getAbsolutePath()     // Catch:{ all -> 0x0831 }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2, r6)     // Catch:{ all -> 0x0831 }
            if (r2 == 0) goto L_0x0834
            r8.setLargeIcon(r2)     // Catch:{ all -> 0x0831 }
            goto L_0x0834
        L_0x0831:
            goto L_0x0834
        L_0x0833:
            r5 = 0
        L_0x0834:
            r2 = 5
            r3 = 26
            r6 = r38
            if (r48 == 0) goto L_0x0877
            r10 = 1
            if (r6 != r10) goto L_0x083f
            goto L_0x0877
        L_0x083f:
            if (r7 != 0) goto L_0x084c
            r10 = 0
            r8.setPriority(r10)     // Catch:{ Exception -> 0x0b29 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b29 }
            if (r7 < r3) goto L_0x0882
            r7 = 1
            r10 = 3
            goto L_0x0884
        L_0x084c:
            r10 = 1
            if (r7 == r10) goto L_0x086c
            r10 = 2
            if (r7 != r10) goto L_0x0853
            goto L_0x086c
        L_0x0853:
            r10 = 4
            if (r7 != r10) goto L_0x0861
            r7 = -2
            r8.setPriority(r7)     // Catch:{ Exception -> 0x0b29 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b29 }
            if (r7 < r3) goto L_0x0882
            r7 = 1
            r10 = 1
            goto L_0x0884
        L_0x0861:
            if (r7 != r2) goto L_0x0882
            r7 = -1
            r8.setPriority(r7)     // Catch:{ Exception -> 0x0b29 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b29 }
            if (r7 < r3) goto L_0x0882
            goto L_0x087f
        L_0x086c:
            r7 = 1
            r8.setPriority(r7)     // Catch:{ Exception -> 0x0b29 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b29 }
            if (r7 < r3) goto L_0x0882
            r7 = 1
            r10 = 4
            goto L_0x0884
        L_0x0877:
            r7 = -1
            r8.setPriority(r7)     // Catch:{ Exception -> 0x0b29 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b29 }
            if (r7 < r3) goto L_0x0882
        L_0x087f:
            r7 = 1
            r10 = 2
            goto L_0x0884
        L_0x0882:
            r7 = 1
            r10 = 0
        L_0x0884:
            if (r6 == r7) goto L_0x09e2
            if (r32 != 0) goto L_0x09e2
            if (r33 == 0) goto L_0x0892
            java.lang.String r6 = "EnableInAppPreview"
            boolean r6 = r12.getBoolean(r6, r7)     // Catch:{ Exception -> 0x0b29 }
            if (r6 == 0) goto L_0x08c5
        L_0x0892:
            int r6 = r14.length()     // Catch:{ Exception -> 0x0b29 }
            r7 = 100
            if (r6 <= r7) goto L_0x08c0
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r6.<init>()     // Catch:{ Exception -> 0x0b29 }
            r7 = 100
            r12 = r14
            r13 = 0
            java.lang.String r7 = r12.substring(r13, r7)     // Catch:{ Exception -> 0x0b29 }
            r12 = 32
            r13 = 10
            java.lang.String r7 = r7.replace(r13, r12)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r7 = r7.trim()     // Catch:{ Exception -> 0x0b29 }
            r6.append(r7)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r7 = "..."
            r6.append(r7)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0b29 }
            goto L_0x08c2
        L_0x08c0:
            r12 = r14
            r6 = r12
        L_0x08c2:
            r8.setTicker(r6)     // Catch:{ Exception -> 0x0b29 }
        L_0x08c5:
            if (r1 == 0) goto L_0x097f
            java.lang.String r6 = "NoSound"
            boolean r6 = r1.equals(r6)     // Catch:{ Exception -> 0x0b29 }
            if (r6 != 0) goto L_0x097f
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b29 }
            if (r6 < r3) goto L_0x0918
            java.lang.String r2 = "Default"
            boolean r2 = r1.equals(r2)     // Catch:{ Exception -> 0x0b29 }
            if (r2 != 0) goto L_0x0915
            r3 = r17
            boolean r2 = r1.equals(r3)     // Catch:{ Exception -> 0x0b29 }
            if (r2 == 0) goto L_0x08e4
            goto L_0x0915
        L_0x08e4:
            if (r36 == 0) goto L_0x0910
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b29 }
            r3.<init>()     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r6 = org.telegram.messenger.ApplicationLoader.getApplicationId()     // Catch:{ Exception -> 0x0b29 }
            r3.append(r6)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r6 = ".provider"
            r3.append(r6)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0b29 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0b29 }
            r6.<init>(r1)     // Catch:{ Exception -> 0x0b29 }
            android.net.Uri r14 = androidx.core.content.FileProvider.getUriForFile(r2, r3, r6)     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r2 = "com.android.systemui"
            r3 = 1
            r1.grantUriPermission(r2, r14, r3)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0980
        L_0x0910:
            android.net.Uri r14 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0980
        L_0x0915:
            android.net.Uri r14 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0980
        L_0x0918:
            r3 = r17
            boolean r3 = r1.equals(r3)     // Catch:{ Exception -> 0x0b29 }
            if (r3 == 0) goto L_0x0926
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b29 }
            r8.setSound(r1, r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x097f
        L_0x0926:
            r3 = 24
            if (r6 < r3) goto L_0x0978
            java.lang.String r3 = "file://"
            boolean r3 = r1.startsWith(r3)     // Catch:{ Exception -> 0x0b29 }
            if (r3 == 0) goto L_0x0978
            android.net.Uri r3 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0b29 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r3)     // Catch:{ Exception -> 0x0b29 }
            if (r3 != 0) goto L_0x0978
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0970 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0970 }
            r6.<init>()     // Catch:{ Exception -> 0x0970 }
            java.lang.String r7 = org.telegram.messenger.ApplicationLoader.getApplicationId()     // Catch:{ Exception -> 0x0970 }
            r6.append(r7)     // Catch:{ Exception -> 0x0970 }
            java.lang.String r7 = ".provider"
            r6.append(r7)     // Catch:{ Exception -> 0x0970 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0970 }
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0970 }
            java.lang.String r12 = "file://"
            r13 = r23
            java.lang.String r12 = r1.replace(r12, r13)     // Catch:{ Exception -> 0x0970 }
            r7.<init>(r12)     // Catch:{ Exception -> 0x0970 }
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r3, r6, r7)     // Catch:{ Exception -> 0x0970 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0970 }
            java.lang.String r7 = "com.android.systemui"
            r12 = 1
            r6.grantUriPermission(r7, r3, r12)     // Catch:{ Exception -> 0x0970 }
            r8.setSound(r3, r2)     // Catch:{ Exception -> 0x0970 }
            goto L_0x097f
        L_0x0970:
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0b29 }
            r8.setSound(r1, r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x097f
        L_0x0978:
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0b29 }
            r8.setSound(r1, r2)     // Catch:{ Exception -> 0x0b29 }
        L_0x097f:
            r14 = r5
        L_0x0980:
            if (r44 == 0) goto L_0x098c
            r1 = 1000(0x3e8, float:1.401E-42)
            r2 = 1000(0x3e8, float:1.401E-42)
            r12 = r44
            r8.setLights(r12, r1, r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x098e
        L_0x098c:
            r12 = r44
        L_0x098e:
            r2 = r27
            r1 = 2
            if (r2 != r1) goto L_0x09a1
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0b29 }
            r1 = 0
            r5 = 0
            r2[r1] = r5     // Catch:{ Exception -> 0x0b29 }
            r1 = 1
            r2[r1] = r5     // Catch:{ Exception -> 0x0b29 }
            r8.setVibrate(r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x09e0
        L_0x09a1:
            r1 = 1
            if (r2 != r1) goto L_0x09bc
            r3 = 4
            long[] r2 = new long[r3]     // Catch:{ Exception -> 0x0b29 }
            r3 = 0
            r5 = 0
            r2[r3] = r5     // Catch:{ Exception -> 0x0b29 }
            r17 = 100
            r2[r1] = r17     // Catch:{ Exception -> 0x0b29 }
            r1 = 2
            r2[r1] = r5     // Catch:{ Exception -> 0x0b29 }
            r5 = 100
            r1 = 3
            r2[r1] = r5     // Catch:{ Exception -> 0x0b29 }
            r8.setVibrate(r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x09e0
        L_0x09bc:
            if (r2 == 0) goto L_0x09d9
            r3 = 4
            if (r2 != r3) goto L_0x09c2
            goto L_0x09d9
        L_0x09c2:
            r1 = 3
            if (r2 != r1) goto L_0x09d6
            r1 = 2
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0b29 }
            r1 = 0
            r5 = 0
            r2[r1] = r5     // Catch:{ Exception -> 0x0b29 }
            r1 = 1
            r5 = 1000(0x3e8, double:4.94E-321)
            r2[r1] = r5     // Catch:{ Exception -> 0x0b29 }
            r8.setVibrate(r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x09e0
        L_0x09d6:
            r7 = r5
        L_0x09d7:
            r1 = 1
            goto L_0x09f4
        L_0x09d9:
            r1 = 2
            r8.setDefaults(r1)     // Catch:{ Exception -> 0x0b29 }
            r1 = 0
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0b29 }
        L_0x09e0:
            r7 = r2
            goto L_0x09d7
        L_0x09e2:
            r12 = r44
            r1 = 2
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0b29 }
            r1 = 0
            r6 = 0
            r2[r1] = r6     // Catch:{ Exception -> 0x0b29 }
            r1 = 1
            r2[r1] = r6     // Catch:{ Exception -> 0x0b29 }
            r8.setVibrate(r2)     // Catch:{ Exception -> 0x0b29 }
            r7 = r2
            r14 = r5
        L_0x09f4:
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b29 }
            if (r2 != 0) goto L_0x0ab5
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b29 }
            if (r2 != 0) goto L_0x0ab5
            long r2 = r9.getDialogId()     // Catch:{ Exception -> 0x0b29 }
            r5 = 777000(0xbdb28, double:3.83889E-318)
            int r13 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r13 != 0) goto L_0x0ab5
            org.telegram.tgnet.TLRPC$Message r2 = r9.messageOwner     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0b29 }
            if (r2 == 0) goto L_0x0ab5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows     // Catch:{ Exception -> 0x0b29 }
            int r3 = r2.size()     // Catch:{ Exception -> 0x0b29 }
            r5 = 0
            r6 = 0
        L_0x0a17:
            if (r5 >= r3) goto L_0x0aaf
            java.lang.Object r13 = r2.get(r5)     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r13 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r13     // Catch:{ Exception -> 0x0b29 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r1 = r13.buttons     // Catch:{ Exception -> 0x0b29 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0b29 }
            r4 = 0
        L_0x0a26:
            if (r4 >= r1) goto L_0x0a99
            r48 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r1 = r13.buttons     // Catch:{ Exception -> 0x0b29 }
            java.lang.Object r1 = r1.get(r4)     // Catch:{ Exception -> 0x0b29 }
            org.telegram.tgnet.TLRPC$KeyboardButton r1 = (org.telegram.tgnet.TLRPC$KeyboardButton) r1     // Catch:{ Exception -> 0x0b29 }
            r18 = r2
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0b29 }
            if (r2 == 0) goto L_0x0a7d
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            r19 = r3
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r3 = org.telegram.messenger.NotificationCallbackReceiver.class
            r2.<init>(r6, r3)     // Catch:{ Exception -> 0x0b29 }
            int r3 = r15.currentAccount     // Catch:{ Exception -> 0x0b29 }
            r2.putExtra(r11, r3)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = "did"
            r23 = r13
            r20 = r14
            r13 = r21
            r2.putExtra(r3, r13)     // Catch:{ Exception -> 0x0b29 }
            byte[] r3 = r1.data     // Catch:{ Exception -> 0x0b29 }
            if (r3 == 0) goto L_0x0a5c
            java.lang.String r6 = "data"
            r2.putExtra(r6, r3)     // Catch:{ Exception -> 0x0b29 }
        L_0x0a5c:
            java.lang.String r3 = "mid"
            int r6 = r9.getId()     // Catch:{ Exception -> 0x0b29 }
            r2.putExtra(r3, r6)     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r1 = r1.text     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            int r6 = r15.lastButtonId     // Catch:{ Exception -> 0x0b29 }
            r27 = r9
            int r9 = r6 + 1
            r15.lastButtonId = r9     // Catch:{ Exception -> 0x0b29 }
            r9 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r3, r6, r2, r9)     // Catch:{ Exception -> 0x0b29 }
            r3 = 0
            r8.addAction(r3, r1, r2)     // Catch:{ Exception -> 0x0b29 }
            r6 = 1
            goto L_0x0a88
        L_0x0a7d:
            r19 = r3
            r27 = r9
            r23 = r13
            r20 = r14
            r13 = r21
            r3 = 0
        L_0x0a88:
            int r4 = r4 + 1
            r1 = r48
            r21 = r13
            r2 = r18
            r3 = r19
            r14 = r20
            r13 = r23
            r9 = r27
            goto L_0x0a26
        L_0x0a99:
            r18 = r2
            r19 = r3
            r27 = r9
            r20 = r14
            r13 = r21
            r3 = 0
            int r5 = r5 + 1
            r3 = r19
            r14 = r20
            r1 = 1
            r4 = 134217728(0x8000000, float:3.85186E-34)
            goto L_0x0a17
        L_0x0aaf:
            r20 = r14
            r13 = r21
            r3 = r6
            goto L_0x0aba
        L_0x0ab5:
            r20 = r14
            r13 = r21
            r3 = 0
        L_0x0aba:
            if (r3 != 0) goto L_0x0b0f
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b29 }
            r2 = 24
            if (r1 >= r2) goto L_0x0b0f
            java.lang.String r2 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0b29 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x0b29 }
            if (r2 != 0) goto L_0x0b0f
            boolean r2 = r47.hasMessagesToReply()     // Catch:{ Exception -> 0x0b29 }
            if (r2 == 0) goto L_0x0b0f
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r4 = org.telegram.messenger.PopupReplyReceiver.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0b29 }
            int r3 = r15.currentAccount     // Catch:{ Exception -> 0x0b29 }
            r2.putExtra(r11, r3)     // Catch:{ Exception -> 0x0b29 }
            r3 = 19
            if (r1 > r3) goto L_0x0af9
            int r1 = org.telegram.messenger.R.drawable.ic_ab_reply2     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = "Reply"
            int r4 = org.telegram.messenger.R.string.Reply     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r4, r6, r2, r5)     // Catch:{ Exception -> 0x0b29 }
            r8.addAction(r1, r3, r2)     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0b0f
        L_0x0af9:
            int r1 = org.telegram.messenger.R.drawable.ic_ab_reply     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = "Reply"
            int r4 = org.telegram.messenger.R.string.Reply     // Catch:{ Exception -> 0x0b29 }
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b29 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b29 }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r4, r6, r2, r5)     // Catch:{ Exception -> 0x0b29 }
            r8.addAction(r1, r3, r2)     // Catch:{ Exception -> 0x0b29 }
        L_0x0b0f:
            r1 = r47
            r2 = r8
            r3 = r40
            r4 = r13
            r6 = r25
            r8 = r12
            r9 = r20
            r11 = r43
            r12 = r33
            r13 = r32
            r14 = r39
            r1.showExtraNotifications(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0b29 }
            r47.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0b29 }
            goto L_0x0b2e
        L_0x0b29:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0b2e:
            return
        L_0x0b2f:
            r47.dismissNotification()
            return
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

    /* access modifiers changed from: private */
    public void resetNotificationSound(NotificationCompat.Builder builder, long j, String str, long[] jArr, int i, Uri uri, int i2, boolean z, boolean z2, boolean z3, int i3) {
        long j2 = j;
        int i4 = i3;
        Uri uri2 = Settings.System.DEFAULT_RINGTONE_URI;
        if (uri2 != null && uri != null && !TextUtils.equals(uri2.toString(), uri.toString())) {
            SharedPreferences.Editor edit = getAccountInstance().getNotificationsSettings().edit();
            String uri3 = uri2.toString();
            String string = LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone);
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
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0b74: MOVE  (r1v41 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>) = 
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
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0344  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x035e  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0369  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x03c1  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x03cc  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x03f2  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x03fd A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0452  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0463  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x04a5  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x04c1  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x04d5  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x04f3  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x04fd A[SYNTHETIC, Splitter:B:200:0x04fd] */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x055d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0586 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x05b5  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x05ee  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x0629  */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x06a6  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x06df  */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x06f5  */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x06fe  */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x0792  */
    /* JADX WARNING: Removed duplicated region for block: B:372:0x0866  */
    /* JADX WARNING: Removed duplicated region for block: B:379:0x089d  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x08be  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x0902  */
    /* JADX WARNING: Removed duplicated region for block: B:405:0x0977  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0981  */
    /* JADX WARNING: Removed duplicated region for block: B:412:0x09ac  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x0a04  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0a3b  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a60  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a82  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0b31  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0b3c  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0b41  */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0b51  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0b57  */
    /* JADX WARNING: Removed duplicated region for block: B:441:0x0b5f  */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0b64  */
    /* JADX WARNING: Removed duplicated region for block: B:452:0x0b7f  */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0CLASSNAME A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:474:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0206  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x020f  */
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
            androidx.collection.LongSparseArray r8 = new androidx.collection.LongSparseArray
            r8.<init>()
            r0 = 0
        L_0x00a7:
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.wearNotificationsIds
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x00c3
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.wearNotificationsIds
            long r1 = r1.keyAt(r0)
            androidx.collection.LongSparseArray<java.lang.Integer> r3 = r15.wearNotificationsIds
            java.lang.Object r3 = r3.valueAt(r0)
            java.lang.Integer r3 = (java.lang.Integer) r3
            r8.put(r1, r3)
            int r0 = r0 + 1
            goto L_0x00a7
        L_0x00c3:
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r0.clear()
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 27
            r4 = 1
            if (r0 <= r6) goto L_0x00dd
            int r1 = r11.size()
            if (r1 <= r4) goto L_0x00db
            goto L_0x00dd
        L_0x00db:
            r5 = 0
            goto L_0x00de
        L_0x00dd:
            r5 = 1
        L_0x00de:
            if (r5 == 0) goto L_0x00e5
            if (r0 < r13) goto L_0x00e5
            checkOtherNotificationsChannel()
        L_0x00e5:
            org.telegram.messenger.UserConfig r0 = r69.getUserConfig()
            long r2 = r0.getClientUserId()
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r0 != 0) goto L_0x00fb
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 == 0) goto L_0x00f8
            goto L_0x00fb
        L_0x00f8:
            r19 = 0
            goto L_0x00fd
        L_0x00fb:
            r19 = 1
        L_0x00fd:
            r1 = 7
            androidx.collection.LongSparseArray r13 = new androidx.collection.LongSparseArray
            r13.<init>()
            int r6 = r11.size()
            r4 = 0
        L_0x0108:
            if (r4 >= r6) goto L_0x0ca6
            int r0 = r7.size()
            if (r0 < r1) goto L_0x0112
            goto L_0x0ca6
        L_0x0112:
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
            r24 = r4
            r4 = 32
            if (r0 != 0) goto L_0x014b
            int r0 = (int) r6
            r26 = r10
            r25 = r11
            long r10 = r6 >> r4
            int r11 = (int) r10
            int r0 = r0 + r11
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            goto L_0x0152
        L_0x014b:
            r26 = r10
            r25 = r11
            r8.remove(r6)
        L_0x0152:
            r11 = r0
            r10 = 0
            java.lang.Object r0 = r1.get(r10)
            r10 = r0
            org.telegram.messenger.MessageObject r10 = (org.telegram.messenger.MessageObject) r10
            r28 = r8
            r0 = 0
            r4 = 0
        L_0x015f:
            int r8 = r1.size()
            if (r0 >= r8) goto L_0x017e
            java.lang.Object r8 = r1.get(r0)
            org.telegram.messenger.MessageObject r8 = (org.telegram.messenger.MessageObject) r8
            org.telegram.tgnet.TLRPC$Message r8 = r8.messageOwner
            int r8 = r8.date
            if (r4 >= r8) goto L_0x017b
            java.lang.Object r4 = r1.get(r0)
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            int r4 = r4.date
        L_0x017b:
            int r0 = r0 + 1
            goto L_0x015f
        L_0x017e:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            r29 = 777000(0xbdb28, double:3.83889E-318)
            r31 = 0
            if (r0 != 0) goto L_0x02ad
            int r0 = (r6 > r29 ? 1 : (r6 == r29 ? 0 : -1))
            if (r0 == 0) goto L_0x018f
            r0 = 1
            goto L_0x0190
        L_0x018f:
            r0 = 0
        L_0x0190:
            boolean r33 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r33 == 0) goto L_0x0225
            org.telegram.messenger.MessagesController r8 = r69.getMessagesController()
            r34 = r0
            java.lang.Long r0 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r0 = r8.getUser(r0)
            if (r0 != 0) goto L_0x01d9
            boolean r8 = r10.isFcmMessage()
            if (r8 == 0) goto L_0x01b3
            java.lang.String r8 = r10.localName
            r37 = r4
            r36 = r5
            goto L_0x01ff
        L_0x01b3:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x01cb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found user to show dialog notification "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x01cb:
            r29 = r2
            r23 = r5
            r65 = r12
            r67 = r13
            r14 = r20
            r20 = r28
            goto L_0x02ef
        L_0x01d9:
            java.lang.String r8 = org.telegram.messenger.UserObject.getUserName(r0)
            r35 = r8
            org.telegram.tgnet.TLRPC$UserProfilePhoto r8 = r0.photo
            if (r8 == 0) goto L_0x01f9
            org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_small
            if (r8 == 0) goto L_0x01f9
            r37 = r4
            r36 = r5
            long r4 = r8.volume_id
            int r38 = (r4 > r31 ? 1 : (r4 == r31 ? 0 : -1))
            if (r38 == 0) goto L_0x01fd
            int r4 = r8.local_id
            if (r4 == 0) goto L_0x01fd
            r4 = r8
            r8 = r35
            goto L_0x0200
        L_0x01f9:
            r37 = r4
            r36 = r5
        L_0x01fd:
            r8 = r35
        L_0x01ff:
            r4 = 0
        L_0x0200:
            boolean r5 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r5 == 0) goto L_0x020f
            int r5 = org.telegram.messenger.R.string.RepliesTitle
            java.lang.String r8 = "RepliesTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r5)
            goto L_0x021b
        L_0x020f:
            int r5 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r5 != 0) goto L_0x021b
            int r5 = org.telegram.messenger.R.string.MessageScheduledReminderNotification
            java.lang.String r8 = "MessageScheduledReminderNotification"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r5)
        L_0x021b:
            r38 = r1
            r39 = r4
            r1 = 0
            r5 = 0
            r35 = 0
            goto L_0x0337
        L_0x0225:
            r34 = r0
            r37 = r4
            r36 = r5
            org.telegram.messenger.MessagesController r0 = r69.getMessagesController()
            long r4 = -r6
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
            if (r0 != 0) goto L_0x026e
            boolean r4 = r10.isFcmMessage()
            if (r4 == 0) goto L_0x0254
            boolean r4 = r10.isSupergroup()
            java.lang.String r8 = r10.localName
            boolean r5 = r10.localChannel
            r38 = r1
            r35 = r4
            r1 = r5
            r39 = 0
            r5 = r0
            r0 = r8
            r8 = 0
            goto L_0x033c
        L_0x0254:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02e3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found chat to show dialog notification "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02e3
        L_0x026e:
            boolean r4 = r0.megagroup
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r5 == 0) goto L_0x027c
            boolean r5 = r0.megagroup
            if (r5 != 0) goto L_0x027c
            r5 = 1
            goto L_0x027d
        L_0x027c:
            r5 = 0
        L_0x027d:
            java.lang.String r8 = r0.title
            r35 = r4
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r0.photo
            if (r4 == 0) goto L_0x02a0
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.photo_small
            if (r4 == 0) goto L_0x02a0
            r39 = r0
            r38 = r1
            long r0 = r4.volume_id
            int r40 = (r0 > r31 ? 1 : (r0 == r31 ? 0 : -1))
            if (r40 == 0) goto L_0x02a4
            int r0 = r4.local_id
            if (r0 == 0) goto L_0x02a4
            r1 = r5
            r0 = r8
            r5 = r39
            r8 = 0
            r39 = r4
            goto L_0x033c
        L_0x02a0:
            r39 = r0
            r38 = r1
        L_0x02a4:
            r1 = r5
            r0 = r8
            r5 = r39
            r8 = 0
            r39 = 0
            goto L_0x033c
        L_0x02ad:
            r38 = r1
            r37 = r4
            r36 = r5
            long r0 = globalSecretChatId
            int r4 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r4 == 0) goto L_0x0326
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            org.telegram.messenger.MessagesController r1 = r69.getMessagesController()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r4)
            if (r1 != 0) goto L_0x02fb
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x02e3
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "not found secret chat to show dialog notification "
            r1.append(r4)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x02e3:
            r29 = r2
            r65 = r12
            r67 = r13
            r14 = r20
            r20 = r28
            r23 = r36
        L_0x02ef:
            r22 = 7
            r27 = 1
            r28 = 0
            r31 = 27
            r32 = 26
            goto L_0x0c8d
        L_0x02fb:
            org.telegram.messenger.MessagesController r0 = r69.getMessagesController()
            long r4 = r1.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r4)
            if (r0 != 0) goto L_0x0327
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02e3
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "not found secret chat user to show dialog notification "
            r0.append(r4)
            long r4 = r1.user_id
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02e3
        L_0x0326:
            r0 = 0
        L_0x0327:
            int r1 = org.telegram.messenger.R.string.SecretChatName
            java.lang.String r4 = "SecretChatName"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r1 = 0
            r5 = 0
            r34 = 0
            r35 = 0
            r39 = 0
        L_0x0337:
            r68 = r8
            r8 = r0
            r0 = r68
        L_0x033c:
            java.lang.String r4 = "NotificationHiddenChatName"
            r40 = r12
            java.lang.String r12 = "NotificationHiddenName"
            if (r19 == 0) goto L_0x035e
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r0 == 0) goto L_0x0351
            int r0 = org.telegram.messenger.R.string.NotificationHiddenChatName
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            goto L_0x0357
        L_0x0351:
            int r0 = org.telegram.messenger.R.string.NotificationHiddenName
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
        L_0x0357:
            r34 = r8
            r39 = 0
            r8 = r0
            r0 = 0
            goto L_0x0367
        L_0x035e:
            r68 = r8
            r8 = r0
            r0 = r39
            r39 = r34
            r34 = r68
        L_0x0367:
            if (r0 == 0) goto L_0x03c1
            org.telegram.messenger.FileLoader r14 = r69.getFileLoader()
            r41 = r12
            r12 = 1
            java.io.File r14 = r14.getPathToAttach(r0, r12)
            int r12 = android.os.Build.VERSION.SDK_INT
            r42 = r4
            r4 = 28
            if (r12 >= r4) goto L_0x03bc
            org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r12 = "50_50"
            r43 = r10
            r10 = 0
            android.graphics.drawable.BitmapDrawable r0 = r4.getImageFromMemory(r0, r10, r12)
            if (r0 == 0) goto L_0x0391
            android.graphics.Bitmap r0 = r0.getBitmap()
        L_0x038f:
            r4 = r0
            goto L_0x03ca
        L_0x0391:
            boolean r0 = r14.exists()     // Catch:{ all -> 0x03bf }
            if (r0 == 0) goto L_0x03ba
            r0 = 1126170624(0x43200000, float:160.0)
            r4 = 1112014848(0x42480000, float:50.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ all -> 0x03bf }
            float r4 = (float) r4     // Catch:{ all -> 0x03bf }
            float r0 = r0 / r4
            android.graphics.BitmapFactory$Options r4 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x03bf }
            r4.<init>()     // Catch:{ all -> 0x03bf }
            r12 = 1065353216(0x3var_, float:1.0)
            int r12 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
            if (r12 >= 0) goto L_0x03ae
            r0 = 1
            goto L_0x03af
        L_0x03ae:
            int r0 = (int) r0     // Catch:{ all -> 0x03bf }
        L_0x03af:
            r4.inSampleSize = r0     // Catch:{ all -> 0x03bf }
            java.lang.String r0 = r14.getAbsolutePath()     // Catch:{ all -> 0x03bf }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r4)     // Catch:{ all -> 0x03bf }
            goto L_0x038f
        L_0x03ba:
            r0 = r10
            goto L_0x038f
        L_0x03bc:
            r43 = r10
            r10 = 0
        L_0x03bf:
            r4 = r10
            goto L_0x03ca
        L_0x03c1:
            r42 = r4
            r43 = r10
            r41 = r12
            r10 = 0
            r4 = r10
            r14 = r4
        L_0x03ca:
            if (r5 == 0) goto L_0x03f2
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r8)
            if (r14 == 0) goto L_0x03e6
            boolean r12 = r14.exists()
            if (r12 == 0) goto L_0x03e6
            int r12 = android.os.Build.VERSION.SDK_INT
            r10 = 28
            if (r12 < r10) goto L_0x03e6
            r15.loadRoundAvatar(r14, r0)
        L_0x03e6:
            r12 = r11
            long r10 = r5.id
            long r10 = -r10
            androidx.core.app.Person r0 = r0.build()
            r13.put(r10, r0)
            goto L_0x03f3
        L_0x03f2:
            r12 = r11
        L_0x03f3:
            java.lang.String r10 = "max_id"
            java.lang.String r11 = "dialog_id"
            r44 = r14
            java.lang.String r14 = "currentAccount"
            if (r1 == 0) goto L_0x03ff
            if (r35 == 0) goto L_0x0492
        L_0x03ff:
            if (r39 == 0) goto L_0x0492
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0492
            int r0 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x0492
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r0 != 0) goto L_0x0492
            android.content.Intent r0 = new android.content.Intent
            r35 = r5
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            r39 = r4
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r4 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r5, r4)
            r0.putExtra(r11, r6)
            r0.putExtra(r10, r9)
            int r4 = r15.currentAccount
            r0.putExtra(r14, r4)
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r5 = r12.intValue()
            r45 = r12
            r12 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r4, r5, r0, r12)
            androidx.core.app.RemoteInput$Builder r4 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r5 = "extra_voice_reply"
            r4.<init>(r5)
            int r5 = org.telegram.messenger.R.string.Reply
            java.lang.String r12 = "Reply"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            androidx.core.app.RemoteInput$Builder r4 = r4.setLabel(r5)
            androidx.core.app.RemoteInput r4 = r4.build()
            boolean r5 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r5 == 0) goto L_0x0463
            int r5 = org.telegram.messenger.R.string.ReplyToGroup
            r46 = r9
            r12 = 1
            java.lang.Object[] r9 = new java.lang.Object[r12]
            r12 = 0
            r9[r12] = r8
            java.lang.String r12 = "ReplyToGroup"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r12, r5, r9)
            goto L_0x0473
        L_0x0463:
            r46 = r9
            int r5 = org.telegram.messenger.R.string.ReplyToUser
            r9 = 1
            java.lang.Object[] r12 = new java.lang.Object[r9]
            r9 = 0
            r12[r9] = r8
            java.lang.String r9 = "ReplyToUser"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r9, r5, r12)
        L_0x0473:
            androidx.core.app.NotificationCompat$Action$Builder r9 = new androidx.core.app.NotificationCompat$Action$Builder
            int r12 = org.telegram.messenger.R.drawable.ic_reply_icon
            r9.<init>(r12, r5, r0)
            r5 = 1
            androidx.core.app.NotificationCompat$Action$Builder r0 = r9.setAllowGeneratedReplies(r5)
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setSemanticAction(r5)
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.addRemoteInput(r4)
            r4 = 0
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setShowsUserInterface(r4)
            androidx.core.app.NotificationCompat$Action r0 = r0.build()
            r4 = r0
            goto L_0x049b
        L_0x0492:
            r39 = r4
            r35 = r5
            r46 = r9
            r45 = r12
            r4 = 0
        L_0x049b:
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.pushDialogs
            java.lang.Object r0 = r0.get(r6)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x04aa
            r5 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r5)
        L_0x04aa:
            int r0 = r0.intValue()
            int r5 = r38.size()
            int r0 = java.lang.Math.max(r0, r5)
            r5 = 2
            r9 = 1
            if (r0 <= r9) goto L_0x04d5
            int r12 = android.os.Build.VERSION.SDK_INT
            r9 = 28
            if (r12 < r9) goto L_0x04c1
            goto L_0x04d5
        L_0x04c1:
            java.lang.Object[] r9 = new java.lang.Object[r5]
            r12 = 0
            r9[r12] = r8
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r12 = 1
            r9[r12] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r9)
            r9 = r0
            goto L_0x04d6
        L_0x04d5:
            r9 = r8
        L_0x04d6:
            java.lang.Object r0 = r13.get(r2)
            r12 = r0
            androidx.core.app.Person r12 = (androidx.core.app.Person) r12
            int r0 = android.os.Build.VERSION.SDK_INT
            r5 = 28
            if (r0 < r5) goto L_0x054b
            if (r12 != 0) goto L_0x054b
            org.telegram.messenger.MessagesController r0 = r69.getMessagesController()
            java.lang.Long r5 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)
            if (r0 != 0) goto L_0x04fb
            org.telegram.messenger.UserConfig r0 = r69.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x04fb:
            if (r0 == 0) goto L_0x054b
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r0.photo     // Catch:{ all -> 0x0542 }
            if (r5 == 0) goto L_0x054b
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ all -> 0x0542 }
            if (r5 == 0) goto L_0x054b
            r47 = r10
            r48 = r11
            long r10 = r5.volume_id     // Catch:{ all -> 0x0540 }
            int r49 = (r10 > r31 ? 1 : (r10 == r31 ? 0 : -1))
            if (r49 == 0) goto L_0x054f
            int r5 = r5.local_id     // Catch:{ all -> 0x0540 }
            if (r5 == 0) goto L_0x054f
            androidx.core.app.Person$Builder r5 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x0540 }
            r5.<init>()     // Catch:{ all -> 0x0540 }
            java.lang.String r10 = "FromYou"
            int r11 = org.telegram.messenger.R.string.FromYou     // Catch:{ all -> 0x0540 }
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)     // Catch:{ all -> 0x0540 }
            androidx.core.app.Person$Builder r5 = r5.setName(r10)     // Catch:{ all -> 0x0540 }
            org.telegram.messenger.FileLoader r10 = r69.getFileLoader()     // Catch:{ all -> 0x0540 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x0540 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x0540 }
            r11 = 1
            java.io.File r0 = r10.getPathToAttach(r0, r11)     // Catch:{ all -> 0x0540 }
            r15.loadRoundAvatar(r0, r5)     // Catch:{ all -> 0x0540 }
            androidx.core.app.Person r5 = r5.build()     // Catch:{ all -> 0x0540 }
            r13.put(r2, r5)     // Catch:{ all -> 0x053d }
            r12 = r5
            goto L_0x054f
        L_0x053d:
            r0 = move-exception
            r12 = r5
            goto L_0x0547
        L_0x0540:
            r0 = move-exception
            goto L_0x0547
        L_0x0542:
            r0 = move-exception
            r47 = r10
            r48 = r11
        L_0x0547:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x054f
        L_0x054b:
            r47 = r10
            r48 = r11
        L_0x054f:
            r5 = r43
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest
            r5 = 1
            r0 = r0 ^ r5
            java.lang.String r5 = ""
            if (r12 == 0) goto L_0x0565
            if (r0 == 0) goto L_0x0565
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((androidx.core.app.Person) r12)
            goto L_0x056a
        L_0x0565:
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((java.lang.CharSequence) r5)
        L_0x056a:
            r10 = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            r11 = 28
            if (r0 < r11) goto L_0x057f
            boolean r11 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r11 == 0) goto L_0x0579
            if (r1 == 0) goto L_0x057f
        L_0x0579:
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r11 == 0) goto L_0x0582
        L_0x057f:
            r10.setConversationTitle(r9)
        L_0x0582:
            r9 = 28
            if (r0 < r9) goto L_0x0597
            if (r1 != 0) goto L_0x058e
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r0 != 0) goto L_0x0597
        L_0x058e:
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r0 == 0) goto L_0x0595
            goto L_0x0597
        L_0x0595:
            r0 = 0
            goto L_0x0598
        L_0x0597:
            r0 = 1
        L_0x0598:
            r10.setGroupConversation(r0)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r11 = 1
            java.lang.String[] r12 = new java.lang.String[r11]
            r43 = r4
            boolean[] r4 = new boolean[r11]
            int r0 = r38.size()
            int r0 = r0 - r11
            r11 = r0
            r50 = 0
            r51 = 0
        L_0x05b1:
            r52 = 1000(0x3e8, double:4.94E-321)
            if (r11 < 0) goto L_0x0936
            r49 = r14
            r14 = r38
            java.lang.Object r0 = r14.get(r11)
            r14 = r0
            org.telegram.messenger.MessageObject r14 = (org.telegram.messenger.MessageObject) r14
            java.lang.String r0 = r15.getShortStringForMessage(r14, r12, r4)
            r54 = r11
            java.lang.String r11 = "NotificationMessageScheduledName"
            int r55 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r55 != 0) goto L_0x05d1
            r23 = 0
            r12[r23] = r8
            goto L_0x05ea
        L_0x05d1:
            r23 = 0
            boolean r55 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r55 == 0) goto L_0x05ea
            r55 = r8
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            boolean r8 = r8.from_scheduled
            if (r8 == 0) goto L_0x05ec
            int r8 = org.telegram.messenger.R.string.NotificationMessageScheduledName
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r11, r8)
            r12[r23] = r8
            goto L_0x05ec
        L_0x05ea:
            r55 = r8
        L_0x05ec:
            if (r0 != 0) goto L_0x0629
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x061d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "message text is null for "
            r0.append(r8)
            int r8 = r14.getId()
            r0.append(r8)
            java.lang.String r8 = " did = "
            r0.append(r8)
            r8 = r10
            long r10 = r14.getDialogId()
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            r59 = r1
            r57 = r2
            r3 = r8
            goto L_0x0622
        L_0x061d:
            r59 = r1
            r57 = r2
            r3 = r10
        L_0x0622:
            r60 = r12
            r61 = r13
            r12 = r9
            goto L_0x0924
        L_0x0629:
            r8 = r10
            int r10 = r9.length()
            if (r10 <= 0) goto L_0x0635
            java.lang.String r10 = "\n\n"
            r9.append(r10)
        L_0x0635:
            java.lang.String r10 = "%1$s: %2$s"
            int r56 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r56 == 0) goto L_0x0665
            r56 = r8
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            boolean r8 = r8.from_scheduled
            if (r8 == 0) goto L_0x0662
            boolean r8 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r8 == 0) goto L_0x0662
            r57 = r2
            r8 = 2
            java.lang.Object[] r2 = new java.lang.Object[r8]
            int r3 = org.telegram.messenger.R.string.NotificationMessageScheduledName
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            r8 = 0
            r2[r8] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = java.lang.String.format(r10, r2)
            r9.append(r0)
            goto L_0x0683
        L_0x0662:
            r57 = r2
            goto L_0x0669
        L_0x0665:
            r57 = r2
            r56 = r8
        L_0x0669:
            r8 = 0
            r2 = r12[r8]
            if (r2 == 0) goto L_0x0680
            r2 = 2
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r2 = r12[r8]
            r3[r8] = r2
            r2 = 1
            r3[r2] = r0
            java.lang.String r2 = java.lang.String.format(r10, r3)
            r9.append(r2)
            goto L_0x0683
        L_0x0680:
            r9.append(r0)
        L_0x0683:
            r2 = r0
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r0 == 0) goto L_0x068b
            goto L_0x069a
        L_0x068b:
            if (r1 == 0) goto L_0x068f
            long r10 = -r6
            goto L_0x069b
        L_0x068f:
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r0 == 0) goto L_0x069a
            long r10 = r14.getSenderId()
            goto L_0x069b
        L_0x069a:
            r10 = r6
        L_0x069b:
            java.lang.Object r0 = r13.get(r10)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r3 = 0
            r8 = r12[r3]
            if (r8 != 0) goto L_0x06df
            if (r19 == 0) goto L_0x06db
            boolean r3 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r3 == 0) goto L_0x06cc
            if (r1 == 0) goto L_0x06bf
            int r3 = android.os.Build.VERSION.SDK_INT
            r8 = 27
            if (r3 <= r8) goto L_0x06db
            int r3 = org.telegram.messenger.R.string.NotificationHiddenChatName
            r8 = r42
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x06c9
        L_0x06bf:
            r8 = r42
            int r3 = org.telegram.messenger.R.string.NotificationHiddenChatUserName
            java.lang.String r8 = "NotificationHiddenChatUserName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
        L_0x06c9:
            r8 = r41
            goto L_0x06e6
        L_0x06cc:
            int r3 = android.os.Build.VERSION.SDK_INT
            r8 = 27
            if (r3 <= r8) goto L_0x06db
            int r3 = org.telegram.messenger.R.string.NotificationHiddenName
            r8 = r41
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r8, r3)
            goto L_0x06e6
        L_0x06db:
            r8 = r41
            r3 = r5
            goto L_0x06e6
        L_0x06df:
            r8 = r41
            r3 = 0
            r41 = r12[r3]
            r3 = r41
        L_0x06e6:
            r41 = r8
            if (r0 == 0) goto L_0x06fe
            java.lang.CharSequence r8 = r0.getName()
            boolean r8 = android.text.TextUtils.equals(r8, r3)
            if (r8 != 0) goto L_0x06f5
            goto L_0x06fe
        L_0x06f5:
            r59 = r1
            r60 = r12
            r1 = r0
            r12 = r9
            r9 = r2
            goto L_0x078c
        L_0x06fe:
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r3)
            r3 = 0
            boolean r8 = r4[r3]
            if (r8 == 0) goto L_0x077e
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r3 != 0) goto L_0x077e
            int r3 = android.os.Build.VERSION.SDK_INT
            r8 = 28
            if (r3 < r8) goto L_0x077e
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r3 != 0) goto L_0x0772
            if (r1 == 0) goto L_0x0721
            goto L_0x0772
        L_0x0721:
            r3 = r9
            long r8 = r14.getSenderId()
            r59 = r1
            org.telegram.messenger.MessagesController r1 = r69.getMessagesController()
            r60 = r12
            java.lang.Long r12 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r12)
            if (r1 != 0) goto L_0x074a
            org.telegram.messenger.MessagesStorage r1 = r69.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r1 = r1.getUserSync(r8)
            if (r1 == 0) goto L_0x074a
            org.telegram.messenger.MessagesController r8 = r69.getMessagesController()
            r9 = 1
            r8.putUser(r1, r9)
        L_0x074a:
            if (r1 == 0) goto L_0x076e
            org.telegram.tgnet.TLRPC$UserProfilePhoto r8 = r1.photo
            if (r8 == 0) goto L_0x076e
            org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_small
            if (r8 == 0) goto L_0x076e
            r9 = r2
            r12 = r3
            long r2 = r8.volume_id
            int r61 = (r2 > r31 ? 1 : (r2 == r31 ? 0 : -1))
            if (r61 == 0) goto L_0x0770
            int r2 = r8.local_id
            if (r2 == 0) goto L_0x0770
            org.telegram.messenger.FileLoader r2 = r69.getFileLoader()
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r1.photo
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            r3 = 1
            java.io.File r1 = r2.getPathToAttach(r1, r3)
            goto L_0x077a
        L_0x076e:
            r9 = r2
            r12 = r3
        L_0x0770:
            r1 = 0
            goto L_0x077a
        L_0x0772:
            r59 = r1
            r60 = r12
            r12 = r9
            r9 = r2
            r1 = r44
        L_0x077a:
            r15.loadRoundAvatar(r1, r0)
            goto L_0x0784
        L_0x077e:
            r59 = r1
            r60 = r12
            r12 = r9
            r9 = r2
        L_0x0784:
            androidx.core.app.Person r0 = r0.build()
            r13.put(r10, r0)
            r1 = r0
        L_0x078c:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r0 != 0) goto L_0x0902
            r2 = 0
            boolean r0 = r4[r2]
            java.lang.String r2 = ".provider"
            if (r0 == 0) goto L_0x0896
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r0 < r3) goto L_0x0896
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r8 = "activity"
            java.lang.Object r0 = r0.getSystemService(r8)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x0896
            if (r19 != 0) goto L_0x0896
            boolean r0 = r14.isSecretMedia()
            if (r0 != 0) goto L_0x0896
            int r0 = r14.type
            r8 = 1
            if (r0 == r8) goto L_0x07c2
            boolean r0 = r14.isSticker()
            if (r0 == 0) goto L_0x0896
        L_0x07c2:
            org.telegram.messenger.FileLoader r0 = r69.getFileLoader()
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            java.io.File r0 = r0.getPathToMessage(r8)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r8 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r10 = r14.messageOwner
            int r10 = r10.date
            long r10 = (long) r10
            long r10 = r10 * r52
            r8.<init>(r9, r10, r1)
            boolean r10 = r14.isSticker()
            if (r10 == 0) goto L_0x07e1
            java.lang.String r10 = "image/webp"
            goto L_0x07e3
        L_0x07e1:
            java.lang.String r10 = "image/jpeg"
        L_0x07e3:
            boolean r11 = r0.exists()
            if (r11 == 0) goto L_0x080e
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0807 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0807 }
            r3.<init>()     // Catch:{ Exception -> 0x0807 }
            r61 = r13
            java.lang.String r13 = org.telegram.messenger.ApplicationLoader.getApplicationId()     // Catch:{ Exception -> 0x0805 }
            r3.append(r13)     // Catch:{ Exception -> 0x0805 }
            r3.append(r2)     // Catch:{ Exception -> 0x0805 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0805 }
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r11, r3, r0)     // Catch:{ Exception -> 0x0805 }
            goto L_0x0864
        L_0x0805:
            r0 = move-exception
            goto L_0x080a
        L_0x0807:
            r0 = move-exception
            r61 = r13
        L_0x080a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0863
        L_0x080e:
            r61 = r13
            org.telegram.messenger.FileLoader r3 = r69.getFileLoader()
            java.lang.String r11 = r0.getName()
            boolean r3 = r3.isLoadingFile(r11)
            if (r3 == 0) goto L_0x0863
            android.net.Uri$Builder r3 = new android.net.Uri$Builder
            r3.<init>()
            java.lang.String r11 = "content"
            android.net.Uri$Builder r3 = r3.scheme(r11)
            java.lang.String r11 = org.telegram.messenger.NotificationImageProvider.getAuthority()
            android.net.Uri$Builder r3 = r3.authority(r11)
            java.lang.String r11 = "msg_media_raw"
            android.net.Uri$Builder r3 = r3.appendPath(r11)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            int r13 = r15.currentAccount
            r11.append(r13)
            r11.append(r5)
            java.lang.String r11 = r11.toString()
            android.net.Uri$Builder r3 = r3.appendPath(r11)
            java.lang.String r11 = r0.getName()
            android.net.Uri$Builder r3 = r3.appendPath(r11)
            java.lang.String r0 = r0.getAbsolutePath()
            java.lang.String r11 = "final_path"
            android.net.Uri$Builder r0 = r3.appendQueryParameter(r11, r0)
            android.net.Uri r0 = r0.build()
            goto L_0x0864
        L_0x0863:
            r0 = 0
        L_0x0864:
            if (r0 == 0) goto L_0x0898
            r8.setData(r10, r0)
            r3 = r56
            r3.addMessage(r8)
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r10 = "com.android.systemui"
            r11 = 1
            r8.grantUriPermission(r10, r0, r11)
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda4 r8 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda4
            r8.<init>(r0)
            r10 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r10)
            java.lang.CharSequence r0 = r14.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0894
            java.lang.CharSequence r0 = r14.caption
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            int r8 = r8.date
            long r10 = (long) r8
            long r10 = r10 * r52
            r3.addMessage(r0, r10, r1)
        L_0x0894:
            r0 = 1
            goto L_0x089b
        L_0x0896:
            r61 = r13
        L_0x0898:
            r3 = r56
            r0 = 0
        L_0x089b:
            if (r0 != 0) goto L_0x08a7
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            int r0 = r0.date
            long r10 = (long) r0
            long r10 = r10 * r52
            r3.addMessage(r9, r10, r1)
        L_0x08a7:
            r1 = 0
            boolean r0 = r4[r1]
            if (r0 == 0) goto L_0x0910
            if (r19 != 0) goto L_0x0910
            boolean r0 = r14.isVoice()
            if (r0 == 0) goto L_0x0910
            java.util.List r0 = r3.getMessages()
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x0910
            org.telegram.messenger.FileLoader r1 = r69.getFileLoader()
            org.telegram.tgnet.TLRPC$Message r8 = r14.messageOwner
            java.io.File r1 = r1.getPathToMessage(r8)
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 24
            if (r8 < r9) goto L_0x08ea
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x08e8 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x08e8 }
            r9.<init>()     // Catch:{ Exception -> 0x08e8 }
            java.lang.String r10 = org.telegram.messenger.ApplicationLoader.getApplicationId()     // Catch:{ Exception -> 0x08e8 }
            r9.append(r10)     // Catch:{ Exception -> 0x08e8 }
            r9.append(r2)     // Catch:{ Exception -> 0x08e8 }
            java.lang.String r2 = r9.toString()     // Catch:{ Exception -> 0x08e8 }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r8, r2, r1)     // Catch:{ Exception -> 0x08e8 }
            goto L_0x08ee
        L_0x08e8:
            r1 = 0
            goto L_0x08ee
        L_0x08ea:
            android.net.Uri r1 = android.net.Uri.fromFile(r1)
        L_0x08ee:
            if (r1 == 0) goto L_0x0910
            int r2 = r0.size()
            r8 = 1
            int r2 = r2 - r8
            java.lang.Object r0 = r0.get(r2)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r0
            java.lang.String r2 = "audio/ogg"
            r0.setData(r2, r1)
            goto L_0x0910
        L_0x0902:
            r61 = r13
            r3 = r56
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            int r0 = r0.date
            long r10 = (long) r0
            long r10 = r10 * r52
            r3.addMessage(r9, r10, r1)
        L_0x0910:
            int r0 = (r6 > r29 ? 1 : (r6 == r29 ? 0 : -1))
            if (r0 != 0) goto L_0x0924
            org.telegram.tgnet.TLRPC$Message r0 = r14.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x0924
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r1 = r14.getId()
            r51 = r0
            r50 = r1
        L_0x0924:
            int r11 = r54 + -1
            r10 = r3
            r9 = r12
            r14 = r49
            r8 = r55
            r2 = r57
            r1 = r59
            r12 = r60
            r13 = r61
            goto L_0x05b1
        L_0x0936:
            r57 = r2
            r55 = r8
            r12 = r9
            r3 = r10
            r61 = r13
            r49 = r14
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.ui.LaunchActivity> r2 = org.telegram.ui.LaunchActivity.class
            r0.<init>(r1, r2)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "com.tmessages.openchat"
            r1.append(r2)
            double r4 = java.lang.Math.random()
            r1.append(r4)
            r2 = 2147483647(0x7fffffff, float:NaN)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.setAction(r1)
            r1 = 67108864(0x4000000, float:1.5046328E-36)
            r0.setFlags(r1)
            java.lang.String r1 = "android.intent.category.LAUNCHER"
            r0.addCategory(r1)
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r1 == 0) goto L_0x0981
            int r1 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            java.lang.String r2 = "encId"
            r0.putExtra(r2, r1)
            goto L_0x0993
        L_0x0981:
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r1 == 0) goto L_0x098d
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r6)
            goto L_0x0993
        L_0x098d:
            long r1 = -r6
            java.lang.String r4 = "chatId"
            r0.putExtra(r4, r1)
        L_0x0993:
            int r1 = r15.currentAccount
            r2 = r49
            r0.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r4 = 1073741824(0x40000000, float:2.0)
            r5 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r1, r5, r0, r4)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r4 = r43
            if (r43 == 0) goto L_0x09af
            r1.addAction(r4)
        L_0x09af:
            android.content.Intent r5 = new android.content.Intent
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r9 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r5.<init>(r8, r9)
            r8 = 32
            r5.addFlags(r8)
            java.lang.String r8 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r5.setAction(r8)
            r8 = r48
            r5.putExtra(r8, r6)
            r8 = r46
            r9 = r47
            r5.putExtra(r9, r8)
            int r9 = r15.currentAccount
            r5.putExtra(r2, r9)
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r10 = r45.intValue()
            r11 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r5 = android.app.PendingIntent.getBroadcast(r9, r10, r5, r11)
            androidx.core.app.NotificationCompat$Action$Builder r9 = new androidx.core.app.NotificationCompat$Action$Builder
            int r10 = org.telegram.messenger.R.drawable.msg_markread
            int r11 = org.telegram.messenger.R.string.MarkAsRead
            java.lang.String r13 = "MarkAsRead"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
            r9.<init>(r10, r11, r5)
            r5 = 2
            androidx.core.app.NotificationCompat$Action$Builder r5 = r9.setSemanticAction(r5)
            r9 = 0
            androidx.core.app.NotificationCompat$Action$Builder r5 = r5.setShowsUserInterface(r9)
            androidx.core.app.NotificationCompat$Action r5 = r5.build()
            boolean r9 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            java.lang.String r10 = "_"
            if (r9 != 0) goto L_0x0a3b
            boolean r9 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r9 == 0) goto L_0x0a22
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "tguser"
            r9.append(r11)
            r9.append(r6)
            r9.append(r10)
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            goto L_0x0a5e
        L_0x0a22:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "tgchat"
            r9.append(r11)
            long r13 = -r6
            r9.append(r13)
            r9.append(r10)
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            goto L_0x0a5e
        L_0x0a3b:
            long r13 = globalSecretChatId
            int r9 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1))
            if (r9 == 0) goto L_0x0a5d
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "tgenc"
            r9.append(r11)
            int r11 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            r9.append(r11)
            r9.append(r10)
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            goto L_0x0a5e
        L_0x0a5d:
            r8 = 0
        L_0x0a5e:
            if (r8 == 0) goto L_0x0a82
            r1.setDismissalId(r8)
            androidx.core.app.NotificationCompat$WearableExtender r9 = new androidx.core.app.NotificationCompat$WearableExtender
            r9.<init>()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "summary_"
            r10.append(r11)
            r10.append(r8)
            java.lang.String r8 = r10.toString()
            r9.setDismissalId(r8)
            r14 = r70
            r14.extend(r9)
            goto L_0x0a84
        L_0x0a82:
            r14 = r70
        L_0x0a84:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "tgaccount"
            r8.append(r9)
            r9 = r57
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            r1.setBridgeTag(r8)
            r8 = r38
            r11 = 0
            java.lang.Object r13 = r8.get(r11)
            org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
            org.telegram.tgnet.TLRPC$Message r11 = r13.messageOwner
            int r11 = r11.date
            long r9 = (long) r11
            long r9 = r9 * r52
            androidx.core.app.NotificationCompat$Builder r11 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            r11.<init>(r13)
            r13 = r55
            androidx.core.app.NotificationCompat$Builder r11 = r11.setContentTitle(r13)
            int r14 = org.telegram.messenger.R.drawable.notification
            androidx.core.app.NotificationCompat$Builder r11 = r11.setSmallIcon(r14)
            java.lang.String r12 = r12.toString()
            androidx.core.app.NotificationCompat$Builder r11 = r11.setContentText(r12)
            r12 = 1
            androidx.core.app.NotificationCompat$Builder r11 = r11.setAutoCancel(r12)
            int r8 = r8.size()
            androidx.core.app.NotificationCompat$Builder r8 = r11.setNumber(r8)
            r11 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            androidx.core.app.NotificationCompat$Builder r8 = r8.setColor(r11)
            r11 = 0
            androidx.core.app.NotificationCompat$Builder r8 = r8.setGroupSummary(r11)
            androidx.core.app.NotificationCompat$Builder r8 = r8.setWhen(r9)
            androidx.core.app.NotificationCompat$Builder r8 = r8.setShowWhen(r12)
            androidx.core.app.NotificationCompat$Builder r3 = r8.setStyle(r3)
            androidx.core.app.NotificationCompat$Builder r0 = r3.setContentIntent(r0)
            androidx.core.app.NotificationCompat$Builder r0 = r0.extend(r1)
            r11 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r11 = r11 - r9
            java.lang.String r1 = java.lang.String.valueOf(r11)
            androidx.core.app.NotificationCompat$Builder r0 = r0.setSortKey(r1)
            java.lang.String r1 = "msg"
            androidx.core.app.NotificationCompat$Builder r9 = r0.setCategory(r1)
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r3 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r1, r3)
            java.lang.String r1 = "messageDate"
            r3 = r37
            r0.putExtra(r1, r3)
            java.lang.String r1 = "dialogId"
            r0.putExtra(r1, r6)
            int r1 = r15.currentAccount
            r0.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r3 = r45.intValue()
            r8 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r1, r3, r0, r8)
            r9.setDeleteIntent(r0)
            if (r36 == 0) goto L_0x0b3a
            java.lang.String r0 = r15.notificationGroup
            r9.setGroup(r0)
            r1 = 1
            r9.setGroupAlertBehavior(r1)
        L_0x0b3a:
            if (r4 == 0) goto L_0x0b3f
            r9.addAction(r4)
        L_0x0b3f:
            if (r19 != 0) goto L_0x0b44
            r9.addAction(r5)
        L_0x0b44:
            int r0 = r25.size()
            r4 = 1
            if (r0 != r4) goto L_0x0b57
            boolean r0 = android.text.TextUtils.isEmpty(r71)
            if (r0 != 0) goto L_0x0b57
            r14 = r71
            r9.setSubText(r14)
            goto L_0x0b59
        L_0x0b57:
            r14 = r71
        L_0x0b59:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r0 == 0) goto L_0x0b62
            r9.setLocalOnly(r4)
        L_0x0b62:
            if (r39 == 0) goto L_0x0b69
            r10 = r39
            r9.setLargeIcon(r10)
        L_0x0b69:
            r1 = 0
            boolean r0 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1)
            if (r0 != 0) goto L_0x0CLASSNAME
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x0CLASSNAME
            r1 = r51
            if (r1 == 0) goto L_0x0CLASSNAME
            int r0 = r1.size()
            r10 = 0
        L_0x0b7d:
            if (r10 >= r0) goto L_0x0CLASSNAME
            java.lang.Object r3 = r1.get(r10)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r3 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r5 = r3.buttons
            int r5 = r5.size()
            r8 = 0
        L_0x0b8c:
            if (r8 >= r5) goto L_0x0bef
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r11 = r3.buttons
            java.lang.Object r11 = r11.get(r8)
            org.telegram.tgnet.TLRPC$KeyboardButton r11 = (org.telegram.tgnet.TLRPC$KeyboardButton) r11
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r12 == 0) goto L_0x0bd8
            android.content.Intent r12 = new android.content.Intent
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            r27 = r0
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r0 = org.telegram.messenger.NotificationCallbackReceiver.class
            r12.<init>(r4, r0)
            int r0 = r15.currentAccount
            r12.putExtra(r2, r0)
            java.lang.String r0 = "did"
            r12.putExtra(r0, r6)
            byte[] r0 = r11.data
            if (r0 == 0) goto L_0x0bb8
            java.lang.String r4 = "data"
            r12.putExtra(r4, r0)
        L_0x0bb8:
            java.lang.String r0 = "mid"
            r4 = r50
            r12.putExtra(r0, r4)
            java.lang.String r0 = r11.text
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext
            r29 = r1
            int r1 = r15.lastButtonId
            r49 = r2
            int r2 = r1 + 1
            r15.lastButtonId = r2
            r2 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r11, r1, r12, r2)
            r11 = 0
            r9.addAction(r11, r0, r1)
            goto L_0x0be3
        L_0x0bd8:
            r27 = r0
            r29 = r1
            r49 = r2
            r4 = r50
            r2 = 134217728(0x8000000, float:3.85186E-34)
            r11 = 0
        L_0x0be3:
            int r8 = r8 + 1
            r50 = r4
            r0 = r27
            r1 = r29
            r2 = r49
            r4 = 1
            goto L_0x0b8c
        L_0x0bef:
            r27 = r0
            r29 = r1
            r49 = r2
            r4 = r50
            r2 = 134217728(0x8000000, float:3.85186E-34)
            r11 = 0
            int r10 = r10 + 1
            r2 = r49
            r4 = 1
            goto L_0x0b7d
        L_0x0CLASSNAME:
            r11 = 0
            if (r35 != 0) goto L_0x0CLASSNAME
            if (r34 == 0) goto L_0x0CLASSNAME
            r8 = r34
            java.lang.String r0 = r8.phone
            if (r0 == 0) goto L_0x0c2b
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0c2b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "tel:+"
            r0.append(r1)
            java.lang.String r1 = r8.phone
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r9.addPerson(r0)
            goto L_0x0c2b
        L_0x0CLASSNAME:
            r8 = r34
        L_0x0c2b:
            int r0 = android.os.Build.VERSION.SDK_INT
            r12 = 26
            r4 = r36
            r10 = r40
            if (r0 < r12) goto L_0x0CLASSNAME
            r15.setNotificationChannel(r10, r9, r4)
        L_0x0CLASSNAME:
            org.telegram.messenger.NotificationsController$1NotificationHolder r0 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            r22 = 7
            r1 = r0
            int r3 = r45.intValue()
            r29 = r57
            r2 = r69
            r23 = r4
            r33 = r35
            r27 = 1
            r4 = r6
            r62 = r6
            r31 = 27
            r6 = r13
            r13 = r20
            r7 = r8
            r20 = r28
            r8 = r33
            r28 = 0
            r16 = r10
            r10 = r74
            r64 = r45
            r11 = r75
            r65 = r16
            r16 = 26
            r12 = r76
            r66 = r13
            r67 = r61
            r32 = 26
            r13 = r77
            r14 = r78
            r15 = r79
            r16 = r80
            r17 = r81
            r18 = r82
            r1.<init>(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            r14 = r66
            r14.add(r0)
            r15 = r69
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r1 = r62
            r3 = r64
            r0.put(r1, r3)
        L_0x0c8d:
            int r4 = r24 + 1
            r7 = r14
            r8 = r20
            r6 = r21
            r5 = r23
            r11 = r25
            r10 = r26
            r2 = r29
            r12 = r65
            r13 = r67
            r1 = 7
            r9 = 0
            r14 = r70
            goto L_0x0108
        L_0x0ca6:
            r23 = r5
            r14 = r7
            r20 = r8
            r65 = r12
            r67 = r13
            r28 = 0
            if (r23 == 0) goto L_0x0cf7
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0ccd
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r15.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0ccd:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager     // Catch:{ SecurityException -> 0x0cd7 }
            int r1 = r15.notificationId     // Catch:{ SecurityException -> 0x0cd7 }
            r2 = r65
            r0.notify(r1, r2)     // Catch:{ SecurityException -> 0x0cd7 }
            goto L_0x0d06
        L_0x0cd7:
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
            goto L_0x0d06
        L_0x0cf7:
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0d06
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.cancel(r1)
        L_0x0d06:
            r9 = 0
        L_0x0d07:
            int r0 = r20.size()
            if (r9 >= r0) goto L_0x0d4c
            r1 = r20
            long r2 = r1.keyAt(r9)
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x0d20
            goto L_0x0d47
        L_0x0d20:
            java.lang.Object r0 = r1.valueAt(r9)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0d3e
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "cancel notification id "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0d3e:
            androidx.core.app.NotificationManagerCompat r2 = notificationManager
            int r0 = r0.intValue()
            r2.cancel(r0)
        L_0x0d47:
            int r9 = r9 + 1
            r20 = r1
            goto L_0x0d07
        L_0x0d4c:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r14.size()
            r0.<init>(r1)
            int r1 = r14.size()
            r9 = 0
        L_0x0d5a:
            if (r9 >= r1) goto L_0x0db8
            java.lang.Object r2 = r14.get(r9)
            org.telegram.messenger.NotificationsController$1NotificationHolder r2 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r2
            r0.clear()
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 29
            if (r3 < r4) goto L_0x0d9d
            long r3 = r2.dialogId
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r3)
            if (r3 != 0) goto L_0x0d9d
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
            if (r3 == 0) goto L_0x0d9f
            r0.add(r3)
            goto L_0x0d9f
        L_0x0d9d:
            r10 = r67
        L_0x0d9f:
            r2.call()
            boolean r2 = r69.unsupportedNotificationShortcut()
            if (r2 != 0) goto L_0x0db3
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L_0x0db3
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r2, r0)
        L_0x0db3:
            int r9 = r9 + 1
            r67 = r10
            goto L_0x0d5a
        L_0x0db8:
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
                    this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_out, 1);
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
            long j2 = notificationsSettings.getLong("sound_document_id_" + j, 0);
            String string = notificationsSettings.getString("sound_path_" + j, (String) null);
            TLRPC$TL_inputPeerNotifySettings tLRPC$TL_inputPeerNotifySettings4 = tLRPC$TL_account_updateNotifySettings.settings;
            tLRPC$TL_inputPeerNotifySettings4.flags = tLRPC$TL_inputPeerNotifySettings4.flags | 8;
            if (j2 != 0) {
                TLRPC$TL_notificationSoundRingtone tLRPC$TL_notificationSoundRingtone = new TLRPC$TL_notificationSoundRingtone();
                tLRPC$TL_notificationSoundRingtone.id = j2;
                tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundRingtone;
            } else if (string == null) {
                tLRPC$TL_inputPeerNotifySettings4.sound = new TLRPC$TL_notificationSoundDefault();
            } else if (string.equals("NoSound")) {
                tLRPC$TL_account_updateNotifySettings.settings.sound = new TLRPC$TL_notificationSoundNone();
            } else {
                TLRPC$TL_notificationSoundLocal tLRPC$TL_notificationSoundLocal = new TLRPC$TL_notificationSoundLocal();
                tLRPC$TL_notificationSoundLocal.title = notificationsSettings.getString("sound_" + j, (String) null);
                tLRPC$TL_notificationSoundLocal.data = string;
                tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundLocal;
            }
            TLRPC$TL_inputNotifyPeer tLRPC$TL_inputNotifyPeer = new TLRPC$TL_inputNotifyPeer();
            tLRPC$TL_account_updateNotifySettings.peer = tLRPC$TL_inputNotifyPeer;
            tLRPC$TL_inputNotifyPeer.peer = getMessagesController().getInputPeer(j);
            getConnectionsManager().sendRequest(tLRPC$TL_account_updateNotifySettings, NotificationsController$$ExternalSyntheticLambda40.INSTANCE);
        }
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
            str3 = "GroupSoundDocId";
            str2 = "GroupSoundPath";
        } else if (i == 1) {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyUsers();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableAll2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewAll", true);
            str = "GlobalSound";
            str3 = "GlobalSoundDocId";
            str2 = "GlobalSoundPath";
        } else {
            tLRPC$TL_account_updateNotifySettings.peer = new TLRPC$TL_inputNotifyBroadcasts();
            tLRPC$TL_account_updateNotifySettings.settings.mute_until = notificationsSettings.getInt("EnableChannel2", 0);
            tLRPC$TL_account_updateNotifySettings.settings.show_previews = notificationsSettings.getBoolean("EnablePreviewChannel", true);
            str = "ChannelSound";
            str3 = "ChannelSoundDocId";
            str2 = "ChannelSoundPath";
        }
        tLRPC$TL_account_updateNotifySettings.settings.flags |= 8;
        long j = notificationsSettings.getLong(str3, 0);
        String string = notificationsSettings.getString(str2, "NoSound");
        if (j != 0) {
            TLRPC$TL_notificationSoundRingtone tLRPC$TL_notificationSoundRingtone = new TLRPC$TL_notificationSoundRingtone();
            tLRPC$TL_notificationSoundRingtone.id = j;
            tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundRingtone;
        } else if (string == null) {
            tLRPC$TL_account_updateNotifySettings.settings.sound = new TLRPC$TL_notificationSoundDefault();
        } else if (string.equals("NoSound")) {
            tLRPC$TL_account_updateNotifySettings.settings.sound = new TLRPC$TL_notificationSoundNone();
        } else {
            TLRPC$TL_notificationSoundLocal tLRPC$TL_notificationSoundLocal = new TLRPC$TL_notificationSoundLocal();
            tLRPC$TL_notificationSoundLocal.title = notificationsSettings.getString(str, (String) null);
            tLRPC$TL_notificationSoundLocal.data = string;
            tLRPC$TL_account_updateNotifySettings.settings.sound = tLRPC$TL_notificationSoundLocal;
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
        getMessagesStorage().setDialogFlags(j, 0);
        edit.apply();
        TLRPC$Dialog tLRPC$Dialog = getMessagesController().dialogs_dict.get(j);
        if (tLRPC$Dialog != null) {
            tLRPC$Dialog.notify_settings = new TLRPC$TL_peerNotifySettings();
        }
        updateServerNotificationsSettings(j);
    }
}
