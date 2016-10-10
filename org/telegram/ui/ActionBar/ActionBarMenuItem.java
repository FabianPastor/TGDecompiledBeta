package org.telegram.ui.ActionBar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.lang.reflect.Field;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarMenuItem
  extends FrameLayout
{
  private boolean allowCloseAnimation = true;
  private ImageView clearButton;
  private ActionBarMenuItemDelegate delegate;
  protected ImageView iconView;
  private boolean isSearchField = false;
  private ActionBarMenuItemSearchListener listener;
  private int[] location;
  private int menuHeight = AndroidUtilities.dp(16.0F);
  protected boolean overrideMenuClick;
  private ActionBarMenu parentMenu;
  private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
  private ActionBarPopupWindow popupWindow;
  private boolean processedPopupClick;
  private Rect rect;
  private FrameLayout searchContainer;
  private EditText searchField;
  private View selectedMenuView;
  private boolean showFromBottom;
  private Runnable showMenuRunnable;
  private int subMenuOpenSide = 0;
  
  public ActionBarMenuItem(Context paramContext, ActionBarMenu paramActionBarMenu, int paramInt)
  {
    super(paramContext);
    if (paramInt != 0) {
      setBackgroundDrawable(Theme.createBarSelectorDrawable(paramInt));
    }
    this.parentMenu = paramActionBarMenu;
    this.iconView = new ImageView(paramContext);
    this.iconView.setScaleType(ImageView.ScaleType.CENTER);
    addView(this.iconView, LayoutHelper.createFrame(-1, -1.0F));
  }
  
  private void updateOrShowPopup(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i;
    if (this.showFromBottom)
    {
      getLocationOnScreen(this.location);
      int k = this.location[1] - AndroidUtilities.statusBarHeight + getMeasuredHeight() - this.menuHeight;
      int j = -this.menuHeight;
      i = j;
      if (k < 0) {
        i = j - k;
      }
      if (paramBoolean1) {
        this.popupLayout.scrollToTop();
      }
      if (this.subMenuOpenSide != 0) {
        break label376;
      }
      if (!this.showFromBottom) {
        break label184;
      }
      if (paramBoolean1) {
        this.popupWindow.showAsDropDown(this, -this.popupLayout.getMeasuredWidth() + getMeasuredWidth(), i);
      }
      if (paramBoolean2) {
        this.popupWindow.update(this, -this.popupLayout.getMeasuredWidth() + getMeasuredWidth(), i, -1, -1);
      }
    }
    label184:
    label281:
    label376:
    do
    {
      Object localObject;
      do
      {
        do
        {
          do
          {
            return;
            if ((this.parentMenu != null) && (this.subMenuOpenSide == 0))
            {
              i = -this.parentMenu.parentActionBar.getMeasuredHeight() + this.parentMenu.getTop();
              break;
            }
            i = -getMeasuredHeight();
            break;
            if (this.parentMenu == null) {
              break label281;
            }
            localObject = this.parentMenu.parentActionBar;
            if (paramBoolean1) {
              this.popupWindow.showAsDropDown((View)localObject, getLeft() + this.parentMenu.getLeft() + getMeasuredWidth() - this.popupLayout.getMeasuredWidth(), i);
            }
          } while (!paramBoolean2);
          this.popupWindow.update((View)localObject, getLeft() + this.parentMenu.getLeft() + getMeasuredWidth() - this.popupLayout.getMeasuredWidth(), i, -1, -1);
          return;
        } while (getParent() == null);
        localObject = (View)getParent();
        if (paramBoolean1) {
          this.popupWindow.showAsDropDown((View)localObject, ((View)localObject).getMeasuredWidth() - this.popupLayout.getMeasuredWidth() - getLeft() - ((View)localObject).getLeft(), i);
        }
      } while (!paramBoolean2);
      this.popupWindow.update((View)localObject, ((View)localObject).getMeasuredWidth() - this.popupLayout.getMeasuredWidth() - getLeft() - ((View)localObject).getLeft(), i, -1, -1);
      return;
      if (paramBoolean1) {
        this.popupWindow.showAsDropDown(this, -AndroidUtilities.dp(8.0F), i);
      }
    } while (!paramBoolean2);
    this.popupWindow.update(this, -AndroidUtilities.dp(8.0F), i, -1, -1);
  }
  
  public TextView addSubItem(int paramInt1, String paramString, int paramInt2)
  {
    if (this.popupLayout == null)
    {
      this.rect = new Rect();
      this.location = new int[2];
      this.popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
      this.popupLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          if ((paramAnonymousMotionEvent.getActionMasked() == 0) && (ActionBarMenuItem.this.popupWindow != null) && (ActionBarMenuItem.this.popupWindow.isShowing()))
          {
            paramAnonymousView.getHitRect(ActionBarMenuItem.this.rect);
            if (!ActionBarMenuItem.this.rect.contains((int)paramAnonymousMotionEvent.getX(), (int)paramAnonymousMotionEvent.getY())) {
              ActionBarMenuItem.this.popupWindow.dismiss();
            }
          }
          return false;
        }
      });
      this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener()
      {
        public void onDispatchKeyEvent(KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousKeyEvent.getKeyCode() == 4) && (paramAnonymousKeyEvent.getRepeatCount() == 0) && (ActionBarMenuItem.this.popupWindow != null) && (ActionBarMenuItem.this.popupWindow.isShowing())) {
            ActionBarMenuItem.this.popupWindow.dismiss();
          }
        }
      });
    }
    TextView localTextView = new TextView(getContext());
    localTextView.setTextColor(-14606047);
    localTextView.setBackgroundResource(2130837796);
    if (!LocaleController.isRTL)
    {
      localTextView.setGravity(16);
      localTextView.setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), 0);
      localTextView.setTextSize(1, 18.0F);
      localTextView.setMinWidth(AndroidUtilities.dp(196.0F));
      localTextView.setTag(Integer.valueOf(paramInt1));
      localTextView.setText(paramString);
      if (paramInt2 != 0)
      {
        localTextView.setCompoundDrawablePadding(AndroidUtilities.dp(12.0F));
        if (LocaleController.isRTL) {
          break label299;
        }
        localTextView.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(paramInt2), null, null, null);
      }
    }
    for (;;)
    {
      this.popupLayout.setShowedFromBotton(this.showFromBottom);
      this.popupLayout.addView(localTextView);
      paramString = (LinearLayout.LayoutParams)localTextView.getLayoutParams();
      if (LocaleController.isRTL) {
        paramString.gravity = 5;
      }
      paramString.width = -1;
      paramString.height = AndroidUtilities.dp(48.0F);
      localTextView.setLayoutParams(paramString);
      localTextView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((ActionBarMenuItem.this.popupWindow != null) && (ActionBarMenuItem.this.popupWindow.isShowing())) {
            if (!ActionBarMenuItem.this.processedPopupClick) {}
          }
          do
          {
            return;
            ActionBarMenuItem.access$202(ActionBarMenuItem.this, true);
            ActionBarMenuItem.this.popupWindow.dismiss(ActionBarMenuItem.this.allowCloseAnimation);
            if (ActionBarMenuItem.this.parentMenu != null)
            {
              ActionBarMenuItem.this.parentMenu.onItemClick(((Integer)paramAnonymousView.getTag()).intValue());
              return;
            }
          } while (ActionBarMenuItem.this.delegate == null);
          ActionBarMenuItem.this.delegate.onItemClick(((Integer)paramAnonymousView.getTag()).intValue());
        }
      });
      this.menuHeight += paramString.height;
      return localTextView;
      localTextView.setGravity(21);
      break;
      label299:
      localTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(paramInt2), null);
    }
  }
  
  public void closeSubMenu()
  {
    if ((this.popupWindow != null) && (this.popupWindow.isShowing())) {
      this.popupWindow.dismiss();
    }
  }
  
  public ImageView getImageView()
  {
    return this.iconView;
  }
  
  public EditText getSearchField()
  {
    return this.searchField;
  }
  
  public boolean hasSubMenu()
  {
    return this.popupLayout != null;
  }
  
  public void hideSubItem(int paramInt)
  {
    View localView = this.popupLayout.findViewWithTag(Integer.valueOf(paramInt));
    if (localView != null) {
      localView.setVisibility(8);
    }
  }
  
  public boolean isSearchField()
  {
    return this.isSearchField;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if ((this.popupWindow != null) && (this.popupWindow.isShowing())) {
      updateOrShowPopup(false, true);
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getActionMasked() == 0) {
      if ((hasSubMenu()) && ((this.popupWindow == null) || ((this.popupWindow != null) && (!this.popupWindow.isShowing()))))
      {
        this.showMenuRunnable = new Runnable()
        {
          public void run()
          {
            if (ActionBarMenuItem.this.getParent() != null) {
              ActionBarMenuItem.this.getParent().requestDisallowInterceptTouchEvent(true);
            }
            ActionBarMenuItem.this.toggleSubMenu();
          }
        };
        AndroidUtilities.runOnUIThread(this.showMenuRunnable, 200L);
      }
    }
    for (;;)
    {
      return super.onTouchEvent(paramMotionEvent);
      if (paramMotionEvent.getActionMasked() == 2)
      {
        if ((hasSubMenu()) && ((this.popupWindow == null) || ((this.popupWindow != null) && (!this.popupWindow.isShowing()))))
        {
          if (paramMotionEvent.getY() > getHeight())
          {
            if (getParent() != null) {
              getParent().requestDisallowInterceptTouchEvent(true);
            }
            toggleSubMenu();
            return true;
          }
        }
        else if ((this.popupWindow != null) && (this.popupWindow.isShowing()))
        {
          getLocationOnScreen(this.location);
          float f3 = paramMotionEvent.getX();
          float f4 = this.location[0];
          float f1 = paramMotionEvent.getY();
          float f2 = this.location[1];
          this.popupLayout.getLocationOnScreen(this.location);
          f3 = f3 + f4 - this.location[0];
          f1 = f1 + f2 - this.location[1];
          this.selectedMenuView = null;
          int i = 0;
          label240:
          View localView;
          if (i < this.popupLayout.getItemsCount())
          {
            localView = this.popupLayout.getItemAt(i);
            localView.getHitRect(this.rect);
            if (((Integer)localView.getTag()).intValue() < 100)
            {
              if (this.rect.contains((int)f3, (int)f1)) {
                break label343;
              }
              localView.setPressed(false);
              localView.setSelected(false);
              if (Build.VERSION.SDK_INT == 21) {
                localView.getBackground().setVisible(false, false);
              }
            }
          }
          for (;;)
          {
            i += 1;
            break label240;
            break;
            label343:
            localView.setPressed(true);
            localView.setSelected(true);
            if (Build.VERSION.SDK_INT >= 21)
            {
              if (Build.VERSION.SDK_INT == 21) {
                localView.getBackground().setVisible(true, false);
              }
              localView.drawableHotspotChanged(f3, f1 - localView.getTop());
            }
            this.selectedMenuView = localView;
          }
        }
      }
      else if ((this.popupWindow != null) && (this.popupWindow.isShowing()) && (paramMotionEvent.getActionMasked() == 1))
      {
        if (this.selectedMenuView != null)
        {
          this.selectedMenuView.setSelected(false);
          if (this.parentMenu != null) {
            this.parentMenu.onItemClick(((Integer)this.selectedMenuView.getTag()).intValue());
          }
          for (;;)
          {
            this.popupWindow.dismiss(this.allowCloseAnimation);
            break;
            if (this.delegate != null) {
              this.delegate.onItemClick(((Integer)this.selectedMenuView.getTag()).intValue());
            }
          }
        }
        this.popupWindow.dismiss();
      }
      else if (this.selectedMenuView != null)
      {
        this.selectedMenuView.setSelected(false);
        this.selectedMenuView = null;
      }
    }
  }
  
  public void openSearch(boolean paramBoolean)
  {
    if ((this.searchContainer == null) || (this.searchContainer.getVisibility() == 0) || (this.parentMenu == null)) {
      return;
    }
    this.parentMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(paramBoolean));
  }
  
  public ActionBarMenuItem setActionBarMenuItemSearchListener(ActionBarMenuItemSearchListener paramActionBarMenuItemSearchListener)
  {
    this.listener = paramActionBarMenuItemSearchListener;
    return this;
  }
  
  public ActionBarMenuItem setAllowCloseAnimation(boolean paramBoolean)
  {
    this.allowCloseAnimation = paramBoolean;
    return this;
  }
  
  public void setDelegate(ActionBarMenuItemDelegate paramActionBarMenuItemDelegate)
  {
    this.delegate = paramActionBarMenuItemDelegate;
  }
  
  public void setIcon(int paramInt)
  {
    this.iconView.setImageResource(paramInt);
  }
  
  public ActionBarMenuItem setIsSearchField(boolean paramBoolean)
  {
    if (this.parentMenu == null) {
      return this;
    }
    Object localObject;
    if ((paramBoolean) && (this.searchContainer == null))
    {
      this.searchContainer = new FrameLayout(getContext());
      this.parentMenu.addView(this.searchContainer, 0);
      localObject = (LinearLayout.LayoutParams)this.searchContainer.getLayoutParams();
      ((LinearLayout.LayoutParams)localObject).weight = 1.0F;
      ((LinearLayout.LayoutParams)localObject).width = 0;
      ((LinearLayout.LayoutParams)localObject).height = -1;
      ((LinearLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(6.0F);
      this.searchContainer.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.searchContainer.setVisibility(8);
      this.searchField = new EditText(getContext());
      this.searchField.setTextSize(1, 18.0F);
      this.searchField.setHintTextColor(-1996488705);
      this.searchField.setTextColor(-1);
      this.searchField.setSingleLine(true);
      this.searchField.setBackgroundResource(0);
      this.searchField.setPadding(0, 0, 0, 0);
      int i = this.searchField.getInputType();
      this.searchField.setInputType(i | 0x80000);
      this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramAnonymousActionMode, MenuItem paramAnonymousMenuItem)
        {
          return false;
        }
        
        public boolean onCreateActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          return false;
        }
        
        public void onDestroyActionMode(ActionMode paramAnonymousActionMode) {}
        
        public boolean onPrepareActionMode(ActionMode paramAnonymousActionMode, Menu paramAnonymousMenu)
        {
          return false;
        }
      });
      this.searchField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousKeyEvent != null) && (((paramAnonymousKeyEvent.getAction() == 1) && (paramAnonymousKeyEvent.getKeyCode() == 84)) || ((paramAnonymousKeyEvent.getAction() == 0) && (paramAnonymousKeyEvent.getKeyCode() == 66))))
          {
            AndroidUtilities.hideKeyboard(ActionBarMenuItem.this.searchField);
            if (ActionBarMenuItem.this.listener != null) {
              ActionBarMenuItem.this.listener.onSearchPressed(ActionBarMenuItem.this.searchField);
            }
          }
          return false;
        }
      });
      this.searchField.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable) {}
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (ActionBarMenuItem.this.listener != null) {
            ActionBarMenuItem.this.listener.onTextChanged(ActionBarMenuItem.this.searchField);
          }
          ImageView localImageView;
          if (ActionBarMenuItem.this.clearButton != null)
          {
            localImageView = ActionBarMenuItem.this.clearButton;
            if ((paramAnonymousCharSequence != null) && (paramAnonymousCharSequence.length() != 0)) {
              break label71;
            }
          }
          label71:
          for (float f = 0.6F;; f = 1.0F)
          {
            localImageView.setAlpha(f);
            return;
          }
        }
      });
    }
    try
    {
      localObject = TextView.class.getDeclaredField("mCursorDrawableRes");
      ((Field)localObject).setAccessible(true);
      ((Field)localObject).set(this.searchField, Integer.valueOf(2130837968));
      this.searchField.setImeOptions(33554435);
      this.searchField.setTextIsSelectable(false);
      this.searchContainer.addView(this.searchField);
      localObject = (FrameLayout.LayoutParams)this.searchField.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).gravity = 16;
      ((FrameLayout.LayoutParams)localObject).height = AndroidUtilities.dp(36.0F);
      ((FrameLayout.LayoutParams)localObject).rightMargin = AndroidUtilities.dp(48.0F);
      this.searchField.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.clearButton = new ImageView(getContext());
      this.clearButton.setImageResource(2130837717);
      this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
      this.clearButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ActionBarMenuItem.this.searchField.setText("");
          ActionBarMenuItem.this.searchField.requestFocus();
          AndroidUtilities.showKeyboard(ActionBarMenuItem.this.searchField);
        }
      });
      this.searchContainer.addView(this.clearButton);
      localObject = (FrameLayout.LayoutParams)this.clearButton.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = AndroidUtilities.dp(48.0F);
      ((FrameLayout.LayoutParams)localObject).gravity = 21;
      ((FrameLayout.LayoutParams)localObject).height = -1;
      this.clearButton.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.isSearchField = paramBoolean;
      return this;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public ActionBarMenuItem setOverrideMenuClick(boolean paramBoolean)
  {
    this.overrideMenuClick = paramBoolean;
    return this;
  }
  
  public void setShowFromBottom(boolean paramBoolean)
  {
    this.showFromBottom = paramBoolean;
    if (this.popupLayout != null) {
      this.popupLayout.setShowedFromBotton(this.showFromBottom);
    }
  }
  
  public void setSubMenuOpenSide(int paramInt)
  {
    this.subMenuOpenSide = paramInt;
  }
  
  public void showSubItem(int paramInt)
  {
    View localView = this.popupLayout.findViewWithTag(Integer.valueOf(paramInt));
    if (localView != null) {
      localView.setVisibility(0);
    }
  }
  
  public boolean toggleSearch(boolean paramBoolean)
  {
    if (this.searchContainer == null) {}
    do
    {
      do
      {
        return false;
        if (this.searchContainer.getVisibility() != 0) {
          break;
        }
      } while ((this.listener != null) && ((this.listener == null) || (!this.listener.canCollapseSearch())));
      this.searchContainer.setVisibility(8);
      this.searchField.clearFocus();
      setVisibility(0);
      AndroidUtilities.hideKeyboard(this.searchField);
    } while (this.listener == null);
    this.listener.onSearchCollapse();
    return false;
    this.searchContainer.setVisibility(0);
    setVisibility(8);
    this.searchField.setText("");
    this.searchField.requestFocus();
    if (paramBoolean) {
      AndroidUtilities.showKeyboard(this.searchField);
    }
    if (this.listener != null) {
      this.listener.onSearchExpand();
    }
    return true;
  }
  
  public void toggleSubMenu()
  {
    if (this.popupLayout == null) {
      return;
    }
    if (this.showMenuRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.showMenuRunnable);
      this.showMenuRunnable = null;
    }
    if ((this.popupWindow != null) && (this.popupWindow.isShowing()))
    {
      this.popupWindow.dismiss();
      return;
    }
    if (this.popupWindow == null)
    {
      this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
      if (Build.VERSION.SDK_INT >= 19)
      {
        this.popupWindow.setAnimationStyle(0);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setClippingEnabled(true);
        this.popupWindow.setInputMethodMode(2);
        this.popupWindow.setSoftInputMode(0);
        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0F), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0F), Integer.MIN_VALUE));
        this.popupWindow.getContentView().setFocusableInTouchMode(true);
        this.popupWindow.getContentView().setOnKeyListener(new View.OnKeyListener()
        {
          public boolean onKey(View paramAnonymousView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
          {
            if ((paramAnonymousInt == 82) && (paramAnonymousKeyEvent.getRepeatCount() == 0) && (paramAnonymousKeyEvent.getAction() == 1) && (ActionBarMenuItem.this.popupWindow != null) && (ActionBarMenuItem.this.popupWindow.isShowing()))
            {
              ActionBarMenuItem.this.popupWindow.dismiss();
              return true;
            }
            return false;
          }
        });
      }
    }
    else
    {
      this.processedPopupClick = false;
      this.popupWindow.setFocusable(true);
      if (this.popupLayout.getMeasuredWidth() != 0) {
        break label236;
      }
      updateOrShowPopup(true, true);
    }
    for (;;)
    {
      this.popupWindow.startAnimation();
      return;
      this.popupWindow.setAnimationStyle(2131296271);
      break;
      label236:
      updateOrShowPopup(true, false);
    }
  }
  
  public static abstract interface ActionBarMenuItemDelegate
  {
    public abstract void onItemClick(int paramInt);
  }
  
  public static class ActionBarMenuItemSearchListener
  {
    public boolean canCollapseSearch()
    {
      return true;
    }
    
    public void onSearchCollapse() {}
    
    public void onSearchExpand() {}
    
    public void onSearchPressed(EditText paramEditText) {}
    
    public void onTextChanged(EditText paramEditText) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/ActionBarMenuItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */