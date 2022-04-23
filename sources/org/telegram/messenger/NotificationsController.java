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
        for (int i4 = 0; i4 < 3; i4++) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0224, code lost:
        if (r11.getBoolean("EnablePreviewAll", true) == false) goto L_0x0228;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0234, code lost:
        if (r11.getBoolean("EnablePreviewGroup", r10) != false) goto L_0x0240;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x023e, code lost:
        if (r11.getBoolean("EnablePreviewChannel", r10) != false) goto L_0x0240;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0240, code lost:
        r4 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x024c, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x024e, code lost:
        r24[0] = null;
        r5 = r4.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x0256, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) == false) goto L_0x025f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x025e, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x0261, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x0265, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L_0x0269;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x026b, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L_0x027d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x027c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x0280, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L_0x02e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x0282, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", NUM, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(((long) r0.messageOwner.date) * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(((long) r0.messageOwner.date) * 1000));
        r0 = r0.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02e0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", NUM, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02e3, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02e7, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L_0x02eb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02ed, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L_0x0307;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02f1, code lost:
        if (r5.video == false) goto L_0x02fd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x02fc, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageVideoIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0306, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x0309, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x0426;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:187:0x030b, code lost:
        r2 = r5.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x0311, code lost:
        if (r2 != 0) goto L_0x032d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x031a, code lost:
        if (r5.users.size() != 1) goto L_0x032d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x031c, code lost:
        r2 = r0.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0331, code lost:
        if (r2 == 0) goto L_0x03ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x033b, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x0356;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x033f, code lost:
        if (r6.megagroup != false) goto L_0x0356;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0355, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0358, code lost:
        if (r2 != r17) goto L_0x036f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x036e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x036f, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x037b, code lost:
        if (r0 != null) goto L_0x037f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x037d, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0383, code lost:
        if (r8 != r0.id) goto L_0x03b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0387, code lost:
        if (r6.megagroup == false) goto L_0x039e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:212:0x039d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x03b2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x03cd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03ce, code lost:
        r2 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03de, code lost:
        if (r3 >= r0.messageOwner.action.users.size()) goto L_0x040b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:220:0x03e0, code lost:
        r4 = getMessagesController().getUser(r0.messageOwner.action.users.get(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x03f4, code lost:
        if (r4 == null) goto L_0x0408;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x03f6, code lost:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03fe, code lost:
        if (r2.length() == 0) goto L_0x0405;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x0400, code lost:
        r2.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x0405, code lost:
        r2.append(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:226:0x0408, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x0425, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r1, r6.title, r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0429, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L_0x043f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x043e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x0441, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) == false) goto L_0x044a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x0449, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x044c, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L_0x0512;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x044e, code lost:
        r2 = r5.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0454, code lost:
        if (r2 != 0) goto L_0x0470;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x045d, code lost:
        if (r5.users.size() != 1) goto L_0x0470;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x045f, code lost:
        r2 = r0.messageOwner.action.users.get(0).longValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x0474, code lost:
        if (r2 == 0) goto L_0x04ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x0478, code lost:
        if (r2 != r17) goto L_0x048f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x048e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x048f, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x049b, code lost:
        if (r0 != null) goto L_0x049f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x049d, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x04b9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04ba, code lost:
        r2 = new java.lang.StringBuilder();
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04ca, code lost:
        if (r3 >= r0.messageOwner.action.users.size()) goto L_0x04f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x04cc, code lost:
        r4 = getMessagesController().getUser(r0.messageOwner.action.users.get(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04e0, code lost:
        if (r4 == null) goto L_0x04f4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x04e2, code lost:
        r4 = org.telegram.messenger.UserObject.getUserName(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04ea, code lost:
        if (r2.length() == 0) goto L_0x04f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x04ec, code lost:
        r2.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x04f1, code lost:
        r2.append(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x04f4, code lost:
        r3 = r3 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x0511, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r1, r6.title, r2.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x0515, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x052c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x052b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0531, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x0545;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0544, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r1, r5.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0547, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0ea2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x054b, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x054f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0551, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x05b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0553, code lost:
        r2 = r5.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0557, code lost:
        if (r2 != r17) goto L_0x056e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x056d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x0573, code lost:
        if (r2 != r8) goto L_0x0587;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0586, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:289:0x0587, code lost:
        r0 = getMessagesController().getUser(java.lang.Long.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0599, code lost:
        if (r0 != null) goto L_0x059d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:291:0x059b, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x05b8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r1, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x05bb, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x05c4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:297:0x05c3, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x05c6, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x05cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x05ce, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:303:0x05d1, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x05e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x05e4, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x05e9, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x05fb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:309:0x05fa, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r5.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:311:0x05fd, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x0606;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x0605, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x0608, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0e3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x060e, code lost:
        if (r6 == null) goto L_0x090b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:319:0x0614, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r6) == false) goto L_0x061a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x0618, code lost:
        if (r6.megagroup == false) goto L_0x090b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x061a, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x061c, code lost:
        if (r0 != null) goto L_0x0633;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x0632, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:327:0x063a, code lost:
        if (r0.isMusic() == false) goto L_0x064e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:329:0x064d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x0657, code lost:
        if (r0.isVideo() == false) goto L_0x06a4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x065d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x068f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x0667, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x068f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x068e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "📹 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x06a3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x06a8, code lost:
        if (r0.isGif() == false) goto L_0x06f5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x06ae, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x06e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x06b8, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x06e0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:347:0x06df, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "🎬 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:349:0x06f4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:351:0x06fc, code lost:
        if (r0.isVoice() == false) goto L_0x0710;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:353:0x070f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:0x0714, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0728;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x0727, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:359:0x072c, code lost:
        if (r0.isSticker() != false) goto L_0x08da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:361:0x0732, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0736;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0736, code lost:
        r4 = r0.messageOwner;
        r7 = r4.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:363:0x073c, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0787;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x0742, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0772;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:367:0x074a, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0772;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x0771, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "📎 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x0786, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:373:0x0789, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x08c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:375:0x078d, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0791;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:377:0x0793, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x07aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:379:0x07a9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:381:0x07ae, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x07d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x07b0, code lost:
        r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:383:0x07cf, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r1, r6.title, org.telegram.messenger.ContactsController.formatName(r7.first_name, r7.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:385:0x07d2, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0810;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x07d4, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:387:0x07da, code lost:
        if (r0.quiz == false) goto L_0x07f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x07f5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r1, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:391:0x080f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r1, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x0812, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x085d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0818, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0848;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0820, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0848;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0847, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r1, "🖼 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x085c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0862, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0876;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0875, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:406:0x0876, code lost:
        r4 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x0878, code lost:
        if (r4 == null) goto L_0x08b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x087e, code lost:
        if (r4.length() <= 0) goto L_0x08b0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a5, code lost:
        if (r11.getBoolean("EnablePreviewGroup", true) != false) goto L_0x00a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0880, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0886, code lost:
        if (r0.length() <= 20) goto L_0x089d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:412:0x0888, code lost:
        r4 = new java.lang.StringBuilder();
        r7 = 0;
        r4.append(r0.subSequence(0, 20));
        r4.append("...");
        r0 = r4.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x089d, code lost:
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:414:0x089e, code lost:
        r2 = new java.lang.Object[3];
        r2[r7] = r1;
        r2[1] = r0;
        r2[2] = r6.title;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x08af, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x08c4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x08d9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:420:0x08da, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x08e0, code lost:
        if (r0 == null) goto L_0x08f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x08f7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r1, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x090a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x090c, code lost:
        if (r6 == null) goto L_0x0bbd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:428:0x090e, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x0910, code lost:
        if (r0 != null) goto L_0x0923;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0922, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x0928, code lost:
        if (r0.isMusic() == false) goto L_0x093a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x0939, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0943, code lost:
        if (r0.isVideo() == false) goto L_0x098a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0949, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0978;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x0953, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0978;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x0977, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x0989, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x098e, code lost:
        if (r0.isGif() == false) goto L_0x09d5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0994, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x09c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x099e, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x09c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x09c2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x09d4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x09db, code lost:
        if (r0.isVoice() == false) goto L_0x09ed;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x09ec, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00b3, code lost:
        if (r11.getBoolean("EnablePreviewChannel", r3) == false) goto L_0x00b5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x09f1, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0a03;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x0a02, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x0a07, code lost:
        if (r0.isSticker() != false) goto L_0x0b91;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x0a0d, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0a11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:468:0x0a11, code lost:
        r1 = r0.messageOwner;
        r7 = r1.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x0a17, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0a5c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0a1d, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0a4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0a25, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L_0x0a4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0a49, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0a5b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0a5e, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0b7f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0a62, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0a66;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0a68, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0a7c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0a7b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0a7f, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0a9f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:488:0x0a81, code lost:
        r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0a9e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r6.title, org.telegram.messenger.ContactsController.formatName(r7.first_name, r7.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0aa1, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0ad9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:492:0x0aa3, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0aa9, code lost:
        if (r0.quiz == false) goto L_0x0ac2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0ac1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0ad8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x0adb, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0b20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:501:0x0ae1, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0b0e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:503:0x0ae9, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L_0x0b0e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:505:0x0b0d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0b1f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:509:0x0b24, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0b36;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:511:0x0b35, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0b36, code lost:
        r1 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0b38, code lost:
        if (r1 == null) goto L_0x0b6d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:515:0x0b3e, code lost:
        if (r1.length() <= 0) goto L_0x0b6d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0b40, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:517:0x0b46, code lost:
        if (r0.length() <= 20) goto L_0x0b5d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0b48, code lost:
        r1 = new java.lang.StringBuilder();
        r8 = 0;
        r1.append(r0.subSequence(0, 20));
        r1.append("...");
        r0 = r1.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:519:0x0b5d, code lost:
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0b5e, code lost:
        r1 = new java.lang.Object[2];
        r1[r8] = r6.title;
        r1[1] = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:521:0x0b6c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:523:0x0b7e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:525:0x0b90, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0b91, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:527:0x0b96, code lost:
        if (r0 == null) goto L_0x0bac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:529:0x0bab, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:531:0x0bbc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0bbd, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:533:0x0bc0, code lost:
        if (r0 != null) goto L_0x0bd0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:535:0x0bcf, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:537:0x0bd4, code lost:
        if (r0.isMusic() == false) goto L_0x0be4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:539:0x0be3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:541:0x0bed, code lost:
        if (r0.isVideo() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:543:0x0bf3, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:545:0x0bfd, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:547:0x0c1f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r1, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:549:0x0c2f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:0x0CLASSNAME, code lost:
        if (r0.isGif() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0c3a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:555:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:557:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r1, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:559:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:561:0x0c7d, code lost:
        if (r0.isVoice() == false) goto L_0x0c8d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:563:0x0c8c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:565:0x0CLASSNAME, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0ca1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:567:0x0ca0, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:569:0x0ca5, code lost:
        if (r0.isSticker() != false) goto L_0x0e17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:571:0x0cab, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0caf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0caf, code lost:
        r4 = r0.messageOwner;
        r7 = r4.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0cb5, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0cf6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:575:0x0cbb, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0ce6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0cc3, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0ce6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:579:0x0ce5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r1, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:581:0x0cf5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:583:0x0cf8, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0e07;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:585:0x0cfc, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0d00;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:587:0x0d02, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0d14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:589:0x0d13, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:591:0x0d17, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0d35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0d19, code lost:
        r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:593:0x0d34, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r1, org.telegram.messenger.ContactsController.formatName(r7.first_name, r7.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:595:0x0d37, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0d6b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0d39, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0d3f, code lost:
        if (r0.quiz == false) goto L_0x0d56;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:599:0x0d55, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r1, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0d6a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r1, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0d6d, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0dae;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0d73, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0d9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0d7b, code lost:
        if (android.text.TextUtils.isEmpty(r4.message) != false) goto L_0x0d9e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0d9d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r1, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0dad, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0db2, code lost:
        if ((r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0dc2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0dc1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0dc2, code lost:
        r4 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:617:0x0dc4, code lost:
        if (r4 == null) goto L_0x0df7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:619:0x0dca, code lost:
        if (r4.length() <= 0) goto L_0x0df7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0dcc, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:621:0x0dd2, code lost:
        if (r0.length() <= 20) goto L_0x0de9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0dd4, code lost:
        r4 = new java.lang.StringBuilder();
        r7 = 0;
        r4.append(r0.subSequence(0, 20));
        r4.append("...");
        r0 = r4.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:623:0x0de9, code lost:
        r7 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0dea, code lost:
        r2 = new java.lang.Object[2];
        r2[r7] = r1;
        r2[1] = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:625:0x0df6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:627:0x0e06, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:629:0x0e16, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0e17, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:631:0x0e1d, code lost:
        if (r0 == null) goto L_0x0e30;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:633:0x0e2f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:635:0x0e3d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:637:0x0e40, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) == false) goto L_0x0e95;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0e42, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r5).emoticon;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:639:0x0e4a, code lost:
        if (android.text.TextUtils.isEmpty(r0) == false) goto L_0x0e70;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:641:0x0e4e, code lost:
        if (r2 != r17) goto L_0x0e5d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0e74, code lost:
        if (r2 != r17) goto L_0x0e84;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:649:0x0e97, code lost:
        if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest) == false) goto L_0x0ea0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:651:0x0e9f, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0ea0, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0eaa, code lost:
        if (r4.peer_id.channel_id == 0) goto L_0x0eda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0eae, code lost:
        if (r6.megagroup != false) goto L_0x0eda;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0eb4, code lost:
        if (r23.isVideoAvatar() == false) goto L_0x0ec8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0ec7, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0ed9, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0edf, code lost:
        if (r23.isVideoAvatar() == false) goto L_0x0ef5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0ef4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r1, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0f0f, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0f1f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", NUM, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0var_, code lost:
        if (r23.isMediaEmpty() == false) goto L_0x0f3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0f2e, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0var_, code lost:
        return replaceSpoilers(r23);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0f3d, code lost:
        return org.telegram.messenger.LocaleController.getString(r13, NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:681:0x0f3e, code lost:
        r1 = r13;
        r2 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0var_, code lost:
        if ((r2.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0f4b, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r2.message) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0var_, code lost:
        return "🖼 " + replaceSpoilers(r23);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0f6f, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0f7b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0f7a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0var_, code lost:
        if (r23.isVideo() == false) goto L_0x0fcb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0f8f, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0faf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0faf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0fae, code lost:
        return "📹 " + replaceSpoilers(r23);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0fb5, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0fc1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0fc0, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0fca, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0fcf, code lost:
        if (r23.isGame() == false) goto L_0x0fdb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0fda, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0fdf, code lost:
        if (r23.isVoice() == false) goto L_0x0feb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0fea, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0fef, code lost:
        if (r23.isRoundVideo() == false) goto L_0x0ffb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0ffa, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0fff, code lost:
        if (r23.isMusic() == false) goto L_0x100b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x100a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:725:0x100b, code lost:
        r2 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x1011, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x101d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x101c, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x101f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x103d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x1027, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2).poll.quiz == false) goto L_0x1033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x1032, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x103c, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x103f, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x110b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x1043, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x1047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x1049, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x1055;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x1054, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x1057, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x10f6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:748:0x105d, code lost:
        if (r23.isSticker() != false) goto L_0x10c8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:750:0x1063, code lost:
        if (r23.isAnimatedSticker() == false) goto L_0x1066;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:752:0x106a, code lost:
        if (r23.isGif() == false) goto L_0x109a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:754:0x1070, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x1090;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:756:0x107a, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x1090;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:758:0x108f, code lost:
        return "🎬 " + replaceSpoilers(r23);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:760:0x1099, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:762:0x109e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x10be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:764:0x10a8, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x10be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:766:0x10bd, code lost:
        return "📎 " + replaceSpoilers(r23);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:768:0x10c7, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:769:0x10c8, code lost:
        r0 = r23.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:770:0x10cc, code lost:
        if (r0 == null) goto L_0x10ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:772:0x10eb, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:774:0x10f5, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:776:0x10fc, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x1103;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:778:0x1102, code lost:
        return replaceSpoilers(r23);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:780:0x110a, code lost:
        return org.telegram.messenger.LocaleController.getString(r1, NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:782:0x1114, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:796:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChangedChatThemeTo", NUM, r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:797:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChatThemeDisabledYou", NUM, new java.lang.Object[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:798:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChatThemeDisabled", NUM, r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:799:?, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChangedChatThemeYou", NUM, r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getShortStringForMessage(org.telegram.messenger.MessageObject r23, java.lang.String[] r24, boolean[] r25) {
        /*
            r22 = this;
            r0 = r23
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x1123
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x1123
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
            if (r25 == 0) goto L_0x0027
            r25[r10] = r1
        L_0x0027:
            org.telegram.messenger.AccountInstance r11 = r22.getAccountInstance()
            android.content.SharedPreferences r11 = r11.getNotificationsSettings()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "content_preview_"
            r12.append(r13)
            r12.append(r2)
            java.lang.String r12 = r12.toString()
            boolean r12 = r11.getBoolean(r12, r1)
            boolean r13 = r23.isFcmMessage()
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
            r24[r10] = r1
        L_0x005f:
            if (r12 == 0) goto L_0x006a
            java.lang.String r1 = "EnablePreviewAll"
            r2 = 1
            boolean r1 = r11.getBoolean(r1, r2)
            if (r1 != 0) goto L_0x00ef
        L_0x006a:
            if (r25 == 0) goto L_0x006e
            r25[r10] = r10
        L_0x006e:
            r0 = 2131626469(0x7f0e09e5, float:1.8880175E38)
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
            boolean r2 = r23.isSupergroup()
            if (r2 == 0) goto L_0x008b
            goto L_0x0094
        L_0x008b:
            int r2 = android.os.Build.VERSION.SDK_INT
            if (r2 <= r14) goto L_0x0098
            java.lang.String r2 = r0.localName
            r24[r10] = r2
            goto L_0x0098
        L_0x0094:
            java.lang.String r2 = r0.localUserName
            r24[r10] = r2
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
            if (r25 == 0) goto L_0x00b9
            r25[r10] = r10
        L_0x00b9:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer_id
            long r2 = r2.channel_id
            int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r4 == 0) goto L_0x00da
            boolean r2 = r23.isSupergroup()
            if (r2 != 0) goto L_0x00da
            r1 = 2131624856(0x7f0e0398, float:1.8876904E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r0 = r0.localName
            r2[r10] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00da:
            r2 = 2131626831(0x7f0e0b4f, float:1.888091E38)
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
            java.lang.String r0 = r22.replaceSpoilers(r23)
            return r0
        L_0x00f4:
            org.telegram.messenger.UserConfig r13 = r22.getUserConfig()
            long r17 = r13.getClientUserId()
            int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r13 != 0) goto L_0x010a
            long r8 = r23.getFromChatId()
            int r13 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r13 != 0) goto L_0x0112
            long r8 = -r4
            goto L_0x0112
        L_0x010a:
            int r13 = (r8 > r17 ? 1 : (r8 == r17 ? 0 : -1))
            if (r13 != 0) goto L_0x0112
            long r8 = r23.getFromChatId()
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
            int r19 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r19 <= 0) goto L_0x0160
            org.telegram.messenger.MessagesController r1 = r22.getMessagesController()
            java.lang.Long r13 = java.lang.Long.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r13)
            if (r1 == 0) goto L_0x015d
            java.lang.String r1 = org.telegram.messenger.UserObject.getUserName(r1)
            int r13 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r13 == 0) goto L_0x0152
            r24[r10] = r1
            goto L_0x015e
        L_0x0152:
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 <= r14) goto L_0x0159
            r24[r10] = r1
            goto L_0x015e
        L_0x0159:
            r13 = 0
            r24[r10] = r13
            goto L_0x015e
        L_0x015d:
            r1 = 0
        L_0x015e:
            r13 = r15
            goto L_0x0176
        L_0x0160:
            org.telegram.messenger.MessagesController r1 = r22.getMessagesController()
            r13 = r15
            long r14 = -r8
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r14)
            if (r1 == 0) goto L_0x0175
            java.lang.String r1 = r1.title
            r24[r10] = r1
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
            boolean r21 = org.telegram.messenger.DialogObject.isChatDialog(r14)
            if (r21 == 0) goto L_0x01c1
            org.telegram.messenger.MessagesController r6 = r22.getMessagesController()
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
            r6 = r24[r10]
            if (r6 == 0) goto L_0x01c1
            r24[r10] = r1
        L_0x01c1:
            if (r1 != 0) goto L_0x01c6
            r20 = 0
            return r20
        L_0x01c6:
            r6 = 0
            r20 = 0
            int r14 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r14 == 0) goto L_0x01f0
            org.telegram.messenger.MessagesController r6 = r22.getMessagesController()
            java.lang.Long r7 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            if (r6 != 0) goto L_0x01dd
            return r20
        L_0x01dd:
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r7 == 0) goto L_0x01f2
            boolean r7 = r6.megagroup
            if (r7 != 0) goto L_0x01f2
            int r7 = android.os.Build.VERSION.SDK_INT
            r14 = 27
            if (r7 > r14) goto L_0x01f2
            r24[r10] = r20
            goto L_0x01f2
        L_0x01f0:
            r6 = r20
        L_0x01f2:
            boolean r7 = org.telegram.messenger.DialogObject.isEncryptedDialog(r2)
            if (r7 == 0) goto L_0x0204
            r24[r10] = r20
            r0 = 2131626808(0x7f0e0b38, float:1.8880863E38)
            java.lang.String r1 = "NotificationHiddenMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0204:
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r7 == 0) goto L_0x0210
            boolean r7 = r6.megagroup
            if (r7 != 0) goto L_0x0210
            r7 = 1
            goto L_0x0211
        L_0x0210:
            r7 = 0
        L_0x0211:
            if (r12 == 0) goto L_0x1115
            r14 = 0
            int r12 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r12 != 0) goto L_0x0227
            int r12 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r12 == 0) goto L_0x0227
            java.lang.String r12 = "EnablePreviewAll"
            r10 = 1
            boolean r12 = r11.getBoolean(r12, r10)
            if (r12 != 0) goto L_0x0240
            goto L_0x0228
        L_0x0227:
            r10 = 1
        L_0x0228:
            int r12 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1))
            if (r12 == 0) goto L_0x1115
            if (r7 != 0) goto L_0x0236
            java.lang.String r4 = "EnablePreviewGroup"
            boolean r4 = r11.getBoolean(r4, r10)
            if (r4 != 0) goto L_0x0240
        L_0x0236:
            if (r7 == 0) goto L_0x1115
            java.lang.String r4 = "EnablePreviewChannel"
            boolean r4 = r11.getBoolean(r4, r10)
            if (r4 == 0) goto L_0x1115
        L_0x0240:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r7 = "🎬 "
            java.lang.String r10 = "📎 "
            java.lang.String r11 = "📹 "
            java.lang.String r12 = "🖼 "
            if (r5 == 0) goto L_0x0var_
            r5 = 0
            r15 = 0
            r24[r15] = r5
            org.telegram.tgnet.TLRPC$MessageAction r5 = r4.action
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r13 == 0) goto L_0x025f
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x025f:
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r13 != 0) goto L_0x0var_
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r13 == 0) goto L_0x0269
            goto L_0x0var_
        L_0x0269:
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r13 == 0) goto L_0x027d
            r0 = 2131626789(0x7f0e0b25, float:1.8880824E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x027d:
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            r15 = 3
            if (r13 == 0) goto L_0x02e1
            r1 = 2131629093(0x7f0e1425, float:1.8885497E38)
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
            r2 = 2131626858(0x7f0e0b6a, float:1.8880964E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r5 = r22.getUserConfig()
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
        L_0x02e1:
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r13 != 0) goto L_0x0var_
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r13 == 0) goto L_0x02eb
            goto L_0x0var_
        L_0x02eb:
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r13 == 0) goto L_0x0307
            boolean r0 = r5.video
            if (r0 == 0) goto L_0x02fd
            r0 = 2131624728(0x7f0e0318, float:1.8876644E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02fd:
            r0 = 2131624722(0x7f0e0312, float:1.8876632E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0307:
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r13 == 0) goto L_0x0426
            long r2 = r5.user_id
            r10 = 0
            int r4 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r4 != 0) goto L_0x032d
            java.util.ArrayList<java.lang.Long> r4 = r5.users
            int r4 = r4.size()
            r5 = 1
            if (r4 != r5) goto L_0x032d
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Long> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x032d:
            r4 = 0
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x03ce
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r10 = r0.channel_id
            int r0 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x0356
            boolean r0 = r6.megagroup
            if (r0 != 0) goto L_0x0356
            r0 = 2131624805(0x7f0e0365, float:1.88768E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0356:
            int r0 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r0 != 0) goto L_0x036f
            r0 = 2131626810(0x7f0e0b3a, float:1.8880867E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x036f:
            org.telegram.messenger.MessagesController r0 = r22.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x037f
            r2 = 0
            return r2
        L_0x037f:
            long r2 = r0.id
            int r4 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x03b3
            boolean r0 = r6.megagroup
            if (r0 == 0) goto L_0x039e
            r0 = 2131626795(0x7f0e0b2b, float:1.8880836E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x039e:
            r2 = 2
            r3 = 0
            r4 = 1
            r0 = 2131626794(0x7f0e0b2a, float:1.8880834E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x03b3:
            r3 = 0
            r4 = 1
            r2 = 2131626793(0x7f0e0b29, float:1.8880832E38)
            java.lang.Object[] r5 = new java.lang.Object[r15]
            r5[r3] = r1
            java.lang.String r1 = r6.title
            r5[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r5[r1] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r5)
            return r0
        L_0x03ce:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
        L_0x03d4:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Long> r4 = r4.users
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x040b
            org.telegram.messenger.MessagesController r4 = r22.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            java.util.ArrayList<java.lang.Long> r5 = r5.users
            java.lang.Object r5 = r5.get(r3)
            java.lang.Long r5 = (java.lang.Long) r5
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 == 0) goto L_0x0408
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r4)
            int r5 = r2.length()
            if (r5 == 0) goto L_0x0405
            java.lang.String r5 = ", "
            r2.append(r5)
        L_0x0405:
            r2.append(r4)
        L_0x0408:
            int r3 = r3 + 1
            goto L_0x03d4
        L_0x040b:
            r0 = 2131626793(0x7f0e0b29, float:1.8880832E38)
            java.lang.Object[] r3 = new java.lang.Object[r15]
            r4 = 0
            r3[r4] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r2.toString()
            r13 = 2
            r3[r13] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            return r0
        L_0x0426:
            r13 = 2
            boolean r14 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r14 == 0) goto L_0x043f
            r0 = 2131626797(0x7f0e0b2d, float:1.888084E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupCreatedCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x043f:
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            if (r13 == 0) goto L_0x044a
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x044a:
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r13 == 0) goto L_0x0512
            long r2 = r5.user_id
            r7 = 0
            int r4 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r4 != 0) goto L_0x0470
            java.util.ArrayList<java.lang.Long> r4 = r5.users
            int r4 = r4.size()
            r5 = 1
            if (r4 != r5) goto L_0x0470
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Long> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x0470:
            r4 = 0
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x04ba
            int r0 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r0 != 0) goto L_0x048f
            r0 = 2131626802(0x7f0e0b32, float:1.888085E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x048f:
            org.telegram.messenger.MessagesController r0 = r22.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x049f
            r2 = 0
            return r2
        L_0x049f:
            r2 = 2131626801(0x7f0e0b31, float:1.8880848E38)
            java.lang.Object[] r3 = new java.lang.Object[r15]
            r4 = 0
            r3[r4] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r3[r1] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x04ba:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
        L_0x04c0:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Long> r4 = r4.users
            int r4 = r4.size()
            if (r3 >= r4) goto L_0x04f7
            org.telegram.messenger.MessagesController r4 = r22.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            java.util.ArrayList<java.lang.Long> r5 = r5.users
            java.lang.Object r5 = r5.get(r3)
            java.lang.Long r5 = (java.lang.Long) r5
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 == 0) goto L_0x04f4
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r4)
            int r5 = r2.length()
            if (r5 == 0) goto L_0x04f1
            java.lang.String r5 = ", "
            r2.append(r5)
        L_0x04f1:
            r2.append(r4)
        L_0x04f4:
            int r3 = r3 + 1
            goto L_0x04c0
        L_0x04f7:
            r0 = 2131626801(0x7f0e0b31, float:1.8880848E38)
            java.lang.Object[] r3 = new java.lang.Object[r15]
            r4 = 0
            r3[r4] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r2.toString()
            r13 = 2
            r3[r13] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            return r0
        L_0x0512:
            r13 = 2
            boolean r14 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r14 == 0) goto L_0x052c
            r0 = 2131626811(0x7f0e0b3b, float:1.8880869E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            r14 = 0
            r2[r14] = r1
            java.lang.String r1 = r6.title
            r16 = 1
            r2[r16] = r1
            java.lang.String r1 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x052c:
            r14 = 0
            r16 = 1
            boolean r15 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r15 == 0) goto L_0x0545
            r0 = 2131626790(0x7f0e0b26, float:1.8880826E38)
            java.lang.Object[] r2 = new java.lang.Object[r13]
            r2[r14] = r1
            java.lang.String r1 = r5.title
            r2[r16] = r1
            java.lang.String r1 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0545:
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r13 != 0) goto L_0x0ea2
            boolean r13 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r13 == 0) goto L_0x054f
            goto L_0x0ea2
        L_0x054f:
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r4 == 0) goto L_0x05b9
            long r2 = r5.user_id
            int r4 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r4 != 0) goto L_0x056e
            r0 = 2131626804(0x7f0e0b34, float:1.8880855E38)
            r4 = 2
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r5 = 0
            r2[r5] = r1
            java.lang.String r1 = r6.title
            r7 = 1
            r2[r7] = r1
            java.lang.String r1 = "NotificationGroupKickYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x056e:
            r4 = 2
            r5 = 0
            r7 = 1
            int r10 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x0587
            r0 = 2131626805(0x7f0e0b35, float:1.8880857E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r5] = r1
            java.lang.String r1 = r6.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationGroupLeftMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0587:
            org.telegram.messenger.MessagesController r2 = r22.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            long r3 = r0.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            if (r0 != 0) goto L_0x059d
            r2 = 0
            return r2
        L_0x059d:
            r2 = 2131626803(0x7f0e0b33, float:1.8880853E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r3[r1] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x05b9:
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r4 == 0) goto L_0x05c4
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x05c4:
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x05cf
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x05cf:
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r4 == 0) goto L_0x05e5
            r0 = 2131624149(0x7f0e00d5, float:1.887547E38)
            r4 = 1
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r8 = 0
            r1[r8] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05e5:
            r4 = 1
            r8 = 0
            boolean r9 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r9 == 0) goto L_0x05fb
            r0 = 2131624149(0x7f0e00d5, float:1.887547E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r5.title
            r1[r8] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05fb:
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r4 == 0) goto L_0x0606
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0606:
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r4 == 0) goto L_0x0e3e
            java.lang.String r2 = "..."
            r3 = 20
            if (r6 == 0) goto L_0x090b
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r4 == 0) goto L_0x061a
            boolean r4 = r6.megagroup
            if (r4 == 0) goto L_0x090b
        L_0x061a:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0633
            r0 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            r4 = 2
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r5 = 0
            r2[r5] = r1
            java.lang.String r1 = r6.title
            r8 = 1
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0633:
            r4 = 2
            r5 = 0
            r8 = 1
            boolean r9 = r0.isMusic()
            if (r9 == 0) goto L_0x064e
            r0 = 2131626755(0x7f0e0b03, float:1.8880755E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r5] = r1
            java.lang.String r1 = r6.title
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x064e:
            boolean r4 = r0.isVideo()
            r5 = 2131626779(0x7f0e0b1b, float:1.8880804E38)
            java.lang.String r8 = "NotificationActionPinnedText"
            if (r4 == 0) goto L_0x06a4
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x068f
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x068f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = r6.title
            r7 = 2
            r2[r7] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            return r0
        L_0x068f:
            r3 = 0
            r4 = 1
            r7 = 2
            r0 = 2131626782(0x7f0e0b1e, float:1.888081E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x06a4:
            boolean r4 = r0.isGif()
            if (r4 == 0) goto L_0x06f5
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x06e0
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x06e0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            r7 = 1
            r2[r7] = r0
            java.lang.String r0 = r6.title
            r9 = 2
            r2[r9] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            return r0
        L_0x06e0:
            r4 = 0
            r7 = 1
            r9 = 2
            r0 = 2131626749(0x7f0e0afd, float:1.8880743E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x06f5:
            r4 = 0
            r7 = 1
            r9 = 2
            boolean r11 = r0.isVoice()
            if (r11 == 0) goto L_0x0710
            r0 = 2131626785(0x7f0e0b21, float:1.8880816E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0710:
            boolean r11 = r0.isRoundVideo()
            if (r11 == 0) goto L_0x0728
            r0 = 2131626770(0x7f0e0b12, float:1.8880786E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0728:
            boolean r4 = r0.isSticker()
            if (r4 != 0) goto L_0x08da
            boolean r4 = r0.isAnimatedSticker()
            if (r4 == 0) goto L_0x0736
            goto L_0x08da
        L_0x0736:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r4.media
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x0787
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0772
            java.lang.String r2 = r4.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0772
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = r6.title
            r7 = 2
            r2[r7] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            return r0
        L_0x0772:
            r3 = 0
            r4 = 1
            r7 = 2
            r0 = 2131626734(0x7f0e0aee, float:1.8880713E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0787:
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r9 != 0) goto L_0x08c5
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r9 == 0) goto L_0x0791
            goto L_0x08c5
        L_0x0791:
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r9 == 0) goto L_0x07aa
            r0 = 2131626745(0x7f0e0af9, float:1.8880735E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r9 = 0
            r2[r9] = r1
            java.lang.String r1 = r6.title
            r10 = 1
            r2[r10] = r1
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x07aa:
            r9 = 0
            r10 = 1
            boolean r11 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r11 == 0) goto L_0x07d0
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7
            r0 = 2131626731(0x7f0e0aeb, float:1.8880706E38)
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r9] = r1
            java.lang.String r1 = r6.title
            r2[r10] = r1
            java.lang.String r1 = r7.first_name
            java.lang.String r3 = r7.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r3)
            r3 = 2
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x07d0:
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r9 == 0) goto L_0x0810
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x07f6
            r2 = 2131626767(0x7f0e0b0f, float:1.888078E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            java.lang.String r1 = r6.title
            r5 = 1
            r3[r5] = r1
            java.lang.String r0 = r0.question
            r7 = 2
            r3[r7] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x07f6:
            r3 = 3
            r4 = 0
            r5 = 1
            r7 = 2
            r2 = 2131626764(0x7f0e0b0c, float:1.8880773E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            java.lang.String r1 = r6.title
            r3[r5] = r1
            java.lang.String r0 = r0.question
            r3[r7] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x0810:
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r9 == 0) goto L_0x085d
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0848
            java.lang.String r2 = r4.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0848
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r12)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            r9 = 1
            r2[r9] = r0
            java.lang.String r0 = r6.title
            r10 = 2
            r2[r10] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            return r0
        L_0x0848:
            r4 = 0
            r9 = 1
            r10 = 2
            r0 = 2131626761(0x7f0e0b09, float:1.8880767E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r9] = r1
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x085d:
            r4 = 0
            r9 = 1
            r10 = 2
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r7 == 0) goto L_0x0876
            r0 = 2131626737(0x7f0e0af1, float:1.8880719E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r4] = r1
            java.lang.String r1 = r6.title
            r2[r9] = r1
            java.lang.String r1 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0876:
            java.lang.CharSequence r4 = r0.messageText
            if (r4 == 0) goto L_0x08b0
            int r4 = r4.length()
            if (r4 <= 0) goto L_0x08b0
            java.lang.CharSequence r0 = r0.messageText
            int r4 = r0.length()
            if (r4 <= r3) goto L_0x089d
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r7 = 0
            java.lang.CharSequence r0 = r0.subSequence(r7, r3)
            r4.append(r0)
            r4.append(r2)
            java.lang.String r0 = r4.toString()
            goto L_0x089e
        L_0x089d:
            r7 = 0
        L_0x089e:
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = r6.title
            r3 = 2
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r5, r2)
            return r0
        L_0x08b0:
            r3 = 2
            r4 = 1
            r7 = 0
            r0 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x08c5:
            r3 = 2
            r4 = 1
            r7 = 0
            r0 = 2131626743(0x7f0e0af7, float:1.888073E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x08da:
            r4 = 1
            r7 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x08f8
            r2 = 2131626775(0x7f0e0b17, float:1.8880796E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r1
            java.lang.String r1 = r6.title
            r3[r4] = r1
            r5 = 2
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x08f8:
            r5 = 2
            r0 = 2131626773(0x7f0e0b15, float:1.8880792E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r7] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x090b:
            r4 = 1
            if (r6 == 0) goto L_0x0bbd
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0923
            r0 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r5 = 0
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0923:
            r5 = 0
            boolean r1 = r0.isMusic()
            if (r1 == 0) goto L_0x093a
            r0 = 2131626756(0x7f0e0b04, float:1.8880757E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x093a:
            boolean r1 = r0.isVideo()
            r4 = 2131626780(0x7f0e0b1c, float:1.8880806E38)
            java.lang.String r5 = "NotificationActionPinnedTextChannel"
            if (r1 == 0) goto L_0x098a
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0978
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0978
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
            r3 = 0
            r1[r3] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r4, r1)
            return r0
        L_0x0978:
            r2 = 1
            r3 = 0
            r0 = 2131626783(0x7f0e0b1f, float:1.8880812E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x098a:
            boolean r1 = r0.isGif()
            if (r1 == 0) goto L_0x09d5
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x09c3
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x09c3
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
            r7 = 0
            r1[r7] = r2
            r8 = 1
            r1[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r4, r1)
            return r0
        L_0x09c3:
            r7 = 0
            r8 = 1
            r0 = 2131626750(0x7f0e0afe, float:1.8880745E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09d5:
            r7 = 0
            r8 = 1
            boolean r1 = r0.isVoice()
            if (r1 == 0) goto L_0x09ed
            r0 = 2131626786(0x7f0e0b22, float:1.8880818E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09ed:
            boolean r1 = r0.isRoundVideo()
            if (r1 == 0) goto L_0x0a03
            r0 = 2131626771(0x7f0e0b13, float:1.8880788E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a03:
            boolean r1 = r0.isSticker()
            if (r1 != 0) goto L_0x0b91
            boolean r1 = r0.isAnimatedSticker()
            if (r1 == 0) goto L_0x0a11
            goto L_0x0b91
        L_0x0a11:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r1.media
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0a5c
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0a4a
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a4a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r3 = 0
            r1[r3] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r4, r1)
            return r0
        L_0x0a4a:
            r2 = 1
            r3 = 0
            r0 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a5c:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x0b7f
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x0a66
            goto L_0x0b7f
        L_0x0a66:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x0a7c
            r0 = 2131626746(0x7f0e0afa, float:1.8880737E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r8 = 0
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a7c:
            r8 = 0
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r9 == 0) goto L_0x0a9f
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7
            r0 = 2131626732(0x7f0e0aec, float:1.8880709E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r8] = r2
            java.lang.String r2 = r7.first_name
            java.lang.String r3 = r7.last_name
            java.lang.String r2 = org.telegram.messenger.ContactsController.formatName(r2, r3)
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a9f:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x0ad9
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0ac2
            r1 = 2131626768(0x7f0e0b10, float:1.8880782E38)
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
            return r0
        L_0x0ac2:
            r2 = 2
            r3 = 1
            r4 = 0
            r1 = 2131626765(0x7f0e0b0d, float:1.8880775E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r5 = r6.title
            r2[r4] = r5
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0ad9:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x0b20
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0b0e
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b0e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r12)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r8 = 0
            r1[r8] = r2
            r9 = 1
            r1[r9] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r4, r1)
            return r0
        L_0x0b0e:
            r8 = 0
            r9 = 1
            r0 = 2131626762(0x7f0e0b0a, float:1.888077E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            java.lang.String r2 = r6.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b20:
            r8 = 0
            r9 = 1
            boolean r1 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0b36
            r0 = 2131626738(0x7f0e0af2, float:1.888072E38)
            java.lang.Object[] r1 = new java.lang.Object[r9]
            java.lang.String r2 = r6.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b36:
            java.lang.CharSequence r1 = r0.messageText
            if (r1 == 0) goto L_0x0b6d
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0b6d
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            if (r1 <= r3) goto L_0x0b5d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r8 = 0
            java.lang.CharSequence r0 = r0.subSequence(r8, r3)
            r1.append(r0)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L_0x0b5e
        L_0x0b5d:
            r8 = 0
        L_0x0b5e:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r8] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r4, r1)
            return r0
        L_0x0b6d:
            r2 = 1
            r8 = 0
            r0 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b7f:
            r2 = 1
            r8 = 0
            r0 = 2131626744(0x7f0e0af8, float:1.8880733E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b91:
            r8 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0bac
            r1 = 2131626776(0x7f0e0b18, float:1.8880798E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r2[r8] = r3
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0bac:
            r4 = 1
            r0 = 2131626774(0x7f0e0b16, float:1.8880794E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = r6.title
            r1[r8] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bbd:
            r8 = 0
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0bd0
            r0 = 2131626760(0x7f0e0b08, float:1.8880765E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0bd0:
            boolean r5 = r0.isMusic()
            if (r5 == 0) goto L_0x0be4
            r0 = 2131626757(0x7f0e0b05, float:1.888076E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedMusicUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0be4:
            boolean r4 = r0.isVideo()
            r5 = 2131626781(0x7f0e0b1d, float:1.8880808E38)
            java.lang.String r6 = "NotificationActionPinnedTextUser"
            if (r4 == 0) goto L_0x0CLASSNAME
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r11)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            return r0
        L_0x0CLASSNAME:
            r3 = 0
            r4 = 1
            r0 = 2131626784(0x7f0e0b20, float:1.8880814E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedVideoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0CLASSNAME:
            boolean r4 = r0.isGif()
            if (r4 == 0) goto L_0x0CLASSNAME
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            r7 = 1
            r2[r7] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            return r0
        L_0x0CLASSNAME:
            r4 = 0
            r7 = 1
            r0 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedGifUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0CLASSNAME:
            r4 = 0
            r7 = 1
            boolean r8 = r0.isVoice()
            if (r8 == 0) goto L_0x0c8d
            r0 = 2131626787(0x7f0e0b23, float:1.888082E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedVoiceUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0c8d:
            boolean r8 = r0.isRoundVideo()
            if (r8 == 0) goto L_0x0ca1
            r0 = 2131626772(0x7f0e0b14, float:1.888079E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedRoundUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0ca1:
            boolean r4 = r0.isSticker()
            if (r4 != 0) goto L_0x0e17
            boolean r4 = r0.isAnimatedSticker()
            if (r4 == 0) goto L_0x0caf
            goto L_0x0e17
        L_0x0caf:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r4.media
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0cf6
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0ce6
            java.lang.String r2 = r4.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0ce6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r4 = 1
            r2[r4] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            return r0
        L_0x0ce6:
            r3 = 0
            r4 = 1
            r0 = 2131626736(0x7f0e0af0, float:1.8880717E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedFileUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0cf6:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x0e07
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x0d00
            goto L_0x0e07
        L_0x0d00:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x0d14
            r0 = 2131626747(0x7f0e0afb, float:1.8880739E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r8 = 0
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedGeoLiveUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0d14:
            r8 = 0
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r9 == 0) goto L_0x0d35
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r7
            r0 = 2131626733(0x7f0e0aed, float:1.888071E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r8] = r1
            java.lang.String r1 = r7.first_name
            java.lang.String r3 = r7.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r3)
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedContactUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0d35:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x0d6b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x0d56
            r2 = 2131626769(0x7f0e0b11, float:1.8880784E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x0d56:
            r3 = 2
            r4 = 0
            r5 = 1
            r2 = 2131626766(0x7f0e0b0e, float:1.8880777E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPollUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            return r0
        L_0x0d6b:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x0dae
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0d9e
            java.lang.String r2 = r4.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0d9e
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r12)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r4 = 0
            r2[r4] = r1
            r8 = 1
            r2[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            return r0
        L_0x0d9e:
            r4 = 0
            r8 = 1
            r0 = 2131626763(0x7f0e0b0b, float:1.8880771E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedPhotoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0dae:
            r4 = 0
            r8 = 1
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r7 == 0) goto L_0x0dc2
            r0 = 2131626742(0x7f0e0af6, float:1.8880729E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r4] = r1
            java.lang.String r1 = "NotificationActionPinnedGameUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0dc2:
            java.lang.CharSequence r4 = r0.messageText
            if (r4 == 0) goto L_0x0df7
            int r4 = r4.length()
            if (r4 <= 0) goto L_0x0df7
            java.lang.CharSequence r0 = r0.messageText
            int r4 = r0.length()
            if (r4 <= r3) goto L_0x0de9
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r7 = 0
            java.lang.CharSequence r0 = r0.subSequence(r7, r3)
            r4.append(r0)
            r4.append(r2)
            java.lang.String r0 = r4.toString()
            goto L_0x0dea
        L_0x0de9:
            r7 = 0
        L_0x0dea:
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r1
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            return r0
        L_0x0df7:
            r3 = 1
            r7 = 0
            r0 = 2131626760(0x7f0e0b08, float:1.8880765E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0e07:
            r3 = 1
            r7 = 0
            r0 = 2131626748(0x7f0e0afc, float:1.888074E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedGeoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0e17:
            r3 = 1
            r7 = 0
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0e30
            r2 = 2131626777(0x7f0e0b19, float:1.88808E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r7] = r1
            r4[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            return r0
        L_0x0e30:
            r0 = 2131626778(0x7f0e0b1a, float:1.8880802E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedStickerUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0e3e:
            boolean r4 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r4 == 0) goto L_0x0e95
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r5 = (org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r5
            java.lang.String r0 = r5.emoticon
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 == 0) goto L_0x0e70
            int r4 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r4 != 0) goto L_0x0e5d
            r0 = 2131624958(0x7f0e03fe, float:1.887711E38)
            r4 = 0
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = "ChatThemeDisabledYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0e94
        L_0x0e5d:
            r4 = 0
            r2 = 2131624957(0x7f0e03fd, float:1.8877108E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "ChatThemeDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x0e94
        L_0x0e70:
            r4 = 0
            r5 = 1
            int r6 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r6 != 0) goto L_0x0e84
            r1 = 2131624955(0x7f0e03fb, float:1.8877104E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r0
            java.lang.String r0 = "ChangedChatThemeYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0e94
        L_0x0e84:
            r2 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            r3[r5] = r0
            java.lang.String r0 = "ChangedChatThemeTo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
        L_0x0e94:
            return r0
        L_0x0e95:
            boolean r1 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest
            if (r1 == 0) goto L_0x0ea0
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0ea0:
            r0 = 0
            return r0
        L_0x0ea2:
            org.telegram.tgnet.TLRPC$Peer r2 = r4.peer_id
            long r2 = r2.channel_id
            r4 = 0
            int r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x0eda
            boolean r2 = r6.megagroup
            if (r2 != 0) goto L_0x0eda
            boolean r0 = r23.isVideoAvatar()
            if (r0 == 0) goto L_0x0ec8
            r0 = 2131624907(0x7f0e03cb, float:1.8877007E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0ec8:
            r1 = 1
            r3 = 0
            r0 = 2131624872(0x7f0e03a8, float:1.8876936E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0eda:
            r3 = 0
            boolean r0 = r23.isVideoAvatar()
            if (r0 == 0) goto L_0x0ef5
            r0 = 2131626792(0x7f0e0b28, float:1.888083E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r4 = 1
            r2[r4] = r1
            java.lang.String r1 = "NotificationEditedGroupVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0ef5:
            r2 = 2
            r4 = 1
            r0 = 2131626791(0x7f0e0b27, float:1.8880828E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r6.title
            r2[r4] = r1
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0var_:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0var_:
            r4 = 1
            r0 = 2131626788(0x7f0e0b24, float:1.8880822E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            return r0
        L_0x0var_:
            boolean r1 = r23.isMediaEmpty()
            if (r1 == 0) goto L_0x0f3e
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
            java.lang.String r0 = r22.replaceSpoilers(r23)
            return r0
        L_0x0var_:
            r1 = r13
            r0 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f3e:
            r1 = r13
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
            java.lang.String r0 = r22.replaceSpoilers(r23)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0f7b
            r0 = 2131624420(0x7f0e01e4, float:1.887602E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f7b:
            r0 = 2131624443(0x7f0e01fb, float:1.8876066E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r2 = r23.isVideo()
            if (r2 == 0) goto L_0x0fcb
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0faf
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0faf
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r11)
            java.lang.String r0 = r22.replaceSpoilers(r23)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0faf:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0fc1
            r0 = 2131624421(0x7f0e01e5, float:1.8876021E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fc1:
            r0 = 2131624449(0x7f0e0201, float:1.8876078E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fcb:
            boolean r2 = r23.isGame()
            if (r2 == 0) goto L_0x0fdb
            r0 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fdb:
            boolean r2 = r23.isVoice()
            if (r2 == 0) goto L_0x0feb
            r0 = 2131624417(0x7f0e01e1, float:1.8876013E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0feb:
            boolean r2 = r23.isRoundVideo()
            if (r2 == 0) goto L_0x0ffb
            r0 = 2131624445(0x7f0e01fd, float:1.887607E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ffb:
            boolean r2 = r23.isMusic()
            if (r2 == 0) goto L_0x100b
            r0 = 2131624442(0x7f0e01fa, float:1.8876064E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x100b:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x101d
            r0 = 2131624419(0x7f0e01e3, float:1.8876017E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x101d:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x103d
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x1033
            r0 = 2131627630(0x7f0e0e6e, float:1.888253E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x1033:
            r0 = 2131627414(0x7f0e0d96, float:1.8882092E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x103d:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x110b
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x1047
            goto L_0x110b
        L_0x1047:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x1055
            r0 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x1055:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x10f6
            boolean r1 = r23.isSticker()
            if (r1 != 0) goto L_0x10c8
            boolean r1 = r23.isAnimatedSticker()
            if (r1 == 0) goto L_0x1066
            goto L_0x10c8
        L_0x1066:
            boolean r1 = r23.isGif()
            if (r1 == 0) goto L_0x109a
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x1090
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1090
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            java.lang.String r0 = r22.replaceSpoilers(r23)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x1090:
            r0 = 2131624424(0x7f0e01e8, float:1.8876027E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x109a:
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x10be
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x10be
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r10)
            java.lang.String r0 = r22.replaceSpoilers(r23)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x10be:
            r0 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x10c8:
            java.lang.String r0 = r23.getStickerEmoji()
            if (r0 == 0) goto L_0x10ec
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            java.lang.String r0 = " "
            r1.append(r0)
            r0 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x10ec:
            r0 = 2131624446(0x7f0e01fe, float:1.8876072E38)
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x10f6:
            java.lang.CharSequence r2 = r0.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1103
            java.lang.String r0 = r22.replaceSpoilers(r23)
            return r0
        L_0x1103:
            r0 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x110b:
            r0 = 2131624433(0x7f0e01f1, float:1.8876046E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x1115:
            r1 = r13
            if (r25 == 0) goto L_0x111b
            r0 = 0
            r25[r0] = r0
        L_0x111b:
            r0 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x1123:
            r0 = 2131626808(0x7f0e0b38, float:1.8880863E38)
            java.lang.String r1 = "NotificationHiddenMessage"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
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
        jadx.core.utils.exceptions.JadxRuntimeException: Several immutable types in one variable: [int, boolean], vars: [r5v92 ?, r5v91 ?, r5v93 ?, r5v94 ?]
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVarType(InitCodeVariables.java:102)
        	at jadx.core.dex.visitors.InitCodeVariables.setCodeVar(InitCodeVariables.java:78)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVar(InitCodeVariables.java:69)
        	at jadx.core.dex.visitors.InitCodeVariables.initCodeVars(InitCodeVariables.java:51)
        	at jadx.core.dex.visitors.InitCodeVariables.visit(InitCodeVariables.java:32)
        */
    private java.lang.String getStringForMessage(org.telegram.messenger.MessageObject r27, boolean r28, boolean[] r29, boolean[] r30) {
        /*
            r26 = this;
            r0 = r27
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x161a
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x161a
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
            r0 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
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
            r2 = 2131624856(0x7f0e0398, float:1.8876904E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r0 = r0.localName
            r3 = 0
            r1[r3] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r1)
            return r0
        L_0x00b8:
            r3 = 0
            r2 = 2131626831(0x7f0e0b4f, float:1.888091E38)
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
            r10 = 2131626494(0x7f0e09fe, float:1.8880226E38)
            java.lang.String r1 = "MessageScheduledReminderNotification"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r10)
            goto L_0x0149
        L_0x0119:
            r1 = 2131626852(0x7f0e0b64, float:1.8880952E38)
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
            r0 = 2131629035(0x7f0e13eb, float:1.888538E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1619
        L_0x0173:
            java.lang.String r7 = "🎬 "
            java.lang.String r10 = "📎 "
            java.lang.String r13 = "📹 "
            r22 = r14
            java.lang.String r14 = "🖼 "
            r23 = r6
            java.lang.String r6 = "NotificationMessageText"
            r24 = r10
            r10 = 3
            r20 = 0
            int r25 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1))
            if (r25 != 0) goto L_0x05ec
            int r25 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1))
            if (r25 == 0) goto L_0x05ec
            if (r12 == 0) goto L_0x05d9
            java.lang.String r4 = "EnablePreviewAll"
            r5 = 1
            boolean r4 = r11.getBoolean(r4, r5)
            if (r4 == 0) goto L_0x05d9
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r5 == 0) goto L_0x02cd
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r5 == 0) goto L_0x01ad
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x1619
        L_0x01ad:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r5 != 0) goto L_0x02bc
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r5 == 0) goto L_0x01b7
            goto L_0x02bc
        L_0x01b7:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r5 == 0) goto L_0x01cc
            r0 = 2131626789(0x7f0e0b25, float:1.8880824E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = "NotificationContactNewPhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x01cc:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r5 == 0) goto L_0x0230
            r1 = 2131629093(0x7f0e1425, float:1.8885497E38)
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
            r2 = 2131626858(0x7f0e0b6a, float:1.8880964E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.messenger.UserConfig r5 = r26.getUserConfig()
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
            r3[r10] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x1619
        L_0x0230:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r5 != 0) goto L_0x02b4
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r5 == 0) goto L_0x023a
            goto L_0x02b4
        L_0x023a:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x0258
            boolean r0 = r4.video
            if (r0 == 0) goto L_0x024d
            r0 = 2131624728(0x7f0e0318, float:1.8876644E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1619
        L_0x024d:
            r0 = 2131624722(0x7f0e0312, float:1.8876632E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1619
        L_0x0258:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r0 == 0) goto L_0x1617
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r4 = (org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r4
            java.lang.String r0 = r4.emoticon
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 == 0) goto L_0x028b
            int r4 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r4 != 0) goto L_0x0278
            r0 = 2131624958(0x7f0e03fe, float:1.887711E38)
            r4 = 0
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = "ChatThemeDisabledYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r5 = 1
            goto L_0x02af
        L_0x0278:
            r4 = 0
            r2 = 2131624957(0x7f0e03fd, float:1.8877108E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "ChatThemeDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x02af
        L_0x028b:
            r4 = 0
            r5 = 1
            int r6 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r6 != 0) goto L_0x029f
            r1 = 2131624955(0x7f0e03fb, float:1.8877104E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r0
            java.lang.String r0 = "ChangedChatThemeYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x02af
        L_0x029f:
            r2 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            r3[r5] = r0
            java.lang.String r0 = "ChangedChatThemeTo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
        L_0x02af:
            r13 = r0
            r29[r4] = r5
            goto L_0x1619
        L_0x02b4:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x1619
        L_0x02bc:
            r4 = 0
            r5 = 1
            r0 = 2131626788(0x7f0e0b24, float:1.8880822E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r1
            java.lang.String r1 = "NotificationContactJoined"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x02cd:
            boolean r2 = r27.isMediaEmpty()
            if (r2 == 0) goto L_0x0315
            if (r28 != 0) goto L_0x0306
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x02f7
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x02f7:
            r3 = 0
            r4 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            r0[r3] = r1
            r2 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r15, r2, r0)
            goto L_0x1619
        L_0x0306:
            r2 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            r3 = 0
            r4 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            r0[r3] = r1
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r15, r2, r0)
            goto L_0x1619
        L_0x0315:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x037c
            if (r28 != 0) goto L_0x0354
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r3 < r4) goto L_0x0354
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0354
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x0354:
            r3 = 0
            r4 = 1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x036d
            r0 = 2131626849(0x7f0e0b61, float:1.8880946E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageSDPhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x036d:
            r0 = 2131626845(0x7f0e0b5d, float:1.8880938E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessagePhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x037c:
            boolean r2 = r27.isVideo()
            if (r2 == 0) goto L_0x03e3
            if (r28 != 0) goto L_0x03bb
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x03bb
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x03bb
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x03bb:
            r3 = 0
            r4 = 1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x03d4
            r0 = 2131626850(0x7f0e0b62, float:1.8880948E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageSDVideo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x03d4:
            r0 = 2131626856(0x7f0e0b68, float:1.888096E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageVideo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x03e3:
            r3 = 0
            boolean r2 = r27.isGame()
            if (r2 == 0) goto L_0x0405
            r2 = 2131626818(0x7f0e0b42, float:1.8880883E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r0 = r0.title
            r3 = 1
            r4[r3] = r0
            java.lang.String r0 = "NotificationMessageGame"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
            goto L_0x1619
        L_0x0405:
            r3 = 1
            boolean r2 = r27.isVoice()
            if (r2 == 0) goto L_0x041c
            r0 = 2131626813(0x7f0e0b3d, float:1.8880873E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r4 = 0
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageAudio"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x041c:
            r4 = 0
            boolean r2 = r27.isRoundVideo()
            if (r2 == 0) goto L_0x0432
            r0 = 2131626848(0x7f0e0b60, float:1.8880944E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageRound"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x0432:
            boolean r2 = r27.isMusic()
            if (r2 == 0) goto L_0x0447
            r0 = 2131626843(0x7f0e0b5b, float:1.8880934E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r4] = r1
            java.lang.String r1 = "NotificationMessageMusic"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x0447:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x046c
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r2
            r0 = 2131626814(0x7f0e0b3e, float:1.8880875E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            java.lang.String r1 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r2)
            r2 = 1
            r3[r2] = r1
            java.lang.String r1 = "NotificationMessageContact2"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x1619
        L_0x046c:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x04a4
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x048d
            r2 = 2131626847(0x7f0e0b5f, float:1.8880942E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x048d:
            r3 = 2
            r4 = 0
            r5 = 1
            r2 = 2131626846(0x7f0e0b5e, float:1.888094E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
        L_0x04a1:
            r13 = r0
            goto L_0x1619
        L_0x04a4:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x05c8
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x04ae
            goto L_0x05c8
        L_0x04ae:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x04c3
            r0 = 2131626841(0x7f0e0b59, float:1.888093E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageLiveLocation"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x04c3:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x059a
            boolean r2 = r27.isSticker()
            if (r2 != 0) goto L_0x0571
            boolean r2 = r27.isAnimatedSticker()
            if (r2 == 0) goto L_0x04d5
            goto L_0x0571
        L_0x04d5:
            boolean r2 = r27.isGif()
            if (r2 == 0) goto L_0x0525
            if (r28 != 0) goto L_0x0514
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0514
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0514
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x0514:
            r3 = 0
            r4 = 1
            r0 = 2131626820(0x7f0e0b44, float:1.8880887E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGif"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x0525:
            if (r28 != 0) goto L_0x0560
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0560
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0560
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r15 = r24
            r1.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x0560:
            r3 = 0
            r4 = 1
            r0 = 2131626815(0x7f0e0b3f, float:1.8880877E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageDocument"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x0571:
            r3 = 0
            r4 = 1
            java.lang.String r0 = r27.getStickerEmoji()
            if (r0 == 0) goto L_0x058b
            r2 = 2131626854(0x7f0e0b66, float:1.8880956E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r3] = r1
            r5[r4] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r5)
            goto L_0x04a1
        L_0x058b:
            r0 = 2131626853(0x7f0e0b65, float:1.8880954E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x059a:
            r3 = 0
            if (r28 != 0) goto L_0x05ba
            java.lang.CharSequence r2 = r0.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x05ba
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.CharSequence r0 = r0.messageText
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x05ba:
            r4 = 1
            java.lang.Object[] r0 = new java.lang.Object[r4]
            r0[r3] = r1
            r1 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r15, r1, r0)
            goto L_0x1619
        L_0x05c8:
            r3 = 0
            r4 = 1
            r0 = 2131626842(0x7f0e0b5a, float:1.8880932E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageMap"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x05d9:
            r3 = 0
            r4 = 1
            if (r30 == 0) goto L_0x05df
            r30[r3] = r3
        L_0x05df:
            java.lang.Object[] r0 = new java.lang.Object[r4]
            r0[r3] = r1
            r1 = 2131626844(0x7f0e0b5c, float:1.8880936E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r15, r1, r0)
            goto L_0x1619
        L_0x05ec:
            r15 = r24
            r20 = 0
            int r16 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1))
            if (r16 == 0) goto L_0x1617
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r23)
            if (r4 == 0) goto L_0x0602
            r4 = r23
            boolean r5 = r4.megagroup
            if (r5 != 0) goto L_0x0604
            r5 = 1
            goto L_0x0605
        L_0x0602:
            r4 = r23
        L_0x0604:
            r5 = 0
        L_0x0605:
            if (r12 == 0) goto L_0x15e5
            if (r5 != 0) goto L_0x0613
            java.lang.String r12 = "EnablePreviewGroup"
            r10 = 1
            boolean r12 = r11.getBoolean(r12, r10)
            if (r12 != 0) goto L_0x061e
            goto L_0x0614
        L_0x0613:
            r10 = 1
        L_0x0614:
            if (r5 == 0) goto L_0x15e5
            java.lang.String r5 = "EnablePreviewChannel"
            boolean r5 = r11.getBoolean(r5, r10)
            if (r5 == 0) goto L_0x15e5
        L_0x061e:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            boolean r10 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r10 == 0) goto L_0x100c
            org.telegram.tgnet.TLRPC$MessageAction r6 = r5.action
            boolean r10 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r10 == 0) goto L_0x074d
            long r2 = r6.user_id
            r10 = 0
            int r5 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r5 != 0) goto L_0x064c
            java.util.ArrayList<java.lang.Long> r5 = r6.users
            int r5 = r5.size()
            r6 = 1
            if (r5 != r6) goto L_0x064c
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Long> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x064c:
            r5 = 0
            int r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x06f3
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r10 = r0.channel_id
            int r0 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x0676
            boolean r0 = r4.megagroup
            if (r0 != 0) goto L_0x0676
            r0 = 2131624805(0x7f0e0365, float:1.88768E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0676:
            int r0 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r0 != 0) goto L_0x0690
            r0 = 2131626810(0x7f0e0b3a, float:1.8880867E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0690:
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x06a0
            r2 = 0
            return r2
        L_0x06a0:
            long r2 = r0.id
            int r5 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1))
            if (r5 != 0) goto L_0x06d6
            boolean r0 = r4.megagroup
            if (r0 == 0) goto L_0x06c0
            r0 = 2131626795(0x7f0e0b2b, float:1.8880836E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r5 = 1
            r2[r5] = r1
            java.lang.String r1 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x06c0:
            r2 = 2
            r3 = 0
            r5 = 1
            r0 = 2131626794(0x7f0e0b2a, float:1.8880834E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r5] = r1
            java.lang.String r1 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x06d6:
            r3 = 0
            r5 = 1
            r2 = 2131626793(0x7f0e0b29, float:1.8880832E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r3] = r1
            java.lang.String r1 = r4.title
            r6[r5] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r6[r1] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r6)
            goto L_0x04a1
        L_0x06f3:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
        L_0x06f9:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            java.util.ArrayList<java.lang.Long> r5 = r5.users
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x0730
            org.telegram.messenger.MessagesController r5 = r26.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            java.lang.Object r6 = r6.get(r3)
            java.lang.Long r6 = (java.lang.Long) r6
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 == 0) goto L_0x072d
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r5)
            int r6 = r2.length()
            if (r6 == 0) goto L_0x072a
            java.lang.String r6 = ", "
            r2.append(r6)
        L_0x072a:
            r2.append(r5)
        L_0x072d:
            int r3 = r3 + 1
            goto L_0x06f9
        L_0x0730:
            r0 = 2131626793(0x7f0e0b29, float:1.8880832E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r5 = 0
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r2.toString()
            r10 = 2
            r3[r10] = r1
            java.lang.String r1 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x04a1
        L_0x074d:
            r10 = 2
            boolean r11 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r11 == 0) goto L_0x0767
            r0 = 2131626797(0x7f0e0b2d, float:1.888084E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupCreatedCall"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x0767:
            boolean r10 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            if (r10 == 0) goto L_0x0773
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x1619
        L_0x0773:
            boolean r10 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r10 == 0) goto L_0x0840
            long r2 = r6.user_id
            r7 = 0
            int r5 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x0799
            java.util.ArrayList<java.lang.Long> r5 = r6.users
            int r5 = r5.size()
            r6 = 1
            if (r5 != r6) goto L_0x0799
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Long> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x0799:
            r5 = 0
            int r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x07e6
            int r0 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r0 != 0) goto L_0x07b9
            r0 = 2131626802(0x7f0e0b32, float:1.888085E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x07b9:
            org.telegram.messenger.MessagesController r0 = r26.getMessagesController()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x07c9
            r2 = 0
            return r2
        L_0x07c9:
            r2 = 2131626801(0x7f0e0b31, float:1.8880848E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r5 = 0
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r3[r1] = r0
            java.lang.String r0 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x07e6:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
        L_0x07ec:
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r5 = r5.action
            java.util.ArrayList<java.lang.Long> r5 = r5.users
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x0823
            org.telegram.messenger.MessagesController r5 = r26.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            java.util.ArrayList<java.lang.Long> r6 = r6.users
            java.lang.Object r6 = r6.get(r3)
            java.lang.Long r6 = (java.lang.Long) r6
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 == 0) goto L_0x0820
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r5)
            int r6 = r2.length()
            if (r6 == 0) goto L_0x081d
            java.lang.String r6 = ", "
            r2.append(r6)
        L_0x081d:
            r2.append(r5)
        L_0x0820:
            int r3 = r3 + 1
            goto L_0x07ec
        L_0x0823:
            r0 = 2131626801(0x7f0e0b31, float:1.8880848E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r5 = 0
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r2.toString()
            r10 = 2
            r3[r10] = r1
            java.lang.String r1 = "NotificationGroupInvitedToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x04a1
        L_0x0840:
            r10 = 2
            boolean r11 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r11 == 0) goto L_0x085a
            r0 = 2131626811(0x7f0e0b3b, float:1.8880869E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r11 = 0
            r2[r11] = r1
            java.lang.String r1 = r4.title
            r12 = 1
            r2[r12] = r1
            java.lang.String r1 = "NotificationInvitedToGroupByLink"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x085a:
            r11 = 0
            boolean r12 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r12 == 0) goto L_0x0873
            r0 = 2131626790(0x7f0e0b26, float:1.8880826E38)
            java.lang.Object[] r2 = new java.lang.Object[r10]
            r2[r11] = r1
            java.lang.String r1 = r6.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationEditedGroupName"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x0873:
            boolean r10 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r10 != 0) goto L_0x0fa1
            boolean r10 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r10 == 0) goto L_0x087d
            goto L_0x0fa1
        L_0x087d:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r5 == 0) goto L_0x08ea
            long r2 = r6.user_id
            int r5 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r5 != 0) goto L_0x089d
            r0 = 2131626804(0x7f0e0b34, float:1.8880855E38)
            r5 = 2
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r6 = 0
            r2[r6] = r1
            java.lang.String r1 = r4.title
            r7 = 1
            r2[r7] = r1
            java.lang.String r1 = "NotificationGroupKickYou"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x089d:
            r5 = 2
            r6 = 0
            r7 = 1
            int r10 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x08b7
            r0 = 2131626805(0x7f0e0b35, float:1.8880857E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r6] = r1
            java.lang.String r1 = r4.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationGroupLeftMember"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x08b7:
            org.telegram.messenger.MessagesController r2 = r26.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            long r5 = r0.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r0 = r2.getUser(r0)
            if (r0 != 0) goto L_0x08cd
            r8 = 0
            return r8
        L_0x08cd:
            r2 = 2131626803(0x7f0e0b33, float:1.8880853E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r5 = 0
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r1 = 2
            r3[r1] = r0
            java.lang.String r0 = "NotificationGroupKickMember"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x1619
        L_0x08ea:
            r8 = 0
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r5 == 0) goto L_0x08f7
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x1619
        L_0x08f7:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r5 == 0) goto L_0x0903
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x1619
        L_0x0903:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r5 == 0) goto L_0x091a
            r0 = 2131624149(0x7f0e00d5, float:1.887547E38)
            r5 = 1
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r4.title
            r9 = 0
            r1[r9] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1619
        L_0x091a:
            r5 = 1
            r9 = 0
            boolean r10 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r10 == 0) goto L_0x0931
            r0 = 2131624149(0x7f0e00d5, float:1.887547E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r6.title
            r1[r9] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1619
        L_0x0931:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r5 == 0) goto L_0x093d
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x1619
        L_0x093d:
            boolean r5 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r5 == 0) goto L_0x0f2e
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r2 == 0) goto L_0x0c1e
            boolean r2 = r4.megagroup
            if (r2 == 0) goto L_0x094d
            goto L_0x0c1e
        L_0x094d:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x0964
            r0 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            r2 = 1
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1619
        L_0x0964:
            r2 = 1
            r3 = 0
            boolean r5 = r1.isMusic()
            if (r5 == 0) goto L_0x097d
            r0 = 2131626756(0x7f0e0b04, float:1.8880757E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x097d:
            boolean r2 = r1.isVideo()
            r3 = 2131626780(0x7f0e0b1c, float:1.8880806E38)
            java.lang.String r5 = "NotificationActionPinnedTextChannel"
            if (r2 == 0) goto L_0x09cf
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x09bc
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09bc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r4.title
            r6 = 0
            r1[r6] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r3, r1)
            goto L_0x04a1
        L_0x09bc:
            r2 = 1
            r6 = 0
            r0 = 2131626783(0x7f0e0b1f, float:1.8880812E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x09cf:
            boolean r2 = r1.isGif()
            if (r2 == 0) goto L_0x0a1c
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0a09
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0a09
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r4.title
            r6 = 0
            r1[r6] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r3, r1)
            goto L_0x04a1
        L_0x0a09:
            r2 = 1
            r6 = 0
            r0 = 2131626750(0x7f0e0afe, float:1.8880745E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0a1c:
            r2 = 1
            r6 = 0
            boolean r7 = r1.isVoice()
            if (r7 == 0) goto L_0x0a35
            r0 = 2131626786(0x7f0e0b22, float:1.8880818E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0a35:
            boolean r7 = r1.isRoundVideo()
            if (r7 == 0) goto L_0x0a4c
            r0 = 2131626771(0x7f0e0b13, float:1.8880788E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0a4c:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0bf0
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x0a5a
            goto L_0x0bf0
        L_0x0a5a:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r2.media
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r7 == 0) goto L_0x0aa7
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r0 < r6) goto L_0x0a94
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0a94
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r4.title
            r6 = 0
            r1[r6] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r3, r1)
            goto L_0x04a1
        L_0x0a94:
            r2 = 1
            r6 = 0
            r0 = 2131626735(0x7f0e0aef, float:1.8880715E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0aa7:
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r7 != 0) goto L_0x0bdd
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r7 == 0) goto L_0x0ab1
            goto L_0x0bdd
        L_0x0ab1:
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r7 == 0) goto L_0x0ac8
            r0 = 2131626746(0x7f0e0afa, float:1.8880737E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r4.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0ac8:
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x0af0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626732(0x7f0e0aec, float:1.8880709E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r4.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r3 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r3, r0)
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedContactChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x04a1
        L_0x0af0:
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0b2c
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r6 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r6
            org.telegram.tgnet.TLRPC$Poll r0 = r6.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0b14
            r1 = 2131626768(0x7f0e0b10, float:1.8880782E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r4.title
            r5 = 0
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x04a1
        L_0x0b14:
            r2 = 2
            r3 = 1
            r5 = 0
            r1 = 2131626765(0x7f0e0b0d, float:1.8880775E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r4.title
            r2[r5] = r4
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x04a1
        L_0x0b2c:
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0b75
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r0 < r6) goto L_0x0b62
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0b62
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.message
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r4.title
            r7 = 0
            r1[r7] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r3, r1)
            goto L_0x04a1
        L_0x0b62:
            r2 = 1
            r7 = 0
            r0 = 2131626762(0x7f0e0b0a, float:1.888077E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0b75:
            r2 = 1
            r7 = 0
            boolean r0 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0b8c
            r0 = 2131626738(0x7f0e0af2, float:1.888072E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0b8c:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0bca
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0bca
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0bb9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r6 = 0
            java.lang.CharSequence r0 = r0.subSequence(r6, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0bba
        L_0x0bb9:
            r6 = 0
        L_0x0bba:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r5, r3, r1)
            goto L_0x04a1
        L_0x0bca:
            r2 = 1
            r6 = 0
            r0 = 2131626759(0x7f0e0b07, float:1.8880763E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0bdd:
            r2 = 1
            r6 = 0
            r0 = 2131626744(0x7f0e0af8, float:1.8880733E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0bf0:
            r6 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0c0c
            r1 = 2131626776(0x7f0e0b18, float:1.8880798E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r4.title
            r2[r6] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x04a1
        L_0x0c0c:
            r3 = 1
            r0 = 2131626774(0x7f0e0b16, float:1.8880794E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r4.title
            r1[r6] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0c1e:
            r6 = 0
            org.telegram.messenger.MessageObject r2 = r0.replyMessageObject
            if (r2 != 0) goto L_0x0CLASSNAME
            r0 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            r3 = 2
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r6] = r1
            java.lang.String r1 = r4.title
            r5 = 1
            r2[r5] = r1
            java.lang.String r1 = "NotificationActionPinnedNoText"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x0CLASSNAME:
            r3 = 2
            r5 = 1
            boolean r8 = r2.isMusic()
            if (r8 == 0) goto L_0x0CLASSNAME
            r0 = 2131626755(0x7f0e0b03, float:1.8880755E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r6] = r1
            java.lang.String r1 = r4.title
            r2[r5] = r1
            java.lang.String r1 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0CLASSNAME:
            boolean r3 = r2.isVideo()
            r5 = 2131626779(0x7f0e0b1b, float:1.8880804E38)
            java.lang.String r6 = "NotificationActionPinnedText"
            if (r3 == 0) goto L_0x0cab
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r13)
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r7 = 1
            r2[r7] = r0
            java.lang.String r0 = r4.title
            r8 = 2
            r2[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            goto L_0x04a1
        L_0x0CLASSNAME:
            r3 = 0
            r7 = 1
            r8 = 2
            r0 = 2131626782(0x7f0e0b1e, float:1.888081E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0cab:
            boolean r3 = r2.isGif()
            if (r3 == 0) goto L_0x0cfe
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0ce8
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0ce8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r7 = 1
            r2[r7] = r0
            java.lang.String r0 = r4.title
            r8 = 2
            r2[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            goto L_0x04a1
        L_0x0ce8:
            r3 = 0
            r7 = 1
            r8 = 2
            r0 = 2131626749(0x7f0e0afd, float:1.8880743E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0cfe:
            r3 = 0
            r7 = 1
            r8 = 2
            boolean r9 = r2.isVoice()
            if (r9 == 0) goto L_0x0d1a
            r0 = 2131626785(0x7f0e0b21, float:1.8880816E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0d1a:
            boolean r9 = r2.isRoundVideo()
            if (r9 == 0) goto L_0x0d33
            r0 = 2131626770(0x7f0e0b12, float:1.8880786E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0d33:
            boolean r3 = r2.isSticker()
            if (r3 != 0) goto L_0x0efb
            boolean r3 = r2.isAnimatedSticker()
            if (r3 == 0) goto L_0x0d41
            goto L_0x0efb
        L_0x0d41:
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r3.media
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0d94
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r0 < r7) goto L_0x0d7e
            java.lang.String r0 = r3.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0d7e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r15)
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r7 = 1
            r2[r7] = r0
            java.lang.String r0 = r4.title
            r8 = 2
            r2[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            goto L_0x04a1
        L_0x0d7e:
            r3 = 0
            r7 = 1
            r8 = 2
            r0 = 2131626734(0x7f0e0aee, float:1.8880713E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r7] = r1
            java.lang.String r1 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0d94:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x0ee5
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x0d9e
            goto L_0x0ee5
        L_0x0d9e:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x0db8
            r0 = 2131626745(0x7f0e0af9, float:1.8880735E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0db8:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x0de3
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r2 = 2131626731(0x7f0e0aeb, float:1.8880706E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r5 = 0
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r0.first_name
            java.lang.String r0 = r0.last_name
            java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r1, r0)
            r1 = 2
            r3[r1] = r0
            java.lang.String r0 = "NotificationActionPinnedContact2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x0de3:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0e25
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r7 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r7
            org.telegram.tgnet.TLRPC$Poll r0 = r7.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x0e0a
            r2 = 2131626767(0x7f0e0b0f, float:1.888078E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r5 = 0
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r6 = 1
            r3[r6] = r1
            java.lang.String r0 = r0.question
            r7 = 2
            r3[r7] = r0
            java.lang.String r0 = "NotificationActionPinnedQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x0e0a:
            r3 = 3
            r5 = 0
            r6 = 1
            r7 = 2
            r2 = 2131626764(0x7f0e0b0c, float:1.8880773E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r3[r6] = r1
            java.lang.String r0 = r0.question
            r3[r7] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x0e25:
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0e74
            int r0 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r0 < r7) goto L_0x0e5e
            java.lang.String r0 = r3.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0e5e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r14)
            org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
            java.lang.String r2 = r2.message
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            r8 = 1
            r2[r8] = r0
            java.lang.String r0 = r4.title
            r9 = 2
            r2[r9] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            goto L_0x04a1
        L_0x0e5e:
            r3 = 0
            r8 = 1
            r9 = 2
            r0 = 2131626761(0x7f0e0b09, float:1.8880767E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0e74:
            r3 = 0
            r8 = 1
            r9 = 2
            boolean r0 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0e8e
            r0 = 2131626737(0x7f0e0af1, float:1.8880719E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r8] = r1
            java.lang.String r1 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0e8e:
            java.lang.CharSequence r0 = r2.messageText
            if (r0 == 0) goto L_0x0ecf
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0ecf
            java.lang.CharSequence r0 = r2.messageText
            int r2 = r0.length()
            r3 = 20
            if (r2 <= r3) goto L_0x0ebb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 20
            r7 = 0
            java.lang.CharSequence r0 = r0.subSequence(r7, r3)
            r2.append(r0)
            java.lang.String r0 = "..."
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            goto L_0x0ebc
        L_0x0ebb:
            r7 = 0
        L_0x0ebc:
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r1
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = r4.title
            r8 = 2
            r2[r8] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r6, r5, r2)
            goto L_0x04a1
        L_0x0ecf:
            r3 = 1
            r7 = 0
            r8 = 2
            r0 = 2131626758(0x7f0e0b06, float:1.8880761E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r7] = r1
            java.lang.String r1 = r4.title
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0ee5:
            r3 = 1
            r7 = 0
            r8 = 2
            r0 = 2131626743(0x7f0e0af7, float:1.888073E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r7] = r1
            java.lang.String r1 = r4.title
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0efb:
            r3 = 1
            r7 = 0
            java.lang.String r0 = r2.getStickerEmoji()
            if (r0 == 0) goto L_0x0f1a
            r2 = 2131626775(0x7f0e0b17, float:1.8880796E38)
            r5 = 3
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r7] = r1
            java.lang.String r1 = r4.title
            r5[r3] = r1
            r6 = 2
            r5[r6] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r5)
            goto L_0x04a1
        L_0x0f1a:
            r6 = 2
            r0 = 2131626773(0x7f0e0b15, float:1.8880792E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r7] = r1
            java.lang.String r1 = r4.title
            r2[r3] = r1
            java.lang.String r1 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x0f2e:
            boolean r4 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r4 == 0) goto L_0x0f3a
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x1619
        L_0x0f3a:
            boolean r4 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r4 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r6 = (org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r6
            java.lang.String r0 = r6.emoticon
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 == 0) goto L_0x0f6e
            int r4 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r4 != 0) goto L_0x0f5a
            r0 = 2131624958(0x7f0e03fe, float:1.887711E38)
            r4 = 0
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.String r2 = "ChatThemeDisabledYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x04a1
        L_0x0f5a:
            r4 = 0
            r2 = 2131624957(0x7f0e03fd, float:1.8877108E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "ChatThemeDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x0f6e:
            r4 = 0
            r5 = 1
            int r6 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r6 != 0) goto L_0x0var_
            r1 = 2131624955(0x7f0e03fb, float:1.8877104E38)
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r4] = r0
            java.lang.String r0 = "ChangedChatThemeYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x04a1
        L_0x0var_:
            r2 = 2131624954(0x7f0e03fa, float:1.8877102E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            r3[r5] = r0
            java.lang.String r0 = "ChangedChatThemeTo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x0var_:
            boolean r1 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest
            if (r1 == 0) goto L_0x1618
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r13 = r0.toString()
            goto L_0x1619
        L_0x0fa1:
            org.telegram.tgnet.TLRPC$Peer r2 = r5.peer_id
            long r2 = r2.channel_id
            r5 = 0
            int r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x0fdb
            boolean r2 = r4.megagroup
            if (r2 != 0) goto L_0x0fdb
            boolean r0 = r27.isVideoAvatar()
            if (r0 == 0) goto L_0x0fc8
            r0 = 2131624907(0x7f0e03cb, float:1.8877007E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r4.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1619
        L_0x0fc8:
            r1 = 1
            r3 = 0
            r0 = 2131624872(0x7f0e03a8, float:1.8876936E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r4.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1619
        L_0x0fdb:
            r3 = 0
            boolean r0 = r27.isVideoAvatar()
            if (r0 == 0) goto L_0x0ff7
            r0 = 2131626792(0x7f0e0b28, float:1.888083E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r5 = 1
            r2[r5] = r1
            java.lang.String r1 = "NotificationEditedGroupVideo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x0ff7:
            r2 = 2
            r5 = 1
            r0 = 2131626791(0x7f0e0b27, float:1.8880828E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r5] = r1
            java.lang.String r1 = "NotificationEditedGroupPhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x100c:
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r2 == 0) goto L_0x12c4
            boolean r2 = r4.megagroup
            if (r2 != 0) goto L_0x12c4
            boolean r2 = r27.isMediaEmpty()
            if (r2 == 0) goto L_0x1051
            if (r28 != 0) goto L_0x1040
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1040
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x1040:
            r3 = 0
            r4 = 1
            r0 = 2131624856(0x7f0e0398, float:1.8876904E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageNoText"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x1051:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r3 == 0) goto L_0x10a1
            if (r28 != 0) goto L_0x1090
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r3 < r4) goto L_0x1090
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1090
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x1090:
            r3 = 0
            r4 = 1
            r0 = 2131624857(0x7f0e0399, float:1.8876906E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessagePhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x10a1:
            boolean r2 = r27.isVideo()
            if (r2 == 0) goto L_0x10f1
            if (r28 != 0) goto L_0x10e0
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x10e0
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x10e0
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x10e0:
            r3 = 0
            r4 = 1
            r0 = 2131624863(0x7f0e039f, float:1.8876918E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageVideo"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x10f1:
            r3 = 0
            r4 = 1
            boolean r2 = r27.isVoice()
            if (r2 == 0) goto L_0x1108
            r0 = 2131624848(0x7f0e0390, float:1.8876887E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageAudio"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x1108:
            boolean r2 = r27.isRoundVideo()
            if (r2 == 0) goto L_0x111d
            r0 = 2131624860(0x7f0e039c, float:1.8876912E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageRound"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x111d:
            boolean r2 = r27.isMusic()
            if (r2 == 0) goto L_0x1132
            r0 = 2131624855(0x7f0e0397, float:1.8876901E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageMusic"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x1132:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r4 == 0) goto L_0x1157
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r2
            r0 = 2131624849(0x7f0e0391, float:1.887689E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r1
            java.lang.String r1 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r2)
            r2 = 1
            r4[r2] = r1
            java.lang.String r1 = "ChannelMessageContact2"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r4)
            goto L_0x1619
        L_0x1157:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x118f
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x1179
            r2 = 2131624859(0x7f0e039b, float:1.887691E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r5 = 1
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x1179:
            r3 = 2
            r4 = 0
            r5 = 1
            r2 = 2131624858(0x7f0e039a, float:1.8876908E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r4] = r1
            java.lang.String r0 = r0.question
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x118f:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x12b3
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x1199
            goto L_0x12b3
        L_0x1199:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x11ae
            r0 = 2131624853(0x7f0e0395, float:1.8876897E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageLiveLocation"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x11ae:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x1283
            boolean r2 = r27.isSticker()
            if (r2 != 0) goto L_0x125a
            boolean r2 = r27.isAnimatedSticker()
            if (r2 == 0) goto L_0x11c0
            goto L_0x125a
        L_0x11c0:
            boolean r2 = r27.isGif()
            if (r2 == 0) goto L_0x1210
            if (r28 != 0) goto L_0x11ff
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x11ff
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x11ff
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x11ff:
            r3 = 0
            r4 = 1
            r0 = 2131624852(0x7f0e0394, float:1.8876895E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageGIF"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x1210:
            if (r28 != 0) goto L_0x1249
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x1249
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1249
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x1249:
            r3 = 0
            r4 = 1
            r0 = 2131624850(0x7f0e0392, float:1.8876891E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageDocument"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x125a:
            r3 = 0
            r4 = 1
            java.lang.String r0 = r27.getStickerEmoji()
            if (r0 == 0) goto L_0x1274
            r2 = 2131624862(0x7f0e039e, float:1.8876916E38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            r5[r3] = r1
            r5[r4] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r5)
            goto L_0x04a1
        L_0x1274:
            r0 = 2131624861(0x7f0e039d, float:1.8876914E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x1283:
            r3 = 0
            if (r28 != 0) goto L_0x12a3
            java.lang.CharSequence r2 = r0.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x12a3
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r1
            java.lang.CharSequence r0 = r0.messageText
            r4 = 1
            r2[r4] = r0
            r0 = 2131626855(0x7f0e0b67, float:1.8880958E38)
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            r29[r3] = r4
            goto L_0x1619
        L_0x12a3:
            r4 = 1
            r0 = 2131624856(0x7f0e0398, float:1.8876904E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageNoText"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x12b3:
            r3 = 0
            r4 = 1
            r0 = 2131624854(0x7f0e0396, float:1.88769E38)
            java.lang.Object[] r2 = new java.lang.Object[r4]
            r2[r3] = r1
            java.lang.String r1 = "ChannelMessageMap"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x12c4:
            boolean r2 = r27.isMediaEmpty()
            r3 = 2131626838(0x7f0e0b56, float:1.8880923E38)
            java.lang.String r5 = "NotificationMessageGroupText"
            if (r2 == 0) goto L_0x1309
            if (r28 != 0) goto L_0x12f3
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x12f3
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r6 = 0
            r2[r6] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r2[r4] = r1
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r6 = 2
            r2[r6] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r3, r2)
            goto L_0x1619
        L_0x12f3:
            r6 = 2
            r0 = 2131626831(0x7f0e0b4f, float:1.888091E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r3 = 1
            r2[r3] = r1
            r6 = r22
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            goto L_0x1619
        L_0x1309:
            r6 = r22
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r2.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x1360
            if (r28 != 0) goto L_0x134a
            int r6 = android.os.Build.VERSION.SDK_INT
            r7 = 19
            if (r6 < r7) goto L_0x134a
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x134a
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r6 = 0
            r2[r6] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r14)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r6 = 2
            r2[r6] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r3, r2)
            goto L_0x1619
        L_0x134a:
            r6 = 2
            r0 = 2131626832(0x7f0e0b50, float:1.8880911E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupPhoto"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x1360:
            boolean r2 = r27.isVideo()
            if (r2 == 0) goto L_0x13b5
            if (r28 != 0) goto L_0x139f
            int r2 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r2 < r6) goto L_0x139f
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x139f
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r6 = 0
            r2[r6] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r13)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r8 = 2
            r2[r8] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r3, r2)
            goto L_0x1619
        L_0x139f:
            r8 = 2
            r0 = 2131626839(0x7f0e0b57, float:1.8880926E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r9 = 0
            r2[r9] = r1
            java.lang.String r1 = r4.title
            r10 = 1
            r2[r10] = r1
            java.lang.String r1 = " "
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x13b5:
            r8 = 2
            r9 = 0
            r10 = 1
            boolean r2 = r27.isVoice()
            if (r2 == 0) goto L_0x13d1
            r0 = 2131626821(0x7f0e0b45, float:1.888089E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r9] = r1
            java.lang.String r1 = r4.title
            r2[r10] = r1
            java.lang.String r1 = "NotificationMessageGroupAudio"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x13d1:
            boolean r2 = r27.isRoundVideo()
            if (r2 == 0) goto L_0x13ea
            r0 = 2131626835(0x7f0e0b53, float:1.8880917E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r9] = r1
            java.lang.String r1 = r4.title
            r2[r10] = r1
            java.lang.String r1 = "NotificationMessageGroupRound"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x13ea:
            boolean r2 = r27.isMusic()
            if (r2 == 0) goto L_0x1403
            r0 = 2131626830(0x7f0e0b4e, float:1.8880907E38)
            java.lang.Object[] r2 = new java.lang.Object[r8]
            r2[r9] = r1
            java.lang.String r1 = r4.title
            r2[r10] = r1
            java.lang.String r1 = "NotificationMessageGroupMusic"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x1403:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x142e
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r2
            r0 = 2131626822(0x7f0e0b46, float:1.8880891E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r5 = 0
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r3[r4] = r1
            java.lang.String r1 = r2.first_name
            java.lang.String r2 = r2.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r1, r2)
            r2 = 2
            r3[r2] = r1
            java.lang.String r1 = "NotificationMessageGroupContact2"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x1619
        L_0x142e:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x1470
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r0 = r2.poll
            boolean r2 = r0.quiz
            if (r2 == 0) goto L_0x1455
            r2 = 2131626834(0x7f0e0b52, float:1.8880915E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r5 = 0
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r6 = 1
            r3[r6] = r1
            java.lang.String r0 = r0.question
            r7 = 2
            r3[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x1455:
            r3 = 3
            r5 = 0
            r6 = 1
            r7 = 2
            r2 = 2131626833(0x7f0e0b51, float:1.8880913E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r3[r6] = r1
            java.lang.String r0 = r0.question
            r3[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            goto L_0x04a1
        L_0x1470:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x1491
            r0 = 2131626824(0x7f0e0b48, float:1.8880895E38)
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r5 = 0
            r3[r5] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r3[r4] = r1
            org.telegram.tgnet.TLRPC$TL_game r1 = r2.game
            java.lang.String r1 = r1.title
            r2 = 2
            r3[r2] = r1
            java.lang.String r1 = "NotificationMessageGroupGame"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r3)
            goto L_0x1619
        L_0x1491:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x15d0
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x149b
            goto L_0x15d0
        L_0x149b:
            boolean r8 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x14b5
            r0 = 2131626828(0x7f0e0b4c, float:1.8880903E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupLiveLocation"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x14b5:
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r2 == 0) goto L_0x159e
            boolean r2 = r27.isSticker()
            if (r2 != 0) goto L_0x156b
            boolean r2 = r27.isAnimatedSticker()
            if (r2 == 0) goto L_0x14c7
            goto L_0x156b
        L_0x14c7:
            boolean r2 = r27.isGif()
            if (r2 == 0) goto L_0x151c
            if (r28 != 0) goto L_0x1506
            int r2 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r2 < r6) goto L_0x1506
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1506
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r6 = 0
            r2[r6] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r7)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r6 = 2
            r2[r6] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r3, r2)
            goto L_0x1619
        L_0x1506:
            r6 = 2
            r0 = 2131626826(0x7f0e0b4a, float:1.88809E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r3 = 1
            r2[r3] = r1
            java.lang.String r1 = "NotificationMessageGroupGif"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x151c:
            if (r28 != 0) goto L_0x1555
            int r2 = android.os.Build.VERSION.SDK_INT
            r6 = 19
            if (r2 < r6) goto L_0x1555
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            java.lang.String r2 = r2.message
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x1555
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r6 = 0
            r2[r6] = r1
            java.lang.String r1 = r4.title
            r4 = 1
            r2[r4] = r1
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r15)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            r6 = 2
            r2[r6] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r3, r2)
            goto L_0x1619
        L_0x1555:
            r6 = 2
            r0 = 2131626823(0x7f0e0b47, float:1.8880893E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r3 = 0
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r5 = 1
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageGroupDocument"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x156b:
            r3 = 0
            r5 = 1
            java.lang.String r0 = r27.getStickerEmoji()
            if (r0 == 0) goto L_0x158a
            r2 = 2131626837(0x7f0e0b55, float:1.8880921E38)
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]
            r6[r3] = r1
            java.lang.String r1 = r4.title
            r6[r5] = r1
            r7 = 2
            r6[r7] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r6)
            goto L_0x04a1
        L_0x158a:
            r7 = 2
            r0 = 2131626836(0x7f0e0b54, float:1.888092E38)
            java.lang.Object[] r2 = new java.lang.Object[r7]
            r2[r3] = r1
            java.lang.String r1 = r4.title
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x04a1
        L_0x159e:
            if (r28 != 0) goto L_0x15bd
            java.lang.CharSequence r2 = r0.messageText
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x15bd
            r2 = 3
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r7 = 0
            r2[r7] = r1
            java.lang.String r1 = r4.title
            r8 = 1
            r2[r8] = r1
            java.lang.CharSequence r0 = r0.messageText
            r9 = 2
            r2[r9] = r0
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r5, r3, r2)
            goto L_0x1619
        L_0x15bd:
            r7 = 0
            r8 = 1
            r9 = 2
            r0 = 2131626831(0x7f0e0b4f, float:1.888091E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r7] = r1
            java.lang.String r1 = r4.title
            r2[r8] = r1
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r2)
            goto L_0x1619
        L_0x15d0:
            r7 = 0
            r8 = 1
            r9 = 2
            r0 = 2131626829(0x7f0e0b4d, float:1.8880905E38)
            java.lang.Object[] r2 = new java.lang.Object[r9]
            r2[r7] = r1
            java.lang.String r1 = r4.title
            r2[r8] = r1
            java.lang.String r1 = "NotificationMessageGroupMap"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x15e5:
            r6 = r22
            r7 = 0
            if (r30 == 0) goto L_0x15ec
            r30[r7] = r7
        L_0x15ec:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r0 == 0) goto L_0x1605
            boolean r0 = r4.megagroup
            if (r0 != 0) goto L_0x1605
            r0 = 2131624856(0x7f0e0398, float:1.8876904E38)
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r1
            java.lang.String r1 = "ChannelMessageNoText"
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1619
        L_0x1605:
            r2 = 1
            r0 = 2131626831(0x7f0e0b4f, float:1.888091E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r1
            java.lang.String r1 = r4.title
            r3[r2] = r1
            java.lang.String r13 = org.telegram.messenger.LocaleController.formatString(r6, r0, r3)
            goto L_0x1619
        L_0x1617:
            r8 = 0
        L_0x1618:
            r13 = r8
        L_0x1619:
            return r13
        L_0x161a:
            r0 = 2131629035(0x7f0e13eb, float:1.888538E38)
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
            r4 = 2131165302(0x7var_, float:1.7944817E38)
            goto L_0x0114
        L_0x0111:
            r4 = 2131165306(0x7var_a, float:1.7944825E38)
        L_0x0114:
            androidx.core.graphics.drawable.IconCompat r4 = androidx.core.graphics.drawable.IconCompat.createWithResource(r6, r4)     // Catch:{ Exception -> 0x0150 }
            goto L_0x0122
        L_0x0119:
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0150 }
            r6 = 2131165304(0x7var_, float:1.7944821E38)
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

    /* JADX WARNING: Removed duplicated region for block: B:187:0x0402 A[LOOP:1: B:185:0x03ff->B:187:0x0402, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0417  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x0465  */
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
            if (r36 == 0) goto L_0x00b5
            r7 = 2131626906(0x7f0e0b9a, float:1.8881061E38)
            java.lang.String r8 = "NotificationsSilent"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.String r8 = "silent"
            goto L_0x0108
        L_0x00b5:
            if (r34 == 0) goto L_0x00df
            if (r35 == 0) goto L_0x00bf
            r11 = 2131626884(0x7f0e0b84, float:1.8881017E38)
            java.lang.String r14 = "NotificationsInAppDefault"
            goto L_0x00c4
        L_0x00bf:
            r11 = 2131626867(0x7f0e0b73, float:1.8880982E38)
            java.lang.String r14 = "NotificationsDefault"
        L_0x00c4:
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r14, r11)
            r14 = 2
            if (r0 != r14) goto L_0x00d2
            if (r35 == 0) goto L_0x00cf
            java.lang.String r9 = "channels_ia"
        L_0x00cf:
            r8 = r9
        L_0x00d0:
            r7 = r11
            goto L_0x0108
        L_0x00d2:
            if (r0 != 0) goto L_0x00da
            if (r35 == 0) goto L_0x00d8
            java.lang.String r7 = "groups_ia"
        L_0x00d8:
            r8 = r7
            goto L_0x00d0
        L_0x00da:
            if (r35 == 0) goto L_0x00d0
            java.lang.String r8 = "private_ia"
            goto L_0x00d0
        L_0x00df:
            if (r35 == 0) goto L_0x00f0
            r7 = 2131626864(0x7f0e0b70, float:1.8880976E38)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r9[r15] = r29
            java.lang.String r8 = "NotificationsChatInApp"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatString(r8, r7, r9)
            goto L_0x00f2
        L_0x00f0:
            r7 = r29
        L_0x00f2:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            if (r35 == 0) goto L_0x00fc
            java.lang.String r9 = "org.telegram.keyia"
            goto L_0x00fe
        L_0x00fc:
            java.lang.String r9 = "org.telegram.key"
        L_0x00fe:
            r8.append(r9)
            r8.append(r2)
            java.lang.String r8 = r8.toString()
        L_0x0108:
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
            if (r11 == 0) goto L_0x039a
            android.app.NotificationManager r12 = systemNotificationManager
            android.app.NotificationChannel r12 = r12.getNotificationChannel(r11)
            boolean r20 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            r21 = r8
            java.lang.String r8 = " = "
            if (r20 == 0) goto L_0x016e
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
            goto L_0x0170
        L_0x016e:
            r20 = r15
        L_0x0170:
            if (r12 == 0) goto L_0x038b
            if (r36 != 0) goto L_0x0383
            if (r13 != 0) goto L_0x0383
            int r4 = r12.getImportance()
            android.net.Uri r15 = r12.getSound()
            long[] r22 = r12.getVibrationPattern()
            r23 = r13
            boolean r13 = r12.shouldVibrate()
            if (r13 != 0) goto L_0x0197
            if (r22 != 0) goto L_0x0197
            r24 = r9
            r25 = r13
            r9 = 2
            long[] r13 = new long[r9]
            r13 = {0, 0} // fill-array
            goto L_0x019d
        L_0x0197:
            r24 = r9
            r25 = r13
            r13 = r22
        L_0x019d:
            int r9 = r12.getLightColor()
            if (r13 == 0) goto L_0x01b1
            r12 = 0
        L_0x01a4:
            int r5 = r13.length
            if (r12 >= r5) goto L_0x01b1
            r2 = r13[r12]
            r10.append(r2)
            int r12 = r12 + 1
            r2 = r27
            goto L_0x01a4
        L_0x01b1:
            r10.append(r9)
            if (r15 == 0) goto L_0x01bd
            java.lang.String r2 = r15.toString()
            r10.append(r2)
        L_0x01bd:
            r10.append(r4)
            if (r34 != 0) goto L_0x01c7
            if (r16 == 0) goto L_0x01c7
            r10.append(r7)
        L_0x01c7:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x01ed
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
        L_0x01ed:
            java.lang.String r2 = r10.toString()
            java.lang.String r2 = org.telegram.messenger.Utilities.MD5(r2)
            r3 = 0
            r10.setLength(r3)
            boolean r3 = r2.equals(r14)
            if (r3 != 0) goto L_0x0372
            java.lang.String r3 = "notify2_"
            if (r4 != 0) goto L_0x023f
            android.content.SharedPreferences$Editor r4 = r6.edit()
            if (r34 == 0) goto L_0x021d
            if (r35 != 0) goto L_0x0218
            java.lang.String r3 = getGlobalNotificationsKey(r37)
            r5 = 2147483647(0x7fffffff, float:NaN)
            r4.putInt(r3, r5)
            r1.updateServerNotificationsSettings((int) r0)
        L_0x0218:
            r8 = r11
            r5 = 1
            r11 = r27
            goto L_0x0237
        L_0x021d:
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
        L_0x0237:
            r15 = r33
            r22 = r2
            r2 = r30
            goto L_0x02d4
        L_0x023f:
            r15 = r33
            r8 = r11
            r5 = 1
            r11 = r27
            if (r4 == r15) goto L_0x02ce
            if (r35 != 0) goto L_0x02c6
            android.content.SharedPreferences$Editor r5 = r6.edit()
            r22 = r2
            r2 = 4
            if (r4 == r2) goto L_0x0263
            r2 = 5
            if (r4 != r2) goto L_0x0256
            goto L_0x0263
        L_0x0256:
            r2 = 1
            if (r4 != r2) goto L_0x025c
            r2 = 2
            r4 = 4
            goto L_0x0265
        L_0x025c:
            r2 = 2
            if (r4 != r2) goto L_0x0261
            r4 = 5
            goto L_0x0265
        L_0x0261:
            r4 = 0
            goto L_0x0265
        L_0x0263:
            r2 = 2
            r4 = 1
        L_0x0265:
            if (r34 == 0) goto L_0x028a
            java.lang.String r3 = getGlobalNotificationsKey(r37)
            r2 = 0
            android.content.SharedPreferences$Editor r3 = r5.putInt(r3, r2)
            r3.commit()
            r2 = 2
            if (r0 != r2) goto L_0x027c
            java.lang.String r2 = "priority_channel"
            r5.putInt(r2, r4)
            goto L_0x02c9
        L_0x027c:
            if (r0 != 0) goto L_0x0284
            java.lang.String r2 = "priority_group"
            r5.putInt(r2, r4)
            goto L_0x02c9
        L_0x0284:
            java.lang.String r2 = "priority_messages"
            r5.putInt(r2, r4)
            goto L_0x02c9
        L_0x028a:
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
            goto L_0x02c9
        L_0x02c6:
            r22 = r2
            r5 = 0
        L_0x02c9:
            r2 = r30
            r4 = r5
            r5 = 1
            goto L_0x02d4
        L_0x02ce:
            r22 = r2
            r2 = r30
            r4 = 0
            r5 = 0
        L_0x02d4:
            boolean r3 = r1.isEmptyVibration(r2)
            r17 = 1
            r3 = r3 ^ 1
            r2 = r25
            if (r3 == r2) goto L_0x032d
            if (r35 != 0) goto L_0x0329
            if (r4 != 0) goto L_0x02e8
            android.content.SharedPreferences$Editor r4 = r6.edit()
        L_0x02e8:
            if (r34 == 0) goto L_0x0310
            r3 = 2
            if (r0 != r3) goto L_0x02f8
            if (r2 == 0) goto L_0x02f1
            r2 = 0
            goto L_0x02f2
        L_0x02f1:
            r2 = 2
        L_0x02f2:
            java.lang.String r3 = "vibrate_channel"
            r4.putInt(r3, r2)
            goto L_0x0329
        L_0x02f8:
            if (r0 != 0) goto L_0x0305
            if (r2 == 0) goto L_0x02fe
            r2 = 0
            goto L_0x02ff
        L_0x02fe:
            r2 = 2
        L_0x02ff:
            java.lang.String r3 = "vibrate_group"
            r4.putInt(r3, r2)
            goto L_0x0329
        L_0x0305:
            if (r2 == 0) goto L_0x0309
            r2 = 0
            goto L_0x030a
        L_0x0309:
            r2 = 2
        L_0x030a:
            java.lang.String r3 = "vibrate_messages"
            r4.putInt(r3, r2)
            goto L_0x0329
        L_0x0310:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "vibrate_"
            r3.append(r5)
            r3.append(r11)
            java.lang.String r3 = r3.toString()
            if (r2 == 0) goto L_0x0325
            r2 = 0
            goto L_0x0326
        L_0x0325:
            r2 = 2
        L_0x0326:
            r4.putInt(r3, r2)
        L_0x0329:
            r2 = r31
            r5 = 1
            goto L_0x0331
        L_0x032d:
            r13 = r30
            r2 = r31
        L_0x0331:
            if (r9 == r2) goto L_0x036a
            if (r35 != 0) goto L_0x0368
            if (r4 != 0) goto L_0x033b
            android.content.SharedPreferences$Editor r4 = r6.edit()
        L_0x033b:
            if (r34 == 0) goto L_0x0354
            r2 = 2
            if (r0 != r2) goto L_0x0346
            java.lang.String r0 = "ChannelLed"
            r4.putInt(r0, r9)
            goto L_0x0368
        L_0x0346:
            if (r0 != 0) goto L_0x034e
            java.lang.String r0 = "GroupLed"
            r4.putInt(r0, r9)
            goto L_0x0368
        L_0x034e:
            java.lang.String r0 = "MessagesLed"
            r4.putInt(r0, r9)
            goto L_0x0368
        L_0x0354:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "color_"
            r0.append(r2)
            r0.append(r11)
            java.lang.String r0 = r0.toString()
            r4.putInt(r0, r9)
        L_0x0368:
            r5 = 1
            goto L_0x036b
        L_0x036a:
            r9 = r2
        L_0x036b:
            if (r4 == 0) goto L_0x0370
            r4.commit()
        L_0x0370:
            r3 = r5
            goto L_0x037f
        L_0x0372:
            r15 = r33
            r22 = r2
            r8 = r11
            r11 = r27
            r2 = r31
            r13 = r30
            r9 = r2
            r3 = 0
        L_0x037f:
            r2 = r9
            r0 = r22
            goto L_0x03ab
        L_0x0383:
            r15 = r5
            r24 = r9
            r8 = r11
            r23 = r13
            r11 = r2
            goto L_0x03a5
        L_0x038b:
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
            goto L_0x03ab
        L_0x039a:
            r21 = r8
            r24 = r9
            r8 = r11
            r23 = r13
            r20 = r15
            r11 = r2
            r15 = r5
        L_0x03a5:
            r2 = r31
            r13 = r30
            r0 = 0
            r3 = 0
        L_0x03ab:
            if (r3 == 0) goto L_0x03ea
            if (r0 == 0) goto L_0x03ea
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
            if (r3 == 0) goto L_0x03f7
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "change edited channel "
            r3.append(r5)
            r3.append(r8)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
            goto L_0x03f7
        L_0x03ea:
            r9 = r20
            r4 = r24
            if (r23 != 0) goto L_0x03fe
            if (r0 == 0) goto L_0x03fe
            if (r35 == 0) goto L_0x03fe
            if (r34 != 0) goto L_0x03f7
            goto L_0x03fe
        L_0x03f7:
            r3 = r32
            r18 = r6
        L_0x03fb:
            r10 = r8
            goto L_0x0463
        L_0x03fe:
            r3 = 0
        L_0x03ff:
            int r0 = r13.length
            if (r3 >= r0) goto L_0x040e
            r18 = r6
            r5 = r13[r3]
            r10.append(r5)
            int r3 = r3 + 1
            r6 = r18
            goto L_0x03ff
        L_0x040e:
            r18 = r6
            r10.append(r2)
            r3 = r32
            if (r3 == 0) goto L_0x041e
            java.lang.String r0 = r32.toString()
            r10.append(r0)
        L_0x041e:
            r10.append(r15)
            if (r34 != 0) goto L_0x0428
            if (r16 == 0) goto L_0x0428
            r10.append(r7)
        L_0x0428:
            java.lang.String r0 = r10.toString()
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r0)
            if (r36 != 0) goto L_0x0461
            if (r8 == 0) goto L_0x0461
            if (r23 != 0) goto L_0x043c
            boolean r0 = r14.equals(r5)
            if (r0 != 0) goto L_0x0461
        L_0x043c:
            android.app.NotificationManager r0 = systemNotificationManager     // Catch:{ Exception -> 0x0442 }
            r0.deleteNotificationChannel(r8)     // Catch:{ Exception -> 0x0442 }
            goto L_0x0446
        L_0x0442:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0446:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x045e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "delete channel by settings change "
            r0.append(r6)
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x045e:
            r0 = r5
            r10 = 0
            goto L_0x0463
        L_0x0461:
            r0 = r5
            goto L_0x03fb
        L_0x0463:
            if (r10 != 0) goto L_0x0546
            java.lang.String r5 = "channel_"
            if (r34 == 0) goto L_0x048c
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
            goto L_0x04ae
        L_0x048c:
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
        L_0x04ae:
            r10 = r5
            android.app.NotificationChannel r5 = new android.app.NotificationChannel
            if (r16 == 0) goto L_0x04bd
            r6 = 2131627935(0x7f0e0f9f, float:1.8883148E38)
            java.lang.String r7 = "SecretChatName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r7, r6)
            goto L_0x04bf
        L_0x04bd:
            r7 = r29
        L_0x04bf:
            r5.<init>(r10, r7, r15)
            r12 = r19
            r5.setGroup(r12)
            if (r2 == 0) goto L_0x04d2
            r6 = 1
            r5.enableLights(r6)
            r5.setLightColor(r2)
            r2 = 0
            goto L_0x04d7
        L_0x04d2:
            r2 = 0
            r6 = 1
            r5.enableLights(r2)
        L_0x04d7:
            boolean r7 = r1.isEmptyVibration(r13)
            if (r7 != 0) goto L_0x04e7
            r5.enableVibration(r6)
            int r2 = r13.length
            if (r2 <= 0) goto L_0x04ea
            r5.setVibrationPattern(r13)
            goto L_0x04ea
        L_0x04e7:
            r5.enableVibration(r2)
        L_0x04ea:
            android.media.AudioAttributes$Builder r2 = new android.media.AudioAttributes$Builder
            r2.<init>()
            r6 = 4
            r2.setContentType(r6)
            r6 = 5
            r2.setUsage(r6)
            if (r3 == 0) goto L_0x0501
            android.media.AudioAttributes r2 = r2.build()
            r5.setSound(r3, r2)
            goto L_0x0505
        L_0x0501:
            r2 = 0
            r5.setSound(r2, r2)
        L_0x0505:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x051d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "create new channel "
            r2.append(r3)
            r2.append(r10)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x051d:
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
        L_0x0546:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v11, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: android.net.Uri} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v2, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v7, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r20v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v9, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v16, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v25, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v21, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v27, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v77, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v78, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v79, resolved type: android.net.Uri} */
    /* JADX WARNING: type inference failed for: r6v89 */
    /* JADX WARNING: type inference failed for: r6v90 */
    /* JADX WARNING: type inference failed for: r6v97 */
    /* JADX WARNING: Code restructure failed: missing block: B:371:0x0873, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x0875;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:409:0x093f */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x01ca A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0217 A[SYNTHETIC, Splitter:B:105:0x0217] */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x028d A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x0349 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0423 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0447 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x044a A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0463 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0509 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0517 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x059a A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x05f7 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x05fb A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0609 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x060c A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0612 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0618 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:243:0x0635 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0670 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x0675 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x06ac A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x071e A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x07df A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x0829  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x0835  */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x086d A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:375:0x087c A[ADDED_TO_REGION, Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0951 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x095b A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:418:0x0962 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0972 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x09e2 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0a88 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:467:0x0ab5 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0ace A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0119 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012b A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x012f A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0149 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0160 A[SYNTHETIC, Splitter:B:84:0x0160] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0193 A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x019f A[Catch:{ Exception -> 0x0b00 }] */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01b4 A[SYNTHETIC, Splitter:B:99:0x01b4] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r48) {
        /*
            r47 = this;
            r15 = r47
            java.lang.String r1 = "currentAccount"
            org.telegram.messenger.UserConfig r2 = r47.getUserConfig()
            boolean r2 = r2.isClientActivated()
            if (r2 == 0) goto L_0x0b06
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r15.pushMessages
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0b06
            boolean r2 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r2 != 0) goto L_0x0022
            int r2 = r15.currentAccount
            int r3 = org.telegram.messenger.UserConfig.selectedAccount
            if (r2 == r3) goto L_0x0022
            goto L_0x0b06
        L_0x0022:
            org.telegram.tgnet.ConnectionsManager r2 = r47.getConnectionsManager()     // Catch:{ Exception -> 0x0b00 }
            r2.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0b00 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r15.pushMessages     // Catch:{ Exception -> 0x0b00 }
            r3 = 0
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x0b00 }
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2     // Catch:{ Exception -> 0x0b00 }
            org.telegram.messenger.AccountInstance r4 = r47.getAccountInstance()     // Catch:{ Exception -> 0x0b00 }
            android.content.SharedPreferences r4 = r4.getNotificationsSettings()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r5 = "dismissDate"
            int r5 = r4.getInt(r5, r3)     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$Message r6 = r2.messageOwner     // Catch:{ Exception -> 0x0b00 }
            int r6 = r6.date     // Catch:{ Exception -> 0x0b00 }
            if (r6 > r5) goto L_0x004a
            r47.dismissNotification()     // Catch:{ Exception -> 0x0b00 }
            return
        L_0x004a:
            long r6 = r2.getDialogId()     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$Message r8 = r2.messageOwner     // Catch:{ Exception -> 0x0b00 }
            boolean r8 = r8.mentioned     // Catch:{ Exception -> 0x0b00 }
            if (r8 == 0) goto L_0x0059
            long r8 = r2.getFromChatId()     // Catch:{ Exception -> 0x0b00 }
            goto L_0x005a
        L_0x0059:
            r8 = r6
        L_0x005a:
            r2.getId()     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$Message r10 = r2.messageOwner     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$Peer r10 = r10.peer_id     // Catch:{ Exception -> 0x0b00 }
            long r11 = r10.chat_id     // Catch:{ Exception -> 0x0b00 }
            r13 = 0
            int r16 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r16 == 0) goto L_0x006a
            goto L_0x006c
        L_0x006a:
            long r11 = r10.channel_id     // Catch:{ Exception -> 0x0b00 }
        L_0x006c:
            r17 = r4
            long r3 = r10.user_id     // Catch:{ Exception -> 0x0b00 }
            boolean r10 = r2.isFromUser()     // Catch:{ Exception -> 0x0b00 }
            if (r10 == 0) goto L_0x008c
            int r10 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
            if (r10 == 0) goto L_0x0086
            org.telegram.messenger.UserConfig r10 = r47.getUserConfig()     // Catch:{ Exception -> 0x0b00 }
            long r18 = r10.getClientUserId()     // Catch:{ Exception -> 0x0b00 }
            int r10 = (r3 > r18 ? 1 : (r3 == r18 ? 0 : -1))
            if (r10 != 0) goto L_0x008c
        L_0x0086:
            org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id     // Catch:{ Exception -> 0x0b00 }
            long r3 = r3.user_id     // Catch:{ Exception -> 0x0b00 }
        L_0x008c:
            org.telegram.messenger.MessagesController r10 = r47.getMessagesController()     // Catch:{ Exception -> 0x0b00 }
            java.lang.Long r13 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$User r10 = r10.getUser(r13)     // Catch:{ Exception -> 0x0b00 }
            r18 = 0
            int r20 = (r11 > r18 ? 1 : (r11 == r18 ? 0 : -1))
            if (r20 == 0) goto L_0x00c5
            org.telegram.messenger.MessagesController r13 = r47.getMessagesController()     // Catch:{ Exception -> 0x0b00 }
            java.lang.Long r14 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$Chat r13 = r13.getChat(r14)     // Catch:{ Exception -> 0x0b00 }
            if (r13 != 0) goto L_0x00b5
            boolean r14 = r2.isFcmMessage()     // Catch:{ Exception -> 0x0b00 }
            if (r14 == 0) goto L_0x00b5
            boolean r14 = r2.localChannel     // Catch:{ Exception -> 0x0b00 }
            goto L_0x00c2
        L_0x00b5:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r13)     // Catch:{ Exception -> 0x0b00 }
            if (r14 == 0) goto L_0x00c1
            boolean r14 = r13.megagroup     // Catch:{ Exception -> 0x0b00 }
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
            int r3 = r15.getNotifyOverride(r1, r8)     // Catch:{ Exception -> 0x0b00 }
            r4 = -1
            r23 = r1
            r1 = 2
            if (r3 != r4) goto L_0x00e2
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r14)     // Catch:{ Exception -> 0x0b00 }
            boolean r3 = r15.isGlobalNotificationsEnabled(r6, r3)     // Catch:{ Exception -> 0x0b00 }
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
            boolean r24 = r2.isFcmMessage()     // Catch:{ Exception -> 0x0b00 }
            if (r24 == 0) goto L_0x00fa
            java.lang.String r4 = r2.localName     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0103
        L_0x00fa:
            if (r13 == 0) goto L_0x00ff
            java.lang.String r4 = r13.title     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0103
        L_0x00ff:
            java.lang.String r4 = org.telegram.messenger.UserObject.getUserName(r10)     // Catch:{ Exception -> 0x0b00 }
        L_0x0103:
            r25 = r4
            boolean r4 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b00 }
            if (r4 != 0) goto L_0x0112
            boolean r4 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b00 }
            if (r4 == 0) goto L_0x0110
            goto L_0x0112
        L_0x0110:
            r4 = 0
            goto L_0x0113
        L_0x0112:
            r4 = 1
        L_0x0113:
            boolean r26 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)     // Catch:{ Exception -> 0x0b00 }
            if (r26 != 0) goto L_0x012b
            androidx.collection.LongSparseArray<java.lang.Integer> r1 = r15.pushDialogs     // Catch:{ Exception -> 0x0b00 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0b00 }
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
            r2 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0152
        L_0x013f:
            java.lang.String r1 = "NotificationHiddenName"
            r2 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0152
        L_0x0149:
            java.lang.String r1 = "AppName"
            r2 = 2131624316(0x7f0e017c, float:1.8875808E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r2)     // Catch:{ Exception -> 0x0b00 }
        L_0x0152:
            r2 = 0
        L_0x0153:
            int r4 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0b00 }
            r28 = r10
            java.lang.String r10 = ""
            r29 = r14
            r14 = 1
            if (r4 <= r14) goto L_0x0193
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0b00 }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0b00 }
            if (r4 != r14) goto L_0x0175
            org.telegram.messenger.UserConfig r4 = r47.getUserConfig()     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$User r4 = r4.getCurrentUser()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = org.telegram.messenger.UserObject.getFirstName(r4)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0194
        L_0x0175:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r4.<init>()     // Catch:{ Exception -> 0x0b00 }
            org.telegram.messenger.UserConfig r14 = r47.getUserConfig()     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$User r14 = r14.getCurrentUser()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r14 = org.telegram.messenger.UserObject.getFirstName(r14)     // Catch:{ Exception -> 0x0b00 }
            r4.append(r14)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r14 = "・"
            r4.append(r14)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0194
        L_0x0193:
            r4 = r10
        L_0x0194:
            androidx.collection.LongSparseArray<java.lang.Integer> r14 = r15.pushDialogs     // Catch:{ Exception -> 0x0b00 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0b00 }
            r30 = r11
            r11 = 1
            if (r14 != r11) goto L_0x01a9
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b00 }
            r12 = 23
            if (r11 >= r12) goto L_0x01a6
            goto L_0x01a9
        L_0x01a6:
            r32 = r6
            goto L_0x01ff
        L_0x01a9:
            androidx.collection.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0b00 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r12 = "NewMessages"
            r14 = 1
            if (r11 != r14) goto L_0x01ca
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r11.<init>()     // Catch:{ Exception -> 0x0b00 }
            r11.append(r4)     // Catch:{ Exception -> 0x0b00 }
            int r4 = r15.total_unread_count     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatPluralString(r12, r4)     // Catch:{ Exception -> 0x0b00 }
            r11.append(r4)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = r11.toString()     // Catch:{ Exception -> 0x0b00 }
            goto L_0x01a6
        L_0x01ca:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r11.<init>()     // Catch:{ Exception -> 0x0b00 }
            r11.append(r4)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = "NotificationMessagesPeopleDisplayOrder"
            r32 = r6
            r14 = 2
            java.lang.Object[] r6 = new java.lang.Object[r14]     // Catch:{ Exception -> 0x0b00 }
            int r7 = r15.total_unread_count     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r12, r7)     // Catch:{ Exception -> 0x0b00 }
            r12 = 0
            r6[r12] = r7     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r7 = "FromChats"
            androidx.collection.LongSparseArray<java.lang.Integer> r12 = r15.pushDialogs     // Catch:{ Exception -> 0x0b00 }
            int r12 = r12.size()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r12)     // Catch:{ Exception -> 0x0b00 }
            r12 = 1
            r6[r12] = r7     // Catch:{ Exception -> 0x0b00 }
            r7 = 2131626857(0x7f0e0b69, float:1.8880962E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r4, r7, r6)     // Catch:{ Exception -> 0x0b00 }
            r11.append(r4)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = r11.toString()     // Catch:{ Exception -> 0x0b00 }
        L_0x01ff:
            androidx.core.app.NotificationCompat$Builder r6 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            r6.<init>(r7)     // Catch:{ Exception -> 0x0b00 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0b00 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r11 = ": "
            java.lang.String r12 = " "
            java.lang.String r14 = " @ "
            r34 = r8
            r9 = 1
            if (r7 != r9) goto L_0x028d
            java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r15.pushMessages     // Catch:{ Exception -> 0x0b00 }
            r7 = 0
            java.lang.Object r5 = r5.get(r7)     // Catch:{ Exception -> 0x0b00 }
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5     // Catch:{ Exception -> 0x0b00 }
            boolean[] r8 = new boolean[r9]     // Catch:{ Exception -> 0x0b00 }
            r36 = r3
            r9 = 0
            java.lang.String r3 = r15.getStringForMessage(r5, r7, r8, r9)     // Catch:{ Exception -> 0x0b00 }
            boolean r5 = r15.isSilentMessage(r5)     // Catch:{ Exception -> 0x0b00 }
            if (r3 != 0) goto L_0x0230
            return
        L_0x0230:
            if (r2 == 0) goto L_0x0275
            if (r13 == 0) goto L_0x0248
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r2.<init>()     // Catch:{ Exception -> 0x0b00 }
            r2.append(r14)     // Catch:{ Exception -> 0x0b00 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r2 = r3.replace(r2, r10)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0276
        L_0x0248:
            r2 = 0
            boolean r7 = r8[r2]     // Catch:{ Exception -> 0x0b00 }
            if (r7 == 0) goto L_0x0261
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r2.<init>()     // Catch:{ Exception -> 0x0b00 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b00 }
            r2.append(r11)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r2 = r3.replace(r2, r10)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0276
        L_0x0261:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r2.<init>()     // Catch:{ Exception -> 0x0b00 }
            r2.append(r1)     // Catch:{ Exception -> 0x0b00 }
            r2.append(r12)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r2 = r3.replace(r2, r10)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0276
        L_0x0275:
            r2 = r3
        L_0x0276:
            r6.setContentText(r2)     // Catch:{ Exception -> 0x0b00 }
            androidx.core.app.NotificationCompat$BigTextStyle r7 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0b00 }
            r7.<init>()     // Catch:{ Exception -> 0x0b00 }
            androidx.core.app.NotificationCompat$BigTextStyle r2 = r7.bigText(r2)     // Catch:{ Exception -> 0x0b00 }
            r6.setStyle(r2)     // Catch:{ Exception -> 0x0b00 }
            r2 = r6
            r46 = r4
            r4 = r3
            r3 = r46
            goto L_0x0347
        L_0x028d:
            r36 = r3
            r6.setContentText(r4)     // Catch:{ Exception -> 0x0b00 }
            androidx.core.app.NotificationCompat$InboxStyle r3 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0b00 }
            r3.<init>()     // Catch:{ Exception -> 0x0b00 }
            r3.setBigContentTitle(r1)     // Catch:{ Exception -> 0x0b00 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0b00 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0b00 }
            r8 = 10
            int r7 = java.lang.Math.min(r8, r7)     // Catch:{ Exception -> 0x0b00 }
            r8 = 1
            boolean[] r9 = new boolean[r8]     // Catch:{ Exception -> 0x0b00 }
            r38 = r6
            r6 = 2
            r8 = 0
            r37 = 0
        L_0x02af:
            if (r8 >= r7) goto L_0x0337
            r39 = r7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0b00 }
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0b00 }
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7     // Catch:{ Exception -> 0x0b00 }
            r42 = r3
            r40 = r4
            r41 = r8
            r4 = 0
            r8 = 0
            java.lang.String r3 = r15.getStringForMessage(r7, r8, r9, r4)     // Catch:{ Exception -> 0x0b00 }
            if (r3 == 0) goto L_0x032c
            org.telegram.tgnet.TLRPC$Message r4 = r7.messageOwner     // Catch:{ Exception -> 0x0b00 }
            int r4 = r4.date     // Catch:{ Exception -> 0x0b00 }
            if (r4 > r5) goto L_0x02d0
            goto L_0x032c
        L_0x02d0:
            r4 = 2
            if (r6 != r4) goto L_0x02d9
            boolean r6 = r15.isSilentMessage(r7)     // Catch:{ Exception -> 0x0b00 }
            r37 = r3
        L_0x02d9:
            androidx.collection.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0b00 }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0b00 }
            r7 = 1
            if (r4 != r7) goto L_0x0326
            if (r2 == 0) goto L_0x0326
            if (r13 == 0) goto L_0x02fa
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r4.<init>()     // Catch:{ Exception -> 0x0b00 }
            r4.append(r14)     // Catch:{ Exception -> 0x0b00 }
            r4.append(r1)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = r3.replace(r4, r10)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0326
        L_0x02fa:
            r4 = 0
            boolean r7 = r9[r4]     // Catch:{ Exception -> 0x0b00 }
            if (r7 == 0) goto L_0x0313
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r4.<init>()     // Catch:{ Exception -> 0x0b00 }
            r4.append(r1)     // Catch:{ Exception -> 0x0b00 }
            r4.append(r11)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = r3.replace(r4, r10)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0326
        L_0x0313:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r4.<init>()     // Catch:{ Exception -> 0x0b00 }
            r4.append(r1)     // Catch:{ Exception -> 0x0b00 }
            r4.append(r12)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = r3.replace(r4, r10)     // Catch:{ Exception -> 0x0b00 }
        L_0x0326:
            r4 = r42
            r4.addLine(r3)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x032e
        L_0x032c:
            r4 = r42
        L_0x032e:
            int r8 = r41 + 1
            r3 = r4
            r7 = r39
            r4 = r40
            goto L_0x02af
        L_0x0337:
            r46 = r4
            r4 = r3
            r3 = r46
            r4.setSummaryText(r3)     // Catch:{ Exception -> 0x0b00 }
            r2 = r38
            r2.setStyle(r4)     // Catch:{ Exception -> 0x0b00 }
            r5 = r6
            r4 = r37
        L_0x0347:
            if (r48 == 0) goto L_0x035b
            if (r36 == 0) goto L_0x035b
            org.telegram.messenger.MediaController r6 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0b00 }
            boolean r6 = r6.isRecordingAudio()     // Catch:{ Exception -> 0x0b00 }
            if (r6 != 0) goto L_0x035b
            r6 = 1
            if (r5 != r6) goto L_0x0359
            goto L_0x035b
        L_0x0359:
            r6 = 0
            goto L_0x035c
        L_0x035b:
            r6 = 1
        L_0x035c:
            java.lang.String r7 = "custom_"
            if (r6 != 0) goto L_0x0416
            int r11 = (r32 > r34 ? 1 : (r32 == r34 ? 0 : -1))
            if (r11 != 0) goto L_0x0416
            if (r13 == 0) goto L_0x0416
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r11.<init>()     // Catch:{ Exception -> 0x0b00 }
            r11.append(r7)     // Catch:{ Exception -> 0x0b00 }
            r8 = r32
            r11.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0b00 }
            r12 = r23
            r14 = 0
            boolean r11 = r12.getBoolean(r11, r14)     // Catch:{ Exception -> 0x0b00 }
            if (r11 == 0) goto L_0x03b0
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r11.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r14 = "smart_max_count_"
            r11.append(r14)     // Catch:{ Exception -> 0x0b00 }
            r11.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0b00 }
            r14 = 2
            int r11 = r12.getInt(r11, r14)     // Catch:{ Exception -> 0x0b00 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r14.<init>()     // Catch:{ Exception -> 0x0b00 }
            r32 = r6
            java.lang.String r6 = "smart_delay_"
            r14.append(r6)     // Catch:{ Exception -> 0x0b00 }
            r14.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r6 = r14.toString()     // Catch:{ Exception -> 0x0b00 }
            r14 = 180(0xb4, float:2.52E-43)
            int r14 = r12.getInt(r6, r14)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x03b5
        L_0x03b0:
            r32 = r6
            r14 = 180(0xb4, float:2.52E-43)
            r11 = 2
        L_0x03b5:
            if (r11 == 0) goto L_0x0412
            androidx.collection.LongSparseArray<android.graphics.Point> r6 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0b00 }
            java.lang.Object r6 = r6.get(r8)     // Catch:{ Exception -> 0x0b00 }
            android.graphics.Point r6 = (android.graphics.Point) r6     // Catch:{ Exception -> 0x0b00 }
            if (r6 != 0) goto L_0x03db
            android.graphics.Point r6 = new android.graphics.Point     // Catch:{ Exception -> 0x0b00 }
            long r36 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0b00 }
            r23 = r10
            r33 = 1000(0x3e8, double:4.94E-321)
            long r10 = r36 / r33
            int r11 = (int) r10     // Catch:{ Exception -> 0x0b00 }
            r10 = 1
            r6.<init>(r10, r11)     // Catch:{ Exception -> 0x0b00 }
            androidx.collection.LongSparseArray<android.graphics.Point> r10 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0b00 }
            r10.put(r8, r6)     // Catch:{ Exception -> 0x0b00 }
            r40 = r3
            r14 = r4
            goto L_0x0421
        L_0x03db:
            r23 = r10
            int r10 = r6.y     // Catch:{ Exception -> 0x0b00 }
            int r10 = r10 + r14
            r40 = r3
            r14 = r4
            long r3 = (long) r10     // Catch:{ Exception -> 0x0b00 }
            long r36 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0b00 }
            r33 = 1000(0x3e8, double:4.94E-321)
            long r36 = r36 / r33
            int r10 = (r3 > r36 ? 1 : (r3 == r36 ? 0 : -1))
            if (r10 >= 0) goto L_0x03fc
            long r3 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0b00 }
            long r3 = r3 / r33
            int r4 = (int) r3     // Catch:{ Exception -> 0x0b00 }
            r3 = 1
            r6.set(r3, r4)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0421
        L_0x03fc:
            int r3 = r6.x     // Catch:{ Exception -> 0x0b00 }
            if (r3 >= r11) goto L_0x040f
            r4 = 1
            int r3 = r3 + r4
            long r10 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0b00 }
            r33 = 1000(0x3e8, double:4.94E-321)
            long r10 = r10 / r33
            int r4 = (int) r10     // Catch:{ Exception -> 0x0b00 }
            r6.set(r3, r4)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0421
        L_0x040f:
            r32 = 1
            goto L_0x0421
        L_0x0412:
            r40 = r3
            r14 = r4
            goto L_0x041f
        L_0x0416:
            r40 = r3
            r14 = r4
            r12 = r23
            r8 = r32
            r32 = r6
        L_0x041f:
            r23 = r10
        L_0x0421:
            if (r32 != 0) goto L_0x043d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r3.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = "sound_enabled_"
            r3.append(r4)     // Catch:{ Exception -> 0x0b00 }
            r3.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0b00 }
            r4 = 1
            boolean r3 = r12.getBoolean(r3, r4)     // Catch:{ Exception -> 0x0b00 }
            if (r3 != 0) goto L_0x043d
            r32 = 1
        L_0x043d:
            android.net.Uri r3 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = r3.getPath()     // Catch:{ Exception -> 0x0b00 }
            boolean r4 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0b00 }
            if (r4 != 0) goto L_0x044a
            r33 = 1
            goto L_0x044c
        L_0x044a:
            r33 = 0
        L_0x044c:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r4.<init>()     // Catch:{ Exception -> 0x0b00 }
            r4.append(r7)     // Catch:{ Exception -> 0x0b00 }
            r4.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b00 }
            r6 = 0
            boolean r4 = r12.getBoolean(r4, r6)     // Catch:{ Exception -> 0x0b00 }
            r6 = 3
            if (r4 == 0) goto L_0x0509
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r4.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r7 = "vibrate_"
            r4.append(r7)     // Catch:{ Exception -> 0x0b00 }
            r4.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0b00 }
            r7 = 0
            int r4 = r12.getInt(r4, r7)     // Catch:{ Exception -> 0x0b00 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r7.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r10 = "priority_"
            r7.append(r10)     // Catch:{ Exception -> 0x0b00 }
            r7.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0b00 }
            int r7 = r12.getInt(r7, r6)     // Catch:{ Exception -> 0x0b00 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r10.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r11 = "sound_document_id_"
            r10.append(r11)     // Catch:{ Exception -> 0x0b00 }
            r10.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0b00 }
            r36 = r7
            r6 = 0
            long r10 = r12.getLong(r10, r6)     // Catch:{ Exception -> 0x0b00 }
            int r38 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r38 == 0) goto L_0x04b7
            org.telegram.messenger.MediaDataController r6 = r47.getMediaDataController()     // Catch:{ Exception -> 0x0b00 }
            org.telegram.messenger.ringtone.RingtoneDataStore r6 = r6.ringtoneDataStore     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r6 = r6.getSoundPath(r10)     // Catch:{ Exception -> 0x0b00 }
            r7 = 1
            goto L_0x04ce
        L_0x04b7:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r6.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r7 = "sound_path_"
            r6.append(r7)     // Catch:{ Exception -> 0x0b00 }
            r6.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0b00 }
            r7 = 0
            java.lang.String r6 = r12.getString(r6, r7)     // Catch:{ Exception -> 0x0b00 }
            r7 = 0
        L_0x04ce:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r10.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r11 = "color_"
            r10.append(r11)     // Catch:{ Exception -> 0x0b00 }
            r10.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0b00 }
            boolean r10 = r12.contains(r10)     // Catch:{ Exception -> 0x0b00 }
            if (r10 == 0) goto L_0x0500
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r10.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r11 = "color_"
            r10.append(r11)     // Catch:{ Exception -> 0x0b00 }
            r10.append(r8)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0b00 }
            r11 = 0
            int r10 = r12.getInt(r10, r11)     // Catch:{ Exception -> 0x0b00 }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0501
        L_0x0500:
            r10 = 0
        L_0x0501:
            r11 = r4
            r46 = r36
            r36 = r7
            r7 = r46
            goto L_0x050f
        L_0x0509:
            r6 = 0
            r7 = 3
            r10 = 0
            r11 = 0
            r36 = 0
        L_0x050f:
            r38 = r5
            r4 = 0
            int r18 = (r30 > r4 ? 1 : (r30 == r4 ? 0 : -1))
            if (r18 == 0) goto L_0x059a
            if (r29 == 0) goto L_0x0559
            r29 = r11
            java.lang.String r11 = "ChannelSoundDocId"
            r41 = r1
            r42 = r2
            long r1 = r12.getLong(r11, r4)     // Catch:{ Exception -> 0x0b00 }
            int r11 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x0535
            org.telegram.messenger.MediaDataController r4 = r47.getMediaDataController()     // Catch:{ Exception -> 0x0b00 }
            org.telegram.messenger.ringtone.RingtoneDataStore r4 = r4.ringtoneDataStore     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r1 = r4.getSoundPath(r1)     // Catch:{ Exception -> 0x0b00 }
            r2 = 1
            goto L_0x053c
        L_0x0535:
            java.lang.String r1 = "ChannelSoundPath"
            java.lang.String r1 = r12.getString(r1, r3)     // Catch:{ Exception -> 0x0b00 }
            r2 = 0
        L_0x053c:
            java.lang.String r4 = "vibrate_channel"
            r5 = 0
            int r4 = r12.getInt(r4, r5)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r5 = "priority_channel"
            r11 = 1
            int r5 = r12.getInt(r5, r11)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r11 = "ChannelLed"
            r43 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r12.getInt(r11, r1)     // Catch:{ Exception -> 0x0b00 }
            r39 = 2
            goto L_0x05dd
        L_0x0559:
            r41 = r1
            r42 = r2
            r29 = r11
            java.lang.String r1 = "GroupSoundDocId"
            r4 = 0
            long r1 = r12.getLong(r1, r4)     // Catch:{ Exception -> 0x0b00 }
            int r11 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x0577
            org.telegram.messenger.MediaDataController r4 = r47.getMediaDataController()     // Catch:{ Exception -> 0x0b00 }
            org.telegram.messenger.ringtone.RingtoneDataStore r4 = r4.ringtoneDataStore     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r1 = r4.getSoundPath(r1)     // Catch:{ Exception -> 0x0b00 }
            r2 = 1
            goto L_0x057e
        L_0x0577:
            java.lang.String r1 = "GroupSoundPath"
            java.lang.String r1 = r12.getString(r1, r3)     // Catch:{ Exception -> 0x0b00 }
            r2 = 0
        L_0x057e:
            java.lang.String r4 = "vibrate_group"
            r5 = 0
            int r4 = r12.getInt(r4, r5)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r5 = "priority_group"
            r11 = 1
            int r5 = r12.getInt(r5, r11)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r11 = "GroupLed"
            r43 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r12.getInt(r11, r1)     // Catch:{ Exception -> 0x0b00 }
            r39 = 0
            goto L_0x05dd
        L_0x059a:
            r41 = r1
            r42 = r2
            r1 = r4
            r29 = r11
            int r4 = (r21 > r1 ? 1 : (r21 == r1 ? 0 : -1))
            if (r4 == 0) goto L_0x05e7
            java.lang.String r4 = "GlobalSoundDocId"
            long r4 = r12.getLong(r4, r1)     // Catch:{ Exception -> 0x0b00 }
            int r11 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1))
            if (r11 == 0) goto L_0x05bb
            org.telegram.messenger.MediaDataController r1 = r47.getMediaDataController()     // Catch:{ Exception -> 0x0b00 }
            org.telegram.messenger.ringtone.RingtoneDataStore r1 = r1.ringtoneDataStore     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r1 = r1.getSoundPath(r4)     // Catch:{ Exception -> 0x0b00 }
            r2 = 1
            goto L_0x05c2
        L_0x05bb:
            java.lang.String r1 = "GlobalSoundPath"
            java.lang.String r1 = r12.getString(r1, r3)     // Catch:{ Exception -> 0x0b00 }
            r2 = 0
        L_0x05c2:
            java.lang.String r4 = "vibrate_messages"
            r5 = 0
            int r4 = r12.getInt(r4, r5)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r5 = "priority_messages"
            r11 = 1
            int r5 = r12.getInt(r5, r11)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r11 = "MessagesLed"
            r43 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r1 = r12.getInt(r11, r1)     // Catch:{ Exception -> 0x0b00 }
            r39 = 1
        L_0x05dd:
            r46 = r4
            r4 = r1
            r1 = r43
            r43 = r2
            r2 = r46
            goto L_0x05f4
        L_0x05e7:
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r1 = 0
            r2 = 0
            r4 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r5 = 0
            r39 = 1
            r43 = 0
        L_0x05f4:
            r11 = 4
            if (r2 != r11) goto L_0x05fb
            r2 = 0
            r44 = 1
            goto L_0x05fd
        L_0x05fb:
            r44 = 0
        L_0x05fd:
            boolean r45 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x0b00 }
            if (r45 != 0) goto L_0x060c
            boolean r45 = android.text.TextUtils.equals(r1, r6)     // Catch:{ Exception -> 0x0b00 }
            if (r45 != 0) goto L_0x060c
            r1 = r6
            r6 = 0
            goto L_0x060f
        L_0x060c:
            r36 = r43
            r6 = 1
        L_0x060f:
            r11 = 3
            if (r7 == r11) goto L_0x0618
            r11 = r29
            if (r5 == r7) goto L_0x061a
            r6 = 0
            goto L_0x061b
        L_0x0618:
            r11 = r29
        L_0x061a:
            r7 = r5
        L_0x061b:
            if (r10 == 0) goto L_0x0628
            int r5 = r10.intValue()     // Catch:{ Exception -> 0x0b00 }
            if (r5 == r4) goto L_0x0628
            int r4 = r10.intValue()     // Catch:{ Exception -> 0x0b00 }
            r6 = 0
        L_0x0628:
            if (r11 == 0) goto L_0x0632
            r5 = 4
            if (r11 == r5) goto L_0x0632
            if (r11 == r2) goto L_0x0632
            r2 = r11
            r11 = 0
            goto L_0x0633
        L_0x0632:
            r11 = r6
        L_0x0633:
            if (r33 == 0) goto L_0x0657
            java.lang.String r5 = "EnableInAppSounds"
            r6 = 1
            boolean r5 = r12.getBoolean(r5, r6)     // Catch:{ Exception -> 0x0b00 }
            if (r5 != 0) goto L_0x063f
            r1 = 0
        L_0x063f:
            java.lang.String r5 = "EnableInAppVibrate"
            boolean r5 = r12.getBoolean(r5, r6)     // Catch:{ Exception -> 0x0b00 }
            if (r5 != 0) goto L_0x0648
            r2 = 2
        L_0x0648:
            java.lang.String r5 = "EnableInAppPriority"
            r6 = 0
            boolean r5 = r12.getBoolean(r5, r6)     // Catch:{ Exception -> 0x0b00 }
            if (r5 != 0) goto L_0x0653
            r7 = 0
            goto L_0x0657
        L_0x0653:
            r5 = 2
            if (r7 != r5) goto L_0x0657
            r7 = 1
        L_0x0657:
            if (r44 == 0) goto L_0x066e
            r5 = 2
            if (r2 == r5) goto L_0x066e
            android.media.AudioManager r5 = audioManager     // Catch:{ Exception -> 0x0669 }
            int r5 = r5.getRingerMode()     // Catch:{ Exception -> 0x0669 }
            if (r5 == 0) goto L_0x066e
            r6 = 1
            if (r5 == r6) goto L_0x066e
            r2 = 2
            goto L_0x066e
        L_0x0669:
            r0 = move-exception
            r5 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)     // Catch:{ Exception -> 0x0b00 }
        L_0x066e:
            if (r32 == 0) goto L_0x0675
            r1 = 0
            r2 = 0
            r7 = 0
            r10 = 0
            goto L_0x0676
        L_0x0675:
            r10 = r4
        L_0x0676:
            android.content.Intent r4 = new android.content.Intent     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r6 = org.telegram.ui.LaunchActivity.class
            r4.<init>(r5, r6)     // Catch:{ Exception -> 0x0b00 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r5.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r6 = "com.tmessages.openchat"
            r5.append(r6)     // Catch:{ Exception -> 0x0b00 }
            r44 = r10
            r43 = r11
            double r10 = java.lang.Math.random()     // Catch:{ Exception -> 0x0b00 }
            r5.append(r10)     // Catch:{ Exception -> 0x0b00 }
            r6 = 2147483647(0x7fffffff, float:NaN)
            r5.append(r6)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0b00 }
            r4.setAction(r5)     // Catch:{ Exception -> 0x0b00 }
            r5 = 67108864(0x4000000, float:1.5046328E-36)
            r4.setFlags(r5)     // Catch:{ Exception -> 0x0b00 }
            boolean r5 = org.telegram.messenger.DialogObject.isEncryptedDialog(r8)     // Catch:{ Exception -> 0x0b00 }
            if (r5 != 0) goto L_0x071e
            androidx.collection.LongSparseArray<java.lang.Integer> r5 = r15.pushDialogs     // Catch:{ Exception -> 0x0b00 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0b00 }
            r6 = 1
            if (r5 != r6) goto L_0x06ce
            r5 = 0
            int r10 = (r30 > r5 ? 1 : (r30 == r5 ? 0 : -1))
            if (r10 == 0) goto L_0x06c3
            java.lang.String r10 = "chatId"
            r5 = r30
            r4.putExtra(r10, r5)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x06ce
        L_0x06c3:
            int r10 = (r21 > r5 ? 1 : (r21 == r5 ? 0 : -1))
            if (r10 == 0) goto L_0x06ce
            java.lang.String r5 = "userId"
            r10 = r21
            r4.putExtra(r5, r10)     // Catch:{ Exception -> 0x0b00 }
        L_0x06ce:
            boolean r5 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b00 }
            if (r5 != 0) goto L_0x071b
            boolean r5 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b00 }
            if (r5 == 0) goto L_0x06d9
            goto L_0x071b
        L_0x06d9:
            androidx.collection.LongSparseArray<java.lang.Integer> r5 = r15.pushDialogs     // Catch:{ Exception -> 0x0b00 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0b00 }
            r6 = 1
            if (r5 != r6) goto L_0x071b
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b00 }
            r6 = 28
            if (r5 >= r6) goto L_0x071b
            if (r13 == 0) goto L_0x0702
            org.telegram.tgnet.TLRPC$ChatPhoto r5 = r13.photo     // Catch:{ Exception -> 0x0b00 }
            if (r5 == 0) goto L_0x071b
            org.telegram.tgnet.TLRPC$FileLocation r5 = r5.photo_small     // Catch:{ Exception -> 0x0b00 }
            if (r5 == 0) goto L_0x071b
            long r10 = r5.volume_id     // Catch:{ Exception -> 0x0b00 }
            r18 = 0
            int r6 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1))
            if (r6 == 0) goto L_0x071b
            int r6 = r5.local_id     // Catch:{ Exception -> 0x0b00 }
            if (r6 == 0) goto L_0x071b
            r6 = r5
            r5 = r28
            goto L_0x0739
        L_0x0702:
            if (r28 == 0) goto L_0x071b
            r5 = r28
            org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r5.photo     // Catch:{ Exception -> 0x0b00 }
            if (r6 == 0) goto L_0x0738
            org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small     // Catch:{ Exception -> 0x0b00 }
            if (r6 == 0) goto L_0x0738
            long r10 = r6.volume_id     // Catch:{ Exception -> 0x0b00 }
            r18 = 0
            int r21 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1))
            if (r21 == 0) goto L_0x0738
            int r10 = r6.local_id     // Catch:{ Exception -> 0x0b00 }
            if (r10 == 0) goto L_0x0738
            goto L_0x0739
        L_0x071b:
            r5 = r28
            goto L_0x0738
        L_0x071e:
            r5 = r28
            androidx.collection.LongSparseArray<java.lang.Integer> r6 = r15.pushDialogs     // Catch:{ Exception -> 0x0b00 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0b00 }
            r10 = 1
            if (r6 != r10) goto L_0x0738
            long r10 = globalSecretChatId     // Catch:{ Exception -> 0x0b00 }
            int r6 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r6 == 0) goto L_0x0738
            java.lang.String r6 = "encId"
            int r10 = org.telegram.messenger.DialogObject.getEncryptedChatId(r8)     // Catch:{ Exception -> 0x0b00 }
            r4.putExtra(r6, r10)     // Catch:{ Exception -> 0x0b00 }
        L_0x0738:
            r6 = 0
        L_0x0739:
            int r10 = r15.currentAccount     // Catch:{ Exception -> 0x0b00 }
            r11 = r17
            r4.putExtra(r11, r10)     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            r21 = r8
            r8 = 1073741824(0x40000000, float:2.0)
            r9 = 0
            android.app.PendingIntent r4 = android.app.PendingIntent.getActivity(r10, r9, r4, r8)     // Catch:{ Exception -> 0x0b00 }
            r9 = r41
            r8 = r42
            androidx.core.app.NotificationCompat$Builder r9 = r8.setContentTitle(r9)     // Catch:{ Exception -> 0x0b00 }
            r10 = 2131165961(0x7var_, float:1.7946154E38)
            androidx.core.app.NotificationCompat$Builder r9 = r9.setSmallIcon(r10)     // Catch:{ Exception -> 0x0b00 }
            r10 = 1
            androidx.core.app.NotificationCompat$Builder r9 = r9.setAutoCancel(r10)     // Catch:{ Exception -> 0x0b00 }
            int r10 = r15.total_unread_count     // Catch:{ Exception -> 0x0b00 }
            androidx.core.app.NotificationCompat$Builder r9 = r9.setNumber(r10)     // Catch:{ Exception -> 0x0b00 }
            androidx.core.app.NotificationCompat$Builder r4 = r9.setContentIntent(r4)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r9 = r15.notificationGroup     // Catch:{ Exception -> 0x0b00 }
            androidx.core.app.NotificationCompat$Builder r4 = r4.setGroup(r9)     // Catch:{ Exception -> 0x0b00 }
            r9 = 1
            androidx.core.app.NotificationCompat$Builder r4 = r4.setGroupSummary(r9)     // Catch:{ Exception -> 0x0b00 }
            androidx.core.app.NotificationCompat$Builder r4 = r4.setShowWhen(r9)     // Catch:{ Exception -> 0x0b00 }
            r9 = r27
            org.telegram.tgnet.TLRPC$Message r10 = r9.messageOwner     // Catch:{ Exception -> 0x0b00 }
            int r10 = r10.date     // Catch:{ Exception -> 0x0b00 }
            r27 = r2
            r17 = r3
            long r2 = (long) r10     // Catch:{ Exception -> 0x0b00 }
            r30 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 * r30
            androidx.core.app.NotificationCompat$Builder r2 = r4.setWhen(r2)     // Catch:{ Exception -> 0x0b00 }
            r3 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r2.setColor(r3)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r2 = "msg"
            r8.setCategory(r2)     // Catch:{ Exception -> 0x0b00 }
            if (r13 != 0) goto L_0x07ba
            if (r5 == 0) goto L_0x07ba
            java.lang.String r2 = r5.phone     // Catch:{ Exception -> 0x0b00 }
            if (r2 == 0) goto L_0x07ba
            int r2 = r2.length()     // Catch:{ Exception -> 0x0b00 }
            if (r2 <= 0) goto L_0x07ba
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r2.<init>()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = "tel:+"
            r2.append(r3)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = r5.phone     // Catch:{ Exception -> 0x0b00 }
            r2.append(r3)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0b00 }
            r8.addPerson(r2)     // Catch:{ Exception -> 0x0b00 }
        L_0x07ba:
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r4 = org.telegram.messenger.NotificationDismissReceiver.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = "messageDate"
            org.telegram.tgnet.TLRPC$Message r4 = r9.messageOwner     // Catch:{ Exception -> 0x0b00 }
            int r4 = r4.date     // Catch:{ Exception -> 0x0b00 }
            r2.putExtra(r3, r4)     // Catch:{ Exception -> 0x0b00 }
            int r3 = r15.currentAccount     // Catch:{ Exception -> 0x0b00 }
            r2.putExtra(r11, r3)     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            r4 = 134217728(0x8000000, float:3.85186E-34)
            r5 = 1
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r3, r5, r2, r4)     // Catch:{ Exception -> 0x0b00 }
            r8.setDeleteIntent(r2)     // Catch:{ Exception -> 0x0b00 }
            if (r6 == 0) goto L_0x0829
            org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = "50_50"
            r5 = 0
            android.graphics.drawable.BitmapDrawable r2 = r2.getImageFromMemory(r6, r5, r3)     // Catch:{ Exception -> 0x0b00 }
            if (r2 == 0) goto L_0x07f4
            android.graphics.Bitmap r2 = r2.getBitmap()     // Catch:{ Exception -> 0x0b00 }
            r8.setLargeIcon(r2)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x082a
        L_0x07f4:
            r2 = 1
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r2)     // Catch:{ all -> 0x0827 }
            boolean r2 = r3.exists()     // Catch:{ all -> 0x0827 }
            if (r2 == 0) goto L_0x082a
            r2 = 1126170624(0x43200000, float:160.0)
            r6 = 1112014848(0x42480000, float:50.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ all -> 0x0827 }
            float r6 = (float) r6     // Catch:{ all -> 0x0827 }
            float r2 = r2 / r6
            android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0827 }
            r6.<init>()     // Catch:{ all -> 0x0827 }
            r10 = 1065353216(0x3var_, float:1.0)
            int r10 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r10 >= 0) goto L_0x0816
            r2 = 1
            goto L_0x0817
        L_0x0816:
            int r2 = (int) r2     // Catch:{ all -> 0x0827 }
        L_0x0817:
            r6.inSampleSize = r2     // Catch:{ all -> 0x0827 }
            java.lang.String r2 = r3.getAbsolutePath()     // Catch:{ all -> 0x0827 }
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r2, r6)     // Catch:{ all -> 0x0827 }
            if (r2 == 0) goto L_0x082a
            r8.setLargeIcon(r2)     // Catch:{ all -> 0x0827 }
            goto L_0x082a
        L_0x0827:
            goto L_0x082a
        L_0x0829:
            r5 = 0
        L_0x082a:
            r2 = 5
            r3 = 26
            r6 = r38
            if (r48 == 0) goto L_0x086d
            r10 = 1
            if (r6 != r10) goto L_0x0835
            goto L_0x086d
        L_0x0835:
            if (r7 != 0) goto L_0x0842
            r10 = 0
            r8.setPriority(r10)     // Catch:{ Exception -> 0x0b00 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b00 }
            if (r7 < r3) goto L_0x0878
            r7 = 1
            r10 = 3
            goto L_0x087a
        L_0x0842:
            r10 = 1
            if (r7 == r10) goto L_0x0862
            r10 = 2
            if (r7 != r10) goto L_0x0849
            goto L_0x0862
        L_0x0849:
            r10 = 4
            if (r7 != r10) goto L_0x0857
            r7 = -2
            r8.setPriority(r7)     // Catch:{ Exception -> 0x0b00 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b00 }
            if (r7 < r3) goto L_0x0878
            r7 = 1
            r10 = 1
            goto L_0x087a
        L_0x0857:
            if (r7 != r2) goto L_0x0878
            r7 = -1
            r8.setPriority(r7)     // Catch:{ Exception -> 0x0b00 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b00 }
            if (r7 < r3) goto L_0x0878
            goto L_0x0875
        L_0x0862:
            r7 = 1
            r8.setPriority(r7)     // Catch:{ Exception -> 0x0b00 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b00 }
            if (r7 < r3) goto L_0x0878
            r7 = 1
            r10 = 4
            goto L_0x087a
        L_0x086d:
            r7 = -1
            r8.setPriority(r7)     // Catch:{ Exception -> 0x0b00 }
            int r7 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b00 }
            if (r7 < r3) goto L_0x0878
        L_0x0875:
            r7 = 1
            r10 = 2
            goto L_0x087a
        L_0x0878:
            r7 = 1
            r10 = 0
        L_0x087a:
            if (r6 == r7) goto L_0x09b5
            if (r32 != 0) goto L_0x09b5
            if (r33 == 0) goto L_0x0888
            java.lang.String r6 = "EnableInAppPreview"
            boolean r6 = r12.getBoolean(r6, r7)     // Catch:{ Exception -> 0x0b00 }
            if (r6 == 0) goto L_0x08bb
        L_0x0888:
            int r6 = r14.length()     // Catch:{ Exception -> 0x0b00 }
            r7 = 100
            if (r6 <= r7) goto L_0x08b6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0b00 }
            r6.<init>()     // Catch:{ Exception -> 0x0b00 }
            r7 = 100
            r12 = r14
            r13 = 0
            java.lang.String r7 = r12.substring(r13, r7)     // Catch:{ Exception -> 0x0b00 }
            r12 = 32
            r13 = 10
            java.lang.String r7 = r7.replace(r13, r12)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r7 = r7.trim()     // Catch:{ Exception -> 0x0b00 }
            r6.append(r7)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r7 = "..."
            r6.append(r7)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0b00 }
            goto L_0x08b8
        L_0x08b6:
            r12 = r14
            r6 = r12
        L_0x08b8:
            r8.setTicker(r6)     // Catch:{ Exception -> 0x0b00 }
        L_0x08bb:
            if (r1 == 0) goto L_0x094e
            java.lang.String r6 = "NoSound"
            boolean r6 = r1.equals(r6)     // Catch:{ Exception -> 0x0b00 }
            if (r6 != 0) goto L_0x094e
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b00 }
            if (r6 < r3) goto L_0x08fa
            java.lang.String r2 = "Default"
            boolean r2 = r1.equals(r2)     // Catch:{ Exception -> 0x0b00 }
            if (r2 != 0) goto L_0x08f7
            r3 = r17
            boolean r2 = r1.equals(r3)     // Catch:{ Exception -> 0x0b00 }
            if (r2 == 0) goto L_0x08da
            goto L_0x08f7
        L_0x08da:
            if (r36 == 0) goto L_0x08f2
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = "org.telegram.messenger.web.provider"
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0b00 }
            r6.<init>(r1)     // Catch:{ Exception -> 0x0b00 }
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r2, r3, r6)     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = "com.android.systemui"
            r6 = 1
            r2.grantUriPermission(r3, r1, r6)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x094f
        L_0x08f2:
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x094f
        L_0x08f7:
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b00 }
            goto L_0x094f
        L_0x08fa:
            r3 = r17
            boolean r3 = r1.equals(r3)     // Catch:{ Exception -> 0x0b00 }
            if (r3 == 0) goto L_0x0908
            android.net.Uri r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0b00 }
            r8.setSound(r1, r2)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x094e
        L_0x0908:
            r3 = 24
            if (r6 < r3) goto L_0x0947
            java.lang.String r3 = "file://"
            boolean r3 = r1.startsWith(r3)     // Catch:{ Exception -> 0x0b00 }
            if (r3 == 0) goto L_0x0947
            android.net.Uri r3 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0b00 }
            boolean r3 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r3)     // Catch:{ Exception -> 0x0b00 }
            if (r3 != 0) goto L_0x0947
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x093f }
            java.lang.String r6 = "org.telegram.messenger.web.provider"
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x093f }
            java.lang.String r12 = "file://"
            r13 = r23
            java.lang.String r12 = r1.replace(r12, r13)     // Catch:{ Exception -> 0x093f }
            r7.<init>(r12)     // Catch:{ Exception -> 0x093f }
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r3, r6, r7)     // Catch:{ Exception -> 0x093f }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x093f }
            java.lang.String r7 = "com.android.systemui"
            r12 = 1
            r6.grantUriPermission(r7, r3, r12)     // Catch:{ Exception -> 0x093f }
            r8.setSound(r3, r2)     // Catch:{ Exception -> 0x093f }
            goto L_0x094e
        L_0x093f:
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0b00 }
            r8.setSound(r1, r2)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x094e
        L_0x0947:
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x0b00 }
            r8.setSound(r1, r2)     // Catch:{ Exception -> 0x0b00 }
        L_0x094e:
            r1 = r5
        L_0x094f:
            if (r44 == 0) goto L_0x095b
            r2 = 1000(0x3e8, float:1.401E-42)
            r3 = 1000(0x3e8, float:1.401E-42)
            r12 = r44
            r8.setLights(r12, r2, r3)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x095d
        L_0x095b:
            r12 = r44
        L_0x095d:
            r2 = r27
            r3 = 2
            if (r2 != r3) goto L_0x0972
            long[] r2 = new long[r3]     // Catch:{ Exception -> 0x0b00 }
            r3 = 0
            r5 = 0
            r2[r3] = r5     // Catch:{ Exception -> 0x0b00 }
            r3 = 1
            r2[r3] = r5     // Catch:{ Exception -> 0x0b00 }
            r8.setVibrate(r2)     // Catch:{ Exception -> 0x0b00 }
        L_0x096f:
            r13 = r1
            r7 = r2
            goto L_0x09a9
        L_0x0972:
            r3 = 1
            if (r2 != r3) goto L_0x098d
            r6 = 4
            long[] r2 = new long[r6]     // Catch:{ Exception -> 0x0b00 }
            r5 = 0
            r7 = 0
            r2[r7] = r5     // Catch:{ Exception -> 0x0b00 }
            r13 = 100
            r2[r3] = r13     // Catch:{ Exception -> 0x0b00 }
            r3 = 2
            r2[r3] = r5     // Catch:{ Exception -> 0x0b00 }
            r5 = 100
            r3 = 3
            r2[r3] = r5     // Catch:{ Exception -> 0x0b00 }
            r8.setVibrate(r2)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x096f
        L_0x098d:
            if (r2 == 0) goto L_0x09ab
            r6 = 4
            if (r2 != r6) goto L_0x0993
            goto L_0x09ab
        L_0x0993:
            r3 = 3
            if (r2 != r3) goto L_0x09a7
            r2 = 2
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b00 }
            r2 = 0
            r5 = 0
            r3[r2] = r5     // Catch:{ Exception -> 0x0b00 }
            r2 = 1
            r5 = 1000(0x3e8, double:4.94E-321)
            r3[r2] = r5     // Catch:{ Exception -> 0x0b00 }
            r8.setVibrate(r3)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x09b2
        L_0x09a7:
            r13 = r1
            r7 = r5
        L_0x09a9:
            r1 = 1
            goto L_0x09c7
        L_0x09ab:
            r2 = 2
            r8.setDefaults(r2)     // Catch:{ Exception -> 0x0b00 }
            r2 = 0
            long[] r3 = new long[r2]     // Catch:{ Exception -> 0x0b00 }
        L_0x09b2:
            r13 = r1
            r7 = r3
            goto L_0x09a9
        L_0x09b5:
            r12 = r44
            r1 = 2
            long[] r2 = new long[r1]     // Catch:{ Exception -> 0x0b00 }
            r1 = 0
            r6 = 0
            r2[r1] = r6     // Catch:{ Exception -> 0x0b00 }
            r1 = 1
            r2[r1] = r6     // Catch:{ Exception -> 0x0b00 }
            r8.setVibrate(r2)     // Catch:{ Exception -> 0x0b00 }
            r7 = r2
            r13 = r5
        L_0x09c7:
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0b00 }
            if (r2 != 0) goto L_0x0a88
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0b00 }
            if (r2 != 0) goto L_0x0a88
            long r2 = r9.getDialogId()     // Catch:{ Exception -> 0x0b00 }
            r5 = 777000(0xbdb28, double:3.83889E-318)
            int r14 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r14 != 0) goto L_0x0a88
            org.telegram.tgnet.TLRPC$Message r2 = r9.messageOwner     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r2.reply_markup     // Catch:{ Exception -> 0x0b00 }
            if (r2 == 0) goto L_0x0a88
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r2 = r2.rows     // Catch:{ Exception -> 0x0b00 }
            int r3 = r2.size()     // Catch:{ Exception -> 0x0b00 }
            r5 = 0
            r6 = 0
        L_0x09ea:
            if (r5 >= r3) goto L_0x0a82
            java.lang.Object r14 = r2.get(r5)     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r14 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r14     // Catch:{ Exception -> 0x0b00 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r1 = r14.buttons     // Catch:{ Exception -> 0x0b00 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x0b00 }
            r4 = 0
        L_0x09f9:
            if (r4 >= r1) goto L_0x0a6c
            r48 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r1 = r14.buttons     // Catch:{ Exception -> 0x0b00 }
            java.lang.Object r1 = r1.get(r4)     // Catch:{ Exception -> 0x0b00 }
            org.telegram.tgnet.TLRPC$KeyboardButton r1 = (org.telegram.tgnet.TLRPC$KeyboardButton) r1     // Catch:{ Exception -> 0x0b00 }
            r18 = r2
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0b00 }
            if (r2 == 0) goto L_0x0a50
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            r19 = r3
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r3 = org.telegram.messenger.NotificationCallbackReceiver.class
            r2.<init>(r6, r3)     // Catch:{ Exception -> 0x0b00 }
            int r3 = r15.currentAccount     // Catch:{ Exception -> 0x0b00 }
            r2.putExtra(r11, r3)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r3 = "did"
            r20 = r13
            r23 = r14
            r13 = r21
            r2.putExtra(r3, r13)     // Catch:{ Exception -> 0x0b00 }
            byte[] r3 = r1.data     // Catch:{ Exception -> 0x0b00 }
            if (r3 == 0) goto L_0x0a2f
            java.lang.String r6 = "data"
            r2.putExtra(r6, r3)     // Catch:{ Exception -> 0x0b00 }
        L_0x0a2f:
            java.lang.String r3 = "mid"
            int r6 = r9.getId()     // Catch:{ Exception -> 0x0b00 }
            r2.putExtra(r3, r6)     // Catch:{ Exception -> 0x0b00 }
            java.lang.String r1 = r1.text     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            int r6 = r15.lastButtonId     // Catch:{ Exception -> 0x0b00 }
            r27 = r9
            int r9 = r6 + 1
            r15.lastButtonId = r9     // Catch:{ Exception -> 0x0b00 }
            r9 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r3, r6, r2, r9)     // Catch:{ Exception -> 0x0b00 }
            r3 = 0
            r8.addAction(r3, r1, r2)     // Catch:{ Exception -> 0x0b00 }
            r6 = 1
            goto L_0x0a5b
        L_0x0a50:
            r19 = r3
            r27 = r9
            r20 = r13
            r23 = r14
            r13 = r21
            r3 = 0
        L_0x0a5b:
            int r4 = r4 + 1
            r1 = r48
            r21 = r13
            r2 = r18
            r3 = r19
            r13 = r20
            r14 = r23
            r9 = r27
            goto L_0x09f9
        L_0x0a6c:
            r18 = r2
            r19 = r3
            r27 = r9
            r20 = r13
            r13 = r21
            r3 = 0
            int r5 = r5 + 1
            r3 = r19
            r13 = r20
            r1 = 1
            r4 = 134217728(0x8000000, float:3.85186E-34)
            goto L_0x09ea
        L_0x0a82:
            r20 = r13
            r13 = r21
            r3 = r6
            goto L_0x0a8d
        L_0x0a88:
            r20 = r13
            r13 = r21
            r3 = 0
        L_0x0a8d:
            if (r3 != 0) goto L_0x0ae6
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0b00 }
            r2 = 24
            if (r1 >= r2) goto L_0x0ae6
            java.lang.String r2 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0b00 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x0b00 }
            if (r2 != 0) goto L_0x0ae6
            boolean r2 = r47.hasMessagesToReply()     // Catch:{ Exception -> 0x0b00 }
            if (r2 == 0) goto L_0x0ae6
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r4 = org.telegram.messenger.PopupReplyReceiver.class
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x0b00 }
            int r3 = r15.currentAccount     // Catch:{ Exception -> 0x0b00 }
            r2.putExtra(r11, r3)     // Catch:{ Exception -> 0x0b00 }
            r3 = 19
            if (r1 > r3) goto L_0x0ace
            r1 = 2131165512(0x7var_, float:1.7945243E38)
            java.lang.String r3 = "Reply"
            r4 = 2131627732(0x7f0e0ed4, float:1.8882737E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r4, r6, r2, r5)     // Catch:{ Exception -> 0x0b00 }
            r8.addAction(r1, r3, r2)     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0ae6
        L_0x0ace:
            r1 = 2131165511(0x7var_, float:1.7945241E38)
            java.lang.String r3 = "Reply"
            r4 = 2131627732(0x7f0e0ed4, float:1.8882737E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r4)     // Catch:{ Exception -> 0x0b00 }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0b00 }
            r5 = 134217728(0x8000000, float:3.85186E-34)
            r6 = 2
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r4, r6, r2, r5)     // Catch:{ Exception -> 0x0b00 }
            r8.addAction(r1, r3, r2)     // Catch:{ Exception -> 0x0b00 }
        L_0x0ae6:
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
            r1.showExtraNotifications(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0b00 }
            r47.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0b00 }
            goto L_0x0b05
        L_0x0b00:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0b05:
            return
        L_0x0b06:
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
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0b6b: MOVE  (r1v44 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>) = 
          (r54v1 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>)
        
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
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0347  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0365  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x036c  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x03c0  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x03f5  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0403 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0459  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x046b  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x04af  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x04cb  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x04df  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x04fd  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0507 A[SYNTHETIC, Splitter:B:202:0x0507] */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0564 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x058d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:249:0x05bc  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x05f6  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0638  */
    /* JADX WARNING: Removed duplicated region for block: B:289:0x06b2  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0710  */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x0730  */
    /* JADX WARNING: Removed duplicated region for block: B:313:0x0737  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x07ba  */
    /* JADX WARNING: Removed duplicated region for block: B:380:0x08a6  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x08c7  */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x08f6  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0968  */
    /* JADX WARNING: Removed duplicated region for block: B:407:0x0972  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x099d  */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x09f7  */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x0a2e  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a53  */
    /* JADX WARNING: Removed duplicated region for block: B:426:0x0a75  */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x0b28  */
    /* JADX WARNING: Removed duplicated region for block: B:431:0x0b33  */
    /* JADX WARNING: Removed duplicated region for block: B:433:0x0b38  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0b48  */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x0b4e  */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0b56  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0b5b  */
    /* JADX WARNING: Removed duplicated region for block: B:453:0x0b76  */
    /* JADX WARNING: Removed duplicated region for block: B:466:0x0bfd A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:475:0x0c2e  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0206  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0210  */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r73, java.lang.String r74, long r75, java.lang.String r77, long[] r78, int r79, android.net.Uri r80, int r81, boolean r82, boolean r83, boolean r84, int r85) {
        /*
            r72 = this;
            r15 = r72
            r14 = r73
            int r0 = android.os.Build.VERSION.SDK_INT
            r13 = 26
            if (r0 < r13) goto L_0x0027
            r1 = r72
            r2 = r75
            r4 = r77
            r5 = r78
            r6 = r79
            r7 = r80
            r8 = r81
            r9 = r82
            r10 = r83
            r11 = r84
            r12 = r85
            java.lang.String r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r14.setChannelId(r1)
        L_0x0027:
            android.app.Notification r12 = r73.build()
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
            org.telegram.messenger.AccountInstance r0 = r72.getAccountInstance()
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
            org.telegram.messenger.UserConfig r0 = r72.getUserConfig()
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
            if (r4 >= r6) goto L_0x0ca9
            int r0 = r7.size()
            if (r0 < r1) goto L_0x0112
            goto L_0x0ca9
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
            if (r0 != 0) goto L_0x02af
            int r0 = (r6 > r29 ? 1 : (r6 == r29 ? 0 : -1))
            if (r0 == 0) goto L_0x018f
            r0 = 1
            goto L_0x0190
        L_0x018f:
            r0 = 0
        L_0x0190:
            boolean r33 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r33 == 0) goto L_0x0227
            org.telegram.messenger.MessagesController r8 = r72.getMessagesController()
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
            r68 = r12
            r70 = r13
            r14 = r20
            r20 = r28
            goto L_0x02f1
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
            if (r5 == 0) goto L_0x0210
            r5 = 2131627725(0x7f0e0ecd, float:1.8882723E38)
            java.lang.String r8 = "RepliesTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r5)
            goto L_0x021d
        L_0x0210:
            int r5 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r5 != 0) goto L_0x021d
            r5 = 2131626494(0x7f0e09fe, float:1.8880226E38)
            java.lang.String r8 = "MessageScheduledReminderNotification"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r5)
        L_0x021d:
            r38 = r1
            r39 = r4
            r1 = 0
            r5 = 0
            r35 = 0
            goto L_0x033a
        L_0x0227:
            r34 = r0
            r37 = r4
            r36 = r5
            org.telegram.messenger.MessagesController r0 = r72.getMessagesController()
            long r4 = -r6
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
            if (r0 != 0) goto L_0x0270
            boolean r4 = r10.isFcmMessage()
            if (r4 == 0) goto L_0x0256
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
            goto L_0x033f
        L_0x0256:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02e5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "not found chat to show dialog notification "
            r0.append(r1)
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02e5
        L_0x0270:
            boolean r4 = r0.megagroup
            boolean r5 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r5 == 0) goto L_0x027e
            boolean r5 = r0.megagroup
            if (r5 != 0) goto L_0x027e
            r5 = 1
            goto L_0x027f
        L_0x027e:
            r5 = 0
        L_0x027f:
            java.lang.String r8 = r0.title
            r35 = r4
            org.telegram.tgnet.TLRPC$ChatPhoto r4 = r0.photo
            if (r4 == 0) goto L_0x02a2
            org.telegram.tgnet.TLRPC$FileLocation r4 = r4.photo_small
            if (r4 == 0) goto L_0x02a2
            r39 = r0
            r38 = r1
            long r0 = r4.volume_id
            int r40 = (r0 > r31 ? 1 : (r0 == r31 ? 0 : -1))
            if (r40 == 0) goto L_0x02a6
            int r0 = r4.local_id
            if (r0 == 0) goto L_0x02a6
            r1 = r5
            r0 = r8
            r5 = r39
            r8 = 0
            r39 = r4
            goto L_0x033f
        L_0x02a2:
            r39 = r0
            r38 = r1
        L_0x02a6:
            r1 = r5
            r0 = r8
            r5 = r39
            r8 = 0
            r39 = 0
            goto L_0x033f
        L_0x02af:
            r38 = r1
            r37 = r4
            r36 = r5
            long r0 = globalSecretChatId
            int r4 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1))
            if (r4 == 0) goto L_0x0328
            int r0 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            org.telegram.messenger.MessagesController r1 = r72.getMessagesController()
            java.lang.Integer r4 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r4)
            if (r1 != 0) goto L_0x02fd
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x02e5
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "not found secret chat to show dialog notification "
            r1.append(r4)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x02e5:
            r29 = r2
            r68 = r12
            r70 = r13
            r14 = r20
            r20 = r28
            r23 = r36
        L_0x02f1:
            r22 = 7
            r27 = 26
            r28 = 0
            r31 = 1
            r32 = 27
            goto L_0x0CLASSNAME
        L_0x02fd:
            org.telegram.messenger.MessagesController r0 = r72.getMessagesController()
            long r4 = r1.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r4)
            if (r0 != 0) goto L_0x0329
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02e5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "not found secret chat user to show dialog notification "
            r0.append(r4)
            long r4 = r1.user_id
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02e5
        L_0x0328:
            r0 = 0
        L_0x0329:
            r1 = 2131627935(0x7f0e0f9f, float:1.8883148E38)
            java.lang.String r4 = "SecretChatName"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r1 = 0
            r5 = 0
            r34 = 0
            r35 = 0
            r39 = 0
        L_0x033a:
            r71 = r8
            r8 = r0
            r0 = r71
        L_0x033f:
            java.lang.String r4 = "NotificationHiddenChatName"
            r41 = r12
            java.lang.String r12 = "NotificationHiddenName"
            if (r19 == 0) goto L_0x0365
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r0 == 0) goto L_0x0357
            r43 = r8
            r8 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r8)
            goto L_0x0360
        L_0x0357:
            r43 = r8
            r8 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r8)
        L_0x0360:
            r8 = r0
            r0 = 0
            r34 = 0
            goto L_0x036a
        L_0x0365:
            r43 = r8
            r8 = r0
            r0 = r39
        L_0x036a:
            if (r0 == 0) goto L_0x03c0
            r14 = 1
            java.io.File r44 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r14)
            int r14 = android.os.Build.VERSION.SDK_INT
            r45 = r12
            r12 = 28
            if (r14 >= r12) goto L_0x03b9
            org.telegram.messenger.ImageLoader r12 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r14 = "50_50"
            r46 = r4
            r4 = 0
            android.graphics.drawable.BitmapDrawable r0 = r12.getImageFromMemory(r0, r4, r14)
            if (r0 == 0) goto L_0x038e
            android.graphics.Bitmap r0 = r0.getBitmap()
        L_0x038c:
            r12 = r0
            goto L_0x03bd
        L_0x038e:
            boolean r0 = r44.exists()     // Catch:{ all -> 0x03bc }
            if (r0 == 0) goto L_0x03b7
            r0 = 1126170624(0x43200000, float:160.0)
            r12 = 1112014848(0x42480000, float:50.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x03bc }
            float r12 = (float) r12     // Catch:{ all -> 0x03bc }
            float r0 = r0 / r12
            android.graphics.BitmapFactory$Options r12 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x03bc }
            r12.<init>()     // Catch:{ all -> 0x03bc }
            r14 = 1065353216(0x3var_, float:1.0)
            int r14 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
            if (r14 >= 0) goto L_0x03ab
            r0 = 1
            goto L_0x03ac
        L_0x03ab:
            int r0 = (int) r0     // Catch:{ all -> 0x03bc }
        L_0x03ac:
            r12.inSampleSize = r0     // Catch:{ all -> 0x03bc }
            java.lang.String r0 = r44.getAbsolutePath()     // Catch:{ all -> 0x03bc }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r12)     // Catch:{ all -> 0x03bc }
            goto L_0x038c
        L_0x03b7:
            r0 = r4
            goto L_0x038c
        L_0x03b9:
            r46 = r4
            r4 = 0
        L_0x03bc:
            r12 = r4
        L_0x03bd:
            r14 = r44
            goto L_0x03c7
        L_0x03c0:
            r46 = r4
            r45 = r12
            r4 = 0
            r12 = r4
            r14 = r12
        L_0x03c7:
            if (r5 == 0) goto L_0x03f5
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r8)
            if (r14 == 0) goto L_0x03e6
            boolean r33 = r14.exists()
            if (r33 == 0) goto L_0x03e6
            int r4 = android.os.Build.VERSION.SDK_INT
            r44 = r12
            r12 = 28
            if (r4 < r12) goto L_0x03e8
            r15.loadRoundAvatar(r14, r0)
            goto L_0x03e8
        L_0x03e6:
            r44 = r12
        L_0x03e8:
            r4 = r10
            r12 = r11
            long r10 = r5.id
            long r10 = -r10
            androidx.core.app.Person r0 = r0.build()
            r13.put(r10, r0)
            goto L_0x03f9
        L_0x03f5:
            r4 = r10
            r44 = r12
            r12 = r11
        L_0x03f9:
            java.lang.String r10 = "max_id"
            java.lang.String r11 = "dialog_id"
            r47 = r4
            java.lang.String r4 = "currentAccount"
            if (r1 == 0) goto L_0x0405
            if (r35 == 0) goto L_0x049c
        L_0x0405:
            if (r34 == 0) goto L_0x049c
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x049c
            int r0 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r0 == 0) goto L_0x049c
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r0 != 0) goto L_0x049c
            android.content.Intent r0 = new android.content.Intent
            r34 = r14
            android.content.Context r14 = org.telegram.messenger.ApplicationLoader.applicationContext
            r35 = r5
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r5 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r14, r5)
            r0.putExtra(r11, r6)
            r0.putExtra(r10, r9)
            int r5 = r15.currentAccount
            r0.putExtra(r4, r5)
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r14 = r12.intValue()
            r48 = r12
            r12 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r5, r14, r0, r12)
            androidx.core.app.RemoteInput$Builder r5 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r12 = "extra_voice_reply"
            r5.<init>(r12)
            r12 = 2131627732(0x7f0e0ed4, float:1.8882737E38)
            java.lang.String r14 = "Reply"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            androidx.core.app.RemoteInput$Builder r5 = r5.setLabel(r12)
            androidx.core.app.RemoteInput r5 = r5.build()
            boolean r12 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r12 == 0) goto L_0x046b
            r14 = 1
            java.lang.Object[] r12 = new java.lang.Object[r14]
            r14 = 0
            r12[r14] = r8
            java.lang.String r14 = "ReplyToGroup"
            r49 = r9
            r9 = 2131627733(0x7f0e0ed5, float:1.8882739E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r14, r9, r12)
            goto L_0x047c
        L_0x046b:
            r49 = r9
            r9 = 2131627734(0x7f0e0ed6, float:1.888274E38)
            r12 = 1
            java.lang.Object[] r14 = new java.lang.Object[r12]
            r12 = 0
            r14[r12] = r8
            java.lang.String r12 = "ReplyToUser"
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatString(r12, r9, r14)
        L_0x047c:
            androidx.core.app.NotificationCompat$Action$Builder r12 = new androidx.core.app.NotificationCompat$Action$Builder
            r14 = 2131165557(0x7var_, float:1.7945334E38)
            r12.<init>(r14, r9, r0)
            r9 = 1
            androidx.core.app.NotificationCompat$Action$Builder r0 = r12.setAllowGeneratedReplies(r9)
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setSemanticAction(r9)
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.addRemoteInput(r5)
            r5 = 0
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setShowsUserInterface(r5)
            androidx.core.app.NotificationCompat$Action r0 = r0.build()
            r5 = r0
            goto L_0x04a5
        L_0x049c:
            r35 = r5
            r49 = r9
            r48 = r12
            r34 = r14
            r5 = 0
        L_0x04a5:
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.pushDialogs
            java.lang.Object r0 = r0.get(r6)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x04b4
            r9 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r9)
        L_0x04b4:
            int r0 = r0.intValue()
            int r9 = r38.size()
            int r0 = java.lang.Math.max(r0, r9)
            r9 = 2
            r12 = 1
            if (r0 <= r12) goto L_0x04df
            int r14 = android.os.Build.VERSION.SDK_INT
            r12 = 28
            if (r14 < r12) goto L_0x04cb
            goto L_0x04df
        L_0x04cb:
            java.lang.Object[] r12 = new java.lang.Object[r9]
            r14 = 0
            r12[r14] = r8
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r14 = 1
            r12[r14] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r12)
            r12 = r0
            goto L_0x04e0
        L_0x04df:
            r12 = r8
        L_0x04e0:
            java.lang.Object r0 = r13.get(r2)
            r14 = r0
            androidx.core.app.Person r14 = (androidx.core.app.Person) r14
            int r0 = android.os.Build.VERSION.SDK_INT
            r9 = 28
            if (r0 < r9) goto L_0x0552
            if (r14 != 0) goto L_0x0552
            org.telegram.messenger.MessagesController r0 = r72.getMessagesController()
            java.lang.Long r9 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r9)
            if (r0 != 0) goto L_0x0505
            org.telegram.messenger.UserConfig r0 = r72.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x0505:
            if (r0 == 0) goto L_0x0552
            org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r0.photo     // Catch:{ all -> 0x0549 }
            if (r9 == 0) goto L_0x0552
            org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small     // Catch:{ all -> 0x0549 }
            if (r9 == 0) goto L_0x0552
            r50 = r10
            r51 = r11
            long r10 = r9.volume_id     // Catch:{ all -> 0x0547 }
            int r52 = (r10 > r31 ? 1 : (r10 == r31 ? 0 : -1))
            if (r52 == 0) goto L_0x0556
            int r9 = r9.local_id     // Catch:{ all -> 0x0547 }
            if (r9 == 0) goto L_0x0556
            androidx.core.app.Person$Builder r9 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x0547 }
            r9.<init>()     // Catch:{ all -> 0x0547 }
            java.lang.String r10 = "FromYou"
            r11 = 2131625932(0x7f0e07cc, float:1.8879086E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)     // Catch:{ all -> 0x0547 }
            androidx.core.app.Person$Builder r9 = r9.setName(r10)     // Catch:{ all -> 0x0547 }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x0547 }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x0547 }
            r10 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r10)     // Catch:{ all -> 0x0547 }
            r15.loadRoundAvatar(r0, r9)     // Catch:{ all -> 0x0547 }
            androidx.core.app.Person r9 = r9.build()     // Catch:{ all -> 0x0547 }
            r13.put(r2, r9)     // Catch:{ all -> 0x0544 }
            r14 = r9
            goto L_0x0556
        L_0x0544:
            r0 = move-exception
            r14 = r9
            goto L_0x054e
        L_0x0547:
            r0 = move-exception
            goto L_0x054e
        L_0x0549:
            r0 = move-exception
            r50 = r10
            r51 = r11
        L_0x054e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0556
        L_0x0552:
            r50 = r10
            r51 = r11
        L_0x0556:
            r9 = r47
            org.telegram.tgnet.TLRPC$Message r0 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest
            r9 = 1
            r0 = r0 ^ r9
            java.lang.String r9 = ""
            if (r14 == 0) goto L_0x056c
            if (r0 == 0) goto L_0x056c
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((androidx.core.app.Person) r14)
            goto L_0x0571
        L_0x056c:
            androidx.core.app.NotificationCompat$MessagingStyle r0 = new androidx.core.app.NotificationCompat$MessagingStyle
            r0.<init>((java.lang.CharSequence) r9)
        L_0x0571:
            r10 = r0
            int r0 = android.os.Build.VERSION.SDK_INT
            r11 = 28
            if (r0 < r11) goto L_0x0586
            boolean r11 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r11 == 0) goto L_0x0580
            if (r1 == 0) goto L_0x0586
        L_0x0580:
            boolean r11 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r11 == 0) goto L_0x0589
        L_0x0586:
            r10.setConversationTitle(r12)
        L_0x0589:
            r11 = 28
            if (r0 < r11) goto L_0x059e
            if (r1 != 0) goto L_0x0595
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r0 != 0) goto L_0x059e
        L_0x0595:
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r6)
            if (r0 == 0) goto L_0x059c
            goto L_0x059e
        L_0x059c:
            r0 = 0
            goto L_0x059f
        L_0x059e:
            r0 = 1
        L_0x059f:
            r10.setGroupConversation(r0)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r12 = 1
            java.lang.String[] r14 = new java.lang.String[r12]
            r47 = r5
            boolean[] r5 = new boolean[r12]
            int r0 = r38.size()
            int r0 = r0 - r12
            r12 = r0
            r53 = 0
            r54 = 0
        L_0x05b8:
            r55 = 1000(0x3e8, double:4.94E-321)
            if (r12 < 0) goto L_0x0929
            r52 = r4
            r4 = r38
            java.lang.Object r0 = r4.get(r12)
            r4 = r0
            org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
            java.lang.String r0 = r15.getShortStringForMessage(r4, r14, r5)
            r57 = r12
            java.lang.String r12 = "NotificationMessageScheduledName"
            int r58 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r58 != 0) goto L_0x05d8
            r23 = 0
            r14[r23] = r8
            goto L_0x05f2
        L_0x05d8:
            r23 = 0
            boolean r58 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r58 == 0) goto L_0x05f2
            r58 = r8
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
            boolean r8 = r8.from_scheduled
            if (r8 == 0) goto L_0x05f4
            r8 = 2131626852(0x7f0e0b64, float:1.8880952E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r12, r8)
            r14[r23] = r8
            goto L_0x05f4
        L_0x05f2:
            r58 = r8
        L_0x05f4:
            if (r0 != 0) goto L_0x0638
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x062b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "message text is null for "
            r0.append(r8)
            int r8 = r4.getId()
            r0.append(r8)
            java.lang.String r8 = " did = "
            r0.append(r8)
            r59 = r9
            long r8 = r4.getDialogId()
            r0.append(r8)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            r61 = r2
            r60 = r11
            r42 = r45
            r63 = r46
            r2 = r59
            goto L_0x0634
        L_0x062b:
            r61 = r2
            r2 = r9
            r60 = r11
            r42 = r45
            r63 = r46
        L_0x0634:
            r46 = r1
            goto L_0x0916
        L_0x0638:
            r59 = r9
            int r8 = r11.length()
            if (r8 <= 0) goto L_0x0645
            java.lang.String r8 = "\n\n"
            r11.append(r8)
        L_0x0645:
            int r8 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r8 == 0) goto L_0x066f
            org.telegram.tgnet.TLRPC$Message r8 = r4.messageOwner
            boolean r8 = r8.from_scheduled
            if (r8 == 0) goto L_0x066f
            boolean r8 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r8 == 0) goto L_0x066f
            r8 = 2
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r8 = 2131626852(0x7f0e0b64, float:1.8880952E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r12, r8)
            r12 = 0
            r9[r12] = r8
            r8 = 1
            r9[r8] = r0
            java.lang.String r0 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r0, r9)
            r11.append(r0)
            goto L_0x068b
        L_0x066f:
            r12 = 0
            r8 = r14[r12]
            if (r8 == 0) goto L_0x0688
            r8 = 2
            java.lang.Object[] r9 = new java.lang.Object[r8]
            r8 = r14[r12]
            r9[r12] = r8
            r8 = 1
            r9[r8] = r0
            java.lang.String r8 = "%1$s: %2$s"
            java.lang.String r8 = java.lang.String.format(r8, r9)
            r11.append(r8)
            goto L_0x068b
        L_0x0688:
            r11.append(r0)
        L_0x068b:
            r8 = r0
            boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r0 == 0) goto L_0x0694
            r9 = r11
            goto L_0x06a5
        L_0x0694:
            if (r1 == 0) goto L_0x0699
            r9 = r11
            long r11 = -r6
            goto L_0x06a6
        L_0x0699:
            r9 = r11
            boolean r0 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r0 == 0) goto L_0x06a5
            long r11 = r4.getSenderId()
            goto L_0x06a6
        L_0x06a5:
            r11 = r6
        L_0x06a6:
            java.lang.Object r0 = r13.get(r11)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r23 = 0
            r60 = r14[r23]
            if (r60 != 0) goto L_0x0710
            if (r19 == 0) goto L_0x0702
            boolean r60 = org.telegram.messenger.DialogObject.isChatDialog(r6)
            if (r60 == 0) goto L_0x06e9
            if (r1 == 0) goto L_0x06d5
            r60 = r9
            int r9 = android.os.Build.VERSION.SDK_INT
            r61 = r2
            r2 = 27
            if (r9 <= r2) goto L_0x06d2
            r9 = r46
            r3 = 2131626806(0x7f0e0b36, float:1.8880859E38)
            java.lang.String r17 = org.telegram.messenger.LocaleController.getString(r9, r3)
            r2 = r17
            goto L_0x06e6
        L_0x06d2:
            r9 = r46
            goto L_0x06ff
        L_0x06d5:
            r61 = r2
            r60 = r9
            r9 = r46
            r2 = 27
            r3 = 2131626807(0x7f0e0b37, float:1.888086E38)
            java.lang.String r2 = "NotificationHiddenChatUserName"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)
        L_0x06e6:
            r3 = r45
            goto L_0x0721
        L_0x06e9:
            r61 = r2
            r60 = r9
            r9 = r46
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 27
            if (r2 <= r3) goto L_0x06ff
            r3 = r45
            r2 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            java.lang.String r42 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x071f
        L_0x06ff:
            r3 = r45
            goto L_0x070a
        L_0x0702:
            r61 = r2
            r60 = r9
            r3 = r45
            r9 = r46
        L_0x070a:
            r2 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            r2 = r59
            goto L_0x0721
        L_0x0710:
            r61 = r2
            r60 = r9
            r3 = r45
            r9 = r46
            r2 = 2131626809(0x7f0e0b39, float:1.8880865E38)
            r23 = 0
            r42 = r14[r23]
        L_0x071f:
            r2 = r42
        L_0x0721:
            r42 = r3
            if (r0 == 0) goto L_0x0737
            java.lang.CharSequence r3 = r0.getName()
            boolean r3 = android.text.TextUtils.equals(r3, r2)
            if (r3 != 0) goto L_0x0730
            goto L_0x0737
        L_0x0730:
            r46 = r1
            r63 = r9
        L_0x0734:
            r1 = r0
            goto L_0x07b4
        L_0x0737:
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r2)
            r2 = 0
            boolean r3 = r5[r2]
            if (r3 == 0) goto L_0x07a8
            boolean r2 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r2 != 0) goto L_0x07a8
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r2 < r3) goto L_0x07a8
            boolean r2 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r2 != 0) goto L_0x079e
            if (r1 == 0) goto L_0x075a
            goto L_0x079e
        L_0x075a:
            long r2 = r4.getSenderId()
            r46 = r1
            org.telegram.messenger.MessagesController r1 = r72.getMessagesController()
            r63 = r9
            java.lang.Long r9 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r9)
            if (r1 != 0) goto L_0x0782
            org.telegram.messenger.MessagesStorage r1 = r72.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r1 = r1.getUserSync(r2)
            if (r1 == 0) goto L_0x0782
            org.telegram.messenger.MessagesController r2 = r72.getMessagesController()
            r3 = 1
            r2.putUser(r1, r3)
        L_0x0782:
            if (r1 == 0) goto L_0x079c
            org.telegram.tgnet.TLRPC$UserProfilePhoto r1 = r1.photo
            if (r1 == 0) goto L_0x079c
            org.telegram.tgnet.TLRPC$FileLocation r1 = r1.photo_small
            if (r1 == 0) goto L_0x079c
            long r2 = r1.volume_id
            int r9 = (r2 > r31 ? 1 : (r2 == r31 ? 0 : -1))
            if (r9 == 0) goto L_0x079c
            int r2 = r1.local_id
            if (r2 == 0) goto L_0x079c
            r2 = 1
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToAttach(r1, r2)
            goto L_0x07a4
        L_0x079c:
            r1 = 0
            goto L_0x07a4
        L_0x079e:
            r46 = r1
            r63 = r9
            r1 = r34
        L_0x07a4:
            r15.loadRoundAvatar(r1, r0)
            goto L_0x07ac
        L_0x07a8:
            r46 = r1
            r63 = r9
        L_0x07ac:
            androidx.core.app.Person r0 = r0.build()
            r13.put(r11, r0)
            goto L_0x0734
        L_0x07b4:
            boolean r0 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r0 != 0) goto L_0x08f6
            r2 = 0
            boolean r0 = r5[r2]
            if (r0 == 0) goto L_0x08a1
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 28
            if (r0 < r2) goto L_0x08a1
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r3 = "activity"
            java.lang.Object r0 = r0.getSystemService(r3)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x08a1
            if (r19 != 0) goto L_0x08a1
            boolean r0 = r4.isSecretMedia()
            if (r0 != 0) goto L_0x08a1
            int r0 = r4.type
            r3 = 1
            if (r0 == r3) goto L_0x07e8
            boolean r0 = r4.isSticker()
            if (r0 == 0) goto L_0x08a1
        L_0x07e8:
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r3 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r9 = r4.messageOwner
            int r9 = r9.date
            long r11 = (long) r9
            long r11 = r11 * r55
            r3.<init>(r8, r11, r1)
            boolean r9 = r4.isSticker()
            if (r9 == 0) goto L_0x0803
            java.lang.String r9 = "image/webp"
            goto L_0x0805
        L_0x0803:
            java.lang.String r9 = "image/jpeg"
        L_0x0805:
            boolean r11 = r0.exists()
            if (r11 == 0) goto L_0x081b
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0816 }
            java.lang.String r12 = "org.telegram.messenger.web.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r11, r12, r0)     // Catch:{ Exception -> 0x0816 }
            r2 = r59
            goto L_0x0871
        L_0x0816:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x086e
        L_0x081b:
            org.telegram.messenger.FileLoader r11 = r72.getFileLoader()
            java.lang.String r12 = r0.getName()
            boolean r11 = r11.isLoadingFile(r12)
            if (r11 == 0) goto L_0x086e
            android.net.Uri$Builder r11 = new android.net.Uri$Builder
            r11.<init>()
            java.lang.String r12 = "content"
            android.net.Uri$Builder r11 = r11.scheme(r12)
            java.lang.String r12 = "org.telegram.messenger.web.notification_image_provider"
            android.net.Uri$Builder r11 = r11.authority(r12)
            java.lang.String r12 = "msg_media_raw"
            android.net.Uri$Builder r11 = r11.appendPath(r12)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            int r2 = r15.currentAccount
            r12.append(r2)
            r2 = r59
            r12.append(r2)
            java.lang.String r12 = r12.toString()
            android.net.Uri$Builder r11 = r11.appendPath(r12)
            java.lang.String r12 = r0.getName()
            android.net.Uri$Builder r11 = r11.appendPath(r12)
            java.lang.String r0 = r0.getAbsolutePath()
            java.lang.String r12 = "final_path"
            android.net.Uri$Builder r0 = r11.appendQueryParameter(r12, r0)
            android.net.Uri r0 = r0.build()
            goto L_0x0871
        L_0x086e:
            r2 = r59
            r0 = 0
        L_0x0871:
            if (r0 == 0) goto L_0x08a3
            r3.setData(r9, r0)
            r10.addMessage(r3)
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r9 = "com.android.systemui"
            r11 = 1
            r3.grantUriPermission(r9, r0, r11)
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda4 r3 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda4
            r3.<init>(r0)
            r11 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3, r11)
            java.lang.CharSequence r0 = r4.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x089f
            java.lang.CharSequence r0 = r4.caption
            org.telegram.tgnet.TLRPC$Message r3 = r4.messageOwner
            int r3 = r3.date
            long r11 = (long) r3
            long r11 = r11 * r55
            r10.addMessage(r0, r11, r1)
        L_0x089f:
            r0 = 1
            goto L_0x08a4
        L_0x08a1:
            r2 = r59
        L_0x08a3:
            r0 = 0
        L_0x08a4:
            if (r0 != 0) goto L_0x08b0
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r11 = (long) r0
            long r11 = r11 * r55
            r10.addMessage(r8, r11, r1)
        L_0x08b0:
            r1 = 0
            boolean r0 = r5[r1]
            if (r0 == 0) goto L_0x0902
            if (r19 != 0) goto L_0x0902
            boolean r0 = r4.isVoice()
            if (r0 == 0) goto L_0x0902
            java.util.List r0 = r10.getMessages()
            boolean r1 = r0.isEmpty()
            if (r1 != 0) goto L_0x0902
            org.telegram.tgnet.TLRPC$Message r1 = r4.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            int r3 = android.os.Build.VERSION.SDK_INT
            r8 = 24
            if (r3 < r8) goto L_0x08de
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x08dc }
            java.lang.String r8 = "org.telegram.messenger.web.provider"
            android.net.Uri r1 = androidx.core.content.FileProvider.getUriForFile(r3, r8, r1)     // Catch:{ Exception -> 0x08dc }
            goto L_0x08e2
        L_0x08dc:
            r1 = 0
            goto L_0x08e2
        L_0x08de:
            android.net.Uri r1 = android.net.Uri.fromFile(r1)
        L_0x08e2:
            if (r1 == 0) goto L_0x0902
            int r3 = r0.size()
            r8 = 1
            int r3 = r3 - r8
            java.lang.Object r0 = r0.get(r3)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r0
            java.lang.String r3 = "audio/ogg"
            r0.setData(r3, r1)
            goto L_0x0902
        L_0x08f6:
            r2 = r59
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            int r0 = r0.date
            long r11 = (long) r0
            long r11 = r11 * r55
            r10.addMessage(r8, r11, r1)
        L_0x0902:
            int r0 = (r6 > r29 ? 1 : (r6 == r29 ? 0 : -1))
            if (r0 != 0) goto L_0x0916
            org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x0916
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r1 = r4.getId()
            r54 = r0
            r53 = r1
        L_0x0916:
            int r12 = r57 + -1
            r9 = r2
            r45 = r42
            r1 = r46
            r4 = r52
            r8 = r58
            r11 = r60
            r2 = r61
            r46 = r63
            goto L_0x05b8
        L_0x0929:
            r61 = r2
            r52 = r4
            r58 = r8
            r60 = r11
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
            if (r1 == 0) goto L_0x0972
            int r1 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            java.lang.String r2 = "encId"
            r0.putExtra(r2, r1)
            goto L_0x0984
        L_0x0972:
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r1 == 0) goto L_0x097e
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r6)
            goto L_0x0984
        L_0x097e:
            long r1 = -r6
            java.lang.String r3 = "chatId"
            r0.putExtra(r3, r1)
        L_0x0984:
            int r1 = r15.currentAccount
            r2 = r52
            r0.putExtra(r2, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r3 = 1073741824(0x40000000, float:2.0)
            r4 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r1, r4, r0, r3)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r3 = r47
            if (r47 == 0) goto L_0x09a0
            r1.addAction(r3)
        L_0x09a0:
            android.content.Intent r4 = new android.content.Intent
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r8 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r4.<init>(r5, r8)
            r5 = 32
            r4.addFlags(r5)
            java.lang.String r5 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r4.setAction(r5)
            r5 = r51
            r4.putExtra(r5, r6)
            r5 = r49
            r8 = r50
            r4.putExtra(r8, r5)
            int r8 = r15.currentAccount
            r4.putExtra(r2, r8)
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r9 = r48.intValue()
            r11 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r4 = android.app.PendingIntent.getBroadcast(r8, r9, r4, r11)
            androidx.core.app.NotificationCompat$Action$Builder r8 = new androidx.core.app.NotificationCompat$Action$Builder
            r9 = 2131165716(0x7var_, float:1.7945657E38)
            r11 = 2131626389(0x7f0e0995, float:1.8880013E38)
            java.lang.String r12 = "MarkAsRead"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r8.<init>(r9, r11, r4)
            r4 = 2
            androidx.core.app.NotificationCompat$Action$Builder r4 = r8.setSemanticAction(r4)
            r8 = 0
            androidx.core.app.NotificationCompat$Action$Builder r4 = r4.setShowsUserInterface(r8)
            androidx.core.app.NotificationCompat$Action r4 = r4.build()
            boolean r8 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            java.lang.String r9 = "_"
            if (r8 != 0) goto L_0x0a2e
            boolean r8 = org.telegram.messenger.DialogObject.isUserDialog(r6)
            if (r8 == 0) goto L_0x0a15
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r11 = "tguser"
            r8.append(r11)
            r8.append(r6)
            r8.append(r9)
            r8.append(r5)
            java.lang.String r8 = r8.toString()
            goto L_0x0a51
        L_0x0a15:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r11 = "tgchat"
            r8.append(r11)
            long r11 = -r6
            r8.append(r11)
            r8.append(r9)
            r8.append(r5)
            java.lang.String r8 = r8.toString()
            goto L_0x0a51
        L_0x0a2e:
            long r11 = globalSecretChatId
            int r8 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r8 == 0) goto L_0x0a50
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r11 = "tgenc"
            r8.append(r11)
            int r11 = org.telegram.messenger.DialogObject.getEncryptedChatId(r6)
            r8.append(r11)
            r8.append(r9)
            r8.append(r5)
            java.lang.String r8 = r8.toString()
            goto L_0x0a51
        L_0x0a50:
            r8 = 0
        L_0x0a51:
            if (r8 == 0) goto L_0x0a75
            r1.setDismissalId(r8)
            androidx.core.app.NotificationCompat$WearableExtender r5 = new androidx.core.app.NotificationCompat$WearableExtender
            r5.<init>()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "summary_"
            r9.append(r11)
            r9.append(r8)
            java.lang.String r8 = r9.toString()
            r5.setDismissalId(r8)
            r14 = r73
            r14.extend(r5)
            goto L_0x0a77
        L_0x0a75:
            r14 = r73
        L_0x0a77:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r8 = "tgaccount"
            r5.append(r8)
            r8 = r61
            r5.append(r8)
            java.lang.String r5 = r5.toString()
            r1.setBridgeTag(r5)
            r5 = r38
            r11 = 0
            java.lang.Object r12 = r5.get(r11)
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            org.telegram.tgnet.TLRPC$Message r11 = r12.messageOwner
            int r11 = r11.date
            long r11 = (long) r11
            long r11 = r11 * r55
            androidx.core.app.NotificationCompat$Builder r8 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext
            r8.<init>(r9)
            r9 = r58
            androidx.core.app.NotificationCompat$Builder r8 = r8.setContentTitle(r9)
            r27 = r13
            r13 = 2131165961(0x7var_, float:1.7946154E38)
            androidx.core.app.NotificationCompat$Builder r8 = r8.setSmallIcon(r13)
            java.lang.String r13 = r60.toString()
            androidx.core.app.NotificationCompat$Builder r8 = r8.setContentText(r13)
            r13 = 1
            androidx.core.app.NotificationCompat$Builder r8 = r8.setAutoCancel(r13)
            int r5 = r5.size()
            androidx.core.app.NotificationCompat$Builder r5 = r8.setNumber(r5)
            r8 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            androidx.core.app.NotificationCompat$Builder r5 = r5.setColor(r8)
            r8 = 0
            androidx.core.app.NotificationCompat$Builder r5 = r5.setGroupSummary(r8)
            androidx.core.app.NotificationCompat$Builder r5 = r5.setWhen(r11)
            androidx.core.app.NotificationCompat$Builder r5 = r5.setShowWhen(r13)
            androidx.core.app.NotificationCompat$Builder r5 = r5.setStyle(r10)
            androidx.core.app.NotificationCompat$Builder r0 = r5.setContentIntent(r0)
            androidx.core.app.NotificationCompat$Builder r0 = r0.extend(r1)
            r29 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r29 = r29 - r11
            java.lang.String r1 = java.lang.String.valueOf(r29)
            androidx.core.app.NotificationCompat$Builder r0 = r0.setSortKey(r1)
            java.lang.String r1 = "msg"
            androidx.core.app.NotificationCompat$Builder r0 = r0.setCategory(r1)
            android.content.Intent r1 = new android.content.Intent
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r8 = org.telegram.messenger.NotificationDismissReceiver.class
            r1.<init>(r5, r8)
            java.lang.String r5 = "messageDate"
            r8 = r37
            r1.putExtra(r5, r8)
            java.lang.String r5 = "dialogId"
            r1.putExtra(r5, r6)
            int r5 = r15.currentAccount
            r1.putExtra(r2, r5)
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r8 = r48.intValue()
            r10 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r5, r8, r1, r10)
            r0.setDeleteIntent(r1)
            if (r36 == 0) goto L_0x0b31
            java.lang.String r1 = r15.notificationGroup
            r0.setGroup(r1)
            r1 = 1
            r0.setGroupAlertBehavior(r1)
        L_0x0b31:
            if (r3 == 0) goto L_0x0b36
            r0.addAction(r3)
        L_0x0b36:
            if (r19 != 0) goto L_0x0b3b
            r0.addAction(r4)
        L_0x0b3b:
            int r1 = r25.size()
            r4 = 1
            if (r1 != r4) goto L_0x0b4e
            boolean r1 = android.text.TextUtils.isEmpty(r74)
            if (r1 != 0) goto L_0x0b4e
            r13 = r74
            r0.setSubText(r13)
            goto L_0x0b50
        L_0x0b4e:
            r13 = r74
        L_0x0b50:
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r6)
            if (r1 == 0) goto L_0x0b59
            r0.setLocalOnly(r4)
        L_0x0b59:
            if (r44 == 0) goto L_0x0b60
            r1 = r44
            r0.setLargeIcon(r1)
        L_0x0b60:
            r1 = 0
            boolean r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1)
            if (r3 != 0) goto L_0x0bfa
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 != 0) goto L_0x0bfa
            r1 = r54
            if (r1 == 0) goto L_0x0bfa
            int r3 = r1.size()
            r10 = 0
        L_0x0b74:
            if (r10 >= r3) goto L_0x0bfa
            java.lang.Object r5 = r1.get(r10)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r5 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r8 = r5.buttons
            int r8 = r8.size()
            r11 = 0
        L_0x0b83:
            if (r11 >= r8) goto L_0x0be8
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r12 = r5.buttons
            java.lang.Object r12 = r12.get(r11)
            org.telegram.tgnet.TLRPC$KeyboardButton r12 = (org.telegram.tgnet.TLRPC$KeyboardButton) r12
            boolean r4 = r12 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r4 == 0) goto L_0x0bd1
            android.content.Intent r4 = new android.content.Intent
            r29 = r1
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r30 = r3
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r3 = org.telegram.messenger.NotificationCallbackReceiver.class
            r4.<init>(r1, r3)
            int r1 = r15.currentAccount
            r4.putExtra(r2, r1)
            java.lang.String r1 = "did"
            r4.putExtra(r1, r6)
            byte[] r1 = r12.data
            if (r1 == 0) goto L_0x0bb1
            java.lang.String r3 = "data"
            r4.putExtra(r3, r1)
        L_0x0bb1:
            java.lang.String r1 = "mid"
            r3 = r53
            r4.putExtra(r1, r3)
            java.lang.String r1 = r12.text
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext
            r52 = r2
            int r2 = r15.lastButtonId
            r31 = r3
            int r3 = r2 + 1
            r15.lastButtonId = r3
            r3 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r12, r2, r4, r3)
            r12 = 0
            r0.addAction(r12, r1, r2)
            goto L_0x0bdc
        L_0x0bd1:
            r29 = r1
            r52 = r2
            r30 = r3
            r31 = r53
            r3 = 134217728(0x8000000, float:3.85186E-34)
            r12 = 0
        L_0x0bdc:
            int r11 = r11 + 1
            r1 = r29
            r3 = r30
            r53 = r31
            r2 = r52
            r4 = 1
            goto L_0x0b83
        L_0x0be8:
            r29 = r1
            r52 = r2
            r30 = r3
            r31 = r53
            r3 = 134217728(0x8000000, float:3.85186E-34)
            r12 = 0
            int r10 = r10 + 1
            r3 = r30
            r4 = 1
            goto L_0x0b74
        L_0x0bfa:
            r12 = 0
            if (r35 != 0) goto L_0x0CLASSNAME
            if (r43 == 0) goto L_0x0CLASSNAME
            r8 = r43
            java.lang.String r1 = r8.phone
            if (r1 == 0) goto L_0x0CLASSNAME
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "tel:+"
            r1.append(r2)
            java.lang.String r2 = r8.phone
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.addPerson(r1)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r8 = r43
        L_0x0CLASSNAME:
            int r1 = android.os.Build.VERSION.SDK_INT
            r11 = 26
            r4 = r36
            r10 = r41
            if (r1 < r11) goto L_0x0CLASSNAME
            r15.setNotificationChannel(r10, r0, r4)
        L_0x0CLASSNAME:
            org.telegram.messenger.NotificationsController$1NotificationHolder r5 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            r22 = 7
            r1 = r5
            int r3 = r48.intValue()
            r29 = r61
            r2 = 27
            r16 = 27
            r2 = r72
            r23 = r4
            r64 = r5
            r33 = r35
            r31 = 1
            r4 = r6
            r65 = r6
            r32 = 27
            r6 = r9
            r9 = r20
            r7 = r8
            r20 = r28
            r8 = r33
            r12 = r9
            r28 = 0
            r9 = r0
            r16 = r10
            r10 = r77
            r67 = r48
            r17 = 26
            r11 = r78
            r69 = r12
            r68 = r16
            r12 = r79
            r70 = r27
            r27 = 26
            r13 = r80
            r14 = r81
            r15 = r82
            r16 = r83
            r17 = r84
            r18 = r85
            r1.<init>(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18)
            r0 = r64
            r14 = r69
            r14.add(r0)
            r15 = r72
            androidx.collection.LongSparseArray<java.lang.Integer> r0 = r15.wearNotificationsIds
            r1 = r65
            r3 = r67
            r0.put(r1, r3)
        L_0x0CLASSNAME:
            int r4 = r24 + 1
            r7 = r14
            r8 = r20
            r6 = r21
            r5 = r23
            r11 = r25
            r10 = r26
            r2 = r29
            r12 = r68
            r13 = r70
            r1 = 7
            r9 = 0
            r14 = r73
            goto L_0x0108
        L_0x0ca9:
            r23 = r5
            r14 = r7
            r20 = r8
            r68 = r12
            r70 = r13
            r28 = 0
            if (r23 == 0) goto L_0x0cfa
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0cd0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r15.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0cd0:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager     // Catch:{ SecurityException -> 0x0cda }
            int r1 = r15.notificationId     // Catch:{ SecurityException -> 0x0cda }
            r2 = r68
            r0.notify(r1, r2)     // Catch:{ SecurityException -> 0x0cda }
            goto L_0x0d09
        L_0x0cda:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = r72
            r2 = r73
            r3 = r75
            r5 = r77
            r6 = r78
            r7 = r79
            r8 = r80
            r9 = r81
            r10 = r82
            r11 = r83
            r12 = r84
            r13 = r85
            r1.resetNotificationSound(r2, r3, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x0d09
        L_0x0cfa:
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0d09
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.cancel(r1)
        L_0x0d09:
            r9 = 0
        L_0x0d0a:
            int r0 = r20.size()
            if (r9 >= r0) goto L_0x0d4f
            r1 = r20
            long r2 = r1.keyAt(r9)
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x0d23
            goto L_0x0d4a
        L_0x0d23:
            java.lang.Object r0 = r1.valueAt(r9)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0d41
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "cancel notification id "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0d41:
            androidx.core.app.NotificationManagerCompat r2 = notificationManager
            int r0 = r0.intValue()
            r2.cancel(r0)
        L_0x0d4a:
            int r9 = r9 + 1
            r20 = r1
            goto L_0x0d0a
        L_0x0d4f:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r14.size()
            r0.<init>(r1)
            int r1 = r14.size()
            r9 = 0
        L_0x0d5d:
            if (r9 >= r1) goto L_0x0dbb
            java.lang.Object r2 = r14.get(r9)
            org.telegram.messenger.NotificationsController$1NotificationHolder r2 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r2
            r0.clear()
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 29
            if (r3 < r4) goto L_0x0da0
            long r3 = r2.dialogId
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r3)
            if (r3 != 0) goto L_0x0da0
            androidx.core.app.NotificationCompat$Builder r3 = r2.notification
            long r4 = r2.dialogId
            java.lang.String r6 = r2.name
            org.telegram.tgnet.TLRPC$User r7 = r2.user
            org.telegram.tgnet.TLRPC$Chat r8 = r2.chat
            r10 = r70
            java.lang.Object r11 = r10.get(r4)
            androidx.core.app.Person r11 = (androidx.core.app.Person) r11
            r73 = r72
            r74 = r3
            r75 = r4
            r77 = r6
            r78 = r7
            r79 = r8
            r80 = r11
            java.lang.String r3 = r73.createNotificationShortcut(r74, r75, r77, r78, r79, r80)
            if (r3 == 0) goto L_0x0da2
            r0.add(r3)
            goto L_0x0da2
        L_0x0da0:
            r10 = r70
        L_0x0da2:
            r2.call()
            boolean r2 = r72.unsupportedNotificationShortcut()
            if (r2 != 0) goto L_0x0db6
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L_0x0db6
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r2, r0)
        L_0x0db6:
            int r9 = r9 + 1
            r70 = r10
            goto L_0x0d5d
        L_0x0dbb:
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
