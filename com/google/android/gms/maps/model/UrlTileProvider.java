package com.google.android.gms.maps.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public abstract class UrlTileProvider
  implements TileProvider
{
  private final int zzrW;
  private final int zzrX;
  
  public UrlTileProvider(int paramInt1, int paramInt2)
  {
    this.zzrW = paramInt1;
    this.zzrX = paramInt2;
  }
  
  private static long zza(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['က'];
    int i;
    for (long l = 0L;; l += i)
    {
      i = paramInputStream.read(arrayOfByte);
      if (i == -1) {
        break;
      }
      paramOutputStream.write(arrayOfByte, 0, i);
    }
    return l;
  }
  
  public final Tile getTile(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject = getTileUrl(paramInt1, paramInt2, paramInt3);
    if (localObject == null) {
      return NO_TILE;
    }
    try
    {
      paramInt1 = this.zzrW;
      paramInt2 = this.zzrX;
      localObject = ((URL)localObject).openStream();
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      zza((InputStream)localObject, localByteArrayOutputStream);
      localObject = new Tile(paramInt1, paramInt2, localByteArrayOutputStream.toByteArray());
      return (Tile)localObject;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public abstract URL getTileUrl(int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/UrlTileProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */