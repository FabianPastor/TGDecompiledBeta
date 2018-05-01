package android.support.media;

import android.content.res.AssetManager.AssetInputStream;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class ExifInterface
{
  private static final Charset ASCII;
  public static final int[] BITS_PER_SAMPLE_GREYSCALE_1;
  public static final int[] BITS_PER_SAMPLE_GREYSCALE_2;
  public static final int[] BITS_PER_SAMPLE_RGB;
  private static final byte[] EXIF_ASCII_PREFIX;
  private static final ExifTag[] EXIF_POINTER_TAGS;
  static final ExifTag[][] EXIF_TAGS;
  private static final List<Integer> FLIPPED_ROTATION_ORDER;
  static final byte[] IDENTIFIER_EXIF_APP1;
  private static final ExifTag[] IFD_EXIF_TAGS;
  static final int[] IFD_FORMAT_BYTES_PER_FORMAT;
  static final String[] IFD_FORMAT_NAMES;
  private static final ExifTag[] IFD_GPS_TAGS;
  private static final ExifTag[] IFD_INTEROPERABILITY_TAGS;
  private static final ExifTag[] IFD_THUMBNAIL_TAGS;
  private static final ExifTag[] IFD_TIFF_TAGS;
  private static final ExifTag JPEG_INTERCHANGE_FORMAT_LENGTH_TAG;
  private static final ExifTag JPEG_INTERCHANGE_FORMAT_TAG;
  static final byte[] JPEG_SIGNATURE;
  private static final ExifTag[] ORF_CAMERA_SETTINGS_TAGS;
  private static final ExifTag[] ORF_IMAGE_PROCESSING_TAGS;
  private static final byte[] ORF_MAKER_NOTE_HEADER_1;
  private static final byte[] ORF_MAKER_NOTE_HEADER_2;
  private static final ExifTag[] ORF_MAKER_NOTE_TAGS;
  private static final ExifTag[] PEF_TAGS;
  private static final List<Integer> ROTATION_ORDER = Arrays.asList(new Integer[] { Integer.valueOf(1), Integer.valueOf(6), Integer.valueOf(3), Integer.valueOf(8) });
  private static final ExifTag TAG_RAF_IMAGE_SIZE;
  private static final HashMap<Integer, Integer> sExifPointerTagMap;
  private static final HashMap<Integer, ExifTag>[] sExifTagMapsForReading;
  private static final HashMap<String, ExifTag>[] sExifTagMapsForWriting;
  private static SimpleDateFormat sFormatter;
  private static final Pattern sGpsTimestampPattern = Pattern.compile("^([0-9][0-9]):([0-9][0-9]):([0-9][0-9])$");
  private static final Pattern sNonZeroTimePattern;
  private static final HashSet<String> sTagSetForCompatibility;
  private final AssetManager.AssetInputStream mAssetInputStream;
  private final HashMap<String, ExifAttribute>[] mAttributes;
  private ByteOrder mExifByteOrder;
  private int mExifOffset;
  private final String mFilename;
  private boolean mHasThumbnail;
  private boolean mIsSupportedFile;
  private int mMimeType;
  private int mOrfMakerNoteOffset;
  private int mOrfThumbnailLength;
  private int mOrfThumbnailOffset;
  private int mRw2JpgFromRawOffset;
  private byte[] mThumbnailBytes;
  private int mThumbnailCompression;
  private int mThumbnailLength;
  private int mThumbnailOffset;
  
  static
  {
    FLIPPED_ROTATION_ORDER = Arrays.asList(new Integer[] { Integer.valueOf(2), Integer.valueOf(7), Integer.valueOf(4), Integer.valueOf(5) });
    BITS_PER_SAMPLE_RGB = new int[] { 8, 8, 8 };
    BITS_PER_SAMPLE_GREYSCALE_1 = new int[] { 4 };
    BITS_PER_SAMPLE_GREYSCALE_2 = new int[] { 8 };
    JPEG_SIGNATURE = new byte[] { -1, -40, -1 };
    ORF_MAKER_NOTE_HEADER_1 = new byte[] { 79, 76, 89, 77, 80, 0 };
    ORF_MAKER_NOTE_HEADER_2 = new byte[] { 79, 76, 89, 77, 80, 85, 83, 0, 73, 73 };
    IFD_FORMAT_NAMES = new String[] { "", "BYTE", "STRING", "USHORT", "ULONG", "URATIONAL", "SBYTE", "UNDEFINED", "SSHORT", "SLONG", "SRATIONAL", "SINGLE", "DOUBLE" };
    IFD_FORMAT_BYTES_PER_FORMAT = new int[] { 0, 1, 1, 2, 4, 8, 1, 1, 2, 4, 8, 4, 8, 1 };
    EXIF_ASCII_PREFIX = new byte[] { 65, 83, 67, 73, 73, 0, 0, 0 };
    IFD_TIFF_TAGS = new ExifTag[] { new ExifTag("NewSubfileType", 254, 4, null), new ExifTag("SubfileType", 255, 4, null), new ExifTag("ImageWidth", 256, 3, 4, null), new ExifTag("ImageLength", 257, 3, 4, null), new ExifTag("BitsPerSample", 258, 3, null), new ExifTag("Compression", 259, 3, null), new ExifTag("PhotometricInterpretation", 262, 3, null), new ExifTag("ImageDescription", 270, 2, null), new ExifTag("Make", 271, 2, null), new ExifTag("Model", 272, 2, null), new ExifTag("StripOffsets", 273, 3, 4, null), new ExifTag("Orientation", 274, 3, null), new ExifTag("SamplesPerPixel", 277, 3, null), new ExifTag("RowsPerStrip", 278, 3, 4, null), new ExifTag("StripByteCounts", 279, 3, 4, null), new ExifTag("XResolution", 282, 5, null), new ExifTag("YResolution", 283, 5, null), new ExifTag("PlanarConfiguration", 284, 3, null), new ExifTag("ResolutionUnit", 296, 3, null), new ExifTag("TransferFunction", 301, 3, null), new ExifTag("Software", 305, 2, null), new ExifTag("DateTime", 306, 2, null), new ExifTag("Artist", 315, 2, null), new ExifTag("WhitePoint", 318, 5, null), new ExifTag("PrimaryChromaticities", 319, 5, null), new ExifTag("SubIFDPointer", 330, 4, null), new ExifTag("JPEGInterchangeFormat", 513, 4, null), new ExifTag("JPEGInterchangeFormatLength", 514, 4, null), new ExifTag("YCbCrCoefficients", 529, 5, null), new ExifTag("YCbCrSubSampling", 530, 3, null), new ExifTag("YCbCrPositioning", 531, 3, null), new ExifTag("ReferenceBlackWhite", 532, 5, null), new ExifTag("Copyright", 33432, 2, null), new ExifTag("ExifIFDPointer", 34665, 4, null), new ExifTag("GPSInfoIFDPointer", 34853, 4, null), new ExifTag("SensorTopBorder", 4, 4, null), new ExifTag("SensorLeftBorder", 5, 4, null), new ExifTag("SensorBottomBorder", 6, 4, null), new ExifTag("SensorRightBorder", 7, 4, null), new ExifTag("ISO", 23, 3, null), new ExifTag("JpgFromRaw", 46, 7, null) };
    IFD_EXIF_TAGS = new ExifTag[] { new ExifTag("ExposureTime", 33434, 5, null), new ExifTag("FNumber", 33437, 5, null), new ExifTag("ExposureProgram", 34850, 3, null), new ExifTag("SpectralSensitivity", 34852, 2, null), new ExifTag("PhotographicSensitivity", 34855, 3, null), new ExifTag("OECF", 34856, 7, null), new ExifTag("ExifVersion", 36864, 2, null), new ExifTag("DateTimeOriginal", 36867, 2, null), new ExifTag("DateTimeDigitized", 36868, 2, null), new ExifTag("ComponentsConfiguration", 37121, 7, null), new ExifTag("CompressedBitsPerPixel", 37122, 5, null), new ExifTag("ShutterSpeedValue", 37377, 10, null), new ExifTag("ApertureValue", 37378, 5, null), new ExifTag("BrightnessValue", 37379, 10, null), new ExifTag("ExposureBiasValue", 37380, 10, null), new ExifTag("MaxApertureValue", 37381, 5, null), new ExifTag("SubjectDistance", 37382, 5, null), new ExifTag("MeteringMode", 37383, 3, null), new ExifTag("LightSource", 37384, 3, null), new ExifTag("Flash", 37385, 3, null), new ExifTag("FocalLength", 37386, 5, null), new ExifTag("SubjectArea", 37396, 3, null), new ExifTag("MakerNote", 37500, 7, null), new ExifTag("UserComment", 37510, 7, null), new ExifTag("SubSecTime", 37520, 2, null), new ExifTag("SubSecTimeOriginal", 37521, 2, null), new ExifTag("SubSecTimeDigitized", 37522, 2, null), new ExifTag("FlashpixVersion", 40960, 7, null), new ExifTag("ColorSpace", 40961, 3, null), new ExifTag("PixelXDimension", 40962, 3, 4, null), new ExifTag("PixelYDimension", 40963, 3, 4, null), new ExifTag("RelatedSoundFile", 40964, 2, null), new ExifTag("InteroperabilityIFDPointer", 40965, 4, null), new ExifTag("FlashEnergy", 41483, 5, null), new ExifTag("SpatialFrequencyResponse", 41484, 7, null), new ExifTag("FocalPlaneXResolution", 41486, 5, null), new ExifTag("FocalPlaneYResolution", 41487, 5, null), new ExifTag("FocalPlaneResolutionUnit", 41488, 3, null), new ExifTag("SubjectLocation", 41492, 3, null), new ExifTag("ExposureIndex", 41493, 5, null), new ExifTag("SensingMethod", 41495, 3, null), new ExifTag("FileSource", 41728, 7, null), new ExifTag("SceneType", 41729, 7, null), new ExifTag("CFAPattern", 41730, 7, null), new ExifTag("CustomRendered", 41985, 3, null), new ExifTag("ExposureMode", 41986, 3, null), new ExifTag("WhiteBalance", 41987, 3, null), new ExifTag("DigitalZoomRatio", 41988, 5, null), new ExifTag("FocalLengthIn35mmFilm", 41989, 3, null), new ExifTag("SceneCaptureType", 41990, 3, null), new ExifTag("GainControl", 41991, 3, null), new ExifTag("Contrast", 41992, 3, null), new ExifTag("Saturation", 41993, 3, null), new ExifTag("Sharpness", 41994, 3, null), new ExifTag("DeviceSettingDescription", 41995, 7, null), new ExifTag("SubjectDistanceRange", 41996, 3, null), new ExifTag("ImageUniqueID", 42016, 2, null), new ExifTag("DNGVersion", 50706, 1, null), new ExifTag("DefaultCropSize", 50720, 3, 4, null) };
    IFD_GPS_TAGS = new ExifTag[] { new ExifTag("GPSVersionID", 0, 1, null), new ExifTag("GPSLatitudeRef", 1, 2, null), new ExifTag("GPSLatitude", 2, 5, null), new ExifTag("GPSLongitudeRef", 3, 2, null), new ExifTag("GPSLongitude", 4, 5, null), new ExifTag("GPSAltitudeRef", 5, 1, null), new ExifTag("GPSAltitude", 6, 5, null), new ExifTag("GPSTimeStamp", 7, 5, null), new ExifTag("GPSSatellites", 8, 2, null), new ExifTag("GPSStatus", 9, 2, null), new ExifTag("GPSMeasureMode", 10, 2, null), new ExifTag("GPSDOP", 11, 5, null), new ExifTag("GPSSpeedRef", 12, 2, null), new ExifTag("GPSSpeed", 13, 5, null), new ExifTag("GPSTrackRef", 14, 2, null), new ExifTag("GPSTrack", 15, 5, null), new ExifTag("GPSImgDirectionRef", 16, 2, null), new ExifTag("GPSImgDirection", 17, 5, null), new ExifTag("GPSMapDatum", 18, 2, null), new ExifTag("GPSDestLatitudeRef", 19, 2, null), new ExifTag("GPSDestLatitude", 20, 5, null), new ExifTag("GPSDestLongitudeRef", 21, 2, null), new ExifTag("GPSDestLongitude", 22, 5, null), new ExifTag("GPSDestBearingRef", 23, 2, null), new ExifTag("GPSDestBearing", 24, 5, null), new ExifTag("GPSDestDistanceRef", 25, 2, null), new ExifTag("GPSDestDistance", 26, 5, null), new ExifTag("GPSProcessingMethod", 27, 7, null), new ExifTag("GPSAreaInformation", 28, 7, null), new ExifTag("GPSDateStamp", 29, 2, null), new ExifTag("GPSDifferential", 30, 3, null) };
    IFD_INTEROPERABILITY_TAGS = new ExifTag[] { new ExifTag("InteroperabilityIndex", 1, 2, null) };
    IFD_THUMBNAIL_TAGS = new ExifTag[] { new ExifTag("NewSubfileType", 254, 4, null), new ExifTag("SubfileType", 255, 4, null), new ExifTag("ThumbnailImageWidth", 256, 3, 4, null), new ExifTag("ThumbnailImageLength", 257, 3, 4, null), new ExifTag("BitsPerSample", 258, 3, null), new ExifTag("Compression", 259, 3, null), new ExifTag("PhotometricInterpretation", 262, 3, null), new ExifTag("ImageDescription", 270, 2, null), new ExifTag("Make", 271, 2, null), new ExifTag("Model", 272, 2, null), new ExifTag("StripOffsets", 273, 3, 4, null), new ExifTag("Orientation", 274, 3, null), new ExifTag("SamplesPerPixel", 277, 3, null), new ExifTag("RowsPerStrip", 278, 3, 4, null), new ExifTag("StripByteCounts", 279, 3, 4, null), new ExifTag("XResolution", 282, 5, null), new ExifTag("YResolution", 283, 5, null), new ExifTag("PlanarConfiguration", 284, 3, null), new ExifTag("ResolutionUnit", 296, 3, null), new ExifTag("TransferFunction", 301, 3, null), new ExifTag("Software", 305, 2, null), new ExifTag("DateTime", 306, 2, null), new ExifTag("Artist", 315, 2, null), new ExifTag("WhitePoint", 318, 5, null), new ExifTag("PrimaryChromaticities", 319, 5, null), new ExifTag("SubIFDPointer", 330, 4, null), new ExifTag("JPEGInterchangeFormat", 513, 4, null), new ExifTag("JPEGInterchangeFormatLength", 514, 4, null), new ExifTag("YCbCrCoefficients", 529, 5, null), new ExifTag("YCbCrSubSampling", 530, 3, null), new ExifTag("YCbCrPositioning", 531, 3, null), new ExifTag("ReferenceBlackWhite", 532, 5, null), new ExifTag("Copyright", 33432, 2, null), new ExifTag("ExifIFDPointer", 34665, 4, null), new ExifTag("GPSInfoIFDPointer", 34853, 4, null), new ExifTag("DNGVersion", 50706, 1, null), new ExifTag("DefaultCropSize", 50720, 3, 4, null) };
    TAG_RAF_IMAGE_SIZE = new ExifTag("StripOffsets", 273, 3, null);
    ORF_MAKER_NOTE_TAGS = new ExifTag[] { new ExifTag("ThumbnailImage", 256, 7, null), new ExifTag("CameraSettingsIFDPointer", 8224, 4, null), new ExifTag("ImageProcessingIFDPointer", 8256, 4, null) };
    ORF_CAMERA_SETTINGS_TAGS = new ExifTag[] { new ExifTag("PreviewImageStart", 257, 4, null), new ExifTag("PreviewImageLength", 258, 4, null) };
    ORF_IMAGE_PROCESSING_TAGS = new ExifTag[] { new ExifTag("AspectFrame", 4371, 3, null) };
    PEF_TAGS = new ExifTag[] { new ExifTag("ColorSpace", 55, 3, null) };
    EXIF_TAGS = new ExifTag[][] { IFD_TIFF_TAGS, IFD_EXIF_TAGS, IFD_GPS_TAGS, IFD_INTEROPERABILITY_TAGS, IFD_THUMBNAIL_TAGS, IFD_TIFF_TAGS, ORF_MAKER_NOTE_TAGS, ORF_CAMERA_SETTINGS_TAGS, ORF_IMAGE_PROCESSING_TAGS, PEF_TAGS };
    EXIF_POINTER_TAGS = new ExifTag[] { new ExifTag("SubIFDPointer", 330, 4, null), new ExifTag("ExifIFDPointer", 34665, 4, null), new ExifTag("GPSInfoIFDPointer", 34853, 4, null), new ExifTag("InteroperabilityIFDPointer", 40965, 4, null), new ExifTag("CameraSettingsIFDPointer", 8224, 1, null), new ExifTag("ImageProcessingIFDPointer", 8256, 1, null) };
    JPEG_INTERCHANGE_FORMAT_TAG = new ExifTag("JPEGInterchangeFormat", 513, 4, null);
    JPEG_INTERCHANGE_FORMAT_LENGTH_TAG = new ExifTag("JPEGInterchangeFormatLength", 514, 4, null);
    sExifTagMapsForReading = new HashMap[EXIF_TAGS.length];
    sExifTagMapsForWriting = new HashMap[EXIF_TAGS.length];
    sTagSetForCompatibility = new HashSet(Arrays.asList(new String[] { "FNumber", "DigitalZoomRatio", "ExposureTime", "SubjectDistance", "GPSTimeStamp" }));
    sExifPointerTagMap = new HashMap();
    ASCII = Charset.forName("US-ASCII");
    IDENTIFIER_EXIF_APP1 = "Exif\000\000".getBytes(ASCII);
    sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    sFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    for (int i = 0; i < EXIF_TAGS.length; i++)
    {
      sExifTagMapsForReading[i] = new HashMap();
      sExifTagMapsForWriting[i] = new HashMap();
      for (ExifTag localExifTag : EXIF_TAGS[i])
      {
        sExifTagMapsForReading[i].put(Integer.valueOf(localExifTag.number), localExifTag);
        sExifTagMapsForWriting[i].put(localExifTag.name, localExifTag);
      }
    }
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[0].number), Integer.valueOf(5));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[1].number), Integer.valueOf(1));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[2].number), Integer.valueOf(2));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[3].number), Integer.valueOf(3));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[4].number), Integer.valueOf(7));
    sExifPointerTagMap.put(Integer.valueOf(EXIF_POINTER_TAGS[5].number), Integer.valueOf(8));
    sNonZeroTimePattern = Pattern.compile(".*[1-9].*");
  }
  
  /* Error */
  public ExifInterface(String paramString)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 631	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: getstatic 541	android/support/media/ExifInterface:EXIF_TAGS	[[Landroid/support/media/ExifInterface$ExifTag;
    //   8: arraylength
    //   9: anewarray 549	java/util/HashMap
    //   12: putfield 633	android/support/media/ExifInterface:mAttributes	[Ljava/util/HashMap;
    //   15: aload_0
    //   16: getstatic 638	java/nio/ByteOrder:BIG_ENDIAN	Ljava/nio/ByteOrder;
    //   19: putfield 640	android/support/media/ExifInterface:mExifByteOrder	Ljava/nio/ByteOrder;
    //   22: aload_1
    //   23: ifnonnull +14 -> 37
    //   26: new 642	java/lang/IllegalArgumentException
    //   29: dup
    //   30: ldc_w 644
    //   33: invokespecial 645	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   36: athrow
    //   37: aconst_null
    //   38: astore_2
    //   39: aload_0
    //   40: aconst_null
    //   41: putfield 647	android/support/media/ExifInterface:mAssetInputStream	Landroid/content/res/AssetManager$AssetInputStream;
    //   44: aload_0
    //   45: aload_1
    //   46: putfield 649	android/support/media/ExifInterface:mFilename	Ljava/lang/String;
    //   49: new 651	java/io/FileInputStream
    //   52: astore_3
    //   53: aload_3
    //   54: aload_1
    //   55: invokespecial 652	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   58: aload_0
    //   59: aload_3
    //   60: invokespecial 656	android/support/media/ExifInterface:loadAttributes	(Ljava/io/InputStream;)V
    //   63: aload_3
    //   64: invokestatic 660	android/support/media/ExifInterface:closeQuietly	(Ljava/io/Closeable;)V
    //   67: return
    //   68: astore_1
    //   69: aload_2
    //   70: invokestatic 660	android/support/media/ExifInterface:closeQuietly	(Ljava/io/Closeable;)V
    //   73: aload_1
    //   74: athrow
    //   75: astore_1
    //   76: aload_3
    //   77: astore_2
    //   78: goto -9 -> 69
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	ExifInterface
    //   0	81	1	paramString	String
    //   38	40	2	localObject	Object
    //   52	25	3	localFileInputStream	java.io.FileInputStream
    // Exception table:
    //   from	to	target	type
    //   49	58	68	finally
    //   58	63	75	finally
  }
  
  private void addDefaultValuesForCompatibility()
  {
    String str = getAttribute("DateTimeOriginal");
    if ((str != null) && (getAttribute("DateTime") == null)) {
      this.mAttributes[0].put("DateTime", ExifAttribute.createString(str));
    }
    if (getAttribute("ImageWidth") == null) {
      this.mAttributes[0].put("ImageWidth", ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
    if (getAttribute("ImageLength") == null) {
      this.mAttributes[0].put("ImageLength", ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
    if (getAttribute("Orientation") == null) {
      this.mAttributes[0].put("Orientation", ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
    if (getAttribute("LightSource") == null) {
      this.mAttributes[1].put("LightSource", ExifAttribute.createULong(0L, this.mExifByteOrder));
    }
  }
  
  private static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {}
    try
    {
      paramCloseable.close();
      return;
    }
    catch (RuntimeException paramCloseable)
    {
      throw paramCloseable;
    }
    catch (Exception paramCloseable)
    {
      for (;;) {}
    }
  }
  
  private static long[] convertToLongArray(Object paramObject)
  {
    if ((paramObject instanceof int[]))
    {
      int[] arrayOfInt = (int[])paramObject;
      long[] arrayOfLong = new long[arrayOfInt.length];
      for (int i = 0;; i++)
      {
        paramObject = arrayOfLong;
        if (i >= arrayOfInt.length) {
          break;
        }
        arrayOfLong[i] = arrayOfInt[i];
      }
    }
    if ((paramObject instanceof long[])) {}
    for (paramObject = (long[])paramObject;; paramObject = null) {
      return (long[])paramObject;
    }
  }
  
  private ExifAttribute getExifAttribute(String paramString)
  {
    String str = paramString;
    if ("ISOSpeedRatings".equals(paramString)) {
      str = "PhotographicSensitivity";
    }
    int i = 0;
    if (i < EXIF_TAGS.length)
    {
      paramString = (ExifAttribute)this.mAttributes[i].get(str);
      if (paramString == null) {}
    }
    for (;;)
    {
      return paramString;
      i++;
      break;
      paramString = null;
    }
  }
  
  private void getJpegAttributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt1, int paramInt2)
    throws IOException
  {
    paramByteOrderedDataInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
    paramByteOrderedDataInputStream.seek(paramInt1);
    int i = paramByteOrderedDataInputStream.readByte();
    if (i != -1) {
      throw new IOException("Invalid marker: " + Integer.toHexString(i & 0xFF));
    }
    if (paramByteOrderedDataInputStream.readByte() != -40) {
      throw new IOException("Invalid marker: " + Integer.toHexString(i & 0xFF));
    }
    for (paramInt1 = paramInt1 + 1 + 1;; paramInt1 = i + paramInt1)
    {
      i = paramByteOrderedDataInputStream.readByte();
      if (i != -1) {
        throw new IOException("Invalid marker:" + Integer.toHexString(i & 0xFF));
      }
      i = paramByteOrderedDataInputStream.readByte();
      if ((i == -39) || (i == -38))
      {
        paramByteOrderedDataInputStream.setByteOrder(this.mExifByteOrder);
        return;
      }
      int j = paramByteOrderedDataInputStream.readUnsignedShort() - 2;
      int k = paramInt1 + 1 + 1 + 2;
      if (j < 0) {
        throw new IOException("Invalid length");
      }
      switch (i)
      {
      default: 
        paramInt1 = j;
        i = k;
      }
      while (paramInt1 < 0)
      {
        throw new IOException("Invalid length");
        i = k;
        paramInt1 = j;
        if (j >= 6)
        {
          byte[] arrayOfByte = new byte[6];
          if (paramByteOrderedDataInputStream.read(arrayOfByte) != 6) {
            throw new IOException("Invalid exif");
          }
          k += 6;
          j -= 6;
          i = k;
          paramInt1 = j;
          if (Arrays.equals(arrayOfByte, IDENTIFIER_EXIF_APP1))
          {
            if (j <= 0) {
              throw new IOException("Invalid exif");
            }
            this.mExifOffset = k;
            arrayOfByte = new byte[j];
            if (paramByteOrderedDataInputStream.read(arrayOfByte) != j) {
              throw new IOException("Invalid exif");
            }
            i = k + j;
            paramInt1 = 0;
            readExifSegment(arrayOfByte, paramInt2);
            continue;
            arrayOfByte = new byte[j];
            if (paramByteOrderedDataInputStream.read(arrayOfByte) != j) {
              throw new IOException("Invalid exif");
            }
            j = 0;
            i = k;
            paramInt1 = j;
            if (getAttribute("UserComment") == null)
            {
              this.mAttributes[1].put("UserComment", ExifAttribute.createString(new String(arrayOfByte, ASCII)));
              i = k;
              paramInt1 = j;
              continue;
              if (paramByteOrderedDataInputStream.skipBytes(1) != 1) {
                throw new IOException("Invalid SOFx");
              }
              this.mAttributes[paramInt2].put("ImageLength", ExifAttribute.createULong(paramByteOrderedDataInputStream.readUnsignedShort(), this.mExifByteOrder));
              this.mAttributes[paramInt2].put("ImageWidth", ExifAttribute.createULong(paramByteOrderedDataInputStream.readUnsignedShort(), this.mExifByteOrder));
              paramInt1 = j - 5;
              i = k;
            }
          }
        }
      }
      if (paramByteOrderedDataInputStream.skipBytes(paramInt1) != paramInt1) {
        throw new IOException("Invalid JPEG segment");
      }
    }
  }
  
  private int getMimeType(BufferedInputStream paramBufferedInputStream)
    throws IOException
  {
    paramBufferedInputStream.mark(5000);
    byte[] arrayOfByte = new byte['ᎈ'];
    paramBufferedInputStream.read(arrayOfByte);
    paramBufferedInputStream.reset();
    int i;
    if (isJpegFormat(arrayOfByte)) {
      i = 4;
    }
    for (;;)
    {
      return i;
      if (isRafFormat(arrayOfByte)) {
        i = 9;
      } else if (isOrfFormat(arrayOfByte)) {
        i = 7;
      } else if (isRw2Format(arrayOfByte)) {
        i = 10;
      } else {
        i = 0;
      }
    }
  }
  
  private void getOrfAttributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    getRawAttributes(paramByteOrderedDataInputStream);
    paramByteOrderedDataInputStream = (ExifAttribute)this.mAttributes[1].get("MakerNote");
    Object localObject;
    if (paramByteOrderedDataInputStream != null)
    {
      localObject = new ByteOrderedDataInputStream(paramByteOrderedDataInputStream.bytes);
      ((ByteOrderedDataInputStream)localObject).setByteOrder(this.mExifByteOrder);
      byte[] arrayOfByte = new byte[ORF_MAKER_NOTE_HEADER_1.length];
      ((ByteOrderedDataInputStream)localObject).readFully(arrayOfByte);
      ((ByteOrderedDataInputStream)localObject).seek(0L);
      paramByteOrderedDataInputStream = new byte[ORF_MAKER_NOTE_HEADER_2.length];
      ((ByteOrderedDataInputStream)localObject).readFully(paramByteOrderedDataInputStream);
      if (!Arrays.equals(arrayOfByte, ORF_MAKER_NOTE_HEADER_1)) {
        break label240;
      }
      ((ByteOrderedDataInputStream)localObject).seek(8L);
    }
    label240:
    label260:
    label383:
    for (;;)
    {
      readImageFileDirectory((ByteOrderedDataInputStream)localObject, 6);
      paramByteOrderedDataInputStream = (ExifAttribute)this.mAttributes[7].get("PreviewImageStart");
      localObject = (ExifAttribute)this.mAttributes[7].get("PreviewImageLength");
      if ((paramByteOrderedDataInputStream != null) && (localObject != null))
      {
        this.mAttributes[5].put("JPEGInterchangeFormat", paramByteOrderedDataInputStream);
        this.mAttributes[5].put("JPEGInterchangeFormatLength", localObject);
      }
      paramByteOrderedDataInputStream = (ExifAttribute)this.mAttributes[8].get("AspectFrame");
      if (paramByteOrderedDataInputStream != null)
      {
        paramByteOrderedDataInputStream = (int[])paramByteOrderedDataInputStream.getValue(this.mExifByteOrder);
        if ((paramByteOrderedDataInputStream != null) && (paramByteOrderedDataInputStream.length == 4)) {
          break label260;
        }
        Log.w("ExifInterface", "Invalid aspect frame values. frame=" + Arrays.toString(paramByteOrderedDataInputStream));
      }
      for (;;)
      {
        return;
        if (!Arrays.equals(paramByteOrderedDataInputStream, ORF_MAKER_NOTE_HEADER_2)) {
          break label383;
        }
        ((ByteOrderedDataInputStream)localObject).seek(12L);
        break;
        if ((paramByteOrderedDataInputStream[2] > paramByteOrderedDataInputStream[0]) && (paramByteOrderedDataInputStream[3] > paramByteOrderedDataInputStream[1]))
        {
          int i = paramByteOrderedDataInputStream[2] - paramByteOrderedDataInputStream[0] + 1;
          int j = paramByteOrderedDataInputStream[3] - paramByteOrderedDataInputStream[1] + 1;
          int k = j;
          int m = i;
          if (i < j)
          {
            m = i + j;
            k = m - j;
            m -= k;
          }
          localObject = ExifAttribute.createUShort(m, this.mExifByteOrder);
          paramByteOrderedDataInputStream = ExifAttribute.createUShort(k, this.mExifByteOrder);
          this.mAttributes[0].put("ImageWidth", localObject);
          this.mAttributes[0].put("ImageLength", paramByteOrderedDataInputStream);
        }
      }
    }
  }
  
  private void getRafAttributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    paramByteOrderedDataInputStream.skipBytes(84);
    Object localObject = new byte[4];
    byte[] arrayOfByte = new byte[4];
    paramByteOrderedDataInputStream.read((byte[])localObject);
    paramByteOrderedDataInputStream.skipBytes(4);
    paramByteOrderedDataInputStream.read(arrayOfByte);
    int i = ByteBuffer.wrap((byte[])localObject).getInt();
    int j = ByteBuffer.wrap(arrayOfByte).getInt();
    getJpegAttributes(paramByteOrderedDataInputStream, i, 5);
    paramByteOrderedDataInputStream.seek(j);
    paramByteOrderedDataInputStream.setByteOrder(ByteOrder.BIG_ENDIAN);
    j = paramByteOrderedDataInputStream.readInt();
    for (i = 0;; i++)
    {
      int m;
      if (i < j)
      {
        int k = paramByteOrderedDataInputStream.readUnsignedShort();
        m = paramByteOrderedDataInputStream.readUnsignedShort();
        if (k == TAG_RAF_IMAGE_SIZE.number)
        {
          j = paramByteOrderedDataInputStream.readShort();
          i = paramByteOrderedDataInputStream.readShort();
          localObject = ExifAttribute.createUShort(j, this.mExifByteOrder);
          paramByteOrderedDataInputStream = ExifAttribute.createUShort(i, this.mExifByteOrder);
          this.mAttributes[0].put("ImageLength", localObject);
          this.mAttributes[0].put("ImageWidth", paramByteOrderedDataInputStream);
        }
      }
      else
      {
        return;
      }
      paramByteOrderedDataInputStream.skipBytes(m);
    }
  }
  
  private void getRawAttributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    parseTiffHeaders(paramByteOrderedDataInputStream, paramByteOrderedDataInputStream.available());
    readImageFileDirectory(paramByteOrderedDataInputStream, 0);
    updateImageSizeValues(paramByteOrderedDataInputStream, 0);
    updateImageSizeValues(paramByteOrderedDataInputStream, 5);
    updateImageSizeValues(paramByteOrderedDataInputStream, 4);
    validateImages(paramByteOrderedDataInputStream);
    if (this.mMimeType == 8)
    {
      paramByteOrderedDataInputStream = (ExifAttribute)this.mAttributes[1].get("MakerNote");
      if (paramByteOrderedDataInputStream != null)
      {
        paramByteOrderedDataInputStream = new ByteOrderedDataInputStream(paramByteOrderedDataInputStream.bytes);
        paramByteOrderedDataInputStream.setByteOrder(this.mExifByteOrder);
        paramByteOrderedDataInputStream.seek(6L);
        readImageFileDirectory(paramByteOrderedDataInputStream, 9);
        paramByteOrderedDataInputStream = (ExifAttribute)this.mAttributes[9].get("ColorSpace");
        if (paramByteOrderedDataInputStream != null) {
          this.mAttributes[1].put("ColorSpace", paramByteOrderedDataInputStream);
        }
      }
    }
  }
  
  private void getRw2Attributes(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    getRawAttributes(paramByteOrderedDataInputStream);
    if ((ExifAttribute)this.mAttributes[0].get("JpgFromRaw") != null) {
      getJpegAttributes(paramByteOrderedDataInputStream, this.mRw2JpgFromRawOffset, 5);
    }
    paramByteOrderedDataInputStream = (ExifAttribute)this.mAttributes[0].get("ISO");
    ExifAttribute localExifAttribute = (ExifAttribute)this.mAttributes[1].get("PhotographicSensitivity");
    if ((paramByteOrderedDataInputStream != null) && (localExifAttribute == null)) {
      this.mAttributes[1].put("PhotographicSensitivity", paramByteOrderedDataInputStream);
    }
  }
  
  private void handleThumbnailFromJfif(ByteOrderedDataInputStream paramByteOrderedDataInputStream, HashMap paramHashMap)
    throws IOException
  {
    ExifAttribute localExifAttribute = (ExifAttribute)paramHashMap.get("JPEGInterchangeFormat");
    paramHashMap = (ExifAttribute)paramHashMap.get("JPEGInterchangeFormatLength");
    int i;
    int j;
    int k;
    if ((localExifAttribute != null) && (paramHashMap != null))
    {
      i = localExifAttribute.getIntValue(this.mExifByteOrder);
      j = Math.min(paramHashMap.getIntValue(this.mExifByteOrder), paramByteOrderedDataInputStream.available() - i);
      if ((this.mMimeType != 4) && (this.mMimeType != 9) && (this.mMimeType != 10)) {
        break label157;
      }
      k = i + this.mExifOffset;
    }
    for (;;)
    {
      if ((k > 0) && (j > 0))
      {
        this.mHasThumbnail = true;
        this.mThumbnailOffset = k;
        this.mThumbnailLength = j;
        if ((this.mFilename == null) && (this.mAssetInputStream == null))
        {
          paramHashMap = new byte[j];
          paramByteOrderedDataInputStream.seek(k);
          paramByteOrderedDataInputStream.readFully(paramHashMap);
          this.mThumbnailBytes = paramHashMap;
        }
      }
      return;
      label157:
      k = i;
      if (this.mMimeType == 7) {
        k = i + this.mOrfMakerNoteOffset;
      }
    }
  }
  
  private void handleThumbnailFromStrips(ByteOrderedDataInputStream paramByteOrderedDataInputStream, HashMap paramHashMap)
    throws IOException
  {
    Object localObject1 = (ExifAttribute)paramHashMap.get("StripOffsets");
    Object localObject2 = (ExifAttribute)paramHashMap.get("StripByteCounts");
    if ((localObject1 != null) && (localObject2 != null))
    {
      paramHashMap = convertToLongArray(((ExifAttribute)localObject1).getValue(this.mExifByteOrder));
      localObject2 = convertToLongArray(((ExifAttribute)localObject2).getValue(this.mExifByteOrder));
      if (paramHashMap != null) {
        break label71;
      }
      Log.w("ExifInterface", "stripOffsets should not be null.");
    }
    for (;;)
    {
      return;
      label71:
      if (localObject2 == null)
      {
        Log.w("ExifInterface", "stripByteCounts should not be null.");
      }
      else
      {
        long l = 0L;
        int i = localObject2.length;
        for (int j = 0; j < i; j++) {
          l += localObject2[j];
        }
        byte[] arrayOfByte = new byte[(int)l];
        int k = 0;
        i = 0;
        for (j = 0; j < paramHashMap.length; j++)
        {
          int m = (int)paramHashMap[j];
          int n = (int)localObject2[j];
          m -= k;
          if (m < 0) {
            Log.d("ExifInterface", "Invalid strip offset value");
          }
          paramByteOrderedDataInputStream.seek(m);
          localObject1 = new byte[n];
          paramByteOrderedDataInputStream.read((byte[])localObject1);
          k = k + m + n;
          System.arraycopy(localObject1, 0, arrayOfByte, i, localObject1.length);
          i += localObject1.length;
        }
        this.mHasThumbnail = true;
        this.mThumbnailBytes = arrayOfByte;
        this.mThumbnailLength = arrayOfByte.length;
      }
    }
  }
  
  private static boolean isJpegFormat(byte[] paramArrayOfByte)
    throws IOException
  {
    int i = 0;
    if (i < JPEG_SIGNATURE.length) {
      if (paramArrayOfByte[i] == JPEG_SIGNATURE[i]) {}
    }
    for (boolean bool = false;; bool = true)
    {
      return bool;
      i++;
      break;
    }
  }
  
  private boolean isOrfFormat(byte[] paramArrayOfByte)
    throws IOException
  {
    paramArrayOfByte = new ByteOrderedDataInputStream(paramArrayOfByte);
    this.mExifByteOrder = readByteOrder(paramArrayOfByte);
    paramArrayOfByte.setByteOrder(this.mExifByteOrder);
    int i = paramArrayOfByte.readShort();
    paramArrayOfByte.close();
    if ((i == 20306) || (i == 21330)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean isRafFormat(byte[] paramArrayOfByte)
    throws IOException
  {
    byte[] arrayOfByte = "FUJIFILMCCD-RAW".getBytes(Charset.defaultCharset());
    int i = 0;
    if (i < arrayOfByte.length) {
      if (paramArrayOfByte[i] == arrayOfByte[i]) {}
    }
    for (boolean bool = false;; bool = true)
    {
      return bool;
      i++;
      break;
    }
  }
  
  private boolean isRw2Format(byte[] paramArrayOfByte)
    throws IOException
  {
    paramArrayOfByte = new ByteOrderedDataInputStream(paramArrayOfByte);
    this.mExifByteOrder = readByteOrder(paramArrayOfByte);
    paramArrayOfByte.setByteOrder(this.mExifByteOrder);
    int i = paramArrayOfByte.readShort();
    paramArrayOfByte.close();
    if (i == 85) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private boolean isSupportedDataType(HashMap paramHashMap)
    throws IOException
  {
    Object localObject = (ExifAttribute)paramHashMap.get("BitsPerSample");
    boolean bool;
    if (localObject != null)
    {
      localObject = (int[])((ExifAttribute)localObject).getValue(this.mExifByteOrder);
      if (Arrays.equals(BITS_PER_SAMPLE_RGB, (int[])localObject)) {
        bool = true;
      }
    }
    for (;;)
    {
      return bool;
      if (this.mMimeType == 3)
      {
        paramHashMap = (ExifAttribute)paramHashMap.get("PhotometricInterpretation");
        if (paramHashMap != null)
        {
          int i = paramHashMap.getIntValue(this.mExifByteOrder);
          if (((i == 1) && (Arrays.equals((int[])localObject, BITS_PER_SAMPLE_GREYSCALE_2))) || ((i == 6) && (Arrays.equals((int[])localObject, BITS_PER_SAMPLE_RGB))))
          {
            bool = true;
            continue;
          }
        }
      }
      bool = false;
    }
  }
  
  private boolean isThumbnail(HashMap paramHashMap)
    throws IOException
  {
    ExifAttribute localExifAttribute = (ExifAttribute)paramHashMap.get("ImageLength");
    paramHashMap = (ExifAttribute)paramHashMap.get("ImageWidth");
    if ((localExifAttribute != null) && (paramHashMap != null))
    {
      int i = localExifAttribute.getIntValue(this.mExifByteOrder);
      int j = paramHashMap.getIntValue(this.mExifByteOrder);
      if ((i > 512) || (j > 512)) {}
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  /* Error */
  private void loadAttributes(InputStream paramInputStream)
    throws IOException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: iload_2
    //   3: getstatic 541	android/support/media/ExifInterface:EXIF_TAGS	[[Landroid/support/media/ExifInterface$ExifTag;
    //   6: arraylength
    //   7: if_icmpge +22 -> 29
    //   10: aload_0
    //   11: getfield 633	android/support/media/ExifInterface:mAttributes	[Ljava/util/HashMap;
    //   14: iload_2
    //   15: new 549	java/util/HashMap
    //   18: dup
    //   19: invokespecial 562	java/util/HashMap:<init>	()V
    //   22: aastore
    //   23: iinc 2 1
    //   26: goto -24 -> 2
    //   29: new 773	java/io/BufferedInputStream
    //   32: dup
    //   33: aload_1
    //   34: sipush 5000
    //   37: invokespecial 934	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;I)V
    //   40: astore_1
    //   41: aload_0
    //   42: aload_0
    //   43: aload_1
    //   44: checkcast 773	java/io/BufferedInputStream
    //   47: invokespecial 936	android/support/media/ExifInterface:getMimeType	(Ljava/io/BufferedInputStream;)I
    //   50: putfield 870	android/support/media/ExifInterface:mMimeType	I
    //   53: new 8	android/support/media/ExifInterface$ByteOrderedDataInputStream
    //   56: astore_3
    //   57: aload_3
    //   58: aload_1
    //   59: invokespecial 938	android/support/media/ExifInterface$ByteOrderedDataInputStream:<init>	(Ljava/io/InputStream;)V
    //   62: aload_0
    //   63: getfield 870	android/support/media/ExifInterface:mMimeType	I
    //   66: tableswitch	default:+62->128, 0:+131->197, 1:+131->197, 2:+131->197, 3:+131->197, 4:+77->143, 5:+131->197, 6:+131->197, 7:+115->181, 8:+131->197, 9:+100->166, 10:+123->189, 11:+131->197
    //   128: aload_0
    //   129: aload_3
    //   130: invokespecial 941	android/support/media/ExifInterface:setThumbnailData	(Landroid/support/media/ExifInterface$ByteOrderedDataInputStream;)V
    //   133: aload_0
    //   134: iconst_1
    //   135: putfield 943	android/support/media/ExifInterface:mIsSupportedFile	Z
    //   138: aload_0
    //   139: invokespecial 945	android/support/media/ExifInterface:addDefaultValuesForCompatibility	()V
    //   142: return
    //   143: aload_0
    //   144: aload_3
    //   145: iconst_0
    //   146: iconst_0
    //   147: invokespecial 849	android/support/media/ExifInterface:getJpegAttributes	(Landroid/support/media/ExifInterface$ByteOrderedDataInputStream;II)V
    //   150: goto -22 -> 128
    //   153: astore_1
    //   154: aload_0
    //   155: iconst_0
    //   156: putfield 943	android/support/media/ExifInterface:mIsSupportedFile	Z
    //   159: aload_0
    //   160: invokespecial 945	android/support/media/ExifInterface:addDefaultValuesForCompatibility	()V
    //   163: goto -21 -> 142
    //   166: aload_0
    //   167: aload_3
    //   168: invokespecial 947	android/support/media/ExifInterface:getRafAttributes	(Landroid/support/media/ExifInterface$ByteOrderedDataInputStream;)V
    //   171: goto -43 -> 128
    //   174: astore_1
    //   175: aload_0
    //   176: invokespecial 945	android/support/media/ExifInterface:addDefaultValuesForCompatibility	()V
    //   179: aload_1
    //   180: athrow
    //   181: aload_0
    //   182: aload_3
    //   183: invokespecial 949	android/support/media/ExifInterface:getOrfAttributes	(Landroid/support/media/ExifInterface$ByteOrderedDataInputStream;)V
    //   186: goto -58 -> 128
    //   189: aload_0
    //   190: aload_3
    //   191: invokespecial 951	android/support/media/ExifInterface:getRw2Attributes	(Landroid/support/media/ExifInterface$ByteOrderedDataInputStream;)V
    //   194: goto -66 -> 128
    //   197: aload_0
    //   198: aload_3
    //   199: invokespecial 799	android/support/media/ExifInterface:getRawAttributes	(Landroid/support/media/ExifInterface$ByteOrderedDataInputStream;)V
    //   202: goto -74 -> 128
    //   205: astore_1
    //   206: goto -31 -> 175
    //   209: astore_1
    //   210: goto -56 -> 154
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	213	0	this	ExifInterface
    //   0	213	1	paramInputStream	InputStream
    //   1	23	2	i	int
    //   56	143	3	localByteOrderedDataInputStream	ByteOrderedDataInputStream
    // Exception table:
    //   from	to	target	type
    //   41	128	153	java/io/IOException
    //   128	138	153	java/io/IOException
    //   143	150	153	java/io/IOException
    //   166	171	153	java/io/IOException
    //   181	186	153	java/io/IOException
    //   189	194	153	java/io/IOException
    //   197	202	153	java/io/IOException
    //   41	128	174	finally
    //   128	138	174	finally
    //   143	150	174	finally
    //   166	171	174	finally
    //   181	186	174	finally
    //   189	194	174	finally
    //   197	202	174	finally
    //   2	23	205	finally
    //   29	41	205	finally
    //   154	159	205	finally
    //   2	23	209	java/io/IOException
    //   29	41	209	java/io/IOException
  }
  
  private void parseTiffHeaders(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt)
    throws IOException
  {
    this.mExifByteOrder = readByteOrder(paramByteOrderedDataInputStream);
    paramByteOrderedDataInputStream.setByteOrder(this.mExifByteOrder);
    int i = paramByteOrderedDataInputStream.readUnsignedShort();
    if ((this.mMimeType != 7) && (this.mMimeType != 10) && (i != 42)) {
      throw new IOException("Invalid start code: " + Integer.toHexString(i));
    }
    i = paramByteOrderedDataInputStream.readInt();
    if ((i < 8) || (i >= paramInt)) {
      throw new IOException("Invalid first Ifd offset: " + i);
    }
    paramInt = i - 8;
    if ((paramInt > 0) && (paramByteOrderedDataInputStream.skipBytes(paramInt) != paramInt)) {
      throw new IOException("Couldn't jump to first Ifd: " + paramInt);
    }
  }
  
  private ByteOrder readByteOrder(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    int i = paramByteOrderedDataInputStream.readShort();
    switch (i)
    {
    default: 
      throw new IOException("Invalid byte order: " + Integer.toHexString(i));
    }
    for (paramByteOrderedDataInputStream = ByteOrder.LITTLE_ENDIAN;; paramByteOrderedDataInputStream = ByteOrder.BIG_ENDIAN) {
      return paramByteOrderedDataInputStream;
    }
  }
  
  private void readExifSegment(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    ByteOrderedDataInputStream localByteOrderedDataInputStream = new ByteOrderedDataInputStream(paramArrayOfByte);
    parseTiffHeaders(localByteOrderedDataInputStream, paramArrayOfByte.length);
    readImageFileDirectory(localByteOrderedDataInputStream, paramInt);
  }
  
  private void readImageFileDirectory(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt)
    throws IOException
  {
    if (paramByteOrderedDataInputStream.mPosition + 2 > paramByteOrderedDataInputStream.mLength) {}
    for (;;)
    {
      return;
      int i = paramByteOrderedDataInputStream.readShort();
      if (paramByteOrderedDataInputStream.mPosition + i * 12 <= paramByteOrderedDataInputStream.mLength)
      {
        int j = 0;
        if (j < i)
        {
          int k = paramByteOrderedDataInputStream.readUnsignedShort();
          int m = paramByteOrderedDataInputStream.readUnsignedShort();
          int n = paramByteOrderedDataInputStream.readInt();
          long l1 = paramByteOrderedDataInputStream.peek() + 4L;
          ExifTag localExifTag = (ExifTag)sExifTagMapsForReading[paramInt].get(Integer.valueOf(k));
          long l2 = 0L;
          int i1 = 0;
          int i2;
          if (localExifTag == null)
          {
            Log.w("ExifInterface", "Skip the tag entry since tag number is not defined: " + k);
            i2 = m;
            label134:
            if (i1 != 0) {
              break label351;
            }
            paramByteOrderedDataInputStream.seek(l1);
          }
          for (;;)
          {
            j = (short)(j + 1);
            break;
            if ((m <= 0) || (m >= IFD_FORMAT_BYTES_PER_FORMAT.length))
            {
              Log.w("ExifInterface", "Skip the tag entry since data format is invalid: " + m);
              i2 = m;
              break label134;
            }
            if (!localExifTag.isFormatCompatible(m))
            {
              Log.w("ExifInterface", "Skip the tag entry since data format (" + IFD_FORMAT_NAMES[m] + ") is unexpected for tag: " + localExifTag.name);
              i2 = m;
              break label134;
            }
            i2 = m;
            if (m == 7) {
              i2 = localExifTag.primaryFormat;
            }
            l2 = n * IFD_FORMAT_BYTES_PER_FORMAT[i2];
            if ((l2 < 0L) || (l2 > 2147483647L))
            {
              Log.w("ExifInterface", "Skip the tag entry since the number of components is invalid: " + n);
              break label134;
            }
            i1 = 1;
            break label134;
            label351:
            if (l2 > 4L)
            {
              m = paramByteOrderedDataInputStream.readInt();
              if (this.mMimeType != 7) {
                break label671;
              }
              if ("MakerNote".equals(localExifTag.name))
              {
                this.mOrfMakerNoteOffset = m;
                label395:
                if (m + l2 > paramByteOrderedDataInputStream.mLength) {
                  break label703;
                }
                paramByteOrderedDataInputStream.seek(m);
              }
            }
            else
            {
              localObject = (Integer)sExifPointerTagMap.get(Integer.valueOf(k));
              if (localObject == null) {
                break label810;
              }
              long l3 = -1L;
              l2 = l3;
              switch (i2)
              {
              default: 
                l2 = l3;
              case 5: 
              case 6: 
              case 7: 
              case 10: 
              case 11: 
              case 12: 
                label512:
                if ((l2 > 0L) && (l2 < paramByteOrderedDataInputStream.mLength))
                {
                  paramByteOrderedDataInputStream.seek(l2);
                  readImageFileDirectory(paramByteOrderedDataInputStream, ((Integer)localObject).intValue());
                }
                break;
              }
            }
            for (;;)
            {
              paramByteOrderedDataInputStream.seek(l1);
              break;
              if ((paramInt != 6) || (!"ThumbnailImage".equals(localExifTag.name))) {
                break label395;
              }
              this.mOrfThumbnailOffset = m;
              this.mOrfThumbnailLength = n;
              ExifAttribute localExifAttribute1 = ExifAttribute.createUShort(6, this.mExifByteOrder);
              localObject = ExifAttribute.createULong(this.mOrfThumbnailOffset, this.mExifByteOrder);
              ExifAttribute localExifAttribute2 = ExifAttribute.createULong(this.mOrfThumbnailLength, this.mExifByteOrder);
              this.mAttributes[4].put("Compression", localExifAttribute1);
              this.mAttributes[4].put("JPEGInterchangeFormat", localObject);
              this.mAttributes[4].put("JPEGInterchangeFormatLength", localExifAttribute2);
              break label395;
              label671:
              if ((this.mMimeType != 10) || (!"JpgFromRaw".equals(localExifTag.name))) {
                break label395;
              }
              this.mRw2JpgFromRawOffset = m;
              break label395;
              label703:
              Log.w("ExifInterface", "Skip the tag entry since data offset is invalid: " + m);
              paramByteOrderedDataInputStream.seek(l1);
              break;
              l2 = paramByteOrderedDataInputStream.readUnsignedShort();
              break label512;
              l2 = paramByteOrderedDataInputStream.readShort();
              break label512;
              l2 = paramByteOrderedDataInputStream.readUnsignedInt();
              break label512;
              l2 = paramByteOrderedDataInputStream.readInt();
              break label512;
              Log.w("ExifInterface", "Skip jump into the IFD since its offset is invalid: " + l2);
            }
            label810:
            Object localObject = new byte[(int)l2];
            paramByteOrderedDataInputStream.readFully((byte[])localObject);
            localObject = new ExifAttribute(i2, n, (byte[])localObject, null);
            this.mAttributes[paramInt].put(localExifTag.name, localObject);
            if ("DNGVersion".equals(localExifTag.name)) {
              this.mMimeType = 3;
            }
            if (((!"Make".equals(localExifTag.name)) && (!"Model".equals(localExifTag.name))) || ((((ExifAttribute)localObject).getStringValue(this.mExifByteOrder).contains("PENTAX")) || (("Compression".equals(localExifTag.name)) && (((ExifAttribute)localObject).getIntValue(this.mExifByteOrder) == 65535)))) {
              this.mMimeType = 8;
            }
            if (paramByteOrderedDataInputStream.peek() != l1) {
              paramByteOrderedDataInputStream.seek(l1);
            }
          }
        }
        if (paramByteOrderedDataInputStream.peek() + 4 <= paramByteOrderedDataInputStream.mLength)
        {
          paramInt = paramByteOrderedDataInputStream.readInt();
          if ((paramInt > 8) && (paramInt < paramByteOrderedDataInputStream.mLength))
          {
            paramByteOrderedDataInputStream.seek(paramInt);
            if (this.mAttributes[4].isEmpty()) {
              readImageFileDirectory(paramByteOrderedDataInputStream, 4);
            } else if (this.mAttributes[5].isEmpty()) {
              readImageFileDirectory(paramByteOrderedDataInputStream, 5);
            }
          }
        }
      }
    }
  }
  
  private void retrieveJpegImageSize(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt)
    throws IOException
  {
    ExifAttribute localExifAttribute1 = (ExifAttribute)this.mAttributes[paramInt].get("ImageLength");
    ExifAttribute localExifAttribute2 = (ExifAttribute)this.mAttributes[paramInt].get("ImageWidth");
    if ((localExifAttribute1 == null) || (localExifAttribute2 == null))
    {
      localExifAttribute1 = (ExifAttribute)this.mAttributes[paramInt].get("JPEGInterchangeFormat");
      if (localExifAttribute1 != null) {
        getJpegAttributes(paramByteOrderedDataInputStream, localExifAttribute1.getIntValue(this.mExifByteOrder), paramInt);
      }
    }
  }
  
  private void setThumbnailData(ByteOrderedDataInputStream paramByteOrderedDataInputStream)
    throws IOException
  {
    HashMap localHashMap = this.mAttributes[4];
    ExifAttribute localExifAttribute = (ExifAttribute)localHashMap.get("Compression");
    if (localExifAttribute != null)
    {
      this.mThumbnailCompression = localExifAttribute.getIntValue(this.mExifByteOrder);
      switch (this.mThumbnailCompression)
      {
      }
    }
    for (;;)
    {
      return;
      handleThumbnailFromJfif(paramByteOrderedDataInputStream, localHashMap);
      continue;
      if (isSupportedDataType(localHashMap))
      {
        handleThumbnailFromStrips(paramByteOrderedDataInputStream, localHashMap);
        continue;
        this.mThumbnailCompression = 6;
        handleThumbnailFromJfif(paramByteOrderedDataInputStream, localHashMap);
      }
    }
  }
  
  private void swapBasedOnImageSize(int paramInt1, int paramInt2)
    throws IOException
  {
    if ((this.mAttributes[paramInt1].isEmpty()) || (this.mAttributes[paramInt2].isEmpty())) {}
    for (;;)
    {
      return;
      ExifAttribute localExifAttribute1 = (ExifAttribute)this.mAttributes[paramInt1].get("ImageLength");
      ExifAttribute localExifAttribute2 = (ExifAttribute)this.mAttributes[paramInt1].get("ImageWidth");
      ExifAttribute localExifAttribute3 = (ExifAttribute)this.mAttributes[paramInt2].get("ImageLength");
      Object localObject = (ExifAttribute)this.mAttributes[paramInt2].get("ImageWidth");
      if ((localExifAttribute1 != null) && (localExifAttribute2 != null) && (localExifAttribute3 != null) && (localObject != null))
      {
        int i = localExifAttribute1.getIntValue(this.mExifByteOrder);
        int j = localExifAttribute2.getIntValue(this.mExifByteOrder);
        int k = localExifAttribute3.getIntValue(this.mExifByteOrder);
        int m = ((ExifAttribute)localObject).getIntValue(this.mExifByteOrder);
        if ((i < k) && (j < m))
        {
          localObject = this.mAttributes[paramInt1];
          this.mAttributes[paramInt1] = this.mAttributes[paramInt2];
          this.mAttributes[paramInt2] = localObject;
        }
      }
    }
  }
  
  private void updateImageSizeValues(ByteOrderedDataInputStream paramByteOrderedDataInputStream, int paramInt)
    throws IOException
  {
    ExifAttribute localExifAttribute1 = (ExifAttribute)this.mAttributes[paramInt].get("DefaultCropSize");
    ExifAttribute localExifAttribute2 = (ExifAttribute)this.mAttributes[paramInt].get("SensorTopBorder");
    ExifAttribute localExifAttribute3 = (ExifAttribute)this.mAttributes[paramInt].get("SensorLeftBorder");
    ExifAttribute localExifAttribute4 = (ExifAttribute)this.mAttributes[paramInt].get("SensorBottomBorder");
    ExifAttribute localExifAttribute5 = (ExifAttribute)this.mAttributes[paramInt].get("SensorRightBorder");
    if (localExifAttribute1 != null) {
      if (localExifAttribute1.format == 5)
      {
        paramByteOrderedDataInputStream = (Rational[])localExifAttribute1.getValue(this.mExifByteOrder);
        if ((paramByteOrderedDataInputStream == null) || (paramByteOrderedDataInputStream.length != 2)) {
          Log.w("ExifInterface", "Invalid crop size values. cropSize=" + Arrays.toString(paramByteOrderedDataInputStream));
        }
      }
    }
    for (;;)
    {
      return;
      localExifAttribute4 = ExifAttribute.createURational(paramByteOrderedDataInputStream[0], this.mExifByteOrder);
      for (paramByteOrderedDataInputStream = ExifAttribute.createURational(paramByteOrderedDataInputStream[1], this.mExifByteOrder);; paramByteOrderedDataInputStream = ExifAttribute.createUShort(paramByteOrderedDataInputStream[1], this.mExifByteOrder))
      {
        this.mAttributes[paramInt].put("ImageWidth", localExifAttribute4);
        this.mAttributes[paramInt].put("ImageLength", paramByteOrderedDataInputStream);
        break;
        paramByteOrderedDataInputStream = (int[])localExifAttribute1.getValue(this.mExifByteOrder);
        if ((paramByteOrderedDataInputStream == null) || (paramByteOrderedDataInputStream.length != 2))
        {
          Log.w("ExifInterface", "Invalid crop size values. cropSize=" + Arrays.toString(paramByteOrderedDataInputStream));
          break;
        }
        localExifAttribute4 = ExifAttribute.createUShort(paramByteOrderedDataInputStream[0], this.mExifByteOrder);
      }
      if ((localExifAttribute2 != null) && (localExifAttribute3 != null) && (localExifAttribute4 != null) && (localExifAttribute5 != null))
      {
        int i = localExifAttribute2.getIntValue(this.mExifByteOrder);
        int j = localExifAttribute4.getIntValue(this.mExifByteOrder);
        int k = localExifAttribute5.getIntValue(this.mExifByteOrder);
        int m = localExifAttribute3.getIntValue(this.mExifByteOrder);
        if ((j > i) && (k > m))
        {
          localExifAttribute4 = ExifAttribute.createUShort(j - i, this.mExifByteOrder);
          paramByteOrderedDataInputStream = ExifAttribute.createUShort(k - m, this.mExifByteOrder);
          this.mAttributes[paramInt].put("ImageLength", localExifAttribute4);
          this.mAttributes[paramInt].put("ImageWidth", paramByteOrderedDataInputStream);
        }
      }
      else
      {
        retrieveJpegImageSize(paramByteOrderedDataInputStream, paramInt);
      }
    }
  }
  
  private void validateImages(InputStream paramInputStream)
    throws IOException
  {
    swapBasedOnImageSize(0, 5);
    swapBasedOnImageSize(0, 4);
    swapBasedOnImageSize(5, 4);
    paramInputStream = (ExifAttribute)this.mAttributes[1].get("PixelXDimension");
    ExifAttribute localExifAttribute = (ExifAttribute)this.mAttributes[1].get("PixelYDimension");
    if ((paramInputStream != null) && (localExifAttribute != null))
    {
      this.mAttributes[0].put("ImageWidth", paramInputStream);
      this.mAttributes[0].put("ImageLength", localExifAttribute);
    }
    if ((this.mAttributes[4].isEmpty()) && (isThumbnail(this.mAttributes[5])))
    {
      this.mAttributes[4] = this.mAttributes[5];
      this.mAttributes[5] = new HashMap();
    }
    if (!isThumbnail(this.mAttributes[4])) {
      Log.d("ExifInterface", "No image meets the size requirements of a thumbnail image.");
    }
  }
  
  public String getAttribute(String paramString)
  {
    ExifAttribute localExifAttribute = getExifAttribute(paramString);
    if (localExifAttribute != null) {
      if (!sTagSetForCompatibility.contains(paramString)) {
        paramString = localExifAttribute.getStringValue(this.mExifByteOrder);
      }
    }
    for (;;)
    {
      return paramString;
      if (paramString.equals("GPSTimeStamp"))
      {
        if ((localExifAttribute.format != 5) && (localExifAttribute.format != 10))
        {
          Log.w("ExifInterface", "GPS Timestamp format is not rational. format=" + localExifAttribute.format);
          paramString = null;
        }
        else
        {
          paramString = (Rational[])localExifAttribute.getValue(this.mExifByteOrder);
          if ((paramString == null) || (paramString.length != 3))
          {
            Log.w("ExifInterface", "Invalid GPS Timestamp array. array=" + Arrays.toString(paramString));
            paramString = null;
          }
          else
          {
            paramString = String.format("%02d:%02d:%02d", new Object[] { Integer.valueOf((int)((float)paramString[0].numerator / (float)paramString[0].denominator)), Integer.valueOf((int)((float)paramString[1].numerator / (float)paramString[1].denominator)), Integer.valueOf((int)((float)paramString[2].numerator / (float)paramString[2].denominator)) });
          }
        }
      }
      else
      {
        try
        {
          paramString = Double.toString(localExifAttribute.getDoubleValue(this.mExifByteOrder));
        }
        catch (NumberFormatException paramString)
        {
          paramString = null;
        }
        continue;
        paramString = null;
      }
    }
  }
  
  public int getAttributeInt(String paramString, int paramInt)
  {
    paramString = getExifAttribute(paramString);
    if (paramString == null) {}
    for (;;)
    {
      return paramInt;
      try
      {
        int i = paramString.getIntValue(this.mExifByteOrder);
        paramInt = i;
      }
      catch (NumberFormatException paramString) {}
    }
  }
  
  private static class ByteOrderedDataInputStream
    extends InputStream
    implements DataInput
  {
    private static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
    private static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
    private ByteOrder mByteOrder = ByteOrder.BIG_ENDIAN;
    private DataInputStream mDataInputStream;
    private final int mLength;
    private int mPosition;
    
    public ByteOrderedDataInputStream(InputStream paramInputStream)
      throws IOException
    {
      this.mDataInputStream = new DataInputStream(paramInputStream);
      this.mLength = this.mDataInputStream.available();
      this.mPosition = 0;
      this.mDataInputStream.mark(this.mLength);
    }
    
    public ByteOrderedDataInputStream(byte[] paramArrayOfByte)
      throws IOException
    {
      this(new ByteArrayInputStream(paramArrayOfByte));
    }
    
    public int available()
      throws IOException
    {
      return this.mDataInputStream.available();
    }
    
    public int peek()
    {
      return this.mPosition;
    }
    
    public int read()
      throws IOException
    {
      this.mPosition += 1;
      return this.mDataInputStream.read();
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      paramInt1 = this.mDataInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
      this.mPosition += paramInt1;
      return paramInt1;
    }
    
    public boolean readBoolean()
      throws IOException
    {
      this.mPosition += 1;
      return this.mDataInputStream.readBoolean();
    }
    
    public byte readByte()
      throws IOException
    {
      this.mPosition += 1;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = this.mDataInputStream.read();
      if (i < 0) {
        throw new EOFException();
      }
      return (byte)i;
    }
    
    public char readChar()
      throws IOException
    {
      this.mPosition += 2;
      return this.mDataInputStream.readChar();
    }
    
    public double readDouble()
      throws IOException
    {
      return Double.longBitsToDouble(readLong());
    }
    
    public float readFloat()
      throws IOException
    {
      return Float.intBitsToFloat(readInt());
    }
    
    public void readFully(byte[] paramArrayOfByte)
      throws IOException
    {
      this.mPosition += paramArrayOfByte.length;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      if (this.mDataInputStream.read(paramArrayOfByte, 0, paramArrayOfByte.length) != paramArrayOfByte.length) {
        throw new IOException("Couldn't read up to the length of buffer");
      }
    }
    
    public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      this.mPosition += paramInt2;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      if (this.mDataInputStream.read(paramArrayOfByte, paramInt1, paramInt2) != paramInt2) {
        throw new IOException("Couldn't read up to the length of buffer");
      }
    }
    
    public int readInt()
      throws IOException
    {
      this.mPosition += 4;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = this.mDataInputStream.read();
      int j = this.mDataInputStream.read();
      int k = this.mDataInputStream.read();
      int m = this.mDataInputStream.read();
      if ((i | j | k | m) < 0) {
        throw new EOFException();
      }
      if (this.mByteOrder == LITTLE_ENDIAN) {}
      for (k = (m << 24) + (k << 16) + (j << 8) + i;; k = (i << 24) + (j << 16) + (k << 8) + m)
      {
        return k;
        if (this.mByteOrder != BIG_ENDIAN) {
          break;
        }
      }
      throw new IOException("Invalid byte order: " + this.mByteOrder);
    }
    
    public String readLine()
      throws IOException
    {
      Log.d("ExifInterface", "Currently unsupported");
      return null;
    }
    
    public long readLong()
      throws IOException
    {
      this.mPosition += 8;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = this.mDataInputStream.read();
      int j = this.mDataInputStream.read();
      int k = this.mDataInputStream.read();
      int m = this.mDataInputStream.read();
      int n = this.mDataInputStream.read();
      int i1 = this.mDataInputStream.read();
      int i2 = this.mDataInputStream.read();
      int i3 = this.mDataInputStream.read();
      if ((i | j | k | m | n | i1 | i2 | i3) < 0) {
        throw new EOFException();
      }
      if (this.mByteOrder == LITTLE_ENDIAN) {}
      for (long l = (i3 << 56) + (i2 << 48) + (i1 << 40) + (n << 32) + (m << 24) + (k << 16) + (j << 8) + i;; l = (i << 56) + (j << 48) + (k << 40) + (m << 32) + (n << 24) + (i1 << 16) + (i2 << 8) + i3)
      {
        return l;
        if (this.mByteOrder != BIG_ENDIAN) {
          break;
        }
      }
      throw new IOException("Invalid byte order: " + this.mByteOrder);
    }
    
    public short readShort()
      throws IOException
    {
      this.mPosition += 2;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = this.mDataInputStream.read();
      int j = this.mDataInputStream.read();
      if ((i | j) < 0) {
        throw new EOFException();
      }
      if (this.mByteOrder == LITTLE_ENDIAN) {
        j = (short)((j << 8) + i);
      }
      for (int k = j;; k = j)
      {
        return k;
        if (this.mByteOrder != BIG_ENDIAN) {
          break;
        }
        j = (short)((i << 8) + j);
      }
      throw new IOException("Invalid byte order: " + this.mByteOrder);
    }
    
    public String readUTF()
      throws IOException
    {
      this.mPosition += 2;
      return this.mDataInputStream.readUTF();
    }
    
    public int readUnsignedByte()
      throws IOException
    {
      this.mPosition += 1;
      return this.mDataInputStream.readUnsignedByte();
    }
    
    public long readUnsignedInt()
      throws IOException
    {
      return readInt() & 0xFFFFFFFF;
    }
    
    public int readUnsignedShort()
      throws IOException
    {
      this.mPosition += 2;
      if (this.mPosition > this.mLength) {
        throw new EOFException();
      }
      int i = this.mDataInputStream.read();
      int j = this.mDataInputStream.read();
      if ((i | j) < 0) {
        throw new EOFException();
      }
      if (this.mByteOrder == LITTLE_ENDIAN) {}
      for (j = (j << 8) + i;; j = (i << 8) + j)
      {
        return j;
        if (this.mByteOrder != BIG_ENDIAN) {
          break;
        }
      }
      throw new IOException("Invalid byte order: " + this.mByteOrder);
    }
    
    public void seek(long paramLong)
      throws IOException
    {
      if (this.mPosition > paramLong)
      {
        this.mPosition = 0;
        this.mDataInputStream.reset();
        this.mDataInputStream.mark(this.mLength);
      }
      while (skipBytes((int)paramLong) != (int)paramLong)
      {
        throw new IOException("Couldn't seek up to the byteCount");
        paramLong -= this.mPosition;
      }
    }
    
    public void setByteOrder(ByteOrder paramByteOrder)
    {
      this.mByteOrder = paramByteOrder;
    }
    
    public int skipBytes(int paramInt)
      throws IOException
    {
      int i = Math.min(paramInt, this.mLength - this.mPosition);
      paramInt = 0;
      while (paramInt < i) {
        paramInt += this.mDataInputStream.skipBytes(i - paramInt);
      }
      this.mPosition += paramInt;
      return paramInt;
    }
  }
  
  private static class ExifAttribute
  {
    public final byte[] bytes;
    public final int format;
    public final int numberOfComponents;
    
    private ExifAttribute(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
      this.format = paramInt1;
      this.numberOfComponents = paramInt2;
      this.bytes = paramArrayOfByte;
    }
    
    public static ExifAttribute createString(String paramString)
    {
      paramString = (paramString + '\000').getBytes(ExifInterface.ASCII);
      return new ExifAttribute(2, paramString.length, paramString);
    }
    
    public static ExifAttribute createULong(long paramLong, ByteOrder paramByteOrder)
    {
      return createULong(new long[] { paramLong }, paramByteOrder);
    }
    
    public static ExifAttribute createULong(long[] paramArrayOfLong, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[4] * paramArrayOfLong.length]);
      localByteBuffer.order(paramByteOrder);
      int i = paramArrayOfLong.length;
      for (int j = 0; j < i; j++) {
        localByteBuffer.putInt((int)paramArrayOfLong[j]);
      }
      return new ExifAttribute(4, paramArrayOfLong.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createURational(ExifInterface.Rational paramRational, ByteOrder paramByteOrder)
    {
      return createURational(new ExifInterface.Rational[] { paramRational }, paramByteOrder);
    }
    
    public static ExifAttribute createURational(ExifInterface.Rational[] paramArrayOfRational, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[5] * paramArrayOfRational.length]);
      localByteBuffer.order(paramByteOrder);
      int i = paramArrayOfRational.length;
      for (int j = 0; j < i; j++)
      {
        paramByteOrder = paramArrayOfRational[j];
        localByteBuffer.putInt((int)paramByteOrder.numerator);
        localByteBuffer.putInt((int)paramByteOrder.denominator);
      }
      return new ExifAttribute(5, paramArrayOfRational.length, localByteBuffer.array());
    }
    
    public static ExifAttribute createUShort(int paramInt, ByteOrder paramByteOrder)
    {
      return createUShort(new int[] { paramInt }, paramByteOrder);
    }
    
    public static ExifAttribute createUShort(int[] paramArrayOfInt, ByteOrder paramByteOrder)
    {
      ByteBuffer localByteBuffer = ByteBuffer.wrap(new byte[ExifInterface.IFD_FORMAT_BYTES_PER_FORMAT[3] * paramArrayOfInt.length]);
      localByteBuffer.order(paramByteOrder);
      int i = paramArrayOfInt.length;
      for (int j = 0; j < i; j++) {
        localByteBuffer.putShort((short)paramArrayOfInt[j]);
      }
      return new ExifAttribute(3, paramArrayOfInt.length, localByteBuffer.array());
    }
    
    /* Error */
    private Object getValue(ByteOrder paramByteOrder)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_2
      //   2: aconst_null
      //   3: astore_3
      //   4: aload_2
      //   5: astore 4
      //   7: new 113	android/support/media/ExifInterface$ByteOrderedDataInputStream
      //   10: astore 5
      //   12: aload_2
      //   13: astore 4
      //   15: aload 5
      //   17: aload_0
      //   18: getfield 23	android/support/media/ExifInterface$ExifAttribute:bytes	[B
      //   21: invokespecial 116	android/support/media/ExifInterface$ByteOrderedDataInputStream:<init>	([B)V
      //   24: aload 5
      //   26: aload_1
      //   27: invokevirtual 120	android/support/media/ExifInterface$ByteOrderedDataInputStream:setByteOrder	(Ljava/nio/ByteOrder;)V
      //   30: aload_0
      //   31: getfield 19	android/support/media/ExifInterface$ExifAttribute:format	I
      //   34: istore 6
      //   36: iload 6
      //   38: tableswitch	default:+62->100, 1:+76->114, 2:+199->237, 3:+427->465, 4:+489->527, 5:+551->589, 6:+76->114, 7:+199->237, 8:+627->665, 9:+689->727, 10:+751->789, 11:+829->867, 12:+892->930
      //   100: aconst_null
      //   101: astore_1
      //   102: aload 5
      //   104: ifnull +8 -> 112
      //   107: aload 5
      //   109: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   112: aload_1
      //   113: areturn
      //   114: aload_0
      //   115: getfield 23	android/support/media/ExifInterface$ExifAttribute:bytes	[B
      //   118: arraylength
      //   119: iconst_1
      //   120: if_icmpne +74 -> 194
      //   123: aload_0
      //   124: getfield 23	android/support/media/ExifInterface$ExifAttribute:bytes	[B
      //   127: iconst_0
      //   128: baload
      //   129: iflt +65 -> 194
      //   132: aload_0
      //   133: getfield 23	android/support/media/ExifInterface$ExifAttribute:bytes	[B
      //   136: iconst_0
      //   137: baload
      //   138: iconst_1
      //   139: if_icmpgt +55 -> 194
      //   142: new 55	java/lang/String
      //   145: dup
      //   146: iconst_1
      //   147: newarray <illegal type>
      //   149: dup
      //   150: iconst_0
      //   151: aload_0
      //   152: getfield 23	android/support/media/ExifInterface$ExifAttribute:bytes	[B
      //   155: iconst_0
      //   156: baload
      //   157: bipush 48
      //   159: iadd
      //   160: i2c
      //   161: castore
      //   162: invokespecial 126	java/lang/String:<init>	([C)V
      //   165: astore_1
      //   166: aload 5
      //   168: ifnull +8 -> 176
      //   171: aload 5
      //   173: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   176: goto -64 -> 112
      //   179: astore 4
      //   181: ldc -128
      //   183: ldc -126
      //   185: aload 4
      //   187: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   190: pop
      //   191: goto -15 -> 176
      //   194: new 55	java/lang/String
      //   197: dup
      //   198: aload_0
      //   199: getfield 23	android/support/media/ExifInterface$ExifAttribute:bytes	[B
      //   202: invokestatic 53	android/support/media/ExifInterface:access$000	()Ljava/nio/charset/Charset;
      //   205: invokespecial 139	java/lang/String:<init>	([BLjava/nio/charset/Charset;)V
      //   208: astore_1
      //   209: aload 5
      //   211: ifnull +8 -> 219
      //   214: aload 5
      //   216: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   219: goto -107 -> 112
      //   222: astore 4
      //   224: ldc -128
      //   226: ldc -126
      //   228: aload 4
      //   230: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   233: pop
      //   234: goto -15 -> 219
      //   237: iconst_0
      //   238: istore 7
      //   240: iload 7
      //   242: istore 6
      //   244: aload_0
      //   245: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   248: invokestatic 142	android/support/media/ExifInterface:access$100	()[B
      //   251: arraylength
      //   252: if_icmplt +56 -> 308
      //   255: iconst_1
      //   256: istore 8
      //   258: iconst_0
      //   259: istore 6
      //   261: iload 8
      //   263: istore 9
      //   265: iload 6
      //   267: invokestatic 142	android/support/media/ExifInterface:access$100	()[B
      //   270: arraylength
      //   271: if_icmpge +22 -> 293
      //   274: aload_0
      //   275: getfield 23	android/support/media/ExifInterface$ExifAttribute:bytes	[B
      //   278: iload 6
      //   280: baload
      //   281: invokestatic 142	android/support/media/ExifInterface:access$100	()[B
      //   284: iload 6
      //   286: baload
      //   287: if_icmpeq +70 -> 357
      //   290: iconst_0
      //   291: istore 9
      //   293: iload 7
      //   295: istore 6
      //   297: iload 9
      //   299: ifeq +9 -> 308
      //   302: invokestatic 142	android/support/media/ExifInterface:access$100	()[B
      //   305: arraylength
      //   306: istore 6
      //   308: new 37	java/lang/StringBuilder
      //   311: astore_1
      //   312: aload_1
      //   313: invokespecial 38	java/lang/StringBuilder:<init>	()V
      //   316: iload 6
      //   318: aload_0
      //   319: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   322: if_icmpge +17 -> 339
      //   325: aload_0
      //   326: getfield 23	android/support/media/ExifInterface$ExifAttribute:bytes	[B
      //   329: iload 6
      //   331: baload
      //   332: istore 9
      //   334: iload 9
      //   336: ifne +27 -> 363
      //   339: aload_1
      //   340: invokevirtual 49	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   343: astore_1
      //   344: aload 5
      //   346: ifnull +8 -> 354
      //   349: aload 5
      //   351: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   354: goto -242 -> 112
      //   357: iinc 6 1
      //   360: goto -99 -> 261
      //   363: iload 9
      //   365: bipush 32
      //   367: if_icmplt +21 -> 388
      //   370: iload 9
      //   372: i2c
      //   373: istore 10
      //   375: aload_1
      //   376: iload 10
      //   378: invokevirtual 45	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
      //   381: pop
      //   382: iinc 6 1
      //   385: goto -69 -> 316
      //   388: aload_1
      //   389: bipush 63
      //   391: invokevirtual 45	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
      //   394: pop
      //   395: goto -13 -> 382
      //   398: astore_1
      //   399: aload 5
      //   401: astore 4
      //   403: ldc -128
      //   405: ldc -112
      //   407: aload_1
      //   408: invokestatic 147	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   411: pop
      //   412: aconst_null
      //   413: astore 4
      //   415: aload 4
      //   417: astore_1
      //   418: aload 5
      //   420: ifnull -308 -> 112
      //   423: aload 5
      //   425: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   428: aload 4
      //   430: astore_1
      //   431: goto -319 -> 112
      //   434: astore_1
      //   435: ldc -128
      //   437: ldc -126
      //   439: aload_1
      //   440: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   443: pop
      //   444: aload 4
      //   446: astore_1
      //   447: goto -335 -> 112
      //   450: astore 4
      //   452: ldc -128
      //   454: ldc -126
      //   456: aload 4
      //   458: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   461: pop
      //   462: goto -108 -> 354
      //   465: aload_0
      //   466: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   469: newarray <illegal type>
      //   471: astore_1
      //   472: iconst_0
      //   473: istore 6
      //   475: iload 6
      //   477: aload_0
      //   478: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   481: if_icmpge +18 -> 499
      //   484: aload_1
      //   485: iload 6
      //   487: aload 5
      //   489: invokevirtual 151	android/support/media/ExifInterface$ByteOrderedDataInputStream:readUnsignedShort	()I
      //   492: iastore
      //   493: iinc 6 1
      //   496: goto -21 -> 475
      //   499: aload 5
      //   501: ifnull +8 -> 509
      //   504: aload 5
      //   506: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   509: goto -397 -> 112
      //   512: astore 4
      //   514: ldc -128
      //   516: ldc -126
      //   518: aload 4
      //   520: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   523: pop
      //   524: goto -15 -> 509
      //   527: aload_0
      //   528: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   531: newarray <illegal type>
      //   533: astore_1
      //   534: iconst_0
      //   535: istore 6
      //   537: iload 6
      //   539: aload_0
      //   540: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   543: if_icmpge +18 -> 561
      //   546: aload_1
      //   547: iload 6
      //   549: aload 5
      //   551: invokevirtual 155	android/support/media/ExifInterface$ByteOrderedDataInputStream:readUnsignedInt	()J
      //   554: lastore
      //   555: iinc 6 1
      //   558: goto -21 -> 537
      //   561: aload 5
      //   563: ifnull +8 -> 571
      //   566: aload 5
      //   568: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   571: goto -459 -> 112
      //   574: astore 4
      //   576: ldc -128
      //   578: ldc -126
      //   580: aload 4
      //   582: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   585: pop
      //   586: goto -15 -> 571
      //   589: aload_0
      //   590: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   593: anewarray 90	android/support/media/ExifInterface$Rational
      //   596: astore_1
      //   597: iconst_0
      //   598: istore 6
      //   600: iload 6
      //   602: aload_0
      //   603: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   606: if_icmpge +31 -> 637
      //   609: aload_1
      //   610: iload 6
      //   612: new 90	android/support/media/ExifInterface$Rational
      //   615: dup
      //   616: aload 5
      //   618: invokevirtual 155	android/support/media/ExifInterface$ByteOrderedDataInputStream:readUnsignedInt	()J
      //   621: aload 5
      //   623: invokevirtual 155	android/support/media/ExifInterface$ByteOrderedDataInputStream:readUnsignedInt	()J
      //   626: aconst_null
      //   627: invokespecial 158	android/support/media/ExifInterface$Rational:<init>	(JJLandroid/support/media/ExifInterface$1;)V
      //   630: aastore
      //   631: iinc 6 1
      //   634: goto -34 -> 600
      //   637: aload 5
      //   639: ifnull +8 -> 647
      //   642: aload 5
      //   644: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   647: goto -535 -> 112
      //   650: astore 4
      //   652: ldc -128
      //   654: ldc -126
      //   656: aload 4
      //   658: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   661: pop
      //   662: goto -15 -> 647
      //   665: aload_0
      //   666: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   669: newarray <illegal type>
      //   671: astore_1
      //   672: iconst_0
      //   673: istore 6
      //   675: iload 6
      //   677: aload_0
      //   678: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   681: if_icmpge +18 -> 699
      //   684: aload_1
      //   685: iload 6
      //   687: aload 5
      //   689: invokevirtual 162	android/support/media/ExifInterface$ByteOrderedDataInputStream:readShort	()S
      //   692: iastore
      //   693: iinc 6 1
      //   696: goto -21 -> 675
      //   699: aload 5
      //   701: ifnull +8 -> 709
      //   704: aload 5
      //   706: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   709: goto -597 -> 112
      //   712: astore 4
      //   714: ldc -128
      //   716: ldc -126
      //   718: aload 4
      //   720: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   723: pop
      //   724: goto -15 -> 709
      //   727: aload_0
      //   728: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   731: newarray <illegal type>
      //   733: astore_1
      //   734: iconst_0
      //   735: istore 6
      //   737: iload 6
      //   739: aload_0
      //   740: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   743: if_icmpge +18 -> 761
      //   746: aload_1
      //   747: iload 6
      //   749: aload 5
      //   751: invokevirtual 165	android/support/media/ExifInterface$ByteOrderedDataInputStream:readInt	()I
      //   754: iastore
      //   755: iinc 6 1
      //   758: goto -21 -> 737
      //   761: aload 5
      //   763: ifnull +8 -> 771
      //   766: aload 5
      //   768: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   771: goto -659 -> 112
      //   774: astore 4
      //   776: ldc -128
      //   778: ldc -126
      //   780: aload 4
      //   782: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   785: pop
      //   786: goto -15 -> 771
      //   789: aload_0
      //   790: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   793: anewarray 90	android/support/media/ExifInterface$Rational
      //   796: astore_1
      //   797: iconst_0
      //   798: istore 6
      //   800: iload 6
      //   802: aload_0
      //   803: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   806: if_icmpge +33 -> 839
      //   809: aload_1
      //   810: iload 6
      //   812: new 90	android/support/media/ExifInterface$Rational
      //   815: dup
      //   816: aload 5
      //   818: invokevirtual 165	android/support/media/ExifInterface$ByteOrderedDataInputStream:readInt	()I
      //   821: i2l
      //   822: aload 5
      //   824: invokevirtual 165	android/support/media/ExifInterface$ByteOrderedDataInputStream:readInt	()I
      //   827: i2l
      //   828: aconst_null
      //   829: invokespecial 158	android/support/media/ExifInterface$Rational:<init>	(JJLandroid/support/media/ExifInterface$1;)V
      //   832: aastore
      //   833: iinc 6 1
      //   836: goto -36 -> 800
      //   839: aload 5
      //   841: ifnull +8 -> 849
      //   844: aload 5
      //   846: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   849: goto -737 -> 112
      //   852: astore 4
      //   854: ldc -128
      //   856: ldc -126
      //   858: aload 4
      //   860: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   863: pop
      //   864: goto -15 -> 849
      //   867: aload_0
      //   868: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   871: newarray <illegal type>
      //   873: astore_1
      //   874: iconst_0
      //   875: istore 6
      //   877: iload 6
      //   879: aload_0
      //   880: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   883: if_icmpge +19 -> 902
      //   886: aload_1
      //   887: iload 6
      //   889: aload 5
      //   891: invokevirtual 169	android/support/media/ExifInterface$ByteOrderedDataInputStream:readFloat	()F
      //   894: f2d
      //   895: dastore
      //   896: iinc 6 1
      //   899: goto -22 -> 877
      //   902: aload 5
      //   904: ifnull +8 -> 912
      //   907: aload 5
      //   909: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   912: goto -800 -> 112
      //   915: astore 4
      //   917: ldc -128
      //   919: ldc -126
      //   921: aload 4
      //   923: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   926: pop
      //   927: goto -15 -> 912
      //   930: aload_0
      //   931: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   934: newarray <illegal type>
      //   936: astore_1
      //   937: iconst_0
      //   938: istore 6
      //   940: iload 6
      //   942: aload_0
      //   943: getfield 21	android/support/media/ExifInterface$ExifAttribute:numberOfComponents	I
      //   946: if_icmpge +18 -> 964
      //   949: aload_1
      //   950: iload 6
      //   952: aload 5
      //   954: invokevirtual 173	android/support/media/ExifInterface$ByteOrderedDataInputStream:readDouble	()D
      //   957: dastore
      //   958: iinc 6 1
      //   961: goto -21 -> 940
      //   964: aload 5
      //   966: ifnull +8 -> 974
      //   969: aload 5
      //   971: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   974: goto -862 -> 112
      //   977: astore 4
      //   979: ldc -128
      //   981: ldc -126
      //   983: aload 4
      //   985: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   988: pop
      //   989: goto -15 -> 974
      //   992: astore 4
      //   994: ldc -128
      //   996: ldc -126
      //   998: aload 4
      //   1000: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1003: pop
      //   1004: goto -892 -> 112
      //   1007: astore_1
      //   1008: aload 4
      //   1010: ifnull +8 -> 1018
      //   1013: aload 4
      //   1015: invokevirtual 123	android/support/media/ExifInterface$ByteOrderedDataInputStream:close	()V
      //   1018: aload_1
      //   1019: athrow
      //   1020: astore 4
      //   1022: ldc -128
      //   1024: ldc -126
      //   1026: aload 4
      //   1028: invokestatic 136	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   1031: pop
      //   1032: goto -14 -> 1018
      //   1035: astore_1
      //   1036: aload 5
      //   1038: astore 4
      //   1040: goto -32 -> 1008
      //   1043: astore_1
      //   1044: aload_3
      //   1045: astore 5
      //   1047: goto -648 -> 399
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	1050	0	this	ExifAttribute
      //   0	1050	1	paramByteOrder	ByteOrder
      //   1	12	2	localObject1	Object
      //   3	1042	3	localObject2	Object
      //   5	9	4	localObject3	Object
      //   179	7	4	localIOException1	IOException
      //   222	7	4	localIOException2	IOException
      //   401	44	4	localObject4	Object
      //   450	7	4	localIOException3	IOException
      //   512	7	4	localIOException4	IOException
      //   574	7	4	localIOException5	IOException
      //   650	7	4	localIOException6	IOException
      //   712	7	4	localIOException7	IOException
      //   774	7	4	localIOException8	IOException
      //   852	7	4	localIOException9	IOException
      //   915	7	4	localIOException10	IOException
      //   977	7	4	localIOException11	IOException
      //   992	22	4	localIOException12	IOException
      //   1020	7	4	localIOException13	IOException
      //   1038	1	4	localObject5	Object
      //   10	1036	5	localObject6	Object
      //   34	925	6	i	int
      //   238	56	7	j	int
      //   256	6	8	k	int
      //   263	108	9	m	int
      //   373	4	10	c	char
      // Exception table:
      //   from	to	target	type
      //   171	176	179	java/io/IOException
      //   214	219	222	java/io/IOException
      //   24	36	398	java/io/IOException
      //   114	166	398	java/io/IOException
      //   194	209	398	java/io/IOException
      //   244	255	398	java/io/IOException
      //   265	290	398	java/io/IOException
      //   302	308	398	java/io/IOException
      //   308	316	398	java/io/IOException
      //   316	334	398	java/io/IOException
      //   339	344	398	java/io/IOException
      //   375	382	398	java/io/IOException
      //   388	395	398	java/io/IOException
      //   465	472	398	java/io/IOException
      //   475	493	398	java/io/IOException
      //   527	534	398	java/io/IOException
      //   537	555	398	java/io/IOException
      //   589	597	398	java/io/IOException
      //   600	631	398	java/io/IOException
      //   665	672	398	java/io/IOException
      //   675	693	398	java/io/IOException
      //   727	734	398	java/io/IOException
      //   737	755	398	java/io/IOException
      //   789	797	398	java/io/IOException
      //   800	833	398	java/io/IOException
      //   867	874	398	java/io/IOException
      //   877	896	398	java/io/IOException
      //   930	937	398	java/io/IOException
      //   940	958	398	java/io/IOException
      //   423	428	434	java/io/IOException
      //   349	354	450	java/io/IOException
      //   504	509	512	java/io/IOException
      //   566	571	574	java/io/IOException
      //   642	647	650	java/io/IOException
      //   704	709	712	java/io/IOException
      //   766	771	774	java/io/IOException
      //   844	849	852	java/io/IOException
      //   907	912	915	java/io/IOException
      //   969	974	977	java/io/IOException
      //   107	112	992	java/io/IOException
      //   7	12	1007	finally
      //   15	24	1007	finally
      //   403	412	1007	finally
      //   1013	1018	1020	java/io/IOException
      //   24	36	1035	finally
      //   114	166	1035	finally
      //   194	209	1035	finally
      //   244	255	1035	finally
      //   265	290	1035	finally
      //   302	308	1035	finally
      //   308	316	1035	finally
      //   316	334	1035	finally
      //   339	344	1035	finally
      //   375	382	1035	finally
      //   388	395	1035	finally
      //   465	472	1035	finally
      //   475	493	1035	finally
      //   527	534	1035	finally
      //   537	555	1035	finally
      //   589	597	1035	finally
      //   600	631	1035	finally
      //   665	672	1035	finally
      //   675	693	1035	finally
      //   727	734	1035	finally
      //   737	755	1035	finally
      //   789	797	1035	finally
      //   800	833	1035	finally
      //   867	874	1035	finally
      //   877	896	1035	finally
      //   930	937	1035	finally
      //   940	958	1035	finally
      //   7	12	1043	java/io/IOException
      //   15	24	1043	java/io/IOException
    }
    
    public double getDoubleValue(ByteOrder paramByteOrder)
    {
      paramByteOrder = getValue(paramByteOrder);
      if (paramByteOrder == null) {
        throw new NumberFormatException("NULL can't be converted to a double value");
      }
      double d;
      if ((paramByteOrder instanceof String)) {
        d = Double.parseDouble((String)paramByteOrder);
      }
      for (;;)
      {
        return d;
        if ((paramByteOrder instanceof long[]))
        {
          paramByteOrder = (long[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            d = paramByteOrder[0];
          } else {
            throw new NumberFormatException("There are more than one component");
          }
        }
        else if ((paramByteOrder instanceof int[]))
        {
          paramByteOrder = (int[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            d = paramByteOrder[0];
          } else {
            throw new NumberFormatException("There are more than one component");
          }
        }
        else if ((paramByteOrder instanceof double[]))
        {
          paramByteOrder = (double[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            d = paramByteOrder[0];
          } else {
            throw new NumberFormatException("There are more than one component");
          }
        }
        else
        {
          if (!(paramByteOrder instanceof ExifInterface.Rational[])) {
            break label182;
          }
          paramByteOrder = (ExifInterface.Rational[])paramByteOrder;
          if (paramByteOrder.length != 1) {
            break;
          }
          d = paramByteOrder[0].calculate();
        }
      }
      throw new NumberFormatException("There are more than one component");
      label182:
      throw new NumberFormatException("Couldn't find a double value");
    }
    
    public int getIntValue(ByteOrder paramByteOrder)
    {
      paramByteOrder = getValue(paramByteOrder);
      if (paramByteOrder == null) {
        throw new NumberFormatException("NULL can't be converted to a integer value");
      }
      int i;
      if ((paramByteOrder instanceof String)) {
        i = Integer.parseInt((String)paramByteOrder);
      }
      for (;;)
      {
        return i;
        if ((paramByteOrder instanceof long[]))
        {
          paramByteOrder = (long[])paramByteOrder;
          if (paramByteOrder.length == 1) {
            i = (int)paramByteOrder[0];
          } else {
            throw new NumberFormatException("There are more than one component");
          }
        }
        else
        {
          if (!(paramByteOrder instanceof int[])) {
            break label108;
          }
          paramByteOrder = (int[])paramByteOrder;
          if (paramByteOrder.length != 1) {
            break;
          }
          i = paramByteOrder[0];
        }
      }
      throw new NumberFormatException("There are more than one component");
      label108:
      throw new NumberFormatException("Couldn't find a integer value");
    }
    
    public String getStringValue(ByteOrder paramByteOrder)
    {
      Object localObject = getValue(paramByteOrder);
      if (localObject == null) {
        paramByteOrder = null;
      }
      for (;;)
      {
        return paramByteOrder;
        if ((localObject instanceof String))
        {
          paramByteOrder = (String)localObject;
        }
        else
        {
          paramByteOrder = new StringBuilder();
          int i;
          if ((localObject instanceof long[]))
          {
            localObject = (long[])localObject;
            for (i = 0; i < localObject.length; i++)
            {
              paramByteOrder.append(localObject[i]);
              if (i + 1 != localObject.length) {
                paramByteOrder.append(",");
              }
            }
            paramByteOrder = paramByteOrder.toString();
          }
          else if ((localObject instanceof int[]))
          {
            localObject = (int[])localObject;
            for (i = 0; i < localObject.length; i++)
            {
              paramByteOrder.append(localObject[i]);
              if (i + 1 != localObject.length) {
                paramByteOrder.append(",");
              }
            }
            paramByteOrder = paramByteOrder.toString();
          }
          else if ((localObject instanceof double[]))
          {
            localObject = (double[])localObject;
            for (i = 0; i < localObject.length; i++)
            {
              paramByteOrder.append(localObject[i]);
              if (i + 1 != localObject.length) {
                paramByteOrder.append(",");
              }
            }
            paramByteOrder = paramByteOrder.toString();
          }
          else if ((localObject instanceof ExifInterface.Rational[]))
          {
            localObject = (ExifInterface.Rational[])localObject;
            for (i = 0; i < localObject.length; i++)
            {
              paramByteOrder.append(localObject[i].numerator);
              paramByteOrder.append('/');
              paramByteOrder.append(localObject[i].denominator);
              if (i + 1 != localObject.length) {
                paramByteOrder.append(",");
              }
            }
            paramByteOrder = paramByteOrder.toString();
          }
          else
          {
            paramByteOrder = null;
          }
        }
      }
    }
    
    public String toString()
    {
      return "(" + ExifInterface.IFD_FORMAT_NAMES[this.format] + ", data length:" + this.bytes.length + ")";
    }
  }
  
  static class ExifTag
  {
    public final String name;
    public final int number;
    public final int primaryFormat;
    public final int secondaryFormat;
    
    private ExifTag(String paramString, int paramInt1, int paramInt2)
    {
      this.name = paramString;
      this.number = paramInt1;
      this.primaryFormat = paramInt2;
      this.secondaryFormat = -1;
    }
    
    private ExifTag(String paramString, int paramInt1, int paramInt2, int paramInt3)
    {
      this.name = paramString;
      this.number = paramInt1;
      this.primaryFormat = paramInt2;
      this.secondaryFormat = paramInt3;
    }
    
    private boolean isFormatCompatible(int paramInt)
    {
      boolean bool1 = true;
      boolean bool2 = bool1;
      if (this.primaryFormat != 7)
      {
        if (paramInt != 7) {
          break label23;
        }
        bool2 = bool1;
      }
      for (;;)
      {
        return bool2;
        label23:
        bool2 = bool1;
        if (this.primaryFormat != paramInt)
        {
          bool2 = bool1;
          if (this.secondaryFormat != paramInt) {
            if ((this.primaryFormat == 4) || (this.secondaryFormat == 4))
            {
              bool2 = bool1;
              if (paramInt == 3) {}
            }
            else if ((this.primaryFormat == 9) || (this.secondaryFormat == 9))
            {
              bool2 = bool1;
              if (paramInt == 8) {}
            }
            else if ((this.primaryFormat == 12) || (this.secondaryFormat == 12))
            {
              bool2 = bool1;
              if (paramInt == 11) {}
            }
            else
            {
              bool2 = false;
            }
          }
        }
      }
    }
  }
  
  private static class Rational
  {
    public final long denominator;
    public final long numerator;
    
    private Rational(long paramLong1, long paramLong2)
    {
      if (paramLong2 == 0L) {
        this.numerator = 0L;
      }
      for (this.denominator = 1L;; this.denominator = paramLong2)
      {
        return;
        this.numerator = paramLong1;
      }
    }
    
    public double calculate()
    {
      return this.numerator / this.denominator;
    }
    
    public String toString()
    {
      return this.numerator + "/" + this.denominator;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/media/ExifInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */