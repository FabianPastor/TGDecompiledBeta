package org.telegram.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;

public class PhotoCropActivity
  extends BaseFragment
{
  private static final int done_button = 1;
  private String bitmapKey;
  private PhotoEditActivityDelegate delegate = null;
  private boolean doneButtonPressed = false;
  private BitmapDrawable drawable;
  private Bitmap imageToCrop;
  private boolean sameBitmap = false;
  private PhotoCropView view;
  
  public PhotoCropActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(-13421773);
    this.actionBar.setItemsBackgroundColor(-12763843);
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("CropImage", 2131165537));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          PhotoCropActivity.this.finishFragment();
        }
        while (paramAnonymousInt != 1) {
          return;
        }
        if ((PhotoCropActivity.this.delegate != null) && (!PhotoCropActivity.this.doneButtonPressed))
        {
          Bitmap localBitmap = PhotoCropActivity.this.view.getBitmap();
          if (localBitmap == PhotoCropActivity.this.imageToCrop) {
            PhotoCropActivity.access$502(PhotoCropActivity.this, true);
          }
          PhotoCropActivity.this.delegate.didFinishEdit(localBitmap);
          PhotoCropActivity.access$302(PhotoCropActivity.this, true);
        }
        PhotoCropActivity.this.finishFragment();
      }
    });
    this.actionBar.createMenu().addItemWithWidth(1, 2130837720, AndroidUtilities.dp(56.0F));
    paramContext = new PhotoCropView(paramContext);
    this.view = paramContext;
    this.fragmentView = paramContext;
    ((PhotoCropView)this.fragmentView).freeform = getArguments().getBoolean("freeform", false);
    this.fragmentView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    return this.fragmentView;
  }
  
  public boolean onFragmentCreate()
  {
    this.swipeBackEnabled = false;
    String str;
    Uri localUri;
    if (this.imageToCrop == null)
    {
      str = getArguments().getString("photoPath");
      localUri = (Uri)getArguments().getParcelable("photoUri");
      if ((str == null) && (localUri == null)) {}
      while ((str != null) && (!new File(str).exists())) {
        return false;
      }
      if (!AndroidUtilities.isTablet()) {
        break label118;
      }
    }
    label118:
    for (int i = AndroidUtilities.dp(520.0F);; i = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y))
    {
      this.imageToCrop = ImageLoader.loadBitmap(str, localUri, i, i, true);
      if (this.imageToCrop == null) {
        break;
      }
      this.drawable = new BitmapDrawable(this.imageToCrop);
      super.onFragmentCreate();
      return true;
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if ((this.bitmapKey != null) && (ImageLoader.getInstance().decrementUseCount(this.bitmapKey)) && (!ImageLoader.getInstance().isInCache(this.bitmapKey))) {
      this.bitmapKey = null;
    }
    if ((this.bitmapKey == null) && (this.imageToCrop != null) && (!this.sameBitmap))
    {
      this.imageToCrop.recycle();
      this.imageToCrop = null;
    }
    this.drawable = null;
  }
  
  public void setDelegate(PhotoEditActivityDelegate paramPhotoEditActivityDelegate)
  {
    this.delegate = paramPhotoEditActivityDelegate;
  }
  
  private class PhotoCropView
    extends FrameLayout
  {
    int bitmapHeight;
    int bitmapWidth;
    int bitmapX;
    int bitmapY;
    Paint circlePaint = null;
    int draggingState = 0;
    boolean freeform;
    Paint halfPaint = null;
    float oldX = 0.0F;
    float oldY = 0.0F;
    Paint rectPaint = null;
    float rectSizeX = 600.0F;
    float rectSizeY = 600.0F;
    float rectX = -1.0F;
    float rectY = -1.0F;
    int viewHeight;
    int viewWidth;
    
    public PhotoCropView(Context paramContext)
    {
      super();
      init();
    }
    
    private void init()
    {
      this.rectPaint = new Paint();
      this.rectPaint.setColor(1073412858);
      this.rectPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
      this.rectPaint.setStyle(Paint.Style.STROKE);
      this.circlePaint = new Paint();
      this.circlePaint.setColor(-1);
      this.halfPaint = new Paint();
      this.halfPaint.setColor(-939524096);
      setBackgroundColor(-13421773);
      setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          float f4 = paramAnonymousMotionEvent.getX();
          float f5 = paramAnonymousMotionEvent.getY();
          int i = AndroidUtilities.dp(14.0F);
          if (paramAnonymousMotionEvent.getAction() == 0) {
            if ((PhotoCropActivity.PhotoCropView.this.rectX - i < f4) && (PhotoCropActivity.PhotoCropView.this.rectX + i > f4) && (PhotoCropActivity.PhotoCropView.this.rectY - i < f5) && (PhotoCropActivity.PhotoCropView.this.rectY + i > f5))
            {
              PhotoCropActivity.PhotoCropView.this.draggingState = 1;
              if (PhotoCropActivity.PhotoCropView.this.draggingState != 0) {
                PhotoCropActivity.PhotoCropView.this.requestDisallowInterceptTouchEvent(true);
              }
              PhotoCropActivity.PhotoCropView.this.oldX = f4;
              PhotoCropActivity.PhotoCropView.this.oldY = f5;
            }
          }
          do
          {
            return true;
            if ((PhotoCropActivity.PhotoCropView.this.rectX - i + PhotoCropActivity.PhotoCropView.this.rectSizeX < f4) && (PhotoCropActivity.PhotoCropView.this.rectX + i + PhotoCropActivity.PhotoCropView.this.rectSizeX > f4) && (PhotoCropActivity.PhotoCropView.this.rectY - i < f5) && (PhotoCropActivity.PhotoCropView.this.rectY + i > f5))
            {
              PhotoCropActivity.PhotoCropView.this.draggingState = 2;
              break;
            }
            if ((PhotoCropActivity.PhotoCropView.this.rectX - i < f4) && (PhotoCropActivity.PhotoCropView.this.rectX + i > f4) && (PhotoCropActivity.PhotoCropView.this.rectY - i + PhotoCropActivity.PhotoCropView.this.rectSizeY < f5) && (PhotoCropActivity.PhotoCropView.this.rectY + i + PhotoCropActivity.PhotoCropView.this.rectSizeY > f5))
            {
              PhotoCropActivity.PhotoCropView.this.draggingState = 3;
              break;
            }
            if ((PhotoCropActivity.PhotoCropView.this.rectX - i + PhotoCropActivity.PhotoCropView.this.rectSizeX < f4) && (PhotoCropActivity.PhotoCropView.this.rectX + i + PhotoCropActivity.PhotoCropView.this.rectSizeX > f4) && (PhotoCropActivity.PhotoCropView.this.rectY - i + PhotoCropActivity.PhotoCropView.this.rectSizeY < f5) && (PhotoCropActivity.PhotoCropView.this.rectY + i + PhotoCropActivity.PhotoCropView.this.rectSizeY > f5))
            {
              PhotoCropActivity.PhotoCropView.this.draggingState = 4;
              break;
            }
            if ((PhotoCropActivity.PhotoCropView.this.rectX < f4) && (PhotoCropActivity.PhotoCropView.this.rectX + PhotoCropActivity.PhotoCropView.this.rectSizeX > f4) && (PhotoCropActivity.PhotoCropView.this.rectY < f5) && (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeY > f5))
            {
              PhotoCropActivity.PhotoCropView.this.draggingState = 5;
              break;
            }
            PhotoCropActivity.PhotoCropView.this.draggingState = 0;
            break;
            if (paramAnonymousMotionEvent.getAction() == 1)
            {
              PhotoCropActivity.PhotoCropView.this.draggingState = 0;
              return true;
            }
          } while ((paramAnonymousMotionEvent.getAction() != 2) || (PhotoCropActivity.PhotoCropView.this.draggingState == 0));
          float f2 = f4 - PhotoCropActivity.PhotoCropView.this.oldX;
          float f1 = f5 - PhotoCropActivity.PhotoCropView.this.oldY;
          if (PhotoCropActivity.PhotoCropView.this.draggingState == 5)
          {
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
            paramAnonymousView.rectX += f2;
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
            paramAnonymousView.rectY += f1;
            if (PhotoCropActivity.PhotoCropView.this.rectX < PhotoCropActivity.PhotoCropView.this.bitmapX)
            {
              PhotoCropActivity.PhotoCropView.this.rectX = PhotoCropActivity.PhotoCropView.this.bitmapX;
              if (PhotoCropActivity.PhotoCropView.this.rectY >= PhotoCropActivity.PhotoCropView.this.bitmapY) {
                break label796;
              }
              PhotoCropActivity.PhotoCropView.this.rectY = PhotoCropActivity.PhotoCropView.this.bitmapY;
            }
          }
          label796:
          label1912:
          do
          {
            for (;;)
            {
              PhotoCropActivity.PhotoCropView.this.oldX = f4;
              PhotoCropActivity.PhotoCropView.this.oldY = f5;
              PhotoCropActivity.PhotoCropView.this.invalidate();
              return true;
              if (PhotoCropActivity.PhotoCropView.this.rectX + PhotoCropActivity.PhotoCropView.this.rectSizeX <= PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth) {
                break;
              }
              PhotoCropActivity.PhotoCropView.this.rectX = (PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth - PhotoCropActivity.PhotoCropView.this.rectSizeX);
              break;
              if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeY > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight)
              {
                PhotoCropActivity.PhotoCropView.this.rectY = (PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight - PhotoCropActivity.PhotoCropView.this.rectSizeY);
                continue;
                if (PhotoCropActivity.PhotoCropView.this.draggingState == 1)
                {
                  f3 = f2;
                  if (PhotoCropActivity.PhotoCropView.this.rectSizeX - f2 < 160.0F) {
                    f3 = PhotoCropActivity.PhotoCropView.this.rectSizeX - 160.0F;
                  }
                  f2 = f3;
                  if (PhotoCropActivity.PhotoCropView.this.rectX + f3 < PhotoCropActivity.PhotoCropView.this.bitmapX) {
                    f2 = PhotoCropActivity.PhotoCropView.this.bitmapX - PhotoCropActivity.PhotoCropView.this.rectX;
                  }
                  if (!PhotoCropActivity.PhotoCropView.this.freeform)
                  {
                    f1 = f2;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + f2 < PhotoCropActivity.PhotoCropView.this.bitmapY) {
                      f1 = PhotoCropActivity.PhotoCropView.this.bitmapY - PhotoCropActivity.PhotoCropView.this.rectY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectX += f1;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectY += f1;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX -= f1;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY -= f1;
                  }
                  else
                  {
                    f3 = f1;
                    if (PhotoCropActivity.PhotoCropView.this.rectSizeY - f1 < 160.0F) {
                      f3 = PhotoCropActivity.PhotoCropView.this.rectSizeY - 160.0F;
                    }
                    f1 = f3;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + f3 < PhotoCropActivity.PhotoCropView.this.bitmapY) {
                      f1 = PhotoCropActivity.PhotoCropView.this.bitmapY - PhotoCropActivity.PhotoCropView.this.rectY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectX += f2;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectY += f1;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX -= f2;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY -= f1;
                  }
                }
                else if (PhotoCropActivity.PhotoCropView.this.draggingState == 2)
                {
                  f3 = f2;
                  if (PhotoCropActivity.PhotoCropView.this.rectSizeX + f2 < 160.0F) {
                    f3 = -(PhotoCropActivity.PhotoCropView.this.rectSizeX - 160.0F);
                  }
                  f2 = f3;
                  if (PhotoCropActivity.PhotoCropView.this.rectX + PhotoCropActivity.PhotoCropView.this.rectSizeX + f3 > PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth) {
                    f2 = PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth - PhotoCropActivity.PhotoCropView.this.rectX - PhotoCropActivity.PhotoCropView.this.rectSizeX;
                  }
                  if (!PhotoCropActivity.PhotoCropView.this.freeform)
                  {
                    f1 = f2;
                    if (PhotoCropActivity.PhotoCropView.this.rectY - f2 < PhotoCropActivity.PhotoCropView.this.bitmapY) {
                      f1 = PhotoCropActivity.PhotoCropView.this.rectY - PhotoCropActivity.PhotoCropView.this.bitmapY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectY -= f1;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX += f1;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY += f1;
                  }
                  else
                  {
                    f3 = f1;
                    if (PhotoCropActivity.PhotoCropView.this.rectSizeY - f1 < 160.0F) {
                      f3 = PhotoCropActivity.PhotoCropView.this.rectSizeY - 160.0F;
                    }
                    f1 = f3;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + f3 < PhotoCropActivity.PhotoCropView.this.bitmapY) {
                      f1 = PhotoCropActivity.PhotoCropView.this.bitmapY - PhotoCropActivity.PhotoCropView.this.rectY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectY += f1;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX += f2;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY -= f1;
                  }
                }
                else
                {
                  if (PhotoCropActivity.PhotoCropView.this.draggingState != 3) {
                    break label1912;
                  }
                  f3 = f2;
                  if (PhotoCropActivity.PhotoCropView.this.rectSizeX - f2 < 160.0F) {
                    f3 = PhotoCropActivity.PhotoCropView.this.rectSizeX - 160.0F;
                  }
                  f2 = f3;
                  if (PhotoCropActivity.PhotoCropView.this.rectX + f3 < PhotoCropActivity.PhotoCropView.this.bitmapX) {
                    f2 = PhotoCropActivity.PhotoCropView.this.bitmapX - PhotoCropActivity.PhotoCropView.this.rectX;
                  }
                  if (!PhotoCropActivity.PhotoCropView.this.freeform)
                  {
                    f1 = f2;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeX - f2 > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight) {
                      f1 = PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeX - PhotoCropActivity.PhotoCropView.this.bitmapY - PhotoCropActivity.PhotoCropView.this.bitmapHeight;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectX += f1;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX -= f1;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY -= f1;
                  }
                  else
                  {
                    f3 = f1;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeY + f1 > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight) {
                      f3 = PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight - PhotoCropActivity.PhotoCropView.this.rectY - PhotoCropActivity.PhotoCropView.this.rectSizeY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectX += f2;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX -= f2;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY += f3;
                    if (PhotoCropActivity.PhotoCropView.this.rectSizeY < 160.0F) {
                      PhotoCropActivity.PhotoCropView.this.rectSizeY = 160.0F;
                    }
                  }
                }
              }
            }
          } while (PhotoCropActivity.PhotoCropView.this.draggingState != 4);
          float f3 = f2;
          if (PhotoCropActivity.PhotoCropView.this.rectX + PhotoCropActivity.PhotoCropView.this.rectSizeX + f2 > PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth) {
            f3 = PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth - PhotoCropActivity.PhotoCropView.this.rectX - PhotoCropActivity.PhotoCropView.this.rectSizeX;
          }
          if (!PhotoCropActivity.PhotoCropView.this.freeform)
          {
            f1 = f3;
            if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeX + f3 > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight) {
              f1 = PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight - PhotoCropActivity.PhotoCropView.this.rectY - PhotoCropActivity.PhotoCropView.this.rectSizeX;
            }
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
            paramAnonymousView.rectSizeX += f1;
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
          }
          for (paramAnonymousView.rectSizeY += f1;; paramAnonymousView.rectSizeY += f2)
          {
            if (PhotoCropActivity.PhotoCropView.this.rectSizeX < 160.0F) {
              PhotoCropActivity.PhotoCropView.this.rectSizeX = 160.0F;
            }
            if (PhotoCropActivity.PhotoCropView.this.rectSizeY >= 160.0F) {
              break;
            }
            PhotoCropActivity.PhotoCropView.this.rectSizeY = 160.0F;
            break;
            f2 = f1;
            if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeY + f1 > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight) {
              f2 = PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight - PhotoCropActivity.PhotoCropView.this.rectY - PhotoCropActivity.PhotoCropView.this.rectSizeY;
            }
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
            paramAnonymousView.rectSizeX += f3;
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
          }
        }
      });
    }
    
    private void updateBitmapSize()
    {
      if ((this.viewWidth == 0) || (this.viewHeight == 0) || (PhotoCropActivity.this.imageToCrop == null)) {
        return;
      }
      float f1 = (this.rectX - this.bitmapX) / this.bitmapWidth;
      float f2 = (this.rectY - this.bitmapY) / this.bitmapHeight;
      float f3 = this.rectSizeX / this.bitmapWidth;
      float f4 = this.rectSizeY / this.bitmapHeight;
      float f5 = PhotoCropActivity.this.imageToCrop.getWidth();
      float f6 = PhotoCropActivity.this.imageToCrop.getHeight();
      float f7 = this.viewWidth / f5;
      float f8 = this.viewHeight / f6;
      if (f7 > f8)
      {
        this.bitmapHeight = this.viewHeight;
        this.bitmapWidth = ((int)Math.ceil(f5 * f8));
        this.bitmapX = ((this.viewWidth - this.bitmapWidth) / 2 + AndroidUtilities.dp(14.0F));
        this.bitmapY = ((this.viewHeight - this.bitmapHeight) / 2 + AndroidUtilities.dp(14.0F));
        if ((this.rectX != -1.0F) || (this.rectY != -1.0F)) {
          break label408;
        }
        if (!this.freeform) {
          break label293;
        }
        this.rectY = this.bitmapY;
        this.rectX = this.bitmapX;
        this.rectSizeX = this.bitmapWidth;
        this.rectSizeY = this.bitmapHeight;
      }
      for (;;)
      {
        invalidate();
        return;
        this.bitmapWidth = this.viewWidth;
        this.bitmapHeight = ((int)Math.ceil(f6 * f7));
        break;
        label293:
        if (this.bitmapWidth > this.bitmapHeight)
        {
          this.rectY = this.bitmapY;
          this.rectX = ((this.viewWidth - this.bitmapHeight) / 2 + AndroidUtilities.dp(14.0F));
          this.rectSizeX = this.bitmapHeight;
          this.rectSizeY = this.bitmapHeight;
        }
        else
        {
          this.rectX = this.bitmapX;
          this.rectY = ((this.viewHeight - this.bitmapWidth) / 2 + AndroidUtilities.dp(14.0F));
          this.rectSizeX = this.bitmapWidth;
          this.rectSizeY = this.bitmapWidth;
          continue;
          label408:
          this.rectX = (this.bitmapWidth * f1 + this.bitmapX);
          this.rectY = (this.bitmapHeight * f2 + this.bitmapY);
          this.rectSizeX = (this.bitmapWidth * f3);
          this.rectSizeY = (this.bitmapHeight * f4);
        }
      }
    }
    
    public Bitmap getBitmap()
    {
      float f1 = (this.rectX - this.bitmapX) / this.bitmapWidth;
      float f2 = (this.rectY - this.bitmapY) / this.bitmapHeight;
      float f3 = this.rectSizeX / this.bitmapWidth;
      float f4 = this.rectSizeY / this.bitmapWidth;
      int j = (int)(PhotoCropActivity.this.imageToCrop.getWidth() * f1);
      int k = (int)(PhotoCropActivity.this.imageToCrop.getHeight() * f2);
      int m = (int)(PhotoCropActivity.this.imageToCrop.getWidth() * f3);
      int n = (int)(PhotoCropActivity.this.imageToCrop.getWidth() * f4);
      int i = j;
      if (j < 0) {
        i = 0;
      }
      j = k;
      if (k < 0) {
        j = 0;
      }
      k = m;
      if (i + m > PhotoCropActivity.this.imageToCrop.getWidth()) {
        k = PhotoCropActivity.this.imageToCrop.getWidth() - i;
      }
      m = n;
      if (j + n > PhotoCropActivity.this.imageToCrop.getHeight()) {
        m = PhotoCropActivity.this.imageToCrop.getHeight() - j;
      }
      try
      {
        Bitmap localBitmap1 = Bitmaps.createBitmap(PhotoCropActivity.this.imageToCrop, i, j, k, m);
        return localBitmap1;
      }
      catch (Throwable localThrowable1)
      {
        FileLog.e("tmessags", localThrowable1);
        System.gc();
        try
        {
          Bitmap localBitmap2 = Bitmaps.createBitmap(PhotoCropActivity.this.imageToCrop, i, j, k, m);
          return localBitmap2;
        }
        catch (Throwable localThrowable2)
        {
          FileLog.e("tmessages", localThrowable2);
        }
      }
      return null;
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      if (PhotoCropActivity.this.drawable != null) {}
      try
      {
        PhotoCropActivity.this.drawable.setBounds(this.bitmapX, this.bitmapY, this.bitmapX + this.bitmapWidth, this.bitmapY + this.bitmapHeight);
        PhotoCropActivity.this.drawable.draw(paramCanvas);
        paramCanvas.drawRect(this.bitmapX, this.bitmapY, this.bitmapX + this.bitmapWidth, this.rectY, this.halfPaint);
        float f1 = this.bitmapX;
        float f2 = this.rectY;
        float f3 = this.rectX;
        float f4 = this.rectY;
        paramCanvas.drawRect(f1, f2, f3, this.rectSizeY + f4, this.halfPaint);
        f1 = this.rectX;
        f2 = this.rectSizeX;
        f3 = this.rectY;
        f4 = this.bitmapX + this.bitmapWidth;
        float f5 = this.rectY;
        paramCanvas.drawRect(f2 + f1, f3, f4, this.rectSizeY + f5, this.halfPaint);
        f1 = this.bitmapX;
        f2 = this.rectY;
        paramCanvas.drawRect(f1, this.rectSizeY + f2, this.bitmapX + this.bitmapWidth, this.bitmapY + this.bitmapHeight, this.halfPaint);
        f1 = this.rectX;
        f2 = this.rectY;
        f3 = this.rectX;
        f4 = this.rectSizeX;
        f5 = this.rectY;
        paramCanvas.drawRect(f1, f2, f4 + f3, this.rectSizeY + f5, this.rectPaint);
        int j = AndroidUtilities.dp(1.0F);
        f1 = this.rectX;
        f2 = j;
        f3 = this.rectY;
        f4 = j;
        f5 = this.rectX;
        float f6 = j;
        float f7 = AndroidUtilities.dp(20.0F);
        float f8 = this.rectY;
        paramCanvas.drawRect(f2 + f1, f4 + f3, f7 + (f5 + f6), j * 3 + f8, this.circlePaint);
        f1 = this.rectX;
        f2 = j;
        f3 = this.rectY;
        f4 = j;
        f5 = this.rectX;
        f6 = j * 3;
        f7 = this.rectY;
        f8 = j;
        paramCanvas.drawRect(f2 + f1, f4 + f3, f6 + f5, AndroidUtilities.dp(20.0F) + (f7 + f8), this.circlePaint);
        f1 = this.rectX;
        f2 = this.rectSizeX;
        f3 = j;
        f4 = AndroidUtilities.dp(20.0F);
        f5 = this.rectY;
        f6 = j;
        f7 = this.rectX;
        f8 = this.rectSizeX;
        float f9 = j;
        float f10 = this.rectY;
        paramCanvas.drawRect(f1 + f2 - f3 - f4, f6 + f5, f7 + f8 - f9, j * 3 + f10, this.circlePaint);
        f1 = this.rectX;
        f2 = this.rectSizeX;
        f3 = j * 3;
        f4 = this.rectY;
        f5 = j;
        f6 = this.rectX;
        f7 = this.rectSizeX;
        f8 = j;
        f9 = this.rectY;
        f10 = j;
        paramCanvas.drawRect(f1 + f2 - f3, f5 + f4, f6 + f7 - f8, AndroidUtilities.dp(20.0F) + (f9 + f10), this.circlePaint);
        f1 = this.rectX;
        f2 = j;
        f3 = this.rectY;
        f4 = this.rectSizeY;
        f5 = j;
        f6 = AndroidUtilities.dp(20.0F);
        f7 = this.rectX;
        paramCanvas.drawRect(f2 + f1, f3 + f4 - f5 - f6, j * 3 + f7, this.rectY + this.rectSizeY - j, this.circlePaint);
        f1 = this.rectX;
        f2 = j;
        f3 = this.rectY;
        f4 = this.rectSizeY;
        f5 = j * 3;
        f6 = this.rectX;
        f7 = j;
        paramCanvas.drawRect(f2 + f1, f3 + f4 - f5, AndroidUtilities.dp(20.0F) + (f6 + f7), this.rectY + this.rectSizeY - j, this.circlePaint);
        paramCanvas.drawRect(this.rectX + this.rectSizeX - j - AndroidUtilities.dp(20.0F), this.rectY + this.rectSizeY - j * 3, this.rectX + this.rectSizeX - j, this.rectY + this.rectSizeY - j, this.circlePaint);
        paramCanvas.drawRect(this.rectX + this.rectSizeX - j * 3, this.rectY + this.rectSizeY - j - AndroidUtilities.dp(20.0F), this.rectX + this.rectSizeX - j, this.rectY + this.rectSizeY - j, this.circlePaint);
        int i = 1;
        while (i < 3)
        {
          f1 = this.rectX;
          f2 = this.rectSizeX / 3.0F;
          f3 = i;
          f4 = this.rectY;
          f5 = j;
          f6 = this.rectX;
          f7 = j;
          paramCanvas.drawRect(f2 * f3 + f1, f5 + f4, this.rectSizeX / 3.0F * i + (f6 + f7), this.rectY + this.rectSizeY - j, this.circlePaint);
          f1 = this.rectX;
          f2 = j;
          f3 = this.rectY;
          f4 = this.rectSizeY / 3.0F;
          f5 = i;
          f6 = this.rectX;
          f7 = j;
          f8 = this.rectSizeX;
          f9 = this.rectY;
          f10 = this.rectSizeY / 3.0F;
          float f11 = i;
          paramCanvas.drawRect(f2 + f1, f4 * f5 + f3, f8 + (f6 - f7), j + (f9 + f10 * f11), this.circlePaint);
          i += 1;
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          FileLog.e("tmessages", localThrowable);
        }
      }
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      this.viewWidth = (paramInt3 - paramInt1 - AndroidUtilities.dp(28.0F));
      this.viewHeight = (paramInt4 - paramInt2 - AndroidUtilities.dp(28.0F));
      updateBitmapSize();
    }
  }
  
  public static abstract interface PhotoEditActivityDelegate
  {
    public abstract void didFinishEdit(Bitmap paramBitmap);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PhotoCropActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */