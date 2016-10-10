package com.googlecode.mp4parser.authoring.builder;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;

public abstract interface Mp4Builder
{
  public abstract Container build(Movie paramMovie);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/authoring/builder/Mp4Builder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */