package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.Keep;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextColorThemeCell;
import org.telegram.ui.LaunchActivity;

public class ThemeEditorView
{
  @SuppressLint({"StaticFieldLeak"})
  private static volatile ThemeEditorView Instance = null;
  private ArrayList<ThemeDescription> currentThemeDesription;
  private int currentThemeDesriptionPosition;
  private String currentThemeName;
  private DecelerateInterpolator decelerateInterpolator;
  private EditorAlert editorAlert;
  private final int editorHeight = AndroidUtilities.dp(54.0F);
  private final int editorWidth = AndroidUtilities.dp(54.0F);
  private boolean hidden;
  private Activity parentActivity;
  private SharedPreferences preferences;
  private WallpaperUpdater wallpaperUpdater;
  private WindowManager.LayoutParams windowLayoutParams;
  private WindowManager windowManager;
  private FrameLayout windowView;
  
  private void animateToBoundsMaybe()
  {
    int i = getSideCoord(true, 0, 0.0F, this.editorWidth);
    int j = getSideCoord(true, 1, 0.0F, this.editorWidth);
    int k = getSideCoord(false, 0, 0.0F, this.editorHeight);
    int m = getSideCoord(false, 1, 0.0F, this.editorHeight);
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    SharedPreferences.Editor localEditor = this.preferences.edit();
    int n = AndroidUtilities.dp(20.0F);
    int i1 = 0;
    if ((Math.abs(i - this.windowLayoutParams.x) <= n) || ((this.windowLayoutParams.x < 0) && (this.windowLayoutParams.x > -this.editorWidth / 4)))
    {
      if (0 == 0) {
        localObject4 = new ArrayList();
      }
      localEditor.putInt("sidex", 0);
      if (this.windowView.getAlpha() != 1.0F) {
        ((ArrayList)localObject4).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F }));
      }
      ((ArrayList)localObject4).add(ObjectAnimator.ofInt(this, "x", new int[] { i }));
      localObject1 = localObject4;
      if (i1 == 0)
      {
        if ((Math.abs(k - this.windowLayoutParams.y) > n) && (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight())) {
          break label691;
        }
        localObject1 = localObject4;
        if (localObject4 == null) {
          localObject1 = new ArrayList();
        }
        localEditor.putInt("sidey", 0);
        ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "y", new int[] { k }));
        localObject4 = localObject1;
      }
    }
    for (;;)
    {
      localEditor.commit();
      localObject1 = localObject4;
      if (localObject1 != null)
      {
        if (this.decelerateInterpolator == null) {
          this.decelerateInterpolator = new DecelerateInterpolator();
        }
        localObject4 = new AnimatorSet();
        ((AnimatorSet)localObject4).setInterpolator(this.decelerateInterpolator);
        ((AnimatorSet)localObject4).setDuration(150L);
        if (i1 != 0)
        {
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 0.0F }));
          ((AnimatorSet)localObject4).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              Theme.saveCurrentTheme(ThemeEditorView.this.currentThemeName, true);
              ThemeEditorView.this.destroy();
            }
          });
        }
        ((AnimatorSet)localObject4).playTogether((Collection)localObject1);
        ((AnimatorSet)localObject4).start();
      }
      return;
      if ((Math.abs(j - this.windowLayoutParams.x) <= n) || ((this.windowLayoutParams.x > AndroidUtilities.displaySize.x - this.editorWidth) && (this.windowLayoutParams.x < AndroidUtilities.displaySize.x - this.editorWidth / 4 * 3)))
      {
        localObject4 = localObject2;
        if (0 == 0) {
          localObject4 = new ArrayList();
        }
        localEditor.putInt("sidex", 1);
        if (this.windowView.getAlpha() != 1.0F) {
          ((ArrayList)localObject4).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F }));
        }
        ((ArrayList)localObject4).add(ObjectAnimator.ofInt(this, "x", new int[] { j }));
        break;
      }
      if (this.windowView.getAlpha() != 1.0F)
      {
        localObject4 = localObject3;
        if (0 == 0) {
          localObject4 = new ArrayList();
        }
        if (this.windowLayoutParams.x < 0) {
          ((ArrayList)localObject4).add(ObjectAnimator.ofInt(this, "x", new int[] { -this.editorWidth }));
        }
        for (;;)
        {
          i1 = 1;
          break;
          ((ArrayList)localObject4).add(ObjectAnimator.ofInt(this, "x", new int[] { AndroidUtilities.displaySize.x }));
        }
      }
      localEditor.putFloat("px", (this.windowLayoutParams.x - i) / (j - i));
      localEditor.putInt("sidex", 2);
      localObject4 = localObject1;
      break;
      label691:
      if (Math.abs(m - this.windowLayoutParams.y) <= n)
      {
        localObject1 = localObject4;
        if (localObject4 == null) {
          localObject1 = new ArrayList();
        }
        localEditor.putInt("sidey", 1);
        ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "y", new int[] { m }));
        localObject4 = localObject1;
      }
      else
      {
        localEditor.putFloat("py", (this.windowLayoutParams.y - k) / (m - k));
        localEditor.putInt("sidey", 2);
      }
    }
  }
  
  public static ThemeEditorView getInstance()
  {
    return Instance;
  }
  
  private static int getSideCoord(boolean paramBoolean, int paramInt1, float paramFloat, int paramInt2)
  {
    if (paramBoolean)
    {
      paramInt2 = AndroidUtilities.displaySize.x - paramInt2;
      if (paramInt1 != 0) {
        break label54;
      }
      paramInt1 = AndroidUtilities.dp(10.0F);
    }
    for (;;)
    {
      paramInt2 = paramInt1;
      if (!paramBoolean) {
        paramInt2 = paramInt1 + ActionBar.getCurrentActionBarHeight();
      }
      return paramInt2;
      paramInt2 = AndroidUtilities.displaySize.y - paramInt2 - ActionBar.getCurrentActionBarHeight();
      break;
      label54:
      if (paramInt1 == 1) {
        paramInt1 = paramInt2 - AndroidUtilities.dp(10.0F);
      } else {
        paramInt1 = Math.round((paramInt2 - AndroidUtilities.dp(20.0F)) * paramFloat) + AndroidUtilities.dp(10.0F);
      }
    }
  }
  
  private void hide()
  {
    if (this.parentActivity == null) {}
    for (;;)
    {
      return;
      try
      {
        AnimatorSet localAnimatorSet = new android/animation/AnimatorSet;
        localAnimatorSet.<init>();
        localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[] { 1.0F, 0.0F }) });
        localAnimatorSet.setInterpolator(this.decelerateInterpolator);
        localAnimatorSet.setDuration(150L);
        AnimatorListenerAdapter local3 = new org/telegram/ui/Components/ThemeEditorView$3;
        local3.<init>(this);
        localAnimatorSet.addListener(local3);
        localAnimatorSet.start();
        this.hidden = true;
      }
      catch (Exception localException) {}
    }
  }
  
  private void show()
  {
    if (this.parentActivity == null) {}
    for (;;)
    {
      return;
      try
      {
        this.windowManager.addView(this.windowView, this.windowLayoutParams);
        this.hidden = false;
        showWithAnimation();
      }
      catch (Exception localException) {}
    }
  }
  
  private void showWithAnimation()
  {
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.windowView, "scaleX", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(this.windowView, "scaleY", new float[] { 0.0F, 1.0F }) });
    localAnimatorSet.setInterpolator(this.decelerateInterpolator);
    localAnimatorSet.setDuration(150L);
    localAnimatorSet.start();
  }
  
  public void close()
  {
    try
    {
      this.windowManager.removeView(this.windowView);
      this.parentActivity = null;
      return;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  public void destroy()
  {
    this.wallpaperUpdater.cleanup();
    if ((this.parentActivity == null) || (this.windowView == null)) {}
    for (;;)
    {
      return;
      try
      {
        this.windowManager.removeViewImmediate(this.windowView);
        this.windowView = null;
      }
      catch (Exception localException1)
      {
        try
        {
          for (;;)
          {
            if (this.editorAlert != null)
            {
              this.editorAlert.dismiss();
              this.editorAlert = null;
            }
            this.parentActivity = null;
            Instance = null;
            break;
            localException1 = localException1;
            FileLog.e(localException1);
          }
        }
        catch (Exception localException2)
        {
          for (;;)
          {
            FileLog.e(localException2);
          }
        }
      }
    }
  }
  
  public int getX()
  {
    return this.windowLayoutParams.x;
  }
  
  public int getY()
  {
    return this.windowLayoutParams.y;
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (this.wallpaperUpdater != null) {
      this.wallpaperUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public void onConfigurationChanged()
  {
    int i = this.preferences.getInt("sidex", 1);
    int j = this.preferences.getInt("sidey", 0);
    float f1 = this.preferences.getFloat("px", 0.0F);
    float f2 = this.preferences.getFloat("py", 0.0F);
    this.windowLayoutParams.x = getSideCoord(true, i, f1, this.editorWidth);
    this.windowLayoutParams.y = getSideCoord(false, j, f2, this.editorHeight);
    try
    {
      if (this.windowView.getParent() != null) {
        this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
      }
      return;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  @Keep
  public void setX(int paramInt)
  {
    this.windowLayoutParams.x = paramInt;
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }
  
  @Keep
  public void setY(int paramInt)
  {
    this.windowLayoutParams.y = paramInt;
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }
  
  public void show(Activity paramActivity, final String paramString)
  {
    if (Instance != null) {
      Instance.destroy();
    }
    this.hidden = false;
    this.currentThemeName = paramString;
    this.windowView = new FrameLayout(paramActivity)
    {
      private boolean dragging;
      private float startX;
      private float startY;
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        float f1 = paramAnonymousMotionEvent.getRawX();
        float f2 = paramAnonymousMotionEvent.getRawY();
        float f3;
        int i;
        if (paramAnonymousMotionEvent.getAction() == 0)
        {
          this.startX = f1;
          this.startY = f2;
          if (this.dragging)
          {
            if (paramAnonymousMotionEvent.getAction() != 2) {
              break label711;
            }
            f3 = this.startX;
            float f4 = this.startY;
            paramAnonymousMotionEvent = ThemeEditorView.this.windowLayoutParams;
            paramAnonymousMotionEvent.x = ((int)(paramAnonymousMotionEvent.x + (f1 - f3)));
            paramAnonymousMotionEvent = ThemeEditorView.this.windowLayoutParams;
            paramAnonymousMotionEvent.y = ((int)(paramAnonymousMotionEvent.y + (f2 - f4)));
            i = ThemeEditorView.this.editorWidth / 2;
            if (ThemeEditorView.this.windowLayoutParams.x >= -i) {
              break label508;
            }
            ThemeEditorView.this.windowLayoutParams.x = (-i);
            label140:
            f3 = 1.0F;
            if (ThemeEditorView.this.windowLayoutParams.x >= 0) {
              break label574;
            }
            f3 = 1.0F + ThemeEditorView.this.windowLayoutParams.x / i * 0.5F;
            label178:
            if (ThemeEditorView.this.windowView.getAlpha() != f3) {
              ThemeEditorView.this.windowView.setAlpha(f3);
            }
            if (ThemeEditorView.this.windowLayoutParams.y >= -0) {
              break label647;
            }
            ThemeEditorView.this.windowLayoutParams.y = (-0);
            label233:
            ThemeEditorView.this.windowManager.updateViewLayout(ThemeEditorView.this.windowView, ThemeEditorView.this.windowLayoutParams);
            this.startX = f1;
            this.startY = f2;
          }
        }
        for (;;)
        {
          return true;
          if ((paramAnonymousMotionEvent.getAction() == 2) && (!this.dragging))
          {
            if ((Math.abs(this.startX - f1) < AndroidUtilities.getPixelsInCM(0.3F, true)) && (Math.abs(this.startY - f2) < AndroidUtilities.getPixelsInCM(0.3F, false))) {
              break;
            }
            this.dragging = true;
            this.startX = f1;
            this.startY = f2;
            break;
          }
          if ((paramAnonymousMotionEvent.getAction() != 1) || (this.dragging) || (ThemeEditorView.this.editorAlert != null)) {
            break;
          }
          Object localObject = ((LaunchActivity)ThemeEditorView.this.parentActivity).getActionBarLayout();
          if (((ActionBarLayout)localObject).fragmentsStack.isEmpty()) {
            break;
          }
          localObject = ((BaseFragment)((ActionBarLayout)localObject).fragmentsStack.get(((ActionBarLayout)localObject).fragmentsStack.size() - 1)).getThemeDescriptions();
          if (localObject == null) {
            break;
          }
          ThemeEditorView.access$2402(ThemeEditorView.this, new ThemeEditorView.EditorAlert(ThemeEditorView.this, ThemeEditorView.this.parentActivity, (ThemeDescription[])localObject));
          ThemeEditorView.this.editorAlert.setOnDismissListener(new DialogInterface.OnDismissListener()
          {
            public void onDismiss(DialogInterface paramAnonymous2DialogInterface) {}
          });
          ThemeEditorView.this.editorAlert.setOnDismissListener(new DialogInterface.OnDismissListener()
          {
            public void onDismiss(DialogInterface paramAnonymous2DialogInterface)
            {
              ThemeEditorView.access$2402(ThemeEditorView.this, null);
              ThemeEditorView.this.show();
            }
          });
          ThemeEditorView.this.editorAlert.show();
          ThemeEditorView.this.hide();
          break;
          label508:
          if (ThemeEditorView.this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width + i) {
            break label140;
          }
          ThemeEditorView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width + i);
          break label140;
          label574:
          if (ThemeEditorView.this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - ThemeEditorView.this.windowLayoutParams.width) {
            break label178;
          }
          f3 = 1.0F - (ThemeEditorView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x + ThemeEditorView.this.windowLayoutParams.width) / i * 0.5F;
          break label178;
          label647:
          if (ThemeEditorView.this.windowLayoutParams.y <= AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height + 0) {
            break label233;
          }
          ThemeEditorView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - ThemeEditorView.this.windowLayoutParams.height + 0);
          break label233;
          label711:
          if (paramAnonymousMotionEvent.getAction() == 1)
          {
            this.dragging = false;
            ThemeEditorView.this.animateToBoundsMaybe();
          }
        }
      }
    };
    this.windowView.setBackgroundResource(NUM);
    this.windowManager = ((WindowManager)paramActivity.getSystemService("window"));
    this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
    int i = this.preferences.getInt("sidex", 1);
    int j = this.preferences.getInt("sidey", 0);
    float f1 = this.preferences.getFloat("px", 0.0F);
    float f2 = this.preferences.getFloat("py", 0.0F);
    try
    {
      WindowManager.LayoutParams localLayoutParams = new android/view/WindowManager$LayoutParams;
      localLayoutParams.<init>();
      this.windowLayoutParams = localLayoutParams;
      this.windowLayoutParams.width = this.editorWidth;
      this.windowLayoutParams.height = this.editorHeight;
      this.windowLayoutParams.x = getSideCoord(true, i, f1, this.editorWidth);
      this.windowLayoutParams.y = getSideCoord(false, j, f2, this.editorHeight);
      this.windowLayoutParams.format = -3;
      this.windowLayoutParams.gravity = 51;
      this.windowLayoutParams.type = 99;
      this.windowLayoutParams.flags = 16777736;
      this.windowManager.addView(this.windowView, this.windowLayoutParams);
      this.wallpaperUpdater = new WallpaperUpdater(paramActivity, new WallpaperUpdater.WallpaperUpdaterDelegate()
      {
        public void didSelectWallpaper(File paramAnonymousFile, Bitmap paramAnonymousBitmap)
        {
          Theme.setThemeWallpaper(paramString, paramAnonymousBitmap, paramAnonymousFile);
        }
        
        public void needOpenColorPicker()
        {
          for (int i = 0; i < ThemeEditorView.this.currentThemeDesription.size(); i++)
          {
            ThemeDescription localThemeDescription = (ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i);
            localThemeDescription.startEditing();
            if (i == 0) {
              ThemeEditorView.EditorAlert.access$900(ThemeEditorView.this.editorAlert).setColor(localThemeDescription.getCurrentColor());
            }
          }
          ThemeEditorView.EditorAlert.access$1700(ThemeEditorView.this.editorAlert, true);
        }
      });
      Instance = this;
      this.parentActivity = paramActivity;
      showWithAnimation();
      return;
    }
    catch (Exception paramActivity)
    {
      for (;;)
      {
        FileLog.e(paramActivity);
      }
    }
  }
  
  public class EditorAlert
    extends BottomSheet
  {
    private boolean animationInProgress;
    private FrameLayout bottomLayout;
    private FrameLayout bottomSaveLayout;
    private TextView cancelButton;
    private AnimatorSet colorChangeAnimation;
    private ColorPicker colorPicker;
    private TextView defaultButtom;
    private boolean ignoreTextChange;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int previousScrollPosition;
    private TextView saveButton;
    private int scrollOffsetY;
    private View shadow;
    private Drawable shadowDrawable;
    private boolean startedColorChange;
    private int topBeforeSwitch;
    
    public EditorAlert(Context paramContext, ThemeDescription[] paramArrayOfThemeDescription)
    {
      super(true);
      this.shadowDrawable = paramContext.getResources().getDrawable(NUM).mutate();
      this.containerView = new FrameLayout(paramContext)
      {
        private boolean ignoreLayout = false;
        
        protected void onDraw(Canvas paramAnonymousCanvas)
        {
          ThemeEditorView.EditorAlert.this.shadowDrawable.setBounds(0, ThemeEditorView.EditorAlert.this.scrollOffsetY - ThemeEditorView.EditorAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
          ThemeEditorView.EditorAlert.this.shadowDrawable.draw(paramAnonymousCanvas);
        }
        
        public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((paramAnonymousMotionEvent.getAction() == 0) && (ThemeEditorView.EditorAlert.this.scrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < ThemeEditorView.EditorAlert.this.scrollOffsetY)) {
            ThemeEditorView.EditorAlert.this.dismiss();
          }
          for (boolean bool = true;; bool = super.onInterceptTouchEvent(paramAnonymousMotionEvent)) {
            return bool;
          }
        }
        
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          ThemeEditorView.EditorAlert.this.updateLayout();
        }
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          int i = View.MeasureSpec.getSize(paramAnonymousInt1);
          int j = View.MeasureSpec.getSize(paramAnonymousInt2);
          paramAnonymousInt2 = j;
          if (Build.VERSION.SDK_INT >= 21) {
            paramAnonymousInt2 = j - AndroidUtilities.statusBarHeight;
          }
          j = paramAnonymousInt2 - Math.min(i, paramAnonymousInt2);
          if (ThemeEditorView.EditorAlert.this.listView.getPaddingTop() != j)
          {
            this.ignoreLayout = true;
            ThemeEditorView.EditorAlert.this.listView.getPaddingTop();
            ThemeEditorView.EditorAlert.this.listView.setPadding(0, j, 0, AndroidUtilities.dp(48.0F));
            if (ThemeEditorView.EditorAlert.this.colorPicker.getVisibility() == 0)
            {
              ThemeEditorView.EditorAlert.access$702(ThemeEditorView.EditorAlert.this, ThemeEditorView.EditorAlert.this.listView.getPaddingTop());
              ThemeEditorView.EditorAlert.this.listView.setTopGlowOffset(ThemeEditorView.EditorAlert.this.scrollOffsetY);
              ThemeEditorView.EditorAlert.this.colorPicker.setTranslationY(ThemeEditorView.EditorAlert.this.scrollOffsetY);
              ThemeEditorView.EditorAlert.access$1002(ThemeEditorView.EditorAlert.this, 0);
            }
            this.ignoreLayout = false;
          }
          super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(paramAnonymousInt2, NUM));
        }
        
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((!ThemeEditorView.EditorAlert.this.isDismissed()) && (super.onTouchEvent(paramAnonymousMotionEvent))) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
        
        public void requestLayout()
        {
          if (this.ignoreLayout) {}
          for (;;)
          {
            return;
            super.requestLayout();
          }
        }
      };
      this.containerView.setWillNotDraw(false);
      this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
      this.listView = new RecyclerListView(paramContext);
      this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0F));
      this.listView.setClipToPadding(false);
      RecyclerListView localRecyclerListView = this.listView;
      LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getContext());
      this.layoutManager = localLinearLayoutManager;
      localRecyclerListView.setLayoutManager(localLinearLayoutManager);
      this.listView.setHorizontalScrollBarEnabled(false);
      this.listView.setVerticalScrollBarEnabled(false);
      this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      localRecyclerListView = this.listView;
      paramArrayOfThemeDescription = new ListAdapter(paramContext, paramArrayOfThemeDescription);
      this.listAdapter = paramArrayOfThemeDescription;
      localRecyclerListView.setAdapter(paramArrayOfThemeDescription);
      this.listView.setGlowColor(-657673);
      this.listView.setItemAnimator(null);
      this.listView.setLayoutAnimation(null);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          ThemeEditorView.access$202(ThemeEditorView.this, ThemeEditorView.EditorAlert.this.listAdapter.getItem(paramAnonymousInt));
          ThemeEditorView.access$1502(ThemeEditorView.this, paramAnonymousInt);
          paramAnonymousInt = 0;
          if (paramAnonymousInt < ThemeEditorView.this.currentThemeDesription.size())
          {
            paramAnonymousView = (ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(paramAnonymousInt);
            if (paramAnonymousView.getCurrentKey().equals("chat_wallpaper")) {
              ThemeEditorView.this.wallpaperUpdater.showAlert(true);
            }
          }
          for (;;)
          {
            return;
            paramAnonymousView.startEditing();
            if (paramAnonymousInt == 0) {
              ThemeEditorView.EditorAlert.this.colorPicker.setColor(paramAnonymousView.getCurrentColor());
            }
            paramAnonymousInt++;
            break;
            ThemeEditorView.EditorAlert.this.setColorPickerVisible(true);
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          ThemeEditorView.EditorAlert.this.updateLayout();
        }
      });
      this.colorPicker = new ColorPicker(paramContext);
      this.colorPicker.setVisibility(8);
      this.containerView.addView(this.colorPicker, LayoutHelper.createFrame(-1, -1, 1));
      this.shadow = new View(paramContext);
      this.shadow.setBackgroundResource(NUM);
      this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
      this.bottomSaveLayout = new FrameLayout(paramContext);
      this.bottomSaveLayout.setBackgroundColor(-1);
      this.containerView.addView(this.bottomSaveLayout, LayoutHelper.createFrame(-1, 48, 83));
      paramArrayOfThemeDescription = new TextView(paramContext);
      paramArrayOfThemeDescription.setTextSize(1, 14.0F);
      paramArrayOfThemeDescription.setTextColor(-15095832);
      paramArrayOfThemeDescription.setGravity(17);
      paramArrayOfThemeDescription.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      paramArrayOfThemeDescription.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramArrayOfThemeDescription.setText(LocaleController.getString("CloseEditor", NUM).toUpperCase());
      paramArrayOfThemeDescription.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.bottomSaveLayout.addView(paramArrayOfThemeDescription, LayoutHelper.createFrame(-2, -1, 51));
      paramArrayOfThemeDescription.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ThemeEditorView.EditorAlert.this.dismiss();
        }
      });
      paramArrayOfThemeDescription = new TextView(paramContext);
      paramArrayOfThemeDescription.setTextSize(1, 14.0F);
      paramArrayOfThemeDescription.setTextColor(-15095832);
      paramArrayOfThemeDescription.setGravity(17);
      paramArrayOfThemeDescription.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      paramArrayOfThemeDescription.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramArrayOfThemeDescription.setText(LocaleController.getString("SaveTheme", NUM).toUpperCase());
      paramArrayOfThemeDescription.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.bottomSaveLayout.addView(paramArrayOfThemeDescription, LayoutHelper.createFrame(-2, -1, 53));
      paramArrayOfThemeDescription.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Theme.saveCurrentTheme(ThemeEditorView.this.currentThemeName, true);
          ThemeEditorView.EditorAlert.this.setOnDismissListener(null);
          ThemeEditorView.EditorAlert.this.dismiss();
          ThemeEditorView.this.close();
        }
      });
      this.bottomLayout = new FrameLayout(paramContext);
      this.bottomLayout.setVisibility(8);
      this.bottomLayout.setBackgroundColor(-1);
      this.containerView.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 83));
      this.cancelButton = new TextView(paramContext);
      this.cancelButton.setTextSize(1, 14.0F);
      this.cancelButton.setTextColor(-15095832);
      this.cancelButton.setGravity(17);
      this.cancelButton.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      this.cancelButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      this.cancelButton.setText(LocaleController.getString("Cancel", NUM).toUpperCase());
      this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.bottomLayout.addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
      this.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          for (int i = 0; i < ThemeEditorView.this.currentThemeDesription.size(); i++) {
            ((ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i)).setPreviousColor();
          }
          ThemeEditorView.EditorAlert.this.setColorPickerVisible(false);
        }
      });
      paramArrayOfThemeDescription = new LinearLayout(paramContext);
      paramArrayOfThemeDescription.setOrientation(0);
      this.bottomLayout.addView(paramArrayOfThemeDescription, LayoutHelper.createFrame(-2, -1, 53));
      this.defaultButtom = new TextView(paramContext);
      this.defaultButtom.setTextSize(1, 14.0F);
      this.defaultButtom.setTextColor(-15095832);
      this.defaultButtom.setGravity(17);
      this.defaultButtom.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      this.defaultButtom.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      this.defaultButtom.setText(LocaleController.getString("Default", NUM).toUpperCase());
      this.defaultButtom.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramArrayOfThemeDescription.addView(this.defaultButtom, LayoutHelper.createFrame(-2, -1, 51));
      this.defaultButtom.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          for (int i = 0; i < ThemeEditorView.this.currentThemeDesription.size(); i++) {
            ((ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i)).setDefaultColor();
          }
          ThemeEditorView.EditorAlert.this.setColorPickerVisible(false);
        }
      });
      paramContext = new TextView(paramContext);
      paramContext.setTextSize(1, 14.0F);
      paramContext.setTextColor(-15095832);
      paramContext.setGravity(17);
      paramContext.setBackgroundDrawable(Theme.createSelectorDrawable(788529152, 0));
      paramContext.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramContext.setText(LocaleController.getString("Save", NUM).toUpperCase());
      paramContext.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramArrayOfThemeDescription.addView(paramContext, LayoutHelper.createFrame(-2, -1, 51));
      paramContext.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          ThemeEditorView.EditorAlert.this.setColorPickerVisible(false);
        }
      });
    }
    
    private int getCurrentTop()
    {
      int i = 0;
      int j;
      if (this.listView.getChildCount() != 0)
      {
        View localView = this.listView.getChildAt(0);
        RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.listView.findContainingViewHolder(localView);
        if (localHolder != null)
        {
          j = this.listView.getPaddingTop();
          k = i;
          if (localHolder.getAdapterPosition() == 0)
          {
            k = i;
            if (localView.getTop() >= 0) {
              k = localView.getTop();
            }
          }
        }
      }
      for (int k = j - k;; k = 64536) {
        return k;
      }
    }
    
    private void setColorPickerVisible(boolean paramBoolean)
    {
      AnimatorSet localAnimatorSet;
      if (paramBoolean)
      {
        this.animationInProgress = true;
        this.colorPicker.setVisibility(0);
        this.bottomLayout.setVisibility(0);
        this.colorPicker.setAlpha(0.0F);
        this.bottomLayout.setAlpha(0.0F);
        localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.colorPicker, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.listView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.bottomSaveLayout, "alpha", new float[] { 0.0F }), ObjectAnimator.ofInt(this, "scrollOffsetY", new int[] { this.listView.getPaddingTop() }) });
        localAnimatorSet.setDuration(150L);
        localAnimatorSet.setInterpolator(ThemeEditorView.this.decelerateInterpolator);
        localAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            ThemeEditorView.EditorAlert.this.listView.setVisibility(4);
            ThemeEditorView.EditorAlert.this.bottomSaveLayout.setVisibility(4);
            ThemeEditorView.EditorAlert.access$2102(ThemeEditorView.EditorAlert.this, false);
          }
        });
        localAnimatorSet.start();
        this.previousScrollPosition = this.scrollOffsetY;
      }
      for (;;)
      {
        return;
        if (ThemeEditorView.this.parentActivity != null) {
          ((LaunchActivity)ThemeEditorView.this.parentActivity).rebuildAllFragments(false);
        }
        Theme.saveCurrentTheme(ThemeEditorView.this.currentThemeName, false);
        AndroidUtilities.hideKeyboard(getCurrentFocus());
        this.animationInProgress = true;
        this.listView.setVisibility(0);
        this.bottomSaveLayout.setVisibility(0);
        this.listView.setAlpha(0.0F);
        localAnimatorSet = new AnimatorSet();
        localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.colorPicker, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.bottomLayout, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.listView, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.bottomSaveLayout, "alpha", new float[] { 1.0F }), ObjectAnimator.ofInt(this, "scrollOffsetY", new int[] { this.previousScrollPosition }) });
        localAnimatorSet.setDuration(150L);
        localAnimatorSet.setInterpolator(ThemeEditorView.this.decelerateInterpolator);
        localAnimatorSet.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            ThemeEditorView.EditorAlert.this.colorPicker.setVisibility(8);
            ThemeEditorView.EditorAlert.this.bottomLayout.setVisibility(8);
            ThemeEditorView.EditorAlert.access$2102(ThemeEditorView.EditorAlert.this, false);
          }
        });
        localAnimatorSet.start();
        this.listAdapter.notifyItemChanged(ThemeEditorView.this.currentThemeDesriptionPosition);
      }
    }
    
    @SuppressLint({"NewApi"})
    private void updateLayout()
    {
      int i = 0;
      if ((this.listView.getChildCount() <= 0) || (this.listView.getVisibility() != 0) || (this.animationInProgress)) {
        return;
      }
      View localView = this.listView.getChildAt(0);
      RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.listView.findContainingViewHolder(localView);
      if ((this.listView.getVisibility() != 0) || (this.animationInProgress)) {}
      for (int j = this.listView.getPaddingTop();; j = localView.getTop() - AndroidUtilities.dp(8.0F))
      {
        int k = i;
        if (j > 0)
        {
          k = i;
          if (localHolder != null)
          {
            k = i;
            if (localHolder.getAdapterPosition() == 0) {
              k = j;
            }
          }
        }
        if (this.scrollOffsetY == k) {
          break;
        }
        setScrollOffsetY(k);
        break;
      }
    }
    
    protected boolean canDismissWithSwipe()
    {
      return false;
    }
    
    public int getScrollOffsetY()
    {
      return this.scrollOffsetY;
    }
    
    @Keep
    public void setScrollOffsetY(int paramInt)
    {
      RecyclerListView localRecyclerListView = this.listView;
      this.scrollOffsetY = paramInt;
      localRecyclerListView.setTopGlowOffset(paramInt);
      this.colorPicker.setTranslationY(this.scrollOffsetY);
      this.containerView.invalidate();
    }
    
    private class ColorPicker
      extends FrameLayout
    {
      private float alpha = 1.0F;
      private LinearGradient alphaGradient;
      private boolean alphaPressed;
      private Drawable circleDrawable;
      private Paint circlePaint;
      private boolean circlePressed;
      private EditTextBoldCursor[] colorEditText = new EditTextBoldCursor[4];
      private LinearGradient colorGradient;
      private float[] colorHSV = { 0.0F, 0.0F, 1.0F };
      private boolean colorPressed;
      private Bitmap colorWheelBitmap;
      private Paint colorWheelPaint;
      private int colorWheelRadius;
      private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
      private float[] hsvTemp = new float[3];
      private LinearLayout linearLayout;
      private final int paramValueSliderWidth = AndroidUtilities.dp(20.0F);
      private Paint valueSliderPaint;
      
      public ColorPicker(Context paramContext)
      {
        super();
        setWillNotDraw(false);
        this.circlePaint = new Paint(1);
        this.circleDrawable = paramContext.getResources().getDrawable(NUM).mutate();
        this.colorWheelPaint = new Paint();
        this.colorWheelPaint.setAntiAlias(true);
        this.colorWheelPaint.setDither(true);
        this.valueSliderPaint = new Paint();
        this.valueSliderPaint.setAntiAlias(true);
        this.valueSliderPaint.setDither(true);
        this.linearLayout = new LinearLayout(paramContext);
        this.linearLayout.setOrientation(0);
        addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, 49));
        final int i = 0;
        if (i < 4)
        {
          this.colorEditText[i] = new EditTextBoldCursor(paramContext);
          this.colorEditText[i].setInputType(2);
          this.colorEditText[i].setTextColor(-14606047);
          this.colorEditText[i].setCursorColor(-14606047);
          this.colorEditText[i].setCursorSize(AndroidUtilities.dp(20.0F));
          this.colorEditText[i].setCursorWidth(1.5F);
          this.colorEditText[i].setTextSize(1, 18.0F);
          this.colorEditText[i].setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, true));
          this.colorEditText[i].setMaxLines(1);
          this.colorEditText[i].setTag(Integer.valueOf(i));
          this.colorEditText[i].setGravity(17);
          label346:
          Object localObject;
          int j;
          label363:
          LinearLayout localLinearLayout;
          if (i == 0)
          {
            this.colorEditText[i].setHint("red");
            localObject = this.colorEditText[i];
            if (i != 3) {
              break label543;
            }
            j = 6;
            ((EditTextBoldCursor)localObject).setImeOptions(j | 0x10000000);
            localObject = new InputFilter.LengthFilter(3);
            this.colorEditText[i].setFilters(new InputFilter[] { localObject });
            localLinearLayout = this.linearLayout;
            localObject = this.colorEditText[i];
            if (i == 3) {
              break label549;
            }
          }
          label543:
          label549:
          for (float f = 16.0F;; f = 0.0F)
          {
            localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(55, 36, 0.0F, 0.0F, f, 0.0F));
            this.colorEditText[i].addTextChangedListener(new TextWatcher()
            {
              public void afterTextChanged(Editable paramAnonymousEditable)
              {
                if (ThemeEditorView.EditorAlert.this.ignoreTextChange) {}
                for (;;)
                {
                  return;
                  ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, true);
                  int i = Utilities.parseInt(paramAnonymousEditable.toString()).intValue();
                  int j;
                  int k;
                  if (i < 0)
                  {
                    j = 0;
                    ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[i].setText("" + 0);
                    ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[i].setSelection(ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[i].length());
                    k = ThemeEditorView.EditorAlert.ColorPicker.this.getColor();
                    if (i != 2) {
                      break label286;
                    }
                    i = k & 0xFF00 | j & 0xFF;
                  }
                  for (;;)
                  {
                    ThemeEditorView.EditorAlert.ColorPicker.this.setColor(i);
                    for (i = 0; i < ThemeEditorView.this.currentThemeDesription.size(); i++) {
                      ((ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i)).setColor(ThemeEditorView.EditorAlert.ColorPicker.this.getColor(), false);
                    }
                    j = i;
                    if (i <= 255) {
                      break;
                    }
                    j = 255;
                    ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[i].setText("" + 255);
                    ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[i].setSelection(ThemeEditorView.EditorAlert.ColorPicker.this.colorEditText[i].length());
                    break;
                    label286:
                    if (i == 1)
                    {
                      i = 0xFFFF00FF & k | (j & 0xFF) << 8;
                    }
                    else if (i == 0)
                    {
                      i = 0xFF00FFFF & k | (j & 0xFF) << 16;
                    }
                    else
                    {
                      i = k;
                      if (i == 3) {
                        i = 0xFFFFFF & k | (j & 0xFF) << 24;
                      }
                    }
                  }
                  ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, false);
                }
              }
              
              public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
              
              public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
            });
            this.colorEditText[i].setOnEditorActionListener(new TextView.OnEditorActionListener()
            {
              public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
              {
                if (paramAnonymousInt == 6) {
                  AndroidUtilities.hideKeyboard(paramAnonymousTextView);
                }
                for (boolean bool = true;; bool = false) {
                  return bool;
                }
              }
            });
            i++;
            break;
            if (i == 1)
            {
              this.colorEditText[i].setHint("green");
              break label346;
            }
            if (i == 2)
            {
              this.colorEditText[i].setHint("blue");
              break label346;
            }
            if (i != 3) {
              break label346;
            }
            this.colorEditText[i].setHint("alpha");
            break label346;
            j = 5;
            break label363;
          }
        }
      }
      
      private Bitmap createColorWheelBitmap(int paramInt1, int paramInt2)
      {
        Bitmap localBitmap = Bitmap.createBitmap(paramInt1, paramInt2, Bitmap.Config.ARGB_8888);
        Object localObject = new int[13];
        float[] arrayOfFloat = new float[3];
        float[] tmp22_20 = arrayOfFloat;
        tmp22_20[0] = 0.0F;
        float[] tmp26_22 = tmp22_20;
        tmp26_22[1] = 1.0F;
        float[] tmp30_26 = tmp26_22;
        tmp30_26[2] = 1.0F;
        tmp30_26;
        for (int i = 0; i < localObject.length; i++)
        {
          arrayOfFloat[0] = ((i * 30 + 180) % 360);
          localObject[i] = Color.HSVToColor(arrayOfFloat);
        }
        localObject[12] = localObject[0];
        localObject = new ComposeShader(new SweepGradient(paramInt1 / 2, paramInt2 / 2, (int[])localObject, null), new RadialGradient(paramInt1 / 2, paramInt2 / 2, this.colorWheelRadius, -1, 16777215, Shader.TileMode.CLAMP), PorterDuff.Mode.SRC_OVER);
        this.colorWheelPaint.setShader((Shader)localObject);
        new Canvas(localBitmap).drawCircle(paramInt1 / 2, paramInt2 / 2, this.colorWheelRadius, this.colorWheelPaint);
        return localBitmap;
      }
      
      private void drawPointerArrow(Canvas paramCanvas, int paramInt1, int paramInt2, int paramInt3)
      {
        int i = AndroidUtilities.dp(13.0F);
        this.circleDrawable.setBounds(paramInt1 - i, paramInt2 - i, paramInt1 + i, paramInt2 + i);
        this.circleDrawable.draw(paramCanvas);
        this.circlePaint.setColor(-1);
        paramCanvas.drawCircle(paramInt1, paramInt2, AndroidUtilities.dp(11.0F), this.circlePaint);
        this.circlePaint.setColor(paramInt3);
        paramCanvas.drawCircle(paramInt1, paramInt2, AndroidUtilities.dp(9.0F), this.circlePaint);
      }
      
      private void startColorChange(boolean paramBoolean)
      {
        if (ThemeEditorView.EditorAlert.this.startedColorChange == paramBoolean) {
          return;
        }
        if (ThemeEditorView.EditorAlert.this.colorChangeAnimation != null) {
          ThemeEditorView.EditorAlert.this.colorChangeAnimation.cancel();
        }
        ThemeEditorView.EditorAlert.access$302(ThemeEditorView.EditorAlert.this, paramBoolean);
        ThemeEditorView.EditorAlert.access$402(ThemeEditorView.EditorAlert.this, new AnimatorSet());
        AnimatorSet localAnimatorSet = ThemeEditorView.EditorAlert.this.colorChangeAnimation;
        Object localObject = ThemeEditorView.EditorAlert.this.backDrawable;
        int i;
        label79:
        ViewGroup localViewGroup;
        if (paramBoolean)
        {
          i = 0;
          localObject = ObjectAnimator.ofInt(localObject, "alpha", new int[] { i });
          localViewGroup = ThemeEditorView.EditorAlert.this.containerView;
          if (!paramBoolean) {
            break label190;
          }
        }
        label190:
        for (float f = 0.2F;; f = 1.0F)
        {
          localAnimatorSet.playTogether(new Animator[] { localObject, ObjectAnimator.ofFloat(localViewGroup, "alpha", new float[] { f }) });
          ThemeEditorView.EditorAlert.this.colorChangeAnimation.setDuration(150L);
          ThemeEditorView.EditorAlert.this.colorChangeAnimation.setInterpolator(this.decelerateInterpolator);
          ThemeEditorView.EditorAlert.this.colorChangeAnimation.start();
          break;
          i = 51;
          break label79;
        }
      }
      
      public int getColor()
      {
        return Color.HSVToColor(this.colorHSV) & 0xFFFFFF | (int)(this.alpha * 255.0F) << 24;
      }
      
      protected void onDraw(Canvas paramCanvas)
      {
        int i = getWidth() / 2 - this.paramValueSliderWidth * 2;
        int j = getHeight() / 2 - AndroidUtilities.dp(8.0F);
        paramCanvas.drawBitmap(this.colorWheelBitmap, i - this.colorWheelRadius, j - this.colorWheelRadius, null);
        float f1 = (float)Math.toRadians(this.colorHSV[0]);
        int k = (int)(-Math.cos(f1) * this.colorHSV[1] * this.colorWheelRadius);
        int m = (int)(-Math.sin(f1) * this.colorHSV[1] * this.colorWheelRadius);
        f1 = this.colorWheelRadius;
        this.hsvTemp[0] = this.colorHSV[0];
        this.hsvTemp[1] = this.colorHSV[1];
        this.hsvTemp[2] = 1.0F;
        drawPointerArrow(paramCanvas, k + i, m + j, Color.HSVToColor(this.hsvTemp));
        i = this.colorWheelRadius + i + this.paramValueSliderWidth;
        k = j - this.colorWheelRadius;
        j = AndroidUtilities.dp(9.0F);
        m = this.colorWheelRadius * 2;
        float f2;
        float f3;
        float f4;
        int n;
        Shader.TileMode localTileMode;
        if (this.colorGradient == null)
        {
          f2 = i;
          f3 = k;
          f4 = i + j;
          f1 = k + m;
          n = Color.HSVToColor(this.hsvTemp);
          localTileMode = Shader.TileMode.CLAMP;
          this.colorGradient = new LinearGradient(f2, f3, f4, f1, new int[] { -16777216, n }, null, localTileMode);
        }
        this.valueSliderPaint.setShader(this.colorGradient);
        paramCanvas.drawRect(i, k, i + j, k + m, this.valueSliderPaint);
        drawPointerArrow(paramCanvas, j / 2 + i, (int)(k + this.colorHSV[2] * m), Color.HSVToColor(this.colorHSV));
        i += this.paramValueSliderWidth * 2;
        if (this.alphaGradient == null)
        {
          n = Color.HSVToColor(this.hsvTemp);
          f4 = i;
          f1 = k;
          f3 = i + j;
          f2 = k + m;
          localTileMode = Shader.TileMode.CLAMP;
          this.alphaGradient = new LinearGradient(f4, f1, f3, f2, new int[] { n, 0xFFFFFF & n }, null, localTileMode);
        }
        this.valueSliderPaint.setShader(this.alphaGradient);
        paramCanvas.drawRect(i, k, i + j, k + m, this.valueSliderPaint);
        drawPointerArrow(paramCanvas, j / 2 + i, (int)(k + (1.0F - this.alpha) * m), Color.HSVToColor(this.colorHSV) & 0xFFFFFF | (int)(255.0F * this.alpha) << 24);
      }
      
      protected void onMeasure(int paramInt1, int paramInt2)
      {
        int i = Math.min(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt2));
        measureChild(this.linearLayout, paramInt1, paramInt2);
        setMeasuredDimension(i, i);
      }
      
      protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        this.colorWheelRadius = Math.max(1, paramInt1 / 2 - this.paramValueSliderWidth * 2 - AndroidUtilities.dp(20.0F));
        this.colorWheelBitmap = createColorWheelBitmap(this.colorWheelRadius * 2, this.colorWheelRadius * 2);
        this.colorGradient = null;
        this.alphaGradient = null;
      }
      
      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        switch (paramMotionEvent.getAction())
        {
        }
        for (;;)
        {
          label576:
          label808:
          for (boolean bool = super.onTouchEvent(paramMotionEvent);; bool = true)
          {
            return bool;
            int i = (int)paramMotionEvent.getX();
            int j = (int)paramMotionEvent.getY();
            int k = getWidth() / 2 - this.paramValueSliderWidth * 2;
            int m = getHeight() / 2 - AndroidUtilities.dp(8.0F);
            int n = i - k;
            int i1 = j - m;
            double d1 = Math.sqrt(n * n + i1 * i1);
            if ((this.circlePressed) || ((!this.alphaPressed) && (!this.colorPressed) && (d1 <= this.colorWheelRadius)))
            {
              double d2 = d1;
              if (d1 > this.colorWheelRadius) {
                d2 = this.colorWheelRadius;
              }
              this.circlePressed = true;
              this.colorHSV[0] = ((float)(Math.toDegrees(Math.atan2(i1, n)) + 180.0D));
              this.colorHSV[1] = Math.max(0.0F, Math.min(1.0F, (float)(d2 / this.colorWheelRadius)));
              this.colorGradient = null;
              this.alphaGradient = null;
            }
            float f1;
            float f2;
            if ((this.colorPressed) || ((!this.circlePressed) && (!this.alphaPressed) && (i >= this.colorWheelRadius + k + this.paramValueSliderWidth) && (i <= this.colorWheelRadius + k + this.paramValueSliderWidth * 2) && (j >= m - this.colorWheelRadius) && (j <= this.colorWheelRadius + m)))
            {
              f1 = (j - (m - this.colorWheelRadius)) / (this.colorWheelRadius * 2.0F);
              if (f1 < 0.0F)
              {
                f2 = 0.0F;
                this.colorHSV[2] = f2;
                this.colorPressed = true;
              }
            }
            else if ((this.alphaPressed) || ((!this.circlePressed) && (!this.colorPressed) && (i >= this.colorWheelRadius + k + this.paramValueSliderWidth * 3) && (i <= this.colorWheelRadius + k + this.paramValueSliderWidth * 4) && (j >= m - this.colorWheelRadius) && (j <= this.colorWheelRadius + m)))
            {
              this.alpha = (1.0F - (j - (m - this.colorWheelRadius)) / (this.colorWheelRadius * 2.0F));
              if (this.alpha >= 0.0F) {
                break label576;
              }
              this.alpha = 0.0F;
            }
            for (;;)
            {
              this.alphaPressed = true;
              if ((!this.alphaPressed) && (!this.colorPressed) && (!this.circlePressed)) {
                break label808;
              }
              startColorChange(true);
              k = getColor();
              for (i1 = 0; i1 < ThemeEditorView.this.currentThemeDesription.size(); i1++) {
                ((ThemeDescription)ThemeEditorView.this.currentThemeDesription.get(i1)).setColor(k, false);
              }
              f2 = f1;
              if (f1 <= 1.0F) {
                break;
              }
              f2 = 1.0F;
              break;
              if (this.alpha > 1.0F) {
                this.alpha = 1.0F;
              }
            }
            i1 = Color.red(k);
            n = Color.green(k);
            j = Color.blue(k);
            k = Color.alpha(k);
            if (!ThemeEditorView.EditorAlert.this.ignoreTextChange)
            {
              ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, true);
              this.colorEditText[0].setText("" + i1);
              this.colorEditText[1].setText("" + n);
              this.colorEditText[2].setText("" + j);
              this.colorEditText[3].setText("" + k);
              for (i1 = 0; i1 < 4; i1++) {
                this.colorEditText[i1].setSelection(this.colorEditText[i1].length());
              }
              ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, false);
            }
            invalidate();
          }
          this.alphaPressed = false;
          this.colorPressed = false;
          this.circlePressed = false;
          startColorChange(false);
        }
      }
      
      public void setColor(int paramInt)
      {
        int i = Color.red(paramInt);
        int j = Color.green(paramInt);
        int k = Color.blue(paramInt);
        int m = Color.alpha(paramInt);
        if (!ThemeEditorView.EditorAlert.this.ignoreTextChange)
        {
          ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, true);
          this.colorEditText[0].setText("" + i);
          this.colorEditText[1].setText("" + j);
          this.colorEditText[2].setText("" + k);
          this.colorEditText[3].setText("" + m);
          for (k = 0; k < 4; k++) {
            this.colorEditText[k].setSelection(this.colorEditText[k].length());
          }
          ThemeEditorView.EditorAlert.access$002(ThemeEditorView.EditorAlert.this, false);
        }
        this.alphaGradient = null;
        this.colorGradient = null;
        this.alpha = (m / 255.0F);
        Color.colorToHSV(paramInt, this.colorHSV);
        invalidate();
      }
    }
    
    private class ListAdapter
      extends RecyclerListView.SelectionAdapter
    {
      private Context context;
      private int currentCount;
      private ArrayList<ArrayList<ThemeDescription>> items = new ArrayList();
      private HashMap<String, ArrayList<ThemeDescription>> itemsMap = new HashMap();
      
      public ListAdapter(Context paramContext, ThemeDescription[] paramArrayOfThemeDescription)
      {
        this.context = paramContext;
        for (int i = 0; i < paramArrayOfThemeDescription.length; i++)
        {
          ThemeDescription localThemeDescription = paramArrayOfThemeDescription[i];
          String str = localThemeDescription.getCurrentKey();
          paramContext = (ArrayList)this.itemsMap.get(str);
          this$1 = paramContext;
          if (paramContext == null)
          {
            this$1 = new ArrayList();
            this.itemsMap.put(str, ThemeEditorView.EditorAlert.this);
            this.items.add(ThemeEditorView.EditorAlert.this);
          }
          ThemeEditorView.EditorAlert.this.add(localThemeDescription);
        }
      }
      
      public ArrayList<ThemeDescription> getItem(int paramInt)
      {
        if ((paramInt < 0) || (paramInt >= this.items.size())) {}
        for (Object localObject = null;; localObject = (ArrayList)this.items.get(paramInt)) {
          return (ArrayList<ThemeDescription>)localObject;
        }
      }
      
      public int getItemCount()
      {
        return this.items.size();
      }
      
      public int getItemViewType(int paramInt)
      {
        return 0;
      }
      
      public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
      {
        return true;
      }
      
      public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
      {
        ThemeDescription localThemeDescription = (ThemeDescription)((ArrayList)this.items.get(paramInt)).get(0);
        if (localThemeDescription.getCurrentKey().equals("chat_wallpaper")) {}
        for (paramInt = 0;; paramInt = localThemeDescription.getSetColor())
        {
          ((TextColorThemeCell)paramViewHolder.itemView).setTextAndColor(localThemeDescription.getTitle(), paramInt);
          return;
        }
      }
      
      public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
      {
        paramViewGroup = new TextColorThemeCell(this.context);
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ThemeEditorView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */