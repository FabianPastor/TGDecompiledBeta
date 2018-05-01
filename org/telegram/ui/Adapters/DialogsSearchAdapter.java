package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LongSparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DialogsSearchAdapter
  extends RecyclerListView.SelectionAdapter
{
  private int currentAccount = UserConfig.selectedAccount;
  private DialogsSearchAdapterDelegate delegate;
  private int dialogsType;
  private RecyclerListView innerListView;
  private String lastMessagesSearchString;
  private int lastReqId;
  private int lastSearchId = 0;
  private String lastSearchText;
  private Context mContext;
  private boolean messagesSearchEndReached;
  private int needMessagesSearch;
  private ArrayList<RecentSearchObject> recentSearchObjects = new ArrayList();
  private LongSparseArray<RecentSearchObject> recentSearchObjectsById = new LongSparseArray();
  private int reqId = 0;
  private SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(false);
  private ArrayList<TLObject> searchResult = new ArrayList();
  private ArrayList<String> searchResultHashtags = new ArrayList();
  private ArrayList<MessageObject> searchResultMessages = new ArrayList();
  private ArrayList<CharSequence> searchResultNames = new ArrayList();
  private Timer searchTimer;
  private int selfUserId;
  
  public DialogsSearchAdapter(Context paramContext, int paramInt1, int paramInt2)
  {
    this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate()
    {
      public void onDataSetChanged()
      {
        DialogsSearchAdapter.this.notifyDataSetChanged();
      }
      
      public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramAnonymousArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramAnonymousHashMap)
      {
        for (int i = 0; i < paramAnonymousArrayList.size(); i++) {
          DialogsSearchAdapter.this.searchResultHashtags.add(((SearchAdapterHelper.HashtagObject)paramAnonymousArrayList.get(i)).hashtag);
        }
        if (DialogsSearchAdapter.this.delegate != null) {
          DialogsSearchAdapter.this.delegate.searchStateChanged(false);
        }
        DialogsSearchAdapter.this.notifyDataSetChanged();
      }
    });
    this.mContext = paramContext;
    this.needMessagesSearch = paramInt1;
    this.dialogsType = paramInt2;
    this.selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
    loadRecentSearch();
    DataQuery.getInstance(this.currentAccount).loadHints(true);
  }
  
  private void searchDialogsInternal(final String paramString, final int paramInt)
  {
    if (this.needMessagesSearch == 2) {}
    for (;;)
    {
      return;
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            localObject1 = LocaleController.getString("SavedMessages", NUM).toLowerCase();
            localObject2 = paramString.trim().toLowerCase();
            Object localObject4;
            if (((String)localObject2).length() == 0)
            {
              DialogsSearchAdapter.access$1002(DialogsSearchAdapter.this, -1);
              localObject3 = DialogsSearchAdapter.this;
              localObject4 = new java/util/ArrayList;
              ((ArrayList)localObject4).<init>();
              localObject6 = new java/util/ArrayList;
              ((ArrayList)localObject6).<init>();
              localObject7 = new java/util/ArrayList;
              ((ArrayList)localObject7).<init>();
              ((DialogsSearchAdapter)localObject3).updateSearchResults((ArrayList)localObject4, (ArrayList)localObject6, (ArrayList)localObject7, DialogsSearchAdapter.this.lastSearchId);
              return;
            }
            localObject3 = LocaleController.getInstance().getTranslitString((String)localObject2);
            if (!((String)localObject2).equals(localObject3))
            {
              localObject4 = localObject3;
              if (((String)localObject3).length() != 0) {}
            }
            else
            {
              localObject4 = null;
            }
            if (localObject4 != null)
            {
              i = 1;
              localObject7 = new String[i + 1];
              localObject7[0] = localObject2;
              if (localObject4 != null) {
                localObject7[1] = localObject4;
              }
              localObject4 = new java/util/ArrayList;
              ((ArrayList)localObject4).<init>();
              localObject8 = new java/util/ArrayList;
              ((ArrayList)localObject8).<init>();
              localObject9 = new java/util/ArrayList;
              ((ArrayList)localObject9).<init>();
              localArrayList = new java/util/ArrayList;
              localArrayList.<init>();
              i = 0;
              localObject6 = new android/util/LongSparseArray;
              ((LongSparseArray)localObject6).<init>();
              localObject3 = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 600", new Object[0]);
              for (;;)
              {
                if (!((SQLiteCursor)localObject3).next()) {
                  break label463;
                }
                l = ((SQLiteCursor)localObject3).longValue(0);
                localObject10 = new org/telegram/ui/Adapters/DialogsSearchAdapter$DialogSearchResult;
                ((DialogsSearchAdapter.DialogSearchResult)localObject10).<init>(DialogsSearchAdapter.this, null);
                ((DialogsSearchAdapter.DialogSearchResult)localObject10).date = ((SQLiteCursor)localObject3).intValue(1);
                ((LongSparseArray)localObject6).put(l, localObject10);
                j = (int)l;
                k = (int)(l >> 32);
                if (j == 0) {
                  break label426;
                }
                if (k != 1) {
                  break;
                }
                if ((DialogsSearchAdapter.this.dialogsType == 0) && (!((ArrayList)localObject8).contains(Integer.valueOf(j)))) {
                  ((ArrayList)localObject8).add(Integer.valueOf(j));
                }
              }
            }
          }
          catch (Exception localException)
          {
            for (;;)
            {
              Object localObject1;
              Object localObject2;
              Object localObject3;
              Object localObject6;
              Object localObject7;
              Object localObject8;
              Object localObject9;
              ArrayList localArrayList;
              long l;
              Object localObject10;
              int j;
              int k;
              FileLog.e(localException);
              continue;
              int i = 0;
              continue;
              if (j > 0)
              {
                if ((DialogsSearchAdapter.this.dialogsType != 2) && (!localException.contains(Integer.valueOf(j)))) {
                  localException.add(Integer.valueOf(j));
                }
              }
              else if (!((ArrayList)localObject8).contains(Integer.valueOf(-j)))
              {
                ((ArrayList)localObject8).add(Integer.valueOf(-j));
                continue;
                label426:
                if ((DialogsSearchAdapter.this.dialogsType == 0) && (!((ArrayList)localObject9).contains(Integer.valueOf(k))))
                {
                  ((ArrayList)localObject9).add(Integer.valueOf(k));
                  continue;
                  label463:
                  ((SQLiteCursor)localObject3).dispose();
                  if (((String)localObject1).startsWith((String)localObject2))
                  {
                    localObject2 = UserConfig.getInstance(DialogsSearchAdapter.this.currentAccount).getCurrentUser();
                    localObject3 = new org/telegram/ui/Adapters/DialogsSearchAdapter$DialogSearchResult;
                    ((DialogsSearchAdapter.DialogSearchResult)localObject3).<init>(DialogsSearchAdapter.this, null);
                    ((DialogsSearchAdapter.DialogSearchResult)localObject3).date = Integer.MAX_VALUE;
                    ((DialogsSearchAdapter.DialogSearchResult)localObject3).name = ((CharSequence)localObject1);
                    ((DialogsSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject2);
                    ((LongSparseArray)localObject6).put(((TLRPC.User)localObject2).id, localObject3);
                    i = 0 + 1;
                  }
                  k = i;
                  int m;
                  StringBuilder localStringBuilder;
                  if (!localException.isEmpty())
                  {
                    localObject1 = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[] { TextUtils.join(",", localException) }), new Object[0]);
                    label768:
                    label871:
                    label974:
                    while (((SQLiteCursor)localObject1).next())
                    {
                      localObject10 = ((SQLiteCursor)localObject1).stringValue(2);
                      localObject3 = LocaleController.getInstance().getTranslitString((String)localObject10);
                      localObject5 = localObject3;
                      if (((String)localObject10).equals(localObject3)) {
                        localObject5 = null;
                      }
                      localObject3 = null;
                      k = ((String)localObject10).lastIndexOf(";;;");
                      if (k != -1) {
                        localObject3 = ((String)localObject10).substring(k + 3);
                      }
                      m = 0;
                      int n = localObject7.length;
                      j = 0;
                      for (;;)
                      {
                        if (j >= n) {
                          break label974;
                        }
                        localObject2 = localObject7[j];
                        if (!((String)localObject10).startsWith((String)localObject2))
                        {
                          localStringBuilder = new java/lang/StringBuilder;
                          localStringBuilder.<init>();
                          if (!((String)localObject10).contains(" " + (String)localObject2))
                          {
                            if (localObject5 == null) {
                              break label871;
                            }
                            if (!((String)localObject5).startsWith((String)localObject2))
                            {
                              localStringBuilder = new java/lang/StringBuilder;
                              localStringBuilder.<init>();
                              if (!((String)localObject5).contains(" " + (String)localObject2)) {
                                break label871;
                              }
                            }
                          }
                        }
                        k = 1;
                        if (k != 0)
                        {
                          localObject3 = ((SQLiteCursor)localObject1).byteBufferValue(0);
                          if (localObject3 == null) {
                            break;
                          }
                          localObject5 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                          ((NativeByteBuffer)localObject3).reuse();
                          localObject3 = (DialogsSearchAdapter.DialogSearchResult)((LongSparseArray)localObject6).get(((TLRPC.User)localObject5).id);
                          if (((TLRPC.User)localObject5).status != null) {
                            ((TLRPC.User)localObject5).status.expires = ((SQLiteCursor)localObject1).intValue(1);
                          }
                          if (k == 1) {}
                          for (((DialogsSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName(((TLRPC.User)localObject5).first_name, ((TLRPC.User)localObject5).last_name, (String)localObject2);; ((DialogsSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName((String)localObject10, null, "@" + (String)localObject2))
                          {
                            ((DialogsSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject5);
                            i++;
                            break;
                            k = m;
                            if (localObject3 == null) {
                              break label768;
                            }
                            k = m;
                            if (!((String)localObject3).startsWith((String)localObject2)) {
                              break label768;
                            }
                            k = 2;
                            break label768;
                            localObject10 = new java/lang/StringBuilder;
                            ((StringBuilder)localObject10).<init>();
                            localObject10 = "@" + ((TLRPC.User)localObject5).username;
                            localStringBuilder = new java/lang/StringBuilder;
                            localStringBuilder.<init>();
                          }
                        }
                        j++;
                        m = k;
                      }
                    }
                    ((SQLiteCursor)localObject1).dispose();
                    k = i;
                  }
                  i = k;
                  if (!((ArrayList)localObject8).isEmpty())
                  {
                    localObject8 = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject8) }), new Object[0]);
                    i = k;
                    label1309:
                    while (((SQLiteCursor)localObject8).next())
                    {
                      localObject1 = ((SQLiteCursor)localObject8).stringValue(1);
                      localObject3 = LocaleController.getInstance().getTranslitString((String)localObject1);
                      localObject5 = localObject3;
                      if (((String)localObject1).equals(localObject3)) {
                        localObject5 = null;
                      }
                      j = localObject7.length;
                      for (k = 0;; k++)
                      {
                        if (k >= j) {
                          break label1309;
                        }
                        localObject3 = localObject7[k];
                        if (!((String)localObject1).startsWith((String)localObject3))
                        {
                          localObject2 = new java/lang/StringBuilder;
                          ((StringBuilder)localObject2).<init>();
                          if (!((String)localObject1).contains(" " + (String)localObject3))
                          {
                            if (localObject5 == null) {
                              continue;
                            }
                            if (!((String)localObject5).startsWith((String)localObject3))
                            {
                              localObject2 = new java/lang/StringBuilder;
                              ((StringBuilder)localObject2).<init>();
                              if (!((String)localObject5).contains(" " + (String)localObject3)) {
                                continue;
                              }
                            }
                          }
                        }
                        localObject1 = ((SQLiteCursor)localObject8).byteBufferValue(0);
                        if (localObject1 == null) {
                          break;
                        }
                        localObject5 = TLRPC.Chat.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
                        ((NativeByteBuffer)localObject1).reuse();
                        if ((localObject5 == null) || (((TLRPC.Chat)localObject5).deactivated) || ((ChatObject.isChannel((TLRPC.Chat)localObject5)) && (ChatObject.isNotInChat((TLRPC.Chat)localObject5)))) {
                          break;
                        }
                        if (((TLRPC.Chat)localObject5).id > 0) {}
                        for (l = -((TLRPC.Chat)localObject5).id;; l = AndroidUtilities.makeBroadcastId(((TLRPC.Chat)localObject5).id))
                        {
                          localObject1 = (DialogsSearchAdapter.DialogSearchResult)((LongSparseArray)localObject6).get(l);
                          ((DialogsSearchAdapter.DialogSearchResult)localObject1).name = AndroidUtilities.generateSearchName(((TLRPC.Chat)localObject5).title, null, (String)localObject3);
                          ((DialogsSearchAdapter.DialogSearchResult)localObject1).object = ((TLObject)localObject5);
                          i++;
                          break;
                        }
                      }
                    }
                    ((SQLiteCursor)localObject8).dispose();
                  }
                  k = i;
                  if (!((ArrayList)localObject9).isEmpty())
                  {
                    localObject9 = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash, q.in_seq_no, q.admin_id, q.mtproto_seq FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject9) }), new Object[0]);
                    label1542:
                    label1962:
                    label2061:
                    while (((SQLiteCursor)localObject9).next())
                    {
                      localObject1 = ((SQLiteCursor)localObject9).stringValue(1);
                      localObject3 = LocaleController.getInstance().getTranslitString((String)localObject1);
                      localObject5 = localObject3;
                      if (((String)localObject1).equals(localObject3)) {
                        localObject5 = null;
                      }
                      localObject3 = null;
                      k = ((String)localObject1).lastIndexOf(";;;");
                      if (k != -1) {
                        localObject3 = ((String)localObject1).substring(k + 2);
                      }
                      m = 0;
                      j = 0;
                      for (;;)
                      {
                        if (j >= localObject7.length) {
                          break label2061;
                        }
                        localObject8 = localObject7[j];
                        if (!((String)localObject1).startsWith((String)localObject8))
                        {
                          localObject2 = new java/lang/StringBuilder;
                          ((StringBuilder)localObject2).<init>();
                          if (!((String)localObject1).contains(" " + (String)localObject8))
                          {
                            if (localObject5 == null) {
                              break label1962;
                            }
                            if (!((String)localObject5).startsWith((String)localObject8))
                            {
                              localObject2 = new java/lang/StringBuilder;
                              ((StringBuilder)localObject2).<init>();
                              if (!((String)localObject5).contains(" " + (String)localObject8)) {
                                break label1962;
                              }
                            }
                          }
                        }
                        k = 1;
                        if (k != 0)
                        {
                          localObject5 = null;
                          localObject3 = null;
                          localObject1 = ((SQLiteCursor)localObject9).byteBufferValue(0);
                          if (localObject1 != null)
                          {
                            localObject5 = TLRPC.EncryptedChat.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
                            ((NativeByteBuffer)localObject1).reuse();
                          }
                          localObject1 = ((SQLiteCursor)localObject9).byteBufferValue(6);
                          if (localObject1 != null)
                          {
                            localObject3 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
                            ((NativeByteBuffer)localObject1).reuse();
                          }
                          if ((localObject5 == null) || (localObject3 == null)) {
                            break;
                          }
                          localObject1 = (DialogsSearchAdapter.DialogSearchResult)((LongSparseArray)localObject6).get(((TLRPC.EncryptedChat)localObject5).id << 32);
                          ((TLRPC.EncryptedChat)localObject5).user_id = ((SQLiteCursor)localObject9).intValue(2);
                          ((TLRPC.EncryptedChat)localObject5).a_or_b = ((SQLiteCursor)localObject9).byteArrayValue(3);
                          ((TLRPC.EncryptedChat)localObject5).auth_key = ((SQLiteCursor)localObject9).byteArrayValue(4);
                          ((TLRPC.EncryptedChat)localObject5).ttl = ((SQLiteCursor)localObject9).intValue(5);
                          ((TLRPC.EncryptedChat)localObject5).layer = ((SQLiteCursor)localObject9).intValue(8);
                          ((TLRPC.EncryptedChat)localObject5).seq_in = ((SQLiteCursor)localObject9).intValue(9);
                          ((TLRPC.EncryptedChat)localObject5).seq_out = ((SQLiteCursor)localObject9).intValue(10);
                          j = ((SQLiteCursor)localObject9).intValue(11);
                          ((TLRPC.EncryptedChat)localObject5).key_use_count_in = ((short)(short)(j >> 16));
                          ((TLRPC.EncryptedChat)localObject5).key_use_count_out = ((short)(short)j);
                          ((TLRPC.EncryptedChat)localObject5).exchange_id = ((SQLiteCursor)localObject9).longValue(12);
                          ((TLRPC.EncryptedChat)localObject5).key_create_date = ((SQLiteCursor)localObject9).intValue(13);
                          ((TLRPC.EncryptedChat)localObject5).future_key_fingerprint = ((SQLiteCursor)localObject9).longValue(14);
                          ((TLRPC.EncryptedChat)localObject5).future_auth_key = ((SQLiteCursor)localObject9).byteArrayValue(15);
                          ((TLRPC.EncryptedChat)localObject5).key_hash = ((SQLiteCursor)localObject9).byteArrayValue(16);
                          ((TLRPC.EncryptedChat)localObject5).in_seq_no = ((SQLiteCursor)localObject9).intValue(17);
                          j = ((SQLiteCursor)localObject9).intValue(18);
                          if (j != 0) {
                            ((TLRPC.EncryptedChat)localObject5).admin_id = j;
                          }
                          ((TLRPC.EncryptedChat)localObject5).mtproto_seq = ((SQLiteCursor)localObject9).intValue(19);
                          if (((TLRPC.User)localObject3).status != null) {
                            ((TLRPC.User)localObject3).status.expires = ((SQLiteCursor)localObject9).intValue(7);
                          }
                          if (k == 1)
                          {
                            localObject8 = new android/text/SpannableStringBuilder;
                            ((SpannableStringBuilder)localObject8).<init>(ContactsController.formatName(((TLRPC.User)localObject3).first_name, ((TLRPC.User)localObject3).last_name));
                            ((DialogsSearchAdapter.DialogSearchResult)localObject1).name = ((CharSequence)localObject8);
                            localObject8 = (SpannableStringBuilder)((DialogsSearchAdapter.DialogSearchResult)localObject1).name;
                            localObject2 = new android/text/style/ForegroundColorSpan;
                            ((ForegroundColorSpan)localObject2).<init>(Theme.getColor("chats_secretName"));
                            ((SpannableStringBuilder)localObject8).setSpan(localObject2, 0, ((DialogsSearchAdapter.DialogSearchResult)localObject1).name.length(), 33);
                          }
                          for (;;)
                          {
                            ((DialogsSearchAdapter.DialogSearchResult)localObject1).object = ((TLObject)localObject5);
                            localArrayList.add(localObject3);
                            i++;
                            break;
                            k = m;
                            if (localObject3 == null) {
                              break label1542;
                            }
                            k = m;
                            if (!((String)localObject3).startsWith((String)localObject8)) {
                              break label1542;
                            }
                            k = 2;
                            break label1542;
                            localObject2 = new java/lang/StringBuilder;
                            ((StringBuilder)localObject2).<init>();
                            localObject2 = "@" + ((TLRPC.User)localObject3).username;
                            localObject10 = new java/lang/StringBuilder;
                            ((StringBuilder)localObject10).<init>();
                            ((DialogsSearchAdapter.DialogSearchResult)localObject1).name = AndroidUtilities.generateSearchName((String)localObject2, null, "@" + (String)localObject8);
                          }
                        }
                        j++;
                        m = k;
                      }
                    }
                    ((SQLiteCursor)localObject9).dispose();
                    k = i;
                  }
                  Object localObject5 = new java/util/ArrayList;
                  ((ArrayList)localObject5).<init>(k);
                  for (i = 0; i < ((LongSparseArray)localObject6).size(); i++)
                  {
                    localObject3 = (DialogsSearchAdapter.DialogSearchResult)((LongSparseArray)localObject6).valueAt(i);
                    if ((((DialogsSearchAdapter.DialogSearchResult)localObject3).object != null) && (((DialogsSearchAdapter.DialogSearchResult)localObject3).name != null)) {
                      ((ArrayList)localObject5).add(localObject3);
                    }
                  }
                  localObject3 = new org/telegram/ui/Adapters/DialogsSearchAdapter$6$1;
                  ((1)localObject3).<init>(this);
                  Collections.sort((List)localObject5, (Comparator)localObject3);
                  localObject8 = new java/util/ArrayList;
                  ((ArrayList)localObject8).<init>();
                  localObject9 = new java/util/ArrayList;
                  ((ArrayList)localObject9).<init>();
                  for (i = 0; i < ((ArrayList)localObject5).size(); i++)
                  {
                    localObject3 = (DialogsSearchAdapter.DialogSearchResult)((ArrayList)localObject5).get(i);
                    ((ArrayList)localObject8).add(((DialogsSearchAdapter.DialogSearchResult)localObject3).object);
                    ((ArrayList)localObject9).add(((DialogsSearchAdapter.DialogSearchResult)localObject3).name);
                  }
                  if (DialogsSearchAdapter.this.dialogsType != 2)
                  {
                    localObject1 = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
                    label2448:
                    label2537:
                    label2636:
                    while (((SQLiteCursor)localObject1).next()) {
                      if (((LongSparseArray)localObject6).indexOfKey(((SQLiteCursor)localObject1).intValue(3)) < 0)
                      {
                        localObject10 = ((SQLiteCursor)localObject1).stringValue(2);
                        localObject3 = LocaleController.getInstance().getTranslitString((String)localObject10);
                        localObject5 = localObject3;
                        if (((String)localObject10).equals(localObject3)) {
                          localObject5 = null;
                        }
                        localObject3 = null;
                        i = ((String)localObject10).lastIndexOf(";;;");
                        if (i != -1) {
                          localObject3 = ((String)localObject10).substring(i + 3);
                        }
                        j = 0;
                        m = localObject7.length;
                        k = 0;
                        for (;;)
                        {
                          if (k >= m) {
                            break label2636;
                          }
                          localObject2 = localObject7[k];
                          if (!((String)localObject10).startsWith((String)localObject2))
                          {
                            localStringBuilder = new java/lang/StringBuilder;
                            localStringBuilder.<init>();
                            if (!((String)localObject10).contains(" " + (String)localObject2))
                            {
                              if (localObject5 == null) {
                                break label2537;
                              }
                              if (!((String)localObject5).startsWith((String)localObject2))
                              {
                                localStringBuilder = new java/lang/StringBuilder;
                                localStringBuilder.<init>();
                                if (!((String)localObject5).contains(" " + (String)localObject2)) {
                                  break label2537;
                                }
                              }
                            }
                          }
                          i = 1;
                          if (i != 0)
                          {
                            localObject3 = ((SQLiteCursor)localObject1).byteBufferValue(0);
                            if (localObject3 == null) {
                              break;
                            }
                            localObject5 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                            ((NativeByteBuffer)localObject3).reuse();
                            if (((TLRPC.User)localObject5).status != null) {
                              ((TLRPC.User)localObject5).status.expires = ((SQLiteCursor)localObject1).intValue(1);
                            }
                            if (i == 1) {
                              ((ArrayList)localObject9).add(AndroidUtilities.generateSearchName(((TLRPC.User)localObject5).first_name, ((TLRPC.User)localObject5).last_name, (String)localObject2));
                            }
                            for (;;)
                            {
                              ((ArrayList)localObject8).add(localObject5);
                              break;
                              i = j;
                              if (localObject3 == null) {
                                break label2448;
                              }
                              i = j;
                              if (!((String)localObject3).startsWith((String)localObject2)) {
                                break label2448;
                              }
                              i = 2;
                              break label2448;
                              localObject3 = new java/lang/StringBuilder;
                              ((StringBuilder)localObject3).<init>();
                              localObject10 = "@" + ((TLRPC.User)localObject5).username;
                              localObject3 = new java/lang/StringBuilder;
                              ((StringBuilder)localObject3).<init>();
                              ((ArrayList)localObject9).add(AndroidUtilities.generateSearchName((String)localObject10, null, "@" + (String)localObject2));
                            }
                          }
                          k++;
                          j = i;
                        }
                      }
                    }
                    ((SQLiteCursor)localObject1).dispose();
                  }
                  DialogsSearchAdapter.this.updateSearchResults((ArrayList)localObject8, (ArrayList)localObject9, localArrayList, paramInt);
                }
              }
            }
          }
        }
      });
    }
  }
  
  private void searchMessagesInternal(String paramString)
  {
    if ((this.needMessagesSearch == 0) || (((this.lastMessagesSearchString == null) || (this.lastMessagesSearchString.length() == 0)) && ((paramString == null) || (paramString.length() == 0)))) {}
    for (;;)
    {
      return;
      if (this.reqId != 0)
      {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
        this.reqId = 0;
      }
      if ((paramString != null) && (paramString.length() != 0)) {
        break;
      }
      this.searchResultMessages.clear();
      this.lastReqId = 0;
      this.lastMessagesSearchString = null;
      notifyDataSetChanged();
      if (this.delegate != null) {
        this.delegate.searchStateChanged(false);
      }
    }
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
    label227:
    for (localTL_messages_searchGlobal.offset_peer = MessagesController.getInstance(this.currentAccount).getInputPeer(i);; localTL_messages_searchGlobal.offset_peer = new TLRPC.TL_inputPeerEmpty())
    {
      this.lastMessagesSearchString = paramString;
      i = this.lastReqId + 1;
      this.lastReqId = i;
      if (this.delegate != null) {
        this.delegate.searchStateChanged(true);
      }
      this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_searchGlobal, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              boolean bool1 = true;
              Object localObject;
              if ((DialogsSearchAdapter.2.this.val$currentReqId == DialogsSearchAdapter.this.lastReqId) && (paramAnonymousTL_error == null))
              {
                TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)paramAnonymousTLObject;
                MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).putUsersAndChats(localmessages_Messages.users, localmessages_Messages.chats, true, true);
                MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putUsers(localmessages_Messages.users, false);
                MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putChats(localmessages_Messages.chats, false);
                if (DialogsSearchAdapter.2.this.val$req.offset_id == 0) {
                  DialogsSearchAdapter.this.searchResultMessages.clear();
                }
                int i = 0;
                if (i < localmessages_Messages.messages.size())
                {
                  TLRPC.Message localMessage = (TLRPC.Message)localmessages_Messages.messages.get(i);
                  DialogsSearchAdapter.this.searchResultMessages.add(new MessageObject(DialogsSearchAdapter.this.currentAccount, localMessage, false));
                  long l = MessageObject.getDialogId(localMessage);
                  if (localMessage.out)
                  {
                    localObject = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).dialogs_read_outbox_max;
                    label224:
                    Integer localInteger1 = (Integer)((ConcurrentHashMap)localObject).get(Long.valueOf(l));
                    Integer localInteger2 = localInteger1;
                    if (localInteger1 == null)
                    {
                      localInteger2 = Integer.valueOf(MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDialogReadMax(localMessage.out, l));
                      ((ConcurrentHashMap)localObject).put(Long.valueOf(l), localInteger2);
                    }
                    if (localInteger2.intValue() >= localMessage.id) {
                      break label339;
                    }
                  }
                  label339:
                  for (bool2 = true;; bool2 = false)
                  {
                    localMessage.unread = bool2;
                    i++;
                    break;
                    localObject = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).dialogs_read_inbox_max;
                    break label224;
                  }
                }
                localObject = DialogsSearchAdapter.this;
                if (localmessages_Messages.messages.size() == 20) {
                  break label429;
                }
              }
              label429:
              for (boolean bool2 = bool1;; bool2 = false)
              {
                DialogsSearchAdapter.access$602((DialogsSearchAdapter)localObject, bool2);
                DialogsSearchAdapter.this.notifyDataSetChanged();
                if (DialogsSearchAdapter.this.delegate != null) {
                  DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                }
                DialogsSearchAdapter.access$702(DialogsSearchAdapter.this, 0);
                return;
              }
            }
          });
        }
      }, 2);
      break;
      if (localMessageObject.messageOwner.to_id.chat_id != 0)
      {
        i = -localMessageObject.messageOwner.to_id.chat_id;
        break label227;
      }
      i = localMessageObject.messageOwner.to_id.user_id;
      break label227;
      localTL_messages_searchGlobal.offset_date = 0;
      localTL_messages_searchGlobal.offset_id = 0;
    }
  }
  
  private void setRecentSearch(ArrayList<RecentSearchObject> paramArrayList, LongSparseArray<RecentSearchObject> paramLongSparseArray)
  {
    this.recentSearchObjects = paramArrayList;
    this.recentSearchObjectsById = paramLongSparseArray;
    int i = 0;
    if (i < this.recentSearchObjects.size())
    {
      paramArrayList = (RecentSearchObject)this.recentSearchObjects.get(i);
      if ((paramArrayList.object instanceof TLRPC.User)) {
        MessagesController.getInstance(this.currentAccount).putUser((TLRPC.User)paramArrayList.object, true);
      }
      for (;;)
      {
        i++;
        break;
        if ((paramArrayList.object instanceof TLRPC.Chat)) {
          MessagesController.getInstance(this.currentAccount).putChat((TLRPC.Chat)paramArrayList.object, true);
        } else if ((paramArrayList.object instanceof TLRPC.EncryptedChat)) {
          MessagesController.getInstance(this.currentAccount).putEncryptedChat((TLRPC.EncryptedChat)paramArrayList.object, true);
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
        if (paramInt != DialogsSearchAdapter.this.lastSearchId) {}
        for (;;)
        {
          return;
          int i = 0;
          if (i < paramArrayList.size())
          {
            Object localObject = (TLObject)paramArrayList.get(i);
            if ((localObject instanceof TLRPC.User))
            {
              localObject = (TLRPC.User)localObject;
              MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putUser((TLRPC.User)localObject, true);
            }
            for (;;)
            {
              i++;
              break;
              if ((localObject instanceof TLRPC.Chat))
              {
                localObject = (TLRPC.Chat)localObject;
                MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putChat((TLRPC.Chat)localObject, true);
              }
              else if ((localObject instanceof TLRPC.EncryptedChat))
              {
                localObject = (TLRPC.EncryptedChat)localObject;
                MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putEncryptedChat((TLRPC.EncryptedChat)localObject, true);
              }
            }
          }
          MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).putUsers(paramArrayList2, true);
          DialogsSearchAdapter.access$1302(DialogsSearchAdapter.this, paramArrayList);
          DialogsSearchAdapter.access$1402(DialogsSearchAdapter.this, paramArrayList1);
          DialogsSearchAdapter.this.searchAdapterHelper.mergeResults(DialogsSearchAdapter.this.searchResult);
          DialogsSearchAdapter.this.notifyDataSetChanged();
        }
      }
    });
  }
  
  public void addHashtagsFromMessage(CharSequence paramCharSequence)
  {
    this.searchAdapterHelper.addHashtagsFromMessage(paramCharSequence);
  }
  
  public void clearRecentHashtags()
  {
    this.searchAdapterHelper.clearRecentHashtags();
    this.searchResultHashtags.clear();
    notifyDataSetChanged();
  }
  
  public void clearRecentSearch()
  {
    this.recentSearchObjectsById = new LongSparseArray();
    this.recentSearchObjects = new ArrayList();
    notifyDataSetChanged();
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
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
  
  public RecyclerListView getInnerListView()
  {
    return this.innerListView;
  }
  
  public Object getItem(int paramInt)
  {
    TLObject localTLObject = null;
    int i;
    Object localObject1;
    Object localObject2;
    if (isRecentSearchDisplayed()) {
      if (!DataQuery.getInstance(this.currentAccount).hints.isEmpty())
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
              break label121;
            }
            localObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC.User)localTLObject).id));
            localObject1 = localTLObject;
            if (localObject2 != null) {
              localObject1 = localObject2;
            }
          }
        }
      }
    }
    for (;;)
    {
      return localObject1;
      i = 0;
      break;
      label121:
      localObject1 = localTLObject;
      if ((localTLObject instanceof TLRPC.Chat))
      {
        localObject2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(((TLRPC.Chat)localTLObject).id));
        localObject1 = localTLObject;
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          continue;
          if (!this.searchResultHashtags.isEmpty())
          {
            localObject1 = localTLObject;
            if (paramInt > 0) {
              localObject1 = this.searchResultHashtags.get(paramInt - 1);
            }
          }
          else
          {
            localObject1 = this.searchAdapterHelper.getGlobalSearch();
            localObject2 = this.searchAdapterHelper.getLocalServerSearch();
            int j = this.searchResult.size();
            int k = ((ArrayList)localObject2).size();
            if (((ArrayList)localObject1).isEmpty())
            {
              i = 0;
              label244:
              if (!this.searchResultMessages.isEmpty()) {
                break label291;
              }
            }
            label291:
            for (int m = 0;; m = this.searchResultMessages.size() + 1)
            {
              if ((paramInt < 0) || (paramInt >= j)) {
                break label305;
              }
              localObject1 = this.searchResult.get(paramInt);
              break;
              i = ((ArrayList)localObject1).size() + 1;
              break label244;
            }
            label305:
            if ((paramInt >= j) && (paramInt < k + j))
            {
              localObject1 = ((ArrayList)localObject2).get(paramInt - j);
            }
            else if ((paramInt > j + k) && (paramInt < i + j + k))
            {
              localObject1 = ((ArrayList)localObject1).get(paramInt - j - k - 1);
            }
            else
            {
              localObject1 = localTLObject;
              if (paramInt > i + j + k)
              {
                localObject1 = localTLObject;
                if (paramInt < i + j + m + k) {
                  localObject1 = this.searchResultMessages.get(paramInt - j - i - k - 1);
                }
              }
            }
          }
        }
      }
    }
  }
  
  public int getItemCount()
  {
    int i = 0;
    int j = 0;
    if (isRecentSearchDisplayed()) {
      if (!this.recentSearchObjects.isEmpty())
      {
        k = this.recentSearchObjects.size() + 1;
        if (!DataQuery.getInstance(this.currentAccount).hints.isEmpty()) {
          j = 2;
        }
        k += j;
      }
    }
    label83:
    int n;
    do
    {
      for (;;)
      {
        return k;
        k = 0;
        break;
        if (this.searchResultHashtags.isEmpty()) {
          break label83;
        }
        k = this.searchResultHashtags.size() + 1;
      }
      k = this.searchResult.size();
      j = this.searchAdapterHelper.getLocalServerSearch().size();
      int m = this.searchAdapterHelper.getGlobalSearch().size();
      n = this.searchResultMessages.size();
      k += j;
      j = k;
      if (m != 0) {
        j = k + (m + 1);
      }
      k = j;
    } while (n == 0);
    if (this.messagesSearchEndReached) {}
    for (int k = i;; k = 1)
    {
      k = j + (n + 1 + k);
      break;
    }
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    int i = 1;
    int j;
    int k;
    if (isRecentSearchDisplayed()) {
      if (!DataQuery.getInstance(this.currentAccount).hints.isEmpty())
      {
        j = 2;
        if (paramInt > j) {
          break label63;
        }
        k = i;
        if (paramInt != j)
        {
          if (paramInt % 2 != 0) {
            break label57;
          }
          k = i;
        }
      }
    }
    for (;;)
    {
      return k;
      j = 0;
      break;
      label57:
      k = 5;
      continue;
      label63:
      k = 0;
      continue;
      if (!this.searchResultHashtags.isEmpty())
      {
        k = i;
        if (paramInt != 0) {
          k = 4;
        }
      }
      else
      {
        ArrayList localArrayList = this.searchAdapterHelper.getGlobalSearch();
        int m = this.searchResult.size();
        int n = this.searchAdapterHelper.getLocalServerSearch().size();
        if (localArrayList.isEmpty())
        {
          j = 0;
          label132:
          if (!this.searchResultMessages.isEmpty()) {
            break label195;
          }
        }
        label195:
        for (int i1 = 0;; i1 = this.searchResultMessages.size() + 1)
        {
          if (((paramInt < 0) || (paramInt >= m + n)) && ((paramInt <= m + n) || (paramInt >= j + m + n))) {
            break label209;
          }
          k = 0;
          break;
          j = localArrayList.size() + 1;
          break label132;
        }
        label209:
        if ((paramInt > j + m + n) && (paramInt < j + m + i1 + n))
        {
          k = 2;
        }
        else
        {
          k = i;
          if (i1 != 0)
          {
            k = i;
            if (paramInt == j + m + i1 + n) {
              k = 3;
            }
          }
        }
      }
    }
  }
  
  public String getLastSearchString()
  {
    return this.lastMessagesSearchString;
  }
  
  public boolean hasRecentRearch()
  {
    if ((!this.recentSearchObjects.isEmpty()) || (!DataQuery.getInstance(this.currentAccount).hints.isEmpty())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    boolean bool = true;
    int i = paramViewHolder.getItemViewType();
    if ((i != 1) && (i != 3)) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public boolean isMessagesSearchEndReached()
  {
    return this.messagesSearchEndReached;
  }
  
  public boolean isRecentSearchDisplayed()
  {
    if ((this.needMessagesSearch != 2) && ((this.lastSearchText == null) || (this.lastSearchText.length() == 0)) && ((!this.recentSearchObjects.isEmpty()) || (!DataQuery.getInstance(this.currentAccount).hints.isEmpty()))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void loadMoreSearchMessages()
  {
    searchMessagesInternal(this.lastMessagesSearchString);
  }
  
  public void loadRecentSearch()
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        Object localObject1;
        Object localObject2;
        Object localObject3;
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        long l;
        int i;
        int j;
        int k;
        int m;
        Object localObject4;
        try
        {
          localObject1 = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
          localObject2 = new java/util/ArrayList;
          ((ArrayList)localObject2).<init>();
          localObject3 = new java/util/ArrayList;
          ((ArrayList)localObject3).<init>();
          localArrayList1 = new java/util/ArrayList;
          localArrayList1.<init>();
          new ArrayList();
          localArrayList2 = new java/util/ArrayList;
          localArrayList2.<init>();
          LongSparseArray localLongSparseArray = new android/util/LongSparseArray;
          localLongSparseArray.<init>();
          for (;;)
          {
            if (((SQLiteCursor)localObject1).next())
            {
              l = ((SQLiteCursor)localObject1).longValue(0);
              i = 0;
              j = (int)l;
              k = (int)(l >> 32);
              if (j != 0) {
                if (k == 1)
                {
                  m = i;
                  if (DialogsSearchAdapter.this.dialogsType == 0)
                  {
                    m = i;
                    if (!((ArrayList)localObject3).contains(Integer.valueOf(j)))
                    {
                      ((ArrayList)localObject3).add(Integer.valueOf(j));
                      m = 1;
                    }
                  }
                  if (m == 0) {
                    continue;
                  }
                  localObject4 = new org/telegram/ui/Adapters/DialogsSearchAdapter$RecentSearchObject;
                  ((DialogsSearchAdapter.RecentSearchObject)localObject4).<init>();
                  ((DialogsSearchAdapter.RecentSearchObject)localObject4).did = l;
                  ((DialogsSearchAdapter.RecentSearchObject)localObject4).date = ((SQLiteCursor)localObject1).intValue(1);
                  localArrayList2.add(localObject4);
                  localLongSparseArray.put(((DialogsSearchAdapter.RecentSearchObject)localObject4).did, localObject4);
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
          if (j > 0)
          {
            m = i;
            if (DialogsSearchAdapter.this.dialogsType == 2) {
              break;
            }
            m = i;
            if (((ArrayList)localObject2).contains(Integer.valueOf(j))) {
              break;
            }
            ((ArrayList)localObject2).add(Integer.valueOf(j));
            m = 1;
            break;
          }
          m = i;
          if (((ArrayList)localObject3).contains(Integer.valueOf(-j))) {
            break;
          }
          ((ArrayList)localObject3).add(Integer.valueOf(-j));
          m = 1;
          break;
          m = i;
          if (DialogsSearchAdapter.this.dialogsType != 0) {
            break;
          }
          m = i;
          if (localArrayList1.contains(Integer.valueOf(k))) {
            break;
          }
          localArrayList1.add(Integer.valueOf(k));
          m = 1;
          break;
          ((SQLiteCursor)localObject1).dispose();
          localObject1 = new java/util/ArrayList;
          ((ArrayList)localObject1).<init>();
          if (!localArrayList1.isEmpty())
          {
            localObject4 = new java/util/ArrayList;
            ((ArrayList)localObject4).<init>();
            MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getEncryptedChatsInternal(TextUtils.join(",", localArrayList1), (ArrayList)localObject4, (ArrayList)localObject2);
            for (m = 0; m < ((ArrayList)localObject4).size(); m++) {
              ((DialogsSearchAdapter.RecentSearchObject)localException.get(((TLRPC.EncryptedChat)((ArrayList)localObject4).get(m)).id << 32)).object = ((TLObject)((ArrayList)localObject4).get(m));
            }
          }
          if (!((ArrayList)localObject3).isEmpty())
          {
            localArrayList1 = new java/util/ArrayList;
            localArrayList1.<init>();
            MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getChatsInternal(TextUtils.join(",", (Iterable)localObject3), localArrayList1);
            m = 0;
            if (m < localArrayList1.size())
            {
              localObject3 = (TLRPC.Chat)localArrayList1.get(m);
              if (((TLRPC.Chat)localObject3).id > 0)
              {
                l = -((TLRPC.Chat)localObject3).id;
                label543:
                if (((TLRPC.Chat)localObject3).migrated_to == null) {
                  break label597;
                }
                localObject3 = (DialogsSearchAdapter.RecentSearchObject)localException.get(l);
                localException.remove(l);
                if (localObject3 != null) {
                  localArrayList2.remove(localObject3);
                }
              }
              for (;;)
              {
                m++;
                break;
                l = AndroidUtilities.makeBroadcastId(((TLRPC.Chat)localObject3).id);
                break label543;
                label597:
                ((DialogsSearchAdapter.RecentSearchObject)localException.get(l)).object = ((TLObject)localObject3);
              }
            }
          }
          if (!((ArrayList)localObject2).isEmpty())
          {
            MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getUsersInternal(TextUtils.join(",", (Iterable)localObject2), (ArrayList)localObject1);
            for (m = 0; m < ((ArrayList)localObject1).size(); m++)
            {
              localObject2 = (TLRPC.User)((ArrayList)localObject1).get(m);
              localObject3 = (DialogsSearchAdapter.RecentSearchObject)localException.get(((TLRPC.User)localObject2).id);
              if (localObject3 != null) {
                ((DialogsSearchAdapter.RecentSearchObject)localObject3).object = ((TLObject)localObject2);
              }
            }
          }
          localObject1 = new org/telegram/ui/Adapters/DialogsSearchAdapter$3$1;
          ((1)localObject1).<init>(this);
          Collections.sort(localArrayList2, (Comparator)localObject1);
          localObject1 = new org/telegram/ui/Adapters/DialogsSearchAdapter$3$2;
          ((2)localObject1).<init>(this, localArrayList2, localException);
          AndroidUtilities.runOnUIThread((Runnable)localObject1);
        }
      }
    });
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    switch (paramViewHolder.getItemViewType())
    {
    }
    for (;;)
    {
      return;
      ProfileSearchCell localProfileSearchCell = (ProfileSearchCell)paramViewHolder.itemView;
      TLRPC.User localUser = null;
      paramViewHolder = null;
      TLRPC.EncryptedChat localEncryptedChat = null;
      Object localObject1 = null;
      Object localObject2 = null;
      boolean bool1 = false;
      Object localObject3 = null;
      Object localObject4 = getItem(paramInt);
      Object localObject5;
      Object localObject6;
      label105:
      boolean bool2;
      if ((localObject4 instanceof TLRPC.User))
      {
        localUser = (TLRPC.User)localObject4;
        localObject5 = localUser.username;
        localObject6 = paramViewHolder;
        if (!isRecentSearchDisplayed()) {
          break label433;
        }
        bool1 = true;
        if (paramInt == getItemCount() - 1) {
          break label427;
        }
        bool2 = true;
        label128:
        localProfileSearchCell.useSeparator = bool2;
        bool2 = bool1;
        localObject3 = localObject1;
        paramViewHolder = (RecyclerView.ViewHolder)localObject2;
        label145:
        boolean bool3 = false;
        localObject2 = paramViewHolder;
        localObject5 = localObject3;
        bool1 = bool3;
        if (localUser != null)
        {
          localObject2 = paramViewHolder;
          localObject5 = localObject3;
          bool1 = bool3;
          if (localUser.id == this.selfUserId)
          {
            localObject2 = LocaleController.getString("SavedMessages", NUM);
            localObject5 = null;
            bool1 = true;
          }
        }
        paramViewHolder = (RecyclerView.ViewHolder)localObject5;
        if (localObject6 != null)
        {
          paramViewHolder = (RecyclerView.ViewHolder)localObject5;
          if (((TLRPC.Chat)localObject6).participants_count != 0)
          {
            if ((!ChatObject.isChannel((TLRPC.Chat)localObject6)) || (((TLRPC.Chat)localObject6).megagroup)) {
              break label990;
            }
            paramViewHolder = LocaleController.formatPluralString("Subscribers", ((TLRPC.Chat)localObject6).participants_count);
            label251:
            if (!(localObject5 instanceof SpannableStringBuilder)) {
              break label1005;
            }
            ((SpannableStringBuilder)localObject5).append(", ").append(paramViewHolder);
            paramViewHolder = (RecyclerView.ViewHolder)localObject5;
          }
        }
        label278:
        if (localUser == null) {
          break label1042;
        }
        localObject6 = localUser;
      }
      label427:
      label433:
      int k;
      label472:
      label685:
      label691:
      label833:
      label965:
      label990:
      label1005:
      label1042:
      for (;;)
      {
        localProfileSearchCell.setData((TLObject)localObject6, localEncryptedChat, (CharSequence)localObject2, paramViewHolder, bool2, bool1);
        break;
        if ((localObject4 instanceof TLRPC.Chat))
        {
          paramViewHolder = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(((TLRPC.Chat)localObject4).id));
          localObject6 = paramViewHolder;
          if (paramViewHolder == null) {
            localObject6 = (TLRPC.Chat)localObject4;
          }
          localObject5 = ((TLRPC.Chat)localObject6).username;
          break label105;
        }
        localObject6 = paramViewHolder;
        localObject5 = localObject3;
        if (!(localObject4 instanceof TLRPC.EncryptedChat)) {
          break label105;
        }
        localEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(((TLRPC.EncryptedChat)localObject4).id));
        localUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(localEncryptedChat.user_id));
        localObject6 = paramViewHolder;
        localObject5 = localObject3;
        break label105;
        bool2 = false;
        break label128;
        paramViewHolder = this.searchAdapterHelper.getGlobalSearch();
        int i = this.searchResult.size();
        int j = this.searchAdapterHelper.getLocalServerSearch().size();
        if (paramViewHolder.isEmpty())
        {
          k = 0;
          if ((paramInt == getItemCount() - 1) || (paramInt == i + j - 1) || (paramInt == i + k + j - 1)) {
            break label685;
          }
        }
        for (bool2 = true;; bool2 = false)
        {
          localProfileSearchCell.useSeparator = bool2;
          if (paramInt >= this.searchResult.size()) {
            break label691;
          }
          localObject5 = (CharSequence)this.searchResultNames.get(paramInt);
          paramViewHolder = (RecyclerView.ViewHolder)localObject5;
          localObject3 = localObject1;
          bool2 = bool1;
          if (localObject5 == null) {
            break;
          }
          paramViewHolder = (RecyclerView.ViewHolder)localObject5;
          localObject3 = localObject1;
          bool2 = bool1;
          if (localUser == null) {
            break;
          }
          paramViewHolder = (RecyclerView.ViewHolder)localObject5;
          localObject3 = localObject1;
          bool2 = bool1;
          if (localUser.username == null) {
            break;
          }
          paramViewHolder = (RecyclerView.ViewHolder)localObject5;
          localObject3 = localObject1;
          bool2 = bool1;
          if (localUser.username.length() <= 0) {
            break;
          }
          paramViewHolder = (RecyclerView.ViewHolder)localObject5;
          localObject3 = localObject1;
          bool2 = bool1;
          if (!((CharSequence)localObject5).toString().startsWith("@" + localUser.username)) {
            break;
          }
          localObject3 = localObject5;
          paramViewHolder = null;
          bool2 = bool1;
          break;
          k = paramViewHolder.size() + 1;
          break label472;
        }
        localObject4 = this.searchAdapterHelper.getLastFoundUsername();
        paramViewHolder = (RecyclerView.ViewHolder)localObject2;
        localObject3 = localObject1;
        bool2 = bool1;
        if (TextUtils.isEmpty((CharSequence)localObject4)) {
          break label145;
        }
        paramViewHolder = null;
        localObject3 = null;
        if (localUser != null)
        {
          paramViewHolder = ContactsController.formatName(localUser.first_name, localUser.last_name);
          localObject3 = paramViewHolder.toLowerCase();
        }
        for (;;)
        {
          if (paramViewHolder == null) {
            break label833;
          }
          paramInt = ((String)localObject3).indexOf((String)localObject4);
          if (paramInt == -1) {
            break label833;
          }
          paramViewHolder = new SpannableStringBuilder(paramViewHolder);
          paramViewHolder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), paramInt, ((String)localObject4).length() + paramInt, 33);
          localObject3 = localObject1;
          bool2 = bool1;
          break;
          if (localObject6 != null)
          {
            paramViewHolder = ((TLRPC.Chat)localObject6).title;
            localObject3 = paramViewHolder.toLowerCase();
          }
        }
        paramViewHolder = (RecyclerView.ViewHolder)localObject2;
        localObject3 = localObject1;
        bool2 = bool1;
        if (localObject5 == null) {
          break label145;
        }
        paramViewHolder = (RecyclerView.ViewHolder)localObject4;
        if (((String)localObject4).startsWith("@")) {
          paramViewHolder = ((String)localObject4).substring(1);
        }
        try
        {
          localObject3 = new android/text/SpannableStringBuilder;
          ((SpannableStringBuilder)localObject3).<init>();
          ((SpannableStringBuilder)localObject3).append("@");
          ((SpannableStringBuilder)localObject3).append((CharSequence)localObject5);
          k = ((String)localObject5).toLowerCase().indexOf(paramViewHolder);
          if (k != -1)
          {
            paramInt = paramViewHolder.length();
            if (k != 0) {
              break label965;
            }
            paramInt++;
          }
          for (;;)
          {
            paramViewHolder = new android/text/style/ForegroundColorSpan;
            paramViewHolder.<init>(Theme.getColor("windowBackgroundWhiteBlueText4"));
            ((SpannableStringBuilder)localObject3).setSpan(paramViewHolder, k, k + paramInt, 33);
            paramViewHolder = (RecyclerView.ViewHolder)localObject2;
            bool2 = bool1;
            break;
            k++;
          }
        }
        catch (Exception paramViewHolder)
        {
          localObject3 = localObject5;
          FileLog.e(paramViewHolder);
          paramViewHolder = (RecyclerView.ViewHolder)localObject2;
          bool2 = bool1;
        }
        paramViewHolder = LocaleController.formatPluralString("Members", ((TLRPC.Chat)localObject6).participants_count);
        break label251;
        if (!TextUtils.isEmpty((CharSequence)localObject5))
        {
          paramViewHolder = TextUtils.concat(new CharSequence[] { localObject5, ", ", paramViewHolder });
          break label278;
        }
        break label278;
      }
      paramViewHolder = (GraySectionCell)paramViewHolder.itemView;
      if (isRecentSearchDisplayed())
      {
        if (!DataQuery.getInstance(this.currentAccount).hints.isEmpty()) {}
        for (k = 2;; k = 0)
        {
          if (paramInt >= k) {
            break label1110;
          }
          paramViewHolder.setText(LocaleController.getString("ChatHints", NUM).toUpperCase());
          break;
        }
        label1110:
        paramViewHolder.setText(LocaleController.getString("Recent", NUM).toUpperCase());
      }
      else if (!this.searchResultHashtags.isEmpty())
      {
        paramViewHolder.setText(LocaleController.getString("Hashtags", NUM).toUpperCase());
      }
      else if ((!this.searchAdapterHelper.getGlobalSearch().isEmpty()) && (paramInt == this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size()))
      {
        paramViewHolder.setText(LocaleController.getString("GlobalSearch", NUM));
      }
      else
      {
        paramViewHolder.setText(LocaleController.getString("SearchMessages", NUM));
        continue;
        localObject3 = (DialogCell)paramViewHolder.itemView;
        if (paramInt != getItemCount() - 1) {}
        for (bool2 = true;; bool2 = false)
        {
          ((DialogCell)localObject3).useSeparator = bool2;
          paramViewHolder = (MessageObject)getItem(paramInt);
          ((DialogCell)localObject3).setDialog(paramViewHolder.getDialogId(), paramViewHolder, paramViewHolder.messageOwner.date);
          break;
        }
        paramViewHolder = (HashtagSearchCell)paramViewHolder.itemView;
        paramViewHolder.setText((CharSequence)this.searchResultHashtags.get(paramInt - 1));
        if (paramInt != this.searchResultHashtags.size()) {}
        for (bool2 = true;; bool2 = false)
        {
          paramViewHolder.setNeedDivider(bool2);
          break;
        }
        ((CategoryAdapterRecycler)((RecyclerListView)paramViewHolder.itemView).getAdapter()).setIndex(paramInt / 2);
      }
    }
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
      return new RecyclerListView.Holder(paramViewGroup);
      paramViewGroup = new ProfileSearchCell(this.mContext);
      break;
      paramViewGroup = new GraySectionCell(this.mContext);
      break;
      paramViewGroup = new DialogCell(this.mContext, false);
      break;
      paramViewGroup = new LoadingCell(this.mContext);
      break;
      paramViewGroup = new HashtagSearchCell(this.mContext);
      break;
      RecyclerListView local9 = new RecyclerListView(this.mContext)
      {
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((getParent() != null) && (getParent().getParent() != null)) {
            getParent().getParent().requestDisallowInterceptTouchEvent(true);
          }
          return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
        }
      };
      local9.setTag(Integer.valueOf(9));
      local9.setItemAnimator(null);
      local9.setLayoutAnimation(null);
      paramViewGroup = new LinearLayoutManager(this.mContext)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      paramViewGroup.setOrientation(0);
      local9.setLayoutManager(paramViewGroup);
      local9.setAdapter(new CategoryAdapterRecycler(null));
      local9.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (DialogsSearchAdapter.this.delegate != null) {
            DialogsSearchAdapter.this.delegate.didPressedOnSubDialog(((Integer)paramAnonymousView.getTag()).intValue());
          }
        }
      });
      local9.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          if (DialogsSearchAdapter.this.delegate != null) {
            DialogsSearchAdapter.this.delegate.needRemoveHint(((Integer)paramAnonymousView.getTag()).intValue());
          }
          return true;
        }
      });
      paramViewGroup = local9;
      this.innerListView = local9;
      break;
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
    }
  }
  
  public void putRecentSearch(final long paramLong, TLObject paramTLObject)
  {
    RecentSearchObject localRecentSearchObject = (RecentSearchObject)this.recentSearchObjectsById.get(paramLong);
    if (localRecentSearchObject == null)
    {
      localRecentSearchObject = new RecentSearchObject();
      this.recentSearchObjectsById.put(paramLong, localRecentSearchObject);
    }
    for (;;)
    {
      this.recentSearchObjects.add(0, localRecentSearchObject);
      localRecentSearchObject.did = paramLong;
      localRecentSearchObject.object = paramTLObject;
      localRecentSearchObject.date = ((int)(System.currentTimeMillis() / 1000L));
      notifyDataSetChanged();
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance(DialogsSearchAdapter.this.currentAccount).getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindLong(1, paramLong);
            localSQLitePreparedStatement.bindInteger(2, (int)(System.currentTimeMillis() / 1000L));
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
      return;
      this.recentSearchObjects.remove(localRecentSearchObject);
    }
  }
  
  public void searchDialogs(final String paramString)
  {
    if ((paramString != null) && (this.lastSearchText != null) && (paramString.equals(this.lastSearchText))) {}
    for (;;)
    {
      return;
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
          this.searchAdapterHelper.unloadRecentHashtags();
          this.searchResult.clear();
          this.searchResultNames.clear();
          this.searchResultHashtags.clear();
          this.searchAdapterHelper.mergeResults(null);
          if (this.needMessagesSearch != 2) {
            this.searchAdapterHelper.queryServerSearch(null, true, true, true, true, 0, false);
          }
          searchMessagesInternal(null);
          notifyDataSetChanged();
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
        if (this.needMessagesSearch == 2) {
          break label324;
        }
      }
    }
    final int i;
    if ((paramString.startsWith("#")) && (paramString.length() == 1))
    {
      this.messagesSearchEndReached = true;
      if (this.searchAdapterHelper.loadRecentHashtags())
      {
        this.searchResultMessages.clear();
        this.searchResultHashtags.clear();
        ArrayList localArrayList = this.searchAdapterHelper.getHashtags();
        for (i = 0; i < localArrayList.size(); i++) {
          this.searchResultHashtags.add(((SearchAdapterHelper.HashtagObject)localArrayList.get(i)).hashtag);
        }
        if (this.delegate != null) {
          this.delegate.searchStateChanged(false);
        }
        label251:
        notifyDataSetChanged();
      }
    }
    for (;;)
    {
      i = this.lastSearchId + 1;
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
            DialogsSearchAdapter.access$1602(DialogsSearchAdapter.this, null);
            DialogsSearchAdapter.this.searchDialogsInternal(paramString, i);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (DialogsSearchAdapter.this.needMessagesSearch != 2) {
                  DialogsSearchAdapter.this.searchAdapterHelper.queryServerSearch(DialogsSearchAdapter.8.this.val$query, true, true, true, true, 0, false);
                }
                DialogsSearchAdapter.this.searchMessagesInternal(DialogsSearchAdapter.8.this.val$query);
              }
            });
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
      }, 200L, 300L);
      break;
      if (this.delegate == null) {
        break label251;
      }
      this.delegate.searchStateChanged(true);
      break label251;
      label324:
      this.searchResultHashtags.clear();
      notifyDataSetChanged();
    }
  }
  
  public void setDelegate(DialogsSearchAdapterDelegate paramDialogsSearchAdapterDelegate)
  {
    this.delegate = paramDialogsSearchAdapterDelegate;
  }
  
  private class CategoryAdapterRecycler
    extends RecyclerListView.SelectionAdapter
  {
    private CategoryAdapterRecycler() {}
    
    public int getItemCount()
    {
      return DataQuery.getInstance(DialogsSearchAdapter.this.currentAccount).hints.size();
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      HintDialogCell localHintDialogCell = (HintDialogCell)paramViewHolder.itemView;
      TLRPC.TL_topPeer localTL_topPeer = (TLRPC.TL_topPeer)DataQuery.getInstance(DialogsSearchAdapter.this.currentAccount).hints.get(paramInt);
      new TLRPC.TL_dialog();
      paramViewHolder = null;
      String str = null;
      paramInt = 0;
      Object localObject;
      if (localTL_topPeer.peer.user_id != 0)
      {
        paramInt = localTL_topPeer.peer.user_id;
        localObject = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getUser(Integer.valueOf(localTL_topPeer.peer.user_id));
        localHintDialogCell.setTag(Integer.valueOf(paramInt));
        str = "";
        if (localObject == null) {
          break label242;
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
          paramViewHolder = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(localTL_topPeer.peer.channel_id));
          localObject = str;
          break;
        }
        localObject = str;
        if (localTL_topPeer.peer.chat_id == 0) {
          break;
        }
        paramInt = -localTL_topPeer.peer.chat_id;
        paramViewHolder = MessagesController.getInstance(DialogsSearchAdapter.this.currentAccount).getChat(Integer.valueOf(localTL_topPeer.peer.chat_id));
        localObject = str;
        break;
        label242:
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
      return new RecyclerListView.Holder(paramViewGroup);
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
    public abstract void didPressedOnSubDialog(long paramLong);
    
    public abstract void needRemoveHint(int paramInt);
    
    public abstract void searchStateChanged(boolean paramBoolean);
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