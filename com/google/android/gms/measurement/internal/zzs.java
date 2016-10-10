package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzvk.zzd;
import java.math.BigDecimal;

class zzs
{
  final int apJ;
  BigDecimal apK;
  BigDecimal apL;
  BigDecimal apM;
  final boolean apN;
  
  public zzs(zzvk.zzd paramzzd)
  {
    zzac.zzy(paramzzd);
    boolean bool = true;
    if ((paramzzd.asO == null) || (paramzzd.asO.intValue() == 0))
    {
      bool = false;
      if (!bool) {
        break label198;
      }
      this.apJ = paramzzd.asO.intValue();
      if (paramzzd.asO.intValue() != 4) {
        break label162;
      }
      if ((!zzal.zznj(paramzzd.asR)) || (!zzal.zznj(paramzzd.asS))) {
        bool = false;
      }
    }
    for (;;)
    {
      try
      {
        this.apL = new BigDecimal(paramzzd.asR);
        this.apM = new BigDecimal(paramzzd.asS);
        this.apN = bool;
        return;
        if (paramzzd.asO.intValue() != 4)
        {
          if (paramzzd.asQ != null) {
            break;
          }
          bool = false;
          break;
        }
        if ((paramzzd.asR != null) && (paramzzd.asS != null)) {
          break;
        }
        bool = false;
      }
      catch (NumberFormatException paramzzd)
      {
        bool = false;
        continue;
      }
      label162:
      if (!zzal.zznj(paramzzd.asQ)) {
        bool = false;
      }
      try
      {
        this.apK = new BigDecimal(paramzzd.asQ);
      }
      catch (NumberFormatException paramzzd)
      {
        bool = false;
      }
      continue;
      label198:
      this.apJ = 0;
    }
  }
  
  private Boolean zza(BigDecimal paramBigDecimal)
  {
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool1 = true;
    if (!this.apN) {
      return null;
    }
    if (paramBigDecimal == null) {
      return null;
    }
    switch (this.apJ)
    {
    default: 
      return null;
    case 1: 
      if (paramBigDecimal.compareTo(this.apK) == -1) {}
      for (;;)
      {
        return Boolean.valueOf(bool1);
        bool1 = false;
      }
    case 2: 
      if (paramBigDecimal.compareTo(this.apK) == 1) {}
      for (bool1 = bool2;; bool1 = false) {
        return Boolean.valueOf(bool1);
      }
    case 3: 
      if (paramBigDecimal.compareTo(this.apK) == 0) {}
      for (bool1 = bool3;; bool1 = false) {
        return Boolean.valueOf(bool1);
      }
    }
    if ((paramBigDecimal.compareTo(this.apL) != -1) && (paramBigDecimal.compareTo(this.apM) != 1)) {}
    for (bool1 = bool4;; bool1 = false) {
      return Boolean.valueOf(bool1);
    }
  }
  
  public Boolean zzbn(long paramLong)
  {
    try
    {
      Boolean localBoolean = zza(new BigDecimal(paramLong));
      return localBoolean;
    }
    catch (NumberFormatException localNumberFormatException) {}
    return null;
  }
  
  public Boolean zzj(double paramDouble)
  {
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    boolean bool1 = true;
    if (!this.apN) {
      return null;
    }
    try
    {
      localBigDecimal = new BigDecimal(paramDouble);
      switch (this.apJ)
      {
      case 1: 
        if (localBigDecimal.compareTo(this.apK) != -1) {
          break label232;
        }
        return Boolean.valueOf(bool1);
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      BigDecimal localBigDecimal;
      label98:
      return null;
    }
    if (localBigDecimal.compareTo(this.apK) == 1)
    {
      bool1 = bool2;
      return Boolean.valueOf(bool1);
      if ((localBigDecimal.compareTo(this.apK.subtract(new BigDecimal(Math.ulp(paramDouble)).multiply(new BigDecimal(2)))) != 1) || (localBigDecimal.compareTo(this.apK.add(new BigDecimal(Math.ulp(paramDouble)).multiply(new BigDecimal(2)))) != -1)) {
        break label242;
      }
    }
    label232:
    label242:
    for (bool1 = bool3;; bool1 = false)
    {
      return Boolean.valueOf(bool1);
      if ((localBigDecimal.compareTo(this.apL) != -1) && (localBigDecimal.compareTo(this.apM) != 1)) {}
      for (bool1 = bool4;; bool1 = false) {
        return Boolean.valueOf(bool1);
      }
      return null;
      bool1 = false;
      break;
      bool1 = false;
      break label98;
    }
  }
  
  public Boolean zzmk(String paramString)
  {
    if (!zzal.zznj(paramString)) {
      return null;
    }
    try
    {
      paramString = zza(new BigDecimal(paramString));
      return paramString;
    }
    catch (NumberFormatException paramString) {}
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */