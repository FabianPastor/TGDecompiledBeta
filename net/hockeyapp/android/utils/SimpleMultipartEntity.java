package net.hockeyapp.android.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class SimpleMultipartEntity
{
  private static final char[] BOUNDARY_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
  private String mBoundary;
  private boolean mIsSetFirst = false;
  private boolean mIsSetLast = false;
  private OutputStream mOut;
  private File mTempFile;
  
  public SimpleMultipartEntity(File paramFile)
  {
    this.mTempFile = paramFile;
    try
    {
      paramFile = new java/io/FileOutputStream;
      paramFile.<init>(this.mTempFile);
      this.mOut = paramFile;
      paramFile = new StringBuilder();
      Random localRandom = new Random();
      for (int i = 0; i < 30; i++) {
        paramFile.append(BOUNDARY_CHARS[localRandom.nextInt(BOUNDARY_CHARS.length)]);
      }
    }
    catch (IOException paramFile)
    {
      for (;;)
      {
        HockeyLog.error("Failed to open temp file", paramFile);
      }
      this.mBoundary = paramFile.toString();
    }
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
      Object localObject = new java/lang/StringBuilder;
      ((StringBuilder)localObject).<init>();
      localObject = "Content-Type: " + paramString3 + "\r\n";
      OutputStream localOutputStream = this.mOut;
      paramString3 = new java/lang/StringBuilder;
      paramString3.<init>();
      localOutputStream.write(("Content-Disposition: form-data; name=\"" + paramString1 + "\"; filename=\"" + paramString2 + "\"\r\n").getBytes());
      this.mOut.write(((String)localObject).getBytes());
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
        try
        {
          for (;;)
          {
            paramInputStream.close();
            return;
            paramString2 = this.mOut;
            paramString1 = new java/lang/StringBuilder;
            paramString1.<init>();
            paramString2.write(("\r\n--" + this.mBoundary + "\r\n").getBytes());
          }
        }
        catch (IOException paramString1)
        {
          for (;;) {}
        }
      }
      catch (IOException paramString2)
      {
        for (;;) {}
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
    return this.mTempFile.length();
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
    if (this.mIsSetLast) {}
    for (;;)
    {
      return;
      try
      {
        OutputStream localOutputStream = this.mOut;
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localOutputStream.write(("\r\n--" + this.mBoundary + "--\r\n").getBytes());
        this.mOut.flush();
        this.mOut.close();
        this.mOut = null;
        this.mIsSetLast = true;
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          HockeyLog.error("Failed to close temp file", localIOException);
        }
      }
    }
  }
  
  public void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    writeLastBoundaryIfNeeds();
    FileInputStream localFileInputStream = new FileInputStream(this.mTempFile);
    BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(paramOutputStream);
    paramOutputStream = new byte['က'];
    for (;;)
    {
      int i = localFileInputStream.read(paramOutputStream);
      if (i == -1) {
        break;
      }
      localBufferedOutputStream.write(paramOutputStream, 0, i);
    }
    localFileInputStream.close();
    localBufferedOutputStream.flush();
    localBufferedOutputStream.close();
    this.mTempFile.delete();
    this.mTempFile = null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/SimpleMultipartEntity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */