package com.google.android.gms.internal;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class zzcet
  extends zzchj
{
  private long zzbpC;
  private String zzbpD;
  
  zzcet(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
  }
  
  protected final void zzjD()
  {
    Object localObject1 = Calendar.getInstance();
    Object localObject2 = TimeUnit.MINUTES;
    int i = ((Calendar)localObject1).get(15);
    this.zzbpC = ((TimeUnit)localObject2).convert(((Calendar)localObject1).get(16) + i, TimeUnit.MILLISECONDS);
    localObject2 = Locale.getDefault();
    localObject1 = String.valueOf(((Locale)localObject2).getLanguage().toLowerCase(Locale.ENGLISH));
    localObject2 = String.valueOf(((Locale)localObject2).getCountry().toLowerCase(Locale.ENGLISH));
    this.zzbpD = (String.valueOf(localObject1).length() + 1 + String.valueOf(localObject2).length() + (String)localObject1 + "-" + (String)localObject2);
  }
  
  public final long zzyq()
  {
    zzkD();
    return this.zzbpC;
  }
  
  public final String zzyr()
  {
    zzkD();
    return this.zzbpD;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */