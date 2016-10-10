package org.telegram.messenger.query;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterVoice;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class SharedMediaQuery
{
  public static final int MEDIA_AUDIO = 2;
  public static final int MEDIA_FILE = 1;
  public static final int MEDIA_MUSIC = 4;
  public static final int MEDIA_PHOTOVIDEO = 0;
  public static final int MEDIA_TYPES_COUNT = 5;
  public static final int MEDIA_URL = 3;
  
  public static boolean canAddMessageToMedia(TLRPC.Message paramMessage)
  {
    if (((paramMessage instanceof TLRPC.TL_message_secret)) && ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessage.ttl != 0) && (paramMessage.ttl <= 60)) {}
    for (;;)
    {
      return false;
      if (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) || (((paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) && (!MessageObject.isGifDocument(paramMessage.media.document)))) {
        return true;
      }
      if (!paramMessage.entities.isEmpty())
      {
        int i = 0;
        while (i < paramMessage.entities.size())
        {
          TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramMessage.entities.get(i);
          if (((localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityEmail))) {
            return true;
          }
          i += 1;
        }
      }
    }
  }
  
  public static void getMediaCount(long paramLong, int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    int i = (int)paramLong;
    if ((paramBoolean) || (i == 0))
    {
      getMediaCountDatabase(paramLong, paramInt1, paramInt2);
      return;
    }
    TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
    localTL_messages_search.offset = 0;
    localTL_messages_search.limit = 1;
    localTL_messages_search.max_id = 0;
    if (paramInt1 == 0) {
      localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
    }
    for (;;)
    {
      localTL_messages_search.q = "";
      localTL_messages_search.peer = MessagesController.getInputPeer(i);
      if (localTL_messages_search.peer == null) {
        break;
      }
      paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
            MessagesStorage.getInstance().putUsersAndChats(paramAnonymousTLObject.users, paramAnonymousTLObject.chats, true, true);
            if (!(paramAnonymousTLObject instanceof TLRPC.TL_messages_messages)) {
              break label70;
            }
          }
          label70:
          for (int i = paramAnonymousTLObject.messages.size();; i = paramAnonymousTLObject.count)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.getInstance().putUsers(paramAnonymousTLObject.users, false);
                MessagesController.getInstance().putChats(paramAnonymousTLObject.chats, false);
              }
            });
            SharedMediaQuery.processLoadedMediaCount(i, this.val$uid, paramInt2, this.val$classGuid, false);
            return;
          }
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt2);
      return;
      if (paramInt1 == 1) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
      } else if (paramInt1 == 2) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterVoice();
      } else if (paramInt1 == 3) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
      } else if (paramInt1 == 4) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
      }
    }
  }
  
  private static void getMediaCountDatabase(long paramLong, int paramInt1, final int paramInt2)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        int i = -1;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[] { Long.valueOf(this.val$uid), Integer.valueOf(paramInt2) }), new Object[0]);
          if (localSQLiteCursor.next()) {
            i = localSQLiteCursor.intValue(0);
          }
          localSQLiteCursor.dispose();
          int k = (int)this.val$uid;
          int j = i;
          if (i == -1)
          {
            j = i;
            if (k == 0)
            {
              localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[] { Long.valueOf(this.val$uid), Integer.valueOf(paramInt2) }), new Object[0]);
              if (localSQLiteCursor.next()) {
                i = localSQLiteCursor.intValue(0);
              }
              localSQLiteCursor.dispose();
              j = i;
              if (i != -1)
              {
                SharedMediaQuery.putMediaCountDatabase(this.val$uid, paramInt2, i);
                j = i;
              }
            }
          }
          SharedMediaQuery.processLoadedMediaCount(j, this.val$uid, paramInt2, this.val$classGuid, true);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public static int getMediaType(TLRPC.Message paramMessage)
  {
    if (paramMessage == null) {}
    for (;;)
    {
      return -1;
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) {
        return 0;
      }
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
      {
        if (MessageObject.isVoiceMessage(paramMessage)) {
          return 2;
        }
        if (MessageObject.isVideoMessage(paramMessage)) {
          return 0;
        }
        if (!MessageObject.isStickerMessage(paramMessage))
        {
          if (MessageObject.isMusicMessage(paramMessage)) {
            return 4;
          }
          return 1;
        }
      }
      else if (!paramMessage.entities.isEmpty())
      {
        int i = 0;
        while (i < paramMessage.entities.size())
        {
          TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramMessage.entities.get(i);
          if (((localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityEmail))) {
            return 3;
          }
          i += 1;
        }
      }
    }
  }
  
  public static void loadMedia(final long paramLong, int paramInt1, int paramInt2, final int paramInt3, final int paramInt4, boolean paramBoolean, final int paramInt5)
  {
    if (((int)paramLong < 0) && (ChatObject.isChannel(-(int)paramLong))) {}
    int i;
    for (final boolean bool = true;; bool = false)
    {
      i = (int)paramLong;
      if ((!paramBoolean) && (i != 0)) {
        break;
      }
      loadMediaDatabase(paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, bool);
      return;
    }
    TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
    localTL_messages_search.offset = paramInt1;
    localTL_messages_search.limit = (paramInt2 + 1);
    localTL_messages_search.max_id = paramInt3;
    if (paramInt4 == 0) {
      localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
    }
    for (;;)
    {
      localTL_messages_search.q = "";
      localTL_messages_search.peer = MessagesController.getInputPeer(i);
      if (localTL_messages_search.peer == null) {
        break;
      }
      paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          boolean bool;
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
            if (paramAnonymousTLObject.messages.size() <= this.val$count) {
              break label77;
            }
            bool = false;
            paramAnonymousTLObject.messages.remove(paramAnonymousTLObject.messages.size() - 1);
          }
          for (;;)
          {
            SharedMediaQuery.processLoadedMedia(paramAnonymousTLObject, paramLong, paramInt3, this.val$count, paramInt4, paramInt5, false, bool, this.val$isChannel, bool);
            return;
            label77:
            bool = true;
          }
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt5);
      return;
      if (paramInt4 == 1) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
      } else if (paramInt4 == 2) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterVoice();
      } else if (paramInt4 == 3) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
      } else if (paramInt4 == 4) {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
      }
    }
  }
  
  private static void loadMediaDatabase(final long paramLong, final int paramInt1, int paramInt2, int paramInt3, final int paramInt4, final int paramInt5, final boolean paramBoolean)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        TLRPC.TL_messages_messages localTL_messages_messages = new TLRPC.TL_messages_messages();
        for (;;)
        {
          ArrayList localArrayList1;
          ArrayList localArrayList2;
          int j;
          boolean bool;
          int i;
          try
          {
            localArrayList1 = new ArrayList();
            localArrayList2 = new ArrayList();
            j = this.val$count + 1;
            localObject2 = MessagesStorage.getInstance().getDatabase();
            bool = false;
            if ((int)paramLong == 0) {
              break label992;
            }
            i = 0;
            l1 = paramBoolean;
            if (!paramInt4) {
              break label1297;
            }
            i = -(int)paramLong;
          }
          catch (Exception localException)
          {
            Object localObject2;
            long l3;
            localTL_messages_messages.messages.clear();
            localTL_messages_messages.chats.clear();
            localTL_messages_messages.users.clear();
            FileLog.e("tmessages", localException);
            return;
            bool = false;
            continue;
            ((SQLiteCursor)localObject4).dispose();
            localObject4 = localException.queryFinalized(String.format(Locale.US, "SELECT min(mid) FROM media_v2 WHERE uid = %d AND type = %d AND mid > 0", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1) }), new Object[0]);
            if (!((SQLiteCursor)localObject4).next()) {
              continue;
            }
            int k = ((SQLiteCursor)localObject4).intValue(0);
            if (k == 0) {
              continue;
            }
            localObject5 = localException.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
            ((SQLitePreparedStatement)localObject5).requery();
            ((SQLitePreparedStatement)localObject5).bindLong(1, paramLong);
            ((SQLitePreparedStatement)localObject5).bindInteger(2, paramInt1);
            ((SQLitePreparedStatement)localObject5).bindInteger(3, 0);
            ((SQLitePreparedStatement)localObject5).bindInteger(4, k);
            ((SQLitePreparedStatement)localObject5).step();
            ((SQLitePreparedStatement)localObject5).dispose();
            ((SQLiteCursor)localObject4).dispose();
            continue;
          }
          finally
          {
            SharedMediaQuery.processLoadedMedia(localTL_messages_messages, paramLong, paramInt5, this.val$count, paramBoolean, paramInt1, true, this.val$classGuid, paramInt4, false);
          }
          Object localObject4 = ((SQLiteDatabase)localObject2).queryFinalized(String.format(Locale.US, "SELECT start FROM media_holes_v2 WHERE uid = %d AND type = %d AND start IN (0, 1)", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1) }), new Object[0]);
          Object localObject1;
          Object localObject5;
          if (((SQLiteCursor)localObject4).next()) {
            if (((SQLiteCursor)localObject4).intValue(0) == 1)
            {
              bool = true;
              ((SQLiteCursor)localObject4).dispose();
              if (localObject1 == 0L) {
                break label765;
              }
              l1 = 0L;
              localObject4 = ((SQLiteDatabase)localObject2).queryFinalized(String.format(Locale.US, "SELECT end FROM media_holes_v2 WHERE uid = %d AND type = %d AND end <= %d ORDER BY end DESC LIMIT 1", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramBoolean) }), new Object[0]);
              if (((SQLiteCursor)localObject4).next())
              {
                l3 = ((SQLiteCursor)localObject4).intValue(0);
                l1 = l3;
                if (i != 0) {
                  l1 = l3 | i << 32;
                }
              }
              ((SQLiteCursor)localObject4).dispose();
              if (l1 <= 1L) {
                break label704;
              }
              localObject2 = ((SQLiteDatabase)localObject2).queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Long.valueOf(localObject1), Long.valueOf(l1), Integer.valueOf(paramInt1), Integer.valueOf(j) }), new Object[0]);
              if (!((SQLiteCursor)localObject2).next()) {
                break label1163;
              }
              localObject4 = ((SQLiteCursor)localObject2).byteBufferValue(0);
              if (localObject4 == null) {
                continue;
              }
              localObject5 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
              ((NativeByteBuffer)localObject4).reuse();
              ((TLRPC.Message)localObject5).id = ((SQLiteCursor)localObject2).intValue(1);
              ((TLRPC.Message)localObject5).dialog_id = paramLong;
              if ((int)paramLong == 0) {
                ((TLRPC.Message)localObject5).random_id = ((SQLiteCursor)localObject2).longValue(2);
              }
              localTL_messages_messages.messages.add(localObject5);
              if (((TLRPC.Message)localObject5).from_id <= 0) {
                break label1128;
              }
              if (localArrayList1.contains(Integer.valueOf(((TLRPC.Message)localObject5).from_id))) {
                continue;
              }
              localArrayList1.add(Integer.valueOf(((TLRPC.Message)localObject5).from_id));
              continue;
            }
          }
          label704:
          SQLiteCursor localSQLiteCursor = ((SQLiteDatabase)localObject3).queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d", new Object[] { Long.valueOf(paramLong), Long.valueOf(localObject1), Integer.valueOf(paramInt1), Integer.valueOf(j) }), new Object[0]);
          continue;
          label765:
          long l1 = 0L;
          localObject4 = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT max(end) FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1) }), new Object[0]);
          long l2;
          if (((SQLiteCursor)localObject4).next())
          {
            l2 = ((SQLiteCursor)localObject4).intValue(0);
            l1 = l2;
            if (i != 0) {
              l1 = l2 | i << 32;
            }
          }
          ((SQLiteCursor)localObject4).dispose();
          if (l1 > 1L)
          {
            localSQLiteCursor = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid >= %d AND type = %d ORDER BY date DESC, mid DESC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Long.valueOf(l1), Integer.valueOf(paramInt1), Integer.valueOf(paramInt5), Integer.valueOf(j) }), new Object[0]);
          }
          else
          {
            localSQLiteCursor = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid > 0 AND type = %d ORDER BY date DESC, mid DESC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt5), Integer.valueOf(j) }), new Object[0]);
            continue;
            label992:
            bool = true;
            if (paramBoolean != 0)
            {
              localSQLiteCursor = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND m.mid > %d AND type = %d ORDER BY m.mid ASC LIMIT %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramBoolean), Integer.valueOf(paramInt1), Integer.valueOf(j) }), new Object[0]);
            }
            else
            {
              localSQLiteCursor = localSQLiteCursor.queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, r.random_id FROM media_v2 as m LEFT JOIN randoms as r ON r.mid = m.mid WHERE m.uid = %d AND type = %d ORDER BY m.mid ASC LIMIT %d,%d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt5), Integer.valueOf(j) }), new Object[0]);
              continue;
              label1128:
              if (!localArrayList2.contains(Integer.valueOf(-((TLRPC.Message)localObject5).from_id)))
              {
                localArrayList2.add(Integer.valueOf(-((TLRPC.Message)localObject5).from_id));
                continue;
                label1163:
                localSQLiteCursor.dispose();
                if (!localArrayList1.isEmpty()) {
                  MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList1), localTL_messages_messages.users);
                }
                if (!localArrayList2.isEmpty()) {
                  MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList2), localTL_messages_messages.chats);
                }
                if (localTL_messages_messages.messages.size() > this.val$count)
                {
                  bool = false;
                  localTL_messages_messages.messages.remove(localTL_messages_messages.messages.size() - 1);
                }
                for (;;)
                {
                  SharedMediaQuery.processLoadedMedia(localTL_messages_messages, paramLong, paramInt5, this.val$count, paramBoolean, paramInt1, true, this.val$classGuid, paramInt4, bool);
                  return;
                }
                label1297:
                l2 = l1;
                if (l1 != 0L)
                {
                  l2 = l1;
                  if (i != 0) {
                    l2 = l1 | i << 32;
                  }
                }
              }
            }
          }
        }
      }
    });
  }
  
  public static void loadMusic(long paramLong, int paramInt)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        final ArrayList localArrayList = new ArrayList();
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[] { Long.valueOf(this.val$uid), Integer.valueOf(this.val$max_id), Integer.valueOf(4) }), new Object[0]);
          while (localSQLiteCursor.next())
          {
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
            if (localNativeByteBuffer != null)
            {
              TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              if (MessageObject.isMusicMessage(localMessage))
              {
                localMessage.id = localSQLiteCursor.intValue(1);
                localMessage.dialog_id = this.val$uid;
                localArrayList.add(0, new MessageObject(localMessage, null, false));
                continue;
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.musicDidLoaded, new Object[] { Long.valueOf(SharedMediaQuery.9.this.val$uid), localArrayList });
                  }
                });
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
        for (;;)
        {
          return;
          localException.dispose();
        }
      }
    });
  }
  
  private static void processLoadedMedia(TLRPC.messages_Messages parammessages_Messages, final long paramLong, int paramInt1, int paramInt2, int paramInt3, final int paramInt4, final boolean paramBoolean1, final int paramInt5, boolean paramBoolean2, final boolean paramBoolean3)
  {
    int i = (int)paramLong;
    if ((paramBoolean1) && (parammessages_Messages.messages.isEmpty()) && (i != 0))
    {
      loadMedia(paramLong, paramInt1, paramInt2, paramInt3, paramInt4, false, paramInt5);
      return;
    }
    if (!paramBoolean1)
    {
      ImageLoader.saveMessagesThumbs(parammessages_Messages.messages);
      MessagesStorage.getInstance().putUsersAndChats(parammessages_Messages.users, parammessages_Messages.chats, true, true);
      putMediaDatabase(paramLong, paramInt4, parammessages_Messages.messages, paramInt3, paramBoolean3);
    }
    HashMap localHashMap = new HashMap();
    paramInt1 = 0;
    while (paramInt1 < parammessages_Messages.users.size())
    {
      localObject = (TLRPC.User)parammessages_Messages.users.get(paramInt1);
      localHashMap.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
      paramInt1 += 1;
    }
    Object localObject = new ArrayList();
    paramInt1 = 0;
    while (paramInt1 < parammessages_Messages.messages.size())
    {
      ((ArrayList)localObject).add(new MessageObject((TLRPC.Message)parammessages_Messages.messages.get(paramInt1), localHashMap, true));
      paramInt1 += 1;
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        int i = this.val$res.count;
        MessagesController.getInstance().putUsers(this.val$res.users, paramBoolean1);
        MessagesController.getInstance().putChats(this.val$res.chats, paramBoolean1);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.mediaDidLoaded, new Object[] { Long.valueOf(paramLong), Integer.valueOf(i), paramInt5, Integer.valueOf(paramInt4), Integer.valueOf(paramBoolean3), Boolean.valueOf(this.val$topReached) });
      }
    });
  }
  
  private static void processLoadedMediaCount(final int paramInt1, long paramLong, final int paramInt2, final int paramInt3, boolean paramBoolean)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        int i = 0;
        int j = (int)this.val$uid;
        if ((paramInt1) && (paramInt2 == -1) && (j != 0))
        {
          SharedMediaQuery.getMediaCount(this.val$uid, paramInt3, this.val$classGuid, false);
          return;
        }
        if (!paramInt1) {
          SharedMediaQuery.putMediaCountDatabase(this.val$uid, paramInt3, paramInt2);
        }
        NotificationCenter localNotificationCenter = NotificationCenter.getInstance();
        j = NotificationCenter.mediaCountDidLoaded;
        long l = this.val$uid;
        if ((paramInt1) && (paramInt2 == -1)) {}
        for (;;)
        {
          localNotificationCenter.postNotificationName(j, new Object[] { Long.valueOf(l), Integer.valueOf(i), Boolean.valueOf(paramInt1), Integer.valueOf(paramInt3) });
          return;
          i = paramInt2;
        }
      }
    });
  }
  
  private static void putMediaCountDatabase(long paramLong, int paramInt1, final int paramInt2)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindLong(1, this.val$uid);
          localSQLitePreparedStatement.bindInteger(2, paramInt2);
          localSQLitePreparedStatement.bindInteger(3, this.val$count);
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
  
  private static void putMediaDatabase(final long paramLong, final int paramInt1, ArrayList<TLRPC.Message> paramArrayList, int paramInt2, final boolean paramBoolean)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        int i = 1;
        try
        {
          if ((this.val$messages.isEmpty()) || (paramBoolean))
          {
            MessagesStorage.getInstance().doneHolesInMedia(paramLong, paramInt1, this.val$type);
            if (this.val$messages.isEmpty()) {
              return;
            }
          }
          MessagesStorage.getInstance().getDatabase().beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
          Iterator localIterator = this.val$messages.iterator();
          while (localIterator.hasNext())
          {
            TLRPC.Message localMessage = (TLRPC.Message)localIterator.next();
            if (SharedMediaQuery.canAddMessageToMedia(localMessage))
            {
              long l2 = localMessage.id;
              long l1 = l2;
              if (localMessage.to_id.channel_id != 0) {
                l1 = l2 | localMessage.to_id.channel_id << 32;
              }
              localSQLitePreparedStatement.requery();
              NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localMessage.getObjectSize());
              localMessage.serializeToStream(localNativeByteBuffer);
              localSQLitePreparedStatement.bindLong(1, l1);
              localSQLitePreparedStatement.bindLong(2, paramLong);
              localSQLitePreparedStatement.bindInteger(3, localMessage.date);
              localSQLitePreparedStatement.bindInteger(4, this.val$type);
              localSQLitePreparedStatement.bindByteBuffer(5, localNativeByteBuffer);
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
        if ((!paramBoolean) || (paramInt1 != 0))
        {
          if (!paramBoolean) {
            break label305;
          }
          if (paramInt1 == 0) {
            break label331;
          }
          MessagesStorage.getInstance().closeHolesInMedia(paramLong, i, paramInt1, this.val$type);
        }
        for (;;)
        {
          MessagesStorage.getInstance().getDatabase().commitTransaction();
          return;
          label305:
          i = ((TLRPC.Message)this.val$messages.get(this.val$messages.size() - 1)).id;
          break;
          label331:
          MessagesStorage.getInstance().closeHolesInMedia(paramLong, i, Integer.MAX_VALUE, this.val$type);
        }
      }
    });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/query/SharedMediaQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */