package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import org.telegram.messenger.AndroidUtilities;

public class ActionBarMenu
  extends LinearLayout
{
  protected ActionBar parentActionBar;
  
  public ActionBarMenu(Context paramContext)
  {
    super(paramContext);
  }
  
  public ActionBarMenu(Context paramContext, ActionBar paramActionBar)
  {
    super(paramContext);
    setOrientation(0);
    this.parentActionBar = paramActionBar;
  }
  
  public ActionBarMenuItem addItem(int paramInt1, int paramInt2)
  {
    return addItem(paramInt1, paramInt2, this.parentActionBar.itemsBackgroundColor);
  }
  
  public ActionBarMenuItem addItem(int paramInt1, int paramInt2, int paramInt3)
  {
    return addItem(paramInt1, paramInt2, paramInt3, null, AndroidUtilities.dp(48.0F));
  }
  
  public ActionBarMenuItem addItem(int paramInt1, int paramInt2, int paramInt3, Drawable paramDrawable, int paramInt4)
  {
    ActionBarMenuItem localActionBarMenuItem = new ActionBarMenuItem(getContext(), this, paramInt3);
    localActionBarMenuItem.setTag(Integer.valueOf(paramInt1));
    if (paramDrawable != null) {
      localActionBarMenuItem.iconView.setImageDrawable(paramDrawable);
    }
    for (;;)
    {
      addView(localActionBarMenuItem);
      paramDrawable = (LinearLayout.LayoutParams)localActionBarMenuItem.getLayoutParams();
      paramDrawable.height = -1;
      paramDrawable.width = paramInt4;
      localActionBarMenuItem.setLayoutParams(paramDrawable);
      localActionBarMenuItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ActionBarMenuItem localActionBarMenuItem = (ActionBarMenuItem)paramAnonymousView;
          if (localActionBarMenuItem.hasSubMenu())
          {
            if (ActionBarMenu.this.parentActionBar.actionBarMenuOnItemClick.canOpenMenu()) {
              localActionBarMenuItem.toggleSubMenu();
            }
            return;
          }
          if (localActionBarMenuItem.isSearchField())
          {
            ActionBarMenu.this.parentActionBar.onSearchFieldVisibilityChanged(localActionBarMenuItem.toggleSearch(true));
            return;
          }
          ActionBarMenu.this.onItemClick(((Integer)paramAnonymousView.getTag()).intValue());
        }
      });
      return localActionBarMenuItem;
      localActionBarMenuItem.iconView.setImageResource(paramInt2);
    }
  }
  
  public ActionBarMenuItem addItem(int paramInt, Drawable paramDrawable)
  {
    return addItem(paramInt, 0, this.parentActionBar.itemsBackgroundColor, paramDrawable, AndroidUtilities.dp(48.0F));
  }
  
  public View addItemResource(int paramInt1, int paramInt2)
  {
    View localView = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(paramInt2, null);
    localView.setTag(Integer.valueOf(paramInt1));
    addView(localView);
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)localView.getLayoutParams();
    localLayoutParams.height = -1;
    localView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.parentActionBar.itemsBackgroundColor));
    localView.setLayoutParams(localLayoutParams);
    localView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ActionBarMenu.this.onItemClick(((Integer)paramAnonymousView.getTag()).intValue());
      }
    });
    return localView;
  }
  
  public ActionBarMenuItem addItemWithWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    return addItem(paramInt1, paramInt2, this.parentActionBar.itemsBackgroundColor, null, paramInt3);
  }
  
  public void clearItems()
  {
    removeAllViews();
  }
  
  public void closeSearchField()
  {
    int j = getChildCount();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        Object localObject = getChildAt(i);
        if ((localObject instanceof ActionBarMenuItem))
        {
          localObject = (ActionBarMenuItem)localObject;
          if (((ActionBarMenuItem)localObject).isSearchField()) {
            this.parentActionBar.onSearchFieldVisibilityChanged(((ActionBarMenuItem)localObject).toggleSearch(false));
          }
        }
      }
      else
      {
        return;
      }
      i += 1;
    }
  }
  
  public ActionBarMenuItem getItem(int paramInt)
  {
    View localView = findViewWithTag(Integer.valueOf(paramInt));
    if ((localView instanceof ActionBarMenuItem)) {
      return (ActionBarMenuItem)localView;
    }
    return null;
  }
  
  public void hideAllPopupMenus()
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = getChildAt(i);
      if ((localView instanceof ActionBarMenuItem)) {
        ((ActionBarMenuItem)localView).closeSubMenu();
      }
      i += 1;
    }
  }
  
  public void onItemClick(int paramInt)
  {
    if (this.parentActionBar.actionBarMenuOnItemClick != null) {
      this.parentActionBar.actionBarMenuOnItemClick.onItemClick(paramInt);
    }
  }
  
  public void onMenuButtonPressed()
  {
    int j = getChildCount();
    int i = 0;
    Object localObject;
    if (i < j)
    {
      localObject = getChildAt(i);
      if ((localObject instanceof ActionBarMenuItem))
      {
        localObject = (ActionBarMenuItem)localObject;
        if (((ActionBarMenuItem)localObject).getVisibility() == 0) {
          break label44;
        }
      }
    }
    label44:
    do
    {
      i += 1;
      break;
      if (((ActionBarMenuItem)localObject).hasSubMenu())
      {
        ((ActionBarMenuItem)localObject).toggleSubMenu();
        return;
      }
    } while (!((ActionBarMenuItem)localObject).overrideMenuClick);
    onItemClick(((Integer)((ActionBarMenuItem)localObject).getTag()).intValue());
  }
  
  public void openSearchField(boolean paramBoolean, String paramString)
  {
    int j = getChildCount();
    int i = 0;
    for (;;)
    {
      if (i < j)
      {
        Object localObject = getChildAt(i);
        if ((localObject instanceof ActionBarMenuItem))
        {
          localObject = (ActionBarMenuItem)localObject;
          if (((ActionBarMenuItem)localObject).isSearchField())
          {
            if (paramBoolean) {
              this.parentActionBar.onSearchFieldVisibilityChanged(((ActionBarMenuItem)localObject).toggleSearch(true));
            }
            ((ActionBarMenuItem)localObject).getSearchField().setText(paramString);
            ((ActionBarMenuItem)localObject).getSearchField().setSelection(paramString.length());
          }
        }
      }
      else
      {
        return;
      }
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/ActionBarMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */