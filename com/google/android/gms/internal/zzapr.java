package com.google.android.gms.internal;

public final class zzapr
{
  private static zzapr zzaWJ;
  private final zzapo zzaWK = new zzapo();
  private final zzapp zzaWL = new zzapp();
  
  static
  {
    zza(new zzapr());
  }
  
  private static zzapr zzCP()
  {
    try
    {
      zzapr localzzapr = zzaWJ;
      return localzzapr;
    }
    finally {}
  }
  
  public static zzapo zzCQ()
  {
    return zzCP().zzaWK;
  }
  
  public static zzapp zzCR()
  {
    return zzCP().zzaWL;
  }
  
  protected static void zza(zzapr paramzzapr)
  {
    try
    {
      zzaWJ = paramzzapr;
      return;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */