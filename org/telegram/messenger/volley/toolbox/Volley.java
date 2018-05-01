package org.telegram.messenger.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build.VERSION;
import java.io.File;
import org.telegram.messenger.volley.RequestQueue;

public class Volley
{
  private static final String DEFAULT_CACHE_DIR = "volley";
  
  public static RequestQueue newRequestQueue(Context paramContext)
  {
    return newRequestQueue(paramContext, null);
  }
  
  public static RequestQueue newRequestQueue(Context paramContext, HttpStack paramHttpStack)
  {
    File localFile = new File(paramContext.getCacheDir(), "volley");
    Object localObject = "volley/0";
    try
    {
      String str = paramContext.getPackageName();
      paramContext = paramContext.getPackageManager().getPackageInfo(str, 0);
      paramContext = str + "/" + paramContext.versionCode;
      localObject = paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      label118:
      for (;;) {}
    }
    paramContext = paramHttpStack;
    if (paramHttpStack == null) {
      if (Build.VERSION.SDK_INT < 9) {
        break label118;
      }
    }
    for (paramContext = new HurlStack();; paramContext = new HttpClientStack(AndroidHttpClient.newInstance((String)localObject)))
    {
      paramContext = new BasicNetwork(paramContext);
      paramContext = new RequestQueue(new DiskBasedCache(localFile), paramContext);
      paramContext.start();
      return paramContext;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/toolbox/Volley.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */