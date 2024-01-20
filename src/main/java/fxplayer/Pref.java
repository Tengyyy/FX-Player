package fxplayer;

import java.util.prefs.Preferences;

public class Pref {

    public Preferences preferences;

    Pref(){
        preferences = Preferences.userNodeForPackage(Pref.class);
    }
}
