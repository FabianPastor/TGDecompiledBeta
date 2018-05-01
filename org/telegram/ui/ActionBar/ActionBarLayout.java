package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarLayout
  extends FrameLayout
{
  private static Drawable headerShadowDrawable;
  private static Drawable layerShadowDrawable;
  private static Paint scrimPaint;
  private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
  private int[] animateEndColors;
  private Theme.ThemeInfo animateSetThemeAfterAnimation;
  private int[] animateStartColors;
  private boolean animateThemeAfterAnimation;
  protected boolean animationInProgress;
  private float animationProgress = 0.0F;
  private Runnable animationRunnable;
  private View backgroundView;
  private boolean beginTrackingSent;
  private LinearLayoutContainer containerView;
  private LinearLayoutContainer containerViewBack;
  private ActionBar currentActionBar;
  private AnimatorSet currentAnimation;
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(1.5F);
  private Runnable delayedOpenAnimationRunnable;
  private ActionBarLayoutDelegate delegate = null;
  private DrawerLayoutContainer drawerLayoutContainer;
  public ArrayList<BaseFragment> fragmentsStack = null;
  private boolean inActionMode;
  public float innerTranslationX;
  private long lastFrameTime;
  private boolean maybeStartTracking;
  private Runnable onCloseAnimationEndRunnable;
  private Runnable onOpenAnimationEndRunnable;
  private Runnable overlayAction;
  protected Activity parentActivity = null;
  private boolean rebuildAfterAnimation;
  private boolean rebuildLastAfterAnimation;
  private boolean removeActionBarExtraHeight;
  private boolean showLastAfterAnimation;
  protected boolean startedTracking;
  private int startedTrackingPointerId;
  private int startedTrackingX;
  private int startedTrackingY;
  private String subtitleOverlayText;
  private float themeAnimationValue;
  private ThemeDescription.ThemeDescriptionDelegate themeAnimatorDelegate;
  private ThemeDescription[] themeAnimatorDescriptions;
  private AnimatorSet themeAnimatorSet;
  private String titleOverlayText;
  private boolean transitionAnimationInProgress;
  private long transitionAnimationStartTime;
  private boolean useAlphaAnimations;
  private VelocityTracker velocityTracker;
  private Runnable waitingForKeyboardCloseRunnable;
  
  public ActionBarLayout(Context paramContext)
  {
    super(paramContext);
    this.parentActivity = ((Activity)paramContext);
    if (layerShadowDrawable == null)
    {
      layerShadowDrawable = getResources().getDrawable(NUM);
      headerShadowDrawable = getResources().getDrawable(NUM).mutate();
      scrimPaint = new Paint();
    }
  }
  
  private void checkNeedRebuild()
  {
    if (this.rebuildAfterAnimation)
    {
      rebuildAllFragmentViews(this.rebuildLastAfterAnimation, this.showLastAfterAnimation);
      this.rebuildAfterAnimation = false;
    }
    for (;;)
    {
      return;
      if (this.animateThemeAfterAnimation)
      {
        animateThemedValues(this.animateSetThemeAfterAnimation);
        this.animateSetThemeAfterAnimation = null;
        this.animateThemeAfterAnimation = false;
      }
    }
  }
  
  private void closeLastFragmentInternalRemoveOld(BaseFragment paramBaseFragment)
  {
    paramBaseFragment.onPause();
    paramBaseFragment.onFragmentDestroy();
    paramBaseFragment.setParentLayout(null);
    this.fragmentsStack.remove(paramBaseFragment);
    this.containerViewBack.setVisibility(8);
    bringChildToFront(this.containerView);
  }
  
  private void onAnimationEndCheck(boolean paramBoolean)
  {
    onCloseAnimationEnd();
    onOpenAnimationEnd();
    if (this.waitingForKeyboardCloseRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.waitingForKeyboardCloseRunnable);
      this.waitingForKeyboardCloseRunnable = null;
    }
    if (this.currentAnimation != null)
    {
      if (paramBoolean) {
        this.currentAnimation.cancel();
      }
      this.currentAnimation = null;
    }
    if (this.animationRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
      this.animationRunnable = null;
    }
    setAlpha(1.0F);
    this.containerView.setAlpha(1.0F);
    this.containerView.setScaleX(1.0F);
    this.containerView.setScaleY(1.0F);
    this.containerViewBack.setAlpha(1.0F);
    this.containerViewBack.setScaleX(1.0F);
    this.containerViewBack.setScaleY(1.0F);
  }
  
  private void onCloseAnimationEnd()
  {
    if ((this.transitionAnimationInProgress) && (this.onCloseAnimationEndRunnable != null))
    {
      this.transitionAnimationInProgress = false;
      this.transitionAnimationStartTime = 0L;
      this.onCloseAnimationEndRunnable.run();
      this.onCloseAnimationEndRunnable = null;
      checkNeedRebuild();
    }
  }
  
  private void onOpenAnimationEnd()
  {
    if ((this.transitionAnimationInProgress) && (this.onOpenAnimationEndRunnable != null))
    {
      this.transitionAnimationInProgress = false;
      this.transitionAnimationStartTime = 0L;
      this.onOpenAnimationEndRunnable.run();
      this.onOpenAnimationEndRunnable = null;
      checkNeedRebuild();
    }
  }
  
  private void onSlideAnimationEnd(boolean paramBoolean)
  {
    Object localObject;
    if (!paramBoolean)
    {
      localObject = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1);
      ((BaseFragment)localObject).onPause();
      ((BaseFragment)localObject).onFragmentDestroy();
      ((BaseFragment)localObject).setParentLayout(null);
      this.fragmentsStack.remove(this.fragmentsStack.size() - 1);
      localObject = this.containerView;
      this.containerView = this.containerViewBack;
      this.containerViewBack = ((LinearLayoutContainer)localObject);
      bringChildToFront(this.containerView);
      localObject = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1);
      this.currentActionBar = ((BaseFragment)localObject).actionBar;
      ((BaseFragment)localObject).onResume();
      ((BaseFragment)localObject).onBecomeFullyVisible();
    }
    for (;;)
    {
      this.containerViewBack.setVisibility(8);
      this.startedTracking = false;
      this.animationInProgress = false;
      this.containerView.setTranslationX(0.0F);
      this.containerViewBack.setTranslationX(0.0F);
      setInnerTranslationX(0.0F);
      return;
      localObject = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 2);
      ((BaseFragment)localObject).onPause();
      ViewGroup localViewGroup;
      if (((BaseFragment)localObject).fragmentView != null)
      {
        localViewGroup = (ViewGroup)((BaseFragment)localObject).fragmentView.getParent();
        if (localViewGroup != null)
        {
          ((BaseFragment)localObject).onRemoveFromParent();
          localViewGroup.removeView(((BaseFragment)localObject).fragmentView);
        }
      }
      if ((((BaseFragment)localObject).actionBar != null) && (((BaseFragment)localObject).actionBar.getAddToContainer()))
      {
        localViewGroup = (ViewGroup)((BaseFragment)localObject).actionBar.getParent();
        if (localViewGroup != null) {
          localViewGroup.removeView(((BaseFragment)localObject).actionBar);
        }
      }
    }
  }
  
  private void prepareForMoving(MotionEvent paramMotionEvent)
  {
    this.maybeStartTracking = false;
    this.startedTracking = true;
    this.startedTrackingX = ((int)paramMotionEvent.getX());
    this.containerViewBack.setVisibility(0);
    this.beginTrackingSent = false;
    BaseFragment localBaseFragment = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 2);
    Object localObject = localBaseFragment.fragmentView;
    paramMotionEvent = (MotionEvent)localObject;
    if (localObject == null) {
      paramMotionEvent = localBaseFragment.createView(this.parentActivity);
    }
    localObject = (ViewGroup)paramMotionEvent.getParent();
    if (localObject != null)
    {
      localBaseFragment.onRemoveFromParent();
      ((ViewGroup)localObject).removeView(paramMotionEvent);
    }
    if ((localBaseFragment.actionBar != null) && (localBaseFragment.actionBar.getAddToContainer()))
    {
      localObject = (ViewGroup)localBaseFragment.actionBar.getParent();
      if (localObject != null) {
        ((ViewGroup)localObject).removeView(localBaseFragment.actionBar);
      }
      if (this.removeActionBarExtraHeight) {
        localBaseFragment.actionBar.setOccupyStatusBar(false);
      }
      this.containerViewBack.addView(localBaseFragment.actionBar);
      localBaseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
    }
    this.containerViewBack.addView(paramMotionEvent);
    localObject = paramMotionEvent.getLayoutParams();
    ((ViewGroup.LayoutParams)localObject).width = -1;
    ((ViewGroup.LayoutParams)localObject).height = -1;
    paramMotionEvent.setLayoutParams((ViewGroup.LayoutParams)localObject);
    if ((!localBaseFragment.hasOwnBackground) && (paramMotionEvent.getBackground() == null)) {
      paramMotionEvent.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    }
    localBaseFragment.onResume();
  }
  
  private void presentFragmentInternalRemoveOld(boolean paramBoolean, BaseFragment paramBaseFragment)
  {
    if (paramBaseFragment == null) {
      return;
    }
    paramBaseFragment.onPause();
    if (paramBoolean)
    {
      paramBaseFragment.onFragmentDestroy();
      paramBaseFragment.setParentLayout(null);
      this.fragmentsStack.remove(paramBaseFragment);
    }
    for (;;)
    {
      this.containerViewBack.setVisibility(8);
      break;
      ViewGroup localViewGroup;
      if (paramBaseFragment.fragmentView != null)
      {
        localViewGroup = (ViewGroup)paramBaseFragment.fragmentView.getParent();
        if (localViewGroup != null)
        {
          paramBaseFragment.onRemoveFromParent();
          localViewGroup.removeView(paramBaseFragment.fragmentView);
        }
      }
      if ((paramBaseFragment.actionBar != null) && (paramBaseFragment.actionBar.getAddToContainer()))
      {
        localViewGroup = (ViewGroup)paramBaseFragment.actionBar.getParent();
        if (localViewGroup != null) {
          localViewGroup.removeView(paramBaseFragment.actionBar);
        }
      }
    }
  }
  
  private void removeFragmentFromStackInternal(BaseFragment paramBaseFragment)
  {
    paramBaseFragment.onPause();
    paramBaseFragment.onFragmentDestroy();
    paramBaseFragment.setParentLayout(null);
    this.fragmentsStack.remove(paramBaseFragment);
  }
  
  private void startLayoutAnimation(final boolean paramBoolean1, final boolean paramBoolean2)
  {
    if (paramBoolean2)
    {
      this.animationProgress = 0.0F;
      this.lastFrameTime = (System.nanoTime() / 1000000L);
    }
    Runnable local2 = new Runnable()
    {
      public void run()
      {
        if (ActionBarLayout.this.animationRunnable != this) {}
        for (;;)
        {
          return;
          ActionBarLayout.access$502(ActionBarLayout.this, null);
          if (paramBoolean2) {
            ActionBarLayout.access$602(ActionBarLayout.this, System.currentTimeMillis());
          }
          long l1 = System.nanoTime() / 1000000L;
          long l2 = l1 - ActionBarLayout.this.lastFrameTime;
          long l3 = l2;
          if (l2 > 18L) {
            l3 = 18L;
          }
          ActionBarLayout.access$702(ActionBarLayout.this, l1);
          ActionBarLayout.access$802(ActionBarLayout.this, ActionBarLayout.this.animationProgress + (float)l3 / 150.0F);
          if (ActionBarLayout.this.animationProgress > 1.0F) {
            ActionBarLayout.access$802(ActionBarLayout.this, 1.0F);
          }
          float f = ActionBarLayout.this.decelerateInterpolator.getInterpolation(ActionBarLayout.this.animationProgress);
          if (paramBoolean1)
          {
            ActionBarLayout.this.containerView.setAlpha(f);
            ActionBarLayout.this.containerView.setTranslationX(AndroidUtilities.dp(48.0F) * (1.0F - f));
          }
          for (;;)
          {
            if (ActionBarLayout.this.animationProgress >= 1.0F) {
              break label247;
            }
            ActionBarLayout.this.startLayoutAnimation(paramBoolean1, false);
            break;
            ActionBarLayout.this.containerViewBack.setAlpha(1.0F - f);
            ActionBarLayout.this.containerViewBack.setTranslationX(AndroidUtilities.dp(48.0F) * f);
          }
          label247:
          ActionBarLayout.this.onAnimationEndCheck(false);
        }
      }
    };
    this.animationRunnable = local2;
    AndroidUtilities.runOnUIThread(local2);
  }
  
  public boolean addFragmentToStack(BaseFragment paramBaseFragment)
  {
    return addFragmentToStack(paramBaseFragment, -1);
  }
  
  public boolean addFragmentToStack(BaseFragment paramBaseFragment, int paramInt)
  {
    boolean bool;
    if (((this.delegate != null) && (!this.delegate.needAddFragmentToStack(paramBaseFragment, this))) || (!paramBaseFragment.onFragmentCreate()))
    {
      bool = false;
      return bool;
    }
    paramBaseFragment.setParentLayout(this);
    if (paramInt == -1)
    {
      if (!this.fragmentsStack.isEmpty())
      {
        BaseFragment localBaseFragment = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1);
        localBaseFragment.onPause();
        ViewGroup localViewGroup;
        if ((localBaseFragment.actionBar != null) && (localBaseFragment.actionBar.getAddToContainer()))
        {
          localViewGroup = (ViewGroup)localBaseFragment.actionBar.getParent();
          if (localViewGroup != null) {
            localViewGroup.removeView(localBaseFragment.actionBar);
          }
        }
        if (localBaseFragment.fragmentView != null)
        {
          localViewGroup = (ViewGroup)localBaseFragment.fragmentView.getParent();
          if (localViewGroup != null)
          {
            localBaseFragment.onRemoveFromParent();
            localViewGroup.removeView(localBaseFragment.fragmentView);
          }
        }
      }
      this.fragmentsStack.add(paramBaseFragment);
    }
    for (;;)
    {
      bool = true;
      break;
      this.fragmentsStack.add(paramInt, paramBaseFragment);
    }
  }
  
  public void animateThemedValues(Theme.ThemeInfo paramThemeInfo)
  {
    if ((this.transitionAnimationInProgress) || (this.startedTracking))
    {
      this.animateThemeAfterAnimation = true;
      this.animateSetThemeAfterAnimation = paramThemeInfo;
    }
    for (;;)
    {
      return;
      if (this.themeAnimatorSet != null)
      {
        this.themeAnimatorSet.cancel();
        this.themeAnimatorSet = null;
      }
      Object localObject = getLastFragment();
      if (localObject != null)
      {
        this.themeAnimatorDescriptions = ((BaseFragment)localObject).getThemeDescriptions();
        this.animateStartColors = new int[this.themeAnimatorDescriptions.length];
        for (int i = 0; i < this.themeAnimatorDescriptions.length; i++)
        {
          this.animateStartColors[i] = this.themeAnimatorDescriptions[i].getSetColor();
          localObject = this.themeAnimatorDescriptions[i].setDelegateDisabled();
          if ((this.themeAnimatorDelegate == null) && (localObject != null)) {
            this.themeAnimatorDelegate = ((ThemeDescription.ThemeDescriptionDelegate)localObject);
          }
        }
        Theme.applyTheme(paramThemeInfo, true);
        this.animateEndColors = new int[this.themeAnimatorDescriptions.length];
        for (i = 0; i < this.themeAnimatorDescriptions.length; i++) {
          this.animateEndColors[i] = this.themeAnimatorDescriptions[i].getSetColor();
        }
        this.themeAnimatorSet = new AnimatorSet();
        this.themeAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationCancel(Animator paramAnonymousAnimator)
          {
            if (paramAnonymousAnimator.equals(ActionBarLayout.this.themeAnimatorSet))
            {
              ActionBarLayout.access$2002(ActionBarLayout.this, null);
              ActionBarLayout.access$1902(ActionBarLayout.this, null);
              ActionBarLayout.access$2202(ActionBarLayout.this, null);
              ActionBarLayout.access$2302(ActionBarLayout.this, null);
            }
          }
          
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            if (paramAnonymousAnimator.equals(ActionBarLayout.this.themeAnimatorSet))
            {
              ActionBarLayout.access$2002(ActionBarLayout.this, null);
              ActionBarLayout.access$2102(ActionBarLayout.this, null);
              ActionBarLayout.access$1902(ActionBarLayout.this, null);
              ActionBarLayout.access$2202(ActionBarLayout.this, null);
              ActionBarLayout.access$2302(ActionBarLayout.this, null);
            }
          }
        });
        this.themeAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "themeAnimationValue", new float[] { 0.0F, 1.0F }) });
        this.themeAnimatorSet.setDuration(200L);
        this.themeAnimatorSet.start();
        for (i = 0; i < this.fragmentsStack.size() - 1; i++)
        {
          paramThemeInfo = (BaseFragment)this.fragmentsStack.get(i);
          paramThemeInfo.clearViews();
          paramThemeInfo.setParentLayout(this);
        }
      }
    }
  }
  
  public boolean checkTransitionAnimation()
  {
    if ((this.transitionAnimationInProgress) && (this.transitionAnimationStartTime < System.currentTimeMillis() - 1500L)) {
      onAnimationEndCheck(true);
    }
    return this.transitionAnimationInProgress;
  }
  
  public void closeLastFragment(boolean paramBoolean)
  {
    if (((this.delegate != null) && (!this.delegate.needCloseLastFragment(this))) || (checkTransitionAnimation()) || (this.fragmentsStack.isEmpty())) {}
    for (;;)
    {
      return;
      if (this.parentActivity.getCurrentFocus() != null) {
        AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
      }
      setInnerTranslationX(0.0F);
      int i;
      label84:
      final BaseFragment localBaseFragment;
      final Object localObject1;
      Object localObject2;
      Object localObject3;
      if ((paramBoolean) && (MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)))
      {
        i = 1;
        localBaseFragment = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1);
        localObject1 = null;
        if (this.fragmentsStack.size() > 1) {
          localObject1 = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 2);
        }
        if (localObject1 == null) {
          break label572;
        }
        localObject2 = this.containerView;
        this.containerView = this.containerViewBack;
        this.containerViewBack = ((LinearLayoutContainer)localObject2);
        this.containerView.setVisibility(0);
        ((BaseFragment)localObject1).setParentLayout(this);
        localObject3 = ((BaseFragment)localObject1).fragmentView;
        if (localObject3 != null) {
          break label495;
        }
        localObject2 = ((BaseFragment)localObject1).createView(this.parentActivity);
      }
      for (;;)
      {
        if ((((BaseFragment)localObject1).actionBar != null) && (((BaseFragment)localObject1).actionBar.getAddToContainer()))
        {
          if (this.removeActionBarExtraHeight) {
            ((BaseFragment)localObject1).actionBar.setOccupyStatusBar(false);
          }
          localObject3 = (ViewGroup)((BaseFragment)localObject1).actionBar.getParent();
          if (localObject3 != null) {
            ((ViewGroup)localObject3).removeView(((BaseFragment)localObject1).actionBar);
          }
          this.containerView.addView(((BaseFragment)localObject1).actionBar);
          ((BaseFragment)localObject1).actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
        }
        this.containerView.addView((View)localObject2);
        localObject3 = ((View)localObject2).getLayoutParams();
        ((ViewGroup.LayoutParams)localObject3).width = -1;
        ((ViewGroup.LayoutParams)localObject3).height = -1;
        ((View)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject3);
        ((BaseFragment)localObject1).onTransitionAnimationStart(true, true);
        localBaseFragment.onTransitionAnimationStart(false, false);
        ((BaseFragment)localObject1).onResume();
        this.currentActionBar = ((BaseFragment)localObject1).actionBar;
        if ((!((BaseFragment)localObject1).hasOwnBackground) && (((View)localObject2).getBackground() == null)) {
          ((View)localObject2).setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        if (i == 0) {
          closeLastFragmentInternalRemoveOld(localBaseFragment);
        }
        if (i == 0) {
          break label551;
        }
        this.transitionAnimationStartTime = System.currentTimeMillis();
        this.transitionAnimationInProgress = true;
        this.onCloseAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            ActionBarLayout.this.closeLastFragmentInternalRemoveOld(localBaseFragment);
            ActionBarLayout.this.containerViewBack.setTranslationX(0.0F);
            localBaseFragment.onTransitionAnimationEnd(false, false);
            localObject1.onTransitionAnimationEnd(true, true);
            localObject1.onBecomeFullyVisible();
          }
        };
        localObject1 = localBaseFragment.onCustomTransitionAnimation(false, new Runnable()
        {
          public void run()
          {
            ActionBarLayout.this.onAnimationEndCheck(false);
          }
        });
        if (localObject1 != null) {
          break label542;
        }
        if ((!this.containerView.isKeyboardVisible) && (!this.containerViewBack.isKeyboardVisible)) {
          break label533;
        }
        this.waitingForKeyboardCloseRunnable = new Runnable()
        {
          public void run()
          {
            if (ActionBarLayout.this.waitingForKeyboardCloseRunnable != this) {}
            for (;;)
            {
              return;
              ActionBarLayout.access$102(ActionBarLayout.this, null);
              ActionBarLayout.this.startLayoutAnimation(false, true);
            }
          }
        };
        AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200L);
        break;
        i = 0;
        break label84;
        label495:
        ViewGroup localViewGroup = (ViewGroup)((View)localObject3).getParent();
        localObject2 = localObject3;
        if (localViewGroup != null)
        {
          ((BaseFragment)localObject1).onRemoveFromParent();
          localViewGroup.removeView((View)localObject3);
          localObject2 = localObject3;
        }
      }
      label533:
      startLayoutAnimation(false, true);
      continue;
      label542:
      this.currentAnimation = ((AnimatorSet)localObject1);
      continue;
      label551:
      localBaseFragment.onTransitionAnimationEnd(false, false);
      ((BaseFragment)localObject1).onTransitionAnimationEnd(true, true);
      ((BaseFragment)localObject1).onBecomeFullyVisible();
      continue;
      label572:
      if (this.useAlphaAnimations)
      {
        this.transitionAnimationStartTime = System.currentTimeMillis();
        this.transitionAnimationInProgress = true;
        this.onCloseAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            ActionBarLayout.this.removeFragmentFromStackInternal(localBaseFragment);
            ActionBarLayout.this.setVisibility(8);
            if (ActionBarLayout.this.backgroundView != null) {
              ActionBarLayout.this.backgroundView.setVisibility(8);
            }
            if (ActionBarLayout.this.drawerLayoutContainer != null) {
              ActionBarLayout.this.drawerLayoutContainer.setAllowOpenDrawer(true, false);
            }
          }
        };
        localObject1 = new ArrayList();
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this, "alpha", new float[] { 1.0F, 0.0F }));
        if (this.backgroundView != null) {
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.backgroundView, "alpha", new float[] { 1.0F, 0.0F }));
        }
        this.currentAnimation = new AnimatorSet();
        this.currentAnimation.playTogether((Collection)localObject1);
        this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
        this.currentAnimation.setDuration(200L);
        this.currentAnimation.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            ActionBarLayout.this.onAnimationEndCheck(false);
          }
          
          public void onAnimationStart(Animator paramAnonymousAnimator)
          {
            ActionBarLayout.access$602(ActionBarLayout.this, System.currentTimeMillis());
          }
        });
        this.currentAnimation.start();
      }
      else
      {
        removeFragmentFromStackInternal(localBaseFragment);
        setVisibility(8);
        if (this.backgroundView != null) {
          this.backgroundView.setVisibility(8);
        }
      }
    }
  }
  
  public void dismissDialogs()
  {
    if (!this.fragmentsStack.isEmpty()) {
      ((BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1)).dismissCurrentDialig();
    }
  }
  
  public boolean dispatchKeyEventPreIme(KeyEvent paramKeyEvent)
  {
    boolean bool1 = true;
    boolean bool2;
    if ((paramKeyEvent != null) && (paramKeyEvent.getKeyCode() == 4) && (paramKeyEvent.getAction() == 1)) {
      if (this.delegate != null)
      {
        bool2 = bool1;
        if (this.delegate.onPreIme()) {}
      }
      else
      {
        if (!super.dispatchKeyEventPreIme(paramKeyEvent)) {
          break label55;
        }
        bool2 = bool1;
      }
    }
    for (;;)
    {
      return bool2;
      label55:
      bool2 = false;
      continue;
      bool2 = super.dispatchKeyEventPreIme(paramKeyEvent);
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    int i = getWidth() - getPaddingLeft() - getPaddingRight();
    int j = (int)this.innerTranslationX + getPaddingRight();
    int k = getPaddingLeft();
    int m = i + getPaddingLeft();
    int n;
    boolean bool;
    float f1;
    if (paramView == this.containerViewBack)
    {
      n = j;
      m = paramCanvas.save();
      if (!this.transitionAnimationInProgress) {
        paramCanvas.clipRect(k, 0, n, getHeight());
      }
      bool = super.drawChild(paramCanvas, paramView, paramLong);
      paramCanvas.restoreToCount(m);
      if (j != 0)
      {
        if (paramView != this.containerView) {
          break label205;
        }
        f1 = Math.max(0.0F, Math.min((i - j) / AndroidUtilities.dp(20.0F), 1.0F));
        layerShadowDrawable.setBounds(j - layerShadowDrawable.getIntrinsicWidth(), paramView.getTop(), j, paramView.getBottom());
        layerShadowDrawable.setAlpha((int)(255.0F * f1));
        layerShadowDrawable.draw(paramCanvas);
      }
    }
    for (;;)
    {
      return bool;
      n = m;
      if (paramView != this.containerView) {
        break;
      }
      k = j;
      n = m;
      break;
      label205:
      if (paramView == this.containerViewBack)
      {
        float f2 = Math.min(0.8F, (i - j) / i);
        f1 = f2;
        if (f2 < 0.0F) {
          f1 = 0.0F;
        }
        scrimPaint.setColor((int)(153.0F * f1) << 24);
        paramCanvas.drawRect(k, 0.0F, n, getHeight(), scrimPaint);
      }
    }
  }
  
  public void drawHeaderShadow(Canvas paramCanvas, int paramInt)
  {
    if (headerShadowDrawable != null)
    {
      headerShadowDrawable.setBounds(0, paramInt, getMeasuredWidth(), headerShadowDrawable.getIntrinsicHeight() + paramInt);
      headerShadowDrawable.draw(paramCanvas);
    }
  }
  
  public boolean extendActionMode(Menu paramMenu)
  {
    if ((!this.fragmentsStack.isEmpty()) && (((BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1)).extendActionMode(paramMenu))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public DrawerLayoutContainer getDrawerLayoutContainer()
  {
    return this.drawerLayoutContainer;
  }
  
  @Keep
  public float getInnerTranslationX()
  {
    return this.innerTranslationX;
  }
  
  public BaseFragment getLastFragment()
  {
    if (this.fragmentsStack.isEmpty()) {}
    for (BaseFragment localBaseFragment = null;; localBaseFragment = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1)) {
      return localBaseFragment;
    }
  }
  
  @Keep
  public float getThemeAnimationValue()
  {
    return this.themeAnimationValue;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void init(ArrayList<BaseFragment> paramArrayList)
  {
    this.fragmentsStack = paramArrayList;
    this.containerViewBack = new LinearLayoutContainer(this.parentActivity);
    addView(this.containerViewBack);
    paramArrayList = (FrameLayout.LayoutParams)this.containerViewBack.getLayoutParams();
    paramArrayList.width = -1;
    paramArrayList.height = -1;
    paramArrayList.gravity = 51;
    this.containerViewBack.setLayoutParams(paramArrayList);
    this.containerView = new LinearLayoutContainer(this.parentActivity);
    addView(this.containerView);
    paramArrayList = (FrameLayout.LayoutParams)this.containerView.getLayoutParams();
    paramArrayList.width = -1;
    paramArrayList.height = -1;
    paramArrayList.gravity = 51;
    this.containerView.setLayoutParams(paramArrayList);
    paramArrayList = this.fragmentsStack.iterator();
    while (paramArrayList.hasNext()) {
      ((BaseFragment)paramArrayList.next()).setParentLayout(this);
    }
  }
  
  public void onActionModeFinished(Object paramObject)
  {
    if (this.currentActionBar != null) {
      this.currentActionBar.setVisibility(0);
    }
    this.inActionMode = false;
  }
  
  public void onActionModeStarted(Object paramObject)
  {
    if (this.currentActionBar != null) {
      this.currentActionBar.setVisibility(8);
    }
    this.inActionMode = true;
  }
  
  public void onBackPressed()
  {
    if ((this.startedTracking) || (checkTransitionAnimation()) || (this.fragmentsStack.isEmpty())) {}
    for (;;)
    {
      return;
      if ((this.currentActionBar != null) && (this.currentActionBar.isSearchFieldVisible)) {
        this.currentActionBar.closeSearchField();
      } else if ((((BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onBackPressed()) && (!this.fragmentsStack.isEmpty())) {
        closeLastFragment(true);
      }
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (!this.fragmentsStack.isEmpty())
    {
      BaseFragment localBaseFragment = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1);
      localBaseFragment.onConfigurationChanged(paramConfiguration);
      if ((localBaseFragment.visibleDialog instanceof BottomSheet)) {
        ((BottomSheet)localBaseFragment.visibleDialog).onConfigurationChanged(paramConfiguration);
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.animationInProgress) || (checkTransitionAnimation()) || (onTouchEvent(paramMotionEvent))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 82) && (!checkTransitionAnimation()) && (!this.startedTracking) && (this.currentActionBar != null)) {
      this.currentActionBar.onMenuButtonPressed();
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  public void onLowMemory()
  {
    Iterator localIterator = this.fragmentsStack.iterator();
    while (localIterator.hasNext()) {
      ((BaseFragment)localIterator.next()).onLowMemory();
    }
  }
  
  public void onPause()
  {
    if (!this.fragmentsStack.isEmpty()) {
      ((BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onPause();
    }
  }
  
  public void onResume()
  {
    if (this.transitionAnimationInProgress)
    {
      if (this.currentAnimation != null)
      {
        this.currentAnimation.cancel();
        this.currentAnimation = null;
      }
      if (this.onCloseAnimationEndRunnable == null) {
        break label70;
      }
      onCloseAnimationEnd();
    }
    for (;;)
    {
      if (!this.fragmentsStack.isEmpty()) {
        ((BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onResume();
      }
      return;
      label70:
      if (this.onOpenAnimationEndRunnable != null) {
        onOpenAnimationEnd();
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    final boolean bool;
    if ((!checkTransitionAnimation()) && (!this.inActionMode) && (!this.animationInProgress)) {
      if (this.fragmentsStack.size() > 1)
      {
        if ((paramMotionEvent == null) || (paramMotionEvent.getAction() != 0) || (this.startedTracking) || (this.maybeStartTracking)) {
          break label140;
        }
        if (!((BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1)).swipeBackEnabled) {
          bool = false;
        }
      }
    }
    for (;;)
    {
      return bool;
      this.startedTrackingPointerId = paramMotionEvent.getPointerId(0);
      this.maybeStartTracking = true;
      this.startedTrackingX = ((int)paramMotionEvent.getX());
      this.startedTrackingY = ((int)paramMotionEvent.getY());
      if (this.velocityTracker != null) {
        this.velocityTracker.clear();
      }
      for (;;)
      {
        bool = this.startedTracking;
        break;
        label140:
        if ((paramMotionEvent != null) && (paramMotionEvent.getAction() == 2) && (paramMotionEvent.getPointerId(0) == this.startedTrackingPointerId))
        {
          if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
          }
          int i = Math.max(0, (int)(paramMotionEvent.getX() - this.startedTrackingX));
          int j = Math.abs((int)paramMotionEvent.getY() - this.startedTrackingY);
          this.velocityTracker.addMovement(paramMotionEvent);
          if ((this.maybeStartTracking) && (!this.startedTracking) && (i >= AndroidUtilities.getPixelsInCM(0.4F, true)) && (Math.abs(i) / 3 > j))
          {
            prepareForMoving(paramMotionEvent);
          }
          else if (this.startedTracking)
          {
            if (!this.beginTrackingSent)
            {
              if (this.parentActivity.getCurrentFocus() != null) {
                AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
              }
              ((BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1)).onBeginSlide();
              this.beginTrackingSent = true;
            }
            this.containerView.setTranslationX(i);
            setInnerTranslationX(i);
          }
        }
        else if ((paramMotionEvent != null) && (paramMotionEvent.getPointerId(0) == this.startedTrackingPointerId) && ((paramMotionEvent.getAction() == 3) || (paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 6)))
        {
          if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
          }
          this.velocityTracker.computeCurrentVelocity(1000);
          float f1;
          float f2;
          if ((!this.startedTracking) && (((BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1)).swipeBackEnabled))
          {
            f1 = this.velocityTracker.getXVelocity();
            f2 = this.velocityTracker.getYVelocity();
            if ((f1 >= 3500.0F) && (f1 > Math.abs(f2)))
            {
              prepareForMoving(paramMotionEvent);
              if (!this.beginTrackingSent)
              {
                if (((Activity)getContext()).getCurrentFocus() != null) {
                  AndroidUtilities.hideKeyboard(((Activity)getContext()).getCurrentFocus());
                }
                this.beginTrackingSent = true;
              }
            }
          }
          if (this.startedTracking)
          {
            f1 = this.containerView.getX();
            paramMotionEvent = new AnimatorSet();
            f2 = this.velocityTracker.getXVelocity();
            float f3 = this.velocityTracker.getYVelocity();
            if ((f1 < this.containerView.getMeasuredWidth() / 3.0F) && ((f2 < 3500.0F) || (f2 < f3)))
            {
              bool = true;
              label599:
              if (bool) {
                break label751;
              }
              f1 = this.containerView.getMeasuredWidth() - f1;
              paramMotionEvent.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "translationX", new float[] { this.containerView.getMeasuredWidth() }), ObjectAnimator.ofFloat(this, "innerTranslationX", new float[] { this.containerView.getMeasuredWidth() }) });
              label675:
              paramMotionEvent.setDuration(Math.max((int)(200.0F / this.containerView.getMeasuredWidth() * f1), 50));
              paramMotionEvent.addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymousAnimator)
                {
                  ActionBarLayout.this.onSlideAnimationEnd(bool);
                }
              });
              paramMotionEvent.start();
              this.animationInProgress = true;
            }
          }
          for (;;)
          {
            if (this.velocityTracker == null) {
              break label810;
            }
            this.velocityTracker.recycle();
            this.velocityTracker = null;
            break;
            bool = false;
            break label599;
            label751:
            paramMotionEvent.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "translationX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this, "innerTranslationX", new float[] { 0.0F }) });
            break label675;
            this.maybeStartTracking = false;
            this.startedTracking = false;
          }
        }
        else
        {
          label810:
          if (paramMotionEvent == null)
          {
            this.maybeStartTracking = false;
            this.startedTracking = false;
            if (this.velocityTracker != null)
            {
              this.velocityTracker.recycle();
              this.velocityTracker = null;
            }
          }
        }
      }
      bool = false;
    }
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment)
  {
    return presentFragment(paramBaseFragment, false, false, true);
  }
  
  public boolean presentFragment(BaseFragment paramBaseFragment, boolean paramBoolean)
  {
    return presentFragment(paramBaseFragment, paramBoolean, false, true);
  }
  
  public boolean presentFragment(final BaseFragment paramBaseFragment, final boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if ((checkTransitionAnimation()) || ((this.delegate != null) && (paramBoolean3) && (!this.delegate.needPresentFragment(paramBaseFragment, paramBoolean1, paramBoolean2, this))) || (!paramBaseFragment.onFragmentCreate()))
    {
      paramBoolean1 = false;
      return paramBoolean1;
    }
    if (this.parentActivity.getCurrentFocus() != null) {
      AndroidUtilities.hideKeyboard(this.parentActivity.getCurrentFocus());
    }
    int i;
    label88:
    final Object localObject1;
    label119:
    Object localObject2;
    Object localObject3;
    if ((!paramBoolean2) && (MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)))
    {
      i = 1;
      if (this.fragmentsStack.isEmpty()) {
        break label594;
      }
      localObject1 = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1);
      paramBaseFragment.setParentLayout(this);
      localObject2 = paramBaseFragment.fragmentView;
      if (localObject2 != null) {
        break label600;
      }
      localObject3 = paramBaseFragment.createView(this.parentActivity);
      label145:
      if ((paramBaseFragment.actionBar != null) && (paramBaseFragment.actionBar.getAddToContainer()))
      {
        if (this.removeActionBarExtraHeight) {
          paramBaseFragment.actionBar.setOccupyStatusBar(false);
        }
        localObject2 = (ViewGroup)paramBaseFragment.actionBar.getParent();
        if (localObject2 != null) {
          ((ViewGroup)localObject2).removeView(paramBaseFragment.actionBar);
        }
        this.containerViewBack.addView(paramBaseFragment.actionBar);
        paramBaseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
      }
      this.containerViewBack.addView((View)localObject3);
      localObject2 = ((View)localObject3).getLayoutParams();
      ((ViewGroup.LayoutParams)localObject2).width = -1;
      ((ViewGroup.LayoutParams)localObject2).height = -1;
      ((View)localObject3).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      this.fragmentsStack.add(paramBaseFragment);
      paramBaseFragment.onResume();
      this.currentActionBar = paramBaseFragment.actionBar;
      if ((!paramBaseFragment.hasOwnBackground) && (((View)localObject3).getBackground() == null)) {
        ((View)localObject3).setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
      localObject3 = this.containerView;
      this.containerView = this.containerViewBack;
      this.containerViewBack = ((LinearLayoutContainer)localObject3);
      this.containerView.setVisibility(0);
      setInnerTranslationX(0.0F);
      bringChildToFront(this.containerView);
      if (i == 0)
      {
        presentFragmentInternalRemoveOld(paramBoolean1, (BaseFragment)localObject1);
        if (this.backgroundView != null) {
          this.backgroundView.setVisibility(0);
        }
      }
      if (i == 0) {
        break label820;
      }
      if ((!this.useAlphaAnimations) || (this.fragmentsStack.size() != 1)) {
        break label637;
      }
      presentFragmentInternalRemoveOld(paramBoolean1, (BaseFragment)localObject1);
      this.transitionAnimationStartTime = System.currentTimeMillis();
      this.transitionAnimationInProgress = true;
      this.onOpenAnimationEndRunnable = new Runnable()
      {
        public void run()
        {
          paramBaseFragment.onTransitionAnimationEnd(true, false);
          paramBaseFragment.onBecomeFullyVisible();
        }
      };
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this, "alpha", new float[] { 0.0F, 1.0F }));
      if (this.backgroundView != null)
      {
        this.backgroundView.setVisibility(0);
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.backgroundView, "alpha", new float[] { 0.0F, 1.0F }));
      }
      paramBaseFragment.onTransitionAnimationStart(true, false);
      this.currentAnimation = new AnimatorSet();
      this.currentAnimation.playTogether((Collection)localObject1);
      this.currentAnimation.setInterpolator(this.accelerateDecelerateInterpolator);
      this.currentAnimation.setDuration(200L);
      this.currentAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ActionBarLayout.this.onAnimationEndCheck(false);
        }
      });
      this.currentAnimation.start();
    }
    for (;;)
    {
      paramBoolean1 = true;
      break;
      i = 0;
      break label88;
      label594:
      localObject1 = null;
      break label119;
      label600:
      ViewGroup localViewGroup = (ViewGroup)((View)localObject2).getParent();
      localObject3 = localObject2;
      if (localViewGroup == null) {
        break label145;
      }
      paramBaseFragment.onRemoveFromParent();
      localViewGroup.removeView((View)localObject2);
      localObject3 = localObject2;
      break label145;
      label637:
      this.transitionAnimationStartTime = System.currentTimeMillis();
      this.transitionAnimationInProgress = true;
      this.onOpenAnimationEndRunnable = new Runnable()
      {
        public void run()
        {
          ActionBarLayout.this.presentFragmentInternalRemoveOld(paramBoolean1, localObject1);
          paramBaseFragment.onTransitionAnimationEnd(true, false);
          paramBaseFragment.onBecomeFullyVisible();
          ActionBarLayout.this.containerView.setTranslationX(0.0F);
        }
      };
      paramBaseFragment.onTransitionAnimationStart(true, false);
      localObject1 = paramBaseFragment.onCustomTransitionAnimation(true, new Runnable()
      {
        public void run()
        {
          ActionBarLayout.this.onAnimationEndCheck(false);
        }
      });
      if (localObject1 == null)
      {
        this.containerView.setAlpha(0.0F);
        this.containerView.setTranslationX(48.0F);
        if ((this.containerView.isKeyboardVisible) || (this.containerViewBack.isKeyboardVisible))
        {
          this.waitingForKeyboardCloseRunnable = new Runnable()
          {
            public void run()
            {
              if (ActionBarLayout.this.waitingForKeyboardCloseRunnable != this) {}
              for (;;)
              {
                return;
                ActionBarLayout.access$102(ActionBarLayout.this, null);
                ActionBarLayout.this.startLayoutAnimation(true, true);
              }
            }
          };
          AndroidUtilities.runOnUIThread(this.waitingForKeyboardCloseRunnable, 200L);
        }
        else if (paramBaseFragment.needDelayOpenAnimation())
        {
          this.delayedOpenAnimationRunnable = new Runnable()
          {
            public void run()
            {
              if (ActionBarLayout.this.delayedOpenAnimationRunnable != this) {}
              for (;;)
              {
                return;
                ActionBarLayout.access$1402(ActionBarLayout.this, null);
                ActionBarLayout.this.startLayoutAnimation(true, true);
              }
            }
          };
          AndroidUtilities.runOnUIThread(this.delayedOpenAnimationRunnable, 200L);
        }
        else
        {
          startLayoutAnimation(true, true);
        }
      }
      else
      {
        this.containerView.setAlpha(1.0F);
        this.containerView.setTranslationX(0.0F);
        this.currentAnimation = ((AnimatorSet)localObject1);
        continue;
        label820:
        if (this.backgroundView != null)
        {
          this.backgroundView.setAlpha(1.0F);
          this.backgroundView.setVisibility(0);
        }
        paramBaseFragment.onTransitionAnimationStart(true, false);
        paramBaseFragment.onTransitionAnimationEnd(true, false);
        paramBaseFragment.onBecomeFullyVisible();
      }
    }
  }
  
  public void rebuildAllFragmentViews(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.transitionAnimationInProgress) || (this.startedTracking))
    {
      this.rebuildAfterAnimation = true;
      this.rebuildLastAfterAnimation = paramBoolean1;
      this.showLastAfterAnimation = paramBoolean2;
    }
    for (;;)
    {
      return;
      int i = 0;
      int j = this.fragmentsStack.size();
      if (paramBoolean1) {}
      for (int k = 0;; k = 1)
      {
        if (i >= j - k) {
          break label98;
        }
        ((BaseFragment)this.fragmentsStack.get(i)).clearViews();
        ((BaseFragment)this.fragmentsStack.get(i)).setParentLayout(this);
        i++;
        break;
      }
      label98:
      if (this.delegate != null) {
        this.delegate.onRebuildAllFragments(this, paramBoolean1);
      }
      if (paramBoolean2) {
        showLastFragment();
      }
    }
  }
  
  public void removeAllFragments()
  {
    for (int i = 0; this.fragmentsStack.size() > 0; i = i - 1 + 1) {
      removeFragmentFromStackInternal((BaseFragment)this.fragmentsStack.get(i));
    }
  }
  
  public void removeFragmentFromStack(int paramInt)
  {
    if (paramInt >= this.fragmentsStack.size()) {}
    for (;;)
    {
      return;
      removeFragmentFromStackInternal((BaseFragment)this.fragmentsStack.get(paramInt));
    }
  }
  
  public void removeFragmentFromStack(BaseFragment paramBaseFragment)
  {
    if ((this.useAlphaAnimations) && (this.fragmentsStack.size() == 1) && (AndroidUtilities.isTablet())) {
      closeLastFragment(true);
    }
    for (;;)
    {
      return;
      removeFragmentFromStackInternal(paramBaseFragment);
    }
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    onTouchEvent(null);
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void resumeDelayedFragmentAnimation()
  {
    if (this.delayedOpenAnimationRunnable == null) {}
    for (;;)
    {
      return;
      AndroidUtilities.cancelRunOnUIThread(this.delayedOpenAnimationRunnable);
      this.delayedOpenAnimationRunnable.run();
      this.delayedOpenAnimationRunnable = null;
    }
  }
  
  public void setBackgroundView(View paramView)
  {
    this.backgroundView = paramView;
  }
  
  public void setDelegate(ActionBarLayoutDelegate paramActionBarLayoutDelegate)
  {
    this.delegate = paramActionBarLayoutDelegate;
  }
  
  public void setDrawerLayoutContainer(DrawerLayoutContainer paramDrawerLayoutContainer)
  {
    this.drawerLayoutContainer = paramDrawerLayoutContainer;
  }
  
  @Keep
  public void setInnerTranslationX(float paramFloat)
  {
    this.innerTranslationX = paramFloat;
    invalidate();
  }
  
  public void setRemoveActionBarExtraHeight(boolean paramBoolean)
  {
    this.removeActionBarExtraHeight = paramBoolean;
  }
  
  @Keep
  public void setThemeAnimationValue(float paramFloat)
  {
    this.themeAnimationValue = paramFloat;
    if (this.themeAnimatorDescriptions != null)
    {
      for (int i = 0; i < this.themeAnimatorDescriptions.length; i++)
      {
        int j = Color.red(this.animateEndColors[i]);
        int k = Color.green(this.animateEndColors[i]);
        int m = Color.blue(this.animateEndColors[i]);
        int n = Color.alpha(this.animateEndColors[i]);
        int i1 = Color.red(this.animateStartColors[i]);
        int i2 = Color.green(this.animateStartColors[i]);
        int i3 = Color.blue(this.animateStartColors[i]);
        int i4 = Color.alpha(this.animateStartColors[i]);
        i4 = Math.min(255, (int)(i4 + (n - i4) * paramFloat));
        i1 = Math.min(255, (int)(i1 + (j - i1) * paramFloat));
        i2 = Math.min(255, (int)(i2 + (k - i2) * paramFloat));
        m = Math.min(255, (int)(i3 + (m - i3) * paramFloat));
        this.themeAnimatorDescriptions[i].setColor(Color.argb(i4, i1, i2, m), false, false);
      }
      if (this.themeAnimatorDelegate != null) {
        this.themeAnimatorDelegate.didSetColor();
      }
    }
  }
  
  public void setTitleOverlayText(String paramString1, String paramString2, Runnable paramRunnable)
  {
    this.titleOverlayText = paramString1;
    this.subtitleOverlayText = paramString2;
    this.overlayAction = paramRunnable;
    for (int i = 0; i < this.fragmentsStack.size(); i++)
    {
      paramString1 = (BaseFragment)this.fragmentsStack.get(i);
      if (paramString1.actionBar != null) {
        paramString1.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, paramRunnable);
      }
    }
  }
  
  public void setUseAlphaAnimations(boolean paramBoolean)
  {
    this.useAlphaAnimations = paramBoolean;
  }
  
  public void showLastFragment()
  {
    if (this.fragmentsStack.isEmpty()) {
      return;
    }
    Object localObject1;
    for (int i = 0; i < this.fragmentsStack.size() - 1; i++)
    {
      localObject1 = (BaseFragment)this.fragmentsStack.get(i);
      if ((((BaseFragment)localObject1).actionBar != null) && (((BaseFragment)localObject1).actionBar.getAddToContainer()))
      {
        localObject2 = (ViewGroup)((BaseFragment)localObject1).actionBar.getParent();
        if (localObject2 != null) {
          ((ViewGroup)localObject2).removeView(((BaseFragment)localObject1).actionBar);
        }
      }
      if (((BaseFragment)localObject1).fragmentView != null)
      {
        localObject2 = (ViewGroup)((BaseFragment)localObject1).fragmentView.getParent();
        if (localObject2 != null)
        {
          ((BaseFragment)localObject1).onPause();
          ((BaseFragment)localObject1).onRemoveFromParent();
          ((ViewGroup)localObject2).removeView(((BaseFragment)localObject1).fragmentView);
        }
      }
    }
    BaseFragment localBaseFragment = (BaseFragment)this.fragmentsStack.get(this.fragmentsStack.size() - 1);
    localBaseFragment.setParentLayout(this);
    Object localObject2 = localBaseFragment.fragmentView;
    if (localObject2 == null) {
      localObject1 = localBaseFragment.createView(this.parentActivity);
    }
    for (;;)
    {
      if ((localBaseFragment.actionBar != null) && (localBaseFragment.actionBar.getAddToContainer()))
      {
        if (this.removeActionBarExtraHeight) {
          localBaseFragment.actionBar.setOccupyStatusBar(false);
        }
        localObject2 = (ViewGroup)localBaseFragment.actionBar.getParent();
        if (localObject2 != null) {
          ((ViewGroup)localObject2).removeView(localBaseFragment.actionBar);
        }
        this.containerView.addView(localBaseFragment.actionBar);
        localBaseFragment.actionBar.setTitleOverlayText(this.titleOverlayText, this.subtitleOverlayText, this.overlayAction);
      }
      this.containerView.addView((View)localObject1, LayoutHelper.createLinear(-1, -1));
      localBaseFragment.onResume();
      this.currentActionBar = localBaseFragment.actionBar;
      if ((localBaseFragment.hasOwnBackground) || (((View)localObject1).getBackground() != null)) {
        break;
      }
      ((View)localObject1).setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      break;
      ViewGroup localViewGroup = (ViewGroup)((View)localObject2).getParent();
      localObject1 = localObject2;
      if (localViewGroup != null)
      {
        localBaseFragment.onRemoveFromParent();
        localViewGroup.removeView((View)localObject2);
        localObject1 = localObject2;
      }
    }
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    if (this.parentActivity == null) {}
    for (;;)
    {
      return;
      if (this.transitionAnimationInProgress)
      {
        if (this.currentAnimation != null)
        {
          this.currentAnimation.cancel();
          this.currentAnimation = null;
        }
        if (this.onCloseAnimationEndRunnable != null) {
          onCloseAnimationEnd();
        }
        for (;;)
        {
          this.containerView.invalidate();
          if (paramIntent == null) {
            break;
          }
          this.parentActivity.startActivityForResult(paramIntent, paramInt);
          break;
          if (this.onOpenAnimationEndRunnable != null) {
            onOpenAnimationEnd();
          }
        }
      }
      if (paramIntent != null) {
        this.parentActivity.startActivityForResult(paramIntent, paramInt);
      }
    }
  }
  
  public static abstract interface ActionBarLayoutDelegate
  {
    public abstract boolean needAddFragmentToStack(BaseFragment paramBaseFragment, ActionBarLayout paramActionBarLayout);
    
    public abstract boolean needCloseLastFragment(ActionBarLayout paramActionBarLayout);
    
    public abstract boolean needPresentFragment(BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2, ActionBarLayout paramActionBarLayout);
    
    public abstract boolean onPreIme();
    
    public abstract void onRebuildAllFragments(ActionBarLayout paramActionBarLayout, boolean paramBoolean);
  }
  
  public class LinearLayoutContainer
    extends LinearLayout
  {
    private boolean isKeyboardVisible;
    private Rect rect = new Rect();
    
    public LinearLayoutContainer(Context paramContext)
    {
      super();
      setOrientation(1);
    }
    
    protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
    {
      boolean bool1;
      if ((paramView instanceof ActionBar)) {
        bool1 = super.drawChild(paramCanvas, paramView, paramLong);
      }
      for (;;)
      {
        return bool1;
        int i = 0;
        int j = getChildCount();
        int k = 0;
        int m = i;
        if (k < j)
        {
          View localView = getChildAt(k);
          if (localView == paramView) {}
          while ((!(localView instanceof ActionBar)) || (localView.getVisibility() != 0))
          {
            k++;
            break;
          }
          m = i;
          if (((ActionBar)localView).getCastShadows()) {
            m = localView.getMeasuredHeight();
          }
        }
        boolean bool2 = super.drawChild(paramCanvas, paramView, paramLong);
        bool1 = bool2;
        if (m != 0)
        {
          bool1 = bool2;
          if (ActionBarLayout.headerShadowDrawable != null)
          {
            ActionBarLayout.headerShadowDrawable.setBounds(0, m, getMeasuredWidth(), ActionBarLayout.headerShadowDrawable.getIntrinsicHeight() + m);
            ActionBarLayout.headerShadowDrawable.draw(paramCanvas);
            bool1 = bool2;
          }
        }
      }
    }
    
    public boolean hasOverlappingRendering()
    {
      return false;
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      boolean bool = false;
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      View localView = getRootView();
      getWindowVisibleDisplayFrame(this.rect);
      paramInt2 = localView.getHeight();
      if (this.rect.top != 0) {}
      for (paramInt1 = AndroidUtilities.statusBarHeight;; paramInt1 = 0)
      {
        paramBoolean = bool;
        if (paramInt2 - paramInt1 - AndroidUtilities.getViewInset(localView) - (this.rect.bottom - this.rect.top) > 0) {
          paramBoolean = true;
        }
        this.isKeyboardVisible = paramBoolean;
        if ((ActionBarLayout.this.waitingForKeyboardCloseRunnable != null) && (!ActionBarLayout.this.containerView.isKeyboardVisible) && (!ActionBarLayout.this.containerViewBack.isKeyboardVisible))
        {
          AndroidUtilities.cancelRunOnUIThread(ActionBarLayout.this.waitingForKeyboardCloseRunnable);
          ActionBarLayout.this.waitingForKeyboardCloseRunnable.run();
          ActionBarLayout.access$102(ActionBarLayout.this, null);
        }
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/ActionBarLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */