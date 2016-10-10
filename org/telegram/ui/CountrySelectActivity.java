package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.CountryAdapter;
import org.telegram.ui.Adapters.CountryAdapter.Country;
import org.telegram.ui.Adapters.CountrySearchAdapter;
import org.telegram.ui.Components.LetterSectionsListView;

public class CountrySelectActivity
  extends BaseFragment
{
  private CountrySelectActivityDelegate delegate;
  private TextView emptyTextView;
  private LetterSectionsListView listView;
  private CountryAdapter listViewAdapter;
  private CountrySearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ChooseCountry", 2131165505));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          CountrySelectActivity.this.finishFragment();
        }
      }
    });
    this.actionBar.createMenu().addItem(0, 2130837711).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        CountrySelectActivity.this.searchListViewAdapter.search(null);
        CountrySelectActivity.access$002(CountrySelectActivity.this, false);
        CountrySelectActivity.access$202(CountrySelectActivity.this, false);
        CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.listViewAdapter);
        CountrySelectActivity.this.listView.setFastScrollAlwaysVisible(true);
        CountrySelectActivity.this.listView.setFastScrollEnabled(true);
        CountrySelectActivity.this.listView.setVerticalScrollBarEnabled(false);
        CountrySelectActivity.this.emptyTextView.setText(LocaleController.getString("ChooseCountry", 2131165505));
      }
      
      public void onSearchExpand()
      {
        CountrySelectActivity.access$002(CountrySelectActivity.this, true);
      }
      
      public void onTextChanged(EditText paramAnonymousEditText)
      {
        paramAnonymousEditText = paramAnonymousEditText.getText().toString();
        CountrySelectActivity.this.searchListViewAdapter.search(paramAnonymousEditText);
        if (paramAnonymousEditText.length() != 0)
        {
          CountrySelectActivity.access$202(CountrySelectActivity.this, true);
          if (CountrySelectActivity.this.listView != null)
          {
            CountrySelectActivity.this.listView.setAdapter(CountrySelectActivity.this.searchListViewAdapter);
            CountrySelectActivity.this.listView.setFastScrollAlwaysVisible(false);
            CountrySelectActivity.this.listView.setFastScrollEnabled(false);
            CountrySelectActivity.this.listView.setVerticalScrollBarEnabled(true);
          }
          if (CountrySelectActivity.this.emptyTextView == null) {}
        }
      }
    }).getSearchField().setHint(LocaleController.getString("Search", 2131166206));
    this.searching = false;
    this.searchWas = false;
    this.listViewAdapter = new CountryAdapter(paramContext);
    this.searchListViewAdapter = new CountrySearchAdapter(paramContext, this.listViewAdapter.getCountries());
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
    this.listView = new LetterSectionsListView(paramContext);
    this.listView.setEmptyView(localLinearLayout);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setDivider(null);
    this.listView.setDividerHeight(0);
    this.listView.setFastScrollEnabled(true);
    this.listView.setScrollBarStyle(33554432);
    this.listView.setAdapter(this.listViewAdapter);
    this.listView.setFastScrollAlwaysVisible(true);
    paramContext = this.listView;
    if (LocaleController.isRTL) {}
    for (int i = 1;; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      ((FrameLayout)this.fragmentView).addView(this.listView);
      paramContext = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
      paramContext.width = -1;
      paramContext.height = -1;
      this.listView.setLayoutParams(paramContext);
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if ((CountrySelectActivity.this.searching) && (CountrySelectActivity.this.searchWas))
          {
            paramAnonymousAdapterView = CountrySelectActivity.this.searchListViewAdapter.getItem(paramAnonymousInt);
            if (paramAnonymousInt >= 0) {
              break label91;
            }
          }
          label91:
          do
          {
            int i;
            int j;
            do
            {
              return;
              i = CountrySelectActivity.this.listViewAdapter.getSectionForPosition(paramAnonymousInt);
              j = CountrySelectActivity.this.listViewAdapter.getPositionInSectionForPosition(paramAnonymousInt);
            } while ((j < 0) || (i < 0));
            paramAnonymousAdapterView = CountrySelectActivity.this.listViewAdapter.getItem(i, j);
            break;
            CountrySelectActivity.this.finishFragment();
          } while ((paramAnonymousAdapterView == null) || (CountrySelectActivity.this.delegate == null));
          CountrySelectActivity.this.delegate.didSelectCountry(paramAnonymousAdapterView.name);
        }
      });
      this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (paramAnonymousAbsListView.isFastScrollEnabled()) {
            AndroidUtilities.clearDrawableAnimation(paramAnonymousAbsListView);
          }
        }
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == 1) && (CountrySelectActivity.this.searching) && (CountrySelectActivity.this.searchWas)) {
            AndroidUtilities.hideKeyboard(CountrySelectActivity.this.getParentActivity().getCurrentFocus());
          }
        }
      });
      return this.fragmentView;
    }
  }
  
  public boolean onFragmentCreate()
  {
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null) {
      this.listViewAdapter.notifyDataSetChanged();
    }
  }
  
  public void setCountrySelectActivityDelegate(CountrySelectActivityDelegate paramCountrySelectActivityDelegate)
  {
    this.delegate = paramCountrySelectActivityDelegate;
  }
  
  public static abstract interface CountrySelectActivityDelegate
  {
    public abstract void didSelectCountry(String paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/CountrySelectActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */