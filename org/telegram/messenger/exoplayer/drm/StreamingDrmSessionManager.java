package org.telegram.messenger.exoplayer.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.NotProvisionedException;
import android.media.UnsupportedSchemeException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.UUID;
import org.telegram.messenger.exoplayer.extractor.mp4.PsshAtomUtil;
import org.telegram.messenger.exoplayer.util.Util;

@TargetApi(18)
public class StreamingDrmSessionManager<T extends ExoMediaCrypto>
  implements DrmSessionManager<T>
{
  private static final int MSG_KEYS = 1;
  private static final int MSG_PROVISION = 0;
  public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
  public static final UUID PLAYREADY_UUID = new UUID(-7348484286925749626L, -6083546864340672619L);
  public static final UUID WIDEVINE_UUID = new UUID(-1301668207276963122L, -6645017420763422227L);
  final MediaDrmCallback callback;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private Exception lastException;
  private T mediaCrypto;
  private final ExoMediaDrm<T> mediaDrm;
  final StreamingDrmSessionManager<T>.MediaDrmHandler mediaDrmHandler;
  private int openCount;
  private final HashMap<String, String> optionalKeyRequestParameters;
  private Handler postRequestHandler;
  final StreamingDrmSessionManager<T>.PostResponseHandler postResponseHandler;
  private boolean provisioningInProgress;
  private HandlerThread requestHandlerThread;
  private DrmInitData.SchemeInitData schemeInitData;
  private byte[] sessionId;
  private int state;
  final UUID uuid;
  
  private StreamingDrmSessionManager(UUID paramUUID, Looper paramLooper, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener, ExoMediaDrm<T> paramExoMediaDrm)
    throws UnsupportedDrmException
  {
    this.uuid = paramUUID;
    this.callback = paramMediaDrmCallback;
    this.optionalKeyRequestParameters = paramHashMap;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.mediaDrm = paramExoMediaDrm;
    paramExoMediaDrm.setOnEventListener(new MediaDrmEventListener(null));
    this.mediaDrmHandler = new MediaDrmHandler(paramLooper);
    this.postResponseHandler = new PostResponseHandler(paramLooper);
    this.state = 1;
  }
  
  private static FrameworkMediaDrm createFrameworkDrm(UUID paramUUID)
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
  
  public static StreamingDrmSessionManager<FrameworkMediaCrypto> newFrameworkInstance(UUID paramUUID, Looper paramLooper, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    return newInstance(paramUUID, paramLooper, paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener, createFrameworkDrm(paramUUID));
  }
  
  public static <T extends ExoMediaCrypto> StreamingDrmSessionManager<T> newInstance(UUID paramUUID, Looper paramLooper, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener, ExoMediaDrm<T> paramExoMediaDrm)
    throws UnsupportedDrmException
  {
    return new StreamingDrmSessionManager(paramUUID, paramLooper, paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener, paramExoMediaDrm);
  }
  
  public static StreamingDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(Looper paramLooper, MediaDrmCallback paramMediaDrmCallback, String paramString, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    HashMap localHashMap;
    if (!TextUtils.isEmpty(paramString))
    {
      localHashMap = new HashMap();
      localHashMap.put("PRCustomData", paramString);
    }
    for (paramString = localHashMap;; paramString = null) {
      return newFrameworkInstance(PLAYREADY_UUID, paramLooper, paramMediaDrmCallback, paramString, paramHandler, paramEventListener);
    }
  }
  
  public static StreamingDrmSessionManager<FrameworkMediaCrypto> newWidevineInstance(Looper paramLooper, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    return newFrameworkInstance(WIDEVINE_UUID, paramLooper, paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener);
  }
  
  private void onError(final Exception paramException)
  {
    this.lastException = paramException;
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          StreamingDrmSessionManager.this.eventListener.onDrmSessionManagerError(paramException);
        }
      });
    }
    if (this.state != 4) {
      this.state = 0;
    }
  }
  
  private void onKeyResponse(Object paramObject)
  {
    if ((this.state != 3) && (this.state != 4)) {}
    for (;;)
    {
      return;
      if ((paramObject instanceof Exception))
      {
        onKeysError((Exception)paramObject);
        return;
      }
      try
      {
        this.mediaDrm.provideKeyResponse(this.sessionId, (byte[])paramObject);
        this.state = 4;
        if ((this.eventHandler != null) && (this.eventListener != null))
        {
          this.eventHandler.post(new Runnable()
          {
            public void run()
            {
              StreamingDrmSessionManager.this.eventListener.onDrmKeysLoaded();
            }
          });
          return;
        }
      }
      catch (Exception paramObject)
      {
        onKeysError((Exception)paramObject);
      }
    }
  }
  
  private void onKeysError(Exception paramException)
  {
    if ((paramException instanceof NotProvisionedException))
    {
      postProvisionRequest();
      return;
    }
    onError(paramException);
  }
  
  private void onProvisionResponse(Object paramObject)
  {
    this.provisioningInProgress = false;
    if ((this.state != 2) && (this.state != 3) && (this.state != 4)) {
      return;
    }
    if ((paramObject instanceof Exception))
    {
      onError((Exception)paramObject);
      return;
    }
    try
    {
      this.mediaDrm.provideProvisionResponse((byte[])paramObject);
      if (this.state == 2)
      {
        openInternal(false);
        return;
      }
    }
    catch (DeniedByServerException paramObject)
    {
      onError((Exception)paramObject);
      return;
    }
    postKeyRequest();
  }
  
  private void openInternal(boolean paramBoolean)
  {
    try
    {
      this.sessionId = this.mediaDrm.openSession();
      this.mediaCrypto = this.mediaDrm.createMediaCrypto(this.uuid, this.sessionId);
      this.state = 3;
      postKeyRequest();
      return;
    }
    catch (NotProvisionedException localNotProvisionedException)
    {
      if (paramBoolean)
      {
        postProvisionRequest();
        return;
      }
      onError(localNotProvisionedException);
      return;
    }
    catch (Exception localException)
    {
      onError(localException);
    }
  }
  
  private void postKeyRequest()
  {
    try
    {
      ExoMediaDrm.KeyRequest localKeyRequest = this.mediaDrm.getKeyRequest(this.sessionId, this.schemeInitData.data, this.schemeInitData.mimeType, 1, this.optionalKeyRequestParameters);
      this.postRequestHandler.obtainMessage(1, localKeyRequest).sendToTarget();
      return;
    }
    catch (NotProvisionedException localNotProvisionedException)
    {
      onKeysError(localNotProvisionedException);
    }
  }
  
  private void postProvisionRequest()
  {
    if (this.provisioningInProgress) {
      return;
    }
    this.provisioningInProgress = true;
    ExoMediaDrm.ProvisionRequest localProvisionRequest = this.mediaDrm.getProvisionRequest();
    this.postRequestHandler.obtainMessage(0, localProvisionRequest).sendToTarget();
  }
  
  public void close()
  {
    int i = this.openCount - 1;
    this.openCount = i;
    if (i != 0) {}
    do
    {
      return;
      this.state = 1;
      this.provisioningInProgress = false;
      this.mediaDrmHandler.removeCallbacksAndMessages(null);
      this.postResponseHandler.removeCallbacksAndMessages(null);
      this.postRequestHandler.removeCallbacksAndMessages(null);
      this.postRequestHandler = null;
      this.requestHandlerThread.quit();
      this.requestHandlerThread = null;
      this.schemeInitData = null;
      this.mediaCrypto = null;
      this.lastException = null;
    } while (this.sessionId == null);
    this.mediaDrm.closeSession(this.sessionId);
    this.sessionId = null;
  }
  
  public final Exception getError()
  {
    if (this.state == 0) {
      return this.lastException;
    }
    return null;
  }
  
  public final T getMediaCrypto()
  {
    if ((this.state != 3) && (this.state != 4)) {
      throw new IllegalStateException();
    }
    return this.mediaCrypto;
  }
  
  public final byte[] getPropertyByteArray(String paramString)
  {
    return this.mediaDrm.getPropertyByteArray(paramString);
  }
  
  public final String getPropertyString(String paramString)
  {
    return this.mediaDrm.getPropertyString(paramString);
  }
  
  public final int getState()
  {
    return this.state;
  }
  
  public void open(DrmInitData paramDrmInitData)
  {
    int i = this.openCount + 1;
    this.openCount = i;
    if (i != 1) {
      return;
    }
    if (this.postRequestHandler == null)
    {
      this.requestHandlerThread = new HandlerThread("DrmRequestHandler");
      this.requestHandlerThread.start();
      this.postRequestHandler = new PostRequestHandler(this.requestHandlerThread.getLooper());
    }
    if (this.schemeInitData == null)
    {
      this.schemeInitData = paramDrmInitData.get(this.uuid);
      if (this.schemeInitData == null)
      {
        onError(new IllegalStateException("Media does not support uuid: " + this.uuid));
        return;
      }
      if (Util.SDK_INT < 21)
      {
        paramDrmInitData = PsshAtomUtil.parseSchemeSpecificData(this.schemeInitData.data, WIDEVINE_UUID);
        if (paramDrmInitData != null) {
          break label165;
        }
      }
    }
    for (;;)
    {
      this.state = 2;
      openInternal(true);
      return;
      label165:
      this.schemeInitData = new DrmInitData.SchemeInitData(this.schemeInitData.mimeType, paramDrmInitData);
    }
  }
  
  public boolean requiresSecureDecoderComponent(String paramString)
  {
    if ((this.state != 3) && (this.state != 4)) {
      throw new IllegalStateException();
    }
    return this.mediaCrypto.requiresSecureDecoderComponent(paramString);
  }
  
  public final void setPropertyByteArray(String paramString, byte[] paramArrayOfByte)
  {
    this.mediaDrm.setPropertyByteArray(paramString, paramArrayOfByte);
  }
  
  public final void setPropertyString(String paramString1, String paramString2)
  {
    this.mediaDrm.setPropertyString(paramString1, paramString2);
  }
  
  public static abstract interface EventListener
  {
    public abstract void onDrmKeysLoaded();
    
    public abstract void onDrmSessionManagerError(Exception paramException);
  }
  
  private class MediaDrmEventListener
    implements ExoMediaDrm.OnEventListener<T>
  {
    private MediaDrmEventListener() {}
    
    public void onEvent(ExoMediaDrm<? extends T> paramExoMediaDrm, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
    {
      StreamingDrmSessionManager.this.mediaDrmHandler.sendEmptyMessage(paramInt1);
    }
  }
  
  @SuppressLint({"HandlerLeak"})
  private class MediaDrmHandler
    extends Handler
  {
    public MediaDrmHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      if ((StreamingDrmSessionManager.this.openCount == 0) || ((StreamingDrmSessionManager.this.state != 3) && (StreamingDrmSessionManager.this.state != 4))) {
        return;
      }
      switch (paramMessage.what)
      {
      default: 
        return;
      case 1: 
        StreamingDrmSessionManager.access$302(StreamingDrmSessionManager.this, 3);
        StreamingDrmSessionManager.this.postProvisionRequest();
        return;
      case 2: 
        StreamingDrmSessionManager.this.postKeyRequest();
        return;
      }
      StreamingDrmSessionManager.access$302(StreamingDrmSessionManager.this, 3);
      StreamingDrmSessionManager.this.onError(new KeysExpiredException());
    }
  }
  
  @SuppressLint({"HandlerLeak"})
  private class PostRequestHandler
    extends Handler
  {
    public PostRequestHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      for (;;)
      {
        try
        {
          switch (paramMessage.what)
          {
          case 0: 
            throw new RuntimeException();
          }
        }
        catch (Exception localException)
        {
          StreamingDrmSessionManager.this.postResponseHandler.obtainMessage(paramMessage.what, localException).sendToTarget();
          return;
        }
        byte[] arrayOfByte = StreamingDrmSessionManager.this.callback.executeProvisionRequest(StreamingDrmSessionManager.this.uuid, (ExoMediaDrm.ProvisionRequest)paramMessage.obj);
        continue;
        arrayOfByte = StreamingDrmSessionManager.this.callback.executeKeyRequest(StreamingDrmSessionManager.this.uuid, (ExoMediaDrm.KeyRequest)paramMessage.obj);
      }
    }
  }
  
  @SuppressLint({"HandlerLeak"})
  private class PostResponseHandler
    extends Handler
  {
    public PostResponseHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 0: 
        StreamingDrmSessionManager.this.onProvisionResponse(paramMessage.obj);
        return;
      }
      StreamingDrmSessionManager.this.onKeyResponse(paramMessage.obj);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/drm/StreamingDrmSessionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */