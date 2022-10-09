package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$MessageExtendedMedia extends TLObject {
    public static TLRPC$MessageExtendedMedia TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageExtendedMedia tLRPC$TL_messageExtendedMediaPreview;
        if (i == -NUM) {
            tLRPC$TL_messageExtendedMediaPreview = new TLRPC$TL_messageExtendedMediaPreview();
        } else {
            tLRPC$TL_messageExtendedMediaPreview = i != -NUM ? null : new TLRPC$TL_messageExtendedMedia();
        }
        if (tLRPC$TL_messageExtendedMediaPreview != null || !z) {
            if (tLRPC$TL_messageExtendedMediaPreview != null) {
                tLRPC$TL_messageExtendedMediaPreview.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messageExtendedMediaPreview;
        }
        throw new RuntimeException(String.format("can't parse magic %x in MessageExtendedMedia", Integer.valueOf(i)));
    }
}
