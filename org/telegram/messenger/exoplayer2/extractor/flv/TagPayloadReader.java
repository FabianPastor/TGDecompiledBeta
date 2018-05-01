package org.telegram.messenger.exoplayer2.extractor.flv;

import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

abstract class TagPayloadReader
{
  protected final TrackOutput output;
  
  protected TagPayloadReader(TrackOutput paramTrackOutput)
  {
    this.output = paramTrackOutput;
  }
  
  public final void consume(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException
  {
    if (parseHeader(paramParsableByteArray)) {
      parsePayload(paramParsableByteArray, paramLong);
    }
  }
  
  protected abstract boolean parseHeader(ParsableByteArray paramParsableByteArray)
    throws ParserException;
  
  protected abstract void parsePayload(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException;
  
  public abstract void seek();
  
  public static final class UnsupportedFormatException
    extends ParserException
  {
    public UnsupportedFormatException(String paramString)
    {
      super();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/flv/TagPayloadReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */