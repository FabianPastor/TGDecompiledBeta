package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseIntArray;
import java.util.ArrayList;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messages_editMessage;
import org.telegram.tgnet.TLRPC.TL_messages_getRecentLocations;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.messages_Messages;

public class LocationController
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int BACKGROUD_UPDATE_TIME = 90000;
  private static final int FOREGROUND_UPDATE_TIME = 20000;
  private static volatile LocationController[] Instance = new LocationController[3];
  private static final int LOCATION_ACQUIRE_TIME = 10000;
  private static final double eps = 1.0E-4D;
  private LongSparseArray<Boolean> cacheRequests = new LongSparseArray();
  private int currentAccount;
  private GpsLocationListener gpsLocationListener = new GpsLocationListener(null);
  private Location lastKnownLocation;
  private boolean lastLocationByGoogleMaps;
  private long lastLocationSendTime;
  private long lastLocationStartTime;
  private LocationManager locationManager;
  private boolean locationSentSinceLastGoogleMapUpdate = true;
  public LongSparseArray<ArrayList<TLRPC.Message>> locationsCache = new LongSparseArray();
  private GpsLocationListener networkLocationListener = new GpsLocationListener(null);
  private GpsLocationListener passiveLocationListener = new GpsLocationListener(null);
  private SparseIntArray requests = new SparseIntArray();
  private ArrayList<SharingLocationInfo> sharingLocations = new ArrayList();
  private LongSparseArray<SharingLocationInfo> sharingLocationsMap = new LongSparseArray();
  private LongSparseArray<SharingLocationInfo> sharingLocationsMapUI = new LongSparseArray();
  public ArrayList<SharingLocationInfo> sharingLocationsUI = new ArrayList();
  private boolean started;
  
  public LocationController(int paramInt)
  {
    this.currentAccount = paramInt;
    this.locationManager = ((LocationManager)ApplicationLoader.applicationContext.getSystemService("location"));
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        LocationController localLocationController = LocationController.getInstance(LocationController.this.currentAccount);
        NotificationCenter.getInstance(LocationController.this.currentAccount).addObserver(localLocationController, NotificationCenter.didReceivedNewMessages);
        NotificationCenter.getInstance(LocationController.this.currentAccount).addObserver(localLocationController, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(LocationController.this.currentAccount).addObserver(localLocationController, NotificationCenter.replaceMessagesObjects);
      }
    });
    loadSharingLocations();
  }
  
  private void broadcastLastKnownLocation()
  {
    if (this.lastKnownLocation == null) {}
    for (;;)
    {
      return;
      if (this.requests.size() != 0)
      {
        for (i = 0; i < this.requests.size(); i++) {
          ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requests.keyAt(i), false);
        }
        this.requests.clear();
      }
      int j = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      int i = 0;
      if (i < this.sharingLocations.size())
      {
        final SharingLocationInfo localSharingLocationInfo = (SharingLocationInfo)this.sharingLocations.get(i);
        int k;
        label153:
        Object localObject;
        if ((localSharingLocationInfo.messageObject.messageOwner.media != null) && (localSharingLocationInfo.messageObject.messageOwner.media.geo != null)) {
          if (localSharingLocationInfo.messageObject.messageOwner.edit_date != 0)
          {
            k = localSharingLocationInfo.messageObject.messageOwner.edit_date;
            localObject = localSharingLocationInfo.messageObject.messageOwner.media.geo;
            if ((Math.abs(j - k) >= 30) || (Math.abs(((TLRPC.GeoPoint)localObject).lat - this.lastKnownLocation.getLatitude()) > 1.0E-4D) || (Math.abs(((TLRPC.GeoPoint)localObject)._long - this.lastKnownLocation.getLongitude()) > 1.0E-4D)) {
              break label247;
            }
          }
        }
        for (;;)
        {
          i++;
          break;
          k = localSharingLocationInfo.messageObject.messageOwner.date;
          break label153;
          label247:
          localObject = new TLRPC.TL_messages_editMessage();
          ((TLRPC.TL_messages_editMessage)localObject).peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int)localSharingLocationInfo.did);
          ((TLRPC.TL_messages_editMessage)localObject).id = localSharingLocationInfo.mid;
          ((TLRPC.TL_messages_editMessage)localObject).stop_geo_live = false;
          ((TLRPC.TL_messages_editMessage)localObject).flags |= 0x2000;
          ((TLRPC.TL_messages_editMessage)localObject).geo_point = new TLRPC.TL_inputGeoPoint();
          ((TLRPC.TL_messages_editMessage)localObject).geo_point.lat = this.lastKnownLocation.getLatitude();
          ((TLRPC.TL_messages_editMessage)localObject).geo_point._long = this.lastKnownLocation.getLongitude();
          final int[] arrayOfInt = new int[1];
          arrayOfInt[0 = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTL_error != null) {
                if (paramAnonymousTL_error.text.equals("MESSAGE_ID_INVALID"))
                {
                  LocationController.this.sharingLocations.remove(localSharingLocationInfo);
                  LocationController.this.sharingLocationsMap.remove(localSharingLocationInfo.did);
                  LocationController.this.saveSharingLocation(localSharingLocationInfo, 1);
                  LocationController.this.requests.delete(arrayOfInt[0]);
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      LocationController.this.sharingLocationsUI.remove(LocationController.2.this.val$info);
                      LocationController.this.sharingLocationsMapUI.remove(LocationController.2.this.val$info.did);
                      if (LocationController.this.sharingLocationsUI.isEmpty()) {
                        LocationController.this.stopService();
                      }
                      NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
                    }
                  });
                }
              }
              for (;;)
              {
                return;
                paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
                int i = 0;
                int j = 0;
                if (j < paramAnonymousTLObject.updates.size())
                {
                  paramAnonymousTL_error = (TLRPC.Update)paramAnonymousTLObject.updates.get(j);
                  if ((paramAnonymousTL_error instanceof TLRPC.TL_updateEditMessage))
                  {
                    i = 1;
                    localSharingLocationInfo.messageObject.messageOwner = ((TLRPC.TL_updateEditMessage)paramAnonymousTL_error).message;
                  }
                  for (;;)
                  {
                    j++;
                    break;
                    if ((paramAnonymousTL_error instanceof TLRPC.TL_updateEditChannelMessage))
                    {
                      i = 1;
                      localSharingLocationInfo.messageObject.messageOwner = ((TLRPC.TL_updateEditChannelMessage)paramAnonymousTL_error).message;
                    }
                  }
                }
                if (i != 0) {
                  LocationController.this.saveSharingLocation(localSharingLocationInfo, 0);
                }
                MessagesController.getInstance(LocationController.this.currentAccount).processUpdates(paramAnonymousTLObject, false);
              }
            }
          });
          this.requests.put(arrayOfInt[0], 0);
        }
      }
      ConnectionsManager.getInstance(this.currentAccount).resumeNetworkMaybe();
      stop(false);
    }
  }
  
  public static LocationController getInstance(int paramInt)
  {
    Object localObject1 = Instance[paramInt];
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject1 = Instance[paramInt];
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = Instance;
        localObject2 = new org/telegram/messenger/LocationController;
        ((LocationController)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (LocationController)localObject2;
    }
    finally {}
  }
  
  public static int getLocationsCount()
  {
    int i = 0;
    for (int j = 0; j < 3; j++) {
      i += getInstance(j).sharingLocationsUI.size();
    }
    return i;
  }
  
  private void loadSharingLocations()
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        final ArrayList localArrayList1 = new ArrayList();
        final ArrayList localArrayList2 = new ArrayList();
        final ArrayList localArrayList3 = new ArrayList();
        ArrayList localArrayList4;
        SQLiteCursor localSQLiteCursor;
        int i;
        try
        {
          localArrayList4 = new java/util/ArrayList;
          localArrayList4.<init>();
          ArrayList localArrayList5 = new java/util/ArrayList;
          localArrayList5.<init>();
          localSQLiteCursor = MessagesStorage.getInstance(LocationController.this.currentAccount).getDatabase().queryFinalized("SELECT uid, mid, date, period, message FROM sharing_locations WHERE 1", new Object[0]);
          for (;;)
          {
            if (localSQLiteCursor.next())
            {
              LocationController.SharingLocationInfo localSharingLocationInfo = new org/telegram/messenger/LocationController$SharingLocationInfo;
              localSharingLocationInfo.<init>();
              localSharingLocationInfo.did = localSQLiteCursor.longValue(0);
              localSharingLocationInfo.mid = localSQLiteCursor.intValue(1);
              localSharingLocationInfo.stopTime = localSQLiteCursor.intValue(2);
              localSharingLocationInfo.period = localSQLiteCursor.intValue(3);
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(4);
              if (localNativeByteBuffer != null)
              {
                MessageObject localMessageObject = new org/telegram/messenger/MessageObject;
                localMessageObject.<init>(LocationController.this.currentAccount, TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false), false);
                localSharingLocationInfo.messageObject = localMessageObject;
                MessagesStorage.addUsersAndChatsFromMessage(localSharingLocationInfo.messageObject.messageOwner, localArrayList4, localArrayList5);
                localNativeByteBuffer.reuse();
              }
              localArrayList1.add(localSharingLocationInfo);
              i = (int)localSharingLocationInfo.did;
              int j = (int)(localSharingLocationInfo.did >> 32);
              if (i != 0) {
                if (i < 0)
                {
                  if (localArrayList5.contains(Integer.valueOf(-i))) {
                    continue;
                  }
                  localArrayList5.add(Integer.valueOf(-i));
                  continue;
                  if (localArrayList1.isEmpty()) {
                    break;
                  }
                }
              }
            }
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        for (;;)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.getInstance(LocationController.this.currentAccount).putUsers(localArrayList2, true);
              MessagesController.getInstance(LocationController.this.currentAccount).putChats(localArrayList3, true);
              Utilities.stageQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  LocationController.this.sharingLocations.addAll(LocationController.6.1.this.val$result);
                  for (int i = 0; i < LocationController.this.sharingLocations.size(); i++)
                  {
                    LocationController.SharingLocationInfo localSharingLocationInfo = (LocationController.SharingLocationInfo)LocationController.this.sharingLocations.get(i);
                    LocationController.this.sharingLocationsMap.put(localSharingLocationInfo.did, localSharingLocationInfo);
                  }
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      LocationController.this.sharingLocationsUI.addAll(LocationController.6.1.this.val$result);
                      for (int i = 0; i < LocationController.6.1.this.val$result.size(); i++)
                      {
                        LocationController.SharingLocationInfo localSharingLocationInfo = (LocationController.SharingLocationInfo)LocationController.6.1.this.val$result.get(i);
                        LocationController.this.sharingLocationsMapUI.put(localSharingLocationInfo.did, localSharingLocationInfo);
                      }
                      LocationController.this.startService();
                      NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
                    }
                  });
                }
              });
            }
          });
          return;
          if (localArrayList4.contains(Integer.valueOf(i))) {
            break;
          }
          localArrayList4.add(Integer.valueOf(i));
          break;
          localSQLiteCursor.dispose();
          if (!localException.isEmpty()) {
            MessagesStorage.getInstance(LocationController.this.currentAccount).getChatsInternal(TextUtils.join(",", localException), localArrayList3);
          }
          if (!localArrayList4.isEmpty()) {
            MessagesStorage.getInstance(LocationController.this.currentAccount).getUsersInternal(TextUtils.join(",", localArrayList4), localArrayList2);
          }
        }
      }
    });
  }
  
  private void saveSharingLocation(final SharingLocationInfo paramSharingLocationInfo, final int paramInt)
  {
    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          if (paramInt == 2) {
            MessagesStorage.getInstance(LocationController.this.currentAccount).getDatabase().executeFast("DELETE FROM sharing_locations WHERE 1").stepThis().dispose();
          }
          for (;;)
          {
            return;
            if (paramInt != 1) {
              break;
            }
            if (paramSharingLocationInfo != null)
            {
              SQLiteDatabase localSQLiteDatabase = MessagesStorage.getInstance(LocationController.this.currentAccount).getDatabase();
              localObject = new java/lang/StringBuilder;
              ((StringBuilder)localObject).<init>();
              localSQLiteDatabase.executeFast("DELETE FROM sharing_locations WHERE uid = " + paramSharingLocationInfo.did).stepThis().dispose();
            }
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            Object localObject;
            FileLog.e(localException);
            continue;
            if (paramSharingLocationInfo != null)
            {
              localObject = MessagesStorage.getInstance(LocationController.this.currentAccount).getDatabase().executeFast("REPLACE INTO sharing_locations VALUES(?, ?, ?, ?, ?)");
              ((SQLitePreparedStatement)localObject).requery();
              NativeByteBuffer localNativeByteBuffer = new org/telegram/tgnet/NativeByteBuffer;
              localNativeByteBuffer.<init>(paramSharingLocationInfo.messageObject.messageOwner.getObjectSize());
              paramSharingLocationInfo.messageObject.messageOwner.serializeToStream(localNativeByteBuffer);
              ((SQLitePreparedStatement)localObject).bindLong(1, paramSharingLocationInfo.did);
              ((SQLitePreparedStatement)localObject).bindInteger(2, paramSharingLocationInfo.mid);
              ((SQLitePreparedStatement)localObject).bindInteger(3, paramSharingLocationInfo.stopTime);
              ((SQLitePreparedStatement)localObject).bindInteger(4, paramSharingLocationInfo.period);
              ((SQLitePreparedStatement)localObject).bindByteBuffer(5, localNativeByteBuffer);
              ((SQLitePreparedStatement)localObject).step();
              ((SQLitePreparedStatement)localObject).dispose();
              localNativeByteBuffer.reuse();
            }
          }
        }
      }
    });
  }
  
  private void start()
  {
    if (this.started) {}
    for (;;)
    {
      return;
      this.lastLocationStartTime = System.currentTimeMillis();
      this.started = true;
      try
      {
        this.locationManager.requestLocationUpdates("gps", 1L, 0.0F, this.gpsLocationListener);
      }
      catch (Exception localException3)
      {
        try
        {
          this.locationManager.requestLocationUpdates("network", 1L, 0.0F, this.networkLocationListener);
        }
        catch (Exception localException3)
        {
          try
          {
            for (;;)
            {
              this.locationManager.requestLocationUpdates("passive", 1L, 0.0F, this.passiveLocationListener);
              if (this.lastKnownLocation != null) {
                break;
              }
              try
              {
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
                if (this.lastKnownLocation != null) {
                  break;
                }
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
              }
              catch (Exception localException1)
              {
                FileLog.e(localException1);
              }
              break;
              localException2 = localException2;
              FileLog.e(localException2);
              continue;
              localException3 = localException3;
              FileLog.e(localException3);
            }
          }
          catch (Exception localException4)
          {
            for (;;)
            {
              FileLog.e(localException4);
            }
          }
        }
      }
    }
  }
  
  private void startService()
  {
    try
    {
      Context localContext = ApplicationLoader.applicationContext;
      Intent localIntent = new android/content/Intent;
      localIntent.<init>(ApplicationLoader.applicationContext, LocationSharingService.class);
      localContext.startService(localIntent);
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        FileLog.e(localThrowable);
      }
    }
  }
  
  private void stop(boolean paramBoolean)
  {
    this.started = false;
    this.locationManager.removeUpdates(this.gpsLocationListener);
    if (paramBoolean)
    {
      this.locationManager.removeUpdates(this.networkLocationListener);
      this.locationManager.removeUpdates(this.passiveLocationListener);
    }
  }
  
  private void stopService()
  {
    ApplicationLoader.applicationContext.stopService(new Intent(ApplicationLoader.applicationContext, LocationSharingService.class));
  }
  
  protected void addSharingLocation(long paramLong, int paramInt1, int paramInt2, final TLRPC.Message paramMessage)
  {
    final SharingLocationInfo localSharingLocationInfo = new SharingLocationInfo();
    localSharingLocationInfo.did = paramLong;
    localSharingLocationInfo.mid = paramInt1;
    localSharingLocationInfo.period = paramInt2;
    localSharingLocationInfo.messageObject = new MessageObject(this.currentAccount, paramMessage, false);
    localSharingLocationInfo.stopTime = (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + paramInt2);
    paramMessage = (SharingLocationInfo)this.sharingLocationsMap.get(paramLong);
    this.sharingLocationsMap.put(paramLong, localSharingLocationInfo);
    if (paramMessage != null) {
      this.sharingLocations.remove(paramMessage);
    }
    this.sharingLocations.add(localSharingLocationInfo);
    saveSharingLocation(localSharingLocationInfo, 0);
    this.lastLocationSendTime = (System.currentTimeMillis() - 90000L + 5000L);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (paramMessage != null) {
          LocationController.this.sharingLocationsUI.remove(paramMessage);
        }
        LocationController.this.sharingLocationsUI.add(localSharingLocationInfo);
        LocationController.this.sharingLocationsMapUI.put(localSharingLocationInfo.did, localSharingLocationInfo);
        LocationController.this.startService();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
      }
    });
  }
  
  public void cleanup()
  {
    this.sharingLocationsUI.clear();
    this.sharingLocationsMapUI.clear();
    this.locationsCache.clear();
    this.cacheRequests.clear();
    stopService();
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        LocationController.this.requests.clear();
        LocationController.this.sharingLocationsMap.clear();
        LocationController.this.sharingLocations.clear();
        LocationController.access$102(LocationController.this, null);
        LocationController.this.stop(true);
      }
    });
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    long l;
    if (paramInt1 == NotificationCenter.didReceivedNewMessages)
    {
      l = ((Long)paramVarArgs[0]).longValue();
      if (isSharingLocation(l)) {}
    }
    for (;;)
    {
      return;
      Object localObject1 = (ArrayList)this.locationsCache.get(l);
      if (localObject1 != null)
      {
        paramVarArgs = (ArrayList)paramVarArgs[1];
        paramInt2 = 0;
        paramInt1 = 0;
        Object localObject2;
        int i;
        int k;
        if (paramInt1 < paramVarArgs.size())
        {
          localObject2 = (MessageObject)paramVarArgs.get(paramInt1);
          int j;
          if (((MessageObject)localObject2).isLiveLocation())
          {
            i = 1;
            j = 0;
          }
          for (paramInt2 = 0;; paramInt2++)
          {
            k = j;
            if (paramInt2 < ((ArrayList)localObject1).size())
            {
              if (((TLRPC.Message)((ArrayList)localObject1).get(paramInt2)).from_id == ((MessageObject)localObject2).messageOwner.from_id)
              {
                k = 1;
                ((ArrayList)localObject1).set(paramInt2, ((MessageObject)localObject2).messageOwner);
              }
            }
            else
            {
              paramInt2 = i;
              if (k == 0)
              {
                ((ArrayList)localObject1).add(((MessageObject)localObject2).messageOwner);
                paramInt2 = i;
              }
              paramInt1++;
              break;
            }
          }
        }
        if (paramInt2 != 0)
        {
          NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, new Object[] { Long.valueOf(l), Integer.valueOf(this.currentAccount) });
          continue;
          if (paramInt1 == NotificationCenter.messagesDeleted)
          {
            if (!this.sharingLocationsUI.isEmpty())
            {
              localObject2 = (ArrayList)paramVarArgs[0];
              k = ((Integer)paramVarArgs[1]).intValue();
              paramVarArgs = null;
              paramInt1 = 0;
              if (paramInt1 < this.sharingLocationsUI.size())
              {
                SharingLocationInfo localSharingLocationInfo = (SharingLocationInfo)this.sharingLocationsUI.get(paramInt1);
                if (localSharingLocationInfo.messageObject != null)
                {
                  paramInt2 = localSharingLocationInfo.messageObject.getChannelId();
                  label296:
                  if (k == paramInt2) {
                    break label319;
                  }
                  localObject1 = paramVarArgs;
                }
                for (;;)
                {
                  paramInt1++;
                  paramVarArgs = (Object[])localObject1;
                  break;
                  paramInt2 = 0;
                  break label296;
                  label319:
                  localObject1 = paramVarArgs;
                  if (((ArrayList)localObject2).contains(Integer.valueOf(localSharingLocationInfo.mid)))
                  {
                    localObject1 = paramVarArgs;
                    if (paramVarArgs == null) {
                      localObject1 = new ArrayList();
                    }
                    ((ArrayList)localObject1).add(Long.valueOf(localSharingLocationInfo.did));
                  }
                }
              }
              if (paramVarArgs != null) {
                for (paramInt1 = 0; paramInt1 < paramVarArgs.size(); paramInt1++) {
                  removeSharingLocation(((Long)paramVarArgs.get(paramInt1)).longValue());
                }
              }
            }
          }
          else if (paramInt1 == NotificationCenter.replaceMessagesObjects)
          {
            l = ((Long)paramVarArgs[0]).longValue();
            if (isSharingLocation(l))
            {
              localObject1 = (ArrayList)this.locationsCache.get(l);
              if (localObject1 != null)
              {
                paramInt2 = 0;
                paramVarArgs = (ArrayList)paramVarArgs[1];
                paramInt1 = 0;
                if (paramInt1 < paramVarArgs.size())
                {
                  localObject2 = (MessageObject)paramVarArgs.get(paramInt1);
                  for (i = 0;; i++)
                  {
                    k = paramInt2;
                    if (i < ((ArrayList)localObject1).size())
                    {
                      if (((TLRPC.Message)((ArrayList)localObject1).get(i)).from_id != ((MessageObject)localObject2).messageOwner.from_id) {
                        continue;
                      }
                      if (((MessageObject)localObject2).isLiveLocation()) {
                        break label549;
                      }
                      ((ArrayList)localObject1).remove(i);
                    }
                    for (;;)
                    {
                      k = 1;
                      paramInt1++;
                      paramInt2 = k;
                      break;
                      label549:
                      ((ArrayList)localObject1).set(i, ((MessageObject)localObject2).messageOwner);
                    }
                  }
                }
                if (paramInt2 != 0) {
                  NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, new Object[] { Long.valueOf(l), Integer.valueOf(this.currentAccount) });
                }
              }
            }
          }
        }
      }
    }
  }
  
  public SharingLocationInfo getSharingLocationInfo(long paramLong)
  {
    return (SharingLocationInfo)this.sharingLocationsMapUI.get(paramLong);
  }
  
  public boolean isSharingLocation(long paramLong)
  {
    if (this.sharingLocationsMapUI.indexOfKey(paramLong) >= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public void loadLiveLocations(final long paramLong)
  {
    if (this.cacheRequests.indexOfKey(paramLong) >= 0) {}
    for (;;)
    {
      return;
      this.cacheRequests.put(paramLong, Boolean.valueOf(true));
      TLRPC.TL_messages_getRecentLocations localTL_messages_getRecentLocations = new TLRPC.TL_messages_getRecentLocations();
      localTL_messages_getRecentLocations.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int)paramLong);
      localTL_messages_getRecentLocations.limit = 100;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getRecentLocations, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error != null) {}
          for (;;)
          {
            return;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                LocationController.this.cacheRequests.delete(LocationController.10.this.val$did);
                TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)paramAnonymousTLObject;
                int j;
                for (int i = 0; i < localmessages_Messages.messages.size(); i = j + 1)
                {
                  j = i;
                  if (!(((TLRPC.Message)localmessages_Messages.messages.get(i)).media instanceof TLRPC.TL_messageMediaGeoLive))
                  {
                    localmessages_Messages.messages.remove(i);
                    j = i - 1;
                  }
                }
                MessagesStorage.getInstance(LocationController.this.currentAccount).putUsersAndChats(localmessages_Messages.users, localmessages_Messages.chats, true, true);
                MessagesController.getInstance(LocationController.this.currentAccount).putUsers(localmessages_Messages.users, false);
                MessagesController.getInstance(LocationController.this.currentAccount).putChats(localmessages_Messages.chats, false);
                LocationController.this.locationsCache.put(LocationController.10.this.val$did, localmessages_Messages.messages);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsCacheChanged, new Object[] { Long.valueOf(LocationController.10.this.val$did), Integer.valueOf(LocationController.this.currentAccount) });
              }
            });
          }
        }
      });
    }
  }
  
  public void removeAllLocationSharings()
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        for (int i = 0; i < LocationController.this.sharingLocations.size(); i++)
        {
          LocationController.SharingLocationInfo localSharingLocationInfo = (LocationController.SharingLocationInfo)LocationController.this.sharingLocations.get(i);
          TLRPC.TL_messages_editMessage localTL_messages_editMessage = new TLRPC.TL_messages_editMessage();
          localTL_messages_editMessage.peer = MessagesController.getInstance(LocationController.this.currentAccount).getInputPeer((int)localSharingLocationInfo.did);
          localTL_messages_editMessage.id = localSharingLocationInfo.mid;
          localTL_messages_editMessage.stop_geo_live = true;
          ConnectionsManager.getInstance(LocationController.this.currentAccount).sendRequest(localTL_messages_editMessage, new RequestDelegate()
          {
            public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              if (paramAnonymous2TL_error != null) {}
              for (;;)
              {
                return;
                MessagesController.getInstance(LocationController.this.currentAccount).processUpdates((TLRPC.Updates)paramAnonymous2TLObject, false);
              }
            }
          });
        }
        LocationController.this.sharingLocations.clear();
        LocationController.this.sharingLocationsMap.clear();
        LocationController.this.saveSharingLocation(null, 2);
        LocationController.this.stop(true);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            LocationController.this.sharingLocationsUI.clear();
            LocationController.this.sharingLocationsMapUI.clear();
            LocationController.this.stopService();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
          }
        });
      }
    });
  }
  
  public void removeSharingLocation(final long paramLong)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final LocationController.SharingLocationInfo localSharingLocationInfo = (LocationController.SharingLocationInfo)LocationController.this.sharingLocationsMap.get(paramLong);
        LocationController.this.sharingLocationsMap.remove(paramLong);
        if (localSharingLocationInfo != null)
        {
          TLRPC.TL_messages_editMessage localTL_messages_editMessage = new TLRPC.TL_messages_editMessage();
          localTL_messages_editMessage.peer = MessagesController.getInstance(LocationController.this.currentAccount).getInputPeer((int)localSharingLocationInfo.did);
          localTL_messages_editMessage.id = localSharingLocationInfo.mid;
          localTL_messages_editMessage.stop_geo_live = true;
          ConnectionsManager.getInstance(LocationController.this.currentAccount).sendRequest(localTL_messages_editMessage, new RequestDelegate()
          {
            public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              if (paramAnonymous2TL_error != null) {}
              for (;;)
              {
                return;
                MessagesController.getInstance(LocationController.this.currentAccount).processUpdates((TLRPC.Updates)paramAnonymous2TLObject, false);
              }
            }
          });
          LocationController.this.sharingLocations.remove(localSharingLocationInfo);
          LocationController.this.saveSharingLocation(localSharingLocationInfo, 1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              LocationController.this.sharingLocationsUI.remove(localSharingLocationInfo);
              LocationController.this.sharingLocationsMapUI.remove(localSharingLocationInfo.did);
              if (LocationController.this.sharingLocationsUI.isEmpty()) {
                LocationController.this.stopService();
              }
              NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
            }
          });
          if (LocationController.this.sharingLocations.isEmpty()) {
            LocationController.this.stop(true);
          }
        }
      }
    });
  }
  
  public void setGoogleMapLocation(Location paramLocation, boolean paramBoolean)
  {
    if (paramLocation == null) {
      return;
    }
    this.lastLocationByGoogleMaps = true;
    if ((paramBoolean) || ((this.lastKnownLocation != null) && (this.lastKnownLocation.distanceTo(paramLocation) >= 20.0F)))
    {
      this.lastLocationSendTime = (System.currentTimeMillis() - 90000L);
      this.locationSentSinceLastGoogleMapUpdate = false;
    }
    for (;;)
    {
      this.lastKnownLocation = paramLocation;
      break;
      if (this.locationSentSinceLastGoogleMapUpdate)
      {
        this.lastLocationSendTime = (System.currentTimeMillis() - 90000L + 20000L);
        this.locationSentSinceLastGoogleMapUpdate = false;
      }
    }
  }
  
  protected void update()
  {
    if (this.sharingLocations.isEmpty()) {}
    for (;;)
    {
      return;
      int k;
      for (int i = 0; i < this.sharingLocations.size(); i = k + 1)
      {
        final SharingLocationInfo localSharingLocationInfo = (SharingLocationInfo)this.sharingLocations.get(i);
        int j = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        k = i;
        if (localSharingLocationInfo.stopTime <= j)
        {
          this.sharingLocations.remove(i);
          this.sharingLocationsMap.remove(localSharingLocationInfo.did);
          saveSharingLocation(localSharingLocationInfo, 1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              LocationController.this.sharingLocationsUI.remove(localSharingLocationInfo);
              LocationController.this.sharingLocationsMapUI.remove(localSharingLocationInfo.did);
              if (LocationController.this.sharingLocationsUI.isEmpty()) {
                LocationController.this.stopService();
              }
              NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.liveLocationsChanged, new Object[0]);
            }
          });
          k = i - 1;
        }
      }
      if (!this.started)
      {
        if (Math.abs(this.lastLocationSendTime - System.currentTimeMillis()) > 90000L)
        {
          this.lastLocationStartTime = System.currentTimeMillis();
          start();
        }
      }
      else if ((this.lastLocationByGoogleMaps) || (Math.abs(this.lastLocationStartTime - System.currentTimeMillis()) > 10000L))
      {
        this.lastLocationByGoogleMaps = false;
        this.locationSentSinceLastGoogleMapUpdate = true;
        this.lastLocationSendTime = System.currentTimeMillis();
        broadcastLastKnownLocation();
      }
    }
  }
  
  private class GpsLocationListener
    implements LocationListener
  {
    private GpsLocationListener() {}
    
    public void onLocationChanged(Location paramLocation)
    {
      if (paramLocation == null) {}
      for (;;)
      {
        return;
        if ((LocationController.this.lastKnownLocation != null) && ((this == LocationController.this.networkLocationListener) || (this == LocationController.this.passiveLocationListener)))
        {
          if ((!LocationController.this.started) && (paramLocation.distanceTo(LocationController.this.lastKnownLocation) > 20.0F))
          {
            LocationController.access$102(LocationController.this, paramLocation);
            LocationController.access$502(LocationController.this, System.currentTimeMillis() - 90000L + 5000L);
          }
        }
        else {
          LocationController.access$102(LocationController.this, paramLocation);
        }
      }
    }
    
    public void onProviderDisabled(String paramString) {}
    
    public void onProviderEnabled(String paramString) {}
    
    public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle) {}
  }
  
  public static class SharingLocationInfo
  {
    public long did;
    public MessageObject messageObject;
    public int mid;
    public int period;
    public int stopTime;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/LocationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */