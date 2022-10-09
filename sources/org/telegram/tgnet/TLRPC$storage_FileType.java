package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$storage_FileType extends TLObject {
    public static TLRPC$storage_FileType TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$storage_FileType tLRPC$storage_FileType;
        switch (i) {
            case -1432995067:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_fileUnknown
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1373745011:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_filePdf
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1278304028:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_fileMp4
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -891180321:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_fileGif
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 8322574:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_fileJpeg
                    public static int constructor = 8322574;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 172975040:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_filePng
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 276907596:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_fileWebp
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1086091090:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_filePartial
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1258941372:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_fileMov
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 1384777335:
                tLRPC$storage_FileType = new TLRPC$storage_FileType() { // from class: org.telegram.tgnet.TLRPC$TL_storage_fileMp3
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            default:
                tLRPC$storage_FileType = null;
                break;
        }
        if (tLRPC$storage_FileType != null || !z) {
            if (tLRPC$storage_FileType != null) {
                tLRPC$storage_FileType.readParams(abstractSerializedData, z);
            }
            return tLRPC$storage_FileType;
        }
        throw new RuntimeException(String.format("can't parse magic %x in storage_FileType", Integer.valueOf(i)));
    }
}
