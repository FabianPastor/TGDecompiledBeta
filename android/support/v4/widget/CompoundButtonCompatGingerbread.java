package android.support.v4.widget;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.CompoundButton;
import java.lang.reflect.Field;

@TargetApi(9)
@RequiresApi(9)
class CompoundButtonCompatGingerbread
{
  private static final String TAG = "CompoundButtonCompatGingerbread";
  private static Field sButtonDrawableField;
  private static boolean sButtonDrawableFieldFetched;
  
  static Drawable getButtonDrawable(CompoundButton paramCompoundButton)
  {
    if (!sButtonDrawableFieldFetched) {}
    try
    {
      sButtonDrawableField = CompoundButton.class.getDeclaredField("mButtonDrawable");
      sButtonDrawableField.setAccessible(true);
      sButtonDrawableFieldFetched = true;
      if (sButtonDrawableField == null) {}
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;)
      {
        try
        {
          paramCompoundButton = (Drawable)sButtonDrawableField.get(paramCompoundButton);
          return paramCompoundButton;
        }
        catch (IllegalAccessException paramCompoundButton)
        {
          Log.i("CompoundButtonCompatGingerbread", "Failed to get button drawable via reflection", paramCompoundButton);
          sButtonDrawableField = null;
        }
        localNoSuchFieldException = localNoSuchFieldException;
        Log.i("CompoundButtonCompatGingerbread", "Failed to retrieve mButtonDrawable field", localNoSuchFieldException);
      }
    }
    return null;
  }
  
  static ColorStateList getButtonTintList(CompoundButton paramCompoundButton)
  {
    if ((paramCompoundButton instanceof TintableCompoundButton)) {
      return ((TintableCompoundButton)paramCompoundButton).getSupportButtonTintList();
    }
    return null;
  }
  
  static PorterDuff.Mode getButtonTintMode(CompoundButton paramCompoundButton)
  {
    if ((paramCompoundButton instanceof TintableCompoundButton)) {
      return ((TintableCompoundButton)paramCompoundButton).getSupportButtonTintMode();
    }
    return null;
  }
  
  static void setButtonTintList(CompoundButton paramCompoundButton, ColorStateList paramColorStateList)
  {
    if ((paramCompoundButton instanceof TintableCompoundButton)) {
      ((TintableCompoundButton)paramCompoundButton).setSupportButtonTintList(paramColorStateList);
    }
  }
  
  static void setButtonTintMode(CompoundButton paramCompoundButton, PorterDuff.Mode paramMode)
  {
    if ((paramCompoundButton instanceof TintableCompoundButton)) {
      ((TintableCompoundButton)paramCompoundButton).setSupportButtonTintMode(paramMode);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/widget/CompoundButtonCompatGingerbread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */