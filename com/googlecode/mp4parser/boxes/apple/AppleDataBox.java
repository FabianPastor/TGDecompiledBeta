package com.googlecode.mp4parser.boxes.apple;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.googlecode.mp4parser.AbstractBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import com.googlecode.mp4parser.annotations.DoNotParseDetail;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Locale;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public abstract class AppleDataBox
  extends AbstractBox
{
  private static HashMap<String, String> language;
  int dataCountry;
  int dataLanguage;
  int dataType;
  
  static
  {
    ajc$preClinit();
    language = new HashMap();
    language.put("0", "English");
    language.put("1", "French");
    language.put("2", "German");
    language.put("3", "Italian");
    language.put("4", "Dutch");
    language.put("5", "Swedish");
    language.put("6", "Spanish");
    language.put("7", "Danish");
    language.put("8", "Portuguese");
    language.put("9", "Norwegian");
    language.put("10", "Hebrew");
    language.put("11", "Japanese");
    language.put("12", "Arabic");
    language.put("13", "Finnish");
    language.put("14", "Greek");
    language.put("15", "Icelandic");
    language.put("16", "Maltese");
    language.put("17", "Turkish");
    language.put("18", "Croatian");
    language.put("19", "Traditional_Chinese");
    language.put("20", "Urdu");
    language.put("21", "Hindi");
    language.put("22", "Thai");
    language.put("23", "Korean");
    language.put("24", "Lithuanian");
    language.put("25", "Polish");
    language.put("26", "Hungarian");
    language.put("27", "Estonian");
    language.put("28", "Lettish");
    language.put("29", "Sami");
    language.put("30", "Faroese");
    language.put("31", "Farsi");
    language.put("32", "Russian");
    language.put("33", "Simplified_Chinese");
    language.put("34", "Flemish");
    language.put("35", "Irish");
    language.put("36", "Albanian");
    language.put("37", "Romanian");
    language.put("38", "Czech");
    language.put("39", "Slovak");
    language.put("40", "Slovenian");
    language.put("41", "Yiddish");
    language.put("42", "Serbian");
    language.put("43", "Macedonian");
    language.put("44", "Bulgarian");
    language.put("45", "Ukrainian");
    language.put("46", "Belarusian");
    language.put("47", "Uzbek");
    language.put("48", "Kazakh");
    language.put("49", "Azerbaijani");
    language.put("50", "AzerbaijanAr");
    language.put("51", "Armenian");
    language.put("52", "Georgian");
    language.put("53", "Moldavian");
    language.put("54", "Kirghiz");
    language.put("55", "Tajiki");
    language.put("56", "Turkmen");
    language.put("57", "Mongolian");
    language.put("58", "MongolianCyr");
    language.put("59", "Pashto");
    language.put("60", "Kurdish");
    language.put("61", "Kashmiri");
    language.put("62", "Sindhi");
    language.put("63", "Tibetan");
    language.put("64", "Nepali");
    language.put("65", "Sanskrit");
    language.put("66", "Marathi");
    language.put("67", "Bengali");
    language.put("68", "Assamese");
    language.put("69", "Gujarati");
    language.put("70", "Punjabi");
    language.put("71", "Oriya");
    language.put("72", "Malayalam");
    language.put("73", "Kannada");
    language.put("74", "Tamil");
    language.put("75", "Telugu");
    language.put("76", "Sinhala");
    language.put("77", "Burmese");
    language.put("78", "Khmer");
    language.put("79", "Lao");
    language.put("80", "Vietnamese");
    language.put("81", "Indonesian");
    language.put("82", "Tagalog");
    language.put("83", "MalayRoman");
    language.put("84", "MalayArabic");
    language.put("85", "Amharic");
    language.put("87", "Galla");
    language.put("87", "Oromo");
    language.put("88", "Somali");
    language.put("89", "Swahili");
    language.put("90", "Kinyarwanda");
    language.put("91", "Rundi");
    language.put("92", "Nyanja");
    language.put("93", "Malagasy");
    language.put("94", "Esperanto");
    language.put("128", "Welsh");
    language.put("129", "Basque");
    language.put("130", "Catalan");
    language.put("131", "Latin");
    language.put("132", "Quechua");
    language.put("133", "Guarani");
    language.put("134", "Aymara");
    language.put("135", "Tatar");
    language.put("136", "Uighur");
    language.put("137", "Dzongkha");
    language.put("138", "JavaneseRom");
    language.put("32767", "Unspecified");
  }
  
  protected AppleDataBox(String paramString, int paramInt)
  {
    super(paramString);
    this.dataType = paramInt;
  }
  
  protected void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseData(parseDataLength4ccTypeCountryLanguageAndReturnRest(paramByteBuffer));
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeDataLength4ccTypeCountryLanguage(paramByteBuffer);
    paramByteBuffer.put(writeData());
  }
  
  protected long getContentSize()
  {
    return getDataLength() + 16;
  }
  
  public int getDataCountry()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataCountry;
  }
  
  public int getDataLanguage()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataLanguage;
  }
  
  protected abstract int getDataLength();
  
  public int getDataType()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.dataType;
  }
  
  public String getLanguageString()
  {
    Object localObject = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before((JoinPoint)localObject);
    String str = (String)language.get(this.dataLanguage);
    localObject = str;
    if (str == null)
    {
      localObject = ByteBuffer.wrap(new byte[2]);
      IsoTypeWriter.writeUInt16((ByteBuffer)localObject, this.dataLanguage);
      ((ByteBuffer)localObject).reset();
      localObject = new Locale(IsoTypeReader.readIso639((ByteBuffer)localObject)).getDisplayLanguage();
    }
    return (String)localObject;
  }
  
  protected abstract void parseData(ByteBuffer paramByteBuffer);
  
  @DoNotParseDetail
  protected ByteBuffer parseDataLength4ccTypeCountryLanguageAndReturnRest(ByteBuffer paramByteBuffer)
  {
    int i = paramByteBuffer.getInt();
    paramByteBuffer.getInt();
    this.dataType = paramByteBuffer.getInt();
    this.dataCountry = paramByteBuffer.getShort();
    if (this.dataCountry < 0) {
      this.dataCountry += 65536;
    }
    this.dataLanguage = paramByteBuffer.getShort();
    if (this.dataLanguage < 0) {
      this.dataLanguage += 65536;
    }
    ByteBuffer localByteBuffer = (ByteBuffer)paramByteBuffer.duplicate().slice().limit(i - 16);
    paramByteBuffer.position(i - 16 + paramByteBuffer.position());
    return localByteBuffer;
  }
  
  public void setDataCountry(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.dataCountry = paramInt;
  }
  
  public void setDataLanguage(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.dataLanguage = paramInt;
  }
  
  protected abstract byte[] writeData();
  
  @DoNotParseDetail
  protected void writeDataLength4ccTypeCountryLanguage(ByteBuffer paramByteBuffer)
  {
    paramByteBuffer.putInt(getDataLength() + 16);
    paramByteBuffer.put("data".getBytes());
    paramByteBuffer.putInt(this.dataType);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.dataCountry);
    IsoTypeWriter.writeUInt16(paramByteBuffer, this.dataLanguage);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/apple/AppleDataBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */