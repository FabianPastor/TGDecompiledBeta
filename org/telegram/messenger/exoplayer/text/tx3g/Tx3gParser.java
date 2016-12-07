package org.telegram.messenger.exoplayer.text.tx3g;

import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.Subtitle;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class Tx3gParser implements SubtitleParser {
    private final ParsableByteArray parsableByteArray = new ParsableByteArray();

    public boolean canParse(String mimeType) {
        return MimeTypes.APPLICATION_TX3G.equals(mimeType);
    }

    public Subtitle parse(byte[] bytes, int offset, int length) {
        this.parsableByteArray.reset(bytes, length);
        int textLength = this.parsableByteArray.readUnsignedShort();
        if (textLength == 0) {
            return Tx3gSubtitle.EMPTY;
        }
        return new Tx3gSubtitle(new Cue(this.parsableByteArray.readString(textLength)));
    }
}
