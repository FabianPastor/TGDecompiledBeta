package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class SpliceScheduleCommand
  extends SpliceCommand
{
  public static final Parcelable.Creator<SpliceScheduleCommand> CREATOR = new Parcelable.Creator()
  {
    public SpliceScheduleCommand createFromParcel(Parcel paramAnonymousParcel)
    {
      return new SpliceScheduleCommand(paramAnonymousParcel, null);
    }
    
    public SpliceScheduleCommand[] newArray(int paramAnonymousInt)
    {
      return new SpliceScheduleCommand[paramAnonymousInt];
    }
  };
  public final List<Event> events;
  
  private SpliceScheduleCommand(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++) {
      localArrayList.add(Event.createFromParcel(paramParcel));
    }
    this.events = Collections.unmodifiableList(localArrayList);
  }
  
  private SpliceScheduleCommand(List<Event> paramList)
  {
    this.events = Collections.unmodifiableList(paramList);
  }
  
  static SpliceScheduleCommand parseFromSection(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.readUnsignedByte();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; j < i; j++) {
      localArrayList.add(Event.parseFromSection(paramParsableByteArray));
    }
    return new SpliceScheduleCommand(localArrayList);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = this.events.size();
    paramParcel.writeInt(i);
    for (paramInt = 0; paramInt < i; paramInt++) {
      ((Event)this.events.get(paramInt)).writeToParcel(paramParcel);
    }
  }
  
  public static final class ComponentSplice
  {
    public final int componentTag;
    public final long utcSpliceTime;
    
    private ComponentSplice(int paramInt, long paramLong)
    {
      this.componentTag = paramInt;
      this.utcSpliceTime = paramLong;
    }
    
    private static ComponentSplice createFromParcel(Parcel paramParcel)
    {
      return new ComponentSplice(paramParcel.readInt(), paramParcel.readLong());
    }
    
    private void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeInt(this.componentTag);
      paramParcel.writeLong(this.utcSpliceTime);
    }
  }
  
  public static final class Event
  {
    public final boolean autoReturn;
    public final int availNum;
    public final int availsExpected;
    public final long breakDurationUs;
    public final List<SpliceScheduleCommand.ComponentSplice> componentSpliceList;
    public final boolean outOfNetworkIndicator;
    public final boolean programSpliceFlag;
    public final boolean spliceEventCancelIndicator;
    public final long spliceEventId;
    public final int uniqueProgramId;
    public final long utcSpliceTime;
    
    private Event(long paramLong1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, List<SpliceScheduleCommand.ComponentSplice> paramList, long paramLong2, boolean paramBoolean4, long paramLong3, int paramInt1, int paramInt2, int paramInt3)
    {
      this.spliceEventId = paramLong1;
      this.spliceEventCancelIndicator = paramBoolean1;
      this.outOfNetworkIndicator = paramBoolean2;
      this.programSpliceFlag = paramBoolean3;
      this.componentSpliceList = Collections.unmodifiableList(paramList);
      this.utcSpliceTime = paramLong2;
      this.autoReturn = paramBoolean4;
      this.breakDurationUs = paramLong3;
      this.uniqueProgramId = paramInt1;
      this.availNum = paramInt2;
      this.availsExpected = paramInt3;
    }
    
    private Event(Parcel paramParcel)
    {
      this.spliceEventId = paramParcel.readLong();
      if (paramParcel.readByte() == 1)
      {
        bool2 = true;
        this.spliceEventCancelIndicator = bool2;
        if (paramParcel.readByte() != 1) {
          break label107;
        }
        bool2 = true;
        label39:
        this.outOfNetworkIndicator = bool2;
        if (paramParcel.readByte() != 1) {
          break label112;
        }
      }
      ArrayList localArrayList;
      label107:
      label112:
      for (boolean bool2 = true;; bool2 = false)
      {
        this.programSpliceFlag = bool2;
        int i = paramParcel.readInt();
        localArrayList = new ArrayList(i);
        for (int j = 0; j < i; j++) {
          localArrayList.add(SpliceScheduleCommand.ComponentSplice.createFromParcel(paramParcel));
        }
        bool2 = false;
        break;
        bool2 = false;
        break label39;
      }
      this.componentSpliceList = Collections.unmodifiableList(localArrayList);
      this.utcSpliceTime = paramParcel.readLong();
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
    
    private static Event createFromParcel(Parcel paramParcel)
    {
      return new Event(paramParcel);
    }
    
    private static Event parseFromSection(ParsableByteArray paramParsableByteArray)
    {
      long l1 = paramParsableByteArray.readUnsignedInt();
      boolean bool1;
      boolean bool2;
      boolean bool3;
      long l2;
      Object localObject1;
      int i;
      int j;
      boolean bool5;
      long l3;
      Object localObject2;
      long l4;
      long l5;
      if ((paramParsableByteArray.readUnsignedByte() & 0x80) != 0)
      {
        bool1 = true;
        bool2 = false;
        bool3 = false;
        l2 = -9223372036854775807L;
        localObject1 = new ArrayList();
        i = 0;
        j = 0;
        k = 0;
        bool4 = false;
        bool5 = false;
        l3 = -9223372036854775807L;
        localObject2 = localObject1;
        l4 = l2;
        l5 = l3;
        if (bool1) {
          break label294;
        }
        k = paramParsableByteArray.readUnsignedByte();
        if ((k & 0x80) == 0) {
          break label198;
        }
        bool2 = true;
        label92:
        if ((k & 0x40) == 0) {
          break label204;
        }
        bool3 = true;
        label103:
        if ((k & 0x20) == 0) {
          break label210;
        }
      }
      label198:
      label204:
      label210:
      for (int k = 1;; k = 0)
      {
        l4 = l2;
        if (bool3) {
          l4 = paramParsableByteArray.readUnsignedInt();
        }
        if (bool3) {
          break label216;
        }
        j = paramParsableByteArray.readUnsignedByte();
        localObject2 = new ArrayList(j);
        for (i = 0;; i++)
        {
          localObject1 = localObject2;
          if (i >= j) {
            break;
          }
          ((ArrayList)localObject2).add(new SpliceScheduleCommand.ComponentSplice(paramParsableByteArray.readUnsignedByte(), paramParsableByteArray.readUnsignedInt(), null));
        }
        bool1 = false;
        break;
        bool2 = false;
        break label92;
        bool3 = false;
        break label103;
      }
      label216:
      boolean bool4 = bool5;
      if (k != 0)
      {
        l3 = paramParsableByteArray.readUnsignedByte();
        if ((0x80 & l3) == 0L) {
          break label322;
        }
      }
      label294:
      label322:
      for (bool4 = true;; bool4 = false)
      {
        l3 = 1000L * ((1L & l3) << 32 | paramParsableByteArray.readUnsignedInt()) / 90L;
        i = paramParsableByteArray.readUnsignedShort();
        j = paramParsableByteArray.readUnsignedByte();
        k = paramParsableByteArray.readUnsignedByte();
        l5 = l3;
        localObject2 = localObject1;
        return new Event(l1, bool1, bool2, bool3, (List)localObject2, l4, bool4, l5, i, j, k);
      }
    }
    
    private void writeToParcel(Parcel paramParcel)
    {
      int i = 1;
      paramParcel.writeLong(this.spliceEventId);
      if (this.spliceEventCancelIndicator)
      {
        j = 1;
        paramParcel.writeByte((byte)j);
        if (!this.outOfNetworkIndicator) {
          break label108;
        }
        j = 1;
        label34:
        paramParcel.writeByte((byte)j);
        if (!this.programSpliceFlag) {
          break label113;
        }
      }
      label108:
      label113:
      for (int j = 1;; j = 0)
      {
        paramParcel.writeByte((byte)j);
        int k = this.componentSpliceList.size();
        paramParcel.writeInt(k);
        for (j = 0; j < k; j++) {
          ((SpliceScheduleCommand.ComponentSplice)this.componentSpliceList.get(j)).writeToParcel(paramParcel);
        }
        j = 0;
        break;
        j = 0;
        break label34;
      }
      paramParcel.writeLong(this.utcSpliceTime);
      if (this.autoReturn) {}
      for (j = i;; j = 0)
      {
        paramParcel.writeByte((byte)j);
        paramParcel.writeLong(this.breakDurationUs);
        paramParcel.writeInt(this.uniqueProgramId);
        paramParcel.writeInt(this.availNum);
        paramParcel.writeInt(this.availsExpected);
        return;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/scte35/SpliceScheduleCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */