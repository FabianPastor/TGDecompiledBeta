package org.telegram.messenger.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.NotProvisionedException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.extractor.mp4.PsshAtomUtil;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(18)
public class StreamingDrmSessionManager<T extends ExoMediaCrypto>
  implements DrmSessionManager<T>, DrmSession<T>
{
  private static final int MSG_KEYS = 1;
  private static final int MSG_PROVISION = 0;
  public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
  final MediaDrmCallback callback;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private Exception lastException;
  private T mediaCrypto;
  private final ExoMediaDrm<T> mediaDrm;
  StreamingDrmSessionManager<T>.MediaDrmHandler mediaDrmHandler;
  private int openCount;
  private final HashMap<String, String> optionalKeyRequestParameters;
  private Looper playbackLooper;
  private Handler postRequestHandler;
  StreamingDrmSessionManager<T>.PostResponseHandler postResponseHandler;
  private boolean provisioningInProgress;
  private HandlerThread requestHandlerThread;
  private DrmInitData.SchemeData schemeData;
  private byte[] sessionId;
  private int state;
  final UUID uuid;
  
  public StreamingDrmSessionManager(UUID paramUUID, ExoMediaDrm<T> paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
  {
    this.uuid = paramUUID;
    this.mediaDrm = paramExoMediaDrm;
    this.callback = paramMediaDrmCallback;
    this.optionalKeyRequestParameters = paramHashMap;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    paramExoMediaDrm.setOnEventListener(new MediaDrmEventListener(null));
    this.state = 1;
  }
  
  public static StreamingDrmSessionManager<FrameworkMediaCrypto> newFrameworkInstance(UUID paramUUID, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    return new StreamingDrmSessionManager(paramUUID, FrameworkMediaDrm.newInstance(paramUUID), paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener);
  }
  
  public static StreamingDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(MediaDrmCallback paramMediaDrmCallback, String paramString, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    HashMap localHashMap;
    if (!TextUtils.isEmpty(paramString))
    {
      localHashMap = new HashMap();
      localHashMap.put("PRCustomData", paramString);
    }
    for (paramString = localHashMap;; paramString = null) {
      return newFrameworkInstance(C.PLAYREADY_UUID, paramMediaDrmCallback, paramString, paramHandler, paramEventListener);
    }
  }
  
  public static StreamingDrmSessionManager<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    return newFrameworkInstance(C.WIDEVINE_UUID, paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener);
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
      ExoMediaDrm.KeyRequest localKeyRequest = this.mediaDrm.getKeyRequest(this.sessionId, this.schemeData.data, this.schemeData.mimeType, 1, this.optionalKeyRequestParameters);
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
  
  public DrmSession<T> acquireSession(Looper paramLooper, DrmInitData paramDrmInitData)
  {
    if ((this.playbackLooper == null) || (this.playbackLooper == paramLooper)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      int i = this.openCount + 1;
      this.openCount = i;
      if (i == 1) {
        break;
      }
      return this;
    }
    if (this.playbackLooper == null)
    {
      this.playbackLooper = paramLooper;
      this.mediaDrmHandler = new MediaDrmHandler(paramLooper);
      this.postResponseHandler = new PostResponseHandler(paramLooper);
    }
    this.requestHandlerThread = new HandlerThread("DrmRequestHandler");
    this.requestHandlerThread.start();
    this.postRequestHandler = new PostRequestHandler(this.requestHandlerThread.getLooper());
    this.schemeData = paramDrmInitData.get(this.uuid);
    if (this.schemeData == null)
    {
      onError(new IllegalStateException("Media does not support uuid: " + this.uuid));
      return this;
    }
    if (Util.SDK_INT < 21)
    {
      paramLooper = PsshAtomUtil.parseSchemeSpecificData(this.schemeData.data, C.WIDEVINE_UUID);
      if (paramLooper != null) {
        break label219;
      }
    }
    for (;;)
    {
      this.state = 2;
      openInternal(true);
      return this;
      label219:
      this.schemeData = new DrmInitData.SchemeData(C.WIDEVINE_UUID, this.schemeData.mimeType, paramLooper);
    }
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
  
  public void releaseSession(DrmSession<T> paramDrmSession)
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
      this.schemeData = null;
      this.mediaCrypto = null;
      this.lastException = null;
    } while (this.sessionId == null);
    this.mediaDrm.closeSession(this.sessionId);
    this.sessionId = null;
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/StreamingDrmSessionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */