package fxplayer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.W32APIOptions;

public interface Shell32 extends com.sun.jna.platform.win32.Shell32 {
    Shell32 INSTANCE = Native.load("shell32", Shell32.class, W32APIOptions.DEFAULT_OPTIONS);

    WinNT.HRESULT SHParseDisplayName(WString pszName, Pointer pbc, PointerByReference ppidl, WinDef.ULONG sfgaoIn, Pointer psfgaoOut);
    WinNT.HRESULT SHOpenFolderAndSelectItems(Pointer pidlFolder, WinDef.UINT cidl, Pointer apidl, WinDef.DWORD dwFlags);
}