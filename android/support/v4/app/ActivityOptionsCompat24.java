package android.support.v4.app;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Pair;
import android.view.View;

@TargetApi(24)
@RequiresApi(24)
class ActivityOptionsCompat24
{
  private final ActivityOptions mActivityOptions;
  
  private ActivityOptionsCompat24(ActivityOptions paramActivityOptions)
  {
    this.mActivityOptions = paramActivityOptions;
  }
  
  public static ActivityOptionsCompat24 makeBasic()
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeBasic());
  }
  
  public static ActivityOptionsCompat24 makeClipRevealAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeClipRevealAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public static ActivityOptionsCompat24 makeCustomAnimation(Context paramContext, int paramInt1, int paramInt2)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeCustomAnimation(paramContext, paramInt1, paramInt2));
  }
  
  public static ActivityOptionsCompat24 makeScaleUpAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeScaleUpAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4));
  }
  
  public static ActivityOptionsCompat24 makeSceneTransitionAnimation(Activity paramActivity, View paramView, String paramString)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeSceneTransitionAnimation(paramActivity, paramView, paramString));
  }
  
  public static ActivityOptionsCompat24 makeSceneTransitionAnimation(Activity paramActivity, View[] paramArrayOfView, String[] paramArrayOfString)
  {
    Object localObject = null;
    if (paramArrayOfView != null)
    {
      Pair[] arrayOfPair = new Pair[paramArrayOfView.length];
      int i = 0;
      for (;;)
      {
        localObject = arrayOfPair;
        if (i >= arrayOfPair.length) {
          break;
        }
        arrayOfPair[i] = Pair.create(paramArrayOfView[i], paramArrayOfString[i]);
        i += 1;
      }
    }
    return new ActivityOptionsCompat24(ActivityOptions.makeSceneTransitionAnimation(paramActivity, (Pair[])localObject));
  }
  
  public static ActivityOptionsCompat24 makeTaskLaunchBehind()
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeTaskLaunchBehind());
  }
  
  public static ActivityOptionsCompat24 makeThumbnailScaleUpAnimation(View paramView, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    return new ActivityOptionsCompat24(ActivityOptions.makeThumbnailScaleUpAnimation(paramView, paramBitmap, paramInt1, paramInt2));
  }
  
  public Rect getLaunchBounds()
  {
    return this.mActivityOptions.getLaunchBounds();
  }
  
  public void requestUsageTimeReport(PendingIntent paramPendingIntent)
  {
    this.mActivityOptions.requestUsageTimeReport(paramPendingIntent);
  }
  
  public ActivityOptionsCompat24 setLaunchBounds(@Nullable Rect paramRect)
  {
    return new ActivityOptionsCompat24(this.mActivityOptions.setLaunchBounds(paramRect));
  }
  
  public Bundle toBundle()
  {
    return this.mActivityOptions.toBundle();
  }
  
  public void update(ActivityOptionsCompat24 paramActivityOptionsCompat24)
  {
    this.mActivityOptions.update(paramActivityOptionsCompat24.mActivityOptions);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/ActivityOptionsCompat24.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */