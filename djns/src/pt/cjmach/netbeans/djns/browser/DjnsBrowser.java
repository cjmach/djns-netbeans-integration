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

import java.awt.Component;
import org.openide.awt.HtmlBrowser;

/**
 *
 * @author cmachado
 */
public class DjnsBrowser extends HtmlBrowser {
    public DjnsBrowser() {
        super(new DjnsBrowserFactory(), true, true);
    }
    
    public DjnsBrowser(boolean toolbar, boolean statusLine) {
        super(new DjnsBrowserFactory(), toolbar, statusLine);
    }

    public DjnsBrowser(boolean toolbar, boolean statusLine, Component extraToolbar) {
        super(new DjnsBrowserFactory(), toolbar, statusLine, extraToolbar);
    }
}
