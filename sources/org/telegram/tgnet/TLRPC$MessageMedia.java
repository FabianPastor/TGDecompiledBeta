package org.telegram.tgnet;

public abstract class TLRPC$MessageMedia extends TLObject {
    public String address;
    public TLRPC$Audio audio_unused;
    public byte[] bytes;
    public String captionLegacy;
    public String currency;
    public String description;
    public TLRPC$Document document;
    public String first_name;
    public int flags;
    public TLRPC$TL_game game;
    public TLRPC$GeoPoint geo;
    public int heading;
    public String last_name;
    public int period;
    public String phone_number;
    public TLRPC$Photo photo;
    public String provider;
    public int proximity_notification_radius;
    public int receipt_msg_id;
    public boolean shipping_address_requested;
    public String start_param;
    public boolean test;
    public String title;
    public long total_amount;
    public int ttl_seconds;
    public long user_id;
    public String vcard;
    public String venue_id;
    public String venue_type;
    public TLRPC$Video video_unused;
    public TLRPC$WebPage webpage;

    public static TLRPC$MessageMedia TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$MessageMedia tLRPC$MessageMedia;
        TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument;
        switch (i) {
            case -2074799289:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaInvoice();
                break;
            case -1666158377:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaDocument();
                break;
            case -1618676578:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaUnsupported();
                break;
            case -1563278704:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaVideo_old();
                break;
            case -1557277184:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaWebPage();
                break;
            case -1256047857:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaPhoto_layer74();
                break;
            case -1186937242:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaGeoLive();
                break;
            case -961117440:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaAudio_layer45();
                break;
            case -926655958:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaPhoto_old();
                break;
            case -873313984:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaContact_layer131();
                break;
            case -203411800:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaDocument_layer68();
                break;
            case -38694904:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaGame();
                break;
            case 694364726:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaUnsupported_old();
                break;
            case 784356159:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaVenue();
                break;
            case 802824708:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaDocument_old();
                break;
            case 1032643901:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaPhoto_layer68();
                break;
            case 1038967584:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaEmpty();
                break;
            case 1065280907:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaDice();
                break;
            case 1272375192:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaPoll();
                break;
            case 1457575028:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaGeo();
                break;
            case 1540298357:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaVideo_layer45();
                break;
            case 1585262393:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaContact_layer81();
                break;
            case 1670374507:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaDice_layer111();
                break;
            case 1766936791:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaPhoto();
                break;
            case 1882335561:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaContact();
                break;
            case 2031269663:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaVenue_layer71();
                break;
            case 2084316681:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaGeoLive_layer119();
                break;
            case 2084836563:
                tLRPC$MessageMedia = new TLRPC$TL_messageMediaDocument_layer74();
                break;
            default:
                tLRPC$MessageMedia = null;
                break;
        }
        if (tLRPC$MessageMedia == null && z) {
            throw new RuntimeException(String.format("can't parse magic %x in MessageMedia", new Object[]{Integer.valueOf(i)}));
        } else if (tLRPC$MessageMedia == null) {
            return tLRPC$MessageMedia;
        } else {
            tLRPC$MessageMedia.readParams(abstractSerializedData, z);
            if (tLRPC$MessageMedia.video_unused != null) {
                tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
                if (tLRPC$MessageMedia.video_unused instanceof TLRPC$TL_videoEncrypted) {
                    TLRPC$TL_documentEncrypted tLRPC$TL_documentEncrypted = new TLRPC$TL_documentEncrypted();
                    tLRPC$TL_messageMediaDocument.document = tLRPC$TL_documentEncrypted;
                    TLRPC$Video tLRPC$Video = tLRPC$MessageMedia.video_unused;
                    tLRPC$TL_documentEncrypted.key = tLRPC$Video.key;
                    tLRPC$TL_documentEncrypted.iv = tLRPC$Video.iv;
                } else {
                    tLRPC$TL_messageMediaDocument.document = new TLRPC$TL_document();
                }
                tLRPC$TL_messageMediaDocument.flags = 3;
                TLRPC$Document tLRPC$Document = tLRPC$TL_messageMediaDocument.document;
                tLRPC$Document.file_reference = new byte[0];
                TLRPC$Video tLRPC$Video2 = tLRPC$MessageMedia.video_unused;
                tLRPC$Document.id = tLRPC$Video2.id;
                tLRPC$Document.access_hash = tLRPC$Video2.access_hash;
                tLRPC$Document.date = tLRPC$Video2.date;
                String str = tLRPC$Video2.mime_type;
                if (str != null) {
                    tLRPC$Document.mime_type = str;
                } else {
                    tLRPC$Document.mime_type = "video/mp4";
                }
                tLRPC$Document.size = tLRPC$Video2.size;
                tLRPC$Document.thumbs.add(tLRPC$Video2.thumb);
                tLRPC$TL_messageMediaDocument.document.dc_id = tLRPC$MessageMedia.video_unused.dc_id;
                tLRPC$TL_messageMediaDocument.captionLegacy = tLRPC$MessageMedia.captionLegacy;
                TLRPC$TL_documentAttributeVideo tLRPC$TL_documentAttributeVideo = new TLRPC$TL_documentAttributeVideo();
                TLRPC$Video tLRPC$Video3 = tLRPC$MessageMedia.video_unused;
                tLRPC$TL_documentAttributeVideo.w = tLRPC$Video3.w;
                tLRPC$TL_documentAttributeVideo.h = tLRPC$Video3.h;
                tLRPC$TL_documentAttributeVideo.duration = tLRPC$Video3.duration;
                tLRPC$TL_messageMediaDocument.document.attributes.add(tLRPC$TL_documentAttributeVideo);
                if (tLRPC$TL_messageMediaDocument.captionLegacy == null) {
                    tLRPC$TL_messageMediaDocument.captionLegacy = "";
                }
            } else if (tLRPC$MessageMedia.audio_unused == null) {
                return tLRPC$MessageMedia;
            } else {
                tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
                if (tLRPC$MessageMedia.audio_unused instanceof TLRPC$TL_audioEncrypted) {
                    TLRPC$TL_documentEncrypted tLRPC$TL_documentEncrypted2 = new TLRPC$TL_documentEncrypted();
                    tLRPC$TL_messageMediaDocument.document = tLRPC$TL_documentEncrypted2;
                    TLRPC$Audio tLRPC$Audio = tLRPC$MessageMedia.audio_unused;
                    tLRPC$TL_documentEncrypted2.key = tLRPC$Audio.key;
                    tLRPC$TL_documentEncrypted2.iv = tLRPC$Audio.iv;
                } else {
                    tLRPC$TL_messageMediaDocument.document = new TLRPC$TL_document();
                }
                tLRPC$TL_messageMediaDocument.flags = 3;
                TLRPC$Document tLRPC$Document2 = tLRPC$TL_messageMediaDocument.document;
                tLRPC$Document2.file_reference = new byte[0];
                TLRPC$Audio tLRPC$Audio2 = tLRPC$MessageMedia.audio_unused;
                tLRPC$Document2.id = tLRPC$Audio2.id;
                tLRPC$Document2.access_hash = tLRPC$Audio2.access_hash;
                tLRPC$Document2.date = tLRPC$Audio2.date;
                String str2 = tLRPC$Audio2.mime_type;
                if (str2 != null) {
                    tLRPC$Document2.mime_type = str2;
                } else {
                    tLRPC$Document2.mime_type = "audio/ogg";
                }
                tLRPC$Document2.size = tLRPC$Audio2.size;
                TLRPC$TL_photoSizeEmpty tLRPC$TL_photoSizeEmpty = new TLRPC$TL_photoSizeEmpty();
                tLRPC$TL_photoSizeEmpty.type = "s";
                tLRPC$TL_messageMediaDocument.document.thumbs.add(tLRPC$TL_photoSizeEmpty);
                tLRPC$TL_messageMediaDocument.document.dc_id = tLRPC$MessageMedia.audio_unused.dc_id;
                tLRPC$TL_messageMediaDocument.captionLegacy = tLRPC$MessageMedia.captionLegacy;
                TLRPC$TL_documentAttributeAudio tLRPC$TL_documentAttributeAudio = new TLRPC$TL_documentAttributeAudio();
                tLRPC$TL_documentAttributeAudio.duration = tLRPC$MessageMedia.audio_unused.duration;
                tLRPC$TL_documentAttributeAudio.voice = true;
                tLRPC$TL_messageMediaDocument.document.attributes.add(tLRPC$TL_documentAttributeAudio);
                if (tLRPC$TL_messageMediaDocument.captionLegacy == null) {
                    tLRPC$TL_messageMediaDocument.captionLegacy = "";
                }
            }
            return tLRPC$TL_messageMediaDocument;
        }
    }
}
