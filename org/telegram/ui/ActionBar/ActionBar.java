package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.Collection;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBar
  extends FrameLayout
{
  public ActionBarMenuOnItemClick actionBarMenuOnItemClick;
  private ActionBarMenu actionMode;
  private AnimatorSet actionModeAnimation;
  private View actionModeTop;
  private boolean actionModeVisible;
  private boolean addToContainer;
  private boolean allowOverlayTitle;
  private ImageView backButtonImageView;
  private boolean castShadows;
  private int extraHeight;
  private boolean interceptTouches;
  private boolean isBackOverlayVisible;
  protected boolean isSearchFieldVisible;
  protected int itemsBackgroundColor;
  private CharSequence lastTitle;
  private ActionBarMenu menu;
  private boolean occupyStatusBar;
  protected BaseFragment parentFragment;
  private SimpleTextView subtitleTextView;
  private SimpleTextView titleTextView;
  
  public ActionBar(Context paramContext)
  {
    super(paramContext);
    if (Build.VERSION.SDK_INT >= 21) {}
    for (boolean bool = true;; bool = false)
    {
      this.occupyStatusBar = bool;
      this.addToContainer = true;
      this.interceptTouches = true;
      this.castShadows = true;
      return;
    }
  }
  
  private void createBackButtonImage()
  {
    if (this.backButtonImageView != null) {
      return;
    }
    this.backButtonImageView = new ImageView(getContext());
    this.backButtonImageView.setScaleType(ImageView.ScaleType.CENTER);
    this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.itemsBackgroundColor));
    this.backButtonImageView.setPadding(AndroidUtilities.dp(1.0F), 0, 0, 0);
    addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
    this.backButtonImageView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (ActionBar.this.isSearchFieldVisible) {
          ActionBar.this.closeSearchField();
        }
        while (ActionBar.this.actionBarMenuOnItemClick == null) {
          return;
        }
        ActionBar.this.actionBarMenuOnItemClick.onItemClick(-1);
      }
    });
  }
  
  private void createSubtitleTextView()
  {
    if (this.subtitleTextView != null) {
      return;
    }
    this.subtitleTextView = new SimpleTextView(getContext());
    this.subtitleTextView.setGravity(3);
    this.subtitleTextView.setTextColor(-2758409);
    addView(this.subtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
  }
  
  private void createTitleTextView()
  {
    if (this.titleTextView != null) {
      return;
    }
    this.titleTextView = new SimpleTextView(getContext());
    this.titleTextView.setGravity(3);
    this.titleTextView.setTextColor(-1);
    this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    addView(this.titleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
  }
  
  public static int getCurrentActionBarHeight()
  {
    if (AndroidUtilities.isTablet()) {
      return AndroidUtilities.dp(64.0F);
    }
    if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
      return AndroidUtilities.dp(48.0F);
    }
    return AndroidUtilities.dp(56.0F);
  }
  
  public void closeSearchField()
  {
    if ((!this.isSearchFieldVisible) || (this.menu == null)) {
      return;
    }
    this.menu.closeSearchField();
  }
  
  public ActionBarMenu createActionMode()
  {
    if (this.actionMode != null) {
      return this.actionMode;
    }
    this.actionMode = new ActionBarMenu(getContext(), this);
    this.actionMode.setBackgroundColor(-1);
    addView(this.actionMode, indexOfChild(this.backButtonImageView));
    Object localObject = this.actionMode;
    if (this.occupyStatusBar) {}
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      ((ActionBarMenu)localObject).setPadding(0, i, 0, 0);
      localObject = (FrameLayout.LayoutParams)this.actionMode.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).height = -1;
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).gravity = 5;
      this.actionMode.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.actionMode.setVisibility(4);
      if ((this.occupyStatusBar) && (this.actionModeTop == null))
      {
        this.actionModeTop = new View(getContext());
        this.actionModeTop.setBackgroundColor(-1728053248);
        addView(this.actionModeTop);
        localObject = (FrameLayout.LayoutParams)this.actionModeTop.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).height = AndroidUtilities.statusBarHeight;
        ((FrameLayout.LayoutParams)localObject).width = -1;
        ((FrameLayout.LayoutParams)localObject).gravity = 51;
        this.actionModeTop.setLayoutParams((ViewGroup.LayoutParams)localObject);
        this.actionModeTop.setVisibility(4);
      }
      return this.actionMode;
    }
  }
  
  public ActionBarMenu createMenu()
  {
    if (this.menu != null) {
      return this.menu;
    }
    this.menu = new ActionBarMenu(getContext(), this);
    addView(this.menu, 0, LayoutHelper.createFrame(-2, -1, 5));
    return this.menu;
  }
  
  public boolean getAddToContainer()
  {
    return this.addToContainer;
  }
  
  public boolean getCastShadows()
  {
    return this.castShadows;
  }
  
  public boolean getOccupyStatusBar()
  {
    return this.occupyStatusBar;
  }
  
  public SimpleTextView getSubtitleTextView()
  {
    return this.subtitleTextView;
  }
  
  public String getTitle()
  {
    if (this.titleTextView == null) {
      return null;
    }
    return this.titleTextView.getText().toString();
  }
  
  public SimpleTextView getTitleTextView()
  {
    return this.titleTextView;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void hideActionMode()
  {
    if ((this.actionMode == null) || (!this.actionModeVisible)) {}
    do
    {
      return;
      this.actionModeVisible = false;
      localObject = new ArrayList();
      ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[] { 0.0F }));
      if ((this.occupyStatusBar) && (this.actionModeTop != null)) {
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[] { 0.0F }));
      }
      if (this.actionModeAnimation != null) {
        this.actionModeAnimation.cancel();
      }
      this.actionModeAnimation = new AnimatorSet();
      this.actionModeAnimation.playTogether((Collection)localObject);
      this.actionModeAnimation.setDuration(200L);
      this.actionModeAnimation.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ActionBar.this.actionModeAnimation != null) && (ActionBar.this.actionModeAnimation.equals(paramAnonymousAnimator))) {
            ActionBar.access$302(ActionBar.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ActionBar.this.actionModeAnimation != null) && (ActionBar.this.actionModeAnimation.equals(paramAnonymousAnimator)))
          {
            ActionBar.access$302(ActionBar.this, null);
            ActionBar.this.actionMode.setVisibility(4);
            if ((ActionBar.this.occupyStatusBar) && (ActionBar.this.actionModeTop != null)) {
              ActionBar.this.actionModeTop.setVisibility(4);
            }
          }
        }
      });
      this.actionModeAnimation.start();
      if (this.titleTextView != null) {
        this.titleTextView.setVisibility(0);
      }
      if (this.subtitleTextView != null) {
        this.subtitleTextView.setVisibility(0);
      }
      if (this.menu != null) {
        this.menu.setVisibility(0);
      }
    } while (this.backButtonImageView == null);
    Object localObject = this.backButtonImageView.getDrawable();
    if ((localObject instanceof BackDrawable)) {
      ((BackDrawable)localObject).setRotation(0.0F, true);
    }
    this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.itemsBackgroundColor));
  }
  
  public boolean isActionModeShowed()
  {
    return (this.actionMode != null) && (this.actionModeVisible);
  }
  
  public boolean isSearchFieldVisible()
  {
    return this.isSearchFieldVisible;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    float f;
    label69:
    label113:
    int k;
    label120:
    label229:
    label239:
    int m;
    if (this.occupyStatusBar)
    {
      i = AndroidUtilities.statusBarHeight;
      if ((this.backButtonImageView == null) || (this.backButtonImageView.getVisibility() == 8)) {
        break label481;
      }
      this.backButtonImageView.layout(0, i, this.backButtonImageView.getMeasuredWidth(), this.backButtonImageView.getMeasuredHeight() + i);
      if (!AndroidUtilities.isTablet()) {
        break label473;
      }
      f = 80.0F;
      j = AndroidUtilities.dp(f);
      if ((this.menu != null) && (this.menu.getVisibility() != 8))
      {
        if (!this.isSearchFieldVisible) {
          break label518;
        }
        if (!AndroidUtilities.isTablet()) {
          break label510;
        }
        f = 74.0F;
        k = AndroidUtilities.dp(f);
        this.menu.layout(k, i, this.menu.getMeasuredWidth() + k, this.menu.getMeasuredHeight() + i);
      }
      if ((this.titleTextView != null) && (this.titleTextView.getVisibility() != 8))
      {
        if ((this.subtitleTextView == null) || (this.subtitleTextView.getVisibility() == 8)) {
          break label543;
        }
        k = (getCurrentActionBarHeight() / 2 - this.titleTextView.getTextHeight()) / 2;
        if ((AndroidUtilities.isTablet()) || (getResources().getConfiguration().orientation != 2)) {
          break label535;
        }
        f = 2.0F;
        k += AndroidUtilities.dp(f);
        this.titleTextView.layout(j, i + k, this.titleTextView.getMeasuredWidth() + j, i + k + this.titleTextView.getTextHeight());
      }
      if ((this.subtitleTextView != null) && (this.subtitleTextView.getVisibility() != 8))
      {
        k = getCurrentActionBarHeight() / 2;
        m = (getCurrentActionBarHeight() / 2 - this.subtitleTextView.getTextHeight()) / 2;
        if ((AndroidUtilities.isTablet()) || (getResources().getConfiguration().orientation != 2)) {
          break label561;
        }
      }
    }
    View localView;
    label473:
    label481:
    label510:
    label518:
    label535:
    label543:
    label561:
    for (;;)
    {
      k = m + k - AndroidUtilities.dp(1.0F);
      this.subtitleTextView.layout(j, i + k, this.subtitleTextView.getMeasuredWidth() + j, i + k + this.subtitleTextView.getTextHeight());
      m = getChildCount();
      k = 0;
      for (;;)
      {
        if (k >= m) {
          return;
        }
        localView = getChildAt(k);
        if ((localView.getVisibility() != 8) && (localView != this.titleTextView) && (localView != this.subtitleTextView) && (localView != this.menu) && (localView != this.backButtonImageView)) {
          break;
        }
        k += 1;
      }
      i = 0;
      break;
      f = 72.0F;
      break label69;
      if (AndroidUtilities.isTablet()) {}
      for (f = 26.0F;; f = 18.0F)
      {
        j = AndroidUtilities.dp(f);
        break;
      }
      f = 66.0F;
      break label113;
      k = paramInt3 - paramInt1 - this.menu.getMeasuredWidth();
      break label120;
      f = 3.0F;
      break label229;
      k = (getCurrentActionBarHeight() - this.titleTextView.getTextHeight()) / 2;
      break label239;
    }
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
    int n = localView.getMeasuredWidth();
    int i1 = localView.getMeasuredHeight();
    int j = localLayoutParams.gravity;
    int i = j;
    if (j == -1) {
      i = 51;
    }
    switch (i & 0x7 & 0x7)
    {
    default: 
      j = localLayoutParams.leftMargin;
      label651:
      switch (i & 0x70)
      {
      default: 
        i = localLayoutParams.topMargin;
      }
      break;
    }
    for (;;)
    {
      localView.layout(j, i, j + n, i + i1);
      break;
      j = (paramInt3 - paramInt1 - n) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
      break label651;
      j = paramInt3 - n - localLayoutParams.rightMargin;
      break label651;
      i = localLayoutParams.topMargin;
      continue;
      i = (paramInt4 - paramInt2 - i1) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
      continue;
      i = paramInt4 - paramInt2 - i1 - localLayoutParams.bottomMargin;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.getSize(paramInt1);
    View.MeasureSpec.getSize(paramInt2);
    int i = getCurrentActionBarHeight();
    int k = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
    float f;
    label102:
    label143:
    label158:
    label223:
    Object localObject;
    if (this.occupyStatusBar)
    {
      paramInt2 = AndroidUtilities.statusBarHeight;
      setMeasuredDimension(j, paramInt2 + i + this.extraHeight);
      if ((this.backButtonImageView == null) || (this.backButtonImageView.getVisibility() == 8)) {
        break label487;
      }
      this.backButtonImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0F), 1073741824), k);
      if (!AndroidUtilities.isTablet()) {
        break label480;
      }
      f = 80.0F;
      paramInt2 = AndroidUtilities.dp(f);
      if ((this.menu != null) && (this.menu.getVisibility() != 8))
      {
        if (!this.isSearchFieldVisible) {
          break label519;
        }
        if (!AndroidUtilities.isTablet()) {
          break label512;
        }
        f = 74.0F;
        i = View.MeasureSpec.makeMeasureSpec(j - AndroidUtilities.dp(f), 1073741824);
        this.menu.measure(i, k);
      }
      if (((this.titleTextView != null) && (this.titleTextView.getVisibility() != 8)) || ((this.subtitleTextView != null) && (this.subtitleTextView.getVisibility() != 8)))
      {
        if (this.menu == null) {
          break label532;
        }
        i = this.menu.getMeasuredWidth();
        i = j - i - AndroidUtilities.dp(16.0F) - paramInt2;
        if ((this.titleTextView != null) && (this.titleTextView.getVisibility() != 8))
        {
          localObject = this.titleTextView;
          if ((AndroidUtilities.isTablet()) || (getResources().getConfiguration().orientation != 2)) {
            break label538;
          }
          paramInt2 = 18;
          label287:
          ((SimpleTextView)localObject).setTextSize(paramInt2);
          this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), Integer.MIN_VALUE));
        }
        if ((this.subtitleTextView != null) && (this.subtitleTextView.getVisibility() != 8))
        {
          localObject = this.subtitleTextView;
          if ((AndroidUtilities.isTablet()) || (getResources().getConfiguration().orientation != 2)) {
            break label544;
          }
          paramInt2 = 14;
          label368:
          ((SimpleTextView)localObject).setTextSize(paramInt2);
          this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), Integer.MIN_VALUE));
        }
      }
      i = getChildCount();
      paramInt2 = 0;
      label409:
      if (paramInt2 >= i) {
        return;
      }
      localObject = getChildAt(paramInt2);
      if ((((View)localObject).getVisibility() != 8) && (localObject != this.titleTextView) && (localObject != this.subtitleTextView) && (localObject != this.menu) && (localObject != this.backButtonImageView)) {
        break label550;
      }
    }
    for (;;)
    {
      paramInt2 += 1;
      break label409;
      paramInt2 = 0;
      break;
      label480:
      f = 72.0F;
      break label102;
      label487:
      if (AndroidUtilities.isTablet()) {}
      for (f = 26.0F;; f = 18.0F)
      {
        paramInt2 = AndroidUtilities.dp(f);
        break;
      }
      label512:
      f = 66.0F;
      break label143;
      label519:
      i = View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE);
      break label158;
      label532:
      i = 0;
      break label223;
      label538:
      paramInt2 = 20;
      break label287;
      label544:
      paramInt2 = 16;
      break label368;
      label550:
      measureChildWithMargins((View)localObject, paramInt1, 0, View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824), 0);
    }
  }
  
  public void onMenuButtonPressed()
  {
    if (this.menu != null) {
      this.menu.onMenuButtonPressed();
    }
  }
  
  protected void onPause()
  {
    if (this.menu != null) {
      this.menu.hideAllPopupMenus();
    }
  }
  
  protected void onSearchFieldVisibilityChanged(boolean paramBoolean)
  {
    int j = 4;
    this.isSearchFieldVisible = paramBoolean;
    Object localObject;
    int i;
    if (this.titleTextView != null)
    {
      localObject = this.titleTextView;
      if (paramBoolean)
      {
        i = 4;
        ((SimpleTextView)localObject).setVisibility(i);
      }
    }
    else
    {
      if (this.subtitleTextView != null)
      {
        localObject = this.subtitleTextView;
        if (!paramBoolean) {
          break label107;
        }
        i = j;
        label53:
        ((SimpleTextView)localObject).setVisibility(i);
      }
      localObject = this.backButtonImageView.getDrawable();
      if ((localObject != null) && ((localObject instanceof MenuDrawable)))
      {
        localObject = (MenuDrawable)localObject;
        if (!paramBoolean) {
          break label112;
        }
      }
    }
    label107:
    label112:
    for (float f = 1.0F;; f = 0.0F)
    {
      ((MenuDrawable)localObject).setRotation(f, true);
      return;
      i = 0;
      break;
      i = 0;
      break label53;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    return (super.onTouchEvent(paramMotionEvent)) || (this.interceptTouches);
  }
  
  public void openSearchField(String paramString)
  {
    if ((this.menu == null) || (paramString == null)) {
      return;
    }
    ActionBarMenu localActionBarMenu = this.menu;
    if (!this.isSearchFieldVisible) {}
    for (boolean bool = true;; bool = false)
    {
      localActionBarMenu.openSearchField(bool, paramString);
      return;
    }
  }
  
  public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick paramActionBarMenuOnItemClick)
  {
    this.actionBarMenuOnItemClick = paramActionBarMenuOnItemClick;
  }
  
  public void setAddToContainer(boolean paramBoolean)
  {
    this.addToContainer = paramBoolean;
  }
  
  public void setAllowOverlayTitle(boolean paramBoolean)
  {
    this.allowOverlayTitle = paramBoolean;
  }
  
  public void setBackButtonDrawable(Drawable paramDrawable)
  {
    if (this.backButtonImageView == null) {
      createBackButtonImage();
    }
    ImageView localImageView = this.backButtonImageView;
    int i;
    if (paramDrawable == null)
    {
      i = 8;
      localImageView.setVisibility(i);
      this.backButtonImageView.setImageDrawable(paramDrawable);
      if ((paramDrawable instanceof BackDrawable))
      {
        paramDrawable = (BackDrawable)paramDrawable;
        if (!isActionModeShowed()) {
          break label71;
        }
      }
    }
    label71:
    for (float f = 1.0F;; f = 0.0F)
    {
      paramDrawable.setRotation(f, false);
      return;
      i = 0;
      break;
    }
  }
  
  public void setBackButtonImage(int paramInt)
  {
    if (this.backButtonImageView == null) {
      createBackButtonImage();
    }
    ImageView localImageView = this.backButtonImageView;
    if (paramInt == 0) {}
    for (int i = 8;; i = 0)
    {
      localImageView.setVisibility(i);
      this.backButtonImageView.setImageResource(paramInt);
      return;
    }
  }
  
  public void setCastShadows(boolean paramBoolean)
  {
    this.castShadows = paramBoolean;
  }
  
  public void setExtraHeight(int paramInt)
  {
    this.extraHeight = paramInt;
  }
  
  public void setInterceptTouches(boolean paramBoolean)
  {
    this.interceptTouches = paramBoolean;
  }
  
  public void setItemsBackgroundColor(int paramInt)
  {
    this.itemsBackgroundColor = paramInt;
    if (this.backButtonImageView != null) {
      this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(this.itemsBackgroundColor));
    }
  }
  
  public void setOccupyStatusBar(boolean paramBoolean)
  {
    this.occupyStatusBar = paramBoolean;
    ActionBarMenu localActionBarMenu;
    if (this.actionMode != null)
    {
      localActionBarMenu = this.actionMode;
      if (!this.occupyStatusBar) {
        break label37;
      }
    }
    label37:
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      localActionBarMenu.setPadding(0, i, 0, 0);
      return;
    }
  }
  
  public void setSubtitle(CharSequence paramCharSequence)
  {
    if ((paramCharSequence != null) && (this.subtitleTextView == null)) {
      createSubtitleTextView();
    }
    SimpleTextView localSimpleTextView;
    if (this.subtitleTextView != null)
    {
      localSimpleTextView = this.subtitleTextView;
      if ((paramCharSequence == null) || (this.isSearchFieldVisible)) {
        break label54;
      }
    }
    label54:
    for (int i = 0;; i = 4)
    {
      localSimpleTextView.setVisibility(i);
      this.subtitleTextView.setText(paramCharSequence);
      return;
    }
  }
  
  public void setSubtitleColor(int paramInt)
  {
    if (this.subtitleTextView == null) {
      createSubtitleTextView();
    }
    this.subtitleTextView.setTextColor(paramInt);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    if ((paramCharSequence != null) && (this.titleTextView == null)) {
      createTitleTextView();
    }
    SimpleTextView localSimpleTextView;
    if (this.titleTextView != null)
    {
      this.lastTitle = paramCharSequence;
      localSimpleTextView = this.titleTextView;
      if ((paramCharSequence == null) || (this.isSearchFieldVisible)) {
        break label59;
      }
    }
    label59:
    for (int i = 0;; i = 4)
    {
      localSimpleTextView.setVisibility(i);
      this.titleTextView.setText(paramCharSequence);
      return;
    }
  }
  
  public void setTitleOverlayText(String paramString)
  {
    if ((!this.allowOverlayTitle) || (this.parentFragment.parentLayout == null)) {}
    do
    {
      return;
      if (paramString == null) {
        break;
      }
      if ((paramString != null) && (this.titleTextView == null)) {
        createTitleTextView();
      }
    } while (this.titleTextView == null);
    SimpleTextView localSimpleTextView = this.titleTextView;
    if ((paramString != null) && (!this.isSearchFieldVisible)) {}
    for (int i = 0;; i = 4)
    {
      localSimpleTextView.setVisibility(i);
      this.titleTextView.setText(paramString);
      return;
      paramString = this.lastTitle;
      break;
    }
  }
  
  public void showActionMode()
  {
    if ((this.actionMode == null) || (this.actionModeVisible)) {}
    do
    {
      return;
      this.actionModeVisible = true;
      localObject = new ArrayList();
      ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.actionMode, "alpha", new float[] { 0.0F, 1.0F }));
      if ((this.occupyStatusBar) && (this.actionModeTop != null)) {
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.actionModeTop, "alpha", new float[] { 0.0F, 1.0F }));
      }
      if (this.actionModeAnimation != null) {
        this.actionModeAnimation.cancel();
      }
      this.actionModeAnimation = new AnimatorSet();
      this.actionModeAnimation.playTogether((Collection)localObject);
      this.actionModeAnimation.setDuration(200L);
      this.actionModeAnimation.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ActionBar.this.actionModeAnimation != null) && (ActionBar.this.actionModeAnimation.equals(paramAnonymousAnimator))) {
            ActionBar.access$302(ActionBar.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ActionBar.this.actionModeAnimation != null) && (ActionBar.this.actionModeAnimation.equals(paramAnonymousAnimator)))
          {
            ActionBar.access$302(ActionBar.this, null);
            if (ActionBar.this.titleTextView != null) {
              ActionBar.this.titleTextView.setVisibility(4);
            }
            if (ActionBar.this.subtitleTextView != null) {
              ActionBar.this.subtitleTextView.setVisibility(4);
            }
            if (ActionBar.this.menu != null) {
              ActionBar.this.menu.setVisibility(4);
            }
          }
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          ActionBar.this.actionMode.setVisibility(0);
          if ((ActionBar.this.occupyStatusBar) && (ActionBar.this.actionModeTop != null)) {
            ActionBar.this.actionModeTop.setVisibility(0);
          }
        }
      });
      this.actionModeAnimation.start();
    } while (this.backButtonImageView == null);
    Object localObject = this.backButtonImageView.getDrawable();
    if ((localObject instanceof BackDrawable)) {
      ((BackDrawable)localObject).setRotation(1.0F, true);
    }
    this.backButtonImageView.setBackgroundDrawable(Theme.createBarSelectorDrawable(-986896));
  }
  
  public void showActionModeTop()
  {
    if ((this.occupyStatusBar) && (this.actionModeTop == null))
    {
      this.actionModeTop = new View(getContext());
      this.actionModeTop.setBackgroundColor(-1728053248);
      addView(this.actionModeTop);
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.actionModeTop.getLayoutParams();
      localLayoutParams.height = AndroidUtilities.statusBarHeight;
      localLayoutParams.width = -1;
      localLayoutParams.gravity = 51;
      this.actionModeTop.setLayoutParams(localLayoutParams);
    }
  }
  
  public static class ActionBarMenuOnItemClick
  {
    public boolean canOpenMenu()
    {
      return true;
    }
    
    public void onItemClick(int paramInt) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/ActionBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */