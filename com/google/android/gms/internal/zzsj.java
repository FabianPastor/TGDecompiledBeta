package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.SystemClock;
import com.google.android.gms.common.util.zzs;

public final class zzsj
  extends Drawable
  implements Drawable.Callback
{
  private boolean CH = true;
  private int CM = 0;
  private int CN;
  private int CO = 255;
  private int CP;
  private int CQ = 0;
  private boolean CR;
  private zzb CS;
  private Drawable CT;
  private Drawable CU;
  private boolean CV;
  private boolean CW;
  private boolean CX;
  private int CY;
  private long eg;
  private int mFrom;
  
  public zzsj(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    this(null);
    Object localObject = paramDrawable1;
    if (paramDrawable1 == null) {
      localObject = zza.zzaux();
    }
    this.CT = ((Drawable)localObject);
    ((Drawable)localObject).setCallback(this);
    paramDrawable1 = this.CS;
    paramDrawable1.Db |= ((Drawable)localObject).getChangingConfigurations();
    paramDrawable1 = paramDrawable2;
    if (paramDrawable2 == null) {
      paramDrawable1 = zza.zzaux();
    }
    this.CU = paramDrawable1;
    paramDrawable1.setCallback(this);
    paramDrawable2 = this.CS;
    paramDrawable2.Db |= paramDrawable1.getChangingConfigurations();
  }
  
  zzsj(zzb paramzzb)
  {
    this.CS = new zzb(paramzzb);
  }
  
  public boolean canConstantState()
  {
    if (!this.CV) {
      if ((this.CT.getConstantState() == null) || (this.CU.getConstantState() == null)) {
        break label44;
      }
    }
    label44:
    for (boolean bool = true;; bool = false)
    {
      this.CW = bool;
      this.CV = true;
      return this.CW;
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    int j = 1;
    int i = 1;
    int k = 0;
    switch (this.CM)
    {
    }
    boolean bool;
    Drawable localDrawable1;
    Drawable localDrawable2;
    do
    {
      for (;;)
      {
        j = this.CQ;
        bool = this.CH;
        localDrawable1 = this.CT;
        localDrawable2 = this.CU;
        if (i == 0) {
          break;
        }
        if ((!bool) || (j == 0)) {
          localDrawable1.draw(paramCanvas);
        }
        if (j == this.CO)
        {
          localDrawable2.setAlpha(this.CO);
          localDrawable2.draw(paramCanvas);
        }
        return;
        this.eg = SystemClock.uptimeMillis();
        this.CM = 2;
        i = k;
      }
    } while (this.eg < 0L);
    float f = (float)(SystemClock.uptimeMillis() - this.eg) / this.CP;
    if (f >= 1.0F) {}
    for (i = j;; i = 0)
    {
      if (i != 0) {
        this.CM = 0;
      }
      this.CQ = ((int)(Math.min(f, 1.0F) * (this.CN + 0) + 0.0F));
      break;
    }
    if (bool) {
      localDrawable1.setAlpha(this.CO - j);
    }
    localDrawable1.draw(paramCanvas);
    if (bool) {
      localDrawable1.setAlpha(this.CO);
    }
    if (j > 0)
    {
      localDrawable2.setAlpha(j);
      localDrawable2.draw(paramCanvas);
      localDrawable2.setAlpha(this.CO);
    }
    invalidateSelf();
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.CS.mChangingConfigurations | this.CS.Db;
  }
  
  public Drawable.ConstantState getConstantState()
  {
    if (canConstantState())
    {
      this.CS.mChangingConfigurations = getChangingConfigurations();
      return this.CS;
    }
    return null;
  }
  
  public int getIntrinsicHeight()
  {
    return Math.max(this.CT.getIntrinsicHeight(), this.CU.getIntrinsicHeight());
  }
  
  public int getIntrinsicWidth()
  {
    return Math.max(this.CT.getIntrinsicWidth(), this.CU.getIntrinsicWidth());
  }
  
  public int getOpacity()
  {
    if (!this.CX)
    {
      this.CY = Drawable.resolveOpacity(this.CT.getOpacity(), this.CU.getOpacity());
      this.CX = true;
    }
    return this.CY;
  }
  
  @TargetApi(11)
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (zzs.zzayn())
    {
      paramDrawable = getCallback();
      if (paramDrawable != null) {
        paramDrawable.invalidateDrawable(this);
      }
    }
  }
  
  public Drawable mutate()
  {
    if ((!this.CR) && (super.mutate() == this))
    {
      if (!canConstantState()) {
        throw new IllegalStateException("One or more children of this LayerDrawable does not have constant state; this drawable cannot be mutated.");
      }
      this.CT.mutate();
      this.CU.mutate();
      this.CR = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    this.CT.setBounds(paramRect);
    this.CU.setBounds(paramRect);
  }
  
  @TargetApi(11)
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    if (zzs.zzayn())
    {
      paramDrawable = getCallback();
      if (paramDrawable != null) {
        paramDrawable.scheduleDrawable(this, paramRunnable, paramLong);
      }
    }
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.CQ == this.CO) {
      this.CQ = paramInt;
    }
    this.CO = paramInt;
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.CT.setColorFilter(paramColorFilter);
    this.CU.setColorFilter(paramColorFilter);
  }
  
  public void startTransition(int paramInt)
  {
    this.mFrom = 0;
    this.CN = this.CO;
    this.CQ = 0;
    this.CP = paramInt;
    this.CM = 1;
    invalidateSelf();
  }
  
  @TargetApi(11)
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    if (zzs.zzayn())
    {
      paramDrawable = getCallback();
      if (paramDrawable != null) {
        paramDrawable.unscheduleDrawable(this, paramRunnable);
      }
    }
  }
  
  public Drawable zzauw()
  {
    return this.CU;
  }
  
  private static final class zza
    extends Drawable
  {
    private static final zza CZ = new zza();
    private static final zza Da = new zza(null);
    
    public void draw(Canvas paramCanvas) {}
    
    public Drawable.ConstantState getConstantState()
    {
      return Da;
    }
    
    public int getOpacity()
    {
      return -2;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
    
    private static final class zza
      extends Drawable.ConstantState
    {
      public int getChangingConfigurations()
      {
        return 0;
      }
      
      public Drawable newDrawable()
      {
        return zzsj.zza.zzaux();
      }
    }
  }
  
  static final class zzb
    extends Drawable.ConstantState
  {
    int Db;
    int mChangingConfigurations;
    
    zzb(zzb paramzzb)
    {
      if (paramzzb != null)
      {
        this.mChangingConfigurations = paramzzb.mChangingConfigurations;
        this.Db = paramzzb.Db;
      }
    }
    
    public int getChangingConfigurations()
    {
      return this.mChangingConfigurations;
    }
    
    public Drawable newDrawable()
    {
      return new zzsj(this);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzsj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */