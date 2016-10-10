package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
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
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.RemoteInput.Builder;
import android.text.TextUtils;
import android.util.SparseArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_game;
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
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PopupNotificationActivity;

public class NotificationsController
{
  public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
  private static volatile NotificationsController Instance = null;
  private AlarmManager alarmManager;
  protected AudioManager audioManager;
  private ArrayList<MessageObject> delayedPushMessages = new ArrayList();
  private boolean inChatSoundEnabled = true;
  private int lastBadgeCount;
  private int lastOnlineFromOtherDevice = 0;
  private long lastSoundOutPlay;
  private long lastSoundPlay;
  private String launcherClassName;
  private Runnable notificationDelayRunnable;
  private PowerManager.WakeLock notificationDelayWakelock;
  private NotificationManagerCompat notificationManager = null;
  private DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
  private boolean notifyCheck = false;
  private long opened_dialog_id = 0L;
  private int personal_count = 0;
  public ArrayList<MessageObject> popupMessages = new ArrayList();
  public ArrayList<MessageObject> popupReplyMessages = new ArrayList();
  private HashMap<Long, Integer> pushDialogs = new HashMap();
  private HashMap<Long, Integer> pushDialogsOverrideMention = new HashMap();
  private ArrayList<MessageObject> pushMessages = new ArrayList();
  private HashMap<Long, MessageObject> pushMessagesDict = new HashMap();
  private HashMap<Long, Point> smartNotificationsDialogs = new HashMap();
  private int soundIn;
  private boolean soundInLoaded;
  private int soundOut;
  private boolean soundOutLoaded;
  private SoundPool soundPool;
  private int soundRecord;
  private boolean soundRecordLoaded;
  private int total_unread_count = 0;
  private HashMap<Long, Integer> wearNotificationsIds = new HashMap();
  
  public NotificationsController()
  {
    try
    {
      this.audioManager = ((AudioManager)ApplicationLoader.applicationContext.getSystemService("audio"));
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
                FileLog.e("tmessages", "delay reached");
                if (!NotificationsController.this.delayedPushMessages.isEmpty())
                {
                  NotificationsController.this.showOrUpdateNotification(true);
                  NotificationsController.this.delayedPushMessages.clear();
                }
                try
                {
                  if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
                    NotificationsController.this.notificationDelayWakelock.release();
                  }
                  return;
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                }
              }
            };
            return;
            localException1 = localException1;
            FileLog.e("tmessages", localException1);
            continue;
            localException2 = localException2;
            FileLog.e("tmessages", localException2);
          }
        }
        catch (Exception localException3)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException3);
          }
        }
      }
    }
  }
  
  private void dismissNotification()
  {
    try
    {
      this.notificationManager.cancel(1);
      this.pushMessages.clear();
      this.pushMessagesDict.clear();
      Iterator localIterator = this.wearNotificationsIds.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        this.notificationManager.cancel(((Integer)localEntry.getValue()).intValue());
      }
      this.wearNotificationsIds.clear();
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
      return;
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
      }
    });
  }
  
  public static NotificationsController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          NotificationsController localNotificationsController2 = Instance;
          localObject1 = localNotificationsController2;
          if (localNotificationsController2 == null) {
            localObject1 = new NotificationsController();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (NotificationsController)localObject1;
          return (NotificationsController)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localNotificationsController1;
  }
  
  private static String getLauncherClassName(Context paramContext)
  {
    try
    {
      Object localObject1 = paramContext.getPackageManager();
      Object localObject2 = new Intent("android.intent.action.MAIN");
      ((Intent)localObject2).addCategory("android.intent.category.LAUNCHER");
      localObject1 = ((PackageManager)localObject1).queryIntentActivities((Intent)localObject2, 0).iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (ResolveInfo)((Iterator)localObject1).next();
        if (((ResolveInfo)localObject2).activityInfo.applicationInfo.packageName.equalsIgnoreCase(paramContext.getPackageName()))
        {
          paramContext = ((ResolveInfo)localObject2).activityInfo.name;
          return paramContext;
        }
      }
    }
    catch (Throwable paramContext)
    {
      FileLog.e("tmessages", paramContext);
    }
    return null;
  }
  
  private int getNotifyOverride(SharedPreferences paramSharedPreferences, long paramLong)
  {
    int j = paramSharedPreferences.getInt("notify2_" + paramLong, 0);
    int i = j;
    if (j == 3)
    {
      i = j;
      if (paramSharedPreferences.getInt("notifyuntil_" + paramLong, 0) >= ConnectionsManager.getInstance().getCurrentTime()) {
        i = 2;
      }
    }
    return i;
  }
  
  private String getStringForMessage(MessageObject paramMessageObject, boolean paramBoolean)
  {
    long l2 = paramMessageObject.messageOwner.dialog_id;
    int j;
    int k;
    int i;
    label73:
    long l1;
    label95:
    String str;
    Object localObject1;
    if (paramMessageObject.messageOwner.to_id.chat_id != 0)
    {
      j = paramMessageObject.messageOwner.to_id.chat_id;
      k = paramMessageObject.messageOwner.to_id.user_id;
      if (k != 0) {
        break label155;
      }
      if ((!paramMessageObject.isFromUser()) && (paramMessageObject.getId() >= 0)) {
        break label148;
      }
      i = paramMessageObject.messageOwner.from_id;
      l1 = l2;
      if (l2 == 0L)
      {
        if (j == 0) {
          break label177;
        }
        l1 = -j;
      }
      str = null;
      if (i <= 0) {
        break label192;
      }
      localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(i));
      if (localObject1 != null) {
        str = UserObject.getUserName((TLRPC.User)localObject1);
      }
      label126:
      if (str != null) {
        break label220;
      }
    }
    label148:
    label155:
    label177:
    label192:
    label220:
    label538:
    label1300:
    label1319:
    label3924:
    label4779:
    label5608:
    do
    {
      do
      {
        do
        {
          do
          {
            Object localObject2;
            do
            {
              do
              {
                do
                {
                  return null;
                  j = paramMessageObject.messageOwner.to_id.channel_id;
                  break;
                  i = -j;
                  break label73;
                  i = k;
                  if (k != UserConfig.getClientUserId()) {
                    break label73;
                  }
                  i = paramMessageObject.messageOwner.from_id;
                  break label73;
                  l1 = l2;
                  if (i == 0) {
                    break label95;
                  }
                  l1 = i;
                  break label95;
                  localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-i));
                  if (localObject1 == null) {
                    break label126;
                  }
                  str = ((TLRPC.Chat)localObject1).title;
                  break label126;
                  localObject1 = null;
                  if (j != 0)
                  {
                    localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(j));
                    localObject1 = localObject2;
                    if (localObject2 == null) {
                      return null;
                    }
                  }
                  if (((int)l1 == 0) || (AndroidUtilities.needShowPasscode(false)) || (UserConfig.isWaitingForPasscodeEnter)) {
                    return LocaleController.getString("YouHaveNewMessage", 2131166417);
                  }
                  if ((j != 0) || (i == 0)) {
                    break label1319;
                  }
                  if (!ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewAll", true)) {
                    break label1300;
                  }
                  if (!(paramMessageObject.messageOwner instanceof TLRPC.TL_messageService)) {
                    break label538;
                  }
                  if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserJoined)) {
                    return LocaleController.formatString("NotificationContactJoined", 2131165990, new Object[] { str });
                  }
                  if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto)) {
                    return LocaleController.formatString("NotificationContactNewPhoto", 2131165991, new Object[] { str });
                  }
                  if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
                  {
                    str = LocaleController.formatString("formatDateAtTime", 2131166435, new Object[] { LocaleController.getInstance().formatterYear.format(paramMessageObject.messageOwner.date * 1000L), LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L) });
                    return LocaleController.formatString("NotificationUnrecognizedDevice", 2131166029, new Object[] { UserConfig.getCurrentUser().first_name, str, paramMessageObject.messageOwner.action.title, paramMessageObject.messageOwner.action.address });
                  }
                } while (!(paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionGameScore));
                return paramMessageObject.messageText.toString();
                if (paramMessageObject.isMediaEmpty())
                {
                  if (!paramBoolean)
                  {
                    if ((paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0)) {
                      return LocaleController.formatString("NotificationMessageText", 2131166026, new Object[] { str, paramMessageObject.messageOwner.message });
                    }
                    return LocaleController.formatString("NotificationMessageNoText", 2131166022, new Object[] { str });
                  }
                  return LocaleController.formatString("NotificationMessageNoText", 2131166022, new Object[] { str });
                }
                if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                {
                  if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
                    return LocaleController.formatString("NotificationMessageText", 2131166026, new Object[] { str, "🖼 " + paramMessageObject.messageOwner.media.caption });
                  }
                  return LocaleController.formatString("NotificationMessagePhoto", 2131166023, new Object[] { str });
                }
                if (paramMessageObject.isVideo())
                {
                  if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
                    return LocaleController.formatString("NotificationMessageText", 2131166026, new Object[] { str, "📹 " + paramMessageObject.messageOwner.media.caption });
                  }
                  return LocaleController.formatString("NotificationMessageVideo", 2131166027, new Object[] { str });
                }
                if (paramMessageObject.isGame()) {
                  return LocaleController.formatString("NotificationMessageGame", 2131166005, new Object[] { str, paramMessageObject.messageOwner.media.game.title });
                }
                if (paramMessageObject.isVoice()) {
                  return LocaleController.formatString("NotificationMessageAudio", 2131166002, new Object[] { str });
                }
                if (paramMessageObject.isMusic()) {
                  return LocaleController.formatString("NotificationMessageMusic", 2131166021, new Object[] { str });
                }
                if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact)) {
                  return LocaleController.formatString("NotificationMessageContact", 2131166003, new Object[] { str });
                }
                if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue))) {
                  return LocaleController.formatString("NotificationMessageMap", 2131166020, new Object[] { str });
                }
              } while (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument));
              if (paramMessageObject.isSticker())
              {
                paramMessageObject = paramMessageObject.getStickerEmoji();
                if (paramMessageObject != null) {
                  return LocaleController.formatString("NotificationMessageStickerEmoji", 2131166025, new Object[] { str, paramMessageObject });
                }
                return LocaleController.formatString("NotificationMessageSticker", 2131166024, new Object[] { str });
              }
              if (paramMessageObject.isGif())
              {
                if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
                  return LocaleController.formatString("NotificationMessageText", 2131166026, new Object[] { str, "🎬 " + paramMessageObject.messageOwner.media.caption });
                }
                return LocaleController.formatString("NotificationMessageGif", 2131166006, new Object[] { str });
              }
              if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
                return LocaleController.formatString("NotificationMessageText", 2131166026, new Object[] { str, "📎 " + paramMessageObject.messageOwner.media.caption });
              }
              return LocaleController.formatString("NotificationMessageDocument", 2131166004, new Object[] { str });
              return LocaleController.formatString("NotificationMessageNoText", 2131166022, new Object[] { str });
            } while (j == 0);
            if (!ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewGroup", true)) {
              break label6493;
            }
            if (!(paramMessageObject.messageOwner instanceof TLRPC.TL_messageService)) {
              break label3924;
            }
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser))
            {
              k = paramMessageObject.messageOwner.action.user_id;
              j = k;
              if (k == 0)
              {
                j = k;
                if (paramMessageObject.messageOwner.action.users.size() == 1) {
                  j = ((Integer)paramMessageObject.messageOwner.action.users.get(0)).intValue();
                }
              }
              if (j != 0)
              {
                if ((paramMessageObject.messageOwner.to_id.channel_id != 0) && (!((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("ChannelAddedByNotification", 2131165407, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                if (j == UserConfig.getClientUserId()) {
                  return LocaleController.formatString("NotificationInvitedToGroup", 2131166000, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(j));
                if (paramMessageObject == null) {
                  return null;
                }
                if (i == paramMessageObject.id)
                {
                  if (((TLRPC.Chat)localObject1).megagroup) {
                    return LocaleController.formatString("NotificationGroupAddSelfMega", 2131165996, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                  }
                  return LocaleController.formatString("NotificationGroupAddSelf", 2131165995, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationGroupAddMember", 2131165994, new Object[] { str, ((TLRPC.Chat)localObject1).title, UserObject.getUserName(paramMessageObject) });
              }
              localObject2 = new StringBuilder("");
              i = 0;
              while (i < paramMessageObject.messageOwner.action.users.size())
              {
                Object localObject3 = MessagesController.getInstance().getUser((Integer)paramMessageObject.messageOwner.action.users.get(i));
                if (localObject3 != null)
                {
                  localObject3 = UserObject.getUserName((TLRPC.User)localObject3);
                  if (((StringBuilder)localObject2).length() != 0) {
                    ((StringBuilder)localObject2).append(", ");
                  }
                  ((StringBuilder)localObject2).append((String)localObject3);
                }
                i += 1;
              }
              return LocaleController.formatString("NotificationGroupAddMember", 2131165994, new Object[] { str, ((TLRPC.Chat)localObject1).title, ((StringBuilder)localObject2).toString() });
            }
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByLink)) {
              return LocaleController.formatString("NotificationInvitedToGroupByLink", 2131166001, new Object[] { str, ((TLRPC.Chat)localObject1).title });
            }
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatEditTitle)) {
              return LocaleController.formatString("NotificationEditedGroupName", 2131165992, new Object[] { str, paramMessageObject.messageOwner.action.title });
            }
            if (((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto)) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatDeletePhoto)))
            {
              if ((paramMessageObject.messageOwner.to_id.channel_id != 0) && (!((TLRPC.Chat)localObject1).megagroup)) {
                return LocaleController.formatString("ChannelPhotoEditNotification", 2131165464, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              return LocaleController.formatString("NotificationEditedGroupPhoto", 2131165993, new Object[] { str, ((TLRPC.Chat)localObject1).title });
            }
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser))
            {
              if (paramMessageObject.messageOwner.action.user_id == UserConfig.getClientUserId()) {
                return LocaleController.formatString("NotificationGroupKickYou", 2131165998, new Object[] { str, ((TLRPC.Chat)localObject1).title });
              }
              if (paramMessageObject.messageOwner.action.user_id == i) {
                return LocaleController.formatString("NotificationGroupLeftMember", 2131165999, new Object[] { str, ((TLRPC.Chat)localObject1).title });
              }
              paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.action.user_id));
              if (paramMessageObject == null) {
                return null;
              }
              return LocaleController.formatString("NotificationGroupKickMember", 2131165997, new Object[] { str, ((TLRPC.Chat)localObject1).title, UserObject.getUserName(paramMessageObject) });
            }
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatCreate)) {
              return paramMessageObject.messageText.toString();
            }
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChannelCreate)) {
              return paramMessageObject.messageText.toString();
            }
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo)) {
              return LocaleController.formatString("ActionMigrateFromGroupNotify", 2131165227, new Object[] { ((TLRPC.Chat)localObject1).title });
            }
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChannelMigrateFrom)) {
              return LocaleController.formatString("ActionMigrateFromGroupNotify", 2131165227, new Object[] { paramMessageObject.messageOwner.action.title });
            }
            if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))
            {
              if (paramMessageObject.replyMessageObject == null)
              {
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedNoText", 2131165976, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedNoTextChannel", 2131165977, new Object[] { str, ((TLRPC.Chat)localObject1).title });
              }
              localObject2 = paramMessageObject.replyMessageObject;
              if (((MessageObject)localObject2).isMusic())
              {
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedMusic", 2131165974, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedMusicChannel", 2131165975, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if (((MessageObject)localObject2).isVideo())
              {
                if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(((MessageObject)localObject2).messageOwner.media.caption)))
                {
                  paramMessageObject = "📹 " + ((MessageObject)localObject2).messageOwner.media.caption;
                  if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                    return LocaleController.formatString("NotificationActionPinnedText", 2131165984, new Object[] { str, paramMessageObject, ((TLRPC.Chat)localObject1).title });
                  }
                  return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131165985, new Object[] { ((TLRPC.Chat)localObject1).title, paramMessageObject });
                }
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedVideo", 2131165986, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedVideoChannel", 2131165987, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if (((MessageObject)localObject2).isGif())
              {
                if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(((MessageObject)localObject2).messageOwner.media.caption)))
                {
                  paramMessageObject = "🎬 " + ((MessageObject)localObject2).messageOwner.media.caption;
                  if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                    return LocaleController.formatString("NotificationActionPinnedText", 2131165984, new Object[] { str, paramMessageObject, ((TLRPC.Chat)localObject1).title });
                  }
                  return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131165985, new Object[] { ((TLRPC.Chat)localObject1).title, paramMessageObject });
                }
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedGif", 2131165972, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedGifChannel", 2131165973, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if (((MessageObject)localObject2).isVoice())
              {
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedVoice", 2131165988, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedVoiceChannel", 2131165989, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if (((MessageObject)localObject2).isSticker())
              {
                paramMessageObject = paramMessageObject.getStickerEmoji();
                if (paramMessageObject != null)
                {
                  if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                    return LocaleController.formatString("NotificationActionPinnedStickerEmoji", 2131165982, new Object[] { str, ((TLRPC.Chat)localObject1).title, paramMessageObject });
                  }
                  return LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", 2131165983, new Object[] { ((TLRPC.Chat)localObject1).title, paramMessageObject });
                }
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedSticker", 2131165980, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedStickerChannel", 2131165981, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if ((((MessageObject)localObject2).messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
              {
                if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(((MessageObject)localObject2).messageOwner.media.caption)))
                {
                  paramMessageObject = "📎 " + ((MessageObject)localObject2).messageOwner.media.caption;
                  if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                    return LocaleController.formatString("NotificationActionPinnedText", 2131165984, new Object[] { str, paramMessageObject, ((TLRPC.Chat)localObject1).title });
                  }
                  return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131165985, new Object[] { ((TLRPC.Chat)localObject1).title, paramMessageObject });
                }
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedFile", 2131165966, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedFileChannel", 2131165967, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if ((((MessageObject)localObject2).messageOwner.media instanceof TLRPC.TL_messageMediaGeo))
              {
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedGeo", 2131165970, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedGeoChannel", 2131165971, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if ((((MessageObject)localObject2).messageOwner.media instanceof TLRPC.TL_messageMediaContact))
              {
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedContact", 2131165964, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedContactChannel", 2131165965, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if ((((MessageObject)localObject2).messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
              {
                if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(((MessageObject)localObject2).messageOwner.media.caption)))
                {
                  paramMessageObject = "🖼 " + ((MessageObject)localObject2).messageOwner.media.caption;
                  if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                    return LocaleController.formatString("NotificationActionPinnedText", 2131165984, new Object[] { str, paramMessageObject, ((TLRPC.Chat)localObject1).title });
                  }
                  return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131165985, new Object[] { ((TLRPC.Chat)localObject1).title, paramMessageObject });
                }
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedPhoto", 2131165978, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedPhotoChannel", 2131165979, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if ((((MessageObject)localObject2).messageOwner.media instanceof TLRPC.TL_messageMediaGame))
              {
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedGame", 2131165968, new Object[] { str, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedGameChannel", 2131165969, new Object[] { ((TLRPC.Chat)localObject1).title });
              }
              if ((((MessageObject)localObject2).messageText != null) && (((MessageObject)localObject2).messageText.length() > 0))
              {
                localObject2 = ((MessageObject)localObject2).messageText;
                paramMessageObject = (MessageObject)localObject2;
                if (((CharSequence)localObject2).length() > 20) {
                  paramMessageObject = ((CharSequence)localObject2).subSequence(0, 20) + "...";
                }
                if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                  return LocaleController.formatString("NotificationActionPinnedText", 2131165984, new Object[] { str, paramMessageObject, ((TLRPC.Chat)localObject1).title });
                }
                return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131165985, new Object[] { ((TLRPC.Chat)localObject1).title, paramMessageObject });
              }
              if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
                return LocaleController.formatString("NotificationActionPinnedNoText", 2131165976, new Object[] { str, ((TLRPC.Chat)localObject1).title });
              }
              return LocaleController.formatString("NotificationActionPinnedNoTextChannel", 2131165977, new Object[] { ((TLRPC.Chat)localObject1).title });
            }
          } while (!(paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionGameScore));
          return paramMessageObject.messageText.toString();
          if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup)) {
            break label5608;
          }
          if (!paramMessageObject.messageOwner.post) {
            break label4779;
          }
          if (paramMessageObject.isMediaEmpty())
          {
            if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0)) {
              return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, paramMessageObject.messageOwner.message });
            }
            return LocaleController.formatString("ChannelMessageNoText", 2131165455, new Object[] { str, ((TLRPC.Chat)localObject1).title });
          }
          if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
          {
            if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
              return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "🖼 " + paramMessageObject.messageOwner.media.caption });
            }
            return LocaleController.formatString("ChannelMessagePhoto", 2131165456, new Object[] { str, ((TLRPC.Chat)localObject1).title });
          }
          if (paramMessageObject.isVideo())
          {
            if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
              return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "📹 " + paramMessageObject.messageOwner.media.caption });
            }
            return LocaleController.formatString("ChannelMessageVideo", 2131165459, new Object[] { str, ((TLRPC.Chat)localObject1).title });
          }
          if (paramMessageObject.isVoice()) {
            return LocaleController.formatString("ChannelMessageAudio", 2131165438, new Object[] { str, ((TLRPC.Chat)localObject1).title });
          }
          if (paramMessageObject.isMusic()) {
            return LocaleController.formatString("ChannelMessageMusic", 2131165454, new Object[] { str, ((TLRPC.Chat)localObject1).title });
          }
          if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact)) {
            return LocaleController.formatString("ChannelMessageContact", 2131165439, new Object[] { str, ((TLRPC.Chat)localObject1).title });
          }
          if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue))) {
            return LocaleController.formatString("ChannelMessageMap", 2131165453, new Object[] { str, ((TLRPC.Chat)localObject1).title });
          }
        } while (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument));
        if (paramMessageObject.isSticker())
        {
          paramMessageObject = paramMessageObject.getStickerEmoji();
          if (paramMessageObject != null) {
            return LocaleController.formatString("ChannelMessageStickerEmoji", 2131165458, new Object[] { str, ((TLRPC.Chat)localObject1).title, paramMessageObject });
          }
          return LocaleController.formatString("ChannelMessageSticker", 2131165457, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        }
        if (paramMessageObject.isGif())
        {
          if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
            return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "🎬 " + paramMessageObject.messageOwner.media.caption });
          }
          return LocaleController.formatString("ChannelMessageGIF", 2131165441, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        }
        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
          return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "📎 " + paramMessageObject.messageOwner.media.caption });
        }
        return LocaleController.formatString("ChannelMessageDocument", 2131165440, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        if (paramMessageObject.isMediaEmpty())
        {
          if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0)) {
            return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, paramMessageObject.messageOwner.message });
          }
          return LocaleController.formatString("ChannelMessageGroupNoText", 2131165448, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        }
        if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
        {
          if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
            return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "🖼 " + paramMessageObject.messageOwner.media.caption });
          }
          return LocaleController.formatString("ChannelMessageGroupPhoto", 2131165449, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        }
        if (paramMessageObject.isVideo())
        {
          if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
            return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "📹 " + paramMessageObject.messageOwner.media.caption });
          }
          return LocaleController.formatString("ChannelMessageGroupVideo", 2131165452, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        }
        if (paramMessageObject.isVoice()) {
          return LocaleController.formatString("ChannelMessageGroupAudio", 2131165442, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        }
        if (paramMessageObject.isMusic()) {
          return LocaleController.formatString("ChannelMessageGroupMusic", 2131165447, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        }
        if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact)) {
          return LocaleController.formatString("ChannelMessageGroupContact", 2131165443, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        }
        if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue))) {
          return LocaleController.formatString("ChannelMessageGroupMap", 2131165446, new Object[] { str, ((TLRPC.Chat)localObject1).title });
        }
      } while (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument));
      if (paramMessageObject.isSticker())
      {
        paramMessageObject = paramMessageObject.getStickerEmoji();
        if (paramMessageObject != null) {
          return LocaleController.formatString("ChannelMessageGroupStickerEmoji", 2131165451, new Object[] { str, ((TLRPC.Chat)localObject1).title, paramMessageObject });
        }
        return LocaleController.formatString("ChannelMessageGroupSticker", 2131165450, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      }
      if (paramMessageObject.isGif())
      {
        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
          return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "🎬 " + paramMessageObject.messageOwner.media.caption });
        }
        return LocaleController.formatString("ChannelMessageGroupGif", 2131165445, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      }
      if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
        return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "📎 " + paramMessageObject.messageOwner.media.caption });
      }
      return LocaleController.formatString("ChannelMessageGroupDocument", 2131165444, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      if (paramMessageObject.isMediaEmpty())
      {
        if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0)) {
          return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, paramMessageObject.messageOwner.message });
        }
        return LocaleController.formatString("NotificationMessageGroupNoText", 2131166014, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      }
      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
          return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "🖼 " + paramMessageObject.messageOwner.media.caption });
        }
        return LocaleController.formatString("NotificationMessageGroupPhoto", 2131166015, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      }
      if (paramMessageObject.isVideo())
      {
        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
          return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "📹 " + paramMessageObject.messageOwner.media.caption });
        }
        return LocaleController.formatString("NotificationMessageGroupVideo", 2131166019, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      }
      if (paramMessageObject.isVoice()) {
        return LocaleController.formatString("NotificationMessageGroupAudio", 2131166007, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      }
      if (paramMessageObject.isMusic()) {
        return LocaleController.formatString("NotificationMessageGroupMusic", 2131166013, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      }
      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact)) {
        return LocaleController.formatString("NotificationMessageGroupContact", 2131166008, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      }
      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) {
        return LocaleController.formatString("NotificationMessageGroupGame", 2131166010, new Object[] { str, ((TLRPC.Chat)localObject1).title, paramMessageObject.messageOwner.media.game.title });
      }
      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue))) {
        return LocaleController.formatString("NotificationMessageGroupMap", 2131166012, new Object[] { str, ((TLRPC.Chat)localObject1).title });
      }
    } while (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument));
    if (paramMessageObject.isSticker())
    {
      paramMessageObject = paramMessageObject.getStickerEmoji();
      if (paramMessageObject != null) {
        return LocaleController.formatString("NotificationMessageGroupStickerEmoji", 2131166017, new Object[] { str, ((TLRPC.Chat)localObject1).title, paramMessageObject });
      }
      return LocaleController.formatString("NotificationMessageGroupSticker", 2131166016, new Object[] { str, ((TLRPC.Chat)localObject1).title });
    }
    if (paramMessageObject.isGif())
    {
      if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
        return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "🎬 " + paramMessageObject.messageOwner.media.caption });
      }
      return LocaleController.formatString("NotificationMessageGroupGif", 2131166011, new Object[] { str, ((TLRPC.Chat)localObject1).title });
    }
    if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption))) {
      return LocaleController.formatString("NotificationMessageGroupText", 2131166018, new Object[] { str, ((TLRPC.Chat)localObject1).title, "📎 " + paramMessageObject.messageOwner.media.caption });
    }
    return LocaleController.formatString("NotificationMessageGroupDocument", 2131166009, new Object[] { str, ((TLRPC.Chat)localObject1).title });
    label6493:
    if ((ChatObject.isChannel((TLRPC.Chat)localObject1)) && (!((TLRPC.Chat)localObject1).megagroup)) {
      return LocaleController.formatString("ChannelMessageNoText", 2131165455, new Object[] { str, ((TLRPC.Chat)localObject1).title });
    }
    return LocaleController.formatString("NotificationMessageGroupNoText", 2131166014, new Object[] { str, ((TLRPC.Chat)localObject1).title });
  }
  
  private boolean isPersonalMessage(MessageObject paramMessageObject)
  {
    return (paramMessageObject.messageOwner.to_id != null) && (paramMessageObject.messageOwner.to_id.chat_id == 0) && (paramMessageObject.messageOwner.to_id.channel_id == 0) && ((paramMessageObject.messageOwner.action == null) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)));
  }
  
  private void playInChatSound()
  {
    if ((!this.inChatSoundEnabled) || (MediaController.getInstance().isRecordingAudio())) {}
    for (;;)
    {
      return;
      for (;;)
      {
        try
        {
          int i = this.audioManager.getRingerMode();
          if (i == 0) {
            break;
          }
        }
        catch (Exception localException2)
        {
          FileLog.e("tmessages", localException2);
          continue;
        }
        try
        {
          if (getNotifyOverride(ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0), this.opened_dialog_id) == 2) {
            break;
          }
          this.notificationsQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundPlay) <= 500L) {}
              for (;;)
              {
                return;
                try
                {
                  if (NotificationsController.this.soundPool == null)
                  {
                    NotificationsController.access$2302(NotificationsController.this, new SoundPool(3, 1, 0));
                    NotificationsController.this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
                    {
                      public void onLoadComplete(SoundPool paramAnonymous2SoundPool, int paramAnonymous2Int1, int paramAnonymous2Int2)
                      {
                        if (paramAnonymous2Int2 == 0) {
                          paramAnonymous2SoundPool.play(paramAnonymous2Int1, 1.0F, 1.0F, 1, 0, 1.0F);
                        }
                      }
                    });
                  }
                  if ((NotificationsController.this.soundIn == 0) && (!NotificationsController.this.soundInLoaded))
                  {
                    NotificationsController.access$2502(NotificationsController.this, true);
                    NotificationsController.access$2402(NotificationsController.this, NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, 2131099648, 1));
                  }
                  if (NotificationsController.this.soundIn != 0)
                  {
                    NotificationsController.this.soundPool.play(NotificationsController.this.soundIn, 1.0F, 1.0F, 1, 0, 1.0F);
                    return;
                  }
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                }
              }
            }
          });
          return;
        }
        catch (Exception localException1)
        {
          FileLog.e("tmessages", localException1);
          return;
        }
      }
    }
  }
  
  private void scheduleNotificationDelay(boolean paramBoolean)
  {
    try
    {
      FileLog.e("tmessages", "delay notification start, onlineReason = " + paramBoolean);
      this.notificationDelayWakelock.acquire(10000L);
      AndroidUtilities.cancelRunOnUIThread(this.notificationDelayRunnable);
      Runnable localRunnable = this.notificationDelayRunnable;
      if (paramBoolean) {}
      for (int i = 3000;; i = 1000)
      {
        AndroidUtilities.runOnUIThread(localRunnable, i);
        return;
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
      showOrUpdateNotification(this.notifyCheck);
    }
  }
  
  private void scheduleNotificationRepeat()
  {
    try
    {
      PendingIntent localPendingIntent = PendingIntent.getService(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class), 0);
      int i = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("repeat_messages", 60);
      if ((i > 0) && (this.personal_count > 0))
      {
        this.alarmManager.set(2, SystemClock.elapsedRealtime() + i * 60 * 1000, localPendingIntent);
        return;
      }
      this.alarmManager.cancel(localPendingIntent);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  private void setBadge(final int paramInt)
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (NotificationsController.this.lastBadgeCount == paramInt) {}
        for (;;)
        {
          return;
          NotificationsController.access$1102(NotificationsController.this, paramInt);
          try
          {
            ContentValues localContentValues = new ContentValues();
            localContentValues.put("tag", "org.telegram.messenger/org.telegram.ui.LaunchActivity");
            localContentValues.put("count", Integer.valueOf(paramInt));
            ApplicationLoader.applicationContext.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), localContentValues);
            try
            {
              if (NotificationsController.this.launcherClassName == null) {
                NotificationsController.access$2002(NotificationsController.this, NotificationsController.getLauncherClassName(ApplicationLoader.applicationContext));
              }
              if (NotificationsController.this.launcherClassName == null) {
                continue;
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  try
                  {
                    Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
                    localIntent.putExtra("badge_count", NotificationsController.11.this.val$count);
                    localIntent.putExtra("badge_count_package_name", ApplicationLoader.applicationContext.getPackageName());
                    localIntent.putExtra("badge_count_class_name", NotificationsController.this.launcherClassName);
                    ApplicationLoader.applicationContext.sendBroadcast(localIntent);
                    return;
                  }
                  catch (Exception localException)
                  {
                    FileLog.e("tmessages", localException);
                  }
                }
              });
              return;
            }
            catch (Throwable localThrowable1)
            {
              FileLog.e("tmessages", localThrowable1);
              return;
            }
          }
          catch (Throwable localThrowable2)
          {
            for (;;) {}
          }
        }
      }
    });
  }
  
  @SuppressLint({"InlinedApi"})
  private void showExtraNotifications(NotificationCompat.Builder paramBuilder, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT < 18) {}
    for (;;)
    {
      return;
      ArrayList localArrayList1 = new ArrayList();
      HashMap localHashMap1 = new HashMap();
      int i = 0;
      Object localObject2;
      long l;
      Object localObject1;
      if (i < this.pushMessages.size())
      {
        localObject2 = (MessageObject)this.pushMessages.get(i);
        l = ((MessageObject)localObject2).getDialogId();
        if ((int)l == 0) {}
        for (;;)
        {
          i += 1;
          break;
          localObject1 = (ArrayList)localHashMap1.get(Long.valueOf(l));
          paramBuilder = (NotificationCompat.Builder)localObject1;
          if (localObject1 == null)
          {
            paramBuilder = new ArrayList();
            localHashMap1.put(Long.valueOf(l), paramBuilder);
            localArrayList1.add(0, Long.valueOf(l));
          }
          paramBuilder.add(localObject2);
        }
      }
      HashMap localHashMap2 = new HashMap();
      localHashMap2.putAll(this.wearNotificationsIds);
      this.wearNotificationsIds.clear();
      i = 0;
      if (i < localArrayList1.size())
      {
        l = ((Long)localArrayList1.get(i)).longValue();
        ArrayList localArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(l));
        int j = ((MessageObject)localArrayList2.get(0)).getId();
        int k = ((MessageObject)localArrayList2.get(0)).messageOwner.date;
        TLRPC.Chat localChat = null;
        localObject2 = null;
        if (l > 0L)
        {
          paramBuilder = MessagesController.getInstance().getUser(Integer.valueOf((int)l));
          localObject2 = paramBuilder;
          if (paramBuilder != null) {}
        }
        else
        {
          do
          {
            i += 1;
            break;
            localChat = MessagesController.getInstance().getChat(Integer.valueOf(-(int)l));
          } while (localChat == null);
        }
        Integer localInteger = null;
        Object localObject3;
        label361:
        NotificationCompat.CarExtender.UnreadConversation.Builder localBuilder;
        Object localObject4;
        if ((AndroidUtilities.needShowPasscode(false)) || (UserConfig.isWaitingForPasscodeEnter))
        {
          localObject3 = LocaleController.getString("AppName", 2131165299);
          paramBuilder = localInteger;
          localInteger = (Integer)localHashMap2.get(Long.valueOf(l));
          if (localInteger != null) {
            break label1061;
          }
          localInteger = Integer.valueOf((int)l);
          localBuilder = new NotificationCompat.CarExtender.UnreadConversation.Builder((String)localObject3).setLatestTimestamp(k * 1000L);
          localObject1 = new Intent();
          ((Intent)localObject1).addFlags(32);
          ((Intent)localObject1).setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
          ((Intent)localObject1).putExtra("dialog_id", l);
          ((Intent)localObject1).putExtra("max_id", j);
          localBuilder.setReadPendingIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, localInteger.intValue(), (Intent)localObject1, 134217728));
          localObject1 = null;
          localObject4 = localObject1;
          if (!ChatObject.isChannel(localChat))
          {
            localObject4 = localObject1;
            if (!AndroidUtilities.needShowPasscode(false))
            {
              localObject4 = localObject1;
              if (!UserConfig.isWaitingForPasscodeEnter)
              {
                localObject1 = new Intent();
                ((Intent)localObject1).addFlags(32);
                ((Intent)localObject1).setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
                ((Intent)localObject1).putExtra("dialog_id", l);
                ((Intent)localObject1).putExtra("max_id", j);
                localBuilder.setReplyAction(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, localInteger.intValue(), (Intent)localObject1, 134217728), new RemoteInput.Builder("extra_voice_reply").setLabel(LocaleController.getString("Reply", 2131166156)).build());
                localObject1 = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
                ((Intent)localObject1).putExtra("dialog_id", l);
                ((Intent)localObject1).putExtra("max_id", j);
                localObject4 = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, localInteger.intValue(), (Intent)localObject1, 134217728);
                localObject5 = new RemoteInput.Builder("extra_voice_reply").setLabel(LocaleController.getString("Reply", 2131166156)).build();
                if (localChat == null) {
                  break label1075;
                }
              }
            }
          }
        }
        NotificationCompat.MessagingStyle localMessagingStyle;
        MessageObject localMessageObject;
        label968:
        label1061:
        label1075:
        for (localObject1 = LocaleController.formatString("ReplyToGroup", 2131166157, new Object[] { localObject3 });; localObject1 = LocaleController.formatString("ReplyToUser", 2131166158, new Object[] { localObject3 }))
        {
          localObject4 = new NotificationCompat.Action.Builder(2130837750, (CharSequence)localObject1, (PendingIntent)localObject4).addRemoteInput((RemoteInput)localObject5).build();
          localObject5 = (Integer)this.pushDialogs.get(Long.valueOf(l));
          localObject1 = localObject5;
          if (localObject5 == null) {
            localObject1 = Integer.valueOf(0);
          }
          localMessagingStyle = new NotificationCompat.MessagingStyle(null).setConversationTitle(String.format("%1$s (%2$s)", new Object[] { localObject3, LocaleController.formatPluralString("NewMessages", Math.max(((Integer)localObject1).intValue(), localArrayList2.size())) }));
          localObject1 = "";
          j = localArrayList2.size() - 1;
          for (;;)
          {
            if (j < 0) {
              break label1285;
            }
            localMessageObject = (MessageObject)localArrayList2.get(j);
            localObject5 = getStringForMessage(localMessageObject, false);
            if (localObject5 != null) {
              break;
            }
            j -= 1;
          }
          if (localChat != null) {}
          for (localObject1 = localChat.title;; localObject1 = UserObject.getUserName((TLRPC.User)localObject2))
          {
            if (localChat == null) {
              break label968;
            }
            localObject3 = localObject1;
            paramBuilder = localInteger;
            if (localChat.photo == null) {
              break;
            }
            localObject3 = localObject1;
            paramBuilder = localInteger;
            if (localChat.photo.photo_small == null) {
              break;
            }
            localObject3 = localObject1;
            paramBuilder = localInteger;
            if (localChat.photo.photo_small.volume_id == 0L) {
              break;
            }
            localObject3 = localObject1;
            paramBuilder = localInteger;
            if (localChat.photo.photo_small.local_id == 0) {
              break;
            }
            paramBuilder = localChat.photo.photo_small;
            localObject3 = localObject1;
            break;
          }
          localObject3 = localObject1;
          paramBuilder = localInteger;
          if (((TLRPC.User)localObject2).photo == null) {
            break;
          }
          localObject3 = localObject1;
          paramBuilder = localInteger;
          if (((TLRPC.User)localObject2).photo.photo_small == null) {
            break;
          }
          localObject3 = localObject1;
          paramBuilder = localInteger;
          if (((TLRPC.User)localObject2).photo.photo_small.volume_id == 0L) {
            break;
          }
          localObject3 = localObject1;
          paramBuilder = localInteger;
          if (((TLRPC.User)localObject2).photo.photo_small.local_id == 0) {
            break;
          }
          paramBuilder = ((TLRPC.User)localObject2).photo.photo_small;
          localObject3 = localObject1;
          break;
          localHashMap2.remove(Long.valueOf(l));
          break label361;
        }
        if (localChat != null) {}
        Object localObject6;
        for (Object localObject5 = ((String)localObject5).replace(" @ " + (String)localObject3, "");; localObject5 = ((String)localObject5).replace((String)localObject3 + ": ", "").replace((String)localObject3 + " ", ""))
        {
          localObject6 = localObject1;
          if (((String)localObject1).length() > 0) {
            localObject6 = (String)localObject1 + "\n\n";
          }
          localObject1 = (String)localObject6 + (String)localObject5;
          localBuilder.addMessage((String)localObject5);
          localMessagingStyle.addMessage((CharSequence)localObject5, localMessageObject.messageOwner.date * 1000L, null);
          break;
        }
        label1285:
        localObject5 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        ((Intent)localObject5).setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        ((Intent)localObject5).setFlags(32768);
        if (localChat != null) {
          ((Intent)localObject5).putExtra("chatId", localChat.id);
        }
        for (;;)
        {
          localObject5 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject5, 1073741824);
          localObject6 = new NotificationCompat.WearableExtender();
          if (localObject4 != null) {
            ((NotificationCompat.WearableExtender)localObject6).addAction((NotificationCompat.Action)localObject4);
          }
          localObject1 = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle((CharSequence)localObject3).setSmallIcon(2130837859).setGroup("messages").setContentText((CharSequence)localObject1).setAutoCancel(true).setNumber(localArrayList2.size()).setColor(-13851168).setGroupSummary(false).setWhen(((MessageObject)localArrayList2.get(0)).messageOwner.date * 1000L).setStyle(localMessagingStyle).setContentIntent((PendingIntent)localObject5).extend((NotificationCompat.Extender)localObject6).extend(new NotificationCompat.CarExtender().setUnreadConversation(localBuilder.build())).setCategory("msg");
          if (paramBuilder != null)
          {
            localObject3 = ImageLoader.getInstance().getImageFromMemory(paramBuilder, null, "50_50");
            if (localObject3 == null) {
              break label1665;
            }
            ((NotificationCompat.Builder)localObject1).setLargeIcon(((BitmapDrawable)localObject3).getBitmap());
          }
          if ((localChat == null) && (localObject2 != null) && (((TLRPC.User)localObject2).phone != null) && (((TLRPC.User)localObject2).phone.length() > 0)) {
            ((NotificationCompat.Builder)localObject1).addPerson("tel:+" + ((TLRPC.User)localObject2).phone);
          }
          this.notificationManager.notify(localInteger.intValue(), ((NotificationCompat.Builder)localObject1).build());
          this.wearNotificationsIds.put(Long.valueOf(l), localInteger);
          break;
          if (localObject2 != null) {
            ((Intent)localObject5).putExtra("userId", ((TLRPC.User)localObject2).id);
          }
        }
        for (;;)
        {
          label1665:
          float f;
          try
          {
            f = 160.0F / AndroidUtilities.dp(50.0F);
            localObject3 = new BitmapFactory.Options();
            if (f >= 1.0F) {
              break label1734;
            }
            j = 1;
            ((BitmapFactory.Options)localObject3).inSampleSize = j;
            paramBuilder = BitmapFactory.decodeFile(FileLoader.getPathToAttach(paramBuilder, true).toString(), (BitmapFactory.Options)localObject3);
            if (paramBuilder == null) {
              break;
            }
            ((NotificationCompat.Builder)localObject1).setLargeIcon(paramBuilder);
          }
          catch (Throwable paramBuilder) {}
          break;
          label1734:
          j = (int)f;
        }
      }
      paramBuilder = localHashMap2.entrySet().iterator();
      while (paramBuilder.hasNext())
      {
        localObject1 = (Map.Entry)paramBuilder.next();
        this.notificationManager.cancel(((Integer)((Map.Entry)localObject1).getValue()).intValue());
      }
    }
  }
  
  private void showOrUpdateNotification(boolean paramBoolean)
  {
    if ((!UserConfig.isClientActivated()) || (this.pushMessages.isEmpty()))
    {
      dismissNotification();
      return;
    }
    MessageObject localMessageObject1;
    Object localObject8;
    int i7;
    try
    {
      ConnectionsManager.getInstance().resumeNetworkMaybe();
      localMessageObject1 = (MessageObject)this.pushMessages.get(0);
      localObject8 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
      i7 = ((SharedPreferences)localObject8).getInt("dismissDate", 0);
      if (localMessageObject1.messageOwner.date <= i7)
      {
        dismissNotification();
        return;
      }
    }
    catch (Exception localException1)
    {
      FileLog.e("tmessages", localException1);
      return;
    }
    long l2 = localMessageObject1.getDialogId();
    long l1 = l2;
    if (localMessageObject1.messageOwner.mentioned) {
      l1 = localMessageObject1.messageOwner.from_id;
    }
    localMessageObject1.getId();
    int i3;
    int i;
    int m;
    label184:
    Object localObject6;
    TLRPC.Chat localChat;
    Object localObject5;
    int i1;
    int k;
    int i6;
    Object localObject7;
    int j;
    boolean bool1;
    int i5;
    int n;
    int i2;
    label309:
    int i4;
    Object localObject1;
    label465:
    String str2;
    boolean bool2;
    if (localMessageObject1.messageOwner.to_id.chat_id != 0)
    {
      i3 = localMessageObject1.messageOwner.to_id.chat_id;
      i = localMessageObject1.messageOwner.to_id.user_id;
      if (i != 0) {
        break label1779;
      }
      m = localMessageObject1.messageOwner.from_id;
      localObject6 = MessagesController.getInstance().getUser(Integer.valueOf(m));
      localChat = null;
      if (i3 != 0) {
        localChat = MessagesController.getInstance().getChat(Integer.valueOf(i3));
      }
      localObject5 = null;
      i1 = 0;
      k = 0;
      i6 = 0;
      localObject7 = null;
      j = -16711936;
      bool1 = false;
      i5 = 0;
      n = 0;
      i2 = getNotifyOverride((SharedPreferences)localObject8, l1);
      if ((!paramBoolean) || (i2 == 2)) {
        break label2981;
      }
      if (!((SharedPreferences)localObject8).getBoolean("EnableAll", true)) {
        break label2973;
      }
      i = i1;
      if (i3 != 0)
      {
        i = i1;
        if (!((SharedPreferences)localObject8).getBoolean("EnableGroup", true)) {
          break label2973;
        }
      }
      i2 = i;
      if (i == 0)
      {
        i2 = i;
        if (l2 == l1)
        {
          i2 = i;
          if (localChat != null)
          {
            i1 = ((SharedPreferences)localObject8).getInt("smart_max_count_" + l2, 2);
            i4 = ((SharedPreferences)localObject8).getInt("smart_delay_" + l2, 180);
            i2 = i;
            if (i1 != 0)
            {
              localObject1 = (Point)this.smartNotificationsDialogs.get(Long.valueOf(l2));
              if (localObject1 != null) {
                break label1802;
              }
              localObject1 = new Point(1, (int)(System.currentTimeMillis() / 1000L));
              this.smartNotificationsDialogs.put(Long.valueOf(l2), localObject1);
              i2 = i;
            }
          }
        }
      }
      str2 = Settings.System.DEFAULT_NOTIFICATION_URI.getPath();
      i4 = j;
      if (i2 == 0)
      {
        bool1 = ((SharedPreferences)localObject8).getBoolean("EnableInAppSounds", true);
        boolean bool3 = ((SharedPreferences)localObject8).getBoolean("EnableInAppVibrate", true);
        bool2 = ((SharedPreferences)localObject8).getBoolean("EnableInAppPreview", true);
        boolean bool4 = ((SharedPreferences)localObject8).getBoolean("EnableInAppPriority", false);
        i1 = ((SharedPreferences)localObject8).getInt("vibrate_" + l2, 0);
        i5 = ((SharedPreferences)localObject8).getInt("priority_" + l2, 3);
        i4 = 0;
        localObject4 = ((SharedPreferences)localObject8).getString("sound_path_" + l2, null);
        if (i3 == 0) {
          break label1905;
        }
        if ((localObject4 == null) || (!((String)localObject4).equals(str2))) {
          break label1879;
        }
        localObject1 = null;
        label653:
        i = ((SharedPreferences)localObject8).getInt("vibrate_group", 0);
        k = ((SharedPreferences)localObject8).getInt("priority_group", 1);
        j = ((SharedPreferences)localObject8).getInt("GroupLed", -16711936);
        label693:
        n = j;
        if (!((SharedPreferences)localObject8).contains("color_" + l2)) {
          break label2986;
        }
        n = ((SharedPreferences)localObject8).getInt("color_" + l2, 0);
        break label2986;
        label762:
        boolean bool5 = ApplicationLoader.mainInterfacePaused;
        localObject4 = localObject1;
        i1 = j;
        i = k;
        if (!bool5)
        {
          if (!bool1) {
            localObject1 = null;
          }
          if (!bool3) {
            j = 2;
          }
          if (bool4) {
            break label3086;
          }
          i = 0;
          i1 = j;
          localObject4 = localObject1;
        }
        label814:
        localObject7 = localObject4;
        bool1 = bool2;
        i4 = n;
        k = i1;
        i5 = i;
        if (i6 != 0)
        {
          localObject7 = localObject4;
          bool1 = bool2;
          i4 = n;
          k = i1;
          i5 = i;
          if (i1 == 2) {}
        }
      }
    }
    try
    {
      j = this.audioManager.getRingerMode();
      localObject7 = localObject4;
      bool1 = bool2;
      i4 = n;
      k = i1;
      i5 = i;
      if (j != 0)
      {
        localObject7 = localObject4;
        bool1 = bool2;
        i4 = n;
        k = i1;
        i5 = i;
        if (j != 1)
        {
          k = 2;
          i5 = i;
          i4 = n;
          bool1 = bool2;
          localObject7 = localObject4;
        }
      }
    }
    catch (Exception localException2)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException2);
        localObject7 = localObject4;
        bool1 = bool2;
        i4 = n;
        k = i1;
        i5 = i;
        continue;
        if (m != 0)
        {
          ((Intent)localObject4).putExtra("userId", m);
          continue;
          localObject2 = localObject5;
          if (this.pushDialogs.size() == 1) {
            if (localChat != null)
            {
              localObject2 = localObject5;
              if (localChat.photo != null)
              {
                localObject2 = localObject5;
                if (localChat.photo.photo_small != null)
                {
                  localObject2 = localObject5;
                  if (localChat.photo.photo_small.volume_id != 0L)
                  {
                    localObject2 = localObject5;
                    if (localChat.photo.photo_small.local_id != 0) {
                      localObject2 = localChat.photo.photo_small;
                    }
                  }
                }
              }
            }
            else
            {
              localObject2 = localObject5;
              if (localObject6 != null)
              {
                localObject2 = localObject5;
                if (((TLRPC.User)localObject6).photo != null)
                {
                  localObject2 = localObject5;
                  if (((TLRPC.User)localObject6).photo.photo_small != null)
                  {
                    localObject2 = localObject5;
                    if (((TLRPC.User)localObject6).photo.photo_small.volume_id != 0L)
                    {
                      localObject2 = localObject5;
                      if (((TLRPC.User)localObject6).photo.photo_small.local_id != 0)
                      {
                        localObject2 = ((TLRPC.User)localObject6).photo.photo_small;
                        continue;
                        localObject2 = localObject5;
                        if (this.pushDialogs.size() == 1)
                        {
                          ((Intent)localObject4).putExtra("encId", (int)(l2 >> 32));
                          localObject2 = localObject5;
                          continue;
                          if (localChat != null)
                          {
                            localObject5 = localChat.title;
                          }
                          else
                          {
                            localObject5 = UserObject.getUserName((TLRPC.User)localObject6);
                            continue;
                            localObject8 = LocaleController.formatString("NotificationMessagesPeopleDisplayOrder", 2131166028, new Object[] { LocaleController.formatPluralString("NewMessages", this.total_unread_count), LocaleController.formatPluralString("FromChats", this.pushDialogs.size()) });
                            continue;
                            localObject4 = ((String)localObject8).replace((String)localObject5 + ": ", "").replace((String)localObject5 + " ", "");
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
      localBuilder.setContentText((CharSequence)localObject8);
      localInboxStyle = new NotificationCompat.InboxStyle();
      localInboxStyle.setBigContentTitle((CharSequence)localObject5);
      i1 = Math.min(10, this.pushMessages.size());
      n = 0;
    }
    Object localObject4 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
    ((Intent)localObject4).setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
    ((Intent)localObject4).setFlags(32768);
    label1046:
    NotificationCompat.Builder localBuilder;
    if ((int)l2 != 0) {
      if (this.pushDialogs.size() == 1)
      {
        if (i3 != 0) {
          ((Intent)localObject4).putExtra("chatId", i3);
        }
      }
      else
      {
        if (AndroidUtilities.needShowPasscode(false)) {
          break label3062;
        }
        if (!UserConfig.isWaitingForPasscodeEnter) {
          break label2058;
        }
        break label3062;
        localObject4 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject4, 1073741824);
        m = 1;
        if (((int)l2 != 0) && (this.pushDialogs.size() <= 1) && (!AndroidUtilities.needShowPasscode(false)) && (!UserConfig.isWaitingForPasscodeEnter)) {
          break label2280;
        }
        localObject5 = LocaleController.getString("AppName", 2131165299);
        m = 0;
        if (this.pushDialogs.size() != 1) {
          break label2305;
        }
        localObject8 = LocaleController.formatPluralString("NewMessages", this.total_unread_count);
        localBuilder = new NotificationCompat.Builder(ApplicationLoader.applicationContext).setContentTitle((CharSequence)localObject5).setSmallIcon(2130837859).setAutoCancel(true).setNumber(this.total_unread_count).setContentIntent((PendingIntent)localObject4).setGroup("messages").setGroupSummary(true).setColor(-13851168);
        localBuilder.setCategory("msg");
        if ((localChat == null) && (localObject6 != null) && (((TLRPC.User)localObject6).phone != null) && (((TLRPC.User)localObject6).phone.length() > 0)) {
          localBuilder.addPerson("tel:+" + ((TLRPC.User)localObject6).phone);
        }
        i = 2;
        localObject4 = null;
        if (this.pushMessages.size() != 1) {
          break label2413;
        }
        localObject4 = (MessageObject)this.pushMessages.get(0);
        localObject6 = getStringForMessage((MessageObject)localObject4, false);
        localObject8 = localObject6;
        if (!((MessageObject)localObject4).messageOwner.silent) {
          break label3116;
        }
        i = 1;
        label1308:
        if (localObject8 == null) {
          break label3119;
        }
        localObject4 = localObject8;
        if (m != 0)
        {
          if (localChat == null) {
            break label2352;
          }
          localObject4 = ((String)localObject8).replace(" @ " + (String)localObject5, "");
        }
        localBuilder.setContentText((CharSequence)localObject4);
        localBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText((CharSequence)localObject4));
        localObject4 = localObject6;
        label1388:
        localObject5 = new Intent(ApplicationLoader.applicationContext, NotificationDismissReceiver.class);
        ((Intent)localObject5).putExtra("messageDate", localMessageObject1.messageOwner.date);
        localBuilder.setDeleteIntent(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 1, (Intent)localObject5, 134217728));
        if (localObject1 == null) {
          break label3068;
        }
        localObject5 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject1, null, "50_50");
        if (localObject5 == null) {
          break label2701;
        }
        localBuilder.setLargeIcon(((BitmapDrawable)localObject5).getBitmap());
        break label3068;
        label1476:
        localBuilder.setPriority(-1);
        label1483:
        if ((i == 1) || (i2 != 0)) {
          break label2920;
        }
        if ((ApplicationLoader.mainInterfacePaused) || (bool1))
        {
          localObject1 = localObject4;
          if (((String)localObject4).length() > 100) {
            localObject1 = ((String)localObject4).substring(0, 100).replace('\n', ' ').trim() + "...";
          }
          localBuilder.setTicker((CharSequence)localObject1);
        }
        if ((!MediaController.getInstance().isRecordingAudio()) && (localObject7 != null) && (!((String)localObject7).equals("NoSound")))
        {
          if (!((String)localObject7).equals(str2)) {
            break label2829;
          }
          localBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, 5);
        }
        label1610:
        if (i4 != 0) {
          localBuilder.setLights(i4, 1000, 1000);
        }
        if ((k != 2) && (!MediaController.getInstance().isRecordingAudio())) {
          break label2844;
        }
        label1660:
        localBuilder.setVibrate(new long[] { 0L, 0L });
        break label2892;
      }
    }
    for (;;)
    {
      label1661:
      if ((Build.VERSION.SDK_INT < 24) && (UserConfig.passcodeHash.length() == 0) && (hasMessagesToReply()))
      {
        localObject1 = new Intent(ApplicationLoader.applicationContext, PopupReplyReceiver.class);
        if (Build.VERSION.SDK_INT > 19) {
          break label2940;
        }
        localBuilder.addAction(2130837710, LocaleController.getString("Reply", 2131166156), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, (Intent)localObject1, 134217728));
      }
      label1738:
      showExtraNotifications(localBuilder, paramBoolean);
      this.notificationManager.notify(1, localBuilder.build());
      scheduleNotificationRepeat();
      return;
      i3 = localMessageObject1.messageOwner.to_id.channel_id;
      break;
      label1779:
      m = i;
      if (i != UserConfig.getClientUserId()) {
        break label184;
      }
      m = localMessageObject1.messageOwner.from_id;
      break label184;
      label1802:
      if (((Point)localObject1).y + i4 < System.currentTimeMillis() / 1000L)
      {
        ((Point)localObject1).set(1, (int)(System.currentTimeMillis() / 1000L));
        i2 = i;
        break label465;
      }
      i2 = ((Point)localObject1).x;
      if (i2 >= i1) {
        break label3080;
      }
      ((Point)localObject1).set(i2 + 1, (int)(System.currentTimeMillis() / 1000L));
      i2 = i;
      break label465;
      label1879:
      localObject1 = localObject4;
      if (localObject4 != null) {
        break label653;
      }
      localObject1 = ((SharedPreferences)localObject8).getString("GroupSoundPath", str2);
      break label653;
      label1905:
      localObject1 = localObject4;
      i = i6;
      k = n;
      if (m == 0) {
        break label693;
      }
      if ((localObject4 != null) && (((String)localObject4).equals(str2))) {
        localObject1 = null;
      }
      for (;;)
      {
        i = ((SharedPreferences)localObject8).getInt("vibrate_messages", 0);
        k = ((SharedPreferences)localObject8).getInt("priority_group", 1);
        j = ((SharedPreferences)localObject8).getInt("MessagesLed", -16711936);
        break;
        localObject1 = localObject4;
        if (localObject4 == null) {
          localObject1 = ((SharedPreferences)localObject8).getString("GlobalSoundPath", str2);
        }
      }
      label2058:
      Object localObject2;
      label2280:
      label2305:
      label2352:
      label2413:
      NotificationCompat.InboxStyle localInboxStyle;
      label2455:
      if (n < i1)
      {
        MessageObject localMessageObject2 = (MessageObject)this.pushMessages.get(n);
        String str1 = getStringForMessage(localMessageObject2, false);
        localObject6 = localObject4;
        j = i;
        if (str1 == null) {
          break label3121;
        }
        if (localMessageObject2.messageOwner.date <= i7)
        {
          localObject6 = localObject4;
          j = i;
          break label3121;
        }
        j = i;
        if (i == 2)
        {
          localObject4 = str1;
          if (!localMessageObject2.messageOwner.silent) {
            break label3137;
          }
          j = 1;
        }
        label2546:
        localObject6 = str1;
        if (this.pushDialogs.size() == 1)
        {
          localObject6 = str1;
          if (m != 0) {
            if (localChat == null) {
              break label2621;
            }
          }
        }
        label2621:
        for (localObject6 = str1.replace(" @ " + (String)localObject5, "");; localObject6 = str1.replace((String)localObject5 + ": ", "").replace((String)localObject5 + " ", ""))
        {
          localInboxStyle.addLine((CharSequence)localObject6);
          localObject6 = localObject4;
          break;
        }
      }
      localInboxStyle.setSummaryText((CharSequence)localObject8);
      localBuilder.setStyle(localInboxStyle);
      break label1388;
      for (;;)
      {
        label2701:
        float f;
        try
        {
          f = 160.0F / AndroidUtilities.dp(50.0F);
          localObject5 = new BitmapFactory.Options();
          if (f < 1.0F)
          {
            j = 1;
            ((BitmapFactory.Options)localObject5).inSampleSize = j;
            localObject2 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject2, true).toString(), (BitmapFactory.Options)localObject5);
            if (localObject2 == null) {
              break;
            }
            localBuilder.setLargeIcon((Bitmap)localObject2);
          }
        }
        catch (Throwable localThrowable) {}
        j = (int)f;
      }
      label2782:
      if (i5 == 0)
      {
        localBuilder.setPriority(0);
        break label1483;
      }
      if (i5 == 1)
      {
        localBuilder.setPriority(1);
        break label1483;
      }
      if (i5 != 2) {
        break label1483;
      }
      localBuilder.setPriority(2);
      break label1483;
      label2829:
      localBuilder.setSound(Uri.parse((String)localObject7), 5);
      break label1610;
      label2844:
      if (k != 1) {
        break label3143;
      }
      localBuilder.setVibrate(new long[] { 0L, 100L, 0L, 100L });
    }
    for (;;)
    {
      localBuilder.setDefaults(2);
      break label1661;
      label2892:
      if (k != 3) {
        break label1661;
      }
      localBuilder.setVibrate(new long[] { 0L, 1000L });
      break label1661;
      label2920:
      localBuilder.setVibrate(new long[] { 0L, 0L });
      break label1661;
      label2940:
      localBuilder.addAction(2130837709, LocaleController.getString("Reply", 2131166156), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, localThrowable, 134217728));
      break label1738;
      label2973:
      i = i1;
      if (i2 != 0) {
        break label309;
      }
      label2981:
      i = 1;
      break label309;
      label2986:
      if (i5 != 3) {
        k = i5;
      }
      j = i;
      i6 = i4;
      if (i == 4)
      {
        i6 = 1;
        j = 0;
      }
      if (((j != 2) || ((i1 != 1) && (i1 != 3) && (i1 != 5))) && ((j == 2) || (i1 != 2)) && (i1 == 0)) {
        break label762;
      }
      j = i1;
      break label762;
      label3062:
      Object localObject3 = null;
      break label1046;
      label3068:
      if (!paramBoolean) {
        break label1476;
      }
      if (i != 1) {
        break label2782;
      }
      break label1476;
      label3080:
      i2 = 1;
      break label465;
      label3086:
      localObject4 = localObject3;
      i1 = j;
      i = k;
      if (k != 2) {
        break label814;
      }
      i = 1;
      localObject4 = localObject3;
      i1 = j;
      break label814;
      label3116:
      i = 0;
      break label1308;
      label3119:
      break;
      label3121:
      n += 1;
      localObject4 = localObject6;
      i = j;
      break label2455;
      label3137:
      j = 0;
      break label2546;
      label3143:
      if (k != 0) {
        if (k != 4) {
          break label1660;
        }
      }
    }
  }
  
  public static void updateServerNotificationsSettings(long paramLong)
  {
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
    if ((int)paramLong == 0) {
      return;
    }
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    TLRPC.TL_account_updateNotifySettings localTL_account_updateNotifySettings = new TLRPC.TL_account_updateNotifySettings();
    localTL_account_updateNotifySettings.settings = new TLRPC.TL_inputPeerNotifySettings();
    localTL_account_updateNotifySettings.settings.sound = "default";
    int i = localSharedPreferences.getInt("notify2_" + paramLong, 0);
    if (i == 3)
    {
      localTL_account_updateNotifySettings.settings.mute_until = localSharedPreferences.getInt("notifyuntil_" + paramLong, 0);
      localTL_account_updateNotifySettings.settings.show_previews = localSharedPreferences.getBoolean("preview_" + paramLong, true);
      localTL_account_updateNotifySettings.settings.silent = localSharedPreferences.getBoolean("silent_" + paramLong, false);
      localTL_account_updateNotifySettings.peer = new TLRPC.TL_inputNotifyPeer();
      ((TLRPC.TL_inputNotifyPeer)localTL_account_updateNotifySettings.peer).peer = MessagesController.getInputPeer((int)paramLong);
      ConnectionsManager.getInstance().sendRequest(localTL_account_updateNotifySettings, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
      return;
    }
    TLRPC.TL_inputPeerNotifySettings localTL_inputPeerNotifySettings = localTL_account_updateNotifySettings.settings;
    if (i != 2) {}
    for (i = 0;; i = Integer.MAX_VALUE)
    {
      localTL_inputPeerNotifySettings.mute_until = i;
      break;
    }
  }
  
  public void cleanup()
  {
    this.popupMessages.clear();
    this.popupReplyMessages.clear();
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.access$302(NotificationsController.this, 0L);
        NotificationsController.access$402(NotificationsController.this, 0);
        NotificationsController.access$502(NotificationsController.this, 0);
        NotificationsController.this.pushMessages.clear();
        NotificationsController.this.pushMessagesDict.clear();
        NotificationsController.this.pushDialogs.clear();
        NotificationsController.this.wearNotificationsIds.clear();
        NotificationsController.this.delayedPushMessages.clear();
        NotificationsController.access$1002(NotificationsController.this, false);
        NotificationsController.access$1102(NotificationsController.this, 0);
        try
        {
          if (NotificationsController.this.notificationDelayWakelock.isHeld()) {
            NotificationsController.this.notificationDelayWakelock.release();
          }
          NotificationsController.this.setBadge(0);
          SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          localEditor.clear();
          localEditor.commit();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException);
          }
        }
      }
    });
  }
  
  protected void forceShowPopupForReply()
  {
    this.notificationsQueue.postRunnable(new Runnable()
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
            i += 1;
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
  
  public boolean hasMessagesToReply()
  {
    int i = 0;
    while (i < this.pushMessages.size())
    {
      MessageObject localMessageObject = (MessageObject)this.pushMessages.get(i);
      long l = localMessageObject.getDialogId();
      if (((localMessageObject.messageOwner.mentioned) && ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))) || ((int)l == 0) || ((localMessageObject.messageOwner.to_id.channel_id != 0) && (!localMessageObject.isMegagroup()))) {
        i += 1;
      } else {
        return true;
      }
    }
    return false;
  }
  
  public void playOutChatSound()
  {
    if ((!this.inChatSoundEnabled) || (MediaController.getInstance().isRecordingAudio())) {}
    for (;;)
    {
      return;
      try
      {
        int i = this.audioManager.getRingerMode();
        if (i == 0) {}
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
      }
    }
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundOutPlay) <= 100L) {
            return;
          }
          NotificationsController.access$2802(NotificationsController.this, System.currentTimeMillis());
          if (NotificationsController.this.soundPool == null)
          {
            NotificationsController.access$2302(NotificationsController.this, new SoundPool(3, 1, 0));
            NotificationsController.this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
            {
              public void onLoadComplete(SoundPool paramAnonymous2SoundPool, int paramAnonymous2Int1, int paramAnonymous2Int2)
              {
                if (paramAnonymous2Int2 == 0) {
                  paramAnonymous2SoundPool.play(paramAnonymous2Int1, 1.0F, 1.0F, 1, 0, 1.0F);
                }
              }
            });
          }
          if ((NotificationsController.this.soundOut == 0) && (!NotificationsController.this.soundOutLoaded))
          {
            NotificationsController.access$3002(NotificationsController.this, true);
            NotificationsController.access$2902(NotificationsController.this, NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, 2131099649, 1));
          }
          if (NotificationsController.this.soundOut != 0)
          {
            NotificationsController.this.soundPool.play(NotificationsController.this.soundOut, 1.0F, 1.0F, 1, 0, 1.0F);
            return;
          }
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void processDialogsUpdateRead(final HashMap<Long, Integer> paramHashMap)
  {
    if (this.popupMessages.isEmpty()) {}
    for (final ArrayList localArrayList = null;; localArrayList = new ArrayList(this.popupMessages))
    {
      this.notificationsQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int k = NotificationsController.this.total_unread_count;
          SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          Iterator localIterator = paramHashMap.entrySet().iterator();
          while (localIterator.hasNext())
          {
            localObject = (Map.Entry)localIterator.next();
            long l3 = ((Long)((Map.Entry)localObject).getKey()).longValue();
            int j = NotificationsController.this.getNotifyOverride(localSharedPreferences, l3);
            int i = j;
            Integer localInteger1;
            if (NotificationsController.this.notifyCheck)
            {
              localInteger1 = (Integer)NotificationsController.this.pushDialogsOverrideMention.get(Long.valueOf(l3));
              i = j;
              if (localInteger1 != null)
              {
                i = j;
                if (localInteger1.intValue() == 1)
                {
                  NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l3), Integer.valueOf(0));
                  i = 1;
                }
              }
            }
            if ((i != 2) && (((localSharedPreferences.getBoolean("EnableAll", true)) && (((int)l3 >= 0) || (localSharedPreferences.getBoolean("EnableGroup", true)))) || (i != 0))) {}
            for (i = 1;; i = 0)
            {
              Integer localInteger2 = (Integer)NotificationsController.this.pushDialogs.get(Long.valueOf(l3));
              localInteger1 = (Integer)((Map.Entry)localObject).getValue();
              if (localInteger1.intValue() == 0) {
                NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(l3));
              }
              localObject = localInteger1;
              if (localInteger1.intValue() < 0)
              {
                if (localInteger2 == null) {
                  break;
                }
                localObject = Integer.valueOf(localInteger2.intValue() + localInteger1.intValue());
              }
              if (((i != 0) || (((Integer)localObject).intValue() == 0)) && (localInteger2 != null)) {
                NotificationsController.access$402(NotificationsController.this, NotificationsController.this.total_unread_count - localInteger2.intValue());
              }
              if (((Integer)localObject).intValue() != 0) {
                break label596;
              }
              NotificationsController.this.pushDialogs.remove(Long.valueOf(l3));
              NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(l3));
              for (i = 0; i < NotificationsController.this.pushMessages.size(); i = j + 1)
              {
                localObject = (MessageObject)NotificationsController.this.pushMessages.get(i);
                j = i;
                if (((MessageObject)localObject).getDialogId() == l3)
                {
                  if (NotificationsController.this.isPersonalMessage((MessageObject)localObject)) {
                    NotificationsController.access$510(NotificationsController.this);
                  }
                  NotificationsController.this.pushMessages.remove(i);
                  i -= 1;
                  NotificationsController.this.delayedPushMessages.remove(localObject);
                  long l2 = ((MessageObject)localObject).messageOwner.id;
                  long l1 = l2;
                  if (((MessageObject)localObject).messageOwner.to_id.channel_id != 0) {
                    l1 = l2 | ((MessageObject)localObject).messageOwner.to_id.channel_id << 32;
                  }
                  NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l1));
                  j = i;
                  if (localArrayList != null)
                  {
                    localArrayList.remove(localObject);
                    j = i;
                  }
                }
              }
            }
            if ((localArrayList != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!localArrayList.isEmpty()))
            {
              localArrayList.clear();
              continue;
              label596:
              if (i != 0)
              {
                NotificationsController.access$402(NotificationsController.this, NotificationsController.this.total_unread_count + ((Integer)localObject).intValue());
                NotificationsController.this.pushDialogs.put(Long.valueOf(l3), localObject);
              }
            }
          }
          if (localArrayList != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationsController.this.popupMessages = NotificationsController.9.this.val$popupArray;
              }
            });
          }
          if (k != NotificationsController.this.total_unread_count)
          {
            if (!NotificationsController.this.notifyCheck)
            {
              NotificationsController.this.delayedPushMessages.clear();
              NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
            }
          }
          else
          {
            NotificationsController.access$1002(NotificationsController.this, false);
            if (localSharedPreferences.getBoolean("badgeNumber", true)) {
              NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
            }
            return;
          }
          Object localObject = NotificationsController.this;
          if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime()) {}
          for (boolean bool = true;; bool = false)
          {
            ((NotificationsController)localObject).scheduleNotificationDelay(bool);
            break;
          }
        }
      });
      return;
    }
  }
  
  public void processLoadedUnreadMessages(final HashMap<Long, Integer> paramHashMap, final ArrayList<TLRPC.Message> paramArrayList, ArrayList<TLRPC.User> paramArrayList1, ArrayList<TLRPC.Chat> paramArrayList2, ArrayList<TLRPC.EncryptedChat> paramArrayList3)
  {
    MessagesController.getInstance().putUsers(paramArrayList1, true);
    MessagesController.getInstance().putChats(paramArrayList2, true);
    MessagesController.getInstance().putEncryptedChats(paramArrayList3, true);
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.this.pushDialogs.clear();
        NotificationsController.this.pushMessages.clear();
        NotificationsController.this.pushMessagesDict.clear();
        NotificationsController.access$402(NotificationsController.this, 0);
        NotificationsController.access$502(NotificationsController.this, 0);
        SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        HashMap localHashMap = new HashMap();
        long l1;
        Object localObject2;
        Boolean localBoolean;
        int i;
        if (paramArrayList != null)
        {
          localIterator = paramArrayList.iterator();
          while (localIterator.hasNext())
          {
            localObject1 = (TLRPC.Message)localIterator.next();
            l1 = ((TLRPC.Message)localObject1).id;
            long l2 = l1;
            if (((TLRPC.Message)localObject1).to_id.channel_id != 0) {
              l2 = l1 | ((TLRPC.Message)localObject1).to_id.channel_id << 32;
            }
            if (!NotificationsController.this.pushMessagesDict.containsKey(Long.valueOf(l2)))
            {
              localObject2 = new MessageObject((TLRPC.Message)localObject1, null, false);
              if (NotificationsController.this.isPersonalMessage((MessageObject)localObject2)) {
                NotificationsController.access$508(NotificationsController.this);
              }
              long l3 = ((MessageObject)localObject2).getDialogId();
              l1 = l3;
              if (((MessageObject)localObject2).messageOwner.mentioned) {
                l1 = ((MessageObject)localObject2).messageOwner.from_id;
              }
              localBoolean = (Boolean)localHashMap.get(Long.valueOf(l1));
              localObject1 = localBoolean;
              if (localBoolean == null)
              {
                i = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
                if ((i == 2) || (((!localSharedPreferences.getBoolean("EnableAll", true)) || (((int)l1 < 0) && (!localSharedPreferences.getBoolean("EnableGroup", true)))) && (i == 0))) {
                  break label408;
                }
              }
              label408:
              for (bool = true;; bool = false)
              {
                localObject1 = Boolean.valueOf(bool);
                localHashMap.put(Long.valueOf(l1), localObject1);
                if ((!((Boolean)localObject1).booleanValue()) || ((l1 == NotificationsController.this.opened_dialog_id) && (ApplicationLoader.isScreenOn))) {
                  break;
                }
                NotificationsController.this.pushMessagesDict.put(Long.valueOf(l2), localObject2);
                NotificationsController.this.pushMessages.add(0, localObject2);
                if (l3 == l1) {
                  break;
                }
                NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l3), Integer.valueOf(1));
                break;
              }
            }
          }
        }
        Iterator localIterator = paramHashMap.entrySet().iterator();
        if (localIterator.hasNext())
        {
          localObject2 = (Map.Entry)localIterator.next();
          l1 = ((Long)((Map.Entry)localObject2).getKey()).longValue();
          localBoolean = (Boolean)localHashMap.get(Long.valueOf(l1));
          localObject1 = localBoolean;
          if (localBoolean == null)
          {
            int j = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
            localObject1 = (Integer)NotificationsController.this.pushDialogsOverrideMention.get(Long.valueOf(l1));
            i = j;
            if (localObject1 != null)
            {
              i = j;
              if (((Integer)localObject1).intValue() == 1)
              {
                NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l1), Integer.valueOf(0));
                i = 1;
              }
            }
            if ((i == 2) || (((!localSharedPreferences.getBoolean("EnableAll", true)) || (((int)l1 < 0) && (!localSharedPreferences.getBoolean("EnableGroup", true)))) && (i == 0))) {
              break label679;
            }
          }
          label679:
          for (bool = true;; bool = false)
          {
            localObject1 = Boolean.valueOf(bool);
            localHashMap.put(Long.valueOf(l1), localObject1);
            if (!((Boolean)localObject1).booleanValue()) {
              break;
            }
            i = ((Integer)((Map.Entry)localObject2).getValue()).intValue();
            NotificationsController.this.pushDialogs.put(Long.valueOf(l1), Integer.valueOf(i));
            NotificationsController.access$402(NotificationsController.this, NotificationsController.this.total_unread_count + i);
            break;
          }
        }
        if (NotificationsController.this.total_unread_count == 0) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationsController.this.popupMessages.clear();
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
            }
          });
        }
        Object localObject1 = NotificationsController.this;
        if (SystemClock.uptimeMillis() / 1000L < 60L) {}
        for (boolean bool = true;; bool = false)
        {
          ((NotificationsController)localObject1).showOrUpdateNotification(bool);
          if (localSharedPreferences.getBoolean("badgeNumber", true)) {
            NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
          }
          return;
        }
      }
    });
  }
  
  public void processNewMessages(final ArrayList<MessageObject> paramArrayList, final boolean paramBoolean)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    final ArrayList localArrayList = new ArrayList(this.popupMessages);
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        int i1 = localArrayList.size();
        HashMap localHashMap = new HashMap();
        SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        boolean bool2 = localSharedPreferences.getBoolean("PinnedMessages", true);
        final int m = 0;
        int k = 0;
        if (k < paramArrayList.size())
        {
          MessageObject localMessageObject = (MessageObject)paramArrayList.get(k);
          long l1 = localMessageObject.messageOwner.id;
          long l2 = l1;
          if (localMessageObject.messageOwner.to_id.channel_id != 0) {
            l2 = l1 | localMessageObject.messageOwner.to_id.channel_id << 32;
          }
          if (NotificationsController.this.pushMessagesDict.containsKey(Long.valueOf(l2))) {}
          long l3;
          label182:
          do
          {
            for (;;)
            {
              k += 1;
              break;
              l3 = localMessageObject.getDialogId();
              if ((l3 != NotificationsController.this.opened_dialog_id) || (!ApplicationLoader.isScreenOn)) {
                break label182;
              }
              NotificationsController.this.playInChatSound();
            }
            l1 = l3;
            if (!localMessageObject.messageOwner.mentioned) {
              break label227;
            }
          } while ((!bool2) && ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)));
          l1 = localMessageObject.messageOwner.from_id;
          label227:
          if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
            NotificationsController.access$508(NotificationsController.this);
          }
          int n = 1;
          Boolean localBoolean = (Boolean)localHashMap.get(Long.valueOf(l1));
          int j;
          label273:
          Object localObject;
          if ((int)l1 < 0)
          {
            j = 1;
            if ((int)l1 != 0) {
              break label521;
            }
            i = 0;
            localObject = localBoolean;
            if (localBoolean == null)
            {
              m = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
              if ((m == 2) || (((!localSharedPreferences.getBoolean("EnableAll", true)) || ((j != 0) && (!localSharedPreferences.getBoolean("EnableGroup", true)))) && (m == 0))) {
                break label550;
              }
            }
          }
          label521:
          label550:
          for (boolean bool1 = true;; bool1 = false)
          {
            localObject = Boolean.valueOf(bool1);
            localHashMap.put(Long.valueOf(l1), localObject);
            j = i;
            if (i != 0)
            {
              j = i;
              if (localMessageObject.messageOwner.to_id.channel_id != 0)
              {
                j = i;
                if (!localMessageObject.isMegagroup()) {
                  j = 0;
                }
              }
            }
            i = n;
            m = j;
            if (!((Boolean)localObject).booleanValue()) {
              break;
            }
            if (j != 0) {
              localArrayList.add(0, localMessageObject);
            }
            NotificationsController.this.delayedPushMessages.add(localMessageObject);
            NotificationsController.this.pushMessages.add(0, localMessageObject);
            NotificationsController.this.pushMessagesDict.put(Long.valueOf(l2), localMessageObject);
            i = n;
            m = j;
            if (l3 == l1) {
              break;
            }
            NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l3), Integer.valueOf(1));
            i = n;
            m = j;
            break;
            j = 0;
            break label273;
            if (j != 0) {}
            for (localObject = "popupGroup";; localObject = "popupAll")
            {
              i = localSharedPreferences.getInt((String)localObject, 0);
              break;
            }
          }
        }
        if (i != 0) {
          NotificationsController.access$1002(NotificationsController.this, paramBoolean);
        }
        if ((!localArrayList.isEmpty()) && (i1 != localArrayList.size()) && (!AndroidUtilities.needShowPasscode(false))) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationsController.this.popupMessages = NotificationsController.8.this.val$popupArray;
              if (((ApplicationLoader.mainInterfacePaused) || ((!ApplicationLoader.isScreenOn) && (!UserConfig.isWaitingForPasscodeEnter))) && ((m == 3) || ((m == 1) && (ApplicationLoader.isScreenOn)) || ((m == 2) && (!ApplicationLoader.isScreenOn))))
              {
                Intent localIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                localIntent.setFlags(268763140);
                ApplicationLoader.applicationContext.startActivity(localIntent);
              }
            }
          });
        }
      }
    });
  }
  
  public void processReadMessages(final SparseArray<Long> paramSparseArray, final long paramLong, final int paramInt1, int paramInt2, final boolean paramBoolean)
  {
    if (this.popupMessages.isEmpty()) {}
    for (final ArrayList localArrayList = null;; localArrayList = new ArrayList(this.popupMessages))
    {
      this.notificationsQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int k;
          int j;
          int i;
          MessageObject localMessageObject;
          int m;
          long l2;
          long l1;
          if (localArrayList != null)
          {
            k = localArrayList.size();
            if (paramSparseArray != null) {
              j = 0;
            }
          }
          else
          {
            for (;;)
            {
              if (j >= paramSparseArray.size()) {
                break label275;
              }
              int n = paramSparseArray.keyAt(j);
              long l3 = ((Long)paramSparseArray.get(n)).longValue();
              i = 0;
              for (;;)
              {
                if (i < NotificationsController.this.pushMessages.size())
                {
                  localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(i);
                  m = i;
                  if (localMessageObject.getDialogId() == n)
                  {
                    m = i;
                    if (localMessageObject.getId() <= (int)l3)
                    {
                      if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                        NotificationsController.access$510(NotificationsController.this);
                      }
                      if (localArrayList != null) {
                        localArrayList.remove(localMessageObject);
                      }
                      l2 = localMessageObject.messageOwner.id;
                      l1 = l2;
                      if (localMessageObject.messageOwner.to_id.channel_id != 0) {
                        l1 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
                      }
                      NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l1));
                      NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                      NotificationsController.this.pushMessages.remove(i);
                      m = i - 1;
                    }
                  }
                  i = m + 1;
                  continue;
                  k = 0;
                  break;
                }
              }
              j += 1;
            }
            label275:
            if ((localArrayList != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!localArrayList.isEmpty())) {
              localArrayList.clear();
            }
          }
          if ((paramLong != 0L) && ((paramInt1 != 0) || (paramBoolean != 0)))
          {
            j = 0;
            if (j < NotificationsController.this.pushMessages.size())
            {
              localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(j);
              m = j;
              if (localMessageObject.getDialogId() == paramLong)
              {
                i = 0;
                if (paramBoolean == 0) {
                  break label556;
                }
                if (localMessageObject.messageOwner.date <= paramBoolean) {
                  i = 1;
                }
              }
              for (;;)
              {
                m = j;
                if (i != 0)
                {
                  if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                    NotificationsController.access$510(NotificationsController.this);
                  }
                  NotificationsController.this.pushMessages.remove(j);
                  NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                  if (localArrayList != null) {
                    localArrayList.remove(localMessageObject);
                  }
                  l2 = localMessageObject.messageOwner.id;
                  l1 = l2;
                  if (localMessageObject.messageOwner.to_id.channel_id != 0) {
                    l1 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
                  }
                  NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l1));
                  m = j - 1;
                }
                j = m + 1;
                break;
                label556:
                if (!this.val$isPopup)
                {
                  if ((localMessageObject.getId() <= paramInt1) || (paramInt1 < 0)) {
                    i = 1;
                  }
                }
                else if ((localMessageObject.getId() == paramInt1) || (paramInt1 < 0)) {
                  i = 1;
                }
              }
            }
            if ((localArrayList != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!localArrayList.isEmpty())) {
              localArrayList.clear();
            }
          }
          if ((localArrayList != null) && (k != localArrayList.size())) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationsController.this.popupMessages = NotificationsController.7.this.val$popupArray;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
              }
            });
          }
        }
      });
      return;
    }
  }
  
  public void removeDeletedMessagesFromNotifications(final SparseArray<ArrayList<Integer>> paramSparseArray)
  {
    if (this.popupMessages.isEmpty()) {}
    for (final ArrayList localArrayList = null;; localArrayList = new ArrayList(this.popupMessages))
    {
      this.notificationsQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int k = NotificationsController.this.total_unread_count;
          SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          int i = 0;
          while (i < paramSparseArray.size())
          {
            int m = paramSparseArray.keyAt(i);
            long l1 = -m;
            ArrayList localArrayList = (ArrayList)paramSparseArray.get(m);
            Object localObject2 = (Integer)NotificationsController.this.pushDialogs.get(Long.valueOf(l1));
            localObject1 = localObject2;
            if (localObject2 == null) {
              localObject1 = Integer.valueOf(0);
            }
            localObject2 = localObject1;
            int j = 0;
            while (j < localArrayList.size())
            {
              long l2 = ((Integer)localArrayList.get(j)).intValue() | m << 32;
              MessageObject localMessageObject = (MessageObject)NotificationsController.this.pushMessagesDict.get(Long.valueOf(l2));
              localObject3 = localObject2;
              if (localMessageObject != null)
              {
                NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l2));
                NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                NotificationsController.this.pushMessages.remove(localMessageObject);
                if (NotificationsController.this.isPersonalMessage(localMessageObject)) {
                  NotificationsController.access$510(NotificationsController.this);
                }
                if (localArrayList != null) {
                  localArrayList.remove(localMessageObject);
                }
                localObject3 = Integer.valueOf(((Integer)localObject2).intValue() - 1);
              }
              j += 1;
              localObject2 = localObject3;
            }
            Object localObject3 = localObject2;
            if (((Integer)localObject2).intValue() <= 0)
            {
              localObject3 = Integer.valueOf(0);
              NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(l1));
            }
            if (!((Integer)localObject3).equals(localObject1))
            {
              NotificationsController.access$402(NotificationsController.this, NotificationsController.this.total_unread_count - ((Integer)localObject1).intValue());
              NotificationsController.access$402(NotificationsController.this, NotificationsController.this.total_unread_count + ((Integer)localObject3).intValue());
              NotificationsController.this.pushDialogs.put(Long.valueOf(l1), localObject3);
            }
            if (((Integer)localObject3).intValue() == 0)
            {
              NotificationsController.this.pushDialogs.remove(Long.valueOf(l1));
              NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(l1));
              if ((localArrayList != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!localArrayList.isEmpty())) {
                localArrayList.clear();
              }
            }
            i += 1;
          }
          if (localArrayList != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationsController.this.popupMessages = NotificationsController.6.this.val$popupArray;
              }
            });
          }
          if (k != NotificationsController.this.total_unread_count)
          {
            if (!NotificationsController.this.notifyCheck)
            {
              NotificationsController.this.delayedPushMessages.clear();
              NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
            }
          }
          else
          {
            NotificationsController.access$1002(NotificationsController.this, false);
            if (localSharedPreferences.getBoolean("badgeNumber", true)) {
              NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
            }
            return;
          }
          Object localObject1 = NotificationsController.this;
          if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime()) {}
          for (boolean bool = true;; bool = false)
          {
            ((NotificationsController)localObject1).scheduleNotificationDelay(bool);
            break;
          }
        }
      });
      return;
    }
  }
  
  public void removeNotificationsForDialog(long paramLong)
  {
    getInstance().processReadMessages(null, paramLong, 0, Integer.MAX_VALUE, false);
    HashMap localHashMap = new HashMap();
    localHashMap.put(Long.valueOf(paramLong), Integer.valueOf(0));
    getInstance().processDialogsUpdateRead(localHashMap);
  }
  
  protected void repeatNotificationMaybe()
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = Calendar.getInstance().get(11);
        if ((i >= 11) && (i <= 22))
        {
          NotificationsController.this.notificationManager.cancel(1);
          NotificationsController.this.showOrUpdateNotification(true);
          return;
        }
        NotificationsController.this.scheduleNotificationRepeat();
      }
    });
  }
  
  public void setBadgeEnabled(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = this.total_unread_count;; i = 0)
    {
      setBadge(i);
      return;
    }
  }
  
  public void setInChatSoundEnabled(boolean paramBoolean)
  {
    this.inChatSoundEnabled = paramBoolean;
  }
  
  public void setLastOnlineFromOtherDevice(final int paramInt)
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        FileLog.e("tmessages", "set last online from other device = " + paramInt);
        NotificationsController.access$1302(NotificationsController.this, paramInt);
      }
    });
  }
  
  public void setOpenedDialogId(final long paramLong)
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.access$302(NotificationsController.this, paramLong);
      }
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/NotificationsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */