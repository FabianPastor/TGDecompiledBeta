package org.telegram.messenger.exoplayer2.extractor.mkv;

import java.io.IOException;
import java.util.Stack;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class DefaultEbmlReader
  implements EbmlReader
{
  private static final int ELEMENT_STATE_READ_CONTENT = 2;
  private static final int ELEMENT_STATE_READ_CONTENT_SIZE = 1;
  private static final int ELEMENT_STATE_READ_ID = 0;
  private static final int MAX_ID_BYTES = 4;
  private static final int MAX_INTEGER_ELEMENT_SIZE_BYTES = 8;
  private static final int MAX_LENGTH_BYTES = 8;
  private static final int VALID_FLOAT32_ELEMENT_SIZE_BYTES = 4;
  private static final int VALID_FLOAT64_ELEMENT_SIZE_BYTES = 8;
  private long elementContentSize;
  private int elementId;
  private int elementState;
  private final Stack<MasterElement> masterElementsStack = new Stack();
  private EbmlReaderOutput output;
  private final byte[] scratch = new byte[8];
  private final VarintReader varintReader = new VarintReader();
  
  private long maybeResyncToNextLevel1Element(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    paramExtractorInput.resetPeekPosition();
    for (;;)
    {
      paramExtractorInput.peekFully(this.scratch, 0, 4);
      int i = VarintReader.parseUnsignedVarintLength(this.scratch[0]);
      if ((i != -1) && (i <= 4))
      {
        int j = (int)VarintReader.assembleVarint(this.scratch, i, false);
        if (this.output.isLevel1Element(j))
        {
          paramExtractorInput.skipFully(i);
          return j;
        }
      }
      paramExtractorInput.skipFully(1);
    }
  }
  
  private double readFloat(ExtractorInput paramExtractorInput, int paramInt)
    throws IOException, InterruptedException
  {
    long l = readInteger(paramExtractorInput, paramInt);
    if (paramInt == 4) {}
    for (double d = Float.intBitsToFloat((int)l);; d = Double.longBitsToDouble(l)) {
      return d;
    }
  }
  
  private long readInteger(ExtractorInput paramExtractorInput, int paramInt)
    throws IOException, InterruptedException
  {
    paramExtractorInput.readFully(this.scratch, 0, paramInt);
    long l = 0L;
    for (int i = 0; i < paramInt; i++) {
      l = l << 8 | this.scratch[i] & 0xFF;
    }
    return l;
  }
  
  private String readString(ExtractorInput paramExtractorInput, int paramInt)
    throws IOException, InterruptedException
  {
    if (paramInt == 0) {}
    byte[] arrayOfByte;
    for (paramExtractorInput = "";; paramExtractorInput = new String(arrayOfByte))
    {
      return paramExtractorInput;
      arrayOfByte = new byte[paramInt];
      paramExtractorInput.readFully(arrayOfByte, 0, paramInt);
    }
  }
  
  public void init(EbmlReaderOutput paramEbmlReaderOutput)
  {
    this.output = paramEbmlReaderOutput;
  }
  
  public boolean read(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool;
    if (this.output != null)
    {
      bool = true;
      Assertions.checkState(bool);
    }
    for (;;)
    {
      if ((!this.masterElementsStack.isEmpty()) && (paramExtractorInput.getPosition() >= ((MasterElement)this.masterElementsStack.peek()).elementEndPosition))
      {
        this.output.endMasterElement(((MasterElement)this.masterElementsStack.pop()).elementId);
        bool = true;
      }
      for (;;)
      {
        return bool;
        bool = false;
        break;
        long l1;
        long l2;
        if (this.elementState == 0)
        {
          l1 = this.varintReader.readUnsignedVarint(paramExtractorInput, true, false, 4);
          l2 = l1;
          if (l1 == -2L) {
            l2 = maybeResyncToNextLevel1Element(paramExtractorInput);
          }
          if (l2 == -1L)
          {
            bool = false;
          }
          else
          {
            this.elementId = ((int)l2);
            this.elementState = 1;
          }
        }
        else
        {
          if (this.elementState == 1)
          {
            this.elementContentSize = this.varintReader.readUnsignedVarint(paramExtractorInput, false, true, 8);
            this.elementState = 2;
          }
          int i = this.output.getElementType(this.elementId);
          switch (i)
          {
          default: 
            throw new ParserException("Invalid element type " + i);
          case 1: 
            l1 = paramExtractorInput.getPosition();
            l2 = this.elementContentSize;
            this.masterElementsStack.add(new MasterElement(this.elementId, l1 + l2, null));
            this.output.startMasterElement(this.elementId, l1, this.elementContentSize);
            this.elementState = 0;
            bool = true;
            break;
          case 2: 
            if (this.elementContentSize > 8L) {
              throw new ParserException("Invalid integer size: " + this.elementContentSize);
            }
            this.output.integerElement(this.elementId, readInteger(paramExtractorInput, (int)this.elementContentSize));
            this.elementState = 0;
            bool = true;
            break;
          case 5: 
            if ((this.elementContentSize != 4L) && (this.elementContentSize != 8L)) {
              throw new ParserException("Invalid float size: " + this.elementContentSize);
            }
            this.output.floatElement(this.elementId, readFloat(paramExtractorInput, (int)this.elementContentSize));
            this.elementState = 0;
            bool = true;
            break;
          case 3: 
            if (this.elementContentSize > 2147483647L) {
              throw new ParserException("String element size: " + this.elementContentSize);
            }
            this.output.stringElement(this.elementId, readString(paramExtractorInput, (int)this.elementContentSize));
            this.elementState = 0;
            bool = true;
            break;
          case 4: 
            this.output.binaryElement(this.elementId, (int)this.elementContentSize, paramExtractorInput);
            this.elementState = 0;
            bool = true;
          }
        }
      }
      paramExtractorInput.skipFully((int)this.elementContentSize);
      this.elementState = 0;
    }
  }
  
  public void reset()
  {
    this.elementState = 0;
    this.masterElementsStack.clear();
    this.varintReader.reset();
  }
  
  private static final class MasterElement
  {
    private final long elementEndPosition;
    private final int elementId;
    
    private MasterElement(int paramInt, long paramLong)
    {
      this.elementId = paramInt;
      this.elementEndPosition = paramLong;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mkv/DefaultEbmlReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */