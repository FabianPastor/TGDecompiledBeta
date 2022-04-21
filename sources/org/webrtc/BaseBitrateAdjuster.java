package org.webrtc;

class BaseBitrateAdjuster implements BitrateAdjuster {
    protected int targetBitrateBps;
    protected int targetFps;

    BaseBitrateAdjuster() {
    }

    public void setTargets(int targetBitrateBps2, int targetFps2) {
        this.targetBitrateBps = targetBitrateBps2;
        this.targetFps = targetFps2;
    }

    public void reportEncodedFrame(int size) {
    }

    public int getAdjustedBitrateBps() {
        return this.targetBitrateBps;
    }

    public int getCodecConfigFramerate() {
        return this.targetFps;
    }
}
