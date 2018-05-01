package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.ui.Components.AnimatedFileDrawable;

public class ImageReceiver
  implements NotificationCenter.NotificationCenterDelegate
{
  private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, PorterDuff.Mode.MULTIPLY);
  private static PorterDuffColorFilter selectedGroupColorFilter = new PorterDuffColorFilter(-4473925, PorterDuff.Mode.MULTIPLY);
  private boolean allowDecodeSingleFrame;
  private boolean allowStartAnimation = true;
  private RectF bitmapRect = new RectF();
  private BitmapShader bitmapShader;
  private BitmapShader bitmapShaderThumb;
  private boolean canceledLoading;
  private boolean centerRotation;
  private ColorFilter colorFilter;
  private byte crossfadeAlpha = (byte)1;
  private Drawable crossfadeImage;
  private String crossfadeKey;
  private BitmapShader crossfadeShader;
  private boolean crossfadeWithOldImage;
  private boolean crossfadeWithThumb;
  private int currentAccount;
  private float currentAlpha;
  private int currentCacheType;
  private String currentExt;
  private String currentFilter;
  private String currentHttpUrl;
  private Drawable currentImage;
  private TLObject currentImageLocation;
  private String currentKey;
  private int currentSize;
  private Drawable currentThumb;
  private String currentThumbFilter;
  private String currentThumbKey;
  private TLRPC.FileLocation currentThumbLocation;
  private ImageReceiverDelegate delegate;
  private Rect drawRegion = new Rect();
  private boolean forceCrossfade;
  private boolean forceLoding;
  private boolean forcePreview;
  private int imageH;
  private int imageW;
  private int imageX;
  private int imageY;
  private boolean invalidateAll;
  private boolean isAspectFit;
  private int isPressed;
  private boolean isVisible = true;
  private long lastUpdateAlphaTime;
  private boolean manualAlphaAnimator;
  private boolean needsQualityThumb;
  private int orientation;
  private float overrideAlpha = 1.0F;
  private int param;
  private MessageObject parentMessageObject;
  private View parentView;
  private Paint roundPaint;
  private int roundRadius;
  private RectF roundRect = new RectF();
  private SetImageBackup setImageBackup;
  private Matrix shaderMatrix = new Matrix();
  private boolean shouldGenerateQualityThumb;
  private Drawable staticThumb;
  private int tag;
  private int thumbTag;
  
  public ImageReceiver()
  {
    this(null);
  }
  
  public ImageReceiver(View paramView)
  {
    this.parentView = paramView;
    this.roundPaint = new Paint(1);
    this.currentAccount = UserConfig.selectedAccount;
  }
  
  private void checkAlphaAnimation(boolean paramBoolean)
  {
    if (this.manualAlphaAnimator) {}
    for (;;)
    {
      return;
      if (this.currentAlpha != 1.0F)
      {
        if (!paramBoolean)
        {
          long l1 = System.currentTimeMillis() - this.lastUpdateAlphaTime;
          long l2 = l1;
          if (l1 > 18L) {
            l2 = 18L;
          }
          this.currentAlpha += (float)l2 / 150.0F;
          if (this.currentAlpha > 1.0F)
          {
            this.currentAlpha = 1.0F;
            if (this.crossfadeImage != null)
            {
              recycleBitmap(null, 2);
              this.crossfadeShader = null;
            }
          }
        }
        this.lastUpdateAlphaTime = System.currentTimeMillis();
        if (this.parentView != null) {
          if (this.invalidateAll) {
            this.parentView.invalidate();
          } else {
            this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
          }
        }
      }
    }
  }
  
  private void drawDrawable(Canvas paramCanvas, Drawable paramDrawable, int paramInt, BitmapShader paramBitmapShader)
  {
    BitmapDrawable localBitmapDrawable;
    Paint localPaint;
    int i;
    label40:
    label66:
    label90:
    int j;
    label139:
    float f1;
    float f2;
    float f3;
    label317:
    int k;
    int m;
    if ((paramDrawable instanceof BitmapDrawable))
    {
      localBitmapDrawable = (BitmapDrawable)paramDrawable;
      if (paramBitmapShader != null)
      {
        localPaint = this.roundPaint;
        if ((localPaint == null) || (localPaint.getColorFilter() == null)) {
          break label477;
        }
        i = 1;
        if ((i == 0) || (this.isPressed != 0)) {
          break label500;
        }
        if (paramBitmapShader == null) {
          break label483;
        }
        this.roundPaint.setColorFilter(null);
        if (this.colorFilter != null)
        {
          if (paramBitmapShader == null) {
            break label580;
          }
          this.roundPaint.setColorFilter(this.colorFilter);
        }
        if (!(localBitmapDrawable instanceof AnimatedFileDrawable)) {
          break label609;
        }
        if ((this.orientation % 360 != 90) && (this.orientation % 360 != 270)) {
          break label592;
        }
        j = localBitmapDrawable.getIntrinsicHeight();
        i = localBitmapDrawable.getIntrinsicWidth();
        f1 = j / this.imageW;
        f2 = i / this.imageH;
        if (paramBitmapShader == null) {
          break label816;
        }
        this.roundPaint.setShader(paramBitmapShader);
        f3 = Math.min(f1, f2);
        this.roundRect.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        this.shaderMatrix.reset();
        if (Math.abs(f1 - f2) <= 1.0E-5F) {
          break label743;
        }
        if (j / f2 <= this.imageW) {
          break label682;
        }
        this.drawRegion.set(this.imageX - ((int)(j / f2) - this.imageW) / 2, this.imageY, this.imageX + ((int)(j / f2) + this.imageW) / 2, this.imageY + this.imageH);
        if (this.isVisible)
        {
          if (Math.abs(f1 - f2) <= 1.0E-5F) {
            break label779;
          }
          k = (int)Math.floor(this.imageW * f3);
          m = (int)Math.floor(this.imageH * f3);
          this.bitmapRect.set((j - k) / 2, (i - m) / 2, (j + k) / 2, (i + m) / 2);
          this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, Matrix.ScaleToFit.START);
          label427:
          paramBitmapShader.setLocalMatrix(this.shaderMatrix);
          this.roundPaint.setAlpha(paramInt);
          paramCanvas.drawRoundRect(this.roundRect, this.roundRadius, this.roundRadius, this.roundPaint);
        }
      }
    }
    for (;;)
    {
      return;
      localPaint = localBitmapDrawable.getPaint();
      break;
      label477:
      i = 0;
      break label40;
      label483:
      if (this.staticThumb == paramDrawable) {
        break label66;
      }
      localBitmapDrawable.setColorFilter(null);
      break label66;
      label500:
      if ((i != 0) || (this.isPressed == 0)) {
        break label66;
      }
      if (this.isPressed == 1)
      {
        if (paramBitmapShader != null)
        {
          this.roundPaint.setColorFilter(selectedColorFilter);
          break label66;
        }
        localBitmapDrawable.setColorFilter(selectedColorFilter);
        break label66;
      }
      if (paramBitmapShader != null)
      {
        this.roundPaint.setColorFilter(selectedGroupColorFilter);
        break label66;
      }
      localBitmapDrawable.setColorFilter(selectedGroupColorFilter);
      break label66;
      label580:
      localBitmapDrawable.setColorFilter(this.colorFilter);
      break label90;
      label592:
      j = localBitmapDrawable.getIntrinsicWidth();
      i = localBitmapDrawable.getIntrinsicHeight();
      break label139;
      label609:
      if ((this.orientation % 360 == 90) || (this.orientation % 360 == 270))
      {
        j = localBitmapDrawable.getBitmap().getHeight();
        i = localBitmapDrawable.getBitmap().getWidth();
        break label139;
      }
      j = localBitmapDrawable.getBitmap().getWidth();
      i = localBitmapDrawable.getBitmap().getHeight();
      break label139;
      label682:
      this.drawRegion.set(this.imageX, this.imageY - ((int)(i / f1) - this.imageH) / 2, this.imageX + this.imageW, this.imageY + ((int)(i / f1) + this.imageH) / 2);
      break label317;
      label743:
      this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break label317;
      label779:
      this.bitmapRect.set(0.0F, 0.0F, j, i);
      this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, Matrix.ScaleToFit.FILL);
      break label427;
      label816:
      if (this.isAspectFit)
      {
        f3 = Math.max(f1, f2);
        paramCanvas.save();
        j = (int)(j / f3);
        i = (int)(i / f3);
        this.drawRegion.set(this.imageX + (this.imageW - j) / 2, this.imageY + (this.imageH - i) / 2, this.imageX + (this.imageW + j) / 2, this.imageY + (this.imageH + i) / 2);
        localBitmapDrawable.setBounds(this.drawRegion);
        try
        {
          localBitmapDrawable.setAlpha(paramInt);
          localBitmapDrawable.draw(paramCanvas);
          paramCanvas.restore();
        }
        catch (Exception paramDrawable)
        {
          if (localBitmapDrawable != this.currentImage) {
            break label1025;
          }
        }
        if (this.currentKey != null)
        {
          ImageLoader.getInstance().removeImage(this.currentKey);
          this.currentKey = null;
        }
        for (;;)
        {
          setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheType);
          FileLog.e(paramDrawable);
          break;
          label1025:
          if ((localBitmapDrawable == this.currentThumb) && (this.currentThumbKey != null))
          {
            ImageLoader.getInstance().removeImage(this.currentThumbKey);
            this.currentThumbKey = null;
          }
        }
      }
      else
      {
        if (Math.abs(f1 - f2) > 1.0E-5F)
        {
          paramCanvas.save();
          paramCanvas.clipRect(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
          if (this.orientation % 360 != 0)
          {
            if (this.centerRotation) {
              paramCanvas.rotate(this.orientation, this.imageW / 2, this.imageH / 2);
            }
          }
          else
          {
            label1151:
            if (j / f2 <= this.imageW) {
              break label1423;
            }
            i = (int)(j / f2);
            this.drawRegion.set(this.imageX - (i - this.imageW) / 2, this.imageY, this.imageX + (this.imageW + i) / 2, this.imageY + this.imageH);
            label1223:
            if ((localBitmapDrawable instanceof AnimatedFileDrawable)) {
              ((AnimatedFileDrawable)localBitmapDrawable).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
            }
            if ((this.orientation % 360 != 90) && (this.orientation % 360 != 270)) {
              break label1483;
            }
            i = (this.drawRegion.right - this.drawRegion.left) / 2;
            k = (this.drawRegion.bottom - this.drawRegion.top) / 2;
            j = (this.drawRegion.right + this.drawRegion.left) / 2;
            m = (this.drawRegion.top + this.drawRegion.bottom) / 2;
            localBitmapDrawable.setBounds(j - k, m - i, j + k, m + i);
          }
          for (;;)
          {
            if (this.isVisible) {}
            try
            {
              localBitmapDrawable.setAlpha(paramInt);
              localBitmapDrawable.draw(paramCanvas);
              paramCanvas.restore();
            }
            catch (Exception paramDrawable)
            {
              if (localBitmapDrawable != this.currentImage) {
                break label1574;
              }
            }
            paramCanvas.rotate(this.orientation, 0.0F, 0.0F);
            break label1151;
            label1423:
            i = (int)(i / f1);
            this.drawRegion.set(this.imageX, this.imageY - (i - this.imageH) / 2, this.imageX + this.imageW, this.imageY + (this.imageH + i) / 2);
            break label1223;
            label1483:
            localBitmapDrawable.setBounds(this.drawRegion);
          }
          if (this.currentKey != null)
          {
            ImageLoader.getInstance().removeImage(this.currentKey);
            this.currentKey = null;
          }
          for (;;)
          {
            setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheType);
            FileLog.e(paramDrawable);
            break;
            label1574:
            if ((localBitmapDrawable == this.currentThumb) && (this.currentThumbKey != null))
            {
              ImageLoader.getInstance().removeImage(this.currentThumbKey);
              this.currentThumbKey = null;
            }
          }
        }
        paramCanvas.save();
        if (this.orientation % 360 != 0)
        {
          if (this.centerRotation) {
            paramCanvas.rotate(this.orientation, this.imageW / 2, this.imageH / 2);
          }
        }
        else
        {
          label1654:
          this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
          if ((localBitmapDrawable instanceof AnimatedFileDrawable)) {
            ((AnimatedFileDrawable)localBitmapDrawable).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
          }
          if ((this.orientation % 360 != 90) && (this.orientation % 360 != 270)) {
            break label1887;
          }
          m = (this.drawRegion.right - this.drawRegion.left) / 2;
          i = (this.drawRegion.bottom - this.drawRegion.top) / 2;
          k = (this.drawRegion.right + this.drawRegion.left) / 2;
          j = (this.drawRegion.top + this.drawRegion.bottom) / 2;
          localBitmapDrawable.setBounds(k - i, j - m, k + i, j + m);
        }
        for (;;)
        {
          if (this.isVisible) {}
          try
          {
            localBitmapDrawable.setAlpha(paramInt);
            localBitmapDrawable.draw(paramCanvas);
            paramCanvas.restore();
          }
          catch (Exception paramDrawable)
          {
            if (localBitmapDrawable != this.currentImage) {
              break label1978;
            }
          }
          paramCanvas.rotate(this.orientation, 0.0F, 0.0F);
          break label1654;
          label1887:
          localBitmapDrawable.setBounds(this.drawRegion);
        }
        if (this.currentKey != null)
        {
          ImageLoader.getInstance().removeImage(this.currentKey);
          this.currentKey = null;
        }
        for (;;)
        {
          setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheType);
          FileLog.e(paramDrawable);
          break;
          label1978:
          if ((localBitmapDrawable == this.currentThumb) && (this.currentThumbKey != null))
          {
            ImageLoader.getInstance().removeImage(this.currentThumbKey);
            this.currentThumbKey = null;
          }
        }
        this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        paramDrawable.setBounds(this.drawRegion);
        if (this.isVisible) {
          try
          {
            paramDrawable.setAlpha(paramInt);
            paramDrawable.draw(paramCanvas);
          }
          catch (Exception paramCanvas)
          {
            FileLog.e(paramCanvas);
          }
        }
      }
    }
  }
  
  private void recycleBitmap(String paramString, int paramInt)
  {
    String str;
    Drawable localDrawable;
    if (paramInt == 2)
    {
      str = this.crossfadeKey;
      localDrawable = this.crossfadeImage;
      if ((str != null) && ((paramString == null) || (!paramString.equals(str))) && (localDrawable != null))
      {
        if (!(localDrawable instanceof AnimatedFileDrawable)) {
          break label102;
        }
        ((AnimatedFileDrawable)localDrawable).recycle();
      }
      label53:
      if (paramInt != 2) {
        break label150;
      }
      this.crossfadeKey = null;
      this.crossfadeImage = null;
    }
    for (;;)
    {
      return;
      if (paramInt == 1)
      {
        str = this.currentThumbKey;
        localDrawable = this.currentThumb;
        break;
      }
      str = this.currentKey;
      localDrawable = this.currentImage;
      break;
      label102:
      if (!(localDrawable instanceof BitmapDrawable)) {
        break label53;
      }
      paramString = ((BitmapDrawable)localDrawable).getBitmap();
      boolean bool = ImageLoader.getInstance().decrementUseCount(str);
      if ((ImageLoader.getInstance().isInCache(str)) || (!bool)) {
        break label53;
      }
      paramString.recycle();
      break label53;
      label150:
      if (paramInt == 1)
      {
        this.currentThumb = null;
        this.currentThumbKey = null;
      }
      else
      {
        this.currentImage = null;
        this.currentKey = null;
      }
    }
  }
  
  public void cancelLoadImage()
  {
    this.forceLoding = false;
    ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
    this.canceledLoading = true;
  }
  
  public void clearImage()
  {
    for (int i = 0; i < 3; i++) {
      recycleBitmap(null, i);
    }
    if (this.needsQualityThumb) {
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
    }
    ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    String str;
    if (paramInt1 == NotificationCenter.messageThumbGenerated)
    {
      str = (String)paramVarArgs[1];
      if ((this.currentThumbKey != null) && (this.currentThumbKey.equals(str)))
      {
        if (this.currentThumb == null) {
          ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
        }
        this.currentThumb = ((BitmapDrawable)paramVarArgs[0]);
        if ((this.roundRadius == 0) || (this.currentImage != null) || (!(this.currentThumb instanceof BitmapDrawable)) || ((this.currentThumb instanceof AnimatedFileDrawable))) {
          break label159;
        }
        this.bitmapShaderThumb = new BitmapShader(((BitmapDrawable)this.currentThumb).getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if ((this.staticThumb instanceof BitmapDrawable)) {
          this.staticThumb = null;
        }
        if (this.parentView != null)
        {
          if (!this.invalidateAll) {
            break label167;
          }
          this.parentView.invalidate();
        }
      }
    }
    for (;;)
    {
      return;
      label159:
      this.bitmapShaderThumb = null;
      break;
      label167:
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      continue;
      if (paramInt1 == NotificationCenter.didReplacedPhotoInMemCache)
      {
        str = (String)paramVarArgs[0];
        if ((this.currentKey != null) && (this.currentKey.equals(str)))
        {
          this.currentKey = ((String)paramVarArgs[1]);
          this.currentImageLocation = ((TLRPC.FileLocation)paramVarArgs[2]);
        }
        if ((this.currentThumbKey != null) && (this.currentThumbKey.equals(str)))
        {
          this.currentThumbKey = ((String)paramVarArgs[1]);
          this.currentThumbLocation = ((TLRPC.FileLocation)paramVarArgs[2]);
        }
        if (this.setImageBackup != null)
        {
          if ((this.currentKey != null) && (this.currentKey.equals(str)))
          {
            this.currentKey = ((String)paramVarArgs[1]);
            this.currentImageLocation = ((TLRPC.FileLocation)paramVarArgs[2]);
          }
          if ((this.currentThumbKey != null) && (this.currentThumbKey.equals(str)))
          {
            this.currentThumbKey = ((String)paramVarArgs[1]);
            this.currentThumbLocation = ((TLRPC.FileLocation)paramVarArgs[2]);
          }
        }
      }
    }
  }
  
  public boolean draw(Canvas paramCanvas)
  {
    Drawable localDrawable = null;
    boolean bool;
    int i;
    Object localObject1;
    Object localObject2;
    label60:
    label114:
    label121:
    label128:
    label200:
    Object localObject3;
    Object localObject4;
    Object localObject5;
    label249:
    int j;
    try
    {
      if (((this.currentImage instanceof AnimatedFileDrawable)) && (!((AnimatedFileDrawable)this.currentImage).hasBitmap()))
      {
        bool = true;
        i = 0;
        localObject1 = null;
        if ((this.forcePreview) || (this.currentImage == null) || (bool)) {
          break label128;
        }
        localDrawable = this.currentImage;
        localObject2 = localObject1;
        if (localDrawable == null) {
          break label506;
        }
        if (this.crossfadeAlpha == 0) {
          break label449;
        }
        if ((!this.crossfadeWithThumb) || (!bool)) {
          break label200;
        }
        drawDrawable(paramCanvas, localDrawable, (int)(this.overrideAlpha * 255.0F), this.bitmapShaderThumb);
      }
      for (;;)
      {
        if ((bool) && (this.crossfadeWithThumb))
        {
          bool = true;
          checkAlphaAnimation(bool);
          bool = true;
          return bool;
          bool = false;
          break;
          if (this.crossfadeImage != null)
          {
            localDrawable = this.crossfadeImage;
            localObject2 = this.crossfadeShader;
            break label60;
          }
          if ((this.staticThumb instanceof BitmapDrawable))
          {
            localDrawable = this.staticThumb;
            i = 1;
            localObject2 = localObject1;
            break label60;
          }
          localObject2 = localObject1;
          if (this.currentThumb == null) {
            break label60;
          }
          localDrawable = this.currentThumb;
          i = 1;
          localObject2 = localObject1;
          break label60;
          if ((this.crossfadeWithThumb) && (this.currentAlpha != 1.0F))
          {
            localObject3 = null;
            localObject4 = null;
            if (localDrawable != this.currentImage) {
              break label373;
            }
            if (this.crossfadeImage == null) {
              break label325;
            }
            localObject1 = this.crossfadeImage;
            localObject5 = this.crossfadeShader;
            if (localObject1 != null)
            {
              j = (int)(this.overrideAlpha * 255.0F);
              if (localObject5 == null) {
                break label417;
              }
              label270:
              drawDrawable(paramCanvas, (Drawable)localObject1, j, (BitmapShader)localObject5);
            }
          }
          j = (int)(this.overrideAlpha * this.currentAlpha * 255.0F);
          if (localObject2 != null)
          {
            label302:
            drawDrawable(paramCanvas, localDrawable, j, (BitmapShader)localObject2);
            continue;
            bool = false;
          }
        }
      }
    }
    catch (Exception paramCanvas)
    {
      FileLog.e(paramCanvas);
    }
    for (;;)
    {
      break label121;
      label325:
      if (this.staticThumb != null)
      {
        localObject1 = this.staticThumb;
        localObject5 = localObject4;
        break label249;
      }
      localObject5 = localObject4;
      localObject1 = localObject3;
      if (this.currentThumb == null) {
        break label249;
      }
      localObject1 = this.currentThumb;
      localObject5 = localObject4;
      break label249;
      label373:
      localObject5 = localObject4;
      localObject1 = localObject3;
      if (localDrawable != this.currentThumb) {
        break label249;
      }
      localObject5 = localObject4;
      localObject1 = localObject3;
      if (this.staticThumb == null) {
        break label249;
      }
      localObject1 = this.staticThumb;
      localObject5 = localObject4;
      break label249;
      label417:
      localObject5 = this.bitmapShaderThumb;
      break label270;
      if (i != 0)
      {
        localObject2 = this.bitmapShaderThumb;
        break label302;
      }
      localObject2 = this.bitmapShader;
      break label302;
      label449:
      j = (int)(this.overrideAlpha * 255.0F);
      if (localObject2 != null) {}
      for (;;)
      {
        drawDrawable(paramCanvas, localDrawable, j, (BitmapShader)localObject2);
        break;
        if (i != 0) {
          localObject2 = this.bitmapShaderThumb;
        } else {
          localObject2 = this.bitmapShader;
        }
      }
      bool = false;
      break label114;
      label506:
      if (this.staticThumb != null)
      {
        drawDrawable(paramCanvas, this.staticThumb, 255, null);
        checkAlphaAnimation(bool);
        bool = true;
        break label121;
      }
      checkAlphaAnimation(bool);
    }
  }
  
  public int getAnimatedOrientation()
  {
    int i;
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      i = ((AnimatedFileDrawable)this.currentImage).getOrientation();
    }
    for (;;)
    {
      return i;
      if ((this.staticThumb instanceof AnimatedFileDrawable)) {
        i = ((AnimatedFileDrawable)this.staticThumb).getOrientation();
      } else {
        i = 0;
      }
    }
  }
  
  public AnimatedFileDrawable getAnimation()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable)) {}
    for (AnimatedFileDrawable localAnimatedFileDrawable = (AnimatedFileDrawable)this.currentImage;; localAnimatedFileDrawable = null) {
      return localAnimatedFileDrawable;
    }
  }
  
  public Bitmap getBitmap()
  {
    Bitmap localBitmap;
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      localBitmap = ((AnimatedFileDrawable)this.currentImage).getAnimatedBitmap();
    }
    for (;;)
    {
      return localBitmap;
      if ((this.staticThumb instanceof AnimatedFileDrawable)) {
        localBitmap = ((AnimatedFileDrawable)this.staticThumb).getAnimatedBitmap();
      } else if ((this.currentImage instanceof BitmapDrawable)) {
        localBitmap = ((BitmapDrawable)this.currentImage).getBitmap();
      } else if ((this.currentThumb instanceof BitmapDrawable)) {
        localBitmap = ((BitmapDrawable)this.currentThumb).getBitmap();
      } else if ((this.staticThumb instanceof BitmapDrawable)) {
        localBitmap = ((BitmapDrawable)this.staticThumb).getBitmap();
      } else {
        localBitmap = null;
      }
    }
  }
  
  public int getBitmapHeight()
  {
    int i;
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
        i = this.currentImage.getIntrinsicHeight();
      }
    }
    for (;;)
    {
      return i;
      i = this.currentImage.getIntrinsicWidth();
      continue;
      if ((this.staticThumb instanceof AnimatedFileDrawable))
      {
        if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
          i = this.staticThumb.getIntrinsicHeight();
        } else {
          i = this.staticThumb.getIntrinsicWidth();
        }
      }
      else
      {
        Bitmap localBitmap = getBitmap();
        if (localBitmap == null)
        {
          if (this.staticThumb != null) {
            i = this.staticThumb.getIntrinsicHeight();
          } else {
            i = 1;
          }
        }
        else if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
          i = localBitmap.getHeight();
        } else {
          i = localBitmap.getWidth();
        }
      }
    }
  }
  
  public BitmapHolder getBitmapSafe()
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3;
    if ((this.currentImage instanceof AnimatedFileDrawable))
    {
      localObject1 = ((AnimatedFileDrawable)this.currentImage).getAnimatedBitmap();
      localObject3 = localObject2;
      if (localObject1 == null) {
        break label155;
      }
    }
    label155:
    for (localObject1 = new BitmapHolder((Bitmap)localObject1, (String)localObject3);; localObject1 = null)
    {
      return (BitmapHolder)localObject1;
      if ((this.staticThumb instanceof AnimatedFileDrawable))
      {
        localObject1 = ((AnimatedFileDrawable)this.staticThumb).getAnimatedBitmap();
        localObject3 = localObject2;
        break;
      }
      if ((this.currentImage instanceof BitmapDrawable))
      {
        localObject1 = ((BitmapDrawable)this.currentImage).getBitmap();
        localObject3 = this.currentKey;
        break;
      }
      if ((this.currentThumb instanceof BitmapDrawable))
      {
        localObject1 = ((BitmapDrawable)this.currentThumb).getBitmap();
        localObject3 = this.currentThumbKey;
        break;
      }
      localObject3 = localObject2;
      if (!(this.staticThumb instanceof BitmapDrawable)) {
        break;
      }
      localObject1 = ((BitmapDrawable)this.staticThumb).getBitmap();
      localObject3 = localObject2;
      break;
    }
  }
  
  public int getBitmapWidth()
  {
    int i;
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
        i = this.currentImage.getIntrinsicWidth();
      }
    }
    for (;;)
    {
      return i;
      i = this.currentImage.getIntrinsicHeight();
      continue;
      if ((this.staticThumb instanceof AnimatedFileDrawable))
      {
        if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
          i = this.staticThumb.getIntrinsicWidth();
        } else {
          i = this.staticThumb.getIntrinsicHeight();
        }
      }
      else
      {
        Bitmap localBitmap = getBitmap();
        if (localBitmap == null)
        {
          if (this.staticThumb != null) {
            i = this.staticThumb.getIntrinsicWidth();
          } else {
            i = 1;
          }
        }
        else if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
          i = localBitmap.getWidth();
        } else {
          i = localBitmap.getHeight();
        }
      }
    }
  }
  
  public int getCacheType()
  {
    return this.currentCacheType;
  }
  
  public float getCenterX()
  {
    return this.imageX + this.imageW / 2.0F;
  }
  
  public float getCenterY()
  {
    return this.imageY + this.imageH / 2.0F;
  }
  
  public float getCurrentAlpha()
  {
    return this.currentAlpha;
  }
  
  public Rect getDrawRegion()
  {
    return this.drawRegion;
  }
  
  public String getExt()
  {
    return this.currentExt;
  }
  
  public String getFilter()
  {
    return this.currentFilter;
  }
  
  public String getHttpImageLocation()
  {
    return this.currentHttpUrl;
  }
  
  public int getImageHeight()
  {
    return this.imageH;
  }
  
  public TLObject getImageLocation()
  {
    return this.currentImageLocation;
  }
  
  public int getImageWidth()
  {
    return this.imageW;
  }
  
  public int getImageX()
  {
    return this.imageX;
  }
  
  public int getImageX2()
  {
    return this.imageX + this.imageW;
  }
  
  public int getImageY()
  {
    return this.imageY;
  }
  
  public int getImageY2()
  {
    return this.imageY + this.imageH;
  }
  
  public String getKey()
  {
    return this.currentKey;
  }
  
  public int getOrientation()
  {
    return this.orientation;
  }
  
  public int getParam()
  {
    return this.param;
  }
  
  public MessageObject getParentMessageObject()
  {
    return this.parentMessageObject;
  }
  
  public boolean getPressed()
  {
    if (this.isPressed != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public int getRoundRadius()
  {
    return this.roundRadius;
  }
  
  public int getSize()
  {
    return this.currentSize;
  }
  
  public Drawable getStaticThumb()
  {
    return this.staticThumb;
  }
  
  protected int getTag(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (int i = this.thumbTag;; i = this.tag) {
      return i;
    }
  }
  
  public Bitmap getThumbBitmap()
  {
    Bitmap localBitmap;
    if ((this.currentThumb instanceof BitmapDrawable)) {
      localBitmap = ((BitmapDrawable)this.currentThumb).getBitmap();
    }
    for (;;)
    {
      return localBitmap;
      if ((this.staticThumb instanceof BitmapDrawable)) {
        localBitmap = ((BitmapDrawable)this.staticThumb).getBitmap();
      } else {
        localBitmap = null;
      }
    }
  }
  
  public BitmapHolder getThumbBitmapSafe()
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3;
    if ((this.currentThumb instanceof BitmapDrawable))
    {
      localObject1 = ((BitmapDrawable)this.currentThumb).getBitmap();
      localObject3 = this.currentThumbKey;
      if (localObject1 == null) {
        break label74;
      }
    }
    label74:
    for (localObject1 = new BitmapHolder((Bitmap)localObject1, (String)localObject3);; localObject1 = null)
    {
      return (BitmapHolder)localObject1;
      localObject3 = localObject2;
      if (!(this.staticThumb instanceof BitmapDrawable)) {
        break;
      }
      localObject1 = ((BitmapDrawable)this.staticThumb).getBitmap();
      localObject3 = localObject2;
      break;
    }
  }
  
  public String getThumbFilter()
  {
    return this.currentThumbFilter;
  }
  
  public String getThumbKey()
  {
    return this.currentThumbKey;
  }
  
  public TLRPC.FileLocation getThumbLocation()
  {
    return this.currentThumbLocation;
  }
  
  public boolean getVisible()
  {
    return this.isVisible;
  }
  
  public int getcurrentAccount()
  {
    return this.currentAccount;
  }
  
  public boolean hasBitmapImage()
  {
    if ((this.currentImage != null) || (this.currentThumb != null) || (this.staticThumb != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean hasImage()
  {
    if ((this.currentImage != null) || (this.currentThumb != null) || (this.currentKey != null) || (this.currentHttpUrl != null) || (this.staticThumb != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isAllowStartAnimation()
  {
    return this.allowStartAnimation;
  }
  
  public boolean isAnimationRunning()
  {
    if (((this.currentImage instanceof AnimatedFileDrawable)) && (((AnimatedFileDrawable)this.currentImage).isRunning())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isForceLoding()
  {
    return this.forceLoding;
  }
  
  public boolean isForcePreview()
  {
    return this.forcePreview;
  }
  
  public boolean isInsideImage(float paramFloat1, float paramFloat2)
  {
    if ((paramFloat1 >= this.imageX) && (paramFloat1 <= this.imageX + this.imageW) && (paramFloat2 >= this.imageY) && (paramFloat2 <= this.imageY + this.imageH)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isNeedsQualityThumb()
  {
    return this.needsQualityThumb;
  }
  
  public boolean isShouldGenerateQualityThumb()
  {
    return this.shouldGenerateQualityThumb;
  }
  
  public boolean onAttachedToWindow()
  {
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
    if (this.needsQualityThumb) {
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messageThumbGenerated);
    }
    if ((this.setImageBackup != null) && ((this.setImageBackup.fileLocation != null) || (this.setImageBackup.httpUrl != null) || (this.setImageBackup.thumbLocation != null) || (this.setImageBackup.thumb != null))) {
      setImage(this.setImageBackup.fileLocation, this.setImageBackup.httpUrl, this.setImageBackup.filter, this.setImageBackup.thumb, this.setImageBackup.thumbLocation, this.setImageBackup.thumbFilter, this.setImageBackup.size, this.setImageBackup.ext, this.setImageBackup.cacheType);
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void onDetachedFromWindow()
  {
    if ((this.currentImageLocation != null) || (this.currentHttpUrl != null) || (this.currentThumbLocation != null) || (this.staticThumb != null))
    {
      if (this.setImageBackup == null) {
        this.setImageBackup = new SetImageBackup(null);
      }
      this.setImageBackup.fileLocation = this.currentImageLocation;
      this.setImageBackup.httpUrl = this.currentHttpUrl;
      this.setImageBackup.filter = this.currentFilter;
      this.setImageBackup.thumb = this.staticThumb;
      this.setImageBackup.thumbLocation = this.currentThumbLocation;
      this.setImageBackup.thumbFilter = this.currentThumbFilter;
      this.setImageBackup.size = this.currentSize;
      this.setImageBackup.ext = this.currentExt;
      this.setImageBackup.cacheType = this.currentCacheType;
    }
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
    clearImage();
  }
  
  public void setAllowDecodeSingleFrame(boolean paramBoolean)
  {
    this.allowDecodeSingleFrame = paramBoolean;
  }
  
  public void setAllowStartAnimation(boolean paramBoolean)
  {
    this.allowStartAnimation = paramBoolean;
  }
  
  public void setAlpha(float paramFloat)
  {
    this.overrideAlpha = paramFloat;
  }
  
  public void setAspectFit(boolean paramBoolean)
  {
    this.isAspectFit = paramBoolean;
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.colorFilter = paramColorFilter;
  }
  
  public void setCrossfadeAlpha(byte paramByte)
  {
    this.crossfadeAlpha = ((byte)paramByte);
  }
  
  public void setCrossfadeWithOldImage(boolean paramBoolean)
  {
    this.crossfadeWithOldImage = paramBoolean;
  }
  
  public void setCurrentAccount(int paramInt)
  {
    this.currentAccount = paramInt;
  }
  
  public void setCurrentAlpha(float paramFloat)
  {
    this.currentAlpha = paramFloat;
  }
  
  public void setDelegate(ImageReceiverDelegate paramImageReceiverDelegate)
  {
    this.delegate = paramImageReceiverDelegate;
  }
  
  public void setForceCrossfade(boolean paramBoolean)
  {
    this.forceCrossfade = paramBoolean;
  }
  
  public void setForceLoading(boolean paramBoolean)
  {
    this.forceLoding = paramBoolean;
  }
  
  public void setForcePreview(boolean paramBoolean)
  {
    this.forcePreview = paramBoolean;
  }
  
  public void setImage(String paramString1, String paramString2, Drawable paramDrawable, String paramString3, int paramInt)
  {
    setImage(null, paramString1, paramString2, paramDrawable, null, null, paramInt, paramString3, 1);
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, Drawable paramDrawable, int paramInt1, String paramString2, int paramInt2)
  {
    setImage(paramTLObject, null, paramString1, paramDrawable, null, null, paramInt1, paramString2, paramInt2);
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, Drawable paramDrawable, String paramString2, int paramInt)
  {
    setImage(paramTLObject, null, paramString1, paramDrawable, null, null, 0, paramString2, paramInt);
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, String paramString2, Drawable paramDrawable, TLRPC.FileLocation paramFileLocation, String paramString3, int paramInt1, String paramString4, int paramInt2)
  {
    if (this.setImageBackup != null)
    {
      this.setImageBackup.fileLocation = null;
      this.setImageBackup.httpUrl = null;
      this.setImageBackup.thumbLocation = null;
      this.setImageBackup.thumb = null;
    }
    boolean bool1;
    label260:
    boolean bool2;
    if (((paramTLObject == null) && (paramString1 == null) && (paramFileLocation == null)) || ((paramTLObject != null) && (!(paramTLObject instanceof TLRPC.TL_fileLocation)) && (!(paramTLObject instanceof TLRPC.TL_fileEncryptedLocation)) && (!(paramTLObject instanceof TLRPC.TL_document)) && (!(paramTLObject instanceof TLRPC.TL_webDocument)) && (!(paramTLObject instanceof TLRPC.TL_documentEncrypted))))
    {
      for (paramInt1 = 0; paramInt1 < 3; paramInt1++) {
        recycleBitmap(null, paramInt1);
      }
      this.currentKey = null;
      this.currentExt = paramString4;
      this.currentThumbKey = null;
      this.currentThumbFilter = null;
      this.currentImageLocation = null;
      this.currentHttpUrl = null;
      this.currentFilter = null;
      this.currentCacheType = 0;
      this.staticThumb = paramDrawable;
      this.currentAlpha = 1.0F;
      this.currentThumbLocation = null;
      this.currentSize = 0;
      this.currentImage = null;
      this.bitmapShader = null;
      this.bitmapShaderThumb = null;
      this.crossfadeShader = null;
      ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
      if (this.parentView != null)
      {
        if (this.invalidateAll) {
          this.parentView.invalidate();
        }
      }
      else if (this.delegate != null)
      {
        paramTLObject = this.delegate;
        if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null)) {
          break label318;
        }
        bool1 = true;
        if (this.currentImage != null) {
          break label324;
        }
        bool2 = true;
        label270:
        paramTLObject.didSetImage(this, bool1, bool2);
      }
    }
    for (;;)
    {
      return;
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break;
      label318:
      bool1 = false;
      break label260;
      label324:
      bool2 = false;
      break label270;
      TLRPC.FileLocation localFileLocation = paramFileLocation;
      if (!(paramFileLocation instanceof TLRPC.TL_fileLocation))
      {
        localFileLocation = paramFileLocation;
        if (!(paramFileLocation instanceof TLRPC.TL_fileEncryptedLocation)) {
          localFileLocation = null;
        }
      }
      paramFileLocation = null;
      Object localObject1;
      label414:
      Object localObject2;
      if (paramTLObject != null) {
        if ((paramTLObject instanceof TLRPC.FileLocation))
        {
          paramFileLocation = (TLRPC.FileLocation)paramTLObject;
          paramFileLocation = paramFileLocation.volume_id + "_" + paramFileLocation.local_id;
          localObject1 = paramTLObject;
          localObject2 = paramFileLocation;
          if (paramFileLocation != null)
          {
            localObject2 = paramFileLocation;
            if (paramString2 != null) {
              localObject2 = paramFileLocation + "@" + paramString2;
            }
          }
          if ((this.currentKey != null) && (localObject2 != null) && (this.currentKey.equals(localObject2)))
          {
            if (this.delegate != null)
            {
              paramTLObject = this.delegate;
              if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null)) {
                break label1021;
              }
              bool1 = true;
              label518:
              if (this.currentImage != null) {
                break label1027;
              }
              bool2 = true;
              label528:
              paramTLObject.didSetImage(this, bool1, bool2);
            }
            if ((!this.canceledLoading) && (!this.forcePreview)) {
              continue;
            }
          }
          paramTLObject = null;
          if (localFileLocation != null)
          {
            paramFileLocation = localFileLocation.volume_id + "_" + localFileLocation.local_id;
            paramTLObject = paramFileLocation;
            if (paramString3 != null) {
              paramTLObject = paramFileLocation + "@" + paramString3;
            }
          }
          if (!this.crossfadeWithOldImage) {
            break label1117;
          }
          if (this.currentImage == null) {
            break label1033;
          }
          recycleBitmap(paramTLObject, 1);
          recycleBitmap(null, 2);
          this.crossfadeShader = this.bitmapShader;
          this.crossfadeImage = this.currentImage;
          this.crossfadeKey = this.currentKey;
          this.currentImage = null;
          this.currentKey = null;
          label689:
          this.currentThumbKey = paramTLObject;
          this.currentKey = ((String)localObject2);
          this.currentExt = paramString4;
          this.currentImageLocation = ((TLObject)localObject1);
          this.currentHttpUrl = paramString1;
          this.currentFilter = paramString2;
          this.currentThumbFilter = paramString3;
          this.currentSize = paramInt1;
          this.currentCacheType = paramInt2;
          this.currentThumbLocation = localFileLocation;
          this.staticThumb = paramDrawable;
          this.bitmapShader = null;
          this.bitmapShaderThumb = null;
          this.currentAlpha = 1.0F;
          if (this.delegate != null)
          {
            paramTLObject = this.delegate;
            if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null)) {
              break label1144;
            }
            bool1 = true;
            label803:
            if (this.currentImage != null) {
              break label1150;
            }
          }
        }
      }
      label1021:
      label1027:
      label1033:
      label1117:
      label1144:
      label1150:
      for (bool2 = true;; bool2 = false)
      {
        paramTLObject.didSetImage(this, bool1, bool2);
        ImageLoader.getInstance().loadImageForImageReceiver(this);
        if (this.parentView == null) {
          break;
        }
        if (!this.invalidateAll) {
          break label1156;
        }
        this.parentView.invalidate();
        break;
        if ((paramTLObject instanceof TLRPC.TL_webDocument))
        {
          paramFileLocation = Utilities.MD5(((TLRPC.TL_webDocument)paramTLObject).url);
          localObject1 = paramTLObject;
          break label414;
        }
        localObject1 = (TLRPC.Document)paramTLObject;
        if (((TLRPC.Document)localObject1).dc_id != 0)
        {
          if (((TLRPC.Document)localObject1).version == 0)
          {
            paramFileLocation = ((TLRPC.Document)localObject1).dc_id + "_" + ((TLRPC.Document)localObject1).id;
            localObject1 = paramTLObject;
            break label414;
          }
          paramFileLocation = ((TLRPC.Document)localObject1).dc_id + "_" + ((TLRPC.Document)localObject1).id + "_" + ((TLRPC.Document)localObject1).version;
          localObject1 = paramTLObject;
          break label414;
        }
        localObject1 = null;
        break label414;
        localObject1 = paramTLObject;
        if (paramString1 == null) {
          break label414;
        }
        paramFileLocation = Utilities.MD5(paramString1);
        localObject1 = paramTLObject;
        break label414;
        bool1 = false;
        break label518;
        bool2 = false;
        break label528;
        if (this.currentThumb != null)
        {
          recycleBitmap((String)localObject2, 0);
          recycleBitmap(null, 2);
          this.crossfadeShader = this.bitmapShaderThumb;
          this.crossfadeImage = this.currentThumb;
          this.crossfadeKey = this.currentThumbKey;
          this.currentThumb = null;
          this.currentThumbKey = null;
          break label689;
        }
        recycleBitmap((String)localObject2, 0);
        recycleBitmap(paramTLObject, 1);
        recycleBitmap(null, 2);
        this.crossfadeShader = null;
        break label689;
        recycleBitmap((String)localObject2, 0);
        recycleBitmap(paramTLObject, 1);
        recycleBitmap(null, 2);
        this.crossfadeShader = null;
        break label689;
        bool1 = false;
        break label803;
      }
      label1156:
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
    }
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, TLRPC.FileLocation paramFileLocation, String paramString2, int paramInt1, String paramString3, int paramInt2)
  {
    setImage(paramTLObject, null, paramString1, null, paramFileLocation, paramString2, paramInt1, paramString3, paramInt2);
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, TLRPC.FileLocation paramFileLocation, String paramString2, String paramString3, int paramInt)
  {
    setImage(paramTLObject, null, paramString1, null, paramFileLocation, paramString2, 0, paramString3, paramInt);
  }
  
  public void setImageBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap != null) {}
    for (paramBitmap = new BitmapDrawable(null, paramBitmap);; paramBitmap = null)
    {
      setImageBitmap(paramBitmap);
      return;
    }
  }
  
  public void setImageBitmap(Drawable paramDrawable)
  {
    boolean bool = false;
    ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
    for (int i = 0; i < 3; i++) {
      recycleBitmap(null, i);
    }
    this.staticThumb = paramDrawable;
    if ((this.roundRadius != 0) && ((paramDrawable instanceof BitmapDrawable)))
    {
      this.bitmapShaderThumb = new BitmapShader(((BitmapDrawable)paramDrawable).getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      this.currentThumbLocation = null;
      this.currentKey = null;
      this.currentExt = null;
      this.currentThumbKey = null;
      this.currentImage = null;
      this.currentThumbFilter = null;
      this.currentImageLocation = null;
      this.currentHttpUrl = null;
      this.currentFilter = null;
      this.currentSize = 0;
      this.currentCacheType = 0;
      this.bitmapShader = null;
      this.crossfadeShader = null;
      if (this.setImageBackup != null)
      {
        this.setImageBackup.fileLocation = null;
        this.setImageBackup.httpUrl = null;
        this.setImageBackup.thumbLocation = null;
        this.setImageBackup.thumb = null;
      }
      this.currentAlpha = 1.0F;
      if (this.delegate != null)
      {
        paramDrawable = this.delegate;
        if ((this.currentThumb != null) || (this.staticThumb != null)) {
          bool = true;
        }
        paramDrawable.didSetImage(this, bool, true);
      }
      if (this.parentView != null)
      {
        if (!this.invalidateAll) {
          break label248;
        }
        this.parentView.invalidate();
      }
    }
    for (;;)
    {
      return;
      this.bitmapShaderThumb = null;
      break;
      label248:
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
    }
  }
  
  protected boolean setImageBitmapByKey(BitmapDrawable paramBitmapDrawable, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = bool2;
    if (paramBitmapDrawable != null)
    {
      if (paramString != null) {
        break label25;
      }
      bool3 = bool2;
    }
    label25:
    do
    {
      do
      {
        return bool3;
        if (paramBoolean1) {
          break;
        }
        bool3 = bool2;
      } while (this.currentKey == null);
      bool3 = bool2;
    } while (!paramString.equals(this.currentKey));
    if (!(paramBitmapDrawable instanceof AnimatedFileDrawable)) {
      ImageLoader.getInstance().incrementUseCount(this.currentKey);
    }
    this.currentImage = paramBitmapDrawable;
    if ((this.roundRadius != 0) && ((paramBitmapDrawable instanceof BitmapDrawable))) {
      if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
      {
        ((AnimatedFileDrawable)paramBitmapDrawable).setRoundRadius(this.roundRadius);
        label109:
        if (((paramBoolean2) || (this.forcePreview)) && (!this.forceCrossfade)) {
          break label345;
        }
        if (((this.currentThumb == null) && (this.staticThumb == null)) || (this.currentAlpha == 1.0F) || (this.forceCrossfade))
        {
          this.currentAlpha = 0.0F;
          this.lastUpdateAlphaTime = System.currentTimeMillis();
          if ((this.currentThumb == null) && (this.staticThumb == null)) {
            break label340;
          }
          paramBoolean1 = true;
          label186:
          this.crossfadeWithThumb = paramBoolean1;
        }
        label191:
        if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
        {
          paramBitmapDrawable = (AnimatedFileDrawable)paramBitmapDrawable;
          paramBitmapDrawable.setParentView(this.parentView);
          if (!this.allowStartAnimation) {
            break label353;
          }
          paramBitmapDrawable.start();
        }
        label222:
        if (this.parentView != null)
        {
          if (!this.invalidateAll) {
            break label364;
          }
          this.parentView.invalidate();
        }
        label243:
        if (this.delegate != null)
        {
          paramBitmapDrawable = this.delegate;
          if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null)) {
            break label713;
          }
        }
      }
    }
    label340:
    label345:
    label353:
    label364:
    label517:
    label628:
    label669:
    label675:
    label677:
    label713:
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      paramBoolean2 = bool1;
      if (this.currentImage == null) {
        paramBoolean2 = true;
      }
      paramBitmapDrawable.didSetImage(this, paramBoolean1, paramBoolean2);
      bool3 = true;
      break;
      this.bitmapShader = new BitmapShader(paramBitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      break label109;
      this.bitmapShader = null;
      break label109;
      paramBoolean1 = false;
      break label186;
      this.currentAlpha = 1.0F;
      break label191;
      paramBitmapDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
      break label222;
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break label243;
      if ((this.currentThumb != null) || ((this.currentImage != null) && ((!(this.currentImage instanceof AnimatedFileDrawable)) || (((AnimatedFileDrawable)this.currentImage).hasBitmap())) && (!this.forcePreview))) {
        break label243;
      }
      bool3 = bool2;
      if (this.currentThumbKey == null) {
        break;
      }
      bool3 = bool2;
      if (!paramString.equals(this.currentThumbKey)) {
        break;
      }
      ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
      this.currentThumb = paramBitmapDrawable;
      if ((this.roundRadius != 0) && ((paramBitmapDrawable instanceof BitmapDrawable))) {
        if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
        {
          ((AnimatedFileDrawable)paramBitmapDrawable).setRoundRadius(this.roundRadius);
          if ((paramBoolean2) || (this.crossfadeAlpha == 2)) {
            break label669;
          }
          if ((this.parentMessageObject == null) || (!this.parentMessageObject.isRoundVideo()) || (!this.parentMessageObject.isSending())) {
            break label628;
          }
          this.currentAlpha = 1.0F;
        }
      }
      for (;;)
      {
        if (((this.staticThumb instanceof BitmapDrawable)) || (this.parentView == null)) {
          break label675;
        }
        if (!this.invalidateAll) {
          break label677;
        }
        this.parentView.invalidate();
        break;
        this.bitmapShaderThumb = new BitmapShader(paramBitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        break label517;
        this.bitmapShaderThumb = null;
        break label517;
        this.currentAlpha = 0.0F;
        this.lastUpdateAlphaTime = System.currentTimeMillis();
        if ((this.staticThumb != null) && (this.currentKey == null)) {}
        for (paramBoolean1 = true;; paramBoolean1 = false)
        {
          this.crossfadeWithThumb = paramBoolean1;
          break;
        }
        this.currentAlpha = 1.0F;
      }
      break label243;
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break label243;
    }
  }
  
  public void setImageCoords(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.imageX = paramInt1;
    this.imageY = paramInt2;
    this.imageW = paramInt3;
    this.imageH = paramInt4;
  }
  
  public void setImageWidth(int paramInt)
  {
    this.imageW = paramInt;
  }
  
  public void setImageX(int paramInt)
  {
    this.imageX = paramInt;
  }
  
  public void setImageY(int paramInt)
  {
    this.imageY = paramInt;
  }
  
  public void setInvalidateAll(boolean paramBoolean)
  {
    this.invalidateAll = paramBoolean;
  }
  
  public void setManualAlphaAnimator(boolean paramBoolean)
  {
    this.manualAlphaAnimator = paramBoolean;
  }
  
  public void setNeedsQualityThumb(boolean paramBoolean)
  {
    this.needsQualityThumb = paramBoolean;
    if (this.needsQualityThumb) {
      NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messageThumbGenerated);
    }
    for (;;)
    {
      return;
      NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
    }
  }
  
  public void setOrientation(int paramInt, boolean paramBoolean)
  {
    int i;
    for (;;)
    {
      i = paramInt;
      if (paramInt >= 0) {
        break;
      }
      paramInt += 360;
    }
    while (i > 360) {
      i -= 360;
    }
    this.orientation = i;
    this.centerRotation = paramBoolean;
  }
  
  public void setParam(int paramInt)
  {
    this.param = paramInt;
  }
  
  public void setParentMessageObject(MessageObject paramMessageObject)
  {
    this.parentMessageObject = paramMessageObject;
  }
  
  public void setParentView(View paramView)
  {
    this.parentView = paramView;
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      ((AnimatedFileDrawable)this.currentImage).setParentView(this.parentView);
    }
  }
  
  public void setPressed(int paramInt)
  {
    this.isPressed = paramInt;
  }
  
  public void setRoundRadius(int paramInt)
  {
    this.roundRadius = paramInt;
  }
  
  public void setShouldGenerateQualityThumb(boolean paramBoolean)
  {
    this.shouldGenerateQualityThumb = paramBoolean;
  }
  
  protected void setTag(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.thumbTag = paramInt;
    }
    for (;;)
    {
      return;
      this.tag = paramInt;
    }
  }
  
  public void setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.isVisible == paramBoolean1) {}
    for (;;)
    {
      return;
      this.isVisible = paramBoolean1;
      if ((paramBoolean2) && (this.parentView != null)) {
        if (this.invalidateAll) {
          this.parentView.invalidate();
        } else {
          this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        }
      }
    }
  }
  
  public void startAnimation()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      ((AnimatedFileDrawable)this.currentImage).start();
    }
  }
  
  public void stopAnimation()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      ((AnimatedFileDrawable)this.currentImage).stop();
    }
  }
  
  public static class BitmapHolder
  {
    public Bitmap bitmap;
    private String key;
    
    public BitmapHolder(Bitmap paramBitmap, String paramString)
    {
      this.bitmap = paramBitmap;
      this.key = paramString;
      if (this.key != null) {
        ImageLoader.getInstance().incrementUseCount(this.key);
      }
    }
    
    public int getHeight()
    {
      if (this.bitmap != null) {}
      for (int i = this.bitmap.getHeight();; i = 0) {
        return i;
      }
    }
    
    public int getWidth()
    {
      if (this.bitmap != null) {}
      for (int i = this.bitmap.getWidth();; i = 0) {
        return i;
      }
    }
    
    public boolean isRecycled()
    {
      if ((this.bitmap == null) || (this.bitmap.isRecycled())) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void release()
    {
      if (this.key == null) {}
      for (this.bitmap = null;; this.bitmap = null)
      {
        return;
        boolean bool = ImageLoader.getInstance().decrementUseCount(this.key);
        if ((!ImageLoader.getInstance().isInCache(this.key)) && (bool)) {
          this.bitmap.recycle();
        }
        this.key = null;
      }
    }
  }
  
  public static abstract interface ImageReceiverDelegate
  {
    public abstract void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2);
  }
  
  private class SetImageBackup
  {
    public int cacheType;
    public String ext;
    public TLObject fileLocation;
    public String filter;
    public String httpUrl;
    public int size;
    public Drawable thumb;
    public String thumbFilter;
    public TLRPC.FileLocation thumbLocation;
    
    private SetImageBackup() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/ImageReceiver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */