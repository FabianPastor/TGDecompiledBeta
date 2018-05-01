package org.telegram.messenger.exoplayer2.source.dash.manifest;

import java.util.Locale;

public final class UrlTemplate
{
  private static final String BANDWIDTH = "Bandwidth";
  private static final int BANDWIDTH_ID = 3;
  private static final String DEFAULT_FORMAT_TAG = "%01d";
  private static final String ESCAPED_DOLLAR = "$$";
  private static final String NUMBER = "Number";
  private static final int NUMBER_ID = 2;
  private static final String REPRESENTATION = "RepresentationID";
  private static final int REPRESENTATION_ID = 1;
  private static final String TIME = "Time";
  private static final int TIME_ID = 4;
  private final int identifierCount;
  private final String[] identifierFormatTags;
  private final int[] identifiers;
  private final String[] urlPieces;
  
  private UrlTemplate(String[] paramArrayOfString1, int[] paramArrayOfInt, String[] paramArrayOfString2, int paramInt)
  {
    this.urlPieces = paramArrayOfString1;
    this.identifiers = paramArrayOfInt;
    this.identifierFormatTags = paramArrayOfString2;
    this.identifierCount = paramInt;
  }
  
  public static UrlTemplate compile(String paramString)
  {
    String[] arrayOfString1 = new String[5];
    int[] arrayOfInt = new int[4];
    String[] arrayOfString2 = new String[4];
    return new UrlTemplate(arrayOfString1, arrayOfInt, arrayOfString2, parseTemplate(paramString, arrayOfString1, arrayOfInt, arrayOfString2));
  }
  
  private static int parseTemplate(String paramString, String[] paramArrayOfString1, int[] paramArrayOfInt, String[] paramArrayOfString2)
  {
    paramArrayOfString1[0] = "";
    int i = 0;
    int j = 0;
    while (i < paramString.length())
    {
      int k = paramString.indexOf("$", i);
      if (k == -1)
      {
        paramArrayOfString1[j] = (paramArrayOfString1[j] + paramString.substring(i));
        i = paramString.length();
      }
      else if (k != i)
      {
        paramArrayOfString1[j] = (paramArrayOfString1[j] + paramString.substring(i, k));
        i = k;
      }
      else if (paramString.startsWith("$$", i))
      {
        paramArrayOfString1[j] = (paramArrayOfString1[j] + "$");
        i += 2;
      }
      else
      {
        k = paramString.indexOf("$", i + 1);
        String str1 = paramString.substring(i + 1, k);
        if (str1.equals("RepresentationID"))
        {
          paramArrayOfInt[j] = 1;
          j++;
          paramArrayOfString1[j] = "";
          i = k + 1;
        }
        else
        {
          i = str1.indexOf("%0");
          Object localObject = "%01d";
          String str2 = str1;
          if (i != -1)
          {
            str2 = str1.substring(i);
            localObject = str2;
            if (!str2.endsWith("d")) {
              localObject = str2 + "d";
            }
            str2 = str1.substring(0, i);
          }
          i = -1;
          switch (str2.hashCode())
          {
          }
          for (;;)
          {
            switch (i)
            {
            default: 
              throw new IllegalArgumentException("Invalid template: " + paramString);
              if (str2.equals("Number"))
              {
                i = 0;
                continue;
                if (str2.equals("Bandwidth"))
                {
                  i = 1;
                  continue;
                  if (str2.equals("Time")) {
                    i = 2;
                  }
                }
              }
              break;
            }
          }
          paramArrayOfInt[j] = 2;
          for (;;)
          {
            paramArrayOfString2[j] = localObject;
            break;
            paramArrayOfInt[j] = 3;
            continue;
            paramArrayOfInt[j] = 4;
          }
        }
      }
    }
    return j;
  }
  
  public String buildUri(String paramString, int paramInt1, int paramInt2, long paramLong)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if (i < this.identifierCount)
    {
      localStringBuilder.append(this.urlPieces[i]);
      if (this.identifiers[i] == 1) {
        localStringBuilder.append(paramString);
      }
      for (;;)
      {
        i++;
        break;
        if (this.identifiers[i] == 2) {
          localStringBuilder.append(String.format(Locale.US, this.identifierFormatTags[i], new Object[] { Integer.valueOf(paramInt1) }));
        } else if (this.identifiers[i] == 3) {
          localStringBuilder.append(String.format(Locale.US, this.identifierFormatTags[i], new Object[] { Integer.valueOf(paramInt2) }));
        } else if (this.identifiers[i] == 4) {
          localStringBuilder.append(String.format(Locale.US, this.identifierFormatTags[i], new Object[] { Long.valueOf(paramLong) }));
        }
      }
    }
    localStringBuilder.append(this.urlPieces[this.identifierCount]);
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/manifest/UrlTemplate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */