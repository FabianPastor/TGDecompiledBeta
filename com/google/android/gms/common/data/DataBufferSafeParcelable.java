package com.google.android.gms.common.data;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

public class DataBufferSafeParcelable<T extends SafeParcelable>
  extends AbstractDataBuffer<T>
{
  private static final String[] zznk = { "data" };
  private final Parcelable.Creator<T> zznl;
  
  public DataBufferSafeParcelable(DataHolder paramDataHolder, Parcelable.Creator<T> paramCreator)
  {
    super(paramDataHolder);
    this.zznl = paramCreator;
  }
  
  public static <T extends SafeParcelable> void addValue(DataHolder.Builder paramBuilder, T paramT)
  {
    Parcel localParcel = Parcel.obtain();
    paramT.writeToParcel(localParcel, 0);
    paramT = new ContentValues();
    paramT.put("data", localParcel.marshall());
    paramBuilder.withRow(paramT);
    localParcel.recycle();
  }
  
  public static DataHolder.Builder buildDataHolder()
  {
    return DataHolder.builder(zznk);
  }
  
  public T get(int paramInt)
  {
    Object localObject = this.mDataHolder.getByteArray("data", paramInt, this.mDataHolder.getWindowIndex(paramInt));
    Parcel localParcel = Parcel.obtain();
    localParcel.unmarshall((byte[])localObject, 0, localObject.length);
    localParcel.setDataPosition(0);
    localObject = (SafeParcelable)this.zznl.createFromParcel(localParcel);
    localParcel.recycle();
    return (T)localObject;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/DataBufferSafeParcelable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */