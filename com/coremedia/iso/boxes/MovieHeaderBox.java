package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.util.DateHelper;
import com.googlecode.mp4parser.util.Matrix;
import java.nio.ByteBuffer;
import java.util.Date;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public class MovieHeaderBox extends AbstractFullBox {
    public static final String TYPE = "mvhd";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_10 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_11 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_12 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_13 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_14 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_15 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_16 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_17 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_18 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_19 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_20 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_21 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_22 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_23 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_24 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_25 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_26 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_27 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_28 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_9 = null;
    private Date creationTime;
    private int currentTime;
    private long duration;
    private Matrix matrix = Matrix.ROTATE_0;
    private Date modificationTime;
    private long nextTrackId;
    private int posterTime;
    private int previewDuration;
    private int previewTime;
    private double rate = 1.0d;
    private int selectionDuration;
    private int selectionTime;
    private long timescale;
    private float volume = 1.0f;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("MovieHeaderBox.java", MovieHeaderBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getCreationTime", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.util.Date"), 63);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getModificationTime", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.util.Date"), 67);
        ajc$tjp_10 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setModificationTime", "com.coremedia.iso.boxes.MovieHeaderBox", "java.util.Date", "modificationTime", TtmlNode.ANONYMOUS_REGION_ID, "void"), 203);
        ajc$tjp_11 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setTimescale", "com.coremedia.iso.boxes.MovieHeaderBox", "long", "timescale", TtmlNode.ANONYMOUS_REGION_ID, "void"), 211);
        ajc$tjp_12 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setDuration", "com.coremedia.iso.boxes.MovieHeaderBox", "long", "duration", TtmlNode.ANONYMOUS_REGION_ID, "void"), 215);
        ajc$tjp_13 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setRate", "com.coremedia.iso.boxes.MovieHeaderBox", "double", "rate", TtmlNode.ANONYMOUS_REGION_ID, "void"), 222);
        ajc$tjp_14 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setVolume", "com.coremedia.iso.boxes.MovieHeaderBox", "float", "volume", TtmlNode.ANONYMOUS_REGION_ID, "void"), 226);
        ajc$tjp_15 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setMatrix", "com.coremedia.iso.boxes.MovieHeaderBox", "com.googlecode.mp4parser.util.Matrix", "matrix", TtmlNode.ANONYMOUS_REGION_ID, "void"), 230);
        ajc$tjp_16 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setNextTrackId", "com.coremedia.iso.boxes.MovieHeaderBox", "long", "nextTrackId", TtmlNode.ANONYMOUS_REGION_ID, "void"), 234);
        ajc$tjp_17 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getPreviewTime", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 238);
        ajc$tjp_18 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setPreviewTime", "com.coremedia.iso.boxes.MovieHeaderBox", "int", "previewTime", TtmlNode.ANONYMOUS_REGION_ID, "void"), 242);
        ajc$tjp_19 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getPreviewDuration", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 246);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getTimescale", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 71);
        ajc$tjp_20 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setPreviewDuration", "com.coremedia.iso.boxes.MovieHeaderBox", "int", "previewDuration", TtmlNode.ANONYMOUS_REGION_ID, "void"), Callback.DEFAULT_SWIPE_ANIMATION_DURATION);
        ajc$tjp_21 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getPosterTime", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 254);
        ajc$tjp_22 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setPosterTime", "com.coremedia.iso.boxes.MovieHeaderBox", "int", "posterTime", TtmlNode.ANONYMOUS_REGION_ID, "void"), 258);
        ajc$tjp_23 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSelectionTime", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 262);
        ajc$tjp_24 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSelectionTime", "com.coremedia.iso.boxes.MovieHeaderBox", "int", "selectionTime", TtmlNode.ANONYMOUS_REGION_ID, "void"), 266);
        ajc$tjp_25 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getSelectionDuration", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 270);
        ajc$tjp_26 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setSelectionDuration", "com.coremedia.iso.boxes.MovieHeaderBox", "int", "selectionDuration", TtmlNode.ANONYMOUS_REGION_ID, "void"), 274);
        ajc$tjp_27 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getCurrentTime", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 278);
        ajc$tjp_28 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setCurrentTime", "com.coremedia.iso.boxes.MovieHeaderBox", "int", "currentTime", TtmlNode.ANONYMOUS_REGION_ID, "void"), 282);
        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getDuration", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 75);
        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getRate", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "double"), 79);
        ajc$tjp_5 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getVolume", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "float"), 83);
        ajc$tjp_6 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getMatrix", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "com.googlecode.mp4parser.util.Matrix"), 87);
        ajc$tjp_7 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getNextTrackId", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "long"), 91);
        ajc$tjp_8 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "toString", "com.coremedia.iso.boxes.MovieHeaderBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 139);
        ajc$tjp_9 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setCreationTime", "com.coremedia.iso.boxes.MovieHeaderBox", "java.util.Date", "creationTime", TtmlNode.ANONYMOUS_REGION_ID, "void"), 195);
    }

    public MovieHeaderBox() {
        super(TYPE);
    }

    public Date getCreationTime() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.creationTime;
    }

    public Date getModificationTime() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, this, this));
        return this.modificationTime;
    }

    public long getTimescale() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.timescale;
    }

    public long getDuration() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, this, this));
        return this.duration;
    }

    public double getRate() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        return this.rate;
    }

    public float getVolume() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, this, this));
        return this.volume;
    }

    public Matrix getMatrix() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
        return this.matrix;
    }

    public long getNextTrackId() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, this, this));
        return this.nextTrackId;
    }

    protected long getContentSize() {
        long contentSize;
        if (getVersion() == 1) {
            contentSize = 4 + 28;
        } else {
            contentSize = 4 + 16;
        }
        return contentSize + 80;
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        if (getVersion() == 1) {
            this.creationTime = DateHelper.convert(IsoTypeReader.readUInt64(content));
            this.modificationTime = DateHelper.convert(IsoTypeReader.readUInt64(content));
            this.timescale = IsoTypeReader.readUInt32(content);
            this.duration = IsoTypeReader.readUInt64(content);
        } else {
            this.creationTime = DateHelper.convert(IsoTypeReader.readUInt32(content));
            this.modificationTime = DateHelper.convert(IsoTypeReader.readUInt32(content));
            this.timescale = IsoTypeReader.readUInt32(content);
            this.duration = IsoTypeReader.readUInt32(content);
        }
        this.rate = IsoTypeReader.readFixedPoint1616(content);
        this.volume = IsoTypeReader.readFixedPoint88(content);
        IsoTypeReader.readUInt16(content);
        IsoTypeReader.readUInt32(content);
        IsoTypeReader.readUInt32(content);
        this.matrix = Matrix.fromByteBuffer(content);
        this.previewTime = content.getInt();
        this.previewDuration = content.getInt();
        this.posterTime = content.getInt();
        this.selectionTime = content.getInt();
        this.selectionDuration = content.getInt();
        this.currentTime = content.getInt();
        this.nextTrackId = IsoTypeReader.readUInt32(content);
    }

    public String toString() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this));
        StringBuilder result = new StringBuilder();
        result.append("MovieHeaderBox[");
        result.append("creationTime=").append(getCreationTime());
        result.append(";");
        result.append("modificationTime=").append(getModificationTime());
        result.append(";");
        result.append("timescale=").append(getTimescale());
        result.append(";");
        result.append("duration=").append(getDuration());
        result.append(";");
        result.append("rate=").append(getRate());
        result.append(";");
        result.append("volume=").append(getVolume());
        result.append(";");
        result.append("matrix=").append(this.matrix);
        result.append(";");
        result.append("nextTrackId=").append(getNextTrackId());
        result.append("]");
        return result.toString();
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        if (getVersion() == 1) {
            IsoTypeWriter.writeUInt64(byteBuffer, DateHelper.convert(this.creationTime));
            IsoTypeWriter.writeUInt64(byteBuffer, DateHelper.convert(this.modificationTime));
            IsoTypeWriter.writeUInt32(byteBuffer, this.timescale);
            IsoTypeWriter.writeUInt64(byteBuffer, this.duration);
        } else {
            IsoTypeWriter.writeUInt32(byteBuffer, DateHelper.convert(this.creationTime));
            IsoTypeWriter.writeUInt32(byteBuffer, DateHelper.convert(this.modificationTime));
            IsoTypeWriter.writeUInt32(byteBuffer, this.timescale);
            IsoTypeWriter.writeUInt32(byteBuffer, this.duration);
        }
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.rate);
        IsoTypeWriter.writeFixedPoint88(byteBuffer, (double) this.volume);
        IsoTypeWriter.writeUInt16(byteBuffer, 0);
        IsoTypeWriter.writeUInt32(byteBuffer, 0);
        IsoTypeWriter.writeUInt32(byteBuffer, 0);
        this.matrix.getContent(byteBuffer);
        byteBuffer.putInt(this.previewTime);
        byteBuffer.putInt(this.previewDuration);
        byteBuffer.putInt(this.posterTime);
        byteBuffer.putInt(this.selectionTime);
        byteBuffer.putInt(this.selectionDuration);
        byteBuffer.putInt(this.currentTime);
        IsoTypeWriter.writeUInt32(byteBuffer, this.nextTrackId);
    }

    public void setCreationTime(Date creationTime) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, (Object) this, (Object) this, (Object) creationTime));
        this.creationTime = creationTime;
        if (DateHelper.convert(creationTime) >= 4294967296L) {
            setVersion(1);
        }
    }

    public void setModificationTime(Date modificationTime) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, (Object) this, (Object) this, (Object) modificationTime));
        this.modificationTime = modificationTime;
        if (DateHelper.convert(modificationTime) >= 4294967296L) {
            setVersion(1);
        }
    }

    public void setTimescale(long timescale) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, (Object) this, (Object) this, Conversions.longObject(timescale)));
        this.timescale = timescale;
    }

    public void setDuration(long duration) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_12, (Object) this, (Object) this, Conversions.longObject(duration)));
        this.duration = duration;
        if (duration >= 4294967296L) {
            setVersion(1);
        }
    }

    public void setRate(double rate) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_13, (Object) this, (Object) this, Conversions.doubleObject(rate)));
        this.rate = rate;
    }

    public void setVolume(float volume) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_14, (Object) this, (Object) this, Conversions.floatObject(volume)));
        this.volume = volume;
    }

    public void setMatrix(Matrix matrix) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_15, (Object) this, (Object) this, (Object) matrix));
        this.matrix = matrix;
    }

    public void setNextTrackId(long nextTrackId) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_16, (Object) this, (Object) this, Conversions.longObject(nextTrackId)));
        this.nextTrackId = nextTrackId;
    }

    public int getPreviewTime() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_17, this, this));
        return this.previewTime;
    }

    public void setPreviewTime(int previewTime) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_18, (Object) this, (Object) this, Conversions.intObject(previewTime)));
        this.previewTime = previewTime;
    }

    public int getPreviewDuration() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_19, this, this));
        return this.previewDuration;
    }

    public void setPreviewDuration(int previewDuration) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_20, (Object) this, (Object) this, Conversions.intObject(previewDuration)));
        this.previewDuration = previewDuration;
    }

    public int getPosterTime() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_21, this, this));
        return this.posterTime;
    }

    public void setPosterTime(int posterTime) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_22, (Object) this, (Object) this, Conversions.intObject(posterTime)));
        this.posterTime = posterTime;
    }

    public int getSelectionTime() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_23, this, this));
        return this.selectionTime;
    }

    public void setSelectionTime(int selectionTime) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_24, (Object) this, (Object) this, Conversions.intObject(selectionTime)));
        this.selectionTime = selectionTime;
    }

    public int getSelectionDuration() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_25, this, this));
        return this.selectionDuration;
    }

    public void setSelectionDuration(int selectionDuration) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_26, (Object) this, (Object) this, Conversions.intObject(selectionDuration)));
        this.selectionDuration = selectionDuration;
    }

    public int getCurrentTime() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_27, this, this));
        return this.currentTime;
    }

    public void setCurrentTime(int currentTime) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_28, (Object) this, (Object) this, Conversions.intObject(currentTime)));
        this.currentTime = currentTime;
    }
}
