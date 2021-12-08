package org.webrtc;

import org.webrtc.PeerConnection;

public final class PeerConnectionDependencies {
    private final PeerConnection.Observer observer;
    private final SSLCertificateVerifier sslCertificateVerifier;

    public static class Builder {
        private PeerConnection.Observer observer;
        private SSLCertificateVerifier sslCertificateVerifier;

        private Builder(PeerConnection.Observer observer2) {
            this.observer = observer2;
        }

        public Builder setSSLCertificateVerifier(SSLCertificateVerifier sSLCertificateVerifier) {
            this.sslCertificateVerifier = sSLCertificateVerifier;
            return this;
        }

        public PeerConnectionDependencies createPeerConnectionDependencies() {
            return new PeerConnectionDependencies(this.observer, this.sslCertificateVerifier);
        }
    }

    public static Builder builder(PeerConnection.Observer observer2) {
        return new Builder(observer2);
    }

    /* access modifiers changed from: package-private */
    public PeerConnection.Observer getObserver() {
        return this.observer;
    }

    /* access modifiers changed from: package-private */
    public SSLCertificateVerifier getSSLCertificateVerifier() {
        return this.sslCertificateVerifier;
    }

    private PeerConnectionDependencies(PeerConnection.Observer observer2, SSLCertificateVerifier sSLCertificateVerifier) {
        this.observer = observer2;
        this.sslCertificateVerifier = sSLCertificateVerifier;
    }
}
