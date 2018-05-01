package org.telegram.messenger;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.telegram.tgnet.TLRPC.TL_messages_botResults;
import org.telegram.tgnet.TLRPC.TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_BotResults;
import org.telegram.tgnet.TLRPC.messages_Dialogs;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;

public class MessagesStorage
{
  private static volatile MessagesStorage[] Instance = new MessagesStorage[3];
  private File cacheFile;
  private int currentAccount;
  private SQLiteDatabase database;
  private int lastDateValue = 0;
  private int lastPtsValue = 0;
  private int lastQtsValue = 0;
  private int lastSavedDate = 0;
  private int lastSavedPts = 0;
  private int lastSavedQts = 0;
  private int lastSavedSeq = 0;
  private int lastSecretVersion = 0;
  private int lastSeqValue = 0;
  private AtomicLong lastTaskId = new AtomicLong(System.currentTimeMillis());
  private CountDownLatch openSync = new CountDownLatch(1);
  private int secretG = 0;
  private byte[] secretPBytes = null;
  private File shmCacheFile;
  private DispatchQueue storageQueue = new DispatchQueue("storageQueue");
  private File walCacheFile;
  
  public MessagesStorage(int paramInt)
  {
    this.currentAccount = paramInt;
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesStorage.this.openDatabase(true);
      }
    });
  }
  
  public static void addUsersAndChatsFromMessage(TLRPC.Message paramMessage, ArrayList<Integer> paramArrayList1, ArrayList<Integer> paramArrayList2)
  {
    if (paramMessage.from_id != 0)
    {
      if (paramMessage.from_id <= 0) {
        break label273;
      }
      if (!paramArrayList1.contains(Integer.valueOf(paramMessage.from_id))) {
        paramArrayList1.add(Integer.valueOf(paramMessage.from_id));
      }
    }
    int i;
    Object localObject;
    for (;;)
    {
      if ((paramMessage.via_bot_id != 0) && (!paramArrayList1.contains(Integer.valueOf(paramMessage.via_bot_id)))) {
        paramArrayList1.add(Integer.valueOf(paramMessage.via_bot_id));
      }
      if (paramMessage.action == null) {
        break;
      }
      if ((paramMessage.action.user_id != 0) && (!paramArrayList1.contains(Integer.valueOf(paramMessage.action.user_id)))) {
        paramArrayList1.add(Integer.valueOf(paramMessage.action.user_id));
      }
      if ((paramMessage.action.channel_id != 0) && (!paramArrayList2.contains(Integer.valueOf(paramMessage.action.channel_id)))) {
        paramArrayList2.add(Integer.valueOf(paramMessage.action.channel_id));
      }
      if ((paramMessage.action.chat_id != 0) && (!paramArrayList2.contains(Integer.valueOf(paramMessage.action.chat_id)))) {
        paramArrayList2.add(Integer.valueOf(paramMessage.action.chat_id));
      }
      if (paramMessage.action.users.isEmpty()) {
        break;
      }
      for (i = 0; i < paramMessage.action.users.size(); i++)
      {
        localObject = (Integer)paramMessage.action.users.get(i);
        if (!paramArrayList1.contains(localObject)) {
          paramArrayList1.add(localObject);
        }
      }
      label273:
      if (!paramArrayList2.contains(Integer.valueOf(-paramMessage.from_id))) {
        paramArrayList2.add(Integer.valueOf(-paramMessage.from_id));
      }
    }
    if (!paramMessage.entities.isEmpty())
    {
      i = 0;
      if (i < paramMessage.entities.size())
      {
        localObject = (TLRPC.MessageEntity)paramMessage.entities.get(i);
        if ((localObject instanceof TLRPC.TL_messageEntityMentionName)) {
          paramArrayList1.add(Integer.valueOf(((TLRPC.TL_messageEntityMentionName)localObject).user_id));
        }
        for (;;)
        {
          i++;
          break;
          if ((localObject instanceof TLRPC.TL_inputMessageEntityMentionName)) {
            paramArrayList1.add(Integer.valueOf(((TLRPC.TL_inputMessageEntityMentionName)localObject).user_id.user_id));
          }
        }
      }
    }
    if ((paramMessage.media != null) && (paramMessage.media.user_id != 0) && (!paramArrayList1.contains(Integer.valueOf(paramMessage.media.user_id)))) {
      paramArrayList1.add(Integer.valueOf(paramMessage.media.user_id));
    }
    if (paramMessage.fwd_from != null)
    {
      if ((paramMessage.fwd_from.from_id != 0) && (!paramArrayList1.contains(Integer.valueOf(paramMessage.fwd_from.from_id)))) {
        paramArrayList1.add(Integer.valueOf(paramMessage.fwd_from.from_id));
      }
      if ((paramMessage.fwd_from.channel_id != 0) && (!paramArrayList2.contains(Integer.valueOf(paramMessage.fwd_from.channel_id)))) {
        paramArrayList2.add(Integer.valueOf(paramMessage.fwd_from.channel_id));
      }
      if (paramMessage.fwd_from.saved_from_peer != null)
      {
        if (paramMessage.fwd_from.saved_from_peer.user_id == 0) {
          break label637;
        }
        if (!paramArrayList2.contains(Integer.valueOf(paramMessage.fwd_from.saved_from_peer.user_id))) {
          paramArrayList1.add(Integer.valueOf(paramMessage.fwd_from.saved_from_peer.user_id));
        }
      }
    }
    for (;;)
    {
      if ((paramMessage.ttl < 0) && (!paramArrayList2.contains(Integer.valueOf(-paramMessage.ttl)))) {
        paramArrayList2.add(Integer.valueOf(-paramMessage.ttl));
      }
      return;
      label637:
      if (paramMessage.fwd_from.saved_from_peer.channel_id != 0)
      {
        if (!paramArrayList2.contains(Integer.valueOf(paramMessage.fwd_from.saved_from_peer.channel_id))) {
          paramArrayList2.add(Integer.valueOf(paramMessage.fwd_from.saved_from_peer.channel_id));
        }
      }
      else if ((paramMessage.fwd_from.saved_from_peer.chat_id != 0) && (!paramArrayList2.contains(Integer.valueOf(paramMessage.fwd_from.saved_from_peer.chat_id)))) {
        paramArrayList2.add(Integer.valueOf(paramMessage.fwd_from.saved_from_peer.chat_id));
      }
    }
  }
  
  private void cleanupInternal()
  {
    this.lastDateValue = 0;
    this.lastSeqValue = 0;
    this.lastPtsValue = 0;
    this.lastQtsValue = 0;
    this.lastSecretVersion = 0;
    this.lastSavedSeq = 0;
    this.lastSavedPts = 0;
    this.lastSavedDate = 0;
    this.lastSavedQts = 0;
    this.secretPBytes = null;
    this.secretG = 0;
    if (this.database != null)
    {
      this.database.close();
      this.database = null;
    }
    if (this.cacheFile != null)
    {
      this.cacheFile.delete();
      this.cacheFile = null;
    }
    if (this.walCacheFile != null)
    {
      this.walCacheFile.delete();
      this.walCacheFile = null;
    }
    if (this.shmCacheFile != null)
    {
      this.shmCacheFile.delete();
      this.shmCacheFile = null;
    }
  }
  
  private void closeHolesInTable(String paramString, long paramLong, int paramInt1, int paramInt2)
    throws Exception
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    int j;
    try
    {
      localObject1 = this.database;
      localObject2 = Locale.US;
      localObject3 = new java/lang/StringBuilder;
      ((StringBuilder)localObject3).<init>();
      localObject3 = ((SQLiteDatabase)localObject1).queryFinalized(String.format((Locale)localObject2, "SELECT start, end FROM " + paramString + " WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), new Object[0]);
      localObject2 = null;
      while (((SQLiteCursor)localObject3).next())
      {
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new java/util/ArrayList;
          ((ArrayList)localObject1).<init>();
        }
        i = ((SQLiteCursor)localObject3).intValue(0);
        j = ((SQLiteCursor)localObject3).intValue(1);
        if (i == j)
        {
          localObject2 = localObject1;
          if (i == 1) {
            break;
          }
        }
        else
        {
          localObject2 = new org/telegram/messenger/MessagesStorage$Hole;
          ((Hole)localObject2).<init>(this, i, j);
          ((ArrayList)localObject1).add(localObject2);
          localObject2 = localObject1;
          continue;
          return;
        }
      }
    }
    catch (Exception paramString)
    {
      FileLog.e(paramString);
    }
    do
    {
      ((SQLiteCursor)localObject3).dispose();
    } while (localObject2 == null);
    int i = 0;
    label249:
    Object localObject4;
    Object localObject5;
    if (i < ((ArrayList)localObject2).size())
    {
      localObject1 = (Hole)((ArrayList)localObject2).get(i);
      if ((paramInt2 < ((Hole)localObject1).end - 1) || (paramInt1 > ((Hole)localObject1).start + 1)) {
        break label392;
      }
      localObject3 = this.database;
      localObject4 = Locale.US;
      localObject5 = new java/lang/StringBuilder;
      ((StringBuilder)localObject5).<init>();
      ((SQLiteDatabase)localObject3).executeFast(String.format((Locale)localObject4, "DELETE FROM " + paramString + " WHERE uid = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject1).start), Integer.valueOf(((Hole)localObject1).end) })).stepThis().dispose();
    }
    for (;;)
    {
      i++;
      break label249;
      break;
      label392:
      if (paramInt2 >= ((Hole)localObject1).end - 1)
      {
        j = ((Hole)localObject1).end;
        if (j != paramInt1) {
          try
          {
            localObject5 = this.database;
            localObject3 = Locale.US;
            localObject4 = new java/lang/StringBuilder;
            ((StringBuilder)localObject4).<init>();
            ((SQLiteDatabase)localObject5).executeFast(String.format((Locale)localObject3, "UPDATE " + paramString + " SET end = %d WHERE uid = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt1), Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject1).start), Integer.valueOf(((Hole)localObject1).end) })).stepThis().dispose();
          }
          catch (Exception localException1)
          {
            FileLog.e(localException1);
          }
        }
      }
      else if (paramInt1 <= localException1.start + 1)
      {
        j = localException1.start;
        if (j != paramInt2) {
          try
          {
            localObject3 = this.database;
            localObject5 = Locale.US;
            localObject4 = new java/lang/StringBuilder;
            ((StringBuilder)localObject4).<init>();
            ((SQLiteDatabase)localObject3).executeFast(String.format((Locale)localObject5, "UPDATE " + paramString + " SET start = %d WHERE uid = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt2), Long.valueOf(paramLong), Integer.valueOf(localException1.start), Integer.valueOf(localException1.end) })).stepThis().dispose();
          }
          catch (Exception localException2)
          {
            FileLog.e(localException2);
          }
        }
      }
      else
      {
        localObject3 = this.database;
        localObject4 = Locale.US;
        localObject5 = new java/lang/StringBuilder;
        ((StringBuilder)localObject5).<init>();
        ((SQLiteDatabase)localObject3).executeFast(String.format((Locale)localObject4, "DELETE FROM " + paramString + " WHERE uid = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(localException2.start), Integer.valueOf(localException2.end) })).stepThis().dispose();
        localObject3 = this.database;
        localObject5 = new java/lang/StringBuilder;
        ((StringBuilder)localObject5).<init>();
        localObject3 = ((SQLiteDatabase)localObject3).executeFast("REPLACE INTO " + paramString + " VALUES(?, ?, ?)");
        ((SQLitePreparedStatement)localObject3).requery();
        ((SQLitePreparedStatement)localObject3).bindLong(1, paramLong);
        ((SQLitePreparedStatement)localObject3).bindInteger(2, localException2.start);
        ((SQLitePreparedStatement)localObject3).bindInteger(3, paramInt1);
        ((SQLitePreparedStatement)localObject3).step();
        ((SQLitePreparedStatement)localObject3).requery();
        ((SQLitePreparedStatement)localObject3).bindLong(1, paramLong);
        ((SQLitePreparedStatement)localObject3).bindInteger(2, paramInt2);
        ((SQLitePreparedStatement)localObject3).bindInteger(3, localException2.end);
        ((SQLitePreparedStatement)localObject3).step();
        ((SQLitePreparedStatement)localObject3).dispose();
      }
    }
  }
  
  public static void createFirstHoles(long paramLong, SQLitePreparedStatement paramSQLitePreparedStatement1, SQLitePreparedStatement paramSQLitePreparedStatement2, int paramInt)
    throws Exception
  {
    paramSQLitePreparedStatement1.requery();
    paramSQLitePreparedStatement1.bindLong(1, paramLong);
    int i;
    if (paramInt == 1)
    {
      i = 1;
      paramSQLitePreparedStatement1.bindInteger(2, i);
      paramSQLitePreparedStatement1.bindInteger(3, paramInt);
      paramSQLitePreparedStatement1.step();
      i = 0;
      label41:
      if (i >= 5) {
        return;
      }
      paramSQLitePreparedStatement2.requery();
      paramSQLitePreparedStatement2.bindLong(1, paramLong);
      paramSQLitePreparedStatement2.bindInteger(2, i);
      if (paramInt != 1) {
        break label104;
      }
    }
    label104:
    for (int j = 1;; j = 0)
    {
      paramSQLitePreparedStatement2.bindInteger(3, j);
      paramSQLitePreparedStatement2.bindInteger(4, paramInt);
      paramSQLitePreparedStatement2.step();
      i++;
      break label41;
      i = 0;
      break;
    }
  }
  
  private void doneHolesInTable(String paramString, long paramLong, int paramInt)
    throws Exception
  {
    if (paramInt == 0) {
      this.database.executeFast(String.format(Locale.US, "DELETE FROM " + paramString + " WHERE uid = %d", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
    }
    for (;;)
    {
      paramString = this.database.executeFast("REPLACE INTO " + paramString + " VALUES(?, ?, ?)");
      paramString.requery();
      paramString.bindLong(1, paramLong);
      paramString.bindInteger(2, 1);
      paramString.bindInteger(3, 1);
      paramString.step();
      paramString.dispose();
      return;
      this.database.executeFast(String.format(Locale.US, "DELETE FROM " + paramString + " WHERE uid = %d AND start = 0", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
    }
  }
  
  private void ensureOpened()
  {
    try
    {
      this.openSync.await();
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
  }
  
  private void fixNotificationSettings()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Map localMap;
        Object localObject1;
        Object localObject2;
        int i;
        long l;
        try
        {
          LongSparseArray localLongSparseArray = new android/util/LongSparseArray;
          localLongSparseArray.<init>();
          localMap = MessagesController.getNotificationsSettings(MessagesStorage.this.currentAccount).getAll();
          localObject1 = localMap.entrySet().iterator();
          for (;;)
          {
            if (((Iterator)localObject1).hasNext())
            {
              localObject2 = (Map.Entry)((Iterator)localObject1).next();
              String str = (String)((Map.Entry)localObject2).getKey();
              if (str.startsWith("notify2_"))
              {
                localObject2 = (Integer)((Map.Entry)localObject2).getValue();
                if ((((Integer)localObject2).intValue() == 2) || (((Integer)localObject2).intValue() == 3))
                {
                  str = str.replace("notify2_", "");
                  i = ((Integer)localObject2).intValue();
                  if (i == 2)
                  {
                    l = 1L;
                    try
                    {
                      label135:
                      localLongSparseArray.put(Long.parseLong(str), Long.valueOf(l));
                    }
                    catch (Exception localException2)
                    {
                      localException2.printStackTrace();
                    }
                    continue;
                    return;
                  }
                }
              }
            }
          }
        }
        catch (Throwable localThrowable)
        {
          FileLog.e(localThrowable);
        }
        for (;;)
        {
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          localObject2 = (Integer)localMap.get("notifyuntil_" + localException2);
          if (localObject2 != null)
          {
            l = ((Integer)localObject2).intValue() << 32 | 1L;
            break label135;
          }
          l = 1L;
          break label135;
          i = ((Integer)localObject2).intValue();
          if (i != 3) {
            break;
          }
          break;
          try
          {
            MessagesStorage.this.database.beginTransaction();
            localObject1 = MessagesStorage.this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
            for (i = 0; i < localThrowable.size(); i++)
            {
              ((SQLitePreparedStatement)localObject1).requery();
              ((SQLitePreparedStatement)localObject1).bindLong(1, localThrowable.keyAt(i));
              ((SQLitePreparedStatement)localObject1).bindLong(2, ((Long)localThrowable.valueAt(i)).longValue());
              ((SQLitePreparedStatement)localObject1).step();
            }
            ((SQLitePreparedStatement)localObject1).dispose();
            MessagesStorage.this.database.commitTransaction();
          }
          catch (Exception localException1)
          {
            FileLog.e(localException1);
          }
        }
      }
    });
  }
  
  private void fixUnsupportedMedia(TLRPC.Message paramMessage)
  {
    if (paramMessage == null) {}
    for (;;)
    {
      return;
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported_old))
      {
        if (paramMessage.media.bytes.length == 0)
        {
          paramMessage.media.bytes = new byte[1];
          paramMessage.media.bytes[0] = ((byte)76);
        }
      }
      else if ((paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported))
      {
        paramMessage.media = new TLRPC.TL_messageMediaUnsupported_old();
        paramMessage.media.bytes = new byte[1];
        paramMessage.media.bytes[0] = ((byte)76);
        paramMessage.flags |= 0x200;
      }
    }
  }
  
  private String formatUserSearchName(TLRPC.User paramUser)
  {
    StringBuilder localStringBuilder = new StringBuilder("");
    if ((paramUser.first_name != null) && (paramUser.first_name.length() > 0)) {
      localStringBuilder.append(paramUser.first_name);
    }
    if ((paramUser.last_name != null) && (paramUser.last_name.length() > 0))
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(" ");
      }
      localStringBuilder.append(paramUser.last_name);
    }
    localStringBuilder.append(";;;");
    if ((paramUser.username != null) && (paramUser.username.length() > 0)) {
      localStringBuilder.append(paramUser.username);
    }
    return localStringBuilder.toString().toLowerCase();
  }
  
  public static MessagesStorage getInstance(int paramInt)
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
        localObject2 = new org/telegram/messenger/MessagesStorage;
        ((MessagesStorage)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (MessagesStorage)localObject2;
    }
    finally {}
  }
  
  private int getMessageMediaType(TLRPC.Message paramMessage)
  {
    int i = 0;
    int j;
    if ((paramMessage instanceof TLRPC.TL_message_secret)) {
      if (((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (!MessageObject.isGifMessage(paramMessage))) || (((paramMessage.ttl > 0) && (paramMessage.ttl <= 60)) || (MessageObject.isVoiceMessage(paramMessage)) || (MessageObject.isVideoMessage(paramMessage)) || (MessageObject.isRoundVideoMessage(paramMessage)))) {
        j = 1;
      }
    }
    for (;;)
    {
      return j;
      j = i;
      if (!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        j = i;
        if (!MessageObject.isVideoMessage(paramMessage))
        {
          do
          {
            j = -1;
            break;
            if (((paramMessage instanceof TLRPC.TL_message)) && (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))) && (paramMessage.media.ttl_seconds != 0))
            {
              j = 1;
              break;
            }
            j = i;
            if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) {
              break;
            }
          } while (!MessageObject.isVideoMessage(paramMessage));
          j = i;
        }
      }
    }
  }
  
  private static boolean isEmpty(LongSparseArray<?> paramLongSparseArray)
  {
    if ((paramLongSparseArray == null) || (paramLongSparseArray.size() == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean isEmpty(SparseArray<?> paramSparseArray)
  {
    if ((paramSparseArray == null) || (paramSparseArray.size() == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean isEmpty(SparseIntArray paramSparseIntArray)
  {
    if ((paramSparseIntArray == null) || (paramSparseIntArray.size() == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean isEmpty(List<?> paramList)
  {
    if ((paramList == null) || (paramList.isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static boolean isEmpty(SparseLongArray paramSparseLongArray)
  {
    if ((paramSparseLongArray == null) || (paramSparseLongArray.size() == 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean isValidKeyboardToSave(TLRPC.Message paramMessage)
  {
    if ((paramMessage.reply_markup != null) && (!(paramMessage.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) && ((!paramMessage.reply_markup.selective) || (paramMessage.mentioned))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void loadPendingTasks()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        SQLiteCursor localSQLiteCursor;
        long l1;
        NativeByteBuffer localNativeByteBuffer;
        int i;
        try
        {
          localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
          while (localSQLiteCursor.next())
          {
            l1 = localSQLiteCursor.longValue(0);
            localNativeByteBuffer = localSQLiteCursor.byteBufferValue(1);
            if (localNativeByteBuffer != null)
            {
              i = localNativeByteBuffer.readInt32(false);
              switch (i)
              {
              default: 
                localNativeByteBuffer.reuse();
                continue;
                return;
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          Object localObject2 = TLRPC.Chat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          if (localObject2 == null) {
            break;
          }
          Object localObject1 = Utilities.stageQueue;
          Object localObject3 = new org/telegram/messenger/MessagesStorage$8$1;
          ((1)localObject3).<init>(this, (TLRPC.Chat)localObject2, l1);
          ((DispatchQueue)localObject1).postRunnable((Runnable)localObject3);
          break;
          i = localNativeByteBuffer.readInt32(false);
          int j = localNativeByteBuffer.readInt32(false);
          localObject1 = Utilities.stageQueue;
          localObject3 = new org/telegram/messenger/MessagesStorage$8$2;
          ((2)localObject3).<init>(this, i, j, l1);
          ((DispatchQueue)localObject1).postRunnable((Runnable)localObject3);
          break;
          localObject3 = new org/telegram/tgnet/TLRPC$TL_dialog;
          ((TLRPC.TL_dialog)localObject3).<init>();
          ((TLRPC.TL_dialog)localObject3).id = localNativeByteBuffer.readInt64(false);
          ((TLRPC.TL_dialog)localObject3).top_message = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject3).read_inbox_max_id = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject3).read_outbox_max_id = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject3).unread_count = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject3).last_message_date = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject3).pts = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject3).flags = localNativeByteBuffer.readInt32(false);
          if (i >= 5)
          {
            ((TLRPC.TL_dialog)localObject3).pinned = localNativeByteBuffer.readBool(false);
            ((TLRPC.TL_dialog)localObject3).pinnedNum = localNativeByteBuffer.readInt32(false);
          }
          if (i >= 8) {
            ((TLRPC.TL_dialog)localObject3).unread_mentions_count = localNativeByteBuffer.readInt32(false);
          }
          localObject1 = TLRPC.InputPeer.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          localObject2 = new org/telegram/messenger/MessagesStorage$8$3;
          ((3)localObject2).<init>(this, (TLRPC.TL_dialog)localObject3, (TLRPC.InputPeer)localObject1, l1);
          AndroidUtilities.runOnUIThread((Runnable)localObject2);
          break;
          long l2 = localNativeByteBuffer.readInt64(false);
          localObject3 = TLRPC.InputPeer.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          localObject1 = (TLRPC.TL_inputMediaGame)TLRPC.InputMedia.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          SendMessagesHelper.getInstance(MessagesStorage.this.currentAccount).sendGame((TLRPC.InputPeer)localObject3, (TLRPC.TL_inputMediaGame)localObject1, l2, l1);
          break;
          l2 = localNativeByteBuffer.readInt64(false);
          boolean bool = localNativeByteBuffer.readBool(false);
          localObject1 = TLRPC.InputPeer.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          localObject3 = new org/telegram/messenger/MessagesStorage$8$4;
          ((4)localObject3).<init>(this, l2, bool, (TLRPC.InputPeer)localObject1, l1);
          AndroidUtilities.runOnUIThread((Runnable)localObject3);
          break;
          j = localNativeByteBuffer.readInt32(false);
          i = localNativeByteBuffer.readInt32(false);
          localObject2 = TLRPC.InputChannel.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          localObject1 = Utilities.stageQueue;
          localObject3 = new org/telegram/messenger/MessagesStorage$8$5;
          ((5)localObject3).<init>(this, j, i, l1, (TLRPC.InputChannel)localObject2);
          ((DispatchQueue)localObject1).postRunnable((Runnable)localObject3);
          break;
          i = localNativeByteBuffer.readInt32(false);
          j = localNativeByteBuffer.readInt32(false);
          localObject3 = TLRPC.TL_messages_deleteMessages.TLdeserialize(localNativeByteBuffer, j, false);
          localObject1 = localObject3;
          if (localObject3 == null) {
            localObject1 = TLRPC.TL_channels_deleteMessages.TLdeserialize(localNativeByteBuffer, j, false);
          }
          if (localObject1 == null)
          {
            MessagesStorage.this.removePendingTask(l1);
            break;
          }
          localObject3 = new org/telegram/messenger/MessagesStorage$8$6;
          ((6)localObject3).<init>(this, i, l1, (TLObject)localObject1);
          AndroidUtilities.runOnUIThread((Runnable)localObject3);
          break;
          localSQLiteCursor.dispose();
        }
      }
    });
  }
  
  private ArrayList<Long> markMessagesAsDeletedInternal(int paramInt1, int paramInt2)
  {
    try
    {
      ArrayList localArrayList1 = new java/util/ArrayList;
      localArrayList1.<init>();
      LongSparseArray localLongSparseArray = new android/util/LongSparseArray;
      localLongSparseArray.<init>();
      long l1 = paramInt2 | paramInt1 << 32;
      ArrayList localArrayList2 = new java/util/ArrayList;
      localArrayList2.<init>();
      int i = UserConfig.getInstance(this.currentAccount).getClientUserId();
      Object localObject1 = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out, mention FROM messages WHERE uid = %d AND mid <= %d", new Object[] { Integer.valueOf(-paramInt1), Long.valueOf(l1) }), new Object[0]);
      try
      {
        while (((SQLiteCursor)localObject1).next())
        {
          l2 = ((SQLiteCursor)localObject1).longValue(0);
          if (l2 != i)
          {
            paramInt2 = ((SQLiteCursor)localObject1).intValue(2);
            Object localObject3;
            if (((SQLiteCursor)localObject1).intValue(3) == 0)
            {
              localObject2 = (Integer[])localLongSparseArray.get(l2);
              localObject3 = localObject2;
              if (localObject2 == null)
              {
                localObject3 = new Integer[2];
                localObject3[0] = Integer.valueOf(0);
                localObject3[1] = Integer.valueOf(0);
                localLongSparseArray.put(l2, localObject3);
              }
              if (paramInt2 < 2)
              {
                localObject2 = localObject3[1];
                localObject3[1] = Integer.valueOf(localObject3[1].intValue() + 1);
              }
              if ((paramInt2 == 0) || (paramInt2 == 2))
              {
                localObject2 = localObject3[0];
                localObject3[0] = Integer.valueOf(localObject3[0].intValue() + 1);
              }
            }
            if ((int)l2 == 0)
            {
              localObject2 = ((SQLiteCursor)localObject1).byteBufferValue(1);
              if (localObject2 != null)
              {
                localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                ((TLRPC.Message)localObject3).readAttachPath((AbstractSerializedData)localObject2, UserConfig.getInstance(this.currentAccount).clientUserId);
                ((NativeByteBuffer)localObject2).reuse();
                if (localObject3 != null)
                {
                  if (!(((TLRPC.Message)localObject3).media instanceof TLRPC.TL_messageMediaPhoto)) {
                    break label607;
                  }
                  localObject3 = ((TLRPC.Message)localObject3).media.photo.sizes.iterator();
                  while (((Iterator)localObject3).hasNext())
                  {
                    localObject2 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject3).next());
                    if ((localObject2 != null) && (((File)localObject2).toString().length() > 0)) {
                      localArrayList2.add(localObject2);
                    }
                  }
                }
              }
            }
          }
        }
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          long l2;
          Object localObject2;
          FileLog.e(localException1);
          ((SQLiteCursor)localObject1).dispose();
          FileLoader.getInstance(this.currentAccount).deleteFiles(localArrayList2, 0);
          for (paramInt2 = 0; paramInt2 < localLongSparseArray.size(); paramInt2++)
          {
            l2 = localLongSparseArray.keyAt(paramInt2);
            localObject4 = (Integer[])localLongSparseArray.valueAt(paramInt2);
            localObject1 = this.database;
            localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            localObject2 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT unread_count, unread_count_i FROM dialogs WHERE did = " + l2, new Object[0]);
            i = 0;
            int j = 0;
            if (((SQLiteCursor)localObject2).next())
            {
              i = ((SQLiteCursor)localObject2).intValue(0);
              j = ((SQLiteCursor)localObject2).intValue(1);
            }
            ((SQLiteCursor)localObject2).dispose();
            localArrayList1.add(Long.valueOf(l2));
            localObject2 = this.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
            ((SQLitePreparedStatement)localObject2).requery();
            ((SQLitePreparedStatement)localObject2).bindInteger(1, Math.max(0, i - localObject4[0].intValue()));
            ((SQLitePreparedStatement)localObject2).bindInteger(2, Math.max(0, j - localObject4[1].intValue()));
            ((SQLitePreparedStatement)localObject2).bindLong(3, l2);
            ((SQLitePreparedStatement)localObject2).step();
            ((SQLitePreparedStatement)localObject2).dispose();
          }
          label607:
          if ((((TLRPC.Message)localObject4).media instanceof TLRPC.TL_messageMediaDocument))
          {
            localObject2 = FileLoader.getPathToAttach(((TLRPC.Message)localObject4).media.document);
            if ((localObject2 != null) && (((File)localObject2).toString().length() > 0)) {
              localArrayList2.add(localObject2);
            }
            localObject4 = FileLoader.getPathToAttach(((TLRPC.Message)localObject4).media.document.thumb);
            if ((localObject4 != null) && (((File)localObject4).toString().length() > 0)) {
              localArrayList2.add(localObject4);
            }
          }
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE uid = %d AND mid <= %d", new Object[] { Integer.valueOf(-paramInt1), Long.valueOf(l1) })).stepThis().dispose();
        this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE uid = %d AND mid <= %d", new Object[] { Integer.valueOf(-paramInt1), Long.valueOf(l1) })).stepThis().dispose();
        this.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
        localObject4 = localArrayList1;
      }
    }
    catch (Exception localException2)
    {
      for (;;)
      {
        Object localObject4;
        FileLog.e(localException2);
        Object localObject5 = null;
      }
    }
    return (ArrayList<Long>)localObject4;
  }
  
  private ArrayList<Long> markMessagesAsDeletedInternal(ArrayList<Integer> paramArrayList, int paramInt)
  {
    try
    {
      ArrayList localArrayList = new java/util/ArrayList;
      localArrayList.<init>();
      LongSparseArray localLongSparseArray = new android/util/LongSparseArray;
      localLongSparseArray.<init>();
      Object localObject1;
      int i;
      long l2;
      Object localObject2;
      SQLiteCursor localSQLiteCursor;
      if (paramInt != 0)
      {
        localObject1 = new java/lang/StringBuilder;
        ((StringBuilder)localObject1).<init>(paramArrayList.size());
        for (i = 0; i < paramArrayList.size(); i++)
        {
          long l1 = ((Integer)paramArrayList.get(i)).intValue();
          l2 = paramInt;
          if (((StringBuilder)localObject1).length() > 0) {
            ((StringBuilder)localObject1).append(',');
          }
          ((StringBuilder)localObject1).append(l1 | l2 << 32);
        }
        localObject1 = ((StringBuilder)localObject1).toString();
        localObject2 = new java/util/ArrayList;
        ((ArrayList)localObject2).<init>();
        paramInt = UserConfig.getInstance(this.currentAccount).getClientUserId();
        localSQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out, mention FROM messages WHERE mid IN(%s)", new Object[] { localObject1 }), new Object[0]);
      }
      for (;;)
      {
        Object localObject3;
        try
        {
          if (localSQLiteCursor.next())
          {
            l2 = localSQLiteCursor.longValue(0);
            if (l2 == paramInt) {
              continue;
            }
            i = localSQLiteCursor.intValue(2);
            if (localSQLiteCursor.intValue(3) == 0)
            {
              localObject3 = (Integer[])localLongSparseArray.get(l2);
              localObject4 = localObject3;
              if (localObject3 == null)
              {
                localObject4 = new Integer[2];
                localObject4[0] = Integer.valueOf(0);
                localObject4[1] = Integer.valueOf(0);
                localLongSparseArray.put(l2, localObject4);
              }
              if (i < 2)
              {
                localObject3 = localObject4[1];
                localObject4[1] = Integer.valueOf(localObject4[1].intValue() + 1);
              }
              if ((i == 0) || (i == 2))
              {
                localObject3 = localObject4[0];
                localObject4[0] = Integer.valueOf(localObject4[0].intValue() + 1);
              }
            }
            if ((int)l2 != 0) {
              continue;
            }
            localObject3 = localSQLiteCursor.byteBufferValue(1);
            if (localObject3 == null) {
              continue;
            }
            Object localObject4 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
            ((TLRPC.Message)localObject4).readAttachPath((AbstractSerializedData)localObject3, UserConfig.getInstance(this.currentAccount).clientUserId);
            ((NativeByteBuffer)localObject3).reuse();
            if (localObject4 == null) {
              continue;
            }
            if (!(((TLRPC.Message)localObject4).media instanceof TLRPC.TL_messageMediaPhoto)) {
              break label692;
            }
            localObject3 = ((TLRPC.Message)localObject4).media.photo.sizes.iterator();
            if (!((Iterator)localObject3).hasNext()) {
              continue;
            }
            localObject4 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject3).next());
            if ((localObject4 == null) || (((File)localObject4).toString().length() <= 0)) {
              continue;
            }
            ((ArrayList)localObject2).add(localObject4);
            continue;
          }
          if (paramInt >= localLongSparseArray.size()) {
            break label783;
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          localSQLiteCursor.dispose();
          FileLoader.getInstance(this.currentAccount).deleteFiles((ArrayList)localObject2, 0);
          paramInt = 0;
        }
        Object localObject5;
        for (;;)
        {
          l2 = localLongSparseArray.keyAt(paramInt);
          localObject5 = (Integer[])localLongSparseArray.valueAt(paramInt);
          localObject3 = this.database;
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          localObject3 = ((SQLiteDatabase)localObject3).queryFinalized("SELECT unread_count, unread_count_i FROM dialogs WHERE did = " + l2, new Object[0]);
          i = 0;
          int j = 0;
          if (((SQLiteCursor)localObject3).next())
          {
            i = ((SQLiteCursor)localObject3).intValue(0);
            j = ((SQLiteCursor)localObject3).intValue(1);
          }
          ((SQLiteCursor)localObject3).dispose();
          localArrayList.add(Long.valueOf(l2));
          localObject3 = this.database.executeFast("UPDATE dialogs SET unread_count = ?, unread_count_i = ? WHERE did = ?");
          ((SQLitePreparedStatement)localObject3).requery();
          ((SQLitePreparedStatement)localObject3).bindInteger(1, Math.max(0, i - localObject5[0].intValue()));
          ((SQLitePreparedStatement)localObject3).bindInteger(2, Math.max(0, j - localObject5[1].intValue()));
          ((SQLitePreparedStatement)localObject3).bindLong(3, l2);
          ((SQLitePreparedStatement)localObject3).step();
          ((SQLitePreparedStatement)localObject3).dispose();
          paramInt++;
        }
        localObject1 = TextUtils.join(",", paramArrayList);
        break;
        label692:
        if ((((TLRPC.Message)localObject5).media instanceof TLRPC.TL_messageMediaDocument))
        {
          localObject3 = FileLoader.getPathToAttach(((TLRPC.Message)localObject5).media.document);
          if ((localObject3 != null) && (((File)localObject3).toString().length() > 0)) {
            ((ArrayList)localObject2).add(localObject3);
          }
          localObject5 = FileLoader.getPathToAttach(((TLRPC.Message)localObject5).media.document.thumb);
          if ((localObject5 != null) && (((File)localObject5).toString().length() > 0)) {
            ((ArrayList)localObject2).add(localObject5);
          }
        }
      }
      label783:
      this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
      this.database.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
      this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
      this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
      this.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
      DataQuery.getInstance(this.currentAccount).clearBotKeyboard(0L, paramArrayList);
      paramArrayList = localArrayList;
    }
    catch (Exception paramArrayList)
    {
      for (;;)
      {
        FileLog.e(paramArrayList);
        paramArrayList = null;
      }
    }
    return paramArrayList;
  }
  
  private void markMessagesAsReadInternal(SparseLongArray paramSparseLongArray1, SparseLongArray paramSparseLongArray2, SparseIntArray paramSparseIntArray)
  {
    try
    {
      int i;
      int j;
      long l;
      if (!isEmpty(paramSparseLongArray1)) {
        for (i = 0; i < paramSparseLongArray1.size(); i++)
        {
          j = paramSparseLongArray1.keyAt(i);
          l = paramSparseLongArray1.get(j);
          this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", new Object[] { Integer.valueOf(j), Long.valueOf(l) })).stepThis().dispose();
        }
      }
      if (!isEmpty(paramSparseLongArray2)) {
        for (i = 0; i < paramSparseLongArray2.size(); i++)
        {
          j = paramSparseLongArray2.keyAt(i);
          l = paramSparseLongArray2.get(j);
          this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", new Object[] { Integer.valueOf(j), Long.valueOf(l) })).stepThis().dispose();
        }
      }
      if ((paramSparseIntArray != null) && (!isEmpty(paramSparseIntArray))) {
        for (i = 0; i < paramSparseIntArray.size(); i++)
        {
          l = paramSparseIntArray.keyAt(i);
          j = paramSparseIntArray.valueAt(i);
          paramSparseLongArray1 = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
          paramSparseLongArray1.requery();
          paramSparseLongArray1.bindLong(1, l << 32);
          paramSparseLongArray1.bindInteger(2, j);
          paramSparseLongArray1.step();
          paramSparseLongArray1.dispose();
        }
      }
      return;
    }
    catch (Exception paramSparseLongArray1)
    {
      FileLog.e(paramSparseLongArray1);
    }
  }
  
  private void putChatsInternal(ArrayList<TLRPC.Chat> paramArrayList)
    throws Exception
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      SQLitePreparedStatement localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
      int i = 0;
      Object localObject1;
      if (i < paramArrayList.size())
      {
        localObject1 = (TLRPC.Chat)paramArrayList.get(i);
        Object localObject2 = localObject1;
        SQLiteCursor localSQLiteCursor;
        if (((TLRPC.Chat)localObject1).min)
        {
          localSQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[] { Integer.valueOf(((TLRPC.Chat)localObject1).id) }), new Object[0]);
          localObject2 = localObject1;
          if (!localSQLiteCursor.next()) {}
        }
        for (;;)
        {
          try
          {
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
            localObject2 = localObject1;
            if (localNativeByteBuffer != null)
            {
              localChat = TLRPC.Chat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              localObject2 = localObject1;
              if (localChat != null)
              {
                localChat.title = ((TLRPC.Chat)localObject1).title;
                localChat.photo = ((TLRPC.Chat)localObject1).photo;
                localChat.broadcast = ((TLRPC.Chat)localObject1).broadcast;
                localChat.verified = ((TLRPC.Chat)localObject1).verified;
                localChat.megagroup = ((TLRPC.Chat)localObject1).megagroup;
                localChat.democracy = ((TLRPC.Chat)localObject1).democracy;
                if (((TLRPC.Chat)localObject1).username == null) {
                  continue;
                }
                localChat.username = ((TLRPC.Chat)localObject1).username;
                localChat.flags |= 0x40;
                localObject2 = localChat;
              }
            }
          }
          catch (Exception localException)
          {
            TLRPC.Chat localChat;
            FileLog.e(localException);
            Object localObject3 = localObject1;
            continue;
            localSQLitePreparedStatement.bindString(2, "");
            continue;
          }
          localSQLiteCursor.dispose();
          localSQLitePreparedStatement.requery();
          localObject1 = new NativeByteBuffer(((TLRPC.Chat)localObject2).getObjectSize());
          ((TLRPC.Chat)localObject2).serializeToStream((AbstractSerializedData)localObject1);
          localSQLitePreparedStatement.bindInteger(1, ((TLRPC.Chat)localObject2).id);
          if (((TLRPC.Chat)localObject2).title == null) {
            continue;
          }
          localSQLitePreparedStatement.bindString(2, ((TLRPC.Chat)localObject2).title.toLowerCase());
          localSQLitePreparedStatement.bindByteBuffer(3, (NativeByteBuffer)localObject1);
          localSQLitePreparedStatement.step();
          ((NativeByteBuffer)localObject1).reuse();
          i++;
          break;
          localChat.username = null;
          localChat.flags &= 0xFFFFFFBF;
        }
      }
      localSQLitePreparedStatement.dispose();
    }
  }
  
  private void putDialogsInternal(TLRPC.messages_Dialogs parammessages_Dialogs, boolean paramBoolean)
  {
    LongSparseArray localLongSparseArray;
    Object localObject1;
    SQLitePreparedStatement localSQLitePreparedStatement1;
    SQLitePreparedStatement localSQLitePreparedStatement2;
    SQLitePreparedStatement localSQLitePreparedStatement3;
    SQLitePreparedStatement localSQLitePreparedStatement4;
    SQLitePreparedStatement localSQLitePreparedStatement5;
    TLRPC.TL_dialog localTL_dialog;
    Object localObject2;
    Object localObject3;
    try
    {
      this.database.beginTransaction();
      localLongSparseArray = new android/util/LongSparseArray;
      localLongSparseArray.<init>(parammessages_Dialogs.messages.size());
      for (int i = 0; i < parammessages_Dialogs.messages.size(); i++)
      {
        localObject1 = (TLRPC.Message)parammessages_Dialogs.messages.get(i);
        localLongSparseArray.put(MessageObject.getDialogId((TLRPC.Message)localObject1), localObject1);
      }
      if (!parammessages_Dialogs.dialogs.isEmpty())
      {
        localSQLitePreparedStatement1 = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
        localSQLitePreparedStatement2 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        localSQLitePreparedStatement3 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
        localObject1 = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
        localSQLitePreparedStatement4 = this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
        localSQLitePreparedStatement5 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        i = 0;
        if (i < parammessages_Dialogs.dialogs.size())
        {
          localTL_dialog = (TLRPC.TL_dialog)parammessages_Dialogs.dialogs.get(i);
          if (localTL_dialog.id == 0L)
          {
            if (localTL_dialog.peer.user_id == 0) {
              break label287;
            }
            localTL_dialog.id = localTL_dialog.peer.user_id;
          }
          for (;;)
          {
            if (paramBoolean)
            {
              localObject2 = this.database;
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              localObject2 = ((SQLiteDatabase)localObject2).queryFinalized("SELECT did FROM dialogs WHERE did = " + localTL_dialog.id, new Object[0]);
              boolean bool = ((SQLiteCursor)localObject2).next();
              ((SQLiteCursor)localObject2).dispose();
              if (bool)
              {
                label281:
                i++;
                break;
                label287:
                if (localTL_dialog.peer.chat_id != 0)
                {
                  localTL_dialog.id = (-localTL_dialog.peer.chat_id);
                  continue;
                }
              }
            }
          }
        }
      }
    }
    catch (Exception parammessages_Dialogs)
    {
      FileLog.e(parammessages_Dialogs);
    }
    for (;;)
    {
      localTL_dialog.id = (-localTL_dialog.peer.channel_id);
      break;
      int j = 0;
      localObject2 = (TLRPC.Message)localLongSparseArray.get(localTL_dialog.id);
      long l1;
      long l2;
      if (localObject2 != null)
      {
        int k = Math.max(((TLRPC.Message)localObject2).date, 0);
        if (isValidKeyboardToSave((TLRPC.Message)localObject2)) {
          DataQuery.getInstance(this.currentAccount).putBotKeyboard(localTL_dialog.id, (TLRPC.Message)localObject2);
        }
        fixUnsupportedMedia((TLRPC.Message)localObject2);
        localObject3 = new org/telegram/tgnet/NativeByteBuffer;
        ((NativeByteBuffer)localObject3).<init>(((TLRPC.Message)localObject2).getObjectSize());
        ((TLRPC.Message)localObject2).serializeToStream((AbstractSerializedData)localObject3);
        l1 = ((TLRPC.Message)localObject2).id;
        l2 = l1;
        if (((TLRPC.Message)localObject2).to_id.channel_id != 0) {
          l2 = l1 | ((TLRPC.Message)localObject2).to_id.channel_id << 32;
        }
        localSQLitePreparedStatement1.requery();
        localSQLitePreparedStatement1.bindLong(1, l2);
        localSQLitePreparedStatement1.bindLong(2, localTL_dialog.id);
        localSQLitePreparedStatement1.bindInteger(3, MessageObject.getUnreadFlags((TLRPC.Message)localObject2));
        localSQLitePreparedStatement1.bindInteger(4, ((TLRPC.Message)localObject2).send_state);
        localSQLitePreparedStatement1.bindInteger(5, ((TLRPC.Message)localObject2).date);
        localSQLitePreparedStatement1.bindByteBuffer(6, (NativeByteBuffer)localObject3);
        if (MessageObject.isOut((TLRPC.Message)localObject2))
        {
          j = 1;
          label544:
          localSQLitePreparedStatement1.bindInteger(7, j);
          localSQLitePreparedStatement1.bindInteger(8, 0);
          if ((((TLRPC.Message)localObject2).flags & 0x400) == 0) {
            break label942;
          }
          j = ((TLRPC.Message)localObject2).views;
          label580:
          localSQLitePreparedStatement1.bindInteger(9, j);
          localSQLitePreparedStatement1.bindInteger(10, 0);
          if (!((TLRPC.Message)localObject2).mentioned) {
            break label948;
          }
          j = 1;
          label608:
          localSQLitePreparedStatement1.bindInteger(11, j);
          localSQLitePreparedStatement1.step();
          if (DataQuery.canAddMessageToMedia((TLRPC.Message)localObject2))
          {
            localSQLitePreparedStatement3.requery();
            localSQLitePreparedStatement3.bindLong(1, l2);
            localSQLitePreparedStatement3.bindLong(2, localTL_dialog.id);
            localSQLitePreparedStatement3.bindInteger(3, ((TLRPC.Message)localObject2).date);
            localSQLitePreparedStatement3.bindInteger(4, DataQuery.getMediaType((TLRPC.Message)localObject2));
            localSQLitePreparedStatement3.bindByteBuffer(5, (NativeByteBuffer)localObject3);
            localSQLitePreparedStatement3.step();
          }
          ((NativeByteBuffer)localObject3).reuse();
          createFirstHoles(localTL_dialog.id, localSQLitePreparedStatement4, localSQLitePreparedStatement5, ((TLRPC.Message)localObject2).id);
          j = k;
        }
      }
      else
      {
        l1 = localTL_dialog.top_message;
        l2 = l1;
        if (localTL_dialog.peer.channel_id != 0) {
          l2 = l1 | localTL_dialog.peer.channel_id << 32;
        }
        localSQLitePreparedStatement2.requery();
        localSQLitePreparedStatement2.bindLong(1, localTL_dialog.id);
        localSQLitePreparedStatement2.bindInteger(2, j);
        localSQLitePreparedStatement2.bindInteger(3, localTL_dialog.unread_count);
        localSQLitePreparedStatement2.bindLong(4, l2);
        localSQLitePreparedStatement2.bindInteger(5, localTL_dialog.read_inbox_max_id);
        localSQLitePreparedStatement2.bindInteger(6, localTL_dialog.read_outbox_max_id);
        localSQLitePreparedStatement2.bindLong(7, 0L);
        localSQLitePreparedStatement2.bindInteger(8, localTL_dialog.unread_mentions_count);
        localSQLitePreparedStatement2.bindInteger(9, localTL_dialog.pts);
        localSQLitePreparedStatement2.bindInteger(10, 0);
        localSQLitePreparedStatement2.bindInteger(11, localTL_dialog.pinnedNum);
        localSQLitePreparedStatement2.step();
        if (localTL_dialog.notify_settings == null) {
          break label281;
        }
        ((SQLitePreparedStatement)localObject1).requery();
        ((SQLitePreparedStatement)localObject1).bindLong(1, localTL_dialog.id);
        if (localTL_dialog.notify_settings.mute_until == 0) {
          break label954;
        }
      }
      label942:
      label948:
      label954:
      for (j = 1;; j = 0)
      {
        ((SQLitePreparedStatement)localObject1).bindInteger(2, j);
        ((SQLitePreparedStatement)localObject1).step();
        break;
        j = 0;
        break label544;
        j = 0;
        break label580;
        j = 0;
        break label608;
      }
      localSQLitePreparedStatement1.dispose();
      localSQLitePreparedStatement2.dispose();
      localSQLitePreparedStatement3.dispose();
      ((SQLitePreparedStatement)localObject1).dispose();
      localSQLitePreparedStatement4.dispose();
      localSQLitePreparedStatement5.dispose();
      putUsersInternal(parammessages_Dialogs.users);
      putChatsInternal(parammessages_Dialogs.chats);
      this.database.commitTransaction();
    }
  }
  
  private void putMessagesInternal(ArrayList<TLRPC.Message> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, int paramInt, boolean paramBoolean3)
  {
    if (paramBoolean3) {}
    for (;;)
    {
      try
      {
        localObject1 = (TLRPC.Message)paramArrayList.get(0);
        if (((TLRPC.Message)localObject1).dialog_id == 0L)
        {
          if (((TLRPC.Message)localObject1).to_id.user_id != 0) {
            ((TLRPC.Message)localObject1).dialog_id = ((TLRPC.Message)localObject1).to_id.user_id;
          }
        }
        else
        {
          i = -1;
          localObject2 = this.database;
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          localObject1 = ((SQLiteDatabase)localObject2).queryFinalized("SELECT last_mid FROM dialogs WHERE did = " + ((TLRPC.Message)localObject1).dialog_id, new Object[0]);
          if (((SQLiteCursor)localObject1).next()) {
            i = ((SQLiteCursor)localObject1).intValue(0);
          }
          ((SQLiteCursor)localObject1).dispose();
          if (i == 0) {
            break label181;
          }
          return;
        }
        if (((TLRPC.Message)localObject1).to_id.chat_id != 0)
        {
          ((TLRPC.Message)localObject1).dialog_id = (-((TLRPC.Message)localObject1).to_id.chat_id);
          continue;
        }
      }
      catch (Exception paramArrayList)
      {
        FileLog.e(paramArrayList);
        continue;
        ((TLRPC.Message)localObject1).dialog_id = (-((TLRPC.Message)localObject1).to_id.channel_id);
        continue;
      }
      label181:
      if (paramBoolean1) {
        this.database.beginTransaction();
      }
      LongSparseArray localLongSparseArray1 = new android/util/LongSparseArray;
      localLongSparseArray1.<init>();
      LongSparseArray localLongSparseArray2 = new android/util/LongSparseArray;
      localLongSparseArray2.<init>();
      LongSparseArray localLongSparseArray3 = new android/util/LongSparseArray;
      localLongSparseArray3.<init>();
      Object localObject4 = null;
      LongSparseArray localLongSparseArray4 = new android/util/LongSparseArray;
      localLongSparseArray4.<init>();
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3 = null;
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      LongSparseArray localLongSparseArray5 = new android/util/LongSparseArray;
      localLongSparseArray5.<init>();
      LongSparseArray localLongSparseArray6 = new android/util/LongSparseArray;
      localLongSparseArray6.<init>();
      LongSparseArray localLongSparseArray7 = new android/util/LongSparseArray;
      localLongSparseArray7.<init>();
      SQLitePreparedStatement localSQLitePreparedStatement1 = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
      Object localObject5 = null;
      SQLitePreparedStatement localSQLitePreparedStatement2 = this.database.executeFast("REPLACE INTO randoms VALUES(?, ?)");
      SQLitePreparedStatement localSQLitePreparedStatement3 = this.database.executeFast("REPLACE INTO download_queue VALUES(?, ?, ?, ?)");
      SQLitePreparedStatement localSQLitePreparedStatement4 = this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
      int i = 0;
      long l1;
      label401:
      long l2;
      Object localObject6;
      Object localObject8;
      if (i < paramArrayList.size())
      {
        TLRPC.Message localMessage = (TLRPC.Message)paramArrayList.get(i);
        l1 = localMessage.id;
        if (localMessage.dialog_id == 0L)
        {
          if (localMessage.to_id.user_id != 0) {
            localMessage.dialog_id = localMessage.to_id.user_id;
          }
        }
        else
        {
          l2 = l1;
          if (localMessage.to_id.channel_id != 0) {
            l2 = l1 | localMessage.to_id.channel_id << 32;
          }
          if ((localMessage.mentioned) && (localMessage.media_unread)) {
            localLongSparseArray7.put(l2, Long.valueOf(localMessage.dialog_id));
          }
          if ((!(localMessage.action instanceof TLRPC.TL_messageActionHistoryClear)) && (!MessageObject.isOut(localMessage)) && ((localMessage.id > 0) || (MessageObject.isUnread(localMessage))))
          {
            localObject6 = (Integer)localLongSparseArray5.get(localMessage.dialog_id);
            localObject7 = localObject6;
            if (localObject6 == null)
            {
              localObject6 = this.database;
              localObject7 = new java/lang/StringBuilder;
              ((StringBuilder)localObject7).<init>();
              localObject6 = ((SQLiteDatabase)localObject6).queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + localMessage.dialog_id, new Object[0]);
              if (!((SQLiteCursor)localObject6).next()) {
                break label911;
              }
            }
          }
        }
        label911:
        for (localObject7 = Integer.valueOf(((SQLiteCursor)localObject6).intValue(0));; localObject7 = Integer.valueOf(0))
        {
          ((SQLiteCursor)localObject6).dispose();
          localLongSparseArray5.put(localMessage.dialog_id, localObject7);
          if ((localMessage.id < 0) || (((Integer)localObject7).intValue() < localMessage.id))
          {
            if (localStringBuilder.length() > 0) {
              localStringBuilder.append(",");
            }
            localStringBuilder.append(l2);
            localLongSparseArray6.put(l2, Long.valueOf(localMessage.dialog_id));
          }
          localObject7 = localObject3;
          localObject8 = localObject2;
          localObject6 = localObject1;
          if (DataQuery.canAddMessageToMedia(localMessage))
          {
            localObject7 = localObject3;
            localObject3 = localObject2;
            if (localObject2 == null)
            {
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              localObject1 = new android/util/LongSparseArray;
              ((LongSparseArray)localObject1).<init>();
              localObject7 = new android/util/LongSparseArray;
              ((LongSparseArray)localObject7).<init>();
            }
            if (((StringBuilder)localObject3).length() > 0) {
              ((StringBuilder)localObject3).append(",");
            }
            ((StringBuilder)localObject3).append(l2);
            ((LongSparseArray)localObject1).put(l2, Long.valueOf(localMessage.dialog_id));
            ((LongSparseArray)localObject7).put(l2, Integer.valueOf(DataQuery.getMediaType(localMessage)));
            localObject6 = localObject1;
            localObject8 = localObject3;
          }
          if (isValidKeyboardToSave(localMessage))
          {
            localObject1 = (TLRPC.Message)localLongSparseArray4.get(localMessage.dialog_id);
            if ((localObject1 == null) || (((TLRPC.Message)localObject1).id < localMessage.id)) {
              localLongSparseArray4.put(localMessage.dialog_id, localMessage);
            }
          }
          i++;
          localObject3 = localObject7;
          localObject2 = localObject8;
          localObject1 = localObject6;
          break;
          if (localMessage.to_id.chat_id != 0)
          {
            localMessage.dialog_id = (-localMessage.to_id.chat_id);
            break label401;
          }
          localMessage.dialog_id = (-localMessage.to_id.channel_id);
          break label401;
        }
      }
      for (i = 0; i < localLongSparseArray4.size(); i++) {
        DataQuery.getInstance(this.currentAccount).putBotKeyboard(localLongSparseArray4.keyAt(i), (TLRPC.Message)localLongSparseArray4.valueAt(i));
      }
      Object localObject7 = localObject4;
      if (localObject2 != null)
      {
        localObject7 = this.database;
        localObject6 = new java/lang/StringBuilder;
        ((StringBuilder)localObject6).<init>();
        localObject2 = ((SQLiteDatabase)localObject7).queryFinalized("SELECT mid FROM media_v2 WHERE mid IN(" + ((StringBuilder)localObject2).toString() + ")", new Object[0]);
        while (((SQLiteCursor)localObject2).next()) {
          ((LongSparseArray)localObject1).remove(((SQLiteCursor)localObject2).longValue(0));
        }
        ((SQLiteCursor)localObject2).dispose();
        localObject8 = new android/util/SparseArray;
        ((SparseArray)localObject8).<init>();
        i = 0;
        localObject7 = localObject8;
        if (i < ((LongSparseArray)localObject1).size())
        {
          l2 = ((LongSparseArray)localObject1).keyAt(i);
          l1 = ((Long)((LongSparseArray)localObject1).valueAt(i)).longValue();
          localObject6 = (Integer)((LongSparseArray)localObject3).get(l2);
          localObject7 = (LongSparseArray)((SparseArray)localObject8).get(((Integer)localObject6).intValue());
          if (localObject7 == null)
          {
            localObject7 = new android/util/LongSparseArray;
            ((LongSparseArray)localObject7).<init>();
            localObject2 = Integer.valueOf(0);
            ((SparseArray)localObject8).put(((Integer)localObject6).intValue(), localObject7);
          }
          for (;;)
          {
            localObject6 = localObject2;
            if (localObject2 == null) {
              localObject6 = Integer.valueOf(0);
            }
            ((LongSparseArray)localObject7).put(l1, Integer.valueOf(((Integer)localObject6).intValue() + 1));
            i++;
            break;
            localObject2 = (Integer)((LongSparseArray)localObject7).get(l1);
          }
        }
      }
      if (localStringBuilder.length() > 0)
      {
        localObject3 = this.database;
        localObject1 = new java/lang/StringBuilder;
        ((StringBuilder)localObject1).<init>();
        localObject1 = ((SQLiteDatabase)localObject3).queryFinalized("SELECT mid FROM messages WHERE mid IN(" + localStringBuilder.toString() + ")", new Object[0]);
        while (((SQLiteCursor)localObject1).next())
        {
          l2 = ((SQLiteCursor)localObject1).longValue(0);
          localLongSparseArray6.remove(l2);
          localLongSparseArray7.remove(l2);
        }
        ((SQLiteCursor)localObject1).dispose();
        for (i = 0; i < localLongSparseArray6.size(); i++)
        {
          l2 = ((Long)localLongSparseArray6.valueAt(i)).longValue();
          localObject3 = (Integer)localLongSparseArray2.get(l2);
          localObject1 = localObject3;
          if (localObject3 == null) {
            localObject1 = Integer.valueOf(0);
          }
          localLongSparseArray2.put(l2, Integer.valueOf(((Integer)localObject1).intValue() + 1));
        }
        for (i = 0; i < localLongSparseArray7.size(); i++)
        {
          l2 = ((Long)localLongSparseArray7.valueAt(i)).longValue();
          localObject3 = (Integer)localLongSparseArray3.get(l2);
          localObject1 = localObject3;
          if (localObject3 == null) {
            localObject1 = Integer.valueOf(0);
          }
          localLongSparseArray3.put(l2, Integer.valueOf(((Integer)localObject1).intValue() + 1));
        }
      }
      int j = 0;
      int k = 0;
      localObject1 = localObject5;
      int m;
      if (k < paramArrayList.size())
      {
        localObject6 = (TLRPC.Message)paramArrayList.get(k);
        fixUnsupportedMedia((TLRPC.Message)localObject6);
        localSQLitePreparedStatement1.requery();
        l2 = ((TLRPC.Message)localObject6).id;
        if (((TLRPC.Message)localObject6).local_id != 0) {
          l2 = ((TLRPC.Message)localObject6).local_id;
        }
        l1 = l2;
        if (((TLRPC.Message)localObject6).to_id.channel_id != 0) {
          l1 = l2 | ((TLRPC.Message)localObject6).to_id.channel_id << 32;
        }
        localObject2 = new org/telegram/tgnet/NativeByteBuffer;
        ((NativeByteBuffer)localObject2).<init>(((TLRPC.Message)localObject6).getObjectSize());
        ((TLRPC.Message)localObject6).serializeToStream((AbstractSerializedData)localObject2);
        m = 1;
        i = m;
        if (((TLRPC.Message)localObject6).action != null)
        {
          i = m;
          if ((((TLRPC.Message)localObject6).action instanceof TLRPC.TL_messageEncryptedAction))
          {
            i = m;
            if (!(((TLRPC.Message)localObject6).action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))
            {
              i = m;
              if (!(((TLRPC.Message)localObject6).action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) {
                i = 0;
              }
            }
          }
        }
        if (i != 0)
        {
          localObject3 = (TLRPC.Message)localLongSparseArray1.get(((TLRPC.Message)localObject6).dialog_id);
          if ((localObject3 == null) || (((TLRPC.Message)localObject6).date > ((TLRPC.Message)localObject3).date) || ((((TLRPC.Message)localObject6).id > 0) && (((TLRPC.Message)localObject3).id > 0) && (((TLRPC.Message)localObject6).id > ((TLRPC.Message)localObject3).id)) || ((((TLRPC.Message)localObject6).id < 0) && (((TLRPC.Message)localObject3).id < 0) && (((TLRPC.Message)localObject6).id < ((TLRPC.Message)localObject3).id))) {
            localLongSparseArray1.put(((TLRPC.Message)localObject6).dialog_id, localObject6);
          }
        }
        localSQLitePreparedStatement1.bindLong(1, l1);
        localSQLitePreparedStatement1.bindLong(2, ((TLRPC.Message)localObject6).dialog_id);
        localSQLitePreparedStatement1.bindInteger(3, MessageObject.getUnreadFlags((TLRPC.Message)localObject6));
        localSQLitePreparedStatement1.bindInteger(4, ((TLRPC.Message)localObject6).send_state);
        localSQLitePreparedStatement1.bindInteger(5, ((TLRPC.Message)localObject6).date);
        localSQLitePreparedStatement1.bindByteBuffer(6, (NativeByteBuffer)localObject2);
        if (MessageObject.isOut((TLRPC.Message)localObject6))
        {
          i = 1;
          label1840:
          localSQLitePreparedStatement1.bindInteger(7, i);
          localSQLitePreparedStatement1.bindInteger(8, ((TLRPC.Message)localObject6).ttl);
          if ((((TLRPC.Message)localObject6).flags & 0x400) == 0) {
            break label2420;
          }
          localSQLitePreparedStatement1.bindInteger(9, ((TLRPC.Message)localObject6).views);
          label1885:
          localSQLitePreparedStatement1.bindInteger(10, 0);
          if (!((TLRPC.Message)localObject6).mentioned) {
            break label2436;
          }
          i = 1;
          label1904:
          localSQLitePreparedStatement1.bindInteger(11, i);
          localSQLitePreparedStatement1.step();
          if (((TLRPC.Message)localObject6).random_id != 0L)
          {
            localSQLitePreparedStatement2.requery();
            localSQLitePreparedStatement2.bindLong(1, ((TLRPC.Message)localObject6).random_id);
            localSQLitePreparedStatement2.bindLong(2, l1);
            localSQLitePreparedStatement2.step();
          }
          localObject3 = localObject1;
          if (DataQuery.canAddMessageToMedia((TLRPC.Message)localObject6))
          {
            localObject3 = localObject1;
            if (localObject1 == null) {
              localObject3 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
            }
            ((SQLitePreparedStatement)localObject3).requery();
            ((SQLitePreparedStatement)localObject3).bindLong(1, l1);
            ((SQLitePreparedStatement)localObject3).bindLong(2, ((TLRPC.Message)localObject6).dialog_id);
            ((SQLitePreparedStatement)localObject3).bindInteger(3, ((TLRPC.Message)localObject6).date);
            ((SQLitePreparedStatement)localObject3).bindInteger(4, DataQuery.getMediaType((TLRPC.Message)localObject6));
            ((SQLitePreparedStatement)localObject3).bindByteBuffer(5, (NativeByteBuffer)localObject2);
            ((SQLitePreparedStatement)localObject3).step();
          }
          if ((((TLRPC.Message)localObject6).media instanceof TLRPC.TL_messageMediaWebPage))
          {
            localSQLitePreparedStatement4.requery();
            localSQLitePreparedStatement4.bindLong(1, ((TLRPC.Message)localObject6).media.webpage.id);
            localSQLitePreparedStatement4.bindLong(2, l1);
            localSQLitePreparedStatement4.step();
          }
          ((NativeByteBuffer)localObject2).reuse();
          m = j;
          if (paramInt != 0) {
            if (((TLRPC.Message)localObject6).to_id.channel_id != 0)
            {
              m = j;
              if (!((TLRPC.Message)localObject6).post) {}
            }
            else
            {
              m = j;
              if (((TLRPC.Message)localObject6).date >= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - 3600)
              {
                m = j;
                if (DownloadController.getInstance(this.currentAccount).canDownloadMedia((TLRPC.Message)localObject6)) {
                  if (!(((TLRPC.Message)localObject6).media instanceof TLRPC.TL_messageMediaPhoto))
                  {
                    m = j;
                    if (!(((TLRPC.Message)localObject6).media instanceof TLRPC.TL_messageMediaDocument)) {}
                  }
                  else
                  {
                    m = 0;
                    l1 = 0L;
                    localObject2 = null;
                    if (!MessageObject.isVoiceMessage((TLRPC.Message)localObject6)) {
                      break label2442;
                    }
                    l2 = ((TLRPC.Message)localObject6).media.document.id;
                    i = 2;
                    localObject1 = new org/telegram/tgnet/TLRPC$TL_messageMediaDocument;
                    ((TLRPC.TL_messageMediaDocument)localObject1).<init>();
                    ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject6).media.document;
                    ((TLRPC.MessageMedia)localObject1).flags |= 0x1;
                  }
                }
              }
            }
          }
        }
        for (;;)
        {
          m = j;
          if (localObject1 != null)
          {
            if (((TLRPC.Message)localObject6).media.ttl_seconds != 0)
            {
              ((TLRPC.MessageMedia)localObject1).ttl_seconds = ((TLRPC.Message)localObject6).media.ttl_seconds;
              ((TLRPC.MessageMedia)localObject1).flags |= 0x4;
            }
            m = j | i;
            localSQLitePreparedStatement3.requery();
            localObject2 = new org/telegram/tgnet/NativeByteBuffer;
            ((NativeByteBuffer)localObject2).<init>(((TLRPC.MessageMedia)localObject1).getObjectSize());
            ((TLRPC.MessageMedia)localObject1).serializeToStream((AbstractSerializedData)localObject2);
            localSQLitePreparedStatement3.bindLong(1, l2);
            localSQLitePreparedStatement3.bindInteger(2, i);
            localSQLitePreparedStatement3.bindInteger(3, ((TLRPC.Message)localObject6).date);
            localSQLitePreparedStatement3.bindByteBuffer(4, (NativeByteBuffer)localObject2);
            localSQLitePreparedStatement3.step();
            ((NativeByteBuffer)localObject2).reuse();
          }
          k++;
          j = m;
          localObject1 = localObject3;
          break;
          i = 0;
          break label1840;
          label2420:
          localSQLitePreparedStatement1.bindInteger(9, getMessageMediaType((TLRPC.Message)localObject6));
          break label1885;
          label2436:
          i = 0;
          break label1904;
          label2442:
          if (MessageObject.isRoundVideoMessage((TLRPC.Message)localObject6))
          {
            l2 = ((TLRPC.Message)localObject6).media.document.id;
            i = 64;
            localObject1 = new org/telegram/tgnet/TLRPC$TL_messageMediaDocument;
            ((TLRPC.TL_messageMediaDocument)localObject1).<init>();
            ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject6).media.document;
            ((TLRPC.MessageMedia)localObject1).flags |= 0x1;
          }
          else if ((((TLRPC.Message)localObject6).media instanceof TLRPC.TL_messageMediaPhoto))
          {
            l2 = l1;
            localObject1 = localObject2;
            i = m;
            if (FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Message)localObject6).media.photo.sizes, AndroidUtilities.getPhotoSize()) != null)
            {
              l2 = ((TLRPC.Message)localObject6).media.photo.id;
              i = 1;
              localObject1 = new org/telegram/tgnet/TLRPC$TL_messageMediaPhoto;
              ((TLRPC.TL_messageMediaPhoto)localObject1).<init>();
              ((TLRPC.MessageMedia)localObject1).photo = ((TLRPC.Message)localObject6).media.photo;
              ((TLRPC.MessageMedia)localObject1).flags |= 0x1;
            }
          }
          else if (MessageObject.isVideoMessage((TLRPC.Message)localObject6))
          {
            l2 = ((TLRPC.Message)localObject6).media.document.id;
            i = 4;
            localObject1 = new org/telegram/tgnet/TLRPC$TL_messageMediaDocument;
            ((TLRPC.TL_messageMediaDocument)localObject1).<init>();
            ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject6).media.document;
            ((TLRPC.MessageMedia)localObject1).flags |= 0x1;
          }
          else
          {
            l2 = l1;
            localObject1 = localObject2;
            i = m;
            if ((((TLRPC.Message)localObject6).media instanceof TLRPC.TL_messageMediaDocument))
            {
              l2 = l1;
              localObject1 = localObject2;
              i = m;
              if (!MessageObject.isMusicMessage((TLRPC.Message)localObject6))
              {
                l2 = l1;
                localObject1 = localObject2;
                i = m;
                if (!MessageObject.isGifDocument(((TLRPC.Message)localObject6).media.document))
                {
                  l2 = ((TLRPC.Message)localObject6).media.document.id;
                  i = 8;
                  localObject1 = new org/telegram/tgnet/TLRPC$TL_messageMediaDocument;
                  ((TLRPC.TL_messageMediaDocument)localObject1).<init>();
                  ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject6).media.document;
                  ((TLRPC.MessageMedia)localObject1).flags |= 0x1;
                }
              }
            }
          }
        }
      }
      localSQLitePreparedStatement1.dispose();
      if (localObject1 != null) {
        ((SQLitePreparedStatement)localObject1).dispose();
      }
      localSQLitePreparedStatement2.dispose();
      localSQLitePreparedStatement3.dispose();
      localSQLitePreparedStatement4.dispose();
      localObject3 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
      i = 0;
      label2952:
      int i12;
      while (i < localLongSparseArray1.size())
      {
        long l3 = localLongSparseArray1.keyAt(i);
        if (l3 == 0L)
        {
          i++;
        }
        else
        {
          localObject2 = (TLRPC.Message)localLongSparseArray1.valueAt(i);
          k = 0;
          if (localObject2 != null) {
            k = ((TLRPC.Message)localObject2).to_id.channel_id;
          }
          paramArrayList = this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          paramArrayList = paramArrayList.queryFinalized("SELECT date, unread_count, pts, last_mid, inbox_max, outbox_max, pinned, unread_count_i FROM dialogs WHERE did = " + l3, new Object[0]);
          int n = 0;
          int i1 = 0;
          int i2 = 0;
          int i3;
          int i4;
          int i5;
          int i6;
          int i7;
          int i8;
          int i9;
          int i10;
          int i11;
          int i13;
          if (k != 0)
          {
            paramInt = 1;
            i3 = 0;
            i4 = 0;
            i5 = 0;
            i6 = 0;
            if (!paramArrayList.next()) {
              break label3278;
            }
            i7 = paramArrayList.intValue(0);
            i8 = paramArrayList.intValue(1);
            m = paramArrayList.intValue(2);
            i9 = paramArrayList.intValue(3);
            i10 = paramArrayList.intValue(4);
            i11 = paramArrayList.intValue(5);
            i12 = paramArrayList.intValue(6);
            i13 = paramArrayList.intValue(7);
            label3029:
            paramArrayList.dispose();
            localObject1 = (Integer)localLongSparseArray3.get(l3);
            paramArrayList = (Integer)localLongSparseArray2.get(l3);
            if (paramArrayList != null) {
              break label3362;
            }
            paramArrayList = Integer.valueOf(0);
            label3065:
            if (localObject1 != null) {
              break label3382;
            }
            localObject1 = Integer.valueOf(0);
            label3076:
            if (localObject2 == null) {
              break label3403;
            }
            l1 = ((TLRPC.Message)localObject2).id;
            label3089:
            l2 = l1;
            if (localObject2 != null)
            {
              l2 = l1;
              if (((TLRPC.Message)localObject2).local_id != 0) {
                l2 = ((TLRPC.Message)localObject2).local_id;
              }
            }
            l1 = l2;
            if (k != 0) {
              l1 = l2 | k << 32;
            }
            ((SQLitePreparedStatement)localObject3).requery();
            ((SQLitePreparedStatement)localObject3).bindLong(1, l3);
            if ((localObject2 == null) || ((paramBoolean2) && (i7 != 0))) {
              break label3411;
            }
            ((SQLitePreparedStatement)localObject3).bindInteger(2, ((TLRPC.Message)localObject2).date);
          }
          for (;;)
          {
            ((SQLitePreparedStatement)localObject3).bindInteger(3, paramArrayList.intValue() + i8);
            ((SQLitePreparedStatement)localObject3).bindLong(4, l1);
            ((SQLitePreparedStatement)localObject3).bindInteger(5, i10);
            ((SQLitePreparedStatement)localObject3).bindInteger(6, i11);
            ((SQLitePreparedStatement)localObject3).bindLong(7, 0L);
            ((SQLitePreparedStatement)localObject3).bindInteger(8, ((Integer)localObject1).intValue() + i13);
            ((SQLitePreparedStatement)localObject3).bindInteger(9, m);
            ((SQLitePreparedStatement)localObject3).bindInteger(10, 0);
            ((SQLitePreparedStatement)localObject3).bindInteger(11, i12);
            ((SQLitePreparedStatement)localObject3).step();
            break;
            paramInt = 0;
            break label2952;
            label3278:
            i7 = n;
            i10 = i3;
            i9 = i1;
            i13 = i6;
            i8 = i2;
            i11 = i4;
            i12 = i5;
            m = paramInt;
            if (k == 0) {
              break label3029;
            }
            MessagesController.getInstance(this.currentAccount).checkChannelInviter(k);
            i7 = n;
            i10 = i3;
            i9 = i1;
            i13 = i6;
            i8 = i2;
            i11 = i4;
            i12 = i5;
            m = paramInt;
            break label3029;
            label3362:
            localLongSparseArray2.put(l3, Integer.valueOf(paramArrayList.intValue() + i8));
            break label3065;
            label3382:
            localLongSparseArray3.put(l3, Integer.valueOf(((Integer)localObject1).intValue() + i13));
            break label3076;
            label3403:
            l1 = i9;
            break label3089;
            label3411:
            ((SQLitePreparedStatement)localObject3).bindInteger(2, i7);
          }
        }
      }
      ((SQLitePreparedStatement)localObject3).dispose();
      if (localObject7 != null)
      {
        localObject1 = this.database.executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
        for (paramInt = 0; paramInt < ((SparseArray)localObject7).size(); paramInt++)
        {
          m = ((SparseArray)localObject7).keyAt(paramInt);
          paramArrayList = (LongSparseArray)((SparseArray)localObject7).valueAt(paramInt);
          for (i = 0; i < paramArrayList.size(); i++)
          {
            l2 = paramArrayList.keyAt(i);
            k = (int)l2;
            k = -1;
            localObject3 = this.database.queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[] { Long.valueOf(l2), Integer.valueOf(m) }), new Object[0]);
            if (((SQLiteCursor)localObject3).next()) {
              k = ((SQLiteCursor)localObject3).intValue(0);
            }
            ((SQLiteCursor)localObject3).dispose();
            if (k != -1)
            {
              ((SQLitePreparedStatement)localObject1).requery();
              i12 = ((Integer)paramArrayList.valueAt(i)).intValue();
              ((SQLitePreparedStatement)localObject1).bindLong(1, l2);
              ((SQLitePreparedStatement)localObject1).bindInteger(2, m);
              ((SQLitePreparedStatement)localObject1).bindInteger(3, k + i12);
              ((SQLitePreparedStatement)localObject1).step();
            }
          }
        }
        ((SQLitePreparedStatement)localObject1).dispose();
      }
      if (paramBoolean1) {
        this.database.commitTransaction();
      }
      MessagesController.getInstance(this.currentAccount).processDialogsUpdateRead(localLongSparseArray2, localLongSparseArray3);
      if (j != 0)
      {
        paramArrayList = new org/telegram/messenger/MessagesStorage$77;
        paramArrayList.<init>(this, j);
        AndroidUtilities.runOnUIThread(paramArrayList);
      }
    }
  }
  
  private void putUsersAndChatsInternal(ArrayList<TLRPC.User> paramArrayList, ArrayList<TLRPC.Chat> paramArrayList1, boolean paramBoolean)
  {
    if (paramBoolean) {}
    try
    {
      this.database.beginTransaction();
      putUsersInternal(paramArrayList);
      putChatsInternal(paramArrayList1);
      if (paramBoolean) {
        this.database.commitTransaction();
      }
      return;
    }
    catch (Exception paramArrayList)
    {
      for (;;)
      {
        FileLog.e(paramArrayList);
      }
    }
  }
  
  private void putUsersInternal(ArrayList<TLRPC.User> paramArrayList)
    throws Exception
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      SQLitePreparedStatement localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
      int i = 0;
      Object localObject1;
      if (i < paramArrayList.size())
      {
        localObject1 = (TLRPC.User)paramArrayList.get(i);
        Object localObject2 = localObject1;
        SQLiteCursor localSQLiteCursor;
        if (((TLRPC.User)localObject1).min)
        {
          localSQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", new Object[] { Integer.valueOf(((TLRPC.User)localObject1).id) }), new Object[0]);
          localObject2 = localObject1;
          if (!localSQLiteCursor.next()) {}
        }
        for (;;)
        {
          try
          {
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
            localObject2 = localObject1;
            if (localNativeByteBuffer == null) {
              continue;
            }
            localUser = TLRPC.User.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            localObject2 = localObject1;
            if (localUser == null) {
              continue;
            }
            if (((TLRPC.User)localObject1).username == null) {
              continue;
            }
            localUser.username = ((TLRPC.User)localObject1).username;
            localUser.flags |= 0x8;
            if (((TLRPC.User)localObject1).photo == null) {
              continue;
            }
            localUser.photo = ((TLRPC.User)localObject1).photo;
            localUser.flags |= 0x20;
          }
          catch (Exception localException)
          {
            TLRPC.User localUser;
            FileLog.e(localException);
            Object localObject3 = localObject1;
            continue;
            localUser.photo = null;
            localUser.flags &= 0xFFFFFFDF;
            continue;
            if (!(((TLRPC.User)localObject3).status instanceof TLRPC.TL_userStatusLastWeek)) {
              continue;
            }
            ((TLRPC.User)localObject3).status.expires = -101;
            continue;
            if (!(((TLRPC.User)localObject3).status instanceof TLRPC.TL_userStatusLastMonth)) {
              continue;
            }
            ((TLRPC.User)localObject3).status.expires = -102;
            continue;
            localSQLitePreparedStatement.bindInteger(3, 0);
            continue;
          }
          localObject2 = localUser;
          localSQLiteCursor.dispose();
          localSQLitePreparedStatement.requery();
          localObject1 = new NativeByteBuffer(((TLRPC.User)localObject2).getObjectSize());
          ((TLRPC.User)localObject2).serializeToStream((AbstractSerializedData)localObject1);
          localSQLitePreparedStatement.bindInteger(1, ((TLRPC.User)localObject2).id);
          localSQLitePreparedStatement.bindString(2, formatUserSearchName((TLRPC.User)localObject2));
          if (((TLRPC.User)localObject2).status == null) {
            continue;
          }
          if (!(((TLRPC.User)localObject2).status instanceof TLRPC.TL_userStatusRecently)) {
            continue;
          }
          ((TLRPC.User)localObject2).status.expires = -100;
          localSQLitePreparedStatement.bindInteger(3, ((TLRPC.User)localObject2).status.expires);
          localSQLitePreparedStatement.bindByteBuffer(4, (NativeByteBuffer)localObject1);
          localSQLitePreparedStatement.step();
          ((NativeByteBuffer)localObject1).reuse();
          i++;
          break;
          localUser.username = null;
          localUser.flags &= 0xFFFFFFF7;
        }
      }
      localSQLitePreparedStatement.dispose();
    }
  }
  
  private void saveDiffParamsInternal(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    try
    {
      if ((this.lastSavedSeq == paramInt1) && (this.lastSavedPts == paramInt2) && (this.lastSavedDate == paramInt3) && (this.lastQtsValue == paramInt4)) {}
      for (;;)
      {
        return;
        SQLitePreparedStatement localSQLitePreparedStatement = this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
        localSQLitePreparedStatement.bindInteger(1, paramInt1);
        localSQLitePreparedStatement.bindInteger(2, paramInt2);
        localSQLitePreparedStatement.bindInteger(3, paramInt3);
        localSQLitePreparedStatement.bindInteger(4, paramInt4);
        localSQLitePreparedStatement.step();
        localSQLitePreparedStatement.dispose();
        this.lastSavedSeq = paramInt1;
        this.lastSavedPts = paramInt2;
        this.lastSavedDate = paramInt3;
        this.lastSavedQts = paramInt4;
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
  
  private void updateDbToLastVersion(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i;
        int j;
        SQLiteCursor localSQLiteCursor;
        int k;
        try
        {
          i = paramInt;
          j = i;
          if (i < 4)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
            MessagesStorage.this.fixNotificationSettings();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
            j = 4;
          }
          i = j;
          Object localObject1;
          Object localObject2;
          if (j == 4)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            MessagesStorage.this.database.beginTransaction();
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            localObject1 = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            if (localSQLiteCursor.next())
            {
              k = localSQLiteCursor.intValue(0);
              localObject2 = localSQLiteCursor.byteBufferValue(1);
              if (localObject2 != null)
              {
                j = ((NativeByteBuffer)localObject2).limit();
                for (i = 0; i < j / 4; i++)
                {
                  ((SQLitePreparedStatement)localObject1).requery();
                  ((SQLitePreparedStatement)localObject1).bindInteger(1, ((NativeByteBuffer)localObject2).readInt32(false));
                  ((SQLitePreparedStatement)localObject1).bindInteger(2, k);
                  ((SQLitePreparedStatement)localObject1).step();
                }
                ((NativeByteBuffer)localObject2).reuse();
              }
            }
            ((SQLitePreparedStatement)localObject1).dispose();
            localSQLiteCursor.dispose();
            MessagesStorage.this.database.commitTransaction();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            i = 6;
          }
          j = i;
          if (i == 6)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
            j = 7;
          }
          if ((j != 7) && (j != 8))
          {
            i = j;
            if (j != 9) {}
          }
          else
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
            i = 10;
          }
          j = i;
          if (i == 10)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
            j = 11;
          }
          if (j != 11)
          {
            i = j;
            if (j != 12) {}
          }
          else
          {
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
            i = 13;
          }
          j = i;
          if (i == 13)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            j = 14;
          }
          k = j;
          if (j == 14)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            k = 15;
          }
          i = k;
          if (k == 15)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
            i = 16;
          }
          j = i;
          if (i == 16)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
            j = 17;
          }
          i = j;
          if (j == 17)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
            i = 18;
          }
          j = i;
          if (i == 18)
          {
            MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
            j = 19;
          }
          k = j;
          if (j == 19)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            k = 20;
          }
          i = k;
          if (k == 20)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            i = 21;
          }
          k = i;
          if (i == 21)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
            localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
            while (localSQLiteCursor.next())
            {
              i = localSQLiteCursor.intValue(0);
              localObject1 = localSQLiteCursor.byteBufferValue(1);
              if (localObject1 != null)
              {
                Object localObject3 = TLRPC.ChatParticipants.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
                ((NativeByteBuffer)localObject1).reuse();
                if (localObject3 != null)
                {
                  localObject1 = new org/telegram/tgnet/TLRPC$TL_chatFull;
                  ((TLRPC.TL_chatFull)localObject1).<init>();
                  ((TLRPC.TL_chatFull)localObject1).id = i;
                  Object localObject4 = new org/telegram/tgnet/TLRPC$TL_photoEmpty;
                  ((TLRPC.TL_photoEmpty)localObject4).<init>();
                  ((TLRPC.TL_chatFull)localObject1).chat_photo = ((TLRPC.Photo)localObject4);
                  localObject4 = new org/telegram/tgnet/TLRPC$TL_peerNotifySettingsEmpty;
                  ((TLRPC.TL_peerNotifySettingsEmpty)localObject4).<init>();
                  ((TLRPC.TL_chatFull)localObject1).notify_settings = ((TLRPC.PeerNotifySettings)localObject4);
                  localObject4 = new org/telegram/tgnet/TLRPC$TL_chatInviteEmpty;
                  ((TLRPC.TL_chatInviteEmpty)localObject4).<init>();
                  ((TLRPC.TL_chatFull)localObject1).exported_invite = ((TLRPC.ExportedChatInvite)localObject4);
                  ((TLRPC.TL_chatFull)localObject1).participants = ((TLRPC.ChatParticipants)localObject3);
                  localObject3 = new org/telegram/tgnet/NativeByteBuffer;
                  ((NativeByteBuffer)localObject3).<init>(((TLRPC.TL_chatFull)localObject1).getObjectSize());
                  ((TLRPC.TL_chatFull)localObject1).serializeToStream((AbstractSerializedData)localObject3);
                  ((SQLitePreparedStatement)localObject2).requery();
                  ((SQLitePreparedStatement)localObject2).bindInteger(1, i);
                  ((SQLitePreparedStatement)localObject2).bindByteBuffer(2, (NativeByteBuffer)localObject3);
                  ((SQLitePreparedStatement)localObject2).step();
                  ((NativeByteBuffer)localObject3).reuse();
                  continue;
                  return;
                }
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          localException.dispose();
          localSQLiteCursor.dispose();
          MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
          k = 22;
          j = k;
          if (k == 22)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
            j = 23;
          }
          if (j != 23)
          {
            i = j;
            if (j != 24) {}
          }
          else
          {
            MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
            i = 25;
          }
          if (i != 25)
          {
            k = i;
            if (i != 26) {}
          }
          else
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
            k = 27;
          }
          j = k;
          if (k == 27)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
            j = 28;
          }
          if (j != 28)
          {
            i = j;
            if (j != 29) {}
          }
          else
          {
            MessagesStorage.this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
            i = 30;
          }
          k = i;
          if (i == 30)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
            k = 31;
          }
          j = k;
          if (k == 31)
          {
            MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
            j = 32;
          }
          i = j;
          if (j == 32)
          {
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
            i = 33;
          }
          j = i;
          if (i == 33)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
            j = 34;
          }
          i = j;
          if (j == 34)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
            i = 35;
          }
          j = i;
          if (i == 35)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
            j = 36;
          }
          k = j;
          if (j == 36)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
            k = 37;
          }
          i = k;
          if (k == 37)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
            i = 38;
          }
          k = i;
          if (i == 38)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
            k = 39;
          }
          j = k;
          if (k == 39)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
            j = 40;
          }
          i = j;
          if (j == 40)
          {
            MessagesStorage.this.fixNotificationSettings();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
            i = 41;
          }
          j = i;
          if (i == 41)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN mention INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE user_contacts_v6 ADD COLUMN imported INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 42").stepThis().dispose();
            j = 42;
          }
          i = j;
          if (j == 42)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 43").stepThis().dispose();
            i = 43;
          }
          j = i;
          if (i == 43)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_admins(did INTEGER, uid INTEGER, PRIMARY KEY(did, uid))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 44").stepThis().dispose();
            j = 44;
          }
          i = j;
          if (j == 44)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 45").stepThis().dispose();
            i = 45;
          }
          j = i;
          if (i == 45)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN mtproto_seq INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 46").stepThis().dispose();
            j = 46;
          }
          if (j == 46)
          {
            MessagesStorage.this.database.executeFast("DELETE FROM botcache WHERE 1").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
          }
        }
      }
    });
  }
  
  private void updateDialogsWithDeletedMessagesInternal(ArrayList<Integer> paramArrayList, ArrayList<Long> paramArrayList1, int paramInt)
  {
    if (Thread.currentThread().getId() != this.storageQueue.getId()) {
      throw new RuntimeException("wrong db thread");
    }
    ArrayList localArrayList1;
    int i;
    try
    {
      localArrayList1 = new java/util/ArrayList;
      localArrayList1.<init>();
      if (!paramArrayList.isEmpty())
      {
        if (paramInt != 0)
        {
          localArrayList1.add(Long.valueOf(-paramInt));
          paramArrayList = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ?)) WHERE did = ?");
          this.database.beginTransaction();
          for (i = 0; i < localArrayList1.size(); i++)
          {
            long l = ((Long)localArrayList1.get(i)).longValue();
            paramArrayList.requery();
            paramArrayList.bindLong(1, l);
            paramArrayList.bindLong(2, l);
            paramArrayList.bindLong(3, l);
            paramArrayList.step();
          }
        }
        paramArrayList = TextUtils.join(",", paramArrayList);
        paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s)", new Object[] { paramArrayList }), new Object[0]);
        while (paramArrayList.next())
        {
          localArrayList1.add(Long.valueOf(paramArrayList.longValue(0)));
          continue;
          return;
        }
      }
    }
    catch (Exception paramArrayList)
    {
      FileLog.e(paramArrayList);
    }
    for (;;)
    {
      paramArrayList.dispose();
      paramArrayList = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? AND date != 0)) WHERE did = ?");
      break;
      paramArrayList.dispose();
      this.database.commitTransaction();
      while (paramArrayList1 != null)
      {
        for (i = 0; i < paramArrayList1.size(); i++)
        {
          paramArrayList = (Long)paramArrayList1.get(i);
          if (!localArrayList1.contains(paramArrayList)) {
            localArrayList1.add(paramArrayList);
          }
        }
        localArrayList1.add(Long.valueOf(-paramInt));
      }
      Object localObject = TextUtils.join(",", localArrayList1);
      TLRPC.TL_messages_dialogs localTL_messages_dialogs = new org/telegram/tgnet/TLRPC$TL_messages_dialogs;
      localTL_messages_dialogs.<init>();
      localArrayList1 = new java/util/ArrayList;
      localArrayList1.<init>();
      paramArrayList1 = new java/util/ArrayList;
      paramArrayList1.<init>();
      paramArrayList = new java/util/ArrayList;
      paramArrayList.<init>();
      ArrayList localArrayList2 = new java/util/ArrayList;
      localArrayList2.<init>();
      localObject = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max, d.pinned, d.unread_count_i FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid WHERE d.did IN(%s)", new Object[] { localObject }), new Object[0]);
      while (((SQLiteCursor)localObject).next())
      {
        TLRPC.TL_dialog localTL_dialog = new org/telegram/tgnet/TLRPC$TL_dialog;
        localTL_dialog.<init>();
        localTL_dialog.id = ((SQLiteCursor)localObject).longValue(0);
        localTL_dialog.top_message = ((SQLiteCursor)localObject).intValue(1);
        localTL_dialog.read_inbox_max_id = ((SQLiteCursor)localObject).intValue(10);
        localTL_dialog.read_outbox_max_id = ((SQLiteCursor)localObject).intValue(11);
        localTL_dialog.unread_count = ((SQLiteCursor)localObject).intValue(2);
        localTL_dialog.unread_mentions_count = ((SQLiteCursor)localObject).intValue(13);
        localTL_dialog.last_message_date = ((SQLiteCursor)localObject).intValue(3);
        localTL_dialog.pts = ((SQLiteCursor)localObject).intValue(9);
        if (paramInt == 0)
        {
          i = 0;
          label506:
          localTL_dialog.flags = i;
          localTL_dialog.pinnedNum = ((SQLiteCursor)localObject).intValue(12);
          if (localTL_dialog.pinnedNum == 0) {
            break label748;
          }
        }
        int j;
        label748:
        for (boolean bool = true;; bool = false)
        {
          localTL_dialog.pinned = bool;
          localTL_messages_dialogs.dialogs.add(localTL_dialog);
          NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject).byteBufferValue(4);
          if (localNativeByteBuffer != null)
          {
            TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localMessage.readAttachPath(localNativeByteBuffer, UserConfig.getInstance(this.currentAccount).clientUserId);
            localNativeByteBuffer.reuse();
            MessageObject.setUnreadFlags(localMessage, ((SQLiteCursor)localObject).intValue(5));
            localMessage.id = ((SQLiteCursor)localObject).intValue(6);
            localMessage.send_state = ((SQLiteCursor)localObject).intValue(7);
            i = ((SQLiteCursor)localObject).intValue(8);
            if (i != 0) {
              localTL_dialog.last_message_date = i;
            }
            localMessage.dialog_id = localTL_dialog.id;
            localTL_messages_dialogs.messages.add(localMessage);
            addUsersAndChatsFromMessage(localMessage, paramArrayList1, paramArrayList);
          }
          j = (int)localTL_dialog.id;
          i = (int)(localTL_dialog.id >> 32);
          if (j == 0) {
            break label811;
          }
          if (i != 1) {
            break label754;
          }
          if (paramArrayList.contains(Integer.valueOf(j))) {
            break;
          }
          paramArrayList.add(Integer.valueOf(j));
          break;
          i = 1;
          break label506;
        }
        label754:
        if (j > 0)
        {
          if (!paramArrayList1.contains(Integer.valueOf(j))) {
            paramArrayList1.add(Integer.valueOf(j));
          }
        }
        else if (!paramArrayList.contains(Integer.valueOf(-j)))
        {
          paramArrayList.add(Integer.valueOf(-j));
          continue;
          label811:
          if (!localArrayList2.contains(Integer.valueOf(i))) {
            localArrayList2.add(Integer.valueOf(i));
          }
        }
      }
      ((SQLiteCursor)localObject).dispose();
      if (!localArrayList2.isEmpty()) {
        getEncryptedChatsInternal(TextUtils.join(",", localArrayList2), localArrayList1, paramArrayList1);
      }
      if (!paramArrayList.isEmpty()) {
        getChatsInternal(TextUtils.join(",", paramArrayList), localTL_messages_dialogs.chats);
      }
      if (!paramArrayList1.isEmpty()) {
        getUsersInternal(TextUtils.join(",", paramArrayList1), localTL_messages_dialogs.users);
      }
      if ((!localTL_messages_dialogs.dialogs.isEmpty()) || (!localArrayList1.isEmpty())) {
        MessagesController.getInstance(this.currentAccount).processDialogsUpdate(localTL_messages_dialogs, localArrayList1);
      }
    }
  }
  
  private void updateDialogsWithReadMessagesInternal(ArrayList<Integer> paramArrayList, SparseLongArray paramSparseLongArray1, SparseLongArray paramSparseLongArray2, ArrayList<Long> paramArrayList1)
  {
    LongSparseArray localLongSparseArray1;
    LongSparseArray localLongSparseArray2;
    ArrayList localArrayList;
    long l;
    try
    {
      localLongSparseArray1 = new android/util/LongSparseArray;
      localLongSparseArray1.<init>();
      localLongSparseArray2 = new android/util/LongSparseArray;
      localLongSparseArray2.<init>();
      localArrayList = new java/util/ArrayList;
      localArrayList.<init>();
      if (!isEmpty(paramArrayList))
      {
        paramArrayList = TextUtils.join(",", paramArrayList);
        paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out FROM messages WHERE mid IN(%s)", new Object[] { paramArrayList }), new Object[0]);
        for (;;)
        {
          if (paramArrayList.next()) {
            if ((paramArrayList.intValue(2) == 0) && (paramArrayList.intValue(1) == 0))
            {
              l = paramArrayList.longValue(0);
              paramSparseLongArray1 = (Integer)localLongSparseArray1.get(l);
              if (paramSparseLongArray1 == null)
              {
                localLongSparseArray1.put(l, Integer.valueOf(1));
                continue;
              }
            }
          }
        }
      }
    }
    catch (Exception paramArrayList)
    {
      FileLog.e(paramArrayList);
    }
    for (;;)
    {
      localLongSparseArray1.put(l, Integer.valueOf(paramSparseLongArray1.intValue() + 1));
      break;
      paramArrayList.dispose();
      while ((localLongSparseArray1.size() > 0) || (localLongSparseArray2.size() > 0))
      {
        this.database.beginTransaction();
        int i;
        if (localLongSparseArray1.size() > 0)
        {
          paramArrayList = this.database.executeFast("UPDATE dialogs SET unread_count = ? WHERE did = ?");
          i = 0;
          for (;;)
          {
            if (i < localLongSparseArray1.size())
            {
              paramArrayList.requery();
              paramArrayList.bindInteger(1, ((Integer)localLongSparseArray1.valueAt(i)).intValue());
              paramArrayList.bindLong(2, localLongSparseArray1.keyAt(i));
              paramArrayList.step();
              i++;
              continue;
              int j;
              if (!isEmpty(paramSparseLongArray1)) {
                for (i = 0; i < paramSparseLongArray1.size(); i++)
                {
                  j = paramSparseLongArray1.keyAt(i);
                  l = paramSparseLongArray1.get(j);
                  paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mid > %d AND read_state IN(0,2) AND out = 0", new Object[] { Integer.valueOf(j), Long.valueOf(l) }), new Object[0]);
                  if (paramArrayList.next()) {
                    localLongSparseArray1.put(j, Integer.valueOf(paramArrayList.intValue(0)));
                  }
                  paramArrayList.dispose();
                  paramArrayList = this.database.executeFast("UPDATE dialogs SET inbox_max = max((SELECT inbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                  paramArrayList.requery();
                  paramArrayList.bindLong(1, j);
                  paramArrayList.bindInteger(2, (int)l);
                  paramArrayList.bindLong(3, j);
                  paramArrayList.step();
                  paramArrayList.dispose();
                }
              }
              if (!isEmpty(paramArrayList1))
              {
                paramArrayList = new java/util/ArrayList;
                paramArrayList.<init>(paramArrayList1);
                paramSparseLongArray1 = TextUtils.join(",", paramArrayList1);
                paramSparseLongArray1 = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out, mention, mid FROM messages WHERE mid IN(%s)", new Object[] { paramSparseLongArray1 }), new Object[0]);
                while (paramSparseLongArray1.next())
                {
                  l = paramSparseLongArray1.longValue(0);
                  paramArrayList.remove(Long.valueOf(paramSparseLongArray1.longValue(4)));
                  if ((paramSparseLongArray1.intValue(1) < 2) && (paramSparseLongArray1.intValue(2) == 0) && (paramSparseLongArray1.intValue(3) == 1))
                  {
                    paramArrayList1 = (Integer)localLongSparseArray2.get(l);
                    if (paramArrayList1 == null)
                    {
                      paramArrayList1 = this.database;
                      StringBuilder localStringBuilder = new java/lang/StringBuilder;
                      localStringBuilder.<init>();
                      paramArrayList1 = paramArrayList1.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + l, new Object[0]);
                      i = 0;
                      if (paramArrayList1.next()) {
                        i = paramArrayList1.intValue(0);
                      }
                      paramArrayList1.dispose();
                      localLongSparseArray2.put(l, Integer.valueOf(Math.max(0, i - 1)));
                    }
                    else
                    {
                      localLongSparseArray2.put(l, Integer.valueOf(Math.max(0, paramArrayList1.intValue() - 1)));
                    }
                  }
                }
                paramSparseLongArray1.dispose();
                for (i = 0; i < paramArrayList.size(); i++)
                {
                  j = (int)(((Long)paramArrayList.get(i)).longValue() >> 32);
                  if ((j > 0) && (!localArrayList.contains(Integer.valueOf(j)))) {
                    localArrayList.add(Integer.valueOf(j));
                  }
                }
              }
              if (isEmpty(paramSparseLongArray2)) {
                break;
              }
              for (i = 0; i < paramSparseLongArray2.size(); i++)
              {
                j = paramSparseLongArray2.keyAt(i);
                l = paramSparseLongArray2.get(j);
                paramArrayList = this.database.executeFast("UPDATE dialogs SET outbox_max = max((SELECT outbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                paramArrayList.requery();
                paramArrayList.bindLong(1, j);
                paramArrayList.bindInteger(2, (int)l);
                paramArrayList.bindLong(3, j);
                paramArrayList.step();
                paramArrayList.dispose();
              }
              break;
            }
          }
          paramArrayList.dispose();
        }
        if (localLongSparseArray2.size() > 0)
        {
          paramArrayList = this.database.executeFast("UPDATE dialogs SET unread_count_i = ? WHERE did = ?");
          for (i = 0; i < localLongSparseArray2.size(); i++)
          {
            paramArrayList.requery();
            paramArrayList.bindInteger(1, ((Integer)localLongSparseArray2.valueAt(i)).intValue());
            paramArrayList.bindLong(2, localLongSparseArray2.keyAt(i));
            paramArrayList.step();
          }
          paramArrayList.dispose();
        }
        this.database.commitTransaction();
      }
      MessagesController.getInstance(this.currentAccount).processDialogsUpdateRead(localLongSparseArray1, localLongSparseArray2);
      if (!localArrayList.isEmpty()) {
        MessagesController.getInstance(this.currentAccount).reloadMentionsCountForChannels(localArrayList);
      }
    }
  }
  
  private long[] updateMessageStateAndIdInternal(long paramLong, Integer paramInteger, int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Integer localInteger = null;
    long l1 = paramInt1;
    Object localObject8 = paramInteger;
    if (paramInteger == null)
    {
      localObject2 = localInteger;
      try
      {
        localObject8 = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id = %d LIMIT 1", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
        localInteger = paramInteger;
        localObject2 = localObject8;
        localObject1 = localObject8;
        if (((SQLiteCursor)localObject8).next())
        {
          localObject2 = localObject8;
          localObject1 = localObject8;
          int i = ((SQLiteCursor)localObject8).intValue(0);
          localInteger = Integer.valueOf(i);
        }
        localObject9 = localObject8;
        localObject1 = localInteger;
        if (localObject8 != null)
        {
          ((SQLiteCursor)localObject8).dispose();
          localObject1 = localInteger;
          localObject9 = localObject8;
        }
      }
      catch (Exception localException6)
      {
        for (;;)
        {
          localObject1 = localObject2;
          FileLog.e(localException6);
          Object localObject9 = localObject2;
          localObject1 = paramInteger;
          if (localObject2 != null)
          {
            ((SQLiteCursor)localObject2).dispose();
            localObject9 = localObject2;
            localObject1 = paramInteger;
          }
        }
      }
      finally
      {
        if (localObject1 == null) {
          break label192;
        }
        ((SQLiteCursor)localObject1).dispose();
      }
      localObject2 = localObject9;
      localObject8 = localObject1;
      if (localObject1 == null) {
        paramInteger = null;
      }
    }
    for (;;)
    {
      return paramInteger;
      label192:
      paramLong = localException6.intValue();
      long l2 = l1;
      l3 = paramLong;
      if (paramInt3 != 0)
      {
        l3 = paramLong | paramInt3 << 32;
        l2 = l1 | paramInt3 << 32;
      }
      l1 = 0L;
      paramInteger = (Integer)localObject2;
      long l4;
      label374:
      try
      {
        localObject1 = this.database.queryFinalized(String.format(Locale.US, "SELECT uid FROM messages WHERE mid = %d LIMIT 1", new Object[] { Long.valueOf(l3) }), new Object[0]);
        paramLong = l1;
        paramInteger = (Integer)localObject1;
        localObject2 = localObject1;
        if (((SQLiteCursor)localObject1).next())
        {
          paramInteger = (Integer)localObject1;
          localObject2 = localObject1;
          paramLong = ((SQLiteCursor)localObject1).longValue(0);
        }
        l4 = paramLong;
        if (localObject1 != null)
        {
          ((SQLiteCursor)localObject1).dispose();
          l4 = paramLong;
        }
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          localObject2 = paramInteger;
          FileLog.e(localException1);
          l4 = l1;
          if (paramInteger != null)
          {
            paramInteger.dispose();
            l4 = l1;
          }
        }
      }
      finally
      {
        if (localObject2 == null) {
          break label374;
        }
        ((SQLiteCursor)localObject2).dispose();
      }
      if (l4 == 0L)
      {
        paramInteger = null;
      }
      else if ((l3 == l2) && (paramInt2 != 0))
      {
        paramInteger = null;
        localObject2 = null;
        try
        {
          localSQLitePreparedStatement1 = this.database.executeFast("UPDATE messages SET send_state = 0, date = ? WHERE mid = ?");
          localObject2 = localSQLitePreparedStatement1;
          paramInteger = localSQLitePreparedStatement1;
          localSQLitePreparedStatement1.bindInteger(1, paramInt2);
          localObject2 = localSQLitePreparedStatement1;
          paramInteger = localSQLitePreparedStatement1;
          localSQLitePreparedStatement1.bindLong(2, l2);
          localObject2 = localSQLitePreparedStatement1;
          paramInteger = localSQLitePreparedStatement1;
          localSQLitePreparedStatement1.step();
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            SQLitePreparedStatement localSQLitePreparedStatement1;
            paramInteger = (Integer)localObject2;
            FileLog.e(localException2);
            if (localObject2 != null) {
              ((SQLitePreparedStatement)localObject2).dispose();
            }
          }
        }
        finally
        {
          if (paramInteger == null) {
            break label510;
          }
          paramInteger.dispose();
        }
        paramInteger = new long[2];
        paramInteger[0] = l4;
        paramInteger[1] = paramInt1;
      }
      else
      {
        label510:
        paramInteger = null;
        localObject4 = null;
      }
      try
      {
        localSQLitePreparedStatement2 = this.database.executeFast("UPDATE messages SET mid = ?, send_state = 0 WHERE mid = ?");
        localObject4 = localSQLitePreparedStatement2;
        paramInteger = localSQLitePreparedStatement2;
        localSQLitePreparedStatement2.bindLong(1, l2);
        localObject4 = localSQLitePreparedStatement2;
        paramInteger = localSQLitePreparedStatement2;
        localSQLitePreparedStatement2.bindLong(2, l3);
        localObject4 = localSQLitePreparedStatement2;
        paramInteger = localSQLitePreparedStatement2;
        localSQLitePreparedStatement2.step();
        paramInteger = localSQLitePreparedStatement2;
        if (localSQLitePreparedStatement2 != null)
        {
          localSQLitePreparedStatement2.dispose();
          paramInteger = null;
        }
      }
      catch (Exception paramInteger)
      {
        for (;;)
        {
          SQLitePreparedStatement localSQLitePreparedStatement2;
          paramInteger = (Integer)localObject4;
          try
          {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l3) })).stepThis().dispose();
            paramInteger = (Integer)localObject4;
            this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid = %d", new Object[] { Long.valueOf(l3) })).stepThis().dispose();
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              paramInteger = (Integer)localObject4;
              FileLog.e(localException3);
            }
          }
          paramInteger = (Integer)localObject4;
          if (localObject4 != null)
          {
            ((SQLitePreparedStatement)localObject4).dispose();
            paramInteger = null;
          }
        }
      }
      finally
      {
        if (paramInteger == null) {
          break label866;
        }
        paramInteger.dispose();
      }
      localObject4 = paramInteger;
      try
      {
        localSQLitePreparedStatement2 = this.database.executeFast("UPDATE media_v2 SET mid = ? WHERE mid = ?");
        localObject4 = localSQLitePreparedStatement2;
        paramInteger = localSQLitePreparedStatement2;
        localSQLitePreparedStatement2.bindLong(1, l2);
        localObject4 = localSQLitePreparedStatement2;
        paramInteger = localSQLitePreparedStatement2;
        localSQLitePreparedStatement2.bindLong(2, l3);
        localObject4 = localSQLitePreparedStatement2;
        paramInteger = localSQLitePreparedStatement2;
        localSQLitePreparedStatement2.step();
        paramInteger = localSQLitePreparedStatement2;
        if (localSQLitePreparedStatement2 != null)
        {
          localSQLitePreparedStatement2.dispose();
          paramInteger = null;
        }
      }
      catch (Exception paramInteger)
      {
        for (;;)
        {
          paramInteger = (Integer)localObject5;
          try
          {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid = %d", new Object[] { Long.valueOf(l3) })).stepThis().dispose();
            paramInteger = (Integer)localObject5;
            if (localObject5 == null) {
              continue;
            }
            ((SQLitePreparedStatement)localObject5).dispose();
            paramInteger = null;
          }
          catch (Exception localException4)
          {
            for (;;)
            {
              paramInteger = (Integer)localObject5;
              FileLog.e(localException4);
            }
          }
        }
      }
      finally
      {
        if (paramInteger == null) {
          break label948;
        }
        paramInteger.dispose();
      }
      localObject4 = paramInteger;
      try
      {
        localSQLitePreparedStatement2 = this.database.executeFast("UPDATE dialogs SET last_mid = ? WHERE last_mid = ?");
        localObject4 = localSQLitePreparedStatement2;
        paramInteger = localSQLitePreparedStatement2;
        localSQLitePreparedStatement2.bindLong(1, l2);
        localObject4 = localSQLitePreparedStatement2;
        paramInteger = localSQLitePreparedStatement2;
        localSQLitePreparedStatement2.bindLong(2, l3);
        localObject4 = localSQLitePreparedStatement2;
        paramInteger = localSQLitePreparedStatement2;
        localSQLitePreparedStatement2.step();
      }
      catch (Exception localException5)
      {
        for (;;)
        {
          paramInteger = (Integer)localObject6;
          FileLog.e(localException5);
          if (localObject6 != null) {
            ((SQLitePreparedStatement)localObject6).dispose();
          }
        }
      }
      finally
      {
        if (paramInteger == null) {
          break label984;
        }
        paramInteger.dispose();
      }
      paramInteger = new long[2];
      paramInteger[0] = l4;
      paramInteger[1] = localException6.intValue();
    }
  }
  
  private void updateUsersInternal(ArrayList<TLRPC.User> paramArrayList, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (Thread.currentThread().getId() != this.storageQueue.getId()) {
      throw new RuntimeException("wrong db thread");
    }
    Object localObject1;
    Object localObject2;
    if (paramBoolean1)
    {
      if (paramBoolean2) {}
      try
      {
        this.database.beginTransaction();
        localObject1 = this.database.executeFast("UPDATE users SET status = ? WHERE uid = ?");
        localObject2 = paramArrayList.iterator();
        for (;;)
        {
          if (((Iterator)localObject2).hasNext())
          {
            paramArrayList = (TLRPC.User)((Iterator)localObject2).next();
            ((SQLitePreparedStatement)localObject1).requery();
            if (paramArrayList.status != null)
            {
              ((SQLitePreparedStatement)localObject1).bindInteger(1, paramArrayList.status.expires);
              ((SQLitePreparedStatement)localObject1).bindInteger(2, paramArrayList.id);
              ((SQLitePreparedStatement)localObject1).step();
              continue;
            }
          }
        }
      }
      catch (Exception paramArrayList)
      {
        FileLog.e(paramArrayList);
      }
    }
    for (;;)
    {
      ((SQLitePreparedStatement)localObject1).bindInteger(1, 0);
      break;
      ((SQLitePreparedStatement)localObject1).dispose();
      if (paramBoolean2)
      {
        this.database.commitTransaction();
        continue;
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        localObject1 = new android/util/SparseArray;
        ((SparseArray)localObject1).<init>();
        paramArrayList = paramArrayList.iterator();
        TLRPC.User localUser1;
        while (paramArrayList.hasNext())
        {
          localUser1 = (TLRPC.User)paramArrayList.next();
          if (((StringBuilder)localObject2).length() != 0) {
            ((StringBuilder)localObject2).append(",");
          }
          ((StringBuilder)localObject2).append(localUser1.id);
          ((SparseArray)localObject1).put(localUser1.id, localUser1);
        }
        paramArrayList = new java/util/ArrayList;
        paramArrayList.<init>();
        getUsersInternal(((StringBuilder)localObject2).toString(), paramArrayList);
        localObject2 = paramArrayList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          TLRPC.User localUser2 = (TLRPC.User)((Iterator)localObject2).next();
          localUser1 = (TLRPC.User)((SparseArray)localObject1).get(localUser2.id);
          if (localUser1 != null) {
            if ((localUser1.first_name != null) && (localUser1.last_name != null))
            {
              if (!UserObject.isContact(localUser2))
              {
                localUser2.first_name = localUser1.first_name;
                localUser2.last_name = localUser1.last_name;
              }
              localUser2.username = localUser1.username;
            }
            else if (localUser1.photo != null)
            {
              localUser2.photo = localUser1.photo;
            }
            else if (localUser1.phone != null)
            {
              localUser2.phone = localUser1.phone;
            }
          }
        }
        if (!paramArrayList.isEmpty())
        {
          if (paramBoolean2) {
            this.database.beginTransaction();
          }
          putUsersInternal(paramArrayList);
          if (paramBoolean2) {
            this.database.commitTransaction();
          }
        }
      }
    }
  }
  
  public void addRecentLocalFile(final String paramString1, final String paramString2, final TLRPC.Document paramDocument)
  {
    if ((paramString1 == null) || (paramString1.length() == 0) || (((paramString2 == null) || (paramString2.length() == 0)) && (paramDocument == null))) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            SQLitePreparedStatement localSQLitePreparedStatement;
            if (paramDocument != null)
            {
              localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
              localSQLitePreparedStatement.requery();
              NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
              localNativeByteBuffer.<init>(paramDocument.getObjectSize());
              paramDocument.serializeToStream(localNativeByteBuffer);
              localSQLitePreparedStatement.bindByteBuffer(1, localNativeByteBuffer);
              localSQLitePreparedStatement.bindString(2, paramString1);
              localSQLitePreparedStatement.step();
              localSQLitePreparedStatement.dispose();
              localNativeByteBuffer.reuse();
            }
            for (;;)
            {
              return;
              localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
              localSQLitePreparedStatement.requery();
              localSQLitePreparedStatement.bindString(1, paramString2);
              localSQLitePreparedStatement.bindString(2, paramString1);
              localSQLitePreparedStatement.step();
              localSQLitePreparedStatement.dispose();
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
      });
    }
  }
  
  public void applyPhoneBookUpdates(final String paramString1, final String paramString2)
  {
    if ((paramString1.length() == 0) && (paramString2.length() == 0)) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            if (paramString1.length() != 0) {
              MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 0 WHERE sphone IN(%s)", new Object[] { paramString1 })).stepThis().dispose();
            }
            if (paramString2.length() != 0) {
              MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v7 SET deleted = 1 WHERE sphone IN(%s)", new Object[] { paramString2 })).stepThis().dispose();
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public boolean checkMessageId(final long paramLong, int paramInt)
  {
    final boolean[] arrayOfBoolean = new boolean[1];
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        localObject1 = null;
        localObject2 = null;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d AND mid = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(arrayOfBoolean) }), new Object[0]);
          localObject2 = localSQLiteCursor;
          localObject1 = localSQLiteCursor;
          if (localSQLiteCursor.next())
          {
            localObject2 = localSQLiteCursor;
            localObject1 = localSQLiteCursor;
            localCountDownLatch[0] = true;
          }
          if (localSQLiteCursor != null) {
            localSQLiteCursor.dispose();
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localObject1 = localObject2;
            FileLog.e(localException);
            if (localObject2 != null) {
              ((SQLiteCursor)localObject2).dispose();
            }
          }
        }
        finally
        {
          if (localObject1 == null) {
            break label116;
          }
          ((SQLiteCursor)localObject1).dispose();
        }
        this.val$countDownLatch.countDown();
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public void cleanup(final boolean paramBoolean)
  {
    this.storageQueue.cleanupQueue();
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesStorage.this.cleanupInternal();
        MessagesStorage.this.openDatabase(false);
        if (paramBoolean) {
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              MessagesController.getInstance(MessagesStorage.this.currentAccount).getDifference();
            }
          });
        }
      }
    });
  }
  
  public void clearDownloadQueue(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          if (paramInt == 0) {
            MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
          }
          for (;;)
          {
            return;
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", new Object[] { Integer.valueOf(paramInt) })).stepThis().dispose();
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
    });
  }
  
  public void clearSentMedia()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void clearUserPhoto(final int paramInt, final long paramLong)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLiteDatabase localSQLiteDatabase = MessagesStorage.this.database;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localSQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + paramInt + " AND id = " + paramLong).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void clearUserPhotos(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLiteDatabase localSQLiteDatabase = MessagesStorage.this.database;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localSQLiteDatabase.executeFast("DELETE FROM user_photos WHERE uid = " + paramInt).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void clearWebRecent(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLiteDatabase localSQLiteDatabase = MessagesStorage.this.database;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localSQLiteDatabase.executeFast("DELETE FROM web_recent_v3 WHERE type = " + paramInt).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void closeHolesInMedia(long paramLong, int paramInt1, int paramInt2, int paramInt3)
    throws Exception
  {
    Object localObject1;
    Object localObject3;
    int j;
    if (paramInt3 < 0) {
      try
      {
        localObject1 = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), new Object[0]);
        Object localObject2 = null;
        while (((SQLiteCursor)localObject1).next())
        {
          localObject3 = localObject2;
          if (localObject2 == null)
          {
            localObject3 = new java/util/ArrayList;
            ((ArrayList)localObject3).<init>();
          }
          int i = ((SQLiteCursor)localObject1).intValue(0);
          paramInt3 = ((SQLiteCursor)localObject1).intValue(1);
          j = ((SQLiteCursor)localObject1).intValue(2);
          if (paramInt3 == j)
          {
            localObject2 = localObject3;
            if (paramInt3 == 1) {
              break;
            }
          }
          else
          {
            localObject2 = new org/telegram/messenger/MessagesStorage$Hole;
            ((Hole)localObject2).<init>(this, i, paramInt3, j);
            ((ArrayList)localObject3).add(localObject2);
            localObject2 = localObject3;
            continue;
            return;
          }
        }
      }
      catch (Exception localException3)
      {
        FileLog.e(localException3);
      }
    }
    do
    {
      localObject1 = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt3), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), new Object[0]);
      break;
      ((SQLiteCursor)localObject1).dispose();
    } while (localException3 == null);
    paramInt3 = 0;
    label335:
    if (paramInt3 < localException3.size())
    {
      localObject1 = (Hole)localException3.get(paramInt3);
      if ((paramInt2 < ((Hole)localObject1).end - 1) || (paramInt1 > ((Hole)localObject1).start + 1)) {
        break label452;
      }
      this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject1).type), Integer.valueOf(((Hole)localObject1).start), Integer.valueOf(((Hole)localObject1).end) })).stepThis().dispose();
    }
    for (;;)
    {
      paramInt3++;
      break label335;
      break;
      label452:
      if (paramInt2 >= ((Hole)localObject1).end - 1)
      {
        j = ((Hole)localObject1).end;
        if (j != paramInt1) {
          try
          {
            this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt1), Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject1).type), Integer.valueOf(((Hole)localObject1).start), Integer.valueOf(((Hole)localObject1).end) })).stepThis().dispose();
          }
          catch (Exception localException1)
          {
            FileLog.e(localException1);
          }
        }
      }
      else if (paramInt1 <= localException1.start + 1)
      {
        j = localException1.start;
        if (j != paramInt2) {
          try
          {
            this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt2), Long.valueOf(paramLong), Integer.valueOf(localException1.type), Integer.valueOf(localException1.start), Integer.valueOf(localException1.end) })).stepThis().dispose();
          }
          catch (Exception localException2)
          {
            FileLog.e(localException2);
          }
        }
      }
      else
      {
        this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(localException2.type), Integer.valueOf(localException2.start), Integer.valueOf(localException2.end) })).stepThis().dispose();
        localObject3 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        ((SQLitePreparedStatement)localObject3).requery();
        ((SQLitePreparedStatement)localObject3).bindLong(1, paramLong);
        ((SQLitePreparedStatement)localObject3).bindInteger(2, localException2.type);
        ((SQLitePreparedStatement)localObject3).bindInteger(3, localException2.start);
        ((SQLitePreparedStatement)localObject3).bindInteger(4, paramInt1);
        ((SQLitePreparedStatement)localObject3).step();
        ((SQLitePreparedStatement)localObject3).requery();
        ((SQLitePreparedStatement)localObject3).bindLong(1, paramLong);
        ((SQLitePreparedStatement)localObject3).bindInteger(2, localException2.type);
        ((SQLitePreparedStatement)localObject3).bindInteger(3, paramInt2);
        ((SQLitePreparedStatement)localObject3).bindInteger(4, localException2.end);
        ((SQLitePreparedStatement)localObject3).step();
        ((SQLitePreparedStatement)localObject3).dispose();
      }
    }
  }
  
  public long createPendingTask(NativeByteBuffer paramNativeByteBuffer)
  {
    final long l;
    if (paramNativeByteBuffer == null) {
      l = 0L;
    }
    for (;;)
    {
      return l;
      l = this.lastTaskId.getAndAdd(1L);
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
            localSQLitePreparedStatement.bindLong(1, l);
            localSQLitePreparedStatement.bindByteBuffer(2, this.val$data);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
              this.val$data.reuse();
            }
          }
          finally
          {
            this.val$data.reuse();
          }
        }
      });
    }
  }
  
  public void createTaskForMid(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final int paramInt5, final boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          int j;
          SparseArray localSparseArray;
          ArrayList localArrayList;
          long l2;
          Object localObject;
          if (paramInt3 > paramInt4)
          {
            i = paramInt3;
            j = i + paramInt5;
            localSparseArray = new android/util/SparseArray;
            localSparseArray.<init>();
            localArrayList = new java/util/ArrayList;
            localArrayList.<init>();
            long l1 = paramInt1;
            l2 = l1;
            if (paramInt2 != 0) {
              l2 = l1 | paramInt2 << 32;
            }
            localArrayList.add(Long.valueOf(l2));
            localSparseArray.put(j, localArrayList);
            localObject = new org/telegram/messenger/MessagesStorage$34$1;
            ((1)localObject).<init>(this, localArrayList);
            AndroidUtilities.runOnUIThread((Runnable)localObject);
            localObject = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
          }
          for (int i = 0;; i++)
          {
            if (i >= localSparseArray.size()) {
              break label221;
            }
            int k = localSparseArray.keyAt(i);
            localArrayList = (ArrayList)localSparseArray.get(k);
            int m = 0;
            for (;;)
            {
              if (m < localArrayList.size())
              {
                ((SQLitePreparedStatement)localObject).requery();
                ((SQLitePreparedStatement)localObject).bindLong(1, ((Long)localArrayList.get(m)).longValue());
                ((SQLitePreparedStatement)localObject).bindInteger(2, k);
                ((SQLitePreparedStatement)localObject).step();
                m++;
                continue;
                i = paramInt4;
                break;
              }
            }
          }
          label221:
          ((SQLitePreparedStatement)localObject).dispose();
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid = %d", new Object[] { Long.valueOf(l2) })).stepThis().dispose();
          MessagesController.getInstance(MessagesStorage.this.currentAccount).didAddedNewTask(j, localSparseArray);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void createTaskForSecretChat(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final ArrayList<Long> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = Integer.MAX_VALUE;
        SparseArray localSparseArray;
        ArrayList localArrayList1;
        StringBuilder localStringBuilder;
        int j;
        int k;
        label162:
        Object localObject2;
        try
        {
          localSparseArray = new android/util/SparseArray;
          localSparseArray.<init>();
          localArrayList1 = new java/util/ArrayList;
          localArrayList1.<init>();
          localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          if (paramArrayList == null)
          {
            SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE uid = %d AND out = %d AND read_state != 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", new Object[] { Long.valueOf(paramInt1 << 32), Integer.valueOf(paramInt4), Integer.valueOf(paramInt2) }), new Object[0]);
            for (;;)
            {
              if (localSQLiteCursor.next())
              {
                j = localSQLiteCursor.intValue(1);
                long l = localSQLiteCursor.intValue(0);
                if (paramArrayList != null) {
                  localArrayList1.add(Long.valueOf(l));
                }
                if (j > 0) {
                  if (paramInt2 > paramInt3)
                  {
                    k = paramInt2;
                    k += j;
                    i = Math.min(i, k);
                    ArrayList localArrayList2 = (ArrayList)localSparseArray.get(k);
                    localObject2 = localArrayList2;
                    if (localArrayList2 == null)
                    {
                      localObject2 = new java/util/ArrayList;
                      ((ArrayList)localObject2).<init>();
                      localSparseArray.put(k, localObject2);
                    }
                    if (localStringBuilder.length() != 0) {
                      localStringBuilder.append(",");
                    }
                    localStringBuilder.append(l);
                    ((ArrayList)localObject2).add(Long.valueOf(l));
                    continue;
                  }
                }
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          Object localObject1 = TextUtils.join(",", paramArrayList);
          localObject1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages as m INNER JOIN randoms as r ON m.mid = r.mid WHERE r.random_id IN (%s)", new Object[] { localObject1 }), new Object[0]);
          break;
          k = paramInt3;
          break label162;
          ((SQLiteCursor)localObject1).dispose();
          if (paramArrayList != null)
          {
            localObject1 = new org/telegram/messenger/MessagesStorage$35$1;
            ((1)localObject1).<init>(this, localArrayList1);
            AndroidUtilities.runOnUIThread((Runnable)localObject1);
          }
          if (localSparseArray.size() != 0)
          {
            MessagesStorage.this.database.beginTransaction();
            localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            for (k = 0; k < localSparseArray.size(); k++)
            {
              int m = localSparseArray.keyAt(k);
              localObject1 = (ArrayList)localSparseArray.get(m);
              for (j = 0; j < ((ArrayList)localObject1).size(); j++)
              {
                ((SQLitePreparedStatement)localObject2).requery();
                ((SQLitePreparedStatement)localObject2).bindLong(1, ((Long)((ArrayList)localObject1).get(j)).longValue());
                ((SQLitePreparedStatement)localObject2).bindInteger(2, m);
                ((SQLitePreparedStatement)localObject2).step();
              }
            }
            ((SQLitePreparedStatement)localObject2).dispose();
            MessagesStorage.this.database.commitTransaction();
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid IN(%s)", new Object[] { localStringBuilder.toString() })).stepThis().dispose();
            MessagesController.getInstance(MessagesStorage.this.currentAccount).didAddedNewTask(i, localSparseArray);
          }
        }
      }
    });
  }
  
  public void deleteBlockedUser(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLiteDatabase localSQLiteDatabase = MessagesStorage.this.database;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localSQLiteDatabase.executeFast("DELETE FROM blocked_users WHERE uid = " + paramInt).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void deleteContacts(final ArrayList<Integer> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            String str = TextUtils.join(",", paramArrayList);
            SQLiteDatabase localSQLiteDatabase = MessagesStorage.this.database;
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localSQLiteDatabase.executeFast("DELETE FROM contacts WHERE uid IN(" + str + ")").stepThis().dispose();
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void deleteDialog(final long paramLong, final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          try
          {
            if (paramInt == 3)
            {
              i = -1;
              localObject1 = MessagesStorage.this.database;
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              localObject1 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT last_mid FROM dialogs WHERE did = " + paramLong, new Object[0]);
              if (((SQLiteCursor)localObject1).next()) {
                i = ((SQLiteCursor)localObject1).intValue(0);
              }
              ((SQLiteCursor)localObject1).dispose();
              if (i != 0) {
                return;
              }
            }
          }
          catch (Exception localException1)
          {
            Object localObject1;
            FileLog.e(localException1);
            continue;
            if (!(localException2.media instanceof TLRPC.TL_messageMediaDocument)) {
              continue;
            }
            Object localObject4 = FileLoader.getPathToAttach(localException2.media.document);
            if ((localObject4 == null) || (((File)localObject4).toString().length() <= 0)) {
              continue;
            }
            ((ArrayList)localObject3).add(localObject4);
            Object localObject6 = FileLoader.getPathToAttach(localException2.media.document.thumb);
            if ((localObject6 == null) || (((File)localObject6).toString().length() <= 0)) {
              continue;
            }
            ((ArrayList)localObject3).add(localObject6);
            continue;
            if (i >= 0) {
              continue;
            }
            continue;
            Object localObject3 = MessagesStorage.this.database;
            Object localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            ((SQLiteDatabase)localObject3).executeFast("DELETE FROM enc_chats WHERE uid = " + j).stepThis().dispose();
            continue;
            if (paramInt != 2) {
              continue;
            }
            localObject2 = MessagesStorage.this.database;
            localObject3 = new java/lang/StringBuilder;
            ((StringBuilder)localObject3).<init>();
            localObject2 = ((SQLiteDatabase)localObject2).queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + paramLong, new Object[0]);
            int i = -1;
            if (!((SQLiteCursor)localObject2).next()) {
              continue;
            }
            long l1 = ((SQLiteCursor)localObject2).longValue(0);
            long l2 = ((SQLiteCursor)localObject2).longValue(1);
            localObject3 = MessagesStorage.this.database;
            localObject6 = new java/lang/StringBuilder;
            ((StringBuilder)localObject6).<init>();
            localObject3 = ((SQLiteDatabase)localObject3).queryFinalized("SELECT data FROM messages WHERE uid = " + paramLong + " AND mid IN (" + l1 + "," + l2 + ")", new Object[0]);
            int j = i;
            try
            {
              if (((SQLiteCursor)localObject3).next())
              {
                localObject6 = ((SQLiteCursor)localObject3).byteBufferValue(0);
                i = j;
                if (localObject6 == null) {
                  continue;
                }
                localObject4 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject6, ((NativeByteBuffer)localObject6).readInt32(false), false);
                ((TLRPC.Message)localObject4).readAttachPath((AbstractSerializedData)localObject6, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                ((NativeByteBuffer)localObject6).reuse();
                i = j;
                if (localObject4 == null) {
                  continue;
                }
                i = ((TLRPC.Message)localObject4).id;
              }
            }
            catch (Exception localException3)
            {
              FileLog.e(localException3);
              ((SQLiteCursor)localObject3).dispose();
              localObject3 = MessagesStorage.this.database;
              Object localObject7 = new java/lang/StringBuilder;
              ((StringBuilder)localObject7).<init>();
              ((SQLiteDatabase)localObject3).executeFast("DELETE FROM messages WHERE uid = " + paramLong + " AND mid != " + l1 + " AND mid != " + l2).stepThis().dispose();
              localObject7 = MessagesStorage.this.database;
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              ((SQLiteDatabase)localObject7).executeFast("DELETE FROM messages_holes WHERE uid = " + paramLong).stepThis().dispose();
              localObject3 = MessagesStorage.this.database;
              localObject7 = new java/lang/StringBuilder;
              ((StringBuilder)localObject7).<init>();
              ((SQLiteDatabase)localObject3).executeFast("DELETE FROM bot_keyboard WHERE uid = " + paramLong).stepThis().dispose();
              localObject3 = MessagesStorage.this.database;
              localObject7 = new java/lang/StringBuilder;
              ((StringBuilder)localObject7).<init>();
              ((SQLiteDatabase)localObject3).executeFast("DELETE FROM media_counts_v2 WHERE uid = " + paramLong).stepThis().dispose();
              localObject3 = MessagesStorage.this.database;
              localObject7 = new java/lang/StringBuilder;
              ((StringBuilder)localObject7).<init>();
              ((SQLiteDatabase)localObject3).executeFast("DELETE FROM media_v2 WHERE uid = " + paramLong).stepThis().dispose();
              localObject7 = MessagesStorage.this.database;
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>();
              ((SQLiteDatabase)localObject7).executeFast("DELETE FROM media_holes_v2 WHERE uid = " + paramLong).stepThis().dispose();
              DataQuery.getInstance(MessagesStorage.this.currentAccount).clearBotKeyboard(paramLong, null);
              localObject7 = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
              localObject3 = MessagesStorage.this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
              if (j != -1) {
                MessagesStorage.createFirstHoles(paramLong, (SQLitePreparedStatement)localObject7, (SQLitePreparedStatement)localObject3, j);
              }
              ((SQLitePreparedStatement)localObject7).dispose();
              ((SQLitePreparedStatement)localObject3).dispose();
            }
            ((SQLiteCursor)localObject2).dispose();
            continue;
          }
          if (((int)paramLong == 0) || (paramInt == 2))
          {
            localObject3 = MessagesStorage.this.database;
            localObject1 = new java/lang/StringBuilder;
            ((StringBuilder)localObject1).<init>();
            localObject1 = ((SQLiteDatabase)localObject3).queryFinalized("SELECT data FROM messages WHERE uid = " + paramLong, new Object[0]);
            localObject3 = new java/util/ArrayList;
            ((ArrayList)localObject3).<init>();
            try
            {
              if (((SQLiteCursor)localObject1).next())
              {
                localObject4 = ((SQLiteCursor)localObject1).byteBufferValue(0);
                if (localObject4 == null) {
                  continue;
                }
                Object localObject5 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
                ((TLRPC.Message)localObject5).readAttachPath((AbstractSerializedData)localObject4, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                ((NativeByteBuffer)localObject4).reuse();
                if ((localObject5 == null) || (((TLRPC.Message)localObject5).media == null)) {
                  continue;
                }
                if (!(((TLRPC.Message)localObject5).media instanceof TLRPC.TL_messageMediaPhoto)) {
                  continue;
                }
                localObject4 = ((TLRPC.Message)localObject5).media.photo.sizes.iterator();
                if (!((Iterator)localObject4).hasNext()) {
                  continue;
                }
                localObject5 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject4).next());
                if ((localObject5 == null) || (((File)localObject5).toString().length() <= 0)) {
                  continue;
                }
                ((ArrayList)localObject3).add(localObject5);
                continue;
              }
              if (paramInt == 0) {
                continue;
              }
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
              ((SQLiteCursor)localObject1).dispose();
              FileLoader.getInstance(MessagesStorage.this.currentAccount).deleteFiles((ArrayList)localObject3, paramInt);
            }
          }
          if (paramInt != 3) {
            continue;
          }
          localObject3 = MessagesStorage.this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((SQLiteDatabase)localObject3).executeFast("DELETE FROM dialogs WHERE did = " + paramLong).stepThis().dispose();
          localObject1 = MessagesStorage.this.database;
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((SQLiteDatabase)localObject1).executeFast("DELETE FROM chat_settings_v2 WHERE uid = " + paramLong).stepThis().dispose();
          localObject3 = MessagesStorage.this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((SQLiteDatabase)localObject3).executeFast("DELETE FROM chat_pinned WHERE uid = " + paramLong).stepThis().dispose();
          localObject3 = MessagesStorage.this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((SQLiteDatabase)localObject3).executeFast("DELETE FROM channel_users_v2 WHERE did = " + paramLong).stepThis().dispose();
          localObject3 = MessagesStorage.this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((SQLiteDatabase)localObject3).executeFast("DELETE FROM search_recent WHERE did = " + paramLong).stepThis().dispose();
          i = (int)paramLong;
          j = (int)(paramLong >> 32);
          if (i == 0) {
            continue;
          }
          if (j != 1) {
            continue;
          }
          localObject3 = MessagesStorage.this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((SQLiteDatabase)localObject3).executeFast("DELETE FROM chats WHERE uid = " + i).stepThis().dispose();
          localObject1 = MessagesStorage.this.database;
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((SQLiteDatabase)localObject1).executeFast("UPDATE dialogs SET unread_count = 0 WHERE did = " + paramLong).stepThis().dispose();
          localObject3 = MessagesStorage.this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((SQLiteDatabase)localObject3).executeFast("DELETE FROM messages WHERE uid = " + paramLong).stepThis().dispose();
          localObject3 = MessagesStorage.this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((SQLiteDatabase)localObject3).executeFast("DELETE FROM bot_keyboard WHERE uid = " + paramLong).stepThis().dispose();
          localObject3 = MessagesStorage.this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((SQLiteDatabase)localObject3).executeFast("DELETE FROM media_counts_v2 WHERE uid = " + paramLong).stepThis().dispose();
          localObject3 = MessagesStorage.this.database;
          localObject1 = new java/lang/StringBuilder;
          ((StringBuilder)localObject1).<init>();
          ((SQLiteDatabase)localObject3).executeFast("DELETE FROM media_v2 WHERE uid = " + paramLong).stepThis().dispose();
          localObject1 = MessagesStorage.this.database;
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((SQLiteDatabase)localObject1).executeFast("DELETE FROM messages_holes WHERE uid = " + paramLong).stepThis().dispose();
          localObject1 = MessagesStorage.this.database;
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((SQLiteDatabase)localObject1).executeFast("DELETE FROM media_holes_v2 WHERE uid = " + paramLong).stepThis().dispose();
          DataQuery.getInstance(MessagesStorage.this.currentAccount).clearBotKeyboard(paramLong, null);
          localObject1 = new org/telegram/messenger/MessagesStorage$23$1;
          ((1)localObject1).<init>(this);
          AndroidUtilities.runOnUIThread((Runnable)localObject1);
        }
      }
    });
  }
  
  public void deleteUserChannelHistory(final int paramInt1, final int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          try
          {
            long l = -paramInt1;
            ArrayList localArrayList = new java/util/ArrayList;
            localArrayList.<init>();
            localObject1 = MessagesStorage.this.database;
            Object localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            localObject2 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT data FROM messages WHERE uid = " + l, new Object[0]);
            localObject1 = new java/util/ArrayList;
            ((ArrayList)localObject1).<init>();
            try
            {
              if (((SQLiteCursor)localObject2).next())
              {
                localObject3 = ((SQLiteCursor)localObject2).byteBufferValue(0);
                if (localObject3 == null) {
                  continue;
                }
                Object localObject4 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                ((TLRPC.Message)localObject4).readAttachPath((AbstractSerializedData)localObject3, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                ((NativeByteBuffer)localObject3).reuse();
                if ((localObject4 == null) || (((TLRPC.Message)localObject4).from_id != paramInt2) || (((TLRPC.Message)localObject4).id == 1)) {
                  continue;
                }
                localArrayList.add(Integer.valueOf(((TLRPC.Message)localObject4).id));
                if ((((TLRPC.Message)localObject4).media instanceof TLRPC.TL_messageMediaPhoto))
                {
                  localObject4 = ((TLRPC.Message)localObject4).media.photo.sizes.iterator();
                  if (!((Iterator)localObject4).hasNext()) {
                    continue;
                  }
                  localObject3 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject4).next());
                  if ((localObject3 == null) || (((File)localObject3).toString().length() <= 0)) {
                    continue;
                  }
                  ((ArrayList)localObject1).add(localObject3);
                  continue;
                }
              }
              else
              {
                return;
              }
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
              ((SQLiteCursor)localObject2).dispose();
              localObject2 = new org/telegram/messenger/MessagesStorage$22$1;
              ((1)localObject2).<init>(this, localArrayList);
              AndroidUtilities.runOnUIThread((Runnable)localObject2);
              MessagesStorage.this.markMessagesAsDeletedInternal(localArrayList, paramInt1);
              MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(localArrayList, null, paramInt1);
              FileLoader.getInstance(MessagesStorage.this.currentAccount).deleteFiles((ArrayList)localObject1, 0);
              if (!localArrayList.isEmpty())
              {
                localObject1 = new org/telegram/messenger/MessagesStorage$22$2;
                ((2)localObject1).<init>(this, localArrayList);
                AndroidUtilities.runOnUIThread((Runnable)localObject1);
              }
            }
          }
          catch (Exception localException1)
          {
            Object localObject1;
            Object localObject3;
            File localFile;
            FileLog.e(localException1);
            continue;
          }
          if ((localException2.media instanceof TLRPC.TL_messageMediaDocument))
          {
            localObject3 = FileLoader.getPathToAttach(localException2.media.document);
            if ((localObject3 != null) && (((File)localObject3).toString().length() > 0)) {
              ((ArrayList)localObject1).add(localObject3);
            }
            localFile = FileLoader.getPathToAttach(localException2.media.document.thumb);
            if ((localFile != null) && (localFile.toString().length() > 0)) {
              ((ArrayList)localObject1).add(localFile);
            }
          }
        }
      }
    });
  }
  
  public void doneHolesInMedia(long paramLong, int paramInt1, int paramInt2)
    throws Exception
  {
    SQLitePreparedStatement localSQLitePreparedStatement;
    if (paramInt2 == -1)
    {
      if (paramInt1 == 0) {
        this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
      }
      for (;;)
      {
        localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        for (paramInt1 = 0; paramInt1 < 5; paramInt1++)
        {
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindLong(1, paramLong);
          localSQLitePreparedStatement.bindInteger(2, paramInt1);
          localSQLitePreparedStatement.bindInteger(3, 1);
          localSQLitePreparedStatement.bindInteger(4, 1);
          localSQLitePreparedStatement.step();
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
      }
      localSQLitePreparedStatement.dispose();
      return;
    }
    if (paramInt1 == 0) {
      this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) })).stepThis().dispose();
    }
    for (;;)
    {
      localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
      localSQLitePreparedStatement.requery();
      localSQLitePreparedStatement.bindLong(1, paramLong);
      localSQLitePreparedStatement.bindInteger(2, paramInt2);
      localSQLitePreparedStatement.bindInteger(3, 1);
      localSQLitePreparedStatement.bindInteger(4, 1);
      localSQLitePreparedStatement.step();
      localSQLitePreparedStatement.dispose();
      break;
      this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) })).stepThis().dispose();
    }
  }
  
  public void emptyMessagesMedia(final ArrayList<Integer> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1;
        Object localObject1;
        Object localObject2;
        Object localObject3;
        Object localObject4;
        try
        {
          localArrayList1 = new java/util/ArrayList;
          localArrayList1.<init>();
          ArrayList localArrayList2 = new java/util/ArrayList;
          localArrayList2.<init>();
          localObject1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN (%s)", new Object[] { TextUtils.join(",", paramArrayList) }), new Object[0]);
          for (;;)
          {
            if (((SQLiteCursor)localObject1).next())
            {
              localObject2 = ((SQLiteCursor)localObject1).byteBufferValue(0);
              if (localObject2 != null)
              {
                localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                ((TLRPC.Message)localObject3).readAttachPath((AbstractSerializedData)localObject2, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                ((NativeByteBuffer)localObject2).reuse();
                if (((TLRPC.Message)localObject3).media != null) {
                  if (((TLRPC.Message)localObject3).media.document != null)
                  {
                    localObject2 = FileLoader.getPathToAttach(((TLRPC.Message)localObject3).media.document, true);
                    if ((localObject2 != null) && (((File)localObject2).toString().length() > 0)) {
                      localArrayList1.add(localObject2);
                    }
                    localObject2 = FileLoader.getPathToAttach(((TLRPC.Message)localObject3).media.document.thumb, true);
                    if ((localObject2 != null) && (((File)localObject2).toString().length() > 0)) {
                      localArrayList1.add(localObject2);
                    }
                    localObject4 = ((TLRPC.Message)localObject3).media;
                    localObject2 = new org/telegram/tgnet/TLRPC$TL_documentEmpty;
                    ((TLRPC.TL_documentEmpty)localObject2).<init>();
                    ((TLRPC.MessageMedia)localObject4).document = ((TLRPC.Document)localObject2);
                    label233:
                    ((TLRPC.Message)localObject3).media.flags &= 0xFFFFFFFE;
                    ((TLRPC.Message)localObject3).id = ((SQLiteCursor)localObject1).intValue(1);
                    ((TLRPC.Message)localObject3).date = ((SQLiteCursor)localObject1).intValue(2);
                    ((TLRPC.Message)localObject3).dialog_id = ((SQLiteCursor)localObject1).longValue(3);
                    localArrayList2.add(localObject3);
                    continue;
                  }
                }
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        while (((TLRPC.Message)localObject3).media.photo != null)
        {
          localObject2 = ((TLRPC.Message)localObject3).media.photo.sizes.iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject4 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject2).next(), true);
            if ((localObject4 != null) && (((File)localObject4).toString().length() > 0)) {
              localArrayList1.add(localObject4);
            }
          }
          localObject4 = ((TLRPC.Message)localObject3).media;
          localObject2 = new org/telegram/tgnet/TLRPC$TL_photoEmpty;
          ((TLRPC.TL_photoEmpty)localObject2).<init>();
          ((TLRPC.MessageMedia)localObject4).photo = ((TLRPC.Photo)localObject2);
          break label233;
          ((SQLiteCursor)localObject1).dispose();
          if (!localException.isEmpty())
          {
            localObject3 = MessagesStorage.this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
            int i = 0;
            if (i < localException.size())
            {
              localObject2 = (TLRPC.Message)localException.get(i);
              localObject1 = new org/telegram/tgnet/NativeByteBuffer;
              ((NativeByteBuffer)localObject1).<init>(((TLRPC.Message)localObject2).getObjectSize());
              ((TLRPC.Message)localObject2).serializeToStream((AbstractSerializedData)localObject1);
              ((SQLitePreparedStatement)localObject3).requery();
              ((SQLitePreparedStatement)localObject3).bindLong(1, ((TLRPC.Message)localObject2).id);
              ((SQLitePreparedStatement)localObject3).bindLong(2, ((TLRPC.Message)localObject2).dialog_id);
              ((SQLitePreparedStatement)localObject3).bindInteger(3, MessageObject.getUnreadFlags((TLRPC.Message)localObject2));
              ((SQLitePreparedStatement)localObject3).bindInteger(4, ((TLRPC.Message)localObject2).send_state);
              ((SQLitePreparedStatement)localObject3).bindInteger(5, ((TLRPC.Message)localObject2).date);
              ((SQLitePreparedStatement)localObject3).bindByteBuffer(6, (NativeByteBuffer)localObject1);
              if (MessageObject.isOut((TLRPC.Message)localObject2))
              {
                j = 1;
                label551:
                ((SQLitePreparedStatement)localObject3).bindInteger(7, j);
                ((SQLitePreparedStatement)localObject3).bindInteger(8, ((TLRPC.Message)localObject2).ttl);
                if ((((TLRPC.Message)localObject2).flags & 0x400) == 0) {
                  break label646;
                }
                ((SQLitePreparedStatement)localObject3).bindInteger(9, ((TLRPC.Message)localObject2).views);
                label596:
                ((SQLitePreparedStatement)localObject3).bindInteger(10, 0);
                if (!((TLRPC.Message)localObject2).mentioned) {
                  break label665;
                }
              }
              label646:
              label665:
              for (int j = 1;; j = 0)
              {
                ((SQLitePreparedStatement)localObject3).bindInteger(11, j);
                ((SQLitePreparedStatement)localObject3).step();
                ((NativeByteBuffer)localObject1).reuse();
                i++;
                break;
                j = 0;
                break label551;
                ((SQLitePreparedStatement)localObject3).bindInteger(9, MessagesStorage.this.getMessageMediaType((TLRPC.Message)localObject2));
                break label596;
              }
            }
            ((SQLitePreparedStatement)localObject3).dispose();
            localObject1 = new org/telegram/messenger/MessagesStorage$29$1;
            ((1)localObject1).<init>(this, localException);
            AndroidUtilities.runOnUIThread((Runnable)localObject1);
          }
          FileLoader.getInstance(MessagesStorage.this.currentAccount).deleteFiles(localArrayList1, 0);
        }
      }
    });
  }
  
  public void getBlockedUsers()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        SQLiteCursor localSQLiteCursor;
        try
        {
          localArrayList1 = new java/util/ArrayList;
          localArrayList1.<init>();
          localArrayList2 = new java/util/ArrayList;
          localArrayList2.<init>();
          localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT * FROM blocked_users WHERE 1", new Object[0]);
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          while (localSQLiteCursor.next())
          {
            int i = localSQLiteCursor.intValue(0);
            localArrayList1.add(Integer.valueOf(i));
            if (localStringBuilder.length() != 0) {
              localStringBuilder.append(",");
            }
            localStringBuilder.append(i);
            continue;
            return;
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          localSQLiteCursor.dispose();
          if (localException.length() != 0) {
            MessagesStorage.this.getUsersInternal(localException.toString(), localArrayList2);
          }
          MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedBlockedUsers(localArrayList1, localArrayList2, true);
        }
      }
    });
  }
  
  public void getBotCache(final String paramString, final RequestDelegate paramRequestDelegate)
  {
    if ((paramString == null) || (paramRequestDelegate == null)) {}
    for (;;)
    {
      return;
      final int i = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          Object localObject1 = null;
          localObject2 = null;
          Object localObject4 = null;
          Object localObject5 = null;
          localObject6 = localObject2;
          localObject7 = localObject4;
          for (;;)
          {
            try
            {
              localObject8 = MessagesStorage.this.database;
              localObject6 = localObject2;
              localObject7 = localObject4;
              localObject9 = new java/lang/StringBuilder;
              localObject6 = localObject2;
              localObject7 = localObject4;
              ((StringBuilder)localObject9).<init>();
              localObject6 = localObject2;
              localObject7 = localObject4;
              ((SQLiteDatabase)localObject8).executeFast("DELETE FROM botcache WHERE date < " + i).stepThis().dispose();
              localObject6 = localObject2;
              localObject7 = localObject4;
              localObject8 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM botcache WHERE id = '%s'", new Object[] { paramString }), new Object[0]);
              localObject6 = localObject2;
              localObject7 = localObject4;
              boolean bool = ((SQLiteCursor)localObject8).next();
              localObject2 = localObject5;
              if (bool)
              {
                localObject2 = localObject1;
                localObject7 = localObject4;
              }
            }
            catch (Exception localException1)
            {
              Object localObject8;
              Object localObject9;
              int i;
              localObject7 = localObject6;
              FileLog.e(localException1);
              paramRequestDelegate.run((TLObject)localObject6, null);
              continue;
            }
            finally
            {
              paramRequestDelegate.run((TLObject)localObject7, null);
            }
            try
            {
              localObject9 = ((SQLiteCursor)localObject8).byteBufferValue(0);
              localObject2 = localObject5;
              if (localObject9 != null)
              {
                localObject2 = localObject1;
                localObject7 = localObject4;
                i = ((NativeByteBuffer)localObject9).readInt32(false);
                localObject2 = localObject1;
                localObject7 = localObject4;
                if (i != TLRPC.TL_messages_botCallbackAnswer.constructor) {
                  continue;
                }
                localObject2 = localObject1;
                localObject7 = localObject4;
                localObject6 = TLRPC.TL_messages_botCallbackAnswer.TLdeserialize((AbstractSerializedData)localObject9, i, false);
                localObject2 = localObject6;
                localObject7 = localObject6;
                ((NativeByteBuffer)localObject9).reuse();
                localObject2 = localObject6;
              }
            }
            catch (Exception localException2)
            {
              localObject6 = localObject2;
              localObject7 = localObject2;
              FileLog.e(localException2);
              continue;
            }
            localObject6 = localObject2;
            localObject7 = localObject2;
            ((SQLiteCursor)localObject8).dispose();
            paramRequestDelegate.run((TLObject)localObject2, null);
            return;
            localObject2 = localObject1;
            localObject7 = localObject4;
            localObject6 = TLRPC.messages_BotResults.TLdeserialize((AbstractSerializedData)localObject9, i, false);
          }
        }
      });
    }
  }
  
  public void getCachedPhoneBook(final boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1 = null;
        Object localObject2 = null;
        try
        {
          localObject7 = MessagesStorage.this.database.queryFinalized("SELECT name FROM sqlite_master WHERE type='table' AND name='user_contacts_v6'", new Object[0]);
          localObject2 = localObject7;
          localObject1 = localObject7;
          bool = ((SQLiteCursor)localObject7).next();
          localObject2 = localObject7;
          localObject1 = localObject7;
          ((SQLiteCursor)localObject7).dispose();
          localObject2 = null;
          localObject1 = null;
          localObject7 = null;
          if (bool)
          {
            i = 16;
            localObject7 = MessagesStorage.this.database.queryFinalized("SELECT COUNT(uid) FROM user_contacts_v6 WHERE 1", new Object[0]);
            localObject2 = localObject7;
            localObject1 = localObject7;
            if (((SQLiteCursor)localObject7).next())
            {
              localObject2 = localObject7;
              localObject1 = localObject7;
              i = Math.min(5000, ((SQLiteCursor)localObject7).intValue(0));
            }
            localObject2 = localObject7;
            localObject1 = localObject7;
            ((SQLiteCursor)localObject7).dispose();
            localObject2 = localObject7;
            localObject1 = localObject7;
            localObject9 = new android/util/SparseArray;
            localObject2 = localObject7;
            localObject1 = localObject7;
            ((SparseArray)localObject9).<init>(i);
            localObject2 = localObject7;
            localObject1 = localObject7;
            localObject7 = MessagesStorage.this.database.queryFinalized("SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1", new Object[0]);
            do
            {
              do
              {
                do
                {
                  localObject2 = localObject7;
                  localObject1 = localObject7;
                  if (!((SQLiteCursor)localObject7).next()) {
                    break;
                  }
                  localObject2 = localObject7;
                  localObject1 = localObject7;
                  i = ((SQLiteCursor)localObject7).intValue(0);
                  localObject2 = localObject7;
                  localObject1 = localObject7;
                  localObject10 = (ContactsController.Contact)((SparseArray)localObject9).get(i);
                  localObject11 = localObject10;
                  if (localObject10 == null)
                  {
                    localObject2 = localObject7;
                    localObject1 = localObject7;
                    localObject11 = new org/telegram/messenger/ContactsController$Contact;
                    localObject2 = localObject7;
                    localObject1 = localObject7;
                    ((ContactsController.Contact)localObject11).<init>();
                    localObject2 = localObject7;
                    localObject1 = localObject7;
                    ((ContactsController.Contact)localObject11).first_name = ((SQLiteCursor)localObject7).stringValue(1);
                    localObject2 = localObject7;
                    localObject1 = localObject7;
                    ((ContactsController.Contact)localObject11).last_name = ((SQLiteCursor)localObject7).stringValue(2);
                    localObject2 = localObject7;
                    localObject1 = localObject7;
                    ((ContactsController.Contact)localObject11).imported = ((SQLiteCursor)localObject7).intValue(6);
                    localObject2 = localObject7;
                    localObject1 = localObject7;
                    if (((ContactsController.Contact)localObject11).first_name == null)
                    {
                      localObject2 = localObject7;
                      localObject1 = localObject7;
                      ((ContactsController.Contact)localObject11).first_name = "";
                    }
                    localObject2 = localObject7;
                    localObject1 = localObject7;
                    if (((ContactsController.Contact)localObject11).last_name == null)
                    {
                      localObject2 = localObject7;
                      localObject1 = localObject7;
                      ((ContactsController.Contact)localObject11).last_name = "";
                    }
                    localObject2 = localObject7;
                    localObject1 = localObject7;
                    ((ContactsController.Contact)localObject11).contact_id = i;
                    localObject2 = localObject7;
                    localObject1 = localObject7;
                    ((SparseArray)localObject9).put(i, localObject11);
                  }
                  localObject2 = localObject7;
                  localObject1 = localObject7;
                  str1 = ((SQLiteCursor)localObject7).stringValue(3);
                } while (str1 == null);
                localObject2 = localObject7;
                localObject1 = localObject7;
                ((ContactsController.Contact)localObject11).phones.add(str1);
                localObject2 = localObject7;
                localObject1 = localObject7;
                str2 = ((SQLiteCursor)localObject7).stringValue(4);
              } while (str2 == null);
              localObject10 = str2;
              localObject2 = localObject7;
              localObject1 = localObject7;
              if (str2.length() == 8)
              {
                localObject10 = str2;
                localObject2 = localObject7;
                localObject1 = localObject7;
                if (str1.length() != 8)
                {
                  localObject2 = localObject7;
                  localObject1 = localObject7;
                  localObject10 = PhoneFormat.stripExceptNumbers(str1);
                }
              }
              localObject2 = localObject7;
              localObject1 = localObject7;
              ((ContactsController.Contact)localObject11).shortPhones.add(localObject10);
              localObject2 = localObject7;
              localObject1 = localObject7;
              ((ContactsController.Contact)localObject11).phoneDeleted.add(Integer.valueOf(((SQLiteCursor)localObject7).intValue(5)));
              localObject2 = localObject7;
              localObject1 = localObject7;
              ((ContactsController.Contact)localObject11).phoneTypes.add("");
              localObject2 = localObject7;
              localObject1 = localObject7;
            } while (((SparseArray)localObject9).size() != 5000);
            localObject2 = localObject7;
            localObject1 = localObject7;
            ((SQLiteCursor)localObject7).dispose();
            localObject1 = null;
            localObject2 = null;
            ContactsController.getInstance(MessagesStorage.this.currentAccount).migratePhoneBookToV7((SparseArray)localObject9);
            if (0 != 0) {
              throw new NullPointerException();
            }
            return;
          }
          if (0 != 0) {
            throw new NullPointerException();
          }
        }
        catch (Throwable localThrowable2)
        {
          for (;;)
          {
            Object localObject7;
            Object localObject10;
            Object localObject11;
            String str1;
            String str2;
            int k;
            int m;
            int i1;
            localObject1 = localObject2;
            FileLog.e(localThrowable2);
            localObject8 = localObject2;
            if (localObject2 != null)
            {
              ((SQLiteCursor)localObject2).dispose();
              localObject8 = localObject2;
            }
          }
        }
        finally
        {
          if (localObject1 == null) {
            break label1377;
          }
          ((SQLiteCursor)localObject1).dispose();
        }
        int i = 16;
        int j = 0;
        k = 0;
        m = 0;
        int n = 0;
        i1 = 0;
        int i2 = i;
        int i3 = j;
        localObject1 = localObject7;
        int i4 = n;
        try
        {
          localObject11 = MessagesStorage.this.database.queryFinalized("SELECT COUNT(key) FROM user_contacts_v7 WHERE 1", new Object[0]);
          int i5 = i;
          i2 = i;
          i3 = j;
          localObject1 = localObject11;
          i4 = n;
          localObject7 = localObject11;
          if (((SQLiteCursor)localObject11).next())
          {
            i2 = i;
            i3 = j;
            localObject1 = localObject11;
            i4 = n;
            localObject7 = localObject11;
            j = ((SQLiteCursor)localObject11).intValue(0);
            i2 = i;
            i3 = j;
            localObject1 = localObject11;
            i4 = n;
            localObject7 = localObject11;
            n = Math.min(5000, j);
            i = i1;
            if (j > 5000) {
              i = j - 5000;
            }
            i5 = n;
            k = j;
            m = i;
            i2 = n;
            i3 = j;
            localObject1 = localObject11;
            i4 = i;
            localObject7 = localObject11;
            if (BuildVars.LOGS_ENABLED)
            {
              i2 = n;
              i3 = j;
              localObject1 = localObject11;
              i4 = i;
              localObject7 = localObject11;
              localObject2 = new java/lang/StringBuilder;
              i2 = n;
              i3 = j;
              localObject1 = localObject11;
              i4 = i;
              localObject7 = localObject11;
              ((StringBuilder)localObject2).<init>();
              i2 = n;
              i3 = j;
              localObject1 = localObject11;
              i4 = i;
              localObject7 = localObject11;
              FileLog.d(MessagesStorage.this.currentAccount + " current cached contacts count = " + j);
              m = i;
              k = j;
              i5 = n;
            }
          }
          i = i5;
          n = k;
          localObject2 = localObject11;
          j = m;
          if (localObject11 != null)
          {
            ((SQLiteCursor)localObject11).dispose();
            j = m;
            localObject2 = localObject11;
            n = k;
            i = i5;
          }
        }
        catch (Throwable localThrowable1)
        {
          for (;;)
          {
            localObject8 = localObject1;
            FileLog.e(localThrowable1);
            i = i2;
            n = i3;
            localObject4 = localObject1;
            j = i4;
            if (localObject1 != null)
            {
              ((SQLiteCursor)localObject1).dispose();
              i = i2;
              n = i3;
              localObject4 = localObject1;
              j = i4;
            }
          }
        }
        finally
        {
          if (localObject8 == null) {
            break label1434;
          }
          ((SQLiteCursor)localObject8).dispose();
        }
        Object localObject9 = new HashMap(i);
        if (j != 0)
        {
          localObject7 = localObject2;
          localObject1 = localObject2;
        }
        try
        {
          localObject11 = MessagesStorage.this.database;
          localObject7 = localObject2;
          localObject1 = localObject2;
          localObject10 = new java/lang/StringBuilder;
          localObject7 = localObject2;
          localObject1 = localObject2;
          ((StringBuilder)localObject10).<init>();
          localObject7 = localObject2;
          localObject1 = localObject2;
          localObject2 = ((SQLiteDatabase)localObject11).queryFinalized("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1 LIMIT 0," + n, new Object[0]);
          label946:
          do
          {
            do
            {
              do
              {
                localObject7 = localObject2;
                localObject1 = localObject2;
                if (!((SQLiteCursor)localObject2).next()) {
                  break;
                }
                localObject7 = localObject2;
                localObject1 = localObject2;
                str2 = ((SQLiteCursor)localObject2).stringValue(0);
                localObject7 = localObject2;
                localObject1 = localObject2;
                localObject10 = (ContactsController.Contact)((HashMap)localObject9).get(str2);
                localObject11 = localObject10;
                if (localObject10 == null)
                {
                  localObject7 = localObject2;
                  localObject1 = localObject2;
                  localObject11 = new org/telegram/messenger/ContactsController$Contact;
                  localObject7 = localObject2;
                  localObject1 = localObject2;
                  ((ContactsController.Contact)localObject11).<init>();
                  localObject7 = localObject2;
                  localObject1 = localObject2;
                  ((ContactsController.Contact)localObject11).contact_id = ((SQLiteCursor)localObject2).intValue(1);
                  localObject7 = localObject2;
                  localObject1 = localObject2;
                  ((ContactsController.Contact)localObject11).first_name = ((SQLiteCursor)localObject2).stringValue(2);
                  localObject7 = localObject2;
                  localObject1 = localObject2;
                  ((ContactsController.Contact)localObject11).last_name = ((SQLiteCursor)localObject2).stringValue(3);
                  localObject7 = localObject2;
                  localObject1 = localObject2;
                  ((ContactsController.Contact)localObject11).imported = ((SQLiteCursor)localObject2).intValue(7);
                  localObject7 = localObject2;
                  localObject1 = localObject2;
                  if (((ContactsController.Contact)localObject11).first_name == null)
                  {
                    localObject7 = localObject2;
                    localObject1 = localObject2;
                    ((ContactsController.Contact)localObject11).first_name = "";
                  }
                  localObject7 = localObject2;
                  localObject1 = localObject2;
                  if (((ContactsController.Contact)localObject11).last_name == null)
                  {
                    localObject7 = localObject2;
                    localObject1 = localObject2;
                    ((ContactsController.Contact)localObject11).last_name = "";
                  }
                  localObject7 = localObject2;
                  localObject1 = localObject2;
                  ((HashMap)localObject9).put(str2, localObject11);
                }
                localObject7 = localObject2;
                localObject1 = localObject2;
                str1 = ((SQLiteCursor)localObject2).stringValue(4);
              } while (str1 == null);
              localObject7 = localObject2;
              localObject1 = localObject2;
              ((ContactsController.Contact)localObject11).phones.add(str1);
              localObject7 = localObject2;
              localObject1 = localObject2;
              str2 = ((SQLiteCursor)localObject2).stringValue(5);
            } while (str2 == null);
            localObject10 = str2;
            localObject7 = localObject2;
            localObject1 = localObject2;
            if (str2.length() == 8)
            {
              localObject10 = str2;
              localObject7 = localObject2;
              localObject1 = localObject2;
              if (str1.length() != 8)
              {
                localObject7 = localObject2;
                localObject1 = localObject2;
                localObject10 = PhoneFormat.stripExceptNumbers(str1);
              }
            }
            localObject7 = localObject2;
            localObject1 = localObject2;
            ((ContactsController.Contact)localObject11).shortPhones.add(localObject10);
            localObject7 = localObject2;
            localObject1 = localObject2;
            ((ContactsController.Contact)localObject11).phoneDeleted.add(Integer.valueOf(((SQLiteCursor)localObject2).intValue(6)));
            localObject7 = localObject2;
            localObject1 = localObject2;
            ((ContactsController.Contact)localObject11).phoneTypes.add("");
            localObject7 = localObject2;
            localObject1 = localObject2;
          } while (((HashMap)localObject9).size() != 5000);
          localObject7 = localObject2;
          localObject1 = localObject2;
          ((SQLiteCursor)localObject2).dispose();
          if (0 != 0) {
            throw new NullPointerException();
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Object localObject8;
            localObject1 = localObject8;
            ((HashMap)localObject9).clear();
            localObject1 = localObject8;
            FileLog.e(localException);
            if (localObject8 != null) {
              ((SQLiteCursor)localObject8).dispose();
            }
          }
        }
        finally
        {
          if (localObject1 == null) {
            break label1494;
          }
          ((SQLiteCursor)localObject1).dispose();
        }
        localObject2 = ContactsController.getInstance(MessagesStorage.this.currentAccount);
        if (!paramBoolean) {}
        label1377:
        label1434:
        label1494:
        for (boolean bool = true;; bool = false)
        {
          ((ContactsController)localObject2).performSyncPhoneBook((HashMap)localObject9, true, true, false, false, bool, false);
          break;
          Object localObject4;
          localObject8 = localObject5;
          localObject1 = localObject5;
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT us.key, us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted, us.imported FROM user_contacts_v7 as us LEFT JOIN user_phones_v7 as up ON us.key = up.key WHERE 1", new Object[0]);
          break label946;
        }
      }
    });
  }
  
  public int getChannelPtsSync(final int paramInt)
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    final Integer[] arrayOfInteger = new Integer[1];
    arrayOfInteger[0] = Integer.valueOf(0);
    this.storageQueue.postRunnable(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_1
        //   2: aconst_null
        //   3: astore_2
        //   4: aload_2
        //   5: astore_3
        //   6: aload_1
        //   7: astore 4
        //   9: aload_0
        //   10: getfield 23	org/telegram/messenger/MessagesStorage$95:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   13: invokestatic 40	org/telegram/messenger/MessagesStorage:access$000	(Lorg/telegram/messenger/MessagesStorage;)Lorg/telegram/SQLite/SQLiteDatabase;
        //   16: astore 5
        //   18: aload_2
        //   19: astore_3
        //   20: aload_1
        //   21: astore 4
        //   23: new 42	java/lang/StringBuilder
        //   26: astore 6
        //   28: aload_2
        //   29: astore_3
        //   30: aload_1
        //   31: astore 4
        //   33: aload 6
        //   35: invokespecial 43	java/lang/StringBuilder:<init>	()V
        //   38: aload_2
        //   39: astore_3
        //   40: aload_1
        //   41: astore 4
        //   43: aload 5
        //   45: aload 6
        //   47: ldc 45
        //   49: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   52: aload_0
        //   53: getfield 25	org/telegram/messenger/MessagesStorage$95:val$channelId	I
        //   56: ineg
        //   57: invokevirtual 52	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   60: invokevirtual 56	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   63: iconst_0
        //   64: anewarray 4	java/lang/Object
        //   67: invokevirtual 62	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
        //   70: astore_2
        //   71: aload_2
        //   72: astore_3
        //   73: aload_2
        //   74: astore 4
        //   76: aload_2
        //   77: invokevirtual 68	org/telegram/SQLite/SQLiteCursor:next	()Z
        //   80: ifeq +22 -> 102
        //   83: aload_2
        //   84: astore_3
        //   85: aload_2
        //   86: astore 4
        //   88: aload_0
        //   89: getfield 27	org/telegram/messenger/MessagesStorage$95:val$pts	[Ljava/lang/Integer;
        //   92: iconst_0
        //   93: aload_2
        //   94: iconst_0
        //   95: invokevirtual 71	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   98: invokestatic 77	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   101: aastore
        //   102: aload_2
        //   103: ifnull +7 -> 110
        //   106: aload_2
        //   107: invokevirtual 80	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   110: aload_0
        //   111: getfield 29	org/telegram/messenger/MessagesStorage$95:val$countDownLatch	Ljava/util/concurrent/CountDownLatch;
        //   114: ifnull +10 -> 124
        //   117: aload_0
        //   118: getfield 29	org/telegram/messenger/MessagesStorage$95:val$countDownLatch	Ljava/util/concurrent/CountDownLatch;
        //   121: invokevirtual 85	java/util/concurrent/CountDownLatch:countDown	()V
        //   124: return
        //   125: astore_2
        //   126: aload_3
        //   127: astore 4
        //   129: aload_2
        //   130: invokestatic 91	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   133: aload_3
        //   134: ifnull -24 -> 110
        //   137: aload_3
        //   138: invokevirtual 80	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   141: goto -31 -> 110
        //   144: astore_3
        //   145: aload 4
        //   147: ifnull +8 -> 155
        //   150: aload 4
        //   152: invokevirtual 80	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   155: aload_3
        //   156: athrow
        //   157: astore 4
        //   159: aload 4
        //   161: invokestatic 91	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   164: goto -40 -> 124
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	167	0	this	95
        //   1	40	1	localObject1	Object
        //   3	104	2	localSQLiteCursor1	SQLiteCursor
        //   125	5	2	localException1	Exception
        //   5	133	3	localSQLiteCursor2	SQLiteCursor
        //   144	12	3	localObject2	Object
        //   7	144	4	localObject3	Object
        //   157	3	4	localException2	Exception
        //   16	28	5	localSQLiteDatabase	SQLiteDatabase
        //   26	20	6	localStringBuilder	StringBuilder
        // Exception table:
        //   from	to	target	type
        //   9	18	125	java/lang/Exception
        //   23	28	125	java/lang/Exception
        //   33	38	125	java/lang/Exception
        //   43	71	125	java/lang/Exception
        //   76	83	125	java/lang/Exception
        //   88	102	125	java/lang/Exception
        //   9	18	144	finally
        //   23	28	144	finally
        //   33	38	144	finally
        //   43	71	144	finally
        //   76	83	144	finally
        //   88	102	144	finally
        //   129	133	144	finally
        //   110	124	157	java/lang/Exception
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfInteger[0].intValue();
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public TLRPC.Chat getChat(int paramInt)
  {
    localObject1 = null;
    try
    {
      ArrayList localArrayList = new java/util/ArrayList;
      localArrayList.<init>();
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      getChatsInternal("" + paramInt, localArrayList);
      localObject2 = localObject1;
      if (!localArrayList.isEmpty()) {
        localObject2 = (TLRPC.Chat)localArrayList.get(0);
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Object localObject2;
        FileLog.e(localException);
        Object localObject3 = localObject1;
      }
    }
    return (TLRPC.Chat)localObject2;
  }
  
  public TLRPC.Chat getChatSync(final int paramInt)
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    final TLRPC.Chat[] arrayOfChat = new TLRPC.Chat[1];
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        arrayOfChat[0] = MessagesStorage.this.getChat(paramInt);
        localCountDownLatch.countDown();
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfChat[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public void getChatsInternal(String paramString, ArrayList<TLRPC.Chat> paramArrayList)
    throws Exception
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayList == null)) {}
    for (;;)
    {
      return;
      paramString = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", new Object[] { paramString }), new Object[0]);
      while (paramString.next()) {
        try
        {
          NativeByteBuffer localNativeByteBuffer = paramString.byteBufferValue(0);
          if (localNativeByteBuffer != null)
          {
            TLRPC.Chat localChat = TLRPC.Chat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (localChat != null) {
              paramArrayList.add(localChat);
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
      paramString.dispose();
    }
  }
  
  public void getContacts()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        SQLiteCursor localSQLiteCursor;
        boolean bool;
        try
        {
          localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          for (;;)
          {
            if (localSQLiteCursor.next())
            {
              int i = localSQLiteCursor.intValue(0);
              TLRPC.TL_contact localTL_contact = new org/telegram/tgnet/TLRPC$TL_contact;
              localTL_contact.<init>();
              localTL_contact.user_id = i;
              if (localSQLiteCursor.intValue(1) == 1)
              {
                bool = true;
                localTL_contact.mutual = bool;
                if (localStringBuilder.length() != 0) {
                  localStringBuilder.append(",");
                }
                localArrayList1.add(localTL_contact);
                localStringBuilder.append(localTL_contact.user_id);
                continue;
                ContactsController.getInstance(MessagesStorage.this.currentAccount).processLoadedContacts(localArrayList1, localArrayList2, 1);
              }
            }
          }
        }
        catch (Exception localException)
        {
          localArrayList1.clear();
          localArrayList2.clear();
          FileLog.e(localException);
        }
        for (;;)
        {
          return;
          bool = false;
          break;
          localSQLiteCursor.dispose();
          if (localException.length() != 0) {
            MessagesStorage.this.getUsersInternal(localException.toString(), localArrayList2);
          }
        }
      }
    });
  }
  
  public SQLiteDatabase getDatabase()
  {
    return this.database;
  }
  
  public long getDatabaseSize()
  {
    long l1 = 0L;
    if (this.cacheFile != null) {
      l1 = 0L + this.cacheFile.length();
    }
    long l2 = l1;
    if (this.shmCacheFile != null) {
      l2 = l1 + this.shmCacheFile.length();
    }
    return l2;
  }
  
  public void getDialogPhotos(int paramInt1, final int paramInt2, final long paramLong, final int paramInt3)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        TLRPC.TL_photos_photos localTL_photos_photos;
        Object localObject2;
        try
        {
          if (paramLong != 0L)
          {
            SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY id DESC LIMIT %d", new Object[] { Integer.valueOf(paramInt2), Long.valueOf(paramLong), Integer.valueOf(paramInt3) }), new Object[0]);
            localTL_photos_photos = new org/telegram/tgnet/TLRPC$TL_photos_photos;
            localTL_photos_photos.<init>();
            while (localSQLiteCursor.next())
            {
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
              if (localNativeByteBuffer != null)
              {
                localObject2 = TLRPC.Photo.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                localTL_photos_photos.photos.add(localObject2);
                continue;
                return;
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          Object localObject1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY id DESC LIMIT %d", new Object[] { Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) }), new Object[0]);
          break;
          ((SQLiteCursor)localObject1).dispose();
          localObject2 = Utilities.stageQueue;
          localObject1 = new org/telegram/messenger/MessagesStorage$24$1;
          ((1)localObject1).<init>(this, localTL_photos_photos);
          ((DispatchQueue)localObject2).postRunnable((Runnable)localObject1);
        }
      }
    });
  }
  
  public int getDialogReadMax(final boolean paramBoolean, final long paramLong)
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    Integer[] arrayOfInteger = new Integer[1];
    arrayOfInteger[0] = Integer.valueOf(0);
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1 = null;
        SQLiteCursor localSQLiteCursor1 = null;
        localSQLiteCursor2 = localSQLiteCursor1;
        localObject3 = localObject1;
        for (;;)
        {
          try
          {
            if (!paramBoolean) {
              continue;
            }
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject3 = localObject1;
            localObject4 = MessagesStorage.this.database;
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject3 = localObject1;
            localObject5 = new java/lang/StringBuilder;
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject3 = localObject1;
            ((StringBuilder)localObject5).<init>();
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject3 = localObject1;
            localSQLiteCursor1 = ((SQLiteDatabase)localObject4).queryFinalized("SELECT outbox_max FROM dialogs WHERE did = " + paramLong, new Object[0]);
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject3 = localSQLiteCursor1;
            if (localSQLiteCursor1.next())
            {
              localSQLiteCursor2 = localSQLiteCursor1;
              localObject3 = localSQLiteCursor1;
              localCountDownLatch[0] = Integer.valueOf(localSQLiteCursor1.intValue(0));
            }
            if (localSQLiteCursor1 != null) {
              localSQLiteCursor1.dispose();
            }
          }
          catch (Exception localException)
          {
            Object localObject4;
            Object localObject5;
            localObject3 = localSQLiteCursor2;
            FileLog.e(localException);
            if (localSQLiteCursor2 == null) {
              continue;
            }
            localSQLiteCursor2.dispose();
            continue;
          }
          finally
          {
            if (localObject3 == null) {
              continue;
            }
            ((SQLiteCursor)localObject3).dispose();
          }
          this.val$countDownLatch.countDown();
          return;
          localSQLiteCursor2 = localSQLiteCursor1;
          localObject3 = localObject1;
          localObject5 = MessagesStorage.this.database;
          localSQLiteCursor2 = localSQLiteCursor1;
          localObject3 = localObject1;
          localObject4 = new java/lang/StringBuilder;
          localSQLiteCursor2 = localSQLiteCursor1;
          localObject3 = localObject1;
          ((StringBuilder)localObject4).<init>();
          localSQLiteCursor2 = localSQLiteCursor1;
          localObject3 = localObject1;
          localSQLiteCursor1 = ((SQLiteDatabase)localObject5).queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + paramLong, new Object[0]);
        }
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfInteger[0].intValue();
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public void getDialogs(final int paramInt1, final int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: new 33	org/telegram/tgnet/TLRPC$TL_messages_dialogs
        //   3: dup
        //   4: invokespecial 34	org/telegram/tgnet/TLRPC$TL_messages_dialogs:<init>	()V
        //   7: astore_1
        //   8: new 36	java/util/ArrayList
        //   11: dup
        //   12: invokespecial 37	java/util/ArrayList:<init>	()V
        //   15: astore_2
        //   16: new 36	java/util/ArrayList
        //   19: astore_3
        //   20: aload_3
        //   21: invokespecial 37	java/util/ArrayList:<init>	()V
        //   24: aload_3
        //   25: aload_0
        //   26: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   29: invokestatic 41	org/telegram/messenger/MessagesStorage:access$300	(Lorg/telegram/messenger/MessagesStorage;)I
        //   32: invokestatic 47	org/telegram/messenger/UserConfig:getInstance	(I)Lorg/telegram/messenger/UserConfig;
        //   35: invokevirtual 51	org/telegram/messenger/UserConfig:getClientUserId	()I
        //   38: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   41: invokevirtual 61	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   44: pop
        //   45: new 36	java/util/ArrayList
        //   48: astore 4
        //   50: aload 4
        //   52: invokespecial 37	java/util/ArrayList:<init>	()V
        //   55: new 36	java/util/ArrayList
        //   58: astore 5
        //   60: aload 5
        //   62: invokespecial 37	java/util/ArrayList:<init>	()V
        //   65: new 36	java/util/ArrayList
        //   68: astore 6
        //   70: aload 6
        //   72: invokespecial 37	java/util/ArrayList:<init>	()V
        //   75: new 63	android/util/LongSparseArray
        //   78: astore 7
        //   80: aload 7
        //   82: invokespecial 64	android/util/LongSparseArray:<init>	()V
        //   85: aload_0
        //   86: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   89: invokestatic 68	org/telegram/messenger/MessagesStorage:access$000	(Lorg/telegram/messenger/MessagesStorage;)Lorg/telegram/SQLite/SQLiteDatabase;
        //   92: getstatic 74	java/util/Locale:US	Ljava/util/Locale;
        //   95: ldc 76
        //   97: iconst_2
        //   98: anewarray 4	java/lang/Object
        //   101: dup
        //   102: iconst_0
        //   103: aload_0
        //   104: getfield 22	org/telegram/messenger/MessagesStorage$90:val$offset	I
        //   107: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   110: aastore
        //   111: dup
        //   112: iconst_1
        //   113: aload_0
        //   114: getfield 24	org/telegram/messenger/MessagesStorage$90:val$count	I
        //   117: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   120: aastore
        //   121: invokestatic 82	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   124: iconst_0
        //   125: anewarray 4	java/lang/Object
        //   128: invokevirtual 88	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
        //   131: astore 8
        //   133: aload 8
        //   135: invokevirtual 94	org/telegram/SQLite/SQLiteCursor:next	()Z
        //   138: ifeq +845 -> 983
        //   141: new 96	org/telegram/tgnet/TLRPC$TL_dialog
        //   144: astore 9
        //   146: aload 9
        //   148: invokespecial 97	org/telegram/tgnet/TLRPC$TL_dialog:<init>	()V
        //   151: aload 9
        //   153: aload 8
        //   155: iconst_0
        //   156: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:longValue	(I)J
        //   159: putfield 105	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   162: aload 9
        //   164: aload 8
        //   166: iconst_1
        //   167: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   170: putfield 112	org/telegram/tgnet/TLRPC$TL_dialog:top_message	I
        //   173: aload 9
        //   175: aload 8
        //   177: iconst_2
        //   178: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   181: putfield 115	org/telegram/tgnet/TLRPC$TL_dialog:unread_count	I
        //   184: aload 9
        //   186: aload 8
        //   188: iconst_3
        //   189: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   192: putfield 118	org/telegram/tgnet/TLRPC$TL_dialog:last_message_date	I
        //   195: aload 9
        //   197: aload 8
        //   199: bipush 10
        //   201: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   204: putfield 121	org/telegram/tgnet/TLRPC$TL_dialog:pts	I
        //   207: aload 9
        //   209: getfield 121	org/telegram/tgnet/TLRPC$TL_dialog:pts	I
        //   212: ifeq +12 -> 224
        //   215: aload 9
        //   217: getfield 105	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   220: l2i
        //   221: ifle +654 -> 875
        //   224: iconst_0
        //   225: istore 10
        //   227: aload 9
        //   229: iload 10
        //   231: putfield 124	org/telegram/tgnet/TLRPC$TL_dialog:flags	I
        //   234: aload 9
        //   236: aload 8
        //   238: bipush 11
        //   240: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   243: putfield 127	org/telegram/tgnet/TLRPC$TL_dialog:read_inbox_max_id	I
        //   246: aload 9
        //   248: aload 8
        //   250: bipush 12
        //   252: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   255: putfield 130	org/telegram/tgnet/TLRPC$TL_dialog:read_outbox_max_id	I
        //   258: aload 9
        //   260: aload 8
        //   262: bipush 14
        //   264: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   267: putfield 133	org/telegram/tgnet/TLRPC$TL_dialog:pinnedNum	I
        //   270: aload 9
        //   272: getfield 133	org/telegram/tgnet/TLRPC$TL_dialog:pinnedNum	I
        //   275: ifeq +606 -> 881
        //   278: iconst_1
        //   279: istore 11
        //   281: aload 9
        //   283: iload 11
        //   285: putfield 137	org/telegram/tgnet/TLRPC$TL_dialog:pinned	Z
        //   288: aload 9
        //   290: aload 8
        //   292: bipush 15
        //   294: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   297: putfield 140	org/telegram/tgnet/TLRPC$TL_dialog:unread_mentions_count	I
        //   300: aload 8
        //   302: bipush 8
        //   304: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:longValue	(I)J
        //   307: lstore 12
        //   309: lload 12
        //   311: l2i
        //   312: istore 10
        //   314: new 142	org/telegram/tgnet/TLRPC$TL_peerNotifySettings
        //   317: astore 14
        //   319: aload 14
        //   321: invokespecial 143	org/telegram/tgnet/TLRPC$TL_peerNotifySettings:<init>	()V
        //   324: aload 9
        //   326: aload 14
        //   328: putfield 147	org/telegram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/telegram/tgnet/TLRPC$PeerNotifySettings;
        //   331: iload 10
        //   333: iconst_1
        //   334: iand
        //   335: ifeq +38 -> 373
        //   338: aload 9
        //   340: getfield 147	org/telegram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/telegram/tgnet/TLRPC$PeerNotifySettings;
        //   343: lload 12
        //   345: bipush 32
        //   347: lshr
        //   348: l2i
        //   349: putfield 152	org/telegram/tgnet/TLRPC$PeerNotifySettings:mute_until	I
        //   352: aload 9
        //   354: getfield 147	org/telegram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/telegram/tgnet/TLRPC$PeerNotifySettings;
        //   357: getfield 152	org/telegram/tgnet/TLRPC$PeerNotifySettings:mute_until	I
        //   360: ifne +13 -> 373
        //   363: aload 9
        //   365: getfield 147	org/telegram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/telegram/tgnet/TLRPC$PeerNotifySettings;
        //   368: ldc -103
        //   370: putfield 152	org/telegram/tgnet/TLRPC$PeerNotifySettings:mute_until	I
        //   373: aload_1
        //   374: getfield 159	org/telegram/tgnet/TLRPC$messages_Dialogs:dialogs	Ljava/util/ArrayList;
        //   377: aload 9
        //   379: invokevirtual 61	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   382: pop
        //   383: aload 8
        //   385: iconst_4
        //   386: invokevirtual 163	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
        //   389: astore 15
        //   391: aload 15
        //   393: ifnull +372 -> 765
        //   396: aload 15
        //   398: aload 15
        //   400: iconst_0
        //   401: invokevirtual 169	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   404: iconst_0
        //   405: invokestatic 175	org/telegram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$Message;
        //   408: astore 14
        //   410: aload 14
        //   412: aload 15
        //   414: aload_0
        //   415: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   418: invokestatic 41	org/telegram/messenger/MessagesStorage:access$300	(Lorg/telegram/messenger/MessagesStorage;)I
        //   421: invokestatic 47	org/telegram/messenger/UserConfig:getInstance	(I)Lorg/telegram/messenger/UserConfig;
        //   424: getfield 178	org/telegram/messenger/UserConfig:clientUserId	I
        //   427: invokevirtual 182	org/telegram/tgnet/TLRPC$Message:readAttachPath	(Lorg/telegram/tgnet/AbstractSerializedData;I)V
        //   430: aload 15
        //   432: invokevirtual 185	org/telegram/tgnet/NativeByteBuffer:reuse	()V
        //   435: aload 14
        //   437: ifnull +328 -> 765
        //   440: aload 14
        //   442: aload 8
        //   444: iconst_5
        //   445: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   448: invokestatic 191	org/telegram/messenger/MessageObject:setUnreadFlags	(Lorg/telegram/tgnet/TLRPC$Message;I)V
        //   451: aload 14
        //   453: aload 8
        //   455: bipush 6
        //   457: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   460: putfield 193	org/telegram/tgnet/TLRPC$Message:id	I
        //   463: aload 8
        //   465: bipush 9
        //   467: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   470: istore 10
        //   472: iload 10
        //   474: ifeq +10 -> 484
        //   477: aload 9
        //   479: iload 10
        //   481: putfield 118	org/telegram/tgnet/TLRPC$TL_dialog:last_message_date	I
        //   484: aload 14
        //   486: aload 8
        //   488: bipush 7
        //   490: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   493: putfield 196	org/telegram/tgnet/TLRPC$Message:send_state	I
        //   496: aload 14
        //   498: aload 9
        //   500: getfield 105	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   503: putfield 199	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   506: aload_1
        //   507: getfield 202	org/telegram/tgnet/TLRPC$messages_Dialogs:messages	Ljava/util/ArrayList;
        //   510: aload 14
        //   512: invokevirtual 61	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   515: pop
        //   516: aload 14
        //   518: aload_3
        //   519: aload 4
        //   521: invokestatic 206	org/telegram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/telegram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   524: aload 14
        //   526: getfield 209	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   529: ifeq +236 -> 765
        //   532: aload 14
        //   534: getfield 213	org/telegram/tgnet/TLRPC$Message:action	Lorg/telegram/tgnet/TLRPC$MessageAction;
        //   537: instanceof 215
        //   540: ifne +25 -> 565
        //   543: aload 14
        //   545: getfield 213	org/telegram/tgnet/TLRPC$Message:action	Lorg/telegram/tgnet/TLRPC$MessageAction;
        //   548: instanceof 217
        //   551: ifne +14 -> 565
        //   554: aload 14
        //   556: getfield 213	org/telegram/tgnet/TLRPC$Message:action	Lorg/telegram/tgnet/TLRPC$MessageAction;
        //   559: instanceof 219
        //   562: ifeq +203 -> 765
        //   565: aload 8
        //   567: bipush 13
        //   569: invokevirtual 223	org/telegram/SQLite/SQLiteCursor:isNull	(I)Z
        //   572: ifne +109 -> 681
        //   575: aload 8
        //   577: bipush 13
        //   579: invokevirtual 163	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
        //   582: astore 15
        //   584: aload 15
        //   586: ifnull +95 -> 681
        //   589: aload 14
        //   591: aload 15
        //   593: aload 15
        //   595: iconst_0
        //   596: invokevirtual 169	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   599: iconst_0
        //   600: invokestatic 175	org/telegram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$Message;
        //   603: putfield 227	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   606: aload 14
        //   608: getfield 227	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   611: aload 15
        //   613: aload_0
        //   614: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   617: invokestatic 41	org/telegram/messenger/MessagesStorage:access$300	(Lorg/telegram/messenger/MessagesStorage;)I
        //   620: invokestatic 47	org/telegram/messenger/UserConfig:getInstance	(I)Lorg/telegram/messenger/UserConfig;
        //   623: getfield 178	org/telegram/messenger/UserConfig:clientUserId	I
        //   626: invokevirtual 182	org/telegram/tgnet/TLRPC$Message:readAttachPath	(Lorg/telegram/tgnet/AbstractSerializedData;I)V
        //   629: aload 15
        //   631: invokevirtual 185	org/telegram/tgnet/NativeByteBuffer:reuse	()V
        //   634: aload 14
        //   636: getfield 227	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   639: ifnull +42 -> 681
        //   642: aload 14
        //   644: invokestatic 231	org/telegram/messenger/MessageObject:isMegagroup	(Lorg/telegram/tgnet/TLRPC$Message;)Z
        //   647: ifeq +23 -> 670
        //   650: aload 14
        //   652: getfield 227	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   655: astore 15
        //   657: aload 15
        //   659: aload 15
        //   661: getfield 232	org/telegram/tgnet/TLRPC$Message:flags	I
        //   664: ldc -23
        //   666: ior
        //   667: putfield 232	org/telegram/tgnet/TLRPC$Message:flags	I
        //   670: aload 14
        //   672: getfield 227	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   675: aload_3
        //   676: aload 4
        //   678: invokestatic 206	org/telegram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/telegram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   681: aload 14
        //   683: getfield 227	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   686: ifnonnull +79 -> 765
        //   689: aload 14
        //   691: getfield 209	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   694: i2l
        //   695: lstore 16
        //   697: lload 16
        //   699: lstore 12
        //   701: aload 14
        //   703: getfield 237	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
        //   706: getfield 242	org/telegram/tgnet/TLRPC$Peer:channel_id	I
        //   709: ifeq +20 -> 729
        //   712: lload 16
        //   714: aload 14
        //   716: getfield 237	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
        //   719: getfield 242	org/telegram/tgnet/TLRPC$Peer:channel_id	I
        //   722: i2l
        //   723: bipush 32
        //   725: lshl
        //   726: lor
        //   727: lstore 12
        //   729: aload 6
        //   731: lload 12
        //   733: invokestatic 247	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   736: invokevirtual 250	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   739: ifne +14 -> 753
        //   742: aload 6
        //   744: lload 12
        //   746: invokestatic 247	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   749: invokevirtual 61	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   752: pop
        //   753: aload 7
        //   755: aload 9
        //   757: getfield 105	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   760: aload 14
        //   762: invokevirtual 254	android/util/LongSparseArray:put	(JLjava/lang/Object;)V
        //   765: aload 9
        //   767: getfield 105	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   770: l2i
        //   771: istore 10
        //   773: aload 9
        //   775: getfield 105	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   778: bipush 32
        //   780: lshr
        //   781: l2i
        //   782: istore 18
        //   784: iload 10
        //   786: ifeq +170 -> 956
        //   789: iload 18
        //   791: iconst_1
        //   792: if_icmpne +105 -> 897
        //   795: aload 4
        //   797: iload 10
        //   799: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   802: invokevirtual 250	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   805: ifne -672 -> 133
        //   808: aload 4
        //   810: iload 10
        //   812: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   815: invokevirtual 61	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   818: pop
        //   819: goto -686 -> 133
        //   822: astore_3
        //   823: aload_1
        //   824: getfield 159	org/telegram/tgnet/TLRPC$messages_Dialogs:dialogs	Ljava/util/ArrayList;
        //   827: invokevirtual 257	java/util/ArrayList:clear	()V
        //   830: aload_1
        //   831: getfield 260	org/telegram/tgnet/TLRPC$messages_Dialogs:users	Ljava/util/ArrayList;
        //   834: invokevirtual 257	java/util/ArrayList:clear	()V
        //   837: aload_1
        //   838: getfield 263	org/telegram/tgnet/TLRPC$messages_Dialogs:chats	Ljava/util/ArrayList;
        //   841: invokevirtual 257	java/util/ArrayList:clear	()V
        //   844: aload_2
        //   845: invokevirtual 257	java/util/ArrayList:clear	()V
        //   848: aload_3
        //   849: invokestatic 269	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   852: aload_0
        //   853: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   856: invokestatic 41	org/telegram/messenger/MessagesStorage:access$300	(Lorg/telegram/messenger/MessagesStorage;)I
        //   859: invokestatic 274	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
        //   862: aload_1
        //   863: aload_2
        //   864: iconst_0
        //   865: bipush 100
        //   867: iconst_1
        //   868: iconst_1
        //   869: iconst_0
        //   870: iconst_1
        //   871: invokevirtual 278	org/telegram/messenger/MessagesController:processLoadedDialogs	(Lorg/telegram/tgnet/TLRPC$messages_Dialogs;Ljava/util/ArrayList;IIIZZZ)V
        //   874: return
        //   875: iconst_1
        //   876: istore 10
        //   878: goto -651 -> 227
        //   881: iconst_0
        //   882: istore 11
        //   884: goto -603 -> 281
        //   887: astore 14
        //   889: aload 14
        //   891: invokestatic 269	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   894: goto -129 -> 765
        //   897: iload 10
        //   899: ifle +28 -> 927
        //   902: aload_3
        //   903: iload 10
        //   905: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   908: invokevirtual 250	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   911: ifne -778 -> 133
        //   914: aload_3
        //   915: iload 10
        //   917: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   920: invokevirtual 61	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   923: pop
        //   924: goto -791 -> 133
        //   927: aload 4
        //   929: iload 10
        //   931: ineg
        //   932: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   935: invokevirtual 250	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   938: ifne -805 -> 133
        //   941: aload 4
        //   943: iload 10
        //   945: ineg
        //   946: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   949: invokevirtual 61	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   952: pop
        //   953: goto -820 -> 133
        //   956: aload 5
        //   958: iload 18
        //   960: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   963: invokevirtual 250	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   966: ifne -833 -> 133
        //   969: aload 5
        //   971: iload 18
        //   973: invokestatic 57	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   976: invokevirtual 61	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   979: pop
        //   980: goto -847 -> 133
        //   983: aload 8
        //   985: invokevirtual 281	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   988: aload 6
        //   990: invokevirtual 284	java/util/ArrayList:isEmpty	()Z
        //   993: ifne +217 -> 1210
        //   996: aload_0
        //   997: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   1000: invokestatic 68	org/telegram/messenger/MessagesStorage:access$000	(Lorg/telegram/messenger/MessagesStorage;)Lorg/telegram/SQLite/SQLiteDatabase;
        //   1003: getstatic 74	java/util/Locale:US	Ljava/util/Locale;
        //   1006: ldc_w 286
        //   1009: iconst_1
        //   1010: anewarray 4	java/lang/Object
        //   1013: dup
        //   1014: iconst_0
        //   1015: ldc_w 288
        //   1018: aload 6
        //   1020: invokestatic 294	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1023: aastore
        //   1024: invokestatic 82	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   1027: iconst_0
        //   1028: anewarray 4	java/lang/Object
        //   1031: invokevirtual 88	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
        //   1034: astore 8
        //   1036: aload 8
        //   1038: invokevirtual 94	org/telegram/SQLite/SQLiteCursor:next	()Z
        //   1041: ifeq +164 -> 1205
        //   1044: aload 8
        //   1046: iconst_0
        //   1047: invokevirtual 163	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
        //   1050: astore 9
        //   1052: aload 9
        //   1054: ifnull -18 -> 1036
        //   1057: aload 9
        //   1059: aload 9
        //   1061: iconst_0
        //   1062: invokevirtual 169	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   1065: iconst_0
        //   1066: invokestatic 175	org/telegram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$Message;
        //   1069: astore 6
        //   1071: aload 6
        //   1073: aload 9
        //   1075: aload_0
        //   1076: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   1079: invokestatic 41	org/telegram/messenger/MessagesStorage:access$300	(Lorg/telegram/messenger/MessagesStorage;)I
        //   1082: invokestatic 47	org/telegram/messenger/UserConfig:getInstance	(I)Lorg/telegram/messenger/UserConfig;
        //   1085: getfield 178	org/telegram/messenger/UserConfig:clientUserId	I
        //   1088: invokevirtual 182	org/telegram/tgnet/TLRPC$Message:readAttachPath	(Lorg/telegram/tgnet/AbstractSerializedData;I)V
        //   1091: aload 9
        //   1093: invokevirtual 185	org/telegram/tgnet/NativeByteBuffer:reuse	()V
        //   1096: aload 6
        //   1098: aload 8
        //   1100: iconst_1
        //   1101: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   1104: putfield 193	org/telegram/tgnet/TLRPC$Message:id	I
        //   1107: aload 6
        //   1109: aload 8
        //   1111: iconst_2
        //   1112: invokevirtual 109	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   1115: putfield 297	org/telegram/tgnet/TLRPC$Message:date	I
        //   1118: aload 6
        //   1120: aload 8
        //   1122: iconst_3
        //   1123: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:longValue	(I)J
        //   1126: putfield 199	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   1129: aload 6
        //   1131: aload_3
        //   1132: aload 4
        //   1134: invokestatic 206	org/telegram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/telegram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   1137: aload 7
        //   1139: aload 6
        //   1141: getfield 199	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   1144: invokevirtual 301	android/util/LongSparseArray:get	(J)Ljava/lang/Object;
        //   1147: checkcast 171	org/telegram/tgnet/TLRPC$Message
        //   1150: astore 9
        //   1152: aload 9
        //   1154: ifnull -118 -> 1036
        //   1157: aload 9
        //   1159: aload 6
        //   1161: putfield 227	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   1164: aload 6
        //   1166: aload 9
        //   1168: getfield 199	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   1171: putfield 199	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   1174: aload 9
        //   1176: invokestatic 231	org/telegram/messenger/MessageObject:isMegagroup	(Lorg/telegram/tgnet/TLRPC$Message;)Z
        //   1179: ifeq -143 -> 1036
        //   1182: aload 9
        //   1184: getfield 227	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   1187: astore 6
        //   1189: aload 6
        //   1191: aload 6
        //   1193: getfield 232	org/telegram/tgnet/TLRPC$Message:flags	I
        //   1196: ldc -23
        //   1198: ior
        //   1199: putfield 232	org/telegram/tgnet/TLRPC$Message:flags	I
        //   1202: goto -166 -> 1036
        //   1205: aload 8
        //   1207: invokevirtual 281	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   1210: aload 5
        //   1212: invokevirtual 284	java/util/ArrayList:isEmpty	()Z
        //   1215: ifne +20 -> 1235
        //   1218: aload_0
        //   1219: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   1222: ldc_w 288
        //   1225: aload 5
        //   1227: invokestatic 294	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1230: aload_2
        //   1231: aload_3
        //   1232: invokevirtual 305	org/telegram/messenger/MessagesStorage:getEncryptedChatsInternal	(Ljava/lang/String;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   1235: aload 4
        //   1237: invokevirtual 284	java/util/ArrayList:isEmpty	()Z
        //   1240: ifne +22 -> 1262
        //   1243: aload_0
        //   1244: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   1247: ldc_w 288
        //   1250: aload 4
        //   1252: invokestatic 294	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1255: aload_1
        //   1256: getfield 263	org/telegram/tgnet/TLRPC$messages_Dialogs:chats	Ljava/util/ArrayList;
        //   1259: invokevirtual 309	org/telegram/messenger/MessagesStorage:getChatsInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   1262: aload_3
        //   1263: invokevirtual 284	java/util/ArrayList:isEmpty	()Z
        //   1266: ifne +21 -> 1287
        //   1269: aload_0
        //   1270: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   1273: ldc_w 288
        //   1276: aload_3
        //   1277: invokestatic 294	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1280: aload_1
        //   1281: getfield 260	org/telegram/tgnet/TLRPC$messages_Dialogs:users	Ljava/util/ArrayList;
        //   1284: invokevirtual 312	org/telegram/messenger/MessagesStorage:getUsersInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   1287: aload_0
        //   1288: getfield 20	org/telegram/messenger/MessagesStorage$90:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   1291: invokestatic 41	org/telegram/messenger/MessagesStorage:access$300	(Lorg/telegram/messenger/MessagesStorage;)I
        //   1294: invokestatic 274	org/telegram/messenger/MessagesController:getInstance	(I)Lorg/telegram/messenger/MessagesController;
        //   1297: aload_1
        //   1298: aload_2
        //   1299: aload_0
        //   1300: getfield 22	org/telegram/messenger/MessagesStorage$90:val$offset	I
        //   1303: aload_0
        //   1304: getfield 24	org/telegram/messenger/MessagesStorage$90:val$count	I
        //   1307: iconst_1
        //   1308: iconst_0
        //   1309: iconst_0
        //   1310: iconst_1
        //   1311: invokevirtual 278	org/telegram/messenger/MessagesController:processLoadedDialogs	(Lorg/telegram/tgnet/TLRPC$messages_Dialogs;Ljava/util/ArrayList;IIIZZZ)V
        //   1314: goto -440 -> 874
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	1317	0	this	90
        //   7	1291	1	localTL_messages_dialogs	TLRPC.TL_messages_dialogs
        //   15	1284	2	localArrayList1	ArrayList
        //   19	657	3	localArrayList2	ArrayList
        //   822	455	3	localException1	Exception
        //   48	1203	4	localArrayList3	ArrayList
        //   58	1168	5	localArrayList4	ArrayList
        //   68	1124	6	localObject1	Object
        //   78	1060	7	localLongSparseArray	LongSparseArray
        //   131	1075	8	localSQLiteCursor	SQLiteCursor
        //   144	1039	9	localObject2	Object
        //   225	719	10	i	int
        //   279	604	11	bool	boolean
        //   307	438	12	l1	long
        //   317	444	14	localObject3	Object
        //   887	3	14	localException2	Exception
        //   389	271	15	localObject4	Object
        //   695	18	16	l2	long
        //   782	190	18	j	int
        // Exception table:
        //   from	to	target	type
        //   16	133	822	java/lang/Exception
        //   133	224	822	java/lang/Exception
        //   227	278	822	java/lang/Exception
        //   281	309	822	java/lang/Exception
        //   314	331	822	java/lang/Exception
        //   338	373	822	java/lang/Exception
        //   373	391	822	java/lang/Exception
        //   396	435	822	java/lang/Exception
        //   440	472	822	java/lang/Exception
        //   477	484	822	java/lang/Exception
        //   484	524	822	java/lang/Exception
        //   765	784	822	java/lang/Exception
        //   795	819	822	java/lang/Exception
        //   889	894	822	java/lang/Exception
        //   902	924	822	java/lang/Exception
        //   927	953	822	java/lang/Exception
        //   956	980	822	java/lang/Exception
        //   983	1036	822	java/lang/Exception
        //   1036	1052	822	java/lang/Exception
        //   1057	1152	822	java/lang/Exception
        //   1157	1202	822	java/lang/Exception
        //   1205	1210	822	java/lang/Exception
        //   1210	1235	822	java/lang/Exception
        //   1235	1262	822	java/lang/Exception
        //   1262	1287	822	java/lang/Exception
        //   1287	1314	822	java/lang/Exception
        //   524	565	887	java/lang/Exception
        //   565	584	887	java/lang/Exception
        //   589	670	887	java/lang/Exception
        //   670	681	887	java/lang/Exception
        //   681	697	887	java/lang/Exception
        //   701	729	887	java/lang/Exception
        //   729	753	887	java/lang/Exception
        //   753	765	887	java/lang/Exception
      }
    });
  }
  
  public void getDownloadQueue(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        SQLiteCursor localSQLiteCursor;
        DownloadObject localDownloadObject;
        Object localObject;
        boolean bool;
        try
        {
          ArrayList localArrayList = new java/util/ArrayList;
          localArrayList.<init>();
          localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", new Object[] { Integer.valueOf(paramInt) }), new Object[0]);
          for (;;)
          {
            if (localSQLiteCursor.next())
            {
              localDownloadObject = new org/telegram/messenger/DownloadObject;
              localDownloadObject.<init>();
              localDownloadObject.type = localSQLiteCursor.intValue(1);
              localDownloadObject.id = localSQLiteCursor.longValue(0);
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(2);
              if (localNativeByteBuffer != null)
              {
                localObject = TLRPC.MessageMedia.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                if (((TLRPC.MessageMedia)localObject).document != null)
                {
                  localDownloadObject.object = ((TLRPC.MessageMedia)localObject).document;
                  if (((TLRPC.MessageMedia)localObject).ttl_seconds == 0) {
                    break label187;
                  }
                  bool = true;
                  label137:
                  localDownloadObject.secret = bool;
                }
              }
              else
              {
                localArrayList.add(localDownloadObject);
                continue;
                return;
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        while (((TLRPC.MessageMedia)localObject).photo != null)
        {
          localDownloadObject.object = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.MessageMedia)localObject).photo.sizes, AndroidUtilities.getPhotoSize());
          break;
          label187:
          bool = false;
          break label137;
          localSQLiteCursor.dispose();
          localObject = new org/telegram/messenger/MessagesStorage$73$1;
          ((1)localObject).<init>(this, localException);
          AndroidUtilities.runOnUIThread((Runnable)localObject);
        }
      }
    });
  }
  
  public TLRPC.EncryptedChat getEncryptedChat(int paramInt)
  {
    localObject1 = null;
    try
    {
      ArrayList localArrayList = new java/util/ArrayList;
      localArrayList.<init>();
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      getEncryptedChatsInternal("" + paramInt, localArrayList, null);
      localObject2 = localObject1;
      if (!localArrayList.isEmpty()) {
        localObject2 = (TLRPC.EncryptedChat)localArrayList.get(0);
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Object localObject2;
        FileLog.e(localException);
        Object localObject3 = localObject1;
      }
    }
    return (TLRPC.EncryptedChat)localObject2;
  }
  
  public void getEncryptedChat(final int paramInt, final CountDownLatch paramCountDownLatch, final ArrayList<TLObject> paramArrayList)
  {
    if ((paramCountDownLatch == null) || (paramArrayList == null)) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            ArrayList localArrayList1 = new java/util/ArrayList;
            localArrayList1.<init>();
            ArrayList localArrayList2 = new java/util/ArrayList;
            localArrayList2.<init>();
            Object localObject2 = MessagesStorage.this;
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            ((MessagesStorage)localObject2).getEncryptedChatsInternal("" + paramInt, localArrayList2, localArrayList1);
            if ((!localArrayList2.isEmpty()) && (!localArrayList1.isEmpty()))
            {
              localObject2 = new java/util/ArrayList;
              ((ArrayList)localObject2).<init>();
              MessagesStorage.this.getUsersInternal(TextUtils.join(",", localArrayList1), (ArrayList)localObject2);
              if (!((ArrayList)localObject2).isEmpty())
              {
                paramArrayList.add(localArrayList2.get(0));
                paramArrayList.add(((ArrayList)localObject2).get(0));
              }
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
              paramCountDownLatch.countDown();
            }
          }
          finally
          {
            paramCountDownLatch.countDown();
          }
        }
      });
    }
  }
  
  public void getEncryptedChatsInternal(String paramString, ArrayList<TLRPC.EncryptedChat> paramArrayList, ArrayList<Integer> paramArrayList1)
    throws Exception
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayList == null)) {}
    for (;;)
    {
      return;
      paramString = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id, mtproto_seq FROM enc_chats WHERE uid IN(%s)", new Object[] { paramString }), new Object[0]);
      while (paramString.next()) {
        try
        {
          NativeByteBuffer localNativeByteBuffer = paramString.byteBufferValue(0);
          if (localNativeByteBuffer != null)
          {
            TLRPC.EncryptedChat localEncryptedChat = TLRPC.EncryptedChat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (localEncryptedChat != null)
            {
              localEncryptedChat.user_id = paramString.intValue(1);
              if ((paramArrayList1 != null) && (!paramArrayList1.contains(Integer.valueOf(localEncryptedChat.user_id)))) {
                paramArrayList1.add(Integer.valueOf(localEncryptedChat.user_id));
              }
              localEncryptedChat.a_or_b = paramString.byteArrayValue(2);
              localEncryptedChat.auth_key = paramString.byteArrayValue(3);
              localEncryptedChat.ttl = paramString.intValue(4);
              localEncryptedChat.layer = paramString.intValue(5);
              localEncryptedChat.seq_in = paramString.intValue(6);
              localEncryptedChat.seq_out = paramString.intValue(7);
              int i = paramString.intValue(8);
              localEncryptedChat.key_use_count_in = ((short)(short)(i >> 16));
              localEncryptedChat.key_use_count_out = ((short)(short)i);
              localEncryptedChat.exchange_id = paramString.longValue(9);
              localEncryptedChat.key_create_date = paramString.intValue(10);
              localEncryptedChat.future_key_fingerprint = paramString.longValue(11);
              localEncryptedChat.future_auth_key = paramString.byteArrayValue(12);
              localEncryptedChat.key_hash = paramString.byteArrayValue(13);
              localEncryptedChat.in_seq_no = paramString.intValue(14);
              i = paramString.intValue(15);
              if (i != 0) {
                localEncryptedChat.admin_id = i;
              }
              localEncryptedChat.mtproto_seq = paramString.intValue(16);
              paramArrayList.add(localEncryptedChat);
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
      paramString.dispose();
    }
  }
  
  public int getLastDateValue()
  {
    ensureOpened();
    return this.lastDateValue;
  }
  
  public int getLastPtsValue()
  {
    ensureOpened();
    return this.lastPtsValue;
  }
  
  public int getLastQtsValue()
  {
    ensureOpened();
    return this.lastQtsValue;
  }
  
  public int getLastSecretVersion()
  {
    ensureOpened();
    return this.lastSecretVersion;
  }
  
  public int getLastSeqValue()
  {
    ensureOpened();
    return this.lastSeqValue;
  }
  
  public void getMessages(final long paramLong, final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final int paramInt5, int paramInt6, final boolean paramBoolean, final int paramInt7)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        TLRPC.TL_messages_messages localTL_messages_messages = new TLRPC.TL_messages_messages();
        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        int i1 = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        int i9 = 0;
        int i10 = 0;
        int i11 = paramInt1;
        int i12 = 0;
        int i13 = 0;
        int i14 = 0;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        int i18 = 0;
        int i19 = 0;
        int i20 = 0;
        int i21 = 0;
        int i22 = 0;
        int i23 = 0;
        int i24 = 0;
        int i25 = 0;
        int i26 = 0;
        int i27 = 0;
        boolean bool1 = false;
        boolean bool2 = false;
        boolean bool3 = false;
        boolean bool4 = false;
        boolean bool5 = false;
        boolean bool6 = false;
        int i28 = 0;
        int i29 = 0;
        int i30 = 0;
        int i31 = 0;
        int i32 = 0;
        int i33 = 0;
        int i34 = 0;
        int i35 = 0;
        long l1 = paramInt2;
        int i36 = paramInt2;
        int i37 = 0;
        int i38 = paramInt2;
        int i39 = 0;
        if (paramBoolean) {
          i39 = -(int)paramLong;
        }
        long l2 = l1;
        if (l1 != 0L)
        {
          l2 = l1;
          if (i39 != 0) {
            l2 = l1 | i39 << 32;
          }
        }
        boolean bool7 = false;
        boolean bool8 = false;
        boolean bool9 = false;
        int i40;
        if (paramLong == 777000L)
        {
          i40 = 10;
          i41 = i11;
          i42 = i38;
          i43 = i15;
          i44 = i22;
          i45 = j;
          i46 = i29;
          bool10 = bool7;
          bool11 = bool2;
          i47 = i6;
          i48 = i11;
          i49 = i38;
          i50 = i18;
          i51 = i23;
          i52 = n;
          i53 = i32;
          bool12 = bool8;
          bool13 = bool5;
          i54 = i9;
        }
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        Object localObject1;
        SparseArray localSparseArray;
        LongSparseArray localLongSparseArray;
        int i55;
        Object localObject8;
        for (;;)
        {
          TLRPC.Message localMessage;
          try
          {
            localArrayList1 = new java/util/ArrayList;
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            localArrayList1.<init>();
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            localArrayList2 = new java/util/ArrayList;
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            localArrayList2.<init>();
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            localObject1 = new java/util/ArrayList;
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            ((ArrayList)localObject1).<init>();
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            localSparseArray = new android/util/SparseArray;
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            localSparseArray.<init>();
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            localLongSparseArray = new android/util/LongSparseArray;
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            localLongSparseArray.<init>();
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            i55 = (int)paramLong;
            if (i55 == 0) {
              break label19295;
            }
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            if (paramInt4 == 3)
            {
              i41 = i11;
              i42 = i38;
              i43 = i15;
              i44 = i22;
              i45 = j;
              i46 = i29;
              bool10 = bool7;
              bool11 = bool2;
              i47 = i6;
              i48 = i11;
              i49 = i38;
              i50 = i18;
              i51 = i23;
              i52 = n;
              i53 = i32;
              bool12 = bool8;
              bool13 = bool5;
              i54 = i9;
              if (paramInt3 == 0)
              {
                i41 = i11;
                i42 = i38;
                i43 = i15;
                i44 = i22;
                i45 = j;
                i46 = i29;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i6;
                i48 = i11;
                i49 = i38;
                i50 = i18;
                i51 = i23;
                i52 = n;
                i53 = i32;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i9;
                Object localObject2 = MessagesStorage.this.database;
                i41 = i11;
                i42 = i38;
                i43 = i15;
                i44 = i22;
                i45 = j;
                i46 = i29;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i6;
                i48 = i11;
                i49 = i38;
                i50 = i18;
                i51 = i23;
                i52 = n;
                i53 = i32;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i9;
                Object localObject4 = new java/lang/StringBuilder;
                i41 = i11;
                i42 = i38;
                i43 = i15;
                i44 = i22;
                i45 = j;
                i46 = i29;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i6;
                i48 = i11;
                i49 = i38;
                i50 = i18;
                i51 = i23;
                i52 = n;
                i53 = i32;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i9;
                ((StringBuilder)localObject4).<init>();
                i41 = i11;
                i42 = i38;
                i43 = i15;
                i44 = i22;
                i45 = j;
                i46 = i29;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i6;
                i48 = i11;
                i49 = i38;
                i50 = i18;
                i51 = i23;
                i52 = n;
                i53 = i32;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i9;
                localObject4 = ((SQLiteDatabase)localObject2).queryFinalized("SELECT inbox_max, unread_count, date, unread_count_i FROM dialogs WHERE did = " + paramLong, new Object[0]);
                i27 = i35;
                i33 = i10;
                i41 = i11;
                i42 = i38;
                i43 = i15;
                i44 = i22;
                i45 = j;
                i46 = i29;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i6;
                i48 = i11;
                i49 = i38;
                i50 = i18;
                i51 = i23;
                i52 = n;
                i53 = i32;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i9;
                if (((SQLiteCursor)localObject4).next())
                {
                  i41 = i11;
                  i42 = i38;
                  i43 = i15;
                  i44 = i22;
                  i45 = j;
                  i46 = i29;
                  bool10 = bool7;
                  bool11 = bool2;
                  i47 = i6;
                  i48 = i11;
                  i49 = i38;
                  i50 = i18;
                  i51 = i23;
                  i52 = n;
                  i53 = i32;
                  bool12 = bool8;
                  bool13 = bool5;
                  i54 = i9;
                  i21 = ((SQLiteCursor)localObject4).intValue(0) + 1;
                  i41 = i11;
                  i42 = i38;
                  i43 = i21;
                  i44 = i22;
                  i45 = j;
                  i46 = i29;
                  bool10 = bool7;
                  bool11 = bool2;
                  i47 = i6;
                  i48 = i11;
                  i49 = i38;
                  i50 = i21;
                  i51 = i23;
                  i52 = n;
                  i53 = i32;
                  bool12 = bool8;
                  bool13 = bool5;
                  i54 = i9;
                  i3 = ((SQLiteCursor)localObject4).intValue(1);
                  i41 = i11;
                  i42 = i38;
                  i43 = i21;
                  i44 = i22;
                  i45 = i3;
                  i46 = i29;
                  bool10 = bool7;
                  bool11 = bool2;
                  i47 = i6;
                  i48 = i11;
                  i49 = i38;
                  i50 = i21;
                  i51 = i23;
                  i52 = i3;
                  i53 = i32;
                  bool12 = bool8;
                  bool13 = bool5;
                  i54 = i9;
                  i27 = ((SQLiteCursor)localObject4).intValue(2);
                  i41 = i11;
                  i42 = i38;
                  i43 = i21;
                  i44 = i22;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool7;
                  bool11 = bool2;
                  i47 = i6;
                  i48 = i11;
                  i49 = i38;
                  i50 = i21;
                  i51 = i23;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool8;
                  bool13 = bool5;
                  i54 = i9;
                  i33 = ((SQLiteCursor)localObject4).intValue(3);
                }
                i41 = i11;
                i42 = i38;
                i43 = i21;
                i44 = i22;
                i45 = i3;
                i46 = i27;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i33;
                i48 = i11;
                i49 = i38;
                i50 = i21;
                i51 = i23;
                i52 = i3;
                i53 = i27;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i33;
                ((SQLiteCursor)localObject4).dispose();
                i12 = i13;
                l1 = l2;
                i5 = i36;
                bool1 = bool6;
                i10 = i11;
                i16 = 0;
                i26 = 0;
                i25 = 0;
                i1 = 0;
                i24 = 0;
                i41 = i10;
                i42 = i38;
                i43 = i21;
                i44 = i26;
                i45 = i3;
                i46 = i27;
                bool10 = bool7;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i38;
                i50 = i21;
                i51 = i25;
                i52 = i3;
                i53 = i27;
                bool12 = bool8;
                bool13 = bool1;
                i54 = i33;
                localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                i41 = i10;
                i42 = i38;
                i43 = i21;
                i44 = i26;
                i45 = i3;
                i46 = i27;
                bool10 = bool7;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i38;
                i50 = i21;
                i51 = i25;
                i52 = i3;
                i53 = i27;
                bool12 = bool8;
                bool13 = bool1;
                i54 = i33;
                if (!((SQLiteCursor)localObject4).next()) {
                  break label14023;
                }
                i41 = i10;
                i42 = i38;
                i43 = i21;
                i44 = i26;
                i45 = i3;
                i46 = i27;
                bool10 = bool7;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i38;
                i50 = i21;
                i51 = i25;
                i52 = i3;
                i53 = i27;
                bool12 = bool8;
                bool13 = bool1;
                i54 = i33;
                if (((SQLiteCursor)localObject4).intValue(0) != 1) {
                  break label14017;
                }
                bool3 = true;
                i41 = i10;
                i42 = i38;
                i43 = i21;
                i44 = i26;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i38;
                i50 = i21;
                i51 = i25;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((SQLiteCursor)localObject4).dispose();
                i41 = i10;
                i42 = i38;
                i43 = i21;
                i44 = i26;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i38;
                i50 = i21;
                i51 = i25;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (paramInt4 != 3)
                {
                  i41 = i10;
                  i42 = i38;
                  i43 = i21;
                  i44 = i26;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i38;
                  i50 = i21;
                  i51 = i25;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (paramInt4 != 4)
                  {
                    if (!bool1) {
                      break label16526;
                    }
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i26;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i25;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (paramInt4 != 2) {
                      break label16526;
                    }
                  }
                }
                i41 = i10;
                i42 = i38;
                i43 = i21;
                i44 = i26;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i38;
                i50 = i21;
                i51 = i25;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                i16 = i24;
                i41 = i10;
                i42 = i38;
                i43 = i21;
                i44 = i26;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i38;
                i50 = i21;
                i51 = i25;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (((SQLiteCursor)localObject4).next())
                {
                  i41 = i10;
                  i42 = i38;
                  i43 = i21;
                  i44 = i26;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i38;
                  i50 = i21;
                  i51 = i25;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  i16 = ((SQLiteCursor)localObject4).intValue(0);
                }
                i41 = i10;
                i42 = i38;
                i43 = i21;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i38;
                i50 = i21;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((SQLiteCursor)localObject4).dispose();
                i25 = i38;
                i24 = i5;
                l3 = l1;
                i41 = i10;
                i42 = i38;
                i43 = i21;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i38;
                i50 = i21;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (paramInt4 == 4)
                {
                  i25 = i38;
                  i24 = i5;
                  l3 = l1;
                  i41 = i10;
                  i42 = i38;
                  i43 = i21;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i38;
                  i50 = i21;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (paramInt5 != 0)
                  {
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND date <= %d AND mid > 0", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt5) }), new Object[0]);
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (!((SQLiteCursor)localObject4).next()) {
                      break label15020;
                    }
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    i26 = ((SQLiteCursor)localObject4).intValue(0);
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    ((SQLiteCursor)localObject4).dispose();
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND date >= %d AND mid > 0", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt5) }), new Object[0]);
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (!((SQLiteCursor)localObject4).next()) {
                      break label15026;
                    }
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    i1 = ((SQLiteCursor)localObject4).intValue(0);
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    ((SQLiteCursor)localObject4).dispose();
                    i25 = i38;
                    i24 = i5;
                    l3 = l1;
                    if (i26 != -1)
                    {
                      i25 = i38;
                      i24 = i5;
                      l3 = l1;
                      if (i1 != -1)
                      {
                        if (i26 != i1) {
                          break label15032;
                        }
                        l3 = l1;
                        i24 = i26;
                        i25 = i38;
                      }
                    }
                  }
                }
                if (i24 == 0) {
                  break label15717;
                }
                i26 = 1;
                i50 = i26;
                if (i26 != 0)
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i21;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i21;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i24), Integer.valueOf(i24) }), new Object[0]);
                  i41 = i10;
                  i42 = i25;
                  i43 = i21;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i21;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (((SQLiteCursor)localObject4).next()) {
                    i26 = 0;
                  }
                  i41 = i10;
                  i42 = i25;
                  i43 = i21;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i21;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  ((SQLiteCursor)localObject4).dispose();
                  i50 = i26;
                }
                if (i50 == 0) {
                  break label15892;
                }
                l2 = 0L;
                l1 = 1L;
                i41 = i10;
                i42 = i25;
                i43 = i21;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i21;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i24) }), new Object[0]);
                i41 = i10;
                i42 = i25;
                i43 = i21;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i21;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (((SQLiteCursor)localObject4).next())
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i21;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i21;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  l4 = ((SQLiteCursor)localObject4).intValue(0);
                  l2 = l4;
                  if (i39 != 0) {
                    l2 = l4 | i39 << 32;
                  }
                }
                i41 = i10;
                i42 = i25;
                i43 = i21;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i21;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((SQLiteCursor)localObject4).dispose();
                i41 = i10;
                i42 = i25;
                i43 = i21;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i21;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i24) }), new Object[0]);
                i41 = i10;
                i42 = i25;
                i43 = i21;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i21;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (((SQLiteCursor)localObject4).next())
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i21;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i21;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  l4 = ((SQLiteCursor)localObject4).intValue(0);
                  l1 = l4;
                  if (i39 != 0) {
                    l1 = l4 | i39 << 32;
                  }
                }
                i41 = i10;
                i42 = i25;
                i43 = i21;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i21;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((SQLiteCursor)localObject4).dispose();
                if ((l2 == 0L) && (l1 == 1L)) {
                  break label15723;
                }
                long l4 = l2;
                if (l2 == 0L)
                {
                  l4 = 1000000000L;
                  if (i39 != 0) {
                    l4 = 0x3B9ACA00 | i39 << 32;
                  }
                }
                i41 = i10;
                i42 = i25;
                i43 = i21;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i21;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND m.mid >= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", new Object[] { Long.valueOf(paramLong), Long.valueOf(l3), Long.valueOf(l1), Integer.valueOf(i10 / 2), Long.valueOf(paramLong), Long.valueOf(l3), Long.valueOf(l4), Integer.valueOf(i10 / 2) }), new Object[0]);
                i1 = i37;
                i26 = i21;
                if (localObject4 == null) {
                  break label24341;
                }
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (!((SQLiteCursor)localObject4).next()) {
                  break label24264;
                }
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localObject2 = ((SQLiteCursor)localObject4).byteBufferValue(1);
                if (localObject2 == null) {
                  continue;
                }
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localMessage.readAttachPath((AbstractSerializedData)localObject2, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((NativeByteBuffer)localObject2).reuse();
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                MessageObject.setUnreadFlags(localMessage, ((SQLiteCursor)localObject4).intValue(0));
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localMessage.id = ((SQLiteCursor)localObject4).intValue(3);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localMessage.date = ((SQLiteCursor)localObject4).intValue(4);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localMessage.dialog_id = paramLong;
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if ((localMessage.flags & 0x400) != 0)
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  localMessage.views = ((SQLiteCursor)localObject4).intValue(7);
                }
                if (i55 != 0)
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (localMessage.ttl == 0)
                  {
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localMessage.ttl = ((SQLiteCursor)localObject4).intValue(8);
                  }
                }
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (((SQLiteCursor)localObject4).intValue(9) != 0)
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  localMessage.mentioned = true;
                }
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localTL_messages_messages.messages.add(localMessage);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                MessagesStorage.addUsersAndChatsFromMessage(localMessage, localArrayList1, localArrayList2);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (localMessage.reply_to_msg_id == 0)
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (localMessage.reply_to_random_id == 0L) {}
                }
                else
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (!((SQLiteCursor)localObject4).isNull(6))
                  {
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localObject2 = ((SQLiteCursor)localObject4).byteBufferValue(6);
                    if (localObject2 != null)
                    {
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      localMessage.replyMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      localMessage.replyMessage.readAttachPath((AbstractSerializedData)localObject2, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      ((NativeByteBuffer)localObject2).reuse();
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      if (localMessage.replyMessage != null)
                      {
                        i41 = i10;
                        i42 = i25;
                        i43 = i26;
                        i44 = i16;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i25;
                        i50 = i26;
                        i51 = i16;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        if (MessageObject.isMegagroup(localMessage))
                        {
                          i41 = i10;
                          i42 = i25;
                          i43 = i26;
                          i44 = i16;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i25;
                          i50 = i26;
                          i51 = i16;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          localObject2 = localMessage.replyMessage;
                          i41 = i10;
                          i42 = i25;
                          i43 = i26;
                          i44 = i16;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i25;
                          i50 = i26;
                          i51 = i16;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          ((TLRPC.Message)localObject2).flags |= 0x80000000;
                        }
                        i41 = i10;
                        i42 = i25;
                        i43 = i26;
                        i44 = i16;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i25;
                        i50 = i26;
                        i51 = i16;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        MessagesStorage.addUsersAndChatsFromMessage(localMessage.replyMessage, localArrayList1, localArrayList2);
                      }
                    }
                  }
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (localMessage.replyMessage == null)
                  {
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (localMessage.reply_to_msg_id == 0) {
                      break label23673;
                    }
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    l1 = localMessage.reply_to_msg_id;
                    l2 = l1;
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (localMessage.to_id.channel_id != 0)
                    {
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      l2 = l1 | localMessage.to_id.channel_id << 32;
                    }
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (!((ArrayList)localObject1).contains(Long.valueOf(l2)))
                    {
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      ((ArrayList)localObject1).add(Long.valueOf(l2));
                    }
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localObject8 = (ArrayList)localSparseArray.get(localMessage.reply_to_msg_id);
                    localObject2 = localObject8;
                    if (localObject8 == null)
                    {
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      localObject2 = new java/util/ArrayList;
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      ((ArrayList)localObject2).<init>();
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      localSparseArray.put(localMessage.reply_to_msg_id, localObject2);
                    }
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    ((ArrayList)localObject2).add(localMessage);
                  }
                }
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localMessage.send_state = ((SQLiteCursor)localObject4).intValue(2);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (localMessage.id > 0)
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (localMessage.send_state != 0)
                  {
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localMessage.send_state = 0;
                  }
                }
                if (i55 == 0)
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (!((SQLiteCursor)localObject4).isNull(5))
                  {
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localMessage.random_id = ((SQLiteCursor)localObject4).longValue(5);
                  }
                }
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                bool2 = MessageObject.isSecretPhotoOrVideo(localMessage);
                if (!bool2) {
                  continue;
                }
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                try
                {
                  localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT date FROM enc_tasks_v2 WHERE mid = %d", new Object[] { Integer.valueOf(localMessage.id) }), new Object[0]);
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (((SQLiteCursor)localObject2).next())
                  {
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localMessage.destroyTime = ((SQLiteCursor)localObject2).intValue(0);
                  }
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  ((SQLiteCursor)localObject2).dispose();
                }
                catch (Exception localException1)
                {
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  FileLog.e(localException1);
                }
                continue;
              }
            }
          }
          catch (Exception localException2)
          {
            i48 = i41;
            i49 = i42;
            i50 = i43;
            i51 = i44;
            i52 = i45;
            i53 = i46;
            bool12 = bool10;
            bool13 = bool11;
            i54 = i47;
            localTL_messages_messages.messages.clear();
            i48 = i41;
            i49 = i42;
            i50 = i43;
            i51 = i44;
            i52 = i45;
            i53 = i46;
            bool12 = bool10;
            bool13 = bool11;
            i54 = i47;
            localTL_messages_messages.chats.clear();
            i48 = i41;
            i49 = i42;
            i50 = i43;
            i51 = i44;
            i52 = i45;
            i53 = i46;
            bool12 = bool10;
            bool13 = bool11;
            i54 = i47;
            localTL_messages_messages.users.clear();
            i48 = i41;
            i49 = i42;
            i50 = i43;
            i51 = i44;
            i52 = i45;
            i53 = i46;
            bool12 = bool10;
            bool13 = bool11;
            i54 = i47;
            FileLog.e(localException2);
            return;
            i40 = 1;
            break;
            i10 = i11;
            i21 = i14;
            i3 = i;
            i27 = i28;
            bool1 = bool6;
            i33 = i4;
            i5 = i36;
            l1 = l2;
            i12 = i13;
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            if (paramInt4 == 1) {
              continue;
            }
            i10 = i11;
            i21 = i14;
            i3 = i;
            i27 = i28;
            bool1 = bool6;
            i33 = i4;
            i5 = i36;
            l1 = l2;
            i12 = i13;
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            if (paramInt4 == 3) {
              continue;
            }
            i10 = i11;
            i21 = i14;
            i3 = i;
            i27 = i28;
            bool1 = bool6;
            i33 = i4;
            i5 = i36;
            l1 = l2;
            i12 = i13;
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            if (paramInt4 == 4) {
              continue;
            }
            i10 = i11;
            i21 = i14;
            i3 = i;
            i27 = i28;
            bool1 = bool6;
            i33 = i4;
            i5 = i36;
            l1 = l2;
            i12 = i13;
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            if (paramInt3 != 0) {
              continue;
            }
            i41 = i11;
            i42 = i38;
            i43 = i15;
            i44 = i22;
            i45 = j;
            i46 = i29;
            bool10 = bool7;
            bool11 = bool2;
            i47 = i6;
            i21 = i17;
            i25 = m;
            i26 = i31;
            bool1 = bool4;
            i24 = i8;
            i1 = i36;
            l1 = l2;
            i48 = i11;
            i49 = i38;
            i50 = i18;
            i51 = i23;
            i52 = n;
            i53 = i32;
            bool12 = bool8;
            bool13 = bool5;
            i54 = i9;
            Object localObject5;
            if (paramInt4 == 2)
            {
              i41 = i11;
              i42 = i38;
              i43 = i15;
              i44 = i22;
              i45 = j;
              i46 = i29;
              bool10 = bool7;
              bool11 = bool2;
              i47 = i6;
              i48 = i11;
              i49 = i38;
              i50 = i18;
              i51 = i23;
              i52 = n;
              i53 = i32;
              bool12 = bool8;
              bool13 = bool5;
              i54 = i9;
              localObject5 = MessagesStorage.this.database;
              i41 = i11;
              i42 = i38;
              i43 = i15;
              i44 = i22;
              i45 = j;
              i46 = i29;
              bool10 = bool7;
              bool11 = bool2;
              i47 = i6;
              i48 = i11;
              i49 = i38;
              i50 = i18;
              i51 = i23;
              i52 = n;
              i53 = i32;
              bool12 = bool8;
              bool13 = bool5;
              i54 = i9;
              localObject3 = new java/lang/StringBuilder;
              i41 = i11;
              i42 = i38;
              i43 = i15;
              i44 = i22;
              i45 = j;
              i46 = i29;
              bool10 = bool7;
              bool11 = bool2;
              i47 = i6;
              i48 = i11;
              i49 = i38;
              i50 = i18;
              i51 = i23;
              i52 = n;
              i53 = i32;
              bool12 = bool8;
              bool13 = bool5;
              i54 = i9;
              ((StringBuilder)localObject3).<init>();
              i41 = i11;
              i42 = i38;
              i43 = i15;
              i44 = i22;
              i45 = j;
              i46 = i29;
              bool10 = bool7;
              bool11 = bool2;
              i47 = i6;
              i48 = i11;
              i49 = i38;
              i50 = i18;
              i51 = i23;
              i52 = n;
              i53 = i32;
              bool12 = bool8;
              bool13 = bool5;
              i54 = i9;
              localObject5 = ((SQLiteDatabase)localObject5).queryFinalized("SELECT inbox_max, unread_count, date, unread_count_i FROM dialogs WHERE did = " + paramLong, new Object[0]);
              i41 = i11;
              i42 = i38;
              i43 = i15;
              i44 = i22;
              i45 = j;
              i46 = i29;
              bool10 = bool7;
              bool11 = bool2;
              i47 = i6;
              i3 = k;
              i33 = i30;
              i10 = i7;
              i27 = i36;
              i48 = i11;
              i49 = i38;
              i50 = i18;
              i51 = i23;
              i52 = n;
              i53 = i32;
              bool12 = bool8;
              bool13 = bool5;
              i54 = i9;
              if (((SQLiteCursor)localObject5).next())
              {
                i41 = i11;
                i42 = i38;
                i43 = i15;
                i44 = i22;
                i45 = j;
                i46 = i29;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i6;
                i48 = i11;
                i49 = i38;
                i50 = i18;
                i51 = i23;
                i52 = n;
                i53 = i32;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i9;
                i21 = ((SQLiteCursor)localObject5).intValue(0);
                i24 = i21;
                l1 = i21;
                i41 = i11;
                i42 = i38;
                i43 = i21;
                i44 = i22;
                i45 = j;
                i46 = i29;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i6;
                i48 = i11;
                i49 = i38;
                i50 = i21;
                i51 = i23;
                i52 = n;
                i53 = i32;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i9;
                i26 = ((SQLiteCursor)localObject5).intValue(1);
                i41 = i11;
                i42 = i38;
                i43 = i21;
                i44 = i22;
                i45 = i26;
                i46 = i29;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i6;
                i48 = i11;
                i49 = i38;
                i50 = i21;
                i51 = i23;
                i52 = i26;
                i53 = i32;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i9;
                i25 = ((SQLiteCursor)localObject5).intValue(2);
                i41 = i11;
                i42 = i38;
                i43 = i21;
                i44 = i22;
                i45 = i26;
                i46 = i25;
                bool10 = bool7;
                bool11 = bool2;
                i47 = i6;
                i48 = i11;
                i49 = i38;
                i50 = i21;
                i51 = i23;
                i52 = i26;
                i53 = i25;
                bool12 = bool8;
                bool13 = bool5;
                i54 = i9;
                i1 = ((SQLiteCursor)localObject5).intValue(3);
                bool10 = true;
                i16 = i21;
                i3 = i26;
                i33 = i25;
                bool3 = bool10;
                i10 = i1;
                i27 = i24;
                l2 = l1;
                if (l1 != 0L)
                {
                  i16 = i21;
                  i3 = i26;
                  i33 = i25;
                  bool3 = bool10;
                  i10 = i1;
                  i27 = i24;
                  l2 = l1;
                  if (i39 != 0)
                  {
                    l2 = l1 | i39 << 32;
                    i27 = i24;
                    i10 = i1;
                    bool3 = bool10;
                    i33 = i25;
                    i3 = i26;
                    i16 = i21;
                  }
                }
              }
              i41 = i11;
              i42 = i38;
              i43 = i16;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i48 = i11;
              i49 = i38;
              i50 = i16;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              ((SQLiteCursor)localObject5).dispose();
              if (bool3) {
                continue;
              }
              i41 = i11;
              i42 = i38;
              i43 = i16;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i48 = i11;
              i49 = i38;
              i50 = i16;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              localObject5 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
              i41 = i11;
              i42 = i38;
              i43 = i16;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i5 = i16;
              i36 = i33;
              i48 = i11;
              i49 = i38;
              i50 = i16;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              if (((SQLiteCursor)localObject5).next())
              {
                i41 = i11;
                i42 = i38;
                i43 = i16;
                i44 = i22;
                i45 = i3;
                i46 = i33;
                bool10 = bool7;
                bool11 = bool3;
                i47 = i10;
                i48 = i11;
                i49 = i38;
                i50 = i16;
                i51 = i23;
                i52 = i3;
                i53 = i33;
                bool12 = bool8;
                bool13 = bool3;
                i54 = i10;
                i5 = ((SQLiteCursor)localObject5).intValue(0);
                i41 = i11;
                i42 = i38;
                i43 = i5;
                i44 = i22;
                i45 = i3;
                i46 = i33;
                bool10 = bool7;
                bool11 = bool3;
                i47 = i10;
                i48 = i11;
                i49 = i38;
                i50 = i5;
                i51 = i23;
                i52 = i3;
                i53 = i33;
                bool12 = bool8;
                bool13 = bool3;
                i54 = i10;
                i36 = ((SQLiteCursor)localObject5).intValue(1);
              }
              i41 = i11;
              i42 = i38;
              i43 = i5;
              i44 = i22;
              i45 = i3;
              i46 = i36;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i48 = i11;
              i49 = i38;
              i50 = i5;
              i51 = i23;
              i52 = i3;
              i53 = i36;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              ((SQLiteCursor)localObject5).dispose();
              i21 = i5;
              i25 = i3;
              i26 = i36;
              bool1 = bool3;
              i24 = i10;
              i1 = i27;
              l1 = l2;
              if (i5 != 0)
              {
                i41 = i11;
                i42 = i38;
                i43 = i5;
                i44 = i22;
                i45 = i3;
                i46 = i36;
                bool10 = bool7;
                bool11 = bool3;
                i47 = i10;
                i48 = i11;
                i49 = i38;
                i50 = i5;
                i51 = i23;
                i52 = i3;
                i53 = i36;
                bool12 = bool8;
                bool13 = bool3;
                i54 = i10;
                localObject5 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid >= %d AND out = 0 AND read_state IN(0,2)", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i5) }), new Object[0]);
                i41 = i11;
                i42 = i38;
                i43 = i5;
                i44 = i22;
                i45 = i3;
                i46 = i36;
                bool10 = bool7;
                bool11 = bool3;
                i47 = i10;
                i25 = i3;
                i48 = i11;
                i49 = i38;
                i50 = i5;
                i51 = i23;
                i52 = i3;
                i53 = i36;
                bool12 = bool8;
                bool13 = bool3;
                i54 = i10;
                if (((SQLiteCursor)localObject5).next())
                {
                  i41 = i11;
                  i42 = i38;
                  i43 = i5;
                  i44 = i22;
                  i45 = i3;
                  i46 = i36;
                  bool10 = bool7;
                  bool11 = bool3;
                  i47 = i10;
                  i48 = i11;
                  i49 = i38;
                  i50 = i5;
                  i51 = i23;
                  i52 = i3;
                  i53 = i36;
                  bool12 = bool8;
                  bool13 = bool3;
                  i54 = i10;
                  i25 = ((SQLiteCursor)localObject5).intValue(0);
                }
                i41 = i11;
                i42 = i38;
                i43 = i5;
                i44 = i22;
                i45 = i25;
                i46 = i36;
                bool10 = bool7;
                bool11 = bool3;
                i47 = i10;
                i48 = i11;
                i49 = i38;
                i50 = i5;
                i51 = i23;
                i52 = i25;
                i53 = i36;
                bool12 = bool8;
                bool13 = bool3;
                i54 = i10;
                ((SQLiteCursor)localObject5).dispose();
                l1 = l2;
                i1 = i27;
                i24 = i10;
                bool1 = bool3;
                i26 = i36;
                i21 = i5;
              }
            }
            if ((i11 <= i25) && (i25 >= i40)) {
              break label13984;
            }
            i41 = i11;
            i42 = i38;
            i43 = i21;
            i44 = i22;
            i45 = i25;
            i46 = i26;
            bool10 = bool7;
            bool11 = bool1;
            i47 = i24;
            i48 = i11;
            i49 = i38;
            i50 = i21;
            i51 = i23;
            i52 = i25;
            i53 = i26;
            bool12 = bool8;
            bool13 = bool1;
            i54 = i24;
            i16 = Math.max(i11, i25 + 10);
            i10 = i16;
            i3 = i25;
            i27 = i26;
            i33 = i24;
            i5 = i1;
            i12 = i13;
            if (i25 >= i40) {
              continue;
            }
            i3 = 0;
            i21 = 0;
            l1 = 0L;
            bool1 = false;
            i10 = i16;
            i27 = i26;
            i33 = i24;
            i5 = i1;
            i12 = i13;
            continue;
            if (i27 == 0)
            {
              i36 = 0;
              i41 = i11;
              i42 = i38;
              i43 = i16;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i48 = i11;
              i49 = i38;
              i50 = i16;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              localObject5 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid > 0 AND out = 0 AND read_state IN(0,2)", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
              i41 = i11;
              i42 = i38;
              i43 = i16;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i48 = i11;
              i49 = i38;
              i50 = i16;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              if (((SQLiteCursor)localObject5).next())
              {
                i41 = i11;
                i42 = i38;
                i43 = i16;
                i44 = i22;
                i45 = i3;
                i46 = i33;
                bool10 = bool7;
                bool11 = bool3;
                i47 = i10;
                i48 = i11;
                i49 = i38;
                i50 = i16;
                i51 = i23;
                i52 = i3;
                i53 = i33;
                bool12 = bool8;
                bool13 = bool3;
                i54 = i10;
                i36 = ((SQLiteCursor)localObject5).intValue(0);
              }
              i41 = i11;
              i42 = i38;
              i43 = i16;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i48 = i11;
              i49 = i38;
              i50 = i16;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              ((SQLiteCursor)localObject5).dispose();
              i21 = i16;
              i25 = i3;
              i26 = i33;
              bool1 = bool3;
              i24 = i10;
              i1 = i27;
              l1 = l2;
              if (i36 != i3) {
                continue;
              }
              i41 = i11;
              i42 = i38;
              i43 = i16;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i48 = i11;
              i49 = i38;
              i50 = i16;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              localObject5 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
              i41 = i11;
              i42 = i38;
              i43 = i16;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i21 = i16;
              i48 = i11;
              i49 = i38;
              i50 = i16;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              if (((SQLiteCursor)localObject5).next())
              {
                i41 = i11;
                i42 = i38;
                i43 = i16;
                i44 = i22;
                i45 = i3;
                i46 = i33;
                bool10 = bool7;
                bool11 = bool3;
                i47 = i10;
                i48 = i11;
                i49 = i38;
                i50 = i16;
                i51 = i23;
                i52 = i3;
                i53 = i33;
                bool12 = bool8;
                bool13 = bool3;
                i54 = i10;
                i16 = ((SQLiteCursor)localObject5).intValue(0);
                i50 = i16;
                l1 = i16;
                i21 = i16;
                i27 = i50;
                l2 = l1;
                if (l1 != 0L)
                {
                  i21 = i16;
                  i27 = i50;
                  l2 = l1;
                  if (i39 != 0)
                  {
                    l2 = l1 | i39 << 32;
                    i27 = i50;
                    i21 = i16;
                  }
                }
              }
              i41 = i11;
              i42 = i38;
              i43 = i21;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i48 = i11;
              i49 = i38;
              i50 = i21;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              ((SQLiteCursor)localObject5).dispose();
              i25 = i3;
              i26 = i33;
              bool1 = bool3;
              i24 = i10;
              i1 = i27;
              l1 = l2;
              continue;
            }
          }
          finally
          {
            MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedMessages(localTL_messages_messages, paramLong, i48, i49, paramInt5, true, paramInt7, i50, i51, i52, i53, paramInt4, paramBoolean, bool12, this.val$loadIndex, bool13, i54);
          }
          i41 = i11;
          i42 = i38;
          i43 = i16;
          i44 = i22;
          i45 = i3;
          i46 = i33;
          bool10 = bool7;
          bool11 = bool3;
          i47 = i10;
          i48 = i11;
          i49 = i38;
          i50 = i16;
          i51 = i23;
          i52 = i3;
          i53 = i33;
          bool12 = bool8;
          bool13 = bool3;
          i54 = i10;
          localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i27), Integer.valueOf(i27) }), new Object[0]);
          i41 = i11;
          i42 = i38;
          i43 = i16;
          i44 = i22;
          i45 = i3;
          i46 = i33;
          bool10 = bool7;
          bool11 = bool3;
          i47 = i10;
          i48 = i11;
          i49 = i38;
          i50 = i16;
          i51 = i23;
          i52 = i3;
          i53 = i33;
          bool12 = bool8;
          bool13 = bool3;
          i54 = i10;
          if (!((SQLiteCursor)localObject7).next()) {}
          for (i36 = 1;; i36 = 0)
          {
            i41 = i11;
            i42 = i38;
            i43 = i16;
            i44 = i22;
            i45 = i3;
            i46 = i33;
            bool10 = bool7;
            bool11 = bool3;
            i47 = i10;
            i48 = i11;
            i49 = i38;
            i50 = i16;
            i51 = i23;
            i52 = i3;
            i53 = i33;
            bool12 = bool8;
            bool13 = bool3;
            i54 = i10;
            ((SQLiteCursor)localObject7).dispose();
            i21 = i16;
            i25 = i3;
            i26 = i33;
            bool1 = bool3;
            i24 = i10;
            i1 = i27;
            l1 = l2;
            if (i36 == 0) {
              break;
            }
            i41 = i11;
            i42 = i38;
            i43 = i16;
            i44 = i22;
            i45 = i3;
            i46 = i33;
            bool10 = bool7;
            bool11 = bool3;
            i47 = i10;
            i48 = i11;
            i49 = i38;
            i50 = i16;
            i51 = i23;
            i52 = i3;
            i53 = i33;
            bool12 = bool8;
            bool13 = bool3;
            i54 = i10;
            localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i27) }), new Object[0]);
            i41 = i11;
            i42 = i38;
            i43 = i16;
            i44 = i22;
            i45 = i3;
            i46 = i33;
            bool10 = bool7;
            bool11 = bool3;
            i47 = i10;
            i48 = i11;
            i49 = i38;
            i50 = i16;
            i51 = i23;
            i52 = i3;
            i53 = i33;
            bool12 = bool8;
            bool13 = bool3;
            i54 = i10;
            if (((SQLiteCursor)localObject7).next())
            {
              i41 = i11;
              i42 = i38;
              i43 = i16;
              i44 = i22;
              i45 = i3;
              i46 = i33;
              bool10 = bool7;
              bool11 = bool3;
              i47 = i10;
              i48 = i11;
              i49 = i38;
              i50 = i16;
              i51 = i23;
              i52 = i3;
              i53 = i33;
              bool12 = bool8;
              bool13 = bool3;
              i54 = i10;
              i21 = ((SQLiteCursor)localObject7).intValue(0);
              l1 = i21;
              i27 = i21;
              l2 = l1;
              if (l1 != 0L)
              {
                i27 = i21;
                l2 = l1;
                if (i39 != 0)
                {
                  l2 = l1 | i39 << 32;
                  i27 = i21;
                }
              }
            }
            i41 = i11;
            i42 = i38;
            i43 = i16;
            i44 = i22;
            i45 = i3;
            i46 = i33;
            bool10 = bool7;
            bool11 = bool3;
            i47 = i10;
            i48 = i11;
            i49 = i38;
            i50 = i16;
            i51 = i23;
            i52 = i3;
            i53 = i33;
            bool12 = bool8;
            bool13 = bool3;
            i54 = i10;
            ((SQLiteCursor)localObject7).dispose();
            i21 = i16;
            i25 = i3;
            i26 = i33;
            bool1 = bool3;
            i24 = i10;
            i1 = i27;
            l1 = l2;
            break;
          }
          label13984:
          i12 = i25 - i11;
          i10 = i11 + 10;
          i3 = i25;
          i27 = i26;
          i33 = i24;
          i5 = i1;
          continue;
          label14017:
          bool3 = false;
          continue;
          label14023:
          i41 = i10;
          i42 = i38;
          i43 = i21;
          i44 = i26;
          i45 = i3;
          i46 = i27;
          bool10 = bool7;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i38;
          i50 = i21;
          i51 = i25;
          i52 = i3;
          i53 = i27;
          bool12 = bool8;
          bool13 = bool1;
          i54 = i33;
          ((SQLiteCursor)localObject7).dispose();
          i41 = i10;
          i42 = i38;
          i43 = i21;
          i44 = i26;
          i45 = i3;
          i46 = i27;
          bool10 = bool7;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i38;
          i50 = i21;
          i51 = i25;
          i52 = i3;
          i53 = i27;
          bool12 = bool8;
          bool13 = bool1;
          i54 = i33;
          localObject3 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
          i41 = i10;
          i42 = i38;
          i43 = i21;
          i44 = i26;
          i45 = i3;
          i46 = i27;
          bool10 = bool7;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i38;
          i50 = i21;
          i51 = i25;
          i52 = i3;
          i53 = i27;
          bool12 = bool8;
          bool13 = bool1;
          i54 = i33;
          if (((SQLiteCursor)localObject3).next())
          {
            i41 = i10;
            i42 = i38;
            i43 = i21;
            i44 = i26;
            i45 = i3;
            i46 = i27;
            bool10 = bool7;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i38;
            i50 = i21;
            i51 = i25;
            i52 = i3;
            i53 = i27;
            bool12 = bool8;
            bool13 = bool1;
            i54 = i33;
            i36 = ((SQLiteCursor)localObject3).intValue(0);
            if (i36 != 0)
            {
              i41 = i10;
              i42 = i38;
              i43 = i21;
              i44 = i26;
              i45 = i3;
              i46 = i27;
              bool10 = bool7;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i38;
              i50 = i21;
              i51 = i25;
              i52 = i3;
              i53 = i27;
              bool12 = bool8;
              bool13 = bool1;
              i54 = i33;
              localObject7 = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
              i41 = i10;
              i42 = i38;
              i43 = i21;
              i44 = i26;
              i45 = i3;
              i46 = i27;
              bool10 = bool7;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i38;
              i50 = i21;
              i51 = i25;
              i52 = i3;
              i53 = i27;
              bool12 = bool8;
              bool13 = bool1;
              i54 = i33;
              ((SQLitePreparedStatement)localObject7).requery();
              i41 = i10;
              i42 = i38;
              i43 = i21;
              i44 = i26;
              i45 = i3;
              i46 = i27;
              bool10 = bool7;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i38;
              i50 = i21;
              i51 = i25;
              i52 = i3;
              i53 = i27;
              bool12 = bool8;
              bool13 = bool1;
              i54 = i33;
              ((SQLitePreparedStatement)localObject7).bindLong(1, paramLong);
              i41 = i10;
              i42 = i38;
              i43 = i21;
              i44 = i26;
              i45 = i3;
              i46 = i27;
              bool10 = bool7;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i38;
              i50 = i21;
              i51 = i25;
              i52 = i3;
              i53 = i27;
              bool12 = bool8;
              bool13 = bool1;
              i54 = i33;
              ((SQLitePreparedStatement)localObject7).bindInteger(2, 0);
              i41 = i10;
              i42 = i38;
              i43 = i21;
              i44 = i26;
              i45 = i3;
              i46 = i27;
              bool10 = bool7;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i38;
              i50 = i21;
              i51 = i25;
              i52 = i3;
              i53 = i27;
              bool12 = bool8;
              bool13 = bool1;
              i54 = i33;
              ((SQLitePreparedStatement)localObject7).bindInteger(3, i36);
              i41 = i10;
              i42 = i38;
              i43 = i21;
              i44 = i26;
              i45 = i3;
              i46 = i27;
              bool10 = bool7;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i38;
              i50 = i21;
              i51 = i25;
              i52 = i3;
              i53 = i27;
              bool12 = bool8;
              bool13 = bool1;
              i54 = i33;
              ((SQLitePreparedStatement)localObject7).step();
              i41 = i10;
              i42 = i38;
              i43 = i21;
              i44 = i26;
              i45 = i3;
              i46 = i27;
              bool10 = bool7;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i38;
              i50 = i21;
              i51 = i25;
              i52 = i3;
              i53 = i27;
              bool12 = bool8;
              bool13 = bool1;
              i54 = i33;
              ((SQLitePreparedStatement)localObject7).dispose();
            }
          }
          i41 = i10;
          i42 = i38;
          i43 = i21;
          i44 = i26;
          i45 = i3;
          i46 = i27;
          bool10 = bool7;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i38;
          i50 = i21;
          i51 = i25;
          i52 = i3;
          i53 = i27;
          bool12 = bool8;
          bool13 = bool1;
          i54 = i33;
          ((SQLiteCursor)localObject3).dispose();
          bool3 = bool9;
          continue;
          label15020:
          i26 = -1;
          continue;
          label15026:
          i1 = -1;
          continue;
          label15032:
          i41 = i10;
          i42 = i38;
          i43 = i21;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i38;
          i50 = i21;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i26), Integer.valueOf(i26) }), new Object[0]);
          i41 = i10;
          i42 = i38;
          i43 = i21;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i38;
          i50 = i21;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          if (((SQLiteCursor)localObject7).next()) {
            i26 = -1;
          }
          i41 = i10;
          i42 = i38;
          i43 = i21;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i38;
          i50 = i21;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          ((SQLiteCursor)localObject7).dispose();
          i25 = i38;
          i24 = i5;
          long l3 = l1;
          if (i26 != -1)
          {
            i41 = i10;
            i42 = i38;
            i43 = i21;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i38;
            i50 = i21;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start <= %d AND end > %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i1), Integer.valueOf(i1) }), new Object[0]);
            i41 = i10;
            i42 = i38;
            i43 = i21;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i38;
            i50 = i21;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            i26 = i1;
            if (((SQLiteCursor)localObject7).next()) {
              i26 = -1;
            }
            i41 = i10;
            i42 = i38;
            i43 = i21;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i38;
            i50 = i21;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            ((SQLiteCursor)localObject7).dispose();
            i25 = i38;
            i24 = i5;
            l3 = l1;
            if (i26 != -1)
            {
              i50 = i26;
              i43 = i26;
              l2 = i26;
              i25 = i50;
              i24 = i43;
              l3 = l2;
              if (l2 != 0L)
              {
                i25 = i50;
                i24 = i43;
                l3 = l2;
                if (i39 != 0)
                {
                  l3 = l2 | i39 << 32;
                  i25 = i50;
                  i24 = i43;
                  continue;
                  label15717:
                  i26 = 0;
                  continue;
                  label15723:
                  i41 = i10;
                  i42 = i25;
                  i43 = i21;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i21;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", new Object[] { Long.valueOf(paramLong), Long.valueOf(l3), Integer.valueOf(i10 / 2), Long.valueOf(paramLong), Long.valueOf(l3), Integer.valueOf(i10 / 2) }), new Object[0]);
                  i26 = i21;
                  i1 = i37;
                  continue;
                  label15892:
                  i41 = i10;
                  i42 = i25;
                  i43 = i21;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i21;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  if (paramInt4 == 2)
                  {
                    i26 = 0;
                    i41 = i10;
                    i42 = i25;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid != 0 AND out = 0 AND read_state IN(0,2)", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                    i41 = i10;
                    i42 = i25;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (((SQLiteCursor)localObject7).next())
                    {
                      i41 = i10;
                      i42 = i25;
                      i43 = i21;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i21;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      i26 = ((SQLiteCursor)localObject7).intValue(0);
                    }
                    i41 = i10;
                    i42 = i25;
                    i43 = i21;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i21;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    ((SQLiteCursor)localObject7).dispose();
                    if (i26 == i3)
                    {
                      i1 = 1;
                      i41 = i10;
                      i42 = i25;
                      i43 = i21;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i21;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", new Object[] { Long.valueOf(paramLong), Long.valueOf(l3), Integer.valueOf(i10 / 2), Long.valueOf(paramLong), Long.valueOf(l3), Integer.valueOf(i10 / 2) }), new Object[0]);
                      i26 = i21;
                    }
                    else
                    {
                      localObject7 = null;
                      i26 = i21;
                      i1 = i37;
                    }
                  }
                  else
                  {
                    localObject7 = null;
                    i26 = i21;
                    i1 = i37;
                    continue;
                    label16526:
                    i41 = i10;
                    i42 = i38;
                    i43 = i21;
                    i44 = i26;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i38;
                    i50 = i21;
                    i51 = i25;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (paramInt4 == 1)
                    {
                      l2 = 0L;
                      i41 = i10;
                      i42 = i38;
                      i43 = i21;
                      i44 = i26;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i38;
                      i50 = i21;
                      i51 = i25;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM messages_holes WHERE uid = %d AND start >= %d AND start != 1 AND end != 1 ORDER BY start ASC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) }), new Object[0]);
                      i41 = i10;
                      i42 = i38;
                      i43 = i21;
                      i44 = i26;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i38;
                      i50 = i21;
                      i51 = i25;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      if (((SQLiteCursor)localObject7).next())
                      {
                        i41 = i10;
                        i42 = i38;
                        i43 = i21;
                        i44 = i26;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i38;
                        i50 = i21;
                        i51 = i25;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        l3 = ((SQLiteCursor)localObject7).intValue(0);
                        l2 = l3;
                        if (i39 != 0) {
                          l2 = l3 | i39 << 32;
                        }
                      }
                      i41 = i10;
                      i42 = i38;
                      i43 = i21;
                      i44 = i26;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i38;
                      i50 = i21;
                      i51 = i25;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      ((SQLiteCursor)localObject7).dispose();
                      if (l2 != 0L)
                      {
                        i41 = i10;
                        i42 = i38;
                        i43 = i21;
                        i44 = i26;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i38;
                        i50 = i21;
                        i51 = i25;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt3), Long.valueOf(l1), Long.valueOf(l2), Integer.valueOf(i10) }), new Object[0]);
                        i25 = i38;
                        i26 = i21;
                        i24 = i5;
                        i1 = i37;
                      }
                      else
                      {
                        i41 = i10;
                        i42 = i38;
                        i43 = i21;
                        i44 = i26;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i38;
                        i50 = i21;
                        i51 = i25;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt3), Long.valueOf(l1), Integer.valueOf(i10) }), new Object[0]);
                        i25 = i38;
                        i26 = i21;
                        i24 = i5;
                        i1 = i37;
                      }
                    }
                    else
                    {
                      i41 = i10;
                      i42 = i38;
                      i43 = i21;
                      i44 = i26;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i38;
                      i50 = i21;
                      i51 = i25;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      if (paramInt3 != 0)
                      {
                        if (l1 != 0L)
                        {
                          l2 = 0L;
                          i41 = i10;
                          i42 = i38;
                          i43 = i21;
                          i44 = i26;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i38;
                          i50 = i21;
                          i51 = i25;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) }), new Object[0]);
                          i41 = i10;
                          i42 = i38;
                          i43 = i21;
                          i44 = i26;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i38;
                          i50 = i21;
                          i51 = i25;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          if (((SQLiteCursor)localObject7).next())
                          {
                            i41 = i10;
                            i42 = i38;
                            i43 = i21;
                            i44 = i26;
                            i45 = i3;
                            i46 = i27;
                            bool10 = bool3;
                            bool11 = bool1;
                            i47 = i33;
                            i48 = i10;
                            i49 = i38;
                            i50 = i21;
                            i51 = i25;
                            i52 = i3;
                            i53 = i27;
                            bool12 = bool3;
                            bool13 = bool1;
                            i54 = i33;
                            l3 = ((SQLiteCursor)localObject7).intValue(0);
                            l2 = l3;
                            if (i39 != 0) {
                              l2 = l3 | i39 << 32;
                            }
                          }
                          i41 = i10;
                          i42 = i38;
                          i43 = i21;
                          i44 = i26;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i38;
                          i50 = i21;
                          i51 = i25;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          ((SQLiteCursor)localObject7).dispose();
                          if (l2 != 0L)
                          {
                            i41 = i10;
                            i42 = i38;
                            i43 = i21;
                            i44 = i26;
                            i45 = i3;
                            i46 = i27;
                            bool10 = bool3;
                            bool11 = bool1;
                            i47 = i33;
                            i48 = i10;
                            i49 = i38;
                            i50 = i21;
                            i51 = i25;
                            i52 = i3;
                            i53 = i27;
                            bool12 = bool3;
                            bool13 = bool1;
                            i54 = i33;
                            localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt3), Long.valueOf(l1), Long.valueOf(l2), Integer.valueOf(i10) }), new Object[0]);
                            i25 = i38;
                            i26 = i21;
                            i24 = i5;
                            i1 = i37;
                          }
                          else
                          {
                            i41 = i10;
                            i42 = i38;
                            i43 = i21;
                            i44 = i26;
                            i45 = i3;
                            i46 = i27;
                            bool10 = bool3;
                            bool11 = bool1;
                            i47 = i33;
                            i48 = i10;
                            i49 = i38;
                            i50 = i21;
                            i51 = i25;
                            i52 = i3;
                            i53 = i27;
                            bool12 = bool3;
                            bool13 = bool1;
                            i54 = i33;
                            localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d ORDER BY m.date DESC, m.mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt3), Long.valueOf(l1), Integer.valueOf(i10) }), new Object[0]);
                            i25 = i38;
                            i26 = i21;
                            i24 = i5;
                            i1 = i37;
                          }
                        }
                        else
                        {
                          i41 = i10;
                          i42 = i38;
                          i43 = i21;
                          i44 = i26;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i38;
                          i50 = i21;
                          i51 = i25;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt3), Integer.valueOf(i12), Integer.valueOf(i10) }), new Object[0]);
                          i25 = i38;
                          i26 = i21;
                          i24 = i5;
                          i1 = i37;
                        }
                      }
                      else
                      {
                        i41 = i10;
                        i42 = i38;
                        i43 = i21;
                        i44 = i26;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i38;
                        i50 = i21;
                        i51 = i25;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                        i41 = i10;
                        i42 = i38;
                        i43 = i21;
                        i44 = i26;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i38;
                        i50 = i21;
                        i51 = i25;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        i16 = i1;
                        if (((SQLiteCursor)localObject7).next())
                        {
                          i41 = i10;
                          i42 = i38;
                          i43 = i21;
                          i44 = i26;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i38;
                          i50 = i21;
                          i51 = i25;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          i16 = ((SQLiteCursor)localObject7).intValue(0);
                        }
                        i41 = i10;
                        i42 = i38;
                        i43 = i21;
                        i44 = i16;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i38;
                        i50 = i21;
                        i51 = i16;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        ((SQLiteCursor)localObject7).dispose();
                        l2 = 0L;
                        i41 = i10;
                        i42 = i38;
                        i43 = i21;
                        i44 = i16;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i38;
                        i50 = i21;
                        i51 = i16;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM messages_holes WHERE uid = %d", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                        i41 = i10;
                        i42 = i38;
                        i43 = i21;
                        i44 = i16;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i38;
                        i50 = i21;
                        i51 = i16;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        if (((SQLiteCursor)localObject7).next())
                        {
                          i41 = i10;
                          i42 = i38;
                          i43 = i21;
                          i44 = i16;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i38;
                          i50 = i21;
                          i51 = i16;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          l1 = ((SQLiteCursor)localObject7).intValue(0);
                          l2 = l1;
                          if (i39 != 0) {
                            l2 = l1 | i39 << 32;
                          }
                        }
                        i41 = i10;
                        i42 = i38;
                        i43 = i21;
                        i44 = i16;
                        i45 = i3;
                        i46 = i27;
                        bool10 = bool3;
                        bool11 = bool1;
                        i47 = i33;
                        i48 = i10;
                        i49 = i38;
                        i50 = i21;
                        i51 = i16;
                        i52 = i3;
                        i53 = i27;
                        bool12 = bool3;
                        bool13 = bool1;
                        i54 = i33;
                        ((SQLiteCursor)localObject7).dispose();
                        if (l2 != 0L)
                        {
                          i41 = i10;
                          i42 = i38;
                          i43 = i21;
                          i44 = i16;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i38;
                          i50 = i21;
                          i51 = i16;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Long.valueOf(l2), Integer.valueOf(i12), Integer.valueOf(i10) }), new Object[0]);
                          i25 = i38;
                          i26 = i21;
                          i24 = i5;
                          i1 = i37;
                        }
                        else
                        {
                          i41 = i10;
                          i42 = i38;
                          i43 = i21;
                          i44 = i16;
                          i45 = i3;
                          i46 = i27;
                          bool10 = bool3;
                          bool11 = bool1;
                          i47 = i33;
                          i48 = i10;
                          i49 = i38;
                          i50 = i21;
                          i51 = i16;
                          i52 = i3;
                          i53 = i27;
                          bool12 = bool3;
                          bool13 = bool1;
                          i54 = i33;
                          localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i12), Integer.valueOf(i10) }), new Object[0]);
                          i25 = i38;
                          i26 = i21;
                          i24 = i5;
                          i1 = i37;
                          continue;
                          label19295:
                          bool9 = true;
                          bool7 = true;
                          bool3 = true;
                          i41 = i11;
                          i42 = i38;
                          i43 = i15;
                          i44 = i22;
                          i45 = j;
                          i46 = i29;
                          bool10 = bool9;
                          bool11 = bool2;
                          i47 = i6;
                          i48 = i11;
                          i49 = i38;
                          i50 = i18;
                          i51 = i23;
                          i52 = n;
                          i53 = i32;
                          bool12 = bool7;
                          bool13 = bool5;
                          i54 = i9;
                          i16 = i20;
                          i3 = i2;
                          i21 = i34;
                          if (paramInt4 == 3)
                          {
                            i41 = i11;
                            i42 = i38;
                            i43 = i15;
                            i44 = i22;
                            i45 = j;
                            i46 = i29;
                            bool10 = bool9;
                            bool11 = bool2;
                            i47 = i6;
                            i48 = i11;
                            i49 = i38;
                            i50 = i18;
                            i51 = i23;
                            i52 = n;
                            i53 = i32;
                            bool12 = bool7;
                            bool13 = bool5;
                            i54 = i9;
                            i16 = i20;
                            i3 = i2;
                            i21 = i34;
                            if (paramInt3 == 0)
                            {
                              i41 = i11;
                              i42 = i38;
                              i43 = i15;
                              i44 = i22;
                              i45 = j;
                              i46 = i29;
                              bool10 = bool9;
                              bool11 = bool2;
                              i47 = i6;
                              i48 = i11;
                              i49 = i38;
                              i50 = i18;
                              i51 = i23;
                              i52 = n;
                              i53 = i32;
                              bool12 = bool7;
                              bool13 = bool5;
                              i54 = i9;
                              localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                              i41 = i11;
                              i42 = i38;
                              i43 = i15;
                              i44 = i22;
                              i45 = j;
                              i46 = i29;
                              bool10 = bool9;
                              bool11 = bool2;
                              i47 = i6;
                              i48 = i11;
                              i49 = i38;
                              i50 = i18;
                              i51 = i23;
                              i52 = n;
                              i53 = i32;
                              bool12 = bool7;
                              bool13 = bool5;
                              i54 = i9;
                              i16 = i19;
                              if (((SQLiteCursor)localObject7).next())
                              {
                                i41 = i11;
                                i42 = i38;
                                i43 = i15;
                                i44 = i22;
                                i45 = j;
                                i46 = i29;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i18;
                                i51 = i23;
                                i52 = n;
                                i53 = i32;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                i16 = ((SQLiteCursor)localObject7).intValue(0);
                              }
                              i41 = i11;
                              i42 = i38;
                              i43 = i16;
                              i44 = i22;
                              i45 = j;
                              i46 = i29;
                              bool10 = bool9;
                              bool11 = bool2;
                              i47 = i6;
                              i48 = i11;
                              i49 = i38;
                              i50 = i16;
                              i51 = i23;
                              i52 = n;
                              i53 = i32;
                              bool12 = bool7;
                              bool13 = bool5;
                              i54 = i9;
                              ((SQLiteCursor)localObject7).dispose();
                              i10 = 0;
                              i41 = i11;
                              i42 = i38;
                              i43 = i16;
                              i44 = i22;
                              i45 = j;
                              i46 = i29;
                              bool10 = bool9;
                              bool11 = bool2;
                              i47 = i6;
                              i48 = i11;
                              i49 = i38;
                              i50 = i16;
                              i51 = i23;
                              i52 = n;
                              i53 = i32;
                              bool12 = bool7;
                              bool13 = bool5;
                              i54 = i9;
                              localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                              i41 = i11;
                              i42 = i38;
                              i43 = i16;
                              i44 = i22;
                              i45 = j;
                              i46 = i29;
                              bool10 = bool9;
                              bool11 = bool2;
                              i47 = i6;
                              i48 = i11;
                              i49 = i38;
                              i50 = i16;
                              i51 = i23;
                              i52 = n;
                              i53 = i32;
                              bool12 = bool7;
                              bool13 = bool5;
                              i54 = i9;
                              if (((SQLiteCursor)localObject7).next())
                              {
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i22;
                                i45 = j;
                                i46 = i29;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i23;
                                i52 = n;
                                i53 = i32;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                i10 = ((SQLiteCursor)localObject7).intValue(0);
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i22;
                                i45 = j;
                                i46 = i29;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i23;
                                i52 = n;
                                i53 = i32;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                i33 = ((SQLiteCursor)localObject7).intValue(1);
                              }
                              i41 = i11;
                              i42 = i38;
                              i43 = i16;
                              i44 = i22;
                              i45 = j;
                              i46 = i33;
                              bool10 = bool9;
                              bool11 = bool2;
                              i47 = i6;
                              i48 = i11;
                              i49 = i38;
                              i50 = i16;
                              i51 = i23;
                              i52 = n;
                              i53 = i33;
                              bool12 = bool7;
                              bool13 = bool5;
                              i54 = i9;
                              ((SQLiteCursor)localObject7).dispose();
                              i3 = i2;
                              i21 = i33;
                              if (i10 != 0)
                              {
                                i16 = i10;
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i22;
                                i45 = j;
                                i46 = i33;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i23;
                                i52 = n;
                                i53 = i33;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i10) }), new Object[0]);
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i22;
                                i45 = j;
                                i46 = i33;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i23;
                                i52 = n;
                                i53 = i33;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                i3 = i1;
                                if (((SQLiteCursor)localObject7).next())
                                {
                                  i41 = i11;
                                  i42 = i38;
                                  i43 = i16;
                                  i44 = i22;
                                  i45 = j;
                                  i46 = i33;
                                  bool10 = bool9;
                                  bool11 = bool2;
                                  i47 = i6;
                                  i48 = i11;
                                  i49 = i38;
                                  i50 = i16;
                                  i51 = i23;
                                  i52 = n;
                                  i53 = i33;
                                  bool12 = bool7;
                                  bool13 = bool5;
                                  i54 = i9;
                                  i3 = ((SQLiteCursor)localObject7).intValue(0);
                                }
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i22;
                                i45 = i3;
                                i46 = i33;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i23;
                                i52 = i3;
                                i53 = i33;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                ((SQLiteCursor)localObject7).dispose();
                                i21 = i33;
                              }
                            }
                          }
                          i41 = i11;
                          i42 = i38;
                          i43 = i16;
                          i44 = i22;
                          i45 = i3;
                          i46 = i21;
                          bool10 = bool9;
                          bool11 = bool2;
                          i47 = i6;
                          i48 = i11;
                          i49 = i38;
                          i50 = i16;
                          i51 = i23;
                          i52 = i3;
                          i53 = i21;
                          bool12 = bool7;
                          bool13 = bool5;
                          i54 = i9;
                          if (paramInt4 != 3)
                          {
                            i41 = i11;
                            i42 = i38;
                            i43 = i16;
                            i44 = i22;
                            i45 = i3;
                            i46 = i21;
                            bool10 = bool9;
                            bool11 = bool2;
                            i47 = i6;
                            i48 = i11;
                            i49 = i38;
                            i50 = i16;
                            i51 = i23;
                            i52 = i3;
                            i53 = i21;
                            bool12 = bool7;
                            bool13 = bool5;
                            i54 = i9;
                            if (paramInt4 != 4) {}
                          }
                          else
                          {
                            i41 = i11;
                            i42 = i38;
                            i43 = i16;
                            i44 = i22;
                            i45 = i3;
                            i46 = i21;
                            bool10 = bool9;
                            bool11 = bool2;
                            i47 = i6;
                            i48 = i11;
                            i49 = i38;
                            i50 = i16;
                            i51 = i23;
                            i52 = i3;
                            i53 = i21;
                            bool12 = bool7;
                            bool13 = bool5;
                            i54 = i9;
                            localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                            i41 = i11;
                            i42 = i38;
                            i43 = i16;
                            i44 = i22;
                            i45 = i3;
                            i46 = i21;
                            bool10 = bool9;
                            bool11 = bool2;
                            i47 = i6;
                            i48 = i11;
                            i49 = i38;
                            i50 = i16;
                            i51 = i23;
                            i52 = i3;
                            i53 = i21;
                            bool12 = bool7;
                            bool13 = bool5;
                            i54 = i9;
                            i33 = i24;
                            if (((SQLiteCursor)localObject7).next())
                            {
                              i41 = i11;
                              i42 = i38;
                              i43 = i16;
                              i44 = i22;
                              i45 = i3;
                              i46 = i21;
                              bool10 = bool9;
                              bool11 = bool2;
                              i47 = i6;
                              i48 = i11;
                              i49 = i38;
                              i50 = i16;
                              i51 = i23;
                              i52 = i3;
                              i53 = i21;
                              bool12 = bool7;
                              bool13 = bool5;
                              i54 = i9;
                              i33 = ((SQLiteCursor)localObject7).intValue(0);
                            }
                            i41 = i11;
                            i42 = i38;
                            i43 = i16;
                            i44 = i33;
                            i45 = i3;
                            i46 = i21;
                            bool10 = bool9;
                            bool11 = bool2;
                            i47 = i6;
                            i48 = i11;
                            i49 = i38;
                            i50 = i16;
                            i51 = i33;
                            i52 = i3;
                            i53 = i21;
                            bool12 = bool7;
                            bool13 = bool5;
                            i54 = i9;
                            ((SQLiteCursor)localObject7).dispose();
                            i41 = i11;
                            i42 = i38;
                            i43 = i16;
                            i44 = i33;
                            i45 = i3;
                            i46 = i21;
                            bool10 = bool9;
                            bool11 = bool2;
                            i47 = i6;
                            i48 = i11;
                            i49 = i38;
                            i50 = i16;
                            i51 = i33;
                            i52 = i3;
                            i53 = i21;
                            bool12 = bool7;
                            bool13 = bool5;
                            i54 = i9;
                            localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d)", new Object[] { Long.valueOf(paramLong), Long.valueOf(l2), Integer.valueOf(i11 / 2), Long.valueOf(paramLong), Long.valueOf(l2), Integer.valueOf(i11 / 2) }), new Object[0]);
                            i10 = i11;
                            i25 = i38;
                            i26 = i16;
                            i16 = i33;
                            i27 = i21;
                            i33 = i5;
                            i24 = i36;
                            i1 = i37;
                            continue;
                          }
                          i41 = i11;
                          i42 = i38;
                          i43 = i16;
                          i44 = i22;
                          i45 = i3;
                          i46 = i21;
                          bool10 = bool9;
                          bool11 = bool2;
                          i47 = i6;
                          i48 = i11;
                          i49 = i38;
                          i50 = i16;
                          i51 = i23;
                          i52 = i3;
                          i53 = i21;
                          bool12 = bool7;
                          bool13 = bool5;
                          i54 = i9;
                          if (paramInt4 == 1)
                          {
                            i41 = i11;
                            i42 = i38;
                            i43 = i16;
                            i44 = i22;
                            i45 = i3;
                            i46 = i21;
                            bool10 = bool9;
                            bool11 = bool2;
                            i47 = i6;
                            i48 = i11;
                            i49 = i38;
                            i50 = i16;
                            i51 = i23;
                            i52 = i3;
                            i53 = i21;
                            bool12 = bool7;
                            bool13 = bool5;
                            i54 = i9;
                            localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d ORDER BY m.mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2), Integer.valueOf(i11) }), new Object[0]);
                            i10 = i11;
                            i25 = i38;
                            i26 = i16;
                            i16 = i27;
                            i27 = i21;
                            i33 = i5;
                            i24 = i36;
                            i1 = i37;
                          }
                          else
                          {
                            i41 = i11;
                            i42 = i38;
                            i43 = i16;
                            i44 = i22;
                            i45 = i3;
                            i46 = i21;
                            bool10 = bool9;
                            bool11 = bool2;
                            i47 = i6;
                            i48 = i11;
                            i49 = i38;
                            i50 = i16;
                            i51 = i23;
                            i52 = i3;
                            i53 = i21;
                            bool12 = bool7;
                            bool13 = bool5;
                            i54 = i9;
                            if (paramInt3 != 0)
                            {
                              i41 = i11;
                              i42 = i38;
                              i43 = i16;
                              i44 = i22;
                              i45 = i3;
                              i46 = i21;
                              bool10 = bool9;
                              bool11 = bool2;
                              i47 = i6;
                              i48 = i11;
                              i49 = i38;
                              i50 = i16;
                              i51 = i23;
                              i52 = i3;
                              i53 = i21;
                              bool12 = bool7;
                              bool13 = bool5;
                              i54 = i9;
                              if (paramInt2 != 0)
                              {
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i22;
                                i45 = i3;
                                i46 = i21;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i23;
                                i52 = i3;
                                i53 = i21;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2), Integer.valueOf(i11) }), new Object[0]);
                                i10 = i11;
                                i25 = i38;
                                i26 = i16;
                                i16 = i27;
                                i27 = i21;
                                i33 = i5;
                                i24 = i36;
                                i1 = i37;
                              }
                              else
                              {
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i22;
                                i45 = i3;
                                i46 = i21;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i23;
                                i52 = i3;
                                i53 = i21;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.mid ASC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt3), Integer.valueOf(0), Integer.valueOf(i11) }), new Object[0]);
                                i10 = i11;
                                i25 = i38;
                                i26 = i16;
                                i16 = i27;
                                i27 = i21;
                                i33 = i5;
                                i24 = i36;
                                i1 = i37;
                              }
                            }
                            else
                            {
                              i41 = i11;
                              i42 = i38;
                              i43 = i16;
                              i44 = i22;
                              i45 = i3;
                              i46 = i21;
                              bool10 = bool9;
                              bool11 = bool2;
                              i47 = i6;
                              i48 = i11;
                              i49 = i38;
                              i50 = i16;
                              i51 = i23;
                              i52 = i3;
                              i53 = i21;
                              bool12 = bool7;
                              bool13 = bool5;
                              i54 = i9;
                              i27 = i16;
                              i10 = i3;
                              i33 = i21;
                              if (paramInt4 == 2)
                              {
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i22;
                                i45 = i3;
                                i46 = i21;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i23;
                                i52 = i3;
                                i53 = i21;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i22;
                                i45 = i3;
                                i46 = i21;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i23;
                                i52 = i3;
                                i53 = i21;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                if (((SQLiteCursor)localObject7).next())
                                {
                                  i41 = i11;
                                  i42 = i38;
                                  i43 = i16;
                                  i44 = i22;
                                  i45 = i3;
                                  i46 = i21;
                                  bool10 = bool9;
                                  bool11 = bool2;
                                  i47 = i6;
                                  i48 = i11;
                                  i49 = i38;
                                  i50 = i16;
                                  i51 = i23;
                                  i52 = i3;
                                  i53 = i21;
                                  bool12 = bool7;
                                  bool13 = bool5;
                                  i54 = i9;
                                  i25 = ((SQLiteCursor)localObject7).intValue(0);
                                }
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i25;
                                i45 = i3;
                                i46 = i21;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i25;
                                i52 = i3;
                                i53 = i21;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                ((SQLiteCursor)localObject7).dispose();
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i25;
                                i45 = i3;
                                i46 = i21;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i25;
                                i52 = i3;
                                i53 = i21;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                                i41 = i11;
                                i42 = i38;
                                i43 = i16;
                                i44 = i25;
                                i45 = i3;
                                i46 = i21;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i16;
                                i51 = i25;
                                i52 = i3;
                                i53 = i21;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                i1 = i16;
                                i24 = i21;
                                if (((SQLiteCursor)localObject7).next())
                                {
                                  i41 = i11;
                                  i42 = i38;
                                  i43 = i16;
                                  i44 = i25;
                                  i45 = i3;
                                  i46 = i21;
                                  bool10 = bool9;
                                  bool11 = bool2;
                                  i47 = i6;
                                  i48 = i11;
                                  i49 = i38;
                                  i50 = i16;
                                  i51 = i25;
                                  i52 = i3;
                                  i53 = i21;
                                  bool12 = bool7;
                                  bool13 = bool5;
                                  i54 = i9;
                                  i1 = ((SQLiteCursor)localObject7).intValue(0);
                                  i41 = i11;
                                  i42 = i38;
                                  i43 = i1;
                                  i44 = i25;
                                  i45 = i3;
                                  i46 = i21;
                                  bool10 = bool9;
                                  bool11 = bool2;
                                  i47 = i6;
                                  i48 = i11;
                                  i49 = i38;
                                  i50 = i1;
                                  i51 = i25;
                                  i52 = i3;
                                  i53 = i21;
                                  bool12 = bool7;
                                  bool13 = bool5;
                                  i54 = i9;
                                  i24 = ((SQLiteCursor)localObject7).intValue(1);
                                }
                                i41 = i11;
                                i42 = i38;
                                i43 = i1;
                                i44 = i25;
                                i45 = i3;
                                i46 = i24;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i1;
                                i51 = i25;
                                i52 = i3;
                                i53 = i24;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                ((SQLiteCursor)localObject7).dispose();
                                i27 = i1;
                                i26 = i25;
                                i10 = i3;
                                i33 = i24;
                                if (i1 != 0)
                                {
                                  i41 = i11;
                                  i42 = i38;
                                  i43 = i1;
                                  i44 = i25;
                                  i45 = i3;
                                  i46 = i24;
                                  bool10 = bool9;
                                  bool11 = bool2;
                                  i47 = i6;
                                  i48 = i11;
                                  i49 = i38;
                                  i50 = i1;
                                  i51 = i25;
                                  i52 = i3;
                                  i53 = i24;
                                  bool12 = bool7;
                                  bool13 = bool5;
                                  i54 = i9;
                                  localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i1) }), new Object[0]);
                                  i41 = i11;
                                  i42 = i38;
                                  i43 = i1;
                                  i44 = i25;
                                  i45 = i3;
                                  i46 = i24;
                                  bool10 = bool9;
                                  bool11 = bool2;
                                  i47 = i6;
                                  i48 = i11;
                                  i49 = i38;
                                  i50 = i1;
                                  i51 = i25;
                                  i52 = i3;
                                  i53 = i24;
                                  bool12 = bool7;
                                  bool13 = bool5;
                                  i54 = i9;
                                  i10 = i3;
                                  if (((SQLiteCursor)localObject7).next())
                                  {
                                    i41 = i11;
                                    i42 = i38;
                                    i43 = i1;
                                    i44 = i25;
                                    i45 = i3;
                                    i46 = i24;
                                    bool10 = bool9;
                                    bool11 = bool2;
                                    i47 = i6;
                                    i48 = i11;
                                    i49 = i38;
                                    i50 = i1;
                                    i51 = i25;
                                    i52 = i3;
                                    i53 = i24;
                                    bool12 = bool7;
                                    bool13 = bool5;
                                    i54 = i9;
                                    i10 = ((SQLiteCursor)localObject7).intValue(0);
                                  }
                                  i41 = i11;
                                  i42 = i38;
                                  i43 = i1;
                                  i44 = i25;
                                  i45 = i10;
                                  i46 = i24;
                                  bool10 = bool9;
                                  bool11 = bool2;
                                  i47 = i6;
                                  i48 = i11;
                                  i49 = i38;
                                  i50 = i1;
                                  i51 = i25;
                                  i52 = i10;
                                  i53 = i24;
                                  bool12 = bool7;
                                  bool13 = bool5;
                                  i54 = i9;
                                  ((SQLiteCursor)localObject7).dispose();
                                  i33 = i24;
                                  i26 = i25;
                                  i27 = i1;
                                }
                              }
                              if ((i11 > i10) || (i10 < i40))
                              {
                                i41 = i11;
                                i42 = i38;
                                i43 = i27;
                                i44 = i26;
                                i45 = i10;
                                i46 = i33;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i11;
                                i49 = i38;
                                i50 = i27;
                                i51 = i26;
                                i52 = i10;
                                i53 = i33;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                i25 = Math.max(i11, i10 + 10);
                                i21 = i25;
                                i16 = i26;
                                i3 = i10;
                                i26 = i12;
                                if (i10 < i40)
                                {
                                  i3 = 0;
                                  i27 = 0;
                                  i16 = 0;
                                  i26 = i12;
                                  i21 = i25;
                                }
                              }
                              for (;;)
                              {
                                i41 = i21;
                                i42 = i38;
                                i43 = i27;
                                i44 = i16;
                                i45 = i3;
                                i46 = i33;
                                bool10 = bool9;
                                bool11 = bool2;
                                i47 = i6;
                                i48 = i21;
                                i49 = i38;
                                i50 = i27;
                                i51 = i16;
                                i52 = i3;
                                i53 = i33;
                                bool12 = bool7;
                                bool13 = bool5;
                                i54 = i9;
                                localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl, m.mention FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.mid ASC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i26), Integer.valueOf(i21) }), new Object[0]);
                                i10 = i21;
                                i25 = i38;
                                i26 = i27;
                                i27 = i33;
                                i33 = i5;
                                i24 = i36;
                                i1 = i37;
                                break;
                                i50 = i10 - i11;
                                i21 = i11 + 10;
                                i16 = i26;
                                i3 = i10;
                                i26 = i50;
                              }
                              label23673:
                              i41 = i10;
                              i42 = i25;
                              i43 = i26;
                              i44 = i16;
                              i45 = i3;
                              i46 = i27;
                              bool10 = bool3;
                              bool11 = bool1;
                              i47 = i33;
                              i48 = i10;
                              i49 = i25;
                              i50 = i26;
                              i51 = i16;
                              i52 = i3;
                              i53 = i27;
                              bool12 = bool3;
                              bool13 = bool1;
                              i54 = i33;
                              if (!((ArrayList)localObject1).contains(Long.valueOf(localMessage.reply_to_random_id)))
                              {
                                i41 = i10;
                                i42 = i25;
                                i43 = i26;
                                i44 = i16;
                                i45 = i3;
                                i46 = i27;
                                bool10 = bool3;
                                bool11 = bool1;
                                i47 = i33;
                                i48 = i10;
                                i49 = i25;
                                i50 = i26;
                                i51 = i16;
                                i52 = i3;
                                i53 = i27;
                                bool12 = bool3;
                                bool13 = bool1;
                                i54 = i33;
                                ((ArrayList)localObject1).add(Long.valueOf(localMessage.reply_to_random_id));
                              }
                              i41 = i10;
                              i42 = i25;
                              i43 = i26;
                              i44 = i16;
                              i45 = i3;
                              i46 = i27;
                              bool10 = bool3;
                              bool11 = bool1;
                              i47 = i33;
                              i48 = i10;
                              i49 = i25;
                              i50 = i26;
                              i51 = i16;
                              i52 = i3;
                              i53 = i27;
                              bool12 = bool3;
                              bool13 = bool1;
                              i54 = i33;
                              localObject8 = (ArrayList)localLongSparseArray.get(localMessage.reply_to_random_id);
                              localObject3 = localObject8;
                              if (localObject8 == null)
                              {
                                i41 = i10;
                                i42 = i25;
                                i43 = i26;
                                i44 = i16;
                                i45 = i3;
                                i46 = i27;
                                bool10 = bool3;
                                bool11 = bool1;
                                i47 = i33;
                                i48 = i10;
                                i49 = i25;
                                i50 = i26;
                                i51 = i16;
                                i52 = i3;
                                i53 = i27;
                                bool12 = bool3;
                                bool13 = bool1;
                                i54 = i33;
                                localObject3 = new java/util/ArrayList;
                                i41 = i10;
                                i42 = i25;
                                i43 = i26;
                                i44 = i16;
                                i45 = i3;
                                i46 = i27;
                                bool10 = bool3;
                                bool11 = bool1;
                                i47 = i33;
                                i48 = i10;
                                i49 = i25;
                                i50 = i26;
                                i51 = i16;
                                i52 = i3;
                                i53 = i27;
                                bool12 = bool3;
                                bool13 = bool1;
                                i54 = i33;
                                ((ArrayList)localObject3).<init>();
                                i41 = i10;
                                i42 = i25;
                                i43 = i26;
                                i44 = i16;
                                i45 = i3;
                                i46 = i27;
                                bool10 = bool3;
                                bool11 = bool1;
                                i47 = i33;
                                i48 = i10;
                                i49 = i25;
                                i50 = i26;
                                i51 = i16;
                                i52 = i3;
                                i53 = i27;
                                bool12 = bool3;
                                bool13 = bool1;
                                i54 = i33;
                                localLongSparseArray.put(localMessage.reply_to_random_id, localObject3);
                              }
                              i41 = i10;
                              i42 = i25;
                              i43 = i26;
                              i44 = i16;
                              i45 = i3;
                              i46 = i27;
                              bool10 = bool3;
                              bool11 = bool1;
                              i47 = i33;
                              i48 = i10;
                              i49 = i25;
                              i50 = i26;
                              i51 = i16;
                              i52 = i3;
                              i53 = i27;
                              bool12 = bool3;
                              bool13 = bool1;
                              i54 = i33;
                              ((ArrayList)localObject3).add(localMessage);
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
        label24264:
        int i41 = i10;
        int i42 = i25;
        int i43 = i26;
        int i44 = i16;
        int i45 = i3;
        int i46 = i27;
        boolean bool10 = bool3;
        boolean bool11 = bool1;
        int i47 = i33;
        int i48 = i10;
        int i49 = i25;
        int i50 = i26;
        int i51 = i16;
        int i52 = i3;
        int i53 = i27;
        boolean bool12 = bool3;
        boolean bool13 = bool1;
        int i54 = i33;
        ((SQLiteCursor)localObject7).dispose();
        label24341:
        i41 = i10;
        i42 = i25;
        i43 = i26;
        i44 = i16;
        i45 = i3;
        i46 = i27;
        bool10 = bool3;
        bool11 = bool1;
        i47 = i33;
        i48 = i10;
        i49 = i25;
        i50 = i26;
        i51 = i16;
        i52 = i3;
        i53 = i27;
        bool12 = bool3;
        bool13 = bool1;
        i54 = i33;
        Object localObject7 = localTL_messages_messages.messages;
        i41 = i10;
        i42 = i25;
        i43 = i26;
        i44 = i16;
        i45 = i3;
        i46 = i27;
        bool10 = bool3;
        bool11 = bool1;
        i47 = i33;
        i48 = i10;
        i49 = i25;
        i50 = i26;
        i51 = i16;
        i52 = i3;
        i53 = i27;
        bool12 = bool3;
        bool13 = bool1;
        i54 = i33;
        Object localObject3 = new org/telegram/messenger/MessagesStorage$58$1;
        i41 = i10;
        i42 = i25;
        i43 = i26;
        i44 = i16;
        i45 = i3;
        i46 = i27;
        bool10 = bool3;
        bool11 = bool1;
        i47 = i33;
        i48 = i10;
        i49 = i25;
        i50 = i26;
        i51 = i16;
        i52 = i3;
        i53 = i27;
        bool12 = bool3;
        bool13 = bool1;
        i54 = i33;
        ((1)localObject3).<init>(this);
        i41 = i10;
        i42 = i25;
        i43 = i26;
        i44 = i16;
        i45 = i3;
        i46 = i27;
        bool10 = bool3;
        bool11 = bool1;
        i47 = i33;
        i48 = i10;
        i49 = i25;
        i50 = i26;
        i51 = i16;
        i52 = i3;
        i53 = i27;
        bool12 = bool3;
        bool13 = bool1;
        i54 = i33;
        Collections.sort((List)localObject7, (Comparator)localObject3);
        if (i55 != 0)
        {
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          if (paramInt4 != 3)
          {
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            if (paramInt4 != 4)
            {
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              if ((paramInt4 != 2) || (!bool1) || (i1 != 0)) {
                break label25498;
              }
            }
          }
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          if (!localTL_messages_messages.messages.isEmpty())
          {
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            i21 = ((TLRPC.Message)localTL_messages_messages.messages.get(localTL_messages_messages.messages.size() - 1)).id;
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            i1 = ((TLRPC.Message)localTL_messages_messages.messages.get(0)).id;
            if ((i21 > i24) || (i1 < i24))
            {
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              ((ArrayList)localObject1).clear();
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              localArrayList1.clear();
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              localArrayList2.clear();
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              localTL_messages_messages.messages.clear();
            }
          }
          label25498:
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          if (paramInt4 != 4)
          {
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            if (paramInt4 != 3) {}
          }
          else
          {
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            if (localTL_messages_messages.messages.size() == 1)
            {
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              localTL_messages_messages.messages.clear();
            }
          }
        }
        i41 = i10;
        i42 = i25;
        i43 = i26;
        i44 = i16;
        i45 = i3;
        i46 = i27;
        bool10 = bool3;
        bool11 = bool1;
        i47 = i33;
        i48 = i10;
        i49 = i25;
        i50 = i26;
        i51 = i16;
        i52 = i3;
        i53 = i27;
        bool12 = bool3;
        bool13 = bool1;
        i54 = i33;
        if (!((ArrayList)localObject1).isEmpty())
        {
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          if (localSparseArray.size() > 0)
          {
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject1) }), new Object[0]);
          }
          do
          {
            for (;;)
            {
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              if (!((SQLiteCursor)localObject7).next()) {
                break label28453;
              }
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              localObject8 = ((SQLiteCursor)localObject7).byteBufferValue(0);
              if (localObject8 != null)
              {
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject8, ((NativeByteBuffer)localObject8).readInt32(false), false);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((TLRPC.Message)localObject3).readAttachPath((AbstractSerializedData)localObject8, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((NativeByteBuffer)localObject8).reuse();
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((TLRPC.Message)localObject3).id = ((SQLiteCursor)localObject7).intValue(1);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((TLRPC.Message)localObject3).date = ((SQLiteCursor)localObject7).intValue(2);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((TLRPC.Message)localObject3).dialog_id = paramLong;
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject3, localArrayList1, localArrayList2);
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (localSparseArray.size() <= 0) {
                  break;
                }
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                localObject8 = (ArrayList)localSparseArray.get(((TLRPC.Message)localObject3).id);
                if (localObject8 != null)
                {
                  for (i21 = 0;; i21++)
                  {
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (i21 >= ((ArrayList)localObject8).size()) {
                      break;
                    }
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    localObject1 = (TLRPC.Message)((ArrayList)localObject8).get(i21);
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    ((TLRPC.Message)localObject1).replyMessage = ((TLRPC.Message)localObject3);
                    i41 = i10;
                    i42 = i25;
                    i43 = i26;
                    i44 = i16;
                    i45 = i3;
                    i46 = i27;
                    bool10 = bool3;
                    bool11 = bool1;
                    i47 = i33;
                    i48 = i10;
                    i49 = i25;
                    i50 = i26;
                    i51 = i16;
                    i52 = i3;
                    i53 = i27;
                    bool12 = bool3;
                    bool13 = bool1;
                    i54 = i33;
                    if (MessageObject.isMegagroup((TLRPC.Message)localObject1))
                    {
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      localObject1 = ((TLRPC.Message)localObject1).replyMessage;
                      i41 = i10;
                      i42 = i25;
                      i43 = i26;
                      i44 = i16;
                      i45 = i3;
                      i46 = i27;
                      bool10 = bool3;
                      bool11 = bool1;
                      i47 = i33;
                      i48 = i10;
                      i49 = i25;
                      i50 = i26;
                      i51 = i16;
                      i52 = i3;
                      i53 = i27;
                      bool12 = bool3;
                      bool13 = bool1;
                      i54 = i33;
                      ((TLRPC.Message)localObject1).flags |= 0x80000000;
                    }
                  }
                  i41 = i10;
                  i42 = i25;
                  i43 = i26;
                  i44 = i16;
                  i45 = i3;
                  i46 = i27;
                  bool10 = bool3;
                  bool11 = bool1;
                  i47 = i33;
                  i48 = i10;
                  i49 = i25;
                  i50 = i26;
                  i51 = i16;
                  i52 = i3;
                  i53 = i27;
                  bool12 = bool3;
                  bool13 = bool1;
                  i54 = i33;
                  localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject1) }), new Object[0]);
                }
              }
            }
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            l2 = ((SQLiteCursor)localObject7).longValue(3);
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            localObject8 = (ArrayList)localLongSparseArray.get(l2);
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            localLongSparseArray.remove(l2);
          } while (localObject8 == null);
          for (i21 = 0;; i21++)
          {
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            if (i21 >= ((ArrayList)localObject8).size()) {
              break;
            }
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            localObject1 = (TLRPC.Message)((ArrayList)localObject8).get(i21);
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            ((TLRPC.Message)localObject1).replyMessage = ((TLRPC.Message)localObject3);
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            ((TLRPC.Message)localObject1).reply_to_msg_id = ((TLRPC.Message)localObject3).id;
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i33;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i33;
            if (MessageObject.isMegagroup((TLRPC.Message)localObject1))
            {
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              localObject1 = ((TLRPC.Message)localObject1).replyMessage;
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              ((TLRPC.Message)localObject1).flags |= 0x80000000;
            }
          }
          label28453:
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          ((SQLiteCursor)localObject7).dispose();
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          if (localLongSparseArray.size() > 0) {
            for (i21 = 0;; i21++)
            {
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              if (i21 >= localLongSparseArray.size()) {
                break;
              }
              i41 = i10;
              i42 = i25;
              i43 = i26;
              i44 = i16;
              i45 = i3;
              i46 = i27;
              bool10 = bool3;
              bool11 = bool1;
              i47 = i33;
              i48 = i10;
              i49 = i25;
              i50 = i26;
              i51 = i16;
              i52 = i3;
              i53 = i27;
              bool12 = bool3;
              bool13 = bool1;
              i54 = i33;
              localObject7 = (ArrayList)localLongSparseArray.valueAt(i21);
              for (i24 = 0;; i24++)
              {
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                if (i24 >= ((ArrayList)localObject7).size()) {
                  break;
                }
                i41 = i10;
                i42 = i25;
                i43 = i26;
                i44 = i16;
                i45 = i3;
                i46 = i27;
                bool10 = bool3;
                bool11 = bool1;
                i47 = i33;
                i48 = i10;
                i49 = i25;
                i50 = i26;
                i51 = i16;
                i52 = i3;
                i53 = i27;
                bool12 = bool3;
                bool13 = bool1;
                i54 = i33;
                ((TLRPC.Message)((ArrayList)localObject7).get(i24)).reply_to_random_id = 0L;
              }
            }
          }
        }
        i21 = i33;
        if (i33 != 0)
        {
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          localObject7 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          if (!((SQLiteCursor)localObject7).next()) {
            break label29734;
          }
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i33;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i33;
          i21 = i33;
          if (i33 == ((SQLiteCursor)localObject7).intValue(0)) {}
        }
        label29734:
        for (i21 = i33 * -1;; i21 = i33 * -1)
        {
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i21;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i21;
          ((SQLiteCursor)localObject7).dispose();
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i21;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i21;
          if (!localArrayList1.isEmpty())
          {
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i21;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i21;
            MessagesStorage.this.getUsersInternal(TextUtils.join(",", localArrayList1), localTL_messages_messages.users);
          }
          i41 = i10;
          i42 = i25;
          i43 = i26;
          i44 = i16;
          i45 = i3;
          i46 = i27;
          bool10 = bool3;
          bool11 = bool1;
          i47 = i21;
          i48 = i10;
          i49 = i25;
          i50 = i26;
          i51 = i16;
          i52 = i3;
          i53 = i27;
          bool12 = bool3;
          bool13 = bool1;
          i54 = i21;
          if (!localArrayList2.isEmpty())
          {
            i41 = i10;
            i42 = i25;
            i43 = i26;
            i44 = i16;
            i45 = i3;
            i46 = i27;
            bool10 = bool3;
            bool11 = bool1;
            i47 = i21;
            i48 = i10;
            i49 = i25;
            i50 = i26;
            i51 = i16;
            i52 = i3;
            i53 = i27;
            bool12 = bool3;
            bool13 = bool1;
            i54 = i21;
            MessagesStorage.this.getChatsInternal(TextUtils.join(",", localArrayList2), localTL_messages_messages.chats);
          }
          MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedMessages(localTL_messages_messages, paramLong, i10, i25, paramInt5, true, paramInt7, i26, i16, i3, i27, paramInt4, paramBoolean, bool3, this.val$loadIndex, bool1, i21);
          break;
        }
      }
    });
  }
  
  public void getNewTask(final ArrayList<Integer> paramArrayList, int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i;
        int j;
        Object localObject2;
        SQLiteCursor localSQLiteCursor;
        try
        {
          Object localObject1;
          if (paramArrayList != null)
          {
            localObject1 = TextUtils.join(",", paramArrayList);
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v2 WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
          }
          i = 0;
          j = -1;
          localObject2 = null;
          localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT mid, date FROM enc_tasks_v2 WHERE date = (SELECT min(date) FROM enc_tasks_v2)", new Object[0]);
          while (localSQLiteCursor.next())
          {
            long l = localSQLiteCursor.longValue(0);
            i = j;
            if (j == -1)
            {
              j = (int)(l >> 32);
              i = j;
              if (j < 0) {
                i = 0;
              }
            }
            int k = localSQLiteCursor.intValue(1);
            localObject1 = localObject2;
            if (localObject2 == null)
            {
              localObject1 = new java/util/ArrayList;
              ((ArrayList)localObject1).<init>();
            }
            ((ArrayList)localObject1).add(Integer.valueOf((int)l));
            localObject2 = localObject1;
            j = i;
            i = k;
            continue;
            return;
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          localSQLiteCursor.dispose();
          MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedDeleteTask(i, (ArrayList)localObject2, j);
        }
      }
    });
  }
  
  public int getSecretG()
  {
    ensureOpened();
    return this.secretG;
  }
  
  public byte[] getSecretPBytes()
  {
    ensureOpened();
    return this.secretPBytes;
  }
  
  public TLObject getSentFile(final String paramString, final int paramInt)
  {
    if ((paramString == null) || (paramString.toLowerCase().endsWith("attheme"))) {
      paramString = null;
    }
    for (;;)
    {
      return paramString;
      final CountDownLatch localCountDownLatch = new CountDownLatch(1);
      final ArrayList localArrayList = new ArrayList();
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            try
            {
              localObject1 = Utilities.MD5(paramString);
              if (localObject1 != null)
              {
                SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM sent_files_v2 WHERE uid = '%s' AND type = %d", new Object[] { localObject1, Integer.valueOf(paramInt) }), new Object[0]);
                if (localSQLiteCursor.next())
                {
                  NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
                  if (localNativeByteBuffer != null)
                  {
                    localObject1 = TLRPC.MessageMedia.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                    localNativeByteBuffer.reuse();
                    if (!(localObject1 instanceof TLRPC.TL_messageMediaDocument)) {
                      continue;
                    }
                    localArrayList.add(((TLRPC.TL_messageMediaDocument)localObject1).document);
                  }
                }
                localSQLiteCursor.dispose();
              }
              else
              {
                return;
              }
            }
            catch (Exception localException)
            {
              Object localObject1;
              FileLog.e(localException);
              localCountDownLatch.countDown();
              continue;
            }
            finally
            {
              localCountDownLatch.countDown();
            }
            if ((localObject1 instanceof TLRPC.TL_messageMediaPhoto)) {
              localArrayList.add(((TLRPC.TL_messageMediaPhoto)localObject1).photo);
            }
          }
        }
      });
      try
      {
        localCountDownLatch.await();
        if (!localArrayList.isEmpty()) {
          paramString = (TLObject)localArrayList.get(0);
        }
      }
      catch (Exception paramString)
      {
        for (;;)
        {
          FileLog.e(paramString);
        }
        paramString = null;
      }
    }
  }
  
  public DispatchQueue getStorageQueue()
  {
    return this.storageQueue;
  }
  
  public void getUnreadMention(final long paramLong, IntCallback paramIntCallback)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          try
          {
            Object localObject = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
            if (((SQLiteCursor)localObject).next())
            {
              i = ((SQLiteCursor)localObject).intValue(0);
              ((SQLiteCursor)localObject).dispose();
              localObject = new org/telegram/messenger/MessagesStorage$57$1;
              ((1)localObject).<init>(this, i);
              AndroidUtilities.runOnUIThread((Runnable)localObject);
              return;
            }
          }
          catch (Exception localException)
          {
            int i;
            FileLog.e(localException);
            continue;
          }
          i = 0;
        }
      }
    });
  }
  
  public void getUnsentMessages(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1;
        ArrayList localArrayList2;
        ArrayList localArrayList3;
        ArrayList localArrayList4;
        ArrayList localArrayList5;
        Object localObject2;
        ArrayList localArrayList6;
        Object localObject3;
        Object localObject4;
        int i;
        int j;
        try
        {
          localObject1 = new android/util/SparseArray;
          ((SparseArray)localObject1).<init>();
          ArrayList localArrayList1 = new java/util/ArrayList;
          localArrayList1.<init>();
          localArrayList2 = new java/util/ArrayList;
          localArrayList2.<init>();
          localArrayList3 = new java/util/ArrayList;
          localArrayList3.<init>();
          localArrayList4 = new java/util/ArrayList;
          localArrayList4.<init>();
          localArrayList5 = new java/util/ArrayList;
          localArrayList5.<init>();
          localObject2 = new java/util/ArrayList;
          ((ArrayList)localObject2).<init>();
          localArrayList6 = new java/util/ArrayList;
          localArrayList6.<init>();
          localObject3 = new java/util/ArrayList;
          ((ArrayList)localObject3).<init>();
          localObject4 = MessagesStorage.this.database;
          Object localObject5 = new java/lang/StringBuilder;
          ((StringBuilder)localObject5).<init>();
          localObject4 = ((SQLiteDatabase)localObject4).queryFinalized("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.mid < 0 AND m.send_state = 1 ORDER BY m.mid DESC LIMIT " + paramInt, new Object[0]);
          for (;;)
          {
            if (((SQLiteCursor)localObject4).next())
            {
              NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject4).byteBufferValue(1);
              if (localNativeByteBuffer != null)
              {
                localObject5 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                ((TLRPC.Message)localObject5).readAttachPath(localNativeByteBuffer, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                localNativeByteBuffer.reuse();
                if (((SparseArray)localObject1).indexOfKey(((TLRPC.Message)localObject5).id) < 0)
                {
                  MessageObject.setUnreadFlags((TLRPC.Message)localObject5, ((SQLiteCursor)localObject4).intValue(0));
                  ((TLRPC.Message)localObject5).id = ((SQLiteCursor)localObject4).intValue(3);
                  ((TLRPC.Message)localObject5).date = ((SQLiteCursor)localObject4).intValue(4);
                  if (!((SQLiteCursor)localObject4).isNull(5)) {
                    ((TLRPC.Message)localObject5).random_id = ((SQLiteCursor)localObject4).longValue(5);
                  }
                  ((TLRPC.Message)localObject5).dialog_id = ((SQLiteCursor)localObject4).longValue(6);
                  ((TLRPC.Message)localObject5).seq_in = ((SQLiteCursor)localObject4).intValue(7);
                  ((TLRPC.Message)localObject5).seq_out = ((SQLiteCursor)localObject4).intValue(8);
                  ((TLRPC.Message)localObject5).ttl = ((SQLiteCursor)localObject4).intValue(9);
                  localArrayList1.add(localObject5);
                  ((SparseArray)localObject1).put(((TLRPC.Message)localObject5).id, localObject5);
                  i = (int)((TLRPC.Message)localObject5).dialog_id;
                  j = (int)(((TLRPC.Message)localObject5).dialog_id >> 32);
                  if (i != 0) {
                    if (j == 1)
                    {
                      if (!localArrayList6.contains(Integer.valueOf(i))) {
                        localArrayList6.add(Integer.valueOf(i));
                      }
                      MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject5, localArrayList5, (ArrayList)localObject2);
                      ((TLRPC.Message)localObject5).send_state = ((SQLiteCursor)localObject4).intValue(2);
                      if (((((TLRPC.Message)localObject5).to_id.channel_id == 0) && (!MessageObject.isUnread((TLRPC.Message)localObject5)) && (i != 0)) || (((TLRPC.Message)localObject5).id > 0)) {
                        ((TLRPC.Message)localObject5).send_state = 0;
                      }
                      if ((i != 0) || (((SQLiteCursor)localObject4).isNull(5))) {
                        continue;
                      }
                      ((TLRPC.Message)localObject5).random_id = ((SQLiteCursor)localObject4).longValue(5);
                      continue;
                      return;
                    }
                  }
                }
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          if (i < 0)
          {
            j = -i;
            if (((ArrayList)localObject2).contains(Integer.valueOf(j))) {
              break;
            }
            ((ArrayList)localObject2).add(Integer.valueOf(-i));
            break;
          }
          if (localArrayList5.contains(Integer.valueOf(i))) {
            break;
          }
          localArrayList5.add(Integer.valueOf(i));
          break;
          if (((ArrayList)localObject3).contains(Integer.valueOf(j))) {
            break;
          }
          ((ArrayList)localObject3).add(Integer.valueOf(j));
          break;
          ((SQLiteCursor)localObject4).dispose();
          if (!((ArrayList)localObject3).isEmpty()) {
            MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", (Iterable)localObject3), localArrayList4, localArrayList5);
          }
          if (!localArrayList5.isEmpty()) {
            MessagesStorage.this.getUsersInternal(TextUtils.join(",", localArrayList5), localArrayList2);
          }
          if ((!((ArrayList)localObject2).isEmpty()) || (!localArrayList6.isEmpty()))
          {
            localObject1 = new java/lang/StringBuilder;
            ((StringBuilder)localObject1).<init>();
            for (i = 0; i < ((ArrayList)localObject2).size(); i++)
            {
              localObject3 = (Integer)((ArrayList)localObject2).get(i);
              if (((StringBuilder)localObject1).length() != 0) {
                ((StringBuilder)localObject1).append(",");
              }
              ((StringBuilder)localObject1).append(localObject3);
            }
            for (i = 0; i < localArrayList6.size(); i++)
            {
              localObject2 = (Integer)localArrayList6.get(i);
              if (((StringBuilder)localObject1).length() != 0) {
                ((StringBuilder)localObject1).append(",");
              }
              ((StringBuilder)localObject1).append(-((Integer)localObject2).intValue());
            }
            MessagesStorage.this.getChatsInternal(((StringBuilder)localObject1).toString(), localArrayList3);
          }
          SendMessagesHelper.getInstance(MessagesStorage.this.currentAccount).processUnsentMessages(localException, localArrayList2, localArrayList3, localArrayList4);
        }
      }
    });
  }
  
  public TLRPC.User getUser(int paramInt)
  {
    localObject1 = null;
    try
    {
      ArrayList localArrayList = new java/util/ArrayList;
      localArrayList.<init>();
      localObject2 = new java/lang/StringBuilder;
      ((StringBuilder)localObject2).<init>();
      getUsersInternal("" + paramInt, localArrayList);
      localObject2 = localObject1;
      if (!localArrayList.isEmpty()) {
        localObject2 = (TLRPC.User)localArrayList.get(0);
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Object localObject2;
        FileLog.e(localException);
        Object localObject3 = localObject1;
      }
    }
    return (TLRPC.User)localObject2;
  }
  
  public TLRPC.User getUserSync(final int paramInt)
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    final TLRPC.User[] arrayOfUser = new TLRPC.User[1];
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        arrayOfUser[0] = MessagesStorage.this.getUser(paramInt);
        localCountDownLatch.countDown();
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfUser[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public ArrayList<TLRPC.User> getUsers(ArrayList<Integer> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      getUsersInternal(TextUtils.join(",", paramArrayList), localArrayList);
      return localArrayList;
    }
    catch (Exception paramArrayList)
    {
      for (;;)
      {
        localArrayList.clear();
        FileLog.e(paramArrayList);
      }
    }
  }
  
  public void getUsersInternal(String paramString, ArrayList<TLRPC.User> paramArrayList)
    throws Exception
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayList == null)) {}
    for (;;)
    {
      return;
      paramString = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", new Object[] { paramString }), new Object[0]);
      while (paramString.next()) {
        try
        {
          NativeByteBuffer localNativeByteBuffer = paramString.byteBufferValue(0);
          if (localNativeByteBuffer != null)
          {
            TLRPC.User localUser = TLRPC.User.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (localUser != null)
            {
              if (localUser.status != null) {
                localUser.status.expires = paramString.intValue(1);
              }
              paramArrayList.add(localUser);
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
      paramString.dispose();
    }
  }
  
  public void getWallpapers()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        SQLiteCursor localSQLiteCursor;
        Object localObject;
        try
        {
          localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT data FROM wallpapers WHERE 1", new Object[0]);
          ArrayList localArrayList = new java/util/ArrayList;
          localArrayList.<init>();
          while (localSQLiteCursor.next())
          {
            localObject = localSQLiteCursor.byteBufferValue(0);
            if (localObject != null)
            {
              TLRPC.WallPaper localWallPaper = TLRPC.WallPaper.TLdeserialize((AbstractSerializedData)localObject, ((NativeByteBuffer)localObject).readInt32(false), false);
              ((NativeByteBuffer)localObject).reuse();
              localArrayList.add(localWallPaper);
              continue;
              return;
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          localSQLiteCursor.dispose();
          localObject = new org/telegram/messenger/MessagesStorage$18$1;
          ((1)localObject).<init>(this, localException);
          AndroidUtilities.runOnUIThread((Runnable)localObject);
        }
      }
    });
  }
  
  public boolean hasAuthMessage(final int paramInt)
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    final boolean[] arrayOfBoolean = new boolean[1];
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", new Object[] { Integer.valueOf(paramInt) }), new Object[0]);
          arrayOfBoolean[0] = localSQLiteCursor.next();
          localSQLiteCursor.dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
            localCountDownLatch.countDown();
          }
        }
        finally
        {
          localCountDownLatch.countDown();
        }
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public boolean isDialogHasMessages(final long paramLong)
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    boolean[] arrayOfBoolean = new boolean[1];
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d LIMIT 1", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
          localCountDownLatch[0] = localSQLiteCursor.next();
          localSQLiteCursor.dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
            this.val$countDownLatch.countDown();
          }
        }
        finally
        {
          this.val$countDownLatch.countDown();
        }
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public boolean isMigratedChat(final int paramInt)
  {
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    final boolean[] arrayOfBoolean = new boolean[1];
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        try
        {
          Object localObject1 = MessagesStorage.this.database;
          Object localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          SQLiteCursor localSQLiteCursor = ((SQLiteDatabase)localObject1).queryFinalized("SELECT info FROM chat_settings_v2 WHERE uid = " + paramInt, new Object[0]);
          localObject1 = null;
          new ArrayList();
          localObject2 = localObject1;
          if (localSQLiteCursor.next())
          {
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
            localObject2 = localObject1;
            if (localNativeByteBuffer != null)
            {
              localObject2 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
            }
          }
          localSQLiteCursor.dispose();
          localObject1 = arrayOfBoolean;
          int j = i;
          if ((localObject2 instanceof TLRPC.TL_channelFull))
          {
            j = i;
            if (((TLRPC.ChatFull)localObject2).migrated_from_chat_id != 0) {
              j = 1;
            }
          }
          localObject1[0] = j;
          if (localCountDownLatch != null) {
            localCountDownLatch.countDown();
          }
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
            if (localCountDownLatch != null) {
              localCountDownLatch.countDown();
            }
          }
        }
        finally
        {
          if (localCountDownLatch != null) {
            localCountDownLatch.countDown();
          }
        }
      }
    });
    try
    {
      localCountDownLatch.await();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public void loadChannelAdmins(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject2;
        try
        {
          Object localObject1 = MessagesStorage.this.database;
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          localObject1 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT uid FROM channel_admins WHERE did = " + paramInt, new Object[0]);
          localObject2 = new java/util/ArrayList;
          ((ArrayList)localObject2).<init>();
          while (((SQLiteCursor)localObject1).next())
          {
            ((ArrayList)localObject2).add(Integer.valueOf(((SQLiteCursor)localObject1).intValue(0)));
            continue;
            return;
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          localException.dispose();
          MessagesController.getInstance(MessagesStorage.this.currentAccount).processLoadedChannelAdmins((ArrayList)localObject2, paramInt, true);
        }
      }
    });
  }
  
  public void loadChatInfo(final int paramInt, final CountDownLatch paramCountDownLatch, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1 = null;
        localObject2 = null;
        NativeByteBuffer localNativeByteBuffer = null;
        Object localObject3 = null;
        localArrayList = new ArrayList();
        localObject4 = localObject2;
        localObject5 = localNativeByteBuffer;
        label526:
        do
        {
          try
          {
            localObject6 = MessagesStorage.this.database;
            localObject4 = localObject2;
            localObject5 = localNativeByteBuffer;
            Object localObject8 = new java/lang/StringBuilder;
            localObject4 = localObject2;
            localObject5 = localNativeByteBuffer;
            ((StringBuilder)localObject8).<init>();
            localObject4 = localObject2;
            localObject5 = localNativeByteBuffer;
            SQLiteCursor localSQLiteCursor = ((SQLiteDatabase)localObject6).queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + paramInt, new Object[0]);
            localObject6 = localObject3;
            localObject4 = localObject2;
            localObject5 = localNativeByteBuffer;
            if (localSQLiteCursor.next())
            {
              localObject4 = localObject2;
              localObject5 = localNativeByteBuffer;
              localObject8 = localSQLiteCursor.byteBufferValue(0);
              localObject6 = localObject3;
              if (localObject8 != null)
              {
                localObject4 = localObject2;
                localObject5 = localNativeByteBuffer;
                localObject6 = TLRPC.ChatFull.TLdeserialize((AbstractSerializedData)localObject8, ((NativeByteBuffer)localObject8).readInt32(false), false);
                localObject4 = localObject6;
                localObject5 = localObject6;
                ((NativeByteBuffer)localObject8).reuse();
                localObject4 = localObject6;
                localObject5 = localObject6;
                ((TLRPC.ChatFull)localObject6).pinned_msg_id = localSQLiteCursor.intValue(1);
              }
            }
            localObject4 = localObject6;
            localObject5 = localObject6;
            localSQLiteCursor.dispose();
            localObject4 = localObject6;
            localObject5 = localObject6;
            if (!(localObject6 instanceof TLRPC.TL_chatFull)) {
              break label526;
            }
            localObject4 = localObject6;
            localObject5 = localObject6;
            localObject2 = new java/lang/StringBuilder;
            localObject4 = localObject6;
            localObject5 = localObject6;
            ((StringBuilder)localObject2).<init>();
            for (i = 0;; i++)
            {
              localObject4 = localObject6;
              localObject5 = localObject6;
              if (i >= ((TLRPC.ChatFull)localObject6).participants.participants.size()) {
                break;
              }
              localObject4 = localObject6;
              localObject5 = localObject6;
              localObject3 = (TLRPC.ChatParticipant)((TLRPC.ChatFull)localObject6).participants.participants.get(i);
              localObject4 = localObject6;
              localObject5 = localObject6;
              if (((StringBuilder)localObject2).length() != 0)
              {
                localObject4 = localObject6;
                localObject5 = localObject6;
                ((StringBuilder)localObject2).append(",");
              }
              localObject4 = localObject6;
              localObject5 = localObject6;
              ((StringBuilder)localObject2).append(((TLRPC.ChatParticipant)localObject3).user_id);
            }
            localObject4 = localObject6;
            localObject5 = localObject6;
            if (((StringBuilder)localObject2).length() != 0)
            {
              localObject4 = localObject6;
              localObject5 = localObject6;
              MessagesStorage.this.getUsersInternal(((StringBuilder)localObject2).toString(), localArrayList);
            }
          }
          catch (Exception localException2)
          {
            for (;;)
            {
              Object localObject6;
              int i;
              localObject5 = localObject4;
              FileLog.e(localException2);
              MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(paramInt, (TLRPC.ChatFull)localObject4, localArrayList, true, paramBoolean1, paramBoolean2, null);
              if (paramCountDownLatch != null)
              {
                paramCountDownLatch.countDown();
                continue;
                localObject4 = localException2;
                localObject5 = localException2;
                ((SQLiteCursor)localObject2).dispose();
                localObject4 = localException2;
                localObject5 = localException2;
                StringBuilder localStringBuilder = new java/lang/StringBuilder;
                localObject4 = localException2;
                localObject5 = localException2;
                localStringBuilder.<init>();
                for (i = 0;; i++)
                {
                  localObject4 = localException2;
                  localObject5 = localException2;
                  if (i >= localException2.bot_info.size()) {
                    break;
                  }
                  localObject4 = localException2;
                  localObject5 = localException2;
                  localObject2 = (TLRPC.BotInfo)localException2.bot_info.get(i);
                  localObject4 = localException2;
                  localObject5 = localException2;
                  if (localStringBuilder.length() != 0)
                  {
                    localObject4 = localException2;
                    localObject5 = localException2;
                    localStringBuilder.append(",");
                  }
                  localObject4 = localException2;
                  localObject5 = localException2;
                  localStringBuilder.append(((TLRPC.BotInfo)localObject2).user_id);
                }
                localObject4 = localException2;
                localObject5 = localException2;
                if (localStringBuilder.length() != 0)
                {
                  localObject4 = localException2;
                  localObject5 = localException2;
                  MessagesStorage.this.getUsersInternal(localStringBuilder.toString(), localArrayList);
                }
              }
            }
          }
          finally
          {
            MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(paramInt, (TLRPC.ChatFull)localObject5, localArrayList, true, paramBoolean1, paramBoolean2, null);
            if (paramCountDownLatch == null) {
              break label1227;
            }
            paramCountDownLatch.countDown();
          }
          localObject4 = localObject6;
          localObject5 = localObject6;
          if (paramCountDownLatch != null)
          {
            localObject4 = localObject6;
            localObject5 = localObject6;
            paramCountDownLatch.countDown();
          }
          localObject3 = localObject1;
          localObject4 = localObject6;
          localObject5 = localObject6;
          if ((localObject6 instanceof TLRPC.TL_channelFull))
          {
            localObject3 = localObject1;
            localObject4 = localObject6;
            localObject5 = localObject6;
            if (((TLRPC.ChatFull)localObject6).pinned_msg_id != 0)
            {
              localObject4 = localObject6;
              localObject5 = localObject6;
              localObject3 = DataQuery.getInstance(MessagesStorage.this.currentAccount).loadPinnedMessage(paramInt, ((TLRPC.ChatFull)localObject6).pinned_msg_id, false);
            }
          }
          MessagesController.getInstance(MessagesStorage.this.currentAccount).processChatInfo(paramInt, (TLRPC.ChatFull)localObject6, localArrayList, true, paramBoolean1, paramBoolean2, (MessageObject)localObject3);
          if (paramCountDownLatch != null) {
            paramCountDownLatch.countDown();
          }
          return;
          localObject4 = localObject6;
          localObject5 = localObject6;
        } while (!(localObject6 instanceof TLRPC.TL_channelFull));
        localObject4 = localObject6;
        localObject5 = localObject6;
        localObject3 = MessagesStorage.this.database;
        localObject4 = localObject6;
        localObject5 = localObject6;
        localObject2 = new java/lang/StringBuilder;
        localObject4 = localObject6;
        localObject5 = localObject6;
        ((StringBuilder)localObject2).<init>();
        localObject4 = localObject6;
        localObject5 = localObject6;
        localObject2 = ((SQLiteDatabase)localObject3).queryFinalized("SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = " + -paramInt + " ORDER BY cu.date DESC", new Object[0]);
        localObject4 = localObject6;
        localObject5 = localObject6;
        localObject3 = new org/telegram/tgnet/TLRPC$TL_chatParticipants;
        localObject4 = localObject6;
        localObject5 = localObject6;
        ((TLRPC.TL_chatParticipants)localObject3).<init>();
        localObject4 = localObject6;
        localObject5 = localObject6;
        ((TLRPC.ChatFull)localObject6).participants = ((TLRPC.ChatParticipants)localObject3);
        for (;;)
        {
          localObject4 = localObject6;
          localObject5 = localObject6;
          boolean bool = ((SQLiteCursor)localObject2).next();
          if (!bool) {
            break;
          }
          localObject4 = null;
          localObject3 = null;
          localObject5 = localObject6;
          try
          {
            localNativeByteBuffer = ((SQLiteCursor)localObject2).byteBufferValue(0);
            if (localNativeByteBuffer != null)
            {
              localObject5 = localObject6;
              localObject4 = TLRPC.User.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localObject5 = localObject6;
              localNativeByteBuffer.reuse();
            }
            localObject5 = localObject6;
            localNativeByteBuffer = ((SQLiteCursor)localObject2).byteBufferValue(2);
            if (localNativeByteBuffer != null)
            {
              localObject5 = localObject6;
              localObject3 = TLRPC.ChannelParticipant.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localObject5 = localObject6;
              localNativeByteBuffer.reuse();
            }
            if ((localObject4 != null) && (localObject3 != null))
            {
              localObject5 = localObject6;
              if (((TLRPC.User)localObject4).status != null)
              {
                localObject5 = localObject6;
                ((TLRPC.User)localObject4).status.expires = ((SQLiteCursor)localObject2).intValue(1);
              }
              localObject5 = localObject6;
              localArrayList.add(localObject4);
              localObject5 = localObject6;
              ((TLRPC.ChannelParticipant)localObject3).date = ((SQLiteCursor)localObject2).intValue(3);
              localObject5 = localObject6;
              localObject4 = new org/telegram/tgnet/TLRPC$TL_chatChannelParticipant;
              localObject5 = localObject6;
              ((TLRPC.TL_chatChannelParticipant)localObject4).<init>();
              localObject5 = localObject6;
              ((TLRPC.TL_chatChannelParticipant)localObject4).user_id = ((TLRPC.ChannelParticipant)localObject3).user_id;
              localObject5 = localObject6;
              ((TLRPC.TL_chatChannelParticipant)localObject4).date = ((TLRPC.ChannelParticipant)localObject3).date;
              localObject5 = localObject6;
              ((TLRPC.TL_chatChannelParticipant)localObject4).inviter_id = ((TLRPC.ChannelParticipant)localObject3).inviter_id;
              localObject5 = localObject6;
              ((TLRPC.TL_chatChannelParticipant)localObject4).channelParticipant = ((TLRPC.ChannelParticipant)localObject3);
              localObject5 = localObject6;
              ((TLRPC.ChatFull)localObject6).participants.participants.add(localObject4);
            }
          }
          catch (Exception localException1)
          {
            localObject4 = localObject6;
            localObject5 = localObject6;
            FileLog.e(localException1);
          }
        }
      }
    });
  }
  
  public void loadUnreadMessages()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        Object localObject1;
        LongSparseArray localLongSparseArray;
        Object localObject2;
        int i;
        long l1;
        int j;
        label105:
        int k;
        try
        {
          localArrayList1 = new java/util/ArrayList;
          localArrayList1.<init>();
          localArrayList2 = new java/util/ArrayList;
          localArrayList2.<init>();
          localObject1 = new java/util/ArrayList;
          ((ArrayList)localObject1).<init>();
          localLongSparseArray = new android/util/LongSparseArray;
          localLongSparseArray.<init>();
          localObject2 = MessagesStorage.this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count != 0", new Object[0]);
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          i = ConnectionsManager.getInstance(MessagesStorage.this.currentAccount).getCurrentTime();
          for (;;)
          {
            if (((SQLiteCursor)localObject2).next())
            {
              l1 = ((SQLiteCursor)localObject2).longValue(2);
              if ((1L & l1) != 0L)
              {
                j = 1;
                k = (int)(l1 >> 32);
                if ((!((SQLiteCursor)localObject2).isNull(2)) && (j != 0) && ((k == 0) || (k >= i))) {
                  continue;
                }
                l1 = ((SQLiteCursor)localObject2).longValue(0);
                localLongSparseArray.put(l1, Integer.valueOf(((SQLiteCursor)localObject2).intValue(1)));
                if (localStringBuilder.length() != 0) {
                  localStringBuilder.append(",");
                }
                localStringBuilder.append(l1);
                j = (int)l1;
                k = (int)(l1 >> 32);
                if (j == 0) {
                  break label276;
                }
                if (j >= 0) {
                  break label251;
                }
                if (localArrayList2.contains(Integer.valueOf(-j))) {
                  continue;
                }
                localArrayList2.add(Integer.valueOf(-j));
                continue;
              }
            }
          }
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
        }
        for (;;)
        {
          j = 0;
          break label105;
          label251:
          if (localArrayList1.contains(Integer.valueOf(j))) {
            break;
          }
          localArrayList1.add(Integer.valueOf(j));
          break;
          label276:
          if (((ArrayList)localObject1).contains(Integer.valueOf(k))) {
            break;
          }
          ((ArrayList)localObject1).add(Integer.valueOf(k));
          break;
          ((SQLiteCursor)localObject2).dispose();
          Object localObject5 = new java/util/ArrayList;
          ((ArrayList)localObject5).<init>();
          SparseArray localSparseArray = new android/util/SparseArray;
          localSparseArray.<init>();
          ArrayList localArrayList3 = new java/util/ArrayList;
          localArrayList3.<init>();
          ArrayList localArrayList4 = new java/util/ArrayList;
          localArrayList4.<init>();
          ArrayList localArrayList5 = new java/util/ArrayList;
          localArrayList5.<init>();
          ArrayList localArrayList6 = new java/util/ArrayList;
          localArrayList6.<init>();
          if (localException1.length() > 0)
          {
            localObject2 = MessagesStorage.this.database;
            Object localObject6 = new java/lang/StringBuilder;
            ((StringBuilder)localObject6).<init>();
            localObject6 = ((SQLiteDatabase)localObject2).queryFinalized("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages WHERE uid IN (" + localException1.toString() + ") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50", new Object[0]);
            while (((SQLiteCursor)localObject6).next())
            {
              Object localObject3 = ((SQLiteCursor)localObject6).byteBufferValue(1);
              if (localObject3 != null)
              {
                TLRPC.Message localMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                localMessage.readAttachPath((AbstractSerializedData)localObject3, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                ((NativeByteBuffer)localObject3).reuse();
                MessageObject.setUnreadFlags(localMessage, ((SQLiteCursor)localObject6).intValue(0));
                localMessage.id = ((SQLiteCursor)localObject6).intValue(3);
                localMessage.date = ((SQLiteCursor)localObject6).intValue(4);
                localMessage.dialog_id = ((SQLiteCursor)localObject6).longValue(5);
                localArrayList3.add(localMessage);
                j = (int)localMessage.dialog_id;
                MessagesStorage.addUsersAndChatsFromMessage(localMessage, localArrayList1, localArrayList2);
                localMessage.send_state = ((SQLiteCursor)localObject6).intValue(2);
                if (((localMessage.to_id.channel_id == 0) && (!MessageObject.isUnread(localMessage)) && (j != 0)) || (localMessage.id > 0)) {
                  localMessage.send_state = 0;
                }
                if ((j == 0) && (!((SQLiteCursor)localObject6).isNull(5))) {
                  localMessage.random_id = ((SQLiteCursor)localObject6).longValue(5);
                }
                try
                {
                  if ((localMessage.reply_to_msg_id != 0) && (((localMessage.action instanceof TLRPC.TL_messageActionPinMessage)) || ((localMessage.action instanceof TLRPC.TL_messageActionPaymentSent)) || ((localMessage.action instanceof TLRPC.TL_messageActionGameScore))))
                  {
                    if (!((SQLiteCursor)localObject6).isNull(6))
                    {
                      localObject3 = ((SQLiteCursor)localObject6).byteBufferValue(6);
                      if (localObject3 != null)
                      {
                        localMessage.replyMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                        localMessage.replyMessage.readAttachPath((AbstractSerializedData)localObject3, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                        ((NativeByteBuffer)localObject3).reuse();
                        if (localMessage.replyMessage != null)
                        {
                          if (MessageObject.isMegagroup(localMessage))
                          {
                            localObject3 = localMessage.replyMessage;
                            ((TLRPC.Message)localObject3).flags |= 0x80000000;
                          }
                          MessagesStorage.addUsersAndChatsFromMessage(localMessage.replyMessage, localArrayList1, localArrayList2);
                        }
                      }
                    }
                    if (localMessage.replyMessage == null)
                    {
                      long l2 = localMessage.reply_to_msg_id;
                      l1 = l2;
                      if (localMessage.to_id.channel_id != 0) {
                        l1 = l2 | localMessage.to_id.channel_id << 32;
                      }
                      if (!((ArrayList)localObject5).contains(Long.valueOf(l1))) {
                        ((ArrayList)localObject5).add(Long.valueOf(l1));
                      }
                      localObject2 = (ArrayList)localSparseArray.get(localMessage.reply_to_msg_id);
                      localObject3 = localObject2;
                      if (localObject2 == null)
                      {
                        localObject3 = new java/util/ArrayList;
                        ((ArrayList)localObject3).<init>();
                        localSparseArray.put(localMessage.reply_to_msg_id, localObject3);
                      }
                      ((ArrayList)localObject3).add(localMessage);
                    }
                  }
                }
                catch (Exception localException2)
                {
                  FileLog.e(localException2);
                }
              }
            }
            ((SQLiteCursor)localObject6).dispose();
            if (!((ArrayList)localObject5).isEmpty())
            {
              localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject5) }), new Object[0]);
              while (((SQLiteCursor)localObject4).next())
              {
                localObject5 = ((SQLiteCursor)localObject4).byteBufferValue(0);
                if (localObject5 != null)
                {
                  localObject2 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject5, ((NativeByteBuffer)localObject5).readInt32(false), false);
                  ((TLRPC.Message)localObject2).readAttachPath((AbstractSerializedData)localObject5, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                  ((NativeByteBuffer)localObject5).reuse();
                  ((TLRPC.Message)localObject2).id = ((SQLiteCursor)localObject4).intValue(1);
                  ((TLRPC.Message)localObject2).date = ((SQLiteCursor)localObject4).intValue(2);
                  ((TLRPC.Message)localObject2).dialog_id = ((SQLiteCursor)localObject4).longValue(3);
                  MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject2, localArrayList1, localArrayList2);
                  localObject5 = (ArrayList)localSparseArray.get(((TLRPC.Message)localObject2).id);
                  if (localObject5 != null) {
                    for (j = 0; j < ((ArrayList)localObject5).size(); j++)
                    {
                      localObject6 = (TLRPC.Message)((ArrayList)localObject5).get(j);
                      ((TLRPC.Message)localObject6).replyMessage = ((TLRPC.Message)localObject2);
                      if (MessageObject.isMegagroup((TLRPC.Message)localObject6))
                      {
                        localObject6 = ((TLRPC.Message)localObject6).replyMessage;
                        ((TLRPC.Message)localObject6).flags |= 0x80000000;
                      }
                    }
                  }
                }
              }
              ((SQLiteCursor)localObject4).dispose();
            }
            if (!((ArrayList)localObject1).isEmpty()) {
              MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", (Iterable)localObject1), localArrayList6, localArrayList1);
            }
            if (!localArrayList1.isEmpty()) {
              MessagesStorage.this.getUsersInternal(TextUtils.join(",", localArrayList1), localArrayList4);
            }
            if (!localArrayList2.isEmpty())
            {
              MessagesStorage.this.getChatsInternal(TextUtils.join(",", localArrayList2), localArrayList5);
              for (j = 0; j < localArrayList5.size(); j = i + 1)
              {
                localObject2 = (TLRPC.Chat)localArrayList5.get(j);
                i = j;
                if (localObject2 != null) {
                  if (!((TLRPC.Chat)localObject2).left)
                  {
                    i = j;
                    if (((TLRPC.Chat)localObject2).migrated_to == null) {}
                  }
                  else
                  {
                    l1 = -((TLRPC.Chat)localObject2).id;
                    localObject4 = MessagesStorage.this.database;
                    localObject1 = new java/lang/StringBuilder;
                    ((StringBuilder)localObject1).<init>();
                    ((SQLiteDatabase)localObject4).executeFast("UPDATE dialogs SET unread_count = 0 WHERE did = " + l1).stepThis().dispose();
                    MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", new Object[] { Long.valueOf(l1) })).stepThis().dispose();
                    localArrayList5.remove(j);
                    k = j - 1;
                    localLongSparseArray.remove(-((TLRPC.Chat)localObject2).id);
                    for (j = 0;; j = i + 1)
                    {
                      i = k;
                      if (j >= localArrayList3.size()) {
                        break;
                      }
                      i = j;
                      if (((TLRPC.Message)localArrayList3.get(j)).dialog_id == -((TLRPC.Chat)localObject2).id)
                      {
                        localArrayList3.remove(j);
                        i = j - 1;
                      }
                    }
                  }
                }
              }
            }
          }
          Collections.reverse(localArrayList3);
          Object localObject4 = new org/telegram/messenger/MessagesStorage$12$1;
          ((1)localObject4).<init>(this, localLongSparseArray, localArrayList3, localArrayList4, localArrayList5, localArrayList6);
          AndroidUtilities.runOnUIThread((Runnable)localObject4);
        }
      }
    });
  }
  
  public void loadWebRecent(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject2;
        SQLiteCursor localSQLiteCursor;
        try
        {
          Object localObject1 = MessagesStorage.this.database;
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          localSQLiteCursor = ((SQLiteDatabase)localObject1).queryFinalized("SELECT id, image_url, thumb_url, local_url, width, height, size, date, document FROM web_recent_v3 WHERE type = " + paramInt + " ORDER BY date DESC", new Object[0]);
          localObject1 = new java/util/ArrayList;
          ((ArrayList)localObject1).<init>();
          while (localSQLiteCursor.next())
          {
            localObject2 = new org/telegram/messenger/MediaController$SearchImage;
            ((MediaController.SearchImage)localObject2).<init>();
            ((MediaController.SearchImage)localObject2).id = localSQLiteCursor.stringValue(0);
            ((MediaController.SearchImage)localObject2).imageUrl = localSQLiteCursor.stringValue(1);
            ((MediaController.SearchImage)localObject2).thumbUrl = localSQLiteCursor.stringValue(2);
            ((MediaController.SearchImage)localObject2).localUrl = localSQLiteCursor.stringValue(3);
            ((MediaController.SearchImage)localObject2).width = localSQLiteCursor.intValue(4);
            ((MediaController.SearchImage)localObject2).height = localSQLiteCursor.intValue(5);
            ((MediaController.SearchImage)localObject2).size = localSQLiteCursor.intValue(6);
            ((MediaController.SearchImage)localObject2).date = localSQLiteCursor.intValue(7);
            if (!localSQLiteCursor.isNull(8))
            {
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(8);
              if (localNativeByteBuffer != null)
              {
                ((MediaController.SearchImage)localObject2).document = TLRPC.Document.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
              }
            }
            ((MediaController.SearchImage)localObject2).type = paramInt;
            ((ArrayList)localObject1).add(localObject2);
            continue;
            return;
          }
        }
        catch (Throwable localThrowable)
        {
          FileLog.e(localThrowable);
        }
        for (;;)
        {
          localSQLiteCursor.dispose();
          localObject2 = new org/telegram/messenger/MessagesStorage$14$1;
          ((1)localObject2).<init>(this, localThrowable);
          AndroidUtilities.runOnUIThread((Runnable)localObject2);
        }
      }
    });
  }
  
  public void markMentionMessageAsRead(final int paramInt1, final int paramInt2, final long paramLong)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          long l1 = paramInt1;
          long l2 = l1;
          if (paramInt2 != 0) {
            l2 = l1 | paramInt2 << 32;
          }
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid = %d", new Object[] { Long.valueOf(l2) })).stepThis().dispose();
          Object localObject = MessagesStorage.this.database;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localObject = ((SQLiteDatabase)localObject).queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + paramLong, new Object[0]);
          int i = 0;
          if (((SQLiteCursor)localObject).next()) {
            i = Math.max(0, ((SQLiteCursor)localObject).intValue(0) - 1);
          }
          ((SQLiteCursor)localObject).dispose();
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[] { Integer.valueOf(i), Long.valueOf(paramLong) })).stepThis().dispose();
          localObject = new android/util/LongSparseArray;
          ((LongSparseArray)localObject).<init>(1);
          ((LongSparseArray)localObject).put(paramLong, Integer.valueOf(i));
          MessagesController.getInstance(MessagesStorage.this.currentAccount).processDialogsUpdateRead(null, (LongSparseArray)localObject);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void markMessageAsMention(final long paramLong)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET mention = 1, read_state = read_state & ~2 WHERE mid = %d", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void markMessageAsSendError(final TLRPC.Message paramMessage)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          long l1 = paramMessage.id;
          long l2 = l1;
          if (paramMessage.to_id.channel_id != 0) {
            l2 = l1 | paramMessage.to_id.channel_id << 32;
          }
          SQLiteDatabase localSQLiteDatabase = MessagesStorage.this.database;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localSQLiteDatabase.executeFast("UPDATE messages SET send_state = 2 WHERE mid = " + l2).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public ArrayList<Long> markMessagesAsDeleted(final int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.markMessagesAsDeletedInternal(paramInt1, paramInt2);
        }
      });
    }
    for (Object localObject = null;; localObject = markMessagesAsDeletedInternal(paramInt1, paramInt2)) {
      return (ArrayList<Long>)localObject;
    }
  }
  
  public ArrayList<Long> markMessagesAsDeleted(final ArrayList<Integer> paramArrayList, boolean paramBoolean, final int paramInt)
  {
    Object localObject = null;
    if (paramArrayList.isEmpty()) {
      paramArrayList = (ArrayList<Integer>)localObject;
    }
    for (;;)
    {
      return paramArrayList;
      if (paramBoolean)
      {
        this.storageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesStorage.this.markMessagesAsDeletedInternal(paramArrayList, paramInt);
          }
        });
        paramArrayList = (ArrayList<Integer>)localObject;
      }
      else
      {
        paramArrayList = markMessagesAsDeletedInternal(paramArrayList, paramInt);
      }
    }
  }
  
  public void markMessagesAsDeletedByRandoms(final ArrayList<Long> paramArrayList)
  {
    if (paramArrayList.isEmpty()) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          Object localObject2;
          try
          {
            Object localObject1 = TextUtils.join(",", paramArrayList);
            localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id IN(%s)", new Object[] { localObject1 }), new Object[0]);
            localObject1 = new java/util/ArrayList;
            ((ArrayList)localObject1).<init>();
            while (((SQLiteCursor)localObject2).next())
            {
              ((ArrayList)localObject1).add(Integer.valueOf(((SQLiteCursor)localObject2).intValue(0)));
              continue;
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
          for (;;)
          {
            ((SQLiteCursor)localObject2).dispose();
            if (!localException.isEmpty())
            {
              localObject2 = new org/telegram/messenger/MessagesStorage$85$1;
              ((1)localObject2).<init>(this, localException);
              AndroidUtilities.runOnUIThread((Runnable)localObject2);
              MessagesStorage.this.updateDialogsWithReadMessagesInternal(localException, null, null, null);
              MessagesStorage.this.markMessagesAsDeletedInternal(localException, 0);
              MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(localException, null, 0);
            }
          }
        }
      });
    }
  }
  
  public void markMessagesAsRead(final SparseLongArray paramSparseLongArray1, final SparseLongArray paramSparseLongArray2, final SparseIntArray paramSparseIntArray, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.markMessagesAsReadInternal(paramSparseLongArray1, paramSparseLongArray2, paramSparseIntArray);
        }
      });
    }
    for (;;)
    {
      return;
      markMessagesAsReadInternal(paramSparseLongArray1, paramSparseLongArray2, paramSparseIntArray);
    }
  }
  
  public void markMessagesContentAsRead(final ArrayList<Long> paramArrayList, final int paramInt)
  {
    if (isEmpty(paramArrayList)) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          SQLiteCursor localSQLiteCursor;
          Object localObject2;
          try
          {
            Object localObject1 = TextUtils.join(",", paramArrayList);
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid IN (%s)", new Object[] { localObject1 })).stepThis().dispose();
            if (paramInt != 0)
            {
              localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE mid IN (%s) AND ttl > 0", new Object[] { localObject1 }), new Object[0]);
              for (localObject2 = null; localSQLiteCursor.next(); localObject2 = localObject1)
              {
                localObject1 = localObject2;
                if (localObject2 == null)
                {
                  localObject1 = new java/util/ArrayList;
                  ((ArrayList)localObject1).<init>();
                }
                ((ArrayList)localObject1).add(Integer.valueOf(localSQLiteCursor.intValue(0)));
              }
            }
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
          for (;;)
          {
            if (localObject2 != null) {
              MessagesStorage.this.emptyMessagesMedia((ArrayList)localObject2);
            }
            localSQLiteCursor.dispose();
          }
        }
      });
    }
  }
  
  public void openDatabase(boolean paramBoolean)
  {
    File localFile = ApplicationLoader.getFilesDirFixed();
    localObject = localFile;
    if (this.currentAccount != 0)
    {
      localObject = new File(localFile, "account" + this.currentAccount + "/");
      ((File)localObject).mkdirs();
    }
    this.cacheFile = new File((File)localObject, "cache4.db");
    this.walCacheFile = new File((File)localObject, "cache4.db-wal");
    this.shmCacheFile = new File((File)localObject, "cache4.db-shm");
    i = 0;
    if (!this.cacheFile.exists()) {
      i = 1;
    }
    try
    {
      localObject = new org/telegram/SQLite/SQLiteDatabase;
      ((SQLiteDatabase)localObject).<init>(this.cacheFile.getPath());
      this.database = ((SQLiteDatabase)localObject);
      this.database.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
      this.database.executeFast("PRAGMA temp_store = 1").stepThis().dispose();
      this.database.executeFast("PRAGMA journal_mode = WAL").stepThis().dispose();
      if (i == 0) {
        break label1290;
      }
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("create new database");
      }
      this.database.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE messages(mid INTEGER PRIMARY KEY, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER, mention INTEGER)").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_idx_messages ON messages(uid, mid);").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mention_idx_messages ON messages(uid, mention, read_state);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE user_contacts_v7(key TEXT PRIMARY KEY, uid INTEGER, fname TEXT, sname TEXT, imported INTEGER)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE user_phones_v7(key TEXT, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (key, phone))").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v7(sphone, deleted);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER, pinned INTEGER)").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE randoms(random_id INTEGER, mid INTEGER, PRIMARY KEY (random_id, mid))").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
      this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
      this.database.executeFast("CREATE TABLE chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
      this.database.executeFast("CREATE TABLE chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
      this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB, in_seq_no INTEGER, admin_id INTEGER, mtproto_seq INTEGER)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
      this.database.executeFast("CREATE TABLE channel_admins(did INTEGER, uid INTEGER, PRIMARY KEY(did, uid))").stepThis().dispose();
      this.database.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE wallpapers(uid INTEGER PRIMARY KEY, data BLOB)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
      this.database.executeFast("CREATE TABLE blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));").stepThis().dispose();
      this.database.executeFast("CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
      this.database.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
      this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
      this.database.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
      this.database.executeFast("CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
      this.database.executeFast("CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
      this.database.executeFast("CREATE TABLE sharing_locations(uid INTEGER PRIMARY KEY, mid INTEGER, date INTEGER, period INTEGER, message BLOB);").stepThis().dispose();
      this.database.executeFast("PRAGMA user_version = 47").stepThis().dispose();
    }
    catch (Exception localException1)
    {
      try
      {
        for (;;)
        {
          this.openSync.countDown();
          return;
          i = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
          if (BuildVars.LOGS_ENABLED)
          {
            localObject = new java/lang/StringBuilder;
            ((StringBuilder)localObject).<init>();
            FileLog.d("current db version = " + i);
          }
          if (i != 0) {
            break;
          }
          localObject = new java/lang/Exception;
          ((Exception)localObject).<init>("malformed");
          throw ((Throwable)localObject);
          localException1 = localException1;
          FileLog.e(localException1);
          if ((paramBoolean) && (localException1.getMessage().contains("malformed")))
          {
            cleanupInternal();
            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId = 0;
            UserConfig.getInstance(this.currentAccount).totalDialogsLoadCount = 0;
            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate = 0;
            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = 0;
            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = 0;
            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = 0;
            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = 0L;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            openDatabase(false);
          }
        }
        for (;;)
        {
          try
          {
            localSQLiteCursor = this.database.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
            if (localSQLiteCursor.next())
            {
              this.lastSeqValue = localSQLiteCursor.intValue(0);
              this.lastPtsValue = localSQLiteCursor.intValue(1);
              this.lastDateValue = localSQLiteCursor.intValue(2);
              this.lastQtsValue = localSQLiteCursor.intValue(3);
              this.lastSecretVersion = localSQLiteCursor.intValue(4);
              this.secretG = localSQLiteCursor.intValue(5);
              if (!localSQLiteCursor.isNull(6)) {
                continue;
              }
              this.secretPBytes = null;
            }
            localSQLiteCursor.dispose();
          }
          catch (Exception localException2)
          {
            SQLiteCursor localSQLiteCursor;
            FileLog.e(localException2);
            try
            {
              this.database.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
              this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
            }
            catch (Exception localException3)
            {
              FileLog.e(localException3);
            }
            continue;
          }
          if (i >= 47) {
            break;
          }
          updateDbToLastVersion(i);
          break;
          this.secretPBytes = localSQLiteCursor.byteArrayValue(6);
          if ((this.secretPBytes != null) && (this.secretPBytes.length == 1)) {
            this.secretPBytes = null;
          }
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;) {}
      }
    }
    loadUnreadMessages();
    loadPendingTasks();
  }
  
  public void overwriteChannel(final int paramInt1, final TLRPC.TL_updates_channelDifferenceTooLong paramTL_updates_channelDifferenceTooLong, final int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        boolean bool = false;
        int i = 0;
        for (;;)
        {
          try
          {
            long l = -paramInt1;
            int j = 0;
            localObject1 = MessagesStorage.this.database;
            Object localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            localObject1 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT pts, pinned FROM dialogs WHERE did = " + l, new Object[0]);
            if (!((SQLiteCursor)localObject1).next())
            {
              k = j;
              if (paramInt2 != 0)
              {
                i = 1;
                k = j;
              }
              ((SQLiteCursor)localObject1).dispose();
              localObject1 = MessagesStorage.this.database;
              localObject2 = new java/lang/StringBuilder;
              ((StringBuilder)localObject2).<init>();
              ((SQLiteDatabase)localObject1).executeFast("DELETE FROM messages WHERE uid = " + l).stepThis().dispose();
              localObject2 = MessagesStorage.this.database;
              localObject1 = new java/lang/StringBuilder;
              ((StringBuilder)localObject1).<init>();
              ((SQLiteDatabase)localObject2).executeFast("DELETE FROM bot_keyboard WHERE uid = " + l).stepThis().dispose();
              localObject1 = MessagesStorage.this.database;
              localObject2 = new java/lang/StringBuilder;
              ((StringBuilder)localObject2).<init>();
              ((SQLiteDatabase)localObject1).executeFast("DELETE FROM media_counts_v2 WHERE uid = " + l).stepThis().dispose();
              localObject2 = MessagesStorage.this.database;
              localObject1 = new java/lang/StringBuilder;
              ((StringBuilder)localObject1).<init>();
              ((SQLiteDatabase)localObject2).executeFast("DELETE FROM media_v2 WHERE uid = " + l).stepThis().dispose();
              localObject2 = MessagesStorage.this.database;
              localObject1 = new java/lang/StringBuilder;
              ((StringBuilder)localObject1).<init>();
              ((SQLiteDatabase)localObject2).executeFast("DELETE FROM messages_holes WHERE uid = " + l).stepThis().dispose();
              localObject1 = MessagesStorage.this.database;
              localObject2 = new java/lang/StringBuilder;
              ((StringBuilder)localObject2).<init>();
              ((SQLiteDatabase)localObject1).executeFast("DELETE FROM media_holes_v2 WHERE uid = " + l).stepThis().dispose();
              DataQuery.getInstance(MessagesStorage.this.currentAccount).clearBotKeyboard(l, null);
              TLRPC.TL_messages_dialogs localTL_messages_dialogs = new org/telegram/tgnet/TLRPC$TL_messages_dialogs;
              localTL_messages_dialogs.<init>();
              localTL_messages_dialogs.chats.addAll(paramTL_updates_channelDifferenceTooLong.chats);
              localTL_messages_dialogs.users.addAll(paramTL_updates_channelDifferenceTooLong.users);
              localTL_messages_dialogs.messages.addAll(paramTL_updates_channelDifferenceTooLong.messages);
              localObject2 = new org/telegram/tgnet/TLRPC$TL_dialog;
              ((TLRPC.TL_dialog)localObject2).<init>();
              ((TLRPC.TL_dialog)localObject2).id = l;
              ((TLRPC.TL_dialog)localObject2).flags = 1;
              localObject1 = new org/telegram/tgnet/TLRPC$TL_peerChannel;
              ((TLRPC.TL_peerChannel)localObject1).<init>();
              ((TLRPC.TL_dialog)localObject2).peer = ((TLRPC.Peer)localObject1);
              ((TLRPC.TL_dialog)localObject2).peer.channel_id = paramInt1;
              ((TLRPC.TL_dialog)localObject2).top_message = paramTL_updates_channelDifferenceTooLong.top_message;
              ((TLRPC.TL_dialog)localObject2).read_inbox_max_id = paramTL_updates_channelDifferenceTooLong.read_inbox_max_id;
              ((TLRPC.TL_dialog)localObject2).read_outbox_max_id = paramTL_updates_channelDifferenceTooLong.read_outbox_max_id;
              ((TLRPC.TL_dialog)localObject2).unread_count = paramTL_updates_channelDifferenceTooLong.unread_count;
              ((TLRPC.TL_dialog)localObject2).unread_mentions_count = paramTL_updates_channelDifferenceTooLong.unread_mentions_count;
              ((TLRPC.TL_dialog)localObject2).notify_settings = null;
              if (k != 0) {
                bool = true;
              }
              ((TLRPC.TL_dialog)localObject2).pinned = bool;
              ((TLRPC.TL_dialog)localObject2).pinnedNum = k;
              ((TLRPC.TL_dialog)localObject2).pts = paramTL_updates_channelDifferenceTooLong.pts;
              localTL_messages_dialogs.dialogs.add(localObject2);
              MessagesStorage.this.putDialogsInternal(localTL_messages_dialogs, false);
              localObject2 = MessagesStorage.this;
              localObject1 = new java/util/ArrayList;
              ((ArrayList)localObject1).<init>();
              ((MessagesStorage)localObject2).updateDialogsWithDeletedMessages((ArrayList)localObject1, null, false, paramInt1);
              localObject1 = new org/telegram/messenger/MessagesStorage$75$1;
              ((1)localObject1).<init>(this, l);
              AndroidUtilities.runOnUIThread((Runnable)localObject1);
              if (i != 0)
              {
                if (paramInt2 != 1) {
                  continue;
                }
                MessagesController.getInstance(MessagesStorage.this.currentAccount).checkChannelInviter(paramInt1);
              }
              return;
            }
          }
          catch (Exception localException)
          {
            Object localObject1;
            int k;
            FileLog.e(localException);
            continue;
          }
          k = ((SQLiteCursor)localObject1).intValue(1);
          continue;
          MessagesController.getInstance(MessagesStorage.this.currentAccount).generateJoinMessage(paramInt1, false);
        }
      }
    });
  }
  
  public void processPendingRead(final long paramLong1, long paramLong2, long paramLong3, int paramInt, final boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        long l1 = 0L;
        int i = 0;
        long l2 = 0L;
        for (;;)
        {
          try
          {
            localObject = MessagesStorage.this.database;
            StringBuilder localStringBuilder = new java/lang/StringBuilder;
            localStringBuilder.<init>();
            localObject = ((SQLiteDatabase)localObject).queryFinalized("SELECT unread_count, inbox_max, last_mid FROM dialogs WHERE did = " + paramLong1, new Object[0]);
            if (((SQLiteCursor)localObject).next())
            {
              i = ((SQLiteCursor)localObject).intValue(0);
              l1 = ((SQLiteCursor)localObject).intValue(1);
              l2 = ((SQLiteCursor)localObject).longValue(2);
            }
            ((SQLiteCursor)localObject).dispose();
            MessagesStorage.this.database.beginTransaction();
            j = (int)paramLong1;
            if (j == 0) {
              continue;
            }
            long l3 = Math.max(l1, (int)paramBoolean);
            l1 = l3;
            if (this.val$isChannel) {
              l1 = l3 | -j << 32;
            }
            localObject = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
            ((SQLitePreparedStatement)localObject).requery();
            ((SQLitePreparedStatement)localObject).bindLong(1, paramLong1);
            ((SQLitePreparedStatement)localObject).bindLong(2, l1);
            ((SQLitePreparedStatement)localObject).step();
            ((SQLitePreparedStatement)localObject).dispose();
            if (l1 >= l2)
            {
              i = 0;
              localObject = MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = ?, inbox_max = ? WHERE did = ?");
              ((SQLitePreparedStatement)localObject).requery();
              ((SQLitePreparedStatement)localObject).bindInteger(1, i);
              ((SQLitePreparedStatement)localObject).bindInteger(2, (int)l1);
              ((SQLitePreparedStatement)localObject).bindLong(3, paramLong1);
              ((SQLitePreparedStatement)localObject).step();
              ((SQLitePreparedStatement)localObject).dispose();
              MessagesStorage.this.database.commitTransaction();
              return;
            }
          }
          catch (Exception localException)
          {
            Object localObject;
            int j;
            FileLog.e(localException);
            continue;
          }
          j = 0;
          localObject = MessagesStorage.this.database.queryFinalized("SELECT changes()", new Object[0]);
          if (((SQLiteCursor)localObject).next()) {
            j = ((SQLiteCursor)localObject).intValue(0);
          }
          ((SQLiteCursor)localObject).dispose();
          i = Math.max(0, i - j);
          continue;
          l1 = (int)this.val$maxNegativeId;
          localObject = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid >= ? AND read_state IN(0,2) AND out = 0");
          ((SQLitePreparedStatement)localObject).requery();
          ((SQLitePreparedStatement)localObject).bindLong(1, paramLong1);
          ((SQLitePreparedStatement)localObject).bindLong(2, l1);
          ((SQLitePreparedStatement)localObject).step();
          ((SQLitePreparedStatement)localObject).dispose();
          if (l1 <= l2)
          {
            i = 0;
          }
          else
          {
            j = 0;
            localObject = MessagesStorage.this.database.queryFinalized("SELECT changes()", new Object[0]);
            if (((SQLiteCursor)localObject).next()) {
              j = ((SQLiteCursor)localObject).intValue(0);
            }
            ((SQLiteCursor)localObject).dispose();
            i = Math.max(0, i - j);
          }
        }
      }
    });
  }
  
  public void putBlockedUsers(final ArrayList<Integer> paramArrayList, final boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            if (paramBoolean) {
              MessagesStorage.this.database.executeFast("DELETE FROM blocked_users WHERE 1").stepThis().dispose();
            }
            MessagesStorage.this.database.beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO blocked_users VALUES(?)");
            Iterator localIterator = paramArrayList.iterator();
            while (localIterator.hasNext())
            {
              Integer localInteger = (Integer)localIterator.next();
              localSQLitePreparedStatement.requery();
              localSQLitePreparedStatement.bindInteger(1, localInteger.intValue());
              localSQLitePreparedStatement.step();
              continue;
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
          for (;;)
          {
            localException.dispose();
            MessagesStorage.this.database.commitTransaction();
          }
        }
      });
    }
  }
  
  public void putCachedPhoneBook(final HashMap<String, ContactsController.Contact> paramHashMap, final boolean paramBoolean)
  {
    if ((paramHashMap == null) || ((paramHashMap.isEmpty()) && (!paramBoolean))) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            if (BuildVars.LOGS_ENABLED)
            {
              localObject = new java/lang/StringBuilder;
              ((StringBuilder)localObject).<init>();
              FileLog.d(MessagesStorage.this.currentAccount + " save contacts to db " + paramHashMap.size());
            }
            MessagesStorage.this.database.executeFast("DELETE FROM user_contacts_v7 WHERE 1").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM user_phones_v7 WHERE 1").stepThis().dispose();
            MessagesStorage.this.database.beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement1 = MessagesStorage.this.database.executeFast("REPLACE INTO user_contacts_v7 VALUES(?, ?, ?, ?, ?)");
            SQLitePreparedStatement localSQLitePreparedStatement2 = MessagesStorage.this.database.executeFast("REPLACE INTO user_phones_v7 VALUES(?, ?, ?, ?)");
            Object localObject = paramHashMap.entrySet().iterator();
            while (((Iterator)localObject).hasNext())
            {
              ContactsController.Contact localContact = (ContactsController.Contact)((Map.Entry)((Iterator)localObject).next()).getValue();
              if ((!localContact.phones.isEmpty()) && (!localContact.shortPhones.isEmpty()))
              {
                localSQLitePreparedStatement1.requery();
                localSQLitePreparedStatement1.bindString(1, localContact.key);
                localSQLitePreparedStatement1.bindInteger(2, localContact.contact_id);
                localSQLitePreparedStatement1.bindString(3, localContact.first_name);
                localSQLitePreparedStatement1.bindString(4, localContact.last_name);
                localSQLitePreparedStatement1.bindInteger(5, localContact.imported);
                localSQLitePreparedStatement1.step();
                for (int i = 0; i < localContact.phones.size(); i++)
                {
                  localSQLitePreparedStatement2.requery();
                  localSQLitePreparedStatement2.bindString(1, localContact.key);
                  localSQLitePreparedStatement2.bindString(2, (String)localContact.phones.get(i));
                  localSQLitePreparedStatement2.bindString(3, (String)localContact.shortPhones.get(i));
                  localSQLitePreparedStatement2.bindInteger(4, ((Integer)localContact.phoneDeleted.get(i)).intValue());
                  localSQLitePreparedStatement2.step();
                }
              }
            }
            localSQLitePreparedStatement1.dispose();
            localSQLitePreparedStatement2.dispose();
            MessagesStorage.this.database.commitTransaction();
            if (paramBoolean)
            {
              MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS user_contacts_v6;").stepThis().dispose();
              MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS user_phones_v6;").stepThis().dispose();
              MessagesStorage.this.getCachedPhoneBook(false);
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void putChannelAdmins(final int paramInt, final ArrayList<Integer> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          Object localObject = MessagesStorage.this.database;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          ((SQLiteDatabase)localObject).executeFast("DELETE FROM channel_admins WHERE did = " + paramInt).stepThis().dispose();
          MessagesStorage.this.database.beginTransaction();
          localObject = MessagesStorage.this.database.executeFast("REPLACE INTO channel_admins VALUES(?, ?)");
          int i = (int)(System.currentTimeMillis() / 1000L);
          for (i = 0; i < paramArrayList.size(); i++)
          {
            ((SQLitePreparedStatement)localObject).requery();
            ((SQLitePreparedStatement)localObject).bindInteger(1, paramInt);
            ((SQLitePreparedStatement)localObject).bindInteger(2, ((Integer)paramArrayList.get(i)).intValue());
            ((SQLitePreparedStatement)localObject).step();
          }
          ((SQLitePreparedStatement)localObject).dispose();
          MessagesStorage.this.database.commitTransaction();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void putChannelViews(final SparseArray<SparseIntArray> paramSparseArray, final boolean paramBoolean)
  {
    if (isEmpty(paramSparseArray)) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            MessagesStorage.this.database.beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE messages SET media = max((SELECT media FROM messages WHERE mid = ?), ?) WHERE mid = ?");
            for (int i = 0; i < paramSparseArray.size(); i++)
            {
              int j = paramSparseArray.keyAt(i);
              SparseIntArray localSparseIntArray = (SparseIntArray)paramSparseArray.get(j);
              for (int k = 0; k < localSparseIntArray.size(); k++)
              {
                int m = localSparseIntArray.get(localSparseIntArray.keyAt(k));
                long l1 = localSparseIntArray.keyAt(k);
                long l2 = l1;
                if (paramBoolean) {
                  l2 = l1 | -j << 32;
                }
                localSQLitePreparedStatement.requery();
                localSQLitePreparedStatement.bindLong(1, l2);
                localSQLitePreparedStatement.bindInteger(2, m);
                localSQLitePreparedStatement.bindLong(3, l2);
                localSQLitePreparedStatement.step();
              }
            }
            localSQLitePreparedStatement.dispose();
            MessagesStorage.this.database.commitTransaction();
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void putContacts(final ArrayList<TLRPC.TL_contact> paramArrayList, final boolean paramBoolean)
  {
    if (paramArrayList.isEmpty()) {}
    for (;;)
    {
      return;
      paramArrayList = new ArrayList(paramArrayList);
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            if (paramBoolean) {
              MessagesStorage.this.database.executeFast("DELETE FROM contacts WHERE 1").stepThis().dispose();
            }
            MessagesStorage.this.database.beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO contacts VALUES(?, ?)");
            int i = 0;
            if (i < paramArrayList.size())
            {
              TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)paramArrayList.get(i);
              localSQLitePreparedStatement.requery();
              localSQLitePreparedStatement.bindInteger(1, localTL_contact.user_id);
              if (localTL_contact.mutual) {}
              for (int j = 1;; j = 0)
              {
                localSQLitePreparedStatement.bindInteger(2, j);
                localSQLitePreparedStatement.step();
                i++;
                break;
              }
            }
            localSQLitePreparedStatement.dispose();
            MessagesStorage.this.database.commitTransaction();
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void putDialogPhotos(final int paramInt, final TLRPC.photos_Photos paramphotos_Photos)
  {
    if ((paramphotos_Photos == null) || (paramphotos_Photos.photos.isEmpty())) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          SQLitePreparedStatement localSQLitePreparedStatement;
          try
          {
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
            Iterator localIterator = paramphotos_Photos.photos.iterator();
            while (localIterator.hasNext())
            {
              TLRPC.Photo localPhoto = (TLRPC.Photo)localIterator.next();
              if (!(localPhoto instanceof TLRPC.TL_photoEmpty))
              {
                localSQLitePreparedStatement.requery();
                NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
                localNativeByteBuffer.<init>(localPhoto.getObjectSize());
                localPhoto.serializeToStream(localNativeByteBuffer);
                localSQLitePreparedStatement.bindInteger(1, paramInt);
                localSQLitePreparedStatement.bindLong(2, localPhoto.id);
                localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
                localSQLitePreparedStatement.step();
                localNativeByteBuffer.reuse();
                continue;
                return;
              }
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
          for (;;)
          {
            localSQLitePreparedStatement.dispose();
          }
        }
      });
    }
  }
  
  public void putDialogs(final TLRPC.messages_Dialogs parammessages_Dialogs, final boolean paramBoolean)
  {
    if (parammessages_Dialogs.dialogs.isEmpty()) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.putDialogsInternal(parammessages_Dialogs, paramBoolean);
          try
          {
            MessagesStorage.this.loadUnreadMessages();
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void putEncryptedChat(final TLRPC.EncryptedChat paramEncryptedChat, final TLRPC.User paramUser, final TLRPC.TL_dialog paramTL_dialog)
  {
    if (paramEncryptedChat == null) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i = 1;
          for (;;)
          {
            try
            {
              if (((paramEncryptedChat.key_hash == null) || (paramEncryptedChat.key_hash.length < 16)) && (paramEncryptedChat.auth_key != null)) {
                paramEncryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(paramEncryptedChat.auth_key);
              }
              SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
              NativeByteBuffer localNativeByteBuffer1 = new org/telegram/tgnet/NativeByteBuffer;
              localNativeByteBuffer1.<init>(paramEncryptedChat.getObjectSize());
              NativeByteBuffer localNativeByteBuffer2 = new org/telegram/tgnet/NativeByteBuffer;
              if (paramEncryptedChat.a_or_b != null)
              {
                j = paramEncryptedChat.a_or_b.length;
                localNativeByteBuffer2.<init>(j);
                NativeByteBuffer localNativeByteBuffer3 = new org/telegram/tgnet/NativeByteBuffer;
                if (paramEncryptedChat.auth_key == null) {
                  continue;
                }
                j = paramEncryptedChat.auth_key.length;
                localNativeByteBuffer3.<init>(j);
                NativeByteBuffer localNativeByteBuffer4 = new org/telegram/tgnet/NativeByteBuffer;
                if (paramEncryptedChat.future_auth_key == null) {
                  continue;
                }
                j = paramEncryptedChat.future_auth_key.length;
                localNativeByteBuffer4.<init>(j);
                NativeByteBuffer localNativeByteBuffer5 = new org/telegram/tgnet/NativeByteBuffer;
                j = i;
                if (paramEncryptedChat.key_hash != null) {
                  j = paramEncryptedChat.key_hash.length;
                }
                localNativeByteBuffer5.<init>(j);
                paramEncryptedChat.serializeToStream(localNativeByteBuffer1);
                localSQLitePreparedStatement.bindInteger(1, paramEncryptedChat.id);
                localSQLitePreparedStatement.bindInteger(2, paramUser.id);
                localSQLitePreparedStatement.bindString(3, MessagesStorage.this.formatUserSearchName(paramUser));
                localSQLitePreparedStatement.bindByteBuffer(4, localNativeByteBuffer1);
                if (paramEncryptedChat.a_or_b != null) {
                  localNativeByteBuffer2.writeBytes(paramEncryptedChat.a_or_b);
                }
                if (paramEncryptedChat.auth_key != null) {
                  localNativeByteBuffer3.writeBytes(paramEncryptedChat.auth_key);
                }
                if (paramEncryptedChat.future_auth_key != null) {
                  localNativeByteBuffer4.writeBytes(paramEncryptedChat.future_auth_key);
                }
                if (paramEncryptedChat.key_hash != null) {
                  localNativeByteBuffer5.writeBytes(paramEncryptedChat.key_hash);
                }
                localSQLitePreparedStatement.bindByteBuffer(5, localNativeByteBuffer2);
                localSQLitePreparedStatement.bindByteBuffer(6, localNativeByteBuffer3);
                localSQLitePreparedStatement.bindInteger(7, paramEncryptedChat.ttl);
                localSQLitePreparedStatement.bindInteger(8, paramEncryptedChat.layer);
                localSQLitePreparedStatement.bindInteger(9, paramEncryptedChat.seq_in);
                localSQLitePreparedStatement.bindInteger(10, paramEncryptedChat.seq_out);
                localSQLitePreparedStatement.bindInteger(11, paramEncryptedChat.key_use_count_in << 16 | paramEncryptedChat.key_use_count_out);
                localSQLitePreparedStatement.bindLong(12, paramEncryptedChat.exchange_id);
                localSQLitePreparedStatement.bindInteger(13, paramEncryptedChat.key_create_date);
                localSQLitePreparedStatement.bindLong(14, paramEncryptedChat.future_key_fingerprint);
                localSQLitePreparedStatement.bindByteBuffer(15, localNativeByteBuffer4);
                localSQLitePreparedStatement.bindByteBuffer(16, localNativeByteBuffer5);
                localSQLitePreparedStatement.bindInteger(17, paramEncryptedChat.in_seq_no);
                localSQLitePreparedStatement.bindInteger(18, paramEncryptedChat.admin_id);
                localSQLitePreparedStatement.bindInteger(19, paramEncryptedChat.mtproto_seq);
                localSQLitePreparedStatement.step();
                localSQLitePreparedStatement.dispose();
                localNativeByteBuffer1.reuse();
                localNativeByteBuffer2.reuse();
                localNativeByteBuffer3.reuse();
                localNativeByteBuffer4.reuse();
                localNativeByteBuffer5.reuse();
                if (paramTL_dialog != null)
                {
                  localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                  localSQLitePreparedStatement.bindLong(1, paramTL_dialog.id);
                  localSQLitePreparedStatement.bindInteger(2, paramTL_dialog.last_message_date);
                  localSQLitePreparedStatement.bindInteger(3, paramTL_dialog.unread_count);
                  localSQLitePreparedStatement.bindInteger(4, paramTL_dialog.top_message);
                  localSQLitePreparedStatement.bindInteger(5, paramTL_dialog.read_inbox_max_id);
                  localSQLitePreparedStatement.bindInteger(6, paramTL_dialog.read_outbox_max_id);
                  localSQLitePreparedStatement.bindInteger(7, 0);
                  localSQLitePreparedStatement.bindInteger(8, paramTL_dialog.unread_mentions_count);
                  localSQLitePreparedStatement.bindInteger(9, paramTL_dialog.pts);
                  localSQLitePreparedStatement.bindInteger(10, 0);
                  localSQLitePreparedStatement.bindInteger(11, paramTL_dialog.pinnedNum);
                  localSQLitePreparedStatement.step();
                  localSQLitePreparedStatement.dispose();
                }
                return;
              }
            }
            catch (Exception localException)
            {
              int j;
              FileLog.e(localException);
              continue;
            }
            j = 1;
            continue;
            j = 1;
            continue;
            j = 1;
          }
        }
      });
    }
  }
  
  public void putMessages(ArrayList<TLRPC.Message> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    putMessages(paramArrayList, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, false);
  }
  
  public void putMessages(final ArrayList<TLRPC.Message> paramArrayList, final boolean paramBoolean1, boolean paramBoolean2, final boolean paramBoolean3, final int paramInt, final boolean paramBoolean4)
  {
    if (paramArrayList.size() == 0) {}
    for (;;)
    {
      return;
      if (paramBoolean2) {
        this.storageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesStorage.this.putMessagesInternal(paramArrayList, paramBoolean1, paramBoolean3, paramInt, paramBoolean4);
          }
        });
      } else {
        putMessagesInternal(paramArrayList, paramBoolean1, paramBoolean3, paramInt, paramBoolean4);
      }
    }
  }
  
  public void putMessages(final TLRPC.messages_Messages parammessages_Messages, final long paramLong, final int paramInt1, int paramInt2, final boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = Integer.MAX_VALUE;
        try
        {
          if (parammessages_Messages.messages.isEmpty())
          {
            if (paramInt1 == 0)
            {
              MessagesStorage.this.doneHolesInTable("messages_holes", paramLong, paramBoolean);
              MessagesStorage.this.doneHolesInMedia(paramLong, paramBoolean, -1);
            }
            return;
          }
          MessagesStorage.this.database.beginTransaction();
          if (paramInt1 == 0)
          {
            j = ((TLRPC.Message)parammessages_Messages.messages.get(parammessages_Messages.messages.size() - 1)).id;
            MessagesStorage.this.closeHolesInTable("messages_holes", paramLong, j, paramBoolean);
            MessagesStorage.this.closeHolesInMedia(paramLong, j, paramBoolean, -1);
          }
          for (;;)
          {
            int k = parammessages_Messages.messages.size();
            localSQLitePreparedStatement1 = MessagesStorage.this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)");
            localSQLitePreparedStatement2 = MessagesStorage.this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
            Object localObject1 = null;
            localObject3 = null;
            m = 0;
            n = 0;
            j = i;
            i = m;
            for (;;)
            {
              if (n >= k) {
                break label1467;
              }
              localMessage = (TLRPC.Message)parammessages_Messages.messages.get(n);
              long l1 = localMessage.id;
              int i1 = i;
              if (i == 0) {
                i1 = localMessage.to_id.channel_id;
              }
              l2 = l1;
              if (localMessage.to_id.channel_id != 0) {
                l2 = l1 | i1 << 32;
              }
              m = j;
              if (paramInt1 != -2) {
                break label804;
              }
              localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data, ttl, mention, read_state FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l2) }), new Object[0]);
              boolean bool = ((SQLiteCursor)localObject4).next();
              i = j;
              if (bool)
              {
                Object localObject5 = ((SQLiteCursor)localObject4).byteBufferValue(1);
                if (localObject5 != null)
                {
                  localObject6 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject5, ((NativeByteBuffer)localObject5).readInt32(false), false);
                  ((TLRPC.Message)localObject6).readAttachPath((AbstractSerializedData)localObject5, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                  ((NativeByteBuffer)localObject5).reuse();
                  if (localObject6 != null)
                  {
                    localMessage.attachPath = ((TLRPC.Message)localObject6).attachPath;
                    localMessage.ttl = ((SQLiteCursor)localObject4).intValue(2);
                  }
                }
                if (((SQLiteCursor)localObject4).intValue(3) == 0) {
                  break;
                }
                i2 = 1;
                int i3 = ((SQLiteCursor)localObject4).intValue(4);
                i = j;
                if (i2 != localMessage.mentioned)
                {
                  m = j;
                  if (j == Integer.MAX_VALUE)
                  {
                    localObject6 = MessagesStorage.this.database;
                    localObject5 = new java/lang/StringBuilder;
                    ((StringBuilder)localObject5).<init>();
                    localObject6 = ((SQLiteDatabase)localObject6).queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + paramLong, new Object[0]);
                    if (((SQLiteCursor)localObject6).next()) {
                      j = ((SQLiteCursor)localObject6).intValue(0);
                    }
                    ((SQLiteCursor)localObject6).dispose();
                    m = j;
                  }
                  if (i2 == 0) {
                    break label785;
                  }
                  i = m;
                  if (i3 <= 1) {
                    i = m - 1;
                  }
                }
              }
              ((SQLiteCursor)localObject4).dispose();
              m = i;
              if (bool) {
                break label804;
              }
              localObject6 = localObject1;
              j = i;
              localObject1 = localObject3;
              n++;
              localObject3 = localObject1;
              i = i1;
              localObject1 = localObject6;
            }
            if (paramInt1 != 1) {
              break;
            }
            j = ((TLRPC.Message)parammessages_Messages.messages.get(0)).id;
            MessagesStorage.this.closeHolesInTable("messages_holes", paramLong, paramBoolean, j);
            MessagesStorage.this.closeHolesInMedia(paramLong, paramBoolean, j, -1);
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            int j;
            SQLitePreparedStatement localSQLitePreparedStatement1;
            SQLitePreparedStatement localSQLitePreparedStatement2;
            Object localObject3;
            int m;
            int n;
            TLRPC.Message localMessage;
            long l2;
            Object localObject4;
            Object localObject6;
            int i2;
            FileLog.e(localException);
            continue;
            if ((paramInt1 == 3) || (paramInt1 == 2) || (paramInt1 == 4))
            {
              if ((paramBoolean == 0) && (paramInt1 != 4)) {}
              for (j = Integer.MAX_VALUE;; j = ((TLRPC.Message)parammessages_Messages.messages.get(0)).id)
              {
                m = ((TLRPC.Message)parammessages_Messages.messages.get(parammessages_Messages.messages.size() - 1)).id;
                MessagesStorage.this.closeHolesInTable("messages_holes", paramLong, m, j);
                MessagesStorage.this.closeHolesInMedia(paramLong, m, j, -1);
                break;
              }
              i2 = 0;
              continue;
              label785:
              i = m;
              if (localMessage.media_unread)
              {
                i = m + 1;
                continue;
                label804:
                if ((n == 0) && (this.val$createDialog))
                {
                  i = 0;
                  j = 0;
                  localObject4 = MessagesStorage.this.database;
                  localObject6 = new java/lang/StringBuilder;
                  ((StringBuilder)localObject6).<init>();
                  localObject4 = ((SQLiteDatabase)localObject4).queryFinalized("SELECT pinned, unread_count_i FROM dialogs WHERE did = " + paramLong, new Object[0]);
                  if (((SQLiteCursor)localObject4).next())
                  {
                    i = ((SQLiteCursor)localObject4).intValue(0);
                    j = ((SQLiteCursor)localObject4).intValue(1);
                  }
                  ((SQLiteCursor)localObject4).dispose();
                  localObject4 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                  ((SQLitePreparedStatement)localObject4).bindLong(1, paramLong);
                  ((SQLitePreparedStatement)localObject4).bindInteger(2, localMessage.date);
                  ((SQLitePreparedStatement)localObject4).bindInteger(3, 0);
                  ((SQLitePreparedStatement)localObject4).bindLong(4, l2);
                  ((SQLitePreparedStatement)localObject4).bindInteger(5, localMessage.id);
                  ((SQLitePreparedStatement)localObject4).bindInteger(6, 0);
                  ((SQLitePreparedStatement)localObject4).bindLong(7, l2);
                  ((SQLitePreparedStatement)localObject4).bindInteger(8, j);
                  ((SQLitePreparedStatement)localObject4).bindInteger(9, parammessages_Messages.pts);
                  ((SQLitePreparedStatement)localObject4).bindInteger(10, localMessage.date);
                  ((SQLitePreparedStatement)localObject4).bindInteger(11, i);
                  ((SQLitePreparedStatement)localObject4).step();
                  ((SQLitePreparedStatement)localObject4).dispose();
                }
                MessagesStorage.this.fixUnsupportedMedia(localMessage);
                localSQLitePreparedStatement1.requery();
                localObject4 = new org/telegram/tgnet/NativeByteBuffer;
                ((NativeByteBuffer)localObject4).<init>(localMessage.getObjectSize());
                localMessage.serializeToStream((AbstractSerializedData)localObject4);
                localSQLitePreparedStatement1.bindLong(1, l2);
                localSQLitePreparedStatement1.bindLong(2, paramLong);
                localSQLitePreparedStatement1.bindInteger(3, MessageObject.getUnreadFlags(localMessage));
                localSQLitePreparedStatement1.bindInteger(4, localMessage.send_state);
                localSQLitePreparedStatement1.bindInteger(5, localMessage.date);
                localSQLitePreparedStatement1.bindByteBuffer(6, (NativeByteBuffer)localObject4);
                if (MessageObject.isOut(localMessage))
                {
                  j = 1;
                  label1131:
                  localSQLitePreparedStatement1.bindInteger(7, j);
                  localSQLitePreparedStatement1.bindInteger(8, localMessage.ttl);
                  if ((localMessage.flags & 0x400) == 0) {
                    break label1443;
                  }
                  localSQLitePreparedStatement1.bindInteger(9, localMessage.views);
                  label1175:
                  localSQLitePreparedStatement1.bindInteger(10, 0);
                  if (!localMessage.mentioned) {
                    break label1462;
                  }
                }
                Object localObject2;
                label1443:
                label1462:
                for (j = 1;; j = 0)
                {
                  localSQLitePreparedStatement1.bindInteger(11, j);
                  localSQLitePreparedStatement1.step();
                  if (DataQuery.canAddMessageToMedia(localMessage))
                  {
                    localSQLitePreparedStatement2.requery();
                    localSQLitePreparedStatement2.bindLong(1, l2);
                    localSQLitePreparedStatement2.bindLong(2, paramLong);
                    localSQLitePreparedStatement2.bindInteger(3, localMessage.date);
                    localSQLitePreparedStatement2.bindInteger(4, DataQuery.getMediaType(localMessage));
                    localSQLitePreparedStatement2.bindByteBuffer(5, (NativeByteBuffer)localObject4);
                    localSQLitePreparedStatement2.step();
                  }
                  ((NativeByteBuffer)localObject4).reuse();
                  localObject4 = localException;
                  if ((localMessage.media instanceof TLRPC.TL_messageMediaWebPage))
                  {
                    localObject4 = localException;
                    if (localException == null) {
                      localObject4 = MessagesStorage.this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
                    }
                    ((SQLitePreparedStatement)localObject4).requery();
                    ((SQLitePreparedStatement)localObject4).bindLong(1, localMessage.media.webpage.id);
                    ((SQLitePreparedStatement)localObject4).bindLong(2, l2);
                    ((SQLitePreparedStatement)localObject4).step();
                  }
                  localObject2 = localObject3;
                  j = m;
                  localObject6 = localObject4;
                  if (paramInt1 != 0) {
                    break;
                  }
                  localObject2 = localObject3;
                  j = m;
                  localObject6 = localObject4;
                  if (!MessagesStorage.this.isValidKeyboardToSave(localMessage)) {
                    break;
                  }
                  if (localObject3 != null)
                  {
                    localObject2 = localObject3;
                    j = m;
                    localObject6 = localObject4;
                    if (((TLRPC.Message)localObject3).id >= localMessage.id) {
                      break;
                    }
                  }
                  localObject2 = localMessage;
                  j = m;
                  localObject6 = localObject4;
                  break;
                  j = 0;
                  break label1131;
                  localSQLitePreparedStatement1.bindInteger(9, MessagesStorage.this.getMessageMediaType(localMessage));
                  break label1175;
                }
                label1467:
                localSQLitePreparedStatement1.dispose();
                localSQLitePreparedStatement2.dispose();
                if (localObject2 != null) {
                  ((SQLitePreparedStatement)localObject2).dispose();
                }
                if (localObject3 != null) {
                  DataQuery.getInstance(MessagesStorage.this.currentAccount).putBotKeyboard(paramLong, (TLRPC.Message)localObject3);
                }
                MessagesStorage.this.putUsersInternal(parammessages_Messages.users);
                MessagesStorage.this.putChatsInternal(parammessages_Messages.chats);
                if (j != Integer.MAX_VALUE)
                {
                  MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[] { Integer.valueOf(j), Long.valueOf(paramLong) })).stepThis().dispose();
                  localObject2 = new android/util/LongSparseArray;
                  ((LongSparseArray)localObject2).<init>(1);
                  ((LongSparseArray)localObject2).put(paramLong, Integer.valueOf(j));
                  MessagesController.getInstance(MessagesStorage.this.currentAccount).processDialogsUpdateRead(null, (LongSparseArray)localObject2);
                }
                MessagesStorage.this.database.commitTransaction();
                if (this.val$createDialog)
                {
                  localObject4 = MessagesStorage.this;
                  localObject2 = new java/util/ArrayList;
                  ((ArrayList)localObject2).<init>();
                  ((MessagesStorage)localObject4).updateDialogsWithDeletedMessages((ArrayList)localObject2, null, false, i);
                }
              }
            }
          }
        }
      }
    });
  }
  
  public void putSentFile(final String paramString, final TLObject paramTLObject, final int paramInt)
  {
    if ((paramString == null) || (paramTLObject == null)) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          localSQLitePreparedStatement = null;
          localObject1 = null;
          localNativeByteBuffer = null;
          localObject2 = localNativeByteBuffer;
          localObject4 = localObject1;
          for (;;)
          {
            try
            {
              str = Utilities.MD5(paramString);
              if (str == null) {
                continue;
              }
              localObject5 = null;
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              if (!(paramTLObject instanceof TLRPC.Photo)) {
                continue;
              }
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              localObject5 = new org/telegram/tgnet/TLRPC$TL_messageMediaPhoto;
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              ((TLRPC.TL_messageMediaPhoto)localObject5).<init>();
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              ((TLRPC.MessageMedia)localObject5).photo = ((TLRPC.Photo)paramTLObject);
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              ((TLRPC.MessageMedia)localObject5).flags |= 0x1;
              if (localObject5 != null) {
                continue;
              }
              if (0 != 0) {
                throw new NullPointerException();
              }
            }
            catch (Exception localException)
            {
              String str;
              Object localObject5;
              localObject4 = localObject2;
              FileLog.e(localException);
              if (localObject2 == null) {
                continue;
              }
              ((SQLitePreparedStatement)localObject2).dispose();
              continue;
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO sent_files_v2 VALUES(?, ?, ?)");
              localObject2 = localSQLitePreparedStatement;
              localObject4 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.requery();
              localObject2 = localSQLitePreparedStatement;
              localObject4 = localSQLitePreparedStatement;
              localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
              localObject2 = localSQLitePreparedStatement;
              localObject4 = localSQLitePreparedStatement;
              localNativeByteBuffer.<init>(localException.getObjectSize());
              localObject2 = localSQLitePreparedStatement;
              localObject4 = localSQLitePreparedStatement;
              localException.serializeToStream(localNativeByteBuffer);
              localObject2 = localSQLitePreparedStatement;
              localObject4 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.bindString(1, str);
              localObject2 = localSQLitePreparedStatement;
              localObject4 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.bindInteger(2, paramInt);
              localObject2 = localSQLitePreparedStatement;
              localObject4 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
              localObject2 = localSQLitePreparedStatement;
              localObject4 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.step();
              localObject2 = localSQLitePreparedStatement;
              localObject4 = localSQLitePreparedStatement;
              localNativeByteBuffer.reuse();
              if (localSQLitePreparedStatement == null) {
                continue;
              }
              localSQLitePreparedStatement.dispose();
              continue;
            }
            finally
            {
              if (localObject4 == null) {
                continue;
              }
              ((SQLitePreparedStatement)localObject4).dispose();
            }
            return;
            localObject2 = localNativeByteBuffer;
            localObject4 = localObject1;
            if ((paramTLObject instanceof TLRPC.Document))
            {
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              localObject5 = new org/telegram/tgnet/TLRPC$TL_messageMediaDocument;
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              ((TLRPC.TL_messageMediaDocument)localObject5).<init>();
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              ((TLRPC.MessageMedia)localObject5).document = ((TLRPC.Document)paramTLObject);
              localObject2 = localNativeByteBuffer;
              localObject4 = localObject1;
              ((TLRPC.MessageMedia)localObject5).flags |= 0x1;
            }
          }
        }
      });
    }
  }
  
  public void putUsersAndChats(final ArrayList<TLRPC.User> paramArrayList, final ArrayList<TLRPC.Chat> paramArrayList1, final boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramArrayList != null) && (paramArrayList.isEmpty()) && (paramArrayList1 != null) && (paramArrayList1.isEmpty())) {}
    for (;;)
    {
      return;
      if (paramBoolean2) {
        this.storageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesStorage.this.putUsersAndChatsInternal(paramArrayList, paramArrayList1, paramBoolean1);
          }
        });
      } else {
        putUsersAndChatsInternal(paramArrayList, paramArrayList1, paramBoolean1);
      }
    }
  }
  
  public void putWallpapers(final ArrayList<TLRPC.WallPaper> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        SQLitePreparedStatement localSQLitePreparedStatement;
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM wallpapers WHERE 1").stepThis().dispose();
          MessagesStorage.this.database.beginTransaction();
          localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO wallpapers VALUES(?, ?)");
          Iterator localIterator = paramArrayList.iterator();
          while (localIterator.hasNext())
          {
            TLRPC.WallPaper localWallPaper = (TLRPC.WallPaper)localIterator.next();
            localSQLitePreparedStatement.requery();
            NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
            localNativeByteBuffer.<init>(localWallPaper.getObjectSize());
            localWallPaper.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindInteger(1, i);
            localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer);
            localSQLitePreparedStatement.step();
            i++;
            localNativeByteBuffer.reuse();
            continue;
            return;
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          localSQLitePreparedStatement.dispose();
          MessagesStorage.this.database.commitTransaction();
        }
      }
    });
  }
  
  public void putWebPages(final LongSparseArray<TLRPC.WebPage> paramLongSparseArray)
  {
    if (isEmpty(paramLongSparseArray)) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i;
          Object localObject1;
          Object localObject2;
          try
          {
            ArrayList localArrayList = new java/util/ArrayList;
            localArrayList.<init>();
            i = 0;
            if (i < paramLongSparseArray.size())
            {
              localObject1 = MessagesStorage.this.database;
              localObject2 = new java/lang/StringBuilder;
              ((StringBuilder)localObject2).<init>();
              localObject1 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT mid FROM webpage_pending WHERE id = " + paramLongSparseArray.keyAt(i), new Object[0]);
              localObject2 = new java/util/ArrayList;
              ((ArrayList)localObject2).<init>();
              while (((SQLiteCursor)localObject1).next())
              {
                ((ArrayList)localObject2).add(Long.valueOf(((SQLiteCursor)localObject1).longValue(0)));
                continue;
                return;
              }
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
          for (;;)
          {
            ((SQLiteCursor)localObject1).dispose();
            if (((ArrayList)localObject2).isEmpty()) {}
            Object localObject3;
            for (;;)
            {
              i++;
              break;
              localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data FROM messages WHERE mid IN (%s)", new Object[] { TextUtils.join(",", (Iterable)localObject2) }), new Object[0]);
              while (((SQLiteCursor)localObject2).next())
              {
                int j = ((SQLiteCursor)localObject2).intValue(0);
                localObject1 = ((SQLiteCursor)localObject2).byteBufferValue(1);
                if (localObject1 != null)
                {
                  localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
                  ((TLRPC.Message)localObject3).readAttachPath((AbstractSerializedData)localObject1, UserConfig.getInstance(MessagesStorage.this.currentAccount).clientUserId);
                  ((NativeByteBuffer)localObject1).reuse();
                  if ((((TLRPC.Message)localObject3).media instanceof TLRPC.TL_messageMediaWebPage))
                  {
                    ((TLRPC.Message)localObject3).id = j;
                    ((TLRPC.Message)localObject3).media.webpage = ((TLRPC.WebPage)paramLongSparseArray.valueAt(i));
                    localException.add(localObject3);
                  }
                }
              }
              ((SQLiteCursor)localObject2).dispose();
            }
            if (!localException.isEmpty())
            {
              MessagesStorage.this.database.beginTransaction();
              localObject3 = MessagesStorage.this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
              SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE media_v2 SET data = ? WHERE mid = ?");
              for (i = 0; i < localException.size(); i++)
              {
                localObject2 = (TLRPC.Message)localException.get(i);
                localObject1 = new org/telegram/tgnet/NativeByteBuffer;
                ((NativeByteBuffer)localObject1).<init>(((TLRPC.Message)localObject2).getObjectSize());
                ((TLRPC.Message)localObject2).serializeToStream((AbstractSerializedData)localObject1);
                long l1 = ((TLRPC.Message)localObject2).id;
                long l2 = l1;
                if (((TLRPC.Message)localObject2).to_id.channel_id != 0) {
                  l2 = l1 | ((TLRPC.Message)localObject2).to_id.channel_id << 32;
                }
                ((SQLitePreparedStatement)localObject3).requery();
                ((SQLitePreparedStatement)localObject3).bindByteBuffer(1, (NativeByteBuffer)localObject1);
                ((SQLitePreparedStatement)localObject3).bindLong(2, l2);
                ((SQLitePreparedStatement)localObject3).step();
                localSQLitePreparedStatement.requery();
                localSQLitePreparedStatement.bindByteBuffer(1, (NativeByteBuffer)localObject1);
                localSQLitePreparedStatement.bindLong(2, l2);
                localSQLitePreparedStatement.step();
                ((NativeByteBuffer)localObject1).reuse();
              }
              ((SQLitePreparedStatement)localObject3).dispose();
              localSQLitePreparedStatement.dispose();
              MessagesStorage.this.database.commitTransaction();
              localObject2 = new org/telegram/messenger/MessagesStorage$74$1;
              ((1)localObject2).<init>(this, localException);
              AndroidUtilities.runOnUIThread((Runnable)localObject2);
            }
          }
        }
      });
    }
  }
  
  public void putWebRecent(final ArrayList<MediaController.SearchImage> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.beginTransaction();
          Object localObject1 = MessagesStorage.this.database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
          int i = 0;
          Object localObject2;
          if ((i >= paramArrayList.size()) || (i == 200))
          {
            ((SQLitePreparedStatement)localObject1).dispose();
            MessagesStorage.this.database.commitTransaction();
            if (paramArrayList.size() >= 200)
            {
              MessagesStorage.this.database.beginTransaction();
              for (i = 200; i < paramArrayList.size(); i++)
              {
                localObject2 = MessagesStorage.this.database;
                localObject1 = new java/lang/StringBuilder;
                ((StringBuilder)localObject1).<init>();
                ((SQLiteDatabase)localObject2).executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((MediaController.SearchImage)paramArrayList.get(i)).id + "'").stepThis().dispose();
              }
            }
          }
          else
          {
            MediaController.SearchImage localSearchImage = (MediaController.SearchImage)paramArrayList.get(i);
            ((SQLitePreparedStatement)localObject1).requery();
            ((SQLitePreparedStatement)localObject1).bindString(1, localSearchImage.id);
            ((SQLitePreparedStatement)localObject1).bindInteger(2, localSearchImage.type);
            if (localSearchImage.imageUrl != null)
            {
              localObject2 = localSearchImage.imageUrl;
              label209:
              ((SQLitePreparedStatement)localObject1).bindString(3, (String)localObject2);
              if (localSearchImage.thumbUrl == null) {
                break label366;
              }
              localObject2 = localSearchImage.thumbUrl;
              label229:
              ((SQLitePreparedStatement)localObject1).bindString(4, (String)localObject2);
              if (localSearchImage.localUrl == null) {
                break label372;
              }
              localObject2 = localSearchImage.localUrl;
              label249:
              ((SQLitePreparedStatement)localObject1).bindString(5, (String)localObject2);
              ((SQLitePreparedStatement)localObject1).bindInteger(6, localSearchImage.width);
              ((SQLitePreparedStatement)localObject1).bindInteger(7, localSearchImage.height);
              ((SQLitePreparedStatement)localObject1).bindInteger(8, localSearchImage.size);
              ((SQLitePreparedStatement)localObject1).bindInteger(9, localSearchImage.date);
              localObject2 = null;
              if (localSearchImage.document == null) {
                break label378;
              }
              localObject2 = new org/telegram/tgnet/NativeByteBuffer;
              ((NativeByteBuffer)localObject2).<init>(localSearchImage.document.getObjectSize());
              localSearchImage.document.serializeToStream((AbstractSerializedData)localObject2);
              ((SQLitePreparedStatement)localObject1).bindByteBuffer(10, (NativeByteBuffer)localObject2);
            }
            for (;;)
            {
              ((SQLitePreparedStatement)localObject1).step();
              if (localObject2 != null) {
                ((NativeByteBuffer)localObject2).reuse();
              }
              i++;
              break;
              localObject2 = "";
              break label209;
              label366:
              localObject2 = "";
              break label229;
              label372:
              localObject2 = "";
              break label249;
              label378:
              ((SQLitePreparedStatement)localObject1).bindNull(10);
            }
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          MessagesStorage.this.database.commitTransaction();
        }
      }
    });
  }
  
  public void removeFromDownloadQueue(final long paramLong, final int paramInt, final boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          try
          {
            if (paramBoolean)
            {
              int i = -1;
              SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(date) FROM download_queue WHERE type = %d", new Object[] { Integer.valueOf(paramInt) }), new Object[0]);
              if (localSQLiteCursor.next()) {
                i = localSQLiteCursor.intValue(0);
              }
              localSQLiteCursor.dispose();
              if (i != -1) {
                MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", new Object[] { Integer.valueOf(i - 1), Long.valueOf(paramLong), Integer.valueOf(paramInt) })).stepThis().dispose();
              }
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            continue;
          }
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt) })).stepThis().dispose();
        }
      }
    });
  }
  
  public void removePendingTask(final long paramLong)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLiteDatabase localSQLiteDatabase = MessagesStorage.this.database;
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localSQLiteDatabase.executeFast("DELETE FROM pending_tasks WHERE id = " + paramLong).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void resetDialogs(final TLRPC.messages_Dialogs parammessages_Dialogs, final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final int paramInt5, final LongSparseArray<TLRPC.TL_dialog> paramLongSparseArray, final LongSparseArray<MessageObject> paramLongSparseArray1, final TLRPC.Message paramMessage, final int paramInt6)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        int j;
        LongSparseArray localLongSparseArray;
        ArrayList localArrayList2;
        int k;
        int m;
        try
        {
          localObject1 = new java/util/ArrayList;
          ((ArrayList)localObject1).<init>();
          j = parammessages_Dialogs.dialogs.size() - paramInt6;
          localLongSparseArray = new android/util/LongSparseArray;
          localLongSparseArray.<init>();
          ArrayList localArrayList1 = new java/util/ArrayList;
          localArrayList1.<init>();
          localArrayList2 = new java/util/ArrayList;
          localArrayList2.<init>();
          for (k = paramInt6; k < parammessages_Dialogs.dialogs.size(); k++) {
            localArrayList2.add(Long.valueOf(((TLRPC.TL_dialog)parammessages_Dialogs.dialogs.get(k)).id));
          }
          localObject3 = MessagesStorage.this.database.queryFinalized("SELECT did, pinned FROM dialogs WHERE 1", new Object[0]);
          k = i;
          while (((SQLiteCursor)localObject3).next())
          {
            long l = ((SQLiteCursor)localObject3).longValue(0);
            i = ((SQLiteCursor)localObject3).intValue(1);
            m = (int)l;
            if (m != 0)
            {
              ((ArrayList)localObject1).add(Integer.valueOf(m));
              if (i > 0)
              {
                k = Math.max(i, k);
                localLongSparseArray.put(l, Integer.valueOf(i));
                localArrayList1.add(Long.valueOf(l));
                continue;
                return;
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        Object localObject4 = new org/telegram/messenger/MessagesStorage$27$1;
        ((1)localObject4).<init>(this, localLongSparseArray);
        Collections.sort(localException, (Comparator)localObject4);
        while (localException.size() < j) {
          localException.add(0, Long.valueOf(0L));
        }
        ((SQLiteCursor)localObject3).dispose();
        Object localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        Object localObject1 = "(" + TextUtils.join(",", (Iterable)localObject1) + ")";
        MessagesStorage.this.database.beginTransaction();
        localObject3 = MessagesStorage.this.database;
        localObject4 = new java/lang/StringBuilder;
        ((StringBuilder)localObject4).<init>();
        ((SQLiteDatabase)localObject3).executeFast("DELETE FROM dialogs WHERE did IN " + (String)localObject1).stepThis().dispose();
        localObject4 = MessagesStorage.this.database;
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        ((SQLiteDatabase)localObject4).executeFast("DELETE FROM messages WHERE uid IN " + (String)localObject1).stepThis().dispose();
        localObject4 = MessagesStorage.this.database;
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        ((SQLiteDatabase)localObject4).executeFast("DELETE FROM bot_keyboard WHERE uid IN " + (String)localObject1).stepThis().dispose();
        localObject4 = MessagesStorage.this.database;
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        ((SQLiteDatabase)localObject4).executeFast("DELETE FROM media_counts_v2 WHERE uid IN " + (String)localObject1).stepThis().dispose();
        localObject3 = MessagesStorage.this.database;
        localObject4 = new java/lang/StringBuilder;
        ((StringBuilder)localObject4).<init>();
        ((SQLiteDatabase)localObject3).executeFast("DELETE FROM media_v2 WHERE uid IN " + (String)localObject1).stepThis().dispose();
        localObject3 = MessagesStorage.this.database;
        localObject4 = new java/lang/StringBuilder;
        ((StringBuilder)localObject4).<init>();
        ((SQLiteDatabase)localObject3).executeFast("DELETE FROM messages_holes WHERE uid IN " + (String)localObject1).stepThis().dispose();
        localObject3 = MessagesStorage.this.database;
        localObject4 = new java/lang/StringBuilder;
        ((StringBuilder)localObject4).<init>();
        ((SQLiteDatabase)localObject3).executeFast("DELETE FROM media_holes_v2 WHERE uid IN " + (String)localObject1).stepThis().dispose();
        MessagesStorage.this.database.commitTransaction();
        i = 0;
        if (i < j)
        {
          localObject1 = (TLRPC.TL_dialog)parammessages_Dialogs.dialogs.get(paramInt6 + i);
          int n = localException.indexOf(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
          m = localArrayList2.indexOf(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
          if ((n != -1) && (m != -1))
          {
            if (n != m) {
              break label754;
            }
            localObject3 = (Integer)localLongSparseArray.get(((TLRPC.TL_dialog)localObject1).id);
            if (localObject3 != null) {
              ((TLRPC.TL_dialog)localObject1).pinnedNum = ((Integer)localObject3).intValue();
            }
          }
          for (;;)
          {
            if (((TLRPC.TL_dialog)localObject1).pinnedNum == 0) {
              ((TLRPC.TL_dialog)localObject1).pinnedNum = (j - i + k);
            }
            i++;
            break;
            label754:
            localObject3 = (Integer)localLongSparseArray.get(((Long)localException.get(m)).longValue());
            if (localObject3 != null) {
              ((TLRPC.TL_dialog)localObject1).pinnedNum = ((Integer)localObject3).intValue();
            }
          }
        }
        MessagesStorage.this.putDialogsInternal(parammessages_Dialogs, false);
        MessagesStorage.this.saveDiffParamsInternal(paramInt2, paramInt3, paramInt4, paramInt5);
        label989:
        Object localObject2;
        if ((paramMessage != null) && (paramMessage.id != UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetId))
        {
          UserConfig.getInstance(MessagesStorage.this.currentAccount).totalDialogsLoadCount = parammessages_Dialogs.dialogs.size();
          UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetId = paramMessage.id;
          UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetDate = paramMessage.date;
          if (paramMessage.to_id.channel_id != 0)
          {
            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChannelId = paramMessage.to_id.channel_id;
            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChatId = 0;
            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetUserId = 0;
            k = 0;
            if (k < parammessages_Dialogs.chats.size())
            {
              localObject2 = (TLRPC.Chat)parammessages_Dialogs.chats.get(k);
              if (((TLRPC.Chat)localObject2).id != UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChannelId) {
                break label1126;
              }
              UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetAccess = ((TLRPC.Chat)localObject2).access_hash;
            }
          }
        }
        for (;;)
        {
          UserConfig.getInstance(MessagesStorage.this.currentAccount).saveConfig(false);
          MessagesController.getInstance(MessagesStorage.this.currentAccount).completeDialogsReset(parammessages_Dialogs, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramLongSparseArray, paramLongSparseArray1, paramMessage);
          break;
          label1126:
          k++;
          break label989;
          if (paramMessage.to_id.chat_id != 0)
          {
            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChatId = paramMessage.to_id.chat_id;
            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChannelId = 0;
            UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetUserId = 0;
            for (k = 0;; k++)
            {
              if (k >= parammessages_Dialogs.chats.size()) {
                break label1277;
              }
              localObject2 = (TLRPC.Chat)parammessages_Dialogs.chats.get(k);
              if (((TLRPC.Chat)localObject2).id == UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChatId)
              {
                UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetAccess = ((TLRPC.Chat)localObject2).access_hash;
                break;
              }
            }
          }
          else
          {
            label1277:
            if (paramMessage.to_id.user_id != 0)
            {
              UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetUserId = paramMessage.to_id.user_id;
              UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChatId = 0;
              UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetChannelId = 0;
              for (k = 0;; k++)
              {
                if (k >= parammessages_Dialogs.users.size()) {
                  break label1424;
                }
                localObject2 = (TLRPC.User)parammessages_Dialogs.users.get(k);
                if (((TLRPC.User)localObject2).id == UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetUserId)
                {
                  UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetAccess = ((TLRPC.User)localObject2).access_hash;
                  break;
                }
              }
              label1424:
              continue;
              UserConfig.getInstance(MessagesStorage.this.currentAccount).dialogsLoadOffsetId = Integer.MAX_VALUE;
            }
          }
        }
      }
    });
  }
  
  public void resetMentionsCount(final long paramLong, final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          if (paramInt == 0) {
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
          }
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", new Object[] { Integer.valueOf(paramInt), Long.valueOf(paramLong) })).stepThis().dispose();
          LongSparseArray localLongSparseArray = new android/util/LongSparseArray;
          localLongSparseArray.<init>(1);
          localLongSparseArray.put(paramLong, Integer.valueOf(paramInt));
          MessagesController.getInstance(MessagesStorage.this.currentAccount).processDialogsUpdateRead(null, localLongSparseArray);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void saveBotCache(final String paramString, final TLObject paramTLObject)
  {
    if ((paramTLObject == null) || (TextUtils.isEmpty(paramString))) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          for (;;)
          {
            try
            {
              i = ConnectionsManager.getInstance(MessagesStorage.this.currentAccount).getCurrentTime();
              if ((paramTLObject instanceof TLRPC.TL_messages_botCallbackAnswer))
              {
                j = i + ((TLRPC.TL_messages_botCallbackAnswer)paramTLObject).cache_time;
                SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
                NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
                localNativeByteBuffer.<init>(paramTLObject.getObjectSize());
                paramTLObject.serializeToStream(localNativeByteBuffer);
                localSQLitePreparedStatement.bindString(1, paramString);
                localSQLitePreparedStatement.bindInteger(2, j);
                localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
                localSQLitePreparedStatement.step();
                localSQLitePreparedStatement.dispose();
                localNativeByteBuffer.reuse();
                return;
              }
            }
            catch (Exception localException)
            {
              int i;
              int j;
              FileLog.e(localException);
              continue;
            }
            j = i;
            if ((paramTLObject instanceof TLRPC.TL_messages_botResults))
            {
              j = ((TLRPC.TL_messages_botResults)paramTLObject).cache_time;
              j = i + j;
            }
          }
        }
      });
    }
  }
  
  public void saveChannelPts(final int paramInt1, final int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
          localSQLitePreparedStatement.bindInteger(1, paramInt2);
          localSQLitePreparedStatement.bindInteger(2, -paramInt1);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void saveDiffParams(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesStorage.this.saveDiffParamsInternal(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    });
  }
  
  public void saveSecretParams(final int paramInt1, final int paramInt2, final byte[] paramArrayOfByte)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 1;
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
          localSQLitePreparedStatement.bindInteger(1, paramInt1);
          localSQLitePreparedStatement.bindInteger(2, paramInt2);
          NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
          if (paramArrayOfByte != null) {
            i = paramArrayOfByte.length;
          }
          localNativeByteBuffer.<init>(i);
          if (paramArrayOfByte != null) {
            localNativeByteBuffer.writeBytes(paramArrayOfByte);
          }
          localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          localNativeByteBuffer.reuse();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void setDialogFlags(final long paramLong1, long paramLong2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", new Object[] { Long.valueOf(paramLong1), Long.valueOf(this.val$flags) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void setDialogPinned(final long paramLong, final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
          localSQLitePreparedStatement.bindInteger(1, paramInt);
          localSQLitePreparedStatement.bindLong(2, paramLong);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void setLastDateValue(int paramInt)
  {
    ensureOpened();
    this.lastDateValue = paramInt;
  }
  
  public void setLastPtsValue(int paramInt)
  {
    ensureOpened();
    this.lastPtsValue = paramInt;
  }
  
  public void setLastQtsValue(int paramInt)
  {
    ensureOpened();
    this.lastQtsValue = paramInt;
  }
  
  public void setLastSecretVersion(int paramInt)
  {
    ensureOpened();
    this.lastSecretVersion = paramInt;
  }
  
  public void setLastSeqValue(int paramInt)
  {
    ensureOpened();
    this.lastSeqValue = paramInt;
  }
  
  public void setMessageSeq(final int paramInt1, final int paramInt2, final int paramInt3)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, paramInt1);
          localSQLitePreparedStatement.bindInteger(2, paramInt2);
          localSQLitePreparedStatement.bindInteger(3, paramInt3);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void setSecretG(int paramInt)
  {
    ensureOpened();
    this.secretG = paramInt;
  }
  
  public void setSecretPBytes(byte[] paramArrayOfByte)
  {
    ensureOpened();
    this.secretPBytes = paramArrayOfByte;
  }
  
  public void unpinAllDialogsExceptNew(final ArrayList<Long> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject;
        try
        {
          ArrayList localArrayList = new java/util/ArrayList;
          localArrayList.<init>();
          localObject = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE pinned != 0 AND did NOT IN (%s)", new Object[] { TextUtils.join(",", paramArrayList) }), new Object[0]);
          while (((SQLiteCursor)localObject).next()) {
            if ((int)((SQLiteCursor)localObject).longValue(0) != 0)
            {
              localArrayList.add(Long.valueOf(((SQLiteCursor)localObject).longValue(0)));
              continue;
              return;
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          ((SQLiteCursor)localObject).dispose();
          if (!localException.isEmpty())
          {
            localObject = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
            for (int i = 0; i < localException.size(); i++)
            {
              long l = ((Long)localException.get(i)).longValue();
              ((SQLitePreparedStatement)localObject).requery();
              ((SQLitePreparedStatement)localObject).bindInteger(1, 0);
              ((SQLitePreparedStatement)localObject).bindLong(2, l);
              ((SQLitePreparedStatement)localObject).step();
            }
            ((SQLitePreparedStatement)localObject).dispose();
          }
        }
      }
    });
  }
  
  public void updateChannelPinnedMessage(final int paramInt1, final int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          Object localObject1 = MessagesStorage.this.database;
          Object localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          Object localObject3 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + paramInt1, new Object[0]);
          localObject2 = null;
          new ArrayList();
          localObject1 = localObject2;
          if (((SQLiteCursor)localObject3).next())
          {
            NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject3).byteBufferValue(0);
            localObject1 = localObject2;
            if (localNativeByteBuffer != null)
            {
              localObject1 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              ((TLRPC.ChatFull)localObject1).pinned_msg_id = ((SQLiteCursor)localObject3).intValue(1);
            }
          }
          ((SQLiteCursor)localObject3).dispose();
          if ((localObject1 instanceof TLRPC.TL_channelFull))
          {
            ((TLRPC.ChatFull)localObject1).pinned_msg_id = paramInt2;
            ((TLRPC.ChatFull)localObject1).flags |= 0x20;
            localObject2 = new org/telegram/messenger/MessagesStorage$44$1;
            ((1)localObject2).<init>(this, (TLRPC.ChatFull)localObject1);
            AndroidUtilities.runOnUIThread((Runnable)localObject2);
            localObject3 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
            localObject2 = new org/telegram/tgnet/NativeByteBuffer;
            ((NativeByteBuffer)localObject2).<init>(((TLRPC.ChatFull)localObject1).getObjectSize());
            ((TLRPC.ChatFull)localObject1).serializeToStream((AbstractSerializedData)localObject2);
            ((SQLitePreparedStatement)localObject3).bindInteger(1, paramInt1);
            ((SQLitePreparedStatement)localObject3).bindByteBuffer(2, (NativeByteBuffer)localObject2);
            ((SQLitePreparedStatement)localObject3).bindInteger(3, ((TLRPC.ChatFull)localObject1).pinned_msg_id);
            ((SQLitePreparedStatement)localObject3).step();
            ((SQLitePreparedStatement)localObject3).dispose();
            ((NativeByteBuffer)localObject2).reuse();
          }
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void updateChannelUsers(final int paramInt, final ArrayList<TLRPC.ChannelParticipant> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          long l = -paramInt;
          Object localObject1 = MessagesStorage.this.database;
          Object localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          ((SQLiteDatabase)localObject1).executeFast("DELETE FROM channel_users_v2 WHERE did = " + l).stepThis().dispose();
          MessagesStorage.this.database.beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
          int i = (int)(System.currentTimeMillis() / 1000L);
          for (int j = 0; j < paramArrayList.size(); j++)
          {
            localObject1 = (TLRPC.ChannelParticipant)paramArrayList.get(j);
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindLong(1, l);
            localSQLitePreparedStatement.bindInteger(2, ((TLRPC.ChannelParticipant)localObject1).user_id);
            localSQLitePreparedStatement.bindInteger(3, i);
            localObject2 = new org/telegram/tgnet/NativeByteBuffer;
            ((NativeByteBuffer)localObject2).<init>(((TLRPC.ChannelParticipant)localObject1).getObjectSize());
            ((TLRPC.ChannelParticipant)localObject1).serializeToStream((AbstractSerializedData)localObject2);
            localSQLitePreparedStatement.bindByteBuffer(4, (NativeByteBuffer)localObject2);
            ((NativeByteBuffer)localObject2).reuse();
            localSQLitePreparedStatement.step();
            i--;
          }
          localSQLitePreparedStatement.dispose();
          MessagesStorage.this.database.commitTransaction();
          MessagesStorage.this.loadChatInfo(paramInt, null, false, true);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
      }
    });
  }
  
  public void updateChatInfo(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final int paramInt5)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject2;
        Object localObject3;
        int i;
        for (;;)
        {
          try
          {
            localObject1 = MessagesStorage.this.database;
            localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            localObject3 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + paramInt1, new Object[0]);
            localObject2 = null;
            new ArrayList();
            localObject1 = localObject2;
            if (((SQLiteCursor)localObject3).next())
            {
              NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject3).byteBufferValue(0);
              localObject1 = localObject2;
              if (localNativeByteBuffer != null)
              {
                localObject1 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                ((TLRPC.ChatFull)localObject1).pinned_msg_id = ((SQLiteCursor)localObject3).intValue(1);
              }
            }
            ((SQLiteCursor)localObject3).dispose();
            if (!(localObject1 instanceof TLRPC.TL_chatFull)) {
              continue;
            }
            if (paramInt3 != 1) {
              continue;
            }
            i = 0;
            if (i < ((TLRPC.ChatFull)localObject1).participants.participants.size())
            {
              if (((TLRPC.ChatParticipant)((TLRPC.ChatFull)localObject1).participants.participants.get(i)).user_id != paramInt2) {
                continue;
              }
              ((TLRPC.ChatFull)localObject1).participants.participants.remove(i);
            }
          }
          catch (Exception localException)
          {
            Object localObject1;
            FileLog.e(localException);
            continue;
            if (paramInt3 != 2) {
              continue;
            }
            i = 0;
          }
          ((TLRPC.ChatFull)localObject1).participants.version = paramInt5;
          localObject2 = new org/telegram/messenger/MessagesStorage$45$1;
          ((1)localObject2).<init>(this, (TLRPC.ChatFull)localObject1);
          AndroidUtilities.runOnUIThread((Runnable)localObject2);
          localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
          localObject3 = new org/telegram/tgnet/NativeByteBuffer;
          ((NativeByteBuffer)localObject3).<init>(((TLRPC.ChatFull)localObject1).getObjectSize());
          ((TLRPC.ChatFull)localObject1).serializeToStream((AbstractSerializedData)localObject3);
          ((SQLitePreparedStatement)localObject2).bindInteger(1, paramInt1);
          ((SQLitePreparedStatement)localObject2).bindByteBuffer(2, (NativeByteBuffer)localObject3);
          ((SQLitePreparedStatement)localObject2).bindInteger(3, ((TLRPC.ChatFull)localObject1).pinned_msg_id);
          ((SQLitePreparedStatement)localObject2).step();
          ((SQLitePreparedStatement)localObject2).dispose();
          ((NativeByteBuffer)localObject3).reuse();
          return;
          i++;
          continue;
          if (paramInt3 != 0) {
            continue;
          }
          localObject2 = ((TLRPC.ChatFull)localObject1).participants.participants.iterator();
          if (((Iterator)localObject2).hasNext())
          {
            if (((TLRPC.ChatParticipant)((Iterator)localObject2).next()).user_id != paramInt2) {}
          }
          else
          {
            localObject2 = new org/telegram/tgnet/TLRPC$TL_chatParticipant;
            ((TLRPC.TL_chatParticipant)localObject2).<init>();
            ((TLRPC.TL_chatParticipant)localObject2).user_id = paramInt2;
            ((TLRPC.TL_chatParticipant)localObject2).inviter_id = paramInt4;
            ((TLRPC.TL_chatParticipant)localObject2).date = ConnectionsManager.getInstance(MessagesStorage.this.currentAccount).getCurrentTime();
            ((TLRPC.ChatFull)localObject1).participants.participants.add(localObject2);
          }
        }
        while (i < localException.participants.participants.size())
        {
          localObject3 = (TLRPC.ChatParticipant)localException.participants.participants.get(i);
          if (((TLRPC.ChatParticipant)localObject3).user_id == paramInt2)
          {
            if (paramInt4 == 1)
            {
              localObject2 = new org/telegram/tgnet/TLRPC$TL_chatParticipantAdmin;
              ((TLRPC.TL_chatParticipantAdmin)localObject2).<init>();
              ((TLRPC.ChatParticipant)localObject2).user_id = ((TLRPC.ChatParticipant)localObject3).user_id;
              ((TLRPC.ChatParticipant)localObject2).date = ((TLRPC.ChatParticipant)localObject3).date;
            }
            for (((TLRPC.ChatParticipant)localObject2).inviter_id = ((TLRPC.ChatParticipant)localObject3).inviter_id;; ((TLRPC.ChatParticipant)localObject2).inviter_id = ((TLRPC.ChatParticipant)localObject3).inviter_id)
            {
              localException.participants.participants.set(i, localObject2);
              break;
              localObject2 = new org/telegram/tgnet/TLRPC$TL_chatParticipant;
              ((TLRPC.TL_chatParticipant)localObject2).<init>();
              ((TLRPC.ChatParticipant)localObject2).user_id = ((TLRPC.ChatParticipant)localObject3).user_id;
              ((TLRPC.ChatParticipant)localObject2).date = ((TLRPC.ChatParticipant)localObject3).date;
            }
          }
          i++;
        }
      }
    });
  }
  
  public void updateChatInfo(final TLRPC.ChatFull paramChatFull, final boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          try
          {
            if (paramBoolean)
            {
              localObject1 = MessagesStorage.this.database;
              localObject2 = new java/lang/StringBuilder;
              ((StringBuilder)localObject2).<init>();
              localObject1 = ((SQLiteDatabase)localObject1).queryFinalized("SELECT uid FROM chat_settings_v2 WHERE uid = " + paramChatFull.id, new Object[0]);
              boolean bool = ((SQLiteCursor)localObject1).next();
              ((SQLiteCursor)localObject1).dispose();
              if (!bool) {
                return;
              }
            }
          }
          catch (Exception localException)
          {
            Object localObject1;
            Object localObject2;
            int i;
            int j;
            long l;
            int k;
            int m;
            int n;
            FileLog.e(localException);
            continue;
          }
          localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
          localObject1 = new org/telegram/tgnet/NativeByteBuffer;
          ((NativeByteBuffer)localObject1).<init>(paramChatFull.getObjectSize());
          paramChatFull.serializeToStream((AbstractSerializedData)localObject1);
          ((SQLitePreparedStatement)localObject2).bindInteger(1, paramChatFull.id);
          ((SQLitePreparedStatement)localObject2).bindByteBuffer(2, (NativeByteBuffer)localObject1);
          ((SQLitePreparedStatement)localObject2).bindInteger(3, paramChatFull.pinned_msg_id);
          ((SQLitePreparedStatement)localObject2).step();
          ((SQLitePreparedStatement)localObject2).dispose();
          ((NativeByteBuffer)localObject1).reuse();
          if ((paramChatFull instanceof TLRPC.TL_channelFull))
          {
            localObject2 = MessagesStorage.this.database;
            localObject1 = new java/lang/StringBuilder;
            ((StringBuilder)localObject1).<init>();
            localObject1 = ((SQLiteDatabase)localObject2).queryFinalized("SELECT date, pts, last_mid, inbox_max, outbox_max, pinned, unread_count_i FROM dialogs WHERE did = " + -paramChatFull.id, new Object[0]);
            if ((((SQLiteCursor)localObject1).next()) && (((SQLiteCursor)localObject1).intValue(3) < paramChatFull.read_inbox_max_id))
            {
              i = ((SQLiteCursor)localObject1).intValue(0);
              j = ((SQLiteCursor)localObject1).intValue(1);
              l = ((SQLiteCursor)localObject1).longValue(2);
              k = ((SQLiteCursor)localObject1).intValue(4);
              m = ((SQLiteCursor)localObject1).intValue(5);
              n = ((SQLiteCursor)localObject1).intValue(6);
              localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
              ((SQLitePreparedStatement)localObject2).bindLong(1, -paramChatFull.id);
              ((SQLitePreparedStatement)localObject2).bindInteger(2, i);
              ((SQLitePreparedStatement)localObject2).bindInteger(3, paramChatFull.unread_count);
              ((SQLitePreparedStatement)localObject2).bindLong(4, l);
              ((SQLitePreparedStatement)localObject2).bindInteger(5, paramChatFull.read_inbox_max_id);
              ((SQLitePreparedStatement)localObject2).bindInteger(6, Math.max(k, paramChatFull.read_outbox_max_id));
              ((SQLitePreparedStatement)localObject2).bindLong(7, 0L);
              ((SQLitePreparedStatement)localObject2).bindInteger(8, n);
              ((SQLitePreparedStatement)localObject2).bindInteger(9, j);
              ((SQLitePreparedStatement)localObject2).bindInteger(10, 0);
              ((SQLitePreparedStatement)localObject2).bindInteger(11, m);
              ((SQLitePreparedStatement)localObject2).step();
              ((SQLitePreparedStatement)localObject2).dispose();
            }
            ((SQLiteCursor)localObject1).dispose();
          }
        }
      }
    });
  }
  
  public void updateChatParticipants(final TLRPC.ChatParticipants paramChatParticipants)
  {
    if (paramChatParticipants == null) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            Object localObject1 = MessagesStorage.this.database;
            Object localObject2 = new java/lang/StringBuilder;
            ((StringBuilder)localObject2).<init>();
            SQLiteCursor localSQLiteCursor = ((SQLiteDatabase)localObject1).queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + paramChatParticipants.chat_id, new Object[0]);
            localObject1 = null;
            new ArrayList();
            localObject2 = localObject1;
            NativeByteBuffer localNativeByteBuffer;
            if (localSQLiteCursor.next())
            {
              localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
              localObject2 = localObject1;
              if (localNativeByteBuffer != null)
              {
                localObject2 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                ((TLRPC.ChatFull)localObject2).pinned_msg_id = localSQLiteCursor.intValue(1);
              }
            }
            localSQLiteCursor.dispose();
            if ((localObject2 instanceof TLRPC.TL_chatFull))
            {
              ((TLRPC.ChatFull)localObject2).participants = paramChatParticipants;
              localObject1 = new org/telegram/messenger/MessagesStorage$37$1;
              ((1)localObject1).<init>(this, (TLRPC.ChatFull)localObject2);
              AndroidUtilities.runOnUIThread((Runnable)localObject1);
              localObject1 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
              localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
              localNativeByteBuffer.<init>(((TLRPC.ChatFull)localObject2).getObjectSize());
              ((TLRPC.ChatFull)localObject2).serializeToStream(localNativeByteBuffer);
              ((SQLitePreparedStatement)localObject1).bindInteger(1, ((TLRPC.ChatFull)localObject2).id);
              ((SQLitePreparedStatement)localObject1).bindByteBuffer(2, localNativeByteBuffer);
              ((SQLitePreparedStatement)localObject1).bindInteger(3, ((TLRPC.ChatFull)localObject2).pinned_msg_id);
              ((SQLitePreparedStatement)localObject1).step();
              ((SQLitePreparedStatement)localObject1).dispose();
              localNativeByteBuffer.reuse();
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e(localException);
            }
          }
        }
      });
    }
  }
  
  public void updateDialogsWithDeletedMessages(final ArrayList<Integer> paramArrayList, final ArrayList<Long> paramArrayList1, boolean paramBoolean, final int paramInt)
  {
    if ((paramArrayList.isEmpty()) && (paramInt == 0)) {}
    for (;;)
    {
      return;
      if (paramBoolean) {
        this.storageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(paramArrayList, paramArrayList1, paramInt);
          }
        });
      } else {
        updateDialogsWithDeletedMessagesInternal(paramArrayList, paramArrayList1, paramInt);
      }
    }
  }
  
  public void updateDialogsWithReadMessages(final SparseLongArray paramSparseLongArray1, final SparseLongArray paramSparseLongArray2, final ArrayList<Long> paramArrayList, boolean paramBoolean)
  {
    if ((isEmpty(paramSparseLongArray1)) && (isEmpty(paramArrayList))) {}
    for (;;)
    {
      return;
      if (paramBoolean) {
        this.storageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesStorage.this.updateDialogsWithReadMessagesInternal(null, paramSparseLongArray1, paramSparseLongArray2, paramArrayList);
          }
        });
      } else {
        updateDialogsWithReadMessagesInternal(null, paramSparseLongArray1, paramSparseLongArray2, paramArrayList);
      }
    }
  }
  
  public void updateEncryptedChat(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i = 1;
          NativeByteBuffer localNativeByteBuffer1 = null;
          SQLitePreparedStatement localSQLitePreparedStatement1 = null;
          localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
          localObject2 = localNativeByteBuffer1;
          for (;;)
          {
            try
            {
              if (paramEncryptedChat.key_hash != null)
              {
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localNativeByteBuffer1;
                if (paramEncryptedChat.key_hash.length >= 16) {}
              }
              else
              {
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localNativeByteBuffer1;
                if (paramEncryptedChat.auth_key != null)
                {
                  localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                  localObject2 = localNativeByteBuffer1;
                  paramEncryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(paramEncryptedChat.auth_key);
                }
              }
              localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
              localObject2 = localNativeByteBuffer1;
              localSQLitePreparedStatement1 = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ?, admin_id = ?, mtproto_seq = ? WHERE uid = ?");
              localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
              localObject2 = localSQLitePreparedStatement1;
              NativeByteBuffer localNativeByteBuffer2 = new org/telegram/tgnet/NativeByteBuffer;
              localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
              localObject2 = localSQLitePreparedStatement1;
              localNativeByteBuffer2.<init>(paramEncryptedChat.getObjectSize());
              localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
              localObject2 = localSQLitePreparedStatement1;
              NativeByteBuffer localNativeByteBuffer3 = new org/telegram/tgnet/NativeByteBuffer;
              localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
              localObject2 = localSQLitePreparedStatement1;
              if (paramEncryptedChat.a_or_b != null)
              {
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                j = paramEncryptedChat.a_or_b.length;
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer3.<init>(j);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer1 = new org/telegram/tgnet/NativeByteBuffer;
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                if (paramEncryptedChat.auth_key == null) {
                  continue;
                }
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                j = paramEncryptedChat.auth_key.length;
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer1.<init>(j);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                NativeByteBuffer localNativeByteBuffer4 = new org/telegram/tgnet/NativeByteBuffer;
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                if (paramEncryptedChat.future_auth_key == null) {
                  continue;
                }
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                j = paramEncryptedChat.future_auth_key.length;
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer4.<init>(j);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                NativeByteBuffer localNativeByteBuffer5 = new org/telegram/tgnet/NativeByteBuffer;
                j = i;
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                if (paramEncryptedChat.key_hash != null)
                {
                  localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                  localObject2 = localSQLitePreparedStatement1;
                  j = paramEncryptedChat.key_hash.length;
                }
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer5.<init>(j);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                paramEncryptedChat.serializeToStream(localNativeByteBuffer2);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindByteBuffer(1, localNativeByteBuffer2);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                if (paramEncryptedChat.a_or_b != null)
                {
                  localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                  localObject2 = localSQLitePreparedStatement1;
                  localNativeByteBuffer3.writeBytes(paramEncryptedChat.a_or_b);
                }
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                if (paramEncryptedChat.auth_key != null)
                {
                  localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                  localObject2 = localSQLitePreparedStatement1;
                  localNativeByteBuffer1.writeBytes(paramEncryptedChat.auth_key);
                }
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                if (paramEncryptedChat.future_auth_key != null)
                {
                  localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                  localObject2 = localSQLitePreparedStatement1;
                  localNativeByteBuffer4.writeBytes(paramEncryptedChat.future_auth_key);
                }
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                if (paramEncryptedChat.key_hash != null)
                {
                  localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                  localObject2 = localSQLitePreparedStatement1;
                  localNativeByteBuffer5.writeBytes(paramEncryptedChat.key_hash);
                }
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindByteBuffer(2, localNativeByteBuffer3);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindByteBuffer(3, localNativeByteBuffer1);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(4, paramEncryptedChat.ttl);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(5, paramEncryptedChat.layer);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(6, paramEncryptedChat.seq_in);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(7, paramEncryptedChat.seq_out);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(8, paramEncryptedChat.key_use_count_in << 16 | paramEncryptedChat.key_use_count_out);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindLong(9, paramEncryptedChat.exchange_id);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(10, paramEncryptedChat.key_create_date);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindLong(11, paramEncryptedChat.future_key_fingerprint);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindByteBuffer(12, localNativeByteBuffer4);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindByteBuffer(13, localNativeByteBuffer5);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(14, paramEncryptedChat.in_seq_no);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(15, paramEncryptedChat.admin_id);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(16, paramEncryptedChat.mtproto_seq);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.bindInteger(17, paramEncryptedChat.id);
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localSQLitePreparedStatement1.step();
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer2.reuse();
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer3.reuse();
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer1.reuse();
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer4.reuse();
                localSQLitePreparedStatement2 = localSQLitePreparedStatement1;
                localObject2 = localSQLitePreparedStatement1;
                localNativeByteBuffer5.reuse();
                if (localSQLitePreparedStatement1 != null) {
                  localSQLitePreparedStatement1.dispose();
                }
                return;
              }
            }
            catch (Exception localException)
            {
              int j;
              localObject2 = localSQLitePreparedStatement2;
              FileLog.e(localException);
              if (localSQLitePreparedStatement2 == null) {
                continue;
              }
              localSQLitePreparedStatement2.dispose();
              continue;
            }
            finally
            {
              if (localObject2 == null) {
                continue;
              }
              ((SQLitePreparedStatement)localObject2).dispose();
            }
            j = 1;
            continue;
            j = 1;
            continue;
            j = 1;
          }
        }
      });
    }
  }
  
  public void updateEncryptedChatLayer(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          Object localObject1 = null;
          Object localObject2 = null;
          try
          {
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(1, paramEncryptedChat.layer);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(2, paramEncryptedChat.id);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.step();
            if (localSQLitePreparedStatement != null) {
              localSQLitePreparedStatement.dispose();
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              localObject1 = localObject2;
              FileLog.e(localException);
              if (localObject2 != null) {
                ((SQLitePreparedStatement)localObject2).dispose();
              }
            }
          }
          finally
          {
            if (localObject1 != null) {
              ((SQLitePreparedStatement)localObject1).dispose();
            }
          }
        }
      });
    }
  }
  
  public void updateEncryptedChatSeq(final TLRPC.EncryptedChat paramEncryptedChat, final boolean paramBoolean)
  {
    if (paramEncryptedChat == null) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          Object localObject1 = null;
          Object localObject2 = null;
          try
          {
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(1, paramEncryptedChat.seq_in);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(2, paramEncryptedChat.seq_out);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(3, paramEncryptedChat.key_use_count_in << 16 | paramEncryptedChat.key_use_count_out);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(4, paramEncryptedChat.in_seq_no);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(5, paramEncryptedChat.mtproto_seq);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(6, paramEncryptedChat.id);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.step();
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            if (paramBoolean)
            {
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              long l = paramEncryptedChat.id;
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN (SELECT m.mid FROM messages as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d)", new Object[] { Long.valueOf(l << 32), Integer.valueOf(paramEncryptedChat.in_seq_no) })).stepThis().dispose();
            }
            if (localSQLitePreparedStatement != null) {
              localSQLitePreparedStatement.dispose();
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              localObject1 = localObject2;
              FileLog.e(localException);
              if (localObject2 != null) {
                ((SQLitePreparedStatement)localObject2).dispose();
              }
            }
          }
          finally
          {
            if (localObject1 != null) {
              ((SQLitePreparedStatement)localObject1).dispose();
            }
          }
        }
      });
    }
  }
  
  public void updateEncryptedChatTTL(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null) {}
    for (;;)
    {
      return;
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          Object localObject1 = null;
          Object localObject2 = null;
          try
          {
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(1, paramEncryptedChat.ttl);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(2, paramEncryptedChat.id);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.step();
            if (localSQLitePreparedStatement != null) {
              localSQLitePreparedStatement.dispose();
            }
            return;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              localObject1 = localObject2;
              FileLog.e(localException);
              if (localObject2 != null) {
                ((SQLitePreparedStatement)localObject2).dispose();
              }
            }
          }
          finally
          {
            if (localObject1 != null) {
              ((SQLitePreparedStatement)localObject1).dispose();
            }
          }
        }
      });
    }
  }
  
  public long[] updateMessageStateAndId(final long paramLong, Integer paramInteger, final int paramInt1, final int paramInt2, boolean paramBoolean, final int paramInt3)
  {
    if (paramBoolean) {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.updateMessageStateAndIdInternal(paramLong, paramInt1, paramInt2, paramInt3, this.val$channelId);
        }
      });
    }
    for (paramInteger = null;; paramInteger = updateMessageStateAndIdInternal(paramLong, paramInteger, paramInt1, paramInt2, paramInt3)) {
      return paramInteger;
    }
  }
  
  public void updateUsers(final ArrayList<TLRPC.User> paramArrayList, final boolean paramBoolean1, final boolean paramBoolean2, boolean paramBoolean3)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      if (paramBoolean3) {
        this.storageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesStorage.this.updateUsersInternal(paramArrayList, paramBoolean1, paramBoolean2);
          }
        });
      } else {
        updateUsersInternal(paramArrayList, paramBoolean1, paramBoolean2);
      }
    }
  }
  
  private class Hole
  {
    public int end;
    public int start;
    public int type;
    
    public Hole(int paramInt1, int paramInt2)
    {
      this.start = paramInt1;
      this.end = paramInt2;
    }
    
    public Hole(int paramInt1, int paramInt2, int paramInt3)
    {
      this.type = paramInt1;
      this.start = paramInt2;
      this.end = paramInt3;
    }
  }
  
  public static abstract interface IntCallback
  {
    public abstract void run(int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MessagesStorage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */