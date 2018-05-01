package com.google.android.gms.common.data;

import android.content.ContentValues;
import android.database.CursorIndexOutOfBoundsException;
import android.database.CursorWindow;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepName;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@KeepName
public final class DataHolder
  extends AbstractSafeParcelable
  implements Closeable
{
  public static final Parcelable.Creator<DataHolder> CREATOR = new DataHolderCreator();
  private static final Builder zznt = new zza(new String[0], null);
  private boolean mClosed = false;
  private final int zzal;
  private final int zzam;
  private final String[] zznm;
  private Bundle zznn;
  private final CursorWindow[] zzno;
  private final Bundle zznp;
  private int[] zznq;
  private int zznr;
  private boolean zzns = true;
  
  DataHolder(int paramInt1, String[] paramArrayOfString, CursorWindow[] paramArrayOfCursorWindow, int paramInt2, Bundle paramBundle)
  {
    this.zzal = paramInt1;
    this.zznm = paramArrayOfString;
    this.zzno = paramArrayOfCursorWindow;
    this.zzam = paramInt2;
    this.zznp = paramBundle;
  }
  
  private DataHolder(Builder paramBuilder, int paramInt, Bundle paramBundle)
  {
    this(Builder.zza(paramBuilder), zza(paramBuilder, -1), paramInt, paramBundle);
  }
  
  public DataHolder(String[] paramArrayOfString, CursorWindow[] paramArrayOfCursorWindow, int paramInt, Bundle paramBundle)
  {
    this.zzal = 1;
    this.zznm = ((String[])Preconditions.checkNotNull(paramArrayOfString));
    this.zzno = ((CursorWindow[])Preconditions.checkNotNull(paramArrayOfCursorWindow));
    this.zzam = paramInt;
    this.zznp = paramBundle;
    validateContents();
  }
  
  public static Builder builder(String[] paramArrayOfString)
  {
    return new Builder(paramArrayOfString, null, null);
  }
  
  private final void zza(String paramString, int paramInt)
  {
    if ((this.zznn == null) || (!this.zznn.containsKey(paramString)))
    {
      paramString = String.valueOf(paramString);
      if (paramString.length() != 0) {}
      for (paramString = "No such column: ".concat(paramString);; paramString = new String("No such column: ")) {
        throw new IllegalArgumentException(paramString);
      }
    }
    if (isClosed()) {
      throw new IllegalArgumentException("Buffer is closed.");
    }
    if ((paramInt < 0) || (paramInt >= this.zznr)) {
      throw new CursorIndexOutOfBoundsException(paramInt, this.zznr);
    }
  }
  
  private static CursorWindow[] zza(Builder paramBuilder, int paramInt)
  {
    int i = 0;
    if (Builder.zza(paramBuilder).length == 0) {}
    label37:
    ArrayList localArrayList;
    for (paramBuilder = new CursorWindow[0];; paramBuilder = (CursorWindow[])localArrayList.toArray(new CursorWindow[localArrayList.size()])) {
      for (;;)
      {
        return paramBuilder;
        Object localObject1;
        Object localObject2;
        int k;
        Object localObject3;
        if ((paramInt < 0) || (paramInt >= Builder.zzb(paramBuilder).size()))
        {
          localObject1 = Builder.zzb(paramBuilder);
          int j = ((List)localObject1).size();
          localObject2 = new CursorWindow(false);
          localArrayList = new ArrayList();
          localArrayList.add(localObject2);
          ((CursorWindow)localObject2).setNumColumns(Builder.zza(paramBuilder).length);
          paramInt = 0;
          k = 0;
          if (paramInt < j) {
            localObject3 = localObject2;
          }
        }
        else
        {
          try
          {
            if (!((CursorWindow)localObject2).allocRow())
            {
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>(72);
              Log.d("DataHolder", "Allocating additional cursor window for large data set (row " + paramInt + ")");
              localObject2 = new android/database/CursorWindow;
              ((CursorWindow)localObject2).<init>(false);
              ((CursorWindow)localObject2).setStartPosition(paramInt);
              ((CursorWindow)localObject2).setNumColumns(Builder.zza(paramBuilder).length);
              localArrayList.add(localObject2);
              localObject3 = localObject2;
              if (!((CursorWindow)localObject2).allocRow())
              {
                Log.e("DataHolder", "Unable to allocate row to hold data.");
                localArrayList.remove(localObject2);
                paramBuilder = (CursorWindow[])localArrayList.toArray(new CursorWindow[localArrayList.size()]);
                continue;
                localObject1 = Builder.zzb(paramBuilder).subList(0, paramInt);
                break label37;
              }
            }
            Map localMap = (Map)((List)localObject1).get(paramInt);
            m = 0;
            bool = true;
            if ((m < Builder.zza(paramBuilder).length) && (bool))
            {
              localObject2 = Builder.zza(paramBuilder)[m];
              Object localObject4 = localMap.get(localObject2);
              if (localObject4 == null) {
                bool = ((CursorWindow)localObject3).putNull(paramInt, m);
              }
              for (;;)
              {
                m++;
                break;
                if ((localObject4 instanceof String))
                {
                  bool = ((CursorWindow)localObject3).putString((String)localObject4, paramInt, m);
                }
                else if ((localObject4 instanceof Long))
                {
                  bool = ((CursorWindow)localObject3).putLong(((Long)localObject4).longValue(), paramInt, m);
                }
                else if ((localObject4 instanceof Integer))
                {
                  bool = ((CursorWindow)localObject3).putLong(((Integer)localObject4).intValue(), paramInt, m);
                }
                else
                {
                  if ((localObject4 instanceof Boolean))
                  {
                    if (((Boolean)localObject4).booleanValue()) {}
                    for (long l = 1L;; l = 0L)
                    {
                      bool = ((CursorWindow)localObject3).putLong(l, paramInt, m);
                      break;
                    }
                  }
                  if ((localObject4 instanceof byte[]))
                  {
                    bool = ((CursorWindow)localObject3).putBlob((byte[])localObject4, paramInt, m);
                  }
                  else if ((localObject4 instanceof Double))
                  {
                    bool = ((CursorWindow)localObject3).putDouble(((Double)localObject4).doubleValue(), paramInt, m);
                  }
                  else
                  {
                    if (!(localObject4 instanceof Float)) {
                      break label527;
                    }
                    bool = ((CursorWindow)localObject3).putDouble(((Float)localObject4).floatValue(), paramInt, m);
                  }
                }
              }
              label527:
              paramBuilder = new java/lang/IllegalArgumentException;
              localObject1 = String.valueOf(localObject4);
              k = String.valueOf(localObject2).length();
              paramInt = String.valueOf(localObject1).length();
              localObject3 = new java/lang/StringBuilder;
              ((StringBuilder)localObject3).<init>(k + 32 + paramInt);
              paramBuilder.<init>("Unsupported object for column " + (String)localObject2 + ": " + (String)localObject1);
              throw paramBuilder;
            }
          }
          catch (RuntimeException paramBuilder)
          {
            int m;
            boolean bool;
            k = localArrayList.size();
            paramInt = i;
            while (paramInt < k)
            {
              ((CursorWindow)localArrayList.get(paramInt)).close();
              paramInt++;
              continue;
              if (!bool)
              {
                if (k != 0)
                {
                  paramBuilder = new com/google/android/gms/common/data/DataHolder$DataHolderException;
                  paramBuilder.<init>("Could not add the value to a new CursorWindow. The size of value may be larger than what a CursorWindow can handle.");
                  throw paramBuilder;
                }
                localObject2 = new java/lang/StringBuilder;
                ((StringBuilder)localObject2).<init>(74);
                Log.d("DataHolder", "Couldn't populate window data for row " + paramInt + " - allocating new window.");
                ((CursorWindow)localObject3).freeLastRow();
                localObject3 = new android/database/CursorWindow;
                ((CursorWindow)localObject3).<init>(false);
                ((CursorWindow)localObject3).setStartPosition(paramInt);
                ((CursorWindow)localObject3).setNumColumns(Builder.zza(paramBuilder).length);
                localArrayList.add(localObject3);
                k = paramInt - 1;
              }
              for (paramInt = 1;; paramInt = m)
              {
                m = k + 1;
                k = paramInt;
                paramInt = m;
                localObject2 = localObject3;
                break;
                m = 0;
                k = paramInt;
              }
            }
            throw paramBuilder;
          }
        }
      }
    }
  }
  
  public final void close()
  {
    try
    {
      if (!this.mClosed)
      {
        this.mClosed = true;
        for (int i = 0; i < this.zzno.length; i++) {
          this.zzno[i].close();
        }
      }
      return;
    }
    finally {}
  }
  
  protected final void finalize()
    throws Throwable
  {
    try
    {
      if ((this.zzns) && (this.zzno.length > 0) && (!isClosed()))
      {
        close();
        String str = toString();
        int i = String.valueOf(str).length();
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>(i + 178);
        Log.e("DataBuffer", "Internal data leak within a DataBuffer object detected!  Be sure to explicitly call release() on all DataBuffer extending objects when you are done with them. (internal object: " + str + ")");
      }
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public final byte[] getByteArray(String paramString, int paramInt1, int paramInt2)
  {
    zza(paramString, paramInt1);
    return this.zzno[paramInt2].getBlob(paramInt1, this.zznn.getInt(paramString));
  }
  
  public final int getCount()
  {
    return this.zznr;
  }
  
  public final int getInteger(String paramString, int paramInt1, int paramInt2)
  {
    zza(paramString, paramInt1);
    return this.zzno[paramInt2].getInt(paramInt1, this.zznn.getInt(paramString));
  }
  
  public final Bundle getMetadata()
  {
    return this.zznp;
  }
  
  public final int getStatusCode()
  {
    return this.zzam;
  }
  
  public final String getString(String paramString, int paramInt1, int paramInt2)
  {
    zza(paramString, paramInt1);
    return this.zzno[paramInt2].getString(paramInt1, this.zznn.getInt(paramString));
  }
  
  public final int getWindowIndex(int paramInt)
  {
    int i = 0;
    boolean bool;
    if ((paramInt >= 0) && (paramInt < this.zznr))
    {
      bool = true;
      Preconditions.checkState(bool);
    }
    for (;;)
    {
      int j = i;
      if (i < this.zznq.length)
      {
        if (paramInt < this.zznq[i]) {
          j = i - 1;
        }
      }
      else
      {
        paramInt = j;
        if (j == this.zznq.length) {
          paramInt = j - 1;
        }
        return paramInt;
        bool = false;
        break;
      }
      i++;
    }
  }
  
  public final boolean isClosed()
  {
    try
    {
      boolean bool = this.mClosed;
      return bool;
    }
    finally {}
  }
  
  public final void validateContents()
  {
    this.zznn = new Bundle();
    for (int i = 0; i < this.zznm.length; i++) {
      this.zznn.putInt(this.zznm[i], i);
    }
    this.zznq = new int[this.zzno.length];
    i = 0;
    int j = 0;
    while (i < this.zzno.length)
    {
      this.zznq[i] = j;
      int k = this.zzno[i].getStartPosition();
      j += this.zzno[i].getNumRows() - (j - k);
      i++;
    }
    this.zznr = j;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeStringArray(paramParcel, 1, this.zznm, false);
    SafeParcelWriter.writeTypedArray(paramParcel, 2, this.zzno, paramInt, false);
    SafeParcelWriter.writeInt(paramParcel, 3, getStatusCode());
    SafeParcelWriter.writeBundle(paramParcel, 4, getMetadata(), false);
    SafeParcelWriter.writeInt(paramParcel, 1000, this.zzal);
    SafeParcelWriter.finishObjectHeader(paramParcel, i);
    if ((paramInt & 0x1) != 0) {
      close();
    }
  }
  
  public static class Builder
  {
    private final String[] zznm;
    private final ArrayList<HashMap<String, Object>> zznu;
    private final String zznv;
    private final HashMap<Object, Integer> zznw;
    private boolean zznx;
    private String zzny;
    
    private Builder(String[] paramArrayOfString, String paramString)
    {
      this.zznm = ((String[])Preconditions.checkNotNull(paramArrayOfString));
      this.zznu = new ArrayList();
      this.zznv = paramString;
      this.zznw = new HashMap();
      this.zznx = false;
      this.zzny = null;
    }
    
    public DataHolder build(int paramInt)
    {
      return new DataHolder(this, paramInt, null, null);
    }
    
    public Builder withRow(ContentValues paramContentValues)
    {
      Asserts.checkNotNull(paramContentValues);
      HashMap localHashMap = new HashMap(paramContentValues.size());
      Iterator localIterator = paramContentValues.valueSet().iterator();
      while (localIterator.hasNext())
      {
        paramContentValues = (Map.Entry)localIterator.next();
        localHashMap.put((String)paramContentValues.getKey(), paramContentValues.getValue());
      }
      return withRow(localHashMap);
    }
    
    public Builder withRow(HashMap<String, Object> paramHashMap)
    {
      Asserts.checkNotNull(paramHashMap);
      int i;
      if (this.zznv == null)
      {
        i = -1;
        if (i != -1) {
          break label103;
        }
        this.zznu.add(paramHashMap);
      }
      for (;;)
      {
        this.zznx = false;
        return this;
        Object localObject = paramHashMap.get(this.zznv);
        if (localObject == null)
        {
          i = -1;
          break;
        }
        Integer localInteger = (Integer)this.zznw.get(localObject);
        if (localInteger == null)
        {
          this.zznw.put(localObject, Integer.valueOf(this.zznu.size()));
          i = -1;
          break;
        }
        i = localInteger.intValue();
        break;
        label103:
        this.zznu.remove(i);
        this.zznu.add(i, paramHashMap);
      }
    }
  }
  
  public static class DataHolderException
    extends RuntimeException
  {
    public DataHolderException(String paramString)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/DataHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */