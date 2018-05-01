package org.telegram.messenger.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.NotProvisionedException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C;

@TargetApi(18)
class DefaultDrmSession<T extends ExoMediaCrypto>
  implements DrmSession<T>
{
  private static final int MAX_LICENSE_DURATION_TO_RENEW = 60;
  private static final int MSG_KEYS = 1;
  private static final int MSG_PROVISION = 0;
  private static final String TAG = "DefaultDrmSession";
  final MediaDrmCallback callback;
  private final Handler eventHandler;
  private final DefaultDrmSessionManager.EventListener eventListener;
  private final byte[] initData;
  private final int initialDrmRequestRetryCount;
  private DrmSession.DrmSessionException lastException;
  private T mediaCrypto;
  private final ExoMediaDrm<T> mediaDrm;
  private final String mimeType;
  private final int mode;
  private byte[] offlineLicenseKeySetId;
  private int openCount;
  private final HashMap<String, String> optionalKeyRequestParameters;
  private DefaultDrmSession<T>.PostRequestHandler postRequestHandler;
  final DefaultDrmSession<T>.PostResponseHandler postResponseHandler;
  private final ProvisioningManager<T> provisioningManager;
  private HandlerThread requestHandlerThread;
  private byte[] sessionId;
  private int state;
  final UUID uuid;
  
  public DefaultDrmSession(UUID paramUUID, ExoMediaDrm<T> paramExoMediaDrm, ProvisioningManager<T> paramProvisioningManager, byte[] paramArrayOfByte1, String paramString, int paramInt1, byte[] paramArrayOfByte2, HashMap<String, String> paramHashMap, MediaDrmCallback paramMediaDrmCallback, Looper paramLooper, Handler paramHandler, DefaultDrmSessionManager.EventListener paramEventListener, int paramInt2)
  {
    this.uuid = paramUUID;
    this.provisioningManager = paramProvisioningManager;
    this.mediaDrm = paramExoMediaDrm;
    this.mode = paramInt1;
    this.offlineLicenseKeySetId = paramArrayOfByte2;
    this.optionalKeyRequestParameters = paramHashMap;
    this.callback = paramMediaDrmCallback;
    this.initialDrmRequestRetryCount = paramInt2;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.state = 2;
    this.postResponseHandler = new PostResponseHandler(paramLooper);
    this.requestHandlerThread = new HandlerThread("DrmRequestHandler");
    this.requestHandlerThread.start();
    this.postRequestHandler = new PostRequestHandler(this.requestHandlerThread.getLooper());
    if (paramArrayOfByte2 == null) {
      this.initData = paramArrayOfByte1;
    }
    for (this.mimeType = paramString;; this.mimeType = null)
    {
      return;
      this.initData = null;
    }
  }
  
  private void doLicense(boolean paramBoolean)
  {
    switch (this.mode)
    {
    }
    for (;;)
    {
      return;
      if (this.offlineLicenseKeySetId == null)
      {
        postKeyRequest(1, paramBoolean);
      }
      else if ((this.state == 4) || (restoreKeys()))
      {
        long l = getLicenseDurationRemainingSec();
        if ((this.mode == 0) && (l <= 60L))
        {
          Log.d("DefaultDrmSession", "Offline license has expired or will expire soon. Remaining seconds: " + l);
          postKeyRequest(2, paramBoolean);
        }
        else if (l <= 0L)
        {
          onError(new KeysExpiredException());
        }
        else
        {
          this.state = 4;
          if ((this.eventHandler != null) && (this.eventListener != null))
          {
            this.eventHandler.post(new Runnable()
            {
              public void run()
              {
                DefaultDrmSession.this.eventListener.onDrmKeysRestored();
              }
            });
            continue;
            if (this.offlineLicenseKeySetId == null)
            {
              postKeyRequest(2, paramBoolean);
            }
            else if (restoreKeys())
            {
              postKeyRequest(2, paramBoolean);
              continue;
              if (restoreKeys()) {
                postKeyRequest(3, paramBoolean);
              }
            }
          }
        }
      }
    }
  }
  
  private long getLicenseDurationRemainingSec()
  {
    if (!C.WIDEVINE_UUID.equals(this.uuid)) {}
    Pair localPair;
    for (long l = Long.MAX_VALUE;; l = Math.min(((Long)localPair.first).longValue(), ((Long)localPair.second).longValue()))
    {
      return l;
      localPair = WidevineUtil.getLicenseDurationRemainingSec(this);
    }
  }
  
  private boolean isOpen()
  {
    if ((this.state == 3) || (this.state == 4)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void onError(final Exception paramException)
  {
    this.lastException = new DrmSession.DrmSessionException(paramException);
    if ((this.eventHandler != null) && (this.eventListener != null)) {
      this.eventHandler.post(new Runnable()
      {
        public void run()
        {
          DefaultDrmSession.this.eventListener.onDrmSessionManagerError(paramException);
        }
      });
    }
    if (this.state != 4) {
      this.state = 1;
    }
  }
  
  private void onKeyResponse(Object paramObject)
  {
    if (!isOpen()) {}
    for (;;)
    {
      return;
      if ((paramObject instanceof Exception))
      {
        onKeysError((Exception)paramObject);
      }
      else
      {
        Object localObject;
        try
        {
          localObject = (byte[])paramObject;
          paramObject = localObject;
          if (C.CLEARKEY_UUID.equals(this.uuid)) {
            paramObject = ClearKeyUtil.adjustResponseData((byte[])localObject);
          }
          if (this.mode != 3) {
            break label120;
          }
          this.mediaDrm.provideKeyResponse(this.offlineLicenseKeySetId, (byte[])paramObject);
          if ((this.eventHandler == null) || (this.eventListener == null)) {
            continue;
          }
          localObject = this.eventHandler;
          paramObject = new org/telegram/messenger/exoplayer2/drm/DefaultDrmSession$2;
          ((2)paramObject).<init>(this);
          ((Handler)localObject).post((Runnable)paramObject);
        }
        catch (Exception paramObject)
        {
          onKeysError((Exception)paramObject);
        }
        continue;
        label120:
        paramObject = this.mediaDrm.provideKeyResponse(this.sessionId, (byte[])paramObject);
        if (((this.mode == 2) || ((this.mode == 0) && (this.offlineLicenseKeySetId != null))) && (paramObject != null) && (paramObject.length != 0)) {
          this.offlineLicenseKeySetId = ((byte[])paramObject);
        }
        this.state = 4;
        if ((this.eventHandler != null) && (this.eventListener != null))
        {
          localObject = this.eventHandler;
          paramObject = new org/telegram/messenger/exoplayer2/drm/DefaultDrmSession$3;
          ((3)paramObject).<init>(this);
          ((Handler)localObject).post((Runnable)paramObject);
        }
      }
    }
  }
  
  private void onKeysError(Exception paramException)
  {
    if ((paramException instanceof NotProvisionedException)) {
      this.provisioningManager.provisionRequired(this);
    }
    for (;;)
    {
      return;
      onError(paramException);
    }
  }
  
  private void onKeysExpired()
  {
    if (this.state == 4)
    {
      this.state = 3;
      onError(new KeysExpiredException());
    }
  }
  
  private void onProvisionResponse(Object paramObject)
  {
    if ((this.state != 2) && (!isOpen())) {}
    for (;;)
    {
      return;
      if ((paramObject instanceof Exception)) {
        this.provisioningManager.onProvisionError((Exception)paramObject);
      } else {
        try
        {
          this.mediaDrm.provideProvisionResponse((byte[])paramObject);
          this.provisioningManager.onProvisionCompleted();
        }
        catch (Exception paramObject)
        {
          this.provisioningManager.onProvisionError((Exception)paramObject);
        }
      }
    }
  }
  
  private boolean openInternal(boolean paramBoolean)
  {
    boolean bool = true;
    if (isOpen()) {
      paramBoolean = bool;
    }
    for (;;)
    {
      return paramBoolean;
      try
      {
        this.sessionId = this.mediaDrm.openSession();
        this.mediaCrypto = this.mediaDrm.createMediaCrypto(this.sessionId);
        this.state = 3;
        paramBoolean = bool;
      }
      catch (NotProvisionedException localNotProvisionedException)
      {
        if (paramBoolean) {
          this.provisioningManager.provisionRequired(this);
        }
        for (;;)
        {
          paramBoolean = false;
          break;
          onError(localNotProvisionedException);
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          onError(localException);
        }
      }
    }
  }
  
  private void postKeyRequest(int paramInt, boolean paramBoolean)
  {
    if (paramInt == 3) {}
    for (Object localObject = this.offlineLicenseKeySetId;; localObject = this.sessionId) {
      try
      {
        ExoMediaDrm.KeyRequest localKeyRequest = this.mediaDrm.getKeyRequest((byte[])localObject, this.initData, this.mimeType, paramInt, this.optionalKeyRequestParameters);
        localObject = localKeyRequest;
        if (C.CLEARKEY_UUID.equals(this.uuid))
        {
          localObject = new org/telegram/messenger/exoplayer2/drm/ExoMediaDrm$DefaultKeyRequest;
          ((ExoMediaDrm.DefaultKeyRequest)localObject).<init>(ClearKeyUtil.adjustRequestData(localKeyRequest.getData()), localKeyRequest.getDefaultUrl());
        }
        this.postRequestHandler.obtainMessage(1, localObject, paramBoolean).sendToTarget();
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          onKeysError(localException);
        }
      }
    }
  }
  
  private boolean restoreKeys()
  {
    try
    {
      this.mediaDrm.restoreKeys(this.sessionId, this.offlineLicenseKeySetId);
      bool = true;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e("DefaultDrmSession", "Error trying to restore Widevine keys.", localException);
        onError(localException);
        boolean bool = false;
      }
    }
    return bool;
  }
  
  public void acquire()
  {
    int i = this.openCount + 1;
    this.openCount = i;
    if ((i != 1) || (this.state == 1)) {}
    for (;;)
    {
      return;
      if (openInternal(true)) {
        doLicense(true);
      }
    }
  }
  
  public final DrmSession.DrmSessionException getError()
  {
    if (this.state == 1) {}
    for (DrmSession.DrmSessionException localDrmSessionException = this.lastException;; localDrmSessionException = null) {
      return localDrmSessionException;
    }
  }
  
  public final T getMediaCrypto()
  {
    return this.mediaCrypto;
  }
  
  public byte[] getOfflineLicenseKeySetId()
  {
    return this.offlineLicenseKeySetId;
  }
  
  public final int getState()
  {
    return this.state;
  }
  
  public boolean hasInitData(byte[] paramArrayOfByte)
  {
    return Arrays.equals(this.initData, paramArrayOfByte);
  }
  
  public boolean hasSessionId(byte[] paramArrayOfByte)
  {
    return Arrays.equals(this.sessionId, paramArrayOfByte);
  }
  
  public void onMediaDrmEvent(int paramInt)
  {
    if (!isOpen()) {}
    for (;;)
    {
      return;
      switch (paramInt)
      {
      default: 
        break;
      case 1: 
        this.state = 3;
        this.provisioningManager.provisionRequired(this);
        break;
      case 2: 
        doLicense(false);
        break;
      case 3: 
        onKeysExpired();
      }
    }
  }
  
  public void onProvisionCompleted()
  {
    if (openInternal(false)) {
      doLicense(true);
    }
  }
  
  public void onProvisionError(Exception paramException)
  {
    onError(paramException);
  }
  
  public void provision()
  {
    ExoMediaDrm.ProvisionRequest localProvisionRequest = this.mediaDrm.getProvisionRequest();
    this.postRequestHandler.obtainMessage(0, localProvisionRequest, true).sendToTarget();
  }
  
  public Map<String, String> queryKeyStatus()
  {
    if (this.sessionId == null) {}
    for (Object localObject = null;; localObject = this.mediaDrm.queryKeyStatus(this.sessionId)) {
      return (Map<String, String>)localObject;
    }
  }
  
  public boolean release()
  {
    boolean bool = false;
    int i = this.openCount - 1;
    this.openCount = i;
    if (i == 0)
    {
      this.state = 0;
      this.postResponseHandler.removeCallbacksAndMessages(null);
      this.postRequestHandler.removeCallbacksAndMessages(null);
      this.postRequestHandler = null;
      this.requestHandlerThread.quit();
      this.requestHandlerThread = null;
      this.mediaCrypto = null;
      this.lastException = null;
      if (this.sessionId != null)
      {
        this.mediaDrm.closeSession(this.sessionId);
        this.sessionId = null;
      }
      bool = true;
    }
    return bool;
  }
  
  @SuppressLint({"HandlerLeak"})
  private class PostRequestHandler
    extends Handler
  {
    public PostRequestHandler(Looper paramLooper)
    {
      super();
    }
    
    private long getRetryDelayMillis(int paramInt)
    {
      return Math.min((paramInt - 1) * 1000, 5000);
    }
    
    private boolean maybeRetryRequest(Message paramMessage)
    {
      boolean bool = false;
      int i;
      if (paramMessage.arg1 == 1)
      {
        i = 1;
        if (i != 0) {
          break label23;
        }
      }
      for (;;)
      {
        return bool;
        i = 0;
        break;
        label23:
        i = paramMessage.arg2 + 1;
        if (i <= DefaultDrmSession.this.initialDrmRequestRetryCount)
        {
          paramMessage = Message.obtain(paramMessage);
          paramMessage.arg2 = i;
          sendMessageDelayed(paramMessage, getRetryDelayMillis(i));
          bool = true;
        }
      }
    }
    
    public void handleMessage(Message paramMessage)
    {
      try
      {
        switch (paramMessage.what)
        {
        default: 
          RuntimeException localRuntimeException = new java/lang/RuntimeException;
          localRuntimeException.<init>();
          throw localRuntimeException;
        }
      }
      catch (Exception localException)
      {
        if (!maybeRetryRequest(paramMessage)) {
          break label126;
        }
      }
      return;
      byte[] arrayOfByte = DefaultDrmSession.this.callback.executeProvisionRequest(DefaultDrmSession.this.uuid, (ExoMediaDrm.ProvisionRequest)paramMessage.obj);
      label126:
      for (;;)
      {
        DefaultDrmSession.this.postResponseHandler.obtainMessage(paramMessage.what, arrayOfByte).sendToTarget();
        break;
        arrayOfByte = DefaultDrmSession.this.callback.executeKeyRequest(DefaultDrmSession.this.uuid, (ExoMediaDrm.KeyRequest)paramMessage.obj);
      }
    }
    
    Message obtainMessage(int paramInt, Object paramObject, boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 1;; i = 0) {
        return obtainMessage(paramInt, i, 0, paramObject);
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
      }
      for (;;)
      {
        return;
        DefaultDrmSession.this.onProvisionResponse(paramMessage.obj);
        continue;
        DefaultDrmSession.this.onKeyResponse(paramMessage.obj);
      }
    }
  }
  
  public static abstract interface ProvisioningManager<T extends ExoMediaCrypto>
  {
    public abstract void onProvisionCompleted();
    
    public abstract void onProvisionError(Exception paramException);
    
    public abstract void provisionRequired(DefaultDrmSession<T> paramDefaultDrmSession);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/DefaultDrmSession.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */