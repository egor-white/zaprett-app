package io.egorwhite.zaprett;

import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class QSTileService extends TileService {
    private static SharedPreferences settings;
    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }
    @Override
public void onCreate() {
    super.onCreate();
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
}
    @Override
    public void onClick() {
        super.onClick();
        settings = getApplicationContext().getSharedPreferences(Context.MODE_PRIVATE);
        Tile tile = getQsTile();
        boolean isActive = tile.getState() == Tile.STATE_ACTIVE;
        if (!isActive){ ModuleInteractor.startService();}
        else { ModuleInteractor.stopService();}
        tile.setState(isActive ? Tile.STATE_INACTIVE : Tile.STATE_ACTIVE);
        tile.updateTile();
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (settings.getBoolean("use_module", false)){
            if (ModuleInteractor.getStatus()){ tile.setState(Tile.STATE_ACTIVE);}
            else { tile.setState(Tile.STATE_INACTIVE);}
        }
        else { tile.setState(Tile.STATE_UNAVAILABLE);}
        tile.setLabel("zaprett");
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_launcher_monochrome));
        tile.updateTile();
    }
}