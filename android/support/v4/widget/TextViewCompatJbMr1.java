package android.support.v4.widget;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

class TextViewCompatJbMr1
{
  static Drawable[] getCompoundDrawablesRelative(@NonNull TextView paramTextView)
  {
    return paramTextView.getCompoundDrawablesRelative();
  }
  
  public static void setCompoundDrawablesRelative(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
  {
    int i = 1;
    Drawable localDrawable;
    if (paramTextView.getLayoutDirection() == 1)
    {
      if (i == 0) {
        break label41;
      }
      localDrawable = paramDrawable3;
      label19:
      if (i == 0) {
        break label47;
      }
    }
    for (;;)
    {
      paramTextView.setCompoundDrawables(localDrawable, paramDrawable2, paramDrawable1, paramDrawable4);
      return;
      i = 0;
      break;
      label41:
      localDrawable = paramDrawable1;
      break label19;
      label47:
      paramDrawable1 = paramDrawable3;
    }
  }
  
  public static void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 1;
    int j;
    if (paramTextView.getLayoutDirection() == 1)
    {
      if (i == 0) {
        break label41;
      }
      j = paramInt3;
      label19:
      if (i == 0) {
        break label47;
      }
    }
    for (;;)
    {
      paramTextView.setCompoundDrawablesWithIntrinsicBounds(j, paramInt2, paramInt1, paramInt4);
      return;
      i = 0;
      break;
      label41:
      j = paramInt1;
      break label19;
      label47:
      paramInt1 = paramInt3;
    }
  }
  
  public static void setCompoundDrawablesRelativeWithIntrinsicBounds(@NonNull TextView paramTextView, @Nullable Drawable paramDrawable1, @Nullable Drawable paramDrawable2, @Nullable Drawable paramDrawable3, @Nullable Drawable paramDrawable4)
  {
    int i = 1;
    Drawable localDrawable;
    if (paramTextView.getLayoutDirection() == 1)
    {
      if (i == 0) {
        break label41;
      }
      localDrawable = paramDrawable3;
      label19:
      if (i == 0) {
        break label47;
      }
    }
    for (;;)
    {
      paramTextView.setCompoundDrawablesWithIntrinsicBounds(localDrawable, paramDrawable2, paramDrawable1, paramDrawable4);
      return;
      i = 0;
      break;
      label41:
      localDrawable = paramDrawable1;
      break label19;
      label47:
      paramDrawable1 = paramDrawable3;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/widget/TextViewCompatJbMr1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */