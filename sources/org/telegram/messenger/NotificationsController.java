package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
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
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.Person;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.IconCompat;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC.TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC.TL_inputNotifyPeer;
import org.telegram.tgnet.TLRPC.TL_inputNotifyUsers;
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
import org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
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
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PopupNotificationActivity;

public class NotificationsController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = new NotificationsController[3];
    public static String OTHER_NOTIFICATIONS_CHANNEL = null;
    public static final int TYPE_CHANNEL = 2;
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_PRIVATE = 1;
    protected static AudioManager audioManager = ((AudioManager) ApplicationLoader.applicationContext.getSystemService("audio"));
    public static long globalSecretChatId = -4294967296L;
    private static NotificationManagerCompat notificationManager;
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    private static NotificationManager systemNotificationManager;
    private AlarmManager alarmManager;
    private int currentAccount;
    private ArrayList<MessageObject> delayedPushMessages = new ArrayList();
    private LongSparseArray<MessageObject> fcmRandomMessagesDict = new LongSparseArray();
    private boolean inChatSoundEnabled;
    private int lastBadgeCount = -1;
    private int lastButtonId = 5000;
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
    public boolean showBadgeMessages;
    public boolean showBadgeMuted;
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

    /* renamed from: org.telegram.messenger.NotificationsController$1NotificationHolder */
    class AnonymousClass1NotificationHolder {
        int id;
        Notification notification;

        AnonymousClass1NotificationHolder(int i, Notification n) {
            this.id = i;
            this.notification = n;
        }

        /* Access modifiers changed, original: 0000 */
        public void call() {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("show dialog notification with id " + this.id);
            }
            NotificationsController.notificationManager.notify(this.id, this.notification);
        }
    }

    static {
        notificationManager = null;
        systemNotificationManager = null;
        if (VERSION.SDK_INT >= 26 && ApplicationLoader.applicationContext != null) {
            notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            systemNotificationManager = (NotificationManager) ApplicationLoader.applicationContext.getSystemService("notification");
            checkOtherNotificationsChannel();
        }
    }

    public static NotificationsController getInstance(int num) {
        Throwable th;
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
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public NotificationsController(int instance) {
        Object obj;
        this.currentAccount = instance;
        this.notificationId = this.currentAccount + 1;
        StringBuilder append = new StringBuilder().append("messages");
        if (this.currentAccount == 0) {
            obj = "";
        } else {
            obj = Integer.valueOf(this.currentAccount);
        }
        this.notificationGroup = append.append(obj).toString();
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        this.inChatSoundEnabled = preferences.getBoolean("EnableInChatSound", true);
        this.showBadgeNumber = preferences.getBoolean("badgeNumber", true);
        this.showBadgeMuted = preferences.getBoolean("badgeNumberMuted", false);
        this.showBadgeMessages = preferences.getBoolean("badgeNumberMessages", true);
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
            this.notificationDelayWakelock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "lock");
            this.notificationDelayWakelock.setReferenceCounted(false);
        } catch (Exception e22) {
            FileLog.e(e22);
        }
        this.notificationDelayRunnable = new NotificationsController$$Lambda$0(this);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$0$NotificationsController() {
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
            FileLog.e(e);
        }
    }

    public static void checkOtherNotificationsChannel() {
        if (VERSION.SDK_INT >= 26) {
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
                notificationChannel = new NotificationChannel(OTHER_NOTIFICATIONS_CHANNEL, "Other", 3);
                notificationChannel.enableLights(false);
                notificationChannel.enableVibration(false);
                notificationChannel.setSound(null, null);
                systemNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$1(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$cleanup$1$NotificationsController() {
        this.opened_dialog_id = 0;
        this.total_unread_count = 0;
        this.personal_count = 0;
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        this.fcmRandomMessagesDict.clear();
        this.pushDialogs.clear();
        this.wearNotificationsIds.clear();
        this.lastWearNotifiedMessageId.clear();
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
        Editor editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
        editor.clear();
        editor.commit();
        if (VERSION.SDK_INT >= 26) {
            try {
                String keyStart = this.currentAccount + "channel";
                List<NotificationChannel> list = systemNotificationManager.getNotificationChannels();
                int count = list.size();
                for (int a = 0; a < count; a++) {
                    String id = ((NotificationChannel) list.get(a)).getId();
                    if (id.startsWith(keyStart)) {
                        systemNotificationManager.deleteNotificationChannel(id);
                    }
                }
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
    }

    public void setInChatSoundEnabled(boolean value) {
        this.inChatSoundEnabled = value;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setOpenedDialogId$2$NotificationsController(long dialog_id) {
        this.opened_dialog_id = dialog_id;
    }

    public void setOpenedDialogId(long dialog_id) {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$2(this, dialog_id));
    }

    public void setLastOnlineFromOtherDevice(int time) {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$3(this, time));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setLastOnlineFromOtherDevice$3$NotificationsController(int time) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set last online from other device = " + time);
        }
        this.lastOnlineFromOtherDevice = time;
    }

    public void removeNotificationsForDialog(long did) {
        getInstance(this.currentAccount).processReadMessages(null, did, 0, Integer.MAX_VALUE, false);
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

    /* Access modifiers changed, original: protected */
    public void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$4(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$forceShowPopupForReply$5$NotificationsController() {
        ArrayList<MessageObject> popupArray = new ArrayList();
        for (int a = 0; a < this.pushMessages.size(); a++) {
            MessageObject messageObject = (MessageObject) this.pushMessages.get(a);
            long dialog_id = messageObject.getDialogId();
            if (!((messageObject.messageOwner.mentioned && (messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) || ((int) dialog_id) == 0 || (messageObject.messageOwner.to_id.channel_id != 0 && !messageObject.isMegagroup()))) {
                popupArray.add(0, messageObject);
            }
        }
        if (!popupArray.isEmpty() && !AndroidUtilities.needShowPasscode(false)) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$35(this, popupArray));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$4$NotificationsController(ArrayList popupArray) {
        this.popupReplyMessages = popupArray;
        Intent popupIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
        popupIntent.putExtra("force", true);
        popupIntent.putExtra("currentAccount", this.currentAccount);
        popupIntent.setFlags(NUM);
        ApplicationLoader.applicationContext.startActivity(popupIntent);
        ApplicationLoader.applicationContext.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    public void removeDeletedMessagesFromNotifications(SparseArray<ArrayList<Integer>> deletedMessages) {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$5(this, deletedMessages, new ArrayList(0)));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$removeDeletedMessagesFromNotifications$8$NotificationsController(SparseArray deletedMessages, ArrayList popupArrayRemove) {
        int old_unread_count = this.total_unread_count;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        for (int a = 0; a < deletedMessages.size(); a++) {
            int key = deletedMessages.keyAt(a);
            long dialog_id = (long) (-key);
            ArrayList<Integer> mids = (ArrayList) deletedMessages.get(key);
            Integer currentCount = (Integer) this.pushDialogs.get(dialog_id);
            if (currentCount == null) {
                currentCount = Integer.valueOf(0);
            }
            Integer newCount = currentCount;
            for (int b = 0; b < mids.size(); b++) {
                long mid = ((long) ((Integer) mids.get(b)).intValue()) | (((long) key) << 32);
                MessageObject messageObject = (MessageObject) this.pushMessagesDict.get(mid);
                if (messageObject != null) {
                    this.pushMessagesDict.remove(mid);
                    this.delayedPushMessages.remove(messageObject);
                    this.pushMessages.remove(messageObject);
                    if (isPersonalMessage(messageObject)) {
                        this.personal_count--;
                    }
                    popupArrayRemove.add(messageObject);
                    newCount = Integer.valueOf(newCount.intValue() - 1);
                }
            }
            if (newCount.intValue() <= 0) {
                newCount = Integer.valueOf(0);
                this.smartNotificationsDialogs.remove(dialog_id);
            }
            if (!newCount.equals(currentCount)) {
                this.total_unread_count -= currentCount.intValue();
                this.total_unread_count += newCount.intValue();
                this.pushDialogs.put(dialog_id, newCount);
            }
            if (newCount.intValue() == 0) {
                this.pushDialogs.remove(dialog_id);
                this.pushDialogsOverrideMention.remove(dialog_id);
            }
        }
        if (!popupArrayRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$33(this, popupArrayRemove));
        }
        if (old_unread_count != this.total_unread_count) {
            if (this.notifyCheck) {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
            } else {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            }
            AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$34(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$6$NotificationsController(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$7$NotificationsController(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void removeDeletedHisoryFromNotifications(SparseIntArray deletedMessages) {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$6(this, deletedMessages, new ArrayList(0)));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$removeDeletedHisoryFromNotifications$11$NotificationsController(SparseIntArray deletedMessages, ArrayList popupArrayRemove) {
        int old_unread_count = this.total_unread_count;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        for (int a = 0; a < deletedMessages.size(); a++) {
            int key = deletedMessages.keyAt(a);
            long dialog_id = (long) (-key);
            int id = deletedMessages.get(key);
            Integer currentCount = (Integer) this.pushDialogs.get(dialog_id);
            if (currentCount == null) {
                currentCount = Integer.valueOf(0);
            }
            Integer newCount = currentCount;
            int c = 0;
            while (c < this.pushMessages.size()) {
                MessageObject messageObject = (MessageObject) this.pushMessages.get(c);
                if (messageObject.getDialogId() == dialog_id && messageObject.getId() <= id) {
                    this.pushMessagesDict.remove(messageObject.getIdWithChannel());
                    this.delayedPushMessages.remove(messageObject);
                    this.pushMessages.remove(messageObject);
                    c--;
                    if (isPersonalMessage(messageObject)) {
                        this.personal_count--;
                    }
                    popupArrayRemove.add(messageObject);
                    newCount = Integer.valueOf(newCount.intValue() - 1);
                }
                c++;
            }
            if (newCount.intValue() <= 0) {
                newCount = Integer.valueOf(0);
                this.smartNotificationsDialogs.remove(dialog_id);
            }
            if (!newCount.equals(currentCount)) {
                this.total_unread_count -= currentCount.intValue();
                this.total_unread_count += newCount.intValue();
                this.pushDialogs.put(dialog_id, newCount);
            }
            if (newCount.intValue() == 0) {
                this.pushDialogs.remove(dialog_id);
                this.pushDialogsOverrideMention.remove(dialog_id);
            }
        }
        if (popupArrayRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$31(this, popupArrayRemove));
        }
        if (old_unread_count != this.total_unread_count) {
            if (this.notifyCheck) {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
            } else {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            }
            AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$32(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$9$NotificationsController(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$10$NotificationsController(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void processReadMessages(SparseLongArray inbox, long dialog_id, int max_date, int max_id, boolean isPopup) {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$7(this, inbox, new ArrayList(0), dialog_id, max_id, max_date, isPopup));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processReadMessages$13$NotificationsController(SparseLongArray inbox, ArrayList popupArrayRemove, long dialog_id, int max_id, int max_date, boolean isPopup) {
        int a;
        MessageObject messageObject;
        long mid;
        if (inbox != null) {
            for (int b = 0; b < inbox.size(); b++) {
                int key = inbox.keyAt(b);
                long messageId = inbox.get(key);
                a = 0;
                while (a < this.pushMessages.size()) {
                    messageObject = (MessageObject) this.pushMessages.get(a);
                    if (messageObject.getDialogId() == ((long) key) && messageObject.getId() <= ((int) messageId)) {
                        if (isPersonalMessage(messageObject)) {
                            this.personal_count--;
                        }
                        popupArrayRemove.add(messageObject);
                        mid = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        this.pushMessagesDict.remove(mid);
                        this.delayedPushMessages.remove(messageObject);
                        this.pushMessages.remove(a);
                        a--;
                    }
                    a++;
                }
            }
        }
        if (!(dialog_id == 0 || (max_id == 0 && max_date == 0))) {
            a = 0;
            while (a < this.pushMessages.size()) {
                messageObject = (MessageObject) this.pushMessages.get(a);
                if (messageObject.getDialogId() == dialog_id) {
                    boolean remove = false;
                    if (max_date != 0) {
                        if (messageObject.messageOwner.date <= max_date) {
                            remove = true;
                        }
                    } else if (isPopup) {
                        if (messageObject.getId() == max_id || max_id < 0) {
                            remove = true;
                        }
                    } else if (messageObject.getId() <= max_id || max_id < 0) {
                        remove = true;
                    }
                    if (remove) {
                        if (isPersonalMessage(messageObject)) {
                            this.personal_count--;
                        }
                        this.pushMessages.remove(a);
                        this.delayedPushMessages.remove(messageObject);
                        popupArrayRemove.add(messageObject);
                        mid = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        this.pushMessagesDict.remove(mid);
                        a--;
                    }
                }
                a++;
            }
        }
        if (!popupArrayRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$30(this, popupArrayRemove));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$12$NotificationsController(ArrayList popupArrayRemove) {
        int size = popupArrayRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayRemove.get(a));
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
    }

    private int addToPopupMessages(ArrayList<MessageObject> popupArrayAdd, MessageObject messageObject, int lower_id, long dialog_id, boolean isChannel, SharedPreferences preferences) {
        int popup = 0;
        if (lower_id != 0) {
            if (preferences.getBoolean("custom_" + dialog_id, false)) {
                popup = preferences.getInt("popup_" + dialog_id, 0);
            } else {
                popup = 0;
            }
            if (popup == 0) {
                if (isChannel) {
                    popup = preferences.getInt("popupChannel", 0);
                } else {
                    popup = preferences.getInt(((int) dialog_id) < 0 ? "popupGroup" : "popupAll", 0);
                }
            } else if (popup == 1) {
                popup = 3;
            } else if (popup == 2) {
                popup = 0;
            }
        }
        if (!(popup == 0 || messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup())) {
            popup = 0;
        }
        if (popup != 0) {
            popupArrayAdd.add(0, messageObject);
        }
        return popup;
    }

    public void processNewMessages(ArrayList<MessageObject> messageObjects, boolean isLast, boolean isFcm, CountDownLatch countDownLatch) {
        if (!messageObjects.isEmpty()) {
            notificationsQueue.postRunnable(new NotificationsController$$Lambda$8(this, messageObjects, new ArrayList(0), isFcm, isLast, countDownLatch));
        } else if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processNewMessages$16$NotificationsController(ArrayList messageObjects, ArrayList popupArrayAdd, boolean isFcm, boolean isLast, CountDownLatch countDownLatch) {
        long dialog_id;
        int notifyOverride;
        boolean added = false;
        LongSparseArray<Boolean> settingsCache = new LongSparseArray();
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        boolean allowPinned = preferences.getBoolean("PinnedMessages", true);
        int popup = 0;
        for (int a = 0; a < messageObjects.size(); a++) {
            MessageObject messageObject = (MessageObject) messageObjects.get(a);
            if (!(messageObject.messageOwner != null && messageObject.messageOwner.silent && ((messageObject.messageOwner.action instanceof TL_messageActionContactSignUp) || (messageObject.messageOwner.action instanceof TL_messageActionUserJoined)))) {
                boolean isChannel;
                long mid = (long) messageObject.getId();
                long random_id = messageObject.isFcmMessage() ? messageObject.messageOwner.random_id : 0;
                dialog_id = messageObject.getDialogId();
                int lower_id = (int) dialog_id;
                if (messageObject.messageOwner.to_id.channel_id != 0) {
                    mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                    isChannel = true;
                } else {
                    isChannel = false;
                }
                MessageObject oldMessageObject = (MessageObject) this.pushMessagesDict.get(mid);
                if (oldMessageObject == null && messageObject.messageOwner.random_id != 0) {
                    oldMessageObject = (MessageObject) this.fcmRandomMessagesDict.get(messageObject.messageOwner.random_id);
                    if (oldMessageObject != null) {
                        this.fcmRandomMessagesDict.remove(messageObject.messageOwner.random_id);
                    }
                }
                if (oldMessageObject == null) {
                    if (isFcm) {
                        MessagesStorage.getInstance(this.currentAccount).putPushMessage(messageObject);
                    }
                    long original_dialog_id = dialog_id;
                    if (dialog_id != this.opened_dialog_id || !ApplicationLoader.isScreenOn) {
                        boolean value;
                        if (messageObject.messageOwner.mentioned) {
                            if (allowPinned || !(messageObject.messageOwner.action instanceof TL_messageActionPinMessage)) {
                                dialog_id = (long) messageObject.messageOwner.from_id;
                            }
                        }
                        if (isPersonalMessage(messageObject)) {
                            this.personal_count++;
                        }
                        added = true;
                        if (lower_id < 0) {
                        }
                        int index = settingsCache.indexOfKey(dialog_id);
                        if (index >= 0) {
                            value = ((Boolean) settingsCache.valueAt(index)).booleanValue();
                        } else {
                            notifyOverride = getNotifyOverride(preferences, dialog_id);
                            value = notifyOverride == -1 ? isGlobalNotificationsEnabled(dialog_id) : notifyOverride != 2;
                            settingsCache.put(dialog_id, Boolean.valueOf(value));
                        }
                        if (value) {
                            if (!isFcm) {
                                popup = addToPopupMessages(popupArrayAdd, messageObject, lower_id, dialog_id, isChannel, preferences);
                            }
                            this.delayedPushMessages.add(messageObject);
                            this.pushMessages.add(0, messageObject);
                            if (mid != 0) {
                                this.pushMessagesDict.put(mid, messageObject);
                            } else if (random_id != 0) {
                                this.fcmRandomMessagesDict.put(random_id, messageObject);
                            }
                            if (original_dialog_id != dialog_id) {
                                int i;
                                Integer current = (Integer) this.pushDialogsOverrideMention.get(original_dialog_id);
                                LongSparseArray longSparseArray = this.pushDialogsOverrideMention;
                                if (current == null) {
                                    i = 1;
                                } else {
                                    i = current.intValue() + 1;
                                }
                                longSparseArray.put(original_dialog_id, Integer.valueOf(i));
                            }
                        }
                    } else if (!isFcm) {
                        playInChatSound();
                    }
                } else if (oldMessageObject.isFcmMessage()) {
                    this.pushMessagesDict.put(mid, messageObject);
                    int idxOld = this.pushMessages.indexOf(oldMessageObject);
                    if (idxOld >= 0) {
                        this.pushMessages.set(idxOld, messageObject);
                        popup = addToPopupMessages(popupArrayAdd, messageObject, lower_id, dialog_id, isChannel, preferences);
                    }
                }
            }
        }
        if (added) {
            this.notifyCheck = isLast;
        }
        if (!(popupArrayAdd.isEmpty() || AndroidUtilities.needShowPasscode(false))) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$28(this, popupArrayAdd, popup));
        }
        if (added && isFcm) {
            dialog_id = ((MessageObject) messageObjects.get(0)).getDialogId();
            int old_unread_count = this.total_unread_count;
            notifyOverride = getNotifyOverride(preferences, dialog_id);
            boolean canAddValue = notifyOverride == -1 ? isGlobalNotificationsEnabled(dialog_id) : notifyOverride != 2;
            Integer currentCount = (Integer) this.pushDialogs.get(dialog_id);
            Integer newCount = Integer.valueOf(currentCount != null ? currentCount.intValue() + 1 : 1);
            if (this.notifyCheck && !canAddValue) {
                Integer override = (Integer) this.pushDialogsOverrideMention.get(dialog_id);
                if (!(override == null || override.intValue() == 0)) {
                    canAddValue = true;
                    newCount = override;
                }
            }
            if (canAddValue) {
                if (currentCount != null) {
                    this.total_unread_count -= currentCount.intValue();
                }
                this.total_unread_count += newCount.intValue();
                this.pushDialogs.put(dialog_id, newCount);
            }
            if (old_unread_count != this.total_unread_count) {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
                AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$29(this, this.pushDialogs.size()));
            }
            this.notifyCheck = false;
            if (this.showBadgeNumber) {
                setBadge(getTotalAllUnreadCount());
            }
        }
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$14$NotificationsController(ArrayList popupArrayAdd, int popupFinal) {
        this.popupMessages.addAll(0, popupArrayAdd);
        if (!ApplicationLoader.mainInterfacePaused && (ApplicationLoader.isScreenOn || SharedConfig.isWaitingForPasscodeEnter)) {
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

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$15$NotificationsController(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public int getTotalUnreadCount() {
        return this.total_unread_count;
    }

    public void processDialogsUpdateRead(LongSparseArray<Integer> dialogsToUpdate) {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$9(this, dialogsToUpdate, new ArrayList()));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processDialogsUpdateRead$19$NotificationsController(LongSparseArray dialogsToUpdate, ArrayList popupArrayToRemove) {
        int old_unread_count = this.total_unread_count;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        for (int b = 0; b < dialogsToUpdate.size(); b++) {
            long dialog_id = dialogsToUpdate.keyAt(b);
            int notifyOverride = getNotifyOverride(preferences, dialog_id);
            boolean canAddValue = notifyOverride == -1 ? isGlobalNotificationsEnabled(dialog_id) : notifyOverride != 2;
            Integer currentCount = (Integer) this.pushDialogs.get(dialog_id);
            Integer newCount = (Integer) dialogsToUpdate.get(dialog_id);
            if (this.notifyCheck && !canAddValue) {
                Integer override = (Integer) this.pushDialogsOverrideMention.get(dialog_id);
                if (!(override == null || override.intValue() == 0)) {
                    canAddValue = true;
                    newCount = override;
                }
            }
            if (newCount.intValue() == 0) {
                this.smartNotificationsDialogs.remove(dialog_id);
            }
            if (newCount.intValue() < 0) {
                if (currentCount == null) {
                } else {
                    newCount = Integer.valueOf(currentCount.intValue() + newCount.intValue());
                }
            }
            if ((canAddValue || newCount.intValue() == 0) && currentCount != null) {
                this.total_unread_count -= currentCount.intValue();
            }
            if (newCount.intValue() == 0) {
                this.pushDialogs.remove(dialog_id);
                this.pushDialogsOverrideMention.remove(dialog_id);
                int a = 0;
                while (a < this.pushMessages.size()) {
                    MessageObject messageObject = (MessageObject) this.pushMessages.get(a);
                    if (messageObject.getDialogId() == dialog_id) {
                        if (isPersonalMessage(messageObject)) {
                            this.personal_count--;
                        }
                        this.pushMessages.remove(a);
                        a--;
                        this.delayedPushMessages.remove(messageObject);
                        long mid = (long) messageObject.getId();
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        this.pushMessagesDict.remove(mid);
                        popupArrayToRemove.add(messageObject);
                    }
                    a++;
                }
            } else if (canAddValue) {
                this.total_unread_count += newCount.intValue();
                this.pushDialogs.put(dialog_id, newCount);
            }
        }
        if (!popupArrayToRemove.isEmpty()) {
            AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$26(this, popupArrayToRemove));
        }
        if (old_unread_count != this.total_unread_count) {
            if (this.notifyCheck) {
                scheduleNotificationDelay(this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
            } else {
                this.delayedPushMessages.clear();
                showOrUpdateNotification(this.notifyCheck);
            }
            AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$27(this, this.pushDialogs.size()));
        }
        this.notifyCheck = false;
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$17$NotificationsController(ArrayList popupArrayToRemove) {
        int size = popupArrayToRemove.size();
        for (int a = 0; a < size; a++) {
            this.popupMessages.remove(popupArrayToRemove.get(a));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$18$NotificationsController(int pushDialogsCount) {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    public void processLoadedUnreadMessages(LongSparseArray<Integer> dialogs, ArrayList<Message> messages, ArrayList<MessageObject> push, ArrayList<User> users, ArrayList<Chat> chats, ArrayList<EncryptedChat> encryptedChats) {
        MessagesController.getInstance(this.currentAccount).putUsers(users, true);
        MessagesController.getInstance(this.currentAccount).putChats(chats, true);
        MessagesController.getInstance(this.currentAccount).putEncryptedChats(encryptedChats, true);
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$10(this, messages, dialogs, push));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processLoadedUnreadMessages$21$NotificationsController(ArrayList messages, LongSparseArray dialogs, ArrayList push) {
        int a;
        long mid;
        MessageObject messageObject;
        long dialog_id;
        long original_dialog_id;
        int index;
        boolean value;
        int notifyOverride;
        Integer current;
        this.pushDialogs.clear();
        this.pushMessages.clear();
        this.pushMessagesDict.clear();
        this.total_unread_count = 0;
        this.personal_count = 0;
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        LongSparseArray<Boolean> settingsCache = new LongSparseArray();
        if (messages != null) {
            for (a = 0; a < messages.size(); a++) {
                Message message = (Message) messages.get(a);
                if (!(message != null && message.silent && ((message.action instanceof TL_messageActionContactSignUp) || (message.action instanceof TL_messageActionUserJoined)))) {
                    mid = (long) message.id;
                    if (message.to_id.channel_id != 0) {
                        mid |= ((long) message.to_id.channel_id) << 32;
                    }
                    if (this.pushMessagesDict.indexOfKey(mid) < 0) {
                        messageObject = new MessageObject(this.currentAccount, message, false);
                        if (isPersonalMessage(messageObject)) {
                            this.personal_count++;
                        }
                        dialog_id = messageObject.getDialogId();
                        original_dialog_id = dialog_id;
                        if (messageObject.messageOwner.mentioned) {
                            dialog_id = (long) messageObject.messageOwner.from_id;
                        }
                        index = settingsCache.indexOfKey(dialog_id);
                        if (index >= 0) {
                            value = ((Boolean) settingsCache.valueAt(index)).booleanValue();
                        } else {
                            notifyOverride = getNotifyOverride(preferences, dialog_id);
                            value = notifyOverride == -1 ? isGlobalNotificationsEnabled(dialog_id) : notifyOverride != 2;
                            settingsCache.put(dialog_id, Boolean.valueOf(value));
                        }
                        if (value && !(dialog_id == this.opened_dialog_id && ApplicationLoader.isScreenOn)) {
                            this.pushMessagesDict.put(mid, messageObject);
                            this.pushMessages.add(0, messageObject);
                            if (original_dialog_id != dialog_id) {
                                int i;
                                current = (Integer) this.pushDialogsOverrideMention.get(original_dialog_id);
                                LongSparseArray longSparseArray = this.pushDialogsOverrideMention;
                                if (current == null) {
                                    i = 1;
                                } else {
                                    i = current.intValue() + 1;
                                }
                                longSparseArray.put(original_dialog_id, Integer.valueOf(i));
                            }
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
                notifyOverride = getNotifyOverride(preferences, dialog_id);
                value = notifyOverride == -1 ? isGlobalNotificationsEnabled(dialog_id) : notifyOverride != 2;
                settingsCache.put(dialog_id, Boolean.valueOf(value));
            }
            if (value) {
                int count = ((Integer) dialogs.valueAt(a)).intValue();
                this.pushDialogs.put(dialog_id, Integer.valueOf(count));
                this.total_unread_count += count;
            }
        }
        if (push != null) {
            for (a = 0; a < push.size(); a++) {
                messageObject = (MessageObject) push.get(a);
                mid = (long) messageObject.getId();
                if (messageObject.messageOwner.to_id.channel_id != 0) {
                    mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                }
                if (this.pushMessagesDict.indexOfKey(mid) < 0) {
                    if (isPersonalMessage(messageObject)) {
                        this.personal_count++;
                    }
                    dialog_id = messageObject.getDialogId();
                    original_dialog_id = dialog_id;
                    long random_id = messageObject.messageOwner.random_id;
                    if (messageObject.messageOwner.mentioned) {
                        dialog_id = (long) messageObject.messageOwner.from_id;
                    }
                    index = settingsCache.indexOfKey(dialog_id);
                    if (index >= 0) {
                        value = ((Boolean) settingsCache.valueAt(index)).booleanValue();
                    } else {
                        notifyOverride = getNotifyOverride(preferences, dialog_id);
                        value = notifyOverride == -1 ? isGlobalNotificationsEnabled(dialog_id) : notifyOverride != 2;
                        settingsCache.put(dialog_id, Boolean.valueOf(value));
                    }
                    if (value && !(dialog_id == this.opened_dialog_id && ApplicationLoader.isScreenOn)) {
                        if (mid != 0) {
                            this.pushMessagesDict.put(mid, messageObject);
                        } else if (random_id != 0) {
                            this.fcmRandomMessagesDict.put(random_id, messageObject);
                        }
                        this.pushMessages.add(0, messageObject);
                        if (original_dialog_id != dialog_id) {
                            current = (Integer) this.pushDialogsOverrideMention.get(original_dialog_id);
                            this.pushDialogsOverrideMention.put(original_dialog_id, Integer.valueOf(current == null ? 1 : current.intValue() + 1));
                        }
                        Integer currentCount = (Integer) this.pushDialogs.get(dialog_id);
                        Integer newCount = Integer.valueOf(currentCount != null ? currentCount.intValue() + 1 : 1);
                        if (currentCount != null) {
                            this.total_unread_count -= currentCount.intValue();
                        }
                        this.total_unread_count += newCount.intValue();
                        this.pushDialogs.put(dialog_id, newCount);
                    }
                }
            }
        }
        AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$25(this, this.pushDialogs.size()));
        showOrUpdateNotification(SystemClock.elapsedRealtime() / 1000 < 60);
        if (this.showBadgeNumber) {
            setBadge(getTotalAllUnreadCount());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$20$NotificationsController(int pushDialogsCount) {
        if (this.total_unread_count == 0) {
            this.popupMessages.clear();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(this.currentAccount));
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsUnreadCounterChanged, Integer.valueOf(pushDialogsCount));
    }

    private int getTotalAllUnreadCount() {
        int count = 0;
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                NotificationsController controller = getInstance(a);
                if (controller.showBadgeNumber) {
                    int N;
                    int i;
                    if (controller.showBadgeMessages) {
                        if (controller.showBadgeMuted) {
                            try {
                                N = MessagesController.getInstance(a).dialogs.size();
                                for (i = 0; i < N; i++) {
                                    TL_dialog dialog = (TL_dialog) MessagesController.getInstance(a).dialogs.get(i);
                                    if (dialog.unread_count != 0) {
                                        count += dialog.unread_count;
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else {
                            count += controller.total_unread_count;
                        }
                    } else if (controller.showBadgeMuted) {
                        try {
                            N = MessagesController.getInstance(a).dialogs.size();
                            for (i = 0; i < N; i++) {
                                if (((TL_dialog) MessagesController.getInstance(a).dialogs.get(i)).unread_count != 0) {
                                    count++;
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    } else {
                        count += controller.pushDialogs.size();
                    }
                }
            }
        }
        return count;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateBadge$22$NotificationsController() {
        setBadge(getTotalAllUnreadCount());
    }

    public void updateBadge() {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$11(this));
    }

    private void setBadge(int count) {
        if (this.lastBadgeCount != count) {
            this.lastBadgeCount = count;
            NotificationBadge.applyCount(count);
        }
    }

    private String getShortStringForMessage(MessageObject messageObject, String[] userName, boolean[] preview) {
        if (AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
            return LocaleController.getString("YouHaveNewMessage", NUM);
        }
        int chat_id;
        long dialog_id = messageObject.messageOwner.dialog_id;
        if (messageObject.messageOwner.to_id.chat_id != 0) {
            chat_id = messageObject.messageOwner.to_id.chat_id;
        } else {
            chat_id = messageObject.messageOwner.to_id.channel_id;
        }
        int from_id = messageObject.messageOwner.to_id.user_id;
        if (preview != null) {
            preview[0] = true;
        }
        SharedPreferences preferences;
        if (messageObject.isFcmMessage()) {
            if (chat_id == 0 && from_id != 0) {
                if (!MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewAll", true)) {
                    if (preview != null) {
                        preview[0] = false;
                    }
                    return LocaleController.formatString("NotificationMessageNoText", NUM, messageObject.localName);
                } else if (VERSION.SDK_INT > 27) {
                    userName[0] = messageObject.localName;
                }
            } else if (chat_id != 0) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                if ((!messageObject.localChannel && !preferences.getBoolean("EnablePreviewGroup", true)) || (messageObject.localChannel && !preferences.getBoolean("EnablePreviewChannel", true))) {
                    if (preview != null) {
                        preview[0] = false;
                    }
                    if (messageObject.isMegagroup() || messageObject.messageOwner.to_id.channel_id == 0) {
                        return LocaleController.formatString("NotificationMessageGroupNoText", NUM, messageObject.localUserName, messageObject.localName);
                    }
                    return LocaleController.formatString("ChannelMessageNoText", NUM, messageObject.localName);
                } else if (messageObject.messageOwner.to_id.channel_id == 0 || messageObject.isMegagroup()) {
                    userName[0] = messageObject.localUserName;
                } else if (VERSION.SDK_INT > 27) {
                    userName[0] = messageObject.localName;
                }
            }
            return messageObject.messageOwner.message;
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
                if (chat_id != 0) {
                    userName[0] = name;
                } else if (VERSION.SDK_INT > 27) {
                    userName[0] = name;
                } else {
                    userName[0] = null;
                }
            }
        } else {
            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-from_id));
            if (chat != null) {
                name = chat.title;
                userName[0] = name;
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
            if (ChatObject.isChannel(chat) && !chat.megagroup && VERSION.SDK_INT <= 27) {
                userName[0] = null;
            }
        }
        if (((int) dialog_id) == 0) {
            userName[0] = null;
            return LocaleController.getString("YouHaveNewMessage", NUM);
        }
        preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        boolean isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
        if (!(chat_id == 0 && from_id != 0 && preferences.getBoolean("EnablePreviewAll", true)) && (chat_id == 0 || ((isChannel || !preferences.getBoolean("EnablePreviewGroup", true)) && !(isChannel && preferences.getBoolean("EnablePreviewChannel", true))))) {
            if (preview != null) {
                preview[0] = false;
            }
            return LocaleController.getString("Message", NUM);
        }
        if (messageObject.messageOwner instanceof TL_messageService) {
            userName[0] = null;
            if ((messageObject.messageOwner.action instanceof TL_messageActionUserJoined) || (messageObject.messageOwner.action instanceof TL_messageActionContactSignUp)) {
                return LocaleController.formatString("NotificationContactJoined", NUM, name);
            } else if (messageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                return LocaleController.formatString("NotificationContactNewPhoto", NUM, name);
            } else if (messageObject.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                r26 = new Object[2];
                r26[0] = LocaleController.getInstance().formatterYear.format(((long) messageObject.messageOwner.date) * 1000);
                r26[1] = LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000);
                String date = LocaleController.formatString("formatDateAtTime", NUM, r26);
                return LocaleController.formatString("NotificationUnrecognizedDevice", NUM, UserConfig.getInstance(this.currentAccount).getCurrentUser().first_name, date, messageObject.messageOwner.action.title, messageObject.messageOwner.action.address);
            } else if ((messageObject.messageOwner.action instanceof TL_messageActionGameScore) || (messageObject.messageOwner.action instanceof TL_messageActionPaymentSent)) {
                return messageObject.messageText.toString();
            } else {
                if (messageObject.messageOwner.action instanceof TL_messageActionPhoneCall) {
                    PhoneCallDiscardReason reason = messageObject.messageOwner.action.reason;
                    if (!messageObject.isOut() && (reason instanceof TL_phoneCallDiscardReasonMissed)) {
                        return LocaleController.getString("CallMessageIncomingMissed", NUM);
                    }
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatAddUser) {
                    int singleUserId = messageObject.messageOwner.action.user_id;
                    if (singleUserId == 0 && messageObject.messageOwner.action.users.size() == 1) {
                        singleUserId = ((Integer) messageObject.messageOwner.action.users.get(0)).intValue();
                    }
                    if (singleUserId == 0) {
                        StringBuilder names = new StringBuilder();
                        for (int a = 0; a < messageObject.messageOwner.action.users.size(); a++) {
                            user = MessagesController.getInstance(this.currentAccount).getUser((Integer) messageObject.messageOwner.action.users.get(a));
                            if (user != null) {
                                String name2 = UserObject.getUserName(user);
                                if (names.length() != 0) {
                                    names.append(", ");
                                }
                                names.append(name2);
                            }
                        }
                        return LocaleController.formatString("NotificationGroupAddMember", NUM, name, chat.title, names.toString());
                    } else if (messageObject.messageOwner.to_id.channel_id != 0 && !chat.megagroup) {
                        return LocaleController.formatString("ChannelAddedByNotification", NUM, name, chat.title);
                    } else if (singleUserId == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        return LocaleController.formatString("NotificationInvitedToGroup", NUM, name, chat.title);
                    } else {
                        User u2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(singleUserId));
                        if (u2 == null) {
                            return null;
                        }
                        if (from_id != u2.id) {
                            return LocaleController.formatString("NotificationGroupAddMember", NUM, name, chat.title, UserObject.getUserName(u2));
                        } else if (chat.megagroup) {
                            return LocaleController.formatString("NotificationGroupAddSelfMega", NUM, name, chat.title);
                        } else {
                            return LocaleController.formatString("NotificationGroupAddSelf", NUM, name, chat.title);
                        }
                    }
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatJoinedByLink) {
                    return LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, name, chat.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatEditTitle) {
                    return LocaleController.formatString("NotificationEditedGroupName", NUM, name, messageObject.messageOwner.action.title);
                } else if ((messageObject.messageOwner.action instanceof TL_messageActionChatEditPhoto) || (messageObject.messageOwner.action instanceof TL_messageActionChatDeletePhoto)) {
                    if (messageObject.messageOwner.to_id.channel_id == 0 || chat.megagroup) {
                        return LocaleController.formatString("NotificationEditedGroupPhoto", NUM, name, chat.title);
                    }
                    return LocaleController.formatString("ChannelPhotoEditNotification", NUM, chat.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatDeleteUser) {
                    if (messageObject.messageOwner.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        return LocaleController.formatString("NotificationGroupKickYou", NUM, name, chat.title);
                    } else if (messageObject.messageOwner.action.user_id == from_id) {
                        return LocaleController.formatString("NotificationGroupLeftMember", NUM, name, chat.title);
                    } else {
                        if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.action.user_id)) == null) {
                            return null;
                        }
                        return LocaleController.formatString("NotificationGroupKickMember", NUM, name, chat.title, UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.action.user_id))));
                    }
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatCreate) {
                    return messageObject.messageText.toString();
                } else {
                    if (messageObject.messageOwner.action instanceof TL_messageActionChannelCreate) {
                        return messageObject.messageText.toString();
                    }
                    if (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                        return LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, chat.title);
                    } else if (messageObject.messageOwner.action instanceof TL_messageActionChannelMigrateFrom) {
                        return LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, messageObject.messageOwner.action.title);
                    } else if (messageObject.messageOwner.action instanceof TL_messageActionScreenshotTaken) {
                        return messageObject.messageText.toString();
                    } else {
                        if (messageObject.messageOwner.action instanceof TL_messageActionPinMessage) {
                            MessageObject object;
                            String message;
                            CharSequence message2;
                            if (chat == null || (ChatObject.isChannel(chat) && !chat.megagroup)) {
                                if (messageObject.replyMessageObject == null) {
                                    return LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, chat.title);
                                }
                                object = messageObject.replyMessageObject;
                                if (object.isMusic()) {
                                    return LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, chat.title);
                                } else if (object.isVideo()) {
                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, chat.title);
                                    }
                                    message = "📹 " + object.messageOwner.message;
                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message);
                                } else if (object.isGif()) {
                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, chat.title);
                                    }
                                    message = "🎬 " + object.messageOwner.message;
                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message);
                                } else if (object.isVoice()) {
                                    return LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, chat.title);
                                } else if (object.isRoundVideo()) {
                                    return LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, chat.title);
                                } else if (object.isSticker()) {
                                    if (object.getStickerEmoji() != null) {
                                        return LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, chat.title, object.getStickerEmoji());
                                    }
                                    return LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaDocument) {
                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, chat.title);
                                    }
                                    message = "📎 " + object.messageOwner.message;
                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message);
                                } else if ((object.messageOwner.media instanceof TL_messageMediaGeo) || (object.messageOwner.media instanceof TL_messageMediaVenue)) {
                                    return LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                    return LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaContact) {
                                    return LocaleController.formatString("NotificationActionPinnedContactChannel", NUM, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaPoll) {
                                    return LocaleController.formatString("NotificationActionPinnedPollChannel", NUM, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaPhoto) {
                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, chat.title);
                                    }
                                    message = "🖼 " + object.messageOwner.message;
                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message);
                                } else if (object.messageOwner.media instanceof TL_messageMediaGame) {
                                    return LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, chat.title);
                                } else if (object.messageText == null || object.messageText.length() <= 0) {
                                    return LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, chat.title);
                                } else {
                                    message2 = object.messageText;
                                    if (message2.length() > 20) {
                                        message2 = message2.subSequence(0, 20) + "...";
                                    }
                                    return LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message2);
                                }
                            } else if (messageObject.replyMessageObject == null) {
                                return LocaleController.formatString("NotificationActionPinnedNoText", NUM, name, chat.title);
                            } else {
                                object = messageObject.replyMessageObject;
                                if (object.isMusic()) {
                                    return LocaleController.formatString("NotificationActionPinnedMusic", NUM, name, chat.title);
                                } else if (object.isVideo()) {
                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedVideo", NUM, name, chat.title);
                                    }
                                    message = "📹 " + object.messageOwner.message;
                                    return LocaleController.formatString("NotificationActionPinnedText", NUM, name, message, chat.title);
                                } else if (object.isGif()) {
                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedGif", NUM, name, chat.title);
                                    }
                                    message = "🎬 " + object.messageOwner.message;
                                    return LocaleController.formatString("NotificationActionPinnedText", NUM, name, message, chat.title);
                                } else if (object.isVoice()) {
                                    return LocaleController.formatString("NotificationActionPinnedVoice", NUM, name, chat.title);
                                } else if (object.isRoundVideo()) {
                                    return LocaleController.formatString("NotificationActionPinnedRound", NUM, name, chat.title);
                                } else if (object.isSticker()) {
                                    if (object.getStickerEmoji() != null) {
                                        return LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, name, chat.title, object.getStickerEmoji());
                                    }
                                    return LocaleController.formatString("NotificationActionPinnedSticker", NUM, name, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaDocument) {
                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedFile", NUM, name, chat.title);
                                    }
                                    message = "📎 " + object.messageOwner.message;
                                    return LocaleController.formatString("NotificationActionPinnedText", NUM, name, message, chat.title);
                                } else if ((object.messageOwner.media instanceof TL_messageMediaGeo) || (object.messageOwner.media instanceof TL_messageMediaVenue)) {
                                    return LocaleController.formatString("NotificationActionPinnedGeo", NUM, name, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                    return LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, name, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaContact) {
                                    return LocaleController.formatString("NotificationActionPinnedContact", NUM, name, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaPoll) {
                                    return LocaleController.formatString("NotificationActionPinnedPoll", NUM, name, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaPhoto) {
                                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                        return LocaleController.formatString("NotificationActionPinnedPhoto", NUM, name, chat.title);
                                    }
                                    message = "🖼 " + object.messageOwner.message;
                                    return LocaleController.formatString("NotificationActionPinnedText", NUM, name, message, chat.title);
                                } else if (object.messageOwner.media instanceof TL_messageMediaGame) {
                                    return LocaleController.formatString("NotificationActionPinnedGame", NUM, name, chat.title);
                                } else if (object.messageText == null || object.messageText.length() <= 0) {
                                    return LocaleController.formatString("NotificationActionPinnedNoText", NUM, name, chat.title);
                                } else {
                                    message2 = object.messageText;
                                    if (message2.length() > 20) {
                                        message2 = message2.subSequence(0, 20) + "...";
                                    }
                                    return LocaleController.formatString("NotificationActionPinnedText", NUM, name, message2, chat.title);
                                }
                            }
                        }
                    }
                }
            }
        } else if (messageObject.isMediaEmpty()) {
            if (TextUtils.isEmpty(messageObject.messageOwner.message)) {
                return LocaleController.getString("Message", NUM);
            }
            return messageObject.messageOwner.message;
        } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
            if (VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject.messageOwner.message)) {
                return "🖼 " + messageObject.messageOwner.message;
            }
            if (messageObject.messageOwner.media.ttl_seconds != 0) {
                return LocaleController.getString("AttachDestructingPhoto", NUM);
            }
            return LocaleController.getString("AttachPhoto", NUM);
        } else if (messageObject.isVideo()) {
            if (VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject.messageOwner.message)) {
                return "📹 " + messageObject.messageOwner.message;
            }
            if (messageObject.messageOwner.media.ttl_seconds != 0) {
                return LocaleController.getString("AttachDestructingVideo", NUM);
            }
            return LocaleController.getString("AttachVideo", NUM);
        } else if (messageObject.isGame()) {
            return LocaleController.getString("AttachGame", NUM);
        } else {
            if (messageObject.isVoice()) {
                return LocaleController.getString("AttachAudio", NUM);
            }
            if (messageObject.isRoundVideo()) {
                return LocaleController.getString("AttachRound", NUM);
            }
            if (messageObject.isMusic()) {
                return LocaleController.getString("AttachMusic", NUM);
            }
            if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                return LocaleController.getString("AttachContact", NUM);
            }
            if (messageObject.messageOwner.media instanceof TL_messageMediaPoll) {
                return LocaleController.getString("Poll", NUM);
            }
            if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                return LocaleController.getString("AttachLocation", NUM);
            }
            if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                return LocaleController.getString("AttachLiveLocation", NUM);
            }
            if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                if (messageObject.isSticker()) {
                    String emoji = messageObject.getStickerEmoji();
                    if (emoji != null) {
                        return emoji + " " + LocaleController.getString("AttachSticker", NUM);
                    }
                    return LocaleController.getString("AttachSticker", NUM);
                } else if (messageObject.isGif()) {
                    if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                        return LocaleController.getString("AttachGif", NUM);
                    }
                    return "🎬 " + messageObject.messageOwner.message;
                } else if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    return LocaleController.getString("AttachDocument", NUM);
                } else {
                    return "📎 " + messageObject.messageOwner.message;
                }
            }
        }
        return null;
    }

    private String getStringForMessage(MessageObject messageObject, boolean shortMessage, boolean[] text, boolean[] preview) {
        if (AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
            return LocaleController.getString("YouHaveNewMessage", NUM);
        }
        int chat_id;
        long dialog_id = messageObject.messageOwner.dialog_id;
        if (messageObject.messageOwner.to_id.chat_id != 0) {
            chat_id = messageObject.messageOwner.to_id.chat_id;
        } else {
            chat_id = messageObject.messageOwner.to_id.channel_id;
        }
        int from_id = messageObject.messageOwner.to_id.user_id;
        if (preview != null) {
            preview[0] = true;
        }
        SharedPreferences preferences;
        if (messageObject.isFcmMessage()) {
            if (chat_id == 0 && from_id != 0) {
                if (!MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewAll", true)) {
                    if (preview != null) {
                        preview[0] = false;
                    }
                    return LocaleController.formatString("NotificationMessageNoText", NUM, messageObject.localName);
                }
            } else if (chat_id != 0) {
                preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                if (!(messageObject.localChannel || preferences.getBoolean("EnablePreviewGroup", true)) || (messageObject.localChannel && !preferences.getBoolean("EnablePreviewChannel", true))) {
                    if (preview != null) {
                        preview[0] = false;
                    }
                    if (messageObject.isMegagroup() || messageObject.messageOwner.to_id.channel_id == 0) {
                        return LocaleController.formatString("NotificationMessageGroupNoText", NUM, messageObject.localUserName, messageObject.localName);
                    }
                    return LocaleController.formatString("ChannelMessageNoText", NUM, messageObject.localName);
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
            msg = LocaleController.getString("YouHaveNewMessage", NUM);
        } else if (chat_id == 0 && from_id != 0) {
            if (!MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewAll", true)) {
                if (preview != null) {
                    preview[0] = false;
                }
                msg = LocaleController.formatString("NotificationMessageNoText", NUM, name);
            } else if (messageObject.messageOwner instanceof TL_messageService) {
                if ((messageObject.messageOwner.action instanceof TL_messageActionUserJoined) || (messageObject.messageOwner.action instanceof TL_messageActionContactSignUp)) {
                    msg = LocaleController.formatString("NotificationContactJoined", NUM, name);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    msg = LocaleController.formatString("NotificationContactNewPhoto", NUM, name);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                    r26 = new Object[2];
                    r26[0] = LocaleController.getInstance().formatterYear.format(((long) messageObject.messageOwner.date) * 1000);
                    r26[1] = LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000);
                    String date = LocaleController.formatString("formatDateAtTime", NUM, r26);
                    msg = LocaleController.formatString("NotificationUnrecognizedDevice", NUM, UserConfig.getInstance(this.currentAccount).getCurrentUser().first_name, date, messageObject.messageOwner.action.title, messageObject.messageOwner.action.address);
                } else if ((messageObject.messageOwner.action instanceof TL_messageActionGameScore) || (messageObject.messageOwner.action instanceof TL_messageActionPaymentSent)) {
                    msg = messageObject.messageText.toString();
                } else if (messageObject.messageOwner.action instanceof TL_messageActionPhoneCall) {
                    PhoneCallDiscardReason reason = messageObject.messageOwner.action.reason;
                    if (!messageObject.isOut() && (reason instanceof TL_phoneCallDiscardReasonMissed)) {
                        msg = LocaleController.getString("CallMessageIncomingMissed", NUM);
                    }
                }
            } else if (messageObject.isMediaEmpty()) {
                if (shortMessage) {
                    msg = LocaleController.formatString("NotificationMessageNoText", NUM, name);
                } else if (TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("NotificationMessageNoText", NUM, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", NUM, name, messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = messageObject.messageOwner.media.ttl_seconds != 0 ? LocaleController.formatString("NotificationMessageSDPhoto", NUM, name) : LocaleController.formatString("NotificationMessagePhoto", NUM, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", NUM, name, "🖼 " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.isVideo()) {
                if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = messageObject.messageOwner.media.ttl_seconds != 0 ? LocaleController.formatString("NotificationMessageSDVideo", NUM, name) : LocaleController.formatString("NotificationMessageVideo", NUM, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", NUM, name, "📹 " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.isGame()) {
                msg = LocaleController.formatString("NotificationMessageGame", NUM, name, messageObject.messageOwner.media.game.title);
            } else if (messageObject.isVoice()) {
                msg = LocaleController.formatString("NotificationMessageAudio", NUM, name);
            } else if (messageObject.isRoundVideo()) {
                msg = LocaleController.formatString("NotificationMessageRound", NUM, name);
            } else if (messageObject.isMusic()) {
                msg = LocaleController.formatString("NotificationMessageMusic", NUM, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                msg = LocaleController.formatString("NotificationMessageContact", NUM, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaPoll) {
                msg = LocaleController.formatString("NotificationMessagePoll", NUM, name);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                msg = LocaleController.formatString("NotificationMessageMap", NUM, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                msg = LocaleController.formatString("NotificationMessageLiveLocation", NUM, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                if (messageObject.isSticker()) {
                    msg = messageObject.getStickerEmoji() != null ? LocaleController.formatString("NotificationMessageStickerEmoji", NUM, name, messageObject.getStickerEmoji()) : LocaleController.formatString("NotificationMessageSticker", NUM, name);
                } else if (messageObject.isGif()) {
                    if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                        msg = LocaleController.formatString("NotificationMessageGif", NUM, name);
                    } else {
                        msg = LocaleController.formatString("NotificationMessageText", NUM, name, "🎬 " + messageObject.messageOwner.message);
                        text[0] = true;
                    }
                } else if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("NotificationMessageDocument", NUM, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", NUM, name, "📎 " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            }
        } else if (chat_id != 0) {
            preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            boolean isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
            if ((isChannel || !preferences.getBoolean("EnablePreviewGroup", true)) && !(isChannel && preferences.getBoolean("EnablePreviewChannel", true))) {
                if (preview != null) {
                    preview[0] = false;
                }
                msg = (!ChatObject.isChannel(chat) || chat.megagroup) ? LocaleController.formatString("NotificationMessageGroupNoText", NUM, name, chat.title) : LocaleController.formatString("ChannelMessageNoText", NUM, name);
            } else if (messageObject.messageOwner instanceof TL_messageService) {
                if (messageObject.messageOwner.action instanceof TL_messageActionChatAddUser) {
                    int singleUserId = messageObject.messageOwner.action.user_id;
                    if (singleUserId == 0 && messageObject.messageOwner.action.users.size() == 1) {
                        singleUserId = ((Integer) messageObject.messageOwner.action.users.get(0)).intValue();
                    }
                    if (singleUserId == 0) {
                        StringBuilder names = new StringBuilder();
                        for (int a = 0; a < messageObject.messageOwner.action.users.size(); a++) {
                            user = MessagesController.getInstance(this.currentAccount).getUser((Integer) messageObject.messageOwner.action.users.get(a));
                            if (user != null) {
                                String name2 = UserObject.getUserName(user);
                                if (names.length() != 0) {
                                    names.append(", ");
                                }
                                names.append(name2);
                            }
                        }
                        msg = LocaleController.formatString("NotificationGroupAddMember", NUM, name, chat.title, names.toString());
                    } else if (messageObject.messageOwner.to_id.channel_id != 0 && !chat.megagroup) {
                        msg = LocaleController.formatString("ChannelAddedByNotification", NUM, name, chat.title);
                    } else if (singleUserId == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        msg = LocaleController.formatString("NotificationInvitedToGroup", NUM, name, chat.title);
                    } else {
                        User u2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(singleUserId));
                        if (u2 == null) {
                            return null;
                        }
                        msg = from_id == u2.id ? chat.megagroup ? LocaleController.formatString("NotificationGroupAddSelfMega", NUM, name, chat.title) : LocaleController.formatString("NotificationGroupAddSelf", NUM, name, chat.title) : LocaleController.formatString("NotificationGroupAddMember", NUM, name, chat.title, UserObject.getUserName(u2));
                    }
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatJoinedByLink) {
                    msg = LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, name, chat.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatEditTitle) {
                    msg = LocaleController.formatString("NotificationEditedGroupName", NUM, name, messageObject.messageOwner.action.title);
                } else if ((messageObject.messageOwner.action instanceof TL_messageActionChatEditPhoto) || (messageObject.messageOwner.action instanceof TL_messageActionChatDeletePhoto)) {
                    msg = (messageObject.messageOwner.to_id.channel_id == 0 || chat.megagroup) ? LocaleController.formatString("NotificationEditedGroupPhoto", NUM, name, chat.title) : LocaleController.formatString("ChannelPhotoEditNotification", NUM, chat.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatDeleteUser) {
                    if (messageObject.messageOwner.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        msg = LocaleController.formatString("NotificationGroupKickYou", NUM, name, chat.title);
                    } else if (messageObject.messageOwner.action.user_id == from_id) {
                        msg = LocaleController.formatString("NotificationGroupLeftMember", NUM, name, chat.title);
                    } else {
                        if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.action.user_id)) == null) {
                            return null;
                        }
                        msg = LocaleController.formatString("NotificationGroupKickMember", NUM, name, chat.title, UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.action.user_id))));
                    }
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatCreate) {
                    msg = messageObject.messageText.toString();
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChannelCreate) {
                    msg = messageObject.messageText.toString();
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                    msg = LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, chat.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionChannelMigrateFrom) {
                    msg = LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, messageObject.messageOwner.action.title);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionScreenshotTaken) {
                    msg = messageObject.messageText.toString();
                } else if (messageObject.messageOwner.action instanceof TL_messageActionPinMessage) {
                    MessageObject object;
                    String message;
                    CharSequence message2;
                    if (chat == null || (ChatObject.isChannel(chat) && !chat.megagroup)) {
                        if (messageObject.replyMessageObject == null) {
                            msg = LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, chat.title);
                        } else {
                            object = messageObject.replyMessageObject;
                            if (object.isMusic()) {
                                msg = LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, chat.title);
                            } else if (object.isVideo()) {
                                if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                    msg = LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, chat.title);
                                } else {
                                    message = "📹 " + object.messageOwner.message;
                                    msg = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message);
                                }
                            } else if (object.isGif()) {
                                if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                    msg = LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, chat.title);
                                } else {
                                    message = "🎬 " + object.messageOwner.message;
                                    msg = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message);
                                }
                            } else if (object.isVoice()) {
                                msg = LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, chat.title);
                            } else if (object.isRoundVideo()) {
                                msg = LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, chat.title);
                            } else if (object.isSticker()) {
                                msg = object.getStickerEmoji() != null ? LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, chat.title, object.getStickerEmoji()) : LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, chat.title);
                            } else if (object.messageOwner.media instanceof TL_messageMediaDocument) {
                                if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                    msg = LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, chat.title);
                                } else {
                                    message = "📎 " + object.messageOwner.message;
                                    msg = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message);
                                }
                            } else if ((object.messageOwner.media instanceof TL_messageMediaGeo) || (object.messageOwner.media instanceof TL_messageMediaVenue)) {
                                msg = LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, chat.title);
                            } else if (object.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                msg = LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, chat.title);
                            } else if (object.messageOwner.media instanceof TL_messageMediaContact) {
                                msg = LocaleController.formatString("NotificationActionPinnedContactChannel", NUM, chat.title);
                            } else if (object.messageOwner.media instanceof TL_messageMediaPoll) {
                                msg = LocaleController.formatString("NotificationActionPinnedPollChannel", NUM, chat.title);
                            } else if (object.messageOwner.media instanceof TL_messageMediaPhoto) {
                                if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                    msg = LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, chat.title);
                                } else {
                                    message = "🖼 " + object.messageOwner.message;
                                    msg = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message);
                                }
                            } else if (object.messageOwner.media instanceof TL_messageMediaGame) {
                                msg = LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, chat.title);
                            } else if (object.messageText == null || object.messageText.length() <= 0) {
                                msg = LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, chat.title);
                            } else {
                                message2 = object.messageText;
                                if (message2.length() > 20) {
                                    message2 = message2.subSequence(0, 20) + "...";
                                }
                                msg = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, chat.title, message2);
                            }
                        }
                    } else if (messageObject.replyMessageObject == null) {
                        msg = LocaleController.formatString("NotificationActionPinnedNoText", NUM, name, chat.title);
                    } else {
                        object = messageObject.replyMessageObject;
                        if (object.isMusic()) {
                            msg = LocaleController.formatString("NotificationActionPinnedMusic", NUM, name, chat.title);
                        } else if (object.isVideo()) {
                            if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                msg = LocaleController.formatString("NotificationActionPinnedVideo", NUM, name, chat.title);
                            } else {
                                message = "📹 " + object.messageOwner.message;
                                msg = LocaleController.formatString("NotificationActionPinnedText", NUM, name, message, chat.title);
                            }
                        } else if (object.isGif()) {
                            if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                msg = LocaleController.formatString("NotificationActionPinnedGif", NUM, name, chat.title);
                            } else {
                                message = "🎬 " + object.messageOwner.message;
                                msg = LocaleController.formatString("NotificationActionPinnedText", NUM, name, message, chat.title);
                            }
                        } else if (object.isVoice()) {
                            msg = LocaleController.formatString("NotificationActionPinnedVoice", NUM, name, chat.title);
                        } else if (object.isRoundVideo()) {
                            msg = LocaleController.formatString("NotificationActionPinnedRound", NUM, name, chat.title);
                        } else if (object.isSticker()) {
                            msg = object.getStickerEmoji() != null ? LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, name, chat.title, object.getStickerEmoji()) : LocaleController.formatString("NotificationActionPinnedSticker", NUM, name, chat.title);
                        } else if (object.messageOwner.media instanceof TL_messageMediaDocument) {
                            if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                msg = LocaleController.formatString("NotificationActionPinnedFile", NUM, name, chat.title);
                            } else {
                                message = "📎 " + object.messageOwner.message;
                                msg = LocaleController.formatString("NotificationActionPinnedText", NUM, name, message, chat.title);
                            }
                        } else if ((object.messageOwner.media instanceof TL_messageMediaGeo) || (object.messageOwner.media instanceof TL_messageMediaVenue)) {
                            msg = LocaleController.formatString("NotificationActionPinnedGeo", NUM, name, chat.title);
                        } else if (object.messageOwner.media instanceof TL_messageMediaGeoLive) {
                            msg = LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, name, chat.title);
                        } else if (object.messageOwner.media instanceof TL_messageMediaContact) {
                            msg = LocaleController.formatString("NotificationActionPinnedContact", NUM, name, chat.title);
                        } else if (object.messageOwner.media instanceof TL_messageMediaPoll) {
                            msg = LocaleController.formatString("NotificationActionPinnedPoll", NUM, name, chat.title);
                        } else if (object.messageOwner.media instanceof TL_messageMediaPhoto) {
                            if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                msg = LocaleController.formatString("NotificationActionPinnedPhoto", NUM, name, chat.title);
                            } else {
                                message = "🖼 " + object.messageOwner.message;
                                msg = LocaleController.formatString("NotificationActionPinnedText", NUM, name, message, chat.title);
                            }
                        } else if (object.messageOwner.media instanceof TL_messageMediaGame) {
                            msg = LocaleController.formatString("NotificationActionPinnedGame", NUM, name, chat.title);
                        } else if (object.messageText == null || object.messageText.length() <= 0) {
                            msg = LocaleController.formatString("NotificationActionPinnedNoText", NUM, name, chat.title);
                        } else {
                            message2 = object.messageText;
                            if (message2.length() > 20) {
                                message2 = message2.subSequence(0, 20) + "...";
                            }
                            msg = LocaleController.formatString("NotificationActionPinnedText", NUM, name, message2, chat.title);
                        }
                    }
                } else if (messageObject.messageOwner.action instanceof TL_messageActionGameScore) {
                    msg = messageObject.messageText.toString();
                }
            } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                if (messageObject.isMediaEmpty()) {
                    msg = (shortMessage || messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) ? LocaleController.formatString("NotificationMessageGroupNoText", NUM, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", NUM, name, chat.title, messageObject.messageOwner.message);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                    msg = (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) ? LocaleController.formatString("NotificationMessageGroupPhoto", NUM, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", NUM, name, chat.title, "🖼 " + messageObject.messageOwner.message);
                } else if (messageObject.isVideo()) {
                    msg = (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) ? LocaleController.formatString(" ", NUM, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", NUM, name, chat.title, "📹 " + messageObject.messageOwner.message);
                } else if (messageObject.isVoice()) {
                    msg = LocaleController.formatString("NotificationMessageGroupAudio", NUM, name, chat.title);
                } else if (messageObject.isRoundVideo()) {
                    msg = LocaleController.formatString("NotificationMessageGroupRound", NUM, name, chat.title);
                } else if (messageObject.isMusic()) {
                    msg = LocaleController.formatString("NotificationMessageGroupMusic", NUM, name, chat.title);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                    msg = LocaleController.formatString("NotificationMessageGroupContact", NUM, name, chat.title);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaPoll) {
                    msg = LocaleController.formatString("NotificationMessageGroupPoll", NUM, name, chat.title);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                    msg = LocaleController.formatString("NotificationMessageGroupGame", NUM, name, chat.title, messageObject.messageOwner.media.game.title);
                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                    msg = LocaleController.formatString("NotificationMessageGroupMap", NUM, name, chat.title);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                    msg = LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, name, chat.title);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                    if (messageObject.isSticker()) {
                        msg = messageObject.getStickerEmoji() != null ? LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, name, chat.title, messageObject.getStickerEmoji()) : LocaleController.formatString("NotificationMessageGroupSticker", NUM, name, chat.title);
                    } else {
                        msg = messageObject.isGif() ? (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) ? LocaleController.formatString("NotificationMessageGroupGif", NUM, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", NUM, name, chat.title, "🎬 " + messageObject.messageOwner.message) : (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) ? LocaleController.formatString("NotificationMessageGroupDocument", NUM, name, chat.title) : LocaleController.formatString("NotificationMessageGroupText", NUM, name, chat.title, "📎 " + messageObject.messageOwner.message);
                    }
                }
            } else if (messageObject.isMediaEmpty()) {
                if (shortMessage || messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) {
                    msg = LocaleController.formatString("ChannelMessageNoText", NUM, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", NUM, name, messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("ChannelMessagePhoto", NUM, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", NUM, name, "🖼 " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.isVideo()) {
                if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("ChannelMessageVideo", NUM, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", NUM, name, "📹 " + messageObject.messageOwner.message);
                    text[0] = true;
                }
            } else if (messageObject.isVoice()) {
                msg = LocaleController.formatString("ChannelMessageAudio", NUM, name);
            } else if (messageObject.isRoundVideo()) {
                msg = LocaleController.formatString("ChannelMessageRound", NUM, name);
            } else if (messageObject.isMusic()) {
                msg = LocaleController.formatString("ChannelMessageMusic", NUM, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                msg = LocaleController.formatString("ChannelMessageContact", NUM, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaPoll) {
                msg = LocaleController.formatString("ChannelMessagePoll", NUM, name);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                msg = LocaleController.formatString("ChannelMessageMap", NUM, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                msg = LocaleController.formatString("ChannelMessageLiveLocation", NUM, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                if (messageObject.isSticker()) {
                    msg = messageObject.getStickerEmoji() != null ? LocaleController.formatString("ChannelMessageStickerEmoji", NUM, name, messageObject.getStickerEmoji()) : LocaleController.formatString("ChannelMessageSticker", NUM, name);
                } else if (messageObject.isGif()) {
                    if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                        msg = LocaleController.formatString("ChannelMessageGIF", NUM, name);
                    } else {
                        msg = LocaleController.formatString("NotificationMessageText", NUM, name, "🎬 " + messageObject.messageOwner.message);
                        text[0] = true;
                    }
                } else if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("ChannelMessageDocument", NUM, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", NUM, name, "📎 " + messageObject.messageOwner.message);
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
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private boolean isPersonalMessage(MessageObject messageObject) {
        return messageObject.messageOwner.to_id != null && messageObject.messageOwner.to_id.chat_id == 0 && messageObject.messageOwner.to_id.channel_id == 0 && (messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageActionEmpty));
    }

    private int getNotifyOverride(SharedPreferences preferences, long dialog_id) {
        int notifyOverride = preferences.getInt("notify2_" + dialog_id, -1);
        if (notifyOverride != 3 || preferences.getInt("notifyuntil_" + dialog_id, 0) < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
            return notifyOverride;
        }
        return 2;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$showNotifications$23$NotificationsController() {
        showOrUpdateNotification(false);
    }

    public void showNotifications() {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$12(this));
    }

    public void hideNotifications() {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$13(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$hideNotifications$24$NotificationsController() {
        notificationManager.cancel(this.notificationId);
        this.lastWearNotifiedMessageId.clear();
        for (int a = 0; a < this.wearNotificationsIds.size(); a++) {
            notificationManager.cancel(((Integer) this.wearNotificationsIds.valueAt(a)).intValue());
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
                notificationManager.cancel(((Integer) this.wearNotificationsIds.valueAt(a)).intValue());
            }
            this.wearNotificationsIds.clear();
            AndroidUtilities.runOnUIThread(NotificationsController$$Lambda$14.$instance);
            if (WearDataLayerListenerService.isWatchConnected()) {
                try {
                    JSONObject o = new JSONObject();
                    o.put("id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                    o.put("cancel_all", true);
                    WearDataLayerListenerService.sendMessageToWatch("/notify", o.toString().getBytes("UTF-8"), "remote_notifications");
                } catch (JSONException e) {
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    private void playInChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                if (getNotifyOverride(MessagesController.getNotificationsSettings(this.currentAccount), this.opened_dialog_id) != 2) {
                    notificationsQueue.postRunnable(new NotificationsController$$Lambda$15(this));
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$playInChatSound$27$NotificationsController() {
        if (Math.abs(System.currentTimeMillis() - this.lastSoundPlay) > 500) {
            try {
                if (this.soundPool == null) {
                    this.soundPool = new SoundPool(3, 1, 0);
                    this.soundPool.setOnLoadCompleteListener(NotificationsController$$Lambda$24.$instance);
                }
                if (this.soundIn == 0 && !this.soundInLoaded) {
                    this.soundInLoaded = true;
                    this.soundIn = this.soundPool.load(ApplicationLoader.applicationContext, NUM, 1);
                }
                if (this.soundIn != 0) {
                    try {
                        this.soundPool.play(this.soundIn, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    static final /* synthetic */ void lambda$null$26$NotificationsController(SoundPool soundPool, int sampleId, int status) {
        if (status == 0) {
            try {
                soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
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
            FileLog.e(e);
            showOrUpdateNotification(this.notifyCheck);
        }
    }

    /* Access modifiers changed, original: protected */
    public void repeatNotificationMaybe() {
        notificationsQueue.postRunnable(new NotificationsController$$Lambda$16(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$repeatNotificationMaybe$28$NotificationsController() {
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
            Builder builder = new Builder();
            builder.setContentType(4);
            builder.setUsage(5);
            if (sound != null) {
                notificationChannel.setSound(sound, builder.build());
            } else {
                notificationChannel.setSound(null, builder.build());
            }
            systemNotificationManager.createNotificationChannel(notificationChannel);
            preferences.edit().putString(key, channelId).putString(key + "_s", newSettingsHash).commit();
        }
        return channelId;
    }

    private void showOrUpdateNotification(boolean notifyAboutLast) {
        if (UserConfig.getInstance(this.currentAccount).isClientActivated() && !this.pushMessages.isEmpty() && (SharedConfig.showNotificationsForAllAccounts || this.currentAccount == UserConfig.selectedAccount)) {
            try {
                ConnectionsManager.getInstance(this.currentAccount).resumeNetworkMaybe();
                MessageObject lastMessageObject = (MessageObject) this.pushMessages.get(0);
                SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
                int dismissDate = preferences.getInt("dismissDate", 0);
                if (lastMessageObject.messageOwner.date <= dismissDate) {
                    dismissNotification();
                    return;
                }
                int count;
                int vibrateOverride;
                int priorityOverride;
                String choosenSoundPath;
                String chatName;
                String name;
                String detailText;
                long dialog_id = lastMessageObject.getDialogId();
                boolean isChannel = false;
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
                    isChannel = ChatObject.isChannel(chat) && !chat.megagroup;
                }
                TLObject photoPath = null;
                boolean notifyDisabled = false;
                int needVibrate = 0;
                int ledColor = -16776961;
                int priority = 0;
                int notifyOverride = getNotifyOverride(preferences, override_dialog_id);
                boolean value;
                if (notifyOverride == -1) {
                    value = isGlobalNotificationsEnabled(dialog_id);
                } else {
                    value = notifyOverride != 2;
                }
                if (!(notifyAboutLast && value)) {
                    notifyDisabled = true;
                }
                if (!(notifyDisabled || dialog_id != override_dialog_id || chat == null)) {
                    int notifyMaxCount;
                    int notifyDelay;
                    if (preferences.getBoolean("custom_" + dialog_id, false)) {
                        notifyMaxCount = preferences.getInt("smart_max_count_" + dialog_id, 2);
                        notifyDelay = preferences.getInt("smart_delay_" + dialog_id, 180);
                    } else {
                        notifyMaxCount = 2;
                        notifyDelay = 180;
                    }
                    if (notifyMaxCount != 0) {
                        Point dialogInfo = (Point) this.smartNotificationsDialogs.get(dialog_id);
                        if (dialogInfo == null) {
                            this.smartNotificationsDialogs.put(dialog_id, new Point(1, (int) (System.currentTimeMillis() / 1000)));
                        } else if (((long) (dialogInfo.y + notifyDelay)) < System.currentTimeMillis() / 1000) {
                            dialogInfo.set(1, (int) (System.currentTimeMillis() / 1000));
                        } else {
                            count = dialogInfo.x;
                            if (count < notifyMaxCount) {
                                dialogInfo.set(count + 1, (int) (System.currentTimeMillis() / 1000));
                            } else {
                                notifyDisabled = true;
                            }
                        }
                    }
                }
                String defaultPath = System.DEFAULT_NOTIFICATION_URI.getPath();
                boolean inAppSounds = preferences.getBoolean("EnableInAppSounds", true);
                boolean inAppVibrate = preferences.getBoolean("EnableInAppVibrate", true);
                boolean inAppPreview = preferences.getBoolean("EnableInAppPreview", true);
                boolean inAppPriority = preferences.getBoolean("EnableInAppPriority", false);
                boolean custom = preferences.getBoolean("custom_" + dialog_id, false);
                if (custom) {
                    vibrateOverride = preferences.getInt("vibrate_" + dialog_id, 0);
                    priorityOverride = preferences.getInt("priority_" + dialog_id, 3);
                    choosenSoundPath = preferences.getString("sound_path_" + dialog_id, null);
                } else {
                    vibrateOverride = 0;
                    priorityOverride = 3;
                    choosenSoundPath = null;
                }
                boolean vibrateOnlyIfSilent = false;
                if (chat_id != 0) {
                    if (isChannel) {
                        if (choosenSoundPath != null && choosenSoundPath.equals(defaultPath)) {
                            choosenSoundPath = null;
                        } else if (choosenSoundPath == null) {
                            choosenSoundPath = preferences.getString("ChannelSoundPath", defaultPath);
                        }
                        needVibrate = preferences.getInt("vibrate_channel", 0);
                        priority = preferences.getInt("priority_channel", 1);
                        ledColor = preferences.getInt("ChannelLed", -16776961);
                    } else {
                        if (choosenSoundPath != null && choosenSoundPath.equals(defaultPath)) {
                            choosenSoundPath = null;
                        } else if (choosenSoundPath == null) {
                            choosenSoundPath = preferences.getString("GroupSoundPath", defaultPath);
                        }
                        needVibrate = preferences.getInt("vibrate_group", 0);
                        priority = preferences.getInt("priority_group", 1);
                        ledColor = preferences.getInt("GroupLed", -16776961);
                    }
                } else if (user_id != 0) {
                    if (choosenSoundPath != null && choosenSoundPath.equals(defaultPath)) {
                        choosenSoundPath = null;
                    } else if (choosenSoundPath == null) {
                        choosenSoundPath = preferences.getString("GlobalSoundPath", defaultPath);
                    }
                    needVibrate = preferences.getInt("vibrate_messages", 0);
                    priority = preferences.getInt("priority_messages", 1);
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
                    try {
                        int mode = audioManager.getRingerMode();
                        if (!(mode == 0 || mode == 1)) {
                            needVibrate = 2;
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                Uri configSound = null;
                long[] configVibrationPattern = null;
                int configImportance = 0;
                if (VERSION.SDK_INT >= 26) {
                    if (needVibrate == 2) {
                        configVibrationPattern = new long[]{0, 0};
                    } else if (needVibrate == 1) {
                        configVibrationPattern = new long[]{0, 100, 0, 100};
                    } else if (needVibrate == 0 || needVibrate == 4) {
                        configVibrationPattern = new long[0];
                    } else if (needVibrate == 3) {
                        configVibrationPattern = new long[]{0, 1000};
                    }
                    if (choosenSoundPath != null) {
                        if (!choosenSoundPath.equals("NoSound")) {
                            configSound = choosenSoundPath.equals(defaultPath) ? System.DEFAULT_NOTIFICATION_URI : Uri.parse(choosenSoundPath);
                        }
                    }
                    if (priority == 0) {
                        configImportance = 3;
                    } else if (priority == 1 || priority == 2) {
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
                Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
                intent.setFlags(32768);
                if (((int) dialog_id) != 0) {
                    if (this.pushDialogs.size() == 1) {
                        if (chat_id != 0) {
                            intent.putExtra("chatId", chat_id);
                        } else if (user_id != 0) {
                            intent.putExtra("userId", user_id);
                        }
                    }
                    if (AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
                        photoPath = null;
                    } else if (this.pushDialogs.size() == 1 && VERSION.SDK_INT < 28) {
                        if (chat != null) {
                            if (!(chat.photo == null || chat.photo.photo_small == null || chat.photo.photo_small.volume_id == 0 || chat.photo.photo_small.local_id == 0)) {
                                photoPath = chat.photo.photo_small;
                            }
                        } else if (!(user == null || user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0)) {
                            photoPath = user.photo.photo_small;
                        }
                    }
                } else if (this.pushDialogs.size() == 1 && dialog_id != globalSecretChatId) {
                    intent.putExtra("encId", (int) (dialog_id >> 32));
                }
                intent.putExtra("currentAccount", this.currentAccount);
                PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM);
                boolean replace = true;
                if (((chat_id != 0 && chat == null) || user == null) && lastMessageObject.isFcmMessage()) {
                    chatName = lastMessageObject.localName;
                } else if (chat != null) {
                    chatName = chat.title;
                } else {
                    chatName = UserObject.getUserName(user);
                }
                if (((int) dialog_id) == 0 || this.pushDialogs.size() > 1 || AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
                    name = LocaleController.getString("AppName", NUM);
                    replace = false;
                } else {
                    name = chatName;
                }
                if (UserConfig.getActivatedAccountsCount() <= 1) {
                    detailText = "";
                } else if (this.pushDialogs.size() == 1) {
                    detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser());
                } else {
                    detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser()) + "・";
                }
                if (this.pushDialogs.size() != 1 || VERSION.SDK_INT < 23) {
                    if (this.pushDialogs.size() == 1) {
                        detailText = detailText + LocaleController.formatPluralString("NewMessages", this.total_unread_count);
                    } else {
                        detailText = detailText + LocaleController.formatString("NotificationMessagesPeopleDisplayOrder", NUM, LocaleController.formatPluralString("NewMessages", this.total_unread_count), LocaleController.formatPluralString("FromChats", this.pushDialogs.size()));
                    }
                }
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(NUM).setAutoCancel(true).setNumber(this.total_unread_count).setContentIntent(contentIntent).setGroup(this.notificationGroup).setGroupSummary(true).setShowWhen(true).setWhen(((long) lastMessageObject.messageOwner.date) * 1000).setColor(-15618822);
                long[] vibrationPattern = null;
                int importance = 0;
                Uri sound = null;
                mBuilder.setCategory("msg");
                if (chat == null && user != null && user.phone != null && user.phone.length() > 0) {
                    mBuilder.addPerson("tel:+" + user.phone);
                }
                int silent = 2;
                String lastMessage = null;
                MessageObject messageObject;
                boolean[] text;
                String message;
                if (this.pushMessages.size() == 1) {
                    messageObject = (MessageObject) this.pushMessages.get(0);
                    text = new boolean[1];
                    lastMessage = getStringForMessage(messageObject, false, text, null);
                    message = lastMessage;
                    silent = messageObject.messageOwner.silent ? 1 : 0;
                    if (message != null) {
                        if (replace) {
                            if (chat != null) {
                                message = message.replace(" @ " + name, "");
                            } else if (text[0]) {
                                message = message.replace(name + ": ", "");
                            } else {
                                message = message.replace(name + " ", "");
                            }
                        }
                        mBuilder.setContentText(message);
                        mBuilder.setStyle(new BigTextStyle().bigText(message));
                    } else {
                        return;
                    }
                }
                mBuilder.setContentText(detailText);
                Style inboxStyle = new InboxStyle();
                inboxStyle.setBigContentTitle(name);
                count = Math.min(10, this.pushMessages.size());
                text = new boolean[1];
                for (int i = 0; i < count; i++) {
                    messageObject = (MessageObject) this.pushMessages.get(i);
                    message = getStringForMessage(messageObject, false, text, null);
                    if (message != null && messageObject.messageOwner.date > dismissDate) {
                        if (silent == 2) {
                            lastMessage = message;
                            silent = messageObject.messageOwner.silent ? 1 : 0;
                        }
                        if (this.pushDialogs.size() == 1 && replace) {
                            message = chat != null ? message.replace(" @ " + name, "") : text[0] ? message.replace(name + ": ", "") : message.replace(name + " ", "");
                        }
                        inboxStyle.addLine(message);
                    }
                }
                inboxStyle.setSummaryText(detailText);
                mBuilder.setStyle(inboxStyle);
                intent = new Intent(ApplicationLoader.applicationContext, NotificationDismissReceiver.class);
                intent.putExtra("messageDate", lastMessageObject.messageOwner.date);
                intent.putExtra("currentAccount", this.currentAccount);
                mBuilder.setDeleteIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 1, intent, NUM));
                if (photoPath != null) {
                    BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                    if (img != null) {
                        mBuilder.setLargeIcon(img.getBitmap());
                    } else {
                        try {
                            File file = FileLoader.getPathToAttach(photoPath, true);
                            if (file.exists()) {
                                int i2;
                                float scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                                Options options = new Options();
                                if (scaleFactor < 1.0f) {
                                    i2 = 1;
                                } else {
                                    i2 = (int) scaleFactor;
                                }
                                options.inSampleSize = i2;
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                                if (bitmap != null) {
                                    mBuilder.setLargeIcon(bitmap);
                                }
                            }
                        } catch (Throwable th) {
                        }
                    }
                }
                if (!notifyAboutLast || silent == 1) {
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
                if (silent == 1 || notifyDisabled) {
                    vibrationPattern = new long[]{0, 0};
                    mBuilder.setVibrate(vibrationPattern);
                } else {
                    if (ApplicationLoader.mainInterfacePaused || inAppPreview) {
                        if (lastMessage.length() > 100) {
                            lastMessage = lastMessage.substring(0, 100).replace(10, ' ').trim() + "...";
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
                boolean hasCallback = false;
                if (!(AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter || lastMessageObject.getDialogId() != 777000 || lastMessageObject.messageOwner.reply_markup == null)) {
                    ArrayList<TL_keyboardButtonRow> rows = lastMessageObject.messageOwner.reply_markup.rows;
                    int size = rows.size();
                    for (int a = 0; a < size; a++) {
                        TL_keyboardButtonRow row = (TL_keyboardButtonRow) rows.get(a);
                        int size2 = row.buttons.size();
                        for (int b = 0; b < size2; b++) {
                            KeyboardButton button = (KeyboardButton) row.buttons.get(b);
                            if (button instanceof TL_keyboardButtonCallback) {
                                intent = new Intent(ApplicationLoader.applicationContext, NotificationCallbackReceiver.class);
                                intent.putExtra("currentAccount", this.currentAccount);
                                intent.putExtra("did", dialog_id);
                                if (button.data != null) {
                                    intent.putExtra("data", button.data);
                                }
                                intent.putExtra("mid", lastMessageObject.getId());
                                String str = button.text;
                                Context context = ApplicationLoader.applicationContext;
                                int i3 = this.lastButtonId;
                                this.lastButtonId = i3 + 1;
                                mBuilder.addAction(0, str, PendingIntent.getBroadcast(context, i3, intent, NUM));
                                hasCallback = true;
                            }
                        }
                    }
                }
                if (!hasCallback && VERSION.SDK_INT < 24 && SharedConfig.passcodeHash.length() == 0 && hasMessagesToReply()) {
                    intent = new Intent(ApplicationLoader.applicationContext, PopupReplyReceiver.class);
                    intent.putExtra("currentAccount", this.currentAccount);
                    if (VERSION.SDK_INT <= 19) {
                        mBuilder.addAction(NUM, LocaleController.getString("Reply", NUM), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, NUM));
                    } else {
                        mBuilder.addAction(NUM, LocaleController.getString("Reply", NUM), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, NUM));
                    }
                }
                if (VERSION.SDK_INT >= 26) {
                    mBuilder.setChannelId(validateChannelId(dialog_id, chatName, vibrationPattern, ledColor, sound, importance, configVibrationPattern, configSound, configImportance));
                }
                showExtraNotifications(mBuilder, notifyAboutLast, detailText);
                scheduleNotificationRepeat();
                return;
            } catch (Exception e2) {
                FileLog.e(e2);
                return;
            }
        }
        dismissNotification();
    }

    @SuppressLint({"InlinedApi"})
    private void showExtraNotifications(NotificationCompat.Builder notificationBuilder, boolean notifyAboutLast, String summary) {
        Notification mainNotification = notificationBuilder.build();
        if (VERSION.SDK_INT < 18) {
            notificationManager.notify(this.notificationId, mainNotification);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("show summary notification by SDK check");
                return;
            }
            return;
        }
        int a;
        MessageObject messageObject;
        long dialog_id;
        ArrayList<Long> sortedDialogs = new ArrayList();
        LongSparseArray<ArrayList<MessageObject>> messagesByDialogs = new LongSparseArray();
        for (a = 0; a < this.pushMessages.size(); a++) {
            messageObject = (MessageObject) this.pushMessages.get(a);
            dialog_id = messageObject.getDialogId();
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
        boolean useSummaryNotification = VERSION.SDK_INT <= 27 || (VERSION.SDK_INT > 27 && sortedDialogs.size() > 1);
        if (useSummaryNotification && VERSION.SDK_INT >= 26) {
            checkOtherNotificationsChannel();
        }
        int size = sortedDialogs.size();
        for (int b = 0; b < size; b++) {
            boolean canReply;
            String name;
            Intent intent;
            String conversationName;
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
            JSONObject serializedChat = null;
            if (serializedNotifications != null) {
                serializedChat = new JSONObject();
            }
            MessageObject lastMessageObject = (MessageObject) messageObjects.get(0);
            int max_date = lastMessageObject.messageOwner.date;
            Chat chat = null;
            User user = null;
            boolean isChannel = false;
            boolean isSupergroup = false;
            TLObject photoPath = null;
            Bitmap avatarBitmap = null;
            File avatalFile = null;
            LongSparseArray<Person> personCache = new LongSparseArray();
            if (lowerId != 0) {
                canReply = lowerId != 777000;
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
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("not found user to show dialog notification " + lowerId);
                        }
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
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("not found chat to show dialog notification " + lowerId);
                        }
                    }
                }
            } else {
                canReply = false;
                if (dialog_id != globalSecretChatId) {
                    EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(highId));
                    if (encryptedChat != null) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                        if (user == null) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.w("not found secret chat user to show dialog notification " + encryptedChat.user_id);
                            }
                        }
                    } else if (BuildVars.LOGS_ENABLED) {
                        FileLog.w("not found secret chat to show dialog notification " + highId);
                    }
                }
                name = LocaleController.getString("SecretChatName", NUM);
                photoPath = null;
                serializedChat = null;
            }
            if (AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
                name = LocaleController.getString("AppName", NUM);
                photoPath = null;
                canReply = false;
            }
            if (photoPath != null) {
                avatalFile = FileLoader.getPathToAttach(photoPath, true);
                BitmapDrawable img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                if (img != null) {
                    avatarBitmap = img.getBitmap();
                } else if (VERSION.SDK_INT < 28) {
                    try {
                        if (avatalFile.exists()) {
                            int i;
                            float scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                            Options options = new Options();
                            if (scaleFactor < 1.0f) {
                                i = 1;
                            } else {
                                i = (int) scaleFactor;
                            }
                            options.inSampleSize = i;
                            avatarBitmap = BitmapFactory.decodeFile(avatalFile.getAbsolutePath(), options);
                        }
                    } catch (Throwable th) {
                    }
                }
            }
            Action wearReplyAction = null;
            if ((!isChannel || isSupergroup) && canReply && !SharedConfig.isWaitingForPasscodeEnter) {
                String replyToString;
                intent = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                intent.putExtra("dialog_id", dialog_id);
                intent.putExtra("max_id", max_id);
                intent.putExtra("currentAccount", this.currentAccount);
                PendingIntent replyPendingIntent = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, NUM);
                RemoteInput remoteInputWear = new RemoteInput.Builder("extra_voice_reply").setLabel(LocaleController.getString("Reply", NUM)).build();
                if (lowerId < 0) {
                    replyToString = LocaleController.formatString("ReplyToGroup", NUM, name);
                } else {
                    replyToString = LocaleController.formatString("ReplyToUser", NUM, name);
                }
                wearReplyAction = new Action.Builder(NUM, replyToString, replyPendingIntent).setAllowGeneratedReplies(true).setSemanticAction(1).addRemoteInput(remoteInputWear).setShowsUserInterface(false).build();
            }
            Integer count = (Integer) this.pushDialogs.get(dialog_id);
            if (count == null) {
                count = Integer.valueOf(0);
            }
            if (Math.max(count.intValue(), messageObjects.size()) <= 1 || VERSION.SDK_INT >= 28) {
                conversationName = name;
            } else {
                conversationName = String.format("%1$s (%2$d)", new Object[]{name, Integer.valueOf(n)});
            }
            Style messagingStyle = new MessagingStyle("");
            if (VERSION.SDK_INT < 28 || (lowerId < 0 && !isChannel)) {
                messagingStyle.setConversationTitle(conversationName);
            }
            boolean z = VERSION.SDK_INT < 28 || (!isChannel && lowerId < 0);
            messagingStyle.setGroupConversation(z);
            StringBuilder text = new StringBuilder();
            String[] senderName = new String[1];
            boolean[] preview = new boolean[1];
            ArrayList<TL_keyboardButtonRow> rows = null;
            int rowsMid = 0;
            JSONArray serializedMsgs = null;
            if (serializedChat != null) {
                serializedMsgs = new JSONArray();
            }
            for (a = messageObjects.size() - 1; a >= 0; a--) {
                messageObject = (MessageObject) messageObjects.get(a);
                String message = getShortStringForMessage(messageObject, senderName, preview);
                if (message != null) {
                    long uid;
                    User sender;
                    if (text.length() > 0) {
                        text.append("\n\n");
                    }
                    if (senderName[0] != null) {
                        text.append(String.format("%1$s: %2$s", new Object[]{senderName[0], message}));
                    } else {
                        text.append(message);
                    }
                    if (lowerId > 0) {
                        uid = (long) lowerId;
                    } else if (isChannel) {
                        uid = (long) (-lowerId);
                    } else if (lowerId < 0) {
                        uid = (long) messageObject.getFromId();
                    } else {
                        uid = dialog_id;
                    }
                    Person person = (Person) personCache.get(uid);
                    if (person == null) {
                        Person.Builder personBuilder = new Person.Builder().setName(senderName[0] == null ? "" : senderName[0]);
                        if (preview[0] && lowerId != 0 && VERSION.SDK_INT >= 28) {
                            File avatar = null;
                            if (lowerId > 0 || isChannel) {
                                avatar = avatalFile;
                            } else if (lowerId < 0) {
                                int fromId = messageObject.getFromId();
                                sender = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(fromId));
                                if (sender == null) {
                                    sender = MessagesStorage.getInstance(this.currentAccount).getUserSync(fromId);
                                    if (sender != null) {
                                        MessagesController.getInstance(this.currentAccount).putUser(sender, true);
                                    }
                                }
                                if (!(sender == null || sender.photo == null || sender.photo.photo_small == null || sender.photo.photo_small.volume_id == 0 || sender.photo.photo_small.local_id == 0)) {
                                    avatar = FileLoader.getPathToAttach(sender.photo.photo_small, true);
                                }
                            }
                            if (avatar != null) {
                                try {
                                    personBuilder.setIcon(IconCompat.createWithBitmap(ImageDecoder.decodeBitmap(ImageDecoder.createSource(avatar), NotificationsController$$Lambda$17.$instance)));
                                } catch (Throwable th2) {
                                }
                            }
                        }
                        person = personBuilder.build();
                        personCache.put(uid, person);
                    }
                    if (lowerId != 0) {
                        Uri uri;
                        if (VERSION.SDK_INT < 28 || ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).isLowRamDevice()) {
                            messagingStyle.addMessage(message, ((long) messageObject.messageOwner.date) * 1000, person);
                        } else if (messageObject.type == 1 || messageObject.isSticker()) {
                            File attach = FileLoader.getPathToMessage(messageObject.messageOwner);
                            MessagingStyle.Message message2 = new MessagingStyle.Message(message, ((long) messageObject.messageOwner.date) * 1000, person);
                            String mimeType = messageObject.isSticker() ? "image/webp" : "image/jpeg";
                            if (attach.exists()) {
                                uri = FileProvider.getUriForFile(ApplicationLoader.applicationContext, "org.telegram.messenger.provider", attach);
                            } else if (FileLoader.getInstance(this.currentAccount).isLoadingFile(attach.getName())) {
                                uri = new Uri.Builder().scheme("content").authority("org.telegram.messenger.notification_image_provider").appendPath("msg_media_raw").appendPath(this.currentAccount + "").appendPath(attach.getName()).appendQueryParameter("final_path", attach.getAbsolutePath()).build();
                            } else {
                                uri = null;
                            }
                            if (uri != null) {
                                message2.setData(mimeType, uri);
                                messagingStyle.addMessage(message2);
                                ApplicationLoader.applicationContext.grantUriPermission("com.android.systemui", uri, 1);
                                AndroidUtilities.runOnUIThread(new NotificationsController$$Lambda$18(uri), 20000);
                                if (!TextUtils.isEmpty(messageObject.caption)) {
                                    messagingStyle.addMessage(messageObject.caption, ((long) messageObject.messageOwner.date) * 1000, person);
                                }
                            } else {
                                messagingStyle.addMessage(message, ((long) messageObject.messageOwner.date) * 1000, person);
                            }
                        } else {
                            messagingStyle.addMessage(message, ((long) messageObject.messageOwner.date) * 1000, person);
                        }
                        if (messageObject.isVoice()) {
                            List<MessagingStyle.Message> messages = messagingStyle.getMessages();
                            if (!messages.isEmpty()) {
                                File f = FileLoader.getPathToMessage(messageObject.messageOwner);
                                if (VERSION.SDK_INT >= 24) {
                                    try {
                                        uri = FileProvider.getUriForFile(ApplicationLoader.applicationContext, "org.telegram.messenger.provider", f);
                                    } catch (Exception e) {
                                        uri = null;
                                    }
                                } else {
                                    uri = Uri.fromFile(f);
                                }
                                if (uri != null) {
                                    ((MessagingStyle.Message) messages.get(messages.size() - 1)).setData("audio/ogg", uri);
                                }
                            }
                        }
                    } else {
                        messagingStyle.addMessage(message, ((long) messageObject.messageOwner.date) * 1000, person);
                    }
                    if (serializedMsgs != null) {
                        try {
                            JSONObject jmsg = new JSONObject();
                            jmsg.put("text", message);
                            jmsg.put("date", messageObject.messageOwner.date);
                            if (messageObject.isFromUser() && lowerId < 0) {
                                sender = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.getFromId()));
                                if (sender != null) {
                                    jmsg.put("fname", sender.first_name);
                                    jmsg.put("lname", sender.last_name);
                                }
                            }
                            serializedMsgs.put(jmsg);
                        } catch (JSONException e2) {
                        }
                    }
                    if (dialog_id == 777000 && messageObject.messageOwner.reply_markup != null) {
                        rows = messageObject.messageOwner.reply_markup.rows;
                        rowsMid = messageObject.getId();
                    }
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.w("message text is null for " + messageObject.getId() + " did = " + messageObject.getDialogId());
                }
            }
            intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
            intent.setFlags(32768);
            intent.addCategory("android.intent.category.LAUNCHER");
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
            intent = new Intent(ApplicationLoader.applicationContext, AutoMessageHeardReceiver.class);
            intent.addFlags(32);
            intent.setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
            intent.putExtra("dialog_id", dialog_id);
            intent.putExtra("max_id", max_id);
            intent.putExtra("currentAccount", this.currentAccount);
            Action readAction = new Action.Builder(NUM, LocaleController.getString("MarkAsRead", NUM), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, NUM)).setSemanticAction(2).setShowsUserInterface(false).build();
            if (lowerId != 0) {
                if (lowerId > 0) {
                    dismissalID = "tguser" + lowerId + "_" + max_id;
                } else {
                    dismissalID = "tgchat" + (-lowerId) + "_" + max_id;
                }
            } else if (dialog_id != globalSecretChatId) {
                dismissalID = "tgenc" + highId + "_" + max_id;
            } else {
                dismissalID = null;
            }
            if (dismissalID != null) {
                wearableExtender.setDismissalId(dismissalID);
                WearableExtender summaryExtender = new WearableExtender();
                summaryExtender.setDismissalId("summary_" + dismissalID);
                notificationBuilder.extend(summaryExtender);
            }
            wearableExtender.setBridgeTag("tgaccount" + UserConfig.getInstance(this.currentAccount).getClientUserId());
            long date = ((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(NUM).setContentText(text.toString()).setAutoCancel(true).setNumber(messageObjects.size()).setColor(-15618822).setGroupSummary(false).setWhen(date).setShowWhen(true).setShortcutId("sdid_" + dialog_id).setStyle(messagingStyle).setContentIntent(contentIntent).extend(wearableExtender).setSortKey("" + (Long.MAX_VALUE - date)).setCategory("msg");
            if (useSummaryNotification) {
                builder.setGroup(this.notificationGroup);
                builder.setGroupAlertBehavior(1);
            }
            if (wearReplyAction != null) {
                builder.addAction(wearReplyAction);
            }
            builder.addAction(readAction);
            if (this.pushDialogs.size() == 1 && !TextUtils.isEmpty(summary)) {
                builder.setSubText(summary);
            }
            if (lowerId == 0) {
                builder.setLocalOnly(true);
            }
            if (avatarBitmap != null && VERSION.SDK_INT < 28) {
                builder.setLargeIcon(avatarBitmap);
            }
            if (!(AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter || rows == null)) {
                int rc = rows.size();
                for (int r = 0; r < rc; r++) {
                    TL_keyboardButtonRow row = (TL_keyboardButtonRow) rows.get(r);
                    int cc = row.buttons.size();
                    for (int c = 0; c < cc; c++) {
                        KeyboardButton button = (KeyboardButton) row.buttons.get(c);
                        if (button instanceof TL_keyboardButtonCallback) {
                            intent = new Intent(ApplicationLoader.applicationContext, NotificationCallbackReceiver.class);
                            intent.putExtra("currentAccount", this.currentAccount);
                            intent.putExtra("did", dialog_id);
                            if (button.data != null) {
                                intent.putExtra("data", button.data);
                            }
                            intent.putExtra("mid", rowsMid);
                            String str = button.text;
                            Context context = ApplicationLoader.applicationContext;
                            int i2 = this.lastButtonId;
                            this.lastButtonId = i2 + 1;
                            builder.addAction(0, str, PendingIntent.getBroadcast(context, i2, intent, NUM));
                        }
                    }
                }
            }
            if (chat == null && user != null && user.phone != null && user.phone.length() > 0) {
                builder.addPerson("tel:+" + user.phone);
            }
            if (VERSION.SDK_INT >= 26) {
                if (useSummaryNotification) {
                    builder.setChannelId(OTHER_NOTIFICATIONS_CHANNEL);
                } else {
                    builder.setChannelId(mainNotification.getChannelId());
                }
            }
            holders.add(new AnonymousClass1NotificationHolder(internalId.intValue(), builder.build()));
            this.wearNotificationsIds.put(dialog_id, internalId);
            if (!(lowerId == 0 || serializedChat == null)) {
                try {
                    serializedChat.put("reply", canReply);
                    serializedChat.put("name", name);
                    serializedChat.put("max_id", max_id);
                    serializedChat.put("max_date", max_date);
                    serializedChat.put("id", Math.abs(lowerId));
                    if (photoPath != null) {
                        serializedChat.put("photo", photoPath.dc_id + "_" + photoPath.volume_id + "_" + photoPath.secret);
                    }
                    if (serializedMsgs != null) {
                        serializedChat.put("msgs", serializedMsgs);
                    }
                    if (lowerId > 0) {
                        serializedChat.put("type", "user");
                    } else if (lowerId < 0) {
                        if (isChannel || isSupergroup) {
                            serializedChat.put("type", "channel");
                        } else {
                            serializedChat.put("type", "group");
                        }
                    }
                    serializedNotifications.put(serializedChat);
                } catch (JSONException e3) {
                }
            }
        }
        if (useSummaryNotification) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("show summary with id " + this.notificationId);
            }
            notificationManager.notify(this.notificationId, mainNotification);
        } else {
            notificationManager.cancel(this.notificationId);
        }
        size = holders.size();
        for (a = 0; a < size; a++) {
            ((AnonymousClass1NotificationHolder) holders.get(a)).call();
        }
        for (a = 0; a < oldIdsWear.size(); a++) {
            Integer id = (Integer) oldIdsWear.valueAt(a);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.w("cancel notification id " + id);
            }
            notificationManager.cancel(id.intValue());
        }
        if (serializedNotifications != null) {
            try {
                JSONObject s = new JSONObject();
                s.put("id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                s.put("n", serializedNotifications);
                WearDataLayerListenerService.sendMessageToWatch("/notify", s.toString().getBytes("UTF-8"), "remote_notifications");
            } catch (Exception e4) {
            }
        }
    }

    static final /* synthetic */ int lambda$null$29$NotificationsController(Canvas canvas) {
        Path path = new Path();
        path.setFillType(FillType.INVERSE_EVEN_ODD);
        int width = canvas.getWidth();
        path.addRoundRect(0.0f, 0.0f, (float) width, (float) canvas.getHeight(), (float) (width / 2), (float) (width / 2), Direction.CW);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
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
                FileLog.e(e);
            }
            notificationsQueue.postRunnable(new NotificationsController$$Lambda$19(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$playOutChatSound$33$NotificationsController() {
        try {
            if (Math.abs(System.currentTimeMillis() - this.lastSoundOutPlay) > 100) {
                this.lastSoundOutPlay = System.currentTimeMillis();
                if (this.soundPool == null) {
                    this.soundPool = new SoundPool(3, 1, 0);
                    this.soundPool.setOnLoadCompleteListener(NotificationsController$$Lambda$22.$instance);
                }
                if (this.soundOut == 0 && !this.soundOutLoaded) {
                    this.soundOutLoaded = true;
                    this.soundOut = this.soundPool.load(ApplicationLoader.applicationContext, NUM, 1);
                }
                if (this.soundOut != 0) {
                    try {
                        this.soundPool.play(this.soundOut, 1.0f, 1.0f, 1, 0, 1.0f);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    static final /* synthetic */ void lambda$null$32$NotificationsController(SoundPool soundPool, int sampleId, int status) {
        if (status == 0) {
            try {
                soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void updateServerNotificationsSettings(long dialog_id) {
        int i = 0;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        if (((int) dialog_id) != 0) {
            SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
            TL_account_updateNotifySettings req = new TL_account_updateNotifySettings();
            req.settings = new TL_inputPeerNotifySettings();
            TL_inputPeerNotifySettings tL_inputPeerNotifySettings = req.settings;
            tL_inputPeerNotifySettings.flags |= 1;
            req.settings.show_previews = preferences.getBoolean("preview_" + dialog_id, true);
            tL_inputPeerNotifySettings = req.settings;
            tL_inputPeerNotifySettings.flags |= 2;
            req.settings.silent = preferences.getBoolean("silent_" + dialog_id, false);
            int mute_type = preferences.getInt("notify2_" + dialog_id, -1);
            if (mute_type != -1) {
                tL_inputPeerNotifySettings = req.settings;
                tL_inputPeerNotifySettings.flags |= 4;
                if (mute_type == 3) {
                    req.settings.mute_until = preferences.getInt("notifyuntil_" + dialog_id, 0);
                } else {
                    tL_inputPeerNotifySettings = req.settings;
                    if (mute_type == 2) {
                        i = Integer.MAX_VALUE;
                    }
                    tL_inputPeerNotifySettings.mute_until = i;
                }
            }
            req.peer = new TL_inputNotifyPeer();
            ((TL_inputNotifyPeer) req.peer).peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) dialog_id);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, NotificationsController$$Lambda$20.$instance);
        }
    }

    static final /* synthetic */ void lambda$updateServerNotificationsSettings$34$NotificationsController(TLObject response, TL_error error) {
    }

    public void updateServerNotificationsSettings(int type) {
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        TL_account_updateNotifySettings req = new TL_account_updateNotifySettings();
        req.settings = new TL_inputPeerNotifySettings();
        req.settings.flags = 5;
        if (type == 0) {
            req.peer = new TL_inputNotifyChats();
            req.settings.mute_until = preferences.getInt("EnableGroup2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewGroup", true);
        } else if (type == 1) {
            req.peer = new TL_inputNotifyUsers();
            req.settings.mute_until = preferences.getInt("EnableAll2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewAll", true);
        } else {
            req.peer = new TL_inputNotifyBroadcasts();
            req.settings.mute_until = preferences.getInt("EnableChannel2", 0);
            req.settings.show_previews = preferences.getBoolean("EnablePreviewChannel", true);
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, NotificationsController$$Lambda$21.$instance);
    }

    static final /* synthetic */ void lambda$updateServerNotificationsSettings$35$NotificationsController(TLObject response, TL_error error) {
    }

    public boolean isGlobalNotificationsEnabled(long did) {
        int type;
        int lower_id = (int) did;
        if (lower_id < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_id));
            if (!ChatObject.isChannel(chat) || chat.megagroup) {
                type = 0;
            } else {
                type = 2;
            }
        } else {
            type = 1;
        }
        return isGlobalNotificationsEnabled(type);
    }

    public boolean isGlobalNotificationsEnabled(int type) {
        return MessagesController.getNotificationsSettings(this.currentAccount).getInt(getGlobalNotificationsKey(type), 0) < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
    }

    public void setGlobalNotificationsEnabled(int type, int time) {
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt(getGlobalNotificationsKey(type), time).commit();
        getInstance(this.currentAccount).updateServerNotificationsSettings(type);
    }

    public String getGlobalNotificationsKey(int type) {
        if (type == 0) {
            return "EnableGroup2";
        }
        if (type == 1) {
            return "EnableAll2";
        }
        return "EnableChannel2";
    }
}
