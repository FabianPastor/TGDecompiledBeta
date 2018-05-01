package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.TextPaint;
import android.text.TextUtils;
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
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.FireworksEffect;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SnowflakesEffect;

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
  private FireworksEffect fireworksEffect;
  private Paint.FontMetricsInt fontMetricsInt;
  private boolean interceptTouches;
  private boolean isBackOverlayVisible;
  protected boolean isSearchFieldVisible;
  protected int itemsActionModeBackgroundColor;
  protected int itemsActionModeColor;
  protected int itemsBackgroundColor;
  protected int itemsColor;
  private CharSequence lastSubtitle;
  private CharSequence lastTitle;
  private boolean manualStart;
  private ActionBarMenu menu;
  private boolean occupyStatusBar;
  protected BaseFragment parentFragment;
  private Rect rect;
  private SnowflakesEffect snowflakesEffect;
  private SimpleTextView subtitleTextView;
  private boolean supportsHolidayImage;
  private Runnable titleActionRunnable;
  private boolean titleOverlayShown;
  private int titleRightMargin;
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
      setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ActionBar.this.titleActionRunnable != null) {
            ActionBar.this.titleActionRunnable.run();
          }
        }
      });
      return;
    }
  }
  
  private void createBackButtonImage()
  {
    if (this.backButtonImageView != null) {}
    for (;;)
    {
      return;
      this.backButtonImageView = new ImageView(getContext());
      this.backButtonImageView.setScaleType(ImageView.ScaleType.CENTER);
      this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
      if (this.itemsColor != 0) {
        this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, PorterDuff.Mode.MULTIPLY));
      }
      this.backButtonImageView.setPadding(AndroidUtilities.dp(1.0F), 0, 0, 0);
      addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
      this.backButtonImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((!ActionBar.this.actionModeVisible) && (ActionBar.this.isSearchFieldVisible)) {
            ActionBar.this.closeSearchField();
          }
          for (;;)
          {
            return;
            if (ActionBar.this.actionBarMenuOnItemClick != null) {
              ActionBar.this.actionBarMenuOnItemClick.onItemClick(-1);
            }
          }
        }
      });
    }
  }
  
  private void createSubtitleTextView()
  {
    if (this.subtitleTextView != null) {}
    for (;;)
    {
      return;
      this.subtitleTextView = new SimpleTextView(getContext());
      this.subtitleTextView.setGravity(3);
      this.subtitleTextView.setVisibility(8);
      this.subtitleTextView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
      addView(this.subtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
    }
  }
  
  private void createTitleTextView()
  {
    if (this.titleTextView != null) {}
    for (;;)
    {
      return;
      this.titleTextView = new SimpleTextView(getContext());
      this.titleTextView.setGravity(3);
      this.titleTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
      this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.titleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
    }
  }
  
  public static int getCurrentActionBarHeight()
  {
    int i;
    if (AndroidUtilities.isTablet()) {
      i = AndroidUtilities.dp(64.0F);
    }
    for (;;)
    {
      return i;
      if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
        i = AndroidUtilities.dp(48.0F);
      } else {
        i = AndroidUtilities.dp(56.0F);
      }
    }
  }
  
  public void closeSearchField()
  {
    closeSearchField(true);
  }
  
  public void closeSearchField(boolean paramBoolean)
  {
    if ((!this.isSearchFieldVisible) || (this.menu == null)) {}
    for (;;)
    {
      return;
      this.menu.closeSearchField(paramBoolean);
    }
  }
  
  public ActionBarMenu createActionMode()
  {
    if (this.actionMode != null)
    {
      localObject = this.actionMode;
      return (ActionBarMenu)localObject;
    }
    this.actionMode = new ActionBarMenu(getContext(), this);
    this.actionMode.isActionMode = true;
    this.actionMode.setBackgroundColor(Theme.getColor("actionBarActionModeDefault"));
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
        this.actionModeTop.setBackgroundColor(Theme.getColor("actionBarActionModeDefaultTop"));
        addView(this.actionModeTop);
        localObject = (FrameLayout.LayoutParams)this.actionModeTop.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).height = AndroidUtilities.statusBarHeight;
        ((FrameLayout.LayoutParams)localObject).width = -1;
        ((FrameLayout.LayoutParams)localObject).gravity = 51;
        this.actionModeTop.setLayoutParams((ViewGroup.LayoutParams)localObject);
        this.actionModeTop.setVisibility(4);
      }
      localObject = this.actionMode;
      break;
    }
  }
  
  public ActionBarMenu createMenu()
  {
    if (this.menu != null) {}
    for (ActionBarMenu localActionBarMenu = this.menu;; localActionBarMenu = this.menu)
    {
      return localActionBarMenu;
      this.menu = new ActionBarMenu(getContext(), this);
      addView(this.menu, 0, LayoutHelper.createFrame(-2, -1, 5));
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    if ((this.supportsHolidayImage) && (!this.titleOverlayShown) && (!LocaleController.isRTL) && (paramView == this.titleTextView))
    {
      paramView = Theme.getCurrentHolidayDrawable();
      if (paramView != null)
      {
        TextPaint localTextPaint = this.titleTextView.getTextPaint();
        localTextPaint.getFontMetricsInt(this.fontMetricsInt);
        localTextPaint.getTextBounds((String)this.titleTextView.getText(), 0, 1, this.rect);
        int i = this.titleTextView.getTextStartX() + Theme.getCurrentHolidayDrawableXOffset() + (this.rect.width() - (paramView.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2;
        int j = this.titleTextView.getTextStartY() + Theme.getCurrentHolidayDrawableYOffset() + (int)Math.ceil((this.titleTextView.getTextHeight() - this.rect.height()) / 2.0F);
        paramView.setBounds(i, j - paramView.getIntrinsicHeight(), paramView.getIntrinsicWidth() + i, j);
        paramView.draw(paramCanvas);
        if (!Theme.canStartHolidayAnimation()) {
          break label224;
        }
        if (this.snowflakesEffect == null) {
          this.snowflakesEffect = new SnowflakesEffect();
        }
        if (this.snowflakesEffect == null) {
          break label246;
        }
        this.snowflakesEffect.onDraw(this, paramCanvas);
      }
    }
    for (;;)
    {
      return bool;
      label224:
      if ((this.manualStart) || (this.snowflakesEffect == null)) {
        break;
      }
      this.snowflakesEffect = null;
      break;
      label246:
      if (this.fireworksEffect != null) {
        this.fireworksEffect.onDraw(this, paramCanvas);
      }
    }
  }
  
  public ActionBarMenuOnItemClick getActionBarMenuOnItemClick()
  {
    return this.actionBarMenuOnItemClick;
  }
  
  public boolean getAddToContainer()
  {
    return this.addToContainer;
  }
  
  public View getBackButton()
  {
    return this.backButtonImageView;
  }
  
  public boolean getCastShadows()
  {
    return this.castShadows;
  }
  
  public boolean getOccupyStatusBar()
  {
    return this.occupyStatusBar;
  }
  
  public String getSubtitle()
  {
    if (this.subtitleTextView == null) {}
    for (String str = null;; str = this.subtitleTextView.getText().toString()) {
      return str;
    }
  }
  
  public SimpleTextView getSubtitleTextView()
  {
    return this.subtitleTextView;
  }
  
  public String getTitle()
  {
    if (this.titleTextView == null) {}
    for (String str = null;; str = this.titleTextView.getText().toString()) {
      return str;
    }
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
    for (;;)
    {
      return;
      this.actionModeVisible = false;
      Object localObject = new ArrayList();
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
      this.actionModeAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ActionBar.this.actionModeAnimation != null) && (ActionBar.this.actionModeAnimation.equals(paramAnonymousAnimator))) {
            ActionBar.access$502(ActionBar.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ActionBar.this.actionModeAnimation != null) && (ActionBar.this.actionModeAnimation.equals(paramAnonymousAnimator)))
          {
            ActionBar.access$502(ActionBar.this, null);
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
      if ((this.subtitleTextView != null) && (!TextUtils.isEmpty(this.subtitleTextView.getText()))) {
        this.subtitleTextView.setVisibility(0);
      }
      if (this.menu != null) {
        this.menu.setVisibility(0);
      }
      if (this.backButtonImageView != null)
      {
        localObject = this.backButtonImageView.getDrawable();
        if ((localObject instanceof BackDrawable)) {
          ((BackDrawable)localObject).setRotation(0.0F, true);
        }
        this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
      }
    }
  }
  
  public boolean isActionModeShowed()
  {
    if ((this.actionMode != null) && (this.actionModeVisible)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isSearchFieldVisible()
  {
    return this.isSearchFieldVisible;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.supportsHolidayImage) && (!this.titleOverlayShown) && (!LocaleController.isRTL) && (paramMotionEvent.getAction() == 0))
    {
      Drawable localDrawable = Theme.getCurrentHolidayDrawable();
      if ((localDrawable != null) && (localDrawable.getBounds().contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())))
      {
        this.manualStart = true;
        if (this.snowflakesEffect != null) {
          break label100;
        }
        this.fireworksEffect = null;
        this.snowflakesEffect = new SnowflakesEffect();
        this.titleTextView.invalidate();
        invalidate();
      }
    }
    for (;;)
    {
      return super.onInterceptTouchEvent(paramMotionEvent);
      label100:
      if (BuildVars.DEBUG_PRIVATE_VERSION)
      {
        this.snowflakesEffect = null;
        this.fireworksEffect = new FireworksEffect();
        this.titleTextView.invalidate();
        invalidate();
      }
    }
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
        break label478;
      }
      this.backButtonImageView.layout(0, i, this.backButtonImageView.getMeasuredWidth(), this.backButtonImageView.getMeasuredHeight() + i);
      if (!AndroidUtilities.isTablet()) {
        break label470;
      }
      f = 80.0F;
      j = AndroidUtilities.dp(f);
      if ((this.menu != null) && (this.menu.getVisibility() != 8))
      {
        if (!this.isSearchFieldVisible) {
          break label515;
        }
        if (!AndroidUtilities.isTablet()) {
          break label507;
        }
        f = 74.0F;
        k = AndroidUtilities.dp(f);
        this.menu.layout(k, i, this.menu.getMeasuredWidth() + k, this.menu.getMeasuredHeight() + i);
      }
      if ((this.titleTextView != null) && (this.titleTextView.getVisibility() != 8))
      {
        if ((this.subtitleTextView == null) || (this.subtitleTextView.getVisibility() == 8)) {
          break label540;
        }
        k = (getCurrentActionBarHeight() / 2 - this.titleTextView.getTextHeight()) / 2;
        if ((AndroidUtilities.isTablet()) || (getResources().getConfiguration().orientation != 2)) {
          break label532;
        }
        f = 2.0F;
        k += AndroidUtilities.dp(f);
        this.titleTextView.layout(j, i + k, this.titleTextView.getMeasuredWidth() + j, i + k + this.titleTextView.getTextHeight());
      }
      if ((this.subtitleTextView != null) && (this.subtitleTextView.getVisibility() != 8))
      {
        m = getCurrentActionBarHeight() / 2;
        k = (getCurrentActionBarHeight() / 2 - this.subtitleTextView.getTextHeight()) / 2;
        if ((AndroidUtilities.isTablet()) || (getResources().getConfiguration().orientation != 2)) {
          break label558;
        }
      }
    }
    View localView;
    label470:
    label478:
    label507:
    label515:
    label532:
    label540:
    label558:
    for (;;)
    {
      k = k + m - AndroidUtilities.dp(1.0F);
      this.subtitleTextView.layout(j, i + k, this.subtitleTextView.getMeasuredWidth() + j, i + k + this.subtitleTextView.getTextHeight());
      m = getChildCount();
      for (k = 0;; k++)
      {
        if (k >= m) {
          return;
        }
        localView = getChildAt(k);
        if ((localView.getVisibility() != 8) && (localView != this.titleTextView) && (localView != this.subtitleTextView) && (localView != this.menu) && (localView != this.backButtonImageView)) {
          break;
        }
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
      label647:
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
      break label647;
      j = paramInt3 - n - localLayoutParams.rightMargin;
      break label647;
      i = localLayoutParams.topMargin;
      continue;
      i = (paramInt4 - paramInt2 - i1) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
      continue;
      i = paramInt4 - paramInt2 - i1 - localLayoutParams.bottomMargin;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    View.MeasureSpec.getSize(paramInt2);
    int j = getCurrentActionBarHeight();
    int k = View.MeasureSpec.makeMeasureSpec(j, NUM);
    float f;
    label101:
    label144:
    label159:
    label224:
    Object localObject;
    if (this.occupyStatusBar)
    {
      paramInt2 = AndroidUtilities.statusBarHeight;
      setMeasuredDimension(i, paramInt2 + j + this.extraHeight);
      if ((this.backButtonImageView == null) || (this.backButtonImageView.getVisibility() == 8)) {
        break label492;
      }
      this.backButtonImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0F), NUM), k);
      if (!AndroidUtilities.isTablet()) {
        break label484;
      }
      f = 80.0F;
      paramInt2 = AndroidUtilities.dp(f);
      if ((this.menu != null) && (this.menu.getVisibility() != 8))
      {
        if (!this.isSearchFieldVisible) {
          break label528;
        }
        if (!AndroidUtilities.isTablet()) {
          break label520;
        }
        f = 74.0F;
        j = View.MeasureSpec.makeMeasureSpec(i - AndroidUtilities.dp(f), NUM);
        this.menu.measure(j, k);
      }
      if (((this.titleTextView != null) && (this.titleTextView.getVisibility() != 8)) || ((this.subtitleTextView != null) && (this.subtitleTextView.getVisibility() != 8)))
      {
        if (this.menu == null) {
          break label540;
        }
        j = this.menu.getMeasuredWidth();
        j = i - j - AndroidUtilities.dp(16.0F) - paramInt2 - this.titleRightMargin;
        if ((this.titleTextView != null) && (this.titleTextView.getVisibility() != 8))
        {
          localObject = this.titleTextView;
          if ((AndroidUtilities.isTablet()) || (getResources().getConfiguration().orientation != 2)) {
            break label546;
          }
          paramInt2 = 18;
          label292:
          ((SimpleTextView)localObject).setTextSize(paramInt2);
          this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), Integer.MIN_VALUE));
        }
        if ((this.subtitleTextView != null) && (this.subtitleTextView.getVisibility() != 8))
        {
          localObject = this.subtitleTextView;
          if ((AndroidUtilities.isTablet()) || (getResources().getConfiguration().orientation != 2)) {
            break label552;
          }
          paramInt2 = 14;
          label373:
          ((SimpleTextView)localObject).setTextSize(paramInt2);
          this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(j, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), Integer.MIN_VALUE));
        }
      }
      j = getChildCount();
      paramInt2 = 0;
      label414:
      if (paramInt2 >= j) {
        return;
      }
      localObject = getChildAt(paramInt2);
      if ((((View)localObject).getVisibility() != 8) && (localObject != this.titleTextView) && (localObject != this.subtitleTextView) && (localObject != this.menu) && (localObject != this.backButtonImageView)) {
        break label558;
      }
    }
    for (;;)
    {
      paramInt2++;
      break label414;
      paramInt2 = 0;
      break;
      label484:
      f = 72.0F;
      break label101;
      label492:
      if (AndroidUtilities.isTablet()) {}
      for (f = 26.0F;; f = 18.0F)
      {
        paramInt2 = AndroidUtilities.dp(f);
        break;
      }
      label520:
      f = 66.0F;
      break label144;
      label528:
      j = View.MeasureSpec.makeMeasureSpec(i, Integer.MIN_VALUE);
      break label159;
      label540:
      j = 0;
      break label224;
      label546:
      paramInt2 = 20;
      break label292;
      label552:
      paramInt2 = 16;
      break label373;
      label558:
      measureChildWithMargins((View)localObject, paramInt1, 0, View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM), 0);
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
    int i = 4;
    this.isSearchFieldVisible = paramBoolean;
    Object localObject;
    int j;
    if (this.titleTextView != null)
    {
      localObject = this.titleTextView;
      if (paramBoolean)
      {
        j = 4;
        ((SimpleTextView)localObject).setVisibility(j);
      }
    }
    else
    {
      if ((this.subtitleTextView != null) && (!TextUtils.isEmpty(this.subtitleTextView.getText())))
      {
        localObject = this.subtitleTextView;
        if (!paramBoolean) {
          break label115;
        }
        j = i;
        label64:
        ((SimpleTextView)localObject).setVisibility(j);
      }
      localObject = this.backButtonImageView.getDrawable();
      if ((localObject != null) && ((localObject instanceof MenuDrawable)))
      {
        localObject = (MenuDrawable)localObject;
        if (!paramBoolean) {
          break label121;
        }
      }
    }
    label115:
    label121:
    for (float f = 1.0F;; f = 0.0F)
    {
      ((MenuDrawable)localObject).setRotation(f, true);
      return;
      j = 0;
      break;
      j = 0;
      break label64;
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((super.onTouchEvent(paramMotionEvent)) || (this.interceptTouches)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
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
      break;
    }
  }
  
  public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick paramActionBarMenuOnItemClick)
  {
    this.actionBarMenuOnItemClick = paramActionBarMenuOnItemClick;
  }
  
  public void setActionModeColor(int paramInt)
  {
    if (this.actionMode != null) {
      this.actionMode.setBackgroundColor(paramInt);
    }
  }
  
  public void setActionModeTopColor(int paramInt)
  {
    if (this.actionModeTop != null) {
      this.actionModeTop.setBackgroundColor(paramInt);
    }
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
          break label87;
        }
      }
    }
    label87:
    for (float f = 1.0F;; f = 0.0F)
    {
      paramDrawable.setRotation(f, false);
      paramDrawable.setRotatedColor(this.itemsActionModeColor);
      paramDrawable.setColor(this.itemsColor);
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
  
  public void setItemsBackgroundColor(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.itemsActionModeBackgroundColor = paramInt;
      if ((this.actionModeVisible) && (this.backButtonImageView != null)) {
        this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
      }
      if (this.actionMode != null) {
        this.actionMode.updateItemsBackgroundColor();
      }
    }
    for (;;)
    {
      return;
      this.itemsBackgroundColor = paramInt;
      if (this.backButtonImageView != null) {
        this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
      }
      if (this.menu != null) {
        this.menu.updateItemsBackgroundColor();
      }
    }
  }
  
  public void setItemsColor(int paramInt, boolean paramBoolean)
  {
    Drawable localDrawable;
    if (paramBoolean)
    {
      this.itemsActionModeColor = paramInt;
      if (this.actionMode != null) {
        this.actionMode.updateItemsColor();
      }
      if (this.backButtonImageView != null)
      {
        localDrawable = this.backButtonImageView.getDrawable();
        if ((localDrawable instanceof BackDrawable)) {
          ((BackDrawable)localDrawable).setRotatedColor(paramInt);
        }
      }
    }
    for (;;)
    {
      return;
      this.itemsColor = paramInt;
      if ((this.backButtonImageView != null) && (this.itemsColor != 0))
      {
        this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, PorterDuff.Mode.MULTIPLY));
        localDrawable = this.backButtonImageView.getDrawable();
        if ((localDrawable instanceof BackDrawable)) {
          ((BackDrawable)localDrawable).setColor(paramInt);
        }
      }
      if (this.menu != null) {
        this.menu.updateItemsColor();
      }
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
  
  public void setPopupBackgroundColor(int paramInt)
  {
    if (this.menu != null) {
      this.menu.redrawPopup(paramInt);
    }
  }
  
  public void setPopupItemsColor(int paramInt)
  {
    if (this.menu != null) {
      this.menu.setPopupItemsColor(paramInt);
    }
  }
  
  public void setSearchTextColor(int paramInt, boolean paramBoolean)
  {
    if (this.menu != null) {
      this.menu.setSearchTextColor(paramInt, paramBoolean);
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
      this.lastSubtitle = paramCharSequence;
      localSimpleTextView = this.subtitleTextView;
      if ((TextUtils.isEmpty(paramCharSequence)) || (this.isSearchFieldVisible)) {
        break label62;
      }
    }
    label62:
    for (int i = 0;; i = 8)
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
  
  public void setSupportsHolidayImage(boolean paramBoolean)
  {
    this.supportsHolidayImage = paramBoolean;
    if (this.supportsHolidayImage)
    {
      this.fontMetricsInt = new Paint.FontMetricsInt();
      this.rect = new Rect();
    }
    invalidate();
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
  
  public void setTitleColor(int paramInt)
  {
    if (this.titleTextView == null) {
      createTitleTextView();
    }
    this.titleTextView.setTextColor(paramInt);
  }
  
  public void setTitleOverlayText(String paramString1, String paramString2, Runnable paramRunnable)
  {
    int i = 0;
    if ((!this.allowOverlayTitle) || (this.parentFragment.parentLayout == null)) {
      return;
    }
    Object localObject;
    label28:
    boolean bool;
    if (paramString1 != null)
    {
      localObject = paramString1;
      if ((localObject != null) && (this.titleTextView == null)) {
        createTitleTextView();
      }
      if (this.titleTextView != null)
      {
        if (paramString1 == null) {
          break label199;
        }
        bool = true;
        label58:
        this.titleOverlayShown = bool;
        if (this.supportsHolidayImage)
        {
          this.titleTextView.invalidate();
          invalidate();
        }
        paramString1 = this.titleTextView;
        if ((localObject == null) || (this.isSearchFieldVisible)) {
          break label205;
        }
        j = 0;
        label102:
        paramString1.setVisibility(j);
        this.titleTextView.setText((CharSequence)localObject);
      }
      if (paramString2 == null) {
        break label211;
      }
      paramString1 = paramString2;
      label123:
      if ((paramString1 != null) && (this.subtitleTextView == null)) {
        createSubtitleTextView();
      }
      if (this.subtitleTextView != null)
      {
        paramString2 = this.subtitleTextView;
        if ((TextUtils.isEmpty(paramString1)) || (this.isSearchFieldVisible)) {
          break label219;
        }
      }
    }
    label199:
    label205:
    label211:
    label219:
    for (int j = i;; j = 8)
    {
      paramString2.setVisibility(j);
      this.subtitleTextView.setText(paramString1);
      this.titleActionRunnable = paramRunnable;
      break;
      localObject = this.lastTitle;
      break label28;
      bool = false;
      break label58;
      j = 4;
      break label102;
      paramString1 = this.lastSubtitle;
      break label123;
    }
  }
  
  public void setTitleRightMargin(int paramInt)
  {
    this.titleRightMargin = paramInt;
  }
  
  public void showActionMode()
  {
    if ((this.actionMode == null) || (this.actionModeVisible)) {}
    for (;;)
    {
      return;
      this.actionModeVisible = true;
      Object localObject = new ArrayList();
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
      this.actionModeAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((ActionBar.this.actionModeAnimation != null) && (ActionBar.this.actionModeAnimation.equals(paramAnonymousAnimator))) {
            ActionBar.access$502(ActionBar.this, null);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((ActionBar.this.actionModeAnimation != null) && (ActionBar.this.actionModeAnimation.equals(paramAnonymousAnimator)))
          {
            ActionBar.access$502(ActionBar.this, null);
            if (ActionBar.this.titleTextView != null) {
              ActionBar.this.titleTextView.setVisibility(4);
            }
            if ((ActionBar.this.subtitleTextView != null) && (!TextUtils.isEmpty(ActionBar.this.subtitleTextView.getText()))) {
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
      if (this.backButtonImageView != null)
      {
        localObject = this.backButtonImageView.getDrawable();
        if ((localObject instanceof BackDrawable)) {
          ((BackDrawable)localObject).setRotation(1.0F, true);
        }
        this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
      }
    }
  }
  
  public void showActionModeTop()
  {
    if ((this.occupyStatusBar) && (this.actionModeTop == null))
    {
      this.actionModeTop = new View(getContext());
      this.actionModeTop.setBackgroundColor(Theme.getColor("actionBarActionModeDefaultTop"));
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