package com.mcssoftware.app.mcsclient.MAP_CLASSES;

import java.util.List;

public interface DirectionFinderListener {

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
