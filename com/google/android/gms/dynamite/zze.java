package com.google.android.gms.dynamite;

import android.content.Context;

final class zze
  implements DynamiteModule.VersionPolicy
{
  public final DynamiteModule.VersionPolicy.SelectionResult selectModule(Context paramContext, String paramString, DynamiteModule.VersionPolicy.IVersions paramIVersions)
    throws DynamiteModule.LoadingException
  {
    DynamiteModule.VersionPolicy.SelectionResult localSelectionResult = new DynamiteModule.VersionPolicy.SelectionResult();
    localSelectionResult.localVersion = paramIVersions.getLocalVersion(paramContext, paramString);
    if (localSelectionResult.localVersion != 0)
    {
      localSelectionResult.remoteVersion = paramIVersions.getRemoteVersion(paramContext, paramString, false);
      if ((localSelectionResult.localVersion != 0) || (localSelectionResult.remoteVersion != 0)) {
        break label86;
      }
      localSelectionResult.selection = 0;
    }
    for (;;)
    {
      return localSelectionResult;
      localSelectionResult.remoteVersion = paramIVersions.getRemoteVersion(paramContext, paramString, true);
      break;
      label86:
      if (localSelectionResult.localVersion >= localSelectionResult.remoteVersion) {
        localSelectionResult.selection = -1;
      } else {
        localSelectionResult.selection = 1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamite/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */