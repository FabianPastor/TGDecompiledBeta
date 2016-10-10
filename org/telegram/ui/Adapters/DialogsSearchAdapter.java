package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_messages_searchGlobal;
import org.telegram.tgnet.TLRPC.TL_topPeer;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

public class DialogsSearchAdapter
  extends BaseSearchAdapterRecycler
{
  private DialogsSearchAdapterDelegate delegate;
  private int dialogsType;
  private String lastMessagesSearchString;
  private int lastReqId;
  private int lastSearchId = 0;
  private String lastSearchText;
  private Context mContext;
  private boolean messagesSearchEndReached;
  private int needMessagesSearch;
  private ArrayList<RecentSearchObject> recentSearchObjects = new ArrayList();
  private HashMap<Long, RecentSearchObject> recentSearchObjectsById = new HashMap();
  private int reqId = 0;
  private ArrayList<TLObject> searchResult = new ArrayList();
  private ArrayList<String> searchResultHashtags = new ArrayList();
  private ArrayList<MessageObject> searchResultMessages = new ArrayList();
  private ArrayList<CharSequence> searchResultNames = new ArrayList();
  private Timer searchTimer;
  
  public DialogsSearchAdapter(Context paramContext, int paramInt1, int paramInt2)
  {
    this.mContext = paramContext;
    this.needMessagesSearch = paramInt1;
    this.dialogsType = paramInt2;
    loadRecentSearch();
    SearchQuery.loadHints(true);
  }
  
  private void searchDialogsInternal(final String paramString, final int paramInt)
  {
    if (this.needMessagesSearch == 2) {
      return;
    }
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        long l;
        int k;
        try
        {
          localObject4 = paramString.trim().toLowerCase();
          if (((String)localObject4).length() == 0)
          {
            DialogsSearchAdapter.access$802(DialogsSearchAdapter.this, -1);
            DialogsSearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList(), new ArrayList(), DialogsSearchAdapter.this.lastSearchId);
            return;
          }
          localObject3 = LocaleController.getInstance().getTranslitString((String)localObject4);
          if (((String)localObject4).equals(localObject3)) {
            break label2433;
          }
          localObject1 = localObject3;
          if (((String)localObject3).length() != 0) {
            break label2436;
          }
        }
        catch (Exception localException)
        {
          Object localObject1;
          FileLog.e("tmessages", localException);
          return;
        }
        String[] arrayOfString = new String[i + 1];
        arrayOfString[0] = localObject4;
        if (localObject1 != null) {
          arrayOfString[1] = localObject1;
        }
        localObject1 = new ArrayList();
        Object localObject6 = new ArrayList();
        Object localObject5 = new ArrayList();
        Object localObject4 = new ArrayList();
        int i = 0;
        int j = 0;
        HashMap localHashMap = new HashMap();
        Object localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
        Object localObject7;
        int m;
        for (;;)
        {
          if (!((SQLiteCursor)localObject3).next()) {
            break label421;
          }
          l = ((SQLiteCursor)localObject3).longValue(0);
          localObject7 = new DialogsSearchAdapter.DialogSearchResult(DialogsSearchAdapter.this, null);
          ((DialogsSearchAdapter.DialogSearchResult)localObject7).date = ((SQLiteCursor)localObject3).intValue(1);
          localHashMap.put(Long.valueOf(l), localObject7);
          k = (int)l;
          m = (int)(l >> 32);
          if (k != 0)
          {
            if (m == 1)
            {
              if ((DialogsSearchAdapter.this.dialogsType != 0) || (((ArrayList)localObject6).contains(Integer.valueOf(k)))) {
                continue;
              }
              ((ArrayList)localObject6).add(Integer.valueOf(k));
              continue;
              label312:
              i = 0;
              break;
            }
            if (k > 0)
            {
              if ((DialogsSearchAdapter.this.dialogsType == 2) || (localException.contains(Integer.valueOf(k)))) {
                continue;
              }
              localException.add(Integer.valueOf(k));
              continue;
            }
            if (((ArrayList)localObject6).contains(Integer.valueOf(-k))) {
              continue;
            }
            ((ArrayList)localObject6).add(Integer.valueOf(-k));
            continue;
          }
          if ((DialogsSearchAdapter.this.dialogsType == 0) && (!((ArrayList)localObject5).contains(Integer.valueOf(m)))) {
            ((ArrayList)localObject5).add(Integer.valueOf(m));
          }
        }
        label421:
        ((SQLiteCursor)localObject3).dispose();
        label473:
        String str1;
        label552:
        String str2;
        if (!localException.isEmpty())
        {
          localObject7 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[] { TextUtils.join(",", localException) }), new Object[0]);
          i = j;
          while (((SQLiteCursor)localObject7).next())
          {
            str1 = ((SQLiteCursor)localObject7).stringValue(2);
            localObject3 = LocaleController.getInstance().getTranslitString(str1);
            localObject2 = localObject3;
            if (str1.equals(localObject3)) {
              localObject2 = null;
            }
            localObject3 = null;
            j = str1.lastIndexOf(";;;");
            if (j != -1) {
              localObject3 = str1.substring(j + 3);
            }
            m = 0;
            int n = arrayOfString.length;
            k = 0;
            if (k >= n) {
              break label2459;
            }
            str2 = arrayOfString[k];
            if ((str1.startsWith(str2)) || (str1.contains(" " + str2))) {
              break label2446;
            }
            if (localObject2 != null)
            {
              if (((String)localObject2).startsWith(str2)) {
                break label2446;
              }
              if (((String)localObject2).contains(" " + str2))
              {
                break label2446;
                label648:
                if (j == 0) {
                  break label2451;
                }
                localObject3 = ((SQLiteCursor)localObject7).byteBufferValue(0);
                if (localObject3 == null) {
                  continue;
                }
                localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                ((NativeByteBuffer)localObject3).reuse();
                localObject3 = (DialogsSearchAdapter.DialogSearchResult)localHashMap.get(Long.valueOf(((TLRPC.User)localObject2).id));
                if (((TLRPC.User)localObject2).status != null) {
                  ((TLRPC.User)localObject2).status.expires = ((SQLiteCursor)localObject7).intValue(1);
                }
                if (j != 1) {
                  break label790;
                }
              }
            }
            label790:
            for (((DialogsSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name, str2);; ((DialogsSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject2).username, null, "@" + str2))
            {
              ((DialogsSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject2);
              i += 1;
              break;
              j = m;
              if (localObject3 == null) {
                break label648;
              }
              j = m;
              if (!((String)localObject3).startsWith(str2)) {
                break label648;
              }
              j = 2;
              break label648;
            }
          }
          ((SQLiteCursor)localObject7).dispose();
        }
        j = i;
        if (!((ArrayList)localObject6).isEmpty())
        {
          localObject6 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject6) }), new Object[0]);
          label900:
          while (((SQLiteCursor)localObject6).next())
          {
            localObject7 = ((SQLiteCursor)localObject6).stringValue(1);
            localObject3 = LocaleController.getInstance().getTranslitString((String)localObject7);
            localObject2 = localObject3;
            if (((String)localObject7).equals(localObject3)) {
              localObject2 = null;
            }
            k = arrayOfString.length;
            j = 0;
            label949:
            if (j >= k) {
              break label2466;
            }
            localObject3 = arrayOfString[j];
            if ((!((String)localObject7).startsWith((String)localObject3)) && (!((String)localObject7).contains(" " + (String)localObject3)) && ((localObject2 == null) || ((!((String)localObject2).startsWith((String)localObject3)) && (!((String)localObject2).contains(" " + (String)localObject3))))) {
              break label2461;
            }
            localObject7 = ((SQLiteCursor)localObject6).byteBufferValue(0);
            if (localObject7 != null)
            {
              localObject2 = TLRPC.Chat.TLdeserialize((AbstractSerializedData)localObject7, ((NativeByteBuffer)localObject7).readInt32(false), false);
              ((NativeByteBuffer)localObject7).reuse();
              if ((localObject2 != null) && (!((TLRPC.Chat)localObject2).deactivated) && ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (!ChatObject.isNotInChat((TLRPC.Chat)localObject2))))
              {
                if (((TLRPC.Chat)localObject2).id > 0) {}
                for (l = -((TLRPC.Chat)localObject2).id;; l = AndroidUtilities.makeBroadcastId(((TLRPC.Chat)localObject2).id))
                {
                  localObject7 = (DialogsSearchAdapter.DialogSearchResult)localHashMap.get(Long.valueOf(l));
                  ((DialogsSearchAdapter.DialogSearchResult)localObject7).name = AndroidUtilities.generateSearchName(((TLRPC.Chat)localObject2).title, null, (String)localObject3);
                  ((DialogsSearchAdapter.DialogSearchResult)localObject7).object = ((TLObject)localObject2);
                  i += 1;
                  break;
                }
              }
            }
          }
          ((SQLiteCursor)localObject6).dispose();
          j = i;
        }
        i = j;
        if (!((ArrayList)localObject5).isEmpty())
        {
          localObject5 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject5) }), new Object[0]);
          label1232:
          while (((SQLiteCursor)localObject5).next())
          {
            localObject7 = ((SQLiteCursor)localObject5).stringValue(1);
            localObject3 = LocaleController.getInstance().getTranslitString((String)localObject7);
            localObject2 = localObject3;
            if (((String)localObject7).equals(localObject3)) {
              localObject2 = null;
            }
            localObject3 = null;
            i = ((String)localObject7).lastIndexOf(";;;");
            if (i == -1) {
              break label2468;
            }
            localObject3 = ((String)localObject7).substring(i + 2);
            break label2468;
            label1304:
            if (k >= arrayOfString.length) {
              break label2489;
            }
            localObject6 = arrayOfString[k];
            if ((((String)localObject7).startsWith((String)localObject6)) || (((String)localObject7).contains(" " + (String)localObject6))) {
              break label2476;
            }
            if (localObject2 != null)
            {
              if (((String)localObject2).startsWith((String)localObject6)) {
                break label2476;
              }
              if (((String)localObject2).contains(" " + (String)localObject6))
              {
                break label2476;
                label1401:
                if (i == 0) {
                  break label2481;
                }
                localObject2 = null;
                localObject3 = null;
                localObject7 = ((SQLiteCursor)localObject5).byteBufferValue(0);
                if (localObject7 != null)
                {
                  localObject2 = TLRPC.EncryptedChat.TLdeserialize((AbstractSerializedData)localObject7, ((NativeByteBuffer)localObject7).readInt32(false), false);
                  ((NativeByteBuffer)localObject7).reuse();
                }
                localObject7 = ((SQLiteCursor)localObject5).byteBufferValue(6);
                if (localObject7 != null)
                {
                  localObject3 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject7, ((NativeByteBuffer)localObject7).readInt32(false), false);
                  ((NativeByteBuffer)localObject7).reuse();
                }
                if ((localObject2 == null) || (localObject3 == null)) {
                  continue;
                }
                localObject7 = (DialogsSearchAdapter.DialogSearchResult)localHashMap.get(Long.valueOf(((TLRPC.EncryptedChat)localObject2).id << 32));
                ((TLRPC.EncryptedChat)localObject2).user_id = ((SQLiteCursor)localObject5).intValue(2);
                ((TLRPC.EncryptedChat)localObject2).a_or_b = ((SQLiteCursor)localObject5).byteArrayValue(3);
                ((TLRPC.EncryptedChat)localObject2).auth_key = ((SQLiteCursor)localObject5).byteArrayValue(4);
                ((TLRPC.EncryptedChat)localObject2).ttl = ((SQLiteCursor)localObject5).intValue(5);
                ((TLRPC.EncryptedChat)localObject2).layer = ((SQLiteCursor)localObject5).intValue(8);
                ((TLRPC.EncryptedChat)localObject2).seq_in = ((SQLiteCursor)localObject5).intValue(9);
                ((TLRPC.EncryptedChat)localObject2).seq_out = ((SQLiteCursor)localObject5).intValue(10);
                k = ((SQLiteCursor)localObject5).intValue(11);
                ((TLRPC.EncryptedChat)localObject2).key_use_count_in = ((short)(k >> 16));
                ((TLRPC.EncryptedChat)localObject2).key_use_count_out = ((short)k);
                ((TLRPC.EncryptedChat)localObject2).exchange_id = ((SQLiteCursor)localObject5).longValue(12);
                ((TLRPC.EncryptedChat)localObject2).key_create_date = ((SQLiteCursor)localObject5).intValue(13);
                ((TLRPC.EncryptedChat)localObject2).future_key_fingerprint = ((SQLiteCursor)localObject5).longValue(14);
                ((TLRPC.EncryptedChat)localObject2).future_auth_key = ((SQLiteCursor)localObject5).byteArrayValue(15);
                ((TLRPC.EncryptedChat)localObject2).key_hash = ((SQLiteCursor)localObject5).byteArrayValue(16);
                if (((TLRPC.User)localObject3).status != null) {
                  ((TLRPC.User)localObject3).status.expires = ((SQLiteCursor)localObject5).intValue(7);
                }
                if (i != 1) {
                  break label1795;
                }
              }
            }
            label1795:
            for (((DialogsSearchAdapter.DialogSearchResult)localObject7).name = AndroidUtilities.replaceTags("<c#ff00a60e>" + ContactsController.formatName(((TLRPC.User)localObject3).first_name, ((TLRPC.User)localObject3).last_name) + "</c>");; ((DialogsSearchAdapter.DialogSearchResult)localObject7).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject3).username, null, "@" + (String)localObject6))
            {
              ((DialogsSearchAdapter.DialogSearchResult)localObject7).object = ((TLObject)localObject2);
              ((ArrayList)localObject4).add(localObject3);
              j += 1;
              break;
              i = m;
              if (localObject3 == null) {
                break label1401;
              }
              i = m;
              if (!((String)localObject3).startsWith((String)localObject6)) {
                break label1401;
              }
              i = 2;
              break label1401;
            }
          }
          ((SQLiteCursor)localObject5).dispose();
          i = j;
        }
        Object localObject2 = new ArrayList(i);
        localObject3 = localHashMap.values().iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject5 = (DialogsSearchAdapter.DialogSearchResult)((Iterator)localObject3).next();
          if ((((DialogsSearchAdapter.DialogSearchResult)localObject5).object != null) && (((DialogsSearchAdapter.DialogSearchResult)localObject5).name != null)) {
            ((ArrayList)localObject2).add(localObject5);
          }
        }
        Collections.sort((List)localObject2, new Comparator()
        {
          public int compare(DialogsSearchAdapter.DialogSearchResult paramAnonymous2DialogSearchResult1, DialogsSearchAdapter.DialogSearchResult paramAnonymous2DialogSearchResult2)
          {
            if (paramAnonymous2DialogSearchResult1.date < paramAnonymous2DialogSearchResult2.date) {
              return 1;
            }
            if (paramAnonymous2DialogSearchResult1.date > paramAnonymous2DialogSearchResult2.date) {
              return -1;
            }
            return 0;
          }
        });
        localObject5 = new ArrayList();
        localObject6 = new ArrayList();
        i = 0;
        while (i < ((ArrayList)localObject2).size())
        {
          localObject3 = (DialogsSearchAdapter.DialogSearchResult)((ArrayList)localObject2).get(i);
          ((ArrayList)localObject5).add(((DialogsSearchAdapter.DialogSearchResult)localObject3).object);
          ((ArrayList)localObject6).add(((DialogsSearchAdapter.DialogSearchResult)localObject3).name);
          i += 1;
        }
        if (DialogsSearchAdapter.this.dialogsType != 2) {
          localObject7 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
        }
        label2041:
        label2233:
        label2433:
        label2436:
        label2446:
        label2451:
        label2459:
        label2461:
        label2466:
        label2468:
        label2476:
        label2481:
        label2489:
        label2491:
        label2496:
        label2503:
        for (;;)
        {
          if (((SQLiteCursor)localObject7).next())
          {
            if (!localHashMap.containsKey(Long.valueOf(((SQLiteCursor)localObject7).intValue(3))))
            {
              str1 = ((SQLiteCursor)localObject7).stringValue(2);
              localObject3 = LocaleController.getInstance().getTranslitString(str1);
              localObject2 = localObject3;
              if (str1.equals(localObject3)) {
                localObject2 = null;
              }
              localObject3 = null;
              i = str1.lastIndexOf(";;;");
              if (i != -1) {
                localObject3 = str1.substring(i + 3);
              }
              k = 0;
              m = arrayOfString.length;
              j = 0;
            }
          }
          else {
            for (;;)
            {
              if (j >= m) {
                break label2503;
              }
              str2 = arrayOfString[j];
              if ((!str1.startsWith(str2)) && (!str1.contains(" " + str2))) {
                if (localObject2 != null)
                {
                  if (((String)localObject2).startsWith(str2)) {
                    break label2491;
                  }
                  if (((String)localObject2).contains(" " + str2)) {
                    break label2491;
                  }
                }
              }
              for (;;)
              {
                if (i == 0) {
                  break label2496;
                }
                localObject2 = ((SQLiteCursor)localObject7).byteBufferValue(0);
                if (localObject2 == null) {
                  break label2041;
                }
                localObject3 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
                ((NativeByteBuffer)localObject2).reuse();
                if (((TLRPC.User)localObject3).status != null) {
                  ((TLRPC.User)localObject3).status.expires = ((SQLiteCursor)localObject7).intValue(1);
                }
                if (i == 1) {
                  ((ArrayList)localObject6).add(AndroidUtilities.generateSearchName(((TLRPC.User)localObject3).first_name, ((TLRPC.User)localObject3).last_name, str2));
                }
                for (;;)
                {
                  ((ArrayList)localObject5).add(localObject3);
                  break;
                  i = k;
                  if (localObject3 == null) {
                    break label2233;
                  }
                  i = k;
                  if (!((String)localObject3).startsWith(str2)) {
                    break label2233;
                  }
                  i = 2;
                  break label2233;
                  ((ArrayList)localObject6).add(AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject3).username, null, "@" + str2));
                }
                ((SQLiteCursor)localObject7).dispose();
                DialogsSearchAdapter.this.updateSearchResults((ArrayList)localObject5, (ArrayList)localObject6, (ArrayList)localObject4, paramInt);
                return;
                localObject2 = null;
                if (localObject2 == null) {
                  break label312;
                }
                i = 1;
                break;
                j = 1;
                break label648;
                k += 1;
                m = j;
                break label552;
                break label473;
                j += 1;
                break label949;
                break label900;
                m = 0;
                k = 0;
                break label1304;
                i = 1;
                break label1401;
                k += 1;
                m = i;
                break label1304;
                break label1232;
                i = 1;
              }
              j += 1;
              k = i;
            }
          }
        }
      }
    });
  }
  
  private void searchMessagesInternal(String paramString)
  {
    if ((this.needMessagesSearch == 0) || (((this.lastMessagesSearchString == null) || (this.lastMessagesSearchString.length() == 0)) && ((paramString == null) || (paramString.length() == 0)))) {}
    do
    {
      return;
      if (this.reqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
        this.reqId = 0;
      }
      if ((paramString != null) && (paramString.length() != 0)) {
        break;
      }
      this.searchResultMessages.clear();
      this.lastReqId = 0;
      this.lastMessagesSearchString = null;
      notifyDataSetChanged();
    } while (this.delegate == null);
    this.delegate.searchStateChanged(false);
    return;
    final TLRPC.TL_messages_searchGlobal localTL_messages_searchGlobal = new TLRPC.TL_messages_searchGlobal();
    localTL_messages_searchGlobal.limit = 20;
    localTL_messages_searchGlobal.q = paramString;
    MessageObject localMessageObject;
    final int i;
    if ((this.lastMessagesSearchString != null) && (paramString.equals(this.lastMessagesSearchString)) && (!this.searchResultMessages.isEmpty()))
    {
      localMessageObject = (MessageObject)this.searchResultMessages.get(this.searchResultMessages.size() - 1);
      localTL_messages_searchGlobal.offset_id = localMessageObject.getId();
      localTL_messages_searchGlobal.offset_date = localMessageObject.messageOwner.date;
      if (localMessageObject.messageOwner.to_id.channel_id != 0) {
        i = -localMessageObject.messageOwner.to_id.channel_id;
      }
    }
    for (localTL_messages_searchGlobal.offset_peer = MessagesController.getInputPeer(i);; localTL_messages_searchGlobal.offset_peer = new TLRPC.TL_inputPeerEmpty())
    {
      this.lastMessagesSearchString = paramString;
      i = this.lastReqId + 1;
      this.lastReqId = i;
      if (this.delegate != null) {
        this.delegate.searchStateChanged(true);
      }
      this.reqId = ConnectionsManager.getInstance().sendRequest(localTL_messages_searchGlobal, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              boolean bool2 = true;
              Object localObject;
              if ((DialogsSearchAdapter.1.this.val$currentReqId == DialogsSearchAdapter.this.lastReqId) && (paramAnonymousTL_error == null))
              {
                TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)paramAnonymousTLObject;
                MessagesStorage.getInstance().putUsersAndChats(localmessages_Messages.users, localmessages_Messages.chats, true, true);
                MessagesController.getInstance().putUsers(localmessages_Messages.users, false);
                MessagesController.getInstance().putChats(localmessages_Messages.chats, false);
                if (DialogsSearchAdapter.1.this.val$req.offset_id == 0) {
                  DialogsSearchAdapter.this.searchResultMessages.clear();
                }
                int i = 0;
                if (i < localmessages_Messages.messages.size())
                {
                  TLRPC.Message localMessage = (TLRPC.Message)localmessages_Messages.messages.get(i);
                  DialogsSearchAdapter.this.searchResultMessages.add(new MessageObject(localMessage, null, false));
                  long l = MessageObject.getDialogId(localMessage);
                  if (localMessage.out)
                  {
                    localObject = MessagesController.getInstance().dialogs_read_outbox_max;
                    label182:
                    Integer localInteger2 = (Integer)((ConcurrentHashMap)localObject).get(Long.valueOf(l));
                    Integer localInteger1 = localInteger2;
                    if (localInteger2 == null)
                    {
                      localInteger1 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localMessage.out, l));
                      ((ConcurrentHashMap)localObject).put(Long.valueOf(l), localInteger1);
                    }
                    if (localInteger1.intValue() >= localMessage.id) {
                      break label276;
                    }
                  }
                  label276:
                  for (bool1 = true;; bool1 = false)
                  {
                    localMessage.unread = bool1;
                    i += 1;
                    break;
                    localObject = MessagesController.getInstance().dialogs_read_inbox_max;
                    break label182;
                  }
                }
                localObject = DialogsSearchAdapter.this;
                if (localmessages_Messages.messages.size() == 20) {
                  break label364;
                }
              }
              label364:
              for (boolean bool1 = bool2;; bool1 = false)
              {
                DialogsSearchAdapter.access$302((DialogsSearchAdapter)localObject, bool1);
                DialogsSearchAdapter.this.notifyDataSetChanged();
                if (DialogsSearchAdapter.this.delegate != null) {
                  DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                }
                DialogsSearchAdapter.access$502(DialogsSearchAdapter.this, 0);
                return;
              }
            }
          });
        }
      }, 2);
      return;
      if (localMessageObject.messageOwner.to_id.chat_id != 0)
      {
        i = -localMessageObject.messageOwner.to_id.chat_id;
        break;
      }
      i = localMessageObject.messageOwner.to_id.user_id;
      break;
      localTL_messages_searchGlobal.offset_date = 0;
      localTL_messages_searchGlobal.offset_id = 0;
    }
  }
  
  private void setRecentSearch(ArrayList<RecentSearchObject> paramArrayList, HashMap<Long, RecentSearchObject> paramHashMap)
  {
    this.recentSearchObjects = paramArrayList;
    this.recentSearchObjectsById = paramHashMap;
    int i = 0;
    if (i < this.recentSearchObjects.size())
    {
      paramArrayList = (RecentSearchObject)this.recentSearchObjects.get(i);
      if ((paramArrayList.object instanceof TLRPC.User)) {
        MessagesController.getInstance().putUser((TLRPC.User)paramArrayList.object, true);
      }
      for (;;)
      {
        i += 1;
        break;
        if ((paramArrayList.object instanceof TLRPC.Chat)) {
          MessagesController.getInstance().putChat((TLRPC.Chat)paramArrayList.object, true);
        } else if ((paramArrayList.object instanceof TLRPC.EncryptedChat)) {
          MessagesController.getInstance().putEncryptedChat((TLRPC.EncryptedChat)paramArrayList.object, true);
        }
      }
    }
    notifyDataSetChanged();
  }
  
  private void updateSearchResults(final ArrayList<TLObject> paramArrayList, final ArrayList<CharSequence> paramArrayList1, final ArrayList<TLRPC.User> paramArrayList2, final int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (paramInt != DialogsSearchAdapter.this.lastSearchId) {
          return;
        }
        int i = 0;
        if (i < paramArrayList.size())
        {
          Object localObject = (TLObject)paramArrayList.get(i);
          if ((localObject instanceof TLRPC.User))
          {
            localObject = (TLRPC.User)localObject;
            MessagesController.getInstance().putUser((TLRPC.User)localObject, true);
          }
          for (;;)
          {
            i += 1;
            break;
            if ((localObject instanceof TLRPC.Chat))
            {
              localObject = (TLRPC.Chat)localObject;
              MessagesController.getInstance().putChat((TLRPC.Chat)localObject, true);
            }
            else if ((localObject instanceof TLRPC.EncryptedChat))
            {
              localObject = (TLRPC.EncryptedChat)localObject;
              MessagesController.getInstance().putEncryptedChat((TLRPC.EncryptedChat)localObject, true);
            }
          }
        }
        MessagesController.getInstance().putUsers(paramArrayList2, true);
        DialogsSearchAdapter.access$1102(DialogsSearchAdapter.this, paramArrayList);
        DialogsSearchAdapter.access$1202(DialogsSearchAdapter.this, paramArrayList1);
        DialogsSearchAdapter.this.notifyDataSetChanged();
      }
    });
  }
  
  public void clearRecentHashtags()
  {
    super.clearRecentHashtags();
    this.searchResultHashtags.clear();
    notifyDataSetChanged();
  }
  
  public void clearRecentSearch()
  {
    this.recentSearchObjectsById = new HashMap();
    this.recentSearchObjects = new ArrayList();
    notifyDataSetChanged();
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }
  
  public Object getItem(int paramInt)
  {
    TLObject localTLObject = null;
    int i;
    Object localObject1;
    Object localObject2;
    if (isRecentSearchDisplayed()) {
      if (!SearchQuery.hints.isEmpty())
      {
        i = 2;
        localObject1 = localTLObject;
        if (paramInt > i)
        {
          localObject1 = localTLObject;
          if (paramInt - 1 - i < this.recentSearchObjects.size())
          {
            localTLObject = ((RecentSearchObject)this.recentSearchObjects.get(paramInt - 1 - i)).object;
            if (!(localTLObject instanceof TLRPC.User)) {
              break label117;
            }
            localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.User)localTLObject).id));
            localObject1 = localTLObject;
            if (localObject2 != null) {
              localObject1 = localObject2;
            }
          }
        }
      }
    }
    label117:
    label189:
    int k;
    int j;
    label254:
    label267:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              return localObject1;
              i = 0;
              break;
              localObject1 = localTLObject;
            } while (!(localTLObject instanceof TLRPC.Chat));
            localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(((TLRPC.Chat)localTLObject).id));
            localObject1 = localTLObject;
          } while (localObject2 == null);
          return localObject2;
          if (this.searchResultHashtags.isEmpty()) {
            break label189;
          }
          localObject1 = localTLObject;
        } while (paramInt <= 0);
        return this.searchResultHashtags.get(paramInt - 1);
        k = this.searchResult.size();
        if (this.globalSearch.isEmpty())
        {
          i = 0;
          if (!this.searchResultMessages.isEmpty()) {
            break label254;
          }
        }
        for (j = 0;; j = this.searchResultMessages.size() + 1)
        {
          if ((paramInt < 0) || (paramInt >= k)) {
            break label267;
          }
          return this.searchResult.get(paramInt);
          i = this.globalSearch.size() + 1;
          break;
        }
        if ((paramInt > k) && (paramInt < i + k)) {
          return this.globalSearch.get(paramInt - k - 1);
        }
        localObject1 = localTLObject;
      } while (paramInt <= i + k);
      localObject1 = localTLObject;
    } while (paramInt >= i + k + j);
    return this.searchResultMessages.get(paramInt - k - i - 1);
  }
  
  public int getItemCount()
  {
    int k = 0;
    int j = 0;
    int i;
    if (isRecentSearchDisplayed()) {
      if (!this.recentSearchObjects.isEmpty())
      {
        i = this.recentSearchObjects.size() + 1;
        if (!SearchQuery.hints.isEmpty()) {
          j = 2;
        }
        j = i + j;
      }
    }
    int m;
    do
    {
      return j;
      i = 0;
      break;
      if (!this.searchResultHashtags.isEmpty()) {
        return this.searchResultHashtags.size() + 1;
      }
      j = this.searchResult.size();
      int n = this.globalSearch.size();
      m = this.searchResultMessages.size();
      i = j;
      if (n != 0) {
        i = j + (n + 1);
      }
      j = i;
    } while (m == 0);
    if (this.messagesSearchEndReached) {}
    for (j = k;; j = 1) {
      return i + (m + 1 + j);
    }
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    int i;
    if (isRecentSearchDisplayed()) {
      if (!SearchQuery.hints.isEmpty())
      {
        i = 2;
        if (paramInt > i) {
          break label43;
        }
        if ((paramInt != i) && (paramInt % 2 != 0)) {
          break label41;
        }
      }
    }
    label41:
    label43:
    label61:
    int k;
    int j;
    label133:
    label146:
    do
    {
      do
      {
        return 1;
        i = 0;
        break;
        return 5;
        return 0;
        if (this.searchResultHashtags.isEmpty()) {
          break label61;
        }
      } while (paramInt == 0);
      return 4;
      k = this.searchResult.size();
      if (this.globalSearch.isEmpty())
      {
        i = 0;
        if (!this.searchResultMessages.isEmpty()) {
          break label133;
        }
      }
      for (j = 0;; j = this.searchResultMessages.size() + 1)
      {
        if (((paramInt < 0) || (paramInt >= k)) && ((paramInt <= k) || (paramInt >= i + k))) {
          break label146;
        }
        return 0;
        i = this.globalSearch.size() + 1;
        break;
      }
      if ((paramInt > i + k) && (paramInt < i + k + j)) {
        return 2;
      }
    } while ((j == 0) || (paramInt != i + k + j));
    return 3;
  }
  
  public String getLastSearchString()
  {
    return this.lastMessagesSearchString;
  }
  
  public boolean hasRecentRearch()
  {
    return (!this.recentSearchObjects.isEmpty()) || (!SearchQuery.hints.isEmpty());
  }
  
  public boolean isGlobalSearch(int paramInt)
  {
    return (paramInt > this.searchResult.size()) && (paramInt <= this.globalSearch.size() + this.searchResult.size());
  }
  
  public boolean isMessagesSearchEndReached()
  {
    return this.messagesSearchEndReached;
  }
  
  public boolean isRecentSearchDisplayed()
  {
    return (this.needMessagesSearch != 2) && ((this.lastSearchText == null) || (this.lastSearchText.length() == 0)) && ((!this.recentSearchObjects.isEmpty()) || (!SearchQuery.hints.isEmpty()));
  }
  
  public void loadMoreSearchMessages()
  {
    searchMessagesInternal(this.lastMessagesSearchString);
  }
  
  public void loadRecentSearch()
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1;
        Object localObject3;
        ArrayList localArrayList2;
        final HashMap localHashMap;
        long l;
        int i;
        Object localObject4;
        for (;;)
        {
          int j;
          int k;
          int m;
          try
          {
            localObject2 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
            localObject1 = new ArrayList();
            localObject3 = new ArrayList();
            localArrayList2 = new ArrayList();
            new ArrayList();
            ArrayList localArrayList1 = new ArrayList();
            localHashMap = new HashMap();
            if (!((SQLiteCursor)localObject2).next()) {
              break;
            }
            l = ((SQLiteCursor)localObject2).longValue(0);
            j = 0;
            k = (int)l;
            m = (int)(l >> 32);
            if (k == 0) {
              break label293;
            }
            if (m == 1)
            {
              i = j;
              if (DialogsSearchAdapter.this.dialogsType == 0)
              {
                i = j;
                if (!((ArrayList)localObject3).contains(Integer.valueOf(k)))
                {
                  ((ArrayList)localObject3).add(Integer.valueOf(k));
                  i = 1;
                }
              }
              if (i == 0) {
                continue;
              }
              localObject4 = new DialogsSearchAdapter.RecentSearchObject();
              ((DialogsSearchAdapter.RecentSearchObject)localObject4).did = l;
              ((DialogsSearchAdapter.RecentSearchObject)localObject4).date = ((SQLiteCursor)localObject2).intValue(1);
              localArrayList1.add(localObject4);
              localHashMap.put(Long.valueOf(((DialogsSearchAdapter.RecentSearchObject)localObject4).did), localObject4);
              continue;
            }
            if (k <= 0) {
              break label262;
            }
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            return;
          }
          i = j;
          if (DialogsSearchAdapter.this.dialogsType != 2)
          {
            i = j;
            if (!((ArrayList)localObject1).contains(Integer.valueOf(k)))
            {
              ((ArrayList)localObject1).add(Integer.valueOf(k));
              i = 1;
              continue;
              label262:
              i = j;
              if (!((ArrayList)localObject3).contains(Integer.valueOf(-k)))
              {
                ((ArrayList)localObject3).add(Integer.valueOf(-k));
                i = 1;
                continue;
                label293:
                i = j;
                if (DialogsSearchAdapter.this.dialogsType == 0)
                {
                  i = j;
                  if (!localArrayList2.contains(Integer.valueOf(m)))
                  {
                    localArrayList2.add(Integer.valueOf(m));
                    i = 1;
                  }
                }
              }
            }
          }
        }
        ((SQLiteCursor)localObject2).dispose();
        Object localObject2 = new ArrayList();
        if (!localArrayList2.isEmpty())
        {
          localObject4 = new ArrayList();
          MessagesStorage.getInstance().getEncryptedChatsInternal(TextUtils.join(",", localArrayList2), (ArrayList)localObject4, (ArrayList)localObject1);
          i = 0;
          while (i < ((ArrayList)localObject4).size())
          {
            ((DialogsSearchAdapter.RecentSearchObject)localHashMap.get(Long.valueOf(((TLRPC.EncryptedChat)((ArrayList)localObject4).get(i)).id << 32))).object = ((TLObject)((ArrayList)localObject4).get(i));
            i += 1;
          }
        }
        if (!((ArrayList)localObject3).isEmpty())
        {
          localArrayList2 = new ArrayList();
          MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", (Iterable)localObject3), localArrayList2);
          i = 0;
          if (i < localArrayList2.size())
          {
            localObject3 = (TLRPC.Chat)localArrayList2.get(i);
            if (((TLRPC.Chat)localObject3).id > 0) {}
            for (l = -((TLRPC.Chat)localObject3).id; ((TLRPC.Chat)localObject3).migrated_to != null; l = AndroidUtilities.makeBroadcastId(((TLRPC.Chat)localObject3).id))
            {
              localObject3 = (DialogsSearchAdapter.RecentSearchObject)localHashMap.remove(Long.valueOf(l));
              if (localObject3 == null) {
                break label693;
              }
              localException.remove(localObject3);
              break label693;
            }
            ((DialogsSearchAdapter.RecentSearchObject)localHashMap.get(Long.valueOf(l))).object = ((TLObject)localObject3);
            break label693;
          }
        }
        if (!((ArrayList)localObject1).isEmpty())
        {
          MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", (Iterable)localObject1), (ArrayList)localObject2);
          i = 0;
        }
        for (;;)
        {
          if (i < ((ArrayList)localObject2).size())
          {
            localObject1 = (TLRPC.User)((ArrayList)localObject2).get(i);
            localObject3 = (DialogsSearchAdapter.RecentSearchObject)localHashMap.get(Long.valueOf(((TLRPC.User)localObject1).id));
            if (localObject3 != null) {
              ((DialogsSearchAdapter.RecentSearchObject)localObject3).object = ((TLObject)localObject1);
            }
          }
          else
          {
            Collections.sort(localException, new Comparator()
            {
              public int compare(DialogsSearchAdapter.RecentSearchObject paramAnonymous2RecentSearchObject1, DialogsSearchAdapter.RecentSearchObject paramAnonymous2RecentSearchObject2)
              {
                if (paramAnonymous2RecentSearchObject1.date < paramAnonymous2RecentSearchObject2.date) {
                  return 1;
                }
                if (paramAnonymous2RecentSearchObject1.date > paramAnonymous2RecentSearchObject2.date) {
                  return -1;
                }
                return 0;
              }
            });
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                DialogsSearchAdapter.this.setRecentSearch(localException, localHashMap);
              }
            });
            return;
            label693:
            i += 1;
            break;
          }
          i += 1;
        }
      }
    });
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    Object localObject1;
    boolean bool1;
    label128:
    label147:
    label280:
    label286:
    int i;
    switch (paramViewHolder.getItemViewType())
    {
    case 3: 
    default: 
      return;
    case 0: 
      ProfileSearchCell localProfileSearchCell = (ProfileSearchCell)paramViewHolder.itemView;
      paramViewHolder = null;
      Object localObject2 = null;
      TLRPC.EncryptedChat localEncryptedChat = null;
      Object localObject7 = null;
      Object localObject6 = null;
      boolean bool2 = false;
      Object localObject5 = null;
      Object localObject8 = getItem(paramInt);
      Object localObject4;
      if ((localObject8 instanceof TLRPC.User))
      {
        paramViewHolder = (TLRPC.User)localObject8;
        localObject1 = paramViewHolder.username;
        localObject4 = localObject2;
        if (!isRecentSearchDisplayed()) {
          break label286;
        }
        bool2 = true;
        if (paramInt == getItemCount() - 1) {
          break label280;
        }
        bool1 = true;
        localProfileSearchCell.useSeparator = bool1;
        bool1 = bool2;
        localObject2 = localObject7;
        localObject5 = localObject6;
        if (paramViewHolder == null) {
          break label671;
        }
      }
      for (;;)
      {
        localProfileSearchCell.setData(paramViewHolder, localEncryptedChat, (CharSequence)localObject5, (CharSequence)localObject2, bool1);
        return;
        if ((localObject8 instanceof TLRPC.Chat))
        {
          localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(((TLRPC.Chat)localObject8).id));
          localObject4 = localObject1;
          if (localObject1 == null) {
            localObject4 = (TLRPC.Chat)localObject8;
          }
          localObject1 = ((TLRPC.Chat)localObject4).username;
          break;
        }
        localObject4 = localObject2;
        localObject1 = localObject5;
        if (!(localObject8 instanceof TLRPC.EncryptedChat)) {
          break;
        }
        localEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(((TLRPC.EncryptedChat)localObject8).id));
        paramViewHolder = MessagesController.getInstance().getUser(Integer.valueOf(localEncryptedChat.user_id));
        localObject4 = localObject2;
        localObject1 = localObject5;
        break;
        bool1 = false;
        break label128;
        int j = this.searchResult.size();
        if (this.globalSearch.isEmpty())
        {
          i = 0;
          if ((paramInt == getItemCount() - 1) || (paramInt == j - 1) || (paramInt == j + i - 1)) {
            break label518;
          }
        }
        for (bool1 = true;; bool1 = false)
        {
          localProfileSearchCell.useSeparator = bool1;
          if (paramInt >= this.searchResult.size()) {
            break label524;
          }
          localObject1 = (CharSequence)this.searchResultNames.get(paramInt);
          localObject5 = localObject1;
          localObject2 = localObject7;
          bool1 = bool2;
          if (localObject1 == null) {
            break;
          }
          localObject5 = localObject1;
          localObject2 = localObject7;
          bool1 = bool2;
          if (paramViewHolder == null) {
            break;
          }
          localObject5 = localObject1;
          localObject2 = localObject7;
          bool1 = bool2;
          if (paramViewHolder.username == null) {
            break;
          }
          localObject5 = localObject1;
          localObject2 = localObject7;
          bool1 = bool2;
          if (paramViewHolder.username.length() <= 0) {
            break;
          }
          localObject5 = localObject1;
          localObject2 = localObject7;
          bool1 = bool2;
          if (!((CharSequence)localObject1).toString().startsWith("@" + paramViewHolder.username)) {
            break;
          }
          localObject5 = null;
          localObject2 = localObject1;
          bool1 = bool2;
          break;
          i = this.globalSearch.size() + 1;
          break label307;
        }
        localObject5 = localObject6;
        localObject2 = localObject7;
        bool1 = bool2;
        if (paramInt <= this.searchResult.size()) {
          break label147;
        }
        localObject5 = localObject6;
        localObject2 = localObject7;
        bool1 = bool2;
        if (localObject1 == null) {
          break label147;
        }
        localObject5 = this.lastFoundUsername;
        localObject2 = localObject5;
        if (((String)localObject5).startsWith("@")) {
          localObject2 = ((String)localObject5).substring(1);
        }
        try
        {
          localObject2 = AndroidUtilities.replaceTags(String.format("<c#ff4d83b3>@%s</c>%s", new Object[] { ((String)localObject1).substring(0, ((String)localObject2).length()), ((String)localObject1).substring(((String)localObject2).length()) }));
          localObject5 = localObject6;
          bool1 = bool2;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          localObject5 = localObject6;
          Object localObject3 = localObject1;
          bool1 = bool2;
        }
        break label147;
        paramViewHolder = (RecyclerView.ViewHolder)localObject4;
      }
    case 1: 
      paramViewHolder = (GreySectionCell)paramViewHolder.itemView;
      if (isRecentSearchDisplayed())
      {
        if (!SearchQuery.hints.isEmpty()) {}
        for (i = 2; paramInt < i; i = 0)
        {
          paramViewHolder.setText(LocaleController.getString("ChatHints", 2131165493).toUpperCase());
          return;
        }
        paramViewHolder.setText(LocaleController.getString("Recent", 2131166148).toUpperCase());
        return;
      }
      if (!this.searchResultHashtags.isEmpty())
      {
        paramViewHolder.setText(LocaleController.getString("Hashtags", 2131165728).toUpperCase());
        return;
      }
      if ((!this.globalSearch.isEmpty()) && (paramInt == this.searchResult.size()))
      {
        paramViewHolder.setText(LocaleController.getString("GlobalSearch", 2131165715));
        return;
      }
      paramViewHolder.setText(LocaleController.getString("SearchMessages", 2131166212));
      return;
    case 2: 
      paramViewHolder = (DialogCell)paramViewHolder.itemView;
      if (paramInt != getItemCount() - 1) {}
      for (bool1 = true;; bool1 = false)
      {
        paramViewHolder.useSeparator = bool1;
        localObject1 = (MessageObject)getItem(paramInt);
        paramViewHolder.setDialog(((MessageObject)localObject1).getDialogId(), (MessageObject)localObject1, ((MessageObject)localObject1).messageOwner.date);
        return;
      }
    case 4: 
      label307:
      label518:
      label524:
      label671:
      paramViewHolder = (HashtagSearchCell)paramViewHolder.itemView;
      paramViewHolder.setText((CharSequence)this.searchResultHashtags.get(paramInt - 1));
      if (paramInt != this.searchResultHashtags.size()) {}
      for (bool1 = true;; bool1 = false)
      {
        paramViewHolder.setNeedDivider(bool1);
        return;
      }
    }
    ((CategoryAdapterRecycler)((RecyclerListView)paramViewHolder.itemView).getAdapter()).setIndex(paramInt / 2);
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup = null;
    switch (paramInt)
    {
    default: 
      if (paramInt == 5) {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      }
      break;
    }
    for (;;)
    {
      return new Holder(paramViewGroup);
      paramViewGroup = new ProfileSearchCell(this.mContext);
      paramViewGroup.setBackgroundResource(2130837796);
      break;
      paramViewGroup = new GreySectionCell(this.mContext);
      break;
      paramViewGroup = new DialogCell(this.mContext);
      break;
      paramViewGroup = new LoadingCell(this.mContext);
      break;
      paramViewGroup = new HashtagSearchCell(this.mContext);
      break;
      paramViewGroup = new RecyclerListView(this.mContext)
      {
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((getParent() != null) && (getParent().getParent() != null)) {
            getParent().getParent().requestDisallowInterceptTouchEvent(true);
          }
          return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
        }
      };
      paramViewGroup.setTag(Integer.valueOf(9));
      paramViewGroup.setItemAnimator(null);
      paramViewGroup.setLayoutAnimation(null);
      LinearLayoutManager local9 = new LinearLayoutManager(this.mContext)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      local9.setOrientation(0);
      paramViewGroup.setLayoutManager(local9);
      paramViewGroup.setAdapter(new CategoryAdapterRecycler(null));
      paramViewGroup.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (DialogsSearchAdapter.this.delegate != null) {
            DialogsSearchAdapter.this.delegate.didPressedOnSubDialog(((Integer)paramAnonymousView.getTag()).intValue());
          }
        }
      });
      paramViewGroup.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (DialogsSearchAdapter.this.delegate != null) {
            DialogsSearchAdapter.this.delegate.needRemoveHint(((Integer)paramAnonymousView.getTag()).intValue());
          }
          return true;
        }
      });
      break;
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
    }
  }
  
  public void putRecentSearch(final long paramLong, TLObject paramTLObject)
  {
    RecentSearchObject localRecentSearchObject = (RecentSearchObject)this.recentSearchObjectsById.get(Long.valueOf(paramLong));
    if (localRecentSearchObject == null)
    {
      localRecentSearchObject = new RecentSearchObject();
      this.recentSearchObjectsById.put(Long.valueOf(paramLong), localRecentSearchObject);
    }
    for (;;)
    {
      this.recentSearchObjects.add(0, localRecentSearchObject);
      localRecentSearchObject.did = paramLong;
      localRecentSearchObject.object = paramTLObject;
      localRecentSearchObject.date = ((int)(System.currentTimeMillis() / 1000L));
      notifyDataSetChanged();
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindLong(1, paramLong);
            localSQLitePreparedStatement.bindInteger(2, (int)(System.currentTimeMillis() / 1000L));
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
      return;
      this.recentSearchObjects.remove(localRecentSearchObject);
    }
  }
  
  public void searchDialogs(final String paramString)
  {
    if ((paramString != null) && (this.lastSearchText != null) && (paramString.equals(this.lastSearchText))) {
      return;
    }
    this.lastSearchText = paramString;
    try
    {
      if (this.searchTimer != null)
      {
        this.searchTimer.cancel();
        this.searchTimer = null;
      }
      if ((paramString == null) || (paramString.length() == 0))
      {
        this.hashtagsLoadedFromDb = false;
        this.searchResult.clear();
        this.searchResultNames.clear();
        this.searchResultHashtags.clear();
        if (this.needMessagesSearch != 2) {
          queryServerSearch(null, true);
        }
        searchMessagesInternal(null);
        notifyDataSetChanged();
        return;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
      if ((this.needMessagesSearch != 2) && (paramString.startsWith("#")) && (paramString.length() == 1))
      {
        this.messagesSearchEndReached = true;
        if (!this.hashtagsLoadedFromDb)
        {
          loadRecentHashtags();
          if (this.delegate != null) {
            this.delegate.searchStateChanged(true);
          }
          notifyDataSetChanged();
          return;
        }
        this.searchResultMessages.clear();
        this.searchResultHashtags.clear();
        i = 0;
        while (i < this.hashtags.size())
        {
          this.searchResultHashtags.add(((BaseSearchAdapterRecycler.HashtagObject)this.hashtags.get(i)).hashtag);
          i += 1;
        }
        if (this.delegate != null) {
          this.delegate.searchStateChanged(false);
        }
        notifyDataSetChanged();
        return;
      }
      this.searchResultHashtags.clear();
      notifyDataSetChanged();
      final int i = this.lastSearchId + 1;
      this.lastSearchId = i;
      this.searchTimer = new Timer();
      this.searchTimer.schedule(new TimerTask()
      {
        public void run()
        {
          try
          {
            cancel();
            DialogsSearchAdapter.this.searchTimer.cancel();
            DialogsSearchAdapter.access$1302(DialogsSearchAdapter.this, null);
            DialogsSearchAdapter.this.searchDialogsInternal(paramString, i);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (DialogsSearchAdapter.this.needMessagesSearch != 2) {
                  DialogsSearchAdapter.this.queryServerSearch(DialogsSearchAdapter.7.this.val$query, true);
                }
                DialogsSearchAdapter.this.searchMessagesInternal(DialogsSearchAdapter.7.this.val$query);
              }
            });
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
      }, 200L, 300L);
    }
  }
  
  public void setDelegate(DialogsSearchAdapterDelegate paramDialogsSearchAdapterDelegate)
  {
    this.delegate = paramDialogsSearchAdapterDelegate;
  }
  
  protected void setHashtags(ArrayList<BaseSearchAdapterRecycler.HashtagObject> paramArrayList, HashMap<String, BaseSearchAdapterRecycler.HashtagObject> paramHashMap)
  {
    super.setHashtags(paramArrayList, paramHashMap);
    int i = 0;
    while (i < paramArrayList.size())
    {
      this.searchResultHashtags.add(((BaseSearchAdapterRecycler.HashtagObject)paramArrayList.get(i)).hashtag);
      i += 1;
    }
    if (this.delegate != null) {
      this.delegate.searchStateChanged(false);
    }
    notifyDataSetChanged();
  }
  
  private class CategoryAdapterRecycler
    extends RecyclerView.Adapter
  {
    private CategoryAdapterRecycler() {}
    
    public int getItemCount()
    {
      return SearchQuery.hints.size();
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      HintDialogCell localHintDialogCell = (HintDialogCell)paramViewHolder.itemView;
      TLRPC.TL_topPeer localTL_topPeer = (TLRPC.TL_topPeer)SearchQuery.hints.get(paramInt);
      new TLRPC.TL_dialog();
      paramViewHolder = null;
      String str = null;
      paramInt = 0;
      Object localObject;
      if (localTL_topPeer.peer.user_id != 0)
      {
        paramInt = localTL_topPeer.peer.user_id;
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(localTL_topPeer.peer.user_id));
        localHintDialogCell.setTag(Integer.valueOf(paramInt));
        str = "";
        if (localObject == null) {
          break label205;
        }
        localObject = ContactsController.formatName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name);
      }
      for (;;)
      {
        localHintDialogCell.setDialog(paramInt, true, (CharSequence)localObject);
        return;
        if (localTL_topPeer.peer.channel_id != 0)
        {
          paramInt = -localTL_topPeer.peer.channel_id;
          paramViewHolder = MessagesController.getInstance().getChat(Integer.valueOf(localTL_topPeer.peer.channel_id));
          localObject = str;
          break;
        }
        localObject = str;
        if (localTL_topPeer.peer.chat_id == 0) {
          break;
        }
        paramInt = -localTL_topPeer.peer.chat_id;
        paramViewHolder = MessagesController.getInstance().getChat(Integer.valueOf(localTL_topPeer.peer.chat_id));
        localObject = str;
        break;
        label205:
        localObject = str;
        if (paramViewHolder != null) {
          localObject = paramViewHolder.title;
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new HintDialogCell(DialogsSearchAdapter.this.mContext);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0F), AndroidUtilities.dp(100.0F)));
      return new DialogsSearchAdapter.Holder(DialogsSearchAdapter.this, paramViewGroup);
    }
    
    public void setIndex(int paramInt)
    {
      notifyDataSetChanged();
    }
  }
  
  private class DialogSearchResult
  {
    public int date;
    public CharSequence name;
    public TLObject object;
    
    private DialogSearchResult() {}
  }
  
  public static abstract interface DialogsSearchAdapterDelegate
  {
    public abstract void didPressedOnSubDialog(int paramInt);
    
    public abstract void needRemoveHint(int paramInt);
    
    public abstract void searchStateChanged(boolean paramBoolean);
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
  
  protected static class RecentSearchObject
  {
    int date;
    long did;
    TLObject object;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/DialogsSearchAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */