package org.telegram.tgnet;

public abstract class TLRPC$Video extends TLObject {
    public long access_hash;
    public String caption;
    public int date;
    public int dc_id;
    public int duration;
    public int h;
    public long id;
    public byte[] iv;
    public byte[] key;
    public String mime_type;
    public int size;
    public TLRPC$PhotoSize thumb;
    public long user_id;
    public int w;

    public static TLRPC$Video TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Video tLRPC$Video;
        switch (i) {
            case -1056548696:
                tLRPC$Video = new TLRPC$TL_videoEmpty_layer45();
                break;
            case -291550643:
                tLRPC$Video = new TLRPC$TL_video_old3();
                break;
            case -148338733:
                tLRPC$Video = new TLRPC$TL_video_layer45();
                break;
            case 948937617:
                tLRPC$Video = new TLRPC$TL_video_old2();
                break;
            case 1431655763:
                tLRPC$Video = new TLRPC$TL_videoEncrypted();
                break;
            case 1510253727:
                tLRPC$Video = new TLRPC$TL_video_old();
                break;
            default:
                tLRPC$Video = null;
                break;
        }
        if (tLRPC$Video != null || !z) {
            if (tLRPC$Video != null) {
                tLRPC$Video.readParams(abstractSerializedData, z);
            }
            return tLRPC$Video;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Video", new Object[]{Integer.valueOf(i)}));
    }
}
