/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chrriis.dj.nativeswing.swtcore.cocoa.macosx.x86_64;

/**
 *
 * @author mach
 */
import chrriis.dj.nativeswing.swtimpl.internal.NativeCoreObjectFactory;
import chrriis.dj.nativeswing.swtimpl.netbeans.NativeCoreAPIProvider;

import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = NativeCoreAPIProvider.class)
public class APIProvider implements NativeCoreAPIProvider {

    @Override
    public NativeCoreObjectFactory getObjectFactory() {
        return new NativeCoreObjectFactory(APIProvider.class.getClassLoader());
    }
}

