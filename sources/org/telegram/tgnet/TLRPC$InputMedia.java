package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$InputMedia extends TLObject {
    public String address;
    public ArrayList<TLRPC$DocumentAttribute> attributes = new ArrayList<>();
    public TLRPC$InputFile file;
    public String first_name;
    public int flags;
    public TLRPC$InputGeoPoint geo_point;
    public String last_name;
    public String mime_type;
    public boolean nosound_video;
    public int period;
    public String phone_number;
    public String provider;
    public ArrayList<TLRPC$InputDocument> stickers = new ArrayList<>();
    public boolean stopped;
    public TLRPC$InputFile thumb;
    public String title;
    public int ttl_seconds;
    public String vcard;
    public String venue_id;
    public String venue_type;

    public static TLRPC$InputMedia TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$InputMedia tLRPC$InputMedia;
        switch (i) {
            case -1771768449:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaEmpty();
                break;
            case -1279654347:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaPhoto();
                break;
            case -1052959727:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaVenue();
                break;
            case -833715459:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaGeoLive();
                break;
            case -750828557:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaGame();
                break;
            case -440664550:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaPhotoExternal();
                break;
            case -428884101:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaDice();
                break;
            case -122978821:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaContact();
                break;
            case -104578748:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaGeoPoint();
                break;
            case -78455655:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaDocumentExternal();
                break;
            case 261416433:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaPoll();
                break;
            case 505969924:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaUploadedPhoto();
                break;
            case 598418386:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaDocument();
                break;
            case 1530447553:
                tLRPC$InputMedia = new TLRPC$TL_inputMediaUploadedDocument();
                break;
            default:
                tLRPC$InputMedia = null;
                break;
        }
        if (tLRPC$InputMedia != null || !z) {
            if (tLRPC$InputMedia != null) {
                tLRPC$InputMedia.readParams(abstractSerializedData, z);
            }
            return tLRPC$InputMedia;
        }
        throw new RuntimeException(String.format("can't parse magic %x in InputMedia", new Object[]{Integer.valueOf(i)}));
    }
}
