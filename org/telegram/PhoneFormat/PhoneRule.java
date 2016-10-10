package org.telegram.PhoneFormat;

public class PhoneRule
{
  public int byte8;
  public int flag12;
  public int flag13;
  public String format;
  public boolean hasIntlPrefix;
  public boolean hasTrunkPrefix;
  public int maxLen;
  public int maxVal;
  public int minVal;
  public int otherFlag;
  public int prefixLen;
  
  String format(String paramString1, String paramString2, String paramString3)
  {
    int i1 = 0;
    int n = 0;
    int i = 0;
    int m = 0;
    StringBuilder localStringBuilder = new StringBuilder(20);
    int k = 0;
    if (k < this.format.length())
    {
      int i5 = this.format.charAt(k);
      switch (i5)
      {
      }
      for (;;)
      {
        int i2;
        int i3;
        int j;
        int i4;
        if ((i5 == 32) && (k > 0))
        {
          if (this.format.charAt(k - 1) == 'n')
          {
            i2 = i1;
            i3 = n;
            j = i;
            i4 = m;
            if (paramString3 == null) {
              break label286;
            }
          }
          if (this.format.charAt(k - 1) == 'c')
          {
            i2 = i1;
            i3 = n;
            j = i;
            i4 = m;
            if (paramString2 == null) {
              break label286;
            }
          }
        }
        if (m >= paramString1.length())
        {
          i2 = i1;
          i3 = n;
          j = i;
          i4 = m;
          if (i != 0)
          {
            i2 = i1;
            i3 = n;
            j = i;
            i4 = m;
            if (i5 != 41) {}
          }
        }
        else
        {
          localStringBuilder.append(this.format.substring(k, k + 1));
          i2 = i1;
          i3 = n;
          j = i;
          i4 = m;
          if (i5 == 41)
          {
            j = 0;
            i4 = m;
            i3 = n;
            i2 = i1;
          }
        }
        for (;;)
        {
          label286:
          k += 1;
          i1 = i2;
          n = i3;
          i = j;
          m = i4;
          break;
          i1 = 1;
          i2 = i1;
          i3 = n;
          j = i;
          i4 = m;
          if (paramString2 != null)
          {
            localStringBuilder.append(paramString2);
            i2 = i1;
            i3 = n;
            j = i;
            i4 = m;
            continue;
            n = 1;
            i2 = i1;
            i3 = n;
            j = i;
            i4 = m;
            if (paramString3 != null)
            {
              localStringBuilder.append(paramString3);
              i2 = i1;
              i3 = n;
              j = i;
              i4 = m;
              continue;
              if (m < paramString1.length())
              {
                localStringBuilder.append(paramString1.substring(m, m + 1));
                i4 = m + 1;
                i2 = i1;
                i3 = n;
                j = i;
              }
              else
              {
                i2 = i1;
                i3 = n;
                j = i;
                i4 = m;
                if (i != 0)
                {
                  localStringBuilder.append(" ");
                  i2 = i1;
                  i3 = n;
                  j = i;
                  i4 = m;
                }
              }
            }
          }
        }
        if (m < paramString1.length()) {
          i = 1;
        }
      }
    }
    if ((paramString2 != null) && (i1 == 0)) {
      localStringBuilder.insert(0, String.format("%s ", new Object[] { paramString2 }));
    }
    for (;;)
    {
      return localStringBuilder.toString();
      if ((paramString3 != null) && (n == 0)) {
        localStringBuilder.insert(0, paramString3);
      }
    }
  }
  
  boolean hasIntlPrefix()
  {
    return (this.flag12 & 0x2) != 0;
  }
  
  boolean hasTrunkPrefix()
  {
    return (this.flag12 & 0x1) != 0;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/PhoneFormat/PhoneRule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */