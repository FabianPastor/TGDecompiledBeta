package org.telegram.ui.Components.Paint;

import android.content.Context;
import android.graphics.RectF;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;

public class Slice
{
  private RectF bounds;
  private File file;
  
  public Slice(ByteBuffer paramByteBuffer, RectF paramRectF, DispatchQueue paramDispatchQueue)
  {
    this.bounds = paramRectF;
    try
    {
      this.file = File.createTempFile("paint", ".bin", ApplicationLoader.applicationContext.getCacheDir());
      if (this.file == null) {
        return;
      }
    }
    catch (Exception paramRectF)
    {
      for (;;)
      {
        FileLog.e(paramRectF);
        continue;
        storeData(paramByteBuffer);
      }
    }
  }
  
  private void storeData(ByteBuffer paramByteBuffer)
  {
    FileOutputStream localFileOutputStream;
    Deflater localDeflater;
    try
    {
      byte[] arrayOfByte = paramByteBuffer.array();
      localFileOutputStream = new java/io/FileOutputStream;
      localFileOutputStream.<init>(this.file);
      localDeflater = new java/util/zip/Deflater;
      localDeflater.<init>(1, true);
      localDeflater.setInput(arrayOfByte, paramByteBuffer.arrayOffset(), paramByteBuffer.remaining());
      localDeflater.finish();
      paramByteBuffer = new byte['Ѐ'];
      while (!localDeflater.finished())
      {
        localFileOutputStream.write(paramByteBuffer, 0, localDeflater.deflate(paramByteBuffer));
        continue;
        return;
      }
    }
    catch (Exception paramByteBuffer)
    {
      FileLog.e(paramByteBuffer);
    }
    for (;;)
    {
      localDeflater.end();
      localFileOutputStream.close();
    }
  }
  
  public void cleanResources()
  {
    if (this.file != null)
    {
      this.file.delete();
      this.file = null;
    }
  }
  
  public RectF getBounds()
  {
    return new RectF(this.bounds);
  }
  
  public ByteBuffer getData()
  {
    for (;;)
    {
      FileInputStream localFileInputStream;
      ByteArrayOutputStream localByteArrayOutputStream;
      Inflater localInflater;
      try
      {
        byte[] arrayOfByte1 = new byte['Ѐ'];
        byte[] arrayOfByte2 = new byte['Ѐ'];
        localFileInputStream = new java/io/FileInputStream;
        localFileInputStream.<init>(this.file);
        localByteArrayOutputStream = new java/io/ByteArrayOutputStream;
        localByteArrayOutputStream.<init>();
        localInflater = new java/util/zip/Inflater;
        localInflater.<init>(true);
        int i = localFileInputStream.read(arrayOfByte1);
        if (i != -1) {
          localInflater.setInput(arrayOfByte1, 0, i);
        }
        i = localInflater.inflate(arrayOfByte2, 0, arrayOfByte2.length);
        if (i != 0)
        {
          localByteArrayOutputStream.write(arrayOfByte2, 0, i);
          continue;
        }
        if (!localInflater.finished()) {
          break label144;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        localByteBuffer = null;
        return localByteBuffer;
      }
      localInflater.end();
      ByteBuffer localByteBuffer = ByteBuffer.wrap(localByteArrayOutputStream.toByteArray(), 0, localByteArrayOutputStream.size());
      localByteArrayOutputStream.close();
      localFileInputStream.close();
      continue;
      label144:
      boolean bool = localInflater.needsInput();
      if (!bool) {}
    }
  }
  
  public int getHeight()
  {
    return (int)this.bounds.height();
  }
  
  public int getWidth()
  {
    return (int)this.bounds.width();
  }
  
  public int getX()
  {
    return (int)this.bounds.left;
  }
  
  public int getY()
  {
    return (int)this.bounds.top;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Slice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */