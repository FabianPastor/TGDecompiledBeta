package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import org.telegram.messenger.exoplayer2.C0600C;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput.CryptoData;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class TrackEncryptionBox {
    private static final String TAG = "TrackEncryptionBox";
    public final CryptoData cryptoData;
    public final byte[] defaultInitializationVector;
    public final int initializationVectorSize;
    public final boolean isEncrypted;
    public final String schemeType;

    public TrackEncryptionBox(boolean isEncrypted, String schemeType, int initializationVectorSize, byte[] keyId, int defaultEncryptedBlocks, int defaultClearBlocks, byte[] defaultInitializationVector) {
        int i = 1;
        int i2 = initializationVectorSize == 0 ? 1 : 0;
        if (defaultInitializationVector != null) {
            i = 0;
        }
        Assertions.checkArgument(i ^ i2);
        this.isEncrypted = isEncrypted;
        this.schemeType = schemeType;
        this.initializationVectorSize = initializationVectorSize;
        this.defaultInitializationVector = defaultInitializationVector;
        this.cryptoData = new CryptoData(schemeToCryptoMode(schemeType), keyId, defaultEncryptedBlocks, defaultClearBlocks);
    }

    private static int schemeToCryptoMode(String schemeType) {
        if (schemeType == null) {
            return 1;
        }
        int i = -1;
        switch (schemeType.hashCode()) {
            case 3046605:
                if (schemeType.equals(C0600C.CENC_TYPE_cbc1)) {
                    i = 2;
                    break;
                }
                break;
            case 3046671:
                if (schemeType.equals(C0600C.CENC_TYPE_cbcs)) {
                    i = 3;
                    break;
                }
                break;
            case 3049879:
                if (schemeType.equals(C0600C.CENC_TYPE_cenc)) {
                    i = 0;
                    break;
                }
                break;
            case 3049895:
                if (schemeType.equals(C0600C.CENC_TYPE_cens)) {
                    i = 1;
                    break;
                }
                break;
        }
        switch (i) {
            case 0:
            case 1:
                return 1;
            case 2:
            case 3:
                return 2;
            default:
                Log.w(TAG, "Unsupported protection scheme type '" + schemeType + "'. Assuming AES-CTR crypto mode.");
                return 1;
        }
    }
}
