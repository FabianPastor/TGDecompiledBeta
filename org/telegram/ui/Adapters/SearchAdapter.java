package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;

public class SearchAdapter
  extends BaseSearchAdapter
{
  private boolean allowBots;
  private boolean allowChats;
  private boolean allowUsernameSearch;
  private HashMap<Integer, ?> checkedMap;
  private HashMap<Integer, TLRPC.User> ignoreUsers;
  private Context mContext;
  private boolean onlyMutual;
  private ArrayList<TLRPC.User> searchResult = new ArrayList();
  private ArrayList<CharSequence> searchResultNames = new ArrayList();
  private Timer searchTimer;
  private boolean useUserCell;
  
  public SearchAdapter(Context paramContext, HashMap<Integer, TLRPC.User> paramHashMap, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    this.mContext = paramContext;
    this.ignoreUsers = paramHashMap;
    this.onlyMutual = paramBoolean2;
    this.allowUsernameSearch = paramBoolean1;
    this.allowChats = paramBoolean3;
    this.allowBots = paramBoolean4;
  }
  
  private void processSearch(final String paramString)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (SearchAdapter.this.allowUsernameSearch) {
          SearchAdapter.this.queryServerSearch(paramString, SearchAdapter.this.allowChats, SearchAdapter.this.allowBots);
        }
        final ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(ContactsController.getInstance().contacts);
        Utilities.searchQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            Object localObject2 = SearchAdapter.2.this.val$query.trim().toLowerCase();
            if (((String)localObject2).length() == 0)
            {
              SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
              return;
            }
            String str1 = LocaleController.getInstance().getTranslitString((String)localObject2);
            Object localObject1;
            if (!((String)localObject2).equals(str1))
            {
              localObject1 = str1;
              if (str1.length() != 0) {}
            }
            else
            {
              localObject1 = null;
            }
            int i;
            String[] arrayOfString;
            ArrayList localArrayList;
            int j;
            label135:
            TLRPC.User localUser;
            if (localObject1 != null)
            {
              i = 1;
              arrayOfString = new String[i + 1];
              arrayOfString[0] = localObject2;
              if (localObject1 != null) {
                arrayOfString[1] = localObject1;
              }
              localObject2 = new ArrayList();
              localArrayList = new ArrayList();
              j = 0;
              if (j >= localArrayList.size()) {
                break label508;
              }
              localObject1 = (TLRPC.TL_contact)localArrayList.get(j);
              localUser = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject1).user_id));
              if ((localUser.id != UserConfig.getClientUserId()) && ((!SearchAdapter.this.onlyMutual) || (localUser.mutual_contact))) {
                break label219;
              }
            }
            label219:
            label369:
            label442:
            label498:
            label506:
            for (;;)
            {
              j += 1;
              break label135;
              i = 0;
              break;
              String str2 = ContactsController.formatName(localUser.first_name, localUser.last_name).toLowerCase();
              str1 = LocaleController.getInstance().getTranslitString(str2);
              localObject1 = str1;
              if (str2.equals(str1)) {
                localObject1 = null;
              }
              int m = 0;
              int n = arrayOfString.length;
              int k = 0;
              for (;;)
              {
                if (k >= n) {
                  break label506;
                }
                str1 = arrayOfString[k];
                if ((str2.startsWith(str1)) || (str2.contains(" " + str1)) || ((localObject1 != null) && ((((String)localObject1).startsWith(str1)) || (((String)localObject1).contains(" " + str1)))))
                {
                  i = 1;
                  if (i == 0) {
                    break label498;
                  }
                  if (i != 1) {
                    break label442;
                  }
                  localArrayList.add(AndroidUtilities.generateSearchName(localUser.first_name, localUser.last_name, str1));
                }
                for (;;)
                {
                  ((ArrayList)localObject2).add(localUser);
                  break;
                  i = m;
                  if (localUser.username == null) {
                    break label369;
                  }
                  i = m;
                  if (!localUser.username.startsWith(str1)) {
                    break label369;
                  }
                  i = 2;
                  break label369;
                  localArrayList.add(AndroidUtilities.generateSearchName("@" + localUser.username, null, "@" + str1));
                }
                k += 1;
                m = i;
              }
            }
            label508:
            SearchAdapter.this.updateSearchResults((ArrayList)localObject2, localArrayList);
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
        SearchAdapter.access$702(SearchAdapter.this, paramArrayList);
        SearchAdapter.access$802(SearchAdapter.this, paramArrayList1);
        SearchAdapter.this.notifyDataSetChanged();
      }
    });
  }
  
  public boolean areAllItemsEnabled()
  {
    return false;
  }
  
  public int getCount()
  {
    int j = this.searchResult.size();
    int k = this.globalSearch.size();
    int i = j;
    if (k != 0) {
      i = j + (k + 1);
    }
    return i;
  }
  
  public TLObject getItem(int paramInt)
  {
    int i = this.searchResult.size();
    int j = this.globalSearch.size();
    if ((paramInt >= 0) && (paramInt < i)) {
      return (TLObject)this.searchResult.get(paramInt);
    }
    if ((paramInt > i) && (paramInt <= j + i)) {
      return (TLObject)this.globalSearch.get(paramInt - i - 1);
    }
    return null;
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    if (paramInt == this.searchResult.size()) {
      return 1;
    }
    return 0;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    Object localObject1;
    if (paramInt == this.searchResult.size())
    {
      localObject1 = paramView;
      if (paramView == null)
      {
        localObject1 = new GreySectionCell(this.mContext);
        ((GreySectionCell)localObject1).setText(LocaleController.getString("GlobalSearch", 2131165715));
      }
    }
    label97:
    TLObject localTLObject;
    do
    {
      return (View)localObject1;
      paramViewGroup = paramView;
      if (paramView == null)
      {
        if (!this.useUserCell) {
          break;
        }
        paramView = new UserCell(this.mContext, 1, 1, false);
        paramViewGroup = paramView;
        if (this.checkedMap != null)
        {
          ((UserCell)paramView).setChecked(false, false);
          paramViewGroup = paramView;
        }
      }
      localTLObject = getItem(paramInt);
      localObject1 = paramViewGroup;
    } while (localTLObject == null);
    int i = 0;
    paramView = null;
    label144:
    Object localObject4;
    CharSequence localCharSequence;
    Object localObject3;
    if ((localTLObject instanceof TLRPC.User))
    {
      paramView = ((TLRPC.User)localTLObject).username;
      i = ((TLRPC.User)localTLObject).id;
      localObject4 = null;
      localCharSequence = null;
      if (paramInt >= this.searchResult.size()) {
        break label359;
      }
      localCharSequence = (CharSequence)this.searchResultNames.get(paramInt);
      localObject3 = localCharSequence;
      localObject1 = localObject4;
      if (localCharSequence != null)
      {
        localObject3 = localCharSequence;
        localObject1 = localObject4;
        if (paramView != null)
        {
          localObject3 = localCharSequence;
          localObject1 = localObject4;
          if (paramView.length() > 0)
          {
            localObject3 = localCharSequence;
            localObject1 = localObject4;
            if (localCharSequence.toString().startsWith("@" + paramView))
            {
              localObject1 = localCharSequence;
              localObject3 = null;
            }
          }
        }
      }
    }
    label359:
    Object localObject2;
    for (;;)
    {
      if (!this.useUserCell) {
        break label483;
      }
      ((UserCell)paramViewGroup).setData(localTLObject, (CharSequence)localObject3, (CharSequence)localObject1, 0);
      localObject1 = paramViewGroup;
      if (this.checkedMap == null) {
        break;
      }
      ((UserCell)paramViewGroup).setChecked(this.checkedMap.containsKey(Integer.valueOf(i)), false);
      return paramViewGroup;
      paramViewGroup = new ProfileSearchCell(this.mContext);
      break label97;
      if (!(localTLObject instanceof TLRPC.Chat)) {
        break label144;
      }
      paramView = ((TLRPC.Chat)localTLObject).username;
      i = ((TLRPC.Chat)localTLObject).id;
      break label144;
      localObject3 = localCharSequence;
      localObject1 = localObject4;
      if (paramInt > this.searchResult.size())
      {
        localObject3 = localCharSequence;
        localObject1 = localObject4;
        if (paramView != null)
        {
          localObject3 = this.lastFoundUsername;
          localObject1 = localObject3;
          if (((String)localObject3).startsWith("@")) {
            localObject1 = ((String)localObject3).substring(1);
          }
          try
          {
            localObject1 = AndroidUtilities.replaceTags(String.format("<c#ff4d83b3>@%s</c>%s", new Object[] { paramView.substring(0, ((String)localObject1).length()), paramView.substring(((String)localObject1).length()) }));
            localObject3 = localCharSequence;
          }
          catch (Exception localException)
          {
            FileLog.e("tmessages", localException);
            localObject3 = localCharSequence;
            localObject2 = paramView;
          }
        }
      }
    }
    label483:
    ((ProfileSearchCell)paramViewGroup).setData(localTLObject, null, (CharSequence)localObject3, (CharSequence)localObject2, false);
    paramView = (ProfileSearchCell)paramViewGroup;
    if ((paramInt != getCount() - 1) && (paramInt != this.searchResult.size() - 1)) {}
    for (boolean bool = true;; bool = false)
    {
      paramView.useSeparator = bool;
      localObject2 = paramViewGroup;
      if (this.ignoreUsers == null) {
        break;
      }
      if (!this.ignoreUsers.containsKey(Integer.valueOf(i))) {
        break label577;
      }
      ((ProfileSearchCell)paramViewGroup).drawAlpha = 0.5F;
      return paramViewGroup;
    }
    label577:
    ((ProfileSearchCell)paramViewGroup).drawAlpha = 1.0F;
    return paramViewGroup;
  }
  
  public int getViewTypeCount()
  {
    return 2;
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  public boolean isEmpty()
  {
    return (this.searchResult.isEmpty()) && (this.globalSearch.isEmpty());
  }
  
  public boolean isEnabled(int paramInt)
  {
    return paramInt != this.searchResult.size();
  }
  
  public boolean isGlobalSearch(int paramInt)
  {
    int i = this.searchResult.size();
    int j = this.globalSearch.size();
    if ((paramInt >= 0) && (paramInt < i)) {}
    while ((paramInt <= i) || (paramInt > j + i)) {
      return false;
    }
    return true;
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
          queryServerSearch(null, this.allowChats, this.allowBots);
        }
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
              FileLog.e("tmessages", localException);
            }
          }
        }
      }, 200L, 300L);
    }
  }
  
  public void setCheckedMap(HashMap<Integer, ?> paramHashMap)
  {
    this.checkedMap = paramHashMap;
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