package com.googlecode.mp4parser.h264.model;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BitstreamElement
{
  public abstract void write(OutputStream paramOutputStream)
    throws IOException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/h264/model/BitstreamElement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */