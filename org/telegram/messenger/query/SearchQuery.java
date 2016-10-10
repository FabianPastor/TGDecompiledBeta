package org.telegram.messenger.query;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_contacts_getTopPeers;
import org.telegram.tgnet.TLRPC.TL_contacts_resetTopPeerRating;
import org.telegram.tgnet.TLRPC.TL_contacts_topPeers;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryBotsInline;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryCorrespondents;
import org.telegram.tgnet.TLRPC.TL_topPeerCategoryPeers;
import org.telegram.tgnet.TLRPC.User;

public class SearchQuery
{
  public static ArrayList<TLRPC.TL_topPeer> hints = new ArrayList();
  public static ArrayList<TLRPC.TL_topPeer> inlineBots = new ArrayList();
  private static HashMap<Integer, Integer> inlineDates = new HashMap();
  private static boolean loaded;
  private static boolean loading;
  
  public static void cleanup()
  {
    loading = false;
    loaded = false;
    hints.clear();
    inlineBots.clear();
    inlineDates.clear();
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
  }
  
  private static void deletePeer(int paramInt1, final int paramInt2)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[] { Integer.valueOf(this.val$did), Integer.valueOf(paramInt2) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public static void increaseInlineRaiting(int paramInt)
  {
    Object localObject1 = (Integer)inlineDates.get(Integer.valueOf(paramInt));
    int i;
    Object localObject2;
    int j;
    if (localObject1 != null)
    {
      i = Math.max(1, (int)(System.currentTimeMillis() / 1000L) - ((Integer)localObject1).intValue());
      localObject2 = null;
      j = 0;
    }
    for (;;)
    {
      localObject1 = localObject2;
      if (j < inlineBots.size())
      {
        localObject1 = (TLRPC.TL_topPeer)inlineBots.get(j);
        if (((TLRPC.TL_topPeer)localObject1).peer.user_id != paramInt) {}
      }
      else
      {
        localObject2 = localObject1;
        if (localObject1 == null)
        {
          localObject2 = new TLRPC.TL_topPeer();
          ((TLRPC.TL_topPeer)localObject2).peer = new TLRPC.TL_peerUser();
          ((TLRPC.TL_topPeer)localObject2).peer.user_id = paramInt;
          inlineBots.add(localObject2);
        }
        ((TLRPC.TL_topPeer)localObject2).rating += Math.exp(i / MessagesController.getInstance().ratingDecay);
        Collections.sort(inlineBots, new Comparator()
        {
          public int compare(TLRPC.TL_topPeer paramAnonymousTL_topPeer1, TLRPC.TL_topPeer paramAnonymousTL_topPeer2)
          {
            if (paramAnonymousTL_topPeer1.rating > paramAnonymousTL_topPeer2.rating) {
              return -1;
            }
            if (paramAnonymousTL_topPeer1.rating < paramAnonymousTL_topPeer2.rating) {
              return 1;
            }
            return 0;
          }
        });
        if (inlineBots.size() > 20) {
          inlineBots.remove(inlineBots.size() - 1);
        }
        savePeer(paramInt, 1, ((TLRPC.TL_topPeer)localObject2).rating);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        return;
        i = 60;
        break;
      }
      j += 1;
    }
  }
  
  public static void increasePeerRaiting(long paramLong)
  {
    int i = (int)paramLong;
    if (i <= 0) {}
    for (;;)
    {
      return;
      if (i > 0) {}
      for (TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(i)); (localUser != null) && (!localUser.bot); localUser = null)
      {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            double d2 = 0.0D;
            int j = 0;
            int i = 0;
            d3 = d2;
            try
            {
              SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[] { Long.valueOf(this.val$did) }), new Object[0]);
              d3 = d2;
              if (localSQLiteCursor.next())
              {
                d3 = d2;
                i = localSQLiteCursor.intValue(0);
                d3 = d2;
                j = localSQLiteCursor.intValue(1);
              }
              d3 = d2;
              localSQLiteCursor.dispose();
              d1 = d2;
              if (i > 0)
              {
                d3 = d2;
                localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT date FROM messages WHERE uid = %d AND mid < %d AND out = 1 ORDER BY date DESC", new Object[] { Long.valueOf(this.val$did), Integer.valueOf(i) }), new Object[0]);
                d1 = d2;
                d3 = d2;
                if (localSQLiteCursor.next())
                {
                  d3 = d2;
                  d1 = j - localSQLiteCursor.intValue(0);
                }
                d3 = d1;
                localSQLiteCursor.dispose();
              }
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e("tmessages", localException);
                final double d1 = d3;
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                Object localObject2 = null;
                int i = 0;
                Object localObject1 = localObject2;
                if (i < SearchQuery.hints.size())
                {
                  localObject1 = (TLRPC.TL_topPeer)SearchQuery.hints.get(i);
                  if (((SearchQuery.6.this.val$lower_id >= 0) || ((((TLRPC.TL_topPeer)localObject1).peer.chat_id != -SearchQuery.6.this.val$lower_id) && (((TLRPC.TL_topPeer)localObject1).peer.channel_id != -SearchQuery.6.this.val$lower_id))) && ((SearchQuery.6.this.val$lower_id <= 0) || (((TLRPC.TL_topPeer)localObject1).peer.user_id != SearchQuery.6.this.val$lower_id))) {}
                }
                else
                {
                  localObject2 = localObject1;
                  if (localObject1 == null)
                  {
                    localObject2 = new TLRPC.TL_topPeer();
                    if (SearchQuery.6.this.val$lower_id <= 0) {
                      break label232;
                    }
                    ((TLRPC.TL_topPeer)localObject2).peer = new TLRPC.TL_peerUser();
                    ((TLRPC.TL_topPeer)localObject2).peer.user_id = SearchQuery.6.this.val$lower_id;
                  }
                }
                for (;;)
                {
                  SearchQuery.hints.add(localObject2);
                  ((TLRPC.TL_topPeer)localObject2).rating += Math.exp(d1 / MessagesController.getInstance().ratingDecay);
                  Collections.sort(SearchQuery.hints, new Comparator()
                  {
                    public int compare(TLRPC.TL_topPeer paramAnonymous3TL_topPeer1, TLRPC.TL_topPeer paramAnonymous3TL_topPeer2)
                    {
                      if (paramAnonymous3TL_topPeer1.rating > paramAnonymous3TL_topPeer2.rating) {
                        return -1;
                      }
                      if (paramAnonymous3TL_topPeer1.rating < paramAnonymous3TL_topPeer2.rating) {
                        return 1;
                      }
                      return 0;
                    }
                  });
                  SearchQuery.savePeer((int)SearchQuery.6.this.val$did, 0, ((TLRPC.TL_topPeer)localObject2).rating);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                  return;
                  i += 1;
                  break;
                  label232:
                  ((TLRPC.TL_topPeer)localObject2).peer = new TLRPC.TL_peerChat();
                  ((TLRPC.TL_topPeer)localObject2).peer.chat_id = (-SearchQuery.6.this.val$lower_id);
                }
              }
            });
          }
        });
        return;
      }
    }
  }
  
  public static void loadHints(boolean paramBoolean)
  {
    if (loading) {}
    do
    {
      return;
      if (!paramBoolean) {
        break;
      }
    } while (loaded);
    loading = true;
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1 = new ArrayList();
        final ArrayList localArrayList2 = new ArrayList();
        final HashMap localHashMap = new HashMap();
        final ArrayList localArrayList3 = new ArrayList();
        final ArrayList localArrayList4 = new ArrayList();
        ArrayList localArrayList5;
        ArrayList localArrayList6;
        SQLiteCursor localSQLiteCursor;
        for (;;)
        {
          int i;
          int j;
          TLRPC.TL_topPeer localTL_topPeer;
          try
          {
            localArrayList5 = new ArrayList();
            localArrayList6 = new ArrayList();
            localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, type, rating, date FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
            if (!localSQLiteCursor.next()) {
              break;
            }
            i = localSQLiteCursor.intValue(0);
            j = localSQLiteCursor.intValue(1);
            localTL_topPeer = new TLRPC.TL_topPeer();
            localTL_topPeer.rating = localSQLiteCursor.doubleValue(2);
            if (i > 0)
            {
              localTL_topPeer.peer = new TLRPC.TL_peerUser();
              localTL_topPeer.peer.user_id = i;
              localArrayList5.add(Integer.valueOf(i));
              if (j != 0) {
                break label214;
              }
              localArrayList1.add(localTL_topPeer);
              continue;
            }
            localTL_topPeer.peer = new TLRPC.TL_peerChat();
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          localTL_topPeer.peer.chat_id = (-i);
          localArrayList6.add(Integer.valueOf(-i));
          continue;
          label214:
          if (j == 1)
          {
            localArrayList2.add(localTL_topPeer);
            localHashMap.put(Integer.valueOf(i), Integer.valueOf(localSQLiteCursor.intValue(3)));
          }
        }
        localSQLiteCursor.dispose();
        if (!localArrayList5.isEmpty()) {
          MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList5), localArrayList3);
        }
        if (!localArrayList6.isEmpty()) {
          MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList6), localArrayList4);
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.getInstance().putUsers(localArrayList3, true);
            MessagesController.getInstance().putChats(localArrayList4, true);
            SearchQuery.access$002(false);
            SearchQuery.access$102(true);
            SearchQuery.hints = localException;
            SearchQuery.inlineBots = localArrayList2;
            SearchQuery.access$202(localHashMap);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
            if (Math.abs(UserConfig.lastHintsSyncTime - (int)(System.currentTimeMillis() / 1000L)) >= 86400) {
              SearchQuery.loadHints(false);
            }
          }
        });
      }
    });
    loaded = true;
    return;
    loading = true;
    TLRPC.TL_contacts_getTopPeers localTL_contacts_getTopPeers = new TLRPC.TL_contacts_getTopPeers();
    localTL_contacts_getTopPeers.hash = 0;
    localTL_contacts_getTopPeers.bots_pm = false;
    localTL_contacts_getTopPeers.correspondents = true;
    localTL_contacts_getTopPeers.groups = false;
    localTL_contacts_getTopPeers.channels = false;
    localTL_contacts_getTopPeers.bots_inline = true;
    localTL_contacts_getTopPeers.offset = 0;
    localTL_contacts_getTopPeers.limit = 20;
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_getTopPeers, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if ((paramAnonymousTLObject instanceof TLRPC.TL_contacts_topPeers)) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              final TLRPC.TL_contacts_topPeers localTL_contacts_topPeers = (TLRPC.TL_contacts_topPeers)paramAnonymousTLObject;
              MessagesController.getInstance().putUsers(localTL_contacts_topPeers.users, false);
              MessagesController.getInstance().putChats(localTL_contacts_topPeers.chats, false);
              int i = 0;
              if (i < localTL_contacts_topPeers.categories.size())
              {
                localObject = (TLRPC.TL_topPeerCategoryPeers)localTL_contacts_topPeers.categories.get(i);
                if ((((TLRPC.TL_topPeerCategoryPeers)localObject).category instanceof TLRPC.TL_topPeerCategoryBotsInline)) {
                  SearchQuery.inlineBots = ((TLRPC.TL_topPeerCategoryPeers)localObject).peers;
                }
                for (;;)
                {
                  i += 1;
                  break;
                  SearchQuery.hints = ((TLRPC.TL_topPeerCategoryPeers)localObject).peers;
                }
              }
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
              final Object localObject = new HashMap(SearchQuery.inlineDates);
              MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
              {
                public void run()
                {
                  for (;;)
                  {
                    int j;
                    int k;
                    int m;
                    try
                    {
                      MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
                      MessagesStorage.getInstance().getDatabase().beginTransaction();
                      MessagesStorage.getInstance().putUsersAndChats(localTL_contacts_topPeers.users, localTL_contacts_topPeers.chats, false, false);
                      SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                      j = 0;
                      if (j < localTL_contacts_topPeers.categories.size())
                      {
                        TLRPC.TL_topPeerCategoryPeers localTL_topPeerCategoryPeers = (TLRPC.TL_topPeerCategoryPeers)localTL_contacts_topPeers.categories.get(j);
                        if (!(localTL_topPeerCategoryPeers.category instanceof TLRPC.TL_topPeerCategoryBotsInline)) {
                          break label315;
                        }
                        k = 1;
                        break label309;
                        if (m >= localTL_topPeerCategoryPeers.peers.size()) {
                          break label325;
                        }
                        TLRPC.TL_topPeer localTL_topPeer = (TLRPC.TL_topPeer)localTL_topPeerCategoryPeers.peers.get(m);
                        if ((localTL_topPeer.peer instanceof TLRPC.TL_peerUser))
                        {
                          i = localTL_topPeer.peer.user_id;
                          Integer localInteger = (Integer)localObject.get(Integer.valueOf(i));
                          localSQLitePreparedStatement.requery();
                          localSQLitePreparedStatement.bindInteger(1, i);
                          localSQLitePreparedStatement.bindInteger(2, k);
                          localSQLitePreparedStatement.bindDouble(3, localTL_topPeer.rating);
                          if (localInteger == null) {
                            break label320;
                          }
                          i = localInteger.intValue();
                          localSQLitePreparedStatement.bindInteger(4, i);
                          localSQLitePreparedStatement.step();
                          m += 1;
                          continue;
                        }
                        if ((localTL_topPeer.peer instanceof TLRPC.TL_peerChat))
                        {
                          i = -localTL_topPeer.peer.chat_id;
                          continue;
                        }
                        i = -localTL_topPeer.peer.channel_id;
                        continue;
                      }
                      else
                      {
                        localSQLitePreparedStatement.dispose();
                        MessagesStorage.getInstance().getDatabase().commitTransaction();
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            UserConfig.lastHintsSyncTime = (int)(System.currentTimeMillis() / 1000L);
                            UserConfig.saveConfig(false);
                          }
                        });
                        return;
                      }
                    }
                    catch (Exception localException)
                    {
                      FileLog.e("tmessages", localException);
                      return;
                    }
                    for (;;)
                    {
                      label309:
                      m = 0;
                      break;
                      label315:
                      k = 0;
                    }
                    label320:
                    int i = 0;
                    continue;
                    label325:
                    j += 1;
                  }
                }
              });
            }
          });
        }
      }
    });
  }
  
  public static void removeInline(int paramInt)
  {
    int i = 0;
    for (;;)
    {
      if (i < inlineBots.size())
      {
        if (((TLRPC.TL_topPeer)inlineBots.get(i)).peer.user_id == paramInt)
        {
          inlineBots.remove(i);
          TLRPC.TL_contacts_resetTopPeerRating localTL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
          localTL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryBotsInline();
          localTL_contacts_resetTopPeerRating.peer = MessagesController.getInputPeer(paramInt);
          ConnectionsManager.getInstance().sendRequest(localTL_contacts_resetTopPeerRating, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
          });
          deletePeer(paramInt, 1);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
      }
      else {
        return;
      }
      i += 1;
    }
  }
  
  public static void removePeer(int paramInt)
  {
    int i = 0;
    for (;;)
    {
      if (i < hints.size())
      {
        if (((TLRPC.TL_topPeer)hints.get(i)).peer.user_id == paramInt)
        {
          hints.remove(i);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
          TLRPC.TL_contacts_resetTopPeerRating localTL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
          localTL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryCorrespondents();
          localTL_contacts_resetTopPeerRating.peer = MessagesController.getInputPeer(paramInt);
          deletePeer(paramInt, 0);
          ConnectionsManager.getInstance().sendRequest(localTL_contacts_resetTopPeerRating, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
          });
        }
      }
      else {
        return;
      }
      i += 1;
    }
  }
  
  private static void savePeer(int paramInt1, final int paramInt2, final double paramDouble)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, this.val$did);
          localSQLitePreparedStatement.bindInteger(2, paramInt2);
          localSQLitePreparedStatement.bindDouble(3, paramDouble);
          localSQLitePreparedStatement.bindInteger(4, (int)System.currentTimeMillis() / 1000);
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/query/SearchQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */