package org.telegram.messenger.exoplayer2.text.webvtt;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.text.webvtt.WebvttCue.Builder;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class WebvttDecoder extends SimpleSubtitleDecoder {
    private static final String COMMENT_START = "NOTE";
    private static final int EVENT_COMMENT = 1;
    private static final int EVENT_CUE = 3;
    private static final int EVENT_END_OF_FILE = 0;
    private static final int EVENT_NONE = -1;
    private static final int EVENT_STYLE_BLOCK = 2;
    private static final String STYLE_START = "STYLE";
    private final CssParser cssParser = new CssParser();
    private final WebvttCueParser cueParser = new WebvttCueParser();
    private final List<WebvttCssStyle> definedStyles = new ArrayList();
    private final ParsableByteArray parsableWebvttData = new ParsableByteArray();
    private final Builder webvttCueBuilder = new Builder();

    public WebvttDecoder() {
        super("WebvttDecoder");
    }

    protected WebvttSubtitle decode(byte[] bArr, int i, boolean z) throws SubtitleDecoderException {
        this.parsableWebvttData.reset(bArr, i);
        this.webvttCueBuilder.reset();
        this.definedStyles.clear();
        WebvttParserUtil.validateWebvttHeaderLine(this.parsableWebvttData);
        while (TextUtils.isEmpty(this.parsableWebvttData.readLine()) == null) {
        }
        bArr = new ArrayList();
        while (true) {
            boolean nextEvent = getNextEvent(this.parsableWebvttData);
            if (!nextEvent) {
                return new WebvttSubtitle(bArr);
            }
            if (nextEvent) {
                skipComment(this.parsableWebvttData);
            } else if (nextEvent) {
                if (bArr.isEmpty() == 0) {
                    throw new SubtitleDecoderException("A style block was found after the first cue.");
                }
                this.parsableWebvttData.readLine();
                i = this.cssParser.parseBlock(this.parsableWebvttData);
                if (i != 0) {
                    this.definedStyles.add(i);
                }
            } else if (nextEvent && this.cueParser.parseCue(this.parsableWebvttData, this.webvttCueBuilder, this.definedStyles) != 0) {
                bArr.add(this.webvttCueBuilder.build());
                this.webvttCueBuilder.reset();
            }
        }
    }

    private static int getNextEvent(ParsableByteArray parsableByteArray) {
        int i = 0;
        int i2 = -1;
        while (i2 == -1) {
            i = parsableByteArray.getPosition();
            String readLine = parsableByteArray.readLine();
            i2 = readLine == null ? 0 : STYLE_START.equals(readLine) ? 2 : COMMENT_START.startsWith(readLine) ? 1 : 3;
        }
        parsableByteArray.setPosition(i);
        return i2;
    }

    private static void skipComment(ParsableByteArray parsableByteArray) {
        while (!TextUtils.isEmpty(parsableByteArray.readLine())) {
        }
    }
}
