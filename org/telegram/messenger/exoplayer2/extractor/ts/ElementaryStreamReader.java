package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public abstract interface ElementaryStreamReader
{
  public abstract void consume(ParsableByteArray paramParsableByteArray)
    throws ParserException;
  
  public abstract void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator);
  
  public abstract void packetFinished();
  
  public abstract void packetStarted(long paramLong, boolean paramBoolean);
  
  public abstract void seek();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/ElementaryStreamReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */