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
package pt.cjmach.netbeans.djns.browser;

import java.awt.EventQueue;
import java.util.Collection;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.awt.HtmlBrowser;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author cmachado
 */
@MultiViewElement.Registration(displayName = "#LBL_HTMLPREVIEW_ELEM",
        // iconBase = "org/myorg/abcfiletype/Datasource.gif",
        mimeType = "text/html",
        persistenceType = TopComponent.PERSISTENCE_NEVER,
        preferredID = "HtmlPreviewMultiViewElement",
        position = 3000)
@NbBundle.Messages({
    "LBL_HTMLPREVIEW_ELEM=Preview"
})
public class HtmlPreviewMultiViewElement extends MultiViewEditorElement {

    private HtmlBrowser browser;
    Lookup.Result<SaveCookie> saveCookieResult;

    public HtmlPreviewMultiViewElement(Lookup lookup) {
        super(lookup);
        DataObject htmlObject = getLookup().lookup(DataObject.class);
        htmlObject.addPropertyChangeListener((pce) -> {
            if (DataObject.PROP_MODIFIED.equals(pce.getPropertyName())) {
                EventQueue.invokeLater(() -> updateName());
            }
        });
        saveCookieResult = getLookup().lookupResult(SaveCookie.class);
        saveCookieResult.addLookupListener((e) -> {
            Collection<? extends SaveCookie> cookies = saveCookieResult.allInstances();
            if (cookies.isEmpty()) {
                if (browser != null) {
                    EventQueue.invokeLater(() -> {
                        browser.getBrowserImpl().reloadDocument();
                    });
                }
            }
        });
    }

    @Override
    public HtmlBrowser getVisualRepresentation() {
        if (browser == null) {
            browser = new DjnsBrowser(false, false);
            
            DataObject htmlObject = getLookup().lookup(DataObject.class);
            FileObject file = htmlObject.getPrimaryFile();
            browser.setURL(file.toURL());
        }
        return browser;
    }
}
