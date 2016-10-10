package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

public class StickerPreviewViewer
{
  private static volatile StickerPreviewViewer Instance = null;
  private ColorDrawable backgroundDrawable = new ColorDrawable(1895825408);
  private ImageReceiver centerImage = new ImageReceiver();
  private FrameLayoutDrawer containerView;
  private TLRPC.Document currentSticker = null;
  private View currentStickerPreviewCell;
  private boolean isVisible = false;
  private int keyboardHeight = AndroidUtilities.dp(200.0F);
  private long lastUpdateTime;
  private Runnable openStickerPreviewRunnable;
  private Activity parentActivity;
  private float showProgress;
  private int startX;
  private int startY;
  private WindowManager.LayoutParams windowLayoutParams;
  private FrameLayout windowView;
  
  public static StickerPreviewViewer getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      for (;;)
      {
        try
        {
          StickerPreviewViewer localStickerPreviewViewer2 = Instance;
          localObject1 = localStickerPreviewViewer2;
          if (localStickerPreviewViewer2 == null) {
            localObject1 = new StickerPreviewViewer();
          }
        }
        finally
        {
          continue;
        }
        try
        {
          Instance = (StickerPreviewViewer)localObject1;
          return (StickerPreviewViewer)localObject1;
        }
        finally {}
      }
      throw ((Throwable)localObject1);
    }
    return localStickerPreviewViewer1;
  }
  
  private void onDraw(Canvas paramCanvas)
  {
    if ((this.containerView == null) || (this.backgroundDrawable == null)) {}
    do
    {
      do
      {
        return;
        this.backgroundDrawable.setAlpha((int)(180.0F * this.showProgress));
        this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
        this.backgroundDrawable.draw(paramCanvas);
        paramCanvas.save();
        int i = (int)(Math.min(this.containerView.getWidth(), this.containerView.getHeight()) / 1.8F);
        paramCanvas.translate(this.containerView.getWidth() / 2, Math.max(i / 2 + AndroidUtilities.statusBarHeight, (this.containerView.getHeight() - this.keyboardHeight) / 2));
        if (this.centerImage.getBitmap() != null)
        {
          float f = this.showProgress * 0.8F / 0.8F;
          i = (int)(i * f);
          this.centerImage.setAlpha(this.showProgress);
          this.centerImage.setImageCoords(-i / 2, -i / 2, i, i);
          this.centerImage.draw(paramCanvas);
        }
        paramCanvas.restore();
      } while (this.showProgress == 1.0F);
      long l1 = System.currentTimeMillis();
      long l2 = this.lastUpdateTime;
      this.lastUpdateTime = l1;
      this.showProgress += (float)(l1 - l2) / 150.0F;
      this.containerView.invalidate();
    } while (this.showProgress <= 1.0F);
    this.showProgress = 1.0F;
  }
  
  public void close()
  {
    if (this.parentActivity == null) {}
    for (;;)
    {
      return;
      this.showProgress = 1.0F;
      this.currentSticker = null;
      this.isVisible = false;
      AndroidUtilities.unlockOrientation(this.parentActivity);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          StickerPreviewViewer.this.centerImage.setImageBitmap((Bitmap)null);
        }
      });
      try
      {
        if (this.windowView.getParent() != null)
        {
          ((WindowManager)this.parentActivity.getSystemService("window")).removeView(this.windowView);
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
      }
    }
  }
  
  public void destroy()
  {
    this.isVisible = false;
    this.currentSticker = null;
    if ((this.parentActivity == null) || (this.windowView == null)) {
      return;
    }
    try
    {
      if (this.windowView.getParent() != null) {
        ((WindowManager)this.parentActivity.getSystemService("window")).removeViewImmediate(this.windowView);
      }
      this.windowView = null;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e("tmessages", localException);
      }
    }
    Instance = null;
  }
  
  public boolean isVisible()
  {
    return this.isVisible;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent, final View paramView, final int paramInt)
  {
    if (paramMotionEvent.getAction() == 0)
    {
      int k = (int)paramMotionEvent.getX();
      int m = (int)paramMotionEvent.getY();
      int i = 0;
      int j;
      if ((paramView instanceof AbsListView))
      {
        i = ((AbsListView)paramView).getChildCount();
        j = 0;
      }
      for (;;)
      {
        if (j < i)
        {
          paramMotionEvent = null;
          if ((paramView instanceof AbsListView)) {
            paramMotionEvent = ((AbsListView)paramView).getChildAt(j);
          }
          for (;;)
          {
            if (paramMotionEvent != null) {
              break label114;
            }
            return false;
            if (!(paramView instanceof RecyclerListView)) {
              break;
            }
            i = ((RecyclerListView)paramView).getChildCount();
            break;
            if ((paramView instanceof RecyclerListView)) {
              paramMotionEvent = ((RecyclerListView)paramView).getChildAt(j);
            }
          }
          label114:
          int n = paramMotionEvent.getTop();
          int i1 = paramMotionEvent.getBottom();
          int i2 = paramMotionEvent.getLeft();
          int i3 = paramMotionEvent.getRight();
          if ((n > m) || (i1 < m) || (i2 > k) || (i3 < k))
          {
            j += 1;
          }
          else
          {
            boolean bool = false;
            if ((paramMotionEvent instanceof StickerEmojiCell)) {
              bool = ((StickerEmojiCell)paramMotionEvent).showingBitmap();
            }
            while (!bool)
            {
              return false;
              if ((paramMotionEvent instanceof StickerCell))
              {
                bool = ((StickerCell)paramMotionEvent).showingBitmap();
              }
              else if ((paramMotionEvent instanceof ContextLinkCell))
              {
                ContextLinkCell localContextLinkCell = (ContextLinkCell)paramMotionEvent;
                if ((localContextLinkCell.isSticker()) && (localContextLinkCell.showingBitmap())) {}
                for (bool = true;; bool = false) {
                  break;
                }
              }
            }
            this.startX = k;
            this.startY = m;
            this.currentStickerPreviewCell = paramMotionEvent;
            this.openStickerPreviewRunnable = new Runnable()
            {
              public void run()
              {
                if (StickerPreviewViewer.this.openStickerPreviewRunnable == null) {}
                do
                {
                  return;
                  if ((paramView instanceof AbsListView))
                  {
                    ((AbsListView)paramView).setOnItemClickListener(null);
                    ((AbsListView)paramView).requestDisallowInterceptTouchEvent(true);
                  }
                  for (;;)
                  {
                    StickerPreviewViewer.access$102(StickerPreviewViewer.this, null);
                    StickerPreviewViewer.this.setParentActivity((Activity)paramView.getContext());
                    StickerPreviewViewer.this.setKeyboardHeight(paramInt);
                    if (!(StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerEmojiCell)) {
                      break;
                    }
                    StickerPreviewViewer.this.open(((StickerEmojiCell)StickerPreviewViewer.this.currentStickerPreviewCell).getSticker());
                    ((StickerEmojiCell)StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                    return;
                    if ((paramView instanceof RecyclerListView))
                    {
                      ((RecyclerListView)paramView).setOnItemClickListener(null);
                      ((RecyclerListView)paramView).requestDisallowInterceptTouchEvent(true);
                    }
                  }
                  if ((StickerPreviewViewer.this.currentStickerPreviewCell instanceof StickerCell))
                  {
                    StickerPreviewViewer.this.open(((StickerCell)StickerPreviewViewer.this.currentStickerPreviewCell).getSticker());
                    ((StickerCell)StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
                    return;
                  }
                } while (!(StickerPreviewViewer.this.currentStickerPreviewCell instanceof ContextLinkCell));
                StickerPreviewViewer.this.open(((ContextLinkCell)StickerPreviewViewer.this.currentStickerPreviewCell).getDocument());
                ((ContextLinkCell)StickerPreviewViewer.this.currentStickerPreviewCell).setScaled(true);
              }
            };
            AndroidUtilities.runOnUIThread(this.openStickerPreviewRunnable, 200L);
            return true;
          }
        }
      }
    }
    return false;
  }
  
  public boolean onTouch(MotionEvent paramMotionEvent, final View paramView, int paramInt, final Object paramObject)
  {
    if ((this.openStickerPreviewRunnable != null) || (isVisible()))
    {
      if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3) && (paramMotionEvent.getAction() != 6)) {
        break label172;
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if ((paramView instanceof AbsListView)) {
            ((AbsListView)paramView).setOnItemClickListener((AdapterView.OnItemClickListener)paramObject);
          }
          while (!(paramView instanceof RecyclerListView)) {
            return;
          }
          ((RecyclerListView)paramView).setOnItemClickListener((RecyclerListView.OnItemClickListener)paramObject);
        }
      }, 150L);
      if (this.openStickerPreviewRunnable == null) {
        break label77;
      }
      AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
      this.openStickerPreviewRunnable = null;
    }
    for (;;)
    {
      return false;
      label77:
      if (isVisible())
      {
        close();
        if (this.currentStickerPreviewCell != null)
        {
          if ((this.currentStickerPreviewCell instanceof StickerEmojiCell)) {
            ((StickerEmojiCell)this.currentStickerPreviewCell).setScaled(false);
          }
          for (;;)
          {
            this.currentStickerPreviewCell = null;
            break;
            if ((this.currentStickerPreviewCell instanceof StickerCell)) {
              ((StickerCell)this.currentStickerPreviewCell).setScaled(false);
            } else if ((this.currentStickerPreviewCell instanceof ContextLinkCell)) {
              ((ContextLinkCell)this.currentStickerPreviewCell).setScaled(false);
            }
          }
          label172:
          if (paramMotionEvent.getAction() != 0)
          {
            if (isVisible())
            {
              label301:
              boolean bool;
              if (paramMotionEvent.getAction() == 2)
              {
                int k = (int)paramMotionEvent.getX();
                int m = (int)paramMotionEvent.getY();
                int i = 0;
                int j;
                if ((paramView instanceof AbsListView))
                {
                  i = ((AbsListView)paramView).getChildCount();
                  j = 0;
                }
                for (;;)
                {
                  if (j < i)
                  {
                    paramMotionEvent = null;
                    if ((paramView instanceof AbsListView)) {
                      paramMotionEvent = ((AbsListView)paramView).getChildAt(j);
                    }
                    for (;;)
                    {
                      if (paramMotionEvent != null) {
                        break label301;
                      }
                      return false;
                      if (!(paramView instanceof RecyclerListView)) {
                        break;
                      }
                      i = ((RecyclerListView)paramView).getChildCount();
                      break;
                      if ((paramView instanceof RecyclerListView)) {
                        paramMotionEvent = ((RecyclerListView)paramView).getChildAt(j);
                      }
                    }
                    int n = paramMotionEvent.getTop();
                    int i1 = paramMotionEvent.getBottom();
                    int i2 = paramMotionEvent.getLeft();
                    int i3 = paramMotionEvent.getRight();
                    if ((n > m) || (i1 < m) || (i2 > k) || (i3 < k))
                    {
                      j += 1;
                    }
                    else
                    {
                      bool = false;
                      if (!(paramMotionEvent instanceof StickerEmojiCell)) {
                        break label390;
                      }
                      bool = true;
                    }
                  }
                }
              }
              while ((!bool) || (paramMotionEvent == this.currentStickerPreviewCell))
              {
                return true;
                label390:
                if ((paramMotionEvent instanceof StickerCell)) {
                  bool = true;
                } else if ((paramMotionEvent instanceof ContextLinkCell)) {
                  bool = ((ContextLinkCell)paramMotionEvent).isSticker();
                }
              }
              if ((this.currentStickerPreviewCell instanceof StickerEmojiCell))
              {
                ((StickerEmojiCell)this.currentStickerPreviewCell).setScaled(false);
                this.currentStickerPreviewCell = paramMotionEvent;
                setKeyboardHeight(paramInt);
                if (!(this.currentStickerPreviewCell instanceof StickerEmojiCell)) {
                  break label538;
                }
                open(((StickerEmojiCell)this.currentStickerPreviewCell).getSticker());
                ((StickerEmojiCell)this.currentStickerPreviewCell).setScaled(true);
              }
              for (;;)
              {
                return true;
                if ((this.currentStickerPreviewCell instanceof StickerCell))
                {
                  ((StickerCell)this.currentStickerPreviewCell).setScaled(false);
                  break;
                }
                if (!(this.currentStickerPreviewCell instanceof ContextLinkCell)) {
                  break;
                }
                ((ContextLinkCell)this.currentStickerPreviewCell).setScaled(false);
                break;
                label538:
                if ((this.currentStickerPreviewCell instanceof StickerCell))
                {
                  open(((StickerCell)this.currentStickerPreviewCell).getSticker());
                  ((StickerCell)this.currentStickerPreviewCell).setScaled(true);
                }
                else if ((this.currentStickerPreviewCell instanceof ContextLinkCell))
                {
                  open(((ContextLinkCell)this.currentStickerPreviewCell).getDocument());
                  ((ContextLinkCell)this.currentStickerPreviewCell).setScaled(true);
                }
              }
            }
            if (this.openStickerPreviewRunnable != null) {
              if (paramMotionEvent.getAction() == 2)
              {
                if (Math.hypot(this.startX - paramMotionEvent.getX(), this.startY - paramMotionEvent.getY()) > AndroidUtilities.dp(10.0F))
                {
                  AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                  this.openStickerPreviewRunnable = null;
                }
              }
              else
              {
                AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
                this.openStickerPreviewRunnable = null;
              }
            }
          }
        }
      }
    }
  }
  
  public void open(TLRPC.Document paramDocument)
  {
    if ((this.parentActivity == null) || (paramDocument == null)) {}
    do
    {
      return;
      this.centerImage.setImage(paramDocument, null, paramDocument.thumb.location, null, "webp", true);
      this.currentSticker = paramDocument;
      this.containerView.invalidate();
    } while (this.isVisible);
    AndroidUtilities.lockOrientation(this.parentActivity);
    try
    {
      if (this.windowView.getParent() != null) {
        ((WindowManager)this.parentActivity.getSystemService("window")).removeView(this.windowView);
      }
      ((WindowManager)this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
      this.isVisible = true;
      this.showProgress = 0.0F;
      this.lastUpdateTime = System.currentTimeMillis();
      return;
    }
    catch (Exception paramDocument)
    {
      for (;;)
      {
        FileLog.e("tmessages", paramDocument);
      }
    }
  }
  
  public void reset()
  {
    if (this.openStickerPreviewRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.openStickerPreviewRunnable);
      this.openStickerPreviewRunnable = null;
    }
    if (this.currentStickerPreviewCell != null)
    {
      if (!(this.currentStickerPreviewCell instanceof StickerEmojiCell)) {
        break label53;
      }
      ((StickerEmojiCell)this.currentStickerPreviewCell).setScaled(false);
    }
    for (;;)
    {
      this.currentStickerPreviewCell = null;
      return;
      label53:
      if ((this.currentStickerPreviewCell instanceof StickerCell)) {
        ((StickerCell)this.currentStickerPreviewCell).setScaled(false);
      } else if ((this.currentStickerPreviewCell instanceof ContextLinkCell)) {
        ((ContextLinkCell)this.currentStickerPreviewCell).setScaled(false);
      }
    }
  }
  
  public void setKeyboardHeight(int paramInt)
  {
    this.keyboardHeight = paramInt;
  }
  
  public void setParentActivity(Activity paramActivity)
  {
    if (this.parentActivity == paramActivity) {
      return;
    }
    this.parentActivity = paramActivity;
    this.windowView = new FrameLayout(paramActivity);
    this.windowView.setFocusable(true);
    this.windowView.setFocusableInTouchMode(true);
    if (Build.VERSION.SDK_INT >= 23) {
      this.windowView.setFitsSystemWindows(true);
    }
    this.containerView = new FrameLayoutDrawer(paramActivity);
    this.containerView.setFocusable(false);
    this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
    this.containerView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        if ((paramAnonymousMotionEvent.getAction() == 1) || (paramAnonymousMotionEvent.getAction() == 6) || (paramAnonymousMotionEvent.getAction() == 3)) {
          StickerPreviewViewer.this.close();
        }
        return true;
      }
    });
    this.windowLayoutParams = new WindowManager.LayoutParams();
    this.windowLayoutParams.height = -1;
    this.windowLayoutParams.format = -3;
    this.windowLayoutParams.width = -1;
    this.windowLayoutParams.gravity = 48;
    this.windowLayoutParams.type = 99;
    if (Build.VERSION.SDK_INT >= 21) {}
    for (this.windowLayoutParams.flags = -2147483640;; this.windowLayoutParams.flags = 8)
    {
      this.centerImage.setAspectFit(true);
      this.centerImage.setInvalidateAll(true);
      this.centerImage.setParentView(this.containerView);
      return;
    }
  }
  
  private class FrameLayoutDrawer
    extends FrameLayout
  {
    public FrameLayoutDrawer(Context paramContext)
    {
      super();
      setWillNotDraw(false);
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      StickerPreviewViewer.getInstance().onDraw(paramCanvas);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/StickerPreviewViewer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */