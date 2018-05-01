package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class LanguageSelectActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private EmptyTextProgressView emptyView;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private ListAdapter searchListViewAdapter;
  private ArrayList<LocaleController.LocaleInfo> searchResult;
  private Timer searchTimer;
  private boolean searchWas;
  private boolean searching;
  private ArrayList<LocaleController.LocaleInfo> sortedLanguages;
  
  private void fillLanguages()
  {
    this.sortedLanguages = new ArrayList(LocaleController.getInstance().languages);
    final LocaleController.LocaleInfo localLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
    Collections.sort(this.sortedLanguages, new Comparator()
    {
      public int compare(LocaleController.LocaleInfo paramAnonymousLocaleInfo1, LocaleController.LocaleInfo paramAnonymousLocaleInfo2)
      {
        int i;
        if (paramAnonymousLocaleInfo1 == localLocaleInfo) {
          i = -1;
        }
        for (;;)
        {
          return i;
          if (paramAnonymousLocaleInfo2 == localLocaleInfo) {
            i = 1;
          } else {
            i = paramAnonymousLocaleInfo1.name.compareTo(paramAnonymousLocaleInfo2.name);
          }
        }
      }
    });
  }
  
  private void processSearch(final String paramString)
  {
    Utilities.searchQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (paramString.trim().toLowerCase().length() == 0) {
          LanguageSelectActivity.this.updateSearchResults(new ArrayList());
        }
        for (;;)
        {
          return;
          System.currentTimeMillis();
          ArrayList localArrayList = new ArrayList();
          for (int i = 0; i < LanguageSelectActivity.this.sortedLanguages.size(); i++)
          {
            LocaleController.LocaleInfo localLocaleInfo = (LocaleController.LocaleInfo)LanguageSelectActivity.this.sortedLanguages.get(i);
            if ((localLocaleInfo.name.toLowerCase().startsWith(paramString)) || (localLocaleInfo.nameEnglish.toLowerCase().startsWith(paramString))) {
              localArrayList.add(localLocaleInfo);
            }
          }
          LanguageSelectActivity.this.updateSearchResults(localArrayList);
        }
      }
    });
  }
  
  private void updateSearchResults(final ArrayList<LocaleController.LocaleInfo> paramArrayList)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        LanguageSelectActivity.access$702(LanguageSelectActivity.this, paramArrayList);
        LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
      }
    });
  }
  
  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("Language", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          LanguageSelectActivity.this.finishFragment();
        }
      }
    });
    this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        LanguageSelectActivity.this.search(null);
        LanguageSelectActivity.access$002(LanguageSelectActivity.this, false);
        LanguageSelectActivity.access$102(LanguageSelectActivity.this, false);
        if (LanguageSelectActivity.this.listView != null)
        {
          LanguageSelectActivity.this.emptyView.setVisibility(8);
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
    }).getSearchField().setHint(LocaleController.getString("Search", NUM));
    this.listAdapter = new ListAdapter(paramContext, false);
    this.searchListViewAdapter = new ListAdapter(paramContext, true);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setText(LocaleController.getString("NoResult", NUM));
    this.emptyView.showTextView();
    this.emptyView.setShowAtCenter(true);
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setEmptyView(this.emptyView);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setAdapter(this.listAdapter);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if ((LanguageSelectActivity.this.getParentActivity() == null) || (LanguageSelectActivity.this.parentLayout == null)) {
          return;
        }
        Object localObject = null;
        if ((LanguageSelectActivity.this.searching) && (LanguageSelectActivity.this.searchWas))
        {
          paramAnonymousView = (View)localObject;
          if (paramAnonymousInt >= 0)
          {
            paramAnonymousView = (View)localObject;
            if (paramAnonymousInt < LanguageSelectActivity.this.searchResult.size()) {
              paramAnonymousView = (LocaleController.LocaleInfo)LanguageSelectActivity.this.searchResult.get(paramAnonymousInt);
            }
          }
        }
        for (;;)
        {
          if (paramAnonymousView != null)
          {
            LocaleController.getInstance().applyLanguage(paramAnonymousView, true, false, false, true, LanguageSelectActivity.this.currentAccount);
            LanguageSelectActivity.this.parentLayout.rebuildAllFragmentViews(false, false);
          }
          LanguageSelectActivity.this.finishFragment();
          break;
          paramAnonymousView = (View)localObject;
          if (paramAnonymousInt >= 0)
          {
            paramAnonymousView = (View)localObject;
            if (paramAnonymousInt < LanguageSelectActivity.this.sortedLanguages.size()) {
              paramAnonymousView = (LocaleController.LocaleInfo)LanguageSelectActivity.this.sortedLanguages.get(paramAnonymousInt);
            }
          }
        }
      }
    });
    this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
    {
      public boolean onItemClick(final View paramAnonymousView, int paramAnonymousInt)
      {
        AlertDialog.Builder localBuilder = null;
        if ((LanguageSelectActivity.this.searching) && (LanguageSelectActivity.this.searchWas))
        {
          paramAnonymousView = localBuilder;
          if (paramAnonymousInt >= 0)
          {
            paramAnonymousView = localBuilder;
            if (paramAnonymousInt < LanguageSelectActivity.this.searchResult.size()) {
              paramAnonymousView = (LocaleController.LocaleInfo)LanguageSelectActivity.this.searchResult.get(paramAnonymousInt);
            }
          }
          if ((paramAnonymousView != null) && (paramAnonymousView.pathToFile != null) && (LanguageSelectActivity.this.getParentActivity() != null) && (!paramAnonymousView.isRemote())) {
            break label133;
          }
        }
        for (boolean bool = false;; bool = true)
        {
          return bool;
          paramAnonymousView = localBuilder;
          if (paramAnonymousInt < 0) {
            break;
          }
          paramAnonymousView = localBuilder;
          if (paramAnonymousInt >= LanguageSelectActivity.this.sortedLanguages.size()) {
            break;
          }
          paramAnonymousView = (LocaleController.LocaleInfo)LanguageSelectActivity.this.sortedLanguages.get(paramAnonymousInt);
          break;
          label133:
          localBuilder = new AlertDialog.Builder(LanguageSelectActivity.this.getParentActivity());
          localBuilder.setMessage(LocaleController.getString("DeleteLocalization", NUM));
          localBuilder.setTitle(LocaleController.getString("AppName", NUM));
          localBuilder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              if (LocaleController.getInstance().deleteLanguage(paramAnonymousView, LanguageSelectActivity.this.currentAccount))
              {
                LanguageSelectActivity.this.fillLanguages();
                if (LanguageSelectActivity.this.searchResult != null) {
                  LanguageSelectActivity.this.searchResult.remove(paramAnonymousView);
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
          localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
          LanguageSelectActivity.this.showDialog(localBuilder.create());
        }
      }
    });
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
      {
        if ((paramAnonymousInt == 1) && (LanguageSelectActivity.this.searching) && (LanguageSelectActivity.this.searchWas)) {
          AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
        }
      }
    });
    return this.fragmentView;
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if ((paramInt1 == NotificationCenter.suggestedLangpack) && (this.listAdapter != null))
    {
      fillLanguages();
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, 0, new Class[] { LanguageCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { LanguageCell.class }, new String[] { "textView2" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, 0, new Class[] { LanguageCell.class }, new String[] { "checkImage" }, null, null, null, "featuredStickers_addedIcon") };
  }
  
  public boolean onFragmentCreate()
  {
    fillLanguages();
    LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
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
    if (paramString == null) {
      this.searchResult = null;
    }
    for (;;)
    {
      return;
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
              LanguageSelectActivity.access$1302(LanguageSelectActivity.this, null);
              LanguageSelectActivity.this.processSearch(paramString);
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
        }, 100L, 300L);
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
  }
  
  private class ListAdapter
    extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    private boolean search;
    
    public ListAdapter(Context paramContext, boolean paramBoolean)
    {
      this.mContext = paramContext;
      this.search = paramBoolean;
    }
    
    public int getItemCount()
    {
      int i;
      if (this.search) {
        if (LanguageSelectActivity.this.searchResult == null) {
          i = 0;
        }
      }
      for (;;)
      {
        return i;
        i = LanguageSelectActivity.this.searchResult.size();
        continue;
        i = LanguageSelectActivity.this.sortedLanguages.size();
      }
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = true;
      LanguageCell localLanguageCell = (LanguageCell)paramViewHolder.itemView;
      if (this.search)
      {
        paramViewHolder = (LocaleController.LocaleInfo)LanguageSelectActivity.this.searchResult.get(paramInt);
        if (paramInt == LanguageSelectActivity.this.searchResult.size() - 1)
        {
          paramInt = 1;
          if (!paramViewHolder.isLocal()) {
            break label176;
          }
          String str = String.format("%1$s (%2$s)", new Object[] { paramViewHolder.name, LocaleController.getString("LanguageCustom", NUM) });
          if (paramInt != 0) {
            break label170;
          }
          bool2 = true;
          label93:
          localLanguageCell.setLanguage(paramViewHolder, str, bool2);
          if (paramViewHolder != LocaleController.getInstance().getCurrentLocaleInfo()) {
            break label201;
          }
        }
      }
      label170:
      label176:
      label201:
      for (boolean bool2 = bool1;; bool2 = false)
      {
        localLanguageCell.setLanguageSelected(bool2);
        return;
        paramInt = 0;
        break;
        paramViewHolder = (LocaleController.LocaleInfo)LanguageSelectActivity.this.sortedLanguages.get(paramInt);
        if (paramInt == LanguageSelectActivity.this.sortedLanguages.size() - 1) {}
        for (paramInt = 1;; paramInt = 0) {
          break;
        }
        bool2 = false;
        break label93;
        if (paramInt == 0) {}
        for (bool2 = true;; bool2 = false)
        {
          localLanguageCell.setLanguage(paramViewHolder, null, bool2);
          break;
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new RecyclerListView.Holder(new LanguageCell(this.mContext, false));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/LanguageSelectActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */