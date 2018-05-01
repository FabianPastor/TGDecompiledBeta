package org.telegram.messenger.exoplayer2.util;

import android.net.Uri;
import android.text.TextUtils;

public final class UriUtil
{
  private static final int FRAGMENT = 3;
  private static final int INDEX_COUNT = 4;
  private static final int PATH = 1;
  private static final int QUERY = 2;
  private static final int SCHEME_COLON = 0;
  
  private static int[] getUriIndices(String paramString)
  {
    int[] arrayOfInt = new int[4];
    if (TextUtils.isEmpty(paramString))
    {
      arrayOfInt[0] = -1;
      return arrayOfInt;
    }
    int i = paramString.length();
    int j = paramString.indexOf('#');
    int k = j;
    if (j == -1) {
      k = i;
    }
    j = paramString.indexOf('?');
    if (j != -1)
    {
      i = j;
      if (j <= k) {}
    }
    else
    {
      i = k;
    }
    int m = paramString.indexOf('/');
    if (m != -1)
    {
      j = m;
      if (m <= i) {}
    }
    else
    {
      j = i;
    }
    int n = paramString.indexOf(':');
    m = n;
    if (n > j) {
      m = -1;
    }
    if ((m + 2 < i) && (paramString.charAt(m + 1) == '/') && (paramString.charAt(m + 2) == '/'))
    {
      j = 1;
      label145:
      if (j == 0) {
        break label204;
      }
      n = paramString.indexOf('/', m + 3);
      if (n != -1)
      {
        j = n;
        if (n <= i) {
          break label178;
        }
      }
    }
    label178:
    label204:
    for (j = i;; j = m + 1)
    {
      arrayOfInt[0] = m;
      arrayOfInt[1] = j;
      arrayOfInt[2] = i;
      arrayOfInt[3] = k;
      break;
      j = 0;
      break label145;
    }
  }
  
  private static String removeDotSegments(StringBuilder paramStringBuilder, int paramInt1, int paramInt2)
  {
    if (paramInt1 >= paramInt2) {}
    for (paramStringBuilder = paramStringBuilder.toString();; paramStringBuilder = paramStringBuilder.toString())
    {
      return paramStringBuilder;
      int i = paramInt1;
      if (paramStringBuilder.charAt(paramInt1) == '/') {
        i = paramInt1 + 1;
      }
      int j = i;
      paramInt1 = i;
      int k = paramInt2;
      paramInt2 = j;
      while (paramInt1 <= k)
      {
        if (paramInt1 == k) {
          j = paramInt1;
        }
        for (;;)
        {
          if ((paramInt1 == paramInt2 + 1) && (paramStringBuilder.charAt(paramInt2) == '.'))
          {
            paramStringBuilder.delete(paramInt2, j);
            k -= j - paramInt2;
            paramInt1 = paramInt2;
            break;
            if (paramStringBuilder.charAt(paramInt1) == '/')
            {
              j = paramInt1 + 1;
            }
            else
            {
              paramInt1++;
              break;
            }
          }
        }
        if ((paramInt1 == paramInt2 + 2) && (paramStringBuilder.charAt(paramInt2) == '.') && (paramStringBuilder.charAt(paramInt2 + 1) == '.'))
        {
          paramInt1 = paramStringBuilder.lastIndexOf("/", paramInt2 - 2) + 1;
          if (paramInt1 > i) {}
          for (paramInt2 = paramInt1;; paramInt2 = i)
          {
            paramStringBuilder.delete(paramInt2, j);
            k -= j - paramInt2;
            paramInt2 = paramInt1;
            break;
          }
        }
        paramInt1++;
        paramInt2 = paramInt1;
      }
    }
  }
  
  public static String resolve(String paramString1, String paramString2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    String str = paramString1;
    if (paramString1 == null) {
      str = "";
    }
    paramString1 = paramString2;
    if (paramString2 == null) {
      paramString1 = "";
    }
    int[] arrayOfInt = getUriIndices(paramString1);
    if (arrayOfInt[0] != -1)
    {
      localStringBuilder.append(paramString1);
      removeDotSegments(localStringBuilder, arrayOfInt[1], arrayOfInt[2]);
      paramString1 = localStringBuilder.toString();
    }
    for (;;)
    {
      return paramString1;
      paramString2 = getUriIndices(str);
      if (arrayOfInt[3] == 0)
      {
        paramString1 = paramString1;
      }
      else if (arrayOfInt[2] == 0)
      {
        paramString1 = paramString1;
      }
      else if (arrayOfInt[1] != 0)
      {
        i = paramString2[0] + 1;
        localStringBuilder.append(str, 0, i).append(paramString1);
        paramString1 = removeDotSegments(localStringBuilder, arrayOfInt[1] + i, arrayOfInt[2] + i);
      }
      else if (paramString1.charAt(arrayOfInt[1]) == '/')
      {
        localStringBuilder.append(str, 0, paramString2[1]).append(paramString1);
        paramString1 = removeDotSegments(localStringBuilder, paramString2[1], paramString2[1] + arrayOfInt[2]);
      }
      else
      {
        if ((paramString2[0] + 2 >= paramString2[1]) || (paramString2[1] != paramString2[2])) {
          break;
        }
        localStringBuilder.append(str, 0, paramString2[1]).append('/').append(paramString1);
        paramString1 = removeDotSegments(localStringBuilder, paramString2[1], paramString2[1] + arrayOfInt[2] + 1);
      }
    }
    int i = str.lastIndexOf('/', paramString2[2] - 1);
    if (i == -1) {}
    for (i = paramString2[1];; i++)
    {
      localStringBuilder.append(str, 0, i).append(paramString1);
      paramString1 = removeDotSegments(localStringBuilder, paramString2[1], arrayOfInt[2] + i);
      break;
    }
  }
  
  public static Uri resolveToUri(String paramString1, String paramString2)
  {
    return Uri.parse(resolve(paramString1, paramString2));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/UriUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */