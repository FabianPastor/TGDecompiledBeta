package org.telegram.tgnet;

public abstract class TLRPC$Audio extends TLObject {
    public long access_hash;
    public int date;
    public int dc_id;
    public int duration;
    public long id;
    public byte[] iv;
    public byte[] key;
    public String mime_type;
    public int size;
    public long user_id;

    public static TLRPC$Audio TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$Audio tLRPC$Audio;
        switch (i) {
            case -945003370:
                tLRPC$Audio = new TLRPC$TL_audio_old2();
                break;
            case -102543275:
                tLRPC$Audio = new TLRPC$TL_audio_layer45();
                break;
            case 1114908135:
                tLRPC$Audio = new TLRPC$TL_audio_old();
                break;
            case 1431655926:
                tLRPC$Audio = new TLRPC$TL_audioEncrypted();
                break;
            case 1483311320:
                tLRPC$Audio = new TLRPC$TL_audioEmpty_layer45();
                break;
            default:
                tLRPC$Audio = null;
                break;
        }
        if (tLRPC$Audio != null || !z) {
            if (tLRPC$Audio != null) {
                tLRPC$Audio.readParams(abstractSerializedData, z);
            }
            return tLRPC$Audio;
        }
        throw new RuntimeException(String.format("can't parse magic %x in Audio", new Object[]{Integer.valueOf(i)}));
    }
}
