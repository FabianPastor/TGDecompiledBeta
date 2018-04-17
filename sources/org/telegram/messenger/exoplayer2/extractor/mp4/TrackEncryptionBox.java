package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import org.telegram.messenger.exoplayer2.C0539C;
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
        int i = 0;
        int i2 = initializationVectorSize == 0 ? 1 : 0;
        if (defaultInitializationVector == null) {
            i = 1;
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
        int hashCode = schemeType.hashCode();
        if (hashCode != 3046605) {
            if (hashCode != 3046671) {
                if (hashCode != 3049879) {
                    if (hashCode == 3049895) {
                        if (schemeType.equals(C0539C.CENC_TYPE_cens)) {
                            i = 1;
                        }
                    }
                } else if (schemeType.equals(C0539C.CENC_TYPE_cenc)) {
                    i = 0;
                }
            } else if (schemeType.equals(C0539C.CENC_TYPE_cbcs)) {
                i = 3;
            }
        } else if (schemeType.equals(C0539C.CENC_TYPE_cbc1)) {
            i = 2;
        }
        switch (i) {
            case 0:
            case 1:
                return 1;
            case 2:
            case 3:
                return 2;
            default:
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unsupported protection scheme type '");
                stringBuilder.append(schemeType);
                stringBuilder.append("'. Assuming AES-CTR crypto mode.");
                Log.w(str, stringBuilder.toString());
                return 1;
        }
    }
}
