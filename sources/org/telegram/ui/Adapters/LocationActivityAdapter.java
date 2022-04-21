package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationDirectionCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LocationActivity;

public class LocationActivityAdapter extends BaseLocationAdapter implements LocationController.LocationFetchCallback {
    private String addressName;
    private TLRPC.TL_channelLocation chatLocation;
    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<LocationActivity.LiveLocation> currentLiveLocations = new ArrayList<>();
    private MessageObject currentMessageObject;
    private Location customLocation;
    private long dialogId;
    private FrameLayout emptyCell;
    private boolean fetchingLocation;
    private FlickerLoadingView globalGradientView;
    private Location gpsLocation;
    private int locationType;
    private Context mContext;
    private boolean myLocationDenied = false;
    private boolean needEmptyView;
    private int overScrollHeight;
    private Location previousFetchedLocation;
    private final Theme.ResourcesProvider resourcesProvider;
    private SendLocationCell sendLocationCell;
    private int shareLiveLocationPotistion = -1;
    private Runnable updateRunnable;

    public LocationActivityAdapter(Context context, int type, long did, boolean emptyView, Theme.ResourcesProvider resourcesProvider2) {
        this.mContext = context;
        this.locationType = type;
        this.dialogId = did;
        this.needEmptyView = emptyView;
        this.resourcesProvider = resourcesProvider2;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.globalGradientView = flickerLoadingView;
        flickerLoadingView.setIsSingleCell(true);
    }

    public void setMyLocationDenied(boolean myLocationDenied2) {
        if (this.myLocationDenied != myLocationDenied2) {
            this.myLocationDenied = myLocationDenied2;
            notifyDataSetChanged();
        }
    }

    public void setOverScrollHeight(int value) {
        this.overScrollHeight = value;
        FrameLayout frameLayout = this.emptyCell;
        if (frameLayout != null) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) frameLayout.getLayoutParams();
            if (lp == null) {
                lp = new RecyclerView.LayoutParams(-1, this.overScrollHeight);
            } else {
                lp.height = this.overScrollHeight;
            }
            this.emptyCell.setLayoutParams(lp);
            this.emptyCell.forceLayout();
        }
    }

    public void setUpdateRunnable(Runnable runnable) {
        this.updateRunnable = runnable;
    }

    public void setGpsLocation(Location location) {
        int i;
        boolean notSet = this.gpsLocation == null;
        this.gpsLocation = location;
        if (this.customLocation == null) {
            fetchLocationAddress();
        }
        if (notSet && (i = this.shareLiveLocationPotistion) > 0) {
            notifyItemChanged(i);
        }
        if (this.currentMessageObject != null) {
            notifyItemChanged(1, new Object());
            updateLiveLocations();
        } else if (this.locationType != 2) {
            updateCell();
        } else {
            updateLiveLocations();
        }
    }

    public void updateLiveLocationCell() {
        int i = this.shareLiveLocationPotistion;
        if (i > 0) {
            notifyItemChanged(i);
        }
    }

    public void updateLiveLocations() {
        if (!this.currentLiveLocations.isEmpty()) {
            notifyItemRangeChanged(2, this.currentLiveLocations.size(), new Object());
        }
    }

    public void setCustomLocation(Location location) {
        this.customLocation = location;
        fetchLocationAddress();
        updateCell();
    }

    public void setLiveLocations(ArrayList<LocationActivity.LiveLocation> liveLocations) {
        this.currentLiveLocations = new ArrayList<>(liveLocations);
        long uid = UserConfig.getInstance(this.currentAccount).getClientUserId();
        int a = 0;
        while (true) {
            if (a >= this.currentLiveLocations.size()) {
                break;
            } else if (this.currentLiveLocations.get(a).id == uid || this.currentLiveLocations.get(a).object.out) {
                this.currentLiveLocations.remove(a);
            } else {
                a++;
            }
        }
        notifyDataSetChanged();
    }

    public void setMessageObject(MessageObject messageObject) {
        this.currentMessageObject = messageObject;
        notifyDataSetChanged();
    }

    public void setChatLocation(TLRPC.TL_channelLocation location) {
        this.chatLocation = location;
    }

    private void updateCell() {
        SendLocationCell sendLocationCell2 = this.sendLocationCell;
        if (sendLocationCell2 == null) {
            return;
        }
        if (this.locationType == 4 || this.customLocation != null) {
            String address = "";
            if (!TextUtils.isEmpty(this.addressName)) {
                address = this.addressName;
            } else {
                Location location = this.customLocation;
                if ((location == null && this.gpsLocation == null) || this.fetchingLocation) {
                    address = LocaleController.getString("Loading", NUM);
                } else if (location != null) {
                    address = String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude())});
                } else if (this.gpsLocation != null) {
                    address = String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.gpsLocation.getLatitude()), Double.valueOf(this.gpsLocation.getLongitude())});
                } else if (!this.myLocationDenied) {
                    address = LocaleController.getString("Loading", NUM);
                }
            }
            if (this.locationType == 4) {
                this.sendLocationCell.setText(LocaleController.getString("ChatSetThisLocation", NUM), address);
            } else {
                this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", NUM), address);
            }
            this.sendLocationCell.setHasLocation(true);
        } else if (this.gpsLocation != null) {
            sendLocationCell2.setText(LocaleController.getString("SendLocation", NUM), LocaleController.formatString("AccurateTo", NUM, LocaleController.formatPluralString("Meters", (int) this.gpsLocation.getAccuracy())));
            this.sendLocationCell.setHasLocation(true);
        } else {
            sendLocationCell2.setText(LocaleController.getString("SendLocation", NUM), this.myLocationDenied ? "" : LocaleController.getString("Loading", NUM));
            this.sendLocationCell.setHasLocation(!this.myLocationDenied);
        }
    }

    private String getAddressName() {
        return this.addressName;
    }

    public void onLocationAddressAvailable(String address, String displayAddress, Location location) {
        this.fetchingLocation = false;
        this.previousFetchedLocation = location;
        this.addressName = address;
        updateCell();
    }

    /* access modifiers changed from: protected */
    public void onDirectionClick() {
    }

    public void fetchLocationAddress() {
        Location location;
        if (this.locationType == 4) {
            if (this.customLocation != null) {
                location = this.customLocation;
            } else if (this.gpsLocation != null) {
                location = this.gpsLocation;
            } else {
                return;
            }
            Location location2 = this.previousFetchedLocation;
            if (location2 == null || location2.distanceTo(location) > 100.0f) {
                this.addressName = null;
            }
            this.fetchingLocation = true;
            updateCell();
            LocationController.fetchLocationAddress(location, this);
        } else if (this.customLocation != null) {
            Location location3 = this.customLocation;
            Location location4 = this.previousFetchedLocation;
            if (location4 == null || location4.distanceTo(location3) > 20.0f) {
                this.addressName = null;
            }
            this.fetchingLocation = true;
            updateCell();
            LocationController.fetchLocationAddress(location3, this);
        }
    }

    public int getItemCount() {
        int i = this.locationType;
        int i2 = 6;
        int i3 = 2;
        if (i == 6 || i == 5 || i == 4) {
            return 2;
        }
        int i4 = 1;
        if (this.currentMessageObject != null) {
            if (!this.currentLiveLocations.isEmpty()) {
                i4 = this.currentLiveLocations.size() + 3;
            }
            return i4 + 2;
        } else if (i == 2) {
            return this.currentLiveLocations.size() + 2;
        } else {
            if (this.searching || !this.searched || this.places.isEmpty()) {
                if (this.locationType == 0) {
                    i2 = 5;
                }
                int i5 = i2 + ((this.myLocationDenied || (!this.searching && this.searched)) ? 0 : 2) + (this.needEmptyView ? 1 : 0);
                if (!this.myLocationDenied) {
                    i3 = 0;
                }
                return i5 - i3;
            }
            if (this.locationType != 1) {
                i2 = 5;
            }
            return i2 + this.places.size() + (this.needEmptyView ? 1 : 0);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                FrameLayout frameLayout = new FrameLayout(this.mContext);
                this.emptyCell = frameLayout;
                FrameLayout frameLayout2 = frameLayout;
                frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, this.overScrollHeight));
                view = frameLayout;
                break;
            case 1:
                view = new SendLocationCell(this.mContext, false, this.resourcesProvider);
                break;
            case 2:
                view = new HeaderCell(this.mContext, this.resourcesProvider);
                break;
            case 3:
                view = new LocationCell(this.mContext, false, this.resourcesProvider);
                break;
            case 4:
                view = new LocationLoadingCell(this.mContext, this.resourcesProvider);
                break;
            case 5:
                view = new LocationPoweredCell(this.mContext, this.resourcesProvider);
                break;
            case 6:
                SendLocationCell cell = new SendLocationCell(this.mContext, true, this.resourcesProvider);
                cell.setDialogId(this.dialogId);
                SendLocationCell sendLocationCell2 = cell;
                view = cell;
                break;
            case 7:
                Context context = this.mContext;
                int i = this.locationType;
                view = new SharingLiveLocationCell(context, true, (i == 4 || i == 5) ? 16 : 54, this.resourcesProvider);
                break;
            case 8:
                LocationDirectionCell cell2 = new LocationDirectionCell(this.mContext, this.resourcesProvider);
                cell2.setOnButtonClick(new LocationActivityAdapter$$ExternalSyntheticLambda0(this));
                LocationDirectionCell locationDirectionCell = cell2;
                view = cell2;
                break;
            case 9:
                View view2 = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(getThemedColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                combinedDrawable.setFullsize(true);
                view2.setBackgroundDrawable(combinedDrawable);
                view = view2;
                break;
            default:
                view = new View(this.mContext);
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Adapters-LocationActivityAdapter  reason: not valid java name */
    public /* synthetic */ void m1332x64aa3647(View v) {
        onDirectionClick();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int position2;
        boolean z = true;
        switch (holder.getItemViewType()) {
            case 0:
                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                if (lp == null) {
                    lp = new RecyclerView.LayoutParams(-1, this.overScrollHeight);
                } else {
                    lp.height = this.overScrollHeight;
                }
                holder.itemView.setLayoutParams(lp);
                return;
            case 1:
                this.sendLocationCell = (SendLocationCell) holder.itemView;
                updateCell();
                return;
            case 2:
                HeaderCell cell = (HeaderCell) holder.itemView;
                if (this.currentMessageObject != null) {
                    cell.setText(LocaleController.getString("LiveLocations", NUM));
                    return;
                } else {
                    cell.setText(LocaleController.getString("NearbyVenue", NUM));
                    return;
                }
            case 3:
                LocationCell cell2 = (LocationCell) holder.itemView;
                if (this.locationType == 0) {
                    position2 = position - 4;
                } else {
                    position2 = position - 5;
                }
                String iconUrl = null;
                TLRPC.TL_messageMediaVenue place = (position2 < 0 || position2 >= this.places.size() || !this.searched) ? null : (TLRPC.TL_messageMediaVenue) this.places.get(position2);
                if (position2 >= 0 && position2 < this.iconUrls.size() && this.searched) {
                    iconUrl = (String) this.iconUrls.get(position2);
                }
                cell2.setLocation(place, iconUrl, position2, true);
                return;
            case 4:
                ((LocationLoadingCell) holder.itemView).setLoading(this.searching);
                return;
            case 6:
                SendLocationCell sendLocationCell2 = (SendLocationCell) holder.itemView;
                if (this.gpsLocation == null) {
                    z = false;
                }
                sendLocationCell2.setHasLocation(z);
                return;
            case 7:
                SharingLiveLocationCell locationCell = (SharingLiveLocationCell) holder.itemView;
                if (this.locationType == 6) {
                    locationCell.setDialog(this.currentMessageObject, this.gpsLocation, this.myLocationDenied);
                    return;
                }
                TLRPC.TL_channelLocation tL_channelLocation = this.chatLocation;
                if (tL_channelLocation != null) {
                    locationCell.setDialog(this.dialogId, tL_channelLocation);
                    return;
                }
                MessageObject messageObject = this.currentMessageObject;
                if (messageObject == null || position != 1) {
                    locationCell.setDialog(this.currentLiveLocations.get(position - (messageObject != null ? 5 : 2)), this.gpsLocation);
                    return;
                } else {
                    locationCell.setDialog(messageObject, this.gpsLocation, this.myLocationDenied);
                    return;
                }
            case 10:
                holder.itemView.setBackgroundColor(Theme.getColor(this.myLocationDenied ? "dialogBackgroundGray" : "dialogBackground"));
                return;
            default:
                return;
        }
    }

    public Object getItem(int i) {
        int i2 = this.locationType;
        if (i2 != 4) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                if (i == 1) {
                    return messageObject;
                }
                if (i > 4 && i < this.places.size() + 4) {
                    return this.currentLiveLocations.get(i - 5);
                }
            } else if (i2 == 2) {
                if (i >= 2) {
                    return this.currentLiveLocations.get(i - 2);
                }
                return null;
            } else if (i2 == 1) {
                if (i > 4 && i < this.places.size() + 5) {
                    return this.places.get(i - 5);
                }
            } else if (i > 3 && i < this.places.size() + 4) {
                return this.places.get(i - 4);
            }
            return null;
        } else if (this.addressName == null) {
            return null;
        } else {
            TLRPC.TL_messageMediaVenue venue = new TLRPC.TL_messageMediaVenue();
            venue.address = this.addressName;
            venue.geo = new TLRPC.TL_geoPoint();
            if (this.customLocation != null) {
                venue.geo.lat = this.customLocation.getLatitude();
                venue.geo._long = this.customLocation.getLongitude();
            } else if (this.gpsLocation != null) {
                venue.geo.lat = this.gpsLocation.getLatitude();
                venue.geo._long = this.gpsLocation.getLongitude();
            }
            return venue;
        }
    }

    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        if (this.locationType == 6) {
            return 7;
        }
        if (this.needEmptyView && position == getItemCount() - 1) {
            return 10;
        }
        int i = this.locationType;
        if (i == 5) {
            return 7;
        }
        if (i == 4) {
            return 1;
        }
        if (this.currentMessageObject != null) {
            if (this.currentLiveLocations.isEmpty()) {
                if (position == 2) {
                    return 8;
                }
            } else if (position == 2) {
                return 9;
            } else {
                if (position == 3) {
                    return 2;
                }
                if (position == 4) {
                    this.shareLiveLocationPotistion = position;
                    return 6;
                }
            }
            return 7;
        } else if (i != 2) {
            if (i == 1) {
                if (position == 1) {
                    return 1;
                }
                if (position == 2) {
                    this.shareLiveLocationPotistion = position;
                    return 6;
                } else if (position == 3) {
                    return 9;
                } else {
                    if (position == 4) {
                        return 2;
                    }
                    if (this.searching || this.places.isEmpty() || !this.searched) {
                        if (position > 7 || ((!this.searching && this.searched) || this.myLocationDenied)) {
                            return 4;
                        }
                        return 3;
                    } else if (position == this.places.size() + 5) {
                        return 5;
                    }
                }
            } else if (position == 1) {
                return 1;
            } else {
                if (position == 2) {
                    return 9;
                }
                if (position == 3) {
                    return 2;
                }
                if (this.searching || this.places.isEmpty()) {
                    if (position > 6 || ((!this.searching && this.searched) || this.myLocationDenied)) {
                        return 4;
                    }
                    return 3;
                } else if (position == this.places.size() + 4) {
                    return 5;
                }
            }
            return 3;
        } else if (position != 1) {
            return 7;
        } else {
            this.shareLiveLocationPotistion = position;
            return 6;
        }
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int viewType = holder.getItemViewType();
        if (viewType == 6) {
            if (LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null && this.gpsLocation == null) {
                return false;
            }
            return true;
        } else if (viewType == 1 || viewType == 3 || viewType == 7) {
            return true;
        } else {
            return false;
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
