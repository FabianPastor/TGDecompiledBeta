package org.telegram.messenger.exoplayer.extractor;

import java.io.IOException;

public abstract interface Extractor
{
  public static final int RESULT_CONTINUE = 0;
  public static final int RESULT_END_OF_INPUT = -1;
  public static final int RESULT_SEEK = 1;
  
  public abstract void init(ExtractorOutput paramExtractorOutput);
  
  public abstract int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException;
  
  public abstract void release();
  
  public abstract void seek();
  
  public abstract boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/Extractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */