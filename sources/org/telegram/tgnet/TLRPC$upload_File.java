package org.telegram.tgnet;

import java.util.ArrayList;

public abstract class TLRPC$upload_File extends TLObject {
    public NativeByteBuffer bytes;
    public int dc_id;
    public byte[] encryption_iv;
    public byte[] encryption_key;
    public ArrayList<TLRPC$TL_fileHash> file_hashes = new ArrayList<>();
    public byte[] file_token;
    public int mtime;
    public TLRPC$storage_FileType type;

    public static TLRPC$upload_File TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$upload_File tLRPC$upload_File;
        if (i != -NUM) {
            tLRPC$upload_File = i != NUM ? null : new TLRPC$TL_upload_file();
        } else {
            tLRPC$upload_File = new TLRPC$TL_upload_fileCdnRedirect();
        }
        if (tLRPC$upload_File != null || !z) {
            if (tLRPC$upload_File != null) {
                tLRPC$upload_File.readParams(abstractSerializedData, z);
            }
            return tLRPC$upload_File;
        }
        throw new RuntimeException(String.format("can't parse magic %x in upload_File", new Object[]{Integer.valueOf(i)}));
    }
}
