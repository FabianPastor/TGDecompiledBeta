package org.telegram.messenger.query;

import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_channelMessages;
import org.telegram.tgnet.TLRPC.TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.User;
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
  private static HashMap<Integer, MessageObject>[] searchResultMessagesMap = { new HashMap(), new HashMap() };
  
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
  
  public static boolean isMessageFound(int paramInt, boolean paramBoolean)
  {
    HashMap[] arrayOfHashMap = searchResultMessagesMap;
    if (paramBoolean) {}
    for (int i = 1;; i = 0) {
      return arrayOfHashMap[i].containsKey(Integer.valueOf(paramInt));
    }
  }
  
  public static void searchMessagesInChat(String paramString, long paramLong1, long paramLong2, int paramInt1, int paramInt2, TLRPC.User paramUser)
  {
    searchMessagesInChat(paramString, paramLong1, paramLong2, paramInt1, paramInt2, false, paramUser);
  }
  
  private static void searchMessagesInChat(String paramString, final long paramLong1, long paramLong2, final int paramInt1, int paramInt2, boolean paramBoolean, final TLRPC.User paramUser)
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
      if (paramString != null) {
        break label607;
      }
      if (!searchResultMessages.isEmpty()) {
        break label77;
      }
    }
    label77:
    Object localObject1;
    label277:
    final Object localObject2;
    label441:
    label468:
    label607:
    label727:
    label743:
    do
    {
      int k;
      long l1;
      do
      {
        return;
        i = 0;
        break;
        if (paramInt2 != 1) {
          break label468;
        }
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
        localObject1 = lastSearchQuery;
        paramString = (MessageObject)searchResultMessages.get(searchResultMessages.size() - 1);
        if ((paramString.getDialogId() != paramLong1) || (messagesSearchEndReached[0] != 0)) {
          break label441;
        }
        j = paramString.getId();
        l1 = paramLong1;
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
          break label743;
        }
        if (paramLong2 == 0L) {
          break label727;
        }
        localObject2 = MessagesController.getInputPeer((int)paramLong2);
      } while (localObject2 == null);
      paramString = new TLRPC.TL_messages_search();
      paramString.peer = ((TLRPC.InputPeer)localObject2);
      lastMergeDialogId = paramLong2;
      paramString.limit = 1;
      if (localObject1 != null) {}
      for (;;)
      {
        paramString.q = ((String)localObject1);
        if (paramUser != null)
        {
          paramString.from_id = MessagesController.getInputUser(paramUser);
          paramString.flags |= 0x1;
        }
        paramString.filter = new TLRPC.TL_inputMessagesFilterEmpty();
        mergeReqId = ConnectionsManager.getInstance().sendRequest(paramString, new RequestDelegate()
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
                      break label116;
                    }
                  }
                }
                label116:
                for (int i = localmessages_Messages.count;; i = localmessages_Messages.messages.size())
                {
                  arrayOfInt[1] = i;
                  MessagesSearchQuery.searchMessagesInChat(MessagesSearchQuery.1.this.val$req.q, MessagesSearchQuery.1.this.val$dialog_id, MessagesSearchQuery.1.this.val$mergeDialogId, MessagesSearchQuery.1.this.val$guid, MessagesSearchQuery.1.this.val$direction, true, MessagesSearchQuery.1.this.val$user);
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
        break label277;
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
        localObject1 = paramString;
        if (i == 0) {
          break label277;
        }
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsLoading, new Object[] { Integer.valueOf(paramInt1) });
        localObject1 = messagesSearchEndReached;
        messagesSearchEndReached[1] = false;
        localObject1[0] = 0;
        localObject1 = messagesSearchCount;
        messagesSearchCount[1] = 0;
        localObject1[0] = 0;
        searchResultMessages.clear();
        searchResultMessagesMap[0].clear();
        searchResultMessagesMap[1].clear();
        k = i;
        j = m;
        l1 = l2;
        localObject1 = paramString;
        break label277;
        localObject1 = "";
      }
      lastMergeDialogId = 0L;
      messagesSearchEndReached[1] = true;
      messagesSearchCount[1] = 0;
      localObject2 = new TLRPC.TL_messages_search();
      ((TLRPC.TL_messages_search)localObject2).peer = MessagesController.getInputPeer((int)l2);
    } while (((TLRPC.TL_messages_search)localObject2).peer == null);
    ((TLRPC.TL_messages_search)localObject2).limit = 21;
    if (localObject1 != null) {}
    for (paramString = (String)localObject1;; paramString = "")
    {
      ((TLRPC.TL_messages_search)localObject2).q = paramString;
      ((TLRPC.TL_messages_search)localObject2).offset_id = j;
      if (paramUser != null)
      {
        ((TLRPC.TL_messages_search)localObject2).from_id = MessagesController.getInputUser(paramUser);
        ((TLRPC.TL_messages_search)localObject2).flags |= 0x1;
      }
      ((TLRPC.TL_messages_search)localObject2).filter = new TLRPC.TL_inputMessagesFilterEmpty();
      paramInt2 = lastReqId + 1;
      lastReqId = paramInt2;
      lastSearchQuery = (String)localObject1;
      reqId = ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate()
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
              int k;
              int m;
              if (MessagesSearchQuery.2.this.val$currentReqId == MessagesSearchQuery.lastReqId)
              {
                MessagesSearchQuery.access$602(0);
                if (paramAnonymousTLObject != null)
                {
                  localObject1 = (TLRPC.messages_Messages)paramAnonymousTLObject;
                  for (i = 0; i < ((TLRPC.messages_Messages)localObject1).messages.size(); i = j + 1)
                  {
                    localObject2 = (TLRPC.Message)((TLRPC.messages_Messages)localObject1).messages.get(i);
                    if (!(localObject2 instanceof TLRPC.TL_messageEmpty))
                    {
                      j = i;
                      if (!(((TLRPC.Message)localObject2).action instanceof TLRPC.TL_messageActionHistoryClear)) {}
                    }
                    else
                    {
                      ((TLRPC.messages_Messages)localObject1).messages.remove(i);
                      j = i - 1;
                    }
                  }
                  MessagesStorage.getInstance().putUsersAndChats(((TLRPC.messages_Messages)localObject1).users, ((TLRPC.messages_Messages)localObject1).chats, true, true);
                  MessagesController.getInstance().putUsers(((TLRPC.messages_Messages)localObject1).users, false);
                  MessagesController.getInstance().putChats(((TLRPC.messages_Messages)localObject1).chats, false);
                  if ((MessagesSearchQuery.2.this.val$req.offset_id == 0) && (MessagesSearchQuery.2.this.val$queryWithDialogFinal == MessagesSearchQuery.2.this.val$dialog_id))
                  {
                    MessagesSearchQuery.access$702(0);
                    MessagesSearchQuery.searchResultMessages.clear();
                    MessagesSearchQuery.searchResultMessagesMap[0].clear();
                    MessagesSearchQuery.searchResultMessagesMap[1].clear();
                    MessagesSearchQuery.messagesSearchCount[0] = 0;
                  }
                  i = 0;
                  j = 0;
                  if (j < Math.min(((TLRPC.messages_Messages)localObject1).messages.size(), 20))
                  {
                    localObject2 = (TLRPC.Message)((TLRPC.messages_Messages)localObject1).messages.get(j);
                    k = 1;
                    localObject2 = new MessageObject((TLRPC.Message)localObject2, null, false);
                    MessagesSearchQuery.searchResultMessages.add(localObject2);
                    HashMap[] arrayOfHashMap = MessagesSearchQuery.searchResultMessagesMap;
                    if (MessagesSearchQuery.2.this.val$queryWithDialogFinal == MessagesSearchQuery.2.this.val$dialog_id) {}
                    for (i = 0;; i = 1)
                    {
                      arrayOfHashMap[i].put(Integer.valueOf(((MessageObject)localObject2).getId()), localObject2);
                      j += 1;
                      i = k;
                      break;
                    }
                  }
                  Object localObject2 = MessagesSearchQuery.messagesSearchEndReached;
                  if (MessagesSearchQuery.2.this.val$queryWithDialogFinal != MessagesSearchQuery.2.this.val$dialog_id) {
                    break label581;
                  }
                  j = 0;
                  if (((TLRPC.messages_Messages)localObject1).messages.size() == 21) {
                    break label586;
                  }
                  m = 1;
                  label367:
                  localObject2[j] = m;
                  localObject2 = MessagesSearchQuery.messagesSearchCount;
                  if (MessagesSearchQuery.2.this.val$queryWithDialogFinal != MessagesSearchQuery.2.this.val$dialog_id) {
                    break label592;
                  }
                  j = 0;
                  label398:
                  if ((!(localObject1 instanceof TLRPC.TL_messages_messagesSlice)) && (!(localObject1 instanceof TLRPC.TL_messages_channelMessages))) {
                    break label597;
                  }
                  k = ((TLRPC.messages_Messages)localObject1).count;
                  label420:
                  localObject2[j] = k;
                  if (!MessagesSearchQuery.searchResultMessages.isEmpty()) {
                    break label609;
                  }
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(MessagesSearchQuery.2.this.val$guid), Integer.valueOf(0), Integer.valueOf(MessagesSearchQuery.access$1000()), Long.valueOf(0L), Integer.valueOf(0), Integer.valueOf(0) });
                }
              }
              for (;;)
              {
                if ((MessagesSearchQuery.2.this.val$queryWithDialogFinal == MessagesSearchQuery.2.this.val$dialog_id) && (MessagesSearchQuery.messagesSearchEndReached[0] != 0) && (MessagesSearchQuery.2.this.val$mergeDialogId != 0L) && (MessagesSearchQuery.messagesSearchEndReached[1] == 0)) {
                  MessagesSearchQuery.searchMessagesInChat(MessagesSearchQuery.lastSearchQuery, MessagesSearchQuery.2.this.val$dialog_id, MessagesSearchQuery.2.this.val$mergeDialogId, MessagesSearchQuery.2.this.val$guid, 0, true, MessagesSearchQuery.2.this.val$user);
                }
                return;
                label581:
                j = 1;
                break;
                label586:
                m = 0;
                break label367;
                label592:
                j = 1;
                break label398;
                label597:
                k = ((TLRPC.messages_Messages)localObject1).messages.size();
                break label420;
                label609:
                if (i != 0)
                {
                  if (MessagesSearchQuery.lastReturnedNum >= MessagesSearchQuery.searchResultMessages.size()) {
                    MessagesSearchQuery.access$702(MessagesSearchQuery.searchResultMessages.size() - 1);
                  }
                  localObject1 = (MessageObject)MessagesSearchQuery.searchResultMessages.get(MessagesSearchQuery.lastReturnedNum);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(MessagesSearchQuery.2.this.val$guid), Integer.valueOf(((MessageObject)localObject1).getId()), Integer.valueOf(MessagesSearchQuery.access$1000()), Long.valueOf(((MessageObject)localObject1).getDialogId()), Integer.valueOf(MessagesSearchQuery.lastReturnedNum), Integer.valueOf(MessagesSearchQuery.messagesSearchCount[0] + MessagesSearchQuery.messagesSearchCount[1]) });
                }
              }
            }
          });
        }
      }, 2);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/query/MessagesSearchQuery.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */