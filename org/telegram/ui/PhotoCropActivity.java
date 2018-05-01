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
    this.actionBar.setItemsBackgroundColor(-12763843, false);
    this.actionBar.setTitleColor(-1);
    this.actionBar.setBackButtonImage(NUM);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("CropImage", NUM));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          PhotoCropActivity.this.finishFragment();
        }
        for (;;)
        {
          return;
          if (paramAnonymousInt == 1)
          {
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
        }
      }
    });
    this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0F));
    paramContext = new PhotoCropView(paramContext);
    this.view = paramContext;
    this.fragmentView = paramContext;
    ((PhotoCropView)this.fragmentView).freeform = getArguments().getBoolean("freeform", false);
    this.fragmentView.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
    return this.fragmentView;
  }
  
  public boolean onFragmentCreate()
  {
    boolean bool1 = false;
    this.swipeBackEnabled = false;
    String str;
    Uri localUri;
    boolean bool2;
    if (this.imageToCrop == null)
    {
      str = getArguments().getString("photoPath");
      localUri = (Uri)getArguments().getParcelable("photoUri");
      if ((str == null) && (localUri == null)) {
        bool2 = bool1;
      }
      do
      {
        return bool2;
        if (str == null) {
          break;
        }
        bool2 = bool1;
      } while (!new File(str).exists());
      if (!AndroidUtilities.isTablet()) {
        break label137;
      }
    }
    label137:
    for (int i = AndroidUtilities.dp(520.0F);; i = Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y))
    {
      this.imageToCrop = ImageLoader.loadBitmap(str, localUri, i, i, true);
      bool2 = bool1;
      if (this.imageToCrop == null) {
        break;
      }
      this.drawable = new BitmapDrawable(this.imageToCrop);
      super.onFragmentCreate();
      bool2 = true;
      break;
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
      this.rectPaint.setColor(NUM);
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
          float f1 = paramAnonymousMotionEvent.getX();
          float f2 = paramAnonymousMotionEvent.getY();
          int i = AndroidUtilities.dp(14.0F);
          if (paramAnonymousMotionEvent.getAction() == 0) {
            if ((PhotoCropActivity.PhotoCropView.this.rectX - i < f1) && (PhotoCropActivity.PhotoCropView.this.rectX + i > f1) && (PhotoCropActivity.PhotoCropView.this.rectY - i < f2) && (PhotoCropActivity.PhotoCropView.this.rectY + i > f2))
            {
              PhotoCropActivity.PhotoCropView.this.draggingState = 1;
              if (PhotoCropActivity.PhotoCropView.this.draggingState != 0) {
                PhotoCropActivity.PhotoCropView.this.requestDisallowInterceptTouchEvent(true);
              }
              PhotoCropActivity.PhotoCropView.this.oldX = f1;
              PhotoCropActivity.PhotoCropView.this.oldY = f2;
            }
          }
          label538:
          do
          {
            for (;;)
            {
              return true;
              if ((PhotoCropActivity.PhotoCropView.this.rectX - i + PhotoCropActivity.PhotoCropView.this.rectSizeX < f1) && (PhotoCropActivity.PhotoCropView.this.rectX + i + PhotoCropActivity.PhotoCropView.this.rectSizeX > f1) && (PhotoCropActivity.PhotoCropView.this.rectY - i < f2) && (PhotoCropActivity.PhotoCropView.this.rectY + i > f2))
              {
                PhotoCropActivity.PhotoCropView.this.draggingState = 2;
                break;
              }
              if ((PhotoCropActivity.PhotoCropView.this.rectX - i < f1) && (PhotoCropActivity.PhotoCropView.this.rectX + i > f1) && (PhotoCropActivity.PhotoCropView.this.rectY - i + PhotoCropActivity.PhotoCropView.this.rectSizeY < f2) && (PhotoCropActivity.PhotoCropView.this.rectY + i + PhotoCropActivity.PhotoCropView.this.rectSizeY > f2))
              {
                PhotoCropActivity.PhotoCropView.this.draggingState = 3;
                break;
              }
              if ((PhotoCropActivity.PhotoCropView.this.rectX - i + PhotoCropActivity.PhotoCropView.this.rectSizeX < f1) && (PhotoCropActivity.PhotoCropView.this.rectX + i + PhotoCropActivity.PhotoCropView.this.rectSizeX > f1) && (PhotoCropActivity.PhotoCropView.this.rectY - i + PhotoCropActivity.PhotoCropView.this.rectSizeY < f2) && (PhotoCropActivity.PhotoCropView.this.rectY + i + PhotoCropActivity.PhotoCropView.this.rectSizeY > f2))
              {
                PhotoCropActivity.PhotoCropView.this.draggingState = 4;
                break;
              }
              if ((PhotoCropActivity.PhotoCropView.this.rectX < f1) && (PhotoCropActivity.PhotoCropView.this.rectX + PhotoCropActivity.PhotoCropView.this.rectSizeX > f1) && (PhotoCropActivity.PhotoCropView.this.rectY < f2) && (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeY > f2))
              {
                PhotoCropActivity.PhotoCropView.this.draggingState = 5;
                break;
              }
              PhotoCropActivity.PhotoCropView.this.draggingState = 0;
              break;
              if (paramAnonymousMotionEvent.getAction() != 1) {
                break label538;
              }
              PhotoCropActivity.PhotoCropView.this.draggingState = 0;
            }
          } while ((paramAnonymousMotionEvent.getAction() != 2) || (PhotoCropActivity.PhotoCropView.this.draggingState == 0));
          float f3 = f1 - PhotoCropActivity.PhotoCropView.this.oldX;
          float f4 = f2 - PhotoCropActivity.PhotoCropView.this.oldY;
          if (PhotoCropActivity.PhotoCropView.this.draggingState == 5)
          {
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
            paramAnonymousView.rectX += f3;
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
            paramAnonymousView.rectY += f4;
            if (PhotoCropActivity.PhotoCropView.this.rectX < PhotoCropActivity.PhotoCropView.this.bitmapX)
            {
              PhotoCropActivity.PhotoCropView.this.rectX = PhotoCropActivity.PhotoCropView.this.bitmapX;
              label656:
              if (PhotoCropActivity.PhotoCropView.this.rectY >= PhotoCropActivity.PhotoCropView.this.bitmapY) {
                break label786;
              }
              PhotoCropActivity.PhotoCropView.this.rectY = PhotoCropActivity.PhotoCropView.this.bitmapY;
            }
          }
          label786:
          label1932:
          do
          {
            for (;;)
            {
              PhotoCropActivity.PhotoCropView.this.oldX = f1;
              PhotoCropActivity.PhotoCropView.this.oldY = f2;
              PhotoCropActivity.PhotoCropView.this.invalidate();
              break;
              if (PhotoCropActivity.PhotoCropView.this.rectX + PhotoCropActivity.PhotoCropView.this.rectSizeX <= PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth) {
                break label656;
              }
              PhotoCropActivity.PhotoCropView.this.rectX = (PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth - PhotoCropActivity.PhotoCropView.this.rectSizeX);
              break label656;
              if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeY > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight)
              {
                PhotoCropActivity.PhotoCropView.this.rectY = (PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight - PhotoCropActivity.PhotoCropView.this.rectSizeY);
                continue;
                if (PhotoCropActivity.PhotoCropView.this.draggingState == 1)
                {
                  f5 = f3;
                  if (PhotoCropActivity.PhotoCropView.this.rectSizeX - f3 < 160.0F) {
                    f5 = PhotoCropActivity.PhotoCropView.this.rectSizeX - 160.0F;
                  }
                  f3 = f5;
                  if (PhotoCropActivity.PhotoCropView.this.rectX + f5 < PhotoCropActivity.PhotoCropView.this.bitmapX) {
                    f3 = PhotoCropActivity.PhotoCropView.this.bitmapX - PhotoCropActivity.PhotoCropView.this.rectX;
                  }
                  if (!PhotoCropActivity.PhotoCropView.this.freeform)
                  {
                    f4 = f3;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + f3 < PhotoCropActivity.PhotoCropView.this.bitmapY) {
                      f4 = PhotoCropActivity.PhotoCropView.this.bitmapY - PhotoCropActivity.PhotoCropView.this.rectY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectX += f4;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectY += f4;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX -= f4;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY -= f4;
                  }
                  else
                  {
                    f5 = f4;
                    if (PhotoCropActivity.PhotoCropView.this.rectSizeY - f4 < 160.0F) {
                      f5 = PhotoCropActivity.PhotoCropView.this.rectSizeY - 160.0F;
                    }
                    f4 = f5;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + f5 < PhotoCropActivity.PhotoCropView.this.bitmapY) {
                      f4 = PhotoCropActivity.PhotoCropView.this.bitmapY - PhotoCropActivity.PhotoCropView.this.rectY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectX += f3;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectY += f4;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX -= f3;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY -= f4;
                  }
                }
                else if (PhotoCropActivity.PhotoCropView.this.draggingState == 2)
                {
                  f5 = f3;
                  if (PhotoCropActivity.PhotoCropView.this.rectSizeX + f3 < 160.0F) {
                    f5 = -(PhotoCropActivity.PhotoCropView.this.rectSizeX - 160.0F);
                  }
                  f3 = f5;
                  if (PhotoCropActivity.PhotoCropView.this.rectX + PhotoCropActivity.PhotoCropView.this.rectSizeX + f5 > PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth) {
                    f3 = PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth - PhotoCropActivity.PhotoCropView.this.rectX - PhotoCropActivity.PhotoCropView.this.rectSizeX;
                  }
                  if (!PhotoCropActivity.PhotoCropView.this.freeform)
                  {
                    f4 = f3;
                    if (PhotoCropActivity.PhotoCropView.this.rectY - f3 < PhotoCropActivity.PhotoCropView.this.bitmapY) {
                      f4 = PhotoCropActivity.PhotoCropView.this.rectY - PhotoCropActivity.PhotoCropView.this.bitmapY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectY -= f4;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX += f4;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY += f4;
                  }
                  else
                  {
                    f5 = f4;
                    if (PhotoCropActivity.PhotoCropView.this.rectSizeY - f4 < 160.0F) {
                      f5 = PhotoCropActivity.PhotoCropView.this.rectSizeY - 160.0F;
                    }
                    f4 = f5;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + f5 < PhotoCropActivity.PhotoCropView.this.bitmapY) {
                      f4 = PhotoCropActivity.PhotoCropView.this.bitmapY - PhotoCropActivity.PhotoCropView.this.rectY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectY += f4;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX += f3;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY -= f4;
                  }
                }
                else
                {
                  if (PhotoCropActivity.PhotoCropView.this.draggingState != 3) {
                    break label1932;
                  }
                  f5 = f3;
                  if (PhotoCropActivity.PhotoCropView.this.rectSizeX - f3 < 160.0F) {
                    f5 = PhotoCropActivity.PhotoCropView.this.rectSizeX - 160.0F;
                  }
                  f3 = f5;
                  if (PhotoCropActivity.PhotoCropView.this.rectX + f5 < PhotoCropActivity.PhotoCropView.this.bitmapX) {
                    f3 = PhotoCropActivity.PhotoCropView.this.bitmapX - PhotoCropActivity.PhotoCropView.this.rectX;
                  }
                  if (!PhotoCropActivity.PhotoCropView.this.freeform)
                  {
                    f4 = f3;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeX - f3 > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight) {
                      f4 = PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeX - PhotoCropActivity.PhotoCropView.this.bitmapY - PhotoCropActivity.PhotoCropView.this.bitmapHeight;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectX += f4;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX -= f4;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY -= f4;
                  }
                  else
                  {
                    f5 = f4;
                    if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeY + f4 > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight) {
                      f5 = PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight - PhotoCropActivity.PhotoCropView.this.rectY - PhotoCropActivity.PhotoCropView.this.rectSizeY;
                    }
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectX += f3;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeX -= f3;
                    paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
                    paramAnonymousView.rectSizeY += f5;
                    if (PhotoCropActivity.PhotoCropView.this.rectSizeY < 160.0F) {
                      PhotoCropActivity.PhotoCropView.this.rectSizeY = 160.0F;
                    }
                  }
                }
              }
            }
          } while (PhotoCropActivity.PhotoCropView.this.draggingState != 4);
          float f5 = f3;
          if (PhotoCropActivity.PhotoCropView.this.rectX + PhotoCropActivity.PhotoCropView.this.rectSizeX + f3 > PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth) {
            f5 = PhotoCropActivity.PhotoCropView.this.bitmapX + PhotoCropActivity.PhotoCropView.this.bitmapWidth - PhotoCropActivity.PhotoCropView.this.rectX - PhotoCropActivity.PhotoCropView.this.rectSizeX;
          }
          if (!PhotoCropActivity.PhotoCropView.this.freeform)
          {
            f4 = f5;
            if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeX + f5 > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight) {
              f4 = PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight - PhotoCropActivity.PhotoCropView.this.rectY - PhotoCropActivity.PhotoCropView.this.rectSizeX;
            }
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
            paramAnonymousView.rectSizeX += f4;
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
          }
          for (paramAnonymousView.rectSizeY += f4;; paramAnonymousView.rectSizeY += f3)
          {
            if (PhotoCropActivity.PhotoCropView.this.rectSizeX < 160.0F) {
              PhotoCropActivity.PhotoCropView.this.rectSizeX = 160.0F;
            }
            if (PhotoCropActivity.PhotoCropView.this.rectSizeY >= 160.0F) {
              break;
            }
            PhotoCropActivity.PhotoCropView.this.rectSizeY = 160.0F;
            break;
            f3 = f4;
            if (PhotoCropActivity.PhotoCropView.this.rectY + PhotoCropActivity.PhotoCropView.this.rectSizeY + f4 > PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight) {
              f3 = PhotoCropActivity.PhotoCropView.this.bitmapY + PhotoCropActivity.PhotoCropView.this.bitmapHeight - PhotoCropActivity.PhotoCropView.this.rectY - PhotoCropActivity.PhotoCropView.this.rectSizeY;
            }
            paramAnonymousView = PhotoCropActivity.PhotoCropView.this;
            paramAnonymousView.rectSizeX += f5;
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
        label158:
        this.bitmapX = ((this.viewWidth - this.bitmapWidth) / 2 + AndroidUtilities.dp(14.0F));
        this.bitmapY = ((this.viewHeight - this.bitmapHeight) / 2 + AndroidUtilities.dp(14.0F));
        if ((this.rectX != -1.0F) || (this.rectY != -1.0F)) {
          break label410;
        }
        if (!this.freeform) {
          break label295;
        }
        this.rectY = this.bitmapY;
        this.rectX = this.bitmapX;
        this.rectSizeX = this.bitmapWidth;
        this.rectSizeY = this.bitmapHeight;
      }
      for (;;)
      {
        invalidate();
        break;
        this.bitmapWidth = this.viewWidth;
        this.bitmapHeight = ((int)Math.ceil(f6 * f7));
        break label158;
        label295:
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
          label410:
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
      int i = (int)(PhotoCropActivity.this.imageToCrop.getWidth() * f1);
      int j = (int)(PhotoCropActivity.this.imageToCrop.getHeight() * f2);
      int k = (int)(PhotoCropActivity.this.imageToCrop.getWidth() * f3);
      int m = (int)(PhotoCropActivity.this.imageToCrop.getWidth() * f4);
      int n = i;
      if (i < 0) {
        n = 0;
      }
      i = j;
      if (j < 0) {
        i = 0;
      }
      j = k;
      if (n + k > PhotoCropActivity.this.imageToCrop.getWidth()) {
        j = PhotoCropActivity.this.imageToCrop.getWidth() - n;
      }
      k = m;
      if (i + m > PhotoCropActivity.this.imageToCrop.getHeight()) {
        k = PhotoCropActivity.this.imageToCrop.getHeight() - i;
      }
      try
      {
        Bitmap localBitmap1 = Bitmaps.createBitmap(PhotoCropActivity.this.imageToCrop, n, i, j, k);
        return localBitmap1;
      }
      catch (Throwable localThrowable1)
      {
        for (;;)
        {
          FileLog.e(localThrowable1);
          System.gc();
          try
          {
            Bitmap localBitmap2 = Bitmaps.createBitmap(PhotoCropActivity.this.imageToCrop, n, i, j, k);
          }
          catch (Throwable localThrowable2)
          {
            FileLog.e(localThrowable2);
            Object localObject = null;
          }
        }
      }
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
        f2 = this.rectX;
        f1 = this.rectSizeX;
        float f5 = this.rectY;
        f3 = this.bitmapX + this.bitmapWidth;
        f4 = this.rectY;
        paramCanvas.drawRect(f1 + f2, f5, f3, this.rectSizeY + f4, this.halfPaint);
        f1 = this.bitmapX;
        f4 = this.rectY;
        paramCanvas.drawRect(f1, this.rectSizeY + f4, this.bitmapX + this.bitmapWidth, this.bitmapY + this.bitmapHeight, this.halfPaint);
        f3 = this.rectX;
        f5 = this.rectY;
        f1 = this.rectX;
        f4 = this.rectSizeX;
        f2 = this.rectY;
        paramCanvas.drawRect(f3, f5, f4 + f1, this.rectSizeY + f2, this.rectPaint);
        int i = AndroidUtilities.dp(1.0F);
        f4 = this.rectX;
        f3 = i;
        f2 = this.rectY;
        float f6 = i;
        f5 = this.rectX;
        f1 = i;
        float f7 = AndroidUtilities.dp(20.0F);
        float f8 = this.rectY;
        paramCanvas.drawRect(f3 + f4, f6 + f2, f7 + (f5 + f1), i * 3 + f8, this.circlePaint);
        f8 = this.rectX;
        f5 = i;
        f7 = this.rectY;
        f4 = i;
        f2 = this.rectX;
        f6 = i * 3;
        f3 = this.rectY;
        f1 = i;
        paramCanvas.drawRect(f5 + f8, f4 + f7, f6 + f2, AndroidUtilities.dp(20.0F) + (f3 + f1), this.circlePaint);
        f5 = this.rectX;
        f2 = this.rectSizeX;
        f3 = i;
        float f9 = AndroidUtilities.dp(20.0F);
        f7 = this.rectY;
        f4 = i;
        f6 = this.rectX;
        float f10 = this.rectSizeX;
        f8 = i;
        f1 = this.rectY;
        paramCanvas.drawRect(f5 + f2 - f3 - f9, f4 + f7, f6 + f10 - f8, i * 3 + f1, this.circlePaint);
        f10 = this.rectX;
        f3 = this.rectSizeX;
        f6 = i * 3;
        f7 = this.rectY;
        f8 = i;
        f1 = this.rectX;
        f5 = this.rectSizeX;
        f4 = i;
        f9 = this.rectY;
        f2 = i;
        paramCanvas.drawRect(f10 + f3 - f6, f8 + f7, f1 + f5 - f4, AndroidUtilities.dp(20.0F) + (f9 + f2), this.circlePaint);
        f4 = this.rectX;
        f2 = i;
        f5 = this.rectY;
        f8 = this.rectSizeY;
        f7 = i;
        f3 = AndroidUtilities.dp(20.0F);
        f1 = this.rectX;
        paramCanvas.drawRect(f2 + f4, f5 + f8 - f7 - f3, i * 3 + f1, this.rectY + this.rectSizeY - i, this.circlePaint);
        f2 = this.rectX;
        f4 = i;
        f7 = this.rectY;
        f5 = this.rectSizeY;
        f8 = i * 3;
        f1 = this.rectX;
        f3 = i;
        paramCanvas.drawRect(f4 + f2, f7 + f5 - f8, AndroidUtilities.dp(20.0F) + (f1 + f3), this.rectY + this.rectSizeY - i, this.circlePaint);
        paramCanvas.drawRect(this.rectX + this.rectSizeX - i - AndroidUtilities.dp(20.0F), this.rectY + this.rectSizeY - i * 3, this.rectX + this.rectSizeX - i, this.rectY + this.rectSizeY - i, this.circlePaint);
        paramCanvas.drawRect(this.rectX + this.rectSizeX - i * 3, this.rectY + this.rectSizeY - i - AndroidUtilities.dp(20.0F), this.rectX + this.rectSizeX - i, this.rectY + this.rectSizeY - i, this.circlePaint);
        for (int j = 1; j < 3; j++)
        {
          f1 = this.rectX;
          f3 = this.rectSizeX / 3.0F;
          f7 = j;
          f4 = this.rectY;
          f8 = i;
          f2 = this.rectX;
          f5 = i;
          paramCanvas.drawRect(f3 * f7 + f1, f8 + f4, this.rectSizeX / 3.0F * j + (f2 + f5), this.rectY + this.rectSizeY - i, this.circlePaint);
          f5 = this.rectX;
          f7 = i;
          f10 = this.rectY;
          float f11 = this.rectSizeY / 3.0F;
          f3 = j;
          f8 = this.rectX;
          f4 = i;
          f2 = this.rectSizeX;
          f6 = this.rectY;
          f9 = this.rectSizeY / 3.0F;
          f1 = j;
          paramCanvas.drawRect(f7 + f5, f11 * f3 + f10, f2 + (f8 - f4), i + (f6 + f9 * f1), this.circlePaint);
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          FileLog.e(localThrowable);
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