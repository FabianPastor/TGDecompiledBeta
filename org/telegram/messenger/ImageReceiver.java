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
import org.telegram.ui.Components.AnimatedFileDrawable;

public class ImageReceiver
  implements NotificationCenter.NotificationCenterDelegate
{
  private static Paint roundPaint;
  private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, PorterDuff.Mode.MULTIPLY);
  private boolean allowStartAnimation = true;
  private RectF bitmapRect = new RectF();
  private BitmapShader bitmapShader;
  private BitmapShader bitmapShaderThumb;
  private boolean canceledLoading;
  private boolean centerRotation;
  private ColorFilter colorFilter;
  private byte crossfadeAlpha = 1;
  private boolean crossfadeWithThumb;
  private float currentAlpha;
  private boolean currentCacheOnly;
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
  private boolean forcePreview;
  private int imageH;
  private int imageW;
  private int imageX;
  private int imageY;
  private boolean invalidateAll;
  private boolean isAspectFit;
  private boolean isPressed;
  private boolean isVisible = true;
  private long lastUpdateAlphaTime;
  private boolean needsQualityThumb;
  private int orientation;
  private float overrideAlpha = 1.0F;
  private MessageObject parentMessageObject;
  private View parentView;
  private int roundRadius;
  private RectF roundRect = new RectF();
  private SetImageBackup setImageBackup;
  private Matrix shaderMatrix = new Matrix();
  private boolean shouldGenerateQualityThumb;
  private Drawable staticThumb;
  private Integer tag;
  private Integer thumbTag;
  
  public ImageReceiver()
  {
    this(null);
  }
  
  public ImageReceiver(View paramView)
  {
    this.parentView = paramView;
    if (roundPaint == null) {
      roundPaint = new Paint(1);
    }
  }
  
  private void checkAlphaAnimation(boolean paramBoolean)
  {
    if (this.currentAlpha != 1.0F)
    {
      if (!paramBoolean)
      {
        long l2 = System.currentTimeMillis() - this.lastUpdateAlphaTime;
        long l1 = l2;
        if (l2 > 18L) {
          l1 = 18L;
        }
        this.currentAlpha += (float)l1 / 150.0F;
        if (this.currentAlpha > 1.0F) {
          this.currentAlpha = 1.0F;
        }
      }
      this.lastUpdateAlphaTime = System.currentTimeMillis();
      if (this.parentView != null)
      {
        if (!this.invalidateAll) {
          break label96;
        }
        this.parentView.invalidate();
      }
    }
    return;
    label96:
    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
  }
  
  private void drawDrawable(Canvas paramCanvas, Drawable paramDrawable, int paramInt, BitmapShader paramBitmapShader)
  {
    BitmapDrawable localBitmapDrawable;
    int i;
    label36:
    label61:
    label84:
    int j;
    label133:
    float f1;
    float f2;
    label309:
    int k;
    int m;
    if ((paramDrawable instanceof BitmapDrawable))
    {
      localBitmapDrawable = (BitmapDrawable)paramDrawable;
      if (paramBitmapShader != null)
      {
        paramDrawable = roundPaint;
        if ((paramDrawable == null) || (paramDrawable.getColorFilter() == null)) {
          break label465;
        }
        i = 1;
        if ((i == 0) || (this.isPressed)) {
          break label480;
        }
        if (paramBitmapShader == null) {
          break label471;
        }
        roundPaint.setColorFilter(null);
        if (this.colorFilter != null)
        {
          if (paramBitmapShader == null) {
            break label521;
          }
          roundPaint.setColorFilter(this.colorFilter);
        }
        if (!(localBitmapDrawable instanceof AnimatedFileDrawable)) {
          break label550;
        }
        if ((this.orientation % 360 != 90) && (this.orientation % 360 != 270)) {
          break label533;
        }
        j = localBitmapDrawable.getIntrinsicHeight();
        i = localBitmapDrawable.getIntrinsicWidth();
        f1 = j / this.imageW;
        f2 = i / this.imageH;
        if (paramBitmapShader == null) {
          break label757;
        }
        roundPaint.setShader(paramBitmapShader);
        float f3 = Math.min(f1, f2);
        this.roundRect.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        this.shaderMatrix.reset();
        if (Math.abs(f1 - f2) <= 1.0E-5F) {
          break label684;
        }
        if (j / f2 <= this.imageW) {
          break label623;
        }
        this.drawRegion.set(this.imageX - ((int)(j / f2) - this.imageW) / 2, this.imageY, this.imageX + ((int)(j / f2) + this.imageW) / 2, this.imageY + this.imageH);
        if (this.isVisible)
        {
          if (Math.abs(f1 - f2) <= 1.0E-5F) {
            break label720;
          }
          k = (int)Math.floor(this.imageW * f3);
          m = (int)Math.floor(this.imageH * f3);
          this.bitmapRect.set((j - k) / 2, (i - m) / 2, (j + k) / 2, (i + m) / 2);
          this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, Matrix.ScaleToFit.START);
          label418:
          paramBitmapShader.setLocalMatrix(this.shaderMatrix);
          roundPaint.setAlpha(paramInt);
          paramCanvas.drawRoundRect(this.roundRect, this.roundRadius, this.roundRadius, roundPaint);
        }
      }
    }
    label465:
    label471:
    label480:
    label521:
    label533:
    label550:
    label623:
    label684:
    label720:
    label757:
    label967:
    label1164:
    label1330:
    label1390:
    label1484:
    label1763:
    label1857:
    do
    {
      return;
      paramDrawable = localBitmapDrawable.getPaint();
      break;
      i = 0;
      break label36;
      localBitmapDrawable.setColorFilter(null);
      break label61;
      if ((i != 0) || (!this.isPressed)) {
        break label61;
      }
      if (paramBitmapShader != null)
      {
        roundPaint.setColorFilter(selectedColorFilter);
        break label61;
      }
      localBitmapDrawable.setColorFilter(selectedColorFilter);
      break label61;
      localBitmapDrawable.setColorFilter(this.colorFilter);
      break label84;
      j = localBitmapDrawable.getIntrinsicWidth();
      i = localBitmapDrawable.getIntrinsicHeight();
      break label133;
      if ((this.orientation % 360 == 90) || (this.orientation % 360 == 270))
      {
        j = localBitmapDrawable.getBitmap().getHeight();
        i = localBitmapDrawable.getBitmap().getWidth();
        break label133;
      }
      j = localBitmapDrawable.getBitmap().getWidth();
      i = localBitmapDrawable.getBitmap().getHeight();
      break label133;
      this.drawRegion.set(this.imageX, this.imageY - ((int)(i / f1) - this.imageH) / 2, this.imageX + this.imageW, this.imageY + ((int)(i / f1) + this.imageH) / 2);
      break label309;
      this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break label309;
      this.bitmapRect.set(0.0F, 0.0F, j, i);
      this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, Matrix.ScaleToFit.FILL);
      break label418;
      if (this.isAspectFit)
      {
        f1 = Math.max(f1, f2);
        paramCanvas.save();
        j = (int)(j / f1);
        i = (int)(i / f1);
        this.drawRegion.set(this.imageX + (this.imageW - j) / 2, this.imageY + (this.imageH - i) / 2, this.imageX + (this.imageW + j) / 2, this.imageY + (this.imageH + i) / 2);
        localBitmapDrawable.setBounds(this.drawRegion);
        try
        {
          localBitmapDrawable.setAlpha(paramInt);
          localBitmapDrawable.draw(paramCanvas);
          paramCanvas.restore();
          return;
        }
        catch (Exception paramDrawable)
        {
          if (localBitmapDrawable != this.currentImage) {
            break label967;
          }
        }
        if (this.currentKey != null)
        {
          ImageLoader.getInstance().removeImage(this.currentKey);
          this.currentKey = null;
        }
        for (;;)
        {
          setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheOnly);
          FileLog.e("tmessages", paramDrawable);
          break;
          if ((localBitmapDrawable == this.currentThumb) && (this.currentThumbKey != null))
          {
            ImageLoader.getInstance().removeImage(this.currentThumbKey);
            this.currentThumbKey = null;
          }
        }
      }
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
          if (j / f2 <= this.imageW) {
            break label1330;
          }
          i = (int)(j / f2);
          this.drawRegion.set(this.imageX - (i - this.imageW) / 2, this.imageY, this.imageX + (this.imageW + i) / 2, this.imageY + this.imageH);
          if ((this.orientation % 360 != 90) && (this.orientation % 360 != 270)) {
            break label1390;
          }
          i = (this.drawRegion.right - this.drawRegion.left) / 2;
          j = (this.drawRegion.bottom - this.drawRegion.top) / 2;
          k = (this.drawRegion.right + this.drawRegion.left) / 2;
          m = (this.drawRegion.top + this.drawRegion.bottom) / 2;
          localBitmapDrawable.setBounds(k - j, m - i, k + j, m + i);
        }
        for (;;)
        {
          if (this.isVisible) {}
          try
          {
            localBitmapDrawable.setAlpha(paramInt);
            localBitmapDrawable.draw(paramCanvas);
            paramCanvas.restore();
            return;
          }
          catch (Exception paramDrawable)
          {
            if (localBitmapDrawable != this.currentImage) {
              break label1484;
            }
          }
          paramCanvas.rotate(this.orientation, 0.0F, 0.0F);
          break;
          i = (int)(i / f1);
          this.drawRegion.set(this.imageX, this.imageY - (i - this.imageH) / 2, this.imageX + this.imageW, this.imageY + (this.imageH + i) / 2);
          break label1164;
          localBitmapDrawable.setBounds(this.drawRegion);
        }
        if (this.currentKey != null)
        {
          ImageLoader.getInstance().removeImage(this.currentKey);
          this.currentKey = null;
        }
        for (;;)
        {
          setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheOnly);
          FileLog.e("tmessages", paramDrawable);
          break;
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
        this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        if ((this.orientation % 360 != 90) && (this.orientation % 360 != 270)) {
          break label1763;
        }
        i = (this.drawRegion.right - this.drawRegion.left) / 2;
        j = (this.drawRegion.bottom - this.drawRegion.top) / 2;
        k = (this.drawRegion.right + this.drawRegion.left) / 2;
        m = (this.drawRegion.top + this.drawRegion.bottom) / 2;
        localBitmapDrawable.setBounds(k - j, m - i, k + j, m + i);
      }
      for (;;)
      {
        if (this.isVisible) {}
        try
        {
          localBitmapDrawable.setAlpha(paramInt);
          localBitmapDrawable.draw(paramCanvas);
          paramCanvas.restore();
          return;
        }
        catch (Exception paramDrawable)
        {
          if (localBitmapDrawable != this.currentImage) {
            break label1857;
          }
        }
        paramCanvas.rotate(this.orientation, 0.0F, 0.0F);
        break;
        localBitmapDrawable.setBounds(this.drawRegion);
      }
      if (this.currentKey != null)
      {
        ImageLoader.getInstance().removeImage(this.currentKey);
        this.currentKey = null;
      }
      for (;;)
      {
        setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheOnly);
        FileLog.e("tmessages", paramDrawable);
        break;
        if ((localBitmapDrawable == this.currentThumb) && (this.currentThumbKey != null))
        {
          ImageLoader.getInstance().removeImage(this.currentThumbKey);
          this.currentThumbKey = null;
        }
      }
      this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      paramDrawable.setBounds(this.drawRegion);
    } while (!this.isVisible);
    try
    {
      paramDrawable.setAlpha(paramInt);
      paramDrawable.draw(paramCanvas);
      return;
    }
    catch (Exception paramCanvas)
    {
      FileLog.e("tmessages", paramCanvas);
    }
  }
  
  private void recycleBitmap(String paramString, boolean paramBoolean)
  {
    String str;
    Drawable localDrawable;
    if (paramBoolean)
    {
      str = this.currentThumbKey;
      localDrawable = this.currentThumb;
      if ((str != null) && ((paramString == null) || (!paramString.equals(str))) && (localDrawable != null))
      {
        if (!(localDrawable instanceof AnimatedFileDrawable)) {
          break label85;
        }
        ((AnimatedFileDrawable)localDrawable).recycle();
      }
    }
    for (;;)
    {
      if (!paramBoolean) {
        break label133;
      }
      this.currentThumb = null;
      this.currentThumbKey = null;
      return;
      str = this.currentKey;
      localDrawable = this.currentImage;
      break;
      label85:
      if ((localDrawable instanceof BitmapDrawable))
      {
        paramString = ((BitmapDrawable)localDrawable).getBitmap();
        boolean bool = ImageLoader.getInstance().decrementUseCount(str);
        if ((!ImageLoader.getInstance().isInCache(str)) && (bool)) {
          paramString.recycle();
        }
      }
    }
    label133:
    this.currentImage = null;
    this.currentKey = null;
  }
  
  public void cancelLoadImage()
  {
    ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
    this.canceledLoading = true;
  }
  
  public void clearImage()
  {
    recycleBitmap(null, false);
    recycleBitmap(null, true);
    if (this.needsQualityThumb)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
      ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    String str;
    if (paramInt == NotificationCenter.messageThumbGenerated)
    {
      str = (String)paramVarArgs[1];
      if ((this.currentThumbKey != null) && (this.currentThumbKey.equals(str)))
      {
        if (this.currentThumb == null) {
          ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
        }
        this.currentThumb = ((BitmapDrawable)paramVarArgs[0]);
        if ((this.roundRadius == 0) || (this.currentImage != null) || (!(this.currentThumb instanceof BitmapDrawable)) || ((this.currentThumb instanceof AnimatedFileDrawable))) {
          break label157;
        }
        this.bitmapShaderThumb = new BitmapShader(((BitmapDrawable)this.currentThumb).getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if ((this.staticThumb instanceof BitmapDrawable)) {
          this.staticThumb = null;
        }
        if (this.parentView != null)
        {
          if (!this.invalidateAll) {
            break label165;
          }
          this.parentView.invalidate();
        }
      }
    }
    label157:
    label165:
    do
    {
      do
      {
        do
        {
          return;
          this.bitmapShaderThumb = null;
          break;
          this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
          return;
        } while (paramInt != NotificationCenter.didReplacedPhotoInMemCache);
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
      } while (this.setImageBackup == null);
      if ((this.currentKey != null) && (this.currentKey.equals(str)))
      {
        this.currentKey = ((String)paramVarArgs[1]);
        this.currentImageLocation = ((TLRPC.FileLocation)paramVarArgs[2]);
      }
    } while ((this.currentThumbKey == null) || (!this.currentThumbKey.equals(str)));
    this.currentThumbKey = ((String)paramVarArgs[1]);
    this.currentThumbLocation = ((TLRPC.FileLocation)paramVarArgs[2]);
  }
  
  public boolean draw(Canvas paramCanvas)
  {
    Drawable localDrawable = null;
    for (;;)
    {
      boolean bool;
      int i;
      Object localObject2;
      Object localObject1;
      int j;
      try
      {
        if ((!(this.currentImage instanceof AnimatedFileDrawable)) || (((AnimatedFileDrawable)this.currentImage).hasBitmap())) {
          break label417;
        }
        bool = true;
        i = 0;
        if ((!this.forcePreview) && (this.currentImage != null) && (!bool))
        {
          localDrawable = this.currentImage;
          if (localDrawable == null) {
            break label380;
          }
          if (this.crossfadeAlpha == 0) {
            break label338;
          }
          if ((this.crossfadeWithThumb) && (bool))
          {
            drawDrawable(paramCanvas, localDrawable, (int)(this.overrideAlpha * 255.0F), this.bitmapShaderThumb);
            if ((!bool) || (!this.crossfadeWithThumb)) {
              break label423;
            }
            bool = true;
            checkAlphaAnimation(bool);
            return true;
          }
        }
        else
        {
          if ((this.staticThumb instanceof BitmapDrawable))
          {
            localDrawable = this.staticThumb;
            i = 1;
            continue;
          }
          if (this.currentThumb == null) {
            continue;
          }
          localDrawable = this.currentThumb;
          i = 1;
          continue;
        }
        if ((this.crossfadeWithThumb) && (this.currentAlpha != 1.0F))
        {
          localObject2 = null;
          if (localDrawable != this.currentImage) {
            break label296;
          }
          if (this.staticThumb == null) {
            break label276;
          }
          localObject1 = this.staticThumb;
          if (localObject1 != null) {
            drawDrawable(paramCanvas, (Drawable)localObject1, (int)(this.overrideAlpha * 255.0F), this.bitmapShaderThumb);
          }
        }
        j = (int)(this.overrideAlpha * this.currentAlpha * 255.0F);
        if (i == 0) {
          break label329;
        }
        localObject1 = this.bitmapShaderThumb;
        drawDrawable(paramCanvas, localDrawable, j, (BitmapShader)localObject1);
        continue;
        localObject1 = localObject2;
      }
      catch (Exception paramCanvas)
      {
        FileLog.e("tmessages", paramCanvas);
        return false;
      }
      label276:
      if (this.currentThumb != null)
      {
        localObject1 = this.currentThumb;
        continue;
        label296:
        localObject1 = localObject2;
        if (localDrawable == this.currentThumb)
        {
          localObject1 = localObject2;
          if (this.staticThumb != null)
          {
            localObject1 = this.staticThumb;
            continue;
            label329:
            localObject1 = this.bitmapShader;
            continue;
            label338:
            j = (int)(this.overrideAlpha * 255.0F);
            if (i != 0) {}
            for (localObject1 = this.bitmapShaderThumb;; localObject1 = this.bitmapShader)
            {
              drawDrawable(paramCanvas, localDrawable, j, (BitmapShader)localObject1);
              break;
            }
            label380:
            if (this.staticThumb != null)
            {
              drawDrawable(paramCanvas, this.staticThumb, 255, null);
              checkAlphaAnimation(bool);
              return true;
            }
            checkAlphaAnimation(bool);
            continue;
            label417:
            bool = false;
            continue;
            label423:
            bool = false;
          }
        }
      }
    }
  }
  
  public int getAnimatedOrientation()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      return ((AnimatedFileDrawable)this.currentImage).getOrientation();
    }
    if ((this.staticThumb instanceof AnimatedFileDrawable)) {
      return ((AnimatedFileDrawable)this.staticThumb).getOrientation();
    }
    return 0;
  }
  
  public AnimatedFileDrawable getAnimation()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      return (AnimatedFileDrawable)this.currentImage;
    }
    return null;
  }
  
  public Bitmap getBitmap()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable)) {
      return ((AnimatedFileDrawable)this.currentImage).getAnimatedBitmap();
    }
    if ((this.staticThumb instanceof AnimatedFileDrawable)) {
      return ((AnimatedFileDrawable)this.staticThumb).getAnimatedBitmap();
    }
    if ((this.currentImage instanceof BitmapDrawable)) {
      return ((BitmapDrawable)this.currentImage).getBitmap();
    }
    if ((this.currentThumb instanceof BitmapDrawable)) {
      return ((BitmapDrawable)this.currentThumb).getBitmap();
    }
    if ((this.staticThumb instanceof BitmapDrawable)) {
      return ((BitmapDrawable)this.staticThumb).getBitmap();
    }
    return null;
  }
  
  public int getBitmapHeight()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable))
    {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
        return this.currentImage.getIntrinsicHeight();
      }
      return this.currentImage.getIntrinsicWidth();
    }
    if ((this.staticThumb instanceof AnimatedFileDrawable))
    {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
        return this.staticThumb.getIntrinsicHeight();
      }
      return this.staticThumb.getIntrinsicWidth();
    }
    Bitmap localBitmap = getBitmap();
    if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
      return localBitmap.getHeight();
    }
    return localBitmap.getWidth();
  }
  
  public int getBitmapWidth()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable))
    {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
        return this.currentImage.getIntrinsicWidth();
      }
      return this.currentImage.getIntrinsicHeight();
    }
    if ((this.staticThumb instanceof AnimatedFileDrawable))
    {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
        return this.staticThumb.getIntrinsicWidth();
      }
      return this.staticThumb.getIntrinsicHeight();
    }
    Bitmap localBitmap = getBitmap();
    if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180)) {
      return localBitmap.getWidth();
    }
    return localBitmap.getHeight();
  }
  
  public boolean getCacheOnly()
  {
    return this.currentCacheOnly;
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
  
  public MessageObject getParentMessageObject()
  {
    return this.parentMessageObject;
  }
  
  public boolean getPressed()
  {
    return this.isPressed;
  }
  
  public int getRoundRadius()
  {
    return this.roundRadius;
  }
  
  public int getSize()
  {
    return this.currentSize;
  }
  
  protected Integer getTag(boolean paramBoolean)
  {
    if (paramBoolean) {
      return this.thumbTag;
    }
    return this.tag;
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
  
  public boolean hasBitmapImage()
  {
    return (this.currentImage != null) || (this.currentThumb != null) || (this.staticThumb != null);
  }
  
  public boolean hasImage()
  {
    return (this.currentImage != null) || (this.currentThumb != null) || (this.currentKey != null) || (this.currentHttpUrl != null) || (this.staticThumb != null);
  }
  
  public boolean isAllowStartAnimation()
  {
    return this.allowStartAnimation;
  }
  
  public boolean isAnimationRunning()
  {
    return ((this.currentImage instanceof AnimatedFileDrawable)) && (((AnimatedFileDrawable)this.currentImage).isRunning());
  }
  
  public boolean isForcePreview()
  {
    return this.forcePreview;
  }
  
  public boolean isInsideImage(float paramFloat1, float paramFloat2)
  {
    return (paramFloat1 >= this.imageX) && (paramFloat1 <= this.imageX + this.imageW) && (paramFloat2 >= this.imageY) && (paramFloat2 <= this.imageY + this.imageH);
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
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
    if ((this.setImageBackup != null) && ((this.setImageBackup.fileLocation != null) || (this.setImageBackup.httpUrl != null) || (this.setImageBackup.thumbLocation != null) || (this.setImageBackup.thumb != null)))
    {
      setImage(this.setImageBackup.fileLocation, this.setImageBackup.httpUrl, this.setImageBackup.filter, this.setImageBackup.thumb, this.setImageBackup.thumbLocation, this.setImageBackup.thumbFilter, this.setImageBackup.size, this.setImageBackup.ext, this.setImageBackup.cacheOnly);
      return true;
    }
    return false;
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
      this.setImageBackup.cacheOnly = this.currentCacheOnly;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
    clearImage();
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
    this.crossfadeAlpha = paramByte;
  }
  
  public void setDelegate(ImageReceiverDelegate paramImageReceiverDelegate)
  {
    this.delegate = paramImageReceiverDelegate;
  }
  
  public void setForcePreview(boolean paramBoolean)
  {
    this.forcePreview = paramBoolean;
  }
  
  public void setImage(String paramString1, String paramString2, Drawable paramDrawable, String paramString3, int paramInt)
  {
    setImage(null, paramString1, paramString2, paramDrawable, null, null, paramInt, paramString3, true);
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, Drawable paramDrawable, int paramInt, String paramString2, boolean paramBoolean)
  {
    setImage(paramTLObject, null, paramString1, paramDrawable, null, null, paramInt, paramString2, paramBoolean);
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, Drawable paramDrawable, String paramString2, boolean paramBoolean)
  {
    setImage(paramTLObject, null, paramString1, paramDrawable, null, null, 0, paramString2, paramBoolean);
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, String paramString2, Drawable paramDrawable, TLRPC.FileLocation paramFileLocation, String paramString3, int paramInt, String paramString4, boolean paramBoolean)
  {
    if (this.setImageBackup != null)
    {
      this.setImageBackup.fileLocation = null;
      this.setImageBackup.httpUrl = null;
      this.setImageBackup.thumbLocation = null;
      this.setImageBackup.thumb = null;
    }
    if (((paramTLObject == null) && (paramString1 == null) && (paramFileLocation == null)) || ((paramTLObject != null) && (!(paramTLObject instanceof TLRPC.TL_fileLocation)) && (!(paramTLObject instanceof TLRPC.TL_fileEncryptedLocation)) && (!(paramTLObject instanceof TLRPC.TL_document)) && (!(paramTLObject instanceof TLRPC.TL_documentEncrypted))))
    {
      recycleBitmap(null, false);
      recycleBitmap(null, true);
      this.currentKey = null;
      this.currentExt = paramString4;
      this.currentThumbKey = null;
      this.currentThumbFilter = null;
      this.currentImageLocation = null;
      this.currentHttpUrl = null;
      this.currentFilter = null;
      this.currentCacheOnly = false;
      this.staticThumb = paramDrawable;
      this.currentAlpha = 1.0F;
      this.currentThumbLocation = null;
      this.currentSize = 0;
      this.currentImage = null;
      this.bitmapShader = null;
      this.bitmapShaderThumb = null;
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
          break label296;
        }
        paramBoolean = true;
        label238:
        if (this.currentImage != null) {
          break label302;
        }
        bool1 = true;
        label248:
        paramTLObject.didSetImage(this, paramBoolean, bool1);
      }
    }
    label296:
    label302:
    TLRPC.FileLocation localFileLocation;
    Object localObject1;
    label380:
    Object localObject2;
    label484:
    boolean bool2;
    label494:
    do
    {
      return;
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break;
      paramBoolean = false;
      break label238;
      bool1 = false;
      break label248;
      localFileLocation = paramFileLocation;
      if (!(paramFileLocation instanceof TLRPC.TL_fileLocation)) {
        localFileLocation = null;
      }
      paramFileLocation = null;
      if (paramTLObject == null) {
        break label894;
      }
      if (!(paramTLObject instanceof TLRPC.FileLocation)) {
        break label772;
      }
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
      if ((this.currentKey == null) || (localObject2 == null) || (!this.currentKey.equals(localObject2))) {
        break label519;
      }
      if (this.delegate != null)
      {
        paramTLObject = this.delegate;
        if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null)) {
          break label913;
        }
        bool1 = true;
        if (this.currentImage != null) {
          break label919;
        }
        bool2 = true;
        paramTLObject.didSetImage(this, bool1, bool2);
      }
    } while ((!this.canceledLoading) && (!this.forcePreview));
    label519:
    paramTLObject = null;
    if (localFileLocation != null)
    {
      paramFileLocation = localFileLocation.volume_id + "_" + localFileLocation.local_id;
      paramTLObject = paramFileLocation;
      if (paramString3 != null) {
        paramTLObject = paramFileLocation + "@" + paramString3;
      }
    }
    recycleBitmap((String)localObject2, false);
    recycleBitmap(paramTLObject, true);
    this.currentThumbKey = paramTLObject;
    this.currentKey = ((String)localObject2);
    this.currentExt = paramString4;
    this.currentImageLocation = ((TLObject)localObject1);
    this.currentHttpUrl = paramString1;
    this.currentFilter = paramString2;
    this.currentThumbFilter = paramString3;
    this.currentSize = paramInt;
    this.currentCacheOnly = paramBoolean;
    this.currentThumbLocation = localFileLocation;
    this.staticThumb = paramDrawable;
    this.bitmapShader = null;
    this.bitmapShaderThumb = null;
    this.currentAlpha = 1.0F;
    if (this.delegate != null)
    {
      paramTLObject = this.delegate;
      if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null)) {
        break label925;
      }
      paramBoolean = true;
      label722:
      if (this.currentImage != null) {
        break label931;
      }
    }
    label772:
    label894:
    label913:
    label919:
    label925:
    label931:
    for (boolean bool1 = true;; bool1 = false)
    {
      paramTLObject.didSetImage(this, paramBoolean, bool1);
      ImageLoader.getInstance().loadImageForImageReceiver(this);
      if (this.parentView == null) {
        break;
      }
      if (!this.invalidateAll) {
        break label937;
      }
      this.parentView.invalidate();
      return;
      localObject1 = (TLRPC.Document)paramTLObject;
      if (((TLRPC.Document)localObject1).dc_id != 0)
      {
        if (((TLRPC.Document)localObject1).version == 0)
        {
          paramFileLocation = ((TLRPC.Document)localObject1).dc_id + "_" + ((TLRPC.Document)localObject1).id;
          localObject1 = paramTLObject;
          break label380;
        }
        paramFileLocation = ((TLRPC.Document)localObject1).dc_id + "_" + ((TLRPC.Document)localObject1).id + "_" + ((TLRPC.Document)localObject1).version;
        localObject1 = paramTLObject;
        break label380;
      }
      localObject1 = null;
      break label380;
      localObject1 = paramTLObject;
      if (paramString1 == null) {
        break label380;
      }
      paramFileLocation = Utilities.MD5(paramString1);
      localObject1 = paramTLObject;
      break label380;
      bool1 = false;
      break label484;
      bool2 = false;
      break label494;
      paramBoolean = false;
      break label722;
    }
    label937:
    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, TLRPC.FileLocation paramFileLocation, String paramString2, int paramInt, String paramString3, boolean paramBoolean)
  {
    setImage(paramTLObject, null, paramString1, null, paramFileLocation, paramString2, paramInt, paramString3, paramBoolean);
  }
  
  public void setImage(TLObject paramTLObject, String paramString1, TLRPC.FileLocation paramFileLocation, String paramString2, String paramString3, boolean paramBoolean)
  {
    setImage(paramTLObject, null, paramString1, null, paramFileLocation, paramString2, 0, paramString3, paramBoolean);
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
    recycleBitmap(null, false);
    recycleBitmap(null, true);
    this.staticThumb = paramDrawable;
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
    this.currentCacheOnly = false;
    this.bitmapShader = null;
    this.bitmapShaderThumb = null;
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
      if (this.invalidateAll) {
        this.parentView.invalidate();
      }
    }
    else {
      return;
    }
    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
  }
  
  protected boolean setImageBitmapByKey(BitmapDrawable paramBitmapDrawable, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    if ((paramBitmapDrawable == null) || (paramString == null)) {}
    do
    {
      return false;
      if (paramBoolean1) {
        break;
      }
    } while ((this.currentKey == null) || (!paramString.equals(this.currentKey)));
    if (!(paramBitmapDrawable instanceof AnimatedFileDrawable)) {
      ImageLoader.getInstance().incrementUseCount(this.currentKey);
    }
    this.currentImage = paramBitmapDrawable;
    if ((this.roundRadius != 0) && ((paramBitmapDrawable instanceof BitmapDrawable))) {
      if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
      {
        ((AnimatedFileDrawable)paramBitmapDrawable).setRoundRadius(this.roundRadius);
        label89:
        if ((paramBoolean2) || (this.forcePreview)) {
          break label307;
        }
        if (((this.currentThumb == null) && (this.staticThumb == null)) || (this.currentAlpha == 1.0F))
        {
          this.currentAlpha = 0.0F;
          this.lastUpdateAlphaTime = System.currentTimeMillis();
          if ((this.currentThumb == null) && (this.staticThumb == null)) {
            break label302;
          }
          paramBoolean1 = true;
          label152:
          this.crossfadeWithThumb = paramBoolean1;
        }
        label157:
        if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
        {
          paramBitmapDrawable = (AnimatedFileDrawable)paramBitmapDrawable;
          paramBitmapDrawable.setParentView(this.parentView);
          if (this.allowStartAnimation) {
            paramBitmapDrawable.start();
          }
        }
        if (this.parentView != null)
        {
          if (!this.invalidateAll) {
            break label315;
          }
          this.parentView.invalidate();
        }
        label209:
        if (this.delegate != null)
        {
          paramBitmapDrawable = this.delegate;
          if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null)) {
            break label628;
          }
        }
      }
    }
    label302:
    label307:
    label315:
    label467:
    label508:
    label579:
    label584:
    label590:
    label592:
    label628:
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      paramBoolean2 = bool;
      if (this.currentImage == null) {
        paramBoolean2 = true;
      }
      paramBitmapDrawable.didSetImage(this, paramBoolean1, paramBoolean2);
      return true;
      this.bitmapShader = new BitmapShader(paramBitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      break label89;
      this.bitmapShader = null;
      break label89;
      paramBoolean1 = false;
      break label152;
      this.currentAlpha = 1.0F;
      break label157;
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break label209;
      if ((this.currentThumb != null) || ((this.currentImage != null) && ((!(this.currentImage instanceof AnimatedFileDrawable)) || (((AnimatedFileDrawable)this.currentImage).hasBitmap())) && (!this.forcePreview))) {
        break label209;
      }
      if ((this.currentThumbKey == null) || (!paramString.equals(this.currentThumbKey))) {
        break;
      }
      ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
      this.currentThumb = paramBitmapDrawable;
      if ((this.roundRadius != 0) && (this.currentImage == null) && ((paramBitmapDrawable instanceof BitmapDrawable))) {
        if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
        {
          ((AnimatedFileDrawable)paramBitmapDrawable).setRoundRadius(this.roundRadius);
          if ((paramBoolean2) || (this.crossfadeAlpha == 2)) {
            break label584;
          }
          this.currentAlpha = 0.0F;
          this.lastUpdateAlphaTime = System.currentTimeMillis();
          if ((this.staticThumb == null) || (this.currentKey != null)) {
            break label579;
          }
          paramBoolean1 = true;
          this.crossfadeWithThumb = paramBoolean1;
        }
      }
      for (;;)
      {
        if (((this.staticThumb instanceof BitmapDrawable)) || (this.parentView == null)) {
          break label590;
        }
        if (!this.invalidateAll) {
          break label592;
        }
        this.parentView.invalidate();
        break;
        this.bitmapShaderThumb = new BitmapShader(paramBitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        break label467;
        this.bitmapShaderThumb = null;
        break label467;
        paramBoolean1 = false;
        break label508;
        this.currentAlpha = 1.0F;
      }
      break label209;
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break label209;
    }
  }
  
  public void setImageCoords(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.imageX = paramInt1;
    this.imageY = paramInt2;
    this.imageW = paramInt3;
    this.imageH = paramInt4;
  }
  
  public void setInvalidateAll(boolean paramBoolean)
  {
    this.invalidateAll = paramBoolean;
  }
  
  public void setNeedsQualityThumb(boolean paramBoolean)
  {
    this.needsQualityThumb = paramBoolean;
    if (this.needsQualityThumb)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageThumbGenerated);
      return;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
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
  
  public void setPressed(boolean paramBoolean)
  {
    this.isPressed = paramBoolean;
  }
  
  public void setRoundRadius(int paramInt)
  {
    this.roundRadius = paramInt;
  }
  
  public void setShouldGenerateQualityThumb(boolean paramBoolean)
  {
    this.shouldGenerateQualityThumb = paramBoolean;
  }
  
  protected void setTag(Integer paramInteger, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.thumbTag = paramInteger;
      return;
    }
    this.tag = paramInteger;
  }
  
  public void setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.isVisible == paramBoolean1) {}
    do
    {
      return;
      this.isVisible = paramBoolean1;
    } while ((!paramBoolean2) || (this.parentView == null));
    if (this.invalidateAll)
    {
      this.parentView.invalidate();
      return;
    }
    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
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
  
  public static abstract interface ImageReceiverDelegate
  {
    public abstract void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2);
  }
  
  private class SetImageBackup
  {
    public boolean cacheOnly;
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