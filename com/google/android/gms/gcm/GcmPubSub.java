package com.google.android.gms.gcm;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GcmPubSub
{
  private static GcmPubSub aeH;
  private static final Pattern aeJ = Pattern.compile("/topics/[a-zA-Z0-9-_.~%]{1,900}");
  private InstanceID aeI;
  
  private GcmPubSub(Context paramContext)
  {
    this.aeI = InstanceID.getInstance(paramContext);
  }
  
  public static GcmPubSub getInstance(Context paramContext)
  {
    try
    {
      if (aeH == null) {
        aeH = new GcmPubSub(paramContext);
      }
      paramContext = aeH;
      return paramContext;
    }
    finally {}
  }
  
  @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
  public void subscribe(String paramString1, String paramString2, Bundle paramBundle)
    throws IOException
  {
    if ((paramString1 == null) || (paramString1.isEmpty()))
    {
      paramString1 = String.valueOf(paramString1);
      if (paramString1.length() != 0) {}
      for (paramString1 = "Invalid appInstanceToken: ".concat(paramString1);; paramString1 = new String("Invalid appInstanceToken: ")) {
        throw new IllegalArgumentException(paramString1);
      }
    }
    if ((paramString2 == null) || (!aeJ.matcher(paramString2).matches()))
    {
      paramString1 = String.valueOf(paramString2);
      if (paramString1.length() != 0) {}
      for (paramString1 = "Invalid topic name: ".concat(paramString1);; paramString1 = new String("Invalid topic name: ")) {
        throw new IllegalArgumentException(paramString1);
      }
    }
    Bundle localBundle = paramBundle;
    if (paramBundle == null) {
      localBundle = new Bundle();
    }
    localBundle.putString("gcm.topic", paramString2);
    this.aeI.getToken(paramString1, paramString2, localBundle);
  }
  
  @RequiresPermission("com.google.android.c2dm.permission.RECEIVE")
  public void unsubscribe(String paramString1, String paramString2)
    throws IOException
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("gcm.topic", paramString2);
    this.aeI.zzb(paramString1, paramString2, localBundle);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/gcm/GcmPubSub.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */