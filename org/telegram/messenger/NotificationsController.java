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
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.app.NotificationCompat.Action.Builder;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.CarExtender;
import android.support.v4.app.NotificationCompat.CarExtender.UnreadConversation.Builder;
import android.support.v4.app.NotificationCompat.Extender;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.app.NotificationCompat.MessagingStyle;
import android.support.v4.app.NotificationCompat.Style;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.RemoteInput.Builder;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_game;
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
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PopupNotificationActivity;

public class NotificationsController
{
  public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
  private static volatile NotificationsController[] Instance = new NotificationsController[3];
  public static final String OTHER_NOTIFICATIONS_CHANNEL = "Other3";
  protected static AudioManager audioManager;
  public static long lastNoDataNotificationTime;
  private static NotificationManagerCompat notificationManager;
  private static DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
  private static NotificationManager systemNotificationManager;
  private AlarmManager alarmManager;
  private int currentAccount;
  private ArrayList<MessageObject> delayedPushMessages = new ArrayList();
  private boolean inChatSoundEnabled = true;
  private int lastBadgeCount = -1;
  private int lastButtonId = 5000;
  private boolean lastNotificationIsNoData;
  private int lastOnlineFromOtherDevice = 0;
  private long lastSoundOutPlay;
  private long lastSoundPlay;
  private LongSparseArray<Integer> lastWearNotifiedMessageId = new LongSparseArray();
  private String launcherClassName;
  private Runnable notificationDelayRunnable;
  private PowerManager.WakeLock notificationDelayWakelock;
  private String notificationGroup;
  private int notificationId;
  private boolean notifyCheck = false;
  private long opened_dialog_id = 0L;
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
  
  static
  {
    notificationManager = null;
    systemNotificationManager = null;
    if ((Build.VERSION.SDK_INT >= 26) && (ApplicationLoader.applicationContext != null))
    {
      notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
      systemNotificationManager = (NotificationManager)ApplicationLoader.applicationContext.getSystemService("notification");
      NotificationChannel localNotificationChannel = new NotificationChannel("Other3", "Other", 3);
      localNotificationChannel.enableLights(false);
      localNotificationChannel.enableVibration(false);
      localNotificationChannel.setSound(null, null);
      systemNotificationManager.createNotificationChannel(localNotificationChannel);
    }
    audioManager = (AudioManager)ApplicationLoader.applicationContext.getSystemService("audio");
  }
  
