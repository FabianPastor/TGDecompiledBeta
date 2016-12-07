package org.telegram.messenger.exoplayer.text;

import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.ExoPlaybackException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.MediaFormatHolder;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.SampleSource;
import org.telegram.messenger.exoplayer.SampleSourceTrackRenderer;
import org.telegram.messenger.exoplayer.util.Assertions;

@TargetApi(16)
public final class TextTrackRenderer extends SampleSourceTrackRenderer implements Callback {
    private static final List<Class<? extends SubtitleParser>> DEFAULT_PARSER_CLASSES = new ArrayList();
    private static final int MSG_UPDATE_OVERLAY = 0;
    private final MediaFormatHolder formatHolder;
    private boolean inputStreamEnded;
    private PlayableSubtitle nextSubtitle;
    private int nextSubtitleEventIndex;
    private SubtitleParserHelper parserHelper;
    private int parserIndex;
    private HandlerThread parserThread;
    private PlayableSubtitle subtitle;
    private final SubtitleParser[] subtitleParsers;
    private final TextRenderer textRenderer;
    private final Handler textRendererHandler;

    static {
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.webvtt.WebvttParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e) {
        }
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.ttml.TtmlParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e2) {
        }
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.webvtt.Mp4WebvttParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e3) {
        }
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.subrip.SubripParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e4) {
        }
        try {
            DEFAULT_PARSER_CLASSES.add(Class.forName("org.telegram.messenger.exoplayer.text.tx3g.Tx3gParser").asSubclass(SubtitleParser.class));
        } catch (ClassNotFoundException e5) {
        }
    }

    public TextTrackRenderer(SampleSource source, TextRenderer textRenderer, Looper textRendererLooper, SubtitleParser... subtitleParsers) {
        this(new SampleSource[]{source}, textRenderer, textRendererLooper, subtitleParsers);
    }

    public TextTrackRenderer(SampleSource[] sources, TextRenderer textRenderer, Looper textRendererLooper, SubtitleParser... subtitleParsers) {
        super(sources);
        this.textRenderer = (TextRenderer) Assertions.checkNotNull(textRenderer);
        this.textRendererHandler = textRendererLooper == null ? null : new Handler(textRendererLooper, this);
        if (subtitleParsers == null || subtitleParsers.length == 0) {
            subtitleParsers = new SubtitleParser[DEFAULT_PARSER_CLASSES.size()];
            int i = 0;
            while (i < subtitleParsers.length) {
                try {
                    subtitleParsers[i] = (SubtitleParser) ((Class) DEFAULT_PARSER_CLASSES.get(i)).newInstance();
                    i++;
                } catch (InstantiationException e) {
                    throw new IllegalStateException("Unexpected error creating default parser", e);
                } catch (IllegalAccessException e2) {
                    throw new IllegalStateException("Unexpected error creating default parser", e2);
                }
            }
        }
        this.subtitleParsers = subtitleParsers;
        this.formatHolder = new MediaFormatHolder();
    }

    protected boolean handlesTrack(MediaFormat mediaFormat) {
        return getParserIndex(mediaFormat) != -1;
    }

    protected void onEnabled(int track, long positionUs, boolean joining) throws ExoPlaybackException {
        super.onEnabled(track, positionUs, joining);
        this.parserIndex = getParserIndex(getFormat(track));
        this.parserThread = new HandlerThread("textParser");
        this.parserThread.start();
        this.parserHelper = new SubtitleParserHelper(this.parserThread.getLooper(), this.subtitleParsers[this.parserIndex]);
    }

    protected void onDiscontinuity(long positionUs) {
        this.inputStreamEnded = false;
        this.subtitle = null;
        this.nextSubtitle = null;
        clearTextRenderer();
        if (this.parserHelper != null) {
            this.parserHelper.flush();
        }
    }

    protected void doSomeWork(long positionUs, long elapsedRealtimeUs, boolean sourceIsReady) throws ExoPlaybackException {
        if (this.nextSubtitle == null) {
            try {
                this.nextSubtitle = this.parserHelper.getAndClearResult();
            } catch (Throwable e) {
                throw new ExoPlaybackException(e);
            }
        }
        if (getState() == 3) {
            boolean textRendererNeedsUpdate = false;
            if (this.subtitle != null) {
                long subtitleNextEventTimeUs = getNextEventTime();
                while (subtitleNextEventTimeUs <= positionUs) {
                    this.nextSubtitleEventIndex++;
                    subtitleNextEventTimeUs = getNextEventTime();
                    textRendererNeedsUpdate = true;
                }
            }
            if (this.nextSubtitle != null && this.nextSubtitle.startTimeUs <= positionUs) {
                this.subtitle = this.nextSubtitle;
                this.nextSubtitle = null;
                this.nextSubtitleEventIndex = this.subtitle.getNextEventTimeIndex(positionUs);
                textRendererNeedsUpdate = true;
            }
            if (textRendererNeedsUpdate) {
                updateTextRenderer(this.subtitle.getCues(positionUs));
            }
            if (!this.inputStreamEnded && this.nextSubtitle == null && !this.parserHelper.isParsing()) {
                SampleHolder sampleHolder = this.parserHelper.getSampleHolder();
                sampleHolder.clearData();
                int result = readSource(positionUs, this.formatHolder, sampleHolder);
                if (result == -4) {
                    this.parserHelper.setFormat(this.formatHolder.format);
                } else if (result == -3) {
                    this.parserHelper.startParseOperation();
                } else if (result == -1) {
                    this.inputStreamEnded = true;
                }
            }
        }
    }

    protected void onDisabled() throws ExoPlaybackException {
        this.subtitle = null;
        this.nextSubtitle = null;
        this.parserThread.quit();
        this.parserThread = null;
        this.parserHelper = null;
        clearTextRenderer();
        super.onDisabled();
    }

    protected long getBufferedPositionUs() {
        return -3;
    }

    protected boolean isEnded() {
        return this.inputStreamEnded && (this.subtitle == null || getNextEventTime() == Long.MAX_VALUE);
    }

    protected boolean isReady() {
        return true;
    }

    private long getNextEventTime() {
        if (this.nextSubtitleEventIndex == -1 || this.nextSubtitleEventIndex >= this.subtitle.getEventTimeCount()) {
            return Long.MAX_VALUE;
        }
        return this.subtitle.getEventTime(this.nextSubtitleEventIndex);
    }

    private void updateTextRenderer(List<Cue> cues) {
        if (this.textRendererHandler != null) {
            this.textRendererHandler.obtainMessage(0, cues).sendToTarget();
        } else {
            invokeRendererInternalCues(cues);
        }
    }

    private void clearTextRenderer() {
        updateTextRenderer(Collections.emptyList());
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 0:
                invokeRendererInternalCues((List) msg.obj);
                return true;
            default:
                return false;
        }
    }

    private void invokeRendererInternalCues(List<Cue> cues) {
        this.textRenderer.onCues(cues);
    }

    private int getParserIndex(MediaFormat mediaFormat) {
        for (int i = 0; i < this.subtitleParsers.length; i++) {
            if (this.subtitleParsers[i].canParse(mediaFormat.mimeType)) {
                return i;
            }
        }
        return -1;
    }
}
