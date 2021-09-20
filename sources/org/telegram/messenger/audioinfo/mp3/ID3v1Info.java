package org.telegram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.AudioInfo;

public class ID3v1Info extends AudioInfo {
    public static boolean isID3v1StartPosition(InputStream inputStream) throws IOException {
        inputStream.mark(3);
        try {
            return inputStream.read() == 84 && inputStream.read() == 65 && inputStream.read() == 71;
        } finally {
            inputStream.reset();
        }
    }

    public ID3v1Info(InputStream inputStream) throws IOException {
        if (isID3v1StartPosition(inputStream)) {
            this.brand = "ID3";
            byte[] readBytes = readBytes(inputStream, 128);
            this.title = extractString(readBytes, 3, 30);
            this.artist = extractString(readBytes, 33, 30);
            this.album = extractString(readBytes, 63, 30);
            try {
                this.year = Short.parseShort(extractString(readBytes, 93, 4));
            } catch (NumberFormatException unused) {
                this.year = 0;
            }
            this.comment = extractString(readBytes, 97, 30);
            ID3v1Genre genre = ID3v1Genre.getGenre(readBytes[127]);
            if (genre != null) {
                this.genre = genre.getDescription();
            }
            if (readBytes[125] == 0 && readBytes[126] != 0) {
                this.track = (short) (readBytes[126] & 255);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] readBytes(InputStream inputStream, int i) throws IOException {
        byte[] bArr = new byte[i];
        int i2 = 0;
        while (i2 < i) {
            int read = inputStream.read(bArr, i2, i - i2);
            if (read > 0) {
                i2 += read;
            } else {
                throw new EOFException();
            }
        }
        return bArr;
    }

    /* access modifiers changed from: package-private */
    public String extractString(byte[] bArr, int i, int i2) {
        try {
            String str = new String(bArr, i, i2, "ISO-8859-1");
            int indexOf = str.indexOf(0);
            return indexOf < 0 ? str : str.substring(0, indexOf);
        } catch (Exception unused) {
            return "";
        }
    }
}
