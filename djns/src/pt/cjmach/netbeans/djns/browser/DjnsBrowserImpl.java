/*
 * Copyright (C) 2024  Carlos Machado
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

import chrriis.dj.nativeswing.NSComponentOptions;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserListener;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;
import chrriis.dj.nativeswing.swtimpl.netbeans.NativeInterfaceNetBeansHandler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;

/**
 *
 * @author cmachado
 */
public class DjnsBrowserImpl extends HtmlBrowser.Impl {

    private static final Logger LOGGER = Logger.getLogger(DjnsBrowserImpl.class.getName());
    private static final int PROGRESS_MAX_VALUE = 100;

    private JWebBrowser browser;
    private final PropertyChangeSupport propChangeSupport;
    private boolean canGoBackward;
    private boolean canGoForward;
    private URL url;
    private String statusMessage = "";
    private String title = "";
    private ProgressHandle progressHandle;
    private int progress;
    private final WebBrowserListener browserListener;

    static {
        NativeInterfaceNetBeansHandler.initialize();
        NativeInterface.open();
    }

    public DjnsBrowserImpl() {
        browserListener = new BrowserListener();
        propChangeSupport = new PropertyChangeSupport(this);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (browser != null) {
            browser.removeWebBrowserListener(browserListener);
            browser.disposeNativePeer(false);
            browser = null;
        }
    }

    @Override
    public JWebBrowser getComponent() {
        if (browser == null) {
            browser = new JWebBrowser(NSComponentOptions.destroyOnFinalization());
            browser.setDefaultPopupMenuRegistered(false);
            browser.setBarsVisible(false);
            browser.addWebBrowserListener(browserListener);
        }
        return browser;
    }

    @Override
    public void reloadDocument() {
        getComponent().reloadPage();
    }

    @Override
    public void stopLoading() {
        getComponent().stopLoading();
    }

    @Override
    public void setURL(URL newUrl) {
        if (!newUrl.equals(url)) {
            getComponent().navigate(newUrl.toString());
            URL old = url;
            url = newUrl;
            propChangeSupport.firePropertyChange(PROP_URL, old, newUrl);
        }
    }

    public void setUrl(String newUrl) {
        try {
            setURL(new URL(newUrl));
        } catch (MalformedURLException ex) {
            LOGGER.log(Level.FINE, null, ex);
        }
    }

    @Override
    public URL getURL() {
        return url;
    }

    @Override
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String newMessage) {
        if (!newMessage.equals(statusMessage)) {
            String old = statusMessage;
            statusMessage = newMessage;
            propChangeSupport.firePropertyChange(PROP_STATUS_MESSAGE, old, newMessage);
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        if (!newTitle.equals(title)) {
            String old = title;
            title = newTitle;
            propChangeSupport.firePropertyChange(PROP_TITLE, old, newTitle);
        }
    }

    @Override
    public boolean isForward() {
        return canGoForward;
    }

    @Override
    public void forward() {
        getComponent().navigateForward();
    }

    @Override
    public boolean isBackward() {
        return canGoBackward;
    }

    @Override
    public void backward() {
        getComponent().navigateBack();
    }

    @Override
    public boolean isHistory() {
        return false;
    }

    @Override
    public void showHistory() {
        // do nothing
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pl) {
        propChangeSupport.addPropertyChangeListener(pl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pl) {
        propChangeSupport.removePropertyChangeListener(pl);
    }

    private void updateBackAndForward() {
        final boolean oldForward = canGoForward;
        final boolean oldBackward = canGoBackward;
        canGoForward = getComponent().isForwardNavigationEnabled();
        canGoBackward = getComponent().isBackNavigationEnabled();
        if (canGoBackward != oldBackward) {
            propChangeSupport.firePropertyChange(PROP_BACKWARD, oldBackward, canGoBackward);
        }
        if (canGoForward != oldForward) {
            propChangeSupport.firePropertyChange(PROP_FORWARD, oldForward, canGoForward);
        }
    }

    private synchronized void updateProgress() {
        if (progressHandle == null) {
            String msg = NbBundle.getMessage(DjnsBrowserImpl.class, "CTL_BrowserProgressMessage"); // NOI18N
            progressHandle = ProgressHandle.createHandle(msg);
            progressHandle.setInitialDelay(0);
            progressHandle.start(PROGRESS_MAX_VALUE);
            progress = 0;
        }
        int newProgress = browser.getLoadingProgress();
        if (newProgress < progress || newProgress == PROGRESS_MAX_VALUE) {
            progressHandle.finish();
            progressHandle = null;
            propChangeSupport.firePropertyChange(PROP_LOADING, true, false);
        } else {
            progressHandle.progress(newProgress);
            progress = newProgress;
            propChangeSupport.firePropertyChange(PROP_LOADING, false, true);
        }
    }

    class BrowserListener extends WebBrowserAdapter {

        @Override
        public void windowWillOpen(WebBrowserWindowWillOpenEvent e) {
            String text = e.getWebBrowser().getStatusText();
            try {
                URL url = new URL(text);
                HtmlBrowser.URLDisplayer.getDefault().showURL(url);
                e.consume();
            } catch (MalformedURLException ex) {
                LOGGER.log(Level.FINE, null, ex);
            }
        }

        @Override
        public void locationChanged(WebBrowserNavigationEvent wbne) {
            String encoded = wbne.getNewResourceLocation();
            String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
            setUrl(decoded);
            updateBackAndForward();
        }

        @Override
        public void loadingProgressChanged(WebBrowserEvent wbe) {
            updateProgress();
        }

        @Override
        public void titleChanged(WebBrowserEvent wbe) {
            setTitle(wbe.getWebBrowser().getPageTitle());
        }

        @Override
        public void statusChanged(WebBrowserEvent wbe) {
            String encoded = wbe.getWebBrowser().getStatusText();
            String decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
            setStatusMessage(decoded);
        }
    }
}
