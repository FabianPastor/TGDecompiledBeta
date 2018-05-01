package org.telegram.messenger.exoplayer2.metadata.emsg;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.util.Util;

public final class EventMessage
  implements Metadata.Entry
{
  public static final Parcelable.Creator<EventMessage> CREATOR = new Parcelable.Creator()
  {
    public EventMessage createFromParcel(Parcel paramAnonymousParcel)
    {
      return new EventMessage(paramAnonymousParcel);
    }
    
    public EventMessage[] newArray(int paramAnonymousInt)
    {
      return new EventMessage[paramAnonymousInt];
    }
  };
  public final long durationMs;
  private int hashCode;
  public final long id;
  public final byte[] messageData;
  public final long presentationTimeUs;
  public final String schemeIdUri;
  public final String value;
  
  EventMessage(Parcel paramParcel)
  {
    this.schemeIdUri = paramParcel.readString();
    this.value = paramParcel.readString();
    this.presentationTimeUs = paramParcel.readLong();
    this.durationMs = paramParcel.readLong();
    this.id = paramParcel.readLong();
    this.messageData = paramParcel.createByteArray();
  }
  
  public EventMessage(String paramString1, String paramString2, long paramLong1, long paramLong2, byte[] paramArrayOfByte, long paramLong3)
  {
    this.schemeIdUri = paramString1;
    this.value = paramString2;
    this.durationMs = paramLong1;
    this.id = paramLong2;
    this.messageData = paramArrayOfByte;
    this.presentationTimeUs = paramLong3;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (EventMessage)paramObject;
        if ((this.presentationTimeUs != ((EventMessage)paramObject).presentationTimeUs) || (this.durationMs != ((EventMessage)paramObject).durationMs) || (this.id != ((EventMessage)paramObject).id) || (!Util.areEqual(this.schemeIdUri, ((EventMessage)paramObject).schemeIdUri)) || (!Util.areEqual(this.value, ((EventMessage)paramObject).value)) || (!Arrays.equals(this.messageData, ((EventMessage)paramObject).messageData))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    int i = 0;
    if (this.hashCode == 0) {
      if (this.schemeIdUri == null) {
        break label120;
      }
    }
    label120:
    for (int j = this.schemeIdUri.hashCode();; j = 0)
    {
      if (this.value != null) {
        i = this.value.hashCode();
      }
      this.hashCode = ((((((j + 527) * 31 + i) * 31 + (int)(this.presentationTimeUs ^ this.presentationTimeUs >>> 32)) * 31 + (int)(this.durationMs ^ this.durationMs >>> 32)) * 31 + (int)(this.id ^ this.id >>> 32)) * 31 + Arrays.hashCode(this.messageData));
      return this.hashCode;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.schemeIdUri);
    paramParcel.writeString(this.value);
    paramParcel.writeLong(this.presentationTimeUs);
    paramParcel.writeLong(this.durationMs);
    paramParcel.writeLong(this.id);
    paramParcel.writeByteArray(this.messageData);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/emsg/EventMessage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */