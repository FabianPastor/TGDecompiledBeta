package org.telegram.messenger.exoplayer.chunk;

import java.util.List;
import java.util.Random;
import org.telegram.messenger.exoplayer.upstream.BandwidthMeter;

public interface FormatEvaluator {

    public static final class Evaluation {
        public Format format;
        public int queueSize;
        public int trigger = 1;
    }

    public static final class AdaptiveEvaluator implements FormatEvaluator {
        public static final float DEFAULT_BANDWIDTH_FRACTION = 0.75f;
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

        public AdaptiveEvaluator(BandwidthMeter bandwidthMeter) {
            this(bandwidthMeter, DEFAULT_MAX_INITIAL_BITRATE, 10000, 25000, 25000, DEFAULT_BANDWIDTH_FRACTION);
        }

        public AdaptiveEvaluator(BandwidthMeter bandwidthMeter, int maxInitialBitrate, int minDurationForQualityIncreaseMs, int maxDurationForQualityDecreaseMs, int minDurationToRetainAfterDiscardMs, float bandwidthFraction) {
            this.bandwidthMeter = bandwidthMeter;
            this.maxInitialBitrate = maxInitialBitrate;
            this.minDurationForQualityIncreaseUs = ((long) minDurationForQualityIncreaseMs) * 1000;
            this.maxDurationForQualityDecreaseUs = ((long) maxDurationForQualityDecreaseMs) * 1000;
            this.minDurationToRetainAfterDiscardUs = ((long) minDurationToRetainAfterDiscardMs) * 1000;
            this.bandwidthFraction = bandwidthFraction;
        }

        public void enable() {
        }

        public void disable() {
        }

        public void evaluate(List<? extends MediaChunk> queue, long playbackPositionUs, Format[] formats, Evaluation evaluation) {
            long bufferedDurationUs;
            if (queue.isEmpty()) {
                bufferedDurationUs = 0;
            } else {
                bufferedDurationUs = ((MediaChunk) queue.get(queue.size() - 1)).endTimeUs - playbackPositionUs;
            }
            Format current = evaluation.format;
            Format ideal = determineIdealFormat(formats, this.bandwidthMeter.getBitrateEstimate());
            boolean isHigher = (ideal == null || current == null || ideal.bitrate <= current.bitrate) ? false : true;
            boolean isLower = (ideal == null || current == null || ideal.bitrate >= current.bitrate) ? false : true;
            if (isHigher) {
                if (bufferedDurationUs < this.minDurationForQualityIncreaseUs) {
                    ideal = current;
                } else if (bufferedDurationUs >= this.minDurationToRetainAfterDiscardUs) {
                    for (int i = 1; i < queue.size(); i++) {
                        MediaChunk thisChunk = (MediaChunk) queue.get(i);
                        if (thisChunk.startTimeUs - playbackPositionUs >= this.minDurationToRetainAfterDiscardUs && thisChunk.format.bitrate < ideal.bitrate && thisChunk.format.height < ideal.height && thisChunk.format.height < 720 && thisChunk.format.width < 1280) {
                            evaluation.queueSize = i;
                            break;
                        }
                    }
                }
            } else if (isLower && current != null && bufferedDurationUs >= this.maxDurationForQualityDecreaseUs) {
                ideal = current;
            }
            if (!(current == null || ideal == current)) {
                evaluation.trigger = 3;
            }
            evaluation.format = ideal;
        }

        private Format determineIdealFormat(Format[] formats, long bitrateEstimate) {
            long effectiveBitrate = bitrateEstimate == -1 ? (long) this.maxInitialBitrate : (long) (((float) bitrateEstimate) * this.bandwidthFraction);
            for (Format format : formats) {
                if (((long) format.bitrate) <= effectiveBitrate) {
                    return format;
                }
            }
            return formats[formats.length - 1];
        }
    }

    public static final class FixedEvaluator implements FormatEvaluator {
        public void enable() {
        }

        public void disable() {
        }

        public void evaluate(List<? extends MediaChunk> list, long playbackPositionUs, Format[] formats, Evaluation evaluation) {
            evaluation.format = formats[0];
        }
    }

    public static final class RandomEvaluator implements FormatEvaluator {
        private final Random random;

        public RandomEvaluator() {
            this.random = new Random();
        }

        public RandomEvaluator(int seed) {
            this.random = new Random((long) seed);
        }

        public void enable() {
        }

        public void disable() {
        }

        public void evaluate(List<? extends MediaChunk> list, long playbackPositionUs, Format[] formats, Evaluation evaluation) {
            Format newFormat = formats[this.random.nextInt(formats.length)];
            if (!(evaluation.format == null || evaluation.format.equals(newFormat))) {
                evaluation.trigger = 3;
            }
            evaluation.format = newFormat;
        }
    }

    void disable();

    void enable();

    void evaluate(List<? extends MediaChunk> list, long j, Format[] formatArr, Evaluation evaluation);
}
