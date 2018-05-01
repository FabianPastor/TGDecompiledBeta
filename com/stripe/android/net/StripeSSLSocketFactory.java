package com.stripe.android.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class StripeSSLSocketFactory
  extends SSLSocketFactory
{
  private final boolean tlsv11Supported;
  private final boolean tlsv12Supported;
  private final SSLSocketFactory under = HttpsURLConnection.getDefaultSSLSocketFactory();
  
  public StripeSSLSocketFactory()
  {
    boolean bool1 = false;
    boolean bool2 = false;
    try
    {
      String[] arrayOfString1 = SSLContext.getDefault().getSupportedSSLParameters().getProtocols();
      int j = arrayOfString1.length;
      for (;;)
      {
        if (i >= j) {
          break label99;
        }
        str = arrayOfString1[i];
        if (!str.equals("TLSv1.1")) {
          break;
        }
        bool3 = true;
        i++;
        bool1 = bool3;
      }
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      for (;;)
      {
        String str;
        String[] arrayOfString2 = new String[0];
        continue;
        boolean bool3 = bool1;
        if (str.equals("TLSv1.2"))
        {
          bool2 = true;
          bool3 = bool1;
        }
      }
      label99:
      this.tlsv11Supported = bool1;
      this.tlsv12Supported = bool2;
    }
  }
  
  private Socket fixupSocket(Socket paramSocket)
  {
    if (!(paramSocket instanceof SSLSocket)) {}
    for (;;)
    {
      return paramSocket;
      paramSocket = (SSLSocket)paramSocket;
      HashSet localHashSet = new HashSet(Arrays.asList(paramSocket.getEnabledProtocols()));
      if (this.tlsv11Supported) {
        localHashSet.add("TLSv1.1");
      }
      if (this.tlsv12Supported) {
        localHashSet.add("TLSv1.2");
      }
      paramSocket.setEnabledProtocols((String[])localHashSet.toArray(new String[0]));
    }
  }
  
  public Socket createSocket(String paramString, int paramInt)
    throws IOException
  {
    return fixupSocket(this.under.createSocket(paramString, paramInt));
  }
  
  public Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws IOException
  {
    return fixupSocket(this.under.createSocket(paramString, paramInt1, paramInetAddress, paramInt2));
  }
  
  public Socket createSocket(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    return fixupSocket(this.under.createSocket(paramInetAddress, paramInt));
  }
  
  public Socket createSocket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2)
    throws IOException
  {
    return fixupSocket(this.under.createSocket(paramInetAddress1, paramInt1, paramInetAddress2, paramInt2));
  }
  
  public Socket createSocket(Socket paramSocket, String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    return fixupSocket(this.under.createSocket(paramSocket, paramString, paramInt, paramBoolean));
  }
  
  public String[] getDefaultCipherSuites()
  {
    return this.under.getDefaultCipherSuites();
  }
  
  public String[] getSupportedCipherSuites()
  {
    return this.under.getSupportedCipherSuites();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/stripe/android/net/StripeSSLSocketFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */