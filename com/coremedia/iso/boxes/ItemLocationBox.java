package com.coremedia.iso.boxes;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeReaderVariable;
import com.coremedia.iso.IsoTypeWriter;
import com.coremedia.iso.IsoTypeWriterVariable;
import com.googlecode.mp4parser.AbstractFullBox;
import com.googlecode.mp4parser.RequiresParseDetailAspect;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.runtime.internal.Conversions;
import org.aspectj.runtime.reflect.Factory;

public class ItemLocationBox
  extends AbstractFullBox
{
  public static final String TYPE = "iloc";
  public int baseOffsetSize = 8;
  public int indexSize = 0;
  public List<Item> items = new LinkedList();
  public int lengthSize = 8;
  public int offsetSize = 8;
  
  static {}
  
  public ItemLocationBox()
  {
    super("iloc");
  }
  
  public void _parseDetails(ByteBuffer paramByteBuffer)
  {
    parseVersionAndFlags(paramByteBuffer);
    int i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.offsetSize = (i >>> 4);
    this.lengthSize = (i & 0xF);
    i = IsoTypeReader.readUInt8(paramByteBuffer);
    this.baseOffsetSize = (i >>> 4);
    if (getVersion() == 1) {
      this.indexSize = (i & 0xF);
    }
    int j = IsoTypeReader.readUInt16(paramByteBuffer);
    i = 0;
    for (;;)
    {
      if (i >= j) {
        return;
      }
      this.items.add(new Item(paramByteBuffer));
      i += 1;
    }
  }
  
  public Extent createExtent(long paramLong1, long paramLong2, long paramLong3)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_11, this, this, new Object[] { Conversions.longObject(paramLong1), Conversions.longObject(paramLong2), Conversions.longObject(paramLong3) });
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return new Extent(paramLong1, paramLong2, paramLong3);
  }
  
  Extent createExtent(ByteBuffer paramByteBuffer)
  {
    return new Extent(paramByteBuffer);
  }
  
  public Item createItem(int paramInt1, int paramInt2, int paramInt3, long paramLong, List<Extent> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_10, this, this, new Object[] { Conversions.intObject(paramInt1), Conversions.intObject(paramInt2), Conversions.intObject(paramInt3), Conversions.longObject(paramLong), paramList });
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return new Item(paramInt1, paramInt2, paramInt3, paramLong, paramList);
  }
  
  Item createItem(ByteBuffer paramByteBuffer)
  {
    return new Item(paramByteBuffer);
  }
  
  public int getBaseOffsetSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_4, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.baseOffsetSize;
  }
  
  protected void getContent(ByteBuffer paramByteBuffer)
  {
    writeVersionAndFlags(paramByteBuffer);
    IsoTypeWriter.writeUInt8(paramByteBuffer, this.offsetSize << 4 | this.lengthSize);
    Iterator localIterator;
    if (getVersion() == 1)
    {
      IsoTypeWriter.writeUInt8(paramByteBuffer, this.baseOffsetSize << 4 | this.indexSize);
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.items.size());
      localIterator = this.items.iterator();
    }
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        return;
        IsoTypeWriter.writeUInt8(paramByteBuffer, this.baseOffsetSize << 4);
        break;
      }
      ((Item)localIterator.next()).getContent(paramByteBuffer);
    }
  }
  
  protected long getContentSize()
  {
    long l = 8L;
    Iterator localIterator = this.items.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return l;
      }
      l += ((Item)localIterator.next()).getSize();
    }
  }
  
  public int getIndexSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_6, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.indexSize;
  }
  
  public List<Item> getItems()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_8, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.items;
  }
  
  public int getLengthSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_2, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.lengthSize;
  }
  
  public int getOffsetSize()
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_0, this, this);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    return this.offsetSize;
  }
  
  public void setBaseOffsetSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_5, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.baseOffsetSize = paramInt;
  }
  
  public void setIndexSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_7, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.indexSize = paramInt;
  }
  
  public void setItems(List<Item> paramList)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_9, this, this, paramList);
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.items = paramList;
  }
  
  public void setLengthSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_3, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.lengthSize = paramInt;
  }
  
  public void setOffsetSize(int paramInt)
  {
    JoinPoint localJoinPoint = Factory.makeJP(ajc$tjp_1, this, this, Conversions.intObject(paramInt));
    RequiresParseDetailAspect.aspectOf().before(localJoinPoint);
    this.offsetSize = paramInt;
  }
  
  public class Extent
  {
    public long extentIndex;
    public long extentLength;
    public long extentOffset;
    
    public Extent(long paramLong1, long paramLong2, long paramLong3)
    {
      this.extentOffset = paramLong1;
      this.extentLength = paramLong2;
      this.extentIndex = paramLong3;
    }
    
    public Extent(ByteBuffer paramByteBuffer)
    {
      if ((ItemLocationBox.this.getVersion() == 1) && (ItemLocationBox.this.indexSize > 0)) {
        this.extentIndex = IsoTypeReaderVariable.read(paramByteBuffer, ItemLocationBox.this.indexSize);
      }
      this.extentOffset = IsoTypeReaderVariable.read(paramByteBuffer, ItemLocationBox.this.offsetSize);
      this.extentLength = IsoTypeReaderVariable.read(paramByteBuffer, ItemLocationBox.this.lengthSize);
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        paramObject = (Extent)paramObject;
        if (this.extentIndex != ((Extent)paramObject).extentIndex) {
          return false;
        }
        if (this.extentLength != ((Extent)paramObject).extentLength) {
          return false;
        }
      } while (this.extentOffset == ((Extent)paramObject).extentOffset);
      return false;
    }
    
    public void getContent(ByteBuffer paramByteBuffer)
    {
      if ((ItemLocationBox.this.getVersion() == 1) && (ItemLocationBox.this.indexSize > 0)) {
        IsoTypeWriterVariable.write(this.extentIndex, paramByteBuffer, ItemLocationBox.this.indexSize);
      }
      IsoTypeWriterVariable.write(this.extentOffset, paramByteBuffer, ItemLocationBox.this.offsetSize);
      IsoTypeWriterVariable.write(this.extentLength, paramByteBuffer, ItemLocationBox.this.lengthSize);
    }
    
    public int getSize()
    {
      if (ItemLocationBox.this.indexSize > 0) {}
      for (int i = ItemLocationBox.this.indexSize;; i = 0) {
        return i + ItemLocationBox.this.offsetSize + ItemLocationBox.this.lengthSize;
      }
    }
    
    public int hashCode()
    {
      return ((int)(this.extentOffset ^ this.extentOffset >>> 32) * 31 + (int)(this.extentLength ^ this.extentLength >>> 32)) * 31 + (int)(this.extentIndex ^ this.extentIndex >>> 32);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Extent");
      localStringBuilder.append("{extentOffset=").append(this.extentOffset);
      localStringBuilder.append(", extentLength=").append(this.extentLength);
      localStringBuilder.append(", extentIndex=").append(this.extentIndex);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  public class Item
  {
    public long baseOffset;
    public int constructionMethod;
    public int dataReferenceIndex;
    public List<ItemLocationBox.Extent> extents = new LinkedList();
    public int itemId;
    
    public Item(int paramInt1, int paramInt2, long paramLong, List<ItemLocationBox.Extent> paramList)
    {
      this.itemId = paramInt1;
      this.constructionMethod = paramInt2;
      this.dataReferenceIndex = paramLong;
      this.baseOffset = ???;
      List localList;
      this.extents = localList;
    }
    
    public Item(ByteBuffer paramByteBuffer)
    {
      this.itemId = IsoTypeReader.readUInt16(paramByteBuffer);
      if (ItemLocationBox.this.getVersion() == 1) {
        this.constructionMethod = (IsoTypeReader.readUInt16(paramByteBuffer) & 0xF);
      }
      this.dataReferenceIndex = IsoTypeReader.readUInt16(paramByteBuffer);
      int j;
      int i;
      if (ItemLocationBox.this.baseOffsetSize > 0)
      {
        this.baseOffset = IsoTypeReaderVariable.read(paramByteBuffer, ItemLocationBox.this.baseOffsetSize);
        j = IsoTypeReader.readUInt16(paramByteBuffer);
        i = 0;
      }
      for (;;)
      {
        if (i >= j)
        {
          return;
          this.baseOffset = 0L;
          break;
        }
        this.extents.add(new ItemLocationBox.Extent(ItemLocationBox.this, paramByteBuffer));
        i += 1;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass())) {
          return false;
        }
        paramObject = (Item)paramObject;
        if (this.baseOffset != ((Item)paramObject).baseOffset) {
          return false;
        }
        if (this.constructionMethod != ((Item)paramObject).constructionMethod) {
          return false;
        }
        if (this.dataReferenceIndex != ((Item)paramObject).dataReferenceIndex) {
          return false;
        }
        if (this.itemId != ((Item)paramObject).itemId) {
          return false;
        }
        if (this.extents == null) {
          break;
        }
      } while (this.extents.equals(((Item)paramObject).extents));
      for (;;)
      {
        return false;
        if (((Item)paramObject).extents == null) {
          break;
        }
      }
    }
    
    public void getContent(ByteBuffer paramByteBuffer)
    {
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.itemId);
      if (ItemLocationBox.this.getVersion() == 1) {
        IsoTypeWriter.writeUInt16(paramByteBuffer, this.constructionMethod);
      }
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.dataReferenceIndex);
      if (ItemLocationBox.this.baseOffsetSize > 0) {
        IsoTypeWriterVariable.write(this.baseOffset, paramByteBuffer, ItemLocationBox.this.baseOffsetSize);
      }
      IsoTypeWriter.writeUInt16(paramByteBuffer, this.extents.size());
      Iterator localIterator = this.extents.iterator();
      for (;;)
      {
        if (!localIterator.hasNext()) {
          return;
        }
        ((ItemLocationBox.Extent)localIterator.next()).getContent(paramByteBuffer);
      }
    }
    
    public int getSize()
    {
      int i = 2;
      if (ItemLocationBox.this.getVersion() == 1) {
        i = 2 + 2;
      }
      i = i + 2 + ItemLocationBox.this.baseOffsetSize + 2;
      Iterator localIterator = this.extents.iterator();
      for (;;)
      {
        if (!localIterator.hasNext()) {
          return i;
        }
        i += ((ItemLocationBox.Extent)localIterator.next()).getSize();
      }
    }
    
    public int hashCode()
    {
      int j = this.itemId;
      int k = this.constructionMethod;
      int m = this.dataReferenceIndex;
      int n = (int)(this.baseOffset ^ this.baseOffset >>> 32);
      if (this.extents != null) {}
      for (int i = this.extents.hashCode();; i = 0) {
        return (((j * 31 + k) * 31 + m) * 31 + n) * 31 + i;
      }
    }
    
    public void setBaseOffset(long paramLong)
    {
      this.baseOffset = paramLong;
    }
    
    public String toString()
    {
      return "Item{baseOffset=" + this.baseOffset + ", itemId=" + this.itemId + ", constructionMethod=" + this.constructionMethod + ", dataReferenceIndex=" + this.dataReferenceIndex + ", extents=" + this.extents + '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/coremedia/iso/boxes/ItemLocationBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */