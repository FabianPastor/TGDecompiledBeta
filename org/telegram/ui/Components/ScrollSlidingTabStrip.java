package org.telegram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.PhotoSize;

public class ScrollSlidingTabStrip
  extends HorizontalScrollView
{
  private int currentPosition;
  private LinearLayout.LayoutParams defaultTabLayoutParams;
  private ScrollSlidingTabStripDelegate delegate;
  private int dividerPadding = AndroidUtilities.dp(12.0F);
  private int indicatorColor = -10066330;
  private int indicatorHeight;
  private int lastScrollX = 0;
  private Paint rectPaint;
  private int scrollOffset = AndroidUtilities.dp(52.0F);
  private int tabCount;
  private int tabPadding = AndroidUtilities.dp(24.0F);
  private LinearLayout tabsContainer;
  private int underlineColor = 436207616;
  private int underlineHeight = AndroidUtilities.dp(2.0F);
  
  public ScrollSlidingTabStrip(Context paramContext)
  {
    super(paramContext);
    setFillViewport(true);
    setWillNotDraw(false);
    setHorizontalScrollBarEnabled(false);
    this.tabsContainer = new LinearLayout(paramContext);
    this.tabsContainer.setOrientation(0);
    this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    addView(this.tabsContainer);
    this.rectPaint = new Paint();
    this.rectPaint.setAntiAlias(true);
    this.rectPaint.setStyle(Paint.Style.FILL);
    this.defaultTabLayoutParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(52.0F), -1);
  }
  
  private void scrollToChild(int paramInt)
  {
    if ((this.tabCount == 0) || (this.tabsContainer.getChildAt(paramInt) == null)) {}
    for (;;)
    {
      return;
      int i = this.tabsContainer.getChildAt(paramInt).getLeft();
      int j = i;
      if (paramInt > 0) {
        j = i - this.scrollOffset;
      }
      paramInt = getScrollX();
      if (j != this.lastScrollX) {
        if (j < paramInt)
        {
          this.lastScrollX = j;
          smoothScrollTo(this.lastScrollX, 0);
        }
        else if (this.scrollOffset + j > getWidth() + paramInt - this.scrollOffset * 2)
        {
          this.lastScrollX = (j - getWidth() + this.scrollOffset * 3);
          smoothScrollTo(this.lastScrollX, 0);
        }
      }
    }
  }
  
  public void addIconTab(Drawable paramDrawable)
  {
    boolean bool = true;
    final int i = this.tabCount;
    this.tabCount = (i + 1);
    ImageView localImageView = new ImageView(getContext());
    localImageView.setFocusable(true);
    localImageView.setImageDrawable(paramDrawable);
    localImageView.setScaleType(ImageView.ScaleType.CENTER);
    localImageView.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
      }
    });
    this.tabsContainer.addView(localImageView);
    if (i == this.currentPosition) {}
    for (;;)
    {
      localImageView.setSelected(bool);
      return;
      bool = false;
    }
  }
  
  public TextView addIconTabWithCounter(Drawable paramDrawable)
  {
    final int i = this.tabCount;
    this.tabCount = (i + 1);
    FrameLayout localFrameLayout = new FrameLayout(getContext());
    localFrameLayout.setFocusable(true);
    this.tabsContainer.addView(localFrameLayout);
    ImageView localImageView = new ImageView(getContext());
    localImageView.setImageDrawable(paramDrawable);
    localImageView.setScaleType(ImageView.ScaleType.CENTER);
    localFrameLayout.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
      }
    });
    localFrameLayout.addView(localImageView, LayoutHelper.createFrame(-1, -1.0F));
    if (i == this.currentPosition) {}
    for (boolean bool = true;; bool = false)
    {
      localFrameLayout.setSelected(bool);
      paramDrawable = new TextView(getContext());
      paramDrawable.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramDrawable.setTextSize(1, 12.0F);
      paramDrawable.setTextColor(-1);
      paramDrawable.setGravity(17);
      paramDrawable.setBackgroundResource(NUM);
      paramDrawable.setMinWidth(AndroidUtilities.dp(18.0F));
      paramDrawable.setPadding(AndroidUtilities.dp(5.0F), 0, AndroidUtilities.dp(5.0F), AndroidUtilities.dp(1.0F));
      localFrameLayout.addView(paramDrawable, LayoutHelper.createFrame(-2, 18.0F, 51, 26.0F, 6.0F, 0.0F, 0.0F));
      return paramDrawable;
    }
  }
  
  public void addStickerTab(TLRPC.Chat paramChat)
  {
    final int i = this.tabCount;
    this.tabCount = (i + 1);
    FrameLayout localFrameLayout = new FrameLayout(getContext());
    localFrameLayout.setFocusable(true);
    localFrameLayout.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
      }
    });
    this.tabsContainer.addView(localFrameLayout);
    if (i == this.currentPosition) {}
    for (boolean bool = true;; bool = false)
    {
      localFrameLayout.setSelected(bool);
      BackupImageView localBackupImageView = new BackupImageView(getContext());
      localBackupImageView.setRoundRadius(AndroidUtilities.dp(15.0F));
      TLRPC.FileLocation localFileLocation = null;
      AvatarDrawable localAvatarDrawable = new AvatarDrawable();
      if (paramChat.photo != null) {
        localFileLocation = paramChat.photo.photo_small;
      }
      localAvatarDrawable.setTextSize(AndroidUtilities.dp(14.0F));
      localAvatarDrawable.setInfo(paramChat);
      localBackupImageView.setImage(localFileLocation, "50_50", localAvatarDrawable);
      localBackupImageView.setAspectFit(true);
      localFrameLayout.addView(localBackupImageView, LayoutHelper.createFrame(30, 30, 17));
      return;
    }
  }
  
  public void addStickerTab(TLRPC.Document paramDocument)
  {
    final int i = this.tabCount;
    this.tabCount = (i + 1);
    FrameLayout localFrameLayout = new FrameLayout(getContext());
    localFrameLayout.setTag(paramDocument);
    localFrameLayout.setFocusable(true);
    localFrameLayout.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        ScrollSlidingTabStrip.this.delegate.onPageSelected(i);
      }
    });
    this.tabsContainer.addView(localFrameLayout);
    if (i == this.currentPosition) {}
    for (boolean bool = true;; bool = false)
    {
      localFrameLayout.setSelected(bool);
      paramDocument = new BackupImageView(getContext());
      paramDocument.setAspectFit(true);
      localFrameLayout.addView(paramDocument, LayoutHelper.createFrame(30, 30, 17));
      return;
    }
  }
  
  public int getCurrentPosition()
  {
    return this.currentPosition;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((isInEditMode()) || (this.tabCount == 0)) {}
    for (;;)
    {
      return;
      int i = getHeight();
      this.rectPaint.setColor(this.underlineColor);
      paramCanvas.drawRect(0.0F, i - this.underlineHeight, this.tabsContainer.getWidth(), i, this.rectPaint);
      View localView = this.tabsContainer.getChildAt(this.currentPosition);
      float f1 = 0.0F;
      float f2 = 0.0F;
      if (localView != null)
      {
        f1 = localView.getLeft();
        f2 = localView.getRight();
      }
      this.rectPaint.setColor(this.indicatorColor);
      if (this.indicatorHeight == 0) {
        paramCanvas.drawRect(f1, 0.0F, f2, i, this.rectPaint);
      } else {
        paramCanvas.drawRect(f1, i - this.indicatorHeight, f2, i, this.rectPaint);
      }
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    setImages();
  }
  
  public void onPageScrolled(int paramInt1, int paramInt2)
  {
    if (this.currentPosition == paramInt1) {}
    do
    {
      return;
      this.currentPosition = paramInt1;
    } while (paramInt1 >= this.tabsContainer.getChildCount());
    int i = 0;
    if (i < this.tabsContainer.getChildCount())
    {
      View localView = this.tabsContainer.getChildAt(i);
      if (i == paramInt1) {}
      for (boolean bool = true;; bool = false)
      {
        localView.setSelected(bool);
        i++;
        break;
      }
    }
    if ((paramInt2 == paramInt1) && (paramInt1 > 1)) {
      scrollToChild(paramInt1 - 1);
    }
    for (;;)
    {
      invalidate();
      break;
      scrollToChild(paramInt1);
    }
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    int i = AndroidUtilities.dp(52.0F);
    paramInt4 = paramInt3 / i;
    paramInt2 = paramInt1 / i;
    paramInt3 = (int)Math.ceil(getMeasuredWidth() / i) + 1;
    paramInt1 = Math.max(0, Math.min(paramInt4, paramInt2));
    paramInt4 = Math.min(this.tabsContainer.getChildCount(), Math.max(paramInt4, paramInt2) + paramInt3);
    if (paramInt1 < paramInt4)
    {
      Object localObject1 = this.tabsContainer.getChildAt(paramInt1);
      if (localObject1 == null) {}
      for (;;)
      {
        paramInt1++;
        break;
        Object localObject2 = ((View)localObject1).getTag();
        if ((localObject2 instanceof TLRPC.Document))
        {
          localObject1 = (BackupImageView)((FrameLayout)localObject1).getChildAt(0);
          if ((paramInt1 < paramInt2) || (paramInt1 >= paramInt2 + paramInt3))
          {
            ((BackupImageView)localObject1).setImageDrawable(null);
          }
          else
          {
            localObject2 = (TLRPC.Document)localObject2;
            if (((TLRPC.Document)localObject2).thumb != null) {
              ((BackupImageView)localObject1).setImage(((TLRPC.Document)localObject2).thumb.location, null, "webp", null);
            }
          }
        }
      }
    }
  }
  
  public void removeTabs()
  {
    this.tabsContainer.removeAllViews();
    this.tabCount = 0;
    this.currentPosition = 0;
  }
  
  public void selectTab(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.tabCount)) {}
    for (;;)
    {
      return;
      this.tabsContainer.getChildAt(paramInt).performClick();
    }
  }
  
  public void setDelegate(ScrollSlidingTabStripDelegate paramScrollSlidingTabStripDelegate)
  {
    this.delegate = paramScrollSlidingTabStripDelegate;
  }
  
  public void setImages()
  {
    int i = AndroidUtilities.dp(52.0F);
    int j = getScrollX() / i;
    i = Math.min(this.tabsContainer.getChildCount(), (int)Math.ceil(getMeasuredWidth() / i) + j + 1);
    if (j < i)
    {
      View localView = this.tabsContainer.getChildAt(j);
      Object localObject = localView.getTag();
      if (!(localObject instanceof TLRPC.Document)) {}
      for (;;)
      {
        j++;
        break;
        ((BackupImageView)((FrameLayout)localView).getChildAt(0)).setImage(((TLRPC.Document)localObject).thumb.location, null, "webp", null);
      }
    }
  }
  
  public void setIndicatorColor(int paramInt)
  {
    this.indicatorColor = paramInt;
    invalidate();
  }
  
  public void setIndicatorHeight(int paramInt)
  {
    this.indicatorHeight = paramInt;
    invalidate();
  }
  
  public void setUnderlineColor(int paramInt)
  {
    this.underlineColor = paramInt;
    invalidate();
  }
  
  public void setUnderlineColorResource(int paramInt)
  {
    this.underlineColor = getResources().getColor(paramInt);
    invalidate();
  }
  
  public void setUnderlineHeight(int paramInt)
  {
    this.underlineHeight = paramInt;
    invalidate();
  }
  
  public void updateTabStyles()
  {
    for (int i = 0; i < this.tabCount; i++) {
      this.tabsContainer.getChildAt(i).setLayoutParams(this.defaultTabLayoutParams);
    }
  }
  
  public static abstract interface ScrollSlidingTabStripDelegate
  {
    public abstract void onPageSelected(int paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ScrollSlidingTabStrip.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */