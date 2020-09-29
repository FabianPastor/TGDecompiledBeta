package org.telegram.tgnet;

public abstract class TLRPC$PhotoSize extends TLObject {
    public byte[] bytes;
    public int h;
    public TLRPC$FileLocation location;
    public int size;
    public String type;
    public int w;

    public static TLRPC$PhotoSize TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PhotoSize tLRPC$PhotoSize;
        switch (i) {
            case -525288402:
                tLRPC$PhotoSize = new TLRPC$TL_photoStrippedSize();
                break;
            case -374917894:
                tLRPC$PhotoSize = new TLRPC$TL_photoCachedSize();
                break;
            case 236446268:
                tLRPC$PhotoSize = new TLRPC$TL_photoSizeEmpty();
                break;
            case 1520986705:
                tLRPC$PhotoSize = new TLRPC$TL_photoSizeProgressive();
                break;
            case 2009052699:
                tLRPC$PhotoSize = new TLRPC$TL_photoSize();
                break;
            default:
                tLRPC$PhotoSize = null;
                break;
        }
        if (tLRPC$PhotoSize != null || !z) {
            if (tLRPC$PhotoSize != null) {
                tLRPC$PhotoSize.readParams(abstractSerializedData, z);
            }
            return tLRPC$PhotoSize;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PhotoSize", new Object[]{Integer.valueOf(i)}));
    }
}
