/*
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.wfs.ds;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.n52.iceland.cache.WritableContentCache;
import org.n52.iceland.coding.CodingRepository;
import org.n52.iceland.coding.decode.DecoderRepository;
import org.n52.iceland.exception.ows.NoApplicableCodeException;
import org.n52.iceland.exception.ows.OwsExceptionReport;
import org.n52.iceland.ogc.ows.OWSConstants;
import org.n52.iceland.ogc.ows.OwsCapabilities;
import org.n52.iceland.ogc.sos.SosConstants;
import org.n52.iceland.response.GetCapabilitiesResponse;
import org.n52.sos.util.CodingHelper;
import org.n52.sos.util.XmlHelper;
import org.n52.wfs.cache.InMemoryCacheImpl;
import org.n52.wfs.cache.WfsCacheFeederHandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WfsCacheFeederDAO implements WfsCacheFeederHandler {
    
    @Inject
    private HttpClientHandler httpClientHandler;
    
    @Inject
    private CodingRepository codingReposiory;

    @Override
    public void updateCache(WritableContentCache cache) throws OwsExceptionReport {
        Object object =
                CodingHelper.decodeXmlElement(XmlHelper.parseXmlString(httpClientHandler.doGet(getParameter())));
        if (object instanceof GetCapabilitiesResponse) {
            ((InMemoryCacheImpl)cache).setOwsCapabilities(((GetCapabilitiesResponse) object).getCapabilities());
        } else if (object instanceof OwsCapabilities) {
            ((InMemoryCacheImpl)cache).setOwsCapabilities((OwsCapabilities) object);
        } else if (object instanceof OwsExceptionReport) {
            throw new NoApplicableCodeException().causedBy((OwsExceptionReport) object).withMessage("error");
        } else {
            throw new NoApplicableCodeException().withMessage("error");
        }
    }
    
    private Map<String, List<String>> getParameter() {
        Map<String, List<String>> parameter = Maps.newHashMap();
        parameter.put(OWSConstants.GetCapabilitiesParams.service.name(), Lists.newArrayList(SosConstants.SOS));
        parameter.put(OWSConstants.GetCapabilitiesParams.request.name(),
                Lists.newArrayList(OWSConstants.Operations.GetCapabilities.name()));
        parameter.put(OWSConstants.GetCapabilitiesParams.Sections.name(),
                Lists.newArrayList(SosConstants.CapabilitiesSections.OperationsMetadata.name()));
        return parameter;
    }
    
}
