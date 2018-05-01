package com.google.android.gms.common;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import com.google.android.gms.R.styleable;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.zzbv;
import com.google.android.gms.common.internal.zzbw;
import com.google.android.gms.dynamic.zzq;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class SignInButton
  extends FrameLayout
  implements View.OnClickListener
{
  public static final int COLOR_AUTO = 2;
  public static final int COLOR_DARK = 0;
  public static final int COLOR_LIGHT = 1;
  public static final int SIZE_ICON_ONLY = 2;
  public static final int SIZE_STANDARD = 0;
  public static final int SIZE_WIDE = 1;
  private int mColor;
  private int mSize;
  private View zzaAv;
  private View.OnClickListener zzaAw = null;
  
  public SignInButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public SignInButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public SignInButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    paramContext = paramContext.getTheme().obtainStyledAttributes(paramAttributeSet, R.styleable.SignInButton, 0, 0);
    try
    {
      this.mSize = paramContext.getInt(R.styleable.SignInButton_buttonSize, 0);
      this.mColor = paramContext.getInt(R.styleable.SignInButton_colorScheme, 2);
      paramContext.recycle();
      setStyle(this.mSize, this.mColor);
      return;
    }
    finally
    {
      paramContext.recycle();
    }
  }
  
  public final void onClick(View paramView)
  {
    if ((this.zzaAw != null) && (paramView == this.zzaAv)) {
      this.zzaAw.onClick(this);
    }
  }
  
  public final void setColorScheme(int paramInt)
  {
    setStyle(this.mSize, paramInt);
  }
  
  public final void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    this.zzaAv.setEnabled(paramBoolean);
  }
  
  public final void setOnClickListener(View.OnClickListener paramOnClickListener)
  {
    this.zzaAw = paramOnClickListener;
    if (this.zzaAv != null) {
      this.zzaAv.setOnClickListener(this);
    }
  }
  
  @Deprecated
  public final void setScopes(Scope[] paramArrayOfScope)
  {
    setStyle(this.mSize, this.mColor);
  }
  
  public final void setSize(int paramInt)
  {
    setStyle(paramInt, this.mColor);
  }
  
  public final void setStyle(int paramInt1, int paramInt2)
  {
    this.mSize = paramInt1;
    this.mColor = paramInt2;
    Context localContext = getContext();
    if (this.zzaAv != null) {
      removeView(this.zzaAv);
    }
    try
    {
      this.zzaAv = zzbv.zzc(localContext, this.mSize, this.mColor);
      addView(this.zzaAv);
      this.zzaAv.setEnabled(isEnabled());
      this.zzaAv.setOnClickListener(this);
      return;
    }
    catch (zzq localzzq)
    {
      for (;;)
      {
        Log.w("SignInButton", "Sign in button not found, using placeholder instead");
        paramInt1 = this.mSize;
        paramInt2 = this.mColor;
        zzbw localzzbw = new zzbw(localContext);
        localzzbw.zza(localContext.getResources(), paramInt1, paramInt2);
        this.zzaAv = localzzbw;
      }
    }
  }
  
  @Deprecated
  public final void setStyle(int paramInt1, int paramInt2, Scope[] paramArrayOfScope)
  {
    setStyle(paramInt1, paramInt2);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ButtonSize {}
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ColorScheme {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/SignInButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */