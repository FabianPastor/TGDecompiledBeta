package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

abstract class StreamReader
{
  protected ExtractorOutput extractorOutput;
  protected final OggParser oggParser = new OggParser();
  protected final ParsableByteArray scratch = new ParsableByteArray(new byte[65025], 0);
  protected TrackOutput trackOutput;
  
  void init(ExtractorOutput paramExtractorOutput, TrackOutput paramTrackOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = paramTrackOutput;
  }
  
  abstract int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException;
  
  void seek()
  {
    this.oggParser.reset();
    this.scratch.reset();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ogg/StreamReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */