package com.googlecode.mp4parser.authoring.tracks;

import com.coremedia.iso.Utf8;
import com.coremedia.iso.boxes.SampleDescriptionBox;
import com.coremedia.iso.boxes.SubSampleInformationBox;
import com.coremedia.iso.boxes.SubSampleInformationBox.SubSampleEntry;
import com.coremedia.iso.boxes.SubSampleInformationBox.SubSampleEntry.SubsampleEntry;
import com.googlecode.mp4parser.authoring.AbstractTrack;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.authoring.TrackMetaData;
import com.googlecode.mp4parser.util.Iso639;
import com.mp4parser.iso14496.part30.XMLSubtitleSampleEntry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SMPTETTTrackImpl
  extends AbstractTrack
{
  public static final String SMPTE_TT_NAMESPACE = "http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt";
  XMLSubtitleSampleEntry XMLSubtitleSampleEntry = new XMLSubtitleSampleEntry();
  boolean containsImages;
  SampleDescriptionBox sampleDescriptionBox = new SampleDescriptionBox();
  private long[] sampleDurations;
  List<Sample> samples = new ArrayList();
  SubSampleInformationBox subSampleInformationBox = new SubSampleInformationBox();
  TrackMetaData trackMetaData = new TrackMetaData();
  
  public SMPTETTTrackImpl(File... paramVarArgs)
    throws IOException, ParserConfigurationException, SAXException, XPathExpressionException
  {
    super(paramVarArgs[0].getName());
    this.sampleDurations = new long[paramVarArgs.length];
    Object localObject1 = DocumentBuilderFactory.newInstance();
    ((DocumentBuilderFactory)localObject1).setNamespaceAware(true);
    DocumentBuilder localDocumentBuilder = ((DocumentBuilderFactory)localObject1).newDocumentBuilder();
    long l1 = 0L;
    final Object localObject2 = null;
    int i = 0;
    if (i >= paramVarArgs.length)
    {
      this.trackMetaData.setLanguage(Iso639.convert2to3((String)localObject2));
      this.XMLSubtitleSampleEntry.setNamespace("http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt");
      this.XMLSubtitleSampleEntry.setSchemaLocation("http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt");
      if (!this.containsImages) {
        break label793;
      }
      this.XMLSubtitleSampleEntry.setAuxiliaryMimeTypes("image/png");
    }
    for (;;)
    {
      this.sampleDescriptionBox.addBox(this.XMLSubtitleSampleEntry);
      this.trackMetaData.setTimescale(30000L);
      this.trackMetaData.setLayer(65535);
      return;
      final File localFile = paramVarArgs[i];
      SubSampleInformationBox.SubSampleEntry localSubSampleEntry = new SubSampleInformationBox.SubSampleEntry();
      this.subSampleInformationBox.getEntries().add(localSubSampleEntry);
      localSubSampleEntry.setSampleDelta(1L);
      final Object localObject4 = localDocumentBuilder.parse(localFile);
      Object localObject3 = getLanguage((Document)localObject4);
      label242:
      int j;
      label339:
      label380:
      label434:
      Object localObject5;
      if (localObject2 == null)
      {
        localObject1 = localObject3;
        localObject3 = XPathFactory.newInstance();
        localObject2 = new TextTrackNamespaceContext(null);
        localObject3 = ((XPathFactory)localObject3).newXPath();
        ((XPath)localObject3).setNamespaceContext((NamespaceContext)localObject2);
        long l2 = latestTimestamp((Document)localObject4);
        this.sampleDurations[i] = (l2 - l1);
        l1 = l2;
        localObject2 = (NodeList)((XPath)localObject3).compile("/ttml:tt/ttml:body/ttml:div/@smpte:backgroundImage").evaluate(localObject4, XPathConstants.NODESET);
        localObject4 = new HashMap();
        localObject3 = new HashSet();
        j = 0;
        if (j < ((NodeList)localObject2).getLength()) {
          break label563;
        }
        localObject3 = new ArrayList((Collection)localObject3);
        Collections.sort((List)localObject3);
        j = 1;
        localObject2 = ((Collection)localObject3).iterator();
        if (((Iterator)localObject2).hasNext()) {
          break label591;
        }
        if (((Collection)localObject3).isEmpty()) {
          break label770;
        }
        localObject2 = new String(streamToByteArray(new FileInputStream(localFile)));
        localObject4 = ((HashMap)localObject4).entrySet().iterator();
        if (((Iterator)localObject4).hasNext()) {
          break label655;
        }
        localObject4 = new ArrayList();
        this.samples.add(new Sample()
        {
          public ByteBuffer asByteBuffer()
          {
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            try
            {
              writeTo(Channels.newChannel(localByteArrayOutputStream));
              return ByteBuffer.wrap(localByteArrayOutputStream.toByteArray());
            }
            catch (IOException localIOException)
            {
              throw new RuntimeException(localIOException);
            }
          }
          
          public long getSize()
          {
            long l = Utf8.convert(localObject2).length;
            Iterator localIterator = localObject4.iterator();
            for (;;)
            {
              if (!localIterator.hasNext()) {
                return l;
              }
              l += ((File)localIterator.next()).length();
            }
          }
          
          public void writeTo(WritableByteChannel paramAnonymousWritableByteChannel)
            throws IOException
          {
            paramAnonymousWritableByteChannel.write(ByteBuffer.wrap(Utf8.convert(localObject2)));
            Iterator localIterator = localObject4.iterator();
            if (!localIterator.hasNext()) {
              return;
            }
            FileInputStream localFileInputStream = new FileInputStream((File)localIterator.next());
            byte[] arrayOfByte = new byte['ᾠ'];
            for (;;)
            {
              int i = localFileInputStream.read(arrayOfByte);
              if (-1 == i) {
                break;
              }
              paramAnonymousWritableByteChannel.write(ByteBuffer.wrap(arrayOfByte, 0, i));
            }
          }
        });
        localObject5 = new SubSampleInformationBox.SubSampleEntry.SubsampleEntry();
        ((SubSampleInformationBox.SubSampleEntry.SubsampleEntry)localObject5).setSubsampleSize(Utf8.utf8StringLengthInBytes((String)localObject2));
        localSubSampleEntry.getSubsampleEntries().add(localObject5);
        localObject2 = ((Collection)localObject3).iterator();
        label517:
        if (((Iterator)localObject2).hasNext()) {
          break label697;
        }
      }
      for (;;)
      {
        i += 1;
        localObject2 = localObject1;
        break;
        localObject1 = localObject2;
        if (((String)localObject2).equals(localObject3)) {
          break label242;
        }
        throw new RuntimeException("Within one Track all sample documents need to have the same language");
        label563:
        ((Collection)localObject3).add(((NodeList)localObject2).item(j).getNodeValue());
        j += 1;
        break label339;
        label591:
        localObject5 = (String)((Iterator)localObject2).next();
        String str = ((String)localObject5).substring(((String)localObject5).lastIndexOf("."));
        ((HashMap)localObject4).put(localObject5, "urn:dece:container:subtitleimageindex:" + j + str);
        j += 1;
        break label380;
        label655:
        localObject5 = (Map.Entry)((Iterator)localObject4).next();
        localObject2 = ((String)localObject2).replace((CharSequence)((Map.Entry)localObject5).getKey(), (CharSequence)((Map.Entry)localObject5).getValue());
        break label434;
        label697:
        localObject3 = (String)((Iterator)localObject2).next();
        localObject3 = new File(localFile.getParentFile(), (String)localObject3);
        ((List)localObject4).add(localObject3);
        localObject5 = new SubSampleInformationBox.SubSampleEntry.SubsampleEntry();
        ((SubSampleInformationBox.SubSampleEntry.SubsampleEntry)localObject5).setSubsampleSize(((File)localObject3).length());
        localSubSampleEntry.getSubsampleEntries().add(localObject5);
        break label517;
        label770:
        this.samples.add(new Sample()
        {
          public ByteBuffer asByteBuffer()
          {
            try
            {
              ByteBuffer localByteBuffer = ByteBuffer.wrap(SMPTETTTrackImpl.this.streamToByteArray(new FileInputStream(localFile)));
              return localByteBuffer;
            }
            catch (IOException localIOException)
            {
              throw new RuntimeException(localIOException);
            }
          }
          
          public long getSize()
          {
            return localFile.length();
          }
          
          public void writeTo(WritableByteChannel paramAnonymousWritableByteChannel)
            throws IOException
          {
            Channels.newOutputStream(paramAnonymousWritableByteChannel).write(SMPTETTTrackImpl.this.streamToByteArray(new FileInputStream(localFile)));
          }
        });
      }
      label793:
      this.XMLSubtitleSampleEntry.setAuxiliaryMimeTypes("");
    }
  }
  
  /* Error */
  public static long earliestTimestamp(Document paramDocument)
  {
    // Byte code:
    //   0: invokestatic 160	javax/xml/xpath/XPathFactory:newInstance	()Ljavax/xml/xpath/XPathFactory;
    //   3: astore 5
    //   5: new 10	com/googlecode/mp4parser/authoring/tracks/SMPTETTTrackImpl$TextTrackNamespaceContext
    //   8: dup
    //   9: aconst_null
    //   10: invokespecial 163	com/googlecode/mp4parser/authoring/tracks/SMPTETTTrackImpl$TextTrackNamespaceContext:<init>	(Lcom/googlecode/mp4parser/authoring/tracks/SMPTETTTrackImpl$TextTrackNamespaceContext;)V
    //   13: astore 4
    //   15: aload 5
    //   17: invokevirtual 167	javax/xml/xpath/XPathFactory:newXPath	()Ljavax/xml/xpath/XPath;
    //   20: astore 5
    //   22: aload 5
    //   24: aload 4
    //   26: invokeinterface 173 2 0
    //   31: aload 5
    //   33: ldc_w 357
    //   36: invokeinterface 183 2 0
    //   41: aload_0
    //   42: getstatic 189	javax/xml/xpath/XPathConstants:NODESET	Ljavax/xml/namespace/QName;
    //   45: invokeinterface 195 3 0
    //   50: checkcast 197	org/w3c/dom/NodeList
    //   53: astore_0
    //   54: lconst_0
    //   55: lstore_2
    //   56: iconst_0
    //   57: istore_1
    //   58: iload_1
    //   59: aload_0
    //   60: invokeinterface 207 1 0
    //   65: if_icmplt +5 -> 70
    //   68: lload_2
    //   69: lreturn
    //   70: aload_0
    //   71: iload_1
    //   72: invokeinterface 282 2 0
    //   77: invokeinterface 361 1 0
    //   82: ldc_w 363
    //   85: invokeinterface 369 2 0
    //   90: invokeinterface 287 1 0
    //   95: invokestatic 373	com/googlecode/mp4parser/authoring/tracks/SMPTETTTrackImpl:toTime	(Ljava/lang/String;)J
    //   98: lload_2
    //   99: invokestatic 379	java/lang/Math:min	(JJ)J
    //   102: lstore_2
    //   103: iload_1
    //   104: iconst_1
    //   105: iadd
    //   106: istore_1
    //   107: goto -49 -> 58
    //   110: astore_0
    //   111: new 275	java/lang/RuntimeException
    //   114: dup
    //   115: aload_0
    //   116: invokespecial 382	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   119: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	120	0	paramDocument	Document
    //   57	50	1	i	int
    //   55	48	2	l	long
    //   13	12	4	localTextTrackNamespaceContext	TextTrackNamespaceContext
    //   3	29	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   31	54	110	javax/xml/xpath/XPathExpressionException
    //   58	68	110	javax/xml/xpath/XPathExpressionException
    //   70	103	110	javax/xml/xpath/XPathExpressionException
  }
  
  public static String getLanguage(Document paramDocument)
  {
    return paramDocument.getDocumentElement().getAttribute("xml:lang");
  }
  
  public static long latestTimestamp(Document paramDocument)
  {
    Object localObject2 = XPathFactory.newInstance();
    Object localObject1 = new TextTrackNamespaceContext(null);
    localObject2 = ((XPathFactory)localObject2).newXPath();
    ((XPath)localObject2).setNamespaceContext((NamespaceContext)localObject1);
    try
    {
      paramDocument = (NodeList)((XPath)localObject2).compile("//*[@begin]").evaluate(paramDocument, XPathConstants.NODESET);
      long l1 = 0L;
      int i = 0;
      if (i >= paramDocument.getLength()) {
        return l1;
      }
      localObject1 = paramDocument.item(i);
      localObject2 = ((Node)localObject1).getAttributes().getNamedItem("begin").getNodeValue();
      if (((Node)localObject1).getAttributes().getNamedItem("dur") != null) {}
      for (long l2 = toTime((String)localObject2) + toTime(((Node)localObject1).getAttributes().getNamedItem("dur").getNodeValue());; l2 = toTime(((Node)localObject1).getAttributes().getNamedItem("end").getNodeValue()))
      {
        l1 = Math.max(l2, l1);
        i += 1;
        break;
        if (((Node)localObject1).getAttributes().getNamedItem("end") == null) {
          break label210;
        }
      }
      label210:
      throw new RuntimeException("neither end nor dur attribute is present");
    }
    catch (XPathExpressionException paramDocument)
    {
      throw new RuntimeException(paramDocument);
    }
  }
  
  private byte[] streamToByteArray(InputStream paramInputStream)
    throws IOException
  {
    byte[] arrayOfByte = new byte['ᾠ'];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (-1 == i) {
        return localByteArrayOutputStream.toByteArray();
      }
      localByteArrayOutputStream.write(arrayOfByte, 0, i);
    }
  }
  
  static long toTime(String paramString)
  {
    Object localObject = Pattern.compile("([0-9][0-9]):([0-9][0-9]):([0-9][0-9])([\\.:][0-9][0-9]?[0-9]?)?").matcher(paramString);
    if (((Matcher)localObject).matches())
    {
      String str1 = ((Matcher)localObject).group(1);
      String str2 = ((Matcher)localObject).group(2);
      String str3 = ((Matcher)localObject).group(3);
      localObject = ((Matcher)localObject).group(4);
      paramString = (String)localObject;
      if (localObject == null) {
        paramString = ".000";
      }
      paramString = paramString.replace(":", ".");
      return (Long.parseLong(str1) * 60L * 60L * 1000L + Long.parseLong(str2) * 60L * 1000L + Long.parseLong(str3) * 1000L + Double.parseDouble("0" + paramString) * 1000.0D);
    }
    throw new RuntimeException("Cannot match " + paramString + " to time expression");
  }
  
  public void close()
    throws IOException
  {}
  
  public String getHandler()
  {
    return "subt";
  }
  
  public SampleDescriptionBox getSampleDescriptionBox()
  {
    return this.sampleDescriptionBox;
  }
  
  public long[] getSampleDurations()
  {
    long[] arrayOfLong = new long[this.sampleDurations.length];
    int i = 0;
    for (;;)
    {
      if (i >= arrayOfLong.length) {
        return arrayOfLong;
      }
      arrayOfLong[i] = (this.sampleDurations[i] * this.trackMetaData.getTimescale() / 1000L);
      i += 1;
    }
  }
  
  public List<Sample> getSamples()
  {
    return this.samples;
  }
  
  public SubSampleInformationBox getSubsampleInformationBox()
  {
    return this.subSampleInformationBox;
  }
  
  public TrackMetaData getTrackMetaData()
  {
    return this.trackMetaData;
  }
  
  private static class TextTrackNamespaceContext
    implements NamespaceContext
  {
    public String getNamespaceURI(String paramString)
    {
      if (paramString.equals("ttml")) {
        return "http://www.w3.org/ns/ttml";
      }
      if (paramString.equals("smpte")) {
        return "http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt";
      }
      return null;
    }
    
    public String getPrefix(String paramString)
    {
      if (paramString.equals("http://www.w3.org/ns/ttml")) {
        return "ttml";
      }
      if (paramString.equals("http://www.smpte-ra.org/schemas/2052-1/2010/smpte-tt")) {
        return "smpte";
      }
      return null;
    }
    
    public Iterator getPrefixes(String paramString)
    {
      return Arrays.asList(new String[] { "ttml", "smpte" }).iterator();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/SMPTETTTrackImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */