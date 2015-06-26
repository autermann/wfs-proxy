/*
 * Copyright 2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.wfs.request;

import org.n52.ogc.wfs.WfsConstants;
import org.n52.wfs.response.ListStoredQueriesResponse;

/**
 * WFS ListStoredQueries service request
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 *
 * @since 1.0.0
 *
 */
public class ListStoredQueriesRequest extends AbstractWfsServiceRequest<ListStoredQueriesResponse> {

    private ListStoredQueriesResponse response;

    public void setResponse(ListStoredQueriesResponse response) {
        this.response = response;
    }

    @Override
    public ListStoredQueriesResponse getResponse() {
        return response;
    }

    @Override
    public String getOperationName() {
        return WfsConstants.Operations.ListStoredQueries.name();
    }

}
