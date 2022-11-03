package org.webrtc;
/* loaded from: classes.dex */
interface BitrateAdjuster {
    int getAdjustedBitrateBps();

    int getCodecConfigFramerate();

    void reportEncodedFrame(int i);

    void setTargets(int i, int i2);
}
