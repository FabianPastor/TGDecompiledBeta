package com.google.android.gms.maps.internal;

import android.os.Bundle;
import android.os.Parcelable;

public final class zzbw
{
  public static void zza(Bundle paramBundle, String paramString, Parcelable paramParcelable)
  {
    paramBundle.setClassLoader(zzbw.class.getClassLoader());
    Bundle localBundle2 = paramBundle.getBundle("map_state");
    Bundle localBundle1 = localBundle2;
    if (localBundle2 == null) {
      localBundle1 = new Bundle();
    }
    localBundle1.setClassLoader(zzbw.class.getClassLoader());
    localBundle1.putParcelable(paramString, paramParcelable);
    paramBundle.putBundle("map_state", localBundle1);
  }
  
  public static void zzd(Bundle paramBundle1, Bundle paramBundle2)
  {
    if ((paramBundle1 == null) || (paramBundle2 == null)) {}
    do
    {
      return;
      Parcelable localParcelable = zzg(paramBundle1, "MapOptions");
      if (localParcelable != null) {
        zza(paramBundle2, "MapOptions", localParcelable);
      }
      localParcelable = zzg(paramBundle1, "StreetViewPanoramaOptions");
      if (localParcelable != null) {
        zza(paramBundle2, "StreetViewPanoramaOptions", localParcelable);
      }
      localParcelable = zzg(paramBundle1, "camera");
      if (localParcelable != null) {
        zza(paramBundle2, "camera", localParcelable);
      }
      if (paramBundle1.containsKey("position")) {
        paramBundle2.putString("position", paramBundle1.getString("position"));
      }
    } while (!paramBundle1.containsKey("com.google.android.wearable.compat.extra.LOWBIT_AMBIENT"));
    paramBundle2.putBoolean("com.google.android.wearable.compat.extra.LOWBIT_AMBIENT", paramBundle1.getBoolean("com.google.android.wearable.compat.extra.LOWBIT_AMBIENT", false));
  }
  
  private static <T extends Parcelable> T zzg(Bundle paramBundle, String paramString)
  {
    if (paramBundle == null) {}
    do
    {
      return null;
      paramBundle.setClassLoader(zzbw.class.getClassLoader());
      paramBundle = paramBundle.getBundle("map_state");
    } while (paramBundle == null);
    paramBundle.setClassLoader(zzbw.class.getClassLoader());
    return paramBundle.getParcelable(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/internal/zzbw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */