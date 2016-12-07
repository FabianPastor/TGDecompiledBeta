package org.telegram.messenger.exoplayer.text.webvtt;

import android.text.TextUtils;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.text.SubtitleParser;
import org.telegram.messenger.exoplayer.text.webvtt.WebvttCue.Builder;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class WebvttParser implements SubtitleParser {
    private final WebvttCueParser cueParser = new WebvttCueParser();
    private final ParsableByteArray parsableWebvttData = new ParsableByteArray();
    private final Builder webvttCueBuilder = new Builder();

    public final boolean canParse(String mimeType) {
        return MimeTypes.TEXT_VTT.equals(mimeType);
    }

    public final WebvttSubtitle parse(byte[] bytes, int offset, int length) throws ParserException {
        this.parsableWebvttData.reset(bytes, offset + length);
        this.parsableWebvttData.setPosition(offset);
        this.webvttCueBuilder.reset();
        WebvttParserUtil.validateWebvttHeaderLine(this.parsableWebvttData);
        do {
        } while (!TextUtils.isEmpty(this.parsableWebvttData.readLine()));
        ArrayList<WebvttCue> subtitles = new ArrayList();
        while (this.cueParser.parseNextValidCue(this.parsableWebvttData, this.webvttCueBuilder)) {
            subtitles.add(this.webvttCueBuilder.build());
            this.webvttCueBuilder.reset();
        }
        return new WebvttSubtitle(subtitles);
    }
}
