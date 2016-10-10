package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextSettingsCell;

public class LanguageSelectActivity
  extends BaseFragment
{
  private TextView emptyTextView;
  private BaseFragmentAdapter listAdapter;
  private ListView listView;
  private BaseFragmentAdapter searchListViewAdapter;
  public ArrayList<LocaleController.LocaleInfo> searchResult;
  private Timer searchTimer;
  private boolean searchWas;
  private boolean searching;
  
  private void processSearch(final String paramString)
  {
    Utilities.searchQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (paramString.trim().toLowerCase().length() == 0)
        {
          LanguageSelectActivity.this.updateSearchResults(new ArrayList());
          return;
        }
        System.currentTimeMillis();
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator = LocaleController.getInstance().sortedLanguages.iterator();
        while (localIterator.hasNext())
        {
          LocaleController.LocaleInfo localLocaleInfo = (LocaleController.LocaleInfo)localIterator.next();
          if ((localLocaleInfo.name.toLowerCase().startsWith(paramString)) || (localLocaleInfo.nameEnglish.toLowerCase().startsWith(paramString))) {
            localArrayList.add(localLocaleInfo);
          }
        }
        LanguageSelectActivity.this.updateSearchResults(localArrayList);
      }
    });
  }
  
  private void updateSearchResults(final ArrayList<LocaleController.LocaleInfo> paramArrayList)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        LanguageSelectActivity.this.searchResult = paramArrayList;
        LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
      }
    });
  }
  
  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("Language", 2131165784));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          LanguageSelectActivity.this.finishFragment();
        }
      }
    });
    this.actionBar.createMenu().addItem(0, 2130837711).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        LanguageSelectActivity.this.search(null);
        LanguageSelectActivity.access$002(LanguageSelectActivity.this, false);
        LanguageSelectActivity.access$102(LanguageSelectActivity.this, false);
        if (LanguageSelectActivity.this.listView != null)
        {
          LanguageSelectActivity.this.emptyTextView.setVisibility(8);
          LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
        }
      }
      
      public void onSearchExpand()
      {
        LanguageSelectActivity.access$002(LanguageSelectActivity.this, true);
      }
      
      public void onTextChanged(EditText paramAnonymousEditText)
      {
        paramAnonymousEditText = paramAnonymousEditText.getText().toString();
        LanguageSelectActivity.this.search(paramAnonymousEditText);
        if (paramAnonymousEditText.length() != 0)
        {
          LanguageSelectActivity.access$102(LanguageSelectActivity.this, true);
          if (LanguageSelectActivity.this.listView != null) {
            LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
          }
        }
      }
    }).getSearchField().setHint(LocaleController.getString("Search", 2131166206));
    this.listAdapter = new ListAdapter(paramContext);
    this.searchListViewAdapter = new SearchAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    localLinearLayout.setVisibility(4);
    localLinearLayout.setOrientation(1);
    ((FrameLayout)this.fragmentView).addView(localLinearLayout);
    Object localObject = (FrameLayout.LayoutParams)localLinearLayout.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).width = -1;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    ((FrameLayout.LayoutParams)localObject).gravity = 48;
    localLinearLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
    localLinearLayout.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.emptyTextView = new TextView(paramContext);
    this.emptyTextView.setTextColor(-8355712);
    this.emptyTextView.setTextSize(20.0F);
    this.emptyTextView.setGravity(17);
    this.emptyTextView.setText(LocaleController.getString("NoResult", 2131165949));
    localLinearLayout.addView(this.emptyTextView);
    localObject = (LinearLayout.LayoutParams)this.emptyTextView.getLayoutParams();
    ((LinearLayout.LayoutParams)localObject).width = -1;
    ((LinearLayout.LayoutParams)localObject).height = -1;
    ((LinearLayout.LayoutParams)localObject).weight = 0.5F;
    this.emptyTextView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    localObject = new FrameLayout(paramContext);
    localLinearLayout.addView((View)localObject);
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)((FrameLayout)localObject).getLayoutParams();
    localLayoutParams.width = -1;
    localLayoutParams.height = -1;
    localLayoutParams.weight = 0.5F;
    ((FrameLayout)localObject).setLayoutParams(localLayoutParams);
    this.listView = new ListView(paramContext);
    this.listView.setEmptyView(localLinearLayout);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setAdapter(this.listAdapter);
    ((FrameLayout)this.fragmentView).addView(this.listView);
    paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
    paramContext.width = -1;
    paramContext.height = -1;
    this.listView.setLayoutParams(paramContext);
    this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
    {
      public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        paramAnonymousView = null;
        if ((LanguageSelectActivity.this.searching) && (LanguageSelectActivity.this.searchWas))
        {
          paramAnonymousAdapterView = paramAnonymousView;
          if (paramAnonymousInt >= 0)
          {
            paramAnonymousAdapterView = paramAnonymousView;
            if (paramAnonymousInt < LanguageSelectActivity.this.searchResult.size()) {
              paramAnonymousAdapterView = (LocaleController.LocaleInfo)LanguageSelectActivity.this.searchResult.get(paramAnonymousInt);
            }
          }
        }
        for (;;)
        {
          if (paramAnonymousAdapterView != null)
          {
            LocaleController.getInstance().applyLanguage(paramAnonymousAdapterView, true);
            LanguageSelectActivity.this.parentLayout.rebuildAllFragmentViews(false);
          }
          LanguageSelectActivity.this.finishFragment();
          return;
          paramAnonymousAdapterView = paramAnonymousView;
          if (paramAnonymousInt >= 0)
          {
            paramAnonymousAdapterView = paramAnonymousView;
            if (paramAnonymousInt < LocaleController.getInstance().sortedLanguages.size()) {
              paramAnonymousAdapterView = (LocaleController.LocaleInfo)LocaleController.getInstance().sortedLanguages.get(paramAnonymousInt);
            }
          }
        }
      }
    });
    this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
    {
      public boolean onItemLongClick(final AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
      {
        paramAnonymousView = null;
        if ((LanguageSelectActivity.this.searching) && (LanguageSelectActivity.this.searchWas))
        {
          paramAnonymousAdapterView = paramAnonymousView;
          if (paramAnonymousInt >= 0)
          {
            paramAnonymousAdapterView = paramAnonymousView;
            if (paramAnonymousInt < LanguageSelectActivity.this.searchResult.size()) {
              paramAnonymousAdapterView = (LocaleController.LocaleInfo)LanguageSelectActivity.this.searchResult.get(paramAnonymousInt);
            }
          }
        }
        while ((paramAnonymousAdapterView == null) || (paramAnonymousAdapterView.pathToFile == null) || (LanguageSelectActivity.this.getParentActivity() == null))
        {
          return false;
          paramAnonymousAdapterView = paramAnonymousView;
          if (paramAnonymousInt >= 0)
          {
            paramAnonymousAdapterView = paramAnonymousView;
            if (paramAnonymousInt < LocaleController.getInstance().sortedLanguages.size()) {
              paramAnonymousAdapterView = (LocaleController.LocaleInfo)LocaleController.getInstance().sortedLanguages.get(paramAnonymousInt);
            }
          }
        }
        paramAnonymousView = new AlertDialog.Builder(LanguageSelectActivity.this.getParentActivity());
        paramAnonymousView.setMessage(LocaleController.getString("DeleteLocalization", 2131165573));
        paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165299));
        paramAnonymousView.setPositiveButton(LocaleController.getString("Delete", 2131165560), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            if (LocaleController.getInstance().deleteLanguage(paramAnonymousAdapterView))
            {
              if (LanguageSelectActivity.this.searchResult != null) {
                LanguageSelectActivity.this.searchResult.remove(paramAnonymousAdapterView);
              }
              if (LanguageSelectActivity.this.listAdapter != null) {
                LanguageSelectActivity.this.listAdapter.notifyDataSetChanged();
              }
              if (LanguageSelectActivity.this.searchListViewAdapter != null) {
                LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
              }
            }
          }
        });
        paramAnonymousView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
        LanguageSelectActivity.this.showDialog(paramAnonymousView.create());
        return true;
      }
    });
    this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
    {
      public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
      {
        if ((paramAnonymousInt == 1) && (LanguageSelectActivity.this.searching) && (LanguageSelectActivity.this.searchWas)) {
          AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
        }
      }
    });
    return this.fragmentView;
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
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
            LanguageSelectActivity.this.searchTimer.cancel();
            LanguageSelectActivity.access$702(LanguageSelectActivity.this, null);
            LanguageSelectActivity.this.processSearch(paramString);
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
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return true;
    }
    
    public int getCount()
    {
      if (LocaleController.getInstance().sortedLanguages == null) {
        return 0;
      }
      return LocaleController.getInstance().sortedLanguages.size();
    }
    
    public Object getItem(int paramInt)
    {
      return null;
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
      Object localObject = (LocaleController.LocaleInfo)LocaleController.getInstance().sortedLanguages.get(paramInt);
      paramView = (TextSettingsCell)paramViewGroup;
      localObject = ((LocaleController.LocaleInfo)localObject).name;
      if (paramInt != LocaleController.getInstance().sortedLanguages.size() - 1) {}
      for (boolean bool = true;; bool = false)
      {
        paramView.setText((String)localObject, bool);
        return paramViewGroup;
      }
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return (LocaleController.getInstance().sortedLanguages == null) || (LocaleController.getInstance().sortedLanguages.size() == 0);
    }
    
    public boolean isEnabled(int paramInt)
    {
      return true;
    }
  }
  
  private class SearchAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public SearchAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return true;
    }
    
    public int getCount()
    {
      if (LanguageSelectActivity.this.searchResult == null) {
        return 0;
      }
      return LanguageSelectActivity.this.searchResult.size();
    }
    
    public Object getItem(int paramInt)
    {
      return null;
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
      Object localObject = (LocaleController.LocaleInfo)LanguageSelectActivity.this.searchResult.get(paramInt);
      paramView = (TextSettingsCell)paramViewGroup;
      localObject = ((LocaleController.LocaleInfo)localObject).name;
      if (paramInt != LanguageSelectActivity.this.searchResult.size() - 1) {}
      for (boolean bool = true;; bool = false)
      {
        paramView.setText((String)localObject, bool);
        return paramViewGroup;
      }
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return (LanguageSelectActivity.this.searchResult == null) || (LanguageSelectActivity.this.searchResult.size() == 0);
    }
    
    public boolean isEnabled(int paramInt)
    {
      return true;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/LanguageSelectActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */