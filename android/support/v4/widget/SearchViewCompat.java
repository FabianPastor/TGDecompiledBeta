package android.support.v4.widget;

import android.content.ComponentName;
import android.content.Context;
import android.os.Build.VERSION;
import android.view.View;

public final class SearchViewCompat
{
  private static final SearchViewCompatImpl IMPL = new SearchViewCompatStubImpl();
  
  static
  {
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new SearchViewCompatIcsImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 11)
    {
      IMPL = new SearchViewCompatHoneycombImpl();
      return;
    }
  }
  
  private SearchViewCompat(Context paramContext) {}
  
  public static CharSequence getQuery(View paramView)
  {
    return IMPL.getQuery(paramView);
  }
  
  public static boolean isIconified(View paramView)
  {
    return IMPL.isIconified(paramView);
  }
  
  public static boolean isQueryRefinementEnabled(View paramView)
  {
    return IMPL.isQueryRefinementEnabled(paramView);
  }
  
  public static boolean isSubmitButtonEnabled(View paramView)
  {
    return IMPL.isSubmitButtonEnabled(paramView);
  }
  
  public static View newSearchView(Context paramContext)
  {
    return IMPL.newSearchView(paramContext);
  }
  
  public static void setIconified(View paramView, boolean paramBoolean)
  {
    IMPL.setIconified(paramView, paramBoolean);
  }
  
  public static void setImeOptions(View paramView, int paramInt)
  {
    IMPL.setImeOptions(paramView, paramInt);
  }
  
  public static void setInputType(View paramView, int paramInt)
  {
    IMPL.setInputType(paramView, paramInt);
  }
  
  public static void setMaxWidth(View paramView, int paramInt)
  {
    IMPL.setMaxWidth(paramView, paramInt);
  }
  
  public static void setOnCloseListener(View paramView, OnCloseListener paramOnCloseListener)
  {
    IMPL.setOnCloseListener(paramView, paramOnCloseListener);
  }
  
  public static void setOnQueryTextListener(View paramView, OnQueryTextListener paramOnQueryTextListener)
  {
    IMPL.setOnQueryTextListener(paramView, paramOnQueryTextListener);
  }
  
  public static void setQuery(View paramView, CharSequence paramCharSequence, boolean paramBoolean)
  {
    IMPL.setQuery(paramView, paramCharSequence, paramBoolean);
  }
  
  public static void setQueryHint(View paramView, CharSequence paramCharSequence)
  {
    IMPL.setQueryHint(paramView, paramCharSequence);
  }
  
  public static void setQueryRefinementEnabled(View paramView, boolean paramBoolean)
  {
    IMPL.setQueryRefinementEnabled(paramView, paramBoolean);
  }
  
  public static void setSearchableInfo(View paramView, ComponentName paramComponentName)
  {
    IMPL.setSearchableInfo(paramView, paramComponentName);
  }
  
  public static void setSubmitButtonEnabled(View paramView, boolean paramBoolean)
  {
    IMPL.setSubmitButtonEnabled(paramView, paramBoolean);
  }
  
  public static abstract interface OnCloseListener
  {
    public abstract boolean onClose();
  }
  
  @Deprecated
  public static abstract class OnCloseListenerCompat
    implements SearchViewCompat.OnCloseListener
  {
    public boolean onClose()
    {
      return false;
    }
  }
  
  public static abstract interface OnQueryTextListener
  {
    public abstract boolean onQueryTextChange(String paramString);
    
    public abstract boolean onQueryTextSubmit(String paramString);
  }
  
  @Deprecated
  public static abstract class OnQueryTextListenerCompat
    implements SearchViewCompat.OnQueryTextListener
  {
    public boolean onQueryTextChange(String paramString)
    {
      return false;
    }
    
    public boolean onQueryTextSubmit(String paramString)
    {
      return false;
    }
  }
  
  static class SearchViewCompatHoneycombImpl
    extends SearchViewCompat.SearchViewCompatStubImpl
  {
    protected void checkIfLegalArg(View paramView)
    {
      SearchViewCompatHoneycomb.checkIfLegalArg(paramView);
    }
    
    public CharSequence getQuery(View paramView)
    {
      checkIfLegalArg(paramView);
      return SearchViewCompatHoneycomb.getQuery(paramView);
    }
    
    public boolean isIconified(View paramView)
    {
      checkIfLegalArg(paramView);
      return SearchViewCompatHoneycomb.isIconified(paramView);
    }
    
    public boolean isQueryRefinementEnabled(View paramView)
    {
      checkIfLegalArg(paramView);
      return SearchViewCompatHoneycomb.isQueryRefinementEnabled(paramView);
    }
    
    public boolean isSubmitButtonEnabled(View paramView)
    {
      checkIfLegalArg(paramView);
      return SearchViewCompatHoneycomb.isSubmitButtonEnabled(paramView);
    }
    
    public Object newOnCloseListener(final SearchViewCompat.OnCloseListener paramOnCloseListener)
    {
      SearchViewCompatHoneycomb.newOnCloseListener(new SearchViewCompatHoneycomb.OnCloseListenerCompatBridge()
      {
        public boolean onClose()
        {
          return paramOnCloseListener.onClose();
        }
      });
    }
    
    public Object newOnQueryTextListener(final SearchViewCompat.OnQueryTextListener paramOnQueryTextListener)
    {
      SearchViewCompatHoneycomb.newOnQueryTextListener(new SearchViewCompatHoneycomb.OnQueryTextListenerCompatBridge()
      {
        public boolean onQueryTextChange(String paramAnonymousString)
        {
          return paramOnQueryTextListener.onQueryTextChange(paramAnonymousString);
        }
        
        public boolean onQueryTextSubmit(String paramAnonymousString)
        {
          return paramOnQueryTextListener.onQueryTextSubmit(paramAnonymousString);
        }
      });
    }
    
    public View newSearchView(Context paramContext)
    {
      return SearchViewCompatHoneycomb.newSearchView(paramContext);
    }
    
    public void setIconified(View paramView, boolean paramBoolean)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatHoneycomb.setIconified(paramView, paramBoolean);
    }
    
    public void setMaxWidth(View paramView, int paramInt)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatHoneycomb.setMaxWidth(paramView, paramInt);
    }
    
    public void setOnCloseListener(View paramView, SearchViewCompat.OnCloseListener paramOnCloseListener)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatHoneycomb.setOnCloseListener(paramView, newOnCloseListener(paramOnCloseListener));
    }
    
    public void setOnQueryTextListener(View paramView, SearchViewCompat.OnQueryTextListener paramOnQueryTextListener)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatHoneycomb.setOnQueryTextListener(paramView, newOnQueryTextListener(paramOnQueryTextListener));
    }
    
    public void setQuery(View paramView, CharSequence paramCharSequence, boolean paramBoolean)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatHoneycomb.setQuery(paramView, paramCharSequence, paramBoolean);
    }
    
    public void setQueryHint(View paramView, CharSequence paramCharSequence)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatHoneycomb.setQueryHint(paramView, paramCharSequence);
    }
    
    public void setQueryRefinementEnabled(View paramView, boolean paramBoolean)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatHoneycomb.setQueryRefinementEnabled(paramView, paramBoolean);
    }
    
    public void setSearchableInfo(View paramView, ComponentName paramComponentName)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatHoneycomb.setSearchableInfo(paramView, paramComponentName);
    }
    
    public void setSubmitButtonEnabled(View paramView, boolean paramBoolean)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatHoneycomb.setSubmitButtonEnabled(paramView, paramBoolean);
    }
  }
  
  static class SearchViewCompatIcsImpl
    extends SearchViewCompat.SearchViewCompatHoneycombImpl
  {
    public View newSearchView(Context paramContext)
    {
      return SearchViewCompatIcs.newSearchView(paramContext);
    }
    
    public void setImeOptions(View paramView, int paramInt)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatIcs.setImeOptions(paramView, paramInt);
    }
    
    public void setInputType(View paramView, int paramInt)
    {
      checkIfLegalArg(paramView);
      SearchViewCompatIcs.setInputType(paramView, paramInt);
    }
  }
  
  static abstract interface SearchViewCompatImpl
  {
    public abstract CharSequence getQuery(View paramView);
    
    public abstract boolean isIconified(View paramView);
    
    public abstract boolean isQueryRefinementEnabled(View paramView);
    
    public abstract boolean isSubmitButtonEnabled(View paramView);
    
    public abstract Object newOnCloseListener(SearchViewCompat.OnCloseListener paramOnCloseListener);
    
    public abstract Object newOnQueryTextListener(SearchViewCompat.OnQueryTextListener paramOnQueryTextListener);
    
    public abstract View newSearchView(Context paramContext);
    
    public abstract void setIconified(View paramView, boolean paramBoolean);
    
    public abstract void setImeOptions(View paramView, int paramInt);
    
    public abstract void setInputType(View paramView, int paramInt);
    
    public abstract void setMaxWidth(View paramView, int paramInt);
    
    public abstract void setOnCloseListener(View paramView, SearchViewCompat.OnCloseListener paramOnCloseListener);
    
    public abstract void setOnQueryTextListener(View paramView, SearchViewCompat.OnQueryTextListener paramOnQueryTextListener);
    
    public abstract void setQuery(View paramView, CharSequence paramCharSequence, boolean paramBoolean);
    
    public abstract void setQueryHint(View paramView, CharSequence paramCharSequence);
    
    public abstract void setQueryRefinementEnabled(View paramView, boolean paramBoolean);
    
    public abstract void setSearchableInfo(View paramView, ComponentName paramComponentName);
    
    public abstract void setSubmitButtonEnabled(View paramView, boolean paramBoolean);
  }
  
  static class SearchViewCompatStubImpl
    implements SearchViewCompat.SearchViewCompatImpl
  {
    public CharSequence getQuery(View paramView)
    {
      return null;
    }
    
    public boolean isIconified(View paramView)
    {
      return true;
    }
    
    public boolean isQueryRefinementEnabled(View paramView)
    {
      return false;
    }
    
    public boolean isSubmitButtonEnabled(View paramView)
    {
      return false;
    }
    
    public Object newOnCloseListener(SearchViewCompat.OnCloseListener paramOnCloseListener)
    {
      return null;
    }
    
    public Object newOnQueryTextListener(SearchViewCompat.OnQueryTextListener paramOnQueryTextListener)
    {
      return null;
    }
    
    public View newSearchView(Context paramContext)
    {
      return null;
    }
    
    public void setIconified(View paramView, boolean paramBoolean) {}
    
    public void setImeOptions(View paramView, int paramInt) {}
    
    public void setInputType(View paramView, int paramInt) {}
    
    public void setMaxWidth(View paramView, int paramInt) {}
    
    public void setOnCloseListener(View paramView, SearchViewCompat.OnCloseListener paramOnCloseListener) {}
    
    public void setOnQueryTextListener(View paramView, SearchViewCompat.OnQueryTextListener paramOnQueryTextListener) {}
    
    public void setQuery(View paramView, CharSequence paramCharSequence, boolean paramBoolean) {}
    
    public void setQueryHint(View paramView, CharSequence paramCharSequence) {}
    
    public void setQueryRefinementEnabled(View paramView, boolean paramBoolean) {}
    
    public void setSearchableInfo(View paramView, ComponentName paramComponentName) {}
    
    public void setSubmitButtonEnabled(View paramView, boolean paramBoolean) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/widget/SearchViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */