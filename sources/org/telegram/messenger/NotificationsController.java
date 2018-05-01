package org.telegram.messenger;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC.TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.PopupNotificationActivity;

public class NotificationsController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = new NotificationsController[3];
    public static final String OTHER_NOTIFICATIONS_CHANNEL = "Other3";
    protected static AudioManager audioManager = ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO));
    public static long lastNoDataNotificationTime;
    private static NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    private static NotificationManager systemNotificationManager = ((NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification"));
    private AlarmManager alarmManager;
    private int currentAccount;
    private ArrayList<MessageObject> delayedPushMessages = new ArrayList();
    private boolean inChatSoundEnabled = true;
    private int lastBadgeCount = -1;
    private int lastButtonId = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
    private boolean lastNotificationIsNoData;
    private int lastOnlineFromOtherDevice = 0;
    private long lastSoundOutPlay;
    private long lastSoundPlay;
    private LongSparseArray<Integer> lastWearNotifiedMessageId = new LongSparseArray();
    private String launcherClassName;
    private Runnable notificationDelayRunnable;
    private WakeLock notificationDelayWakelock;
    private String notificationGroup;
    private int notificationId;
    private boolean notifyCheck = false;
    private long opened_dialog_id = 0;
    private int personal_count = 0;
    public ArrayList<MessageObject> popupMessages = new ArrayList();
    public ArrayList<MessageObject> popupReplyMessages = new ArrayList();
    private LongSparseArray<Integer> pushDialogs = new LongSparseArray();
    private LongSparseArray<Integer> pushDialogsOverrideMention = new LongSparseArray();
    private ArrayList<MessageObject> pushMessages = new ArrayList();
    private LongSparseArray<MessageObject> pushMessagesDict = new LongSparseArray();
    public boolean showBadgeNumber;
    private LongSparseArray<Point> smartNotificationsDialogs = new LongSparseArray();
    private int soundIn;
    private boolean soundInLoaded;
    private int soundOut;
    private boolean soundOutLoaded;
    private SoundPool soundPool;
    private int soundRecord;
    private boolean soundRecordLoaded;
    private int total_unread_count = 0;
    private LongSparseArray<Integer> wearNotificationsIds = new LongSparseArray();

    /* renamed from: org.telegram.messenger.NotificationsController$1 */
    class C04291 implements Runnable {
        C04291() {
        }

        public void run() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("delay reached");
            }
            if (!NotificationsController.this.delayedPushMessages.isEmpty()) {
                NotificationsController.this.showOrUpdateNotification(true);
                NotificationsController.this.delayedPushMessages.clear();
            } else if (NotificationsController.this.lastNotificationIsNoData) {
                NotificationsController.notificationManager.cancel(NotificationsController.this.notificationId);
            }
            try {
                if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
                    NotificationsController.this.notificationDelayWakelock.release();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController$1NotificationHolder */
    class AnonymousClass1NotificationHolder {
        int id;
        Notification notification;

        AnonymousClass1NotificationHolder(int i, Notification notification) {
            this.id = i;
            this.notification = notification;
        }

        void call() {
            NotificationsController.notificationManager.notify(this.id, this.notification);
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController$2 */
    class C04302 implements Runnable {
        C04302() {
        }

        public void run() {
            NotificationsController.this.opened_dialog_id = 0;
            int i = 0;
            NotificationsController.this.total_unread_count = 0;
            NotificationsController.this.personal_count = 0;
            NotificationsController.this.pushMessages.clear();
            NotificationsController.this.pushMessagesDict.clear();
            NotificationsController.this.pushDialogs.clear();
            NotificationsController.this.wearNotificationsIds.clear();
            NotificationsController.this.lastWearNotifiedMessageId.clear();
            NotificationsController.this.delayedPushMessages.clear();
            NotificationsController.this.notifyCheck = false;
            NotificationsController.this.lastBadgeCount = 0;
            try {
                if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
                    NotificationsController.this.notificationDelayWakelock.release();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
            Editor edit = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount).edit();
            edit.clear();
            edit.commit();
            if (VERSION.SDK_INT >= 26) {
                try {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(NotificationsController.this.currentAccount);
                    stringBuilder.append("channel");
                    String stringBuilder2 = stringBuilder.toString();
                    List notificationChannels = NotificationsController.systemNotificationManager.getNotificationChannels();
                    int size = notificationChannels.size();
                    while (i < size) {
                        String id = ((NotificationChannel) notificationChannels.get(i)).getId();
                        if (id.startsWith(stringBuilder2)) {
                            NotificationsController.systemNotificationManager.deleteNotificationChannel(id);
                        }
                        i++;
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController$5 */
    class C04345 implements Runnable {
        C04345() {
        }

        public void run() {
            final ArrayList arrayList = new ArrayList();
            for (int i = 0; i < NotificationsController.this.pushMessages.size(); i++) {
                MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessages.get(i);
                long dialogId = messageObject.getDialogId();
                if (!((messageObject.messageOwner.mentioned && (messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) || ((int) dialogId) == 0)) {
                    if (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup()) {
                        arrayList.add(0, messageObject);
                    }
                }
            }
            if (!arrayList.isEmpty() && !AndroidUtilities.needShowPasscode(false)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationsController.this.popupReplyMessages = arrayList;
                        Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                        intent.putExtra("force", true);
                        intent.putExtra("currentAccount", NotificationsController.this.currentAccount);
                        intent.setFlags(268763140);
                        ApplicationLoader.applicationContext.startActivity(intent);
                        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                    }
                });
            }
        }
    }

    static {
        if (VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
            NotificationChannel notificationChannel = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, "Other", 3);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            systemNotificationManager.createNotificationChannel(notificationChannel);
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
        this.currentAccount = i;
        this.notificationId = this.currentAccount + 1;
        i = new StringBuilder();
        i.append("messages");
        i.append(this.currentAccount == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(this.currentAccount));
        this.notificationGroup = i.toString();
        i = MessagesController.getNotificationsSettings(this.currentAccount);
        this.inChatSoundEnabled = i.getBoolean("EnableInChatSound", true);
        this.showBadgeNumber = i.getBoolean("badgeNumber", true);
        notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
        systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
        try {
            audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm");
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        try {
            this.notificationDelayWakelock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "lock");
            this.notificationDelayWakelock.setReferenceCounted(false);
        } catch (Throwable e22) {
            FileLog.m3e(e22);
        }
        this.notificationDelayRunnable = new C04291();
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        notificationsQueue.postRunnable(new C04302());
    }

    public void setInChatSoundEnabled(boolean z) {
        this.inChatSoundEnabled = z;
    }

    public void setOpenedDialogId(final long j) {
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                NotificationsController.this.opened_dialog_id = j;
            }
        });
    }

    public void setLastOnlineFromOtherDevice(final int i) {
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                if (BuildVars.LOGS_ENABLED) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("set last online from other device = ");
                    stringBuilder.append(i);
                    FileLog.m0d(stringBuilder.toString());
                }
                NotificationsController.this.lastOnlineFromOtherDevice = i;
            }
        });
    }

    public void removeNotificationsForDialog(long j) {
        getInstance(this.currentAccount).processReadMessages(null, j, 0, ConnectionsManager.DEFAULT_DATACENTER_ID, false);
        LongSparseArray longSparseArray = new LongSparseArray();
        longSparseArray.put(j, Integer.valueOf(0));
        getInstance(this.currentAccount).processDialogsUpdateRead(longSparseArray);
    }

    public boolean hasMessagesToReply() {
        for (int i = 0; i < this.pushMessages.size(); i++) {
            MessageObject messageObject = (MessageObject) this.pushMessages.get(i);
            long dialogId = messageObject.getDialogId();
            if (!((messageObject.messageOwner.mentioned && (messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) || ((int) dialogId) == 0)) {
                if (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new C04345());
    }

    public void removeDeletedMessagesFromNotifications(final SparseArray<ArrayList<Integer>> sparseArray) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$6$1 */
            class C04351 implements Runnable {
                C04351() {
                }

                public void run() {
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        NotificationsController.this.popupMessages.remove(arrayList.get(i));
                    }
                }
            }

            /* renamed from: org.telegram.messenger.NotificationsController$6$2 */
            class C04362 implements Runnable {
                C04362() {
                }

                public void run() {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                }
            }

            public void run() {
                int access$700 = NotificationsController.this.total_unread_count;
                MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                int i = 0;
                int i2 = 0;
                while (true) {
                    boolean z = true;
                    if (i2 >= sparseArray.size()) {
                        break;
                    }
                    int i3;
                    int keyAt = sparseArray.keyAt(i2);
                    long j = (long) (-keyAt);
                    ArrayList arrayList = (ArrayList) sparseArray.get(keyAt);
                    Integer num = (Integer) NotificationsController.this.pushDialogs.get(j);
                    if (num == null) {
                        num = Integer.valueOf(i);
                    }
                    int i4 = i;
                    Integer num2 = num;
                    while (i4 < arrayList.size()) {
                        i3 = i2;
                        long intValue = ((long) ((Integer) arrayList.get(i4)).intValue()) | (((long) keyAt) << 32);
                        MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessagesDict.get(intValue);
                        if (messageObject != null) {
                            NotificationsController.this.pushMessagesDict.remove(intValue);
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            NotificationsController.this.pushMessages.remove(messageObject);
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            arrayList.add(messageObject);
                            num2 = Integer.valueOf(num2.intValue() - 1);
                        }
                        i4++;
                        i2 = i3;
                    }
                    i3 = i2;
                    if (num2.intValue() <= 0) {
                        num2 = Integer.valueOf(0);
                        NotificationsController.this.smartNotificationsDialogs.remove(j);
                    }
                    if (!num2.equals(num)) {
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - num.intValue();
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + num2.intValue();
                        NotificationsController.this.pushDialogs.put(j, num2);
                    }
                    if (num2.intValue() == 0) {
                        NotificationsController.this.pushDialogs.remove(j);
                        NotificationsController.this.pushDialogsOverrideMention.remove(j);
                    }
                    i2 = i3 + 1;
                    i = 0;
                }
                if (!arrayList.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C04351());
                }
                if (access$700 != NotificationsController.this.total_unread_count) {
                    if (NotificationsController.this.notifyCheck) {
                        NotificationsController notificationsController = NotificationsController.this;
                        if (NotificationsController.this.lastOnlineFromOtherDevice <= ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime()) {
                            z = false;
                        }
                        notificationsController.scheduleNotificationDelay(z);
                    } else {
                        NotificationsController.this.delayedPushMessages.clear();
                        NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                    }
                    AndroidUtilities.runOnUIThread(new C04362());
                }
                NotificationsController.this.notifyCheck = false;
                if (NotificationsController.this.showBadgeNumber) {
                    NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
                }
            }
        });
    }

    public void removeDeletedHisoryFromNotifications(final SparseIntArray sparseIntArray) {
        final ArrayList arrayList = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$7$1 */
            class C04381 implements Runnable {
                C04381() {
                }

                public void run() {
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        NotificationsController.this.popupMessages.remove(arrayList.get(i));
                    }
                }
            }

            /* renamed from: org.telegram.messenger.NotificationsController$7$2 */
            class C04392 implements Runnable {
                C04392() {
                }

                public void run() {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                }
            }

            public void run() {
                int access$700 = NotificationsController.this.total_unread_count;
                MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                int i = 0;
                while (true) {
                    boolean z = true;
                    if (i >= sparseIntArray.size()) {
                        break;
                    }
                    int keyAt = sparseIntArray.keyAt(i);
                    long j = (long) (-keyAt);
                    keyAt = sparseIntArray.get(keyAt);
                    Integer num = (Integer) NotificationsController.this.pushDialogs.get(j);
                    if (num == null) {
                        num = Integer.valueOf(0);
                    }
                    int i2 = 0;
                    Integer num2 = num;
                    while (i2 < NotificationsController.this.pushMessages.size()) {
                        MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessages.get(i2);
                        if (messageObject.getDialogId() == j && messageObject.getId() <= keyAt) {
                            NotificationsController.this.pushMessagesDict.remove(messageObject.getIdWithChannel());
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            NotificationsController.this.pushMessages.remove(messageObject);
                            i2--;
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            arrayList.add(messageObject);
                            num2 = Integer.valueOf(num2.intValue() - 1);
                        }
                        i2++;
                    }
                    if (num2.intValue() <= 0) {
                        num2 = Integer.valueOf(0);
                        NotificationsController.this.smartNotificationsDialogs.remove(j);
                    }
                    if (!num2.equals(num)) {
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - num.intValue();
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + num2.intValue();
                        NotificationsController.this.pushDialogs.put(j, num2);
                    }
                    if (num2.intValue() == 0) {
                        NotificationsController.this.pushDialogs.remove(j);
                        NotificationsController.this.pushDialogsOverrideMention.remove(j);
                    }
                    i++;
                }
                if (arrayList.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C04381());
                }
                if (access$700 != NotificationsController.this.total_unread_count) {
                    if (NotificationsController.this.notifyCheck) {
                        NotificationsController notificationsController = NotificationsController.this;
                        if (NotificationsController.this.lastOnlineFromOtherDevice <= ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime()) {
                            z = false;
                        }
                        notificationsController.scheduleNotificationDelay(z);
                    } else {
                        NotificationsController.this.delayedPushMessages.clear();
                        NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                    }
                    AndroidUtilities.runOnUIThread(new C04392());
                }
                NotificationsController.this.notifyCheck = false;
                if (NotificationsController.this.showBadgeNumber) {
                    NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
                }
            }
        });
    }

    public void processReadMessages(SparseLongArray sparseLongArray, long j, int i, int i2, boolean z) {
        final ArrayList arrayList = new ArrayList(0);
        final SparseLongArray sparseLongArray2 = sparseLongArray;
        final long j2 = j;
        final int i3 = i2;
        final int i4 = i;
        final boolean z2 = z;
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$8$1 */
            class C04411 implements Runnable {
                C04411() {
                }

                public void run() {
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        NotificationsController.this.popupMessages.remove(arrayList.get(i));
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                }
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                int i;
                if (sparseLongArray2 != null) {
                    for (i = 0; i < sparseLongArray2.size(); i++) {
                        int keyAt = sparseLongArray2.keyAt(i);
                        long j = sparseLongArray2.get(keyAt);
                        int i2 = 0;
                        while (i2 < NotificationsController.this.pushMessages.size()) {
                            MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessages.get(i2);
                            if (messageObject.getDialogId() == ((long) keyAt) && messageObject.getId() <= ((int) j)) {
                                if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                    NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                                }
                                arrayList.add(messageObject);
                                long id = (long) messageObject.getId();
                                NotificationsController.this.pushMessagesDict.remove(messageObject.messageOwner.to_id.channel_id != 0 ? id | (((long) messageObject.messageOwner.to_id.channel_id) << 32) : id);
                                NotificationsController.this.delayedPushMessages.remove(messageObject);
                                NotificationsController.this.pushMessages.remove(i2);
                                i2--;
                            }
                            i2++;
                        }
                    }
                }
                if (!(j2 == 0 || (i3 == 0 && i4 == 0))) {
                    i = 0;
                    while (i < NotificationsController.this.pushMessages.size()) {
                        MessageObject messageObject2 = (MessageObject) NotificationsController.this.pushMessages.get(i);
                        if (messageObject2.getDialogId() == j2) {
                            int i3;
                            if (i4 == 0) {
                                if (z2) {
                                    if (messageObject2.getId() != i3) {
                                        if (i3 < 0) {
                                        }
                                        i3 = 0;
                                        if (i3 != 0) {
                                            if (NotificationsController.this.isPersonalMessage(messageObject2)) {
                                                NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                                            }
                                            NotificationsController.this.pushMessages.remove(i);
                                            NotificationsController.this.delayedPushMessages.remove(messageObject2);
                                            arrayList.add(messageObject2);
                                            j = (long) messageObject2.getId();
                                            if (messageObject2.messageOwner.to_id.channel_id != 0) {
                                                j |= ((long) messageObject2.messageOwner.to_id.channel_id) << 32;
                                            }
                                            NotificationsController.this.pushMessagesDict.remove(j);
                                            i--;
                                        }
                                    }
                                } else if (messageObject2.getId() > i3) {
                                    if (i3 < 0) {
                                    }
                                    i3 = 0;
                                    if (i3 != 0) {
                                        if (NotificationsController.this.isPersonalMessage(messageObject2)) {
                                            NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                                        }
                                        NotificationsController.this.pushMessages.remove(i);
                                        NotificationsController.this.delayedPushMessages.remove(messageObject2);
                                        arrayList.add(messageObject2);
                                        j = (long) messageObject2.getId();
                                        if (messageObject2.messageOwner.to_id.channel_id != 0) {
                                            j |= ((long) messageObject2.messageOwner.to_id.channel_id) << 32;
                                        }
                                        NotificationsController.this.pushMessagesDict.remove(j);
                                        i--;
                                    }
                                }
                            }
                            i3 = 1;
                            if (i3 != 0) {
                                if (NotificationsController.this.isPersonalMessage(messageObject2)) {
                                    NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                                }
                                NotificationsController.this.pushMessages.remove(i);
                                NotificationsController.this.delayedPushMessages.remove(messageObject2);
                                arrayList.add(messageObject2);
                                j = (long) messageObject2.getId();
                                if (messageObject2.messageOwner.to_id.channel_id != 0) {
                                    j |= ((long) messageObject2.messageOwner.to_id.channel_id) << 32;
                                }
                                NotificationsController.this.pushMessagesDict.remove(j);
                                i--;
                            }
                        }
                        i++;
                    }
                }
                if (!arrayList.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C04411());
                }
            }
        });
    }

    public void processNewMessages(ArrayList<MessageObject> arrayList, boolean z, boolean z2) {
        if (!arrayList.isEmpty()) {
            final ArrayList arrayList2 = new ArrayList(0);
            final ArrayList<MessageObject> arrayList3 = arrayList;
            final boolean z3 = z2;
            final boolean z4 = z;
            notificationsQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.NotificationsController$9$2 */
                class C04442 implements Runnable {
                    C04442() {
                    }

                    public void run() {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                    }
                }

                public void run() {
                    LongSparseArray longSparseArray = new LongSparseArray();
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                    boolean z = true;
                    boolean z2 = notificationsSettings.getBoolean("PinnedMessages", true);
                    int i = 0;
                    Object obj = null;
                    int i2 = 0;
                    while (i < arrayList3.size()) {
                        int i3;
                        boolean z3;
                        MessageObject messageObject = (MessageObject) arrayList3.get(i);
                        long id = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            id |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        MessageObject messageObject2 = (MessageObject) NotificationsController.this.pushMessagesDict.get(id);
                        if (messageObject2 == null) {
                            long dialogId = messageObject.getDialogId();
                            if (dialogId != NotificationsController.this.opened_dialog_id || !ApplicationLoader.isScreenOn) {
                                long j;
                                int i4;
                                if (!messageObject.messageOwner.mentioned) {
                                    i3 = i;
                                    j = dialogId;
                                } else if (z2 || !(messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) {
                                    i3 = i;
                                    j = (long) messageObject.messageOwner.from_id;
                                }
                                if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                    NotificationsController.this.personal_count = NotificationsController.this.personal_count + 1;
                                }
                                int i5 = (int) j;
                                boolean z4 = i5 < 0 ? z : false;
                                int indexOfKey = longSparseArray.indexOfKey(j);
                                if (indexOfKey >= 0) {
                                    z = ((Boolean) longSparseArray.valueAt(indexOfKey)).booleanValue();
                                    z3 = z2;
                                } else {
                                    indexOfKey = NotificationsController.this.getNotifyOverride(notificationsSettings, j);
                                    if (indexOfKey != 2) {
                                        z3 = z2;
                                        if ((notificationsSettings.getBoolean("EnableAll", true) && (!z4 || notificationsSettings.getBoolean("EnableGroup", true))) || indexOfKey != 0) {
                                            z = true;
                                            longSparseArray.put(j, Boolean.valueOf(z));
                                        }
                                    } else {
                                        z3 = z2;
                                    }
                                    z = false;
                                    longSparseArray.put(j, Boolean.valueOf(z));
                                }
                                if (i5 != 0) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("custom_");
                                    stringBuilder.append(j);
                                    if (notificationsSettings.getBoolean(stringBuilder.toString(), false)) {
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append("popup_");
                                        stringBuilder.append(j);
                                        i4 = notificationsSettings.getInt(stringBuilder.toString(), 0);
                                    } else {
                                        i4 = 0;
                                    }
                                    if (i4 == 0) {
                                        i4 = notificationsSettings.getInt(i5 < 0 ? "popupGroup" : "popupAll", 0);
                                    } else if (i4 == 1) {
                                        i4 = 3;
                                    } else if (i4 == 2) {
                                        i4 = 0;
                                    }
                                } else {
                                    i4 = i2;
                                }
                                if (!(i4 == 0 || messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
                                    i4 = 0;
                                }
                                if (z) {
                                    if (i4 != 0) {
                                        i5 = 0;
                                        arrayList2.add(0, messageObject);
                                    } else {
                                        i5 = 0;
                                    }
                                    NotificationsController.this.delayedPushMessages.add(messageObject);
                                    NotificationsController.this.pushMessages.add(i5, messageObject);
                                    NotificationsController.this.pushMessagesDict.put(id, messageObject);
                                    if (dialogId != j) {
                                        NotificationsController.this.pushDialogsOverrideMention.put(dialogId, Integer.valueOf(1));
                                    }
                                }
                                i2 = i4;
                                obj = 1;
                                i = i3 + 1;
                                z2 = z3;
                                z = true;
                            } else if (!z3) {
                                NotificationsController.this.playInChatSound();
                            }
                        } else if (messageObject2.isFcmMessage()) {
                            NotificationsController.this.pushMessagesDict.put(id, messageObject);
                            int indexOf = NotificationsController.this.pushMessages.indexOf(messageObject2);
                            if (indexOf >= 0) {
                                NotificationsController.this.pushMessages.set(indexOf, messageObject);
                            }
                        }
                        z3 = z2;
                        i3 = i;
                        i = i3 + 1;
                        z2 = z3;
                        z = true;
                    }
                    if (obj != null) {
                        NotificationsController.this.notifyCheck = z4;
                    }
                    if (!(arrayList2.isEmpty() || AndroidUtilities.needShowPasscode(false))) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationsController.this.popupMessages.addAll(0, arrayList2);
                                if (!ApplicationLoader.mainInterfacePaused && (ApplicationLoader.isScreenOn || SharedConfig.isWaitingForPasscodeEnter)) {
                                    return;
                                }
                                if (i2 == 3 || ((i2 == 1 && ApplicationLoader.isScreenOn) || (i2 == 2 && !ApplicationLoader.isScreenOn))) {
                                    Intent intent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                                    intent.setFlags(268763140);
                                    ApplicationLoader.applicationContext.startActivity(intent);
                                }
                            }
                        });
                    }
                    if (obj != null && z3) {
                        Integer num;
                        boolean z5;
                        long dialogId2 = ((MessageObject) arrayList3.get(0)).getDialogId();
                        int access$700 = NotificationsController.this.total_unread_count;
                        int access$2600 = NotificationsController.this.getNotifyOverride(notificationsSettings, dialogId2);
                        if (NotificationsController.this.notifyCheck) {
                            num = (Integer) NotificationsController.this.pushDialogsOverrideMention.get(dialogId2);
                            if (num != null && num.intValue() == 1) {
                                NotificationsController.this.pushDialogsOverrideMention.put(dialogId2, Integer.valueOf(0));
                                access$2600 = 1;
                            }
                        }
                        Object obj2 = (access$2600 == 2 || ((!notificationsSettings.getBoolean("EnableAll", true) || (((int) dialogId2) < 0 && !notificationsSettings.getBoolean("EnableGroup", true))) && access$2600 == 0)) ? null : 1;
                        Integer num2 = (Integer) NotificationsController.this.pushDialogs.get(dialogId2);
                        if (num2 != null) {
                            z5 = true;
                            i = num2.intValue() + 1;
                        } else {
                            z5 = true;
                            i = 1;
                        }
                        num = Integer.valueOf(i);
                        if (obj2 != null) {
                            if (num2 != null) {
                                NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - num2.intValue();
                            }
                            NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + num.intValue();
                            NotificationsController.this.pushDialogs.put(dialogId2, num);
                        }
                        if (access$700 != NotificationsController.this.total_unread_count) {
                            if (NotificationsController.this.notifyCheck) {
                                NotificationsController notificationsController = NotificationsController.this;
                                if (NotificationsController.this.lastOnlineFromOtherDevice <= ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime()) {
                                    z5 = false;
                                }
                                notificationsController.scheduleNotificationDelay(z5);
                            } else {
                                NotificationsController.this.delayedPushMessages.clear();
                                NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                            }
                            AndroidUtilities.runOnUIThread(new C04442());
                        }
                        NotificationsController.this.notifyCheck = false;
                        if (NotificationsController.this.showBadgeNumber) {
                            NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
                        }
                    }
                }
            });
        }
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(final LongSparseArray<Integer> longSparseArray) {
        final ArrayList arrayList = new ArrayList();
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$10$1 */
            class C04241 implements Runnable {
                C04241() {
                }

                public void run() {
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        NotificationsController.this.popupMessages.remove(arrayList.get(i));
                    }
                }
            }

            /* renamed from: org.telegram.messenger.NotificationsController$10$2 */
            class C04252 implements Runnable {
                C04252() {
                }

                public void run() {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                }
            }

            public void run() {
                int access$700 = NotificationsController.this.total_unread_count;
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                int i = 0;
                while (true) {
                    boolean z = true;
                    if (i >= longSparseArray.size()) {
                        break;
                    }
                    Integer num;
                    long keyAt = longSparseArray.keyAt(i);
                    int access$2600 = NotificationsController.this.getNotifyOverride(notificationsSettings, keyAt);
                    if (NotificationsController.this.notifyCheck) {
                        num = (Integer) NotificationsController.this.pushDialogsOverrideMention.get(keyAt);
                        if (num != null && num.intValue() == 1) {
                            NotificationsController.this.pushDialogsOverrideMention.put(keyAt, Integer.valueOf(0));
                            access$2600 = 1;
                        }
                    }
                    boolean z2 = access$2600 != 2 && ((notificationsSettings.getBoolean("EnableAll", true) && (((int) keyAt) >= 0 || notificationsSettings.getBoolean("EnableGroup", true))) || access$2600 != 0);
                    num = (Integer) NotificationsController.this.pushDialogs.get(keyAt);
                    Integer num2 = (Integer) longSparseArray.get(keyAt);
                    if (num2.intValue() == 0) {
                        NotificationsController.this.smartNotificationsDialogs.remove(keyAt);
                    }
                    if (num2.intValue() < 0) {
                        if (num == null) {
                            i++;
                        } else {
                            num2 = Integer.valueOf(num.intValue() + num2.intValue());
                        }
                    }
                    if ((z2 || num2.intValue() == 0) && num != null) {
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - num.intValue();
                    }
                    if (num2.intValue() == 0) {
                        NotificationsController.this.pushDialogs.remove(keyAt);
                        NotificationsController.this.pushDialogsOverrideMention.remove(keyAt);
                        access$2600 = 0;
                        while (access$2600 < NotificationsController.this.pushMessages.size()) {
                            MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessages.get(access$2600);
                            if (messageObject.getDialogId() == keyAt) {
                                if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                    NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                                }
                                NotificationsController.this.pushMessages.remove(access$2600);
                                access$2600--;
                                NotificationsController.this.delayedPushMessages.remove(messageObject);
                                long id = (long) messageObject.getId();
                                NotificationsController.this.pushMessagesDict.remove(messageObject.messageOwner.to_id.channel_id != 0 ? id | (((long) messageObject.messageOwner.to_id.channel_id) << 32) : id);
                                arrayList.add(messageObject);
                            }
                            access$2600++;
                        }
                    } else if (z2) {
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + num2.intValue();
                        NotificationsController.this.pushDialogs.put(keyAt, num2);
                    }
                    i++;
                }
                if (!arrayList.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C04241());
                }
                if (access$700 != NotificationsController.this.total_unread_count) {
                    if (NotificationsController.this.notifyCheck) {
                        NotificationsController notificationsController = NotificationsController.this;
                        if (NotificationsController.this.lastOnlineFromOtherDevice <= ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime()) {
                            z = false;
                        }
                        notificationsController.scheduleNotificationDelay(z);
                    } else {
                        NotificationsController.this.delayedPushMessages.clear();
                        NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                    }
                    AndroidUtilities.runOnUIThread(new C04252());
                }
                NotificationsController.this.notifyCheck = false;
                if (NotificationsController.this.showBadgeNumber) {
                    NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
                }
            }
        });
    }

    public void processLoadedUnreadMessages(final LongSparseArray<Integer> longSparseArray, final ArrayList<Message> arrayList, ArrayList<User> arrayList2, ArrayList<Chat> arrayList3, ArrayList<EncryptedChat> arrayList4) {
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList2, true);
        MessagesController.getInstance(this.currentAccount).putChats(arrayList3, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(arrayList4, true);
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$11$1 */
            class C04261 implements Runnable {
                C04261() {
                }

                public void run() {
                    if (NotificationsController.this.total_unread_count == 0) {
                        NotificationsController.this.popupMessages.clear();
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                }
            }

            public void run() {
                int i;
                NotificationsController.this.pushDialogs.clear();
                NotificationsController.this.pushMessages.clear();
                NotificationsController.this.pushMessagesDict.clear();
                boolean z = false;
                NotificationsController.this.total_unread_count = 0;
                NotificationsController.this.personal_count = 0;
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                LongSparseArray longSparseArray = new LongSparseArray();
                if (arrayList != null) {
                    for (i = 0; i < arrayList.size(); i++) {
                        Message message = (Message) arrayList.get(i);
                        long j = (long) message.id;
                        if (message.to_id.channel_id != 0) {
                            j |= ((long) message.to_id.channel_id) << 32;
                        }
                        if (NotificationsController.this.pushMessagesDict.indexOfKey(j) < 0) {
                            boolean booleanValue;
                            MessageObject messageObject = new MessageObject(NotificationsController.this.currentAccount, message, false);
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                NotificationsController.this.personal_count = NotificationsController.this.personal_count + 1;
                            }
                            long dialogId = messageObject.getDialogId();
                            long j2 = messageObject.messageOwner.mentioned ? (long) messageObject.messageOwner.from_id : dialogId;
                            int indexOfKey = longSparseArray.indexOfKey(j2);
                            if (indexOfKey >= 0) {
                                booleanValue = ((Boolean) longSparseArray.valueAt(indexOfKey)).booleanValue();
                            } else {
                                indexOfKey = NotificationsController.this.getNotifyOverride(notificationsSettings, j2);
                                booleanValue = indexOfKey != 2 && ((notificationsSettings.getBoolean("EnableAll", true) && (((int) j2) >= 0 || notificationsSettings.getBoolean("EnableGroup", true))) || indexOfKey != 0);
                                longSparseArray.put(j2, Boolean.valueOf(booleanValue));
                            }
                            if (booleanValue) {
                                if (j2 != NotificationsController.this.opened_dialog_id || !ApplicationLoader.isScreenOn) {
                                    NotificationsController.this.pushMessagesDict.put(j, messageObject);
                                    NotificationsController.this.pushMessages.add(0, messageObject);
                                    if (dialogId != j2) {
                                        NotificationsController.this.pushDialogsOverrideMention.put(dialogId, Integer.valueOf(1));
                                    }
                                }
                            }
                        }
                    }
                }
                for (i = 0; i < longSparseArray.size(); i++) {
                    boolean booleanValue2;
                    long keyAt = longSparseArray.keyAt(i);
                    int indexOfKey2 = longSparseArray.indexOfKey(keyAt);
                    if (indexOfKey2 >= 0) {
                        booleanValue2 = ((Boolean) longSparseArray.valueAt(indexOfKey2)).booleanValue();
                    } else {
                        indexOfKey2 = NotificationsController.this.getNotifyOverride(notificationsSettings, keyAt);
                        Integer num = (Integer) NotificationsController.this.pushDialogsOverrideMention.get(keyAt);
                        if (num != null && num.intValue() == 1) {
                            NotificationsController.this.pushDialogsOverrideMention.put(keyAt, Integer.valueOf(0));
                            indexOfKey2 = 1;
                        }
                        booleanValue2 = indexOfKey2 != 2 && ((notificationsSettings.getBoolean("EnableAll", true) && (((int) keyAt) >= 0 || notificationsSettings.getBoolean("EnableGroup", true))) || indexOfKey2 != 0);
                        longSparseArray.put(keyAt, Boolean.valueOf(booleanValue2));
                    }
                    if (booleanValue2) {
                        indexOfKey2 = ((Integer) longSparseArray.valueAt(i)).intValue();
                        NotificationsController.this.pushDialogs.put(keyAt, Integer.valueOf(indexOfKey2));
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + indexOfKey2;
                    }
                }
                AndroidUtilities.runOnUIThread(new C04261());
                NotificationsController notificationsController = NotificationsController.this;
                if (SystemClock.uptimeMillis() / 1000 < 60) {
                    z = true;
                }
                notificationsController.showOrUpdateNotification(z);
                if (NotificationsController.this.showBadgeNumber) {
                    NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
                }
            }
        });
    }

    private int getTotalAllUnreadCount() {
        int i = 0;
        int i2 = 0;
        while (i < 3) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                NotificationsController instance = getInstance(i);
                if (instance.showBadgeNumber) {
                    i2 += instance.total_unread_count;
                }
            }
            i++;
        }
        return i2;
    }

    public void setBadgeEnabled(boolean z) {
        this.showBadgeNumber = z;
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
            }
        });
    }

    private void setBadge(int i) {
        if (this.lastBadgeCount != i) {
            this.lastBadgeCount = i;
            NotificationBadge.applyCount(i);
        }
    }

    private String getShortStringForMessage(MessageObject messageObject) {
        if (!(messageObject.isMediaEmpty() || TextUtils.isEmpty(messageObject.messageOwner.message))) {
            StringBuilder stringBuilder;
            if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("\ud83d\uddbc ");
                stringBuilder.append(messageObject.messageOwner.message);
                return stringBuilder.toString();
            } else if (messageObject.isVideo()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("\ud83d\udcf9 ");
                stringBuilder.append(messageObject.messageOwner.message);
                return stringBuilder.toString();
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                if (messageObject.isGif()) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("\ud83c\udfac ");
                    stringBuilder.append(messageObject.messageOwner.message);
                    return stringBuilder.toString();
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("\ud83d\udcce ");
                stringBuilder.append(messageObject.messageOwner.message);
                return stringBuilder.toString();
            }
        }
        return messageObject.messageText.toString();
    }

    private String getStringForMessage(MessageObject messageObject, boolean z, boolean[] zArr) {
        NotificationsController notificationsController = this;
        MessageObject messageObject2 = messageObject;
        if (!AndroidUtilities.needShowPasscode(false)) {
            if (!SharedConfig.isWaitingForPasscodeEnter) {
                long j = messageObject2.messageOwner.dialog_id;
                int i = messageObject2.messageOwner.to_id.chat_id != 0 ? messageObject2.messageOwner.to_id.chat_id : messageObject2.messageOwner.to_id.channel_id;
                int i2 = messageObject2.messageOwner.to_id.user_id;
                if (messageObject.isFcmMessage()) {
                    if (i != 0 || i2 == 0) {
                        if (!(i == 0 || MessagesController.getNotificationsSettings(notificationsController.currentAccount).getBoolean("EnablePreviewGroup", true))) {
                            if (messageObject.isMegagroup() || messageObject2.messageOwner.to_id.channel_id == 0) {
                                return LocaleController.formatString("NotificationMessageGroupNoText", C0446R.string.NotificationMessageGroupNoText, messageObject2.localUserName, messageObject2.localName);
                            }
                            return LocaleController.formatString("ChannelMessageNoText", C0446R.string.ChannelMessageNoText, messageObject2.localName);
                        }
                    } else if (!MessagesController.getNotificationsSettings(notificationsController.currentAccount).getBoolean("EnablePreviewAll", true)) {
                        return LocaleController.formatString("NotificationMessageNoText", C0446R.string.NotificationMessageNoText, messageObject2.localName);
                    }
                    zArr[0] = true;
                    return (String) messageObject2.messageText;
                }
                String userName;
                Chat chat;
                int i3;
                StringBuilder stringBuilder;
                int i4;
                User user;
                String userName2;
                String formatString;
                User user2;
                if (i2 == 0) {
                    if (!messageObject.isFromUser()) {
                        if (messageObject.getId() >= 0) {
                            i2 = -i;
                        }
                    }
                    i2 = messageObject2.messageOwner.from_id;
                } else if (i2 == UserConfig.getInstance(notificationsController.currentAccount).getClientUserId()) {
                    i2 = messageObject2.messageOwner.from_id;
                }
                if (j == 0) {
                    if (i != 0) {
                        j = (long) (-i);
                    } else if (i2 != 0) {
                        j = (long) i2;
                    }
                }
                String str = null;
                if (i2 > 0) {
                    User user3 = MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(i2));
                    if (user3 != null) {
                        userName = UserObject.getUserName(user3);
                        if (userName != null) {
                            return null;
                        }
                        if (i != 0) {
                            chat = MessagesController.getInstance(notificationsController.currentAccount).getChat(Integer.valueOf(i));
                            if (chat == null) {
                                return null;
                            }
                        }
                        chat = null;
                        if (((int) j) == 0) {
                            str = LocaleController.getString("YouHaveNewMessage", C0446R.string.YouHaveNewMessage);
                        } else {
                            Object[] objArr;
                            StringBuilder stringBuilder2;
                            if (i == 0 || i2 == 0) {
                                if (i != 0) {
                                    if (MessagesController.getNotificationsSettings(notificationsController.currentAccount).getBoolean("EnablePreviewGroup", true)) {
                                        str = (ChatObject.isChannel(chat) || chat.megagroup) ? LocaleController.formatString("NotificationMessageGroupNoText", C0446R.string.NotificationMessageGroupNoText, userName, chat.title) : LocaleController.formatString("ChannelMessageNoText", C0446R.string.ChannelMessageNoText, userName);
                                    } else if (messageObject2.messageOwner instanceof TL_messageService) {
                                        if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatAddUser)) {
                                            i3 = messageObject2.messageOwner.action.user_id;
                                            if (i3 == 0 && messageObject2.messageOwner.action.users.size() == 1) {
                                                i3 = ((Integer) messageObject2.messageOwner.action.users.get(0)).intValue();
                                            }
                                            if (i3 == 0) {
                                                stringBuilder = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                                                for (i4 = 0; i4 < messageObject2.messageOwner.action.users.size(); i4++) {
                                                    user = MessagesController.getInstance(notificationsController.currentAccount).getUser((Integer) messageObject2.messageOwner.action.users.get(i4));
                                                    if (user == null) {
                                                        userName2 = UserObject.getUserName(user);
                                                        if (stringBuilder.length() != 0) {
                                                            stringBuilder.append(", ");
                                                        }
                                                        stringBuilder.append(userName2);
                                                    }
                                                }
                                                formatString = LocaleController.formatString("NotificationGroupAddMember", C0446R.string.NotificationGroupAddMember, userName, chat.title, stringBuilder.toString());
                                            } else if (messageObject2.messageOwner.to_id.channel_id == 0 && !chat.megagroup) {
                                                formatString = LocaleController.formatString("ChannelAddedByNotification", C0446R.string.ChannelAddedByNotification, userName, chat.title);
                                            } else if (i3 == UserConfig.getInstance(notificationsController.currentAccount).getClientUserId()) {
                                                formatString = LocaleController.formatString("NotificationInvitedToGroup", C0446R.string.NotificationInvitedToGroup, userName, chat.title);
                                            } else {
                                                user2 = MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(i3));
                                                if (user2 != null) {
                                                    return null;
                                                }
                                                if (i2 == user2.id) {
                                                    formatString = LocaleController.formatString("NotificationGroupAddMember", C0446R.string.NotificationGroupAddMember, userName, chat.title, UserObject.getUserName(user2));
                                                } else if (chat.megagroup) {
                                                    formatString = LocaleController.formatString("NotificationGroupAddSelfMega", C0446R.string.NotificationGroupAddSelfMega, userName, chat.title);
                                                } else {
                                                    formatString = LocaleController.formatString("NotificationGroupAddSelf", C0446R.string.NotificationGroupAddSelf, userName, chat.title);
                                                }
                                            }
                                        } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatJoinedByLink)) {
                                            str = LocaleController.formatString("NotificationInvitedToGroupByLink", C0446R.string.NotificationInvitedToGroupByLink, userName, chat.title);
                                        } else if (messageObject2.messageOwner.action instanceof TL_messageActionChatEditTitle) {
                                            str = LocaleController.formatString("NotificationEditedGroupName", C0446R.string.NotificationEditedGroupName, userName, messageObject2.messageOwner.action.title);
                                        } else {
                                            if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatEditPhoto)) {
                                                if (messageObject2.messageOwner.action instanceof TL_messageActionChatDeletePhoto) {
                                                    if (messageObject2.messageOwner.action instanceof TL_messageActionChatDeleteUser) {
                                                        if (messageObject2.messageOwner.action.user_id != UserConfig.getInstance(notificationsController.currentAccount).getClientUserId()) {
                                                            str = LocaleController.formatString("NotificationGroupKickYou", C0446R.string.NotificationGroupKickYou, userName, chat.title);
                                                        } else if (messageObject2.messageOwner.action.user_id == i2) {
                                                            str = LocaleController.formatString("NotificationGroupLeftMember", C0446R.string.NotificationGroupLeftMember, userName, chat.title);
                                                        } else {
                                                            if (MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.action.user_id)) != null) {
                                                                return null;
                                                            }
                                                            str = LocaleController.formatString("NotificationGroupKickMember", C0446R.string.NotificationGroupKickMember, userName, chat.title, UserObject.getUserName(MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.action.user_id))));
                                                        }
                                                    } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatCreate)) {
                                                        str = messageObject2.messageText.toString();
                                                    } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChannelCreate)) {
                                                        str = messageObject2.messageText.toString();
                                                    } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatMigrateTo)) {
                                                        str = LocaleController.formatString("ActionMigrateFromGroupNotify", C0446R.string.ActionMigrateFromGroupNotify, chat.title);
                                                    } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChannelMigrateFrom)) {
                                                        str = LocaleController.formatString("ActionMigrateFromGroupNotify", C0446R.string.ActionMigrateFromGroupNotify, messageObject2.messageOwner.action.title);
                                                    } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionScreenshotTaken)) {
                                                        str = messageObject2.messageText.toString();
                                                    } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionPinMessage)) {
                                                        CharSequence charSequence;
                                                        StringBuilder stringBuilder3;
                                                        if (chat == null && chat.megagroup) {
                                                            if (messageObject2.replyMessageObject == null) {
                                                                str = LocaleController.formatString("NotificationActionPinnedNoText", C0446R.string.NotificationActionPinnedNoText, userName, chat.title);
                                                            } else {
                                                                messageObject2 = messageObject2.replyMessageObject;
                                                                if (messageObject2.isMusic()) {
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedMusic", C0446R.string.NotificationActionPinnedMusic, userName, chat.title);
                                                                } else if (messageObject2.isVideo()) {
                                                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedVideo", C0446R.string.NotificationActionPinnedVideo, userName, chat.title);
                                                                    } else {
                                                                        stringBuilder = new StringBuilder();
                                                                        stringBuilder.append("\ud83d\udcf9 ");
                                                                        stringBuilder.append(messageObject2.messageOwner.message);
                                                                        formatString = stringBuilder.toString();
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedText", C0446R.string.NotificationActionPinnedText, userName, formatString, chat.title);
                                                                    }
                                                                } else if (messageObject2.isGif()) {
                                                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedGif", C0446R.string.NotificationActionPinnedGif, userName, chat.title);
                                                                    } else {
                                                                        stringBuilder = new StringBuilder();
                                                                        stringBuilder.append("\ud83c\udfac ");
                                                                        stringBuilder.append(messageObject2.messageOwner.message);
                                                                        formatString = stringBuilder.toString();
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedText", C0446R.string.NotificationActionPinnedText, userName, formatString, chat.title);
                                                                    }
                                                                } else if (messageObject2.isVoice()) {
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedVoice", C0446R.string.NotificationActionPinnedVoice, userName, chat.title);
                                                                } else if (messageObject2.isRoundVideo()) {
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedRound", C0446R.string.NotificationActionPinnedRound, userName, chat.title);
                                                                } else if (messageObject2.isSticker()) {
                                                                    if (messageObject2.getStickerEmoji() != null) {
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedStickerEmoji", C0446R.string.NotificationActionPinnedStickerEmoji, userName, chat.title, messageObject2.getStickerEmoji());
                                                                    } else {
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedSticker", C0446R.string.NotificationActionPinnedSticker, userName, chat.title);
                                                                    }
                                                                } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaDocument)) {
                                                                    if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeo)) {
                                                                        if (!(messageObject2.messageOwner.media instanceof TL_messageMediaVenue)) {
                                                                            if (messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                                                                formatString = LocaleController.formatString("NotificationActionPinnedGeoLive", C0446R.string.NotificationActionPinnedGeoLive, userName, chat.title);
                                                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaContact) {
                                                                                formatString = LocaleController.formatString("NotificationActionPinnedContact", C0446R.string.NotificationActionPinnedContact, userName, chat.title);
                                                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaPhoto) {
                                                                                if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                                                    formatString = LocaleController.formatString("NotificationActionPinnedPhoto", C0446R.string.NotificationActionPinnedPhoto, userName, chat.title);
                                                                                } else {
                                                                                    stringBuilder = new StringBuilder();
                                                                                    stringBuilder.append("\ud83d\uddbc ");
                                                                                    stringBuilder.append(messageObject2.messageOwner.message);
                                                                                    formatString = stringBuilder.toString();
                                                                                    formatString = LocaleController.formatString("NotificationActionPinnedText", C0446R.string.NotificationActionPinnedText, userName, formatString, chat.title);
                                                                                }
                                                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                                                                formatString = LocaleController.formatString("NotificationActionPinnedGame", C0446R.string.NotificationActionPinnedGame, userName, chat.title);
                                                                            } else if (messageObject2.messageText == null || messageObject2.messageText.length() <= 0) {
                                                                                formatString = LocaleController.formatString("NotificationActionPinnedNoText", C0446R.string.NotificationActionPinnedNoText, userName, chat.title);
                                                                            } else {
                                                                                charSequence = messageObject2.messageText;
                                                                                if (charSequence.length() > 20) {
                                                                                    stringBuilder3 = new StringBuilder();
                                                                                    stringBuilder3.append(charSequence.subSequence(0, 20));
                                                                                    stringBuilder3.append("...");
                                                                                    charSequence = stringBuilder3.toString();
                                                                                }
                                                                                formatString = LocaleController.formatString("NotificationActionPinnedText", C0446R.string.NotificationActionPinnedText, userName, charSequence, chat.title);
                                                                            }
                                                                        }
                                                                    }
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedGeo", C0446R.string.NotificationActionPinnedGeo, userName, chat.title);
                                                                } else if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedFile", C0446R.string.NotificationActionPinnedFile, userName, chat.title);
                                                                } else {
                                                                    stringBuilder = new StringBuilder();
                                                                    stringBuilder.append("\ud83d\udcce ");
                                                                    stringBuilder.append(messageObject2.messageOwner.message);
                                                                    formatString = stringBuilder.toString();
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedText", C0446R.string.NotificationActionPinnedText, userName, formatString, chat.title);
                                                                }
                                                            }
                                                        } else if (messageObject2.replyMessageObject == null) {
                                                            str = LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0446R.string.NotificationActionPinnedNoTextChannel, chat.title);
                                                        } else {
                                                            messageObject2 = messageObject2.replyMessageObject;
                                                            if (!messageObject2.isMusic()) {
                                                                formatString = LocaleController.formatString("NotificationActionPinnedMusicChannel", C0446R.string.NotificationActionPinnedMusicChannel, chat.title);
                                                            } else if (!messageObject2.isVideo()) {
                                                                if (VERSION.SDK_INT >= 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedVideoChannel", C0446R.string.NotificationActionPinnedVideoChannel, chat.title);
                                                                } else {
                                                                    stringBuilder = new StringBuilder();
                                                                    stringBuilder.append("\ud83d\udcf9 ");
                                                                    stringBuilder.append(messageObject2.messageOwner.message);
                                                                    formatString = stringBuilder.toString();
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedTextChannel", C0446R.string.NotificationActionPinnedTextChannel, chat.title, formatString);
                                                                }
                                                            } else if (!messageObject2.isGif()) {
                                                                if (VERSION.SDK_INT >= 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedGifChannel", C0446R.string.NotificationActionPinnedGifChannel, chat.title);
                                                                } else {
                                                                    stringBuilder = new StringBuilder();
                                                                    stringBuilder.append("\ud83c\udfac ");
                                                                    stringBuilder.append(messageObject2.messageOwner.message);
                                                                    formatString = stringBuilder.toString();
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedTextChannel", C0446R.string.NotificationActionPinnedTextChannel, chat.title, formatString);
                                                                }
                                                            } else if (!messageObject2.isVoice()) {
                                                                formatString = LocaleController.formatString("NotificationActionPinnedVoiceChannel", C0446R.string.NotificationActionPinnedVoiceChannel, chat.title);
                                                            } else if (!messageObject2.isRoundVideo()) {
                                                                formatString = LocaleController.formatString("NotificationActionPinnedRoundChannel", C0446R.string.NotificationActionPinnedRoundChannel, chat.title);
                                                            } else if (!messageObject2.isSticker()) {
                                                                if (messageObject2.getStickerEmoji() != null) {
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", C0446R.string.NotificationActionPinnedStickerEmojiChannel, chat.title, messageObject2.getStickerEmoji());
                                                                } else {
                                                                    formatString = LocaleController.formatString("NotificationActionPinnedStickerChannel", C0446R.string.NotificationActionPinnedStickerChannel, chat.title);
                                                                }
                                                            } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaDocument)) {
                                                                if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeo)) {
                                                                    if (messageObject2.messageOwner.media instanceof TL_messageMediaVenue) {
                                                                        if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive)) {
                                                                            formatString = LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", C0446R.string.NotificationActionPinnedGeoLiveChannel, chat.title);
                                                                        } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaContact)) {
                                                                            formatString = LocaleController.formatString("NotificationActionPinnedContactChannel", C0446R.string.NotificationActionPinnedContactChannel, chat.title);
                                                                        } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaPhoto)) {
                                                                            if (VERSION.SDK_INT >= 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                                                formatString = LocaleController.formatString("NotificationActionPinnedPhotoChannel", C0446R.string.NotificationActionPinnedPhotoChannel, chat.title);
                                                                            } else {
                                                                                stringBuilder = new StringBuilder();
                                                                                stringBuilder.append("\ud83d\uddbc ");
                                                                                stringBuilder.append(messageObject2.messageOwner.message);
                                                                                formatString = stringBuilder.toString();
                                                                                formatString = LocaleController.formatString("NotificationActionPinnedTextChannel", C0446R.string.NotificationActionPinnedTextChannel, chat.title, formatString);
                                                                            }
                                                                        } else if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                                                            formatString = LocaleController.formatString("NotificationActionPinnedGameChannel", C0446R.string.NotificationActionPinnedGameChannel, chat.title);
                                                                        } else if (messageObject2.messageText != null || messageObject2.messageText.length() <= 0) {
                                                                            formatString = LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0446R.string.NotificationActionPinnedNoTextChannel, chat.title);
                                                                        } else {
                                                                            charSequence = messageObject2.messageText;
                                                                            if (charSequence.length() > 20) {
                                                                                stringBuilder3 = new StringBuilder();
                                                                                stringBuilder3.append(charSequence.subSequence(0, 20));
                                                                                stringBuilder3.append("...");
                                                                                charSequence = stringBuilder3.toString();
                                                                            }
                                                                            formatString = LocaleController.formatString("NotificationActionPinnedTextChannel", C0446R.string.NotificationActionPinnedTextChannel, chat.title, charSequence);
                                                                        }
                                                                    }
                                                                }
                                                                formatString = LocaleController.formatString("NotificationActionPinnedGeoChannel", C0446R.string.NotificationActionPinnedGeoChannel, chat.title);
                                                            } else if (VERSION.SDK_INT >= 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                                formatString = LocaleController.formatString("NotificationActionPinnedFileChannel", C0446R.string.NotificationActionPinnedFileChannel, chat.title);
                                                            } else {
                                                                stringBuilder = new StringBuilder();
                                                                stringBuilder.append("\ud83d\udcce ");
                                                                stringBuilder.append(messageObject2.messageOwner.message);
                                                                formatString = stringBuilder.toString();
                                                                formatString = LocaleController.formatString("NotificationActionPinnedTextChannel", C0446R.string.NotificationActionPinnedTextChannel, chat.title, formatString);
                                                            }
                                                        }
                                                    } else if (messageObject2.messageOwner.action instanceof TL_messageActionGameScore) {
                                                        str = messageObject2.messageText.toString();
                                                    }
                                                }
                                            }
                                            str = (messageObject2.messageOwner.to_id.channel_id != 0 || chat.megagroup) ? LocaleController.formatString("NotificationEditedGroupPhoto", C0446R.string.NotificationEditedGroupPhoto, userName, chat.title) : LocaleController.formatString("ChannelPhotoEditNotification", C0446R.string.ChannelPhotoEditNotification, chat.title);
                                        }
                                    } else if (ChatObject.isChannel(chat) || chat.megagroup) {
                                        if (!messageObject.isMediaEmpty()) {
                                            str = (z || messageObject2.messageOwner.message == null || messageObject2.messageOwner.message.length() == 0) ? LocaleController.formatString("NotificationMessageGroupNoText", C0446R.string.NotificationMessageGroupNoText, userName, chat.title) : LocaleController.formatString("NotificationMessageGroupText", C0446R.string.NotificationMessageGroupText, userName, chat.title, messageObject2.messageOwner.message);
                                        } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaPhoto)) {
                                            if (!z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                str = LocaleController.formatString("NotificationMessageGroupPhoto", C0446R.string.NotificationMessageGroupPhoto, userName, chat.title);
                                            } else {
                                                r3 = new Object[3];
                                                r3[0] = userName;
                                                r3[1] = chat.title;
                                                r4 = new StringBuilder();
                                                r4.append("\ud83d\uddbc ");
                                                r4.append(messageObject2.messageOwner.message);
                                                r3[2] = r4.toString();
                                                str = LocaleController.formatString("NotificationMessageGroupText", C0446R.string.NotificationMessageGroupText, r3);
                                            }
                                        } else if (!messageObject.isVideo()) {
                                            if (!z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                str = LocaleController.formatString("NotificationMessageGroupVideo", C0446R.string.NotificationMessageGroupVideo, userName, chat.title);
                                            } else {
                                                r3 = new Object[3];
                                                r3[0] = userName;
                                                r3[1] = chat.title;
                                                r4 = new StringBuilder();
                                                r4.append("\ud83d\udcf9 ");
                                                r4.append(messageObject2.messageOwner.message);
                                                r3[2] = r4.toString();
                                                str = LocaleController.formatString("NotificationMessageGroupText", C0446R.string.NotificationMessageGroupText, r3);
                                            }
                                        } else if (!messageObject.isVoice()) {
                                            str = LocaleController.formatString("NotificationMessageGroupAudio", C0446R.string.NotificationMessageGroupAudio, userName, chat.title);
                                        } else if (!messageObject.isRoundVideo()) {
                                            str = LocaleController.formatString("NotificationMessageGroupRound", C0446R.string.NotificationMessageGroupRound, userName, chat.title);
                                        } else if (!messageObject.isMusic()) {
                                            str = LocaleController.formatString("NotificationMessageGroupMusic", C0446R.string.NotificationMessageGroupMusic, userName, chat.title);
                                        } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaContact)) {
                                            str = LocaleController.formatString("NotificationMessageGroupContact", C0446R.string.NotificationMessageGroupContact, userName, chat.title);
                                        } else if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                            str = LocaleController.formatString("NotificationMessageGroupGame", C0446R.string.NotificationMessageGroupGame, userName, chat.title, messageObject2.messageOwner.media.game.title);
                                        } else {
                                            if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeo)) {
                                                if (messageObject2.messageOwner.media instanceof TL_messageMediaVenue) {
                                                    if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive)) {
                                                        str = LocaleController.formatString("NotificationMessageGroupLiveLocation", C0446R.string.NotificationMessageGroupLiveLocation, userName, chat.title);
                                                    } else if (messageObject2.messageOwner.media instanceof TL_messageMediaDocument) {
                                                        if (!messageObject.isSticker()) {
                                                            if (messageObject.getStickerEmoji() != null) {
                                                                formatString = LocaleController.formatString("NotificationMessageGroupStickerEmoji", C0446R.string.NotificationMessageGroupStickerEmoji, userName, chat.title, messageObject.getStickerEmoji());
                                                            } else {
                                                                formatString = LocaleController.formatString("NotificationMessageGroupSticker", C0446R.string.NotificationMessageGroupSticker, userName, chat.title);
                                                            }
                                                        } else if (messageObject.isGif()) {
                                                            if (!z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                                str = LocaleController.formatString("NotificationMessageGroupGif", C0446R.string.NotificationMessageGroupGif, userName, chat.title);
                                                            } else {
                                                                r3 = new Object[3];
                                                                r3[0] = userName;
                                                                r3[1] = chat.title;
                                                                r4 = new StringBuilder();
                                                                r4.append("\ud83c\udfac ");
                                                                r4.append(messageObject2.messageOwner.message);
                                                                r3[2] = r4.toString();
                                                                str = LocaleController.formatString("NotificationMessageGroupText", C0446R.string.NotificationMessageGroupText, r3);
                                                            }
                                                        } else if (!z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                            str = LocaleController.formatString("NotificationMessageGroupDocument", C0446R.string.NotificationMessageGroupDocument, userName, chat.title);
                                                        } else {
                                                            r3 = new Object[3];
                                                            r3[0] = userName;
                                                            r3[1] = chat.title;
                                                            r4 = new StringBuilder();
                                                            r4.append("\ud83d\udcce ");
                                                            r4.append(messageObject2.messageOwner.message);
                                                            r3[2] = r4.toString();
                                                            str = LocaleController.formatString("NotificationMessageGroupText", C0446R.string.NotificationMessageGroupText, r3);
                                                        }
                                                    }
                                                }
                                            }
                                            str = LocaleController.formatString("NotificationMessageGroupMap", C0446R.string.NotificationMessageGroupMap, userName, chat.title);
                                        }
                                    } else if (messageObject.isMediaEmpty()) {
                                        if (z || messageObject2.messageOwner.message == null || messageObject2.messageOwner.message.length() == 0) {
                                            str = LocaleController.formatString("ChannelMessageNoText", C0446R.string.ChannelMessageNoText, userName);
                                        } else {
                                            str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, userName, messageObject2.messageOwner.message);
                                            zArr[0] = true;
                                        }
                                    } else if (messageObject2.messageOwner.media instanceof TL_messageMediaPhoto) {
                                        if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                            str = LocaleController.formatString("ChannelMessagePhoto", C0446R.string.ChannelMessagePhoto, userName);
                                        } else {
                                            objArr = new Object[2];
                                            objArr[0] = userName;
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("\ud83d\uddbc ");
                                            stringBuilder2.append(messageObject2.messageOwner.message);
                                            objArr[1] = stringBuilder2.toString();
                                            str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, objArr);
                                            zArr[0] = true;
                                        }
                                    } else if (messageObject.isVideo()) {
                                        if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                            str = LocaleController.formatString("ChannelMessageVideo", C0446R.string.ChannelMessageVideo, userName);
                                        } else {
                                            objArr = new Object[2];
                                            objArr[0] = userName;
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("\ud83d\udcf9 ");
                                            stringBuilder2.append(messageObject2.messageOwner.message);
                                            objArr[1] = stringBuilder2.toString();
                                            str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, objArr);
                                            zArr[0] = true;
                                        }
                                    } else if (messageObject.isVoice()) {
                                        str = LocaleController.formatString("ChannelMessageAudio", C0446R.string.ChannelMessageAudio, userName);
                                    } else if (messageObject.isRoundVideo()) {
                                        str = LocaleController.formatString("ChannelMessageRound", C0446R.string.ChannelMessageRound, userName);
                                    } else if (messageObject.isMusic()) {
                                        str = LocaleController.formatString("ChannelMessageMusic", C0446R.string.ChannelMessageMusic, userName);
                                    } else if (messageObject2.messageOwner.media instanceof TL_messageMediaContact) {
                                        str = LocaleController.formatString("ChannelMessageContact", C0446R.string.ChannelMessageContact, userName);
                                    } else {
                                        if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeo)) {
                                            if (!(messageObject2.messageOwner.media instanceof TL_messageMediaVenue)) {
                                                if (messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                                    str = LocaleController.formatString("ChannelMessageLiveLocation", C0446R.string.ChannelMessageLiveLocation, userName);
                                                } else if (messageObject2.messageOwner.media instanceof TL_messageMediaDocument) {
                                                    if (messageObject.isSticker()) {
                                                        if (messageObject.getStickerEmoji() != null) {
                                                            formatString = LocaleController.formatString("ChannelMessageStickerEmoji", C0446R.string.ChannelMessageStickerEmoji, userName, messageObject.getStickerEmoji());
                                                        } else {
                                                            formatString = LocaleController.formatString("ChannelMessageSticker", C0446R.string.ChannelMessageSticker, userName);
                                                        }
                                                    } else if (messageObject.isGif()) {
                                                        if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                            str = LocaleController.formatString("ChannelMessageGIF", C0446R.string.ChannelMessageGIF, userName);
                                                        } else {
                                                            objArr = new Object[2];
                                                            objArr[0] = userName;
                                                            stringBuilder2 = new StringBuilder();
                                                            stringBuilder2.append("\ud83c\udfac ");
                                                            stringBuilder2.append(messageObject2.messageOwner.message);
                                                            objArr[1] = stringBuilder2.toString();
                                                            str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, objArr);
                                                            zArr[0] = true;
                                                        }
                                                    } else if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                        str = LocaleController.formatString("ChannelMessageDocument", C0446R.string.ChannelMessageDocument, userName);
                                                    } else {
                                                        objArr = new Object[2];
                                                        objArr[0] = userName;
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append("\ud83d\udcce ");
                                                        stringBuilder2.append(messageObject2.messageOwner.message);
                                                        objArr[1] = stringBuilder2.toString();
                                                        str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, objArr);
                                                        zArr[0] = true;
                                                    }
                                                }
                                            }
                                        }
                                        str = LocaleController.formatString("ChannelMessageMap", C0446R.string.ChannelMessageMap, userName);
                                    }
                                }
                            } else if (!MessagesController.getNotificationsSettings(notificationsController.currentAccount).getBoolean("EnablePreviewAll", true)) {
                                str = LocaleController.formatString("NotificationMessageNoText", C0446R.string.NotificationMessageNoText, userName);
                            } else if (messageObject2.messageOwner instanceof TL_messageService) {
                                if (messageObject2.messageOwner.action instanceof TL_messageActionUserJoined) {
                                    str = LocaleController.formatString("NotificationContactJoined", C0446R.string.NotificationContactJoined, userName);
                                } else if (messageObject2.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                                    str = LocaleController.formatString("NotificationContactNewPhoto", C0446R.string.NotificationContactNewPhoto, userName);
                                } else if (messageObject2.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                                    String formatString2 = LocaleController.formatString("formatDateAtTime", C0446R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(((long) messageObject2.messageOwner.date) * 1000), LocaleController.getInstance().formatterDay.format(((long) messageObject2.messageOwner.date) * 1000));
                                    str = LocaleController.formatString("NotificationUnrecognizedDevice", C0446R.string.NotificationUnrecognizedDevice, UserConfig.getInstance(notificationsController.currentAccount).getCurrentUser().first_name, formatString2, messageObject2.messageOwner.action.title, messageObject2.messageOwner.action.address);
                                } else {
                                    if (!(messageObject2.messageOwner.action instanceof TL_messageActionGameScore)) {
                                        if (!(messageObject2.messageOwner.action instanceof TL_messageActionPaymentSent)) {
                                            if (messageObject2.messageOwner.action instanceof TL_messageActionPhoneCall) {
                                                PhoneCallDiscardReason phoneCallDiscardReason = messageObject2.messageOwner.action.reason;
                                                if (!messageObject.isOut() && (phoneCallDiscardReason instanceof TL_phoneCallDiscardReasonMissed)) {
                                                    str = LocaleController.getString("CallMessageIncomingMissed", C0446R.string.CallMessageIncomingMissed);
                                                }
                                            }
                                        }
                                    }
                                    str = messageObject2.messageText.toString();
                                }
                            } else if (messageObject.isMediaEmpty()) {
                                if (z) {
                                    str = LocaleController.formatString("NotificationMessageNoText", C0446R.string.NotificationMessageNoText, userName);
                                } else if (TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                    str = LocaleController.formatString("NotificationMessageNoText", C0446R.string.NotificationMessageNoText, userName);
                                } else {
                                    str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, userName, messageObject2.messageOwner.message);
                                    zArr[0] = true;
                                }
                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaPhoto) {
                                if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                    str = messageObject2.messageOwner.media.ttl_seconds != 0 ? LocaleController.formatString("NotificationMessageSDPhoto", C0446R.string.NotificationMessageSDPhoto, userName) : LocaleController.formatString("NotificationMessagePhoto", C0446R.string.NotificationMessagePhoto, userName);
                                } else {
                                    objArr = new Object[2];
                                    objArr[0] = userName;
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("\ud83d\uddbc ");
                                    stringBuilder2.append(messageObject2.messageOwner.message);
                                    objArr[1] = stringBuilder2.toString();
                                    str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, objArr);
                                    zArr[0] = true;
                                }
                            } else if (messageObject.isVideo()) {
                                if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                    str = messageObject2.messageOwner.media.ttl_seconds != 0 ? LocaleController.formatString("NotificationMessageSDVideo", C0446R.string.NotificationMessageSDVideo, userName) : LocaleController.formatString("NotificationMessageVideo", C0446R.string.NotificationMessageVideo, userName);
                                } else {
                                    objArr = new Object[2];
                                    objArr[0] = userName;
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("\ud83d\udcf9 ");
                                    stringBuilder2.append(messageObject2.messageOwner.message);
                                    objArr[1] = stringBuilder2.toString();
                                    str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, objArr);
                                    zArr[0] = true;
                                }
                            } else if (messageObject.isGame()) {
                                str = LocaleController.formatString("NotificationMessageGame", C0446R.string.NotificationMessageGame, userName, messageObject2.messageOwner.media.game.title);
                            } else if (messageObject.isVoice()) {
                                str = LocaleController.formatString("NotificationMessageAudio", C0446R.string.NotificationMessageAudio, userName);
                            } else if (messageObject.isRoundVideo()) {
                                str = LocaleController.formatString("NotificationMessageRound", C0446R.string.NotificationMessageRound, userName);
                            } else if (messageObject.isMusic()) {
                                str = LocaleController.formatString("NotificationMessageMusic", C0446R.string.NotificationMessageMusic, userName);
                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaContact) {
                                str = LocaleController.formatString("NotificationMessageContact", C0446R.string.NotificationMessageContact, userName);
                            } else {
                                if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeo)) {
                                    if (!(messageObject2.messageOwner.media instanceof TL_messageMediaVenue)) {
                                        if (messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                            str = LocaleController.formatString("NotificationMessageLiveLocation", C0446R.string.NotificationMessageLiveLocation, userName);
                                        } else if (messageObject2.messageOwner.media instanceof TL_messageMediaDocument) {
                                            if (messageObject.isSticker()) {
                                                if (messageObject.getStickerEmoji() != null) {
                                                    formatString = LocaleController.formatString("NotificationMessageStickerEmoji", C0446R.string.NotificationMessageStickerEmoji, userName, messageObject.getStickerEmoji());
                                                } else {
                                                    formatString = LocaleController.formatString("NotificationMessageSticker", C0446R.string.NotificationMessageSticker, userName);
                                                }
                                            } else if (messageObject.isGif()) {
                                                if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                    str = LocaleController.formatString("NotificationMessageGif", C0446R.string.NotificationMessageGif, userName);
                                                } else {
                                                    objArr = new Object[2];
                                                    objArr[0] = userName;
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append("\ud83c\udfac ");
                                                    stringBuilder2.append(messageObject2.messageOwner.message);
                                                    objArr[1] = stringBuilder2.toString();
                                                    str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, objArr);
                                                    zArr[0] = true;
                                                }
                                            } else if (z || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                                                str = LocaleController.formatString("NotificationMessageDocument", C0446R.string.NotificationMessageDocument, userName);
                                            } else {
                                                objArr = new Object[2];
                                                objArr[0] = userName;
                                                stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append("\ud83d\udcce ");
                                                stringBuilder2.append(messageObject2.messageOwner.message);
                                                objArr[1] = stringBuilder2.toString();
                                                str = LocaleController.formatString("NotificationMessageText", C0446R.string.NotificationMessageText, objArr);
                                                zArr[0] = true;
                                            }
                                        }
                                    }
                                }
                                str = LocaleController.formatString("NotificationMessageMap", C0446R.string.NotificationMessageMap, userName);
                            }
                            str = formatString;
                        }
                        return str;
                    }
                }
                Chat chat2 = MessagesController.getInstance(notificationsController.currentAccount).getChat(Integer.valueOf(-i2));
                if (chat2 != null) {
                    userName = chat2.title;
                    if (userName != null) {
                        return null;
                    }
                    if (i != 0) {
                        chat = null;
                    } else {
                        chat = MessagesController.getInstance(notificationsController.currentAccount).getChat(Integer.valueOf(i));
                        if (chat == null) {
                            return null;
                        }
                    }
                    if (((int) j) == 0) {
                        if (i == 0) {
                        }
                        if (i != 0) {
                            if (MessagesController.getNotificationsSettings(notificationsController.currentAccount).getBoolean("EnablePreviewGroup", true)) {
                                if (ChatObject.isChannel(chat)) {
                                }
                            } else if (messageObject2.messageOwner instanceof TL_messageService) {
                                if (ChatObject.isChannel(chat)) {
                                }
                                if (!messageObject.isMediaEmpty()) {
                                    if (!z) {
                                    }
                                } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaPhoto)) {
                                    if (z) {
                                    }
                                    str = LocaleController.formatString("NotificationMessageGroupPhoto", C0446R.string.NotificationMessageGroupPhoto, userName, chat.title);
                                } else if (!messageObject.isVideo()) {
                                    if (z) {
                                    }
                                    str = LocaleController.formatString("NotificationMessageGroupVideo", C0446R.string.NotificationMessageGroupVideo, userName, chat.title);
                                } else if (!messageObject.isVoice()) {
                                    str = LocaleController.formatString("NotificationMessageGroupAudio", C0446R.string.NotificationMessageGroupAudio, userName, chat.title);
                                } else if (!messageObject.isRoundVideo()) {
                                    str = LocaleController.formatString("NotificationMessageGroupRound", C0446R.string.NotificationMessageGroupRound, userName, chat.title);
                                } else if (!messageObject.isMusic()) {
                                    str = LocaleController.formatString("NotificationMessageGroupMusic", C0446R.string.NotificationMessageGroupMusic, userName, chat.title);
                                } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaContact)) {
                                    str = LocaleController.formatString("NotificationMessageGroupContact", C0446R.string.NotificationMessageGroupContact, userName, chat.title);
                                } else if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                    if (messageObject2.messageOwner.media instanceof TL_messageMediaGeo) {
                                        if (messageObject2.messageOwner.media instanceof TL_messageMediaVenue) {
                                            if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive)) {
                                                str = LocaleController.formatString("NotificationMessageGroupLiveLocation", C0446R.string.NotificationMessageGroupLiveLocation, userName, chat.title);
                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaDocument) {
                                                if (!messageObject.isSticker()) {
                                                    if (messageObject.getStickerEmoji() != null) {
                                                        formatString = LocaleController.formatString("NotificationMessageGroupSticker", C0446R.string.NotificationMessageGroupSticker, userName, chat.title);
                                                    } else {
                                                        formatString = LocaleController.formatString("NotificationMessageGroupStickerEmoji", C0446R.string.NotificationMessageGroupStickerEmoji, userName, chat.title, messageObject.getStickerEmoji());
                                                    }
                                                    str = formatString;
                                                } else if (messageObject.isGif()) {
                                                    if (z) {
                                                    }
                                                    str = LocaleController.formatString("NotificationMessageGroupDocument", C0446R.string.NotificationMessageGroupDocument, userName, chat.title);
                                                } else {
                                                    if (z) {
                                                    }
                                                    str = LocaleController.formatString("NotificationMessageGroupGif", C0446R.string.NotificationMessageGroupGif, userName, chat.title);
                                                }
                                            }
                                        }
                                    }
                                    str = LocaleController.formatString("NotificationMessageGroupMap", C0446R.string.NotificationMessageGroupMap, userName, chat.title);
                                } else {
                                    str = LocaleController.formatString("NotificationMessageGroupGame", C0446R.string.NotificationMessageGroupGame, userName, chat.title, messageObject2.messageOwner.media.game.title);
                                }
                            } else {
                                if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatAddUser)) {
                                    i3 = messageObject2.messageOwner.action.user_id;
                                    i3 = ((Integer) messageObject2.messageOwner.action.users.get(0)).intValue();
                                    if (i3 == 0) {
                                        stringBuilder = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                                        for (i4 = 0; i4 < messageObject2.messageOwner.action.users.size(); i4++) {
                                            user = MessagesController.getInstance(notificationsController.currentAccount).getUser((Integer) messageObject2.messageOwner.action.users.get(i4));
                                            if (user == null) {
                                                userName2 = UserObject.getUserName(user);
                                                if (stringBuilder.length() != 0) {
                                                    stringBuilder.append(", ");
                                                }
                                                stringBuilder.append(userName2);
                                            }
                                        }
                                        formatString = LocaleController.formatString("NotificationGroupAddMember", C0446R.string.NotificationGroupAddMember, userName, chat.title, stringBuilder.toString());
                                    } else {
                                        if (messageObject2.messageOwner.to_id.channel_id == 0) {
                                        }
                                        if (i3 == UserConfig.getInstance(notificationsController.currentAccount).getClientUserId()) {
                                            user2 = MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(i3));
                                            if (user2 != null) {
                                                return null;
                                            }
                                            if (i2 == user2.id) {
                                                formatString = LocaleController.formatString("NotificationGroupAddMember", C0446R.string.NotificationGroupAddMember, userName, chat.title, UserObject.getUserName(user2));
                                            } else if (chat.megagroup) {
                                                formatString = LocaleController.formatString("NotificationGroupAddSelf", C0446R.string.NotificationGroupAddSelf, userName, chat.title);
                                            } else {
                                                formatString = LocaleController.formatString("NotificationGroupAddSelfMega", C0446R.string.NotificationGroupAddSelfMega, userName, chat.title);
                                            }
                                        } else {
                                            formatString = LocaleController.formatString("NotificationInvitedToGroup", C0446R.string.NotificationInvitedToGroup, userName, chat.title);
                                        }
                                    }
                                } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatJoinedByLink)) {
                                    str = LocaleController.formatString("NotificationInvitedToGroupByLink", C0446R.string.NotificationInvitedToGroupByLink, userName, chat.title);
                                } else if (messageObject2.messageOwner.action instanceof TL_messageActionChatEditTitle) {
                                    if (messageObject2.messageOwner.action instanceof TL_messageActionChatEditPhoto) {
                                        if (messageObject2.messageOwner.action instanceof TL_messageActionChatDeletePhoto) {
                                            if (messageObject2.messageOwner.action instanceof TL_messageActionChatDeleteUser) {
                                                if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatCreate)) {
                                                    str = messageObject2.messageText.toString();
                                                } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChannelCreate)) {
                                                    str = messageObject2.messageText.toString();
                                                } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatMigrateTo)) {
                                                    str = LocaleController.formatString("ActionMigrateFromGroupNotify", C0446R.string.ActionMigrateFromGroupNotify, chat.title);
                                                } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChannelMigrateFrom)) {
                                                    str = LocaleController.formatString("ActionMigrateFromGroupNotify", C0446R.string.ActionMigrateFromGroupNotify, messageObject2.messageOwner.action.title);
                                                } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionScreenshotTaken)) {
                                                    str = messageObject2.messageText.toString();
                                                } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionPinMessage)) {
                                                    if (chat == null) {
                                                    }
                                                    if (messageObject2.replyMessageObject == null) {
                                                        messageObject2 = messageObject2.replyMessageObject;
                                                        if (!messageObject2.isMusic()) {
                                                            formatString = LocaleController.formatString("NotificationActionPinnedMusicChannel", C0446R.string.NotificationActionPinnedMusicChannel, chat.title);
                                                        } else if (!messageObject2.isVideo()) {
                                                            if (VERSION.SDK_INT >= 19) {
                                                            }
                                                            formatString = LocaleController.formatString("NotificationActionPinnedVideoChannel", C0446R.string.NotificationActionPinnedVideoChannel, chat.title);
                                                        } else if (!messageObject2.isGif()) {
                                                            if (VERSION.SDK_INT >= 19) {
                                                            }
                                                            formatString = LocaleController.formatString("NotificationActionPinnedGifChannel", C0446R.string.NotificationActionPinnedGifChannel, chat.title);
                                                        } else if (!messageObject2.isVoice()) {
                                                            formatString = LocaleController.formatString("NotificationActionPinnedVoiceChannel", C0446R.string.NotificationActionPinnedVoiceChannel, chat.title);
                                                        } else if (!messageObject2.isRoundVideo()) {
                                                            formatString = LocaleController.formatString("NotificationActionPinnedRoundChannel", C0446R.string.NotificationActionPinnedRoundChannel, chat.title);
                                                        } else if (!messageObject2.isSticker()) {
                                                            if (messageObject2.getStickerEmoji() != null) {
                                                                formatString = LocaleController.formatString("NotificationActionPinnedStickerChannel", C0446R.string.NotificationActionPinnedStickerChannel, chat.title);
                                                            } else {
                                                                formatString = LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", C0446R.string.NotificationActionPinnedStickerEmojiChannel, chat.title, messageObject2.getStickerEmoji());
                                                            }
                                                        } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaDocument)) {
                                                            if (messageObject2.messageOwner.media instanceof TL_messageMediaGeo) {
                                                                if (messageObject2.messageOwner.media instanceof TL_messageMediaVenue) {
                                                                    if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive)) {
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", C0446R.string.NotificationActionPinnedGeoLiveChannel, chat.title);
                                                                    } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaContact)) {
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedContactChannel", C0446R.string.NotificationActionPinnedContactChannel, chat.title);
                                                                    } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaPhoto)) {
                                                                        if (VERSION.SDK_INT >= 19) {
                                                                        }
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedPhotoChannel", C0446R.string.NotificationActionPinnedPhotoChannel, chat.title);
                                                                    } else if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                                                        if (messageObject2.messageText != null) {
                                                                        }
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0446R.string.NotificationActionPinnedNoTextChannel, chat.title);
                                                                    } else {
                                                                        formatString = LocaleController.formatString("NotificationActionPinnedGameChannel", C0446R.string.NotificationActionPinnedGameChannel, chat.title);
                                                                    }
                                                                }
                                                            }
                                                            formatString = LocaleController.formatString("NotificationActionPinnedGeoChannel", C0446R.string.NotificationActionPinnedGeoChannel, chat.title);
                                                        } else {
                                                            if (VERSION.SDK_INT >= 19) {
                                                            }
                                                            formatString = LocaleController.formatString("NotificationActionPinnedFileChannel", C0446R.string.NotificationActionPinnedFileChannel, chat.title);
                                                        }
                                                    } else {
                                                        str = LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0446R.string.NotificationActionPinnedNoTextChannel, chat.title);
                                                    }
                                                } else if (messageObject2.messageOwner.action instanceof TL_messageActionGameScore) {
                                                    str = messageObject2.messageText.toString();
                                                }
                                            } else if (messageObject2.messageOwner.action.user_id != UserConfig.getInstance(notificationsController.currentAccount).getClientUserId()) {
                                                str = LocaleController.formatString("NotificationGroupKickYou", C0446R.string.NotificationGroupKickYou, userName, chat.title);
                                            } else if (messageObject2.messageOwner.action.user_id == i2) {
                                                if (MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.action.user_id)) != null) {
                                                    return null;
                                                }
                                                str = LocaleController.formatString("NotificationGroupKickMember", C0446R.string.NotificationGroupKickMember, userName, chat.title, UserObject.getUserName(MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.action.user_id))));
                                            } else {
                                                str = LocaleController.formatString("NotificationGroupLeftMember", C0446R.string.NotificationGroupLeftMember, userName, chat.title);
                                            }
                                        }
                                    }
                                    if (messageObject2.messageOwner.to_id.channel_id != 0) {
                                    }
                                } else {
                                    str = LocaleController.formatString("NotificationEditedGroupName", C0446R.string.NotificationEditedGroupName, userName, messageObject2.messageOwner.action.title);
                                }
                                str = formatString;
                            }
                        }
                    } else {
                        str = LocaleController.getString("YouHaveNewMessage", C0446R.string.YouHaveNewMessage);
                    }
                    return str;
                }
                userName = null;
                if (userName != null) {
                    return null;
                }
                if (i != 0) {
                    chat = MessagesController.getInstance(notificationsController.currentAccount).getChat(Integer.valueOf(i));
                    if (chat == null) {
                        return null;
                    }
                }
                chat = null;
                if (((int) j) == 0) {
                    str = LocaleController.getString("YouHaveNewMessage", C0446R.string.YouHaveNewMessage);
                } else {
                    if (i == 0) {
                    }
                    if (i != 0) {
                        if (MessagesController.getNotificationsSettings(notificationsController.currentAccount).getBoolean("EnablePreviewGroup", true)) {
                            if (ChatObject.isChannel(chat)) {
                            }
                        } else if (messageObject2.messageOwner instanceof TL_messageService) {
                            if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatAddUser)) {
                                i3 = messageObject2.messageOwner.action.user_id;
                                i3 = ((Integer) messageObject2.messageOwner.action.users.get(0)).intValue();
                                if (i3 == 0) {
                                    if (messageObject2.messageOwner.to_id.channel_id == 0) {
                                    }
                                    if (i3 == UserConfig.getInstance(notificationsController.currentAccount).getClientUserId()) {
                                        formatString = LocaleController.formatString("NotificationInvitedToGroup", C0446R.string.NotificationInvitedToGroup, userName, chat.title);
                                    } else {
                                        user2 = MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(i3));
                                        if (user2 != null) {
                                            return null;
                                        }
                                        if (i2 == user2.id) {
                                            formatString = LocaleController.formatString("NotificationGroupAddMember", C0446R.string.NotificationGroupAddMember, userName, chat.title, UserObject.getUserName(user2));
                                        } else if (chat.megagroup) {
                                            formatString = LocaleController.formatString("NotificationGroupAddSelfMega", C0446R.string.NotificationGroupAddSelfMega, userName, chat.title);
                                        } else {
                                            formatString = LocaleController.formatString("NotificationGroupAddSelf", C0446R.string.NotificationGroupAddSelf, userName, chat.title);
                                        }
                                    }
                                } else {
                                    stringBuilder = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                                    for (i4 = 0; i4 < messageObject2.messageOwner.action.users.size(); i4++) {
                                        user = MessagesController.getInstance(notificationsController.currentAccount).getUser((Integer) messageObject2.messageOwner.action.users.get(i4));
                                        if (user == null) {
                                            userName2 = UserObject.getUserName(user);
                                            if (stringBuilder.length() != 0) {
                                                stringBuilder.append(", ");
                                            }
                                            stringBuilder.append(userName2);
                                        }
                                    }
                                    formatString = LocaleController.formatString("NotificationGroupAddMember", C0446R.string.NotificationGroupAddMember, userName, chat.title, stringBuilder.toString());
                                }
                            } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatJoinedByLink)) {
                                str = LocaleController.formatString("NotificationInvitedToGroupByLink", C0446R.string.NotificationInvitedToGroupByLink, userName, chat.title);
                            } else if (messageObject2.messageOwner.action instanceof TL_messageActionChatEditTitle) {
                                str = LocaleController.formatString("NotificationEditedGroupName", C0446R.string.NotificationEditedGroupName, userName, messageObject2.messageOwner.action.title);
                            } else {
                                if (messageObject2.messageOwner.action instanceof TL_messageActionChatEditPhoto) {
                                    if (messageObject2.messageOwner.action instanceof TL_messageActionChatDeletePhoto) {
                                        if (messageObject2.messageOwner.action instanceof TL_messageActionChatDeleteUser) {
                                            if (messageObject2.messageOwner.action.user_id != UserConfig.getInstance(notificationsController.currentAccount).getClientUserId()) {
                                                str = LocaleController.formatString("NotificationGroupKickYou", C0446R.string.NotificationGroupKickYou, userName, chat.title);
                                            } else if (messageObject2.messageOwner.action.user_id == i2) {
                                                str = LocaleController.formatString("NotificationGroupLeftMember", C0446R.string.NotificationGroupLeftMember, userName, chat.title);
                                            } else {
                                                if (MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.action.user_id)) != null) {
                                                    return null;
                                                }
                                                str = LocaleController.formatString("NotificationGroupKickMember", C0446R.string.NotificationGroupKickMember, userName, chat.title, UserObject.getUserName(MessagesController.getInstance(notificationsController.currentAccount).getUser(Integer.valueOf(messageObject2.messageOwner.action.user_id))));
                                            }
                                        } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatCreate)) {
                                            str = messageObject2.messageText.toString();
                                        } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChannelCreate)) {
                                            str = messageObject2.messageText.toString();
                                        } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChatMigrateTo)) {
                                            str = LocaleController.formatString("ActionMigrateFromGroupNotify", C0446R.string.ActionMigrateFromGroupNotify, chat.title);
                                        } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionChannelMigrateFrom)) {
                                            str = LocaleController.formatString("ActionMigrateFromGroupNotify", C0446R.string.ActionMigrateFromGroupNotify, messageObject2.messageOwner.action.title);
                                        } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionScreenshotTaken)) {
                                            str = messageObject2.messageText.toString();
                                        } else if (!(messageObject2.messageOwner.action instanceof TL_messageActionPinMessage)) {
                                            if (chat == null) {
                                            }
                                            if (messageObject2.replyMessageObject == null) {
                                                str = LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0446R.string.NotificationActionPinnedNoTextChannel, chat.title);
                                            } else {
                                                messageObject2 = messageObject2.replyMessageObject;
                                                if (!messageObject2.isMusic()) {
                                                    formatString = LocaleController.formatString("NotificationActionPinnedMusicChannel", C0446R.string.NotificationActionPinnedMusicChannel, chat.title);
                                                } else if (!messageObject2.isVideo()) {
                                                    if (VERSION.SDK_INT >= 19) {
                                                    }
                                                    formatString = LocaleController.formatString("NotificationActionPinnedVideoChannel", C0446R.string.NotificationActionPinnedVideoChannel, chat.title);
                                                } else if (!messageObject2.isGif()) {
                                                    if (VERSION.SDK_INT >= 19) {
                                                    }
                                                    formatString = LocaleController.formatString("NotificationActionPinnedGifChannel", C0446R.string.NotificationActionPinnedGifChannel, chat.title);
                                                } else if (!messageObject2.isVoice()) {
                                                    formatString = LocaleController.formatString("NotificationActionPinnedVoiceChannel", C0446R.string.NotificationActionPinnedVoiceChannel, chat.title);
                                                } else if (!messageObject2.isRoundVideo()) {
                                                    formatString = LocaleController.formatString("NotificationActionPinnedRoundChannel", C0446R.string.NotificationActionPinnedRoundChannel, chat.title);
                                                } else if (!messageObject2.isSticker()) {
                                                    if (messageObject2.getStickerEmoji() != null) {
                                                        formatString = LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", C0446R.string.NotificationActionPinnedStickerEmojiChannel, chat.title, messageObject2.getStickerEmoji());
                                                    } else {
                                                        formatString = LocaleController.formatString("NotificationActionPinnedStickerChannel", C0446R.string.NotificationActionPinnedStickerChannel, chat.title);
                                                    }
                                                } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaDocument)) {
                                                    if (VERSION.SDK_INT >= 19) {
                                                    }
                                                    formatString = LocaleController.formatString("NotificationActionPinnedFileChannel", C0446R.string.NotificationActionPinnedFileChannel, chat.title);
                                                } else {
                                                    if (messageObject2.messageOwner.media instanceof TL_messageMediaGeo) {
                                                        if (messageObject2.messageOwner.media instanceof TL_messageMediaVenue) {
                                                            if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive)) {
                                                                formatString = LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", C0446R.string.NotificationActionPinnedGeoLiveChannel, chat.title);
                                                            } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaContact)) {
                                                                formatString = LocaleController.formatString("NotificationActionPinnedContactChannel", C0446R.string.NotificationActionPinnedContactChannel, chat.title);
                                                            } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaPhoto)) {
                                                                if (VERSION.SDK_INT >= 19) {
                                                                }
                                                                formatString = LocaleController.formatString("NotificationActionPinnedPhotoChannel", C0446R.string.NotificationActionPinnedPhotoChannel, chat.title);
                                                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                                                formatString = LocaleController.formatString("NotificationActionPinnedGameChannel", C0446R.string.NotificationActionPinnedGameChannel, chat.title);
                                                            } else {
                                                                if (messageObject2.messageText != null) {
                                                                }
                                                                formatString = LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0446R.string.NotificationActionPinnedNoTextChannel, chat.title);
                                                            }
                                                        }
                                                    }
                                                    formatString = LocaleController.formatString("NotificationActionPinnedGeoChannel", C0446R.string.NotificationActionPinnedGeoChannel, chat.title);
                                                }
                                            }
                                        } else if (messageObject2.messageOwner.action instanceof TL_messageActionGameScore) {
                                            str = messageObject2.messageText.toString();
                                        }
                                    }
                                }
                                if (messageObject2.messageOwner.to_id.channel_id != 0) {
                                }
                            }
                            str = formatString;
                        } else {
                            if (ChatObject.isChannel(chat)) {
                            }
                            if (!messageObject.isMediaEmpty()) {
                                if (z) {
                                }
                            } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaPhoto)) {
                                if (z) {
                                }
                                str = LocaleController.formatString("NotificationMessageGroupPhoto", C0446R.string.NotificationMessageGroupPhoto, userName, chat.title);
                            } else if (!messageObject.isVideo()) {
                                if (z) {
                                }
                                str = LocaleController.formatString("NotificationMessageGroupVideo", C0446R.string.NotificationMessageGroupVideo, userName, chat.title);
                            } else if (!messageObject.isVoice()) {
                                str = LocaleController.formatString("NotificationMessageGroupAudio", C0446R.string.NotificationMessageGroupAudio, userName, chat.title);
                            } else if (!messageObject.isRoundVideo()) {
                                str = LocaleController.formatString("NotificationMessageGroupRound", C0446R.string.NotificationMessageGroupRound, userName, chat.title);
                            } else if (!messageObject.isMusic()) {
                                str = LocaleController.formatString("NotificationMessageGroupMusic", C0446R.string.NotificationMessageGroupMusic, userName, chat.title);
                            } else if (!(messageObject2.messageOwner.media instanceof TL_messageMediaContact)) {
                                str = LocaleController.formatString("NotificationMessageGroupContact", C0446R.string.NotificationMessageGroupContact, userName, chat.title);
                            } else if (messageObject2.messageOwner.media instanceof TL_messageMediaGame) {
                                str = LocaleController.formatString("NotificationMessageGroupGame", C0446R.string.NotificationMessageGroupGame, userName, chat.title, messageObject2.messageOwner.media.game.title);
                            } else {
                                if (messageObject2.messageOwner.media instanceof TL_messageMediaGeo) {
                                    if (messageObject2.messageOwner.media instanceof TL_messageMediaVenue) {
                                        if (!(messageObject2.messageOwner.media instanceof TL_messageMediaGeoLive)) {
                                            str = LocaleController.formatString("NotificationMessageGroupLiveLocation", C0446R.string.NotificationMessageGroupLiveLocation, userName, chat.title);
                                        } else if (messageObject2.messageOwner.media instanceof TL_messageMediaDocument) {
                                            if (!messageObject.isSticker()) {
                                                if (messageObject.getStickerEmoji() != null) {
                                                    formatString = LocaleController.formatString("NotificationMessageGroupStickerEmoji", C0446R.string.NotificationMessageGroupStickerEmoji, userName, chat.title, messageObject.getStickerEmoji());
                                                } else {
                                                    formatString = LocaleController.formatString("NotificationMessageGroupSticker", C0446R.string.NotificationMessageGroupSticker, userName, chat.title);
                                                }
                                                str = formatString;
                                            } else if (messageObject.isGif()) {
                                                if (z) {
                                                }
                                                str = LocaleController.formatString("NotificationMessageGroupGif", C0446R.string.NotificationMessageGroupGif, userName, chat.title);
                                            } else {
                                                if (z) {
                                                }
                                                str = LocaleController.formatString("NotificationMessageGroupDocument", C0446R.string.NotificationMessageGroupDocument, userName, chat.title);
                                            }
                                        }
                                    }
                                }
                                str = LocaleController.formatString("NotificationMessageGroupMap", C0446R.string.NotificationMessageGroupMap, userName, chat.title);
                            }
                        }
                    }
                }
                return str;
            }
        }
        return LocaleController.getString("YouHaveNewMessage", C0446R.string.YouHaveNewMessage);
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent service = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 0);
            int i = MessagesController.getNotificationsSettings(this.currentAccount).getInt("repeat_messages", 60);
            if (i <= 0 || this.personal_count <= 0) {
                this.alarmManager.cancel(service);
            } else {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) ((i * 60) * 1000)), service);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        return (messageObject.messageOwner.to_id == null || messageObject.messageOwner.to_id.chat_id != 0 || messageObject.messageOwner.to_id.channel_id != 0 || (messageObject.messageOwner.action != null && (messageObject.messageOwner.action instanceof TL_messageActionEmpty) == null)) ? null : true;
    }

    private int getNotifyOverride(SharedPreferences sharedPreferences, long j) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("notify2_");
        stringBuilder.append(j);
        int i = sharedPreferences.getInt(stringBuilder.toString(), 0);
        if (i != 3) {
            return i;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("notifyuntil_");
        stringBuilder2.append(j);
        return sharedPreferences.getInt(stringBuilder2.toString(), 0) >= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() ? 2 : i;
    }

    private void dismissNotification() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r3 = this;
        r0 = 0;
        r3.lastNotificationIsNoData = r0;	 Catch:{ Exception -> 0x0074 }
        r1 = notificationManager;	 Catch:{ Exception -> 0x0074 }
        r2 = r3.notificationId;	 Catch:{ Exception -> 0x0074 }
        r1.cancel(r2);	 Catch:{ Exception -> 0x0074 }
        r1 = r3.pushMessages;	 Catch:{ Exception -> 0x0074 }
        r1.clear();	 Catch:{ Exception -> 0x0074 }
        r1 = r3.pushMessagesDict;	 Catch:{ Exception -> 0x0074 }
        r1.clear();	 Catch:{ Exception -> 0x0074 }
        r1 = r3.lastWearNotifiedMessageId;	 Catch:{ Exception -> 0x0074 }
        r1.clear();	 Catch:{ Exception -> 0x0074 }
    L_0x0019:
        r1 = r3.wearNotificationsIds;	 Catch:{ Exception -> 0x0074 }
        r1 = r1.size();	 Catch:{ Exception -> 0x0074 }
        if (r0 >= r1) goto L_0x0035;	 Catch:{ Exception -> 0x0074 }
    L_0x0021:
        r1 = notificationManager;	 Catch:{ Exception -> 0x0074 }
        r2 = r3.wearNotificationsIds;	 Catch:{ Exception -> 0x0074 }
        r2 = r2.valueAt(r0);	 Catch:{ Exception -> 0x0074 }
        r2 = (java.lang.Integer) r2;	 Catch:{ Exception -> 0x0074 }
        r2 = r2.intValue();	 Catch:{ Exception -> 0x0074 }
        r1.cancel(r2);	 Catch:{ Exception -> 0x0074 }
        r0 = r0 + 1;	 Catch:{ Exception -> 0x0074 }
        goto L_0x0019;	 Catch:{ Exception -> 0x0074 }
    L_0x0035:
        r0 = r3.wearNotificationsIds;	 Catch:{ Exception -> 0x0074 }
        r0.clear();	 Catch:{ Exception -> 0x0074 }
        r0 = new org.telegram.messenger.NotificationsController$13;	 Catch:{ Exception -> 0x0074 }
        r0.<init>();	 Catch:{ Exception -> 0x0074 }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Exception -> 0x0074 }
        r0 = org.telegram.messenger.WearDataLayerListenerService.isWatchConnected();	 Catch:{ Exception -> 0x0074 }
        if (r0 == 0) goto L_0x0078;
    L_0x0048:
        r0 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x0078 }
        r0.<init>();	 Catch:{ JSONException -> 0x0078 }
        r1 = "id";	 Catch:{ JSONException -> 0x0078 }
        r2 = r3.currentAccount;	 Catch:{ JSONException -> 0x0078 }
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);	 Catch:{ JSONException -> 0x0078 }
        r2 = r2.getClientUserId();	 Catch:{ JSONException -> 0x0078 }
        r0.put(r1, r2);	 Catch:{ JSONException -> 0x0078 }
        r1 = "cancel_all";	 Catch:{ JSONException -> 0x0078 }
        r2 = 1;	 Catch:{ JSONException -> 0x0078 }
        r0.put(r1, r2);	 Catch:{ JSONException -> 0x0078 }
        r1 = "/notify";	 Catch:{ JSONException -> 0x0078 }
        r0 = r0.toString();	 Catch:{ JSONException -> 0x0078 }
        r2 = "UTF-8";	 Catch:{ JSONException -> 0x0078 }
        r0 = r0.getBytes(r2);	 Catch:{ JSONException -> 0x0078 }
        r2 = "remote_notifications";	 Catch:{ JSONException -> 0x0078 }
        org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r1, r0, r2);	 Catch:{ JSONException -> 0x0078 }
        goto L_0x0078;
    L_0x0074:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x0078:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.dismissNotification():void");
    }

    private void playInChatSound() {
        if (this.inChatSoundEnabled) {
            if (!MediaController.getInstance().isRecordingAudio()) {
                try {
                    if (audioManager.getRingerMode() == 0) {
                        return;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                try {
                    if (getNotifyOverride(MessagesController.getNotificationsSettings(this.currentAccount), this.opened_dialog_id) != 2) {
                        notificationsQueue.postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.NotificationsController$14$1 */
                            class C04271 implements OnLoadCompleteListener {
                                C04271() {
                                }

                                public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                                    if (i2 == 0) {
                                        try {
                                            soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    }
                                }
                            }

                            public void run() {
                                if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundPlay) > 500) {
                                    try {
                                        if (NotificationsController.this.soundPool == null) {
                                            NotificationsController.this.soundPool = new SoundPool(3, 1, 0);
                                            NotificationsController.this.soundPool.setOnLoadCompleteListener(new C04271());
                                        }
                                        if (NotificationsController.this.soundIn == 0 && !NotificationsController.this.soundInLoaded) {
                                            NotificationsController.this.soundInLoaded = true;
                                            NotificationsController.this.soundIn = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, C0446R.raw.sound_in, 1);
                                        }
                                        if (NotificationsController.this.soundIn != 0) {
                                            try {
                                                NotificationsController.this.soundPool.play(NotificationsController.this.soundIn, 1.0f, 1.0f, 1, 0, 1.0f);
                                            } catch (Throwable e) {
                                                FileLog.m3e(e);
                                            }
                                        }
                                    } catch (Throwable e2) {
                                        FileLog.m3e(e2);
                                    }
                                }
                            }
                        });
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
        }
    }

    private void scheduleNotificationDelay(boolean z) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("delay notification start, onlineReason = ");
                stringBuilder.append(z);
                FileLog.m0d(stringBuilder.toString());
            }
            this.notificationDelayWakelock.acquire(10000);
            AndroidUtilities.cancelRunOnUIThread(this.notificationDelayRunnable);
            AndroidUtilities.runOnUIThread(this.notificationDelayRunnable, (long) (z ? true : true));
        } catch (Throwable e) {
            FileLog.m3e(e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    protected void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                int i = Calendar.getInstance().get(11);
                if (i < 11 || i > 22) {
                    NotificationsController.this.scheduleNotificationRepeat();
                    return;
                }
                NotificationsController.notificationManager.cancel(NotificationsController.this.notificationId);
                NotificationsController.this.showOrUpdateNotification(true);
            }
        });
    }

    private boolean isEmptyVibration(long[] jArr) {
        if (jArr != null) {
            if (jArr.length != 0) {
                for (int i = 0; i < jArr.length; i++) {
                    if (jArr[0] != 0) {
                        return false;
                    }
                }
                return 1;
            }
        }
        return false;
    }

    @TargetApi(26)
    private String validateChannelId(long j, String str, long[] jArr, int i, Uri uri, int i2, long[] jArr2, Uri uri2, int i3) {
        String str2;
        long j2 = j;
        long[] jArr3 = jArr;
        int i4 = i;
        Uri uri3 = uri;
        int i5 = i2;
        SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("org.telegram.key");
        stringBuilder.append(j2);
        String stringBuilder2 = stringBuilder.toString();
        String string = notificationsSettings.getString(stringBuilder2, null);
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("_s");
        String string2 = notificationsSettings.getString(stringBuilder3.toString(), null);
        StringBuilder stringBuilder4 = new StringBuilder();
        int i6 = 0;
        while (i6 < jArr3.length) {
            str2 = string;
            stringBuilder4.append(jArr3[i6]);
            i6++;
            string = str2;
        }
        str2 = string;
        stringBuilder4.append(i4);
        if (uri3 != null) {
            stringBuilder4.append(uri.toString());
        }
        stringBuilder4.append(i5);
        String MD5 = Utilities.MD5(stringBuilder4.toString());
        if (str2 == null || string2.equals(MD5)) {
            string2 = str2;
        } else {
            systemNotificationManager.deleteNotificationChannel(str2);
            string2 = null;
        }
        if (string2 == null) {
            StringBuilder stringBuilder5 = new StringBuilder();
            stringBuilder5.append(r0.currentAccount);
            stringBuilder5.append("channel");
            stringBuilder5.append(j2);
            stringBuilder5.append("_");
            stringBuilder5.append(Utilities.random.nextLong());
            string2 = stringBuilder5.toString();
            NotificationChannel notificationChannel = new NotificationChannel(string2, str, i5);
            if (i4 != 0) {
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(i4);
            }
            if (isEmptyVibration(jArr3)) {
                notificationChannel.enableVibration(false);
            } else {
                notificationChannel.enableVibration(true);
                if (jArr3 != null && jArr3.length > 0) {
                    notificationChannel.setVibrationPattern(jArr3);
                }
            }
            if (uri3 != null) {
                Builder builder = new Builder();
                builder.setContentType(4);
                builder.setUsage(5);
                notificationChannel.setSound(uri3, builder.build());
            } else {
                notificationChannel.setSound(null, null);
            }
            systemNotificationManager.createNotificationChannel(notificationChannel);
            Editor putString = notificationsSettings.edit().putString(stringBuilder2, string2);
            StringBuilder stringBuilder6 = new StringBuilder();
            stringBuilder6.append(stringBuilder2);
            stringBuilder6.append("_s");
            putString.putString(stringBuilder6.toString(), MD5).commit();
        }
        return string2;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean r55) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r54 = this;
        r12 = r54;
        r13 = r55;
        r1 = r12.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.isClientActivated();
        if (r1 == 0) goto L_0x0a4c;
    L_0x0010:
        r1 = r12.pushMessages;
        r1 = r1.isEmpty();
        if (r1 == 0) goto L_0x001a;
    L_0x0018:
        goto L_0x0a4c;
    L_0x001a:
        r1 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);	 Catch:{ Exception -> 0x0a46 }
        r1.resumeNetworkMaybe();	 Catch:{ Exception -> 0x0a46 }
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0a46 }
        r14 = 0;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.get(r14);	 Catch:{ Exception -> 0x0a46 }
        r1 = (org.telegram.messenger.MessageObject) r1;	 Catch:{ Exception -> 0x0a46 }
        r2 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2);	 Catch:{ Exception -> 0x0a46 }
        r3 = "dismissDate";	 Catch:{ Exception -> 0x0a46 }
        r3 = r2.getInt(r3, r14);	 Catch:{ Exception -> 0x0a46 }
        r4 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.date;	 Catch:{ Exception -> 0x0a46 }
        if (r4 > r3) goto L_0x0042;	 Catch:{ Exception -> 0x0a46 }
    L_0x003e:
        r54.dismissNotification();	 Catch:{ Exception -> 0x0a46 }
        return;	 Catch:{ Exception -> 0x0a46 }
    L_0x0042:
        r4 = r1.getDialogId();	 Catch:{ Exception -> 0x0a46 }
        r6 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.mentioned;	 Catch:{ Exception -> 0x0a46 }
        if (r6 == 0) goto L_0x0052;	 Catch:{ Exception -> 0x0a46 }
    L_0x004c:
        r6 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.from_id;	 Catch:{ Exception -> 0x0a46 }
        r6 = (long) r6;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0053;	 Catch:{ Exception -> 0x0a46 }
    L_0x0052:
        r6 = r4;	 Catch:{ Exception -> 0x0a46 }
    L_0x0053:
        r1.getId();	 Catch:{ Exception -> 0x0a46 }
        r8 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r8 = r8.to_id;	 Catch:{ Exception -> 0x0a46 }
        r8 = r8.chat_id;	 Catch:{ Exception -> 0x0a46 }
        if (r8 == 0) goto L_0x0065;	 Catch:{ Exception -> 0x0a46 }
    L_0x005e:
        r8 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r8 = r8.to_id;	 Catch:{ Exception -> 0x0a46 }
        r8 = r8.chat_id;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x006b;	 Catch:{ Exception -> 0x0a46 }
    L_0x0065:
        r8 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r8 = r8.to_id;	 Catch:{ Exception -> 0x0a46 }
        r8 = r8.channel_id;	 Catch:{ Exception -> 0x0a46 }
    L_0x006b:
        r9 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r9 = r9.to_id;	 Catch:{ Exception -> 0x0a46 }
        r9 = r9.user_id;	 Catch:{ Exception -> 0x0a46 }
        if (r9 != 0) goto L_0x0078;	 Catch:{ Exception -> 0x0a46 }
    L_0x0073:
        r9 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r9 = r9.from_id;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0088;	 Catch:{ Exception -> 0x0a46 }
    L_0x0078:
        r10 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r10 = org.telegram.messenger.UserConfig.getInstance(r10);	 Catch:{ Exception -> 0x0a46 }
        r10 = r10.getClientUserId();	 Catch:{ Exception -> 0x0a46 }
        if (r9 != r10) goto L_0x0088;	 Catch:{ Exception -> 0x0a46 }
    L_0x0084:
        r9 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r9 = r9.from_id;	 Catch:{ Exception -> 0x0a46 }
    L_0x0088:
        r10 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);	 Catch:{ Exception -> 0x0a46 }
        r11 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x0a46 }
        r10 = r10.getUser(r11);	 Catch:{ Exception -> 0x0a46 }
        if (r8 == 0) goto L_0x00a7;	 Catch:{ Exception -> 0x0a46 }
    L_0x0098:
        r15 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r15 = org.telegram.messenger.MessagesController.getInstance(r15);	 Catch:{ Exception -> 0x0a46 }
        r11 = java.lang.Integer.valueOf(r8);	 Catch:{ Exception -> 0x0a46 }
        r11 = r15.getChat(r11);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x00a8;	 Catch:{ Exception -> 0x0a46 }
    L_0x00a7:
        r11 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x00a8:
        r15 = r12.getNotifyOverride(r2, r6);	 Catch:{ Exception -> 0x0a46 }
        r14 = 2;	 Catch:{ Exception -> 0x0a46 }
        r17 = r3;	 Catch:{ Exception -> 0x0a46 }
        r3 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r13 == 0) goto L_0x00cb;	 Catch:{ Exception -> 0x0a46 }
    L_0x00b2:
        if (r15 == r14) goto L_0x00cb;	 Catch:{ Exception -> 0x0a46 }
    L_0x00b4:
        r14 = "EnableAll";	 Catch:{ Exception -> 0x0a46 }
        r14 = r2.getBoolean(r14, r3);	 Catch:{ Exception -> 0x0a46 }
        if (r14 == 0) goto L_0x00c6;	 Catch:{ Exception -> 0x0a46 }
    L_0x00bc:
        if (r8 == 0) goto L_0x00c9;	 Catch:{ Exception -> 0x0a46 }
    L_0x00be:
        r14 = "EnableGroup";	 Catch:{ Exception -> 0x0a46 }
        r14 = r2.getBoolean(r14, r3);	 Catch:{ Exception -> 0x0a46 }
        if (r14 != 0) goto L_0x00c9;	 Catch:{ Exception -> 0x0a46 }
    L_0x00c6:
        if (r15 != 0) goto L_0x00c9;	 Catch:{ Exception -> 0x0a46 }
    L_0x00c8:
        goto L_0x00cb;	 Catch:{ Exception -> 0x0a46 }
    L_0x00c9:
        r14 = 0;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x00cc;	 Catch:{ Exception -> 0x0a46 }
    L_0x00cb:
        r14 = r3;	 Catch:{ Exception -> 0x0a46 }
    L_0x00cc:
        r18 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Exception -> 0x0a46 }
        if (r14 != 0) goto L_0x016c;	 Catch:{ Exception -> 0x0a46 }
    L_0x00d0:
        r15 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));	 Catch:{ Exception -> 0x0a46 }
        if (r15 != 0) goto L_0x016c;	 Catch:{ Exception -> 0x0a46 }
    L_0x00d4:
        if (r11 == 0) goto L_0x016c;	 Catch:{ Exception -> 0x0a46 }
    L_0x00d6:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r6.<init>();	 Catch:{ Exception -> 0x0a46 }
        r7 = "custom_";	 Catch:{ Exception -> 0x0a46 }
        r6.append(r7);	 Catch:{ Exception -> 0x0a46 }
        r6.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0a46 }
        r7 = 0;	 Catch:{ Exception -> 0x0a46 }
        r6 = r2.getBoolean(r6, r7);	 Catch:{ Exception -> 0x0a46 }
        r7 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;	 Catch:{ Exception -> 0x0a46 }
        if (r6 == 0) goto L_0x011c;	 Catch:{ Exception -> 0x0a46 }
    L_0x00f0:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r6.<init>();	 Catch:{ Exception -> 0x0a46 }
        r15 = "smart_max_count_";	 Catch:{ Exception -> 0x0a46 }
        r6.append(r15);	 Catch:{ Exception -> 0x0a46 }
        r6.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0a46 }
        r15 = 2;	 Catch:{ Exception -> 0x0a46 }
        r6 = r2.getInt(r6, r15);	 Catch:{ Exception -> 0x0a46 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r15.<init>();	 Catch:{ Exception -> 0x0a46 }
        r3 = "smart_delay_";	 Catch:{ Exception -> 0x0a46 }
        r15.append(r3);	 Catch:{ Exception -> 0x0a46 }
        r15.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r3 = r15.toString();	 Catch:{ Exception -> 0x0a46 }
        r7 = r2.getInt(r3, r7);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x011d;	 Catch:{ Exception -> 0x0a46 }
    L_0x011c:
        r6 = 2;	 Catch:{ Exception -> 0x0a46 }
    L_0x011d:
        if (r6 == 0) goto L_0x016c;	 Catch:{ Exception -> 0x0a46 }
    L_0x011f:
        r3 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0a46 }
        r3 = r3.get(r4);	 Catch:{ Exception -> 0x0a46 }
        r3 = (android.graphics.Point) r3;	 Catch:{ Exception -> 0x0a46 }
        if (r3 != 0) goto L_0x013c;	 Catch:{ Exception -> 0x0a46 }
    L_0x0129:
        r3 = new android.graphics.Point;	 Catch:{ Exception -> 0x0a46 }
        r6 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0a46 }
        r6 = r6 / r18;	 Catch:{ Exception -> 0x0a46 }
        r6 = (int) r6;	 Catch:{ Exception -> 0x0a46 }
        r7 = 1;	 Catch:{ Exception -> 0x0a46 }
        r3.<init>(r7, r6);	 Catch:{ Exception -> 0x0a46 }
        r6 = r12.smartNotificationsDialogs;	 Catch:{ Exception -> 0x0a46 }
        r6.put(r4, r3);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x016c;	 Catch:{ Exception -> 0x0a46 }
    L_0x013c:
        r15 = r3.y;	 Catch:{ Exception -> 0x0a46 }
        r15 = r15 + r7;	 Catch:{ Exception -> 0x0a46 }
        r20 = r14;	 Catch:{ Exception -> 0x0a46 }
        r14 = (long) r15;	 Catch:{ Exception -> 0x0a46 }
        r21 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0a46 }
        r21 = r21 / r18;	 Catch:{ Exception -> 0x0a46 }
        r7 = (r14 > r21 ? 1 : (r14 == r21 ? 0 : -1));	 Catch:{ Exception -> 0x0a46 }
        if (r7 >= 0) goto L_0x0158;	 Catch:{ Exception -> 0x0a46 }
    L_0x014c:
        r6 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0a46 }
        r6 = r6 / r18;	 Catch:{ Exception -> 0x0a46 }
        r6 = (int) r6;	 Catch:{ Exception -> 0x0a46 }
        r7 = 1;	 Catch:{ Exception -> 0x0a46 }
        r3.set(r7, r6);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x016e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0158:
        r7 = r3.x;	 Catch:{ Exception -> 0x0a46 }
        if (r7 >= r6) goto L_0x0169;	 Catch:{ Exception -> 0x0a46 }
    L_0x015c:
        r6 = 1;	 Catch:{ Exception -> 0x0a46 }
        r7 = r7 + r6;	 Catch:{ Exception -> 0x0a46 }
        r14 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0a46 }
        r14 = r14 / r18;	 Catch:{ Exception -> 0x0a46 }
        r6 = (int) r14;	 Catch:{ Exception -> 0x0a46 }
        r3.set(r7, r6);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x016e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0169:
        r20 = 1;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x016e;	 Catch:{ Exception -> 0x0a46 }
    L_0x016c:
        r20 = r14;	 Catch:{ Exception -> 0x0a46 }
    L_0x016e:
        r3 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0a46 }
        r3 = r3.getPath();	 Catch:{ Exception -> 0x0a46 }
        r6 = "EnableInAppSounds";	 Catch:{ Exception -> 0x0a46 }
        r7 = 1;	 Catch:{ Exception -> 0x0a46 }
        r6 = r2.getBoolean(r6, r7);	 Catch:{ Exception -> 0x0a46 }
        r14 = "EnableInAppVibrate";	 Catch:{ Exception -> 0x0a46 }
        r14 = r2.getBoolean(r14, r7);	 Catch:{ Exception -> 0x0a46 }
        r15 = "EnableInAppPreview";	 Catch:{ Exception -> 0x0a46 }
        r15 = r2.getBoolean(r15, r7);	 Catch:{ Exception -> 0x0a46 }
        r7 = "EnableInAppPriority";	 Catch:{ Exception -> 0x0a46 }
        r23 = r15;	 Catch:{ Exception -> 0x0a46 }
        r15 = 0;	 Catch:{ Exception -> 0x0a46 }
        r7 = r2.getBoolean(r7, r15);	 Catch:{ Exception -> 0x0a46 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r15.<init>();	 Catch:{ Exception -> 0x0a46 }
        r13 = "custom_";	 Catch:{ Exception -> 0x0a46 }
        r15.append(r13);	 Catch:{ Exception -> 0x0a46 }
        r15.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r13 = r15.toString();	 Catch:{ Exception -> 0x0a46 }
        r15 = 0;	 Catch:{ Exception -> 0x0a46 }
        r13 = r2.getBoolean(r13, r15);	 Catch:{ Exception -> 0x0a46 }
        if (r13 == 0) goto L_0x01f9;	 Catch:{ Exception -> 0x0a46 }
    L_0x01a8:
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r15.<init>();	 Catch:{ Exception -> 0x0a46 }
        r24 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = "vibrate_";	 Catch:{ Exception -> 0x0a46 }
        r15.append(r1);	 Catch:{ Exception -> 0x0a46 }
        r15.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r1 = r15.toString();	 Catch:{ Exception -> 0x0a46 }
        r15 = 0;	 Catch:{ Exception -> 0x0a46 }
        r1 = r2.getInt(r1, r15);	 Catch:{ Exception -> 0x0a46 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r15.<init>();	 Catch:{ Exception -> 0x0a46 }
        r25 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = "priority_";	 Catch:{ Exception -> 0x0a46 }
        r15.append(r1);	 Catch:{ Exception -> 0x0a46 }
        r15.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r1 = r15.toString();	 Catch:{ Exception -> 0x0a46 }
        r15 = 3;	 Catch:{ Exception -> 0x0a46 }
        r1 = r2.getInt(r1, r15);	 Catch:{ Exception -> 0x0a46 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r15.<init>();	 Catch:{ Exception -> 0x0a46 }
        r26 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = "sound_path_";	 Catch:{ Exception -> 0x0a46 }
        r15.append(r1);	 Catch:{ Exception -> 0x0a46 }
        r15.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r1 = r15.toString();	 Catch:{ Exception -> 0x0a46 }
        r15 = 0;	 Catch:{ Exception -> 0x0a46 }
        r1 = r2.getString(r1, r15);	 Catch:{ Exception -> 0x0a46 }
        r27 = r10;	 Catch:{ Exception -> 0x0a46 }
        r28 = r11;	 Catch:{ Exception -> 0x0a46 }
        r15 = r25;	 Catch:{ Exception -> 0x0a46 }
        r10 = r26;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0202;	 Catch:{ Exception -> 0x0a46 }
    L_0x01f9:
        r24 = r1;	 Catch:{ Exception -> 0x0a46 }
        r27 = r10;	 Catch:{ Exception -> 0x0a46 }
        r28 = r11;	 Catch:{ Exception -> 0x0a46 }
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        r10 = 3;	 Catch:{ Exception -> 0x0a46 }
        r15 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x0202:
        if (r8 == 0) goto L_0x023a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0204:
        if (r1 == 0) goto L_0x020e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0206:
        r21 = r1.equals(r3);	 Catch:{ Exception -> 0x0a46 }
        if (r21 == 0) goto L_0x020e;	 Catch:{ Exception -> 0x0a46 }
    L_0x020c:
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0216;	 Catch:{ Exception -> 0x0a46 }
    L_0x020e:
        if (r1 != 0) goto L_0x0216;	 Catch:{ Exception -> 0x0a46 }
    L_0x0210:
        r1 = "GroupSoundPath";	 Catch:{ Exception -> 0x0a46 }
        r1 = r2.getString(r1, r3);	 Catch:{ Exception -> 0x0a46 }
    L_0x0216:
        r11 = "vibrate_group";	 Catch:{ Exception -> 0x0a46 }
        r29 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        r11 = r2.getInt(r11, r1);	 Catch:{ Exception -> 0x0a46 }
        r1 = "priority_group";	 Catch:{ Exception -> 0x0a46 }
        r30 = r11;	 Catch:{ Exception -> 0x0a46 }
        r11 = 1;	 Catch:{ Exception -> 0x0a46 }
        r1 = r2.getInt(r1, r11);	 Catch:{ Exception -> 0x0a46 }
        r11 = "GroupLed";	 Catch:{ Exception -> 0x0a46 }
        r31 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = -16776961; // 0xffffffffff0000ff float:-1.7014636E38 double:NaN;	 Catch:{ Exception -> 0x0a46 }
        r11 = r2.getInt(r11, r1);	 Catch:{ Exception -> 0x0a46 }
        r21 = r11;	 Catch:{ Exception -> 0x0a46 }
        r11 = r29;	 Catch:{ Exception -> 0x0a46 }
        r1 = r30;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x027e;	 Catch:{ Exception -> 0x0a46 }
    L_0x023a:
        if (r9 == 0) goto L_0x0275;	 Catch:{ Exception -> 0x0a46 }
    L_0x023c:
        if (r1 == 0) goto L_0x0246;	 Catch:{ Exception -> 0x0a46 }
    L_0x023e:
        r11 = r1.equals(r3);	 Catch:{ Exception -> 0x0a46 }
        if (r11 == 0) goto L_0x0246;	 Catch:{ Exception -> 0x0a46 }
    L_0x0244:
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x024f;	 Catch:{ Exception -> 0x0a46 }
    L_0x0246:
        if (r1 != 0) goto L_0x024f;	 Catch:{ Exception -> 0x0a46 }
    L_0x0248:
        r1 = "GlobalSoundPath";	 Catch:{ Exception -> 0x0a46 }
        r11 = r2.getString(r1, r3);	 Catch:{ Exception -> 0x0a46 }
        r1 = r11;	 Catch:{ Exception -> 0x0a46 }
    L_0x024f:
        r11 = "vibrate_messages";	 Catch:{ Exception -> 0x0a46 }
        r32 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        r11 = r2.getInt(r11, r1);	 Catch:{ Exception -> 0x0a46 }
        r1 = "priority_group";	 Catch:{ Exception -> 0x0a46 }
        r33 = r11;	 Catch:{ Exception -> 0x0a46 }
        r11 = 1;	 Catch:{ Exception -> 0x0a46 }
        r1 = r2.getInt(r1, r11);	 Catch:{ Exception -> 0x0a46 }
        r11 = "MessagesLed";	 Catch:{ Exception -> 0x0a46 }
        r34 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = -16776961; // 0xffffffffff0000ff float:-1.7014636E38 double:NaN;	 Catch:{ Exception -> 0x0a46 }
        r11 = r2.getInt(r11, r1);	 Catch:{ Exception -> 0x0a46 }
        r21 = r11;	 Catch:{ Exception -> 0x0a46 }
        r11 = r32;	 Catch:{ Exception -> 0x0a46 }
        r1 = r33;	 Catch:{ Exception -> 0x0a46 }
        r31 = r34;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x027e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0275:
        r11 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = -16776961; // 0xffffffffff0000ff float:-1.7014636E38 double:NaN;	 Catch:{ Exception -> 0x0a46 }
        r21 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        r31 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x027e:
        if (r13 == 0) goto L_0x02b0;	 Catch:{ Exception -> 0x0a46 }
    L_0x0280:
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r13.<init>();	 Catch:{ Exception -> 0x0a46 }
        r35 = r11;	 Catch:{ Exception -> 0x0a46 }
        r11 = "color_";	 Catch:{ Exception -> 0x0a46 }
        r13.append(r11);	 Catch:{ Exception -> 0x0a46 }
        r13.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r11 = r13.toString();	 Catch:{ Exception -> 0x0a46 }
        r11 = r2.contains(r11);	 Catch:{ Exception -> 0x0a46 }
        if (r11 == 0) goto L_0x02b2;	 Catch:{ Exception -> 0x0a46 }
    L_0x0299:
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r11.<init>();	 Catch:{ Exception -> 0x0a46 }
        r13 = "color_";	 Catch:{ Exception -> 0x0a46 }
        r11.append(r13);	 Catch:{ Exception -> 0x0a46 }
        r11.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x0a46 }
        r13 = 0;	 Catch:{ Exception -> 0x0a46 }
        r21 = r2.getInt(r11, r13);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x02b2;	 Catch:{ Exception -> 0x0a46 }
    L_0x02b0:
        r35 = r11;	 Catch:{ Exception -> 0x0a46 }
    L_0x02b2:
        r2 = 3;	 Catch:{ Exception -> 0x0a46 }
        if (r10 == r2) goto L_0x02b6;	 Catch:{ Exception -> 0x0a46 }
    L_0x02b5:
        goto L_0x02b8;	 Catch:{ Exception -> 0x0a46 }
    L_0x02b6:
        r10 = r31;	 Catch:{ Exception -> 0x0a46 }
    L_0x02b8:
        r11 = 4;	 Catch:{ Exception -> 0x0a46 }
        if (r1 != r11) goto L_0x02bf;	 Catch:{ Exception -> 0x0a46 }
    L_0x02bb:
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        r11 = 2;	 Catch:{ Exception -> 0x0a46 }
        r13 = 1;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x02c1;	 Catch:{ Exception -> 0x0a46 }
    L_0x02bf:
        r11 = 2;	 Catch:{ Exception -> 0x0a46 }
        r13 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x02c1:
        if (r1 != r11) goto L_0x02ca;	 Catch:{ Exception -> 0x0a46 }
    L_0x02c3:
        r11 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r15 == r11) goto L_0x02d6;	 Catch:{ Exception -> 0x0a46 }
    L_0x02c6:
        if (r15 == r2) goto L_0x02d6;	 Catch:{ Exception -> 0x0a46 }
    L_0x02c8:
        r2 = 2;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x02cb;	 Catch:{ Exception -> 0x0a46 }
    L_0x02ca:
        r2 = r11;	 Catch:{ Exception -> 0x0a46 }
    L_0x02cb:
        if (r1 == r2) goto L_0x02cf;	 Catch:{ Exception -> 0x0a46 }
    L_0x02cd:
        if (r15 == r2) goto L_0x02d6;	 Catch:{ Exception -> 0x0a46 }
    L_0x02cf:
        if (r15 == 0) goto L_0x02d5;	 Catch:{ Exception -> 0x0a46 }
    L_0x02d1:
        r2 = 4;	 Catch:{ Exception -> 0x0a46 }
        if (r15 == r2) goto L_0x02d5;	 Catch:{ Exception -> 0x0a46 }
    L_0x02d4:
        goto L_0x02d6;	 Catch:{ Exception -> 0x0a46 }
    L_0x02d5:
        r15 = r1;	 Catch:{ Exception -> 0x0a46 }
    L_0x02d6:
        r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0a46 }
        if (r1 != 0) goto L_0x02f1;
    L_0x02da:
        if (r6 != 0) goto L_0x02de;
    L_0x02dc:
        r35 = 0;
    L_0x02de:
        if (r14 != 0) goto L_0x02e1;
    L_0x02e0:
        r15 = 2;
    L_0x02e1:
        if (r7 != 0) goto L_0x02e9;
    L_0x02e3:
        r14 = r15;
        r11 = r35;
        r1 = 2;
        r10 = 0;
        goto L_0x02f5;
    L_0x02e9:
        r1 = 2;
        if (r10 != r1) goto L_0x02f2;
    L_0x02ec:
        r14 = r15;
        r11 = r35;
        r10 = 1;
        goto L_0x02f5;
    L_0x02f1:
        r1 = 2;
    L_0x02f2:
        r14 = r15;
        r11 = r35;
    L_0x02f5:
        if (r13 == 0) goto L_0x030b;
    L_0x02f7:
        if (r14 == r1) goto L_0x030b;
    L_0x02f9:
        r1 = audioManager;	 Catch:{ Exception -> 0x0306 }
        r1 = r1.getRingerMode();	 Catch:{ Exception -> 0x0306 }
        if (r1 == 0) goto L_0x030b;
    L_0x0301:
        r2 = 1;
        if (r1 == r2) goto L_0x030b;
    L_0x0304:
        r14 = 2;
        goto L_0x030b;
    L_0x0306:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ Exception -> 0x0a46 }
    L_0x030b:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r2 = 5;	 Catch:{ Exception -> 0x0a46 }
        r6 = 26;	 Catch:{ Exception -> 0x0a46 }
        if (r1 < r6) goto L_0x037a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0312:
        r1 = 2;	 Catch:{ Exception -> 0x0a46 }
        if (r14 != r1) goto L_0x031b;	 Catch:{ Exception -> 0x0a46 }
    L_0x0315:
        r7 = new long[r1];	 Catch:{ Exception -> 0x0a46 }
        r7 = {0, 0};	 Catch:{ Exception -> 0x0a46 }
        goto L_0x033a;	 Catch:{ Exception -> 0x0a46 }
    L_0x031b:
        r1 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r14 != r1) goto L_0x0325;	 Catch:{ Exception -> 0x0a46 }
    L_0x031e:
        r1 = 4;	 Catch:{ Exception -> 0x0a46 }
        r7 = new long[r1];	 Catch:{ Exception -> 0x0a46 }
        r7 = {0, 100, 0, 100};	 Catch:{ Exception -> 0x0a46 }
        goto L_0x033a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0325:
        r1 = 4;	 Catch:{ Exception -> 0x0a46 }
        if (r14 == 0) goto L_0x0337;	 Catch:{ Exception -> 0x0a46 }
    L_0x0328:
        if (r14 != r1) goto L_0x032b;	 Catch:{ Exception -> 0x0a46 }
    L_0x032a:
        goto L_0x0337;	 Catch:{ Exception -> 0x0a46 }
    L_0x032b:
        r1 = 3;	 Catch:{ Exception -> 0x0a46 }
        if (r14 != r1) goto L_0x0335;	 Catch:{ Exception -> 0x0a46 }
    L_0x032e:
        r1 = 2;	 Catch:{ Exception -> 0x0a46 }
        r7 = new long[r1];	 Catch:{ Exception -> 0x0a46 }
        r7 = {0, 1000};	 Catch:{ Exception -> 0x0a46 }
        goto L_0x033a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0335:
        r7 = 0;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x033a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0337:
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        r7 = new long[r1];	 Catch:{ Exception -> 0x0a46 }
    L_0x033a:
        if (r11 == 0) goto L_0x0352;	 Catch:{ Exception -> 0x0a46 }
    L_0x033c:
        r1 = "NoSound";	 Catch:{ Exception -> 0x0a46 }
        r1 = r11.equals(r1);	 Catch:{ Exception -> 0x0a46 }
        if (r1 != 0) goto L_0x0352;	 Catch:{ Exception -> 0x0a46 }
    L_0x0344:
        r1 = r11.equals(r3);	 Catch:{ Exception -> 0x0a46 }
        if (r1 == 0) goto L_0x034d;	 Catch:{ Exception -> 0x0a46 }
    L_0x034a:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0353;	 Catch:{ Exception -> 0x0a46 }
    L_0x034d:
        r1 = android.net.Uri.parse(r11);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0353;	 Catch:{ Exception -> 0x0a46 }
    L_0x0352:
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x0353:
        if (r10 != 0) goto L_0x035a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0355:
        r15 = r1;	 Catch:{ Exception -> 0x0a46 }
        r22 = r7;	 Catch:{ Exception -> 0x0a46 }
        r13 = 3;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x037e;	 Catch:{ Exception -> 0x0a46 }
    L_0x035a:
        r13 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r10 == r13) goto L_0x0375;	 Catch:{ Exception -> 0x0a46 }
    L_0x035d:
        r13 = 2;	 Catch:{ Exception -> 0x0a46 }
        if (r10 != r13) goto L_0x0361;	 Catch:{ Exception -> 0x0a46 }
    L_0x0360:
        goto L_0x0375;	 Catch:{ Exception -> 0x0a46 }
    L_0x0361:
        r13 = 4;	 Catch:{ Exception -> 0x0a46 }
        if (r10 != r13) goto L_0x0369;	 Catch:{ Exception -> 0x0a46 }
    L_0x0364:
        r15 = r1;	 Catch:{ Exception -> 0x0a46 }
        r22 = r7;	 Catch:{ Exception -> 0x0a46 }
        r13 = 1;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x037e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0369:
        if (r10 != r2) goto L_0x0370;	 Catch:{ Exception -> 0x0a46 }
    L_0x036b:
        r15 = r1;	 Catch:{ Exception -> 0x0a46 }
        r22 = r7;	 Catch:{ Exception -> 0x0a46 }
        r13 = 2;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x037e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0370:
        r15 = r1;	 Catch:{ Exception -> 0x0a46 }
        r22 = r7;	 Catch:{ Exception -> 0x0a46 }
        r13 = 0;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x037e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0375:
        r15 = r1;	 Catch:{ Exception -> 0x0a46 }
        r22 = r7;	 Catch:{ Exception -> 0x0a46 }
        r13 = 4;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x037e;	 Catch:{ Exception -> 0x0a46 }
    L_0x037a:
        r13 = 0;	 Catch:{ Exception -> 0x0a46 }
        r15 = 0;	 Catch:{ Exception -> 0x0a46 }
        r22 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x037e:
        if (r20 == 0) goto L_0x0385;	 Catch:{ Exception -> 0x0a46 }
    L_0x0380:
        r7 = 0;	 Catch:{ Exception -> 0x0a46 }
        r10 = 0;	 Catch:{ Exception -> 0x0a46 }
        r11 = 0;	 Catch:{ Exception -> 0x0a46 }
        r14 = 0;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0387;	 Catch:{ Exception -> 0x0a46 }
    L_0x0385:
        r7 = r21;	 Catch:{ Exception -> 0x0a46 }
    L_0x0387:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0a46 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r6 = org.telegram.ui.LaunchActivity.class;	 Catch:{ Exception -> 0x0a46 }
        r1.<init>(r2, r6);	 Catch:{ Exception -> 0x0a46 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r2.<init>();	 Catch:{ Exception -> 0x0a46 }
        r6 = "com.tmessages.openchat";	 Catch:{ Exception -> 0x0a46 }
        r2.append(r6);	 Catch:{ Exception -> 0x0a46 }
        r36 = r13;	 Catch:{ Exception -> 0x0a46 }
        r37 = r14;	 Catch:{ Exception -> 0x0a46 }
        r13 = java.lang.Math.random();	 Catch:{ Exception -> 0x0a46 }
        r2.append(r13);	 Catch:{ Exception -> 0x0a46 }
        r6 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;	 Catch:{ Exception -> 0x0a46 }
        r2.append(r6);	 Catch:{ Exception -> 0x0a46 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0a46 }
        r1.setAction(r2);	 Catch:{ Exception -> 0x0a46 }
        r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;	 Catch:{ Exception -> 0x0a46 }
        r1.setFlags(r2);	 Catch:{ Exception -> 0x0a46 }
        r2 = (int) r4;	 Catch:{ Exception -> 0x0a46 }
        if (r2 == 0) goto L_0x0461;	 Catch:{ Exception -> 0x0a46 }
    L_0x03bb:
        r13 = r12.pushDialogs;	 Catch:{ Exception -> 0x0a46 }
        r13 = r13.size();	 Catch:{ Exception -> 0x0a46 }
        r14 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r13 != r14) goto L_0x03d3;	 Catch:{ Exception -> 0x0a46 }
    L_0x03c4:
        if (r8 == 0) goto L_0x03cc;	 Catch:{ Exception -> 0x0a46 }
    L_0x03c6:
        r9 = "chatId";	 Catch:{ Exception -> 0x0a46 }
        r1.putExtra(r9, r8);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x03d3;	 Catch:{ Exception -> 0x0a46 }
    L_0x03cc:
        if (r9 == 0) goto L_0x03d3;	 Catch:{ Exception -> 0x0a46 }
    L_0x03ce:
        r13 = "userId";	 Catch:{ Exception -> 0x0a46 }
        r1.putExtra(r13, r9);	 Catch:{ Exception -> 0x0a46 }
    L_0x03d3:
        r9 = 0;	 Catch:{ Exception -> 0x0a46 }
        r13 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r9);	 Catch:{ Exception -> 0x0a46 }
        if (r13 != 0) goto L_0x0456;	 Catch:{ Exception -> 0x0a46 }
    L_0x03da:
        r9 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0a46 }
        if (r9 == 0) goto L_0x03e0;	 Catch:{ Exception -> 0x0a46 }
    L_0x03de:
        goto L_0x0456;	 Catch:{ Exception -> 0x0a46 }
    L_0x03e0:
        r9 = r12.pushDialogs;	 Catch:{ Exception -> 0x0a46 }
        r9 = r9.size();	 Catch:{ Exception -> 0x0a46 }
        r13 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r9 != r13) goto L_0x0456;	 Catch:{ Exception -> 0x0a46 }
    L_0x03e9:
        r13 = 0;	 Catch:{ Exception -> 0x0a46 }
        if (r28 == 0) goto L_0x041d;	 Catch:{ Exception -> 0x0a46 }
    L_0x03ed:
        r9 = r28;	 Catch:{ Exception -> 0x0a46 }
        r6 = r9.photo;	 Catch:{ Exception -> 0x0a46 }
        if (r6 == 0) goto L_0x041a;	 Catch:{ Exception -> 0x0a46 }
    L_0x03f3:
        r6 = r9.photo;	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.photo_small;	 Catch:{ Exception -> 0x0a46 }
        if (r6 == 0) goto L_0x041a;	 Catch:{ Exception -> 0x0a46 }
    L_0x03f9:
        r6 = r9.photo;	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.photo_small;	 Catch:{ Exception -> 0x0a46 }
        r38 = r7;	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.volume_id;	 Catch:{ Exception -> 0x0a46 }
        r21 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1));	 Catch:{ Exception -> 0x0a46 }
        if (r21 == 0) goto L_0x044f;	 Catch:{ Exception -> 0x0a46 }
    L_0x0405:
        r6 = r9.photo;	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.photo_small;	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.local_id;	 Catch:{ Exception -> 0x0a46 }
        if (r6 == 0) goto L_0x044f;	 Catch:{ Exception -> 0x0a46 }
    L_0x040d:
        r6 = r9.photo;	 Catch:{ Exception -> 0x0a46 }
        r6 = r6.photo_small;	 Catch:{ Exception -> 0x0a46 }
        r39 = r10;	 Catch:{ Exception -> 0x0a46 }
        r40 = r11;	 Catch:{ Exception -> 0x0a46 }
        r11 = r6;	 Catch:{ Exception -> 0x0a46 }
        r6 = r27;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x047f;	 Catch:{ Exception -> 0x0a46 }
    L_0x041a:
        r38 = r7;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x044f;	 Catch:{ Exception -> 0x0a46 }
    L_0x041d:
        r38 = r7;	 Catch:{ Exception -> 0x0a46 }
        r9 = r28;	 Catch:{ Exception -> 0x0a46 }
        if (r27 == 0) goto L_0x044f;	 Catch:{ Exception -> 0x0a46 }
    L_0x0423:
        r6 = r27;	 Catch:{ Exception -> 0x0a46 }
        r7 = r6.photo;	 Catch:{ Exception -> 0x0a46 }
        if (r7 == 0) goto L_0x044a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0429:
        r7 = r6.photo;	 Catch:{ Exception -> 0x0a46 }
        r7 = r7.photo_small;	 Catch:{ Exception -> 0x0a46 }
        if (r7 == 0) goto L_0x044a;	 Catch:{ Exception -> 0x0a46 }
    L_0x042f:
        r7 = r6.photo;	 Catch:{ Exception -> 0x0a46 }
        r7 = r7.photo_small;	 Catch:{ Exception -> 0x0a46 }
        r39 = r10;	 Catch:{ Exception -> 0x0a46 }
        r40 = r11;	 Catch:{ Exception -> 0x0a46 }
        r10 = r7.volume_id;	 Catch:{ Exception -> 0x0a46 }
        r7 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1));	 Catch:{ Exception -> 0x0a46 }
        if (r7 == 0) goto L_0x047e;	 Catch:{ Exception -> 0x0a46 }
    L_0x043d:
        r7 = r6.photo;	 Catch:{ Exception -> 0x0a46 }
        r7 = r7.photo_small;	 Catch:{ Exception -> 0x0a46 }
        r7 = r7.local_id;	 Catch:{ Exception -> 0x0a46 }
        if (r7 == 0) goto L_0x047e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0445:
        r7 = r6.photo;	 Catch:{ Exception -> 0x0a46 }
        r11 = r7.photo_small;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x047f;	 Catch:{ Exception -> 0x0a46 }
    L_0x044a:
        r39 = r10;	 Catch:{ Exception -> 0x0a46 }
        r40 = r11;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x047e;	 Catch:{ Exception -> 0x0a46 }
    L_0x044f:
        r39 = r10;	 Catch:{ Exception -> 0x0a46 }
        r40 = r11;	 Catch:{ Exception -> 0x0a46 }
        r6 = r27;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x047e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0456:
        r38 = r7;	 Catch:{ Exception -> 0x0a46 }
        r39 = r10;	 Catch:{ Exception -> 0x0a46 }
        r40 = r11;	 Catch:{ Exception -> 0x0a46 }
        r6 = r27;	 Catch:{ Exception -> 0x0a46 }
        r9 = r28;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x047e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0461:
        r38 = r7;	 Catch:{ Exception -> 0x0a46 }
        r39 = r10;	 Catch:{ Exception -> 0x0a46 }
        r40 = r11;	 Catch:{ Exception -> 0x0a46 }
        r6 = r27;	 Catch:{ Exception -> 0x0a46 }
        r9 = r28;	 Catch:{ Exception -> 0x0a46 }
        r7 = r12.pushDialogs;	 Catch:{ Exception -> 0x0a46 }
        r7 = r7.size();	 Catch:{ Exception -> 0x0a46 }
        r10 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r7 != r10) goto L_0x047e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0474:
        r7 = "encId";	 Catch:{ Exception -> 0x0a46 }
        r10 = 32;	 Catch:{ Exception -> 0x0a46 }
        r13 = r4 >> r10;	 Catch:{ Exception -> 0x0a46 }
        r10 = (int) r13;	 Catch:{ Exception -> 0x0a46 }
        r1.putExtra(r7, r10);	 Catch:{ Exception -> 0x0a46 }
    L_0x047e:
        r11 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x047f:
        r7 = "currentAccount";	 Catch:{ Exception -> 0x0a46 }
        r10 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r1.putExtra(r7, r10);	 Catch:{ Exception -> 0x0a46 }
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r10 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0a46 }
        r13 = 0;	 Catch:{ Exception -> 0x0a46 }
        r1 = android.app.PendingIntent.getActivity(r7, r13, r1, r10);	 Catch:{ Exception -> 0x0a46 }
        if (r8 == 0) goto L_0x0497;	 Catch:{ Exception -> 0x0a46 }
    L_0x0491:
        if (r9 == 0) goto L_0x0494;	 Catch:{ Exception -> 0x0a46 }
    L_0x0493:
        goto L_0x0497;	 Catch:{ Exception -> 0x0a46 }
    L_0x0494:
        r7 = r24;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x049a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0497:
        if (r6 != 0) goto L_0x04a3;	 Catch:{ Exception -> 0x0a46 }
    L_0x0499:
        goto L_0x0494;	 Catch:{ Exception -> 0x0a46 }
    L_0x049a:
        r8 = r7.isFcmMessage();	 Catch:{ Exception -> 0x0a46 }
        if (r8 == 0) goto L_0x04a5;	 Catch:{ Exception -> 0x0a46 }
    L_0x04a0:
        r8 = r7.localName;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x04ae;	 Catch:{ Exception -> 0x0a46 }
    L_0x04a3:
        r7 = r24;	 Catch:{ Exception -> 0x0a46 }
    L_0x04a5:
        if (r9 == 0) goto L_0x04aa;	 Catch:{ Exception -> 0x0a46 }
    L_0x04a7:
        r8 = r9.title;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x04ae;	 Catch:{ Exception -> 0x0a46 }
    L_0x04aa:
        r8 = org.telegram.messenger.UserObject.getUserName(r6);	 Catch:{ Exception -> 0x0a46 }
    L_0x04ae:
        if (r2 == 0) goto L_0x04c8;	 Catch:{ Exception -> 0x0a46 }
    L_0x04b0:
        r2 = r12.pushDialogs;	 Catch:{ Exception -> 0x0a46 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0a46 }
        r10 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r2 > r10) goto L_0x04c8;	 Catch:{ Exception -> 0x0a46 }
    L_0x04b9:
        r2 = 0;	 Catch:{ Exception -> 0x0a46 }
        r10 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r2);	 Catch:{ Exception -> 0x0a46 }
        if (r10 != 0) goto L_0x04c8;	 Catch:{ Exception -> 0x0a46 }
    L_0x04c0:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0a46 }
        if (r2 == 0) goto L_0x04c5;	 Catch:{ Exception -> 0x0a46 }
    L_0x04c4:
        goto L_0x04c8;	 Catch:{ Exception -> 0x0a46 }
    L_0x04c5:
        r2 = r8;	 Catch:{ Exception -> 0x0a46 }
        r10 = 1;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x04d2;	 Catch:{ Exception -> 0x0a46 }
    L_0x04c8:
        r2 = "AppName";	 Catch:{ Exception -> 0x0a46 }
        r10 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;	 Catch:{ Exception -> 0x0a46 }
        r2 = org.telegram.messenger.LocaleController.getString(r2, r10);	 Catch:{ Exception -> 0x0a46 }
        r10 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x04d2:
        r13 = org.telegram.messenger.UserConfig.getActivatedAccountsCount();	 Catch:{ Exception -> 0x0a46 }
        r14 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r13 <= r14) goto L_0x0510;	 Catch:{ Exception -> 0x0a46 }
    L_0x04d9:
        r13 = r12.pushDialogs;	 Catch:{ Exception -> 0x0a46 }
        r13 = r13.size();	 Catch:{ Exception -> 0x0a46 }
        if (r13 != r14) goto L_0x04f0;	 Catch:{ Exception -> 0x0a46 }
    L_0x04e1:
        r13 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r13 = org.telegram.messenger.UserConfig.getInstance(r13);	 Catch:{ Exception -> 0x0a46 }
        r13 = r13.getCurrentUser();	 Catch:{ Exception -> 0x0a46 }
        r13 = org.telegram.messenger.UserObject.getFirstName(r13);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0512;	 Catch:{ Exception -> 0x0a46 }
    L_0x04f0:
        r13 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r13.<init>();	 Catch:{ Exception -> 0x0a46 }
        r14 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r14 = org.telegram.messenger.UserConfig.getInstance(r14);	 Catch:{ Exception -> 0x0a46 }
        r14 = r14.getCurrentUser();	 Catch:{ Exception -> 0x0a46 }
        r14 = org.telegram.messenger.UserObject.getFirstName(r14);	 Catch:{ Exception -> 0x0a46 }
        r13.append(r14);	 Catch:{ Exception -> 0x0a46 }
        r14 = "\u30fb";	 Catch:{ Exception -> 0x0a46 }
        r13.append(r14);	 Catch:{ Exception -> 0x0a46 }
        r13 = r13.toString();	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0512;	 Catch:{ Exception -> 0x0a46 }
    L_0x0510:
        r13 = "";	 Catch:{ Exception -> 0x0a46 }
    L_0x0512:
        r14 = r12.pushDialogs;	 Catch:{ Exception -> 0x0a46 }
        r14 = r14.size();	 Catch:{ Exception -> 0x0a46 }
        r41 = r15;	 Catch:{ Exception -> 0x0a46 }
        r15 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r14 != r15) goto L_0x0529;	 Catch:{ Exception -> 0x0a46 }
    L_0x051d:
        r14 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r15 = 23;	 Catch:{ Exception -> 0x0a46 }
        if (r14 >= r15) goto L_0x0524;	 Catch:{ Exception -> 0x0a46 }
    L_0x0523:
        goto L_0x0529;	 Catch:{ Exception -> 0x0a46 }
    L_0x0524:
        r43 = r4;	 Catch:{ Exception -> 0x0a46 }
        r42 = r8;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0583;	 Catch:{ Exception -> 0x0a46 }
    L_0x0529:
        r14 = r12.pushDialogs;	 Catch:{ Exception -> 0x0a46 }
        r14 = r14.size();	 Catch:{ Exception -> 0x0a46 }
        r15 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r14 != r15) goto L_0x054a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0532:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r14.<init>();	 Catch:{ Exception -> 0x0a46 }
        r14.append(r13);	 Catch:{ Exception -> 0x0a46 }
        r13 = "NewMessages";	 Catch:{ Exception -> 0x0a46 }
        r15 = r12.total_unread_count;	 Catch:{ Exception -> 0x0a46 }
        r13 = org.telegram.messenger.LocaleController.formatPluralString(r13, r15);	 Catch:{ Exception -> 0x0a46 }
        r14.append(r13);	 Catch:{ Exception -> 0x0a46 }
        r13 = r14.toString();	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0524;	 Catch:{ Exception -> 0x0a46 }
    L_0x054a:
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r14.<init>();	 Catch:{ Exception -> 0x0a46 }
        r14.append(r13);	 Catch:{ Exception -> 0x0a46 }
        r13 = "NotificationMessagesPeopleDisplayOrder";	 Catch:{ Exception -> 0x0a46 }
        r42 = r8;	 Catch:{ Exception -> 0x0a46 }
        r15 = 2;	 Catch:{ Exception -> 0x0a46 }
        r8 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x0a46 }
        r15 = "NewMessages";	 Catch:{ Exception -> 0x0a46 }
        r43 = r4;	 Catch:{ Exception -> 0x0a46 }
        r4 = r12.total_unread_count;	 Catch:{ Exception -> 0x0a46 }
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r15, r4);	 Catch:{ Exception -> 0x0a46 }
        r5 = 0;	 Catch:{ Exception -> 0x0a46 }
        r8[r5] = r4;	 Catch:{ Exception -> 0x0a46 }
        r4 = "FromChats";	 Catch:{ Exception -> 0x0a46 }
        r5 = r12.pushDialogs;	 Catch:{ Exception -> 0x0a46 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0a46 }
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r4, r5);	 Catch:{ Exception -> 0x0a46 }
        r5 = 1;	 Catch:{ Exception -> 0x0a46 }
        r8[r5] = r4;	 Catch:{ Exception -> 0x0a46 }
        r4 = NUM; // 0x7f0c0472 float:1.86115E38 double:1.0530979607E-314;	 Catch:{ Exception -> 0x0a46 }
        r4 = org.telegram.messenger.LocaleController.formatString(r13, r4, r8);	 Catch:{ Exception -> 0x0a46 }
        r14.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r13 = r14.toString();	 Catch:{ Exception -> 0x0a46 }
    L_0x0583:
        r4 = new android.support.v4.app.NotificationCompat$Builder;	 Catch:{ Exception -> 0x0a46 }
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r4.<init>(r5);	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.setContentTitle(r2);	 Catch:{ Exception -> 0x0a46 }
        r5 = NUM; // 0x7f070167 float:1.7945306E38 double:1.0529356804E-314;	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.setSmallIcon(r5);	 Catch:{ Exception -> 0x0a46 }
        r5 = 1;	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.setAutoCancel(r5);	 Catch:{ Exception -> 0x0a46 }
        r5 = r12.total_unread_count;	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.setNumber(r5);	 Catch:{ Exception -> 0x0a46 }
        r1 = r4.setContentIntent(r1);	 Catch:{ Exception -> 0x0a46 }
        r4 = r12.notificationGroup;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.setGroup(r4);	 Catch:{ Exception -> 0x0a46 }
        r4 = 1;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.setGroupSummary(r4);	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.setShowWhen(r4);	 Catch:{ Exception -> 0x0a46 }
        r4 = r7.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.date;	 Catch:{ Exception -> 0x0a46 }
        r4 = (long) r4;	 Catch:{ Exception -> 0x0a46 }
        r4 = r4 * r18;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.setWhen(r4);	 Catch:{ Exception -> 0x0a46 }
        r4 = -13851168; // 0xffffffffff2ca5e0 float:-2.2948849E38 double:NaN;	 Catch:{ Exception -> 0x0a46 }
        r14 = r1.setColor(r4);	 Catch:{ Exception -> 0x0a46 }
        r1 = "msg";	 Catch:{ Exception -> 0x0a46 }
        r14.setCategory(r1);	 Catch:{ Exception -> 0x0a46 }
        if (r9 != 0) goto L_0x05f0;	 Catch:{ Exception -> 0x0a46 }
    L_0x05cc:
        if (r6 == 0) goto L_0x05f0;	 Catch:{ Exception -> 0x0a46 }
    L_0x05ce:
        r1 = r6.phone;	 Catch:{ Exception -> 0x0a46 }
        if (r1 == 0) goto L_0x05f0;	 Catch:{ Exception -> 0x0a46 }
    L_0x05d2:
        r1 = r6.phone;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.length();	 Catch:{ Exception -> 0x0a46 }
        if (r1 <= 0) goto L_0x05f0;	 Catch:{ Exception -> 0x0a46 }
    L_0x05da:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r1.<init>();	 Catch:{ Exception -> 0x0a46 }
        r4 = "tel:+";	 Catch:{ Exception -> 0x0a46 }
        r1.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r4 = r6.phone;	 Catch:{ Exception -> 0x0a46 }
        r1.append(r4);	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0a46 }
        r14.addPerson(r1);	 Catch:{ Exception -> 0x0a46 }
    L_0x05f0:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.size();	 Catch:{ Exception -> 0x0a46 }
        r4 = 10;	 Catch:{ Exception -> 0x0a46 }
        r5 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r1 != r5) goto L_0x0679;	 Catch:{ Exception -> 0x0a46 }
    L_0x05fb:
        r1 = r12.pushMessages;	 Catch:{ Exception -> 0x0a46 }
        r6 = 0;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.get(r6);	 Catch:{ Exception -> 0x0a46 }
        r1 = (org.telegram.messenger.MessageObject) r1;	 Catch:{ Exception -> 0x0a46 }
        r8 = new boolean[r5];	 Catch:{ Exception -> 0x0a46 }
        r5 = r12.getStringForMessage(r1, r6, r8);	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.silent;	 Catch:{ Exception -> 0x0a46 }
        if (r5 != 0) goto L_0x0611;	 Catch:{ Exception -> 0x0a46 }
    L_0x0610:
        return;	 Catch:{ Exception -> 0x0a46 }
    L_0x0611:
        if (r10 == 0) goto L_0x0662;	 Catch:{ Exception -> 0x0a46 }
    L_0x0613:
        if (r9 == 0) goto L_0x062d;	 Catch:{ Exception -> 0x0a46 }
    L_0x0615:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r6.<init>();	 Catch:{ Exception -> 0x0a46 }
        r8 = " @ ";	 Catch:{ Exception -> 0x0a46 }
        r6.append(r8);	 Catch:{ Exception -> 0x0a46 }
        r6.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r2 = r6.toString();	 Catch:{ Exception -> 0x0a46 }
        r6 = "";	 Catch:{ Exception -> 0x0a46 }
        r2 = r5.replace(r2, r6);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0663;	 Catch:{ Exception -> 0x0a46 }
    L_0x062d:
        r6 = 0;	 Catch:{ Exception -> 0x0a46 }
        r8 = r8[r6];	 Catch:{ Exception -> 0x0a46 }
        if (r8 == 0) goto L_0x064a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0632:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r6.<init>();	 Catch:{ Exception -> 0x0a46 }
        r6.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r2 = ": ";	 Catch:{ Exception -> 0x0a46 }
        r6.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r2 = r6.toString();	 Catch:{ Exception -> 0x0a46 }
        r6 = "";	 Catch:{ Exception -> 0x0a46 }
        r2 = r5.replace(r2, r6);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0663;	 Catch:{ Exception -> 0x0a46 }
    L_0x064a:
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r6.<init>();	 Catch:{ Exception -> 0x0a46 }
        r6.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r2 = " ";	 Catch:{ Exception -> 0x0a46 }
        r6.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r2 = r6.toString();	 Catch:{ Exception -> 0x0a46 }
        r6 = "";	 Catch:{ Exception -> 0x0a46 }
        r2 = r5.replace(r2, r6);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0663;	 Catch:{ Exception -> 0x0a46 }
    L_0x0662:
        r2 = r5;	 Catch:{ Exception -> 0x0a46 }
    L_0x0663:
        r14.setContentText(r2);	 Catch:{ Exception -> 0x0a46 }
        r6 = new android.support.v4.app.NotificationCompat$BigTextStyle;	 Catch:{ Exception -> 0x0a46 }
        r6.<init>();	 Catch:{ Exception -> 0x0a46 }
        r2 = r6.bigText(r2);	 Catch:{ Exception -> 0x0a46 }
        r14.setStyle(r2);	 Catch:{ Exception -> 0x0a46 }
        r15 = r1;	 Catch:{ Exception -> 0x0a46 }
        r46 = r3;	 Catch:{ Exception -> 0x0a46 }
        r47 = r11;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0739;	 Catch:{ Exception -> 0x0a46 }
    L_0x0679:
        r14.setContentText(r13);	 Catch:{ Exception -> 0x0a46 }
        r1 = new android.support.v4.app.NotificationCompat$InboxStyle;	 Catch:{ Exception -> 0x0a46 }
        r1.<init>();	 Catch:{ Exception -> 0x0a46 }
        r1.setBigContentTitle(r2);	 Catch:{ Exception -> 0x0a46 }
        r5 = r12.pushMessages;	 Catch:{ Exception -> 0x0a46 }
        r5 = r5.size();	 Catch:{ Exception -> 0x0a46 }
        r5 = java.lang.Math.min(r4, r5);	 Catch:{ Exception -> 0x0a46 }
        r6 = 1;	 Catch:{ Exception -> 0x0a46 }
        r8 = new boolean[r6];	 Catch:{ Exception -> 0x0a46 }
        r6 = 0;	 Catch:{ Exception -> 0x0a46 }
        r15 = 2;	 Catch:{ Exception -> 0x0a46 }
        r18 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x0695:
        if (r6 >= r5) goto L_0x072d;	 Catch:{ Exception -> 0x0a46 }
    L_0x0697:
        r4 = r12.pushMessages;	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.get(r6);	 Catch:{ Exception -> 0x0a46 }
        r4 = (org.telegram.messenger.MessageObject) r4;	 Catch:{ Exception -> 0x0a46 }
        r46 = r3;	 Catch:{ Exception -> 0x0a46 }
        r45 = r5;	 Catch:{ Exception -> 0x0a46 }
        r5 = 0;	 Catch:{ Exception -> 0x0a46 }
        r3 = r12.getStringForMessage(r4, r5, r8);	 Catch:{ Exception -> 0x0a46 }
        if (r3 == 0) goto L_0x071b;	 Catch:{ Exception -> 0x0a46 }
    L_0x06aa:
        r5 = r4.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r5 = r5.date;	 Catch:{ Exception -> 0x0a46 }
        r47 = r11;	 Catch:{ Exception -> 0x0a46 }
        r11 = r17;	 Catch:{ Exception -> 0x0a46 }
        if (r5 > r11) goto L_0x06b5;	 Catch:{ Exception -> 0x0a46 }
    L_0x06b4:
        goto L_0x071f;	 Catch:{ Exception -> 0x0a46 }
    L_0x06b5:
        r5 = 2;	 Catch:{ Exception -> 0x0a46 }
        if (r15 != r5) goto L_0x06be;	 Catch:{ Exception -> 0x0a46 }
    L_0x06b8:
        r4 = r4.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r15 = r4.silent;	 Catch:{ Exception -> 0x0a46 }
        r18 = r3;	 Catch:{ Exception -> 0x0a46 }
    L_0x06be:
        r4 = r12.pushDialogs;	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.size();	 Catch:{ Exception -> 0x0a46 }
        r5 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r4 != r5) goto L_0x0717;	 Catch:{ Exception -> 0x0a46 }
    L_0x06c7:
        if (r10 == 0) goto L_0x0717;	 Catch:{ Exception -> 0x0a46 }
    L_0x06c9:
        if (r9 == 0) goto L_0x06e3;	 Catch:{ Exception -> 0x0a46 }
    L_0x06cb:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r4.<init>();	 Catch:{ Exception -> 0x0a46 }
        r5 = " @ ";	 Catch:{ Exception -> 0x0a46 }
        r4.append(r5);	 Catch:{ Exception -> 0x0a46 }
        r4.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0a46 }
        r5 = "";	 Catch:{ Exception -> 0x0a46 }
        r3 = r3.replace(r4, r5);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0717;	 Catch:{ Exception -> 0x0a46 }
    L_0x06e3:
        r4 = 0;	 Catch:{ Exception -> 0x0a46 }
        r5 = r8[r4];	 Catch:{ Exception -> 0x0a46 }
        if (r5 == 0) goto L_0x0700;	 Catch:{ Exception -> 0x0a46 }
    L_0x06e8:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r4.<init>();	 Catch:{ Exception -> 0x0a46 }
        r4.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r5 = ": ";	 Catch:{ Exception -> 0x0a46 }
        r4.append(r5);	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0a46 }
        r5 = "";	 Catch:{ Exception -> 0x0a46 }
        r3 = r3.replace(r4, r5);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0717;	 Catch:{ Exception -> 0x0a46 }
    L_0x0700:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r4.<init>();	 Catch:{ Exception -> 0x0a46 }
        r4.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r5 = " ";	 Catch:{ Exception -> 0x0a46 }
        r4.append(r5);	 Catch:{ Exception -> 0x0a46 }
        r4 = r4.toString();	 Catch:{ Exception -> 0x0a46 }
        r5 = "";	 Catch:{ Exception -> 0x0a46 }
        r3 = r3.replace(r4, r5);	 Catch:{ Exception -> 0x0a46 }
    L_0x0717:
        r1.addLine(r3);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x071f;	 Catch:{ Exception -> 0x0a46 }
    L_0x071b:
        r47 = r11;	 Catch:{ Exception -> 0x0a46 }
        r11 = r17;	 Catch:{ Exception -> 0x0a46 }
    L_0x071f:
        r6 = r6 + 1;	 Catch:{ Exception -> 0x0a46 }
        r17 = r11;	 Catch:{ Exception -> 0x0a46 }
        r5 = r45;	 Catch:{ Exception -> 0x0a46 }
        r3 = r46;	 Catch:{ Exception -> 0x0a46 }
        r11 = r47;	 Catch:{ Exception -> 0x0a46 }
        r4 = 10;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0695;	 Catch:{ Exception -> 0x0a46 }
    L_0x072d:
        r46 = r3;	 Catch:{ Exception -> 0x0a46 }
        r47 = r11;	 Catch:{ Exception -> 0x0a46 }
        r1.setSummaryText(r13);	 Catch:{ Exception -> 0x0a46 }
        r14.setStyle(r1);	 Catch:{ Exception -> 0x0a46 }
        r5 = r18;	 Catch:{ Exception -> 0x0a46 }
    L_0x0739:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0a46 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r3 = org.telegram.messenger.NotificationDismissReceiver.class;	 Catch:{ Exception -> 0x0a46 }
        r1.<init>(r2, r3);	 Catch:{ Exception -> 0x0a46 }
        r2 = "messageDate";	 Catch:{ Exception -> 0x0a46 }
        r3 = r7.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r3 = r3.date;	 Catch:{ Exception -> 0x0a46 }
        r1.putExtra(r2, r3);	 Catch:{ Exception -> 0x0a46 }
        r2 = "currentAccount";	 Catch:{ Exception -> 0x0a46 }
        r3 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r1.putExtra(r2, r3);	 Catch:{ Exception -> 0x0a46 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r3 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;	 Catch:{ Exception -> 0x0a46 }
        r4 = 1;	 Catch:{ Exception -> 0x0a46 }
        r1 = android.app.PendingIntent.getBroadcast(r2, r4, r1, r3);	 Catch:{ Exception -> 0x0a46 }
        r14.setDeleteIntent(r1);	 Catch:{ Exception -> 0x0a46 }
        if (r47 == 0) goto L_0x07aa;	 Catch:{ Exception -> 0x0a46 }
    L_0x0760:
        r1 = org.telegram.messenger.ImageLoader.getInstance();	 Catch:{ Exception -> 0x0a46 }
        r2 = "50_50";	 Catch:{ Exception -> 0x0a46 }
        r6 = r47;	 Catch:{ Exception -> 0x0a46 }
        r4 = 0;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.getImageFromMemory(r6, r4, r2);	 Catch:{ Exception -> 0x0a46 }
        if (r1 == 0) goto L_0x0777;	 Catch:{ Exception -> 0x0a46 }
    L_0x076f:
        r1 = r1.getBitmap();	 Catch:{ Exception -> 0x0a46 }
        r14.setLargeIcon(r1);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x07ab;
    L_0x0777:
        r1 = 1;
        r2 = org.telegram.messenger.FileLoader.getPathToAttach(r6, r1);	 Catch:{ Throwable -> 0x07ab }
        r1 = r2.exists();	 Catch:{ Throwable -> 0x07ab }
        if (r1 == 0) goto L_0x07ab;	 Catch:{ Throwable -> 0x07ab }
    L_0x0782:
        r1 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;	 Catch:{ Throwable -> 0x07ab }
        r6 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;	 Catch:{ Throwable -> 0x07ab }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Throwable -> 0x07ab }
        r6 = (float) r6;	 Catch:{ Throwable -> 0x07ab }
        r1 = r1 / r6;	 Catch:{ Throwable -> 0x07ab }
        r6 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x07ab }
        r6.<init>();	 Catch:{ Throwable -> 0x07ab }
        r8 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x07ab }
        r8 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1));	 Catch:{ Throwable -> 0x07ab }
        if (r8 >= 0) goto L_0x0799;	 Catch:{ Throwable -> 0x07ab }
    L_0x0797:
        r1 = 1;	 Catch:{ Throwable -> 0x07ab }
        goto L_0x079a;	 Catch:{ Throwable -> 0x07ab }
    L_0x0799:
        r1 = (int) r1;	 Catch:{ Throwable -> 0x07ab }
    L_0x079a:
        r6.inSampleSize = r1;	 Catch:{ Throwable -> 0x07ab }
        r1 = r2.getAbsolutePath();	 Catch:{ Throwable -> 0x07ab }
        r1 = android.graphics.BitmapFactory.decodeFile(r1, r6);	 Catch:{ Throwable -> 0x07ab }
        if (r1 == 0) goto L_0x07ab;	 Catch:{ Throwable -> 0x07ab }
    L_0x07a6:
        r14.setLargeIcon(r1);	 Catch:{ Throwable -> 0x07ab }
        goto L_0x07ab;
    L_0x07aa:
        r4 = 0;
    L_0x07ab:
        r11 = r55;
        if (r11 == 0) goto L_0x07f6;
    L_0x07af:
        r1 = 1;
        if (r15 != r1) goto L_0x07b3;
    L_0x07b2:
        goto L_0x07f6;
    L_0x07b3:
        if (r39 != 0) goto L_0x07c2;
    L_0x07b5:
        r1 = 0;
        r14.setPriority(r1);	 Catch:{ Exception -> 0x0a46 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r2 = 26;	 Catch:{ Exception -> 0x0a46 }
        if (r1 < r2) goto L_0x0803;	 Catch:{ Exception -> 0x0a46 }
    L_0x07bf:
        r1 = 1;	 Catch:{ Exception -> 0x0a46 }
        r8 = 3;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0805;	 Catch:{ Exception -> 0x0a46 }
    L_0x07c2:
        r10 = r39;	 Catch:{ Exception -> 0x0a46 }
        r1 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r10 == r1) goto L_0x07ea;	 Catch:{ Exception -> 0x0a46 }
    L_0x07c7:
        r1 = 2;	 Catch:{ Exception -> 0x0a46 }
        if (r10 != r1) goto L_0x07cc;	 Catch:{ Exception -> 0x0a46 }
    L_0x07ca:
        r1 = 1;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x07ea;	 Catch:{ Exception -> 0x0a46 }
    L_0x07cc:
        r1 = 4;	 Catch:{ Exception -> 0x0a46 }
        if (r10 != r1) goto L_0x07dc;	 Catch:{ Exception -> 0x0a46 }
    L_0x07cf:
        r1 = -2;	 Catch:{ Exception -> 0x0a46 }
        r14.setPriority(r1);	 Catch:{ Exception -> 0x0a46 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r2 = 26;	 Catch:{ Exception -> 0x0a46 }
        if (r1 < r2) goto L_0x0803;	 Catch:{ Exception -> 0x0a46 }
    L_0x07d9:
        r1 = 1;	 Catch:{ Exception -> 0x0a46 }
        r8 = 1;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0805;	 Catch:{ Exception -> 0x0a46 }
    L_0x07dc:
        r1 = 5;	 Catch:{ Exception -> 0x0a46 }
        if (r10 != r1) goto L_0x0803;	 Catch:{ Exception -> 0x0a46 }
    L_0x07df:
        r1 = -1;	 Catch:{ Exception -> 0x0a46 }
        r14.setPriority(r1);	 Catch:{ Exception -> 0x0a46 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r2 = 26;	 Catch:{ Exception -> 0x0a46 }
        if (r1 < r2) goto L_0x0803;	 Catch:{ Exception -> 0x0a46 }
    L_0x07e9:
        goto L_0x0800;	 Catch:{ Exception -> 0x0a46 }
    L_0x07ea:
        r14.setPriority(r1);	 Catch:{ Exception -> 0x0a46 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r2 = 26;	 Catch:{ Exception -> 0x0a46 }
        if (r1 < r2) goto L_0x0803;	 Catch:{ Exception -> 0x0a46 }
    L_0x07f3:
        r1 = 1;	 Catch:{ Exception -> 0x0a46 }
        r8 = 4;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0805;	 Catch:{ Exception -> 0x0a46 }
    L_0x07f6:
        r1 = -1;	 Catch:{ Exception -> 0x0a46 }
        r14.setPriority(r1);	 Catch:{ Exception -> 0x0a46 }
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r2 = 26;	 Catch:{ Exception -> 0x0a46 }
        if (r1 < r2) goto L_0x0803;	 Catch:{ Exception -> 0x0a46 }
    L_0x0800:
        r1 = 1;	 Catch:{ Exception -> 0x0a46 }
        r8 = 2;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0805;	 Catch:{ Exception -> 0x0a46 }
    L_0x0803:
        r1 = 1;	 Catch:{ Exception -> 0x0a46 }
        r8 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x0805:
        if (r15 == r1) goto L_0x08dd;	 Catch:{ Exception -> 0x0a46 }
    L_0x0807:
        if (r20 != 0) goto L_0x08d9;	 Catch:{ Exception -> 0x0a46 }
    L_0x0809:
        r1 = org.telegram.messenger.ApplicationLoader.mainInterfacePaused;	 Catch:{ Exception -> 0x0a46 }
        if (r1 != 0) goto L_0x080f;	 Catch:{ Exception -> 0x0a46 }
    L_0x080d:
        if (r23 == 0) goto L_0x083e;	 Catch:{ Exception -> 0x0a46 }
    L_0x080f:
        r1 = r5.length();	 Catch:{ Exception -> 0x0a46 }
        r2 = 100;	 Catch:{ Exception -> 0x0a46 }
        if (r1 <= r2) goto L_0x083b;	 Catch:{ Exception -> 0x0a46 }
    L_0x0817:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0a46 }
        r1.<init>();	 Catch:{ Exception -> 0x0a46 }
        r2 = 100;	 Catch:{ Exception -> 0x0a46 }
        r6 = 0;	 Catch:{ Exception -> 0x0a46 }
        r2 = r5.substring(r6, r2);	 Catch:{ Exception -> 0x0a46 }
        r5 = 32;	 Catch:{ Exception -> 0x0a46 }
        r6 = 10;	 Catch:{ Exception -> 0x0a46 }
        r2 = r2.replace(r6, r5);	 Catch:{ Exception -> 0x0a46 }
        r2 = r2.trim();	 Catch:{ Exception -> 0x0a46 }
        r1.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r2 = "...";	 Catch:{ Exception -> 0x0a46 }
        r1.append(r2);	 Catch:{ Exception -> 0x0a46 }
        r5 = r1.toString();	 Catch:{ Exception -> 0x0a46 }
    L_0x083b:
        r14.setTicker(r5);	 Catch:{ Exception -> 0x0a46 }
    L_0x083e:
        r1 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.isRecordingAudio();	 Catch:{ Exception -> 0x0a46 }
        if (r1 != 0) goto L_0x0881;	 Catch:{ Exception -> 0x0a46 }
    L_0x0848:
        if (r40 == 0) goto L_0x0881;	 Catch:{ Exception -> 0x0a46 }
    L_0x084a:
        r1 = "NoSound";	 Catch:{ Exception -> 0x0a46 }
        r2 = r40;	 Catch:{ Exception -> 0x0a46 }
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0a46 }
        if (r1 != 0) goto L_0x0881;	 Catch:{ Exception -> 0x0a46 }
    L_0x0854:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r5 = 26;	 Catch:{ Exception -> 0x0a46 }
        if (r1 < r5) goto L_0x086a;	 Catch:{ Exception -> 0x0a46 }
    L_0x085a:
        r1 = r46;	 Catch:{ Exception -> 0x0a46 }
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0a46 }
        if (r1 == 0) goto L_0x0865;	 Catch:{ Exception -> 0x0a46 }
    L_0x0862:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0882;	 Catch:{ Exception -> 0x0a46 }
    L_0x0865:
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0882;	 Catch:{ Exception -> 0x0a46 }
    L_0x086a:
        r1 = r46;	 Catch:{ Exception -> 0x0a46 }
        r1 = r2.equals(r1);	 Catch:{ Exception -> 0x0a46 }
        if (r1 == 0) goto L_0x0879;	 Catch:{ Exception -> 0x0a46 }
    L_0x0872:
        r1 = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI;	 Catch:{ Exception -> 0x0a46 }
        r5 = 5;	 Catch:{ Exception -> 0x0a46 }
        r14.setSound(r1, r5);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0881;	 Catch:{ Exception -> 0x0a46 }
    L_0x0879:
        r5 = 5;	 Catch:{ Exception -> 0x0a46 }
        r1 = android.net.Uri.parse(r2);	 Catch:{ Exception -> 0x0a46 }
        r14.setSound(r1, r5);	 Catch:{ Exception -> 0x0a46 }
    L_0x0881:
        r1 = r4;	 Catch:{ Exception -> 0x0a46 }
    L_0x0882:
        if (r38 == 0) goto L_0x088e;	 Catch:{ Exception -> 0x0a46 }
    L_0x0884:
        r2 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Exception -> 0x0a46 }
        r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Exception -> 0x0a46 }
        r6 = r38;	 Catch:{ Exception -> 0x0a46 }
        r14.setLights(r6, r2, r5);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0890;	 Catch:{ Exception -> 0x0a46 }
    L_0x088e:
        r6 = r38;	 Catch:{ Exception -> 0x0a46 }
    L_0x0890:
        r2 = r37;	 Catch:{ Exception -> 0x0a46 }
        r5 = 2;	 Catch:{ Exception -> 0x0a46 }
        if (r2 == r5) goto L_0x08cc;	 Catch:{ Exception -> 0x0a46 }
    L_0x0895:
        r5 = org.telegram.messenger.MediaController.getInstance();	 Catch:{ Exception -> 0x0a46 }
        r5 = r5.isRecordingAudio();	 Catch:{ Exception -> 0x0a46 }
        if (r5 == 0) goto L_0x08a2;	 Catch:{ Exception -> 0x0a46 }
    L_0x089f:
        r2 = 2;	 Catch:{ Exception -> 0x0a46 }
        r5 = 1;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x08ce;	 Catch:{ Exception -> 0x0a46 }
    L_0x08a2:
        r5 = 1;	 Catch:{ Exception -> 0x0a46 }
        if (r2 != r5) goto L_0x08b1;	 Catch:{ Exception -> 0x0a46 }
    L_0x08a5:
        r9 = 4;	 Catch:{ Exception -> 0x0a46 }
        r2 = new long[r9];	 Catch:{ Exception -> 0x0a46 }
        r2 = {0, 100, 0, 100};	 Catch:{ Exception -> 0x0a46 }
        r14.setVibrate(r2);	 Catch:{ Exception -> 0x0a46 }
        r10 = r1;	 Catch:{ Exception -> 0x0a46 }
        r9 = r2;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x08eb;	 Catch:{ Exception -> 0x0a46 }
    L_0x08b1:
        if (r2 == 0) goto L_0x08c4;	 Catch:{ Exception -> 0x0a46 }
    L_0x08b3:
        r9 = 4;	 Catch:{ Exception -> 0x0a46 }
        if (r2 != r9) goto L_0x08b7;	 Catch:{ Exception -> 0x0a46 }
    L_0x08b6:
        goto L_0x08c4;	 Catch:{ Exception -> 0x0a46 }
    L_0x08b7:
        r9 = 3;	 Catch:{ Exception -> 0x0a46 }
        if (r2 != r9) goto L_0x08d6;	 Catch:{ Exception -> 0x0a46 }
    L_0x08ba:
        r2 = 2;	 Catch:{ Exception -> 0x0a46 }
        r4 = new long[r2];	 Catch:{ Exception -> 0x0a46 }
        r4 = {0, 1000};	 Catch:{ Exception -> 0x0a46 }
        r14.setVibrate(r4);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x08d6;	 Catch:{ Exception -> 0x0a46 }
    L_0x08c4:
        r2 = 2;	 Catch:{ Exception -> 0x0a46 }
        r14.setDefaults(r2);	 Catch:{ Exception -> 0x0a46 }
        r2 = 0;	 Catch:{ Exception -> 0x0a46 }
        r4 = new long[r2];	 Catch:{ Exception -> 0x0a46 }
        goto L_0x08d6;	 Catch:{ Exception -> 0x0a46 }
    L_0x08cc:
        r5 = 1;	 Catch:{ Exception -> 0x0a46 }
        r2 = 2;	 Catch:{ Exception -> 0x0a46 }
    L_0x08ce:
        r4 = new long[r2];	 Catch:{ Exception -> 0x0a46 }
        r4 = {0, 0};	 Catch:{ Exception -> 0x0a46 }
        r14.setVibrate(r4);	 Catch:{ Exception -> 0x0a46 }
    L_0x08d6:
        r10 = r1;	 Catch:{ Exception -> 0x0a46 }
        r9 = r4;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x08eb;	 Catch:{ Exception -> 0x0a46 }
    L_0x08d9:
        r6 = r38;	 Catch:{ Exception -> 0x0a46 }
        r5 = 1;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x08e0;	 Catch:{ Exception -> 0x0a46 }
    L_0x08dd:
        r5 = r1;	 Catch:{ Exception -> 0x0a46 }
        r6 = r38;	 Catch:{ Exception -> 0x0a46 }
    L_0x08e0:
        r1 = 2;	 Catch:{ Exception -> 0x0a46 }
        r2 = new long[r1];	 Catch:{ Exception -> 0x0a46 }
        r2 = {0, 0};	 Catch:{ Exception -> 0x0a46 }
        r14.setVibrate(r2);	 Catch:{ Exception -> 0x0a46 }
        r9 = r2;	 Catch:{ Exception -> 0x0a46 }
        r10 = r4;	 Catch:{ Exception -> 0x0a46 }
    L_0x08eb:
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        r2 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r1);	 Catch:{ Exception -> 0x0a46 }
        if (r2 != 0) goto L_0x09bb;	 Catch:{ Exception -> 0x0a46 }
    L_0x08f2:
        r1 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;	 Catch:{ Exception -> 0x0a46 }
        if (r1 != 0) goto L_0x09bb;	 Catch:{ Exception -> 0x0a46 }
    L_0x08f6:
        r1 = r7.getDialogId();	 Catch:{ Exception -> 0x0a46 }
        r15 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;	 Catch:{ Exception -> 0x0a46 }
        r4 = (r1 > r15 ? 1 : (r1 == r15 ? 0 : -1));	 Catch:{ Exception -> 0x0a46 }
        if (r4 != 0) goto L_0x09bb;	 Catch:{ Exception -> 0x0a46 }
    L_0x0901:
        r1 = r7.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.reply_markup;	 Catch:{ Exception -> 0x0a46 }
        if (r1 == 0) goto L_0x09bb;	 Catch:{ Exception -> 0x0a46 }
    L_0x0907:
        r1 = r7.messageOwner;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.reply_markup;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.rows;	 Catch:{ Exception -> 0x0a46 }
        r2 = r1.size();	 Catch:{ Exception -> 0x0a46 }
        r4 = 0;	 Catch:{ Exception -> 0x0a46 }
        r15 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x0913:
        if (r4 >= r2) goto L_0x09b6;	 Catch:{ Exception -> 0x0a46 }
    L_0x0915:
        r16 = r1.get(r4);	 Catch:{ Exception -> 0x0a46 }
        r5 = r16;	 Catch:{ Exception -> 0x0a46 }
        r5 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r5;	 Catch:{ Exception -> 0x0a46 }
        r3 = r5.buttons;	 Catch:{ Exception -> 0x0a46 }
        r3 = r3.size();	 Catch:{ Exception -> 0x0a46 }
        r16 = r15;	 Catch:{ Exception -> 0x0a46 }
        r15 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x0926:
        if (r15 >= r3) goto L_0x099f;	 Catch:{ Exception -> 0x0a46 }
    L_0x0928:
        r48 = r1;	 Catch:{ Exception -> 0x0a46 }
        r1 = r5.buttons;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.get(r15);	 Catch:{ Exception -> 0x0a46 }
        r1 = (org.telegram.tgnet.TLRPC.KeyboardButton) r1;	 Catch:{ Exception -> 0x0a46 }
        r49 = r2;	 Catch:{ Exception -> 0x0a46 }
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;	 Catch:{ Exception -> 0x0a46 }
        if (r2 == 0) goto L_0x0982;	 Catch:{ Exception -> 0x0a46 }
    L_0x0938:
        r2 = new android.content.Intent;	 Catch:{ Exception -> 0x0a46 }
        r50 = r3;	 Catch:{ Exception -> 0x0a46 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r51 = r5;	 Catch:{ Exception -> 0x0a46 }
        r5 = org.telegram.messenger.NotificationCallbackReceiver.class;	 Catch:{ Exception -> 0x0a46 }
        r2.<init>(r3, r5);	 Catch:{ Exception -> 0x0a46 }
        r3 = "currentAccount";	 Catch:{ Exception -> 0x0a46 }
        r5 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0a46 }
        r3 = "did";	 Catch:{ Exception -> 0x0a46 }
        r52 = r10;	 Catch:{ Exception -> 0x0a46 }
        r10 = r43;	 Catch:{ Exception -> 0x0a46 }
        r2.putExtra(r3, r10);	 Catch:{ Exception -> 0x0a46 }
        r3 = r1.data;	 Catch:{ Exception -> 0x0a46 }
        if (r3 == 0) goto L_0x0960;	 Catch:{ Exception -> 0x0a46 }
    L_0x0959:
        r3 = "data";	 Catch:{ Exception -> 0x0a46 }
        r5 = r1.data;	 Catch:{ Exception -> 0x0a46 }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0a46 }
    L_0x0960:
        r3 = "mid";	 Catch:{ Exception -> 0x0a46 }
        r5 = r7.getId();	 Catch:{ Exception -> 0x0a46 }
        r2.putExtra(r3, r5);	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.text;	 Catch:{ Exception -> 0x0a46 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r5 = r12.lastButtonId;	 Catch:{ Exception -> 0x0a46 }
        r53 = r7;	 Catch:{ Exception -> 0x0a46 }
        r7 = r5 + 1;	 Catch:{ Exception -> 0x0a46 }
        r12.lastButtonId = r7;	 Catch:{ Exception -> 0x0a46 }
        r7 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;	 Catch:{ Exception -> 0x0a46 }
        r2 = android.app.PendingIntent.getBroadcast(r3, r5, r2, r7);	 Catch:{ Exception -> 0x0a46 }
        r3 = 0;	 Catch:{ Exception -> 0x0a46 }
        r14.addAction(r3, r1, r2);	 Catch:{ Exception -> 0x0a46 }
        r16 = 1;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x098c;	 Catch:{ Exception -> 0x0a46 }
    L_0x0982:
        r50 = r3;	 Catch:{ Exception -> 0x0a46 }
        r51 = r5;	 Catch:{ Exception -> 0x0a46 }
        r53 = r7;	 Catch:{ Exception -> 0x0a46 }
        r52 = r10;	 Catch:{ Exception -> 0x0a46 }
        r10 = r43;	 Catch:{ Exception -> 0x0a46 }
    L_0x098c:
        r15 = r15 + 1;	 Catch:{ Exception -> 0x0a46 }
        r43 = r10;	 Catch:{ Exception -> 0x0a46 }
        r1 = r48;	 Catch:{ Exception -> 0x0a46 }
        r2 = r49;	 Catch:{ Exception -> 0x0a46 }
        r3 = r50;	 Catch:{ Exception -> 0x0a46 }
        r5 = r51;	 Catch:{ Exception -> 0x0a46 }
        r10 = r52;	 Catch:{ Exception -> 0x0a46 }
        r7 = r53;	 Catch:{ Exception -> 0x0a46 }
        r11 = r55;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0926;	 Catch:{ Exception -> 0x0a46 }
    L_0x099f:
        r48 = r1;	 Catch:{ Exception -> 0x0a46 }
        r49 = r2;	 Catch:{ Exception -> 0x0a46 }
        r53 = r7;	 Catch:{ Exception -> 0x0a46 }
        r52 = r10;	 Catch:{ Exception -> 0x0a46 }
        r10 = r43;	 Catch:{ Exception -> 0x0a46 }
        r4 = r4 + 1;	 Catch:{ Exception -> 0x0a46 }
        r15 = r16;	 Catch:{ Exception -> 0x0a46 }
        r10 = r52;	 Catch:{ Exception -> 0x0a46 }
        r3 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;	 Catch:{ Exception -> 0x0a46 }
        r5 = 1;	 Catch:{ Exception -> 0x0a46 }
        r11 = r55;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0913;	 Catch:{ Exception -> 0x0a46 }
    L_0x09b6:
        r52 = r10;	 Catch:{ Exception -> 0x0a46 }
        r10 = r43;	 Catch:{ Exception -> 0x0a46 }
        goto L_0x09c0;	 Catch:{ Exception -> 0x0a46 }
    L_0x09bb:
        r52 = r10;	 Catch:{ Exception -> 0x0a46 }
        r10 = r43;	 Catch:{ Exception -> 0x0a46 }
        r15 = 0;	 Catch:{ Exception -> 0x0a46 }
    L_0x09c0:
        if (r15 != 0) goto L_0x0a1d;	 Catch:{ Exception -> 0x0a46 }
    L_0x09c2:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r2 = 24;	 Catch:{ Exception -> 0x0a46 }
        if (r1 >= r2) goto L_0x0a1d;	 Catch:{ Exception -> 0x0a46 }
    L_0x09c8:
        r1 = org.telegram.messenger.SharedConfig.passcodeHash;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.length();	 Catch:{ Exception -> 0x0a46 }
        if (r1 != 0) goto L_0x0a1d;	 Catch:{ Exception -> 0x0a46 }
    L_0x09d0:
        r1 = r54.hasMessagesToReply();	 Catch:{ Exception -> 0x0a46 }
        if (r1 == 0) goto L_0x0a1d;	 Catch:{ Exception -> 0x0a46 }
    L_0x09d6:
        r1 = new android.content.Intent;	 Catch:{ Exception -> 0x0a46 }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r3 = org.telegram.messenger.PopupReplyReceiver.class;	 Catch:{ Exception -> 0x0a46 }
        r1.<init>(r2, r3);	 Catch:{ Exception -> 0x0a46 }
        r2 = "currentAccount";	 Catch:{ Exception -> 0x0a46 }
        r3 = r12.currentAccount;	 Catch:{ Exception -> 0x0a46 }
        r1.putExtra(r2, r3);	 Catch:{ Exception -> 0x0a46 }
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r3 = 19;	 Catch:{ Exception -> 0x0a46 }
        if (r2 > r3) goto L_0x0a05;	 Catch:{ Exception -> 0x0a46 }
    L_0x09ec:
        r2 = NUM; // 0x7f0700ab float:1.7944925E38 double:1.0529355875E-314;	 Catch:{ Exception -> 0x0a46 }
        r3 = "Reply";	 Catch:{ Exception -> 0x0a46 }
        r4 = NUM; // 0x7f0c055b float:1.8611973E38 double:1.053098076E-314;	 Catch:{ Exception -> 0x0a46 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0a46 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r5 = 2;	 Catch:{ Exception -> 0x0a46 }
        r7 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;	 Catch:{ Exception -> 0x0a46 }
        r1 = android.app.PendingIntent.getBroadcast(r4, r5, r1, r7);	 Catch:{ Exception -> 0x0a46 }
        r14.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0a1d;	 Catch:{ Exception -> 0x0a46 }
    L_0x0a05:
        r2 = NUM; // 0x7f0700aa float:1.7944923E38 double:1.052935587E-314;	 Catch:{ Exception -> 0x0a46 }
        r3 = "Reply";	 Catch:{ Exception -> 0x0a46 }
        r4 = NUM; // 0x7f0c055b float:1.8611973E38 double:1.053098076E-314;	 Catch:{ Exception -> 0x0a46 }
        r3 = org.telegram.messenger.LocaleController.getString(r3, r4);	 Catch:{ Exception -> 0x0a46 }
        r4 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0a46 }
        r5 = 2;	 Catch:{ Exception -> 0x0a46 }
        r7 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;	 Catch:{ Exception -> 0x0a46 }
        r1 = android.app.PendingIntent.getBroadcast(r4, r5, r1, r7);	 Catch:{ Exception -> 0x0a46 }
        r14.addAction(r2, r3, r1);	 Catch:{ Exception -> 0x0a46 }
    L_0x0a1d:
        r1 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0a46 }
        r2 = 26;	 Catch:{ Exception -> 0x0a46 }
        if (r1 < r2) goto L_0x0a3a;	 Catch:{ Exception -> 0x0a46 }
    L_0x0a23:
        r1 = r12;	 Catch:{ Exception -> 0x0a46 }
        r2 = r10;	 Catch:{ Exception -> 0x0a46 }
        r4 = r42;	 Catch:{ Exception -> 0x0a46 }
        r5 = r9;	 Catch:{ Exception -> 0x0a46 }
        r7 = r52;	 Catch:{ Exception -> 0x0a46 }
        r9 = r22;	 Catch:{ Exception -> 0x0a46 }
        r10 = r41;	 Catch:{ Exception -> 0x0a46 }
        r15 = r55;	 Catch:{ Exception -> 0x0a46 }
        r11 = r36;	 Catch:{ Exception -> 0x0a46 }
        r1 = r1.validateChannelId(r2, r4, r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x0a46 }
        r14.setChannelId(r1);	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0a3c;	 Catch:{ Exception -> 0x0a46 }
    L_0x0a3a:
        r15 = r55;	 Catch:{ Exception -> 0x0a46 }
    L_0x0a3c:
        r12.showExtraNotifications(r14, r15, r13);	 Catch:{ Exception -> 0x0a46 }
        r1 = 0;	 Catch:{ Exception -> 0x0a46 }
        r12.lastNotificationIsNoData = r1;	 Catch:{ Exception -> 0x0a46 }
        r54.scheduleNotificationRepeat();	 Catch:{ Exception -> 0x0a46 }
        goto L_0x0a4b;
    L_0x0a46:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
    L_0x0a4b:
        return;
    L_0x0a4c:
        r54.dismissNotification();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showOrUpdateNotification(boolean):void");
    }

    @android.annotation.SuppressLint({"InlinedApi"})
    private void showExtraNotifications(android.support.v4.app.NotificationCompat.Builder r52, boolean r53, java.lang.String r54) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r51 = this;
        r0 = r51;
        r1 = r52.build();
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 18;
        if (r2 >= r3) goto L_0x0014;
    L_0x000c:
        r2 = notificationManager;
        r3 = r0.notificationId;
        r2.notify(r3, r1);
        return;
    L_0x0014:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new android.util.LongSparseArray;
        r3.<init>();
        r4 = 0;
        r5 = r4;
    L_0x0020:
        r6 = r0.pushMessages;
        r6 = r6.size();
        if (r5 >= r6) goto L_0x0051;
    L_0x0028:
        r6 = r0.pushMessages;
        r6 = r6.get(r5);
        r6 = (org.telegram.messenger.MessageObject) r6;
        r7 = r6.getDialogId();
        r9 = r3.get(r7);
        r9 = (java.util.ArrayList) r9;
        if (r9 != 0) goto L_0x004b;
    L_0x003c:
        r9 = new java.util.ArrayList;
        r9.<init>();
        r3.put(r7, r9);
        r7 = java.lang.Long.valueOf(r7);
        r2.add(r4, r7);
    L_0x004b:
        r9.add(r6);
        r5 = r5 + 1;
        goto L_0x0020;
    L_0x0051:
        r5 = r0.wearNotificationsIds;
        r5 = r5.clone();
        r6 = r0.wearNotificationsIds;
        r6.clear();
        r6 = new java.util.ArrayList;
        r6.<init>();
        r7 = org.telegram.messenger.WearDataLayerListenerService.isWatchConnected();
        if (r7 == 0) goto L_0x006d;
    L_0x0067:
        r7 = new org.json.JSONArray;
        r7.<init>();
        goto L_0x006e;
    L_0x006d:
        r7 = 0;
    L_0x006e:
        r9 = r2.size();
        r10 = r4;
    L_0x0073:
        if (r10 >= r9) goto L_0x0831;
    L_0x0075:
        r11 = r2.get(r10);
        r11 = (java.lang.Long) r11;
        r11 = r11.longValue();
        r13 = r3.get(r11);
        r13 = (java.util.ArrayList) r13;
        r14 = r13.get(r4);
        r14 = (org.telegram.messenger.MessageObject) r14;
        r14 = r14.getId();
        r15 = (int) r11;
        r8 = 32;
        r16 = r2;
        r17 = r3;
        r2 = r11 >> r8;
        r2 = (int) r2;
        r3 = r5.get(r11);
        r3 = (java.lang.Integer) r3;
        if (r3 != 0) goto L_0x00ad;
    L_0x00a1:
        if (r15 == 0) goto L_0x00a8;
    L_0x00a3:
        r3 = java.lang.Integer.valueOf(r15);
        goto L_0x00b0;
    L_0x00a8:
        r3 = java.lang.Integer.valueOf(r2);
        goto L_0x00b0;
    L_0x00ad:
        r5.remove(r11);
    L_0x00b0:
        if (r7 == 0) goto L_0x00b8;
    L_0x00b2:
        r8 = new org.json.JSONObject;
        r8.<init>();
        goto L_0x00b9;
    L_0x00b8:
        r8 = 0;
    L_0x00b9:
        r19 = r13.get(r4);
        r4 = r19;
        r4 = (org.telegram.messenger.MessageObject) r4;
        r20 = r8;
        r8 = r4.messageOwner;
        r8 = r8.date;
        r21 = r9;
        if (r15 == 0) goto L_0x01b4;
    L_0x00cb:
        r22 = 0;
        if (r15 <= 0) goto L_0x0137;
    L_0x00cf:
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getInstance(r9);
        r24 = r5;
        r5 = java.lang.Integer.valueOf(r15);
        r5 = r9.getUser(r5);
        if (r5 != 0) goto L_0x00f6;
    L_0x00e1:
        r9 = r4.isFcmMessage();
        if (r9 == 0) goto L_0x00ef;
    L_0x00e7:
        r4 = r4.localName;
        r29 = r4;
        r4 = r5;
        r25 = r10;
        goto L_0x0129;
    L_0x00ef:
        r31 = r1;
        r1 = r6;
        r25 = r10;
        goto L_0x01cb;
    L_0x00f6:
        r4 = org.telegram.messenger.UserObject.getUserName(r5);
        r9 = r5.photo;
        if (r9 == 0) goto L_0x0124;
    L_0x00fe:
        r9 = r5.photo;
        r9 = r9.photo_small;
        if (r9 == 0) goto L_0x0124;
    L_0x0104:
        r9 = r5.photo;
        r9 = r9.photo_small;
        r25 = r10;
        r9 = r9.volume_id;
        r19 = (r9 > r22 ? 1 : (r9 == r22 ? 0 : -1));
        if (r19 == 0) goto L_0x0126;
    L_0x0110:
        r9 = r5.photo;
        r9 = r9.photo_small;
        r9 = r9.local_id;
        if (r9 == 0) goto L_0x0126;
    L_0x0118:
        r9 = r5.photo;
        r9 = r9.photo_small;
        r29 = r4;
        r4 = r5;
        r10 = r9;
        r9 = r20;
        r5 = 0;
        goto L_0x012d;
    L_0x0124:
        r25 = r10;
    L_0x0126:
        r29 = r4;
        r4 = r5;
    L_0x0129:
        r9 = r20;
        r5 = 0;
        r10 = 0;
    L_0x012d:
        r19 = 0;
        r26 = 0;
        r27 = 0;
    L_0x0133:
        r30 = 1;
        goto L_0x01f7;
    L_0x0137:
        r24 = r5;
        r25 = r10;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r9 = -r15;
        r9 = java.lang.Integer.valueOf(r9);
        r5 = r5.getChat(r9);
        if (r5 != 0) goto L_0x0168;
    L_0x014c:
        r9 = r4.isFcmMessage();
        if (r9 == 0) goto L_0x01c8;
    L_0x0152:
        r9 = r4.isMegagroup();
        r10 = r4.localName;
        r4 = r4.localChannel;
        r27 = r4;
        r19 = r5;
        r26 = r9;
        r29 = r10;
        r9 = r20;
    L_0x0164:
        r4 = 0;
        r5 = 0;
        r10 = 0;
        goto L_0x0133;
    L_0x0168:
        r4 = r5.megagroup;
        r9 = org.telegram.messenger.ChatObject.isChannel(r5);
        if (r9 == 0) goto L_0x0176;
    L_0x0170:
        r9 = r5.megagroup;
        if (r9 != 0) goto L_0x0176;
    L_0x0174:
        r9 = 1;
        goto L_0x0177;
    L_0x0176:
        r9 = 0;
    L_0x0177:
        r10 = r5.title;
        r26 = r4;
        r4 = r5.photo;
        if (r4 == 0) goto L_0x01a9;
    L_0x017f:
        r4 = r5.photo;
        r4 = r4.photo_small;
        if (r4 == 0) goto L_0x01a9;
    L_0x0185:
        r4 = r5.photo;
        r4 = r4.photo_small;
        r27 = r9;
        r28 = r10;
        r9 = r4.volume_id;
        r4 = (r9 > r22 ? 1 : (r9 == r22 ? 0 : -1));
        if (r4 == 0) goto L_0x01ad;
    L_0x0193:
        r4 = r5.photo;
        r4 = r4.photo_small;
        r4 = r4.local_id;
        if (r4 == 0) goto L_0x01ad;
    L_0x019b:
        r4 = r5.photo;
        r4 = r4.photo_small;
        r10 = r4;
        r19 = r5;
        r9 = r20;
        r29 = r28;
        r4 = 0;
        r5 = 0;
        goto L_0x0133;
    L_0x01a9:
        r27 = r9;
        r28 = r10;
    L_0x01ad:
        r19 = r5;
        r9 = r20;
        r29 = r28;
        goto L_0x0164;
    L_0x01b4:
        r24 = r5;
        r25 = r10;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r5 = java.lang.Integer.valueOf(r2);
        r4 = r4.getEncryptedChat(r5);
        if (r4 != 0) goto L_0x01ce;
    L_0x01c8:
        r31 = r1;
        r1 = r6;
    L_0x01cb:
        r4 = 0;
        goto L_0x0822;
    L_0x01ce:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r4 = r4.user_id;
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r5.getUser(r4);
        if (r4 != 0) goto L_0x01e1;
    L_0x01e0:
        goto L_0x01c8;
    L_0x01e1:
        r5 = "SecretChatName";
        r9 = NUM; // 0x7f0c05b3 float:1.8612151E38 double:1.0530981193E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r5, r9);
        r29 = r5;
        r5 = 0;
        r9 = 0;
        r10 = 0;
        r19 = 0;
        r26 = 0;
        r27 = 0;
        r30 = 0;
    L_0x01f7:
        r20 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r5);
        if (r20 != 0) goto L_0x020c;
    L_0x01fd:
        r5 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r5 == 0) goto L_0x0202;
    L_0x0201:
        goto L_0x020c;
    L_0x0202:
        r31 = r1;
        r32 = r7;
        r1 = r10;
        r5 = r29;
        r10 = r30;
        goto L_0x021d;
    L_0x020c:
        r5 = "AppName";
        r10 = NUM; // 0x7f0c0075 float:1.860943E38 double:1.0530974563E-314;
        r29 = org.telegram.messenger.LocaleController.getString(r5, r10);
        r31 = r1;
        r32 = r7;
        r5 = r29;
        r1 = 0;
        r10 = 0;
    L_0x021d:
        r7 = new android.support.v4.app.NotificationCompat$CarExtender$UnreadConversation$Builder;
        r7.<init>(r5);
        r34 = r1;
        r33 = r2;
        r1 = (long) r8;
        r22 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r1 = r1 * r22;
        r1 = r7.setLatestTimestamp(r1);
        r2 = new android.content.Intent;
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r35 = r8;
        r8 = org.telegram.messenger.AutoMessageHeardReceiver.class;
        r2.<init>(r7, r8);
        r7 = 32;
        r2.addFlags(r7);
        r7 = "org.telegram.messenger.ACTION_MESSAGE_HEARD";
        r2.setAction(r7);
        r7 = "dialog_id";
        r2.putExtra(r7, r11);
        r7 = "max_id";
        r2.putExtra(r7, r14);
        r7 = "currentAccount";
        r8 = r0.currentAccount;
        r2.putExtra(r7, r8);
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r8 = r3.intValue();
        r36 = r6;
        r6 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r2 = android.app.PendingIntent.getBroadcast(r7, r8, r2, r6);
        r1.setReadPendingIntent(r2);
        if (r27 == 0) goto L_0x0271;
    L_0x0268:
        if (r26 == 0) goto L_0x026b;
    L_0x026a:
        goto L_0x0271;
    L_0x026b:
        r38 = r3;
        r37 = r10;
        goto L_0x033f;
    L_0x0271:
        if (r10 == 0) goto L_0x026b;
    L_0x0273:
        r2 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r2 != 0) goto L_0x026b;
    L_0x0277:
        r2 = new android.content.Intent;
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r8 = org.telegram.messenger.AutoMessageReplyReceiver.class;
        r2.<init>(r7, r8);
        r7 = 32;
        r2.addFlags(r7);
        r7 = "org.telegram.messenger.ACTION_MESSAGE_REPLY";
        r2.setAction(r7);
        r7 = "dialog_id";
        r2.putExtra(r7, r11);
        r7 = "max_id";
        r2.putExtra(r7, r14);
        r7 = "currentAccount";
        r8 = r0.currentAccount;
        r2.putExtra(r7, r8);
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r8 = r3.intValue();
        r2 = android.app.PendingIntent.getBroadcast(r7, r8, r2, r6);
        r7 = new android.support.v4.app.RemoteInput$Builder;
        r8 = "extra_voice_reply";
        r7.<init>(r8);
        r8 = "Reply";
        r6 = NUM; // 0x7f0c055b float:1.8611973E38 double:1.053098076E-314;
        r8 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r7 = r7.setLabel(r8);
        r7 = r7.build();
        r1.setReplyAction(r2, r7);
        r2 = new android.content.Intent;
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r8 = org.telegram.messenger.WearReplyReceiver.class;
        r2.<init>(r7, r8);
        r7 = "dialog_id";
        r2.putExtra(r7, r11);
        r7 = "max_id";
        r2.putExtra(r7, r14);
        r7 = "currentAccount";
        r8 = r0.currentAccount;
        r2.putExtra(r7, r8);
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r8 = r3.intValue();
        r6 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r2 = android.app.PendingIntent.getBroadcast(r7, r8, r2, r6);
        r6 = new android.support.v4.app.RemoteInput$Builder;
        r7 = "extra_voice_reply";
        r6.<init>(r7);
        r7 = "Reply";
        r8 = NUM; // 0x7f0c055b float:1.8611973E38 double:1.053098076E-314;
        r7 = org.telegram.messenger.LocaleController.getString(r7, r8);
        r6 = r6.setLabel(r7);
        r6 = r6.build();
        if (r15 >= 0) goto L_0x0316;
    L_0x0300:
        r7 = "ReplyToGroup";
        r37 = r10;
        r8 = 1;
        r10 = new java.lang.Object[r8];
        r18 = 0;
        r10[r18] = r5;
        r8 = NUM; // 0x7f0c055c float:1.8611975E38 double:1.0530980763E-314;
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);
        r38 = r3;
        r10 = 1;
        goto L_0x032a;
    L_0x0316:
        r37 = r10;
        r18 = 0;
        r7 = "ReplyToUser";
        r8 = NUM; // 0x7f0c055d float:1.8611977E38 double:1.053098077E-314;
        r38 = r3;
        r10 = 1;
        r3 = new java.lang.Object[r10];
        r3[r18] = r5;
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r3);
    L_0x032a:
        r3 = new android.support.v4.app.NotificationCompat$Action$Builder;
        r8 = NUM; // 0x7f0700de float:1.7945028E38 double:1.0529356127E-314;
        r3.<init>(r8, r7, r2);
        r2 = r3.setAllowGeneratedReplies(r10);
        r2 = r2.addRemoteInput(r6);
        r8 = r2.build();
        goto L_0x0340;
    L_0x033f:
        r8 = 0;
    L_0x0340:
        r2 = r0.pushDialogs;
        r2 = r2.get(r11);
        r2 = (java.lang.Integer) r2;
        if (r2 != 0) goto L_0x0350;
    L_0x034a:
        r3 = 0;
        r2 = java.lang.Integer.valueOf(r3);
        goto L_0x0351;
    L_0x0350:
        r3 = 0;
    L_0x0351:
        r6 = new android.support.v4.app.NotificationCompat$MessagingStyle;
        r7 = "";
        r6.<init>(r7);
        r7 = "%1$s (%2$s)";
        r10 = 2;
        r10 = new java.lang.Object[r10];
        r10[r3] = r5;
        r3 = "NewMessages";
        r2 = r2.intValue();
        r39 = r4;
        r4 = r13.size();
        r2 = java.lang.Math.max(r2, r4);
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2);
        r3 = 1;
        r10[r3] = r2;
        r2 = java.lang.String.format(r7, r10);
        r2 = r6.setConversationTitle(r2);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = new boolean[r3];
        if (r9 == 0) goto L_0x038d;
    L_0x0387:
        r7 = new org.json.JSONArray;
        r7.<init>();
        goto L_0x038e;
    L_0x038d:
        r7 = 0;
    L_0x038e:
        r10 = r13.size();
        r10 = r10 - r3;
        r40 = r9;
        r3 = 0;
        r9 = 0;
    L_0x0397:
        if (r10 < 0) goto L_0x049e;
    L_0x0399:
        r18 = r13.get(r10);
        r41 = r9;
        r9 = r18;
        r9 = (org.telegram.messenger.MessageObject) r9;
        r42 = r3;
        r43 = r5;
        r3 = 0;
        r5 = r0.getStringForMessage(r9, r3, r6);
        r3 = r9.isFcmMessage();
        if (r3 == 0) goto L_0x03b5;
    L_0x03b2:
        r3 = r9.localName;
        goto L_0x03b7;
    L_0x03b5:
        r3 = r43;
    L_0x03b7:
        if (r5 != 0) goto L_0x03bf;
    L_0x03b9:
        r44 = r13;
        r45 = r14;
        goto L_0x0490;
    L_0x03bf:
        if (r15 >= 0) goto L_0x03dd;
    L_0x03c1:
        r44 = r13;
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r45 = r14;
        r14 = " @ ";
        r13.append(r14);
        r13.append(r3);
        r3 = r13.toString();
        r13 = "";
        r3 = r5.replace(r3, r13);
        goto L_0x0415;
    L_0x03dd:
        r44 = r13;
        r45 = r14;
        r13 = 0;
        r14 = r6[r13];
        if (r14 == 0) goto L_0x03fe;
    L_0x03e6:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r3);
        r3 = ": ";
        r13.append(r3);
        r3 = r13.toString();
        r13 = "";
        r3 = r5.replace(r3, r13);
        goto L_0x0415;
    L_0x03fe:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r3);
        r3 = " ";
        r13.append(r3);
        r3 = r13.toString();
        r13 = "";
        r3 = r5.replace(r3, r13);
    L_0x0415:
        r5 = r4.length();
        if (r5 <= 0) goto L_0x0420;
    L_0x041b:
        r5 = "\n\n";
        r4.append(r5);
    L_0x0420:
        r4.append(r3);
        r1.addMessage(r3);
        r5 = r9.messageOwner;
        r5 = r5.date;
        r13 = (long) r5;
        r13 = r13 * r22;
        r5 = 0;
        r2.addMessage(r3, r13, r5);
        if (r7 == 0) goto L_0x0477;
    L_0x0433:
        r3 = new org.json.JSONObject;	 Catch:{ JSONException -> 0x0477 }
        r3.<init>();	 Catch:{ JSONException -> 0x0477 }
        r5 = "text";	 Catch:{ JSONException -> 0x0477 }
        r13 = r0.getShortStringForMessage(r9);	 Catch:{ JSONException -> 0x0477 }
        r3.put(r5, r13);	 Catch:{ JSONException -> 0x0477 }
        r5 = "date";	 Catch:{ JSONException -> 0x0477 }
        r13 = r9.messageOwner;	 Catch:{ JSONException -> 0x0477 }
        r13 = r13.date;	 Catch:{ JSONException -> 0x0477 }
        r3.put(r5, r13);	 Catch:{ JSONException -> 0x0477 }
        r5 = r9.isFromUser();	 Catch:{ JSONException -> 0x0477 }
        if (r5 == 0) goto L_0x0474;	 Catch:{ JSONException -> 0x0477 }
    L_0x0450:
        if (r15 >= 0) goto L_0x0474;	 Catch:{ JSONException -> 0x0477 }
    L_0x0452:
        r5 = r0.currentAccount;	 Catch:{ JSONException -> 0x0477 }
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);	 Catch:{ JSONException -> 0x0477 }
        r13 = r9.getFromId();	 Catch:{ JSONException -> 0x0477 }
        r13 = java.lang.Integer.valueOf(r13);	 Catch:{ JSONException -> 0x0477 }
        r5 = r5.getUser(r13);	 Catch:{ JSONException -> 0x0477 }
        if (r5 == 0) goto L_0x0474;	 Catch:{ JSONException -> 0x0477 }
    L_0x0466:
        r13 = "fname";	 Catch:{ JSONException -> 0x0477 }
        r14 = r5.first_name;	 Catch:{ JSONException -> 0x0477 }
        r3.put(r13, r14);	 Catch:{ JSONException -> 0x0477 }
        r13 = "lname";	 Catch:{ JSONException -> 0x0477 }
        r5 = r5.last_name;	 Catch:{ JSONException -> 0x0477 }
        r3.put(r13, r5);	 Catch:{ JSONException -> 0x0477 }
    L_0x0474:
        r7.put(r3);	 Catch:{ JSONException -> 0x0477 }
    L_0x0477:
        r13 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r3 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1));
        if (r3 != 0) goto L_0x0490;
    L_0x047e:
        r3 = r9.messageOwner;
        r3 = r3.reply_markup;
        if (r3 == 0) goto L_0x0490;
    L_0x0484:
        r3 = r9.messageOwner;
        r3 = r3.reply_markup;
        r3 = r3.rows;
        r5 = r9.getId();
        r9 = r5;
        goto L_0x0494;
    L_0x0490:
        r9 = r41;
        r3 = r42;
    L_0x0494:
        r10 = r10 + -1;
        r5 = r43;
        r13 = r44;
        r14 = r45;
        goto L_0x0397;
    L_0x049e:
        r42 = r3;
        r43 = r5;
        r41 = r9;
        r44 = r13;
        r45 = r14;
        r3 = new android.content.Intent;
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r6 = org.telegram.ui.LaunchActivity.class;
        r3.<init>(r5, r6);
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "com.tmessages.openchat";
        r5.append(r6);
        r9 = java.lang.Math.random();
        r5.append(r9);
        r6 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r5.append(r6);
        r5 = r5.toString();
        r3.setAction(r5);
        r5 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r3.setFlags(r5);
        if (r15 == 0) goto L_0x04e8;
    L_0x04d7:
        if (r15 <= 0) goto L_0x04e1;
    L_0x04d9:
        r5 = "userId";
        r3.putExtra(r5, r15);
    L_0x04de:
        r6 = r33;
        goto L_0x04ef;
    L_0x04e1:
        r5 = "chatId";
        r6 = -r15;
        r3.putExtra(r5, r6);
        goto L_0x04de;
    L_0x04e8:
        r5 = "encId";
        r6 = r33;
        r3.putExtra(r5, r6);
    L_0x04ef:
        r5 = "currentAccount";
        r9 = r0.currentAccount;
        r3.putExtra(r5, r9);
        r5 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r10 = 0;
        r3 = android.app.PendingIntent.getActivity(r5, r10, r3, r9);
        r5 = new android.support.v4.app.NotificationCompat$WearableExtender;
        r5.<init>();
        if (r8 == 0) goto L_0x0509;
    L_0x0506:
        r5.addAction(r8);
    L_0x0509:
        if (r15 == 0) goto L_0x0546;
    L_0x050b:
        if (r15 <= 0) goto L_0x0529;
    L_0x050d:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r8 = "tguser";
        r6.append(r8);
        r6.append(r15);
        r8 = "_";
        r6.append(r8);
        r8 = r45;
        r6.append(r8);
        r6 = r6.toString();
        goto L_0x0561;
    L_0x0529:
        r8 = r45;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r9 = "tgchat";
        r6.append(r9);
        r9 = -r15;
        r6.append(r9);
        r9 = "_";
        r6.append(r9);
        r6.append(r8);
        r6 = r6.toString();
        goto L_0x0561;
    L_0x0546:
        r8 = r45;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "tgenc";
        r9.append(r10);
        r9.append(r6);
        r6 = "_";
        r9.append(r6);
        r9.append(r8);
        r6 = r9.toString();
    L_0x0561:
        r5.setDismissalId(r6);
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "tgaccount";
        r9.append(r10);
        r10 = r0.currentAccount;
        r10 = org.telegram.messenger.UserConfig.getInstance(r10);
        r10 = r10.getClientUserId();
        r9.append(r10);
        r9 = r9.toString();
        r5.setBridgeTag(r9);
        r9 = new android.support.v4.app.NotificationCompat$WearableExtender;
        r9.<init>();
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r13 = "summary_";
        r10.append(r13);
        r10.append(r6);
        r6 = r10.toString();
        r9.setDismissalId(r6);
        r6 = r52;
        r6.extend(r9);
        r13 = r44;
        r9 = 0;
        r10 = r13.get(r9);
        r10 = (org.telegram.messenger.MessageObject) r10;
        r9 = r10.messageOwner;
        r9 = r9.date;
        r9 = (long) r9;
        r9 = r9 * r22;
        r14 = new android.support.v4.app.NotificationCompat$Builder;
        r6 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r14.<init>(r6);
        r6 = r43;
        r14 = r14.setContentTitle(r6);
        r46 = r7;
        r7 = NUM; // 0x7f070167 float:1.7945306E38 double:1.0529356804E-314;
        r7 = r14.setSmallIcon(r7);
        r14 = r0.notificationGroup;
        r7 = r7.setGroup(r14);
        r4 = r4.toString();
        r4 = r7.setContentText(r4);
        r7 = 1;
        r4 = r4.setAutoCancel(r7);
        r13 = r13.size();
        r4 = r4.setNumber(r13);
        r13 = -13851168; // 0xffffffffff2ca5e0 float:-2.2948849E38 double:NaN;
        r4 = r4.setColor(r13);
        r13 = 0;
        r4 = r4.setGroupSummary(r13);
        r4 = r4.setWhen(r9);
        r4 = r4.setShowWhen(r7);
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r14 = "sdid_";
        r13.append(r14);
        r13.append(r11);
        r13 = r13.toString();
        r4 = r4.setShortcutId(r13);
        r4 = r4.setGroupAlertBehavior(r7);
        r2 = r4.setStyle(r2);
        r2 = r2.setContentIntent(r3);
        r2 = r2.extend(r5);
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "";
        r3.append(r4);
        r4 = 922337203NUM; // 0x7fffffffffffffff float:NaN double:NaN;
        r13 = r4 - r9;
        r3.append(r13);
        r3 = r3.toString();
        r2 = r2.setSortKey(r3);
        r3 = new android.support.v4.app.NotificationCompat$CarExtender;
        r3.<init>();
        r1 = r1.build();
        r1 = r3.setUnreadConversation(r1);
        r1 = r2.extend(r1);
        r2 = "msg";
        r1 = r1.setCategory(r2);
        r2 = r0.pushDialogs;
        r2 = r2.size();
        r3 = 1;
        if (r2 != r3) goto L_0x0662;
    L_0x0656:
        r2 = android.text.TextUtils.isEmpty(r54);
        if (r2 != 0) goto L_0x0662;
    L_0x065c:
        r2 = r54;
        r1.setSubText(r2);
        goto L_0x0664;
    L_0x0662:
        r2 = r54;
    L_0x0664:
        if (r15 != 0) goto L_0x0669;
    L_0x0666:
        r1.setLocalOnly(r3);
    L_0x0669:
        if (r34 == 0) goto L_0x06b3;
    L_0x066b:
        r4 = org.telegram.messenger.ImageLoader.getInstance();
        r5 = "50_50";
        r10 = r34;
        r7 = 0;
        r4 = r4.getImageFromMemory(r10, r7, r5);
        if (r4 == 0) goto L_0x0682;
    L_0x067a:
        r3 = r4.getBitmap();
        r1.setLargeIcon(r3);
        goto L_0x06b6;
    L_0x0682:
        r4 = org.telegram.messenger.FileLoader.getPathToAttach(r10, r3);	 Catch:{ Throwable -> 0x06b6 }
        r5 = r4.exists();	 Catch:{ Throwable -> 0x06b6 }
        if (r5 == 0) goto L_0x06b6;	 Catch:{ Throwable -> 0x06b6 }
    L_0x068c:
        r5 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;	 Catch:{ Throwable -> 0x06b6 }
        r9 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;	 Catch:{ Throwable -> 0x06b6 }
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Throwable -> 0x06b6 }
        r9 = (float) r9;	 Catch:{ Throwable -> 0x06b6 }
        r5 = r5 / r9;	 Catch:{ Throwable -> 0x06b6 }
        r9 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x06b6 }
        r9.<init>();	 Catch:{ Throwable -> 0x06b6 }
        r13 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x06b6 }
        r13 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1));	 Catch:{ Throwable -> 0x06b6 }
        if (r13 >= 0) goto L_0x06a2;	 Catch:{ Throwable -> 0x06b6 }
    L_0x06a1:
        goto L_0x06a3;	 Catch:{ Throwable -> 0x06b6 }
    L_0x06a2:
        r3 = (int) r5;	 Catch:{ Throwable -> 0x06b6 }
    L_0x06a3:
        r9.inSampleSize = r3;	 Catch:{ Throwable -> 0x06b6 }
        r3 = r4.getAbsolutePath();	 Catch:{ Throwable -> 0x06b6 }
        r3 = android.graphics.BitmapFactory.decodeFile(r3, r9);	 Catch:{ Throwable -> 0x06b6 }
        if (r3 == 0) goto L_0x06b6;	 Catch:{ Throwable -> 0x06b6 }
    L_0x06af:
        r1.setLargeIcon(r3);	 Catch:{ Throwable -> 0x06b6 }
        goto L_0x06b6;
    L_0x06b3:
        r10 = r34;
        r7 = 0;
    L_0x06b6:
        r3 = 0;
        r4 = org.telegram.messenger.AndroidUtilities.needShowPasscode(r3);
        if (r4 != 0) goto L_0x0756;
    L_0x06bd:
        r3 = org.telegram.messenger.SharedConfig.isWaitingForPasscodeEnter;
        if (r3 != 0) goto L_0x0756;
    L_0x06c1:
        if (r42 == 0) goto L_0x0756;
    L_0x06c3:
        r3 = r42;
        r4 = r3.size();
        r5 = 0;
    L_0x06ca:
        if (r5 >= r4) goto L_0x0756;
    L_0x06cc:
        r9 = r3.get(r5);
        r9 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r9;
        r13 = r9.buttons;
        r13 = r13.size();
        r14 = 0;
    L_0x06d9:
        if (r14 >= r13) goto L_0x0744;
    L_0x06db:
        r7 = r9.buttons;
        r7 = r7.get(r14);
        r7 = (org.telegram.tgnet.TLRPC.KeyboardButton) r7;
        r2 = r7 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
        if (r2 == 0) goto L_0x072b;
    L_0x06e7:
        r2 = new android.content.Intent;
        r47 = r3;
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r48 = r4;
        r4 = org.telegram.messenger.NotificationCallbackReceiver.class;
        r2.<init>(r3, r4);
        r3 = "currentAccount";
        r4 = r0.currentAccount;
        r2.putExtra(r3, r4);
        r3 = "did";
        r2.putExtra(r3, r11);
        r3 = r7.data;
        if (r3 == 0) goto L_0x070b;
    L_0x0704:
        r3 = "data";
        r4 = r7.data;
        r2.putExtra(r3, r4);
    L_0x070b:
        r3 = "mid";
        r4 = r41;
        r2.putExtra(r3, r4);
        r3 = r7.text;
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r49 = r4;
        r4 = r0.lastButtonId;
        r50 = r9;
        r9 = r4 + 1;
        r0.lastButtonId = r9;
        r9 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r2 = android.app.PendingIntent.getBroadcast(r7, r4, r2, r9);
        r4 = 0;
        r1.addAction(r4, r3, r2);
        goto L_0x0736;
    L_0x072b:
        r47 = r3;
        r48 = r4;
        r50 = r9;
        r49 = r41;
        r4 = 0;
        r9 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
    L_0x0736:
        r14 = r14 + 1;
        r3 = r47;
        r4 = r48;
        r41 = r49;
        r9 = r50;
        r2 = r54;
        r7 = 0;
        goto L_0x06d9;
    L_0x0744:
        r47 = r3;
        r48 = r4;
        r49 = r41;
        r4 = 0;
        r9 = 134217728; // 0x8000000 float:3.85186E-34 double:6.63123685E-316;
        r5 = r5 + 1;
        r4 = r48;
        r2 = r54;
        r7 = 0;
        goto L_0x06ca;
    L_0x0756:
        r4 = 0;
        if (r19 != 0) goto L_0x077f;
    L_0x0759:
        if (r39 == 0) goto L_0x077f;
    L_0x075b:
        r5 = r39;
        r2 = r5.phone;
        if (r2 == 0) goto L_0x077f;
    L_0x0761:
        r2 = r5.phone;
        r2 = r2.length();
        if (r2 <= 0) goto L_0x077f;
    L_0x0769:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "tel:+";
        r2.append(r3);
        r3 = r5.phone;
        r2.append(r3);
        r2 = r2.toString();
        r1.addPerson(r2);
    L_0x077f:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 26;
        if (r2 < r3) goto L_0x078a;
    L_0x0785:
        r2 = "Other3";
        r1.setChannelId(r2);
    L_0x078a:
        r2 = new org.telegram.messenger.NotificationsController$1NotificationHolder;
        r3 = r38;
        r5 = r3.intValue();
        r1 = r1.build();
        r2.<init>(r5, r1);
        r1 = r36;
        r1.add(r2);
        r2 = r0.wearNotificationsIds;
        r2.put(r11, r3);
        if (r40 == 0) goto L_0x0820;
    L_0x07a5:
        r2 = "reply";	 Catch:{ JSONException -> 0x0820 }
        r3 = r37;	 Catch:{ JSONException -> 0x0820 }
        r5 = r40;	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r3);	 Catch:{ JSONException -> 0x0820 }
        r2 = "name";	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r6);	 Catch:{ JSONException -> 0x0820 }
        r2 = "max_id";	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r8);	 Catch:{ JSONException -> 0x0820 }
        r2 = "max_date";	 Catch:{ JSONException -> 0x0820 }
        r3 = r35;	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r3);	 Catch:{ JSONException -> 0x0820 }
        r2 = "id";	 Catch:{ JSONException -> 0x0820 }
        r3 = java.lang.Math.abs(r15);	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r3);	 Catch:{ JSONException -> 0x0820 }
        if (r10 == 0) goto L_0x07f1;	 Catch:{ JSONException -> 0x0820 }
    L_0x07ca:
        r2 = "photo";	 Catch:{ JSONException -> 0x0820 }
        r3 = new java.lang.StringBuilder;	 Catch:{ JSONException -> 0x0820 }
        r3.<init>();	 Catch:{ JSONException -> 0x0820 }
        r6 = r10.dc_id;	 Catch:{ JSONException -> 0x0820 }
        r3.append(r6);	 Catch:{ JSONException -> 0x0820 }
        r6 = "_";	 Catch:{ JSONException -> 0x0820 }
        r3.append(r6);	 Catch:{ JSONException -> 0x0820 }
        r6 = r10.volume_id;	 Catch:{ JSONException -> 0x0820 }
        r3.append(r6);	 Catch:{ JSONException -> 0x0820 }
        r6 = "_";	 Catch:{ JSONException -> 0x0820 }
        r3.append(r6);	 Catch:{ JSONException -> 0x0820 }
        r6 = r10.secret;	 Catch:{ JSONException -> 0x0820 }
        r3.append(r6);	 Catch:{ JSONException -> 0x0820 }
        r3 = r3.toString();	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r3);	 Catch:{ JSONException -> 0x0820 }
    L_0x07f1:
        if (r46 == 0) goto L_0x07fa;	 Catch:{ JSONException -> 0x0820 }
    L_0x07f3:
        r2 = "msgs";	 Catch:{ JSONException -> 0x0820 }
        r7 = r46;	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r7);	 Catch:{ JSONException -> 0x0820 }
    L_0x07fa:
        if (r15 <= 0) goto L_0x0804;	 Catch:{ JSONException -> 0x0820 }
    L_0x07fc:
        r2 = "type";	 Catch:{ JSONException -> 0x0820 }
        r3 = "user";	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r3);	 Catch:{ JSONException -> 0x0820 }
        goto L_0x081a;	 Catch:{ JSONException -> 0x0820 }
    L_0x0804:
        if (r15 >= 0) goto L_0x081a;	 Catch:{ JSONException -> 0x0820 }
    L_0x0806:
        if (r27 != 0) goto L_0x0813;	 Catch:{ JSONException -> 0x0820 }
    L_0x0808:
        if (r26 == 0) goto L_0x080b;	 Catch:{ JSONException -> 0x0820 }
    L_0x080a:
        goto L_0x0813;	 Catch:{ JSONException -> 0x0820 }
    L_0x080b:
        r2 = "type";	 Catch:{ JSONException -> 0x0820 }
        r3 = "group";	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r3);	 Catch:{ JSONException -> 0x0820 }
        goto L_0x081a;	 Catch:{ JSONException -> 0x0820 }
    L_0x0813:
        r2 = "type";	 Catch:{ JSONException -> 0x0820 }
        r3 = "channel";	 Catch:{ JSONException -> 0x0820 }
        r5.put(r2, r3);	 Catch:{ JSONException -> 0x0820 }
    L_0x081a:
        r7 = r32;
        r7.put(r5);	 Catch:{ JSONException -> 0x0822 }
        goto L_0x0822;
    L_0x0820:
        r7 = r32;
    L_0x0822:
        r10 = r25 + 1;
        r6 = r1;
        r2 = r16;
        r3 = r17;
        r9 = r21;
        r5 = r24;
        r1 = r31;
        goto L_0x0073;
    L_0x0831:
        r31 = r1;
        r24 = r5;
        r1 = r6;
        r2 = notificationManager;
        r3 = r0.notificationId;
        r5 = r31;
        r2.notify(r3, r5);
        r2 = r1.size();
        r3 = r4;
    L_0x0844:
        if (r3 >= r2) goto L_0x0852;
    L_0x0846:
        r5 = r1.get(r3);
        r5 = (org.telegram.messenger.NotificationsController.AnonymousClass1NotificationHolder) r5;
        r5.call();
        r3 = r3 + 1;
        goto L_0x0844;
    L_0x0852:
        r1 = r24;
    L_0x0854:
        r2 = r1.size();
        if (r4 >= r2) goto L_0x086c;
    L_0x085a:
        r2 = notificationManager;
        r3 = r1.valueAt(r4);
        r3 = (java.lang.Integer) r3;
        r3 = r3.intValue();
        r2.cancel(r3);
        r4 = r4 + 1;
        goto L_0x0854;
    L_0x086c:
        if (r7 == 0) goto L_0x0898;
    L_0x086e:
        r1 = new org.json.JSONObject;	 Catch:{ Exception -> 0x0898 }
        r1.<init>();	 Catch:{ Exception -> 0x0898 }
        r2 = "id";	 Catch:{ Exception -> 0x0898 }
        r3 = r0.currentAccount;	 Catch:{ Exception -> 0x0898 }
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);	 Catch:{ Exception -> 0x0898 }
        r3 = r3.getClientUserId();	 Catch:{ Exception -> 0x0898 }
        r1.put(r2, r3);	 Catch:{ Exception -> 0x0898 }
        r2 = "n";	 Catch:{ Exception -> 0x0898 }
        r1.put(r2, r7);	 Catch:{ Exception -> 0x0898 }
        r2 = "/notify";	 Catch:{ Exception -> 0x0898 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0898 }
        r3 = "UTF-8";	 Catch:{ Exception -> 0x0898 }
        r1 = r1.getBytes(r3);	 Catch:{ Exception -> 0x0898 }
        r3 = "remote_notifications";	 Catch:{ Exception -> 0x0898 }
        org.telegram.messenger.WearDataLayerListenerService.sendMessageToWatch(r2, r1, r3);	 Catch:{ Exception -> 0x0898 }
    L_0x0898:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.NotificationsController.showExtraNotifications(android.support.v4.app.NotificationCompat$Builder, boolean, java.lang.String):void");
    }

    public void playOutChatSound() {
        if (this.inChatSoundEnabled) {
            if (!MediaController.getInstance().isRecordingAudio()) {
                try {
                    if (audioManager.getRingerMode() == 0) {
                        return;
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                notificationsQueue.postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.NotificationsController$16$1 */
                    class C04281 implements OnLoadCompleteListener {
                        C04281() {
                        }

                        public void onLoadComplete(SoundPool soundPool, int i, int i2) {
                            if (i2 == 0) {
                                try {
                                    soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                        }
                    }

                    public void run() {
                        try {
                            if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundOutPlay) > 100) {
                                NotificationsController.this.lastSoundOutPlay = System.currentTimeMillis();
                                if (NotificationsController.this.soundPool == null) {
                                    NotificationsController.this.soundPool = new SoundPool(3, 1, 0);
                                    NotificationsController.this.soundPool.setOnLoadCompleteListener(new C04281());
                                }
                                if (NotificationsController.this.soundOut == 0 && !NotificationsController.this.soundOutLoaded) {
                                    NotificationsController.this.soundOutLoaded = true;
                                    NotificationsController.this.soundOut = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, C0446R.raw.sound_out, 1);
                                }
                                if (NotificationsController.this.soundOut != 0) {
                                    try {
                                        NotificationsController.this.soundPool.play(NotificationsController.this.soundOut, 1.0f, 1.0f, 1, 0, 1.0f);
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                }
                            }
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                });
            }
        }
    }

    public void updateServerNotificationsSettings(long j) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        int i = (int) j;
        if (i != 0) {
            TL_inputPeerNotifySettings tL_inputPeerNotifySettings;
            StringBuilder stringBuilder;
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            TLObject tL_account_updateNotifySettings = new TL_account_updateNotifySettings();
            tL_account_updateNotifySettings.settings = new TL_inputPeerNotifySettings();
            tL_account_updateNotifySettings.settings.sound = "default";
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("notify2_");
            stringBuilder2.append(j);
            int i2 = notificationsSettings.getInt(stringBuilder2.toString(), 0);
            if (i2 == 3) {
                tL_inputPeerNotifySettings = tL_account_updateNotifySettings.settings;
                stringBuilder = new StringBuilder();
                stringBuilder.append("notifyuntil_");
                stringBuilder.append(j);
                tL_inputPeerNotifySettings.mute_until = notificationsSettings.getInt(stringBuilder.toString(), 0);
            } else {
                tL_account_updateNotifySettings.settings.mute_until = i2 != 2 ? 0 : ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            tL_inputPeerNotifySettings = tL_account_updateNotifySettings.settings;
            stringBuilder = new StringBuilder();
            stringBuilder.append("preview_");
            stringBuilder.append(j);
            tL_inputPeerNotifySettings.show_previews = notificationsSettings.getBoolean(stringBuilder.toString(), true);
            tL_inputPeerNotifySettings = tL_account_updateNotifySettings.settings;
            stringBuilder = new StringBuilder();
            stringBuilder.append("silent_");
            stringBuilder.append(j);
            tL_inputPeerNotifySettings.silent = notificationsSettings.getBoolean(stringBuilder.toString(), false);
            tL_account_updateNotifySettings.peer = new TL_inputNotifyPeer();
            ((TL_inputNotifyPeer) tL_account_updateNotifySettings.peer).peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateNotifySettings, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                }
            });
        }
    }
}
