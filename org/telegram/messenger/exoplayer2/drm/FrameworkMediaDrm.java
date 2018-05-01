package org.telegram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaDrm;
import android.media.MediaDrm.KeyRequest;
import android.media.MediaDrm.KeyStatus;
import android.media.MediaDrm.OnEventListener;
import android.media.MediaDrm.OnKeyStatusChangeListener;
import android.media.MediaDrm.ProvisionRequest;
import android.media.MediaDrmException;
import android.media.NotProvisionedException;
import android.media.UnsupportedSchemeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(23)
public final class FrameworkMediaDrm
  implements ExoMediaDrm<FrameworkMediaCrypto>
{
  private final MediaDrm mediaDrm;
  private final UUID uuid;
  
  private FrameworkMediaDrm(UUID paramUUID)
    throws UnsupportedSchemeException
  {
    Assertions.checkNotNull(paramUUID);
    if (!C.COMMON_PSSH_UUID.equals(paramUUID)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool, "Use C.CLEARKEY_UUID instead");
      UUID localUUID = paramUUID;
      if (Util.SDK_INT < 27)
      {
        localUUID = paramUUID;
        if (C.CLEARKEY_UUID.equals(paramUUID)) {
          localUUID = C.COMMON_PSSH_UUID;
        }
      }
      this.uuid = localUUID;
      this.mediaDrm = new MediaDrm(localUUID);
      return;
    }
  }
  
  public static FrameworkMediaDrm newInstance(UUID paramUUID)
    throws UnsupportedDrmException
  {
    try
    {
      paramUUID = new FrameworkMediaDrm(paramUUID);
      return paramUUID;
    }
    catch (UnsupportedSchemeException paramUUID)
    {
      throw new UnsupportedDrmException(1, paramUUID);
    }
    catch (Exception paramUUID)
    {
      throw new UnsupportedDrmException(2, paramUUID);
    }
  }
  
  public void closeSession(byte[] paramArrayOfByte)
  {
    this.mediaDrm.closeSession(paramArrayOfByte);
  }
  
  public FrameworkMediaCrypto createMediaCrypto(byte[] paramArrayOfByte)
    throws MediaCryptoException
  {
    if ((Util.SDK_INT < 21) && (C.WIDEVINE_UUID.equals(this.uuid)) && ("L3".equals(getPropertyString("securityLevel")))) {}
    for (boolean bool = true;; bool = false) {
      return new FrameworkMediaCrypto(new MediaCrypto(this.uuid, paramArrayOfByte), bool);
    }
  }
  
  public ExoMediaDrm.KeyRequest getKeyRequest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString, int paramInt, HashMap<String, String> paramHashMap)
    throws NotProvisionedException
  {
    paramArrayOfByte1 = this.mediaDrm.getKeyRequest(paramArrayOfByte1, paramArrayOfByte2, paramString, paramInt, paramHashMap);
    return new ExoMediaDrm.DefaultKeyRequest(paramArrayOfByte1.getData(), paramArrayOfByte1.getDefaultUrl());
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
    MediaDrm.ProvisionRequest localProvisionRequest = this.mediaDrm.getProvisionRequest();
    return new ExoMediaDrm.DefaultProvisionRequest(localProvisionRequest.getData(), localProvisionRequest.getDefaultUrl());
  }
  
  public byte[] openSession()
    throws MediaDrmException
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
  
  public void setOnKeyStatusChangeListener(final ExoMediaDrm.OnKeyStatusChangeListener<? super FrameworkMediaCrypto> paramOnKeyStatusChangeListener)
  {
    if (Util.SDK_INT < 23) {
      throw new UnsupportedOperationException();
    }
    MediaDrm localMediaDrm = this.mediaDrm;
    if (paramOnKeyStatusChangeListener == null) {}
    for (paramOnKeyStatusChangeListener = null;; paramOnKeyStatusChangeListener = new MediaDrm.OnKeyStatusChangeListener()
        {
          public void onKeyStatusChange(MediaDrm paramAnonymousMediaDrm, byte[] paramAnonymousArrayOfByte, List<MediaDrm.KeyStatus> paramAnonymousList, boolean paramAnonymousBoolean)
          {
            paramAnonymousMediaDrm = new ArrayList();
            Iterator localIterator = paramAnonymousList.iterator();
            while (localIterator.hasNext())
            {
              paramAnonymousList = (MediaDrm.KeyStatus)localIterator.next();
              paramAnonymousMediaDrm.add(new ExoMediaDrm.DefaultKeyStatus(paramAnonymousList.getStatusCode(), paramAnonymousList.getKeyId()));
            }
            paramOnKeyStatusChangeListener.onKeyStatusChange(FrameworkMediaDrm.this, paramAnonymousArrayOfByte, paramAnonymousMediaDrm, paramAnonymousBoolean);
          }
        })
    {
      localMediaDrm.setOnKeyStatusChangeListener(paramOnKeyStatusChangeListener, null);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/FrameworkMediaDrm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */