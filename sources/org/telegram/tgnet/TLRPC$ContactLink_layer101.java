package org.telegram.tgnet;

public abstract class TLRPC$ContactLink_layer101 extends TLObject {
    public static TLRPC$ContactLink_layer101 TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$ContactLink_layer101 tLRPC$ContactLink_layer101;
        if (i == -NUM) {
            tLRPC$ContactLink_layer101 = new TLRPC$TL_contactLinkContact();
        } else if (i != -17968211) {
            tLRPC$ContactLink_layer101 = i != NUM ? null : new TLRPC$TL_contactLinkUnknown();
        } else {
            tLRPC$ContactLink_layer101 = new TLRPC$TL_contactLinkNone();
        }
        if (tLRPC$ContactLink_layer101 != null || !z) {
            if (tLRPC$ContactLink_layer101 != null) {
                tLRPC$ContactLink_layer101.readParams(abstractSerializedData, z);
            }
            return tLRPC$ContactLink_layer101;
        }
        throw new RuntimeException(String.format("can't parse magic %x in ContactLink", new Object[]{Integer.valueOf(i)}));
    }
}
