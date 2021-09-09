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

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        this.channelGroupsCreated = false;
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda11(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cleanup$1() {
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
    public /* synthetic */ void lambda$setOpenedDialogId$2(long j) {
        this.opened_dialog_id = j;
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
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda6(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$forceShowPopupForReply$6() {
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
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda31(this, arrayList));
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

    public void removeDeletedMessagesFromNotifications(SparseArray<ArrayList<Integer>> sparseArray) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda26(this, sparseArray, new ArrayList(0)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$9(SparseArray sparseArray, ArrayList arrayList) {
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
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda29(this, arrayList2));
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

    public void removeDeletedHisoryFromNotifications(SparseIntArray sparseIntArray) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda27(this, sparseIntArray, new ArrayList(0)));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$12(SparseIntArray sparseIntArray, ArrayList arrayList) {
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
            AndroidUtilities.runOnUIThread(new NotificationsController$$ExternalSyntheticLambda30(this, arrayList2));
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

    public void processReadMessages(SparseLongArray sparseLongArray, long j, int i, int i2, boolean z) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda36(this, sparseLongArray, new ArrayList(0), j, i2, i, z));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00bb, code lost:
        r6 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processReadMessages$14(org.telegram.messenger.support.SparseLongArray r19, java.util.ArrayList r20, long r21, int r23, int r24, boolean r25) {
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
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda32 r1 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda32
            r1.<init>(r0, r2)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x0105:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processReadMessages$14(org.telegram.messenger.support.SparseLongArray, java.util.ArrayList, long, int, int, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processReadMessages$13(ArrayList arrayList) {
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
            notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda35(this, arrayList, new ArrayList(0), z2, z, countDownLatch));
        } else if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0049, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L_0x0051;
     */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0111  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processNewMessages$18(java.util.ArrayList r32, java.util.ArrayList r33, boolean r34, boolean r35, java.util.concurrent.CountDownLatch r36) {
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
            if (r15 >= r1) goto L_0x0208
            java.lang.Object r1 = r9.get(r15)
            r7 = r1
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            if (r1 == 0) goto L_0x0051
            boolean r1 = r7.isImportedForward()
            if (r1 != 0) goto L_0x004b
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r1.action
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL
            if (r5 != 0) goto L_0x004b
            boolean r1 = r1.silent
            if (r1 == 0) goto L_0x0051
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r1 != 0) goto L_0x004b
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r1 == 0) goto L_0x0051
        L_0x004b:
            r22 = r13
            r21 = r15
            goto L_0x013d
        L_0x0051:
            int r1 = r7.getId()
            long r4 = (long) r1
            boolean r1 = r7.isFcmMessage()
            r19 = 0
            if (r1 == 0) goto L_0x0067
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            r21 = r15
            long r14 = r1.random_id
            r22 = r13
            goto L_0x006d
        L_0x0067:
            r21 = r15
            r22 = r13
            r14 = r19
        L_0x006d:
            long r12 = r7.getDialogId()
            int r6 = (int) r12
            boolean r1 = r7.isFcmMessage()
            if (r1 == 0) goto L_0x007d
            boolean r1 = r7.localChannel
        L_0x007a:
            r24 = r1
            goto L_0x009c
        L_0x007d:
            if (r6 >= 0) goto L_0x009a
            org.telegram.messenger.MessagesController r1 = r31.getMessagesController()
            int r2 = -r6
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r2 == 0) goto L_0x0098
            boolean r1 = r1.megagroup
            if (r1 != 0) goto L_0x0098
            r1 = 1
            goto L_0x007a
        L_0x0098:
            r1 = 0
            goto L_0x007a
        L_0x009a:
            r24 = 0
        L_0x009c:
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x00aa
            long r1 = (long) r1
            r25 = 32
            long r1 = r1 << r25
            long r4 = r4 | r1
        L_0x00aa:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.pushMessagesDict
            java.lang.Object r1 = r1.get(r4)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 != 0) goto L_0x00d2
            org.telegram.tgnet.TLRPC$Message r2 = r7.messageOwner
            r26 = r4
            long r3 = r2.random_id
            int r2 = (r3 > r19 ? 1 : (r3 == r19 ? 0 : -1))
            if (r2 == 0) goto L_0x00d4
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.fcmRandomMessagesDict
            java.lang.Object r1 = r1.get(r3)
            org.telegram.messenger.MessageObject r1 = (org.telegram.messenger.MessageObject) r1
            if (r1 == 0) goto L_0x00d4
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r8.fcmRandomMessagesDict
            org.telegram.tgnet.TLRPC$Message r3 = r7.messageOwner
            long r3 = r3.random_id
            r2.remove(r3)
            goto L_0x00d4
        L_0x00d2:
            r26 = r4
        L_0x00d4:
            if (r1 == 0) goto L_0x0111
            boolean r2 = r1.isFcmMessage()
            if (r2 == 0) goto L_0x013d
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r2 = r8.pushMessagesDict
            r4 = r26
            r2.put(r4, r7)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r2 = r8.pushMessages
            int r1 = r2.indexOf(r1)
            if (r1 < 0) goto L_0x0100
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
            goto L_0x0101
        L_0x0100:
            r12 = r7
        L_0x0101:
            if (r34 == 0) goto L_0x013d
            boolean r1 = r12.localEdit
            if (r1 == 0) goto L_0x010e
            org.telegram.messenger.MessagesStorage r2 = r31.getMessagesStorage()
            r2.putPushMessage(r12)
        L_0x010e:
            r17 = r1
            goto L_0x013d
        L_0x0111:
            r4 = r26
            if (r17 == 0) goto L_0x0116
            goto L_0x013d
        L_0x0116:
            if (r34 == 0) goto L_0x011f
            org.telegram.messenger.MessagesStorage r1 = r31.getMessagesStorage()
            r1.putPushMessage(r7)
        L_0x011f:
            long r1 = r8.opened_dialog_id
            int r3 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r3 != 0) goto L_0x012f
            boolean r1 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r1 == 0) goto L_0x012f
            if (r34 != 0) goto L_0x013d
            r31.playInChatSound()
            goto L_0x013d
        L_0x012f:
            org.telegram.tgnet.TLRPC$Message r1 = r7.messageOwner
            boolean r2 = r1.mentioned
            if (r2 == 0) goto L_0x0148
            if (r22 != 0) goto L_0x0141
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r1 == 0) goto L_0x0141
        L_0x013d:
            r30 = r10
            goto L_0x01fd
        L_0x0141:
            int r1 = r7.getFromChatId()
            long r1 = (long) r1
            r2 = r1
            goto L_0x0149
        L_0x0148:
            r2 = r12
        L_0x0149:
            boolean r1 = r8.isPersonalMessage(r7)
            if (r1 == 0) goto L_0x0157
            int r1 = r8.personal_count
            r16 = 1
            int r1 = r1 + 1
            r8.personal_count = r1
        L_0x0157:
            int r1 = r10.indexOfKey(r2)
            if (r1 < 0) goto L_0x016a
            java.lang.Object r1 = r10.valueAt(r1)
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r26 = r4
            goto L_0x0189
        L_0x016a:
            int r1 = r8.getNotifyOverride(r11, r2)
            r26 = r4
            r4 = -1
            if (r1 != r4) goto L_0x017c
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r24)
            boolean r1 = r8.isGlobalNotificationsEnabled(r2, r1)
            goto L_0x0182
        L_0x017c:
            r4 = 2
            if (r1 == r4) goto L_0x0181
            r1 = 1
            goto L_0x0182
        L_0x0181:
            r1 = 0
        L_0x0182:
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r1)
            r10.put(r2, r4)
        L_0x0189:
            if (r1 == 0) goto L_0x01f9
            if (r34 != 0) goto L_0x01a6
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
            goto L_0x01af
        L_0x01a6:
            r28 = r2
            r30 = r10
            r23 = r12
            r9 = r26
            r12 = r7
        L_0x01af:
            if (r18 != 0) goto L_0x01b7
            org.telegram.tgnet.TLRPC$Message r1 = r12.messageOwner
            boolean r1 = r1.from_scheduled
            r18 = r1
        L_0x01b7:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r8.delayedPushMessages
            r1.add(r12)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r8.pushMessages
            r2 = 0
            r1.add(r2, r12)
            int r1 = (r9 > r19 ? 1 : (r9 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x01cc
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.pushMessagesDict
            r1.put(r9, r12)
            goto L_0x01d5
        L_0x01cc:
            int r1 = (r14 > r19 ? 1 : (r14 == r19 ? 0 : -1))
            if (r1 == 0) goto L_0x01d5
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r1 = r8.fcmRandomMessagesDict
            r1.put(r14, r12)
        L_0x01d5:
            int r1 = (r23 > r28 ? 1 : (r23 == r28 ? 0 : -1))
            if (r1 == 0) goto L_0x01fb
            android.util.LongSparseArray<java.lang.Integer> r1 = r8.pushDialogsOverrideMention
            r2 = r23
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            android.util.LongSparseArray<java.lang.Integer> r4 = r8.pushDialogsOverrideMention
            if (r1 != 0) goto L_0x01ea
            r16 = 1
            goto L_0x01f1
        L_0x01ea:
            int r1 = r1.intValue()
            r5 = 1
            int r16 = r1 + 1
        L_0x01f1:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r16)
            r4.put(r2, r1)
            goto L_0x01fb
        L_0x01f9:
            r30 = r10
        L_0x01fb:
            r16 = 1
        L_0x01fd:
            int r15 = r21 + 1
            r9 = r32
            r13 = r22
            r10 = r30
            r12 = 1
            goto L_0x0020
        L_0x0208:
            if (r16 == 0) goto L_0x020e
            r1 = r35
            r8.notifyCheck = r1
        L_0x020e:
            boolean r1 = r33.isEmpty()
            if (r1 != 0) goto L_0x0228
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()
            if (r1 != 0) goto L_0x0228
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 != 0) goto L_0x0228
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda33 r1 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda33
            r2 = r33
            r1.<init>(r8, r2, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x0228:
            if (r34 != 0) goto L_0x022c
            if (r18 == 0) goto L_0x02e7
        L_0x022c:
            if (r17 == 0) goto L_0x023a
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            goto L_0x02e7
        L_0x023a:
            if (r16 == 0) goto L_0x02e7
            r0 = r32
            r1 = 0
            java.lang.Object r0 = r0.get(r1)
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            long r1 = r0.getDialogId()
            boolean r3 = r0.isFcmMessage()
            if (r3 == 0) goto L_0x0256
            boolean r0 = r0.localChannel
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            goto L_0x0257
        L_0x0256:
            r0 = 0
        L_0x0257:
            int r3 = r8.total_unread_count
            int r4 = r8.getNotifyOverride(r11, r1)
            r5 = -1
            if (r4 != r5) goto L_0x0265
            boolean r0 = r8.isGlobalNotificationsEnabled(r1, r0)
            goto L_0x026f
        L_0x0265:
            r0 = 2
            if (r4 == r0) goto L_0x026b
            r16 = 1
            goto L_0x026d
        L_0x026b:
            r16 = 0
        L_0x026d:
            r0 = r16
        L_0x026f:
            android.util.LongSparseArray<java.lang.Integer> r4 = r8.pushDialogs
            java.lang.Object r4 = r4.get(r1)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x0282
            int r5 = r4.intValue()
            r16 = 1
            int r5 = r5 + 1
            goto L_0x0285
        L_0x0282:
            r16 = 1
            r5 = 1
        L_0x0285:
            boolean r6 = r8.notifyCheck
            if (r6 == 0) goto L_0x02a1
            if (r0 != 0) goto L_0x02a1
            android.util.LongSparseArray<java.lang.Integer> r6 = r8.pushDialogsOverrideMention
            java.lang.Object r6 = r6.get(r1)
            java.lang.Integer r6 = (java.lang.Integer) r6
            if (r6 == 0) goto L_0x02a1
            int r7 = r6.intValue()
            if (r7 == 0) goto L_0x02a1
            int r5 = r6.intValue()
            r12 = 1
            goto L_0x02a2
        L_0x02a1:
            r12 = r0
        L_0x02a2:
            if (r12 == 0) goto L_0x02bd
            if (r4 == 0) goto L_0x02af
            int r0 = r8.total_unread_count
            int r4 = r4.intValue()
            int r0 = r0 - r4
            r8.total_unread_count = r0
        L_0x02af:
            int r0 = r8.total_unread_count
            int r0 = r0 + r5
            r8.total_unread_count = r0
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r0.put(r1, r4)
        L_0x02bd:
            int r0 = r8.total_unread_count
            if (r3 == r0) goto L_0x02d9
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r8.delayedPushMessages
            r0.clear()
            boolean r0 = r8.notifyCheck
            r8.showOrUpdateNotification(r0)
            android.util.LongSparseArray<java.lang.Integer> r0 = r8.pushDialogs
            int r0 = r0.size()
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda20 r1 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda20
            r1.<init>(r8, r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
        L_0x02d9:
            r0 = 0
            r8.notifyCheck = r0
            boolean r0 = r8.showBadgeNumber
            if (r0 == 0) goto L_0x02e7
            int r0 = r31.getTotalAllUnreadCount()
            r8.setBadge(r0)
        L_0x02e7:
            if (r36 == 0) goto L_0x02ec
            r36.countDown()
        L_0x02ec:
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

    public void processDialogsUpdateRead(LongSparseArray<Integer> longSparseArray) {
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda25(this, longSparseArray, new ArrayList()));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDialogsUpdateRead$21(LongSparseArray longSparseArray, ArrayList arrayList) {
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
        notificationsQueue.postRunnable(new NotificationsController$$ExternalSyntheticLambda34(this, arrayList, longSparseArray, arrayList2));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0055, code lost:
        if ((r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) == false) goto L_0x005b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$processLoadedUnreadMessages$23(java.util.ArrayList r21, android.util.LongSparseArray r22, java.util.ArrayList r23) {
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
            if (r1 == 0) goto L_0x0108
            r11 = 0
        L_0x002f:
            int r12 = r21.size()
            if (r11 >= r12) goto L_0x0108
            java.lang.Object r12 = r1.get(r11)
            org.telegram.tgnet.TLRPC$Message r12 = (org.telegram.tgnet.TLRPC$Message) r12
            if (r12 == 0) goto L_0x005b
            org.telegram.tgnet.TLRPC$MessageFwdHeader r13 = r12.fwd_from
            if (r13 == 0) goto L_0x0045
            boolean r13 = r13.imported
            if (r13 != 0) goto L_0x0057
        L_0x0045:
            org.telegram.tgnet.TLRPC$MessageAction r13 = r12.action
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL
            if (r14 != 0) goto L_0x0057
            boolean r14 = r12.silent
            if (r14 == 0) goto L_0x005b
            boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r14 != 0) goto L_0x0057
            boolean r13 = r13 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r13 == 0) goto L_0x005b
        L_0x0057:
            r15 = r5
            r12 = r11
            goto L_0x0100
        L_0x005b:
            int r13 = r12.id
            long r13 = (long) r13
            org.telegram.tgnet.TLRPC$Peer r15 = r12.peer_id
            int r15 = r15.channel_id
            if (r15 == 0) goto L_0x0067
            long r8 = (long) r15
            long r8 = r8 << r7
            long r13 = r13 | r8
        L_0x0067:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r8 = r0.pushMessagesDict
            int r8 = r8.indexOfKey(r13)
            if (r8 < 0) goto L_0x0070
            goto L_0x0057
        L_0x0070:
            org.telegram.messenger.MessageObject r8 = new org.telegram.messenger.MessageObject
            int r9 = r0.currentAccount
            r8.<init>(r9, r12, r4, r4)
            boolean r9 = r0.isPersonalMessage(r8)
            if (r9 == 0) goto L_0x0082
            int r9 = r0.personal_count
            int r9 = r9 + r10
            r0.personal_count = r9
        L_0x0082:
            r12 = r11
            long r10 = r8.getDialogId()
            org.telegram.tgnet.TLRPC$Message r15 = r8.messageOwner
            boolean r15 = r15.mentioned
            if (r15 == 0) goto L_0x0095
            int r15 = r8.getFromChatId()
            r17 = r10
            long r9 = (long) r15
            goto L_0x0099
        L_0x0095:
            r17 = r10
            r9 = r17
        L_0x0099:
            int r11 = r6.indexOfKey(r9)
            if (r11 < 0) goto L_0x00aa
            java.lang.Object r11 = r6.valueAt(r11)
            java.lang.Boolean r11 = (java.lang.Boolean) r11
            boolean r11 = r11.booleanValue()
            goto L_0x00c3
        L_0x00aa:
            int r11 = r0.getNotifyOverride(r5, r9)
            r15 = -1
            if (r11 != r15) goto L_0x00b6
            boolean r11 = r0.isGlobalNotificationsEnabled((long) r9)
            goto L_0x00bc
        L_0x00b6:
            r15 = 2
            if (r11 == r15) goto L_0x00bb
            r11 = 1
            goto L_0x00bc
        L_0x00bb:
            r11 = 0
        L_0x00bc:
            java.lang.Boolean r15 = java.lang.Boolean.valueOf(r11)
            r6.put(r9, r15)
        L_0x00c3:
            r15 = r5
            if (r11 == 0) goto L_0x0100
            long r4 = r0.opened_dialog_id
            int r19 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r19 != 0) goto L_0x00d1
            boolean r4 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r4 == 0) goto L_0x00d1
            goto L_0x0100
        L_0x00d1:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r0.pushMessagesDict
            r4.put(r13, r8)
            java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.pushMessages
            r5 = 0
            r4.add(r5, r8)
            int r4 = (r17 > r9 ? 1 : (r17 == r9 ? 0 : -1))
            if (r4 == 0) goto L_0x0100
            android.util.LongSparseArray<java.lang.Integer> r4 = r0.pushDialogsOverrideMention
            r13 = r17
            java.lang.Object r4 = r4.get(r13)
            java.lang.Integer r4 = (java.lang.Integer) r4
            android.util.LongSparseArray<java.lang.Integer> r5 = r0.pushDialogsOverrideMention
            if (r4 != 0) goto L_0x00f1
            r16 = 1
            goto L_0x00f9
        L_0x00f1:
            int r4 = r4.intValue()
            r8 = 1
            int r4 = r4 + r8
            r16 = r4
        L_0x00f9:
            java.lang.Integer r4 = java.lang.Integer.valueOf(r16)
            r5.put(r13, r4)
        L_0x0100:
            int r4 = r12 + 1
            r11 = r4
            r5 = r15
            r4 = 0
            r10 = 1
            goto L_0x002f
        L_0x0108:
            r15 = r5
            r5 = 0
        L_0x010a:
            int r1 = r22.size()
            if (r5 >= r1) goto L_0x0160
            long r12 = r2.keyAt(r5)
            int r1 = r6.indexOfKey(r12)
            if (r1 < 0) goto L_0x0127
            java.lang.Object r1 = r6.valueAt(r1)
            java.lang.Boolean r1 = (java.lang.Boolean) r1
            boolean r1 = r1.booleanValue()
            r4 = r1
            r1 = r15
            goto L_0x0141
        L_0x0127:
            r1 = r15
            int r4 = r0.getNotifyOverride(r1, r12)
            r8 = -1
            if (r4 != r8) goto L_0x0134
            boolean r4 = r0.isGlobalNotificationsEnabled((long) r12)
            goto L_0x013a
        L_0x0134:
            r8 = 2
            if (r4 == r8) goto L_0x0139
            r4 = 1
            goto L_0x013a
        L_0x0139:
            r4 = 0
        L_0x013a:
            java.lang.Boolean r8 = java.lang.Boolean.valueOf(r4)
            r6.put(r12, r8)
        L_0x0141:
            if (r4 != 0) goto L_0x0144
            goto L_0x015c
        L_0x0144:
            java.lang.Object r4 = r2.valueAt(r5)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            android.util.LongSparseArray<java.lang.Integer> r8 = r0.pushDialogs
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            r8.put(r12, r10)
            int r8 = r0.total_unread_count
            int r8 = r8 + r4
            r0.total_unread_count = r8
        L_0x015c:
            int r5 = r5 + 1
            r15 = r1
            goto L_0x010a
        L_0x0160:
            r1 = r15
            if (r3 == 0) goto L_0x0257
            r5 = 0
        L_0x0164:
            int r2 = r23.size()
            if (r5 >= r2) goto L_0x0257
            java.lang.Object r2 = r3.get(r5)
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            int r4 = r2.getId()
            long r12 = (long) r4
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.peer_id
            int r4 = r4.channel_id
            if (r4 == 0) goto L_0x0180
            long r14 = (long) r4
            long r14 = r14 << r7
            long r12 = r12 | r14
        L_0x0180:
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r4 = r0.pushMessagesDict
            int r4 = r4.indexOfKey(r12)
            if (r4 < 0) goto L_0x018d
        L_0x0188:
            r4 = 0
            r16 = 1
            goto L_0x024f
        L_0x018d:
            boolean r4 = r0.isPersonalMessage(r2)
            if (r4 == 0) goto L_0x0199
            int r4 = r0.personal_count
            r8 = 1
            int r4 = r4 + r8
            r0.personal_count = r4
        L_0x0199:
            long r14 = r2.getDialogId()
            org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
            long r7 = r4.random_id
            boolean r4 = r4.mentioned
            if (r4 == 0) goto L_0x01ab
            int r4 = r2.getFromChatId()
            long r9 = (long) r4
            goto L_0x01ac
        L_0x01ab:
            r9 = r14
        L_0x01ac:
            int r4 = r6.indexOfKey(r9)
            if (r4 < 0) goto L_0x01bd
            java.lang.Object r4 = r6.valueAt(r4)
            java.lang.Boolean r4 = (java.lang.Boolean) r4
            boolean r4 = r4.booleanValue()
            goto L_0x01d6
        L_0x01bd:
            int r4 = r0.getNotifyOverride(r1, r9)
            r11 = -1
            if (r4 != r11) goto L_0x01c9
            boolean r4 = r0.isGlobalNotificationsEnabled((long) r9)
            goto L_0x01cf
        L_0x01c9:
            r11 = 2
            if (r4 == r11) goto L_0x01ce
            r4 = 1
            goto L_0x01cf
        L_0x01ce:
            r4 = 0
        L_0x01cf:
            java.lang.Boolean r11 = java.lang.Boolean.valueOf(r4)
            r6.put(r9, r11)
        L_0x01d6:
            if (r4 == 0) goto L_0x0188
            long r3 = r0.opened_dialog_id
            int r11 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r11 != 0) goto L_0x01e3
            boolean r3 = org.telegram.messenger.ApplicationLoader.isScreenOn
            if (r3 == 0) goto L_0x01e3
            goto L_0x0188
        L_0x01e3:
            r3 = 0
            int r11 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1))
            if (r11 == 0) goto L_0x01ef
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r0.pushMessagesDict
            r3.put(r12, r2)
            goto L_0x01f8
        L_0x01ef:
            int r11 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r11 == 0) goto L_0x01f8
            android.util.LongSparseArray<org.telegram.messenger.MessageObject> r3 = r0.fcmRandomMessagesDict
            r3.put(r7, r2)
        L_0x01f8:
            java.util.ArrayList<org.telegram.messenger.MessageObject> r3 = r0.pushMessages
            r4 = 0
            r3.add(r4, r2)
            int r2 = (r14 > r9 ? 1 : (r14 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0222
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogsOverrideMention
            java.lang.Object r2 = r2.get(r14)
            java.lang.Integer r2 = (java.lang.Integer) r2
            android.util.LongSparseArray<java.lang.Integer> r3 = r0.pushDialogsOverrideMention
            if (r2 != 0) goto L_0x0212
            r2 = 1
            r16 = 1
            goto L_0x021a
        L_0x0212:
            int r2 = r2.intValue()
            r16 = 1
            int r2 = r2 + 1
        L_0x021a:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r3.put(r14, r2)
            goto L_0x0224
        L_0x0222:
            r16 = 1
        L_0x0224:
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogs
            java.lang.Object r2 = r2.get(r9)
            java.lang.Integer r2 = (java.lang.Integer) r2
            if (r2 == 0) goto L_0x0235
            int r3 = r2.intValue()
            int r3 = r3 + 1
            goto L_0x0236
        L_0x0235:
            r3 = 1
        L_0x0236:
            if (r2 == 0) goto L_0x0241
            int r7 = r0.total_unread_count
            int r2 = r2.intValue()
            int r7 = r7 - r2
            r0.total_unread_count = r7
        L_0x0241:
            int r2 = r0.total_unread_count
            int r2 = r2 + r3
            r0.total_unread_count = r2
            android.util.LongSparseArray<java.lang.Integer> r2 = r0.pushDialogs
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2.put(r9, r3)
        L_0x024f:
            int r5 = r5 + 1
            r3 = r23
            r7 = 32
            goto L_0x0164
        L_0x0257:
            r4 = 0
            r16 = 1
            android.util.LongSparseArray<java.lang.Integer> r1 = r0.pushDialogs
            int r1 = r1.size()
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda16 r2 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda16
            r2.<init>(r0, r1)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            long r1 = android.os.SystemClock.elapsedRealtime()
            r5 = 1000(0x3e8, double:4.94E-321)
            long r1 = r1 / r5
            r5 = 60
            int r3 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x0276
            r4 = 1
        L_0x0276:
            r0.showOrUpdateNotification(r4)
            boolean r1 = r0.showBadgeNumber
            if (r1 == 0) goto L_0x0284
            int r1 = r20.getTotalAllUnreadCount()
            r0.setBadge(r1)
        L_0x0284:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.lambda$processLoadedUnreadMessages$23(java.util.ArrayList, android.util.LongSparseArray, java.util.ArrayList):void");
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
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x020d, code lost:
        if ((r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService) == false) goto L_0x0dfc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x020f, code lost:
        r20[0] = null;
        r3 = r2.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0215, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) == false) goto L_0x021e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x021d, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0220, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined) != false) goto L_0x0ded;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0224, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp) == false) goto L_0x0228;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x022a, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto) == false) goto L_0x023b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x023a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactNewPhoto", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x023e, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation) == false) goto L_0x029d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x0240, code lost:
        r1 = org.telegram.messenger.LocaleController.formatString("formatDateAtTime", NUM, org.telegram.messenger.LocaleController.getInstance().formatterYear.format(((long) r0.messageOwner.date) * 1000), org.telegram.messenger.LocaleController.getInstance().formatterDay.format(((long) r0.messageOwner.date) * 1000));
        r0 = r0.messageOwner.action;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x029c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationUnrecognizedDevice", NUM, getUserConfig().getCurrentUser().first_name, r1, r0.title, r0.address);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x029f, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore) != false) goto L_0x0de6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02a3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent) == false) goto L_0x02a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:164:0x02a9, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) == false) goto L_0x02c3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x02ad, code lost:
        if (r3.video == false) goto L_0x02b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x02b8, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageVideoIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x02c2, code lost:
        return org.telegram.messenger.LocaleController.getString("CallMessageIncomingMissed", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02c5, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser) == false) goto L_0x03cc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x02c7, code lost:
        r2 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02c9, code lost:
        if (r2 != 0) goto L_0x02e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x02d2, code lost:
        if (r3.users.size() != 1) goto L_0x02e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x02d4, code lost:
        r2 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x02e4, code lost:
        if (r2 == 0) goto L_0x0375;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:180:0x02ec, code lost:
        if (r0.messageOwner.peer_id.channel_id == 0) goto L_0x0306;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:182:0x02f0, code lost:
        if (r6.megagroup != false) goto L_0x0306;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:184:0x0305, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelAddedByNotification", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x0308, code lost:
        if (r2 != r10) goto L_0x031c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x031b, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroup", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:189:0x031c, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0328, code lost:
        if (r0 != null) goto L_0x032b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:191:0x032a, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x032d, code lost:
        if (r1 != r0.id) goto L_0x035b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x0331, code lost:
        if (r6.megagroup == false) goto L_0x0347;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:197:0x0346, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelfMega", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x035a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddSelf", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0374, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r11, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x0375, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0385, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x03b2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0387, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x039b, code lost:
        if (r3 == null) goto L_0x03af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x039d, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x03a5, code lost:
        if (r1.length() == 0) goto L_0x03ac;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x03a7, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x03ac, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x03af, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x03cb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupAddMember", NUM, r11, r6.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x03cf, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall) == false) goto L_0x03e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x03e3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupCreatedCall", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x03e6, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) == false) goto L_0x03ef;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x03ee, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x03f1, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall) == false) goto L_0x04a8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:224:0x03f3, code lost:
        r1 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x03f5, code lost:
        if (r1 != 0) goto L_0x0410;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x03fe, code lost:
        if (r3.users.size() != 1) goto L_0x0410;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x0400, code lost:
        r1 = r0.messageOwner.action.users.get(0).intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0410, code lost:
        if (r1 == 0) goto L_0x0451;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:230:0x0412, code lost:
        if (r1 != r10) goto L_0x0428;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:232:0x0427, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedYouToCall", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x0428, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r1));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x0434, code lost:
        if (r0 != null) goto L_0x0437;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x0436, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0450, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r11, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0451, code lost:
        r1 = new java.lang.StringBuilder();
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:240:0x0461, code lost:
        if (r2 >= r0.messageOwner.action.users.size()) goto L_0x048e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x0463, code lost:
        r3 = getMessagesController().getUser(r0.messageOwner.action.users.get(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x0477, code lost:
        if (r3 == null) goto L_0x048b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x0479, code lost:
        r3 = org.telegram.messenger.UserObject.getUserName(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x0481, code lost:
        if (r1.length() == 0) goto L_0x0488;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x0483, code lost:
        r1.append(", ");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x0488, code lost:
        r1.append(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:247:0x048b, code lost:
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:249:0x04a7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupInvitedToCall", NUM, r11, r6.title, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x04ab, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink) == false) goto L_0x04c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x04bf, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x04c3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle) == false) goto L_0x04d7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x04d6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupName", NUM, r11, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x04d9, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto) != false) goto L_0x0d86;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x04dd, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto) == false) goto L_0x04e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x04e3, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser) == false) goto L_0x0543;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x04e5, code lost:
        r2 = r3.user_id;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:265:0x04e7, code lost:
        if (r2 != r10) goto L_0x04fd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x04fc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickYou", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x04ff, code lost:
        if (r2 != r1) goto L_0x0513;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:271:0x0512, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupLeftMember", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x0513, code lost:
        r0 = getMessagesController().getUser(java.lang.Integer.valueOf(r0.messageOwner.action.user_id));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x0525, code lost:
        if (r0 != null) goto L_0x0528;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x0527, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0542, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationGroupKickMember", NUM, r11, r6.title, org.telegram.messenger.UserObject.getUserName(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x0545, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate) == false) goto L_0x054e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x054d, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0550, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate) == false) goto L_0x0559;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x0558, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x055b, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo) == false) goto L_0x056e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x056d, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x0571, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x0583;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0582, code lost:
        return org.telegram.messenger.LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, r3.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x0585, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken) == false) goto L_0x058e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x058d, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0590, code lost:
        if ((r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage) == false) goto L_0x0d85;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x0596, code lost:
        if (r6 == null) goto L_0x087d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x059c, code lost:
        if (org.telegram.messenger.ChatObject.isChannel(r6) == false) goto L_0x05a2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x05a0, code lost:
        if (r6.megagroup == false) goto L_0x087d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:305:0x05a2, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x05a4, code lost:
        if (r0 != null) goto L_0x05ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x05b9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x05c0, code lost:
        if (r0.isMusic() == false) goto L_0x05d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x05d3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusic", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x05dd, code lost:
        if (r0.isVideo() == false) goto L_0x0628;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x05e3, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0614;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x05ed, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0614;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:320:0x0613, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "📹 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x0627, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideo", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x062c, code lost:
        if (r0.isGif() == false) goto L_0x0677;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x0632, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0663;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:328:0x063c, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0663;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:0x0662, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "🎬 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0676, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGif", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:334:0x067d, code lost:
        if (r0.isVoice() == false) goto L_0x0691;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x0690, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoice", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0695, code lost:
        if (r0.isRoundVideo() == false) goto L_0x06a9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x06a8, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRound", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x06ad, code lost:
        if (r0.isSticker() != false) goto L_0x084d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x06b3, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x06b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x06b7, code lost:
        r3 = r0.messageOwner;
        r4 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:346:0x06bd, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0706;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:348:0x06c3, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x06f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:350:0x06cb, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x06f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:352:0x06f1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "📎 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:0x0705, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFile", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:356:0x0708, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0839;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:358:0x070c, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0710;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:360:0x0712, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0728;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:362:0x0727, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:364:0x072b, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x074d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:365:0x072d, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:366:0x074c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContact2", NUM, r11, r6.title, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:368:0x074f, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x078b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:369:0x0751, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:370:0x0757, code lost:
        if (r0.quiz == false) goto L_0x0772;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:372:0x0771, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuiz2", NUM, r11, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:374:0x078a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPoll2", NUM, r11, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:376:0x078d, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x07d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:378:0x0793, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x07c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:380:0x079b, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x07c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:382:0x07c1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, "🖼 " + r0.messageOwner.message, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:384:0x07d5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhoto", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:386:0x07da, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x07ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:388:0x07ed, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGame", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:389:0x07ee, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:390:0x07f0, code lost:
        if (r3 == null) goto L_0x0825;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:392:0x07f6, code lost:
        if (r3.length() <= 0) goto L_0x0825;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:393:0x07f8, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:394:0x07fe, code lost:
        if (r0.length() <= 20) goto L_0x0813;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:395:0x0800, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:397:0x0824, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedText", NUM, r11, r0, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:399:0x0838, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoText", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:401:0x084c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeo", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:402:0x084d, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:403:0x0852, code lost:
        if (r0 == null) goto L_0x086a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:405:0x0869, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, r11, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:407:0x087c, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedSticker", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:409:0x087e, code lost:
        if (r6 == null) goto L_0x0b19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:410:0x0880, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:411:0x0882, code lost:
        if (r0 != null) goto L_0x0894;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:413:0x0893, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:415:0x0898, code lost:
        if (r0.isMusic() == false) goto L_0x08aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:417:0x08a9, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:419:0x08b3, code lost:
        if (r0.isVideo() == false) goto L_0x08f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:421:0x08b9, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x08e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:423:0x08c3, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x08e7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:425:0x08e6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:427:0x08f7, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:429:0x08fc, code lost:
        if (r0.isGif() == false) goto L_0x0941;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:431:0x0902, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0930;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:433:0x090c, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0930;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:435:0x092f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:437:0x0940, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:439:0x0946, code lost:
        if (r0.isVoice() == false) goto L_0x0958;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:441:0x0957, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:443:0x095c, code lost:
        if (r0.isRoundVideo() == false) goto L_0x096e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:445:0x096d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:447:0x0972, code lost:
        if (r0.isSticker() != false) goto L_0x0aee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:449:0x0978, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x097c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:450:0x097c, code lost:
        r3 = r0.messageOwner;
        r4 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0982, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x09c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0988, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x09b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x0990, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x09b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:457:0x09b3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:459:0x09c4, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:461:0x09c7, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0add;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:463:0x09cb, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x09cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:465:0x09d1, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x09e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:467:0x09e3, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:469:0x09e6, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0a06;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:470:0x09e8, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:471:0x0a05, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactChannel2", NUM, r6.title, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:473:0x0a08, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0a3e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:474:0x0a0a, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:475:0x0a10, code lost:
        if (r0.quiz == false) goto L_0x0a28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:477:0x0a27, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:479:0x0a3d, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollChannel2", NUM, r6.title, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:0x0a40, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0a83;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x0a46, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0a72;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:485:0x0a4e, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0a72;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x0a71, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:489:0x0a82, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:491:0x0a86, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0a98;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:493:0x0a97, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:494:0x0a98, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:495:0x0a9a, code lost:
        if (r3 == null) goto L_0x0acc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:497:0x0aa0, code lost:
        if (r3.length() <= 0) goto L_0x0acc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:498:0x0aa2, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:499:0x0aa8, code lost:
        if (r0.length() <= 20) goto L_0x0abd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:500:0x0aaa, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:502:0x0acb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:504:0x0adc, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:506:0x0aed, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:507:0x0aee, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:508:0x0af2, code lost:
        if (r0 == null) goto L_0x0b08;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:510:0x0b07, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, r6.title, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:512:0x0b18, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:513:0x0b19, code lost:
        r0 = r0.replyMessageObject;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:514:0x0b1b, code lost:
        if (r0 != null) goto L_0x0b2b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:516:0x0b2a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:518:0x0b2f, code lost:
        if (r0.isMusic() == false) goto L_0x0b3f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:520:0x0b3e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedMusicUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:522:0x0b48, code lost:
        if (r0.isVideo() == false) goto L_0x0b89;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:524:0x0b4e, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0b7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:526:0x0b58, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0b7a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:528:0x0b79, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "📹 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:530:0x0b88, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVideoUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:532:0x0b8d, code lost:
        if (r0.isGif() == false) goto L_0x0bce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:534:0x0b93, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0bbf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:536:0x0b9d, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0bbf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:538:0x0bbe, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "🎬 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:540:0x0bcd, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGifUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:542:0x0bd3, code lost:
        if (r0.isVoice() == false) goto L_0x0be3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:544:0x0be2, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedVoiceUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:546:0x0be7, code lost:
        if (r0.isRoundVideo() == false) goto L_0x0bf7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:548:0x0bf6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedRoundUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:550:0x0bfb, code lost:
        if (r0.isSticker() != false) goto L_0x0d5f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:0x0CLASSNAME, code lost:
        if (r0.isAnimatedSticker() == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:553:0x0CLASSNAME, code lost:
        r3 = r0.messageOwner;
        r4 = r3.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:554:0x0c0b, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0c4a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:556:0x0CLASSNAME, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0c3b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:558:0x0CLASSNAME, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0c3b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:560:0x0c3a, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "📎 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:562:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedFileUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:564:0x0c4c, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0d50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:566:0x0CLASSNAME, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:568:0x0CLASSNAME, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:570:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoLiveUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:572:0x0CLASSNAME, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0CLASSNAME;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:573:0x0c6b, code lost:
        r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:574:0x0CLASSNAME, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedContactUser", NUM, r11, org.telegram.messenger.ContactsController.formatName(r4.first_name, r4.last_name));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:576:0x0CLASSNAME, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0cbb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:577:0x0c8b, code lost:
        r0 = ((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4).poll;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:578:0x0CLASSNAME, code lost:
        if (r0.quiz == false) goto L_0x0ca7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:580:0x0ca6, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedQuizUser", NUM, r11, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:582:0x0cba, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPollUser", NUM, r11, r0.question);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:584:0x0cbd, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0cfc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:586:0x0cc3, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0ced;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:588:0x0ccb, code lost:
        if (android.text.TextUtils.isEmpty(r3.message) != false) goto L_0x0ced;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:590:0x0cec, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, "🖼 " + r0.messageOwner.message);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:592:0x0cfb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedPhotoUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:594:0x0cff, code lost:
        if ((r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame) == false) goto L_0x0d0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:596:0x0d0e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGameUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:597:0x0d0f, code lost:
        r3 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:598:0x0d11, code lost:
        if (r3 == null) goto L_0x0d41;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:600:0x0d17, code lost:
        if (r3.length() <= 0) goto L_0x0d41;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:601:0x0d19, code lost:
        r0 = r0.messageText;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:602:0x0d1f, code lost:
        if (r0.length() <= 20) goto L_0x0d34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:603:0x0d21, code lost:
        r0 = r0.subSequence(0, 20) + "...";
     */
    /* JADX WARNING: Code restructure failed: missing block: B:605:0x0d40, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedTextUser", NUM, r11, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:607:0x0d4f, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedNoTextUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:609:0x0d5e, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedGeoUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:610:0x0d5f, code lost:
        r0 = r0.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:611:0x0d64, code lost:
        if (r0 == null) goto L_0x0d77;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0d76, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerEmojiUser", NUM, r11, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0d84, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationActionPinnedStickerUser", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:616:0x0d85, code lost:
        return null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:618:0x0d8a, code lost:
        if (r2.peer_id.channel_id == 0) goto L_0x0db8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:620:0x0d8e, code lost:
        if (r6.megagroup != false) goto L_0x0db8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:622:0x0d94, code lost:
        if (r19.isVideoAvatar() == false) goto L_0x0da7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:624:0x0da6, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelVideoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:626:0x0db7, code lost:
        return org.telegram.messenger.LocaleController.formatString("ChannelPhotoEditNotification", NUM, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:628:0x0dbc, code lost:
        if (r19.isVideoAvatar() == false) goto L_0x0dd2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:630:0x0dd1, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupVideo", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:632:0x0de5, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationEditedGroupPhoto", NUM, r11, r6.title);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:634:0x0dec, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:636:0x0dfb, code lost:
        return org.telegram.messenger.LocaleController.formatString("NotificationContactJoined", NUM, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:638:0x0e00, code lost:
        if (r19.isMediaEmpty() == false) goto L_0x0e19;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:640:0x0e0a, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0e11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:642:0x0e10, code lost:
        return r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:644:0x0e18, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:645:0x0e19, code lost:
        r1 = r0.messageOwner;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:646:0x0e1f, code lost:
        if ((r1.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto) == false) goto L_0x0e5f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:648:0x0e25, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0e43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:650:0x0e2d, code lost:
        if (android.text.TextUtils.isEmpty(r1.message) != false) goto L_0x0e43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:652:0x0e42, code lost:
        return "🖼 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:654:0x0e49, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0e55;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:656:0x0e54, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:658:0x0e5e, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachPhoto", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:660:0x0e63, code lost:
        if (r19.isVideo() == false) goto L_0x0ea5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:662:0x0e69, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0e89;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:664:0x0e73, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0e89;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:666:0x0e88, code lost:
        return "📹 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:668:0x0e8f, code lost:
        if (r0.messageOwner.media.ttl_seconds == 0) goto L_0x0e9b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:670:0x0e9a, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDestructingVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:672:0x0ea4, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachVideo", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:674:0x0ea9, code lost:
        if (r19.isGame() == false) goto L_0x0eb5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:676:0x0eb4, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGame", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:678:0x0eb9, code lost:
        if (r19.isVoice() == false) goto L_0x0ec5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:680:0x0ec4, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachAudio", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:682:0x0ec9, code lost:
        if (r19.isRoundVideo() == false) goto L_0x0ed5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:684:0x0ed4, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachRound", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:686:0x0ed9, code lost:
        if (r19.isMusic() == false) goto L_0x0ee5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:688:0x0ee4, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachMusic", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:689:0x0ee5, code lost:
        r1 = r0.messageOwner.media;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:690:0x0eeb, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact) == false) goto L_0x0ef7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:692:0x0ef6, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachContact", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:694:0x0ef9, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll) == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:696:0x0var_, code lost:
        if (((org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1).poll.quiz == false) goto L_0x0f0d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:698:0x0f0c, code lost:
        return org.telegram.messenger.LocaleController.getString("QuizPoll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:700:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("Poll", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:702:0x0var_, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo) != false) goto L_0x0fe7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:704:0x0f1d, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue) == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:706:0x0var_, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive) == false) goto L_0x0f2f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:708:0x0f2e, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachLiveLocation", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:710:0x0var_, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument) == false) goto L_0x0fd0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:712:0x0var_, code lost:
        if (r19.isSticker() != false) goto L_0x0fa2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:714:0x0f3d, code lost:
        if (r19.isAnimatedSticker() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:716:0x0var_, code lost:
        if (r19.isGif() == false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:718:0x0f4a, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0f6a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:720:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0f6a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:0x0var_, code lost:
        return "🎬 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:724:0x0var_, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachGif", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:726:0x0var_, code lost:
        if (android.os.Build.VERSION.SDK_INT < 19) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:728:0x0var_, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageOwner.message) != false) goto L_0x0var_;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:730:0x0var_, code lost:
        return "📎 " + r0.messageOwner.message;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:732:0x0fa1, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachDocument", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:733:0x0fa2, code lost:
        r0 = r19.getStickerEmoji();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:734:0x0fa6, code lost:
        if (r0 == null) goto L_0x0fc6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:736:0x0fc5, code lost:
        return r0 + " " + org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:738:0x0fcf, code lost:
        return org.telegram.messenger.LocaleController.getString("AttachSticker", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:740:0x0fd6, code lost:
        if (android.text.TextUtils.isEmpty(r0.messageText) != false) goto L_0x0fdf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:742:0x0fde, code lost:
        return r0.messageText.toString();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:744:0x0fe6, code lost:
        return org.telegram.messenger.LocaleController.getString("Message", NUM);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:746:0x0ff0, code lost:
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
            if (r1 != 0) goto L_0x0ffd
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x0010
            goto L_0x0ffd
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
            r11 = 2131626212(0x7f0e08e4, float:1.8879654E38)
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
            r1 = 2131624763(0x7f0e033b, float:1.8876715E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            java.lang.String r0 = r0.localName
            r2[r7] = r0
            java.lang.String r0 = "ChannelMessageNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x00c9:
            r1 = 2131626529(0x7f0e0a21, float:1.8880297E38)
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
            r0 = 2131626506(0x7f0e0a0a, float:1.888025E38)
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
            if (r9 == 0) goto L_0x0ff1
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
            if (r5 == 0) goto L_0x0ff1
            if (r2 != 0) goto L_0x01f7
            java.lang.String r3 = "EnablePreviewGroup"
            boolean r3 = r8.getBoolean(r3, r4)
            if (r3 != 0) goto L_0x0201
        L_0x01f7:
            if (r2 == 0) goto L_0x0ff1
            java.lang.String r2 = "EnablePreviewChannel"
            boolean r2 = r8.getBoolean(r2, r4)
            if (r2 == 0) goto L_0x0ff1
        L_0x0201:
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r4 = "🎬 "
            java.lang.String r5 = "📎 "
            java.lang.String r8 = "📹 "
            java.lang.String r9 = "🖼 "
            if (r3 == 0) goto L_0x0dfc
            r20[r7] = r15
            org.telegram.tgnet.TLRPC$MessageAction r3 = r2.action
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r12 == 0) goto L_0x021e
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x021e:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r12 != 0) goto L_0x0ded
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r12 == 0) goto L_0x0228
            goto L_0x0ded
        L_0x0228:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r12 == 0) goto L_0x023b
            r0 = 2131626487(0x7f0e09f7, float:1.8880212E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x023b:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            r14 = 3
            if (r12 == 0) goto L_0x029d
            r1 = 2131628609(0x7f0e1241, float:1.8884516E38)
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
            r2 = 2131626556(0x7f0e0a3c, float:1.8880352E38)
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
        L_0x029d:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r12 != 0) goto L_0x0de6
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r12 == 0) goto L_0x02a7
            goto L_0x0de6
        L_0x02a7:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r12 == 0) goto L_0x02c3
            boolean r0 = r3.video
            if (r0 == 0) goto L_0x02b9
            r0 = 2131624638(0x7f0e02be, float:1.8876461E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02b9:
            r0 = 2131624632(0x7f0e02b8, float:1.887645E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x02c3:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r12 == 0) goto L_0x03cc
            int r2 = r3.user_id
            if (r2 != 0) goto L_0x02e4
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            r4 = 1
            if (r3 != r4) goto L_0x02e4
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            java.lang.Object r2 = r2.get(r7)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
        L_0x02e4:
            if (r2 == 0) goto L_0x0375
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x0306
            boolean r0 = r6.megagroup
            if (r0 != 0) goto L_0x0306
            r0 = 2131624712(0x7f0e0308, float:1.8876611E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0306:
            r3 = 2
            r4 = 1
            if (r2 != r10) goto L_0x031c
            r0 = 2131626508(0x7f0e0a0c, float:1.8880254E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x031c:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x032b
            return r15
        L_0x032b:
            int r2 = r0.id
            if (r1 != r2) goto L_0x035b
            boolean r0 = r6.megagroup
            if (r0 == 0) goto L_0x0347
            r0 = 2131626493(0x7f0e09fd, float:1.8880224E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0347:
            r1 = 2
            r3 = 1
            r0 = 2131626492(0x7f0e09fc, float:1.8880222E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x035b:
            r3 = 1
            r1 = 2131626491(0x7f0e09fb, float:1.888022E38)
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
        L_0x0375:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x037b:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x03b2
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x03af
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x03ac
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x03ac:
            r1.append(r3)
        L_0x03af:
            int r2 = r2 + 1
            goto L_0x037b
        L_0x03b2:
            r0 = 2131626491(0x7f0e09fb, float:1.888022E38)
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
        L_0x03cc:
            r12 = 2
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r13 == 0) goto L_0x03e4
            r0 = 2131626495(0x7f0e09ff, float:1.8880228E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupCreatedCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x03e4:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            if (r12 == 0) goto L_0x03ef
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x03ef:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r12 == 0) goto L_0x04a8
            int r1 = r3.user_id
            if (r1 != 0) goto L_0x0410
            java.util.ArrayList<java.lang.Integer> r2 = r3.users
            int r2 = r2.size()
            r3 = 1
            if (r2 != r3) goto L_0x0410
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            java.lang.Object r1 = r1.get(r7)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
        L_0x0410:
            if (r1 == 0) goto L_0x0451
            if (r1 != r10) goto L_0x0428
            r0 = 2131626500(0x7f0e0a04, float:1.8880238E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0428:
            org.telegram.messenger.MessagesController r0 = r18.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x0437
            return r15
        L_0x0437:
            r1 = 2131626499(0x7f0e0a03, float:1.8880236E38)
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
        L_0x0451:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0457:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x048e
            org.telegram.messenger.MessagesController r3 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x048b
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0488
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0488:
            r1.append(r3)
        L_0x048b:
            int r2 = r2 + 1
            goto L_0x0457
        L_0x048e:
            r0 = 2131626499(0x7f0e0a03, float:1.8880236E38)
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
        L_0x04a8:
            r12 = 2
            boolean r13 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r13 == 0) goto L_0x04c0
            r0 = 2131626509(0x7f0e0a0d, float:1.8880256E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r13 = 1
            r1[r13] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04c0:
            r13 = 1
            boolean r14 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r14 == 0) goto L_0x04d7
            r0 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            r1[r7] = r11
            java.lang.String r2 = r3.title
            r1[r13] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04d7:
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r12 != 0) goto L_0x0d86
            boolean r12 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r12 == 0) goto L_0x04e1
            goto L_0x0d86
        L_0x04e1:
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r2 == 0) goto L_0x0543
            int r2 = r3.user_id
            if (r2 != r10) goto L_0x04fd
            r0 = 2131626502(0x7f0e0a06, float:1.8880242E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r4 = 1
            r1[r4] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x04fd:
            r3 = 2
            r4 = 1
            if (r2 != r1) goto L_0x0513
            r0 = 2131626503(0x7f0e0a07, float:1.8880244E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0513:
            org.telegram.messenger.MessagesController r1 = r18.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x0528
            return r15
        L_0x0528:
            r1 = 2131626501(0x7f0e0a05, float:1.888024E38)
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
        L_0x0543:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r1 == 0) goto L_0x054e
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x054e:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r1 == 0) goto L_0x0559
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0559:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r1 == 0) goto L_0x056e
            r0 = 2131624139(0x7f0e00cb, float:1.887545E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x056e:
            r1 = 1
            boolean r2 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r2 == 0) goto L_0x0583
            r0 = 2131624139(0x7f0e00cb, float:1.887545E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r3.title
            r1[r7] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0583:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r1 == 0) goto L_0x058e
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x058e:
            boolean r1 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r1 == 0) goto L_0x0d85
            java.lang.String r1 = "..."
            r2 = 20
            if (r6 == 0) goto L_0x087d
            boolean r3 = org.telegram.messenger.ChatObject.isChannel(r6)
            if (r3 == 0) goto L_0x05a2
            boolean r3 = r6.megagroup
            if (r3 == 0) goto L_0x087d
        L_0x05a2:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x05ba
            r0 = 2131626456(0x7f0e09d8, float:1.8880149E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r10 = 1
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05ba:
            r3 = 2
            r10 = 1
            boolean r12 = r0.isMusic()
            if (r12 == 0) goto L_0x05d4
            r0 = 2131626453(0x7f0e09d5, float:1.8880143E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r10] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x05d4:
            boolean r3 = r0.isVideo()
            r10 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r12 = "NotificationActionPinnedText"
            if (r3 == 0) goto L_0x0628
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0614
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0614
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
        L_0x0614:
            r2 = 1
            r3 = 2
            r0 = 2131626480(0x7f0e09f0, float:1.8880197E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r3 = r6.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0628:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x0677
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0663
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0663
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
        L_0x0663:
            r3 = 1
            r4 = 2
            r0 = 2131626447(0x7f0e09cf, float:1.888013E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0677:
            r3 = 1
            r4 = 2
            boolean r8 = r0.isVoice()
            if (r8 == 0) goto L_0x0691
            r0 = 2131626483(0x7f0e09f3, float:1.8880203E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0691:
            boolean r8 = r0.isRoundVideo()
            if (r8 == 0) goto L_0x06a9
            r0 = 2131626468(0x7f0e09e4, float:1.8880173E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x06a9:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x084d
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x06b7
            goto L_0x084d
        L_0x06b7:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r3.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0706
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x06f2
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x06f2
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
        L_0x06f2:
            r2 = 1
            r3 = 2
            r0 = 2131626432(0x7f0e09c0, float:1.88801E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r3 = r6.title
            r1[r2] = r3
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0706:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x0839
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x0710
            goto L_0x0839
        L_0x0710:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0728
            r0 = 2131626443(0x7f0e09cb, float:1.8880122E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r5 = 1
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0728:
            r5 = 1
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x074d
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131626429(0x7f0e09bd, float:1.8880094E38)
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
        L_0x074d:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x078b
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0772
            r1 = 2131626465(0x7f0e09e1, float:1.8880167E38)
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
        L_0x0772:
            r2 = 3
            r3 = 2
            r4 = 1
            r1 = 2131626462(0x7f0e09de, float:1.888016E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r5 = r6.title
            r2[r4] = r5
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x078b:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x07d6
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x07c2
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x07c2
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
        L_0x07c2:
            r3 = 1
            r5 = 2
            r0 = 2131626459(0x7f0e09db, float:1.8880155E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07d6:
            r3 = 1
            r5 = 2
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x07ee
            r0 = 2131626435(0x7f0e09c3, float:1.8880106E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x07ee:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x0825
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0825
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r2) goto L_0x0813
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
        L_0x0813:
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
        L_0x0825:
            r2 = 2
            r3 = 1
            r0 = 2131626456(0x7f0e09d8, float:1.8880149E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0839:
            r2 = 2
            r3 = 1
            r0 = 2131626441(0x7f0e09c9, float:1.8880118E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x084d:
            r3 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x086a
            r1 = 2131626473(0x7f0e09e9, float:1.8880183E38)
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
        L_0x086a:
            r4 = 2
            r0 = 2131626471(0x7f0e09e7, float:1.888018E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x087d:
            r3 = 1
            if (r6 == 0) goto L_0x0b19
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0894
            r0 = 2131626457(0x7f0e09d9, float:1.888015E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0894:
            boolean r10 = r0.isMusic()
            if (r10 == 0) goto L_0x08aa
            r0 = 2131626454(0x7f0e09d6, float:1.8880145E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08aa:
            boolean r3 = r0.isVideo()
            r10 = 2131626478(0x7f0e09ee, float:1.8880193E38)
            java.lang.String r11 = "NotificationActionPinnedTextChannel"
            if (r3 == 0) goto L_0x08f8
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x08e7
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x08e7
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
        L_0x08e7:
            r2 = 1
            r0 = 2131626481(0x7f0e09f1, float:1.88802E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x08f8:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x0941
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0930
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0930
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
        L_0x0930:
            r3 = 1
            r0 = 2131626448(0x7f0e09d0, float:1.8880132E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0941:
            r3 = 1
            boolean r4 = r0.isVoice()
            if (r4 == 0) goto L_0x0958
            r0 = 2131626484(0x7f0e09f4, float:1.8880205E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0958:
            boolean r4 = r0.isRoundVideo()
            if (r4 == 0) goto L_0x096e
            r0 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x096e:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x0aee
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x097c
            goto L_0x0aee
        L_0x097c:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r3.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x09c5
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x09b4
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x09b4
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
        L_0x09b4:
            r2 = 1
            r0 = 2131626433(0x7f0e09c1, float:1.8880102E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09c5:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x0add
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x09cf
            goto L_0x0add
        L_0x09cf:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x09e4
            r0 = 2131626444(0x7f0e09cc, float:1.8880124E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x09e4:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r5 == 0) goto L_0x0a06
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r4
            r0 = 2131626430(0x7f0e09be, float:1.8880096E38)
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
        L_0x0a06:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x0a3e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0a28
            r1 = 2131626466(0x7f0e09e2, float:1.888017E38)
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
        L_0x0a28:
            r2 = 2
            r3 = 1
            r1 = 2131626463(0x7f0e09df, float:1.8880163E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r4 = r6.title
            r2[r7] = r4
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0a3e:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0a83
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0a72
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a72
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
        L_0x0a72:
            r3 = 1
            r0 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a83:
            r3 = 1
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0a98
            r0 = 2131626436(0x7f0e09c4, float:1.8880108E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0a98:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x0acc
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0acc
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r2) goto L_0x0abd
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
        L_0x0abd:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r11, r10, r1)
            return r0
        L_0x0acc:
            r2 = 1
            r0 = 2131626457(0x7f0e09d9, float:1.888015E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0add:
            r2 = 1
            r0 = 2131626442(0x7f0e09ca, float:1.888012E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0aee:
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0b08
            r1 = 2131626474(0x7f0e09ea, float:1.8880185E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r6.title
            r2[r7] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0b08:
            r3 = 1
            r0 = 2131626472(0x7f0e09e8, float:1.8880181E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b19:
            org.telegram.messenger.MessageObject r0 = r0.replyMessageObject
            if (r0 != 0) goto L_0x0b2b
            r0 = 2131626458(0x7f0e09da, float:1.8880153E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b2b:
            boolean r6 = r0.isMusic()
            if (r6 == 0) goto L_0x0b3f
            r0 = 2131626455(0x7f0e09d7, float:1.8880147E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedMusicUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b3f:
            boolean r3 = r0.isVideo()
            r6 = 2131626479(0x7f0e09ef, float:1.8880195E38)
            java.lang.String r10 = "NotificationActionPinnedTextUser"
            if (r3 == 0) goto L_0x0b89
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0b7a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b7a
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
        L_0x0b7a:
            r2 = 1
            r0 = 2131626482(0x7f0e09f2, float:1.8880201E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedVideoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0b89:
            boolean r3 = r0.isGif()
            if (r3 == 0) goto L_0x0bce
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0bbf
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0bbf
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
        L_0x0bbf:
            r3 = 1
            r0 = 2131626449(0x7f0e09d1, float:1.8880135E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGifUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bce:
            r3 = 1
            boolean r4 = r0.isVoice()
            if (r4 == 0) goto L_0x0be3
            r0 = 2131626485(0x7f0e09f5, float:1.8880208E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedVoiceUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0be3:
            boolean r4 = r0.isRoundVideo()
            if (r4 == 0) goto L_0x0bf7
            r0 = 2131626470(0x7f0e09e6, float:1.8880177E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedRoundUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0bf7:
            boolean r3 = r0.isSticker()
            if (r3 != 0) goto L_0x0d5f
            boolean r3 = r0.isAnimatedSticker()
            if (r3 == 0) goto L_0x0CLASSNAME
            goto L_0x0d5f
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r3.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x0c4a
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0c3b
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0c3b
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
        L_0x0c3b:
            r2 = 1
            r0 = 2131626434(0x7f0e09c2, float:1.8880104E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedFileUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0c4a:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 != 0) goto L_0x0d50
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r5 == 0) goto L_0x0CLASSNAME
            goto L_0x0d50
        L_0x0CLASSNAME:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x0CLASSNAME
            r0 = 2131626445(0x7f0e09cd, float:1.8880126E38)
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
            r0 = 2131626431(0x7f0e09bf, float:1.8880098E38)
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
            if (r5 == 0) goto L_0x0cbb
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0ca7
            r1 = 2131626467(0x7f0e09e3, float:1.8880171E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r0 = r0.question
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0ca7:
            r2 = 2
            r3 = 1
            r1 = 2131626464(0x7f0e09e0, float:1.8880165E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r7] = r11
            java.lang.String r0 = r0.question
            r2[r3] = r0
            java.lang.String r0 = "NotificationActionPinnedPollUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            return r0
        L_0x0cbb:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x0cfc
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0ced
            java.lang.String r1 = r3.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ced
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
        L_0x0ced:
            r3 = 1
            r0 = 2131626461(0x7f0e09dd, float:1.8880159E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedPhotoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0cfc:
            r3 = 1
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r4 == 0) goto L_0x0d0f
            r0 = 2131626440(0x7f0e09c8, float:1.8880116E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGameUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d0f:
            java.lang.CharSequence r3 = r0.messageText
            if (r3 == 0) goto L_0x0d41
            int r3 = r3.length()
            if (r3 <= 0) goto L_0x0d41
            java.lang.CharSequence r0 = r0.messageText
            int r3 = r0.length()
            if (r3 <= r2) goto L_0x0d34
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r3.append(r0)
            r3.append(r1)
            java.lang.String r0 = r3.toString()
        L_0x0d34:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            r2 = 1
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r6, r1)
            return r0
        L_0x0d41:
            r2 = 1
            r0 = 2131626458(0x7f0e09da, float:1.8880153E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedNoTextUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d50:
            r2 = 1
            r0 = 2131626446(0x7f0e09ce, float:1.8880128E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedGeoUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d5f:
            r2 = 1
            java.lang.String r0 = r0.getStickerEmoji()
            if (r0 == 0) goto L_0x0d77
            r1 = 2131626475(0x7f0e09eb, float:1.8880187E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r7] = r11
            r3[r2] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            return r0
        L_0x0d77:
            r0 = 2131626476(0x7f0e09ec, float:1.888019E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r7] = r11
            java.lang.String r2 = "NotificationActionPinnedStickerUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0d85:
            return r15
        L_0x0d86:
            org.telegram.tgnet.TLRPC$Peer r1 = r2.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0db8
            boolean r1 = r6.megagroup
            if (r1 != 0) goto L_0x0db8
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x0da7
            r0 = 2131624814(0x7f0e036e, float:1.8876818E38)
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0da7:
            r1 = 1
            r0 = 2131624779(0x7f0e034b, float:1.8876747E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r6.title
            r1[r7] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0db8:
            boolean r0 = r19.isVideoAvatar()
            if (r0 == 0) goto L_0x0dd2
            r0 = 2131626490(0x7f0e09fa, float:1.8880218E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r3 = 1
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0dd2:
            r1 = 2
            r3 = 1
            r0 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r7] = r11
            java.lang.String r2 = r6.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0de6:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0ded:
            r3 = 1
            r0 = 2131626486(0x7f0e09f6, float:1.888021E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r7] = r11
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            return r0
        L_0x0dfc:
            boolean r1 = r19.isMediaEmpty()
            if (r1 == 0) goto L_0x0e19
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e11
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            return r0
        L_0x0e11:
            r0 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0e19:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0e5f
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0e43
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e43
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0e43:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0e55
            r0 = 2131624388(0x7f0e01c4, float:1.8875954E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e55:
            r0 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e5f:
            boolean r1 = r19.isVideo()
            if (r1 == 0) goto L_0x0ea5
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0e89
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0e89
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0e89:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0e9b
            r0 = 2131624389(0x7f0e01c5, float:1.8875956E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0e9b:
            r0 = 2131624411(0x7f0e01db, float:1.8876E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ea5:
            boolean r1 = r19.isGame()
            if (r1 == 0) goto L_0x0eb5
            r0 = 2131624391(0x7f0e01c7, float:1.887596E38)
            java.lang.String r1 = "AttachGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0eb5:
            boolean r1 = r19.isVoice()
            if (r1 == 0) goto L_0x0ec5
            r0 = 2131624385(0x7f0e01c1, float:1.8875948E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ec5:
            boolean r1 = r19.isRoundVideo()
            if (r1 == 0) goto L_0x0ed5
            r0 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ed5:
            boolean r1 = r19.isMusic()
            if (r1 == 0) goto L_0x0ee5
            r0 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ee5:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r2 == 0) goto L_0x0ef7
            r0 = 2131624387(0x7f0e01c3, float:1.8875952E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ef7:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0f0d
            r0 = 2131627264(0x7f0e0d00, float:1.8881788E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f0d:
            r0 = 2131627083(0x7f0e0c4b, float:1.888142E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r2 != 0) goto L_0x0fe7
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r2 == 0) goto L_0x0var_
            goto L_0x0fe7
        L_0x0var_:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x0f2f
            r0 = 2131624397(0x7f0e01cd, float:1.8875973E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0f2f:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0fd0
            boolean r1 = r19.isSticker()
            if (r1 != 0) goto L_0x0fa2
            boolean r1 = r19.isAnimatedSticker()
            if (r1 == 0) goto L_0x0var_
            goto L_0x0fa2
        L_0x0var_:
            boolean r1 = r19.isGif()
            if (r1 == 0) goto L_0x0var_
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0f6a
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0f6a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            return r0
        L_0x0f6a:
            r0 = 2131624392(0x7f0e01c8, float:1.8875962E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0var_:
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
            r0 = 2131624390(0x7f0e01c6, float:1.8875958E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fa2:
            java.lang.String r0 = r19.getStickerEmoji()
            if (r0 == 0) goto L_0x0fc6
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
        L_0x0fc6:
            r0 = 2131624408(0x7f0e01d8, float:1.8875995E38)
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0fd0:
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0fdf
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r0 = r0.toString()
            return r0
        L_0x0fdf:
            r0 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0fe7:
            r0 = 2131624401(0x7f0e01d1, float:1.887598E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            return r0
        L_0x0ff1:
            if (r21 == 0) goto L_0x0ff5
            r21[r7] = r7
        L_0x0ff5:
            r0 = 2131626212(0x7f0e08e4, float:1.8879654E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r12, r0)
            return r0
        L_0x0ffd:
            r0 = 2131626506(0x7f0e0a0a, float:1.888025E38)
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
            if (r1 != 0) goto L_0x1468
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 == 0) goto L_0x000e
            goto L_0x1468
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
            r10 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.String r11 = "NotificationMessageGroupNoText"
            r12 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
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
            r1 = 2131624763(0x7f0e033b, float:1.8876715E38)
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
            r12 = 2131626231(0x7f0e08f7, float:1.8879692E38)
            java.lang.String r13 = "MessageScheduledReminderNotification"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            goto L_0x0136
        L_0x0102:
            r12 = 2131626550(0x7f0e0a36, float:1.888034E38)
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
            r0 = 2131628532(0x7f0e11f4, float:1.888436E38)
            java.lang.String r1 = "YouHaveNewMessage"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1467
        L_0x0159:
            java.lang.String r2 = "🎬 "
            java.lang.String r3 = "📎 "
            java.lang.String r13 = "📹 "
            java.lang.String r15 = "🖼 "
            java.lang.String r14 = "NotificationMessageText"
            r6 = 3
            if (r4 != 0) goto L_0x0555
            if (r1 == 0) goto L_0x0555
            if (r8 == 0) goto L_0x0541
            java.lang.String r1 = "EnablePreviewAll"
            boolean r1 = r7.getBoolean(r1, r5)
            if (r1 == 0) goto L_0x0541
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r4 == 0) goto L_0x0246
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r2 == 0) goto L_0x0186
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1467
        L_0x0186:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r2 != 0) goto L_0x0236
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r2 == 0) goto L_0x0190
            goto L_0x0236
        L_0x0190:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r2 == 0) goto L_0x01a4
            r0 = 2131626487(0x7f0e09f7, float:1.8880212E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x01a4:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r2 == 0) goto L_0x0207
            r1 = 2131628609(0x7f0e1241, float:1.8884516E38)
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
            r2 = 2131626556(0x7f0e0a3c, float:1.8880352E38)
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
            goto L_0x1467
        L_0x0207:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 != 0) goto L_0x022e
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r2 == 0) goto L_0x0210
            goto L_0x022e
        L_0x0210:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x1465
            boolean r0 = r1.video
            if (r0 == 0) goto L_0x0223
            r0 = 2131624638(0x7f0e02be, float:1.8876461E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1467
        L_0x0223:
            r0 = 2131624632(0x7f0e02b8, float:1.887645E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r15 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x1467
        L_0x022e:
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1467
        L_0x0236:
            r0 = 2131626486(0x7f0e09f6, float:1.888021E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0246:
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x028f
            if (r21 != 0) goto L_0x027f
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x026f
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1467
        L_0x026f:
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r4 = r17
            r1 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1467
        L_0x027f:
            r4 = r17
            r1 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            r2 = 0
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1467
        L_0x028f:
            r4 = r17
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r1.media
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r6 == 0) goto L_0x02f6
            if (r21 != 0) goto L_0x02cf
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x02cf
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x02cf
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
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1467
        L_0x02cf:
            r2 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x02e7
            r0 = 2131626547(0x7f0e0a33, float:1.8880333E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSDPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x02e7:
            r0 = 2131626543(0x7f0e0a2f, float:1.8880325E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x02f6:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x035b
            if (r21 != 0) goto L_0x0334
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0334
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0334
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
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r6] = r5
            goto L_0x1467
        L_0x0334:
            r6 = 0
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x034c
            r0 = 2131626548(0x7f0e0a34, float:1.8880335E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageSDVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x034c:
            r0 = 2131626554(0x7f0e0a3a, float:1.8880347E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x035b:
            r6 = 0
            boolean r1 = r20.isGame()
            if (r1 == 0) goto L_0x037c
            r1 = 2131626516(0x7f0e0a14, float:1.888027E38)
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
            goto L_0x1467
        L_0x037c:
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x0392
            r0 = 2131626511(0x7f0e0a0f, float:1.888026E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r6 = 0
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0392:
            r6 = 0
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x03a8
            r0 = 2131626546(0x7f0e0a32, float:1.8880331E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x03a8:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x03bd
            r0 = 2131626541(0x7f0e0a2d, float:1.8880321E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r6] = r12
            java.lang.String r2 = "NotificationMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x03bd:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r7 == 0) goto L_0x03e1
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131626512(0x7f0e0a10, float:1.8880262E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r6] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "NotificationMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1467
        L_0x03e1:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x0417
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0401
            r1 = 2131626545(0x7f0e0a31, float:1.888033E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0414
        L_0x0401:
            r2 = 2
            r3 = 0
            r1 = 2131626544(0x7f0e0a30, float:1.8880327E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
        L_0x0414:
            r15 = r0
            goto L_0x1467
        L_0x0417:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r6 != 0) goto L_0x0531
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r6 == 0) goto L_0x0421
            goto L_0x0531
        L_0x0421:
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r6 == 0) goto L_0x0435
            r0 = 2131626539(0x7f0e0a2b, float:1.8880317E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0435:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x0505
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x04dd
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x0447
            goto L_0x04dd
        L_0x0447:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x0495
            if (r21 != 0) goto L_0x0485
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x0485
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0485
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
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x1467
        L_0x0485:
            r3 = 0
            r0 = 2131626518(0x7f0e0a16, float:1.8880274E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "NotificationMessageGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0495:
            if (r21 != 0) goto L_0x04cd
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x04cd
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x04cd
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
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1467
        L_0x04cd:
            r2 = 0
            r0 = 2131626513(0x7f0e0a11, float:1.8880264E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x04dd:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x04f6
            r1 = 2131626552(0x7f0e0a38, float:1.8880343E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "NotificationMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0414
        L_0x04f6:
            r0 = 2131626551(0x7f0e0a37, float:1.8880341E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0505:
            r2 = 0
            if (r21 != 0) goto L_0x0524
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0524
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1467
        L_0x0524:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1467
        L_0x0531:
            r2 = 0
            r0 = 2131626540(0x7f0e0a2c, float:1.888032E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "NotificationMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0541:
            r4 = r17
            r2 = 0
            if (r23 == 0) goto L_0x0548
            r23[r2] = r2
        L_0x0548:
            java.lang.Object[] r0 = new java.lang.Object[r5]
            r0[r2] = r12
            r1 = 2131626542(0x7f0e0a2e, float:1.8880323E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r4, r1, r0)
            goto L_0x1467
        L_0x0555:
            if (r4 == 0) goto L_0x1465
            boolean r4 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r4 == 0) goto L_0x0563
            boolean r4 = r10.megagroup
            if (r4 != 0) goto L_0x0563
            r4 = 1
            goto L_0x0564
        L_0x0563:
            r4 = 0
        L_0x0564:
            if (r8 == 0) goto L_0x1437
            if (r4 != 0) goto L_0x0570
            java.lang.String r8 = "EnablePreviewGroup"
            boolean r8 = r7.getBoolean(r8, r5)
            if (r8 != 0) goto L_0x057a
        L_0x0570:
            if (r4 == 0) goto L_0x1437
            java.lang.String r4 = "EnablePreviewChannel"
            boolean r4 = r7.getBoolean(r4, r5)
            if (r4 == 0) goto L_0x1437
        L_0x057a:
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            if (r7 == 0) goto L_0x0e94
            org.telegram.tgnet.TLRPC$MessageAction r7 = r4.action
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r8 == 0) goto L_0x0692
            int r2 = r7.user_id
            if (r2 != 0) goto L_0x05a3
            java.util.ArrayList<java.lang.Integer> r3 = r7.users
            int r3 = r3.size()
            if (r3 != r5) goto L_0x05a3
            org.telegram.tgnet.TLRPC$Message r2 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Integer> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Integer r2 = (java.lang.Integer) r2
            int r2 = r2.intValue()
        L_0x05a3:
            if (r2 == 0) goto L_0x063a
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x05c6
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x05c6
            r0 = 2131624712(0x7f0e0308, float:1.8876611E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "ChannelAddedByNotification"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x05c6:
            r3 = 2
            r4 = 0
            if (r2 != r9) goto L_0x05dd
            r0 = 2131626508(0x7f0e0a0c, float:1.8880254E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x05dd:
            org.telegram.messenger.MessagesController r0 = r19.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            if (r0 != 0) goto L_0x05ed
            r2 = 0
            return r2
        L_0x05ed:
            int r2 = r0.id
            if (r1 != r2) goto L_0x061f
            boolean r0 = r10.megagroup
            if (r0 == 0) goto L_0x060a
            r0 = 2131626493(0x7f0e09fd, float:1.8880224E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x060a:
            r1 = 2
            r2 = 0
            r0 = 2131626492(0x7f0e09fc, float:1.8880222E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupAddSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x061f:
            r2 = 0
            r1 = 2131626491(0x7f0e09fb, float:1.888022E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            java.lang.String r0 = org.telegram.messenger.UserObject.getUserName(r0)
            r2 = 2
            r3[r2] = r0
            java.lang.String r0 = "NotificationGroupAddMember"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0414
        L_0x063a:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0640:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0677
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0674
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0671
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0671:
            r1.append(r3)
        L_0x0674:
            int r2 = r2 + 1
            goto L_0x0640
        L_0x0677:
            r0 = 2131626491(0x7f0e09fb, float:1.888022E38)
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
            goto L_0x0414
        L_0x0692:
            r8 = 2
            boolean r11 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r11 == 0) goto L_0x06ab
            r0 = 2131626495(0x7f0e09ff, float:1.8880228E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupCreatedCall"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x06ab:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            if (r8 == 0) goto L_0x06b7
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1467
        L_0x06b7:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            if (r8 == 0) goto L_0x0775
            int r1 = r7.user_id
            if (r1 != 0) goto L_0x06d9
            java.util.ArrayList<java.lang.Integer> r2 = r7.users
            int r2 = r2.size()
            if (r2 != r5) goto L_0x06d9
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.util.ArrayList<java.lang.Integer> r1 = r1.users
            r2 = 0
            java.lang.Object r1 = r1.get(r2)
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            goto L_0x06da
        L_0x06d9:
            r2 = 0
        L_0x06da:
            if (r1 == 0) goto L_0x071d
            if (r1 != r9) goto L_0x06f2
            r0 = 2131626500(0x7f0e0a04, float:1.8880238E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupInvitedYouToCall"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x06f2:
            org.telegram.messenger.MessagesController r0 = r19.getMessagesController()
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r1)
            if (r0 != 0) goto L_0x0702
            r1 = 0
            return r1
        L_0x0702:
            r1 = 2131626499(0x7f0e0a03, float:1.8880236E38)
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
            goto L_0x0414
        L_0x071d:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
        L_0x0723:
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            java.util.ArrayList<java.lang.Integer> r3 = r3.users
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x075a
            org.telegram.messenger.MessagesController r3 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            java.util.ArrayList<java.lang.Integer> r4 = r4.users
            java.lang.Object r4 = r4.get(r2)
            java.lang.Integer r4 = (java.lang.Integer) r4
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            if (r3 == 0) goto L_0x0757
            java.lang.String r3 = org.telegram.messenger.UserObject.getUserName(r3)
            int r4 = r1.length()
            if (r4 == 0) goto L_0x0754
            java.lang.String r4 = ", "
            r1.append(r4)
        L_0x0754:
            r1.append(r3)
        L_0x0757:
            int r2 = r2 + 1
            goto L_0x0723
        L_0x075a:
            r0 = 2131626499(0x7f0e0a03, float:1.8880236E38)
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
            goto L_0x0414
        L_0x0775:
            r8 = 2
            boolean r11 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r11 == 0) goto L_0x078e
            r0 = 2131626509(0x7f0e0a0d, float:1.8880256E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r11 = 0
            r1[r11] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationInvitedToGroupByLink"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x078e:
            r11 = 0
            boolean r14 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r14 == 0) goto L_0x07a6
            r0 = 2131626488(0x7f0e09f8, float:1.8880214E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r11] = r12
            java.lang.String r2 = r7.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupName"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x07a6:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r8 != 0) goto L_0x0e31
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r8 == 0) goto L_0x07b0
            goto L_0x0e31
        L_0x07b0:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r4 == 0) goto L_0x0815
            int r2 = r7.user_id
            if (r2 != r9) goto L_0x07cd
            r0 = 2131626502(0x7f0e0a06, float:1.8880242E38)
            r3 = 2
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r4 = 0
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupKickYou"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x07cd:
            r3 = 2
            r4 = 0
            if (r2 != r1) goto L_0x07e4
            r0 = 2131626503(0x7f0e0a07, float:1.8880244E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationGroupLeftMember"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x07e4:
            org.telegram.messenger.MessagesController r1 = r19.getMessagesController()
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            int r0 = r0.user_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r0 = r1.getUser(r0)
            if (r0 != 0) goto L_0x07fa
            r1 = 0
            return r1
        L_0x07fa:
            r1 = 2131626501(0x7f0e0a05, float:1.888024E38)
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
            goto L_0x1467
        L_0x0815:
            r1 = 0
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r4 == 0) goto L_0x0822
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1467
        L_0x0822:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x082e
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1467
        L_0x082e:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r4 == 0) goto L_0x0844
            r0 = 2131624139(0x7f0e00cb, float:1.887545E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0844:
            r4 = 0
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r8 == 0) goto L_0x085a
            r0 = 2131624139(0x7f0e00cb, float:1.887545E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r7.title
            r1[r4] = r2
            java.lang.String r2 = "ActionMigrateFromGroupNotify"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x085a:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r4 == 0) goto L_0x0866
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1467
        L_0x0866:
            boolean r4 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r4 == 0) goto L_0x0e25
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x0b32
            boolean r1 = r10.megagroup
            if (r1 == 0) goto L_0x0876
            goto L_0x0b32
        L_0x0876:
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x088c
            r0 = 2131626457(0x7f0e09d9, float:1.888015E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x088c:
            r4 = 0
            boolean r6 = r1.isMusic()
            if (r6 == 0) goto L_0x08a4
            r0 = 2131626454(0x7f0e09d6, float:1.8880145E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedMusicChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x08a4:
            boolean r4 = r1.isVideo()
            r6 = 2131626478(0x7f0e09ee, float:1.8880193E38)
            java.lang.String r7 = "NotificationActionPinnedTextChannel"
            if (r4 == 0) goto L_0x08f4
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x08e2
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x08e2
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
            goto L_0x0414
        L_0x08e2:
            r3 = 0
            r0 = 2131626481(0x7f0e09f1, float:1.88802E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedVideoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x08f4:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x093f
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x092d
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x092d
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
            goto L_0x0414
        L_0x092d:
            r4 = 0
            r0 = 2131626448(0x7f0e09d0, float:1.8880132E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGifChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x093f:
            r4 = 0
            boolean r2 = r1.isVoice()
            if (r2 == 0) goto L_0x0957
            r0 = 2131626484(0x7f0e09f4, float:1.8880205E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedVoiceChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0957:
            boolean r2 = r1.isRoundVideo()
            if (r2 == 0) goto L_0x096e
            r0 = 2131626469(0x7f0e09e5, float:1.8880175E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedRoundChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x096e:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0b06
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x097c
            goto L_0x0b06
        L_0x097c:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r8 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r8 == 0) goto L_0x09c7
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x09b5
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x09b5
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
            goto L_0x0414
        L_0x09b5:
            r3 = 0
            r0 = 2131626433(0x7f0e09c1, float:1.8880102E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedFileChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x09c7:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0af4
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x09d1
            goto L_0x0af4
        L_0x09d1:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x09e7
            r0 = 2131626444(0x7f0e09cc, float:1.8880124E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLiveChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x09e7:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0a0e
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626430(0x7f0e09be, float:1.8880096E38)
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
            goto L_0x0414
        L_0x0a0e:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0a48
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0a31
            r1 = 2131626466(0x7f0e09e2, float:1.888017E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedQuizChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0414
        L_0x0a31:
            r2 = 2
            r4 = 0
            r1 = 2131626463(0x7f0e09df, float:1.8880163E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r2[r4] = r3
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedPollChannel2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0414
        L_0x0a48:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0a8f
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0a7d
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0a7d
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
            goto L_0x0414
        L_0x0a7d:
            r3 = 0
            r0 = 2131626460(0x7f0e09dc, float:1.8880157E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedPhotoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0a8f:
            r3 = 0
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0aa5
            r0 = 2131626436(0x7f0e09c4, float:1.8880108E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "NotificationActionPinnedGameChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0aa5:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0ae2
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0ae2
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0ad2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r4 = 0
            java.lang.CharSequence r0 = r0.subSequence(r4, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0ad3
        L_0x0ad2:
            r4 = 0
        L_0x0ad3:
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            r1[r5] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r7, r6, r1)
            goto L_0x0414
        L_0x0ae2:
            r4 = 0
            r0 = 2131626457(0x7f0e09d9, float:1.888015E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedNoTextChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0af4:
            r4 = 0
            r0 = 2131626442(0x7f0e09ca, float:1.888012E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0b06:
            r4 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0b21
            r1 = 2131626474(0x7f0e09ea, float:1.8880185E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = r10.title
            r2[r4] = r3
            r2[r5] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmojiChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0414
        L_0x0b21:
            r0 = 2131626472(0x7f0e09e8, float:1.8880181E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r4] = r2
            java.lang.String r2 = "NotificationActionPinnedStickerChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0b32:
            r4 = 0
            org.telegram.messenger.MessageObject r1 = r0.replyMessageObject
            if (r1 != 0) goto L_0x0b4b
            r0 = 2131626456(0x7f0e09d8, float:1.8880149E38)
            r7 = 2
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0b4b:
            r7 = 2
            boolean r8 = r1.isMusic()
            if (r8 == 0) goto L_0x0b65
            r0 = 2131626453(0x7f0e09d5, float:1.8880143E38)
            java.lang.Object[] r1 = new java.lang.Object[r7]
            r1[r4] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0b65:
            boolean r4 = r1.isVideo()
            r7 = 2131626477(0x7f0e09ed, float:1.8880191E38)
            java.lang.String r8 = "NotificationActionPinnedText"
            if (r4 == 0) goto L_0x0bba
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r0 < r2) goto L_0x0ba5
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0ba5
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
            goto L_0x0414
        L_0x0ba5:
            r2 = 0
            r3 = 2
            r0 = 2131626480(0x7f0e09f0, float:1.8880197E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0bba:
            boolean r4 = r1.isGif()
            if (r4 == 0) goto L_0x0c0a
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0bf5
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0bf5
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
            goto L_0x0414
        L_0x0bf5:
            r2 = 0
            r4 = 2
            r0 = 2131626447(0x7f0e09cf, float:1.888013E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0c0a:
            r2 = 0
            r4 = 2
            boolean r9 = r1.isVoice()
            if (r9 == 0) goto L_0x0CLASSNAME
            r0 = 2131626483(0x7f0e09f3, float:1.8880203E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0CLASSNAME:
            boolean r9 = r1.isRoundVideo()
            if (r9 == 0) goto L_0x0c3e
            r0 = 2131626468(0x7f0e09e4, float:1.8880173E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0c3e:
            boolean r2 = r1.isSticker()
            if (r2 != 0) goto L_0x0df4
            boolean r2 = r1.isAnimatedSticker()
            if (r2 == 0) goto L_0x0c4c
            goto L_0x0df4
        L_0x0c4c:
            org.telegram.tgnet.TLRPC$Message r2 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            boolean r9 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r9 == 0) goto L_0x0c9c
            int r0 = android.os.Build.VERSION.SDK_INT
            r4 = 19
            if (r0 < r4) goto L_0x0CLASSNAME
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0CLASSNAME
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
            goto L_0x0414
        L_0x0CLASSNAME:
            r2 = 0
            r3 = 2
            r0 = 2131626432(0x7f0e09c0, float:1.88801E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0c9c:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r3 != 0) goto L_0x0ddf
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r3 == 0) goto L_0x0ca6
            goto L_0x0ddf
        L_0x0ca6:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r3 == 0) goto L_0x0cbf
            r0 = 2131626443(0x7f0e09cb, float:1.8880122E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0cbf:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r3 == 0) goto L_0x0ce8
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r0
            r1 = 2131626429(0x7f0e09bd, float:1.8880094E38)
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
            goto L_0x0414
        L_0x0ce8:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0d26
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0d0d
            r1 = 2131626465(0x7f0e09e1, float:1.8880167E38)
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
            goto L_0x0414
        L_0x0d0d:
            r3 = 0
            r4 = 2
            r1 = 2131626462(0x7f0e09de, float:1.888016E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0414
        L_0x0d26:
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0d72
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r0 < r3) goto L_0x0d5d
            java.lang.String r0 = r2.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0d5d
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
            goto L_0x0414
        L_0x0d5d:
            r2 = 0
            r3 = 2
            r0 = 2131626459(0x7f0e09db, float:1.8880155E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0d72:
            r2 = 0
            r3 = 2
            boolean r0 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0d8b
            r0 = 2131626435(0x7f0e09c3, float:1.8880106E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0d8b:
            java.lang.CharSequence r0 = r1.messageText
            if (r0 == 0) goto L_0x0dca
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0dca
            java.lang.CharSequence r0 = r1.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x0db8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 20
            r3 = 0
            java.lang.CharSequence r0 = r0.subSequence(r3, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            goto L_0x0db9
        L_0x0db8:
            r3 = 0
        L_0x0db9:
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r1[r3] = r12
            r1[r5] = r0
            java.lang.String r0 = r10.title
            r2 = 2
            r1[r2] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r8, r7, r1)
            goto L_0x0414
        L_0x0dca:
            r2 = 2
            r3 = 0
            r0 = 2131626456(0x7f0e09d8, float:1.8880149E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedNoText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0ddf:
            r2 = 2
            r3 = 0
            r0 = 2131626441(0x7f0e09c9, float:1.8880118E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0df4:
            r3 = 0
            java.lang.String r0 = r1.getStickerEmoji()
            if (r0 == 0) goto L_0x0e11
            r1 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            r4 = 2
            r2[r4] = r0
            java.lang.String r0 = "NotificationActionPinnedStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0414
        L_0x0e11:
            r4 = 2
            r0 = 2131626471(0x7f0e09e7, float:1.888018E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x0e25:
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r2 == 0) goto L_0x1466
            java.lang.CharSequence r0 = r0.messageText
            java.lang.String r15 = r0.toString()
            goto L_0x1467
        L_0x0e31:
            org.telegram.tgnet.TLRPC$Peer r1 = r4.peer_id
            int r1 = r1.channel_id
            if (r1 == 0) goto L_0x0e65
            boolean r1 = r10.megagroup
            if (r1 != 0) goto L_0x0e65
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0e53
            r0 = 2131624814(0x7f0e036e, float:1.8876818E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ChannelVideoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0e53:
            r3 = 0
            r0 = 2131624779(0x7f0e034b, float:1.8876747E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            java.lang.String r2 = r10.title
            r1[r3] = r2
            java.lang.String r2 = "ChannelPhotoEditNotification"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0e65:
            r3 = 0
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0e80
            r0 = 2131626490(0x7f0e09fa, float:1.8880218E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0e80:
            r1 = 2
            r0 = 2131626489(0x7f0e09f9, float:1.8880216E38)
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r3] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationEditedGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0e94:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r1 == 0) goto L_0x1139
            boolean r1 = r10.megagroup
            if (r1 != 0) goto L_0x1139
            boolean r1 = r20.isMediaEmpty()
            if (r1 == 0) goto L_0x0ed7
            if (r21 != 0) goto L_0x0ec7
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0ec7
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            r1[r5] = r0
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1467
        L_0x0ec7:
            r2 = 0
            r0 = 2131624763(0x7f0e033b, float:1.8876715E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0ed7:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r1.media
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r4 == 0) goto L_0x0var_
            if (r21 != 0) goto L_0x0var_
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x0var_
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
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
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1467
        L_0x0var_:
            r2 = 0
            r0 = 2131624764(0x7f0e033c, float:1.8876717E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessagePhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0var_:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x0var_
            if (r21 != 0) goto L_0x0var_
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0var_
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
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r4] = r5
            goto L_0x1467
        L_0x0var_:
            r4 = 0
            r0 = 2131624770(0x7f0e0342, float:1.887673E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageVideo"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0var_:
            r4 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x0var_
            r0 = 2131624755(0x7f0e0333, float:1.8876699E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0var_:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x0f9e
            r0 = 2131624767(0x7f0e033f, float:1.8876723E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0f9e:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x0fb3
            r0 = 2131624762(0x7f0e033a, float:1.8876713E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r4] = r12
            java.lang.String r2 = "ChannelMessageMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x0fb3:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r6 == 0) goto L_0x0fd7
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131624756(0x7f0e0334, float:1.88767E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r4] = r12
            java.lang.String r3 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r3, r1)
            r2[r5] = r1
            java.lang.String r1 = "ChannelMessageContact2"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            goto L_0x1467
        L_0x0fd7:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r4 == 0) goto L_0x100d
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x0ff8
            r1 = 2131624766(0x7f0e033e, float:1.887672E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessageQuiz2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0414
        L_0x0ff8:
            r2 = 2
            r3 = 0
            r1 = 2131624765(0x7f0e033d, float:1.8876719E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r3] = r12
            java.lang.String r0 = r0.question
            r2[r5] = r0
            java.lang.String r0 = "ChannelMessagePoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0414
        L_0x100d:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r4 != 0) goto L_0x1129
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r4 == 0) goto L_0x1017
            goto L_0x1129
        L_0x1017:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r4 == 0) goto L_0x102b
            r0 = 2131624760(0x7f0e0338, float:1.8876709E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x102b:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x10fb
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x10d3
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x103d
            goto L_0x10d3
        L_0x103d:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x108b
            if (r21 != 0) goto L_0x107b
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x107b
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x107b
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
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r3] = r5
            goto L_0x1467
        L_0x107b:
            r3 = 0
            r0 = 2131624759(0x7f0e0337, float:1.8876707E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r3] = r12
            java.lang.String r2 = "ChannelMessageGIF"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x108b:
            if (r21 != 0) goto L_0x10c3
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x10c3
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x10c3
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
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1467
        L_0x10c3:
            r2 = 0
            r0 = 2131624757(0x7f0e0335, float:1.8876703E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x10d3:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x10ec
            r1 = 2131624769(0x7f0e0341, float:1.8876727E38)
            r3 = 2
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r3[r2] = r12
            r3[r5] = r0
            java.lang.String r0 = "ChannelMessageStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0414
        L_0x10ec:
            r0 = 2131624768(0x7f0e0340, float:1.8876725E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x10fb:
            r2 = 0
            if (r21 != 0) goto L_0x111a
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x111a
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r1[r2] = r12
            java.lang.CharSequence r0 = r0.messageText
            r1[r5] = r0
            r0 = 2131626553(0x7f0e0a39, float:1.8880345E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r22[r2] = r5
            goto L_0x1467
        L_0x111a:
            r0 = 2131624763(0x7f0e033b, float:1.8876715E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x1129:
            r2 = 0
            r0 = 2131624761(0x7f0e0339, float:1.887671E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x1139:
            boolean r1 = r20.isMediaEmpty()
            r4 = 2131626536(0x7f0e0a28, float:1.888031E38)
            java.lang.String r7 = "NotificationMessageGroupText"
            if (r1 == 0) goto L_0x1179
            if (r21 != 0) goto L_0x1166
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1166
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
            goto L_0x1467
        L_0x1166:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x1467
        L_0x1179:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r1.media
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r8 == 0) goto L_0x11cb
            if (r21 != 0) goto L_0x11b6
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r2 < r3) goto L_0x11b6
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x11b6
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
            goto L_0x1467
        L_0x11b6:
            r2 = 2
            r0 = 2131626530(0x7f0e0a22, float:1.8880299E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupPhoto"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x11cb:
            boolean r1 = r20.isVideo()
            if (r1 == 0) goto L_0x121d
            if (r21 != 0) goto L_0x1208
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x1208
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1208
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
            goto L_0x1467
        L_0x1208:
            r8 = 2
            r0 = 2131626537(0x7f0e0a29, float:1.8880313E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r9 = 0
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = " "
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x121d:
            r8 = 2
            r9 = 0
            boolean r1 = r20.isVoice()
            if (r1 == 0) goto L_0x1238
            r0 = 2131626519(0x7f0e0a17, float:1.8880276E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupAudio"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x1238:
            boolean r1 = r20.isRoundVideo()
            if (r1 == 0) goto L_0x1251
            r0 = 2131626533(0x7f0e0a25, float:1.8880305E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupRound"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x1251:
            boolean r1 = r20.isMusic()
            if (r1 == 0) goto L_0x126a
            r0 = 2131626528(0x7f0e0a20, float:1.8880295E38)
            java.lang.Object[] r1 = new java.lang.Object[r8]
            r1[r9] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMusic"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x126a:
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r8 == 0) goto L_0x1293
            org.telegram.tgnet.TLRPC$TL_messageMediaContact r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaContact) r1
            r0 = 2131626520(0x7f0e0a18, float:1.8880279E38)
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
            goto L_0x1467
        L_0x1293:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r8 == 0) goto L_0x12d1
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r1 = r0.quiz
            if (r1 == 0) goto L_0x12b8
            r1 = 2131626532(0x7f0e0a24, float:1.8880303E38)
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
            goto L_0x0414
        L_0x12b8:
            r3 = 0
            r4 = 2
            r1 = 2131626531(0x7f0e0a23, float:1.88803E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r3] = r12
            java.lang.String r3 = r10.title
            r2[r5] = r3
            java.lang.String r0 = r0.question
            r2[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupPoll2"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0414
        L_0x12d1:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r8 == 0) goto L_0x12f0
            r0 = 2131626522(0x7f0e0a1a, float:1.8880283E38)
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
            goto L_0x1467
        L_0x12f0:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r8 != 0) goto L_0x1423
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r8 == 0) goto L_0x12fa
            goto L_0x1423
        L_0x12fa:
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r8 == 0) goto L_0x1313
            r0 = 2131626526(0x7f0e0a1e, float:1.888029E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupLiveLocation"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x1313:
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r1 == 0) goto L_0x13f4
            boolean r1 = r20.isSticker()
            if (r1 != 0) goto L_0x13c3
            boolean r1 = r20.isAnimatedSticker()
            if (r1 == 0) goto L_0x1325
            goto L_0x13c3
        L_0x1325:
            boolean r1 = r20.isGif()
            if (r1 == 0) goto L_0x1377
            if (r21 != 0) goto L_0x1362
            int r1 = android.os.Build.VERSION.SDK_INT
            r3 = 19
            if (r1 < r3) goto L_0x1362
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1362
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
            goto L_0x1467
        L_0x1362:
            r2 = 2
            r0 = 2131626524(0x7f0e0a1c, float:1.8880287E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupGif"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x1377:
            if (r21 != 0) goto L_0x13ae
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 19
            if (r1 < r2) goto L_0x13ae
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            java.lang.String r1 = r1.message
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x13ae
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
            goto L_0x1467
        L_0x13ae:
            r2 = 2
            r0 = 2131626521(0x7f0e0a19, float:1.888028E38)
            java.lang.Object[] r1 = new java.lang.Object[r2]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupDocument"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x13c3:
            r2 = 0
            java.lang.String r0 = r20.getStickerEmoji()
            if (r0 == 0) goto L_0x13e0
            r1 = 2131626535(0x7f0e0a27, float:1.8880309E38)
            java.lang.Object[] r3 = new java.lang.Object[r6]
            r3[r2] = r12
            java.lang.String r2 = r10.title
            r3[r5] = r2
            r4 = 2
            r3[r4] = r0
            java.lang.String r0 = "NotificationMessageGroupStickerEmoji"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r3)
            goto L_0x0414
        L_0x13e0:
            r4 = 2
            r0 = 2131626534(0x7f0e0a26, float:1.8880307E38)
            java.lang.Object[] r1 = new java.lang.Object[r4]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0414
        L_0x13f4:
            if (r21 != 0) goto L_0x1411
            java.lang.CharSequence r1 = r0.messageText
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x1411
            java.lang.Object[] r1 = new java.lang.Object[r6]
            r2 = 0
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.CharSequence r0 = r0.messageText
            r3 = 2
            r1[r3] = r0
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r7, r4, r1)
            goto L_0x1467
        L_0x1411:
            r2 = 0
            r3 = 2
            java.lang.Object[] r0 = new java.lang.Object[r3]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x1467
        L_0x1423:
            r2 = 0
            r3 = 2
            r0 = 2131626527(0x7f0e0a1f, float:1.8880293E38)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            r1[r2] = r12
            java.lang.String r2 = r10.title
            r1[r5] = r2
            java.lang.String r2 = "NotificationMessageGroupMap"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x1437:
            r2 = 0
            if (r23 == 0) goto L_0x143c
            r23[r2] = r2
        L_0x143c:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r10)
            if (r0 == 0) goto L_0x1454
            boolean r0 = r10.megagroup
            if (r0 != 0) goto L_0x1454
            r0 = 2131624763(0x7f0e033b, float:1.8876715E38)
            java.lang.Object[] r1 = new java.lang.Object[r5]
            r1[r2] = r12
            java.lang.String r2 = "ChannelMessageNoText"
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x1467
        L_0x1454:
            r0 = 2
            java.lang.Object[] r0 = new java.lang.Object[r0]
            r0[r2] = r12
            java.lang.String r1 = r10.title
            r0[r5] = r1
            r1 = 2131626529(0x7f0e0a21, float:1.8880297E38)
            java.lang.String r15 = org.telegram.messenger.LocaleController.formatString(r11, r1, r0)
            goto L_0x1467
        L_0x1465:
            r1 = 0
        L_0x1466:
            r15 = r1
        L_0x1467:
            return r15
        L_0x1468:
            r0 = 2131628532(0x7f0e11f4, float:1.888436E38)
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

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00d9 A[Catch:{ Exception -> 0x013a }] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00dd A[Catch:{ Exception -> 0x013a }] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00ea A[Catch:{ Exception -> 0x013a }] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00ef A[Catch:{ Exception -> 0x013a }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0121 A[Catch:{ Exception -> 0x013a }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0122 A[Catch:{ Exception -> 0x013a }] */
    @android.annotation.SuppressLint({"RestrictedApi"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String createNotificationShortcut(androidx.core.app.NotificationCompat.Builder r10, int r11, java.lang.String r12, org.telegram.tgnet.TLRPC$User r13, org.telegram.tgnet.TLRPC$Chat r14, androidx.core.app.Person r15) {
        /*
            r9 = this;
            java.lang.String r0 = "com.tmessages.openchat"
            boolean r1 = r9.unsupportedNotificationShortcut()
            r2 = 0
            if (r1 != 0) goto L_0x013e
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r14)
            if (r1 == 0) goto L_0x0015
            boolean r1 = r14.megagroup
            if (r1 != 0) goto L_0x0015
            goto L_0x013e
        L_0x0015:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013a }
            r1.<init>()     // Catch:{ Exception -> 0x013a }
            java.lang.String r3 = "ndid_"
            r1.append(r3)     // Catch:{ Exception -> 0x013a }
            r1.append(r11)     // Catch:{ Exception -> 0x013a }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x013a }
            android.content.Intent r3 = new android.content.Intent     // Catch:{ Exception -> 0x013a }
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x013a }
            java.lang.Class<org.telegram.messenger.OpenChatReceiver> r5 = org.telegram.messenger.OpenChatReceiver.class
            r3.<init>(r4, r5)     // Catch:{ Exception -> 0x013a }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013a }
            r4.<init>()     // Catch:{ Exception -> 0x013a }
            r4.append(r0)     // Catch:{ Exception -> 0x013a }
            double r5 = java.lang.Math.random()     // Catch:{ Exception -> 0x013a }
            r4.append(r5)     // Catch:{ Exception -> 0x013a }
            r5 = 2147483647(0x7fffffff, float:NaN)
            r4.append(r5)     // Catch:{ Exception -> 0x013a }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x013a }
            r3.setAction(r4)     // Catch:{ Exception -> 0x013a }
            java.lang.String r4 = "userId"
            java.lang.String r6 = "chatId"
            if (r11 <= 0) goto L_0x0055
            r3.putExtra(r4, r11)     // Catch:{ Exception -> 0x013a }
            goto L_0x0059
        L_0x0055:
            int r7 = -r11
            r3.putExtra(r6, r7)     // Catch:{ Exception -> 0x013a }
        L_0x0059:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r7 = new androidx.core.content.pm.ShortcutInfoCompat$Builder     // Catch:{ Exception -> 0x013a }
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x013a }
            r7.<init>((android.content.Context) r8, (java.lang.String) r1)     // Catch:{ Exception -> 0x013a }
            if (r14 == 0) goto L_0x0064
            r14 = r12
            goto L_0x0068
        L_0x0064:
            java.lang.String r14 = org.telegram.messenger.UserObject.getFirstName(r13)     // Catch:{ Exception -> 0x013a }
        L_0x0068:
            androidx.core.content.pm.ShortcutInfoCompat$Builder r14 = r7.setShortLabel(r14)     // Catch:{ Exception -> 0x013a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r12 = r14.setLongLabel(r12)     // Catch:{ Exception -> 0x013a }
            android.content.Intent r14 = new android.content.Intent     // Catch:{ Exception -> 0x013a }
            java.lang.String r7 = "android.intent.action.VIEW"
            r14.<init>(r7)     // Catch:{ Exception -> 0x013a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r12 = r12.setIntent(r14)     // Catch:{ Exception -> 0x013a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r12 = r12.setIntent(r3)     // Catch:{ Exception -> 0x013a }
            r14 = 1
            androidx.core.content.pm.ShortcutInfoCompat$Builder r12 = r12.setLongLived(r14)     // Catch:{ Exception -> 0x013a }
            androidx.core.content.LocusIdCompat r3 = new androidx.core.content.LocusIdCompat     // Catch:{ Exception -> 0x013a }
            r3.<init>(r1)     // Catch:{ Exception -> 0x013a }
            androidx.core.content.pm.ShortcutInfoCompat$Builder r12 = r12.setLocusId(r3)     // Catch:{ Exception -> 0x013a }
            if (r15 == 0) goto L_0x00a8
            r12.setPerson(r15)     // Catch:{ Exception -> 0x013a }
            androidx.core.graphics.drawable.IconCompat r3 = r15.getIcon()     // Catch:{ Exception -> 0x013a }
            r12.setIcon(r3)     // Catch:{ Exception -> 0x013a }
            androidx.core.graphics.drawable.IconCompat r3 = r15.getIcon()     // Catch:{ Exception -> 0x013a }
            if (r3 == 0) goto L_0x00a8
            androidx.core.graphics.drawable.IconCompat r15 = r15.getIcon()     // Catch:{ Exception -> 0x013a }
            android.graphics.Bitmap r15 = r15.getBitmap()     // Catch:{ Exception -> 0x013a }
            goto L_0x00a9
        L_0x00a8:
            r15 = r2
        L_0x00a9:
            androidx.core.content.pm.ShortcutInfoCompat r12 = r12.build()     // Catch:{ Exception -> 0x013a }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x013a }
            androidx.core.content.pm.ShortcutManagerCompat.pushDynamicShortcut(r3, r12)     // Catch:{ Exception -> 0x013a }
            r10.setShortcutInfo(r12)     // Catch:{ Exception -> 0x013a }
            android.content.Intent r12 = new android.content.Intent     // Catch:{ Exception -> 0x013a }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x013a }
            java.lang.Class<org.telegram.ui.BubbleActivity> r7 = org.telegram.ui.BubbleActivity.class
            r12.<init>(r3, r7)     // Catch:{ Exception -> 0x013a }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013a }
            r3.<init>()     // Catch:{ Exception -> 0x013a }
            r3.append(r0)     // Catch:{ Exception -> 0x013a }
            double r7 = java.lang.Math.random()     // Catch:{ Exception -> 0x013a }
            r3.append(r7)     // Catch:{ Exception -> 0x013a }
            r3.append(r5)     // Catch:{ Exception -> 0x013a }
            java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x013a }
            r12.setAction(r0)     // Catch:{ Exception -> 0x013a }
            if (r11 <= 0) goto L_0x00dd
            r12.putExtra(r4, r11)     // Catch:{ Exception -> 0x013a }
            goto L_0x00e1
        L_0x00dd:
            int r0 = -r11
            r12.putExtra(r6, r0)     // Catch:{ Exception -> 0x013a }
        L_0x00e1:
            java.lang.String r0 = "currentAccount"
            int r3 = r9.currentAccount     // Catch:{ Exception -> 0x013a }
            r12.putExtra(r0, r3)     // Catch:{ Exception -> 0x013a }
            if (r15 == 0) goto L_0x00ef
            androidx.core.graphics.drawable.IconCompat r13 = androidx.core.graphics.drawable.IconCompat.createWithAdaptiveBitmap(r15)     // Catch:{ Exception -> 0x013a }
            goto L_0x010c
        L_0x00ef:
            if (r13 == 0) goto L_0x0103
            android.content.Context r15 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x013a }
            boolean r13 = r13.bot     // Catch:{ Exception -> 0x013a }
            if (r13 == 0) goto L_0x00fb
            r13 = 2131165288(0x7var_, float:1.7944789E38)
            goto L_0x00fe
        L_0x00fb:
            r13 = 2131165292(0x7var_c, float:1.7944797E38)
        L_0x00fe:
            androidx.core.graphics.drawable.IconCompat r13 = androidx.core.graphics.drawable.IconCompat.createWithResource(r15, r13)     // Catch:{ Exception -> 0x013a }
            goto L_0x010c
        L_0x0103:
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x013a }
            r15 = 2131165290(0x7var_a, float:1.7944793E38)
            androidx.core.graphics.drawable.IconCompat r13 = androidx.core.graphics.drawable.IconCompat.createWithResource(r13, r15)     // Catch:{ Exception -> 0x013a }
        L_0x010c:
            androidx.core.app.NotificationCompat$BubbleMetadata$Builder r15 = new androidx.core.app.NotificationCompat$BubbleMetadata$Builder     // Catch:{ Exception -> 0x013a }
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x013a }
            r3 = 134217728(0x8000000, float:3.85186E-34)
            r4 = 0
            android.app.PendingIntent r12 = android.app.PendingIntent.getActivity(r0, r4, r12, r3)     // Catch:{ Exception -> 0x013a }
            r15.<init>(r12, r13)     // Catch:{ Exception -> 0x013a }
            long r12 = r9.opened_dialog_id     // Catch:{ Exception -> 0x013a }
            long r5 = (long) r11     // Catch:{ Exception -> 0x013a }
            int r11 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1))
            if (r11 != 0) goto L_0x0122
            goto L_0x0123
        L_0x0122:
            r14 = 0
        L_0x0123:
            r15.setSuppressNotification(r14)     // Catch:{ Exception -> 0x013a }
            r15.setAutoExpandBubble(r4)     // Catch:{ Exception -> 0x013a }
            r11 = 1142947840(0x44200000, float:640.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ Exception -> 0x013a }
            r15.setDesiredHeight(r11)     // Catch:{ Exception -> 0x013a }
            androidx.core.app.NotificationCompat$BubbleMetadata r11 = r15.build()     // Catch:{ Exception -> 0x013a }
            r10.setBubbleMetadata(r11)     // Catch:{ Exception -> 0x013a }
            return r1
        L_0x013a:
            r10 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
        L_0x013e:
            return r2
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
                TLRPC$User user = getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId()));
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

    /* JADX WARNING: Removed duplicated region for block: B:166:0x03a5  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x03f2  */
    /* JADX WARNING: Removed duplicated region for block: B:193:0x03f8  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0431  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x04ee A[LOOP:1: B:225:0x04eb->B:227:0x04ee, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0501  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x054f  */
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
            r6 = 2131626603(0x7f0e0a6b, float:1.8880447E38)
            java.lang.String r7 = "NotificationsSilent"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.String r7 = "silent"
        L_0x0094:
            r8 = 0
            goto L_0x00e9
        L_0x0096:
            if (r36 == 0) goto L_0x00bf
            if (r37 == 0) goto L_0x00a0
            r9 = 2131626581(0x7f0e0a55, float:1.8880402E38)
            java.lang.String r14 = "NotificationsInAppDefault"
            goto L_0x00a5
        L_0x00a0:
            r9 = 2131626565(0x7f0e0a45, float:1.888037E38)
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
            r6 = 2131626562(0x7f0e0a42, float:1.8880364E38)
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
            if (r9 == 0) goto L_0x047c
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
            if (r6 == 0) goto L_0x0469
            if (r38 != 0) goto L_0x0458
            if (r12 != 0) goto L_0x0458
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
            if (r3 != 0) goto L_0x0435
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
            r14 = 2131625136(0x7f0e04b0, float:1.8877471E38)
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
            if (r4 == r12) goto L_0x03f2
            if (r37 != 0) goto L_0x03ee
            if (r7 != 0) goto L_0x03ad
            android.content.SharedPreferences$Editor r7 = r5.edit()
        L_0x03ad:
            if (r36 == 0) goto L_0x03d5
            r2 = 2
            if (r0 != r2) goto L_0x03bd
            if (r12 == 0) goto L_0x03b6
            r2 = 0
            goto L_0x03b7
        L_0x03b6:
            r2 = 2
        L_0x03b7:
            java.lang.String r3 = "vibrate_channel"
            r7.putInt(r3, r2)
            goto L_0x03ee
        L_0x03bd:
            if (r0 != 0) goto L_0x03ca
            if (r12 == 0) goto L_0x03c3
            r2 = 0
            goto L_0x03c4
        L_0x03c3:
            r2 = 2
        L_0x03c4:
            java.lang.String r3 = "vibrate_group"
            r7.putInt(r3, r2)
            goto L_0x03ee
        L_0x03ca:
            if (r12 == 0) goto L_0x03ce
            r2 = 0
            goto L_0x03cf
        L_0x03ce:
            r2 = 2
        L_0x03cf:
            java.lang.String r3 = "vibrate_messages"
            r7.putInt(r3, r2)
            goto L_0x03ee
        L_0x03d5:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "vibrate_"
            r2.append(r3)
            r2.append(r9)
            java.lang.String r2 = r2.toString()
            if (r12 == 0) goto L_0x03ea
            r3 = 0
            goto L_0x03eb
        L_0x03ea:
            r3 = 2
        L_0x03eb:
            r7.putInt(r2, r3)
        L_0x03ee:
            r4 = r33
            r2 = 1
            goto L_0x03f6
        L_0x03f2:
            r4 = r33
            r23 = r3
        L_0x03f6:
            if (r6 == r4) goto L_0x042f
            if (r37 != 0) goto L_0x042d
            if (r7 != 0) goto L_0x0400
            android.content.SharedPreferences$Editor r7 = r5.edit()
        L_0x0400:
            if (r36 == 0) goto L_0x0419
            r2 = 2
            if (r0 != r2) goto L_0x040b
            java.lang.String r0 = "ChannelLed"
            r7.putInt(r0, r6)
            goto L_0x042d
        L_0x040b:
            if (r0 != 0) goto L_0x0413
            java.lang.String r0 = "GroupLed"
            r7.putInt(r0, r6)
            goto L_0x042d
        L_0x0413:
            java.lang.String r0 = "MessagesLed"
            r7.putInt(r0, r6)
            goto L_0x042d
        L_0x0419:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "color_"
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            r7.putInt(r0, r6)
        L_0x042d:
            r4 = r6
            r2 = 1
        L_0x042f:
            if (r7 == 0) goto L_0x044c
            r7.commit()
            goto L_0x044c
        L_0x0435:
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
        L_0x044c:
            r0 = r2
            r6 = r22
            r3 = r23
            r7 = r24
            r12 = r25
            r2 = r27
            goto L_0x049a
        L_0x0458:
            r11 = r4
            r24 = r9
            r19 = r10
            r20 = r12
            r25 = r14
            r26 = r15
            r4 = r33
            r9 = r2
            r3 = r32
            goto L_0x0492
        L_0x0469:
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
            goto L_0x049a
        L_0x047c:
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
        L_0x0492:
            r2 = r34
            r7 = r24
            r12 = r25
            r0 = 0
            r6 = 0
        L_0x049a:
            if (r0 == 0) goto L_0x04d9
            if (r6 == 0) goto L_0x04d9
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
            if (r0 == 0) goto L_0x04e6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "change edited channel "
            r0.append(r8)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x04e6
        L_0x04d9:
            r14 = r16
            r13 = r18
            if (r20 != 0) goto L_0x04ea
            if (r6 == 0) goto L_0x04ea
            if (r37 == 0) goto L_0x04ea
            if (r36 != 0) goto L_0x04e6
            goto L_0x04ea
        L_0x04e6:
            r8 = r7
            r16 = r14
            goto L_0x054d
        L_0x04ea:
            r0 = 0
        L_0x04eb:
            int r6 = r3.length
            if (r0 >= r6) goto L_0x04fa
            r16 = r14
            r14 = r3[r0]
            r8.append(r14)
            int r0 = r0 + 1
            r14 = r16
            goto L_0x04eb
        L_0x04fa:
            r16 = r14
            r8.append(r4)
            if (r2 == 0) goto L_0x0508
            java.lang.String r0 = r2.toString()
            r8.append(r0)
        L_0x0508:
            r8.append(r11)
            if (r36 != 0) goto L_0x0514
            if (r26 == 0) goto L_0x0514
            r0 = r19
            r8.append(r0)
        L_0x0514:
            java.lang.String r0 = r8.toString()
            java.lang.String r6 = org.telegram.messenger.Utilities.MD5(r0)
            if (r38 != 0) goto L_0x054c
            if (r7 == 0) goto L_0x054c
            if (r20 != 0) goto L_0x0528
            boolean r0 = r12.equals(r6)
            if (r0 != 0) goto L_0x054c
        L_0x0528:
            android.app.NotificationManager r0 = systemNotificationManager     // Catch:{ Exception -> 0x052e }
            r0.deleteNotificationChannel(r7)     // Catch:{ Exception -> 0x052e }
            goto L_0x0532
        L_0x052e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0532:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x054a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "delete channel by settings change "
            r0.append(r8)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x054a:
            r8 = 0
            goto L_0x054d
        L_0x054c:
            r8 = r7
        L_0x054d:
            if (r8 != 0) goto L_0x0634
            java.lang.String r0 = "_"
            java.lang.String r7 = "channel_"
            if (r36 == 0) goto L_0x0576
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
            goto L_0x0596
        L_0x0576:
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
        L_0x0596:
            r8 = r0
            android.app.NotificationChannel r0 = new android.app.NotificationChannel
            if (r26 == 0) goto L_0x05a5
            r7 = 2131627509(0x7f0e0df5, float:1.8882284E38)
            java.lang.String r9 = "SecretChatName"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            goto L_0x05a7
        L_0x05a5:
            r7 = r31
        L_0x05a7:
            r0.<init>(r8, r7, r11)
            r11 = r17
            r0.setGroup(r11)
            if (r4 == 0) goto L_0x05ba
            r7 = 1
            r0.enableLights(r7)
            r0.setLightColor(r4)
            r4 = 0
            goto L_0x05bf
        L_0x05ba:
            r4 = 0
            r7 = 1
            r0.enableLights(r4)
        L_0x05bf:
            boolean r9 = r1.isEmptyVibration(r3)
            if (r9 != 0) goto L_0x05cf
            r0.enableVibration(r7)
            int r4 = r3.length
            if (r4 <= 0) goto L_0x05d2
            r0.setVibrationPattern(r3)
            goto L_0x05d2
        L_0x05cf:
            r0.enableVibration(r4)
        L_0x05d2:
            android.media.AudioAttributes$Builder r3 = new android.media.AudioAttributes$Builder
            r3.<init>()
            r4 = 4
            r3.setContentType(r4)
            r4 = 5
            r3.setUsage(r4)
            if (r2 == 0) goto L_0x05e9
            android.media.AudioAttributes r3 = r3.build()
            r0.setSound(r2, r3)
            goto L_0x05f1
        L_0x05e9:
            android.media.AudioAttributes r2 = r3.build()
            r3 = 0
            r0.setSound(r3, r2)
        L_0x05f1:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0609
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "create new channel "
            r2.append(r3)
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0609:
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
        L_0x0634:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.validateChannelId(long, java.lang.String, long[], int, android.net.Uri, int, boolean, boolean, boolean, int):java.lang.String");
    }

    /* JADX WARNING: type inference failed for: r2v88 */
    /* JADX WARNING: type inference failed for: r2v89 */
    /* JADX WARNING: type inference failed for: r2v103 */
    /* JADX WARNING: Code restructure failed: missing block: B:345:0x07b9, code lost:
        if (android.os.Build.VERSION.SDK_INT >= 26) goto L_0x07bb;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:378:0x0868 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0203 A[SYNTHETIC, Splitter:B:101:0x0203] */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0276 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x0326 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0404 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0407 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x0420 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0495 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x049b A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04f1 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x053b A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x053f A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x0549 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x054d A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0551 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0572 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x0599  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x05b3 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x05b8 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:244:0x05e9 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:285:0x0668 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x0725 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:316:0x076f  */
    /* JADX WARNING: Removed duplicated region for block: B:321:0x077b  */
    /* JADX WARNING: Removed duplicated region for block: B:344:0x07b3 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x07c2 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:383:0x087a A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0884 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x0889 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0897 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x0908 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0991 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:435:0x09bb A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:436:0x09d4 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00c1 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00ca A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00d1 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00e0 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00e3 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00ed A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00fa A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0110 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0116 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x012c A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0145 A[SYNTHETIC, Splitter:B:80:0x0145] */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0178 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0184 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x019d A[SYNTHETIC, Splitter:B:95:0x019d] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01b5 A[Catch:{ Exception -> 0x0a07 }] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r41) {
        /*
            r40 = this;
            r15 = r40
            java.lang.String r1 = "currentAccount"
            org.telegram.messenger.UserConfig r0 = r40.getUserConfig()
            boolean r0 = r0.isClientActivated()
            if (r0 == 0) goto L_0x0a0c
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r15.pushMessages
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0a0c
            boolean r0 = org.telegram.messenger.SharedConfig.showNotificationsForAllAccounts
            if (r0 != 0) goto L_0x0022
            int r0 = r15.currentAccount
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            if (r0 == r2) goto L_0x0022
            goto L_0x0a0c
        L_0x0022:
            org.telegram.tgnet.ConnectionsManager r0 = r40.getConnectionsManager()     // Catch:{ Exception -> 0x0a07 }
            r0.resumeNetworkMaybe()     // Catch:{ Exception -> 0x0a07 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r15.pushMessages     // Catch:{ Exception -> 0x0a07 }
            r2 = 0
            java.lang.Object r0 = r0.get(r2)     // Catch:{ Exception -> 0x0a07 }
            r3 = r0
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3     // Catch:{ Exception -> 0x0a07 }
            org.telegram.messenger.AccountInstance r0 = r40.getAccountInstance()     // Catch:{ Exception -> 0x0a07 }
            android.content.SharedPreferences r4 = r0.getNotificationsSettings()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r0 = "dismissDate"
            int r0 = r4.getInt(r0, r2)     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner     // Catch:{ Exception -> 0x0a07 }
            int r5 = r5.date     // Catch:{ Exception -> 0x0a07 }
            if (r5 > r0) goto L_0x004b
            r40.dismissNotification()     // Catch:{ Exception -> 0x0a07 }
            return
        L_0x004b:
            long r5 = r3.getDialogId()     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner     // Catch:{ Exception -> 0x0a07 }
            boolean r7 = r7.mentioned     // Catch:{ Exception -> 0x0a07 }
            if (r7 == 0) goto L_0x005b
            int r7 = r3.getFromChatId()     // Catch:{ Exception -> 0x0a07 }
            long r7 = (long) r7     // Catch:{ Exception -> 0x0a07 }
            goto L_0x005c
        L_0x005b:
            r7 = r5
        L_0x005c:
            r3.getId()     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$Message r9 = r3.messageOwner     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$Peer r9 = r9.peer_id     // Catch:{ Exception -> 0x0a07 }
            int r10 = r9.chat_id     // Catch:{ Exception -> 0x0a07 }
            if (r10 == 0) goto L_0x0068
            goto L_0x006a
        L_0x0068:
            int r10 = r9.channel_id     // Catch:{ Exception -> 0x0a07 }
        L_0x006a:
            int r9 = r9.user_id     // Catch:{ Exception -> 0x0a07 }
            boolean r11 = r3.isFromUser()     // Catch:{ Exception -> 0x0a07 }
            if (r11 == 0) goto L_0x0084
            if (r9 == 0) goto L_0x007e
            org.telegram.messenger.UserConfig r11 = r40.getUserConfig()     // Catch:{ Exception -> 0x0a07 }
            int r11 = r11.getClientUserId()     // Catch:{ Exception -> 0x0a07 }
            if (r9 != r11) goto L_0x0084
        L_0x007e:
            org.telegram.tgnet.TLRPC$Message r9 = r3.messageOwner     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$Peer r9 = r9.from_id     // Catch:{ Exception -> 0x0a07 }
            int r9 = r9.user_id     // Catch:{ Exception -> 0x0a07 }
        L_0x0084:
            org.telegram.messenger.MessagesController r11 = r40.getMessagesController()     // Catch:{ Exception -> 0x0a07 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r12)     // Catch:{ Exception -> 0x0a07 }
            if (r10 == 0) goto L_0x00b5
            org.telegram.messenger.MessagesController r14 = r40.getMessagesController()     // Catch:{ Exception -> 0x0a07 }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$Chat r12 = r14.getChat(r12)     // Catch:{ Exception -> 0x0a07 }
            if (r12 != 0) goto L_0x00a9
            boolean r14 = r3.isFcmMessage()     // Catch:{ Exception -> 0x0a07 }
            if (r14 == 0) goto L_0x00a9
            boolean r14 = r3.localChannel     // Catch:{ Exception -> 0x0a07 }
            goto L_0x00b7
        L_0x00a9:
            boolean r14 = org.telegram.messenger.ChatObject.isChannel(r12)     // Catch:{ Exception -> 0x0a07 }
            if (r14 == 0) goto L_0x00b6
            boolean r14 = r12.megagroup     // Catch:{ Exception -> 0x0a07 }
            if (r14 != 0) goto L_0x00b6
            r14 = 1
            goto L_0x00b7
        L_0x00b5:
            r12 = 0
        L_0x00b6:
            r14 = 0
        L_0x00b7:
            int r2 = r15.getNotifyOverride(r4, r7)     // Catch:{ Exception -> 0x0a07 }
            r13 = -1
            r18 = r1
            r1 = 2
            if (r2 != r13) goto L_0x00ca
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r14)     // Catch:{ Exception -> 0x0a07 }
            boolean r2 = r15.isGlobalNotificationsEnabled(r5, r2)     // Catch:{ Exception -> 0x0a07 }
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
            boolean r19 = r3.isFcmMessage()     // Catch:{ Exception -> 0x0a07 }
            if (r19 == 0) goto L_0x00de
            java.lang.String r13 = r3.localName     // Catch:{ Exception -> 0x0a07 }
            goto L_0x00e7
        L_0x00de:
            if (r12 == 0) goto L_0x00e3
            java.lang.String r13 = r12.title     // Catch:{ Exception -> 0x0a07 }
            goto L_0x00e7
        L_0x00e3:
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r11)     // Catch:{ Exception -> 0x0a07 }
        L_0x00e7:
            boolean r20 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a07 }
            if (r20 != 0) goto L_0x00f5
            boolean r20 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a07 }
            if (r20 == 0) goto L_0x00f2
            goto L_0x00f5
        L_0x00f2:
            r20 = 0
            goto L_0x00f7
        L_0x00f5:
            r20 = 1
        L_0x00f7:
            int r1 = (int) r5     // Catch:{ Exception -> 0x0a07 }
            if (r1 == 0) goto L_0x0110
            r21 = r13
            android.util.LongSparseArray<java.lang.Integer> r13 = r15.pushDialogs     // Catch:{ Exception -> 0x0a07 }
            int r13 = r13.size()     // Catch:{ Exception -> 0x0a07 }
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
            r13 = 2131626504(0x7f0e0a08, float:1.8880246E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r13)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0135
        L_0x0122:
            java.lang.String r3 = "NotificationHiddenName"
            r13 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r13)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0135
        L_0x012c:
            java.lang.String r3 = "AppName"
            r13 = 2131624288(0x7f0e0160, float:1.8875751E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r13)     // Catch:{ Exception -> 0x0a07 }
        L_0x0135:
            r20 = r11
            r13 = 0
        L_0x0138:
            int r11 = org.telegram.messenger.UserConfig.getActivatedAccountsCount()     // Catch:{ Exception -> 0x0a07 }
            r23 = r1
            java.lang.String r1 = ""
            r24 = r9
            r9 = 1
            if (r11 <= r9) goto L_0x0178
            android.util.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0a07 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0a07 }
            if (r11 != r9) goto L_0x015a
            org.telegram.messenger.UserConfig r9 = r40.getUserConfig()     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$User r9 = r9.getCurrentUser()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r9 = org.telegram.messenger.UserObject.getFirstName(r9)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0179
        L_0x015a:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r9.<init>()     // Catch:{ Exception -> 0x0a07 }
            org.telegram.messenger.UserConfig r11 = r40.getUserConfig()     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$User r11 = r11.getCurrentUser()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r11)     // Catch:{ Exception -> 0x0a07 }
            r9.append(r11)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r11 = "・"
            r9.append(r11)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0179
        L_0x0178:
            r9 = r1
        L_0x0179:
            android.util.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0a07 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0a07 }
            r25 = r14
            r14 = 1
            if (r11 != r14) goto L_0x0190
            int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a07 }
            r14 = 23
            if (r11 >= r14) goto L_0x018b
            goto L_0x0190
        L_0x018b:
            r27 = r4
            r26 = r10
            goto L_0x01eb
        L_0x0190:
            android.util.LongSparseArray<java.lang.Integer> r11 = r15.pushDialogs     // Catch:{ Exception -> 0x0a07 }
            int r11 = r11.size()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r14 = "NewMessages"
            r26 = r10
            r10 = 1
            if (r11 != r10) goto L_0x01b5
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r10.<init>()     // Catch:{ Exception -> 0x0a07 }
            r10.append(r9)     // Catch:{ Exception -> 0x0a07 }
            int r9 = r15.total_unread_count     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r9 = org.telegram.messenger.LocaleController.formatPluralString(r14, r9)     // Catch:{ Exception -> 0x0a07 }
            r10.append(r9)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r9 = r10.toString()     // Catch:{ Exception -> 0x0a07 }
            r27 = r4
            goto L_0x01eb
        L_0x01b5:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r10.<init>()     // Catch:{ Exception -> 0x0a07 }
            r10.append(r9)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r9 = "NotificationMessagesPeopleDisplayOrder"
            r27 = r4
            r11 = 2
            java.lang.Object[] r4 = new java.lang.Object[r11]     // Catch:{ Exception -> 0x0a07 }
            int r11 = r15.total_unread_count     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11)     // Catch:{ Exception -> 0x0a07 }
            r14 = 0
            r4[r14] = r11     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r11 = "FromChats"
            android.util.LongSparseArray<java.lang.Integer> r14 = r15.pushDialogs     // Catch:{ Exception -> 0x0a07 }
            int r14 = r14.size()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r11 = org.telegram.messenger.LocaleController.formatPluralString(r11, r14)     // Catch:{ Exception -> 0x0a07 }
            r14 = 1
            r4[r14] = r11     // Catch:{ Exception -> 0x0a07 }
            r11 = 2131626555(0x7f0e0a3b, float:1.888035E38)
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r11, r4)     // Catch:{ Exception -> 0x0a07 }
            r10.append(r4)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r4 = r10.toString()     // Catch:{ Exception -> 0x0a07 }
            r9 = r4
        L_0x01eb:
            androidx.core.app.NotificationCompat$Builder r4 = new androidx.core.app.NotificationCompat$Builder     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r10 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            r4.<init>(r10)     // Catch:{ Exception -> 0x0a07 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r10 = r15.pushMessages     // Catch:{ Exception -> 0x0a07 }
            int r10 = r10.size()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r11 = ": "
            java.lang.String r14 = " "
            r28 = r5
            java.lang.String r5 = " @ "
            r6 = 1
            if (r10 != r6) goto L_0x0276
            java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r15.pushMessages     // Catch:{ Exception -> 0x0a07 }
            r10 = 0
            java.lang.Object r0 = r0.get(r10)     // Catch:{ Exception -> 0x0a07 }
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0     // Catch:{ Exception -> 0x0a07 }
            r30 = r7
            boolean[] r7 = new boolean[r6]     // Catch:{ Exception -> 0x0a07 }
            r6 = 0
            java.lang.String r8 = r15.getStringForMessage(r0, r10, r7, r6)     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x0a07 }
            boolean r0 = r0.silent     // Catch:{ Exception -> 0x0a07 }
            if (r8 != 0) goto L_0x021c
            return
        L_0x021c:
            if (r13 == 0) goto L_0x0261
            if (r12 == 0) goto L_0x0234
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r6.<init>()     // Catch:{ Exception -> 0x0a07 }
            r6.append(r5)     // Catch:{ Exception -> 0x0a07 }
            r6.append(r3)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = r6.toString()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = r8.replace(r5, r1)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0262
        L_0x0234:
            r5 = 0
            boolean r6 = r7[r5]     // Catch:{ Exception -> 0x0a07 }
            if (r6 == 0) goto L_0x024d
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r5.<init>()     // Catch:{ Exception -> 0x0a07 }
            r5.append(r3)     // Catch:{ Exception -> 0x0a07 }
            r5.append(r11)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = r8.replace(r5, r1)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0262
        L_0x024d:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r5.<init>()     // Catch:{ Exception -> 0x0a07 }
            r5.append(r3)     // Catch:{ Exception -> 0x0a07 }
            r5.append(r14)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = r8.replace(r5, r1)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0262
        L_0x0261:
            r5 = r8
        L_0x0262:
            r4.setContentText(r5)     // Catch:{ Exception -> 0x0a07 }
            androidx.core.app.NotificationCompat$BigTextStyle r6 = new androidx.core.app.NotificationCompat$BigTextStyle     // Catch:{ Exception -> 0x0a07 }
            r6.<init>()     // Catch:{ Exception -> 0x0a07 }
            androidx.core.app.NotificationCompat$BigTextStyle r5 = r6.bigText(r5)     // Catch:{ Exception -> 0x0a07 }
            r4.setStyle(r5)     // Catch:{ Exception -> 0x0a07 }
            r33 = r2
            r2 = r0
            goto L_0x0324
        L_0x0276:
            r30 = r7
            r4.setContentText(r9)     // Catch:{ Exception -> 0x0a07 }
            androidx.core.app.NotificationCompat$InboxStyle r6 = new androidx.core.app.NotificationCompat$InboxStyle     // Catch:{ Exception -> 0x0a07 }
            r6.<init>()     // Catch:{ Exception -> 0x0a07 }
            r6.setBigContentTitle(r3)     // Catch:{ Exception -> 0x0a07 }
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0a07 }
            int r7 = r7.size()     // Catch:{ Exception -> 0x0a07 }
            r8 = 10
            int r7 = java.lang.Math.min(r8, r7)     // Catch:{ Exception -> 0x0a07 }
            r8 = 1
            boolean[] r10 = new boolean[r8]     // Catch:{ Exception -> 0x0a07 }
            r33 = r2
            r2 = 2
            r8 = 0
            r32 = 0
        L_0x0298:
            if (r8 >= r7) goto L_0x031a
            r34 = r7
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r15.pushMessages     // Catch:{ Exception -> 0x0a07 }
            java.lang.Object r7 = r7.get(r8)     // Catch:{ Exception -> 0x0a07 }
            org.telegram.messenger.MessageObject r7 = (org.telegram.messenger.MessageObject) r7     // Catch:{ Exception -> 0x0a07 }
            r35 = r4
            r37 = r8
            r36 = r9
            r4 = 0
            r9 = 0
            java.lang.String r8 = r15.getStringForMessage(r7, r9, r10, r4)     // Catch:{ Exception -> 0x0a07 }
            if (r8 == 0) goto L_0x0310
            org.telegram.tgnet.TLRPC$Message r4 = r7.messageOwner     // Catch:{ Exception -> 0x0a07 }
            int r7 = r4.date     // Catch:{ Exception -> 0x0a07 }
            if (r7 > r0) goto L_0x02b9
            goto L_0x0310
        L_0x02b9:
            r7 = 2
            if (r2 != r7) goto L_0x02c0
            boolean r2 = r4.silent     // Catch:{ Exception -> 0x0a07 }
            r32 = r8
        L_0x02c0:
            android.util.LongSparseArray<java.lang.Integer> r4 = r15.pushDialogs     // Catch:{ Exception -> 0x0a07 }
            int r4 = r4.size()     // Catch:{ Exception -> 0x0a07 }
            r7 = 1
            if (r4 != r7) goto L_0x030d
            if (r13 == 0) goto L_0x030d
            if (r12 == 0) goto L_0x02e1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r4.<init>()     // Catch:{ Exception -> 0x0a07 }
            r4.append(r5)     // Catch:{ Exception -> 0x0a07 }
            r4.append(r3)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r8 = r8.replace(r4, r1)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x030d
        L_0x02e1:
            r4 = 0
            boolean r7 = r10[r4]     // Catch:{ Exception -> 0x0a07 }
            if (r7 == 0) goto L_0x02fa
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r4.<init>()     // Catch:{ Exception -> 0x0a07 }
            r4.append(r3)     // Catch:{ Exception -> 0x0a07 }
            r4.append(r11)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r8 = r8.replace(r4, r1)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x030d
        L_0x02fa:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r4.<init>()     // Catch:{ Exception -> 0x0a07 }
            r4.append(r3)     // Catch:{ Exception -> 0x0a07 }
            r4.append(r14)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r8 = r8.replace(r4, r1)     // Catch:{ Exception -> 0x0a07 }
        L_0x030d:
            r6.addLine(r8)     // Catch:{ Exception -> 0x0a07 }
        L_0x0310:
            int r8 = r37 + 1
            r7 = r34
            r4 = r35
            r9 = r36
            goto L_0x0298
        L_0x031a:
            r35 = r4
            r6.setSummaryText(r9)     // Catch:{ Exception -> 0x0a07 }
            r4.setStyle(r6)     // Catch:{ Exception -> 0x0a07 }
            r8 = r32
        L_0x0324:
            if (r41 == 0) goto L_0x0338
            if (r33 == 0) goto L_0x0338
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()     // Catch:{ Exception -> 0x0a07 }
            boolean r0 = r0.isRecordingAudio()     // Catch:{ Exception -> 0x0a07 }
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
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r10.<init>()     // Catch:{ Exception -> 0x0a07 }
            r10.append(r5)     // Catch:{ Exception -> 0x0a07 }
            r13 = r28
            r10.append(r13)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0a07 }
            r11 = r27
            r6 = 0
            boolean r7 = r11.getBoolean(r10, r6)     // Catch:{ Exception -> 0x0a07 }
            if (r7 == 0) goto L_0x038b
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r7.<init>()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r10 = "smart_max_count_"
            r7.append(r10)     // Catch:{ Exception -> 0x0a07 }
            r7.append(r13)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a07 }
            r10 = 2
            int r7 = r11.getInt(r7, r10)     // Catch:{ Exception -> 0x0a07 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r10.<init>()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r6 = "smart_delay_"
            r10.append(r6)     // Catch:{ Exception -> 0x0a07 }
            r10.append(r13)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r6 = r10.toString()     // Catch:{ Exception -> 0x0a07 }
            r10 = 180(0xb4, float:2.52E-43)
            int r6 = r11.getInt(r6, r10)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0390
        L_0x038b:
            r10 = 180(0xb4, float:2.52E-43)
            r6 = 180(0xb4, float:2.52E-43)
            r7 = 2
        L_0x0390:
            if (r7 == 0) goto L_0x03ed
            android.util.LongSparseArray<android.graphics.Point> r10 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a07 }
            java.lang.Object r10 = r10.get(r13)     // Catch:{ Exception -> 0x0a07 }
            android.graphics.Point r10 = (android.graphics.Point) r10     // Catch:{ Exception -> 0x0a07 }
            if (r10 != 0) goto L_0x03b3
            android.graphics.Point r6 = new android.graphics.Point     // Catch:{ Exception -> 0x0a07 }
            long r29 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a07 }
            r36 = r9
            r27 = 1000(0x3e8, double:4.94E-321)
            long r9 = r29 / r27
            int r7 = (int) r9     // Catch:{ Exception -> 0x0a07 }
            r9 = 1
            r6.<init>(r9, r7)     // Catch:{ Exception -> 0x0a07 }
            android.util.LongSparseArray<android.graphics.Point> r7 = r15.smartNotificationsDialogs     // Catch:{ Exception -> 0x0a07 }
            r7.put(r13, r6)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x03f8
        L_0x03b3:
            r36 = r9
            int r9 = r10.y     // Catch:{ Exception -> 0x0a07 }
            int r9 = r9 + r6
            r29 = r0
            r6 = r1
            long r0 = (long) r9     // Catch:{ Exception -> 0x0a07 }
            long r30 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a07 }
            r27 = 1000(0x3e8, double:4.94E-321)
            long r30 = r30 / r27
            int r9 = (r0 > r30 ? 1 : (r0 == r30 ? 0 : -1))
            if (r9 >= 0) goto L_0x03d5
            long r0 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a07 }
            long r0 = r0 / r27
            int r1 = (int) r0     // Catch:{ Exception -> 0x0a07 }
            r7 = 1
            r10.set(r7, r1)     // Catch:{ Exception -> 0x0a07 }
            r1 = r6
            goto L_0x03fa
        L_0x03d5:
            int r0 = r10.x     // Catch:{ Exception -> 0x0a07 }
            if (r0 >= r7) goto L_0x03e9
            r1 = 1
            int r0 = r0 + r1
            long r30 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0a07 }
            r1 = r6
            r27 = 1000(0x3e8, double:4.94E-321)
            long r6 = r30 / r27
            int r7 = (int) r6     // Catch:{ Exception -> 0x0a07 }
            r10.set(r0, r7)     // Catch:{ Exception -> 0x0a07 }
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
            android.net.Uri r0 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r6 = r0.getPath()     // Catch:{ Exception -> 0x0a07 }
            boolean r0 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused     // Catch:{ Exception -> 0x0a07 }
            if (r0 != 0) goto L_0x0407
            r30 = 1
            goto L_0x0409
        L_0x0407:
            r30 = 0
        L_0x0409:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r0.<init>()     // Catch:{ Exception -> 0x0a07 }
            r0.append(r5)     // Catch:{ Exception -> 0x0a07 }
            r0.append(r13)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a07 }
            r5 = 0
            boolean r0 = r11.getBoolean(r0, r5)     // Catch:{ Exception -> 0x0a07 }
            r5 = 3
            if (r0 == 0) goto L_0x0495
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r0.<init>()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r7 = "vibrate_"
            r0.append(r7)     // Catch:{ Exception -> 0x0a07 }
            r0.append(r13)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a07 }
            r7 = 0
            int r0 = r11.getInt(r0, r7)     // Catch:{ Exception -> 0x0a07 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r7.<init>()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r9 = "priority_"
            r7.append(r9)     // Catch:{ Exception -> 0x0a07 }
            r7.append(r13)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0a07 }
            int r7 = r11.getInt(r7, r5)     // Catch:{ Exception -> 0x0a07 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r9.<init>()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r10 = "sound_path_"
            r9.append(r10)     // Catch:{ Exception -> 0x0a07 }
            r9.append(r13)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0a07 }
            r10 = 0
            java.lang.String r9 = r11.getString(r9, r10)     // Catch:{ Exception -> 0x0a07 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r10.<init>()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = "color_"
            r10.append(r5)     // Catch:{ Exception -> 0x0a07 }
            r10.append(r13)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = r10.toString()     // Catch:{ Exception -> 0x0a07 }
            boolean r5 = r11.contains(r5)     // Catch:{ Exception -> 0x0a07 }
            if (r5 == 0) goto L_0x0493
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r5.<init>()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r10 = "color_"
            r5.append(r10)     // Catch:{ Exception -> 0x0a07 }
            r5.append(r13)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a07 }
            r10 = 0
            int r5 = r11.getInt(r5, r10)     // Catch:{ Exception -> 0x0a07 }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0499
        L_0x0493:
            r5 = 0
            goto L_0x0499
        L_0x0495:
            r0 = 0
            r5 = 0
            r7 = 3
            r9 = 0
        L_0x0499:
            if (r26 == 0) goto L_0x04f1
            if (r25 == 0) goto L_0x04c7
            java.lang.String r10 = "ChannelSoundPath"
            java.lang.String r10 = r11.getString(r10, r6)     // Catch:{ Exception -> 0x0a07 }
            r32 = r10
            java.lang.String r10 = "vibrate_channel"
            r33 = r1
            r1 = 0
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r1 = "priority_channel"
            r34 = r10
            r10 = 1
            int r1 = r11.getInt(r1, r10)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r10 = "ChannelLed"
            r35 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a07 }
            r1 = r32
            r25 = 2
            goto L_0x051c
        L_0x04c7:
            r33 = r1
            java.lang.String r1 = "GroupSoundPath"
            java.lang.String r1 = r11.getString(r1, r6)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r10 = "vibrate_group"
            r32 = r1
            r1 = 0
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r1 = "priority_group"
            r34 = r10
            r10 = 1
            int r1 = r11.getInt(r1, r10)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r10 = "GroupLed"
            r35 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a07 }
            r1 = r32
            r25 = 0
            goto L_0x051c
        L_0x04f1:
            r33 = r1
            if (r24 == 0) goto L_0x0527
            java.lang.String r1 = "GlobalSoundPath"
            java.lang.String r1 = r11.getString(r1, r6)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r10 = "vibrate_messages"
            r32 = r1
            r1 = 0
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r1 = "priority_messages"
            r34 = r10
            r10 = 1
            int r1 = r11.getInt(r1, r10)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r10 = "MessagesLed"
            r35 = r1
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            int r10 = r11.getInt(r10, r1)     // Catch:{ Exception -> 0x0a07 }
            r1 = r32
            r25 = 1
        L_0x051c:
            r32 = r6
            r6 = r34
            r34 = r8
            r8 = r35
            r35 = r2
            goto L_0x0538
        L_0x0527:
            r1 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r35 = r2
            r32 = r6
            r34 = r8
            r1 = 0
            r6 = 0
            r8 = 0
            r10 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            r25 = 1
        L_0x0538:
            r2 = 4
            if (r6 != r2) goto L_0x053f
            r6 = 0
            r37 = 1
            goto L_0x0541
        L_0x053f:
            r37 = 0
        L_0x0541:
            if (r9 == 0) goto L_0x054d
            boolean r38 = android.text.TextUtils.equals(r1, r9)     // Catch:{ Exception -> 0x0a07 }
            if (r38 != 0) goto L_0x054d
            r1 = r9
            r2 = 3
            r9 = 0
            goto L_0x054f
        L_0x054d:
            r2 = 3
            r9 = 1
        L_0x054f:
            if (r7 == r2) goto L_0x0555
            if (r8 == r7) goto L_0x0555
            r9 = 0
            goto L_0x0556
        L_0x0555:
            r7 = r8
        L_0x0556:
            if (r5 == 0) goto L_0x0563
            int r2 = r5.intValue()     // Catch:{ Exception -> 0x0a07 }
            if (r2 == r10) goto L_0x0563
            int r10 = r5.intValue()     // Catch:{ Exception -> 0x0a07 }
            r9 = 0
        L_0x0563:
            if (r0 == 0) goto L_0x056d
            r2 = 4
            if (r0 == r2) goto L_0x056d
            if (r0 == r6) goto L_0x056d
            r39 = 0
            goto L_0x0570
        L_0x056d:
            r0 = r6
            r39 = r9
        L_0x0570:
            if (r30 == 0) goto L_0x0599
            java.lang.String r2 = "EnableInAppSounds"
            r5 = 1
            boolean r2 = r11.getBoolean(r2, r5)     // Catch:{ Exception -> 0x0a07 }
            if (r2 != 0) goto L_0x057d
            r6 = 0
            goto L_0x057e
        L_0x057d:
            r6 = r1
        L_0x057e:
            java.lang.String r1 = "EnableInAppVibrate"
            boolean r1 = r11.getBoolean(r1, r5)     // Catch:{ Exception -> 0x0a07 }
            if (r1 != 0) goto L_0x0587
            r0 = 2
        L_0x0587:
            java.lang.String r1 = "EnableInAppPriority"
            r2 = 0
            boolean r1 = r11.getBoolean(r1, r2)     // Catch:{ Exception -> 0x0a07 }
            if (r1 != 0) goto L_0x0593
            r1 = r0
            r7 = 0
            goto L_0x059b
        L_0x0593:
            r1 = 2
            if (r7 != r1) goto L_0x059a
            r1 = r0
            r7 = 1
            goto L_0x059b
        L_0x0599:
            r6 = r1
        L_0x059a:
            r1 = r0
        L_0x059b:
            if (r37 == 0) goto L_0x05b1
            r2 = 2
            if (r1 == r2) goto L_0x05b1
            android.media.AudioManager r0 = audioManager     // Catch:{ Exception -> 0x05ad }
            int r0 = r0.getRingerMode()     // Catch:{ Exception -> 0x05ad }
            if (r0 == 0) goto L_0x05b1
            r2 = 1
            if (r0 == r2) goto L_0x05b1
            r1 = 2
            goto L_0x05b1
        L_0x05ad:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0a07 }
        L_0x05b1:
            if (r29 == 0) goto L_0x05b8
            r1 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            goto L_0x05b9
        L_0x05b8:
            r8 = r10
        L_0x05b9:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            java.lang.Class<org.telegram.ui.LaunchActivity> r5 = org.telegram.ui.LaunchActivity.class
            r0.<init>(r2, r5)     // Catch:{ Exception -> 0x0a07 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r2.<init>()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = "com.tmessages.openchat"
            r2.append(r5)     // Catch:{ Exception -> 0x0a07 }
            double r9 = java.lang.Math.random()     // Catch:{ Exception -> 0x0a07 }
            r2.append(r9)     // Catch:{ Exception -> 0x0a07 }
            r5 = 2147483647(0x7fffffff, float:NaN)
            r2.append(r5)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0a07 }
            r0.setAction(r2)     // Catch:{ Exception -> 0x0a07 }
            r2 = 67108864(0x4000000, float:1.5046328E-36)
            r0.setFlags(r2)     // Catch:{ Exception -> 0x0a07 }
            r9 = 0
            if (r23 == 0) goto L_0x0668
            android.util.LongSparseArray<java.lang.Integer> r2 = r15.pushDialogs     // Catch:{ Exception -> 0x0a07 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0a07 }
            r5 = 1
            if (r2 != r5) goto L_0x0605
            if (r26 == 0) goto L_0x05fc
            java.lang.String r2 = "chatId"
            r5 = r26
            r0.putExtra(r2, r5)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0605
        L_0x05fc:
            if (r24 == 0) goto L_0x0605
            java.lang.String r2 = "userId"
            r5 = r24
            r0.putExtra(r2, r5)     // Catch:{ Exception -> 0x0a07 }
        L_0x0605:
            boolean r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a07 }
            if (r2 != 0) goto L_0x0662
            boolean r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a07 }
            if (r2 == 0) goto L_0x0610
            goto L_0x0662
        L_0x0610:
            android.util.LongSparseArray<java.lang.Integer> r2 = r15.pushDialogs     // Catch:{ Exception -> 0x0a07 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0a07 }
            r5 = 1
            if (r2 != r5) goto L_0x065a
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a07 }
            r5 = 28
            if (r2 >= r5) goto L_0x065a
            if (r12 == 0) goto L_0x063c
            org.telegram.tgnet.TLRPC$ChatPhoto r2 = r12.photo     // Catch:{ Exception -> 0x0a07 }
            if (r2 == 0) goto L_0x065a
            org.telegram.tgnet.TLRPC$FileLocation r2 = r2.photo_small     // Catch:{ Exception -> 0x0a07 }
            if (r2 == 0) goto L_0x065a
            r23 = r6
            long r5 = r2.volume_id     // Catch:{ Exception -> 0x0a07 }
            int r24 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r24 == 0) goto L_0x065c
            int r5 = r2.local_id     // Catch:{ Exception -> 0x0a07 }
            if (r5 == 0) goto L_0x065c
            r6 = r2
            r5 = r7
            r2 = r20
            r20 = r8
            goto L_0x0689
        L_0x063c:
            r23 = r6
            if (r20 == 0) goto L_0x065c
            r2 = r20
            org.telegram.tgnet.TLRPC$UserProfilePhoto r5 = r2.photo     // Catch:{ Exception -> 0x0a07 }
            if (r5 == 0) goto L_0x0658
            org.telegram.tgnet.TLRPC$FileLocation r6 = r5.photo_small     // Catch:{ Exception -> 0x0a07 }
            if (r6 == 0) goto L_0x0658
            r5 = r7
            r20 = r8
            long r7 = r6.volume_id     // Catch:{ Exception -> 0x0a07 }
            int r24 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r24 == 0) goto L_0x0688
            int r7 = r6.local_id     // Catch:{ Exception -> 0x0a07 }
            if (r7 == 0) goto L_0x0688
            goto L_0x0689
        L_0x0658:
            r5 = r7
            goto L_0x065f
        L_0x065a:
            r23 = r6
        L_0x065c:
            r5 = r7
            r2 = r20
        L_0x065f:
            r20 = r8
            goto L_0x0688
        L_0x0662:
            r23 = r6
            r5 = r7
            r2 = r20
            goto L_0x065f
        L_0x0668:
            r23 = r6
            r5 = r7
            r2 = r20
            r20 = r8
            android.util.LongSparseArray<java.lang.Integer> r6 = r15.pushDialogs     // Catch:{ Exception -> 0x0a07 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x0a07 }
            r7 = 1
            if (r6 != r7) goto L_0x0688
            long r6 = globalSecretChatId     // Catch:{ Exception -> 0x0a07 }
            int r8 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x0688
            java.lang.String r6 = "encId"
            r7 = 32
            long r7 = r13 >> r7
            int r8 = (int) r7     // Catch:{ Exception -> 0x0a07 }
            r0.putExtra(r6, r8)     // Catch:{ Exception -> 0x0a07 }
        L_0x0688:
            r6 = 0
        L_0x0689:
            int r7 = r15.currentAccount     // Catch:{ Exception -> 0x0a07 }
            r8 = r18
            r0.putExtra(r8, r7)     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            r9 = 1073741824(0x40000000, float:2.0)
            r10 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r7, r10, r0, r9)     // Catch:{ Exception -> 0x0a07 }
            androidx.core.app.NotificationCompat$Builder r3 = r4.setContentTitle(r3)     // Catch:{ Exception -> 0x0a07 }
            r7 = 2131165887(0x7var_bf, float:1.7946004E38)
            androidx.core.app.NotificationCompat$Builder r3 = r3.setSmallIcon(r7)     // Catch:{ Exception -> 0x0a07 }
            r7 = 1
            androidx.core.app.NotificationCompat$Builder r3 = r3.setAutoCancel(r7)     // Catch:{ Exception -> 0x0a07 }
            int r7 = r15.total_unread_count     // Catch:{ Exception -> 0x0a07 }
            androidx.core.app.NotificationCompat$Builder r3 = r3.setNumber(r7)     // Catch:{ Exception -> 0x0a07 }
            androidx.core.app.NotificationCompat$Builder r0 = r3.setContentIntent(r0)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r3 = r15.notificationGroup     // Catch:{ Exception -> 0x0a07 }
            androidx.core.app.NotificationCompat$Builder r0 = r0.setGroup(r3)     // Catch:{ Exception -> 0x0a07 }
            r3 = 1
            androidx.core.app.NotificationCompat$Builder r0 = r0.setGroupSummary(r3)     // Catch:{ Exception -> 0x0a07 }
            androidx.core.app.NotificationCompat$Builder r0 = r0.setShowWhen(r3)     // Catch:{ Exception -> 0x0a07 }
            r3 = r22
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner     // Catch:{ Exception -> 0x0a07 }
            int r7 = r7.date     // Catch:{ Exception -> 0x0a07 }
            long r9 = (long) r7     // Catch:{ Exception -> 0x0a07 }
            r27 = 1000(0x3e8, double:4.94E-321)
            long r9 = r9 * r27
            androidx.core.app.NotificationCompat$Builder r0 = r0.setWhen(r9)     // Catch:{ Exception -> 0x0a07 }
            r7 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            r0.setColor(r7)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r0 = "msg"
            r4.setCategory(r0)     // Catch:{ Exception -> 0x0a07 }
            if (r12 != 0) goto L_0x0700
            if (r2 == 0) goto L_0x0700
            java.lang.String r0 = r2.phone     // Catch:{ Exception -> 0x0a07 }
            if (r0 == 0) goto L_0x0700
            int r0 = r0.length()     // Catch:{ Exception -> 0x0a07 }
            if (r0 <= 0) goto L_0x0700
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r0.<init>()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r7 = "tel:+"
            r0.append(r7)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r2 = r2.phone     // Catch:{ Exception -> 0x0a07 }
            r0.append(r2)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0a07 }
            r4.addPerson(r0)     // Catch:{ Exception -> 0x0a07 }
        L_0x0700:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r7 = org.telegram.messenger.NotificationDismissReceiver.class
            r0.<init>(r2, r7)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r2 = "messageDate"
            org.telegram.tgnet.TLRPC$Message r7 = r3.messageOwner     // Catch:{ Exception -> 0x0a07 }
            int r7 = r7.date     // Catch:{ Exception -> 0x0a07 }
            r0.putExtra(r2, r7)     // Catch:{ Exception -> 0x0a07 }
            int r2 = r15.currentAccount     // Catch:{ Exception -> 0x0a07 }
            r0.putExtra(r8, r2)     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            r7 = 134217728(0x8000000, float:3.85186E-34)
            r9 = 1
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r2, r9, r0, r7)     // Catch:{ Exception -> 0x0a07 }
            r4.setDeleteIntent(r0)     // Catch:{ Exception -> 0x0a07 }
            if (r6 == 0) goto L_0x076f
            org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.getInstance()     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r2 = "50_50"
            r9 = 0
            android.graphics.drawable.BitmapDrawable r0 = r0.getImageFromMemory(r6, r9, r2)     // Catch:{ Exception -> 0x0a07 }
            if (r0 == 0) goto L_0x073a
            android.graphics.Bitmap r0 = r0.getBitmap()     // Catch:{ Exception -> 0x0a07 }
            r4.setLargeIcon(r0)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0770
        L_0x073a:
            r2 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r2)     // Catch:{ all -> 0x076d }
            boolean r2 = r0.exists()     // Catch:{ all -> 0x076d }
            if (r2 == 0) goto L_0x0770
            r2 = 1126170624(0x43200000, float:160.0)
            r6 = 1112014848(0x42480000, float:50.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)     // Catch:{ all -> 0x076d }
            float r6 = (float) r6     // Catch:{ all -> 0x076d }
            float r2 = r2 / r6
            android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x076d }
            r6.<init>()     // Catch:{ all -> 0x076d }
            r10 = 1065353216(0x3var_, float:1.0)
            int r10 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r10 >= 0) goto L_0x075c
            r2 = 1
            goto L_0x075d
        L_0x075c:
            int r2 = (int) r2     // Catch:{ all -> 0x076d }
        L_0x075d:
            r6.inSampleSize = r2     // Catch:{ all -> 0x076d }
            java.lang.String r0 = r0.getAbsolutePath()     // Catch:{ all -> 0x076d }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r6)     // Catch:{ all -> 0x076d }
            if (r0 == 0) goto L_0x0770
            r4.setLargeIcon(r0)     // Catch:{ all -> 0x076d }
            goto L_0x0770
        L_0x076d:
            goto L_0x0770
        L_0x076f:
            r9 = 0
        L_0x0770:
            r0 = 5
            r2 = 26
            r6 = r35
            if (r41 == 0) goto L_0x07b3
            r10 = 1
            if (r6 != r10) goto L_0x077b
            goto L_0x07b3
        L_0x077b:
            if (r5 != 0) goto L_0x0788
            r10 = 0
            r4.setPriority(r10)     // Catch:{ Exception -> 0x0a07 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a07 }
            if (r5 < r2) goto L_0x07be
            r5 = 1
            r10 = 3
            goto L_0x07c0
        L_0x0788:
            r10 = 1
            if (r5 == r10) goto L_0x07a8
            r10 = 2
            if (r5 != r10) goto L_0x078f
            goto L_0x07a8
        L_0x078f:
            r10 = 4
            if (r5 != r10) goto L_0x079d
            r5 = -2
            r4.setPriority(r5)     // Catch:{ Exception -> 0x0a07 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a07 }
            if (r5 < r2) goto L_0x07be
            r5 = 1
            r10 = 1
            goto L_0x07c0
        L_0x079d:
            if (r5 != r0) goto L_0x07be
            r5 = -1
            r4.setPriority(r5)     // Catch:{ Exception -> 0x0a07 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a07 }
            if (r5 < r2) goto L_0x07be
            goto L_0x07bb
        L_0x07a8:
            r5 = 1
            r4.setPriority(r5)     // Catch:{ Exception -> 0x0a07 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a07 }
            if (r5 < r2) goto L_0x07be
            r5 = 1
            r10 = 4
            goto L_0x07c0
        L_0x07b3:
            r5 = -1
            r4.setPriority(r5)     // Catch:{ Exception -> 0x0a07 }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a07 }
            if (r5 < r2) goto L_0x07be
        L_0x07bb:
            r5 = 1
            r10 = 2
            goto L_0x07c0
        L_0x07be:
            r5 = 1
            r10 = 0
        L_0x07c0:
            if (r6 == r5) goto L_0x08db
            if (r29 != 0) goto L_0x08db
            if (r30 == 0) goto L_0x07ce
            java.lang.String r6 = "EnableInAppPreview"
            boolean r6 = r11.getBoolean(r6, r5)     // Catch:{ Exception -> 0x0a07 }
            if (r6 == 0) goto L_0x0803
        L_0x07ce:
            int r5 = r34.length()     // Catch:{ Exception -> 0x0a07 }
            r6 = 100
            if (r5 <= r6) goto L_0x07fd
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0a07 }
            r5.<init>()     // Catch:{ Exception -> 0x0a07 }
            r6 = 100
            r11 = r34
            r12 = 0
            java.lang.String r6 = r11.substring(r12, r6)     // Catch:{ Exception -> 0x0a07 }
            r11 = 32
            r12 = 10
            java.lang.String r6 = r6.replace(r12, r11)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r6 = r6.trim()     // Catch:{ Exception -> 0x0a07 }
            r5.append(r6)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r6 = "..."
            r5.append(r6)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0800
        L_0x07fd:
            r11 = r34
            r5 = r11
        L_0x0800:
            r4.setTicker(r5)     // Catch:{ Exception -> 0x0a07 }
        L_0x0803:
            if (r23 == 0) goto L_0x0877
            java.lang.String r5 = "NoSound"
            r6 = r23
            boolean r5 = r6.equals(r5)     // Catch:{ Exception -> 0x0a07 }
            if (r5 != 0) goto L_0x0877
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a07 }
            if (r5 < r2) goto L_0x0823
            r2 = r32
            boolean r0 = r6.equals(r2)     // Catch:{ Exception -> 0x0a07 }
            if (r0 == 0) goto L_0x081e
            android.net.Uri r6 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0878
        L_0x081e:
            android.net.Uri r6 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0878
        L_0x0823:
            r2 = r32
            boolean r2 = r6.equals(r2)     // Catch:{ Exception -> 0x0a07 }
            if (r2 == 0) goto L_0x0831
            android.net.Uri r2 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI     // Catch:{ Exception -> 0x0a07 }
            r4.setSound(r2, r0)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0877
        L_0x0831:
            r2 = 24
            if (r5 < r2) goto L_0x0870
            java.lang.String r2 = "file://"
            boolean r2 = r6.startsWith(r2)     // Catch:{ Exception -> 0x0a07 }
            if (r2 == 0) goto L_0x0870
            android.net.Uri r2 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a07 }
            boolean r2 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r2)     // Catch:{ Exception -> 0x0a07 }
            if (r2 != 0) goto L_0x0870
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0868 }
            java.lang.String r5 = "org.telegram.messenger.beta.provider"
            java.io.File r11 = new java.io.File     // Catch:{ Exception -> 0x0868 }
            java.lang.String r12 = "file://"
            r9 = r33
            java.lang.String r9 = r6.replace(r12, r9)     // Catch:{ Exception -> 0x0868 }
            r11.<init>(r9)     // Catch:{ Exception -> 0x0868 }
            android.net.Uri r2 = androidx.core.content.FileProvider.getUriForFile(r2, r5, r11)     // Catch:{ Exception -> 0x0868 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0868 }
            java.lang.String r9 = "com.android.systemui"
            r11 = 1
            r5.grantUriPermission(r9, r2, r11)     // Catch:{ Exception -> 0x0868 }
            r4.setSound(r2, r0)     // Catch:{ Exception -> 0x0868 }
            goto L_0x0877
        L_0x0868:
            android.net.Uri r2 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a07 }
            r4.setSound(r2, r0)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0877
        L_0x0870:
            android.net.Uri r2 = android.net.Uri.parse(r6)     // Catch:{ Exception -> 0x0a07 }
            r4.setSound(r2, r0)     // Catch:{ Exception -> 0x0a07 }
        L_0x0877:
            r6 = 0
        L_0x0878:
            if (r20 == 0) goto L_0x0884
            r0 = 1000(0x3e8, float:1.401E-42)
            r2 = 1000(0x3e8, float:1.401E-42)
            r9 = r20
            r4.setLights(r9, r0, r2)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0886
        L_0x0884:
            r9 = r20
        L_0x0886:
            r2 = 2
            if (r1 != r2) goto L_0x0897
            long[] r0 = new long[r2]     // Catch:{ Exception -> 0x0a07 }
            r1 = 0
            r5 = 0
            r0[r5] = r1     // Catch:{ Exception -> 0x0a07 }
            r5 = 1
            r0[r5] = r1     // Catch:{ Exception -> 0x0a07 }
            r4.setVibrate(r0)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x08d8
        L_0x0897:
            r2 = 1
            if (r1 != r2) goto L_0x08b2
            r5 = 4
            long[] r0 = new long[r5]     // Catch:{ Exception -> 0x0a07 }
            r1 = 0
            r11 = 0
            r0[r1] = r11     // Catch:{ Exception -> 0x0a07 }
            r17 = 100
            r0[r2] = r17     // Catch:{ Exception -> 0x0a07 }
            r1 = 2
            r0[r1] = r11     // Catch:{ Exception -> 0x0a07 }
            r1 = 100
            r5 = 3
            r0[r5] = r1     // Catch:{ Exception -> 0x0a07 }
            r4.setVibrate(r0)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x08d8
        L_0x08b2:
            if (r1 == 0) goto L_0x08d1
            r2 = 4
            if (r1 != r2) goto L_0x08b8
            goto L_0x08d1
        L_0x08b8:
            r2 = 3
            if (r1 != r2) goto L_0x08cc
            r1 = 2
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a07 }
            r1 = 0
            r5 = 0
            r0[r5] = r1     // Catch:{ Exception -> 0x0a07 }
            r1 = 1000(0x3e8, double:4.94E-321)
            r5 = 1
            r0[r5] = r1     // Catch:{ Exception -> 0x0a07 }
            r4.setVibrate(r0)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x08d8
        L_0x08cc:
            r16 = r6
            r0 = 0
        L_0x08cf:
            r5 = 1
            goto L_0x08ed
        L_0x08d1:
            r1 = 2
            r4.setDefaults(r1)     // Catch:{ Exception -> 0x0a07 }
            r1 = 0
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a07 }
        L_0x08d8:
            r16 = r6
            goto L_0x08cf
        L_0x08db:
            r9 = r20
            r1 = 2
            long[] r0 = new long[r1]     // Catch:{ Exception -> 0x0a07 }
            r1 = 0
            r5 = 0
            r0[r5] = r1     // Catch:{ Exception -> 0x0a07 }
            r5 = 1
            r0[r5] = r1     // Catch:{ Exception -> 0x0a07 }
            r4.setVibrate(r0)     // Catch:{ Exception -> 0x0a07 }
            r16 = 0
        L_0x08ed:
            boolean r1 = org.telegram.messenger.AndroidUtilities.needShowPasscode()     // Catch:{ Exception -> 0x0a07 }
            if (r1 != 0) goto L_0x0991
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter     // Catch:{ Exception -> 0x0a07 }
            if (r1 != 0) goto L_0x0991
            long r1 = r3.getDialogId()     // Catch:{ Exception -> 0x0a07 }
            r11 = 777000(0xbdb28, double:3.83889E-318)
            int r6 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r6 != 0) goto L_0x0991
            org.telegram.tgnet.TLRPC$Message r1 = r3.messageOwner     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$ReplyMarkup r1 = r1.reply_markup     // Catch:{ Exception -> 0x0a07 }
            if (r1 == 0) goto L_0x0991
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r1 = r1.rows     // Catch:{ Exception -> 0x0a07 }
            int r2 = r1.size()     // Catch:{ Exception -> 0x0a07 }
            r6 = 0
            r11 = 0
        L_0x0910:
            if (r6 >= r2) goto L_0x098f
            java.lang.Object r12 = r1.get(r6)     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r12 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r12     // Catch:{ Exception -> 0x0a07 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r5 = r12.buttons     // Catch:{ Exception -> 0x0a07 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0a07 }
            r7 = 0
        L_0x091f:
            if (r7 >= r5) goto L_0x0980
            r19 = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r1 = r12.buttons     // Catch:{ Exception -> 0x0a07 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ Exception -> 0x0a07 }
            org.telegram.tgnet.TLRPC$KeyboardButton r1 = (org.telegram.tgnet.TLRPC$KeyboardButton) r1     // Catch:{ Exception -> 0x0a07 }
            r41 = r2
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback     // Catch:{ Exception -> 0x0a07 }
            if (r2 == 0) goto L_0x0970
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            r20 = r5
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r5 = org.telegram.messenger.NotificationCallbackReceiver.class
            r2.<init>(r11, r5)     // Catch:{ Exception -> 0x0a07 }
            int r5 = r15.currentAccount     // Catch:{ Exception -> 0x0a07 }
            r2.putExtra(r8, r5)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r5 = "did"
            r2.putExtra(r5, r13)     // Catch:{ Exception -> 0x0a07 }
            byte[] r5 = r1.data     // Catch:{ Exception -> 0x0a07 }
            if (r5 == 0) goto L_0x094f
            java.lang.String r11 = "data"
            r2.putExtra(r11, r5)     // Catch:{ Exception -> 0x0a07 }
        L_0x094f:
            java.lang.String r5 = "mid"
            int r11 = r3.getId()     // Catch:{ Exception -> 0x0a07 }
            r2.putExtra(r5, r11)     // Catch:{ Exception -> 0x0a07 }
            java.lang.String r1 = r1.text     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            int r11 = r15.lastButtonId     // Catch:{ Exception -> 0x0a07 }
            r22 = r3
            int r3 = r11 + 1
            r15.lastButtonId = r3     // Catch:{ Exception -> 0x0a07 }
            r3 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r5, r11, r2, r3)     // Catch:{ Exception -> 0x0a07 }
            r3 = 0
            r4.addAction(r3, r1, r2)     // Catch:{ Exception -> 0x0a07 }
            r11 = 1
            goto L_0x0975
        L_0x0970:
            r22 = r3
            r20 = r5
            r3 = 0
        L_0x0975:
            int r7 = r7 + 1
            r2 = r41
            r1 = r19
            r5 = r20
            r3 = r22
            goto L_0x091f
        L_0x0980:
            r19 = r1
            r41 = r2
            r22 = r3
            r3 = 0
            int r6 = r6 + 1
            r3 = r22
            r5 = 1
            r7 = 134217728(0x8000000, float:3.85186E-34)
            goto L_0x0910
        L_0x098f:
            r2 = r11
            goto L_0x0993
        L_0x0991:
            r3 = 0
            r2 = 0
        L_0x0993:
            if (r2 != 0) goto L_0x09ec
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0a07 }
            r2 = 24
            if (r1 >= r2) goto L_0x09ec
            java.lang.String r2 = org.telegram.messenger.SharedConfig.passcodeHash     // Catch:{ Exception -> 0x0a07 }
            int r2 = r2.length()     // Catch:{ Exception -> 0x0a07 }
            if (r2 != 0) goto L_0x09ec
            boolean r2 = r40.hasMessagesToReply()     // Catch:{ Exception -> 0x0a07 }
            if (r2 == 0) goto L_0x09ec
            android.content.Intent r2 = new android.content.Intent     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            java.lang.Class<org.telegram.messenger.PopupReplyReceiver> r5 = org.telegram.messenger.PopupReplyReceiver.class
            r2.<init>(r3, r5)     // Catch:{ Exception -> 0x0a07 }
            int r3 = r15.currentAccount     // Catch:{ Exception -> 0x0a07 }
            r2.putExtra(r8, r3)     // Catch:{ Exception -> 0x0a07 }
            r3 = 19
            if (r1 > r3) goto L_0x09d4
            r1 = 2131165477(0x7var_, float:1.7945172E38)
            java.lang.String r3 = "Reply"
            r5 = 2131627333(0x7f0e0d45, float:1.8881927E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r5)     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r7 = 2
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r5, r7, r2, r6)     // Catch:{ Exception -> 0x0a07 }
            r4.addAction(r1, r3, r2)     // Catch:{ Exception -> 0x0a07 }
            goto L_0x09ec
        L_0x09d4:
            r1 = 2131165476(0x7var_, float:1.794517E38)
            java.lang.String r3 = "Reply"
            r5 = 2131627333(0x7f0e0d45, float:1.8881927E38)
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r3, r5)     // Catch:{ Exception -> 0x0a07 }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0a07 }
            r6 = 134217728(0x8000000, float:3.85186E-34)
            r7 = 2
            android.app.PendingIntent r2 = android.app.PendingIntent.getBroadcast(r5, r7, r2, r6)     // Catch:{ Exception -> 0x0a07 }
            r4.addAction(r1, r3, r2)     // Catch:{ Exception -> 0x0a07 }
        L_0x09ec:
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
            r1.showExtraNotifications(r2, r3, r4, r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0a07 }
            r40.scheduleNotificationRepeat()     // Catch:{ Exception -> 0x0a07 }
            goto L_0x0a0b
        L_0x0a07:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0a0b:
            return
        L_0x0a0c:
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
        jadx.core.utils.exceptions.JadxRuntimeException: Don't wrap MOVE or CONST insns: 0x0b4a: MOVE  (r1v44 java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow>) = 
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
    /* JADX WARNING: Removed duplicated region for block: B:127:0x030b  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x032a  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x033d  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0391  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x039a  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x03c6  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x03d3 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x03e8  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0467  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x047f  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x049b  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x04af  */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x04d2  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x04dc A[SYNTHETIC, Splitter:B:196:0x04dc] */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0533  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0539  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0544 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x055b A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x057b  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0584  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x0595  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x05d0  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0604  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x06ee  */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x06f3  */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0764  */
    /* JADX WARNING: Removed duplicated region for block: B:370:0x0850  */
    /* JADX WARNING: Removed duplicated region for block: B:378:0x0871  */
    /* JADX WARNING: Removed duplicated region for block: B:387:0x08a0  */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x08ae A[SYNTHETIC, Splitter:B:389:0x08ae] */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x0954  */
    /* JADX WARNING: Removed duplicated region for block: B:410:0x0965  */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x0985  */
    /* JADX WARNING: Removed duplicated region for block: B:416:0x09df  */
    /* JADX WARNING: Removed duplicated region for block: B:419:0x0a12  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x0a33  */
    /* JADX WARNING: Removed duplicated region for block: B:425:0x0a55  */
    /* JADX WARNING: Removed duplicated region for block: B:428:0x0b0b  */
    /* JADX WARNING: Removed duplicated region for block: B:430:0x0b16  */
    /* JADX WARNING: Removed duplicated region for block: B:432:0x0b1b  */
    /* JADX WARNING: Removed duplicated region for block: B:437:0x0b2b  */
    /* JADX WARNING: Removed duplicated region for block: B:438:0x0b31  */
    /* JADX WARNING: Removed duplicated region for block: B:440:0x0b35  */
    /* JADX WARNING: Removed duplicated region for block: B:442:0x0b3a  */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0b55  */
    /* JADX WARNING: Removed duplicated region for block: B:464:0x0be0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:473:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x0cbe A[Catch:{ JSONException -> 0x0d0b }] */
    /* JADX WARNING: Removed duplicated region for block: B:485:0x0ce5 A[Catch:{ JSONException -> 0x0d0b }] */
    /* JADX WARNING: Removed duplicated region for block: B:488:0x0cee  */
    /* JADX WARNING: Removed duplicated region for block: B:492:0x0cf6 A[Catch:{ JSONException -> 0x0d0b }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01ce  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01d8  */
    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(androidx.core.app.NotificationCompat.Builder r94, java.lang.String r95, long r96, java.lang.String r98, long[] r99, int r100, android.net.Uri r101, int r102, boolean r103, boolean r104, boolean r105, int r106) {
        /*
            r93 = this;
            r15 = r93
            r14 = r94
            int r0 = android.os.Build.VERSION.SDK_INT
            r13 = 26
            if (r0 < r13) goto L_0x0027
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
            java.lang.String r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            r14.setChannelId(r1)
        L_0x0027:
            android.app.Notification r12 = r94.build()
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
            org.telegram.messenger.UserConfig r0 = r93.getUserConfig()
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
            if (r2 >= r6) goto L_0x0d26
            int r0 = r7.size()
            if (r0 < r13) goto L_0x0106
            goto L_0x0d26
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
            if (r9 == 0) goto L_0x0277
            r0 = 777000(0xbdb28, float:1.088809E-39)
            if (r9 == r0) goto L_0x0173
            r0 = 1
            goto L_0x0174
        L_0x0173:
            r0 = 0
        L_0x0174:
            if (r9 <= 0) goto L_0x01f3
            r35 = r0
            org.telegram.messenger.MessagesController r0 = r93.getMessagesController()
            r36 = r5
            java.lang.Integer r5 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r5)
            if (r0 != 0) goto L_0x01ad
            boolean r5 = r10.isFcmMessage()
            if (r5 == 0) goto L_0x0193
            java.lang.String r5 = r10.localName
        L_0x0190:
            r37 = r13
            goto L_0x01c6
        L_0x0193:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02a9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found user to show dialog notification "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02a9
        L_0x01ad:
            java.lang.String r5 = org.telegram.messenger.UserObject.getUserName(r0)
            org.telegram.tgnet.TLRPC$UserProfilePhoto r10 = r0.photo
            if (r10 == 0) goto L_0x0190
            org.telegram.tgnet.TLRPC$FileLocation r10 = r10.photo_small
            if (r10 == 0) goto L_0x0190
            r37 = r13
            long r13 = r10.volume_id
            int r38 = (r13 > r33 ? 1 : (r13 == r33 ? 0 : -1))
            if (r38 == 0) goto L_0x01c6
            int r13 = r10.local_id
            if (r13 == 0) goto L_0x01c6
            goto L_0x01c7
        L_0x01c6:
            r10 = 0
        L_0x01c7:
            long r13 = (long) r9
            boolean r13 = org.telegram.messenger.UserObject.isReplyUser((long) r13)
            if (r13 == 0) goto L_0x01d8
            r5 = 2131627326(0x7f0e0d3e, float:1.8881913E38)
            java.lang.String r13 = "RepliesTitle"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)
            goto L_0x01e3
        L_0x01d8:
            if (r9 != r1) goto L_0x01e3
            r5 = 2131626231(0x7f0e08f7, float:1.8879692E38)
            java.lang.String r13 = "MessageScheduledReminderNotification"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r13, r5)
        L_0x01e3:
            r38 = r11
            r14 = r31
            r11 = 0
            r31 = 0
            r39 = 0
            r91 = r10
            r10 = r0
            r0 = r91
            goto L_0x0303
        L_0x01f3:
            r35 = r0
            r36 = r5
            r37 = r13
            org.telegram.messenger.MessagesController r0 = r93.getMessagesController()
            int r5 = -r9
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r5)
            if (r0 != 0) goto L_0x023d
            boolean r5 = r10.isFcmMessage()
            if (r5 == 0) goto L_0x0223
            boolean r5 = r10.isSupergroup()
            java.lang.String r13 = r10.localName
            boolean r10 = r10.localChannel
            r39 = r10
            r38 = r11
            r14 = r31
            r10 = 0
            r11 = r0
            r31 = r5
            r5 = r13
            goto L_0x0302
        L_0x0223:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02a9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found chat to show dialog notification "
            r0.append(r2)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02a9
        L_0x023d:
            boolean r5 = r0.megagroup
            boolean r10 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r10 == 0) goto L_0x024b
            boolean r10 = r0.megagroup
            if (r10 != 0) goto L_0x024b
            r10 = 1
            goto L_0x024c
        L_0x024b:
            r10 = 0
        L_0x024c:
            java.lang.String r13 = r0.title
            org.telegram.tgnet.TLRPC$ChatPhoto r14 = r0.photo
            if (r14 == 0) goto L_0x0269
            org.telegram.tgnet.TLRPC$FileLocation r14 = r14.photo_small
            if (r14 == 0) goto L_0x0269
            r39 = r10
            r38 = r11
            long r10 = r14.volume_id
            int r40 = (r10 > r33 ? 1 : (r10 == r33 ? 0 : -1))
            if (r40 == 0) goto L_0x026d
            int r10 = r14.local_id
            if (r10 == 0) goto L_0x026d
            r11 = r0
            r0 = r14
            r14 = r31
            goto L_0x0271
        L_0x0269:
            r39 = r10
            r38 = r11
        L_0x026d:
            r11 = r0
            r14 = r31
            r0 = 0
        L_0x0271:
            r10 = 0
            r31 = r5
            r5 = r13
            goto L_0x0303
        L_0x0277:
            r36 = r5
            r38 = r11
            r37 = r13
            long r10 = globalSecretChatId
            int r0 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x02ee
            org.telegram.messenger.MessagesController r0 = r93.getMessagesController()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$EncryptedChat r0 = r0.getEncryptedChat(r5)
            if (r0 != 0) goto L_0x02c3
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x02a9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "not found secret chat to show dialog notification "
            r0.append(r2)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x02a9:
            r72 = r1
            r21 = r3
            r74 = r4
            r14 = r22
            r86 = r28
            r12 = r36
            r22 = 0
            r24 = 7
            r28 = 1
            r34 = 27
            r35 = 0
            r36 = 26
            goto L_0x0d0d
        L_0x02c3:
            org.telegram.messenger.MessagesController r5 = r93.getMessagesController()
            int r10 = r0.user_id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r10)
            if (r5 != 0) goto L_0x02ef
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x02a9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "not found secret chat user to show dialog notification "
            r2.append(r5)
            int r0 = r0.user_id
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            org.telegram.messenger.FileLog.w(r0)
            goto L_0x02a9
        L_0x02ee:
            r5 = 0
        L_0x02ef:
            r0 = 2131627509(0x7f0e0df5, float:1.8882284E38)
            java.lang.String r10 = "SecretChatName"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r10, r0)
            r10 = r5
            r11 = 0
            r14 = 0
            r31 = 0
            r35 = 0
            r39 = 0
            r5 = r0
        L_0x0302:
            r0 = 0
        L_0x0303:
            java.lang.String r13 = "NotificationHiddenChatName"
            r41 = r5
            java.lang.String r5 = "NotificationHiddenName"
            if (r20 == 0) goto L_0x032a
            if (r9 >= 0) goto L_0x0317
            r43 = r10
            r10 = 2131626504(0x7f0e0a08, float:1.8880246E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r10)
            goto L_0x0320
        L_0x0317:
            r43 = r10
            r10 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r5, r10)
        L_0x0320:
            r35 = r3
            r41 = r8
            r44 = r12
            r3 = 0
            r10 = 0
            r8 = r0
            goto L_0x033b
        L_0x032a:
            r43 = r10
            r10 = r0
            r44 = r12
            r91 = r35
            r35 = r3
            r3 = r91
            r92 = r41
            r41 = r8
            r8 = r92
        L_0x033b:
            if (r10 == 0) goto L_0x0391
            r12 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r10, r12)
            int r12 = android.os.Build.VERSION.SDK_INT
            r45 = r5
            r5 = 28
            if (r12 >= r5) goto L_0x038b
            org.telegram.messenger.ImageLoader r5 = org.telegram.messenger.ImageLoader.getInstance()
            java.lang.String r12 = "50_50"
            r46 = r13
            r13 = 0
            android.graphics.drawable.BitmapDrawable r5 = r5.getImageFromMemory(r10, r13, r12)
            if (r5 == 0) goto L_0x0360
            android.graphics.Bitmap r5 = r5.getBitmap()
        L_0x035d:
            r12 = r5
            r5 = r0
            goto L_0x0398
        L_0x0360:
            boolean r5 = r0.exists()     // Catch:{ all -> 0x038e }
            if (r5 == 0) goto L_0x0389
            r5 = 1126170624(0x43200000, float:160.0)
            r12 = 1112014848(0x42480000, float:50.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)     // Catch:{ all -> 0x038e }
            float r12 = (float) r12     // Catch:{ all -> 0x038e }
            float r5 = r5 / r12
            android.graphics.BitmapFactory$Options r12 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x038e }
            r12.<init>()     // Catch:{ all -> 0x038e }
            r18 = 1065353216(0x3var_, float:1.0)
            int r18 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1))
            if (r18 >= 0) goto L_0x037d
            r5 = 1
            goto L_0x037e
        L_0x037d:
            int r5 = (int) r5     // Catch:{ all -> 0x038e }
        L_0x037e:
            r12.inSampleSize = r5     // Catch:{ all -> 0x038e }
            java.lang.String r5 = r0.getAbsolutePath()     // Catch:{ all -> 0x038e }
            android.graphics.Bitmap r5 = android.graphics.BitmapFactory.decodeFile(r5, r12)     // Catch:{ all -> 0x038e }
            goto L_0x035d
        L_0x0389:
            r5 = r13
            goto L_0x035d
        L_0x038b:
            r46 = r13
            r13 = 0
        L_0x038e:
            r5 = r0
            r12 = r13
            goto L_0x0398
        L_0x0391:
            r45 = r5
            r46 = r13
            r13 = 0
            r5 = r13
            r12 = r5
        L_0x0398:
            if (r11 == 0) goto L_0x03c6
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r8)
            if (r5 == 0) goto L_0x03b7
            boolean r18 = r5.exists()
            if (r18 == 0) goto L_0x03b7
            int r13 = android.os.Build.VERSION.SDK_INT
            r47 = r10
            r10 = 28
            if (r13 < r10) goto L_0x03b9
            r15.loadRoundAvatar(r5, r0)
            goto L_0x03b9
        L_0x03b7:
            r47 = r10
        L_0x03b9:
            int r10 = r11.id
            int r10 = -r10
            r13 = r11
            long r10 = (long) r10
            androidx.core.app.Person r0 = r0.build()
            r4.put(r10, r0)
            goto L_0x03c9
        L_0x03c6:
            r47 = r10
            r13 = r11
        L_0x03c9:
            java.lang.String r10 = "dialog_id"
            java.lang.String r11 = "max_id"
            r48 = r5
            java.lang.String r5 = "currentAccount"
            if (r39 == 0) goto L_0x03d5
            if (r31 == 0) goto L_0x046c
        L_0x03d5:
            if (r3 == 0) goto L_0x046c
            boolean r0 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r0 != 0) goto L_0x046c
            if (r1 == r9) goto L_0x046c
            r49 = r12
            r50 = r13
            long r12 = (long) r9
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r12)
            if (r0 != 0) goto L_0x0467
            android.content.Intent r0 = new android.content.Intent
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.WearReplyReceiver> r13 = org.telegram.messenger.WearReplyReceiver.class
            r0.<init>(r12, r13)
            r0.putExtra(r10, r6)
            r0.putExtra(r11, r2)
            int r12 = r15.currentAccount
            r0.putExtra(r5, r12)
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r13 = r38.intValue()
            r51 = r3
            r3 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r0 = android.app.PendingIntent.getBroadcast(r12, r13, r0, r3)
            androidx.core.app.RemoteInput$Builder r3 = new androidx.core.app.RemoteInput$Builder
            java.lang.String r12 = "extra_voice_reply"
            r3.<init>(r12)
            r12 = 2131627333(0x7f0e0d45, float:1.8881927E38)
            java.lang.String r13 = "Reply"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            androidx.core.app.RemoteInput$Builder r3 = r3.setLabel(r12)
            androidx.core.app.RemoteInput r3 = r3.build()
            if (r9 >= 0) goto L_0x0436
            r13 = 1
            java.lang.Object[] r12 = new java.lang.Object[r13]
            r13 = 0
            r12[r13] = r8
            java.lang.String r13 = "ReplyToGroup"
            r53 = r2
            r2 = 2131627334(0x7f0e0d46, float:1.888193E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r13, r2, r12)
            goto L_0x0447
        L_0x0436:
            r53 = r2
            r2 = 2131627335(0x7f0e0d47, float:1.8881932E38)
            r12 = 1
            java.lang.Object[] r13 = new java.lang.Object[r12]
            r12 = 0
            r13[r12] = r8
            java.lang.String r12 = "ReplyToUser"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r12, r2, r13)
        L_0x0447:
            androidx.core.app.NotificationCompat$Action$Builder r12 = new androidx.core.app.NotificationCompat$Action$Builder
            r13 = 2131165521(0x7var_, float:1.7945261E38)
            r12.<init>(r13, r2, r0)
            r2 = 1
            androidx.core.app.NotificationCompat$Action$Builder r0 = r12.setAllowGeneratedReplies(r2)
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setSemanticAction(r2)
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.addRemoteInput(r3)
            r2 = 0
            androidx.core.app.NotificationCompat$Action$Builder r0 = r0.setShowsUserInterface(r2)
            androidx.core.app.NotificationCompat$Action r0 = r0.build()
            r2 = r0
            goto L_0x0475
        L_0x0467:
            r53 = r2
            r51 = r3
            goto L_0x0474
        L_0x046c:
            r53 = r2
            r51 = r3
            r49 = r12
            r50 = r13
        L_0x0474:
            r2 = 0
        L_0x0475:
            android.util.LongSparseArray<java.lang.Integer> r0 = r15.pushDialogs
            java.lang.Object r0 = r0.get(r6)
            java.lang.Integer r0 = (java.lang.Integer) r0
            if (r0 != 0) goto L_0x0484
            r3 = 0
            java.lang.Integer r0 = java.lang.Integer.valueOf(r3)
        L_0x0484:
            int r0 = r0.intValue()
            int r3 = r37.size()
            int r0 = java.lang.Math.max(r0, r3)
            r3 = 2
            r12 = 1
            if (r0 <= r12) goto L_0x04af
            int r13 = android.os.Build.VERSION.SDK_INT
            r12 = 28
            if (r13 < r12) goto L_0x049b
            goto L_0x04af
        L_0x049b:
            java.lang.Object[] r12 = new java.lang.Object[r3]
            r13 = 0
            r12[r13] = r8
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r13 = 1
            r12[r13] = r0
            java.lang.String r0 = "%1$s (%2$d)"
            java.lang.String r0 = java.lang.String.format(r0, r12)
            r12 = r0
            goto L_0x04b0
        L_0x04af:
            r12 = r8
        L_0x04b0:
            r13 = r10
            r52 = r11
            long r10 = (long) r1
            java.lang.Object r0 = r4.get(r10)
            r54 = r0
            androidx.core.app.Person r54 = (androidx.core.app.Person) r54
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r0 < r3) goto L_0x0529
            if (r54 != 0) goto L_0x0529
            org.telegram.messenger.MessagesController r0 = r93.getMessagesController()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r3)
            if (r0 != 0) goto L_0x04da
            org.telegram.messenger.UserConfig r0 = r93.getUserConfig()
            org.telegram.tgnet.TLRPC$User r0 = r0.getCurrentUser()
        L_0x04da:
            if (r0 == 0) goto L_0x0529
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r0.photo     // Catch:{ all -> 0x0520 }
            if (r3 == 0) goto L_0x0529
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_small     // Catch:{ all -> 0x0520 }
            if (r3 == 0) goto L_0x0529
            r55 = r1
            r56 = r2
            long r1 = r3.volume_id     // Catch:{ all -> 0x051e }
            int r57 = (r1 > r33 ? 1 : (r1 == r33 ? 0 : -1))
            if (r57 == 0) goto L_0x052d
            int r1 = r3.local_id     // Catch:{ all -> 0x051e }
            if (r1 == 0) goto L_0x052d
            androidx.core.app.Person$Builder r1 = new androidx.core.app.Person$Builder     // Catch:{ all -> 0x051e }
            r1.<init>()     // Catch:{ all -> 0x051e }
            java.lang.String r2 = "FromYou"
            r3 = 2131625717(0x7f0e06f5, float:1.887865E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r3)     // Catch:{ all -> 0x051e }
            androidx.core.app.Person$Builder r1 = r1.setName(r2)     // Catch:{ all -> 0x051e }
            org.telegram.tgnet.TLRPC$UserProfilePhoto r0 = r0.photo     // Catch:{ all -> 0x051e }
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small     // Catch:{ all -> 0x051e }
            r2 = 1
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r2)     // Catch:{ all -> 0x051e }
            r15.loadRoundAvatar(r0, r1)     // Catch:{ all -> 0x051e }
            androidx.core.app.Person r1 = r1.build()     // Catch:{ all -> 0x051e }
            r4.put(r10, r1)     // Catch:{ all -> 0x051a }
            r54 = r1
            goto L_0x052d
        L_0x051a:
            r0 = move-exception
            r54 = r1
            goto L_0x0525
        L_0x051e:
            r0 = move-exception
            goto L_0x0525
        L_0x0520:
            r0 = move-exception
            r55 = r1
            r56 = r2
        L_0x0525:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x052d
        L_0x0529:
            r55 = r1
            r56 = r2
        L_0x052d:
            r0 = r54
            java.lang.String r1 = ""
            if (r0 == 0) goto L_0x0539
            androidx.core.app.NotificationCompat$MessagingStyle r2 = new androidx.core.app.NotificationCompat$MessagingStyle
            r2.<init>((androidx.core.app.Person) r0)
            goto L_0x053e
        L_0x0539:
            androidx.core.app.NotificationCompat$MessagingStyle r2 = new androidx.core.app.NotificationCompat$MessagingStyle
            r2.<init>((java.lang.CharSequence) r1)
        L_0x053e:
            int r0 = android.os.Build.VERSION.SDK_INT
            r3 = 28
            if (r0 < r3) goto L_0x0552
            if (r9 >= 0) goto L_0x0548
            if (r39 == 0) goto L_0x0552
        L_0x0548:
            r54 = r4
            long r3 = (long) r9
            boolean r3 = org.telegram.messenger.UserObject.isReplyUser((long) r3)
            if (r3 == 0) goto L_0x0557
            goto L_0x0554
        L_0x0552:
            r54 = r4
        L_0x0554:
            r2.setConversationTitle(r12)
        L_0x0557:
            r3 = 28
            if (r0 < r3) goto L_0x0569
            if (r39 != 0) goto L_0x055f
            if (r9 < 0) goto L_0x0569
        L_0x055f:
            long r3 = (long) r9
            boolean r0 = org.telegram.messenger.UserObject.isReplyUser((long) r3)
            if (r0 == 0) goto L_0x0567
            goto L_0x0569
        L_0x0567:
            r0 = 0
            goto L_0x056a
        L_0x0569:
            r0 = 1
        L_0x056a:
            r2.setGroupConversation(r0)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 1
            java.lang.String[] r12 = new java.lang.String[r4]
            r57 = r13
            boolean[] r13 = new boolean[r4]
            if (r14 == 0) goto L_0x0584
            org.json.JSONArray r0 = new org.json.JSONArray
            r0.<init>()
            r58 = r14
            r14 = r0
            goto L_0x0587
        L_0x0584:
            r58 = r14
            r14 = 0
        L_0x0587:
            int r0 = r37.size()
            int r0 = r0 - r4
            r4 = r0
            r59 = 0
            r60 = 0
        L_0x0591:
            r61 = 1000(0x3e8, double:4.94E-321)
            if (r4 < 0) goto L_0x0917
            r63 = r5
            r5 = r37
            java.lang.Object r0 = r5.get(r4)
            r5 = r0
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            java.lang.String r0 = r15.getShortStringForMessage(r5, r12, r13)
            int r64 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r64 != 0) goto L_0x05ad
            r21 = 0
            r12[r21] = r8
            goto L_0x05ca
        L_0x05ad:
            r21 = 0
            if (r9 >= 0) goto L_0x05ca
            r64 = r8
            org.telegram.tgnet.TLRPC$Message r8 = r5.messageOwner
            boolean r8 = r8.from_scheduled
            if (r8 == 0) goto L_0x05c7
            r8 = 2131626550(0x7f0e0a36, float:1.888034E38)
            r65 = r4
            java.lang.String r4 = "NotificationMessageScheduledName"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r4, r8)
            r12[r21] = r4
            goto L_0x05ce
        L_0x05c7:
            r65 = r4
            goto L_0x05ce
        L_0x05ca:
            r65 = r4
            r64 = r8
        L_0x05ce:
            if (r0 != 0) goto L_0x0604
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x05f8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "message text is null for "
            r0.append(r4)
            int r4 = r5.getId()
            r0.append(r4)
            java.lang.String r4 = " did = "
            r0.append(r4)
            long r4 = r5.getDialogId()
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.w(r0)
        L_0x05f8:
            r68 = r6
            r66 = r10
            r42 = r45
            r70 = r54
            r54 = r3
            goto L_0x0905
        L_0x0604:
            int r4 = r3.length()
            if (r4 <= 0) goto L_0x060f
            java.lang.String r4 = "\n\n"
            r3.append(r4)
        L_0x060f:
            int r4 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1))
            if (r4 == 0) goto L_0x0639
            org.telegram.tgnet.TLRPC$Message r4 = r5.messageOwner
            boolean r4 = r4.from_scheduled
            if (r4 == 0) goto L_0x0639
            if (r9 <= 0) goto L_0x0639
            r4 = 2
            java.lang.Object[] r8 = new java.lang.Object[r4]
            r4 = 2131626550(0x7f0e0a36, float:1.888034E38)
            r66 = r10
            java.lang.String r10 = "NotificationMessageScheduledName"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r10, r4)
            r10 = 0
            r8[r10] = r4
            r4 = 1
            r8[r4] = r0
            java.lang.String r0 = "%1$s: %2$s"
            java.lang.String r0 = java.lang.String.format(r0, r8)
            r3.append(r0)
            goto L_0x0657
        L_0x0639:
            r66 = r10
            r10 = 0
            r4 = r12[r10]
            if (r4 == 0) goto L_0x0654
            r4 = 2
            java.lang.Object[] r8 = new java.lang.Object[r4]
            r4 = r12[r10]
            r8[r10] = r4
            r4 = 1
            r8[r4] = r0
            java.lang.String r4 = "%1$s: %2$s"
            java.lang.String r4 = java.lang.String.format(r4, r8)
            r3.append(r4)
            goto L_0x0657
        L_0x0654:
            r3.append(r0)
        L_0x0657:
            r4 = r0
            if (r9 <= 0) goto L_0x065e
            long r10 = (long) r9
        L_0x065b:
            r8 = r54
            goto L_0x066c
        L_0x065e:
            if (r39 == 0) goto L_0x0663
            int r0 = -r9
        L_0x0661:
            long r10 = (long) r0
            goto L_0x065b
        L_0x0663:
            if (r9 >= 0) goto L_0x066a
            int r0 = r5.getSenderId()
            goto L_0x0661
        L_0x066a:
            r10 = r6
            goto L_0x065b
        L_0x066c:
            java.lang.Object r0 = r8.get(r10)
            androidx.core.app.Person r0 = (androidx.core.app.Person) r0
            r21 = 0
            r54 = r12[r21]
            if (r54 != 0) goto L_0x06ce
            if (r20 == 0) goto L_0x06c1
            if (r9 >= 0) goto L_0x06a8
            if (r39 == 0) goto L_0x0694
            r54 = r3
            int r3 = android.os.Build.VERSION.SDK_INT
            r68 = r6
            r6 = 27
            r7 = r46
            if (r3 <= r6) goto L_0x06be
            r3 = 2131626504(0x7f0e0a08, float:1.8880246E38)
            java.lang.String r17 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r3 = r17
            goto L_0x06a5
        L_0x0694:
            r54 = r3
            r68 = r6
            r7 = r46
            r6 = 27
            r3 = 2131626505(0x7f0e0a09, float:1.8880248E38)
            java.lang.String r6 = "NotificationHiddenChatUserName"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
        L_0x06a5:
            r6 = r45
            goto L_0x06df
        L_0x06a8:
            r54 = r3
            r68 = r6
            r7 = r46
            int r3 = android.os.Build.VERSION.SDK_INT
            r6 = 27
            if (r3 <= r6) goto L_0x06be
            r6 = r45
            r3 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            java.lang.String r42 = org.telegram.messenger.LocaleController.getString(r6, r3)
            goto L_0x06dd
        L_0x06be:
            r6 = r45
            goto L_0x06c9
        L_0x06c1:
            r54 = r3
            r68 = r6
            r6 = r45
            r7 = r46
        L_0x06c9:
            r3 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            r3 = r1
            goto L_0x06df
        L_0x06ce:
            r54 = r3
            r68 = r6
            r6 = r45
            r7 = r46
            r3 = 2131626507(0x7f0e0a0b, float:1.8880252E38)
            r21 = 0
            r42 = r12[r21]
        L_0x06dd:
            r3 = r42
        L_0x06df:
            r42 = r6
            if (r0 == 0) goto L_0x06f3
            java.lang.CharSequence r6 = r0.getName()
            boolean r6 = android.text.TextUtils.equals(r6, r3)
            if (r6 != 0) goto L_0x06ee
            goto L_0x06f3
        L_0x06ee:
            r3 = r0
            r46 = r7
            goto L_0x0762
        L_0x06f3:
            androidx.core.app.Person$Builder r0 = new androidx.core.app.Person$Builder
            r0.<init>()
            androidx.core.app.Person$Builder r0 = r0.setName(r3)
            r3 = 0
            boolean r6 = r13[r3]
            if (r6 == 0) goto L_0x0758
            if (r9 == 0) goto L_0x0758
            int r3 = android.os.Build.VERSION.SDK_INT
            r6 = 28
            if (r3 < r6) goto L_0x0758
            if (r9 > 0) goto L_0x0750
            if (r39 == 0) goto L_0x070e
            goto L_0x0750
        L_0x070e:
            int r3 = r5.getSenderId()
            org.telegram.messenger.MessagesController r6 = r93.getMessagesController()
            r46 = r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            if (r6 != 0) goto L_0x0734
            org.telegram.messenger.MessagesStorage r6 = r93.getMessagesStorage()
            org.telegram.tgnet.TLRPC$User r6 = r6.getUserSync(r3)
            if (r6 == 0) goto L_0x0734
            org.telegram.messenger.MessagesController r3 = r93.getMessagesController()
            r7 = 1
            r3.putUser(r6, r7)
        L_0x0734:
            if (r6 == 0) goto L_0x074e
            org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r6.photo
            if (r3 == 0) goto L_0x074e
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_small
            if (r3 == 0) goto L_0x074e
            long r6 = r3.volume_id
            int r70 = (r6 > r33 ? 1 : (r6 == r33 ? 0 : -1))
            if (r70 == 0) goto L_0x074e
            int r6 = r3.local_id
            if (r6 == 0) goto L_0x074e
            r6 = 1
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r6)
            goto L_0x0754
        L_0x074e:
            r3 = 0
            goto L_0x0754
        L_0x0750:
            r46 = r7
            r3 = r48
        L_0x0754:
            r15.loadRoundAvatar(r3, r0)
            goto L_0x075a
        L_0x0758:
            r46 = r7
        L_0x075a:
            androidx.core.app.Person r0 = r0.build()
            r8.put(r10, r0)
            r3 = r0
        L_0x0762:
            if (r9 == 0) goto L_0x08a0
            r6 = 0
            boolean r0 = r13[r6]
            if (r0 == 0) goto L_0x084b
            int r0 = android.os.Build.VERSION.SDK_INT
            r6 = 28
            if (r0 < r6) goto L_0x084b
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r7 = "activity"
            java.lang.Object r0 = r0.getSystemService(r7)
            android.app.ActivityManager r0 = (android.app.ActivityManager) r0
            boolean r0 = r0.isLowRamDevice()
            if (r0 != 0) goto L_0x084b
            if (r20 != 0) goto L_0x084b
            boolean r0 = r5.isSecretMedia()
            if (r0 != 0) goto L_0x084b
            int r0 = r5.type
            r7 = 1
            if (r0 == r7) goto L_0x0792
            boolean r0 = r5.isSticker()
            if (r0 == 0) goto L_0x084b
        L_0x0792:
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r7 = new androidx.core.app.NotificationCompat$MessagingStyle$Message
            org.telegram.tgnet.TLRPC$Message r10 = r5.messageOwner
            int r10 = r10.date
            long r10 = (long) r10
            long r10 = r10 * r61
            r7.<init>(r4, r10, r3)
            boolean r10 = r5.isSticker()
            if (r10 == 0) goto L_0x07ad
            java.lang.String r10 = "image/webp"
            goto L_0x07af
        L_0x07ad:
            java.lang.String r10 = "image/jpeg"
        L_0x07af:
            boolean r11 = r0.exists()
            if (r11 == 0) goto L_0x07c5
            android.content.Context r11 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x07c0 }
            java.lang.String r6 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r11, r6, r0)     // Catch:{ Exception -> 0x07c0 }
            r70 = r8
            goto L_0x081b
        L_0x07c0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0818
        L_0x07c5:
            org.telegram.messenger.FileLoader r6 = r93.getFileLoader()
            java.lang.String r11 = r0.getName()
            boolean r6 = r6.isLoadingFile(r11)
            if (r6 == 0) goto L_0x0818
            android.net.Uri$Builder r6 = new android.net.Uri$Builder
            r6.<init>()
            java.lang.String r11 = "content"
            android.net.Uri$Builder r6 = r6.scheme(r11)
            java.lang.String r11 = "org.telegram.messenger.beta.notification_image_provider"
            android.net.Uri$Builder r6 = r6.authority(r11)
            java.lang.String r11 = "msg_media_raw"
            android.net.Uri$Builder r6 = r6.appendPath(r11)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r70 = r8
            int r8 = r15.currentAccount
            r11.append(r8)
            r11.append(r1)
            java.lang.String r8 = r11.toString()
            android.net.Uri$Builder r6 = r6.appendPath(r8)
            java.lang.String r8 = r0.getName()
            android.net.Uri$Builder r6 = r6.appendPath(r8)
            java.lang.String r0 = r0.getAbsolutePath()
            java.lang.String r8 = "final_path"
            android.net.Uri$Builder r0 = r6.appendQueryParameter(r8, r0)
            android.net.Uri r0 = r0.build()
            goto L_0x081b
        L_0x0818:
            r70 = r8
            r0 = 0
        L_0x081b:
            if (r0 == 0) goto L_0x084d
            r7.setData(r10, r0)
            r2.addMessage(r7)
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r7 = "com.android.systemui"
            r8 = 1
            r6.grantUriPermission(r7, r0, r8)
            org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda4 r6 = new org.telegram.messenger.NotificationsController$$ExternalSyntheticLambda4
            r6.<init>(r0)
            r7 = 20000(0x4e20, double:9.8813E-320)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6, r7)
            java.lang.CharSequence r0 = r5.caption
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0849
            java.lang.CharSequence r0 = r5.caption
            org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
            int r6 = r6.date
            long r6 = (long) r6
            long r6 = r6 * r61
            r2.addMessage(r0, r6, r3)
        L_0x0849:
            r0 = 1
            goto L_0x084e
        L_0x084b:
            r70 = r8
        L_0x084d:
            r0 = 0
        L_0x084e:
            if (r0 != 0) goto L_0x085a
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            int r0 = r0.date
            long r6 = (long) r0
            long r6 = r6 * r61
            r2.addMessage(r4, r6, r3)
        L_0x085a:
            r3 = 0
            boolean r0 = r13[r3]
            if (r0 == 0) goto L_0x08ac
            if (r20 != 0) goto L_0x08ac
            boolean r0 = r5.isVoice()
            if (r0 == 0) goto L_0x08ac
            java.util.List r0 = r2.getMessages()
            boolean r3 = r0.isEmpty()
            if (r3 != 0) goto L_0x08ac
            org.telegram.tgnet.TLRPC$Message r3 = r5.messageOwner
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3)
            int r6 = android.os.Build.VERSION.SDK_INT
            r7 = 24
            if (r6 < r7) goto L_0x0888
            android.content.Context r6 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0886 }
            java.lang.String r7 = "org.telegram.messenger.beta.provider"
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r6, r7, r3)     // Catch:{ Exception -> 0x0886 }
            goto L_0x088c
        L_0x0886:
            r3 = 0
            goto L_0x088c
        L_0x0888:
            android.net.Uri r3 = android.net.Uri.fromFile(r3)
        L_0x088c:
            if (r3 == 0) goto L_0x08ac
            int r6 = r0.size()
            r7 = 1
            int r6 = r6 - r7
            java.lang.Object r0 = r0.get(r6)
            androidx.core.app.NotificationCompat$MessagingStyle$Message r0 = (androidx.core.app.NotificationCompat.MessagingStyle.Message) r0
            java.lang.String r6 = "audio/ogg"
            r0.setData(r6, r3)
            goto L_0x08ac
        L_0x08a0:
            r70 = r8
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            int r0 = r0.date
            long r6 = (long) r0
            long r6 = r6 * r61
            r2.addMessage(r4, r6, r3)
        L_0x08ac:
            if (r14 == 0) goto L_0x08ee
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x08ed }
            r0.<init>()     // Catch:{ JSONException -> 0x08ed }
            java.lang.String r3 = "text"
            r0.put(r3, r4)     // Catch:{ JSONException -> 0x08ed }
            java.lang.String r3 = "date"
            org.telegram.tgnet.TLRPC$Message r4 = r5.messageOwner     // Catch:{ JSONException -> 0x08ed }
            int r4 = r4.date     // Catch:{ JSONException -> 0x08ed }
            r0.put(r3, r4)     // Catch:{ JSONException -> 0x08ed }
            boolean r3 = r5.isFromUser()     // Catch:{ JSONException -> 0x08ed }
            if (r3 == 0) goto L_0x08e9
            if (r9 >= 0) goto L_0x08e9
            org.telegram.messenger.MessagesController r3 = r93.getMessagesController()     // Catch:{ JSONException -> 0x08ed }
            int r4 = r5.getSenderId()     // Catch:{ JSONException -> 0x08ed }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ JSONException -> 0x08ed }
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)     // Catch:{ JSONException -> 0x08ed }
            if (r3 == 0) goto L_0x08e9
            java.lang.String r4 = "fname"
            java.lang.String r6 = r3.first_name     // Catch:{ JSONException -> 0x08ed }
            r0.put(r4, r6)     // Catch:{ JSONException -> 0x08ed }
            java.lang.String r4 = "lname"
            java.lang.String r3 = r3.last_name     // Catch:{ JSONException -> 0x08ed }
            r0.put(r4, r3)     // Catch:{ JSONException -> 0x08ed }
        L_0x08e9:
            r14.put(r0)     // Catch:{ JSONException -> 0x08ed }
            goto L_0x08ee
        L_0x08ed:
        L_0x08ee:
            r3 = 777000(0xbdb28, double:3.83889E-318)
            int r0 = (r68 > r3 ? 1 : (r68 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0905
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$ReplyMarkup r0 = r0.reply_markup
            if (r0 == 0) goto L_0x0905
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_keyboardButtonRow> r0 = r0.rows
            int r3 = r5.getId()
            r60 = r0
            r59 = r3
        L_0x0905:
            int r4 = r65 + -1
            r45 = r42
            r3 = r54
            r5 = r63
            r8 = r64
            r10 = r66
            r6 = r68
            r54 = r70
            goto L_0x0591
        L_0x0917:
            r63 = r5
            r68 = r6
            r64 = r8
            r70 = r54
            r54 = r3
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
            r1 = 67108864(0x4000000, float:1.5046328E-36)
            r0.setFlags(r1)
            java.lang.String r1 = "android.intent.category.LAUNCHER"
            r0.addCategory(r1)
            if (r9 == 0) goto L_0x0965
            if (r9 <= 0) goto L_0x095c
            java.lang.String r1 = "userId"
            r0.putExtra(r1, r9)
            goto L_0x0962
        L_0x095c:
            int r1 = -r9
            java.lang.String r3 = "chatId"
            r0.putExtra(r3, r1)
        L_0x0962:
            r3 = r44
            goto L_0x096c
        L_0x0965:
            java.lang.String r1 = "encId"
            r3 = r44
            r0.putExtra(r1, r3)
        L_0x096c:
            int r1 = r15.currentAccount
            r4 = r63
            r0.putExtra(r4, r1)
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r5 = 1073741824(0x40000000, float:2.0)
            r6 = 0
            android.app.PendingIntent r0 = android.app.PendingIntent.getActivity(r1, r6, r0, r5)
            androidx.core.app.NotificationCompat$WearableExtender r1 = new androidx.core.app.NotificationCompat$WearableExtender
            r1.<init>()
            r5 = r56
            if (r56 == 0) goto L_0x0988
            r1.addAction(r5)
        L_0x0988:
            android.content.Intent r6 = new android.content.Intent
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.AutoMessageHeardReceiver> r8 = org.telegram.messenger.AutoMessageHeardReceiver.class
            r6.<init>(r7, r8)
            r7 = 32
            r6.addFlags(r7)
            java.lang.String r7 = "org.telegram.messenger.ACTION_MESSAGE_HEARD"
            r6.setAction(r7)
            r10 = r57
            r7 = r68
            r6.putExtra(r10, r7)
            r11 = r52
            r10 = r53
            r6.putExtra(r11, r10)
            int r12 = r15.currentAccount
            r6.putExtra(r4, r12)
            android.content.Context r12 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r13 = r38.intValue()
            r11 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r6 = android.app.PendingIntent.getBroadcast(r12, r13, r6, r11)
            androidx.core.app.NotificationCompat$Action$Builder r11 = new androidx.core.app.NotificationCompat$Action$Builder
            r12 = 2131165678(0x7var_ee, float:1.794558E38)
            r13 = 2131626137(0x7f0e0899, float:1.8879502E38)
            r30 = r14
            java.lang.String r14 = "MarkAsRead"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r11.<init>(r12, r13, r6)
            r6 = 2
            androidx.core.app.NotificationCompat$Action$Builder r6 = r11.setSemanticAction(r6)
            r11 = 0
            androidx.core.app.NotificationCompat$Action$Builder r6 = r6.setShowsUserInterface(r11)
            androidx.core.app.NotificationCompat$Action r6 = r6.build()
            java.lang.String r14 = "_"
            if (r9 == 0) goto L_0x0a12
            if (r9 <= 0) goto L_0x09f9
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r11 = "tguser"
            r3.append(r11)
            r3.append(r9)
            r3.append(r14)
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            goto L_0x0a31
        L_0x09f9:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r11 = "tgchat"
            r3.append(r11)
            int r11 = -r9
            r3.append(r11)
            r3.append(r14)
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            goto L_0x0a31
        L_0x0a12:
            long r11 = globalSecretChatId
            int r13 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r13 == 0) goto L_0x0a30
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "tgenc"
            r11.append(r12)
            r11.append(r3)
            r11.append(r14)
            r11.append(r10)
            java.lang.String r3 = r11.toString()
            goto L_0x0a31
        L_0x0a30:
            r3 = 0
        L_0x0a31:
            if (r3 == 0) goto L_0x0a55
            r1.setDismissalId(r3)
            androidx.core.app.NotificationCompat$WearableExtender r11 = new androidx.core.app.NotificationCompat$WearableExtender
            r11.<init>()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "summary_"
            r12.append(r13)
            r12.append(r3)
            java.lang.String r3 = r12.toString()
            r11.setDismissalId(r3)
            r13 = r94
            r13.extend(r11)
            goto L_0x0a57
        L_0x0a55:
            r13 = r94
        L_0x0a57:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r11 = "tgaccount"
            r3.append(r11)
            r11 = r55
            r3.append(r11)
            java.lang.String r3 = r3.toString()
            r1.setBridgeTag(r3)
            r3 = r37
            r12 = 0
            java.lang.Object r33 = r3.get(r12)
            r12 = r33
            org.telegram.messenger.MessageObject r12 = (org.telegram.messenger.MessageObject) r12
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            int r12 = r12.date
            r53 = r10
            long r10 = (long) r12
            long r10 = r10 * r61
            androidx.core.app.NotificationCompat$Builder r12 = new androidx.core.app.NotificationCompat$Builder
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            r12.<init>(r13)
            r13 = r64
            androidx.core.app.NotificationCompat$Builder r12 = r12.setContentTitle(r13)
            r33 = r14
            r14 = 2131165887(0x7var_bf, float:1.7946004E38)
            androidx.core.app.NotificationCompat$Builder r12 = r12.setSmallIcon(r14)
            java.lang.String r14 = r54.toString()
            androidx.core.app.NotificationCompat$Builder r12 = r12.setContentText(r14)
            r14 = 1
            androidx.core.app.NotificationCompat$Builder r12 = r12.setAutoCancel(r14)
            int r3 = r3.size()
            androidx.core.app.NotificationCompat$Builder r3 = r12.setNumber(r3)
            r12 = -15618822(0xfffffffffvar_acfa, float:-1.936362E38)
            androidx.core.app.NotificationCompat$Builder r3 = r3.setColor(r12)
            r12 = 0
            androidx.core.app.NotificationCompat$Builder r3 = r3.setGroupSummary(r12)
            androidx.core.app.NotificationCompat$Builder r3 = r3.setWhen(r10)
            androidx.core.app.NotificationCompat$Builder r3 = r3.setShowWhen(r14)
            androidx.core.app.NotificationCompat$Builder r2 = r3.setStyle(r2)
            androidx.core.app.NotificationCompat$Builder r0 = r2.setContentIntent(r0)
            androidx.core.app.NotificationCompat$Builder r0 = r0.extend(r1)
            r1 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            long r1 = r1 - r10
            java.lang.String r1 = java.lang.String.valueOf(r1)
            androidx.core.app.NotificationCompat$Builder r0 = r0.setSortKey(r1)
            java.lang.String r1 = "msg"
            androidx.core.app.NotificationCompat$Builder r0 = r0.setCategory(r1)
            android.content.Intent r1 = new android.content.Intent
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.Class<org.telegram.messenger.NotificationDismissReceiver> r3 = org.telegram.messenger.NotificationDismissReceiver.class
            r1.<init>(r2, r3)
            java.lang.String r2 = "messageDate"
            r10 = r41
            r1.putExtra(r2, r10)
            java.lang.String r2 = "dialogId"
            r1.putExtra(r2, r7)
            int r2 = r15.currentAccount
            r1.putExtra(r4, r2)
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            int r3 = r38.intValue()
            r11 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r2, r3, r1, r11)
            r0.setDeleteIntent(r1)
            if (r35 == 0) goto L_0x0b14
            java.lang.String r1 = r15.notificationGroup
            r0.setGroup(r1)
            r1 = 1
            r0.setGroupAlertBehavior(r1)
        L_0x0b14:
            if (r5 == 0) goto L_0x0b19
            r0.addAction(r5)
        L_0x0b19:
            if (r20 != 0) goto L_0x0b1e
            r0.addAction(r6)
        L_0x0b1e:
            int r1 = r29.size()
            r2 = 1
            if (r1 != r2) goto L_0x0b31
            boolean r1 = android.text.TextUtils.isEmpty(r95)
            if (r1 != 0) goto L_0x0b31
            r14 = r95
            r0.setSubText(r14)
            goto L_0x0b33
        L_0x0b31:
            r14 = r95
        L_0x0b33:
            if (r9 != 0) goto L_0x0b38
            r0.setLocalOnly(r2)
        L_0x0b38:
            if (r49 == 0) goto L_0x0b3f
            r5 = r49
            r0.setLargeIcon(r5)
        L_0x0b3f:
            r1 = 0
            boolean r3 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1)
            if (r3 != 0) goto L_0x0bdd
            boolean r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter
            if (r1 != 0) goto L_0x0bdd
            r1 = r60
            if (r1 == 0) goto L_0x0bdd
            int r3 = r1.size()
            r5 = 0
        L_0x0b53:
            if (r5 >= r3) goto L_0x0bdd
            java.lang.Object r6 = r1.get(r5)
            org.telegram.tgnet.TLRPC$TL_keyboardButtonRow r6 = (org.telegram.tgnet.TLRPC$TL_keyboardButtonRow) r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r11 = r6.buttons
            int r11 = r11.size()
            r12 = 0
        L_0x0b62:
            if (r12 >= r11) goto L_0x0bcb
            java.util.ArrayList<org.telegram.tgnet.TLRPC$KeyboardButton> r2 = r6.buttons
            java.lang.Object r2 = r2.get(r12)
            org.telegram.tgnet.TLRPC$KeyboardButton r2 = (org.telegram.tgnet.TLRPC$KeyboardButton) r2
            r34 = r1
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_keyboardButtonCallback
            if (r1 == 0) goto L_0x0bb2
            android.content.Intent r1 = new android.content.Intent
            r37 = r3
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            r40 = r6
            java.lang.Class<org.telegram.messenger.NotificationCallbackReceiver> r6 = org.telegram.messenger.NotificationCallbackReceiver.class
            r1.<init>(r3, r6)
            int r3 = r15.currentAccount
            r1.putExtra(r4, r3)
            java.lang.String r3 = "did"
            r1.putExtra(r3, r7)
            byte[] r3 = r2.data
            if (r3 == 0) goto L_0x0b92
            java.lang.String r6 = "data"
            r1.putExtra(r6, r3)
        L_0x0b92:
            java.lang.String r3 = "mid"
            r6 = r59
            r1.putExtra(r3, r6)
            java.lang.String r2 = r2.text
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            r63 = r4
            int r4 = r15.lastButtonId
            r41 = r6
            int r6 = r4 + 1
            r15.lastButtonId = r6
            r6 = 134217728(0x8000000, float:3.85186E-34)
            android.app.PendingIntent r1 = android.app.PendingIntent.getBroadcast(r3, r4, r1, r6)
            r4 = 0
            r0.addAction(r4, r2, r1)
            goto L_0x0bbd
        L_0x0bb2:
            r37 = r3
            r63 = r4
            r40 = r6
            r41 = r59
            r4 = 0
            r6 = 134217728(0x8000000, float:3.85186E-34)
        L_0x0bbd:
            int r12 = r12 + 1
            r1 = r34
            r3 = r37
            r6 = r40
            r59 = r41
            r4 = r63
            r2 = 1
            goto L_0x0b62
        L_0x0bcb:
            r34 = r1
            r37 = r3
            r63 = r4
            r41 = r59
            r4 = 0
            r6 = 134217728(0x8000000, float:3.85186E-34)
            int r5 = r5 + 1
            r4 = r63
            r2 = 1
            goto L_0x0b53
        L_0x0bdd:
            r4 = 0
            if (r50 != 0) goto L_0x0CLASSNAME
            if (r43 == 0) goto L_0x0CLASSNAME
            r6 = r43
            java.lang.String r1 = r6.phone
            if (r1 == 0) goto L_0x0CLASSNAME
            int r1 = r1.length()
            if (r1 <= 0) goto L_0x0CLASSNAME
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "tel:+"
            r1.append(r2)
            java.lang.String r2 = r6.phone
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.addPerson(r1)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r6 = r43
        L_0x0CLASSNAME:
            int r1 = android.os.Build.VERSION.SDK_INT
            r12 = 26
            r11 = r28
            r2 = r35
            if (r1 < r12) goto L_0x0CLASSNAME
            r15.setNotificationChannel(r11, r0, r2)
        L_0x0CLASSNAME:
            org.telegram.messenger.NotificationsController$1NotificationHolder r5 = new org.telegram.messenger.NotificationsController$1NotificationHolder
            r3 = r55
            r1 = r5
            int r16 = r38.intValue()
            r21 = r2
            r2 = r3
            r71 = r51
            r3 = r16
            r72 = r2
            r73 = r53
            r28 = 1
            r2 = r93
            r74 = r70
            r16 = 0
            r34 = 27
            r4 = r9
            r76 = r5
            r75 = r36
            r5 = r13
            r35 = 0
            r77 = r7
            r8 = r22
            r7 = r50
            r79 = r10
            r80 = r13
            r13 = r32
            r10 = r8
            r8 = r0
            r81 = r10
            r83 = r27
            r82 = r47
            r22 = 0
            r27 = r9
            r9 = r96
            r16 = r11
            r84 = r38
            r85 = r52
            r11 = r98
            r86 = r16
            r16 = 26
            r12 = r99
            r24 = 7
            r36 = 26
            r13 = r100
            r88 = r30
            r0 = r33
            r87 = r58
            r14 = r101
            r15 = r102
            r16 = r103
            r17 = r104
            r18 = r105
            r19 = r106
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r1 = r76
            r14 = r81
            r14.add(r1)
            r15 = r93
            android.util.LongSparseArray<java.lang.Integer> r1 = r15.wearNotificationsIds
            r2 = r77
            r4 = r84
            r1.put(r2, r4)
            if (r27 == 0) goto L_0x0d0b
            r1 = r87
            if (r1 == 0) goto L_0x0d0b
            java.lang.String r2 = "reply"
            r3 = r71
            r1.put(r2, r3)     // Catch:{ JSONException -> 0x0d0b }
            java.lang.String r2 = "name"
            r3 = r80
            r1.put(r2, r3)     // Catch:{ JSONException -> 0x0d0b }
            r2 = r73
            r3 = r85
            r1.put(r3, r2)     // Catch:{ JSONException -> 0x0d0b }
            java.lang.String r2 = "max_date"
            r3 = r79
            r1.put(r2, r3)     // Catch:{ JSONException -> 0x0d0b }
            int r2 = java.lang.Math.abs(r27)     // Catch:{ JSONException -> 0x0d0b }
            r13 = r83
            r1.put(r13, r2)     // Catch:{ JSONException -> 0x0d0b }
            r2 = r82
            if (r2 == 0) goto L_0x0ce1
            java.lang.String r3 = "photo"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0d0b }
            r4.<init>()     // Catch:{ JSONException -> 0x0d0b }
            int r5 = r2.dc_id     // Catch:{ JSONException -> 0x0d0b }
            r4.append(r5)     // Catch:{ JSONException -> 0x0d0b }
            r4.append(r0)     // Catch:{ JSONException -> 0x0d0b }
            long r5 = r2.volume_id     // Catch:{ JSONException -> 0x0d0b }
            r4.append(r5)     // Catch:{ JSONException -> 0x0d0b }
            r4.append(r0)     // Catch:{ JSONException -> 0x0d0b }
            long r5 = r2.secret     // Catch:{ JSONException -> 0x0d0b }
            r4.append(r5)     // Catch:{ JSONException -> 0x0d0b }
            java.lang.String r0 = r4.toString()     // Catch:{ JSONException -> 0x0d0b }
            r1.put(r3, r0)     // Catch:{ JSONException -> 0x0d0b }
        L_0x0ce1:
            r2 = r88
            if (r2 == 0) goto L_0x0cea
            java.lang.String r0 = "msgs"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d0b }
        L_0x0cea:
            java.lang.String r0 = "type"
            if (r27 <= 0) goto L_0x0cf6
            java.lang.String r2 = "user"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d0b }
        L_0x0cf3:
            r12 = r75
            goto L_0x0d07
        L_0x0cf6:
            if (r39 != 0) goto L_0x0d01
            if (r31 == 0) goto L_0x0cfb
            goto L_0x0d01
        L_0x0cfb:
            java.lang.String r2 = "group"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d0b }
            goto L_0x0cf3
        L_0x0d01:
            java.lang.String r2 = "channel"
            r1.put(r0, r2)     // Catch:{ JSONException -> 0x0d0b }
            goto L_0x0cf3
        L_0x0d07:
            r12.put(r1)     // Catch:{ JSONException -> 0x0d0d }
            goto L_0x0d0d
        L_0x0d0b:
            r12 = r75
        L_0x0d0d:
            int r2 = r25 + 1
            r5 = r12
            r7 = r14
            r3 = r21
            r6 = r23
            r9 = r26
            r11 = r29
            r8 = r32
            r1 = r72
            r4 = r74
            r12 = r86
            r13 = 7
            r14 = r94
            goto L_0x00fa
        L_0x0d26:
            r72 = r1
            r21 = r3
            r74 = r4
            r14 = r7
            r32 = r8
            r13 = r10
            r86 = r12
            r22 = 0
            r12 = r5
            if (r21 == 0) goto L_0x0d83
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0d51
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "show summary with id "
            r0.append(r1)
            int r1 = r15.notificationId
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0d51:
            androidx.core.app.NotificationManagerCompat r0 = notificationManager     // Catch:{ SecurityException -> 0x0d5f }
            int r1 = r15.notificationId     // Catch:{ SecurityException -> 0x0d5f }
            r2 = r86
            r0.notify(r1, r2)     // Catch:{ SecurityException -> 0x0d5f }
            r89 = r12
            r90 = r13
            goto L_0x0d96
        L_0x0d5f:
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
            goto L_0x0d96
        L_0x0d83:
            r89 = r12
            r90 = r13
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0d96
            androidx.core.app.NotificationManagerCompat r0 = notificationManager
            int r1 = r15.notificationId
            r0.cancel(r1)
        L_0x0d96:
            r10 = 0
        L_0x0d97:
            int r0 = r32.size()
            if (r10 >= r0) goto L_0x0ddc
            r1 = r32
            long r2 = r1.keyAt(r10)
            java.util.HashSet<java.lang.Long> r0 = r15.openedInBubbleDialogs
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            boolean r0 = r0.contains(r2)
            if (r0 == 0) goto L_0x0db0
            goto L_0x0dd7
        L_0x0db0:
            java.lang.Object r0 = r1.valueAt(r10)
            java.lang.Integer r0 = (java.lang.Integer) r0
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0dce
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "cancel notification id "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x0dce:
            androidx.core.app.NotificationManagerCompat r2 = notificationManager
            int r0 = r0.intValue()
            r2.cancel(r0)
        L_0x0dd7:
            int r10 = r10 + 1
            r32 = r1
            goto L_0x0d97
        L_0x0ddc:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r1 = r14.size()
            r0.<init>(r1)
            int r1 = r14.size()
            r10 = 0
        L_0x0dea:
            if (r10 >= r1) goto L_0x0e43
            java.lang.Object r2 = r14.get(r10)
            org.telegram.messenger.NotificationsController$1NotificationHolder r2 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r2
            r0.clear()
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 29
            if (r3 < r4) goto L_0x0e28
            int r3 = r2.lowerId
            if (r3 == 0) goto L_0x0e28
            androidx.core.app.NotificationCompat$Builder r4 = r2.notification
            java.lang.String r5 = r2.name
            org.telegram.tgnet.TLRPC$User r6 = r2.user
            org.telegram.tgnet.TLRPC$Chat r7 = r2.chat
            long r8 = (long) r3
            r11 = r74
            java.lang.Object r8 = r11.get(r8)
            androidx.core.app.Person r8 = (androidx.core.app.Person) r8
            r94 = r93
            r95 = r4
            r96 = r3
            r97 = r5
            r98 = r6
            r99 = r7
            r100 = r8
            java.lang.String r3 = r94.createNotificationShortcut(r95, r96, r97, r98, r99, r100)
            if (r3 == 0) goto L_0x0e2a
            r0.add(r3)
            goto L_0x0e2a
        L_0x0e28:
            r11 = r74
        L_0x0e2a:
            r2.call()
            boolean r2 = r93.unsupportedNotificationShortcut()
            if (r2 != 0) goto L_0x0e3e
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L_0x0e3e
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            androidx.core.content.pm.ShortcutManagerCompat.removeDynamicShortcuts(r2, r0)
        L_0x0e3e:
            int r10 = r10 + 1
            r74 = r11
            goto L_0x0dea
        L_0x0e43:
            r6 = r89
            if (r6 == 0) goto L_0x0e67
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0e67 }
            r0.<init>()     // Catch:{ Exception -> 0x0e67 }
            r1 = r72
            r2 = r90
            r0.put(r2, r1)     // Catch:{ Exception -> 0x0e67 }
            java.lang.String r1 = "n"
            r0.put(r1, r6)     // Catch:{ Exception -> 0x0e67 }
            java.lang.String r1 = "/notify"
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0e67 }
            byte[] r0 = r0.getBytes()     // Catch:{ Exception -> 0x0e67 }
            java.lang.String r2 = "remote_notifications"
            org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r1, r0, r2)     // Catch:{ Exception -> 0x0e67 }
        L_0x0e67:
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
