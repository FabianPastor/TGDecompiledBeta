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
import org.telegram.messenger.LocationController.LocationFetchCallback;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.TL_channelLocation;
import org.telegram.tgnet.TLRPC.TL_geoPoint;
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
    private boolean pulledUp;
    private SendLocationCell sendLocationCell;
    private int shareLiveLocationPotistion = -1;

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
        if (this.customLocation == null) {
            fetchLocationAddress();
        }
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
            String str2 = "(%f,%f)";
            if (this.locationType == 4) {
                String str3 = this.addressName;
                if (str3 == null) {
                    if ((this.customLocation == null && this.gpsLocation == null) || this.fetchingLocation) {
                        str3 = LocaleController.getString(str, NUM);
                    } else {
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
                this.sendLocationCell.setText(LocaleController.getString("ChatSetThisLocation", NUM), str3);
            } else if (this.customLocation != null) {
                sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", NUM), String.format(Locale.US, str2, new Object[]{Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude())}));
            } else {
                String str4 = "SendLocation";
                if (this.gpsLocation != null) {
                    String string = LocaleController.getString(str4, NUM);
                    Object[] objArr = new Object[1];
                    objArr[0] = LocaleController.formatPluralString("Meters", (int) this.gpsLocation.getAccuracy());
                    sendLocationCell.setText(string, LocaleController.formatString("AccurateTo", NUM, objArr));
                    return;
                }
                sendLocationCell.setText(LocaleController.getString(str4, NUM), LocaleController.getString(str, NUM));
            }
        }
    }

    private String getAddressName() {
        return this.addressName;
    }

    public void onLocationAddressAvailable(String str, Location location) {
        this.fetchingLocation = false;
        this.previousFetchedLocation = location;
        this.addressName = str;
        updateCell();
    }

    /* JADX WARNING: Missing block: B:6:0x000d, code skipped:
            if (r0 != null) goto L_0x000f;
     */
    public void fetchLocationAddress() {
        /*
        r3 = this;
        r0 = r3.locationType;
        r1 = 4;
        if (r0 == r1) goto L_0x0006;
    L_0x0005:
        return;
    L_0x0006:
        r0 = r3.customLocation;
        if (r0 == 0) goto L_0x000b;
    L_0x000a:
        goto L_0x000f;
    L_0x000b:
        r0 = r3.gpsLocation;
        if (r0 == 0) goto L_0x0029;
    L_0x000f:
        r1 = r3.previousFetchedLocation;
        if (r1 == 0) goto L_0x001d;
    L_0x0013:
        r1 = r1.distanceTo(r0);
        r2 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 <= 0) goto L_0x0020;
    L_0x001d:
        r1 = 0;
        r3.addressName = r1;
    L_0x0020:
        r3.updateCell();
        r1 = 1;
        r3.fetchingLocation = r1;
        org.telegram.messenger.LocationController.fetchLocationAddress(r0, r3);
    L_0x0029:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.LocationActivityAdapter.fetchLocationAddress():void");
    }

    public int getItemCount() {
        int i = this.locationType;
        int i2 = 5;
        if (i == 5 || i == 4) {
            return 2;
        }
        if (this.currentMessageObject != null) {
            return (this.currentLiveLocations.isEmpty() ? 0 : this.currentLiveLocations.size() + 2) + 2;
        } else if (i == 2) {
            return this.currentLiveLocations.size() + 2;
        } else {
            boolean z = this.searching;
            if (z || (!z && this.places.isEmpty())) {
                if (this.locationType == 0) {
                    i2 = 4;
                }
                return i2;
            } else if (this.locationType == 1) {
                return (this.places.size() + 4) + (1 ^ this.places.isEmpty());
            } else {
                return (this.places.size() + 3) + (1 ^ this.places.isEmpty());
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
                Context context = this.mContext;
                int i2 = this.locationType;
                i2 = (i2 == 4 || i2 == 5) ? 16 : 54;
                emptyCell = new SharingLiveLocationCell(context, true, i2);
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
        notifyItemChanged(this.locationType == 0 ? 2 : 3);
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
                                    i2 = 4;
                                }
                                sharingLiveLocationCell.setDialog((LiveLocation) arrayList.get(i - i2), this.gpsLocation);
                                return;
                            }
                            sharingLiveLocationCell.setDialog(messageObject, this.gpsLocation);
                            return;
                        } else {
                            return;
                        }
                    } else if (this.locationType == 0) {
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
        int i2 = this.locationType;
        if (i2 != 4) {
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject != null) {
                if (i == 1) {
                    return messageObject;
                }
                if (i > 3 && i < this.places.size() + 3) {
                    return this.currentLiveLocations.get(i - 4);
                }
            } else if (i2 == 2) {
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
            if (i == 2) {
                return 2;
            }
            if (i != 3) {
                return 7;
            }
            this.shareLiveLocationPotistion = i;
            return 6;
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
