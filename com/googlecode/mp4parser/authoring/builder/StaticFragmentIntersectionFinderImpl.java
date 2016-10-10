package com.googlecode.mp4parser.authoring.builder;

import com.googlecode.mp4parser.authoring.Track;
import java.util.Map;

public class StaticFragmentIntersectionFinderImpl
  implements FragmentIntersectionFinder
{
  Map<Track, long[]> sampleNumbers;
  
  public StaticFragmentIntersectionFinderImpl(Map<Track, long[]> paramMap)
  {
    this.sampleNumbers = paramMap;
  }
  
  public long[] sampleNumbers(Track paramTrack)
  {
    return (long[])this.sampleNumbers.get(paramTrack);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/builder/StaticFragmentIntersectionFinderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */