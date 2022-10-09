package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$EncryptedFile extends TLObject {
    public long access_hash;
    public int dc_id;
    public long id;
    public int key_fingerprint;
    public long size;

    public static TLRPC$EncryptedFile TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$EncryptedFile tLRPC$EncryptedFile;
        if (i == -NUM) {
            tLRPC$EncryptedFile = new TLRPC$EncryptedFile() { // from class: org.telegram.tgnet.TLRPC$TL_encryptedFile
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.id = abstractSerializedData2.readInt64(z2);
                    this.access_hash = abstractSerializedData2.readInt64(z2);
                    this.size = abstractSerializedData2.readInt64(z2);
                    this.dc_id = abstractSerializedData2.readInt32(z2);
                    this.key_fingerprint = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt64(this.id);
                    abstractSerializedData2.writeInt64(this.access_hash);
                    abstractSerializedData2.writeInt64(this.size);
                    abstractSerializedData2.writeInt32(this.dc_id);
                    abstractSerializedData2.writeInt32(this.key_fingerprint);
                }
            };
        } else if (i != -NUM) {
            tLRPC$EncryptedFile = i != NUM ? null : new TLRPC$EncryptedFile() { // from class: org.telegram.tgnet.TLRPC$TL_encryptedFile_layer142
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.id = abstractSerializedData2.readInt64(z2);
                    this.access_hash = abstractSerializedData2.readInt64(z2);
                    this.size = abstractSerializedData2.readInt32(z2);
                    this.dc_id = abstractSerializedData2.readInt32(z2);
                    this.key_fingerprint = abstractSerializedData2.readInt32(z2);
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    abstractSerializedData2.writeInt64(this.id);
                    abstractSerializedData2.writeInt64(this.access_hash);
                    abstractSerializedData2.writeInt32((int) this.size);
                    abstractSerializedData2.writeInt32(this.dc_id);
                    abstractSerializedData2.writeInt32(this.key_fingerprint);
                }
            };
        } else {
            tLRPC$EncryptedFile = new TLRPC$EncryptedFile() { // from class: org.telegram.tgnet.TLRPC$TL_encryptedFileEmpty
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        }
        if (tLRPC$EncryptedFile != null || !z) {
            if (tLRPC$EncryptedFile != null) {
                tLRPC$EncryptedFile.readParams(abstractSerializedData, z);
            }
            return tLRPC$EncryptedFile;
        }
        throw new RuntimeException(String.format("can't parse magic %x in EncryptedFile", Integer.valueOf(i)));
    }
}
