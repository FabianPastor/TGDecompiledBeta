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
        FileLog.e("tmessages", paramRectF);
      }
      storeData(paramByteBuffer);
    }
  }
  
  private void storeData(ByteBuffer paramByteBuffer)
  {
    FileOutputStream localFileOutputStream;
    try
    {
      byte[] arrayOfByte = paramByteBuffer.array();
      localFileOutputStream = new FileOutputStream(this.file);
      Deflater localDeflater = new Deflater(1, true);
      localDeflater.setInput(arrayOfByte, paramByteBuffer.arrayOffset(), paramByteBuffer.remaining());
      localDeflater.finish();
      paramByteBuffer = new byte['Ѐ'];
      while (!localDeflater.finished()) {
        localFileOutputStream.write(paramByteBuffer, 0, localDeflater.deflate(paramByteBuffer));
      }
      localDeflater.end();
    }
    catch (Exception paramByteBuffer)
    {
      FileLog.e("tmessages", paramByteBuffer);
      return;
    }
    localFileOutputStream.close();
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
      ByteArrayOutputStream localByteArrayOutputStream;
      Inflater localInflater;
      try
      {
        localObject = new byte['Ѐ'];
        byte[] arrayOfByte = new byte['Ѐ'];
        FileInputStream localFileInputStream = new FileInputStream(this.file);
        localByteArrayOutputStream = new ByteArrayOutputStream();
        localInflater = new Inflater(true);
        int i = localFileInputStream.read((byte[])localObject);
        if (i != -1) {
          localInflater.setInput((byte[])localObject, 0, i);
        }
        i = localInflater.inflate(arrayOfByte, 0, arrayOfByte.length);
        if (i != 0)
        {
          localByteArrayOutputStream.write(arrayOfByte, 0, i);
          continue;
        }
        if (!localInflater.finished()) {
          break label144;
        }
      }
      catch (Exception localException)
      {
        FileLog.e("tmessages", localException);
        return null;
      }
      localInflater.end();
      Object localObject = ByteBuffer.wrap(localByteArrayOutputStream.toByteArray(), 0, localByteArrayOutputStream.size());
      localByteArrayOutputStream.close();
      localException.close();
      return (ByteBuffer)localObject;
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