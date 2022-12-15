package hans;

public class SleepSuppressor {

    static boolean sleepPreventActive = false;

    public static void preventSleep(){
        if(sleepPreventActive) return;

        PowerManagement.INSTANCE.preventScreensaver();
        sleepPreventActive = true;
    }

    public static void allowSleep(){
        if(!sleepPreventActive) return;

        PowerManagement.INSTANCE.allowScreensaver();
        sleepPreventActive = false;
    }
}
