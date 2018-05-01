package org.telegram.messenger.exoplayer2.drm;

import java.io.IOException;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class LocalMediaDrmCallback
  implements MediaDrmCallback
{
  private final byte[] keyResponse;
  
  public LocalMediaDrmCallback(byte[] paramArrayOfByte)
  {
    this.keyResponse = ((byte[])Assertions.checkNotNull(paramArrayOfByte));
  }
  
  public byte[] executeKeyRequest(UUID paramUUID, ExoMediaDrm.KeyRequest paramKeyRequest)
    throws Exception
  {
    return this.keyResponse;
  }
  
  public byte[] executeProvisionRequest(UUID paramUUID, ExoMediaDrm.ProvisionRequest paramProvisionRequest)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/LocalMediaDrmCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */