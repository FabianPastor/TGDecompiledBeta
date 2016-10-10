package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzvk.zzf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class zzag
{
  final boolean apN;
  final int asn;
  final boolean aso;
  final String asp;
  final List<String> asq;
  final String asr;
  
  public zzag(zzvk.zzf paramzzf)
  {
    zzac.zzy(paramzzf);
    boolean bool1;
    if ((paramzzf.asW == null) || (paramzzf.asW.intValue() == 0)) {
      bool1 = false;
    }
    for (;;)
    {
      if (bool1)
      {
        this.asn = paramzzf.asW.intValue();
        boolean bool2 = bool3;
        if (paramzzf.asY != null)
        {
          bool2 = bool3;
          if (paramzzf.asY.booleanValue()) {
            bool2 = true;
          }
        }
        this.aso = bool2;
        if ((this.aso) || (this.asn == 1) || (this.asn == 6))
        {
          this.asp = paramzzf.asX;
          label108:
          if (paramzzf.asZ != null) {
            break label205;
          }
          paramzzf = null;
          label117:
          this.asq = paramzzf;
          if (this.asn != 1) {
            break label221;
          }
          this.asr = this.asp;
        }
      }
      for (;;)
      {
        this.apN = bool1;
        return;
        if (paramzzf.asW.intValue() == 6)
        {
          if ((paramzzf.asZ != null) && (paramzzf.asZ.length != 0)) {
            break label257;
          }
          bool1 = false;
          break;
        }
        if (paramzzf.asX != null) {
          break label257;
        }
        bool1 = false;
        break;
        this.asp = paramzzf.asX.toUpperCase(Locale.ENGLISH);
        break label108;
        label205:
        paramzzf = zza(paramzzf.asZ, this.aso);
        break label117;
        label221:
        this.asr = null;
        continue;
        this.asn = 0;
        this.aso = false;
        this.asp = null;
        this.asq = null;
        this.asr = null;
      }
      label257:
      bool1 = true;
    }
  }
  
  private List<String> zza(String[] paramArrayOfString, boolean paramBoolean)
  {
    Object localObject;
    if (paramBoolean)
    {
      localObject = Arrays.asList(paramArrayOfString);
      return (List<String>)localObject;
    }
    ArrayList localArrayList = new ArrayList();
    int j = paramArrayOfString.length;
    int i = 0;
    for (;;)
    {
      localObject = localArrayList;
      if (i >= j) {
        break;
      }
      localArrayList.add(paramArrayOfString[i].toUpperCase(Locale.ENGLISH));
      i += 1;
    }
  }
  
  public Boolean zzmw(String paramString)
  {
    if (!this.apN) {}
    while (paramString == null) {
      return null;
    }
    String str = paramString;
    if (!this.aso)
    {
      if (this.asn != 1) {
        break label106;
      }
      str = paramString;
    }
    switch (this.asn)
    {
    default: 
      return null;
    case 1: 
      if (this.aso) {}
      for (int i = 0;; i = 66)
      {
        return Boolean.valueOf(Pattern.compile(this.asr, i).matcher(str).matches());
        str = paramString.toUpperCase(Locale.ENGLISH);
        break;
      }
    case 2: 
      return Boolean.valueOf(str.startsWith(this.asp));
    case 3: 
      return Boolean.valueOf(str.endsWith(this.asp));
    case 4: 
      return Boolean.valueOf(str.contains(this.asp));
    case 5: 
      label106:
      return Boolean.valueOf(str.equals(this.asp));
    }
    return Boolean.valueOf(this.asq.contains(str));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */