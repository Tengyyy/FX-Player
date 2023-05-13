package tengy;

public class SleepSuppressor {

    static boolean sleepPreventActive = false;

    public static void preventSleep(){
        if(sleepPreventActive) return;

        if(App.isWindows) PowerManagement.INSTANCE.preventScreensaver();
        sleepPreventActive = true;
    }

    public static void allowSleep(){
        if(!sleepPreventActive) return;

        if(App.isWindows) PowerManagement.INSTANCE.allowScreensaver();
        sleepPreventActive = false;
    }
}
