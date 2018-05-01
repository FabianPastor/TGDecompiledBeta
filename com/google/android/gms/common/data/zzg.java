package com.google.android.gms.common.data;

import java.util.ArrayList;

public abstract class zzg<T>
  extends AbstractDataBuffer<T>
{
  private boolean zzfwo = false;
  private ArrayList<Integer> zzfwp;
  
  protected zzg(DataHolder paramDataHolder)
  {
    super(paramDataHolder);
  }
  
  private final void zzakb()
  {
    for (;;)
    {
      int i;
      String str2;
      try
      {
        if (this.zzfwo) {
          break label204;
        }
        int j = this.zzfqt.zzfwg;
        this.zzfwp = new ArrayList();
        if (j <= 0) {
          break label199;
        }
        this.zzfwp.add(Integer.valueOf(0));
        String str3 = zzaka();
        i = this.zzfqt.zzbz(0);
        String str1 = this.zzfqt.zzd(str3, 0, i);
        i = 1;
        if (i >= j) {
          break label199;
        }
        int k = this.zzfqt.zzbz(i);
        str2 = this.zzfqt.zzd(str3, i, k);
        if (str2 == null) {
          throw new NullPointerException(String.valueOf(str3).length() + 78 + "Missing value for markerColumn: " + str3 + ", at row: " + i + ", for window: " + k);
        }
      }
      finally {}
      if (!str2.equals(localObject1))
      {
        this.zzfwp.add(Integer.valueOf(i));
        Object localObject2 = str2;
        break label207;
        label199:
        this.zzfwo = true;
        label204:
        return;
      }
      label207:
      i += 1;
    }
  }
  
  private final int zzcc(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.zzfwp.size())) {
      throw new IllegalArgumentException(53 + "Position " + paramInt + " is out of bounds for this buffer");
    }
    return ((Integer)this.zzfwp.get(paramInt)).intValue();
  }
  
  public final T get(int paramInt)
  {
    zzakb();
    int k = zzcc(paramInt);
    int j;
    if ((paramInt < 0) || (paramInt == this.zzfwp.size()))
    {
      j = 0;
      return (T)zzl(k, j);
    }
    if (paramInt == this.zzfwp.size() - 1) {}
    for (int i = this.zzfqt.zzfwg - ((Integer)this.zzfwp.get(paramInt)).intValue();; i = ((Integer)this.zzfwp.get(paramInt + 1)).intValue() - ((Integer)this.zzfwp.get(paramInt)).intValue())
    {
      j = i;
      if (i != 1) {
        break;
      }
      paramInt = zzcc(paramInt);
      this.zzfqt.zzbz(paramInt);
      j = i;
      break;
    }
  }
  
  public int getCount()
  {
    zzakb();
    return this.zzfwp.size();
  }
  
  protected abstract String zzaka();
  
  protected abstract T zzl(int paramInt1, int paramInt2);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/data/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */