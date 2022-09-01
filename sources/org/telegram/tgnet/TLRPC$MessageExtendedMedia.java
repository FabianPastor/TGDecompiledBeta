package org.telegram.tgnet;

public class TLRPC$MessageExtendedMedia extends TLObject {
    public static TLRPC$MessageExtendedMedia TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageExtendedMedia tLRPC$MessageExtendedMedia;
        if (i != -NUM) {
            tLRPC$MessageExtendedMedia = i != -NUM ? null : new TLRPC$TL_messageExtendedMedia();
        } else {
            tLRPC$MessageExtendedMedia = new TLRPC$TL_messageExtendedMediaPreview();
        }
        if (tLRPC$MessageExtendedMedia != null || !z) {
            if (tLRPC$MessageExtendedMedia != null) {
                tLRPC$MessageExtendedMedia.readParams(abstractSerializedData, z);
            }
            return tLRPC$MessageExtendedMedia;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageExtendedMedia", new Object[]{Integer.valueOf(i)}));
    }
}
