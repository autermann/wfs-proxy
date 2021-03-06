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
package org.n52.ogc.wfs;

import java.util.List;

import org.joda.time.DateTime;

import org.n52.iceland.util.CollectionHelper;

import com.google.common.collect.Lists;

/**
 * Class represents a WFS feature collection element
 *
 * @author Carsten Hollmann <c.hollmann@52north.org>
 *
 * @since 1.0.0
 *
 */
public class WfsFeatureCollection extends StandardResponseParameter {

    @SuppressWarnings("rawtypes")
    private List<WfsMember> member = Lists.newArrayList();

    /**
     * constructor
     *
     * @param timeStamp
     *            Required time stamp attribute
     * @param numberMatched
     *            Required number matched attribute
     */
    public WfsFeatureCollection(DateTime timeStamp, String numberMatched) {
        super(timeStamp, numberMatched);
    }

    /**
     * Get members
     *
     * @return the members
     */
    @SuppressWarnings("rawtypes")
    public List<WfsMember> getMember() {
        return member;
    }

    /**
     * Add a new member
     *
     * @param member
     *            the member to add
     */
    @SuppressWarnings("rawtypes")
    public void addMember(WfsMember member) {
        getMember().add(member);
    }

    /**
     * Add new members
     *
     * @param member
     *            the members to add
     */
    @SuppressWarnings("rawtypes")
    public void addMember(List<WfsMember> member) {
        this.member.addAll(member);
    }

    /**
     * Set new members
     *
     * @param member
     *            the members to set
     */
    @SuppressWarnings("rawtypes")
    public void setMember(List<WfsMember> member) {
        this.member = member;
    }

    @Override
    public int getNumberReturned() {
        return getMember().size();
    }

    /**
     * Check if members are set
     *
     * @return <code>true</code>, if members are set
     */
    public boolean isSetMembers() {
        return CollectionHelper.isNotEmpty(getMember());
    }

}
