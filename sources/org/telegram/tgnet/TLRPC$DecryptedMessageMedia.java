package org.telegram.tgnet;

import java.util.ArrayList;

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
        TLRPC$DecryptedMessageMedia tLRPC$DecryptedMessageMedia;
        switch (i) {
            case -1978796689:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaVenue();
                break;
            case -1760785394:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaVideo();
                break;
            case -1332395189:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaDocument_layer8();
                break;
            case -452652584:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaWebPage();
                break;
            case -235238024:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaPhoto();
                break;
            case -90853155:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaExternalDocument();
                break;
            case 144661578:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaEmpty();
                break;
            case 846826124:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaPhoto_layer8();
                break;
            case 893913689:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaGeoPoint();
                break;
            case 1290694387:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaVideo_layer8();
                break;
            case 1380598109:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaVideo_layer17();
                break;
            case 1474341323:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaAudio();
                break;
            case 1485441687:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaContact();
                break;
            case 1619031439:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaAudio_layer8();
                break;
            case 2063502050:
                tLRPC$DecryptedMessageMedia = new TLRPC$TL_decryptedMessageMediaDocument();
                break;
            default:
                tLRPC$DecryptedMessageMedia = null;
                break;
        }
        if (tLRPC$DecryptedMessageMedia != null || !z) {
            if (tLRPC$DecryptedMessageMedia != null) {
                tLRPC$DecryptedMessageMedia.readParams(abstractSerializedData, z);
            }
            return tLRPC$DecryptedMessageMedia;
        }
        throw new RuntimeException(String.format("can't parse magic %x in DecryptedMessageMedia", new Object[]{Integer.valueOf(i)}));
    }
}
