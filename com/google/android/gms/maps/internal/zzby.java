package com.google.android.gms.maps.internal;

import android.os.Bundle;
import android.os.Parcelable;

public final class zzby
{
  private static <T extends Parcelable> T zza(Bundle paramBundle, String paramString)
  {
    Object localObject = null;
    if (paramBundle == null) {
      paramBundle = (Bundle)localObject;
    }
    for (;;)
    {
      return paramBundle;
      paramBundle.setClassLoader(zzby.class.getClassLoader());
      Bundle localBundle = paramBundle.getBundle("map_state");
      paramBundle = (Bundle)localObject;
      if (localBundle != null)
      {
        localBundle.setClassLoader(zzby.class.getClassLoader());
        paramBundle = localBundle.getParcelable(paramString);
      }
    }
  }
  
  public static void zza(Bundle paramBundle1, Bundle paramBundle2)
  {
    if ((paramBundle1 == null) || (paramBundle2 == null)) {}
    for (;;)
    {
      return;
      Parcelable localParcelable = zza(paramBundle1, "MapOptions");
      if (localParcelable != null) {
        zza(paramBundle2, "MapOptions", localParcelable);
      }
      localParcelable = zza(paramBundle1, "StreetViewPanoramaOptions");
      if (localParcelable != null) {
        zza(paramBundle2, "StreetViewPanoramaOptions", localParcelable);
      }
      localParcelable = zza(paramBundle1, "camera");
      if (localParcelable != null) {
        zza(paramBundle2, "camera", localParcelable);
      }
      if (paramBundle1.containsKey("position")) {
        paramBundle2.putString("position", paramBundle1.getString("position"));
      }
      if (paramBundle1.containsKey("com.google.android.wearable.compat.extra.LOWBIT_AMBIENT")) {
        paramBundle2.putBoolean("com.google.android.wearable.compat.extra.LOWBIT_AMBIENT", paramBundle1.getBoolean("com.google.android.wearable.compat.extra.LOWBIT_AMBIENT", false));
      }
    }
  }
  
  public static void zza(Bundle paramBundle, String paramString, Parcelable paramParcelable)
  {
    paramBundle.setClassLoader(zzby.class.getClassLoader());
    Bundle localBundle1 = paramBundle.getBundle("map_state");
    Bundle localBundle2 = localBundle1;
    if (localBundle1 == null) {
      localBundle2 = new Bundle();
    }
    localBundle2.setClassLoader(zzby.class.getClassLoader());
    localBundle2.putParcelable(paramString, paramParcelable);
    paramBundle.putBundle("map_state", localBundle2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzby.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */