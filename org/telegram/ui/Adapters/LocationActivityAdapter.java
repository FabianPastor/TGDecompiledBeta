package org.telegram.ui.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.LocationActivity.LiveLocation;

public class LocationActivityAdapter
  extends BaseLocationAdapter
{
  private int currentAccount = UserConfig.selectedAccount;
  private ArrayList<LocationActivity.LiveLocation> currentLiveLocations = new ArrayList();
  private MessageObject currentMessageObject;
  private Location customLocation;
  private long dialogId;
  private Location gpsLocation;
  private int liveLocationType;
  private Context mContext;
  private int overScrollHeight;
  private boolean pulledUp;
  private SendLocationCell sendLocationCell;
  private int shareLiveLocationPotistion = -1;
  
  public LocationActivityAdapter(Context paramContext, int paramInt, long paramLong)
  {
    this.mContext = paramContext;
    this.liveLocationType = paramInt;
    this.dialogId = paramLong;
  }
  
  private void updateCell()
  {
    if (this.sendLocationCell != null)
    {
      if (this.customLocation == null) {
        break label67;
      }
      this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", NUM), String.format(Locale.US, "(%f,%f)", new Object[] { Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude()) }));
    }
    for (;;)
    {
      return;
      label67:
      if (this.gpsLocation != null) {
        this.sendLocationCell.setText(LocaleController.getString("SendLocation", NUM), LocaleController.formatString("AccurateTo", NUM, new Object[] { LocaleController.formatPluralString("Meters", (int)this.gpsLocation.getAccuracy()) }));
      } else {
        this.sendLocationCell.setText(LocaleController.getString("SendLocation", NUM), LocaleController.getString("Loading", NUM));
      }
    }
  }
  
  public Object getItem(int paramInt)
  {
    Object localObject1 = null;
    Object localObject2;
    if (this.currentMessageObject != null) {
      if (paramInt == 1) {
        localObject2 = this.currentMessageObject;
      }
    }
    for (;;)
    {
      return localObject2;
      localObject2 = localObject1;
      if (paramInt > 3)
      {
        localObject2 = localObject1;
        if (paramInt < this.places.size() + 3)
        {
          localObject2 = this.currentLiveLocations.get(paramInt - 4);
          continue;
          if (this.liveLocationType == 2)
          {
            localObject2 = localObject1;
            if (paramInt >= 2) {
              localObject2 = this.currentLiveLocations.get(paramInt - 2);
            }
          }
          else if (this.liveLocationType == 1)
          {
            localObject2 = localObject1;
            if (paramInt > 3)
            {
              localObject2 = localObject1;
              if (paramInt < this.places.size() + 3) {
                localObject2 = this.places.get(paramInt - 4);
              }
            }
          }
          else
          {
            localObject2 = localObject1;
            if (paramInt > 2)
            {
              localObject2 = localObject1;
              if (paramInt < this.places.size() + 2) {
                localObject2 = this.places.get(paramInt - 3);
              }
            }
          }
        }
      }
    }
  }
  
  public int getItemCount()
  {
    int i = 0;
    int j = 0;
    int k = 0;
    if (this.currentMessageObject != null) {
      if (this.currentLiveLocations.isEmpty())
      {
        i = k;
        i += 2;
      }
    }
    for (;;)
    {
      return i;
      i = this.currentLiveLocations.size() + 2;
      break;
      if (this.liveLocationType == 2)
      {
        i = this.currentLiveLocations.size() + 2;
      }
      else
      {
        if ((!this.searching) && ((this.searching) || (!this.places.isEmpty()))) {
          break label105;
        }
        if (this.liveLocationType != 0) {
          i = 5;
        } else {
          i = 4;
        }
      }
    }
    label105:
    if (this.liveLocationType == 1)
    {
      j = this.places.size();
      if (this.places.isEmpty()) {}
      for (;;)
      {
        i += j + 4;
        break;
        i = 1;
      }
    }
    k = this.places.size();
    if (this.places.isEmpty()) {}
    for (i = j;; i = 1)
    {
      i += k + 3;
      break;
    }
  }
  
  public int getItemViewType(int paramInt)
  {
    int i = 2;
    if (paramInt == 0) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if (this.currentMessageObject != null)
      {
        if (paramInt != 2) {
          if (paramInt == 3)
          {
            this.shareLiveLocationPotistion = paramInt;
            i = 6;
          }
          else
          {
            i = 7;
          }
        }
      }
      else if (this.liveLocationType == 2)
      {
        if (paramInt == 1)
        {
          this.shareLiveLocationPotistion = paramInt;
          i = 6;
        }
        else
        {
          i = 7;
        }
      }
      else
      {
        if (this.liveLocationType == 1)
        {
          if (paramInt == 1)
          {
            i = 1;
            continue;
          }
          if (paramInt == 2)
          {
            this.shareLiveLocationPotistion = paramInt;
            i = 6;
            continue;
          }
          if (paramInt == 3) {
            continue;
          }
          if ((this.searching) || ((!this.searching) && (this.places.isEmpty())))
          {
            i = 4;
            continue;
          }
          if (paramInt == this.places.size() + 4) {
            i = 5;
          }
        }
        else
        {
          if (paramInt == 1)
          {
            i = 1;
            continue;
          }
          if (paramInt == 2) {
            continue;
          }
          if ((this.searching) || ((!this.searching) && (this.places.isEmpty())))
          {
            i = 4;
            continue;
          }
          if (paramInt == this.places.size() + 3)
          {
            i = 5;
            continue;
          }
        }
        i = 3;
      }
    }
  }
  
  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    boolean bool = false;
    int i = paramViewHolder.getItemViewType();
    if (i == 6) {
      if ((LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null) && (this.gpsLocation == null)) {}
    }
    for (bool = true;; bool = true) {
      do
      {
        return bool;
      } while ((i != 1) && (i != 3) && (i != 7));
    }
  }
  
  public boolean isPulledUp()
  {
    return this.pulledUp;
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    switch (paramViewHolder.getItemViewType())
    {
    }
    for (;;)
    {
      return;
      ((EmptyCell)paramViewHolder.itemView).setHeight(this.overScrollHeight);
      continue;
      this.sendLocationCell = ((SendLocationCell)paramViewHolder.itemView);
      updateCell();
      continue;
      if (this.currentMessageObject != null)
      {
        ((GraySectionCell)paramViewHolder.itemView).setText(LocaleController.getString("LiveLocations", NUM));
      }
      else if (this.pulledUp)
      {
        ((GraySectionCell)paramViewHolder.itemView).setText(LocaleController.getString("NearbyPlaces", NUM));
      }
      else
      {
        ((GraySectionCell)paramViewHolder.itemView).setText(LocaleController.getString("ShowNearbyPlaces", NUM));
        continue;
        if (this.liveLocationType == 0)
        {
          ((LocationCell)paramViewHolder.itemView).setLocation((TLRPC.TL_messageMediaVenue)this.places.get(paramInt - 3), (String)this.iconUrls.get(paramInt - 3), true);
        }
        else
        {
          ((LocationCell)paramViewHolder.itemView).setLocation((TLRPC.TL_messageMediaVenue)this.places.get(paramInt - 4), (String)this.iconUrls.get(paramInt - 4), true);
          continue;
          ((LocationLoadingCell)paramViewHolder.itemView).setLoading(this.searching);
          continue;
          paramViewHolder = (SendLocationCell)paramViewHolder.itemView;
          if (this.gpsLocation != null) {}
          for (boolean bool = true;; bool = false)
          {
            paramViewHolder.setHasLocation(bool);
            break;
          }
          if ((this.currentMessageObject == null) || (paramInt != 1)) {
            break;
          }
          ((SharingLiveLocationCell)paramViewHolder.itemView).setDialog(this.currentMessageObject, this.gpsLocation);
        }
      }
    }
    paramViewHolder = (SharingLiveLocationCell)paramViewHolder.itemView;
    ArrayList localArrayList = this.currentLiveLocations;
    if (this.currentMessageObject != null) {}
    for (int i = 4;; i = 2)
    {
      paramViewHolder.setDialog((LocationActivity.LiveLocation)localArrayList.get(paramInt - i), this.gpsLocation);
      break;
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      paramViewGroup = new SharingLiveLocationCell(this.mContext, true);
    }
    for (;;)
    {
      return new RecyclerListView.Holder(paramViewGroup);
      paramViewGroup = new EmptyCell(this.mContext);
      continue;
      paramViewGroup = new SendLocationCell(this.mContext, false);
      continue;
      paramViewGroup = new GraySectionCell(this.mContext);
      continue;
      paramViewGroup = new LocationCell(this.mContext);
      continue;
      paramViewGroup = new LocationLoadingCell(this.mContext);
      continue;
      paramViewGroup = new LocationPoweredCell(this.mContext);
      continue;
      paramViewGroup = new SendLocationCell(this.mContext, true);
      paramViewGroup.setDialogId(this.dialogId);
    }
  }
  
  public void setCustomLocation(Location paramLocation)
  {
    this.customLocation = paramLocation;
    updateCell();
  }
  
  public void setGpsLocation(Location paramLocation)
  {
    int i;
    if (this.gpsLocation == null)
    {
      i = 1;
      this.gpsLocation = paramLocation;
      if ((i != 0) && (this.shareLiveLocationPotistion > 0)) {
        notifyItemChanged(this.shareLiveLocationPotistion);
      }
      if (this.currentMessageObject == null) {
        break label55;
      }
      notifyItemChanged(1);
      updateLiveLocations();
    }
    for (;;)
    {
      return;
      i = 0;
      break;
      label55:
      if (this.liveLocationType != 2) {
        updateCell();
      } else {
        updateLiveLocations();
      }
    }
  }
  
  public void setLiveLocations(ArrayList<LocationActivity.LiveLocation> paramArrayList)
  {
    this.currentLiveLocations = new ArrayList(paramArrayList);
    int i = UserConfig.getInstance(this.currentAccount).getClientUserId();
    for (int j = 0;; j++) {
      if (j < this.currentLiveLocations.size())
      {
        if (((LocationActivity.LiveLocation)this.currentLiveLocations.get(j)).id == i) {
          this.currentLiveLocations.remove(j);
        }
      }
      else
      {
        notifyDataSetChanged();
        return;
      }
    }
  }
  
  public void setMessageObject(MessageObject paramMessageObject)
  {
    this.currentMessageObject = paramMessageObject;
    notifyDataSetChanged();
  }
  
  public void setOverScrollHeight(int paramInt)
  {
    this.overScrollHeight = paramInt;
  }
  
  public void setPulledUp()
  {
    if (this.pulledUp) {}
    for (;;)
    {
      return;
      this.pulledUp = true;
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          LocationActivityAdapter localLocationActivityAdapter = LocationActivityAdapter.this;
          if (LocationActivityAdapter.this.liveLocationType == 0) {}
          for (int i = 2;; i = 3)
          {
            localLocationActivityAdapter.notifyItemChanged(i);
            return;
          }
        }
      });
    }
  }
  
  public void updateLiveLocations()
  {
    if (!this.currentLiveLocations.isEmpty()) {
      notifyItemRangeChanged(2, this.currentLiveLocations.size());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/LocationActivityAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */