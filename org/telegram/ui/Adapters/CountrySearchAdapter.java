package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Cells.TextSettingsCell;

public class CountrySearchAdapter
  extends BaseFragmentAdapter
{
  private HashMap<String, ArrayList<CountryAdapter.Country>> countries;
  private Context mContext;
  private ArrayList<CountryAdapter.Country> searchResult;
  private Timer searchTimer;
  
  public CountrySearchAdapter(Context paramContext, HashMap<String, ArrayList<CountryAdapter.Country>> paramHashMap)
  {
    this.mContext = paramContext;
    this.countries = paramHashMap;
  }
  
  private void processSearch(final String paramString)
  {
    Utilities.searchQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (paramString.trim().toLowerCase().length() == 0)
        {
          CountrySearchAdapter.this.updateSearchResults(new ArrayList());
          return;
        }
        ArrayList localArrayList = new ArrayList();
        Object localObject = paramString.substring(0, 1);
        localObject = (ArrayList)CountrySearchAdapter.this.countries.get(((String)localObject).toUpperCase());
        if (localObject != null)
        {
          localObject = ((ArrayList)localObject).iterator();
          while (((Iterator)localObject).hasNext())
          {
            CountryAdapter.Country localCountry = (CountryAdapter.Country)((Iterator)localObject).next();
            if (localCountry.name.toLowerCase().startsWith(paramString)) {
              localArrayList.add(localCountry);
            }
          }
        }
        CountrySearchAdapter.this.updateSearchResults(localArrayList);
      }
    });
  }
  
  private void updateSearchResults(final ArrayList<CountryAdapter.Country> paramArrayList)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        CountrySearchAdapter.access$402(CountrySearchAdapter.this, paramArrayList);
        CountrySearchAdapter.this.notifyDataSetChanged();
      }
    });
  }
  
  public boolean areAllItemsEnabled()
  {
    return true;
  }
  
  public int getCount()
  {
    if (this.searchResult == null) {
      return 0;
    }
    return this.searchResult.size();
  }
  
  public CountryAdapter.Country getItem(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.searchResult.size())) {
      return null;
    }
    return (CountryAdapter.Country)this.searchResult.get(paramInt);
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public int getItemViewType(int paramInt)
  {
    return 0;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    paramViewGroup = paramView;
    if (paramView == null) {
      paramViewGroup = new TextSettingsCell(this.mContext);
    }
    Object localObject = (CountryAdapter.Country)this.searchResult.get(paramInt);
    paramView = (TextSettingsCell)paramViewGroup;
    String str = ((CountryAdapter.Country)localObject).name;
    localObject = "+" + ((CountryAdapter.Country)localObject).code;
    if (paramInt != this.searchResult.size() - 1) {}
    for (boolean bool = true;; bool = false)
    {
      paramView.setTextAndValue(str, (String)localObject, bool);
      return paramViewGroup;
    }
  }
  
  public int getViewTypeCount()
  {
    return 1;
  }
  
  public boolean hasStableIds()
  {
    return true;
  }
  
  public boolean isEmpty()
  {
    return (this.searchResult == null) || (this.searchResult.size() == 0);
  }
  
  public boolean isEnabled(int paramInt)
  {
    return true;
  }
  
  public void search(final String paramString)
  {
    if (paramString == null)
    {
      this.searchResult = null;
      return;
    }
    try
    {
      if (this.searchTimer != null) {
        this.searchTimer.cancel();
      }
      this.searchTimer = new Timer();
      this.searchTimer.schedule(new TimerTask()
      {
        public void run()
        {
          try
          {
            CountrySearchAdapter.this.searchTimer.cancel();
            CountrySearchAdapter.access$002(CountrySearchAdapter.this, null);
            CountrySearchAdapter.this.processSearch(paramString);
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
      }, 100L, 300L);
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/CountrySearchAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */