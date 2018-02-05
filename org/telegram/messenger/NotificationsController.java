package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
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
import org.telegram.messenger.beta.R;
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
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PopupNotificationActivity;

public class NotificationsController {
    public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    private static volatile NotificationsController[] Instance = new NotificationsController[3];
    public static final String OTHER_NOTIFICATIONS_CHANNEL = "Other3";
    protected static AudioManager audioManager = ((AudioManager) ApplicationLoader.applicationContext.getSystemService(MimeTypes.BASE_TYPE_AUDIO));
    private static NotificationManagerCompat notificationManager;
    private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
    private static NotificationManager systemNotificationManager;
    private AlarmManager alarmManager;
    private int currentAccount;
    private ArrayList<MessageObject> delayedPushMessages = new ArrayList();
    private boolean inChatSoundEnabled = true;
    private int lastBadgeCount = -1;
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
            FileLog.e(e);
        }
        try {
            this.alarmManager = (AlarmManager) ApplicationLoader.applicationContext.getSystemService("alarm");
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        try {
            this.notificationDelayWakelock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "lock");
            this.notificationDelayWakelock.setReferenceCounted(false);
        } catch (Throwable e22) {
            FileLog.e(e22);
        }
        this.notificationDelayRunnable = new Runnable() {
            public void run() {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("delay reached");
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
                    FileLog.e(e);
                }
            }
        };
    }

    public void cleanup() {
        this.popupMessages.clear();
        this.popupReplyMessages.clear();
        notificationsQueue.postRunnable(new Runnable() {
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
                    FileLog.e(e);
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
                        FileLog.e(e2);
                    }
                }
            }
        });
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
                    FileLog.d("set last online from other device = " + time);
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

    protected void showSingleBackgroundNotification() {
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                try {
                    if (ApplicationLoader.mainInterfacePaused) {
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                        boolean notifyDisabled = false;
                        int needVibrate = 0;
                        String choosenSoundPath = null;
                        int ledColor = -16776961;
                        int priority = 0;
                        if (!preferences.getBoolean("EnableAll", true)) {
                            notifyDisabled = true;
                        }
                        String defaultPath = System.DEFAULT_NOTIFICATION_URI.getPath();
                        if (!notifyDisabled) {
                            choosenSoundPath = null;
                            boolean vibrateOnlyIfSilent = false;
                            if (choosenSoundPath != null && choosenSoundPath.equals(defaultPath)) {
                                choosenSoundPath = null;
                            } else if (choosenSoundPath == null) {
                                choosenSoundPath = preferences.getString("GlobalSoundPath", defaultPath);
                            }
                            needVibrate = preferences.getInt("vibrate_messages", 0);
                            priority = preferences.getInt("priority_group", 1);
                            ledColor = preferences.getInt("MessagesLed", -16776961);
                            if (needVibrate == 4) {
                                vibrateOnlyIfSilent = true;
                                needVibrate = 0;
                            }
                            if ((needVibrate == 2 && (0 == 1 || 0 == 3)) || ((needVibrate != 2 && 0 == 2) || !(null == null || 0 == 4))) {
                                needVibrate = 0;
                            }
                            if (vibrateOnlyIfSilent && needVibrate != 2) {
                                try {
                                    int mode = NotificationsController.audioManager.getRingerMode();
                                    if (!(mode == 0 || mode == 1)) {
                                        needVibrate = 2;
                                    }
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            }
                        }
                        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                        intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                        intent.setFlags(32768);
                        PendingIntent contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM);
                        String name = LocaleController.getString("YouHaveNewMessage", R.string.YouHaveNewMessage);
                        Builder mBuilder = new Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(R.drawable.notification).setAutoCancel(true).setNumber(NotificationsController.this.total_unread_count).setContentIntent(contentIntent).setGroup(NotificationsController.this.notificationGroup).setGroupSummary(true).setColor(-13851168);
                        long[] vibrationPattern = null;
                        int importance = 0;
                        Uri sound = null;
                        mBuilder.setCategory("msg");
                        String lastMessage = LocaleController.getString("BackgroundRestricted", R.string.BackgroundRestricted);
                        mBuilder.setContentText(lastMessage);
                        mBuilder.setStyle(new BigTextStyle().bigText(lastMessage));
                        if (priority == 0) {
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
                        if (notifyDisabled) {
                            vibrationPattern = new long[]{0, 0};
                            mBuilder.setVibrate(vibrationPattern);
                        } else {
                            if (lastMessage.length() > 100) {
                                lastMessage = lastMessage.substring(0, 100).replace('\n', ' ').trim() + "...";
                            }
                            mBuilder.setTicker(lastMessage);
                            if (!(choosenSoundPath == null || choosenSoundPath.equals("NoSound"))) {
                                if (VERSION.SDK_INT >= 26) {
                                    sound = choosenSoundPath.equals(defaultPath) ? System.DEFAULT_NOTIFICATION_URI : Uri.parse(choosenSoundPath);
                                } else if (choosenSoundPath.equals(defaultPath)) {
                                    mBuilder.setSound(System.DEFAULT_NOTIFICATION_URI, 5);
                                } else {
                                    mBuilder.setSound(Uri.parse(choosenSoundPath), 5);
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
                        if (VERSION.SDK_INT >= 26) {
                            mBuilder.setChannelId(NotificationsController.this.validateChannelId(0, name, vibrationPattern, ledColor, sound, importance, vibrationPattern, sound, importance));
                        }
                        NotificationsController.this.lastNotificationIsNoData = true;
                        NotificationsController.notificationManager.notify(NotificationsController.this.notificationId, mBuilder.build());
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
        });
    }

    protected void forceShowPopupForReply() {
        notificationsQueue.postRunnable(new Runnable() {
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
        });
    }

    public void removeDeletedMessagesFromNotifications(final SparseArray<ArrayList<Integer>> deletedMessages) {
        final ArrayList<MessageObject> popupArrayRemove = new ArrayList(0);
        notificationsQueue.postRunnable(new Runnable() {
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            int size = popupArrayRemove.size();
                            for (int a = 0; a < size; a++) {
                                NotificationsController.this.popupMessages.remove(popupArrayRemove.get(a));
                            }
                        }
                    });
                }
                if (old_unread_count != NotificationsController.this.total_unread_count) {
                    if (NotificationsController.this.notifyCheck) {
                        NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime());
                    } else {
                        NotificationsController.this.delayedPushMessages.clear();
                        NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                        }
                    });
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            int size = popupArrayRemove.size();
                            for (int a = 0; a < size; a++) {
                                NotificationsController.this.popupMessages.remove(popupArrayRemove.get(a));
                            }
                        }
                    });
                }
                if (old_unread_count != NotificationsController.this.total_unread_count) {
                    if (NotificationsController.this.notifyCheck) {
                        NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime());
                    } else {
                        NotificationsController.this.delayedPushMessages.clear();
                        NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                        }
                    });
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
                                mid = (long) messageObject.messageOwner.id;
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
                                mid = (long) messageObject.messageOwner.id;
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            int size = popupArrayRemove.size();
                            for (int a = 0; a < size; a++) {
                                NotificationsController.this.popupMessages.remove(popupArrayRemove.get(a));
                            }
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                        }
                    });
                }
            }
        });
    }

    public void processNewMessages(final ArrayList<MessageObject> messageObjects, final boolean isLast) {
        if (!messageObjects.isEmpty()) {
            final ArrayList<MessageObject> popupArrayAdd = new ArrayList(0);
            notificationsQueue.postRunnable(new Runnable() {
                public void run() {
                    boolean added = false;
                    LongSparseArray<Boolean> settingsCache = new LongSparseArray();
                    SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
                    boolean allowPinned = preferences.getBoolean("PinnedMessages", true);
                    int popup = 0;
                    for (int a = 0; a < messageObjects.size(); a++) {
                        MessageObject messageObject = (MessageObject) messageObjects.get(a);
                        long mid = (long) messageObject.messageOwner.id;
                        if (messageObject.messageOwner.to_id.channel_id != 0) {
                            mid |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
                        }
                        if (NotificationsController.this.pushMessagesDict.indexOfKey(mid) < 0) {
                            long dialog_id = messageObject.getDialogId();
                            long original_dialog_id = dialog_id;
                            if (dialog_id == NotificationsController.this.opened_dialog_id && ApplicationLoader.isScreenOn) {
                                NotificationsController.this.playInChatSound();
                            } else {
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
                            }
                        }
                    }
                    if (added) {
                        NotificationsController.this.notifyCheck = isLast;
                    }
                    if (!popupArrayAdd.isEmpty() && !AndroidUtilities.needShowPasscode(false)) {
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
                                long mid = (long) messageObject.messageOwner.id;
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
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            int size = popupArrayToRemove.size();
                            for (int a = 0; a < size; a++) {
                                NotificationsController.this.popupMessages.remove(popupArrayToRemove.get(a));
                            }
                        }
                    });
                }
                if (old_unread_count != NotificationsController.this.total_unread_count) {
                    if (NotificationsController.this.notifyCheck) {
                        NotificationsController.this.scheduleNotificationDelay(NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime());
                    } else {
                        NotificationsController.this.delayedPushMessages.clear();
                        NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                        }
                    });
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (NotificationsController.this.total_unread_count == 0) {
                            NotificationsController.this.popupMessages.clear();
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, Integer.valueOf(NotificationsController.this.currentAccount));
                    }
                });
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
        setBadge(getTotalAllUnreadCount());
    }

    private void setBadge(final int count) {
        notificationsQueue.postRunnable(new Runnable() {
            public void run() {
                if (NotificationsController.this.lastBadgeCount != count) {
                    NotificationsController.this.lastBadgeCount = count;
                    NotificationBadge.applyCount(count);
                }
            }
        });
    }

    private String getStringForMessage(MessageObject messageObject, boolean shortMessage, boolean[] text) {
        User user;
        Chat chat;
        long dialog_id = messageObject.messageOwner.dialog_id;
        int chat_id = messageObject.messageOwner.to_id.chat_id != 0 ? messageObject.messageOwner.to_id.chat_id : messageObject.messageOwner.to_id.channel_id;
        int from_id = messageObject.messageOwner.to_id.user_id;
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
        if (((int) dialog_id) == 0 || AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
            return LocaleController.getString("YouHaveNewMessage", R.string.YouHaveNewMessage);
        }
        String msg;
        if (chat_id == 0 && from_id != 0) {
            if (!MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewAll", true)) {
                return LocaleController.formatString("NotificationMessageNoText", R.string.NotificationMessageNoText, name);
            } else if (messageObject.messageOwner instanceof TL_messageService) {
                if (messageObject.messageOwner.action instanceof TL_messageActionUserJoined) {
                    return LocaleController.formatString("NotificationContactJoined", R.string.NotificationContactJoined, name);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto) {
                    return LocaleController.formatString("NotificationContactNewPhoto", R.string.NotificationContactNewPhoto, name);
                } else if (messageObject.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                    String date = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(((long) messageObject.messageOwner.date) * 1000), LocaleController.getInstance().formatterDay.format(((long) messageObject.messageOwner.date) * 1000));
                    return LocaleController.formatString("NotificationUnrecognizedDevice", R.string.NotificationUnrecognizedDevice, UserConfig.getInstance(this.currentAccount).getCurrentUser().first_name, date, messageObject.messageOwner.action.title, messageObject.messageOwner.action.address);
                } else if ((messageObject.messageOwner.action instanceof TL_messageActionGameScore) || (messageObject.messageOwner.action instanceof TL_messageActionPaymentSent)) {
                    return messageObject.messageText.toString();
                } else {
                    if (!(messageObject.messageOwner.action instanceof TL_messageActionPhoneCall)) {
                        return null;
                    }
                    PhoneCallDiscardReason reason = messageObject.messageOwner.action.reason;
                    if (messageObject.isOut() || !(reason instanceof TL_phoneCallDiscardReasonMissed)) {
                        return null;
                    }
                    return LocaleController.getString("CallMessageIncomingMissed", R.string.CallMessageIncomingMissed);
                }
            } else if (messageObject.isMediaEmpty()) {
                if (shortMessage) {
                    return LocaleController.formatString("NotificationMessageNoText", R.string.NotificationMessageNoText, name);
                } else if (messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) {
                    return LocaleController.formatString("NotificationMessageNoText", R.string.NotificationMessageNoText, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, messageObject.messageOwner.message);
                    text[0] = true;
                    return msg;
                }
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                if (!shortMessage && VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, "🖼 " + messageObject.messageOwner.message);
                    text[0] = true;
                    return msg;
                } else if (messageObject.messageOwner.media.ttl_seconds != 0) {
                    return LocaleController.formatString("NotificationMessageSDPhoto", R.string.NotificationMessageSDPhoto, name);
                } else {
                    return LocaleController.formatString("NotificationMessagePhoto", R.string.NotificationMessagePhoto, name);
                }
            } else if (messageObject.isVideo()) {
                if (!shortMessage && VERSION.SDK_INT >= 19 && !TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, "📹 " + messageObject.messageOwner.message);
                    text[0] = true;
                    return msg;
                } else if (messageObject.messageOwner.media.ttl_seconds != 0) {
                    return LocaleController.formatString("NotificationMessageSDVideo", R.string.NotificationMessageSDVideo, name);
                } else {
                    return LocaleController.formatString("NotificationMessageVideo", R.string.NotificationMessageVideo, name);
                }
            } else if (messageObject.isGame()) {
                return LocaleController.formatString("NotificationMessageGame", R.string.NotificationMessageGame, name, messageObject.messageOwner.media.game.title);
            } else if (messageObject.isVoice()) {
                return LocaleController.formatString("NotificationMessageAudio", R.string.NotificationMessageAudio, name);
            } else if (messageObject.isRoundVideo()) {
                return LocaleController.formatString("NotificationMessageRound", R.string.NotificationMessageRound, name);
            } else if (messageObject.isMusic()) {
                return LocaleController.formatString("NotificationMessageMusic", R.string.NotificationMessageMusic, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                return LocaleController.formatString("NotificationMessageContact", R.string.NotificationMessageContact, name);
            } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                return LocaleController.formatString("NotificationMessageMap", R.string.NotificationMessageMap, name);
            } else if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                return LocaleController.formatString("NotificationMessageLiveLocation", R.string.NotificationMessageLiveLocation, name);
            } else if (!(messageObject.messageOwner.media instanceof TL_messageMediaDocument)) {
                return null;
            } else {
                if (messageObject.isSticker()) {
                    if (messageObject.getStickerEmoji() != null) {
                        return LocaleController.formatString("NotificationMessageStickerEmoji", R.string.NotificationMessageStickerEmoji, name, messageObject.getStickerEmoji());
                    }
                    return LocaleController.formatString("NotificationMessageSticker", R.string.NotificationMessageSticker, name);
                } else if (messageObject.isGif()) {
                    if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                        return LocaleController.formatString("NotificationMessageGif", R.string.NotificationMessageGif, name);
                    }
                    msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, "🎬 " + messageObject.messageOwner.message);
                    text[0] = true;
                    return msg;
                } else if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                    return LocaleController.formatString("NotificationMessageDocument", R.string.NotificationMessageDocument, name);
                } else {
                    msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, "📎 " + messageObject.messageOwner.message);
                    text[0] = true;
                    return msg;
                }
            }
        } else if (chat_id == 0) {
            return null;
        } else {
            if (MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewGroup", true)) {
                if (messageObject.messageOwner instanceof TL_messageService) {
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
                            return LocaleController.formatString("NotificationGroupAddMember", R.string.NotificationGroupAddMember, name, chat.title, stringBuilder.toString());
                        } else if (messageObject.messageOwner.to_id.channel_id != 0 && !chat.megagroup) {
                            return LocaleController.formatString("ChannelAddedByNotification", R.string.ChannelAddedByNotification, name, chat.title);
                        } else if (singleUserId == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            return LocaleController.formatString("NotificationInvitedToGroup", R.string.NotificationInvitedToGroup, name, chat.title);
                        } else {
                            User u2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(singleUserId));
                            if (u2 == null) {
                                return null;
                            }
                            if (from_id != u2.id) {
                                return LocaleController.formatString("NotificationGroupAddMember", R.string.NotificationGroupAddMember, name, chat.title, UserObject.getUserName(u2));
                            } else if (chat.megagroup) {
                                return LocaleController.formatString("NotificationGroupAddSelfMega", R.string.NotificationGroupAddSelfMega, name, chat.title);
                            } else {
                                return LocaleController.formatString("NotificationGroupAddSelf", R.string.NotificationGroupAddSelf, name, chat.title);
                            }
                        }
                    } else if (messageObject.messageOwner.action instanceof TL_messageActionChatJoinedByLink) {
                        return LocaleController.formatString("NotificationInvitedToGroupByLink", R.string.NotificationInvitedToGroupByLink, name, chat.title);
                    } else if (messageObject.messageOwner.action instanceof TL_messageActionChatEditTitle) {
                        return LocaleController.formatString("NotificationEditedGroupName", R.string.NotificationEditedGroupName, name, messageObject.messageOwner.action.title);
                    } else if ((messageObject.messageOwner.action instanceof TL_messageActionChatEditPhoto) || (messageObject.messageOwner.action instanceof TL_messageActionChatDeletePhoto)) {
                        if (messageObject.messageOwner.to_id.channel_id == 0 || chat.megagroup) {
                            return LocaleController.formatString("NotificationEditedGroupPhoto", R.string.NotificationEditedGroupPhoto, name, chat.title);
                        }
                        return LocaleController.formatString("ChannelPhotoEditNotification", R.string.ChannelPhotoEditNotification, chat.title);
                    } else if (messageObject.messageOwner.action instanceof TL_messageActionChatDeleteUser) {
                        if (messageObject.messageOwner.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            return LocaleController.formatString("NotificationGroupKickYou", R.string.NotificationGroupKickYou, name, chat.title);
                        } else if (messageObject.messageOwner.action.user_id == from_id) {
                            return LocaleController.formatString("NotificationGroupLeftMember", R.string.NotificationGroupLeftMember, name, chat.title);
                        } else {
                            if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.action.user_id)) == null) {
                                return null;
                            }
                            return LocaleController.formatString("NotificationGroupKickMember", R.string.NotificationGroupKickMember, name, chat.title, UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.action.user_id))));
                        }
                    } else if (messageObject.messageOwner.action instanceof TL_messageActionChatCreate) {
                        return messageObject.messageText.toString();
                    } else {
                        if (messageObject.messageOwner.action instanceof TL_messageActionChannelCreate) {
                            return messageObject.messageText.toString();
                        }
                        if (messageObject.messageOwner.action instanceof TL_messageActionChatMigrateTo) {
                            return LocaleController.formatString("ActionMigrateFromGroupNotify", R.string.ActionMigrateFromGroupNotify, chat.title);
                        } else if (messageObject.messageOwner.action instanceof TL_messageActionChannelMigrateFrom) {
                            return LocaleController.formatString("ActionMigrateFromGroupNotify", R.string.ActionMigrateFromGroupNotify, messageObject.messageOwner.action.title);
                        } else if (messageObject.messageOwner.action instanceof TL_messageActionScreenshotTaken) {
                            return messageObject.messageText.toString();
                        } else {
                            if (messageObject.messageOwner.action instanceof TL_messageActionPinMessage) {
                                MessageObject object;
                                String message;
                                CharSequence message2;
                                if (chat == null || !chat.megagroup) {
                                    if (messageObject.replyMessageObject == null) {
                                        return LocaleController.formatString("NotificationActionPinnedNoTextChannel", R.string.NotificationActionPinnedNoTextChannel, chat.title);
                                    }
                                    object = messageObject.replyMessageObject;
                                    if (object.isMusic()) {
                                        return LocaleController.formatString("NotificationActionPinnedMusicChannel", R.string.NotificationActionPinnedMusicChannel, chat.title);
                                    } else if (object.isVideo()) {
                                        if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                            return LocaleController.formatString("NotificationActionPinnedVideoChannel", R.string.NotificationActionPinnedVideoChannel, chat.title);
                                        }
                                        message = "📹 " + object.messageOwner.message;
                                        return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, chat.title, message);
                                    } else if (object.isGif()) {
                                        if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                            return LocaleController.formatString("NotificationActionPinnedGifChannel", R.string.NotificationActionPinnedGifChannel, chat.title);
                                        }
                                        message = "🎬 " + object.messageOwner.message;
                                        return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, chat.title, message);
                                    } else if (object.isVoice()) {
                                        return LocaleController.formatString("NotificationActionPinnedVoiceChannel", R.string.NotificationActionPinnedVoiceChannel, chat.title);
                                    } else if (object.isRoundVideo()) {
                                        return LocaleController.formatString("NotificationActionPinnedRoundChannel", R.string.NotificationActionPinnedRoundChannel, chat.title);
                                    } else if (object.isSticker()) {
                                        if (object.getStickerEmoji() != null) {
                                            return LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", R.string.NotificationActionPinnedStickerEmojiChannel, chat.title, object.getStickerEmoji());
                                        }
                                        return LocaleController.formatString("NotificationActionPinnedStickerChannel", R.string.NotificationActionPinnedStickerChannel, chat.title);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaDocument) {
                                        if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                            return LocaleController.formatString("NotificationActionPinnedFileChannel", R.string.NotificationActionPinnedFileChannel, chat.title);
                                        }
                                        message = "📎 " + object.messageOwner.message;
                                        return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, chat.title, message);
                                    } else if ((object.messageOwner.media instanceof TL_messageMediaGeo) || (object.messageOwner.media instanceof TL_messageMediaVenue)) {
                                        return LocaleController.formatString("NotificationActionPinnedGeoChannel", R.string.NotificationActionPinnedGeoChannel, chat.title);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                        return LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", R.string.NotificationActionPinnedGeoLiveChannel, chat.title);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaContact) {
                                        return LocaleController.formatString("NotificationActionPinnedContactChannel", R.string.NotificationActionPinnedContactChannel, chat.title);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaPhoto) {
                                        if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                            return LocaleController.formatString("NotificationActionPinnedPhotoChannel", R.string.NotificationActionPinnedPhotoChannel, chat.title);
                                        }
                                        message = "🖼 " + object.messageOwner.message;
                                        return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, chat.title, message);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaGame) {
                                        return LocaleController.formatString("NotificationActionPinnedGameChannel", R.string.NotificationActionPinnedGameChannel, chat.title);
                                    } else if (object.messageText == null || object.messageText.length() <= 0) {
                                        return LocaleController.formatString("NotificationActionPinnedNoTextChannel", R.string.NotificationActionPinnedNoTextChannel, chat.title);
                                    } else {
                                        message2 = object.messageText;
                                        if (message2.length() > 20) {
                                            message2 = message2.subSequence(0, 20) + "...";
                                        }
                                        return LocaleController.formatString("NotificationActionPinnedTextChannel", R.string.NotificationActionPinnedTextChannel, chat.title, message2);
                                    }
                                } else if (messageObject.replyMessageObject == null) {
                                    return LocaleController.formatString("NotificationActionPinnedNoText", R.string.NotificationActionPinnedNoText, name, chat.title);
                                } else {
                                    object = messageObject.replyMessageObject;
                                    if (object.isMusic()) {
                                        return LocaleController.formatString("NotificationActionPinnedMusic", R.string.NotificationActionPinnedMusic, name, chat.title);
                                    } else if (object.isVideo()) {
                                        if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                            return LocaleController.formatString("NotificationActionPinnedVideo", R.string.NotificationActionPinnedVideo, name, chat.title);
                                        }
                                        message = "📹 " + object.messageOwner.message;
                                        return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, name, message, chat.title);
                                    } else if (object.isGif()) {
                                        if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                            return LocaleController.formatString("NotificationActionPinnedGif", R.string.NotificationActionPinnedGif, name, chat.title);
                                        }
                                        message = "🎬 " + object.messageOwner.message;
                                        return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, name, message, chat.title);
                                    } else if (object.isVoice()) {
                                        return LocaleController.formatString("NotificationActionPinnedVoice", R.string.NotificationActionPinnedVoice, name, chat.title);
                                    } else if (object.isRoundVideo()) {
                                        return LocaleController.formatString("NotificationActionPinnedRound", R.string.NotificationActionPinnedRound, name, chat.title);
                                    } else if (object.isSticker()) {
                                        if (object.getStickerEmoji() != null) {
                                            return LocaleController.formatString("NotificationActionPinnedStickerEmoji", R.string.NotificationActionPinnedStickerEmoji, name, chat.title, object.getStickerEmoji());
                                        }
                                        return LocaleController.formatString("NotificationActionPinnedSticker", R.string.NotificationActionPinnedSticker, name, chat.title);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaDocument) {
                                        if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                            return LocaleController.formatString("NotificationActionPinnedFile", R.string.NotificationActionPinnedFile, name, chat.title);
                                        }
                                        message = "📎 " + object.messageOwner.message;
                                        return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, name, message, chat.title);
                                    } else if ((object.messageOwner.media instanceof TL_messageMediaGeo) || (object.messageOwner.media instanceof TL_messageMediaVenue)) {
                                        return LocaleController.formatString("NotificationActionPinnedGeo", R.string.NotificationActionPinnedGeo, name, chat.title);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaGeoLive) {
                                        return LocaleController.formatString("NotificationActionPinnedGeoLive", R.string.NotificationActionPinnedGeoLive, name, chat.title);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaContact) {
                                        return LocaleController.formatString("NotificationActionPinnedContact", R.string.NotificationActionPinnedContact, name, chat.title);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaPhoto) {
                                        if (VERSION.SDK_INT < 19 || TextUtils.isEmpty(object.messageOwner.message)) {
                                            return LocaleController.formatString("NotificationActionPinnedPhoto", R.string.NotificationActionPinnedPhoto, name, chat.title);
                                        }
                                        message = "🖼 " + object.messageOwner.message;
                                        return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, name, message, chat.title);
                                    } else if (object.messageOwner.media instanceof TL_messageMediaGame) {
                                        return LocaleController.formatString("NotificationActionPinnedGame", R.string.NotificationActionPinnedGame, name, chat.title);
                                    } else if (object.messageText == null || object.messageText.length() <= 0) {
                                        return LocaleController.formatString("NotificationActionPinnedNoText", R.string.NotificationActionPinnedNoText, name, chat.title);
                                    } else {
                                        message2 = object.messageText;
                                        if (message2.length() > 20) {
                                            message2 = message2.subSequence(0, 20) + "...";
                                        }
                                        return LocaleController.formatString("NotificationActionPinnedText", R.string.NotificationActionPinnedText, name, message2, chat.title);
                                    }
                                }
                            } else if (messageObject.messageOwner.action instanceof TL_messageActionGameScore) {
                                return messageObject.messageText.toString();
                            } else {
                                return null;
                            }
                        }
                    }
                } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    if (messageObject.isMediaEmpty()) {
                        if (shortMessage || messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) {
                            return LocaleController.formatString("NotificationMessageGroupNoText", R.string.NotificationMessageGroupNoText, name, chat.title);
                        }
                        return LocaleController.formatString("NotificationMessageGroupText", R.string.NotificationMessageGroupText, name, chat.title, messageObject.messageOwner.message);
                    } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                        if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                            return LocaleController.formatString("NotificationMessageGroupPhoto", R.string.NotificationMessageGroupPhoto, name, chat.title);
                        }
                        return LocaleController.formatString("NotificationMessageGroupText", R.string.NotificationMessageGroupText, name, chat.title, "🖼 " + messageObject.messageOwner.message);
                    } else if (messageObject.isVideo()) {
                        if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                            return LocaleController.formatString("NotificationMessageGroupVideo", R.string.NotificationMessageGroupVideo, name, chat.title);
                        }
                        return LocaleController.formatString("NotificationMessageGroupText", R.string.NotificationMessageGroupText, name, chat.title, "📹 " + messageObject.messageOwner.message);
                    } else if (messageObject.isVoice()) {
                        return LocaleController.formatString("NotificationMessageGroupAudio", R.string.NotificationMessageGroupAudio, name, chat.title);
                    } else if (messageObject.isRoundVideo()) {
                        return LocaleController.formatString("NotificationMessageGroupRound", R.string.NotificationMessageGroupRound, name, chat.title);
                    } else if (messageObject.isMusic()) {
                        return LocaleController.formatString("NotificationMessageGroupMusic", R.string.NotificationMessageGroupMusic, name, chat.title);
                    } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                        return LocaleController.formatString("NotificationMessageGroupContact", R.string.NotificationMessageGroupContact, name, chat.title);
                    } else if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                        return LocaleController.formatString("NotificationMessageGroupGame", R.string.NotificationMessageGroupGame, name, chat.title, messageObject.messageOwner.media.game.title);
                    } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                        return LocaleController.formatString("NotificationMessageGroupMap", R.string.NotificationMessageGroupMap, name, chat.title);
                    } else if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                        return LocaleController.formatString("NotificationMessageGroupLiveLocation", R.string.NotificationMessageGroupLiveLocation, name, chat.title);
                    } else if (!(messageObject.messageOwner.media instanceof TL_messageMediaDocument)) {
                        return null;
                    } else {
                        if (messageObject.isSticker()) {
                            if (messageObject.getStickerEmoji() != null) {
                                return LocaleController.formatString("NotificationMessageGroupStickerEmoji", R.string.NotificationMessageGroupStickerEmoji, name, chat.title, messageObject.getStickerEmoji());
                            }
                            return LocaleController.formatString("NotificationMessageGroupSticker", R.string.NotificationMessageGroupSticker, name, chat.title);
                        } else if (messageObject.isGif()) {
                            if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                                return LocaleController.formatString("NotificationMessageGroupGif", R.string.NotificationMessageGroupGif, name, chat.title);
                            }
                            return LocaleController.formatString("NotificationMessageGroupText", R.string.NotificationMessageGroupText, name, chat.title, "🎬 " + messageObject.messageOwner.message);
                        } else if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                            return LocaleController.formatString("NotificationMessageGroupDocument", R.string.NotificationMessageGroupDocument, name, chat.title);
                        } else {
                            return LocaleController.formatString("NotificationMessageGroupText", R.string.NotificationMessageGroupText, name, chat.title, "📎 " + messageObject.messageOwner.message);
                        }
                    }
                } else if (messageObject.isMediaEmpty()) {
                    if (shortMessage || messageObject.messageOwner.message == null || messageObject.messageOwner.message.length() == 0) {
                        return LocaleController.formatString("ChannelMessageNoText", R.string.ChannelMessageNoText, name);
                    }
                    msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, messageObject.messageOwner.message);
                    text[0] = true;
                    return msg;
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                    if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                        return LocaleController.formatString("ChannelMessagePhoto", R.string.ChannelMessagePhoto, name);
                    }
                    msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, "🖼 " + messageObject.messageOwner.message);
                    text[0] = true;
                    return msg;
                } else if (messageObject.isVideo()) {
                    if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                        return LocaleController.formatString("ChannelMessageVideo", R.string.ChannelMessageVideo, name);
                    }
                    msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, "📹 " + messageObject.messageOwner.message);
                    text[0] = true;
                    return msg;
                } else if (messageObject.isVoice()) {
                    return LocaleController.formatString("ChannelMessageAudio", R.string.ChannelMessageAudio, name);
                } else if (messageObject.isRoundVideo()) {
                    return LocaleController.formatString("ChannelMessageRound", R.string.ChannelMessageRound, name);
                } else if (messageObject.isMusic()) {
                    return LocaleController.formatString("ChannelMessageMusic", R.string.ChannelMessageMusic, name);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaContact) {
                    return LocaleController.formatString("ChannelMessageContact", R.string.ChannelMessageContact, name);
                } else if ((messageObject.messageOwner.media instanceof TL_messageMediaGeo) || (messageObject.messageOwner.media instanceof TL_messageMediaVenue)) {
                    return LocaleController.formatString("ChannelMessageMap", R.string.ChannelMessageMap, name);
                } else if (messageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                    return LocaleController.formatString("ChannelMessageLiveLocation", R.string.ChannelMessageLiveLocation, name);
                } else if (!(messageObject.messageOwner.media instanceof TL_messageMediaDocument)) {
                    return null;
                } else {
                    if (messageObject.isSticker()) {
                        if (messageObject.getStickerEmoji() != null) {
                            return LocaleController.formatString("ChannelMessageStickerEmoji", R.string.ChannelMessageStickerEmoji, name, messageObject.getStickerEmoji());
                        }
                        return LocaleController.formatString("ChannelMessageSticker", R.string.ChannelMessageSticker, name);
                    } else if (messageObject.isGif()) {
                        if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                            return LocaleController.formatString("ChannelMessageGIF", R.string.ChannelMessageGIF, name);
                        }
                        msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, "🎬 " + messageObject.messageOwner.message);
                        text[0] = true;
                        return msg;
                    } else if (shortMessage || VERSION.SDK_INT < 19 || TextUtils.isEmpty(messageObject.messageOwner.message)) {
                        return LocaleController.formatString("ChannelMessageDocument", R.string.ChannelMessageDocument, name);
                    } else {
                        msg = LocaleController.formatString("NotificationMessageText", R.string.NotificationMessageText, name, "📎 " + messageObject.messageOwner.message);
                        text[0] = true;
                        return msg;
                    }
                }
            } else if (!ChatObject.isChannel(chat) || chat.megagroup) {
                return LocaleController.formatString("NotificationMessageGroupNoText", R.string.NotificationMessageGroupNoText, name, chat.title);
            } else {
                return LocaleController.formatString("ChannelMessageNoText", R.string.ChannelMessageNoText, name, chat.title);
            }
        }
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
            FileLog.e(e);
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
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void playInChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            try {
                if (getNotifyOverride(MessagesController.getNotificationsSettings(this.currentAccount), this.opened_dialog_id) != 2) {
                    notificationsQueue.postRunnable(new Runnable() {
                        public void run() {
                            if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundPlay) > 500) {
                                try {
                                    if (NotificationsController.this.soundPool == null) {
                                        NotificationsController.this.soundPool = new SoundPool(3, 1, 0);
                                        NotificationsController.this.soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
                                            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                                                if (status == 0) {
                                                    try {
                                                        soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
                                                    } catch (Throwable e) {
                                                        FileLog.e(e);
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    if (NotificationsController.this.soundIn == 0 && !NotificationsController.this.soundInLoaded) {
                                        NotificationsController.this.soundInLoaded = true;
                                        NotificationsController.this.soundIn = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_in, 1);
                                    }
                                    if (NotificationsController.this.soundIn != 0) {
                                        try {
                                            NotificationsController.this.soundPool.play(NotificationsController.this.soundIn, 1.0f, 1.0f, 1, 0, 1.0f);
                                        } catch (Throwable e) {
                                            FileLog.e(e);
                                        }
                                    }
                                } catch (Throwable e2) {
                                    FileLog.e(e2);
                                }
                            }
                        }
                    });
                }
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
    }

    private void scheduleNotificationDelay(boolean onlineReason) {
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("delay notification start, onlineReason = " + onlineReason);
            }
            this.notificationDelayWakelock.acquire(10000);
            AndroidUtilities.cancelRunOnUIThread(this.notificationDelayRunnable);
            AndroidUtilities.runOnUIThread(this.notificationDelayRunnable, (long) (onlineReason ? 3000 : 1000));
        } catch (Throwable e) {
            FileLog.e(e);
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
                AudioAttributes.Builder builder = new AudioAttributes.Builder();
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
            Builder mBuilder;
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
                    if (chat == null) {
                        chatName = chat.title;
                    } else {
                        chatName = UserObject.getUserName(user);
                    }
                    if (((int) dialog_id) != 0 || this.pushDialogs.size() > 1 || AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
                        name = LocaleController.getString("AppName", R.string.AppName);
                        replace = false;
                    } else {
                        name = chatName;
                    }
                    if (UserConfig.getActivatedAccountsCount() > 1) {
                        detailText = TtmlNode.ANONYMOUS_REGION_ID;
                    } else if (this.pushDialogs.size() != 1) {
                        detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser());
                    } else {
                        detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser()) + "・";
                    }
                    if (this.pushDialogs.size() != 1 || VERSION.SDK_INT < 23) {
                        if (this.pushDialogs.size() != 1) {
                            detailText = detailText + LocaleController.formatPluralString("NewMessages", this.total_unread_count);
                        } else {
                            detailText = detailText + LocaleController.formatString("NotificationMessagesPeopleDisplayOrder", R.string.NotificationMessagesPeopleDisplayOrder, LocaleController.formatPluralString("NewMessages", this.total_unread_count), LocaleController.formatPluralString("FromChats", this.pushDialogs.size()));
                        }
                    }
                    mBuilder = new Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(R.drawable.notification).setAutoCancel(true).setNumber(this.total_unread_count).setContentIntent(contentIntent).setGroup(this.notificationGroup).setGroupSummary(true).setWhen(((long) lastMessageObject.messageOwner.date) * 1000).setColor(-13851168);
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
                    if (VERSION.SDK_INT < 24 && SharedConfig.passcodeHash.length() == 0 && hasMessagesToReply()) {
                        intent = new Intent(ApplicationLoader.applicationContext, PopupReplyReceiver.class);
                        intent.putExtra("currentAccount", this.currentAccount);
                        if (VERSION.SDK_INT > 19) {
                            mBuilder.addAction(R.drawable.ic_ab_reply2, LocaleController.getString("Reply", R.string.Reply), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, 134217728));
                        } else {
                            mBuilder.addAction(R.drawable.ic_ab_reply, LocaleController.getString("Reply", R.string.Reply), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, 134217728));
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
                FileLog.e(e);
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
            if (chat == null) {
                chatName = UserObject.getUserName(user);
            } else {
                chatName = chat.title;
            }
            if (((int) dialog_id) != 0) {
            }
            name = LocaleController.getString("AppName", R.string.AppName);
            replace = false;
            if (UserConfig.getActivatedAccountsCount() > 1) {
                detailText = TtmlNode.ANONYMOUS_REGION_ID;
            } else if (this.pushDialogs.size() != 1) {
                detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser()) + "・";
            } else {
                detailText = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser());
            }
            if (this.pushDialogs.size() != 1) {
                detailText = detailText + LocaleController.formatString("NotificationMessagesPeopleDisplayOrder", R.string.NotificationMessagesPeopleDisplayOrder, LocaleController.formatPluralString("NewMessages", this.total_unread_count), LocaleController.formatPluralString("FromChats", this.pushDialogs.size()));
            } else {
                detailText = detailText + LocaleController.formatPluralString("NewMessages", this.total_unread_count);
            }
            mBuilder = new Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(R.drawable.notification).setAutoCancel(true).setNumber(this.total_unread_count).setContentIntent(contentIntent).setGroup(this.notificationGroup).setGroupSummary(true).setWhen(((long) lastMessageObject.messageOwner.date) * 1000).setColor(-13851168);
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
            intent = new Intent(ApplicationLoader.applicationContext, PopupReplyReceiver.class);
            intent.putExtra("currentAccount", this.currentAccount);
            if (VERSION.SDK_INT > 19) {
                mBuilder.addAction(R.drawable.ic_ab_reply, LocaleController.getString("Reply", R.string.Reply), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, 134217728));
            } else {
                mBuilder.addAction(R.drawable.ic_ab_reply2, LocaleController.getString("Reply", R.string.Reply), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, intent, 134217728));
            }
            if (VERSION.SDK_INT >= 26) {
                mBuilder.setChannelId(validateChannelId(dialog_id, chatName, vibrationPattern, ledColor, sound, importance, configVibrationPattern, configSound, configImportance));
            }
            showExtraNotifications(mBuilder, notifyAboutLast, detailText);
            this.lastNotificationIsNoData = false;
            scheduleNotificationRepeat();
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
    }

    @SuppressLint({"InlinedApi"})
    private void showExtraNotifications(Builder notificationBuilder, boolean notifyAboutLast, String summary) {
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
        int size = sortedDialogs.size();
        for (int b = 0; b < size; b++) {
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
            int max_date = ((MessageObject) messageObjects.get(0)).messageOwner.date;
            Chat chat = null;
            User user = null;
            TLObject photoPath = null;
            boolean canReply;
            String name;
            UnreadConversation.Builder unreadConvBuilder;
            Intent intent;
            Action wearReplyAction;
            PendingIntent replyPendingIntent;
            RemoteInput remoteInputWear;
            String replyToString;
            Integer count;
            Style messagingStyle;
            StringBuilder text;
            boolean[] isText;
            String message;
            PendingIntent contentIntent;
            WearableExtender wearableExtender;
            String dismissalID;
            WearableExtender summaryExtender;
            long date;
            Builder builder;
            BitmapDrawable img;
            File file;
            float scaleFactor;
            Options options;
            int i;
            Bitmap bitmap;
            if (lowerId != 0) {
                canReply = true;
                if (lowerId > 0) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lowerId));
                    if (user != null) {
                        name = UserObject.getUserName(user);
                        if (!(user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0)) {
                            photoPath = user.photo.photo_small;
                        }
                        if (AndroidUtilities.needShowPasscode(false) || SharedConfig.isWaitingForPasscodeEnter) {
                            name = LocaleController.getString("AppName", R.string.AppName);
                            photoPath = null;
                            canReply = false;
                        }
                        unreadConvBuilder = new UnreadConversation.Builder(name).setLatestTimestamp(((long) max_date) * 1000);
                        intent = new Intent(ApplicationLoader.applicationContext, AutoMessageHeardReceiver.class);
                        intent.addFlags(32);
                        intent.setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
                        intent.putExtra("dialog_id", dialog_id);
                        intent.putExtra("max_id", max_id);
                        intent.putExtra("currentAccount", this.currentAccount);
                        unreadConvBuilder.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728));
                        wearReplyAction = null;
                        if ((!ChatObject.isChannel(chat) || (chat != null && chat.megagroup)) && canReply && !SharedConfig.isWaitingForPasscodeEnter) {
                            intent = new Intent(ApplicationLoader.applicationContext, AutoMessageReplyReceiver.class);
                            intent.addFlags(32);
                            intent.setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
                            intent.putExtra("dialog_id", dialog_id);
                            intent.putExtra("max_id", max_id);
                            intent.putExtra("currentAccount", this.currentAccount);
                            unreadConvBuilder.setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728), new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", R.string.Reply)).build());
                            intent = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                            intent.putExtra("dialog_id", dialog_id);
                            intent.putExtra("max_id", max_id);
                            intent.putExtra("currentAccount", this.currentAccount);
                            replyPendingIntent = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728);
                            remoteInputWear = new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", R.string.Reply)).build();
                            if (chat != null) {
                                replyToString = LocaleController.formatString("ReplyToGroup", R.string.ReplyToGroup, name);
                            } else {
                                replyToString = LocaleController.formatString("ReplyToUser", R.string.ReplyToUser, name);
                            }
                            wearReplyAction = new Action.Builder(R.drawable.ic_reply_icon, replyToString, replyPendingIntent).setAllowGeneratedReplies(true).addRemoteInput(remoteInputWear).build();
                        }
                        count = (Integer) this.pushDialogs.get(dialog_id);
                        if (count == null) {
                            count = Integer.valueOf(0);
                        }
                        messagingStyle = new MessagingStyle(TtmlNode.ANONYMOUS_REGION_ID).setConversationTitle(String.format("%1$s (%2$s)", new Object[]{name, LocaleController.formatPluralString("NewMessages", Math.max(count.intValue(), messageObjects.size()))}));
                        text = new StringBuilder();
                        isText = new boolean[1];
                        for (a = messageObjects.size() - 1; a >= 0; a--) {
                            messageObject = (MessageObject) messageObjects.get(a);
                            message = getStringForMessage(messageObject, false, isText);
                            if (message != null) {
                                if (chat == null) {
                                    message = message.replace(" @ " + name, TtmlNode.ANONYMOUS_REGION_ID);
                                } else if (isText[0]) {
                                    message = message.replace(name + ": ", TtmlNode.ANONYMOUS_REGION_ID);
                                } else {
                                    message = message.replace(name + " ", TtmlNode.ANONYMOUS_REGION_ID);
                                }
                                if (text.length() > 0) {
                                    text.append("\n\n");
                                }
                                text.append(message);
                                unreadConvBuilder.addMessage(message);
                                messagingStyle.addMessage(message, ((long) messageObject.messageOwner.date) * 1000, null);
                            }
                        }
                        intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                        intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                        intent.setFlags(32768);
                        if (lowerId != 0) {
                            intent.putExtra("encId", highId);
                        } else if (lowerId > 0) {
                            intent.putExtra("userId", lowerId);
                        } else {
                            intent.putExtra("chatId", -lowerId);
                        }
                        intent.putExtra("currentAccount", this.currentAccount);
                        contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM);
                        wearableExtender = new WearableExtender();
                        if (wearReplyAction != null) {
                            wearableExtender.addAction(wearReplyAction);
                        }
                        if (lowerId != 0) {
                            dismissalID = "tgenc" + highId + "_" + max_id;
                        } else if (lowerId > 0) {
                            dismissalID = "tguser" + lowerId + "_" + max_id;
                        } else {
                            dismissalID = "tgchat" + (-lowerId) + "_" + max_id;
                        }
                        wearableExtender.setDismissalId(dismissalID);
                        summaryExtender = new WearableExtender();
                        summaryExtender.setDismissalId("summary_" + dismissalID);
                        notificationBuilder.extend(summaryExtender);
                        date = ((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000;
                        builder = new Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(R.drawable.notification).setGroup(this.notificationGroup).setContentText(text.toString()).setAutoCancel(true).setNumber(messageObjects.size()).setColor(-13851168).setGroupSummary(false).setWhen(date).setShortcutId("sdid_" + dialog_id).setGroupAlertBehavior(1).setStyle(messagingStyle).setContentIntent(contentIntent).extend(wearableExtender).setSortKey(TtmlNode.ANONYMOUS_REGION_ID + (Long.MAX_VALUE - date)).extend(new CarExtender().setUnreadConversation(unreadConvBuilder.build())).setCategory("msg");
                        if (this.pushDialogs.size() == 1 && !TextUtils.isEmpty(summary)) {
                            builder.setSubText(summary);
                        }
                        if (lowerId == 0) {
                            builder.setLocalOnly(true);
                        }
                        if (photoPath != null) {
                            img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                            if (img != null) {
                                builder.setLargeIcon(img.getBitmap());
                            } else {
                                try {
                                    file = FileLoader.getPathToAttach(photoPath, true);
                                    if (file.exists()) {
                                        scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                                        options = new Options();
                                        if (scaleFactor < 1.0f) {
                                            i = 1;
                                        } else {
                                            i = (int) scaleFactor;
                                        }
                                        options.inSampleSize = i;
                                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                                        if (bitmap != null) {
                                            builder.setLargeIcon(bitmap);
                                        }
                                    }
                                } catch (Throwable th) {
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
                    }
                } else {
                    chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lowerId));
                    if (chat != null) {
                        name = chat.title;
                        if (!(chat.photo == null || chat.photo.photo_small == null || chat.photo.photo_small.volume_id == 0 || chat.photo.photo_small.local_id == 0)) {
                            photoPath = chat.photo.photo_small;
                        }
                        name = LocaleController.getString("AppName", R.string.AppName);
                        photoPath = null;
                        canReply = false;
                        unreadConvBuilder = new UnreadConversation.Builder(name).setLatestTimestamp(((long) max_date) * 1000);
                        intent = new Intent(ApplicationLoader.applicationContext, AutoMessageHeardReceiver.class);
                        intent.addFlags(32);
                        intent.setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
                        intent.putExtra("dialog_id", dialog_id);
                        intent.putExtra("max_id", max_id);
                        intent.putExtra("currentAccount", this.currentAccount);
                        unreadConvBuilder.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728));
                        wearReplyAction = null;
                        intent = new Intent(ApplicationLoader.applicationContext, AutoMessageReplyReceiver.class);
                        intent.addFlags(32);
                        intent.setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
                        intent.putExtra("dialog_id", dialog_id);
                        intent.putExtra("max_id", max_id);
                        intent.putExtra("currentAccount", this.currentAccount);
                        unreadConvBuilder.setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728), new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", R.string.Reply)).build());
                        intent = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                        intent.putExtra("dialog_id", dialog_id);
                        intent.putExtra("max_id", max_id);
                        intent.putExtra("currentAccount", this.currentAccount);
                        replyPendingIntent = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728);
                        remoteInputWear = new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", R.string.Reply)).build();
                        if (chat != null) {
                            replyToString = LocaleController.formatString("ReplyToUser", R.string.ReplyToUser, name);
                        } else {
                            replyToString = LocaleController.formatString("ReplyToGroup", R.string.ReplyToGroup, name);
                        }
                        wearReplyAction = new Action.Builder(R.drawable.ic_reply_icon, replyToString, replyPendingIntent).setAllowGeneratedReplies(true).addRemoteInput(remoteInputWear).build();
                        count = (Integer) this.pushDialogs.get(dialog_id);
                        if (count == null) {
                            count = Integer.valueOf(0);
                        }
                        messagingStyle = new MessagingStyle(TtmlNode.ANONYMOUS_REGION_ID).setConversationTitle(String.format("%1$s (%2$s)", new Object[]{name, LocaleController.formatPluralString("NewMessages", Math.max(count.intValue(), messageObjects.size()))}));
                        text = new StringBuilder();
                        isText = new boolean[1];
                        for (a = messageObjects.size() - 1; a >= 0; a--) {
                            messageObject = (MessageObject) messageObjects.get(a);
                            message = getStringForMessage(messageObject, false, isText);
                            if (message != null) {
                                if (chat == null) {
                                    message = message.replace(" @ " + name, TtmlNode.ANONYMOUS_REGION_ID);
                                } else if (isText[0]) {
                                    message = message.replace(name + " ", TtmlNode.ANONYMOUS_REGION_ID);
                                } else {
                                    message = message.replace(name + ": ", TtmlNode.ANONYMOUS_REGION_ID);
                                }
                                if (text.length() > 0) {
                                    text.append("\n\n");
                                }
                                text.append(message);
                                unreadConvBuilder.addMessage(message);
                                messagingStyle.addMessage(message, ((long) messageObject.messageOwner.date) * 1000, null);
                            }
                        }
                        intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                        intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                        intent.setFlags(32768);
                        if (lowerId != 0) {
                            intent.putExtra("encId", highId);
                        } else if (lowerId > 0) {
                            intent.putExtra("chatId", -lowerId);
                        } else {
                            intent.putExtra("userId", lowerId);
                        }
                        intent.putExtra("currentAccount", this.currentAccount);
                        contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM);
                        wearableExtender = new WearableExtender();
                        if (wearReplyAction != null) {
                            wearableExtender.addAction(wearReplyAction);
                        }
                        if (lowerId != 0) {
                            dismissalID = "tgenc" + highId + "_" + max_id;
                        } else if (lowerId > 0) {
                            dismissalID = "tgchat" + (-lowerId) + "_" + max_id;
                        } else {
                            dismissalID = "tguser" + lowerId + "_" + max_id;
                        }
                        wearableExtender.setDismissalId(dismissalID);
                        summaryExtender = new WearableExtender();
                        summaryExtender.setDismissalId("summary_" + dismissalID);
                        notificationBuilder.extend(summaryExtender);
                        date = ((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000;
                        builder = new Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(R.drawable.notification).setGroup(this.notificationGroup).setContentText(text.toString()).setAutoCancel(true).setNumber(messageObjects.size()).setColor(-13851168).setGroupSummary(false).setWhen(date).setShortcutId("sdid_" + dialog_id).setGroupAlertBehavior(1).setStyle(messagingStyle).setContentIntent(contentIntent).extend(wearableExtender).setSortKey(TtmlNode.ANONYMOUS_REGION_ID + (Long.MAX_VALUE - date)).extend(new CarExtender().setUnreadConversation(unreadConvBuilder.build())).setCategory("msg");
                        builder.setSubText(summary);
                        if (lowerId == 0) {
                            builder.setLocalOnly(true);
                        }
                        if (photoPath != null) {
                            img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                            if (img != null) {
                                file = FileLoader.getPathToAttach(photoPath, true);
                                if (file.exists()) {
                                    scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                                    options = new Options();
                                    if (scaleFactor < 1.0f) {
                                        i = (int) scaleFactor;
                                    } else {
                                        i = 1;
                                    }
                                    options.inSampleSize = i;
                                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                                    if (bitmap != null) {
                                        builder.setLargeIcon(bitmap);
                                    }
                                }
                            } else {
                                builder.setLargeIcon(img.getBitmap());
                            }
                        }
                        builder.addPerson("tel:+" + user.phone);
                        if (VERSION.SDK_INT >= 26) {
                            builder.setChannelId(OTHER_NOTIFICATIONS_CHANNEL);
                        }
                        holders.add(new AnonymousClass1NotificationHolder(internalId.intValue(), builder.build()));
                        this.wearNotificationsIds.put(dialog_id, internalId);
                    }
                }
            } else {
                canReply = false;
                EncryptedChat encryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(highId));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                    if (user != null) {
                        name = LocaleController.getString("SecretChatName", R.string.SecretChatName);
                        photoPath = null;
                        name = LocaleController.getString("AppName", R.string.AppName);
                        photoPath = null;
                        canReply = false;
                        unreadConvBuilder = new UnreadConversation.Builder(name).setLatestTimestamp(((long) max_date) * 1000);
                        intent = new Intent(ApplicationLoader.applicationContext, AutoMessageHeardReceiver.class);
                        intent.addFlags(32);
                        intent.setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
                        intent.putExtra("dialog_id", dialog_id);
                        intent.putExtra("max_id", max_id);
                        intent.putExtra("currentAccount", this.currentAccount);
                        unreadConvBuilder.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728));
                        wearReplyAction = null;
                        intent = new Intent(ApplicationLoader.applicationContext, AutoMessageReplyReceiver.class);
                        intent.addFlags(32);
                        intent.setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
                        intent.putExtra("dialog_id", dialog_id);
                        intent.putExtra("max_id", max_id);
                        intent.putExtra("currentAccount", this.currentAccount);
                        unreadConvBuilder.setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728), new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", R.string.Reply)).build());
                        intent = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                        intent.putExtra("dialog_id", dialog_id);
                        intent.putExtra("max_id", max_id);
                        intent.putExtra("currentAccount", this.currentAccount);
                        replyPendingIntent = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, internalId.intValue(), intent, 134217728);
                        remoteInputWear = new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(LocaleController.getString("Reply", R.string.Reply)).build();
                        if (chat != null) {
                            replyToString = LocaleController.formatString("ReplyToGroup", R.string.ReplyToGroup, name);
                        } else {
                            replyToString = LocaleController.formatString("ReplyToUser", R.string.ReplyToUser, name);
                        }
                        wearReplyAction = new Action.Builder(R.drawable.ic_reply_icon, replyToString, replyPendingIntent).setAllowGeneratedReplies(true).addRemoteInput(remoteInputWear).build();
                        count = (Integer) this.pushDialogs.get(dialog_id);
                        if (count == null) {
                            count = Integer.valueOf(0);
                        }
                        messagingStyle = new MessagingStyle(TtmlNode.ANONYMOUS_REGION_ID).setConversationTitle(String.format("%1$s (%2$s)", new Object[]{name, LocaleController.formatPluralString("NewMessages", Math.max(count.intValue(), messageObjects.size()))}));
                        text = new StringBuilder();
                        isText = new boolean[1];
                        for (a = messageObjects.size() - 1; a >= 0; a--) {
                            messageObject = (MessageObject) messageObjects.get(a);
                            message = getStringForMessage(messageObject, false, isText);
                            if (message != null) {
                                if (chat == null) {
                                    message = message.replace(" @ " + name, TtmlNode.ANONYMOUS_REGION_ID);
                                } else if (isText[0]) {
                                    message = message.replace(name + ": ", TtmlNode.ANONYMOUS_REGION_ID);
                                } else {
                                    message = message.replace(name + " ", TtmlNode.ANONYMOUS_REGION_ID);
                                }
                                if (text.length() > 0) {
                                    text.append("\n\n");
                                }
                                text.append(message);
                                unreadConvBuilder.addMessage(message);
                                messagingStyle.addMessage(message, ((long) messageObject.messageOwner.date) * 1000, null);
                            }
                        }
                        intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                        intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                        intent.setFlags(32768);
                        if (lowerId != 0) {
                            intent.putExtra("encId", highId);
                        } else if (lowerId > 0) {
                            intent.putExtra("userId", lowerId);
                        } else {
                            intent.putExtra("chatId", -lowerId);
                        }
                        intent.putExtra("currentAccount", this.currentAccount);
                        contentIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, intent, NUM);
                        wearableExtender = new WearableExtender();
                        if (wearReplyAction != null) {
                            wearableExtender.addAction(wearReplyAction);
                        }
                        if (lowerId != 0) {
                            dismissalID = "tgenc" + highId + "_" + max_id;
                        } else if (lowerId > 0) {
                            dismissalID = "tguser" + lowerId + "_" + max_id;
                        } else {
                            dismissalID = "tgchat" + (-lowerId) + "_" + max_id;
                        }
                        wearableExtender.setDismissalId(dismissalID);
                        summaryExtender = new WearableExtender();
                        summaryExtender.setDismissalId("summary_" + dismissalID);
                        notificationBuilder.extend(summaryExtender);
                        date = ((long) ((MessageObject) messageObjects.get(0)).messageOwner.date) * 1000;
                        builder = new Builder(ApplicationLoader.applicationContext).setContentTitle(name).setSmallIcon(R.drawable.notification).setGroup(this.notificationGroup).setContentText(text.toString()).setAutoCancel(true).setNumber(messageObjects.size()).setColor(-13851168).setGroupSummary(false).setWhen(date).setShortcutId("sdid_" + dialog_id).setGroupAlertBehavior(1).setStyle(messagingStyle).setContentIntent(contentIntent).extend(wearableExtender).setSortKey(TtmlNode.ANONYMOUS_REGION_ID + (Long.MAX_VALUE - date)).extend(new CarExtender().setUnreadConversation(unreadConvBuilder.build())).setCategory("msg");
                        builder.setSubText(summary);
                        if (lowerId == 0) {
                            builder.setLocalOnly(true);
                        }
                        if (photoPath != null) {
                            img = ImageLoader.getInstance().getImageFromMemory(photoPath, null, "50_50");
                            if (img != null) {
                                builder.setLargeIcon(img.getBitmap());
                            } else {
                                file = FileLoader.getPathToAttach(photoPath, true);
                                if (file.exists()) {
                                    scaleFactor = 160.0f / ((float) AndroidUtilities.dp(50.0f));
                                    options = new Options();
                                    if (scaleFactor < 1.0f) {
                                        i = 1;
                                    } else {
                                        i = (int) scaleFactor;
                                    }
                                    options.inSampleSize = i;
                                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                                    if (bitmap != null) {
                                        builder.setLargeIcon(bitmap);
                                    }
                                }
                            }
                        }
                        builder.addPerson("tel:+" + user.phone);
                        if (VERSION.SDK_INT >= 26) {
                            builder.setChannelId(OTHER_NOTIFICATIONS_CHANNEL);
                        }
                        holders.add(new AnonymousClass1NotificationHolder(internalId.intValue(), builder.build()));
                        this.wearNotificationsIds.put(dialog_id, internalId);
                    }
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
    }

    public void playOutChatSound() {
        if (this.inChatSoundEnabled && !MediaController.getInstance().isRecordingAudio()) {
            try {
                if (audioManager.getRingerMode() == 0) {
                    return;
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            notificationsQueue.postRunnable(new Runnable() {
                public void run() {
                    try {
                        if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundOutPlay) > 100) {
                            NotificationsController.this.lastSoundOutPlay = System.currentTimeMillis();
                            if (NotificationsController.this.soundPool == null) {
                                NotificationsController.this.soundPool = new SoundPool(3, 1, 0);
                                NotificationsController.this.soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
                                    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                                        if (status == 0) {
                                            try {
                                                soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
                                            } catch (Throwable e) {
                                                FileLog.e(e);
                                            }
                                        }
                                    }
                                });
                            }
                            if (NotificationsController.this.soundOut == 0 && !NotificationsController.this.soundOutLoaded) {
                                NotificationsController.this.soundOutLoaded = true;
                                NotificationsController.this.soundOut = NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, R.raw.sound_out, 1);
                            }
                            if (NotificationsController.this.soundOut != 0) {
                                try {
                                    NotificationsController.this.soundPool.play(NotificationsController.this.soundOut, 1.0f, 1.0f, 1, 0, 1.0f);
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.e(e2);
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
