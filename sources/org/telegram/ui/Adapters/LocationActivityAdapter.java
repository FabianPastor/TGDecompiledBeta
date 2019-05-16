package org.telegram.ui.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
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

    public LocationActivityAdapter(Context context, int i, long j) {
        this.mContext = context;
        this.liveLocationType = i;
        this.dialogId = j;
    }

    public void setOverScrollHeight(int i) {
        this.overScrollHeight = i;
    }

    public void setGpsLocation(Location location) {
        Object obj = this.gpsLocation == null ? 1 : null;
        this.gpsLocation = location;
        if (obj != null) {
            int i = this.shareLiveLocationPotistion;
            if (i > 0) {
                notifyItemChanged(i);
            }
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

    private void updateCell() {
        SendLocationCell sendLocationCell = this.sendLocationCell;
        if (sendLocationCell == null) {
            return;
        }
        if (this.customLocation != null) {
            sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", NUM), String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude())}));
            return;
        }
        String str = "SendLocation";
        if (this.gpsLocation != null) {
            String string = LocaleController.getString(str, NUM);
            Object[] objArr = new Object[1];
            objArr[0] = LocaleController.formatPluralString("Meters", (int) this.gpsLocation.getAccuracy());
            sendLocationCell.setText(string, LocaleController.formatString("AccurateTo", NUM, objArr));
            return;
        }
        sendLocationCell.setText(LocaleController.getString(str, NUM), LocaleController.getString("Loading", NUM));
    }

    public int getItemCount() {
        if (this.currentMessageObject != null) {
            return (this.currentLiveLocations.isEmpty() ? 0 : this.currentLiveLocations.size() + 2) + 2;
        } else if (this.liveLocationType == 2) {
            return this.currentLiveLocations.size() + 2;
        } else {
            boolean z = this.searching;
            int i = 4;
            if (z || (!z && this.places.isEmpty())) {
                if (this.liveLocationType != 0) {
                    i = 5;
                }
                return i;
            } else if (this.liveLocationType == 1) {
                return (this.places.size() + 4) + (this.places.isEmpty() ^ 1);
            } else {
                return (this.places.size() + 3) + (this.places.isEmpty() ^ 1);
            }
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View emptyCell;
        switch (i) {
            case 0:
                emptyCell = new EmptyCell(this.mContext);
                break;
            case 1:
                emptyCell = new SendLocationCell(this.mContext, false);
                break;
            case 2:
                emptyCell = new GraySectionCell(this.mContext);
                break;
            case 3:
                emptyCell = new LocationCell(this.mContext);
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
            default:
                emptyCell = new SharingLiveLocationCell(this.mContext, true);
                break;
        }
        return new Holder(emptyCell);
    }

    public void setPulledUp() {
        if (!this.pulledUp) {
            this.pulledUp = true;
            AndroidUtilities.runOnUIThread(new -$$Lambda$LocationActivityAdapter$kPn2n4u9DAIYsBitfbC-1mB05AA(this));
        }
    }

    public /* synthetic */ void lambda$setPulledUp$0$LocationActivityAdapter() {
        notifyItemChanged(this.liveLocationType == 0 ? 2 : 3);
    }

    public boolean isPulledUp() {
        return this.pulledUp;
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType != 0) {
            boolean z = true;
            if (itemViewType != 1) {
                int i2 = 2;
                if (itemViewType != 2) {
                    if (itemViewType != 3) {
                        if (itemViewType == 4) {
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
                            MessageObject messageObject = this.currentMessageObject;
                            if (messageObject == null || i != 1) {
                                SharingLiveLocationCell sharingLiveLocationCell = (SharingLiveLocationCell) viewHolder.itemView;
                                ArrayList arrayList = this.currentLiveLocations;
                                if (this.currentMessageObject != null) {
                                    i2 = 4;
                                }
                                sharingLiveLocationCell.setDialog((LiveLocation) arrayList.get(i - i2), this.gpsLocation);
                                return;
                            }
                            ((SharingLiveLocationCell) viewHolder.itemView).setDialog(messageObject, this.gpsLocation);
                            return;
                        } else {
                            return;
                        }
                    } else if (this.liveLocationType == 0) {
                        i -= 3;
                        ((LocationCell) viewHolder.itemView).setLocation((TL_messageMediaVenue) this.places.get(i), (String) this.iconUrls.get(i), true);
                        return;
                    } else {
                        i -= 4;
                        ((LocationCell) viewHolder.itemView).setLocation((TL_messageMediaVenue) this.places.get(i), (String) this.iconUrls.get(i), true);
                        return;
                    }
                } else if (this.currentMessageObject != null) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("LiveLocations", NUM));
                    return;
                } else if (this.pulledUp) {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("NearbyPlaces", NUM));
                    return;
                } else {
                    ((GraySectionCell) viewHolder.itemView).setText(LocaleController.getString("ShowNearbyPlaces", NUM));
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
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            int i2 = this.liveLocationType;
            if (i2 == 2) {
                if (i >= 2) {
                    return this.currentLiveLocations.get(i - 2);
                }
                return null;
            } else if (i2 == 1) {
                if (i > 3 && i < this.places.size() + 4) {
                    return this.places.get(i - 4);
                }
            } else if (i > 2 && i < this.places.size() + 3) {
                return this.places.get(i - 3);
            }
        } else if (i == 1) {
            return messageObject;
        } else {
            if (i > 3 && i < this.places.size() + 3) {
                return this.currentLiveLocations.get(i - 4);
            }
        }
        return null;
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (this.currentMessageObject == null) {
            int i2 = this.liveLocationType;
            if (i2 != 2) {
                boolean z;
                if (i2 == 1) {
                    if (i == 1) {
                        return 1;
                    }
                    if (i == 2) {
                        this.shareLiveLocationPotistion = i;
                        return 6;
                    } else if (i == 3) {
                        return 2;
                    } else {
                        z = this.searching;
                        if (z || (!z && this.places.isEmpty())) {
                            return 4;
                        }
                        if (i == this.places.size() + 4) {
                            return 5;
                        }
                    }
                } else if (i == 1) {
                    return 1;
                } else {
                    if (i == 2) {
                        return 2;
                    }
                    z = this.searching;
                    if (z || (!z && this.places.isEmpty())) {
                        return 4;
                    }
                    if (i == this.places.size() + 3) {
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
        } else if (i == 2) {
            return 2;
        } else {
            if (i != 3) {
                return 7;
            }
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
