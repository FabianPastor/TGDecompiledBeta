package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.CarExtender;
import android.support.v4.app.NotificationCompat.CarExtender.UnreadConversation;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.exoplayer2.C0600C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.upstream.DataSchemeDataSource;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC.TL_inputPeerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
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
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PopupNotificationActivity;

public class NotificationsController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = new NotificationsController[3];
    public static final String OTHER_NOTIFICATIONS_CHANNEL = "Other3";
    protected static AudioManager audioManager = ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO));
    public static long lastNoDataNotificationTime;
    private static NotificationManagerCompat notificationManager;
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    private static NotificationManager systemNotificationManager;
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
    class C04711 implements Runnable {
        C04711() {
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

        AnonymousClass1NotificationHolder(int i, Notification n) {
            this.id = i;
            this.notification = n;
        }

        void call() {
            NotificationsController.notificationManager.notify(this.id, this.notification);
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController$2 */
    class C04722 implements Runnable {
        C04722() {
        }

        public void run() {
            NotificationsController.this.opened_dialog_id = 0;
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
            Editor editor = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount).edit();
            editor.clear();
            editor.commit();
            if (VERSION.SDK_INT >= 26) {
                try {
                    String keyStart = NotificationsController.this.currentAccount + "channel";
                    List<NotificationChannel> list = NotificationsController.systemNotificationManager.getNotificationChannels();
                    int count = list.size();
                    for (int a = 0; a < count; a++) {
                        String id = ((NotificationChannel) list.get(a)).getId();
                        if (id.startsWith(keyStart)) {
                            NotificationsController.systemNotificationManager.deleteNotificationChannel(id);
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.NotificationsController$5 */
    class C04765 implements Runnable {
        C04765() {
        }

        public void run() {
            final ArrayList<MessageObject> popupArray = new ArrayList();
            for (int a = 0; a < NotificationsController.this.pushMessages.size(); a++) {
                MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessages.get(a);
                long dialog_id = messageObject.getDialogId();
                if (!((messageObject.messageOwner.mentioned && (messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) || ((int) dialog_id) == 0 || (messageObject.messageOwner.to_id.channel_id != 0 && !messageObject.isMegagroup()))) {
                    popupArray.add(0, messageObject);
                }
            }
            if (!popupArray.isEmpty() && !AndroidUtilities.needShowPasscode(false)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationsController.this.popupReplyMessages = popupArray;
                        Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                        popupIntent.putExtra("force", true);
                        popupIntent.putExtra("currentAccount", NotificationsController.this.currentAccount);
                        popupIntent.setFlags(268763140);
                        ApplicationLoader.applicationContext.startActivity(popupIntent);
                        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                    }
                });
            }
        }
    }

    static {
        notificationManager = null;
        systemNotificationManager = null;
        if (VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
            notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
            NotificationChannel notificationChannel = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, "Other", 3);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            systemNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static NotificationsController getInstance(int num) {
        NotificationsController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (NotificationsController.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        NotificationsController[] notificationsControllerArr = Instance;
                        NotificationsController localInstance2 = new NotificationsController(num);
                        try {
                            notificationsControllerArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
                }
            }
        }
        return localInstance;
    }

    public NotificationsController(int instance) {
        this.currentAccount = instance;
        this.notificationId = this.currentAccount + 1;
        this.notificationGroup = "messages" + (this.currentAccount == 0 ? TtmlNode.ANONYMOUS_REGION_ID : Integer.valueOf(this.currentAccount));
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        this.inChatSoundEnabled = preferences.getBoolean("EnableInChatSound", true);
        this.showBadgeNumber = preferences.getBoolean("badgeNumber", true);
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
        this.notificationDelayRunnable = new C04711();
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        notificationsQueue.postRunnable(new C04722());
    }

    public void setInChatSoundEnabled(boolean value) {
        this.inChatSoundEnabled = value;
    }

    public void setOpenedDialogId(final long dialog_id) {
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                NotificationsController.this.opened_dialog_id = dialog_id;
            }
        });
    }

    public void setLastOnlineFromOtherDevice(final int time) {
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("set last online from other device = " + time);
                }
                NotificationsController.this.lastOnlineFromOtherDevice = time;
            }
        });
    }

    public void removeNotificationsForDialog(long did) {
        getInstance(this.currentAccount).processReadMessages(null, did, 0, ConnectionsManager.DEFAULT_DATACENTER_ID, false);
        LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray();
        dialogsToUpdate.put(did, Integer.valueOf(0));
        getInstance(this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
    }

    public boolean hasMessagesToReply() {
        for (int a = 0; a < this.pushMessages.size(); a++) {
            MessageObject messageObject = (MessageObject) this.pushMessages.get(a);
            long dialog_id = messageObject.getDialogId();
            if ((!messageObject.messageOwner.mentioned || !(messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) && ((int) dialog_id) != 0 && (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
                return true;
            }
        }
        return false;
    }

    protected void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new C04765());
    }

    public void removeDeletedMessagesFromNotifications(final SparseArray<ArrayList<Integer>> deletedMessages) {
        final ArrayList<MessageObject> popupArrayRemove = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$6$1 */
            class C04771 implements Runnable {
                C04771() {
                }

                public void run() {
                    int size = popupArrayRemove.size();
                    for (int a = 0; a < size; a++) {
                        NotificationsController.this.popupMessages.remove(popupArrayRemove.get(a));
                    }
                }
            }

            /* renamed from: org.telegram.messenger.NotificationsController$6$2 */
            class C04782 implements Runnable {
                C04782() {
                }

                public void run() {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                }
            }

            public void run() {
                int old_unread_count = NotificationsController.this.total_unread_count;
                SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                for (int a = 0; a < deletedMessages.size(); a++) {
                    int key = deletedMessages.keyAt(a);
                    long dialog_id = (long) (-key);
                    ArrayList<Integer> mids = (ArrayList) deletedMessages.get(key);
                    Integer currentCount = (Integer) NotificationsController.this.pushDialogs.get(dialog_id);
                    if (currentCount == null) {
                        currentCount = Integer.valueOf(0);
                    }
                    Integer newCount = currentCount;
                    for (int b = 0; b < mids.size(); b++) {
                        long mid = ((long) ((Integer) mids.get(b)).intValue()) | (((long) key) << 32);
                        MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessagesDict.get(mid);
                        if (messageObject != null) {
                            NotificationsController.this.pushMessagesDict.remove(mid);
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            NotificationsController.this.pushMessages.remove(messageObject);
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            popupArrayRemove.add(messageObject);
                            newCount = Integer.valueOf(newCount.intValue() - 1);
                        }
                    }
                    if (newCount.intValue() <= 0) {
                        newCount = Integer.valueOf(0);
                        NotificationsController.this.smartNotificationsDialogs.remove(dialog_id);
                    }
                    if (!newCount.equals(currentCount)) {
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - currentCount.intValue();
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + newCount.intValue();
                        NotificationsController.this.pushDialogs.put(dialog_id, newCount);
                    }
                    if (newCount.intValue() == 0) {
                        NotificationsController.this.pushDialogs.remove(dialog_id);
                        NotificationsController.this.pushDialogsOverrideMention.remove(dialog_id);
                    }
                }
                if (!popupArrayRemove.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C04771());
                }
                if (old_unread_count != NotificationsController.this.total_unread_count) {
                    if (NotificationsController.this.notifyCheck) {
                        NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime());
                    } else {
                        NotificationsController.this.delayedPushMessages.clear();
                        NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                    }
                    AndroidUtilities.runOnUIThread(new C04782());
                }
                NotificationsController.this.notifyCheck = false;
                if (NotificationsController.this.showBadgeNumber) {
                    NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
                }
            }
        });
    }

    public void removeDeletedHisoryFromNotifications(final SparseIntArray deletedMessages) {
        final ArrayList<MessageObject> popupArrayRemove = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$7$1 */
            class C04801 implements Runnable {
                C04801() {
                }

                public void run() {
                    int size = popupArrayRemove.size();
                    for (int a = 0; a < size; a++) {
                        NotificationsController.this.popupMessages.remove(popupArrayRemove.get(a));
                    }
                }
            }

            /* renamed from: org.telegram.messenger.NotificationsController$7$2 */
            class C04812 implements Runnable {
                C04812() {
                }

                public void run() {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                }
            }

            public void run() {
                int old_unread_count = NotificationsController.this.total_unread_count;
                SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                for (int a = 0; a < deletedMessages.size(); a++) {
                    int key = deletedMessages.keyAt(a);
                    long dialog_id = (long) (-key);
                    int id = deletedMessages.get(key);
                    Integer currentCount = (Integer) NotificationsController.this.pushDialogs.get(dialog_id);
                    if (currentCount == null) {
                        currentCount = Integer.valueOf(0);
                    }
                    Integer newCount = currentCount;
                    int c = 0;
                    while (c < NotificationsController.this.pushMessages.size()) {
                        MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessages.get(c);
                        if (messageObject.getDialogId() == dialog_id && messageObject.getId() <= id) {
                            NotificationsController.this.pushMessagesDict.remove(messageObject.getIdWithChannel());
                            NotificationsController.this.delayedPushMessages.remove(messageObject);
                            NotificationsController.this.pushMessages.remove(messageObject);
                            c--;
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                            }
                            popupArrayRemove.add(messageObject);
                            newCount = Integer.valueOf(newCount.intValue() - 1);
                        }
                        c++;
                    }
                    if (newCount.intValue() <= 0) {
                        newCount = Integer.valueOf(0);
                        NotificationsController.this.smartNotificationsDialogs.remove(dialog_id);
                    }
                    if (!newCount.equals(currentCount)) {
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - currentCount.intValue();
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + newCount.intValue();
                        NotificationsController.this.pushDialogs.put(dialog_id, newCount);
                    }
                    if (newCount.intValue() == 0) {
                        NotificationsController.this.pushDialogs.remove(dialog_id);
                        NotificationsController.this.pushDialogsOverrideMention.remove(dialog_id);
                    }
                }
                if (popupArrayRemove.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C04801());
                }
                if (old_unread_count != NotificationsController.this.total_unread_count) {
                    if (NotificationsController.this.notifyCheck) {
                        NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime());
                    } else {
                        NotificationsController.this.delayedPushMessages.clear();
                        NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                    }
                    AndroidUtilities.runOnUIThread(new C04812());
                }
                NotificationsController.this.notifyCheck = false;
                if (NotificationsController.this.showBadgeNumber) {
                    NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
                }
            }
        });
    }

    public void processReadMessages(SparseLongArray inbox, long dialog_id, int max_date, int max_id, boolean isPopup) {
        final ArrayList<MessageObject> popupArrayRemove = new ArrayList(0);
        final SparseLongArray sparseLongArray = inbox;
        final long j = dialog_id;
        final int i = max_id;
        final int i2 = max_date;
        final boolean z = isPopup;
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$8$1 */
            class C04831 implements Runnable {
                C04831() {
                }

                public void run() {
                    int size = popupArrayRemove.size();
                    for (int a = 0; a < size; a++) {
                        NotificationsController.this.popupMessages.remove(popupArrayRemove.get(a));
                    }
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                }
            }

            public void run() {
                int a;
                MessageObject messageObject;
                long mid;
                if (sparseLongArray != null) {
                    for (int b = 0; b < sparseLongArray.size(); b++) {
                        int key = sparseLongArray.keyAt(b);
                        long messageId = sparseLongArray.get(key);
                        a = 0;
                        while (a < NotificationsController.this.pushMessages.size()) {
                            messageObject = (MessageObject) NotificationsController.this.pushMessages.get(a);
                            if (messageObject.getDialogId() == ((long) key) && messageObject.getId() <= ((int) messageId)) {
                                if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                    NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                                }
                                popupArrayRemove.add(messageObject);
                                mid = (long) messageObject.getId();
                                if (messageObject.messageOwner.to_id.channel_id != 0) {
                                    mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                                }
                                NotificationsController.this.pushMessagesDict.remove(mid);
                                NotificationsController.this.delayedPushMessages.remove(messageObject);
                                NotificationsController.this.pushMessages.remove(a);
                                a--;
                            }
                            a++;
                        }
                    }
                }
                if (!(j == 0 || (i == 0 && i2 == 0))) {
                    a = 0;
                    while (a < NotificationsController.this.pushMessages.size()) {
                        messageObject = (MessageObject) NotificationsController.this.pushMessages.get(a);
                        if (messageObject.getDialogId() == j) {
                            boolean remove = false;
                            if (i2 != 0) {
                                if (messageObject.messageOwner.date <= i2) {
                                    remove = true;
                                }
                            } else if (z) {
                                if (messageObject.getId() == i || i < 0) {
                                    remove = true;
                                }
                            } else if (messageObject.getId() <= i || i < 0) {
                                remove = true;
                            }
                            if (remove) {
                                if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                    NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                                }
                                NotificationsController.this.pushMessages.remove(a);
                                NotificationsController.this.delayedPushMessages.remove(messageObject);
                                popupArrayRemove.add(messageObject);
                                mid = (long) messageObject.getId();
                                if (messageObject.messageOwner.to_id.channel_id != 0) {
                                    mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                                }
                                NotificationsController.this.pushMessagesDict.remove(mid);
                                a--;
                            }
                        }
                        a++;
                    }
                }
                if (!popupArrayRemove.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C04831());
                }
            }
        });
    }

    public void processNewMessages(ArrayList<MessageObject> messageObjects, boolean isLast, boolean isFcm) {
        if (!messageObjects.isEmpty()) {
            final ArrayList<MessageObject> popupArrayAdd = new ArrayList(0);
            final ArrayList<MessageObject> arrayList = messageObjects;
            final boolean z = isFcm;
            final boolean z2 = isLast;
            notificationsQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.NotificationsController$9$2 */
                class C04862 implements Runnable {
                    C04862() {
                    }

                    public void run() {
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                    }
                }

                public void run() {
                    boolean added = false;
                    LongSparseArray<Boolean> settingsCache = new LongSparseArray();
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                    boolean allowPinned = preferences.getBoolean("PinnedMessages", true);
                    int popup = 0;
                    for (int a = 0; a < arrayList.size(); a++) {
                        long dialog_id;
                        MessageObject messageObject = (MessageObject) arrayList.get(a);
                        long mid = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        MessageObject oldMessageObject = (MessageObject) NotificationsController.this.pushMessagesDict.get(mid);
                        if (oldMessageObject == null) {
                            dialog_id = messageObject.getDialogId();
                            long original_dialog_id = dialog_id;
                            if (dialog_id != NotificationsController.this.opened_dialog_id || !ApplicationLoader.isScreenOn) {
                                boolean value;
                                if (messageObject.messageOwner.mentioned) {
                                    if (allowPinned || !(messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) {
                                        dialog_id = (long) messageObject.messageOwner.from_id;
                                    }
                                }
                                if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                    NotificationsController.this.personal_count = NotificationsController.this.personal_count + 1;
                                }
                                added = true;
                                int lower_id = (int) dialog_id;
                                boolean isChat = lower_id < 0;
                                int index = settingsCache.indexOfKey(dialog_id);
                                if (index >= 0) {
                                    value = ((Boolean) settingsCache.valueAt(index)).booleanValue();
                                } else {
                                    int notifyOverride = NotificationsController.this.getNotifyOverride(preferences, dialog_id);
                                    value = notifyOverride != 2 && ((preferences.getBoolean("EnableAll", true) && (!isChat || preferences.getBoolean("EnableGroup", true))) || notifyOverride != 0);
                                    settingsCache.put(dialog_id, Boolean.valueOf(value));
                                }
                                if (lower_id != 0) {
                                    if (preferences.getBoolean("custom_" + dialog_id, false)) {
                                        popup = preferences.getInt("popup_" + dialog_id, 0);
                                    } else {
                                        popup = 0;
                                    }
                                    if (popup == 0) {
                                        popup = preferences.getInt(((int) dialog_id) < 0 ? "popupGroup" : "popupAll", 0);
                                    } else if (popup == 1) {
                                        popup = 3;
                                    } else if (popup == 2) {
                                        popup = 0;
                                    }
                                }
                                if (!(popup == 0 || messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
                                    popup = 0;
                                }
                                if (value) {
                                    if (popup != 0) {
                                        popupArrayAdd.add(0, messageObject);
                                    }
                                    NotificationsController.this.delayedPushMessages.add(messageObject);
                                    NotificationsController.this.pushMessages.add(0, messageObject);
                                    NotificationsController.this.pushMessagesDict.put(mid, messageObject);
                                    if (original_dialog_id != dialog_id) {
                                        NotificationsController.this.pushDialogsOverrideMention.put(original_dialog_id, Integer.valueOf(1));
                                    }
                                }
                            } else if (!z) {
                                NotificationsController.this.playInChatSound();
                            }
                        } else if (oldMessageObject.isFcmMessage()) {
                            NotificationsController.this.pushMessagesDict.put(mid, messageObject);
                            int idxOld = NotificationsController.this.pushMessages.indexOf(oldMessageObject);
                            if (idxOld >= 0) {
                                NotificationsController.this.pushMessages.set(idxOld, messageObject);
                            }
                        }
                    }
                    if (added) {
                        NotificationsController.this.notifyCheck = z2;
                    }
                    if (!(popupArrayAdd.isEmpty() || AndroidUtilities.needShowPasscode(false))) {
                        final int i = popup;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationsController.this.popupMessages.addAll(0, popupArrayAdd);
                                if (!ApplicationLoader.mainInterfacePaused && (ApplicationLoader.isScreenOn || SharedConfig.isWaitingForPasscodeEnter)) {
                                    return;
                                }
                                if (i == 3 || ((i == 1 && ApplicationLoader.isScreenOn) || (i == 2 && !ApplicationLoader.isScreenOn))) {
                                    Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                                    popupIntent.setFlags(268763140);
                                    ApplicationLoader.applicationContext.startActivity(popupIntent);
                                }
                            }
                        });
                    }
                    if (added && z) {
                        dialog_id = ((MessageObject) arrayList.get(0)).getDialogId();
                        int old_unread_count = NotificationsController.this.total_unread_count;
                        notifyOverride = NotificationsController.this.getNotifyOverride(preferences, dialog_id);
                        if (NotificationsController.this.notifyCheck) {
                            Integer override = (Integer) NotificationsController.this.pushDialogsOverrideMention.get(dialog_id);
                            if (override != null && override.intValue() == 1) {
                                NotificationsController.this.pushDialogsOverrideMention.put(dialog_id, Integer.valueOf(0));
                                notifyOverride = 1;
                            }
                        }
                        boolean canAddValue = notifyOverride != 2 && ((preferences.getBoolean("EnableAll", true) && (((int) dialog_id) >= 0 || preferences.getBoolean("EnableGroup", true))) || notifyOverride != 0);
                        Integer currentCount = (Integer) NotificationsController.this.pushDialogs.get(dialog_id);
                        Integer newCount = Integer.valueOf(currentCount != null ? currentCount.intValue() + 1 : 1);
                        if (canAddValue) {
                            if (currentCount != null) {
                                NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - currentCount.intValue();
                            }
                            NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + newCount.intValue();
                            NotificationsController.this.pushDialogs.put(dialog_id, newCount);
                        }
                        if (old_unread_count != NotificationsController.this.total_unread_count) {
                            if (NotificationsController.this.notifyCheck) {
                                NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime());
                            } else {
                                NotificationsController.this.delayedPushMessages.clear();
                                NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                            }
                            AndroidUtilities.runOnUIThread(new C04862());
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

    public void processDialogsUpdateRead(final LongSparseArray<Integer> dialogsToUpdate) {
        final ArrayList<MessageObject> popupArrayToRemove = new ArrayList();
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$10$1 */
            class C04661 implements Runnable {
                C04661() {
                }

                public void run() {
                    int size = popupArrayToRemove.size();
                    for (int a = 0; a < size; a++) {
                        NotificationsController.this.popupMessages.remove(popupArrayToRemove.get(a));
                    }
                }
            }

            /* renamed from: org.telegram.messenger.NotificationsController$10$2 */
            class C04672 implements Runnable {
                C04672() {
                }

                public void run() {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                }
            }

            public void run() {
                int old_unread_count = NotificationsController.this.total_unread_count;
                SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                for (int b = 0; b < dialogsToUpdate.size(); b++) {
                    long dialog_id = dialogsToUpdate.keyAt(b);
                    int notifyOverride = NotificationsController.this.getNotifyOverride(preferences, dialog_id);
                    if (NotificationsController.this.notifyCheck) {
                        Integer override = (Integer) NotificationsController.this.pushDialogsOverrideMention.get(dialog_id);
                        if (override != null && override.intValue() == 1) {
                            NotificationsController.this.pushDialogsOverrideMention.put(dialog_id, Integer.valueOf(0));
                            notifyOverride = 1;
                        }
                    }
                    boolean canAddValue = notifyOverride != 2 && ((preferences.getBoolean("EnableAll", true) && (((int) dialog_id) >= 0 || preferences.getBoolean("EnableGroup", true))) || notifyOverride != 0);
                    Integer currentCount = (Integer) NotificationsController.this.pushDialogs.get(dialog_id);
                    Integer newCount = (Integer) dialogsToUpdate.get(dialog_id);
                    if (newCount.intValue() == 0) {
                        NotificationsController.this.smartNotificationsDialogs.remove(dialog_id);
                    }
                    if (newCount.intValue() < 0) {
                        if (currentCount == null) {
                        } else {
                            newCount = Integer.valueOf(currentCount.intValue() + newCount.intValue());
                        }
                    }
                    if ((canAddValue || newCount.intValue() == 0) && currentCount != null) {
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count - currentCount.intValue();
                    }
                    if (newCount.intValue() == 0) {
                        NotificationsController.this.pushDialogs.remove(dialog_id);
                        NotificationsController.this.pushDialogsOverrideMention.remove(dialog_id);
                        int a = 0;
                        while (a < NotificationsController.this.pushMessages.size()) {
                            MessageObject messageObject = (MessageObject) NotificationsController.this.pushMessages.get(a);
                            if (messageObject.getDialogId() == dialog_id) {
                                if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                    NotificationsController.this.personal_count = NotificationsController.this.personal_count - 1;
                                }
                                NotificationsController.this.pushMessages.remove(a);
                                a--;
                                NotificationsController.this.delayedPushMessages.remove(messageObject);
                                long mid = (long) messageObject.getId();
                                if (messageObject.messageOwner.to_id.channel_id != 0) {
                                    mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                                }
                                NotificationsController.this.pushMessagesDict.remove(mid);
                                popupArrayToRemove.add(messageObject);
                            }
                            a++;
                        }
                    } else if (canAddValue) {
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + newCount.intValue();
                        NotificationsController.this.pushDialogs.put(dialog_id, newCount);
                    }
                }
                if (!popupArrayToRemove.isEmpty()) {
                    AndroidUtilities.runOnUIThread(new C04661());
                }
                if (old_unread_count != NotificationsController.this.total_unread_count) {
                    if (NotificationsController.this.notifyCheck) {
                        NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime());
                    } else {
                        NotificationsController.this.delayedPushMessages.clear();
                        NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                    }
                    AndroidUtilities.runOnUIThread(new C04672());
                }
                NotificationsController.this.notifyCheck = false;
                if (NotificationsController.this.showBadgeNumber) {
                    NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
                }
            }
        });
    }

    public void processLoadedUnreadMessages(final LongSparseArray<Integer> dialogs, final ArrayList<Message> messages, ArrayList<User> users, ArrayList<Chat> chats, ArrayList<EncryptedChat> encryptedChats) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        notificationsQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.NotificationsController$11$1 */
            class C04681 implements Runnable {
                C04681() {
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
                int a;
                long dialog_id;
                int index;
                boolean value;
                NotificationsController.this.pushDialogs.clear();
                NotificationsController.this.pushMessages.clear();
                NotificationsController.this.pushMessagesDict.clear();
                NotificationsController.this.total_unread_count = 0;
                NotificationsController.this.personal_count = 0;
                SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                LongSparseArray<Boolean> settingsCache = new LongSparseArray();
                if (messages != null) {
                    for (a = 0; a < messages.size(); a++) {
                        Message message = (Message) messages.get(a);
                        long mid = (long) message.id;
                        if (message.to_id.channel_id != 0) {
                            mid |= ((long) message.to_id.channel_id) << 32;
                        }
                        if (NotificationsController.this.pushMessagesDict.indexOfKey(mid) < 0) {
                            MessageObject messageObject = new MessageObject(NotificationsController.this.currentAccount, message, false);
                            if (NotificationsController.this.isPersonalMessage(messageObject)) {
                                NotificationsController.this.personal_count = NotificationsController.this.personal_count + 1;
                            }
                            dialog_id = messageObject.getDialogId();
                            long original_dialog_id = dialog_id;
                            if (messageObject.messageOwner.mentioned) {
                                dialog_id = (long) messageObject.messageOwner.from_id;
                            }
                            index = settingsCache.indexOfKey(dialog_id);
                            if (index >= 0) {
                                value = ((Boolean) settingsCache.valueAt(index)).booleanValue();
                            } else {
                                int notifyOverride = NotificationsController.this.getNotifyOverride(preferences, dialog_id);
                                value = notifyOverride != 2 && ((preferences.getBoolean("EnableAll", true) && (((int) dialog_id) >= 0 || preferences.getBoolean("EnableGroup", true))) || notifyOverride != 0);
                                settingsCache.put(dialog_id, Boolean.valueOf(value));
                            }
                            if (value && !(dialog_id == NotificationsController.this.opened_dialog_id && ApplicationLoader.isScreenOn)) {
                                NotificationsController.this.pushMessagesDict.put(mid, messageObject);
                                NotificationsController.this.pushMessages.add(0, messageObject);
                                if (original_dialog_id != dialog_id) {
                                    NotificationsController.this.pushDialogsOverrideMention.put(original_dialog_id, Integer.valueOf(1));
                                }
                            }
                        }
                    }
                }
                for (a = 0; a < dialogs.size(); a++) {
                    dialog_id = dialogs.keyAt(a);
                    index = settingsCache.indexOfKey(dialog_id);
                    if (index >= 0) {
                        value = ((Boolean) settingsCache.valueAt(index)).booleanValue();
                    } else {
                        notifyOverride = NotificationsController.this.getNotifyOverride(preferences, dialog_id);
                        Integer override = (Integer) NotificationsController.this.pushDialogsOverrideMention.get(dialog_id);
                        if (override != null && override.intValue() == 1) {
                            NotificationsController.this.pushDialogsOverrideMention.put(dialog_id, Integer.valueOf(0));
                            notifyOverride = 1;
                        }
                        value = notifyOverride != 2 && ((preferences.getBoolean("EnableAll", true) && (((int) dialog_id) >= 0 || preferences.getBoolean("EnableGroup", true))) || notifyOverride != 0);
                        settingsCache.put(dialog_id, Boolean.valueOf(value));
                    }
                    if (value) {
                        int count = ((Integer) dialogs.valueAt(a)).intValue();
                        NotificationsController.this.pushDialogs.put(dialog_id, Integer.valueOf(count));
                        NotificationsController.this.total_unread_count = NotificationsController.this.total_unread_count + count;
                    }
                }
                AndroidUtilities.runOnUIThread(new C04681());
                NotificationsController.this.showOrUpdateNotification(SystemClock.uptimeMillis() / 1000 < 60);
                if (NotificationsController.this.showBadgeNumber) {
                    NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
                }
            }
        });
    }

    private int getTotalAllUnreadCount() {
        int count = 0;
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                NotificationsController controller = getInstance(a);
                if (controller.showBadgeNumber) {
                    count += controller.total_unread_count;
                }
            }
        }
        return count;
    }

    public void setBadgeEnabled(boolean enabled) {
        this.showBadgeNumber = enabled;
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                NotificationsController.this.setBadge(NotificationsController.this.getTotalAllUnreadCount());
            }
        });
    }

    private void setBadge(int count) {
        if (this.lastBadgeCount != count) {
            this.lastBadgeCount = count;
            NotificationBadge.applyCount(count);
        }
    }

    private String getShortStringForMessage(MessageObject msg) {
        if (!(msg.isMediaEmpty() || TextUtils.isEmpty(msg.messageOwner.message))) {
            if (msg.messageOwner.media instanceof TL_messageMediaPhoto) {
                return "\ud83d\uddbc " + msg.messageOwner.message;
            }
            if (msg.isVideo()) {
                return "\ud83d\udcf9 " + msg.messageOwner.message;
            }
            if (msg.messageOwner.media instanceof TL_messageMediaDocument) {
                if (msg.isGif()) {
                    return "\ud83c\udfac " + msg.messageOwner.message;
                }
                return "\ud83d\udcce " + msg.messageOwner.message;
            }
        }
        return msg.messageText.toString();
    }

    private String getStringForMessage(MessageObject messageObject, boolean shortMessage, boolean[] text) {
        if (AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
            return LocaleController.getString("YouHaveNewMessage", C0488R.string.YouHaveNewMessage);
        }
        long dialog_id = messageObject.messageOwner.dialog_id;
        int chat_id = messageObject.messageOwner.to_id.chat_id != 0 ? messageObject.messageOwner.to_id.chat_id : messageObject.messageOwner.to_id.channel_id;
        int from_id = messageObject.messageOwner.to_id.user_id;
        if (messageObject.isFcmMessage()) {
            if (chat_id == 0 && from_id != 0) {
                if (!MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewAll", true)) {
                    return LocaleController.formatString("NotificationMessageNoText", C0488R.string.NotificationMessageNoText, messageObject.localName);
                }
            } else if (chat_id != 0) {
                if (!MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewGroup", true)) {
                    if (messageObject.isMegagroup() || messageObject.messageOwner.to_id.channel_id == 0) {
                        return LocaleController.formatString("NotificationMessageGroupNoText", C0488R.string.NotificationMessageGroupNoText, messageObject.localUserName, messageObject.localName);
                    }
                    return LocaleController.formatString("ChannelMessageNoText", C0488R.string.ChannelMessageNoText, messageObject.localName);
                }
            }
            text[0] = true;
            return (String) messageObject.messageText;
        }
        User user;
        Chat chat;
        if (from_id == 0) {
            if (messageObject.isFromUser() || messageObject.getId() < 0) {
                from_id = messageObject.messageOwner.from_id;
            } else {
                from_id = -chat_id;
            }
        } else if (from_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            from_id = messageObject.messageOwner.from_id;
        }
        if (dialog_id == 0) {
            if (chat_id != 0) {
                dialog_id = (long) (-chat_id);
            } else if (from_id != 0) {
                dialog_id = (long) from_id;
            }
        }
        String name = null;
        if (from_id > 0) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(from_id));
            if (user != null) {
                name = UserObject.getUserName(user);
            }
        } else {
            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-from_id));
            if (chat != null) {
                name = chat.title;
            }
        }
        if (name == null) {
            return null;
        }
        chat = null;
        if (chat_id != 0) {
            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(chat_id));
            if (chat == null) {
                return null;
            }
        }
        String msg = null;
        if (((int) dialog_id) == 0) {
            msg = LocaleController.getString("YouHaveNewMessage", C0488R.string.YouHaveNewMessage);
        } else if (chat_id == 0 && from_id != 0) {
            if (!MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewAll", true)) {
                msg = LocaleController.formatString("NotificationMessageNoText", C0488R.string.NotificationMessageNoText, name);
            } else if (messageObject.messageOwner instanceof TL_messageService) {
                if (messageObject.messageOwner.action instanceof TL_messageActionUserJoined) {
                    msg = LocaleController.formatString("NotificationContactJoined", C0488R.string.NotificationContactJoined, name);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    msg = LocaleController.formatString("NotificationContactNewPhoto", C0488R.string.NotificationContactNewPhoto, name);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                    String date = LocaleController.formatString("formatDateAtTime", C0488R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(((long) messageObject.messageOwner.date) * 1000), LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000));
                    msg = LocaleController.formatString("NotificationUnrecognizedDevice", C0488R.string.NotificationUnrecognizedDevice, UserConfig.getInstance(this.currentAccount).getCurrentUser().first_name, date, messageObject.messageOwner.action.title, messageObject.messageOwner.action.address);
                } else if ((messageObject.messageOwner.action instanceof TL_messageActionGameScore) || (messageObject.messageOwner.action instanceof TL_messageActionPaymentSent)) {
                    msg = messageObject.messageText.toString();
                } else if (messageObject.messageOwner.action instanceof TL_messageActionPhoneCall) {
                    PhoneCallDiscardReason reason = messageObject.messageOwner.action.reason;
                    if (!messageObject.isOut() && (reason instanceof TL_phoneCallDiscardReasonMissed)) {
                        msg = LocaleController.getString("CallMessageIncomingMissed", C0488R.string.CallMessageIncomingMissed);
                    }
                }
            } else if (messageObject.isMediaEmpty()) {
                if (shortMessage) {
                    msg = LocaleController.formatString("NotificationMessageNoText", C0488R.string.NotificationMessageNoText, name);
                } else if (TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("NotificationMessageNoText", C0488R.string.NotificationMessageNoText, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = messageObject.messageOwner.media.ttl_seconds != 0 ? LocaleController.formatString("NotificationMessageSDPhoto", C0488R.string.NotificationMessageSDPhoto, name) : LocaleController.formatString("NotificationMessagePhoto", C0488R.string.NotificationMessagePhoto, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, "\ud83d\uddbc " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.isVideo()) {
                if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = messageObject.messageOwner.media.ttl_seconds != 0 ? LocaleController.formatString("NotificationMessageSDVideo", C0488R.string.NotificationMessageSDVideo, name) : LocaleController.formatString("NotificationMessageVideo", C0488R.string.NotificationMessageVideo, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, "\ud83d\udcf9 " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.isGame()) {
                msg = LocaleController.formatString("NotificationMessageGame", C0488R.string.NotificationMessageGame, name, messageObject.messageOwner.media.game.title);
            } else if (messageObject.isVoice()) {
                msg = LocaleController.formatString("NotificationMessageAudio", C0488R.string.NotificationMessageAudio, name);
            } else if (messageObject.isRoundVideo()) {
                msg = LocaleController.formatString("NotificationMessageRound", C0488R.string.NotificationMessageRound, name);
            } else if (messageObject.isMusic()) {
                msg = LocaleController.formatString("NotificationMessageMusic", C0488R.string.NotificationMessageMusic, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                msg = LocaleController.formatString("NotificationMessageContact", C0488R.string.NotificationMessageContact, name);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                msg = LocaleController.formatString("NotificationMessageMap", C0488R.string.NotificationMessageMap, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                msg = LocaleController.formatString("NotificationMessageLiveLocation", C0488R.string.NotificationMessageLiveLocation, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                if (messageObject.isSticker()) {
                    msg = messageObject.getStickerEmoji() != null ? LocaleController.formatString("NotificationMessageStickerEmoji", C0488R.string.NotificationMessageStickerEmoji, name, messageObject.getStickerEmoji()) : LocaleController.formatString("NotificationMessageSticker", C0488R.string.NotificationMessageSticker, name);
                } else if (messageObject.isGif()) {
                    if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                        msg = LocaleController.formatString("NotificationMessageGif", C0488R.string.NotificationMessageGif, name);
                    } else {
                        msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, "\ud83c\udfac " + messageObject.messageOwner.message);
                        text[0] = true;
                    }
                } else if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("NotificationMessageDocument", C0488R.string.NotificationMessageDocument, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, "\ud83d\udcce " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            }
        } else if (chat_id != 0) {
            if (!MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewGroup", true)) {
                msg = (!ChatObject.isChannel(chat) || chat.megagroup) ? LocaleController.formatString("NotificationMessageGroupNoText", C0488R.string.NotificationMessageGroupNoText, name, chat.title) : LocaleController.formatString("ChannelMessageNoText", C0488R.string.ChannelMessageNoText, name);
            } else if (messageObject.messageOwner instanceof TL_messageService) {
                if (messageObject.messageOwner.action instanceof TL_messageActionChatAddUser) {
                    int singleUserId = messageObject.messageOwner.action.user_id;
                    if (singleUserId == 0 && messageObject.messageOwner.action.users.size() == 1) {
                        singleUserId = ((Integer) messageObject.messageOwner.action.users.get(0)).intValue();
                    }
                    if (singleUserId == 0) {
                        StringBuilder stringBuilder = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                        for (int a = 0; a < messageObject.messageOwner.action.users.size(); a++) {
                            user = MessagesController.getInstance(this.currentAccount).getUser((Integer) messageObject.messageOwner.action.users.get(a));
                            if (user != null) {
                                String name2 = UserObject.getUserName(user);
                                if (stringBuilder.length() != 0) {
                                    stringBuilder.append(", ");
                                }
                                stringBuilder.append(name2);
                            }
                        }
                        msg = LocaleController.formatString("NotificationGroupAddMember", C0488R.string.NotificationGroupAddMember, name, chat.title, stringBuilder.toString());
                    } else if (messageObject.messageOwner.to_id.channel_id != 0 && !chat.megagroup) {
                        msg = LocaleController.formatString("ChannelAddedByNotification", C0488R.string.ChannelAddedByNotification, name, chat.title);
                    } else if (singleUserId == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        msg = LocaleController.formatString("NotificationInvitedToGroup", C0488R.string.NotificationInvitedToGroup, name, chat.title);
                    } else {
                        User u2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(singleUserId));
                        if (u2 == null) {
                            return null;
                        }
                        msg = from_id == u2.id ? chat.megagroup ? LocaleController.formatString("NotificationGroupAddSelfMega", C0488R.string.NotificationGroupAddSelfMega, name, chat.title) : LocaleController.formatString("NotificationGroupAddSelf", C0488R.string.NotificationGroupAddSelf, name, chat.title) : LocaleController.formatString("NotificationGroupAddMember", C0488R.string.NotificationGroupAddMember, name, chat.title, UserObject.getUserName(u2));
                    }
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatJoinedByLink) {
                    msg = LocaleController.formatString("NotificationInvitedToGroupByLink", C0488R.string.NotificationInvitedToGroupByLink, name, chat.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatEditTitle) {
                    msg = LocaleController.formatString("NotificationEditedGroupName", C0488R.string.NotificationEditedGroupName, name, messageObject.messageOwner.action.title);
                } else if ((messageObject.messageOwner.action instanceof TL_messageActionChatEditPhoto) || (messageObject.messageOwner.action instanceof TL_messageActionChatDeletePhoto)) {
                    msg = (messageObject.messageOwner.to_id.channel_id == 0 || chat.megagroup) ? LocaleController.formatString("NotificationEditedGroupPhoto", C0488R.string.NotificationEditedGroupPhoto, name, chat.title) : LocaleController.formatString("ChannelPhotoEditNotification", C0488R.string.ChannelPhotoEditNotification, chat.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatDeleteUser) {
                    if (messageObject.messageOwner.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        msg = LocaleController.formatString("NotificationGroupKickYou", C0488R.string.NotificationGroupKickYou, name, chat.title);
                    } else if (messageObject.messageOwner.action.user_id == from_id) {
                        msg = LocaleController.formatString("NotificationGroupLeftMember", C0488R.string.NotificationGroupLeftMember, name, chat.title);
                    } else {
                        if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.action.user_id)) == null) {
                            return null;
                        }
                        msg = LocaleController.formatString("NotificationGroupKickMember", C0488R.string.NotificationGroupKickMember, name, chat.title, UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.action.user_id))));
                    }
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatCreate) {
                    msg = messageObject.messageText.toString();
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChannelCreate) {
                    msg = messageObject.messageText.toString();
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                    msg = LocaleController.formatString("ActionMigrateFromGroupNotify", C0488R.string.ActionMigrateFromGroupNotify, chat.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChannelMigrateFrom) {
                    msg = LocaleController.formatString("ActionMigrateFromGroupNotify", C0488R.string.ActionMigrateFromGroupNotify, messageObject.messageOwner.action.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionScreenshotTaken) {
                    msg = messageObject.messageText.toString();
                } else if (messageObject.messageOwner.action instanceof TL_messageActionPinMessage) {
                    MessageObject object;
                    String message;
                    CharSequence message2;
                    if (chat == null || !chat.megagroup) {
                        if (messageObject.replyMessageObject == null) {
                            msg = LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0488R.string.NotificationActionPinnedNoTextChannel, chat.title);
                        } else {
                            object = messageObject.replyMessageObject;
                            if (object.isMusic()) {
                                msg = LocaleController.formatString("NotificationActionPinnedMusicChannel", C0488R.string.NotificationActionPinnedMusicChannel, chat.title);
                            } else if (object.isVideo()) {
                                if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                    msg = LocaleController.formatString("NotificationActionPinnedVideoChannel", C0488R.string.NotificationActionPinnedVideoChannel, chat.title);
                                } else {
                                    message = "\ud83d\udcf9 " + object.messageOwner.message;
                                    msg = LocaleController.formatString("NotificationActionPinnedTextChannel", C0488R.string.NotificationActionPinnedTextChannel, chat.title, message);
                                }
                            } else if (object.isGif()) {
                                if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                    msg = LocaleController.formatString("NotificationActionPinnedGifChannel", C0488R.string.NotificationActionPinnedGifChannel, chat.title);
                                } else {
                                    message = "\ud83c\udfac " + object.messageOwner.message;
                                    msg = LocaleController.formatString("NotificationActionPinnedTextChannel", C0488R.string.NotificationActionPinnedTextChannel, chat.title, message);
                                }
                            } else if (object.isVoice()) {
                                msg = LocaleController.formatString("NotificationActionPinnedVoiceChannel", C0488R.string.NotificationActionPinnedVoiceChannel, chat.title);
                            } else if (object.isRoundVideo()) {
                                msg = LocaleController.formatString("NotificationActionPinnedRoundChannel", C0488R.string.NotificationActionPinnedRoundChannel, chat.title);
                            } else if (object.isSticker()) {
                                msg = object.getStickerEmoji() != null ? LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", C0488R.string.NotificationActionPinnedStickerEmojiChannel, chat.title, object.getStickerEmoji()) : LocaleController.formatString("NotificationActionPinnedStickerChannel", C0488R.string.NotificationActionPinnedStickerChannel, chat.title);
                            } else if (object.messageOwner.media instanceof TL_messageMediaDocument) {
                                if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                    msg = LocaleController.formatString("NotificationActionPinnedFileChannel", C0488R.string.NotificationActionPinnedFileChannel, chat.title);
                                } else {
                                    message = "\ud83d\udcce " + object.messageOwner.message;
                                    msg = LocaleController.formatString("NotificationActionPinnedTextChannel", C0488R.string.NotificationActionPinnedTextChannel, chat.title, message);
                                }
                            } else if ((object.messageOwner.media instanceof TL_messageMediaGeo) || (object.messageOwner.media instanceof TL_messageMediaVenue)) {
                                msg = LocaleController.formatString("NotificationActionPinnedGeoChannel", C0488R.string.NotificationActionPinnedGeoChannel, chat.title);
                            } else if (object.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                msg = LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", C0488R.string.NotificationActionPinnedGeoLiveChannel, chat.title);
                            } else if (object.messageOwner.media instanceof TL_messageMediaContact) {
                                msg = LocaleController.formatString("NotificationActionPinnedContactChannel", C0488R.string.NotificationActionPinnedContactChannel, chat.title);
                            } else if (object.messageOwner.media instanceof TL_messageMediaPhoto) {
                                if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                    msg = LocaleController.formatString("NotificationActionPinnedPhotoChannel", C0488R.string.NotificationActionPinnedPhotoChannel, chat.title);
                                } else {
                                    message = "\ud83d\uddbc " + object.messageOwner.message;
                                    msg = LocaleController.formatString("NotificationActionPinnedTextChannel", C0488R.string.NotificationActionPinnedTextChannel, chat.title, message);
                                }
                            } else if (object.messageOwner.media instanceof TL_messageMediaGame) {
                                msg = LocaleController.formatString("NotificationActionPinnedGameChannel", C0488R.string.NotificationActionPinnedGameChannel, chat.title);
                            } else if (object.messageText == null || object.messageText.length() <= 0) {
                                msg = LocaleController.formatString("NotificationActionPinnedNoTextChannel", C0488R.string.NotificationActionPinnedNoTextChannel, chat.title);
                            } else {
                                message2 = object.messageText;
                                if (message2.length() > 20) {
                                    message2 = message2.subSequence(0, 20) + "...";
                                }
                                msg = LocaleController.formatString("NotificationActionPinnedTextChannel", C0488R.string.NotificationActionPinnedTextChannel, chat.title, message2);
                            }
                        }
                    } else if (messageObject.replyMessageObject == null) {
                        msg = LocaleController.formatString("NotificationActionPinnedNoText", C0488R.string.NotificationActionPinnedNoText, name, chat.title);
                    } else {
                        object = messageObject.replyMessageObject;
                        if (object.isMusic()) {
                            msg = LocaleController.formatString("NotificationActionPinnedMusic", C0488R.string.NotificationActionPinnedMusic, name, chat.title);
                        } else if (object.isVideo()) {
                            if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                msg = LocaleController.formatString("NotificationActionPinnedVideo", C0488R.string.NotificationActionPinnedVideo, name, chat.title);
                            } else {
                                message = "\ud83d\udcf9 " + object.messageOwner.message;
                                msg = LocaleController.formatString("NotificationActionPinnedText", C0488R.string.NotificationActionPinnedText, name, message, chat.title);
                            }
                        } else if (object.isGif()) {
                            if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                msg = LocaleController.formatString("NotificationActionPinnedGif", C0488R.string.NotificationActionPinnedGif, name, chat.title);
                            } else {
                                message = "\ud83c\udfac " + object.messageOwner.message;
                                msg = LocaleController.formatString("NotificationActionPinnedText", C0488R.string.NotificationActionPinnedText, name, message, chat.title);
                            }
                        } else if (object.isVoice()) {
                            msg = LocaleController.formatString("NotificationActionPinnedVoice", C0488R.string.NotificationActionPinnedVoice, name, chat.title);
                        } else if (object.isRoundVideo()) {
                            msg = LocaleController.formatString("NotificationActionPinnedRound", C0488R.string.NotificationActionPinnedRound, name, chat.title);
                        } else if (object.isSticker()) {
                            msg = object.getStickerEmoji() != null ? LocaleController.formatString("NotificationActionPinnedStickerEmoji", C0488R.string.NotificationActionPinnedStickerEmoji, name, chat.title, object.getStickerEmoji()) : LocaleController.formatString("NotificationActionPinnedSticker", C0488R.string.NotificationActionPinnedSticker, name, chat.title);
                        } else if (object.messageOwner.media instanceof TL_messageMediaDocument) {
                            if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                msg = LocaleController.formatString("NotificationActionPinnedFile", C0488R.string.NotificationActionPinnedFile, name, chat.title);
                            } else {
                                message = "\ud83d\udcce " + object.messageOwner.message;
                                msg = LocaleController.formatString("NotificationActionPinnedText", C0488R.string.NotificationActionPinnedText, name, message, chat.title);
                            }
                        } else if ((object.messageOwner.media instanceof TL_messageMediaGeo) || (object.messageOwner.media instanceof TL_messageMediaVenue)) {
                            msg = LocaleController.formatString("NotificationActionPinnedGeo", C0488R.string.NotificationActionPinnedGeo, name, chat.title);
                        } else if (object.messageOwner.media instanceof TL_messageMediaGeoLive) {
                            msg = LocaleController.formatString("NotificationActionPinnedGeoLive", C0488R.string.NotificationActionPinnedGeoLive, name, chat.title);
                        } else if (object.messageOwner.media instanceof TL_messageMediaContact) {
                            msg = LocaleController.formatString("NotificationActionPinnedContact", C0488R.string.NotificationActionPinnedContact, name, chat.title);
                        } else if (object.messageOwner.media instanceof TL_messageMediaPhoto) {
                            if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                msg = LocaleController.formatString("NotificationActionPinnedPhoto", C0488R.string.NotificationActionPinnedPhoto, name, chat.title);
                            } else {
                                message = "\ud83d\uddbc " + object.messageOwner.message;
                                msg = LocaleController.formatString("NotificationActionPinnedText", C0488R.string.NotificationActionPinnedText, name, message, chat.title);
                            }
                        } else if (object.messageOwner.media instanceof TL_messageMediaGame) {
                            msg = LocaleController.formatString("NotificationActionPinnedGame", C0488R.string.NotificationActionPinnedGame, name, chat.title);
                        } else if (object.messageText == null || object.messageText.length() <= 0) {
                            msg = LocaleController.formatString("NotificationActionPinnedNoText", C0488R.string.NotificationActionPinnedNoText, name, chat.title);
                        } else {
                            message2 = object.messageText;
                            if (message2.length() > 20) {
                                message2 = message2.subSequence(0, 20) + "...";
                            }
                            msg = LocaleController.formatString("NotificationActionPinnedText", C0488R.string.NotificationActionPinnedText, name, message2, chat.title);
                        }
                    }
                } else if (messageObject.messageOwner.action instanceof TL_messageActionGameScore) {
                    msg = messageObject.messageText.toString();
                }
            } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                if (messageObject.isMediaEmpty()) {
                    msg = (shortMessage || messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) ? LocaleController.formatString("NotificationMessageGroupNoText", C0488R.string.NotificationMessageGroupNoText, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", C0488R.string.NotificationMessageGroupText, name, chat.title, messageObject.messageOwner.message);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                    msg = (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) ? LocaleController.formatString("NotificationMessageGroupPhoto", C0488R.string.NotificationMessageGroupPhoto, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", C0488R.string.NotificationMessageGroupText, name, chat.title, "\ud83d\uddbc " + messageObject.messageOwner.message);
                } else if (messageObject.isVideo()) {
                    msg = (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) ? LocaleController.formatString("NotificationMessageGroupVideo", C0488R.string.NotificationMessageGroupVideo, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", C0488R.string.NotificationMessageGroupText, name, chat.title, "\ud83d\udcf9 " + messageObject.messageOwner.message);
                } else if (messageObject.isVoice()) {
                    msg = LocaleController.formatString("NotificationMessageGroupAudio", C0488R.string.NotificationMessageGroupAudio, name, chat.title);
                } else if (messageObject.isRoundVideo()) {
                    msg = LocaleController.formatString("NotificationMessageGroupRound", C0488R.string.NotificationMessageGroupRound, name, chat.title);
                } else if (messageObject.isMusic()) {
                    msg = LocaleController.formatString("NotificationMessageGroupMusic", C0488R.string.NotificationMessageGroupMusic, name, chat.title);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                    msg = LocaleController.formatString("NotificationMessageGroupContact", C0488R.string.NotificationMessageGroupContact, name, chat.title);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                    msg = LocaleController.formatString("NotificationMessageGroupGame", C0488R.string.NotificationMessageGroupGame, name, chat.title, messageObject.messageOwner.media.game.title);
                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                    msg = LocaleController.formatString("NotificationMessageGroupMap", C0488R.string.NotificationMessageGroupMap, name, chat.title);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                    msg = LocaleController.formatString("NotificationMessageGroupLiveLocation", C0488R.string.NotificationMessageGroupLiveLocation, name, chat.title);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                    if (messageObject.isSticker()) {
                        msg = messageObject.getStickerEmoji() != null ? LocaleController.formatString("NotificationMessageGroupStickerEmoji", C0488R.string.NotificationMessageGroupStickerEmoji, name, chat.title, messageObject.getStickerEmoji()) : LocaleController.formatString("NotificationMessageGroupSticker", C0488R.string.NotificationMessageGroupSticker, name, chat.title);
                    } else {
                        msg = messageObject.isGif() ? (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) ? LocaleController.formatString("NotificationMessageGroupGif", C0488R.string.NotificationMessageGroupGif, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", C0488R.string.NotificationMessageGroupText, name, chat.title, "\ud83c\udfac " + messageObject.messageOwner.message) : (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) ? LocaleController.formatString("NotificationMessageGroupDocument", C0488R.string.NotificationMessageGroupDocument, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", C0488R.string.NotificationMessageGroupText, name, chat.title, "\ud83d\udcce " + messageObject.messageOwner.message);
                    }
                }
            } else if (messageObject.isMediaEmpty()) {
                if (shortMessage || messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) {
                    msg = LocaleController.formatString("ChannelMessageNoText", C0488R.string.ChannelMessageNoText, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("ChannelMessagePhoto", C0488R.string.ChannelMessagePhoto, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, "\ud83d\uddbc " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.isVideo()) {
                if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("ChannelMessageVideo", C0488R.string.ChannelMessageVideo, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, "\ud83d\udcf9 " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.isVoice()) {
                msg = LocaleController.formatString("ChannelMessageAudio", C0488R.string.ChannelMessageAudio, name);
            } else if (messageObject.isRoundVideo()) {
                msg = LocaleController.formatString("ChannelMessageRound", C0488R.string.ChannelMessageRound, name);
            } else if (messageObject.isMusic()) {
                msg = LocaleController.formatString("ChannelMessageMusic", C0488R.string.ChannelMessageMusic, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                msg = LocaleController.formatString("ChannelMessageContact", C0488R.string.ChannelMessageContact, name);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                msg = LocaleController.formatString("ChannelMessageMap", C0488R.string.ChannelMessageMap, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                msg = LocaleController.formatString("ChannelMessageLiveLocation", C0488R.string.ChannelMessageLiveLocation, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                if (messageObject.isSticker()) {
                    msg = messageObject.getStickerEmoji() != null ? LocaleController.formatString("ChannelMessageStickerEmoji", C0488R.string.ChannelMessageStickerEmoji, name, messageObject.getStickerEmoji()) : LocaleController.formatString("ChannelMessageSticker", C0488R.string.ChannelMessageSticker, name);
                } else if (messageObject.isGif()) {
                    if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                        msg = LocaleController.formatString("ChannelMessageGIF", C0488R.string.ChannelMessageGIF, name);
                    } else {
                        msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, "\ud83c\udfac " + messageObject.messageOwner.message);
                        text[0] = true;
                    }
                } else if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("ChannelMessageDocument", C0488R.string.ChannelMessageDocument, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", C0488R.string.NotificationMessageText, name, "\ud83d\udcce " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            }
        }
        return msg;
    }

    private void scheduleNotificationRepeat() {
        try {
            Intent intent = new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class);
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent pintent = PendingIntent.getService(ApplicationLoader.applicationContext, 0, intent, 0);
            int minutes = MessagesController.getNotificationsSettings(this.currentAccount).getInt("repeat_messages", 60);
            if (minutes <= 0 || this.personal_count <= 0) {
                this.alarmManager.cancel(pintent);
            } else {
                this.alarmManager.set(2, SystemClock.elapsedRealtime() + ((long) ((minutes * 60) * 1000)), pintent);
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        return messageObject.messageOwner.to_id != null && messageObject.messageOwner.to_id.chat_id == 0 && messageObject.messageOwner.to_id.channel_id == 0 && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty));
    }

    private int getNotifyOverride(SharedPreferences preferences, long dialog_id) {
        int notifyOverride = preferences.getInt("notify2_" + dialog_id, 0);
        if (notifyOverride != 3 || preferences.getInt("notifyuntil_" + dialog_id, 0) < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
            return notifyOverride;
        }
        return 2;
    }

    private void dismissNotification() {
        try {
            this.lastNotificationIsNoData = false;
            notificationManager.cancel(this.notificationId);
            this.pushMessages.clear();
            this.pushMessagesDict.clear();
            this.lastWearNotifiedMessageId.clear();
            for (int a = 0; a < this.wearNotificationsIds.size(); a++) {
                notificationManager.cancel(((Integer) this.wearNotificationsIds.valueAt(a)).intValue());
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                }
            });
            if (WearDataLayerListenerService.isWatchConnected()) {
                try {
                    JSONObject o = new JSONObject();
                    o.put(TtmlNode.ATTR_ID, UserConfig.getInstance(this.currentAccount).getClientUserId());
                    o.put("cancel_all", true);
                    WearDataLayerListenerService.sendMessageToWatch("/notify", o.toString().getBytes(C0600C.UTF8_NAME), "remote_notifications");
                } catch (JSONException e) {
                }
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
    }

    private void playInChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
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
                        class C04691 implements OnLoadCompleteListener {
                            C04691() {
                            }

                            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                                if (status == 0) {
                                    try {
                                        soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
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
                                        NotificationsController.this.soundPool.setOnLoadCompleteListener(new C04691());
                                    }
                                    if (NotificationsController.this.soundIn == 0 && !NotificationsController.this.soundInLoaded) {
                                        NotificationsController.this.soundInLoaded = true;
                                        NotificationsController.this.soundIn = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, C0488R.raw.sound_in, 1);
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

    private void scheduleNotificationDelay(boolean onlineReason) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("delay notification start, onlineReason = " + onlineReason);
            }
            this.notificationDelayWakelock.acquire(10000);
            notificationsQueue.cancelRunnable(this.notificationDelayRunnable);
            notificationsQueue.postRunnable(this.notificationDelayRunnable, (long) (onlineReason ? 3000 : 1000));
        } catch (Throwable e) {
            FileLog.m3e(e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    protected void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                int hour = Calendar.getInstance().get(11);
                if (hour < 11 || hour > 22) {
                    NotificationsController.this.scheduleNotificationRepeat();
                    return;
                }
                NotificationsController.notificationManager.cancel(NotificationsController.this.notificationId);
                NotificationsController.this.showOrUpdateNotification(true);
            }
        });
    }

    private boolean isEmptyVibration(long[] pattern) {
        if (pattern == null || pattern.length == 0) {
            return false;
        }
        for (int a = 0; a < pattern.length; a++) {
            if (pattern[0] != 0) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(26)
    private String validateChannelId(long dialogId, String name, long[] vibrationPattern, int ledColor, Uri sound, int importance, long[] configVibrationPattern, Uri configSound, int configImportance) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        String key = "org.telegram.key" + dialogId;
        String channelId = preferences.getString(key, null);
        String settings = preferences.getString(key + "_s", null);
        StringBuilder newSettings = new StringBuilder();
        for (long append : vibrationPattern) {
            newSettings.append(append);
        }
        newSettings.append(ledColor);
        if (sound != null) {
            newSettings.append(sound.toString());
        }
        newSettings.append(importance);
        String newSettingsHash = Utilities.MD5(newSettings.toString());
        if (!(channelId == null || settings.equals(newSettingsHash))) {
            if (false) {
                preferences.edit().putString(key, channelId).putString(key + "_s", newSettingsHash).commit();
            } else {
                systemNotificationManager.deleteNotificationChannel(channelId);
                channelId = null;
            }
        }
        if (channelId == null) {
            channelId = this.currentAccount + "channel" + dialogId + "_" + Utilities.random.nextLong();
            NotificationChannel notificationChannel = new NotificationChannel(channelId, name, importance);
            if (ledColor != 0) {
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(ledColor);
            }
            if (isEmptyVibration(vibrationPattern)) {
                notificationChannel.enableVibration(false);
            } else {
                notificationChannel.enableVibration(true);
                if (vibrationPattern != null && vibrationPattern.length > 0) {
                    notificationChannel.setVibrationPattern(vibrationPattern);
                }
            }
            if (sound != null) {
                Builder builder = new Builder();
                builder.setContentType(4);
                builder.setUsage(5);
                notificationChannel.setSound(sound, builder.build());
            } else {
                notificationChannel.setSound(null, null);
            }
            systemNotificationManager.createNotificationChannel(notificationChannel);
            preferences.edit().putString(key, channelId).putString(key + "_s", newSettingsHash).commit();
        }
        return channelId;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void showOrUpdateNotification(boolean notifyAboutLast) {
        if (!UserConfig.getInstance(this.currentAccount).isClientActivated() || this.pushMessages.isEmpty()) {
            dismissNotification();
            return;
        }
        try {
            ConnectionsManager.getInstance(this.currentAccount).resumeNetworkMaybe();
            MessageObject lastMessageObject = (MessageObject) this.pushMessages.get(0);
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            int dismissDate = preferences.getInt("dismissDate", 0);
            if (lastMessageObject.messageOwner.date <= dismissDate) {
                dismissNotification();
                return;
            }
            int notifyMaxCount;
            int notifyDelay;
            Point dialogInfo;
            int count;
            String defaultPath;
            boolean inAppSounds;
            boolean inAppVibrate;
            boolean inAppPreview;
            boolean inAppPriority;
            boolean custom;
            int vibrateOverride;
            int priorityOverride;
            String choosenSoundPath;
            boolean vibrateOnlyIfSilent;
            int mode;
            Uri configSound;
            long[] configVibrationPattern;
            int configImportance;
            Intent intent;
            PendingIntent contentIntent;
            boolean replace;
            String chatName;
            String name;
            String detailText;
            NotificationCompat.Builder mBuilder;
            long[] vibrationPattern;
            int importance;
            Uri sound;
            int silent;
            String lastMessage;
            MessageObject messageObject;
            boolean[] text;
            String message;
            Style inboxStyle;
            int i;
            BitmapDrawable img;
            File file;
            float scaleFactor;
            Options options;
            int i2;
            Bitmap bitmap;
            boolean hasCallback;
            ArrayList<TL_keyboardButtonRow> rows;
            int size;
            int a;
            TL_keyboardButtonRow row;
            int size2;
            int b;
            KeyboardButton button;
            String str;
            Context context;
            int i3;
            long dialog_id = lastMessageObject.getDialogId();
            long override_dialog_id = dialog_id;
            if (lastMessageObject.messageOwner.mentioned) {
                override_dialog_id = (long) lastMessageObject.messageOwner.from_id;
            }
            int mid = lastMessageObject.getId();
            int chat_id = lastMessageObject.messageOwner.to_id.chat_id != 0 ? lastMessageObject.messageOwner.to_id.chat_id : lastMessageObject.messageOwner.to_id.channel_id;
            int user_id = lastMessageObject.messageOwner.to_id.user_id;
            if (user_id == 0) {
                user_id = lastMessageObject.messageOwner.from_id;
            } else if (user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                user_id = lastMessageObject.messageOwner.from_id;
            }
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(user_id));
            Chat chat = null;
            if (chat_id != 0) {
                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(chat_id));
            }
            TLObject photoPath = null;
            boolean notifyDisabled = false;
            int needVibrate = 0;
            int ledColor = -16776961;
            int priority = 0;
            int notifyOverride = getNotifyOverride(preferences, override_dialog_id);
            if (notifyAboutLast && notifyOverride != 2) {
                if (preferences.getBoolean("EnableAll", true)) {
                    if (chat_id != 0) {
                    }
                    if (!(notifyDisabled || dialog_id != override_dialog_id || chat == null)) {
                        if (preferences.getBoolean("custom_" + dialog_id, false)) {
                            notifyMaxCount = 2;
                            notifyDelay = 180;
                        } else {
                            notifyMaxCount = preferences.getInt("smart_max_count_" + dialog_id, 2);
                            notifyDelay = preferences.getInt("smart_delay_" + dialog_id, 180);
                        }
                        if (notifyMaxCount != 0) {
                            dialogInfo = (Point) this.smartNotificationsDialogs.get(dialog_id);
                            if (dialogInfo == null) {
                                this.smartNotificationsDialogs.put(dialog_id, new Point(1, (int) (System.currentTimeMillis() / 1000)));
                            } else if (((long) (dialogInfo.y + notifyDelay)) >= System.currentTimeMillis() / 1000) {
                                dialogInfo.set(1, (int) (System.currentTimeMillis() / 1000));
                            } else {
                                count = dialogInfo.x;
                                if (count >= notifyMaxCount) {
                                    dialogInfo.set(count + 1, (int) (System.currentTimeMillis() / 1000));
                                } else {
                                    notifyDisabled = true;
                                }
                            }
                        }
                    }
                    defaultPath = System.DEFAULT_NOTIFICATION_URI.getPath();
                    inAppSounds = preferences.getBoolean("EnableInAppSounds", true);
                    inAppVibrate = preferences.getBoolean("EnableInAppVibrate", true);
                    inAppPreview = preferences.getBoolean("EnableInAppPreview", true);
                    inAppPriority = preferences.getBoolean("EnableInAppPriority", false);
                    custom = preferences.getBoolean("custom_" + dialog_id, false);
                    if (custom) {
                        vibrateOverride = 0;
                        priorityOverride = 3;
                        choosenSoundPath = null;
                    } else {
                        vibrateOverride = preferences.getInt("vibrate_" + dialog_id, 0);
                        priorityOverride = preferences.getInt("priority_" + dialog_id, 3);
                        choosenSoundPath = preferences.getString("sound_path_" + dialog_id, null);
                    }
                    vibrateOnlyIfSilent = false;
                    if (chat_id != 0) {
                        if (choosenSoundPath == null && choosenSoundPath.equals(defaultPath)) {
                            choosenSoundPath = null;
                        } else if (choosenSoundPath == null) {
                            choosenSoundPath = preferences.getString("GroupSoundPath", defaultPath);
                        }
                        needVibrate = preferences.getInt("vibrate_group", 0);
                        priority = preferences.getInt("priority_group", 1);
                        ledColor = preferences.getInt("GroupLed", -16776961);
                    } else if (user_id != 0) {
                        if (choosenSoundPath == null && choosenSoundPath.equals(defaultPath)) {
                            choosenSoundPath = null;
                        } else if (choosenSoundPath == null) {
                            choosenSoundPath = preferences.getString("GlobalSoundPath", defaultPath);
                        }
                        needVibrate = preferences.getInt("vibrate_messages", 0);
                        priority = preferences.getInt("priority_group", 1);
                        ledColor = preferences.getInt("MessagesLed", -16776961);
                    }
                    if (custom) {
                        if (preferences.contains("color_" + dialog_id)) {
                            ledColor = preferences.getInt("color_" + dialog_id, 0);
                        }
                    }
                    if (priorityOverride != 3) {
                        priority = priorityOverride;
                    }
                    if (needVibrate == 4) {
                        vibrateOnlyIfSilent = true;
                        needVibrate = 0;
                    }
                    if ((needVibrate == 2 && (vibrateOverride == 1 || vibrateOverride == 3)) || ((needVibrate != 2 && vibrateOverride == 2) || !(vibrateOverride == 0 || vibrateOverride == 4))) {
                        needVibrate = vibrateOverride;
                    }
                    if (!ApplicationLoader.mainInterfacePaused) {
                        if (!inAppSounds) {
                            choosenSoundPath = null;
                        }
                        if (!inAppVibrate) {
                            needVibrate = 2;
                        }
                        if (!inAppPriority) {
                            priority = 0;
                        } else if (priority == 2) {
                            priority = 1;
                        }
                    }
                    if (vibrateOnlyIfSilent && needVibrate != 2) {
                        mode = audioManager.getRingerMode();
                        if (!(mode == 0 || mode == 1)) {
                            needVibrate = 2;
                        }
                    }
                    configSound = null;
                    configVibrationPattern = null;
                    configImportance = 0;
                    if (VERSION.SDK_INT >= 26) {
                        if (needVibrate == 2) {
                            configVibrationPattern = new long[]{0, 0};
                        } else if (needVibrate != 1) {
                            configVibrationPattern = new long[]{0, 100, 0, 100};
                        } else if (needVibrate != 0 || needVibrate == 4) {
                            configVibrationPattern = new long[0];
                        } else if (needVibrate == 3) {
                            configVibrationPattern = new long[]{0, 1000};
                        }
                        if (choosenSoundPath != null) {
                            if (!choosenSoundPath.equals("NoSound")) {
                                configSound = choosenSoundPath.equals(defaultPath) ? System.DEFAULT_NOTIFICATION_URI : Uri.parse(choosenSoundPath);
                            }
                        }
                        if (priority != 0) {
                            configImportance = 3;
                        } else if (priority != 1 || priority == 2) {
                            configImportance = 4;
                        } else if (priority == 4) {
                            configImportance = 1;
                        } else if (priority == 5) {
                            configImportance = 2;
                        }
                    }
                    if (notifyDisabled) {
                        needVibrate = 0;
                        priority = 0;
                        ledColor = 0;
                        choosenSoundPath = null;
                    }
                    intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                    intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                    intent.setFlags(32768);
                    if (((int) dialog_id) != 0) {
                        if (this.pushDialogs.size() == 1) {
                            if (chat_id != 0) {
                                intent.putExtra("chatId", chat_id);
                            } else if (user_id != 0) {
                                intent.putExtra("userId", user_id);
                            }
                        }
                        if (!AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
                            photoPath = null;
                        } else if (this.pushDialogs.size() == 1) {
                            if (chat != null) {
                                if (!(chat.photo == null || chat.photo.photo_small == null || chat.photo.photo_small.volume_id == 0 || chat.photo.photo_small.local_id == 0)) {
                                    photoPath = chat.photo.photo_small;
                                }
                            } else if (!(user == null || user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0)) {
                                photoPath = user.photo.photo_small;
                            }
                        }
                    } else if (this.pushDialogs.size() == 1) {
                        intent.putExtra("encId", (int) (dialog_id >> 32));
                    }
                    intent.putExtra("currentAccount", this.currentAccount);
                    contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM);
                    replace = true;
                    if (((chat_id == 0 && chat == null) || user == null) && lastMessageObject.isFcmMessage()) {
                        chatName = lastMessageObject.localName;
                    } else if (chat == null) {
                        chatName = chat.title;
                    } else {
                        chatName = UserObject.getUserName(user);
                    }
                    if (((int) dialog_id) != 0 || this.pushDialogs.size() > 1 || AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
                        name = LocaleController.getString("AppName", C0488R.string.AppName);
                        replace = false;
                    } else {
                        name = chatName;
                    }
                    if (UserConfig.getActivatedAccountsCount() > 1) {
                        detailText = TtmlNode.ANONYMOUS_REGION_ID;
                    } else if (this.pushDialogs.size() != 1) {
                        detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser());
                    } else {
                        detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser()) + "\u30fb";
                    }
                    if (this.pushDialogs.size() != 1 || VERSION.SDK_INT < 23) {
                        if (this.pushDialogs.size() != 1) {
                            detailText = detailText + LocaleController.formatPluralString("NewMessages", this.total_unread_count);
                        } else {
                            detailText = detailText + LocaleController.formatString("NotificationMessagesPeopleDisplayOrder", C0488R.string.NotificationMessagesPeopleDisplayOrder, LocaleController.formatPluralString("NewMessages", this.total_unread_count), LocaleController.formatPluralString("FromChats", this.pushDialogs.size()));
                        }
                    }
                    mBuilder = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(C0488R.drawable.notification).setAutoCancel(true).setNumber(this.total_unread_count).setContentIntent(contentIntent).setGroup(this.notificationGroup).setGroupSummary(true).setShowWhen(true).setWhen(((long) lastMessageObject.messageOwner.date) * 1000).setColor(-13851168);
                    vibrationPattern = null;
                    importance = 0;
                    sound = null;
                    mBuilder.setCategory("msg");
                    if (chat == null && user != null && user.phone != null && user.phone.length() > 0) {
                        mBuilder.addPerson("tel:+" + user.phone);
                    }
                    silent = 2;
                    lastMessage = null;
                    if (this.pushMessages.size() != 1) {
                        messageObject = (MessageObject) this.pushMessages.get(0);
                        text = new boolean[1];
                        lastMessage = getStringForMessage(messageObject, false, text);
                        message = lastMessage;
                        silent = messageObject.messageOwner.silent ? 1 : 0;
                        if (message != null) {
                            if (replace) {
                                if (chat != null) {
                                    message = message.replace(" @ " + name, TtmlNode.ANONYMOUS_REGION_ID);
                                } else if (text[0]) {
                                    message = message.replace(name + " ", TtmlNode.ANONYMOUS_REGION_ID);
                                } else {
                                    message = message.replace(name + ": ", TtmlNode.ANONYMOUS_REGION_ID);
                                }
                            }
                            mBuilder.setContentText(message);
                            mBuilder.setStyle(new BigTextStyle().bigText(message));
                        } else {
                            return;
                        }
                    }
                    mBuilder.setContentText(detailText);
                    inboxStyle = new InboxStyle();
                    inboxStyle.setBigContentTitle(name);
                    count = Math.min(10, this.pushMessages.size());
                    text = new boolean[1];
                    for (i = 0; i < count; i++) {
                        messageObject = (MessageObject) this.pushMessages.get(i);
                        message = getStringForMessage(messageObject, false, text);
                        if (message != null && messageObject.messageOwner.date > dismissDate) {
                            if (silent == 2) {
                                lastMessage = message;
                                silent = messageObject.messageOwner.silent ? 1 : 0;
                            }
                            if (this.pushDialogs.size() == 1 && replace) {
                                message = chat == null ? message.replace(" @ " + name, TtmlNode.ANONYMOUS_REGION_ID) : text[0] ? message.replace(name + ": ", TtmlNode.ANONYMOUS_REGION_ID) : message.replace(name + " ", TtmlNode.ANONYMOUS_REGION_ID);
                            }
                            inboxStyle.addLine(message);
                        }
                    }
                    inboxStyle.setSummaryText(detailText);
                    mBuilder.setStyle(inboxStyle);
                    intent = new Intent(ApplicationLoader.applicationContext, NotificationDismissReceiver.class);
                    intent.putExtra("messageDate", lastMessageObject.messageOwner.date);
                    intent.putExtra("currentAccount", this.currentAccount);
                    mBuilder.setDeleteIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 1, intent, 134217728));
                    if (photoPath != null) {
                        img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                        if (img == null) {
                            mBuilder.setLargeIcon(img.getBitmap());
                        } else {
                            try {
                                file = FileLoader.getPathToAttach(photoPath, true);
                                if (file.exists()) {
                                    scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                                    options = new Options();
                                    if (scaleFactor >= 1.0f) {
                                        i2 = 1;
                                    } else {
                                        i2 = (int) scaleFactor;
                                    }
                                    options.inSampleSize = i2;
                                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                                    if (bitmap != null) {
                                        mBuilder.setLargeIcon(bitmap);
                                    }
                                }
                            } catch (Throwable th) {
                            }
                        }
                    }
                    if (notifyAboutLast || silent == 1) {
                        mBuilder.setPriority(-1);
                        if (VERSION.SDK_INT >= 26) {
                            importance = 2;
                        }
                    } else if (priority == 0) {
                        mBuilder.setPriority(0);
                        if (VERSION.SDK_INT >= 26) {
                            importance = 3;
                        }
                    } else if (priority == 1 || priority == 2) {
                        mBuilder.setPriority(1);
                        if (VERSION.SDK_INT >= 26) {
                            importance = 4;
                        }
                    } else if (priority == 4) {
                        mBuilder.setPriority(-2);
                        if (VERSION.SDK_INT >= 26) {
                            importance = 1;
                        }
                    } else if (priority == 5) {
                        mBuilder.setPriority(-1);
                        if (VERSION.SDK_INT >= 26) {
                            importance = 2;
                        }
                    }
                    if (silent != 1 || notifyDisabled) {
                        vibrationPattern = new long[]{0, 0};
                        mBuilder.setVibrate(vibrationPattern);
                    } else {
                        if (ApplicationLoader.mainInterfacePaused || inAppPreview) {
                            if (lastMessage.length() > 100) {
                                lastMessage = lastMessage.substring(0, 100).replace('\n', ' ').trim() + "...";
                            }
                            mBuilder.setTicker(lastMessage);
                        }
                        if (!(MediaController.getInstance().isRecordingAudio() || choosenSoundPath == null)) {
                            if (!choosenSoundPath.equals("NoSound")) {
                                if (VERSION.SDK_INT >= 26) {
                                    sound = choosenSoundPath.equals(defaultPath) ? System.DEFAULT_NOTIFICATION_URI : Uri.parse(choosenSoundPath);
                                } else if (choosenSoundPath.equals(defaultPath)) {
                                    mBuilder.setSound(System.DEFAULT_NOTIFICATION_URI, 5);
                                } else {
                                    mBuilder.setSound(Uri.parse(choosenSoundPath), 5);
                                }
                            }
                        }
                        if (ledColor != 0) {
                            mBuilder.setLights(ledColor, 1000, 1000);
                        }
                        if (needVibrate == 2 || MediaController.getInstance().isRecordingAudio()) {
                            vibrationPattern = new long[]{0, 0};
                            mBuilder.setVibrate(vibrationPattern);
                        } else if (needVibrate == 1) {
                            vibrationPattern = new long[]{0, 100, 0, 100};
                            mBuilder.setVibrate(vibrationPattern);
                        } else if (needVibrate == 0 || needVibrate == 4) {
                            mBuilder.setDefaults(2);
                            vibrationPattern = new long[0];
                        } else if (needVibrate == 3) {
                            vibrationPattern = new long[]{0, 1000};
                            mBuilder.setVibrate(vibrationPattern);
                        }
                    }
                    hasCallback = false;
                    if (!(AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter || lastMessageObject.getDialogId() != 777000 || lastMessageObject.messageOwner.reply_markup == null)) {
                        rows = lastMessageObject.messageOwner.reply_markup.rows;
                        size = rows.size();
                        for (a = 0; a < size; a++) {
                            row = (TL_keyboardButtonRow) rows.get(a);
                            size2 = row.buttons.size();
                            for (b = 0; b < size2; b++) {
                                button = (KeyboardButton) row.buttons.get(b);
                                if (button instanceof TL_keyboardButtonCallback) {
                                    intent = new Intent(ApplicationLoader.applicationContext, NotificationCallbackReceiver.class);
                                    intent.putExtra("currentAccount", this.currentAccount);
                                    intent.putExtra("did", dialog_id);
                                    if (button.data != null) {
                                        intent.putExtra(DataSchemeDataSource.SCHEME_DATA, button.data);
                                    }
                                    intent.putExtra("mid", lastMessageObject.getId());
                                    str = button.text;
                                    context = ApplicationLoader.applicationContext;
                                    i3 = this.lastButtonId;
                                    this.lastButtonId = i3 + 1;
                                    mBuilder.addAction(0, str, PendingIntent.getBroadcast(context, i3, intent, 134217728));
                                    hasCallback = true;
                                }
                            }
                        }
                    }
                    if (!hasCallback && VERSION.SDK_INT < 24 && SharedConfig.passcodeHash.length() == 0 && hasMessagesToReply()) {
                        intent = new Intent(ApplicationLoader.applicationContext, PopupReplyReceiver.class);
                        intent.putExtra("currentAccount", this.currentAccount);
                        if (VERSION.SDK_INT > 19) {
                            mBuilder.addAction(C0488R.drawable.ic_ab_reply2, LocaleController.getString("Reply", C0488R.string.Reply), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, 134217728));
                        } else {
                            mBuilder.addAction(C0488R.drawable.ic_ab_reply, LocaleController.getString("Reply", C0488R.string.Reply), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, 134217728));
                        }
                    }
                    if (VERSION.SDK_INT >= 26) {
                        mBuilder.setChannelId(validateChannelId(dialog_id, chatName, vibrationPattern, ledColor, sound, importance, configVibrationPattern, configSound, configImportance));
                    }
                    showExtraNotifications(mBuilder, notifyAboutLast, detailText);
                    this.lastNotificationIsNoData = false;
                    scheduleNotificationRepeat();
                }
            }
            notifyDisabled = true;
            if (preferences.getBoolean("custom_" + dialog_id, false)) {
                notifyMaxCount = 2;
                notifyDelay = 180;
            } else {
                notifyMaxCount = preferences.getInt("smart_max_count_" + dialog_id, 2);
                notifyDelay = preferences.getInt("smart_delay_" + dialog_id, 180);
            }
            if (notifyMaxCount != 0) {
                dialogInfo = (Point) this.smartNotificationsDialogs.get(dialog_id);
                if (dialogInfo == null) {
                    this.smartNotificationsDialogs.put(dialog_id, new Point(1, (int) (System.currentTimeMillis() / 1000)));
                } else if (((long) (dialogInfo.y + notifyDelay)) >= System.currentTimeMillis() / 1000) {
                    count = dialogInfo.x;
                    if (count >= notifyMaxCount) {
                        notifyDisabled = true;
                    } else {
                        dialogInfo.set(count + 1, (int) (System.currentTimeMillis() / 1000));
                    }
                } else {
                    dialogInfo.set(1, (int) (System.currentTimeMillis() / 1000));
                }
            }
            defaultPath = System.DEFAULT_NOTIFICATION_URI.getPath();
            inAppSounds = preferences.getBoolean("EnableInAppSounds", true);
            inAppVibrate = preferences.getBoolean("EnableInAppVibrate", true);
            inAppPreview = preferences.getBoolean("EnableInAppPreview", true);
            inAppPriority = preferences.getBoolean("EnableInAppPriority", false);
            custom = preferences.getBoolean("custom_" + dialog_id, false);
            if (custom) {
                vibrateOverride = 0;
                priorityOverride = 3;
                choosenSoundPath = null;
            } else {
                vibrateOverride = preferences.getInt("vibrate_" + dialog_id, 0);
                priorityOverride = preferences.getInt("priority_" + dialog_id, 3);
                choosenSoundPath = preferences.getString("sound_path_" + dialog_id, null);
            }
            vibrateOnlyIfSilent = false;
            if (chat_id != 0) {
                if (choosenSoundPath == null) {
                }
                if (choosenSoundPath == null) {
                    choosenSoundPath = preferences.getString("GroupSoundPath", defaultPath);
                }
                needVibrate = preferences.getInt("vibrate_group", 0);
                priority = preferences.getInt("priority_group", 1);
                ledColor = preferences.getInt("GroupLed", -16776961);
            } else if (user_id != 0) {
                if (choosenSoundPath == null) {
                }
                if (choosenSoundPath == null) {
                    choosenSoundPath = preferences.getString("GlobalSoundPath", defaultPath);
                }
                needVibrate = preferences.getInt("vibrate_messages", 0);
                priority = preferences.getInt("priority_group", 1);
                ledColor = preferences.getInt("MessagesLed", -16776961);
            }
            if (custom) {
                if (preferences.contains("color_" + dialog_id)) {
                    ledColor = preferences.getInt("color_" + dialog_id, 0);
                }
            }
            if (priorityOverride != 3) {
                priority = priorityOverride;
            }
            if (needVibrate == 4) {
                vibrateOnlyIfSilent = true;
                needVibrate = 0;
            }
            needVibrate = vibrateOverride;
            if (ApplicationLoader.mainInterfacePaused) {
                if (inAppSounds) {
                    choosenSoundPath = null;
                }
                if (inAppVibrate) {
                    needVibrate = 2;
                }
                if (!inAppPriority) {
                    priority = 0;
                } else if (priority == 2) {
                    priority = 1;
                }
            }
            try {
                mode = audioManager.getRingerMode();
                needVibrate = 2;
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            configSound = null;
            configVibrationPattern = null;
            configImportance = 0;
            if (VERSION.SDK_INT >= 26) {
                if (needVibrate == 2) {
                    configVibrationPattern = new long[]{0, 0};
                } else if (needVibrate != 1) {
                    if (needVibrate != 0) {
                    }
                    configVibrationPattern = new long[0];
                } else {
                    configVibrationPattern = new long[]{0, 100, 0, 100};
                }
                if (choosenSoundPath != null) {
                    if (choosenSoundPath.equals("NoSound")) {
                        if (choosenSoundPath.equals(defaultPath)) {
                        }
                    }
                }
                if (priority != 0) {
                    if (priority != 1) {
                    }
                    configImportance = 4;
                } else {
                    configImportance = 3;
                }
            }
            if (notifyDisabled) {
                needVibrate = 0;
                priority = 0;
                ledColor = 0;
                choosenSoundPath = null;
            }
            intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.setFlags(32768);
            if (((int) dialog_id) != 0) {
                if (this.pushDialogs.size() == 1) {
                    if (chat_id != 0) {
                        intent.putExtra("chatId", chat_id);
                    } else if (user_id != 0) {
                        intent.putExtra("userId", user_id);
                    }
                }
                if (AndroidUtilities.needShowPasscode(false)) {
                }
                photoPath = null;
            } else if (this.pushDialogs.size() == 1) {
                intent.putExtra("encId", (int) (dialog_id >> 32));
            }
            intent.putExtra("currentAccount", this.currentAccount);
            contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM);
            replace = true;
            if (chat_id == 0) {
            }
            if (chat == null) {
                chatName = UserObject.getUserName(user);
            } else {
                chatName = chat.title;
            }
            if (((int) dialog_id) != 0) {
            }
            name = LocaleController.getString("AppName", C0488R.string.AppName);
            replace = false;
            if (UserConfig.getActivatedAccountsCount() > 1) {
                detailText = TtmlNode.ANONYMOUS_REGION_ID;
            } else if (this.pushDialogs.size() != 1) {
                detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser()) + "\u30fb";
            } else {
                detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser());
            }
            if (this.pushDialogs.size() != 1) {
                detailText = detailText + LocaleController.formatString("NotificationMessagesPeopleDisplayOrder", C0488R.string.NotificationMessagesPeopleDisplayOrder, LocaleController.formatPluralString("NewMessages", this.total_unread_count), LocaleController.formatPluralString("FromChats", this.pushDialogs.size()));
            } else {
                detailText = detailText + LocaleController.formatPluralString("NewMessages", this.total_unread_count);
            }
            mBuilder = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(C0488R.drawable.notification).setAutoCancel(true).setNumber(this.total_unread_count).setContentIntent(contentIntent).setGroup(this.notificationGroup).setGroupSummary(true).setShowWhen(true).setWhen(((long) lastMessageObject.messageOwner.date) * 1000).setColor(-13851168);
            vibrationPattern = null;
            importance = 0;
            sound = null;
            mBuilder.setCategory("msg");
            mBuilder.addPerson("tel:+" + user.phone);
            silent = 2;
            lastMessage = null;
            if (this.pushMessages.size() != 1) {
                mBuilder.setContentText(detailText);
                inboxStyle = new InboxStyle();
                inboxStyle.setBigContentTitle(name);
                count = Math.min(10, this.pushMessages.size());
                text = new boolean[1];
                for (i = 0; i < count; i++) {
                    messageObject = (MessageObject) this.pushMessages.get(i);
                    message = getStringForMessage(messageObject, false, text);
                    if (silent == 2) {
                        lastMessage = message;
                        if (messageObject.messageOwner.silent) {
                        }
                    }
                    if (chat == null) {
                        if (text[0]) {
                        }
                    }
                    inboxStyle.addLine(message);
                }
                inboxStyle.setSummaryText(detailText);
                mBuilder.setStyle(inboxStyle);
            } else {
                messageObject = (MessageObject) this.pushMessages.get(0);
                text = new boolean[1];
                lastMessage = getStringForMessage(messageObject, false, text);
                message = lastMessage;
                if (messageObject.messageOwner.silent) {
                }
                if (message != null) {
                    if (replace) {
                        if (chat != null) {
                            message = message.replace(" @ " + name, TtmlNode.ANONYMOUS_REGION_ID);
                        } else if (text[0]) {
                            message = message.replace(name + " ", TtmlNode.ANONYMOUS_REGION_ID);
                        } else {
                            message = message.replace(name + ": ", TtmlNode.ANONYMOUS_REGION_ID);
                        }
                    }
                    mBuilder.setContentText(message);
                    mBuilder.setStyle(new BigTextStyle().bigText(message));
                } else {
                    return;
                }
            }
            intent = new Intent(ApplicationLoader.applicationContext, NotificationDismissReceiver.class);
            intent.putExtra("messageDate", lastMessageObject.messageOwner.date);
            intent.putExtra("currentAccount", this.currentAccount);
            mBuilder.setDeleteIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 1, intent, 134217728));
            if (photoPath != null) {
                img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                if (img == null) {
                    file = FileLoader.getPathToAttach(photoPath, true);
                    if (file.exists()) {
                        scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                        options = new Options();
                        if (scaleFactor >= 1.0f) {
                            i2 = (int) scaleFactor;
                        } else {
                            i2 = 1;
                        }
                        options.inSampleSize = i2;
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                        if (bitmap != null) {
                            mBuilder.setLargeIcon(bitmap);
                        }
                    }
                } else {
                    mBuilder.setLargeIcon(img.getBitmap());
                }
            }
            if (notifyAboutLast) {
            }
            mBuilder.setPriority(-1);
            if (VERSION.SDK_INT >= 26) {
                importance = 2;
            }
            if (silent != 1) {
            }
            vibrationPattern = new long[]{0, 0};
            mBuilder.setVibrate(vibrationPattern);
            hasCallback = false;
            rows = lastMessageObject.messageOwner.reply_markup.rows;
            size = rows.size();
            for (a = 0; a < size; a++) {
                row = (TL_keyboardButtonRow) rows.get(a);
                size2 = row.buttons.size();
                for (b = 0; b < size2; b++) {
                    button = (KeyboardButton) row.buttons.get(b);
                    if (button instanceof TL_keyboardButtonCallback) {
                        intent = new Intent(ApplicationLoader.applicationContext, NotificationCallbackReceiver.class);
                        intent.putExtra("currentAccount", this.currentAccount);
                        intent.putExtra("did", dialog_id);
                        if (button.data != null) {
                            intent.putExtra(DataSchemeDataSource.SCHEME_DATA, button.data);
                        }
                        intent.putExtra("mid", lastMessageObject.getId());
                        str = button.text;
                        context = ApplicationLoader.applicationContext;
                        i3 = this.lastButtonId;
                        this.lastButtonId = i3 + 1;
                        mBuilder.addAction(0, str, PendingIntent.getBroadcast(context, i3, intent, 134217728));
                        hasCallback = true;
                    }
                }
            }
            intent = new Intent(ApplicationLoader.applicationContext, PopupReplyReceiver.class);
            intent.putExtra("currentAccount", this.currentAccount);
            if (VERSION.SDK_INT > 19) {
                mBuilder.addAction(C0488R.drawable.ic_ab_reply, LocaleController.getString("Reply", C0488R.string.Reply), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, 134217728));
            } else {
                mBuilder.addAction(C0488R.drawable.ic_ab_reply2, LocaleController.getString("Reply", C0488R.string.Reply), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, 134217728));
            }
            if (VERSION.SDK_INT >= 26) {
                mBuilder.setChannelId(validateChannelId(dialog_id, chatName, vibrationPattern, ledColor, sound, importance, configVibrationPattern, configSound, configImportance));
            }
            showExtraNotifications(mBuilder, notifyAboutLast, detailText);
            this.lastNotificationIsNoData = false;
            scheduleNotificationRepeat();
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
    }

    @SuppressLint({"InlinedApi"})
    private void showExtraNotifications(NotificationCompat.Builder notificationBuilder, boolean notifyAboutLast, String summary) {
        Notification mainNotification = notificationBuilder.build();
        if (VERSION.SDK_INT < 18) {
            notificationManager.notify(this.notificationId, mainNotification);
            return;
        }
        int a;
        ArrayList<Long> sortedDialogs = new ArrayList();
        LongSparseArray<ArrayList<MessageObject>> messagesByDialogs = new LongSparseArray();
        for (a = 0; a < this.pushMessages.size(); a++) {
            MessageObject messageObject = (MessageObject) this.pushMessages.get(a);
            long dialog_id = messageObject.getDialogId();
            ArrayList<MessageObject> arrayList = (ArrayList) messagesByDialogs.get(dialog_id);
            if (arrayList == null) {
                arrayList = new ArrayList();
                messagesByDialogs.put(dialog_id, arrayList);
                sortedDialogs.add(0, Long.valueOf(dialog_id));
            }
            arrayList.add(messageObject);
        }
        LongSparseArray<Integer> oldIdsWear = this.wearNotificationsIds.clone();
        this.wearNotificationsIds.clear();
        ArrayList<AnonymousClass1NotificationHolder> holders = new ArrayList();
        JSONArray serializedNotifications = null;
        if (WearDataLayerListenerService.isWatchConnected()) {
            serializedNotifications = new JSONArray();
        }
        int size = sortedDialogs.size();
        for (int b = 0; b < size; b++) {
            boolean canReply;
            String name;
            String dismissalID;
            dialog_id = ((Long) sortedDialogs.get(b)).longValue();
            ArrayList<MessageObject> messageObjects = (ArrayList) messagesByDialogs.get(dialog_id);
            int max_id = ((MessageObject) messageObjects.get(0)).getId();
            int lowerId = (int) dialog_id;
            int highId = (int) (dialog_id >> 32);
            Integer internalId = (Integer) oldIdsWear.get(dialog_id);
            if (internalId != null) {
                oldIdsWear.remove(dialog_id);
            } else if (lowerId != 0) {
                internalId = Integer.valueOf(lowerId);
            } else {
                internalId = Integer.valueOf(highId);
            }
            JSONObject jSONObject = null;
            if (serializedNotifications != null) {
                jSONObject = new JSONObject();
            }
            MessageObject lastMessageObject = (MessageObject) messageObjects.get(0);
            int max_date = lastMessageObject.messageOwner.date;
            Chat chat = null;
            User user = null;
            boolean isChannel = false;
            boolean isSupergroup = false;
            TLObject photoPath = null;
            if (lowerId != 0) {
                canReply = true;
                if (lowerId > 0) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lowerId));
                    if (user != null) {
                        name = UserObject.getUserName(user);
                        if (!(user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0)) {
                            photoPath = user.photo.photo_small;
                        }
                    } else if (lastMessageObject.isFcmMessage()) {
                        name = lastMessageObject.localName;
                    } else {
                    }
                } else {
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lowerId));
                    if (chat != null) {
                        isSupergroup = chat.megagroup;
                        isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
                        name = chat.title;
                        if (!(chat.photo == null || chat.photo.photo_small == null || chat.photo.photo_small.volume_id == 0 || chat.photo.photo_small.local_id == 0)) {
                            photoPath = chat.photo.photo_small;
                        }
                    } else if (lastMessageObject.isFcmMessage()) {
                        isSupergroup = lastMessageObject.isMegagroup();
                        name = lastMessageObject.localName;
                        isChannel = lastMessageObject.localChannel;
                    } else {
                    }
                }
            } else {
                canReply = false;
                EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(highId));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                    if (user != null) {
                        name = LocaleController.getString("SecretChatName", C0488R.string.SecretChatName);
                        photoPath = null;
                        jSONObject = null;
                    }
                }
            }
            if (AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
                name = LocaleController.getString("AppName", C0488R.string.AppName);
                photoPath = null;
                canReply = false;
            }
            UnreadConversation.Builder unreadConvBuilder = new UnreadConversation.Builder(name).setLatestTimestamp(((long) max_date) * 1000);
            Intent intent = new Intent(ApplicationLoader.applicationContext, AutoMessageHeardReceiver.class);
            intent.addFlags(32);
            intent.setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
            intent.putExtra("dialog_id", dialog_id);
            intent.putExtra("max_id", max_id);
            intent.putExtra("currentAccount", this.currentAccount);
            unreadConvBuilder.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728));
            Action wearReplyAction = null;
            if ((!isChannel || isSupergroup) && canReply && !SharedConfig.isWaitingForPasscodeEnter) {
                String replyToString;
                intent = new Intent(ApplicationLoader.applicationContext, AutoMessageReplyReceiver.class);
                intent.addFlags(32);
                intent.setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
                intent.putExtra("dialog_id", dialog_id);
                intent.putExtra("max_id", max_id);
                intent.putExtra("currentAccount", this.currentAccount);
                unreadConvBuilder.setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728), new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", C0488R.string.Reply)).build());
                intent = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                intent.putExtra("dialog_id", dialog_id);
                intent.putExtra("max_id", max_id);
                intent.putExtra("currentAccount", this.currentAccount);
                PendingIntent replyPendingIntent = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728);
                RemoteInput remoteInputWear = new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", C0488R.string.Reply)).build();
                if (lowerId < 0) {
                    replyToString = LocaleController.formatString("ReplyToGroup", C0488R.string.ReplyToGroup, name);
                } else {
                    replyToString = LocaleController.formatString("ReplyToUser", C0488R.string.ReplyToUser, name);
                }
                wearReplyAction = new Action.Builder(C0488R.drawable.ic_reply_icon, replyToString, replyPendingIntent).setAllowGeneratedReplies(true).addRemoteInput(remoteInputWear).build();
            }
            Integer count = (Integer) this.pushDialogs.get(dialog_id);
            if (count == null) {
                count = Integer.valueOf(0);
            }
            Style messagingStyle = new MessagingStyle(TtmlNode.ANONYMOUS_REGION_ID).setConversationTitle(String.format("%1$s (%2$s)", new Object[]{name, LocaleController.formatPluralString("NewMessages", Math.max(count.intValue(), messageObjects.size()))}));
            StringBuilder text = new StringBuilder();
            boolean[] isText = new boolean[1];
            ArrayList<TL_keyboardButtonRow> rows = null;
            int rowsMid = 0;
            JSONArray serializedMsgs = null;
            if (jSONObject != null) {
                serializedMsgs = new JSONArray();
            }
            for (a = messageObjects.size() - 1; a >= 0; a--) {
                String nameToReplace;
                messageObject = (MessageObject) messageObjects.get(a);
                String message = getStringForMessage(messageObject, false, isText);
                if (messageObject.isFcmMessage()) {
                    nameToReplace = messageObject.localName;
                } else {
                    nameToReplace = name;
                }
                if (message != null) {
                    if (lowerId < 0) {
                        message = message.replace(" @ " + nameToReplace, TtmlNode.ANONYMOUS_REGION_ID);
                    } else if (isText[0]) {
                        message = message.replace(nameToReplace + ": ", TtmlNode.ANONYMOUS_REGION_ID);
                    } else {
                        message = message.replace(nameToReplace + " ", TtmlNode.ANONYMOUS_REGION_ID);
                    }
                    if (text.length() > 0) {
                        text.append("\n\n");
                    }
                    text.append(message);
                    unreadConvBuilder.addMessage(message);
                    messagingStyle.addMessage(message, ((long) messageObject.messageOwner.date) * 1000, null);
                    if (serializedMsgs != null) {
                        try {
                            JSONObject jmsg = new JSONObject();
                            jmsg.put(MimeTypes.BASE_TYPE_TEXT, getShortStringForMessage(messageObject));
                            jmsg.put("date", messageObject.messageOwner.date);
                            if (messageObject.isFromUser() && lowerId < 0) {
                                User sender = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.getFromId()));
                                if (sender != null) {
                                    jmsg.put("fname", sender.first_name);
                                    jmsg.put("lname", sender.last_name);
                                }
                            }
                            serializedMsgs.put(jmsg);
                        } catch (JSONException e) {
                        }
                    }
                    if (dialog_id == 777000 && messageObject.messageOwner.reply_markup != null) {
                        rows = messageObject.messageOwner.reply_markup.rows;
                        rowsMid = messageObject.getId();
                    }
                }
            }
            intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
            intent.setFlags(32768);
            if (lowerId == 0) {
                intent.putExtra("encId", highId);
            } else if (lowerId > 0) {
                intent.putExtra("userId", lowerId);
            } else {
                intent.putExtra("chatId", -lowerId);
            }
            intent.putExtra("currentAccount", this.currentAccount);
            PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM);
            WearableExtender wearableExtender = new WearableExtender();
            if (wearReplyAction != null) {
                wearableExtender.addAction(wearReplyAction);
            }
            if (lowerId == 0) {
                dismissalID = "tgenc" + highId + "_" + max_id;
            } else if (lowerId > 0) {
                dismissalID = "tguser" + lowerId + "_" + max_id;
            } else {
                dismissalID = "tgchat" + (-lowerId) + "_" + max_id;
            }
            wearableExtender.setDismissalId(dismissalID);
            wearableExtender.setBridgeTag("tgaccount" + UserConfig.getInstance(this.currentAccount).getClientUserId());
            WearableExtender summaryExtender = new WearableExtender();
            summaryExtender.setDismissalId("summary_" + dismissalID);
            notificationBuilder.extend(summaryExtender);
            long date = ((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(C0488R.drawable.notification).setGroup(this.notificationGroup).setContentText(text.toString()).setAutoCancel(true).setNumber(messageObjects.size()).setColor(-13851168).setGroupSummary(false).setWhen(date).setShowWhen(true).setShortcutId("sdid_" + dialog_id).setGroupAlertBehavior(1).setStyle(messagingStyle).setContentIntent(contentIntent).extend(wearableExtender).setSortKey(TtmlNode.ANONYMOUS_REGION_ID + (Long.MAX_VALUE - date)).extend(new CarExtender().setUnreadConversation(unreadConvBuilder.build())).setCategory("msg");
            if (this.pushDialogs.size() == 1 && !TextUtils.isEmpty(summary)) {
                builder.setSubText(summary);
            }
            if (lowerId == 0) {
                builder.setLocalOnly(true);
            }
            if (photoPath != null) {
                BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                if (img != null) {
                    builder.setLargeIcon(img.getBitmap());
                } else {
                    try {
                        File file = FileLoader.getPathToAttach(photoPath, true);
                        if (file.exists()) {
                            int i;
                            float scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                            Options options = new Options();
                            if (scaleFactor < 1.0f) {
                                i = 1;
                            } else {
                                i = (int) scaleFactor;
                            }
                            options.inSampleSize = i;
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                            if (bitmap != null) {
                                builder.setLargeIcon(bitmap);
                            }
                        }
                    } catch (Throwable th) {
                    }
                }
            }
            if (!(AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter || rows == null)) {
                int rc = rows.size();
                for (int r = 0; r < rc; r++) {
                    TL_keyboardButtonRow row = (TL_keyboardButtonRow) rows.get(r);
                    int cc = row.buttons.size();
                    for (int c = 0; c < cc; c++) {
                        KeyboardButton button = (KeyboardButton) row.buttons.get(c);
                        if (button instanceof TL_keyboardButtonCallback) {
                            Intent callbackIntent = new Intent(ApplicationLoader.applicationContext, NotificationCallbackReceiver.class);
                            callbackIntent.putExtra("currentAccount", this.currentAccount);
                            callbackIntent.putExtra("did", dialog_id);
                            if (button.data != null) {
                                callbackIntent.putExtra(DataSchemeDataSource.SCHEME_DATA, button.data);
                            }
                            callbackIntent.putExtra("mid", rowsMid);
                            String str = button.text;
                            Context context = ApplicationLoader.applicationContext;
                            int i2 = this.lastButtonId;
                            this.lastButtonId = i2 + 1;
                            builder.addAction(0, str, PendingIntent.getBroadcast(context, i2, callbackIntent, 134217728));
                        }
                    }
                }
            }
            if (chat == null && user != null && user.phone != null && user.phone.length() > 0) {
                builder.addPerson("tel:+" + user.phone);
            }
            if (VERSION.SDK_INT >= 26) {
                builder.setChannelId(OTHER_NOTIFICATIONS_CHANNEL);
            }
            holders.add(new AnonymousClass1NotificationHolder(internalId.intValue(), builder.build()));
            this.wearNotificationsIds.put(dialog_id, internalId);
            if (jSONObject != null) {
                try {
                    jSONObject.put("reply", canReply);
                    jSONObject.put("name", name);
                    jSONObject.put("max_id", max_id);
                    jSONObject.put("max_date", max_date);
                    jSONObject.put(TtmlNode.ATTR_ID, Math.abs(lowerId));
                    if (photoPath != null) {
                        jSONObject.put("photo", photoPath.dc_id + "_" + photoPath.volume_id + "_" + photoPath.secret);
                    }
                    if (serializedMsgs != null) {
                        jSONObject.put("msgs", serializedMsgs);
                    }
                    if (lowerId > 0) {
                        jSONObject.put("type", "user");
                    } else if (lowerId < 0) {
                        if (isChannel || isSupergroup) {
                            jSONObject.put("type", "channel");
                        } else {
                            jSONObject.put("type", "group");
                        }
                    }
                    serializedNotifications.put(jSONObject);
                } catch (JSONException e2) {
                }
            }
        }
        notificationManager.notify(this.notificationId, mainNotification);
        size = holders.size();
        for (a = 0; a < size; a++) {
            ((AnonymousClass1NotificationHolder) holders.get(a)).call();
        }
        for (a = 0; a < oldIdsWear.size(); a++) {
            notificationManager.cancel(((Integer) oldIdsWear.valueAt(a)).intValue());
        }
        if (serializedNotifications != null) {
            try {
                JSONObject s = new JSONObject();
                s.put(TtmlNode.ATTR_ID, UserConfig.getInstance(this.currentAccount).getClientUserId());
                s.put("n", serializedNotifications);
                WearDataLayerListenerService.sendMessageToWatch("/notify", s.toString().getBytes(C0600C.UTF8_NAME), "remote_notifications");
            } catch (Exception e3) {
            }
        }
    }

    public void playOutChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            notificationsQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.NotificationsController$16$1 */
                class C04701 implements OnLoadCompleteListener {
                    C04701() {
                    }

                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                        if (status == 0) {
                            try {
                                soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
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
                                NotificationsController.this.soundPool.setOnLoadCompleteListener(new C04701());
                            }
                            if (NotificationsController.this.soundOut == 0 && !NotificationsController.this.soundOutLoaded) {
                                NotificationsController.this.soundOutLoaded = true;
                                NotificationsController.this.soundOut = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, C0488R.raw.sound_out, 1);
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

    public void updateServerNotificationsSettings(long dialog_id) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        if (((int) dialog_id) != 0) {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            TL_account_updateNotifySettings req = new TL_account_updateNotifySettings();
            req.settings = new TL_inputPeerNotifySettings();
            req.settings.sound = "default";
            int mute_type = preferences.getInt("notify2_" + dialog_id, 0);
            if (mute_type == 3) {
                req.settings.mute_until = preferences.getInt("notifyuntil_" + dialog_id, 0);
            } else {
                req.settings.mute_until = mute_type != 2 ? 0 : ConnectionsManager.DEFAULT_DATACENTER_ID;
            }
            req.settings.show_previews = preferences.getBoolean("preview_" + dialog_id, true);
            req.settings.silent = preferences.getBoolean("silent_" + dialog_id, false);
            req.peer = new TL_inputNotifyPeer();
            ((TL_inputNotifyPeer) req.peer).peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) dialog_id);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
    }
}
