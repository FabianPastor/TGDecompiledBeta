package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;

public final class FilteringDashManifestParser
  implements ParsingLoadable.Parser<DashManifest>
{
  private final DashManifestParser dashManifestParser = new DashManifestParser();
  private final ArrayList<RepresentationKey> filter;
  
  public FilteringDashManifestParser(ArrayList<RepresentationKey> paramArrayList)
  {
    this.filter = paramArrayList;
  }
  
  public DashManifest parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    return this.dashManifestParser.parse(paramUri, paramInputStream).copy(this.filter);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/manifest/FilteringDashManifestParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */