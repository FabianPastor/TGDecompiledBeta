package org.telegram.messenger.exoplayer2.drm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DrmInitData
  implements Parcelable, Comparator<SchemeData>
{
  public static final Parcelable.Creator<DrmInitData> CREATOR = new Parcelable.Creator()
  {
    public DrmInitData createFromParcel(Parcel paramAnonymousParcel)
    {
      return new DrmInitData(paramAnonymousParcel);
    }
    
    public DrmInitData[] newArray(int paramAnonymousInt)
    {
      return new DrmInitData[paramAnonymousInt];
    }
  };
  private int hashCode;
  public final int schemeDataCount;
  private final SchemeData[] schemeDatas;
  public final String schemeType;
  
  DrmInitData(Parcel paramParcel)
  {
    this.schemeType = paramParcel.readString();
    this.schemeDatas = ((SchemeData[])paramParcel.createTypedArray(SchemeData.CREATOR));
    this.schemeDataCount = this.schemeDatas.length;
  }
  
  public DrmInitData(String paramString, List<SchemeData> paramList)
  {
    this(paramString, false, (SchemeData[])paramList.toArray(new SchemeData[paramList.size()]));
  }
  
  private DrmInitData(String paramString, boolean paramBoolean, SchemeData... paramVarArgs)
  {
    this.schemeType = paramString;
    paramString = paramVarArgs;
    if (paramBoolean) {
      paramString = (SchemeData[])paramVarArgs.clone();
    }
    Arrays.sort(paramString, this);
    this.schemeDatas = paramString;
    this.schemeDataCount = paramString.length;
  }
  
  public DrmInitData(String paramString, SchemeData... paramVarArgs)
  {
    this(paramString, true, paramVarArgs);
  }
  
  public DrmInitData(List<SchemeData> paramList)
  {
    this(null, false, (SchemeData[])paramList.toArray(new SchemeData[paramList.size()]));
  }
  
  public DrmInitData(SchemeData... paramVarArgs)
  {
    this(null, paramVarArgs);
  }
  
  private static boolean containsSchemeDataWithUuid(ArrayList<SchemeData> paramArrayList, int paramInt, UUID paramUUID)
  {
    int i = 0;
    if (i < paramInt) {
      if (!((SchemeData)paramArrayList.get(i)).uuid.equals(paramUUID)) {}
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      i++;
      break;
    }
  }
  
  public static DrmInitData createSessionCreationData(DrmInitData paramDrmInitData1, DrmInitData paramDrmInitData2)
  {
    int i = 0;
    ArrayList localArrayList = new ArrayList();
    Object localObject1 = null;
    int j;
    int k;
    if (paramDrmInitData1 != null)
    {
      localObject2 = paramDrmInitData1.schemeType;
      paramDrmInitData1 = paramDrmInitData1.schemeDatas;
      j = paramDrmInitData1.length;
      for (k = 0;; k++)
      {
        localObject1 = localObject2;
        if (k >= j) {
          break;
        }
        localObject1 = paramDrmInitData1[k];
        if (((SchemeData)localObject1).hasData()) {
          localArrayList.add(localObject1);
        }
      }
    }
    Object localObject2 = localObject1;
    if (paramDrmInitData2 != null)
    {
      paramDrmInitData1 = (DrmInitData)localObject1;
      if (localObject1 == null) {
        paramDrmInitData1 = paramDrmInitData2.schemeType;
      }
      j = localArrayList.size();
      paramDrmInitData2 = paramDrmInitData2.schemeDatas;
      int m = paramDrmInitData2.length;
      for (k = i;; k++)
      {
        localObject2 = paramDrmInitData1;
        if (k >= m) {
          break;
        }
        localObject1 = paramDrmInitData2[k];
        if ((((SchemeData)localObject1).hasData()) && (!containsSchemeDataWithUuid(localArrayList, j, ((SchemeData)localObject1).uuid))) {
          localArrayList.add(localObject1);
        }
      }
    }
    if (localArrayList.isEmpty()) {}
    for (paramDrmInitData1 = null;; paramDrmInitData1 = new DrmInitData((String)localObject2, localArrayList)) {
      return paramDrmInitData1;
    }
  }
  
  public int compare(SchemeData paramSchemeData1, SchemeData paramSchemeData2)
  {
    int i;
    if (C.UUID_NIL.equals(paramSchemeData1.uuid)) {
      if (C.UUID_NIL.equals(paramSchemeData2.uuid)) {
        i = 0;
      }
    }
    for (;;)
    {
      return i;
      i = 1;
      continue;
      i = paramSchemeData1.uuid.compareTo(paramSchemeData2.uuid);
    }
  }
  
  public DrmInitData copyWithSchemeType(String paramString)
  {
    if (Util.areEqual(this.schemeType, paramString)) {}
    for (paramString = this;; paramString = new DrmInitData(paramString, false, this.schemeDatas)) {
      return paramString;
    }
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
        paramObject = (DrmInitData)paramObject;
        if ((!Util.areEqual(this.schemeType, ((DrmInitData)paramObject).schemeType)) || (!Arrays.equals(this.schemeDatas, ((DrmInitData)paramObject).schemeDatas))) {
          bool = false;
        }
      }
    }
  }
  
  public SchemeData get(int paramInt)
  {
    return this.schemeDatas[paramInt];
  }
  
  @Deprecated
  public SchemeData get(UUID paramUUID)
  {
    SchemeData[] arrayOfSchemeData = this.schemeDatas;
    int i = arrayOfSchemeData.length;
    int j = 0;
    SchemeData localSchemeData;
    if (j < i)
    {
      localSchemeData = arrayOfSchemeData[j];
      if (!localSchemeData.matches(paramUUID)) {}
    }
    for (paramUUID = localSchemeData;; paramUUID = null)
    {
      return paramUUID;
      j++;
      break;
    }
  }
  
  public int hashCode()
  {
    if (this.hashCode == 0) {
      if (this.schemeType != null) {
        break label37;
      }
    }
    label37:
    for (int i = 0;; i = this.schemeType.hashCode())
    {
      this.hashCode = (i * 31 + Arrays.hashCode(this.schemeDatas));
      return this.hashCode;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.schemeType);
    paramParcel.writeTypedArray(this.schemeDatas, 0);
  }
  
  public static final class SchemeData
    implements Parcelable
  {
    public static final Parcelable.Creator<SchemeData> CREATOR = new Parcelable.Creator()
    {
      public DrmInitData.SchemeData createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DrmInitData.SchemeData(paramAnonymousParcel);
      }
      
      public DrmInitData.SchemeData[] newArray(int paramAnonymousInt)
      {
        return new DrmInitData.SchemeData[paramAnonymousInt];
      }
    };
    public final byte[] data;
    private int hashCode;
    public final String mimeType;
    public final boolean requiresSecureDecryption;
    private final UUID uuid;
    
    SchemeData(Parcel paramParcel)
    {
      this.uuid = new UUID(paramParcel.readLong(), paramParcel.readLong());
      this.mimeType = paramParcel.readString();
      this.data = paramParcel.createByteArray();
      if (paramParcel.readByte() != 0) {}
      for (boolean bool = true;; bool = false)
      {
        this.requiresSecureDecryption = bool;
        return;
      }
    }
    
    public SchemeData(UUID paramUUID, String paramString, byte[] paramArrayOfByte)
    {
      this(paramUUID, paramString, paramArrayOfByte, false);
    }
    
    public SchemeData(UUID paramUUID, String paramString, byte[] paramArrayOfByte, boolean paramBoolean)
    {
      this.uuid = ((UUID)Assertions.checkNotNull(paramUUID));
      this.mimeType = ((String)Assertions.checkNotNull(paramString));
      this.data = paramArrayOfByte;
      this.requiresSecureDecryption = paramBoolean;
    }
    
    public boolean canReplace(SchemeData paramSchemeData)
    {
      if ((hasData()) && (!paramSchemeData.hasData()) && (matches(paramSchemeData.uuid))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool1 = true;
      boolean bool2;
      if (!(paramObject instanceof SchemeData)) {
        bool2 = false;
      }
      for (;;)
      {
        return bool2;
        bool2 = bool1;
        if (paramObject != this)
        {
          paramObject = (SchemeData)paramObject;
          if ((this.mimeType.equals(((SchemeData)paramObject).mimeType)) && (Util.areEqual(this.uuid, ((SchemeData)paramObject).uuid)))
          {
            bool2 = bool1;
            if (Arrays.equals(this.data, ((SchemeData)paramObject).data)) {}
          }
          else
          {
            bool2 = false;
          }
        }
      }
    }
    
    public boolean hasData()
    {
      if (this.data != null) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public int hashCode()
    {
      if (this.hashCode == 0) {
        this.hashCode = ((this.uuid.hashCode() * 31 + this.mimeType.hashCode()) * 31 + Arrays.hashCode(this.data));
      }
      return this.hashCode;
    }
    
    public boolean matches(UUID paramUUID)
    {
      if ((C.UUID_NIL.equals(this.uuid)) || (paramUUID.equals(this.uuid))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.uuid.getMostSignificantBits());
      paramParcel.writeLong(this.uuid.getLeastSignificantBits());
      paramParcel.writeString(this.mimeType);
      paramParcel.writeByteArray(this.data);
      if (this.requiresSecureDecryption) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeByte((byte)paramInt);
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/drm/DrmInitData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */