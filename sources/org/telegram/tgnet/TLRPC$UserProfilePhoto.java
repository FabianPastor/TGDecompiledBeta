package org.telegram.tgnet;

import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
/* loaded from: classes.dex */
public abstract class TLRPC$UserProfilePhoto extends TLObject {
    public int dc_id;
    public int flags;
    public boolean has_video;
    public TLRPC$FileLocation photo_big;
    public long photo_id;
    public TLRPC$FileLocation photo_small;
    public BitmapDrawable strippedBitmap;
    public byte[] stripped_thumb;

    public static TLRPC$UserProfilePhoto TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$UserProfilePhoto tLRPC$TL_userProfilePhoto;
        switch (i) {
            case -2100168954:
                tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto();
                break;
            case -1727196013:
                tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto() { // from class: org.telegram.tgnet.TLRPC$TL_userProfilePhoto_old
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.photo_small = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.photo_big = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        this.photo_small.serializeToStream(abstractSerializedData2);
                        this.photo_big.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case -865771401:
                tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto() { // from class: org.telegram.tgnet.TLRPC$TL_userProfilePhoto_layer127
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        if ((readInt32 & 1) == 0) {
                            z3 = false;
                        }
                        this.has_video = z3;
                        this.photo_id = abstractSerializedData2.readInt64(z2);
                        this.photo_small = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.photo_big = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if ((this.flags & 2) != 0) {
                            this.stripped_thumb = abstractSerializedData2.readByteArray(z2);
                            if (Build.VERSION.SDK_INT >= 21) {
                                try {
                                    this.strippedBitmap = new BitmapDrawable(ImageLoader.getStrippedPhotoBitmap(this.stripped_thumb, "b"));
                                } catch (Throwable th) {
                                    FileLog.e(th);
                                }
                            }
                        }
                        this.dc_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.has_video ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeInt64(this.photo_id);
                        this.photo_small.serializeToStream(abstractSerializedData2);
                        this.photo_big.serializeToStream(abstractSerializedData2);
                        if ((this.flags & 2) != 0) {
                            abstractSerializedData2.writeByteArray(this.stripped_thumb);
                        }
                        abstractSerializedData2.writeInt32(this.dc_id);
                    }
                };
                break;
            case -715532088:
                tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto() { // from class: org.telegram.tgnet.TLRPC$TL_userProfilePhoto_layer97
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.photo_id = abstractSerializedData2.readInt64(z2);
                        this.photo_small = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.photo_big = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt64(this.photo_id);
                        this.photo_small.serializeToStream(abstractSerializedData2);
                        this.photo_big.serializeToStream(abstractSerializedData2);
                    }
                };
                break;
            case -321430132:
                tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto() { // from class: org.telegram.tgnet.TLRPC$TL_userProfilePhoto_layer115
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.photo_id = abstractSerializedData2.readInt64(z2);
                        this.photo_small = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.photo_big = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.dc_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt64(this.photo_id);
                        this.photo_small.serializeToStream(abstractSerializedData2);
                        this.photo_big.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.dc_id);
                    }
                };
                break;
            case 1326562017:
                tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhotoEmpty();
                break;
            case 1775479590:
                tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto() { // from class: org.telegram.tgnet.TLRPC$TL_userProfilePhoto_layer126
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        boolean z3 = true;
                        if ((readInt32 & 1) == 0) {
                            z3 = false;
                        }
                        this.has_video = z3;
                        this.photo_id = abstractSerializedData2.readInt64(z2);
                        this.photo_small = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.photo_big = TLRPC$FileLocation.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        this.dc_id = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_userProfilePhoto, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        int i2 = this.has_video ? this.flags | 1 : this.flags & (-2);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeInt64(this.photo_id);
                        this.photo_small.serializeToStream(abstractSerializedData2);
                        this.photo_big.serializeToStream(abstractSerializedData2);
                        abstractSerializedData2.writeInt32(this.dc_id);
                    }
                };
                break;
            default:
                tLRPC$TL_userProfilePhoto = null;
                break;
        }
        if (tLRPC$TL_userProfilePhoto != null || !z) {
            if (tLRPC$TL_userProfilePhoto != null) {
                tLRPC$TL_userProfilePhoto.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_userProfilePhoto;
        }
        throw new RuntimeException(String.format("can't parse magic %x in UserProfilePhoto", Integer.valueOf(i)));
    }
}
