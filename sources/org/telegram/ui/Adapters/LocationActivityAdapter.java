package org.telegram.ui.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
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

public class LocationActivityAdapter extends BaseLocationAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private ArrayList<LiveLocation> currentLiveLocations = new ArrayList();
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

    /* renamed from: org.telegram.ui.Adapters.LocationActivityAdapter$1 */
    class C07861 implements Runnable {
        C07861() {
        }

        public void run() {
            LocationActivityAdapter.this.notifyItemChanged(LocationActivityAdapter.this.liveLocationType == 0 ? 2 : 3);
        }
    }

    public LocationActivityAdapter(Context context, int i, long j) {
        this.mContext = context;
        this.liveLocationType = i;
        this.dialogId = j;
    }

    public void setOverScrollHeight(int i) {
        this.overScrollHeight = i;
    }

    public void setGpsLocation(Location location) {
        int i = this.gpsLocation == null ? 1 : 0;
        this.gpsLocation = location;
        if (i != 0 && this.shareLiveLocationPotistion > null) {
            notifyItemChanged(this.shareLiveLocationPotistion);
        }
        if (this.currentMessageObject != null) {
            notifyItemChanged(1);
            updateLiveLocations();
        } else if (this.liveLocationType != 2) {
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
        updateCell();
    }

    public void setLiveLocations(ArrayList<LiveLocation> arrayList) {
        this.currentLiveLocations = new ArrayList(arrayList);
        arrayList = UserConfig.getInstance(this.currentAccount).getClientUserId();
        for (int i = 0; i < this.currentLiveLocations.size(); i++) {
            if (((LiveLocation) this.currentLiveLocations.get(i)).id == arrayList) {
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

    private void updateCell() {
        if (this.sendLocationCell == null) {
            return;
        }
        if (this.customLocation != null) {
            this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", C0446R.string.SendSelectedLocation), String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude())}));
        } else if (this.gpsLocation != null) {
            this.sendLocationCell.setText(LocaleController.getString("SendLocation", C0446R.string.SendLocation), LocaleController.formatString("AccurateTo", C0446R.string.AccurateTo, LocaleController.formatPluralString("Meters", (int) this.gpsLocation.getAccuracy())));
        } else {
            this.sendLocationCell.setText(LocaleController.getString("SendLocation", C0446R.string.SendLocation), LocaleController.getString("Loading", C0446R.string.Loading));
        }
    }

    public int getItemCount() {
        if (this.currentMessageObject != null) {
            return 2 + (this.currentLiveLocations.isEmpty() ? 0 : this.currentLiveLocations.size() + 2);
        } else if (this.liveLocationType == 2) {
            return 2 + this.currentLiveLocations.size();
        } else {
            int i = 4;
            if (!this.searching) {
                if (this.searching || !this.places.isEmpty()) {
                    if (this.liveLocationType == 1) {
                        return (4 + this.places.size()) + (this.places.isEmpty() ^ 1);
                    }
                    return (3 + this.places.size()) + (this.places.isEmpty() ^ 1);
                }
            }
            if (this.liveLocationType != 0) {
                i = 5;
            }
            return i;
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case 0:
                i = new EmptyCell(this.mContext);
                break;
            case 1:
                i = new SendLocationCell(this.mContext, false);
                break;
            case 2:
                i = new GraySectionCell(this.mContext);
                break;
            case 3:
                i = new LocationCell(this.mContext);
                break;
            case 4:
                i = new LocationLoadingCell(this.mContext);
                break;
            case 5:
                i = new LocationPoweredCell(this.mContext);
                break;
            case 6:
                i = new SendLocationCell(this.mContext, true);
                i.setDialogId(this.dialogId);
                break;
            default:
                i = new SharingLiveLocationCell(this.mContext, true);
                break;
        }
        return new Holder(i);
    }

    public void setPulledUp() {
        if (!this.pulledUp) {
            this.pulledUp = true;
            AndroidUtilities.runOnUIThread(new C07861());
        }
    }

    public boolean isPulledUp() {
        return this.pulledUp;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int i2 = 4;
        boolean z = true;
        switch (viewHolder.getItemViewType()) {
            case 0:
                ((EmptyCell) viewHolder.itemView).setHeight(this.overScrollHeight);
                return;
            case 1:
                this.sendLocationCell = (SendLocationCell) viewHolder.itemView;
                updateCell();
                return;
            case 2:
                if (this.currentMessageObject != 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("LiveLocations", C0446R.string.LiveLocations));
                    return;
                } else if (this.pulledUp != 0) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("NearbyPlaces", C0446R.string.NearbyPlaces));
                    return;
                } else {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("ShowNearbyPlaces", C0446R.string.ShowNearbyPlaces));
                    return;
                }
            case 3:
                if (this.liveLocationType == 0) {
                    i -= 3;
                    ((LocationCell) viewHolder.itemView).setLocation((TL_messageMediaVenue) this.places.get(i), (String) this.iconUrls.get(i), true);
                    return;
                }
                i -= 4;
                ((LocationCell) viewHolder.itemView).setLocation((TL_messageMediaVenue) this.places.get(i), (String) this.iconUrls.get(i), true);
                return;
            case 4:
                ((LocationLoadingCell) viewHolder.itemView).setLoading(this.searching);
                return;
            case 6:
                SendLocationCell sendLocationCell = (SendLocationCell) viewHolder.itemView;
                if (this.gpsLocation == 0) {
                    z = false;
                }
                sendLocationCell.setHasLocation(z);
                return;
            case 7:
                if (this.currentMessageObject == null || i != 1) {
                    SharingLiveLocationCell sharingLiveLocationCell = (SharingLiveLocationCell) viewHolder.itemView;
                    ArrayList arrayList = this.currentLiveLocations;
                    if (this.currentMessageObject == null) {
                        i2 = 2;
                    }
                    sharingLiveLocationCell.setDialog((LiveLocation) arrayList.get(i - i2), this.gpsLocation);
                    return;
                }
                ((SharingLiveLocationCell) viewHolder.itemView).setDialog(this.currentMessageObject, this.gpsLocation);
                return;
            default:
                return;
        }
    }

    public Object getItem(int i) {
        if (this.currentMessageObject != null) {
            if (i == 1) {
                return this.currentMessageObject;
            }
            if (i > 3 && i < this.places.size() + 3) {
                return this.currentLiveLocations.get(i - 4);
            }
        } else if (this.liveLocationType == 2) {
            if (i >= 2) {
                return this.currentLiveLocations.get(i - 2);
            }
            return null;
        } else if (this.liveLocationType == 1) {
            if (i > 3 && i < this.places.size() + 3) {
                return this.places.get(i - 4);
            }
        } else if (i > 2 && i < this.places.size() + 2) {
            return this.places.get(i - 3);
        }
        return null;
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (this.currentMessageObject != null) {
            if (i == 2) {
                return 2;
            }
            if (i != 3) {
                return 7;
            }
            this.shareLiveLocationPotistion = i;
            return 6;
        } else if (this.liveLocationType != 2) {
            if (this.liveLocationType == 1) {
                if (i == 1) {
                    return 1;
                }
                if (i == 2) {
                    this.shareLiveLocationPotistion = i;
                    return 6;
                } else if (i == 3) {
                    return 2;
                } else {
                    if (!this.searching) {
                        if (this.searching || !this.places.isEmpty()) {
                            if (i == this.places.size() + 4) {
                                return 5;
                            }
                        }
                    }
                    return 4;
                }
            } else if (i == 1) {
                return 1;
            } else {
                if (i == 2) {
                    return 2;
                }
                if (!this.searching) {
                    if (this.searching || !this.places.isEmpty()) {
                        if (i == this.places.size() + 3) {
                            return 5;
                        }
                    }
                }
                return 4;
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
        viewHolder = viewHolder.getItemViewType();
        boolean z = false;
        if (viewHolder == 6) {
            if (!(LocationController.getInstance(this.currentAccount).getSharingLocationInfo(this.dialogId) == null && this.gpsLocation == null)) {
                z = true;
            }
            return z;
        }
        if (viewHolder == 1 || viewHolder == 3 || viewHolder == 7) {
            z = true;
        }
        return z;
    }
}
