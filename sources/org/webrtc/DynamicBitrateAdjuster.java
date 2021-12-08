package org.webrtc;

class DynamicBitrateAdjuster extends BaseBitrateAdjuster {
    private static final double BITRATE_ADJUSTMENT_MAX_SCALE = 4.0d;
    private static final double BITRATE_ADJUSTMENT_SEC = 3.0d;
    private static final int BITRATE_ADJUSTMENT_STEPS = 20;
    private static final double BITS_PER_BYTE = 8.0d;
    private int bitrateAdjustmentScaleExp;
    private double deviationBytes;
    private double timeSinceLastAdjustmentMs;

    DynamicBitrateAdjuster() {
    }

    public void setTargets(int targetBitrateBps, int targetFps) {
        if (this.targetBitrateBps > 0 && targetBitrateBps < this.targetBitrateBps) {
            double d = this.deviationBytes;
            double d2 = (double) targetBitrateBps;
            Double.isNaN(d2);
            double d3 = d * d2;
            double d4 = (double) this.targetBitrateBps;
            Double.isNaN(d4);
            this.deviationBytes = d3 / d4;
        }
        super.setTargets(targetBitrateBps, targetFps);
    }

    public void reportEncodedFrame(int size) {
        if (this.targetFps != 0) {
            double d = (double) this.targetBitrateBps;
            Double.isNaN(d);
            double d2 = (double) this.targetFps;
            Double.isNaN(d2);
            double expectedBytesPerFrame = (d / 8.0d) / d2;
            double d3 = this.deviationBytes;
            double d4 = (double) size;
            Double.isNaN(d4);
            this.deviationBytes = d3 + (d4 - expectedBytesPerFrame);
            double d5 = this.timeSinceLastAdjustmentMs;
            double d6 = (double) this.targetFps;
            Double.isNaN(d6);
            this.timeSinceLastAdjustmentMs = d5 + (1000.0d / d6);
            double d7 = (double) this.targetBitrateBps;
            Double.isNaN(d7);
            double deviationThresholdBytes = d7 / 8.0d;
            double deviationCap = 3.0d * deviationThresholdBytes;
            double min = Math.min(this.deviationBytes, deviationCap);
            this.deviationBytes = min;
            double max = Math.max(min, -deviationCap);
            this.deviationBytes = max;
            if (this.timeSinceLastAdjustmentMs > 3000.0d) {
                if (max > deviationThresholdBytes) {
                    int i = this.bitrateAdjustmentScaleExp - ((int) ((max / deviationThresholdBytes) + 0.5d));
                    this.bitrateAdjustmentScaleExp = i;
                    this.bitrateAdjustmentScaleExp = Math.max(i, -20);
                    this.deviationBytes = deviationThresholdBytes;
                } else if (max < (-deviationThresholdBytes)) {
                    int i2 = this.bitrateAdjustmentScaleExp + ((int) (((-max) / deviationThresholdBytes) + 0.5d));
                    this.bitrateAdjustmentScaleExp = i2;
                    this.bitrateAdjustmentScaleExp = Math.min(i2, 20);
                    this.deviationBytes = -deviationThresholdBytes;
                }
                this.timeSinceLastAdjustmentMs = 0.0d;
            }
        }
    }

    private double getBitrateAdjustmentScale() {
        double d = (double) this.bitrateAdjustmentScaleExp;
        Double.isNaN(d);
        return Math.pow(4.0d, d / 20.0d);
    }

    public int getAdjustedBitrateBps() {
        double d = (double) this.targetBitrateBps;
        double bitrateAdjustmentScale = getBitrateAdjustmentScale();
        Double.isNaN(d);
        return (int) (d * bitrateAdjustmentScale);
    }
}
