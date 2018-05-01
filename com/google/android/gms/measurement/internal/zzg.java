package com.google.android.gms.measurement.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class zzg
  extends zzaa
{
  private long arx;
  private String ary;
  private Boolean arz;
  
  zzg(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  public String zzbws()
  {
    zzacj();
    return Build.VERSION.RELEASE;
  }
  
  public long zzbwt()
  {
    zzacj();
    return this.arx;
  }
  
  public String zzbwu()
  {
    zzacj();
    return this.ary;
  }
  
  public boolean zzdp(Context paramContext)
  {
    if (this.arz == null)
    {
      zzbwd().zzayi();
      this.arz = Boolean.valueOf(false);
    }
    try
    {
      paramContext = paramContext.getPackageManager();
      if (paramContext != null)
      {
        paramContext.getPackageInfo("com.google.android.gms", 128);
        this.arz = Boolean.valueOf(true);
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
    return this.arz.booleanValue();
  }
  
  public String zzvt()
  {
    zzacj();
    return Build.MODEL;
  }
  
  protected void zzzy()
  {
    Object localObject1 = Calendar.getInstance();
    Object localObject2 = TimeUnit.MINUTES;
    int i = ((Calendar)localObject1).get(15);
    this.arx = ((TimeUnit)localObject2).convert(((Calendar)localObject1).get(16) + i, TimeUnit.MILLISECONDS);
    localObject2 = Locale.getDefault();
    localObject1 = String.valueOf(((Locale)localObject2).getLanguage().toLowerCase(Locale.ENGLISH));
    localObject2 = String.valueOf(((Locale)localObject2).getCountry().toLowerCase(Locale.ENGLISH));
    this.ary = (String.valueOf(localObject1).length() + 1 + String.valueOf(localObject2).length() + (String)localObject1 + "-" + (String)localObject2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */