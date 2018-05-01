package android.support.v4.app;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;

public class ActivityOptionsCompat
{
  private static ActivityOptionsCompat createImpl(ActivityOptions paramActivityOptions)
  {
    if (Build.VERSION.SDK_INT >= 24) {
      paramActivityOptions = new ActivityOptionsCompatApi24Impl(paramActivityOptions);
    }
    for (;;)
    {
      return paramActivityOptions;
      if (Build.VERSION.SDK_INT >= 23) {
        paramActivityOptions = new ActivityOptionsCompatApi23Impl(paramActivityOptions);
      } else {
        paramActivityOptions = new ActivityOptionsCompatApi16Impl(paramActivityOptions);
      }
    }
  }
  
  public static ActivityOptionsCompat makeCustomAnimation(Context paramContext, int paramInt1, int paramInt2)
  {
    if (Build.VERSION.SDK_INT >= 16) {}
    for (paramContext = createImpl(ActivityOptions.makeCustomAnimation(paramContext, paramInt1, paramInt2));; paramContext = new ActivityOptionsCompat()) {
      return paramContext;
    }
  }
  
  public Bundle toBundle()
  {
    return null;
  }
  
  private static class ActivityOptionsCompatApi16Impl
    extends ActivityOptionsCompat
  {
    protected final ActivityOptions mActivityOptions;
    
    ActivityOptionsCompatApi16Impl(ActivityOptions paramActivityOptions)
    {
      this.mActivityOptions = paramActivityOptions;
    }
    
    public Bundle toBundle()
    {
      return this.mActivityOptions.toBundle();
    }
  }
  
  private static class ActivityOptionsCompatApi23Impl
    extends ActivityOptionsCompat.ActivityOptionsCompatApi16Impl
  {
    ActivityOptionsCompatApi23Impl(ActivityOptions paramActivityOptions)
    {
      super();
    }
  }
  
  private static class ActivityOptionsCompatApi24Impl
    extends ActivityOptionsCompat.ActivityOptionsCompatApi23Impl
  {
    ActivityOptionsCompatApi24Impl(ActivityOptions paramActivityOptions)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/ActivityOptionsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */