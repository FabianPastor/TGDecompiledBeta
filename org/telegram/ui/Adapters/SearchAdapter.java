package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class SearchAdapter
  extends RecyclerListView.SelectionAdapter
{
  private boolean allowBots;
  private boolean allowChats;
  private boolean allowUsernameSearch;
  private int channelId;
  private SparseArray<?> checkedMap;
  private SparseArray<TLRPC.User> ignoreUsers;
  private Context mContext;
  private boolean onlyMutual;
  private SearchAdapterHelper searchAdapterHelper;
  private ArrayList<TLRPC.User> searchResult = new ArrayList();
  private ArrayList<CharSequence> searchResultNames = new ArrayList();
  private Timer searchTimer;
  private boolean useUserCell;
  
  public SearchAdapter(Context paramContext, SparseArray<TLRPC.User> paramSparseArray, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt)
  {
    this.mContext = paramContext;
    this.ignoreUsers = paramSparseArray;
    this.onlyMutual = paramBoolean2;
    this.allowUsernameSearch = paramBoolean1;
    this.allowChats = paramBoolean3;
    this.allowBots = paramBoolean4;
    this.channelId = paramInt;
    this.searchAdapterHelper = new SearchAdapterHelper(true);
    this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate()
    {
      public void onDataSetChanged()
      {
        SearchAdapter.this.notifyDataSetChanged();
      }
      
      public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramAnonymousArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramAnonymousHashMap) {}
    });
  }
  
  private void processSearch(final String paramString)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (SearchAdapter.this.allowUsernameSearch) {
          SearchAdapter.this.searchAdapterHelper.queryServerSearch(paramString, true, SearchAdapter.this.allowChats, SearchAdapter.this.allowBots, true, SearchAdapter.this.channelId, false);
        }
        final int i = UserConfig.selectedAccount;
        final ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(ContactsController.getInstance(i).contacts);
        Utilities.searchQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            Object localObject1 = SearchAdapter.3.this.val$query.trim().toLowerCase();
            if (((String)localObject1).length() == 0) {
              SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
            }
            for (;;)
            {
              return;
              String str1 = LocaleController.getInstance().getTranslitString((String)localObject1);
              Object localObject2;
              if (!((String)localObject1).equals(str1))
              {
                localObject2 = str1;
                if (str1.length() != 0) {}
              }
              else
              {
                localObject2 = null;
              }
              int i;
              String[] arrayOfString;
              ArrayList localArrayList;
              int j;
              label123:
              TLRPC.User localUser;
              if (localObject2 != null)
              {
                i = 1;
                arrayOfString = new String[i + 1];
                arrayOfString[0] = localObject1;
                if (localObject2 != null) {
                  arrayOfString[1] = localObject2;
                }
                localObject1 = new ArrayList();
                localArrayList = new ArrayList();
                j = 0;
                if (j >= localArrayList.size()) {
                  break label499;
                }
                localObject2 = (TLRPC.TL_contact)localArrayList.get(j);
                localUser = MessagesController.getInstance(i).getUser(Integer.valueOf(((TLRPC.TL_contact)localObject2).user_id));
                if ((localUser.id != UserConfig.getInstance(i).getClientUserId()) && ((!SearchAdapter.this.onlyMutual) || (localUser.mutual_contact))) {
                  break label218;
                }
              }
              label218:
              label359:
              label434:
              label489:
              label497:
              for (;;)
              {
                j++;
                break label123;
                i = 0;
                break;
                String str2 = ContactsController.formatName(localUser.first_name, localUser.last_name).toLowerCase();
                str1 = LocaleController.getInstance().getTranslitString(str2);
                localObject2 = str1;
                if (str2.equals(str1)) {
                  localObject2 = null;
                }
                int k = 0;
                int m = arrayOfString.length;
                int n = 0;
                for (;;)
                {
                  if (n >= m) {
                    break label497;
                  }
                  str1 = arrayOfString[n];
                  if ((str2.startsWith(str1)) || (str2.contains(" " + str1)) || ((localObject2 != null) && ((((String)localObject2).startsWith(str1)) || (((String)localObject2).contains(" " + str1)))))
                  {
                    i = 1;
                    if (i == 0) {
                      break label489;
                    }
                    if (i != 1) {
                      break label434;
                    }
                    localArrayList.add(AndroidUtilities.generateSearchName(localUser.first_name, localUser.last_name, str1));
                  }
                  for (;;)
                  {
                    ((ArrayList)localObject1).add(localUser);
                    break;
                    i = k;
                    if (localUser.username == null) {
                      break label359;
                    }
                    i = k;
                    if (!localUser.username.startsWith(str1)) {
                      break label359;
                    }
                    i = 2;
                    break label359;
                    localArrayList.add(AndroidUtilities.generateSearchName("@" + localUser.username, null, "@" + str1));
                  }
                  n++;
                  k = i;
                }
              }
              label499:
              SearchAdapter.this.updateSearchResults((ArrayList)localObject1, localArrayList);
            }
          }
        });
      }
    });
  }
  
  private void updateSearchResults(final ArrayList<TLRPC.User> paramArrayList, final ArrayList<CharSequence> paramArrayList1)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        SearchAdapter.access$902(SearchAdapter.this, paramArrayList);
        SearchAdapter.access$1002(SearchAdapter.this, paramArrayList1);
        SearchAdapter.this.notifyDataSetChanged();
      }
    });
  }
  
  public TLObject getItem(int paramInt)
  {
    int i = this.searchResult.size();
    int j = this.searchAdapterHelper.getGlobalSearch().size();
    TLObject localTLObject;
    if ((paramInt >= 0) && (paramInt < i)) {
      localTLObject = (TLObject)this.searchResult.get(paramInt);
    }
    for (;;)
    {
      return localTLObject;
      if ((paramInt > i) && (paramInt <= j + i)) {
        localTLObject = (TLObject)this.searchAdapterHelper.getGlobalSearch().get(paramInt - i - 1);
      } else {
        localTLObject = null;
      }
    }
  }
  
  public int getItemCount()
  {
    int i = this.searchResult.size();
    int j = this.searchAdapterHelper.getGlobalSearch().size();
    int k = i;
    if (j != 0) {
      k = i + (j + 1);
    }
    return k;
  }
  
  public int getItemViewType(int paramInt)
  {
    if (paramInt == this.searchResult.size()) {}
    for (paramInt = 1;; paramInt = 0) {
      return paramInt;
    }
  }
  
  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    if (paramViewHolder.getAdapterPosition() != this.searchResult.size()) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isGlobalSearch(int paramInt)
  {
    boolean bool1 = false;
    int i = this.searchResult.size();
    int j = this.searchAdapterHelper.getGlobalSearch().size();
    boolean bool2;
    if ((paramInt >= 0) && (paramInt < i)) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (paramInt > i)
      {
        bool2 = bool1;
        if (paramInt <= j + i) {
          bool2 = true;
        }
      }
    }
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    TLObject localTLObject;
    int i;
    String str1;
    Object localObject1;
    CharSequence localCharSequence;
    Object localObject2;
    Object localObject3;
    if (paramViewHolder.getItemViewType() == 0)
    {
      localTLObject = getItem(paramInt);
      if (localTLObject != null)
      {
        i = 0;
        str1 = null;
        if (!(localTLObject instanceof TLRPC.User)) {
          break label223;
        }
        str1 = ((TLRPC.User)localTLObject).username;
        i = ((TLRPC.User)localTLObject).id;
        localObject1 = null;
        localCharSequence = null;
        if (paramInt >= this.searchResult.size()) {
          break label251;
        }
        localCharSequence = (CharSequence)this.searchResultNames.get(paramInt);
        localObject2 = localCharSequence;
        localObject3 = localObject1;
        if (localCharSequence != null)
        {
          localObject2 = localCharSequence;
          localObject3 = localObject1;
          if (str1 != null)
          {
            localObject2 = localCharSequence;
            localObject3 = localObject1;
            if (str1.length() > 0)
            {
              localObject2 = localCharSequence;
              localObject3 = localObject1;
              if (localCharSequence.toString().startsWith("@" + str1))
              {
                localObject3 = localCharSequence;
                localObject2 = null;
              }
            }
          }
        }
        label168:
        if (!this.useUserCell) {
          break label445;
        }
        paramViewHolder = (UserCell)paramViewHolder.itemView;
        paramViewHolder.setData(localTLObject, (CharSequence)localObject2, (CharSequence)localObject3, 0);
        if (this.checkedMap != null) {
          if (this.checkedMap.indexOfKey(i) < 0) {
            break label439;
          }
        }
      }
    }
    label222:
    label223:
    label251:
    label415:
    String str2;
    label439:
    for (boolean bool = true;; bool = false)
    {
      paramViewHolder.setChecked(bool, false);
      return;
      if (!(localTLObject instanceof TLRPC.Chat)) {
        break label498;
      }
      str1 = ((TLRPC.Chat)localTLObject).username;
      i = ((TLRPC.Chat)localTLObject).id;
      break;
      localObject2 = localCharSequence;
      localObject3 = localObject1;
      if (paramInt <= this.searchResult.size()) {
        break label168;
      }
      localObject2 = localCharSequence;
      localObject3 = localObject1;
      if (str1 == null) {
        break label168;
      }
      localObject2 = this.searchAdapterHelper.getLastFoundUsername();
      localObject3 = localObject2;
      if (((String)localObject2).startsWith("@")) {
        localObject3 = ((String)localObject2).substring(1);
      }
      try
      {
        localObject2 = new android/text/SpannableStringBuilder;
        ((SpannableStringBuilder)localObject2).<init>();
        ((SpannableStringBuilder)localObject2).append("@");
        ((SpannableStringBuilder)localObject2).append(str1);
        int j = str1.toLowerCase().indexOf((String)localObject3);
        int k;
        if (j != -1)
        {
          k = ((String)localObject3).length();
          if (j != 0) {
            break label415;
          }
          k++;
        }
        for (;;)
        {
          localObject3 = new android/text/style/ForegroundColorSpan;
          ((ForegroundColorSpan)localObject3).<init>(Theme.getColor("windowBackgroundWhiteBlueText4"));
          ((SpannableStringBuilder)localObject2).setSpan(localObject3, j, j + k, 33);
          localObject3 = localObject2;
          localObject2 = localCharSequence;
          break;
          j++;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        localObject2 = localCharSequence;
        str2 = str1;
      }
    }
    label445:
    paramViewHolder = (ProfileSearchCell)paramViewHolder.itemView;
    paramViewHolder.setData(localTLObject, null, (CharSequence)localObject2, str2, false, false);
    if ((paramInt != getItemCount() - 1) && (paramInt != this.searchResult.size() - 1)) {}
    for (bool = true;; bool = false)
    {
      paramViewHolder.useSeparator = bool;
      break label222;
      label498:
      break;
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      paramViewGroup = new GraySectionCell(this.mContext);
      ((GraySectionCell)paramViewGroup).setText(LocaleController.getString("GlobalSearch", NUM));
    }
    for (;;)
    {
      return new RecyclerListView.Holder(paramViewGroup);
      if (this.useUserCell)
      {
        UserCell localUserCell = new UserCell(this.mContext, 1, 1, false);
        paramViewGroup = localUserCell;
        if (this.checkedMap != null)
        {
          ((UserCell)localUserCell).setChecked(false, false);
          paramViewGroup = localUserCell;
        }
      }
      else
      {
        paramViewGroup = new ProfileSearchCell(this.mContext);
      }
    }
  }
  
  public void searchDialogs(final String paramString)
  {
    try
    {
      if (this.searchTimer != null) {
        this.searchTimer.cancel();
      }
      if (paramString == null)
      {
        this.searchResult.clear();
        this.searchResultNames.clear();
        if (this.allowUsernameSearch) {
          this.searchAdapterHelper.queryServerSearch(null, true, this.allowChats, this.allowBots, true, this.channelId, false);
        }
        notifyDataSetChanged();
        return;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
        continue;
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask()
        {
          public void run()
          {
            try
            {
              SearchAdapter.this.searchTimer.cancel();
              SearchAdapter.access$002(SearchAdapter.this, null);
              SearchAdapter.this.processSearch(paramString);
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
      }
    }
  }
  
  public void setCheckedMap(SparseArray<?> paramSparseArray)
  {
    this.checkedMap = paramSparseArray;
  }
  
  public void setUseUserCell(boolean paramBoolean)
  {
    this.useUserCell = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/SearchAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */