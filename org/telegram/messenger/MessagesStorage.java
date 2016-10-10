package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.query.BotQuery;
import org.telegram.messenger.query.MessagesQuery;
import org.telegram.messenger.query.SharedMediaQuery;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputMedia;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.ReplyMarkup;
import org.telegram.tgnet.TLRPC.TL_channelFull;
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
import org.telegram.tgnet.TLRPC.TL_inputMediaGame;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageFwdHeader;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.WallPaper;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.messages_Dialogs;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;

public class MessagesStorage
{
  private static volatile MessagesStorage Instance = null;
  public static int lastDateValue = 0;
  public static int lastPtsValue = 0;
  public static int lastQtsValue = 0;
  public static int lastSecretVersion;
  public static int lastSeqValue = 0;
  public static int secretG;
  public static byte[] secretPBytes;
  private File cacheFile;
  private SQLiteDatabase database;
  private int lastSavedDate = 0;
  private int lastSavedPts = 0;
  private int lastSavedQts = 0;
  private int lastSavedSeq = 0;
  private AtomicLong lastTaskId = new AtomicLong(System.currentTimeMillis());
  private DispatchQueue storageQueue = new DispatchQueue("storageQueue");
  
  static
  {
    lastSecretVersion = 0;
    secretPBytes = null;
    secretG = 0;
  }
  
  public MessagesStorage()
  {
    this.storageQueue.setPriority(10);
    openDatabase();
  }
  
  public static void addUsersAndChatsFromMessage(TLRPC.Message paramMessage, ArrayList<Integer> paramArrayList1, ArrayList<Integer> paramArrayList2)
  {
    if (paramMessage.from_id != 0)
    {
      if (paramMessage.from_id <= 0) {
        break label274;
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
      i = 0;
      while (i < paramMessage.action.users.size())
      {
        localObject = (Integer)paramMessage.action.users.get(i);
        if (!paramArrayList1.contains(localObject)) {
          paramArrayList1.add(localObject);
        }
        i += 1;
      }
      label274:
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
          i += 1;
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
    }
    if ((paramMessage.ttl < 0) && (!paramArrayList2.contains(Integer.valueOf(-paramMessage.ttl)))) {
      paramArrayList2.add(Integer.valueOf(-paramMessage.ttl));
    }
  }
  
  private void closeHolesInTable(String paramString, long paramLong, int paramInt1, int paramInt2)
    throws Exception
  {
    Object localObject3;
    Object localObject1;
    Object localObject2;
    int i;
    int j;
    try
    {
      localObject3 = this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM " + paramString + " WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), new Object[0]);
      localObject1 = null;
      while (((SQLiteCursor)localObject3).next())
      {
        localObject2 = localObject1;
        if (localObject1 == null) {
          localObject2 = new ArrayList();
        }
        i = ((SQLiteCursor)localObject3).intValue(0);
        j = ((SQLiteCursor)localObject3).intValue(1);
        if (i == j)
        {
          localObject1 = localObject2;
          if (i == 1) {
            break;
          }
        }
        else
        {
          ((ArrayList)localObject2).add(new Hole(i, j));
          localObject1 = localObject2;
          continue;
          return;
        }
      }
    }
    catch (Exception paramString)
    {
      FileLog.e("tmessages", paramString);
    }
    for (;;)
    {
      ((SQLiteCursor)localObject3).dispose();
      if (localObject1 != null)
      {
        i = 0;
        while (i < ((ArrayList)localObject1).size())
        {
          localObject2 = (Hole)((ArrayList)localObject1).get(i);
          if ((paramInt2 >= ((Hole)localObject2).end - 1) && (paramInt1 <= ((Hole)localObject2).start + 1))
          {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM " + paramString + " WHERE uid = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject2).start), Integer.valueOf(((Hole)localObject2).end) })).stepThis().dispose();
          }
          else if (paramInt2 >= ((Hole)localObject2).end - 1)
          {
            j = ((Hole)localObject2).end;
            if (j != paramInt1) {
              try
              {
                this.database.executeFast(String.format(Locale.US, "UPDATE " + paramString + " SET end = %d WHERE uid = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt1), Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject2).start), Integer.valueOf(((Hole)localObject2).end) })).stepThis().dispose();
              }
              catch (Exception localException1)
              {
                FileLog.e("tmessages", localException1);
              }
            }
          }
          else if (paramInt1 <= localException1.start + 1)
          {
            j = localException1.start;
            if (j != paramInt2) {
              try
              {
                this.database.executeFast(String.format(Locale.US, "UPDATE " + paramString + " SET start = %d WHERE uid = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt2), Long.valueOf(paramLong), Integer.valueOf(localException1.start), Integer.valueOf(localException1.end) })).stepThis().dispose();
              }
              catch (Exception localException2)
              {
                FileLog.e("tmessages", localException2);
              }
            }
          }
          else
          {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM " + paramString + " WHERE uid = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(localException2.start), Integer.valueOf(localException2.end) })).stepThis().dispose();
            localObject3 = this.database.executeFast("REPLACE INTO " + paramString + " VALUES(?, ?, ?)");
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
          i += 1;
        }
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
        break label107;
      }
    }
    label107:
    for (int j = 1;; j = 0)
    {
      paramSQLitePreparedStatement2.bindInteger(3, j);
      paramSQLitePreparedStatement2.bindInteger(4, paramInt);
      paramSQLitePreparedStatement2.step();
      i += 1;
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
  
  private void fixUnsupportedMedia(TLRPC.Message paramMessage)
  {
    if (paramMessage == null) {}
    do
    {
      do
      {
        return;
        if (!(paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported_old)) {
          break;
        }
      } while (paramMessage.media.bytes.length != 0);
      paramMessage.media.bytes = new byte[1];
      paramMessage.media.bytes[0] = 57;
      return;
    } while (!(paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported));
    paramMessage.media = new TLRPC.TL_messageMediaUnsupported_old();
    paramMessage.media.bytes = new byte[1];
    paramMessage.media.bytes[0] = 57;
    paramMessage.flags |= 0x200;
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
  
  public static MessagesStorage getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          MessagesStorage localMessagesStorage2 = Instance;
          localObject1 = localMessagesStorage2;
          if (localMessagesStorage2 == null) {
            localObject1 = new MessagesStorage();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (MessagesStorage)localObject1;
          return (MessagesStorage)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localMessagesStorage1;
  }
  
  private int getMessageMediaType(TLRPC.Message paramMessage)
  {
    if (((paramMessage instanceof TLRPC.TL_message_secret)) && ((((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessage.ttl > 0) && (paramMessage.ttl <= 60)) || (MessageObject.isVoiceMessage(paramMessage)) || (MessageObject.isVideoMessage(paramMessage)))) {
      return 1;
    }
    if (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) || (MessageObject.isVideoMessage(paramMessage))) {
      return 0;
    }
    return -1;
  }
  
  private boolean isValidKeyboardToSave(TLRPC.Message paramMessage)
  {
    return (paramMessage.reply_markup != null) && (!(paramMessage.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) && ((!paramMessage.reply_markup.selective) || (paramMessage.mentioned));
  }
  
  private void loadPendingTasks()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          final long l1;
          NativeByteBuffer localNativeByteBuffer;
          try
          {
            SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
            if (!localSQLiteCursor.next()) {
              break label356;
            }
            l1 = localSQLiteCursor.longValue(0);
            localNativeByteBuffer = localSQLiteCursor.byteBufferValue(1);
            if (localNativeByteBuffer == null) {
              continue;
            }
            switch (localNativeByteBuffer.readInt32(false))
            {
            case 0: 
              localNativeByteBuffer.reuse();
              continue;
              localObject = TLRPC.Chat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          final Object localObject;
          if (localObject != null)
          {
            Utilities.stageQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                MessagesController.getInstance().loadUnknownChannel(localObject, l1);
              }
            });
            continue;
            final int i = localNativeByteBuffer.readInt32(false);
            final int j = localNativeByteBuffer.readInt32(false);
            Utilities.stageQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                MessagesController.getInstance().getChannelDifference(i, j, l1);
              }
            });
            continue;
            localObject = new TLRPC.TL_dialog();
            ((TLRPC.TL_dialog)localObject).id = localNativeByteBuffer.readInt64(false);
            ((TLRPC.TL_dialog)localObject).top_message = localNativeByteBuffer.readInt32(false);
            ((TLRPC.TL_dialog)localObject).read_inbox_max_id = localNativeByteBuffer.readInt32(false);
            ((TLRPC.TL_dialog)localObject).read_outbox_max_id = localNativeByteBuffer.readInt32(false);
            ((TLRPC.TL_dialog)localObject).unread_count = localNativeByteBuffer.readInt32(false);
            ((TLRPC.TL_dialog)localObject).last_message_date = localNativeByteBuffer.readInt32(false);
            ((TLRPC.TL_dialog)localObject).pts = localNativeByteBuffer.readInt32(false);
            ((TLRPC.TL_dialog)localObject).flags = localNativeByteBuffer.readInt32(false);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.getInstance().checkLastDialogMessage(localObject, this.val$peer, l1);
              }
            });
            continue;
            long l2 = localNativeByteBuffer.readInt64(false);
            localObject = TLRPC.InputPeer.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            TLRPC.TL_inputMediaGame localTL_inputMediaGame = (TLRPC.TL_inputMediaGame)TLRPC.InputMedia.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            SendMessagesHelper.getInstance().sendGame((TLRPC.InputPeer)localObject, localTL_inputMediaGame, l2, l1);
            continue;
            label356:
            localException.dispose();
            return;
          }
        }
      }
    });
  }
  
  private void markMessagesAsDeletedInternal(ArrayList<Integer> paramArrayList, int paramInt)
  {
    int j = 0;
    if (paramInt != 0) {}
    try
    {
      Object localObject1 = new StringBuilder(paramArrayList.size());
      int i = 0;
      long l1;
      while (i < paramArrayList.size())
      {
        l1 = ((Integer)paramArrayList.get(i)).intValue();
        long l2 = paramInt;
        if (((StringBuilder)localObject1).length() > 0) {
          ((StringBuilder)localObject1).append(',');
        }
        ((StringBuilder)localObject1).append(l1 | l2 << 32);
        i += 1;
      }
      localObject1 = ((StringBuilder)localObject1).toString();
      Object localObject2 = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state FROM messages WHERE mid IN(%s)", new Object[] { localObject1 }), new Object[0]);
      ArrayList localArrayList = new ArrayList();
      i = j;
      for (;;)
      {
        j = i;
        int k = i;
        Object localObject4;
        try
        {
          if (((SQLiteCursor)localObject2).next())
          {
            j = i;
            l1 = ((SQLiteCursor)localObject2).longValue(0);
            k = i;
            if (paramInt != 0)
            {
              k = i;
              j = i;
              if (((SQLiteCursor)localObject2).intValue(2) == 0) {
                k = i + 1;
              }
            }
            i = k;
            if ((int)l1 != 0) {
              continue;
            }
            j = k;
            localObject4 = ((SQLiteCursor)localObject2).byteBufferValue(1);
            i = k;
            if (localObject4 == null) {
              continue;
            }
            j = k;
            Object localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
            j = k;
            ((NativeByteBuffer)localObject4).reuse();
            i = k;
            if (localObject3 == null) {
              continue;
            }
            j = k;
            if (!(((TLRPC.Message)localObject3).media instanceof TLRPC.TL_messageMediaPhoto)) {
              break label609;
            }
            j = k;
            localObject3 = ((TLRPC.Message)localObject3).media.photo.sizes.iterator();
            for (;;)
            {
              i = k;
              j = k;
              if (!((Iterator)localObject3).hasNext()) {
                break;
              }
              j = k;
              localObject4 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject3).next());
              if (localObject4 != null)
              {
                j = k;
                if (((File)localObject4).toString().length() > 0)
                {
                  j = k;
                  localArrayList.add(localObject4);
                }
              }
            }
          }
          localObject1 = TextUtils.join(",", paramArrayList);
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          k = j;
          ((SQLiteCursor)localObject2).dispose();
          FileLoader.getInstance().deleteFiles(localArrayList, 0);
          if ((paramInt != 0) && (k != 0))
          {
            l1 = -paramInt;
            localObject2 = this.database.executeFast("UPDATE dialogs SET unread_count = ((SELECT unread_count FROM dialogs WHERE did = ?) - ?) WHERE did = ?");
            ((SQLitePreparedStatement)localObject2).requery();
            ((SQLitePreparedStatement)localObject2).bindLong(1, l1);
            ((SQLitePreparedStatement)localObject2).bindInteger(2, k);
            ((SQLitePreparedStatement)localObject2).bindLong(3, l1);
            ((SQLitePreparedStatement)localObject2).step();
            ((SQLitePreparedStatement)localObject2).dispose();
          }
          this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
          this.database.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
          this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
          this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
          this.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
          BotQuery.clearBotKeyboard(0L, paramArrayList);
          return;
        }
        break;
        label609:
        i = k;
        j = k;
        if ((localException.media instanceof TLRPC.TL_messageMediaDocument))
        {
          j = k;
          localObject4 = FileLoader.getPathToAttach(localException.media.document);
          if (localObject4 != null)
          {
            j = k;
            if (((File)localObject4).toString().length() > 0)
            {
              j = k;
              localArrayList.add(localObject4);
            }
          }
          j = k;
          File localFile = FileLoader.getPathToAttach(localException.media.document.thumb);
          i = k;
          if (localFile != null)
          {
            i = k;
            j = k;
            if (localFile.toString().length() > 0)
            {
              j = k;
              localArrayList.add(localFile);
              i = k;
            }
          }
        }
      }
      return;
    }
    catch (Exception paramArrayList)
    {
      FileLog.e("tmessages", paramArrayList);
    }
  }
  
  private void markMessagesAsReadInternal(SparseArray<Long> paramSparseArray1, SparseArray<Long> paramSparseArray2, HashMap<Integer, Integer> paramHashMap)
  {
    if (paramSparseArray1 != null) {}
    for (int i = 0;; i = 0) {
      do
      {
        try
        {
          while (i < paramSparseArray1.size())
          {
            int j = paramSparseArray1.keyAt(i);
            long l = ((Long)paramSparseArray1.get(j)).longValue();
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", new Object[] { Integer.valueOf(j), Long.valueOf(l) })).stepThis().dispose();
            i += 1;
            continue;
            while (i < paramSparseArray2.size())
            {
              j = paramSparseArray2.keyAt(i);
              l = ((Long)paramSparseArray2.get(j)).longValue();
              this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", new Object[] { Integer.valueOf(j), Long.valueOf(l) })).stepThis().dispose();
              i += 1;
            }
            if ((paramHashMap != null) && (!paramHashMap.isEmpty()))
            {
              paramSparseArray1 = paramHashMap.entrySet().iterator();
              while (paramSparseArray1.hasNext())
              {
                paramSparseArray2 = (Map.Entry)paramSparseArray1.next();
                l = ((Integer)paramSparseArray2.getKey()).intValue();
                i = ((Integer)paramSparseArray2.getValue()).intValue();
                paramSparseArray2 = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
                paramSparseArray2.requery();
                paramSparseArray2.bindLong(1, l << 32);
                paramSparseArray2.bindInteger(2, i);
                paramSparseArray2.step();
                paramSparseArray2.dispose();
              }
            }
            return;
          }
        }
        catch (Exception paramSparseArray1)
        {
          FileLog.e("tmessages", paramSparseArray1);
        }
      } while (paramSparseArray2 == null);
    }
  }
  
  private void putChatsInternal(ArrayList<TLRPC.Chat> paramArrayList)
    throws Exception
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    SQLitePreparedStatement localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
    int i = 0;
    Object localObject3;
    if (i < paramArrayList.size())
    {
      localObject3 = (TLRPC.Chat)paramArrayList.get(i);
      Object localObject1 = localObject3;
      SQLiteCursor localSQLiteCursor;
      if (((TLRPC.Chat)localObject3).min)
      {
        localSQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[] { Integer.valueOf(((TLRPC.Chat)localObject3).id) }), new Object[0]);
        localObject1 = localObject3;
        if (!localSQLiteCursor.next()) {}
      }
      for (;;)
      {
        try
        {
          NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
          localObject1 = localObject3;
          if (localNativeByteBuffer != null)
          {
            localChat = TLRPC.Chat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            localObject1 = localObject3;
            if (localChat != null)
            {
              localChat.title = ((TLRPC.Chat)localObject3).title;
              localChat.photo = ((TLRPC.Chat)localObject3).photo;
              localChat.broadcast = ((TLRPC.Chat)localObject3).broadcast;
              localChat.verified = ((TLRPC.Chat)localObject3).verified;
              localChat.megagroup = ((TLRPC.Chat)localObject3).megagroup;
              localChat.democracy = ((TLRPC.Chat)localObject3).democracy;
              if (((TLRPC.Chat)localObject3).username == null) {
                continue;
              }
              localChat.username = ((TLRPC.Chat)localObject3).username;
              localChat.flags |= 0x40;
              localObject1 = localChat;
            }
          }
        }
        catch (Exception localException)
        {
          TLRPC.Chat localChat;
          FileLog.e("tmessages", localException);
          Object localObject2 = localObject3;
          continue;
          localSQLitePreparedStatement.bindString(2, "");
          continue;
        }
        localSQLiteCursor.dispose();
        localSQLitePreparedStatement.requery();
        localObject3 = new NativeByteBuffer(((TLRPC.Chat)localObject1).getObjectSize());
        ((TLRPC.Chat)localObject1).serializeToStream((AbstractSerializedData)localObject3);
        localSQLitePreparedStatement.bindInteger(1, ((TLRPC.Chat)localObject1).id);
        if (((TLRPC.Chat)localObject1).title == null) {
          continue;
        }
        localSQLitePreparedStatement.bindString(2, ((TLRPC.Chat)localObject1).title.toLowerCase());
        localSQLitePreparedStatement.bindByteBuffer(3, (NativeByteBuffer)localObject3);
        localSQLitePreparedStatement.step();
        ((NativeByteBuffer)localObject3).reuse();
        i += 1;
        break;
        localChat.username = null;
        localChat.flags &= 0xFFFFFFBF;
      }
    }
    localSQLitePreparedStatement.dispose();
  }
  
  private void putDialogsInternal(TLRPC.messages_Dialogs parammessages_Dialogs)
  {
    for (;;)
    {
      int i;
      Object localObject;
      SQLitePreparedStatement localSQLitePreparedStatement1;
      SQLitePreparedStatement localSQLitePreparedStatement2;
      SQLitePreparedStatement localSQLitePreparedStatement3;
      SQLitePreparedStatement localSQLitePreparedStatement4;
      SQLitePreparedStatement localSQLitePreparedStatement5;
      try
      {
        this.database.beginTransaction();
        HashMap localHashMap = new HashMap();
        i = 0;
        if (i < parammessages_Dialogs.messages.size())
        {
          localObject = (TLRPC.Message)parammessages_Dialogs.messages.get(i);
          localHashMap.put(Long.valueOf(((TLRPC.Message)localObject).dialog_id), localObject);
          i += 1;
          continue;
        }
        if (parammessages_Dialogs.dialogs.isEmpty()) {
          break label841;
        }
        localObject = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)");
        localSQLitePreparedStatement1 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        localSQLitePreparedStatement2 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
        localSQLitePreparedStatement3 = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
        localSQLitePreparedStatement4 = this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
        localSQLitePreparedStatement5 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        i = 0;
        if (i >= parammessages_Dialogs.dialogs.size()) {
          break label811;
        }
        TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)parammessages_Dialogs.dialogs.get(i);
        if (localTL_dialog.id == 0L)
        {
          if (localTL_dialog.peer.user_id != 0) {
            localTL_dialog.id = localTL_dialog.peer.user_id;
          }
        }
        else
        {
          j = 0;
          TLRPC.Message localMessage = (TLRPC.Message)localHashMap.get(Long.valueOf(localTL_dialog.id));
          if (localMessage != null)
          {
            int k = Math.max(localMessage.date, 0);
            if (isValidKeyboardToSave(localMessage)) {
              BotQuery.putBotKeyboard(localTL_dialog.id, localMessage);
            }
            fixUnsupportedMedia(localMessage);
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localMessage.getObjectSize());
            localMessage.serializeToStream(localNativeByteBuffer);
            l2 = localMessage.id;
            l1 = l2;
            if (localMessage.to_id.channel_id != 0) {
              l1 = l2 | localMessage.to_id.channel_id << 32;
            }
            ((SQLitePreparedStatement)localObject).requery();
            ((SQLitePreparedStatement)localObject).bindLong(1, l1);
            ((SQLitePreparedStatement)localObject).bindLong(2, localTL_dialog.id);
            ((SQLitePreparedStatement)localObject).bindInteger(3, MessageObject.getUnreadFlags(localMessage));
            ((SQLitePreparedStatement)localObject).bindInteger(4, localMessage.send_state);
            ((SQLitePreparedStatement)localObject).bindInteger(5, localMessage.date);
            ((SQLitePreparedStatement)localObject).bindByteBuffer(6, localNativeByteBuffer);
            if (!MessageObject.isOut(localMessage)) {
              break label872;
            }
            j = 1;
            ((SQLitePreparedStatement)localObject).bindInteger(7, j);
            ((SQLitePreparedStatement)localObject).bindInteger(8, 0);
            if ((localMessage.flags & 0x400) == 0) {
              break label877;
            }
            j = localMessage.views;
            ((SQLitePreparedStatement)localObject).bindInteger(9, j);
            ((SQLitePreparedStatement)localObject).bindInteger(10, 0);
            ((SQLitePreparedStatement)localObject).step();
            if (SharedMediaQuery.canAddMessageToMedia(localMessage))
            {
              localSQLitePreparedStatement2.requery();
              localSQLitePreparedStatement2.bindLong(1, l1);
              localSQLitePreparedStatement2.bindLong(2, localTL_dialog.id);
              localSQLitePreparedStatement2.bindInteger(3, localMessage.date);
              localSQLitePreparedStatement2.bindInteger(4, SharedMediaQuery.getMediaType(localMessage));
              localSQLitePreparedStatement2.bindByteBuffer(5, localNativeByteBuffer);
              localSQLitePreparedStatement2.step();
            }
            localNativeByteBuffer.reuse();
            createFirstHoles(localTL_dialog.id, localSQLitePreparedStatement4, localSQLitePreparedStatement5, localMessage.id);
            j = k;
          }
          long l2 = localTL_dialog.top_message;
          long l1 = l2;
          if (localTL_dialog.peer.channel_id != 0) {
            l1 = l2 | localTL_dialog.peer.channel_id << 32;
          }
          localSQLitePreparedStatement1.requery();
          localSQLitePreparedStatement1.bindLong(1, localTL_dialog.id);
          localSQLitePreparedStatement1.bindInteger(2, j);
          localSQLitePreparedStatement1.bindInteger(3, localTL_dialog.unread_count);
          localSQLitePreparedStatement1.bindLong(4, l1);
          localSQLitePreparedStatement1.bindInteger(5, localTL_dialog.read_inbox_max_id);
          localSQLitePreparedStatement1.bindInteger(6, localTL_dialog.read_outbox_max_id);
          localSQLitePreparedStatement1.bindLong(7, 0L);
          localSQLitePreparedStatement1.bindInteger(8, 0);
          localSQLitePreparedStatement1.bindInteger(9, localTL_dialog.pts);
          localSQLitePreparedStatement1.bindInteger(10, 0);
          localSQLitePreparedStatement1.step();
          if (localTL_dialog.notify_settings == null) {
            break label865;
          }
          localSQLitePreparedStatement3.requery();
          localSQLitePreparedStatement3.bindLong(1, localTL_dialog.id);
          if (localTL_dialog.notify_settings.mute_until == 0) {
            break label882;
          }
          j = 1;
          localSQLitePreparedStatement3.bindInteger(2, j);
          localSQLitePreparedStatement3.step();
          break label865;
        }
        if (localTL_dialog.peer.chat_id != 0)
        {
          localTL_dialog.id = (-localTL_dialog.peer.chat_id);
          continue;
        }
        localTL_dialog.id = (-localTL_dialog.peer.channel_id);
      }
      catch (Exception parammessages_Dialogs)
      {
        FileLog.e("tmessages", parammessages_Dialogs);
        return;
      }
      continue;
      label811:
      ((SQLitePreparedStatement)localObject).dispose();
      localSQLitePreparedStatement1.dispose();
      localSQLitePreparedStatement2.dispose();
      localSQLitePreparedStatement3.dispose();
      localSQLitePreparedStatement4.dispose();
      localSQLitePreparedStatement5.dispose();
      label841:
      putUsersInternal(parammessages_Dialogs.users);
      putChatsInternal(parammessages_Dialogs.chats);
      this.database.commitTransaction();
      return;
      label865:
      i += 1;
      continue;
      label872:
      int j = 0;
      continue;
      label877:
      j = 0;
      continue;
      label882:
      j = 0;
    }
  }
  
  private void putMessagesInternal(ArrayList<TLRPC.Message> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, int paramInt, boolean paramBoolean3)
  {
    if (paramBoolean3) {
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
            localObject1 = this.database.queryFinalized("SELECT last_mid FROM dialogs WHERE did = " + ((TLRPC.Message)localObject1).dialog_id, new Object[0]);
            if (((SQLiteCursor)localObject1).next()) {
              i = ((SQLiteCursor)localObject1).intValue(0);
            }
            ((SQLiteCursor)localObject1).dispose();
            if (i == 0) {
              break;
            }
            return;
          }
          if (((TLRPC.Message)localObject1).to_id.chat_id != 0) {
            ((TLRPC.Message)localObject1).dialog_id = (-((TLRPC.Message)localObject1).to_id.chat_id);
          } else {
            ((TLRPC.Message)localObject1).dialog_id = (-((TLRPC.Message)localObject1).to_id.channel_id);
          }
        }
        catch (Exception paramArrayList)
        {
          FileLog.e("tmessages", paramArrayList);
          return;
        }
      }
    }
    if (paramBoolean1) {
      this.database.beginTransaction();
    }
    HashMap localHashMap2 = new HashMap();
    HashMap localHashMap1 = new HashMap();
    Map.Entry localEntry = null;
    HashMap localHashMap4 = new HashMap();
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject1 = null;
    StringBuilder localStringBuilder = new StringBuilder();
    HashMap localHashMap5 = new HashMap();
    HashMap localHashMap3 = new HashMap();
    SQLitePreparedStatement localSQLitePreparedStatement1 = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)");
    Object localObject7 = null;
    SQLitePreparedStatement localSQLitePreparedStatement2 = this.database.executeFast("REPLACE INTO randoms VALUES(?, ?)");
    SQLitePreparedStatement localSQLitePreparedStatement3 = this.database.executeFast("REPLACE INTO download_queue VALUES(?, ?, ?, ?)");
    SQLitePreparedStatement localSQLitePreparedStatement4 = this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
    int i = 0;
    long l1;
    Object localObject5;
    Object localObject6;
    if (i < paramArrayList.size())
    {
      TLRPC.Message localMessage = (TLRPC.Message)paramArrayList.get(i);
      l2 = localMessage.id;
      if (localMessage.dialog_id == 0L)
      {
        if (localMessage.to_id.user_id != 0) {
          localMessage.dialog_id = localMessage.to_id.user_id;
        }
      }
      else
      {
        l1 = l2;
        if (localMessage.to_id.channel_id != 0) {
          l1 = l2 | localMessage.to_id.channel_id << 32;
        }
        if ((MessageObject.isUnread(localMessage)) && (!MessageObject.isOut(localMessage)))
        {
          localObject5 = (Integer)localHashMap5.get(Long.valueOf(localMessage.dialog_id));
          localObject4 = localObject5;
          if (localObject5 == null)
          {
            localObject5 = this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + localMessage.dialog_id, new Object[0]);
            if (!((SQLiteCursor)localObject5).next()) {
              break label822;
            }
          }
        }
      }
      label822:
      for (localObject4 = Integer.valueOf(((SQLiteCursor)localObject5).intValue(0));; localObject4 = Integer.valueOf(0))
      {
        ((SQLiteCursor)localObject5).dispose();
        localHashMap5.put(Long.valueOf(localMessage.dialog_id), localObject4);
        if ((localMessage.id < 0) || (((Integer)localObject4).intValue() < localMessage.id))
        {
          if (localStringBuilder.length() > 0) {
            localStringBuilder.append(",");
          }
          localStringBuilder.append(l1);
          localHashMap3.put(Long.valueOf(l1), Long.valueOf(localMessage.dialog_id));
        }
        localObject6 = localObject1;
        localObject4 = localObject3;
        localObject5 = localObject2;
        if (SharedMediaQuery.canAddMessageToMedia(localMessage))
        {
          localObject4 = localObject3;
          if (localObject3 == null)
          {
            localObject4 = new StringBuilder();
            localObject2 = new HashMap();
            localObject1 = new HashMap();
          }
          if (((StringBuilder)localObject4).length() > 0) {
            ((StringBuilder)localObject4).append(",");
          }
          ((StringBuilder)localObject4).append(l1);
          ((HashMap)localObject2).put(Long.valueOf(l1), Long.valueOf(localMessage.dialog_id));
          ((HashMap)localObject1).put(Long.valueOf(l1), Integer.valueOf(SharedMediaQuery.getMediaType(localMessage)));
          localObject5 = localObject2;
          localObject6 = localObject1;
        }
        if (!isValidKeyboardToSave(localMessage)) {
          break label3380;
        }
        localObject1 = (TLRPC.Message)localHashMap4.get(Long.valueOf(localMessage.dialog_id));
        if ((localObject1 != null) && (((TLRPC.Message)localObject1).id >= localMessage.id)) {
          break label3380;
        }
        localHashMap4.put(Long.valueOf(localMessage.dialog_id), localMessage);
        break label3380;
        if (localMessage.to_id.chat_id != 0)
        {
          localMessage.dialog_id = (-localMessage.to_id.chat_id);
          break;
        }
        localMessage.dialog_id = (-localMessage.to_id.channel_id);
        break;
      }
    }
    Object localObject4 = localHashMap4.entrySet().iterator();
    while (((Iterator)localObject4).hasNext())
    {
      localObject5 = (Map.Entry)((Iterator)localObject4).next();
      BotQuery.putBotKeyboard(((Long)((Map.Entry)localObject5).getKey()).longValue(), (TLRPC.Message)((Map.Entry)localObject5).getValue());
    }
    localObject4 = localEntry;
    if (localObject3 != null)
    {
      localObject3 = this.database.queryFinalized("SELECT mid FROM media_v2 WHERE mid IN(" + ((StringBuilder)localObject3).toString() + ")", new Object[0]);
      while (((SQLiteCursor)localObject3).next()) {
        ((HashMap)localObject2).remove(Long.valueOf(((SQLiteCursor)localObject3).longValue(0)));
      }
      ((SQLiteCursor)localObject3).dispose();
      localObject5 = new HashMap();
      localObject6 = ((HashMap)localObject2).entrySet().iterator();
      localObject4 = localObject5;
      if (((Iterator)localObject6).hasNext())
      {
        localEntry = (Map.Entry)((Iterator)localObject6).next();
        localObject4 = (Integer)((HashMap)localObject1).get(localEntry.getKey());
        localObject3 = (HashMap)((HashMap)localObject5).get(localObject4);
        if (localObject3 == null)
        {
          localObject3 = new HashMap();
          localObject2 = Integer.valueOf(0);
          ((HashMap)localObject5).put(localObject4, localObject3);
        }
        for (;;)
        {
          localObject4 = localObject2;
          if (localObject2 == null) {
            localObject4 = Integer.valueOf(0);
          }
          i = ((Integer)localObject4).intValue();
          ((HashMap)localObject3).put(localEntry.getValue(), Integer.valueOf(i + 1));
          break;
          localObject2 = (Integer)((HashMap)localObject3).get(localEntry.getValue());
        }
      }
    }
    label1312:
    int k;
    int m;
    label1676:
    final int j;
    label2232:
    int i4;
    int i5;
    int i6;
    label2745:
    int i7;
    int i8;
    int i3;
    int n;
    int i1;
    int i2;
    if (localStringBuilder.length() > 0)
    {
      localObject1 = this.database.queryFinalized("SELECT mid FROM messages WHERE mid IN(" + localStringBuilder.toString() + ")", new Object[0]);
      while (((SQLiteCursor)localObject1).next()) {
        localHashMap3.remove(Long.valueOf(((SQLiteCursor)localObject1).longValue(0)));
      }
      ((SQLiteCursor)localObject1).dispose();
      localObject3 = localHashMap3.values().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject5 = (Long)((Iterator)localObject3).next();
        localObject2 = (Integer)localHashMap1.get(localObject5);
        localObject1 = localObject2;
        if (localObject2 == null) {
          localObject1 = Integer.valueOf(0);
        }
        localHashMap1.put(localObject5, Integer.valueOf(((Integer)localObject1).intValue() + 1));
        continue;
        if (k < paramArrayList.size())
        {
          localObject5 = (TLRPC.Message)paramArrayList.get(k);
          fixUnsupportedMedia((TLRPC.Message)localObject5);
          localSQLitePreparedStatement1.requery();
          l1 = ((TLRPC.Message)localObject5).id;
          if (((TLRPC.Message)localObject5).local_id != 0) {
            l1 = ((TLRPC.Message)localObject5).local_id;
          }
          l2 = l1;
          if (((TLRPC.Message)localObject5).to_id.channel_id != 0) {
            l2 = l1 | ((TLRPC.Message)localObject5).to_id.channel_id << 32;
          }
          localObject3 = new NativeByteBuffer(((TLRPC.Message)localObject5).getObjectSize());
          ((TLRPC.Message)localObject5).serializeToStream((AbstractSerializedData)localObject3);
          m = 1;
          i = m;
          if (((TLRPC.Message)localObject5).action != null)
          {
            i = m;
            if ((((TLRPC.Message)localObject5).action instanceof TLRPC.TL_messageEncryptedAction))
            {
              i = m;
              if (!(((TLRPC.Message)localObject5).action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))
              {
                i = m;
                if (!(((TLRPC.Message)localObject5).action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) {
                  i = 0;
                }
              }
            }
          }
          if (i != 0)
          {
            localObject2 = (TLRPC.Message)localHashMap2.get(Long.valueOf(((TLRPC.Message)localObject5).dialog_id));
            if ((localObject2 == null) || (((TLRPC.Message)localObject5).date > ((TLRPC.Message)localObject2).date) || ((((TLRPC.Message)localObject5).id > 0) && (((TLRPC.Message)localObject2).id > 0) && (((TLRPC.Message)localObject5).id > ((TLRPC.Message)localObject2).id)) || ((((TLRPC.Message)localObject5).id < 0) && (((TLRPC.Message)localObject2).id < 0) && (((TLRPC.Message)localObject5).id < ((TLRPC.Message)localObject2).id))) {
              localHashMap2.put(Long.valueOf(((TLRPC.Message)localObject5).dialog_id), localObject5);
            }
          }
          localSQLitePreparedStatement1.bindLong(1, l2);
          localSQLitePreparedStatement1.bindLong(2, ((TLRPC.Message)localObject5).dialog_id);
          localSQLitePreparedStatement1.bindInteger(3, MessageObject.getUnreadFlags((TLRPC.Message)localObject5));
          localSQLitePreparedStatement1.bindInteger(4, ((TLRPC.Message)localObject5).send_state);
          localSQLitePreparedStatement1.bindInteger(5, ((TLRPC.Message)localObject5).date);
          localSQLitePreparedStatement1.bindByteBuffer(6, (NativeByteBuffer)localObject3);
          if (!MessageObject.isOut((TLRPC.Message)localObject5)) {
            break label3431;
          }
          i = 1;
          localSQLitePreparedStatement1.bindInteger(7, i);
          localSQLitePreparedStatement1.bindInteger(8, ((TLRPC.Message)localObject5).ttl);
          if ((((TLRPC.Message)localObject5).flags & 0x400) != 0)
          {
            localSQLitePreparedStatement1.bindInteger(9, ((TLRPC.Message)localObject5).views);
            localSQLitePreparedStatement1.bindInteger(10, 0);
            localSQLitePreparedStatement1.step();
            if (((TLRPC.Message)localObject5).random_id != 0L)
            {
              localSQLitePreparedStatement2.requery();
              localSQLitePreparedStatement2.bindLong(1, ((TLRPC.Message)localObject5).random_id);
              localSQLitePreparedStatement2.bindLong(2, l2);
              localSQLitePreparedStatement2.step();
            }
            localObject2 = localObject1;
            if (SharedMediaQuery.canAddMessageToMedia((TLRPC.Message)localObject5))
            {
              localObject2 = localObject1;
              if (localObject1 == null) {
                localObject2 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
              }
              ((SQLitePreparedStatement)localObject2).requery();
              ((SQLitePreparedStatement)localObject2).bindLong(1, l2);
              ((SQLitePreparedStatement)localObject2).bindLong(2, ((TLRPC.Message)localObject5).dialog_id);
              ((SQLitePreparedStatement)localObject2).bindInteger(3, ((TLRPC.Message)localObject5).date);
              ((SQLitePreparedStatement)localObject2).bindInteger(4, SharedMediaQuery.getMediaType((TLRPC.Message)localObject5));
              ((SQLitePreparedStatement)localObject2).bindByteBuffer(5, (NativeByteBuffer)localObject3);
              ((SQLitePreparedStatement)localObject2).step();
            }
            if (((((TLRPC.Message)localObject5).media instanceof TLRPC.TL_messageMediaWebPage)) && ((((TLRPC.Message)localObject5).media.webpage instanceof TLRPC.TL_webPagePending)))
            {
              localSQLitePreparedStatement4.requery();
              localSQLitePreparedStatement4.bindLong(1, ((TLRPC.Message)localObject5).media.webpage.id);
              localSQLitePreparedStatement4.bindLong(2, l2);
              localSQLitePreparedStatement4.step();
            }
            ((NativeByteBuffer)localObject3).reuse();
            if (((TLRPC.Message)localObject5).to_id.channel_id != 0)
            {
              m = j;
              if (!((TLRPC.Message)localObject5).post) {
                break label3414;
              }
            }
            m = j;
            if (((TLRPC.Message)localObject5).date < ConnectionsManager.getInstance().getCurrentTime() - 3600) {
              break label3414;
            }
            m = j;
            if (paramInt == 0) {
              break label3414;
            }
            if (!(((TLRPC.Message)localObject5).media instanceof TLRPC.TL_messageMediaPhoto))
            {
              m = j;
              if (!(((TLRPC.Message)localObject5).media instanceof TLRPC.TL_messageMediaDocument)) {
                break label3414;
              }
            }
            m = 0;
            l2 = 0L;
            localObject3 = null;
            if (!MessageObject.isVoiceMessage((TLRPC.Message)localObject5)) {
              break label2232;
            }
            l1 = l2;
            localObject1 = localObject3;
            i = m;
            if ((paramInt & 0x2) != 0)
            {
              l1 = l2;
              localObject1 = localObject3;
              i = m;
              if (((TLRPC.Message)localObject5).media.document.size < 5242880)
              {
                l1 = ((TLRPC.Message)localObject5).media.document.id;
                i = 2;
                localObject1 = new TLRPC.TL_messageMediaDocument();
                ((TLRPC.MessageMedia)localObject1).caption = "";
                ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject5).media.document;
              }
            }
          }
          for (;;)
          {
            m = j;
            if (localObject1 == null) {
              break label3414;
            }
            m = j | i;
            localSQLitePreparedStatement3.requery();
            localObject3 = new NativeByteBuffer(((TLRPC.MessageMedia)localObject1).getObjectSize());
            ((TLRPC.MessageMedia)localObject1).serializeToStream((AbstractSerializedData)localObject3);
            localSQLitePreparedStatement3.bindLong(1, l1);
            localSQLitePreparedStatement3.bindInteger(2, i);
            localSQLitePreparedStatement3.bindInteger(3, ((TLRPC.Message)localObject5).date);
            localSQLitePreparedStatement3.bindByteBuffer(4, (NativeByteBuffer)localObject3);
            localSQLitePreparedStatement3.step();
            ((NativeByteBuffer)localObject3).reuse();
            break label3414;
            localSQLitePreparedStatement1.bindInteger(9, getMessageMediaType((TLRPC.Message)localObject5));
            break;
            if ((((TLRPC.Message)localObject5).media instanceof TLRPC.TL_messageMediaPhoto))
            {
              l1 = l2;
              localObject1 = localObject3;
              i = m;
              if ((paramInt & 0x1) != 0)
              {
                l1 = l2;
                localObject1 = localObject3;
                i = m;
                if (FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Message)localObject5).media.photo.sizes, AndroidUtilities.getPhotoSize()) != null)
                {
                  l1 = ((TLRPC.Message)localObject5).media.photo.id;
                  i = 1;
                  localObject1 = new TLRPC.TL_messageMediaPhoto();
                  ((TLRPC.MessageMedia)localObject1).caption = "";
                  ((TLRPC.MessageMedia)localObject1).photo = ((TLRPC.Message)localObject5).media.photo;
                }
              }
            }
            else if (MessageObject.isVideoMessage((TLRPC.Message)localObject5))
            {
              l1 = l2;
              localObject1 = localObject3;
              i = m;
              if ((paramInt & 0x4) != 0)
              {
                l1 = ((TLRPC.Message)localObject5).media.document.id;
                i = 4;
                localObject1 = new TLRPC.TL_messageMediaDocument();
                ((TLRPC.MessageMedia)localObject1).caption = "";
                ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject5).media.document;
              }
            }
            else
            {
              l1 = l2;
              localObject1 = localObject3;
              i = m;
              if ((((TLRPC.Message)localObject5).media instanceof TLRPC.TL_messageMediaDocument))
              {
                l1 = l2;
                localObject1 = localObject3;
                i = m;
                if (!MessageObject.isMusicMessage((TLRPC.Message)localObject5))
                {
                  l1 = l2;
                  localObject1 = localObject3;
                  i = m;
                  if (!MessageObject.isGifDocument(((TLRPC.Message)localObject5).media.document))
                  {
                    l1 = l2;
                    localObject1 = localObject3;
                    i = m;
                    if ((paramInt & 0x8) != 0)
                    {
                      l1 = ((TLRPC.Message)localObject5).media.document.id;
                      i = 8;
                      localObject1 = new TLRPC.TL_messageMediaDocument();
                      ((TLRPC.MessageMedia)localObject1).caption = "";
                      ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject5).media.document;
                    }
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
        localObject1 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        paramArrayList = new HashMap();
        paramArrayList.putAll(localHashMap2);
        localObject2 = paramArrayList.entrySet().iterator();
        for (;;)
        {
          if (((Iterator)localObject2).hasNext())
          {
            localObject3 = (Long)((Map.Entry)((Iterator)localObject2).next()).getKey();
            if (((Long)localObject3).longValue() != 0L)
            {
              localObject5 = (TLRPC.Message)localHashMap2.get(localObject3);
              i = 0;
              if (localObject5 != null) {
                i = ((TLRPC.Message)localObject5).to_id.channel_id;
              }
              paramArrayList = this.database.queryFinalized("SELECT date, unread_count, pts, last_mid, inbox_max, outbox_max FROM dialogs WHERE did = " + localObject3, new Object[0]);
              i4 = 0;
              i5 = 0;
              i6 = 0;
              if (i == 0) {
                break label3460;
              }
              paramInt = 1;
              i7 = 0;
              i8 = 0;
              if (paramArrayList.next())
              {
                i3 = paramArrayList.intValue(0);
                n = paramArrayList.intValue(1);
                k = paramArrayList.intValue(2);
                i1 = paramArrayList.intValue(3);
                i2 = paramArrayList.intValue(4);
                m = paramArrayList.intValue(5);
                label2800:
                paramArrayList.dispose();
                paramArrayList = (Integer)localHashMap1.get(localObject3);
                if (paramArrayList != null) {
                  break label3054;
                }
                paramArrayList = Integer.valueOf(0);
                label2824:
                if (localObject5 == null) {
                  break label3466;
                }
              }
            }
          }
        }
      }
    }
    label3054:
    label3380:
    label3414:
    label3431:
    label3460:
    label3466:
    for (long l2 = ((TLRPC.Message)localObject5).id;; l2 = i1)
    {
      l1 = l2;
      if (localObject5 != null)
      {
        l1 = l2;
        if (((TLRPC.Message)localObject5).local_id != 0) {
          l1 = ((TLRPC.Message)localObject5).local_id;
        }
      }
      for (;;)
      {
        ((SQLitePreparedStatement)localObject1).requery();
        ((SQLitePreparedStatement)localObject1).bindLong(1, ((Long)localObject3).longValue());
        if ((localObject5 != null) && ((!paramBoolean2) || (i3 == 0))) {
          ((SQLitePreparedStatement)localObject1).bindInteger(2, ((TLRPC.Message)localObject5).date);
        }
        for (;;)
        {
          ((SQLitePreparedStatement)localObject1).bindInteger(3, paramArrayList.intValue() + n);
          ((SQLitePreparedStatement)localObject1).bindLong(4, l2);
          ((SQLitePreparedStatement)localObject1).bindInteger(5, i2);
          ((SQLitePreparedStatement)localObject1).bindInteger(6, m);
          ((SQLitePreparedStatement)localObject1).bindLong(7, 0L);
          ((SQLitePreparedStatement)localObject1).bindInteger(8, 0);
          ((SQLitePreparedStatement)localObject1).bindInteger(9, k);
          ((SQLitePreparedStatement)localObject1).bindInteger(10, 0);
          ((SQLitePreparedStatement)localObject1).step();
          break;
          i3 = i4;
          i2 = i7;
          i1 = i5;
          n = i6;
          m = i8;
          k = paramInt;
          if (i == 0) {
            break label2800;
          }
          MessagesController.getInstance().checkChannelInviter(i);
          i3 = i4;
          i2 = i7;
          i1 = i5;
          n = i6;
          m = i8;
          k = paramInt;
          break label2800;
          localHashMap1.put(localObject3, Integer.valueOf(paramArrayList.intValue() + n));
          break label2824;
          ((SQLitePreparedStatement)localObject1).bindInteger(2, i3);
        }
        ((SQLitePreparedStatement)localObject1).dispose();
        if (localObject4 != null)
        {
          paramArrayList = this.database.executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
          localObject1 = ((HashMap)localObject4).entrySet().iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject3 = (Map.Entry)((Iterator)localObject1).next();
            localObject2 = (Integer)((Map.Entry)localObject3).getKey();
            localObject3 = ((HashMap)((Map.Entry)localObject3).getValue()).entrySet().iterator();
            while (((Iterator)localObject3).hasNext())
            {
              localObject4 = (Map.Entry)((Iterator)localObject3).next();
              l1 = ((Long)((Map.Entry)localObject4).getKey()).longValue();
              paramInt = (int)l1;
              paramInt = -1;
              localObject5 = this.database.queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[] { Long.valueOf(l1), localObject2 }), new Object[0]);
              if (((SQLiteCursor)localObject5).next()) {
                paramInt = ((SQLiteCursor)localObject5).intValue(0);
              }
              ((SQLiteCursor)localObject5).dispose();
              if (paramInt != -1)
              {
                paramArrayList.requery();
                i = ((Integer)((Map.Entry)localObject4).getValue()).intValue();
                paramArrayList.bindLong(1, l1);
                paramArrayList.bindInteger(2, ((Integer)localObject2).intValue());
                paramArrayList.bindInteger(3, paramInt + i);
                paramArrayList.step();
              }
            }
          }
          paramArrayList.dispose();
        }
        if (paramBoolean1) {
          this.database.commitTransaction();
        }
        MessagesController.getInstance().processDialogsUpdateRead(localHashMap1);
        if (j != 0) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MediaController.getInstance().newDownloadObjectsAvailable(j);
            }
          });
        }
        return;
        i += 1;
        localObject1 = localObject6;
        localObject3 = localObject4;
        localObject2 = localObject5;
        break;
        j = 0;
        k = 0;
        localObject1 = localObject7;
        break label1312;
        k += 1;
        j = m;
        localObject1 = localObject2;
        break label1312;
        i = 0;
        break label1676;
        l2 = l1;
        if (i != 0) {
          l2 = l1 | i << 32;
        }
      }
      paramInt = 0;
      break label2745;
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
      FileLog.e("tmessages", paramArrayList);
    }
  }
  
  private void putUsersInternal(ArrayList<TLRPC.User> paramArrayList)
    throws Exception
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    SQLitePreparedStatement localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
    int i = 0;
    Object localObject3;
    if (i < paramArrayList.size())
    {
      localObject3 = (TLRPC.User)paramArrayList.get(i);
      Object localObject1 = localObject3;
      SQLiteCursor localSQLiteCursor;
      if (((TLRPC.User)localObject3).min)
      {
        localSQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", new Object[] { Integer.valueOf(((TLRPC.User)localObject3).id) }), new Object[0]);
        localObject1 = localObject3;
        if (!localSQLiteCursor.next()) {}
      }
      for (;;)
      {
        try
        {
          NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
          localObject1 = localObject3;
          if (localNativeByteBuffer == null) {
            continue;
          }
          localUser = TLRPC.User.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          localNativeByteBuffer.reuse();
          localObject1 = localObject3;
          if (localUser == null) {
            continue;
          }
          if (((TLRPC.User)localObject3).username == null) {
            continue;
          }
          localUser.username = ((TLRPC.User)localObject3).username;
          localUser.flags |= 0x8;
          if (((TLRPC.User)localObject3).photo == null) {
            continue;
          }
          localUser.photo = ((TLRPC.User)localObject3).photo;
          localUser.flags |= 0x20;
        }
        catch (Exception localException)
        {
          TLRPC.User localUser;
          FileLog.e("tmessages", localException);
          Object localObject2 = localObject3;
          continue;
          localUser.photo = null;
          localUser.flags &= 0xFFFFFFDF;
          continue;
          if (!(((TLRPC.User)localObject2).status instanceof TLRPC.TL_userStatusLastWeek)) {
            continue;
          }
          ((TLRPC.User)localObject2).status.expires = -101;
          continue;
          if (!(((TLRPC.User)localObject2).status instanceof TLRPC.TL_userStatusLastMonth)) {
            continue;
          }
          ((TLRPC.User)localObject2).status.expires = -102;
          continue;
          localSQLitePreparedStatement.bindInteger(3, 0);
          continue;
        }
        localObject1 = localUser;
        localSQLiteCursor.dispose();
        localSQLitePreparedStatement.requery();
        localObject3 = new NativeByteBuffer(((TLRPC.User)localObject1).getObjectSize());
        ((TLRPC.User)localObject1).serializeToStream((AbstractSerializedData)localObject3);
        localSQLitePreparedStatement.bindInteger(1, ((TLRPC.User)localObject1).id);
        localSQLitePreparedStatement.bindString(2, formatUserSearchName((TLRPC.User)localObject1));
        if (((TLRPC.User)localObject1).status == null) {
          continue;
        }
        if (!(((TLRPC.User)localObject1).status instanceof TLRPC.TL_userStatusRecently)) {
          continue;
        }
        ((TLRPC.User)localObject1).status.expires = -100;
        localSQLitePreparedStatement.bindInteger(3, ((TLRPC.User)localObject1).status.expires);
        localSQLitePreparedStatement.bindByteBuffer(4, (NativeByteBuffer)localObject3);
        localSQLitePreparedStatement.step();
        ((NativeByteBuffer)localObject3).reuse();
        i += 1;
        break;
        localUser.username = null;
        localUser.flags &= 0xFFFFFFF7;
      }
    }
    localSQLitePreparedStatement.dispose();
  }
  
  private void updateDialogsWithDeletedMessagesInternal(ArrayList<Integer> paramArrayList, int paramInt)
  {
    if (Thread.currentThread().getId() != this.storageQueue.getId()) {
      throw new RuntimeException("wrong db thread");
    }
    for (;;)
    {
      try
      {
        if (paramArrayList.isEmpty()) {
          break label597;
        }
        localObject = new ArrayList();
        if (paramInt != 0)
        {
          ((ArrayList)localObject).add(Long.valueOf(-paramInt));
          paramArrayList = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? )) WHERE did = ?");
          this.database.beginTransaction();
          i = 0;
          if (i >= ((ArrayList)localObject).size()) {
            break;
          }
          long l = ((Long)((ArrayList)localObject).get(i)).longValue();
          paramArrayList.requery();
          paramArrayList.bindLong(1, l);
          paramArrayList.bindLong(2, l);
          paramArrayList.bindLong(3, l);
          paramArrayList.step();
          i += 1;
          continue;
        }
        paramArrayList = TextUtils.join(",", paramArrayList);
        paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s)", new Object[] { paramArrayList }), new Object[0]);
        if (paramArrayList.next())
        {
          ((ArrayList)localObject).add(Long.valueOf(paramArrayList.longValue(0)));
          continue;
          return;
        }
      }
      catch (Exception paramArrayList)
      {
        FileLog.e("tmessages", paramArrayList);
      }
      paramArrayList.dispose();
      paramArrayList = this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0, last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? AND date != 0)) WHERE did = ?");
    }
    paramArrayList.dispose();
    this.database.commitTransaction();
    paramArrayList = TextUtils.join(",", (Iterable)localObject);
    label248:
    Object localObject = new TLRPC.messages_Dialogs();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList4 = new ArrayList();
    paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid WHERE d.did IN(%s)", new Object[] { paramArrayList }), new Object[0]);
    label322:
    TLRPC.TL_dialog localTL_dialog;
    if (paramArrayList.next())
    {
      localTL_dialog = new TLRPC.TL_dialog();
      localTL_dialog.id = paramArrayList.longValue(0);
      localTL_dialog.top_message = paramArrayList.intValue(1);
      localTL_dialog.read_inbox_max_id = paramArrayList.intValue(10);
      localTL_dialog.read_outbox_max_id = paramArrayList.intValue(11);
      localTL_dialog.unread_count = paramArrayList.intValue(2);
      localTL_dialog.last_message_date = paramArrayList.intValue(3);
      localTL_dialog.pts = paramArrayList.intValue(9);
      if (paramInt != 0) {
        break label813;
      }
    }
    label597:
    label813:
    for (int i = 0;; i = 1)
    {
      localTL_dialog.flags = i;
      ((TLRPC.messages_Dialogs)localObject).dialogs.add(localTL_dialog);
      NativeByteBuffer localNativeByteBuffer = paramArrayList.byteBufferValue(4);
      if (localNativeByteBuffer != null)
      {
        TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
        localNativeByteBuffer.reuse();
        MessageObject.setUnreadFlags(localMessage, paramArrayList.intValue(5));
        localMessage.id = paramArrayList.intValue(6);
        localMessage.send_state = paramArrayList.intValue(7);
        i = paramArrayList.intValue(8);
        if (i != 0) {
          localTL_dialog.last_message_date = i;
        }
        localMessage.dialog_id = localTL_dialog.id;
        ((TLRPC.messages_Dialogs)localObject).messages.add(localMessage);
        addUsersAndChatsFromMessage(localMessage, localArrayList2, localArrayList3);
      }
      i = (int)localTL_dialog.id;
      int j = (int)(localTL_dialog.id >> 32);
      if (i != 0)
      {
        if (j == 1)
        {
          if (localArrayList3.contains(Integer.valueOf(i))) {
            break label322;
          }
          localArrayList3.add(Integer.valueOf(i));
          break label322;
          paramArrayList = "" + -paramInt;
          break label248;
        }
        if (i > 0)
        {
          if (localArrayList2.contains(Integer.valueOf(i))) {
            break label322;
          }
          localArrayList2.add(Integer.valueOf(i));
          break label322;
        }
        if (localArrayList3.contains(Integer.valueOf(-i))) {
          break label322;
        }
        localArrayList3.add(Integer.valueOf(-i));
        break label322;
      }
      if (localArrayList4.contains(Integer.valueOf(j))) {
        break label322;
      }
      localArrayList4.add(Integer.valueOf(j));
      break label322;
      paramArrayList.dispose();
      if (!localArrayList4.isEmpty()) {
        getEncryptedChatsInternal(TextUtils.join(",", localArrayList4), localArrayList1, localArrayList2);
      }
      if (!localArrayList3.isEmpty()) {
        getChatsInternal(TextUtils.join(",", localArrayList3), ((TLRPC.messages_Dialogs)localObject).chats);
      }
      if (!localArrayList2.isEmpty()) {
        getUsersInternal(TextUtils.join(",", localArrayList2), ((TLRPC.messages_Dialogs)localObject).users);
      }
      if ((((TLRPC.messages_Dialogs)localObject).dialogs.isEmpty()) && (localArrayList1.isEmpty())) {
        break;
      }
      MessagesController.getInstance().processDialogsUpdate((TLRPC.messages_Dialogs)localObject, localArrayList1);
      return;
    }
  }
  
  private void updateDialogsWithReadMessagesInternal(ArrayList<Integer> paramArrayList, SparseArray<Long> paramSparseArray1, SparseArray<Long> paramSparseArray2)
  {
    HashMap localHashMap;
    label261:
    do
    {
      long l;
      for (;;)
      {
        try
        {
          localHashMap = new HashMap();
          if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
            break label261;
          }
          paramArrayList = TextUtils.join(",", paramArrayList);
          paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out FROM messages WHERE mid IN(%s)", new Object[] { paramArrayList }), new Object[0]);
          if (!paramArrayList.next()) {
            break;
          }
          if ((paramArrayList.intValue(2) == 0) && (paramArrayList.intValue(1) == 0))
          {
            l = paramArrayList.longValue(0);
            paramSparseArray1 = (Integer)localHashMap.get(Long.valueOf(l));
            if (paramSparseArray1 == null) {
              localHashMap.put(Long.valueOf(l), Integer.valueOf(1));
            } else {
              localHashMap.put(Long.valueOf(l), Integer.valueOf(paramSparseArray1.intValue() + 1));
            }
          }
        }
        catch (Exception paramArrayList)
        {
          FileLog.e("tmessages", paramArrayList);
          return;
        }
      }
      paramArrayList.dispose();
      while (!localHashMap.isEmpty())
      {
        this.database.beginTransaction();
        paramArrayList = this.database.executeFast("UPDATE dialogs SET unread_count = ? WHERE did = ?");
        paramSparseArray1 = localHashMap.entrySet().iterator();
        for (;;)
        {
          if (paramSparseArray1.hasNext())
          {
            paramSparseArray2 = (Map.Entry)paramSparseArray1.next();
            paramArrayList.requery();
            paramArrayList.bindInteger(1, ((Integer)paramSparseArray2.getValue()).intValue());
            paramArrayList.bindLong(2, ((Long)paramSparseArray2.getKey()).longValue());
            paramArrayList.step();
            continue;
            int j;
            if ((paramSparseArray1 != null) && (paramSparseArray1.size() != 0))
            {
              i = 0;
              while (i < paramSparseArray1.size())
              {
                j = paramSparseArray1.keyAt(i);
                l = ((Long)paramSparseArray1.get(j)).longValue();
                paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mid > %d AND read_state IN(0,2) AND out = 0", new Object[] { Integer.valueOf(j), Long.valueOf(l) }), new Object[0]);
                if (paramArrayList.next())
                {
                  int k = paramArrayList.intValue(0);
                  localHashMap.put(Long.valueOf(j), Integer.valueOf(k));
                }
                paramArrayList.dispose();
                paramArrayList = this.database.executeFast("UPDATE dialogs SET inbox_max = max((SELECT inbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                paramArrayList.requery();
                paramArrayList.bindLong(1, j);
                paramArrayList.bindInteger(2, (int)l);
                paramArrayList.bindLong(3, j);
                paramArrayList.step();
                paramArrayList.dispose();
                i += 1;
              }
            }
            if ((paramSparseArray2 == null) || (paramSparseArray2.size() == 0)) {
              break;
            }
            int i = 0;
            while (i < paramSparseArray2.size())
            {
              j = paramSparseArray2.keyAt(i);
              l = ((Long)paramSparseArray2.get(j)).longValue();
              paramArrayList = this.database.executeFast("UPDATE dialogs SET outbox_max = max((SELECT outbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
              paramArrayList.requery();
              paramArrayList.bindLong(1, j);
              paramArrayList.bindInteger(2, (int)l);
              paramArrayList.bindLong(3, j);
              paramArrayList.step();
              paramArrayList.dispose();
              i += 1;
            }
            break;
          }
        }
        paramArrayList.dispose();
        this.database.commitTransaction();
      }
    } while (localHashMap.isEmpty());
    MessagesController.getInstance().processDialogsUpdateRead(localHashMap);
  }
  
  private long[] updateMessageStateAndIdInternal(long paramLong, Integer paramInteger, int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject5 = null;
    localObject1 = null;
    SQLiteCursor localSQLiteCursor1 = null;
    long l3 = paramInt1;
    Object localObject6 = paramInteger;
    if (paramInteger == null)
    {
      localObject1 = localSQLiteCursor1;
      try
      {
        localSQLiteCursor1 = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id = %d LIMIT 1", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
        localObject6 = paramInteger;
        localObject1 = localSQLiteCursor1;
        localObject5 = localSQLiteCursor1;
        if (localSQLiteCursor1.next())
        {
          localObject1 = localSQLiteCursor1;
          localObject5 = localSQLiteCursor1;
          int i = localSQLiteCursor1.intValue(0);
          localObject6 = Integer.valueOf(i);
        }
        localObject7 = localSQLiteCursor1;
        localObject5 = localObject6;
        if (localSQLiteCursor1 != null)
        {
          localSQLiteCursor1.dispose();
          localObject5 = localObject6;
          localObject7 = localSQLiteCursor1;
        }
      }
      catch (Exception localException1)
      {
        for (;;)
        {
          localObject5 = localObject1;
          FileLog.e("tmessages", localException1);
          Object localObject7 = localObject1;
          localObject5 = paramInteger;
          if (localObject1 != null)
          {
            ((SQLiteCursor)localObject1).dispose();
            localObject7 = localObject1;
            localObject5 = paramInteger;
          }
        }
      }
      finally
      {
        if (localObject5 == null) {
          break label193;
        }
        ((SQLiteCursor)localObject5).dispose();
      }
      localObject1 = localObject7;
      localObject6 = localObject5;
      if (localObject5 == null) {
        return null;
      }
    }
    label193:
    paramLong = ((Integer)localObject6).intValue();
    long l2 = l3;
    l1 = paramLong;
    if (paramInt3 != 0)
    {
      l1 = paramLong | paramInt3 << 32;
      l2 = l3 | paramInt3 << 32;
    }
    l3 = 0L;
    paramInteger = (Integer)localObject1;
    long l4;
    try
    {
      SQLiteCursor localSQLiteCursor2 = this.database.queryFinalized(String.format(Locale.US, "SELECT uid FROM messages WHERE mid = %d LIMIT 1", new Object[] { Long.valueOf(l1) }), new Object[0]);
      paramLong = l3;
      paramInteger = localSQLiteCursor2;
      localObject1 = localSQLiteCursor2;
      if (localSQLiteCursor2.next())
      {
        paramInteger = localSQLiteCursor2;
        localObject1 = localSQLiteCursor2;
        paramLong = localSQLiteCursor2.longValue(0);
      }
      l4 = paramLong;
      if (localSQLiteCursor2 != null)
      {
        localSQLiteCursor2.dispose();
        l4 = paramLong;
      }
    }
    catch (Exception localException2)
    {
      for (;;)
      {
        localObject1 = paramInteger;
        FileLog.e("tmessages", localException2);
        l4 = l3;
        if (paramInteger != null)
        {
          paramInteger.dispose();
          l4 = l3;
        }
      }
    }
    finally
    {
      if (localObject1 == null) {
        break label375;
      }
      ((SQLiteCursor)localObject1).dispose();
    }
    if (l4 == 0L) {
      return null;
    }
    label375:
    if ((l1 == l2) && (paramInt2 != 0))
    {
      localObject1 = null;
      paramInteger = null;
      try
      {
        SQLitePreparedStatement localSQLitePreparedStatement1 = this.database.executeFast("UPDATE messages SET send_state = 0, date = ? WHERE mid = ?");
        paramInteger = localSQLitePreparedStatement1;
        localObject1 = localSQLitePreparedStatement1;
        localSQLitePreparedStatement1.bindInteger(1, paramInt2);
        paramInteger = localSQLitePreparedStatement1;
        localObject1 = localSQLitePreparedStatement1;
        localSQLitePreparedStatement1.bindLong(2, l2);
        paramInteger = localSQLitePreparedStatement1;
        localObject1 = localSQLitePreparedStatement1;
        localSQLitePreparedStatement1.step();
        if (localSQLitePreparedStatement1 != null) {
          localSQLitePreparedStatement1.dispose();
        }
      }
      catch (Exception localException3)
      {
        for (;;)
        {
          localObject1 = paramInteger;
          FileLog.e("tmessages", localException3);
          if (paramInteger != null) {
            paramInteger.dispose();
          }
        }
      }
      finally
      {
        if (localObject1 == null) {
          break label510;
        }
        ((SQLitePreparedStatement)localObject1).dispose();
      }
      return new long[] { l4, paramInt1 };
    }
    label510:
    paramInteger = null;
    localObject1 = null;
    try
    {
      localSQLitePreparedStatement2 = this.database.executeFast("UPDATE messages SET mid = ?, send_state = 0 WHERE mid = ?");
      localObject1 = localSQLitePreparedStatement2;
      paramInteger = localSQLitePreparedStatement2;
      localSQLitePreparedStatement2.bindLong(1, l2);
      localObject1 = localSQLitePreparedStatement2;
      paramInteger = localSQLitePreparedStatement2;
      localSQLitePreparedStatement2.bindLong(2, l1);
      localObject1 = localSQLitePreparedStatement2;
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
        paramInteger = (Integer)localObject1;
        try
        {
          this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l1) })).stepThis().dispose();
          paramInteger = (Integer)localObject1;
          this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid = %d", new Object[] { Long.valueOf(l1) })).stepThis().dispose();
        }
        catch (Exception localException4)
        {
          for (;;)
          {
            paramInteger = (Integer)localObject1;
            FileLog.e("tmessages", localException4);
          }
        }
        paramInteger = (Integer)localObject1;
        if (localObject1 != null)
        {
          ((SQLitePreparedStatement)localObject1).dispose();
          paramInteger = null;
        }
      }
    }
    finally
    {
      if (paramInteger == null) {
        break label865;
      }
      paramInteger.dispose();
    }
    localObject1 = paramInteger;
    try
    {
      localSQLitePreparedStatement2 = this.database.executeFast("UPDATE media_v2 SET mid = ? WHERE mid = ?");
      localObject1 = localSQLitePreparedStatement2;
      paramInteger = localSQLitePreparedStatement2;
      localSQLitePreparedStatement2.bindLong(1, l2);
      localObject1 = localSQLitePreparedStatement2;
      paramInteger = localSQLitePreparedStatement2;
      localSQLitePreparedStatement2.bindLong(2, l1);
      localObject1 = localSQLitePreparedStatement2;
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
        paramInteger = (Integer)localObject2;
        try
        {
          this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid = %d", new Object[] { Long.valueOf(l1) })).stepThis().dispose();
          paramInteger = (Integer)localObject2;
          if (localObject2 == null) {
            continue;
          }
          ((SQLitePreparedStatement)localObject2).dispose();
          paramInteger = null;
        }
        catch (Exception localException5)
        {
          for (;;)
          {
            paramInteger = (Integer)localObject2;
            FileLog.e("tmessages", localException5);
          }
        }
      }
    }
    finally
    {
      if (paramInteger == null) {
        break label950;
      }
      paramInteger.dispose();
    }
    localObject1 = paramInteger;
    try
    {
      localSQLitePreparedStatement2 = this.database.executeFast("UPDATE dialogs SET last_mid = ? WHERE last_mid = ?");
      localObject1 = localSQLitePreparedStatement2;
      paramInteger = localSQLitePreparedStatement2;
      localSQLitePreparedStatement2.bindLong(1, l2);
      localObject1 = localSQLitePreparedStatement2;
      paramInteger = localSQLitePreparedStatement2;
      localSQLitePreparedStatement2.bindLong(2, l1);
      localObject1 = localSQLitePreparedStatement2;
      paramInteger = localSQLitePreparedStatement2;
      localSQLitePreparedStatement2.step();
    }
    catch (Exception localException6)
    {
      for (;;)
      {
        paramInteger = (Integer)localObject3;
        FileLog.e("tmessages", localException6);
        if (localObject3 != null) {
          ((SQLitePreparedStatement)localObject3).dispose();
        }
      }
    }
    finally
    {
      if (paramInteger == null) {
        break label989;
      }
      paramInteger.dispose();
    }
    return new long[] { l4, ((Integer)localObject6).intValue() };
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
        paramArrayList = paramArrayList.iterator();
        for (;;)
        {
          if (paramArrayList.hasNext())
          {
            localObject2 = (TLRPC.User)paramArrayList.next();
            ((SQLitePreparedStatement)localObject1).requery();
            if (((TLRPC.User)localObject2).status != null)
            {
              ((SQLitePreparedStatement)localObject1).bindInteger(1, ((TLRPC.User)localObject2).status.expires);
              ((SQLitePreparedStatement)localObject1).bindInteger(2, ((TLRPC.User)localObject2).id);
              ((SQLitePreparedStatement)localObject1).step();
              continue;
            }
          }
        }
      }
      catch (Exception paramArrayList)
      {
        FileLog.e("tmessages", paramArrayList);
      }
    }
    do
    {
      do
      {
        do
        {
          ((SQLitePreparedStatement)localObject1).bindInteger(1, 0);
          break;
          ((SQLitePreparedStatement)localObject1).dispose();
        } while (!paramBoolean2);
        this.database.commitTransaction();
        return;
        localObject2 = new StringBuilder();
        localObject1 = new HashMap();
        paramArrayList = paramArrayList.iterator();
        TLRPC.User localUser1;
        while (paramArrayList.hasNext())
        {
          localUser1 = (TLRPC.User)paramArrayList.next();
          if (((StringBuilder)localObject2).length() != 0) {
            ((StringBuilder)localObject2).append(",");
          }
          ((StringBuilder)localObject2).append(localUser1.id);
          ((HashMap)localObject1).put(Integer.valueOf(localUser1.id), localUser1);
        }
        paramArrayList = new ArrayList();
        getUsersInternal(((StringBuilder)localObject2).toString(), paramArrayList);
        localObject2 = paramArrayList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localUser1 = (TLRPC.User)((Iterator)localObject2).next();
          TLRPC.User localUser2 = (TLRPC.User)((HashMap)localObject1).get(Integer.valueOf(localUser1.id));
          if (localUser2 != null) {
            if ((localUser2.first_name != null) && (localUser2.last_name != null))
            {
              if (!UserObject.isContact(localUser1))
              {
                localUser1.first_name = localUser2.first_name;
                localUser1.last_name = localUser2.last_name;
              }
              localUser1.username = localUser2.username;
            }
            else if (localUser2.photo != null)
            {
              localUser1.photo = localUser2.photo;
            }
            else if (localUser2.phone != null)
            {
              localUser1.phone = localUser2.phone;
            }
          }
        }
      } while (paramArrayList.isEmpty());
      if (paramBoolean2) {
        this.database.beginTransaction();
      }
      putUsersInternal(paramArrayList);
    } while (!paramBoolean2);
    this.database.commitTransaction();
  }
  
  public void addRecentLocalFile(final String paramString1, final String paramString2, final TLRPC.Document paramDocument)
  {
    if ((paramString1 == null) || (paramString1.length() == 0) || (((paramString2 == null) || (paramString2.length() == 0)) && (paramDocument == null))) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          if (paramDocument != null)
          {
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
            localSQLitePreparedStatement.requery();
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(paramDocument.getObjectSize());
            paramDocument.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindByteBuffer(1, localNativeByteBuffer);
            localSQLitePreparedStatement.bindString(2, paramString1);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            localNativeByteBuffer.reuse();
            return;
          }
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindString(1, paramString2);
          localSQLitePreparedStatement.bindString(2, paramString1);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void applyPhoneBookUpdates(final String paramString1, final String paramString2)
  {
    if ((paramString1.length() == 0) && (paramString2.length() == 0)) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          if (paramString1.length() != 0) {
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v6 SET deleted = 0 WHERE sphone IN(%s)", new Object[] { paramString1 })).stepThis().dispose();
          }
          if (paramString2.length() != 0) {
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v6 SET deleted = 1 WHERE sphone IN(%s)", new Object[] { paramString2 })).stepThis().dispose();
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public boolean checkMessageId(final long paramLong, int paramInt)
  {
    final boolean[] arrayOfBoolean = new boolean[1];
    final Semaphore localSemaphore = new Semaphore(0);
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        localObject3 = null;
        localObject1 = null;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d AND mid = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(arrayOfBoolean) }), new Object[0]);
          localObject1 = localSQLiteCursor;
          localObject3 = localSQLiteCursor;
          if (localSQLiteCursor.next())
          {
            localObject1 = localSQLiteCursor;
            localObject3 = localSQLiteCursor;
            localSemaphore[0] = true;
          }
          if (localSQLiteCursor != null) {
            localSQLiteCursor.dispose();
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localObject3 = localObject1;
            FileLog.e("tmessages", localException);
            if (localObject1 != null) {
              ((SQLiteCursor)localObject1).dispose();
            }
          }
        }
        finally
        {
          if (localObject3 == null) {
            break label118;
          }
          ((SQLiteCursor)localObject3).dispose();
        }
        this.val$semaphore.release();
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
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
        MessagesStorage.lastDateValue = 0;
        MessagesStorage.lastSeqValue = 0;
        MessagesStorage.lastPtsValue = 0;
        MessagesStorage.lastQtsValue = 0;
        MessagesStorage.lastSecretVersion = 0;
        MessagesStorage.access$202(MessagesStorage.this, 0);
        MessagesStorage.access$302(MessagesStorage.this, 0);
        MessagesStorage.access$402(MessagesStorage.this, 0);
        MessagesStorage.access$502(MessagesStorage.this, 0);
        MessagesStorage.secretPBytes = null;
        MessagesStorage.secretG = 0;
        if (MessagesStorage.this.database != null)
        {
          MessagesStorage.this.database.close();
          MessagesStorage.access$002(MessagesStorage.this, null);
        }
        if (MessagesStorage.this.cacheFile != null)
        {
          MessagesStorage.this.cacheFile.delete();
          MessagesStorage.access$602(MessagesStorage.this, null);
        }
        MessagesStorage.this.openDatabase();
        if (paramBoolean) {
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              MessagesController.getInstance().getDifference();
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
          if (paramInt == 0)
          {
            MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            return;
          }
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", new Object[] { Integer.valueOf(paramInt) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
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
          MessagesStorage.this.database.executeFast("DELETE FROM user_photos WHERE uid = " + paramInt + " AND id = " + paramLong).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
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
          MessagesStorage.this.database.executeFast("DELETE FROM user_photos WHERE uid = " + paramInt).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
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
          MessagesStorage.this.database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + paramInt).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void closeHolesInMedia(long paramLong, int paramInt1, int paramInt2, int paramInt3)
    throws Exception
  {
    Object localObject3;
    Object localObject4;
    int i;
    if (paramInt3 < 0) {
      for (;;)
      {
        try
        {
          localObject3 = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), new Object[0]);
        }
        catch (Exception localException1)
        {
          Object localObject1;
          int j;
          FileLog.e("tmessages", localException1);
        }
        if (!((SQLiteCursor)localObject3).next()) {
          break;
        }
        localObject4 = localObject1;
        if (localObject1 == null) {
          localObject4 = new ArrayList();
        }
        paramInt3 = ((SQLiteCursor)localObject3).intValue(0);
        i = ((SQLiteCursor)localObject3).intValue(1);
        j = ((SQLiteCursor)localObject3).intValue(2);
        if (i == j)
        {
          localObject1 = localObject4;
          if (i == 1) {}
        }
        else
        {
          ((ArrayList)localObject4).add(new Hole(paramInt3, i, j));
          localObject1 = localObject4;
        }
      }
    }
    label856:
    label862:
    label869:
    for (;;)
    {
      return;
      localObject3 = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt3), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), new Object[0]);
      break label856;
      ((SQLiteCursor)localObject3).dispose();
      if (localException1 != null)
      {
        paramInt3 = 0;
        for (;;)
        {
          if (paramInt3 >= localException1.size()) {
            break label869;
          }
          localObject3 = (Hole)localException1.get(paramInt3);
          if ((paramInt2 >= ((Hole)localObject3).end - 1) && (paramInt1 <= ((Hole)localObject3).start + 1))
          {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject3).type), Integer.valueOf(((Hole)localObject3).start), Integer.valueOf(((Hole)localObject3).end) })).stepThis().dispose();
          }
          else if (paramInt2 >= ((Hole)localObject3).end - 1)
          {
            i = ((Hole)localObject3).end;
            if (i != paramInt1) {
              try
              {
                this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt1), Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject3).type), Integer.valueOf(((Hole)localObject3).start), Integer.valueOf(((Hole)localObject3).end) })).stepThis().dispose();
              }
              catch (Exception localException2)
              {
                FileLog.e("tmessages", localException2);
              }
            }
          }
          else if (paramInt1 <= localException2.start + 1)
          {
            i = localException2.start;
            if (i != paramInt2) {
              try
              {
                this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt2), Long.valueOf(paramLong), Integer.valueOf(localException2.type), Integer.valueOf(localException2.start), Integer.valueOf(localException2.end) })).stepThis().dispose();
              }
              catch (Exception localException3)
              {
                FileLog.e("tmessages", localException3);
              }
            }
          }
          else
          {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(localException3.type), Integer.valueOf(localException3.start), Integer.valueOf(localException3.end) })).stepThis().dispose();
            localObject4 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
            ((SQLitePreparedStatement)localObject4).requery();
            ((SQLitePreparedStatement)localObject4).bindLong(1, paramLong);
            ((SQLitePreparedStatement)localObject4).bindInteger(2, localException3.type);
            ((SQLitePreparedStatement)localObject4).bindInteger(3, localException3.start);
            ((SQLitePreparedStatement)localObject4).bindInteger(4, paramInt1);
            ((SQLitePreparedStatement)localObject4).step();
            ((SQLitePreparedStatement)localObject4).requery();
            ((SQLitePreparedStatement)localObject4).bindLong(1, paramLong);
            ((SQLitePreparedStatement)localObject4).bindInteger(2, localException3.type);
            ((SQLitePreparedStatement)localObject4).bindInteger(3, paramInt2);
            ((SQLitePreparedStatement)localObject4).bindInteger(4, localException3.end);
            ((SQLitePreparedStatement)localObject4).step();
            ((SQLitePreparedStatement)localObject4).dispose();
            break label862;
            Object localObject2 = null;
            break;
          }
          paramInt3 += 1;
        }
      }
    }
  }
  
  public void commitTransaction(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            MessagesStorage.this.database.commitTransaction();
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
    try
    {
      this.database.commitTransaction();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public long createPendingTask(NativeByteBuffer paramNativeByteBuffer)
  {
    if (paramNativeByteBuffer == null) {
      return 0L;
    }
    final long l = this.lastTaskId.getAndAdd(1L);
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
          FileLog.e("tmessages", localException);
          return;
        }
        finally
        {
          this.val$data.reuse();
        }
      }
    });
    return l;
  }
  
  public void createTaskForSecretChat(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, final ArrayList<Long> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = Integer.MAX_VALUE;
        SparseArray localSparseArray;
        StringBuilder localStringBuilder;
        int m;
        int k;
        ArrayList localArrayList1;
        label302:
        do
        {
          final ArrayList localArrayList3;
          for (;;)
          {
            try
            {
              localSparseArray = new SparseArray();
              localArrayList3 = new ArrayList();
              localStringBuilder = new StringBuilder();
              if (paramArrayList == null)
              {
                SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE uid = %d AND out = %d AND read_state != 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", new Object[] { Long.valueOf(paramInt1 << 32), Integer.valueOf(paramInt4), Integer.valueOf(paramInt2) }), new Object[0]);
                if (!localSQLiteCursor.next()) {
                  break;
                }
                m = localSQLiteCursor.intValue(1);
                k = localSQLiteCursor.intValue(0);
                if (paramArrayList != null) {
                  localArrayList3.add(Long.valueOf(k));
                }
                if (m <= 0) {
                  continue;
                }
                if (paramInt2 <= paramInt3) {
                  break label302;
                }
                j = paramInt2;
                j += m;
                i = Math.min(i, j);
                ArrayList localArrayList2 = (ArrayList)localSparseArray.get(j);
                localArrayList1 = localArrayList2;
                if (localArrayList2 == null)
                {
                  localArrayList1 = new ArrayList();
                  localSparseArray.put(j, localArrayList1);
                }
                if (localStringBuilder.length() != 0) {
                  localStringBuilder.append(",");
                }
                localStringBuilder.append(k);
                localArrayList1.add(Integer.valueOf(k));
                continue;
              }
              localObject = TextUtils.join(",", paramArrayList);
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
              return;
            }
            localObject = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages as m INNER JOIN randoms as r ON m.mid = r.mid WHERE r.random_id IN (%s)", new Object[] { localObject }), new Object[0]);
            continue;
            j = paramInt3;
          }
          ((SQLiteCursor)localObject).dispose();
          if (paramArrayList != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesStorage.getInstance().markMessagesContentAsRead(localArrayList3);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesReadContent, new Object[] { localArrayList3 });
              }
            });
          }
        } while (localSparseArray.size() == 0);
        MessagesStorage.this.database.beginTransaction();
        Object localObject = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
        int j = 0;
        for (;;)
        {
          if (j < localSparseArray.size())
          {
            m = localSparseArray.keyAt(j);
            localArrayList1 = (ArrayList)localSparseArray.get(m);
            k = 0;
            while (k < localArrayList1.size())
            {
              ((SQLitePreparedStatement)localObject).requery();
              ((SQLitePreparedStatement)localObject).bindInteger(1, ((Integer)localArrayList1.get(k)).intValue());
              ((SQLitePreparedStatement)localObject).bindInteger(2, m);
              ((SQLitePreparedStatement)localObject).step();
              k += 1;
            }
          }
          ((SQLitePreparedStatement)localObject).dispose();
          MessagesStorage.this.database.commitTransaction();
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid IN(%s)", new Object[] { localStringBuilder.toString() })).stepThis().dispose();
          MessagesController.getInstance().didAddedNewTask(i, localSparseArray);
          return;
          j += 1;
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
          MessagesStorage.this.database.executeFast("DELETE FROM blocked_users WHERE uid = " + paramInt).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void deleteContacts(final ArrayList<Integer> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          String str = TextUtils.join(",", paramArrayList);
          MessagesStorage.this.database.executeFast("DELETE FROM contacts WHERE uid IN(" + str + ")").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void deleteDialog(final long paramLong, final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1;
        Object localObject4;
        int j;
        label854:
        Object localObject3;
        label945:
        label952:
        do
        {
          for (;;)
          {
            try
            {
              SQLiteCursor localSQLiteCursor1;
              if (paramInt == 3)
              {
                i = -1;
                localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized("SELECT last_mid FROM dialogs WHERE did = " + paramLong, new Object[0]);
                if (localSQLiteCursor1.next()) {
                  i = localSQLiteCursor1.intValue(0);
                }
                localSQLiteCursor1.dispose();
                if (i != 0) {
                  return;
                }
              }
              if (((int)paramLong == 0) || (paramInt == 2))
              {
                localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + paramLong, new Object[0]);
                localObject1 = new ArrayList();
                try
                {
                  if (localSQLiteCursor1.next())
                  {
                    localObject4 = localSQLiteCursor1.byteBufferValue(0);
                    if (localObject4 == null) {
                      continue;
                    }
                    Object localObject2 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
                    ((NativeByteBuffer)localObject4).reuse();
                    if ((localObject2 == null) || (((TLRPC.Message)localObject2).media == null)) {
                      continue;
                    }
                    if (!(((TLRPC.Message)localObject2).media instanceof TLRPC.TL_messageMediaPhoto)) {
                      break label854;
                    }
                    localObject2 = ((TLRPC.Message)localObject2).media.photo.sizes.iterator();
                    if (!((Iterator)localObject2).hasNext()) {
                      continue;
                    }
                    localObject4 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject2).next());
                    if ((localObject4 == null) || (((File)localObject4).toString().length() <= 0)) {
                      continue;
                    }
                    ((ArrayList)localObject1).add(localObject4);
                    continue;
                  }
                  if (paramInt == 0) {
                    continue;
                  }
                }
                catch (Exception localException2)
                {
                  FileLog.e("tmessages", localException2);
                  localSQLiteCursor1.dispose();
                  FileLoader.getInstance().deleteFiles((ArrayList)localObject1, paramInt);
                }
              }
              if (paramInt != 3) {
                break;
              }
              MessagesStorage.this.database.executeFast("DELETE FROM dialogs WHERE did = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM chat_settings_v2 WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM chat_pinned WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM search_recent WHERE did = " + paramLong).stepThis().dispose();
              i = (int)paramLong;
              j = (int)(paramLong >> 32);
              if (i == 0) {
                break label952;
              }
              if (j != 1) {
                break label945;
              }
              MessagesStorage.this.database.executeFast("DELETE FROM chats WHERE uid = " + i).stepThis().dispose();
              MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM messages WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + paramLong).stepThis().dispose();
              BotQuery.clearBotKeyboard(paramLong, null);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                }
              });
              return;
            }
            catch (Exception localException1)
            {
              FileLog.e("tmessages", localException1);
              return;
            }
            if ((localException2.media instanceof TLRPC.TL_messageMediaDocument))
            {
              localObject4 = FileLoader.getPathToAttach(localException2.media.document);
              if ((localObject4 != null) && (((File)localObject4).toString().length() > 0)) {
                ((ArrayList)localObject1).add(localObject4);
              }
              localObject3 = FileLoader.getPathToAttach(localException2.media.document.thumb);
              if ((localObject3 != null) && (((File)localObject3).toString().length() > 0))
              {
                ((ArrayList)localObject1).add(localObject3);
                continue;
                if (i < 0)
                {
                  continue;
                  MessagesStorage.this.database.executeFast("DELETE FROM enc_chats WHERE uid = " + j).stepThis().dispose();
                }
              }
            }
          }
        } while (paramInt != 2);
        SQLiteCursor localSQLiteCursor2 = MessagesStorage.this.database.queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + paramLong, new Object[0]);
        int i = -1;
        if (localSQLiteCursor2.next())
        {
          long l1 = localSQLiteCursor2.longValue(0);
          long l2 = localSQLiteCursor2.longValue(1);
          localObject1 = MessagesStorage.this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + paramLong + " AND mid IN (" + l1 + "," + l2 + ")", new Object[0]);
          for (;;)
          {
            j = i;
            try
            {
              if (((SQLiteCursor)localObject1).next())
              {
                localObject3 = ((SQLiteCursor)localObject1).byteBufferValue(0);
                i = j;
                if (localObject3 == null) {
                  continue;
                }
                localObject4 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                ((NativeByteBuffer)localObject3).reuse();
                i = j;
                if (localObject4 == null) {
                  continue;
                }
                i = ((TLRPC.Message)localObject4).id;
              }
            }
            catch (Exception localException3)
            {
              FileLog.e("tmessages", localException3);
              ((SQLiteCursor)localObject1).dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM messages WHERE uid = " + paramLong + " AND mid != " + l1 + " AND mid != " + l2).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + paramLong).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + paramLong).stepThis().dispose();
              BotQuery.clearBotKeyboard(paramLong, null);
              localObject1 = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
              SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
              if (j != -1) {
                MessagesStorage.createFirstHoles(paramLong, (SQLitePreparedStatement)localObject1, localSQLitePreparedStatement, j);
              }
              ((SQLitePreparedStatement)localObject1).dispose();
              localSQLitePreparedStatement.dispose();
            }
          }
        }
        localSQLiteCursor2.dispose();
      }
    });
  }
  
  public void deleteUserChannelHistory(final int paramInt1, final int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          long l = -paramInt1;
          final ArrayList localArrayList1 = new ArrayList();
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + l, new Object[0]);
          ArrayList localArrayList2 = new ArrayList();
          for (;;)
          {
            try
            {
              if (localSQLiteCursor.next())
              {
                localObject2 = localSQLiteCursor.byteBufferValue(0);
                if (localObject2 == null) {
                  continue;
                }
                Object localObject1 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                ((NativeByteBuffer)localObject2).reuse();
                if ((localObject1 == null) || (((TLRPC.Message)localObject1).from_id != paramInt2) || (((TLRPC.Message)localObject1).id == 1)) {
                  continue;
                }
                localArrayList1.add(Integer.valueOf(((TLRPC.Message)localObject1).id));
                if (!(((TLRPC.Message)localObject1).media instanceof TLRPC.TL_messageMediaPhoto)) {
                  break label296;
                }
                localObject1 = ((TLRPC.Message)localObject1).media.photo.sizes.iterator();
                if (!((Iterator)localObject1).hasNext()) {
                  continue;
                }
                localObject2 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject1).next());
                if ((localObject2 == null) || (((File)localObject2).toString().length() <= 0)) {
                  continue;
                }
                localArrayList2.add(localObject2);
                continue;
              }
              if (!(localException2.media instanceof TLRPC.TL_messageMediaDocument)) {
                continue;
              }
            }
            catch (Exception localException2)
            {
              FileLog.e("tmessages", localException2);
              localSQLiteCursor.dispose();
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.getInstance().markChannelDialogMessageAsDeleted(localArrayList1, MessagesStorage.20.this.val$channelId);
                }
              });
              MessagesStorage.this.markMessagesAsDeletedInternal(localArrayList1, paramInt1);
              MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(localArrayList1, paramInt1);
              FileLoader.getInstance().deleteFiles(localArrayList2, 0);
              if (!localArrayList1.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, new Object[] { localArrayList1, Integer.valueOf(MessagesStorage.20.this.val$channelId) });
                  }
                });
              }
              return;
            }
            label296:
            Object localObject2 = FileLoader.getPathToAttach(localException2.media.document);
            if ((localObject2 != null) && (((File)localObject2).toString().length() > 0)) {
              localArrayList2.add(localObject2);
            }
            File localFile = FileLoader.getPathToAttach(localException2.media.document.thumb);
            if ((localFile != null) && (localFile.toString().length() > 0)) {
              localArrayList2.add(localFile);
            }
          }
          return;
        }
        catch (Exception localException1)
        {
          FileLog.e("tmessages", localException1);
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
        paramInt1 = 0;
        while (paramInt1 < 5)
        {
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindLong(1, paramLong);
          localSQLitePreparedStatement.bindInteger(2, paramInt1);
          localSQLitePreparedStatement.bindInteger(3, 1);
          localSQLitePreparedStatement.bindInteger(4, 1);
          localSQLitePreparedStatement.step();
          paramInt1 += 1;
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
      return;
      this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) })).stepThis().dispose();
    }
  }
  
  public void getBlockedUsers()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList2;
        StringBuilder localStringBuilder;
        try
        {
          ArrayList localArrayList1 = new ArrayList();
          localArrayList2 = new ArrayList();
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT * FROM blocked_users WHERE 1", new Object[0]);
          localStringBuilder = new StringBuilder();
          while (localSQLiteCursor.next())
          {
            int i = localSQLiteCursor.intValue(0);
            localArrayList1.add(Integer.valueOf(i));
            if (localStringBuilder.length() != 0) {
              localStringBuilder.append(",");
            }
            localStringBuilder.append(i);
          }
          localSQLiteCursor.dispose();
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          return;
        }
        if (localStringBuilder.length() != 0) {
          MessagesStorage.this.getUsersInternal(localStringBuilder.toString(), localArrayList2);
        }
        MessagesController.getInstance().processLoadedBlockedUsers(localException, localArrayList2, true);
      }
    });
  }
  
  public void getCachedPhoneBook()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        HashMap localHashMap = new HashMap();
        SQLiteCursor localSQLiteCursor;
        try
        {
          localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1", new Object[0]);
          while (localSQLiteCursor.next())
          {
            int i = localSQLiteCursor.intValue(0);
            Object localObject2 = (ContactsController.Contact)localHashMap.get(Integer.valueOf(i));
            Object localObject1 = localObject2;
            if (localObject2 == null)
            {
              localObject1 = new ContactsController.Contact();
              ((ContactsController.Contact)localObject1).first_name = localSQLiteCursor.stringValue(1);
              ((ContactsController.Contact)localObject1).last_name = localSQLiteCursor.stringValue(2);
              ((ContactsController.Contact)localObject1).id = i;
              localHashMap.put(Integer.valueOf(i), localObject1);
            }
            String str2 = localSQLiteCursor.stringValue(3);
            if (str2 != null)
            {
              ((ContactsController.Contact)localObject1).phones.add(str2);
              String str1 = localSQLiteCursor.stringValue(4);
              if (str1 != null)
              {
                localObject2 = str1;
                if (str1.length() == 8)
                {
                  localObject2 = str1;
                  if (str2.length() != 8) {
                    localObject2 = PhoneFormat.stripExceptNumbers(str2);
                  }
                }
                ((ContactsController.Contact)localObject1).shortPhones.add(localObject2);
                ((ContactsController.Contact)localObject1).phoneDeleted.add(Integer.valueOf(localSQLiteCursor.intValue(5)));
                ((ContactsController.Contact)localObject1).phoneTypes.add("");
                continue;
                ContactsController.getInstance().performSyncPhoneBook(localHashMap, true, true, false, false);
              }
            }
          }
        }
        catch (Exception localException)
        {
          localHashMap.clear();
          FileLog.e("tmessages", localException);
        }
        for (;;)
        {
          return;
          localSQLiteCursor.dispose();
        }
      }
    });
  }
  
  public int getChannelPtsSync(final int paramInt)
  {
    final Semaphore localSemaphore = new Semaphore(0);
    final Integer[] arrayOfInteger = new Integer[1];
    arrayOfInteger[0] = Integer.valueOf(0);
    getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_2
        //   2: aconst_null
        //   3: astore_1
        //   4: aload_0
        //   5: getfield 23	org/telegram/messenger/MessagesStorage$80:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   8: invokestatic 40	org/telegram/messenger/MessagesStorage:access$000	(Lorg/telegram/messenger/MessagesStorage;)Lorg/telegram/SQLite/SQLiteDatabase;
        //   11: new 42	java/lang/StringBuilder
        //   14: dup
        //   15: invokespecial 43	java/lang/StringBuilder:<init>	()V
        //   18: ldc 45
        //   20: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   23: aload_0
        //   24: getfield 25	org/telegram/messenger/MessagesStorage$80:val$channelId	I
        //   27: ineg
        //   28: invokevirtual 52	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   31: invokevirtual 56	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   34: iconst_0
        //   35: anewarray 4	java/lang/Object
        //   38: invokevirtual 62	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
        //   41: astore_3
        //   42: aload_3
        //   43: astore_1
        //   44: aload_3
        //   45: astore_2
        //   46: aload_3
        //   47: invokevirtual 68	org/telegram/SQLite/SQLiteCursor:next	()Z
        //   50: ifeq +21 -> 71
        //   53: aload_3
        //   54: astore_1
        //   55: aload_3
        //   56: astore_2
        //   57: aload_0
        //   58: getfield 27	org/telegram/messenger/MessagesStorage$80:val$pts	[Ljava/lang/Integer;
        //   61: iconst_0
        //   62: aload_3
        //   63: iconst_0
        //   64: invokevirtual 71	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   67: invokestatic 77	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   70: aastore
        //   71: aload_3
        //   72: ifnull +7 -> 79
        //   75: aload_3
        //   76: invokevirtual 80	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   79: aload_0
        //   80: getfield 29	org/telegram/messenger/MessagesStorage$80:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   83: ifnull +10 -> 93
        //   86: aload_0
        //   87: getfield 29	org/telegram/messenger/MessagesStorage$80:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   90: invokevirtual 85	java/util/concurrent/Semaphore:release	()V
        //   93: return
        //   94: astore_3
        //   95: aload_1
        //   96: astore_2
        //   97: ldc 87
        //   99: aload_3
        //   100: invokestatic 93	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   103: aload_1
        //   104: ifnull -25 -> 79
        //   107: aload_1
        //   108: invokevirtual 80	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   111: goto -32 -> 79
        //   114: astore_1
        //   115: aload_2
        //   116: ifnull +7 -> 123
        //   119: aload_2
        //   120: invokevirtual 80	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   123: aload_1
        //   124: athrow
        //   125: astore_1
        //   126: ldc 87
        //   128: aload_1
        //   129: invokestatic 93	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   132: return
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	133	0	this	80
        //   3	105	1	localObject1	Object
        //   114	10	1	localObject2	Object
        //   125	4	1	localException1	Exception
        //   1	119	2	localObject3	Object
        //   41	35	3	localSQLiteCursor	SQLiteCursor
        //   94	6	3	localException2	Exception
        // Exception table:
        //   from	to	target	type
        //   4	42	94	java/lang/Exception
        //   46	53	94	java/lang/Exception
        //   57	71	94	java/lang/Exception
        //   4	42	114	finally
        //   46	53	114	finally
        //   57	71	114	finally
        //   97	103	114	finally
        //   79	93	125	java/lang/Exception
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfInteger[0].intValue();
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public TLRPC.Chat getChat(int paramInt)
  {
    TLRPC.Chat localChat = null;
    try
    {
      ArrayList localArrayList = new ArrayList();
      getChatsInternal("" + paramInt, localArrayList);
      if (!localArrayList.isEmpty()) {
        localChat = (TLRPC.Chat)localArrayList.get(0);
      }
      return localChat;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return null;
  }
  
  public TLRPC.Chat getChatSync(final int paramInt)
  {
    final Semaphore localSemaphore = new Semaphore(0);
    final TLRPC.Chat[] arrayOfChat = new TLRPC.Chat[1];
    getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        arrayOfChat[0] = MessagesStorage.this.getChat(paramInt);
        localSemaphore.release();
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfChat[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void getChatsInternal(String paramString, ArrayList<TLRPC.Chat> paramArrayList)
    throws Exception
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayList == null)) {
      return;
    }
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
        FileLog.e("tmessages", localException);
      }
    }
    paramString.dispose();
  }
  
  public void getContacts()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        StringBuilder localStringBuilder;
        boolean bool;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
          localStringBuilder = new StringBuilder();
          for (;;)
          {
            if (localSQLiteCursor.next())
            {
              int i = localSQLiteCursor.intValue(0);
              TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
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
                ContactsController.getInstance().processLoadedContacts(localArrayList1, localArrayList2, 1);
              }
            }
          }
        }
        catch (Exception localException)
        {
          localArrayList1.clear();
          localArrayList2.clear();
          FileLog.e("tmessages", localException);
        }
        for (;;)
        {
          return;
          bool = false;
          break;
          localException.dispose();
          if (localStringBuilder.length() != 0) {
            MessagesStorage.this.getUsersInternal(localStringBuilder.toString(), localArrayList2);
          }
        }
      }
    });
  }
  
  public SQLiteDatabase getDatabase()
  {
    return this.database;
  }
  
  public void getDialogPhotos(int paramInt1, final int paramInt2, final int paramInt3, final long paramLong, final int paramInt4)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final TLRPC.photos_Photos localphotos_Photos;
        SQLiteCursor localSQLiteCursor2;
        for (;;)
        {
          try
          {
            if (paramLong != 0L)
            {
              SQLiteCursor localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY id DESC LIMIT %d", new Object[] { Integer.valueOf(paramInt3), Long.valueOf(paramLong), Integer.valueOf(paramInt2) }), new Object[0]);
              localphotos_Photos = new TLRPC.photos_Photos();
              if (!localSQLiteCursor1.next()) {
                break;
              }
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor1.byteBufferValue(0);
              if (localNativeByteBuffer == null) {
                continue;
              }
              TLRPC.Photo localPhoto = TLRPC.Photo.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              localphotos_Photos.photos.add(localPhoto);
              continue;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          tmp144_141[0] = Integer.valueOf(paramInt3);
          Object[] tmp154_144 = tmp144_141;
          tmp154_144[1] = Integer.valueOf(paramInt4);
          Object[] tmp164_154 = tmp154_144;
          tmp164_154[2] = Integer.valueOf(paramInt2);
          localSQLiteCursor2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY id DESC LIMIT %d,%d", tmp164_154), new Object[0]);
        }
        localSQLiteCursor2.dispose();
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesController.getInstance().processLoadedUserPhotos(localphotos_Photos, MessagesStorage.22.this.val$did, MessagesStorage.22.this.val$offset, MessagesStorage.22.this.val$count, MessagesStorage.22.this.val$max_id, true, MessagesStorage.22.this.val$classGuid);
          }
        });
      }
    });
  }
  
  public int getDialogReadMax(final boolean paramBoolean, final long paramLong)
  {
    final Semaphore localSemaphore = new Semaphore(0);
    Integer[] arrayOfInteger = new Integer[1];
    arrayOfInteger[0] = Integer.valueOf(0);
    getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject3 = null;
        SQLiteCursor localSQLiteCursor1 = null;
        localSQLiteCursor2 = localSQLiteCursor1;
        localObject2 = localObject3;
        for (;;)
        {
          try
          {
            if (!paramBoolean) {
              continue;
            }
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject2 = localObject3;
            localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized("SELECT outbox_max FROM dialogs WHERE did = " + paramLong, new Object[0]);
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject2 = localSQLiteCursor1;
            if (localSQLiteCursor1.next())
            {
              localSQLiteCursor2 = localSQLiteCursor1;
              localObject2 = localSQLiteCursor1;
              localSemaphore[0] = Integer.valueOf(localSQLiteCursor1.intValue(0));
            }
            if (localSQLiteCursor1 != null) {
              localSQLiteCursor1.dispose();
            }
          }
          catch (Exception localException)
          {
            localObject2 = localSQLiteCursor2;
            FileLog.e("tmessages", localException);
            if (localSQLiteCursor2 == null) {
              continue;
            }
            localSQLiteCursor2.dispose();
            continue;
          }
          finally
          {
            if (localObject2 == null) {
              continue;
            }
            ((SQLiteCursor)localObject2).dispose();
          }
          this.val$semaphore.release();
          return;
          localSQLiteCursor2 = localSQLiteCursor1;
          localObject2 = localObject3;
          localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + paramLong, new Object[0]);
        }
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfInteger[0].intValue();
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
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
        //   0: new 33	org/telegram/tgnet/TLRPC$messages_Dialogs
        //   3: dup
        //   4: invokespecial 34	org/telegram/tgnet/TLRPC$messages_Dialogs:<init>	()V
        //   7: astore 7
        //   9: new 36	java/util/ArrayList
        //   12: dup
        //   13: invokespecial 37	java/util/ArrayList:<init>	()V
        //   16: astore 8
        //   18: new 36	java/util/ArrayList
        //   21: dup
        //   22: invokespecial 37	java/util/ArrayList:<init>	()V
        //   25: astore 9
        //   27: aload 9
        //   29: invokestatic 43	org/telegram/messenger/UserConfig:getClientUserId	()I
        //   32: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   35: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   38: pop
        //   39: new 36	java/util/ArrayList
        //   42: dup
        //   43: invokespecial 37	java/util/ArrayList:<init>	()V
        //   46: astore 10
        //   48: new 36	java/util/ArrayList
        //   51: dup
        //   52: invokespecial 37	java/util/ArrayList:<init>	()V
        //   55: astore 11
        //   57: new 36	java/util/ArrayList
        //   60: dup
        //   61: invokespecial 37	java/util/ArrayList:<init>	()V
        //   64: astore 13
        //   66: new 55	java/util/HashMap
        //   69: dup
        //   70: invokespecial 56	java/util/HashMap:<init>	()V
        //   73: astore 12
        //   75: aload_0
        //   76: getfield 20	org/telegram/messenger/MessagesStorage$77:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   79: invokestatic 60	org/telegram/messenger/MessagesStorage:access$000	(Lorg/telegram/messenger/MessagesStorage;)Lorg/telegram/SQLite/SQLiteDatabase;
        //   82: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   85: ldc 68
        //   87: iconst_2
        //   88: anewarray 4	java/lang/Object
        //   91: dup
        //   92: iconst_0
        //   93: aload_0
        //   94: getfield 22	org/telegram/messenger/MessagesStorage$77:val$offset	I
        //   97: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   100: aastore
        //   101: dup
        //   102: iconst_1
        //   103: aload_0
        //   104: getfield 24	org/telegram/messenger/MessagesStorage$77:val$count	I
        //   107: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   110: aastore
        //   111: invokestatic 74	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   114: iconst_0
        //   115: anewarray 4	java/lang/Object
        //   118: invokevirtual 80	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
        //   121: astore 14
        //   123: aload 14
        //   125: invokevirtual 86	org/telegram/SQLite/SQLiteCursor:next	()Z
        //   128: ifeq +696 -> 824
        //   131: new 88	org/telegram/tgnet/TLRPC$TL_dialog
        //   134: dup
        //   135: invokespecial 89	org/telegram/tgnet/TLRPC$TL_dialog:<init>	()V
        //   138: astore 15
        //   140: aload 15
        //   142: aload 14
        //   144: iconst_0
        //   145: invokevirtual 93	org/telegram/SQLite/SQLiteCursor:longValue	(I)J
        //   148: putfield 97	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   151: aload 15
        //   153: aload 14
        //   155: iconst_1
        //   156: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   159: putfield 104	org/telegram/tgnet/TLRPC$TL_dialog:top_message	I
        //   162: aload 15
        //   164: aload 14
        //   166: iconst_2
        //   167: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   170: putfield 107	org/telegram/tgnet/TLRPC$TL_dialog:unread_count	I
        //   173: aload 15
        //   175: aload 14
        //   177: iconst_3
        //   178: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   181: putfield 110	org/telegram/tgnet/TLRPC$TL_dialog:last_message_date	I
        //   184: aload 15
        //   186: aload 14
        //   188: bipush 10
        //   190: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   193: putfield 113	org/telegram/tgnet/TLRPC$TL_dialog:pts	I
        //   196: aload 15
        //   198: getfield 113	org/telegram/tgnet/TLRPC$TL_dialog:pts	I
        //   201: ifeq +910 -> 1111
        //   204: aload 15
        //   206: getfield 97	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   209: l2i
        //   210: ifle +516 -> 726
        //   213: goto +898 -> 1111
        //   216: aload 15
        //   218: iload_1
        //   219: putfield 116	org/telegram/tgnet/TLRPC$TL_dialog:flags	I
        //   222: aload 15
        //   224: aload 14
        //   226: bipush 11
        //   228: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   231: putfield 119	org/telegram/tgnet/TLRPC$TL_dialog:read_inbox_max_id	I
        //   234: aload 15
        //   236: aload 14
        //   238: bipush 12
        //   240: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   243: putfield 122	org/telegram/tgnet/TLRPC$TL_dialog:read_outbox_max_id	I
        //   246: aload 14
        //   248: bipush 8
        //   250: invokevirtual 93	org/telegram/SQLite/SQLiteCursor:longValue	(I)J
        //   253: lstore_3
        //   254: lload_3
        //   255: l2i
        //   256: istore_1
        //   257: aload 15
        //   259: new 124	org/telegram/tgnet/TLRPC$TL_peerNotifySettings
        //   262: dup
        //   263: invokespecial 125	org/telegram/tgnet/TLRPC$TL_peerNotifySettings:<init>	()V
        //   266: putfield 129	org/telegram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/telegram/tgnet/TLRPC$PeerNotifySettings;
        //   269: iload_1
        //   270: iconst_1
        //   271: iand
        //   272: ifeq +37 -> 309
        //   275: aload 15
        //   277: getfield 129	org/telegram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/telegram/tgnet/TLRPC$PeerNotifySettings;
        //   280: lload_3
        //   281: bipush 32
        //   283: lshr
        //   284: l2i
        //   285: putfield 134	org/telegram/tgnet/TLRPC$PeerNotifySettings:mute_until	I
        //   288: aload 15
        //   290: getfield 129	org/telegram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/telegram/tgnet/TLRPC$PeerNotifySettings;
        //   293: getfield 134	org/telegram/tgnet/TLRPC$PeerNotifySettings:mute_until	I
        //   296: ifne +13 -> 309
        //   299: aload 15
        //   301: getfield 129	org/telegram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/telegram/tgnet/TLRPC$PeerNotifySettings;
        //   304: ldc -121
        //   306: putfield 134	org/telegram/tgnet/TLRPC$PeerNotifySettings:mute_until	I
        //   309: aload 7
        //   311: getfield 139	org/telegram/tgnet/TLRPC$messages_Dialogs:dialogs	Ljava/util/ArrayList;
        //   314: aload 15
        //   316: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   319: pop
        //   320: aload 14
        //   322: iconst_4
        //   323: invokevirtual 143	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
        //   326: astore 17
        //   328: aload 17
        //   330: ifnull +290 -> 620
        //   333: aload 17
        //   335: aload 17
        //   337: iconst_0
        //   338: invokevirtual 149	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   341: iconst_0
        //   342: invokestatic 155	org/telegram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$Message;
        //   345: astore 16
        //   347: aload 17
        //   349: invokevirtual 158	org/telegram/tgnet/NativeByteBuffer:reuse	()V
        //   352: aload 16
        //   354: ifnull +266 -> 620
        //   357: aload 16
        //   359: aload 14
        //   361: iconst_5
        //   362: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   365: invokestatic 164	org/telegram/messenger/MessageObject:setUnreadFlags	(Lorg/telegram/tgnet/TLRPC$Message;I)V
        //   368: aload 16
        //   370: aload 14
        //   372: bipush 6
        //   374: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   377: putfield 166	org/telegram/tgnet/TLRPC$Message:id	I
        //   380: aload 14
        //   382: bipush 9
        //   384: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   387: istore_1
        //   388: iload_1
        //   389: ifeq +9 -> 398
        //   392: aload 15
        //   394: iload_1
        //   395: putfield 110	org/telegram/tgnet/TLRPC$TL_dialog:last_message_date	I
        //   398: aload 16
        //   400: aload 14
        //   402: bipush 7
        //   404: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   407: putfield 169	org/telegram/tgnet/TLRPC$Message:send_state	I
        //   410: aload 16
        //   412: aload 15
        //   414: getfield 97	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   417: putfield 172	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   420: aload 7
        //   422: getfield 175	org/telegram/tgnet/TLRPC$messages_Dialogs:messages	Ljava/util/ArrayList;
        //   425: aload 16
        //   427: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   430: pop
        //   431: aload 16
        //   433: aload 9
        //   435: aload 10
        //   437: invokestatic 179	org/telegram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/telegram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   440: aload 16
        //   442: getfield 182	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   445: ifeq +175 -> 620
        //   448: aload 16
        //   450: getfield 186	org/telegram/tgnet/TLRPC$Message:action	Lorg/telegram/tgnet/TLRPC$MessageAction;
        //   453: instanceof 188
        //   456: ifne +14 -> 470
        //   459: aload 16
        //   461: getfield 186	org/telegram/tgnet/TLRPC$Message:action	Lorg/telegram/tgnet/TLRPC$MessageAction;
        //   464: instanceof 190
        //   467: ifeq +153 -> 620
        //   470: aload 14
        //   472: bipush 13
        //   474: invokevirtual 194	org/telegram/SQLite/SQLiteCursor:isNull	(I)Z
        //   477: ifne +59 -> 536
        //   480: aload 14
        //   482: bipush 13
        //   484: invokevirtual 143	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
        //   487: astore 17
        //   489: aload 17
        //   491: ifnull +45 -> 536
        //   494: aload 16
        //   496: aload 17
        //   498: aload 17
        //   500: iconst_0
        //   501: invokevirtual 149	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   504: iconst_0
        //   505: invokestatic 155	org/telegram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$Message;
        //   508: putfield 198	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   511: aload 17
        //   513: invokevirtual 158	org/telegram/tgnet/NativeByteBuffer:reuse	()V
        //   516: aload 16
        //   518: getfield 198	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   521: ifnull +15 -> 536
        //   524: aload 16
        //   526: getfield 198	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   529: aload 9
        //   531: aload 10
        //   533: invokestatic 179	org/telegram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/telegram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   536: aload 16
        //   538: getfield 198	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   541: ifnonnull +79 -> 620
        //   544: aload 16
        //   546: getfield 182	org/telegram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   549: i2l
        //   550: lstore 5
        //   552: lload 5
        //   554: lstore_3
        //   555: aload 16
        //   557: getfield 202	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
        //   560: getfield 207	org/telegram/tgnet/TLRPC$Peer:channel_id	I
        //   563: ifeq +19 -> 582
        //   566: lload 5
        //   568: aload 16
        //   570: getfield 202	org/telegram/tgnet/TLRPC$Message:to_id	Lorg/telegram/tgnet/TLRPC$Peer;
        //   573: getfield 207	org/telegram/tgnet/TLRPC$Peer:channel_id	I
        //   576: i2l
        //   577: bipush 32
        //   579: lshl
        //   580: lor
        //   581: lstore_3
        //   582: aload 13
        //   584: lload_3
        //   585: invokestatic 212	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   588: invokevirtual 215	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   591: ifne +13 -> 604
        //   594: aload 13
        //   596: lload_3
        //   597: invokestatic 212	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   600: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   603: pop
        //   604: aload 12
        //   606: aload 15
        //   608: getfield 97	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   611: invokestatic 212	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   614: aload 16
        //   616: invokevirtual 219	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   619: pop
        //   620: aload 15
        //   622: getfield 97	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   625: l2i
        //   626: istore_1
        //   627: aload 15
        //   629: getfield 97	org/telegram/tgnet/TLRPC$TL_dialog:id	J
        //   632: bipush 32
        //   634: lshr
        //   635: l2i
        //   636: istore_2
        //   637: iload_1
        //   638: ifeq +161 -> 799
        //   641: iload_2
        //   642: iconst_1
        //   643: if_icmpne +100 -> 743
        //   646: aload 10
        //   648: iload_1
        //   649: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   652: invokevirtual 215	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   655: ifne -532 -> 123
        //   658: aload 10
        //   660: iload_1
        //   661: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   664: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   667: pop
        //   668: goto -545 -> 123
        //   671: astore 9
        //   673: aload 7
        //   675: getfield 139	org/telegram/tgnet/TLRPC$messages_Dialogs:dialogs	Ljava/util/ArrayList;
        //   678: invokevirtual 222	java/util/ArrayList:clear	()V
        //   681: aload 7
        //   683: getfield 225	org/telegram/tgnet/TLRPC$messages_Dialogs:users	Ljava/util/ArrayList;
        //   686: invokevirtual 222	java/util/ArrayList:clear	()V
        //   689: aload 7
        //   691: getfield 228	org/telegram/tgnet/TLRPC$messages_Dialogs:chats	Ljava/util/ArrayList;
        //   694: invokevirtual 222	java/util/ArrayList:clear	()V
        //   697: aload 8
        //   699: invokevirtual 222	java/util/ArrayList:clear	()V
        //   702: ldc -26
        //   704: aload 9
        //   706: invokestatic 236	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   709: invokestatic 242	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
        //   712: aload 7
        //   714: aload 8
        //   716: iconst_0
        //   717: bipush 100
        //   719: iconst_1
        //   720: iconst_1
        //   721: iconst_0
        //   722: invokevirtual 246	org/telegram/messenger/MessagesController:processLoadedDialogs	(Lorg/telegram/tgnet/TLRPC$messages_Dialogs;Ljava/util/ArrayList;IIIZZ)V
        //   725: return
        //   726: iconst_1
        //   727: istore_1
        //   728: goto -512 -> 216
        //   731: astore 16
        //   733: ldc -26
        //   735: aload 16
        //   737: invokestatic 236	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   740: goto -120 -> 620
        //   743: iload_1
        //   744: ifle +28 -> 772
        //   747: aload 9
        //   749: iload_1
        //   750: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   753: invokevirtual 215	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   756: ifne -633 -> 123
        //   759: aload 9
        //   761: iload_1
        //   762: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   765: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   768: pop
        //   769: goto -646 -> 123
        //   772: aload 10
        //   774: iload_1
        //   775: ineg
        //   776: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   779: invokevirtual 215	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   782: ifne -659 -> 123
        //   785: aload 10
        //   787: iload_1
        //   788: ineg
        //   789: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   792: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   795: pop
        //   796: goto -673 -> 123
        //   799: aload 11
        //   801: iload_2
        //   802: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   805: invokevirtual 215	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   808: ifne -685 -> 123
        //   811: aload 11
        //   813: iload_2
        //   814: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   817: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   820: pop
        //   821: goto -698 -> 123
        //   824: aload 14
        //   826: invokevirtual 249	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   829: aload 13
        //   831: invokevirtual 252	java/util/ArrayList:isEmpty	()Z
        //   834: ifne +172 -> 1006
        //   837: aload_0
        //   838: getfield 20	org/telegram/messenger/MessagesStorage$77:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   841: invokestatic 60	org/telegram/messenger/MessagesStorage:access$000	(Lorg/telegram/messenger/MessagesStorage;)Lorg/telegram/SQLite/SQLiteDatabase;
        //   844: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   847: ldc -2
        //   849: iconst_1
        //   850: anewarray 4	java/lang/Object
        //   853: dup
        //   854: iconst_0
        //   855: ldc_w 256
        //   858: aload 13
        //   860: invokestatic 262	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   863: aastore
        //   864: invokestatic 74	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   867: iconst_0
        //   868: anewarray 4	java/lang/Object
        //   871: invokevirtual 80	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
        //   874: astore 13
        //   876: aload 13
        //   878: invokevirtual 86	org/telegram/SQLite/SQLiteCursor:next	()Z
        //   881: ifeq +120 -> 1001
        //   884: aload 13
        //   886: iconst_0
        //   887: invokevirtual 143	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
        //   890: astore 15
        //   892: aload 15
        //   894: ifnull -18 -> 876
        //   897: aload 15
        //   899: aload 15
        //   901: iconst_0
        //   902: invokevirtual 149	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   905: iconst_0
        //   906: invokestatic 155	org/telegram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$Message;
        //   909: astore 14
        //   911: aload 15
        //   913: invokevirtual 158	org/telegram/tgnet/NativeByteBuffer:reuse	()V
        //   916: aload 14
        //   918: aload 13
        //   920: iconst_1
        //   921: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   924: putfield 166	org/telegram/tgnet/TLRPC$Message:id	I
        //   927: aload 14
        //   929: aload 13
        //   931: iconst_2
        //   932: invokevirtual 101	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
        //   935: putfield 265	org/telegram/tgnet/TLRPC$Message:date	I
        //   938: aload 14
        //   940: aload 13
        //   942: iconst_3
        //   943: invokevirtual 93	org/telegram/SQLite/SQLiteCursor:longValue	(I)J
        //   946: putfield 172	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   949: aload 14
        //   951: aload 9
        //   953: aload 10
        //   955: invokestatic 179	org/telegram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/telegram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   958: aload 12
        //   960: aload 14
        //   962: getfield 172	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   965: invokestatic 212	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   968: invokevirtual 269	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   971: checkcast 151	org/telegram/tgnet/TLRPC$Message
        //   974: astore 15
        //   976: aload 15
        //   978: ifnull -102 -> 876
        //   981: aload 15
        //   983: aload 14
        //   985: putfield 198	org/telegram/tgnet/TLRPC$Message:replyMessage	Lorg/telegram/tgnet/TLRPC$Message;
        //   988: aload 14
        //   990: aload 15
        //   992: getfield 172	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   995: putfield 172	org/telegram/tgnet/TLRPC$Message:dialog_id	J
        //   998: goto -122 -> 876
        //   1001: aload 13
        //   1003: invokevirtual 249	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   1006: aload 11
        //   1008: invokevirtual 252	java/util/ArrayList:isEmpty	()Z
        //   1011: ifne +22 -> 1033
        //   1014: aload_0
        //   1015: getfield 20	org/telegram/messenger/MessagesStorage$77:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   1018: ldc_w 256
        //   1021: aload 11
        //   1023: invokestatic 262	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1026: aload 8
        //   1028: aload 9
        //   1030: invokevirtual 273	org/telegram/messenger/MessagesStorage:getEncryptedChatsInternal	(Ljava/lang/String;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   1033: aload 10
        //   1035: invokevirtual 252	java/util/ArrayList:isEmpty	()Z
        //   1038: ifne +23 -> 1061
        //   1041: aload_0
        //   1042: getfield 20	org/telegram/messenger/MessagesStorage$77:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   1045: ldc_w 256
        //   1048: aload 10
        //   1050: invokestatic 262	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1053: aload 7
        //   1055: getfield 228	org/telegram/tgnet/TLRPC$messages_Dialogs:chats	Ljava/util/ArrayList;
        //   1058: invokevirtual 277	org/telegram/messenger/MessagesStorage:getChatsInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   1061: aload 9
        //   1063: invokevirtual 252	java/util/ArrayList:isEmpty	()Z
        //   1066: ifne +23 -> 1089
        //   1069: aload_0
        //   1070: getfield 20	org/telegram/messenger/MessagesStorage$77:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   1073: ldc_w 256
        //   1076: aload 9
        //   1078: invokestatic 262	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1081: aload 7
        //   1083: getfield 225	org/telegram/tgnet/TLRPC$messages_Dialogs:users	Ljava/util/ArrayList;
        //   1086: invokevirtual 280	org/telegram/messenger/MessagesStorage:getUsersInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   1089: invokestatic 242	org/telegram/messenger/MessagesController:getInstance	()Lorg/telegram/messenger/MessagesController;
        //   1092: aload 7
        //   1094: aload 8
        //   1096: aload_0
        //   1097: getfield 22	org/telegram/messenger/MessagesStorage$77:val$offset	I
        //   1100: aload_0
        //   1101: getfield 24	org/telegram/messenger/MessagesStorage$77:val$count	I
        //   1104: iconst_1
        //   1105: iconst_0
        //   1106: iconst_0
        //   1107: invokevirtual 246	org/telegram/messenger/MessagesController:processLoadedDialogs	(Lorg/telegram/tgnet/TLRPC$messages_Dialogs;Ljava/util/ArrayList;IIIZZ)V
        //   1110: return
        //   1111: iconst_0
        //   1112: istore_1
        //   1113: goto -897 -> 216
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	1116	0	this	77
        //   218	895	1	i	int
        //   636	178	2	j	int
        //   253	344	3	l1	long
        //   550	17	5	l2	long
        //   7	1086	7	localmessages_Dialogs	TLRPC.messages_Dialogs
        //   16	1079	8	localArrayList1	ArrayList
        //   25	505	9	localArrayList2	ArrayList
        //   671	406	9	localException1	Exception
        //   46	1003	10	localArrayList3	ArrayList
        //   55	967	11	localArrayList4	ArrayList
        //   73	886	12	localHashMap	HashMap
        //   64	938	13	localObject1	Object
        //   121	868	14	localObject2	Object
        //   138	853	15	localObject3	Object
        //   345	270	16	localMessage	TLRPC.Message
        //   731	5	16	localException2	Exception
        //   326	186	17	localNativeByteBuffer	NativeByteBuffer
        // Exception table:
        //   from	to	target	type
        //   18	123	671	java/lang/Exception
        //   123	213	671	java/lang/Exception
        //   216	254	671	java/lang/Exception
        //   257	269	671	java/lang/Exception
        //   275	309	671	java/lang/Exception
        //   309	328	671	java/lang/Exception
        //   333	352	671	java/lang/Exception
        //   357	388	671	java/lang/Exception
        //   392	398	671	java/lang/Exception
        //   398	440	671	java/lang/Exception
        //   620	637	671	java/lang/Exception
        //   646	668	671	java/lang/Exception
        //   733	740	671	java/lang/Exception
        //   747	769	671	java/lang/Exception
        //   772	796	671	java/lang/Exception
        //   799	821	671	java/lang/Exception
        //   824	876	671	java/lang/Exception
        //   876	892	671	java/lang/Exception
        //   897	976	671	java/lang/Exception
        //   981	998	671	java/lang/Exception
        //   1001	1006	671	java/lang/Exception
        //   1006	1033	671	java/lang/Exception
        //   1033	1061	671	java/lang/Exception
        //   1061	1089	671	java/lang/Exception
        //   1089	1110	671	java/lang/Exception
        //   440	470	731	java/lang/Exception
        //   470	489	731	java/lang/Exception
        //   494	536	731	java/lang/Exception
        //   536	552	731	java/lang/Exception
        //   555	582	731	java/lang/Exception
        //   582	604	731	java/lang/Exception
        //   604	620	731	java/lang/Exception
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
        for (;;)
        {
          DownloadObject localDownloadObject;
          TLRPC.MessageMedia localMessageMedia;
          try
          {
            ArrayList localArrayList = new ArrayList();
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", new Object[] { Integer.valueOf(paramInt) }), new Object[0]);
            if (!localSQLiteCursor.next()) {
              break;
            }
            localDownloadObject = new DownloadObject();
            localDownloadObject.type = localSQLiteCursor.intValue(1);
            localDownloadObject.id = localSQLiteCursor.longValue(0);
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(2);
            if (localNativeByteBuffer != null)
            {
              localMessageMedia = TLRPC.MessageMedia.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              if (localMessageMedia.document != null) {
                localDownloadObject.object = localMessageMedia.document;
              }
            }
            else
            {
              localArrayList.add(localDownloadObject);
              continue;
            }
            if (localMessageMedia.photo == null) {
              continue;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          localDownloadObject.object = FileLoader.getClosestPhotoSizeWithSize(localMessageMedia.photo.sizes, AndroidUtilities.getPhotoSize());
        }
        localSQLiteCursor.dispose();
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MediaController.getInstance().processDownloadObjects(MessagesStorage.61.this.val$type, localException);
          }
        });
      }
    });
  }
  
  public TLRPC.EncryptedChat getEncryptedChat(int paramInt)
  {
    TLRPC.EncryptedChat localEncryptedChat = null;
    try
    {
      ArrayList localArrayList = new ArrayList();
      getEncryptedChatsInternal("" + paramInt, localArrayList, null);
      if (!localArrayList.isEmpty()) {
        localEncryptedChat = (TLRPC.EncryptedChat)localArrayList.get(0);
      }
      return localEncryptedChat;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return null;
  }
  
  public void getEncryptedChat(final int paramInt, final Semaphore paramSemaphore, final ArrayList<TLObject> paramArrayList)
  {
    if ((paramSemaphore == null) || (paramArrayList == null)) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          ArrayList localArrayList1 = new ArrayList();
          ArrayList localArrayList2 = new ArrayList();
          MessagesStorage.this.getEncryptedChatsInternal("" + paramInt, localArrayList2, localArrayList1);
          if ((!localArrayList2.isEmpty()) && (!localArrayList1.isEmpty()))
          {
            ArrayList localArrayList3 = new ArrayList();
            MessagesStorage.this.getUsersInternal(TextUtils.join(",", localArrayList1), localArrayList3);
            if (!localArrayList3.isEmpty())
            {
              paramArrayList.add(localArrayList2.get(0));
              paramArrayList.add(localArrayList3.get(0));
            }
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          return;
        }
        finally
        {
          paramSemaphore.release();
        }
      }
    });
  }
  
  public void getEncryptedChatsInternal(String paramString, ArrayList<TLRPC.EncryptedChat> paramArrayList, ArrayList<Integer> paramArrayList1)
    throws Exception
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayList == null)) {
      return;
    }
    paramString = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash FROM enc_chats WHERE uid IN(%s)", new Object[] { paramString }), new Object[0]);
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
            localEncryptedChat.key_use_count_in = ((short)(i >> 16));
            localEncryptedChat.key_use_count_out = ((short)i);
            localEncryptedChat.exchange_id = paramString.longValue(9);
            localEncryptedChat.key_create_date = paramString.intValue(10);
            localEncryptedChat.future_key_fingerprint = paramString.longValue(11);
            localEncryptedChat.future_auth_key = paramString.byteArrayValue(12);
            localEncryptedChat.key_hash = paramString.byteArrayValue(13);
            paramArrayList.add(localEncryptedChat);
          }
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
    paramString.dispose();
  }
  
  public void getMessages(final long paramLong, final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4, int paramInt5, final boolean paramBoolean, final int paramInt6)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        TLRPC.TL_messages_messages localTL_messages_messages = new TLRPC.TL_messages_messages();
        int i32 = 0;
        int i22 = 0;
        int i23 = 0;
        int i15 = 0;
        int i9 = 0;
        int i37 = 0;
        int i31 = 0;
        int i29 = 0;
        int i16 = paramInt1;
        int i14 = 0;
        int i21 = 0;
        int i33 = 0;
        int i26 = 0;
        int i27 = 0;
        int i34 = 0;
        int i35 = 0;
        int i6 = 0;
        int i40 = 0;
        int i30 = 0;
        int i20 = 0;
        int i8 = 0;
        int i4 = 0;
        int i39 = 0;
        int i19 = 0;
        boolean bool6 = false;
        boolean bool7 = false;
        boolean bool8 = false;
        boolean bool13 = false;
        boolean bool5 = false;
        boolean bool12 = false;
        int i36 = 0;
        int i24 = 0;
        int i25 = 0;
        int i12 = 0;
        int i7 = 0;
        int i5 = 0;
        int i38 = 0;
        int i28 = 0;
        long l2 = paramInt2;
        int i13 = paramInt2;
        int i17 = 0;
        if (paramBoolean) {
          i17 = -(int)paramLong;
        }
        long l1 = l2;
        if (l2 != 0L)
        {
          l1 = l2;
          if (i17 != 0) {
            l1 = l2 | i17 << 32;
          }
        }
        boolean bool10 = false;
        boolean bool11 = false;
        boolean bool9 = false;
        int i18;
        if (paramLong == 777000L)
        {
          i18 = 4;
          i10 = i16;
          i = i26;
          n = i19;
          k = i22;
          i1 = i24;
          bool1 = bool10;
          bool2 = bool7;
          i11 = i16;
          j = i27;
          i2 = i20;
          m = i23;
          i3 = i25;
          bool3 = bool11;
          bool4 = bool8;
        }
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        Object localObject6;
        HashMap localHashMap1;
        HashMap localHashMap2;
        label2494:
        label2962:
        long l3;
        long l4;
        label3829:
        label4008:
        TLRPC.Message localMessage;
        Object localObject5;
        label6071:
        label7957:
        label8399:
        Object localObject4;
        for (;;)
        {
          try
          {
            localArrayList1 = new ArrayList();
            i10 = i16;
            i = i26;
            n = i19;
            k = i22;
            i1 = i24;
            bool1 = bool10;
            bool2 = bool7;
            i11 = i16;
            j = i27;
            i2 = i20;
            m = i23;
            i3 = i25;
            bool3 = bool11;
            bool4 = bool8;
            localArrayList2 = new ArrayList();
            i10 = i16;
            i = i26;
            n = i19;
            k = i22;
            i1 = i24;
            bool1 = bool10;
            bool2 = bool7;
            i11 = i16;
            j = i27;
            i2 = i20;
            m = i23;
            i3 = i25;
            bool3 = bool11;
            bool4 = bool8;
            localObject6 = new ArrayList();
            i10 = i16;
            i = i26;
            n = i19;
            k = i22;
            i1 = i24;
            bool1 = bool10;
            bool2 = bool7;
            i11 = i16;
            j = i27;
            i2 = i20;
            m = i23;
            i3 = i25;
            bool3 = bool11;
            bool4 = bool8;
            localHashMap1 = new HashMap();
            i10 = i16;
            i = i26;
            n = i19;
            k = i22;
            i1 = i24;
            bool1 = bool10;
            bool2 = bool7;
            i11 = i16;
            j = i27;
            i2 = i20;
            m = i23;
            i3 = i25;
            bool3 = bool11;
            bool4 = bool8;
            localHashMap2 = new HashMap();
            i10 = i16;
            i = i26;
            n = i19;
            k = i22;
            i1 = i24;
            bool1 = bool10;
            bool2 = bool7;
            i11 = i16;
            j = i27;
            i2 = i20;
            m = i23;
            i3 = i25;
            bool3 = bool11;
            bool4 = bool8;
            int i41 = (int)paramLong;
            if (i41 == 0) {
              break label11652;
            }
            i4 = i16;
            i6 = i33;
            i5 = i32;
            i8 = i36;
            bool5 = bool6;
            i14 = i13;
            l2 = l1;
            i15 = i21;
            i10 = i16;
            i = i26;
            n = i19;
            k = i22;
            i1 = i24;
            bool1 = bool10;
            bool2 = bool7;
            i11 = i16;
            j = i27;
            i2 = i20;
            m = i23;
            i3 = i25;
            bool3 = bool11;
            bool4 = bool8;
            if (paramInt3 != 1)
            {
              i4 = i16;
              i6 = i33;
              i5 = i32;
              i8 = i36;
              bool5 = bool6;
              i14 = i13;
              l2 = l1;
              i15 = i21;
              i10 = i16;
              i = i26;
              n = i19;
              k = i22;
              i1 = i24;
              bool1 = bool10;
              bool2 = bool7;
              i11 = i16;
              j = i27;
              i2 = i20;
              m = i23;
              i3 = i25;
              bool3 = bool11;
              bool4 = bool8;
              if (paramInt3 != 3)
              {
                i4 = i16;
                i6 = i33;
                i5 = i32;
                i8 = i36;
                bool5 = bool6;
                i14 = i13;
                l2 = l1;
                i15 = i21;
                i10 = i16;
                i = i26;
                n = i19;
                k = i22;
                i1 = i24;
                bool1 = bool10;
                bool2 = bool7;
                i11 = i16;
                j = i27;
                i2 = i20;
                m = i23;
                i3 = i25;
                bool3 = bool11;
                bool4 = bool8;
                if (paramInt4 == 0)
                {
                  i10 = i16;
                  i = i26;
                  n = i19;
                  k = i22;
                  i1 = i24;
                  bool1 = bool10;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i20;
                  m = i23;
                  i3 = i25;
                  bool3 = bool11;
                  bool4 = bool8;
                  i5 = i35;
                  bool6 = bool13;
                  i12 = i13;
                  l2 = l1;
                  if (paramInt3 != 2) {
                    break label16985;
                  }
                  i10 = i16;
                  i = i26;
                  n = i19;
                  k = i22;
                  i1 = i24;
                  bool1 = bool10;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i20;
                  m = i23;
                  i3 = i25;
                  bool3 = bool11;
                  bool4 = bool8;
                  localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized("SELECT inbox_max, unread_count, date FROM dialogs WHERE did = " + paramLong, new Object[0]);
                  i4 = i30;
                  i6 = i29;
                  i8 = i28;
                  bool5 = bool12;
                  i10 = i16;
                  i = i26;
                  n = i19;
                  k = i22;
                  i1 = i24;
                  bool1 = bool10;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i20;
                  m = i23;
                  i3 = i25;
                  bool3 = bool11;
                  bool4 = bool8;
                  if (localSQLiteCursor1.next())
                  {
                    i10 = i16;
                    i = i26;
                    n = i19;
                    k = i22;
                    i1 = i24;
                    bool1 = bool10;
                    bool2 = bool7;
                    i11 = i16;
                    j = i27;
                    i2 = i20;
                    m = i23;
                    i3 = i25;
                    bool3 = bool11;
                    bool4 = bool8;
                    i5 = localSQLiteCursor1.intValue(0);
                    i9 = i5;
                    l2 = i5;
                    i10 = i16;
                    i = i5;
                    n = i19;
                    k = i22;
                    i1 = i24;
                    bool1 = bool10;
                    bool2 = bool7;
                    i11 = i16;
                    j = i5;
                    i2 = i20;
                    m = i23;
                    i3 = i25;
                    bool3 = bool11;
                    bool4 = bool8;
                    i7 = localSQLiteCursor1.intValue(1);
                    i10 = i16;
                    i = i5;
                    n = i19;
                    k = i7;
                    i1 = i24;
                    bool1 = bool10;
                    bool2 = bool7;
                    i11 = i16;
                    j = i5;
                    i2 = i20;
                    m = i7;
                    i3 = i25;
                    bool3 = bool11;
                    bool4 = bool8;
                    i12 = localSQLiteCursor1.intValue(2);
                    bool1 = true;
                    i4 = i5;
                    i6 = i7;
                    i8 = i12;
                    bool5 = bool1;
                    i13 = i9;
                    l1 = l2;
                    if (l2 != 0L)
                    {
                      i4 = i5;
                      i6 = i7;
                      i8 = i12;
                      bool5 = bool1;
                      i13 = i9;
                      l1 = l2;
                      if (i17 != 0)
                      {
                        l1 = l2 | i17 << 32;
                        i13 = i9;
                        bool5 = bool1;
                        i8 = i12;
                        i6 = i7;
                        i4 = i5;
                      }
                    }
                  }
                  i10 = i16;
                  i = i4;
                  n = i19;
                  k = i6;
                  i1 = i8;
                  bool1 = bool10;
                  bool2 = bool5;
                  i11 = i16;
                  j = i4;
                  i2 = i20;
                  m = i6;
                  i3 = i8;
                  bool3 = bool11;
                  bool4 = bool5;
                  localSQLiteCursor1.dispose();
                  if (bool5) {
                    continue;
                  }
                  i10 = i16;
                  i = i4;
                  n = i19;
                  k = i6;
                  i1 = i8;
                  bool1 = bool10;
                  bool2 = bool5;
                  i11 = i16;
                  j = i4;
                  i2 = i20;
                  m = i6;
                  i3 = i8;
                  bool3 = bool11;
                  bool4 = bool5;
                  localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                  i15 = i4;
                  i14 = i8;
                  i10 = i16;
                  i = i4;
                  n = i19;
                  k = i6;
                  i1 = i8;
                  bool1 = bool10;
                  bool2 = bool5;
                  i11 = i16;
                  j = i4;
                  i2 = i20;
                  m = i6;
                  i3 = i8;
                  bool3 = bool11;
                  bool4 = bool5;
                  if (localSQLiteCursor1.next())
                  {
                    i10 = i16;
                    i = i4;
                    n = i19;
                    k = i6;
                    i1 = i8;
                    bool1 = bool10;
                    bool2 = bool5;
                    i11 = i16;
                    j = i4;
                    i2 = i20;
                    m = i6;
                    i3 = i8;
                    bool3 = bool11;
                    bool4 = bool5;
                    i15 = localSQLiteCursor1.intValue(0);
                    i10 = i16;
                    i = i15;
                    n = i19;
                    k = i6;
                    i1 = i8;
                    bool1 = bool10;
                    bool2 = bool5;
                    i11 = i16;
                    j = i15;
                    i2 = i20;
                    m = i6;
                    i3 = i8;
                    bool3 = bool11;
                    bool4 = bool5;
                    i14 = localSQLiteCursor1.intValue(1);
                  }
                  i10 = i16;
                  i = i15;
                  n = i19;
                  k = i6;
                  i1 = i14;
                  bool1 = bool10;
                  bool2 = bool5;
                  i11 = i16;
                  j = i15;
                  i2 = i20;
                  m = i6;
                  i3 = i14;
                  bool3 = bool11;
                  bool4 = bool5;
                  localSQLiteCursor1.dispose();
                  i5 = i15;
                  i9 = i6;
                  i7 = i14;
                  bool6 = bool5;
                  i12 = i13;
                  l2 = l1;
                  if (i15 == 0) {
                    break label16985;
                  }
                  i10 = i16;
                  i = i15;
                  n = i19;
                  k = i6;
                  i1 = i14;
                  bool1 = bool10;
                  bool2 = bool5;
                  i11 = i16;
                  j = i15;
                  i2 = i20;
                  m = i6;
                  i3 = i14;
                  bool3 = bool11;
                  bool4 = bool5;
                  localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid >= %d AND out = 0 AND read_state IN(0,2)", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i15) }), new Object[0]);
                  i9 = i6;
                  i10 = i16;
                  i = i15;
                  n = i19;
                  k = i6;
                  i1 = i14;
                  bool1 = bool10;
                  bool2 = bool5;
                  i11 = i16;
                  j = i15;
                  i2 = i20;
                  m = i6;
                  i3 = i14;
                  bool3 = bool11;
                  bool4 = bool5;
                  if (localSQLiteCursor1.next())
                  {
                    i10 = i16;
                    i = i15;
                    n = i19;
                    k = i6;
                    i1 = i14;
                    bool1 = bool10;
                    bool2 = bool5;
                    i11 = i16;
                    j = i15;
                    i2 = i20;
                    m = i6;
                    i3 = i14;
                    bool3 = bool11;
                    bool4 = bool5;
                    i9 = localSQLiteCursor1.intValue(0);
                  }
                  i10 = i16;
                  i = i15;
                  n = i19;
                  k = i9;
                  i1 = i14;
                  bool1 = bool10;
                  bool2 = bool5;
                  i11 = i16;
                  j = i15;
                  i2 = i20;
                  m = i9;
                  i3 = i14;
                  bool3 = bool11;
                  bool4 = bool5;
                  localSQLiteCursor1.dispose();
                  i5 = i15;
                  i7 = i14;
                  bool6 = bool5;
                  i12 = i13;
                  l2 = l1;
                  break label16985;
                  i10 = i16;
                  i = i5;
                  n = i19;
                  k = i9;
                  i1 = i7;
                  bool1 = bool10;
                  bool2 = bool6;
                  i11 = i16;
                  j = i5;
                  i2 = i20;
                  m = i9;
                  i3 = i7;
                  bool3 = bool11;
                  bool4 = bool6;
                  i13 = Math.max(i16, i9 + 10);
                  i4 = i13;
                  i6 = i5;
                  i5 = i9;
                  i8 = i7;
                  bool5 = bool6;
                  i14 = i12;
                  i15 = i21;
                  if (i9 < i18)
                  {
                    i5 = 0;
                    i6 = 0;
                    l2 = 0L;
                    bool5 = false;
                    i15 = i21;
                    i14 = i12;
                    i8 = i7;
                    i4 = i13;
                  }
                }
              }
            }
            i7 = 0;
            i12 = 0;
            i16 = 0;
            i9 = 0;
            i13 = 0;
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool10;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool11;
            bool4 = bool5;
            SQLiteCursor localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start IN (0, 1)", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool10;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool11;
            bool4 = bool5;
            if (!localSQLiteCursor1.next()) {
              break label8399;
            }
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool10;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool11;
            bool4 = bool5;
            if (localSQLiteCursor1.intValue(0) != 1) {
              break label17089;
            }
            bool6 = true;
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localSQLiteCursor1.dispose();
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            if (paramInt3 != 3)
            {
              if (!bool5) {
                break label9330;
              }
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              if (paramInt3 != 2) {
                break label9330;
              }
            }
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
            i9 = i13;
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            if (localSQLiteCursor1.next())
            {
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              i9 = localSQLiteCursor1.intValue(0);
            }
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localSQLiteCursor1.dispose();
            if (i14 == 0) {
              break label17095;
            }
            i7 = 1;
            i = i7;
            if (i7 != 0)
            {
              i10 = i4;
              i = i6;
              n = i9;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i9;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i14), Integer.valueOf(i14) }), new Object[0]);
              i10 = i4;
              i = i6;
              n = i9;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i9;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              if (localSQLiteCursor1.next()) {
                i7 = 0;
              }
              i10 = i4;
              i = i6;
              n = i9;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i9;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              localSQLiteCursor1.dispose();
              i = i7;
            }
            if (i == 0) {
              break label17101;
            }
            l1 = 0L;
            l3 = 1L;
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start FROM messages_holes WHERE uid = %d AND start >= %d ORDER BY start ASC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i14) }), new Object[0]);
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            if (localSQLiteCursor1.next())
            {
              i10 = i4;
              i = i6;
              n = i9;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i9;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              l4 = localSQLiteCursor1.intValue(0);
              l1 = l4;
              if (i17 != 0) {
                l1 = l4 | i17 << 32;
              }
            }
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localSQLiteCursor1.dispose();
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i14) }), new Object[0]);
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            if (localSQLiteCursor1.next())
            {
              i10 = i4;
              i = i6;
              n = i9;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i9;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              l4 = localSQLiteCursor1.intValue(0);
              l3 = l4;
              if (i17 != 0) {
                l3 = l4 | i17 << 32;
              }
            }
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localSQLiteCursor1.dispose();
            if (l1 != 0L) {
              break label17002;
            }
            if (l3 == 1L) {
              break label9168;
            }
            break label17002;
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d AND m.mid >= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", new Object[] { Long.valueOf(paramLong), Long.valueOf(l2), Long.valueOf(l3), Integer.valueOf(i4 / 2), Long.valueOf(paramLong), Long.valueOf(l2), Long.valueOf(l4), Integer.valueOf(i4 / 2) }), new Object[0]);
            i7 = i4;
            i4 = i9;
            i9 = i5;
            i5 = i8;
            i13 = i14;
            break label17038;
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if (!localSQLiteCursor1.next()) {
              break label13869;
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            Object localObject3 = localSQLiteCursor1.byteBufferValue(1);
            if (localObject3 == null) {
              continue;
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            localMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            ((NativeByteBuffer)localObject3).reuse();
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            MessageObject.setUnreadFlags(localMessage, localSQLiteCursor1.intValue(0));
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            localMessage.id = localSQLiteCursor1.intValue(3);
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            localMessage.date = localSQLiteCursor1.intValue(4);
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            localMessage.dialog_id = paramLong;
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if ((localMessage.flags & 0x400) != 0)
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              localMessage.views = localSQLiteCursor1.intValue(7);
            }
            if (i41 != 0)
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              localMessage.ttl = localSQLiteCursor1.intValue(8);
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            localTL_messages_messages.messages.add(localMessage);
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            MessagesStorage.addUsersAndChatsFromMessage(localMessage, localArrayList1, localArrayList2);
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if (localMessage.reply_to_msg_id == 0)
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (localMessage.reply_to_random_id == 0L) {}
            }
            else
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (!localSQLiteCursor1.isNull(6))
              {
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localObject3 = localSQLiteCursor1.byteBufferValue(6);
                if (localObject3 != null)
                {
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  localMessage.replyMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  ((NativeByteBuffer)localObject3).reuse();
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  if (localMessage.replyMessage != null)
                  {
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    MessagesStorage.addUsersAndChatsFromMessage(localMessage.replyMessage, localArrayList1, localArrayList2);
                  }
                }
              }
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (localMessage.replyMessage == null)
              {
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                if (localMessage.reply_to_msg_id == 0) {
                  break label13458;
                }
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                l2 = localMessage.reply_to_msg_id;
                l1 = l2;
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                if (localMessage.to_id.channel_id != 0)
                {
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  l1 = l2 | localMessage.to_id.channel_id << 32;
                }
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                if (!((ArrayList)localObject6).contains(Long.valueOf(l1)))
                {
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  ((ArrayList)localObject6).add(Long.valueOf(l1));
                }
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localObject5 = (ArrayList)localHashMap1.get(Integer.valueOf(localMessage.reply_to_msg_id));
                localObject3 = localObject5;
                if (localObject5 == null)
                {
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  localObject3 = new ArrayList();
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  localHashMap1.put(Integer.valueOf(localMessage.reply_to_msg_id), localObject3);
                }
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                ((ArrayList)localObject3).add(localMessage);
              }
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            localMessage.send_state = localSQLiteCursor1.intValue(2);
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if (localMessage.id > 0)
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (localMessage.send_state != 0)
              {
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localMessage.send_state = 0;
              }
            }
            if (i41 == 0)
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (!localSQLiteCursor1.isNull(5))
              {
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localMessage.random_id = localSQLiteCursor1.longValue(5);
              }
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if ((int)paramLong != 0) {
              continue;
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if (localMessage.media == null) {
              continue;
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            localObject3 = localMessage.media.photo;
            if (localObject3 == null) {
              continue;
            }
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            try
            {
              localObject3 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT date FROM enc_tasks_v2 WHERE mid = %d", new Object[] { Integer.valueOf(localMessage.id) }), new Object[0]);
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (((SQLiteCursor)localObject3).next())
              {
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localMessage.destroyTime = ((SQLiteCursor)localObject3).intValue(0);
              }
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              ((SQLiteCursor)localObject3).dispose();
            }
            catch (Exception localException2)
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              FileLog.e("tmessages", localException2);
            }
            continue;
          }
          catch (Exception localException1)
          {
            i11 = i10;
            j = i;
            i2 = n;
            m = k;
            i3 = i1;
            bool3 = bool1;
            bool4 = bool2;
            localTL_messages_messages.messages.clear();
            i11 = i10;
            j = i;
            i2 = n;
            m = k;
            i3 = i1;
            bool3 = bool1;
            bool4 = bool2;
            localTL_messages_messages.chats.clear();
            i11 = i10;
            j = i;
            i2 = n;
            m = k;
            i3 = i1;
            bool3 = bool1;
            bool4 = bool2;
            localTL_messages_messages.users.clear();
            i11 = i10;
            j = i;
            i2 = n;
            m = k;
            i3 = i1;
            bool3 = bool1;
            bool4 = bool2;
            FileLog.e("tmessages", localException1);
            return;
            i18 = 1;
            break;
            if (i13 == 0)
            {
              i14 = 0;
              i10 = i16;
              i = i4;
              n = i19;
              k = i6;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i16;
              j = i4;
              i2 = i20;
              m = i6;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              SQLiteCursor localSQLiteCursor2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid > 0 AND out = 0 AND read_state IN(0,2)", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
              i10 = i16;
              i = i4;
              n = i19;
              k = i6;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i16;
              j = i4;
              i2 = i20;
              m = i6;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              if (localSQLiteCursor2.next())
              {
                i10 = i16;
                i = i4;
                n = i19;
                k = i6;
                i1 = i8;
                bool1 = bool10;
                bool2 = bool5;
                i11 = i16;
                j = i4;
                i2 = i20;
                m = i6;
                i3 = i8;
                bool3 = bool11;
                bool4 = bool5;
                i14 = localSQLiteCursor2.intValue(0);
              }
              i10 = i16;
              i = i4;
              n = i19;
              k = i6;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i16;
              j = i4;
              i2 = i20;
              m = i6;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              localSQLiteCursor2.dispose();
              i5 = i4;
              i9 = i6;
              i7 = i8;
              bool6 = bool5;
              i12 = i13;
              l2 = l1;
              if (i14 != i6) {
                break label16985;
              }
              i10 = i16;
              i = i4;
              n = i19;
              k = i6;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i16;
              j = i4;
              i2 = i20;
              m = i6;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              localSQLiteCursor2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
              i10 = i16;
              i = i4;
              n = i19;
              k = i6;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i5 = i4;
              i12 = i13;
              i11 = i16;
              j = i4;
              i2 = i20;
              m = i6;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              if (localSQLiteCursor2.next())
              {
                i10 = i16;
                i = i4;
                n = i19;
                k = i6;
                i1 = i8;
                bool1 = bool10;
                bool2 = bool5;
                i11 = i16;
                j = i4;
                i2 = i20;
                m = i6;
                i3 = i8;
                bool3 = bool11;
                bool4 = bool5;
                i4 = localSQLiteCursor2.intValue(0);
                i = i4;
                l2 = i4;
                i5 = i4;
                i12 = i;
                l1 = l2;
                if (l2 != 0L)
                {
                  i5 = i4;
                  i12 = i;
                  l1 = l2;
                  if (i17 != 0)
                  {
                    l1 = l2 | i17 << 32;
                    i12 = i;
                    i5 = i4;
                  }
                }
              }
              i10 = i16;
              i = i5;
              n = i19;
              k = i6;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i16;
              j = i5;
              i2 = i20;
              m = i6;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              localSQLiteCursor2.dispose();
              i9 = i6;
              i7 = i8;
              bool6 = bool5;
              l2 = l1;
            }
          }
          finally
          {
            MessagesController.getInstance().processLoadedMessages(localTL_messages_messages, paramLong, i11, paramInt2, true, paramInt6, j, i2, m, i3, paramInt3, paramBoolean, bool3, this.val$loadIndex, bool4);
          }
          i10 = i16;
          i = i4;
          n = i19;
          k = i6;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i16;
          j = i4;
          i2 = i20;
          m = i6;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM messages_holes WHERE uid = %d AND start < %d AND end > %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i13), Integer.valueOf(i13) }), new Object[0]);
          i10 = i16;
          i = i4;
          n = i19;
          k = i6;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i16;
          j = i4;
          i2 = i20;
          m = i6;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          if (((SQLiteCursor)localObject2).next()) {
            break label17046;
          }
          i14 = 1;
          i10 = i16;
          i = i4;
          n = i19;
          k = i6;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i16;
          j = i4;
          i2 = i20;
          m = i6;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          ((SQLiteCursor)localObject2).dispose();
          i5 = i4;
          i9 = i6;
          i7 = i8;
          bool6 = bool5;
          i12 = i13;
          l2 = l1;
          if (i14 == 0) {
            break label16985;
          }
          i10 = i16;
          i = i4;
          n = i19;
          k = i6;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i16;
          j = i4;
          i2 = i20;
          m = i6;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid > %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i13) }), new Object[0]);
          i10 = i16;
          i = i4;
          n = i19;
          k = i6;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i16;
          j = i4;
          i2 = i20;
          m = i6;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          i12 = i13;
          if (((SQLiteCursor)localObject2).next())
          {
            i10 = i16;
            i = i4;
            n = i19;
            k = i6;
            i1 = i8;
            bool1 = bool10;
            bool2 = bool5;
            i11 = i16;
            j = i4;
            i2 = i20;
            m = i6;
            i3 = i8;
            bool3 = bool11;
            bool4 = bool5;
            i5 = ((SQLiteCursor)localObject2).intValue(0);
            l2 = i5;
            i12 = i5;
            l1 = l2;
            if (l2 != 0L)
            {
              i12 = i5;
              l1 = l2;
              if (i17 != 0)
              {
                l1 = l2 | i17 << 32;
                i12 = i5;
              }
            }
          }
          i10 = i16;
          i = i4;
          n = i19;
          k = i6;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i16;
          j = i4;
          i2 = i20;
          m = i6;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          ((SQLiteCursor)localObject2).dispose();
          i5 = i4;
          i9 = i6;
          i7 = i8;
          bool6 = bool5;
          l2 = l1;
          break label16985;
          i10 = i4;
          i = i6;
          n = i7;
          k = i5;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i4;
          j = i6;
          i2 = i12;
          m = i5;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          ((SQLiteCursor)localObject2).dispose();
          i10 = i4;
          i = i6;
          n = i7;
          k = i5;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i4;
          j = i6;
          i2 = i12;
          m = i5;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
          i10 = i4;
          i = i6;
          n = i7;
          k = i5;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i4;
          j = i6;
          i2 = i12;
          m = i5;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          if (((SQLiteCursor)localObject2).next())
          {
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool10;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool11;
            bool4 = bool5;
            i18 = ((SQLiteCursor)localObject2).intValue(0);
            if (i18 != 0)
            {
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              localObject4 = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              ((SQLitePreparedStatement)localObject4).requery();
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              ((SQLitePreparedStatement)localObject4).bindLong(1, paramLong);
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              ((SQLitePreparedStatement)localObject4).bindInteger(2, 0);
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              ((SQLitePreparedStatement)localObject4).bindInteger(3, i18);
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              ((SQLitePreparedStatement)localObject4).step();
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool10;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool11;
              bool4 = bool5;
              ((SQLitePreparedStatement)localObject4).dispose();
            }
          }
          i10 = i4;
          i = i6;
          n = i7;
          k = i5;
          i1 = i8;
          bool1 = bool10;
          bool2 = bool5;
          i11 = i4;
          j = i6;
          i2 = i12;
          m = i5;
          i3 = i8;
          bool3 = bool11;
          bool4 = bool5;
          ((SQLiteCursor)localObject2).dispose();
          bool6 = bool9;
        }
        label9168:
        int i10 = i4;
        int i = i6;
        int n = i9;
        int k = i5;
        int i1 = i8;
        boolean bool1 = bool6;
        boolean bool2 = bool5;
        int i11 = i4;
        int j = i6;
        int i2 = i9;
        int m = i5;
        int i3 = i8;
        boolean bool3 = bool6;
        boolean bool4 = bool5;
        Object localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d) UNION SELECT * FROM (SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d)", new Object[] { Long.valueOf(paramLong), Long.valueOf(l2), Integer.valueOf(i4 / 2), Long.valueOf(paramLong), Long.valueOf(l2), Integer.valueOf(i4 / 2) }), new Object[0]);
        i7 = i4;
        i4 = i9;
        i9 = i5;
        i5 = i8;
        i13 = i14;
        break label17038;
        label9330:
        i10 = i4;
        i = i6;
        n = i7;
        k = i5;
        i1 = i8;
        bool1 = bool6;
        bool2 = bool5;
        i11 = i4;
        j = i6;
        i2 = i12;
        m = i5;
        i3 = i8;
        bool3 = bool6;
        bool4 = bool5;
        if (paramInt3 == 1)
        {
          l1 = 0L;
          i10 = i4;
          i = i6;
          n = i7;
          k = i5;
          i1 = i8;
          bool1 = bool6;
          bool2 = bool5;
          i11 = i4;
          j = i6;
          i2 = i12;
          m = i5;
          i3 = i8;
          bool3 = bool6;
          bool4 = bool5;
          localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM messages_holes WHERE uid = %d AND start >= %d AND start != 1 AND end != 1 ORDER BY start ASC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) }), new Object[0]);
          i10 = i4;
          i = i6;
          n = i7;
          k = i5;
          i1 = i8;
          bool1 = bool6;
          bool2 = bool5;
          i11 = i4;
          j = i6;
          i2 = i12;
          m = i5;
          i3 = i8;
          bool3 = bool6;
          bool4 = bool5;
          if (((SQLiteCursor)localObject2).next())
          {
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            l3 = ((SQLiteCursor)localObject2).intValue(0);
            l1 = l3;
            if (i17 != 0) {
              l1 = l3 | i17 << 32;
            }
          }
          i10 = i4;
          i = i6;
          n = i7;
          k = i5;
          i1 = i8;
          bool1 = bool6;
          bool2 = bool5;
          i11 = i4;
          j = i6;
          i2 = i12;
          m = i5;
          i3 = i8;
          bool3 = bool6;
          bool4 = bool5;
          ((SQLiteCursor)localObject2).dispose();
          if (l1 != 0L)
          {
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d AND m.mid <= %d ORDER BY m.date ASC, m.mid ASC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4), Long.valueOf(l2), Long.valueOf(l1), Integer.valueOf(i4) }), new Object[0]);
            i7 = i4;
            i4 = i9;
            i9 = i5;
            i5 = i8;
            i13 = i14;
          }
          else
          {
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date >= %d AND m.mid > %d ORDER BY m.date ASC, m.mid ASC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4), Long.valueOf(l2), Integer.valueOf(i4) }), new Object[0]);
            i7 = i4;
            i4 = i9;
            i9 = i5;
            i5 = i8;
            i13 = i14;
          }
        }
        else
        {
          i10 = i4;
          i = i6;
          n = i7;
          k = i5;
          i1 = i8;
          bool1 = bool6;
          bool2 = bool5;
          i11 = i4;
          j = i6;
          i2 = i12;
          m = i5;
          i3 = i8;
          bool3 = bool6;
          bool4 = bool5;
          if (paramInt4 != 0)
          {
            if (l2 != 0L)
            {
              l1 = 0L;
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT end FROM messages_holes WHERE uid = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) }), new Object[0]);
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              if (((SQLiteCursor)localObject2).next())
              {
                i10 = i4;
                i = i6;
                n = i7;
                k = i5;
                i1 = i8;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i4;
                j = i6;
                i2 = i12;
                m = i5;
                i3 = i8;
                bool3 = bool6;
                bool4 = bool5;
                l3 = ((SQLiteCursor)localObject2).intValue(0);
                l1 = l3;
                if (i17 != 0) {
                  l1 = l3 | i17 << 32;
                }
              }
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              ((SQLiteCursor)localObject2).dispose();
              if (l1 != 0L)
              {
                i10 = i4;
                i = i6;
                n = i7;
                k = i5;
                i1 = i8;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i4;
                j = i6;
                i2 = i12;
                m = i5;
                i3 = i8;
                bool3 = bool6;
                bool4 = bool5;
                localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4), Long.valueOf(l2), Long.valueOf(l1), Integer.valueOf(i4) }), new Object[0]);
                i7 = i4;
                i4 = i9;
                i9 = i5;
                i5 = i8;
                i13 = i14;
              }
              else
              {
                i10 = i4;
                i = i6;
                n = i7;
                k = i5;
                i1 = i8;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i4;
                j = i6;
                i2 = i12;
                m = i5;
                i3 = i8;
                bool3 = bool6;
                bool4 = bool5;
                localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d AND m.mid < %d ORDER BY m.date DESC, m.mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4), Long.valueOf(l2), Integer.valueOf(i4) }), new Object[0]);
                i7 = i4;
                i4 = i9;
                i9 = i5;
                i5 = i8;
                i13 = i14;
              }
            }
            else
            {
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4), Integer.valueOf(i15), Integer.valueOf(i4) }), new Object[0]);
              i7 = i4;
              i4 = i9;
              i9 = i5;
              i5 = i8;
              i13 = i14;
            }
          }
          else
          {
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid) FROM messages WHERE uid = %d AND mid > 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
            i10 = i4;
            i = i6;
            n = i7;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i12;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            i9 = i16;
            if (((SQLiteCursor)localObject2).next())
            {
              i10 = i4;
              i = i6;
              n = i7;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i12;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              i9 = ((SQLiteCursor)localObject2).intValue(0);
            }
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            ((SQLiteCursor)localObject2).dispose();
            l1 = 0L;
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM messages_holes WHERE uid = %d", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            if (((SQLiteCursor)localObject2).next())
            {
              i10 = i4;
              i = i6;
              n = i9;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i9;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              l2 = ((SQLiteCursor)localObject2).intValue(0);
              l1 = l2;
              if (i17 != 0) {
                l1 = l2 | i17 << 32;
              }
            }
            i10 = i4;
            i = i6;
            n = i9;
            k = i5;
            i1 = i8;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i4;
            j = i6;
            i2 = i9;
            m = i5;
            i3 = i8;
            bool3 = bool6;
            bool4 = bool5;
            ((SQLiteCursor)localObject2).dispose();
            if (l1 != 0L)
            {
              i10 = i4;
              i = i6;
              n = i9;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i9;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND (m.mid >= %d OR m.mid < 0) ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Long.valueOf(l1), Integer.valueOf(i15), Integer.valueOf(i4) }), new Object[0]);
              i7 = i4;
              i4 = i9;
              i9 = i5;
              i5 = i8;
              i13 = i14;
            }
            else
            {
              i10 = i4;
              i = i6;
              n = i9;
              k = i5;
              i1 = i8;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i4;
              j = i6;
              i2 = i9;
              m = i5;
              i3 = i8;
              bool3 = bool6;
              bool4 = bool5;
              localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.date DESC, m.mid DESC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i15), Integer.valueOf(i4) }), new Object[0]);
              i7 = i4;
              i4 = i9;
              i9 = i5;
              i5 = i8;
              i13 = i14;
              break label17038;
              label11652:
              bool9 = true;
              bool10 = true;
              bool6 = true;
              i10 = i16;
              i = i26;
              n = i19;
              k = i22;
              i1 = i24;
              bool1 = bool6;
              bool2 = bool7;
              i11 = i16;
              j = i27;
              i2 = i20;
              m = i23;
              i3 = i25;
              bool3 = bool9;
              bool4 = bool8;
              if (paramInt3 == 1)
              {
                i10 = i16;
                i = i26;
                n = i19;
                k = i22;
                i1 = i24;
                bool1 = bool6;
                bool2 = bool7;
                i11 = i16;
                j = i27;
                i2 = i20;
                m = i23;
                i3 = i25;
                bool3 = bool9;
                bool4 = bool8;
                localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid < %d ORDER BY m.mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2), Integer.valueOf(i16) }), new Object[0]);
                i7 = i16;
                i9 = i37;
                bool6 = bool10;
              }
              else
              {
                i10 = i16;
                i = i26;
                n = i19;
                k = i22;
                i1 = i24;
                bool1 = bool6;
                bool2 = bool7;
                i11 = i16;
                j = i27;
                i2 = i20;
                m = i23;
                i3 = i25;
                bool3 = bool9;
                bool4 = bool8;
                if (paramInt4 != 0)
                {
                  i10 = i16;
                  i = i26;
                  n = i19;
                  k = i22;
                  i1 = i24;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i20;
                  m = i23;
                  i3 = i25;
                  bool3 = bool9;
                  bool4 = bool8;
                  if (paramInt2 != 0)
                  {
                    i10 = i16;
                    i = i26;
                    n = i19;
                    k = i22;
                    i1 = i24;
                    bool1 = bool6;
                    bool2 = bool7;
                    i11 = i16;
                    j = i27;
                    i2 = i20;
                    m = i23;
                    i3 = i25;
                    bool3 = bool9;
                    bool4 = bool8;
                    localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d ORDER BY m.mid ASC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2), Integer.valueOf(i16) }), new Object[0]);
                    i7 = i16;
                    i9 = i37;
                    bool6 = bool10;
                  }
                  else
                  {
                    i10 = i16;
                    i = i26;
                    n = i19;
                    k = i22;
                    i1 = i24;
                    bool1 = bool6;
                    bool2 = bool7;
                    i11 = i16;
                    j = i27;
                    i2 = i20;
                    m = i23;
                    i3 = i25;
                    bool3 = bool9;
                    bool4 = bool8;
                    localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.date <= %d ORDER BY m.mid ASC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt4), Integer.valueOf(0), Integer.valueOf(i16) }), new Object[0]);
                    i7 = i16;
                    i9 = i37;
                    bool6 = bool10;
                  }
                }
                else
                {
                  i10 = i16;
                  i = i26;
                  n = i19;
                  k = i22;
                  i1 = i24;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i20;
                  m = i23;
                  i3 = i25;
                  bool3 = bool9;
                  bool4 = bool8;
                  i6 = i40;
                  i7 = i39;
                  i4 = i31;
                  i5 = i38;
                  if (paramInt3 != 2) {
                    break label17127;
                  }
                  i10 = i16;
                  i = i26;
                  n = i19;
                  k = i22;
                  i1 = i24;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i20;
                  m = i23;
                  i3 = i25;
                  bool3 = bool9;
                  bool4 = bool8;
                  localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM messages WHERE uid = %d AND mid < 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                  i10 = i16;
                  i = i26;
                  n = i19;
                  k = i22;
                  i1 = i24;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i20;
                  m = i23;
                  i3 = i25;
                  bool3 = bool9;
                  bool4 = bool8;
                  if (((SQLiteCursor)localObject2).next())
                  {
                    i10 = i16;
                    i = i26;
                    n = i19;
                    k = i22;
                    i1 = i24;
                    bool1 = bool6;
                    bool2 = bool7;
                    i11 = i16;
                    j = i27;
                    i2 = i20;
                    m = i23;
                    i3 = i25;
                    bool3 = bool9;
                    bool4 = bool8;
                    i8 = ((SQLiteCursor)localObject2).intValue(0);
                  }
                  i10 = i16;
                  i = i26;
                  n = i8;
                  k = i22;
                  i1 = i24;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i8;
                  m = i23;
                  i3 = i25;
                  bool3 = bool9;
                  bool4 = bool8;
                  ((SQLiteCursor)localObject2).dispose();
                  i10 = i16;
                  i = i26;
                  n = i8;
                  k = i22;
                  i1 = i24;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i8;
                  m = i23;
                  i3 = i25;
                  bool3 = bool9;
                  bool4 = bool8;
                  localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT max(mid), max(date) FROM messages WHERE uid = %d AND out = 0 AND read_state IN(0,2) AND mid < 0", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
                  i10 = i16;
                  i = i26;
                  n = i8;
                  k = i22;
                  i1 = i24;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i27;
                  i2 = i8;
                  m = i23;
                  i3 = i25;
                  bool3 = bool9;
                  bool4 = bool8;
                  i9 = i34;
                  if (((SQLiteCursor)localObject2).next())
                  {
                    i10 = i16;
                    i = i26;
                    n = i8;
                    k = i22;
                    i1 = i24;
                    bool1 = bool6;
                    bool2 = bool7;
                    i11 = i16;
                    j = i27;
                    i2 = i8;
                    m = i23;
                    i3 = i25;
                    bool3 = bool9;
                    bool4 = bool8;
                    i9 = ((SQLiteCursor)localObject2).intValue(0);
                    i10 = i16;
                    i = i9;
                    n = i8;
                    k = i22;
                    i1 = i24;
                    bool1 = bool6;
                    bool2 = bool7;
                    i11 = i16;
                    j = i9;
                    i2 = i8;
                    m = i23;
                    i3 = i25;
                    bool3 = bool9;
                    bool4 = bool8;
                    i12 = ((SQLiteCursor)localObject2).intValue(1);
                  }
                  i10 = i16;
                  i = i9;
                  n = i8;
                  k = i22;
                  i1 = i12;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i9;
                  i2 = i8;
                  m = i23;
                  i3 = i12;
                  bool3 = bool9;
                  bool4 = bool8;
                  ((SQLiteCursor)localObject2).dispose();
                  i6 = i9;
                  i7 = i8;
                  i4 = i31;
                  i5 = i12;
                  if (i9 == 0) {
                    break label17127;
                  }
                  i10 = i16;
                  i = i9;
                  n = i8;
                  k = i22;
                  i1 = i12;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i9;
                  i2 = i8;
                  m = i23;
                  i3 = i12;
                  bool3 = bool9;
                  bool4 = bool8;
                  localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(*) FROM messages WHERE uid = %d AND mid <= %d AND out = 0 AND read_state IN(0,2)", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i9) }), new Object[0]);
                  i10 = i16;
                  i = i9;
                  n = i8;
                  k = i22;
                  i1 = i12;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i9;
                  i2 = i8;
                  m = i23;
                  i3 = i12;
                  bool3 = bool9;
                  bool4 = bool8;
                  i4 = i15;
                  if (((SQLiteCursor)localObject2).next())
                  {
                    i10 = i16;
                    i = i9;
                    n = i8;
                    k = i22;
                    i1 = i12;
                    bool1 = bool6;
                    bool2 = bool7;
                    i11 = i16;
                    j = i9;
                    i2 = i8;
                    m = i23;
                    i3 = i12;
                    bool3 = bool9;
                    bool4 = bool8;
                    i4 = ((SQLiteCursor)localObject2).intValue(0);
                  }
                  i10 = i16;
                  i = i9;
                  n = i8;
                  k = i4;
                  i1 = i12;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i9;
                  i2 = i8;
                  m = i4;
                  i3 = i12;
                  bool3 = bool9;
                  bool4 = bool8;
                  ((SQLiteCursor)localObject2).dispose();
                  i6 = i9;
                  i7 = i8;
                  i5 = i12;
                  break label17127;
                  label13230:
                  i10 = i16;
                  i = i6;
                  n = i7;
                  k = i4;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool7;
                  i11 = i16;
                  j = i6;
                  i2 = i7;
                  m = i4;
                  i3 = i5;
                  bool3 = bool9;
                  bool4 = bool8;
                  i15 = Math.max(i16, i4 + 10);
                  i12 = i15;
                  i8 = i7;
                  i9 = i4;
                  i7 = i14;
                  if (i4 < i18)
                  {
                    i9 = 0;
                    i6 = 0;
                    i8 = 0;
                    i7 = i14;
                    i12 = i15;
                  }
                }
              }
            }
          }
        }
        for (;;)
        {
          i10 = i12;
          i = i6;
          n = i8;
          k = i9;
          i1 = i5;
          bool1 = bool6;
          bool2 = bool7;
          i11 = i12;
          j = i6;
          i2 = i8;
          m = i9;
          i3 = i5;
          bool3 = bool9;
          bool4 = bool8;
          localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.replydata, m.media, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d ORDER BY m.mid ASC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(i7), Integer.valueOf(i12) }), new Object[0]);
          i7 = i12;
          i4 = i8;
          bool6 = bool10;
          break label17038;
          label13458:
          i10 = i7;
          i = i6;
          n = i4;
          k = i9;
          i1 = i5;
          bool1 = bool6;
          bool2 = bool5;
          i11 = i7;
          j = i6;
          i2 = i4;
          m = i9;
          i3 = i5;
          bool3 = bool6;
          bool4 = bool5;
          if (!((ArrayList)localObject6).contains(Long.valueOf(localMessage.reply_to_random_id)))
          {
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            ((ArrayList)localObject6).add(Long.valueOf(localMessage.reply_to_random_id));
          }
          i10 = i7;
          i = i6;
          n = i4;
          k = i9;
          i1 = i5;
          bool1 = bool6;
          bool2 = bool5;
          i11 = i7;
          j = i6;
          i2 = i4;
          m = i9;
          i3 = i5;
          bool3 = bool6;
          bool4 = bool5;
          localObject5 = (ArrayList)localHashMap2.get(Long.valueOf(localMessage.reply_to_random_id));
          localObject4 = localObject5;
          if (localObject5 == null)
          {
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            localObject4 = new ArrayList();
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            localHashMap2.put(Long.valueOf(localMessage.reply_to_random_id), localObject4);
          }
          i10 = i7;
          i = i6;
          n = i4;
          k = i9;
          i1 = i5;
          bool1 = bool6;
          bool2 = bool5;
          i11 = i7;
          j = i6;
          i2 = i4;
          m = i9;
          i3 = i5;
          bool3 = bool6;
          bool4 = bool5;
          ((ArrayList)localObject4).add(localMessage);
          break label6071;
          label13869:
          i10 = i7;
          i = i6;
          n = i4;
          k = i9;
          i1 = i5;
          bool1 = bool6;
          bool2 = bool5;
          i11 = i7;
          j = i6;
          i2 = i4;
          m = i9;
          i3 = i5;
          bool3 = bool6;
          bool4 = bool5;
          ((SQLiteCursor)localObject2).dispose();
          label16207:
          label16985:
          label17002:
          label17038:
          label17046:
          label17089:
          label17095:
          label17101:
          label17125:
          for (;;)
          {
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            Collections.sort(localTL_messages_messages.messages, new Comparator()
            {
              public int compare(TLRPC.Message paramAnonymous2Message1, TLRPC.Message paramAnonymous2Message2)
              {
                if ((paramAnonymous2Message1.id > 0) && (paramAnonymous2Message2.id > 0)) {
                  if (paramAnonymous2Message1.id <= paramAnonymous2Message2.id) {}
                }
                do
                {
                  do
                  {
                    return -1;
                    if (paramAnonymous2Message1.id >= paramAnonymous2Message2.id) {
                      break label102;
                    }
                    return 1;
                    if ((paramAnonymous2Message1.id >= 0) || (paramAnonymous2Message2.id >= 0)) {
                      break;
                    }
                  } while (paramAnonymous2Message1.id < paramAnonymous2Message2.id);
                  if (paramAnonymous2Message1.id <= paramAnonymous2Message2.id) {
                    break;
                  }
                  return 1;
                } while (paramAnonymous2Message1.date > paramAnonymous2Message2.date);
                if (paramAnonymous2Message1.date < paramAnonymous2Message2.date) {
                  return 1;
                }
                label102:
                return 0;
              }
            });
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if (paramInt3 != 3)
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if ((paramInt3 != 2) || (!bool5)) {}
            }
            else
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (!localTL_messages_messages.messages.isEmpty())
              {
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                i8 = ((TLRPC.Message)localTL_messages_messages.messages.get(localTL_messages_messages.messages.size() - 1)).id;
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                i12 = ((TLRPC.Message)localTL_messages_messages.messages.get(0)).id;
                if ((i8 > i13) || (i12 < i13))
                {
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  ((ArrayList)localObject6).clear();
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  localArrayList1.clear();
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  localArrayList2.clear();
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  localTL_messages_messages.messages.clear();
                }
              }
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if (paramInt3 == 3)
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (localTL_messages_messages.messages.size() == 1)
              {
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localTL_messages_messages.messages.clear();
              }
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if (!((ArrayList)localObject6).isEmpty())
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (!localHashMap1.isEmpty())
              {
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject6) }), new Object[0]);
              }
              do
              {
                for (;;)
                {
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  if (!((SQLiteCursor)localObject2).next()) {
                    break label16207;
                  }
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  localObject5 = ((SQLiteCursor)localObject2).byteBufferValue(0);
                  if (localObject5 != null)
                  {
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    localObject4 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject5, ((NativeByteBuffer)localObject5).readInt32(false), false);
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    ((NativeByteBuffer)localObject5).reuse();
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    ((TLRPC.Message)localObject4).id = ((SQLiteCursor)localObject2).intValue(1);
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    ((TLRPC.Message)localObject4).date = ((SQLiteCursor)localObject2).intValue(2);
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    ((TLRPC.Message)localObject4).dialog_id = paramLong;
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject4, localArrayList1, localArrayList2);
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    if (localHashMap1.isEmpty()) {
                      break;
                    }
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    localObject5 = (ArrayList)localHashMap1.get(Integer.valueOf(((TLRPC.Message)localObject4).id));
                    if (localObject5 != null)
                    {
                      i8 = 0;
                      for (;;)
                      {
                        i10 = i7;
                        i = i6;
                        n = i4;
                        k = i9;
                        i1 = i5;
                        bool1 = bool6;
                        bool2 = bool5;
                        i11 = i7;
                        j = i6;
                        i2 = i4;
                        m = i9;
                        i3 = i5;
                        bool3 = bool6;
                        bool4 = bool5;
                        if (i8 >= ((ArrayList)localObject5).size()) {
                          break;
                        }
                        i10 = i7;
                        i = i6;
                        n = i4;
                        k = i9;
                        i1 = i5;
                        bool1 = bool6;
                        bool2 = bool5;
                        i11 = i7;
                        j = i6;
                        i2 = i4;
                        m = i9;
                        i3 = i5;
                        bool3 = bool6;
                        bool4 = bool5;
                        ((TLRPC.Message)((ArrayList)localObject5).get(i8)).replyMessage = ((TLRPC.Message)localObject4);
                        i8 += 1;
                      }
                      i10 = i7;
                      i = i6;
                      n = i4;
                      k = i9;
                      i1 = i5;
                      bool1 = bool6;
                      bool2 = bool5;
                      i11 = i7;
                      j = i6;
                      i2 = i4;
                      m = i9;
                      i3 = i5;
                      bool3 = bool6;
                      bool4 = bool5;
                      localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject6) }), new Object[0]);
                    }
                  }
                }
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localObject5 = (ArrayList)localHashMap2.remove(Long.valueOf(((SQLiteCursor)localObject2).longValue(3)));
              } while (localObject5 == null);
              i8 = 0;
              for (;;)
              {
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                if (i8 >= ((ArrayList)localObject5).size()) {
                  break;
                }
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localObject6 = (TLRPC.Message)((ArrayList)localObject5).get(i8);
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                ((TLRPC.Message)localObject6).replyMessage = ((TLRPC.Message)localObject4);
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                ((TLRPC.Message)localObject6).reply_to_msg_id = ((TLRPC.Message)localObject4).id;
                i8 += 1;
              }
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              ((SQLiteCursor)localObject2).dispose();
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              if (!localHashMap2.isEmpty())
              {
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                localObject2 = localHashMap2.entrySet().iterator();
                i10 = i7;
                i = i6;
                n = i4;
                k = i9;
                i1 = i5;
                bool1 = bool6;
                bool2 = bool5;
                i11 = i7;
                j = i6;
                i2 = i4;
                m = i9;
                i3 = i5;
                bool3 = bool6;
                bool4 = bool5;
                if (((Iterator)localObject2).hasNext())
                {
                  i10 = i7;
                  i = i6;
                  n = i4;
                  k = i9;
                  i1 = i5;
                  bool1 = bool6;
                  bool2 = bool5;
                  i11 = i7;
                  j = i6;
                  i2 = i4;
                  m = i9;
                  i3 = i5;
                  bool3 = bool6;
                  bool4 = bool5;
                  localObject4 = (ArrayList)((Map.Entry)((Iterator)localObject2).next()).getValue();
                  i8 = 0;
                  for (;;)
                  {
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    if (i8 >= ((ArrayList)localObject4).size()) {
                      break;
                    }
                    i10 = i7;
                    i = i6;
                    n = i4;
                    k = i9;
                    i1 = i5;
                    bool1 = bool6;
                    bool2 = bool5;
                    i11 = i7;
                    j = i6;
                    i2 = i4;
                    m = i9;
                    i3 = i5;
                    bool3 = bool6;
                    bool4 = bool5;
                    ((TLRPC.Message)((ArrayList)localObject4).get(i8)).reply_to_random_id = 0L;
                    i8 += 1;
                  }
                }
              }
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if (!localArrayList1.isEmpty())
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              MessagesStorage.this.getUsersInternal(TextUtils.join(",", localArrayList1), localTL_messages_messages.users);
            }
            i10 = i7;
            i = i6;
            n = i4;
            k = i9;
            i1 = i5;
            bool1 = bool6;
            bool2 = bool5;
            i11 = i7;
            j = i6;
            i2 = i4;
            m = i9;
            i3 = i5;
            bool3 = bool6;
            bool4 = bool5;
            if (!localArrayList2.isEmpty())
            {
              i10 = i7;
              i = i6;
              n = i4;
              k = i9;
              i1 = i5;
              bool1 = bool6;
              bool2 = bool5;
              i11 = i7;
              j = i6;
              i2 = i4;
              m = i9;
              i3 = i5;
              bool3 = bool6;
              bool4 = bool5;
              MessagesStorage.this.getChatsInternal(TextUtils.join(",", localArrayList2), localTL_messages_messages.chats);
            }
            MessagesController.getInstance().processLoadedMessages(localTL_messages_messages, paramLong, i7, paramInt2, true, paramInt6, i6, i4, i9, i5, paramInt3, paramBoolean, bool6, this.val$loadIndex, bool5);
            return;
            if (i16 > i9) {
              break;
            }
            if (i9 < i18)
            {
              break;
              l4 = l1;
              if (l1 != 0L) {
                break label3829;
              }
              l4 = 1000000000L;
              if (i17 == 0) {
                break label3829;
              }
              l4 = 0x3B9ACA00 | i17 << 32;
              break label3829;
            }
            for (;;)
            {
              if (localObject2 == null) {
                break label17125;
              }
              break label4008;
              i14 = 0;
              break label7957;
              i15 = i9 - i16;
              i4 = i16 + 10;
              i6 = i5;
              i5 = i9;
              i8 = i7;
              bool5 = bool6;
              i14 = i12;
              break;
              bool6 = false;
              break label2494;
              i7 = 0;
              break label2962;
              localObject2 = null;
              i7 = i4;
              i4 = i9;
              i9 = i5;
              i5 = i8;
              i13 = i14;
            }
          }
          label17127:
          if ((i16 > i4) || (i4 < i18)) {
            break label13230;
          }
          i = i4 - i16;
          i12 = i16 + 10;
          i8 = i7;
          i9 = i4;
          i7 = i;
        }
      }
    });
  }
  
  public void getNewTask(final ArrayList<Integer> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i;
        try
        {
          if (paramArrayList != null)
          {
            localObject1 = TextUtils.join(",", paramArrayList);
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v2 WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
          }
          i = 0;
          Object localObject1 = null;
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT mid, date FROM enc_tasks_v2 WHERE date = (SELECT min(date) FROM enc_tasks_v2)", new Object[0]);
          while (localSQLiteCursor.next())
          {
            int j = localSQLiteCursor.intValue(0);
            i = localSQLiteCursor.intValue(1);
            Object localObject2 = localObject1;
            if (localObject1 == null) {
              localObject2 = new ArrayList();
            }
            ((ArrayList)localObject2).add(Integer.valueOf(j));
            localObject1 = localObject2;
          }
          localSQLiteCursor.dispose();
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          return;
        }
        MessagesController.getInstance().processLoadedDeleteTask(i, localException);
      }
    });
  }
  
  public TLObject getSentFile(final String paramString, final int paramInt)
  {
    if (paramString == null) {
      return null;
    }
    final Semaphore localSemaphore = new Semaphore(0);
    final ArrayList localArrayList = new ArrayList();
    this.storageQueue.postRunnable(new Runnable()
    {
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 27	org/telegram/messenger/MessagesStorage$48:val$path	Ljava/lang/String;
        //   4: invokestatic 46	org/telegram/messenger/Utilities:MD5	(Ljava/lang/String;)Ljava/lang/String;
        //   7: astore_1
        //   8: aload_1
        //   9: ifnull +102 -> 111
        //   12: aload_0
        //   13: getfield 25	org/telegram/messenger/MessagesStorage$48:this$0	Lorg/telegram/messenger/MessagesStorage;
        //   16: invokestatic 50	org/telegram/messenger/MessagesStorage:access$000	(Lorg/telegram/messenger/MessagesStorage;)Lorg/telegram/SQLite/SQLiteDatabase;
        //   19: getstatic 56	java/util/Locale:US	Ljava/util/Locale;
        //   22: ldc 58
        //   24: iconst_2
        //   25: anewarray 4	java/lang/Object
        //   28: dup
        //   29: iconst_0
        //   30: aload_1
        //   31: aastore
        //   32: dup
        //   33: iconst_1
        //   34: aload_0
        //   35: getfield 29	org/telegram/messenger/MessagesStorage$48:val$type	I
        //   38: invokestatic 64	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   41: aastore
        //   42: invokestatic 70	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   45: iconst_0
        //   46: anewarray 4	java/lang/Object
        //   49: invokevirtual 76	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
        //   52: astore_1
        //   53: aload_1
        //   54: invokevirtual 82	org/telegram/SQLite/SQLiteCursor:next	()Z
        //   57: ifeq +50 -> 107
        //   60: aload_1
        //   61: iconst_0
        //   62: invokevirtual 86	org/telegram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/telegram/tgnet/NativeByteBuffer;
        //   65: astore_2
        //   66: aload_2
        //   67: ifnull +40 -> 107
        //   70: aload_2
        //   71: aload_2
        //   72: iconst_0
        //   73: invokevirtual 92	org/telegram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   76: iconst_0
        //   77: invokestatic 98	org/telegram/tgnet/TLRPC$MessageMedia:TLdeserialize	(Lorg/telegram/tgnet/AbstractSerializedData;IZ)Lorg/telegram/tgnet/TLRPC$MessageMedia;
        //   80: astore_3
        //   81: aload_2
        //   82: invokevirtual 101	org/telegram/tgnet/NativeByteBuffer:reuse	()V
        //   85: aload_3
        //   86: instanceof 103
        //   89: ifeq +30 -> 119
        //   92: aload_0
        //   93: getfield 31	org/telegram/messenger/MessagesStorage$48:val$result	Ljava/util/ArrayList;
        //   96: aload_3
        //   97: checkcast 103	org/telegram/tgnet/TLRPC$TL_messageMediaDocument
        //   100: getfield 107	org/telegram/tgnet/TLRPC$TL_messageMediaDocument:document	Lorg/telegram/tgnet/TLRPC$Document;
        //   103: invokevirtual 113	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   106: pop
        //   107: aload_1
        //   108: invokevirtual 116	org/telegram/SQLite/SQLiteCursor:dispose	()V
        //   111: aload_0
        //   112: getfield 33	org/telegram/messenger/MessagesStorage$48:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   115: invokevirtual 121	java/util/concurrent/Semaphore:release	()V
        //   118: return
        //   119: aload_3
        //   120: instanceof 123
        //   123: ifeq -16 -> 107
        //   126: aload_0
        //   127: getfield 31	org/telegram/messenger/MessagesStorage$48:val$result	Ljava/util/ArrayList;
        //   130: aload_3
        //   131: checkcast 123	org/telegram/tgnet/TLRPC$TL_messageMediaPhoto
        //   134: getfield 127	org/telegram/tgnet/TLRPC$TL_messageMediaPhoto:photo	Lorg/telegram/tgnet/TLRPC$Photo;
        //   137: invokevirtual 113	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   140: pop
        //   141: goto -34 -> 107
        //   144: astore_1
        //   145: ldc -127
        //   147: aload_1
        //   148: invokestatic 135	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
        //   151: aload_0
        //   152: getfield 33	org/telegram/messenger/MessagesStorage$48:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   155: invokevirtual 121	java/util/concurrent/Semaphore:release	()V
        //   158: return
        //   159: astore_1
        //   160: aload_0
        //   161: getfield 33	org/telegram/messenger/MessagesStorage$48:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   164: invokevirtual 121	java/util/concurrent/Semaphore:release	()V
        //   167: aload_1
        //   168: athrow
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	169	0	this	48
        //   7	101	1	localObject1	Object
        //   144	4	1	localException	Exception
        //   159	9	1	localObject2	Object
        //   65	17	2	localNativeByteBuffer	NativeByteBuffer
        //   80	51	3	localMessageMedia	TLRPC.MessageMedia
        // Exception table:
        //   from	to	target	type
        //   0	8	144	java/lang/Exception
        //   12	66	144	java/lang/Exception
        //   70	107	144	java/lang/Exception
        //   107	111	144	java/lang/Exception
        //   119	141	144	java/lang/Exception
        //   0	8	159	finally
        //   12	66	159	finally
        //   70	107	159	finally
        //   107	111	159	finally
        //   119	141	159	finally
        //   145	151	159	finally
      }
    });
    try
    {
      localSemaphore.acquire();
      if (!localArrayList.isEmpty())
      {
        paramString = (TLObject)localArrayList.get(0);
        return paramString;
      }
    }
    catch (Exception paramString)
    {
      for (;;)
      {
        FileLog.e("tmessages", paramString);
        continue;
        paramString = null;
      }
    }
  }
  
  public DispatchQueue getStorageQueue()
  {
    return this.storageQueue;
  }
  
  public void getUnsentMessages(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject2;
        ArrayList localArrayList2;
        ArrayList localArrayList3;
        ArrayList localArrayList4;
        Object localObject3;
        Object localObject1;
        ArrayList localArrayList5;
        ArrayList localArrayList6;
        SQLiteCursor localSQLiteCursor;
        int i;
        for (;;)
        {
          try
          {
            localObject2 = new HashMap();
            ArrayList localArrayList1 = new ArrayList();
            localArrayList2 = new ArrayList();
            localArrayList3 = new ArrayList();
            localArrayList4 = new ArrayList();
            localObject3 = new ArrayList();
            localObject1 = new ArrayList();
            localArrayList5 = new ArrayList();
            localArrayList6 = new ArrayList();
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.mid < 0 AND m.send_state = 1 ORDER BY m.mid DESC LIMIT " + paramInt, new Object[0]);
            if (!localSQLiteCursor.next()) {
              break;
            }
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(1);
            if (localNativeByteBuffer == null) {
              continue;
            }
            TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (((HashMap)localObject2).containsKey(Integer.valueOf(localMessage.id))) {
              continue;
            }
            MessageObject.setUnreadFlags(localMessage, localSQLiteCursor.intValue(0));
            localMessage.id = localSQLiteCursor.intValue(3);
            localMessage.date = localSQLiteCursor.intValue(4);
            if (!localSQLiteCursor.isNull(5)) {
              localMessage.random_id = localSQLiteCursor.longValue(5);
            }
            localMessage.dialog_id = localSQLiteCursor.longValue(6);
            localMessage.seq_in = localSQLiteCursor.intValue(7);
            localMessage.seq_out = localSQLiteCursor.intValue(8);
            localMessage.ttl = localSQLiteCursor.intValue(9);
            localArrayList1.add(localMessage);
            ((HashMap)localObject2).put(Integer.valueOf(localMessage.id), localMessage);
            i = (int)localMessage.dialog_id;
            j = (int)(localMessage.dialog_id >> 32);
            if (i == 0) {
              break label496;
            }
            if (j == 1)
            {
              if (!localArrayList5.contains(Integer.valueOf(i))) {
                localArrayList5.add(Integer.valueOf(i));
              }
              MessagesStorage.addUsersAndChatsFromMessage(localMessage, (ArrayList)localObject3, (ArrayList)localObject1);
              localMessage.send_state = localSQLiteCursor.intValue(2);
              if (((localMessage.to_id.channel_id == 0) && (!MessageObject.isUnread(localMessage)) && (i != 0)) || (localMessage.id > 0)) {
                localMessage.send_state = 0;
              }
              if ((i != 0) || (localSQLiteCursor.isNull(5))) {
                continue;
              }
              localMessage.random_id = localSQLiteCursor.longValue(5);
              continue;
            }
            if (i >= 0) {
              break label471;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          int j = -i;
          if (!((ArrayList)localObject1).contains(Integer.valueOf(j)))
          {
            ((ArrayList)localObject1).add(Integer.valueOf(-i));
            continue;
            label471:
            if (!((ArrayList)localObject3).contains(Integer.valueOf(i)))
            {
              ((ArrayList)localObject3).add(Integer.valueOf(i));
              continue;
              label496:
              if (!localArrayList6.contains(Integer.valueOf(j))) {
                localArrayList6.add(Integer.valueOf(j));
              }
            }
          }
        }
        localSQLiteCursor.dispose();
        if (!localArrayList6.isEmpty()) {
          MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", localArrayList6), localArrayList4, (ArrayList)localObject3);
        }
        if (!((ArrayList)localObject3).isEmpty()) {
          MessagesStorage.this.getUsersInternal(TextUtils.join(",", (Iterable)localObject3), localArrayList2);
        }
        if ((!((ArrayList)localObject1).isEmpty()) || (!localArrayList5.isEmpty()))
        {
          localObject2 = new StringBuilder();
          i = 0;
          while (i < ((ArrayList)localObject1).size())
          {
            localObject3 = (Integer)((ArrayList)localObject1).get(i);
            if (((StringBuilder)localObject2).length() != 0) {
              ((StringBuilder)localObject2).append(",");
            }
            ((StringBuilder)localObject2).append(localObject3);
            i += 1;
          }
        }
        for (;;)
        {
          if (i < localArrayList5.size())
          {
            localObject1 = (Integer)localArrayList5.get(i);
            if (((StringBuilder)localObject2).length() != 0) {
              ((StringBuilder)localObject2).append(",");
            }
            ((StringBuilder)localObject2).append(-((Integer)localObject1).intValue());
            i += 1;
          }
          else
          {
            MessagesStorage.this.getChatsInternal(((StringBuilder)localObject2).toString(), localArrayList3);
            SendMessagesHelper.getInstance().processUnsentMessages(localException, localArrayList2, localArrayList3, localArrayList4);
            return;
            i = 0;
          }
        }
      }
    });
  }
  
  public TLRPC.User getUser(int paramInt)
  {
    TLRPC.User localUser = null;
    try
    {
      ArrayList localArrayList = new ArrayList();
      getUsersInternal("" + paramInt, localArrayList);
      if (!localArrayList.isEmpty()) {
        localUser = (TLRPC.User)localArrayList.get(0);
      }
      return localUser;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
    return null;
  }
  
  public TLRPC.User getUserSync(final int paramInt)
  {
    final Semaphore localSemaphore = new Semaphore(0);
    final TLRPC.User[] arrayOfUser = new TLRPC.User[1];
    getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        arrayOfUser[0] = MessagesStorage.this.getUser(paramInt);
        localSemaphore.release();
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfUser[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
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
      localArrayList.clear();
      FileLog.e("tmessages", paramArrayList);
    }
    return localArrayList;
  }
  
  public void getUsersInternal(String paramString, ArrayList<TLRPC.User> paramArrayList)
    throws Exception
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayList == null)) {
      return;
    }
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
        FileLog.e("tmessages", localException);
      }
    }
    paramString.dispose();
  }
  
  public void getWallpapers()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final ArrayList localArrayList;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT data FROM wallpapers WHERE 1", new Object[0]);
          localArrayList = new ArrayList();
          while (localSQLiteCursor.next())
          {
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
            if (localNativeByteBuffer != null)
            {
              TLRPC.WallPaper localWallPaper = TLRPC.WallPaper.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              localArrayList.add(localWallPaper);
            }
          }
          localException.dispose();
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
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.wallpapersDidLoaded, new Object[] { localArrayList });
          }
        });
      }
    });
  }
  
  public boolean hasAuthMessage(final int paramInt)
  {
    final Semaphore localSemaphore = new Semaphore(0);
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
          FileLog.e("tmessages", localException);
          return;
        }
        finally
        {
          localSemaphore.release();
        }
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public boolean isDialogHasMessages(final long paramLong)
  {
    final Semaphore localSemaphore = new Semaphore(0);
    boolean[] arrayOfBoolean = new boolean[1];
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d LIMIT 1", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
          localSemaphore[0] = localSQLiteCursor.next();
          localSQLiteCursor.dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          return;
        }
        finally
        {
          this.val$semaphore.release();
        }
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public boolean isMigratedChat(final int paramInt)
  {
    final Semaphore localSemaphore = new Semaphore(0);
    final boolean[] arrayOfBoolean = new boolean[1];
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int j = 0;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT info FROM chat_settings_v2 WHERE uid = " + paramInt, new Object[0]);
          boolean[] arrayOfBoolean = null;
          new ArrayList();
          Object localObject1 = arrayOfBoolean;
          if (localSQLiteCursor.next())
          {
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
            localObject1 = arrayOfBoolean;
            if (localNativeByteBuffer != null)
            {
              localObject1 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
            }
          }
          localSQLiteCursor.dispose();
          arrayOfBoolean = arrayOfBoolean;
          int i = j;
          if ((localObject1 instanceof TLRPC.TL_channelFull))
          {
            i = j;
            if (((TLRPC.ChatFull)localObject1).migrated_from_chat_id != 0) {
              i = 1;
            }
          }
          arrayOfBoolean[0] = i;
          if (localSemaphore != null) {
            localSemaphore.release();
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          return;
        }
        finally
        {
          if (localSemaphore != null) {
            localSemaphore.release();
          }
        }
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void loadChatInfo(final int paramInt, final Semaphore paramSemaphore, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject6 = null;
        localObject7 = null;
        NativeByteBuffer localNativeByteBuffer1 = null;
        Object localObject5 = null;
        localArrayList = new ArrayList();
        localObject4 = localObject7;
        localObject1 = localNativeByteBuffer1;
        label470:
        do
        {
          try
          {
            SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + paramInt, new Object[0]);
            localObject2 = localObject5;
            localObject4 = localObject7;
            localObject1 = localNativeByteBuffer1;
            if (localSQLiteCursor.next())
            {
              localObject4 = localObject7;
              localObject1 = localNativeByteBuffer1;
              NativeByteBuffer localNativeByteBuffer2 = localSQLiteCursor.byteBufferValue(0);
              localObject2 = localObject5;
              if (localNativeByteBuffer2 != null)
              {
                localObject4 = localObject7;
                localObject1 = localNativeByteBuffer1;
                localObject2 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer2, localNativeByteBuffer2.readInt32(false), false);
                localObject4 = localObject2;
                localObject1 = localObject2;
                localNativeByteBuffer2.reuse();
                localObject4 = localObject2;
                localObject1 = localObject2;
                ((TLRPC.ChatFull)localObject2).pinned_msg_id = localSQLiteCursor.intValue(1);
              }
            }
            localObject4 = localObject2;
            localObject1 = localObject2;
            localSQLiteCursor.dispose();
            localObject4 = localObject2;
            localObject1 = localObject2;
            if (!(localObject2 instanceof TLRPC.TL_chatFull)) {
              break label470;
            }
            localObject4 = localObject2;
            localObject1 = localObject2;
            localObject5 = new StringBuilder();
            i = 0;
            for (;;)
            {
              localObject4 = localObject2;
              localObject1 = localObject2;
              if (i >= ((TLRPC.ChatFull)localObject2).participants.participants.size()) {
                break;
              }
              localObject4 = localObject2;
              localObject1 = localObject2;
              localObject7 = (TLRPC.ChatParticipant)((TLRPC.ChatFull)localObject2).participants.participants.get(i);
              localObject4 = localObject2;
              localObject1 = localObject2;
              if (((StringBuilder)localObject5).length() != 0)
              {
                localObject4 = localObject2;
                localObject1 = localObject2;
                ((StringBuilder)localObject5).append(",");
              }
              localObject4 = localObject2;
              localObject1 = localObject2;
              ((StringBuilder)localObject5).append(((TLRPC.ChatParticipant)localObject7).user_id);
              i += 1;
            }
            localObject4 = localObject2;
            localObject1 = localObject2;
            if (((StringBuilder)localObject5).length() != 0)
            {
              localObject4 = localObject2;
              localObject1 = localObject2;
              MessagesStorage.this.getUsersInternal(((StringBuilder)localObject5).toString(), localArrayList);
            }
          }
          catch (Exception localException1)
          {
            for (;;)
            {
              Object localObject2;
              localObject1 = localObject4;
              FileLog.e("tmessages", localException1);
              return;
              localObject4 = localException1;
              localObject1 = localException1;
              ((SQLiteCursor)localObject7).dispose();
              localObject4 = localException1;
              localObject1 = localException1;
              StringBuilder localStringBuilder = new StringBuilder();
              int i = 0;
              for (;;)
              {
                localObject4 = localException1;
                localObject1 = localException1;
                if (i >= localException1.bot_info.size()) {
                  break;
                }
                localObject4 = localException1;
                localObject1 = localException1;
                localObject7 = (TLRPC.BotInfo)localException1.bot_info.get(i);
                localObject4 = localException1;
                localObject1 = localException1;
                if (localStringBuilder.length() != 0)
                {
                  localObject4 = localException1;
                  localObject1 = localException1;
                  localStringBuilder.append(",");
                }
                localObject4 = localException1;
                localObject1 = localException1;
                localStringBuilder.append(((TLRPC.BotInfo)localObject7).user_id);
                i += 1;
              }
              localObject4 = localException1;
              localObject1 = localException1;
              if (localStringBuilder.length() != 0)
              {
                localObject4 = localException1;
                localObject1 = localException1;
                MessagesStorage.this.getUsersInternal(localStringBuilder.toString(), localArrayList);
              }
            }
          }
          finally
          {
            MessagesController.getInstance().processChatInfo(paramInt, (TLRPC.ChatFull)localObject1, localArrayList, true, paramBoolean1, paramBoolean2, null);
            if (paramSemaphore == null) {
              break label1077;
            }
            paramSemaphore.release();
          }
          localObject4 = localObject2;
          localObject1 = localObject2;
          if (paramSemaphore != null)
          {
            localObject4 = localObject2;
            localObject1 = localObject2;
            paramSemaphore.release();
          }
          localObject5 = localObject6;
          localObject4 = localObject2;
          localObject1 = localObject2;
          if ((localObject2 instanceof TLRPC.TL_channelFull))
          {
            localObject5 = localObject6;
            localObject4 = localObject2;
            localObject1 = localObject2;
            if (((TLRPC.ChatFull)localObject2).pinned_msg_id != 0)
            {
              localObject4 = localObject2;
              localObject1 = localObject2;
              localObject5 = MessagesQuery.loadPinnedMessage(paramInt, ((TLRPC.ChatFull)localObject2).pinned_msg_id, false);
            }
          }
          MessagesController.getInstance().processChatInfo(paramInt, (TLRPC.ChatFull)localObject2, localArrayList, true, paramBoolean1, paramBoolean2, (MessageObject)localObject5);
          if (paramSemaphore != null) {
            paramSemaphore.release();
          }
          return;
          localObject4 = localObject2;
          localObject1 = localObject2;
        } while (!(localObject2 instanceof TLRPC.TL_channelFull));
        localObject4 = localObject2;
        localObject1 = localObject2;
        localObject7 = MessagesStorage.this.database.queryFinalized("SELECT us.data, us.status, cu.data, cu.date FROM channel_users_v2 as cu LEFT JOIN users as us ON us.uid = cu.uid WHERE cu.did = " + -paramInt + " ORDER BY cu.date DESC", new Object[0]);
        localObject4 = localObject2;
        localObject1 = localObject2;
        ((TLRPC.ChatFull)localObject2).participants = new TLRPC.TL_chatParticipants();
        for (;;)
        {
          localObject4 = localObject2;
          localObject1 = localObject2;
          boolean bool = ((SQLiteCursor)localObject7).next();
          if (!bool) {
            break;
          }
          localObject4 = null;
          localObject5 = null;
          localObject1 = localObject2;
          try
          {
            localNativeByteBuffer1 = ((SQLiteCursor)localObject7).byteBufferValue(0);
            if (localNativeByteBuffer1 != null)
            {
              localObject1 = localObject2;
              localObject4 = TLRPC.User.TLdeserialize(localNativeByteBuffer1, localNativeByteBuffer1.readInt32(false), false);
              localObject1 = localObject2;
              localNativeByteBuffer1.reuse();
            }
            localObject1 = localObject2;
            localNativeByteBuffer1 = ((SQLiteCursor)localObject7).byteBufferValue(2);
            if (localNativeByteBuffer1 != null)
            {
              localObject1 = localObject2;
              localObject5 = TLRPC.ChannelParticipant.TLdeserialize(localNativeByteBuffer1, localNativeByteBuffer1.readInt32(false), false);
              localObject1 = localObject2;
              localNativeByteBuffer1.reuse();
            }
            if ((localObject4 != null) && (localObject5 != null))
            {
              localObject1 = localObject2;
              if (((TLRPC.User)localObject4).status != null)
              {
                localObject1 = localObject2;
                ((TLRPC.User)localObject4).status.expires = ((SQLiteCursor)localObject7).intValue(1);
              }
              localObject1 = localObject2;
              localArrayList.add(localObject4);
              localObject1 = localObject2;
              ((TLRPC.ChannelParticipant)localObject5).date = ((SQLiteCursor)localObject7).intValue(3);
              localObject1 = localObject2;
              localObject4 = new TLRPC.TL_chatChannelParticipant();
              localObject1 = localObject2;
              ((TLRPC.TL_chatChannelParticipant)localObject4).user_id = ((TLRPC.ChannelParticipant)localObject5).user_id;
              localObject1 = localObject2;
              ((TLRPC.TL_chatChannelParticipant)localObject4).date = ((TLRPC.ChannelParticipant)localObject5).date;
              localObject1 = localObject2;
              ((TLRPC.TL_chatChannelParticipant)localObject4).inviter_id = ((TLRPC.ChannelParticipant)localObject5).inviter_id;
              localObject1 = localObject2;
              ((TLRPC.TL_chatChannelParticipant)localObject4).channelParticipant = ((TLRPC.ChannelParticipant)localObject5);
              localObject1 = localObject2;
              ((TLRPC.ChatFull)localObject2).participants.participants.add(localObject4);
            }
          }
          catch (Exception localException2)
          {
            localObject4 = localObject2;
            localObject1 = localObject2;
            FileLog.e("tmessages", localException2);
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
        ArrayList localArrayList3;
        final HashMap localHashMap1;
        Object localObject3;
        long l1;
        int j;
        for (;;)
        {
          try
          {
            localArrayList1 = new ArrayList();
            localArrayList2 = new ArrayList();
            localArrayList3 = new ArrayList();
            localHashMap1 = new HashMap();
            localObject3 = MessagesStorage.this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count != 0", new Object[0]);
            StringBuilder localStringBuilder = new StringBuilder();
            if (!((SQLiteCursor)localObject3).next()) {
              break;
            }
            if ((!((SQLiteCursor)localObject3).isNull(2)) && (((SQLiteCursor)localObject3).intValue(2) == 1)) {
              continue;
            }
            l1 = ((SQLiteCursor)localObject3).longValue(0);
            localHashMap1.put(Long.valueOf(l1), Integer.valueOf(((SQLiteCursor)localObject3).intValue(1)));
            if (localStringBuilder.length() != 0) {
              localStringBuilder.append(",");
            }
            localStringBuilder.append(l1);
            i = (int)l1;
            j = (int)(l1 >> 32);
            if (i == 0) {
              break label223;
            }
            if (i < 0)
            {
              if (localArrayList2.contains(Integer.valueOf(-i))) {
                continue;
              }
              localArrayList2.add(Integer.valueOf(-i));
              continue;
            }
            if (localArrayList1.contains(Integer.valueOf(i))) {
              continue;
            }
          }
          catch (Exception localException1)
          {
            FileLog.e("tmessages", localException1);
            return;
          }
          localArrayList1.add(Integer.valueOf(i));
          continue;
          label223:
          if (!localArrayList3.contains(Integer.valueOf(j))) {
            localArrayList3.add(Integer.valueOf(j));
          }
        }
        ((SQLiteCursor)localObject3).dispose();
        Object localObject4 = new ArrayList();
        HashMap localHashMap2 = new HashMap();
        final ArrayList localArrayList4 = new ArrayList();
        final ArrayList localArrayList5 = new ArrayList();
        final ArrayList localArrayList6 = new ArrayList();
        final ArrayList localArrayList7 = new ArrayList();
        Object localObject2;
        if (localException1.length() > 0)
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages WHERE uid IN (" + localException1.toString() + ") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50", new Object[0]);
          while (localSQLiteCursor.next())
          {
            Object localObject1 = localSQLiteCursor.byteBufferValue(1);
            if (localObject1 != null)
            {
              TLRPC.Message localMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
              ((NativeByteBuffer)localObject1).reuse();
              MessageObject.setUnreadFlags(localMessage, localSQLiteCursor.intValue(0));
              localMessage.id = localSQLiteCursor.intValue(3);
              localMessage.date = localSQLiteCursor.intValue(4);
              localMessage.dialog_id = localSQLiteCursor.longValue(5);
              localArrayList4.add(localMessage);
              i = (int)localMessage.dialog_id;
              MessagesStorage.addUsersAndChatsFromMessage(localMessage, localArrayList1, localArrayList2);
              localMessage.send_state = localSQLiteCursor.intValue(2);
              if (((localMessage.to_id.channel_id == 0) && (!MessageObject.isUnread(localMessage)) && (i != 0)) || (localMessage.id > 0)) {
                localMessage.send_state = 0;
              }
              if ((i == 0) && (!localSQLiteCursor.isNull(5))) {
                localMessage.random_id = localSQLiteCursor.longValue(5);
              }
              try
              {
                if ((localMessage.reply_to_msg_id != 0) && (((localMessage.action instanceof TLRPC.TL_messageActionPinMessage)) || ((localMessage.action instanceof TLRPC.TL_messageActionGameScore))))
                {
                  if (!localSQLiteCursor.isNull(6))
                  {
                    localObject1 = localSQLiteCursor.byteBufferValue(6);
                    if (localObject1 != null)
                    {
                      localMessage.replyMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
                      ((NativeByteBuffer)localObject1).reuse();
                      if (localMessage.replyMessage != null) {
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
                    if (!((ArrayList)localObject4).contains(Long.valueOf(l1))) {
                      ((ArrayList)localObject4).add(Long.valueOf(l1));
                    }
                    localObject3 = (ArrayList)localHashMap2.get(Integer.valueOf(localMessage.reply_to_msg_id));
                    localObject1 = localObject3;
                    if (localObject3 == null)
                    {
                      localObject1 = new ArrayList();
                      localHashMap2.put(Integer.valueOf(localMessage.reply_to_msg_id), localObject1);
                    }
                    ((ArrayList)localObject1).add(localMessage);
                  }
                }
              }
              catch (Exception localException2)
              {
                FileLog.e("tmessages", localException2);
              }
            }
          }
          localSQLiteCursor.dispose();
          if (!((ArrayList)localObject4).isEmpty())
          {
            localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject4) }), new Object[0]);
            while (((SQLiteCursor)localObject2).next())
            {
              localObject4 = ((SQLiteCursor)localObject2).byteBufferValue(0);
              if (localObject4 != null)
              {
                localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
                ((NativeByteBuffer)localObject4).reuse();
                ((TLRPC.Message)localObject3).id = ((SQLiteCursor)localObject2).intValue(1);
                ((TLRPC.Message)localObject3).date = ((SQLiteCursor)localObject2).intValue(2);
                ((TLRPC.Message)localObject3).dialog_id = ((SQLiteCursor)localObject2).longValue(3);
                MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject3, localArrayList1, localArrayList2);
                localObject4 = (ArrayList)localHashMap2.get(Integer.valueOf(((TLRPC.Message)localObject3).id));
                if (localObject4 != null)
                {
                  i = 0;
                  while (i < ((ArrayList)localObject4).size())
                  {
                    ((TLRPC.Message)((ArrayList)localObject4).get(i)).replyMessage = ((TLRPC.Message)localObject3);
                    i += 1;
                  }
                }
              }
            }
            ((SQLiteCursor)localObject2).dispose();
          }
          if (!localArrayList3.isEmpty()) {
            MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", localArrayList3), localArrayList7, localArrayList1);
          }
          if (!localArrayList1.isEmpty()) {
            MessagesStorage.this.getUsersInternal(TextUtils.join(",", localArrayList1), localArrayList5);
          }
          if (!localArrayList2.isEmpty()) {
            MessagesStorage.this.getChatsInternal(TextUtils.join(",", localArrayList2), localArrayList6);
          }
        }
        for (int i = 0;; i = j + 1)
        {
          int k;
          if (i < localArrayList6.size())
          {
            localObject2 = (TLRPC.Chat)localArrayList6.get(i);
            j = i;
            if (localObject2 != null) {
              if (!((TLRPC.Chat)localObject2).left)
              {
                j = i;
                if (((TLRPC.Chat)localObject2).migrated_to == null) {}
              }
              else
              {
                l1 = -((TLRPC.Chat)localObject2).id;
                MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = " + l1).stepThis().dispose();
                MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", new Object[] { Long.valueOf(l1) })).stepThis().dispose();
                localArrayList6.remove(i);
                k = i - 1;
                localHashMap1.remove(Long.valueOf(-((TLRPC.Chat)localObject2).id));
              }
            }
          }
          else
          {
            for (i = 0;; i = j + 1)
            {
              j = k;
              if (i >= localArrayList4.size()) {
                break;
              }
              j = i;
              if (((TLRPC.Message)localArrayList4.get(i)).dialog_id == -((TLRPC.Chat)localObject2).id)
              {
                localArrayList4.remove(i);
                j = i - 1;
                continue;
                Collections.reverse(localArrayList4);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    NotificationsController.getInstance().processLoadedUnreadMessages(localHashMap1, localArrayList4, localArrayList5, localArrayList6, localArrayList7);
                  }
                });
                return;
              }
            }
          }
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
        final ArrayList localArrayList;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT id, image_url, thumb_url, local_url, width, height, size, date, document FROM web_recent_v3 WHERE type = " + paramInt + " ORDER BY date DESC", new Object[0]);
          localArrayList = new ArrayList();
          while (localSQLiteCursor.next())
          {
            MediaController.SearchImage localSearchImage = new MediaController.SearchImage();
            localSearchImage.id = localSQLiteCursor.stringValue(0);
            localSearchImage.imageUrl = localSQLiteCursor.stringValue(1);
            localSearchImage.thumbUrl = localSQLiteCursor.stringValue(2);
            localSearchImage.localUrl = localSQLiteCursor.stringValue(3);
            localSearchImage.width = localSQLiteCursor.intValue(4);
            localSearchImage.height = localSQLiteCursor.intValue(5);
            localSearchImage.size = localSQLiteCursor.intValue(6);
            localSearchImage.date = localSQLiteCursor.intValue(7);
            if (!localSQLiteCursor.isNull(8))
            {
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(8);
              if (localNativeByteBuffer != null)
              {
                localSearchImage.document = TLRPC.Document.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
              }
            }
            localSearchImage.type = paramInt;
            localArrayList.add(localSearchImage);
          }
          localThrowable.dispose();
        }
        catch (Throwable localThrowable)
        {
          FileLog.e("tmessages", localThrowable);
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentImagesDidLoaded, new Object[] { Integer.valueOf(MessagesStorage.12.this.val$type), localArrayList });
          }
        });
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
          long l2 = paramMessage.id;
          long l1 = l2;
          if (paramMessage.to_id.channel_id != 0) {
            l1 = l2 | paramMessage.to_id.channel_id << 32;
          }
          MessagesStorage.this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid = " + l1).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void markMessagesAsDeleted(final ArrayList<Integer> paramArrayList, boolean paramBoolean, final int paramInt)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.markMessagesAsDeletedInternal(paramArrayList, paramInt);
        }
      });
      return;
    }
    markMessagesAsDeletedInternal(paramArrayList, paramInt);
  }
  
  public void markMessagesAsDeletedByRandoms(final ArrayList<Long> paramArrayList)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final ArrayList localArrayList;
        do
        {
          try
          {
            Object localObject = TextUtils.join(",", paramArrayList);
            localObject = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id IN(%s)", new Object[] { localObject }), new Object[0]);
            localArrayList = new ArrayList();
            while (((SQLiteCursor)localObject).next()) {
              localArrayList.add(Integer.valueOf(((SQLiteCursor)localObject).intValue(0)));
            }
            localException.dispose();
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
        } while (localArrayList.isEmpty());
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, new Object[] { localArrayList, Integer.valueOf(0) });
          }
        });
        MessagesStorage.getInstance().updateDialogsWithReadMessagesInternal(localArrayList, null, null);
        MessagesStorage.getInstance().markMessagesAsDeletedInternal(localArrayList, 0);
        MessagesStorage.getInstance().updateDialogsWithDeletedMessagesInternal(localArrayList, 0);
      }
    });
  }
  
  public void markMessagesAsRead(final SparseArray<Long> paramSparseArray1, final SparseArray<Long> paramSparseArray2, final HashMap<Integer, Integer> paramHashMap, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.markMessagesAsReadInternal(paramSparseArray1, paramSparseArray2, paramHashMap);
        }
      });
      return;
    }
    markMessagesAsReadInternal(paramSparseArray1, paramSparseArray2, paramHashMap);
  }
  
  public void markMessagesContentAsRead(final ArrayList<Long> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid IN (%s)", new Object[] { TextUtils.join(",", paramArrayList) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void openDatabase()
  {
    this.cacheFile = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db");
    int i = 0;
    if (!this.cacheFile.exists()) {
      i = 1;
    }
    for (;;)
    {
      try
      {
        this.database = new SQLiteDatabase(this.cacheFile.getPath());
        this.database.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
        this.database.executeFast("PRAGMA temp_store = 1").stepThis().dispose();
        if (i == 0) {
          continue;
        }
        this.database.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
        this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
        this.database.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
        this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
        this.database.executeFast("CREATE TABLE messages(mid INTEGER PRIMARY KEY, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER)").stepThis().dispose();
        this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_idx_messages ON messages(uid, mid);").stepThis().dispose();
        this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
        this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
        this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
        this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
        this.database.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
        this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
        this.database.executeFast("CREATE TABLE user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
        this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
        this.database.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER)").stepThis().dispose();
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
        this.database.executeFast("CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
        this.database.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
        this.database.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
        this.database.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB)").stepThis().dispose();
        this.database.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
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
        this.database.executeFast("CREATE TABLE user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
        this.database.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
        this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
        this.database.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
        this.database.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
        this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
        this.database.executeFast("CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
        this.database.executeFast("CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
        this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        continue;
        secretPBytes = localException1.byteArrayValue(6);
        if ((secretPBytes == null) || (secretPBytes.length != 1)) {
          continue;
        }
        secretPBytes = null;
        continue;
      }
      loadUnreadMessages();
      loadPendingTasks();
      return;
      try
      {
        SQLiteCursor localSQLiteCursor = this.database.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
        if (localSQLiteCursor.next())
        {
          lastSeqValue = localSQLiteCursor.intValue(0);
          lastPtsValue = localSQLiteCursor.intValue(1);
          lastDateValue = localSQLiteCursor.intValue(2);
          lastQtsValue = localSQLiteCursor.intValue(3);
          lastSecretVersion = localSQLiteCursor.intValue(4);
          secretG = localSQLiteCursor.intValue(5);
          if (!localSQLiteCursor.isNull(6)) {
            continue;
          }
          secretPBytes = null;
        }
        localSQLiteCursor.dispose();
      }
      catch (Exception localException2)
      {
        FileLog.e("tmessages", localException2);
        try
        {
          this.database.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
          this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
        }
        catch (Exception localException3)
        {
          FileLog.e("tmessages", localException3);
        }
        continue;
      }
      i = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
      if (i < 36) {
        updateDbToLastVersion(i);
      }
    }
  }
  
  public void overwriteChannel(final int paramInt1, final TLRPC.TL_updates_channelDifferenceTooLong paramTL_updates_channelDifferenceTooLong, final int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        int j = 0;
        try
        {
          final long l = -paramInt1;
          if (paramInt2 != 0)
          {
            localObject = MessagesStorage.this.database.queryFinalized("SELECT pts FROM dialogs WHERE did = " + l, new Object[0]);
            i = j;
            if (!((SQLiteCursor)localObject).next()) {
              i = 1;
            }
            ((SQLiteCursor)localObject).dispose();
          }
          MessagesStorage.this.database.executeFast("DELETE FROM messages WHERE uid = " + l).stepThis().dispose();
          MessagesStorage.this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + l).stepThis().dispose();
          MessagesStorage.this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + l).stepThis().dispose();
          MessagesStorage.this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + l).stepThis().dispose();
          MessagesStorage.this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + l).stepThis().dispose();
          MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + l).stepThis().dispose();
          BotQuery.clearBotKeyboard(l, null);
          Object localObject = new TLRPC.TL_messages_dialogs();
          ((TLRPC.TL_messages_dialogs)localObject).chats.addAll(paramTL_updates_channelDifferenceTooLong.chats);
          ((TLRPC.TL_messages_dialogs)localObject).users.addAll(paramTL_updates_channelDifferenceTooLong.users);
          ((TLRPC.TL_messages_dialogs)localObject).messages.addAll(paramTL_updates_channelDifferenceTooLong.messages);
          TLRPC.TL_dialog localTL_dialog = new TLRPC.TL_dialog();
          localTL_dialog.id = l;
          localTL_dialog.flags = 1;
          localTL_dialog.peer = new TLRPC.TL_peerChannel();
          localTL_dialog.peer.channel_id = paramInt1;
          localTL_dialog.top_message = paramTL_updates_channelDifferenceTooLong.top_message;
          localTL_dialog.read_inbox_max_id = paramTL_updates_channelDifferenceTooLong.read_inbox_max_id;
          localTL_dialog.read_outbox_max_id = paramTL_updates_channelDifferenceTooLong.read_outbox_max_id;
          localTL_dialog.unread_count = paramTL_updates_channelDifferenceTooLong.unread_count;
          localTL_dialog.notify_settings = null;
          localTL_dialog.pts = paramTL_updates_channelDifferenceTooLong.pts;
          ((TLRPC.TL_messages_dialogs)localObject).dialogs.add(localTL_dialog);
          MessagesStorage.this.putDialogsInternal((TLRPC.messages_Dialogs)localObject);
          MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), false, paramInt1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, new Object[] { Long.valueOf(l), Boolean.valueOf(true) });
            }
          });
          if (i != 0)
          {
            if (paramInt2 == 1)
            {
              MessagesController.getInstance().checkChannelInviter(paramInt1);
              return;
            }
            MessagesController.getInstance().generateJoinMessage(paramInt1, false);
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
  
  public void processPendingRead(final long paramLong1, long paramLong2, final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.beginTransaction();
          Object localObject;
          if ((int)paramLong1 != 0)
          {
            localObject = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
            ((SQLitePreparedStatement)localObject).requery();
            ((SQLitePreparedStatement)localObject).bindLong(1, paramLong1);
            ((SQLitePreparedStatement)localObject).bindLong(2, paramInt);
            ((SQLitePreparedStatement)localObject).step();
            ((SQLitePreparedStatement)localObject).dispose();
          }
          for (;;)
          {
            int i = 0;
            localObject = MessagesStorage.this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + paramLong1, new Object[0]);
            if (((SQLiteCursor)localObject).next()) {
              i = ((SQLiteCursor)localObject).intValue(0);
            }
            ((SQLiteCursor)localObject).dispose();
            i = Math.max(i, (int)paramInt);
            localObject = MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0, inbox_max = ? WHERE did = ?");
            ((SQLitePreparedStatement)localObject).requery();
            ((SQLitePreparedStatement)localObject).bindInteger(1, i);
            ((SQLitePreparedStatement)localObject).bindLong(2, paramLong1);
            ((SQLitePreparedStatement)localObject).step();
            ((SQLitePreparedStatement)localObject).dispose();
            MessagesStorage.this.database.commitTransaction();
            return;
            localObject = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 0");
            ((SQLitePreparedStatement)localObject).requery();
            ((SQLitePreparedStatement)localObject).bindLong(1, paramLong1);
            ((SQLitePreparedStatement)localObject).bindInteger(2, this.val$max_date);
            ((SQLitePreparedStatement)localObject).step();
            ((SQLitePreparedStatement)localObject).dispose();
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void putBlockedUsers(final ArrayList<Integer> paramArrayList, final boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
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
          }
          localException.dispose();
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          return;
        }
        MessagesStorage.this.database.commitTransaction();
      }
    });
  }
  
  public void putCachedPhoneBook(final HashMap<Integer, ContactsController.Contact> paramHashMap)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement1 = MessagesStorage.this.database.executeFast("REPLACE INTO user_contacts_v6 VALUES(?, ?, ?)");
          SQLitePreparedStatement localSQLitePreparedStatement2 = MessagesStorage.this.database.executeFast("REPLACE INTO user_phones_v6 VALUES(?, ?, ?, ?)");
          Iterator localIterator = paramHashMap.entrySet().iterator();
          while (localIterator.hasNext())
          {
            ContactsController.Contact localContact = (ContactsController.Contact)((Map.Entry)localIterator.next()).getValue();
            if ((!localContact.phones.isEmpty()) && (!localContact.shortPhones.isEmpty()))
            {
              localSQLitePreparedStatement1.requery();
              localSQLitePreparedStatement1.bindInteger(1, localContact.id);
              localSQLitePreparedStatement1.bindString(2, localContact.first_name);
              localSQLitePreparedStatement1.bindString(3, localContact.last_name);
              localSQLitePreparedStatement1.step();
              int i = 0;
              while (i < localContact.phones.size())
              {
                localSQLitePreparedStatement2.requery();
                localSQLitePreparedStatement2.bindInteger(1, localContact.id);
                localSQLitePreparedStatement2.bindString(2, (String)localContact.phones.get(i));
                localSQLitePreparedStatement2.bindString(3, (String)localContact.shortPhones.get(i));
                localSQLitePreparedStatement2.bindInteger(4, ((Integer)localContact.phoneDeleted.get(i)).intValue());
                localSQLitePreparedStatement2.step();
                i += 1;
              }
            }
          }
          localSQLitePreparedStatement1.dispose();
          localSQLitePreparedStatement2.dispose();
          MessagesStorage.this.database.commitTransaction();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void putChannelViews(final SparseArray<SparseIntArray> paramSparseArray, final boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          int i;
          try
          {
            MessagesStorage.this.database.beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE messages SET media = max((SELECT media FROM messages WHERE mid = ?), ?) WHERE mid = ?");
            i = 0;
            if (i < paramSparseArray.size())
            {
              int k = paramSparseArray.keyAt(i);
              SparseIntArray localSparseIntArray = (SparseIntArray)paramSparseArray.get(k);
              int j = 0;
              if (j < localSparseIntArray.size())
              {
                int m = localSparseIntArray.get(localSparseIntArray.keyAt(j));
                long l2 = localSparseIntArray.keyAt(j);
                long l1 = l2;
                if (paramBoolean) {
                  l1 = l2 | -k << 32;
                }
                localSQLitePreparedStatement.requery();
                localSQLitePreparedStatement.bindLong(1, l1);
                localSQLitePreparedStatement.bindInteger(2, m);
                localSQLitePreparedStatement.bindLong(3, l1);
                localSQLitePreparedStatement.step();
                j += 1;
                continue;
              }
            }
            else
            {
              localSQLitePreparedStatement.dispose();
              MessagesStorage.this.database.commitTransaction();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          i += 1;
        }
      }
    });
  }
  
  public void putContacts(final ArrayList<TLRPC.TL_contact> paramArrayList, final boolean paramBoolean)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    paramArrayList = new ArrayList(paramArrayList);
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
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
              if (localTL_contact.mutual)
              {
                j = 1;
                localSQLitePreparedStatement.bindInteger(2, j);
                localSQLitePreparedStatement.step();
                i += 1;
              }
            }
            else
            {
              localSQLitePreparedStatement.dispose();
              MessagesStorage.this.database.commitTransaction();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          int j = 0;
        }
      }
    });
  }
  
  public void putDialogPhotos(final int paramInt, final TLRPC.photos_Photos paramphotos_Photos)
  {
    if ((paramphotos_Photos == null) || (paramphotos_Photos.photos.isEmpty())) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
          Iterator localIterator = paramphotos_Photos.photos.iterator();
          while (localIterator.hasNext())
          {
            TLRPC.Photo localPhoto = (TLRPC.Photo)localIterator.next();
            if (!(localPhoto instanceof TLRPC.TL_photoEmpty))
            {
              localSQLitePreparedStatement.requery();
              NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localPhoto.getObjectSize());
              localPhoto.serializeToStream(localNativeByteBuffer);
              localSQLitePreparedStatement.bindInteger(1, paramInt);
              localSQLitePreparedStatement.bindLong(2, localPhoto.id);
              localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
              localSQLitePreparedStatement.step();
              localNativeByteBuffer.reuse();
            }
          }
          localException.dispose();
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          return;
        }
      }
    });
  }
  
  public void putDialogs(final TLRPC.messages_Dialogs parammessages_Dialogs)
  {
    if (parammessages_Dialogs.dialogs.isEmpty()) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesStorage.this.putDialogsInternal(parammessages_Dialogs);
        MessagesStorage.this.loadUnreadMessages();
      }
    });
  }
  
  public void putEncryptedChat(final TLRPC.EncryptedChat paramEncryptedChat, final TLRPC.User paramUser, final TLRPC.TL_dialog paramTL_dialog)
  {
    if (paramEncryptedChat == null) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int j = 1;
        try
        {
          if (((paramEncryptedChat.key_hash == null) || (paramEncryptedChat.key_hash.length < 16)) && (paramEncryptedChat.auth_key != null)) {
            paramEncryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(paramEncryptedChat.auth_key);
          }
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
          NativeByteBuffer localNativeByteBuffer1 = new NativeByteBuffer(paramEncryptedChat.getObjectSize());
          NativeByteBuffer localNativeByteBuffer2;
          label129:
          NativeByteBuffer localNativeByteBuffer3;
          if (paramEncryptedChat.a_or_b != null)
          {
            i = paramEncryptedChat.a_or_b.length;
            localNativeByteBuffer2 = new NativeByteBuffer(i);
            if (paramEncryptedChat.auth_key == null) {
              break label665;
            }
            i = paramEncryptedChat.auth_key.length;
            localNativeByteBuffer3 = new NativeByteBuffer(i);
            if (paramEncryptedChat.future_auth_key == null) {
              break label670;
            }
          }
          label665:
          label670:
          for (int i = paramEncryptedChat.future_auth_key.length;; i = 1)
          {
            NativeByteBuffer localNativeByteBuffer4 = new NativeByteBuffer(i);
            i = j;
            if (paramEncryptedChat.key_hash != null) {
              i = paramEncryptedChat.key_hash.length;
            }
            NativeByteBuffer localNativeByteBuffer5 = new NativeByteBuffer(i);
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
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            localNativeByteBuffer1.reuse();
            localNativeByteBuffer2.reuse();
            localNativeByteBuffer3.reuse();
            localNativeByteBuffer4.reuse();
            localNativeByteBuffer5.reuse();
            if (paramTL_dialog != null)
            {
              localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
              localSQLitePreparedStatement.bindLong(1, paramTL_dialog.id);
              localSQLitePreparedStatement.bindInteger(2, paramTL_dialog.last_message_date);
              localSQLitePreparedStatement.bindInteger(3, paramTL_dialog.unread_count);
              localSQLitePreparedStatement.bindInteger(4, paramTL_dialog.top_message);
              localSQLitePreparedStatement.bindInteger(5, paramTL_dialog.read_inbox_max_id);
              localSQLitePreparedStatement.bindInteger(6, paramTL_dialog.read_outbox_max_id);
              localSQLitePreparedStatement.bindInteger(7, 0);
              localSQLitePreparedStatement.bindInteger(8, 0);
              localSQLitePreparedStatement.bindInteger(9, paramTL_dialog.pts);
              localSQLitePreparedStatement.bindInteger(10, 0);
              localSQLitePreparedStatement.step();
              localSQLitePreparedStatement.dispose();
            }
            return;
            i = 1;
            break;
            i = 1;
            break label129;
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void putMessages(ArrayList<TLRPC.Message> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    putMessages(paramArrayList, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, false);
  }
  
  public void putMessages(final ArrayList<TLRPC.Message> paramArrayList, final boolean paramBoolean1, boolean paramBoolean2, final boolean paramBoolean3, final int paramInt, final boolean paramBoolean4)
  {
    if (paramArrayList.size() == 0) {
      return;
    }
    if (paramBoolean2)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.putMessagesInternal(paramArrayList, paramBoolean1, paramBoolean3, paramInt, paramBoolean4);
        }
      });
      return;
    }
    putMessagesInternal(paramArrayList, paramBoolean1, paramBoolean3, paramInt, paramBoolean4);
  }
  
  public void putMessages(final TLRPC.messages_Messages parammessages_Messages, final long paramLong, final int paramInt1, int paramInt2, final boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        SQLitePreparedStatement localSQLitePreparedStatement1;
        SQLitePreparedStatement localSQLitePreparedStatement2;
        Object localObject4;
        TLRPC.Message localMessage1;
        int j;
        long l1;
        Object localObject5;
        do
        {
          try
          {
            if (parammessages_Messages.messages.isEmpty())
            {
              if (paramInt1 != 0) {
                break label1200;
              }
              MessagesStorage.this.doneHolesInTable("messages_holes", paramLong, paramBoolean);
              MessagesStorage.this.doneHolesInMedia(paramLong, paramBoolean, -1);
              return;
            }
            MessagesStorage.this.database.beginTransaction();
            if (paramInt1 == 0)
            {
              i = ((TLRPC.Message)parammessages_Messages.messages.get(parammessages_Messages.messages.size() - 1)).id;
              MessagesStorage.this.closeHolesInTable("messages_holes", paramLong, i, paramBoolean);
              MessagesStorage.this.closeHolesInMedia(paramLong, i, paramBoolean, -1);
            }
            for (;;)
            {
              int m = parammessages_Messages.messages.size();
              localSQLitePreparedStatement1 = MessagesStorage.this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)");
              localSQLitePreparedStatement2 = MessagesStorage.this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
              Object localObject1 = null;
              localObject4 = null;
              k = 0;
              i = 0;
              if (i >= m) {
                break label1106;
              }
              localMessage1 = (TLRPC.Message)parammessages_Messages.messages.get(i);
              long l2 = localMessage1.id;
              j = k;
              if (k == 0) {
                j = localMessage1.to_id.channel_id;
              }
              l1 = l2;
              if (localMessage1.to_id.channel_id != 0) {
                l1 = l2 | j << 32;
              }
              if (paramInt1 != -2) {
                break label569;
              }
              localObject3 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data, ttl FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l1) }), new Object[0]);
              boolean bool = ((SQLiteCursor)localObject3).next();
              if (bool)
              {
                localObject5 = ((SQLiteCursor)localObject3).byteBufferValue(1);
                if (localObject5 != null)
                {
                  TLRPC.Message localMessage2 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject5, ((NativeByteBuffer)localObject5).readInt32(false), false);
                  ((NativeByteBuffer)localObject5).reuse();
                  if (localMessage2 != null)
                  {
                    localMessage1.attachPath = localMessage2.attachPath;
                    localMessage1.ttl = ((SQLiteCursor)localObject3).intValue(2);
                  }
                }
              }
              ((SQLiteCursor)localObject3).dispose();
              if (bool) {
                break label569;
              }
              localObject5 = localObject4;
              break label1201;
              if (paramInt1 != 1) {
                break;
              }
              i = ((TLRPC.Message)parammessages_Messages.messages.get(0)).id;
              MessagesStorage.this.closeHolesInTable("messages_holes", paramLong, paramBoolean, i);
              MessagesStorage.this.closeHolesInMedia(paramLong, paramBoolean, i, -1);
            }
            if (paramInt1 == 3) {
              break;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
        } while (paramInt1 != 2);
        if (paramBoolean == 0) {}
        for (int i = Integer.MAX_VALUE;; i = ((TLRPC.Message)parammessages_Messages.messages.get(0)).id)
        {
          j = ((TLRPC.Message)parammessages_Messages.messages.get(parammessages_Messages.messages.size() - 1)).id;
          MessagesStorage.this.closeHolesInTable("messages_holes", paramLong, j, i);
          MessagesStorage.this.closeHolesInMedia(paramLong, j, i, -1);
          break;
        }
        label569:
        if ((i == 0) && (this.val$createDialog))
        {
          localObject3 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
          ((SQLitePreparedStatement)localObject3).bindLong(1, paramLong);
          ((SQLitePreparedStatement)localObject3).bindInteger(2, localMessage1.date);
          ((SQLitePreparedStatement)localObject3).bindInteger(3, 0);
          ((SQLitePreparedStatement)localObject3).bindLong(4, l1);
          ((SQLitePreparedStatement)localObject3).bindInteger(5, localMessage1.id);
          ((SQLitePreparedStatement)localObject3).bindInteger(6, 0);
          ((SQLitePreparedStatement)localObject3).bindLong(7, l1);
          ((SQLitePreparedStatement)localObject3).bindInteger(8, localMessage1.ttl);
          ((SQLitePreparedStatement)localObject3).bindInteger(9, parammessages_Messages.pts);
          ((SQLitePreparedStatement)localObject3).bindInteger(10, localMessage1.date);
          ((SQLitePreparedStatement)localObject3).step();
          ((SQLitePreparedStatement)localObject3).dispose();
        }
        MessagesStorage.this.fixUnsupportedMedia(localMessage1);
        localSQLitePreparedStatement1.requery();
        Object localObject3 = new NativeByteBuffer(localMessage1.getObjectSize());
        localMessage1.serializeToStream((AbstractSerializedData)localObject3);
        localSQLitePreparedStatement1.bindLong(1, l1);
        localSQLitePreparedStatement1.bindLong(2, paramLong);
        localSQLitePreparedStatement1.bindInteger(3, MessageObject.getUnreadFlags(localMessage1));
        localSQLitePreparedStatement1.bindInteger(4, localMessage1.send_state);
        localSQLitePreparedStatement1.bindInteger(5, localMessage1.date);
        localSQLitePreparedStatement1.bindByteBuffer(6, (NativeByteBuffer)localObject3);
        if (MessageObject.isOut(localMessage1)) {}
        for (int k = 1;; k = 0)
        {
          localSQLitePreparedStatement1.bindInteger(7, k);
          localSQLitePreparedStatement1.bindInteger(8, 0);
          if ((localMessage1.flags & 0x400) != 0) {
            localSQLitePreparedStatement1.bindInteger(9, localMessage1.views);
          }
          Object localObject2;
          for (;;)
          {
            localSQLitePreparedStatement1.bindInteger(10, 0);
            localSQLitePreparedStatement1.step();
            if (SharedMediaQuery.canAddMessageToMedia(localMessage1))
            {
              localSQLitePreparedStatement2.requery();
              localSQLitePreparedStatement2.bindLong(1, l1);
              localSQLitePreparedStatement2.bindLong(2, paramLong);
              localSQLitePreparedStatement2.bindInteger(3, localMessage1.date);
              localSQLitePreparedStatement2.bindInteger(4, SharedMediaQuery.getMediaType(localMessage1));
              localSQLitePreparedStatement2.bindByteBuffer(5, (NativeByteBuffer)localObject3);
              localSQLitePreparedStatement2.step();
            }
            ((NativeByteBuffer)localObject3).reuse();
            localObject3 = localException;
            if ((localMessage1.media instanceof TLRPC.TL_messageMediaWebPage))
            {
              localObject3 = localException;
              if ((localMessage1.media.webpage instanceof TLRPC.TL_webPagePending))
              {
                localObject3 = localException;
                if (localException == null) {
                  localObject3 = MessagesStorage.this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
                }
                ((SQLitePreparedStatement)localObject3).requery();
                ((SQLitePreparedStatement)localObject3).bindLong(1, localMessage1.media.webpage.id);
                ((SQLitePreparedStatement)localObject3).bindLong(2, l1);
                ((SQLitePreparedStatement)localObject3).step();
              }
            }
            localObject5 = localObject4;
            localObject2 = localObject3;
            if (paramInt1 != 0) {
              break;
            }
            localObject5 = localObject4;
            localObject2 = localObject3;
            if (!MessagesStorage.this.isValidKeyboardToSave(localMessage1)) {
              break;
            }
            if (localObject4 == null) {
              break label1214;
            }
            localObject5 = localObject4;
            localObject2 = localObject3;
            if (((TLRPC.Message)localObject4).id >= localMessage1.id) {
              break;
            }
            break label1214;
            localSQLitePreparedStatement1.bindInteger(9, 0);
          }
          label1106:
          localSQLitePreparedStatement1.dispose();
          localSQLitePreparedStatement2.dispose();
          if (localObject2 != null) {
            ((SQLitePreparedStatement)localObject2).dispose();
          }
          if (localObject4 != null) {
            BotQuery.putBotKeyboard(paramLong, (TLRPC.Message)localObject4);
          }
          MessagesStorage.this.putUsersInternal(parammessages_Messages.users);
          MessagesStorage.this.putChatsInternal(parammessages_Messages.chats);
          MessagesStorage.this.database.commitTransaction();
          if (this.val$createDialog) {
            MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), false, k);
          }
          label1200:
          return;
          for (;;)
          {
            label1201:
            i += 1;
            localObject4 = localObject5;
            k = j;
            break;
            label1214:
            localObject5 = localMessage1;
            localObject2 = localObject3;
          }
        }
      }
    });
  }
  
  public void putSentFile(final String paramString, final TLObject paramTLObject, final int paramInt)
  {
    if ((paramString == null) || (paramTLObject == null)) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        localSQLitePreparedStatement = null;
        localNativeByteBuffer = null;
        localObject5 = null;
        localObject2 = localObject5;
        localObject1 = localNativeByteBuffer;
        for (;;)
        {
          try
          {
            str = Utilities.MD5(paramString);
            if (str == null) {
              continue;
            }
            localObject4 = null;
            localObject2 = localObject5;
            localObject1 = localNativeByteBuffer;
            if (!(paramTLObject instanceof TLRPC.Photo)) {
              continue;
            }
            localObject2 = localObject5;
            localObject1 = localNativeByteBuffer;
            localObject4 = new TLRPC.TL_messageMediaPhoto();
            localObject2 = localObject5;
            localObject1 = localNativeByteBuffer;
            ((TLRPC.MessageMedia)localObject4).caption = "";
            localObject2 = localObject5;
            localObject1 = localNativeByteBuffer;
            ((TLRPC.MessageMedia)localObject4).photo = ((TLRPC.Photo)paramTLObject);
            if (localObject4 != null) {
              continue;
            }
            if (0 != 0) {
              throw new NullPointerException();
            }
          }
          catch (Exception localException)
          {
            String str;
            Object localObject4;
            localObject1 = localObject2;
            FileLog.e("tmessages", localException);
            return;
            localObject2 = localObject5;
            localObject1 = localNativeByteBuffer;
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO sent_files_v2 VALUES(?, ?, ?)");
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.requery();
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localNativeByteBuffer = new NativeByteBuffer(localException.getObjectSize());
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localException.serializeToStream(localNativeByteBuffer);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindString(1, str);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindInteger(2, paramInt);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localSQLitePreparedStatement.step();
            localObject2 = localSQLitePreparedStatement;
            localObject1 = localSQLitePreparedStatement;
            localNativeByteBuffer.reuse();
            if (localSQLitePreparedStatement == null) {
              continue;
            }
            localSQLitePreparedStatement.dispose();
            return;
          }
          finally
          {
            if (localObject1 == null) {
              continue;
            }
            ((SQLitePreparedStatement)localObject1).dispose();
          }
          return;
          localObject2 = localObject5;
          localObject1 = localNativeByteBuffer;
          if ((paramTLObject instanceof TLRPC.Document))
          {
            localObject2 = localObject5;
            localObject1 = localNativeByteBuffer;
            localObject4 = new TLRPC.TL_messageMediaDocument();
            localObject2 = localObject5;
            localObject1 = localNativeByteBuffer;
            ((TLRPC.MessageMedia)localObject4).caption = "";
            localObject2 = localObject5;
            localObject1 = localNativeByteBuffer;
            ((TLRPC.MessageMedia)localObject4).document = ((TLRPC.Document)paramTLObject);
          }
        }
      }
    });
  }
  
  public void putUsersAndChats(final ArrayList<TLRPC.User> paramArrayList, final ArrayList<TLRPC.Chat> paramArrayList1, final boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramArrayList != null) && (paramArrayList.isEmpty()) && (paramArrayList1 != null) && (paramArrayList1.isEmpty())) {
      return;
    }
    if (paramBoolean2)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.putUsersAndChatsInternal(paramArrayList, paramArrayList1, paramBoolean1);
        }
      });
      return;
    }
    putUsersAndChatsInternal(paramArrayList, paramArrayList1, paramBoolean1);
  }
  
  public void putWallpapers(final ArrayList<TLRPC.WallPaper> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 0;
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM wallpapers WHERE 1").stepThis().dispose();
          MessagesStorage.this.database.beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO wallpapers VALUES(?, ?)");
          Iterator localIterator = paramArrayList.iterator();
          while (localIterator.hasNext())
          {
            TLRPC.WallPaper localWallPaper = (TLRPC.WallPaper)localIterator.next();
            localSQLitePreparedStatement.requery();
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localWallPaper.getObjectSize());
            localWallPaper.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindInteger(1, i);
            localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer);
            localSQLitePreparedStatement.step();
            i += 1;
            localNativeByteBuffer.reuse();
          }
          localException.dispose();
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          return;
        }
        MessagesStorage.this.database.commitTransaction();
      }
    });
  }
  
  public void putWebPages(final HashMap<Long, TLRPC.WebPage> paramHashMap)
  {
    if ((paramHashMap == null) || (paramHashMap.isEmpty())) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final ArrayList localArrayList;
        Object localObject3;
        Object localObject4;
        do
        {
          do
          {
            try
            {
              localObject1 = TextUtils.join(",", paramHashMap.keySet());
              SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM webpage_pending WHERE id IN (%s)", new Object[] { localObject1 }), new Object[0]);
              localObject2 = new ArrayList();
              while (localSQLiteCursor.next()) {
                ((ArrayList)localObject2).add(Long.valueOf(localSQLiteCursor.longValue(0)));
              }
              localException.dispose();
            }
            catch (Exception localException)
            {
              FileLog.e("tmessages", localException);
              return;
            }
          } while (((ArrayList)localObject2).isEmpty());
          localArrayList = new ArrayList();
          localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data FROM messages WHERE mid IN (%s)", new Object[] { TextUtils.join(",", (Iterable)localObject2) }), new Object[0]);
          while (((SQLiteCursor)localObject2).next())
          {
            i = ((SQLiteCursor)localObject2).intValue(0);
            localObject3 = ((SQLiteCursor)localObject2).byteBufferValue(1);
            if (localObject3 != null)
            {
              localObject4 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
              ((NativeByteBuffer)localObject3).reuse();
              if ((((TLRPC.Message)localObject4).media instanceof TLRPC.TL_messageMediaWebPage))
              {
                ((TLRPC.Message)localObject4).id = i;
                ((TLRPC.Message)localObject4).media.webpage = ((TLRPC.WebPage)paramHashMap.get(Long.valueOf(((TLRPC.Message)localObject4).media.webpage.id)));
                localArrayList.add(localObject4);
              }
            }
          }
          ((SQLiteCursor)localObject2).dispose();
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM webpage_pending WHERE id IN (%s)", new Object[] { localObject1 })).stepThis().dispose();
        } while (localArrayList.isEmpty());
        MessagesStorage.this.database.beginTransaction();
        Object localObject1 = MessagesStorage.this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
        Object localObject2 = MessagesStorage.this.database.executeFast("UPDATE media_v2 SET data = ? WHERE mid = ?");
        int i = 0;
        while (i < localArrayList.size())
        {
          localObject3 = (TLRPC.Message)localArrayList.get(i);
          localObject4 = new NativeByteBuffer(((TLRPC.Message)localObject3).getObjectSize());
          ((TLRPC.Message)localObject3).serializeToStream((AbstractSerializedData)localObject4);
          long l2 = ((TLRPC.Message)localObject3).id;
          long l1 = l2;
          if (((TLRPC.Message)localObject3).to_id.channel_id != 0) {
            l1 = l2 | ((TLRPC.Message)localObject3).to_id.channel_id << 32;
          }
          ((SQLitePreparedStatement)localObject1).requery();
          ((SQLitePreparedStatement)localObject1).bindByteBuffer(1, (NativeByteBuffer)localObject4);
          ((SQLitePreparedStatement)localObject1).bindLong(2, l1);
          ((SQLitePreparedStatement)localObject1).step();
          ((SQLitePreparedStatement)localObject2).requery();
          ((SQLitePreparedStatement)localObject2).bindByteBuffer(1, (NativeByteBuffer)localObject4);
          ((SQLitePreparedStatement)localObject2).bindLong(2, l1);
          ((SQLitePreparedStatement)localObject2).step();
          ((NativeByteBuffer)localObject4).reuse();
          i += 1;
        }
        ((SQLitePreparedStatement)localObject1).dispose();
        ((SQLitePreparedStatement)localObject2).dispose();
        MessagesStorage.this.database.commitTransaction();
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceivedWebpages, new Object[] { localArrayList });
          }
        });
      }
    });
  }
  
  public void putWebRecent(final ArrayList<MediaController.SearchImage> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (;;)
        {
          int i;
          try
          {
            MessagesStorage.this.database.beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            i = 0;
            if ((i >= paramArrayList.size()) || (i == 200))
            {
              localSQLitePreparedStatement.dispose();
              MessagesStorage.this.database.commitTransaction();
              if (paramArrayList.size() >= 200)
              {
                MessagesStorage.this.database.beginTransaction();
                i = 200;
                if (i >= paramArrayList.size()) {
                  break label371;
                }
                MessagesStorage.this.database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((MediaController.SearchImage)paramArrayList.get(i)).id + "'").stepThis().dispose();
                i += 1;
                continue;
              }
            }
            else
            {
              MediaController.SearchImage localSearchImage = (MediaController.SearchImage)paramArrayList.get(i);
              localSQLitePreparedStatement.requery();
              localSQLitePreparedStatement.bindString(1, localSearchImage.id);
              localSQLitePreparedStatement.bindInteger(2, localSearchImage.type);
              if (localSearchImage.imageUrl == null) {
                break label389;
              }
              Object localObject = localSearchImage.imageUrl;
              localSQLitePreparedStatement.bindString(3, (String)localObject);
              if (localSearchImage.thumbUrl == null) {
                break label395;
              }
              localObject = localSearchImage.thumbUrl;
              localSQLitePreparedStatement.bindString(4, (String)localObject);
              if (localSearchImage.localUrl == null) {
                break label401;
              }
              localObject = localSearchImage.localUrl;
              localSQLitePreparedStatement.bindString(5, (String)localObject);
              localSQLitePreparedStatement.bindInteger(6, localSearchImage.width);
              localSQLitePreparedStatement.bindInteger(7, localSearchImage.height);
              localSQLitePreparedStatement.bindInteger(8, localSearchImage.size);
              localSQLitePreparedStatement.bindInteger(9, localSearchImage.date);
              localObject = null;
              if (localSearchImage.document != null)
              {
                localObject = new NativeByteBuffer(localSearchImage.document.getObjectSize());
                localSearchImage.document.serializeToStream((AbstractSerializedData)localObject);
                localSQLitePreparedStatement.bindByteBuffer(10, (NativeByteBuffer)localObject);
                localSQLitePreparedStatement.step();
                if (localObject == null) {
                  break label382;
                }
                ((NativeByteBuffer)localObject).reuse();
                break label382;
              }
              localSQLitePreparedStatement.bindNull(10);
              continue;
            }
            return;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
          }
          label371:
          MessagesStorage.this.database.commitTransaction();
          return;
          label382:
          i += 1;
          continue;
          label389:
          String str = "";
          continue;
          label395:
          str = "";
          continue;
          label401:
          str = "";
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
          }
          else
          {
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt) })).stepThis().dispose();
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
  
  public void removePendingTask(final long paramLong)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM pending_tasks WHERE id = " + paramLong).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
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
          FileLog.e("tmessages", localException);
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
        try
        {
          if ((MessagesStorage.this.lastSavedSeq == paramInt1) && (MessagesStorage.this.lastSavedPts == paramInt2) && (MessagesStorage.this.lastSavedDate == paramInt3) && (MessagesStorage.lastQtsValue == paramInt4)) {
            return;
          }
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
          localSQLitePreparedStatement.bindInteger(1, paramInt1);
          localSQLitePreparedStatement.bindInteger(2, paramInt2);
          localSQLitePreparedStatement.bindInteger(3, paramInt3);
          localSQLitePreparedStatement.bindInteger(4, paramInt4);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          MessagesStorage.access$202(MessagesStorage.this, paramInt1);
          MessagesStorage.access$302(MessagesStorage.this, paramInt2);
          MessagesStorage.access$402(MessagesStorage.this, paramInt3);
          MessagesStorage.access$502(MessagesStorage.this, paramInt4);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
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
          if (paramArrayOfByte != null) {
            i = paramArrayOfByte.length;
          }
          NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(i);
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
          FileLog.e("tmessages", localException);
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
          FileLog.e("tmessages", localException);
        }
      }
    });
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
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void startTransaction(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            MessagesStorage.this.database.beginTransaction();
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
    try
    {
      this.database.beginTransaction();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void updateChannelPinnedMessage(final int paramInt1, final int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          Object localObject2 = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + paramInt1, new Object[0]);
          SQLitePreparedStatement localSQLitePreparedStatement = null;
          new ArrayList();
          final Object localObject1 = localSQLitePreparedStatement;
          if (((SQLiteCursor)localObject2).next())
          {
            NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject2).byteBufferValue(0);
            localObject1 = localSQLitePreparedStatement;
            if (localNativeByteBuffer != null)
            {
              localObject1 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              ((TLRPC.ChatFull)localObject1).pinned_msg_id = ((SQLiteCursor)localObject2).intValue(1);
            }
          }
          ((SQLiteCursor)localObject2).dispose();
          if ((localObject1 instanceof TLRPC.TL_channelFull))
          {
            ((TLRPC.ChatFull)localObject1).pinned_msg_id = paramInt2;
            ((TLRPC.ChatFull)localObject1).flags |= 0x20;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { localObject1, Integer.valueOf(0), Boolean.valueOf(false), null });
              }
            });
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
            localObject2 = new NativeByteBuffer(((TLRPC.ChatFull)localObject1).getObjectSize());
            ((TLRPC.ChatFull)localObject1).serializeToStream((AbstractSerializedData)localObject2);
            localSQLitePreparedStatement.bindInteger(1, paramInt1);
            localSQLitePreparedStatement.bindByteBuffer(2, (NativeByteBuffer)localObject2);
            localSQLitePreparedStatement.bindInteger(3, ((TLRPC.ChatFull)localObject1).pinned_msg_id);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            ((NativeByteBuffer)localObject2).reuse();
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
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
          MessagesStorage.this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + l).stepThis().dispose();
          MessagesStorage.this.database.beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
          int j = (int)(System.currentTimeMillis() / 1000L);
          int i = 0;
          while (i < paramArrayList.size())
          {
            TLRPC.ChannelParticipant localChannelParticipant = (TLRPC.ChannelParticipant)paramArrayList.get(i);
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindLong(1, l);
            localSQLitePreparedStatement.bindInteger(2, localChannelParticipant.user_id);
            localSQLitePreparedStatement.bindInteger(3, j);
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localChannelParticipant.getObjectSize());
            localChannelParticipant.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindByteBuffer(4, localNativeByteBuffer);
            localNativeByteBuffer.reuse();
            localSQLitePreparedStatement.step();
            j -= 1;
            i += 1;
          }
          localSQLitePreparedStatement.dispose();
          MessagesStorage.this.database.commitTransaction();
          MessagesStorage.this.loadChatInfo(paramInt, null, false, true);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
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
        for (;;)
        {
          Object localObject3;
          Object localObject2;
          try
          {
            localObject3 = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + paramInt1, new Object[0]);
            localObject2 = null;
            new ArrayList();
            final Object localObject1 = localObject2;
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
              break label528;
            }
            if (paramInt3 == 1)
            {
              i = 0;
              if (i < ((TLRPC.ChatFull)localObject1).participants.participants.size())
              {
                if (((TLRPC.ChatParticipant)((TLRPC.ChatFull)localObject1).participants.participants.get(i)).user_id != paramInt2) {
                  break label529;
                }
                ((TLRPC.ChatFull)localObject1).participants.participants.remove(i);
              }
              ((TLRPC.ChatFull)localObject1).participants.version = paramInt5;
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { localObject1, Integer.valueOf(0), Boolean.valueOf(false), null });
                }
              });
              localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
              localObject3 = new NativeByteBuffer(((TLRPC.ChatFull)localObject1).getObjectSize());
              ((TLRPC.ChatFull)localObject1).serializeToStream((AbstractSerializedData)localObject3);
              ((SQLitePreparedStatement)localObject2).bindInteger(1, paramInt1);
              ((SQLitePreparedStatement)localObject2).bindByteBuffer(2, (NativeByteBuffer)localObject3);
              ((SQLitePreparedStatement)localObject2).bindInteger(3, ((TLRPC.ChatFull)localObject1).pinned_msg_id);
              ((SQLitePreparedStatement)localObject2).step();
              ((SQLitePreparedStatement)localObject2).dispose();
              ((NativeByteBuffer)localObject3).reuse();
              return;
            }
            if (paramInt3 == 0)
            {
              localObject2 = ((TLRPC.ChatFull)localObject1).participants.participants.iterator();
              if (((Iterator)localObject2).hasNext())
              {
                if (((TLRPC.ChatParticipant)((Iterator)localObject2).next()).user_id != paramInt2) {
                  continue;
                }
                return;
              }
              localObject2 = new TLRPC.TL_chatParticipant();
              ((TLRPC.TL_chatParticipant)localObject2).user_id = paramInt2;
              ((TLRPC.TL_chatParticipant)localObject2).inviter_id = paramInt4;
              ((TLRPC.TL_chatParticipant)localObject2).date = ConnectionsManager.getInstance().getCurrentTime();
              ((TLRPC.ChatFull)localObject1).participants.participants.add(localObject2);
              continue;
            }
            if (paramInt3 != 2) {
              continue;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          int i = 0;
          while (i < localException.participants.participants.size())
          {
            localObject3 = (TLRPC.ChatParticipant)localException.participants.participants.get(i);
            if (((TLRPC.ChatParticipant)localObject3).user_id == paramInt2)
            {
              if (paramInt4 == 1)
              {
                localObject2 = new TLRPC.TL_chatParticipantAdmin();
                ((TLRPC.ChatParticipant)localObject2).user_id = ((TLRPC.ChatParticipant)localObject3).user_id;
                ((TLRPC.ChatParticipant)localObject2).date = ((TLRPC.ChatParticipant)localObject3).date;
              }
              for (((TLRPC.ChatParticipant)localObject2).inviter_id = ((TLRPC.ChatParticipant)localObject3).inviter_id;; ((TLRPC.ChatParticipant)localObject2).inviter_id = ((TLRPC.ChatParticipant)localObject3).inviter_id)
              {
                localException.participants.participants.set(i, localObject2);
                break;
                localObject2 = new TLRPC.TL_chatParticipant();
                ((TLRPC.ChatParticipant)localObject2).user_id = ((TLRPC.ChatParticipant)localObject3).user_id;
                ((TLRPC.ChatParticipant)localObject2).date = ((TLRPC.ChatParticipant)localObject3).date;
              }
            }
            i += 1;
          }
          label528:
          return;
          label529:
          i += 1;
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
        try
        {
          if (paramBoolean)
          {
            localObject1 = MessagesStorage.this.database.queryFinalized("SELECT uid FROM chat_settings_v2 WHERE uid = " + paramChatFull.id, new Object[0]);
            boolean bool = ((SQLiteCursor)localObject1).next();
            ((SQLiteCursor)localObject1).dispose();
            if (!bool) {
              return;
            }
          }
          Object localObject1 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
          Object localObject2 = new NativeByteBuffer(paramChatFull.getObjectSize());
          paramChatFull.serializeToStream((AbstractSerializedData)localObject2);
          ((SQLitePreparedStatement)localObject1).bindInteger(1, paramChatFull.id);
          ((SQLitePreparedStatement)localObject1).bindByteBuffer(2, (NativeByteBuffer)localObject2);
          ((SQLitePreparedStatement)localObject1).bindInteger(3, paramChatFull.pinned_msg_id);
          ((SQLitePreparedStatement)localObject1).step();
          ((SQLitePreparedStatement)localObject1).dispose();
          ((NativeByteBuffer)localObject2).reuse();
          if ((paramChatFull instanceof TLRPC.TL_channelFull))
          {
            localObject1 = MessagesStorage.this.database.queryFinalized("SELECT date, pts, last_mid, inbox_max, outbox_max FROM dialogs WHERE did = " + -paramChatFull.id, new Object[0]);
            if (((SQLiteCursor)localObject1).next())
            {
              int i = ((SQLiteCursor)localObject1).intValue(3);
              if (i <= paramChatFull.read_inbox_max_id)
              {
                i = paramChatFull.read_inbox_max_id - i;
                if (i < paramChatFull.unread_count) {
                  paramChatFull.unread_count = i;
                }
                i = ((SQLiteCursor)localObject1).intValue(0);
                int j = ((SQLiteCursor)localObject1).intValue(1);
                long l = ((SQLiteCursor)localObject1).longValue(2);
                int k = ((SQLiteCursor)localObject1).intValue(4);
                localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                ((SQLitePreparedStatement)localObject2).bindLong(1, -paramChatFull.id);
                ((SQLitePreparedStatement)localObject2).bindInteger(2, i);
                ((SQLitePreparedStatement)localObject2).bindInteger(3, paramChatFull.unread_count);
                ((SQLitePreparedStatement)localObject2).bindLong(4, l);
                ((SQLitePreparedStatement)localObject2).bindInteger(5, paramChatFull.read_inbox_max_id);
                ((SQLitePreparedStatement)localObject2).bindInteger(6, Math.max(k, paramChatFull.read_outbox_max_id));
                ((SQLitePreparedStatement)localObject2).bindLong(7, 0L);
                ((SQLitePreparedStatement)localObject2).bindInteger(8, 0);
                ((SQLitePreparedStatement)localObject2).bindInteger(9, j);
                ((SQLitePreparedStatement)localObject2).bindInteger(10, 0);
                ((SQLitePreparedStatement)localObject2).step();
                ((SQLitePreparedStatement)localObject2).dispose();
              }
            }
            ((SQLiteCursor)localObject1).dispose();
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
  
  public void updateChatParticipants(final TLRPC.ChatParticipants paramChatParticipants)
  {
    if (paramChatParticipants == null) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          Object localObject2 = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + paramChatParticipants.chat_id, new Object[0]);
          SQLitePreparedStatement localSQLitePreparedStatement = null;
          new ArrayList();
          final Object localObject1 = localSQLitePreparedStatement;
          if (((SQLiteCursor)localObject2).next())
          {
            NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject2).byteBufferValue(0);
            localObject1 = localSQLitePreparedStatement;
            if (localNativeByteBuffer != null)
            {
              localObject1 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              ((TLRPC.ChatFull)localObject1).pinned_msg_id = ((SQLiteCursor)localObject2).intValue(1);
            }
          }
          ((SQLiteCursor)localObject2).dispose();
          if ((localObject1 instanceof TLRPC.TL_chatFull))
          {
            ((TLRPC.ChatFull)localObject1).participants = paramChatParticipants;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { localObject1, Integer.valueOf(0), Boolean.valueOf(false), null });
              }
            });
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
            localObject2 = new NativeByteBuffer(((TLRPC.ChatFull)localObject1).getObjectSize());
            ((TLRPC.ChatFull)localObject1).serializeToStream((AbstractSerializedData)localObject2);
            localSQLitePreparedStatement.bindInteger(1, ((TLRPC.ChatFull)localObject1).id);
            localSQLitePreparedStatement.bindByteBuffer(2, (NativeByteBuffer)localObject2);
            localSQLitePreparedStatement.bindInteger(3, ((TLRPC.ChatFull)localObject1).pinned_msg_id);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            ((NativeByteBuffer)localObject2).reuse();
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void updateDbToLastVersion(final int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        SQLitePreparedStatement localSQLitePreparedStatement;
        int k;
        try
        {
          j = paramInt;
          i = j;
          if (j < 4)
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
            MessagesStorage.this.storageQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                Object localObject1 = new ArrayList();
                Object localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getAll().entrySet().iterator();
                while (((Iterator)localObject2).hasNext())
                {
                  Object localObject3 = (Map.Entry)((Iterator)localObject2).next();
                  String str = (String)((Map.Entry)localObject3).getKey();
                  if ((str.startsWith("notify2_")) && (((Integer)((Map.Entry)localObject3).getValue()).intValue() == 2))
                  {
                    localObject3 = str.replace("notify2_", "");
                    try
                    {
                      ((ArrayList)localObject1).add(Integer.valueOf(Integer.parseInt((String)localObject3)));
                    }
                    catch (Exception localException2)
                    {
                      localException2.printStackTrace();
                    }
                  }
                }
                try
                {
                  MessagesStorage.this.database.beginTransaction();
                  localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                  localObject1 = ((ArrayList)localObject1).iterator();
                  while (((Iterator)localObject1).hasNext())
                  {
                    Integer localInteger = (Integer)((Iterator)localObject1).next();
                    ((SQLitePreparedStatement)localObject2).requery();
                    ((SQLitePreparedStatement)localObject2).bindLong(1, localInteger.intValue());
                    ((SQLitePreparedStatement)localObject2).bindInteger(2, 1);
                    ((SQLitePreparedStatement)localObject2).step();
                  }
                  ((SQLitePreparedStatement)localObject2).dispose();
                }
                catch (Exception localException1)
                {
                  FileLog.e("tmessages", localException1);
                  return;
                }
                MessagesStorage.this.database.commitTransaction();
              }
            });
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
            i = 4;
          }
          j = i;
          if (i == 4)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            MessagesStorage.this.database.beginTransaction();
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            if (localSQLiteCursor.next())
            {
              j = localSQLiteCursor.intValue(0);
              localObject1 = localSQLiteCursor.byteBufferValue(1);
              if (localObject1 != null)
              {
                k = ((NativeByteBuffer)localObject1).limit();
                i = 0;
                while (i < k / 4)
                {
                  localSQLitePreparedStatement.requery();
                  localSQLitePreparedStatement.bindInteger(1, ((NativeByteBuffer)localObject1).readInt32(false));
                  localSQLitePreparedStatement.bindInteger(2, j);
                  localSQLitePreparedStatement.step();
                  i += 1;
                }
                ((NativeByteBuffer)localObject1).reuse();
              }
            }
            localSQLitePreparedStatement.dispose();
            localSQLiteCursor.dispose();
            MessagesStorage.this.database.commitTransaction();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            j = 6;
          }
          k = j;
          if (j != 6) {
            break label2769;
          }
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
          k = 7;
        }
        catch (Exception localException)
        {
          SQLiteCursor localSQLiteCursor;
          Object localObject1;
          FileLog.e("tmessages", localException);
        }
        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
        MessagesStorage.this.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
        int i = 10;
        label908:
        int j = i;
        if (i == 10)
        {
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
          j = 11;
          break label2792;
          label958:
          j = i;
          if (i == 12)
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
            j = 13;
          }
          i = j;
          if (j == 13)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            i = 14;
          }
          j = i;
          if (i == 14)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            j = 15;
          }
          i = j;
          if (j == 15)
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
          i = j;
          if (j == 19)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            i = 20;
          }
          j = i;
          if (i == 20)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            j = 21;
          }
          i = j;
          if (j == 21)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
            while (localSQLiteCursor.next())
            {
              i = localSQLiteCursor.intValue(0);
              Object localObject2 = localSQLiteCursor.byteBufferValue(1);
              if (localObject2 != null)
              {
                localObject1 = TLRPC.ChatParticipants.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                ((NativeByteBuffer)localObject2).reuse();
                if (localObject1 != null)
                {
                  localObject2 = new TLRPC.TL_chatFull();
                  ((TLRPC.TL_chatFull)localObject2).id = i;
                  ((TLRPC.TL_chatFull)localObject2).chat_photo = new TLRPC.TL_photoEmpty();
                  ((TLRPC.TL_chatFull)localObject2).notify_settings = new TLRPC.TL_peerNotifySettingsEmpty();
                  ((TLRPC.TL_chatFull)localObject2).exported_invite = new TLRPC.TL_chatInviteEmpty();
                  ((TLRPC.TL_chatFull)localObject2).participants = ((TLRPC.ChatParticipants)localObject1);
                  localObject1 = new NativeByteBuffer(((TLRPC.TL_chatFull)localObject2).getObjectSize());
                  ((TLRPC.TL_chatFull)localObject2).serializeToStream((AbstractSerializedData)localObject1);
                  localSQLitePreparedStatement.requery();
                  localSQLitePreparedStatement.bindInteger(1, i);
                  localSQLitePreparedStatement.bindByteBuffer(2, (NativeByteBuffer)localObject1);
                  localSQLitePreparedStatement.step();
                  ((NativeByteBuffer)localObject1).reuse();
                }
              }
            }
            label1815:
            return;
            localSQLitePreparedStatement.dispose();
            localException.dispose();
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
            i = 22;
          }
          j = i;
          if (i == 22)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
            j = 23;
          }
          k = j;
          if (j != 24) {
            break label2806;
          }
          MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
          k = 25;
          break label2806;
        }
        for (;;)
        {
          label2158:
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
          i = 27;
          label2769:
          label2792:
          label2806:
          do
          {
            j = i;
            if (i == 27)
            {
              MessagesStorage.this.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
              j = 28;
            }
            i = j;
            if (j == 28)
            {
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 29").stepThis().dispose();
              i = 29;
            }
            j = i;
            if (i == 29)
            {
              MessagesStorage.this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
              j = 30;
            }
            i = j;
            if (j == 30)
            {
              MessagesStorage.this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
              i = 31;
            }
            j = i;
            if (i == 31)
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
            if (i != 35) {
              break label1815;
            }
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
            return;
            if ((k == 7) || (k == 8)) {
              break;
            }
            i = k;
            if (k != 9) {
              break label908;
            }
            break;
            i = j;
            if (j != 11) {
              break label958;
            }
            i = 12;
            break label958;
            if (k == 25) {
              break label2158;
            }
            i = k;
          } while (k != 26);
        }
      }
    });
  }
  
  public void updateDialogsWithDeletedMessages(final ArrayList<Integer> paramArrayList, boolean paramBoolean, final int paramInt)
  {
    if ((paramArrayList.isEmpty()) && (paramInt == 0)) {
      return;
    }
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(paramArrayList, paramInt);
        }
      });
      return;
    }
    updateDialogsWithDeletedMessagesInternal(paramArrayList, paramInt);
  }
  
  public void updateDialogsWithReadMessages(final SparseArray<Long> paramSparseArray1, final SparseArray<Long> paramSparseArray2, boolean paramBoolean)
  {
    if (paramSparseArray1.size() == 0) {
      return;
    }
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.updateDialogsWithReadMessagesInternal(null, paramSparseArray1, paramSparseArray2);
        }
      });
      return;
    }
    updateDialogsWithReadMessagesInternal(null, paramSparseArray1, paramSparseArray2);
  }
  
  public void updateEncryptedChat(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int j = 1;
        NativeByteBuffer localNativeByteBuffer1 = null;
        SQLitePreparedStatement localSQLitePreparedStatement2 = null;
        localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
        localObject1 = localNativeByteBuffer1;
        for (;;)
        {
          try
          {
            if (paramEncryptedChat.key_hash != null)
            {
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localNativeByteBuffer1;
              if (paramEncryptedChat.key_hash.length >= 16) {}
            }
            else
            {
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localNativeByteBuffer1;
              if (paramEncryptedChat.auth_key != null)
              {
                localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
                localObject1 = localNativeByteBuffer1;
                paramEncryptedChat.key_hash = AndroidUtilities.calcAuthKeyHash(paramEncryptedChat.auth_key);
              }
            }
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localNativeByteBuffer1;
            localSQLitePreparedStatement2 = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ? WHERE uid = ?");
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localNativeByteBuffer1 = new NativeByteBuffer(paramEncryptedChat.getObjectSize());
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            if (paramEncryptedChat.a_or_b != null)
            {
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              i = paramEncryptedChat.a_or_b.length;
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              NativeByteBuffer localNativeByteBuffer2 = new NativeByteBuffer(i);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              if (paramEncryptedChat.auth_key == null) {
                continue;
              }
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              i = paramEncryptedChat.auth_key.length;
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              NativeByteBuffer localNativeByteBuffer3 = new NativeByteBuffer(i);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              if (paramEncryptedChat.future_auth_key == null) {
                continue;
              }
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              i = paramEncryptedChat.future_auth_key.length;
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              NativeByteBuffer localNativeByteBuffer4 = new NativeByteBuffer(i);
              i = j;
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              if (paramEncryptedChat.key_hash != null)
              {
                localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
                localObject1 = localSQLitePreparedStatement2;
                i = paramEncryptedChat.key_hash.length;
              }
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              NativeByteBuffer localNativeByteBuffer5 = new NativeByteBuffer(i);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              paramEncryptedChat.serializeToStream(localNativeByteBuffer1);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindByteBuffer(1, localNativeByteBuffer1);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              if (paramEncryptedChat.a_or_b != null)
              {
                localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
                localObject1 = localSQLitePreparedStatement2;
                localNativeByteBuffer2.writeBytes(paramEncryptedChat.a_or_b);
              }
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              if (paramEncryptedChat.auth_key != null)
              {
                localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
                localObject1 = localSQLitePreparedStatement2;
                localNativeByteBuffer3.writeBytes(paramEncryptedChat.auth_key);
              }
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              if (paramEncryptedChat.future_auth_key != null)
              {
                localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
                localObject1 = localSQLitePreparedStatement2;
                localNativeByteBuffer4.writeBytes(paramEncryptedChat.future_auth_key);
              }
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              if (paramEncryptedChat.key_hash != null)
              {
                localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
                localObject1 = localSQLitePreparedStatement2;
                localNativeByteBuffer5.writeBytes(paramEncryptedChat.key_hash);
              }
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindByteBuffer(2, localNativeByteBuffer2);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindByteBuffer(3, localNativeByteBuffer3);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindInteger(4, paramEncryptedChat.ttl);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindInteger(5, paramEncryptedChat.layer);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindInteger(6, paramEncryptedChat.seq_in);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindInteger(7, paramEncryptedChat.seq_out);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindInteger(8, paramEncryptedChat.key_use_count_in << 16 | paramEncryptedChat.key_use_count_out);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindLong(9, paramEncryptedChat.exchange_id);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindInteger(10, paramEncryptedChat.key_create_date);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindLong(11, paramEncryptedChat.future_key_fingerprint);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindByteBuffer(12, localNativeByteBuffer4);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindByteBuffer(13, localNativeByteBuffer5);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.bindInteger(14, paramEncryptedChat.id);
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localSQLitePreparedStatement2.step();
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localNativeByteBuffer1.reuse();
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localNativeByteBuffer2.reuse();
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localNativeByteBuffer3.reuse();
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localNativeByteBuffer4.reuse();
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localNativeByteBuffer5.reuse();
              if (localSQLitePreparedStatement2 != null) {
                localSQLitePreparedStatement2.dispose();
              }
              return;
            }
          }
          catch (Exception localException)
          {
            int i;
            localObject1 = localSQLitePreparedStatement1;
            FileLog.e("tmessages", localException);
            if (localSQLitePreparedStatement1 == null) {
              continue;
            }
            localSQLitePreparedStatement1.dispose();
            return;
          }
          finally
          {
            if (localObject1 == null) {
              continue;
            }
            ((SQLitePreparedStatement)localObject1).dispose();
          }
          i = 1;
          continue;
          i = 1;
          continue;
          i = 1;
        }
      }
    });
  }
  
  public void updateEncryptedChatLayer(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject3 = null;
        Object localObject1 = null;
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(1, paramEncryptedChat.layer);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(2, paramEncryptedChat.id);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.step();
          if (localSQLitePreparedStatement != null) {
            localSQLitePreparedStatement.dispose();
          }
          return;
        }
        catch (Exception localException)
        {
          localObject3 = localObject1;
          FileLog.e("tmessages", localException);
          return;
        }
        finally
        {
          if (localObject3 != null) {
            ((SQLitePreparedStatement)localObject3).dispose();
          }
        }
      }
    });
  }
  
  public void updateEncryptedChatSeq(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject3 = null;
        Object localObject1 = null;
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ? WHERE uid = ?");
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(1, paramEncryptedChat.seq_in);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(2, paramEncryptedChat.seq_out);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(3, paramEncryptedChat.key_use_count_in << 16 | paramEncryptedChat.key_use_count_out);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(4, paramEncryptedChat.id);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.step();
          if (localSQLitePreparedStatement != null) {
            localSQLitePreparedStatement.dispose();
          }
          return;
        }
        catch (Exception localException)
        {
          localObject3 = localObject1;
          FileLog.e("tmessages", localException);
          return;
        }
        finally
        {
          if (localObject3 != null) {
            ((SQLitePreparedStatement)localObject3).dispose();
          }
        }
      }
    });
  }
  
  public void updateEncryptedChatTTL(final TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null) {
      return;
    }
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject3 = null;
        Object localObject1 = null;
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(1, paramEncryptedChat.ttl);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(2, paramEncryptedChat.id);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.step();
          if (localSQLitePreparedStatement != null) {
            localSQLitePreparedStatement.dispose();
          }
          return;
        }
        catch (Exception localException)
        {
          localObject3 = localObject1;
          FileLog.e("tmessages", localException);
          return;
        }
        finally
        {
          if (localObject3 != null) {
            ((SQLitePreparedStatement)localObject3).dispose();
          }
        }
      }
    });
  }
  
  public long[] updateMessageStateAndId(final long paramLong, Integer paramInteger, final int paramInt1, final int paramInt2, boolean paramBoolean, final int paramInt3)
  {
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.updateMessageStateAndIdInternal(paramLong, paramInt1, paramInt2, paramInt3, this.val$channelId);
        }
      });
      return null;
    }
    return updateMessageStateAndIdInternal(paramLong, paramInteger, paramInt1, paramInt2, paramInt3);
  }
  
  public void updateUsers(final ArrayList<TLRPC.User> paramArrayList, final boolean paramBoolean1, final boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramArrayList.isEmpty()) {
      return;
    }
    if (paramBoolean3)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          MessagesStorage.this.updateUsersInternal(paramArrayList, paramBoolean1, paramBoolean2);
        }
      });
      return;
    }
    updateUsersInternal(paramArrayList, paramBoolean1, paramBoolean2);
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MessagesStorage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */