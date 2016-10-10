package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.AdapterDataObserver;
import org.telegram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;

public class RecyclerListView
  extends RecyclerView
{
  private static int[] attributes;
  private static boolean gotAttributes;
  private Runnable clickRunnable;
  private int currentChildPosition;
  private View currentChildView;
  private boolean disallowInterceptTouchEvents;
  private View emptyView;
  private boolean instantClick;
  private boolean interceptedByChild;
  private GestureDetector mGestureDetector;
  private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver()
  {
    public void onChanged()
    {
      RecyclerListView.this.checkIfEmpty();
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
  private OnItemLongClickListener onItemLongClickListener;
  private RecyclerView.OnScrollListener onScrollListener;
  private Runnable selectChildRunnable;
  private boolean wasPressed;
  
  public RecyclerListView(Context paramContext)
  {
    super(paramContext);
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
        FileLog.e("tmessages", localThrowable);
      }
    }
    super.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
      {
        MotionEvent localMotionEvent;
        if ((paramAnonymousInt != 0) && (RecyclerListView.this.currentChildView != null))
        {
          if (RecyclerListView.this.selectChildRunnable != null)
          {
            AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
            RecyclerListView.access$602(RecyclerListView.this, null);
          }
          localMotionEvent = MotionEvent.obtain(0L, 0L, 3, 0.0F, 0.0F, 0);
        }
        try
        {
          RecyclerListView.this.mGestureDetector.onTouchEvent(localMotionEvent);
          RecyclerListView.this.currentChildView.onTouchEvent(localMotionEvent);
          localMotionEvent.recycle();
          RecyclerListView.this.currentChildView.setPressed(false);
          RecyclerListView.access$102(RecyclerListView.this, null);
          RecyclerListView.access$702(RecyclerListView.this, false);
          if (RecyclerListView.this.onScrollListener != null) {
            RecyclerListView.this.onScrollListener.onScrollStateChanged(paramAnonymousRecyclerView, paramAnonymousInt);
          }
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException);
          }
        }
      }
      
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (RecyclerListView.this.onScrollListener != null) {
          RecyclerListView.this.onScrollListener.onScrolled(paramAnonymousRecyclerView, paramAnonymousInt1, paramAnonymousInt2);
        }
      }
    });
    addOnItemTouchListener(new RecyclerListViewItemClickListener(paramContext));
  }
  
  private void checkIfEmpty()
  {
    int k = 0;
    if ((this.emptyView == null) || (getAdapter() == null)) {
      return;
    }
    int i;
    View localView;
    if (getAdapter().getItemCount() == 0)
    {
      i = 1;
      localView = this.emptyView;
      if (i == 0) {
        break label66;
      }
    }
    label66:
    for (int j = 0;; j = 8)
    {
      localView.setVisibility(j);
      j = k;
      if (i != 0) {
        j = 4;
      }
      setVisibility(j);
      return;
      i = 0;
      break;
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
      if (paramBoolean) {
        this.currentChildView.setPressed(false);
      }
      this.currentChildView = null;
    }
    if (this.clickRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.clickRunnable);
      this.clickRunnable = null;
    }
    this.interceptedByChild = false;
  }
  
  public View getEmptyView()
  {
    return this.emptyView;
  }
  
  public int[] getResourceDeclareStyleableIntArray(String paramString1, String paramString2)
  {
    try
    {
      paramString1 = Class.forName(paramString1 + ".R$styleable").getField(paramString2);
      if (paramString1 != null)
      {
        paramString1 = (int[])paramString1.get(null);
        return paramString1;
      }
    }
    catch (Throwable paramString1) {}
    return null;
  }
  
  public boolean hasOverlappingRendering()
  {
    return false;
  }
  
  public void invalidateViews()
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      getChildAt(i).invalidate();
      i += 1;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.disallowInterceptTouchEvents) {
      requestDisallowInterceptTouchEvent(true);
    }
    return ((this.onInterceptTouchListener != null) && (this.onInterceptTouchListener.onInterceptTouchEvent(paramMotionEvent))) || (super.onInterceptTouchEvent(paramMotionEvent));
  }
  
  public void setAdapter(RecyclerView.Adapter paramAdapter)
  {
    RecyclerView.Adapter localAdapter = getAdapter();
    if (localAdapter != null) {
      localAdapter.unregisterAdapterDataObserver(this.observer);
    }
    super.setAdapter(paramAdapter);
    if (paramAdapter != null) {
      paramAdapter.registerAdapterDataObserver(this.observer);
    }
    checkIfEmpty();
  }
  
  public void setDisallowInterceptTouchEvents(boolean paramBoolean)
  {
    this.disallowInterceptTouchEvents = paramBoolean;
  }
  
  public void setEmptyView(View paramView)
  {
    if (this.emptyView == paramView) {
      return;
    }
    this.emptyView = paramView;
    checkIfEmpty();
  }
  
  public void setInstantClick(boolean paramBoolean)
  {
    this.instantClick = paramBoolean;
  }
  
  public void setOnInterceptTouchListener(OnInterceptTouchListener paramOnInterceptTouchListener)
  {
    this.onInterceptTouchListener = paramOnInterceptTouchListener;
  }
  
  public void setOnItemClickListener(OnItemClickListener paramOnItemClickListener)
  {
    this.onItemClickListener = paramOnItemClickListener;
  }
  
  public void setOnItemLongClickListener(OnItemLongClickListener paramOnItemLongClickListener)
  {
    this.onItemLongClickListener = paramOnItemLongClickListener;
  }
  
  public void setOnScrollListener(RecyclerView.OnScrollListener paramOnScrollListener)
  {
    this.onScrollListener = paramOnScrollListener;
  }
  
  public void setVerticalScrollBarEnabled(boolean paramBoolean)
  {
    if (attributes != null) {
      super.setVerticalScrollBarEnabled(paramBoolean);
    }
  }
  
  public void stopScroll()
  {
    try
    {
      super.stopScroll();
      return;
    }
    catch (NullPointerException localNullPointerException) {}
  }
  
  public static abstract interface OnInterceptTouchListener
  {
    public abstract boolean onInterceptTouchEvent(MotionEvent paramMotionEvent);
  }
  
  public static abstract interface OnItemClickListener
  {
    public abstract void onItemClick(View paramView, int paramInt);
  }
  
  public static abstract interface OnItemLongClickListener
  {
    public abstract boolean onItemClick(View paramView, int paramInt);
  }
  
  private class RecyclerListViewItemClickListener
    implements RecyclerView.OnItemTouchListener
  {
    public RecyclerListViewItemClickListener(Context paramContext)
    {
      RecyclerListView.access$002(RecyclerListView.this, new GestureDetector(paramContext, new GestureDetector.SimpleOnGestureListener()
      {
        public void onLongPress(MotionEvent paramAnonymousMotionEvent)
        {
          if (RecyclerListView.this.currentChildView != null)
          {
            paramAnonymousMotionEvent = RecyclerListView.this.currentChildView;
            if ((RecyclerListView.this.onItemLongClickListener != null) && (RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition))) {
              paramAnonymousMotionEvent.performHapticFeedback(0);
            }
          }
        }
        
        public boolean onSingleTapUp(final MotionEvent paramAnonymousMotionEvent)
        {
          if ((RecyclerListView.this.currentChildView != null) && (RecyclerListView.this.onItemClickListener != null))
          {
            RecyclerListView.this.currentChildView.setPressed(true);
            paramAnonymousMotionEvent = RecyclerListView.this.currentChildView;
            if (RecyclerListView.this.instantClick)
            {
              paramAnonymousMotionEvent.playSoundEffect(0);
              RecyclerListView.this.onItemClickListener.onItemClick(paramAnonymousMotionEvent, RecyclerListView.this.currentChildPosition);
            }
            AndroidUtilities.runOnUIThread(RecyclerListView.access$502(RecyclerListView.this, new Runnable()
            {
              public void run()
              {
                if (this == RecyclerListView.this.clickRunnable) {
                  RecyclerListView.access$502(RecyclerListView.this, null);
                }
                if (paramAnonymousMotionEvent != null)
                {
                  paramAnonymousMotionEvent.setPressed(false);
                  if (!RecyclerListView.this.instantClick)
                  {
                    paramAnonymousMotionEvent.playSoundEffect(0);
                    if (RecyclerListView.this.onItemClickListener != null) {
                      RecyclerListView.this.onItemClickListener.onItemClick(paramAnonymousMotionEvent, RecyclerListView.this.currentChildPosition);
                    }
                  }
                }
              }
            }), ViewConfiguration.getPressedStateDuration());
            if (RecyclerListView.this.selectChildRunnable != null)
            {
              AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
              RecyclerListView.access$602(RecyclerListView.this, null);
              RecyclerListView.access$102(RecyclerListView.this, null);
              RecyclerListView.access$702(RecyclerListView.this, false);
            }
          }
          return true;
        }
      }));
    }
    
    public boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
      int k = paramMotionEvent.getActionMasked();
      int i;
      if (RecyclerListView.this.getScrollState() == 0) {
        i = 1;
      }
      for (;;)
      {
        int j;
        if (((k == 0) || (k == 5)) && (RecyclerListView.this.currentChildView == null) && (i != 0))
        {
          RecyclerListView.access$102(RecyclerListView.this, paramRecyclerView.findChildViewUnder(paramMotionEvent.getX(), paramMotionEvent.getY()));
          if ((RecyclerListView.this.currentChildView instanceof ViewGroup))
          {
            ViewGroup localViewGroup = (ViewGroup)RecyclerListView.this.currentChildView;
            float f1 = paramMotionEvent.getX() - RecyclerListView.this.currentChildView.getLeft();
            float f2 = paramMotionEvent.getY() - RecyclerListView.this.currentChildView.getTop();
            j = localViewGroup.getChildCount() - 1;
            if (j >= 0)
            {
              View localView = localViewGroup.getChildAt(j);
              if ((f1 < localView.getLeft()) || (f1 > localView.getRight()) || (f2 < localView.getTop()) || (f2 > localView.getBottom()) || (!localView.isClickable())) {
                break label424;
              }
              RecyclerListView.access$102(RecyclerListView.this, null);
            }
          }
          RecyclerListView.access$402(RecyclerListView.this, -1);
          if (RecyclerListView.this.currentChildView != null)
          {
            RecyclerListView.access$402(RecyclerListView.this, paramRecyclerView.getChildPosition(RecyclerListView.this.currentChildView));
            paramRecyclerView = MotionEvent.obtain(0L, 0L, paramMotionEvent.getActionMasked(), paramMotionEvent.getX() - RecyclerListView.this.currentChildView.getLeft(), paramMotionEvent.getY() - RecyclerListView.this.currentChildView.getTop(), 0);
            if (RecyclerListView.this.currentChildView.onTouchEvent(paramRecyclerView)) {
              RecyclerListView.access$702(RecyclerListView.this, true);
            }
            paramRecyclerView.recycle();
          }
        }
        if ((RecyclerListView.this.currentChildView != null) && (!RecyclerListView.this.interceptedByChild) && (paramMotionEvent != null)) {}
        try
        {
          RecyclerListView.this.mGestureDetector.onTouchEvent(paramMotionEvent);
          if ((k == 0) || (k == 5))
          {
            if ((!RecyclerListView.this.interceptedByChild) && (RecyclerListView.this.currentChildView != null))
            {
              RecyclerListView.access$602(RecyclerListView.this, new Runnable()
              {
                public void run()
                {
                  if ((RecyclerListView.this.selectChildRunnable != null) && (RecyclerListView.this.currentChildView != null))
                  {
                    RecyclerListView.this.currentChildView.setPressed(true);
                    RecyclerListView.access$602(RecyclerListView.this, null);
                  }
                }
              });
              AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, ViewConfiguration.getTapTimeout());
            }
            return false;
            i = 0;
            continue;
            label424:
            j -= 1;
          }
        }
        catch (Exception paramRecyclerView)
        {
          for (;;)
          {
            FileLog.e("tmessages", paramRecyclerView);
            continue;
            if ((RecyclerListView.this.currentChildView != null) && ((k == 1) || (k == 6) || (k == 3) || (i == 0)))
            {
              if (RecyclerListView.this.selectChildRunnable != null)
              {
                AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                RecyclerListView.access$602(RecyclerListView.this, null);
              }
              RecyclerListView.this.currentChildView.setPressed(false);
              RecyclerListView.access$102(RecyclerListView.this, null);
              RecyclerListView.access$702(RecyclerListView.this, false);
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/RecyclerListView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */