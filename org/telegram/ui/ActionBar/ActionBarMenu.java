package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.EditTextBoldCursor;

public class ActionBarMenu
  extends LinearLayout
{
  protected boolean isActionMode;
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
    if (this.isActionMode) {}
    for (int i = this.parentActionBar.itemsActionModeBackgroundColor;; i = this.parentActionBar.itemsBackgroundColor) {
      return addItem(paramInt1, paramInt2, i);
    }
  }
  
  public ActionBarMenuItem addItem(int paramInt1, int paramInt2, int paramInt3)
  {
    return addItem(paramInt1, paramInt2, paramInt3, null, AndroidUtilities.dp(48.0F));
  }
  
  public ActionBarMenuItem addItem(int paramInt1, int paramInt2, int paramInt3, Drawable paramDrawable, int paramInt4)
  {
    Object localObject = getContext();
    int i;
    if (this.isActionMode)
    {
      i = this.parentActionBar.itemsActionModeColor;
      localObject = new ActionBarMenuItem((Context)localObject, this, paramInt3, i);
      ((ActionBarMenuItem)localObject).setTag(Integer.valueOf(paramInt1));
      if (paramDrawable == null) {
        break label105;
      }
      ((ActionBarMenuItem)localObject).iconView.setImageDrawable(paramDrawable);
    }
    for (;;)
    {
      addView((View)localObject, new LinearLayout.LayoutParams(paramInt4, -1));
      ((ActionBarMenuItem)localObject).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ActionBarMenuItem localActionBarMenuItem = (ActionBarMenuItem)paramAnonymousView;
          if (localActionBarMenuItem.hasSubMenu()) {
            if (ActionBarMenu.this.parentActionBar.actionBarMenuOnItemClick.canOpenMenu()) {
              localActionBarMenuItem.toggleSubMenu();
            }
          }
          for (;;)
          {
            return;
            if (localActionBarMenuItem.isSearchField()) {
              ActionBarMenu.this.parentActionBar.onSearchFieldVisibilityChanged(localActionBarMenuItem.toggleSearch(true));
            } else {
              ActionBarMenu.this.onItemClick(((Integer)paramAnonymousView.getTag()).intValue());
            }
          }
        }
      });
      return (ActionBarMenuItem)localObject;
      i = this.parentActionBar.itemsColor;
      break;
      label105:
      if (paramInt2 != 0) {
        ((ActionBarMenuItem)localObject).iconView.setImageResource(paramInt2);
      }
    }
  }
  
  public ActionBarMenuItem addItem(int paramInt, Drawable paramDrawable)
  {
    if (this.isActionMode) {}
    for (int i = this.parentActionBar.itemsActionModeBackgroundColor;; i = this.parentActionBar.itemsBackgroundColor) {
      return addItem(paramInt, 0, i, paramDrawable, AndroidUtilities.dp(48.0F));
    }
  }
  
  public ActionBarMenuItem addItemWithWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    if (this.isActionMode) {}
    for (int i = this.parentActionBar.itemsActionModeBackgroundColor;; i = this.parentActionBar.itemsBackgroundColor) {
      return addItem(paramInt1, paramInt2, i, null, paramInt3);
    }
  }
  
  public void clearItems()
  {
    removeAllViews();
  }
  
  public void closeSearchField(boolean paramBoolean)
  {
    int i = getChildCount();
    for (int j = 0;; j++) {
      if (j < i)
      {
        Object localObject = getChildAt(j);
        if ((localObject instanceof ActionBarMenuItem))
        {
          localObject = (ActionBarMenuItem)localObject;
          if (((ActionBarMenuItem)localObject).isSearchField())
          {
            this.parentActionBar.onSearchFieldVisibilityChanged(false);
            ((ActionBarMenuItem)localObject).toggleSearch(paramBoolean);
          }
        }
      }
      else
      {
        return;
      }
    }
  }
  
  public ActionBarMenuItem getItem(int paramInt)
  {
    Object localObject = findViewWithTag(Integer.valueOf(paramInt));
    if ((localObject instanceof ActionBarMenuItem)) {}
    for (localObject = (ActionBarMenuItem)localObject;; localObject = null) {
      return (ActionBarMenuItem)localObject;
    }
  }
  
  public void hideAllPopupMenus()
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((localView instanceof ActionBarMenuItem)) {
        ((ActionBarMenuItem)localView).closeSubMenu();
      }
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
    int i = getChildCount();
    int j = 0;
    Object localObject;
    if (j < i)
    {
      localObject = getChildAt(j);
      if ((localObject instanceof ActionBarMenuItem))
      {
        localObject = (ActionBarMenuItem)localObject;
        if (((ActionBarMenuItem)localObject).getVisibility() == 0) {
          break label43;
        }
      }
    }
    for (;;)
    {
      j++;
      break;
      label43:
      if (((ActionBarMenuItem)localObject).hasSubMenu()) {
        ((ActionBarMenuItem)localObject).toggleSubMenu();
      }
      for (;;)
      {
        return;
        if (!((ActionBarMenuItem)localObject).overrideMenuClick) {
          break;
        }
        onItemClick(((Integer)((ActionBarMenuItem)localObject).getTag()).intValue());
      }
    }
  }
  
  public void openSearchField(boolean paramBoolean, String paramString)
  {
    int i = getChildCount();
    for (int j = 0;; j++) {
      if (j < i)
      {
        Object localObject = getChildAt(j);
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
    }
  }
  
  protected void redrawPopup(int paramInt)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((localView instanceof ActionBarMenuItem)) {
        ((ActionBarMenuItem)localView).redrawPopup(paramInt);
      }
    }
  }
  
  protected void setPopupItemsColor(int paramInt)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView = getChildAt(j);
      if ((localView instanceof ActionBarMenuItem)) {
        ((ActionBarMenuItem)localView).setPopupItemsColor(paramInt);
      }
    }
  }
  
  public void setSearchTextColor(int paramInt, boolean paramBoolean)
  {
    int i = getChildCount();
    for (int j = 0;; j++)
    {
      Object localObject;
      if (j < i)
      {
        localObject = getChildAt(j);
        if (!(localObject instanceof ActionBarMenuItem)) {
          continue;
        }
        localObject = (ActionBarMenuItem)localObject;
        if (!((ActionBarMenuItem)localObject).isSearchField()) {
          continue;
        }
        if (!paramBoolean) {
          break label59;
        }
        ((ActionBarMenuItem)localObject).getSearchField().setHintTextColor(paramInt);
      }
      for (;;)
      {
        return;
        label59:
        ((ActionBarMenuItem)localObject).getSearchField().setTextColor(paramInt);
      }
    }
  }
  
  protected void updateItemsBackgroundColor()
  {
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = getChildAt(j);
      if ((localView instanceof ActionBarMenuItem)) {
        if (!this.isActionMode) {
          break label56;
        }
      }
      label56:
      for (int k = this.parentActionBar.itemsActionModeBackgroundColor;; k = this.parentActionBar.itemsBackgroundColor)
      {
        localView.setBackgroundDrawable(Theme.createSelectorDrawable(k));
        j++;
        break;
      }
    }
  }
  
  protected void updateItemsColor()
  {
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      Object localObject = getChildAt(j);
      if ((localObject instanceof ActionBarMenuItem))
      {
        localObject = (ActionBarMenuItem)localObject;
        if (!this.isActionMode) {
          break label58;
        }
      }
      label58:
      for (int k = this.parentActionBar.itemsActionModeColor;; k = this.parentActionBar.itemsColor)
      {
        ((ActionBarMenuItem)localObject).setIconColor(k);
        j++;
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/ActionBarMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */