package android.support.v13.app;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.os.BuildCompat;
import java.util.Arrays;

@TargetApi(13)
@RequiresApi(13)
public class FragmentCompat
{
  static final FragmentCompatImpl IMPL = new BaseFragmentCompatImpl();
  
  static
  {
    if (BuildCompat.isAtLeastN())
    {
      IMPL = new NFragmentCompatImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 23)
    {
      IMPL = new MncFragmentCompatImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 15)
    {
      IMPL = new ICSMR1FragmentCompatImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 14)
    {
      IMPL = new ICSFragmentCompatImpl();
      return;
    }
  }
  
  public static void requestPermissions(@NonNull Fragment paramFragment, @NonNull String[] paramArrayOfString, int paramInt)
  {
    IMPL.requestPermissions(paramFragment, paramArrayOfString, paramInt);
  }
  
  public static void setMenuVisibility(Fragment paramFragment, boolean paramBoolean)
  {
    IMPL.setMenuVisibility(paramFragment, paramBoolean);
  }
  
  public static void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
  {
    IMPL.setUserVisibleHint(paramFragment, paramBoolean);
  }
  
  public static boolean shouldShowRequestPermissionRationale(@NonNull Fragment paramFragment, @NonNull String paramString)
  {
    return IMPL.shouldShowRequestPermissionRationale(paramFragment, paramString);
  }
  
  static class BaseFragmentCompatImpl
    implements FragmentCompat.FragmentCompatImpl
  {
    public void requestPermissions(final Fragment paramFragment, final String[] paramArrayOfString, final int paramInt)
    {
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          int[] arrayOfInt = new int[paramArrayOfString.length];
          Object localObject = paramFragment.getActivity();
          if (localObject != null)
          {
            PackageManager localPackageManager = ((Context)localObject).getPackageManager();
            localObject = ((Context)localObject).getPackageName();
            int j = paramArrayOfString.length;
            int i = 0;
            while (i < j)
            {
              arrayOfInt[i] = localPackageManager.checkPermission(paramArrayOfString[i], (String)localObject);
              i += 1;
            }
          }
          Arrays.fill(arrayOfInt, -1);
          ((FragmentCompat.OnRequestPermissionsResultCallback)paramFragment).onRequestPermissionsResult(paramInt, paramArrayOfString, arrayOfInt);
        }
      });
    }
    
    public void setMenuVisibility(Fragment paramFragment, boolean paramBoolean) {}
    
    public void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean) {}
    
    public boolean shouldShowRequestPermissionRationale(Fragment paramFragment, String paramString)
    {
      return false;
    }
  }
  
  static abstract interface FragmentCompatImpl
  {
    public abstract void requestPermissions(Fragment paramFragment, String[] paramArrayOfString, int paramInt);
    
    public abstract void setMenuVisibility(Fragment paramFragment, boolean paramBoolean);
    
    public abstract void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean);
    
    public abstract boolean shouldShowRequestPermissionRationale(Fragment paramFragment, String paramString);
  }
  
  static class ICSFragmentCompatImpl
    extends FragmentCompat.BaseFragmentCompatImpl
  {
    public void setMenuVisibility(Fragment paramFragment, boolean paramBoolean)
    {
      FragmentCompatICS.setMenuVisibility(paramFragment, paramBoolean);
    }
  }
  
  static class ICSMR1FragmentCompatImpl
    extends FragmentCompat.ICSFragmentCompatImpl
  {
    public void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
    {
      FragmentCompatICSMR1.setUserVisibleHint(paramFragment, paramBoolean);
    }
  }
  
  static class MncFragmentCompatImpl
    extends FragmentCompat.ICSMR1FragmentCompatImpl
  {
    public void requestPermissions(Fragment paramFragment, String[] paramArrayOfString, int paramInt)
    {
      FragmentCompat23.requestPermissions(paramFragment, paramArrayOfString, paramInt);
    }
    
    public boolean shouldShowRequestPermissionRationale(Fragment paramFragment, String paramString)
    {
      return FragmentCompat23.shouldShowRequestPermissionRationale(paramFragment, paramString);
    }
  }
  
  static class NFragmentCompatImpl
    extends FragmentCompat.MncFragmentCompatImpl
  {
    public void setUserVisibleHint(Fragment paramFragment, boolean paramBoolean)
    {
      FragmentCompatApi24.setUserVisibleHint(paramFragment, paramBoolean);
    }
  }
  
  public static abstract interface OnRequestPermissionsResultCallback
  {
    public abstract void onRequestPermissionsResult(int paramInt, @NonNull String[] paramArrayOfString, @NonNull int[] paramArrayOfInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v13/app/FragmentCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */