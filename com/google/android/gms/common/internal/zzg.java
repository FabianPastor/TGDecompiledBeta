package com.google.android.gms.common.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class zzg
{
  public static final zzg BB = zza("\t\n\013\f\r     　 ᠎ ").zza(zza(' ', ' '));
  public static final zzg BC = zza("\t\n\013\f\r     　").zza(zza(' ', ' ')).zza(zza(' ', ' '));
  public static final zzg BD = zza('\000', '');
  public static final zzg BE;
  public static final zzg BF = zza('\t', '\r').zza(zza('\034', ' ')).zza(zzc(' ')).zza(zzc('᠎')).zza(zza(' ', ' ')).zza(zza(' ', '​')).zza(zza(' ', ' ')).zza(zzc(' ')).zza(zzc('　'));
  public static final zzg BG = new zzg()
  {
    public boolean zzd(char paramAnonymousChar)
    {
      return Character.isDigit(paramAnonymousChar);
    }
  };
  public static final zzg BH = new zzg()
  {
    public boolean zzd(char paramAnonymousChar)
    {
      return Character.isLetter(paramAnonymousChar);
    }
  };
  public static final zzg BI = new zzg()
  {
    public boolean zzd(char paramAnonymousChar)
    {
      return Character.isLetterOrDigit(paramAnonymousChar);
    }
  };
  public static final zzg BJ = new zzg()
  {
    public boolean zzd(char paramAnonymousChar)
    {
      return Character.isUpperCase(paramAnonymousChar);
    }
  };
  public static final zzg BK = new zzg()
  {
    public boolean zzd(char paramAnonymousChar)
    {
      return Character.isLowerCase(paramAnonymousChar);
    }
  };
  public static final zzg BL = zza('\000', '\037').zza(zza('', ''));
  public static final zzg BM = zza('\000', ' ').zza(zza('', ' ')).zza(zzc('­')).zza(zza('؀', '؃')).zza(zza("۝܏ ឴឵᠎")).zza(zza(' ', '‏')).zza(zza(' ', ' ')).zza(zza(' ', '⁤')).zza(zza('⁪', '⁯')).zza(zzc('　')).zza(zza(55296, 63743)).zza(zza("﻿￹￺￻"));
  public static final zzg BN = zza('\000', 'ӹ').zza(zzc('־')).zza(zza('א', 'ת')).zza(zzc('׳')).zza(zzc('״')).zza(zza('؀', 'ۿ')).zza(zza('ݐ', 'ݿ')).zza(zza('฀', '๿')).zza(zza('Ḁ', '₯')).zza(zza('℀', '℺')).zza(zza(64336, 65023)).zza(zza(65136, 65279)).zza(zza(65377, 65500));
  public static final zzg BO = new zzg()
  {
    public zzg zza(zzg paramAnonymouszzg)
    {
      zzac.zzy(paramAnonymouszzg);
      return this;
    }
    
    public boolean zzb(CharSequence paramAnonymousCharSequence)
    {
      zzac.zzy(paramAnonymousCharSequence);
      return true;
    }
    
    public boolean zzd(char paramAnonymousChar)
    {
      return true;
    }
  };
  public static final zzg BP = new zzg()
  {
    public zzg zza(zzg paramAnonymouszzg)
    {
      return (zzg)zzac.zzy(paramAnonymouszzg);
    }
    
    public boolean zzb(CharSequence paramAnonymousCharSequence)
    {
      return paramAnonymousCharSequence.length() == 0;
    }
    
    public boolean zzd(char paramAnonymousChar)
    {
      return false;
    }
  };
  
  static
  {
    zzg localzzg = zza('0', '9');
    char[] arrayOfChar = "٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".toCharArray();
    int j = arrayOfChar.length;
    int i = 0;
    while (i < j)
    {
      char c = arrayOfChar[i];
      localzzg = localzzg.zza(zza(c, (char)(c + '\t')));
      i += 1;
    }
    BE = localzzg;
  }
  
  public static zzg zza(char paramChar1, final char paramChar2)
  {
    if (paramChar2 >= paramChar1) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zzbs(bool);
      new zzg()
      {
        public boolean zzd(char paramAnonymousChar)
        {
          return (this.BT <= paramAnonymousChar) && (paramAnonymousChar <= paramChar2);
        }
      };
    }
  }
  
  public static zzg zza(CharSequence paramCharSequence)
  {
    switch (paramCharSequence.length())
    {
    default: 
      paramCharSequence = paramCharSequence.toString().toCharArray();
      Arrays.sort(paramCharSequence);
      new zzg()
      {
        public boolean zzd(char paramAnonymousChar)
        {
          return Arrays.binarySearch(zzg.this, paramAnonymousChar) >= 0;
        }
      };
    case 0: 
      return BP;
    case 1: 
      return zzc(paramCharSequence.charAt(0));
    }
    new zzg()
    {
      public boolean zzd(char paramAnonymousChar)
      {
        return (paramAnonymousChar == this.BQ) || (paramAnonymousChar == this.BR);
      }
    };
  }
  
  public static zzg zzc(char paramChar)
  {
    new zzg()
    {
      public zzg zza(zzg paramAnonymouszzg)
      {
        if (paramAnonymouszzg.zzd(this.BV)) {
          return paramAnonymouszzg;
        }
        return super.zza(paramAnonymouszzg);
      }
      
      public boolean zzd(char paramAnonymousChar)
      {
        return paramAnonymousChar == this.BV;
      }
    };
  }
  
  public zzg zza(zzg paramzzg)
  {
    return new zza(Arrays.asList(new zzg[] { this, (zzg)zzac.zzy(paramzzg) }));
  }
  
  public boolean zzb(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length() - 1;
    while (i >= 0)
    {
      if (!zzd(paramCharSequence.charAt(i))) {
        return false;
      }
      i -= 1;
    }
    return true;
  }
  
  public abstract boolean zzd(char paramChar);
  
  private static class zza
    extends zzg
  {
    List<zzg> BW;
    
    zza(List<zzg> paramList)
    {
      this.BW = paramList;
    }
    
    public zzg zza(zzg paramzzg)
    {
      ArrayList localArrayList = new ArrayList(this.BW);
      localArrayList.add((zzg)zzac.zzy(paramzzg));
      return new zza(localArrayList);
    }
    
    public boolean zzd(char paramChar)
    {
      Iterator localIterator = this.BW.iterator();
      while (localIterator.hasNext()) {
        if (((zzg)localIterator.next()).zzd(paramChar)) {
          return true;
        }
      }
      return false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */