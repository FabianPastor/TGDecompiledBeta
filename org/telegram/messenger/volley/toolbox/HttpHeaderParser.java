package org.telegram.messenger.volley.toolbox;

import java.util.Date;
import java.util.Map;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.telegram.messenger.volley.Cache.Entry;
import org.telegram.messenger.volley.NetworkResponse;

public class HttpHeaderParser
{
  public static Cache.Entry parseCacheHeaders(NetworkResponse paramNetworkResponse)
  {
    long l10 = System.currentTimeMillis();
    Map localMap = paramNetworkResponse.headers;
    long l3 = 0L;
    long l6 = 0L;
    long l7 = 0L;
    long l8 = 0L;
    long l9 = 0L;
    l2 = 0L;
    l1 = 0L;
    int k = 0;
    int m = 0;
    int i = 0;
    Object localObject1 = (String)localMap.get("Date");
    if (localObject1 != null) {
      l3 = parseDateAsEpoch((String)localObject1);
    }
    localObject1 = (String)localMap.get("Cache-Control");
    l5 = l2;
    l4 = l1;
    int j;
    Object localObject2;
    if (localObject1 != null)
    {
      int n = 1;
      localObject1 = ((String)localObject1).split(",");
      j = 0;
      k = n;
      m = i;
      l5 = l2;
      l4 = l1;
      if (j < localObject1.length)
      {
        localObject2 = localObject1[j].trim();
        if ((((String)localObject2).equals("no-cache")) || (((String)localObject2).equals("no-store"))) {
          return null;
        }
        if (!((String)localObject2).startsWith("max-age=")) {}
      }
    }
    for (;;)
    {
      try
      {
        l4 = Long.parseLong(((String)localObject2).substring(8));
        l5 = l1;
      }
      catch (Exception localException2)
      {
        l4 = l2;
        l5 = l1;
        continue;
      }
      j += 1;
      l2 = l4;
      l1 = l5;
      break;
      if (((String)localObject2).startsWith("stale-while-revalidate=")) {}
      try
      {
        l5 = Long.parseLong(((String)localObject2).substring(23));
        l4 = l2;
      }
      catch (Exception localException1)
      {
        l4 = l2;
        l5 = l1;
      }
      if (!((String)localObject2).equals("must-revalidate"))
      {
        l4 = l2;
        l5 = l1;
        if (!((String)localObject2).equals("proxy-revalidate")) {}
      }
      else
      {
        i = 1;
        l4 = l2;
        l5 = l1;
        continue;
        localObject1 = (String)localMap.get("Expires");
        if (localObject1 != null) {
          l7 = parseDateAsEpoch((String)localObject1);
        }
        localObject1 = (String)localMap.get("Last-Modified");
        if (localObject1 != null) {
          l6 = parseDateAsEpoch((String)localObject1);
        }
        localObject1 = (String)localMap.get("ETag");
        if (k != 0)
        {
          l2 = l10 + 1000L * l5;
          if (m != 0)
          {
            l1 = l2;
            localObject2 = new Cache.Entry();
            ((Cache.Entry)localObject2).data = paramNetworkResponse.data;
            ((Cache.Entry)localObject2).etag = ((String)localObject1);
            ((Cache.Entry)localObject2).softTtl = l2;
            ((Cache.Entry)localObject2).ttl = l1;
            ((Cache.Entry)localObject2).serverDate = l3;
            ((Cache.Entry)localObject2).lastModified = l6;
            ((Cache.Entry)localObject2).responseHeaders = localMap;
            return (Cache.Entry)localObject2;
          }
          l1 = l2 + 1000L * l4;
          continue;
        }
        l1 = l9;
        l2 = l8;
        if (l3 > 0L)
        {
          l1 = l9;
          l2 = l8;
          if (l7 >= l3)
          {
            l2 = l10 + (l7 - l3);
            l1 = l2;
          }
        }
      }
    }
  }
  
  public static String parseCharset(Map<String, String> paramMap)
  {
    return parseCharset(paramMap, "ISO-8859-1");
  }
  
  public static String parseCharset(Map<String, String> paramMap, String paramString)
  {
    Object localObject = (String)paramMap.get("Content-Type");
    paramMap = paramString;
    int i;
    if (localObject != null)
    {
      localObject = ((String)localObject).split(";");
      i = 1;
    }
    for (;;)
    {
      paramMap = paramString;
      if (i < localObject.length)
      {
        paramMap = localObject[i].trim().split("=");
        if ((paramMap.length == 2) && (paramMap[0].equals("charset"))) {
          paramMap = paramMap[1];
        }
      }
      else
      {
        return paramMap;
      }
      i += 1;
    }
  }
  
  public static long parseDateAsEpoch(String paramString)
  {
    try
    {
      long l = DateUtils.parseDate(paramString).getTime();
      return l;
    }
    catch (DateParseException paramString) {}
    return 0L;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/volley/toolbox/HttpHeaderParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */