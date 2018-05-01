package com.google.android.gms.common.data;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;

public class DataBufferRef
{
  protected final DataHolder mDataHolder;
  protected int mDataRow;
  private int zznj;
  
  public DataBufferRef(DataHolder paramDataHolder, int paramInt)
  {
    this.mDataHolder = ((DataHolder)Preconditions.checkNotNull(paramDataHolder));
    setDataRow(paramInt);
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if ((paramObject instanceof DataBufferRef))
    {
      paramObject = (DataBufferRef)paramObject;
      bool2 = bool1;
      if (Objects.equal(Integer.valueOf(((DataBufferRef)paramObject).mDataRow), Integer.valueOf(this.mDataRow)))
      {
        bool2 = bool1;
        if (Objects.equal(Integer.valueOf(((DataBufferRef)paramObject).zznj), Integer.valueOf(this.zznj)))
        {
          bool2 = bool1;
          if (((DataBufferRef)paramObject).mDataHolder == this.mDataHolder) {
            bool2 = true;
          }
        }
      }
    }
    return bool2;
  }
  
  protected byte[] getByteArray(String paramString)
  {
    return this.mDataHolder.getByteArray(paramString, this.mDataRow, this.zznj);
  }
  
  protected int getInteger(String paramString)
  {
    return this.mDataHolder.getInteger(paramString, this.mDataRow, this.zznj);
  }
  
  protected String getString(String paramString)
  {
    return this.mDataHolder.getString(paramString, this.mDataRow, this.zznj);
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { Integer.valueOf(this.mDataRow), Integer.valueOf(this.zznj), this.mDataHolder });
  }
  
  protected void setDataRow(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.mDataHolder.getCount())) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool);
      this.mDataRow = paramInt;
      this.zznj = this.mDataHolder.getWindowIndex(this.mDataRow);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/DataBufferRef.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */