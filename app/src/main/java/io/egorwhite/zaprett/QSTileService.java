package io.egorwhite.zaprett;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.preference.PreferenceManager;
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
        settings = getApplicationContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
    }
    @Override
    public void onClick() {
        super.onClick();

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