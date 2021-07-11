package org.telegram.tgnet;

import android.text.TextUtils;

public abstract class TLRPC$PhotoSize extends TLObject {
    public byte[] bytes;
    public int h;
    public TLRPC$FileLocation location;
    public int size;
    public String type;
    public int w;

    public static TLRPC$PhotoSize TLdeserialize(long j, long j2, long j3, AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PhotoSize tLRPC$PhotoSize;
        switch (i) {
            case -668906175:
                tLRPC$PhotoSize = new TLRPC$TL_photoPathSize();
                break;
            case -525288402:
                tLRPC$PhotoSize = new TLRPC$TL_photoStrippedSize();
                break;
            case -374917894:
                tLRPC$PhotoSize = new TLRPC$TL_photoCachedSize_layer127();
                break;
            case -96535659:
                tLRPC$PhotoSize = new TLRPC$TL_photoSizeProgressive();
                break;
            case 35527382:
                tLRPC$PhotoSize = new TLRPC$TL_photoCachedSize();
                break;
            case 236446268:
                tLRPC$PhotoSize = new TLRPC$TL_photoSizeEmpty();
                break;
            case 1520986705:
                tLRPC$PhotoSize = new TLRPC$TL_photoSizeProgressive_layer127();
                break;
            case 1976012384:
                tLRPC$PhotoSize = new TLRPC$TL_photoSize();
                break;
            case 2009052699:
                tLRPC$PhotoSize = new TLRPC$TL_photoSize_layer127();
                break;
            default:
                tLRPC$PhotoSize = null;
                break;
        }
        if (tLRPC$PhotoSize != null || !z) {
            if (tLRPC$PhotoSize != null) {
                tLRPC$PhotoSize.readParams(abstractSerializedData, z);
                if (tLRPC$PhotoSize.location == null) {
                    if (TextUtils.isEmpty(tLRPC$PhotoSize.type) || (j == 0 && j2 == 0 && j3 == 0)) {
                        tLRPC$PhotoSize.location = new TLRPC$TL_fileLocationUnavailable();
                    } else {
                        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = new TLRPC$TL_fileLocationToBeDeprecated();
                        tLRPC$PhotoSize.location = tLRPC$TL_fileLocationToBeDeprecated;
                        if (j != 0) {
                            tLRPC$TL_fileLocationToBeDeprecated.volume_id = -j;
                            tLRPC$TL_fileLocationToBeDeprecated.local_id = tLRPC$PhotoSize.type.charAt(0);
                        } else if (j2 != 0) {
                            tLRPC$TL_fileLocationToBeDeprecated.volume_id = -j2;
                            tLRPC$TL_fileLocationToBeDeprecated.local_id = tLRPC$PhotoSize.type.charAt(0) + 1000;
                        } else if (j3 != 0) {
                            tLRPC$TL_fileLocationToBeDeprecated.volume_id = -j3;
                            tLRPC$TL_fileLocationToBeDeprecated.local_id = tLRPC$PhotoSize.type.charAt(0) + 2000;
                        }
                    }
                }
            }
            return tLRPC$PhotoSize;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PhotoSize", new Object[]{Integer.valueOf(i)}));
    }
}
