package com.google.android.gms.wearable.internal;

import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import java.util.Set;

public final class zzw
  implements CapabilityInfo
{
  private final String name;
  private final Set<Node> zzbt;
  
  public zzw(CapabilityInfo paramCapabilityInfo)
  {
    this(paramCapabilityInfo.getName(), paramCapabilityInfo.getNodes());
  }
  
  private zzw(String paramString, Set<Node> paramSet)
  {
    this.name = paramString;
    this.zzbt = paramSet;
  }
  
  public final String getName()
  {
    return this.name;
  }
  
  public final Set<Node> getNodes()
  {
    return this.zzbt;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */