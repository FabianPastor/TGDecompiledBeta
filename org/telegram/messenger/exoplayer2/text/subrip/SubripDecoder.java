package org.telegram.messenger.exoplayer2.text.subrip;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.telegram.messenger.exoplayer2.util.LongArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class SubripDecoder extends SimpleSubtitleDecoder {
    private static final Pattern SUBRIP_TIMESTAMP = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+),(\\d+)");
    private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("(\\S*)\\s*-->\\s*(\\S*)");
    private static final String TAG = "SubripDecoder";
    private final StringBuilder textBuilder = new StringBuilder();

    public SubripDecoder() {
        super(TAG);
    }

    protected SubripSubtitle decode(byte[] bytes, int length) {
        ArrayList<Cue> cues = new ArrayList();
        LongArray cueTimesUs = new LongArray();
        ParsableByteArray subripData = new ParsableByteArray(bytes, length);
        while (true) {
            String currentLine = subripData.readLine();
            if (currentLine == null) {
                Cue[] cuesArray = new Cue[cues.size()];
                cues.toArray(cuesArray);
                return new SubripSubtitle(cuesArray, cueTimesUs.toArray());
            } else if (currentLine.length() != 0) {
                try {
                    Integer.parseInt(currentLine);
                    boolean haveEndTimecode = false;
                    currentLine = subripData.readLine();
                    Matcher matcher = SUBRIP_TIMING_LINE.matcher(currentLine);
                    if (matcher.find()) {
                        cueTimesUs.add(parseTimecode(matcher.group(1)));
                        if (!TextUtils.isEmpty(matcher.group(2))) {
                            haveEndTimecode = true;
                            cueTimesUs.add(parseTimecode(matcher.group(2)));
                        }
                        this.textBuilder.setLength(0);
                        while (true) {
                            currentLine = subripData.readLine();
                            if (TextUtils.isEmpty(currentLine)) {
                                break;
                            }
                            if (this.textBuilder.length() > 0) {
                                this.textBuilder.append("<br>");
                            }
                            this.textBuilder.append(currentLine.trim());
                        }
                        cues.add(new Cue(Html.fromHtml(this.textBuilder.toString())));
                        if (haveEndTimecode) {
                            cues.add(null);
                        }
                    } else {
                        Log.w(TAG, "Skipping invalid timing: " + currentLine);
                    }
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Skipping invalid index: " + currentLine);
                }
            }
        }
    }

    private static long parseTimecode(String s) throws NumberFormatException {
        Matcher matcher = SUBRIP_TIMESTAMP.matcher(s);
        if (matcher.matches()) {
            return ((((((Long.parseLong(matcher.group(1)) * 60) * 60) * 1000) + ((Long.parseLong(matcher.group(2)) * 60) * 1000)) + (Long.parseLong(matcher.group(3)) * 1000)) + Long.parseLong(matcher.group(4))) * 1000;
        }
        throw new NumberFormatException("has invalid format");
    }
}
