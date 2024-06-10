package pt.cjmach.netbeans.djns.browser;

import chrriis.dj.nativeswing.NSComponentOptions;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.openide.cookies.EditorCookie;
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

    private JWebBrowser browser;
    private boolean needsUpdate = true;

    public HtmlPreviewMultiViewElement(Lookup lookup) {
        super(lookup);
        DataObject htmlObject = getLookup().lookup(DataObject.class);
        htmlObject.addPropertyChangeListener(new PropertyChangeListener() {
            
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                if (DataObject.PROP_MODIFIED.equals(pce.getPropertyName())) {
                    updateName();
                }
            }
        });
    }

    @Override
    public void componentShowing() {
        // TODO: Update content only when there's actually a change.
        // It's currently updating the content everytime we select the view.
        getVisualRepresentation().setHTMLContent(getHTMLContent());
    }
   
    private String getHTMLContent() {
        DataObject htmlObject = getLookup().lookup(DataObject.class);
        EditorCookie cookie = htmlObject.getCookie(EditorCookie.class);
        StyledDocument document = cookie.getDocument();
        if (document == null) {
            return null;
        }
        try {
            return document.getText(0, document.getLength());
        } catch (BadLocationException ex) {
            return "";
        }
        
    }

    @Override
    public JWebBrowser getVisualRepresentation() {
        if (browser == null) {
            browser = new JWebBrowser(NSComponentOptions.destroyOnFinalization());
            browser.setJavascriptEnabled(true);
            browser.setDefaultPopupMenuRegistered(false);
            browser.setBarsVisible(false);
        }
        return browser;
    }
}
