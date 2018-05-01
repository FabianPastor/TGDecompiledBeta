package org.telegram.messenger.exoplayer2.metadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class Metadata
  implements Parcelable
{
  public static final Parcelable.Creator<Metadata> CREATOR = new Parcelable.Creator()
  {
    public Metadata createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Metadata(paramAnonymousParcel);
    }
    
    public Metadata[] newArray(int paramAnonymousInt)
    {
      return new Metadata[0];
    }
  };
  private final Entry[] entries;
  
  Metadata(Parcel paramParcel)
  {
    this.entries = new Entry[paramParcel.readInt()];
    for (int i = 0; i < this.entries.length; i++) {
      this.entries[i] = ((Entry)paramParcel.readParcelable(Entry.class.getClassLoader()));
    }
  }
  
  public Metadata(List<? extends Entry> paramList)
  {
    if (paramList != null)
    {
      this.entries = new Entry[paramList.size()];
      paramList.toArray(this.entries);
    }
    for (;;)
    {
      return;
      this.entries = new Entry[0];
    }
  }
  
  public Metadata(Entry... paramVarArgs)
  {
    Entry[] arrayOfEntry = paramVarArgs;
    if (paramVarArgs == null) {
      arrayOfEntry = new Entry[0];
    }
    this.entries = arrayOfEntry;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool;
    if (this == paramObject) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (Metadata)paramObject;
        bool = Arrays.equals(this.entries, ((Metadata)paramObject).entries);
      }
    }
  }
  
  public Entry get(int paramInt)
  {
    return this.entries[paramInt];
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(this.entries);
  }
  
  public int length()
  {
    return this.entries.length;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.entries.length);
    Entry[] arrayOfEntry = this.entries;
    int i = arrayOfEntry.length;
    for (paramInt = 0; paramInt < i; paramInt++) {
      paramParcel.writeParcelable(arrayOfEntry[paramInt], 0);
    }
  }
  
  public static abstract interface Entry
    extends Parcelable
  {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/Metadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */