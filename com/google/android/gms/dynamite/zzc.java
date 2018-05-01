package com.google.android.gms.dynamite;

import android.content.Context;

final class zzc
  implements DynamiteModule.VersionPolicy
{
  public final DynamiteModule.VersionPolicy.SelectionResult selectModule(Context paramContext, String paramString, DynamiteModule.VersionPolicy.IVersions paramIVersions)
    throws DynamiteModule.LoadingException
  {
    DynamiteModule.VersionPolicy.SelectionResult localSelectionResult = new DynamiteModule.VersionPolicy.SelectionResult();
    localSelectionResult.localVersion = paramIVersions.getLocalVersion(paramContext, paramString);
    if (localSelectionResult.localVersion != 0) {
      localSelectionResult.selection = -1;
    }
    for (;;)
    {
      return localSelectionResult;
      localSelectionResult.remoteVersion = paramIVersions.getRemoteVersion(paramContext, paramString, true);
      if (localSelectionResult.remoteVersion != 0) {
        localSelectionResult.selection = 1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamite/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */