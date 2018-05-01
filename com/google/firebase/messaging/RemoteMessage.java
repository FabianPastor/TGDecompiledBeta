package com.google.firebase.messaging;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class RemoteMessage
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<RemoteMessage> CREATOR = new zzf();
  Bundle zzafw;
  private Map<String, String> zzrr;
  
  public RemoteMessage(Bundle paramBundle)
  {
    this.zzafw = paramBundle;
  }
  
  public final Map<String, String> getData()
  {
    if (this.zzrr == null)
    {
      this.zzrr = new ArrayMap();
      Iterator localIterator = this.zzafw.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Object localObject = this.zzafw.get(str);
        if ((localObject instanceof String))
        {
          localObject = (String)localObject;
          if ((!str.startsWith("google.")) && (!str.startsWith("gcm.")) && (!str.equals("from")) && (!str.equals("message_type")) && (!str.equals("collapse_key"))) {
            this.zzrr.put(str, localObject);
          }
        }
      }
    }
    return this.zzrr;
  }
  
  public final String getFrom()
  {
    return this.zzafw.getString("from");
  }
  
  public final long getSentTime()
  {
    Object localObject = this.zzafw.get("google.sent_time");
    if ((localObject instanceof Long)) {}
    for (long l = ((Long)localObject).longValue();; l = 0L) {
      for (;;)
      {
        return l;
        if ((localObject instanceof String)) {
          try
          {
            l = Long.parseLong((String)localObject);
          }
          catch (NumberFormatException localNumberFormatException)
          {
            localObject = String.valueOf(localObject);
            Log.w("FirebaseMessaging", String.valueOf(localObject).length() + 19 + "Invalid sent time: " + (String)localObject);
          }
        }
      }
    }
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeBundle(paramParcel, 2, this.zzafw, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/messaging/RemoteMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */