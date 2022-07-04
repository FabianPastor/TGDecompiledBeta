package org.webrtc;

import java.util.Arrays;
import org.webrtc.PeerConnection;

public class IceCandidate {
    public final PeerConnection.AdapterType adapterType;
    public final String sdp;
    public final int sdpMLineIndex;
    public final String sdpMid;
    public final String serverUrl;

    public IceCandidate(String sdpMid2, int sdpMLineIndex2, String sdp2) {
        this.sdpMid = sdpMid2;
        this.sdpMLineIndex = sdpMLineIndex2;
        this.sdp = sdp2;
        this.serverUrl = "";
        this.adapterType = PeerConnection.AdapterType.UNKNOWN;
    }

    IceCandidate(String sdpMid2, int sdpMLineIndex2, String sdp2, String serverUrl2, PeerConnection.AdapterType adapterType2) {
        this.sdpMid = sdpMid2;
        this.sdpMLineIndex = sdpMLineIndex2;
        this.sdp = sdp2;
        this.serverUrl = serverUrl2;
        this.adapterType = adapterType2;
    }

    public String toString() {
        return this.sdpMid + ":" + this.sdpMLineIndex + ":" + this.sdp + ":" + this.serverUrl + ":" + this.adapterType.toString();
    }

    /* access modifiers changed from: package-private */
    public String getSdpMid() {
        return this.sdpMid;
    }

    /* access modifiers changed from: package-private */
    public String getSdp() {
        return this.sdp;
    }

    public boolean equals(Object object) {
        if (!(object instanceof IceCandidate)) {
            return false;
        }
        IceCandidate that = (IceCandidate) object;
        if (!objectEquals(this.sdpMid, that.sdpMid) || this.sdpMLineIndex != that.sdpMLineIndex || !objectEquals(this.sdp, that.sdp)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.sdpMid, Integer.valueOf(this.sdpMLineIndex), this.sdp});
    }

    private static boolean objectEquals(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }
}
