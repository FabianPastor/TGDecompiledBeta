package com.google.android.gms.vision;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.nio.ByteBuffer;

public class Frame
{
  private Bitmap mBitmap = null;
  private Metadata zzam = new Metadata();
  private ByteBuffer zzan = null;
  
  public ByteBuffer getGrayscaleImageData()
  {
    int i = 0;
    if (this.mBitmap != null)
    {
      int j = this.mBitmap.getWidth();
      int k = this.mBitmap.getHeight();
      int[] arrayOfInt = new int[j * k];
      this.mBitmap.getPixels(arrayOfInt, 0, j, 0, 0, j, k);
      localObject = new byte[j * k];
      while (i < arrayOfInt.length)
      {
        localObject[i] = ((byte)(byte)(int)(Color.red(arrayOfInt[i]) * 0.299F + Color.green(arrayOfInt[i]) * 0.587F + Color.blue(arrayOfInt[i]) * 0.114F));
        i++;
      }
    }
    for (Object localObject = ByteBuffer.wrap((byte[])localObject);; localObject = this.zzan) {
      return (ByteBuffer)localObject;
    }
  }
  
  public Metadata getMetadata()
  {
    return this.zzam;
  }
  
  public static class Builder
  {
    private Frame zzao = new Frame(null);
    
    public Frame build()
    {
      if ((Frame.zza(this.zzao) == null) && (Frame.zzb(this.zzao) == null)) {
        throw new IllegalStateException("Missing image data.  Call either setBitmap or setImageData to specify the image");
      }
      return this.zzao;
    }
    
    public Builder setBitmap(Bitmap paramBitmap)
    {
      int i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      Frame.zza(this.zzao, paramBitmap);
      paramBitmap = this.zzao.getMetadata();
      Frame.Metadata.zza(paramBitmap, i);
      Frame.Metadata.zzb(paramBitmap, j);
      return this;
    }
    
    public Builder setRotation(int paramInt)
    {
      Frame.Metadata.zze(this.zzao.getMetadata(), paramInt);
      return this;
    }
  }
  
  public static class Metadata
  {
    private int format = -1;
    private int mId;
    private int zzap;
    private int zzaq;
    private long zzar;
    private int zzg;
    
    public int getHeight()
    {
      return this.zzaq;
    }
    
    public int getId()
    {
      return this.mId;
    }
    
    public int getRotation()
    {
      return this.zzg;
    }
    
    public long getTimestampMillis()
    {
      return this.zzar;
    }
    
    public int getWidth()
    {
      return this.zzap;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/vision/Frame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */