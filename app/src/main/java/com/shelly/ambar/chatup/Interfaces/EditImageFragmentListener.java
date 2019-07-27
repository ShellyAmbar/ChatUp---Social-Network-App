package com.shelly.ambar.chatup.Interfaces;

public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onSaturationChanged(float saturation);
    void onConstrantChanged(float constrant);

    void onEditStart();
    void onEditComplete();


}
