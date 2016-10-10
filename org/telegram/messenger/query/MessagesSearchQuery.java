package org.telegram.messenger.query;

import android.text.TextUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class MessagesSearchQuery
{
  private static long lastMergeDialogId;
  private static int lastReqId;
  private static int lastReturnedNum;
  private static String lastSearchQuery;
  private static int mergeReqId;
  private static int[] messagesSearchCount = { 0, 0 };
  private static boolean[] messagesSearchEndReached = { 0, 0 };
  private static int reqId;
  private static ArrayList<MessageObject> searchResultMessages = new ArrayList();
  
  public static String getLastSearchQuery()
  {
    return lastSearchQuery;
  }
  
  private static int getMask()
  {
    int i = 0;
    if ((lastReturnedNum < searchResultMessages.size() - 1) || (messagesSearchEndReached[0] == 0) || (messagesSearchEndReached[1] == 0)) {
      i = 0x0 | 0x1;
    }
    int j = i;
    if (lastReturnedNum > 0) {
      j = i | 0x2;
    }
    return j;
  }
  
  public static void searchMessagesInChat(String paramString, long paramLong1, long paramLong2, int paramInt1, int paramInt2)
  {
    searchMessagesInChat(paramString, paramLong1, paramLong2, paramInt1, paramInt2, false);
  }
  
  private static void searchMessagesInChat(final String paramString, final long paramLong1, long paramLong2, final int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = 0;
    int m = 0;
    final long l2 = paramLong1;
    int i;
    if (!paramBoolean)
    {
      i = 1;
      if (reqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(reqId, true);
        reqId = 0;
      }
      if (mergeReqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(mergeReqId, true);
        mergeReqId = 0;
      }
      if (!TextUtils.isEmpty(paramString)) {
        break label583;
      }
      if (!searchResultMessages.isEmpty()) {
        break label80;
      }
    }
    label80:
    Object localObject;
    label583:
    label659:
    label675:
    do
    {
      return;
      i = 0;
      break;
      int k;
      long l1;
      if (paramInt2 == 1)
      {
        lastReturnedNum += 1;
        if (lastReturnedNum < searchResultMessages.size())
        {
          paramString = (MessageObject)searchResultMessages.get(lastReturnedNum);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramString.getId()), Integer.valueOf(getMask()), Long.valueOf(paramString.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]) });
          return;
        }
        if ((messagesSearchEndReached[0] != 0) && (paramLong2 == 0L) && (messagesSearchEndReached[1] != 0))
        {
          lastReturnedNum -= 1;
          return;
        }
        k = 0;
        localObject = lastSearchQuery;
        paramString = (MessageObject)searchResultMessages.get(searchResultMessages.size() - 1);
        if ((paramString.getDialogId() == paramLong1) && (messagesSearchEndReached[0] == 0))
        {
          j = paramString.getId();
          l1 = paramLong1;
        }
      }
      for (;;)
      {
        l2 = l1;
        if (messagesSearchEndReached[0] != 0)
        {
          l2 = l1;
          if (messagesSearchEndReached[1] == 0)
          {
            l2 = l1;
            if (paramLong2 != 0L) {
              l2 = paramLong2;
            }
          }
        }
        if ((l2 != paramLong1) || (k == 0)) {
          break label675;
        }
        if (paramLong2 == 0L) {
          break label659;
        }
        paramString = MessagesController.getInputPeer((int)paramLong2);
        if (paramString == null) {
          break;
        }
        TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
        localTL_messages_search.peer = paramString;
        lastMergeDialogId = paramLong2;
        localTL_messages_search.limit = 1;
        localTL_messages_search.q = ((String)localObject);
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
        mergeReqId = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                TLRPC.messages_Messages localmessages_Messages;
                int[] arrayOfInt;
                if (MessagesSearchQuery.lastMergeDialogId == MessagesSearchQuery.1.this.val$mergeDialogId)
                {
                  MessagesSearchQuery.access$102(0);
                  if (paramAnonymousTLObject != null)
                  {
                    localmessages_Messages = (TLRPC.messages_Messages)paramAnonymousTLObject;
                    MessagesSearchQuery.messagesSearchEndReached[1] = localmessages_Messages.messages.isEmpty();
                    arrayOfInt = MessagesSearchQuery.messagesSearchCount;
                    if (!(localmessages_Messages instanceof TLRPC.TL_messages_messagesSlice)) {
                      break label109;
                    }
                  }
                }
                label109:
                for (int i = localmessages_Messages.count;; i = localmessages_Messages.messages.size())
                {
                  arrayOfInt[1] = i;
                  MessagesSearchQuery.searchMessagesInChat(MessagesSearchQuery.1.this.val$req.q, MessagesSearchQuery.1.this.val$dialog_id, MessagesSearchQuery.1.this.val$mergeDialogId, MessagesSearchQuery.1.this.val$guid, MessagesSearchQuery.1.this.val$direction, true);
                  return;
                }
              }
            });
          }
        }, 2);
        return;
        if (paramString.getDialogId() == paramLong2) {
          j = paramString.getId();
        }
        l1 = paramLong2;
        messagesSearchEndReached[1] = false;
        continue;
        if (paramInt2 != 2) {
          break;
        }
        lastReturnedNum -= 1;
        if (lastReturnedNum < 0)
        {
          lastReturnedNum = 0;
          return;
        }
        if (lastReturnedNum >= searchResultMessages.size()) {
          lastReturnedNum = searchResultMessages.size() - 1;
        }
        paramString = (MessageObject)searchResultMessages.get(lastReturnedNum);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramString.getId()), Integer.valueOf(getMask()), Long.valueOf(paramString.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]) });
        return;
        k = i;
        j = m;
        l1 = l2;
        localObject = paramString;
        if (i != 0)
        {
          localObject = messagesSearchEndReached;
          messagesSearchEndReached[1] = false;
          localObject[0] = 0;
          localObject = messagesSearchCount;
          messagesSearchCount[1] = 0;
          localObject[0] = 0;
          searchResultMessages.clear();
          k = i;
          j = m;
          l1 = l2;
          localObject = paramString;
        }
      }
      lastMergeDialogId = 0L;
      messagesSearchEndReached[1] = true;
      messagesSearchCount[1] = 0;
      paramString = new TLRPC.TL_messages_search();
      paramString.peer = MessagesController.getInputPeer((int)l2);
    } while (paramString.peer == null);
    paramString.limit = 21;
    paramString.q = ((String)localObject);
    paramString.max_id = j;
    paramString.filter = new TLRPC.TL_inputMessagesFilterEmpty();
    paramInt2 = lastReqId + 1;
    lastReqId = paramInt2;
    lastSearchQuery = (String)localObject;
    reqId = ConnectionsManager.getInstance().sendRequest(paramString, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            Object localObject1;
            int i;
            int j;
            int m;
            label227:
            label258:
            int k;
            if (MessagesSearchQuery.2.this.val$currentReqId == MessagesSearchQuery.lastReqId)
            {
              MessagesSearchQuery.access$602(0);
              if (paramAnonymousTLObject != null)
              {
                localObject1 = (TLRPC.messages_Messages)paramAnonymousTLObject;
                MessagesStorage.getInstance().putUsersAndChats(((TLRPC.messages_Messages)localObject1).users, ((TLRPC.messages_Messages)localObject1).chats, true, true);
                MessagesController.getInstance().putUsers(((TLRPC.messages_Messages)localObject1).users, false);
                MessagesController.getInstance().putChats(((TLRPC.messages_Messages)localObject1).chats, false);
                if ((MessagesSearchQuery.2.this.val$req.max_id == 0) && (MessagesSearchQuery.2.this.val$queryWithDialogFinal == MessagesSearchQuery.2.this.val$dialog_id))
                {
                  MessagesSearchQuery.access$702(0);
                  MessagesSearchQuery.searchResultMessages.clear();
                  MessagesSearchQuery.messagesSearchCount[0] = 0;
                }
                i = 0;
                j = 0;
                while (j < Math.min(((TLRPC.messages_Messages)localObject1).messages.size(), 20))
                {
                  localObject2 = (TLRPC.Message)((TLRPC.messages_Messages)localObject1).messages.get(j);
                  i = 1;
                  MessagesSearchQuery.searchResultMessages.add(new MessageObject((TLRPC.Message)localObject2, null, false));
                  j += 1;
                }
                Object localObject2 = MessagesSearchQuery.messagesSearchEndReached;
                if (MessagesSearchQuery.2.this.val$queryWithDialogFinal != MessagesSearchQuery.2.this.val$dialog_id) {
                  break label426;
                }
                j = 0;
                if (((TLRPC.messages_Messages)localObject1).messages.size() == 21) {
                  break label431;
                }
                m = 1;
                localObject2[j] = m;
                localObject2 = MessagesSearchQuery.messagesSearchCount;
                if (MessagesSearchQuery.2.this.val$queryWithDialogFinal != MessagesSearchQuery.2.this.val$dialog_id) {
                  break label437;
                }
                j = 0;
                if (!(localObject1 instanceof TLRPC.TL_messages_messagesSlice)) {
                  break label442;
                }
                k = ((TLRPC.messages_Messages)localObject1).count;
                label272:
                localObject2[j] = k;
                if (!MessagesSearchQuery.searchResultMessages.isEmpty()) {
                  break label454;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(MessagesSearchQuery.2.this.val$guid), Integer.valueOf(0), Integer.valueOf(MessagesSearchQuery.access$900()), Long.valueOf(0L), Integer.valueOf(0), Integer.valueOf(0) });
              }
            }
            for (;;)
            {
              if ((MessagesSearchQuery.2.this.val$queryWithDialogFinal == MessagesSearchQuery.2.this.val$dialog_id) && (MessagesSearchQuery.messagesSearchEndReached[0] != 0) && (MessagesSearchQuery.2.this.val$mergeDialogId != 0L) && (MessagesSearchQuery.messagesSearchEndReached[1] == 0)) {
                MessagesSearchQuery.searchMessagesInChat(MessagesSearchQuery.lastSearchQuery, MessagesSearchQuery.2.this.val$dialog_id, MessagesSearchQuery.2.this.val$mergeDialogId, MessagesSearchQuery.2.this.val$guid, 0, true);
              }
              return;
              label426:
              j = 1;
              break;
              label431:
              m = 0;
              break label227;
              label437:
              j = 1;
              break label258;
              label442:
              k = ((TLRPC.messages_Messages)localObject1).messages.size();
              break label272;
              label454:
              if (i != 0)
              {
                if (MessagesSearchQuery.lastReturnedNum >= MessagesSearchQuery.searchResultMessages.size()) {
                  MessagesSearchQuery.access$702(MessagesSearchQuery.searchResultMessages.size() - 1);
                }
                localObject1 = (MessageObject)MessagesSearchQuery.searchResultMessages.get(MessagesSearchQuery.lastReturnedNum);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(MessagesSearchQuery.2.this.val$guid), Integer.valueOf(((MessageObject)localObject1).getId()), Integer.valueOf(MessagesSearchQuery.access$900()), Long.valueOf(((MessageObject)localObject1).getDialogId()), Integer.valueOf(MessagesSearchQuery.lastReturnedNum), Integer.valueOf(MessagesSearchQuery.messagesSearchCount[0] + MessagesSearchQuery.messagesSearchCount[1]) });
              }
            }
          }
        });
      }
    }, 2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/query/MessagesSearchQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */