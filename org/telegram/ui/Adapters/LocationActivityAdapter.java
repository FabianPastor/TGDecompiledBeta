package org.telegram.ui.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import java.util.Locale;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.LocationLoadingCell;
import org.telegram.ui.Cells.LocationPoweredCell;
import org.telegram.ui.Cells.SendLocationCell;
import org.telegram.ui.Components.RecyclerListView.Holder;

public class LocationActivityAdapter extends BaseLocationAdapter {
    private Location customLocation;
    private Location gpsLocation;
    private Context mContext;
    private int overScrollHeight;
    private SendLocationCell sendLocationCell;

    public LocationActivityAdapter(Context context) {
        this.mContext = context;
    }

    public void setOverScrollHeight(int value) {
        this.overScrollHeight = value;
    }

    public void setGpsLocation(Location location) {
        this.gpsLocation = location;
        updateCell();
    }

    public void setCustomLocation(Location location) {
        this.customLocation = location;
        updateCell();
    }

    private void updateCell() {
        if (this.sendLocationCell == null) {
            return;
        }
        if (this.customLocation != null) {
            this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", R.string.SendSelectedLocation), String.format(Locale.US, "(%f,%f)", new Object[]{Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude())}));
        } else if (this.gpsLocation != null) {
            this.sendLocationCell.setText(LocaleController.getString("SendLocation", R.string.SendLocation), LocaleController.formatString("AccurateTo", R.string.AccurateTo, LocaleController.formatPluralString("Meters", (int) this.gpsLocation.getAccuracy())));
        } else {
            this.sendLocationCell.setText(LocaleController.getString("SendLocation", R.string.SendLocation), LocaleController.getString("Loading", R.string.Loading));
        }
    }

    public int getItemCount() {
        if (this.searching || (!this.searching && this.places.isEmpty())) {
            return 4;
        }
        return (this.places.isEmpty() ? 0 : 1) + (this.places.size() + 3);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new EmptyCell(this.mContext);
                break;
            case 1:
                view = new SendLocationCell(this.mContext);
                break;
            case 2:
                view = new GraySectionCell(this.mContext);
                break;
            case 3:
                view = new LocationCell(this.mContext);
                break;
            case 4:
                view = new LocationLoadingCell(this.mContext);
                break;
            default:
                view = new LocationPoweredCell(this.mContext);
                break;
        }
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((EmptyCell) holder.itemView).setHeight(this.overScrollHeight);
                return;
            case 1:
                this.sendLocationCell = (SendLocationCell) holder.itemView;
                updateCell();
                return;
            case 2:
                ((GraySectionCell) holder.itemView).setText(LocaleController.getString("NearbyPlaces", R.string.NearbyPlaces));
                return;
            case 3:
                ((LocationCell) holder.itemView).setLocation((TL_messageMediaVenue) this.places.get(position - 3), (String) this.iconUrls.get(position - 3), true);
                return;
            case 4:
                ((LocationLoadingCell) holder.itemView).setLoading(this.searching);
                return;
            default:
                return;
        }
    }

    public TL_messageMediaVenue getItem(int i) {
        if (i <= 2 || i >= this.places.size() + 3) {
            return null;
        }
        return (TL_messageMediaVenue) this.places.get(i - 3);
    }

    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        if (position == 1) {
            return 1;
        }
        if (position == 2) {
            return 2;
        }
        if (this.searching || (!this.searching && this.places.isEmpty())) {
            return 4;
        }
        if (position == this.places.size() + 3) {
            return 5;
        }
        return 3;
    }

    public boolean isEnabled(ViewHolder holder) {
        int position = holder.getAdapterPosition();
        return (position == 2 || position == 0 || ((position == 3 && (this.searching || (!this.searching && this.places.isEmpty()))) || position == this.places.size() + 3)) ? false : true;
    }
}
