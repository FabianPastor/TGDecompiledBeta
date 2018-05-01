package com.google.firebase.components;

public final class Dependency
{
  private final Class<?> zzam;
  private final int zzan;
  private final int zzao;
  
  public final boolean equals(Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if ((paramObject instanceof Dependency))
    {
      paramObject = (Dependency)paramObject;
      bool2 = bool1;
      if (this.zzam == ((Dependency)paramObject).zzam)
      {
        bool2 = bool1;
        if (this.zzan == ((Dependency)paramObject).zzan)
        {
          bool2 = bool1;
          if (this.zzao == ((Dependency)paramObject).zzao) {
            bool2 = true;
          }
        }
      }
    }
    return bool2;
  }
  
  public final int hashCode()
  {
    return ((this.zzam.hashCode() ^ 0xF4243) * 1000003 ^ this.zzan) * 1000003 ^ this.zzao;
  }
  
  public final String toString()
  {
    boolean bool1 = true;
    StringBuilder localStringBuilder = new StringBuilder("Dependency{interface=").append(this.zzam).append(", required=");
    if (this.zzan == 1)
    {
      bool2 = true;
      localStringBuilder = localStringBuilder.append(bool2).append(", direct=");
      if (this.zzao != 0) {
        break label73;
      }
    }
    label73:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      return bool2 + "}";
      bool2 = false;
      break;
    }
  }
  
  public final Class<?> zzn()
  {
    return this.zzam;
  }
  
  public final boolean zzo()
  {
    boolean bool = true;
    if (this.zzan == 1) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public final boolean zzp()
  {
    if (this.zzao == 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/components/Dependency.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */