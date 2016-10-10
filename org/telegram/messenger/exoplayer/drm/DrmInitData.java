package org.telegram.messenger.exoplayer.drm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.exoplayer.util.Assertions;

public abstract interface DrmInitData
{
  public abstract SchemeInitData get(UUID paramUUID);
  
  public static final class Mapped
    implements DrmInitData
  {
    private final Map<UUID, DrmInitData.SchemeInitData> schemeData = new HashMap();
    
    public DrmInitData.SchemeInitData get(UUID paramUUID)
    {
      return (DrmInitData.SchemeInitData)this.schemeData.get(paramUUID);
    }
    
    public void put(UUID paramUUID, DrmInitData.SchemeInitData paramSchemeInitData)
    {
      this.schemeData.put(paramUUID, paramSchemeInitData);
    }
  }
  
  public static final class SchemeInitData
  {
    public final byte[] data;
    public final String mimeType;
    
    public SchemeInitData(String paramString, byte[] paramArrayOfByte)
    {
      this.mimeType = ((String)Assertions.checkNotNull(paramString));
      this.data = ((byte[])Assertions.checkNotNull(paramArrayOfByte));
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = true;
      boolean bool1;
      if (!(paramObject instanceof SchemeInitData)) {
        bool1 = false;
      }
      do
      {
        do
        {
          return bool1;
          bool1 = bool2;
        } while (paramObject == this);
        paramObject = (SchemeInitData)paramObject;
        if (!this.mimeType.equals(((SchemeInitData)paramObject).mimeType)) {
          break;
        }
        bool1 = bool2;
      } while (Arrays.equals(this.data, ((SchemeInitData)paramObject).data));
      return false;
    }
    
    public int hashCode()
    {
      return this.mimeType.hashCode() + Arrays.hashCode(this.data) * 31;
    }
  }
  
  public static final class Universal
    implements DrmInitData
  {
    private DrmInitData.SchemeInitData data;
    
    public Universal(DrmInitData.SchemeInitData paramSchemeInitData)
    {
      this.data = paramSchemeInitData;
    }
    
    public DrmInitData.SchemeInitData get(UUID paramUUID)
    {
      return this.data;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/drm/DrmInitData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */