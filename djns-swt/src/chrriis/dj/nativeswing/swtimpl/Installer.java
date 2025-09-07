/*
 * Copyright (C) 2022-2025  Carlos Machado
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package chrriis.dj.nativeswing.swtimpl;

import chrriis.dj.nativeswing.common.SystemProperty;
import chrriis.dj.nativeswing.common.Utils;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openide.modules.ModuleInstall;

/**
 * The NetBeans integration object responsible for the activation of the appropriate platform core module.
 * @author Christopher Deckers
 * @author Carlos Machado
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        Map<String, String> modelMap = new HashMap<>();
        modelMap.put("Windows.64", "chrriis.dj.nativeswing.swtcore.win32.win32.x86_64");
        modelMap.put("Windows.aarch64", "chrriis.dj.nativeswing.swtcore.win32.win32.aarch64");
        modelMap.put("Linux.64", "chrriis.dj.nativeswing.swtcore.gtk.linux.x86_64");
        modelMap.put("Linux.ppc64", "chrriis.dj.nativeswing.swtcore.gtk.linux.ppc64");
        modelMap.put("Linux.aarch64", "chrriis.dj.nativeswing.swtcore.gtk.linux.aarch64");
        modelMap.put("Linux.riscv64", "chrriis.dj.nativeswing.swtcore.gtk.linux.riscv64");
        modelMap.put("Mac.64", "chrriis.dj.nativeswing.swtcore.cocoa.macosx.x86_64");
        modelMap.put("Mac.aarch64", "chrriis.dj.nativeswing.swtcore.cocoa.macosx.aarch64");
        String osArch;
        if (Utils.IS_64_BIT) {
            osArch = "64";
        } else {
            throw new UnsupportedOperationException("32-bit systems are not supported.");
        }
        String osName;
        if (Utils.IS_WINDOWS) {
            osName = "Windows";
            String sysOsArch = SystemProperty.OS_ARCH.get();
            if (sysOsArch.startsWith("arm") || sysOsArch.startsWith("aarch")) {
                osArch = "aarch64";
            }
        } else if (Utils.IS_MAC) {
            osName = "Mac";
            String sysOsArch = SystemProperty.OS_ARCH.get();
            if (sysOsArch.startsWith("arm") || sysOsArch.startsWith("aarch")) {
                osArch = "aarch64";
            }
        } else {
            osName = "Linux";
            String sysOsArch = SystemProperty.OS_ARCH.get();
            if ("ppc64".equals(sysOsArch)) {
                osArch = "ppc64";
            } else if (sysOsArch.startsWith("arm") || sysOsArch.startsWith("aarch")) {
                osArch = "aarch64";
            } else if (sysOsArch.startsWith("riscv")) {
                osArch = "riscv64";
            }
        }
        Map<String, String> osNameMap = new HashMap<>();
        osNameMap.put("Windows", "Windows");
        osNameMap.put("Linux", "Linux");
        osNameMap.put("Mac", "Mac");
        String toEnable = modelMap.get(osNameMap.get(osName) + "." + osArch);
        Set<String> toDisable = new HashSet<>(modelMap.values());
        if (toEnable != null) {
            toDisable.remove(toEnable);
        }
        ModuleHandler disablerModuleHandler = new ModuleHandler(true);
        disablerModuleHandler.setModulesState(toDisable, false);
        ModuleHandler enablerModuleHandler = new ModuleHandler(true);
        enablerModuleHandler.setModulesState(Collections.singleton(toEnable), true);
    }

}
