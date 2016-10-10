package com.google.android.gms.common.data;

import java.util.ArrayList;

public abstract class zzf<T>
  extends AbstractDataBuffer<T>
{
  private boolean Ab = false;
  private ArrayList<Integer> Ac;
  
  protected zzf(DataHolder paramDataHolder)
  {
    super(paramDataHolder);
  }
  
  private void zzati()
  {
    for (;;)
    {
      int i;
      String str2;
      try
      {
        if (this.Ab) {
          break label204;
        }
        int j = this.xi.getCount();
        this.Ac = new ArrayList();
        if (j <= 0) {
          break label199;
        }
        this.Ac.add(Integer.valueOf(0));
        String str3 = zzath();
        i = this.xi.zzgb(0);
        String str1 = this.xi.zzd(str3, 0, i);
        i = 1;
        if (i >= j) {
          break label199;
        }
        int k = this.xi.zzgb(i);
        str2 = this.xi.zzd(str3, i, k);
        if (str2 == null) {
          throw new NullPointerException(String.valueOf(str3).length() + 78 + "Missing value for markerColumn: " + str3 + ", at row: " + i + ", for window: " + k);
        }
      }
      finally {}
      if (!str2.equals(localObject1))
      {
        this.Ac.add(Integer.valueOf(i));
        Object localObject2 = str2;
        break label207;
        label199:
        this.Ab = true;
        label204:
        return;
      }
      label207:
      i += 1;
    }
  }
  
  public final T get(int paramInt)
  {
    zzati();
    return (T)zzl(zzgf(paramInt), zzgg(paramInt));
  }
  
  public int getCount()
  {
    zzati();
    return this.Ac.size();
  }
  
  protected abstract String zzath();
  
  protected String zzatj()
  {
    return null;
  }
  
  int zzgf(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.Ac.size())) {
      throw new IllegalArgumentException(53 + "Position " + paramInt + " is out of bounds for this buffer");
    }
    return ((Integer)this.Ac.get(paramInt)).intValue();
  }
  
  protected int zzgg(int paramInt)
  {
    int j;
    if ((paramInt < 0) || (paramInt == this.Ac.size()))
    {
      j = 0;
      return j;
    }
    if (paramInt == this.Ac.size() - 1) {}
    for (int i = this.xi.getCount() - ((Integer)this.Ac.get(paramInt)).intValue();; i = ((Integer)this.Ac.get(paramInt + 1)).intValue() - ((Integer)this.Ac.get(paramInt)).intValue())
    {
      j = i;
      if (i != 1) {
        break;
      }
      paramInt = zzgf(paramInt);
      int k = this.xi.zzgb(paramInt);
      String str = zzatj();
      j = i;
      if (str == null) {
        break;
      }
      j = i;
      if (this.xi.zzd(str, paramInt, k) != null) {
        break;
      }
      return 0;
    }
  }
  
  protected abstract T zzl(int paramInt1, int paramInt2);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */