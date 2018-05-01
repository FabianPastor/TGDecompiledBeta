package org.telegram.messenger.exoplayer2.source.dash.manifest;

import java.util.Collections;
import java.util.List;

public class AdaptationSet
{
  public static final int ID_UNSET = -1;
  public final List<Descriptor> accessibilityDescriptors;
  public final int id;
  public final List<Representation> representations;
  public final List<Descriptor> supplementalProperties;
  public final int type;
  
  public AdaptationSet(int paramInt1, int paramInt2, List<Representation> paramList, List<Descriptor> paramList1, List<Descriptor> paramList2)
  {
    this.id = paramInt1;
    this.type = paramInt2;
    this.representations = Collections.unmodifiableList(paramList);
    if (paramList1 == null)
    {
      paramList = Collections.emptyList();
      this.accessibilityDescriptors = paramList;
      if (paramList2 != null) {
        break label60;
      }
    }
    label60:
    for (paramList = Collections.emptyList();; paramList = Collections.unmodifiableList(paramList2))
    {
      this.supplementalProperties = paramList;
      return;
      paramList = Collections.unmodifiableList(paramList1);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/manifest/AdaptationSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */