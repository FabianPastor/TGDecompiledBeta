package org.telegram.tgnet;

import android.text.TextUtils;
import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$PhotoSize extends TLObject {
    public byte[] bytes;
    public int h;
    public TLRPC$FileLocation location;
    public int size;
    public String type;
    public int w;

    public static TLRPC$PhotoSize TLdeserialize(long j, long j2, long j3, AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$PhotoSize tLRPC$PhotoSize;
        switch (i) {
            case -668906175:
                tLRPC$PhotoSize = new TLRPC$PhotoSize() { // from class: org.telegram.tgnet.TLRPC$TL_photoPathSize
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.type = abstractSerializedData2.readString(z2);
                        this.bytes = abstractSerializedData2.readByteArray(z2);
                        this.h = 50;
                        this.w = 50;
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.type);
                        abstractSerializedData2.writeByteArray(this.bytes);
                    }
                };
                break;
            case -525288402:
                tLRPC$PhotoSize = new TLRPC$TL_photoStrippedSize();
                break;
            case -374917894:
                tLRPC$PhotoSize = new TLRPC$TL_photoCachedSize() { // from class: org.telegram.tgnet.TLRPC$TL_photoCachedSize_layer127
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_photoCachedSize, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.type = abstractSerializedData2.readString(z2);
                        this.location = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.w = abstractSerializedData2.readInt32(z2);
                        this.h = abstractSerializedData2.readInt32(z2);
                        this.bytes = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_photoCachedSize, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.type);
                        this.location.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.w);
                        abstractSerializedData2.writeInt32(this.h);
                        abstractSerializedData2.writeByteArray(this.bytes);
                    }
                };
                break;
            case -96535659:
                tLRPC$PhotoSize = new TLRPC$TL_photoSizeProgressive();
                break;
            case 35527382:
                tLRPC$PhotoSize = new TLRPC$TL_photoCachedSize();
                break;
            case 236446268:
                tLRPC$PhotoSize = new TLRPC$TL_photoSizeEmpty();
                break;
            case 1520986705:
                tLRPC$PhotoSize = new TLRPC$TL_photoSizeProgressive() { // from class: org.telegram.tgnet.TLRPC$TL_photoSizeProgressive_layer127
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_photoSizeProgressive, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.type = abstractSerializedData2.readString(z2);
                        this.location = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.w = abstractSerializedData2.readInt32(z2);
                        this.h = abstractSerializedData2.readInt32(z2);
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        if (readInt32 != NUM) {
                            if (z2) {
                                throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                            }
                            return;
                        }
                        int readInt322 = abstractSerializedData2.readInt32(z2);
                        for (int i2 = 0; i2 < readInt322; i2++) {
                            this.sizes.add(Integer.valueOf(abstractSerializedData2.readInt32(z2)));
                        }
                        if (this.sizes.isEmpty()) {
                            return;
                        }
                        ArrayList<Integer> arrayList = this.sizes;
                        this.size = arrayList.get(arrayList.size() - 1).intValue();
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_photoSizeProgressive, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeString(this.type);
                        this.location.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.w);
                        abstractSerializedData2.writeInt32(this.h);
                        abstractSerializedData2.writeInt32(NUM);
                        int size = this.sizes.size();
                        abstractSerializedData2.writeInt32(size);
                        for (int i2 = 0; i2 < size; i2++) {
                            abstractSerializedData2.writeInt32(this.sizes.get(i2).intValue());
                        }
                    }
                };
                break;
            case 1976012384:
                tLRPC$PhotoSize = new TLRPC$TL_photoSize();
                break;
            case 2009052699:
                tLRPC$PhotoSize = new TLRPC$TL_photoSize_layer127();
                break;
            default:
                tLRPC$PhotoSize = null;
                break;
        }
        if (tLRPC$PhotoSize != null || !z) {
            if (tLRPC$PhotoSize != null) {
                tLRPC$PhotoSize.readParams(abstractSerializedData, z);
                if (tLRPC$PhotoSize.location == null) {
                    if (!TextUtils.isEmpty(tLRPC$PhotoSize.type) && (j != 0 || j2 != 0 || j3 != 0)) {
                        TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = new TLRPC$TL_fileLocationToBeDeprecated();
                        tLRPC$PhotoSize.location = tLRPC$TL_fileLocationToBeDeprecated;
                        if (j != 0) {
                            tLRPC$TL_fileLocationToBeDeprecated.volume_id = -j;
                            tLRPC$TL_fileLocationToBeDeprecated.local_id = tLRPC$PhotoSize.type.charAt(0);
                        } else if (j2 != 0) {
                            tLRPC$TL_fileLocationToBeDeprecated.volume_id = -j2;
                            tLRPC$TL_fileLocationToBeDeprecated.local_id = tLRPC$PhotoSize.type.charAt(0) + 1000;
                        } else if (j3 != 0) {
                            tLRPC$TL_fileLocationToBeDeprecated.volume_id = -j3;
                            tLRPC$TL_fileLocationToBeDeprecated.local_id = tLRPC$PhotoSize.type.charAt(0) + 2000;
                        }
                    } else {
                        tLRPC$PhotoSize.location = new TLRPC$TL_fileLocationUnavailable();
                    }
                }
            }
            return tLRPC$PhotoSize;
        }
        throw new RuntimeException(String.format("can't parse magic %x in PhotoSize", Integer.valueOf(i)));
    }
}
