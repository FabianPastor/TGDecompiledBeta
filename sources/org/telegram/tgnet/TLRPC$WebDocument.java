package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$WebDocument extends TLObject {
    public long access_hash;
    public ArrayList<TLRPC$DocumentAttribute> attributes = new ArrayList<>();
    public String mime_type;
    public int size;
    public String url;

    public static TLRPC$WebDocument TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$WebDocument tLRPC$WebDocument;
        if (i == -NUM) {
            tLRPC$WebDocument = new TLRPC$TL_webDocument() { // from class: org.telegram.tgnet.TLRPC$TL_webDocument_layer81
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLRPC$TL_webDocument, org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.url = abstractSerializedData2.readString(z2);
                    this.access_hash = abstractSerializedData2.readInt64(z2);
                    this.size = abstractSerializedData2.readInt32(z2);
                    this.mime_type = abstractSerializedData2.readString(z2);
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    if (readInt32 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                        }
                        return;
                    }
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt322; i2++) {
                        TLRPC$DocumentAttribute TLdeserialize = TLRPC$DocumentAttribute.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.attributes.add(TLdeserialize);
                    }
                    abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLRPC$TL_webDocument, org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeString(this.url);
                    abstractSerializedData2.writeInt64(this.access_hash);
                    abstractSerializedData2.writeInt32(this.size);
                    abstractSerializedData2.writeString(this.mime_type);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.attributes.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i2 = 0; i2 < size; i2++) {
                        this.attributes.get(i2).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(0);
                }
            };
        } else if (i == -NUM) {
            tLRPC$WebDocument = new TLRPC$WebDocument() { // from class: org.telegram.tgnet.TLRPC$TL_webDocumentNoProxy
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.url = abstractSerializedData2.readString(z2);
                    this.size = abstractSerializedData2.readInt32(z2);
                    this.mime_type = abstractSerializedData2.readString(z2);
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    if (readInt32 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                        }
                        return;
                    }
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt322; i2++) {
                        TLRPC$DocumentAttribute TLdeserialize = TLRPC$DocumentAttribute.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.attributes.add(TLdeserialize);
                    }
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeString(this.url);
                    abstractSerializedData2.writeInt32(this.size);
                    abstractSerializedData2.writeString(this.mime_type);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.attributes.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i2 = 0; i2 < size; i2++) {
                        this.attributes.get(i2).serializeToStream(abstractSerializedData2);
                    }
                }
            };
        } else {
            tLRPC$WebDocument = i != NUM ? null : new TLRPC$TL_webDocument();
        }
        if (tLRPC$WebDocument != null || !z) {
            if (tLRPC$WebDocument != null) {
                tLRPC$WebDocument.readParams(abstractSerializedData, z);
            }
            return tLRPC$WebDocument;
        }
        throw new RuntimeException(String.format("can't parse magic %x in WebDocument", Integer.valueOf(i)));
    }
}
