package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MovieBox;
import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.DataSource;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import com.googlecode.mp4parser.util.Logger;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.WritableByteChannel;
import java.util.Iterator;
import java.util.List;

@DoNotParseDetail
public class IsoFile
  extends BasicContainer
  implements Closeable
{
  private static Logger LOG = Logger.getLogger(IsoFile.class);
  
  public IsoFile(DataSource paramDataSource)
    throws IOException
  {
    this(paramDataSource, new PropertyBoxParserImpl(new String[0]));
  }
  
  public IsoFile(DataSource paramDataSource, BoxParser paramBoxParser)
    throws IOException
  {
    initContainer(paramDataSource, paramDataSource.size(), paramBoxParser);
  }
  
  public IsoFile(String paramString)
    throws IOException
  {
    this(new FileDataSourceImpl(new File(paramString)));
  }
  
  public static String bytesToFourCC(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = new byte[4];
    if (paramArrayOfByte != null) {
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, Math.min(paramArrayOfByte.length, 4));
    }
    try
    {
      paramArrayOfByte = new String(arrayOfByte, "ISO-8859-1");
      return paramArrayOfByte;
    }
    catch (UnsupportedEncodingException paramArrayOfByte)
    {
      throw new Error("Required character encoding is missing", paramArrayOfByte);
    }
  }
  
  public static byte[] fourCCtoBytes(String paramString)
  {
    byte[] arrayOfByte = new byte[4];
    int i;
    if (paramString != null) {
      i = 0;
    }
    for (;;)
    {
      if (i >= Math.min(4, paramString.length())) {
        return arrayOfByte;
      }
      arrayOfByte[i] = ((byte)paramString.charAt(i));
      i += 1;
    }
  }
  
  public void close()
    throws IOException
  {
    this.dataSource.close();
  }
  
  public void getBox(WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    writeContainer(paramWritableByteChannel);
  }
  
  public MovieBox getMovieBox()
  {
    Iterator localIterator = getBoxes().iterator();
    Box localBox;
    do
    {
      if (!localIterator.hasNext()) {
        return null;
      }
      localBox = (Box)localIterator.next();
    } while (!(localBox instanceof MovieBox));
    return (MovieBox)localBox;
  }
  
  public long getSize()
  {
    return getContainerSize();
  }
  
  public String toString()
  {
    return "model(" + this.dataSource.toString() + ")";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/IsoFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */