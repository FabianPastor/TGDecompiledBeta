package com.google.android.gms.internal;

public final class zzbdy<L>
{
  private final L mListener;
  private final String zzaEP;
  
  zzbdy(L paramL, String paramString)
  {
    this.mListener = paramL;
    this.zzaEP = paramString;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzbdy)) {
        return false;
      }
      paramObject = (zzbdy)paramObject;
    } while ((this.mListener == ((zzbdy)paramObject).mListener) && (this.zzaEP.equals(((zzbdy)paramObject).zzaEP)));
    return false;
  }
  
  public final int hashCode()
  {
    return System.identityHashCode(this.mListener) * 31 + this.zzaEP.hashCode();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbdy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */