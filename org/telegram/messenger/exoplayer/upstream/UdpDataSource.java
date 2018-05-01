package org.telegram.messenger.exoplayer.upstream;

import android.net.Uri;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public final class UdpDataSource
  implements UriDataSource
{
  public static final int DEAFULT_SOCKET_TIMEOUT_MILLIS = 8000;
  public static final int DEFAULT_MAX_PACKET_SIZE = 2000;
  private InetAddress address;
  private DataSpec dataSpec;
  private final TransferListener listener;
  private MulticastSocket multicastSocket;
  private boolean opened;
  private final DatagramPacket packet;
  private byte[] packetBuffer;
  private int packetRemaining;
  private DatagramSocket socket;
  private InetSocketAddress socketAddress;
  private final int socketTimeoutMillis;
  
  public UdpDataSource(TransferListener paramTransferListener)
  {
    this(paramTransferListener, 2000);
  }
  
  public UdpDataSource(TransferListener paramTransferListener, int paramInt)
  {
    this(paramTransferListener, paramInt, 8000);
  }
  
  public UdpDataSource(TransferListener paramTransferListener, int paramInt1, int paramInt2)
  {
    this.listener = paramTransferListener;
    this.socketTimeoutMillis = paramInt2;
    this.packetBuffer = new byte[paramInt1];
    this.packet = new DatagramPacket(this.packetBuffer, 0, paramInt1);
  }
  
  public void close()
  {
    if (this.multicastSocket != null) {}
    try
    {
      this.multicastSocket.leaveGroup(this.address);
      this.multicastSocket = null;
      if (this.socket != null)
      {
        this.socket.close();
        this.socket = null;
      }
      this.address = null;
      this.socketAddress = null;
      this.packetRemaining = 0;
      if (this.opened)
      {
        this.opened = false;
        if (this.listener != null) {
          this.listener.onTransferEnd();
        }
      }
      return;
    }
    catch (IOException localIOException)
    {
      for (;;) {}
    }
  }
  
  public String getUri()
  {
    if (this.dataSpec == null) {
      return null;
    }
    return this.dataSpec.uri.toString();
  }
  
  public long open(DataSpec paramDataSpec)
    throws UdpDataSource.UdpDataSourceException
  {
    this.dataSpec = paramDataSpec;
    String str = paramDataSpec.uri.getHost();
    int i = paramDataSpec.uri.getPort();
    for (;;)
    {
      try
      {
        this.address = InetAddress.getByName(str);
        this.socketAddress = new InetSocketAddress(this.address, i);
        if (this.address.isMulticastAddress())
        {
          this.multicastSocket = new MulticastSocket(this.socketAddress);
          this.multicastSocket.joinGroup(this.address);
          this.socket = this.multicastSocket;
        }
      }
      catch (IOException paramDataSpec)
      {
        throw new UdpDataSourceException(paramDataSpec);
      }
      try
      {
        this.socket.setSoTimeout(this.socketTimeoutMillis);
        this.opened = true;
        if (this.listener != null) {
          this.listener.onTransferStart();
        }
        return -1L;
      }
      catch (SocketException paramDataSpec)
      {
        throw new UdpDataSourceException(paramDataSpec);
      }
      this.socket = new DatagramSocket(this.socketAddress);
    }
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws UdpDataSource.UdpDataSourceException
  {
    if (this.packetRemaining == 0) {}
    try
    {
      this.socket.receive(this.packet);
      this.packetRemaining = this.packet.getLength();
      if (this.listener != null) {
        this.listener.onBytesTransferred(this.packetRemaining);
      }
      int i = this.packet.getLength();
      int j = this.packetRemaining;
      paramInt2 = Math.min(this.packetRemaining, paramInt2);
      System.arraycopy(this.packetBuffer, i - j, paramArrayOfByte, paramInt1, paramInt2);
      this.packetRemaining -= paramInt2;
      return paramInt2;
    }
    catch (IOException paramArrayOfByte)
    {
      throw new UdpDataSourceException(paramArrayOfByte);
    }
  }
  
  public static final class UdpDataSourceException
    extends IOException
  {
    public UdpDataSourceException(IOException paramIOException)
    {
      super();
    }
    
    public UdpDataSourceException(String paramString)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/upstream/UdpDataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */