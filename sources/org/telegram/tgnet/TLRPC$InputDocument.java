package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$InputDocument extends TLObject {
    public long access_hash;
    public byte[] file_reference;
    public long id;

    public static TLRPC$InputDocument TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputDocument tLRPC$TL_inputDocument;
        if (i != NUM) {
            tLRPC$TL_inputDocument = i != NUM ? null : new TLRPC$TL_inputDocumentEmpty();
        } else {
            tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
        }
        if (tLRPC$TL_inputDocument != null || !z) {
            if (tLRPC$TL_inputDocument != null) {
                tLRPC$TL_inputDocument.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_inputDocument;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputDocument", Integer.valueOf(i)));
    }
}
