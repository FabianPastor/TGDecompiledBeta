package org.telegram.messenger.exoplayer.hls;

import android.text.TextUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.extractor.ts.PtsTimestampAdjuster;
import org.telegram.messenger.exoplayer.text.webvtt.WebvttCueParser;
import org.telegram.messenger.exoplayer.text.webvtt.WebvttParserUtil;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class WebvttExtractor
  implements Extractor
{
  private static final Pattern LOCAL_TIMESTAMP = Pattern.compile("LOCAL:([^,]+)");
  private static final Pattern MEDIA_TIMESTAMP = Pattern.compile("MPEGTS:(\\d+)");
  private ExtractorOutput output;
  private final PtsTimestampAdjuster ptsTimestampAdjuster;
  private byte[] sampleData;
  private final ParsableByteArray sampleDataWrapper;
  private int sampleSize;
  
  public WebvttExtractor(PtsTimestampAdjuster paramPtsTimestampAdjuster)
  {
    this.ptsTimestampAdjuster = paramPtsTimestampAdjuster;
    this.sampleDataWrapper = new ParsableByteArray();
    this.sampleData = new byte['Ѐ'];
  }
  
  private TrackOutput buildTrackOutput(long paramLong)
  {
    TrackOutput localTrackOutput = this.output.track(0);
    localTrackOutput.format(MediaFormat.createTextFormat("id", "text/vtt", -1, -1L, "en", paramLong));
    this.output.endTracks();
    return localTrackOutput;
  }
  
  private void processSample()
    throws ParserException
  {
    Object localObject = new ParsableByteArray(this.sampleData);
    WebvttParserUtil.validateWebvttHeaderLine((ParsableByteArray)localObject);
    long l2 = 0L;
    long l1 = 0L;
    for (;;)
    {
      String str = ((ParsableByteArray)localObject).readLine();
      if (TextUtils.isEmpty(str)) {
        break;
      }
      if (str.startsWith("X-TIMESTAMP-MAP"))
      {
        Matcher localMatcher1 = LOCAL_TIMESTAMP.matcher(str);
        if (!localMatcher1.find()) {
          throw new ParserException("X-TIMESTAMP-MAP doesn't contain local timestamp: " + str);
        }
        Matcher localMatcher2 = MEDIA_TIMESTAMP.matcher(str);
        if (!localMatcher2.find()) {
          throw new ParserException("X-TIMESTAMP-MAP doesn't contain media timestamp: " + str);
        }
        l2 = WebvttParserUtil.parseTimestampUs(localMatcher1.group(1));
        l1 = PtsTimestampAdjuster.ptsToUs(Long.parseLong(localMatcher2.group(1)));
      }
    }
    localObject = WebvttCueParser.findNextCueHeader((ParsableByteArray)localObject);
    if (localObject == null)
    {
      buildTrackOutput(0L);
      return;
    }
    long l3 = WebvttParserUtil.parseTimestampUs(((Matcher)localObject).group(1));
    l1 = this.ptsTimestampAdjuster.adjustTimestamp(PtsTimestampAdjuster.usToPts(l3 + l1 - l2));
    localObject = buildTrackOutput(l1 - l3);
    this.sampleDataWrapper.reset(this.sampleData, this.sampleSize);
    ((TrackOutput)localObject).sampleData(this.sampleDataWrapper, this.sampleSize);
    ((TrackOutput)localObject).sampleMetadata(l1, 1, this.sampleSize, 0, null);
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.output = paramExtractorOutput;
    paramExtractorOutput.seekMap(SeekMap.UNSEEKABLE);
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int j = (int)paramExtractorInput.getLength();
    if (this.sampleSize == this.sampleData.length)
    {
      paramPositionHolder = this.sampleData;
      if (j == -1) {
        break label105;
      }
    }
    label105:
    for (int i = j;; i = this.sampleData.length)
    {
      this.sampleData = Arrays.copyOf(paramPositionHolder, i * 3 / 2);
      i = paramExtractorInput.read(this.sampleData, this.sampleSize, this.sampleData.length - this.sampleSize);
      if (i == -1) {
        break;
      }
      this.sampleSize += i;
      if ((j != -1) && (this.sampleSize == j)) {
        break;
      }
      return 0;
    }
    processSample();
    return -1;
  }
  
  public void release() {}
  
  public void seek()
  {
    throw new IllegalStateException();
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    throw new IllegalStateException();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/hls/WebvttExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */