package android.support.v4.app;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;

@TargetApi(18)
@RequiresApi(18)
class ActionBarDrawerToggleJellybeanMR2
{
  private static final String TAG = "ActionBarDrawerToggleImplJellybeanMR2";
  private static final int[] THEME_ATTRS = { 16843531 };
  
  public static Drawable getThemeUpIndicator(Activity paramActivity)
  {
    Object localObject = paramActivity.getActionBar();
    if (localObject != null) {
      paramActivity = ((ActionBar)localObject).getThemedContext();
    }
    for (;;)
    {
      paramActivity = paramActivity.obtainStyledAttributes(null, THEME_ATTRS, 16843470, 0);
      localObject = paramActivity.getDrawable(0);
      paramActivity.recycle();
      return (Drawable)localObject;
    }
  }
  
  public static Object setActionBarDescription(Object paramObject, Activity paramActivity, int paramInt)
  {
    paramActivity = paramActivity.getActionBar();
    if (paramActivity != null) {
      paramActivity.setHomeActionContentDescription(paramInt);
    }
    return paramObject;
  }
  
  public static Object setActionBarUpIndicator(Object paramObject, Activity paramActivity, Drawable paramDrawable, int paramInt)
  {
    paramActivity = paramActivity.getActionBar();
    if (paramActivity != null)
    {
      paramActivity.setHomeAsUpIndicator(paramDrawable);
      paramActivity.setHomeActionContentDescription(paramInt);
    }
    return paramObject;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/ActionBarDrawerToggleJellybeanMR2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */