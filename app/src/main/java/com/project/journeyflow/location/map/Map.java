package com.project.journeyflow.location.map;

public class Map {

    private boolean zoomButtons;
    private boolean multiTouchControl;
    private float zoomValue;

    public boolean isZoomButtons() {
        return zoomButtons;
    }

    public void setZoomButtons(boolean zoomButtons) {
        this.zoomButtons = zoomButtons;
    }

    public boolean isMultiTouchControl() {
        return multiTouchControl;
    }

    public void setMultiTouchControl(boolean multiTouchControl) {
        this.multiTouchControl = multiTouchControl;
    }

    public float getZoom() {
        return zoomValue;
    }

    public void setZoom(float zoomValue) {
        this.zoomValue = zoomValue;
    }

    public void setDefaultMapValues() {
        this.zoomButtons = false;
        this.multiTouchControl = true;
        this.zoomValue = 16;
    }

}
