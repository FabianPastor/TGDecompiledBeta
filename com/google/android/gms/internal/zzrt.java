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

public final class zzrt
  extends Drawable
  implements Drawable.Callback
{
  private int AD = 0;
  private int AE;
  private int AF = 255;
  private int AG;
  private int AH = 0;
  private boolean AI;
  private zzb AJ;
  private Drawable AK;
  private Drawable AL;
  private boolean AM;
  private boolean AN;
  private boolean AO;
  private int AP;
  private boolean Ax = true;
  private long bZ;
  private int mFrom;
  
  public zzrt(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    this(null);
    Object localObject = paramDrawable1;
    if (paramDrawable1 == null) {
      localObject = zza.zzato();
    }
    this.AK = ((Drawable)localObject);
    ((Drawable)localObject).setCallback(this);
    paramDrawable1 = this.AJ;
    paramDrawable1.AS |= ((Drawable)localObject).getChangingConfigurations();
    paramDrawable1 = paramDrawable2;
    if (paramDrawable2 == null) {
      paramDrawable1 = zza.zzato();
    }
    this.AL = paramDrawable1;
    paramDrawable1.setCallback(this);
    paramDrawable2 = this.AJ;
    paramDrawable2.AS |= paramDrawable1.getChangingConfigurations();
  }
  
  zzrt(zzb paramzzb)
  {
    this.AJ = new zzb(paramzzb);
  }
  
  public boolean canConstantState()
  {
    if (!this.AM) {
      if ((this.AK.getConstantState() == null) || (this.AL.getConstantState() == null)) {
        break label44;
      }
    }
    label44:
    for (boolean bool = true;; bool = false)
    {
      this.AN = bool;
      this.AM = true;
      return this.AN;
    }
  }
  
  public void draw(Canvas paramCanvas)
  {
    int j = 1;
    int i = 1;
    int k = 0;
    switch (this.AD)
    {
    }
    boolean bool;
    Drawable localDrawable1;
    Drawable localDrawable2;
    do
    {
      for (;;)
      {
        j = this.AH;
        bool = this.Ax;
        localDrawable1 = this.AK;
        localDrawable2 = this.AL;
        if (i == 0) {
          break;
        }
        if ((!bool) || (j == 0)) {
          localDrawable1.draw(paramCanvas);
        }
        if (j == this.AF)
        {
          localDrawable2.setAlpha(this.AF);
          localDrawable2.draw(paramCanvas);
        }
        return;
        this.bZ = SystemClock.uptimeMillis();
        this.AD = 2;
        i = k;
      }
    } while (this.bZ < 0L);
    float f = (float)(SystemClock.uptimeMillis() - this.bZ) / this.AG;
    if (f >= 1.0F) {}
    for (i = j;; i = 0)
    {
      if (i != 0) {
        this.AD = 0;
      }
      this.AH = ((int)(Math.min(f, 1.0F) * (this.AE + 0) + 0.0F));
      break;
    }
    if (bool) {
      localDrawable1.setAlpha(this.AF - j);
    }
    localDrawable1.draw(paramCanvas);
    if (bool) {
      localDrawable1.setAlpha(this.AF);
    }
    if (j > 0)
    {
      localDrawable2.setAlpha(j);
      localDrawable2.draw(paramCanvas);
      localDrawable2.setAlpha(this.AF);
    }
    invalidateSelf();
  }
  
  public int getChangingConfigurations()
  {
    return super.getChangingConfigurations() | this.AJ.mChangingConfigurations | this.AJ.AS;
  }
  
  public Drawable.ConstantState getConstantState()
  {
    if (canConstantState())
    {
      this.AJ.mChangingConfigurations = getChangingConfigurations();
      return this.AJ;
    }
    return null;
  }
  
  public int getIntrinsicHeight()
  {
    return Math.max(this.AK.getIntrinsicHeight(), this.AL.getIntrinsicHeight());
  }
  
  public int getIntrinsicWidth()
  {
    return Math.max(this.AK.getIntrinsicWidth(), this.AL.getIntrinsicWidth());
  }
  
  public int getOpacity()
  {
    if (!this.AO)
    {
      this.AP = Drawable.resolveOpacity(this.AK.getOpacity(), this.AL.getOpacity());
      this.AO = true;
    }
    return this.AP;
  }
  
  @TargetApi(11)
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (zzs.zzaxk())
    {
      paramDrawable = getCallback();
      if (paramDrawable != null) {
        paramDrawable.invalidateDrawable(this);
      }
    }
  }
  
  public Drawable mutate()
  {
    if ((!this.AI) && (super.mutate() == this))
    {
      if (!canConstantState()) {
        throw new IllegalStateException("One or more children of this LayerDrawable does not have constant state; this drawable cannot be mutated.");
      }
      this.AK.mutate();
      this.AL.mutate();
      this.AI = true;
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    this.AK.setBounds(paramRect);
    this.AL.setBounds(paramRect);
  }
  
  @TargetApi(11)
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    if (zzs.zzaxk())
    {
      paramDrawable = getCallback();
      if (paramDrawable != null) {
        paramDrawable.scheduleDrawable(this, paramRunnable, paramLong);
      }
    }
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.AH == this.AF) {
      this.AH = paramInt;
    }
    this.AF = paramInt;
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.AK.setColorFilter(paramColorFilter);
    this.AL.setColorFilter(paramColorFilter);
  }
  
  public void startTransition(int paramInt)
  {
    this.mFrom = 0;
    this.AE = this.AF;
    this.AH = 0;
    this.AG = paramInt;
    this.AD = 1;
    invalidateSelf();
  }
  
  @TargetApi(11)
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    if (zzs.zzaxk())
    {
      paramDrawable = getCallback();
      if (paramDrawable != null) {
        paramDrawable.unscheduleDrawable(this, paramRunnable);
      }
    }
  }
  
  public Drawable zzatn()
  {
    return this.AL;
  }
  
  private static final class zza
    extends Drawable
  {
    private static final zza AQ = new zza();
    private static final zza AR = new zza(null);
    
    public void draw(Canvas paramCanvas) {}
    
    public Drawable.ConstantState getConstantState()
    {
      return AR;
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
        return zzrt.zza.zzato();
      }
    }
  }
  
  static final class zzb
    extends Drawable.ConstantState
  {
    int AS;
    int mChangingConfigurations;
    
    zzb(zzb paramzzb)
    {
      if (paramzzb != null)
      {
        this.mChangingConfigurations = paramzzb.mChangingConfigurations;
        this.AS = paramzzb.AS;
      }
    }
    
    public int getChangingConfigurations()
    {
      return this.mChangingConfigurations;
    }
    
    public Drawable newDrawable()
    {
      return new zzrt(this);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzrt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */