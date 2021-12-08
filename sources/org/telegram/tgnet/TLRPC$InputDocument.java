package org.telegram.tgnet;

public abstract class TLRPC$InputDocument extends TLObject {
    public long access_hash;
    public byte[] file_reference;
    public long id;

    public static TLRPC$InputDocument TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputDocument tLRPC$InputDocument;
        if (i != NUM) {
            tLRPC$InputDocument = i != NUM ? null : new TLRPC$TL_inputDocumentEmpty();
        } else {
            tLRPC$InputDocument = new TLRPC$TL_inputDocument();
        }
        if (tLRPC$InputDocument != null || !z) {
            if (tLRPC$InputDocument != null) {
                tLRPC$InputDocument.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputDocument;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputDocument", new Object[]{Integer.valueOf(i)}));
    }
}