  public NotificationsController(int paramInt)
  {
    this.currentAccount = paramInt;
    this.notificationId = (this.currentAccount + 1);
    StringBuilder localStringBuilder = new StringBuilder().append("messages");
    Object localObject;
    if (this.currentAccount == 0) {
      localObject = "";
    }
    for (;;)
    {
      this.notificationGroup = localObject;
      localObject = MessagesController.getNotificationsSettings(this.currentAccount);
      this.inChatSoundEnabled = ((SharedPreferences)localObject).getBoolean("EnableInChatSound", true);
      this.showBadgeNumber = ((SharedPreferences)localObject).getBoolean("badgeNumber", true);
      notificationManager = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
      systemNotificationManager = (NotificationManager)ApplicationLoader.applicationContext.getSystemService("notification");
      try
      {
        audioManager = (AudioManager)ApplicationLoader.applicationContext.getSystemService("audio");
      }
      catch (Exception localException2)
      {
        try
        {
          this.alarmManager = ((AlarmManager)ApplicationLoader.applicationContext.getSystemService("alarm"));
        }
        catch (Exception localException2)
        {
          try
          {
            for (;;)
            {
              this.notificationDelayWakelock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "lock");
              this.notificationDelayWakelock.setReferenceCounted(false);
              this.notificationDelayRunnable = new Runnable()
              {
                public void run()
                {
                  if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("delay reached");
                  }
                  if (!NotificationsController.this.delayedPushMessages.isEmpty())
                  {
                    NotificationsController.this.showOrUpdateNotification(true);
                    NotificationsController.this.delayedPushMessages.clear();
                  }
                  try
                  {
                    for (;;)
                    {
                      if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
                        NotificationsController.this.notificationDelayWakelock.release();
                      }
                      return;
                      if (NotificationsController.this.lastNotificationIsNoData) {
                        NotificationsController.notificationManager.cancel(NotificationsController.this.notificationId);
                      }
                    }
                  }
                  catch (Exception localException)
                  {
                    for (;;)
                    {
                      FileLog.e(localException);
                    }
                  }
                }
              };
              return;
              localObject = Integer.valueOf(this.currentAccount);
              break;
              localException1 = localException1;
              FileLog.e(localException1);
              continue;
              localException2 = localException2;
              FileLog.e(localException2);
            }
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              FileLog.e(localException3);
            }
          }
        }
      }
    }
  }
  
  /* Error */
  private void dismissNotification()
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: putfield 371	org/telegram/messenger/NotificationsController:lastNotificationIsNoData	Z
    //   5: getstatic 147	org/telegram/messenger/NotificationsController:notificationManager	Landroid/support/v4/app/NotificationManagerCompat;
    //   8: aload_0
    //   9: getfield 258	org/telegram/messenger/NotificationsController:notificationId	I
    //   12: invokevirtual 449	android/support/v4/app/NotificationManagerCompat:cancel	(I)V
    //   15: aload_0
    //   16: getfield 217	org/telegram/messenger/NotificationsController:pushMessages	Ljava/util/ArrayList;
    //   19: invokevirtual 452	java/util/ArrayList:clear	()V
    //   22: aload_0
    //   23: getfield 224	org/telegram/messenger/NotificationsController:pushMessagesDict	Landroid/util/LongSparseArray;
    //   26: invokevirtual 453	android/util/LongSparseArray:clear	()V
    //   29: aload_0
    //   30: getfield 232	org/telegram/messenger/NotificationsController:lastWearNotifiedMessageId	Landroid/util/LongSparseArray;
    //   33: invokevirtual 453	android/util/LongSparseArray:clear	()V
    //   36: iconst_0
    //   37: istore_1
    //   38: iload_1
    //   39: aload_0
    //   40: getfield 230	org/telegram/messenger/NotificationsController:wearNotificationsIds	Landroid/util/LongSparseArray;
    //   43: invokevirtual 456	android/util/LongSparseArray:size	()I
    //   46: if_icmpge +29 -> 75
    //   49: getstatic 147	org/telegram/messenger/NotificationsController:notificationManager	Landroid/support/v4/app/NotificationManagerCompat;
    //   52: aload_0
    //   53: getfield 230	org/telegram/messenger/NotificationsController:wearNotificationsIds	Landroid/util/LongSparseArray;
    //   56: iload_1
    //   57: invokevirtual 460	android/util/LongSparseArray:valueAt	(I)Ljava/lang/Object;
    //   60: checkcast 326	java/lang/Integer
    //   63: invokevirtual 463	java/lang/Integer:intValue	()I
    //   66: invokevirtual 449	android/support/v4/app/NotificationManagerCompat:cancel	(I)V
    //   69: iinc 1 1
    //   72: goto -34 -> 38
    //   75: aload_0
    //   76: getfield 230	org/telegram/messenger/NotificationsController:wearNotificationsIds	Landroid/util/LongSparseArray;
    //   79: invokevirtual 453	android/util/LongSparseArray:clear	()V
    //   82: new 20	org/telegram/messenger/NotificationsController$13
    //   85: astore_2
    //   86: aload_2
    //   87: aload_0
    //   88: invokespecial 464	org/telegram/messenger/NotificationsController$13:<init>	(Lorg/telegram/messenger/NotificationsController;)V
    //   91: aload_2
    //   92: invokestatic 470	org/telegram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
    //   95: invokestatic 476	org/telegram/messenger/WearDataLayerListenerService:isWatchConnected	()Z
    //   98: istore_3
    //   99: iload_3
    //   100: ifeq +57 -> 157
    //   103: new 478	org/json/JSONObject
    //   106: astore_2
    //   107: aload_2
    //   108: invokespecial 479	org/json/JSONObject:<init>	()V
    //   111: aload_2
    //   112: ldc_w 481
    //   115: aload_0
    //   116: getfield 256	org/telegram/messenger/NotificationsController:currentAccount	I
    //   119: invokestatic 487	org/telegram/messenger/UserConfig:getInstance	(I)Lorg/telegram/messenger/UserConfig;
    //   122: invokevirtual 490	org/telegram/messenger/UserConfig:getClientUserId	()I
    //   125: invokevirtual 494	org/json/JSONObject:put	(Ljava/lang/String;I)Lorg/json/JSONObject;
    //   128: pop
    //   129: aload_2
    //   130: ldc_w 496
    //   133: iconst_1
    //   134: invokevirtual 499	org/json/JSONObject:put	(Ljava/lang/String;Z)Lorg/json/JSONObject;
    //   137: pop
    //   138: ldc_w 501
    //   141: aload_2
    //   142: invokevirtual 502	org/json/JSONObject:toString	()Ljava/lang/String;
    //   145: ldc_w 504
    //   148: invokevirtual 510	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   151: ldc_w 512
    //   154: invokestatic 516	org/telegram/messenger/WearDataLayerListenerService:sendMessageToWatch	(Ljava/lang/String;[BLjava/lang/String;)V
    //   157: return
    //   158: astore_2
    //   159: aload_2
    //   160: invokestatic 336	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   163: goto -6 -> 157
    //   166: astore_2
    //   167: goto -10 -> 157
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	170	0	this	NotificationsController
    //   37	33	1	i	int
    //   85	57	2	localObject	Object
    //   158	2	2	localException	Exception
    //   166	1	2	localJSONException	JSONException
    //   98	2	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   0	36	158	java/lang/Exception
    //   38	69	158	java/lang/Exception
    //   75	99	158	java/lang/Exception
    //   103	157	158	java/lang/Exception
    //   103	157	166	org/json/JSONException
  }
  
  public static NotificationsController getInstance(int paramInt)
  {
    Object localObject1 = Instance[paramInt];
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject1 = Instance[paramInt];
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = Instance;
        localObject2 = new org/telegram/messenger/NotificationsController;
        ((NotificationsController)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (NotificationsController)localObject2;
    }
    finally {}
  }
  
  private int getNotifyOverride(SharedPreferences paramSharedPreferences, long paramLong)
  {
    int i = paramSharedPreferences.getInt("notify2_" + paramLong, 0);
    int j = i;
    if (i == 3)
    {
      j = i;
      if (paramSharedPreferences.getInt("notifyuntil_" + paramLong, 0) >= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
        j = 2;
      }
    }
    return j;
  }
  
  private String getShortStringForMessage(MessageObject paramMessageObject)
  {
    if ((!paramMessageObject.isMediaEmpty()) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message))) {
      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) {
        paramMessageObject = "🖼 " + paramMessageObject.messageOwner.message;
      }
    }
    for (;;)
    {
      return paramMessageObject;
      if (paramMessageObject.isVideo()) {
        paramMessageObject = "📹 " + paramMessageObject.messageOwner.message;
      } else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
      {
        if (paramMessageObject.isGif()) {
          paramMessageObject = "🎬 " + paramMessageObject.messageOwner.message;
        } else {
          paramMessageObject = "📎 " + paramMessageObject.messageOwner.message;
        }
      }
      else {
        paramMessageObject = paramMessageObject.messageText.toString();
      }
    }
  }
  
  private String getStringForMessage(MessageObject paramMessageObject, boolean paramBoolean, boolean[] paramArrayOfBoolean)
  {
    if ((AndroidUtilities.needShowPasscode(false)) || (SharedConfig.isWaitingForPasscodeEnter)) {
      paramMessageObject = LocaleController.getString("YouHaveNewMessage", NUM);
    }
    int i;
    int j;
    label146:
    label245:
    label260:
    int k;
    label288:
    long l2;
    label310:
    String str;
    Object localObject1;
    label396:
    label413:
    label446:
    Object localObject2;
    for (;;)
    {
      return paramMessageObject;
      long l1 = paramMessageObject.messageOwner.dialog_id;
      if (paramMessageObject.messageOwner.to_id.chat_id != 0) {}
      for (i = paramMessageObject.messageOwner.to_id.chat_id;; i = paramMessageObject.messageOwner.to_id.channel_id)
      {
        j = paramMessageObject.messageOwner.to_id.user_id;
        if (!paramMessageObject.isFcmMessage()) {
          break label260;
        }
        if ((i != 0) || (j == 0)) {
          break label146;
        }
        if (MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewAll", true)) {
          break label245;
        }
        paramMessageObject = LocaleController.formatString("NotificationMessageNoText", NUM, new Object[] { paramMessageObject.localName });
        break;
      }
      if ((i != 0) && (!MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewGroup", true)))
      {
        if ((!paramMessageObject.isMegagroup()) && (paramMessageObject.messageOwner.to_id.channel_id != 0)) {
          paramMessageObject = LocaleController.formatString("ChannelMessageNoText", NUM, new Object[] { paramMessageObject.localName });
        } else {
          paramMessageObject = LocaleController.formatString("NotificationMessageGroupNoText", NUM, new Object[] { paramMessageObject.localUserName, paramMessageObject.localName });
        }
      }
      else
      {
        paramArrayOfBoolean[0] = true;
        paramMessageObject = (String)paramMessageObject.messageText;
        continue;
        if (j == 0) {
          if ((paramMessageObject.isFromUser()) || (paramMessageObject.getId() < 0))
          {
            k = paramMessageObject.messageOwner.from_id;
            l2 = l1;
            if (l1 == 0L)
            {
              if (i == 0) {
                break label396;
              }
              l2 = -i;
            }
            str = null;
            if (k <= 0) {
              break label413;
            }
            localObject1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(k));
            if (localObject1 != null) {
              str = UserObject.getUserName((TLRPC.User)localObject1);
            }
          }
        }
        for (;;)
        {
          if (str != null) {
            break label446;
          }
          paramMessageObject = null;
          break;
          k = -i;
          break label288;
          k = j;
          if (j != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            break label288;
          }
          k = paramMessageObject.messageOwner.from_id;
          break label288;
          l2 = l1;
          if (k == 0) {
            break label310;
          }
          l2 = k;
          break label310;
          localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-k));
          if (localObject1 != null) {
            str = ((TLRPC.Chat)localObject1).title;
          }
        }
        localObject2 = null;
        if (i == 0) {
          break;
        }
        localObject1 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
        localObject2 = localObject1;
        if (localObject1 != null) {
          break;
        }
        paramMessageObject = null;
      }
    }
    Object localObject3 = null;
    if ((int)l2 == 0) {
      localObject1 = LocaleController.getString("YouHaveNewMessage", NUM);
    }
    for (;;)
    {
      paramMessageObject = (MessageObject)localObject1;
      break;
      if ((i == 0) && (k != 0))
      {
        if (MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewAll", true))
        {
          if ((paramMessageObject.messageOwner instanceof TLRPC.TL_messageService))
          {
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserJoined))
            {
              localObject1 = LocaleController.formatString("NotificationContactJoined", NUM, new Object[] { str });
            }
            else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
            {
              localObject1 = LocaleController.formatString("NotificationContactNewPhoto", NUM, new Object[] { str });
            }
            else if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
            {
              paramArrayOfBoolean = LocaleController.formatString("formatDateAtTime", NUM, new Object[] { LocaleController.getInstance().formatterYear.format(paramMessageObject.messageOwner.date * 1000L), LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L) });
              localObject1 = LocaleController.formatString("NotificationUnrecognizedDevice", NUM, new Object[] { UserConfig.getInstance(this.currentAccount).getCurrentUser().first_name, paramArrayOfBoolean, paramMessageObject.messageOwner.action.title, paramMessageObject.messageOwner.action.address });
            }
            else if (((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionGameScore)) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent)))
            {
              localObject1 = paramMessageObject.messageText.toString();
            }
            else
            {
              localObject1 = localObject3;
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall))
              {
                paramArrayOfBoolean = paramMessageObject.messageOwner.action.reason;
                localObject1 = localObject3;
                if (!paramMessageObject.isOut())
                {
                  localObject1 = localObject3;
                  if ((paramArrayOfBoolean instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) {
                    localObject1 = LocaleController.getString("CallMessageIncomingMissed", NUM);
                  }
                }
              }
            }
          }
          else if (paramMessageObject.isMediaEmpty())
          {
            if (!paramBoolean)
            {
              if (!TextUtils.isEmpty(paramMessageObject.messageOwner.message))
              {
                localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, paramMessageObject.messageOwner.message });
                paramArrayOfBoolean[0] = true;
              }
              else
              {
                localObject1 = LocaleController.formatString("NotificationMessageNoText", NUM, new Object[] { str });
              }
            }
            else {
              localObject1 = LocaleController.formatString("NotificationMessageNoText", NUM, new Object[] { str });
            }
          }
          else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
          {
            if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
            {
              localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, "🖼 " + paramMessageObject.messageOwner.message });
              paramArrayOfBoolean[0] = true;
            }
            else if (paramMessageObject.messageOwner.media.ttl_seconds != 0)
            {
              localObject1 = LocaleController.formatString("NotificationMessageSDPhoto", NUM, new Object[] { str });
            }
            else
            {
              localObject1 = LocaleController.formatString("NotificationMessagePhoto", NUM, new Object[] { str });
            }
          }
          else if (paramMessageObject.isVideo())
          {
            if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
            {
              localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, "📹 " + paramMessageObject.messageOwner.message });
              paramArrayOfBoolean[0] = true;
            }
            else if (paramMessageObject.messageOwner.media.ttl_seconds != 0)
            {
              localObject1 = LocaleController.formatString("NotificationMessageSDVideo", NUM, new Object[] { str });
            }
            else
            {
              localObject1 = LocaleController.formatString("NotificationMessageVideo", NUM, new Object[] { str });
            }
          }
          else if (paramMessageObject.isGame())
          {
            localObject1 = LocaleController.formatString("NotificationMessageGame", NUM, new Object[] { str, paramMessageObject.messageOwner.media.game.title });
          }
          else if (paramMessageObject.isVoice())
          {
            localObject1 = LocaleController.formatString("NotificationMessageAudio", NUM, new Object[] { str });
          }
          else if (paramMessageObject.isRoundVideo())
          {
            localObject1 = LocaleController.formatString("NotificationMessageRound", NUM, new Object[] { str });
          }
          else if (paramMessageObject.isMusic())
          {
            localObject1 = LocaleController.formatString("NotificationMessageMusic", NUM, new Object[] { str });
          }
          else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
          {
            localObject1 = LocaleController.formatString("NotificationMessageContact", NUM, new Object[] { str });
          }
          else if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
          {
            localObject1 = LocaleController.formatString("NotificationMessageMap", NUM, new Object[] { str });
          }
          else if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive))
          {
            localObject1 = LocaleController.formatString("NotificationMessageLiveLocation", NUM, new Object[] { str });
          }
          else
          {
            localObject1 = localObject3;
            if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
              if (paramMessageObject.isSticker())
              {
                paramMessageObject = paramMessageObject.getStickerEmoji();
                if (paramMessageObject != null) {
                  localObject1 = LocaleController.formatString("NotificationMessageStickerEmoji", NUM, new Object[] { str, paramMessageObject });
                } else {
                  localObject1 = LocaleController.formatString("NotificationMessageSticker", NUM, new Object[] { str });
                }
              }
              else if (paramMessageObject.isGif())
              {
                if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                {
                  localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, "🎬 " + paramMessageObject.messageOwner.message });
                  paramArrayOfBoolean[0] = true;
                }
                else
                {
                  localObject1 = LocaleController.formatString("NotificationMessageGif", NUM, new Object[] { str });
                }
              }
              else if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
              {
                localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, "📎 " + paramMessageObject.messageOwner.message });
                paramArrayOfBoolean[0] = true;
              }
              else
              {
                localObject1 = LocaleController.formatString("NotificationMessageDocument", NUM, new Object[] { str });
              }
            }
          }
        }
        else {
          localObject1 = LocaleController.formatString("NotificationMessageNoText", NUM, new Object[] { str });
        }
      }
      else
      {
        localObject1 = localObject3;
        if (i != 0)
        {
          if (MessagesController.getNotificationsSettings(this.currentAccount).getBoolean("EnablePreviewGroup", true))
          {
            if ((paramMessageObject.messageOwner instanceof TLRPC.TL_messageService))
            {
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser))
              {
                j = paramMessageObject.messageOwner.action.user_id;
                i = j;
                if (j == 0)
                {
                  i = j;
                  if (paramMessageObject.messageOwner.action.users.size() == 1) {
                    i = ((Integer)paramMessageObject.messageOwner.action.users.get(0)).intValue();
                  }
                }
                if (i != 0)
                {
                  if ((paramMessageObject.messageOwner.to_id.channel_id != 0) && (!((TLRPC.Chat)localObject2).megagroup))
                  {
                    localObject1 = LocaleController.formatString("ChannelAddedByNotification", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if (i == UserConfig.getInstance(this.currentAccount).getClientUserId())
                  {
                    localObject1 = LocaleController.formatString("NotificationInvitedToGroup", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  paramMessageObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
                  if (paramMessageObject == null)
                  {
                    paramMessageObject = null;
                    break;
                  }
                  if (k == paramMessageObject.id)
                  {
                    if (((TLRPC.Chat)localObject2).megagroup)
                    {
                      localObject1 = LocaleController.formatString("NotificationGroupAddSelfMega", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                      continue;
                    }
                    localObject1 = LocaleController.formatString("NotificationGroupAddSelf", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  localObject1 = LocaleController.formatString("NotificationGroupAddMember", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, UserObject.getUserName(paramMessageObject) });
                  continue;
                }
                paramArrayOfBoolean = new StringBuilder("");
                for (k = 0; k < paramMessageObject.messageOwner.action.users.size(); k++)
                {
                  localObject1 = MessagesController.getInstance(this.currentAccount).getUser((Integer)paramMessageObject.messageOwner.action.users.get(k));
                  if (localObject1 != null)
                  {
                    localObject1 = UserObject.getUserName((TLRPC.User)localObject1);
                    if (paramArrayOfBoolean.length() != 0) {
                      paramArrayOfBoolean.append(", ");
                    }
                    paramArrayOfBoolean.append((String)localObject1);
                  }
                }
                localObject1 = LocaleController.formatString("NotificationGroupAddMember", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, paramArrayOfBoolean.toString() });
                continue;
              }
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByLink))
              {
                localObject1 = LocaleController.formatString("NotificationInvitedToGroupByLink", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                continue;
              }
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatEditTitle))
              {
                localObject1 = LocaleController.formatString("NotificationEditedGroupName", NUM, new Object[] { str, paramMessageObject.messageOwner.action.title });
                continue;
              }
              if (((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto)) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatDeletePhoto)))
              {
                if ((paramMessageObject.messageOwner.to_id.channel_id != 0) && (!((TLRPC.Chat)localObject2).megagroup))
                {
                  localObject1 = LocaleController.formatString("ChannelPhotoEditNotification", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                localObject1 = LocaleController.formatString("NotificationEditedGroupPhoto", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                continue;
              }
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser))
              {
                if (paramMessageObject.messageOwner.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId())
                {
                  localObject1 = LocaleController.formatString("NotificationGroupKickYou", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if (paramMessageObject.messageOwner.action.user_id == k)
                {
                  localObject1 = LocaleController.formatString("NotificationGroupLeftMember", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                paramMessageObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramMessageObject.messageOwner.action.user_id));
                if (paramMessageObject == null)
                {
                  paramMessageObject = null;
                  break;
                }
                localObject1 = LocaleController.formatString("NotificationGroupKickMember", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, UserObject.getUserName(paramMessageObject) });
                continue;
              }
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatCreate))
              {
                localObject1 = paramMessageObject.messageText.toString();
                continue;
              }
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChannelCreate))
              {
                localObject1 = paramMessageObject.messageText.toString();
                continue;
              }
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo))
              {
                localObject1 = LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                continue;
              }
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChannelMigrateFrom))
              {
                localObject1 = LocaleController.formatString("ActionMigrateFromGroupNotify", NUM, new Object[] { paramMessageObject.messageOwner.action.title });
                continue;
              }
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionScreenshotTaken))
              {
                localObject1 = paramMessageObject.messageText.toString();
                continue;
              }
              if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))
              {
                if ((localObject2 != null) && (((TLRPC.Chat)localObject2).megagroup))
                {
                  if (paramMessageObject.replyMessageObject == null)
                  {
                    localObject1 = LocaleController.formatString("NotificationActionPinnedNoText", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  paramMessageObject = paramMessageObject.replyMessageObject;
                  if (paramMessageObject.isMusic())
                  {
                    localObject1 = LocaleController.formatString("NotificationActionPinnedMusic", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if (paramMessageObject.isVideo())
                  {
                    if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                    {
                      localObject1 = LocaleController.formatString("NotificationActionPinnedText", NUM, new Object[] { str, "📹 " + paramMessageObject.messageOwner.message, ((TLRPC.Chat)localObject2).title });
                      continue;
                    }
                    localObject1 = LocaleController.formatString("NotificationActionPinnedVideo", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if (paramMessageObject.isGif())
                  {
                    if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                    {
                      localObject1 = LocaleController.formatString("NotificationActionPinnedText", NUM, new Object[] { str, "🎬 " + paramMessageObject.messageOwner.message, ((TLRPC.Chat)localObject2).title });
                      continue;
                    }
                    localObject1 = LocaleController.formatString("NotificationActionPinnedGif", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if (paramMessageObject.isVoice())
                  {
                    localObject1 = LocaleController.formatString("NotificationActionPinnedVoice", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if (paramMessageObject.isRoundVideo())
                  {
                    localObject1 = LocaleController.formatString("NotificationActionPinnedRound", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if (paramMessageObject.isSticker())
                  {
                    paramMessageObject = paramMessageObject.getStickerEmoji();
                    if (paramMessageObject != null)
                    {
                      localObject1 = LocaleController.formatString("NotificationActionPinnedStickerEmoji", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, paramMessageObject });
                      continue;
                    }
                    localObject1 = LocaleController.formatString("NotificationActionPinnedSticker", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
                  {
                    if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                    {
                      localObject1 = LocaleController.formatString("NotificationActionPinnedText", NUM, new Object[] { str, "📎 " + paramMessageObject.messageOwner.message, ((TLRPC.Chat)localObject2).title });
                      continue;
                    }
                    localObject1 = LocaleController.formatString("NotificationActionPinnedFile", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
                  {
                    localObject1 = LocaleController.formatString("NotificationActionPinnedGeo", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive))
                  {
                    localObject1 = LocaleController.formatString("NotificationActionPinnedGeoLive", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
                  {
                    localObject1 = LocaleController.formatString("NotificationActionPinnedContact", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                  {
                    if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                    {
                      localObject1 = LocaleController.formatString("NotificationActionPinnedText", NUM, new Object[] { str, "🖼 " + paramMessageObject.messageOwner.message, ((TLRPC.Chat)localObject2).title });
                      continue;
                    }
                    localObject1 = LocaleController.formatString("NotificationActionPinnedPhoto", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
                  {
                    localObject1 = LocaleController.formatString("NotificationActionPinnedGame", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  if ((paramMessageObject.messageText != null) && (paramMessageObject.messageText.length() > 0))
                  {
                    paramArrayOfBoolean = paramMessageObject.messageText;
                    paramMessageObject = paramArrayOfBoolean;
                    if (paramArrayOfBoolean.length() > 20) {
                      paramMessageObject = paramArrayOfBoolean.subSequence(0, 20) + "...";
                    }
                    localObject1 = LocaleController.formatString("NotificationActionPinnedText", NUM, new Object[] { str, paramMessageObject, ((TLRPC.Chat)localObject2).title });
                    continue;
                  }
                  localObject1 = LocaleController.formatString("NotificationActionPinnedNoText", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if (paramMessageObject.replyMessageObject == null)
                {
                  localObject1 = LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                paramMessageObject = paramMessageObject.replyMessageObject;
                if (paramMessageObject.isMusic())
                {
                  localObject1 = LocaleController.formatString("NotificationActionPinnedMusicChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if (paramMessageObject.isVideo())
                {
                  if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                  {
                    paramMessageObject = "📹 " + paramMessageObject.messageOwner.message;
                    localObject1 = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                    continue;
                  }
                  localObject1 = LocaleController.formatString("NotificationActionPinnedVideoChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if (paramMessageObject.isGif())
                {
                  if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                  {
                    paramMessageObject = "🎬 " + paramMessageObject.messageOwner.message;
                    localObject1 = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                    continue;
                  }
                  localObject1 = LocaleController.formatString("NotificationActionPinnedGifChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if (paramMessageObject.isVoice())
                {
                  localObject1 = LocaleController.formatString("NotificationActionPinnedVoiceChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if (paramMessageObject.isRoundVideo())
                {
                  localObject1 = LocaleController.formatString("NotificationActionPinnedRoundChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if (paramMessageObject.isSticker())
                {
                  paramMessageObject = paramMessageObject.getStickerEmoji();
                  if (paramMessageObject != null)
                  {
                    localObject1 = LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                    continue;
                  }
                  localObject1 = LocaleController.formatString("NotificationActionPinnedStickerChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
                {
                  if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                  {
                    paramMessageObject = "📎 " + paramMessageObject.messageOwner.message;
                    localObject1 = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                    continue;
                  }
                  localObject1 = LocaleController.formatString("NotificationActionPinnedFileChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
                {
                  localObject1 = LocaleController.formatString("NotificationActionPinnedGeoChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive))
                {
                  localObject1 = LocaleController.formatString("NotificationActionPinnedGeoLiveChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
                {
                  localObject1 = LocaleController.formatString("NotificationActionPinnedContactChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                {
                  if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                  {
                    paramMessageObject = "🖼 " + paramMessageObject.messageOwner.message;
                    localObject1 = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                    continue;
                  }
                  localObject1 = LocaleController.formatString("NotificationActionPinnedPhotoChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
                {
                  localObject1 = LocaleController.formatString("NotificationActionPinnedGameChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                  continue;
                }
                if ((paramMessageObject.messageText != null) && (paramMessageObject.messageText.length() > 0))
                {
                  paramArrayOfBoolean = paramMessageObject.messageText;
                  paramMessageObject = paramArrayOfBoolean;
                  if (paramArrayOfBoolean.length() > 20) {
                    paramMessageObject = paramArrayOfBoolean.subSequence(0, 20) + "...";
                  }
                  localObject1 = LocaleController.formatString("NotificationActionPinnedTextChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                  continue;
                }
                localObject1 = LocaleController.formatString("NotificationActionPinnedNoTextChannel", NUM, new Object[] { ((TLRPC.Chat)localObject2).title });
                continue;
              }
              localObject1 = localObject3;
              if (!(paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionGameScore)) {
                continue;
              }
              localObject1 = paramMessageObject.messageText.toString();
              continue;
            }
            if ((ChatObject.isChannel((TLRPC.Chat)localObject2)) && (!((TLRPC.Chat)localObject2).megagroup))
            {
              if (paramMessageObject.isMediaEmpty())
              {
                if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0))
                {
                  localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, paramMessageObject.messageOwner.message });
                  paramArrayOfBoolean[0] = true;
                  continue;
                }
                localObject1 = LocaleController.formatString("ChannelMessageNoText", NUM, new Object[] { str });
                continue;
              }
              if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
              {
                if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                {
                  localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, "🖼 " + paramMessageObject.messageOwner.message });
                  paramArrayOfBoolean[0] = true;
                  continue;
                }
                localObject1 = LocaleController.formatString("ChannelMessagePhoto", NUM, new Object[] { str });
                continue;
              }
              if (paramMessageObject.isVideo())
              {
                if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                {
                  localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, "📹 " + paramMessageObject.messageOwner.message });
                  paramArrayOfBoolean[0] = true;
                  continue;
                }
                localObject1 = LocaleController.formatString("ChannelMessageVideo", NUM, new Object[] { str });
                continue;
              }
              if (paramMessageObject.isVoice())
              {
                localObject1 = LocaleController.formatString("ChannelMessageAudio", NUM, new Object[] { str });
                continue;
              }
              if (paramMessageObject.isRoundVideo())
              {
                localObject1 = LocaleController.formatString("ChannelMessageRound", NUM, new Object[] { str });
                continue;
              }
              if (paramMessageObject.isMusic())
              {
                localObject1 = LocaleController.formatString("ChannelMessageMusic", NUM, new Object[] { str });
                continue;
              }
              if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
              {
                localObject1 = LocaleController.formatString("ChannelMessageContact", NUM, new Object[] { str });
                continue;
              }
              if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
              {
                localObject1 = LocaleController.formatString("ChannelMessageMap", NUM, new Object[] { str });
                continue;
              }
              if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive))
              {
                localObject1 = LocaleController.formatString("ChannelMessageLiveLocation", NUM, new Object[] { str });
                continue;
              }
              localObject1 = localObject3;
              if (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
                continue;
              }
              if (paramMessageObject.isSticker())
              {
                paramMessageObject = paramMessageObject.getStickerEmoji();
                if (paramMessageObject != null)
                {
                  localObject1 = LocaleController.formatString("ChannelMessageStickerEmoji", NUM, new Object[] { str, paramMessageObject });
                  continue;
                }
                localObject1 = LocaleController.formatString("ChannelMessageSticker", NUM, new Object[] { str });
                continue;
              }
              if (paramMessageObject.isGif())
              {
                if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
                {
                  localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, "🎬 " + paramMessageObject.messageOwner.message });
                  paramArrayOfBoolean[0] = true;
                  continue;
                }
                localObject1 = LocaleController.formatString("ChannelMessageGIF", NUM, new Object[] { str });
                continue;
              }
              if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
              {
                localObject1 = LocaleController.formatString("NotificationMessageText", NUM, new Object[] { str, "📎 " + paramMessageObject.messageOwner.message });
                paramArrayOfBoolean[0] = true;
                continue;
              }
              localObject1 = LocaleController.formatString("ChannelMessageDocument", NUM, new Object[] { str });
              continue;
            }
            if (paramMessageObject.isMediaEmpty())
            {
              if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0))
              {
                localObject1 = LocaleController.formatString("NotificationMessageGroupText", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, paramMessageObject.messageOwner.message });
                continue;
              }
              localObject1 = LocaleController.formatString("NotificationMessageGroupNoText", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
            {
              if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
              {
                localObject1 = LocaleController.formatString("NotificationMessageGroupText", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, "🖼 " + paramMessageObject.messageOwner.message });
                continue;
              }
              localObject1 = LocaleController.formatString("NotificationMessageGroupPhoto", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if (paramMessageObject.isVideo())
            {
              if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
              {
                localObject1 = LocaleController.formatString("NotificationMessageGroupText", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, "📹 " + paramMessageObject.messageOwner.message });
                continue;
              }
              localObject1 = LocaleController.formatString("NotificationMessageGroupVideo", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if (paramMessageObject.isVoice())
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupAudio", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if (paramMessageObject.isRoundVideo())
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupRound", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if (paramMessageObject.isMusic())
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupMusic", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupContact", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupGame", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, paramMessageObject.messageOwner.media.game.title });
              continue;
            }
            if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupMap", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeoLive))
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupLiveLocation", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            localObject1 = localObject3;
            if (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) {
              continue;
            }
            if (paramMessageObject.isSticker())
            {
              paramMessageObject = paramMessageObject.getStickerEmoji();
              if (paramMessageObject != null)
              {
                localObject1 = LocaleController.formatString("NotificationMessageGroupStickerEmoji", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, paramMessageObject });
                continue;
              }
              localObject1 = LocaleController.formatString("NotificationMessageGroupSticker", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if (paramMessageObject.isGif())
            {
              if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
              {
                localObject1 = LocaleController.formatString("NotificationMessageGroupText", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, "🎬 " + paramMessageObject.messageOwner.message });
                continue;
              }
              localObject1 = LocaleController.formatString("NotificationMessageGroupGif", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
              continue;
            }
            if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.message)))
            {
              localObject1 = LocaleController.formatString("NotificationMessageGroupText", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title, "📎 " + paramMessageObject.messageOwner.message });
              continue;
            }
            localObject1 = LocaleController.formatString("NotificationMessageGroupDocument", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
            continue;
          }
          if ((ChatObject.isChannel((TLRPC.Chat)localObject2)) && (!((TLRPC.Chat)localObject2).megagroup)) {
            localObject1 = LocaleController.formatString("ChannelMessageNoText", NUM, new Object[] { str });
          } else {
            localObject1 = LocaleController.formatString("NotificationMessageGroupNoText", NUM, new Object[] { str, ((TLRPC.Chat)localObject2).title });
          }
        }
      }
    }
  }
  
  private int getTotalAllUnreadCount()
  {
    int i = 0;
    int j = 0;
    while (j < 3)
    {
      int k = i;
      if (UserConfig.getInstance(j).isClientActivated())
      {
        NotificationsController localNotificationsController = getInstance(j);
        k = i;
        if (localNotificationsController.showBadgeNumber) {
          k = i + localNotificationsController.total_unread_count;
        }
      }
      j++;
      i = k;
    }
    return i;
  }
  
  private boolean isEmptyVibration(long[] paramArrayOfLong)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (paramArrayOfLong != null)
    {
      if (paramArrayOfLong.length != 0) {
        break label17;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      label17:
      for (int i = 0;; i++)
      {
        if (i >= paramArrayOfLong.length) {
          break label43;
        }
        bool2 = bool1;
        if (paramArrayOfLong[0] != 0L) {
          break;
        }
      }
      label43:
      bool2 = true;
    }
  }
  
  private boolean isPersonalMessage(MessageObject paramMessageObject)
  {
    if ((paramMessageObject.messageOwner.to_id != null) && (paramMessageObject.messageOwner.to_id.chat_id == 0) && (paramMessageObject.messageOwner.to_id.channel_id == 0) && ((paramMessageObject.messageOwner.action == null) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void playInChatSound()
  {
    if ((!this.inChatSoundEnabled) || (MediaController.getInstance().isRecordingAudio())) {}
    for (;;)
    {
      return;
      try
      {
        int i = audioManager.getRingerMode();
        if (i == 0) {
          continue;
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          FileLog.e(localException2);
        }
      }
      try
      {
        if (getNotifyOverride(MessagesController.getNotificationsSettings(this.currentAccount), this.opened_dialog_id) != 2)
        {
          DispatchQueue localDispatchQueue = notificationsQueue;
          Runnable local14 = new org/telegram/messenger/NotificationsController$14;
          local14.<init>(this);
          localDispatchQueue.postRunnable(local14);
        }
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
      }
    }
  }
  
  private void scheduleNotificationDelay(boolean paramBoolean)
  {
    for (;;)
    {
      try
      {
        if (BuildVars.LOGS_ENABLED)
        {
          localObject = new java/lang/StringBuilder;
          ((StringBuilder)localObject).<init>();
          FileLog.d("delay notification start, onlineReason = " + paramBoolean);
        }
        this.notificationDelayWakelock.acquire(10000L);
        notificationsQueue.cancelRunnable(this.notificationDelayRunnable);
        DispatchQueue localDispatchQueue = notificationsQueue;
        Object localObject = this.notificationDelayRunnable;
        if (paramBoolean)
        {
          i = 3000;
          localDispatchQueue.postRunnable((Runnable)localObject, i);
          return;
        }
      }
      catch (Exception localException)
      {
        int i;
        FileLog.e(localException);
        showOrUpdateNotification(this.notifyCheck);
        continue;
      }
      i = 1000;
    }
  }
  
  private void scheduleNotificationRepeat()
  {
    for (;;)
    {
      try
      {
        localObject = new android/content/Intent;
        ((Intent)localObject).<init>(ApplicationLoader.applicationContext, NotificationRepeat.class);
        ((Intent)localObject).putExtra("currentAccount", this.currentAccount);
        localObject = PendingIntent.getService(ApplicationLoader.applicationContext, 0, (Intent)localObject, 0);
        int i = MessagesController.getNotificationsSettings(this.currentAccount).getInt("repeat_messages", 60);
        if ((i > 0) && (this.personal_count > 0))
        {
          this.alarmManager.set(2, SystemClock.elapsedRealtime() + i * 60 * 1000, (PendingIntent)localObject);
          return;
        }
      }
      catch (Exception localException)
      {
        Object localObject;
        FileLog.e(localException);
        continue;
      }
      this.alarmManager.cancel((PendingIntent)localObject);
    }
  }
  
  private void setBadge(int paramInt)
  {
    if (this.lastBadgeCount == paramInt) {}
    for (;;)
    {
      return;
      this.lastBadgeCount = paramInt;
      NotificationBadge.applyCount(paramInt);
    }
  }
  
  @SuppressLint({"InlinedApi"})
  private void showExtraNotifications(NotificationCompat.Builder paramBuilder, boolean paramBoolean, String paramString)
  {
    Notification localNotification = paramBuilder.build();
    if (Build.VERSION.SDK_INT < 18) {
      notificationManager.notify(this.notificationId, localNotification);
    }
    for (;;)
    {
      return;
      ArrayList localArrayList1 = new ArrayList();
      LongSparseArray localLongSparseArray1 = new LongSparseArray();
      Object localObject1;
      long l1;
      Object localObject2;
      Object localObject3;
      for (int i = 0; i < this.pushMessages.size(); i++)
      {
        localObject1 = (MessageObject)this.pushMessages.get(i);
        l1 = ((MessageObject)localObject1).getDialogId();
        localObject2 = (ArrayList)localLongSparseArray1.get(l1);
        localObject3 = localObject2;
        if (localObject2 == null)
        {
          localObject3 = new ArrayList();
          localLongSparseArray1.put(l1, localObject3);
          localArrayList1.add(0, Long.valueOf(l1));
        }
        ((ArrayList)localObject3).add(localObject1);
      }
      LongSparseArray localLongSparseArray2 = this.wearNotificationsIds.clone();
      this.wearNotificationsIds.clear();
      ArrayList localArrayList2 = new ArrayList();
      JSONArray localJSONArray = null;
      if (WearDataLayerListenerService.isWatchConnected()) {
        localJSONArray = new JSONArray();
      }
      i = 0;
      int j = localArrayList1.size();
      if (i < j)
      {
        long l2 = ((Long)localArrayList1.get(i)).longValue();
        Object localObject4 = (ArrayList)localLongSparseArray1.get(l2);
        int k = ((MessageObject)((ArrayList)localObject4).get(0)).getId();
        int m = (int)l2;
        int n = (int)(l2 >> 32);
        localObject1 = (Integer)localLongSparseArray2.get(l2);
        Object localObject5;
        int i1;
        Object localObject6;
        Object localObject7;
        Object localObject8;
        boolean bool3;
        Object localObject9;
        Object localObject10;
        boolean bool4;
        Object localObject11;
        if (localObject1 == null) {
          if (m != 0)
          {
            localObject1 = Integer.valueOf(m);
            localObject5 = null;
            if (localJSONArray != null) {
              localObject5 = new JSONObject();
            }
            localObject2 = (MessageObject)((ArrayList)localObject4).get(0);
            i1 = ((MessageObject)localObject2).messageOwner.date;
            localObject6 = null;
            localObject7 = null;
            bool1 = false;
            bool2 = false;
            localObject8 = null;
            if (m == 0) {
              break label1620;
            }
            bool3 = true;
            if (m <= 0) {
              break label1294;
            }
            localObject7 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(m));
            if (localObject7 != null) {
              break label1074;
            }
            if (!((MessageObject)localObject2).isFcmMessage()) {
              break label1645;
            }
            localObject3 = ((MessageObject)localObject2).localName;
            localObject9 = localObject7;
            localObject10 = localObject5;
            localObject2 = localObject8;
            bool4 = bool2;
            paramBoolean = bool1;
            localObject11 = localObject6;
            bool5 = bool3;
          }
        }
        label418:
        Object localObject12;
        label829:
        Object localObject13;
        StringBuilder localStringBuilder;
        Object localObject14;
        int i3;
        Object localObject15;
        Object localObject16;
        label1027:
        int i4;
        for (;;)
        {
          if (!AndroidUtilities.needShowPasscode(false))
          {
            localObject5 = localObject2;
            if (!SharedConfig.isWaitingForPasscodeEnter) {}
          }
          else
          {
            localObject3 = LocaleController.getString("AppName", NUM);
            localObject5 = null;
            bool5 = false;
          }
          localObject12 = new NotificationCompat.CarExtender.UnreadConversation.Builder((String)localObject3).setLatestTimestamp(i1 * 1000L);
          localObject2 = new Intent(ApplicationLoader.applicationContext, AutoMessageHeardReceiver.class);
          ((Intent)localObject2).addFlags(32);
          ((Intent)localObject2).setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
          ((Intent)localObject2).putExtra("dialog_id", l2);
          ((Intent)localObject2).putExtra("max_id", k);
          ((Intent)localObject2).putExtra("currentAccount", this.currentAccount);
          ((NotificationCompat.CarExtender.UnreadConversation.Builder)localObject12).setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, ((Integer)localObject1).intValue(), (Intent)localObject2, 134217728));
          localObject2 = null;
          if (paramBoolean)
          {
            localObject7 = localObject2;
            if (!bool4) {}
          }
          else
          {
            localObject7 = localObject2;
            if (bool5)
            {
              localObject7 = localObject2;
              if (!SharedConfig.isWaitingForPasscodeEnter)
              {
                localObject2 = new Intent(ApplicationLoader.applicationContext, AutoMessageReplyReceiver.class);
                ((Intent)localObject2).addFlags(32);
                ((Intent)localObject2).setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
                ((Intent)localObject2).putExtra("dialog_id", l2);
                ((Intent)localObject2).putExtra("max_id", k);
                ((Intent)localObject2).putExtra("currentAccount", this.currentAccount);
                ((NotificationCompat.CarExtender.UnreadConversation.Builder)localObject12).setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, ((Integer)localObject1).intValue(), (Intent)localObject2, 134217728), new RemoteInput.Builder("extra_voice_reply").setLabel(LocaleController.getString("Reply", NUM)).build());
                localObject2 = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                ((Intent)localObject2).putExtra("dialog_id", l2);
                ((Intent)localObject2).putExtra("max_id", k);
                ((Intent)localObject2).putExtra("currentAccount", this.currentAccount);
                localObject6 = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, ((Integer)localObject1).intValue(), (Intent)localObject2, 134217728);
                localObject8 = new RemoteInput.Builder("extra_voice_reply").setLabel(LocaleController.getString("Reply", NUM)).build();
                if (m >= 0) {
                  break label1707;
                }
                localObject2 = LocaleController.formatString("ReplyToGroup", NUM, new Object[] { localObject3 });
                localObject7 = new NotificationCompat.Action.Builder(NUM, (CharSequence)localObject2, (PendingIntent)localObject6).setAllowGeneratedReplies(true).addRemoteInput((RemoteInput)localObject8).build();
              }
            }
          }
          localObject6 = (Integer)this.pushDialogs.get(l2);
          localObject2 = localObject6;
          if (localObject6 == null) {
            localObject2 = Integer.valueOf(0);
          }
          localObject13 = new NotificationCompat.MessagingStyle("").setConversationTitle(String.format("%1$s (%2$s)", new Object[] { localObject3, LocaleController.formatPluralString("NewMessages", Math.max(((Integer)localObject2).intValue(), ((ArrayList)localObject4).size())) }));
          localStringBuilder = new StringBuilder();
          localObject14 = new boolean[1];
          localObject8 = null;
          i2 = 0;
          localObject6 = null;
          if (localObject10 != null) {
            localObject6 = new JSONArray();
          }
          i3 = ((ArrayList)localObject4).size() - 1;
          for (;;)
          {
            if (i3 < 0) {
              break label2083;
            }
            localObject15 = (MessageObject)((ArrayList)localObject4).get(i3);
            localObject16 = getStringForMessage((MessageObject)localObject15, false, (boolean[])localObject14);
            if (!((MessageObject)localObject15).isFcmMessage()) {
              break;
            }
            localObject2 = ((MessageObject)localObject15).localName;
            if (localObject16 != null) {
              break label1737;
            }
            i4 = i2;
            localObject2 = localObject8;
            i3--;
            localObject8 = localObject2;
            i2 = i4;
          }
          localObject1 = Integer.valueOf(n);
          break;
          localLongSparseArray2.remove(l2);
          break;
          label1074:
          localObject12 = UserObject.getUserName((TLRPC.User)localObject7);
          bool5 = bool3;
          localObject11 = localObject6;
          paramBoolean = bool1;
          bool4 = bool2;
          localObject3 = localObject12;
          localObject2 = localObject8;
          localObject10 = localObject5;
          localObject9 = localObject7;
          if (((TLRPC.User)localObject7).photo != null)
          {
            bool5 = bool3;
            localObject11 = localObject6;
            paramBoolean = bool1;
            bool4 = bool2;
            localObject3 = localObject12;
            localObject2 = localObject8;
            localObject10 = localObject5;
            localObject9 = localObject7;
            if (((TLRPC.User)localObject7).photo.photo_small != null)
            {
              bool5 = bool3;
              localObject11 = localObject6;
              paramBoolean = bool1;
              bool4 = bool2;
              localObject3 = localObject12;
              localObject2 = localObject8;
              localObject10 = localObject5;
              localObject9 = localObject7;
              if (((TLRPC.User)localObject7).photo.photo_small.volume_id != 0L)
              {
                bool5 = bool3;
                localObject11 = localObject6;
                paramBoolean = bool1;
                bool4 = bool2;
                localObject3 = localObject12;
                localObject2 = localObject8;
                localObject10 = localObject5;
                localObject9 = localObject7;
                if (((TLRPC.User)localObject7).photo.photo_small.local_id != 0)
                {
                  localObject2 = ((TLRPC.User)localObject7).photo.photo_small;
                  bool5 = bool3;
                  localObject11 = localObject6;
                  paramBoolean = bool1;
                  bool4 = bool2;
                  localObject3 = localObject12;
                  localObject10 = localObject5;
                  localObject9 = localObject7;
                  continue;
                  label1294:
                  localObject6 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-m));
                  if (localObject6 != null) {
                    break label1368;
                  }
                  if (!((MessageObject)localObject2).isFcmMessage()) {
                    break label1645;
                  }
                  bool4 = ((MessageObject)localObject2).isMegagroup();
                  localObject3 = ((MessageObject)localObject2).localName;
                  paramBoolean = ((MessageObject)localObject2).localChannel;
                  bool5 = bool3;
                  localObject11 = localObject6;
                  localObject2 = localObject8;
                  localObject10 = localObject5;
                  localObject9 = localObject7;
                }
              }
            }
          }
        }
        label1368:
        boolean bool1 = ((TLRPC.Chat)localObject6).megagroup;
        if ((ChatObject.isChannel((TLRPC.Chat)localObject6)) && (!((TLRPC.Chat)localObject6).megagroup)) {}
        for (boolean bool2 = true;; bool2 = false)
        {
          localObject12 = ((TLRPC.Chat)localObject6).title;
          bool5 = bool3;
          localObject11 = localObject6;
          paramBoolean = bool2;
          bool4 = bool1;
          localObject3 = localObject12;
          localObject2 = localObject8;
          localObject10 = localObject5;
          localObject9 = localObject7;
          if (((TLRPC.Chat)localObject6).photo == null) {
            break;
          }
          bool5 = bool3;
          localObject11 = localObject6;
          paramBoolean = bool2;
          bool4 = bool1;
          localObject3 = localObject12;
          localObject2 = localObject8;
          localObject10 = localObject5;
          localObject9 = localObject7;
          if (((TLRPC.Chat)localObject6).photo.photo_small == null) {
            break;
          }
          bool5 = bool3;
          localObject11 = localObject6;
          paramBoolean = bool2;
          bool4 = bool1;
          localObject3 = localObject12;
          localObject2 = localObject8;
          localObject10 = localObject5;
          localObject9 = localObject7;
          if (((TLRPC.Chat)localObject6).photo.photo_small.volume_id == 0L) {
            break;
          }
          bool5 = bool3;
          localObject11 = localObject6;
          paramBoolean = bool2;
          bool4 = bool1;
          localObject3 = localObject12;
          localObject2 = localObject8;
          localObject10 = localObject5;
          localObject9 = localObject7;
          if (((TLRPC.Chat)localObject6).photo.photo_small.local_id == 0) {
            break;
          }
          localObject2 = ((TLRPC.Chat)localObject6).photo.photo_small;
          bool5 = bool3;
          localObject11 = localObject6;
          paramBoolean = bool2;
          bool4 = bool1;
          localObject3 = localObject12;
          localObject10 = localObject5;
          localObject9 = localObject7;
          break;
        }
        label1620:
        boolean bool5 = false;
        localObject3 = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(n));
        if (localObject3 == null) {}
        label1645:
        label1707:
        label1737:
        label2083:
        label2255:
        label2843:
        label2881:
        label2918:
        label3007:
        label3021:
        do
        {
          do
          {
            i++;
            break;
            localObject9 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.EncryptedChat)localObject3).user_id));
          } while (localObject9 == null);
          localObject3 = LocaleController.getString("SecretChatName", NUM);
          localObject2 = null;
          localObject10 = null;
          localObject11 = localObject6;
          paramBoolean = bool1;
          bool4 = bool2;
          break label418;
          localObject2 = LocaleController.formatString("ReplyToUser", NUM, new Object[] { localObject3 });
          break label829;
          localObject2 = localObject3;
          break label1027;
          if (m < 0) {
            localObject2 = ((String)localObject16).replace(" @ " + (String)localObject2, "");
          }
          for (;;)
          {
            if (localStringBuilder.length() > 0) {
              localStringBuilder.append("\n\n");
            }
            localStringBuilder.append((String)localObject2);
            ((NotificationCompat.CarExtender.UnreadConversation.Builder)localObject12).addMessage((String)localObject2);
            ((NotificationCompat.MessagingStyle)localObject13).addMessage((CharSequence)localObject2, ((MessageObject)localObject15).messageOwner.date * 1000L, null);
            if (localObject6 != null) {}
            try
            {
              localObject16 = new org/json/JSONObject;
              ((JSONObject)localObject16).<init>();
              ((JSONObject)localObject16).put("text", getShortStringForMessage((MessageObject)localObject15));
              ((JSONObject)localObject16).put("date", ((MessageObject)localObject15).messageOwner.date);
              if ((((MessageObject)localObject15).isFromUser()) && (m < 0))
              {
                localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((MessageObject)localObject15).getFromId()));
                if (localObject2 != null)
                {
                  ((JSONObject)localObject16).put("fname", ((TLRPC.User)localObject2).first_name);
                  ((JSONObject)localObject16).put("lname", ((TLRPC.User)localObject2).last_name);
                }
              }
              ((JSONArray)localObject6).put(localObject16);
            }
            catch (JSONException localJSONException1)
            {
              for (;;) {}
            }
            localObject2 = localObject8;
            i4 = i2;
            if (l2 != 777000L) {
              break;
            }
            localObject2 = localObject8;
            i4 = i2;
            if (((MessageObject)localObject15).messageOwner.reply_markup == null) {
              break;
            }
            localObject2 = ((MessageObject)localObject15).messageOwner.reply_markup.rows;
            i4 = ((MessageObject)localObject15).getId();
            break;
            if (localObject14[0] != 0) {
              localObject2 = ((String)localObject16).replace((String)localObject2 + ": ", "");
            } else {
              localObject2 = ((String)localObject16).replace((String)localObject2 + " ", "");
            }
          }
          localObject2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
          ((Intent)localObject2).setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
          ((Intent)localObject2).setFlags(32768);
          if (m != 0) {
            if (m > 0)
            {
              ((Intent)localObject2).putExtra("userId", m);
              ((Intent)localObject2).putExtra("currentAccount", this.currentAccount);
              localObject14 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject2, NUM);
              localObject15 = new NotificationCompat.WearableExtender();
              if (localObject7 != null) {
                ((NotificationCompat.WearableExtender)localObject15).addAction((NotificationCompat.Action)localObject7);
              }
              if (m == 0) {
                break label2881;
              }
              if (m <= 0) {
                break label2843;
              }
              localObject2 = "tguser" + m + "_" + k;
              ((NotificationCompat.WearableExtender)localObject15).setDismissalId((String)localObject2);
              ((NotificationCompat.WearableExtender)localObject15).setBridgeTag("tgaccount" + UserConfig.getInstance(this.currentAccount).getClientUserId());
              localObject7 = new NotificationCompat.WearableExtender();
              ((NotificationCompat.WearableExtender)localObject7).setDismissalId("summary_" + (String)localObject2);
              paramBuilder.extend((NotificationCompat.Extender)localObject7);
              l1 = ((MessageObject)((ArrayList)localObject4).get(0)).messageOwner.date * 1000L;
              localObject2 = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle((CharSequence)localObject3).setSmallIcon(NUM).setGroup(this.notificationGroup).setContentText(localStringBuilder.toString()).setAutoCancel(true).setNumber(((ArrayList)localObject4).size()).setColor(-13851168).setGroupSummary(false).setWhen(l1).setShowWhen(true).setShortcutId("sdid_" + l2).setGroupAlertBehavior(1).setStyle((NotificationCompat.Style)localObject13).setContentIntent((PendingIntent)localObject14).extend((NotificationCompat.Extender)localObject15).setSortKey("" + (Long.MAX_VALUE - l1)).extend(new NotificationCompat.CarExtender().setUnreadConversation(((NotificationCompat.CarExtender.UnreadConversation.Builder)localObject12).build())).setCategory("msg");
              if ((this.pushDialogs.size() == 1) && (!TextUtils.isEmpty(paramString))) {
                ((NotificationCompat.Builder)localObject2).setSubText(paramString);
              }
              if (m == 0) {
                ((NotificationCompat.Builder)localObject2).setLocalOnly(true);
              }
              if (localObject5 != null)
              {
                localObject7 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject5, null, "50_50");
                if (localObject7 == null) {
                  break label2918;
                }
                ((NotificationCompat.Builder)localObject2).setLargeIcon(((BitmapDrawable)localObject7).getBitmap());
              }
              if ((AndroidUtilities.needShowPasscode(false)) || (SharedConfig.isWaitingForPasscodeEnter) || (localObject8 == null)) {
                break label3021;
              }
              i4 = 0;
              n = ((ArrayList)localObject8).size();
            }
          }
          for (;;)
          {
            if (i4 >= n) {
              break label3021;
            }
            localObject7 = (TLRPC.TL_keyboardButtonRow)((ArrayList)localObject8).get(i4);
            i3 = 0;
            int i5 = ((TLRPC.TL_keyboardButtonRow)localObject7).buttons.size();
            for (;;)
            {
              if (i3 < i5)
              {
                localObject4 = (TLRPC.KeyboardButton)((TLRPC.TL_keyboardButtonRow)localObject7).buttons.get(i3);
                if ((localObject4 instanceof TLRPC.TL_keyboardButtonCallback))
                {
                  localObject12 = new Intent(ApplicationLoader.applicationContext, NotificationCallbackReceiver.class);
                  ((Intent)localObject12).putExtra("currentAccount", this.currentAccount);
                  ((Intent)localObject12).putExtra("did", l2);
                  if (((TLRPC.KeyboardButton)localObject4).data != null) {
                    ((Intent)localObject12).putExtra("data", ((TLRPC.KeyboardButton)localObject4).data);
                  }
                  ((Intent)localObject12).putExtra("mid", i2);
                  localObject13 = ((TLRPC.KeyboardButton)localObject4).text;
                  localObject4 = ApplicationLoader.applicationContext;
                  int i6 = this.lastButtonId;
                  this.lastButtonId = (i6 + 1);
                  ((NotificationCompat.Builder)localObject2).addAction(0, (CharSequence)localObject13, PendingIntent.getBroadcast((Context)localObject4, i6, (Intent)localObject12, 134217728));
                }
                i3++;
                continue;
                ((Intent)localObject2).putExtra("chatId", -m);
                break;
                ((Intent)localObject2).putExtra("encId", n);
                break;
                localObject2 = "tgchat" + -m + "_" + k;
                break label2255;
                localObject2 = "tgenc" + n + "_" + k;
                break label2255;
                for (;;)
                {
                  float f;
                  try
                  {
                    localObject7 = FileLoader.getPathToAttach((TLObject)localObject5, true);
                    if (!((File)localObject7).exists()) {
                      break;
                    }
                    f = 160.0F / AndroidUtilities.dp(50.0F);
                    localObject12 = new android/graphics/BitmapFactory$Options;
                    ((BitmapFactory.Options)localObject12).<init>();
                    if (f >= 1.0F) {
                      break label3007;
                    }
                    i4 = 1;
                    ((BitmapFactory.Options)localObject12).inSampleSize = i4;
                    localObject7 = BitmapFactory.decodeFile(((File)localObject7).getAbsolutePath(), (BitmapFactory.Options)localObject12);
                    if (localObject7 == null) {
                      break;
                    }
                    ((NotificationCompat.Builder)localObject2).setLargeIcon((Bitmap)localObject7);
                  }
                  catch (Throwable localThrowable) {}
                  break;
                  i4 = (int)f;
                }
              }
            }
            i4++;
          }
          if ((localObject11 == null) && (localObject9 != null) && (((TLRPC.User)localObject9).phone != null) && (((TLRPC.User)localObject9).phone.length() > 0)) {
            ((NotificationCompat.Builder)localObject2).addPerson("tel:+" + ((TLRPC.User)localObject9).phone);
          }
          if (Build.VERSION.SDK_INT >= 26) {
            ((NotificationCompat.Builder)localObject2).setChannelId("Other3");
          }
          localArrayList2.add(new Object()
          {
            int id;
            Notification notification;
            
            void call()
            {
              NotificationsController.notificationManager.notify(this.id, this.notification);
            }
          });
          this.wearNotificationsIds.put(l2, localObject1);
        } while (localObject10 == null);
        for (;;)
        {
          try
          {
            ((JSONObject)localObject10).put("reply", bool5);
            ((JSONObject)localObject10).put("name", localObject3);
            ((JSONObject)localObject10).put("max_id", k);
            ((JSONObject)localObject10).put("max_date", i1);
            ((JSONObject)localObject10).put("id", Math.abs(m));
            if (localObject5 != null)
            {
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              ((JSONObject)localObject10).put("photo", ((TLRPC.FileLocation)localObject5).dc_id + "_" + ((TLRPC.FileLocation)localObject5).volume_id + "_" + ((TLRPC.FileLocation)localObject5).secret);
            }
            if (localObject6 != null) {
              ((JSONObject)localObject10).put("msgs", localObject6);
            }
            if (m <= 0) {
              break label3308;
            }
            ((JSONObject)localObject10).put("type", "user");
            localJSONArray.put(localObject10);
          }
          catch (JSONException localJSONException2) {}
          break;
          label3308:
          if (m < 0) {
            if ((paramBoolean) || (bool4)) {
              ((JSONObject)localObject10).put("type", "channel");
            } else {
              ((JSONObject)localObject10).put("type", "group");
            }
          }
        }
      }
      notificationManager.notify(this.notificationId, localNotification);
      i = 0;
      int i2 = localArrayList2.size();
      while (i < i2)
      {
        ((1NotificationHolder)localArrayList2.get(i)).call();
        i++;
      }
      for (i = 0; i < localLongSparseArray2.size(); i++) {
        notificationManager.cancel(((Integer)localLongSparseArray2.valueAt(i)).intValue());
      }
      if (localJSONArray != null) {
        try
        {
          paramBuilder = new org/json/JSONObject;
          paramBuilder.<init>();
          paramBuilder.put("id", UserConfig.getInstance(this.currentAccount).getClientUserId());
          paramBuilder.put("n", localJSONArray);
          WearDataLayerListenerService.sendMessageToWatch("/notify", paramBuilder.toString().getBytes("UTF-8"), "remote_notifications");
        }
        catch (Exception paramBuilder) {}
      }
    }
  }
  
  private void showOrUpdateNotification(boolean paramBoolean)
  {
    if ((!UserConfig.getInstance(this.currentAccount).isClientActivated()) || (this.pushMessages.isEmpty())) {
      dismissNotification();
    }
    MessageObject localMessageObject1;
    Object localObject1;
    int i;
    for (;;)
    {
      return;
      try
      {
        ConnectionsManager.getInstance(this.currentAccount).resumeNetworkMaybe();
        localMessageObject1 = (MessageObject)this.pushMessages.get(0);
        localObject1 = MessagesController.getNotificationsSettings(this.currentAccount);
        i = ((SharedPreferences)localObject1).getInt("dismissDate", 0);
        if (localMessageObject1.messageOwner.date > i) {
          break;
        }
        dismissNotification();
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
      }
    }
    long l1 = localMessageObject1.getDialogId();
    long l2 = l1;
    if (localMessageObject1.messageOwner.mentioned) {
      l2 = localMessageObject1.messageOwner.from_id;
    }
    localMessageObject1.getId();
    int j;
    label159:
    int k;
    int m;
    label185:
    Object localObject5;
    Object localObject6;
    Object localObject7;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    Object localObject2;
    label452:
    label514:
    String str;
    boolean bool3;
    int i6;
    int i7;
    Object localObject8;
    if (localMessageObject1.messageOwner.to_id.chat_id != 0)
    {
      j = localMessageObject1.messageOwner.to_id.chat_id;
      k = localMessageObject1.messageOwner.to_id.user_id;
      if (k != 0) {
        break label2421;
      }
      m = localMessageObject1.messageOwner.from_id;
      localObject5 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(m));
      localObject6 = null;
      if (j != 0) {
        localObject6 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(j));
      }
      localObject7 = null;
      n = 0;
      i1 = 0;
      i2 = -16776961;
      i3 = 0;
      i4 = getNotifyOverride((SharedPreferences)localObject1, l2);
      if ((paramBoolean) && (i4 != 2))
      {
        if (((SharedPreferences)localObject1).getBoolean("EnableAll", true))
        {
          k = n;
          if (j != 0)
          {
            k = n;
            if (((SharedPreferences)localObject1).getBoolean("EnableGroup", true)) {}
          }
        }
        else
        {
          k = n;
          if (i4 != 0) {}
        }
      }
      else {
        k = 1;
      }
      i5 = k;
      if (k == 0)
      {
        i5 = k;
        if (l1 == l2)
        {
          i5 = k;
          if (localObject6 != null)
          {
            localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            if (!((SharedPreferences)localObject1).getBoolean("custom_" + l1, false)) {
              break label2452;
            }
            localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            n = ((SharedPreferences)localObject1).getInt("smart_max_count_" + l1, 2);
            localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            i4 = ((SharedPreferences)localObject1).getInt("smart_delay_" + l1, 180);
            i5 = k;
            if (n != 0)
            {
              localObject2 = (Point)this.smartNotificationsDialogs.get(l1);
              if (localObject2 != null) {
                break label2463;
              }
              localObject2 = new android/graphics/Point;
              ((Point)localObject2).<init>(1, (int)(System.currentTimeMillis() / 1000L));
              this.smartNotificationsDialogs.put(l1, localObject2);
              i5 = k;
            }
          }
        }
      }
      str = Settings.System.DEFAULT_NOTIFICATION_URI.getPath();
      boolean bool1 = ((SharedPreferences)localObject1).getBoolean("EnableInAppSounds", true);
      boolean bool2 = ((SharedPreferences)localObject1).getBoolean("EnableInAppVibrate", true);
      bool3 = ((SharedPreferences)localObject1).getBoolean("EnableInAppPreview", true);
      boolean bool4 = ((SharedPreferences)localObject1).getBoolean("EnableInAppPriority", false);
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      boolean bool5 = ((SharedPreferences)localObject1).getBoolean("custom_" + l1, false);
      if (!bool5) {
        break label2548;
      }
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      i6 = ((SharedPreferences)localObject1).getInt("vibrate_" + l1, 0);
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      i7 = ((SharedPreferences)localObject1).getInt("priority_" + l1, 3);
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      localObject8 = ((SharedPreferences)localObject1).getString("sound_path_" + l1, null);
      label715:
      int i8 = 0;
      if (j == 0) {
        break label2585;
      }
      if ((localObject8 == null) || (!((String)localObject8).equals(str))) {
        break label2560;
      }
      localObject2 = null;
      label741:
      k = ((SharedPreferences)localObject1).getInt("vibrate_group", 0);
      i4 = ((SharedPreferences)localObject1).getInt("priority_group", 1);
      n = ((SharedPreferences)localObject1).getInt("GroupLed", -16776961);
      label779:
      i3 = n;
      if (bool5)
      {
        localObject8 = new java/lang/StringBuilder;
        ((StringBuilder)localObject8).<init>();
        i3 = n;
        if (((SharedPreferences)localObject1).contains("color_" + l1))
        {
          localObject8 = new java/lang/StringBuilder;
          ((StringBuilder)localObject8).<init>();
          i3 = ((SharedPreferences)localObject1).getInt("color_" + l1, 0);
        }
      }
      if (i7 != 3) {
        i4 = i7;
      }
      i7 = k;
      i1 = i8;
      if (k == 4)
      {
        i1 = 1;
        i7 = 0;
      }
      if (((i7 != 2) || ((i6 != 1) && (i6 != 3))) && ((i7 == 2) || (i6 != 2)))
      {
        n = i7;
        if (i6 != 0)
        {
          n = i7;
          if (i6 == 4) {}
        }
      }
      else
      {
        n = i6;
      }
      bool5 = ApplicationLoader.mainInterfacePaused;
      localObject1 = localObject2;
      i6 = n;
      k = i4;
      if (!bool5)
      {
        if (!bool1) {
          localObject2 = null;
        }
        if (!bool2) {
          n = 2;
        }
        if (bool4) {
          break label2690;
        }
        k = 0;
        i6 = n;
        localObject1 = localObject2;
      }
    }
    Object localObject9;
    Object localObject10;
    Object localObject11;
    NotificationCompat.Builder localBuilder;
    for (;;)
    {
      i4 = i6;
      if (i1 != 0)
      {
        i4 = i6;
        if (i6 == 2) {}
      }
      try
      {
        n = audioManager.getRingerMode();
        i4 = i6;
        if (n != 0)
        {
          i4 = i6;
          if (n != 1) {
            i4 = 2;
          }
        }
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          Object localObject12;
          Object localObject13;
          Object localObject14;
          Object localObject15;
          Object localObject16;
          Object localObject17;
          FileLog.e(localException2);
          i4 = i6;
          continue;
          if (i4 == 1)
          {
            localObject3 = new long[4];
            Object tmp2747_2745 = localObject3;
            tmp2747_2745[0] = 0L;
            Object tmp2751_2747 = tmp2747_2745;
            tmp2751_2747[1] = 100L;
            Object tmp2757_2751 = tmp2751_2747;
            tmp2757_2751[2] = 0L;
            Object tmp2761_2757 = tmp2757_2751;
            tmp2761_2757[3] = 100L;
            tmp2761_2757;
          }
          else if ((i4 == 0) || (i4 == 4))
          {
            localObject3 = new long[0];
          }
          else if (i4 == 3)
          {
            localObject3 = new long[2];
            Object tmp2803_2801 = localObject3;
            tmp2803_2801[0] = 0L;
            Object tmp2807_2803 = tmp2803_2801;
            tmp2807_2803[1] = 1000L;
            tmp2807_2803;
            continue;
            localObject8 = Uri.parse((String)localObject1);
            continue;
            if ((k == 1) || (k == 2))
            {
              n = 4;
              localObject11 = localObject3;
              localObject9 = localObject8;
            }
            else if (k == 4)
            {
              n = 1;
              localObject11 = localObject3;
              localObject9 = localObject8;
            }
            else
            {
              localObject11 = localObject3;
              localObject9 = localObject8;
              n = i6;
              if (k == 5)
              {
                n = 2;
                localObject11 = localObject3;
                localObject9 = localObject8;
                continue;
                if (m != 0)
                {
                  ((Intent)localObject8).putExtra("userId", m);
                  continue;
                  localObject3 = localObject7;
                  if (this.pushDialogs.size() == 1) {
                    if (localObject6 != null)
                    {
                      localObject3 = localObject7;
                      if (((TLRPC.Chat)localObject6).photo != null)
                      {
                        localObject3 = localObject7;
                        if (((TLRPC.Chat)localObject6).photo.photo_small != null)
                        {
                          localObject3 = localObject7;
                          if (((TLRPC.Chat)localObject6).photo.photo_small.volume_id != 0L)
                          {
                            localObject3 = localObject7;
                            if (((TLRPC.Chat)localObject6).photo.photo_small.local_id != 0) {
                              localObject3 = ((TLRPC.Chat)localObject6).photo.photo_small;
                            }
                          }
                        }
                      }
                    }
                    else
                    {
                      localObject3 = localObject7;
                      if (localObject5 != null)
                      {
                        localObject3 = localObject7;
                        if (((TLRPC.User)localObject5).photo != null)
                        {
                          localObject3 = localObject7;
                          if (((TLRPC.User)localObject5).photo.photo_small != null)
                          {
                            localObject3 = localObject7;
                            if (((TLRPC.User)localObject5).photo.photo_small.volume_id != 0L)
                            {
                              localObject3 = localObject7;
                              if (((TLRPC.User)localObject5).photo.photo_small.local_id != 0)
                              {
                                localObject3 = ((TLRPC.User)localObject5).photo.photo_small;
                                continue;
                                localObject3 = localObject7;
                                if (this.pushDialogs.size() == 1)
                                {
                                  ((Intent)localObject8).putExtra("encId", (int)(l1 >> 32));
                                  localObject3 = localObject7;
                                  continue;
                                  if (localObject6 != null)
                                  {
                                    localObject8 = ((TLRPC.Chat)localObject6).title;
                                  }
                                  else
                                  {
                                    localObject8 = UserObject.getUserName((TLRPC.User)localObject5);
                                    continue;
                                    localObject13 = localObject8;
                                    continue;
                                    localObject1 = new java/lang/StringBuilder;
                                    ((StringBuilder)localObject1).<init>();
                                    localObject1 = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser()) + "・";
                                    continue;
                                    localObject1 = "";
                                    continue;
                                    localObject7 = new java/lang/StringBuilder;
                                    ((StringBuilder)localObject7).<init>();
                                    localObject7 = (String)localObject1 + LocaleController.formatString("NotificationMessagesPeopleDisplayOrder", NUM, new Object[] { LocaleController.formatPluralString("NewMessages", this.total_unread_count), LocaleController.formatPluralString("FromChats", this.pushDialogs.size()) });
                                    continue;
                                    k = 0;
                                    continue;
                                    if (localObject17[0] != 0)
                                    {
                                      localObject1 = new java/lang/StringBuilder;
                                      ((StringBuilder)localObject1).<init>();
                                      localObject1 = ((String)localObject5).replace((String)localObject13 + ": ", "");
                                    }
                                    else
                                    {
                                      localObject1 = new java/lang/StringBuilder;
                                      ((StringBuilder)localObject1).<init>();
                                      localObject1 = ((String)localObject5).replace((String)localObject13 + " ", "");
                                      continue;
                                      localBuilder.setContentText((CharSequence)localObject7);
                                      localObject17 = new android/support/v4/app/NotificationCompat$InboxStyle;
                                      ((NotificationCompat.InboxStyle)localObject17).<init>();
                                      ((NotificationCompat.InboxStyle)localObject17).setBigContentTitle((CharSequence)localObject13);
                                      i2 = Math.min(10, this.pushMessages.size());
                                      arrayOfBoolean = new boolean[1];
                                      j = 0;
                                      while (j < i2)
                                      {
                                        localMessageObject2 = (MessageObject)this.pushMessages.get(j);
                                        localObject5 = getStringForMessage(localMessageObject2, false, arrayOfBoolean);
                                        localObject10 = localObject1;
                                        i4 = k;
                                        if (localObject5 != null)
                                        {
                                          if (localMessageObject2.messageOwner.date <= i)
                                          {
                                            i4 = k;
                                            localObject10 = localObject1;
                                          }
                                        }
                                        else
                                        {
                                          j++;
                                          localObject1 = localObject10;
                                          k = i4;
                                          continue;
                                        }
                                        localObject10 = localObject1;
                                        i4 = k;
                                        if (k == 2)
                                        {
                                          localObject10 = localObject5;
                                          if (localMessageObject2.messageOwner.silent) {
                                            i4 = 1;
                                          }
                                        }
                                        else
                                        {
                                          localObject1 = localObject5;
                                          if (this.pushDialogs.size() == 1)
                                          {
                                            localObject1 = localObject5;
                                            if (m != 0)
                                            {
                                              if (localObject6 == null) {
                                                break label3605;
                                              }
                                              localObject1 = new java/lang/StringBuilder;
                                              ((StringBuilder)localObject1).<init>();
                                              localObject1 = ((String)localObject5).replace(" @ " + (String)localObject13, "");
                                            }
                                          }
                                        }
                                        for (;;)
                                        {
                                          ((NotificationCompat.InboxStyle)localObject17).addLine((CharSequence)localObject1);
                                          break;
                                          i4 = 0;
                                          break label3530;
                                          if (arrayOfBoolean[0] != 0)
                                          {
                                            localObject1 = new java/lang/StringBuilder;
                                            ((StringBuilder)localObject1).<init>();
                                            localObject1 = ((String)localObject5).replace((String)localObject13 + ": ", "");
                                          }
                                          else
                                          {
                                            localObject1 = new java/lang/StringBuilder;
                                            ((StringBuilder)localObject1).<init>();
                                            localObject1 = ((String)localObject5).replace((String)localObject13 + " ", "");
                                          }
                                        }
                                      }
                                      ((NotificationCompat.InboxStyle)localObject17).setSummaryText((CharSequence)localObject7);
                                      localBuilder.setStyle((NotificationCompat.Style)localObject17);
                                      i4 = k;
                                      continue;
                                      for (;;)
                                      {
                                        try
                                        {
                                          localObject3 = FileLoader.getPathToAttach((TLObject)localObject3, true);
                                          if (!((File)localObject3).exists()) {
                                            break;
                                          }
                                          f = 160.0F / AndroidUtilities.dp(50.0F);
                                          localObject10 = new android/graphics/BitmapFactory$Options;
                                          ((BitmapFactory.Options)localObject10).<init>();
                                          if (f >= 1.0F) {
                                            break label3794;
                                          }
                                          k = 1;
                                          ((BitmapFactory.Options)localObject10).inSampleSize = k;
                                          localObject3 = BitmapFactory.decodeFile(((File)localObject3).getAbsolutePath(), (BitmapFactory.Options)localObject10);
                                          if (localObject3 == null) {
                                            break;
                                          }
                                          localBuilder.setLargeIcon((Bitmap)localObject3);
                                        }
                                        catch (Throwable localThrowable) {}
                                        break;
                                        k = (int)f;
                                      }
                                      if (i7 == 0)
                                      {
                                        localBuilder.setPriority(0);
                                        k = i1;
                                        if (Build.VERSION.SDK_INT >= 26) {
                                          k = 3;
                                        }
                                      }
                                      else if ((i7 == 1) || (i7 == 2))
                                      {
                                        localBuilder.setPriority(1);
                                        k = i1;
                                        if (Build.VERSION.SDK_INT >= 26) {
                                          k = 4;
                                        }
                                      }
                                      else if (i7 == 4)
                                      {
                                        localBuilder.setPriority(-2);
                                        k = i1;
                                        if (Build.VERSION.SDK_INT >= 26) {
                                          k = 1;
                                        }
                                      }
                                      else
                                      {
                                        k = i1;
                                        if (i7 == 5)
                                        {
                                          localBuilder.setPriority(-1);
                                          k = i1;
                                          if (Build.VERSION.SDK_INT >= 26)
                                          {
                                            k = 2;
                                            continue;
                                            localObject1 = Uri.parse((String)localObject12);
                                            continue;
                                            if (((String)localObject12).equals(str))
                                            {
                                              localBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, 5);
                                              localObject1 = localObject16;
                                            }
                                            else
                                            {
                                              localBuilder.setSound(Uri.parse((String)localObject12), 5);
                                              localObject1 = localObject16;
                                              continue;
                                              if (i6 == 1)
                                              {
                                                localObject4 = new long[4];
                                                Object tmp4002_4000 = localObject4;
                                                tmp4002_4000[0] = 0L;
                                                Object tmp4006_4002 = tmp4002_4000;
                                                tmp4006_4002[1] = 100L;
                                                Object tmp4012_4006 = tmp4006_4002;
                                                tmp4012_4006[2] = 0L;
                                                Object tmp4016_4012 = tmp4012_4006;
                                                tmp4016_4012[3] = 100L;
                                                tmp4016_4012;
                                                localBuilder.setVibrate((long[])localObject4);
                                                localObject10 = localObject1;
                                              }
                                              else if ((i6 == 0) || (i6 == 4))
                                              {
                                                localBuilder.setDefaults(2);
                                                localObject4 = new long[0];
                                                localObject10 = localObject1;
                                              }
                                              else
                                              {
                                                localObject4 = localObject14;
                                                localObject10 = localObject1;
                                                if (i6 == 3)
                                                {
                                                  localObject4 = new long[2];
                                                  Object tmp4086_4084 = localObject4;
                                                  tmp4086_4084[0] = 0L;
                                                  Object tmp4090_4086 = tmp4086_4084;
                                                  tmp4090_4086[1] = 1000L;
                                                  tmp4090_4086;
                                                  localBuilder.setVibrate((long[])localObject4);
                                                  localObject10 = localObject1;
                                                  continue;
                                                  localObject4 = new long[2];
                                                  Object tmp4118_4116 = localObject4;
                                                  tmp4118_4116[0] = 0L;
                                                  Object tmp4122_4118 = tmp4118_4116;
                                                  tmp4122_4118[1] = 0L;
                                                  tmp4122_4118;
                                                  localBuilder.setVibrate((long[])localObject4);
                                                  localObject10 = localObject15;
                                                  continue;
                                                  m++;
                                                  i4 = i6;
                                                }
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        if ((i6 != 0) || (Build.VERSION.SDK_INT >= 24) || (SharedConfig.passcodeHash.length() != 0) || (!hasMessagesToReply())) {
          break label4244;
        }
        localObject1 = new android/content/Intent;
        ((Intent)localObject1).<init>(ApplicationLoader.applicationContext, PopupReplyReceiver.class);
        ((Intent)localObject1).putExtra("currentAccount", this.currentAccount);
        if (Build.VERSION.SDK_INT > 19) {
          break label4301;
        }
        localBuilder.addAction(NUM, LocaleController.getString("Reply", NUM), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, (Intent)localObject1, 134217728));
      }
      localObject9 = null;
      localObject10 = null;
      localObject11 = null;
      localObject2 = null;
      i6 = 0;
      n = i6;
      if (Build.VERSION.SDK_INT >= 26)
      {
        if (i4 != 2) {
          break label2734;
        }
        localObject2 = new long[2];
        Object tmp1086_1084 = localObject2;
        tmp1086_1084[0] = 0L;
        Object tmp1090_1086 = tmp1086_1084;
        tmp1090_1086[1] = 0L;
        tmp1090_1086;
        localObject8 = localObject10;
        if (localObject1 != null)
        {
          localObject8 = localObject10;
          if (!((String)localObject1).equals("NoSound"))
          {
            if (!((String)localObject1).equals(str)) {
              break label2817;
            }
            localObject8 = Settings.System.DEFAULT_NOTIFICATION_URI;
          }
        }
        if (k != 0) {
          break label2826;
        }
        n = 3;
        localObject9 = localObject8;
        localObject11 = localObject2;
      }
      localObject12 = localObject1;
      i6 = i4;
      i7 = k;
      if (i5 != 0)
      {
        i6 = 0;
        i7 = 0;
        i3 = 0;
        localObject12 = null;
      }
      localObject8 = new android/content/Intent;
      ((Intent)localObject8).<init>(ApplicationLoader.applicationContext, LaunchActivity.class);
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      ((Intent)localObject8).setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
      ((Intent)localObject8).setFlags(32768);
      if ((int)l1 == 0) {
        break label3108;
      }
      if (this.pushDialogs.size() == 1)
      {
        if (j == 0) {
          break label2904;
        }
        ((Intent)localObject8).putExtra("chatId", j);
      }
      if ((!AndroidUtilities.needShowPasscode(false)) && (!SharedConfig.isWaitingForPasscodeEnter)) {
        break label2923;
      }
      localObject2 = null;
      ((Intent)localObject8).putExtra("currentAccount", this.currentAccount);
      localObject10 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject8, NUM);
      m = 1;
      if (((j == 0) || (localObject6 != null)) && ((localObject5 != null) || (!localMessageObject1.isFcmMessage()))) {
        break label3145;
      }
      localObject8 = localMessageObject1.localName;
      if (((int)l1 != 0) && (this.pushDialogs.size() <= 1) && (!AndroidUtilities.needShowPasscode(false)) && (!SharedConfig.isWaitingForPasscodeEnter)) {
        break label3170;
      }
      localObject13 = LocaleController.getString("AppName", NUM);
      m = 0;
      if (UserConfig.getActivatedAccountsCount() <= 1) {
        break label3215;
      }
      if (this.pushDialogs.size() != 1) {
        break label3177;
      }
      localObject1 = UserObject.getFirstName(UserConfig.getInstance(this.currentAccount).getCurrentUser());
      if (this.pushDialogs.size() == 1)
      {
        localObject7 = localObject1;
        if (Build.VERSION.SDK_INT >= 23) {}
      }
      else
      {
        if (this.pushDialogs.size() != 1) {
          break label3222;
        }
        localObject7 = new java/lang/StringBuilder;
        ((StringBuilder)localObject7).<init>();
        localObject7 = (String)localObject1 + LocaleController.formatPluralString("NewMessages", this.total_unread_count);
      }
      localObject1 = new android/support/v4/app/NotificationCompat$Builder;
      ((NotificationCompat.Builder)localObject1).<init>(ApplicationLoader.applicationContext);
      localBuilder = ((NotificationCompat.Builder)localObject1).setContentTitle((CharSequence)localObject13).setSmallIcon(NUM).setAutoCancel(true).setNumber(this.total_unread_count).setContentIntent((PendingIntent)localObject10).setGroup(this.notificationGroup).setGroupSummary(true).setShowWhen(true).setWhen(localMessageObject1.messageOwner.date * 1000L).setColor(-13851168);
      localObject14 = null;
      i1 = 0;
      localObject15 = null;
      localObject16 = null;
      localBuilder.setCategory("msg");
      if ((localObject6 == null) && (localObject5 != null) && (((TLRPC.User)localObject5).phone != null) && (((TLRPC.User)localObject5).phone.length() > 0))
      {
        localObject1 = new java/lang/StringBuilder;
        ((StringBuilder)localObject1).<init>();
        localBuilder.addPerson("tel:+" + ((TLRPC.User)localObject5).phone);
      }
      k = 2;
      localObject1 = null;
      if (this.pushMessages.size() != 1) {
        break label3374;
      }
      localObject1 = (MessageObject)this.pushMessages.get(0);
      localObject17 = new boolean[1];
      localObject10 = getStringForMessage((MessageObject)localObject1, false, (boolean[])localObject17);
      localObject5 = localObject10;
      if (!((MessageObject)localObject1).messageOwner.silent) {
        break label3291;
      }
      k = 1;
      if (localObject5 == null) {
        break;
      }
      localObject1 = localObject5;
      if (m != 0)
      {
        if (localObject6 == null) {
          break label3297;
        }
        localObject1 = new java/lang/StringBuilder;
        ((StringBuilder)localObject1).<init>();
        localObject1 = ((String)localObject5).replace(" @ " + (String)localObject13, "");
      }
      localBuilder.setContentText((CharSequence)localObject1);
      localObject6 = new android/support/v4/app/NotificationCompat$BigTextStyle;
      ((NotificationCompat.BigTextStyle)localObject6).<init>();
      localBuilder.setStyle(((NotificationCompat.BigTextStyle)localObject6).bigText((CharSequence)localObject1));
      i4 = k;
      localObject1 = localObject10;
      localObject10 = new android/content/Intent;
      ((Intent)localObject10).<init>(ApplicationLoader.applicationContext, NotificationDismissReceiver.class);
      ((Intent)localObject10).putExtra("messageDate", localMessageObject1.messageOwner.date);
      ((Intent)localObject10).putExtra("currentAccount", this.currentAccount);
      localBuilder.setDeleteIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 1, (Intent)localObject10, 134217728));
      if (localObject2 != null)
      {
        localObject10 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject2, null, "50_50");
        if (localObject10 == null) {
          break label3705;
        }
        localBuilder.setLargeIcon(((BitmapDrawable)localObject10).getBitmap());
      }
      if ((paramBoolean) && (i4 != 1)) {
        break label3802;
      }
      localBuilder.setPriority(-1);
      k = i1;
      if (Build.VERSION.SDK_INT >= 26) {
        k = 2;
      }
      if ((i4 == 1) || (i5 != 0)) {
        break label4111;
      }
      if ((ApplicationLoader.mainInterfacePaused) || (bool3))
      {
        localObject2 = localObject1;
        if (((String)localObject1).length() > 100)
        {
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          localObject2 = ((String)localObject1).substring(0, 100).replace('\n', ' ').trim() + "...";
        }
        localBuilder.setTicker((CharSequence)localObject2);
      }
      localObject1 = localObject16;
      if (!MediaController.getInstance().isRecordingAudio())
      {
        localObject1 = localObject16;
        if (localObject12 != null)
        {
          localObject1 = localObject16;
          if (!((String)localObject12).equals("NoSound"))
          {
            if (Build.VERSION.SDK_INT < 26) {
              break label3945;
            }
            if (!((String)localObject12).equals(str)) {
              break label3936;
            }
            localObject1 = Settings.System.DEFAULT_NOTIFICATION_URI;
          }
        }
      }
      if (i3 != 0) {
        localBuilder.setLights(i3, 1000, 1000);
      }
      if ((i6 != 2) && (!MediaController.getInstance().isRecordingAudio())) {
        break label3989;
      }
      localObject2 = new long[2];
      Object tmp2106_2104 = localObject2;
      tmp2106_2104[0] = 0L;
      Object tmp2110_2106 = tmp2106_2104;
      tmp2110_2106[1] = 0L;
      tmp2110_2106;
      localBuilder.setVibrate((long[])localObject2);
      localObject10 = localObject1;
      m = 0;
      i4 = 0;
      i6 = m;
      if (AndroidUtilities.needShowPasscode(false)) {
        break label4152;
      }
      i6 = m;
      if (SharedConfig.isWaitingForPasscodeEnter) {
        break label4152;
      }
      i6 = m;
      if (localMessageObject1.getDialogId() != 777000L) {
        break label4152;
      }
      i6 = m;
      if (localMessageObject1.messageOwner.reply_markup == null) {
        break label4152;
      }
      localObject1 = localMessageObject1.messageOwner.reply_markup.rows;
      m = 0;
      i5 = ((ArrayList)localObject1).size();
      i6 = i4;
      if (m >= i5) {
        break label4152;
      }
      localObject12 = (TLRPC.TL_keyboardButtonRow)((ArrayList)localObject1).get(m);
      i7 = 0;
      j = ((TLRPC.TL_keyboardButtonRow)localObject12).buttons.size();
      i6 = i4;
      for (i4 = i7; i4 < j; i4++)
      {
        localObject13 = (TLRPC.KeyboardButton)((TLRPC.TL_keyboardButtonRow)localObject12).buttons.get(i4);
        if ((localObject13 instanceof TLRPC.TL_keyboardButtonCallback))
        {
          localObject6 = new android/content/Intent;
          ((Intent)localObject6).<init>(ApplicationLoader.applicationContext, NotificationCallbackReceiver.class);
          ((Intent)localObject6).putExtra("currentAccount", this.currentAccount);
          ((Intent)localObject6).putExtra("did", l1);
          if (((TLRPC.KeyboardButton)localObject13).data != null) {
            ((Intent)localObject6).putExtra("data", ((TLRPC.KeyboardButton)localObject13).data);
          }
          ((Intent)localObject6).putExtra("mid", localMessageObject1.getId());
          localObject13 = ((TLRPC.KeyboardButton)localObject13).text;
          localObject5 = ApplicationLoader.applicationContext;
          i6 = this.lastButtonId;
          this.lastButtonId = (i6 + 1);
          localBuilder.addAction(0, (CharSequence)localObject13, PendingIntent.getBroadcast((Context)localObject5, i6, (Intent)localObject6, 134217728));
          i6 = 1;
        }
      }
      j = localMessageObject1.messageOwner.to_id.channel_id;
      break label159;
      label2421:
      m = k;
      if (k != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
        break label185;
      }
      m = localMessageObject1.messageOwner.from_id;
      break label185;
      label2452:
      n = 2;
      i4 = 180;
      break label452;
      label2463:
      if (((Point)localObject2).y + i4 < System.currentTimeMillis() / 1000L)
      {
        ((Point)localObject2).set(1, (int)(System.currentTimeMillis() / 1000L));
        i5 = k;
        break label514;
      }
      i4 = ((Point)localObject2).x;
      if (i4 < n)
      {
        ((Point)localObject2).set(i4 + 1, (int)(System.currentTimeMillis() / 1000L));
        i5 = k;
        break label514;
      }
      i5 = 1;
      break label514;
      label2548:
      i6 = 0;
      i7 = 3;
      localObject8 = null;
      break label715;
      label2560:
      localObject2 = localObject8;
      if (localObject8 != null) {
        break label741;
      }
      localObject2 = ((SharedPreferences)localObject1).getString("GroupSoundPath", str);
      break label741;
      label2585:
      n = i2;
      localObject2 = localObject8;
      k = i1;
      i4 = i3;
      if (m == 0) {
        break label779;
      }
      if ((localObject8 != null) && (((String)localObject8).equals(str))) {
        localObject2 = null;
      }
      for (;;)
      {
        k = ((SharedPreferences)localObject1).getInt("vibrate_messages", 0);
        i4 = ((SharedPreferences)localObject1).getInt("priority_group", 1);
        n = ((SharedPreferences)localObject1).getInt("MessagesLed", -16776961);
        break;
        localObject2 = localObject8;
        if (localObject8 == null) {
          localObject2 = ((SharedPreferences)localObject1).getString("GlobalSoundPath", str);
        }
      }
      label2690:
      localObject1 = localObject2;
      i6 = n;
      k = i4;
      if (i4 == 2)
      {
        k = 1;
        localObject1 = localObject2;
        i6 = n;
      }
    }
    for (;;)
    {
      label2734:
      Object localObject3;
      label2817:
      label2826:
      label2904:
      label2923:
      label3108:
      label3145:
      label3170:
      label3177:
      label3215:
      label3222:
      label3291:
      label3297:
      label3374:
      boolean[] arrayOfBoolean;
      MessageObject localMessageObject2;
      label3530:
      label3605:
      label3705:
      float f;
      label3794:
      label3802:
      label3936:
      label3945:
      label3989:
      Object localObject4;
      label4111:
      label4152:
      label4244:
      if (Build.VERSION.SDK_INT >= 26) {
        localBuilder.setChannelId(validateChannelId(l1, (String)localObject8, (long[])localObject4, i3, (Uri)localObject10, k, (long[])localObject11, (Uri)localObject9, n));
      }
      showExtraNotifications(localBuilder, paramBoolean, (String)localObject7);
      this.lastNotificationIsNoData = false;
      scheduleNotificationRepeat();
      break;
      label4301:
      localBuilder.addAction(NUM, LocaleController.getString("Reply", NUM), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, (Intent)localObject1, 134217728));
    }
  }
  
  @TargetApi(26)
  private String validateChannelId(long paramLong, String paramString, long[] paramArrayOfLong1, int paramInt1, Uri paramUri1, int paramInt2, long[] paramArrayOfLong2, Uri paramUri2, int paramInt3)
  {
    SharedPreferences localSharedPreferences = MessagesController.getNotificationsSettings(this.currentAccount);
    String str1 = "org.telegram.key" + paramLong;
    paramUri2 = localSharedPreferences.getString(str1, null);
    String str2 = localSharedPreferences.getString(str1 + "_s", null);
    paramArrayOfLong2 = new StringBuilder();
    for (paramInt3 = 0; paramInt3 < paramArrayOfLong1.length; paramInt3++) {
      paramArrayOfLong2.append(paramArrayOfLong1[paramInt3]);
    }
    paramArrayOfLong2.append(paramInt1);
    if (paramUri1 != null) {
      paramArrayOfLong2.append(paramUri1.toString());
    }
    paramArrayOfLong2.append(paramInt2);
    String str3 = Utilities.MD5(paramArrayOfLong2.toString());
    paramArrayOfLong2 = paramUri2;
    if (paramUri2 != null)
    {
      paramArrayOfLong2 = paramUri2;
      if (!str2.equals(str3))
      {
        if (0 == 0) {
          break label446;
        }
        localSharedPreferences.edit().putString(str1, paramUri2).putString(str1 + "_s", str3).commit();
        paramArrayOfLong2 = paramUri2;
      }
    }
    paramUri2 = paramArrayOfLong2;
    if (paramArrayOfLong2 == null)
    {
      paramUri2 = this.currentAccount + "channel" + paramLong + "_" + Utilities.random.nextLong();
      paramString = new NotificationChannel(paramUri2, paramString, paramInt2);
      if (paramInt1 != 0)
      {
        paramString.enableLights(true);
        paramString.setLightColor(paramInt1);
      }
      if (isEmptyVibration(paramArrayOfLong1)) {
        break label460;
      }
      paramString.enableVibration(true);
      if ((paramArrayOfLong1 != null) && (paramArrayOfLong1.length > 0)) {
        paramString.setVibrationPattern(paramArrayOfLong1);
      }
      label347:
      if (paramUri1 == null) {
        break label468;
      }
      paramArrayOfLong1 = new AudioAttributes.Builder();
      paramArrayOfLong1.setContentType(4);
      paramArrayOfLong1.setUsage(5);
      paramString.setSound(paramUri1, paramArrayOfLong1.build());
    }
    for (;;)
    {
      systemNotificationManager.createNotificationChannel(paramString);
      localSharedPreferences.edit().putString(str1, paramUri2).putString(str1 + "_s", str3).commit();
      return paramUri2;
      label446:
      systemNotificationManager.deleteNotificationChannel(paramUri2);
      paramArrayOfLong2 = null;
      break;
      label460:
      paramString.enableVibration(false);
      break label347;
      label468:
      paramString.setSound(null, null);
    }
  }
  
  public void cleanup()
  {
    this.popupMessages.clear();
    this.popupReplyMessages.clear();
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.access$602(NotificationsController.this, 0L);
        NotificationsController.access$702(NotificationsController.this, 0);
        NotificationsController.access$802(NotificationsController.this, 0);
        NotificationsController.this.pushMessages.clear();
        NotificationsController.this.pushMessagesDict.clear();
        NotificationsController.this.pushDialogs.clear();
        NotificationsController.this.wearNotificationsIds.clear();
        NotificationsController.this.lastWearNotifiedMessageId.clear();
        NotificationsController.this.delayedPushMessages.clear();
        NotificationsController.access$1402(NotificationsController.this, false);
        NotificationsController.access$1502(NotificationsController.this, 0);
        try
        {
          if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
            NotificationsController.this.notificationDelayWakelock.release();
          }
          NotificationsController.this.setBadge(NotificationsController.access$1600(NotificationsController.this));
          localObject = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount).edit();
          ((SharedPreferences.Editor)localObject).clear();
          ((SharedPreferences.Editor)localObject).commit();
          if (Build.VERSION.SDK_INT < 26) {}
        }
        catch (Exception localException)
        {
          try
          {
            Object localObject = new java/lang/StringBuilder;
            ((StringBuilder)localObject).<init>();
            String str = NotificationsController.this.currentAccount + "channel";
            List localList = NotificationsController.systemNotificationManager.getNotificationChannels();
            int i = localList.size();
            int j = 0;
            while (j < i)
            {
              localObject = ((NotificationChannel)localList.get(j)).getId();
              if (((String)localObject).startsWith(str)) {
                NotificationsController.systemNotificationManager.deleteNotificationChannel((String)localObject);
              }
              j++;
              continue;
              localException = localException;
              FileLog.e(localException);
            }
          }
          catch (Throwable localThrowable)
          {
            FileLog.e(localThrowable);
          }
        }
      }
    });
  }
  
  protected void forceShowPopupForReply()
  {
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final ArrayList localArrayList = new ArrayList();
        int i = 0;
        if (i < NotificationsController.this.pushMessages.size())
        {
          MessageObject localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(i);
          long l = localMessageObject.getDialogId();
          if (((localMessageObject.messageOwner.mentioned) && ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))) || ((int)l == 0) || ((localMessageObject.messageOwner.to_id.channel_id != 0) && (!localMessageObject.isMegagroup()))) {}
          for (;;)
          {
            i++;
            break;
            localArrayList.add(0, localMessageObject);
          }
        }
        if ((!localArrayList.isEmpty()) && (!AndroidUtilities.needShowPasscode(false))) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationsController.this.popupReplyMessages = localArrayList;
              Intent localIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
              localIntent.putExtra("force", true);
              localIntent.putExtra("currentAccount", NotificationsController.this.currentAccount);
              localIntent.setFlags(268763140);
              ApplicationLoader.applicationContext.startActivity(localIntent);
              localIntent = new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS");
              ApplicationLoader.applicationContext.sendBroadcast(localIntent);
            }
          });
        }
      }
    });
  }
  
  public int getTotalUnreadCount()
  {
    return this.total_unread_count;
  }
  
  public boolean hasMessagesToReply()
  {
    for (int i = 0;; i++)
    {
      if (i >= this.pushMessages.size()) {
        break label90;
      }
      MessageObject localMessageObject = (MessageObject)this.pushMessages.get(i);
      long l = localMessageObject.getDialogId();
      if (((!localMessageObject.messageOwner.mentioned) || (!(localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))) && ((int)l != 0) && ((localMessageObject.messageOwner.to_id.channel_id == 0) || (localMessageObject.isMegagroup()))) {
        break;
      }
    }
    label90:
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void playOutChatSound()
  {
    if ((!this.inChatSoundEnabled) || (MediaController.getInstance().isRecordingAudio())) {}
    for (;;)
    {
      return;
      try
      {
        int i = audioManager.getRingerMode();
        if (i == 0) {
          continue;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
      notificationsQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundOutPlay) <= 100L) {}
            for (;;)
            {
              return;
              NotificationsController.access$3202(NotificationsController.this, System.currentTimeMillis());
              if (NotificationsController.this.soundPool == null)
              {
                Object localObject = NotificationsController.this;
                SoundPool localSoundPool = new android/media/SoundPool;
                localSoundPool.<init>(3, 1, 0);
                NotificationsController.access$2802((NotificationsController)localObject, localSoundPool);
                localSoundPool = NotificationsController.this.soundPool;
                localObject = new org/telegram/messenger/NotificationsController$16$1;
                ((1)localObject).<init>(this);
                localSoundPool.setOnLoadCompleteListener((SoundPool.OnLoadCompleteListener)localObject);
              }
              if ((NotificationsController.this.soundOut == 0) && (!NotificationsController.this.soundOutLoaded))
              {
                NotificationsController.access$3402(NotificationsController.this, true);
                NotificationsController.access$3302(NotificationsController.this, NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, NUM, 1));
              }
              int i = NotificationsController.this.soundOut;
              if (i != 0) {
                try
                {
                  NotificationsController.this.soundPool.play(NotificationsController.this.soundOut, 1.0F, 1.0F, 1, 0, 1.0F);
                }
                catch (Exception localException1)
                {
                  FileLog.e(localException1);
                }
              }
            }
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              FileLog.e(localException2);
            }
          }
        }
      });
    }
  }
  
  public void processDialogsUpdateRead(final LongSparseArray<Integer> paramLongSparseArray)
  {
    final ArrayList localArrayList = new ArrayList();
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = NotificationsController.this.total_unread_count;
        SharedPreferences localSharedPreferences = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
        int j = 0;
        if (j < paramLongSparseArray.size())
        {
          long l1 = paramLongSparseArray.keyAt(j);
          int k = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
          int m = k;
          if (NotificationsController.this.notifyCheck)
          {
            localObject = (Integer)NotificationsController.this.pushDialogsOverrideMention.get(l1);
            m = k;
            if (localObject != null)
            {
              m = k;
              if (((Integer)localObject).intValue() == 1)
              {
                NotificationsController.this.pushDialogsOverrideMention.put(l1, Integer.valueOf(0));
                m = 1;
              }
            }
          }
          label170:
          Integer localInteger1;
          Integer localInteger2;
          if ((m != 2) && (((localSharedPreferences.getBoolean("EnableAll", true)) && (((int)l1 >= 0) || (localSharedPreferences.getBoolean("EnableGroup", true)))) || (m != 0)))
          {
            m = 1;
            localInteger1 = (Integer)NotificationsController.this.pushDialogs.get(l1);
            localInteger2 = (Integer)paramLongSparseArray.get(l1);
            if (localInteger2.intValue() == 0) {
              NotificationsController.this.smartNotificationsDialogs.remove(l1);
            }
            localObject = localInteger2;
            if (localInteger2.intValue() >= 0) {
              break label266;
            }
            if (localInteger1 != null) {
              break label250;
            }
          }
          for (;;)
          {
            j++;
            break;
            m = 0;
            break label170;
            label250:
            localObject = Integer.valueOf(localInteger1.intValue() + localInteger2.intValue());
            label266:
            if (((m != 0) || (((Integer)localObject).intValue() == 0)) && (localInteger1 != null)) {
              NotificationsController.access$702(NotificationsController.this, NotificationsController.this.total_unread_count - localInteger1.intValue());
            }
            if (((Integer)localObject).intValue() == 0)
            {
              NotificationsController.this.pushDialogs.remove(l1);
              NotificationsController.this.pushDialogsOverrideMention.remove(l1);
              for (m = 0; m < NotificationsController.this.pushMessages.size(); m = k + 1)
              {
                localObject = (MessageObject)NotificationsController.this.pushMessages.get(m);
                k = m;
                if (((MessageObject)localObject).getDialogId() == l1)
                {
                  if (NotificationsController.this.isPersonalMessage((MessageObject)localObject)) {
                    NotificationsController.access$810(NotificationsController.this);
                  }
                  NotificationsController.this.pushMessages.remove(m);
                  k = m - 1;
                  NotificationsController.this.delayedPushMessages.remove(localObject);
                  long l2 = ((MessageObject)localObject).getId();
                  long l3 = l2;
                  if (((MessageObject)localObject).messageOwner.to_id.channel_id != 0) {
                    l3 = l2 | ((MessageObject)localObject).messageOwner.to_id.channel_id << 32;
                  }
                  NotificationsController.this.pushMessagesDict.remove(l3);
                  localArrayList.add(localObject);
                }
              }
            }
            else if (m != 0)
            {
              NotificationsController.access$702(NotificationsController.this, NotificationsController.this.total_unread_count + ((Integer)localObject).intValue());
              NotificationsController.this.pushDialogs.put(l1, localObject);
            }
          }
        }
        if (!localArrayList.isEmpty()) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i = 0;
              int j = NotificationsController.10.this.val$popupArrayToRemove.size();
              while (i < j)
              {
                NotificationsController.this.popupMessages.remove(NotificationsController.10.this.val$popupArrayToRemove.get(i));
                i++;
              }
            }
          });
        }
        if (i != NotificationsController.this.total_unread_count)
        {
          if (!NotificationsController.this.notifyCheck)
          {
            NotificationsController.this.delayedPushMessages.clear();
            NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, new Object[] { Integer.valueOf(NotificationsController.this.currentAccount) });
              }
            });
          }
        }
        else
        {
          NotificationsController.access$1402(NotificationsController.this, false);
          if (NotificationsController.this.showBadgeNumber) {
            NotificationsController.this.setBadge(NotificationsController.access$1600(NotificationsController.this));
          }
          return;
        }
        Object localObject = NotificationsController.this;
        if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime()) {}
        for (boolean bool = true;; bool = false)
        {
          ((NotificationsController)localObject).scheduleNotificationDelay(bool);
          break;
        }
      }
    });
  }
  
  public void processLoadedUnreadMessages(final LongSparseArray<Integer> paramLongSparseArray, final ArrayList<TLRPC.Message> paramArrayList, ArrayList<TLRPC.User> paramArrayList1, ArrayList<TLRPC.Chat> paramArrayList2, ArrayList<TLRPC.EncryptedChat> paramArrayList3)
  {
    MessagesController.getInstance(this.currentAccount).putUsers(paramArrayList1, true);
    MessagesController.getInstance(this.currentAccount).putChats(paramArrayList2, true);
    MessagesController.getInstance(this.currentAccount).putEncryptedChats(paramArrayList3, true);
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.this.pushDialogs.clear();
        NotificationsController.this.pushMessages.clear();
        NotificationsController.this.pushMessagesDict.clear();
        NotificationsController.access$702(NotificationsController.this, 0);
        NotificationsController.access$802(NotificationsController.this, 0);
        SharedPreferences localSharedPreferences = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
        Object localObject1 = new LongSparseArray();
        Object localObject2;
        long l1;
        int j;
        if (paramArrayList != null)
        {
          i = 0;
          if (i < paramArrayList.size())
          {
            localObject2 = (TLRPC.Message)paramArrayList.get(i);
            l1 = ((TLRPC.Message)localObject2).id;
            long l2 = l1;
            if (((TLRPC.Message)localObject2).to_id.channel_id != 0) {
              l2 = l1 | ((TLRPC.Message)localObject2).to_id.channel_id << 32;
            }
            if (NotificationsController.this.pushMessagesDict.indexOfKey(l2) >= 0) {}
            for (;;)
            {
              i++;
              break;
              localObject2 = new MessageObject(NotificationsController.this.currentAccount, (TLRPC.Message)localObject2, false);
              if (NotificationsController.this.isPersonalMessage((MessageObject)localObject2)) {
                NotificationsController.access$808(NotificationsController.this);
              }
              long l3 = ((MessageObject)localObject2).getDialogId();
              l1 = l3;
              if (((MessageObject)localObject2).messageOwner.mentioned) {
                l1 = ((MessageObject)localObject2).messageOwner.from_id;
              }
              j = ((LongSparseArray)localObject1).indexOfKey(l1);
              if (j < 0) {
                break label338;
              }
              bool = ((Boolean)((LongSparseArray)localObject1).valueAt(j)).booleanValue();
              label260:
              if ((!bool) || ((l1 == NotificationsController.this.opened_dialog_id) && (ApplicationLoader.isScreenOn))) {
                break label406;
              }
              NotificationsController.this.pushMessagesDict.put(l2, localObject2);
              NotificationsController.this.pushMessages.add(0, localObject2);
              if (l3 != l1) {
                NotificationsController.this.pushDialogsOverrideMention.put(l3, Integer.valueOf(1));
              }
            }
            label338:
            j = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
            if ((j != 2) && (((localSharedPreferences.getBoolean("EnableAll", true)) && (((int)l1 >= 0) || (localSharedPreferences.getBoolean("EnableGroup", true)))) || (j != 0))) {}
            for (bool = true;; bool = false)
            {
              ((LongSparseArray)localObject1).put(l1, Boolean.valueOf(bool));
              break label260;
              label406:
              break;
            }
          }
        }
        int i = 0;
        if (i < paramLongSparseArray.size())
        {
          l1 = paramLongSparseArray.keyAt(i);
          j = ((LongSparseArray)localObject1).indexOfKey(l1);
          if (j >= 0)
          {
            bool = ((Boolean)((LongSparseArray)localObject1).valueAt(j)).booleanValue();
            if (bool) {
              break label609;
            }
          }
          for (;;)
          {
            i++;
            break;
            int k = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
            localObject2 = (Integer)NotificationsController.this.pushDialogsOverrideMention.get(l1);
            j = k;
            if (localObject2 != null)
            {
              j = k;
              if (((Integer)localObject2).intValue() == 1)
              {
                NotificationsController.this.pushDialogsOverrideMention.put(l1, Integer.valueOf(0));
                j = 1;
              }
            }
            if ((j != 2) && (((localSharedPreferences.getBoolean("EnableAll", true)) && (((int)l1 >= 0) || (localSharedPreferences.getBoolean("EnableGroup", true)))) || (j != 0))) {}
            for (bool = true;; bool = false)
            {
              ((LongSparseArray)localObject1).put(l1, Boolean.valueOf(bool));
              break;
            }
            label609:
            j = ((Integer)paramLongSparseArray.valueAt(i)).intValue();
            NotificationsController.this.pushDialogs.put(l1, Integer.valueOf(j));
            NotificationsController.access$702(NotificationsController.this, NotificationsController.this.total_unread_count + j);
          }
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (NotificationsController.this.total_unread_count == 0)
            {
              NotificationsController.this.popupMessages.clear();
              NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, new Object[] { Integer.valueOf(NotificationsController.this.currentAccount) });
          }
        });
        localObject1 = NotificationsController.this;
        if (SystemClock.uptimeMillis() / 1000L < 60L) {}
        for (boolean bool = true;; bool = false)
        {
          ((NotificationsController)localObject1).showOrUpdateNotification(bool);
          if (NotificationsController.this.showBadgeNumber) {
            NotificationsController.this.setBadge(NotificationsController.access$1600(NotificationsController.this));
          }
          return;
        }
      }
    });
  }
  
  public void processNewMessages(final ArrayList<MessageObject> paramArrayList, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    if (paramArrayList.isEmpty()) {}
    for (;;)
    {
      return;
      final ArrayList localArrayList = new ArrayList(0);
      notificationsQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i = 0;
          LongSparseArray localLongSparseArray = new LongSparseArray();
          Object localObject1 = MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
          boolean bool1 = ((SharedPreferences)localObject1).getBoolean("PinnedMessages", true);
          final int j = 0;
          int k = 0;
          long l1;
          if (k < paramArrayList.size())
          {
            MessageObject localMessageObject = (MessageObject)paramArrayList.get(k);
            l1 = localMessageObject.getId();
            long l2 = l1;
            if (localMessageObject.messageOwner.to_id.channel_id != 0) {
              l2 = l1 | localMessageObject.messageOwner.to_id.channel_id << 32;
            }
            localObject2 = (MessageObject)NotificationsController.this.pushMessagesDict.get(l2);
            int n;
            if (localObject2 != null)
            {
              m = i;
              n = j;
              if (((MessageObject)localObject2).isFcmMessage())
              {
                NotificationsController.this.pushMessagesDict.put(l2, localMessageObject);
                i1 = NotificationsController.this.pushMessages.indexOf(localObject2);
                m = i;
                n = j;
                if (i1 >= 0)
                {
                  NotificationsController.this.pushMessages.set(i1, localMessageObject);
                  n = j;
                  m = i;
                }
              }
            }
            long l3;
            label279:
            do
            {
              for (;;)
              {
                k++;
                i = m;
                j = n;
                break;
                l3 = localMessageObject.getDialogId();
                if ((l3 != NotificationsController.this.opened_dialog_id) || (!ApplicationLoader.isScreenOn)) {
                  break label279;
                }
                m = i;
                n = j;
                if (!paramBoolean2)
                {
                  NotificationsController.this.playInChatSound();
                  m = i;
                  n = j;
                }
              }
              l1 = l3;
              if (!localMessageObject.messageOwner.mentioned) {
                break label331;
              }
              if (bool1) {
                break label320;
              }
              m = i;
              n = j;
            } while ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage));
            label320:
            l1 = localMessageObject.messageOwner.from_id;
            label331:
            if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
              NotificationsController.access$808(NotificationsController.this);
            }
            int i1 = 1;
            int m = (int)l1;
            if (m < 0)
            {
              i = 1;
              label366:
              n = localLongSparseArray.indexOfKey(l1);
              if (n < 0) {
                break label631;
              }
              bool2 = ((Boolean)localLongSparseArray.valueAt(n)).booleanValue();
              if (m != 0)
              {
                if (!((SharedPreferences)localObject1).getBoolean("custom_" + l1, false)) {
                  break label705;
                }
                i = ((SharedPreferences)localObject1).getInt("popup_" + l1, 0);
                label456:
                if (i != 0) {
                  break label717;
                }
                if ((int)l1 >= 0) {
                  break label710;
                }
                localObject2 = "popupGroup";
                label470:
                j = ((SharedPreferences)localObject1).getInt((String)localObject2, 0);
              }
            }
            for (;;)
            {
              i = j;
              if (j != 0)
              {
                i = j;
                if (localMessageObject.messageOwner.to_id.channel_id != 0)
                {
                  i = j;
                  if (!localMessageObject.isMegagroup()) {
                    i = 0;
                  }
                }
              }
              m = i1;
              n = i;
              if (!bool2) {
                break;
              }
              if (i != 0) {
                localArrayList.add(0, localMessageObject);
              }
              NotificationsController.this.delayedPushMessages.add(localMessageObject);
              NotificationsController.this.pushMessages.add(0, localMessageObject);
              NotificationsController.this.pushMessagesDict.put(l2, localMessageObject);
              m = i1;
              n = i;
              if (l3 == l1) {
                break;
              }
              NotificationsController.this.pushDialogsOverrideMention.put(l3, Integer.valueOf(1));
              m = i1;
              n = i;
              break;
              i = 0;
              break label366;
              label631:
              n = NotificationsController.this.getNotifyOverride((SharedPreferences)localObject1, l1);
              if ((n != 2) && (((((SharedPreferences)localObject1).getBoolean("EnableAll", true)) && ((i == 0) || (((SharedPreferences)localObject1).getBoolean("EnableGroup", true)))) || (n != 0))) {}
              for (bool2 = true;; bool2 = false)
              {
                localLongSparseArray.put(l1, Boolean.valueOf(bool2));
                break;
              }
              label705:
              i = 0;
              break label456;
              label710:
              localObject2 = "popupAll";
              break label470;
              label717:
              if (i == 1)
              {
                j = 3;
              }
              else
              {
                j = i;
                if (i == 2) {
                  j = 0;
                }
              }
            }
          }
          if (i != 0) {
            NotificationsController.access$1402(NotificationsController.this, paramBoolean1);
          }
          if ((!localArrayList.isEmpty()) && (!AndroidUtilities.needShowPasscode(false))) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationsController.this.popupMessages.addAll(0, NotificationsController.9.this.val$popupArrayAdd);
                if (((ApplicationLoader.mainInterfacePaused) || ((!ApplicationLoader.isScreenOn) && (!SharedConfig.isWaitingForPasscodeEnter))) && ((j == 3) || ((j == 1) && (ApplicationLoader.isScreenOn)) || ((j == 2) && (!ApplicationLoader.isScreenOn))))
                {
                  Intent localIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                  localIntent.setFlags(268763140);
                  ApplicationLoader.applicationContext.startActivity(localIntent);
                }
              }
            });
          }
          if ((i != 0) && (paramBoolean2))
          {
            l1 = ((MessageObject)paramArrayList.get(0)).getDialogId();
            k = NotificationsController.this.total_unread_count;
            i = NotificationsController.this.getNotifyOverride((SharedPreferences)localObject1, l1);
            j = i;
            if (NotificationsController.this.notifyCheck)
            {
              localObject2 = (Integer)NotificationsController.this.pushDialogsOverrideMention.get(l1);
              j = i;
              if (localObject2 != null)
              {
                j = i;
                if (((Integer)localObject2).intValue() == 1)
                {
                  NotificationsController.this.pushDialogsOverrideMention.put(l1, Integer.valueOf(0));
                  j = 1;
                }
              }
            }
            if ((j == 2) || (((!((SharedPreferences)localObject1).getBoolean("EnableAll", true)) || (((int)l1 < 0) && (!((SharedPreferences)localObject1).getBoolean("EnableGroup", true)))) && (j == 0))) {
              break label1136;
            }
            j = 1;
            localObject1 = (Integer)NotificationsController.this.pushDialogs.get(l1);
            if (localObject1 == null) {
              break label1142;
            }
          }
          label1136:
          label1142:
          for (i = ((Integer)localObject1).intValue() + 1;; i = 1)
          {
            localObject2 = Integer.valueOf(i);
            if (j != 0)
            {
              if (localObject1 != null) {
                NotificationsController.access$702(NotificationsController.this, NotificationsController.this.total_unread_count - ((Integer)localObject1).intValue());
              }
              NotificationsController.access$702(NotificationsController.this, NotificationsController.this.total_unread_count + ((Integer)localObject2).intValue());
              NotificationsController.this.pushDialogs.put(l1, localObject2);
            }
            if (k != NotificationsController.this.total_unread_count)
            {
              if (NotificationsController.this.notifyCheck) {
                break label1147;
              }
              NotificationsController.this.delayedPushMessages.clear();
              NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, new Object[] { Integer.valueOf(NotificationsController.this.currentAccount) });
                }
              });
            }
            NotificationsController.access$1402(NotificationsController.this, false);
            if (NotificationsController.this.showBadgeNumber) {
              NotificationsController.this.setBadge(NotificationsController.access$1600(NotificationsController.this));
            }
            return;
            j = 0;
            break;
          }
          label1147:
          Object localObject2 = NotificationsController.this;
          if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime()) {}
          for (boolean bool2 = true;; bool2 = false)
          {
            ((NotificationsController)localObject2).scheduleNotificationDelay(bool2);
            break;
          }
        }
      });
    }
  }
  
  public void processReadMessages(final SparseLongArray paramSparseLongArray, final long paramLong, final int paramInt1, int paramInt2, final boolean paramBoolean)
  {
    final ArrayList localArrayList = new ArrayList(0);
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i;
        int k;
        MessageObject localMessageObject;
        int m;
        long l2;
        long l3;
        if (paramSparseLongArray != null) {
          for (i = 0; i < paramSparseLongArray.size(); i++)
          {
            int j = paramSparseLongArray.keyAt(i);
            long l1 = paramSparseLongArray.get(j);
            for (k = 0; k < NotificationsController.this.pushMessages.size(); k = m + 1)
            {
              localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(k);
              m = k;
              if (localMessageObject.getDialogId() == j)
              {
                m = k;
                if (localMessageObject.getId() <= (int)l1)
                {
                  if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                    NotificationsController.access$810(NotificationsController.this);
                  }
                  localArrayList.add(localMessageObject);
                  l2 = localMessageObject.getId();
                  l3 = l2;
                  if (localMessageObject.messageOwner.to_id.channel_id != 0) {
                    l3 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
                  }
                  NotificationsController.this.pushMessagesDict.remove(l3);
                  NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                  NotificationsController.this.pushMessages.remove(k);
                  m = k - 1;
                }
              }
            }
          }
        }
        if ((paramLong != 0L) && ((paramInt1 != 0) || (paramBoolean != 0)))
        {
          i = 0;
          if (i < NotificationsController.this.pushMessages.size())
          {
            localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(i);
            m = i;
            if (localMessageObject.getDialogId() == paramLong)
            {
              k = 0;
              if (paramBoolean == 0) {
                break label470;
              }
              if (localMessageObject.messageOwner.date <= paramBoolean) {
                k = 1;
              }
            }
            for (;;)
            {
              m = i;
              if (k != 0)
              {
                if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                  NotificationsController.access$810(NotificationsController.this);
                }
                NotificationsController.this.pushMessages.remove(i);
                NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                localArrayList.add(localMessageObject);
                l2 = localMessageObject.getId();
                l3 = l2;
                if (localMessageObject.messageOwner.to_id.channel_id != 0) {
                  l3 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
                }
                NotificationsController.this.pushMessagesDict.remove(l3);
                m = i - 1;
              }
              i = m + 1;
              break;
              label470:
              if (!this.val$isPopup)
              {
                if ((localMessageObject.getId() <= paramInt1) || (paramInt1 < 0)) {
                  k = 1;
                }
              }
              else if ((localMessageObject.getId() == paramInt1) || (paramInt1 < 0)) {
                k = 1;
              }
            }
          }
        }
        if (!localArrayList.isEmpty()) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i = 0;
              int j = NotificationsController.8.this.val$popupArrayRemove.size();
              while (i < j)
              {
                NotificationsController.this.popupMessages.remove(NotificationsController.8.this.val$popupArrayRemove.get(i));
                i++;
              }
              NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
            }
          });
        }
      }
    });
  }
  
  public void removeDeletedHisoryFromNotifications(final SparseIntArray paramSparseIntArray)
  {
    final ArrayList localArrayList = new ArrayList(0);
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = NotificationsController.this.total_unread_count;
        MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
        for (int j = 0; j < paramSparseIntArray.size(); j++)
        {
          int k = paramSparseIntArray.keyAt(j);
          long l = -k;
          int m = paramSparseIntArray.get(k);
          Object localObject1 = (Integer)NotificationsController.this.pushDialogs.get(l);
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = Integer.valueOf(0);
          }
          localObject1 = localObject2;
          k = 0;
          while (k < NotificationsController.this.pushMessages.size())
          {
            MessageObject localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(k);
            int n = k;
            localObject3 = localObject1;
            if (localMessageObject.getDialogId() == l)
            {
              n = k;
              localObject3 = localObject1;
              if (localMessageObject.getId() <= m)
              {
                NotificationsController.this.pushMessagesDict.remove(localMessageObject.getIdWithChannel());
                NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                NotificationsController.this.pushMessages.remove(localMessageObject);
                n = k - 1;
                if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                  NotificationsController.access$810(NotificationsController.this);
                }
                localArrayList.add(localMessageObject);
                localObject3 = Integer.valueOf(((Integer)localObject1).intValue() - 1);
              }
            }
            k = n + 1;
            localObject1 = localObject3;
          }
          Object localObject3 = localObject1;
          if (((Integer)localObject1).intValue() <= 0)
          {
            localObject3 = Integer.valueOf(0);
            NotificationsController.this.smartNotificationsDialogs.remove(l);
          }
          if (!((Integer)localObject3).equals(localObject2))
          {
            NotificationsController.access$702(NotificationsController.this, NotificationsController.this.total_unread_count - ((Integer)localObject2).intValue());
            NotificationsController.access$702(NotificationsController.this, NotificationsController.this.total_unread_count + ((Integer)localObject3).intValue());
            NotificationsController.this.pushDialogs.put(l, localObject3);
          }
          if (((Integer)localObject3).intValue() == 0)
          {
            NotificationsController.this.pushDialogs.remove(l);
            NotificationsController.this.pushDialogsOverrideMention.remove(l);
          }
        }
        if (localArrayList.isEmpty()) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i = 0;
              int j = NotificationsController.7.this.val$popupArrayRemove.size();
              while (i < j)
              {
                NotificationsController.this.popupMessages.remove(NotificationsController.7.this.val$popupArrayRemove.get(i));
                i++;
              }
            }
          });
        }
        if (i != NotificationsController.this.total_unread_count)
        {
          if (!NotificationsController.this.notifyCheck)
          {
            NotificationsController.this.delayedPushMessages.clear();
            NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, new Object[] { Integer.valueOf(NotificationsController.this.currentAccount) });
              }
            });
          }
        }
        else
        {
          NotificationsController.access$1402(NotificationsController.this, false);
          if (NotificationsController.this.showBadgeNumber) {
            NotificationsController.this.setBadge(NotificationsController.access$1600(NotificationsController.this));
          }
          return;
        }
        Object localObject2 = NotificationsController.this;
        if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime()) {}
        for (boolean bool = true;; bool = false)
        {
          ((NotificationsController)localObject2).scheduleNotificationDelay(bool);
          break;
        }
      }
    });
  }
  
  public void removeDeletedMessagesFromNotifications(final SparseArray<ArrayList<Integer>> paramSparseArray)
  {
    final ArrayList localArrayList = new ArrayList(0);
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = NotificationsController.this.total_unread_count;
        MessagesController.getNotificationsSettings(NotificationsController.this.currentAccount);
        for (int j = 0; j < paramSparseArray.size(); j++)
        {
          int k = paramSparseArray.keyAt(j);
          long l1 = -k;
          ArrayList localArrayList = (ArrayList)paramSparseArray.get(k);
          Object localObject1 = (Integer)NotificationsController.this.pushDialogs.get(l1);
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = Integer.valueOf(0);
          }
          localObject1 = localObject2;
          int m = 0;
          while (m < localArrayList.size())
          {
            long l2 = ((Integer)localArrayList.get(m)).intValue() | k << 32;
            MessageObject localMessageObject = (MessageObject)NotificationsController.this.pushMessagesDict.get(l2);
            localObject3 = localObject1;
            if (localMessageObject != null)
            {
              NotificationsController.this.pushMessagesDict.remove(l2);
              NotificationsController.this.delayedPushMessages.remove(localMessageObject);
              NotificationsController.this.pushMessages.remove(localMessageObject);
              if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                NotificationsController.access$810(NotificationsController.this);
              }
              localArrayList.add(localMessageObject);
              localObject3 = Integer.valueOf(((Integer)localObject1).intValue() - 1);
            }
            m++;
            localObject1 = localObject3;
          }
          Object localObject3 = localObject1;
          if (((Integer)localObject1).intValue() <= 0)
          {
            localObject3 = Integer.valueOf(0);
            NotificationsController.this.smartNotificationsDialogs.remove(l1);
          }
          if (!((Integer)localObject3).equals(localObject2))
          {
            NotificationsController.access$702(NotificationsController.this, NotificationsController.this.total_unread_count - ((Integer)localObject2).intValue());
            NotificationsController.access$702(NotificationsController.this, NotificationsController.this.total_unread_count + ((Integer)localObject3).intValue());
            NotificationsController.this.pushDialogs.put(l1, localObject3);
          }
          if (((Integer)localObject3).intValue() == 0)
          {
            NotificationsController.this.pushDialogs.remove(l1);
            NotificationsController.this.pushDialogsOverrideMention.remove(l1);
          }
        }
        if (!localArrayList.isEmpty()) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i = 0;
              int j = NotificationsController.6.this.val$popupArrayRemove.size();
              while (i < j)
              {
                NotificationsController.this.popupMessages.remove(NotificationsController.6.this.val$popupArrayRemove.get(i));
                i++;
              }
            }
          });
        }
        if (i != NotificationsController.this.total_unread_count)
        {
          if (!NotificationsController.this.notifyCheck)
          {
            NotificationsController.this.delayedPushMessages.clear();
            NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.notificationsCountUpdated, new Object[] { Integer.valueOf(NotificationsController.this.currentAccount) });
              }
            });
          }
        }
        else
        {
          NotificationsController.access$1402(NotificationsController.this, false);
          if (NotificationsController.this.showBadgeNumber) {
            NotificationsController.this.setBadge(NotificationsController.access$1600(NotificationsController.this));
          }
          return;
        }
        Object localObject2 = NotificationsController.this;
        if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance(NotificationsController.this.currentAccount).getCurrentTime()) {}
        for (boolean bool = true;; bool = false)
        {
          ((NotificationsController)localObject2).scheduleNotificationDelay(bool);
          break;
        }
      }
    });
  }
  
  public void removeNotificationsForDialog(long paramLong)
  {
    getInstance(this.currentAccount).processReadMessages(null, paramLong, 0, Integer.MAX_VALUE, false);
    LongSparseArray localLongSparseArray = new LongSparseArray();
    localLongSparseArray.put(paramLong, Integer.valueOf(0));
    getInstance(this.currentAccount).processDialogsUpdateRead(localLongSparseArray);
  }
  
  protected void repeatNotificationMaybe()
  {
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = Calendar.getInstance().get(11);
        if ((i >= 11) && (i <= 22))
        {
          NotificationsController.notificationManager.cancel(NotificationsController.this.notificationId);
          NotificationsController.this.showOrUpdateNotification(true);
        }
        for (;;)
        {
          return;
          NotificationsController.this.scheduleNotificationRepeat();
        }
      }
    });
  }
  
  public void setBadgeEnabled(boolean paramBoolean)
  {
    this.showBadgeNumber = paramBoolean;
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.this.setBadge(NotificationsController.access$1600(NotificationsController.this));
      }
    });
  }
  
  public void setInChatSoundEnabled(boolean paramBoolean)
  {
    this.inChatSoundEnabled = paramBoolean;
  }
  
  public void setLastOnlineFromOtherDevice(final int paramInt)
  {
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("set last online from other device = " + paramInt);
        }
        NotificationsController.access$2002(NotificationsController.this, paramInt);
      }
    });
  }
  
  public void setOpenedDialogId(final long paramLong)
  {
    notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.access$602(NotificationsController.this, paramLong);
      }
    });
  }
  
  public void updateServerNotificationsSettings(long paramLong)
  {
    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
    if ((int)paramLong == 0) {}
    TLRPC.TL_account_updateNotifySettings localTL_account_updateNotifySettings;
    for (;;)
    {
      return;
      SharedPreferences localSharedPreferences = MessagesController.getNotificationsSettings(this.currentAccount);
      localTL_account_updateNotifySettings = new TLRPC.TL_account_updateNotifySettings();
      localTL_account_updateNotifySettings.settings = new TLRPC.TL_inputPeerNotifySettings();
      localTL_account_updateNotifySettings.settings.sound = "default";
      i = localSharedPreferences.getInt("notify2_" + paramLong, 0);
      if (i != 3) {
        break;
      }
      localTL_account_updateNotifySettings.settings.mute_until = localSharedPreferences.getInt("notifyuntil_" + paramLong, 0);
      localTL_account_updateNotifySettings.settings.show_previews = localSharedPreferences.getBoolean("preview_" + paramLong, true);
      localTL_account_updateNotifySettings.settings.silent = localSharedPreferences.getBoolean("silent_" + paramLong, false);
      localTL_account_updateNotifySettings.peer = new TLRPC.TL_inputNotifyPeer();
      ((TLRPC.TL_inputNotifyPeer)localTL_account_updateNotifySettings.peer).peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int)paramLong);
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_account_updateNotifySettings, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
    }
    TLRPC.TL_inputPeerNotifySettings localTL_inputPeerNotifySettings = localTL_account_updateNotifySettings.settings;
    if (i != 2) {}
    for (int i = 0;; i = Integer.MAX_VALUE)
    {
      localTL_inputPeerNotifySettings.mute_until = i;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/NotificationsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */