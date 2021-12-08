package org.webrtc;

enum VideoCodecMimeType {
    VP8("video/x-vnd.on2.vp8"),
    VP9("video/x-vnd.on2.vp9"),
    H264("video/avc"),
    H265("video/hevc"),
    AV1("video/av01");
    
    private final String mimeType;

    private VideoCodecMimeType(String mimeType2) {
        this.mimeType = mimeType2;
    }

    /* access modifiers changed from: package-private */
    public String mimeType() {
        return this.mimeType;
    }
}
