package android.support.v4.net;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class ConnectivityManagerCompat
{
  private static final ConnectivityManagerCompatImpl IMPL = new BaseConnectivityManagerCompatImpl();
  public static final int RESTRICT_BACKGROUND_STATUS_DISABLED = 1;
  public static final int RESTRICT_BACKGROUND_STATUS_ENABLED = 3;
  public static final int RESTRICT_BACKGROUND_STATUS_WHITELISTED = 2;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      IMPL = new Api24ConnectivityManagerCompatImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 16)
    {
      IMPL = new JellyBeanConnectivityManagerCompatImpl();
      return;
    }
    if (Build.VERSION.SDK_INT >= 13)
    {
      IMPL = new HoneycombMR2ConnectivityManagerCompatImpl();
      return;
    }
  }
  
  public static NetworkInfo getNetworkInfoFromBroadcast(ConnectivityManager paramConnectivityManager, Intent paramIntent)
  {
    paramIntent = (NetworkInfo)paramIntent.getParcelableExtra("networkInfo");
    if (paramIntent != null) {
      return paramConnectivityManager.getNetworkInfo(paramIntent.getType());
    }
    return null;
  }
  
  public static int getRestrictBackgroundStatus(ConnectivityManager paramConnectivityManager)
  {
    return IMPL.getRestrictBackgroundStatus(paramConnectivityManager);
  }
  
  public static boolean isActiveNetworkMetered(ConnectivityManager paramConnectivityManager)
  {
    return IMPL.isActiveNetworkMetered(paramConnectivityManager);
  }
  
  static class Api24ConnectivityManagerCompatImpl
    extends ConnectivityManagerCompat.JellyBeanConnectivityManagerCompatImpl
  {
    public int getRestrictBackgroundStatus(ConnectivityManager paramConnectivityManager)
    {
      return ConnectivityManagerCompatApi24.getRestrictBackgroundStatus(paramConnectivityManager);
    }
  }
  
  static class BaseConnectivityManagerCompatImpl
    implements ConnectivityManagerCompat.ConnectivityManagerCompatImpl
  {
    public int getRestrictBackgroundStatus(ConnectivityManager paramConnectivityManager)
    {
      return 3;
    }
    
    public boolean isActiveNetworkMetered(ConnectivityManager paramConnectivityManager)
    {
      paramConnectivityManager = paramConnectivityManager.getActiveNetworkInfo();
      if (paramConnectivityManager == null) {
        return true;
      }
      switch (paramConnectivityManager.getType())
      {
      case 0: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
      default: 
        return true;
      }
      return false;
    }
  }
  
  static abstract interface ConnectivityManagerCompatImpl
  {
    public abstract int getRestrictBackgroundStatus(ConnectivityManager paramConnectivityManager);
    
    public abstract boolean isActiveNetworkMetered(ConnectivityManager paramConnectivityManager);
  }
  
  static class HoneycombMR2ConnectivityManagerCompatImpl
    extends ConnectivityManagerCompat.BaseConnectivityManagerCompatImpl
  {
    public boolean isActiveNetworkMetered(ConnectivityManager paramConnectivityManager)
    {
      return ConnectivityManagerCompatHoneycombMR2.isActiveNetworkMetered(paramConnectivityManager);
    }
  }
  
  static class JellyBeanConnectivityManagerCompatImpl
    extends ConnectivityManagerCompat.HoneycombMR2ConnectivityManagerCompatImpl
  {
    public boolean isActiveNetworkMetered(ConnectivityManager paramConnectivityManager)
    {
      return ConnectivityManagerCompatJellyBean.isActiveNetworkMetered(paramConnectivityManager);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface RestrictBackgroundStatus {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/net/ConnectivityManagerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */