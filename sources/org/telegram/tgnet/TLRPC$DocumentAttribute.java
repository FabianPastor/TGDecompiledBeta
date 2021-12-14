package org.telegram.tgnet;

public abstract class TLRPC$DocumentAttribute extends TLObject {
    public String alt;
    public int duration;
    public String file_name;
    public int flags;
    public int h;
    public boolean mask;
    public TLRPC$TL_maskCoords mask_coords;
    public String performer;
    public boolean round_message;
    public TLRPC$InputStickerSet stickerset;
    public boolean supports_streaming;
    public String title;
    public boolean voice;
    public int w;
    public byte[] waveform;

    public static TLRPC$DocumentAttribute TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$DocumentAttribute tLRPC$DocumentAttribute;
        switch (i) {
            case -1744710921:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeHasStickers();
                break;
            case -1739392570:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeAudio();
                break;
            case -1723033470:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeSticker_old2();
                break;
            case -556656416:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeAudio_layer45();
                break;
            case -83208409:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeSticker_old();
                break;
            case 85215461:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeAudio_old();
                break;
            case 250621158:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeVideo();
                break;
            case 297109817:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeAnimated();
                break;
            case 358154344:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeFilename();
                break;
            case 978674434:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeSticker_layer55();
                break;
            case 1494273227:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeVideo_layer65();
                break;
            case 1662637586:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeSticker();
                break;
            case 1815593308:
                tLRPC$DocumentAttribute = new TLRPC$TL_documentAttributeImageSize();
                break;
            default:
                tLRPC$DocumentAttribute = null;
                break;
        }
        if (tLRPC$DocumentAttribute != null || !z) {
            if (tLRPC$DocumentAttribute != null) {
                tLRPC$DocumentAttribute.readParams(abstractSerializedData, z);
            }
            return tLRPC$DocumentAttribute;
        }
        throw new RuntimeException(String.format("can't parse magic %x in DocumentAttribute", new Object[]{Integer.valueOf(i)}));
    }
}
