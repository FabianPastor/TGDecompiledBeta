package org.telegram.messenger.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.extractor.mp4.PsshAtomUtil;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(18)
public class DefaultDrmSessionManager<T extends ExoMediaCrypto>
  implements DefaultDrmSession.ProvisioningManager<T>, DrmSessionManager<T>
{
  private static final String CENC_SCHEME_MIME_TYPE = "cenc";
  public static final int INITIAL_DRM_REQUEST_RETRY_COUNT = 3;
  public static final int MODE_DOWNLOAD = 2;
  public static final int MODE_PLAYBACK = 0;
  public static final int MODE_QUERY = 1;
  public static final int MODE_RELEASE = 3;
  public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
  private static final String TAG = "DefaultDrmSessionMgr";
  private final MediaDrmCallback callback;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private final int initialDrmRequestRetryCount;
  private final ExoMediaDrm<T> mediaDrm;
  volatile DefaultDrmSessionManager<T>.MediaDrmHandler mediaDrmHandler;
  private int mode;
  private final boolean multiSession;
  private byte[] offlineLicenseKeySetId;
  private final HashMap<String, String> optionalKeyRequestParameters;
  private Looper playbackLooper;
  private final List<DefaultDrmSession<T>> provisioningSessions;
  private final List<DefaultDrmSession<T>> sessions;
  private final UUID uuid;
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm<T> paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
  {
    this(paramUUID, paramExoMediaDrm, paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener, false, 3);
  }
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm<T> paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener, boolean paramBoolean)
  {
    this(paramUUID, paramExoMediaDrm, paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener, paramBoolean, 3);
  }
  
  public DefaultDrmSessionManager(UUID paramUUID, ExoMediaDrm<T> paramExoMediaDrm, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener, boolean paramBoolean, int paramInt)
  {
    Assertions.checkNotNull(paramUUID);
    Assertions.checkNotNull(paramExoMediaDrm);
    if (!C.COMMON_PSSH_UUID.equals(paramUUID)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkArgument(bool, "Use C.CLEARKEY_UUID instead");
      this.uuid = paramUUID;
      this.mediaDrm = paramExoMediaDrm;
      this.callback = paramMediaDrmCallback;
      this.optionalKeyRequestParameters = paramHashMap;
      this.eventHandler = paramHandler;
      this.eventListener = paramEventListener;
      this.multiSession = paramBoolean;
      this.initialDrmRequestRetryCount = paramInt;
      this.mode = 0;
      this.sessions = new ArrayList();
      this.provisioningSessions = new ArrayList();
      if (paramBoolean) {
        paramExoMediaDrm.setPropertyString("sessionSharing", "enable");
      }
      paramExoMediaDrm.setOnEventListener(new MediaDrmEventListener(null));
      return;
    }
  }
  
  private static DrmInitData.SchemeData getSchemeData(DrmInitData paramDrmInitData, UUID paramUUID, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(paramDrmInitData.schemeDataCount);
    int i = 0;
    int j;
    if (i < paramDrmInitData.schemeDataCount)
    {
      DrmInitData.SchemeData localSchemeData = paramDrmInitData.get(i);
      if ((localSchemeData.matches(paramUUID)) || ((C.CLEARKEY_UUID.equals(paramUUID)) && (localSchemeData.matches(C.COMMON_PSSH_UUID)))) {}
      for (j = 1;; j = 0)
      {
        if ((j != 0) && ((localSchemeData.data != null) || (paramBoolean))) {
          localArrayList.add(localSchemeData);
        }
        i++;
        break;
      }
    }
    if (localArrayList.isEmpty()) {
      paramDrmInitData = null;
    }
    for (;;)
    {
      return paramDrmInitData;
      if (C.WIDEVINE_UUID.equals(paramUUID))
      {
        i = 0;
        label129:
        if (i < localArrayList.size())
        {
          paramUUID = (DrmInitData.SchemeData)localArrayList.get(i);
          if (paramUUID.hasData()) {}
          for (j = PsshAtomUtil.parseVersion(paramUUID.data);; j = -1)
          {
            if (Util.SDK_INT < 23)
            {
              paramDrmInitData = paramUUID;
              if (j == 0) {
                break;
              }
            }
            if (Util.SDK_INT >= 23)
            {
              paramDrmInitData = paramUUID;
              if (j == 1) {
                break;
              }
            }
            i++;
            break label129;
          }
        }
      }
      paramDrmInitData = (DrmInitData.SchemeData)localArrayList.get(0);
    }
  }
  
  private static byte[] getSchemeInitData(DrmInitData.SchemeData paramSchemeData, UUID paramUUID)
  {
    byte[] arrayOfByte = paramSchemeData.data;
    paramSchemeData = arrayOfByte;
    if (Util.SDK_INT < 21)
    {
      paramSchemeData = PsshAtomUtil.parseSchemeSpecificData(arrayOfByte, paramUUID);
      if (paramSchemeData != null) {
        break label29;
      }
      paramSchemeData = arrayOfByte;
    }
    label29:
    for (;;)
    {
      return paramSchemeData;
    }
  }
  
  private static String getSchemeMimeType(DrmInitData.SchemeData paramSchemeData, UUID paramUUID)
  {
    String str = paramSchemeData.mimeType;
    paramSchemeData = str;
    if (Util.SDK_INT < 26)
    {
      paramSchemeData = str;
      if (C.CLEARKEY_UUID.equals(paramUUID)) {
        if (!"video/mp4".equals(str))
        {
          paramSchemeData = str;
          if (!"audio/mp4".equals(str)) {}
        }
        else
        {
          paramSchemeData = "cenc";
        }
      }
    }
    return paramSchemeData;
  }
  
  public static DefaultDrmSessionManager<FrameworkMediaCrypto> newFrameworkInstance(UUID paramUUID, MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    return new DefaultDrmSessionManager(paramUUID, FrameworkMediaDrm.newInstance(paramUUID), paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener, false, 3);
  }
  
  public static DefaultDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(MediaDrmCallback paramMediaDrmCallback, String paramString, Handler paramHandler, EventListener paramEventListener)
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
  
  public static DefaultDrmSessionManager<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback paramMediaDrmCallback, HashMap<String, String> paramHashMap, Handler paramHandler, EventListener paramEventListener)
    throws UnsupportedDrmException
  {
    return newFrameworkInstance(C.WIDEVINE_UUID, paramMediaDrmCallback, paramHashMap, paramHandler, paramEventListener);
  }
  
  public DrmSession<T> acquireSession(final Looper paramLooper, DrmInitData paramDrmInitData)
  {
    if ((this.playbackLooper == null) || (this.playbackLooper == paramLooper)) {}
    Object localObject;
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if (this.sessions.isEmpty())
      {
        this.playbackLooper = paramLooper;
        if (this.mediaDrmHandler == null) {
          this.mediaDrmHandler = new MediaDrmHandler(paramLooper);
        }
      }
      arrayOfByte = null;
      str = null;
      if (this.offlineLicenseKeySetId != null) {
        break label174;
      }
      paramDrmInitData = getSchemeData(paramDrmInitData, this.uuid, false);
      if (paramDrmInitData != null) {
        break;
      }
      paramLooper = new MissingSchemeDataException(this.uuid, null);
      if ((this.eventHandler != null) && (this.eventListener != null)) {
        this.eventHandler.post(new Runnable()
        {
          public void run()
          {
            DefaultDrmSessionManager.this.eventListener.onDrmSessionManagerError(paramLooper);
          }
        });
      }
      localObject = new ErrorStateDrmSession(new DrmSession.DrmSessionException(paramLooper));
      return (DrmSession<T>)localObject;
    }
    byte[] arrayOfByte = getSchemeInitData(paramDrmInitData, this.uuid);
    String str = getSchemeMimeType(paramDrmInitData, this.uuid);
    label174:
    if (!this.multiSession) {
      if (this.sessions.isEmpty()) {
        paramDrmInitData = null;
      }
    }
    for (;;)
    {
      localObject = paramDrmInitData;
      if (paramDrmInitData == null)
      {
        localObject = new DefaultDrmSession(this.uuid, this.mediaDrm, this, arrayOfByte, str, this.mode, this.offlineLicenseKeySetId, this.optionalKeyRequestParameters, this.callback, paramLooper, this.eventHandler, this.eventListener, this.initialDrmRequestRetryCount);
        this.sessions.add(localObject);
      }
      ((DefaultDrmSession)localObject).acquire();
      break;
      paramDrmInitData = (DefaultDrmSession)this.sessions.get(0);
      continue;
      localObject = null;
      Iterator localIterator = this.sessions.iterator();
      do
      {
        paramDrmInitData = (DrmInitData)localObject;
        if (!localIterator.hasNext()) {
          break;
        }
        paramDrmInitData = (DefaultDrmSession)localIterator.next();
      } while (!paramDrmInitData.hasInitData(arrayOfByte));
    }
  }
  
  public boolean canAcquireSession(DrmInitData paramDrmInitData)
  {
    boolean bool1 = true;
    boolean bool2;
    if (this.offlineLicenseKeySetId != null) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      if (getSchemeData(paramDrmInitData, this.uuid, true) == null)
      {
        if ((paramDrmInitData.schemeDataCount == 1) && (paramDrmInitData.get(0).matches(C.COMMON_PSSH_UUID))) {
          Log.w("DefaultDrmSessionMgr", "DrmInitData only contains common PSSH SchemeData. Assuming support for: " + this.uuid);
        }
      }
      else
      {
        paramDrmInitData = paramDrmInitData.schemeType;
        bool2 = bool1;
        if (paramDrmInitData == null) {
          continue;
        }
        bool2 = bool1;
        if ("cenc".equals(paramDrmInitData)) {
          continue;
        }
        if ((!"cbc1".equals(paramDrmInitData)) && (!"cbcs".equals(paramDrmInitData)))
        {
          bool2 = bool1;
          if (!"cens".equals(paramDrmInitData)) {
            continue;
          }
        }
        bool2 = bool1;
        if (Util.SDK_INT >= 24) {
          continue;
        }
        bool2 = false;
        continue;
      }
      bool2 = false;
    }
  }
  
  public final byte[] getPropertyByteArray(String paramString)
  {
    return this.mediaDrm.getPropertyByteArray(paramString);
  }
  
  public final String getPropertyString(String paramString)
  {
    return this.mediaDrm.getPropertyString(paramString);
  }
  
  public void onProvisionCompleted()
  {
    Iterator localIterator = this.provisioningSessions.iterator();
    while (localIterator.hasNext()) {
      ((DefaultDrmSession)localIterator.next()).onProvisionCompleted();
    }
    this.provisioningSessions.clear();
  }
  
  public void onProvisionError(Exception paramException)
  {
    Iterator localIterator = this.provisioningSessions.iterator();
    while (localIterator.hasNext()) {
      ((DefaultDrmSession)localIterator.next()).onProvisionError(paramException);
    }
    this.provisioningSessions.clear();
  }
  
  public void provisionRequired(DefaultDrmSession<T> paramDefaultDrmSession)
  {
    this.provisioningSessions.add(paramDefaultDrmSession);
    if (this.provisioningSessions.size() == 1) {
      paramDefaultDrmSession.provision();
    }
  }
  
  public void releaseSession(DrmSession<T> paramDrmSession)
  {
    if ((paramDrmSession instanceof ErrorStateDrmSession)) {}
    for (;;)
    {
      return;
      paramDrmSession = (DefaultDrmSession)paramDrmSession;
      if (paramDrmSession.release())
      {
        this.sessions.remove(paramDrmSession);
        if ((this.provisioningSessions.size() > 1) && (this.provisioningSessions.get(0) == paramDrmSession)) {
          ((DefaultDrmSession)this.provisioningSessions.get(1)).provision();
        }
        this.provisioningSessions.remove(paramDrmSession);
      }
    }
  }
  
  public void setMode(int paramInt, byte[] paramArrayOfByte)
  {
    Assertions.checkState(this.sessions.isEmpty());
    if ((paramInt == 1) || (paramInt == 3)) {
      Assertions.checkNotNull(paramArrayOfByte);
    }
    this.mode = paramInt;
    this.offlineLicenseKeySetId = paramArrayOfByte;
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
    
    public abstract void onDrmKeysRemoved();
    
    public abstract void onDrmKeysRestored();
    
    public abstract void onDrmSessionManagerError(Exception paramException);
  }
  
  private class MediaDrmEventListener
    implements ExoMediaDrm.OnEventListener<T>
  {
    private MediaDrmEventListener() {}
    
    public void onEvent(ExoMediaDrm<? extends T> paramExoMediaDrm, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
    {
      if (DefaultDrmSessionManager.this.mode == 0) {
        DefaultDrmSessionManager.this.mediaDrmHandler.obtainMessage(paramInt1, paramArrayOfByte1).sendToTarget();
      }
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
      byte[] arrayOfByte = (byte[])paramMessage.obj;
      Iterator localIterator = DefaultDrmSessionManager.this.sessions.iterator();
      while (localIterator.hasNext())
      {
        DefaultDrmSession localDefaultDrmSession = (DefaultDrmSession)localIterator.next();
        if (localDefaultDrmSession.hasSessionId(arrayOfByte)) {
          localDefaultDrmSession.onMediaDrmEvent(paramMessage.what);
        }
      }
    }
  }
  
  public static final class MissingSchemeDataException
    extends Exception
  {
    private MissingSchemeDataException(UUID paramUUID)
    {
      super();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Mode {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/DefaultDrmSessionManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */