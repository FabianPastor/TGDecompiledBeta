package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$WebDocument extends TLObject {
    public long access_hash;
    public ArrayList<TLRPC$DocumentAttribute> attributes = new ArrayList<>();
    public String mime_type;
    public int size;
    public String url;

    public static TLRPC$WebDocument TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$WebDocument tLRPC$WebDocument;
        if (i == -NUM) {
            tLRPC$WebDocument = new TLRPC$TL_webDocument_layer81();
        } else if (i != -NUM) {
            tLRPC$WebDocument = i != NUM ? null : new TLRPC$TL_webDocument();
        } else {
            tLRPC$WebDocument = new TLRPC$TL_webDocumentNoProxy();
        }
        if (tLRPC$WebDocument != null || !z) {
            if (tLRPC$WebDocument != null) {
                tLRPC$WebDocument.readParams(abstractSerializedData, z);
            }
            return tLRPC$WebDocument;
        }
        throw new RuntimeException(String.format("can't parse magic %x in WebDocument", new Object[]{Integer.valueOf(i)}));
    }
}
