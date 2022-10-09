package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$DecryptedMessageMedia extends TLObject {
    public double _long;
    public long access_hash;
    public String address;
    public ArrayList<TLRPC$DocumentAttribute> attributes = new ArrayList<>();
    public String caption;
    public int date;
    public int dc_id;
    public int duration;
    public String file_name;
    public String first_name;
    public int h;
    public long id;
    public byte[] iv;
    public byte[] key;
    public String last_name;
    public double lat;
    public String mime_type;
    public String phone_number;
    public String provider;
    public long size;
    public int thumb_h;
    public int thumb_w;
    public String title;
    public String url;
    public long user_id;
    public String venue_id;
    public int w;

    public static TLRPC$DecryptedMessageMedia TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$DecryptedMessageMedia tLRPC$TL_decryptedMessageMediaVenue;
        switch (i) {
            case -1978796689:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaVenue();
                break;
            case -1760785394:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaVideo();
                break;
            case -1332395189:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaDocument() { // from class: org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument_layer8
                    public static int constructor = -NUM;
                    public byte[] thumb;

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.thumb = abstractSerializedData2.readByteArray(z2);
                        this.thumb_w = abstractSerializedData2.readInt32(z2);
                        this.thumb_h = abstractSerializedData2.readInt32(z2);
                        this.file_name = abstractSerializedData2.readString(z2);
                        this.mime_type = abstractSerializedData2.readString(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaDocument, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeByteArray(this.thumb);
                        abstractSerializedData2.writeInt32(this.thumb_w);
                        abstractSerializedData2.writeInt32(this.thumb_h);
                        abstractSerializedData2.writeString(this.file_name);
                        abstractSerializedData2.writeString(this.mime_type);
                        abstractSerializedData2.writeInt32((int) this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case -452652584:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaWebPage();
                break;
            case -235238024:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaPhoto();
                break;
            case -90853155:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaExternalDocument();
                break;
            case 144661578:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaEmpty();
                break;
            case 846826124:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaPhoto() { // from class: org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto_layer8
                    public static int constructor = NUM;
                    public byte[] thumb;

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.thumb = abstractSerializedData2.readByteArray(z2);
                        this.thumb_w = abstractSerializedData2.readInt32(z2);
                        this.thumb_h = abstractSerializedData2.readInt32(z2);
                        this.w = abstractSerializedData2.readInt32(z2);
                        this.h = abstractSerializedData2.readInt32(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaPhoto, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeByteArray(this.thumb);
                        abstractSerializedData2.writeInt32(this.thumb_w);
                        abstractSerializedData2.writeInt32(this.thumb_h);
                        abstractSerializedData2.writeInt32(this.w);
                        abstractSerializedData2.writeInt32(this.h);
                        abstractSerializedData2.writeInt32((int) this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case 893913689:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaGeoPoint();
                break;
            case 1290694387:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaVideo() { // from class: org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo_layer8
                    public static int constructor = NUM;
                    public byte[] thumb;

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.thumb = abstractSerializedData2.readByteArray(z2);
                        this.thumb_w = abstractSerializedData2.readInt32(z2);
                        this.thumb_h = abstractSerializedData2.readInt32(z2);
                        this.duration = abstractSerializedData2.readInt32(z2);
                        this.w = abstractSerializedData2.readInt32(z2);
                        this.h = abstractSerializedData2.readInt32(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeByteArray(this.thumb);
                        abstractSerializedData2.writeInt32(this.thumb_w);
                        abstractSerializedData2.writeInt32(this.thumb_h);
                        abstractSerializedData2.writeInt32(this.duration);
                        abstractSerializedData2.writeInt32(this.w);
                        abstractSerializedData2.writeInt32(this.h);
                        abstractSerializedData2.writeInt32((int) this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case 1380598109:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaVideo() { // from class: org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo_layer17
                    public static int constructor = NUM;
                    public byte[] thumb;

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.thumb = abstractSerializedData2.readByteArray(z2);
                        this.thumb_w = abstractSerializedData2.readInt32(z2);
                        this.thumb_h = abstractSerializedData2.readInt32(z2);
                        this.duration = abstractSerializedData2.readInt32(z2);
                        this.mime_type = abstractSerializedData2.readString(z2);
                        this.w = abstractSerializedData2.readInt32(z2);
                        this.h = abstractSerializedData2.readInt32(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaVideo, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeByteArray(this.thumb);
                        abstractSerializedData2.writeInt32(this.thumb_w);
                        abstractSerializedData2.writeInt32(this.thumb_h);
                        abstractSerializedData2.writeInt32(this.duration);
                        abstractSerializedData2.writeString(this.mime_type);
                        abstractSerializedData2.writeInt32(this.w);
                        abstractSerializedData2.writeInt32(this.h);
                        abstractSerializedData2.writeInt32((int) this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case 1474341323:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaAudio();
                break;
            case 1485441687:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaContact();
                break;
            case 1619031439:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaAudio() { // from class: org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaAudio_layer8
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaAudio, org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.duration = abstractSerializedData2.readInt32(z2);
                        this.size = abstractSerializedData2.readInt32(z2);
                        this.key = abstractSerializedData2.readByteArray(z2);
                        this.iv = abstractSerializedData2.readByteArray(z2);
                    }

                    @Override // org.telegram.tgnet.TLRPC$TL_decryptedMessageMediaAudio, org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.duration);
                        abstractSerializedData2.writeInt32((int) this.size);
                        abstractSerializedData2.writeByteArray(this.key);
                        abstractSerializedData2.writeByteArray(this.iv);
                    }
                };
                break;
            case 2063502050:
                tLRPC$TL_decryptedMessageMediaVenue = new TLRPC$TL_decryptedMessageMediaDocument();
                break;
            default:
                tLRPC$TL_decryptedMessageMediaVenue = null;
                break;
        }
        if (tLRPC$TL_decryptedMessageMediaVenue != null || !z) {
            if (tLRPC$TL_decryptedMessageMediaVenue != null) {
                tLRPC$TL_decryptedMessageMediaVenue.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_decryptedMessageMediaVenue;
        }
        throw new RuntimeException(String.format("can't parse magic %x in DecryptedMessageMedia", Integer.valueOf(i)));
    }
}
