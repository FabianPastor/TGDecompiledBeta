package com.google.android.gms.internal.measurement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

final class zzjx
  extends SSLSocket
{
  private final SSLSocket zzare;
  
  zzjx(zzjw paramzzjw, SSLSocket paramSSLSocket)
  {
    this.zzare = paramSSLSocket;
  }
  
  public final void addHandshakeCompletedListener(HandshakeCompletedListener paramHandshakeCompletedListener)
  {
    this.zzare.addHandshakeCompletedListener(paramHandshakeCompletedListener);
  }
  
  public final void bind(SocketAddress paramSocketAddress)
    throws IOException
  {
    this.zzare.bind(paramSocketAddress);
  }
  
  public final void close()
    throws IOException
  {
    try
    {
      this.zzare.close();
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final void connect(SocketAddress paramSocketAddress)
    throws IOException
  {
    this.zzare.connect(paramSocketAddress);
  }
  
  public final void connect(SocketAddress paramSocketAddress, int paramInt)
    throws IOException
  {
    this.zzare.connect(paramSocketAddress, paramInt);
  }
  
  public final boolean equals(Object paramObject)
  {
    return this.zzare.equals(paramObject);
  }
  
  public final SocketChannel getChannel()
  {
    return this.zzare.getChannel();
  }
  
  public final boolean getEnableSessionCreation()
  {
    return this.zzare.getEnableSessionCreation();
  }
  
  public final String[] getEnabledCipherSuites()
  {
    return this.zzare.getEnabledCipherSuites();
  }
  
  public final String[] getEnabledProtocols()
  {
    return this.zzare.getEnabledProtocols();
  }
  
  public final InetAddress getInetAddress()
  {
    return this.zzare.getInetAddress();
  }
  
  public final InputStream getInputStream()
    throws IOException
  {
    return this.zzare.getInputStream();
  }
  
  public final boolean getKeepAlive()
    throws SocketException
  {
    return this.zzare.getKeepAlive();
  }
  
  public final InetAddress getLocalAddress()
  {
    return this.zzare.getLocalAddress();
  }
  
  public final int getLocalPort()
  {
    return this.zzare.getLocalPort();
  }
  
  public final SocketAddress getLocalSocketAddress()
  {
    return this.zzare.getLocalSocketAddress();
  }
  
  public final boolean getNeedClientAuth()
  {
    return this.zzare.getNeedClientAuth();
  }
  
  public final boolean getOOBInline()
    throws SocketException
  {
    return this.zzare.getOOBInline();
  }
  
  public final OutputStream getOutputStream()
    throws IOException
  {
    return this.zzare.getOutputStream();
  }
  
  public final int getPort()
  {
    return this.zzare.getPort();
  }
  
  public final int getReceiveBufferSize()
    throws SocketException
  {
    try
    {
      int i = this.zzare.getReceiveBufferSize();
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final SocketAddress getRemoteSocketAddress()
  {
    return this.zzare.getRemoteSocketAddress();
  }
  
  public final boolean getReuseAddress()
    throws SocketException
  {
    return this.zzare.getReuseAddress();
  }
  
  public final int getSendBufferSize()
    throws SocketException
  {
    try
    {
      int i = this.zzare.getSendBufferSize();
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final SSLSession getSession()
  {
    return this.zzare.getSession();
  }
  
  public final int getSoLinger()
    throws SocketException
  {
    return this.zzare.getSoLinger();
  }
  
  public final int getSoTimeout()
    throws SocketException
  {
    try
    {
      int i = this.zzare.getSoTimeout();
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final String[] getSupportedCipherSuites()
  {
    return this.zzare.getSupportedCipherSuites();
  }
  
  public final String[] getSupportedProtocols()
  {
    return this.zzare.getSupportedProtocols();
  }
  
  public final boolean getTcpNoDelay()
    throws SocketException
  {
    return this.zzare.getTcpNoDelay();
  }
  
  public final int getTrafficClass()
    throws SocketException
  {
    return this.zzare.getTrafficClass();
  }
  
  public final boolean getUseClientMode()
  {
    return this.zzare.getUseClientMode();
  }
  
  public final boolean getWantClientAuth()
  {
    return this.zzare.getWantClientAuth();
  }
  
  public final boolean isBound()
  {
    return this.zzare.isBound();
  }
  
  public final boolean isClosed()
  {
    return this.zzare.isClosed();
  }
  
  public final boolean isConnected()
  {
    return this.zzare.isConnected();
  }
  
  public final boolean isInputShutdown()
  {
    return this.zzare.isInputShutdown();
  }
  
  public final boolean isOutputShutdown()
  {
    return this.zzare.isOutputShutdown();
  }
  
  public final void removeHandshakeCompletedListener(HandshakeCompletedListener paramHandshakeCompletedListener)
  {
    this.zzare.removeHandshakeCompletedListener(paramHandshakeCompletedListener);
  }
  
  public final void sendUrgentData(int paramInt)
    throws IOException
  {
    this.zzare.sendUrgentData(paramInt);
  }
  
  public final void setEnableSessionCreation(boolean paramBoolean)
  {
    this.zzare.setEnableSessionCreation(paramBoolean);
  }
  
  public final void setEnabledCipherSuites(String[] paramArrayOfString)
  {
    this.zzare.setEnabledCipherSuites(paramArrayOfString);
  }
  
  public final void setEnabledProtocols(String[] paramArrayOfString)
  {
    String[] arrayOfString = paramArrayOfString;
    if (paramArrayOfString != null)
    {
      arrayOfString = paramArrayOfString;
      if (Arrays.asList(paramArrayOfString).contains("SSLv3"))
      {
        paramArrayOfString = new ArrayList(Arrays.asList(this.zzare.getEnabledProtocols()));
        if (paramArrayOfString.size() > 1) {
          paramArrayOfString.remove("SSLv3");
        }
        arrayOfString = (String[])paramArrayOfString.toArray(new String[paramArrayOfString.size()]);
      }
    }
    this.zzare.setEnabledProtocols(arrayOfString);
  }
  
  public final void setKeepAlive(boolean paramBoolean)
    throws SocketException
  {
    this.zzare.setKeepAlive(paramBoolean);
  }
  
  public final void setNeedClientAuth(boolean paramBoolean)
  {
    this.zzare.setNeedClientAuth(paramBoolean);
  }
  
  public final void setOOBInline(boolean paramBoolean)
    throws SocketException
  {
    this.zzare.setOOBInline(paramBoolean);
  }
  
  public final void setPerformancePreferences(int paramInt1, int paramInt2, int paramInt3)
  {
    this.zzare.setPerformancePreferences(paramInt1, paramInt2, paramInt3);
  }
  
  public final void setReceiveBufferSize(int paramInt)
    throws SocketException
  {
    try
    {
      this.zzare.setReceiveBufferSize(paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final void setReuseAddress(boolean paramBoolean)
    throws SocketException
  {
    this.zzare.setReuseAddress(paramBoolean);
  }
  
  public final void setSendBufferSize(int paramInt)
    throws SocketException
  {
    try
    {
      this.zzare.setSendBufferSize(paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final void setSoLinger(boolean paramBoolean, int paramInt)
    throws SocketException
  {
    this.zzare.setSoLinger(paramBoolean, paramInt);
  }
  
  public final void setSoTimeout(int paramInt)
    throws SocketException
  {
    try
    {
      this.zzare.setSoTimeout(paramInt);
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final void setTcpNoDelay(boolean paramBoolean)
    throws SocketException
  {
    this.zzare.setTcpNoDelay(paramBoolean);
  }
  
  public final void setTrafficClass(int paramInt)
    throws SocketException
  {
    this.zzare.setTrafficClass(paramInt);
  }
  
  public final void setUseClientMode(boolean paramBoolean)
  {
    this.zzare.setUseClientMode(paramBoolean);
  }
  
  public final void setWantClientAuth(boolean paramBoolean)
  {
    this.zzare.setWantClientAuth(paramBoolean);
  }
  
  public final void shutdownInput()
    throws IOException
  {
    this.zzare.shutdownInput();
  }
  
  public final void shutdownOutput()
    throws IOException
  {
    this.zzare.shutdownOutput();
  }
  
  public final void startHandshake()
    throws IOException
  {
    this.zzare.startHandshake();
  }
  
  public final String toString()
  {
    return this.zzare.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */