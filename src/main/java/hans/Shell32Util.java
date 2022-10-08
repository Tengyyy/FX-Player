package hans;

import com.sun.jna.platform.win32.*;
import com.sun.jna.*;
import com.sun.jna.ptr.PointerByReference;

import java.io.File;
import java.nio.file.InvalidPathException;

public class Shell32Util extends com.sun.jna.platform.win32.Shell32Util {

    public static Pointer SHParseDisplayName(File file) {
        try {
            PointerByReference ppidl = new PointerByReference();

            // canonicalize file path for Win32 API
            WinNT.HRESULT hres = Shell32.INSTANCE.SHParseDisplayName(new WString(file.getCanonicalPath()), null, ppidl, new WinDef.ULONG(0), null);
            if (W32Errors.FAILED(hres)) {
                throw new Win32Exception(hres);
            }

            return ppidl.getValue();
        } catch (Exception e) {
            throw new InvalidPathException(file.getPath(), e.getMessage());
        }
    }

    public static void SHOpenFolderAndSelectItems(File file) {
        Pointer pidlFolder = SHParseDisplayName(file);

        try {
            WinNT.HRESULT hres = Shell32.INSTANCE.SHOpenFolderAndSelectItems(pidlFolder, new WinDef.UINT(0), null, new WinDef.DWORD(0));
            if (W32Errors.FAILED(hres)) {
                throw new Win32Exception(hres);
            }
        } catch (Exception e) {
            throw new InvalidPathException(file.getPath(), e.getMessage());
        }
    }

}
