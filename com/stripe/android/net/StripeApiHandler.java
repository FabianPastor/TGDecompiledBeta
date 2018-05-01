package com.stripe.android.net;

import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.CardException;
import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.exception.PermissionException;
import com.stripe.android.exception.RateLimitException;
import com.stripe.android.model.Token;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Security;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class StripeApiHandler
{
  private static final SSLSocketFactory SSL_SOCKET_FACTORY = new StripeSSLSocketFactory();
  
  private static HttpURLConnection createGetConnection(String paramString1, String paramString2, RequestOptions paramRequestOptions)
    throws IOException
  {
    paramString1 = createStripeConnection(formatURL(paramString1, paramString2), paramRequestOptions);
    paramString1.setRequestMethod("GET");
    return paramString1;
  }
  
  private static HttpURLConnection createPostConnection(String paramString1, String paramString2, RequestOptions paramRequestOptions)
    throws IOException
  {
    HttpURLConnection localHttpURLConnection = createStripeConnection(paramString1, paramRequestOptions);
    localHttpURLConnection.setDoOutput(true);
    localHttpURLConnection.setRequestMethod("POST");
    localHttpURLConnection.setRequestProperty("Content-Type", String.format("application/x-www-form-urlencoded;charset=%s", new Object[] { "UTF-8" }));
    paramString1 = null;
    try
    {
      paramRequestOptions = localHttpURLConnection.getOutputStream();
      paramString1 = paramRequestOptions;
      paramRequestOptions.write(paramString2.getBytes("UTF-8"));
      return localHttpURLConnection;
    }
    finally
    {
      if (paramString1 != null) {
        paramString1.close();
      }
    }
  }
  
  static String createQuery(Map<String, Object> paramMap)
    throws UnsupportedEncodingException, InvalidRequestException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    paramMap = flattenParams(paramMap).iterator();
    while (paramMap.hasNext())
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append("&");
      }
      Parameter localParameter = (Parameter)paramMap.next();
      localStringBuilder.append(urlEncodePair(localParameter.key, localParameter.value));
    }
    return localStringBuilder.toString();
  }
  
  private static HttpURLConnection createStripeConnection(String paramString, RequestOptions paramRequestOptions)
    throws IOException
  {
    paramString = (HttpURLConnection)new URL(paramString).openConnection();
    paramString.setConnectTimeout(30000);
    paramString.setReadTimeout(80000);
    paramString.setUseCaches(false);
    Iterator localIterator = getHeaders(paramRequestOptions).entrySet().iterator();
    while (localIterator.hasNext())
    {
      paramRequestOptions = (Map.Entry)localIterator.next();
      paramString.setRequestProperty((String)paramRequestOptions.getKey(), (String)paramRequestOptions.getValue());
    }
    if ((paramString instanceof HttpsURLConnection)) {
      ((HttpsURLConnection)paramString).setSSLSocketFactory(SSL_SOCKET_FACTORY);
    }
    return paramString;
  }
  
  public static Token createToken(Map<String, Object> paramMap, RequestOptions paramRequestOptions)
    throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException
  {
    return requestToken("POST", getApiUrl(), paramMap, paramRequestOptions);
  }
  
  private static List<Parameter> flattenParams(Map<String, Object> paramMap)
    throws InvalidRequestException
  {
    return flattenParamsMap(paramMap, null);
  }
  
  private static List<Parameter> flattenParamsList(List<Object> paramList, String paramString)
    throws InvalidRequestException
  {
    LinkedList localLinkedList = new LinkedList();
    Iterator localIterator = paramList.iterator();
    String str = String.format("%s[]", new Object[] { paramString });
    if (paramList.isEmpty()) {
      localLinkedList.add(new Parameter(paramString, ""));
    }
    for (;;)
    {
      return localLinkedList;
      while (localIterator.hasNext()) {
        localLinkedList.addAll(flattenParamsValue(localIterator.next(), str));
      }
    }
  }
  
  private static List<Parameter> flattenParamsMap(Map<String, Object> paramMap, String paramString)
    throws InvalidRequestException
  {
    LinkedList localLinkedList = new LinkedList();
    if (paramMap == null) {}
    for (;;)
    {
      return localLinkedList;
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        paramMap = (Map.Entry)localIterator.next();
        String str = (String)paramMap.getKey();
        Object localObject = paramMap.getValue();
        paramMap = str;
        if (paramString != null) {
          paramMap = String.format("%s[%s]", new Object[] { paramString, str });
        }
        localLinkedList.addAll(flattenParamsValue(localObject, paramMap));
      }
    }
  }
  
  private static List<Parameter> flattenParamsValue(Object paramObject, String paramString)
    throws InvalidRequestException
  {
    if ((paramObject instanceof Map)) {
      paramObject = flattenParamsMap((Map)paramObject, paramString);
    }
    for (;;)
    {
      return (List<Parameter>)paramObject;
      if ((paramObject instanceof List))
      {
        paramObject = flattenParamsList((List)paramObject, paramString);
      }
      else
      {
        if ("".equals(paramObject)) {
          throw new InvalidRequestException("You cannot set '" + paramString + "' to an empty string. We interpret empty strings as null in requests. You may set '" + paramString + "' to null to delete the property.", paramString, null, Integer.valueOf(0), null);
        }
        if (paramObject == null)
        {
          paramObject = new LinkedList();
          ((List)paramObject).add(new Parameter(paramString, ""));
        }
        else
        {
          LinkedList localLinkedList = new LinkedList();
          localLinkedList.add(new Parameter(paramString, paramObject.toString()));
          paramObject = localLinkedList;
        }
      }
    }
  }
  
  private static String formatURL(String paramString1, String paramString2)
  {
    String str = paramString1;
    if (paramString2 != null)
    {
      if (paramString2.isEmpty()) {
        str = paramString1;
      }
    }
    else {
      return str;
    }
    if (paramString1.contains("?")) {}
    for (str = "&";; str = "?")
    {
      str = String.format("%s%s%s", new Object[] { paramString1, str, paramString2 });
      break;
    }
  }
  
  static String getApiUrl()
  {
    return String.format("%s/v1/%s", new Object[] { "https://api.stripe.com", "tokens" });
  }
  
  static Map<String, String> getHeaders(RequestOptions paramRequestOptions)
  {
    int i = 0;
    HashMap localHashMap1 = new HashMap();
    String str1 = paramRequestOptions.getApiVersion();
    localHashMap1.put("Accept-Charset", "UTF-8");
    localHashMap1.put("Accept", "application/json");
    localHashMap1.put("User-Agent", String.format("Stripe/v1 JavaBindings/%s", new Object[] { "3.5.0" }));
    localHashMap1.put("Authorization", String.format("Bearer %s", new Object[] { paramRequestOptions.getPublishableApiKey() }));
    String[] arrayOfString = new String[7];
    arrayOfString[0] = "os.name";
    arrayOfString[1] = "os.version";
    arrayOfString[2] = "os.arch";
    arrayOfString[3] = "java.version";
    arrayOfString[4] = "java.vendor";
    arrayOfString[5] = "java.vm.version";
    arrayOfString[6] = "java.vm.vendor";
    HashMap localHashMap2 = new HashMap();
    int j = arrayOfString.length;
    while (i < j)
    {
      String str2 = arrayOfString[i];
      localHashMap2.put(str2, System.getProperty(str2));
      i++;
    }
    localHashMap2.put("bindings.version", "3.5.0");
    localHashMap2.put("lang", "Java");
    localHashMap2.put("publisher", "Stripe");
    localHashMap1.put("X-Stripe-Client-User-Agent", new JSONObject(localHashMap2).toString());
    if (str1 != null) {
      localHashMap1.put("Stripe-Version", str1);
    }
    if (paramRequestOptions.getIdempotencyKey() != null) {
      localHashMap1.put("Idempotency-Key", paramRequestOptions.getIdempotencyKey());
    }
    return localHashMap1;
  }
  
  private static String getResponseBody(InputStream paramInputStream)
    throws IOException
  {
    String str = new Scanner(paramInputStream, "UTF-8").useDelimiter("\\A").next();
    paramInputStream.close();
    return str;
  }
  
  private static StripeResponse getStripeResponse(String paramString1, String paramString2, Map<String, Object> paramMap, RequestOptions paramRequestOptions)
    throws InvalidRequestException, APIConnectionException, APIException
  {
    try
    {
      paramMap = createQuery(paramMap);
      return makeURLConnectionRequest(paramString1, paramString2, paramMap, paramRequestOptions);
    }
    catch (UnsupportedEncodingException paramString1)
    {
      throw new InvalidRequestException("Unable to encode parameters to UTF-8. Please contact support@stripe.com for assistance.", null, null, Integer.valueOf(0), paramString1);
    }
  }
  
  private static void handleAPIError(String paramString1, int paramInt, String paramString2)
    throws InvalidRequestException, AuthenticationException, CardException, APIException
  {
    paramString1 = ErrorParser.parseError(paramString1);
    switch (paramInt)
    {
    default: 
      throw new APIException(paramString1.message, paramString2, Integer.valueOf(paramInt), null);
    case 400: 
      throw new InvalidRequestException(paramString1.message, paramString1.param, paramString2, Integer.valueOf(paramInt), null);
    case 404: 
      throw new InvalidRequestException(paramString1.message, paramString1.param, paramString2, Integer.valueOf(paramInt), null);
    case 401: 
      throw new AuthenticationException(paramString1.message, paramString2, Integer.valueOf(paramInt));
    case 402: 
      throw new CardException(paramString1.message, paramString2, paramString1.code, paramString1.param, paramString1.decline_code, paramString1.charge, Integer.valueOf(paramInt), null);
    case 403: 
      throw new PermissionException(paramString1.message, paramString2, Integer.valueOf(paramInt));
    }
    throw new RateLimitException(paramString1.message, paramString1.param, paramString2, Integer.valueOf(paramInt), null);
  }
  
  private static StripeResponse makeURLConnectionRequest(String paramString1, String paramString2, String paramString3, RequestOptions paramRequestOptions)
    throws APIConnectionException
  {
    int i = 0;
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = localObject2;
    Object localObject4 = localObject1;
    for (;;)
    {
      try
      {
        switch (paramString1.hashCode())
        {
        default: 
          i = -1;
          switch (i)
          {
          default: 
            localObject3 = localObject2;
            localObject4 = localObject1;
            paramString2 = new com/stripe/android/exception/APIConnectionException;
            localObject3 = localObject2;
            localObject4 = localObject1;
            paramString2.<init>(String.format("Unrecognized HTTP method %s. This indicates a bug in the Stripe bindings. Please contact support@stripe.com for assistance.", new Object[] { paramString1 }));
            localObject3 = localObject2;
            localObject4 = localObject1;
            throw paramString2;
          }
          break;
        }
      }
      catch (IOException paramString2)
      {
        localObject4 = localObject3;
        paramString1 = new com/stripe/android/exception/APIConnectionException;
        localObject4 = localObject3;
        paramString1.<init>(String.format("IOException during API request to Stripe (%s): %s Please check your internet connection and try again. If this problem persists, you should check Stripe's service status at https://twitter.com/stripestatus, or let us know at support@stripe.com.", new Object[] { getApiUrl(), paramString2.getMessage() }), paramString2);
        localObject4 = localObject3;
        throw paramString1;
      }
      finally
      {
        if (localObject4 != null) {
          ((HttpURLConnection)localObject4).disconnect();
        }
      }
      localObject3 = localObject2;
      localObject4 = localObject1;
      if (paramString1.equals("GET"))
      {
        continue;
        localObject3 = localObject2;
        localObject4 = localObject1;
        if (paramString1.equals("POST")) {
          i = 1;
        }
      }
    }
    localObject3 = localObject2;
    localObject4 = localObject1;
    paramString1 = createGetConnection(paramString2, paramString3, paramRequestOptions);
    localObject3 = paramString1;
    localObject4 = paramString1;
    i = paramString1.getResponseCode();
    if ((i >= 200) && (i < 300))
    {
      localObject3 = paramString1;
      localObject4 = paramString1;
    }
    for (paramString2 = getResponseBody(paramString1.getInputStream());; paramString2 = getResponseBody(paramString1.getErrorStream()))
    {
      localObject3 = paramString1;
      localObject4 = paramString1;
      paramString2 = new StripeResponse(i, paramString2, paramString1.getHeaderFields());
      if (paramString1 != null) {
        paramString1.disconnect();
      }
      return paramString2;
      localObject3 = localObject2;
      localObject4 = localObject1;
      paramString1 = createPostConnection(paramString2, paramString3, paramRequestOptions);
      break;
      localObject3 = paramString1;
      localObject4 = paramString1;
    }
  }
  
  private static Token requestToken(String paramString1, String paramString2, Map<String, Object> paramMap, RequestOptions paramRequestOptions)
    throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException
  {
    if (paramRequestOptions == null) {
      paramString1 = null;
    }
    Object localObject1;
    for (;;)
    {
      return paramString1;
      localObject1 = null;
      Boolean localBoolean1 = Boolean.valueOf(true);
      try
      {
        Object localObject2 = Security.getProperty("networkaddress.cache.ttl");
        localObject1 = localObject2;
        Security.setProperty("networkaddress.cache.ttl", "0");
        localObject1 = localObject2;
        localObject2 = localBoolean1;
        if (paramRequestOptions.getPublishableApiKey().trim().isEmpty()) {
          throw new AuthenticationException("No API key provided. (HINT: set your API key using 'Stripe.apiKey = <API-KEY>'. You can generate API keys from the Stripe web interface. See https://stripe.com/api for details or email support@stripe.com if you have questions.", null, Integer.valueOf(0));
        }
      }
      catch (SecurityException localSecurityException)
      {
        Boolean localBoolean2;
        for (;;)
        {
          localBoolean2 = Boolean.valueOf(false);
        }
        try
        {
          paramString1 = getStripeResponse(paramString1, paramString2, paramMap, paramRequestOptions);
          int i = paramString1.getResponseCode();
          paramRequestOptions = paramString1.getResponseBody();
          paramMap = null;
          paramString1 = paramString1.getResponseHeaders();
          if (paramString1 == null) {}
          for (paramString2 = null;; paramString2 = (List)paramString1.get("Request-Id"))
          {
            paramString1 = paramMap;
            if (paramString2 != null)
            {
              paramString1 = paramMap;
              if (paramString2.size() > 0) {
                paramString1 = (String)paramString2.get(0);
              }
            }
            if ((i < 200) || (i >= 300)) {
              handleAPIError(paramRequestOptions, i, paramString1);
            }
            paramString2 = TokenParser.parseToken(paramRequestOptions);
            paramString1 = paramString2;
            if (!localBoolean2.booleanValue()) {
              break;
            }
            if (localObject1 != null) {
              break label219;
            }
            Security.setProperty("networkaddress.cache.ttl", "-1");
            paramString1 = paramString2;
            break;
          }
          label219:
          Security.setProperty("networkaddress.cache.ttl", (String)localObject1);
          paramString1 = paramString2;
        }
        catch (JSONException paramString1)
        {
          paramString2 = null;
          paramString1 = paramString2;
          if (localBoolean2.booleanValue()) {
            if (localObject1 == null)
            {
              Security.setProperty("networkaddress.cache.ttl", "-1");
              paramString1 = paramString2;
            }
            else
            {
              Security.setProperty("networkaddress.cache.ttl", (String)localObject1);
              paramString1 = paramString2;
            }
          }
        }
        finally
        {
          if (localBoolean2.booleanValue())
          {
            if (localObject1 != null) {
              break label302;
            }
            Security.setProperty("networkaddress.cache.ttl", "-1");
          }
        }
      }
    }
    for (;;)
    {
      throw paramString1;
      label302:
      Security.setProperty("networkaddress.cache.ttl", (String)localObject1);
    }
  }
  
  private static String urlEncode(String paramString)
    throws UnsupportedEncodingException
  {
    if (paramString == null) {}
    for (paramString = null;; paramString = URLEncoder.encode(paramString, "UTF-8")) {
      return paramString;
    }
  }
  
  private static String urlEncodePair(String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    return String.format("%s=%s", new Object[] { urlEncode(paramString1), urlEncode(paramString2) });
  }
  
  private static final class Parameter
  {
    public final String key;
    public final String value;
    
    public Parameter(String paramString1, String paramString2)
    {
      this.key = paramString1;
      this.value = paramString2;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/net/StripeApiHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */