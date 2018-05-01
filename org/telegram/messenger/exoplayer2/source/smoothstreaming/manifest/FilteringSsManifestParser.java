package org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;

public final class FilteringSsManifestParser
  implements ParsingLoadable.Parser<SsManifest>
{
  private final List<TrackKey> filter;
  private final SsManifestParser ssManifestParser = new SsManifestParser();
  
  public FilteringSsManifestParser(List<TrackKey> paramList)
  {
    this.filter = paramList;
  }
  
  public SsManifest parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    return this.ssManifestParser.parse(paramUri, paramInputStream).copy(this.filter);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/smoothstreaming/manifest/FilteringSsManifestParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */