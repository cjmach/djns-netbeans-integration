/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.cjmach.netbeans.djns.browser;

import org.openide.awt.HtmlBrowser;

/**
 *
 * @author mach
 */
public class DjnsBrowserFactory implements HtmlBrowser.Factory {
    
    public static boolean isHidden() {
        return false;
    }

    @Override
    public HtmlBrowser.Impl createHtmlBrowserImpl() {
        return new DjnsBrowserImpl();
    }
}
