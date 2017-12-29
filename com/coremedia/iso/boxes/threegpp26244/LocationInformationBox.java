package com.coremedia.iso.boxes.threegpp26244;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.Utf8;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class LocationInformationBox extends AbstractFullBox {
    public static final String TYPE = "loci";
    private static final /* synthetic */ StaticPart ajc$tjp_0 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_1 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_10 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_11 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_12 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_13 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_14 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_15 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_2 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_3 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_4 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_5 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_6 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_7 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_8 = null;
    private static final /* synthetic */ StaticPart ajc$tjp_9 = null;
    private String additionalNotes = TtmlNode.ANONYMOUS_REGION_ID;
    private double altitude;
    private String astronomicalBody = TtmlNode.ANONYMOUS_REGION_ID;
    private String language;
    private double latitude;
    private double longitude;
    private String name = TtmlNode.ANONYMOUS_REGION_ID;
    private int role;

    static {
        ajc$preClinit();
    }

    private static /* synthetic */ void ajc$preClinit() {
        Factory factory = new Factory("LocationInformationBox.java", LocationInformationBox.class);
        ajc$tjp_0 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getLanguage", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 30);
        ajc$tjp_1 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setLanguage", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", "java.lang.String", "language", TtmlNode.ANONYMOUS_REGION_ID, "void"), 34);
        ajc$tjp_10 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAltitude", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "double"), 70);
        ajc$tjp_11 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAltitude", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", "double", "altitude", TtmlNode.ANONYMOUS_REGION_ID, "void"), 74);
        ajc$tjp_12 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAstronomicalBody", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 78);
        ajc$tjp_13 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAstronomicalBody", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", "java.lang.String", "astronomicalBody", TtmlNode.ANONYMOUS_REGION_ID, "void"), 82);
        ajc$tjp_14 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getAdditionalNotes", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 86);
        ajc$tjp_15 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setAdditionalNotes", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", "java.lang.String", "additionalNotes", TtmlNode.ANONYMOUS_REGION_ID, "void"), 90);
        ajc$tjp_2 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getName", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "java.lang.String"), 38);
        ajc$tjp_3 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setName", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", "java.lang.String", "name", TtmlNode.ANONYMOUS_REGION_ID, "void"), 42);
        ajc$tjp_4 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getRole", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "int"), 46);
        ajc$tjp_5 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setRole", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", "int", "role", TtmlNode.ANONYMOUS_REGION_ID, "void"), 50);
        ajc$tjp_6 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getLongitude", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "double"), 54);
        ajc$tjp_7 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setLongitude", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", "double", "longitude", TtmlNode.ANONYMOUS_REGION_ID, "void"), 58);
        ajc$tjp_8 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "getLatitude", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, "double"), 62);
        ajc$tjp_9 = factory.makeSJP("method-execution", factory.makeMethodSig("1", "setLatitude", "com.coremedia.iso.boxes.threegpp26244.LocationInformationBox", "double", "latitude", TtmlNode.ANONYMOUS_REGION_ID, "void"), 66);
    }

    public LocationInformationBox() {
        super(TYPE);
    }

    public String getLanguage() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_0, this, this));
        return this.language;
    }

    public void setLanguage(String language) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_1, (Object) this, (Object) this, (Object) language));
        this.language = language;
    }

    public String getName() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_2, this, this));
        return this.name;
    }

    public void setName(String name) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_3, (Object) this, (Object) this, (Object) name));
        this.name = name;
    }

    public int getRole() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_4, this, this));
        return this.role;
    }

    public void setRole(int role) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_5, (Object) this, (Object) this, Conversions.intObject(role)));
        this.role = role;
    }

    public double getLongitude() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_6, this, this));
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_7, (Object) this, (Object) this, Conversions.doubleObject(longitude)));
        this.longitude = longitude;
    }

    public double getLatitude() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_8, this, this));
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_9, (Object) this, (Object) this, Conversions.doubleObject(latitude)));
        this.latitude = latitude;
    }

    public double getAltitude() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_10, this, this));
        return this.altitude;
    }

    public void setAltitude(double altitude) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_11, (Object) this, (Object) this, Conversions.doubleObject(altitude)));
        this.altitude = altitude;
    }

    public String getAstronomicalBody() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_12, this, this));
        return this.astronomicalBody;
    }

    public void setAstronomicalBody(String astronomicalBody) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_13, (Object) this, (Object) this, (Object) astronomicalBody));
        this.astronomicalBody = astronomicalBody;
    }

    public String getAdditionalNotes() {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_14, this, this));
        return this.additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        RequiresParseDetailAspect.aspectOf().before(Factory.makeJP(ajc$tjp_15, (Object) this, (Object) this, (Object) additionalNotes));
        this.additionalNotes = additionalNotes;
    }

    protected long getContentSize() {
        return (long) (((Utf8.convert(this.name).length + 22) + Utf8.convert(this.astronomicalBody).length) + Utf8.convert(this.additionalNotes).length);
    }

    public void _parseDetails(ByteBuffer content) {
        parseVersionAndFlags(content);
        this.language = IsoTypeReader.readIso639(content);
        this.name = IsoTypeReader.readString(content);
        this.role = IsoTypeReader.readUInt8(content);
        this.longitude = IsoTypeReader.readFixedPoint1616(content);
        this.latitude = IsoTypeReader.readFixedPoint1616(content);
        this.altitude = IsoTypeReader.readFixedPoint1616(content);
        this.astronomicalBody = IsoTypeReader.readString(content);
        this.additionalNotes = IsoTypeReader.readString(content);
    }

    protected void getContent(ByteBuffer byteBuffer) {
        writeVersionAndFlags(byteBuffer);
        IsoTypeWriter.writeIso639(byteBuffer, this.language);
        byteBuffer.put(Utf8.convert(this.name));
        byteBuffer.put((byte) 0);
        IsoTypeWriter.writeUInt8(byteBuffer, this.role);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.longitude);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.latitude);
        IsoTypeWriter.writeFixedPoint1616(byteBuffer, this.altitude);
        byteBuffer.put(Utf8.convert(this.astronomicalBody));
        byteBuffer.put((byte) 0);
        byteBuffer.put(Utf8.convert(this.additionalNotes));
        byteBuffer.put((byte) 0);
    }
}
