package org.telegram.messenger.exoplayer2.metadata.emsg;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class EventMessageDecoder
  implements MetadataDecoder
{
  public Metadata decode(MetadataInputBuffer paramMetadataInputBuffer)
  {
    Object localObject = paramMetadataInputBuffer.data;
    paramMetadataInputBuffer = ((ByteBuffer)localObject).array();
    int i = ((ByteBuffer)localObject).limit();
    localObject = new ParsableByteArray(paramMetadataInputBuffer, i);
    String str1 = ((ParsableByteArray)localObject).readNullTerminatedString();
    String str2 = ((ParsableByteArray)localObject).readNullTerminatedString();
    long l1 = ((ParsableByteArray)localObject).readUnsignedInt();
    long l2 = Util.scaleLargeTimestamp(((ParsableByteArray)localObject).readUnsignedInt(), 1000000L, l1);
    return new Metadata(new Metadata.Entry[] { new EventMessage(str1, str2, Util.scaleLargeTimestamp(((ParsableByteArray)localObject).readUnsignedInt(), 1000L, l1), ((ParsableByteArray)localObject).readUnsignedInt(), Arrays.copyOfRange(paramMetadataInputBuffer, ((ParsableByteArray)localObject).getPosition(), i), l2) });
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/emsg/EventMessageDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */