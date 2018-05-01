package com.google.android.gms.internal;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class zzasw
  extends zzats
{
  private long zzbqD;
  private String zzbqE;
  private Boolean zzbqF;
  
  zzasw(zzatp paramzzatp)
  {
    super(paramzzatp);
  }
  
  public String zzKU()
  {
    zznA();
    return Build.VERSION.RELEASE;
  }
  
  public long zzKV()
  {
    zznA();
    return this.zzbqD;
  }
  
  public String zzKW()
  {
    zznA();
    return this.zzbqE;
  }
  
  public boolean zzbt(Context paramContext)
  {
    if (this.zzbqF == null)
    {
      zzJv().zzKk();
      this.zzbqF = Boolean.valueOf(false);
    }
    try
    {
      paramContext = paramContext.getPackageManager();
      if (paramContext != null)
      {
        paramContext.getPackageInfo("com.google.android.gms", 128);
        this.zzbqF = Boolean.valueOf(true);
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
    return this.zzbqF.booleanValue();
  }
  
  public String zzkm()
  {
    zznA();
    return Build.MODEL;
  }
  
  protected void zzmr()
  {
    Object localObject1 = Calendar.getInstance();
    Object localObject2 = TimeUnit.MINUTES;
    int i = ((Calendar)localObject1).get(15);
    this.zzbqD = ((TimeUnit)localObject2).convert(((Calendar)localObject1).get(16) + i, TimeUnit.MILLISECONDS);
    localObject2 = Locale.getDefault();
    localObject1 = String.valueOf(((Locale)localObject2).getLanguage().toLowerCase(Locale.ENGLISH));
    localObject2 = String.valueOf(((Locale)localObject2).getCountry().toLowerCase(Locale.ENGLISH));
    this.zzbqE = (String.valueOf(localObject1).length() + 1 + String.valueOf(localObject2).length() + (String)localObject1 + "-" + (String)localObject2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzasw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */