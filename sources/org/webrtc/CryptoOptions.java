package org.webrtc;

public final class CryptoOptions {
    private final SFrame sframe;
    private final Srtp srtp;

    public final class Srtp {
        private final boolean enableAes128Sha1_32CryptoCipher;
        private final boolean enableEncryptedRtpHeaderExtensions;
        private final boolean enableGcmCryptoSuites;

        private Srtp(boolean enableGcmCryptoSuites2, boolean enableAes128Sha1_32CryptoCipher2, boolean enableEncryptedRtpHeaderExtensions2) {
            this.enableGcmCryptoSuites = enableGcmCryptoSuites2;
            this.enableAes128Sha1_32CryptoCipher = enableAes128Sha1_32CryptoCipher2;
            this.enableEncryptedRtpHeaderExtensions = enableEncryptedRtpHeaderExtensions2;
        }

        public boolean getEnableGcmCryptoSuites() {
            return this.enableGcmCryptoSuites;
        }

        public boolean getEnableAes128Sha1_32CryptoCipher() {
            return this.enableAes128Sha1_32CryptoCipher;
        }

        public boolean getEnableEncryptedRtpHeaderExtensions() {
            return this.enableEncryptedRtpHeaderExtensions;
        }
    }

    public final class SFrame {
        private final boolean requireFrameEncryption;

        private SFrame(boolean requireFrameEncryption2) {
            this.requireFrameEncryption = requireFrameEncryption2;
        }

        public boolean getRequireFrameEncryption() {
            return this.requireFrameEncryption;
        }
    }

    private CryptoOptions(boolean enableGcmCryptoSuites, boolean enableAes128Sha1_32CryptoCipher, boolean enableEncryptedRtpHeaderExtensions, boolean requireFrameEncryption) {
        this.srtp = new Srtp(enableGcmCryptoSuites, enableAes128Sha1_32CryptoCipher, enableEncryptedRtpHeaderExtensions);
        this.sframe = new SFrame(requireFrameEncryption);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Srtp getSrtp() {
        return this.srtp;
    }

    public SFrame getSFrame() {
        return this.sframe;
    }

    public static class Builder {
        private boolean enableAes128Sha1_32CryptoCipher;
        private boolean enableEncryptedRtpHeaderExtensions;
        private boolean enableGcmCryptoSuites;
        private boolean requireFrameEncryption;

        private Builder() {
        }

        public Builder setEnableGcmCryptoSuites(boolean enableGcmCryptoSuites2) {
            this.enableGcmCryptoSuites = enableGcmCryptoSuites2;
            return this;
        }

        public Builder setEnableAes128Sha1_32CryptoCipher(boolean enableAes128Sha1_32CryptoCipher2) {
            this.enableAes128Sha1_32CryptoCipher = enableAes128Sha1_32CryptoCipher2;
            return this;
        }

        public Builder setEnableEncryptedRtpHeaderExtensions(boolean enableEncryptedRtpHeaderExtensions2) {
            this.enableEncryptedRtpHeaderExtensions = enableEncryptedRtpHeaderExtensions2;
            return this;
        }

        public Builder setRequireFrameEncryption(boolean requireFrameEncryption2) {
            this.requireFrameEncryption = requireFrameEncryption2;
            return this;
        }

        public CryptoOptions createCryptoOptions() {
            return new CryptoOptions(this.enableGcmCryptoSuites, this.enableAes128Sha1_32CryptoCipher, this.enableEncryptedRtpHeaderExtensions, this.requireFrameEncryption);
        }
    }
}
