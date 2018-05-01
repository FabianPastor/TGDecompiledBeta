package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class zzatl
  extends zzauh
{
  private long zzbrw;
  private String zzbrx;
  private Boolean zzbry;
  
  zzatl(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  public String zzLS()
  {
    zzob();
    return Build.VERSION.RELEASE;
  }
  
  public long zzLT()
  {
    zzob();
    return this.zzbrw;
  }
  
  public String zzLU()
  {
    zzob();
    return this.zzbrx;
  }
  
  public boolean zzbL(Context paramContext)
  {
    if (this.zzbry == null)
    {
      zzKn().zzLh();
      this.zzbry = Boolean.valueOf(false);
    }
    try
    {
      paramContext = paramContext.getPackageManager();
      if (paramContext != null)
      {
        paramContext.getPackageInfo("com.google.android.gms", 128);
        this.zzbry = Boolean.valueOf(true);
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
    return this.zzbry.booleanValue();
  }
  
  public String zzkN()
  {
    zzob();
    return Build.MODEL;
  }
  
  protected void zzmS()
  {
    Object localObject1 = Calendar.getInstance();
    Object localObject2 = TimeUnit.MINUTES;
    int i = ((Calendar)localObject1).get(15);
    this.zzbrw = ((TimeUnit)localObject2).convert(((Calendar)localObject1).get(16) + i, TimeUnit.MILLISECONDS);
    localObject2 = Locale.getDefault();
    localObject1 = String.valueOf(((Locale)localObject2).getLanguage().toLowerCase(Locale.ENGLISH));
    localObject2 = String.valueOf(((Locale)localObject2).getCountry().toLowerCase(Locale.ENGLISH));
    this.zzbrx = (String.valueOf(localObject1).length() + 1 + String.valueOf(localObject2).length() + (String)localObject1 + "-" + (String)localObject2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */