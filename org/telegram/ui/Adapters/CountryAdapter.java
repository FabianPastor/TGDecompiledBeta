package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class CountryAdapter
  extends BaseSectionsAdapter
{
  private HashMap<String, ArrayList<Country>> countries = new HashMap();
  private Context mContext;
  private ArrayList<String> sortedCountries = new ArrayList();
  
  public CountryAdapter(Context paramContext)
  {
    this.mContext = paramContext;
    try
    {
      InputStream localInputStream = ApplicationLoader.applicationContext.getResources().getAssets().open("countries.txt");
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream));
      for (;;)
      {
        paramContext = localBufferedReader.readLine();
        if (paramContext == null) {
          break;
        }
        paramContext = paramContext.split(";");
        Country localCountry = new Country();
        localCountry.name = paramContext[2];
        localCountry.code = paramContext[0];
        localCountry.shortname = paramContext[1];
        String str = localCountry.name.substring(0, 1).toUpperCase();
        ArrayList localArrayList = (ArrayList)this.countries.get(str);
        paramContext = localArrayList;
        if (localArrayList == null)
        {
          paramContext = new ArrayList();
          this.countries.put(str, paramContext);
          this.sortedCountries.add(str);
        }
        paramContext.add(localCountry);
      }
      return;
    }
    catch (Exception paramContext)
    {
      FileLog.e("tmessages", paramContext);
      for (;;)
      {
        Collections.sort(this.sortedCountries, new Comparator()
        {
          public int compare(String paramAnonymousString1, String paramAnonymousString2)
          {
            return paramAnonymousString1.compareTo(paramAnonymousString2);
          }
        });
        paramContext = this.countries.values().iterator();
        while (paramContext.hasNext()) {
          Collections.sort((ArrayList)paramContext.next(), new Comparator()
          {
            public int compare(CountryAdapter.Country paramAnonymousCountry1, CountryAdapter.Country paramAnonymousCountry2)
            {
              return paramAnonymousCountry1.name.compareTo(paramAnonymousCountry2.name);
            }
          });
        }
        localBufferedReader.close();
        localInputStream.close();
      }
    }
  }
  
  public int getCountForSection(int paramInt)
  {
    int j = ((ArrayList)this.countries.get(this.sortedCountries.get(paramInt))).size();
    int i = j;
    if (paramInt != this.sortedCountries.size() - 1) {
      i = j + 1;
    }
    return i;
  }
  
  public HashMap<String, ArrayList<Country>> getCountries()
  {
    return this.countries;
  }
  
  public Country getItem(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 >= this.sortedCountries.size())) {}
    ArrayList localArrayList;
    do
    {
      return null;
      localArrayList = (ArrayList)this.countries.get(this.sortedCountries.get(paramInt1));
    } while ((paramInt2 < 0) || (paramInt2 >= localArrayList.size()));
    return (Country)localArrayList.get(paramInt2);
  }
  
  public View getItemView(int paramInt1, int paramInt2, View paramView, ViewGroup paramViewGroup)
  {
    float f2 = 72.0F;
    float f3 = 54.0F;
    int i = getItemViewType(paramInt1, paramInt2);
    if (i == 1)
    {
      paramViewGroup = paramView;
      if (paramView == null)
      {
        paramViewGroup = new DividerCell(this.mContext);
        if (!LocaleController.isRTL) {
          break label84;
        }
        f1 = 24.0F;
        paramInt1 = AndroidUtilities.dp(f1);
        if (!LocaleController.isRTL) {
          break label91;
        }
        f1 = f2;
        label68:
        paramViewGroup.setPadding(paramInt1, 0, AndroidUtilities.dp(f1), 0);
      }
    }
    label84:
    label91:
    do
    {
      return paramViewGroup;
      f1 = 72.0F;
      break;
      f1 = 24.0F;
      break label68;
      paramViewGroup = paramView;
    } while (i != 0);
    paramViewGroup = paramView;
    if (paramView == null)
    {
      paramViewGroup = new TextSettingsCell(this.mContext);
      if (!LocaleController.isRTL) {
        break label231;
      }
      f1 = 16.0F;
      i = AndroidUtilities.dp(f1);
      if (!LocaleController.isRTL) {
        break label238;
      }
    }
    label231:
    label238:
    for (float f1 = f3;; f1 = 16.0F)
    {
      paramViewGroup.setPadding(i, 0, AndroidUtilities.dp(f1), 0);
      paramView = (Country)((ArrayList)this.countries.get(this.sortedCountries.get(paramInt1))).get(paramInt2);
      ((TextSettingsCell)paramViewGroup).setTextAndValue(paramView.name, "+" + paramView.code, false);
      return paramViewGroup;
      f1 = 54.0F;
      break;
    }
  }
  
  public int getItemViewType(int paramInt1, int paramInt2)
  {
    if (paramInt2 < ((ArrayList)this.countries.get(this.sortedCountries.get(paramInt1))).size()) {
      return 0;
    }
    return 1;
  }
  
  public int getSectionCount()
  {
    return this.sortedCountries.size();
  }
  
  public View getSectionHeaderView(int paramInt, View paramView, ViewGroup paramViewGroup)
  {
    paramViewGroup = paramView;
    if (paramView == null)
    {
      paramViewGroup = new LetterSectionCell(this.mContext);
      ((LetterSectionCell)paramViewGroup).setCellHeight(AndroidUtilities.dp(48.0F));
    }
    ((LetterSectionCell)paramViewGroup).setLetter(((String)this.sortedCountries.get(paramInt)).toUpperCase());
    return paramViewGroup;
  }
  
  public int getViewTypeCount()
  {
    return 2;
  }
  
  public boolean isRowEnabled(int paramInt1, int paramInt2)
  {
    return paramInt2 < ((ArrayList)this.countries.get(this.sortedCountries.get(paramInt1))).size();
  }
  
  public static class Country
  {
    public String code;
    public String name;
    public String shortname;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/CountryAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */