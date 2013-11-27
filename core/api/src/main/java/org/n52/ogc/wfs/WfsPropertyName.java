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
package org.n52.ogc.wfs;

import javax.xml.namespace.QName;

import org.n52.sos.ogc.filter.AbstractProjectionClause;

/**
 * Class for WFS propertyName element
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 1.0.0
 * 
 */
public class WfsPropertyName extends StandardResolveParameters implements AbstractProjectionClause {

    private QName qName;

    private String resolvePath;

    /**
     * Get QName
     * 
     * @return the qName
     */
    public QName getqName() {
        return qName;
    }

    /**
     * Set QName
     * 
     * @param qName
     *            the qName to set
     * @return WfsPropertyName
     */
    public WfsPropertyName setqName(QName qName) {
        this.qName = qName;
        return this;
    }

    /**
     * Get resolve path
     * 
     * @return the resolvePath
     */
    public String getResolvePath() {
        return resolvePath;
    }

    /**
     * Set resolve path
     * 
     * @param resolvePath
     *            the resolvePath to set
     * @return WfsPropertyName
     */
    public WfsPropertyName setResolvePath(String resolvePath) {
        this.resolvePath = resolvePath;
        return this;
    }
}