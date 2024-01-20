package fxplayer;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WTypes.LPWSTR;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Power management.
 *
 */
public enum PowerManagement
{
    INSTANCE;

    @FieldOrder({"version", "flags", "simpleReasonString"})
    public static class REASON_CONTEXT extends Structure
    {
        public static class ByReference extends REASON_CONTEXT implements Structure.ByReference
        {
        }

        public ULONG version;
        public DWORD flags;
        public LPWSTR simpleReasonString;
    }

    private interface Kernel32 extends StdCallLibrary
    {
        HANDLE PowerCreateRequest(REASON_CONTEXT.ByReference context);

        /**
         * @param powerRequestHandle the handle returned by {@link #PowerCreateRequest(REASON_CONTEXT.ByReference)}
         * @param requestType        requestType is the ordinal value of {@link PowerRequestType}
         * @return true on success
         */
        boolean PowerSetRequest(HANDLE powerRequestHandle, int requestType);

        /**
         * @param powerRequestHandle the handle returned by {@link #PowerCreateRequest(REASON_CONTEXT.ByReference)}
         * @param requestType        requestType is the ordinal value of {@link PowerRequestType}
         * @return true on success
         */
        boolean PowerClearRequest(HANDLE powerRequestHandle, int requestType);

        enum PowerRequestType
        {
            PowerRequestDisplayRequired,
            PowerRequestSystemRequired,
            PowerRequestAwayModeRequired,
            PowerRequestMaximum
        }
    }

    private final Kernel32 kernel32;
    private HANDLE handle = null;

    PowerManagement()
    {
        // Found in winnt.h
        ULONG POWER_REQUEST_CONTEXT_VERSION = new ULONG(0);
        DWORD POWER_REQUEST_CONTEXT_SIMPLE_STRING = new DWORD(0x1);

        kernel32 = Native.load("kernel32", Kernel32.class);
        REASON_CONTEXT.ByReference context = new REASON_CONTEXT.ByReference();
        context.version = POWER_REQUEST_CONTEXT_VERSION;
        context.flags = POWER_REQUEST_CONTEXT_SIMPLE_STRING;
        context.simpleReasonString = new LPWSTR("FXPlayer media playing");
        handle = kernel32.PowerCreateRequest(context);
        if (handle == WinBase.INVALID_HANDLE_VALUE)
            throw new AssertionError(Native.getLastError());
    }

    /**
     * Prevent the computer from going to sleep while the application is running.
     */
    public void preventScreensaver()
    {
        if (!kernel32.PowerSetRequest(handle, Kernel32.PowerRequestType.PowerRequestSystemRequired.ordinal()) || !kernel32.PowerSetRequest(handle, Kernel32.PowerRequestType.PowerRequestDisplayRequired.ordinal()))
            throw new AssertionError("PowerSetRequest() failed");
    }

    /**
     * Allow the computer to go to sleep.
     */
    public void allowScreensaver()
    {
        if (!kernel32.PowerClearRequest(handle, Kernel32.PowerRequestType.PowerRequestSystemRequired.ordinal()) || !kernel32.PowerClearRequest(handle, Kernel32.PowerRequestType.PowerRequestDisplayRequired.ordinal()))
            throw new AssertionError("PowerClearRequest() failed");
    }
}