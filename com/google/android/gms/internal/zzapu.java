package com.google.android.gms.internal;

import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class zzapu
  extends zzaot<Time>
{
  public static final zzaou bmp = new zzaou()
  {
    public <T> zzaot<T> zza(zzaob paramAnonymouszzaob, zzapx<T> paramAnonymouszzapx)
    {
      if (paramAnonymouszzapx.by() == Time.class) {
        return new zzapu();
      }
      return null;
    }
  };
  private final DateFormat bmP = new SimpleDateFormat("hh:mm:ss a");
  
  public void zza(zzaqa paramzzaqa, Time paramTime)
    throws IOException
  {
    if (paramTime == null) {}
    for (paramTime = null;; paramTime = this.bmP.format(paramTime)) {
      try
      {
        paramzzaqa.zzut(paramTime);
        return;
      }
      finally {}
    }
  }
  
  /* Error */
  public Time zzn(zzapy paramzzapy)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_1
    //   3: invokevirtual 65	com/google/android/gms/internal/zzapy:bn	()Lcom/google/android/gms/internal/zzapz;
    //   6: getstatic 71	com/google/android/gms/internal/zzapz:bos	Lcom/google/android/gms/internal/zzapz;
    //   9: if_acmpne +13 -> 22
    //   12: aload_1
    //   13: invokevirtual 74	com/google/android/gms/internal/zzapy:nextNull	()V
    //   16: aconst_null
    //   17: astore_1
    //   18: aload_0
    //   19: monitorexit
    //   20: aload_1
    //   21: areturn
    //   22: new 35	java/sql/Time
    //   25: dup
    //   26: aload_0
    //   27: getfield 29	com/google/android/gms/internal/zzapu:bmP	Ljava/text/DateFormat;
    //   30: aload_1
    //   31: invokevirtual 78	com/google/android/gms/internal/zzapy:nextString	()Ljava/lang/String;
    //   34: invokevirtual 82	java/text/DateFormat:parse	(Ljava/lang/String;)Ljava/util/Date;
    //   37: invokevirtual 88	java/util/Date:getTime	()J
    //   40: invokespecial 91	java/sql/Time:<init>	(J)V
    //   43: astore_1
    //   44: goto -26 -> 18
    //   47: astore_1
    //   48: new 93	com/google/android/gms/internal/zzaoq
    //   51: dup
    //   52: aload_1
    //   53: invokespecial 96	com/google/android/gms/internal/zzaoq:<init>	(Ljava/lang/Throwable;)V
    //   56: athrow
    //   57: astore_1
    //   58: aload_0
    //   59: monitorexit
    //   60: aload_1
    //   61: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	this	zzapu
    //   0	62	1	paramzzapy	zzapy
    // Exception table:
    //   from	to	target	type
    //   22	44	47	java/text/ParseException
    //   2	16	57	finally
    //   22	44	57	finally
    //   48	57	57	finally
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */