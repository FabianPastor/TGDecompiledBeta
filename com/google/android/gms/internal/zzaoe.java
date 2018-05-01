package com.google.android.gms.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class zzaoe
  extends zzaoh
  implements Iterable<zzaoh>
{
  private final List<zzaoh> aLw = new ArrayList();
  
  public Number aQ()
  {
    if (this.aLw.size() == 1) {
      return ((zzaoh)this.aLw.get(0)).aQ();
    }
    throw new IllegalStateException();
  }
  
  public String aR()
  {
    if (this.aLw.size() == 1) {
      return ((zzaoh)this.aLw.get(0)).aR();
    }
    throw new IllegalStateException();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject == this) || (((paramObject instanceof zzaoe)) && (((zzaoe)paramObject).aLw.equals(this.aLw)));
  }
  
  public boolean getAsBoolean()
  {
    if (this.aLw.size() == 1) {
      return ((zzaoh)this.aLw.get(0)).getAsBoolean();
    }
    throw new IllegalStateException();
  }
  
  public double getAsDouble()
  {
    if (this.aLw.size() == 1) {
      return ((zzaoh)this.aLw.get(0)).getAsDouble();
    }
    throw new IllegalStateException();
  }
  
  public int getAsInt()
  {
    if (this.aLw.size() == 1) {
      return ((zzaoh)this.aLw.get(0)).getAsInt();
    }
    throw new IllegalStateException();
  }
  
  public long getAsLong()
  {
    if (this.aLw.size() == 1) {
      return ((zzaoh)this.aLw.get(0)).getAsLong();
    }
    throw new IllegalStateException();
  }
  
  public int hashCode()
  {
    return this.aLw.hashCode();
  }
  
  public Iterator<zzaoh> iterator()
  {
    return this.aLw.iterator();
  }
  
  public int size()
  {
    return this.aLw.size();
  }
  
  public zzaoh zzagv(int paramInt)
  {
    return (zzaoh)this.aLw.get(paramInt);
  }
  
  public void zzc(zzaoh paramzzaoh)
  {
    Object localObject = paramzzaoh;
    if (paramzzaoh == null) {
      localObject = zzaoj.bld;
    }
    this.aLw.add(localObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaoe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */