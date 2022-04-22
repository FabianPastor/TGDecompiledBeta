package org.webrtc;

import java.util.Arrays;
import org.webrtc.PeerConnection;

public class IceCandidate {
    public final PeerConnection.AdapterType adapterType;
    public final String sdp;
    public final int sdpMLineIndex;
    public final String sdpMid;
    public final String serverUrl;

    public IceCandidate(String str, int i, String str2) {
        this.sdpMid = str;
        this.sdpMLineIndex = i;
        this.sdp = str2;
        this.serverUrl = "";
        this.adapterType = PeerConnection.AdapterType.UNKNOWN;
    }

    @CalledByNative
    IceCandidate(String str, int i, String str2, String str3, PeerConnection.AdapterType adapterType2) {
        this.sdpMid = str;
        this.sdpMLineIndex = i;
        this.sdp = str2;
        this.serverUrl = str3;
        this.adapterType = adapterType2;
    }

    public String toString() {
        return this.sdpMid + ":" + this.sdpMLineIndex + ":" + this.sdp + ":" + this.serverUrl + ":" + this.adapterType.toString();
    }

    /* access modifiers changed from: package-private */
    @CalledByNative
    public String getSdpMid() {
        return this.sdpMid;
    }

    /* access modifiers changed from: package-private */
    @CalledByNative
    public String getSdp() {
        return this.sdp;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IceCandidate)) {
            return false;
        }
        IceCandidate iceCandidate = (IceCandidate) obj;
        if (!objectEquals(this.sdpMid, iceCandidate.sdpMid) || this.sdpMLineIndex != iceCandidate.sdpMLineIndex || !objectEquals(this.sdp, iceCandidate.sdp)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.sdpMid, Integer.valueOf(this.sdpMLineIndex), this.sdp});
    }

    private static boolean objectEquals(Object obj, Object obj2) {
        if (obj == null) {
            return obj2 == null;
        }
        return obj.equals(obj2);
    }
}
