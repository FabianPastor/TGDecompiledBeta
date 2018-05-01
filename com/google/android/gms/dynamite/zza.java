package com.google.android.gms.dynamite;

import android.content.Context;

final class zza
  implements DynamiteModule.VersionPolicy.IVersions
{
  public final int getLocalVersion(Context paramContext, String paramString)
  {
    return DynamiteModule.getLocalVersion(paramContext, paramString);
  }
  
  public final int getRemoteVersion(Context paramContext, String paramString, boolean paramBoolean)
    throws DynamiteModule.LoadingException
  {
    return DynamiteModule.getRemoteVersion(paramContext, paramString, paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamite/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */