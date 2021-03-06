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
package org.yes.cart.domain.dto.impl;

import com.inspiresoftware.lib.dto.geda.annotations.Dto;
import com.inspiresoftware.lib.dto.geda.annotations.DtoCollection;
import com.inspiresoftware.lib.dto.geda.annotations.DtoField;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDTO;
import org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO;
import org.yes.cart.domain.dto.matcher.impl.IdentifiableMatcher;
import org.yes.cart.domain.entity.CustomerOrderDeliveryDet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 *
 * Dto to represent delivery from order.
 * @see org.yes.cart.domain.entity.CustomerOrderDelivery for more details.
 *
 * User: iazarny@yahoo.com
 * Date: 8/17/12
 * Time: 7:20 AM
 */
@Dto
public class CustomerOrderDeliveryDTOImpl implements CustomerOrderDeliveryDTO {

    private static final long serialVersionUID = 20120817L;

    @DtoField(value = "customerOrderDeliveryId", readOnly = true)
    private long customerOrderDeliveryId;

    @DtoField(value = "deliveryNum", readOnly = true)
    private String deliveryNum;

    @DtoField(value = "refNo", readOnly = true)
    private String refNo;

    @DtoField(value = "deliveryStatus", readOnly = true)
    private String deliveryStatus;

    @DtoField(value = "carrierSla.name", readOnly = true)
    private String carrierSlaName;

    @DtoField(value = "carrierSla.carrier.name", readOnly = true)
    private String carrierName;

    @DtoField(value = "customerOrder.ordernum", readOnly = true)
    private String ordernum;

    @DtoField(value = "customerOrder.shippingAddress", readOnly = true)
    private String shippingAddress;

    @DtoField(value = "customerOrder.billingAddress", readOnly = true)
    private String billingAddress;

    @DtoField(value = "customerOrder.currency", readOnly = true)
    private String currency;

    @DtoField(value = "customerOrder.shop.name", readOnly = true)
    private String shopName;

    @DtoField(value = "customerOrder.pgLabel", readOnly = true)
    private String pgLabel;

    private boolean supportCaptureMore;

    private boolean supportCaptureLess;

    @DtoField(value = "deliveryGroup", readOnly = true)
    private String deliveryGroup;

    @DtoField(value = "deliveryRemarks", readOnly = true)
    private String deliveryRemarks;
    @DtoField(value = "deliveryEstimatedMin", readOnly = true)
    private Date deliveryEstimatedMin;
    @DtoField(value = "deliveryEstimatedMax", readOnly = true)
    private Date deliveryEstimatedMax;
    @DtoField(value = "deliveryGuaranteed", readOnly = true)
    private Date deliveryGuaranteed;


    @DtoField(value = "price", readOnly = true)
    private BigDecimal price;
    @DtoField(value = "listPrice", readOnly = true)
    private BigDecimal listPrice;
    @DtoField(value = "promoApplied", readOnly = true)
    private boolean promoApplied;
    @DtoField(value = "appliedPromo", readOnly = true)
    private String appliedPromo;

    @DtoField(value = "netPrice", readOnly = true)
    private BigDecimal netPrice;
    @DtoField(value = "grossPrice", readOnly = true)
    private BigDecimal grossPrice;
    @DtoField(value = "taxRate", readOnly = true)
    private BigDecimal taxRate;
    @DtoField(value = "taxCode", readOnly = true)
    private String taxCode;
    @DtoField(value = "taxExclusiveOfPrice", readOnly = true)
    private boolean taxExclusiveOfPrice;

    @DtoCollection(
            value = "detail",
            dtoBeanKey = "org.yes.cart.domain.dto.CustomerOrderDeliveryDetailDTO",
            entityGenericType =  CustomerOrderDeliveryDet.class,
            entityCollectionClass = ArrayList.class,
            dtoCollectionClass = ArrayList.class,
            dtoToEntityMatcher = IdentifiableMatcher.class,
            readOnly = true
    )
    private Collection<CustomerOrderDeliveryDetailDTO> detail;

    @DtoField(value = "blockExport", readOnly = true)
    private boolean blockExport;
    @DtoField(value = "lastExportDate", readOnly = true)
    private Date lastExportDate;
    @DtoField(value = "lastExportStatus", readOnly = true)
    private String lastExportStatus;
    @DtoField(value = "lastExportDeliveryStatus", readOnly = true)
    private String lastExportDeliveryStatus;


    /** {@inheritDoc} */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /** {@inheritDoc} */
    public void setShippingAddress(final String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    /** {@inheritDoc} */
    public String getBillingAddress() {
        return billingAddress;
    }

    /** {@inheritDoc} */
    public void setBillingAddress(final String billingAddress) {
        this.billingAddress = billingAddress;
    }

    /** {@inheritDoc} */
    public String getCurrency() {
        return currency;
    }

    /** {@inheritDoc} */
    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    /** {@inheritDoc} */
    public String getShopName() {
        return shopName;
    }

    /** {@inheritDoc} */
    public void setShopName(final String shopName) {
        this.shopName = shopName;
    }

    /** {@inheritDoc} */
    public Collection<CustomerOrderDeliveryDetailDTO> getDetail() {
        return detail;
    }

    /** {@inheritDoc} */
    public void setDetail(final Collection<CustomerOrderDeliveryDetailDTO> detail) {
        this.detail = detail;
    }

    /** {@inheritDoc} */
    public BigDecimal getPrice() {
        return price;
    }

    /** {@inheritDoc} */
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    /** {@inheritDoc} */
    public BigDecimal getListPrice() {
        return listPrice;
    }

    /** {@inheritDoc} */
    public void setListPrice(final BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    /** {@inheritDoc} */
    public BigDecimal getNetPrice() {
        return netPrice;
    }

    /** {@inheritDoc} */
    public void setNetPrice(final BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    /** {@inheritDoc} */
    public BigDecimal getGrossPrice() {
        return grossPrice;
    }

    /** {@inheritDoc} */
    public void setGrossPrice(final BigDecimal grossPrice) {
        this.grossPrice = grossPrice;
    }

    /** {@inheritDoc} */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /** {@inheritDoc} */
    public void setTaxRate(final BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /** {@inheritDoc} */
    public String getTaxCode() {
        return taxCode;
    }

    /** {@inheritDoc} */
    public void setTaxCode(final String taxCode) {
        this.taxCode = taxCode;
    }

    /** {@inheritDoc} */
    public boolean isTaxExclusiveOfPrice() {
        return taxExclusiveOfPrice;
    }

    /** {@inheritDoc} */
    public void setTaxExclusiveOfPrice(final boolean taxExclusiveOfPrice) {
        this.taxExclusiveOfPrice = taxExclusiveOfPrice;
    }

    /** {@inheritDoc} */
    public boolean isPromoApplied() {
        return promoApplied;
    }

    /** {@inheritDoc} */
    public void setPromoApplied(final boolean promoApplied) {
        this.promoApplied = promoApplied;
    }

    /** {@inheritDoc} */
    public String getAppliedPromo() {
        return appliedPromo;
    }

    /** {@inheritDoc} */
    public void setAppliedPromo(final String appliedPromo) {
        this.appliedPromo = appliedPromo;
    }

    /** {@inheritDoc} */
    public long getCustomerOrderDeliveryId() {
        return customerOrderDeliveryId;
    }

    /** {@inheritDoc} */
    public void setCustomerOrderDeliveryId(final long customerOrderDeliveryId) {
        this.customerOrderDeliveryId = customerOrderDeliveryId;
    }

    /** {@inheritDoc} */
    public String getDeliveryNum() {
        return deliveryNum;
    }

    /** {@inheritDoc} */
    public void setDeliveryNum(final String deliveryNum) {
        this.deliveryNum = deliveryNum;
    }

    /** {@inheritDoc} */
    public String getRefNo() {
        return refNo;
    }

    /** {@inheritDoc} */
    public void setRefNo(final String refNo) {
        this.refNo = refNo;
    }

    /** {@inheritDoc} */
    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    /** {@inheritDoc} */
    public void setDeliveryStatus(final String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    /** {@inheritDoc} */
    public boolean isBlockExport() {
        return blockExport;
    }

    /** {@inheritDoc} */
    public void setBlockExport(final boolean blockExport) {
        this.blockExport = blockExport;
    }

    /** {@inheritDoc} */
    public Date getLastExportDate() {
        return lastExportDate;
    }

    /** {@inheritDoc} */
    public void setLastExportDate(final Date lastExportDate) {
        this.lastExportDate = lastExportDate;
    }

    /** {@inheritDoc} */
    public String getLastExportStatus() {
        return lastExportStatus;
    }

    /** {@inheritDoc} */
    public void setLastExportStatus(final String lastExportStatus) {
        this.lastExportStatus = lastExportStatus;
    }

    /** {@inheritDoc} */
    public String getLastExportDeliveryStatus() {
        return lastExportDeliveryStatus;
    }

    /** {@inheritDoc} */
    public void setLastExportDeliveryStatus(final String lastExportDeliveryStatus) {
        this.lastExportDeliveryStatus = lastExportDeliveryStatus;
    }

    /** {@inheritDoc} */
    public String getCarrierSlaName() {
        return carrierSlaName;
    }

    /** {@inheritDoc} */
    public void setCarrierSlaName(final String carrierSlaName) {
        this.carrierSlaName = carrierSlaName;
    }

    /** {@inheritDoc} */
    public String getCarrierName() {
        return carrierName;
    }

    /** {@inheritDoc} */
    public void setCarrierName(final String carrierName) {
        this.carrierName = carrierName;
    }

    /** {@inheritDoc} */
    public String getOrdernum() {
        return ordernum;
    }

    /** {@inheritDoc} */
    public void setOrdernum(final String ordernum) {
        this.ordernum = ordernum;
    }

    /** {@inheritDoc} */
    public String getDeliveryGroup() {
        return deliveryGroup;
    }

    /** {@inheritDoc} */
    public void setDeliveryGroup(final String deliveryGroup) {
        this.deliveryGroup = deliveryGroup;
    }

    /** {@inheritDoc} */
    public String getDeliveryRemarks() {
        return deliveryRemarks;
    }

    /** {@inheritDoc} */
    public void setDeliveryRemarks(final String deliveryRemarks) {
        this.deliveryRemarks = deliveryRemarks;
    }

    /** {@inheritDoc} */
    public Date getDeliveryEstimatedMin() {
        return deliveryEstimatedMin;
    }

    /** {@inheritDoc} */
    public void setDeliveryEstimatedMin(final Date deliveryEstimatedMin) {
        this.deliveryEstimatedMin = deliveryEstimatedMin;
    }

    /** {@inheritDoc} */
    public Date getDeliveryEstimatedMax() {
        return deliveryEstimatedMax;
    }

    /** {@inheritDoc} */
    public void setDeliveryEstimatedMax(final Date deliveryEstimatedMax) {
        this.deliveryEstimatedMax = deliveryEstimatedMax;
    }

    /** {@inheritDoc} */
    public Date getDeliveryGuaranteed() {
        return deliveryGuaranteed;
    }

    /** {@inheritDoc} */
    public void setDeliveryGuaranteed(final Date deliveryGuaranteed) {
        this.deliveryGuaranteed = deliveryGuaranteed;
    }

    /** {@inheritDoc} */
    public long getId() {
        return customerOrderDeliveryId;
    }

    /** {@inheritDoc} */
    public String getPgLabel() {
        return pgLabel;
    }

    /** {@inheritDoc} */
    public void setPgLabel(final String pgLabel) {
        this.pgLabel = pgLabel;
    }

    /** {@inheritDoc} */
    public boolean isSupportCaptureMore() {
        return supportCaptureMore;
    }

    /** {@inheritDoc} */
    public void setSupportCaptureMore(final boolean supportCaptureMore) {
        this.supportCaptureMore = supportCaptureMore;
    }

    /** {@inheritDoc} */
    public boolean isSupportCaptureLess() {
        return supportCaptureLess;
    }

    /** {@inheritDoc} */
    public void setSupportCaptureLess(final boolean supportCaptureLess) {
        this.supportCaptureLess = supportCaptureLess;
    }
}
