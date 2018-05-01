package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.AdapterDataObserver;
import org.telegram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.Theme;

public class RecyclerListView
  extends RecyclerView
{
  private static int[] attributes;
  private static boolean gotAttributes;
  private Runnable clickRunnable;
  private int currentChildPosition;
  private View currentChildView;
  private int currentFirst = -1;
  private int currentVisible = -1;
  private boolean disallowInterceptTouchEvents;
  private View emptyView;
  private FastScroll fastScroll;
  private GestureDetector gestureDetector;
  private ArrayList<View> headers;
  private ArrayList<View> headersCache;
  private boolean hiddenByEmptyView;
  private boolean ignoreOnScroll;
  private boolean instantClick;
  private boolean interceptedByChild;
  private boolean isChildViewEnabled;
  private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver()
  {
    public void onChanged()
    {
      RecyclerListView.this.checkIfEmpty();
      RecyclerListView.this.selectorRect.setEmpty();
      RecyclerListView.this.invalidate();
    }
    
    public void onItemRangeInserted(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      RecyclerListView.this.checkIfEmpty();
    }
    
    public void onItemRangeRemoved(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      RecyclerListView.this.checkIfEmpty();
    }
  };
  private OnInterceptTouchListener onInterceptTouchListener;
  private OnItemClickListener onItemClickListener;
  private OnItemClickListenerExtended onItemClickListenerExtended;
  private OnItemLongClickListener onItemLongClickListener;
  private OnItemLongClickListenerExtended onItemLongClickListenerExtended;
  private RecyclerView.OnScrollListener onScrollListener;
  private View pinnedHeader;
  private boolean scrollEnabled = true;
  private SectionsAdapter sectionsAdapter;
  private int sectionsCount;
  private int sectionsType;
  private Runnable selectChildRunnable;
  private Drawable selectorDrawable;
  private int selectorPosition;
  private Rect selectorRect = new Rect();
  private boolean selfOnLayout;
  private int startSection;
  private boolean wasPressed;
  
  public RecyclerListView(Context paramContext)
  {
    super(paramContext);
    setGlowColor(Theme.getColor("actionBarDefault"));
    this.selectorDrawable = Theme.getSelectorDrawable(false);
    this.selectorDrawable.setCallback(this);
    try
    {
      if (!gotAttributes)
      {
        attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
        gotAttributes = true;
      }
      TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(attributes);
      View.class.getDeclaredMethod("initializeScrollbars", new Class[] { TypedArray.class }).invoke(this, new Object[] { localTypedArray });
      localTypedArray.recycle();
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        FileLog.e(localThrowable);
      }
    }
    super.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      boolean scrollingByUser;
      
      public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
      {
        boolean bool = false;
        Object localObject;
        if ((paramAnonymousInt != 0) && (RecyclerListView.this.currentChildView != null))
        {
          if (RecyclerListView.this.selectChildRunnable != null)
          {
            AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
            RecyclerListView.access$802(RecyclerListView.this, null);
          }
          localObject = MotionEvent.obtain(0L, 0L, 3, 0.0F, 0.0F, 0);
        }
        try
        {
          RecyclerListView.this.gestureDetector.onTouchEvent((MotionEvent)localObject);
          RecyclerListView.this.currentChildView.onTouchEvent((MotionEvent)localObject);
          ((MotionEvent)localObject).recycle();
          localObject = RecyclerListView.this.currentChildView;
          RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, false);
          RecyclerListView.access$202(RecyclerListView.this, null);
          RecyclerListView.this.removeSelection((View)localObject, null);
          RecyclerListView.access$902(RecyclerListView.this, false);
          if (RecyclerListView.this.onScrollListener != null) {
            RecyclerListView.this.onScrollListener.onScrollStateChanged(paramAnonymousRecyclerView, paramAnonymousInt);
          }
          if ((paramAnonymousInt == 1) || (paramAnonymousInt == 2)) {
            bool = true;
          }
          this.scrollingByUser = bool;
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
      
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (RecyclerListView.this.onScrollListener != null) {
          RecyclerListView.this.onScrollListener.onScrolled(paramAnonymousRecyclerView, paramAnonymousInt1, paramAnonymousInt2);
        }
        Object localObject;
        int i;
        if (RecyclerListView.this.selectorPosition != -1)
        {
          RecyclerListView.this.selectorRect.offset(-paramAnonymousInt1, -paramAnonymousInt2);
          RecyclerListView.this.selectorDrawable.setBounds(RecyclerListView.this.selectorRect);
          RecyclerListView.this.invalidate();
          if (((this.scrollingByUser) && (RecyclerListView.this.fastScroll != null)) || ((RecyclerListView.this.sectionsType != 0) && (RecyclerListView.this.sectionsAdapter != null)))
          {
            paramAnonymousRecyclerView = RecyclerListView.this.getLayoutManager();
            if ((paramAnonymousRecyclerView instanceof LinearLayoutManager))
            {
              localObject = (LinearLayoutManager)paramAnonymousRecyclerView;
              if (((LinearLayoutManager)localObject).getOrientation() == 1)
              {
                i = ((LinearLayoutManager)localObject).findFirstVisibleItemPosition();
                if (i != -1) {
                  break label166;
                }
              }
            }
          }
        }
        label166:
        label398:
        label417:
        int k;
        label621:
        label684:
        label706:
        label761:
        do
        {
          do
          {
            do
            {
              return;
              RecyclerListView.this.selectorRect.setEmpty();
              break;
              if ((this.scrollingByUser) && (RecyclerListView.this.fastScroll != null))
              {
                paramAnonymousRecyclerView = RecyclerListView.this.getAdapter();
                if ((paramAnonymousRecyclerView instanceof RecyclerListView.FastScrollAdapter)) {
                  RecyclerListView.FastScroll.access$2300(RecyclerListView.this.fastScroll, i / paramAnonymousRecyclerView.getItemCount());
                }
              }
            } while (RecyclerListView.this.sectionsAdapter == null);
            if (RecyclerListView.this.sectionsType != 1) {
              break label761;
            }
            paramAnonymousInt2 = Math.abs(((LinearLayoutManager)localObject).findLastVisibleItemPosition() - i) + 1;
            RecyclerListView.this.headersCache.addAll(RecyclerListView.this.headers);
            RecyclerListView.this.headers.clear();
          } while (RecyclerListView.this.sectionsAdapter.getItemCount() == 0);
          int j;
          if ((RecyclerListView.this.currentFirst != i) || (RecyclerListView.this.currentVisible != paramAnonymousInt2))
          {
            RecyclerListView.access$2602(RecyclerListView.this, i);
            RecyclerListView.access$2702(RecyclerListView.this, paramAnonymousInt2);
            RecyclerListView.access$2802(RecyclerListView.this, 1);
            RecyclerListView.access$2902(RecyclerListView.this, RecyclerListView.this.sectionsAdapter.getSectionForPosition(i));
            paramAnonymousInt1 = RecyclerListView.this.sectionsAdapter.getCountForSection(RecyclerListView.this.startSection) + i - RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(i);
            if (paramAnonymousInt1 < i + paramAnonymousInt2) {}
          }
          else
          {
            paramAnonymousInt1 = i;
            paramAnonymousInt2 = RecyclerListView.this.startSection;
            if (paramAnonymousInt2 < RecyclerListView.this.startSection + RecyclerListView.this.sectionsCount)
            {
              paramAnonymousRecyclerView = null;
              if (!RecyclerListView.this.headersCache.isEmpty())
              {
                paramAnonymousRecyclerView = (View)RecyclerListView.this.headersCache.get(0);
                RecyclerListView.this.headersCache.remove(0);
              }
              paramAnonymousRecyclerView = RecyclerListView.this.getSectionHeaderView(paramAnonymousInt2, paramAnonymousRecyclerView);
              RecyclerListView.this.headers.add(paramAnonymousRecyclerView);
              j = RecyclerListView.this.sectionsAdapter.getCountForSection(paramAnonymousInt2);
              if (paramAnonymousInt2 != RecyclerListView.this.startSection) {
                break label706;
              }
              k = RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(paramAnonymousInt1);
              if (k != j - 1) {
                break label621;
              }
              paramAnonymousRecyclerView.setTag(Integer.valueOf(-paramAnonymousRecyclerView.getHeight()));
            }
          }
          for (;;)
          {
            paramAnonymousInt1 += j - RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(i);
            paramAnonymousInt2++;
            break label417;
            break;
            paramAnonymousInt1 += RecyclerListView.this.sectionsAdapter.getCountForSection(RecyclerListView.this.startSection + RecyclerListView.this.sectionsCount);
            RecyclerListView.access$2808(RecyclerListView.this);
            break label398;
            if (k == j - 2)
            {
              localObject = RecyclerListView.this.getChildAt(paramAnonymousInt1 - i);
              if (localObject != null) {}
              for (k = ((View)localObject).getTop();; k = -AndroidUtilities.dp(100.0F))
              {
                if (k >= 0) {
                  break label684;
                }
                paramAnonymousRecyclerView.setTag(Integer.valueOf(k));
                break;
              }
              paramAnonymousRecyclerView.setTag(Integer.valueOf(0));
            }
            else
            {
              paramAnonymousRecyclerView.setTag(Integer.valueOf(0));
            }
          }
          localObject = RecyclerListView.this.getChildAt(paramAnonymousInt1 - i);
          if (localObject != null) {
            paramAnonymousRecyclerView.setTag(Integer.valueOf(((View)localObject).getTop()));
          }
          for (;;)
          {
            paramAnonymousInt1 += j;
            break;
            paramAnonymousRecyclerView.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0F)));
          }
        } while ((RecyclerListView.this.sectionsType != 2) || (RecyclerListView.this.sectionsAdapter.getItemCount() == 0));
        paramAnonymousInt1 = RecyclerListView.this.sectionsAdapter.getSectionForPosition(i);
        if ((RecyclerListView.this.currentFirst != paramAnonymousInt1) || (RecyclerListView.this.pinnedHeader == null))
        {
          RecyclerListView.access$3102(RecyclerListView.this, RecyclerListView.this.getSectionHeaderView(paramAnonymousInt1, RecyclerListView.this.pinnedHeader));
          RecyclerListView.access$2602(RecyclerListView.this, paramAnonymousInt1);
        }
        paramAnonymousInt1 = RecyclerListView.this.sectionsAdapter.getCountForSection(paramAnonymousInt1);
        if (RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(i) == paramAnonymousInt1 - 1)
        {
          paramAnonymousRecyclerView = RecyclerListView.this.getChildAt(0);
          k = RecyclerListView.this.pinnedHeader.getHeight();
          paramAnonymousInt1 = 0;
          if (paramAnonymousRecyclerView != null)
          {
            paramAnonymousInt2 = paramAnonymousRecyclerView.getTop() + paramAnonymousRecyclerView.getHeight();
            if (paramAnonymousInt2 < k) {
              paramAnonymousInt1 = paramAnonymousInt2 - k;
            }
            label929:
            if (paramAnonymousInt1 >= 0) {
              break label968;
            }
            RecyclerListView.this.pinnedHeader.setTag(Integer.valueOf(paramAnonymousInt1));
          }
        }
        for (;;)
        {
          RecyclerListView.this.invalidate();
          break;
          paramAnonymousInt1 = -AndroidUtilities.dp(100.0F);
          break label929;
          label968:
          RecyclerListView.this.pinnedHeader.setTag(Integer.valueOf(0));
          continue;
          RecyclerListView.this.pinnedHeader.setTag(Integer.valueOf(0));
        }
      }
    });
    addOnItemTouchListener(new RecyclerListViewItemClickListener(paramContext));
  }
  
  private void checkIfEmpty()
  {
    int i = 0;
    if ((getAdapter() == null) || (this.emptyView == null))
    {
      if ((this.hiddenByEmptyView) && (getVisibility() != 0))
      {
        setVisibility(0);
        this.hiddenByEmptyView = false;
      }
      return;
    }
    int j;
    label53:
    View localView;
    if (getAdapter().getItemCount() == 0)
    {
      j = 1;
      localView = this.emptyView;
      if (j == 0) {
        break label100;
      }
    }
    label100:
    for (int k = 0;; k = 8)
    {
      localView.setVisibility(k);
      k = i;
      if (j != 0) {
        k = 4;
      }
      setVisibility(k);
      this.hiddenByEmptyView = true;
      break;
      j = 0;
      break label53;
    }
  }
  
  private void ensurePinnedHeaderLayout(View paramView, boolean paramBoolean)
  {
    int i;
    int j;
    if ((paramView.isLayoutRequested()) || (paramBoolean))
    {
      if (this.sectionsType != 1) {
        break label79;
      }
      ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
      i = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, NUM);
      j = View.MeasureSpec.makeMeasureSpec(localLayoutParams.width, NUM);
    }
    for (;;)
    {
      try
      {
        paramView.measure(j, i);
        paramView.layout(0, 0, paramView.getMeasuredWidth(), paramView.getMeasuredHeight());
        return;
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
        continue;
      }
      label79:
      if (this.sectionsType == 2)
      {
        j = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM);
        i = View.MeasureSpec.makeMeasureSpec(0, 0);
        try
        {
          paramView.measure(j, i);
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
        }
      }
    }
  }
  
  private int[] getDrawableStateForSelector()
  {
    int[] arrayOfInt = onCreateDrawableState(1);
    arrayOfInt[(arrayOfInt.length - 1)] = 16842919;
    return arrayOfInt;
  }
  
  private View getSectionHeaderView(int paramInt, View paramView)
  {
    if (paramView == null) {}
    for (int i = 1;; i = 0)
    {
      paramView = this.sectionsAdapter.getSectionHeaderView(paramInt, paramView);
      if (i != 0) {
        ensurePinnedHeaderLayout(paramView, false);
      }
      return paramView;
    }
  }
  
  private void positionSelector(int paramInt, View paramView)
  {
    positionSelector(paramInt, paramView, false, -1.0F, -1.0F);
  }
  
  private void positionSelector(int paramInt, View paramView, boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    if (this.selectorDrawable == null) {
      return;
    }
    if (paramInt != this.selectorPosition) {}
    for (int i = 1;; i = 0)
    {
      if (paramInt != -1) {
        this.selectorPosition = paramInt;
      }
      this.selectorRect.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
      boolean bool = paramView.isEnabled();
      if (this.isChildViewEnabled != bool) {
        this.isChildViewEnabled = bool;
      }
      if (i != 0)
      {
        this.selectorDrawable.setVisible(false, false);
        this.selectorDrawable.setState(StateSet.NOTHING);
      }
      this.selectorDrawable.setBounds(this.selectorRect);
      if ((i != 0) && (getVisibility() == 0)) {
        this.selectorDrawable.setVisible(true, false);
      }
      if ((Build.VERSION.SDK_INT < 21) || (!paramBoolean)) {
        break;
      }
      this.selectorDrawable.setHotspot(paramFloat1, paramFloat2);
      break;
    }
  }
  
  private void removeSelection(View paramView, MotionEvent paramMotionEvent)
  {
    if (paramView == null) {
      return;
    }
    if ((paramView != null) && (paramView.isEnabled()))
    {
      positionSelector(this.currentChildPosition, paramView);
      if (this.selectorDrawable != null)
      {
        paramView = this.selectorDrawable.getCurrent();
        if ((paramView != null) && ((paramView instanceof TransitionDrawable))) {
          ((TransitionDrawable)paramView).resetTransition();
        }
        if ((paramMotionEvent != null) && (Build.VERSION.SDK_INT >= 21)) {
          this.selectorDrawable.setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
        }
      }
    }
    for (;;)
    {
      updateSelectorState();
      break;
      this.selectorRect.setEmpty();
    }
  }
  
  private void updateSelectorState()
  {
    if ((this.selectorDrawable != null) && (this.selectorDrawable.isStateful()))
    {
      if (this.currentChildView == null) {
        break label47;
      }
      if (this.selectorDrawable.setState(getDrawableStateForSelector())) {
        invalidateDrawable(this.selectorDrawable);
      }
    }
    for (;;)
    {
      return;
      label47:
      this.selectorDrawable.setState(StateSet.NOTHING);
    }
  }
  
  protected boolean allowSelectChildAtPosition(float paramFloat1, float paramFloat2)
  {
    return true;
  }
  
  public boolean canScrollVertically(int paramInt)
  {
    if ((this.scrollEnabled) && (super.canScrollVertically(paramInt))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void cancelClickRunnables(boolean paramBoolean)
  {
    if (this.selectChildRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.selectChildRunnable);
      this.selectChildRunnable = null;
    }
    if (this.currentChildView != null)
    {
      View localView = this.currentChildView;
      if (paramBoolean) {
        onChildPressed(this.currentChildView, false);
      }
      this.currentChildView = null;
      removeSelection(localView, null);
    }
    if (this.clickRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.clickRunnable);
      this.clickRunnable = null;
    }
    this.interceptedByChild = false;
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    float f = 0.0F;
    super.dispatchDraw(paramCanvas);
    if (this.sectionsType == 1) {
      if ((this.sectionsAdapter != null) && (!this.headers.isEmpty())) {}
    }
    for (;;)
    {
      return;
      int i = 0;
      if (i < this.headers.size())
      {
        View localView = (View)this.headers.get(i);
        int j = paramCanvas.save();
        int k = ((Integer)localView.getTag()).intValue();
        if (LocaleController.isRTL) {}
        for (f = getWidth() - localView.getWidth();; f = 0.0F)
        {
          paramCanvas.translate(f, k);
          paramCanvas.clipRect(0, 0, getWidth(), localView.getMeasuredHeight());
          localView.draw(paramCanvas);
          paramCanvas.restoreToCount(j);
          i++;
          break;
        }
        if (this.sectionsType == 2)
        {
          if ((this.sectionsAdapter == null) || (this.pinnedHeader == null)) {
            continue;
          }
          i = paramCanvas.save();
          k = ((Integer)this.pinnedHeader.getTag()).intValue();
          if (LocaleController.isRTL) {
            f = getWidth() - this.pinnedHeader.getWidth();
          }
          paramCanvas.translate(f, k);
          paramCanvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
          this.pinnedHeader.draw(paramCanvas);
          paramCanvas.restoreToCount(i);
        }
      }
      if (!this.selectorRect.isEmpty())
      {
        this.selectorDrawable.setBounds(this.selectorRect);
        this.selectorDrawable.draw(paramCanvas);
      }
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    updateSelectorState();
  }
  
  public View getEmptyView()
  {
    return this.emptyView;
  }
  
  public ArrayList<View> getHeaders()
  {
    return this.headers;
  }
  
  public ArrayList<View> getHeadersCache()
  {
    return this.headersCache;
  }
  
  public View getPinnedHeader()
  {
    return this.pinnedHeader;
  }
  
  protected View getPressedChildView()
  {
    return this.currentChildView;
  }
  
  public int[] getResourceDeclareStyleableIntArray(String paramString1, String paramString2)
  {
    try
    {
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      paramString1 = Class.forName(paramString1 + ".R$styleable").getField(paramString2);
      if (paramString1 == null) {
        break label46;
      }
      paramString1 = (int[])paramString1.get(null);
    }
    catch (Throwable paramString1)
    {
      for (;;)
      {
        label46:
        paramString1 = null;
      }
    }
    return paramString1;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void invalidateViews()
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      getChildAt(j).invalidate();
    }
  }
  
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.selectorDrawable != null) {
      this.selectorDrawable.jumpToCurrentState();
    }
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if ((this.fastScroll != null) && (this.fastScroll.getParent() != getParent()))
    {
      ViewGroup localViewGroup = (ViewGroup)this.fastScroll.getParent();
      if (localViewGroup != null) {
        localViewGroup.removeView(this.fastScroll);
      }
      ((ViewGroup)getParent()).addView(this.fastScroll);
    }
  }
  
  public void onChildAttachedToWindow(View paramView)
  {
    if ((getAdapter() instanceof SelectionAdapter))
    {
      RecyclerView.ViewHolder localViewHolder = findContainingViewHolder(paramView);
      if (localViewHolder != null) {
        paramView.setEnabled(((SelectionAdapter)getAdapter()).isEnabled(localViewHolder));
      }
    }
    for (;;)
    {
      super.onChildAttachedToWindow(paramView);
      return;
      paramView.setEnabled(false);
    }
  }
  
  protected void onChildPressed(View paramView, boolean paramBoolean)
  {
    paramView.setPressed(paramBoolean);
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.selectorPosition = -1;
    this.selectorRect.setEmpty();
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    if (!isEnabled()) {}
    for (;;)
    {
      return bool;
      if (this.disallowInterceptTouchEvents) {
        requestDisallowInterceptTouchEvent(true);
      }
      if (((this.onInterceptTouchListener != null) && (this.onInterceptTouchListener.onInterceptTouchEvent(paramMotionEvent))) || (super.onInterceptTouchEvent(paramMotionEvent))) {
        bool = true;
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.fastScroll != null)
    {
      this.selfOnLayout = true;
      if (!LocaleController.isRTL) {
        break label60;
      }
      this.fastScroll.layout(0, paramInt2, this.fastScroll.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + paramInt2);
    }
    for (;;)
    {
      this.selfOnLayout = false;
      return;
      label60:
      paramInt1 = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
      this.fastScroll.layout(paramInt1, paramInt2, this.fastScroll.getMeasuredWidth() + paramInt1, this.fastScroll.getMeasuredHeight() + paramInt2);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (this.fastScroll != null) {
      this.fastScroll.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0F), NUM), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), NUM));
    }
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.sectionsType == 1) {
      if ((this.sectionsAdapter != null) && (!this.headers.isEmpty())) {}
    }
    for (;;)
    {
      return;
      for (paramInt1 = 0; paramInt1 < this.headers.size(); paramInt1++) {
        ensurePinnedHeaderLayout((View)this.headers.get(paramInt1), true);
      }
      continue;
      if ((this.sectionsType == 2) && (this.sectionsAdapter != null) && (this.pinnedHeader != null)) {
        ensurePinnedHeaderLayout(this.pinnedHeader, true);
      }
    }
  }
  
  public void setAdapter(RecyclerView.Adapter paramAdapter)
  {
    RecyclerView.Adapter localAdapter = getAdapter();
    if (localAdapter != null) {
      localAdapter.unregisterAdapterDataObserver(this.observer);
    }
    if (this.headers != null)
    {
      this.headers.clear();
      this.headersCache.clear();
    }
    this.selectorPosition = -1;
    this.selectorRect.setEmpty();
    this.pinnedHeader = null;
    if ((paramAdapter instanceof SectionsAdapter)) {}
    for (this.sectionsAdapter = ((SectionsAdapter)paramAdapter);; this.sectionsAdapter = null)
    {
      super.setAdapter(paramAdapter);
      if (paramAdapter != null) {
        paramAdapter.registerAdapterDataObserver(this.observer);
      }
      checkIfEmpty();
      return;
    }
  }
  
  public void setDisallowInterceptTouchEvents(boolean paramBoolean)
  {
    this.disallowInterceptTouchEvents = paramBoolean;
  }
  
  public void setEmptyView(View paramView)
  {
    if (this.emptyView == paramView) {}
    for (;;)
    {
      return;
      this.emptyView = paramView;
      checkIfEmpty();
    }
  }
  
  public void setFastScrollEnabled()
  {
    this.fastScroll = new FastScroll(getContext());
    if (getParent() != null) {
      ((ViewGroup)getParent()).addView(this.fastScroll);
    }
  }
  
  public void setFastScrollVisible(boolean paramBoolean)
  {
    if (this.fastScroll == null) {
      return;
    }
    FastScroll localFastScroll = this.fastScroll;
    if (paramBoolean) {}
    for (int i = 0;; i = 8)
    {
      localFastScroll.setVisibility(i);
      break;
    }
  }
  
  public void setInstantClick(boolean paramBoolean)
  {
    this.instantClick = paramBoolean;
  }
  
  public void setListSelectorColor(int paramInt)
  {
    Theme.setSelectorDrawableColor(this.selectorDrawable, paramInt, true);
  }
  
  public void setOnInterceptTouchListener(OnInterceptTouchListener paramOnInterceptTouchListener)
  {
    this.onInterceptTouchListener = paramOnInterceptTouchListener;
  }
  
  public void setOnItemClickListener(OnItemClickListener paramOnItemClickListener)
  {
    this.onItemClickListener = paramOnItemClickListener;
  }
  
  public void setOnItemClickListener(OnItemClickListenerExtended paramOnItemClickListenerExtended)
  {
    this.onItemClickListenerExtended = paramOnItemClickListenerExtended;
  }
  
  public void setOnItemLongClickListener(OnItemLongClickListener paramOnItemLongClickListener)
  {
    this.onItemLongClickListener = paramOnItemLongClickListener;
  }
  
  public void setOnItemLongClickListener(OnItemLongClickListenerExtended paramOnItemLongClickListenerExtended)
  {
    this.onItemLongClickListenerExtended = paramOnItemLongClickListenerExtended;
  }
  
  public void setOnScrollListener(RecyclerView.OnScrollListener paramOnScrollListener)
  {
    this.onScrollListener = paramOnScrollListener;
  }
  
  public void setScrollEnabled(boolean paramBoolean)
  {
    this.scrollEnabled = paramBoolean;
  }
  
  public void setSectionsType(int paramInt)
  {
    this.sectionsType = paramInt;
    if (this.sectionsType == 1)
    {
      this.headers = new ArrayList();
      this.headersCache = new ArrayList();
    }
  }
  
  public void setVerticalScrollBarEnabled(boolean paramBoolean)
  {
    if (attributes != null) {
      super.setVerticalScrollBarEnabled(paramBoolean);
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt != 0) {
      this.hiddenByEmptyView = false;
    }
  }
  
  public void stopScroll()
  {
    try
    {
      super.stopScroll();
      return;
    }
    catch (NullPointerException localNullPointerException)
    {
      for (;;) {}
    }
  }
  
  public void updateFastScrollColors()
  {
    if (this.fastScroll != null) {
      this.fastScroll.updateColors();
    }
  }
  
  public boolean verifyDrawable(Drawable paramDrawable)
  {
    if ((this.selectorDrawable == paramDrawable) || (super.verifyDrawable(paramDrawable))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private class FastScroll
    extends View
  {
    private float bubbleProgress;
    private int[] colors = new int[6];
    private String currentLetter;
    private long lastUpdateTime;
    private float lastY;
    private StaticLayout letterLayout;
    private TextPaint letterPaint = new TextPaint(1);
    private StaticLayout oldLetterLayout;
    private Paint paint = new Paint(1);
    private Path path = new Path();
    private boolean pressed;
    private float progress;
    private float[] radii = new float[8];
    private RectF rect = new RectF();
    private int scrollX;
    private float startDy;
    private float textX;
    private float textY;
    
    public FastScroll(Context paramContext)
    {
      super();
      this.letterPaint.setTextSize(AndroidUtilities.dp(45.0F));
      for (int i = 0; i < 8; i++) {
        this.radii[i] = AndroidUtilities.dp(44.0F);
      }
      if (LocaleController.isRTL) {}
      for (i = AndroidUtilities.dp(10.0F);; i = AndroidUtilities.dp(117.0F))
      {
        this.scrollX = i;
        updateColors();
        return;
      }
    }
    
    private void getCurrentLetter()
    {
      Object localObject1 = RecyclerListView.this.getLayoutManager();
      if ((localObject1 instanceof LinearLayoutManager))
      {
        localObject1 = (LinearLayoutManager)localObject1;
        if (((LinearLayoutManager)localObject1).getOrientation() == 1)
        {
          Object localObject2 = RecyclerListView.this.getAdapter();
          if ((localObject2 instanceof RecyclerListView.FastScrollAdapter))
          {
            localObject2 = (RecyclerListView.FastScrollAdapter)localObject2;
            int i = ((RecyclerListView.FastScrollAdapter)localObject2).getPositionForScrollProgress(this.progress);
            ((LinearLayoutManager)localObject1).scrollToPositionWithOffset(i, 0);
            localObject1 = ((RecyclerListView.FastScrollAdapter)localObject2).getLetter(i);
            if (localObject1 != null) {
              break label94;
            }
            if (this.letterLayout != null) {
              this.oldLetterLayout = this.letterLayout;
            }
            this.letterLayout = null;
          }
        }
      }
      label94:
      do
      {
        return;
        if (((String)localObject1).equals(this.currentLetter)) {
          break;
        }
        this.letterLayout = new StaticLayout((CharSequence)localObject1, this.letterPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.oldLetterLayout = null;
      } while (this.letterLayout.getLineCount() <= 0);
      this.letterLayout.getLineWidth(0);
      this.letterLayout.getLineLeft(0);
      if (LocaleController.isRTL) {}
      for (this.textX = (AndroidUtilities.dp(10.0F) + (AndroidUtilities.dp(88.0F) - this.letterLayout.getLineWidth(0)) / 2.0F - this.letterLayout.getLineLeft(0));; this.textX = ((AndroidUtilities.dp(88.0F) - this.letterLayout.getLineWidth(0)) / 2.0F - this.letterLayout.getLineLeft(0)))
      {
        this.textY = ((AndroidUtilities.dp(88.0F) - this.letterLayout.getHeight()) / 2);
        break;
        break;
      }
    }
    
    private void setProgress(float paramFloat)
    {
      this.progress = paramFloat;
      invalidate();
    }
    
    private void updateColors()
    {
      int i = Theme.getColor("fastScrollInactive");
      int j = Theme.getColor("fastScrollActive");
      this.paint.setColor(i);
      this.letterPaint.setColor(Theme.getColor("fastScrollText"));
      this.colors[0] = Color.red(i);
      this.colors[1] = Color.red(j);
      this.colors[2] = Color.green(i);
      this.colors[3] = Color.green(j);
      this.colors[4] = Color.blue(i);
      this.colors[5] = Color.blue(j);
      invalidate();
    }
    
    public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!RecyclerListView.this.selfOnLayout) {}
      for (;;)
      {
        return;
        super.layout(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      this.paint.setColor(Color.argb(255, this.colors[0] + (int)((this.colors[1] - this.colors[0]) * this.bubbleProgress), this.colors[2] + (int)((this.colors[3] - this.colors[2]) * this.bubbleProgress), this.colors[4] + (int)((this.colors[5] - this.colors[4]) * this.bubbleProgress)));
      int i = (int)Math.ceil((getMeasuredHeight() - AndroidUtilities.dp(54.0F)) * this.progress);
      this.rect.set(this.scrollX, AndroidUtilities.dp(12.0F) + i, this.scrollX + AndroidUtilities.dp(5.0F), AndroidUtilities.dp(42.0F) + i);
      paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0F), AndroidUtilities.dp(2.0F), this.paint);
      float f1;
      float f2;
      Object localObject;
      if ((this.pressed) || (this.bubbleProgress != 0.0F))
      {
        this.paint.setAlpha((int)(255.0F * this.bubbleProgress));
        int j = AndroidUtilities.dp(30.0F);
        int k = i - AndroidUtilities.dp(46.0F);
        f1 = 0.0F;
        int m = k;
        if (k <= AndroidUtilities.dp(12.0F))
        {
          f1 = AndroidUtilities.dp(12.0F) - k;
          m = AndroidUtilities.dp(12.0F);
        }
        paramCanvas.translate(AndroidUtilities.dp(10.0F), m);
        if (f1 > AndroidUtilities.dp(29.0F)) {
          break label733;
        }
        f2 = AndroidUtilities.dp(44.0F);
        f1 = AndroidUtilities.dp(4.0F) + f1 / AndroidUtilities.dp(29.0F) * AndroidUtilities.dp(40.0F);
        if (((LocaleController.isRTL) && ((this.radii[0] != f2) || (this.radii[6] != f1))) || ((!LocaleController.isRTL) && ((this.radii[2] != f2) || (this.radii[4] != f1))))
        {
          if (!LocaleController.isRTL) {
            break label794;
          }
          localObject = this.radii;
          this.radii[1] = f2;
          localObject[0] = f2;
          localObject = this.radii;
          this.radii[7] = f1;
          localObject[6] = f1;
          label429:
          this.path.reset();
          localObject = this.rect;
          if (!LocaleController.isRTL) {
            break label837;
          }
          f1 = AndroidUtilities.dp(10.0F);
          label456:
          if (!LocaleController.isRTL) {
            break label843;
          }
          f2 = 98.0F;
          label467:
          ((RectF)localObject).set(f1, 0.0F, AndroidUtilities.dp(f2), AndroidUtilities.dp(88.0F));
          this.path.addRoundRect(this.rect, this.radii, Path.Direction.CW);
          this.path.close();
        }
        if (this.letterLayout == null) {
          break label850;
        }
        localObject = this.letterLayout;
        label525:
        if (localObject != null)
        {
          paramCanvas.save();
          paramCanvas.scale(this.bubbleProgress, this.bubbleProgress, this.scrollX, i + j - m);
          paramCanvas.drawPath(this.path, this.paint);
          paramCanvas.translate(this.textX, this.textY);
          ((StaticLayout)localObject).draw(paramCanvas);
          paramCanvas.restore();
        }
      }
      long l3;
      if (((this.pressed) && (this.letterLayout != null) && (this.bubbleProgress < 1.0F)) || (((!this.pressed) || (this.letterLayout == null)) && (this.bubbleProgress > 0.0F)))
      {
        long l1 = System.currentTimeMillis();
        long l2 = l1 - this.lastUpdateTime;
        if (l2 >= 0L)
        {
          l3 = l2;
          if (l2 <= 17L) {}
        }
        else
        {
          l3 = 17L;
        }
        this.lastUpdateTime = l1;
        invalidate();
        if ((!this.pressed) || (this.letterLayout == null)) {
          break label859;
        }
        this.bubbleProgress += (float)l3 / 120.0F;
        if (this.bubbleProgress > 1.0F) {
          this.bubbleProgress = 1.0F;
        }
      }
      for (;;)
      {
        return;
        label733:
        float f3 = AndroidUtilities.dp(29.0F);
        f2 = AndroidUtilities.dp(44.0F);
        f3 = AndroidUtilities.dp(4.0F) + (1.0F - (f1 - f3) / AndroidUtilities.dp(29.0F)) * AndroidUtilities.dp(40.0F);
        f1 = f2;
        f2 = f3;
        break;
        label794:
        localObject = this.radii;
        this.radii[3] = f2;
        localObject[2] = f2;
        localObject = this.radii;
        this.radii[5] = f1;
        localObject[4] = f1;
        break label429;
        label837:
        f1 = 0.0F;
        break label456;
        label843:
        f2 = 88.0F;
        break label467;
        label850:
        localObject = this.oldLetterLayout;
        break label525;
        label859:
        this.bubbleProgress -= (float)l3 / 120.0F;
        if (this.bubbleProgress < 0.0F) {
          this.bubbleProgress = 0.0F;
        }
      }
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      setMeasuredDimension(AndroidUtilities.dp(132.0F), View.MeasureSpec.getSize(paramInt2));
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool1 = true;
      boolean bool2;
      switch (paramMotionEvent.getAction())
      {
      default: 
        bool2 = super.onTouchEvent(paramMotionEvent);
      }
      for (;;)
      {
        return bool2;
        float f1 = paramMotionEvent.getX();
        this.lastY = paramMotionEvent.getY();
        float f2 = (float)Math.ceil((getMeasuredHeight() - AndroidUtilities.dp(54.0F)) * this.progress) + AndroidUtilities.dp(12.0F);
        if (((LocaleController.isRTL) && (f1 > AndroidUtilities.dp(25.0F))) || ((!LocaleController.isRTL) && (f1 < AndroidUtilities.dp(107.0F))) || (this.lastY < f2) || (this.lastY > AndroidUtilities.dp(30.0F) + f2))
        {
          bool2 = false;
        }
        else
        {
          this.startDy = (this.lastY - f2);
          this.pressed = true;
          this.lastUpdateTime = System.currentTimeMillis();
          getCurrentLetter();
          invalidate();
          bool2 = bool1;
          continue;
          bool2 = bool1;
          if (this.pressed)
          {
            float f3 = paramMotionEvent.getY();
            f1 = AndroidUtilities.dp(12.0F) + this.startDy;
            f2 = getMeasuredHeight() - AndroidUtilities.dp(42.0F) + this.startDy;
            if (f3 < f1)
            {
              label248:
              f2 = this.lastY;
              this.lastY = f1;
              this.progress += (f1 - f2) / (getMeasuredHeight() - AndroidUtilities.dp(54.0F));
              if (this.progress >= 0.0F) {
                break label332;
              }
              this.progress = 0.0F;
            }
            for (;;)
            {
              getCurrentLetter();
              invalidate();
              bool2 = bool1;
              break;
              f1 = f3;
              if (f3 <= f2) {
                break label248;
              }
              f1 = f2;
              break label248;
              label332:
              if (this.progress > 1.0F) {
                this.progress = 1.0F;
              }
            }
            this.pressed = false;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
            bool2 = bool1;
          }
        }
      }
    }
  }
  
  public static abstract class FastScrollAdapter
    extends RecyclerListView.SelectionAdapter
  {
    public abstract String getLetter(int paramInt);
    
    public abstract int getPositionForScrollProgress(float paramFloat);
  }
  
  public static class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
  
  public static abstract interface OnInterceptTouchListener
  {
    public abstract boolean onInterceptTouchEvent(MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnItemClickListener
  {
    public abstract void onItemClick(View paramView, int paramInt);
  }
  
  public static abstract interface OnItemClickListenerExtended
  {
    public abstract void onItemClick(View paramView, int paramInt, float paramFloat1, float paramFloat2);
  }
  
  public static abstract interface OnItemLongClickListener
  {
    public abstract boolean onItemClick(View paramView, int paramInt);
  }
  
  public static abstract interface OnItemLongClickListenerExtended
  {
    public abstract boolean onItemClick(View paramView, int paramInt, float paramFloat1, float paramFloat2);
  }
  
  private class RecyclerListViewItemClickListener
    implements RecyclerView.OnItemTouchListener
  {
    public RecyclerListViewItemClickListener(Context paramContext)
    {
      RecyclerListView.access$102(RecyclerListView.this, new GestureDetector(paramContext, new GestureDetector.SimpleOnGestureListener()
      {
        public void onLongPress(MotionEvent paramAnonymousMotionEvent)
        {
          if ((RecyclerListView.this.currentChildView == null) || (RecyclerListView.this.currentChildPosition == -1) || ((RecyclerListView.this.onItemLongClickListener == null) && (RecyclerListView.this.onItemLongClickListenerExtended == null))) {}
          for (;;)
          {
            return;
            View localView = RecyclerListView.this.currentChildView;
            if (RecyclerListView.this.onItemLongClickListener != null)
            {
              if (RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                localView.performHapticFeedback(0);
              }
            }
            else if ((RecyclerListView.this.onItemLongClickListenerExtended != null) && (RecyclerListView.this.onItemLongClickListenerExtended.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition, paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY()))) {
              localView.performHapticFeedback(0);
            }
          }
        }
        
        public boolean onSingleTapUp(MotionEvent paramAnonymousMotionEvent)
        {
          final View localView;
          final int i;
          final float f1;
          final float f2;
          if ((RecyclerListView.this.currentChildView != null) && ((RecyclerListView.this.onItemClickListener != null) || (RecyclerListView.this.onItemClickListenerExtended != null)))
          {
            RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, true);
            localView = RecyclerListView.this.currentChildView;
            i = RecyclerListView.this.currentChildPosition;
            f1 = paramAnonymousMotionEvent.getX();
            f2 = paramAnonymousMotionEvent.getY();
            if ((RecyclerListView.this.instantClick) && (i != -1))
            {
              localView.playSoundEffect(0);
              if (RecyclerListView.this.onItemClickListener == null) {
                break label265;
              }
              RecyclerListView.this.onItemClickListener.onItemClick(localView, i);
            }
          }
          for (;;)
          {
            AndroidUtilities.runOnUIThread(RecyclerListView.access$702(RecyclerListView.this, new Runnable()
            {
              public void run()
              {
                if (this == RecyclerListView.this.clickRunnable) {
                  RecyclerListView.access$702(RecyclerListView.this, null);
                }
                if (localView != null)
                {
                  RecyclerListView.this.onChildPressed(localView, false);
                  if (!RecyclerListView.this.instantClick)
                  {
                    localView.playSoundEffect(0);
                    if (i != -1)
                    {
                      if (RecyclerListView.this.onItemClickListener == null) {
                        break label132;
                      }
                      RecyclerListView.this.onItemClickListener.onItemClick(localView, i);
                    }
                  }
                }
                for (;;)
                {
                  return;
                  label132:
                  if (RecyclerListView.this.onItemClickListenerExtended != null) {
                    RecyclerListView.this.onItemClickListenerExtended.onItemClick(localView, i, f1, f2);
                  }
                }
              }
            }), ViewConfiguration.getPressedStateDuration());
            if (RecyclerListView.this.selectChildRunnable != null)
            {
              localView = RecyclerListView.this.currentChildView;
              AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
              RecyclerListView.access$802(RecyclerListView.this, null);
              RecyclerListView.access$202(RecyclerListView.this, null);
              RecyclerListView.access$902(RecyclerListView.this, false);
              RecyclerListView.this.removeSelection(localView, paramAnonymousMotionEvent);
            }
            return true;
            label265:
            if (RecyclerListView.this.onItemClickListenerExtended != null) {
              RecyclerListView.this.onItemClickListenerExtended.onItemClick(localView, i, f1, f2);
            }
          }
        }
      }));
    }
    
    public boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
      int i = paramMotionEvent.getActionMasked();
      int j;
      if (RecyclerListView.this.getScrollState() == 0) {
        j = 1;
      }
      for (;;)
      {
        int k;
        if (((i == 0) || (i == 5)) && (RecyclerListView.this.currentChildView == null) && (j != 0))
        {
          float f1 = paramMotionEvent.getX();
          float f2 = paramMotionEvent.getY();
          if (RecyclerListView.this.allowSelectChildAtPosition(f1, f2)) {
            RecyclerListView.access$202(RecyclerListView.this, paramRecyclerView.findChildViewUnder(f1, f2));
          }
          if ((RecyclerListView.this.currentChildView instanceof ViewGroup))
          {
            f1 = paramMotionEvent.getX() - RecyclerListView.this.currentChildView.getLeft();
            f2 = paramMotionEvent.getY() - RecyclerListView.this.currentChildView.getTop();
            ViewGroup localViewGroup = (ViewGroup)RecyclerListView.this.currentChildView;
            k = localViewGroup.getChildCount() - 1;
            if (k >= 0)
            {
              View localView = localViewGroup.getChildAt(k);
              if ((f1 < localView.getLeft()) || (f1 > localView.getRight()) || (f2 < localView.getTop()) || (f2 > localView.getBottom()) || (!localView.isClickable())) {
                break label573;
              }
              RecyclerListView.access$202(RecyclerListView.this, null);
            }
          }
          RecyclerListView.access$502(RecyclerListView.this, -1);
          if (RecyclerListView.this.currentChildView != null)
          {
            RecyclerListView.access$502(RecyclerListView.this, paramRecyclerView.getChildPosition(RecyclerListView.this.currentChildView));
            paramRecyclerView = MotionEvent.obtain(0L, 0L, paramMotionEvent.getActionMasked(), paramMotionEvent.getX() - RecyclerListView.this.currentChildView.getLeft(), paramMotionEvent.getY() - RecyclerListView.this.currentChildView.getTop(), 0);
            if (RecyclerListView.this.currentChildView.onTouchEvent(paramRecyclerView)) {
              RecyclerListView.access$902(RecyclerListView.this, true);
            }
            paramRecyclerView.recycle();
          }
        }
        if ((RecyclerListView.this.currentChildView != null) && (!RecyclerListView.this.interceptedByChild) && (paramMotionEvent != null)) {}
        try
        {
          RecyclerListView.this.gestureDetector.onTouchEvent(paramMotionEvent);
          if ((i == 0) || (i == 5)) {
            if ((!RecyclerListView.this.interceptedByChild) && (RecyclerListView.this.currentChildView != null))
            {
              RecyclerListView.access$802(RecyclerListView.this, new Runnable()
              {
                public void run()
                {
                  if ((RecyclerListView.this.selectChildRunnable != null) && (RecyclerListView.this.currentChildView != null))
                  {
                    RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, true);
                    RecyclerListView.access$802(RecyclerListView.this, null);
                  }
                }
              });
              AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, ViewConfiguration.getTapTimeout());
              if (!RecyclerListView.this.currentChildView.isEnabled()) {
                break label597;
              }
              RecyclerListView.this.positionSelector(RecyclerListView.this.currentChildPosition, RecyclerListView.this.currentChildView);
              if (RecyclerListView.this.selectorDrawable != null)
              {
                paramRecyclerView = RecyclerListView.this.selectorDrawable.getCurrent();
                if ((paramRecyclerView != null) && ((paramRecyclerView instanceof TransitionDrawable)))
                {
                  if ((RecyclerListView.this.onItemLongClickListener == null) && (RecyclerListView.this.onItemClickListenerExtended == null)) {
                    break label587;
                  }
                  ((TransitionDrawable)paramRecyclerView).startTransition(ViewConfiguration.getLongPressTimeout());
                }
                if (Build.VERSION.SDK_INT >= 21) {
                  RecyclerListView.this.selectorDrawable.setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
                }
              }
              RecyclerListView.this.updateSelectorState();
            }
            else
            {
              return false;
              j = 0;
              continue;
              label573:
              k--;
            }
          }
        }
        catch (Exception paramRecyclerView)
        {
          for (;;)
          {
            FileLog.e(paramRecyclerView);
            continue;
            label587:
            ((TransitionDrawable)paramRecyclerView).resetTransition();
            continue;
            label597:
            RecyclerListView.this.selectorRect.setEmpty();
            continue;
            if (((i == 1) || (i == 6) || (i == 3) || (j == 0)) && (RecyclerListView.this.currentChildView != null))
            {
              if (RecyclerListView.this.selectChildRunnable != null)
              {
                AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                RecyclerListView.access$802(RecyclerListView.this, null);
              }
              paramRecyclerView = RecyclerListView.this.currentChildView;
              RecyclerListView.this.onChildPressed(RecyclerListView.this.currentChildView, false);
              RecyclerListView.access$202(RecyclerListView.this, null);
              RecyclerListView.access$902(RecyclerListView.this, false);
              RecyclerListView.this.removeSelection(paramRecyclerView, paramMotionEvent);
            }
          }
        }
      }
    }
    
    public void onRequestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      RecyclerListView.this.cancelClickRunnables(true);
    }
    
    public void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent) {}
  }
  
  public static abstract class SectionsAdapter
    extends RecyclerListView.FastScrollAdapter
  {
    private int count;
    private SparseIntArray sectionCache;
    private int sectionCount;
    private SparseIntArray sectionCountCache;
    private SparseIntArray sectionPositionCache;
    
    public SectionsAdapter()
    {
      cleanupCache();
    }
    
    private void cleanupCache()
    {
      this.sectionCache = new SparseIntArray();
      this.sectionPositionCache = new SparseIntArray();
      this.sectionCountCache = new SparseIntArray();
      this.count = -1;
      this.sectionCount = -1;
    }
    
    private int internalGetCountForSection(int paramInt)
    {
      int i = this.sectionCountCache.get(paramInt, Integer.MAX_VALUE);
      if (i != Integer.MAX_VALUE) {}
      for (paramInt = i;; paramInt = i)
      {
        return paramInt;
        i = getCountForSection(paramInt);
        this.sectionCountCache.put(paramInt, i);
      }
    }
    
    private int internalGetSectionCount()
    {
      if (this.sectionCount >= 0) {}
      for (int i = this.sectionCount;; i = this.sectionCount)
      {
        return i;
        this.sectionCount = getSectionCount();
      }
    }
    
    public abstract int getCountForSection(int paramInt);
    
    public final Object getItem(int paramInt)
    {
      return getItem(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt));
    }
    
    public abstract Object getItem(int paramInt1, int paramInt2);
    
    public final int getItemCount()
    {
      if (this.count >= 0) {}
      for (int i = this.count;; i = this.count)
      {
        return i;
        this.count = 0;
        for (i = 0; i < internalGetSectionCount(); i++) {
          this.count += internalGetCountForSection(i);
        }
      }
    }
    
    public final int getItemViewType(int paramInt)
    {
      return getItemViewType(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt));
    }
    
    public abstract int getItemViewType(int paramInt1, int paramInt2);
    
    public int getPositionInSectionForPosition(int paramInt)
    {
      int i = this.sectionPositionCache.get(paramInt, Integer.MAX_VALUE);
      if (i != Integer.MAX_VALUE) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        i = 0;
        for (int j = 0;; j++)
        {
          if (j >= internalGetSectionCount()) {
            break label80;
          }
          int k = i + internalGetCountForSection(j);
          if ((paramInt >= i) && (paramInt < k))
          {
            i = paramInt - i;
            this.sectionPositionCache.put(paramInt, i);
            paramInt = i;
            break;
          }
          i = k;
        }
        label80:
        paramInt = -1;
      }
    }
    
    public abstract int getSectionCount();
    
    public final int getSectionForPosition(int paramInt)
    {
      int i = this.sectionCache.get(paramInt, Integer.MAX_VALUE);
      if (i != Integer.MAX_VALUE) {
        paramInt = i;
      }
      for (;;)
      {
        return paramInt;
        int j = 0;
        for (i = 0;; i++)
        {
          if (i >= internalGetSectionCount()) {
            break label76;
          }
          int k = j + internalGetCountForSection(i);
          if ((paramInt >= j) && (paramInt < k))
          {
            this.sectionCache.put(paramInt, i);
            paramInt = i;
            break;
          }
          j = k;
        }
        label76:
        paramInt = -1;
      }
    }
    
    public abstract View getSectionHeaderView(int paramInt, View paramView);
    
    public abstract boolean isEnabled(int paramInt1, int paramInt2);
    
    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return isEnabled(getSectionForPosition(i), getPositionInSectionForPosition(i));
    }
    
    public void notifyDataSetChanged()
    {
      cleanupCache();
      super.notifyDataSetChanged();
    }
    
    public abstract void onBindViewHolder(int paramInt1, int paramInt2, RecyclerView.ViewHolder paramViewHolder);
    
    public final void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      onBindViewHolder(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt), paramViewHolder);
    }
  }
  
  public static abstract class SelectionAdapter
    extends RecyclerView.Adapter
  {
    public abstract boolean isEnabled(RecyclerView.ViewHolder paramViewHolder);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/RecyclerListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */