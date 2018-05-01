package android.support.v4.view;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;

@TargetApi(14)
@RequiresApi(14)
class MenuItemCompatIcs
{
  public static boolean collapseActionView(MenuItem paramMenuItem)
  {
    return paramMenuItem.collapseActionView();
  }
  
  public static boolean expandActionView(MenuItem paramMenuItem)
  {
    return paramMenuItem.expandActionView();
  }
  
  public static boolean isActionViewExpanded(MenuItem paramMenuItem)
  {
    return paramMenuItem.isActionViewExpanded();
  }
  
  public static MenuItem setOnActionExpandListener(MenuItem paramMenuItem, SupportActionExpandProxy paramSupportActionExpandProxy)
  {
    return paramMenuItem.setOnActionExpandListener(new OnActionExpandListenerWrapper(paramSupportActionExpandProxy));
  }
  
  static class OnActionExpandListenerWrapper
    implements MenuItem.OnActionExpandListener
  {
    private MenuItemCompatIcs.SupportActionExpandProxy mWrapped;
    
    public OnActionExpandListenerWrapper(MenuItemCompatIcs.SupportActionExpandProxy paramSupportActionExpandProxy)
    {
      this.mWrapped = paramSupportActionExpandProxy;
    }
    
    public boolean onMenuItemActionCollapse(MenuItem paramMenuItem)
    {
      return this.mWrapped.onMenuItemActionCollapse(paramMenuItem);
    }
    
    public boolean onMenuItemActionExpand(MenuItem paramMenuItem)
    {
      return this.mWrapped.onMenuItemActionExpand(paramMenuItem);
    }
  }
  
  static abstract interface SupportActionExpandProxy
  {
    public abstract boolean onMenuItemActionCollapse(MenuItem paramMenuItem);
    
    public abstract boolean onMenuItemActionExpand(MenuItem paramMenuItem);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/view/MenuItemCompatIcs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */