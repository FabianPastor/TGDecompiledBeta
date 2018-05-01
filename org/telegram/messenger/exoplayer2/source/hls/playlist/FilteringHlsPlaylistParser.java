package org.telegram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;

public final class FilteringHlsPlaylistParser
  implements ParsingLoadable.Parser<HlsPlaylist>
{
  private final List<String> filter;
  private final HlsPlaylistParser hlsPlaylistParser = new HlsPlaylistParser();
  
  public FilteringHlsPlaylistParser(List<String> paramList)
  {
    this.filter = paramList;
  }
  
  public HlsPlaylist parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    paramUri = this.hlsPlaylistParser.parse(paramUri, paramInputStream);
    if ((paramUri instanceof HlsMasterPlaylist)) {}
    for (paramUri = (HlsMasterPlaylist)paramUri;; paramUri = HlsMasterPlaylist.createSingleVariantMasterPlaylist(paramUri.baseUri)) {
      return paramUri.copy(this.filter);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/playlist/FilteringHlsPlaylistParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */