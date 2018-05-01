package org.telegram.messenger.exoplayer2.util;

import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class AtomicFile
{
  private static final String TAG = "AtomicFile";
  private final File backupName;
  private final File baseName;
  
  public AtomicFile(File paramFile)
  {
    this.baseName = paramFile;
    this.backupName = new File(paramFile.getPath() + ".bak");
  }
  
  private void restoreBackup()
  {
    if (this.backupName.exists())
    {
      this.baseName.delete();
      this.backupName.renameTo(this.baseName);
    }
  }
  
  public void delete()
  {
    this.baseName.delete();
    this.backupName.delete();
  }
  
  public void endWrite(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.close();
    this.backupName.delete();
  }
  
  public InputStream openRead()
    throws FileNotFoundException
  {
    restoreBackup();
    return new FileInputStream(this.baseName);
  }
  
  public OutputStream startWrite()
    throws IOException
  {
    if (this.baseName.exists())
    {
      if (this.backupName.exists()) {
        break label88;
      }
      if (!this.baseName.renameTo(this.backupName)) {
        Log.w("AtomicFile", "Couldn't rename file " + this.baseName + " to backup file " + this.backupName);
      }
    }
    try
    {
      for (;;)
      {
        AtomicFileOutputStream localAtomicFileOutputStream1 = new org/telegram/messenger/exoplayer2/util/AtomicFile$AtomicFileOutputStream;
        localAtomicFileOutputStream1.<init>(this.baseName);
        return localAtomicFileOutputStream1;
        label88:
        this.baseName.delete();
      }
    }
    catch (FileNotFoundException localFileNotFoundException1)
    {
      for (;;)
      {
        if (!this.baseName.getParentFile().mkdirs()) {
          throw new IOException("Couldn't create directory " + this.baseName, localFileNotFoundException1);
        }
        try
        {
          AtomicFileOutputStream localAtomicFileOutputStream2 = new AtomicFileOutputStream(this.baseName);
        }
        catch (FileNotFoundException localFileNotFoundException2)
        {
          throw new IOException("Couldn't create " + this.baseName, localFileNotFoundException2);
        }
      }
    }
  }
  
  private static final class AtomicFileOutputStream
    extends OutputStream
  {
    private boolean closed = false;
    private final FileOutputStream fileOutputStream;
    
    public AtomicFileOutputStream(File paramFile)
      throws FileNotFoundException
    {
      this.fileOutputStream = new FileOutputStream(paramFile);
    }
    
    public void close()
      throws IOException
    {
      if (this.closed) {}
      for (;;)
      {
        return;
        this.closed = true;
        flush();
        try
        {
          this.fileOutputStream.getFD().sync();
          this.fileOutputStream.close();
        }
        catch (IOException localIOException)
        {
          for (;;)
          {
            Log.w("AtomicFile", "Failed to sync file descriptor:", localIOException);
          }
        }
      }
    }
    
    public void flush()
      throws IOException
    {
      this.fileOutputStream.flush();
    }
    
    public void write(int paramInt)
      throws IOException
    {
      this.fileOutputStream.write(paramInt);
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      this.fileOutputStream.write(paramArrayOfByte);
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      this.fileOutputStream.write(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/AtomicFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */