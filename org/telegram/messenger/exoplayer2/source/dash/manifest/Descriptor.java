package org.telegram.messenger.exoplayer2.source.dash.manifest;

import org.telegram.messenger.exoplayer2.util.Util;

public final class Descriptor
{
  public final String id;
  public final String schemeIdUri;
  public final String value;
  
  public Descriptor(String paramString1, String paramString2, String paramString3)
  {
    this.schemeIdUri = paramString1;
    this.value = paramString2;
    this.id = paramString3;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (Descriptor)paramObject;
        if ((!Util.areEqual(this.schemeIdUri, ((Descriptor)paramObject).schemeIdUri)) || (!Util.areEqual(this.value, ((Descriptor)paramObject).value)) || (!Util.areEqual(this.id, ((Descriptor)paramObject).id))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    int i = 0;
    int j;
    if (this.schemeIdUri != null)
    {
      j = this.schemeIdUri.hashCode();
      if (this.value == null) {
        break label64;
      }
    }
    label64:
    for (int k = this.value.hashCode();; k = 0)
    {
      if (this.id != null) {
        i = this.id.hashCode();
      }
      return (j * 31 + k) * 31 + i;
      j = 0;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/manifest/Descriptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */