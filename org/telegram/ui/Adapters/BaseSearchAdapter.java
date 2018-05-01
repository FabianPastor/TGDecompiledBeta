package org.telegram.ui.Adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_contacts_found;
import org.telegram.tgnet.TLRPC.TL_contacts_search;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

public class BaseSearchAdapter
  extends BaseFragmentAdapter
{
  protected ArrayList<TLObject> globalSearch = new ArrayList();
  protected ArrayList<HashtagObject> hashtags;
  protected HashMap<String, HashtagObject> hashtagsByText;
  protected boolean hashtagsLoadedFromDb = false;
  protected String lastFoundUsername = null;
  private int lastReqId;
  private int reqId = 0;
  
  private void putRecentHashtags(final ArrayList<HashtagObject> paramArrayList)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
          int i = 0;
          for (;;)
          {
            if ((i >= paramArrayList.size()) || (i == 100))
            {
              localSQLitePreparedStatement.dispose();
              MessagesStorage.getInstance().getDatabase().commitTransaction();
              if (paramArrayList.size() < 100) {
                break label202;
              }
              MessagesStorage.getInstance().getDatabase().beginTransaction();
              i = 100;
              while (i < paramArrayList.size())
              {
                MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((BaseSearchAdapter.HashtagObject)paramArrayList.get(i)).hashtag + "'").stepThis().dispose();
                i += 1;
              }
            }
            BaseSearchAdapter.HashtagObject localHashtagObject = (BaseSearchAdapter.HashtagObject)paramArrayList.get(i);
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindString(1, localHashtagObject.hashtag);
            localSQLitePreparedStatement.bindInteger(2, localHashtagObject.date);
            localSQLitePreparedStatement.step();
            i += 1;
          }
          MessagesStorage.getInstance().getDatabase().commitTransaction();
          label202:
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void addHashtagsFromMessage(String paramString)
  {
    if (paramString == null) {}
    int i;
    do
    {
      return;
      i = 0;
      Matcher localMatcher = Pattern.compile("(^|\\s)#[\\w@\\.]+").matcher(paramString);
      if (localMatcher.find())
      {
        int j = localMatcher.start();
        int k = localMatcher.end();
        i = j;
        if (paramString.charAt(j) != '@')
        {
          i = j;
          if (paramString.charAt(j) != '#') {
            i = j + 1;
          }
        }
        String str = paramString.substring(i, k);
        if (this.hashtagsByText == null)
        {
          this.hashtagsByText = new HashMap();
          this.hashtags = new ArrayList();
        }
        HashtagObject localHashtagObject = (HashtagObject)this.hashtagsByText.get(str);
        if (localHashtagObject == null)
        {
          localHashtagObject = new HashtagObject();
          localHashtagObject.hashtag = str;
          this.hashtagsByText.put(localHashtagObject.hashtag, localHashtagObject);
        }
        for (;;)
        {
          localHashtagObject.date = ((int)(System.currentTimeMillis() / 1000L));
          this.hashtags.add(0, localHashtagObject);
          i = 1;
          break;
          this.hashtags.remove(localHashtagObject);
        }
      }
    } while (i == 0);
    putRecentHashtags(this.hashtags);
  }
  
  public void clearRecentHashtags()
  {
    this.hashtags = new ArrayList();
    this.hashtagsByText = new HashMap();
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public void loadRecentHashtags()
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        final ArrayList localArrayList;
        final HashMap localHashMap;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
          localArrayList = new ArrayList();
          localHashMap = new HashMap();
          while (localSQLiteCursor.next())
          {
            BaseSearchAdapter.HashtagObject localHashtagObject = new BaseSearchAdapter.HashtagObject();
            localHashtagObject.hashtag = localSQLiteCursor.stringValue(0);
            localHashtagObject.date = localSQLiteCursor.intValue(1);
            localArrayList.add(localHashtagObject);
            localHashMap.put(localHashtagObject.hashtag, localHashtagObject);
          }
          localException.dispose();
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          return;
        }
        Collections.sort(localArrayList, new Comparator()
        {
          public int compare(BaseSearchAdapter.HashtagObject paramAnonymous2HashtagObject1, BaseSearchAdapter.HashtagObject paramAnonymous2HashtagObject2)
          {
            if (paramAnonymous2HashtagObject1.date < paramAnonymous2HashtagObject2.date) {
              return 1;
            }
            if (paramAnonymous2HashtagObject1.date > paramAnonymous2HashtagObject2.date) {
              return -1;
            }
            return 0;
          }
        });
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            BaseSearchAdapter.this.setHashtags(localArrayList, localHashMap);
          }
        });
      }
    });
  }
  
  public void queryServerSearch(final String paramString, final boolean paramBoolean1, final boolean paramBoolean2)
  {
    if (this.reqId != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
      this.reqId = 0;
    }
    if ((paramString == null) || (paramString.length() < 5))
    {
      this.globalSearch.clear();
      this.lastReqId = 0;
      notifyDataSetChanged();
      return;
    }
    TLRPC.TL_contacts_search localTL_contacts_search = new TLRPC.TL_contacts_search();
    localTL_contacts_search.q = paramString;
    localTL_contacts_search.limit = 50;
    final int i = this.lastReqId + 1;
    this.lastReqId = i;
    this.reqId = ConnectionsManager.getInstance().sendRequest(localTL_contacts_search, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if ((BaseSearchAdapter.1.this.val$currentReqId == BaseSearchAdapter.this.lastReqId) && (paramAnonymousTL_error == null))
            {
              TLRPC.TL_contacts_found localTL_contacts_found = (TLRPC.TL_contacts_found)paramAnonymousTLObject;
              BaseSearchAdapter.this.globalSearch.clear();
              if (BaseSearchAdapter.1.this.val$allowChats)
              {
                i = 0;
                while (i < localTL_contacts_found.chats.size())
                {
                  BaseSearchAdapter.this.globalSearch.add(localTL_contacts_found.chats.get(i));
                  i += 1;
                }
              }
              int i = 0;
              if (i < localTL_contacts_found.users.size())
              {
                if ((!BaseSearchAdapter.1.this.val$allowBots) && (((TLRPC.User)localTL_contacts_found.users.get(i)).bot)) {}
                for (;;)
                {
                  i += 1;
                  break;
                  BaseSearchAdapter.this.globalSearch.add(localTL_contacts_found.users.get(i));
                }
              }
              BaseSearchAdapter.this.lastFoundUsername = BaseSearchAdapter.1.this.val$query;
              BaseSearchAdapter.this.notifyDataSetChanged();
            }
            BaseSearchAdapter.access$102(BaseSearchAdapter.this, 0);
          }
        });
      }
    }, 2);
  }
  
  protected void setHashtags(ArrayList<HashtagObject> paramArrayList, HashMap<String, HashtagObject> paramHashMap)
  {
    this.hashtags = paramArrayList;
    this.hashtagsByText = paramHashMap;
    this.hashtagsLoadedFromDb = true;
  }
  
  protected static class HashtagObject
  {
    int date;
    String hashtag;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/BaseSearchAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */