package org.telegram.messenger.exoplayer.drm;

import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaDrm;
import android.media.MediaDrm.KeyRequest;
import android.media.MediaDrm.OnEventListener;
import android.media.MediaDrm.ProvisionRequest;
import android.media.NotProvisionedException;
import android.media.ResourceBusyException;
import android.media.UnsupportedSchemeException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.exoplayer.util.Assertions;

@TargetApi(18)
public final class FrameworkMediaDrm
  implements ExoMediaDrm<FrameworkMediaCrypto>
{
  private final MediaDrm mediaDrm;
  
  public FrameworkMediaDrm(UUID paramUUID)
    throws UnsupportedSchemeException
  {
    this.mediaDrm = new MediaDrm((UUID)Assertions.checkNotNull(paramUUID));
  }
  
  public void closeSession(byte[] paramArrayOfByte)
  {
    this.mediaDrm.closeSession(paramArrayOfByte);
  }
  
  public FrameworkMediaCrypto createMediaCrypto(UUID paramUUID, byte[] paramArrayOfByte)
    throws MediaCryptoException
  {
    return new FrameworkMediaCrypto(new MediaCrypto(paramUUID, paramArrayOfByte));
  }
  
  public ExoMediaDrm.KeyRequest getKeyRequest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString, int paramInt, HashMap<String, String> paramHashMap)
    throws NotProvisionedException
  {
    new ExoMediaDrm.KeyRequest()
    {
      public byte[] getData()
      {
        return this.val$request.getData();
      }
      
      public String getDefaultUrl()
      {
        return this.val$request.getDefaultUrl();
      }
    };
  }
  
  public byte[] getPropertyByteArray(String paramString)
  {
    return this.mediaDrm.getPropertyByteArray(paramString);
  }
  
  public String getPropertyString(String paramString)
  {
    return this.mediaDrm.getPropertyString(paramString);
  }
  
  public ExoMediaDrm.ProvisionRequest getProvisionRequest()
  {
    new ExoMediaDrm.ProvisionRequest()
    {
      public byte[] getData()
      {
        return this.val$provisionRequest.getData();
      }
      
      public String getDefaultUrl()
      {
        return this.val$provisionRequest.getDefaultUrl();
      }
    };
  }
  
  public byte[] openSession()
    throws NotProvisionedException, ResourceBusyException
  {
    return this.mediaDrm.openSession();
  }
  
  public byte[] provideKeyResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NotProvisionedException, DeniedByServerException
  {
    return this.mediaDrm.provideKeyResponse(paramArrayOfByte1, paramArrayOfByte2);
  }
  
  public void provideProvisionResponse(byte[] paramArrayOfByte)
    throws DeniedByServerException
  {
    this.mediaDrm.provideProvisionResponse(paramArrayOfByte);
  }
  
  public Map<String, String> queryKeyStatus(byte[] paramArrayOfByte)
  {
    return this.mediaDrm.queryKeyStatus(paramArrayOfByte);
  }
  
  public void release()
  {
    this.mediaDrm.release();
  }
  
  public void restoreKeys(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    this.mediaDrm.restoreKeys(paramArrayOfByte1, paramArrayOfByte2);
  }
  
  public void setOnEventListener(final ExoMediaDrm.OnEventListener<? super FrameworkMediaCrypto> paramOnEventListener)
  {
    MediaDrm localMediaDrm = this.mediaDrm;
    if (paramOnEventListener == null) {}
    for (paramOnEventListener = null;; paramOnEventListener = new MediaDrm.OnEventListener()
        {
          public void onEvent(MediaDrm paramAnonymousMediaDrm, byte[] paramAnonymousArrayOfByte1, int paramAnonymousInt1, int paramAnonymousInt2, byte[] paramAnonymousArrayOfByte2)
          {
            paramOnEventListener.onEvent(FrameworkMediaDrm.this, paramAnonymousArrayOfByte1, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousArrayOfByte2);
          }
        })
    {
      localMediaDrm.setOnEventListener(paramOnEventListener);
      return;
    }
  }
  
  public void setPropertyByteArray(String paramString, byte[] paramArrayOfByte)
  {
    this.mediaDrm.setPropertyByteArray(paramString, paramArrayOfByte);
  }
  
  public void setPropertyString(String paramString1, String paramString2)
  {
    this.mediaDrm.setPropertyString(paramString1, paramString2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/drm/FrameworkMediaDrm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */