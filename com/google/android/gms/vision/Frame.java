package com.google.android.gms.vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.nio.ByteBuffer;

public class Frame
{
  public static final int ROTATION_0 = 0;
  public static final int ROTATION_180 = 2;
  public static final int ROTATION_270 = 3;
  public static final int ROTATION_90 = 1;
  private Metadata aKA = new Metadata();
  private ByteBuffer aKB = null;
  private Bitmap mBitmap = null;
  
  private ByteBuffer zzclo()
  {
    int i = 0;
    int j = this.mBitmap.getWidth();
    int k = this.mBitmap.getHeight();
    int[] arrayOfInt = new int[j * k];
    this.mBitmap.getPixels(arrayOfInt, 0, j, 0, 0, j, k);
    byte[] arrayOfByte = new byte[j * k];
    while (i < arrayOfInt.length)
    {
      arrayOfByte[i] = ((byte)(int)(Color.red(arrayOfInt[i]) * 0.299F + Color.green(arrayOfInt[i]) * 0.587F + Color.blue(arrayOfInt[i]) * 0.114F));
      i += 1;
    }
    return ByteBuffer.wrap(arrayOfByte);
  }
  
  public Bitmap getBitmap()
  {
    return this.mBitmap;
  }
  
  public ByteBuffer getGrayscaleImageData()
  {
    if (this.mBitmap != null) {
      return zzclo();
    }
    return this.aKB;
  }
  
  public Metadata getMetadata()
  {
    return this.aKA;
  }
  
  public static class Builder
  {
    private Frame aKC = new Frame(null);
    
    public Frame build()
    {
      if ((Frame.zza(this.aKC) == null) && (Frame.zzb(this.aKC) == null)) {
        throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
      }
      return this.aKC;
    }
    
    public Builder setBitmap(Bitmap paramBitmap)
    {
      int i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      Frame.zza(this.aKC, paramBitmap);
      paramBitmap = this.aKC.getMetadata();
      Frame.Metadata.zza(paramBitmap, i);
      Frame.Metadata.zzb(paramBitmap, j);
      return this;
    }
    
    public Builder setId(int paramInt)
    {
      Frame.Metadata.zzd(this.aKC.getMetadata(), paramInt);
      return this;
    }
    
    public Builder setImageData(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3)
    {
      if (paramByteBuffer == null) {
        throw new IllegalArgumentException("Null image data supplied.");
      }
      if (paramByteBuffer.capacity() < paramInt1 * paramInt2) {
        throw new IllegalArgumentException("Invalid image data size.");
      }
      switch (paramInt3)
      {
      default: 
        throw new IllegalArgumentException(37 + "Unsupported image format: " + paramInt3);
      }
      Frame.zza(this.aKC, paramByteBuffer);
      paramByteBuffer = this.aKC.getMetadata();
      Frame.Metadata.zza(paramByteBuffer, paramInt1);
      Frame.Metadata.zzb(paramByteBuffer, paramInt2);
      Frame.Metadata.zzc(paramByteBuffer, paramInt3);
      return this;
    }
    
    public Builder setRotation(int paramInt)
    {
      Frame.Metadata.zze(this.aKC.getMetadata(), paramInt);
      return this;
    }
    
    public Builder setTimestampMillis(long paramLong)
    {
      Frame.Metadata.zza(this.aKC.getMetadata(), paramLong);
      return this;
    }
  }
  
  public static class Metadata
  {
    private long abY;
    private int format = -1;
    private int mId;
    private int zzajw;
    private int zzajx;
    private int zzbvy;
    
    public Metadata() {}
    
    public Metadata(Metadata paramMetadata)
    {
      this.zzajw = paramMetadata.getWidth();
      this.zzajx = paramMetadata.getHeight();
      this.mId = paramMetadata.getId();
      this.abY = paramMetadata.getTimestampMillis();
      this.zzbvy = paramMetadata.getRotation();
    }
    
    public int getFormat()
    {
      return this.format;
    }
    
    public int getHeight()
    {
      return this.zzajx;
    }
    
    public int getId()
    {
      return this.mId;
    }
    
    public int getRotation()
    {
      return this.zzbvy;
    }
    
    public long getTimestampMillis()
    {
      return this.abY;
    }
    
    public int getWidth()
    {
      return this.zzajw;
    }
    
    public void zzclp()
    {
      if (this.zzbvy % 2 != 0)
      {
        int i = this.zzajw;
        this.zzajw = this.zzajx;
        this.zzajx = i;
      }
      this.zzbvy = 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/Frame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */