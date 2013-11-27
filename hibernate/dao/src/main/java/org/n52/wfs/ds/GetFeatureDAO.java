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
package org.n52.wfs.ds;

import org.joda.time.DateTime;
import org.n52.ogc.wfs.OmObservationMember;
import org.n52.ogc.wfs.WfsConstants;
import org.n52.ogc.wfs.WfsFeatureCollection;
import org.n52.ogc.wfs.WfsQuery;
import org.n52.sos.exception.CodedException;
import org.n52.sos.exception.ows.NoApplicableCodeException;
import org.n52.sos.exception.ows.OptionNotSupportedException;
import org.n52.sos.ogc.filter.AbstractSelectionClause;
import org.n52.sos.ogc.filter.BinaryLogicFilter;
import org.n52.sos.ogc.filter.ComparisonFilter;
import org.n52.sos.ogc.filter.Filter;
import org.n52.sos.ogc.filter.FilterConstants.BinaryLogicOperator;
import org.n52.sos.ogc.filter.SpatialFilter;
import org.n52.sos.ogc.filter.TemporalFilter;
import org.n52.sos.ogc.om.OmObservation;
import org.n52.sos.ogc.ows.OwsExceptionReport;
import org.n52.sos.ogc.sos.Sos2Constants;
import org.n52.sos.ogc.sos.SosConstants;
import org.n52.sos.request.GetObservationRequest;
import org.n52.sos.request.operator.RequestOperator;
import org.n52.sos.request.operator.RequestOperatorRepository;
import org.n52.sos.response.GetObservationResponse;
import org.n52.sos.service.operator.ServiceOperatorKey;
import org.n52.wfs.request.GetFeatureRequest;
import org.n52.wfs.response.GetFeatureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WFS DAO class for GetFeature operation
 * 
 * @author Carsten Hollmann <c.hollmann@52north.org>
 * 
 * @since 1.0.0
 * 
 */
public class GetFeatureDAO extends AbstractGetFeatureDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFeatureDAO.class);

    /**
     * constructor
     */
    public GetFeatureDAO() {
        super(WfsConstants.WFS);
    }

    @Override
    public GetFeatureResponse getFeatures(GetFeatureRequest request) throws OwsExceptionReport {
        GetObservationRequest sosRequest = convertWfsGetFeatureToSosGetObservation(request);
        return convertSosGetObservationToWfsGetFeature((GetObservationResponse) getGetObservationRequestOperator()
                .receiveRequest(sosRequest));
    }

    /**
     * Convert a WFS GetFeature request to a SOS GetObservation request
     * 
     * @param request
     *            WFS GetFeature request
     * @return Converted SOS GetObservation request
     * @throws OwsExceptionReport
     *             If an error occurs or a requested parameter is not supported
     */
    private GetObservationRequest convertWfsGetFeatureToSosGetObservation(GetFeatureRequest request)
            throws OwsExceptionReport {
        GetObservationRequest sosRequest = new GetObservationRequest();
        sosRequest.setService(SosConstants.SOS);
        sosRequest.setVersion(Sos2Constants.SERVICEVERSION);
        if (request.isSetBBox()) {
            sosRequest.setSpatialFilter(request.getBBox());
        }
        convertFilterForGetObservation(sosRequest, request);
        return sosRequest;
    }

    /**
     * Convert the WfsQueries to SOS GetObservation request parameter
     * 
     * @param sosRequest
     *            SOS GetObservation request
     * @param request
     *            WFS GetFeature request
     * @throws OwsExceptionReport
     *             If an error occurs or a requested parameter is not supported
     */
    private void convertFilterForGetObservation(GetObservationRequest sosRequest, GetFeatureRequest request)
            throws OwsExceptionReport {
        if (request.isSetQueries()) {
            for (WfsQuery query : request.getQueries()) {
                if (query.isSetSelectionClause()) {
                    int binaryLogicOpLevelCounter = 1;
                    AbstractSelectionClause filter = query.getSelectionClause();
                    if (filter instanceof BinaryLogicFilter) {
                        convertBinaryLogicFilter((BinaryLogicFilter) filter, sosRequest, binaryLogicOpLevelCounter);
                    } else if (filter instanceof ComparisonFilter) {
                        convertComparisonFilter((ComparisonFilter) filter, sosRequest, null);
                    } else if (filter instanceof TemporalFilter) {
                        addTemporalFilter((TemporalFilter) filter, sosRequest);
                    } else if (filter instanceof SpatialFilter) {
                        throw new OptionNotSupportedException()
                                .withMessage(
                                        "SpatialOp filter is not supported in '{}'. Please use parameter '{}' for spatial queries!",
                                        WfsConstants.AdHocQueryParams.Filter.name(),
                                        WfsConstants.AdHocQueryParams.BBox.name());
                    } else {
                        throw new OptionNotSupportedException()
                                .withMessage("The requested filter is not supported in by this service!");
                    }
                }
            }
        }
    }

    /**
     * Convert binary logic filter
     * 
     * @param filter
     *            Binary logic filter to convert
     * @param sosRequest
     *            SOS GetObservation request
     * @param binaryLogicOpLevelCounter
     *            Counter for current filter level
     * @throws OwsExceptionReport
     *             If an error occurs, a requested parameter is not supported or
     *             the level is not supported
     */
    private void convertBinaryLogicFilter(BinaryLogicFilter filter, GetObservationRequest sosRequest,
            int binaryLogicOpLevelCounter) throws OwsExceptionReport {
        ComparisonFilterEquality equalityCheck = null;
        int currentBinaryLogicOpLevel = binaryLogicOpLevelCounter + 1;
        for (Filter<?> filterPredicate : filter.getFilterPredicates()) {
            if (filterPredicate instanceof BinaryLogicFilter) {
                if (currentBinaryLogicOpLevel >= 3) {
                    throw new OptionNotSupportedException()
                            .withMessage("BinaryLogicOps is not supported as 3rd level sub-filter!");
                }
                BinaryLogicFilter binLogFilter = (BinaryLogicFilter) filterPredicate;
                if (BinaryLogicOperator.And.equals(binLogFilter.getOperator()) && currentBinaryLogicOpLevel >= 2) {
                    throw new OptionNotSupportedException()
                            .withMessage("'And'-BinaryLogicOps is not supported as sub-filter!");
                }
                convertBinaryLogicFilter(binLogFilter, sosRequest, currentBinaryLogicOpLevel);
            } else if (filterPredicate instanceof ComparisonFilter) {
                convertComparisonFilter((ComparisonFilter) filterPredicate, sosRequest, equalityCheck);
            } else if (filterPredicate instanceof TemporalFilter) {
                TemporalFilter temporalFilter = (TemporalFilter) filterPredicate;
                if (!isPhenomenonTimeFilter(temporalFilter)) {
                    throw new OptionNotSupportedException()
                            .withMessage("Only temporal filters for valueReference = 'om:phenomenonTime' are yet supported!");
                }
                addTemporalFilter((TemporalFilter) filterPredicate, sosRequest);
            } else if (filterPredicate instanceof SpatialFilter) {
                if (sosRequest.isSetSpatialFilter()) {
                    throw new NoApplicableCodeException()
                            .withMessage("This service supports only one spatial filter per request!");
                }
                sosRequest.setSpatialFilter((SpatialFilter) filterPredicate);
            } else {
                throw new OptionNotSupportedException()
                        .withMessage("The requested filter is not supported in by this service!");
            }
        }
    }

    /**
     * Convert comparison filter
     * 
     * @param filter
     *            Comparison filter to convert
     * @param request
     *            SOS GetObservation request
     * @param equalityCheck
     *            Indicator for valid ComparsionFilterEquality value
     * @throws CodedException
     *             If an error occurs or a requested parameter is not supported
     */
    private void convertComparisonFilter(ComparisonFilter filter, GetObservationRequest request,
            ComparisonFilterEquality equalityCheck) throws CodedException {
        ComparisonFilterEquality currentComparisonFilterEquality =
                ComparisonFilterEquality.fromValue(filter.getValueReference());
        if (equalityCheck == null) {
            equalityCheck = currentComparisonFilterEquality;
        }
        if (currentComparisonFilterEquality.equals(equalityCheck)) {
            switch (currentComparisonFilterEquality) {
            case Procedure:
                addProcedureIdentifierFromFilter(filter, request);
                break;
            case ObervedProperty:
                addObservedPropertyIdentifierFromFilter(filter, request);
                break;
            case FeatureOfInterest:
                addFeatureOfInterestIdentifierFromFilter(filter, request);
                break;
            default:
                throw new NoApplicableCodeException().withMessage(
                        "The requested valueReference ({}) is not supported by this service",
                        filter.getValueReference());
            }
        } else {
            throw new NoApplicableCodeException()
                    .withMessage("The requested filter in an 'Or' filter do not have the same valueReferende");
        }
    }

    /**
     * Add procedure parameter value to SOS GetObservation request from
     * comparison filter
     * 
     * @param filter
     *            Comparison filter with procedure parameter value
     * @param request
     *            SOS GetObservation request
     */
    private void addProcedureIdentifierFromFilter(ComparisonFilter filter, GetObservationRequest request) {
        request.getProcedures().add(filter.getValue());
    }

    /**
     * Add observedProperty parameter value to SOS GetObservation request from
     * comparison filter
     * 
     * @param filter
     *            Comparison filter with observedProperty parameter value
     * @param request
     *            SOS GetObservation request
     */
    private void addObservedPropertyIdentifierFromFilter(ComparisonFilter filter, GetObservationRequest request) {
        request.getObservedProperties().add(filter.getValue());
    }

    /**
     * Add featureOfInterest parameter value to SOS GetObservation request from
     * comparison filter
     * 
     * @param filter
     *            Comparison filter with featureOfInterest parameter value
     * @param request
     *            SOS GetObservation request
     */
    private void addFeatureOfInterestIdentifierFromFilter(ComparisonFilter filter, GetObservationRequest request) {
        request.getFeatureIdentifiers().add(filter.getValue());
    }

    /**
     * Check if temporal filter is a phenomeon time filter
     * 
     * @param filter
     *            Temporal filter to check
     * @return <code>true</code>, if temporal filter is a phenomeon time filter
     */
    private boolean isPhenomenonTimeFilter(TemporalFilter filter) {
        return checkValueReference(filter, ComparisonFilterEquality.PhenomenonTime);
    }

    /**
     * Add temporal filter to SOS GetObservation request
     * 
     * @param filter
     *            Temporal filter to add
     * @param request
     *            SOS GetObservation request
     */
    private void addTemporalFilter(TemporalFilter filter, GetObservationRequest request) {
        request.getTemporalFilters().add(filter);
    }

    /**
     * Check if valueReference value equals expected phenomenon time
     * valueReference value
     * 
     * @param filter
     *            Filter to check
     * @param phenomenontime
     *            Phenomenon time ComparisonFilterEquality enum
     * @return <code>true</code>, if valueReference value equals expected
     *         phenomenon time valueReference value
     */
    private boolean checkValueReference(Filter<?> filter, ComparisonFilterEquality phenomenontime) {
        return phenomenontime.isValueReference(filter.getValueReference());
    }

    /**
     * Convert a SOS GetObservation response to a WFS GetFeature response
     * 
     * @param sosResponse
     *            SOS GetObservation response to convert
     * @return Converted WFS GetFeature response
     */
    private GetFeatureResponse convertSosGetObservationToWfsGetFeature(GetObservationResponse sosResponse) {
        GetFeatureResponse response = new GetFeatureResponse();
        response.setService(WfsConstants.WFS);
        response.setVersion(WfsConstants.VERSION);
        WfsFeatureCollection featureCollection =
                new WfsFeatureCollection(new DateTime(), WfsConstants.NUMBER_MATCHED_UNKNOWN);
        for (OmObservation omObservation : sosResponse.getObservationCollection()) {
            featureCollection.addMember(new OmObservationMember(omObservation));
        }
        response.setFeatureCollection(featureCollection);
        return response;
    }

    /**
     * Get SOS GetObservation request operator to execute request
     * 
     * @return SOS GetObservation request operator
     */
    private RequestOperator getGetObservationRequestOperator() {
        return RequestOperatorRepository.getInstance().getRequestOperator(
                new ServiceOperatorKey(SosConstants.SOS, Sos2Constants.SERVICEVERSION),
                SosConstants.Operations.GetObservation.name());
    }

    /**
     * Enum for supported valueReference values in ComparisonOps
     * 
     * @author Carsten Hollmann <c.hollmann@52north.org>
     * 
     * @since 1.0.0
     * 
     */
    enum ComparisonFilterEquality {
        Procedure("om:procedure"), ObervedProperty("om:observedProperty"), FeatureOfInterest("om:featureOfInterest"), PhenomenonTime(
                "om:phenomenonTime");

        private String value;

        /**
         * Constructor
         * 
         * @param value
         */
        private ComparisonFilterEquality(String value) {
            this.value = value;
        }

        /**
         * Check if valueReference is supported
         * 
         * @param valueReference
         *            ValueReference value to check
         * @return <code>true</code>, if valueReference is supported
         */
        public boolean isValueReference(String valueReference) {
            return value.equals(valueReference);
        }

        /**
         * Get enum for string value
         * 
         * @param value
         *            String value to get enum for
         * @return The corresponding enum or null if no enum exists
         */
        public static ComparisonFilterEquality fromValue(String value) {
            for (ComparisonFilterEquality comparisonFilterEquality : ComparisonFilterEquality.values()) {
                if (comparisonFilterEquality.value.equals(value)) {
                    return comparisonFilterEquality;
                }
            }
            return null;
        }

    }
}