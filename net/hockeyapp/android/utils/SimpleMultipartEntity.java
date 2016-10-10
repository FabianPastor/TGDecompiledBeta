package net.hockeyapp.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class SimpleMultipartEntity
{
  private static final char[] BOUNDARY_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  private String mBoundary;
  private boolean mIsSetFirst = false;
  private boolean mIsSetLast = false;
  private ByteArrayOutputStream mOut = new ByteArrayOutputStream();
  
  public SimpleMultipartEntity()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    Random localRandom = new Random();
    int i = 0;
    while (i < 30)
    {
      localStringBuffer.append(BOUNDARY_CHARS[localRandom.nextInt(BOUNDARY_CHARS.length)]);
      i += 1;
    }
    this.mBoundary = localStringBuffer.toString();
  }
  
  public void addPart(String paramString, File paramFile, boolean paramBoolean)
    throws IOException
  {
    addPart(paramString, paramFile.getName(), new FileInputStream(paramFile), paramBoolean);
  }
  
  public void addPart(String paramString1, String paramString2)
    throws IOException
  {
    writeFirstBoundaryIfNeeds();
    this.mOut.write(("Content-Disposition: form-data; name=\"" + paramString1 + "\"\r\n").getBytes());
    this.mOut.write("Content-Type: text/plain; charset=UTF-8\r\n".getBytes());
    this.mOut.write("Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes());
    this.mOut.write(paramString2.getBytes());
    this.mOut.write(("\r\n--" + this.mBoundary + "\r\n").getBytes());
  }
  
  public void addPart(String paramString1, String paramString2, InputStream paramInputStream, String paramString3, boolean paramBoolean)
    throws IOException
  {
    writeFirstBoundaryIfNeeds();
    try
    {
      paramString3 = "Content-Type: " + paramString3 + "\r\n";
      this.mOut.write(("Content-Disposition: form-data; name=\"" + paramString1 + "\"; filename=\"" + paramString2 + "\"\r\n").getBytes());
      this.mOut.write(paramString3.getBytes());
      this.mOut.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());
      paramString1 = new byte['က'];
      for (;;)
      {
        int i = paramInputStream.read(paramString1);
        if (i == -1) {
          break;
        }
        this.mOut.write(paramString1, 0, i);
      }
      try
      {
        paramInputStream.close();
        throw paramString1;
        this.mOut.flush();
        if (paramBoolean) {
          writeLastBoundaryIfNeeds();
        }
        for (;;)
        {
          try
          {
            paramInputStream.close();
            return;
          }
          catch (IOException paramString1)
          {
            paramString1.printStackTrace();
            return;
          }
          this.mOut.write(("\r\n--" + this.mBoundary + "\r\n").getBytes());
        }
      }
      catch (IOException paramString2)
      {
        for (;;)
        {
          paramString2.printStackTrace();
        }
      }
    }
    finally {}
  }
  
  public void addPart(String paramString1, String paramString2, InputStream paramInputStream, boolean paramBoolean)
    throws IOException
  {
    addPart(paramString1, paramString2, paramInputStream, "application/octet-stream", paramBoolean);
  }
  
  public String getBoundary()
  {
    return this.mBoundary;
  }
  
  public long getContentLength()
  {
    writeLastBoundaryIfNeeds();
    return this.mOut.toByteArray().length;
  }
  
  public String getContentType()
  {
    return "multipart/form-data; boundary=" + getBoundary();
  }
  
  public ByteArrayOutputStream getOutputStream()
  {
    writeLastBoundaryIfNeeds();
    return this.mOut;
  }
  
  public void writeFirstBoundaryIfNeeds()
    throws IOException
  {
    if (!this.mIsSetFirst) {
      this.mOut.write(("--" + this.mBoundary + "\r\n").getBytes());
    }
    this.mIsSetFirst = true;
  }
  
  public void writeLastBoundaryIfNeeds()
  {
    if (this.mIsSetLast) {
      return;
    }
    try
    {
      this.mOut.write(("\r\n--" + this.mBoundary + "--\r\n").getBytes());
      this.mIsSetLast = true;
      return;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        localIOException.printStackTrace();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/SimpleMultipartEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */