package com.googlecode.mp4parser.authoring.tracks;

import com.googlecode.mp4parser.authoring.Track;
import com.mp4parser.iso23001.part7.CencSampleAuxiliaryDataFormat;
import java.util.List;
import java.util.UUID;

public abstract interface CencEncryptedTrack
  extends Track
{
  public abstract UUID getDefaultKeyId();
  
  public abstract List<CencSampleAuxiliaryDataFormat> getSampleEncryptionEntries();
  
  public abstract boolean hasSubSampleEncryption();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/tracks/CencEncryptedTrack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */