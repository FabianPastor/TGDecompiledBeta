package org.telegram.messenger.exoplayer2.extractor.mkv;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;

abstract interface EbmlReaderOutput
{
  public abstract void binaryElement(int paramInt1, int paramInt2, ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException;
  
  public abstract void endMasterElement(int paramInt)
    throws ParserException;
  
  public abstract void floatElement(int paramInt, double paramDouble)
    throws ParserException;
  
  public abstract int getElementType(int paramInt);
  
  public abstract void integerElement(int paramInt, long paramLong)
    throws ParserException;
  
  public abstract boolean isLevel1Element(int paramInt);
  
  public abstract void startMasterElement(int paramInt, long paramLong1, long paramLong2)
    throws ParserException;
  
  public abstract void stringElement(int paramInt, String paramString)
    throws ParserException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mkv/EbmlReaderOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */