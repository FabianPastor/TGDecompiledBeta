package org.telegram.messenger.exoplayer2.source.chunk;

import android.util.Log;
import org.telegram.messenger.exoplayer2.extractor.DummyTrackOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.source.SampleQueue;

final class BaseMediaChunkOutput
  implements ChunkExtractorWrapper.TrackOutputProvider
{
  private static final String TAG = "BaseMediaChunkOutput";
  private final SampleQueue[] sampleQueues;
  private final int[] trackTypes;
  
  public BaseMediaChunkOutput(int[] paramArrayOfInt, SampleQueue[] paramArrayOfSampleQueue)
  {
    this.trackTypes = paramArrayOfInt;
    this.sampleQueues = paramArrayOfSampleQueue;
  }
  
  public int[] getWriteIndices()
  {
    int[] arrayOfInt = new int[this.sampleQueues.length];
    for (int i = 0; i < this.sampleQueues.length; i++) {
      if (this.sampleQueues[i] != null) {
        arrayOfInt[i] = this.sampleQueues[i].getWriteIndex();
      }
    }
    return arrayOfInt;
  }
  
  public void setSampleOffsetUs(long paramLong)
  {
    for (SampleQueue localSampleQueue : this.sampleQueues) {
      if (localSampleQueue != null) {
        localSampleQueue.setSampleOffsetUs(paramLong);
      }
    }
  }
  
  public TrackOutput track(int paramInt1, int paramInt2)
  {
    paramInt1 = 0;
    if (paramInt1 < this.trackTypes.length) {
      if (paramInt2 != this.trackTypes[paramInt1]) {}
    }
    for (Object localObject = this.sampleQueues[paramInt1];; localObject = new DummyTrackOutput())
    {
      return (TrackOutput)localObject;
      paramInt1++;
      break;
      Log.e("BaseMediaChunkOutput", "Unmatched track of type: " + paramInt2);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/chunk/BaseMediaChunkOutput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */