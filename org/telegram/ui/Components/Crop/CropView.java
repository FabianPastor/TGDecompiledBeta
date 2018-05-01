package org.telegram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;

public class CropView
  extends FrameLayout
  implements CropAreaView.AreaViewListener, CropGestureDetector.CropGestureListener
{
  private static final float EPSILON = 1.0E-5F;
  private static final float MAX_SCALE = 30.0F;
  private static final int RESULT_SIDE = 1280;
  private boolean animating = false;
  private CropAreaView areaView;
  private View backView;
  private Bitmap bitmap;
  private float bottomPadding;
  private CropGestureDetector detector;
  private boolean freeform;
  private boolean hasAspectRatioDialog;
  private ImageView imageView;
  private RectF initialAreaRect = new RectF();
  private CropViewListener listener;
  private Matrix presentationMatrix = new Matrix();
  private RectF previousAreaRect = new RectF();
  private float rotationStartScale;
  private CropState state;
  private Matrix tempMatrix = new Matrix();
  private CropRectangle tempRect = new CropRectangle();
  
  public CropView(Context paramContext)
  {
    super(paramContext);
    this.backView = new View(paramContext);
    this.backView.setBackgroundColor(-16777216);
    this.backView.setVisibility(4);
    addView(this.backView);
    this.imageView = new ImageView(paramContext);
    this.imageView.setDrawingCacheEnabled(true);
    this.imageView.setScaleType(ImageView.ScaleType.MATRIX);
    addView(this.imageView);
    this.detector = new CropGestureDetector(paramContext);
    this.detector.setOnGestureListener(this);
    this.areaView = new CropAreaView(paramContext);
    this.areaView.setListener(this);
    addView(this.areaView);
  }
  
  private void fillAreaView(RectF paramRectF, final boolean paramBoolean)
  {
    float f1 = Math.max(paramRectF.width() / this.areaView.getCropWidth(), paramRectF.height() / this.areaView.getCropHeight());
    float f2 = this.state.getScale();
    paramBoolean = false;
    final float f3 = f1;
    if (f2 * f1 > 30.0F)
    {
      f3 = 30.0F / this.state.getScale();
      paramBoolean = true;
    }
    if (Build.VERSION.SDK_INT >= 21) {}
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      float f4 = i;
      f2 = (paramRectF.centerX() - this.imageView.getWidth() / 2) / this.areaView.getCropWidth();
      f1 = this.state.getOrientedWidth();
      float f5 = (paramRectF.centerY() - (this.imageView.getHeight() - this.bottomPadding + f4) / 2.0F) / this.areaView.getCropHeight();
      f4 = this.state.getOrientedHeight();
      ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
          f = ((f3 - 1.0F) * f + 1.0F) / this.val$currentScale[0];
          paramAnonymousValueAnimator = this.val$currentScale;
          paramAnonymousValueAnimator[0] *= f;
          CropView.CropState.access$1200(CropView.this.state, f, this.val$x, this.val$y);
          CropView.this.updateMatrix();
        }
      });
      localValueAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramBoolean) {
            CropView.this.fitContentInBounds(false, false, true);
          }
        }
      });
      this.areaView.fill(paramRectF, localValueAnimator, true);
      this.initialAreaRect.set(paramRectF);
      return;
    }
  }
  
  private void fitContentInBounds(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    fitContentInBounds(paramBoolean1, paramBoolean2, paramBoolean3, false);
  }
  
  private void fitContentInBounds(final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3, final boolean paramBoolean4)
  {
    float f1 = this.areaView.getCropWidth();
    float f2 = this.areaView.getCropHeight();
    float f3 = this.state.getOrientedWidth();
    final float f4 = this.state.getOrientedHeight();
    final float f5 = this.state.getRotation();
    final float f6 = (float)Math.toRadians(f5);
    RectF localRectF = calculateBoundingBox(f1, f2, f5);
    Object localObject1 = new RectF(0.0F, 0.0F, f3, f4);
    f1 = (f1 - f3) / 2.0F;
    float f7 = (f2 - f4) / 2.0F;
    f2 = this.state.getScale();
    this.tempRect.setRect((RectF)localObject1);
    Object localObject2 = this.state.getMatrix();
    ((Matrix)localObject2).preTranslate(f1 / f2, f7 / f2);
    this.tempMatrix.reset();
    this.tempMatrix.setTranslate(((RectF)localObject1).centerX(), ((RectF)localObject1).centerY());
    this.tempMatrix.setConcat(this.tempMatrix, (Matrix)localObject2);
    this.tempMatrix.preTranslate(-((RectF)localObject1).centerX(), -((RectF)localObject1).centerY());
    this.tempRect.applyMatrix(this.tempMatrix);
    this.tempMatrix.reset();
    this.tempMatrix.preRotate(-f5, f3 / 2.0F, f4 / 2.0F);
    this.tempRect.applyMatrix(this.tempMatrix);
    this.tempRect.getRect((RectF)localObject1);
    localObject2 = new PointF(this.state.getX(), this.state.getY());
    f5 = f2;
    if (!((RectF)localObject1).contains(localRectF))
    {
      f4 = f5;
      if (paramBoolean1) {
        if (localRectF.width() <= ((RectF)localObject1).width())
        {
          f4 = f5;
          if (localRectF.height() <= ((RectF)localObject1).height()) {}
        }
        else
        {
          f4 = fitScale((RectF)localObject1, f2, localRectF.width() / scaleWidthToMaxSize(localRectF, (RectF)localObject1));
        }
      }
      fitTranslation((RectF)localObject1, localRectF, (PointF)localObject2, f6);
      f5 = ((PointF)localObject2).x - this.state.getX();
      f6 = ((PointF)localObject2).y - this.state.getY();
      if (!paramBoolean3) {
        break label655;
      }
      f4 /= f2;
      if ((Math.abs(f4 - 1.0F) >= 1.0E-5F) || (Math.abs(f5) >= 1.0E-5F) || (Math.abs(f6) >= 1.0E-5F)) {
        break label536;
      }
    }
    for (;;)
    {
      return;
      f4 = f5;
      if (!paramBoolean2) {
        break;
      }
      f4 = f5;
      if (this.rotationStartScale <= 0.0F) {
        break;
      }
      f5 = localRectF.width() / scaleWidthToMaxSize(localRectF, (RectF)localObject1);
      f4 = f5;
      if (this.state.getScale() * f5 < this.rotationStartScale) {
        f4 = 1.0F;
      }
      f4 = fitScale((RectF)localObject1, f2, f4);
      fitTranslation((RectF)localObject1, localRectF, (PointF)localObject2, f6);
      break;
      label536:
      this.animating = true;
      localObject1 = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      ((ValueAnimator)localObject1).addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
        {
          float f1 = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
          float f2 = f5 * f1 - this.val$currentValues[1];
          paramAnonymousValueAnimator = this.val$currentValues;
          paramAnonymousValueAnimator[1] += f2;
          float f3 = f6 * f1 - this.val$currentValues[2];
          paramAnonymousValueAnimator = this.val$currentValues;
          paramAnonymousValueAnimator[2] += f3;
          CropView.CropState.access$1800(CropView.this.state, this.val$currentValues[0] * f2, this.val$currentValues[0] * f3);
          f2 = ((f4 - 1.0F) * f1 + 1.0F) / this.val$currentValues[0];
          paramAnonymousValueAnimator = this.val$currentValues;
          paramAnonymousValueAnimator[0] *= f2;
          CropView.CropState.access$1200(CropView.this.state, f2, 0.0F, 0.0F);
          CropView.this.updateMatrix();
        }
      });
      ((ValueAnimator)localObject1).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          CropView.access$1902(CropView.this, false);
          if (!paramBoolean4) {
            CropView.this.fitContentInBounds(paramBoolean1, paramBoolean2, paramBoolean3, true);
          }
        }
      });
      ((ValueAnimator)localObject1).setInterpolator(this.areaView.getInterpolator());
      if (paramBoolean4) {}
      for (long l = 100L;; l = 200L)
      {
        ((ValueAnimator)localObject1).setDuration(l);
        ((ValueAnimator)localObject1).start();
        break;
      }
      label655:
      this.state.translate(f5, f6);
      this.state.scale(f4 / f2, 0.0F, 0.0F);
      updateMatrix();
    }
  }
  
  private float fitScale(RectF paramRectF, float paramFloat1, float paramFloat2)
  {
    float f1 = paramRectF.width() * paramFloat2;
    float f2 = paramRectF.height() * paramFloat2;
    float f3 = (paramRectF.width() - f1) / 2.0F;
    float f4 = (paramRectF.height() - f2) / 2.0F;
    paramRectF.set(paramRectF.left + f3, paramRectF.top + f4, paramRectF.left + f3 + f1, paramRectF.top + f4 + f2);
    return paramFloat1 * paramFloat2;
  }
  
  private void fitTranslation(RectF paramRectF1, RectF paramRectF2, PointF paramPointF, float paramFloat)
  {
    float f1 = paramRectF2.left;
    float f2 = paramRectF2.top;
    float f3 = paramRectF2.right;
    float f4 = paramRectF2.bottom;
    float f5 = f1;
    float f6 = f3;
    if (paramRectF1.left > f1)
    {
      f6 = f3 + (paramRectF1.left - f1);
      f5 = paramRectF1.left;
    }
    f3 = f4;
    f1 = f2;
    if (paramRectF1.top > f2)
    {
      f3 = f4 + (paramRectF1.top - f2);
      f1 = paramRectF1.top;
    }
    f2 = f5;
    if (paramRectF1.right < f6) {
      f2 = f5 + (paramRectF1.right - f6);
    }
    f5 = f1;
    if (paramRectF1.bottom < f3) {
      f5 = f1 + (paramRectF1.bottom - f3);
    }
    f1 = paramRectF2.centerX() - (paramRectF2.width() / 2.0F + f2);
    f6 = paramRectF2.centerY() - (paramRectF2.height() / 2.0F + f5);
    f5 = (float)(Math.sin(1.5707963267948966D - paramFloat) * f1);
    f3 = (float)(Math.cos(1.5707963267948966D - paramFloat) * f1);
    f1 = (float)(Math.cos(1.5707963267948966D + paramFloat) * f6);
    paramFloat = (float)(Math.sin(1.5707963267948966D + paramFloat) * f6);
    paramPointF.set(paramPointF.x + f5 + f1, paramPointF.y + f3 + paramFloat);
  }
  
  private void resetRotationStartScale()
  {
    this.rotationStartScale = 0.0F;
  }
  
  private void setLockedAspectRatio(float paramFloat)
  {
    this.areaView.setLockedAspectRatio(paramFloat);
    RectF localRectF = new RectF();
    this.areaView.calculateRect(localRectF, paramFloat);
    fillAreaView(localRectF, true);
    if (this.listener != null)
    {
      this.listener.onChange(false);
      this.listener.onAspectLock(true);
    }
  }
  
  public RectF calculateBoundingBox(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    RectF localRectF = new RectF(0.0F, 0.0F, paramFloat1, paramFloat2);
    Matrix localMatrix = new Matrix();
    localMatrix.postRotate(paramFloat3, paramFloat1 / 2.0F, paramFloat2 / 2.0F);
    localMatrix.mapRect(localRectF);
    return localRectF;
  }
  
  public float getCropHeight()
  {
    return this.areaView.getCropHeight();
  }
  
  public float getCropLeft()
  {
    return this.areaView.getCropLeft();
  }
  
  public float getCropTop()
  {
    return this.areaView.getCropTop();
  }
  
  public float getCropWidth()
  {
    return this.areaView.getCropWidth();
  }
  
  public Bitmap getResult()
  {
    Object localObject;
    if ((!this.state.hasChanges()) && (this.state.getBaseRotation() < 1.0E-5F) && (this.freeform)) {
      localObject = this.bitmap;
    }
    for (;;)
    {
      return (Bitmap)localObject;
      localObject = new RectF();
      this.areaView.getCropRect((RectF)localObject);
      int i = (int)Math.ceil(scaleWidthToMaxSize((RectF)localObject, new RectF(0.0F, 0.0F, 1280.0F, 1280.0F)));
      int j = (int)Math.ceil(i / this.areaView.getAspectRatio());
      localObject = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
      Matrix localMatrix = new Matrix();
      localMatrix.postTranslate(-this.state.getWidth() / 2.0F, -this.state.getHeight() / 2.0F);
      localMatrix.postRotate(this.state.getOrientation());
      this.state.getConcatMatrix(localMatrix);
      float f = i / this.areaView.getCropWidth();
      localMatrix.postScale(f, f);
      localMatrix.postTranslate(i / 2, j / 2);
      new Canvas((Bitmap)localObject).drawBitmap(this.bitmap, localMatrix, new Paint(2));
    }
  }
  
  public void hide()
  {
    this.backView.setVisibility(4);
    this.imageView.setVisibility(4);
    this.areaView.setDimVisibility(false);
    this.areaView.setFrameVisibility(false);
    this.areaView.invalidate();
  }
  
  public boolean isReady()
  {
    if ((!this.detector.isScaling()) && (!this.detector.isDragging()) && (!this.areaView.isDragging())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void onAreaChange()
  {
    this.areaView.setGridType(CropAreaView.GridType.MAJOR, false);
    float f1 = this.previousAreaRect.centerX();
    float f2 = this.areaView.getCropCenterX();
    float f3 = this.previousAreaRect.centerY();
    float f4 = this.areaView.getCropCenterY();
    this.state.translate(f1 - f2, f3 - f4);
    updateMatrix();
    this.areaView.getCropRect(this.previousAreaRect);
    fitContentInBounds(true, false, false);
  }
  
  public void onAreaChangeBegan()
  {
    this.areaView.getCropRect(this.previousAreaRect);
    resetRotationStartScale();
    if (this.listener != null) {
      this.listener.onChange(false);
    }
  }
  
  public void onAreaChangeEnded()
  {
    this.areaView.setGridType(CropAreaView.GridType.NONE, true);
    fillAreaView(this.areaView.getTargetRectToFill(), false);
  }
  
  public void onDrag(float paramFloat1, float paramFloat2)
  {
    if (this.animating) {}
    for (;;)
    {
      return;
      this.state.translate(paramFloat1, paramFloat2);
      updateMatrix();
    }
  }
  
  public void onFling(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {}
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return true;
  }
  
  public void onRotationBegan()
  {
    this.areaView.setGridType(CropAreaView.GridType.MINOR, false);
    if (this.rotationStartScale < 1.0E-5F) {
      this.rotationStartScale = this.state.getScale();
    }
  }
  
  public void onRotationEnded()
  {
    this.areaView.setGridType(CropAreaView.GridType.NONE, true);
  }
  
  public void onScale(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (this.animating) {
      return;
    }
    float f1 = paramFloat1;
    if (this.state.getScale() * paramFloat1 > 30.0F) {
      f1 = 30.0F / this.state.getScale();
    }
    if (Build.VERSION.SDK_INT >= 21) {}
    for (int i = AndroidUtilities.statusBarHeight;; i = 0)
    {
      float f2 = i;
      paramFloat1 = (paramFloat2 - this.imageView.getWidth() / 2) / this.areaView.getCropWidth();
      paramFloat2 = this.state.getOrientedWidth();
      paramFloat3 = (paramFloat3 - (this.imageView.getHeight() - this.bottomPadding - f2) / 2.0F) / this.areaView.getCropHeight();
      f2 = this.state.getOrientedHeight();
      this.state.scale(f1, paramFloat1 * paramFloat2, paramFloat3 * f2);
      updateMatrix();
      break;
    }
  }
  
  public void onScrollChangeBegan()
  {
    if (this.animating) {}
    for (;;)
    {
      return;
      this.areaView.setGridType(CropAreaView.GridType.MAJOR, true);
      resetRotationStartScale();
      if (this.listener != null) {
        this.listener.onChange(false);
      }
    }
  }
  
  public void onScrollChangeEnded()
  {
    this.areaView.setGridType(CropAreaView.GridType.NONE, true);
    fitContentInBounds(true, false, true);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1;
    if (this.animating) {
      bool1 = true;
    }
    boolean bool2;
    for (;;)
    {
      return bool1;
      bool2 = false;
      if (this.areaView.onTouchEvent(paramMotionEvent))
      {
        bool1 = true;
      }
      else
      {
        switch (paramMotionEvent.getAction())
        {
        }
        for (;;)
        {
          try
          {
            bool1 = this.detector.onTouchEvent(paramMotionEvent);
          }
          catch (Exception paramMotionEvent)
          {
            bool1 = bool2;
          }
          onScrollChangeBegan();
          continue;
          onScrollChangeEnded();
        }
      }
    }
  }
  
  public void reset()
  {
    this.areaView.resetAnimator();
    CropAreaView localCropAreaView = this.areaView;
    Bitmap localBitmap = this.bitmap;
    boolean bool;
    if (this.state.getBaseRotation() % 180.0F != 0.0F)
    {
      bool = true;
      localCropAreaView.setBitmap(localBitmap, bool, this.freeform);
      localCropAreaView = this.areaView;
      if (!this.freeform) {
        break label134;
      }
    }
    label134:
    for (float f = 0.0F;; f = 1.0F)
    {
      localCropAreaView.setLockedAspectRatio(f);
      this.state.reset(this.areaView, 0.0F, this.freeform);
      this.areaView.getCropRect(this.initialAreaRect);
      updateMatrix();
      resetRotationStartScale();
      if (this.listener != null)
      {
        this.listener.onChange(true);
        this.listener.onAspectLock(false);
      }
      return;
      bool = false;
      break;
    }
  }
  
  public void rotate90Degrees()
  {
    boolean bool1 = true;
    this.areaView.resetAnimator();
    resetRotationStartScale();
    float f = (this.state.getOrientation() - this.state.getBaseRotation() - 90.0F) % 360.0F;
    boolean bool2 = this.freeform;
    Object localObject;
    if ((this.freeform) && (this.areaView.getLockAspectRatio() > 0.0F))
    {
      this.areaView.setLockedAspectRatio(1.0F / this.areaView.getLockAspectRatio());
      this.areaView.setActualRect(this.areaView.getLockAspectRatio());
      bool3 = false;
      this.state.reset(this.areaView, f, bool3);
      updateMatrix();
      if (this.listener != null)
      {
        localObject = this.listener;
        if ((f != 0.0F) || (this.areaView.getLockAspectRatio() != 0.0F)) {
          break label214;
        }
      }
    }
    label214:
    for (boolean bool3 = bool1;; bool3 = false)
    {
      ((CropViewListener)localObject).onChange(bool3);
      return;
      localObject = this.areaView;
      Bitmap localBitmap = this.bitmap;
      if ((this.state.getBaseRotation() + f) % 180.0F != 0.0F) {}
      for (bool3 = true;; bool3 = false)
      {
        ((CropAreaView)localObject).setBitmap(localBitmap, bool3, this.freeform);
        bool3 = bool2;
        break;
      }
    }
  }
  
  public float scaleWidthToMaxSize(RectF paramRectF1, RectF paramRectF2)
  {
    float f1 = paramRectF2.width();
    float f2 = f1;
    if ((float)Math.floor(paramRectF1.height() * f1 / paramRectF1.width()) > paramRectF2.height())
    {
      f2 = paramRectF2.height();
      f2 = (float)Math.floor(paramRectF1.width() * f2 / paramRectF1.height());
    }
    return f2;
  }
  
  public void setBitmap(Bitmap paramBitmap, int paramInt, boolean paramBoolean)
  {
    this.bitmap = paramBitmap;
    this.freeform = paramBoolean;
    this.state = new CropState(this.bitmap, paramInt, null);
    this.backView.setVisibility(4);
    this.imageView.setVisibility(4);
    if (paramBoolean) {
      this.areaView.setDimVisibility(false);
    }
    this.imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        CropView.this.reset();
        CropView.this.imageView.getViewTreeObserver().removeOnPreDrawListener(this);
        return false;
      }
    });
    this.imageView.setImageBitmap(this.bitmap);
  }
  
  public void setBottomPadding(float paramFloat)
  {
    this.bottomPadding = paramFloat;
    this.areaView.setBottomPadding(paramFloat);
  }
  
  public void setListener(CropViewListener paramCropViewListener)
  {
    this.listener = paramCropViewListener;
  }
  
  public void setRotation(float paramFloat)
  {
    float f = this.state.getRotation();
    this.state.rotate(paramFloat - f, 0.0F, 0.0F);
    fitContentInBounds(true, true, false);
  }
  
  public void show()
  {
    this.backView.setVisibility(0);
    this.imageView.setVisibility(0);
    this.areaView.setDimVisibility(true);
    this.areaView.setFrameVisibility(true);
    this.areaView.invalidate();
  }
  
  public void showAspectRatioDialog()
  {
    if (this.areaView.getLockAspectRatio() > 0.0F)
    {
      this.areaView.setLockedAspectRatio(0.0F);
      if (this.listener != null) {
        this.listener.onAspectLock(false);
      }
    }
    for (;;)
    {
      return;
      if (!this.hasAspectRatioDialog)
      {
        this.hasAspectRatioDialog = true;
        Object localObject = new String[8];
        final Integer[][] arrayOfInteger = new Integer[6][];
        arrayOfInteger[0] = { Integer.valueOf(3), Integer.valueOf(2) };
        arrayOfInteger[1] = { Integer.valueOf(5), Integer.valueOf(3) };
        arrayOfInteger[2] = { Integer.valueOf(4), Integer.valueOf(3) };
        arrayOfInteger[3] = { Integer.valueOf(5), Integer.valueOf(4) };
        arrayOfInteger[4] = { Integer.valueOf(7), Integer.valueOf(5) };
        arrayOfInteger[5] = { Integer.valueOf(16), Integer.valueOf(9) };
        localObject[0] = LocaleController.getString("CropOriginal", NUM);
        localObject[1] = LocaleController.getString("CropSquare", NUM);
        int i = 2;
        int j = arrayOfInteger.length;
        int k = 0;
        if (k < j)
        {
          Integer[] arrayOfInteger1 = arrayOfInteger[k];
          if (this.areaView.getAspectRatio() > 1.0F) {
            localObject[i] = String.format("%d:%d", new Object[] { arrayOfInteger1[0], arrayOfInteger1[1] });
          }
          for (;;)
          {
            i++;
            k++;
            break;
            localObject[i] = String.format("%d:%d", new Object[] { arrayOfInteger1[1], arrayOfInteger1[0] });
          }
        }
        localObject = new AlertDialog.Builder(getContext()).setItems((CharSequence[])localObject, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            CropView.access$2302(CropView.this, false);
            switch (paramAnonymousInt)
            {
            default: 
              paramAnonymousDialogInterface = arrayOfInteger[(paramAnonymousInt - 2)];
              if (CropView.this.areaView.getAspectRatio() > 1.0F) {
                CropView.this.setLockedAspectRatio(paramAnonymousDialogInterface[0].intValue() / paramAnonymousDialogInterface[1].intValue());
              }
              break;
            }
            for (;;)
            {
              return;
              float f1;
              if (CropView.CropState.access$200(CropView.this.state) % 180.0F != 0.0F)
              {
                f1 = CropView.CropState.access$500(CropView.this.state);
                label108:
                if (CropView.CropState.access$200(CropView.this.state) % 180.0F == 0.0F) {
                  break label166;
                }
              }
              label166:
              for (float f2 = CropView.CropState.access$400(CropView.this.state);; f2 = CropView.CropState.access$500(CropView.this.state))
              {
                CropView.this.setLockedAspectRatio(f1 / f2);
                break;
                f1 = CropView.CropState.access$400(CropView.this.state);
                break label108;
              }
              CropView.this.setLockedAspectRatio(1.0F);
              continue;
              CropView.this.setLockedAspectRatio(paramAnonymousDialogInterface[1].intValue() / paramAnonymousDialogInterface[0].intValue());
            }
          }
        }).create();
        ((AlertDialog)localObject).setCanceledOnTouchOutside(true);
        ((AlertDialog)localObject).setOnCancelListener(new DialogInterface.OnCancelListener()
        {
          public void onCancel(DialogInterface paramAnonymousDialogInterface)
          {
            CropView.access$2302(CropView.this, false);
          }
        });
        ((AlertDialog)localObject).show();
      }
    }
  }
  
  public void updateLayout()
  {
    float f = this.areaView.getCropWidth();
    this.areaView.calculateRect(this.initialAreaRect, this.state.getWidth() / this.state.getHeight());
    this.areaView.setActualRect(this.areaView.getAspectRatio());
    this.areaView.getCropRect(this.previousAreaRect);
    f = this.areaView.getCropWidth() / f;
    this.state.scale(f, 0.0F, 0.0F);
    updateMatrix();
  }
  
  public void updateMatrix()
  {
    this.presentationMatrix.reset();
    this.presentationMatrix.postTranslate(-this.state.getWidth() / 2.0F, -this.state.getHeight() / 2.0F);
    this.presentationMatrix.postRotate(this.state.getOrientation());
    this.state.getConcatMatrix(this.presentationMatrix);
    this.presentationMatrix.postTranslate(this.areaView.getCropCenterX(), this.areaView.getCropCenterY());
    this.imageView.setImageMatrix(this.presentationMatrix);
  }
  
  public void willShow()
  {
    this.areaView.setFrameVisibility(true);
    this.areaView.setDimVisibility(true);
    this.areaView.invalidate();
  }
  
  private class CropRectangle
  {
    float[] coords = new float[8];
    
    CropRectangle() {}
    
    void applyMatrix(Matrix paramMatrix)
    {
      paramMatrix.mapPoints(this.coords);
    }
    
    void getRect(RectF paramRectF)
    {
      paramRectF.set(this.coords[0], this.coords[1], this.coords[2], this.coords[7]);
    }
    
    void setRect(RectF paramRectF)
    {
      this.coords[0] = paramRectF.left;
      this.coords[1] = paramRectF.top;
      this.coords[2] = paramRectF.right;
      this.coords[3] = paramRectF.top;
      this.coords[4] = paramRectF.right;
      this.coords[5] = paramRectF.bottom;
      this.coords[6] = paramRectF.left;
      this.coords[7] = paramRectF.bottom;
    }
  }
  
  private class CropState
  {
    private float baseRotation;
    private float height;
    private Matrix matrix;
    private float minimumScale;
    private float orientation;
    private float rotation;
    private float scale;
    private float width;
    private float x;
    private float y;
    
    private CropState(Bitmap paramBitmap, int paramInt)
    {
      this.width = paramBitmap.getWidth();
      this.height = paramBitmap.getHeight();
      this.x = 0.0F;
      this.y = 0.0F;
      this.scale = 1.0F;
      this.baseRotation = paramInt;
      this.rotation = 0.0F;
      this.matrix = new Matrix();
    }
    
    private float getBaseRotation()
    {
      return this.baseRotation;
    }
    
    private void getConcatMatrix(Matrix paramMatrix)
    {
      paramMatrix.postConcat(this.matrix);
    }
    
    private float getHeight()
    {
      return this.height;
    }
    
    private Matrix getMatrix()
    {
      Matrix localMatrix = new Matrix();
      localMatrix.set(this.matrix);
      return localMatrix;
    }
    
    private float getMinimumScale()
    {
      return this.minimumScale;
    }
    
    private float getOrientation()
    {
      return this.orientation + this.baseRotation;
    }
    
    private float getOrientedHeight()
    {
      if ((this.orientation + this.baseRotation) % 180.0F != 0.0F) {}
      for (float f = this.width;; f = this.height) {
        return f;
      }
    }
    
    private float getOrientedWidth()
    {
      if ((this.orientation + this.baseRotation) % 180.0F != 0.0F) {}
      for (float f = this.height;; f = this.width) {
        return f;
      }
    }
    
    private float getRotation()
    {
      return this.rotation;
    }
    
    private float getScale()
    {
      return this.scale;
    }
    
    private float getWidth()
    {
      return this.width;
    }
    
    private float getX()
    {
      return this.x;
    }
    
    private float getY()
    {
      return this.y;
    }
    
    private boolean hasChanges()
    {
      if ((Math.abs(this.x) > 1.0E-5F) || (Math.abs(this.y) > 1.0E-5F) || (Math.abs(this.scale - this.minimumScale) > 1.0E-5F) || (Math.abs(this.rotation) > 1.0E-5F) || (Math.abs(this.orientation) > 1.0E-5F)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    private void reset(CropAreaView paramCropAreaView, float paramFloat, boolean paramBoolean)
    {
      this.matrix.reset();
      this.x = 0.0F;
      this.y = 0.0F;
      this.rotation = 0.0F;
      this.orientation = paramFloat;
      float f;
      if ((this.orientation + this.baseRotation) % 180.0F != 0.0F)
      {
        paramFloat = this.height;
        if ((this.orientation + this.baseRotation) % 180.0F == 0.0F) {
          break label119;
        }
        f = this.width;
        label72:
        if (!paramBoolean) {
          break label128;
        }
      }
      label119:
      label128:
      for (this.minimumScale = (paramCropAreaView.getCropWidth() / paramFloat);; this.minimumScale = Math.max(paramCropAreaView.getCropWidth() / paramFloat, paramCropAreaView.getCropHeight() / f))
      {
        this.scale = this.minimumScale;
        this.matrix.postScale(this.scale, this.scale);
        return;
        paramFloat = this.width;
        break;
        f = this.height;
        break label72;
      }
    }
    
    private void rotate(float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.rotation += paramFloat1;
      this.matrix.postRotate(paramFloat1, paramFloat2, paramFloat3);
    }
    
    private void scale(float paramFloat1, float paramFloat2, float paramFloat3)
    {
      this.scale *= paramFloat1;
      this.matrix.postScale(paramFloat1, paramFloat1, paramFloat2, paramFloat3);
    }
    
    private void translate(float paramFloat1, float paramFloat2)
    {
      this.x += paramFloat1;
      this.y += paramFloat2;
      this.matrix.postTranslate(paramFloat1, paramFloat2);
    }
  }
  
  public static abstract interface CropViewListener
  {
    public abstract void onAspectLock(boolean paramBoolean);
    
    public abstract void onChange(boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Crop/CropView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */