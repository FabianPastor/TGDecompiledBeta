package org.telegram.messenger.exoplayer.extractor.webm;

import java.io.IOException;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;

abstract interface EbmlReader
{
  public static final int TYPE_BINARY = 4;
  public static final int TYPE_FLOAT = 5;
  public static final int TYPE_MASTER = 1;
  public static final int TYPE_STRING = 3;
  public static final int TYPE_UNKNOWN = 0;
  public static final int TYPE_UNSIGNED_INT = 2;
  
  public abstract void init(EbmlReaderOutput paramEbmlReaderOutput);
  
  public abstract boolean read(ExtractorInput paramExtractorInput)
    throws ParserException, IOException, InterruptedException;
  
  public abstract void reset();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/webm/EbmlReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */