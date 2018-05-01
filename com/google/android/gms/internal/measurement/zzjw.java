package com.google.android.gms.internal.measurement;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

final class zzjw
  extends SSLSocketFactory
{
  private final SSLSocketFactory zzard;
  
  zzjw()
  {
    this(HttpsURLConnection.getDefaultSSLSocketFactory());
  }
  
  private zzjw(SSLSocketFactory paramSSLSocketFactory)
  {
    this.zzard = paramSSLSocketFactory;
  }
  
  private final SSLSocket zza(SSLSocket paramSSLSocket)
  {
    return new zzjx(this, paramSSLSocket);
  }
  
  public final Socket createSocket()
    throws IOException
  {
    return zza((SSLSocket)this.zzard.createSocket());
  }
  
  public final Socket createSocket(String paramString, int paramInt)
    throws IOException
  {
    return zza((SSLSocket)this.zzard.createSocket(paramString, paramInt));
  }
  
  public final Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2)
    throws IOException
  {
    return zza((SSLSocket)this.zzard.createSocket(paramString, paramInt1, paramInetAddress, paramInt2));
  }
  
  public final Socket createSocket(InetAddress paramInetAddress, int paramInt)
    throws IOException
  {
    return zza((SSLSocket)this.zzard.createSocket(paramInetAddress, paramInt));
  }
  
  public final Socket createSocket(InetAddress paramInetAddress1, int paramInt1, InetAddress paramInetAddress2, int paramInt2)
    throws IOException
  {
    return zza((SSLSocket)this.zzard.createSocket(paramInetAddress1, paramInt1, paramInetAddress2, paramInt2));
  }
  
  public final Socket createSocket(Socket paramSocket, String paramString, int paramInt, boolean paramBoolean)
    throws IOException
  {
    return zza((SSLSocket)this.zzard.createSocket(paramSocket, paramString, paramInt, paramBoolean));
  }
  
  public final String[] getDefaultCipherSuites()
  {
    return this.zzard.getDefaultCipherSuites();
  }
  
  public final String[] getSupportedCipherSuites()
  {
    return this.zzard.getSupportedCipherSuites();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */