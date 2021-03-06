/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.shoppingcart.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.yes.cart.constants.Constants;
import org.yes.cart.domain.entity.CarrierSla;
import org.yes.cart.domain.entity.CustomerOrder;
import org.yes.cart.domain.entity.CustomerOrderDelivery;
import org.yes.cart.domain.entity.Warehouse;
import org.yes.cart.service.domain.WarehouseService;
import org.yes.cart.shoppingcart.DeliveryTimeEstimationVisitor;
import org.yes.cart.util.ShopCodeContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: denispavlov
 * Date: 07/02/2017
 * Time: 07:36
 */
public class DeliveryTimeEstimationVisitorImpl implements DeliveryTimeEstimationVisitor {

    private final WarehouseService warehouseService;

    public DeliveryTimeEstimationVisitorImpl(final WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    /** {@inheritDoc} */
    public void visit(final CustomerOrder order) {

        final Collection<CustomerOrderDelivery> deliveries = order.getDelivery();
        if (CollectionUtils.isNotEmpty(deliveries)) {

            final Map<String, Warehouse> warehouseByCode = getFulfilmentCentresMap(order);

            for (final CustomerOrderDelivery delivery : deliveries) {

                determineDeliveryTime(delivery, warehouseByCode);

            }

        }

    }

    /** {@inheritDoc} */
    public void visit(final CustomerOrderDelivery delivery) {

        final Map<String, Warehouse> warehouseByCode = getFulfilmentCentresMap(delivery.getCustomerOrder());

        determineDeliveryTime(delivery, warehouseByCode);

    }

    /**
     * Fulfilment centres map.
     *
     * @param order order
     *
     * @return applicable suppliers
     */
    protected Map<String, Warehouse> getFulfilmentCentresMap(final CustomerOrder order) {
        return warehouseService.getByShopIdMapped(order.getShop().getShopId(), false);
    }

    /**
     * Visit single delivery.
     *
     * @param customerOrderDelivery customer order delivery
     * @param warehouseByCode       all fulfilment centres
     */
    protected void determineDeliveryTime(final CustomerOrderDelivery customerOrderDelivery, final Map<String, Warehouse> warehouseByCode) {

        if (!CustomerOrderDelivery.ELECTRONIC_DELIVERY_GROUP.equals(customerOrderDelivery.getDeliveryGroup())) {

            Calendar minDeliveryTime = now();
            minDeliveryTime.set(Calendar.HOUR_OF_DAY, 0);
            minDeliveryTime.set(Calendar.MINUTE, 0);
            minDeliveryTime.set(Calendar.SECOND, 0);
            minDeliveryTime.set(Calendar.MILLISECOND, 0);

            skipInventoryLeadTime(customerOrderDelivery, warehouseByCode, minDeliveryTime);

            final CarrierSla sla = customerOrderDelivery.getCarrierSla();
            final Map<Date, Date> slaExcludedDates = getCarrierSlaExcludedDates(sla);

            if (sla.getMinDays() > 0) {
                minDeliveryTime.add(Calendar.DAY_OF_YEAR, sla.getMinDays());
                skipWeekdayExclusions(sla, minDeliveryTime);
                skipDatesExclusions(sla, minDeliveryTime, slaExcludedDates);
            }

            Date guaranteed = null;
            Date min = null;
            Date max = null;

            if (sla.isGuaranteed()) {
                if (CustomerOrderDelivery.STANDARD_DELIVERY_GROUP.equals(customerOrderDelivery.getDeliveryGroup())) {
                    // guarantee is only for in stock items
                    guaranteed = minDeliveryTime.getTime();
                } else {
                    min = minDeliveryTime.getTime();
                }

            } else {
                min = minDeliveryTime.getTime();
                if (sla.getMaxDays() > sla.getMinDays()) {
                    minDeliveryTime.add(Calendar.DAY_OF_YEAR, sla.getMaxDays() - sla.getMinDays());
                    skipWeekdayExclusions(sla, minDeliveryTime);
                }
                max = minDeliveryTime.getTime();
            }

            // Need to reset all in case we have dirty fields
            customerOrderDelivery.setDeliveryGuaranteed(guaranteed);
            customerOrderDelivery.setDeliveryEstimatedMin(min);
            customerOrderDelivery.setDeliveryEstimatedMax(max);

        }

    }

    /**
     * Skip lead time set by inventory
     *
     * @param customerOrderDelivery delivery
     * @param warehouseByCode       fulfilment centers
     * @param minDeliveryTime       start date (i.e. now)
     */
    protected void skipInventoryLeadTime(final CustomerOrderDelivery customerOrderDelivery, final Map<String, Warehouse> warehouseByCode, final Calendar minDeliveryTime) {

        if (CustomerOrderDelivery.STANDARD_DELIVERY_GROUP.equals(customerOrderDelivery.getDeliveryGroup())) {

            final Warehouse ff = warehouseByCode.get(customerOrderDelivery.getDetail().iterator().next().getSupplierCode());
            if (ff != null && ff.getDefaultStandardStockLeadTime() > 0) {
                minDeliveryTime.add(Calendar.DAY_OF_YEAR, ff.getDefaultStandardStockLeadTime());
            }

        } else {
            // Pre, Back and Mixed (very simplistic) TODO: account for product specific lead times and pre-order release dates
            final Warehouse ff = warehouseByCode.get(customerOrderDelivery.getDetail().iterator().next().getSupplierCode());
            if (ff != null && ff.getDefaultBackorderStockLeadTime() > 0) {
                minDeliveryTime.add(Calendar.DAY_OF_YEAR, ff.getDefaultBackorderStockLeadTime());
            }

        }
    }

    protected Calendar now() {
        return Calendar.getInstance();
    }

    protected Map<Date, Date> getCarrierSlaExcludedDates(final CarrierSla sla) {

        final Map<Date, Date> dates = new HashMap<Date, Date>();
        if (StringUtils.isNotBlank(sla.getExcludeDates())) {

            final SimpleDateFormat df = new SimpleDateFormat(Constants.DEFAULT_IMPORT_DATE_FORMAT);
            final String[] all = StringUtils.split(sla.getExcludeDates(), ',');
            for (final String range : all) {
                try {
                    final int rangePos = range.indexOf(':');
                    if (rangePos == -1) {
                        final Date date = df.parse(range);
                        dates.put(date, date);
                    } else {
                        final Date date = df.parse(range.substring(0, rangePos));
                        final Date date2 = df.parse(range.substring(rangePos + 1));
                        dates.put(date, date2);
                    }
                } catch (ParseException pe) {
                    ShopCodeContext.getLog(this).error(pe.getMessage() + ", sla: " + sla.getCarrierslaId(), pe);
                }
            }

        }
        return dates;

    }

    protected void skipWeekdayExclusions(final CarrierSla sla, final Calendar date) {

        if (StringUtils.isNotBlank(sla.getExcludeWeekDays())) {
            final List<String> excluded = new ArrayList<String>(Arrays.asList(StringUtils.split(sla.getExcludeWeekDays(), ',')));
            while (!excluded.isEmpty()) {
                final int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
                final Iterator<String> itExluded = excluded.iterator();
                boolean removed = false;
                while (itExluded.hasNext()) {
                    if (NumberUtils.toInt(itExluded.next()) == dayOfWeek) {
                        date.add(Calendar.DAY_OF_YEAR, 1); // This date is excluded
                        itExluded.remove();
                        removed = true;
                    }
                }
                if (!removed) {
                    return;
                }
            }
        }

    }


    protected void skipDatesExclusions(final CarrierSla sla, final Calendar date, final Map<Date, Date> exclusions) {

        if (!exclusions.isEmpty()) {

            final TreeSet<Date> startDates = new TreeSet<Date>(exclusions.keySet());

            while (true) {
                final Date thisDate = date.getTime();
                final Date beforeOfEqual = startDates.floor(thisDate);
                if (beforeOfEqual == null) {
                    return; // no exclusions before
                } else if (beforeOfEqual.before(thisDate)) {
                    final Date rangeEnd = exclusions.get(beforeOfEqual);
                    // Two cases here:
                    // 1) Single date - same as beforeOfEqual
                    // 2) Range - need to make sure it is before this date
                    if (thisDate.after(rangeEnd)) {
                        return; // This date is after the min in exclusions
                    } else {
                        thisDate.setTime(rangeEnd.getTime());
                        date.add(Calendar.DAY_OF_YEAR, 1);
                        skipWeekdayExclusions(sla, date);
                    }
                } else {
                    // equal, so need to move next day and check weekdays
                    date.add(Calendar.DAY_OF_YEAR, 1);
                    skipWeekdayExclusions(sla, date);
                }
            }

        }

    }


}
