package com.google.android.gms.internal;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.SystemClock;

public final class zzbfg
  extends Drawable
  implements Drawable.Callback
{
  private int mFrom;
  private int zzaGA;
  private boolean zzaGj = true;
  private int zzaGo = 0;
  private int zzaGp;
  private int zzaGq = 255;
  private int zzaGr;
  private int zzaGs = 0;
  private boolean zzaGt;
  private zzbfk zzaGu;
  private Drawable zzaGv;
  private Drawable zzaGw;
  private boolean zzaGx;
  private boolean zzaGy;
  private boolean zzaGz;
  private long zzagZ;
  
  public zzbfg(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    this(null);
    Object localObject = paramDrawable1;
    if (paramDrawable1 == null) {
      localObject = zzbfi.zzqX();
    }
    this.zzaGv = ((Drawable)localObject);
    ((Drawable)localObject).setCallback(this);
    paramDrawable1 = this.zzaGu;
    paramDrawable1.zzaGD |= ((Drawable)localObject).getChangingConfigurations();
    paramDrawable1 = paramDrawable2;
    if (paramDrawable2 == null) {
      paramDrawable1 = zzbfi.zzqX();
    }
    this.zzaGw = paramDrawable1;
    paramDrawable1.setCallback(this);
    paramDrawable2 = this.zzaGu;
    paramDrawable2.zzaGD |= paramDrawable1.getChangingConfigurations();
  }
  
  zzbfg(zzbfk paramzzbfk)
  {
    this.zzaGu = new zzbfk(paramzzbfk);
  }
  
  private final boolean canConstantState()
  {
    if (!this.zzaGx) {
      if ((this.zzaGv.getConstantState() == null) || (this.zzaGw.getConstantState() == null)) {
        break label44;
      }
    }
    label44:
    for (boolean bool = true;; bool = false)
    {
      this.zzaGy = bool;
      this.zzaGx = true;
      return this.zzaGy;
    }
  }
  
  public final void draw(Canvas paramCanvas)
  {
    int j = 1;
    int i = 1;
    int k = 0;
    switch (this.zzaGo)
    {
    }
    boolean bool;
    Drawable localDrawable1;
    Drawable localDrawable2;
    do
    {
      for (;;)
      {
        j = this.zzaGs;
        bool = this.zzaGj;
        localDrawable1 = this.zzaGv;
        localDrawable2 = this.zzaGw;
        if (i == 0) {
          break;
        }
        if ((!bool) || (j == 0)) {
          localDrawable1.draw(paramCanvas);
        }
        if (j == this.zzaGq)
        {
          localDrawable2.setAlpha(this.zzaGq);
          localDrawable2.draw(paramCanvas);
        }
        return;
        this.zzagZ = SystemClock.uptimeMillis();
        this.zzaGo = 2;
        i = k;
      }
    } while (this.zzagZ < 0L);
    float f = (float)(SystemClock.uptimeMillis() - this.zzagZ) / this.zzaGr;
    if (f >= 1.0F) {}
    for (i = j;; i = 0)
    {
      if (i != 0) {
        this.zzaGo = 0;
      }
      this.zzaGs = ((int)(Math.min(f, 1.0F) * this.zzaGp + 0.0F));
      break;
    }
    if (bool) {
      localDrawable1.setAlpha(this.zzaGq - j);
    }
    localDrawable1.draw(paramCanvas);
    if (bool) {
      localDrawable1.setAlpha(this.zzaGq);
    }
    if (j > 0)
    {
      localDrawable2.setAlpha(j);
      localDrawable2.draw(paramCanvas);
      localDrawable2.setAlpha(this.zzaGq);
    }
    invalidateSelf();
  }
  
  public final int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.zzaGu.mChangingConfigurations | this.zzaGu.zzaGD;
  }
  
  public final Drawable.ConstantState getConstantState()
  {
    if (canConstantState())
    {
      this.zzaGu.mChangingConfigurations = getChangingConfigurations();
      return this.zzaGu;
    }
    return null;
  }
  
  public final int getIntrinsicHeight()
  {
    return Math.max(this.zzaGv.getIntrinsicHeight(), this.zzaGw.getIntrinsicHeight());
  }
  
  public final int getIntrinsicWidth()
  {
    return Math.max(this.zzaGv.getIntrinsicWidth(), this.zzaGw.getIntrinsicWidth());
  }
  
  public final int getOpacity()
  {
    if (!this.zzaGz)
    {
      this.zzaGA = Drawable.resolveOpacity(this.zzaGv.getOpacity(), this.zzaGw.getOpacity());
      this.zzaGz = true;
    }
    return this.zzaGA;
  }
  
  public final void invalidateDrawable(Drawable paramDrawable)
  {
    paramDrawable = getCallback();
    if (paramDrawable != null) {
      paramDrawable.invalidateDrawable(this);
    }
  }
  
  public final Drawable mutate()
  {
    if ((!this.zzaGt) && (super.mutate() == this))
    {
      if (!canConstantState()) {
        throw new IllegalStateException("One or more children of this LayerDrawable does not have constant state; this drawable cannot be mutated.");
      }
      this.zzaGv.mutate();
      this.zzaGw.mutate();
      this.zzaGt = true;
    }
    return this;
  }
  
  protected final void onBoundsChange(Rect paramRect)
  {
    this.zzaGv.setBounds(paramRect);
    this.zzaGw.setBounds(paramRect);
  }
  
  public final void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    paramDrawable = getCallback();
    if (paramDrawable != null) {
      paramDrawable.scheduleDrawable(this, paramRunnable, paramLong);
    }
  }
  
  public final void setAlpha(int paramInt)
  {
    if (this.zzaGs == this.zzaGq) {
      this.zzaGs = paramInt;
    }
    this.zzaGq = paramInt;
    invalidateSelf();
  }
  
  public final void setColorFilter(ColorFilter paramColorFilter)
  {
    this.zzaGv.setColorFilter(paramColorFilter);
    this.zzaGw.setColorFilter(paramColorFilter);
  }
  
  public final void startTransition(int paramInt)
  {
    this.mFrom = 0;
    this.zzaGp = this.zzaGq;
    this.zzaGs = 0;
    this.zzaGr = 250;
    this.zzaGo = 1;
    invalidateSelf();
  }
  
  public final void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    paramDrawable = getCallback();
    if (paramDrawable != null) {
      paramDrawable.unscheduleDrawable(this, paramRunnable);
    }
  }
  
  public final Drawable zzqW()
  {
    return this.zzaGw;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbfg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */