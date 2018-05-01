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
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    StringBuilder localStringBuilder = new StringBuilder(20);
    int n = 0;
    if (n < this.format.length())
    {
      int i1 = this.format.charAt(n);
      switch (i1)
      {
      }
      for (;;)
      {
        int i2;
        int i3;
        int i4;
        int i5;
        if ((i1 == 32) && (n > 0))
        {
          if (this.format.charAt(n - 1) == 'n')
          {
            i2 = i;
            i3 = j;
            i4 = k;
            i5 = m;
            if (paramString3 == null) {
              break label286;
            }
          }
          if (this.format.charAt(n - 1) == 'c')
          {
            i2 = i;
            i3 = j;
            i4 = k;
            i5 = m;
            if (paramString2 == null) {
              break label286;
            }
          }
        }
        if (m >= paramString1.length())
        {
          i2 = i;
          i3 = j;
          i4 = k;
          i5 = m;
          if (k != 0)
          {
            i2 = i;
            i3 = j;
            i4 = k;
            i5 = m;
            if (i1 != 41) {}
          }
        }
        else
        {
          localStringBuilder.append(this.format.substring(n, n + 1));
          i2 = i;
          i3 = j;
          i4 = k;
          i5 = m;
          if (i1 == 41)
          {
            i4 = 0;
            i5 = m;
            i3 = j;
            i2 = i;
          }
        }
        for (;;)
        {
          label286:
          n++;
          i = i2;
          j = i3;
          k = i4;
          m = i5;
          break;
          i = 1;
          i2 = i;
          i3 = j;
          i4 = k;
          i5 = m;
          if (paramString2 != null)
          {
            localStringBuilder.append(paramString2);
            i2 = i;
            i3 = j;
            i4 = k;
            i5 = m;
            continue;
            j = 1;
            i2 = i;
            i3 = j;
            i4 = k;
            i5 = m;
            if (paramString3 != null)
            {
              localStringBuilder.append(paramString3);
              i2 = i;
              i3 = j;
              i4 = k;
              i5 = m;
              continue;
              if (m < paramString1.length())
              {
                localStringBuilder.append(paramString1.substring(m, m + 1));
                i5 = m + 1;
                i2 = i;
                i3 = j;
                i4 = k;
              }
              else
              {
                i2 = i;
                i3 = j;
                i4 = k;
                i5 = m;
                if (k != 0)
                {
                  localStringBuilder.append(" ");
                  i2 = i;
                  i3 = j;
                  i4 = k;
                  i5 = m;
                }
              }
            }
          }
        }
        if (m < paramString1.length()) {
          k = 1;
        }
      }
    }
    if ((paramString2 != null) && (i == 0)) {
      localStringBuilder.insert(0, String.format("%s ", new Object[] { paramString2 }));
    }
    for (;;)
    {
      return localStringBuilder.toString();
      if ((paramString3 != null) && (j == 0)) {
        localStringBuilder.insert(0, paramString3);
      }
    }
  }
  
  boolean hasIntlPrefix()
  {
    if ((this.flag12 & 0x2) != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  boolean hasTrunkPrefix()
  {
    if ((this.flag12 & 0x1) != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/PhoneFormat/PhoneRule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */