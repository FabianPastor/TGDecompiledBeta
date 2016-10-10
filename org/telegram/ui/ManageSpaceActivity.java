package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarLayout.ActionBarLayoutDelegate;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;

public class ManageSpaceActivity
  extends Activity
  implements ActionBarLayout.ActionBarLayoutDelegate
{
  private static ArrayList<BaseFragment> layerFragmentsStack = new ArrayList();
  private static ArrayList<BaseFragment> mainFragmentsStack = new ArrayList();
  private ActionBarLayout actionBarLayout;
  private int currentConnectionState;
  protected DrawerLayoutContainer drawerLayoutContainer;
  private boolean finished;
  private ActionBarLayout layersActionBarLayout;
  
  private boolean handleIntent(Intent paramIntent, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (AndroidUtilities.isTablet()) {
      if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
        this.layersActionBarLayout.addFragmentToStack(new CacheControlActivity());
      }
    }
    for (;;)
    {
      this.actionBarLayout.showLastFragment();
      if (AndroidUtilities.isTablet()) {
        this.layersActionBarLayout.showLastFragment();
      }
      paramIntent.setAction(null);
      return false;
      if (this.actionBarLayout.fragmentsStack.isEmpty()) {
        this.actionBarLayout.addFragmentToStack(new CacheControlActivity());
      }
    }
  }
  
  private void onFinish()
  {
    if (this.finished) {
      return;
    }
    this.finished = true;
  }
  
  private void updateCurrentConnectionState()
  {
    String str = null;
    if (this.currentConnectionState == 2) {
      str = LocaleController.getString("WaitingForNetwork", 2131166387);
    }
    for (;;)
    {
      this.actionBarLayout.setTitleOverlayText(str);
      return;
      if (this.currentConnectionState == 1) {
        str = LocaleController.getString("Connecting", 2131165519);
      } else if (this.currentConnectionState == 4) {
        str = LocaleController.getString("Updating", 2131166360);
      }
    }
  }
  
  public void fixLayout()
  {
    if (!AndroidUtilities.isTablet()) {}
    while (this.actionBarLayout == null) {
      return;
    }
    this.actionBarLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
    {
      public void onGlobalLayout()
      {
        ManageSpaceActivity.this.needLayout();
        if (ManageSpaceActivity.this.actionBarLayout != null)
        {
          if (Build.VERSION.SDK_INT < 16) {
            ManageSpaceActivity.this.actionBarLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
          }
        }
        else {
          return;
        }
        ManageSpaceActivity.this.actionBarLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
      }
    });
  }
  
  public boolean needAddFragmentToStack(BaseFragment paramBaseFragment, ActionBarLayout paramActionBarLayout)
  {
    return true;
  }
  
  public boolean needCloseLastFragment(ActionBarLayout paramActionBarLayout)
  {
    if (AndroidUtilities.isTablet())
    {
      if ((paramActionBarLayout == this.actionBarLayout) && (paramActionBarLayout.fragmentsStack.size() <= 1))
      {
        onFinish();
        finish();
        return false;
      }
      if ((paramActionBarLayout == this.layersActionBarLayout) && (this.actionBarLayout.fragmentsStack.isEmpty()) && (this.layersActionBarLayout.fragmentsStack.size() == 1))
      {
        onFinish();
        finish();
        return false;
      }
    }
    else if (paramActionBarLayout.fragmentsStack.size() <= 1)
    {
      onFinish();
      finish();
      return false;
    }
    return true;
  }
  
  public void needLayout()
  {
    if (AndroidUtilities.isTablet())
    {
      localLayoutParams = (RelativeLayout.LayoutParams)this.layersActionBarLayout.getLayoutParams();
      localLayoutParams.leftMargin = ((AndroidUtilities.displaySize.x - localLayoutParams.width) / 2);
      if (Build.VERSION.SDK_INT < 21) {
        break label211;
      }
    }
    label211:
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      localLayoutParams.topMargin = ((AndroidUtilities.displaySize.y - localLayoutParams.height - i) / 2 + i);
      this.layersActionBarLayout.setLayoutParams(localLayoutParams);
      if ((AndroidUtilities.isSmallTablet()) && (getResources().getConfiguration().orientation != 2)) {
        break;
      }
      int j = AndroidUtilities.displaySize.x / 100 * 35;
      i = j;
      if (j < AndroidUtilities.dp(320.0F)) {
        i = AndroidUtilities.dp(320.0F);
      }
      localLayoutParams = (RelativeLayout.LayoutParams)this.actionBarLayout.getLayoutParams();
      localLayoutParams.width = i;
      localLayoutParams.height = -1;
      this.actionBarLayout.setLayoutParams(localLayoutParams);
      if ((AndroidUtilities.isSmallTablet()) && (this.actionBarLayout.fragmentsStack.size() == 2))
      {
        ((BaseFragment)this.actionBarLayout.fragmentsStack.get(1)).onPause();
        this.actionBarLayout.fragmentsStack.remove(1);
        this.actionBarLayout.showLastFragment();
      }
      return;
    }
    RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)this.actionBarLayout.getLayoutParams();
    localLayoutParams.width = -1;
    localLayoutParams.height = -1;
    this.actionBarLayout.setLayoutParams(localLayoutParams);
  }
  
  public boolean needPresentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2, ActionBarLayout paramActionBarLayout)
  {
    return true;
  }
  
  public void onBackPressed()
  {
    if (PhotoViewer.getInstance().isVisible())
    {
      PhotoViewer.getInstance().closePhoto(true, false);
      return;
    }
    if (this.drawerLayoutContainer.isDrawerOpened())
    {
      this.drawerLayoutContainer.closeDrawer(false);
      return;
    }
    if (AndroidUtilities.isTablet())
    {
      if (this.layersActionBarLayout.getVisibility() == 0)
      {
        this.layersActionBarLayout.onBackPressed();
        return;
      }
      this.actionBarLayout.onBackPressed();
      return;
    }
    this.actionBarLayout.onBackPressed();
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    AndroidUtilities.checkDisplaySize(this, paramConfiguration);
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    boolean bool = true;
    ApplicationLoader.postInitApplication();
    requestWindowFeature(1);
    setTheme(2131296262);
    getWindow().setBackgroundDrawableResource(2130838023);
    super.onCreate(paramBundle);
    Theme.loadRecources(this);
    int i = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (i > 0) {
      AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(i);
    }
    this.actionBarLayout = new ActionBarLayout(this);
    this.drawerLayoutContainer = new DrawerLayoutContainer(this);
    this.drawerLayoutContainer.setAllowOpenDrawer(false, false);
    setContentView(this.drawerLayoutContainer, new ViewGroup.LayoutParams(-1, -1));
    Object localObject1;
    if (AndroidUtilities.isTablet())
    {
      getWindow().setSoftInputMode(16);
      localObject1 = new RelativeLayout(this);
      this.drawerLayoutContainer.addView((View)localObject1);
      Object localObject2 = (FrameLayout.LayoutParams)((RelativeLayout)localObject1).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject2).width = -1;
      ((FrameLayout.LayoutParams)localObject2).height = -1;
      ((RelativeLayout)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      localObject2 = new ImageView(this);
      ((ImageView)localObject2).setScaleType(ImageView.ScaleType.CENTER_CROP);
      ((ImageView)localObject2).setImageResource(2130837588);
      ((RelativeLayout)localObject1).addView((View)localObject2);
      RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams)((ImageView)localObject2).getLayoutParams();
      localLayoutParams.width = -1;
      localLayoutParams.height = -1;
      ((ImageView)localObject2).setLayoutParams(localLayoutParams);
      ((RelativeLayout)localObject1).addView(this.actionBarLayout);
      localObject2 = (RelativeLayout.LayoutParams)this.actionBarLayout.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject2).width = -1;
      ((RelativeLayout.LayoutParams)localObject2).height = -1;
      this.actionBarLayout.setLayoutParams((ViewGroup.LayoutParams)localObject2);
      localObject2 = new FrameLayout(this);
      ((FrameLayout)localObject2).setBackgroundColor(2130706432);
      ((RelativeLayout)localObject1).addView((View)localObject2);
      localLayoutParams = (RelativeLayout.LayoutParams)((FrameLayout)localObject2).getLayoutParams();
      localLayoutParams.width = -1;
      localLayoutParams.height = -1;
      ((FrameLayout)localObject2).setLayoutParams(localLayoutParams);
      ((FrameLayout)localObject2).setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          if ((!ManageSpaceActivity.this.actionBarLayout.fragmentsStack.isEmpty()) && (paramAnonymousMotionEvent.getAction() == 1))
          {
            float f1 = paramAnonymousMotionEvent.getX();
            float f2 = paramAnonymousMotionEvent.getY();
            paramAnonymousView = new int[2];
            ManageSpaceActivity.this.layersActionBarLayout.getLocationOnScreen(paramAnonymousView);
            int i = paramAnonymousView[0];
            int j = paramAnonymousView[1];
            if ((ManageSpaceActivity.this.layersActionBarLayout.checkTransitionAnimation()) || ((f1 > i) && (f1 < ManageSpaceActivity.this.layersActionBarLayout.getWidth() + i) && (f2 > j) && (f2 < ManageSpaceActivity.this.layersActionBarLayout.getHeight() + j))) {
              return false;
            }
            if (!ManageSpaceActivity.this.layersActionBarLayout.fragmentsStack.isEmpty())
            {
              for (i = 0; ManageSpaceActivity.this.layersActionBarLayout.fragmentsStack.size() - 1 > 0; i = i - 1 + 1) {
                ManageSpaceActivity.this.layersActionBarLayout.removeFragmentFromStack((BaseFragment)ManageSpaceActivity.this.layersActionBarLayout.fragmentsStack.get(0));
              }
              ManageSpaceActivity.this.layersActionBarLayout.closeLastFragment(true);
            }
            return true;
          }
          return false;
        }
      });
      ((FrameLayout)localObject2).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView) {}
      });
      this.layersActionBarLayout = new ActionBarLayout(this);
      this.layersActionBarLayout.setRemoveActionBarExtraHeight(true);
      this.layersActionBarLayout.setBackgroundView((View)localObject2);
      this.layersActionBarLayout.setUseAlphaAnimations(true);
      this.layersActionBarLayout.setBackgroundResource(2130837577);
      ((RelativeLayout)localObject1).addView(this.layersActionBarLayout);
      localObject1 = (RelativeLayout.LayoutParams)this.layersActionBarLayout.getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject1).width = AndroidUtilities.dp(530.0F);
      ((RelativeLayout.LayoutParams)localObject1).height = AndroidUtilities.dp(528.0F);
      this.layersActionBarLayout.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.layersActionBarLayout.init(layerFragmentsStack);
      this.layersActionBarLayout.setDelegate(this);
      this.layersActionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
      this.drawerLayoutContainer.setParentActionBarLayout(this.actionBarLayout);
      this.actionBarLayout.setDrawerLayoutContainer(this.drawerLayoutContainer);
      this.actionBarLayout.init(mainFragmentsStack);
      this.actionBarLayout.setDelegate(this);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeOtherAppActivities, new Object[] { this });
      this.currentConnectionState = ConnectionsManager.getInstance().getConnectionState();
      localObject1 = getIntent();
      if (paramBundle == null) {
        break label609;
      }
    }
    for (;;)
    {
      handleIntent((Intent)localObject1, false, bool, false);
      needLayout();
      return;
      this.drawerLayoutContainer.addView(this.actionBarLayout, new ViewGroup.LayoutParams(-1, -1));
      break;
      label609:
      bool = false;
    }
  }
  
  protected void onDestroy()
  {
    super.onDestroy();
    onFinish();
  }
  
  public void onLowMemory()
  {
    super.onLowMemory();
    this.actionBarLayout.onLowMemory();
    if (AndroidUtilities.isTablet()) {
      this.layersActionBarLayout.onLowMemory();
    }
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    handleIntent(paramIntent, true, false, false);
  }
  
  protected void onPause()
  {
    super.onPause();
    this.actionBarLayout.onPause();
    if (AndroidUtilities.isTablet()) {
      this.layersActionBarLayout.onPause();
    }
  }
  
  public boolean onPreIme()
  {
    return false;
  }
  
  public void onRebuildAllFragments(ActionBarLayout paramActionBarLayout)
  {
    if ((AndroidUtilities.isTablet()) && (paramActionBarLayout == this.layersActionBarLayout))
    {
      this.actionBarLayout.rebuildAllFragmentViews(true);
      this.actionBarLayout.showLastFragment();
    }
  }
  
  protected void onResume()
  {
    super.onResume();
    this.actionBarLayout.onResume();
    if (AndroidUtilities.isTablet()) {
      this.layersActionBarLayout.onResume();
    }
  }
  
  public void presentFragment(BaseFragment paramBaseFragment)
  {
    this.actionBarLayout.presentFragment(paramBaseFragment);
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2)
  {
    return this.actionBarLayout.presentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, true);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ManageSpaceActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */