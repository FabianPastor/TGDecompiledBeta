package org.telegram.messenger.exoplayer.chunk;

import java.util.List;
import java.util.Random;
import org.telegram.messenger.exoplayer.upstream.BandwidthMeter;

public abstract interface FormatEvaluator
{
  public abstract void disable();
  
  public abstract void enable();
  
  public abstract void evaluate(List<? extends MediaChunk> paramList, long paramLong, Format[] paramArrayOfFormat, Evaluation paramEvaluation);
  
  public static final class AdaptiveEvaluator
    implements FormatEvaluator
  {
    public static final float DEFAULT_BANDWIDTH_FRACTION = 0.75F;
    public static final int DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS = 25000;
    public static final int DEFAULT_MAX_INITIAL_BITRATE = 800000;
    public static final int DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS = 10000;
    public static final int DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = 25000;
    private final float bandwidthFraction;
    private final BandwidthMeter bandwidthMeter;
    private final long maxDurationForQualityDecreaseUs;
    private final int maxInitialBitrate;
    private final long minDurationForQualityIncreaseUs;
    private final long minDurationToRetainAfterDiscardUs;
    
    public AdaptiveEvaluator(BandwidthMeter paramBandwidthMeter)
    {
      this(paramBandwidthMeter, 800000, 10000, 25000, 25000, 0.75F);
    }
    
    public AdaptiveEvaluator(BandwidthMeter paramBandwidthMeter, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat)
    {
      this.bandwidthMeter = paramBandwidthMeter;
      this.maxInitialBitrate = paramInt1;
      this.minDurationForQualityIncreaseUs = (paramInt2 * 1000L);
      this.maxDurationForQualityDecreaseUs = (paramInt3 * 1000L);
      this.minDurationToRetainAfterDiscardUs = (paramInt4 * 1000L);
      this.bandwidthFraction = paramFloat;
    }
    
    private Format determineIdealFormat(Format[] paramArrayOfFormat, long paramLong)
    {
      int i;
      if (paramLong == -1L)
      {
        paramLong = this.maxInitialBitrate;
        i = 0;
      }
      for (;;)
      {
        if (i >= paramArrayOfFormat.length) {
          break label65;
        }
        Format localFormat = paramArrayOfFormat[i];
        if (localFormat.bitrate <= paramLong)
        {
          return localFormat;
          paramLong = ((float)paramLong * this.bandwidthFraction);
          break;
        }
        i += 1;
      }
      label65:
      return paramArrayOfFormat[(paramArrayOfFormat.length - 1)];
    }
    
    public void disable() {}
    
    public void enable() {}
    
    public void evaluate(List<? extends MediaChunk> paramList, long paramLong, Format[] paramArrayOfFormat, FormatEvaluator.Evaluation paramEvaluation)
    {
      long l;
      Format localFormat1;
      Format localFormat2;
      int i;
      label62:
      int j;
      if (paramList.isEmpty())
      {
        l = 0L;
        localFormat1 = paramEvaluation.format;
        localFormat2 = determineIdealFormat(paramArrayOfFormat, this.bandwidthMeter.getBitrateEstimate());
        if ((localFormat2 == null) || (localFormat1 == null) || (localFormat2.bitrate <= localFormat1.bitrate)) {
          break label160;
        }
        i = 1;
        if ((localFormat2 == null) || (localFormat1 == null) || (localFormat2.bitrate >= localFormat1.bitrate)) {
          break label166;
        }
        j = 1;
        label88:
        if (i == 0) {
          break label315;
        }
        if (l >= this.minDurationForQualityIncreaseUs) {
          break label172;
        }
        paramArrayOfFormat = localFormat1;
      }
      for (;;)
      {
        if ((localFormat1 != null) && (paramArrayOfFormat != localFormat1)) {
          paramEvaluation.trigger = 3;
        }
        paramEvaluation.format = paramArrayOfFormat;
        return;
        l = ((MediaChunk)paramList.get(paramList.size() - 1)).endTimeUs - paramLong;
        break;
        label160:
        i = 0;
        break label62;
        label166:
        j = 0;
        break label88;
        label172:
        paramArrayOfFormat = localFormat2;
        if (l >= this.minDurationToRetainAfterDiscardUs)
        {
          i = 1;
          for (;;)
          {
            paramArrayOfFormat = localFormat2;
            if (i >= paramList.size()) {
              break;
            }
            paramArrayOfFormat = (MediaChunk)paramList.get(i);
            if ((paramArrayOfFormat.startTimeUs - paramLong >= this.minDurationToRetainAfterDiscardUs) && (paramArrayOfFormat.format.bitrate < localFormat2.bitrate) && (paramArrayOfFormat.format.height < localFormat2.height) && (paramArrayOfFormat.format.height < 720) && (paramArrayOfFormat.format.width < 1280))
            {
              paramEvaluation.queueSize = i;
              paramArrayOfFormat = localFormat2;
              break;
            }
            i += 1;
          }
          label315:
          paramArrayOfFormat = localFormat2;
          if (j != 0)
          {
            paramArrayOfFormat = localFormat2;
            if (localFormat1 != null)
            {
              paramArrayOfFormat = localFormat2;
              if (l >= this.maxDurationForQualityDecreaseUs) {
                paramArrayOfFormat = localFormat1;
              }
            }
          }
        }
      }
    }
  }
  
  public static final class Evaluation
  {
    public Format format;
    public int queueSize;
    public int trigger = 1;
  }
  
  public static final class FixedEvaluator
    implements FormatEvaluator
  {
    public void disable() {}
    
    public void enable() {}
    
    public void evaluate(List<? extends MediaChunk> paramList, long paramLong, Format[] paramArrayOfFormat, FormatEvaluator.Evaluation paramEvaluation)
    {
      paramEvaluation.format = paramArrayOfFormat[0];
    }
  }
  
  public static final class RandomEvaluator
    implements FormatEvaluator
  {
    private final Random random;
    
    public RandomEvaluator()
    {
      this.random = new Random();
    }
    
    public RandomEvaluator(int paramInt)
    {
      this.random = new Random(paramInt);
    }
    
    public void disable() {}
    
    public void enable() {}
    
    public void evaluate(List<? extends MediaChunk> paramList, long paramLong, Format[] paramArrayOfFormat, FormatEvaluator.Evaluation paramEvaluation)
    {
      paramList = paramArrayOfFormat[this.random.nextInt(paramArrayOfFormat.length)];
      if ((paramEvaluation.format != null) && (!paramEvaluation.format.equals(paramList))) {
        paramEvaluation.trigger = 3;
      }
      paramEvaluation.format = paramList;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/chunk/FormatEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */