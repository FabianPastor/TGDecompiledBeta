package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class SpliceInsertCommand
  extends SpliceCommand
{
  public static final Parcelable.Creator<SpliceInsertCommand> CREATOR = new Parcelable.Creator()
  {
    public SpliceInsertCommand createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SpliceInsertCommand(paramAnonymousParcel, null);
    }
    
    public SpliceInsertCommand[] newArray(int paramAnonymousInt)
    {
      return new SpliceInsertCommand[paramAnonymousInt];
    }
  };
  public final boolean autoReturn;
  public final int availNum;
  public final int availsExpected;
  public final long breakDurationUs;
  public final List<ComponentSplice> componentSpliceList;
  public final boolean outOfNetworkIndicator;
  public final boolean programSpliceFlag;
  public final long programSplicePlaybackPositionUs;
  public final long programSplicePts;
  public final boolean spliceEventCancelIndicator;
  public final long spliceEventId;
  public final boolean spliceImmediateFlag;
  public final int uniqueProgramId;
  
  private SpliceInsertCommand(long paramLong1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, long paramLong2, long paramLong3, List<ComponentSplice> paramList, boolean paramBoolean5, long paramLong4, int paramInt1, int paramInt2, int paramInt3)
  {
    this.spliceEventId = paramLong1;
    this.spliceEventCancelIndicator = paramBoolean1;
    this.outOfNetworkIndicator = paramBoolean2;
    this.programSpliceFlag = paramBoolean3;
    this.spliceImmediateFlag = paramBoolean4;
    this.programSplicePts = paramLong2;
    this.programSplicePlaybackPositionUs = paramLong3;
    this.componentSpliceList = Collections.unmodifiableList(paramList);
    this.autoReturn = paramBoolean5;
    this.breakDurationUs = paramLong4;
    this.uniqueProgramId = paramInt1;
    this.availNum = paramInt2;
    this.availsExpected = paramInt3;
  }
  
  private SpliceInsertCommand(Parcel paramParcel)
  {
    this.spliceEventId = paramParcel.readLong();
    if (paramParcel.readByte() == 1)
    {
      bool2 = true;
      this.spliceEventCancelIndicator = bool2;
      if (paramParcel.readByte() != 1) {
        break label140;
      }
      bool2 = true;
      label39:
      this.outOfNetworkIndicator = bool2;
      if (paramParcel.readByte() != 1) {
        break label145;
      }
      bool2 = true;
      label54:
      this.programSpliceFlag = bool2;
      if (paramParcel.readByte() != 1) {
        break label150;
      }
    }
    ArrayList localArrayList;
    label140:
    label145:
    label150:
    for (boolean bool2 = true;; bool2 = false)
    {
      this.spliceImmediateFlag = bool2;
      this.programSplicePts = paramParcel.readLong();
      this.programSplicePlaybackPositionUs = paramParcel.readLong();
      int i = paramParcel.readInt();
      localArrayList = new ArrayList(i);
      for (int j = 0; j < i; j++) {
        localArrayList.add(ComponentSplice.createFromParcel(paramParcel));
      }
      bool2 = false;
      break;
      bool2 = false;
      break label39;
      bool2 = false;
      break label54;
    }
    this.componentSpliceList = Collections.unmodifiableList(localArrayList);
    if (paramParcel.readByte() == 1) {}
    for (bool2 = bool1;; bool2 = false)
    {
      this.autoReturn = bool2;
      this.breakDurationUs = paramParcel.readLong();
      this.uniqueProgramId = paramParcel.readInt();
      this.availNum = paramParcel.readInt();
      this.availsExpected = paramParcel.readInt();
      return;
    }
  }
  
  static SpliceInsertCommand parseFromSection(ParsableByteArray paramParsableByteArray, long paramLong, TimestampAdjuster paramTimestampAdjuster)
  {
    long l1 = paramParsableByteArray.readUnsignedInt();
    boolean bool1;
    boolean bool2;
    boolean bool3;
    long l2;
    Object localObject1;
    int i;
    int j;
    int k;
    boolean bool6;
    long l3;
    long l4;
    Object localObject2;
    long l5;
    if ((paramParsableByteArray.readUnsignedByte() & 0x80) != 0)
    {
      bool1 = true;
      bool2 = false;
      bool3 = false;
      bool4 = false;
      l2 = -9223372036854775807L;
      localObject1 = Collections.emptyList();
      i = 0;
      j = 0;
      k = 0;
      bool5 = false;
      bool6 = false;
      l3 = -9223372036854775807L;
      l4 = l2;
      localObject2 = localObject1;
      l5 = l3;
      if (bool1) {
        break label349;
      }
      i = paramParsableByteArray.readUnsignedByte();
      if ((i & 0x80) == 0) {
        break label253;
      }
      bool2 = true;
      label94:
      if ((i & 0x40) == 0) {
        break label259;
      }
      bool3 = true;
      label105:
      if ((i & 0x20) == 0) {
        break label265;
      }
      j = 1;
      label116:
      if ((i & 0x10) == 0) {
        break label271;
      }
    }
    label253:
    label259:
    label265:
    label271:
    for (boolean bool4 = true;; bool4 = false)
    {
      l4 = l2;
      if (bool3)
      {
        l4 = l2;
        if (!bool4) {
          l4 = TimeSignalCommand.parseSpliceTime(paramParsableByteArray, paramLong);
        }
      }
      localObject2 = localObject1;
      if (bool3) {
        break label277;
      }
      k = paramParsableByteArray.readUnsignedByte();
      localObject1 = new ArrayList(k);
      for (i = 0;; i++)
      {
        localObject2 = localObject1;
        if (i >= k) {
          break;
        }
        int m = paramParsableByteArray.readUnsignedByte();
        l5 = -9223372036854775807L;
        if (!bool4) {
          l5 = TimeSignalCommand.parseSpliceTime(paramParsableByteArray, paramLong);
        }
        ((List)localObject1).add(new ComponentSplice(m, l5, paramTimestampAdjuster.adjustTsTimestamp(l5), null));
      }
      bool1 = false;
      break;
      bool2 = false;
      break label94;
      bool3 = false;
      break label105;
      j = 0;
      break label116;
    }
    label277:
    boolean bool5 = bool6;
    paramLong = l3;
    if (j != 0)
    {
      paramLong = paramParsableByteArray.readUnsignedByte();
      if ((0x80 & paramLong) == 0L) {
        break label387;
      }
    }
    label349:
    label387:
    for (bool5 = true;; bool5 = false)
    {
      paramLong = 1000L * ((1L & paramLong) << 32 | paramParsableByteArray.readUnsignedInt()) / 90L;
      i = paramParsableByteArray.readUnsignedShort();
      j = paramParsableByteArray.readUnsignedByte();
      k = paramParsableByteArray.readUnsignedByte();
      l5 = paramLong;
      return new SpliceInsertCommand(l1, bool1, bool2, bool3, bool4, l4, paramTimestampAdjuster.adjustTsTimestamp(l4), (List)localObject2, bool5, l5, i, j, k);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeLong(this.spliceEventId);
    if (this.spliceEventCancelIndicator)
    {
      paramInt = 1;
      paramParcel.writeByte((byte)paramInt);
      if (!this.outOfNetworkIndicator) {
        break label139;
      }
      paramInt = 1;
      label34:
      paramParcel.writeByte((byte)paramInt);
      if (!this.programSpliceFlag) {
        break label144;
      }
      paramInt = 1;
      label49:
      paramParcel.writeByte((byte)paramInt);
      if (!this.spliceImmediateFlag) {
        break label149;
      }
    }
    label139:
    label144:
    label149:
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeLong(this.programSplicePts);
      paramParcel.writeLong(this.programSplicePlaybackPositionUs);
      int j = this.componentSpliceList.size();
      paramParcel.writeInt(j);
      for (paramInt = 0; paramInt < j; paramInt++) {
        ((ComponentSplice)this.componentSpliceList.get(paramInt)).writeToParcel(paramParcel);
      }
      paramInt = 0;
      break;
      paramInt = 0;
      break label34;
      paramInt = 0;
      break label49;
    }
    if (this.autoReturn) {}
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeLong(this.breakDurationUs);
      paramParcel.writeInt(this.uniqueProgramId);
      paramParcel.writeInt(this.availNum);
      paramParcel.writeInt(this.availsExpected);
      return;
    }
  }
  
  public static final class ComponentSplice
  {
    public final long componentSplicePlaybackPositionUs;
    public final long componentSplicePts;
    public final int componentTag;
    
    private ComponentSplice(int paramInt, long paramLong1, long paramLong2)
    {
      this.componentTag = paramInt;
      this.componentSplicePts = paramLong1;
      this.componentSplicePlaybackPositionUs = paramLong2;
    }
    
    public static ComponentSplice createFromParcel(Parcel paramParcel)
    {
      return new ComponentSplice(paramParcel.readInt(), paramParcel.readLong(), paramParcel.readLong());
    }
    
    public void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeInt(this.componentTag);
      paramParcel.writeLong(this.componentSplicePts);
      paramParcel.writeLong(this.componentSplicePlaybackPositionUs);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/scte35/SpliceInsertCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */