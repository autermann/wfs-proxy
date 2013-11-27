/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.wfs.request;

import java.util.Map;

import org.n52.ogc.wfs.WfsConstants;
import org.n52.sos.request.AbstractServiceRequest;
import org.n52.sos.util.CollectionHelper;

/**
 * Abstract WFS service request
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 1.0.0
 *
 */
public abstract class AbstractWfsServiceRequest extends AbstractServiceRequest {
    
    /* Attribute for XML, not defined for KVP binding */
    private String handle;
    
    private Map<String, String> namespaces = WfsConstants.defaultNamespaces;
    
    /**
     * Get handle
     * @return the handle
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Set handle
     * @param handle the handle to set
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }

    /**
     * Get namespaces
     * @return the namespaces
     */
    public Map<String, String> getNamespaces() {
        return namespaces;
    }

    /**
     * Set namespaces
     * @param namespaces the namespaces to set
     */
    public void setNamespaces(Map<String, String> namespaces) {
        getNamespaces().putAll(namespaces);
    }
    
    /**
     * Check if namespaces are set
     * @return <code>true</code>, if namespaces are set
     */
    public boolean isSetNamespaces() {
        return CollectionHelper.isNotEmpty(getNamespaces());
    }
    
}