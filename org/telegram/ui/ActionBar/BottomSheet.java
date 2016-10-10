package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.LayoutHelper;

public class BottomSheet
  extends Dialog
{
  protected static int backgroundPaddingLeft;
  protected static int backgroundPaddingTop;
  private AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
  private boolean allowCustomAnimation = true;
  private boolean applyBottomPadding = true;
  private boolean applyTopPadding = true;
  protected ColorDrawable backDrawable = new ColorDrawable(-16777216);
  protected Paint ciclePaint = new Paint(1);
  protected ContainerView container;
  protected ViewGroup containerView;
  protected AnimatorSet currentSheetAnimation;
  private View customView;
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private BottomSheetDelegateInterface delegate;
  private boolean dismissed;
  private boolean focusable;
  protected boolean fullWidth;
  private int[] itemIcons;
  private ArrayList<BottomSheetCell> itemViews = new ArrayList();
  private CharSequence[] items;
  private WindowInsets lastInsets;
  private int layoutCount;
  private DialogInterface.OnClickListener onClickListener;
  private Drawable shadowDrawable;
  private Runnable startAnimationRunnable;
  private int tag;
  private CharSequence title;
  private int touchSlop;
  private boolean useFastDismiss;
  
  public BottomSheet(Context paramContext, boolean paramBoolean)
  {
    super(paramContext, 2131296270);
    if (Build.VERSION.SDK_INT >= 21) {
      getWindow().addFlags(-2147417856);
    }
    this.touchSlop = ViewConfiguration.get(paramContext).getScaledTouchSlop();
    Rect localRect = new Rect();
    this.shadowDrawable = paramContext.getResources().getDrawable(2130837983);
    this.shadowDrawable.getPadding(localRect);
    backgroundPaddingLeft = localRect.left;
    backgroundPaddingTop = localRect.top;
    this.container = new ContainerView(getContext());
    this.container.setBackgroundDrawable(this.backDrawable);
    this.focusable = paramBoolean;
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.container.setFitsSystemWindows(true);
      this.container.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View paramAnonymousView, WindowInsets paramAnonymousWindowInsets)
        {
          BottomSheet.access$502(BottomSheet.this, paramAnonymousWindowInsets);
          paramAnonymousView.requestLayout();
          return paramAnonymousWindowInsets.consumeSystemWindowInsets();
        }
      });
      this.container.setSystemUiVisibility(1280);
    }
    this.ciclePaint.setColor(-1);
    this.backDrawable.setAlpha(0);
  }
  
  private void cancelSheetAnimation()
  {
    if (this.currentSheetAnimation != null)
    {
      this.currentSheetAnimation.cancel();
      this.currentSheetAnimation = null;
    }
  }
  
  private void startOpenAnimation()
  {
    if (this.dismissed) {}
    do
    {
      return;
      this.containerView.setVisibility(0);
    } while (onCustomOpenAnimation());
    if (Build.VERSION.SDK_INT >= 20) {
      this.container.setLayerType(2, null);
    }
    this.containerView.setTranslationY(this.containerView.getMeasuredHeight());
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[] { 51 }) });
    localAnimatorSet.setDuration(200L);
    localAnimatorSet.setStartDelay(20L);
    localAnimatorSet.setInterpolator(new DecelerateInterpolator());
    localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnonymousAnimator))) {
          BottomSheet.this.currentSheetAnimation = null;
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnonymousAnimator)))
        {
          BottomSheet.this.currentSheetAnimation = null;
          if (BottomSheet.this.delegate != null) {
            BottomSheet.this.delegate.onOpenAnimationEnd();
          }
          BottomSheet.this.container.setLayerType(0, null);
        }
      }
    });
    localAnimatorSet.start();
    this.currentSheetAnimation = localAnimatorSet;
  }
  
  protected boolean canDismissWithSwipe()
  {
    return true;
  }
  
  protected boolean canDismissWithTouchOutside()
  {
    return true;
  }
  
  public void dismiss()
  {
    if (this.dismissed) {}
    do
    {
      return;
      this.dismissed = true;
      cancelSheetAnimation();
    } while ((this.allowCustomAnimation) && (onCustomCloseAnimation()));
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "translationY", new float[] { this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0F) }), ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[] { 0 }) });
    if (this.useFastDismiss)
    {
      int i = this.containerView.getMeasuredHeight();
      localAnimatorSet.setDuration(Math.max(60, (int)(180.0F * (i - this.containerView.getTranslationY()) / i)));
      this.useFastDismiss = false;
    }
    for (;;)
    {
      localAnimatorSet.setInterpolator(new AccelerateInterpolator());
      localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnonymousAnimator))) {
            BottomSheet.this.currentSheetAnimation = null;
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnonymousAnimator)))
          {
            BottomSheet.this.currentSheetAnimation = null;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                try
                {
                  BottomSheet.this.dismissInternal();
                  return;
                }
                catch (Exception localException)
                {
                  FileLog.e("tmessages", localException);
                }
              }
            });
          }
        }
      });
      localAnimatorSet.start();
      this.currentSheetAnimation = localAnimatorSet;
      return;
      localAnimatorSet.setDuration(180L);
    }
  }
  
  public void dismissInternal()
  {
    try
    {
      super.dismiss();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  public void dismissWithButtonClick(final int paramInt)
  {
    if (this.dismissed) {
      return;
    }
    this.dismissed = true;
    cancelSheetAnimation();
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.containerView, "translationY", new float[] { this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0F) }), ObjectAnimator.ofInt(this.backDrawable, "alpha", new int[] { 0 }) });
    localAnimatorSet.setDuration(180L);
    localAnimatorSet.setInterpolator(new AccelerateInterpolator());
    localAnimatorSet.addListener(new AnimatorListenerAdapterProxy()
    {
      public void onAnimationCancel(Animator paramAnonymousAnimator)
      {
        if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnonymousAnimator))) {
          BottomSheet.this.currentSheetAnimation = null;
        }
      }
      
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        if ((BottomSheet.this.currentSheetAnimation != null) && (BottomSheet.this.currentSheetAnimation.equals(paramAnonymousAnimator)))
        {
          BottomSheet.this.currentSheetAnimation = null;
          if (BottomSheet.this.onClickListener != null) {
            BottomSheet.this.onClickListener.onClick(BottomSheet.this, paramInt);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              try
              {
                BottomSheet.this.dismiss();
                return;
              }
              catch (Exception localException)
              {
                FileLog.e("tmessages", localException);
              }
            }
          });
        }
      }
    });
    localAnimatorSet.start();
    this.currentSheetAnimation = localAnimatorSet;
  }
  
  public FrameLayout getContainer()
  {
    return this.container;
  }
  
  public ViewGroup getSheetContainer()
  {
    return this.containerView;
  }
  
  public int getTag()
  {
    return this.tag;
  }
  
  public boolean isDismissed()
  {
    return this.dismissed;
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
  }
  
  protected boolean onContainerTouchEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    paramBundle = getWindow();
    paramBundle.setWindowAnimations(2131296261);
    setContentView(this.container, new ViewGroup.LayoutParams(-1, -1));
    Object localObject;
    if (this.containerView == null)
    {
      this.containerView = new FrameLayout(getContext())
      {
        public boolean hasOverlappingRendering()
        {
          return false;
        }
      };
      this.containerView.setBackgroundDrawable(this.shadowDrawable);
      localObject = this.containerView;
      k = backgroundPaddingLeft;
      if (this.applyTopPadding)
      {
        i = AndroidUtilities.dp(8.0F);
        int m = backgroundPaddingTop;
        int n = backgroundPaddingLeft;
        if (!this.applyBottomPadding) {
          break label305;
        }
        j = AndroidUtilities.dp(8.0F);
        label117:
        ((ViewGroup)localObject).setPadding(k, m + i, n, j);
      }
    }
    else
    {
      if (Build.VERSION.SDK_INT >= 21) {
        this.containerView.setFitsSystemWindows(true);
      }
      this.containerView.setVisibility(4);
      this.container.addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
      if (this.customView == null) {
        break label310;
      }
      if (this.customView.getParent() != null) {
        ((ViewGroup)this.customView.getParent()).removeView(this.customView);
      }
      this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2, 51));
    }
    label305:
    label310:
    do
    {
      localObject = paramBundle.getAttributes();
      ((WindowManager.LayoutParams)localObject).width = -1;
      ((WindowManager.LayoutParams)localObject).gravity = 51;
      ((WindowManager.LayoutParams)localObject).dimAmount = 0.0F;
      ((WindowManager.LayoutParams)localObject).flags &= 0xFFFFFFFD;
      if (!this.focusable) {
        ((WindowManager.LayoutParams)localObject).flags |= 0x20000;
      }
      ((WindowManager.LayoutParams)localObject).height = -1;
      paramBundle.setAttributes((WindowManager.LayoutParams)localObject);
      return;
      i = 0;
      break;
      j = 0;
      break label117;
      i = 0;
      if (this.title != null)
      {
        localObject = new TextView(getContext());
        ((TextView)localObject).setLines(1);
        ((TextView)localObject).setSingleLine(true);
        ((TextView)localObject).setText(this.title);
        ((TextView)localObject).setTextColor(-9079435);
        ((TextView)localObject).setTextSize(1, 16.0F);
        ((TextView)localObject).setEllipsize(TextUtils.TruncateAt.MIDDLE);
        ((TextView)localObject).setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), AndroidUtilities.dp(8.0F));
        ((TextView)localObject).setGravity(16);
        this.containerView.addView((View)localObject, LayoutHelper.createFrame(-1, 48.0F));
        ((TextView)localObject).setOnTouchListener(new View.OnTouchListener()
        {
          public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
          {
            return true;
          }
        });
        i = 0 + 48;
      }
    } while (this.items == null);
    int k = 0;
    int j = i;
    int i = k;
    label458:
    CharSequence localCharSequence;
    if (i < this.items.length)
    {
      localObject = new BottomSheetCell(getContext(), 0);
      localCharSequence = this.items[i];
      if (this.itemIcons == null) {
        break label580;
      }
    }
    label580:
    for (k = this.itemIcons[i];; k = 0)
    {
      ((BottomSheetCell)localObject).setTextAndIcon(localCharSequence, k);
      this.containerView.addView((View)localObject, LayoutHelper.createFrame(-1, 48.0F, 51, 0.0F, j, 0.0F, 0.0F));
      j += 48;
      ((BottomSheetCell)localObject).setTag(Integer.valueOf(i));
      ((BottomSheetCell)localObject).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          BottomSheet.this.dismissWithButtonClick(((Integer)paramAnonymousView.getTag()).intValue());
        }
      });
      this.itemViews.add(localObject);
      i += 1;
      break label458;
      break;
    }
  }
  
  protected boolean onCustomCloseAnimation()
  {
    return false;
  }
  
  protected boolean onCustomLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return false;
  }
  
  protected boolean onCustomMeasure(View paramView, int paramInt1, int paramInt2)
  {
    return false;
  }
  
  protected boolean onCustomOpenAnimation()
  {
    return false;
  }
  
  public void setApplyBottomPadding(boolean paramBoolean)
  {
    this.applyBottomPadding = paramBoolean;
  }
  
  public void setApplyTopPadding(boolean paramBoolean)
  {
    this.applyTopPadding = paramBoolean;
  }
  
  public void setCustomView(View paramView)
  {
    this.customView = paramView;
  }
  
  public void setDelegate(BottomSheetDelegateInterface paramBottomSheetDelegateInterface)
  {
    this.delegate = paramBottomSheetDelegateInterface;
  }
  
  public void setItemText(int paramInt, CharSequence paramCharSequence)
  {
    if ((paramInt < 0) || (paramInt >= this.itemViews.size())) {
      return;
    }
    ((BottomSheetCell)this.itemViews.get(paramInt)).textView.setText(paramCharSequence);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.title = paramCharSequence;
  }
  
  public void show()
  {
    super.show();
    if (this.focusable) {
      getWindow().setSoftInputMode(16);
    }
    this.dismissed = false;
    cancelSheetAnimation();
    if (this.containerView.getMeasuredHeight() == 0) {
      this.containerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
    }
    this.backDrawable.setAlpha(0);
    if (Build.VERSION.SDK_INT >= 18)
    {
      this.layoutCount = 2;
      Runnable local5 = new Runnable()
      {
        public void run()
        {
          if ((BottomSheet.this.startAnimationRunnable != this) || (BottomSheet.this.dismissed)) {
            return;
          }
          BottomSheet.access$702(BottomSheet.this, null);
          BottomSheet.this.startOpenAnimation();
        }
      };
      this.startAnimationRunnable = local5;
      AndroidUtilities.runOnUIThread(local5, 150L);
      return;
    }
    startOpenAnimation();
  }
  
  public static class BottomSheetCell
    extends FrameLayout
  {
    private ImageView imageView;
    private TextView textView;
    
    public BottomSheetCell(Context paramContext, int paramInt)
    {
      super();
      setBackgroundResource(2130837796);
      setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), 0);
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      ImageView localImageView = this.imageView;
      int i;
      if (LocaleController.isRTL)
      {
        i = 5;
        addView(localImageView, LayoutHelper.createFrame(24, 24, i | 0x10));
        this.textView = new TextView(paramContext);
        this.textView.setLines(1);
        this.textView.setSingleLine(true);
        this.textView.setGravity(1);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        if (paramInt != 0) {
          break label189;
        }
        this.textView.setTextColor(-14606047);
        this.textView.setTextSize(1, 16.0F);
        paramContext = this.textView;
        if (!LocaleController.isRTL) {
          break label184;
        }
        paramInt = 5;
        addView(paramContext, LayoutHelper.createFrame(-2, -2, paramInt | 0x10));
      }
      label184:
      label189:
      while (paramInt != 1) {
        for (;;)
        {
          return;
          i = 3;
          break;
          paramInt = 3;
        }
      }
      this.textView.setGravity(17);
      this.textView.setTextColor(-14606047);
      this.textView.setTextSize(1, 14.0F);
      this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.textView, LayoutHelper.createFrame(-1, -1.0F));
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F), 1073741824));
    }
    
    public void setGravity(int paramInt)
    {
      this.textView.setGravity(paramInt);
    }
    
    public void setTextAndIcon(CharSequence paramCharSequence, int paramInt)
    {
      this.textView.setText(paramCharSequence);
      if (paramInt != 0)
      {
        this.imageView.setImageResource(paramInt);
        this.imageView.setVisibility(0);
        paramCharSequence = this.textView;
        if (LocaleController.isRTL)
        {
          paramInt = 0;
          if (!LocaleController.isRTL) {
            break label71;
          }
        }
        label71:
        for (int i = AndroidUtilities.dp(56.0F);; i = 0)
        {
          paramCharSequence.setPadding(paramInt, 0, i, 0);
          return;
          paramInt = AndroidUtilities.dp(56.0F);
          break;
        }
      }
      this.imageView.setVisibility(4);
      this.textView.setPadding(0, 0, 0, 0);
    }
    
    public void setTextColor(int paramInt)
    {
      this.textView.setTextColor(paramInt);
    }
  }
  
  public static class BottomSheetDelegate
    implements BottomSheet.BottomSheetDelegateInterface
  {
    public void onOpenAnimationEnd() {}
    
    public void onOpenAnimationStart() {}
  }
  
  public static abstract interface BottomSheetDelegateInterface
  {
    public abstract void onOpenAnimationEnd();
    
    public abstract void onOpenAnimationStart();
  }
  
  public static class Builder
  {
    private BottomSheet bottomSheet;
    
    public Builder(Context paramContext)
    {
      this.bottomSheet = new BottomSheet(paramContext, false);
    }
    
    public Builder(Context paramContext, boolean paramBoolean)
    {
      this.bottomSheet = new BottomSheet(paramContext, paramBoolean);
    }
    
    public BottomSheet create()
    {
      return this.bottomSheet;
    }
    
    public Builder setApplyBottomPadding(boolean paramBoolean)
    {
      BottomSheet.access$1902(this.bottomSheet, paramBoolean);
      return this;
    }
    
    public Builder setApplyTopPadding(boolean paramBoolean)
    {
      BottomSheet.access$1802(this.bottomSheet, paramBoolean);
      return this;
    }
    
    public Builder setCustomView(View paramView)
    {
      BottomSheet.access$1502(this.bottomSheet, paramView);
      return this;
    }
    
    public Builder setDelegate(BottomSheet.BottomSheetDelegate paramBottomSheetDelegate)
    {
      this.bottomSheet.setDelegate(paramBottomSheetDelegate);
      return this;
    }
    
    public Builder setItems(CharSequence[] paramArrayOfCharSequence, DialogInterface.OnClickListener paramOnClickListener)
    {
      BottomSheet.access$1302(this.bottomSheet, paramArrayOfCharSequence);
      BottomSheet.access$1102(this.bottomSheet, paramOnClickListener);
      return this;
    }
    
    public Builder setItems(CharSequence[] paramArrayOfCharSequence, int[] paramArrayOfInt, DialogInterface.OnClickListener paramOnClickListener)
    {
      BottomSheet.access$1302(this.bottomSheet, paramArrayOfCharSequence);
      BottomSheet.access$1402(this.bottomSheet, paramArrayOfInt);
      BottomSheet.access$1102(this.bottomSheet, paramOnClickListener);
      return this;
    }
    
    public Builder setTag(int paramInt)
    {
      BottomSheet.access$1702(this.bottomSheet, paramInt);
      return this;
    }
    
    public Builder setTitle(CharSequence paramCharSequence)
    {
      BottomSheet.access$1602(this.bottomSheet, paramCharSequence);
      return this;
    }
    
    public BottomSheet setUseFullWidth(boolean paramBoolean)
    {
      this.bottomSheet.fullWidth = paramBoolean;
      return this.bottomSheet;
    }
    
    public BottomSheet show()
    {
      this.bottomSheet.show();
      return this.bottomSheet;
    }
  }
  
  protected class ContainerView
    extends FrameLayout
    implements NestedScrollingParent
  {
    private AnimatorSet currentAnimation = null;
    private boolean maybeStartTracking = false;
    private NestedScrollingParentHelper nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    private boolean startedTracking = false;
    private int startedTrackingPointerId = -1;
    private int startedTrackingX;
    private int startedTrackingY;
    private VelocityTracker velocityTracker = null;
    
    public ContainerView(Context paramContext)
    {
      super();
    }
    
    private void cancelCurrentAnimation()
    {
      if (this.currentAnimation != null)
      {
        this.currentAnimation.cancel();
        this.currentAnimation = null;
      }
    }
    
    private void checkDismiss(float paramFloat1, float paramFloat2)
    {
      float f = BottomSheet.this.containerView.getTranslationY();
      if (((f < AndroidUtilities.getPixelsInCM(0.8F, false)) && ((paramFloat2 < 3500.0F) || (Math.abs(paramFloat2) < Math.abs(paramFloat1)))) || ((paramFloat2 < 0.0F) && (Math.abs(paramFloat2) >= 3500.0F))) {}
      for (int i = 1; i == 0; i = 0)
      {
        boolean bool = BottomSheet.this.allowCustomAnimation;
        BottomSheet.access$102(BottomSheet.this, false);
        BottomSheet.access$202(BottomSheet.this, true);
        BottomSheet.this.dismiss();
        BottomSheet.access$102(BottomSheet.this, bool);
        return;
      }
      this.currentAnimation = new AnimatorSet();
      this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(BottomSheet.this.containerView, "translationY", new float[] { 0.0F }) });
      this.currentAnimation.setDuration((int)(f / AndroidUtilities.getPixelsInCM(0.8F, false) * 150.0F));
      this.currentAnimation.setInterpolator(new DecelerateInterpolator());
      this.currentAnimation.addListener(new AnimatorListenerAdapterProxy()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((BottomSheet.ContainerView.this.currentAnimation != null) && (BottomSheet.ContainerView.this.currentAnimation.equals(paramAnonymousAnimator))) {
            BottomSheet.ContainerView.access$302(BottomSheet.ContainerView.this, null);
          }
        }
      });
      this.currentAnimation.start();
    }
    
    public int getNestedScrollAxes()
    {
      return this.nestedScrollingParentHelper.getNestedScrollAxes();
    }
    
    public boolean hasOverlappingRendering()
    {
      return false;
    }
    
    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      if (BottomSheet.this.canDismissWithSwipe()) {
        return onTouchEvent(paramMotionEvent);
      }
      return super.onInterceptTouchEvent(paramMotionEvent);
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      BottomSheet.access$610(BottomSheet.this);
      int i = paramInt1;
      int j = paramInt3;
      if (BottomSheet.this.containerView != null)
      {
        k = paramInt4 - paramInt2 - BottomSheet.this.containerView.getMeasuredHeight();
        i = paramInt1;
        j = paramInt3;
        if (BottomSheet.this.lastInsets != null)
        {
          i = paramInt1;
          j = paramInt3;
          if (Build.VERSION.SDK_INT >= 21)
          {
            i = paramInt1 + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
            j = paramInt3 + BottomSheet.this.lastInsets.getSystemWindowInsetLeft();
          }
        }
        paramInt1 = (j - i - BottomSheet.this.containerView.getMeasuredWidth()) / 2;
        BottomSheet.this.containerView.layout(paramInt1, k, BottomSheet.this.containerView.getMeasuredWidth() + paramInt1, BottomSheet.this.containerView.getMeasuredHeight() + k);
      }
      int m = getChildCount();
      int k = 0;
      if (k < m)
      {
        View localView = getChildAt(k);
        if ((localView.getVisibility() == 8) || (localView == BottomSheet.this.containerView)) {}
        while (BottomSheet.this.onCustomLayout(localView, i, paramInt2, j, paramInt4))
        {
          k += 1;
          break;
        }
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        int n = localView.getMeasuredWidth();
        int i1 = localView.getMeasuredHeight();
        paramInt3 = localLayoutParams.gravity;
        paramInt1 = paramInt3;
        if (paramInt3 == -1) {
          paramInt1 = 51;
        }
        switch (paramInt1 & 0x7 & 0x7)
        {
        default: 
          paramInt3 = localLayoutParams.leftMargin;
          label319:
          switch (paramInt1 & 0x70)
          {
          default: 
            paramInt1 = localLayoutParams.topMargin;
          }
          break;
        }
        for (;;)
        {
          localView.layout(paramInt3, paramInt1, paramInt3 + n, paramInt1 + i1);
          break;
          paramInt3 = (j - i - n) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
          break label319;
          paramInt3 = j - n - localLayoutParams.rightMargin;
          break label319;
          paramInt1 = localLayoutParams.topMargin;
          continue;
          paramInt1 = (paramInt4 - paramInt2 - i1) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
          continue;
          paramInt1 = paramInt4 - paramInt2 - i1 - localLayoutParams.bottomMargin;
        }
      }
      if ((BottomSheet.this.layoutCount == 0) && (BottomSheet.this.startAnimationRunnable != null))
      {
        AndroidUtilities.cancelRunOnUIThread(BottomSheet.this.startAnimationRunnable);
        BottomSheet.this.startAnimationRunnable.run();
        BottomSheet.access$702(BottomSheet.this, null);
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      int j = View.MeasureSpec.getSize(paramInt2);
      paramInt2 = j;
      paramInt1 = i;
      if (BottomSheet.this.lastInsets != null)
      {
        paramInt2 = j;
        paramInt1 = i;
        if (Build.VERSION.SDK_INT >= 21)
        {
          paramInt1 = i - (BottomSheet.this.lastInsets.getSystemWindowInsetRight() + BottomSheet.this.lastInsets.getSystemWindowInsetLeft());
          paramInt2 = j - BottomSheet.this.lastInsets.getSystemWindowInsetBottom();
        }
      }
      setMeasuredDimension(paramInt1, paramInt2);
      label167:
      label175:
      View localView;
      if (paramInt1 < paramInt2)
      {
        i = 1;
        if (BottomSheet.this.containerView != null)
        {
          if (BottomSheet.this.fullWidth) {
            break label275;
          }
          if (!AndroidUtilities.isTablet()) {
            break label222;
          }
          i = View.MeasureSpec.makeMeasureSpec((int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.8F) + BottomSheet.backgroundPaddingLeft * 2, 1073741824);
          BottomSheet.this.containerView.measure(i, View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
        }
        j = getChildCount();
        i = 0;
        if (i >= j) {
          return;
        }
        localView = getChildAt(i);
        if ((localView.getVisibility() != 8) && (localView != BottomSheet.this.containerView)) {
          break label308;
        }
      }
      for (;;)
      {
        i += 1;
        break label175;
        i = 0;
        break;
        label222:
        if (i != 0) {}
        for (i = BottomSheet.backgroundPaddingLeft * 2 + paramInt1;; i = (int)Math.max(paramInt1 * 0.8F, Math.min(AndroidUtilities.dp(480.0F), paramInt1)) + BottomSheet.backgroundPaddingLeft * 2)
        {
          i = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
          break;
        }
        label275:
        BottomSheet.this.containerView.measure(View.MeasureSpec.makeMeasureSpec(BottomSheet.backgroundPaddingLeft * 2 + paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
        break label167;
        label308:
        if (!BottomSheet.this.onCustomMeasure(localView, paramInt1, paramInt2)) {
          measureChildWithMargins(localView, View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), 0, View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), 0);
        }
      }
    }
    
    public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
    {
      return false;
    }
    
    public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
    {
      return false;
    }
    
    public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      if (BottomSheet.this.dismissed) {}
      do
      {
        return;
        cancelCurrentAnimation();
        f1 = BottomSheet.this.containerView.getTranslationY();
      } while ((f1 <= 0.0F) || (paramInt2 <= 0));
      float f2 = f1 - paramInt2;
      paramArrayOfInt[1] = paramInt2;
      float f1 = f2;
      if (f2 < 0.0F)
      {
        f1 = 0.0F;
        paramArrayOfInt[1] = ((int)(paramArrayOfInt[1] + 0.0F));
      }
      BottomSheet.this.containerView.setTranslationY(f1);
    }
    
    public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (BottomSheet.this.dismissed) {}
      do
      {
        return;
        cancelCurrentAnimation();
      } while (paramInt4 == 0);
      float f2 = BottomSheet.this.containerView.getTranslationY() - paramInt4;
      float f1 = f2;
      if (f2 < 0.0F) {
        f1 = 0.0F;
      }
      BottomSheet.this.containerView.setTranslationY(f1);
    }
    
    public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
    {
      this.nestedScrollingParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt);
      if (BottomSheet.this.dismissed) {
        return;
      }
      cancelCurrentAnimation();
    }
    
    public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
    {
      return (!BottomSheet.this.dismissed) && (paramInt == 2) && (!BottomSheet.this.canDismissWithSwipe());
    }
    
    public void onStopNestedScroll(View paramView)
    {
      this.nestedScrollingParentHelper.onStopNestedScroll(paramView);
      if (BottomSheet.this.dismissed) {
        return;
      }
      BottomSheet.this.containerView.getTranslationY();
      checkDismiss(0.0F, 0.0F);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      if (BottomSheet.this.dismissed) {
        return false;
      }
      if (BottomSheet.this.onContainerTouchEvent(paramMotionEvent)) {
        return true;
      }
      if ((BottomSheet.this.canDismissWithTouchOutside()) && (paramMotionEvent != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2)) && (!this.startedTracking) && (!this.maybeStartTracking))
      {
        this.startedTrackingX = ((int)paramMotionEvent.getX());
        this.startedTrackingY = ((int)paramMotionEvent.getY());
        if ((this.startedTrackingY < BottomSheet.this.containerView.getTop()) || (this.startedTrackingX < BottomSheet.this.containerView.getLeft()) || (this.startedTrackingX > BottomSheet.this.containerView.getRight()))
        {
          BottomSheet.this.dismiss();
          return true;
        }
        this.startedTrackingPointerId = paramMotionEvent.getPointerId(0);
        this.maybeStartTracking = true;
        cancelCurrentAnimation();
        if (this.velocityTracker != null) {
          this.velocityTracker.clear();
        }
      }
      label178:
      while ((this.startedTracking) || (!BottomSheet.this.canDismissWithSwipe()))
      {
        return true;
        float f1;
        if ((paramMotionEvent != null) && (paramMotionEvent.getAction() == 2) && (paramMotionEvent.getPointerId(0) == this.startedTrackingPointerId))
        {
          if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
          }
          f1 = Math.abs((int)(paramMotionEvent.getX() - this.startedTrackingX));
          float f2 = (int)paramMotionEvent.getY() - this.startedTrackingY;
          this.velocityTracker.addMovement(paramMotionEvent);
          if ((this.maybeStartTracking) && (!this.startedTracking) && (f2 > 0.0F) && (f2 / 3.0F > Math.abs(f1)) && (Math.abs(f2) >= BottomSheet.this.touchSlop))
          {
            this.startedTrackingY = ((int)paramMotionEvent.getY());
            this.maybeStartTracking = false;
            this.startedTracking = true;
            requestDisallowInterceptTouchEvent(true);
          }
          else if (this.startedTracking)
          {
            f2 = BottomSheet.this.containerView.getTranslationY() + f2;
            f1 = f2;
            if (f2 < 0.0F) {
              f1 = 0.0F;
            }
            BottomSheet.this.containerView.setTranslationY(f1);
            this.startedTrackingY = ((int)paramMotionEvent.getY());
          }
        }
        else if ((paramMotionEvent == null) || ((paramMotionEvent != null) && (paramMotionEvent.getPointerId(0) == this.startedTrackingPointerId) && ((paramMotionEvent.getAction() == 3) || (paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 6))))
        {
          if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
          }
          this.velocityTracker.computeCurrentVelocity(1000);
          f1 = BottomSheet.this.containerView.getTranslationY();
          if ((!this.startedTracking) && (f1 == 0.0F)) {
            break label543;
          }
          checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
        }
      }
      for (this.startedTracking = false;; this.startedTracking = false)
      {
        if (this.velocityTracker != null)
        {
          this.velocityTracker.recycle();
          this.velocityTracker = null;
        }
        this.startedTrackingPointerId = -1;
        break label178;
        break;
        label543:
        this.maybeStartTracking = false;
      }
    }
    
    public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      if ((this.maybeStartTracking) && (!this.startedTracking)) {
        onTouchEvent(null);
      }
      super.requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ActionBar/BottomSheet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */