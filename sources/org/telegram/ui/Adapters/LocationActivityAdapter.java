package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.LocationFetchCallback;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.TL_channelLocation;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationDirectionCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.SharingLiveLocationCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.LocationActivity.LiveLocation;

public class LocationActivityAdapter extends BaseLocationAdapter implements LocationFetchCallback {
    private String addressName;
    private TL_channelLocation chatLocation;
    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<LiveLocation> currentLiveLocations = new ArrayList();
    private MessageObject currentMessageObject;
    private Location customLocation;
    private long dialogId;
    private boolean fetchingLocation;
    private Location gpsLocation;
    private int locationType;
    private Context mContext;
    private int overScrollHeight;
    private Location previousFetchedLocation;
    private SendLocationCell sendLocationCell;
    private int shareLiveLocationPotistion = -1;

    /* Access modifiers changed, original: protected */
    public void onDirectionClick() {
    }

    public LocationActivityAdapter(Context context, int i, long j) {
        this.mContext = context;
        this.locationType = i;
        this.dialogId = j;
    }

    public void setOverScrollHeight(int i) {
        this.overScrollHeight = i;
    }

    public void setGpsLocation(Location location) {
        Object obj = this.gpsLocation == null ? 1 : null;
        this.gpsLocation = location;
        if (this.customLocation == null) {
            fetchLocationAddress();
        }
        if (obj != null) {
            int i = this.shareLiveLocationPotistion;
            if (i > 0) {
                notifyItemChanged(i);
            }
        }
        if (this.currentMessageObject != null) {
            notifyItemChanged(1);
            updateLiveLocations();
        } else if (this.locationType != 2) {
            updateCell();
        } else {
            updateLiveLocations();
        }
    }

    public void updateLiveLocations() {
        if (!this.currentLiveLocations.isEmpty()) {
            notifyItemRangeChanged(2, this.currentLiveLocations.size());
        }
    }

    public void setCustomLocation(Location location) {
        this.customLocation = location;
        fetchLocationAddress();
        updateCell();
    }

    public void setLiveLocations(ArrayList<LiveLocation> arrayList) {
        this.currentLiveLocations = new ArrayList(arrayList);
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        for (int i = 0; i < this.currentLiveLocations.size(); i++) {
            if (((LiveLocation) this.currentLiveLocations.get(i)).id == clientUserId) {
                this.currentLiveLocations.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void setMessageObject(MessageObject messageObject) {
        this.currentMessageObject = messageObject;
        notifyDataSetChanged();
    }

    public void setChatLocation(TL_channelLocation tL_channelLocation) {
        this.chatLocation = tL_channelLocation;
    }

    private void updateCell() {
        SendLocationCell sendLocationCell = this.sendLocationCell;
        if (sendLocationCell != null) {
            String str = "Loading";
            String str2;
            if (this.locationType == 4 || this.customLocation != null) {
                String str3 = this.addressName;
                if (str3 == null) {
                    if ((this.customLocation == null && this.gpsLocation == null) || this.fetchingLocation) {
                        str3 = LocaleController.getString(str, NUM);
                    } else {
                        str2 = "(%f,%f)";
                        if (this.customLocation != null) {
                            str3 = String.format(Locale.US, str2, new Object[]{Double.valueOf(r0.getLatitude()), Double.valueOf(this.customLocation.getLongitude())});
                        } else {
                            if (this.gpsLocation != null) {
                                str3 = String.format(Locale.US, str2, new Object[]{Double.valueOf(r0.getLatitude()), Double.valueOf(this.gpsLocation.getLongitude())});
                            } else {
                                str3 = LocaleController.getString(str, NUM);
                            }
                        }
                    }
                }
                if (this.locationType == 4) {
                    this.sendLocationCell.setText(LocaleController.getString("ChatSetThisLocation", NUM), str3);
                    return;
                } else {
                    this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", NUM), str3);
                    return;
                }
            }
            str2 = "SendLocation";
            if (this.gpsLocation != null) {
                String string = LocaleController.getString(str2, NUM);
                Object[] objArr = new Object[1];
                objArr[0] = LocaleController.formatPluralString("Meters", (int) this.gpsLocation.getAccuracy());
                sendLocationCell.setText(string, LocaleController.formatString("AccurateTo", NUM, objArr));
                return;
            }
            sendLocationCell.setText(LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM));
        }
    }

    private String getAddressName() {
        return this.addressName;
    }

    public void onLocationAddressAvailable(String str, String str2, Location location) {
        this.fetchingLocation = false;
        this.previousFetchedLocation = location;
        this.addressName = str;
        updateCell();
    }

    public void fetchLocationAddress() {
        Location location;
        Location location2;
        if (this.locationType == 4) {
            location = this.customLocation;
            if (location == null) {
                location = this.gpsLocation;
                if (location == null) {
                    return;
                }
            }
            location2 = this.previousFetchedLocation;
            if (location2 == null || location2.distanceTo(location) > 100.0f) {
                this.addressName = null;
            }
            this.fetchingLocation = true;
            updateCell();
            LocationController.fetchLocationAddress(location, this);
        } else {
            location = this.customLocation;
            if (location != null) {
                location2 = this.previousFetchedLocation;
                if (location2 == null || location2.distanceTo(location) > 20.0f) {
                    this.addressName = null;
                }
                this.fetchingLocation = true;
                updateCell();
                LocationController.fetchLocationAddress(location, this);
            }
        }
    }

    public int getItemCount() {
        int i = this.locationType;
        int i2 = 5;
        if (i == 5 || i == 4) {
            return 2;
        }
        int i3 = 1;
        if (this.currentMessageObject != null) {
            if (!this.currentLiveLocations.isEmpty()) {
                i3 = this.currentLiveLocations.size() + 3;
            }
            return i3 + 2;
        } else if (i == 2) {
            return this.currentLiveLocations.size() + 2;
        } else {
            boolean z = this.searching;
            if (z || (!z && this.places.isEmpty())) {
                if (this.locationType != 0) {
                    i2 = 6;
                }
                return i2;
            } else if (this.locationType == 1) {
                return (this.places.size() + 5) + (this.places.isEmpty() ^ 1);
            } else {
                return (this.places.size() + 4) + (this.places.isEmpty() ^ 1);
            }
        }
    }

    public /* synthetic */ void lambda$onCreateViewHolder$0$LocationActivityAdapter(View view) {
        onDirectionClick();
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View emptyCell;
        View sendLocationCell;
        switch (i) {
            case 0:
                emptyCell = new EmptyCell(this.mContext);
                break;
            case 1:
                sendLocationCell = new SendLocationCell(this.mContext, false);
                break;
            case 2:
                emptyCell = new HeaderCell(this.mContext);
                break;
            case 3:
                sendLocationCell = new LocationCell(this.mContext, false);
                break;
            case 4:
                emptyCell = new LocationLoadingCell(this.mContext);
                break;
            case 5:
                emptyCell = new LocationPoweredCell(this.mContext);
                break;
            case 6:
                emptyCell = new SendLocationCell(this.mContext, true);
                emptyCell.setDialogId(this.dialogId);
                break;
            case 7:
                Context context = this.mContext;
                int i2 = this.locationType;
                i2 = (i2 == 4 || i2 == 5) ? 16 : 54;
                emptyCell = new SharingLiveLocationCell(context, true, i2);
                break;
            case 8:
                emptyCell = new LocationDirectionCell(this.mContext);
                emptyCell.setOnButtonClick(new -$$Lambda$LocationActivityAdapter$ZI5zffERDWGSr7GTCgcNce6b1mE(this));
                break;
            default:
                emptyCell = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                combinedDrawable.setFullsize(true);
                emptyCell.setBackgroundDrawable(combinedDrawable);
                break;
        }
        emptyCell = sendLocationCell;
        return new Holder(emptyCell);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType != 0) {
            boolean z = true;
            if (itemViewType != 1) {
                int i2 = 2;
                if (itemViewType == 2) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (this.currentMessageObject != null) {
                        headerCell.setText(LocaleController.getString("LiveLocations", NUM));
                        return;
                    } else {
                        headerCell.setText(LocaleController.getString("NearbyVenue", NUM));
                        return;
                    }
                } else if (itemViewType == 3) {
                    LocationCell locationCell = (LocationCell) viewHolder.itemView;
                    i = this.locationType == 0 ? i - 4 : i - 5;
                    locationCell.setLocation((TL_messageMediaVenue) this.places.get(i), (String) this.iconUrls.get(i), i, true);
                    return;
                } else if (itemViewType == 4) {
                    ((LocationLoadingCell) viewHolder.itemView).setLoading(this.searching);
                    return;
                } else if (itemViewType == 6) {
                    SendLocationCell sendLocationCell = (SendLocationCell) viewHolder.itemView;
                    if (this.gpsLocation == null) {
                        z = false;
                    }
                    sendLocationCell.setHasLocation(z);
                    return;
                } else if (itemViewType == 7) {
                    SharingLiveLocationCell sharingLiveLocationCell = (SharingLiveLocationCell) viewHolder.itemView;
                    TL_channelLocation tL_channelLocation = this.chatLocation;
                    if (tL_channelLocation != null) {
                        sharingLiveLocationCell.setDialog(this.dialogId, tL_channelLocation);
                        return;
                    }
                    MessageObject messageObject = this.currentMessageObject;
                    if (messageObject == null || i != 1) {
                        ArrayList arrayList = this.currentLiveLocations;
                        if (this.currentMessageObject != null) {
                            i2 = 5;
                        }
                        sharingLiveLocationCell.setDialog((LiveLocation) arrayList.get(i - i2), this.gpsLocation);
                        return;
                    }
                    sharingLiveLocationCell.setDialog(messageObject, this.gpsLocation);
                    return;
                } else {
                    return;
                }
            }
            this.sendLocationCell = (SendLocationCell) viewHolder.itemView;
            updateCell();
            return;
        }
        ((EmptyCell) viewHolder.itemView).setHeight(this.overScrollHeight);
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
            TL_messageMediaVenue tL_messageMediaVenue = new TL_messageMediaVenue();
            tL_messageMediaVenue.address = this.addressName;
            tL_messageMediaVenue.geo = new TL_geoPoint();
            Location location = this.customLocation;
            if (location != null) {
                tL_messageMediaVenue.geo.lat = location.getLatitude();
                tL_messageMediaVenue.geo._long = this.customLocation.getLongitude();
            } else {
                location = this.gpsLocation;
                if (location != null) {
                    tL_messageMediaVenue.geo.lat = location.getLatitude();
                    tL_messageMediaVenue.geo._long = this.gpsLocation.getLongitude();
                }
            }
            return tL_messageMediaVenue;
        }
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        int i2 = this.locationType;
        if (i2 == 5) {
            return 7;
        }
        if (i2 == 4) {
            return 1;
        }
        if (this.currentMessageObject != null) {
            if (this.currentLiveLocations.isEmpty()) {
                if (i == 2) {
                    return 8;
                }
            } else if (i == 2) {
                return 9;
            } else {
                if (i == 3) {
                    return 2;
                }
                if (i == 4) {
                    this.shareLiveLocationPotistion = i;
                    return 6;
                }
            }
            return 7;
        } else if (i2 != 2) {
            boolean z;
            if (i2 == 1) {
                if (i == 1) {
                    return 1;
                }
                if (i == 2) {
                    this.shareLiveLocationPotistion = i;
                    return 6;
                } else if (i == 3) {
                    return 9;
                } else {
                    if (i == 4) {
                        return 2;
                    }
                    z = this.searching;
                    if (z || (!z && this.places.isEmpty())) {
                        return 4;
                    }
                    if (i == this.places.size() + 5) {
                        return 5;
                    }
                }
            } else if (i == 1) {
                return 1;
            } else {
                if (i == 2) {
                    return 9;
                }
                if (i == 3) {
                    return 2;
                }
                z = this.searching;
                if (z || (!z && this.places.isEmpty())) {
                    return 4;
                }
                if (i == this.places.size() + 4) {
                    return 5;
                }
            }
            return 3;
        } else if (i != 1) {
            return 7;
        } else {
            this.shareLiveLocationPotistion = i;
            return 6;
        }
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        boolean z = false;
        if (itemViewType == 6) {
            if (!(LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null && this.gpsLocation == null)) {
                z = true;
            }
            return z;
        }
        if (itemViewType == 1 || itemViewType == 3 || itemViewType == 7) {
            z = true;
        }
        return z;
    }
}
