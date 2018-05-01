package android.support.v4.app;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@TargetApi(9)
@RequiresApi(9)
class BundleCompatGingerbread
{
  private static final String TAG = "BundleCompatGingerbread";
  private static Method sGetIBinderMethod;
  private static boolean sGetIBinderMethodFetched;
  private static Method sPutIBinderMethod;
  private static boolean sPutIBinderMethodFetched;
  
  public static IBinder getBinder(Bundle paramBundle, String paramString)
  {
    if (!sGetIBinderMethodFetched) {}
    try
    {
      sGetIBinderMethod = Bundle.class.getMethod("getIBinder", new Class[] { String.class });
      sGetIBinderMethod.setAccessible(true);
      sGetIBinderMethodFetched = true;
      if (sGetIBinderMethod == null) {}
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        try
        {
          paramBundle = (IBinder)sGetIBinderMethod.invoke(paramBundle, new Object[] { paramString });
          return paramBundle;
        }
        catch (IllegalAccessException paramBundle)
        {
          Log.i("BundleCompatGingerbread", "Failed to invoke getIBinder via reflection", paramBundle);
          sGetIBinderMethod = null;
          return null;
        }
        catch (IllegalArgumentException paramBundle)
        {
          continue;
        }
        catch (InvocationTargetException paramBundle)
        {
          continue;
        }
        localNoSuchMethodException = localNoSuchMethodException;
        Log.i("BundleCompatGingerbread", "Failed to retrieve getIBinder method", localNoSuchMethodException);
      }
    }
  }
  
  public static void putBinder(Bundle paramBundle, String paramString, IBinder paramIBinder)
  {
    if (!sPutIBinderMethodFetched) {}
    try
    {
      sPutIBinderMethod = Bundle.class.getMethod("putIBinder", new Class[] { String.class, IBinder.class });
      sPutIBinderMethod.setAccessible(true);
      sPutIBinderMethodFetched = true;
      if (sPutIBinderMethod == null) {}
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      for (;;)
      {
        try
        {
          sPutIBinderMethod.invoke(paramBundle, new Object[] { paramString, paramIBinder });
          return;
        }
        catch (IllegalAccessException paramBundle)
        {
          Log.i("BundleCompatGingerbread", "Failed to invoke putIBinder via reflection", paramBundle);
          sPutIBinderMethod = null;
          return;
        }
        catch (IllegalArgumentException paramBundle)
        {
          continue;
        }
        catch (InvocationTargetException paramBundle)
        {
          continue;
        }
        localNoSuchMethodException = localNoSuchMethodException;
        Log.i("BundleCompatGingerbread", "Failed to retrieve putIBinder method", localNoSuchMethodException);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/BundleCompatGingerbread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */